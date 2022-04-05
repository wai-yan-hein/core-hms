/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CharacterNoSetup.java
 *
 * Created on May 10, 2012, 3:31:50 PM
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.common.NumberKeyListener;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CharacterNo;
import com.cv.app.pharmacy.ui.common.CharNoTableModel;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author winswe
 */
public class CharacterNoSetup extends javax.swing.JPanel {
    static Logger log = Logger.getLogger(CharacterNoSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private CharNoTableModel charNoTableModel = new CharNoTableModel();
    private CharacterNo currCharacterNo = new CharacterNo();
    private BestAppFocusTraversalPolicy focusPolicy;
    private int selectedRow = -1;

    /**
     * Creates new form CharacterNoSetup
     */
    public CharacterNoSetup() {
        initComponents();
        try {
            dao.open();
            initTable();
            dao.close();
            txtNo.addKeyListener(new NumberKeyListener());
        } catch (Exception ex) {
            log.error("CharacterNoSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        //((AbstractDocument)txtNo.getDocument()).setDocumentFilter(new NumberDocFilter());
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    public void setCurrCharacterNo(CharacterNo currCharacterNo) {
        this.currCharacterNo = currCharacterNo;
        txtCharacter.setText(currCharacterNo.getCh());
        txtNo.setText(currCharacterNo.getStrNumber());
        lblStatus.setText("EDIT");
        txtCharacter.setEnabled(false);
    }

    private void clear() {
        selectedRow = -1;
        lblStatus.setText("NEW");
        tblCharacterNo.clearSelection();
        currCharacterNo = new CharacterNo();
        txtCharacter.setText(null);
        txtNo.setText(null);
        txtCharacter.setEnabled(true);
        setFocus();
    }

    public void setFocus() {
        txtCharacter.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector<Component>(7);

        //focusOrder.add(txtCusId);
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

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtCharacter.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Character cannot be blank or null.",
                    "Character null error.", JOptionPane.ERROR_MESSAGE);
            txtCharacter.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtCharacter.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "No cannot be blank or null.",
                    "No null error.", JOptionPane.ERROR_MESSAGE);
            txtNo.requestFocusInWindow();
        } else if(txtCharacter.getText().length() > 2){
            status = false;
            JOptionPane.showMessageDialog(this, "Character length at most 2.",
                    "Character length", JOptionPane.ERROR_MESSAGE);
            txtCharacter.requestFocusInWindow();
        } else if(txtNo.getText().length() > 3){
            status = false;
            JOptionPane.showMessageDialog(this, "Character length at most 3.",
                    "Character length", JOptionPane.ERROR_MESSAGE);
            txtNo.requestFocusInWindow();
        } else {
            currCharacterNo.setCh(txtCharacter.getText().trim());
            currCharacterNo.setStrNumber(txtNo.getText().trim());
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Character No Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currCharacterNo);
                    int tmpRow = selectedRow;
                    charNoTableModel.deleteCharNo(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this character no.",
                        "Character No Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
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
        tblCharacterNo = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCharacter = new javax.swing.JTextField();
        txtNo = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        tblCharacterNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblCharacterNo.setModel(charNoTableModel);
        tblCharacterNo.setRowHeight(23);
        tblCharacterNo.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblCharacterNo);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Character");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("No");

        txtCharacter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNo)
                            .addComponent(txtCharacter)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 34, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtCharacter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addGap(10, 10, 10)
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                dao.save(currCharacterNo);
                if (lblStatus.getText().equals("NEW")) {
                    charNoTableModel.addCharNo(currCharacterNo);
                } else {
                    charNoTableModel.setCharNo(selectedRow, currCharacterNo);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Character No Save", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

    //Inatilize tblCharacterNo
    private void initTable() {
        //Get item type from database
        charNoTableModel.setListCharacterNo(dao.findAll("CharacterNo"));

        //Binding tblItemType with listItemType using beansbinding library.
        /*JTableBinding jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
         listCharacterNo, tblCharacterNo);
         ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ch}"));

         columnBinding.setColumnName("Char");
         columnBinding.setColumnClass(String.class);
         columnBinding.setEditable(false);

         columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${strNumber}"));
         columnBinding.setColumnName("No");
         columnBinding.setColumnClass(String.class);
         columnBinding.setEditable(false);

         jTableBinding.bind();*/

        //Define table selection model to single row selection.
        tblCharacterNo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblCharacterNo.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (tblCharacterNo.getSelectedRow() >= 0) {
                            selectedRow = tblCharacterNo.convertRowIndexToModel(
                                    tblCharacterNo.getSelectedRow());
                        }
                        if (selectedRow >= 0) {
                            setCurrCharacterNo(charNoTableModel.getCharNo(selectedRow));
                        }
                    }
                });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCharacterNo;
    private javax.swing.JTextField txtCharacter;
    private javax.swing.JTextField txtNo;
    // End of variables declaration//GEN-END:variables
}
