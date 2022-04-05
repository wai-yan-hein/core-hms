/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.inpatient.ui.common.InpCategoryTableModel1;
import com.cv.app.inpatient.ui.common.InpMedUsageTableModel;
import com.cv.app.inpatient.ui.common.InpServiceTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
 * @Eitar
 */
public class InpAccSetup extends javax.swing.JPanel implements KeyPropagate,
        SelectionObserver {

    static Logger log = Logger.getLogger(InpAccSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;
    private InpCategoryTableModel1 catTableModel = new InpCategoryTableModel1(dao);
    private int selectRow = -1;
    private int selectServiceRow = -1;
    private InpServiceTableModel srvTableModel = new InpServiceTableModel(dao);
    //private InpFeesTableModel feesTableModel = new InpFeesTableModel(dao, this);
    private InpMedUsageTableModel inpMedUsageTableModel = new InpMedUsageTableModel(dao, this);
    private final StartWithRowFilter swrfGroup;
    private final TableRowSorter<TableModel> sorterGroup;
    //private final StartWithRowFilter swrfSrv;
    //private final TableRowSorter<TableModel> sorterService;

    /**
     * Creates new form OPDSetup
     */
    public InpAccSetup() {
        initComponents();
        initCombo();
        initTable();
        actionMapping();
        catTableModel.getCategory();
        //feesTableModel.setGroupId(((InpGroup) cboGroup.getSelectedItem()).getGroupId());
        inpMedUsageTableModel.addNewRow();

        swrfGroup = new StartWithRowFilter(txtFilterGroup);
        sorterGroup = new TableRowSorter(tblCategory.getModel());
        tblCategory.setRowSorter(sorterGroup);

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

    public void delete() {

    }

    private void actionMapping() {

        //F8 event on tblCat
        tblCategory.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCategory.getActionMap().put("F8-Action", actionCatDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private final Action actionSerDelete = new AbstractAction() {
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
                            "Inpatient Category delete", JOptionPane.YES_NO_OPTION);

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

    private void initCombo() {
        //BindingUtil.BindCombo(cboGroup, dao.findAll("InpGroup"));
        bindStatus = true;
        //catTableModel.setGroupId(((InpGroup) cboGroup.getSelectedItem()).getGroupId());
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
        tblCategory.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblCategory.getColumnModel().getColumn(1).setPreferredWidth(20);
        tblCategory.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblCategory.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
    }

    private void setCategory() {
        /*if (selectRow >= 0) {
            InpCategory cat = catTableModel.getInpCategory(selectRow);

            if (cat.getCatId() != null) {
                srvTableModel.setCatId(cat.getCatId());
            } else {
                srvTableModel.setCatId(-1);
            }

            //lblService.setText("...");
            //feesTableModel.setSrvId(-1);
            inpMedUsageTableModel.setSrvId(-1);
        }*/
    }

    private void setService() {
        /*if (selectServiceRow >= 0) {
            InpService srv = srvTableModel.getInpService(selectServiceRow);

            if (srv.getServiceId() != null) {
                //lblService.setText(srv.getServiceName());
                lblService1.setText(srv.getServiceName());
                //feesTableModel.setSrvId(srv.getServiceId());
                inpMedUsageTableModel.setSrvId(srv.getServiceId());
            } else {
                //lblService.setText("...");
                lblService1.setText("...");
                //feesTableModel.setSrvId(-1);
                inpMedUsageTableModel.setSrvId(-1);
            }
        }*/
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            delete();
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {

        } else if (e.getKeyCode() == KeyEvent.VK_F5) {

        } else if (e.getKeyCode() == KeyEvent.VK_F7) {

        } else if (e.getKeyCode() == KeyEvent.VK_F9) {

        } else if (e.getKeyCode() == KeyEvent.VK_F10) {

        }
    }

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategory = new javax.swing.JTable();
        txtFilterGroup = new javax.swing.JTextField();

        tblCategory.setFont(Global.textFont);
        tblCategory.setModel(catTableModel);
        tblCategory.setRowHeight(23);
        jScrollPane1.setViewportView(tblCategory);

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                    .addComponent(txtFilterGroup))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilterGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JTable tblCategory;
    private javax.swing.JTextField txtFilterGroup;
    // End of variables declaration//GEN-END:variables
}
