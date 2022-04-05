/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.opd.ui.common.TechnicianSetupTableModel;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author admin
 */
public class TechnicianSetup extends javax.swing.JDialog {

    private final TechnicianSetupTableModel tblTechnicialTableModel = new TechnicianSetupTableModel();
    private final TableRowSorter<TableModel> sorter;

    /**
     * Creates new form LabResultAutoCompleteTextSetup
     */
    public TechnicianSetup() {
        super(Util1.getParent(), true);
        initComponents();
        initTable();
        sorter = new TableRowSorter(tblTechnician.getModel());
        tblTechnician.setRowSorter(sorter);
        actionMapping();
    }

    private void initTable() {
        tblTechnician.getColumnModel().getColumn(0).setPreferredWidth(300);
        tblTechnician.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblTechnicialTableModel.getTechnician();
    }

    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            if (entry.getStringValue(0) != null) {
                if (entry.getStringValue(0).toUpperCase().startsWith(
                        txtFilter.getText().toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
    };

    private void actionMapping() {
        tblTechnician.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblTechnician.getActionMap().put("F8-Action", actionDelete);
    }

    private final Action actionDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblTechnician.getSelectedRow() >= 0) {
                try {
                    if(tblTechnician.getCellEditor() != null){
                        tblTechnician.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }
                tblTechnicialTableModel.delete(tblTechnician.getSelectedRow());
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
        tblTechnician = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Technician Setup");

        tblTechnician.setFont(Global.textFont);
        tblTechnician.setModel(tblTechnicialTableModel);
        tblTechnician.setRowHeight(23);
        jScrollPane1.setViewportView(tblTechnician);

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorter.setRowFilter(startsWithFilter);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTechnician;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
