/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * ItemSetup.java
 *
 * Created on Apr 29, 2012, 12:13:58 PM
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.pharmacy.database.entity.PackingTemplate;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.CharacterNo;
import com.cv.app.pharmacy.database.entity.PackingTemplateDetail;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.ItemSetupLocationItemMappingTableModel;
import com.cv.app.pharmacy.ui.common.ItemTableModel;
import com.cv.app.pharmacy.ui.common.RelationPriceTableModel;
import com.cv.app.pharmacy.ui.common.TableUnitCellEditor;
import com.cv.app.pharmacy.ui.util.ItemSetupFilterDialog;
import com.cv.app.pharmacy.ui.util.PackingTemplateList;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.opencsv.CSVReader;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
//import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.richclient.application.Application;

/**
 *
 * Medicine setup panel.
 */
public class ItemSetupKS extends javax.swing.JPanel implements SelectionObserver, FormAction,
        KeyPropagate {

    static Logger log = Logger.getLogger(ItemSetupKS.class.getName());
    protected Medicine currMedicine = new Medicine();
    private final AbstractDataAccess dao = Global.dao;
    private JComboBox cboUnit = new JComboBox();
    private BestAppFocusTraversalPolicy focusPolicy;
    private RelationPriceTableModel rpTableMode = new RelationPriceTableModel();
    private TableRowSorter<TableModel> sorter;
    private ItemTableModel itemTableModel = new ItemTableModel();
    private int selectRow = -1;
    private boolean statusFilter = false;
    private List<ItemUnit> listItemUnit = new ArrayList();
    private String filter = "";
    private ItemSetupLocationItemMappingTableModel mappingTableModel = new ItemSetupLocationItemMappingTableModel();

    /**
     * Creates new form ItemSetup
     */
    public ItemSetupKS() {
        initComponents();

        try {
            dao.open();
            initCombo();
            initTableRelationGroup();
            initTableItem();
            initMappingTableModel();
            sorter = new TableRowSorter(tblItem.getModel());
            tblItem.setRowSorter(sorter);
            assignDefaultValue();
            actionMapping();
            chkActive.setSelected(true);
            //((AbstractDocument)txtBrandName.getDocument()).setDocumentFilter(new NumberDocFilter());
            /*
             * String[] column = {"Brand Name"}; List listBrandName =
             * dao.findAllSQLQuery("select distinct brand_name, chemical_name from
             * medicine"); new AutoCompleter(txtBrandName, listBrandName, column);
             */

            dao.close();
        } catch (Exception ex) {
            log.error("ItemSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        tblItem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblItem.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblItem.getSelectedRow() >= 0) {
                    selectRow = tblItem.convertRowIndexToModel(tblItem.getSelectedRow());
                }
                //System.out.println("Table Selection : " + selectRow);
                if (selectRow >= 0) {
                    try {
                        if (tblRelationPrice.getCellEditor() != null) {
                            tblRelationPrice.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {

                    }
                    selected("Medicine", itemTableModel.getMedicine(selectRow));
                }
            }
        });

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);

        rpTableMode.setRelStrControl(txtUnitRelation);
        rpTableMode.setAutoCalculate(chkCalculate);

        lblStatus.setText("NEW");
        autoAssignTemplate();

        if (Util1.getPropValue("System.app.ItemSetup.AutoCode").equals("N")) {
            txtItemCode.setEditable(true);
        }
    }

    public void setFocus() {
        txtItemName.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

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

    private boolean isDuplicate() {
        boolean status = false;
        try {
            if (!txtItemCode.getText().trim().isEmpty()) {
                if (lblStatus.getText().equals("NEW")) {
                    List<Medicine> listMed = dao.findAllHSQL(
                            "select o from Medicine o where o.medId = '" + txtItemCode.getText().trim() + "'");
                    if (listMed != null) {
                        if (!listMed.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("isDuplicate : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtItemCode.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Medicine code must not be blank.",
                    "Medicine name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtItemCode.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtItemName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Medicine name must not be blank.",
                    "Medicine name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtItemName.requestFocusInWindow();
        } else if (cboItemType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "You must choose medicine type.",
                    "Med Type null error.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboItemType.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtItemName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Med Name cannot be blank.",
                    "Med Name null error.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtItemName.requestFocusInWindow();
        } else if (isDuplicate()) {
            JOptionPane.showMessageDialog(this, "Duplicate item code.",
                    "Item Code.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            try {

                /*if (txtItemCode.getText().isEmpty()) {
                    txtItemCode.setText(getMedCode(txtItemName.getText()));
                }*/
                if (!Util1.nullToBlankStr(txtLiceneExpDate.getText()).equals("")) {
                    currMedicine.setLiceneExpDate(DateUtil.toDateTime(txtLiceneExpDate.getText()));
                }

                if (currMedicine.getMedId() == null) {
                    currMedicine.setMedId(getMedCode(txtItemName.getText()));
                }

                if (isDuplicate(currMedicine.getMedId(), txtItemCode.getText().trim(),
                        lblStatus.getText())) {
                    return false;
                }
                //currMedicine.setMedId(txtItemCode.getText().trim());
                currMedicine.setMedTypeId((ItemType) cboItemType.getSelectedItem());
                currMedicine.setMedName(txtItemName.getText().trim());
                currMedicine.setCatId((Category) cboCategory.getSelectedItem());
                currMedicine.setBrand((ItemBrand) cboBrandName.getSelectedItem());
                currMedicine.setChemicalName(txtChemicalName.getText().trim());
                currMedicine.setRelStr(txtUnitRelation.getText().trim());
                currMedicine.setActive(chkActive.isSelected());
                currMedicine.setBarcode(txtBarcode.getText().trim());

                /*if (txtShortName.getText().trim().isEmpty()) {
                    String codeUsage = Util1.getPropValue("system.item.code.field");
                    if (codeUsage.equals("SHORTNAME") && lblStatus.getText().equals("NEW")) {
                        currMedicine.setShortName(currMedicine.getMedId());
                    } else {
                        currMedicine.setShortName(txtShortName.getText().trim());
                    }
                } else {
                    currMedicine.setShortName(txtShortName.getText().trim());
                }*/
                currMedicine.setShortName(txtItemCode.getText().trim());
                currMedicine.setPurPrice(NumberUtil.getDouble(txtPurPrice.getText()));
                currMedicine.setPurUnit((ItemUnit) cboPurUnit.getSelectedItem());

                if (lblStatus.getText().equals("NEW")) {
                    currMedicine.setCreatedBy(Global.loginUser);
                    currMedicine.setCreatedDate(DateUtil.toDateTime(DateUtil.getTodayDateStr()));
                } else {
                    currMedicine.setUpdatedBy(Global.loginUser);
                }
                currMedicine.setUpdatedDate(DateUtil.toDateTime(DateUtil.getTodayDateStr()));
                currMedicine.setUnitSmallest(rpTableMode.getUnitSmallest());
                currMedicine.setUnitStr(rpTableMode.getUnitStr());

            } catch (Exception ex) {
                log.error("isValidEntry : " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error in validation.",
                        "Some Error", JOptionPane.ERROR_MESSAGE);
                status = false;
            }
        }

        return status;
    }

    private boolean isDuplicate(String itemCode, String shortName, String strStatus) {
        try {
            if (strStatus.equals("NEW")) {
                List<Medicine> listMed = dao.findAllHSQL("select o from Medicine o "
                        + "where o.medId = '" + itemCode + "'");
                if (listMed != null) {
                    if (!listMed.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Duplicate system item code. You cannot save.",
                                "Duplicate", JOptionPane.ERROR_MESSAGE);
                        log.error("isDuplicate item code : " + itemCode + " Status : " + strStatus);
                        return true;
                    }
                }

                listMed = dao.findAllHSQL("select o from Medicine o "
                        + "where o.shortName = '" + itemCode + "'");
                if (listMed != null) {
                    if (!listMed.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Duplicate item code. You cannot save.",
                                "Duplicate", JOptionPane.ERROR_MESSAGE);
                        log.error("isDuplicate short name : " + shortName + " Status : " + strStatus);
                        return true;
                    }
                }
            } else if (strStatus.equals("EDIT")) {
                List<Medicine> listMed = dao.findAllHSQL("select o from Medicine o "
                        + "where o.shortName = '" + shortName + "' and o.medId <> '" + itemCode + "'");
                if (listMed != null) {
                    if (!listMed.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Duplicate item code. You cannot save.",
                                "Duplicate", JOptionPane.ERROR_MESSAGE);
                        log.error("isDuplicate short name : " + shortName + " Status : " + strStatus);
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("isDuplicate : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return false;
    }

    @Override
    public void save() {
        if (isValidEntry()) {
            try {
                if (lblStatus.getText().equals("NEW")) {
                    List<RelationGroup> lstTmp = new ArrayList();
                    lstTmp.addAll(rpTableMode.getDetail());
                    currMedicine.setRelationGroupId(lstTmp);
                } else if (currMedicine.getRelationGroupId() != null) {
                    currMedicine.getRelationGroupId().removeAll(currMedicine.getRelationGroupId());
                    currMedicine.setRelationGroupId(rpTableMode.getDetail());
                } else {
                    List<RelationGroup> lstTmp = new ArrayList();
                    lstTmp.addAll(rpTableMode.getDetail());
                    currMedicine.setRelationGroupId(lstTmp);
                }
                //For BK Pagolay
                if (lblStatus.getText().equals("EDIT")) {
                    Date d = new Date();
                    dao.execProc("bkmedicine",
                            currMedicine.getMedId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId());

                    /*if (currMedicine.getRelationGroupId() == null) {
                        log.error("Save : Invalid relation.");
                        return;
                    } else if (currMedicine.getRelationGroupId().isEmpty()) {
                        log.error("Save : Invalid relation.");
                        return;
                    }*/
                }

                List<RelationGroup> listRG = currMedicine.getRelationGroupId();
                if (listRG == null) {
                    log.error("Save : Invalid relation.");
                } else if (listRG.isEmpty()) {
                    log.error("Save : Invalid relation.");
                } else {
                    //For BK Pagolay
                    dao.save(currMedicine);
                    String deleteSQL;
                    if (lblStatus.getText().equals("EDIT")) {
                        //All detail section need to explicity delete
                        //because of save function only delete to join table
                        deleteSQL = rpTableMode.getDeleteSql();
                        if (deleteSQL != null) {
                            dao.execSql(deleteSQL);
                        }
                    }

                    dao.execSql("update machine_info set action_status = 'ITEM' where machine_name is not null");
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
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void delete() {
        String sql;
        String childId = null;

        try {
            if (lblStatus.getText().equals("EDIT")) {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Item delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.execProc("bkmedicine",
                            currMedicine.getMedId(),
                            DateUtil.toDateTimeStrMYSQL(DateUtil.getTodayDateTime()),
                            Global.loginUser.getUserId());

                    dao.open();
                    dao.beginTran();

                    sql = "delete from med_rel where med_id = '" + currMedicine.getMedId() + "'";
                    dao.deleteSQL(sql);

                    List<RelationGroup> list = currMedicine.getRelationGroupId();
                    for (RelationGroup rg : list) {
                        if (childId == null) {
                            childId = rg.getRelGId().toString();
                        } else {
                            childId = childId + "," + rg.getRelGId().toString();
                        }
                    }
                    sql = "delete from relation_group where rel_group_id in (" + childId + ")";
                    dao.deleteSQL(sql);

                    sql = "delete from medicine where med_id = '" + currMedicine.getMedId() + "'";
                    dao.deleteSQL(sql);

                    dao.commit();
                    itemTableModel.deleteItem(selectRow);
                    selectRow = -1;
                }
                newForm();
            }

        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete this medicine.",
                    "Medicine Delete", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
    }

    private void filterItem(String strFilter) {
        try {
            itemTableModel.setListMedicine(dao.findAll("VMedicine1", strFilter));
            statusFilter = true;
        } catch (Exception ex) {
            log.error("filterItem : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

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
            }
            return false;
        }
    };

    private void autoAssignTemplate() {
        if (Util1.getPropValue("system.app.ItemSetup.AutoTemplate").equals("Y")) {
            int id = NumberUtil.NZeroInt(Util1.getPropValue("system.app.ItemSetup.AutoTemplateId"));
            try {
                PackingTemplate pt = (PackingTemplate) dao.find(PackingTemplate.class, id);

                if (pt != null) {
                    selected("PackingTemplate", pt);
                }
            } catch (Exception ex) {
                log.error("autoAssignTemplate : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void processCSV(File file) {
        if (file != null) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                try (CSVReader csvReader = new CSVReader(reader)) {
                    String[] nextRecord;
                    int ttlRec = 0;

                    log.info("processCSV Start");
                    while ((nextRecord = csvReader.readNext()) != null) {
                        String medName = nextRecord[0];
                        String relStr = nextRecord[1];
                        String strMedType = nextRecord[2];
                        String medCode = getMedCode(medName, strMedType);
                        ItemType medType = (ItemType) dao.find(ItemType.class, strMedType);
                        String tmpRelStr = relStr.replace("*", ",");
                        String[] relStrArr = tmpRelStr.split(",");
                        String strUnitStr = "";
                        String strUnitSmallest = "";
                        float smallestQty = 1f;
                        List<RelationGroup> listRG = new ArrayList();

                        if (relStrArr.length > 0) {
                            int uniqueId = 1;
                            for (String tmpStr : relStrArr) {
                                String strQty = getNumber(tmpStr);
                                String strUnit = tmpStr.replace(strQty, "");

                                if (strUnitStr.isEmpty()) {
                                    strUnitStr = strUnit;
                                } else {
                                    strUnitStr = strUnitStr + "/" + strUnit;
                                }

                                ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class, strUnit);
                                RelationGroup rg = new RelationGroup();
                                rg.setRelUniqueId(uniqueId);
                                rg.setUnitId(iu);
                                rg.setUnitQty(Float.parseFloat(strQty));
                                smallestQty = smallestQty * rg.getUnitQty();

                                listRG.add(rg);
                            }

                            for (RelationGroup rg : listRG) {
                                float unitQty = rg.getUnitQty() == null ? 1 : rg.getUnitQty();
                                if (rg.getUnitId() != null) {
                                    smallestQty = smallestQty / unitQty;
                                    rg.setSmallestQty(smallestQty);
                                    if (strUnitSmallest.isEmpty()) {
                                        strUnitSmallest = String.valueOf(smallestQty);
                                    } else {
                                        strUnitSmallest = strUnitSmallest + "/" + String.valueOf(smallestQty);
                                    }
                                } else {
                                    rg.setSmallestQty(1f);
                                    if (strUnitSmallest.isEmpty()) {
                                        strUnitSmallest = String.valueOf(1);
                                    } else {
                                        strUnitSmallest = strUnitSmallest + "/" + String.valueOf(1);
                                    }
                                }
                            }
                        }

                        ttlRec++;
                        log.info("getNewMedCode : " + medName + " Cnt : " + ttlRec);

                        Medicine med = new Medicine();
                        med.setActive(true);
                        med.setMedId(medCode);
                        med.setMedName(medName);
                        med.setMedTypeId(medType);
                        med.setRelStr(relStr);
                        med.setUnitSmallest(strUnitSmallest);
                        med.setUnitStr(strUnitStr);
                        med.setRelationGroupId(listRG);

                        dao.save(med);
                    }

                    log.info("processCSV End: " + ttlRec);
                }
            } catch (Exception ex) {
                log.error("processCSV : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private String getNumber(String impStr) {
        String strNumber = "";

        try {
            for (int i = 0; i < impStr.length(); i++) {
                String tmpStr = impStr.substring(i, i + 1);
                int tmpValue = Integer.parseInt(tmpStr);
                strNumber = strNumber + tmpStr;
            }
        } catch (NumberFormatException ex) {

        }

        return strNumber;
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
        tblItem = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtItemCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtItemName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboCategory = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtChemicalName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtUnitRelation = new javax.swing.JTextField();
        butItemType = new javax.swing.JButton();
        butCategory = new javax.swing.JButton();
        butUnitRelation = new javax.swing.JButton();
        cboItemType = new javax.swing.JComboBox();
        cboBrandName = new javax.swing.JComboBox();
        butBrand = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        butCharNo = new javax.swing.JButton();
        butUnit = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtLiceneExpDate = new javax.swing.JFormattedTextField();
        butUpload = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        cmdFilter = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRelationPrice = new javax.swing.JTable(rpTableMode);
        chkCalculate = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JTextField();
        cboPurUnit = new javax.swing.JComboBox();
        chkActive = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblLocationItemMapping = new javax.swing.JTable();

        tblItem.setFont(Global.textFont);
        tblItem.setModel(itemTableModel);
        tblItem.setRowHeight(23);
        jScrollPane1.setViewportView(tblItem);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Item Code ");

        txtItemCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Item Type");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Item Name ");

        txtItemName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtItemName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtItemNameFocusLost(evt);
            }
        });
        txtItemName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtItemNameActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category");

        cboCategory.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Brand Name");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Remark");

        txtChemicalName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Unit Relation");

        txtUnitRelation.setEditable(false);
        txtUnitRelation.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtUnitRelation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtUnitRelationMouseClicked(evt);
            }
        });

        butItemType.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butItemType.setText("...");
        butItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butItemTypeActionPerformed(evt);
            }
        });

        butCategory.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butCategory.setText("...");
        butCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCategoryActionPerformed(evt);
            }
        });

        butUnitRelation.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butUnitRelation.setText("...");
        butUnitRelation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUnitRelationActionPerformed(evt);
            }
        });

        cboItemType.setFont(Global.textFont);
        cboItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboItemTypeActionPerformed(evt);
            }
        });

        cboBrandName.setFont(Global.textFont);

        butBrand.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butBrand.setText("...");
        butBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrandActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Barcode");

        txtBarcode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butCharNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butCharNo.setText("Char No");
        butCharNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCharNoActionPerformed(evt);
            }
        });

        butUnit.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butUnit.setText("Unit");
        butUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUnitActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Licene Exp Date");

        txtLiceneExpDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLiceneExpDateActionPerformed(evt);
            }
        });

        butUpload.setText("Upload");
        butUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel2)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtChemicalName, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(butItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtItemCode, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txtUnitRelation, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butUnitRelation, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(butCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(butBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                            .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLiceneExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butUpload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCharNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butCategory, butItemType, butUnitRelation});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel1)
                    .addComponent(txtItemCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butItemType)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butCategory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butBrand))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtChemicalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtUnitRelation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butUnitRelation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butCharNo)
                        .addComponent(butUpload))
                    .addComponent(butUnit)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(txtLiceneExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        cmdFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cmdFilter.setText("...");
        cmdFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFilterActionPerformed(evt);
            }
        });

        tblRelationPrice.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblRelationPrice.setRowHeight(23);
        jScrollPane2.setViewportView(tblRelationPrice);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
        );

        chkCalculate.setText("Calculate");

        lblStatus.setText("NEW");

        jLabel11.setText("Pur Price");

        txtPurPrice.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        cboPurUnit.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        chkActive.setText("Active");

        tblLocationItemMapping.setModel(mappingTableModel);
        tblLocationItemMapping.setRowHeight(23);
        jScrollPane3.setViewportView(tblLocationItemMapping);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPurUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkActive)
                        .addGap(18, 18, 18)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCalculate))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboPurUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCalculate)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdFilter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butItemTypeActionPerformed
        showDialog("Item Type Setup");
        try {
            BindingUtil.BindCombo(cboItemType, dao.findAll("ItemType"));
            cboItemType.setSelectedItem(currMedicine.getMedTypeId());
        } catch (Exception ex) {
            log.error("butItemTypeActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //initCombo();
    }//GEN-LAST:event_butItemTypeActionPerformed

    private void showDialog(String panelName) {
        SetupDialog dialog = new SetupDialog(Util1.getParent(), true, panelName);

        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - dialog.getWidth()) / 2;
        int y = (screen.height - dialog.getHeight()) / 2;

        dialog.setLocation(x, y);
        dialog.show();
        System.out.println("After dialog close");
    }

    private void butCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCategoryActionPerformed
        showDialog("Category Setup");
        try {
            BindingUtil.BindCombo(cboCategory, dao.findAll("Category"));
            cboCategory.setSelectedItem(currMedicine.getCatId());
        } catch (Exception ex) {
            log.error("butCategoryActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //initCombo();
    }//GEN-LAST:event_butCategoryActionPerformed

    private void butUnitRelationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUnitRelationActionPerformed
        showDialog("Packing Template Setup");
    }//GEN-LAST:event_butUnitRelationActionPerformed

    private void butUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUnitActionPerformed
        showDialog("Item Unit Setup");
        try {
            BindingUtil.BindCombo(cboUnit, dao.findAll("ItemUnit"));
        } catch (Exception ex) {
            log.error("butUnitRelationActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //initCombo();
    }//GEN-LAST:event_butUnitActionPerformed

    private void butCharNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCharNoActionPerformed
        showDialog("Character No Setup");
    }//GEN-LAST:event_butCharNoActionPerformed

    private void txtItemNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemNameFocusLost
        if (Util1.getPropValue("system.app.ItemSetup.AutoCode").equals("Y")) {
            String codeUsage = Util1.getPropValue("system.item.code.field");
            if (!codeUsage.equals("SHORTNAME")) {
                if (txtItemCode.getText().equals("") || lblStatus.getText().equals("NEW")) {
                    txtItemCode.setText(getMedCode(txtItemName.getText()));
                }
            }
        }
    }//GEN-LAST:event_txtItemNameFocusLost

    private void txtUnitRelationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUnitRelationMouseClicked
        if (evt.getClickCount() == 2 && !PharmacyUtil.isItemAlreadyUsaded(txtItemCode.getText(), dao)) {
            PackingTemplateList dialog
                    = new PackingTemplateList(Application.instance().getActiveWindow().getControl(), this);
        }
    }//GEN-LAST:event_txtUnitRelationMouseClicked

  private void txtSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyTyped
  }//GEN-LAST:event_txtSearchKeyTyped

  private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
      //if (statusFilter) {
      /*if (filter.isEmpty()) {
       itemTableModel.setListMedicine(dao.findAll("Medicine"));
       } else {
       itemTableModel.setListMedicine(dao.findAll("Medicine", filter));
       }
       statusFilter = false;*/
      //}

      if (txtSearch.getText().length() == 0) {
          sorter.setRowFilter(null);
      } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
          sorter.setRowFilter(startsWithFilter);
      } else {
          sorter.setRowFilter(RowFilter.regexFilter(txtSearch.getText()));
      }
  }//GEN-LAST:event_txtSearchKeyReleased

    private void butBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrandActionPerformed
        showDialog("Brand Setup");
        try {
            BindingUtil.BindCombo(cboBrandName, dao.findAll("ItemBrand"));
            cboBrandName.setSelectedItem(currMedicine.getBrand());
        } catch (Exception ex) {
            log.error("butBrandActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //initCombo();
    }//GEN-LAST:event_butBrandActionPerformed

  private void cmdFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFilterActionPerformed
      ItemSetupFilterDialog filterDialog = new ItemSetupFilterDialog(dao);

      if (filterDialog.getStatus()) {
          filter = filterDialog.getFilter();
          filterItem(filter);
      }
  }//GEN-LAST:event_cmdFilterActionPerformed

  private void txtItemNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtItemNameActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_txtItemNameActionPerformed

    private void txtLiceneExpDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLiceneExpDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLiceneExpDateActionPerformed

    private void butUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUploadActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processCSV(selectedFile);
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }

        //String tmp = getNumber("10Bx");
    }//GEN-LAST:event_butUploadActionPerformed

    private void cboItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboItemTypeActionPerformed

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PackingTemplate":
                PackingTemplate template;
                List<PackingTemplateDetail> details;

                try {
                    dao.open();
                    template = (PackingTemplate) dao.find(PackingTemplate.class,
                            ((PackingTemplate) selectObj).getTemplateId());
                    details = template.getLstPackingTemplateDetail();

                    List<RelationGroup> listRelationGroup = new ArrayList();

                    listItemUnit.removeAll(listItemUnit);
                    txtUnitRelation.setText(template.getRelStr());

                    for (PackingTemplateDetail detail : details) {
                        RelationGroup relGroup = new RelationGroup();

                        relGroup.setUnitId(detail.getItemUnit());
                        relGroup.setRelUniqueId(detail.getUniqueId());
                        relGroup.setSmallestQty(detail.getSmallestQty());
                        relGroup.setUnitQty(detail.getUnitQty());
                        listRelationGroup.add(relGroup);
                        listItemUnit.add(detail.getItemUnit());
                    }

                    dao.close();
                    rpTableMode.setListDetail(listRelationGroup);
                    BindingUtil.BindCombo(cboPurUnit, listItemUnit);
                } catch (Exception ex) {
                    log.error("select PackingTemplate : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
                break;
            case "Medicine":
                try {
                dao.open();
                currMedicine = (Medicine) dao.find(Medicine.class,
                        ((VMedicine1) selectObj).getMedId());

                List<RelationGroup> listRelationGroup = currMedicine.getRelationGroupId();

                if (listRelationGroup != null) {
                    if (listRelationGroup.size() > 0) {
                        currMedicine.setRelationGroupId(listRelationGroup);
                        listItemUnit.removeAll(listItemUnit);
                        for (RelationGroup rg : listRelationGroup) {
                            listItemUnit.add(rg.getUnitId());
                        }
                        BindingUtil.BindCombo(cboPurUnit, listItemUnit);
                        rpTableMode.setEditable(!PharmacyUtil.isItemAlreadyUsaded(currMedicine.getMedId(), dao));
                    } else {
                        rpTableMode.setEditable(true);
                    }
                } else {
                    rpTableMode.setEditable(true);
                }

                //txtItemCode.setText(currMedicine.getMedId());
                txtItemName.setText(currMedicine.getMedName());
                cboBrandName.setSelectedItem(currMedicine.getBrand());
                txtChemicalName.setText(currMedicine.getChemicalName());
                txtUnitRelation.setText(currMedicine.getRelStr());
                cboCategory.setSelectedItem(currMedicine.getCatId());
                cboItemType.setSelectedItem(currMedicine.getMedTypeId());
                chkActive.setSelected(currMedicine.getActive());
                //rpTableMode.setEditable(!PharmacyUtil.isItemAlreadyUsaded(currMedicine.getMedId(), dao));
                rpTableMode.setListDetail(currMedicine.getRelationGroupId());
                txtBarcode.setText(currMedicine.getBarcode());
                txtItemCode.setText(currMedicine.getShortName());
                txtPurPrice.setText(NumberUtil.toChar(currMedicine.getPurPrice()));
                cboPurUnit.setSelectedItem(currMedicine.getPurUnit());
                txtLiceneExpDate.setText(DateUtil.toDateStr(currMedicine.getLiceneExpDate()));

                lblStatus.setText("EDIT");
                dao.close();
                mappingTableModel.setMedId(currMedicine.getMedId());
            } catch (Exception ex) {
                log.error("select Medicine : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            break;
        }
    }

    private void clear() {
        txtItemCode.setText("");
        txtItemName.setText("");
        txtBarcode.setText("");
        cboBrandName.setSelectedItem(null);
        txtChemicalName.setText("");
        txtUnitRelation.setText("");
        txtLiceneExpDate.setText("");
        txtPurPrice.setText("");
        //txtShortName.setText("");
        lblStatus.setText("NEW");
        currMedicine = new Medicine();
        chkActive.setSelected(true);
        selectRow = -1;
        rpTableMode.setEditable(true);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dao.open();
                    if (filter.isEmpty()) {
                        itemTableModel.setListMedicine(dao.findAll("VMedicine1"));
                    } else {
                        filterItem(filter);
                    }
                    dao.close();
                } catch (Exception ex) {
                    log.error("clear : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }
        });
        timer.setRepeats(false);
        timer.start();

        listItemUnit.removeAll(listItemUnit);
        BindingUtil.BindCombo(cboPurUnit, listItemUnit);
        assignDefaultValue();
        setFocus();
        System.gc();

        autoAssignTemplate();
        mappingTableModel.setMedId("-");
    }

    private void assignDefaultValue() {
        rpTableMode.setListDetail(new ArrayList<RelationGroup>());
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboItemType, dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
            BindingUtil.BindCombo(cboCategory, dao.findAllHSQL("select o from Category o order by o.catName"));
            BindingUtil.BindCombo(cboUnit, dao.findAllHSQL("select o from ItemUnit o order by o.itemUnitName"));
            BindingUtil.BindCombo(cboBrandName, dao.findAllHSQL("select o from ItemBrand o order by o.brandName"));

            new ComBoBoxAutoComplete(cboItemType, this);
            new ComBoBoxAutoComplete(cboCategory, this);
            //new ComBoBoxAutoComplete(cboUnit);
            new ComBoBoxAutoComplete(cboBrandName, this);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTableItem() {
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            log.info("Start get.");
                            List<VMedicine1> list = dao.findAll("VMedicine1");
                            log.info("End get.");
                            itemTableModel.setListMedicine(list);
                            log.info("End model assign.");
                        } catch (Exception ex) {
                            log.error("initTableItem : " + ex.getMessage());
                        } finally {
                            dao.close();
                        }
                    }
                };
                //System.out.println("Start : " + new Date());
                thread.start();
                //System.out.println("End : " + new Date());
            }
        });
        timer.setRepeats(false);
        timer.start();

        //Adjust table column width
        TableColumn column = tblItem.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);

        column = tblItem.getColumnModel().getColumn(1);
        column.setPreferredWidth(200);

        column = tblItem.getColumnModel().getColumn(2);
        column.setPreferredWidth(5);

        column = tblItem.getColumnModel().getColumn(3);
        column.setPreferredWidth(30);
    }

    private void initTableRelationGroup() {
        try {
            tblRelationPrice.getColumnModel().getColumn(0).setCellEditor(
                    new BestTableCellEditor(this));
            tblRelationPrice.getColumnModel().getColumn(2).setCellEditor(
                    new BestTableCellEditor(this));
            tblRelationPrice.getColumnModel().getColumn(3).setCellEditor(
                    new BestTableCellEditor(this));
            tblRelationPrice.getColumnModel().getColumn(4).setCellEditor(
                    new BestTableCellEditor(this));
            tblRelationPrice.getColumnModel().getColumn(5).setCellEditor(
                    new BestTableCellEditor(this));
            tblRelationPrice.getColumnModel().getColumn(6).setCellEditor(
                    new BestTableCellEditor(this));
            tblRelationPrice.getColumnModel().getColumn(7).setCellEditor(
                    new BestTableCellEditor(this));
            tblRelationPrice.getColumnModel().getColumn(1).setCellEditor(
                    new TableUnitCellEditor(dao.findAll("ItemUnit")));

            //Adjust table column width
            TableColumn column = tblRelationPrice.getColumnModel().getColumn(0);
            column.setPreferredWidth(30);

            tblRelationPrice.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    List<RelationGroup> listRG = rpTableMode.getRelationGroup();

                    listItemUnit.removeAll(listItemUnit);
                    for (RelationGroup rg : listRG) {
                        listItemUnit.add(rg.getUnitId());
                    }
                    BindingUtil.BindCombo(cboPurUnit, listItemUnit);
                }
            });
        } catch (Exception ex) {
            log.error("initTableRelationGroup : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initMappingTableModel() {
        tblLocationItemMapping.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblLocationItemMapping.getColumnModel().getColumn(1).setPreferredWidth(50);
    }

    private String getMedCode(String strCh) {
        String medCode = "";
        CharacterNo characterNo = null;

        if (!strCh.equals("") && cboItemType.getSelectedItem() != null) {
            try {
                dao.open();
                Object tmpObj;

                for (int i = 0; i < strCh.length(); i++) {
                    String strTmp = strCh.substring(i, i + 1).toUpperCase();
                    if (!strTmp.trim().equals("")) { //Space character detection
                        if (NumberUtil.isNumber(strTmp)) {
                            i = strCh.length();
                            characterNo = new CharacterNo(" ", "00");
                        } else {
                            tmpObj = dao.find(CharacterNo.class, strTmp);
                            if (tmpObj != null) {
                                i = strCh.length();
                                characterNo = (CharacterNo) tmpObj;
                            }
                        }
                    }
                }
                dao.close();
            } catch (Exception ex) {
                log.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }

            ItemType itemType = (ItemType) cboItemType.getSelectedItem();
            medCode = itemType.getItemTypeCode() + Util1.getString(characterNo.getStrNumber(), "");
            int typeLength = itemType.getItemTypeCode().length();

            try {
                dao.open();
                Object maxMedId = dao.getMax("med_id", "medicine", "med_id like '" + medCode + "%'");
                int ttlLength = NumberUtil.NZeroInt(Util1.getPropValue("system.itemsetup.code.length"));
                int leftLength = ttlLength - medCode.length();

                if (maxMedId == null) {
                    for (int i = 0; i < leftLength - 1; i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + "1";
                } else {
                    /*int ln = itemType.getItemTypeCode().length();
                     String tmpMedSerial = maxMedId.toString().substring(4, 5 + ln);

                     Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;

                     tmpMedSerial = tmpSerial.toString();

                     if (tmpMedSerial.length() == 1) {
                     tmpMedSerial = "00" + tmpMedSerial;
                     } else if (tmpMedSerial.length() == 2) {
                     tmpMedSerial = "0" + tmpMedSerial;
                     }

                     medCode = medCode + tmpMedSerial;*/

                    //String tmpMedSerial = maxMedId.toString().replace(medCode, "");
                    int begin = typeLength + 2;
                    String tmpMedSerial = maxMedId.toString().substring(begin);

                    Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;
                    tmpMedSerial = tmpSerial.toString();

                    for (int i = 0; i < (leftLength - tmpMedSerial.length()); i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + tmpMedSerial;
                }
                dao.close();
            } catch (Exception ex) {
                log.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        return medCode;
    }

    private String getMedCode(String strCh, String strItemType) {
        String medCode = "";
        CharacterNo characterNo = null;

        if (!strCh.equals("")) {
            try {
                dao.open();
                Object tmpObj;

                for (int i = 0; i < strCh.length(); i++) {
                    String strTmp = strCh.substring(i, i + 1).toUpperCase();
                    if (!strTmp.trim().equals("")) { //Space character detection
                        if (NumberUtil.isNumber(strTmp)) {
                            i = strCh.length();
                            characterNo = new CharacterNo(" ", "00");
                        } else {
                            tmpObj = dao.find(CharacterNo.class, strTmp);
                            if (tmpObj != null) {
                                i = strCh.length();
                                characterNo = (CharacterNo) tmpObj;
                            }
                        }
                    }
                }
                dao.close();
            } catch (Exception ex) {
                log.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }

            medCode = strItemType + Util1.getString(characterNo.getStrNumber(), "");
            int typeLength = strItemType.length();

            try {
                dao.open();
                Object maxMedId = dao.getMax("med_id", "medicine", "med_id like '" + medCode + "%'");
                int ttlLength = NumberUtil.NZeroInt(Util1.getPropValue("system.itemsetup.code.length"));
                int leftLength = ttlLength - medCode.length();

                if (maxMedId == null) {
                    for (int i = 0; i < leftLength - 1; i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + "1";
                } else {
                    /*int ln = itemType.getItemTypeCode().length();
                     String tmpMedSerial = maxMedId.toString().substring(4, 5 + ln);

                     Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;

                     tmpMedSerial = tmpSerial.toString();

                     if (tmpMedSerial.length() == 1) {
                     tmpMedSerial = "00" + tmpMedSerial;
                     } else if (tmpMedSerial.length() == 2) {
                     tmpMedSerial = "0" + tmpMedSerial;
                     }

                     medCode = medCode + tmpMedSerial;*/

                    //String tmpMedSerial = maxMedId.toString().replace(medCode, "");
                    int begin = typeLength + 2;
                    String tmpMedSerial = maxMedId.toString().substring(begin);

                    Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;
                    tmpMedSerial = tmpSerial.toString();

                    for (int i = 0; i < (leftLength - tmpMedSerial.length()); i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + tmpMedSerial;
                }
                dao.close();
            } catch (Exception ex) {
                log.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        return medCode;
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

    private void actionMapping() {

        //Enter event on tblExpense
        tblRelationPrice.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblRelationPrice.getActionMap().put("ENTER-Action", actionTblRelEnterKey);

        //F8 event on tblExpense
        tblRelationPrice.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblRelationPrice.getActionMap().put("F8-Action", actionItemDeleteRel);

        formActionKeyMapping(txtSearch);
        formActionKeyMapping(tblItem);
        formActionKeyMapping(cmdFilter);
        formActionKeyMapping(txtItemCode);
        formActionKeyMapping(cboItemType);
        formActionKeyMapping(butItemType);
        formActionKeyMapping(txtItemName);
        formActionKeyMapping(cboCategory);
        formActionKeyMapping(butCategory);
        formActionKeyMapping(butBrand);
        formActionKeyMapping(cboBrandName);
        formActionKeyMapping(txtChemicalName);
        formActionKeyMapping(txtUnitRelation);
        formActionKeyMapping(butUnitRelation);
        formActionKeyMapping(chkActive);
        formActionKeyMapping(butCharNo);
        formActionKeyMapping(butUnit);
        formActionKeyMapping(tblRelationPrice);
        formActionKeyMapping(chkCalculate);
    }

    private Action actionTblRelEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblRelationPrice.getCellEditor() != null) {
                    tblRelationPrice.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }
            tblRelationPrice.setColumnSelectionInterval(0, 0); //Move to Exp Type

        }
    };

    private Action actionItemDeleteRel = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblRelationPrice.getSelectedRow() >= 0) {
                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                            "Relation delete", JOptionPane.YES_NO_OPTION);

                    if (tblRelationPrice.getCellEditor() != null) {
                        tblRelationPrice.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    rpTableMode.delete(tblRelationPrice.getSelectedRow());
                }
            }

        }
    };

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Clear
        jc.getInputMap().put(KeyStroke.getKeyStroke("F10"), "F10-Action");
        jc.getActionMap().put("F10-Action", actionNewForm);

        //Delete
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
        jc.getActionMap().put("Ctrl-F8-Action", actionDelete);
    }
    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };
    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };
    private Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butBrand;
    private javax.swing.JButton butCategory;
    private javax.swing.JButton butCharNo;
    private javax.swing.JButton butItemType;
    private javax.swing.JButton butUnit;
    private javax.swing.JButton butUnitRelation;
    private javax.swing.JButton butUpload;
    private javax.swing.JComboBox cboBrandName;
    private javax.swing.JComboBox cboCategory;
    private javax.swing.JComboBox cboItemType;
    private javax.swing.JComboBox cboPurUnit;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkCalculate;
    private javax.swing.JButton cmdFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblItem;
    private javax.swing.JTable tblLocationItemMapping;
    private javax.swing.JTable tblRelationPrice;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtChemicalName;
    private javax.swing.JTextField txtItemCode;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JFormattedTextField txtLiceneExpDate;
    private javax.swing.JTextField txtPurPrice;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUnitRelation;
    // End of variables declaration//GEN-END:variables
}
