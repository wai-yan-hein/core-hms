/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.BuildingStructure;
import com.cv.app.inpatient.database.entity.RBooking;
import com.cv.app.inpatient.ui.util.RoomBookingSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class RoomBooking extends javax.swing.JPanel implements FormAction,
        KeyPropagate, SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(RoomBooking.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private RBooking currBooking = new RBooking();
    //private AvailableDoctorTableModel avaDTableModel = new AvailableDoctorTableModel();
    private BestAppFocusTraversalPolicy focusPolicy;
    private String focusCtrlName = "-";

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (!focusCtrlName.equals("-")) {
                if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtName")) {
                    txtRemark.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtRemark")) {
                    txtContactNo.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtContactNo")) {
                    cboRoom.requestFocus();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void initForFocus() {
        txtContactNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtName.addKeyListener(this);
    }

    /**
     * Creates new form PatientSetup
     */
    public RoomBooking() {
        initComponents();
        initCombo();
        assignDefaultValue();
        initTable();
        actionMapping();
        //applyFocusPolicy();
        //AddFocusMoveKey();
        //this.setFocusTraversalPolicy(focusPolicy);
        initForFocus();
    }

    @Override
    public void save() {
        if (isValidEntry()) {
            try {
                dao.save(currBooking);
                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void newForm() {
        lblStatus.setText("NEW");
        txtBookingDateTime.setText(null);
        txtName.setText(null);
        txtRemark.setText(null);
        txtContactNo.setText(null);
        assignDefaultValue();
        txtName.requestFocusInWindow();
    }

    @Override
    public void history() {
        RoomBookingSearch ps = new RoomBookingSearch(dao, this);
    }

    @Override
    public void delete() {
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            delete();
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            deleteCopy();
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
            save();
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
            print();
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
            history();
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
            newForm();
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboRoom,
                    dao.findAllHSQL("select o from BuildingStructure o where o.regNo is null and o.structureType.typeId in (3,4) order by o.description"));
            new ComBoBoxAutoComplete(cboRoom);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("BookingSearch")) {
            lblStatus.setText("EDIT");
            currBooking = (RBooking) selectObj;

            txtBookingDateTime.setText(DateUtil.toDateTimeStr(currBooking.getBookingDate(),
                    "dd/MM/yyyy HH:mm:ss"));
            txtName.setText(currBooking.getBookingName());
            txtRemark.setText(currBooking.getBookingRemark());
            txtContactNo.setText(currBooking.getBookingContact());
            chkActive.setSelected(currBooking.isCheckStatus());
            if (currBooking.getBuildingStructure() != null) {
                cboRoom.setSelectedItem(currBooking.getBuildingStructure());
            } else {
                cboRoom.setSelectedItem(null);
            }

            dao.close();
        }
    }

    private void assignDefaultValue() {
        txtBookingDateTime.setText(DateUtil.getTodayDateStr("dd/MM/yyyy HH:mm:ss"));
        cboRoom.setSelectedItem(null);
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (Util1.nullToBlankStr(txtName.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Please key in name.", "Booking Name",
                    JOptionPane.ERROR_MESSAGE);
            txtName.requestFocusInWindow();
        } else if (status) {
            currBooking.setBookingDate(DateUtil.toDateTime(txtBookingDateTime.getText()));
            currBooking.setBookingContact(txtContactNo.getText().trim());
            currBooking.setBookingRemark(txtRemark.getText().trim());
            currBooking.setBookingName(txtName.getText().trim());
            currBooking.setBuildingStructure((BuildingStructure) cboRoom.getSelectedItem());
            currBooking.setCheckStatus(chkActive.isSelected());
        }
        return status;
    }

    private void initTable() {

    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtName);
        focusOrder.add(txtRemark);
        focusOrder.add(txtContactNo);
        focusOrder.add(cboRoom);

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

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Print
        /*jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
        jc.getActionMap().put("F7-Action", actionPrint);*/
        //History
        jc.getInputMap().put(KeyStroke.getKeyStroke("F9"), "F9-Action");
        jc.getActionMap().put("F9-Action", actionHistory);

        //Clear
        jc.getInputMap().put(KeyStroke.getKeyStroke("F10"), "F10-Action");
        jc.getActionMap().put("F10-Action", actionNewForm);

        //Delete
        /*KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
        jc.getActionMap().put("Ctrl-F8-Action", actionDelete);*/
        //Delete Copy
        /*keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.SHIFT_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Shift-F8-Action");
        jc.getActionMap().put("Shift-F8-Action", actionDeleteCopy);*/
    }

    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };

    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };

    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    private void actionMapping() {
        //F3 event on tblSale
        /*tblSale.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblSale.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblSale.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblSale.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblSale
        tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);

        //Enter event on tblExpense
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblExpense.getActionMap().put("ENTER-Action", actionTblExpEnterKey);

        //F8 event on tblExpense
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblExpense.getActionMap().put("F8-Action", actionItemDeleteExp);

        //F8 event on tblTransaction
        tblTransaction.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblTransaction.getActionMap().put("F8-Action", actionItemDeleteTran);*/

        formActionKeyMapping(txtName);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtContactNo);
        formActionKeyMapping(cboRoom);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel16 = new javax.swing.JLabel();
        txtBookingDateTime = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtContactNo = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cboRoom = new javax.swing.JComboBox();
        chkActive = new javax.swing.JCheckBox();

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Booking Date");

        txtBookingDateTime.setEditable(false);
        txtBookingDateTime.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Remark");

        txtRemark.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Contact No");

        txtContactNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtContactNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtContactNoFocusGained(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Room No.");

        cboRoom.setEditable(true);
        cboRoom.setFont(Global.textFont);
        cboRoom.setEnabled(true);
        cboRoom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboRoomFocusGained(evt);
            }
        });

        chkActive.setText("Active");
        chkActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActiveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtBookingDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkActive))
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addComponent(cboRoom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtContactNo))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtBookingDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus)
                    .addComponent(chkActive))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jLabel19))
                .addGap(19, 19, 19))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        focusCtrlName = "txtName";
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        focusCtrlName = "txtFatherName";
        txtRemark.selectAll();
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtContactNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactNoFocusGained
        focusCtrlName = "txtContactNo";
        txtContactNo.selectAll();
    }//GEN-LAST:event_txtContactNoFocusGained

    private void cboRoomFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboRoomFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_cboRoomFocusGained

    private void chkActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActiveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkActiveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboRoom;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JFormattedTextField txtBookingDateTime;
    private javax.swing.JTextField txtContactNo;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtRemark;
    // End of variables declaration//GEN-END:variables
}
