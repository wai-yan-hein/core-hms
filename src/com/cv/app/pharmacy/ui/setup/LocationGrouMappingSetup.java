/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.LocationGroup;
import com.cv.app.pharmacy.database.entity.LocationGroupMapping;
import com.cv.app.pharmacy.ui.common.LocationGroupMappingTableModel;
import com.cv.app.pharmacy.ui.common.LocationGroupTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class LocationGrouMappingSetup extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(LocationGrouMappingSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private LocationGroup lg = new LocationGroup();
    private final LocationGroupTableModel lgTableModel = new LocationGroupTableModel();
    private final LocationGroupMappingTableModel lgmTableModel = new LocationGroupMappingTableModel();

    /**
     * Creates new form LocationGrouMappingSetup
     */
    public LocationGrouMappingSetup() {
        super(Util1.getParent(), true);
        initComponents();
        initTable();
    }

    private void setLocationGroup(LocationGroup lg) {
        if (lg != null) {
            this.lg = null;
            this.lg = lg;

            int groupId = -1;
            if (lg.getId() != null) {
                groupId = lg.getId();
            }
            txtGroupName.setText(lg.getGroupName());
            txtDeptCode.setText(lg.getDeptCode());
            if (groupId == -1) {
                lblStatus.setText("NEW");
                lgmTableModel.setListLGM(new ArrayList());
            } else {
                lblStatus.setText("EDIT");
                try {
                    List<LocationGroupMapping> listLGM = dao.findAllHSQL(
                            "select o from LocationGroupMapping o where o.key.groupId = " + groupId);
                    lgmTableModel.setListLGM(listLGM);
                } catch (Exception ex) {
                    log.error("setLocationGroup : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }
    }

    private void clear() {
        lg = new LocationGroup();
        setLocationGroup(lg);
        System.gc();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtGroupName.getText().trim().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Name cannot be blank or null.",
                    "Name null error.", JOptionPane.ERROR_MESSAGE);
        } else {
            lg.setGroupName(txtGroupName.getText().trim());
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(lg);
                List<LocationGroupMapping> listLGM = lgmTableModel.getListLGM();
                for (LocationGroupMapping lgm : listLGM) {
                    if (lgm.getKey().getLocation() != null) {
                        lgm.getKey().setGroupId(lg.getId());
                        dao.save(lgm);
                    }
                }
                clear();
                List<LocationGroup> listLG = dao.findAll("LocationGroup");
                lgTableModel.setListLG(listLG);
            } catch (Exception ex) {
                log.error("save : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void initTable() {
        try {
            //tblLocationGroup.getColumnModel().getColumn(0)
            List<LocationGroup> listLG = dao.findAll("LocationGroup");
            lgTableModel.setListLG(listLG);

            JComboBox cboLocationCell = new JComboBox();
            cboLocationCell.setFont(Global.textFont); // NOI18N
            BindingUtil.BindCombo(cboLocationCell, dao.findAll("Location"));
            tblMapping.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cboLocationCell));

            tblLocationGroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblLocationGroup.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        return;
                    }
                    try {
                        if (tblLocationGroup.getSelectedRow() != -1) {
                            int selectedRow = tblLocationGroup.convertRowIndexToModel(tblLocationGroup.getSelectedRow());

                            if (selectedRow != -1) {
                                setLocationGroup(lgTableModel.getLocationGroup(selectedRow));
                            }
                        }
                    } catch (Exception ex) {
                        log.error("initTable-->valueChanged : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLocationGroup = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtGroupName = new javax.swing.JTextField();
        butSave = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMapping = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtDeptCode = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Group");
        setMinimumSize(new java.awt.Dimension(500, 400));
        setPreferredSize(new java.awt.Dimension(700, 600));

        txtFilter.setFont(Global.textFont);

        tblLocationGroup.setFont(Global.textFont);
        tblLocationGroup.setModel(lgTableModel);
        tblLocationGroup.setRowHeight(23);
        jScrollPane1.setViewportView(tblLocationGroup);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Group Name ");

        txtGroupName.setFont(Global.textFont);

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        tblMapping.setFont(Global.textFont);
        tblMapping.setModel(lgmTableModel);
        tblMapping.setRowHeight(23);
        jScrollPane2.setViewportView(tblMapping);

        lblStatus.setText("NEW");

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jLabel2.setText("Dept Code");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtGroupName)
                            .addComponent(txtDeptCode))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butSave)
                            .addComponent(lblStatus)
                            .addComponent(butClear))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblLocationGroup;
    private javax.swing.JTable tblMapping;
    private javax.swing.JTextField txtDeptCode;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtGroupName;
    // End of variables declaration//GEN-END:variables
}
