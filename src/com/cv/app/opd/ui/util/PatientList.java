/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * CustomerList.java
 *
 * Created on Jun 5, 2012, 10:05:42 PM
 */
package com.cv.app.opd.ui.util;

import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.common.PatientListTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PatientList extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(PatientList.class.getName());
    private Object parent;
    private SelectionObserver observer;
    private AbstractDataAccess dao;
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;
    private PatientListTableModel tableModel = new PatientListTableModel();
    private String title;
    private StartWithRowFilter filter;

    /**
     * Creates new form CustomerList
     */
    public PatientList() {
        initComponents();

        try {
            dao.open();
            initTable();
            dao.close();
            sorter = new TableRowSorter(tblCustomer.getModel());
            tblCustomer.setRowSorter(sorter);
        } catch (Exception ex) {
            log.error("PatientList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
    }

    public PatientList(Object parent, SelectionObserver observer, AbstractDataAccess dao,
            String title) {
        initComponents();

        this.parent = parent;
        this.observer = observer;
        this.dao = dao;
        this.title = title;

        filter = new StartWithRowFilter(txtFilter);

        try {
            dao.open();
            initTable();
            dao.close();
            sorter = new TableRowSorter(tblCustomer.getModel());
            tblCustomer.setRowSorter(sorter);
            txtFilter.registerKeyboardAction(upArrowAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                    JComponent.WHEN_FOCUSED);
            txtFilter.registerKeyboardAction(downArrowAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                    JComponent.WHEN_FOCUSED);
            txtFilter.registerKeyboardAction(escapeAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_FOCUSED);
            txtFilter.registerKeyboardAction(enterAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                    JComponent.WHEN_FOCUSED);

            if (tableModel.getListPatient().size() > 0) {
                tblCustomer.setRowSelectionInterval(0, 0);
            }
        } catch (Exception ex) {
            log.error("PatientList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        if (!Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (title.contains("Supplier")) {
                chkOption.setText("Customer");
            } else {
                chkOption.setText("Supplier");
            }
        } else {
            chkOption.setVisible(false);
        }

        actionMapping();
    }

    private void initTable() {
        getData();

        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(120);
        //tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(80);

        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblCustomer.getSelectedRow();
            }
        });
    }

    private void getData() {
        try {
            List<Patient> listPatient = null;

            /* if (title.contains("Supplier") && !chkOption.isSelected()) {
            listTrader = dao.findAll("Supplier", "Active = true");
        } else if (title.contains("Customer") && !chkOption.isSelected()) {
            listTrader = dao.findAll("Customer", "Active = true");
        } else {*/
            listPatient = dao.findAll("Patient");

            tableModel.setListPatient(listPatient);
        } catch (Exception ex) {
            log.error("getData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void actionMapping() {
        //Enter event on tblSale
        tblCustomer.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblCustomer.getActionMap().put("ENTER-Action", actionTblCustomerEnterKey);
    }
    private Action actionTblCustomerEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblCustomer.getCellEditor() != null) {
                    tblCustomer.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            selectTrader();
        }
    };

    private void selectTrader() {
        if (selectedRow >= 0) {
            observer.selected("PatientList",
                    tableModel.getPatinet(tblCustomer.convertRowIndexToModel(selectedRow)));

            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select patient.",
                    "No Selection.", JOptionPane.ERROR_MESSAGE);
        }
    }
    private Action upArrowAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int si = tblCustomer.getSelectedRow();

            if (si > 0) {
                tblCustomer.setRowSelectionInterval(si - 1, si - 1);
            }

            Rectangle rect = tblCustomer.getCellRect(tblCustomer.getSelectedRow(), 0, true);
            tblCustomer.scrollRectToVisible(rect);
        }
    };
    private Action downArrowAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int si = tblCustomer.getSelectedRow();
            if (si < tableModel.getListPatient().size() - 1) {
                tblCustomer.setRowSelectionInterval(si + 1, si + 1);
            }

            Rectangle rect = tblCustomer.getCellRect(tblCustomer.getSelectedRow(), 0, true);
            tblCustomer.scrollRectToVisible(rect);
        }
    };
    private Action escapeAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        }
    };
    private Action enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectTrader();
        }
    };

    private void filterItem(String strFilter) {
        try {
            tableModel.setListPatient(dao.findAll("Patient"));
        } catch (Exception ex) {
            log.error("filterItem : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //statusFilter = true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFilter = new javax.swing.JTextField();
        butFilter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        chkOption = new javax.swing.JCheckBox();

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        butFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butFilter.setText("...");
        butFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFilterActionPerformed(evt);
            }
        });

        tblCustomer.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblCustomer.setModel(tableModel);
        tblCustomer.setRowHeight(23);
        tblCustomer.setShowVerticalLines(false);
        tblCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCustomer);

        chkOption.setText("Customer");
        chkOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOptionActionPerformed(evt);
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
                        .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkOption)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(1, 1, 1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butFilter)
                    .addComponent(chkOption))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            selectTrader();
        }
    }//GEN-LAST:event_tblCustomerMouseClicked

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
      if (txtFilter.getText().isEmpty()) {
          sorter.setRowFilter(null);
      } else {
          /*String option = Util1.getPropValue("system.trader.filter");
          
          if(option.equals("S")){
              sorter.setRowFilter(filter);
          }else{*/
          sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
          //}

      }
  }//GEN-LAST:event_txtFilterKeyReleased

    private void chkOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOptionActionPerformed
        getData();
    }//GEN-LAST:event_chkOptionActionPerformed

    private void butFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFilterActionPerformed
        /*CustomerSetupFilterDialog filterDialog = new CustomerSetupFilterDialog(dao);

        if (filterDialog.getStatus()) {
            filterItem(filterDialog.getFilter());
        }*/
    }//GEN-LAST:event_butFilterActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butFilter;
    private javax.swing.JCheckBox chkOption;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
