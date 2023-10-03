/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.checkbalance;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import static com.cv.app.pharmacy.database.entity.GenExpense_.vouNo;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class CheckPatientBalance extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(CheckPatientBalance.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final CSVDataTableModel csvModel = new CSVDataTableModel();
    private final ErrorDataTableModel errModel = new ErrorDataTableModel();

    /**
     * Creates new form CheckPatientBalance
     */
    public CheckPatientBalance() {
        initComponents();
        txtDate.setText(DateUtil.getTodayDateStr());
    }

    private void processCSV(File file) {
        if (file != null) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                try ( CSVReader csvReader = new CSVReader(reader)) {
                    String[] nextRecord;
                    int ttlRec = 0;
                    double ttlAmt = 0;
                    double ttlAdjAmt = 0;

                    log.info("processCSV Start");
                    while ((nextRecord = csvReader.readNext()) != null) {
                        String regNo = nextRecord[0];
                        String ptName = nextRecord[1];
                        Double rptBalance = Double.parseDouble(nextRecord[2].replace(",", ""));

                        ttlRec++;
                        log.info("processCSV : " + regNo + " Name : " + ptName
                                + " Balance : " + rptBalance.toString());

                        PatientBalance pb = new PatientBalance();
                        pb.setRegNo(regNo);
                        pb.setPtName(ptName);
                        pb.setRptBalance(rptBalance);
                        ttlAmt += rptBalance;
                        csvModel.addData(pb);

                        Double checkBalance = getPatientBill(pb);
                        if (!checkBalance.equals(rptBalance)) {
                            pb.setChkBalance(checkBalance);
                            pb.setAdjAmount(rptBalance - checkBalance);
                            ttlAdjAmt += pb.getAdjAmount();
                            errModel.addData(pb);
                        }
                    }
                    log.info("processCSV End: " + ttlRec);
                    txtCSVTtlRec.setValue(ttlRec);
                    txtTtlAmt.setValue(ttlAmt);
                    txtTtlAdjRec.setValue(errModel.getRowCount());
                    txtAdjAmount.setValue(ttlAdjAmt);
                }
            } catch (IOException | NumberFormatException ex) {
                log.error("processCSV : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    public Double getPatientBill(PatientBalance pb) {
        try {
            String regNo = pb.getRegNo();
            Double totalBalance = 0.0;
            String currency = Util1.getPropValue("system.app.currency");

            try ( //dao.open();
                     ResultSet resultSet = dao.getPro("patient_bill_payment",
                            regNo, DateUtil.toDateStrMYSQL(txtDate.getText()),
                            currency, Global.machineId)) {
                List<SubBalance> listSB = new ArrayList();
                while (resultSet.next()) {
                    double bal = resultSet.getDouble("balance");
                    if (bal != 0) {
                        totalBalance += bal;
                    }
                    SubBalance sb = new SubBalance();
                    sb.setBalance(bal);
                    sb.setBillName(resultSet.getString("payment_type_name"));
                    sb.setBillType(resultSet.getInt("bill_type"));
                    sb.setCurrency(currency);
                    listSB.add(sb);
                }

                pb.setListSB(listSB);
            }

            return totalBalance;
        } catch (Exception ex) {
            log.error("PatientSearch : Patient_bill_Payment :" + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        return 0.0;
    }

    private void fixBalance() {
        try {
            List<PatientBalance> list = errModel.getListDetail();

            for (PatientBalance pb : list) {
                List<SubBalance> listSB = pb.getListSB();
                if (listSB != null) {
                    if (!listSB.isEmpty()) {
                        double fixBalance = NumberUtil.NZero(pb.getAdjAmount());
                        if (fixBalance > 0) {
                            for (SubBalance sb : listSB) {
                                if (fixBalance > 0) {
                                    PatientBillPayment pbp = new PatientBillPayment();
                                    pbp.setAdmissionNo(null);
                                    pbp.setPtType("OPD");
                                    pbp.setDelete(Boolean.FALSE);
                                    pbp.setBillTypeDesp(sb.getBillName());
                                    pbp.setBillTypeId(sb.getBillType());
                                    pbp.setCreatedBy(Global.loginUser.getUserId());
                                    pbp.setCreatedDate(new Date());
                                    pbp.setCurrency(sb.getCurrency());
                                    if (fixBalance >= sb.getBalance()) {
                                        pbp.setPayAmt(sb.getBalance());
                                        fixBalance -= sb.getBalance();
                                    } else {
                                        pbp.setPayAmt(fixBalance);
                                        fixBalance = 0;
                                    }
                                    pbp.setPayDate(new Date());
                                    pbp.setRegNo(pb.getRegNo());
                                    pbp.setRemark("Fixed balance with check patient balance");
                                    pbp.setIntgUpdStatus("ACK");
                                    pbp.setDiscount(0d);
                                    dao.save(pbp);
                                    log.info("fixBalance : fixed : " + pbp.getRegNo() + " amt : " + pbp.getPayAmt());
                                }
                            }
                        }
                    }
                }
            }
            
            butFixAmt.setEnabled(false);
        } catch (Exception ex) {
            log.error("fixBalance : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtFileName = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCSVTtlRec = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTtlAmt = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTtlAdjRec = new javax.swing.JFormattedTextField();
        txtAdjAmount = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        butFixAmt = new javax.swing.JButton();

        jLabel1.setText("CSV File : ");

        txtFileName.setEditable(false);

        jButton1.setText("...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblInfo.setText(" ");

        jTable1.setFont(Global.textFont);
        jTable1.setModel(csvModel);
        jTable1.setRowHeight(23);
        jScrollPane1.setViewportView(jTable1);

        jTable2.setFont(Global.textFont);
        jTable2.setModel(errModel);
        jTable2.setRowHeight(23);
        jScrollPane2.setViewportView(jTable2);

        jLabel3.setText("Date : ");

        jLabel2.setText("Total Record : ");

        txtCSVTtlRec.setEditable(false);
        txtCSVTtlRec.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText("Total Amt :");

        txtTtlAmt.setEditable(false);
        txtTtlAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText("Total Adj Record : ");

        txtTtlAdjRec.setEditable(false);
        txtTtlAdjRec.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtAdjAmount.setEditable(false);
        txtAdjAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setText("Total Adj : ");

        butFixAmt.setText("Fix Amt");
        butFixAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFixAmtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCSVTtlRec, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlAmt))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlAdjRec, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdjAmount))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFixAmt)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(lblInfo)
                    .addComponent(butFixAmt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtCSVTtlRec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtTtlAdjRec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAdjAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addGap(7, 7, 7))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtFileName.setText(selectedFile.getName());
            processCSV(selectedFile);
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void butFixAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFixAmtActionPerformed
        fixBalance();
    }//GEN-LAST:event_butFixAmtActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butFixAmt;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JFormattedTextField txtAdjAmount;
    private javax.swing.JFormattedTextField txtCSVTtlRec;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtFileName;
    private javax.swing.JFormattedTextField txtTtlAdjRec;
    private javax.swing.JFormattedTextField txtTtlAmt;
    // End of variables declaration//GEN-END:variables
}
