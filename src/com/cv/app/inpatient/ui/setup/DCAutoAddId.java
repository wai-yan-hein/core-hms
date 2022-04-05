/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.inpatient.database.view.VDCGroupService;
import com.cv.app.inpatient.ui.common.DCAutoAddIdTableModel;
import com.cv.app.inpatient.ui.common.DCGroupServiceTableModel;
import com.cv.app.inpatient.ui.common.DCTableCellEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
 * @author user
 */
public class DCAutoAddId extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(DCAutoAddId.class.getName());
    private final DCGroupServiceTableModel dcProcedureTableModel = new DCGroupServiceTableModel();
    private final DCAutoAddIdTableModel autoAddIdTableModel = new DCAutoAddIdTableModel();
    private final AbstractDataAccess dao = Global.dao;

    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    private final TableRowSorter<TableModel> sorter1;
    private final StartWithRowFilter swrf1;

   

    /**
     * Creates new form OTAutoAddId
     */
    public DCAutoAddId() {
        initComponents();
        initTable();
        actionMapping();
        swrf = new StartWithRowFilter(txtFilterDC1);
        sorter = new TableRowSorter(tblCategory.getModel());
        tblCategory.setRowSorter(sorter);

        swrf1 = new StartWithRowFilter(txtFilterDC2);
        sorter1 = new TableRowSorter(tblDCAutoAddId.getModel());
        tblDCAutoAddId.setRowSorter(sorter1);

    }

    private void initTable() {
        try {
            List<VDCGroupService> listDCGS = dao.findAllHSQL("select o from VDCGroupService o");
            dcProcedureTableModel.setListGS(listDCGS);

        } catch (Exception ex) {
            log.error("initTable tblOTProcedure : " + ex.toString());
        } finally {
            dao.close();
        }

        tblCategory.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblCategory.getColumnModel().getColumn(0).setPreferredWidth(200);//Group
        tblCategory.getColumnModel().getColumn(1).setPreferredWidth(200);//Service
        tblCategory.getColumnModel().getColumn(2).setPreferredWidth(30);//Status

        //Define table selection model to single row selection.
        tblCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblCategory.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = -1;
                if (tblCategory.getSelectedRow() >= 0) {
                    selectedRow = tblCategory.convertRowIndexToModel(
                            tblCategory.getSelectedRow());
                }
                if (selectedRow >= 0) {
                    VDCGroupService service = dcProcedureTableModel.getSelected(selectedRow);
                    if (service != null) {
                        autoAddIdTableModel.assignAutoId(service.getSrvId());
                    }

                }

            }

        });

        tblDCAutoAddId.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblDCAutoAddId.getColumnModel().getColumn(0).setPreferredWidth(250);//Description
        tblDCAutoAddId.getColumnModel().getColumn(1).setPreferredWidth(50);//Qty
        tblDCAutoAddId.getColumnModel().getColumn(2).setPreferredWidth(50);//Sort Order
        tblDCAutoAddId.getColumnModel().getColumn(0).setCellEditor(
                new DCTableCellEditor(dao));
    }

    private void actionMapping() {

        tblDCAutoAddId.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblDCAutoAddId.getActionMap().put("F8-Action", actionItemDelete);
    }
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
         
            int yes_no = -1;

            if (tblDCAutoAddId.getSelectedRow() >= 0) {
                //dcauto = listDC.get(tblDCAutoAddId.getSelectedRow());

             //   if (dcauto.getKey().getAddServiceId() != null && dcauto.getKey().getTranOption() != null
                 //       && dcauto.getKey().getTranServiceId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "DC Auto Id item delete", JOptionPane.YES_NO_OPTION);

                        if (tblDCAutoAddId.getCellEditor() != null) {
                            tblDCAutoAddId.getCellEditor().stopCellEditing();
                        }
                    } catch (HeadlessException ex) {
                        log.error(ex.toString());
                    }

                    if (yes_no == 0) {
                        autoAddIdTableModel.delete(tblDCAutoAddId.getSelectedRow());
                    }
                //}
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

        txtFilterDC1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategory = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDCAutoAddId = new javax.swing.JTable();
        txtFilterDC2 = new javax.swing.JTextField();

        txtFilterDC1.setFont(Global.textFont);
        txtFilterDC1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterDC1ActionPerformed(evt);
            }
        });
        txtFilterDC1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterDC1KeyReleased(evt);
            }
        });

        tblCategory.setFont(Global.textFont);
        tblCategory.setModel(dcProcedureTableModel);
        tblCategory.setRowHeight(23);
        jScrollPane1.setViewportView(tblCategory);

        tblDCAutoAddId.setFont(Global.textFont);
        tblDCAutoAddId.setModel(autoAddIdTableModel);
        tblDCAutoAddId.setRowHeight(23);
        jScrollPane2.setViewportView(tblDCAutoAddId);

        txtFilterDC2.setFont(Global.textFont);
        txtFilterDC2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterDC2KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtFilterDC1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addComponent(txtFilterDC2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilterDC1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilterDC2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterDC1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterDC1KeyReleased
        if (txtFilterDC1.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorter.setRowFilter(swrf);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(txtFilterDC1.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterDC1KeyReleased

    private void txtFilterDC2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterDC2KeyReleased
        if (txtFilterDC2.getText().isEmpty()) {
            sorter1.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorter1.setRowFilter(swrf1);
            } else {
                sorter1.setRowFilter(RowFilter.regexFilter(txtFilterDC2.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterDC2KeyReleased

    private void txtFilterDC1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterDC1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterDC1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblCategory;
    private javax.swing.JTable tblDCAutoAddId;
    private javax.swing.JTextField txtFilterDC1;
    private javax.swing.JTextField txtFilterDC2;
    // End of variables declaration//GEN-END:variables
}
