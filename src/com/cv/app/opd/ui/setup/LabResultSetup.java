/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.opd.database.entity.OPDLabResult;
import com.cv.app.opd.database.view.VService;
import com.cv.app.opd.ui.common.LabResultTableModel;
import com.cv.app.opd.ui.common.LabResultTableModelInd;
import com.cv.app.opd.ui.common.LabTestTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class LabResultSetup extends javax.swing.JPanel implements KeyPropagate {

    static Logger log = Logger.getLogger(LabResultSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final LabTestTableModel labTestTableModel = new LabTestTableModel(dao);
    private final LabResultTableModel labResultTableModel = new LabResultTableModel(dao);
    private final LabResultTableModelInd labResultIndTableModel = new LabResultTableModelInd(dao);
    private boolean bindStatus = false;

    /**
     * Creates new form OPDSetup
     */
    public LabResultSetup() {
        initComponents();
        initCombo();
        initTable();
        actionMapping();
        initTextPane();
        List<VService> tmpList = dao.findAllHSQL(
                "select o from VService o where o.groupId = 1 order by o.serviceName");
        if (tmpList != null) {
            labTestTableModel.setListService(tmpList);
        }
    }

    private void initTable() {
        tblLabTest.getTableHeader().setFont(Global.lableFont);
        tblLabTest.getColumnModel().getColumn(0).setPreferredWidth(10);//Code
        tblLabTest.getColumnModel().getColumn(1).setPreferredWidth(200);//Lab Test
        tblLabTest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLabTest.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblLabTest.getSelectedRow() >= 0) {
                    int index = tblLabTest.convertRowIndexToModel(tblLabTest.getSelectedRow());
                    VService service = labTestTableModel.getListService().get(index);

                    if (service != null) {
                        lblLabTest.setText(service.getServiceName());
                        labResultTableModel.setSelectTestId(service.getServiceId());
                        txtLabRemark.setText(service.getLabRemark());
                        txtLabResultRemark.setText("");
                    }
                }
            }
        });

        tblLabResult.getTableHeader().setFont(Global.lableFont);
        tblLabResult.getColumnModel().getColumn(0).setPreferredWidth(150);//Printed Text
        tblLabResult.getColumnModel().getColumn(1).setPreferredWidth(50);//Unit
        tblLabResult.getColumnModel().getColumn(2).setPreferredWidth(80);//Normal
        tblLabResult.getColumnModel().getColumn(3).setPreferredWidth(40);//Type
        tblLabResult.getColumnModel().getColumn(4).setPreferredWidth(40);//Sort Order

        tblLabResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLabResult.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblLabResult.getSelectedRow() >= 0) {
                    int index = tblLabResult.convertRowIndexToModel(tblLabResult.getSelectedRow());
                    OPDLabResult result = labResultTableModel.getListResult().get(index);

                    if (result != null) {
                        lblLabResult.setText(result.getResultText() + " indicator ...");
                        labResultIndTableModel.setSelectResultId(result.getResultId());
                        txtLabResultRemark.setText(result.getLabResultRemark());
                    }
                }
            }
        });
        JComboBox cboResultType = new JComboBox();
        BindingUtil.BindCombo(cboResultType, dao.findAll("OPDResultType"));
        tblLabResult.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cboResultType));

        tblLabResultInd.getTableHeader().setFont(Global.lableFont);
        tblLabResultInd.getColumnModel().getColumn(0).setPreferredWidth(50);//High Value
        tblLabResultInd.getColumnModel().getColumn(1).setPreferredWidth(150);//Low Value
        tblLabResultInd.getColumnModel().getColumn(2).setPreferredWidth(30);//Sex
        JComboBox cboSex = new JComboBox();
        BindingUtil.BindCombo(cboSex, dao.findAll("Gender"));
        tblLabResultInd.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cboSex));
        tblLabResultInd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initCombo() {
        BindingUtil.BindCombo(cboGroup, dao.findAllHSQL("select o from OPDCategory o where groupId = 1 order by o.catName"));
        bindStatus = true;
        labTestTableModel.setGroupId(((OPDCategory) cboGroup.getSelectedItem()).getCatId());
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {

        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {

        } else if (e.getKeyCode() == KeyEvent.VK_F5) {

        } else if (e.getKeyCode() == KeyEvent.VK_F7) {

        } else if (e.getKeyCode() == KeyEvent.VK_F9) {

        } else if (e.getKeyCode() == KeyEvent.VK_F10) {

        }
    }

    public void delete() {

    }

    private void actionMapping() {
        //F8 event on tblMed
        tblLabResultInd.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblLabResultInd.getActionMap().put("F8-Action", actionLabResultIndDelete);

        //F8 event on tblService
        tblLabResult.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblLabResult.getActionMap().put("F8-Action", actionLabResultDelete);
    }

    private final Action actionLabResultIndDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblLabResultInd.getSelectedRow() >= 0) {
                try {
                    if(tblLabResultInd.getCellEditor() != null){
                        tblLabResultInd.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                int index = tblLabResultInd.convertRowIndexToModel(tblLabResultInd.getSelectedRow());
                labResultIndTableModel.delete(index);
            }
        }
    };

    private final Action actionLabResultDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblLabResult.getSelectedRow() >= 0) {
                try {
                    if(tblLabResult.getCellEditor() != null){
                        tblLabResult.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                int index = tblLabResult.convertRowIndexToModel(tblLabResult.getSelectedRow());
                labResultTableModel.delete(index);
            }
        }
    };

    private final Action actionCatDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblLabTest.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Category delete", JOptionPane.YES_NO_OPTION);

                    if(tblLabTest.getCellEditor() != null){
                        tblLabTest.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    //catTableModel.delete(tblCategory.getSelectedRow());
                }

            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblLabTest = new javax.swing.JTable();
        lblLabTest = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLabResult = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblLabResultInd = new javax.swing.JTable();
        lblLabResult = new javax.swing.JLabel();
        cboGroup = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtLabResultRemark = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtLabRemark = new javax.swing.JTextPane();

        tblLabTest.setFont(Global.textFont);
        tblLabTest.setModel(labTestTableModel);
        tblLabTest.setRowHeight(23);
        jScrollPane1.setViewportView(tblLabTest);

        lblLabTest.setFont(Global.lableFont);
        lblLabTest.setText("..");

        tblLabResult.setFont(Global.textFont);
        tblLabResult.setModel(labResultTableModel);
        tblLabResult.setRowHeight(23);
        jScrollPane2.setViewportView(tblLabResult);

        tblLabResultInd.setFont(Global.textFont);
        tblLabResultInd.setModel(labResultIndTableModel);
        tblLabResultInd.setRowHeight(23);
        jScrollPane4.setViewportView(tblLabResultInd);

        lblLabResult.setFont(Global.lableFont);
        lblLabResult.setText("...");

        cboGroup.setFont(Global.textFont);
        cboGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboGroupActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Remark");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Remark");

        jScrollPane6.setViewportView(txtLabResultRemark);

        jScrollPane5.setViewportView(txtLabRemark);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cboGroup, 0, 244, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .addComponent(jScrollPane5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE))
                    .addComponent(lblLabTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLabResult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLabTest)
                    .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblLabResult, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboGroupActionPerformed
        if (bindStatus) {
            int groupId = ((OPDCategory) cboGroup.getSelectedItem()).getCatId();
            labTestTableModel.setGroupId(groupId);
            //lblCategory.setText("...");
            //lblService.setText("...");
            //feesTableModel.setSrvId(-1);
            //srvTableModel.setCatId(-1);
        }

    }//GEN-LAST:event_cboGroupActionPerformed

    private void initTextPane() {
        DocumentListener docLabRemark = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                txtLabRemarkChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                txtLabRemarkChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                txtLabRemarkChange();
            }
        };
        txtLabRemark.getDocument().addDocumentListener(docLabRemark);

        DocumentListener docResultRemark = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                txtResultRemarkChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                txtResultRemarkChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                txtResultRemarkChange();
            }
        };
        txtLabResultRemark.getDocument().addDocumentListener(docResultRemark);
    }

    private void txtLabRemarkChange() {
        int index = tblLabTest.convertRowIndexToModel(tblLabTest.getSelectedRow());
        VService service = labTestTableModel.getListService().get(index);
        if (service != null) {
            if (!txtLabRemark.getText().equals("")) {
                dao.execSql("update opd_service set lab_remark = '" + txtLabRemark.getText() + "' where service_id =" + service.getServiceId());
            } else {
                dao.execSql("update opd_service set lab_remark = '' where service_id =" + service.getServiceId());
            }
        }
    }

    private void txtResultRemarkChange() {
        if (tblLabResult.getSelectedRow() >= 0) {
            int index = tblLabResult.convertRowIndexToModel(tblLabResult.getSelectedRow());
            OPDLabResult result = labResultTableModel.getListResult().get(index);
            if (result != null) {
                //result.setLabResultRemark(txtLabResultRemark.getText());
                if (!txtLabResultRemark.getText().equals("")) {
                    dao.execSql("update opd_lab_result set lab_result_remark = '" + txtLabResultRemark.getText() + "' where result_id =" + result.getResultId());
                } else {
                    dao.execSql("update opd_lab_result set lab_result_remark = '' where result_id =" + result.getResultId());
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblLabResult;
    private javax.swing.JLabel lblLabTest;
    private javax.swing.JTable tblLabResult;
    private javax.swing.JTable tblLabResultInd;
    private javax.swing.JTable tblLabTest;
    private javax.swing.JTextPane txtLabRemark;
    private javax.swing.JTextPane txtLabResultRemark;
    // End of variables declaration//GEN-END:variables
}
