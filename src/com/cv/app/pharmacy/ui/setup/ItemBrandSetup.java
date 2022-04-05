/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.ui.common.BrandTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
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
 * @author WSwe
 */
public class ItemBrandSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(ItemBrandSetup.class.getName());
    private ItemBrand currBrand = new ItemBrand();
    private BrandTableModel brandTableModel = new BrandTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private int selectedRow = -1;
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;

    /**
     * Creates new form ItemBrandSetup
     */
    public ItemBrandSetup() {
        initComponents();
        initTable();
        getBrandList();
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblBrand.getModel());
        tblBrand.setRowSorter(sorter);
        BindingUtil.BindCombo(cboCusGroup, dao.findAll("CustomerGroup"));
        new ComBoBoxAutoComplete(cboCusGroup);
        cboCusGroup.setSelectedItem(null);
    }

    private void clear() {
        selectedRow = -1;
        tblBrand.clearSelection();
        currBrand = new ItemBrand();
        txtBrandName.setText(null);
        txtBrandName.requestFocusInWindow();
        lblStatus.setText("NEW");
        cboCusGroup.setSelectedItem(null);
        //System.gc();
    }

    private void initTable() {
        //Define table selection model to single row selection.
        tblBrand.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblBrand.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try {
                    if (tblBrand.getSelectedRow() != -1) {
                        selectedRow = tblBrand.convertRowIndexToModel(tblBrand.getSelectedRow());

                        if (selectedRow != -1) {
                            select(brandTableModel.getItemBrand(selectedRow));
                        }
                    }
                } catch (Exception ex) {
                    log.error("initTable-->valueChanged : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                }
            }
        });
    }

    private void select(ItemBrand itemBrand) {
        currBrand = itemBrand;
        txtBrandName.setText(itemBrand.getBrandName());
        lblStatus.setText("EDIT");
        if (currBrand.getCusGroup() != null) {
            CustomerGroup cg = (CustomerGroup) dao.find(CustomerGroup.class, currBrand.getCusGroup());
            cboCusGroup.setSelectedItem(cg);
        } else {
            cboCusGroup.setSelectedItem(null);
        }
    }

    private void getBrandList() {
        try {
            dao.open();
            List<ItemBrand> list = dao.findAllHSQL("select o from ItemBrand o order by o.brandName");
            brandTableModel.setListItemBrand(list);
        } catch (Exception ex) {
            log.error("getBrandList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtBrandName.getText().isEmpty()) {
            status = false;
        } else {
            currBrand.setBrandName(txtBrandName.getText().trim());
            currBrand.setUpdatedDate(new Date());
            String strCusGroup = null;
            if (cboCusGroup.getSelectedItem() != null) {
                strCusGroup = ((CustomerGroup) cboCusGroup.getSelectedItem()).getGroupId();
            }
            currBrand.setCusGroup(strCusGroup);
        }

        return status;
    }

    private void save() {
        try {
            if (isValidEntry()) {
                dao.save(currBrand);

                if (lblStatus.getText().equals("NEW")) {
                    brandTableModel.addItemBrand(currBrand);
                } else {
                    brandTableModel.setItemBrand(selectedRow, currBrand);
                }

                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Brand Name", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Brand Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currBrand);
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    tblBrand.setRowSorter(null);
                    brandTableModel.deleteItemBrand(tmpRow);
                    tblBrand.setRowSorter(sorter);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this category.",
                        "Brand Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
    }

    /*private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            return entry.getStringValue(0).toUpperCase().startsWith(
                    txtFilter.getText().toUpperCase());
        }
    };*/
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
        tblBrand = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtBrandName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        butDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cboCusGroup = new javax.swing.JComboBox<>();

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblBrand.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblBrand.setModel(brandTableModel);
        tblBrand.setRowHeight(23);
        tblBrand.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblBrand);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Brand Name");

        txtBrandName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

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

        lblStatus.setText("NEW");

        butDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Cus Group ");

        cboCusGroup.setFont(Global.textFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBrandName, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                            .addComponent(cboCusGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addGap(12, 12, 12)
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorter.setRowFilter(swrf);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox<String> cboCusGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblBrand;
    private javax.swing.JTextField txtBrandName;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
