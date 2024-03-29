/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.view.VItemLocationMapping;
import com.cv.app.pharmacy.ui.common.ItemLocationMappingTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class LocationItemMappingSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(MachinePropSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final ItemLocationMappingTableModel mappingTableModel = new ItemLocationMappingTableModel();
    private boolean comboStatus = false;
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form LocationItemMappingSetup
     */
    public LocationItemMappingSetup() {
        initComponents();
        try {
            dao.open();
            initCombo();
            initItemLocationTable();
            sorter = new TableRowSorter(tblMapping.getModel());
            tblMapping.setRowSorter(sorter);
            dao.close();
        } catch (Exception ex) {
            log.error("ItemSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboLocation, dao.findAll("Location"));
            BindingUtil.BindComboFilter(cboItemType, dao.findAll("ItemType"));
            BindingUtil.BindComboFilter(cboCategory, dao.findAll("Category"));
            BindingUtil.BindComboFilter(cboBrand, dao.findAll("ItemBrand"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }

        comboStatus = true;
    }

    private void initItemLocationTable() {
        //Adjust table column width
        tblMapping.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblMapping.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMapping.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblMapping.getColumnModel().getColumn(3).setPreferredWidth(40);
        tblMapping.getColumnModel().getColumn(4).setPreferredWidth(300);
        tblMapping.getColumnModel().getColumn(5).setPreferredWidth(1);
    }
    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String str1 = Util1.getNullTo(entry.getStringValue(0), "").toUpperCase();
            String str2 = Util1.getNullTo(entry.getStringValue(1), "").toUpperCase();
            String str3 = Util1.getNullTo(entry.getStringValue(2), "").toUpperCase();
            String str4 = Util1.getNullTo(entry.getStringValue(3), "").toUpperCase();
            String filterText = txtFilter.getText().toUpperCase();

            return str1.startsWith(filterText) || str2.startsWith(filterText) || str3.startsWith(filterText)
                    || str4.startsWith(filterText);
        }
    };

    private void getItemLocationMapping() {
        String strSql = "select o from VItemLocationMapping o where (o.key.locationId = "
                + ((Location) cboLocation.getSelectedItem()).getLocationId() + " "
                + "or o.key.locationId = -1)";

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            String itemTypeId = ((ItemType) cboItemType.getSelectedItem()).getItemTypeCode();
            strSql = strSql + " and o.itemTypeId = '" + itemTypeId + "'";
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            int categoryId = ((Category) cboCategory.getSelectedItem()).getCatId();
            strSql = strSql + " and o.categoryId = " + categoryId;
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            int brandId = ((ItemBrand) cboBrand.getSelectedItem()).getBrandId();
            strSql = strSql + " and o.brandId = " + brandId;
        }

        try {
            List<VItemLocationMapping> tmpListVILM = dao.findAllHSQL(strSql);
            mappingTableModel.setlistVILM(tmpListVILM);
        } catch (Exception ex) {
            log.error("getItemLocationMapping : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMapping = new javax.swing.JTable();
        cboItemType = new javax.swing.JComboBox<>();
        cboCategory = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        butSelectAll = new javax.swing.JButton();
        butUnSelectAll = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox<>();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Location ");

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Filter ");

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblMapping.setFont(Global.textFont);
        tblMapping.setModel(mappingTableModel);
        tblMapping.setRowHeight(23);
        jScrollPane1.setViewportView(tblMapping);

        cboItemType.setFont(Global.textFont);
        cboItemType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboItemTypeItemStateChanged(evt);
            }
        });
        cboItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboItemTypeActionPerformed(evt);
            }
        });

        cboCategory.setFont(Global.textFont);
        cboCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoryActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Item Type");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category");

        butSelectAll.setText("Select All");
        butSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelectAllActionPerformed(evt);
            }
        });

        butUnSelectAll.setText("Un Select All");
        butUnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUnSelectAllActionPerformed(evt);
            }
        });

        jLabel5.setText("Brand ");

        cboBrand.setFont(Global.textFont);
        cboBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBrandActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butUnSelectAll)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(butSelectAll)
                    .addComponent(butUnSelectAll)
                    .addComponent(jLabel5)
                    .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (comboStatus) {
            getItemLocationMapping();
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorter.setRowFilter(startsWithFilter);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void cboItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemTypeActionPerformed
        if (comboStatus) {
            getItemLocationMapping();
        }
    }//GEN-LAST:event_cboItemTypeActionPerformed

    private void cboCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoryActionPerformed
        if (comboStatus) {
            getItemLocationMapping();
        }
    }//GEN-LAST:event_cboCategoryActionPerformed

    private void cboItemTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboItemTypeItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboItemTypeItemStateChanged

    private void butSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelectAllActionPerformed
        mappingTableModel.selectAll();
    }//GEN-LAST:event_butSelectAllActionPerformed

    private void butUnSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUnSelectAllActionPerformed
        mappingTableModel.unSelectAll();
    }//GEN-LAST:event_butUnSelectAllActionPerformed

    private void cboBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBrandActionPerformed
        if (comboStatus) {
            getItemLocationMapping();
        }
    }//GEN-LAST:event_cboBrandActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSelectAll;
    private javax.swing.JButton butUnSelectAll;
    private javax.swing.JComboBox<String> cboBrand;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboItemType;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMapping;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables

}
