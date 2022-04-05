/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.ot.database.view.VOTGroupService;
import com.cv.app.ot.ui.common.OTAutoAddIdTableModel;
import com.cv.app.ot.ui.common.OTGroupServiceTableModel;
import com.cv.app.ot.ui.common.OTTableCellEditor;
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
public class OTAutoAddId extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(OTAutoAddId.class.getName());
    private final OTGroupServiceTableModel otProcedureTableModel = new OTGroupServiceTableModel();
    private final OTAutoAddIdTableModel autoAddIdTableModel = new OTAutoAddIdTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    private final TableRowSorter<TableModel> sorter1;
    private final StartWithRowFilter swrf1;

    /**
     * Creates new form OTAutoAddId
     */
    public OTAutoAddId() {
        initComponents();
        initTable();
        actionMapping();

        swrf = new StartWithRowFilter(txtFilter1);
        sorter = new TableRowSorter(tblOTProcedure.getModel());
        tblOTProcedure.setRowSorter(sorter);

        swrf1 = new StartWithRowFilter(txtFilter2);
        sorter1 = new TableRowSorter(tblAutoAddId.getModel());
        tblAutoAddId.setRowSorter(sorter1);
    }

    private void initTable() {
        try {
            List<VOTGroupService> listGS = dao.findAllHSQL("select o from VOTGroupService o");
            otProcedureTableModel.setListGS(listGS);
        } catch (Exception ex) {
            log.error("initTable tblOTProcedure : " + ex.getMessage());
        } finally {
            dao.close();
        }

        tblOTProcedure.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblOTProcedure.getColumnModel().getColumn(0).setPreferredWidth(200);//Group
        tblOTProcedure.getColumnModel().getColumn(1).setPreferredWidth(200);//Service
        tblOTProcedure.getColumnModel().getColumn(2).setPreferredWidth(30);//Status

        //Define table selection model to single row selection.
        tblOTProcedure.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblOTProcedure.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = -1;
                if (tblOTProcedure.getSelectedRow() >= 0) {
                    selectedRow = tblOTProcedure.convertRowIndexToModel(
                            tblOTProcedure.getSelectedRow());
                }
                if (selectedRow >= 0) {
                    VOTGroupService service = otProcedureTableModel.getSelected(selectedRow);
                    if (service != null) {
                        autoAddIdTableModel.assignAutoId(service.getSrvId());
                    }
                }
            }
        });

        tblAutoAddId.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblAutoAddId.getColumnModel().getColumn(0).setPreferredWidth(250);//Description
        tblAutoAddId.getColumnModel().getColumn(1).setPreferredWidth(50);//Qty
        tblAutoAddId.getColumnModel().getColumn(2).setPreferredWidth(50);//Sort Order
        tblAutoAddId.getColumnModel().getColumn(0).setCellEditor(
                new OTTableCellEditor(dao));
    }
    
    private void actionMapping() {

        tblAutoAddId.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblAutoAddId.getActionMap().put("F8-Action", actionItemDelete);
    }
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
         
            int yes_no = -1;

            if (tblAutoAddId.getSelectedRow() >= 0) {
                //dcauto = listDC.get(tblDCAutoAddId.getSelectedRow());

             //   if (dcauto.getKey().getAddServiceId() != null && dcauto.getKey().getTranOption() != null
                 //       && dcauto.getKey().getTranServiceId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "OT Auto Id item delete", JOptionPane.YES_NO_OPTION);

                        if (tblAutoAddId.getCellEditor() != null) {
                            tblAutoAddId.getCellEditor().stopCellEditing();
                        }
                    } catch (HeadlessException ex) {
                        log.error(ex.toString());
                    }

                    if (yes_no == 0) {
                        autoAddIdTableModel.delete(tblAutoAddId.getSelectedRow());
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

        txtFilter1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOTProcedure = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAutoAddId = new javax.swing.JTable();
        txtFilter2 = new javax.swing.JTextField();

        txtFilter1.setFont(Global.textFont);
        txtFilter1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilter1KeyReleased(evt);
            }
        });

        tblOTProcedure.setFont(Global.textFont);
        tblOTProcedure.setModel(otProcedureTableModel);
        tblOTProcedure.setRowHeight(23);
        jScrollPane1.setViewportView(tblOTProcedure);

        tblAutoAddId.setFont(Global.textFont);
        tblAutoAddId.setModel(autoAddIdTableModel);
        tblAutoAddId.setRowHeight(23);
        jScrollPane2.setViewportView(tblAutoAddId);

        txtFilter2.setFont(Global.textFont);
        txtFilter2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilter2KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtFilter1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addComponent(txtFilter2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilter1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilter1KeyReleased
        if (txtFilter1.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorter.setRowFilter(swrf);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(txtFilter1.getText()));
            }
        }
    }//GEN-LAST:event_txtFilter1KeyReleased

    private void txtFilter2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilter2KeyReleased
        if (txtFilter2.getText().isEmpty()) {
            sorter1.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorter1.setRowFilter(swrf1);
            } else {
                sorter1.setRowFilter(RowFilter.regexFilter(txtFilter2.getText()));
            }
        }
    }//GEN-LAST:event_txtFilter2KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblAutoAddId;
    private javax.swing.JTable tblOTProcedure;
    private javax.swing.JTextField txtFilter1;
    private javax.swing.JTextField txtFilter2;
    // End of variables declaration//GEN-END:variables
}
