/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.CompoundKeyPatientOp;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientOpening;
import com.cv.app.opd.ui.common.PatientOpTableModel;
import com.cv.app.opd.ui.common.PatientTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author Eitar
 */
public class PatientOpSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(PatientOpSetup.class.getName());
    private List<Patient> listPatient;
    private final AbstractDataAccess dao = Global.dao;
    private Patient selPatient;
    //private PatientOpTableModel tblEntryModel = new PatientOpTableModel(true);
    private PatientOpTableModel tblLastOpModel = new PatientOpTableModel();
    private final PatientTableModel tblPatientTableModel = new PatientTableModel();
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form TraderOpSetup
     */
    public PatientOpSetup() {
        initComponents();
        //listPatient = dao.findAllHSQL("select t from Patient t");
        initTblTrader();
        initCombo();
        initTblLastOp();
        actionMapping();
        sorter = new TableRowSorter(tblPatient.getModel());
        tblPatient.setRowSorter(sorter);
    }

    private void initTblTrader() {
        //Adjust table column width
        TableColumn column = null;
        column = tblPatient.getColumnModel().getColumn(0);
        column.setPreferredWidth(60);

        column = tblPatient.getColumnModel().getColumn(1);
        column.setPreferredWidth(150);

        column = tblPatient.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);

        //column = tblPatient.getColumnModel().getColumn(3);
        //column.setPreferredWidth(20);
        //Define table selection model to single row selection.
        tblPatient.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblPatient.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = tblPatient.getSelectedRow();
                //log.info("select : " + e.getValueIsAdjusting());
                if (row != -1) {
                    if (e.getValueIsAdjusting()) {
                        selPatient = listPatient.get(tblPatient.convertRowIndexToModel(row));
                        //tblEntryModel.setPatient(selPatient);
                        txtCusCode.setText(selPatient.getRegNo());
                        txtCusName.setText(selPatient.getPatientName());
                        getSelectedPatientData();
                    }
                }
            }
        }
        );
    }

    private void getSelectedPatientData() {
        try {
            String strSQL = "select o from PatientOpening o where o.key.patient.regNo = '"
                    + selPatient.getRegNo() + "'";
            List<PatientOpening> prvOP = dao.findAllHSQL(strSQL);
            tblLastOpModel.setListOp(prvOP);
        } catch (Exception ex) {
            log.error("initTblTrader : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
            BindingUtil.BindCombo(cboBillType, dao.findAll("PaymentType"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTblLastOp() {
        tblLastOp.setModel(tblLastOpModel);

        //Adjust table column width
        TableColumn column = null;
        column = tblLastOp.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);

        column = tblLastOp.getColumnModel().getColumn(1);
        column.setPreferredWidth(50);

        column = tblLastOp.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);
    }

    private void clear() {
        txtCusCode.setText(null);
        txtCusName.setText(null);
        txtOpDate.setText(null);
        txtOpAmt.setText(null);
        selPatient = null;
        //tblEntryModel.clear();
        //tblEntryModel.addEmptyRow();
        tblLastOpModel.clear();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (selPatient == null) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "No patient selected.",
                    "No Patient", JOptionPane.ERROR_MESSAGE);
        } else if (Util1.nullToBlankStr(txtOpDate.getText()).isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Opening date cannot be blank.",
                    "Opening Date", JOptionPane.ERROR_MESSAGE);
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                PatientOpening po = new PatientOpening();
                CompoundKeyPatientOp ckpo = new CompoundKeyPatientOp();
                ckpo.setPatient(selPatient);
                PaymentType pyt = (PaymentType) cboBillType.getSelectedItem();
                ckpo.setBillType(pyt);
                Currency curr = (Currency) cboCurrency.getSelectedItem();
                ckpo.setCurrency(curr);
                ckpo.setPatientType(cboPtType.getSelectedItem().toString());
                ckpo.setOpDate(DateUtil.toDate(txtOpDate.getText()));
                po.setKey(ckpo);
                po.setAmount(NumberUtil.NZero(txtOpAmt.getText()));
                po.setAdmissionNo(txtAdmissionNo.getText());
                dao.save(po);

                getSelectedPatientData();
                txtOpDate.setText(null);
                txtOpAmt.setText(null);
                txtAdmissionNo.setText(null);
                //clear();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void actionMapping() {
        //F8 event on tblEntry
        //KeyStroke delKey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.META_MASK);
        //KeyStroke delKey = KeyStroke.getKeyStroke("BACK_SPACE");
        KeyStroke delKey = KeyStroke.getKeyStroke("F4");
        //tblEntry.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        //tblEntry.getInputMap().put(delKey, "F8-Action");
        //tblEntry.getActionMap().put("F8-Action", actionItemDelete);

        //F8 event on tblLastOp
        //tblLastOp.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblLastOp.getInputMap().put(delKey, "F8-Action");
        tblLastOp.getActionMap().put("F8-Action", actionItemDeleteLastOp);
    }

    private Action actionItemDeleteLastOp = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblLastOp.getSelectedRow() >= 0) {
                tblLastOpModel.delete(tblLastOp.convertRowIndexToModel(tblLastOp.getSelectedRow()));
            }
        }
    };

    private void search() {
        String strFilter = "";

        if (!txtRegNo.getText().trim().isEmpty()) {
            if (strFilter.isEmpty()) {
                strFilter = "o.regNo = '" + txtRegNo.getText().trim() + "'";
            } else {
                strFilter = strFilter + " and o.regNo = '" + txtRegNo.getText().trim() + "'";
            }
        }

        if (!txtFilter.getText().trim().isEmpty()) {
            if (strFilter.isEmpty()) {
                strFilter = "o.patientName like '%" + txtFilter.getText().trim() + "%'";
            } else {
                strFilter = strFilter + " and o.patientName like '%" + txtFilter.getText().trim() + "%'";
            }
        }

        if (strFilter.isEmpty()) {
            //strFilter = "select o from Patient o";
            JOptionPane.showMessageDialog(Util1.getParent(), "You need to enter at least one filter.",
                    "No Filter", JOptionPane.ERROR_MESSAGE);
        } else {
            strFilter = "select o from Patient o where " + strFilter;
            try {
                listPatient = dao.findAllHSQL(strFilter);
                tblPatientTableModel.setListPatient(listPatient);
            } catch (Exception ex) {
                log.error("search : " + ex.getMessage());
            } finally {
                dao.close();
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

        txtFilter = new javax.swing.JTextField();
        butFilter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPatient = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtCusName = new javax.swing.JTextField();
        txtCusCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLastOp = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtOpDate = new javax.swing.JFormattedTextField();
        cboCurrency = new javax.swing.JComboBox<>();
        cboBillType = new javax.swing.JComboBox<>();
        cboPtType = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txtOpAmt = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAdmissionNo = new javax.swing.JTextField();
        txtRegNo = new javax.swing.JTextField();
        btnToAcc = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        butFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butFilter.setText("...");
        butFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFilterActionPerformed(evt);
            }
        });

        tblPatient.setFont(Global.textFont);
        tblPatient.setModel(tblPatientTableModel);
        tblPatient.setRowHeight(23);
        tblPatient.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblPatient);

        txtCusName.setEditable(false);
        txtCusName.setFont(Global.textFont);

        txtCusCode.setEditable(false);
        txtCusCode.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Code");

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Last Opening"));

        tblLastOp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tblLastOp.setFont(Global.textFont);
        tblLastOp.setModel(new javax.swing.table.DefaultTableModel(
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
        tblLastOp.setRowHeight(23);
        tblLastOp.setShowVerticalLines(false);
        jScrollPane2.setViewportView(tblLastOp);

        jLabel3.setText("OP Date : ");

        jLabel5.setText("Currency :");

        jLabel6.setText("Bill Type : ");

        jLabel7.setText("Pt Type : ");

        cboPtType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OPD", "ADMISSION" }));

        jLabel8.setText("OP Amount : ");

        jLabel4.setText("Admission No : ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmissionNo))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboBillType, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtOpAmt))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboPtType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboCurrency, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtOpDate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboBillType, cboCurrency, cboPtType, txtOpAmt, txtOpDate});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtOpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboBillType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboPtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtOpAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtRegNo.setFont(Global.textFont);

        btnToAcc.setText("To Acc");
        btnToAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToAccActionPerformed(evt);
            }
        });

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
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
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnToAcc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(butFilter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(butClear)
                                .addComponent(butSave))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnToAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
      /*if (txtFilter.getText().isEmpty()) {
          sorter.setRowFilter(null);
      } else {
          sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
      }*/
  }//GEN-LAST:event_txtFilterKeyReleased

    private void btnToAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToAccActionPerformed
        // TODO add your handling code here:
        /*
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            List<VPatientOpToAcc> listPatientOp= dao.findAllHSQL("select o from VPatientOpToAcc o");
            if (listPatientOp.size() > 0) {
                for (int i = 0; i < listPatientOp.size() - 1; i++) {
                    if (Global.mqConnection != null) {
                        if (Global.mqConnection.isStatus()) {
                            try {
                                ActiveMQConnection mq = Global.mqConnection;
                                MapMessage msg = mq.getMapMessageTemplate();
                                msg.setString("program", Global.programId);
                                msg.setString("entity", "OPENING-CV");
                                msg.setString("sourceAccId", listPatientOp.get(i).getSourceAccId());
                                msg.setString("currency", listPatientOp.get(i).getCurId());
                                msg.setInt("cusId", listPatientOp.get(i).getAccountId());
                                //msg.setString("cusType", listPatientOp.get(i).getDiscriminator());
                                msg.setBoolean("deleted", false);
                                msg.setDouble("opAmount", listPatientOp.get(i).getAmount());
                               
                                msg.setString("queueName", "OPD");

                                mq.sendMessage(Global.queueName, msg);
                            } catch (Exception ex) {
                                log.error("uploadToAccount : " + listPatientOp + " - " + ex);
                            }
                        }
                    }

                }
            }
        }*/
    }//GEN-LAST:event_btnToAccActionPerformed

    private void butFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFilterActionPerformed
        search();
    }//GEN-LAST:event_butFilterActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnToAcc;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butFilter;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox<String> cboBillType;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JComboBox<String> cboPtType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblLastOp;
    private javax.swing.JTable tblPatient;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JFormattedTextField txtOpAmt;
    private javax.swing.JFormattedTextField txtOpDate;
    private javax.swing.JTextField txtRegNo;
    // End of variables declaration//GEN-END:variables
}
