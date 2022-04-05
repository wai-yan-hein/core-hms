/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.common.MedListTableModel1;
import com.cv.app.util.Util1;
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
public class MedListDialog1 extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(MedListDialog1.class.getName());
    private java.util.List<VMedicine1> listMedicine
            = ObservableCollections.observableList(new java.util.ArrayList<VMedicine1>());
    private AbstractDataAccess dao;
    private VMedicine1 selMed = null;
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;
    private MedListTableModel1 tableModel = new MedListTableModel1();
    private boolean bindStatus = false;
    private int locationId;
    private String cusGroup;

    /**
     * Creates new form MedListDialog
     *
     * @param dao
     * @param filter
     * @param locationId
     * @param cusGroup
     */
    public MedListDialog1(AbstractDataAccess dao, String filter, int locationId,
            String cusGroup) {
        super(Util1.getParent(), true);
        this.dao = dao;
        this.locationId = locationId;
        this.cusGroup = cusGroup;

        initComponents();
        txtFilter.setText(filter);

        try {
            dao.open();
            initTable();
            dao.close();
            actionMapping();
            sorter = new TableRowSorter(tblMedicine.getModel());
            tblMedicine.setRowSorter(sorter);
        } catch (Exception ex) {
            log.error("medListDialog : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        if (txtFilter.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtItemType.getText().isEmpty() && txtItemBrand.getText().isEmpty()
                && txtCategory.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }

    private void initTable() {
        String strBrandFilter = Util1.getPropValue("system.pharmacy.bran.filter");
        String strShowAllGroup = Util1.getPropValue("system.pharmacy.no.filter.group");

        if (cusGroup != null && strBrandFilter.equals("Y")) {
            if (cusGroup.equals(strShowAllGroup)) {
                String strSql = "select o from VMedicine1 o where o.medId in (select a.key.itemId from VLocationItemMapping a"
                        + " where a.mapStatus = true and a.key.locationId = " + locationId + ")";
                listMedicine = dao.findAllHSQL(strSql);
            } else {
                String strSql = "select o from VMedicine1 o where o.cusGroup = '"
                        + cusGroup + "' and o.medId in (select a.key.itemId from VLocationItemMapping a"
                        + " where a.mapStatus = true and a.key.locationId = " + locationId + ")";
                listMedicine = dao.findAllHSQL(strSql);
            }
        } else {
            listMedicine = Global.listItem;
        }

        //listMedicine = dao.findAll("VMedicine1", "active = true");
        tableModel.setListMedicine(listMedicine);

        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMedicine.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblMedicine.getColumnModel().getColumn(3).setPreferredWidth(40);
        tblMedicine.getColumnModel().getColumn(4).setPreferredWidth(40);
        tblMedicine.getColumnModel().getColumn(5).setPreferredWidth(100);

        tblMedicine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMedicine.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblMedicine.getSelectedRow();
            }
        });
    }

    public VMedicine1 getSelect() {
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
            String tmp1 = entry.getStringValue(0).toUpperCase();//Item Code
            String tmp2 = entry.getStringValue(1).toUpperCase();//Item Name
            String tmp3 = entry.getStringValue(2).toUpperCase();//Item Type
            String tmp4 = entry.getStringValue(3).toUpperCase();//Brand
            String tmp5 = entry.getStringValue(4).toUpperCase();//Category
            String tmp6 = entry.getStringValue(5).toUpperCase();//System
            
            String text1 = txtFilter.getText().toUpperCase();
            String text2 = txtItemName.getText().toUpperCase();
            String text3 = txtItemType.getText().toUpperCase();
            String text4 = txtItemBrand.getText().toUpperCase();
            String text5 = txtCategory.getText().toUpperCase();
            String text6 = txtSystem.getText().toUpperCase();
            
            boolean status = true;

            if (text1.trim().length() > 0 && status) {
                if (!tmp1.startsWith(text1)) {
                    status = false;
                }
            }

            if (text2.trim().length() > 0 && status) {
                if (!tmp2.startsWith(text2)) {
                    status = false;
                }
            }

            if (text3.trim().length() > 0 && status) {
                if (!tmp3.startsWith(text3)) {
                    status = false;
                }
            }

            if (text4.trim().length() > 0 && status) {
                if (!tmp4.startsWith(text4)) {
                    status = false;
                }
            }

            if (text5.trim().length() > 0 && status) {
                if (!tmp5.startsWith(text5)) {
                    status = false;
                }
            }
            
            if (text6.trim().length() > 0 && status) {
                if (!tmp6.startsWith(text6)) {
                    status = false;
                }
            }
            
            return status;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable();
        txtItemName = new javax.swing.JTextField();
        txtItemType = new javax.swing.JTextField();
        txtItemBrand = new javax.swing.JTextField();
        txtCategory = new javax.swing.JTextField();
        txtFilter = new javax.swing.JTextField();
        txtSystem = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Item List");
        setPreferredSize(new java.awt.Dimension(1200, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                focus(evt);
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

        txtItemName.setFont(Global.textFont);
        txtItemName.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Item Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        txtItemName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemNameKeyReleased(evt);
            }
        });

        txtItemType.setFont(Global.textFont);
        txtItemType.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Item Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        txtItemType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemTypeKeyReleased(evt);
            }
        });

        txtItemBrand.setFont(Global.textFont);
        txtItemBrand.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Item Brand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        txtItemBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtItemBrandActionPerformed(evt);
            }
        });
        txtItemBrand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemBrandKeyReleased(evt);
            }
        });

        txtCategory.setFont(Global.textFont);
        txtCategory.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Category", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        txtCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCategoryKeyReleased(evt);
            }
        });

        txtFilter.setFont(Global.textFont);
        txtFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Item Code", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        txtSystem.setFont(Global.textFont);
        txtSystem.setBorder(javax.swing.BorderFactory.createTitledBorder("System"));
        txtSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSystemKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtItemName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtItemType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtItemBrand)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSystem)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtItemBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
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

    private void txtItemBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtItemBrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtItemBrandActionPerformed

    private void txtItemNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemNameKeyReleased
        if (txtFilter.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtItemType.getText().isEmpty() && txtItemBrand.getText().isEmpty()
                && txtCategory.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtItemNameKeyReleased

    private void txtItemTypeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemTypeKeyReleased
        if (txtFilter.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtItemType.getText().isEmpty() && txtItemBrand.getText().isEmpty()
                && txtCategory.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtItemTypeKeyReleased

    private void txtItemBrandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemBrandKeyReleased
        if (txtFilter.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtItemType.getText().isEmpty() && txtItemBrand.getText().isEmpty()
                && txtCategory.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtItemBrandKeyReleased

    private void txtCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyReleased
        if (txtFilter.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtItemType.getText().isEmpty() && txtItemBrand.getText().isEmpty()
                && txtCategory.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtCategoryKeyReleased

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtItemType.getText().isEmpty() && txtItemBrand.getText().isEmpty()
                && txtCategory.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtSystemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSystemKeyReleased
        if (txtFilter.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtItemType.getText().isEmpty() && txtItemBrand.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtSystemKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtItemBrand;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtItemType;
    private javax.swing.JTextField txtSystem;
    // End of variables declaration//GEN-END:variables
}
