/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.OPDCusLabGroup;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.opd.database.view.VOpd;
import com.cv.app.opd.ui.common.XRayEntryTableModel;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.opd.ui.util.UltraSoundResultDialog;
import com.cv.app.opd.ui.util.XRayresultDialog;
import com.cv.app.ot.ui.common.OTDrFeeTableCellEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author asus
 */
public class XRayEntry extends javax.swing.JPanel implements KeyPropagate, SelectionObserver {

    static Logger log = Logger.getLogger(XRayEntry.class.getName());
    private final XRayEntryTableModel tblModel = new XRayEntryTableModel();
    private final AbstractDataAccess dao = Global.dao;

    /**
     * Creates new form ReaderEntry
     */
    public XRayEntry() {
        initComponents();
        initTable();
        initCombo();

        txtFlim1Total.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtFlim2Total.setFormatterFactory(NumberUtil.getDecimalFormat());

        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            if (Global.loginDate == null) {
                Global.loginDate = DateUtil.getTodayDateStr();
            }
            txtFrom.setText(Global.loginDate);
            txtTo.setText(Global.loginDate);
        } else {
            txtFrom.setText(DateUtil.getTodayDateStr());
            txtTo.setText(DateUtil.getTodayDateStr());
        }
    }

    private void initTable() {
        try {
            dao.execSql("delete from tmp_xray_print where user_id = '" + Global.machineId + "'");
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblReaderEntry.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblReaderEntry.getColumnModel().getColumn(0).setPreferredWidth(40);//Date
        tblReaderEntry.getColumnModel().getColumn(1).setPreferredWidth(60);//Vou No
        tblReaderEntry.getColumnModel().getColumn(2).setPreferredWidth(20);//Reg No
        tblReaderEntry.getColumnModel().getColumn(3).setPreferredWidth(20);//Admission No
        tblReaderEntry.getColumnModel().getColumn(4).setPreferredWidth(130);//Pt-Name
        tblReaderEntry.getColumnModel().getColumn(5).setPreferredWidth(15);//Qty
        tblReaderEntry.getColumnModel().getColumn(6).setPreferredWidth(30);//Price
        tblReaderEntry.getColumnModel().getColumn(7).setPreferredWidth(130);//Service name
        tblReaderEntry.getColumnModel().getColumn(8).setPreferredWidth(130);//Dr
        tblReaderEntry.getColumnModel().getColumn(9).setPreferredWidth(130);//Refer Dr
        tblReaderEntry.getColumnModel().getColumn(10).setPreferredWidth(130);//Read Dr
        tblReaderEntry.getColumnModel().getColumn(11).setPreferredWidth(40);//XRay No
        tblReaderEntry.getColumnModel().getColumn(12).setPreferredWidth(30);//XRay Flim Cnt
        tblReaderEntry.getColumnModel().getColumn(13).setPreferredWidth(30);//XRay Flim Cnt1
        tblReaderEntry.getColumnModel().getColumn(14).setPreferredWidth(10);//Status
        tblReaderEntry.getColumnModel().getColumn(15).setPreferredWidth(10);//Print

        //Change JTable cell editor
        tblReaderEntry.getColumnModel().getColumn(9).setCellEditor(
                new OTDrFeeTableCellEditor(dao, this));
        tblReaderEntry.getColumnModel().getColumn(10).setCellEditor(
                new OTDrFeeTableCellEditor(dao, this));
        tblReaderEntry.getColumnModel().getColumn(11).setCellEditor(
                new BestTableCellEditor());
        tblReaderEntry.getColumnModel().getColumn(12).setCellEditor(
                new BestTableCellEditor());
        tblReaderEntry.getColumnModel().getColumn(13).setCellEditor(
                new BestTableCellEditor());
    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboService,
                    dao.findAllHSQL("select o from Service o where o.status = true order by o.serviceName"));
            BindingUtil.BindComboFilter(cboOPDCG,
                    dao.findAllHSQL("select o from OPDCusLabGroup o order by o.groupName"));

            cboService.setSelectedItem(null);

            new ComBoBoxAutoComplete(cboService, this);
            new ComBoBoxAutoComplete(cboOPDCG, this);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getPatient() {
        if (txtRegNo.getText() != null && !txtRegNo.getText().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class, txtRegNo.getText());
                dao.close();

                if (pt == null) {
                    txtRegNo.setText(null);
                    txtPtName.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    txtRegNo.setText(pt.getRegNo());
                    txtPtName.setText(pt.getPatientName());
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtRegNo.setText(null);
            txtPtName.setText(null);
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PatientSearch":
                Patient patient = (Patient) selectObj;
                txtRegNo.setText(patient.getRegNo());
                txtPtName.setText(patient.getPatientName());
                break;
        }
    }

    private void printXRayForm() {
        String reportName = Util1.getPropValue("report.xray.form");
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "clinic/"
                + reportName;
        Map<String, Object> params = new HashMap();
        String compName = Util1.getPropValue("report.company.name");
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        params.put("p_user_id", Global.machineId);
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        try {
            dao.close();
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
            dao.commit();
        } catch (Exception ex) {
            log.error("printXRayForm : " + ex.getMessage());
        }
    }

    private void printUltraSoundForm(String regNo, String name, String admission) {
        String reportName = Util1.getPropValue("report.ultrasound.form");
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "clinic/"
                + reportName;
        Map<String, Object> params = new HashMap();
        String compName = Util1.getPropValue("report.company.name");
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        params.put("p_user_id", Global.machineId);
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("reg_no", regNo);
        params.put("name", name);
        params.put("admission", admission);

        try {
            dao.close();
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
            dao.commit();
        } catch (Exception ex) {
            log.error("printXRayForm : " + ex.getMessage());
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
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboService = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        butSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReaderEntry = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        cboOPDCG = new javax.swing.JComboBox<>();
        cboComplete = new javax.swing.JComboBox<>();
        butPrint = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtFlim1Total = new javax.swing.JFormattedTextField();
        txtFlim2Total = new javax.swing.JFormattedTextField();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From ");

        txtFrom.setFont(Global.textFont);
        txtFrom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFromFocusGained(evt);
            }
        });
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });
        txtFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFromActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To ");

        txtTo.setFont(Global.textFont);
        txtTo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtToFocusGained(evt);
            }
        });
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Service ");

        cboService.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Patient ");

        txtRegNo.setFont(Global.textFont);
        txtRegNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRegNoFocusGained(evt);
            }
        });
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        txtPtName.setFont(Global.textFont);
        txtPtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPtNameFocusGained(evt);
            }
        });
        txtPtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPtNameMouseClicked(evt);
            }
        });

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        tblReaderEntry.setFont(Global.textFont);
        tblReaderEntry.setModel(tblModel);
        tblReaderEntry.setRowHeight(23);
        tblReaderEntry.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblReaderEntryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblReaderEntry);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("OPD CG");

        cboOPDCG.setFont(Global.textFont);

        cboComplete.setFont(Global.textFont);
        cboComplete.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "Complete", "In-Complete" }));

        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        jLabel6.setText("Flim 2 Total : ");

        jLabel7.setText("Flim 1 Total : ");

        txtFlim1Total.setEditable(false);
        txtFlim1Total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtFlim2Total.setEditable(false);
        txtFlim2Total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFlim2Total.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboService, 0, 92, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboOPDCG, 0, 81, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPtName, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboComplete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butPrint))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFlim1Total, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFlim2Total, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSearch)
                    .addComponent(jLabel5)
                    .addComponent(cboOPDCG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboComplete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butPrint))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(txtFlim1Total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFlim2Total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFromActionPerformed

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        String serviceId = "-";

        if (cboService.getSelectedItem() instanceof Service) {
            serviceId = ((Service) cboService.getSelectedItem()).getServiceId().toString();
        }

        String groupId = "-";
        if (cboOPDCG.getSelectedItem() instanceof OPDCusLabGroup) {
            OPDCusLabGroup grp = (OPDCusLabGroup) cboOPDCG.getSelectedItem();
            groupId = grp.getId().toString();
        }

        String complete = cboComplete.getSelectedItem().toString();
        if (complete.equals("ALL")) {
            complete = "-";
        }

        tblModel.search(txtFrom.getText(), txtTo.getText(), serviceId,
                txtRegNo.getText(), txtPtName.getText(), groupId, complete);

        int flim1Ttl = 0;
        int flim2Ttl = 0;
        List<VOpd> listService = tblModel.getListService();
        for (VOpd tmp : listService) {
            flim1Ttl += NumberUtil.NZeroInt(tmp.getXrayFlimCnt());
            flim2Ttl += NumberUtil.NZeroInt(tmp.getXrayFlimCnt1());
        }

        txtFlim1Total.setValue(flim1Ttl);
        txtFlim2Total.setValue(flim2Ttl);
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

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        getPatient();
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void txtPtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPtNameMouseClicked
        if (evt.getClickCount() == 2) {
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtPtNameMouseClicked

    private void txtFromFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFromFocusGained
        txtFrom.selectAll();
    }//GEN-LAST:event_txtFromFocusGained

    private void txtToFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtToFocusGained
        txtTo.selectAll();
    }//GEN-LAST:event_txtToFocusGained

    private void txtRegNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRegNoFocusGained
        txtRegNo.selectAll();
    }//GEN-LAST:event_txtRegNoFocusGained

    private void txtPtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPtNameFocusGained
        txtPtName.selectAll();
    }//GEN-LAST:event_txtPtNameFocusGained

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        int index = tblReaderEntry.convertRowIndexToModel(tblReaderEntry.getSelectedRow());
        if (index != -1) {
            VOpd record = tblModel.getSelectedRecord(index);
            if (Util1.getPropValue("system.opd.ultrasound.group").contains(record.getCatId().toString())) {
                printUltraSoundForm(record.getPatientId(), record.getPatientName(), record.getAdmissionNo());
            } else if (Util1.getPropValue("system.opd.xray.group").contains(record.getCatId().toString())) {
                printXRayForm();
            }
        }
    }//GEN-LAST:event_butPrintActionPerformed

    private void tblReaderEntryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReaderEntryMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblReaderEntry.convertRowIndexToModel(tblReaderEntry.getSelectedRow());
            if (index != -1) {
                VOpd record = tblModel.getSelectedRecord(index);
                if (Util1.getPropValue("system.opd.ultrasound.group").contains(record.getCatId().toString())) {
                    UltraSoundResultDialog entry = new UltraSoundResultDialog(record);
                    entry.setLocationRelativeTo(null);
                    entry.setVisible(true);
                } else if (Util1.getPropValue("system.opd.xray.group").contains(record.getCatId().toString())) {
                    XRayresultDialog entry = new XRayresultDialog(record);
                    entry.setLocationRelativeTo(null);
                    entry.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_tblReaderEntryMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboComplete;
    private javax.swing.JComboBox<String> cboOPDCG;
    private javax.swing.JComboBox<String> cboService;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReaderEntry;
    private javax.swing.JFormattedTextField txtFlim1Total;
    private javax.swing.JFormattedTextField txtFlim2Total;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
