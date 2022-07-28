/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * AppUserSetup.java
 *
 * Created on Apr 22, 2012, 9:50:37 PM
 */
package com.cv.app.ui.setup;

import com.cv.app.common.AppuserTableModel;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.UserRole;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.BindingUtil;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author winswe
 */
public class AppUserSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(AppUserSetup.class.getName());
    private AbstractDataAccess dao = Global.dao;
    private BestAppFocusTraversalPolicy focusPolicy;
    private AppuserTableModel userTableModel = new AppuserTableModel();
    private Appuser currAppuser = new Appuser();
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form AppUserSetup
     */
    public AppUserSetup() {
        initComponents();

        try {
            dao.open();
            initCombo();
            initTable();
            userTableModel.setListAppuser(dao.findAll("Appuser"));
            sorter = new TableRowSorter(tblUser.getModel());
            tblUser.setRowSorter(sorter);
            dao.close();
        } catch (Exception ex) {
            log.error("AppUserSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        clear();
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    /**
     * Set the value of currAppuser
     *
     * @param currAppuser new value of currAppuser
     */
    public void setCurrAppuser(Appuser currAppuser) {
        this.currAppuser = currAppuser;

        txtEmail.setText(currAppuser.getEmail());
        txtPassword.setText(currAppuser.getPassword());
        txtPhone.setText(currAppuser.getPhone());
        txtShortName.setText(currAppuser.getUserShortName());
        txtUserName.setText(currAppuser.getUserName());
        txtUserId.setText(currAppuser.getUserId());
        lblStatus.setText("EDIT");
        chkActive.setSelected(currAppuser.getActive());
        cboRole.setSelectedItem(currAppuser.getUserRole());
        cboLocation.setSelectedItem(currAppuser.getDefLocation());
    }

    private void clear() {
        lblStatus.setText("NEW");
        currAppuser = new Appuser();
        txtConfirm.setText(null);
        txtEmail.setText(null);
        txtPassword.setText(null);
        txtPhone.setText(null);
        txtShortName.setText(null);
        txtUserName.setText(null);
        chkActive.setSelected(false);
        txtUserId.setText(null);
        cboRole.setSelectedItem(null);
        cboLocation.setSelectedItem(null);
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboRole, dao.findAll("UserRole"));
            new ComBoBoxAutoComplete(cboRole);
            BindingUtil.BindCombo(cboLocation, dao.findAll("Location"));
            new ComBoBoxAutoComplete(cboLocation);
        } catch (Exception ex) {
            log.error("initCombo : ");
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblUser.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblUser.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblUser.getColumnModel().getColumn(2).setPreferredWidth(10);

        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUser.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblUser.convertRowIndexToModel(tblUser.getSelectedRow());
                if (selectedRow >= 0) {
                    setCurrAppuser(userTableModel.getAppuser(selectedRow));
                }
            }
        });
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtUserName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Name must not be blank or null.",
                    "Name null error.", JOptionPane.ERROR_MESSAGE);
            txtUserName.requestFocusInWindow();
        } else if (txtShortName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Short Name must not be blank or null.",
                    "Short Name null error.", JOptionPane.ERROR_MESSAGE);
            txtShortName.requestFocusInWindow();
        } else if (txtPassword.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Password must not be blank or null.",
                    "Password null error.", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocusInWindow();
        } else if (txtConfirm.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Confirm Password must not be blank or null.",
                    "Confirm Password null error.", JOptionPane.ERROR_MESSAGE);
            txtConfirm.requestFocusInWindow();
        } else if (!txtPassword.getText().equals(txtConfirm.getText())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Password and Confirm Password must be the same.",
                    "Password not the same", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocusInWindow();
        } else if (cboRole.getSelectedItem() == null) {
            status = false;
            JOptionPane.showMessageDialog(this, "You must select role.",
                    "Role not selected.", JOptionPane.ERROR_MESSAGE);
            cboRole.requestFocusInWindow();
        } else {
            currAppuser.setUserName(txtUserName.getText().trim());
            currAppuser.setPassword(txtPassword.getText().trim());
            currAppuser.setUserShortName(txtShortName.getText().trim());
            currAppuser.setEmail(txtEmail.getText().trim());
            currAppuser.setPhone(txtPhone.getText().trim());
            currAppuser.setActive(chkActive.isSelected());
            currAppuser.setUserRole((UserRole) cboRole.getSelectedItem());
            currAppuser.setDefLocation((Location) cboLocation.getSelectedItem());
            currAppuser.setUpdatedDate(new Date());
            if (lblStatus.getText().equals("NEW")) {
                currAppuser.setUserId(getUserId());
            }
        }

        return status;
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector(7);

        focusOrder.add(txtUserName);
        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
    private void AddFocusMoveKey() {
        Set backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);

        Set forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);

        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));

        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
    }//</editor-fold>

    public void setFocus() {
        txtUserName.requestFocusInWindow();
    }

    private String getUserId() {
        String tmpId = PharmacyUtil.getSeq("AppUser", dao).toString();
        int idLength = 3;

        tmpId = tmpId.replaceAll(".0", "");
        if (tmpId.length() < 3) {
            int currLength = tmpId.length();

            for (int i = 0; i < (idLength - currLength); i++) {
                tmpId = "0" + tmpId;
            }
        }
        return tmpId;
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
        tblUser = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtUserId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtConfirm = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        txtShortName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cboRole = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();

        tblUser.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        tblUser.setModel(userTableModel);
        tblUser.setRowHeight(23);
        tblUser.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblUser);

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFilterKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jButton1.setText("...");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("ID");

        txtUserId.setEditable(false);
        txtUserId.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtUserName.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Password");

        txtPassword.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Confirm");

        txtConfirm.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Short Name");

        txtShortName.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Email");

        txtEmail.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Phone");

        txtPhone.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Role");

        cboRole.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Active");

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

        lblStatus.setText("NEW");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Def-Location");

        cboLocation.setFont(new java.awt.Font("Zawgyi-One", 0, 11));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkActive, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboRole, 0, 172, Short.MAX_VALUE)
                            .addComponent(txtPhone)
                            .addComponent(txtEmail)
                            .addComponent(txtShortName)
                            .addComponent(txtConfirm)
                            .addComponent(txtPassword)
                            .addComponent(txtUserName)
                            .addComponent(txtUserId)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(cboLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1)
                    .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtShortName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(cboRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkActive)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                dao.save(currAppuser);
                if (lblStatus.getText().equals("NEW")) {
                    userTableModel.addAppuser(currAppuser);
                } else {
                    if (selectedRow >= 0) {
                        userTableModel.setAppuser(selectedRow, currAppuser);
                    }
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "User Save", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void txtFilterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterKeyPressed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
        }
    }//GEN-LAST:event_txtFilterKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboRole;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblUser;
    private javax.swing.JPasswordField txtConfirm;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtShortName;
    private javax.swing.JTextField txtUserId;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
