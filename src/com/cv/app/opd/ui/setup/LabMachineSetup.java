/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.LabMachine;
import com.cv.app.opd.ui.common.LabMachineTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
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
public class LabMachineSetup extends javax.swing.JDialog {
    static Logger log = Logger.getLogger(LabMachineSetup.class.getName());
    private final LabMachineTableModel tableModel = new LabMachineTableModel();
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private int selectRow = -1;
    private LabMachine currLabMachine = new LabMachine();
    private BestAppFocusTraversalPolicy focusPolicy;
    private TableRowSorter<TableModel> sorter;
    private TitledBorder tb = BorderFactory.createTitledBorder("NEW");
    
    /**
     * Creates new form CitySetup
     */
    public LabMachineSetup() {
        super(Util1.getParent(), true);
        initComponents();
        //applyFocusPolicy();
        //AddFocusMoveKey();
        //this.setFocusTraversalPolicy(focusPolicy);
        initTable();
        sorter = new TableRowSorter(tblLMName.getModel());
        tblLMName.setRowSorter(sorter);
        lblStatus.setText("NEW");
        lblStatus.setVisible(false);
        panelEntry.setBorder(tb);
        /*Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        setVisible(true);*/
        
        
    }

    private void initTable() {
        tableModel.setListCity(dao.findAll("LabMachine"));
        tblLMName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLMName.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblLMName.getSelectedRow() >= 0) {
                    selectRow = tblLMName.convertRowIndexToModel(tblLMName.getSelectedRow());
                }

                if (selectRow >= 0) {
                    setRecord(tableModel.getLabMachine(selectRow));
                }
            }
        });
    }

    private void setRecord(LabMachine labmachine) {
        currLabMachine = labmachine;
        txtLabMachineName.setText(labmachine.getlMachineName());
        lblStatus.setText("EDIT");
        tb.setTitle("EDIT");
        panelEntry.repaint();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        //focusOrder.add(txtCusId);
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

    private void clear() {
        lblStatus.setText("NEW");
        tb.setTitle("NEW");
        panelEntry.repaint();
        selectRow = -1;
        currLabMachine = new LabMachine();
        txtLabMachineName.setText(null);
        txtLabMachineName.requestFocusInWindow();
        System.gc();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtLabMachineName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Lab Machine name must not be blank.",
                    "Blank", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLabMachineName.requestFocusInWindow();
        } else {
            currLabMachine.setlMachineName(txtLabMachineName.getText());
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(currLabMachine);
                if(lblStatus.getText().equals("NEW")){
                    tableModel.addLabMachine(currLabMachine);
                }else{
                    tableModel.setLabMachine(selectRow, currLabMachine);
                }
                clear();
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Duplicate entry.",
                        "Lab Machine Name", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    private void delete(){
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), 
                        "Are you sure to delete " + txtLabMachineName.getText() + "?",
                        "Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currLabMachine);
                    int tmpRow = selectRow;
                    selectRow = -1;
                    tableModel.deleteLabMachine(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Can not delete this lab machine.",
                        "Delete", JOptionPane.ERROR_MESSAGE);
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
        tblLMName = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        panelEntry = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtLabMachineName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lab Machine Setup");

        tblLMName.setFont(Global.textFont);
        tblLMName.setModel(tableModel);
        tblLMName.setRowHeight(23);
        jScrollPane1.setViewportView(tblLMName);

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Name ");

        txtLabMachineName.setFont(Global.textFont);

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jButton1.setText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                .addGap(0, 30, Short.MAX_VALUE)
                .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butClear))
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLabMachineName))
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave, jButton1});

        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtLabMachineName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butClear)
                    .addComponent(jButton1)
                    .addComponent(butSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(9, 9, 9))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)))
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

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
    if (txtFilter.getText().length() == 0) {
          sorter.setRowFilter(null);
      } else {
          sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
      }
  }//GEN-LAST:event_txtFilterKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        delete();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JTable tblLMName;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtLabMachineName;
    // End of variables declaration//GEN-END:variables
}
