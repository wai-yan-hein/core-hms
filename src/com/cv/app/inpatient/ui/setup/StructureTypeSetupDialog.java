/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.BuildingStructurType;
import com.cv.app.inpatient.ui.common.StructureTypeTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author MyintMo
 */
public class StructureTypeSetupDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(StructureTypeSetupDialog.class.getName());
    private final StructureTypeTableModel tblTypeModel = new StructureTypeTableModel();
    private BuildingStructurType currStructure = new BuildingStructurType();
    private final AbstractDataAccess dao = Global.dao;
    private int selectRow = -1;

    /**
     * Creates new form StructureTypeSetupDialog
     */
    public StructureTypeSetupDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initTable();
    }

    public StructureTypeSetupDialog() {
        super(Util1.getParent(), true);
        initComponents();
        initTable();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtTypeDesp.getText().trim().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid type description.",
                    "Type Description", JOptionPane.ERROR_MESSAGE);
        } else {
            if (currStructure == null) {
                currStructure = new BuildingStructurType();
            }

            currStructure.setTypeDesp(txtTypeDesp.getText().trim());
        }

        return status;
    }

    private void clear() {
        currStructure = new BuildingStructurType();
        txtTypeDesp.setText(null);
    }

    private void save() {
        if (isValidEntry()) {
            try {
                boolean isEdit = false;
                if (currStructure.getTypeId() != null) {
                    isEdit = true;
                }

                dao.save(currStructure);
                if (isEdit) {
                    tblTypeModel.add(currStructure);
                }
                clear();
                List<BuildingStructurType> listBST = dao.findAllHSQL(
                        "select o from BuildingStructurType o order by o.typeDesp");
                tblTypeModel.setListBST(listBST);
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
        }
    }

    private void initTable() {
        try {
            List<BuildingStructurType> listBST = dao.findAllHSQL(
                    "select o from BuildingStructurType o order by o.typeDesp");
            tblTypeModel.setListBST(listBST);
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblType.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblType.getSelectedRow() >= 0) {
                    selectRow = tblType.convertRowIndexToModel(tblType.getSelectedRow());
                    List<BuildingStructurType> listBST = tblTypeModel.getListBST();
                    if (listBST != null) {
                        if (!listBST.isEmpty()) {
                            setSelType(listBST.get(selectRow));
                        }
                    }
                }
            }
        });
    }

    private void setSelType(BuildingStructurType type) {
        currStructure = type;
        txtTypeDesp.setText(currStructure.getTypeDesp());
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
        tblType = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtTypeDesp = new javax.swing.JTextField();
        butSave = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Structure Type Setup");

        tblType.setFont(Global.textFont);
        tblType.setModel(tblTypeModel);
        tblType.setRowHeight(23);
        jScrollPane1.setViewportView(tblType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Type Desp ");

        txtTypeDesp.setFont(Global.textFont);

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete))
                    .addComponent(txtTypeDesp))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtTypeDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butSave)
                            .addComponent(butClear)
                            .addComponent(butDelete)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        if (currStructure != null) {
            if (currStructure.getTypeId() != null) {
                try {
                    dao.deleteSQL("delete from building_structur_type where type_id = "
                            + currStructure.getTypeId().toString());
                    tblTypeModel.deleteRow(selectRow);
                } catch (Exception ex) {
                    log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    JOptionPane.showMessageDialog(Util1.getParent(), ex.toString(),
                            "Delete Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    dao.close();
                }
            }
        }
    }//GEN-LAST:event_butDeleteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StructureTypeSetupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StructureTypeSetupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StructureTypeSetupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StructureTypeSetupDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                StructureTypeSetupDialog dialog = new StructureTypeSetupDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblType;
    private javax.swing.JTextField txtTypeDesp;
    // End of variables declaration//GEN-END:variables
}
