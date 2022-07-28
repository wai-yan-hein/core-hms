/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.ui.common.MedListTableModel;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author WSwe
 */
public class MedListDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(MedListDialog.class.getName());
    private java.util.List<Medicine> listMedicine
            = ObservableCollections.observableList(new java.util.ArrayList<Medicine>());
    private AbstractDataAccess dao;
    private Medicine selMed = null;
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;
    private MedListTableModel tableModel = new MedListTableModel();
    private boolean bindStatus = false;

    /**
     * Creates new form MedListDialog
     */
    public MedListDialog(AbstractDataAccess dao, String filter) {
        super(Util1.getParent(), true);
        this.dao = dao;

        initComponents();
        txtFilter.setText(filter);

        try {
            dao.open();
            initTable();
            dao.close();
            actionMapping();
            sorter = new TableRowSorter(tblMedicine.getModel());
            tblMedicine.setRowSorter(sorter);
            //initCombo();

            Dimension screen = Util1.getScreenSize();
            int x = (screen.width - this.getWidth()) / 2;
            int y = (screen.height - this.getHeight()) / 2;

            setLocation(x, y);
            setVisible(true);
            //show();
        } catch (Exception ex) {
            log.error("medListDialog : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        //txtFilter.requestDefaultFocus();
    }

    private void initTable() {
        try {
            listMedicine = dao.findAll("Medicine", "active = true");
            tableModel.setListMedicine(listMedicine);

            tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(200);
            tblMedicine.getColumnModel().getColumn(2).setPreferredWidth(50);
            tblMedicine.getColumnModel().getColumn(3).setPreferredWidth(80);
            tblMedicine.getColumnModel().getColumn(4).setPreferredWidth(50);

            tblMedicine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblMedicine.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selectedRow = tblMedicine.getSelectedRow();
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public Medicine getSelect() {
        return selMed;
    }

    private void close() {
        if (selectedRow >= 0) {
            selMed = listMedicine.get(tblMedicine.convertRowIndexToModel(selectedRow));
        }

        dispose();
    }

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    private void actionMapping() {
        //Enter event on tblSale
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblMedicine.getActionMap().put("ENTER-Action", actionTblMedicineEnterKey);
    }// </editor-fold>
    private Action actionTblMedicineEnterKey = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            close();
        }
    };
    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {

        @Override
        public boolean include(Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase();
            String tmp2 = entry.getStringValue(1).toUpperCase();
            String text = txtFilter.getText().toUpperCase();

            if (tmp1.startsWith(text) || tmp2.startsWith(text)) {
                return true;
            } else {
                return false;
            }
        }
    };

    /*
     * private void initCombo() { bindStatus = false;
     * BindingUtil.BindComboFilter(cboItemType, dao.findAll("ItemType"));
     * BindingUtil.BindComboFilter(cboCategory, dao.findAll("Category"));
     * BindingUtil.BindComboFilter(cboBrand, dao.findAll("ItemBrand"));
     *
     * new ComBoBoxAutoComplete(cboItemType); new
     * ComBoBoxAutoComplete(cboCategory); new ComBoBoxAutoComplete(cboBrand);
     * bindStatus = true;
     }
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Item List");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                focus(evt);
            }
        });

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.setFocusCycleRoot(true);
        txtFilter.setPreferredSize(new java.awt.Dimension(100, 24));
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblMedicine.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblMedicine.setModel(tableModel);
        tblMedicine.setRowHeight(23);
        tblMedicine.setShowVerticalLines(false);
        tblMedicine.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMedicineMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMedicine);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblMedicineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMedicineMouseClicked
        if (evt.getClickCount() == 2) {
            close();
        }
    }//GEN-LAST:event_tblMedicineMouseClicked

    private void focus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_focus
        tblMedicine.requestFocusInWindow();
    }//GEN-LAST:event_focus

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
      if (txtFilter.getText().isEmpty()) {
          sorter.setRowFilter(null);
      } else {
          sorter.setRowFilter(startsWithFilter);
          //sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
      }
  }//GEN-LAST:event_txtFilterKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
