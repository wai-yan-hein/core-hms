/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.inpatient.database.entity.InpCategory;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.inpatient.ui.common.InpCategoryTableModel;
import com.cv.app.inpatient.ui.common.InpMedUsageTableModel;
import com.cv.app.inpatient.ui.common.InpServiceTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
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
public class InpSetup extends javax.swing.JPanel implements KeyPropagate,
        SelectionObserver {

    static Logger log = Logger.getLogger(InpSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;
    private InpCategoryTableModel catTableModel = new InpCategoryTableModel(dao);
    private int selectRow = -1;
    private int selectServiceRow = -1;
    private InpServiceTableModel srvTableModel = new InpServiceTableModel(dao);
    //private InpFeesTableModel feesTableModel = new InpFeesTableModel(dao, this);
    private InpMedUsageTableModel inpMedUsageTableModel = new InpMedUsageTableModel(dao, this);
    private final StartWithRowFilter swrfGroup;
    private final TableRowSorter<TableModel> sorterGroup;
    private final StartWithRowFilter swrfSrv;
    private final TableRowSorter<TableModel> sorterService;

    /**
     * Creates new form OPDSetup
     */
    public InpSetup() {
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

        swrfSrv = new StartWithRowFilter(txtFilter);
        sorterService = new TableRowSorter(tblService.getModel());
        tblService.setRowSorter(sorterService);
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
        //F8 event on tblItem
        tblMedUsage.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblMedUsage.getActionMap().put("F8-Action", actionItemDelete);

        //F8 event on tblService
        tblService.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblService.getActionMap().put("F8-Action", actionSerDelete);

        //F8 event on tblCat
        tblCategory.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCategory.getActionMap().put("F8-Action", actionCatDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblMedUsage.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Inpatient Medicine delete", JOptionPane.YES_NO_OPTION);

                    if (tblMedUsage.getCellEditor() != null) {
                        tblMedUsage.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    inpMedUsageTableModel.delete(tblMedUsage.getSelectedRow());

                }

            }
        }
    };

    private final Action actionSerDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblService.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Inpatient Service delete", JOptionPane.YES_NO_OPTION);

                    if (tblService.getCellEditor() != null) {
                        tblService.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    srvTableModel.delete(tblService.getSelectedRow());

                }

            }
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
        
        tblCategory.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCategory.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblCategory.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());

        tblService.getTableHeader().setFont(Global.lableFont);
        tblService.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblService.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblService.getColumnModel().getColumn(2).setPreferredWidth(10);
        tblService.getColumnModel().getColumn(3).setPreferredWidth(10);
        tblService.getColumnModel().getColumn(4).setPreferredWidth(10);
        tblService.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblService.getColumnModel().getColumn(6).setPreferredWidth(10);
        tblService.getColumnModel().getColumn(7).setPreferredWidth(5);
        tblService.getColumnModel().getColumn(8).setPreferredWidth(5);
        tblService.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblService.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblService.getSelectedRow() >= 0) {
                    selectServiceRow = tblService.convertRowIndexToModel(tblService.getSelectedRow());
                    setService();
                }
            }
        });
        tblService.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor());

        tblMedUsage.getTableHeader().setFont(Global.lableFont);
        tblMedUsage.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblMedUsage.getColumnModel().getColumn(1).setPreferredWidth(150);//Description
        tblMedUsage.getColumnModel().getColumn(2).setPreferredWidth(30);//Qty
        tblMedUsage.getColumnModel().getColumn(3).setPreferredWidth(50);//Unit
        tblMedUsage.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void setCategory() {
        if (selectRow >= 0) {
            InpCategory cat = catTableModel.getInpCategory(selectRow);

            if (cat.getCatId() != null) {
                lblCategory.setText(cat.getCatName());
                srvTableModel.setCatId(cat.getCatId());
            } else {
                lblCategory.setText("...");
                srvTableModel.setCatId(-1);
            }

            //lblService.setText("...");
            //feesTableModel.setSrvId(-1);
            inpMedUsageTableModel.setSrvId(-1);
        }
    }

    private void setService() {
        if (selectServiceRow >= 0) {
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
        }
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
        lblCategory = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMedUsage = new javax.swing.JTable();
        lblService1 = new javax.swing.JLabel();
        txtFilterGroup = new javax.swing.JTextField();
        txtFilter = new javax.swing.JTextField();

        tblCategory.setFont(Global.textFont);
        tblCategory.setModel(catTableModel);
        tblCategory.setRowHeight(23);
        jScrollPane1.setViewportView(tblCategory);

        lblCategory.setFont(Global.lableFont);
        lblCategory.setText("..");

        tblService.setFont(Global.textFont);
        tblService.setModel(srvTableModel);
        tblService.setRowHeight(23);
        jScrollPane2.setViewportView(tblService);

        tblMedUsage.setFont(Global.textFont);
        tblMedUsage.setModel(inpMedUsageTableModel);
        tblMedUsage.setRowHeight(23);
        jScrollPane4.setViewportView(tblMedUsage);

        lblService1.setFont(Global.lableFont);
        lblService1.setText("...");

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilterGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblService1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilterGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblService1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblService1;
    private javax.swing.JTable tblCategory;
    private javax.swing.JTable tblMedUsage;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtFilterGroup;
    // End of variables declaration//GEN-END:variables
}
