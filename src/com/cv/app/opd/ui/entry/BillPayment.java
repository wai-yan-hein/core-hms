/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.view.VPatientBillPayment;
import com.cv.app.opd.ui.common.BillPaymentSearchTableModel;
import com.cv.app.opd.ui.common.BillPaymentTableModel;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class BillPayment extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(BillPayment.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final BillPaymentTableModel tblBillPaymentTableModel = new BillPaymentTableModel();
    private final BillPaymentSearchTableModel tblBillPaymentSearchTableModel = new BillPaymentSearchTableModel();

    public BillPayment() {
        initComponents();
        initTable();
        initCombo();

        txtPayDate.setText(DateUtil.getTodayDateStr());
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
    }

    //FormAction
    @Override
    public void save() {
        try {
            if (tblBillPayment.getCellEditor() != null) {
                tblBillPayment.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        try {
            List<PatientBillPayment> listPBP = tblBillPaymentTableModel.getSavePBP();
            if (txtAdmissionNo.getText() != null) {
                if (!txtAdmissionNo.getText().trim().isEmpty()) {
                    String admissionNo = txtAdmissionNo.getText();
                    String ptType = "OPD";
                    if(!admissionNo.isEmpty()){
                        ptType = "ADMISSION";
                    }
                    for (PatientBillPayment pbp : listPBP) {
                        pbp.setAdmissionNo(admissionNo);
                        pbp.setPtType(admissionNo);
                        pbp.setPtType(ptType);
                    }
                }
            }
            if (!listPBP.isEmpty()) {
                dao.saveBatch(listPBP);
                newForm();
            }
        } catch (Exception ex) {
            log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    @Override
    public void newForm() {
        txtPayDate.setText(DateUtil.getTodayDateStr());
        txtRegNo.setText(null);
        txtPtName.setText(null);
        tblBillPaymentTableModel.setListPBP(new ArrayList());
        txtTtlAmt.setText(null);
        txtTtlBalance.setText(null);
        txtTtlPay.setText(null);
    }

    @Override
    public void history() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void deleteCopy() {

    }

    @Override
    public void print() {

        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "rptBillPayment";
        Map<String, Object> params = new HashMap();
        String compName = Util1.getPropValue("report.company.name");
        if (txtSRegNo.getText().isEmpty()) {
            params.put("prm_reg_no", "-");
        } else {
            params.put("prm_reg_no", txtSRegNo.getText());
        }
        params.put("prm_compName", compName);

        if (txtSPtName.getText().isEmpty()) {
            params.put("prm_patient_name", "-");
        } else {
            params.put("prm_patient_name", txtSPtName.getText());
        }

        if (cboBillTo.getSelectedItem() instanceof PaymentType) {
            PaymentType ptype = (PaymentType) cboBillTo.getSelectedItem();
            params.put("prm_btype_id", ptype.getPaymentTypeId());
        } else {
            params.put("prm_btype_id", 0);
        }

        if (txtFrom.getText().equals(txtTo.getText())) {
            params.put("data_date", txtFrom.getText());
        } else {
            params.put("data_date", "Between " + txtFrom.getText() + " and " + txtTo.getText());
        }
        params.put("prm_fDate", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_toDate", DateUtil.toDateStrMYSQL(txtTo.getText()));
        dao.close();
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
        dao.commit();

    }

    //KeyPropagate
    @Override
    public void keyEvent(KeyEvent e) {

    }

    //SelectionObserver
    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PatientSearch":
                Patient patient = (Patient) selectObj;
                if (patient != null) {
                    txtRegNo.setText(patient.getRegNo());
                    txtPtName.setText(patient.getPatientName());
                    try {
                        List<PatientBillPayment> listPBP = new ArrayList();
                        Double totalBalance = 0.0;
                        String appCurr = Util1.getPropValue("system.app.currency");
                        try ( //dao.open();
                                 ResultSet resultSet = dao.getPro("patient_bill_payment",
                                        patient.getRegNo(), DateUtil.toDateStrMYSQL(txtPayDate.getText()), appCurr,
                                        Global.machineId)) {
                            while (resultSet.next()) {
                                double bal = resultSet.getDouble("balance");
                                if (bal != 0) {
                                    PatientBillPayment pbp = new PatientBillPayment();

                                    pbp.setBillTypeDesp(resultSet.getString("payment_type_name"));
                                    pbp.setBillTypeId(resultSet.getInt("bill_type"));
                                    pbp.setCreatedBy(Global.loginUser.getUserId());
                                    pbp.setCurrency(resultSet.getString("currency"));
                                    pbp.setPayDate(DateUtil.toDate(txtPayDate.getText()));
                                    pbp.setRegNo(resultSet.getString("reg_no"));
                                    pbp.setAmount(resultSet.getDouble("balance"));
                                    pbp.setBalance(resultSet.getDouble("balance"));
                                    pbp.setDelete(Boolean.FALSE);
                                    
                                    totalBalance += pbp.getAmount();
                                    listPBP.add(pbp);
                                }
                            }
                        }

                        tblBillPaymentTableModel.setListPBP(listPBP);
                        txtTtlAmt.setValue(totalBalance);
                        txtTtlBalance.setValue(totalBalance);
                    } catch (Exception ex) {
                        log.error("PatientSearch : Patient_bill_Payment :" + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    } finally {
                        dao.close();
                    }
                } else {
                    txtRegNo.setText(null);
                    txtPtName.setText(null);
                }

                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        /*if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
         if (!focusCtrlName.equals("-")) {
         if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtPatientNo")) {
         txtDoctorNo.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDoctorNo")) {
         txtDonorName.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDonorName")) {
         tblService.requestFocus();
         }else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusId")) {
         txtDonorName.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDoctorNo")) {
         txtPatientNo.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblService")) {
         int selRow = tblService.getSelectedRow();

         if (selRow == -1 || selRow == 0) {
         txtDonorName.requestFocus();
         }
         }
         }
         }*/
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void initForFocus() {
        /*txtPatientNo.addKeyListener(this);
         txtDoctorNo.addKeyListener(this);
         txtDonorName.addKeyListener(this);
         tblService.addKeyListener(this);*/
    }

    private void initTable() {
        tblBillPaymentSearch.getColumnModel().getColumn(0).setPreferredWidth(30);//Reg No
        tblBillPaymentSearch.getColumnModel().getColumn(1).setPreferredWidth(30);//Date
        tblBillPaymentSearch.getColumnModel().getColumn(2).setPreferredWidth(250);//Patient Name
        tblBillPaymentSearch.getColumnModel().getColumn(3).setPreferredWidth(150);//Bill Type
        tblBillPaymentSearch.getColumnModel().getColumn(4).setPreferredWidth(200);//Remark
        tblBillPaymentSearch.getColumnModel().getColumn(5).setPreferredWidth(50);//Amount
        
        tblBillPayment.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        //tblBillPayment.getColumnModel().getColumn(0).setPreferredWidth(50);//Bill Type
        //tblBillPayment.getColumnModel().getColumn(1).setPreferredWidth(50);//
        tblBillPayment.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                if (column >= 0) {
                    switch (column) {
                        case 2: //Payment
                            List<PatientBillPayment> listPBP = tblBillPaymentTableModel.getListPBP();
                            Double ttlBalance = 0.0;
                            Double ttlPay = 0.0;

                            for (PatientBillPayment pbp : listPBP) {
                                ttlBalance += NumberUtil.NZero(pbp.getBalance());
                                ttlPay += NumberUtil.NZero(pbp.getPayAmt());
                            }

                            txtTtlBalance.setValue(ttlBalance);
                            txtTtlPay.setValue(ttlPay);

                            break;
                    }
                }
            }
        });
    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboBillTo, dao.findAll("PaymentType"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void search() {
        String strFilter = "o.deleted = false ";

        if (txtFrom.getText() != null && txtTo.getText() != null) {
            if (strFilter.isEmpty()) {
                strFilter = "o.payDate between '" + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                        + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'";
            } else {
                strFilter = strFilter + " and o.payDate between '" + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                        + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'";
            }
        }

        if (cboBillTo.getSelectedItem() instanceof PaymentType) {
            PaymentType paymentType = (PaymentType) cboBillTo.getSelectedItem();
            if (strFilter.isEmpty()) {
                strFilter = "o.billTypeId = " + paymentType.getClass().toString();
            } else {
                strFilter = strFilter + " and o.billTypeId = " + paymentType.getClass().toString();
            }
        }

        if (txtSRegNo.getText() != null && !txtSRegNo.getText().trim().isEmpty()) {
            if (strFilter.isEmpty()) {
                strFilter = "o.regNo = '" + txtSRegNo.getText().trim() + "'";
            } else {
                strFilter = strFilter + " and o.regNo = '" + txtSRegNo.getText().trim() + "'";
            }
        } else if (txtSPtName.getText() != null && !txtSPtName.getText().trim().isEmpty()) {
            if (strFilter.isEmpty()) {
                strFilter = "o.patientName like '%" + txtSPtName.getText().trim() + "%'";
            } else {
                strFilter = strFilter + " and o.patientName like '%" + txtSPtName.getText().trim() + "%'";
            }
        }

        if (strFilter.isEmpty()) {

        } else {
            strFilter = "select o from VPatientBillPayment o where " + strFilter;
        }

        try {
            List<VPatientBillPayment> listVPBP = dao.findAllHSQL(strFilter);
            if (listVPBP != null) {
                Double ttlPaid = 0.0;
                for (VPatientBillPayment vpbp : listVPBP) {
                    ttlPaid += vpbp.getPayAmt();
                }

                txtSTotalPaid.setValue(ttlPaid);
                tblBillPaymentSearchTableModel.setListVPBP(listVPBP);
            } else {
                txtSTotalPaid.setValue(0.0);
                tblBillPaymentSearchTableModel.setListVPBP(new ArrayList());
            }
        } catch (Exception ex) {
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void deleteBill() {
        int row = tblBillPaymentSearch.convertRowIndexToModel(tblBillPaymentSearch.getSelectedRow());
        if (row >= 0) {
            int y = JOptionPane.showConfirmDialog(this, "Are you sure to delete?");
            if (y == JOptionPane.YES_OPTION) {
                int billId = tblBillPaymentSearchTableModel.getBillId(row);
                String sql = "update opd_patient_bill_payment set deleted = 1 where id=" + billId + "";
                dao.execSql(sql);
                tblBillPaymentSearchTableModel.remove(row);
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

        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtPayDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPtName = new javax.swing.JTextField();
        butSave = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBillPayment = new javax.swing.JTable();
        txtTtlBalance = new javax.swing.JFormattedTextField();
        txtTtlPay = new javax.swing.JFormattedTextField();
        txtTtlAmt = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtAdmissionNo = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        cboBillTo = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        txtSRegNo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtSPtName = new javax.swing.JTextField();
        butSearch = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBillPaymentSearch = new javax.swing.JTable();
        txtSTotalPaid = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        btnPrint = new javax.swing.JButton();
        butSearch1 = new javax.swing.JButton();

        jButton1.setText("jButton1");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Pay Date ");

        txtPayDate.setFont(Global.textFont);
        txtPayDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPayDateMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Reg No. ");

        txtRegNo.setFont(Global.textFont);
        txtRegNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtRegNoMouseClicked(evt);
            }
        });
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Pt. Name ");

        txtPtName.setEditable(false);
        txtPtName.setFont(Global.textFont);
        txtPtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPtNameMouseClicked(evt);
            }
        });

        butSave.setFont(Global.lableFont);
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butClear.setFont(Global.lableFont);
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        tblBillPayment.setFont(Global.textFont);
        tblBillPayment.setModel(tblBillPaymentTableModel);
        tblBillPayment.setRowHeight(23);
        jScrollPane1.setViewportView(tblBillPayment);

        txtTtlBalance.setEditable(false);
        txtTtlBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlBalance.setFont(Global.textFont);
        txtTtlBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTtlBalanceActionPerformed(evt);
            }
        });

        txtTtlPay.setEditable(false);
        txtTtlPay.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlPay.setFont(Global.textFont);

        txtTtlAmt.setEditable(false);
        txtTtlAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlAmt.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Total :");

        jLabel5.setText(" ");

        lblStatus.setText("New");

        jLabel12.setText("Admission No");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPtName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlAmt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlPay)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlBalance)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPayDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSave)
                    .addComponent(butClear)
                    .addComponent(jLabel12)
                    .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(lblStatus)))
        );

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("From ");

        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("To ");

        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Bill To ");

        cboBillTo.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Reg. No");

        txtSRegNo.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Pt. Name ");

        txtSPtName.setFont(Global.textFont);

        butSearch.setFont(Global.lableFont);
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        tblBillPaymentSearch.setFont(Global.textFont);
        tblBillPaymentSearch.setModel(tblBillPaymentSearchTableModel);
        tblBillPaymentSearch.setRowHeight(23);
        tblBillPaymentSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblBillPaymentSearchKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblBillPaymentSearch);

        txtSTotalPaid.setEditable(false);
        txtSTotalPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSTotalPaid.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Total : ");

        btnPrint.setFont(Global.lableFont);
        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        butSearch1.setFont(Global.lableFont);
        butSearch1.setText("Delete");
        butSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearch1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBillTo, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addGap(2, 2, 2)
                        .addComponent(txtSPtName, javax.swing.GroupLayout.PREFERRED_SIZE, 46, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrint))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFrom, txtTo});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(cboBillTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtSRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtSPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSearch)
                    .addComponent(btnPrint)
                    .addComponent(butSearch1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTtlBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTtlBalanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTtlBalanceActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void txtPayDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPayDateMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtPayDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtPayDateMouseClicked

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        // TODO add your handling code here:
        if (txtRegNo.getText().isEmpty() || txtRegNo.getText() == null) {
            txtPtName.setText(null);
            txtRegNo.requestFocus();
        } else {
            getPatient();
        }
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void getPatient() {
        if (txtRegNo.getText() != null && !txtRegNo.getText().isEmpty()) {

            try {
                dao.open();
                Patient ptt = (Patient) dao.find(Patient.class, txtRegNo.getText());
                dao.close();

                if (ptt == null) {
                    txtRegNo.setText(null);
                    txtPtName.setText(null);
                    //currSaleVou.setCustomerId(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid Patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                    txtRegNo.requestFocus();
                } else {
                    selected("PatientSearch", ptt);
                    //txtDrCode.requestFocus();
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }

        }
    }


    private void txtRegNoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtRegNoMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtRegNoMouseClicked

    private void txtPtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPtNameMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtPtNameMouseClicked

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        newForm();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFrom.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFromMouseClicked

    private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTo.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtToMouseClicked

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        print();

    }//GEN-LAST:event_btnPrintActionPerformed

    private void tblBillPaymentSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblBillPaymentSearchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblBillPaymentSearchKeyReleased

    private void butSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearch1ActionPerformed
        // TODO add your handling code here:
        deleteBill();
    }//GEN-LAST:event_butSearch1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSearch1;
    private javax.swing.JComboBox cboBillTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblBillPayment;
    private javax.swing.JTable tblBillPaymentSearch;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtPayDate;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextField txtSPtName;
    private javax.swing.JTextField txtSRegNo;
    private javax.swing.JFormattedTextField txtSTotalPaid;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTtlAmt;
    private javax.swing.JFormattedTextField txtTtlBalance;
    private javax.swing.JFormattedTextField txtTtlPay;
    // End of variables declaration//GEN-END:variables
}
