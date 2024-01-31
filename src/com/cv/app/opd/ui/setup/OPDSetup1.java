/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.entity.LabMachine;
import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.opd.database.entity.OPDGroup;
import com.cv.app.opd.database.entity.OPDLabGroup;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.opd.ui.common.OPDCategoryTableModel;
import com.cv.app.opd.ui.common.OPDMedUsageTableModel;
import com.cv.app.opd.ui.common.ServiceTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
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
public class OPDSetup1 extends javax.swing.JPanel implements KeyPropagate,
        SelectionObserver {

    static Logger log = Logger.getLogger(OPDSetup1.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;
    private OPDCategoryTableModel catTableModel = new OPDCategoryTableModel(dao);
    private int selectRow = -1;
    private int selectServiceRow = -1;
    private ServiceTableModel srvTableModel = new ServiceTableModel(dao);
    //private FeesTableModel feesTableModel = new FeesTableModel(dao, this);
    private OPDMedUsageTableModel opdMedUsageTableModel = new OPDMedUsageTableModel(dao, this);
    private final StartWithRowFilter swrfGroup;
    private final TableRowSorter<TableModel> sorterGroup;
    private final StartWithRowFilter swrfSrv;
    private final TableRowSorter<TableModel> sorterService;

    /**
     * Creates new form OPDSetup
     */
    public OPDSetup1() {
        initComponents();
        initCombo();
        initTable();
        actionMapping();
        //feesTableModel.setGroupId(((OPDGroup) cboGroup.getSelectedItem()).getGroupId());
        opdMedUsageTableModel.addNewRow();

        swrfGroup = new StartWithRowFilter(txtFilterGroup);
        sorterGroup = new TableRowSorter(tblCategory.getModel());
        tblCategory.setRowSorter(sorterGroup);

        swrfSrv = new StartWithRowFilter(txtFilterService);
        sorterService = new TableRowSorter(tblService.getModel());
        tblService.setRowSorter(sorterService);

        setGroupId();
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboGroup, dao.findAll("OPDGroup"));
            BindingUtil.BindComboFilter(cboLabGroupFilter, dao.findAllHSQL("select o from OPDLabGroup o order by o.description"));
            List listMachine = dao.findAllHSQL("select o from LabMachine o order by o.lMachineName");
            listMachine.add(0, "No Machine");
            BindingUtil.BindCombo(cboLabMachine, listMachine);
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
        tblCategory.getColumnModel().getColumn(0).setPreferredWidth(50);//Description
        tblCategory.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblCategory.getColumnModel().getColumn(1).setPreferredWidth(120);//Description
        tblCategory.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());

        tblService.getTableHeader().setFont(Global.lableFont);
        tblService.getColumnModel().getColumn(0).setPreferredWidth(20);//Code
        tblService.getColumnModel().getColumn(1).setPreferredWidth(150);//Description
        tblService.getColumnModel().getColumn(2).setPreferredWidth(25);//Fees
        tblService.getColumnModel().getColumn(3).setPreferredWidth(25);//Srv Fee
        tblService.getColumnModel().getColumn(4).setPreferredWidth(25);//MO Fee
        tblService.getColumnModel().getColumn(5).setPreferredWidth(25);//Staff Fee
        tblService.getColumnModel().getColumn(6).setPreferredWidth(25);//Tech Fee
        tblService.getColumnModel().getColumn(7).setPreferredWidth(25);//Refer Fee
        tblService.getColumnModel().getColumn(8).setPreferredWidth(25);//Read Fee
        tblService.getColumnModel().getColumn(9).setPreferredWidth(3);//%
        tblService.getColumnModel().getColumn(10).setPreferredWidth(3);//CFS
        tblService.getColumnModel().getColumn(11).setPreferredWidth(3);//Active
        tblService.getColumnModel().getColumn(12).setPreferredWidth(56);//Doctor
        tblService.getColumnModel().getColumn(13).setPreferredWidth(25);//Cost

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
        tblService.getColumnModel().getColumn(7).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(8).setCellEditor(new BestTableCellEditor());
        tblService.getColumnModel().getColumn(13).setCellEditor(new BestTableCellEditor());

        try {
            JComboBox cboDoctor = new JComboBox();
            BindingUtil.BindCombo(cboDoctor, dao.findAllHSQL(
                    "select o from Doctor o order by o.doctorName"));
            tblService.getColumnModel().getColumn(12).setCellEditor(new DefaultCellEditor(cboDoctor));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }

        tblMedUsage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMedUsage.getTableHeader().setFont(Global.lableFont);
        tblMedUsage.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblMedUsage.getColumnModel().getColumn(1).setPreferredWidth(150);//Description
        tblMedUsage.getColumnModel().getColumn(2).setPreferredWidth(30);//Qty
        tblMedUsage.getColumnModel().getColumn(3).setPreferredWidth(50);//Unit
        tblMedUsage.getColumnModel().getColumn(4).setPreferredWidth(50);//Location
        tblMedUsage.getColumnModel().getColumn(5).setPreferredWidth(50);//Calc Status
        
        tblMedUsage.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblMedUsage.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
        
        try {
            JComboBox cboLocationCell = new JComboBox();
            cboLocationCell.setFont(Global.textFont); // NOI18N
            BindingUtil.BindCombo(cboLocationCell, dao.findAll("Location"));
            tblMedUsage.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboLocationCell));
            
            JComboBox cboCalcStatus = new JComboBox();
            List<String> listStatus = new ArrayList();
            listStatus.add("AUTO");
            listStatus.add("ASK");
            BindingUtil.BindCombo(cboCalcStatus, listStatus);
            tblMedUsage.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cboCalcStatus));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        }
    }

    private void setCategory() {
        if (selectRow >= 0) {
            OPDCategory cat = catTableModel.getOPDCategory(selectRow);

            if (cat.getCatId() != null) {
                lblCategory.setText(cat.getCatName());
                srvTableModel.setCatId(cat.getCatId());
            } else {
                lblCategory.setText("...");
                srvTableModel.setCatId(-1);
            }

            //feesTableModel.setSrvId(-1);
            opdMedUsageTableModel.setSrvId(-1, getMachineId());
        }
    }

    private void setService() {
        if (selectServiceRow >= 0) {
            Service srv = srvTableModel.getService(selectServiceRow);

            if (srv.getServiceId() != null) {
                lblService1.setText(srv.getServiceName());
                //feesTableModel.setSrvId(srv.getServiceId());
                opdMedUsageTableModel.setSrvId(srv.getServiceId(), getMachineId());
            } else {
                lblService1.setText("...");
                //feesTableModel.setSrvId(-1);
                opdMedUsageTableModel.setSrvId(-1, getMachineId());
            }
        }
    }

    private int getMachineId(){
        if(cboLabMachine.getSelectedItem() instanceof LabMachine){
            int id = ((LabMachine)cboLabMachine.getSelectedItem()).getlMachineId();
            return id;
        }else{
            return 0;
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
        //F8 event on tblMed
        tblMedUsage.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblMedUsage.getActionMap().put("F8-Action", actionItemDelete);

        //F8 event on tblService
        tblService.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblService.getActionMap().put("F8-Action", actionServiceDelete);

        //F8 event on tblService
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
                            "Item delete", JOptionPane.YES_NO_OPTION);
                    if (tblMedUsage.getCellEditor() != null) {
                        tblMedUsage.getCellEditor().stopCellEditing();
                    }
                } catch (HeadlessException ex) {
                }

                if (yes_no == 0) {
                    opdMedUsageTableModel.delete(tblMedUsage.getSelectedRow());
                }

            }
        }
    };

    private final Action actionServiceDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblService.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Service delete", JOptionPane.YES_NO_OPTION);

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
        if (cboLabGroupFilter.getSelectedItem() instanceof OPDLabGroup) {
            OPDLabGroup opdLGroup = (OPDLabGroup) cboLabGroupFilter.getSelectedItem();
            srvTableModel.setLabGroupId(opdLGroup.getId());
        } else {
            srvTableModel.setLabGroupId(-1);
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

        cboGroup = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategory = new javax.swing.JTable();
        lblCategory = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        butGroupSetup = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMedUsage = new javax.swing.JTable();
        lblService1 = new javax.swing.JLabel();
        txtFilterService = new javax.swing.JTextField();
        txtFilterGroup = new javax.swing.JTextField();
        cboLabGroupFilter = new javax.swing.JComboBox<>();
        butLabGroupSetup = new javax.swing.JButton();
        cboLabMachine = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

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

        lblCategory.setFont(Global.lableFont);
        lblCategory.setText("..");

        tblService.setFont(Global.textFont);
        tblService.setModel(srvTableModel);
        tblService.setRowHeight(23);
        jScrollPane2.setViewportView(tblService);

        butGroupSetup.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butGroupSetup.setText("...");
        butGroupSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGroupSetupActionPerformed(evt);
            }
        });

        tblMedUsage.setFont(Global.textFont);
        tblMedUsage.setModel(opdMedUsageTableModel);
        tblMedUsage.setRowHeight(23);
        jScrollPane4.setViewportView(tblMedUsage);

        lblService1.setFont(Global.lableFont);
        lblService1.setText("...");

        txtFilterService.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterServiceKeyReleased(evt);
            }
        });

        txtFilterGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterGroupKeyReleased(evt);
            }
        });

        cboLabGroupFilter.setFont(Global.textFont);
        cboLabGroupFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLabGroupFilterActionPerformed(evt);
            }
        });

        butLabGroupSetup.setText("...");
        butLabGroupSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLabGroupSetupActionPerformed(evt);
            }
        });

        cboLabMachine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLabMachineActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Lab Machine : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butGroupSetup, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtFilterGroup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCategory, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilterService, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLabGroupFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butLabGroupSetup))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblService1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLabMachine, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCategory)
                    .addComponent(butGroupSetup)
                    .addComponent(txtFilterService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLabGroupFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butLabGroupSetup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblService1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLabMachine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilterGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
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
            lblCategory.setText("...");
            //feesTableModel.setSrvId(-1);
            opdMedUsageTableModel.setSrvId(-1, getMachineId());
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

    private void txtFilterServiceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterServiceKeyReleased
        if (txtFilterService.getText().isEmpty()) {
            sorterService.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorterService.setRowFilter(swrfSrv);
        } else {
            sorterService.setRowFilter(RowFilter.regexFilter(txtFilterService.getText()));
        }
    }//GEN-LAST:event_txtFilterServiceKeyReleased

    private void cboLabGroupFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLabGroupFilterActionPerformed
        setGroupId();
    }//GEN-LAST:event_cboLabGroupFilterActionPerformed

    private void butLabGroupSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLabGroupSetupActionPerformed
        LabGroupSetup dialog = new LabGroupSetup();
        bindStatus = false;
        initCombo();
    }//GEN-LAST:event_butLabGroupSetupActionPerformed

    private void cboLabMachineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLabMachineActionPerformed
        opdMedUsageTableModel.setMachineId(getMachineId());
    }//GEN-LAST:event_cboLabMachineActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butGroupSetup;
    private javax.swing.JButton butLabGroupSetup;
    private javax.swing.JComboBox cboGroup;
    private javax.swing.JComboBox<String> cboLabGroupFilter;
    private javax.swing.JComboBox<String> cboLabMachine;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblService1;
    private javax.swing.JTable tblCategory;
    private javax.swing.JTable tblMedUsage;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField txtFilterGroup;
    private javax.swing.JTextField txtFilterService;
    // End of variables declaration//GEN-END:variables
}
