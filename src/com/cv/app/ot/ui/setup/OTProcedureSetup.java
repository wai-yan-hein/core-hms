/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.inpatient.ui.setup.InpSetup;
import com.cv.app.ot.database.entity.OTProcedure;
import com.cv.app.ot.database.entity.OTProcedureGroup;
import com.cv.app.ot.ui.common.OTGroupTableModel;
import com.cv.app.ot.ui.common.OTMedUsageTableModel;
import com.cv.app.ot.ui.common.OTServiceTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class OTProcedureSetup extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(InpSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final OTGroupTableModel tblOTGroupTableModel = new OTGroupTableModel(dao);
    private final OTServiceTableModel tblOTServiceTableModel = new OTServiceTableModel(dao);
    private final OTMedUsageTableModel tblOTServiceMedUsageTableModel = new OTMedUsageTableModel(dao, this);
    private final StartWithRowFilter swrfGroup;
    private final TableRowSorter<TableModel> sorterGroup;
    private final StartWithRowFilter swrfSrv;
    private final TableRowSorter<TableModel> sorterService;
    
    /**
     * Creates new form OTProcedure
     */
    public OTProcedureSetup() {
        initComponents();
        initTable();
        actionMapping();
        
        swrfGroup = new StartWithRowFilter(txtFilterGroup);
        sorterGroup = new TableRowSorter(tblOTGroup.getModel());
        tblOTGroup.setRowSorter(sorterGroup);
        
        swrfSrv = new StartWithRowFilter(txtFilter);
        sorterService = new TableRowSorter(tblOTService.getModel());
        tblOTService.setRowSorter(sorterService);
    }
    
     private void formActionKeyMapping(JComponent jc) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
        jc.getActionMap().put("Ctrl-F8-Action", actionDelete);
    }

     private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };
    //FormAction
    @Override
    public void save() {

    }

    @Override
    public void newForm() {

    }

    @Override
    public void history() {

    }

    @Override
    public void delete() {

    }
    
     private void actionMapping() {
        //F8 event on tblItem
        tblOTServiceMedUsage .getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblOTServiceMedUsage.getActionMap().put("F8-Action", actionItemDelete);

        //F8 event on tblService
        tblOTService .getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblOTService.getActionMap().put("F8-Action", actionSerDelete);
        
        //F8 event on tblCat
        tblOTGroup.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblOTGroup.getActionMap().put("F8-Action", actionCatDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblOTServiceMedUsage.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "OT Medicine delete", JOptionPane.YES_NO_OPTION);

                    if(tblOTServiceMedUsage.getCellEditor() != null){
                        tblOTServiceMedUsage.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    tblOTServiceMedUsageTableModel.delete(tblOTServiceMedUsage.getSelectedRow());

                }

            }
        }
    };

    private final Action actionSerDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblOTService.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "OT Service delete", JOptionPane.YES_NO_OPTION);

                    if(tblOTService.getCellEditor() != null){
                        tblOTService.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    tblOTServiceTableModel.delete(tblOTService.getSelectedRow());

                }

            }
        }
    };

    private final Action actionCatDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblOTGroup.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "OT Group delete", JOptionPane.YES_NO_OPTION);

                    if(tblOTGroup.getCellEditor() != null){
                        tblOTGroup.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    tblOTGroupTableModel.delete(tblOTGroup.getSelectedRow());

                }

            }
        }
    };

    @Override
    public void deleteCopy() {

    }

    @Override
    public void print() {

    }

    //KeyPropagate
    @Override
    public void keyEvent(KeyEvent e) {

    }

    //SelectionObserver
    @Override
    public void selected(Object source, Object selectObj) {

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
        tblOTGroup.getTableHeader().setFont(Global.lableFont);
        tblOTGroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOTGroup.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (tblOTGroup.getSelectedRow() >= 0) {
                            int selectRow = tblOTGroup.convertRowIndexToModel(tblOTGroup.getSelectedRow());
                            OTProcedureGroup otpg = tblOTGroupTableModel.getListOTG().get(selectRow);
                            Integer groupId = otpg.getGroupId();
                            
                            if (groupId != null) {
                                tblOTServiceTableModel.setGroupId(groupId);
                                lblGroup.setText(otpg.getGroupName());
                            }else{
                                tblOTServiceTableModel.setGroupId(-1);
                                lblGroup.setText("...");
                                tblOTServiceMedUsageTableModel.setSrvId(-1);
                                lblService.setText("...");
                            }
                        }
                    }
                });
        tblOTGroupTableModel.getOTGroup();
        tblOTGroup.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblOTGroup.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblOTGroup.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblOTGroup.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        
        tblOTService.getTableHeader().setFont(Global.lableFont);
        tblOTService.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblOTService.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblOTService.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblOTService.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblOTService.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblOTService.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblOTService.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblOTService.getColumnModel().getColumn(7).setPreferredWidth(10);
        tblOTService.getColumnModel().getColumn(8).setPreferredWidth(10);
        tblOTService.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOTService.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (tblOTService.getSelectedRow() >= 0) {
                            int selectServiceRow = tblOTService.convertRowIndexToModel(tblOTService.getSelectedRow());
                            OTProcedure srv = tblOTServiceTableModel.getListOTP().get(selectServiceRow);
                            Integer srvId = srv.getServiceId();
                            if(srvId != null){
                                tblOTServiceMedUsageTableModel.setSrvId(srvId);
                                lblService.setText(srv.getServiceName());
                            }else{
                                tblOTServiceMedUsageTableModel.setSrvId(-1);
                                lblService.setText("...");
                            }
                        }
                    }
                });
        tblOTService.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblOTService.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        tblOTService.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
        tblOTService.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
        tblOTService.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());
        tblOTService.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor());
        tblOTService.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor());
        
        tblOTServiceMedUsage.getTableHeader().setFont(Global.lableFont);
        tblOTServiceMedUsage.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblOTServiceMedUsage.getColumnModel().getColumn(1).setPreferredWidth(150);//Description
        tblOTServiceMedUsage.getColumnModel().getColumn(2).setPreferredWidth(30);//Qty
        tblOTServiceMedUsage.getColumnModel().getColumn(3).setPreferredWidth(50);//Unit
        tblOTServiceMedUsage.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        
        try {
            JComboBox cboLocationCell = new JComboBox();
            cboLocationCell.setFont(Global.textFont); // NOI18N
            BindingUtil.BindCombo(cboLocationCell, dao.findAll("Location"));
            tblOTServiceMedUsage.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboLocationCell));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblOTGroup = new javax.swing.JTable();
        lblGroup = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOTService = new javax.swing.JTable();
        lblService = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblOTServiceMedUsage = new javax.swing.JTable();
        txtFilterGroup = new javax.swing.JTextField();
        txtFilter = new javax.swing.JTextField();

        tblOTGroup.setFont(Global.textFont);
        tblOTGroup.setModel(tblOTGroupTableModel);
        tblOTGroup.setMinimumSize(new java.awt.Dimension(150, 64));
        tblOTGroup.setRowHeight(23);
        tblOTGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblOTGroupKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblOTGroup);

        lblGroup.setFont(Global.lableFont);
        lblGroup.setText("...");

        tblOTService.setFont(Global.textFont);
        tblOTService.setModel(tblOTServiceTableModel);
        tblOTService.setRowHeight(23);
        jScrollPane2.setViewportView(tblOTService);

        lblService.setFont(Global.lableFont);
        lblService.setText("...");

        tblOTServiceMedUsage.setFont(Global.textFont);
        tblOTServiceMedUsage.setModel(tblOTServiceMedUsageTableModel);
        tblOTServiceMedUsage.setRowHeight(23);
        jScrollPane3.setViewportView(tblOTServiceMedUsage);

        txtFilterGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterGroupKeyReleased(evt);
            }
        });

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addComponent(txtFilterGroup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblService, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGroup)
                    .addComponent(txtFilterGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblService)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblOTGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOTGroupKeyReleased
        if (txtFilterGroup.getText().isEmpty()) {
            sorterGroup.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorterGroup.setRowFilter(swrfGroup);
            } else {
                sorterGroup.setRowFilter(RowFilter.regexFilter(txtFilterGroup.getText()));
            }
        }
    }//GEN-LAST:event_tblOTGroupKeyReleased

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty()) {
            sorterService.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorterService.setRowFilter(swrfSrv);
            } else {
                sorterService.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtFilterGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterGroupKeyReleased
        if (txtFilterGroup.getText().isEmpty()) {
            sorterGroup.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorterGroup.setRowFilter(swrfGroup);
            } else {
                sorterGroup.setRowFilter(RowFilter.regexFilter(txtFilterGroup.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterGroupKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblGroup;
    private javax.swing.JLabel lblService;
    private javax.swing.JTable tblOTGroup;
    private javax.swing.JTable tblOTService;
    private javax.swing.JTable tblOTServiceMedUsage;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtFilterGroup;
    // End of variables declaration//GEN-END:variables
}
