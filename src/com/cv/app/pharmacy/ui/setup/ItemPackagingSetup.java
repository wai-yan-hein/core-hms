/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.CharacterNo;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PackingItem;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.ItemTableModel;
import com.cv.app.pharmacy.ui.common.PackingTableModel;
import com.cv.app.pharmacy.ui.common.RelationPriceTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.TableUnitCellEditor;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class ItemPackagingSetup extends javax.swing.JPanel implements SelectionObserver, FormAction,
        KeyPropagate {

    private final ItemTableModel itemTableModel = new ItemTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private PackingTableModel packingTableModel = new PackingTableModel(dao);
    RelationPriceTableModel rpTableMode = new RelationPriceTableModel();
    private int selectRow = -1;
    static Logger log = Logger.getLogger(ItemPackagingSetup.class.getName());
    protected Medicine currMedicine = new Medicine();
    private final List<ItemUnit> listItemUnit = new ArrayList();
    
    /**
     * Creates new form ItemPackagingSetup
     */
    public ItemPackagingSetup() {
        initComponents();
        initPackingTable();
        initCombo();
        initPackingItemTable();
        initTableRelationGroup();
        
        rpTableMode.setRelStrControl(txtRelationString);
    }

    private void assignPackingTable() {
        String strSql = "select o from Medicine o where o.typeOption = 'PACKING' "
                + " order by o.medId";
        List<VMedicine1> listMedicine = dao.findAllHSQL(strSql);
        itemTableModel.setListMedicine(listMedicine);
    }

    private void initPackingTable() {
        assignPackingTable();

        //Adjust table column width
        TableColumn column = tblPackingList.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);

        column = tblPackingList.getColumnModel().getColumn(1);
        column.setPreferredWidth(300);

        column = tblPackingList.getColumnModel().getColumn(2);
        column.setPreferredWidth(10);

        tblPackingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPackingList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (tblPackingList.getSelectedRow() >= 0) {
                            selectRow = tblPackingList.convertRowIndexToModel(tblPackingList.getSelectedRow());
                        }
                        //System.out.println("Table Selection : " + selectRow);
                        if (selectRow >= 0) {
                            try {
                                if(tblPackingList.getCellEditor() != null){
                                    tblPackingList.getCellEditor().stopCellEditing();
                                }
                            } catch (Exception ex) {

                            }

                            VMedicine1 med = itemTableModel.getMedicine(selectRow);
                            assignMed(med);
                        }
                    }
                });
    }

    private void assignMed(VMedicine1 med) {
        currMedicine = (Medicine) dao.find(Medicine.class, med.getMedId());
        List<RelationGroup> listRelationGroup = currMedicine.getRelationGroupId();

        if (listRelationGroup.size() > 0) {
            currMedicine.setRelationGroupId(listRelationGroup);
        }

        txtItemCode.setText(currMedicine.getMedId());
        cboItemType.setSelectedItem(currMedicine.getMedTypeId());
        txtItemName.setText(currMedicine.getMedName());
        cboCategory.setSelectedItem(currMedicine.getCatId());
        cboBrandName.setSelectedItem(currMedicine.getBrand());
        txtRemark.setText(currMedicine.getChemicalName());
        txtBarcode.setText(currMedicine.getBarcode());
        txtShortName.setText(currMedicine.getShortName());
        chkActive.setSelected(currMedicine.getActive());
        txtRelationString.setText(currMedicine.getRelStr());
        rpTableMode.setEditable(!PharmacyUtil.isItemAlreadyUsaded(currMedicine.getMedId(), dao));
        rpTableMode.setListDetail(currMedicine.getRelationGroupId());

        List<PackingItem> listPI = getPackingItem(med.getMedId());
        packingTableModel.setListPI(listPI);
        packingTableModel.addEmptyRow();
        packingTableModel.setSelectedMed(med.getMedId());

        lblStatus.setText("EDIT");
    }

    private void initTableRelationGroup() {
        tblRelation.getColumnModel().getColumn(0).setCellEditor(
                new BestTableCellEditor(this));
        tblRelation.getColumnModel().getColumn(2).setCellEditor(
                new BestTableCellEditor(this));
        tblRelation.getColumnModel().getColumn(3).setCellEditor(
                new BestTableCellEditor(this));
        tblRelation.getColumnModel().getColumn(4).setCellEditor(
                new BestTableCellEditor(this));
        tblRelation.getColumnModel().getColumn(5).setCellEditor(
                new BestTableCellEditor(this));
        tblRelation.getColumnModel().getColumn(6).setCellEditor(
                new BestTableCellEditor(this));
        tblRelation.getColumnModel().getColumn(1).setCellEditor(
                new TableUnitCellEditor(dao.findAll("ItemUnit")));

        //Adjust table column width
        TableColumn column = tblRelation.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);

        /*tblRelation.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                List<RelationGroup> listRG = rpTableMode.getRelationGroup();

                listItemUnit.removeAll(listItemUnit);
                for (RelationGroup rg : listRG) {
                    listItemUnit.add(rg.getUnitId());
                }
                BindingUtil.BindCombo(cboPurUnit, listItemUnit);
            }
        });*/
    }
    
    private void initPackingItemTable() {
        //Adjust table column width
        TableColumn column = tblPackingItem.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);

        column = tblPackingItem.getColumnModel().getColumn(1);
        column.setPreferredWidth(300);

        column = tblPackingItem.getColumnModel().getColumn(2);
        column.setPreferredWidth(10);

        column = tblPackingItem.getColumnModel().getColumn(3);
        column.setPreferredWidth(30);

        tblPackingItem.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));

        /*tblPackingItem.getColumnModel().getColumn(3).setCellEditor(
         new TableUnitCellEditor(dao.findAll("ItemUnit")));*/
        
        //F8 event on tblSale
        tblPackingItem.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblPackingItem.getActionMap().put("F8-Action", actionItemDelete);
    }

    @Override
    public void newForm() {
        txtItemCode.setText("");
        txtItemName.setText("");
        txtBarcode.setText("");
        cboBrandName.setSelectedItem(null);
        txtRemark.setText("");
        lblStatus.setText("NEW");
        txtShortName.setText(null);
        txtRelationString.setText(null);
        currMedicine = new Medicine();
        chkActive.setSelected(true);
        selectRow = -1;
        packingTableModel.setListPI(new ArrayList());
        rpTableMode.setEditable(true);
        rpTableMode.setListDetail(new ArrayList());
    }

    @Override
    public void save() {
        if (packingTableModel.isValidEntry() && isValidEntry()) {
            try {
                if (lblStatus.getText().equals("NEW")) {
                    List<RelationGroup> lstTmp = new ArrayList();
                    lstTmp.addAll(rpTableMode.getDetail());
                    currMedicine.setRelationGroupId(lstTmp);
                } else {
                    currMedicine.getRelationGroupId().removeAll(currMedicine.getRelationGroupId());
                    currMedicine.getRelationGroupId().addAll(rpTableMode.getDetail());
                }

                dao.open();
                dao.save(currMedicine);

                List<PackingItem> listPI = packingTableModel.getListPI();
                for (int i = 0; i < listPI.size() - 1; i++) {
                    dao.save(listPI.get(i));
                }
                
                deletePackingItem(packingTableModel.getDeleteId());
                newForm();
                assignPackingTable();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void history() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
    }

    @Override
    public void keyEvent(KeyEvent e) {
    }

    @Override
    public void selected(Object source, Object selectObj) {

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

                    String tmpMedSerial = maxMedId.toString().replace(medCode, "");
                    Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;
                    tmpMedSerial = tmpSerial.toString();

                    for (int i = 0; i < (leftLength - tmpMedSerial.length()); i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + tmpMedSerial;
                }
                dao.close();
            } catch (Exception ex) {
                log.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            }

        }

        return medCode;
    }

    private void initCombo() {
        BindingUtil.BindCombo(cboItemType, dao.findAll("ItemType"));
        BindingUtil.BindCombo(cboCategory, dao.findAll("Category"));
        BindingUtil.BindCombo(cboBrandName, dao.findAll("ItemBrand"));

        new ComBoBoxAutoComplete(cboItemType, this);
        new ComBoBoxAutoComplete(cboCategory, this);
        new ComBoBoxAutoComplete(cboBrandName, this);

        cboItemType.setSelectedItem(null);
        cboCategory.setSelectedItem(null);
        cboBrandName.setSelectedItem(null);
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtItemName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Item name must not be blank.",
                    "Item name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtItemName.requestFocusInWindow();
        } else if (cboItemType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "You must choose item type.",
                    "Item Type null error.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboItemType.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtItemName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Item Name cannot be blank.",
                    "Item Name null error.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtItemName.requestFocusInWindow();
        } else {

            currMedicine.setMedId(txtItemCode.getText());
            currMedicine.setMedTypeId((ItemType) cboItemType.getSelectedItem());
            currMedicine.setMedName(txtItemName.getText().trim());
            currMedicine.setCatId((Category) cboCategory.getSelectedItem());
            currMedicine.setBrand((ItemBrand) cboBrandName.getSelectedItem());
            currMedicine.setChemicalName(txtRemark.getText().trim());
            currMedicine.setActive(chkActive.isSelected());
            currMedicine.setBarcode(txtBarcode.getText());
            currMedicine.setShortName(txtShortName.getText());
            currMedicine.setTypeOption("PACKING");
            currMedicine.setRelStr(txtRelationString.getText());
            
            if (lblStatus.getText().equals("NEW")) {
                currMedicine.setCreatedBy(Global.loginUser);
            } else {
                currMedicine.setUpdatedBy(Global.loginUser);
                currMedicine.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }
        }

        return status;
    }

    private List<PackingItem> getPackingItem(String medId) {
        List<PackingItem> listPI = dao.findAllHSQL(
                "select o from PackingItem o where o.key.packingItemCode = '"
                + medId + "' order by o.uniqueId");
        return listPI;
    }

    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PackingItem pi;
            int yes_no = -1;

            if (tblPackingItem.getSelectedRow() >= 0) {
                int row = tblPackingItem.convertRowIndexToModel(tblPackingItem.getSelectedRow());
                List<PackingItem> listPI = packingTableModel.getListPI();
                
                pi = listPI.get(row);

                if (pi.getKey().getItem().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                                "Are you sure to delete?",
                                "Item delete", JOptionPane.YES_NO_OPTION);

                        if(tblPackingItem.getCellEditor() != null){
                            tblPackingItem.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                    }

                    if (yes_no == 0) {
                        packingTableModel.delete(row);
                    }
                }
            }
        }
    };
    
    private void deletePackingItem(String ids){
        if(!ids.isEmpty()){
            String sql = "delete from packing_item where packing_item_code = '" +
                    txtItemCode.getText() + "' and med_id in (" + ids + ")";
            dao.execSql(sql);
        }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPackingList = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtItemCode = new javax.swing.JTextField();
        cboItemType = new javax.swing.JComboBox();
        txtItemName = new javax.swing.JTextField();
        cboCategory = new javax.swing.JComboBox();
        cboBrandName = new javax.swing.JComboBox();
        txtRemark = new javax.swing.JTextField();
        txtBarcode = new javax.swing.JTextField();
        txtShortName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtRelationString = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPackingItem = new javax.swing.JTable();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRelation = new javax.swing.JTable();

        tblPackingList.setFont(Global.textFont);
        tblPackingList.setModel(itemTableModel);
        tblPackingList.setRowHeight(23);
        jScrollPane1.setViewportView(tblPackingList);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Item Code");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Item Type");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Item Name");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Brand Name");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Remark");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Barcode");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Short Name");

        txtItemCode.setEditable(false);
        txtItemCode.setFont(Global.textFont);

        cboItemType.setFont(Global.textFont);

        txtItemName.setFont(Global.textFont);
        txtItemName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtItemNameFocusLost(evt);
            }
        });

        cboCategory.setFont(Global.textFont);

        cboBrandName.setFont(Global.textFont);

        txtRemark.setFont(Global.textFont);

        txtBarcode.setFont(Global.textFont);

        txtShortName.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Relation");

        txtRelationString.setEditable(false);
        txtRelationString.setFont(Global.textFont);

        tblPackingItem.setFont(Global.textFont);
        tblPackingItem.setModel(packingTableModel);
        tblPackingItem.setRowHeight(23);
        jScrollPane2.setViewportView(tblPackingItem);

        chkActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActiveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        tblRelation.setFont(Global.textFont);
        tblRelation.setModel(rpTableMode);
        tblRelation.setRowHeight(23);
        jScrollPane3.setViewportView(tblRelation);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRelationString, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(txtShortName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblStatus)
                            .addComponent(chkActive))
                        .addGap(15, 15, 15))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtItemCode))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(cboItemType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtItemName))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(cboCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(cboBrandName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(txtRemark))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(txtBarcode)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtItemCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtShortName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(txtRelationString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkActive))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActiveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkActiveActionPerformed

    private void txtItemNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemNameFocusLost
        if (txtItemCode.getText().isEmpty()) {
            txtItemCode.setText(getMedCode(txtItemName.getText()));
            packingTableModel.addEmptyRow();
            packingTableModel.setSelectedMed(txtItemCode.getText());
            rpTableMode.addEmptyRow();
        }
    }//GEN-LAST:event_txtItemNameFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboBrandName;
    private javax.swing.JComboBox cboCategory;
    private javax.swing.JComboBox cboItemType;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblPackingItem;
    private javax.swing.JTable tblPackingList;
    private javax.swing.JTable tblRelation;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtItemCode;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtRelationString;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtShortName;
    // End of variables declaration//GEN-END:variables
}
