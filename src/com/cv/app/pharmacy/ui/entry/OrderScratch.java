/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.helper.OrderScratchData;
import com.cv.app.pharmacy.ui.common.OrderScratchTableModel;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author ACER
 */
public class OrderScratch extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(OrderScratch.class.getName());
    private final AbstractDataAccess dao = Global.dao; //Use for database access
    private final OrderScratchTableModel tableMode = new OrderScratchTableModel();
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form OrderScratch
     */
    public OrderScratch() {
        initComponents();
        initTable();
        sorter = new TableRowSorter(tblOrder.getModel());
        tblOrder.setRowSorter(sorter);
        
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }

    private void initTable() {
        List<OrderScratchData> listOSD = getOrderScratchData();
        tableMode.setList(listOSD);
        tblOrder.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(10);//Item ID
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(150);//Item Name
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(5);//Active
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(100);//Category
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(150);//System
        tblOrder.getColumnModel().getColumn(5).setPreferredWidth(100);//Brand Name
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(10);//Sup Code
        tblOrder.getColumnModel().getColumn(7).setPreferredWidth(150);//upplier Name
        tblOrder.getColumnModel().getColumn(8).setPreferredWidth(100);//Phone
    }

    private List<OrderScratchData> getOrderScratchData() {
        List<OrderScratchData> listOSD = new ArrayList();
        try {
            String strSql = "select distinct med.med_id, med.med_name, med.active, "
                    + "med.category_id, cat.cat_name, med.phar_sys_id, ps.system_desp, \n"
                    + "med.brand_id, ib.brand_name, vp.cus_id, t.trader_name, t.phone\n"
                    + "from medicine med \n"
                    + "left join category cat on med.category_id = cat.cat_id\n"
                    + "left join phar_system ps on med.phar_sys_id = ps.id\n"
                    + "left join item_brand ib on med.brand_id = ib.brand_id\n"
                    + "left join v_purchase vp on med.med_id = vp.med_id\n"
                    + "left join trader t on vp.cus_id = t.trader_id\n"
                    + "where ifnull(vp.deleted,false) = false \n";

            String status = cboStatus.getSelectedItem().toString();
            if (status.equals("Active")) {
                strSql = strSql + " and med.active = true";
            } else {
                strSql = strSql + " and med.active = false";
            }
            strSql = strSql + " order by med.med_name, t.trader_name";

            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    OrderScratchData osd = new OrderScratchData(
                            rs.getString("med_id"),
                            rs.getString("med_name"),
                            rs.getBoolean("active"),
                            rs.getInt("category_id"),
                            rs.getString("cat_name"),
                            rs.getInt("phar_sys_id"),
                            rs.getString("system_desp"),
                            rs.getString("brand_name"),
                            rs.getString("cus_id"),
                            rs.getString("trader_name"),
                            rs.getString("phone")
                    );
                    listOSD.add(osd);
                }

                rs.close();
            }
        } catch (Exception ex) {
            log.error("getOrderScratchData : " + ex.toString());
        } finally {
            dao.close();
        }
        return listOSD;
    }

    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {

        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase();//Item Code
            String tmp2 = entry.getStringValue(1).toUpperCase();//Item Name
            String tmp3 = entry.getStringValue(3).toUpperCase();//Category
            String tmp4 = entry.getStringValue(4).toUpperCase();//System
            String tmp5 = entry.getStringValue(5).toUpperCase();//Brand
            String tmp6 = entry.getStringValue(7).toUpperCase();//Supplier
            String tmp7 = entry.getStringValue(8).toUpperCase();//Phone
            
            String text1 = txtItemId.getText().toUpperCase();
            String text2 = txtItemName.getText().toUpperCase();
            String text3 = txtCategory.getText().toUpperCase();
            String text4 = txtSystem.getText().toUpperCase();
            String text5 = txtBrand.getText().toUpperCase();
            String text6 = txtSupplier.getText().toUpperCase();
            String text7 = txtPhone.getText().toUpperCase();
            
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
                if (!tmp4.contains(text4)) {
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
            
            if (text7.trim().length() > 0 && status) {
                if (!tmp7.startsWith(text7)) {
                    status = false;
                }
            }
            
            return status;
        }
    };
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtItemId = new javax.swing.JTextField();
        txtItemName = new javax.swing.JTextField();
        txtCategory = new javax.swing.JTextField();
        txtSystem = new javax.swing.JTextField();
        txtBrand = new javax.swing.JTextField();
        txtSupplier = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        cboStatus = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();

        txtItemId.setBorder(javax.swing.BorderFactory.createTitledBorder("Item ID"));
        txtItemId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemIdKeyReleased(evt);
            }
        });

        txtItemName.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Name"));
        txtItemName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemNameKeyReleased(evt);
            }
        });

        txtCategory.setBorder(javax.swing.BorderFactory.createTitledBorder("Category"));
        txtCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCategoryKeyReleased(evt);
            }
        });

        txtSystem.setBorder(javax.swing.BorderFactory.createTitledBorder("System"));
        txtSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSystemKeyReleased(evt);
            }
        });

        txtBrand.setBorder(javax.swing.BorderFactory.createTitledBorder("Brand"));
        txtBrand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBrandKeyReleased(evt);
            }
        });

        txtSupplier.setBorder(javax.swing.BorderFactory.createTitledBorder("Supplier"));
        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });

        txtPhone.setBorder(javax.swing.BorderFactory.createTitledBorder("Phone"));
        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPhoneKeyReleased(evt);
            }
        });

        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "In Active" }));
        cboStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));
        cboStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtItemId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtItemName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSystem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBrand)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSupplier)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtItemId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboStatus, txtBrand, txtCategory, txtItemId, txtItemName, txtPhone, txtSupplier, txtSystem});

        tblOrder.setFont(Global.textFont);
        tblOrder.setModel(tableMode);
        tblOrder.setRowHeight(23);
        jScrollPane1.setViewportView(tblOrder);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtItemIdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemIdKeyReleased
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtItemIdKeyReleased

    private void txtItemNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemNameKeyReleased
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtItemNameKeyReleased

    private void txtCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyReleased
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtCategoryKeyReleased

    private void txtSystemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSystemKeyReleased
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtSystemKeyReleased

    private void txtBrandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBrandKeyReleased
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtBrandKeyReleased

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtSupplierKeyReleased

    private void txtPhoneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyReleased
        if (txtItemId.getText().isEmpty() && txtItemName.getText().isEmpty()
                && txtCategory.getText().isEmpty() && txtSystem.getText().isEmpty()
                && txtBrand.getText().isEmpty() && txtSupplier.getText().isEmpty() 
                && txtPhone.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtPhoneKeyReleased

    private void cboStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboStatusActionPerformed
        List<OrderScratchData> listOSD = getOrderScratchData();
        tableMode.setList(listOSD);
    }//GEN-LAST:event_cboStatusActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        List<OrderScratchData> listOSD = getOrderScratchData();
        tableMode.setList(listOSD);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtItemId;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtSystem;
    // End of variables declaration//GEN-END:variables
}
