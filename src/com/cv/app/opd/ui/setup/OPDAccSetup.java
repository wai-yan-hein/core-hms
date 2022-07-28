/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.opd.database.entity.OPDGroup;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.opd.ui.common.OPDMedUsageTableModel;
import com.cv.app.opd.ui.common.ServiceTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import com.cv.app.opd.ui.common.OPDCategoryTableModel1;

/**
 *
 * @author winswe
 */
public class OPDAccSetup extends javax.swing.JPanel implements KeyPropagate,
        SelectionObserver {

    static Logger log = Logger.getLogger(OPDAccSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;
    private OPDCategoryTableModel1 catTableModel = new OPDCategoryTableModel1(dao);
    private int selectRow = -1;
    private int selectServiceRow = -1;
    private ServiceTableModel srvTableModel = new ServiceTableModel(dao);
    //private FeesTableModel feesTableModel = new FeesTableModel(dao, this);
    private OPDMedUsageTableModel opdMedUsageTableModel = new OPDMedUsageTableModel(dao, this);
    private final StartWithRowFilter swrfGroup;
    private final TableRowSorter<TableModel> sorterGroup;

    /**
     * Creates new form OPDSetup
     */
    public OPDAccSetup() {
        initComponents();
        initCombo();
        initTable();
        actionMapping();
        //feesTableModel.setGroupId(((OPDGroup) cboGroup.getSelectedItem()).getGroupId());
        opdMedUsageTableModel.addNewRow();

        swrfGroup = new StartWithRowFilter(txtFilterGroup);
        sorterGroup = new TableRowSorter(tblCategory.getModel());
        tblCategory.setRowSorter(sorterGroup);
        setGroupId();
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboGroup, dao.findAll("OPDGroup"));
            bindStatus = true;
            catTableModel.setGroupId(((OPDGroup) cboGroup.getSelectedItem()).getGroupId());
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblCategory.getTableHeader().setFont(Global.lableFont);
        tblCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCategory.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblCategory.getSelectedRow() >= 0) {
                    selectRow = tblCategory.convertRowIndexToModel(tblCategory.getSelectedRow());
                    setCategory();
                }
            }
        });
        tblCategory.getColumnModel().getColumn(0).setPreferredWidth(120);//Description
        //tblCategory.getColumnModel().getColumn(1).setPreferredWidth(20);//Account Id
        tblCategory.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(7).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(8).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(9).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(10).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(11).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(12).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(13).setCellEditor(new BestTableCellEditor());
    }

    private void setCategory() {
        if (selectRow >= 0) {
            OPDCategory cat = catTableModel.getOPDCategory(selectRow);

            if (cat.getCatId() != null) {
                srvTableModel.setCatId(cat.getCatId());
            } else {
                srvTableModel.setCatId(-1);
            }

            //feesTableModel.setSrvId(-1);
            opdMedUsageTableModel.setSrvId(-1);
        }
    }

    private void setService() {
        if (selectServiceRow >= 0) {
            Service srv = srvTableModel.getService(selectServiceRow);

            if (srv.getServiceId() != null) {
                //feesTableModel.setSrvId(srv.getServiceId());
                opdMedUsageTableModel.setSrvId(srv.getServiceId());
            } else {
                //feesTableModel.setSrvId(-1);
                opdMedUsageTableModel.setSrvId(-1);
            }
        }
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

    private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };

    private void actionMapping() {

        //F8 event on tblService
        tblCategory.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCategory.getActionMap().put("F8-Action", actionCatDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

        }
    };

    private final Action actionServiceDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private final Action actionCatDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblCategory.getSelectedRow() >= 0) {
                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Category delete", JOptionPane.YES_NO_OPTION);

                    if (tblCategory.getCellEditor() != null) {
                        tblCategory.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    catTableModel.delete(tblCategory.getSelectedRow());
                }
            }
        }
    };

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "FeesChange":
                srvTableModel.updatePriceVersion(selectServiceRow);
                break;
            case "MedUsageChange":
                srvTableModel.updateMedUVersion(selectServiceRow);
                break;
        }
    }

    private void setGroupId() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboGroup = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategory = new javax.swing.JTable();
        butGroupSetup = new javax.swing.JButton();
        txtFilterGroup = new javax.swing.JTextField();

        cboGroup.setFont(Global.textFont);
        cboGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboGroupActionPerformed(evt);
            }
        });

        tblCategory.setFont(Global.textFont);
        tblCategory.setModel(catTableModel);
        tblCategory.setRowHeight(23);
        jScrollPane1.setViewportView(tblCategory);

        butGroupSetup.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butGroupSetup.setText("...");
        butGroupSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGroupSetupActionPerformed(evt);
            }
        });

        txtFilterGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterGroupKeyReleased(evt);
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
                        .addComponent(cboGroup, 0, 290, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butGroupSetup, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtFilterGroup))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butGroupSetup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFilterGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butGroupSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGroupSetupActionPerformed
        OPDGroupSetup dialog = new OPDGroupSetup();
        bindStatus = false;
        initCombo();
    }//GEN-LAST:event_butGroupSetupActionPerformed

    private void cboGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboGroupActionPerformed
        if (bindStatus) {
            int groupId = ((OPDGroup) cboGroup.getSelectedItem()).getGroupId();
            catTableModel.setGroupId(groupId);
            //feesTableModel.setGroupId(groupId);
            //feesTableModel.setSrvId(-1);
            opdMedUsageTableModel.setSrvId(-1);
            srvTableModel.setCatId(-1);
        }
    }//GEN-LAST:event_cboGroupActionPerformed

    private void txtFilterGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterGroupKeyReleased
        if (txtFilterGroup.getText().isEmpty()) {
            sorterGroup.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorterGroup.setRowFilter(swrfGroup);
        } else {
            sorterGroup.setRowFilter(RowFilter.regexFilter(txtFilterGroup.getText()));
        }
    }//GEN-LAST:event_txtFilterGroupKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butGroupSetup;
    private javax.swing.JComboBox cboGroup;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCategory;
    private javax.swing.JTextField txtFilterGroup;
    // End of variables declaration//GEN-END:variables
}
