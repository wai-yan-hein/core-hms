/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.CompoundKeyMedOpDate;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.MedOpDate;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.StockOpeningDetailHis;
import com.cv.app.pharmacy.database.entity.StockOpeningHis;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.database.helper.StockExp;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.ItemCodeFilter;
import com.cv.app.pharmacy.database.tempentity.StockBalance;
import com.cv.app.pharmacy.database.tempentity.TmpMinusFixed;
import com.cv.app.pharmacy.database.tempentity.TmpMinusFixedKey;
import com.cv.app.pharmacy.database.tempentity.TmpStockOpeningDetailHisInsert;
import com.cv.app.pharmacy.database.view.VMedRel;
import com.cv.app.pharmacy.database.view.VStockOpToAcc;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.ItemCodeFilterTableModel;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.StockOpTableModel;
import com.cv.app.pharmacy.ui.common.StockTableModel;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.opencsv.CSVReader;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import javax.jms.MapMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueObjectException;

/**
 *
 * @author WSwe
 */
public class StockOpening extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(StockOpening.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    //private List<StockOpeningDetailHis> listDetail = new ArrayList();
    private MedicineUP medUp = new MedicineUP(dao);
    private BestAppFocusTraversalPolicy focusPolicy;
    private StockOpeningHis currStockOpening = new StockOpeningHis();
    private StockOpTableModel sopTableModel = new StockOpTableModel(dao, medUp, this);
    private StockTableModel stockTableModel = new StockTableModel();
    private ItemCodeFilterTableModel codeTableModel = new ItemCodeFilterTableModel(dao, false);
    private ItemCodeFilterTableModel codeTableModel2 = new ItemCodeFilterTableModel(dao, false);
    private List<StockBalance> listBalance;
    private List<StockOpeningDetailHis> listDetail;
    private boolean bindComplete = false;
    private TableRowSorter<TableModel> sorter;
    private int rowIndex = -1;

    //For op filter
    private String itemType;
    private Integer catId;
    private Integer brandId;
    private String strOPCodeFilter;
    //======================================

    private int mouseClick = 2;

    /**
     * Creates new form StockOpening
     */
    private int intRecLoc;
    private String strRecDate;

    public StockOpening() {
        initComponents();

        try {
            txtOpId.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("StockOpening : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        initOpTable();
        actionMapping();
        txtGroundDate.setText(DateUtil.getTodayDateStr());
        sopTableModel.setStrOpDate(txtGroundDate.getText());
        txtStockDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "StockOpening", DateUtil.getPeriod(txtGroundDate.getText()));
        genVouNo();
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        lblStatus.setText("NEW");
        intRecLoc = 0;
        strRecDate = "";
        initFilterTable();
        addCodeFilterModelListener();

        //sorter = new TableRowSorter(tblGround.getModel());
        //sorter = new TableRowSorter(tblGround.getModel());
        //tblGround.setRowSorter(sorter);
        try {
            dao.deleteSQL("delete from tmp_stock_op_detail_his where user_id = '"
                    + Global.machineId + "'");
        } catch (Exception ex) {
            log.error("StockOpening : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        String propValue = Util1.getPropValue("system.date.mouse.click");
        if (propValue != null) {
            if (!propValue.equals("-")) {
                if (!propValue.isEmpty()) {
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if (tmpValue != 0) {
                        mouseClick = tmpValue;
                    }
                }
            }
        }
    }

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtOpId.setText(vouNo);
    }

    private void clear() {
        //tblGround.setRowSorter(null);
        rowIndex = -1;
        lblStatus.setText("NEW");
        strRecDate = "";
        intRecLoc = 0;
        currStockOpening = new StockOpeningHis();
        sopTableModel.clear();
        codeTableModel2.clear();
        assignDefaultValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtGroundDate.getText()));
        genVouNo();
        setFocus();
        butCost.setEnabled(false);

        try {
            dao.deleteSQL("delete from tmp_stock_op_detail_his where user_id = '"
                    + Global.machineId + "'");
        } catch (Exception ex) {
            log.info("clear : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
        System.gc();
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboStockLocation, getLocationFilter());
            BindingUtil.BindComboFilter(cboItemType, dao.findAll("ItemType"));
            BindingUtil.BindComboFilter(cboItemType1, dao.findAll("ItemType"));
            BindingUtil.BindComboFilter(cboBrand, dao.findAll("ItemBrand"));
            BindingUtil.BindComboFilter(cboBrand1, dao.findAll("ItemBrand"));
            BindingUtil.BindComboFilter(cboCategory, dao.findAll("Category"));
            BindingUtil.BindComboFilter(cboCategory1, dao.findAll("Category"));

            new ComBoBoxAutoComplete(cboLocation, this);
            new ComBoBoxAutoComplete(cboStockLocation, this);
            new ComBoBoxAutoComplete(cboItemType, this);
            new ComBoBoxAutoComplete(cboItemType1, this);
            new ComBoBoxAutoComplete(cboBrand, this);
            new ComBoBoxAutoComplete(cboBrand1, this);
            new ComBoBoxAutoComplete(cboCategory, this);
            new ComBoBoxAutoComplete(cboCategory1, this);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.toString());
        } finally {
            dao.close();
        }
        bindComplete = true;
    }

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowStkOp = true) order by o.locationName");
            } else {
                return dao.findAll("Location");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.toString());
        } finally {
            dao.close();
        }
        return null;
    }

    private void actionMapping() {
        //F3 event
        tblGround.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblGround.getActionMap().put("F3-Action", actionMedList);

        //F8 event
        tblGround.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblGround.getActionMap().put("F8-Action", actionItemDelete);

        tblCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCodeFilter.getActionMap().put("F8-Action", actionItemFilterDelete);

        tblCodeFilter1.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCodeFilter1.getActionMap().put("F8-Action", actionItemFilterDelete1);

        //Enter event
        tblGround.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblGround.getActionMap().put("ENTER-Action", actionTblSopEnterKey);

        formActionKeyMapping(txtOpId);
        formActionKeyMapping(txtGroundDate);
        formActionKeyMapping(cboLocation);
        formActionKeyMapping(tblGround);
    }
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblGround.getCellEditor() != null) {
                    tblGround.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                } finally {
                    dao.close();
                }
            }
        }
    };

    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblGround.getSelectedRow() >= 0) {
                try {
                    if (tblGround.getCellEditor() != null) {
                        tblGround.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                rowIndex = -1;
                int row = tblGround.convertRowIndexToModel(tblGround.getSelectedRow());
                sopTableModel.delete(row);
            }
        }
    };

    @Override
    public void getMedInfo(String medCode) {
        try {
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                selected("MedicineList", medicine);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                        "Invalid.", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void getMedList(String filter) {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        String cusGroup = "-";
        /*if(currSaleVou.getCustomerId() != null){
            if(currSaleVou.getCustomerId().getTraderGroup() != null){
                cusGroup = currSaleVou.getCustomerId().getTraderGroup().getGroupId();
            }
        }*/
        MedListDialog1 dialog = new MedListDialog1(dao, filter, locationId, cusGroup);

        if (dialog.getSelect() != null) {
            selected("MedicineList", dialog.getSelect());
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        /*String strHSQL = "select o from TmpStockOpeningDetailHis o where o.userId = '"
         + Global.loginUser.getUserId() + "' order by o.uniqueId";*/

        try {
            dao.open();

            if (source.toString().equals("OpeningList")) {
                VoucherSearch vs = (VoucherSearch) selectObj;
                currStockOpening = (StockOpeningHis) dao.find(StockOpeningHis.class, vs.getInvNo());
                listDetail = dao.findAllHSQL(
                        "select o from StockOpeningDetailHis o where o.opId = '"
                        + vs.getInvNo() + "' order by o.uniqueId"
                );
                currStockOpening.setListDetail(listDetail);
                for (StockOpeningDetailHis dsodh : listDetail) {
                    try {
                        medUp.add(dsodh.getMed());
                    } catch (Exception ex) {
                        log.error("medUp : " + dsodh.getMed().getMedId() + " : " + ex.toString());
                    }
                }
                lblStatus.setText("EDIT");
                txtOpId.setText(currStockOpening.getStockOpHisId());
                txtGroundDate.setText(DateUtil.toDateStr(currStockOpening.getOpDate()));
                cboLocation.setSelectedItem(currStockOpening.getStockLocation());
                sopTableModel.setListDetail(listDetail);
                sopTableModel.setStrOpDate(txtGroundDate.getText());
                intRecLoc = currStockOpening.getStockLocation().getLocationId();
                strRecDate = DateUtil.toDateStr(currStockOpening.getOpDate());
                tblGround.requestFocusInWindow();
                butCost.setEnabled(true);
            } else if (source.toString().equals("MedicineList")) {
                Medicine med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                medUp.add(med);
                int selectRow = tblGround.convertRowIndexToModel(tblGround.getSelectedRow());
                sopTableModel.setMed(med, selectRow);
            }
        } catch (Exception ex) {
            log.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    /*private void addNewRow() {
     StockOpeningDetailHis his = new StockOpeningDetailHis();

     his.setMed(new Medicine());
     listDetail.add(his);
     }*/

 /*private boolean hasNewRow() {
     boolean status = false;
     StockOpeningDetailHis opDetailHis = listDetail.get(listDetail.size() - 1);

     String ID = opDetailHis.getMed().getMedId();
     if (ID == null) {
     status = true;
     }

     return status;
     }*/
    private void initOpTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblGround.setCellSelectionEnabled(true);
        }
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        sopTableModel.setParent(tblGround);
        sopTableModel.addEmptyRow();
        tblGround.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblGround.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        tblGround.getColumnModel().getColumn(1).setPreferredWidth(200);//Medicine Name
        tblGround.getColumnModel().getColumn(2).setPreferredWidth(80);//Relstr
        tblGround.getColumnModel().getColumn(3).setPreferredWidth(30);//Expire Date
        tblGround.getColumnModel().getColumn(4).setPreferredWidth(20);//Qty
        tblGround.getColumnModel().getColumn(5).setPreferredWidth(10);//Unit
        tblGround.getColumnModel().getColumn(6).setPreferredWidth(40);//Cost Price

        tblGround.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblGround.getColumnModel().getColumn(4).setCellEditor(
                new BestTableCellEditor(this));
        tblGround.getColumnModel().getColumn(6).setCellEditor(
                new BestTableCellEditor(this));
        tblGround.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblStock.getTableHeader().setFont(Global.lableFont);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(200);//Medicine Name
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(80);//Relstr
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(80);//Qty-Str
        tblStock.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        /*
         * JComboBox cboAdjType = new JComboBox(); BindingUtil.BindCombo(cboAdjType,
         * dao.findAll("AdjType"));
         * tblDamage.getColumnModel().getColumn(6).setCellEditor(new
         * DefaultCellEditor(cboAdjType));
         */
    }

    @Override
    public void save() {
        if (isValidEntry() && sopTableModel.isValidEntry()) {
            try {
                List<StockOpeningDetailHis> listOP = sopTableModel.getListDetail();
                String vouNo = currStockOpening.getStockOpHisId();
                dao.open();
                dao.beginTran();
                for (StockOpeningDetailHis sodh : listOP) {
                    sodh.setOpId(vouNo);
                    if (sodh.getOpDetailId() == null) {
                        sodh.setOpDetailId(vouNo + "-" + sodh.getUniqueId().toString());
                    }
                    dao.save1(sodh);
                }
                currStockOpening.setListDetail(listOP);
                dao.save1(currStockOpening);
                dao.commit();

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                String strSql = sopTableModel.getDeleteSql();
                dao.execSql(strSql);
                saveItemOpDate(listOP);
                newForm();
            } catch (Exception ex) {
                dao.rollBack();
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
        UtilDialog tmpDialog = new UtilDialog(Util1.getParent(), true, this,
                "Stock Opening Search", dao, -1);
        tmpDialog.setPreferredSize(new Dimension(1200, 600));
        tmpDialog.setLocationRelativeTo(null);
        tmpDialog.setVisible(true);
    }

    @Override
    public void delete() {
        try {
            Date vouSaleDate = DateUtil.toDate(txtGroundDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("Delete");
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Opening delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                //StockOpeningHis deleteOp = (StockOpeningHis) dao.find(StockOpeningHis.class, currStockOpening.getStockOpHisId());
                String opId = currStockOpening.getStockOpHisId();
                String deleteDetail = "delete from stock_op_detail_his where op_id = '" + opId + "'";
                String deleteHis = "delete from stock_op_his where op_id = '" + opId + "'";
                dao.execSql(deleteDetail, deleteHis);
                //dao.delete(deleteOp);

                newForm();
            }
        } catch (Exception ex) {
            log.error("delete : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    @Override
    public void deleteCopy() {
        System.out.println("Delete Copy");
    }

    @Override
    public void print() {
        System.out.println("Print");
    }
    // </editor-fold>

    public void setFocus() {
        tblGround.requestFocusInWindow();
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

    private boolean isValidEntry() {
        Date vouSaleDate = DateUtil.toDate(txtGroundDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean status = true;

        if (cboLocation.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose location.",
                    "No location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboLocation.requestFocusInWindow();
        } else {
            currStockOpening.setStockOpHisId(txtOpId.getText());
            currStockOpening.setOpDate(DateUtil.toDate(txtGroundDate.getText()));
            currStockOpening.setStockLocation((Location) cboLocation.getSelectedItem());

            currStockOpening.setSession(Global.sessionId);

            if (lblStatus.getText().equals("NEW")) {
                currStockOpening.setCreatedBy(Global.loginUser);
            } else {
                currStockOpening.setUpdatedBy(Global.loginUser);
                currStockOpening.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            String homeCurrency = Util1.getPropValue("system.app.currency");
            if (NumberUtil.NZeroL(currStockOpening.getExrId()) == 0) {
                Long exrId = getExchangeId(txtGroundDate.getText(), homeCurrency);
                currStockOpening.setExrId(exrId);
            }
            try {
                if (tblGround.getCellEditor() != null) {
                    tblGround.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }
        }

        return status;
    }

    private void assignDefaultValue() {
        txtGroundDate.setText(DateUtil.getTodayDateStr());
    }

    private Action actionTblSopEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblGround.getCellEditor() != null) {
                    tblGround.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblGround.getSelectedRow();
            int col = tblGround.getSelectedColumn();

            if (col == 0 && !sopTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 2 && !sopTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 3 && !sopTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && !sopTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(6, 6); //Move to Cost Price
            } else if (col == 5 && !sopTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(6, 6); //Move to Cost Price
            } else if (col == 6 && !sopTableModel.isEmptyRow(row)) {
                tblGround.setRowSelectionInterval(row + 1, row + 1);
                tblGround.setColumnSelectionInterval(0, 0); //Move to Code
            }
        }
    };

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

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //History
        jc.getInputMap().put(KeyStroke.getKeyStroke("F9"), "F9-Action");
        jc.getActionMap().put("F9-Action", actionHistory);

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
    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
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

    private void initFilterTable() {
        codeTableModel.addEmptyRow();
        tblCodeFilter.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblCodeFilter.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblCodeFilter.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));

        codeTableModel2.addEmptyRow();
        tblCodeFilter1.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblCodeFilter1.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblCodeFilter1.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void insertFilterCode() {
        int locationId = ((Location) cboStockLocation.getSelectedItem()).getLocationId();
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select " + locationId + ", m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from medicine m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where location_id = " + locationId + " and op_date <= '"
                + DateUtil.toDateStrMYSQL(txtStockDate.getText())
                + "' group by location_id, med_id) meod on m.med_id = meod.med_id "
                + "where m.active = true";

        /*
         * if(cboItemType.getSelectedItem() instanceof ItemType){ ItemType itemType
         * = (ItemType)cboItemType.getSelectedItem();
         *
         * strSQL = strSQL + " and m.med_type_id = '" + itemType.getItemTypeCode() +
         * "'"; }
         *
         * if(cboCategory.getSelectedItem() instanceof Category){ Category cat =
         * (Category)cboCategory.getSelectedItem();
         *
         * strSQL = strSQL + " and m.category_id = " + cat.getCatId(); }
         *
         * if(cboBrand.getSelectedItem() instanceof ItemBrand){ ItemBrand itemBrand
         * = (ItemBrand)cboBrand.getSelectedItem();
         *
         * strSQL = strSQL + " and m.brand_id = " + itemBrand.getBrandId(); }
         *
         * List<ItemCodeFilter> listCodeFilter = codeTableModel.getListCodeFilter();
         * String tmpStr = "";
         *
         * for(ItemCodeFilter filterCode : listCodeFilter){ if(filterCode.getKey()
         * != null){ if(tmpStr.length() > 0){ tmpStr = tmpStr + "'" +
         * filterCode.getKey().getItemCode().getMedId() + "'"; }else{ tmpStr = "'" +
         * filterCode.getKey().getItemCode().getMedId() + "'"; } } }
         *
         * if(!tmpStr.isEmpty()){ strSQL = strSQL + " and m.med_id in (" + tmpStr +
         * ")";
         }
         */
        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }
    private Action actionItemFilterDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblCodeFilter.getSelectedRow() >= 0) {
                codeTableModel.delete(tblCodeFilter.getSelectedRow());
            }
        }
    };
    private Action actionItemFilterDelete1 = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblCodeFilter1.getSelectedRow() >= 0) {
                codeTableModel2.delete(tblCodeFilter1.getSelectedRow());
            }
        }
    };

    private void saveItemOpDate(List<StockOpeningDetailHis> listOP) {
        int locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        Date opDate = DateUtil.toDate(txtGroundDate.getText());
        org.hibernate.Session session;

        try {
            dao.open();
            dao.beginTran();
            session = dao.getHibSession();

            for (int i = 0; i < listOP.size(); i++) {
                String strItemCode = listOP.get(i).getMed().getMedId();
                if (!strRecDate.equals("")) {
                    if (!DateUtil.toDate(strRecDate).equals(opDate)
                            || locationId != intRecLoc) {
                        dao.execSql("delete from med_op_date where location_id = " + intRecLoc + " "
                                + "and med_id = '" + strItemCode + "' and op_date = '" + DateUtil.toDateStrMYSQL(strRecDate) + "'");
                        dao.open();
                        dao.beginTran();
                        session = dao.getHibSession();
                    }
                }
                if (strItemCode != null) {
                    MedOpDate mod = new MedOpDate(new CompoundKeyMedOpDate(
                            locationId, strItemCode, opDate));
                    try {
                        session.saveOrUpdate(mod);
                    } catch (NonUniqueObjectException ex) {
                        session.merge(mod);
                    }
                }

                if (i % 10 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            session.flush();
            session.clear();
            dao.commit();
        } catch (Exception ex) {
            log.error("saveItemOpDate : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            dao.rollBack();
        } finally {
            dao.close();
        }
    }

    private void generateStockBalance() {
        try {
            dao.execSql("delete from tmp_stock_balance_exp where user_id = '"
                    + Global.machineId + "'");
            dao.execProc("stock_balance_exp", "Opening",
                    DateUtil.toDateStrMYSQL(txtStockDate.getText()),
                    ((Location) cboStockLocation.getSelectedItem()).getLocationId().toString(),
                    Global.machineId);
            listBalance = dao.findAll("StockBalance",
                    "key.userId = '" + Global.machineId + "'");
            dao.commit();
            applyStockBalanceFilter();
        } catch (Exception ex) {
            log.error("generateStockBalance : " + ex.toString());
        }
    }

    private void fixMinusBalance() {
        try {
            dao.deleteSQL("delete from tmp_stock_op_detail_his where user_id = '"
                    + Global.machineId + "'");
            //fixedMinus1(Global.machineId);
            fixedMinus(Global.machineId);
            getFixedResult();
        } catch (Exception ex) {
            log.error("fixMinusBalance : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void fixedMinus(String userId) {
        String strSql = "select location_id, med_id, exp_date, bal_qty\n"
                + "from tmp_stock_balance_exp\n"
                + "where user_id = '" + userId + "' and bal_qty <> 0\n"
                + "order by location_id, med_id, exp_date, bal_qty";
        String prvMedId = "-";

        try {
            dao.execSql("delete from tmp_minus_fixed where user_id = '" + userId + "'");
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<Stock> listMinusStock = new ArrayList();
                List<Stock> listPlusStock = new ArrayList();
                Medicine med = null;
                while (rs.next()) {
                    float qty = NumberUtil.FloatZero(rs.getInt("bal_qty"));
                    String medId = rs.getString("med_id");
                    int locationId = rs.getInt("location_id");

                    if (prvMedId.equals("-")) {
                        prvMedId = medId;
                        med = (Medicine) dao.find(Medicine.class, medId);
                    }

                    if (!prvMedId.equals(medId)) {
                        listPlusStock = PharmacyUtil.getStockList(listMinusStock, listPlusStock);
                        for (Stock s : listPlusStock) {
                            TmpMinusFixedKey key = new TmpMinusFixedKey();
                            key.setExpDate(s.getExpDate());
                            key.setItemId(s.getMed().getMedId());
                            key.setLocationId(s.getLocationId());
                            key.setUserId(userId);
                            TmpMinusFixed tmf = new TmpMinusFixed();
                            tmf.setKey(key);
                            tmf.setBalance(Math.round(s.getBalance()));
                            
                            dao.save(tmf);
                        }

                        listMinusStock = new ArrayList();
                        listPlusStock = new ArrayList();
                        med = (Medicine) dao.find(Medicine.class, medId);
                    }

                    if (qty < 0) {
                        Stock stock = new Stock(med, rs.getDate("exp_date"),
                                null, qty, null, null, locationId);
                        listMinusStock.add(stock);
                    } else {
                        Stock stock = new Stock(med, rs.getDate("exp_date"),
                                null, qty, null, null, locationId);
                        listPlusStock.add(stock);
                    }

                    prvMedId = medId;
                }
                
                if (!listPlusStock.isEmpty()) {
                    listPlusStock = PharmacyUtil.getStockList(listMinusStock, listPlusStock);
                    for (Stock s : listPlusStock) {
                        TmpMinusFixedKey key = new TmpMinusFixedKey();
                        key.setExpDate(s.getExpDate());
                        key.setItemId(s.getMed().getMedId());
                        key.setLocationId(s.getLocationId());
                        key.setUserId(userId);
                        TmpMinusFixed tmf = new TmpMinusFixed();
                        tmf.setKey(key);
                        tmf.setBalance(Math.round(s.getBalance()));

                        dao.save(tmf);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("fixedMinus : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void fixedMinus1(String userId) {
        String strSql = "select location_id, med_id, exp_date, bal_qty\n"
                + "from tmp_stock_balance_exp\n"
                + "where user_id = '" + userId + "' and bal_qty <> 0\n"
                + "order by location_id, med_id, bal_qty, exp_date";
        try {
            dao.execSql("delete from tmp_minus_fixed where user_id = '" + userId + "'");
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                HashMap<String, List<StockExp>> minusHM = new HashMap();
                String prvMedId = "-";
                while (rs.next()) {
                    float qty = NumberUtil.FloatZero(rs.getInt("bal_qty"));
                    String medId = rs.getString("med_id");
                    if (medId.equals("101119")) {
                        log.info("Med Id : " + medId + " Qty : " + qty);
                    }
                    if (prvMedId.equals("-")) {
                        prvMedId = medId;
                    }

                    if (!prvMedId.equals(medId)) {
                        //Need to insert left stock
                        List<StockExp> leftStockList = minusHM.get(prvMedId);
                        if (leftStockList != null) {
                            for (StockExp tmpSE : leftStockList) {
                                if (NumberUtil.FloatZero(tmpSE.getBalance()) != 0) {
                                    String strInsert = "insert into tmp_minus_fixed(location_id, item_id, exp_date, balance, user_id)\n"
                                            + " values(" + tmpSE.getLocationId() + ",'" + prvMedId + "','"
                                            + DateUtil.toDateStr(tmpSE.getExpDate(), "yyyy-MM-dd") + "',"
                                            + tmpSE.getBalance() + ", '" + userId + "')";
                                    dao.execSql(strInsert);
                                    //log.info("if (!prvMedId.equals(medId)) { : insert : " + prvMedId);
                                }
                            }
                            minusHM.remove(prvMedId);
                        }
                    }

                    List<StockExp> minusList = minusHM.get(medId);
                    if (qty < 0) {
                        if (minusList == null) {
                            minusList = new ArrayList();
                            minusHM.put(medId, minusList);
                        }

                        StockExp stock = new StockExp(medId, rs.getDate("exp_date"),
                                rs.getInt("location_id"), qty);
                        minusList.add(stock);
                    } else if (qty > 0) {
                        if (minusList == null) {
                            String strInsert = "insert into tmp_minus_fixed(location_id, item_id, exp_date, balance, user_id)\n"
                                    + " values(" + rs.getInt("location_id") + ",'" + medId + "','"
                                    + DateUtil.toDateStr(rs.getDate("exp_date"), "yyyy-MM-dd") + "',"
                                    + qty + ", '" + userId + "')";
                            dao.execSql(strInsert);
                            qty = 0;
                            //log.info("if (minusList == null) { : insert : " + medId);
                        } else {
                            if (minusList.isEmpty()) {

                            } else {
                                StockExp tmpStk = minusList.get(0);
                                float tmpBalance = tmpStk.getBalance();
                                if ((tmpBalance * -1) > qty) {
                                    float tmpQty = tmpBalance + qty;
                                    qty = 0;
                                    tmpStk.setBalance(tmpQty);
                                } else if ((tmpBalance * -1) == qty) {
                                    tmpStk.setBalance(0f);
                                    minusList.remove(0);
                                    qty = 0;
                                } else {
                                    minusList.remove(0);
                                    qty = tmpBalance + qty;
                                    if (!minusList.isEmpty() && qty > 0) {
                                        List<StockExp> listS = new ArrayList();
                                        listS.addAll(minusList);
                                        for (StockExp stk : listS) {
                                            tmpBalance = stk.getBalance();
                                            if ((tmpBalance * -1) > qty) {
                                                stk.setBalance(tmpBalance + qty);
                                                break;
                                            } else if ((tmpBalance * -1) == qty) {
                                                minusList.remove(0);
                                                qty = tmpBalance + qty;
                                                break;
                                            } else {
                                                minusList.remove(0);
                                                qty = tmpBalance + qty;
                                            }
                                        }
                                    }
                                }
                            }

                            if (qty != 0) {
                                String strInsert = "insert into tmp_minus_fixed(location_id, item_id, exp_date, balance, user_id)\n"
                                        + " values(" + rs.getInt("location_id") + ",'" + medId + "','"
                                        + DateUtil.toDateStr(rs.getDate("exp_date"), "yyyy-MM-dd") + "',"
                                        + qty + ", '" + userId + "')";
                                dao.execSql(strInsert);
                                //log.info("if (qty > 0) { insert : " + medId);
                            }
                        }
                    }

                    prvMedId = medId;
                }
            }
        } catch (Exception ex) {
            log.error("fixedMinus : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void getFixedResult() {
        try {
            String strSQL = "select v from TmpMinusFixed v where v.key.userId = '"
                    + Global.machineId + "' and balance <> 0 order by v.key.itemId, v.key.expDate desc";
            List<TmpMinusFixed> listTMF = dao.findAllHSQL(strSQL);

            if (listTMF != null) {
                if (!listTMF.isEmpty()) {
                    listDetail = new ArrayList();
                    String strNullDateVaue = "01/01/1900";
                    for (TmpMinusFixed tmf : listTMF) {
                        StockOpeningDetailHis sodh = new StockOpeningDetailHis();
                        Medicine m = (Medicine) dao.find(Medicine.class, tmf.getKey().getItemId());
                        medUp.add(m);
                        sodh.setMed(m);
                        ItemUnit iu = medUp.getSmallestUnit(m.getMedId());
                        sodh.setUnit(iu);
                        String strExpDate = DateUtil.toDateStr(tmf.getKey().getExpDate());
                        if (strExpDate.equals(strNullDateVaue)) {
                            sodh.setExpDate(null);
                        } else {
                            sodh.setExpDate(tmf.getKey().getExpDate());
                        }
                        sodh.setQty(tmf.getBalance().floatValue());
                        sodh.setCostPrice(0.0);
                        sodh.setSmallestQty(tmf.getBalance().floatValue());
                        listDetail.add(sodh);
                    }

                    sopTableModel.setListDetail(listDetail);
                    sopTableModel.setStrOpDate(txtGroundDate.getText());
                }
            }

            butCost.setEnabled(false);
            if (listDetail != null) {
                if (!listDetail.isEmpty()) {
                    butCost.setEnabled(true);
                }
            }
            /*List<TmpStockOpeningDetailHisInsert> listFixResult = dao.findAllHSQL(
                    "select o from TmpStockOpeningDetailHisInsert o where o.userId = '"
                    + Global.loginUser.getUserId() + "' order by o.medId"
            );*/
 /*if (listFixResult != null) {
                listDetail = new ArrayList();
                for (TmpStockOpeningDetailHisInsert oph : listFixResult) {
                    StockOpeningDetailHis sodh = new StockOpeningDetailHis();
                    Medicine m = (Medicine) dao.find(Medicine.class, oph.getMedId());
                    sodh.setMed(m);
                    ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class, oph.getUnit());
                    sodh.setUnit(iu);
                    sodh.setExpDate(oph.getExpDate());
                    sodh.setQty(oph.getQty().floatValue());
                    sodh.setCostPrice(oph.getCostPrice());
                    sodh.setSmallestQty(oph.getSmallestQty().floatValue());
                    listDetail.add(sodh);
                }

                sopTableModel.setListDetail(listDetail);
                sopTableModel.setStrOpDate(txtGroundDate.getText());
            }*/
        } catch (Exception ex) {
            log.error("getFixedResult : 2 : " + ex.toString());
        } finally {
            dao.close();
        }
        //listDetail.removeAll(listBalance);

    }

    private void insertTmpStockDetails(String medId, Date expDate,
            int ttlQty, double smallestCost) {
        try {
            String strSql = "select v from VMedRel v where v.key.medId = '"
                    + medId + "' order by v.key.uniqueId";
            List<VMedRel> listVMR = dao.findAllHSQL(strSql);

            if (ttlQty == 0) {
                TmpStockOpeningDetailHisInsert tsodh = new TmpStockOpeningDetailHisInsert();

                tsodh.setMedId(medId);
                tsodh.setExpDate(expDate);
                tsodh.setQty(ttlQty);
                tsodh.setCostPrice(listVMR.get(0).getSmallestQty() * smallestCost);
                tsodh.setUnit(listVMR.get(0).getUnitId());
                tsodh.setUserId(Global.machineId);
                tsodh.setSmallestQty(0);

                try {
                    dao.save(tsodh);
                } catch (Exception ex) {
                    log.error("insertTmpStockDetails : 1 : " + ex.toString());
                }
            } else {
                List<TmpStockOpeningDetailHisInsert> listTSODH = new ArrayList();
                int leftQty;
                boolean isMinus = false;

                if (ttlQty < 0) {
                    isMinus = true;
                    leftQty = -1 * ttlQty;
                } else {
                    leftQty = ttlQty;
                }

                for (int i = 0; i < listVMR.size(); i++) {
                    TmpStockOpeningDetailHisInsert tsodh = new TmpStockOpeningDetailHisInsert();
                    int unitQty = leftQty / listVMR.get(i).getSmallestQty();

                    tsodh.setMedId(medId);
                    tsodh.setExpDate(expDate);
                    tsodh.setCostPrice(listVMR.get(i).getSmallestQty() * smallestCost);
                    tsodh.setUnit(listVMR.get(i).getUnitId());
                    tsodh.setUserId(Global.machineId);

                    int ttlSmallestQty = listVMR.get(i).getSmallestQty() * unitQty;

                    if (isMinus) { //Minus qty
                        tsodh.setQty(unitQty * -1);
                        tsodh.setSmallestQty(ttlSmallestQty * -1);
                    } else {
                        tsodh.setQty(unitQty);
                        tsodh.setSmallestQty(ttlSmallestQty);
                    }

                    listTSODH.add(tsodh);
                    leftQty = leftQty - ttlSmallestQty;

                    if (leftQty == 0) {
                        i = listVMR.size();
                    }
                }

                try {
                    dao.saveBatch(listTSODH);
                } catch (Exception ex) {
                    log.error("insertTmpStockDetails : 2 : " + ex.toString());
                }
            }
        } catch (Exception ex) {
            log.error("insertTmpStockDetails : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private String getStockBalanceFilter() {
        String filter = "";

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and key.med.medTypeId.itemTypeCode = '"
                        + itemType.getItemTypeCode() + "'";
            } else {
                filter = filter + "key.med.medTypeId.itemTypeCode = '"
                        + itemType.getItemTypeCode() + "'";
            }
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and key.med.catId.catId = " + cat.getCatId();
            } else {
                filter = filter + "key.med.catId.catId = " + cat.getCatId();
            }
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and key.med.brand.brandId = "
                        + itemBrand.getBrandId();
            } else {
                filter = filter + "key.med.brand.brandId = "
                        + itemBrand.getBrandId();
            }
        }

        List<ItemCodeFilter> listCodeFilter = codeTableModel.getListCodeFilter();
        String tmpStr = "";

        for (ItemCodeFilter filterCode : listCodeFilter) {
            if (filterCode.getKey() != null) {
                if (tmpStr.length() > 0) {
                    tmpStr = tmpStr + "'" + filterCode.getKey().getItemCode().getMedId() + "'";
                } else {
                    tmpStr = "'" + filterCode.getKey().getItemCode().getMedId() + "'";
                }
            }
        }

        if (!tmpStr.isEmpty()) {
            if (!filter.isEmpty()) {
                filter = filter + " and key.med.medId in (" + tmpStr + ")";
            } else {
                filter = "key.med.medId in (" + tmpStr + ")";
            }
        }

        return filter;
    }

    private void applyStockBalanceFilter() {
        String className = "com.cv.app.pharmacy.database.tempentity.StockBalance";
        String strSql = "SELECT * FROM " + className;
        String strFilter = getStockBalanceFilter();

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        if (listBalance != null) {
            stockTableModel.setListDetail(JoSQLUtil.getResult(strSql, listBalance));
        }
    }

    private void applyFixStockBalanceFilter() {
        String className = "com.cv.app.pharmacy.database.entity.StockOpeningDetailHis";
        String strSql = "SELECT * FROM " + className;
        String strFilter = getStockBalanceFilter();

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        if (listDetail != null) {
            sopTableModel.setListDetail(JoSQLUtil.getResult(strSql, listDetail));
        }
    }

    private void addCodeFilterModelListener() {
        tblCodeFilter.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == 1 || e.getType() == -1) {
                    applyStockBalanceFilter();
                }
            }
        });

        tblCodeFilter1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == 1 || e.getType() == -1) {
                    //applyStockOpFilter();
                }
            }
        });
    }

    private String getStockOpFilter() {
        String filter = "";

        if (cboItemType1.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType1.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and o.med.medTypeId.itemTypeCode = '"
                        + itemType.getItemTypeCode() + "'";
            } else {
                filter = filter + "o.med.medTypeId.itemTypeCode = '"
                        + itemType.getItemTypeCode() + "'";
            }
        }

        if (cboCategory1.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory1.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and o.med.catId.catId = " + cat.getCatId();
            } else {
                filter = filter + "o.med.catId.catId = " + cat.getCatId();
            }
        }

        if (cboBrand1.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand1.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and o.med.brand.brandId = "
                        + itemBrand.getBrandId();
            } else {
                filter = filter + "o.med.brand.brandId = "
                        + itemBrand.getBrandId();
            }
        }

        List<ItemCodeFilter> listCodeFilter = codeTableModel2.getListCodeFilter();
        String tmpStr = "";

        for (ItemCodeFilter filterCode : listCodeFilter) {
            if (filterCode.getKey() != null) {
                if (tmpStr.length() > 0) {
                    tmpStr = tmpStr + "'" + filterCode.getKey().getItemCode().getMedId() + "'";
                } else {
                    tmpStr = "'" + filterCode.getKey().getItemCode().getMedId() + "'";
                }
            }
        }

        if (!tmpStr.isEmpty()) {
            if (!filter.isEmpty()) {
                filter = filter + " and o.med.medId in (" + tmpStr + ")";
            } else {
                filter = "o.med.medId in (" + tmpStr + ")";
            }
        }

        return filter;
    }

    private String getStockOpFilterJOSQL() {
        String filter = "";

        if (cboItemType1.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType1.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and med.medTypeId.itemTypeCode = '"
                        + itemType.getItemTypeCode() + "'";
            } else {
                filter = filter + "med.medTypeId.itemTypeCode = '"
                        + itemType.getItemTypeCode() + "'";
            }
        }

        if (cboCategory1.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory1.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and med.catId.catId = " + cat.getCatId();
            } else {
                filter = filter + "med.catId.catId = " + cat.getCatId();
            }
        }

        if (cboBrand1.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand1.getSelectedItem();

            if (!filter.isEmpty()) {
                filter = filter + " and med.brand.brandId = "
                        + itemBrand.getBrandId();
            } else {
                filter = filter + "med.brand.brandId = "
                        + itemBrand.getBrandId();
            }
        }

        List<ItemCodeFilter> listCodeFilter = codeTableModel2.getListCodeFilter();
        String tmpStr = "";

        for (ItemCodeFilter filterCode : listCodeFilter) {
            if (filterCode.getKey() != null) {
                if (tmpStr.length() > 0) {
                    tmpStr = tmpStr + "'" + filterCode.getKey().getItemCode().getMedId() + "'";
                } else {
                    tmpStr = "'" + filterCode.getKey().getItemCode().getMedId() + "'";
                }
            }
        }

        if (!tmpStr.isEmpty()) {
            if (!filter.isEmpty()) {
                filter = filter + " and med.medId in (" + tmpStr + ")";
            } else {
                filter = "med.medId in (" + tmpStr + ")";
            }
        }

        return filter;
    }

    private void applyStockOpFilter() {
        /*String strHSQL = "select o from TmpStockOpeningDetailHis o where o.userId = '"
         + Global.loginUser.getUserId() + "'";
         String strFilter = getStockOpFilter();
         if (!strFilter.isEmpty()) {
         strHSQL = strHSQL + " and " + strFilter + " order by uniqueId";
         }
         List<StockOpeningDetailHis> list = dao.findAllHSQL(strHSQL);*/
 /*String filterString = "SELECT * FROM com.cv.app.pharmacy.database.entity.StockOpeningDetailHis ";
         String strWhere = getStockOpFilterJOSQL();
         filterString = filterString + " where " + strWhere;
         List<StockOpeningDetailHis> list = JoSQLUtil.getResult(
         filterString,
         currStockOpening.getListDetail());
         if (list != null) {
         sopTableModel.setListDetail(list);
         } else {
         sopTableModel.clear();
         sopTableModel.addEmptyRow();
         }*/
 /*String className = "com.cv.app.pharmacy.database.entity.StockOpeningDetailHis";
        String strSql = "SELECT * FROM " + className;
        String strFilter = getStockOpFilterJOSQL();
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }
        
        listDetail = currStockOpening.getListDetail();
        if (listDetail != null) {
            sopTableModel.setListDetail(JoSQLUtil.getResult(strSql, listDetail));
        }*/

        if (cboItemType1.getSelectedItem() instanceof ItemType) {
            itemType = ((ItemType) cboItemType1.getSelectedItem()).getItemTypeCode();
        } else {
            itemType = "-";
        }

        if (cboCategory1.getSelectedItem() instanceof Category) {
            catId = ((Category) cboCategory1.getSelectedItem()).getCatId();
        } else {
            catId = -1;
        }

        if (cboBrand1.getSelectedItem() instanceof ItemBrand) {
            brandId = ((ItemBrand) cboBrand1.getSelectedItem()).getBrandId();
        } else {
            brandId = -1;
        }

        strOPCodeFilter = "";
        List<ItemCodeFilter> listCodeFilter = codeTableModel2.getListCodeFilter();
        for (ItemCodeFilter filterCode : listCodeFilter) {
            if (filterCode.getKey() != null) {
                if (strOPCodeFilter.isEmpty()) {
                    strOPCodeFilter = filterCode.getKey().getItemCode().getMedId();
                } else {
                    strOPCodeFilter = strOPCodeFilter + "," + filterCode.getKey().getItemCode().getMedId();
                }
            }
        }

        rowIndex = -1;
        if (tblGround.getRowSorter() == null) {
            tblGround.setRowSorter(sorter);
        }

        sorter.setRowFilter(rowFilter);
    }

    private void fillZero() {
        List<StockOpeningDetailHis> listTDetail = sopTableModel.getListDetail();

        try {
            Integer locationId = -1;
            if (cboLocation.getSelectedItem() != null) {
                locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }
            List<Medicine> listMed = dao.findAllHSQL(
                    "select o from Medicine o where o.medId not in (select distinct o.medId from VStockOpening o "
                    + "where o.opDate = '" + DateUtil.toDateStrMYSQL(txtGroundDate.getText())
                    + "' and o.locationId = " + locationId + ")");
            if (listMed != null) {
                if (!listMed.isEmpty()) {
                    //listTDetail.remove(listTDetail.size() - 1);//Remove empty row
                    int uniqueId = 1;
                    for (Medicine med : listMed) {
                        StockOpeningDetailHis tsodh = new StockOpeningDetailHis();
                        tsodh.setMed(med);
                        tsodh.setQty(0f);
                        tsodh.setSmallestQty(0f);
                        ItemUnit iu = getUnit(med);
                        tsodh.setUnit(iu);
                        tsodh.setExpDate(null);
                        tsodh.setCostPrice(0.0);
                        tsodh.setUniqueId(uniqueId);
                        listTDetail.add(tsodh);
                        uniqueId++;
                    }

                    currStockOpening.setListDetail(listTDetail);
                    sopTableModel.setListDetail(listTDetail);
                }
            }
        } catch (Exception ex) {
            log.error("fillZero : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //applyStockOpFilter();
    }

    private void processCSV(File file) {
        if (file != null) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                try ( CSVReader csvReader = new CSVReader(reader)) {
                    String[] nextRecord;
                    int ttlRec = 0;

                    log.info("processCSV Start");
                    List<StockOpeningDetailHis> listDetail = new ArrayList();
                    while ((nextRecord = csvReader.readNext()) != null) {
                        //String medId = getNewMedCode(nextRecord[0]);
                        Date expDate = DateUtil.toDate(nextRecord[0], "dd/MM/yyyy");
                        String medId = nextRecord[1];
                        float smallestQty = Float.valueOf(nextRecord[2]);
                        Medicine med = (Medicine) dao.find(Medicine.class, medId);
                        if (med != null) {
                            StockOpeningDetailHis tsodh = new StockOpeningDetailHis();
                            tsodh.setMed(med);
                            tsodh.setQty(smallestQty);
                            tsodh.setSmallestQty(smallestQty);
                            ItemUnit iu = getUnit(med);
                            tsodh.setUnit(iu);
                            tsodh.setExpDate(expDate);
                            //tsodh.setCostPrice(costPrice);
                            listDetail.add(tsodh);
                        }
                    }
                    sopTableModel.setListDetail(listDetail);
                    log.info("processCSV End: " + ttlRec);
                }
            } catch (IOException | NumberFormatException ex) {
                log.error("processCSV : " + ex.getMessage());
            } catch (Exception ex) {
                log.error("processCSV : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void processCSVCost(File file) {
        if (file != null) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                try ( CSVReader csvReader = new CSVReader(reader)) {
                    String[] nextRecord;
                    int ttlRec = 0;

                    log.info("processCSV Start");
                    List<StockOpeningDetailHis> listDetail = new ArrayList();
                    while ((nextRecord = csvReader.readNext()) != null) {
                        //String medId = getNewMedCode(nextRecord[0]);
                        String medId = nextRecord[0];
                        Float smallestQty = Float.parseFloat(nextRecord[1].replace(",", ""));
                        Double costPrice = 0.0;
                        Date expDate = null;
                        if (!nextRecord[2].isEmpty() && !nextRecord[2].equals("-")) {
                            //costPrice = Double.parseDouble(nextRecord[2].replace(",", ""));
                            expDate = DateUtil.toDate(nextRecord[2], "dd/MM/yy");
                        }
                        ttlRec++;
                        log.info("getNewMedCode : " + medId + " Qty : " + smallestQty.toString()
                                + " Cnt : " + ttlRec);

                        Medicine med = (Medicine) dao.find(Medicine.class, medId);
                        if (med != null) {
                            StockOpeningDetailHis tsodh = new StockOpeningDetailHis();
                            tsodh.setMed(med);
                            tsodh.setQty(smallestQty);
                            tsodh.setSmallestQty(smallestQty);
                            ItemUnit iu = getUnit(med);
                            tsodh.setUnit(iu);
                            tsodh.setExpDate(expDate);
                            tsodh.setCostPrice(costPrice);
                            listDetail.add(tsodh);
                        }
                    }
                    sopTableModel.setListDetail(listDetail);
                    log.info("processCSV End: " + ttlRec);
                }
            } catch (Exception ex) {
                log.error("processCSV : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private String getNewMedCode(String medId) {
        medId = "0" + medId;
        /*String left = medId.substring(0,4);
         String right = medId.replace(left, "");
         medId = left + "0" + right;*/

        return medId;
    }

    private ItemUnit getUnit(Medicine med) {
        List<RelationGroup> listRG = med.getRelationGroupId();
        ItemUnit iu = null;
        if (listRG != null) {
            int cnt = listRG.size();
            if (cnt > 0) {
                iu = listRG.get(cnt - 1).getUnitId();
            }
        }

        return iu;
    }

    private void updateCost() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processCSV(selectedFile);
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    private final RowFilter<Object, Object> rowFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            boolean isItemType = false;
            boolean isCategory = false;
            boolean isItemBrand = false;
            boolean isCode = false;

            rowIndex++;
            log.info("Row Index : " + rowIndex);
            if (listDetail != null) {
                if (listDetail.size() > rowIndex) {
                    StockOpeningDetailHis sodh = listDetail.get(rowIndex);
                    if (sodh.getMed() != null) {
                        if (sodh.getMed().getMedId() == null) {
                            isItemType = true;
                            isCategory = true;
                            isItemBrand = true;
                            isCode = true;
                        } else {
                            if (itemType.equals("-")) {
                                isItemType = true;
                            } else if (sodh.getMed().getMedTypeId() != null) {
                                if (sodh.getMed().getMedTypeId().getItemTypeCode() != null) {
                                    isItemType = itemType.equals(sodh.getMed().getMedTypeId().getItemTypeCode());
                                } else {
                                    isItemType = false;
                                }
                            } else {
                                isItemType = false;
                            }

                            if (catId == -1) {
                                isCategory = true;
                            } else if (sodh.getMed().getCatId() != null) {
                                isCategory = Objects.equals(catId, sodh.getMed().getCatId().getCatId());
                            } else {
                                isCategory = false;
                            }

                            if (brandId == -1) {
                                isItemBrand = true;
                            } else if (sodh.getMed().getBrand() != null) {
                                isItemBrand = Objects.equals(brandId, sodh.getMed().getBrand().getBrandId());
                            } else {
                                isItemBrand = false;
                            }

                            if (strOPCodeFilter.isEmpty()) {
                                isCode = true;
                            } else {
                                if (strOPCodeFilter.contains(sodh.getMed().getMedId())) {
                                    isCode = true;
                                } else {
                                    isCode = false;
                                }
                            }
                        }
                    } else {
                        isItemType = true;
                        isCategory = true;
                        isItemBrand = true;
                        isCode = true;
                    }
                }
            } else {
                isItemType = true;
                isCategory = true;
                isItemBrand = true;
                isCode = true;
            }

            return isItemType && isCategory && isItemBrand && isCode;
        }
    };

    private Long getExchangeId(String strDate, String curr) {
        long id = 0;
        if (Util1.getPropValue("system.multicurrency").equals("Y")) {
            try {
                Object value = dao.getMax("exr_id", "exchange_rate",
                        "(to_curr = '" + curr + "' or from_curr = '" + curr
                        + "') and date(created_date) = '"
                        + DateUtil.toDateStrMYSQL(strDate)
                        + "'"
                );
                if (value != null) {
                    id = NumberUtil.NZeroL(value);
                }
            } catch (Exception ex) {
                log.error("getExchangeId : " + ex.getMessage());
            }
        }
        return id;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtStockDate = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        cboStockLocation = new javax.swing.JComboBox();
        butStocck = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cboItemType = new javax.swing.JComboBox();
        cboCategory = new javax.swing.JComboBox();
        cboBrand = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCodeFilter = new javax.swing.JTable();
        butFix = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtOpId = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txtGroundDate = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGround = new javax.swing.JTable(sopTableModel);
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cboItemType1 = new javax.swing.JComboBox();
        cboCategory1 = new javax.swing.JComboBox();
        cboBrand1 = new javax.swing.JComboBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCodeFilter1 = new javax.swing.JTable();
        butFillZero = new javax.swing.JButton();
        btnToAcc = new javax.swing.JButton();
        butCost = new javax.swing.JButton();
        butCSV = new javax.swing.JButton();

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date ");

        txtStockDate.setEditable(false);
        txtStockDate.setFont(Global.textFont);
        txtStockDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtStockDateMouseClicked(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Location ");

        cboStockLocation.setFont(Global.textFont);

        butStocck.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        butStocck.setText("Stock");
        butStocck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStocckActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Item Type ");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Category ");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Brand ");

        cboItemType.setFont(Global.textFont);
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

        cboBrand.setFont(Global.textFont);
        cboBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBrandActionPerformed(evt);
            }
        });

        tblCodeFilter.setFont(Global.textFont);
        tblCodeFilter.setModel(codeTableModel);
        tblCodeFilter.setRowHeight(23);
        jScrollPane2.setViewportView(tblCodeFilter);

        butFix.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        butFix.setText("Fix");
        butFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFixActionPerformed(evt);
            }
        });

        tblStock.setFont(Global.textFont);
        tblStock.setModel(stockTableModel);
        tblStock.setRowHeight(23);
        jScrollPane3.setViewportView(tblStock);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboStockLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStockDate, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(butStocck, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFix)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel6, jLabel7, jLabel8});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butFix, butStocck});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel5});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboBrand, cboCategory, cboItemType});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboStockLocation, txtStockDate});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtStockDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cboStockLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butStocck)
                            .addComponent(jLabel8)
                            .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(butFix))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
        );

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Op-ID");

        txtOpId.setEditable(false);
        txtOpId.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Location");

        cboLocation.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date");

        txtGroundDate.setEditable(false);
        txtGroundDate.setFont(Global.textFont);
        txtGroundDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtGroundDateMouseClicked(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("Ground Entry");

        tblGround.setFont(Global.textFont);
        tblGround.setModel(sopTableModel);
        tblGround.setRowHeight(23);
        tblGround.setShowVerticalLines(false);
        tblGround.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblGroundFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblGround);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Item Type ");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Category ");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Brand ");

        cboItemType1.setFont(Global.textFont);
        cboItemType1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboItemType1ActionPerformed(evt);
            }
        });

        cboCategory1.setFont(Global.textFont);
        cboCategory1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategory1ActionPerformed(evt);
            }
        });

        cboBrand1.setFont(Global.textFont);
        cboBrand1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBrand1ActionPerformed(evt);
            }
        });

        tblCodeFilter1.setFont(Global.textFont);
        tblCodeFilter1.setModel(codeTableModel2);
        tblCodeFilter1.setRowHeight(23);
        jScrollPane4.setViewportView(tblCodeFilter1);

        butFillZero.setText("Fill Zero");
        butFillZero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFillZeroActionPerformed(evt);
            }
        });

        btnToAcc.setText("To  Acc");
        btnToAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToAccActionPerformed(evt);
            }
        });

        butCost.setText("Cost");
        butCost.setEnabled(false);
        butCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCostActionPerformed(evt);
            }
        });

        butCSV.setText("CSV");
        butCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtGroundDate)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtOpId, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(butFillZero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnToAcc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCost)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCSV)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboItemType1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboCategory1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboBrand1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel4});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel9});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboLocation, txtGroundDate, txtOpId});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtOpId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel9)
                            .addComponent(cboItemType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtGroundDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(cboCategory1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(cboBrand1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butFillZero)
                    .addComponent(btnToAcc)
                    .addComponent(butCost)
                    .addComponent(butCSV))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtGroundDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtGroundDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtGroundDate.setText(strDate);
                sopTableModel.setStrOpDate(strDate);

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtGroundDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtGroundDateMouseClicked

  private void txtStockDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtStockDateMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtStockDate.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtStockDateMouseClicked

  private void butStocckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStocckActionPerformed
      //this.setCursor(Util1.getWaitCursor());
      insertFilterCode();
      generateStockBalance();
      //applyStockBalanceFilter();
      //this.setCursor(Util1.getDefaultCursor());
  }//GEN-LAST:event_butStocckActionPerformed

    private void cboItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemTypeActionPerformed
        if (bindComplete) {
            applyStockBalanceFilter();
        }
    }//GEN-LAST:event_cboItemTypeActionPerformed

    private void cboCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoryActionPerformed
        if (bindComplete) {
            applyStockBalanceFilter();
        }
    }//GEN-LAST:event_cboCategoryActionPerformed

    private void cboBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBrandActionPerformed
        if (bindComplete) {
            applyStockBalanceFilter();
        }
    }//GEN-LAST:event_cboBrandActionPerformed

    private void cboItemType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemType1ActionPerformed
        if (bindComplete) {
            applyStockOpFilter();
        }
    }//GEN-LAST:event_cboItemType1ActionPerformed

    private void cboCategory1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategory1ActionPerformed
        if (bindComplete) {
            applyStockOpFilter();
        }
    }//GEN-LAST:event_cboCategory1ActionPerformed

    private void cboBrand1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBrand1ActionPerformed
        if (bindComplete) {
            applyStockOpFilter();
        }
    }//GEN-LAST:event_cboBrand1ActionPerformed

  private void butFixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFixActionPerformed
      //this.setCursor(Util1.getWaitCursor());
      fixMinusBalance();
      //this.setCursor(Util1.getDefaultCursor());
  }//GEN-LAST:event_butFixActionPerformed

    private void butFillZeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFillZeroActionPerformed
        fillZero();
    }//GEN-LAST:event_butFillZeroActionPerformed

    private void btnToAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToAccActionPerformed
        String isIntegration = Util1.getPropValue("system.integration");
        try {
            if (isIntegration.toUpperCase().equals("Y")) {
                List<VStockOpToAcc> listStockOp = dao.findAllHSQL("select o from VStockOpToAcc o");
                if (listStockOp.size() > 0) {
                    for (int i = 0; i < listStockOp.size(); i++) {
                        if (Global.mqConnection != null) {
                            if (Global.mqConnection.isStatus()) {
                                try {
                                    ActiveMQConnection mq = Global.mqConnection;
                                    MapMessage msg = mq.getMapMessageTemplate();
                                    msg.setString("program", Global.programId);
                                    msg.setString("entity", "OPENING-STOCK");
                                    msg.setString("sourceAccId", listStockOp.get(i).getAccountId());
                                    msg.setBoolean("deleted", false);
                                    msg.setDouble("opAmount", listStockOp.get(i).getAmount());

                                    msg.setString("queueName", "INVENTORY");

                                    mq.sendMessage(Global.queueName, msg);
                                } catch (Exception ex) {
                                    log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + listStockOp + " - " + ex);
                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception ex) {
            log.error("btnToAccActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_btnToAccActionPerformed

    private void butCostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCostActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        sopTableModel.reAssignCost(txtOpId.getText());
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butCostActionPerformed

    private void tblGroundFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblGroundFocusLost
        /*try{
         if(tblGround.getCellEditor() != null){
         tblGround.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblGroundFocusLost

    private void butCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCSVActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processCSV(selectedFile);
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }//GEN-LAST:event_butCSVActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnToAcc;
    private javax.swing.JButton butCSV;
    private javax.swing.JButton butCost;
    private javax.swing.JButton butFillZero;
    private javax.swing.JButton butFix;
    private javax.swing.JButton butStocck;
    private javax.swing.JComboBox cboBrand;
    private javax.swing.JComboBox cboBrand1;
    private javax.swing.JComboBox cboCategory;
    private javax.swing.JComboBox cboCategory1;
    private javax.swing.JComboBox cboItemType;
    private javax.swing.JComboBox cboItemType1;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboStockLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCodeFilter;
    private javax.swing.JTable tblCodeFilter1;
    private javax.swing.JTable tblGround;
    private javax.swing.JTable tblStock;
    private javax.swing.JFormattedTextField txtGroundDate;
    private javax.swing.JFormattedTextField txtOpId;
    private javax.swing.JFormattedTextField txtStockDate;
    // End of variables declaration//GEN-END:variables
}
