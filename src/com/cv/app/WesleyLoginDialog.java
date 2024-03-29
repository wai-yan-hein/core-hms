/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.BestDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.SysProperty;
import com.cv.app.pharmacy.database.entity.UserRole;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class WesleyLoginDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(WesleyLoginDialog.class.getName());
    private boolean status = false;
    private AbstractDataAccess dao = new BestDataAccess();
    private int loginCount = 0;
    private BestAppFocusTraversalPolicy focusPolicy;

    /**
     * Creates new form BestLoginDialog
     */
    public WesleyLoginDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        runAlready(parent);
        initComponents();

        try {
            dao.open();
            initCombo();
            txtLoginDate.setText(DateUtil.getTodayDateStr("dd/MM/yyyy"));
            dao.close();
        } catch (Exception ex) {
            log.error("BestLoginDialog : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        Global.dao = dao;
    }

    private void runAlready(java.awt.Frame parent) {
        try {
            Global.sock = new ServerSocket(10000);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "You cannot run two program at the same time in the same machine.",
                    "Duplicate Program running.", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    public boolean isStatus() {
        return status;
    }

    private void initCombo() {
        try {
            dao.open();
            List<Session> listSession = dao.findAll("Session");
            BindingUtil.BindCombo(cboSession, listSession);
            BindingUtil.BindCombo(cboLocation, dao.findAll("Location"));
            /*for (int i = 0; i < listSession.size() - 1; i++) {
                Session sess = listSession.get(i);
                if (DateUtil.isValidSession(sess.getStartTime(), sess.getEndTime())) {
                    cboSession.setSelectedItem(sess);
                    i = listSession.size();
                }
            }*/
            dao.close();
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        //ComBoBoxAutoComplete comBoBoxAutoComplete = new ComBoBoxAutoComplete(cboSession);
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtLoginName);
        focusOrder.add(txtPassword);
        focusOrder.add(butLogin);
        focusOrder.add(butClear);
        focusOrder.add(cboSession);

        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

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
    }

    public void setFocus() {
        txtLoginName.requestFocusInWindow();
    }

    private void initPrivilege(int roleId) {
        String sql = "select m.menu_name, m.menu_class, p.allow from user_role ur, "
         + "role_privilege rp, privilege p, menu m where ur.role_id = rp.role_id and "
         + "rp.privilege_id = p.privilege_id and p.menu_id = m.menu_id "
         + "and ur.role_id = " + roleId 
         + " and m.menu_id in (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, "
         + "13, 14, 15, 16, 17, 18, 19, 21, 23, 24, 26, 27, 37, 38, 39, 40, 41, 49,"
         + "80, 81, 82, 86, 87, 88, 96, 109, 141, 206, 178, 179)";

        try {
            dao.open();
            ResultSet resultSet = dao.execSQL(sql);
            HashMap<String, Boolean> hashPrivilege = new HashMap();

            while (resultSet.next()) {
                hashPrivilege.put(resultSet.getString("menu_class"), resultSet.getBoolean("allow"));
                /*if(resultSet.getString("menu_class").equals("OPDBillPaymentView")){
                    System.out.println("Have");
                }*/
            }

            Global.hashPrivilege = hashPrivilege;
            //Util1.loadSystemProperties();
            List<SysProperty> listProp = dao.findAll("SysProperty");
            Util1.loadSystemProperties(listProp);
        } catch (Exception ex) {
            log.error("initPrivilege : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void initDefaultValue(UserRole userRole) {
        HashMap<String, Object> defaultValue = new HashMap();

        defaultValue.put("Currency", userRole.getCurrency());
        defaultValue.put("PaymentType", userRole.getPaymentType());
        defaultValue.put("Trader", userRole.getTrader());
        defaultValue.put("VouStatus", userRole.getVouStatus());
        defaultValue.put("VouPrinter", userRole.isVouPrinter());

        Global.defaultValue = defaultValue;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtLoginName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        butLogin = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cboSession = new javax.swing.JComboBox();
        txtPassword = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtLoginDate = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel1.setText("Login Name ");

        txtLoginName.setFont(new java.awt.Font("Zawgyi-One", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel2.setText("Password");

        butClear.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butLogin.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        butLogin.setText("Login");
        butLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLoginActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel3.setText("Session");

        cboSession.setFont(new java.awt.Font("Zawgyi-One", 0, 14)); // NOI18N
        cboSession.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtPassword.setFont(new java.awt.Font("Zawgyi-One", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel4.setText("Login Date");

        txtLoginDate.setFont(new java.awt.Font("Zawgyi-One", 0, 16)); // NOI18N
        txtLoginDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtLoginDateMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel5.setText("Location");

        cboLocation.setFont(new java.awt.Font("Zawgyi-One", 0, 14)); // NOI18N
        cboLocation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 116, Short.MAX_VALUE)
                        .addComponent(butLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLoginName)
                            .addComponent(cboSession, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPassword)
                            .addComponent(txtLoginDate)
                            .addComponent(cboLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butLogin});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtLoginName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtLoginDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butClear)
                    .addComponent(butLogin))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLoginActionPerformed
        status = false;

        if (txtLoginName.getText() == null || txtLoginName.getText().equals("")
                || txtPassword.getText() == null || txtPassword.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                    "Authentication error.", JOptionPane.ERROR_MESSAGE);
            loginCount++;
        } else {
            List<Appuser> listAppUser = null;

            try {
                dao.open();
                listAppUser = dao.findAllHSQL("from Appuser usr where usr.userShortName = '"
                        + txtLoginName.getText() + "' and password = '" + txtPassword.getText() + "'");
                dao.close();
            } catch (Exception ex) {
                log.error("butLoginActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }

            if (listAppUser == null || listAppUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                        "Authentication error.", JOptionPane.ERROR_MESSAGE);
                loginCount++;
            } else {
                Global.loginUser = listAppUser.get(0);
                Global.sessionId = ((Session) cboSession.getSelectedItem()).getSessionId();
                Global.sessionName = ((Session) cboSession.getSelectedItem()).getSessionName();
                Global.loginLocation = (Location) cboLocation.getSelectedItem();
                Global.loginDate = txtLoginDate.getText();
                status = true;
                initPrivilege(Global.loginUser.getUserRole().getRoleId());
                initDefaultValue(Global.loginUser.getUserRole());
                Global.defaultValue.put("Location", Global.loginUser.getDefLocation());
                Global.lableFont = new java.awt.Font("Zawgyi-One", 1, 13);
                Global.textFont = new java.awt.Font("Zawgyi-One", 0, 13);
                Global.dateFormat = "dd/MM/yyyy";
                this.dispose();
            }
        }

        if (loginCount >= 3) {
            this.dispose();
        }
    }//GEN-LAST:event_butLoginActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        txtLoginName.setText("");
        txtPassword.setText("");
        setFocus();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtLoginDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtLoginDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();
            if (strDate != null) {
                txtLoginDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtLoginDateMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WesleyLoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WesleyLoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WesleyLoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WesleyLoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WesleyLoginDialog dialog = new WesleyLoginDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton butLogin;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JFormattedTextField txtLoginDate;
    private javax.swing.JTextField txtLoginName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
