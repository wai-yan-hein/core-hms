/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.view.VOPDGroupService;
import com.cv.app.opd.ui.common.OPDAutoAddIdTableModel;
import com.cv.app.opd.ui.common.OPDGroupServiceTableModel;
import com.cv.app.opd.ui.common.OPDTableCellEditor;
//import com.cv.app.ot.ui.common.OPDGroupServiceTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
public class OPDAutoAddId extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(OPDAutoAddId.class.getName());
    private final OPDGroupServiceTableModel opdProcedureTableModel = new OPDGroupServiceTableModel();
    private final OPDAutoAddIdTableModel autoAddIdTableModel = new OPDAutoAddIdTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    private final TableRowSorter<TableModel> sorter1;
    private final StartWithRowFilter swrf1;

    /**
     * Creates new form OTAutoAddId
     */
    public OPDAutoAddId() {
        initComponents();
        initTable();
actionMapping();
        swrf = new StartWithRowFilter(txtFilter1);
        sorter = new TableRowSorter(tblOPDProcedure.getModel());
        tblOPDProcedure.setRowSorter(sorter);

        swrf1 = new StartWithRowFilter(txtFilter2);
        sorter1 = new TableRowSorter(tblAutoAddId.getModel());
        tblAutoAddId.setRowSorter(sorter1);
    }

    private void initTable() {
        try {
            List<VOPDGroupService> listGS = dao.findAllHSQL("select o from VOPDGroupService o");
            opdProcedureTableModel.setListGS(listGS);
        } catch (Exception ex) {
            log.error("initTable tblOTProcedure : " + ex.getMessage());
        } finally {
            dao.close();
        }

        tblOPDProcedure.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblOPDProcedure.getColumnModel().getColumn(0).setPreferredWidth(200);//Group
        tblOPDProcedure.getColumnModel().getColumn(1).setPreferredWidth(200);//Service
        tblOPDProcedure.getColumnModel().getColumn(2).setPreferredWidth(30);//Status

        //Define table selection model to single row selection.
        tblOPDProcedure.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblOPDProcedure.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = -1;
                if (tblOPDProcedure.getSelectedRow() >= 0) {
                    selectedRow = tblOPDProcedure.convertRowIndexToModel(
                            tblOPDProcedure.getSelectedRow());
                }
                if (selectedRow >= 0) {
                    VOPDGroupService service = opdProcedureTableModel.getSelected(selectedRow);
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
                new OPDTableCellEditor(dao));
    }
 private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
         //   Product p;

            if (tblAutoAddId.getSelectedRow() >= 0) {

                autoAddIdTableModel.delete(tblAutoAddId.getSelectedRow());

            }
        }

    };

    private void actionMapping() {
        tblAutoAddId.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblAutoAddId.getActionMap().put("F8-Action", actionItemDelete);
    }
    
    
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
        tblOPDProcedure = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAutoAddId = new javax.swing.JTable();
        txtFilter2 = new javax.swing.JTextField();

        txtFilter1.setFont(Global.textFont);
        txtFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilter1ActionPerformed(evt);
            }
        });
        txtFilter1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilter1KeyReleased(evt);
            }
        });

        tblOPDProcedure.setFont(Global.textFont);
        tblOPDProcedure.setModel(opdProcedureTableModel);
        tblOPDProcedure.setRowHeight(23);
        jScrollPane1.setViewportView(tblOPDProcedure);

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

    private void txtFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilter1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilter1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblAutoAddId;
    private javax.swing.JTable tblOPDProcedure;
    private javax.swing.JTextField txtFilter1;
    private javax.swing.JTextField txtFilter2;
    // End of variables declaration//GEN-END:variables
}
