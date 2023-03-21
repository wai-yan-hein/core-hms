/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.setup;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.UserRoleTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.ItemTypeMapping;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Privilege;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.UserRole;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.ui.common.ItemTypeMappingTableModel;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.ui.common.JTreeTable;
import com.cv.app.ui.common.MenuNode;
import com.cv.app.ui.common.MenuSystemModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RoleSetup extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(RoleSetup.class.getName());
    private UserRole currUserRole = new UserRole();
    private MenuSystemModel treeModel;
    private JTreeTable treeTable;
    private List<Privilege> privilegeList;
    private final AbstractDataAccess dao = Global.dao;
    private UserRoleTableModel roleTableModel = new UserRoleTableModel();
    private final ItemTypeMappingTableModel itmTableModel = new ItemTypeMappingTableModel();
    private int selectRow = -1;

    /**
     * Creates new form RoleSetup
     */
    public RoleSetup() {
        initComponents();

        try {
            initTreeTable();
            dao.open();
            initTable();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("RoleSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        clear();
        jScrollPane2.setViewportView(treeTable);
    }

    private void initTreeTable() {
        treeModel = new MenuSystemModel();
        treeTable = new JTreeTable(treeModel);
        privilegeList = new ArrayList();
    }

    /**
     * Set the value of currUserRole
     *
     * @param currUserRole new value of currUserRole
     */
    public void setCurrUserRole(UserRole currUserRole) {
        clearPrivilegeStatus(treeModel.getTreeRoot());

        try {
            lblStatus.setText("EDIT");
            dao.open();
            this.currUserRole = (UserRole) dao.find(UserRole.class, currUserRole.getRoleId());
            privilegeList = this.currUserRole.getPrivilege();
            updateStauts(treeModel.getTreeRoot());
            txtRoleName.setText(this.currUserRole.getRoleName());
            cboCurrency.setSelectedItem(this.currUserRole.getCurrency());
            cboPayment.setSelectedItem(this.currUserRole.getPaymentType());
            cboVouStatus.setSelectedItem(this.currUserRole.getVouStatus());
            chkVouPrinter.setSelected(this.currUserRole.isVouPrinter());
            if (this.currUserRole.getTrader() != null) {
                txtTraderId.setText(this.currUserRole.getTrader().getTraderId());
                txtTraderName.setText(this.currUserRole.getTrader().getTraderName());
            }

            List<ItemTypeMapping> listLGM = dao.findAllHSQL(
                    "select o from ItemTypeMapping o where o.key.groupId = " + this.currUserRole.getRoleId());
            itmTableModel.setListLGM(listLGM);

            dao.close();
        } catch (Exception ex) {
            log.error("setCurrUserRole : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        AbstractTableModel model = (AbstractTableModel) treeTable.getModel();
        model.fireTableDataChanged();
    }

    private void clear() {
        tblRole.clearSelection();
        privilegeList = new ArrayList();
        currUserRole = new UserRole();
        txtRoleName.setText(null);
        cboCurrency.setSelectedItem(null);
        cboPayment.setSelectedItem(null);
        cboVouStatus.setSelectedItem(null);
        chkVouPrinter.setSelected(false);
        lblStatus.setText("NEW");
        txtTraderId.setText(null);
        txtTraderName.setText(null);
        clearPrivilegeList(treeModel.getTreeRoot());
        itmTableModel.setListLGM(new ArrayList());
    }

    private void initTable() {
        try {
            roleTableModel.setListUserRole(dao.findAll("UserRole"));
            tblRole.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JComboBox cboLocationCell = new JComboBox();
            cboLocationCell.setFont(Global.textFont); // NOI18N
            BindingUtil.BindCombo(cboLocationCell, dao.findAll("ItemType"));
            tblMapping.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cboLocationCell));
            tblRole.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selectRow = tblRole.convertRowIndexToModel(tblRole.getSelectedRow());
                    if (selectRow >= 0) {
                        setCurrUserRole(roleTableModel.getUserRole(selectRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void createPrivilegeList(MenuNode root) {
        Object[] menuNodes = (Object[]) root.getChildren();
        for (int i = 0; i < menuNodes.length; i++) {
            MenuNode menuNode = (MenuNode) menuNodes[i];
            privilegeList.add(new Privilege(menuNode.getMenu().getMenuId(), menuNode.getAllow()));
            //String menuName = menuNode.getMenu().getMenuNameE();
            menuNode.setAllow(false);

            createPrivilegeList(menuNode);
        }
    }

    private void updateStauts(MenuNode root) {
        //RoleDaoImpl roleDao = new RoleDaoImpl();
        Object[] menuNodes = (Object[]) root.getChildren();
        for (int i = 0; i < menuNodes.length; i++) {
            MenuNode menuNode = (MenuNode) menuNodes[i];
            menuNode.setAllow(getStatus(menuNode.getMenu().getMenuId()));
            updateStauts(menuNode);
        }
    }

    private void clearPrivilegeStatus(MenuNode root) {
        Object[] menuNodes = (Object[]) root.getChildren();

        for (int i = 0; i < menuNodes.length; i++) {
            MenuNode menuNode = (MenuNode) menuNodes[i];
            menuNode.setAllow(false);

            clearPrivilegeStatus(menuNode);
        }
    }

    private boolean getStatus(int id) {
        List<Privilege> privilegeListL = currUserRole.getPrivilege();
        boolean allow = false;

        for (int i = 0; i < privilegeListL.size(); i++) {
            if (privilegeListL.get(i).getMenuId() == id) {
                allow = privilegeListL.get(i).getAllow();
                i = privilegeListL.size();
            }
        }

        return allow;
    }

    private void updateCurrentPrivilegeStatus(MenuNode root) {
        Object[] menuNodes = (Object[]) root.getChildren();
        boolean found;

        for (int i = 0; i < menuNodes.length; i++) {
            MenuNode menuNode = (MenuNode) menuNodes[i];
            found = false;
            for (int j = 0; j < privilegeList.size(); j++) {
                Privilege priv = privilegeList.get(j);

                if (priv.getMenuId().equals(menuNode.getMenu().getMenuId())) {
                    priv.setAllow(menuNode.getAllow());
                    found = true;
                    j = privilegeList.size();
                }
            }

            if (!found) {
                privilegeList.add(new Privilege(menuNode.getMenu().getMenuId(), menuNode.getAllow()));
            }

            updateCurrentPrivilegeStatus(menuNode);
        }
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtRoleName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Role Name must not be blank or null.",
                    "Role null error.", JOptionPane.ERROR_MESSAGE);
            txtRoleName.requestFocusInWindow();
        } else {
            currUserRole.setRoleName(txtRoleName.getText().trim());
            currUserRole.setCurrency((Currency) cboCurrency.getSelectedItem());
            currUserRole.setPaymentType((PaymentType) cboPayment.getSelectedItem());
            currUserRole.setVouPrinter(chkVouPrinter.isSelected());
            currUserRole.setVouStatus((VouStatus) cboVouStatus.getSelectedItem());
            currUserRole.setUpdatedDate(new Date());

            MenuNode root = treeModel.getTreeRoot();

            if (lblStatus.getText().equals("EDIT")) {
                updateCurrentPrivilegeStatus(root);
            } else {
                createPrivilegeList(root);
            }

            currUserRole.setPrivilege(privilegeList);
        }

        return status;
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
            BindingUtil.BindCombo(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindCombo(cboVouStatus, dao.findAll("VouStatus"));

            new ComBoBoxAutoComplete(cboPayment);
            new ComBoBoxAutoComplete(cboVouStatus);
            new ComBoBoxAutoComplete(cboCurrency);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void clearPrivilegeList(MenuNode root) {
        Object[] menuNodes = (Object[]) root.getChildren();
        for (int i = 0; i < menuNodes.length; i++) {
            MenuNode menuNode = (MenuNode) menuNodes[i];
            menuNode.setAllow(false);

            clearPrivilegeList(menuNode);
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("CustomerList")) {
            Trader trader = (Trader) selectObj;

            currUserRole.setTrader(trader);
            txtTraderId.setText(trader.getTraderId());
            txtTraderName.setText(trader.getTraderName());
        }
    }

    private void getCustomerList() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Customer List", dao, -1);
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
        tblRole = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtRoleName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cboVouStatus = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        chkVouPrinter = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        txtTraderId = new javax.swing.JTextField();
        txtTraderName = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblMapping = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();

        tblRole.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        tblRole.setModel(roleTableModel);
        tblRole.setRowHeight(23);
        jScrollPane1.setViewportView(tblRole);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Role Name");

        txtRoleName.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        jScrollPane2.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("User Default"));

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Payment ");

        cboPayment.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou Status");

        cboVouStatus.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Currency");

        cboCurrency.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Vou-Printer");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Customer");

        txtTraderId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTraderIdActionPerformed(evt);
            }
        });

        txtTraderName.setEditable(false);
        txtTraderName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTraderNameMouseClicked(evt);
            }
        });

        tblMapping.setFont(Global.textFont);
        tblMapping.setModel(itmTableModel);
        tblMapping.setRowHeight(23);
        jScrollPane3.setViewportView(tblMapping);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkVouPrinter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(txtTraderId, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTraderName))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(cboCurrency, 0, 501, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cboPayment, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cboVouStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                        .addGap(11, 11, 11)))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel4, jLabel5});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtTraderId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTraderName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(chkVouPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtRoleName))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 545, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtRoleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(butClear)
                                .addComponent(butSave))
                            .addComponent(lblStatus)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                dao.save(currUserRole);

                List<ItemTypeMapping> listLGM = itmTableModel.getListLGM();
                for (ItemTypeMapping lgm : listLGM) {
                    if (lgm.getKey().getItemType() != null) {
                        lgm.getKey().setGroupId(currUserRole.getRoleId());
                        dao.save(lgm);
                    }
                }

                if (lblStatus.getText().equals("NEW")) {
                    roleTableModel.addUserRole(currUserRole);
                } else {
                    roleTableModel.setUserRole(selectRow, currUserRole);
                }
                clear();
            }
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

  private void txtTraderNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTraderNameMouseClicked
      if (evt.getClickCount() == 2) {
          getCustomerList();
      }
  }//GEN-LAST:event_txtTraderNameMouseClicked

  private void txtTraderIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTraderIdActionPerformed
      if (txtTraderId.getText().isEmpty()) {
          txtTraderName.setText(null);
          currUserRole.setTrader(null);
      }
  }//GEN-LAST:event_txtTraderIdActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboVouStatus;
    private javax.swing.JCheckBox chkVouPrinter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblMapping;
    private javax.swing.JTable tblRole;
    private javax.swing.JTextField txtRoleName;
    private javax.swing.JTextField txtTraderId;
    private javax.swing.JTextField txtTraderName;
    // End of variables declaration//GEN-END:variables
}
