/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.RegNo;
import com.cv.app.opd.database.entity.BillOpeningHis;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.common.OpenBillPatientTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.awt.Cursor;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author cv-svr
 */
public class BillProcess extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(BillProcess.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private Patient obPatient = null;
    private final OpenBillPatientTableModel patientMode = new OpenBillPatientTableModel();
    private BillOpeningHis currRecord = null;

    /**
     * Creates new form BillProcess
     */
    public BillProcess() {
        initComponents();
        initTable();
        getBillOpen();
        butOpen.setEnabled(false);
        butCloseBill.setEnabled(false);
        butBillMarge.setEnabled(false);
        butVouAdd.setEnabled(false);
    }

    private void openBill() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            Patient pt = (Patient) dao.find(Patient.class, txtOBRegNo.getText().trim());
            if (pt != null) {
                if (pt.getOtId() == null) {
                    RegNo regNo = new RegNo(dao, "OT-ID");
                    pt.setOtId(regNo.getRegNo());
                    dao.save(pt);
                    regNo.updateRegNo();

                    BillOpeningHis boh = new BillOpeningHis();
                    boh.setAdmNo(pt.getAdmissionNo());
                    boh.setBillId(pt.getOtId());
                    boh.setBillOPDate(new Date());
                    boh.setOpenBy(Global.loginUser.getUserId());
                    boh.setRegNo(pt.getRegNo());
                    boh.setStatus(true);
                    dao.save(boh);
                    butOpen.setEnabled(false);
                    txtOBBillId.setText(pt.getOtId());
                    getBillOpen();
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Patient is already opened bill.",
                            "Bill Id", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registration number.",
                        "Invalid Patient", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("openBill : " + txtOBRegNo.getText().trim() + " : " + ex.getMessage());
        } finally {
            dao.close();
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void getBillOpen() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            ResultSet rs = dao.execSQL("select a.bill_id, a.reg_no, a.adm_no, "
                    + "a.bill_op_date, b.patient_name, a.op_cl_status \n"
                    + "from (select * from bill_opening_his where op_cl_status = true) a\n"
                    + "join patient_detail b on a.reg_no = b.reg_no\n"
                    + "order by b.patient_name");
            if (rs != null) {
                List<BillOpeningHis> list = new ArrayList();
                while (rs.next()) {
                    BillOpeningHis boh = new BillOpeningHis();
                    boh.setAdmNo(rs.getString("adm_no"));
                    boh.setBillId(rs.getString("bill_id"));
                    boh.setBillOPDate(rs.getDate("bill_op_date"));
                    boh.setPtName(rs.getString("patient_name"));
                    boh.setRegNo(rs.getString("reg_no"));
                    boh.setStatus(rs.getBoolean("op_cl_status"));
                    list.add(boh);
                }

                patientMode.setList(list);
            }
        } catch (Exception ex) {
            log.error("getBillOpen : " + ex.getMessage());
        } finally {
            dao.close();
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void initTable() {
        tblPatientList.getColumnModel().getColumn(0).setPreferredWidth(50); //Date
        tblPatientList.getColumnModel().getColumn(1).setPreferredWidth(30); //Bill Id
        tblPatientList.getColumnModel().getColumn(2).setPreferredWidth(30); //Reg No.
        tblPatientList.getColumnModel().getColumn(3).setPreferredWidth(30); //Adm No.
        tblPatientList.getColumnModel().getColumn(4).setPreferredWidth(170); //Name

        tblPatientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPatientList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblPatientList.getSelectedRow() >= 0) {
                    int selectRow = tblPatientList.convertRowIndexToModel(tblPatientList.getSelectedRow());
                    if (selectRow >= 0) {
                        BillOpeningHis record = patientMode.getData(selectRow);
                        selectRecord(record);
                    }
                }
            }
        });
    }

    private void selectRecord(BillOpeningHis record) {
        currRecord = record;
        txtBillId.setText(record.getBillId());
        txtRegNo.setText(record.getRegNo());
        txtAdmNo.setText(record.getAdmNo());
        txtName.setText(record.getPtName());
        boolean status = record.getStatus();
        butVouAdd.setEnabled(status);
        butBillMarge.setEnabled(status);
        butCloseBill.setEnabled(status);
        txtBillId.setEnabled(false);
        txtAdmNo.setEnabled(false);
    }

    private void clearOB() {
        txtOBRegNo.setText(null);
        txtOBName.setText(null);
        txtOBBillId.setText(null);
        butOBClear.setEnabled(true);
        obPatient = null;
    }

    private void getOBPatient() {
        try {
            obPatient = (Patient) dao.find(Patient.class, txtOBRegNo.getText().trim());
            if (obPatient != null) {
                txtOBBillId.setText(obPatient.getOtId());
                txtOBName.setText(obPatient.getPatientName());
                txtOBRegNo.setText(obPatient.getRegNo());
                if (obPatient.getOtId() == null) {
                    butOpen.setEnabled(true);
                } else {
                    butOpen.setEnabled(false);
                }
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registration number.",
                        "Invalid Patient", JOptionPane.ERROR_MESSAGE);
                obPatient = null;
            }
        } catch (Exception ex) {
            log.error("getOBPatient : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void closeBill() {
        if (currRecord.getBillId() != null) {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to close bill?",
                    "Bill Close", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                try {
                    BillOpeningHis record = (BillOpeningHis) dao.find(BillOpeningHis.class,
                            currRecord.getBillId());
                    record.setBillCLDate(new Date());
                    record.setCloseBy(Global.loginUser.getUserId());
                    record.setStatus(false);
                    
                    Patient pt = (Patient) dao.find(Patient.class, currRecord.getRegNo());
                    pt.setOtId(null);
                    
                    dao.save(record);
                    dao.save(pt);
                    
                    getBillOpen();
                } catch (Exception ex) {
                    log.error("closeBill : Bill Id : " + currRecord.getBillId() + " : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
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

        jPanel1 = new javax.swing.JPanel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPatientList = new javax.swing.JTable();
        butRefresh = new javax.swing.JButton();
        txtRegNo = new javax.swing.JTextField();
        txtAdmNo = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtBillId = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtTtlVouBal = new javax.swing.JFormattedTextField();
        txtTtlVouDisc = new javax.swing.JFormattedTextField();
        txtTtlVouTax = new javax.swing.JFormattedTextField();
        txtVouTotal = new javax.swing.JFormattedTextField();
        butCloseBill = new javax.swing.JButton();
        butBillMarge = new javax.swing.JButton();
        butVouAdd = new javax.swing.JButton();
        txtTtlPaid = new javax.swing.JFormattedTextField();
        butClearBill = new javax.swing.JButton();
        butPrint = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtOBRegNo = new javax.swing.JTextField();
        txtOBName = new javax.swing.JTextField();
        butOBClear = new javax.swing.JButton();
        butOpen = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtOBBillId = new javax.swing.JTextField();

        txtFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        tblPatientList.setFont(Global.textFont);
        tblPatientList.setModel(patientMode);
        tblPatientList.setRowHeight(23);
        jScrollPane1.setViewportView(tblPatientList);

        butRefresh.setText("Refresh");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butRefresh)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtRegNo.setEditable(false);
        txtRegNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Reg No."));

        txtAdmNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Adm No."));

        txtName.setEditable(false);
        txtName.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));

        txtBillId.setBorder(javax.swing.BorderFactory.createTitledBorder("Bill Id"));

        jTable1.setFont(Global.textFont);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setRowHeight(23);
        jScrollPane2.setViewportView(jTable1);

        txtTtlVouBal.setEditable(false);
        txtTtlVouBal.setBorder(javax.swing.BorderFactory.createTitledBorder("Ttl Vou Balance"));

        txtTtlVouDisc.setEditable(false);
        txtTtlVouDisc.setBorder(javax.swing.BorderFactory.createTitledBorder("Ttl Vou Discount"));

        txtTtlVouTax.setEditable(false);
        txtTtlVouTax.setBorder(javax.swing.BorderFactory.createTitledBorder("Ttl Vou Tax"));

        txtVouTotal.setEditable(false);
        txtVouTotal.setBorder(javax.swing.BorderFactory.createTitledBorder("Vou Total"));

        butCloseBill.setText("Close Bill");
        butCloseBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCloseBillActionPerformed(evt);
            }
        });

        butBillMarge.setText("Bill Marge");

        butVouAdd.setText("Vou Add");

        txtTtlPaid.setEditable(false);
        txtTtlPaid.setBorder(javax.swing.BorderFactory.createTitledBorder("Ttl Paid"));

        butClearBill.setText("Clear Bill");

        butPrint.setText("Print");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(butCloseBill)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butBillMarge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butVouAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butClearBill)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVouTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlVouTax, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlVouDisc, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlPaid, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlVouBal, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtTtlVouBal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(butCloseBill)
                .addComponent(butBillMarge)
                .addComponent(butVouAdd)
                .addComponent(butClearBill)
                .addComponent(butPrint))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtTtlVouDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtTtlVouTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(txtTtlPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Open Bill"));

        txtOBRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOBRegNoActionPerformed(evt);
            }
        });

        txtOBName.setEditable(false);

        butOBClear.setText("Clear");
        butOBClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOBClearActionPerformed(evt);
            }
        });

        butOpen.setText("Open");
        butOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOpenActionPerformed(evt);
            }
        });

        jLabel1.setText("Bill ID");

        txtOBBillId.setEditable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtOBRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOBName, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOBBillId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butOpen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butOBClear)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOBRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOBName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butOBClear)
                        .addComponent(butOpen))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtOBBillId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 125, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtBillId, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtAdmNo, txtRegNo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBillId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtAdmNo, txtBillId, txtName, txtRegNo});

    }// </editor-fold>//GEN-END:initComponents

    private void butOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOpenActionPerformed
        openBill();
    }//GEN-LAST:event_butOpenActionPerformed

    private void butOBClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOBClearActionPerformed
        clearOB();
    }//GEN-LAST:event_butOBClearActionPerformed

    private void txtOBRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOBRegNoActionPerformed
        getOBPatient();
    }//GEN-LAST:event_txtOBRegNoActionPerformed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        getBillOpen();
    }//GEN-LAST:event_butRefreshActionPerformed

    private void butCloseBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCloseBillActionPerformed
        closeBill();
    }//GEN-LAST:event_butCloseBillActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butBillMarge;
    private javax.swing.JButton butClearBill;
    private javax.swing.JButton butCloseBill;
    private javax.swing.JButton butOBClear;
    private javax.swing.JButton butOpen;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butRefresh;
    private javax.swing.JButton butVouAdd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblPatientList;
    private javax.swing.JTextField txtAdmNo;
    private javax.swing.JTextField txtBillId;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOBBillId;
    private javax.swing.JTextField txtOBName;
    private javax.swing.JTextField txtOBRegNo;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JFormattedTextField txtTtlPaid;
    private javax.swing.JFormattedTextField txtTtlVouBal;
    private javax.swing.JFormattedTextField txtTtlVouDisc;
    private javax.swing.JFormattedTextField txtTtlVouTax;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables
}
