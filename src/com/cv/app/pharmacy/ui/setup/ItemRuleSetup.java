/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemRule;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.ItemRuleTableModel;
import com.cv.app.pharmacy.ui.common.ItemTableModel;
import static com.cv.app.pharmacy.ui.setup.ItemSetup.log;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author Eitar
 */
public class ItemRuleSetup extends javax.swing.JPanel implements SelectionObserver, FormAction,
        KeyPropagate {

    private final AbstractDataAccess dao = Global.dao;
    private MedicineUP medUp = new MedicineUP(dao);
    private ItemTableModel itemTableModel = new ItemTableModel();

    private TableRowSorter<TableModel> sorter;
    private int selectRow = -1;
    private int iRuleSelRow = -1;
    protected Medicine currMedicine = new Medicine();
    private ItemRule currItemRule = new ItemRule();
    private String filter = "";
    private List<ItemRule> listItemRule = ObservableCollections.observableList(new ArrayList());
    private List<ItemUnit> listItemUnit = new ArrayList();
    private ItemRuleTableModel itemRuleTableModel = new ItemRuleTableModel(medUp, this);

    public void save() {
        if (isValidEntry()) {
            try {
                List<ItemRule> listRG = itemRuleTableModel.getItemRule();

                listItemRule.removeAll(listItemRule);
                for (ItemRule rg : listRG) {
                    if (rg.getStartDate() != null) {
                        if (rg.getEndDate() != null) {
                            if (Util1.nullToBlankStr(rg.getDescription()).toString() != "") {
                                if (Util1.nullToBlankStr(rg.getChekcQtyPrice().toString()).toString() != "") {
                                    if (NumberUtil.NZeroFloat(rg.getChekcQtyPrice().toString()) == 1.0) {
                                        if (NumberUtil.NZeroFloat(rg.getStartQty()) > 0) {
                                            if (NumberUtil.NZeroFloat(rg.getEndQty()) > 0) {
                                                if (NumberUtil.NZeroFloat(rg.getPrice()) > 0) {
                                                    dao.save(rg);
                                                } else {
                                                    JOptionPane.showMessageDialog(Util1.getParent(), "Fill Price",
                                                            "Check Price/Qty", JOptionPane.ERROR_MESSAGE);
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(Util1.getParent(), "Fill End Qty",
                                                        "Check Price/Qty", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(Util1.getParent(), "Fill start Qty",
                                                    "Check Price/Qty", JOptionPane.ERROR_MESSAGE);
                                        }

                                    } else if (NumberUtil.NZeroFloat(rg.getChekcQtyPrice().toString()) == 2.0) {
                                        if (NumberUtil.NZeroFloat(rg.getQty()) > 0) {
                                            if (NumberUtil.NZeroFloat(rg.getProQty()) > 0) {
                                                dao.save(rg);
                                            } else {
                                                JOptionPane.showMessageDialog(Util1.getParent(), "Fill Pro Qty",
                                                        "Check Price/Qty", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(Util1.getParent(), "Fill Qty",
                                                    "Check Price/Qty", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(Util1.getParent(), "Fill 1 or 2 at Price/Qty",
                                                "Check Price/Qty", JOptionPane.ERROR_MESSAGE);
                                    }

                                }
                            }
                        }
                    }
                }

                newForm();
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Duplicate entry.",
                        "Item Name", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void newForm() {

    }

    public void history() {
    }

    @Override
    public void delete() {
        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                "Item Rule delete", JOptionPane.YES_NO_OPTION);

        String sql;

        if (yes_no == 0) {
            sql = "delete from item_rule where item_rule_no = '" + currItemRule.getRuleId() + "'";
            try {
                dao.open();
                dao.deleteSQL(sql);
                dao.close();

                itemRuleTableModel.delete(iRuleSelRow);
                iRuleSelRow = -1;
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }

        }
    }

    public void deleteCopy() {
    }

    public void print() {
    }

    public void keyEvent(KeyEvent e) {
    }

    /**
     * Creates new form ItemRuleSetup
     */
    public ItemRuleSetup() {
        initComponents();

        try {
            dao.open();
            initTableItem();
            initTableRule();
            sorter = new TableRowSorter(tblItem.getModel());
            tblItem.setRowSorter(sorter);
            assignDefaultValue();
            dao.close();
        } catch (Exception ex) {
            log.error("ItemRuleSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        tblItem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblItem.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblItem.getSelectedRow() >= 0) {
                    selectRow = tblItem.convertRowIndexToModel(tblItem.getSelectedRow());
                }
                System.out.println("Table Selection-1 : " + selectRow);
                if (selectRow >= 0) {
                    try {
                        if (tblItemRule.getCellEditor() != null) {
                            tblItemRule.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {

                    }
                    selected("Medicine", itemTableModel.getMedicine(selectRow));
                }
            }
        });

        tblItemRule.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblItemRule.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblItem.getSelectedRow() >= 0) {
                    iRuleSelRow = tblItemRule.convertRowIndexToModel(tblItemRule.getSelectedRow());
                }
                System.out.println("Table Item Rule : " + iRuleSelRow);
                if (iRuleSelRow >= 0) {
                    ItemRule ir = itemRuleTableModel.getItemRule(iRuleSelRow);
                    if (ir != null) {
                        selected("ItemRule", ir);
                    }
                }
            }
        });
        itemRuleTableModel.setParent(tblItemRule);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "Medicine":
                try {
                dao.open();
                currMedicine = (Medicine) dao.find(Medicine.class,
                        ((VMedicine1) selectObj).getMedId());
                lblMedName.setText(currMedicine.getMedName());
                lblMedID.setText(currMedicine.getMedId());
                itemRuleTableModel.setMedID(currMedicine.getMedId());

                String str = "select o from ItemRule o where o.med_id='" + currMedicine.getMedId() + "'";
                List<ItemRule> listiItemRules = dao.findAllHSQL(str);
                if (!listiItemRules.isEmpty()) {
                    itemRuleTableModel.setListDetail(listiItemRules);
                } else {
                    listiItemRules.removeAll(listItemRule);
                    itemRuleTableModel.setListDetail(listiItemRules);

                    List<ItemRule> listRG = itemRuleTableModel.getItemRule();

                    for (ItemRule rg : listRG) {
                        rg.setMed_id(currMedicine.getMedId());
                    }
                }

                Medicine med = (Medicine) dao.find(Medicine.class, ((VMedicine1) selectObj).getMedId());
                medUp.add(med);

            } catch (Exception ex) {
                log.error("select Medicine : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
            break;
            case "ItemRule":
                try {
                if (((ItemRule) selectObj).getRuleId() != null) {
                    dao.open();
                    currItemRule = (ItemRule) dao.find(ItemRule.class,
                            ((ItemRule) selectObj).getRuleId());
                    dao.close();
                }
            } catch (Exception ex) {
                log.error("select ItemRule : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    private void assignDefaultValue() {
        itemRuleTableModel.setListDetail(new ArrayList<ItemRule>());
    }

    private void initTableItem() {
        try {
            itemTableModel.setListMedicine(dao.findAllHSQL("select o from VMedicine1 o order by o.medName"));

            //Adjust table column width
            TableColumn column = tblItem.getColumnModel().getColumn(0);
            column.setPreferredWidth(50);

            column = tblItem.getColumnModel().getColumn(1);
            column.setPreferredWidth(300);

            column = tblItem.getColumnModel().getColumn(2);
            column.setPreferredWidth(10);
        } catch (Exception ex) {
            log.error("initTableItem : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTableRule() {
        tblItemRule.getColumnModel().getColumn(0).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(1).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(2).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(3).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(4).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(5).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(6).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(7).setCellEditor(
                new BestTableCellEditor(this));
        tblItemRule.getColumnModel().getColumn(8).setCellEditor(
                new BestTableCellEditor(this));

        //Adjust table column width
        TableColumn column = tblItemRule.getColumnModel().getColumn(0);
        column.setPreferredWidth(150);

        column = tblItemRule.getColumnModel().getColumn(1);
        column.setPreferredWidth(80);

        column = tblItemRule.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);

        column = tblItemRule.getColumnModel().getColumn(3);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(4);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(5);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(6);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(7);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(8);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(9);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(10);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(11);
        column.setPreferredWidth(50);

        column = tblItemRule.getColumnModel().getColumn(11);
        column.setPreferredWidth(50);
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(lblMedName.getText()).equals("Med Name")) {
            JOptionPane.showMessageDialog(this, "Medicine name must not be blank.",
                    "Medicine name.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }
        return status;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        lblMedName = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItemRule = new javax.swing.JTable(itemRuleTableModel);

        tblItemRule.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblItemRule.setRowHeight(23);
        lblMedID = new javax.swing.JLabel();

        txtSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        tblItem.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblItem.setRowHeight(23);
        tblItem.setModel(itemTableModel);
        jScrollPane1.setViewportView(tblItem);

        lblMedName.setFont(new java.awt.Font("Zawgyi-One", 1, 12)); // NOI18N
        lblMedName.setText("Med Name");

        tblItemRule.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setViewportView(tblItemRule);

        lblMedID.setFont(new java.awt.Font("Zawgyi-One", 1, 12)); // NOI18N
        lblMedID.setText("Med Code");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(txtSearch))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMedID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(lblMedName, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
                .addGap(23, 23, 23))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMedName)
                    .addComponent(lblMedID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );
    }// </editor-fold>//GEN-END:initComponents

    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            if (NumberUtil.isNumber(txtSearch.getText())) {
                if (entry.getStringValue(0).toUpperCase().startsWith(
                        txtSearch.getText().toUpperCase())) {
                    return true;
                }
            } else if (entry.getStringValue(1).toUpperCase().startsWith(
                    txtSearch.getText().toUpperCase())) {
                return true;
            }/*
                 * else if(entry.getStringValue(2).toUpperCase().startsWith(
                 * txtSearch.getText().toUpperCase())){ return true;
                 }
             */

 /*
             * for (int i = 0; i < entry.getValueCount(); i++) { String tmp =
             * entry.getStringValue(i); if
             * (entry.getStringValue(i).toUpperCase().startsWith(
             * textComp.getText().toUpperCase())) { return true; }
             }
             */
            return false;
        }
    };

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        //if (statusFilter) {
        /*if (filter.isEmpty()) {
            itemTableModel.setListMedicine(dao.findAll("VMedicine1"));
        } else {
            itemTableModel.setListMedicine(dao.findAll("VMedicine1", filter));
        }*/
        //statusFilter = false;
        //}

        if (txtSearch.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorter.setRowFilter(startsWithFilter);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtSearch.getText()));
        }
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblMedID;
    private javax.swing.JLabel lblMedName;
    private javax.swing.JTable tblItem;
    private javax.swing.JTable tblItemRule;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    private void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
