/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.cellrenderer.APriceRenderer;
import com.cv.app.cellrenderer.BPriceRenderer;
import com.cv.app.cellrenderer.CPriceRenderer;
import com.cv.app.cellrenderer.DPriceRenderer;
import com.cv.app.cellrenderer.NormalPriceRenderer;
import com.cv.app.pharmacy.database.entity.PriceChangeMedHis1;
import com.cv.app.pharmacy.database.entity.PriceChangeHis1;
import com.cv.app.pharmacy.database.entity.PriceChangeUnitHis1;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.LocationItemMapping;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.pharmacy.database.tempentity.StockCostingDetail;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.PChangeMedTableModel1;
import com.cv.app.pharmacy.ui.common.PChangeUnitTableModel1;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.StockCostingDetailTableModel;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.ColumnGroup;
import com.cv.app.ui.common.GroupableTableHeader;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * Damage entry use for medicine price change. This class is using in
 * PriceChangeView class as panel.
 */
public class PriceChange1 extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(PriceChange1.class.getName());
    private final AbstractDataAccess dao = Global.dao; //Use for database access
    private GenVouNoImpl vouEngine = null; //Use for voucher number generation
    //listDetail use in tblMedicine
    private List<PriceChangeMedHis1> listDetail = new ArrayList();
    //listDetailUnit use in tblUnit
    private List<PriceChangeUnitHis1> listDetailUnit = new ArrayList();
    private MedicineUP medUp = new MedicineUP(dao); //Use for unit popup
    private BestAppFocusTraversalPolicy focusPolicy; //For focus managment
    private PriceChangeHis1 currPriceChange = new PriceChangeHis1(); //Current PriceChange entry class
    //Model for tblUnit
    private PChangeUnitTableModel1 unitTableModel = new PChangeUnitTableModel1(listDetailUnit);
    //Model for tblMedicine
    private PChangeMedTableModel1 medTableModel = new PChangeMedTableModel1(listDetail, dao,
            medUp, this, listDetailUnit, unitTableModel);
    private String selectedMedId = "";
    private final StockCostingDetailTableModel modelSCDT = new StockCostingDetailTableModel();
    private int mouseClick = 2;

    /**
     * Creates new form PriceChange
     */
    public PriceChange1() {
        initComponents();

        //formatting txtVouNo text box.
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
        } catch (Exception ex) {
            log.error("PriceChange : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        txtDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "PriceChange", DateUtil.getPeriod(txtDate.getText()));
        genVouNo();
        addNewRow();
        initMedicineTable();
        initUnitTable();
        initCombo();
        actionMapping();

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);

        medTableModel.setParent(tblMedicine);
        unitTableModel.setParent(tblUnit);
        unitTableModel.setAutoCalculate(chkCalculate);

        lblStatus.setText("NEW");
        lblInfo.setText("");
        chkFillWithCostPrice.setSelected(true);

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

    public PriceChange1(String selectedMedId) {
        this();
        this.selectedMedId = selectedMedId;
        getSelectedItem(selectedMedId);

        if (tblMedicine.getRowCount() > 0) {
            tblMedicine.setRowSelectionInterval(0, 0);
        }

        if (tblUnit.getRowCount() > 0) {
            tblUnit.setRowSelectionInterval(0, 0);
        }
    }
    //Get latest Damage voucher serial number

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();

        txtVouNo.setText(vouNo);
    }

    //Clearing the whole form. Calling from newForm method
    private void clear() {
        try {
            if (tblMedicine.getCellEditor() != null) {
                tblMedicine.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }

        selectedMedId = "";
        txtRemark.setText("");
        lblStatus.setText("NEW");
        txtPurPrice.setText("");
        txtCostPrice.setText("");
        txtSPercent.setText("");
        txtPurFrom.setText("");
        txtPurTo.setText("");
        cboItemType.setSelectedIndex(0);
        cboCategory.setSelectedIndex(0);
        cboItemBrand.setSelectedIndex(0);
        cboLocation.setSelectedIndex(0);

        txtAPercent.setText("");
        txtBPercent.setText("");
        txtCPercent.setText("");
        txtDPercent.setText("");
        txtEPercent.setText("");
        txtFrom.setText("");
        txtTo.setText("");

        assignDefaultValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
        genVouNo();
        setFocus();
        modelSCDT.removeAll();
        System.gc();
    }

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    /*
     * Function key mapping
     */
    private void actionMapping() {
        /*
         * F3 event on tblMedicine
         * Medicine list popup dialoug will show
         */
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblMedicine.getActionMap().put("F3-Action", actionMedList);

        /*
         * F8 event on tblMedicine
         * Delete row in tblMedicine table
         */
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblMedicine.getActionMap().put("F8-Action", actionItemDelete);

        /*
         * Enter event on tblMedicine
         * For tblMedicine focus managment
         */
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblMedicine.getActionMap().put("ENTER-Action", actionTblMedEnterKey);

        tblUnit.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblUnit.getActionMap().put("ENTER-Action", actionTblUnitEnterKey);

        //Med selection movement
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK);
        tblUnit.getInputMap().put(keyStroke, "Ctrl-Down-Action");
        tblUnit.getActionMap().put("Ctrl-Down-Action", actionCtrlDownChange);

        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK);
        tblUnit.getInputMap().put(keyStroke, "Ctrl-UP-Action");
        tblUnit.getActionMap().put("Ctrl-UP-Action", actionCtrlUpChange);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtDate);
        formActionKeyMapping(butFill);
        formActionKeyMapping(tblMedicine);
        formActionKeyMapping(chkCalculate);
        formActionKeyMapping(tblUnit);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionMedList">
    /*
     * This function will called when pressing function key F3
     * in tblMedicine.
     */
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblMedicine.getCellEditor() != null) {
                    tblMedicine.getCellEditor().stopCellEditing();
                }
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("eactionMedList : " + ex1.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3

            }
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionItemDelete">
    /*
     * This function will called when pressing function key F8
     * in tblMedicine.
     */
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PriceChangeMedHis1 pcmdh;
            int yes_no = -1;

            if (tblMedicine.getSelectedRow() >= 0) {
                pcmdh = listDetail.get(tblMedicine.getSelectedRow());

                if (pcmdh.getMed().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Adjust item delete", JOptionPane.YES_NO_OPTION);
                        if (tblMedicine.getCellEditor() != null) {
                            tblMedicine.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                    if (yes_no == 0) {
                        medTableModel.delete(tblMedicine.getSelectedRow());
                    }
                }
            }
        }
    };// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMedInfo">
    /*
     * This function will called when pressing F3 or
     * entering medicine code in code field in tblMedicine.
     * When medicine code cannot find in database and 
     * medicine list popup will be show.
     */
    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                + medCode + "' and active = true");

        if (medicine != null) {
            selected("MedicineList", medicine);
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                    "Invalid.", JOptionPane.ERROR_MESSAGE);
            //getMedList(medCode);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMedList">
    /*
     * This function showing medicine list dialouge.
     */
    private void getMedList(String filter) {
        int locationId = -1;
        /*if(cboLocation.getSelectedItem() instanceof Location){
            locationId = ((Location)cboLocation.getSelectedItem()).getLocationId();
        }*/
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
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="selected">
    /*
     * This function will be call from when user select medicine from 
     * medicine list popup or select history data from Price Change Search 
     * dialouge.
     */
    @Override
    public void selected(Object source, Object selectObj) {
        String strSource = source.toString();

        try {
            dao.open();
            switch (strSource) {
                case "MedicineList":
                    Medicine selectedMed = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                    if (!selectedMed.getRelationGroupId().isEmpty()) {
                        selectedMed.setRelationGroupId(selectedMed.getRelationGroupId());
                    }
                    medUp.add(selectedMed);
                    int selectRow = tblMedicine.getSelectedRow();
                    medTableModel.setMed(selectedMed, selectRow);
                    unitTableModel.setCurrMed(selectedMed);
                    lblInfo.setText(selectedMed.getMedName());
                    break;
                case "PriceChangeVouList":
                    lblStatus.setText("EDIT");
                    String vouId = ((PriceChangeHis1) selectObj).getPriceChangeVouId();
                    currPriceChange = (PriceChangeHis1) dao.find(PriceChangeHis1.class, vouId);
                    txtVouNo.setText(currPriceChange.getPriceChangeVouId());
                    txtDate.setText(DateUtil.toDateStr(currPriceChange.getPriceChangeDate()));
                    txtRemark.setText(currPriceChange.getRemark());
                    if (!currPriceChange.getListDetail().isEmpty()) {
                        listDetail = currPriceChange.getListDetail();
                        medTableModel.setListDetail(listDetail);

                        for (PriceChangeMedHis1 pcm : listDetail) {
                            if (pcm.getListUnit() != null) {
                                if (pcm.getListUnit().size() > 0) {

                                }
                            }
                        }
                    }
                    break;
            }

        } catch (Exception ex) {
            log.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addNewRow">
    /*
     * Using for adding blank row for tblMedicine.
     */
    private void addNewRow() {
        PriceChangeMedHis1 his = new PriceChangeMedHis1();

        his.setMed(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="initMedicineTable">
    /*
     * Initializing tblMedicine and set column with of the table and
     * add selection listener for selecting row event.
     */
    private void initMedicineTable() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        tblMedicine.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(200);//Medicine Name
        tblMedicine.getColumnModel().getColumn(2).setPreferredWidth(30);//Pur Price
        tblMedicine.getColumnModel().getColumn(3).setPreferredWidth(30);//Cost Price
        tblMedicine.getColumnModel().getColumn(4).setPreferredWidth(30);//Market Price
        tblMedicine.getColumnModel().getColumn(5).setPreferredWidth(30);//Remark
        tblMedicine.getColumnModel().getColumn(6).setPreferredWidth(30);
        tblMedicine.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblMedicine.getColumnModel().getColumn(8).setPreferredWidth(30);
        tblMedicine.getColumnModel().getColumn(9).setPreferredWidth(30);
        tblMedicine.getColumnModel().getColumn(10).setPreferredWidth(20);
        tblMedicine.getColumnModel().getColumn(11).setPreferredWidth(30);
        tblMedicine.getColumnModel().getColumn(12).setPreferredWidth(30);

        tblMedicine.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);//Pur Price
        tblMedicine.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);//Cost Price
        tblMedicine.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);//Market Price

        tblMedicine.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        tblMedicine.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        tblMedicine.getColumnModel().getColumn(8).setCellRenderer(new DPriceRenderer());
        tblMedicine.getColumnModel().getColumn(9).setCellRenderer(rightRenderer);
        tblMedicine.getColumnModel().getColumn(10).setCellRenderer(rightRenderer);
        tblMedicine.getColumnModel().getColumn(11).setCellRenderer(new DPriceRenderer());
        tblMedicine.getColumnModel().getColumn(12).setCellRenderer(rightRenderer);

        tblMedicine.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblMedicine.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor(this));
        tblMedicine.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor(this));

        //Define table selection model to single row selection.
        tblMedicine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblMedicine.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int selectedRow = tblMedicine.getSelectedRow();
            List<PriceChangeUnitHis1> listPcuh;
            Medicine selectedMed;

            try {
                listDetailUnit.removeAll(listDetailUnit);
                unitTableModel.dataChange();
                lblInfo.setText("");
                if (selectedRow >= 0) {
                    listPcuh = listDetail.get(selectedRow).getListUnit();
                    PriceChangeMedHis1 pcmh = listDetail.get(selectedRow);
                    selectedMed = pcmh.getMed();
                    unitTableModel.setCurrMed(selectedMed);

                    String purUnit = "";
                    if (pcmh.getPurchaseUnit() != null) {
                        purUnit = pcmh.getPurchaseUnit().getItemUnitCode();
                    }
                    txtPurPrice.setText(NumberUtil.NZero(pcmh.getPurchasePrice()).toString()
                            + " " + purUnit);

                    String costUnit = "";
                    if (pcmh.getCostUnit() != null) {
                        costUnit = pcmh.getCostUnit().getItemUnitCode();
                    }
                    txtCostPrice.setText(NumberUtil.NZero(pcmh.getCostPrice()).toString()
                            + " " + costUnit);

                    if (selectedMed != null) {
                        lblInfo.setText(selectedMed.getMedName());
                        getCostDetail(selectedMed.getMedId());
                    } else {
                        modelSCDT.removeAll();
                    }

                    if (listPcuh != null) {
                        listDetailUnit.addAll(listPcuh);
                    }

                }
            } catch (IndexOutOfBoundsException ex) {
                log.error(ex.toString());
            }
        });
    }// </editor-fold>

    private void getCostDetail(String itemId) {
        if (itemId == null) {
            modelSCDT.removeAll();
        } else {
            String strHSQL = "select c from StockCostingDetail c where c.itemId = '"
                    + itemId + "' and c.userId = '" + Global.loginUser.getUserId() + "'"
                    + " and c.costFor = 'Opening' ";

            List<StockCostingDetail> listCostDetail = dao.findAllHSQL(strHSQL);
            modelSCDT.setListStockCostingDetail(listCostDetail);
        }
    }

    public void checkLost() {
        String strMedList = "";
        for (int rowIndex = 0; rowIndex < listDetail.size() - 1; rowIndex++) {
            //boolean isBreak = false;
            PriceChangeMedHis1 pcmh = listDetail.get(rowIndex);
            if (rowIndex < listDetail.size()) {
                List<PriceChangeUnitHis1> listPcuh = pcmh.getListUnit();
                for (PriceChangeUnitHis1 pcuh : listPcuh) {
                    if (NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getnPriceOld())
                            && NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getnPriceNew())) {
                        //isBreak = true;
                        if (strMedList.isEmpty()) {
                            strMedList = pcmh.getMed().getMedId();
                        } else {
                            strMedList = strMedList + "," + pcmh.getMed().getMedId();
                        }
                        log.info(pcmh.getMed().getMedId() + " Sale Price");
                        break;
                    }

                    if (NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getaPriceOld())
                            && NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getaPriceNew())) {
                        //isBreak = true;
                        if (strMedList.isEmpty()) {
                            strMedList = pcmh.getMed().getMedId();
                        } else {
                            strMedList = strMedList + "," + pcmh.getMed().getMedId();
                        }
                        log.info(pcmh.getMed().getMedId() + " Price A");
                        break;
                    }

                    if (NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getbPriceOld())
                            && NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getbPriceNew())) {
                        //isBreak = true;
                        if (strMedList.isEmpty()) {
                            strMedList = pcmh.getMed().getMedId();
                        } else {
                            strMedList = strMedList + "," + pcmh.getMed().getMedId();
                        }
                        log.info(pcmh.getMed().getMedId() + " Price B");
                        break;
                    }

                    if (NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getcPriceOld())
                            && NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getcPriceNew())) {
                        //isBreak = true;
                        if (strMedList.isEmpty()) {
                            strMedList = pcmh.getMed().getMedId();
                        } else {
                            strMedList = strMedList + "," + pcmh.getMed().getMedId();
                        }
                        log.info(pcmh.getMed().getMedId() + " Price C");
                        break;
                    }

                    if (NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getdPriceOld())
                            && NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getdPriceNew())) {
                        //isBreak = true;
                        if (strMedList.isEmpty()) {
                            strMedList = pcmh.getMed().getMedId();
                        } else {
                            strMedList = strMedList + "," + pcmh.getMed().getMedId();
                        }
                        log.info(pcmh.getMed().getMedId() + " Price D");
                        break;
                    }

                    if (NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getePriceOld())
                            && NumberUtil.NZero(pcuh.getCostPrice()) > NumberUtil.NZero(pcuh.getePriceNew())) {
                        //isBreak = true;
                        if (strMedList.isEmpty()) {
                            strMedList = pcmh.getMed().getMedId();
                        } else {
                            strMedList = strMedList + "," + pcmh.getMed().getMedId();
                        }
                        log.info(pcmh.getMed().getMedId() + " Price E");
                        break;
                    }
                }
            }
        }

        if (!strMedList.isEmpty()) {
            clear();
            getSelectedItem(strMedList);
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Lost not found.",
                    "Lost", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * This section is implementation of FormAction interface.
     * Include common function of all entry form. Those function will be
     * call from PriceChangeView.
     * Save
     * newForm
     * history
     * delete
     * deleteCopy
     * print
     */
    // <editor-fold defaultstate="collapsed" desc="FormAction Implementation">
    @Override
    public void save() {
        try {
            if (tblMedicine.getCellEditor() != null) {
                tblMedicine.getCellEditor().stopCellEditing();
            }
            if (tblUnit.getCellEditor() != null) {
                tblUnit.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (lblStatus.getText().equals("NEW")) {
            try {
                currPriceChange.setPriceChangeVouId(txtVouNo.getText());
                currPriceChange.setPriceChangeDate(DateUtil.toDate(txtDate.getText()));
                currPriceChange.setCreatedBy(Global.loginUser);
                currPriceChange.setRemark(txtRemark.getText());
                removeEmptyRow();
                currPriceChange.setListDetail(listDetail);
                dao.save(currPriceChange);

                //dao.open();
                for (PriceChangeMedHis1 pcmHis : listDetail) {
                    log.info("Med Id : " + pcmHis.getMed().getMedId());
                    if (pcmHis.getListUnit() != null) {
                        Medicine med = (Medicine) dao.find(Medicine.class, pcmHis.getMed().getMedId());

                        List<RelationGroup> listRg = med.getRelationGroupId();
                        List<PriceChangeUnitHis1> listPcUh = pcmHis.getListUnit();
                        int index = 0;

                        for (PriceChangeUnitHis1 pcUh : listPcUh) {
                            if (pcUh.getnPriceNew() != null) {
                                listRg.get(index).setSalePrice(pcUh.getnPriceNew());
                            }

                            if (pcUh.getaPriceNew() != null) {
                                listRg.get(index).setSalePriceA(pcUh.getaPriceNew());
                            }

                            if (pcUh.getbPriceNew() != null) {
                                listRg.get(index).setSalePriceB(pcUh.getbPriceNew());
                            }

                            if (pcUh.getcPriceNew() != null) {
                                listRg.get(index).setSalePriceC(pcUh.getcPriceNew());
                            }

                            if (pcUh.getdPriceNew() != null) {
                                listRg.get(index).setSalePriceD(pcUh.getdPriceNew());
                            }

                            if (pcUh.getePriceNew() != null) {
                                listRg.get(index).setStdCost(pcUh.getePriceNew());
                            }
                            index++;
                        }

                        med.setUpdatedDate(new Date());
                        List<RelationGroup> listTRG = med.getRelationGroupId();
                        if (listTRG == null) {
                            log.error("price change error : " + med.getMedId());
                        } else if (listTRG.isEmpty()) {
                            log.error("price change error : " + med.getMedId() + ", " + listTRG.size());
                        } else {
                            dao.save(med);
                        }
                    }
                }
                //dao.flush();
                //dao.commit();
                vouEngine.updateVouNo();
                dao.execSql("update machine_info set action_status = 'ITEM' where machine_name is not null");
                clear();
            } catch (Exception ex) {
                //dao.rollBack();
                log.error("save : " + +ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        } else {
            clear();
        }
    }

    private void removeEmptyRow() {
        listDetail.remove(listDetail.size() - 1);
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Price Change Search", dao);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        System.out.println("Delete");
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

    // <editor-fold defaultstate="collapsed" desc="initUnitTable">
    /*
     * Initializing tblUnit and set column with of the table and
     * create group table header.
     */
    private void initUnitTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblUnit.setCellSelectionEnabled(true);
        }
        //Adjust column width
        tblUnit.getColumnModel().getColumn(0).setPreferredWidth(100);//Unit
        //tblUnit.getColumnModel().getColumn(1).setPreferredWidth(140);//Cost Price
        tblUnit.getColumnModel().getColumn(1).setPreferredWidth(140);//N price old
        tblUnit.getColumnModel().getColumn(2).setPreferredWidth(140);//N price new
        tblUnit.getColumnModel().getColumn(3).setPreferredWidth(140);//A price old
        tblUnit.getColumnModel().getColumn(4).setPreferredWidth(140);//A price new
        tblUnit.getColumnModel().getColumn(5).setPreferredWidth(140);//B price old
        tblUnit.getColumnModel().getColumn(6).setPreferredWidth(140);//B price new
        tblUnit.getColumnModel().getColumn(7).setPreferredWidth(140);//C price old
        tblUnit.getColumnModel().getColumn(8).setPreferredWidth(140);//C price new
        tblUnit.getColumnModel().getColumn(9).setPreferredWidth(140);//D price old
        tblUnit.getColumnModel().getColumn(10).setPreferredWidth(140);//D price new
        tblUnit.getColumnModel().getColumn(11).setPreferredWidth(140);//D price old
        tblUnit.getColumnModel().getColumn(12).setPreferredWidth(140);//D price new

        tblUnit.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(8).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(10).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(11).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(12).setCellEditor(new BestTableCellEditor(this));

        tblUnit.getColumnModel().getColumn(1).setCellRenderer(new NormalPriceRenderer());
        tblUnit.getColumnModel().getColumn(3).setCellRenderer(new APriceRenderer());
        tblUnit.getColumnModel().getColumn(5).setCellRenderer(new BPriceRenderer());
        tblUnit.getColumnModel().getColumn(7).setCellRenderer(new CPriceRenderer());
        tblUnit.getColumnModel().getColumn(9).setCellRenderer(new DPriceRenderer());
        tblUnit.getColumnModel().getColumn(11).setCellRenderer(new DPriceRenderer());

        TableColumnModel cm = tblUnit.getColumnModel();

        ColumnGroup sale_price = new ColumnGroup("Sale Price");
        sale_price.add(cm.getColumn(1));
        sale_price.add(cm.getColumn(2));

        ColumnGroup price_a = new ColumnGroup("Price A");
        price_a.add(cm.getColumn(3));
        price_a.add(cm.getColumn(4));

        ColumnGroup price_b = new ColumnGroup("Price B");
        price_b.add(cm.getColumn(5));
        price_b.add(cm.getColumn(6));

        ColumnGroup price_c = new ColumnGroup("Price C");
        price_c.add(cm.getColumn(7));
        price_c.add(cm.getColumn(8));

        ColumnGroup price_d = new ColumnGroup("Price D");
        price_d.add(cm.getColumn(9));
        price_d.add(cm.getColumn(10));

        ColumnGroup price_e = new ColumnGroup("Price E");
        price_e.add(cm.getColumn(11));
        price_e.add(cm.getColumn(12));

        GroupableTableHeader header = (GroupableTableHeader) tblUnit.getTableHeader();
        header.addColumnGroup(sale_price);
        header.addColumnGroup(price_a);
        header.addColumnGroup(price_b);
        header.addColumnGroup(price_c);
        header.addColumnGroup(price_d);
        header.addColumnGroup(price_e);

    }//</editor-fold>

    //Set focus to tblMedicine.
    public void setFocus() {
        tblMedicine.requestFocusInWindow();
    }

    /*
     * Focus managment function
     */
    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        //focusOrder.add(txtCusId);
        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
    /*
     * Adding focus movement key.
     * Default movement key is tab key.
     * Adding arrow key down and up to focus movement key
     */
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

    /*
     * This function calling from clear method.
     * User request newForm or after saved entry and this function was call.
     */
    private void assignDefaultValue() {
        listDetail = ObservableCollections.observableList(new ArrayList<PriceChangeMedHis1>());
        medTableModel.setListDetail(listDetail);
        listDetailUnit.removeAll(listDetailUnit);

        txtDate.setText(DateUtil.getTodayDateStr());
    }
    /*
     * Focus managment for tblMedicine when user press enter key
     */
    private Action actionTblMedEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblMedicine.getCellEditor() != null) {
                    tblMedicine.getCellEditor().stopCellEditing();
                }

                int row = tblMedicine.getSelectedRow();
                int col = tblMedicine.getSelectedColumn();

                PriceChangeMedHis1 ddh = listDetail.get(row);

                if (col == 0 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(2, 2); //Pur Price
                } else if (col == 2 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(4, 4); //Cost Price
                } else if (col == 4 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(6, 6); //Market price
                } else if (col == 6 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(8, 8); //Remark
                } else if (col == 8 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setRowSelectionInterval(row + 1, row + 1);
                    tblMedicine.setColumnSelectionInterval(0, 0); //Move to Code
                }
            } catch (Exception ex) {

            }
        }
    };

    private Action actionTblUnitEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblUnit.getCellEditor() != null) {
                    tblUnit.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            int row = tblUnit.getSelectedRow();
            int col = tblUnit.getSelectedColumn();

            switch (col) {
                case 0:
                case 1:
                    tblUnit.setColumnSelectionInterval(2, 2);
                    log.info("Focus To : 2");
                    break;
                case 2:
                case 3:
                    tblUnit.setColumnSelectionInterval(4, 4);
                    log.info("Focus To : 4");
                    break;
                case 4:
                case 5:
                    tblUnit.setColumnSelectionInterval(6, 6);
                    log.info("Focus To : 6");
                    break;
                case 6:
                case 7:
                    tblUnit.setColumnSelectionInterval(8, 8);
                    log.info("Focus To : 8");
                    break;
                case 8:
                case 9:
                    tblUnit.setColumnSelectionInterval(10, 10);
                    log.info("Focus To : 10");
                    break;
                case 10:
                case 11:
                    tblUnit.setColumnSelectionInterval(12, 12);
                    log.info("Focus To : 12");
                    break;
                case 12:
                    if (tblUnit.getRowCount() > (row + 1)) {
                        tblUnit.setRowSelectionInterval(row + 1, row + 1);
                    } else {
                        tblUnit.setRowSelectionInterval(0, 0);
                    }
                    tblUnit.setColumnSelectionInterval(2, 2); //Move to Code
                    log.info("Focus To : Next Line");
                    break;
                default:
                    break;
            }
        }
    };

    private Action actionCtrlDownChange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblUnit.getCellEditor() != null) {
                    tblUnit.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            int currIndex = tblMedicine.getSelectedRow();
            if (tblMedicine.getRowCount() > (currIndex + 1)) {
                tblMedicine.setRowSelectionInterval(currIndex + 1, currIndex + 1);
            }

            if (tblUnit.getRowCount() > 0) {
                tblUnit.setRowSelectionInterval(0, 0);
                tblUnit.setColumnSelectionInterval(2, 2);
                tblUnit.requestFocus();
            }

            log.info("Ctrl Down");
        }
    };

    private Action actionCtrlUpChange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblUnit.getCellEditor() != null) {
                    tblUnit.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            int currIndex = tblMedicine.getSelectedRow();
            if ((currIndex - 1) >= 0) {
                tblMedicine.setRowSelectionInterval(currIndex - 1, currIndex - 1);
            }

            if (tblUnit.getRowCount() > 0) {
                tblUnit.setRowSelectionInterval(0, 0);
                tblUnit.setColumnSelectionInterval(2, 2);
                tblUnit.requestFocus();
            }

            log.info("Ctrl Up");
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
    }
    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actionHistory">
    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };// </editor-fold>

    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    private void getSelectedItem(String selectedMedId) {
        String[] medIdList = selectedMedId.split(",");

        for (String medId : medIdList) {
            Medicine selectedMed = (Medicine) dao.find(Medicine.class, medId.replace("'", ""));
            if (selectedMed.getRelationGroupId().size() > 0) {
                selectedMed.setRelationGroupId(selectedMed.getRelationGroupId());
            }
            medUp.add(selectedMed);
            int selectRow = medTableModel.getLastIndex();
            medTableModel.setMed(selectedMed, selectRow);
            unitTableModel.setCurrMed(selectedMed);
            //medTableModel.addEmptyRow();
            /*if (selectedMedId == null) {
                        selectedMedId = "'" + rs.getString("med_id") + "'";
                    } else {
                        selectedMedId = selectedMedId + ",'" + rs.getString("med_id") + "'";
                    }*/
            //selectedMedId = rs.getString("med_id");
            //getMedInfo(selectedMedId);
        }
    }

    private HashMap getLatestPur(String selectedMedId) {
        /*String strLatestPur = "select med_id, max(pur_date) last_date, pur_unit,"
                + "pur_price, pur_unit_cost from v_purchase "
                + "where deleted = false and med_id in (" + selectedMedId
                + ") group by med_id";*/
        String strLatestPur = "select vlp.med_id, vlp.pur_date last_date, vlp.pur_unit, vlp.pur_price, vlp.pur_unit_cost\n"
                + "from v_purchase vlp, (\n"
                + "		select vp.med_id, max(vp.pur_detail_id) pur_detail_id\n"
                + "		from v_purchase vp, (\n"
                + "						select med_id, max(pur_date) pur_date\n"
                + "						from v_purchase where deleted = false and med_id in (" + selectedMedId + ")\n"
                + "						group by med_id) mpd\n"
                + "		where vp.med_id = mpd.med_id and vp.pur_date = mpd.pur_date\n"
                + "		and vp.deleted = false and vp.med_id in (" + selectedMedId + ")) vpid\n"
                + "where vlp.med_id = vpid.med_id and vlp.pur_detail_id = vpid.pur_detail_id";
        ResultSet rsLatestPur = dao.execSQL(strLatestPur);
        HashMap<String, String> hmLatestPur = new HashMap();

        try {
            while (rsLatestPur.next()) {
                String strMedId = rsLatestPur.getString("med_id");
                String strPurUnit = Util1.getString(rsLatestPur.getString("pur_unit"), "-");
                String strPurPrice = Util1.getString(rsLatestPur.getString("pur_price"), "0");
                String strUnitCost = Util1.getString(rsLatestPur.getString("pur_unit_cost"), "0");

                hmLatestPur.put(strMedId, strPurUnit + "@" + strPurPrice
                        + "@" + strUnitCost);
            }
        } catch (Exception ex) {
            log.error("getLastPur : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.closeStatment();
        }

        return hmLatestPur;
    }

    private HashMap getLatestMarket(String selectedMedId) {
        String strLatestMarket = "select med_id, max(change_date) last_date, "
                + "market_price, market_unit, remark_med "
                + "from v_price_change_med where med_id in (" + selectedMedId
                + ") group by med_id";
        ResultSet rsLatestMarket = dao.execSQL(strLatestMarket);
        HashMap<String, String> hmLatestMarket = new HashMap();

        try {
            while (rsLatestMarket.next()) {
                String strMedId = rsLatestMarket.getString("med_id");
                String strMarketPrice = Util1.getString(rsLatestMarket.getString("market_price"), "0");
                String strMarketUnit = Util1.getString(rsLatestMarket.getString("market_unit"), "-");
                String strRemark = Util1.getString(rsLatestMarket.getString("remark_med"), "-");

                hmLatestMarket.put(strMedId, strMarketPrice + "@" + strMarketUnit
                        + "@" + strRemark);
            }
        } catch (Exception ex) {
            log.error("getLastMarket : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.closeStatment();
        }

        return hmLatestMarket;
    }

    private void search() {
        String strSql = "select med_id from medicine ";
        String filter = null;

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            String itemType = ((ItemType) cboItemType.getSelectedItem()).getItemTypeCode();
            if (filter == null) {
                filter = "med_type_id = '" + itemType + "'";
            } else {
                filter = filter + " and med_type_id = '" + itemType + "'";
            }
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            int catId = ((Category) cboCategory.getSelectedItem()).getCatId();
            if (filter == null) {
                filter = "category_id = " + catId;
            } else {
                filter = filter + " and category_id = " + catId;
            }
        }

        if (cboItemBrand.getSelectedItem() instanceof ItemBrand) {
            int brandId = ((ItemBrand) cboItemBrand.getSelectedItem()).getBrandId();
            if (filter == null) {
                filter = "brand_id = " + brandId;
            } else {
                filter = filter + " and brand_id = " + brandId;
            }
        }

        if (!txtRemark.getText().trim().isEmpty()) {
            String strCode = txtRemark.getText().trim();
            if (filter == null) {
                filter = "med_id like '" + strCode + "%'";
            } else {
                filter = filter + " and med_id like '" + strCode + "%'";
            }
        }

        String selectIds = "";
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            try {
                locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                List<LocationItemMapping> listLIM = dao.findAllHSQL("select o from LocationItemMapping o "
                        + "where o.key.locationId = " + locationId);

                for (LocationItemMapping lim : listLIM) {
                    if (selectIds.isEmpty()) {
                        selectIds = "'" + lim.getKey().getItemId() + "'";
                    } else {
                        selectIds = selectIds + ",'" + lim.getKey().getItemId() + "'";
                    }
                }
            } catch (Exception ex) {
                log.error("fillByPercent : location : " + ex.toString());
            } finally {
                dao.close();
            }
        }

        //Purchase date filter
        String selectedMedIDs = "";
        if (!txtPurFrom.getText().trim().isEmpty() && !txtPurTo.getText().trim().isEmpty()) {
            String strSql1 = "select distinct med_id"
                    + " from v_purchase where date(pur_date) between '"
                    + DateUtil.toDateStrMYSQL(txtPurFrom.getText().trim())
                    + "' and '" + DateUtil.toDateStrMYSQL(txtPurTo.getText().trim()) + "'"
                    + " and med_id in (" + selectIds + ")";

            if (selectIds.isEmpty()) {
                strSql1 = "select distinct med_id"
                        + " from v_purchase where date(pur_date) between '"
                        + DateUtil.toDateStrMYSQL(txtPurFrom.getText().trim())
                        + "' and '" + DateUtil.toDateStrMYSQL(txtPurTo.getText().trim()) + "'";
            }

            strSql1 = strSql1 + " and deleted = false";
            if (locationId != -1) {
                strSql1 = strSql1 + " and location = " + locationId;
            }

            try {
                ResultSet rs1 = dao.execSQL(strSql1);
                if (rs1 != null) {
                    while (rs1.next()) {
                        if (selectedMedIDs.isEmpty()) {
                            selectedMedIDs = "'" + rs1.getString("med_id") + "'";
                        } else {
                            selectedMedIDs = selectedMedIDs + ",'" + rs1.getString("med_id") + "'";
                        }
                    }
                    rs1.close();
                }
            } catch (SQLException ex) {
                log.error("fillByPercent : " + ex.toString());
            } finally {
                //selectIds = "";
                dao.close();
            }
        } else {
            if (!selectIds.isEmpty()) {
                if (filter == null) {
                    filter = "med_id in (" + selectIds + ")";
                } else {
                    filter = filter + " and med_id in (" + selectIds + ")";
                }
            }
        }

        if (!selectedMedIDs.isEmpty()) {
            if (filter == null) {
                filter = "med_id in (" + selectedMedIDs + ")";
            } else {
                filter = filter + " and med_id in (" + selectedMedIDs + ")";
            }
        }

        if (filter != null) {
            strSql = strSql + " where " + filter;

            String selectedMedId = null;
            ResultSet rs = dao.execSQL(strSql);

            try {
                while (rs.next()) {
                    selectedMedId = rs.getString("med_id");
                    Medicine selectedMed = (Medicine) dao.find(Medicine.class, selectedMedId);
                    if (!selectedMed.getRelationGroupId().isEmpty()) {
                        selectedMed.setRelationGroupId(selectedMed.getRelationGroupId());
                    }
                    medUp.add(selectedMed);
                    int selectRow = medTableModel.getLastIndex();
                    medTableModel.setMed(selectedMed, selectRow);
                    //unitTableModel.setCurrMed(selectedMed);
                    /*if (selectedMedId == null) {
                        selectedMedId = "'" + rs.getString("med_id") + "'";
                    } else {
                        selectedMedId = selectedMedId + ",'" + rs.getString("med_id") + "'";
                    }*/
                }
            } catch (SQLException ex) {
                log.error("fillByPercent : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.closeStatment();
            }
        }
    }

    private void fillPercent() {
        int count = 0;
        double nPercent = NumberUtil.NZero(txtSPercent.getText().trim()) / 100;
        double aPercent = NumberUtil.NZero(txtAPercent.getText().trim()) / 100;
        double bPercent = NumberUtil.NZero(txtBPercent.getText().trim()) / 100;
        double cPercent = NumberUtil.NZero(txtCPercent.getText().trim()) / 100;
        double dPercent = NumberUtil.NZero(txtDPercent.getText().trim()) / 100;
        double ePercent = NumberUtil.NZero(txtEPercent.getText().trim()) / 100;
        double incValue;
        boolean isFillWithCostPrice = chkFillWithCostPrice.isSelected();
        boolean isFillWithPurPrice = chkFillWithSalePrice.isSelected();

        for (PriceChangeMedHis1 pcmh : listDetail) {
            if (count < listDetail.size() - 1) {
                List<PriceChangeUnitHis1> listUnit = pcmh.getListUnit();

                for (PriceChangeUnitHis1 pcuh : listUnit) {
                    if (isFillWithCostPrice) {
                        if (nPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * nPercent);
                            pcuh.setnPriceNew(Math.ceil(incValue));
                        }

                        if (aPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * aPercent);
                            pcuh.setaPriceNew(Math.ceil(incValue));
                        }

                        if (bPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * bPercent);
                            pcuh.setbPriceNew(Math.ceil(incValue));
                        }

                        if (cPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * cPercent);
                            pcuh.setcPriceNew(Math.ceil(incValue));
                        }

                        if (dPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * dPercent);
                            pcuh.setdPriceNew(Math.ceil(incValue));
                        }

                        if (ePercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * ePercent);
                            pcuh.setePriceNew(Math.ceil(incValue));
                        }
                    } else if (isFillWithPurPrice) {
                        if (nPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * nPercent);
                            pcuh.setnPriceNew(Math.ceil(incValue));
                        }

                        if (aPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * aPercent);
                            pcuh.setaPriceNew(Math.ceil(incValue));
                        }

                        if (bPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * bPercent);
                            pcuh.setbPriceNew(Math.ceil(incValue));
                        }

                        if (cPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * cPercent);
                            pcuh.setcPriceNew(Math.ceil(incValue));
                        }

                        if (dPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * dPercent);
                            pcuh.setdPriceNew(Math.ceil(incValue));
                        }

                        if (ePercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getCostPrice())
                                    + (NumberUtil.NZero(pcuh.getCostPrice()) * ePercent);
                            pcuh.setePriceNew(Math.ceil(incValue));
                        }
                    } else {
                        if (nPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getnPriceOld())
                                    + (NumberUtil.NZero(pcuh.getnPriceOld()) * nPercent);
                            pcuh.setnPriceNew(Math.ceil(incValue));
                        }

                        if (aPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getaPriceOld())
                                    + (NumberUtil.NZero(pcuh.getaPriceOld()) * aPercent);
                            pcuh.setaPriceNew(Math.ceil(incValue));
                        }

                        if (bPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getbPriceOld())
                                    + (NumberUtil.NZero(pcuh.getbPriceOld()) * bPercent);
                            pcuh.setbPriceNew(Math.ceil(incValue));
                        }

                        if (cPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getcPriceOld())
                                    + (NumberUtil.NZero(pcuh.getcPriceOld()) * cPercent);
                            pcuh.setcPriceNew(Math.ceil(incValue));
                        }

                        if (dPercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getdPriceOld())
                                    + (NumberUtil.NZero(pcuh.getdPriceOld()) * dPercent);
                            pcuh.setdPriceNew(Math.ceil(incValue));
                        }

                        if (ePercent != 0.0) {
                            incValue = NumberUtil.NZero(pcuh.getdPriceOld())
                                    + (NumberUtil.NZero(pcuh.getdPriceOld()) * ePercent);
                            pcuh.setePriceNew(Math.ceil(incValue));
                        }
                    }
                }
            }

            count++;
        }
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboItemType, dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
        BindingUtil.BindComboFilter(cboCategory, dao.findAllHSQL("select o from Category o order by o.catName"));
        BindingUtil.BindComboFilter(cboItemBrand, dao.findAllHSQL("select o from ItemBrand o order by o.brandName"));
        BindingUtil.BindComboFilter(cboLocation, dao.findAllHSQL("select o from Location o order by o.locationName"));
    }

    private void insertStockFilterCode() {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.loginUser.getUserId() + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.loginUser.getUserId()
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(txtDate.getText()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true";

        String tmpMedFilter = "";
        if (!selectedMedId.isEmpty()) {
            String[] medIdList = selectedMedId.split(",");
            for (String strTmp : medIdList) {
                if (tmpMedFilter.isEmpty()) {
                    tmpMedFilter = "'" + strTmp + "'";
                } else {
                    tmpMedFilter = tmpMedFilter + ",'" + strTmp + "'";
                }
            }

            if (!tmpMedFilter.isEmpty()) {
                strSQL = strSQL + " and m.med_id in (" + tmpMedFilter + ")";
            }
        } else {
            if (listDetail.size() > 1) {
                for (PriceChangeMedHis1 pcmh : listDetail) {
                    if (pcmh.getMed() != null) {
                        String medId = pcmh.getMed().getMedId();
                        if (tmpMedFilter.isEmpty()) {
                            tmpMedFilter = "'" + medId + "'";
                        } else {
                            tmpMedFilter = tmpMedFilter + ",'" + medId + "'";
                        }
                    }
                }

                if (!tmpMedFilter.isEmpty()) {
                    strSQL = strSQL + " and m.med_id in (" + tmpMedFilter + ")";
                }
            }
        }

        if (!tmpMedFilter.isEmpty()) {
            try {
                dao.open();
                dao.execSql(strSQLDelete, strSQL);
            } catch (Exception ex) {
                log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void calculateCost() {
        try {
            String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                    + Global.loginUser.getUserId() + "'";
            String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                    + Global.loginUser.getUserId() + "'";
            String strMethod = "FIFO";

            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();
            insertStockFilterCode();

            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(txtDate.getText()), "Opening",
                    Global.loginUser.getUserId());

            //String strLocation = "0";
            /*dao.execProc("insert_cost_detail",
                    "Opening", DateUtil.toDateStrMYSQL(txtDate.getText()),
                    Global.loginUser.getUserId(), strMethod);*/
            insertCostDetail("Opening", DateUtil.toDateStrMYSQL(txtDate.getText()),
                    strMethod);
            dao.commit();

        } catch (Exception ex) {
            dao.rollBack();
            log.error("calculateCost : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void getAnalystData() {
        if (listDetail.size() > 1) {
            for (PriceChangeMedHis1 pcmh : listDetail) {
                if (pcmh.getMed() != null) {
                    String medId = pcmh.getMed().getMedId();
                    if (medId != null) {
                        //Sale Data
                        String strSale = "select sum(ifnull(sale_smallest_qty,0)) ttl_sale, sum(ifnull(foc_smallest_qty,0)) ttl_foc,\n"
                                + "sum(ifnull(sale_amount,0)) ttl_sale_amt\n"
                                + "from v_sale\n"
                                + "where deleted = false and date(sale_date) between '"
                                + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateStrMYSQL(txtTo.getText())
                                + "' and med_id = '" + medId + "'";

                        try {
                            log.info("Sale Med Id : " + medId);
                            try (ResultSet rsSale = dao.execSQL(strSale)) {
                                if (rsSale != null) {
                                    if (rsSale.next()) {
                                        Double ttlSale = rsSale.getDouble("ttl_sale");
                                        pcmh.setTtlSaleSmallest(ttlSale);
                                        String strTtlsale = MedicineUtil.getQtyInStr(pcmh.getMed(), ttlSale.floatValue());
                                        pcmh.setTtlSaleStr(strTtlsale);
                                        Double ttlSaleFoc = rsSale.getDouble("ttl_foc");
                                        String strSaleFoc = MedicineUtil.getQtyInStr(pcmh.getMed(), ttlSaleFoc.floatValue());
                                        pcmh.setSaleFocStr(strSaleFoc);
                                        pcmh.setSaleFocSmallest(ttlSaleFoc);
                                        pcmh.setSaleAmt(rsSale.getDouble("ttl_sale_amt"));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            log.error("getAnalystData sale : " + medId + " : " + ex.toString());
                        }

                        //Purchase Data
                        String strPurchase = "select sum(ifnull(pur_smallest_qty,0)) ttl_pur, sum(ifnull(pur_foc_smallest_qty,0)) ttl_foc,\n"
                                + "sum(ifnull(pur_amount,0)) ttl_pur_amt\n"
                                + "from v_purchase\n"
                                + "where deleted = false and date(pur_date) between '"
                                + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateStrMYSQL(txtTo.getText())
                                + "' and med_id = '" + medId + "'";
                        try {
                            log.info("Purchase Med Id : " + medId);
                            try (ResultSet rsPur = dao.execSQL(strPurchase)) {
                                if (rsPur != null) {
                                    if (rsPur.next()) {
                                        Double ttlPur = rsPur.getDouble("ttl_pur");
                                        pcmh.setTtlPurSmallest(ttlPur);
                                        String strTtlPur = MedicineUtil.getQtyInStr(pcmh.getMed(), ttlPur.floatValue());
                                        pcmh.setTtlPurStr(strTtlPur);
                                        Double ttlPurFoc = rsPur.getDouble("ttl_foc");
                                        String strPurFoc = MedicineUtil.getQtyInStr(pcmh.getMed(), ttlPurFoc.floatValue());
                                        pcmh.setPurFocStr(strPurFoc);
                                        pcmh.setPurFocSmallest(ttlPurFoc);
                                        pcmh.setPurAmt(rsPur.getDouble("ttl_pur_amt"));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            log.error("getAnalystData Purchase : " + medId + " : " + ex.toString());
                        }

                        //Cost Balance
                        String strHSQL = "select c from StockCosting c where c.key.userId = '"
                                + Global.loginUser.getUserId() + "' and c.key.tranOption = 'Opening'"
                                + " and c.key.medicine.medId = '" + medId + "'";
                        List<StockCosting> listStockCosting = dao.findAllHSQL(strHSQL);
                        if (listStockCosting != null) {
                            if (!listStockCosting.isEmpty()) {
                                StockCosting sc = listStockCosting.get(0);
                                pcmh.setStkBalanceSmallest(sc.getBlaQty().doubleValue());
                                pcmh.setStkBalanceStr(sc.getBalQtyStr());
                            }
                        }

                        medTableModel.fireDataChanged();
                    }
                }
            }
        }
    }

    private void insertCostDetail(String costFor, String costDate, String method) {
        String userId = Global.loginUser.getUserId();
        String strDelete = "delete from tmp_costing_detail where cost_for = '" + costFor + "' and user_id = '" + userId + "'";
        String strSql = "select tsc.med_id item_id, bal_qty ttl_stock, cost_price.tran_date, cost_price.tran_option, \n"
                + "         cost_price.ttl_qty, cost_price.smallest_cost, cost_price, item_unit\n"
                + "    from tmp_stock_costing tsc, \n"
                + "         (select 'Adjust' tran_option, v_adj.med_id item_id, adj_date tran_date, \n"
                + "                 sum(adj_smallest_qty) ttl_qty, cost_price,\n"
                + "                 (cost_price/vm.smallest_qty) smallest_cost, v_adj.item_unit\n"
                + "            from v_adj, (select med_id, min(op_date) op_date\n"
                + "	                       from tmp_stock_filter where user_id = prm_user_id\n"
                + "                          group by med_id) tsf,\n"
                + "				 v_medicine vm\n"
                + "           where v_adj.med_id = tsf.med_id and deleted = false \n"
                + "			 and v_adj.med_id = vm.med_id and v_adj.item_unit = vm.item_unit\n"
                + "             and date(adj_date) >= op_date and date(adj_date) <= prm_cost_date\n"
                + "			 and vm.active = true\n"
                + "		   group by v_adj.med_id, adj_date, cost_price, v_adj.item_unit\n"
                + "		   union all\n"
                + "          select 'Purchase' tran_option, vpur.med_id item_id, pur_date tran_date, \n"
                + "                 sum(pur_smallest_qty+ifnull(pur_foc_smallest_qty,0)) ttl_qty, pur_unit_cost cost_price, \n"
                + "                 (pur_unit_cost/vm.smallest_qty) smallest_cost, vpur.pur_unit item_unit\n"
                + "            from v_purchase vpur, (select med_id, min(op_date) op_date\n"
                + "								     from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                    group by med_id) tsf,\n"
                + "				 v_medicine vm\n"
                + "           where vpur.med_id = tsf.med_id and deleted = false and date(pur_date) >= op_date\n"
                + "			 and vpur.med_id = vm.med_id and vpur.pur_unit = vm.item_unit\n"
                + "			 and date(pur_date) <= prm_cost_date and vm.active = true\n"
                + "           group by vpur.med_id, pur_date, pur_unit_cost, vpur.pur_unit\n"
                + "		   union all\n"
                + "          select 'Return-In' tran_option, vretin.med_id item_id, ret_in_date tran_date, \n"
                + "                 sum(ret_in_smallest_qty) ttl_qty, ret_in_price cost_price, \n"
                + "                 (ret_in_price/vm.smallest_qty) smallest_cost, vretin.item_unit\n"
                + "            from v_return_in vretin, (select med_id, min(op_date) op_date\n"
                + "	                                    from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                       group by med_id) tsf,\n"
                + "                 v_medicine vm\n"
                + "           where vretin.med_id = tsf.med_id and deleted = false and date(ret_in_date) >= op_date\n"
                + "	         and vretin.med_id = vm.med_id and vretin.item_unit = vm.item_unit\n"
                + "			 and date(ret_in_date) <= prm_cost_date and vm.active = true\n"
                + "           group by vretin.med_id, ret_in_date, ret_in_price, vretin.item_unit\n"
                + "           union all\n"
                + "          select 'Opening' tran_option, vso.med_id item_id, vso.op_date tran_date, \n"
                + "				 sum(vso.op_smallest_qty) ttl_qty, vso.cost_price, \n"
                + "				 (vso.cost_price/vm.smallest_qty) smallest_cost, vso.item_unit\n"
                + "            from v_stock_op vso, tmp_stock_filter tsf, v_medicine vm\n"
                + "		   where vso.med_id = tsf.med_id and vso.location = tsf.location_id\n"
                + "             and vso.med_id = vm.med_id and vso.item_unit = vm.item_unit\n"
                + "             and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                + "			 and vm.active = true\n"
                + "           group by vso.med_id, vso.op_date, vso.cost_price, vso.item_unit) cost_price\n"
                + "   where tsc.med_id = cost_price.item_id and tsc.user_id = prm_user_id and tsc.tran_option = prm_cost_for\n"
                + "   order by item_id, cost_price.tran_date desc, cost_price desc";

        strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                .replace("prm_cost_date", "'" + costDate + "'")
                .replace("prm_cost_for", "'" + costFor + "'");
        try {
            dao.execSql(strDelete);
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                String prvItemId = "-";
                String itemId;
                Double totalStock;
                String tranDate;
                String tranOption;
                Double ttlQty;
                Double smallestCost;
                Double unitCost;
                String unit;
                Double leftStock = 0.0;
                Double prvTtlStock = 0.0;
                String prvTranDate = "-";
                Double prvTtlQty = 0.0;
                Double prvCost = 0.0;
                Double prvLeftStock = 0.0;
                Double prvSmallestCost = 0.0;
                String prvTranOption = "-";
                Double costQty;

                while (rs.next()) {
                    itemId = rs.getString("item_id");
                    if (itemId.equals("101094")) {
                        log.info("Error Tran : " + itemId);
                    }
                    totalStock = rs.getDouble("ttl_stock");
                    tranDate = DateUtil.toDateStrMYSQL(DateUtil.toDateStr(rs.getDate("tran_date")));
                    tranOption = rs.getString("tran_option");
                    ttlQty = rs.getDouble("ttl_qty");
                    smallestCost = rs.getDouble("smallest_cost");
                    unitCost = rs.getDouble("cost_price");
                    unit = rs.getString("item_unit");

                    if (!prvItemId.equals(itemId)) {
                        if (leftStock > 0.0) {
                            String tmpSql = "insert into tmp_costing_detail(item_id, ttl_stock, tran_date, tran_option, ttl_qty, \n"
                                    + "					cost_price, cost_qty, smallest_cost, user_id, cost_for, item_unit)\n"
                                    + "		values('" + prvItemId + "', " + prvTtlStock + ", '" + prvTranDate
                                    + "', 'ERR', " + prvTtlQty + ",\n"
                                    + prvCost + ", " + prvLeftStock + ", " + prvSmallestCost + ", '" + userId + "',\n'"
                                    + costFor + "', '" + unit + "')";
                            dao.execSql(tmpSql);
                        }

                        prvItemId = itemId;
                        prvTtlStock = totalStock;
                        prvTranDate = tranDate;
                        prvTranOption = tranOption;
                        prvTtlQty = ttlQty;
                        prvSmallestCost = smallestCost;
                        prvCost = unitCost;
                        leftStock = totalStock;
                    }

                    if (leftStock > 0) {
                        if (leftStock >= ttlQty) {
                            costQty = ttlQty;
                            leftStock = leftStock - ttlQty;
                        } else {
                            costQty = leftStock;
                            leftStock = 0.0;
                        }

                        if (costQty > 0) {
                            String tmpSql1 = "insert into tmp_costing_detail(item_id, ttl_stock, tran_date, tran_option, ttl_qty, \n"
                                    + "					cost_price, cost_qty, smallest_cost, user_id, cost_for, item_unit)\n"
                                    + "	  values('" + itemId + "', " + totalStock + ", '" + tranDate + "', '" + tranOption + "', " + ttlQty + ",\n"
                                    + unitCost + ", " + costQty + ", " + smallestCost + ", '" + userId + "',\n'"
                                    + costFor + "' , '" + unit + "')";
                            dao.execSql(tmpSql1);
                        }
                    }
                }

                //rs.close();
                if (method.equals("FIFO")) {
                    String tmpSql2 = "update tmp_stock_costing tsc, (select item_id, sum(cost_qty*smallest_cost) ttl_cost, user_id\n"
                            + "						    from tmp_costing_detail\n"
                            + "					      where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "					      group by item_id, user_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    tmpSql2 = tmpSql2.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(tmpSql2);
                } else if (method.equals("AVG")) {
                    String tmpSql3 = "update tmp_costing_detail tcd, (\n"
                            + "	select user_id, item_id, sum(ttl_qty) ttl_qty, sum(ttl_qty*smallest_cost) ttl_amt, \n"
                            + "		(sum(ttl_qty*smallest_cost)/sum(ttl_qty)) as avg_cost\n"
                            + "	  from tmp_costing_detail\n"
                            + "	  where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "	  group by user_id,item_id) avgc\n"
                            + "	set tcd.smallest_cost = avgc.avg_cost\n"
                            + "	where tcd.item_id = avgc.item_id and tcd.user_id = avgc.user_id and tcd.user_id = prm_user_id";
                    tmpSql3 = tmpSql3.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    String tmpSql4 = "update tmp_stock_costing tsc, \n"
                            + "               (select user_id, item_id, sum(cost_qty*smallest_cost) ttl_cost\n"
                            + "				  from tmp_costing_detail\n"
                            + "				 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "				 group by user_id,item_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    tmpSql4 = tmpSql4.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(tmpSql3, tmpSql4);
                }
            }
        } catch (Exception ex) {
            log.error("insertCostDetail : " + ex.toString());
        } finally {
            //dao.close();
        }
    }

    private void checkBoxSelect(String ctrlName) {
        if (ctrlName.equals("FillWithCost")) {
            chkFillWithSalePrice.setSelected(false);
        } else if (ctrlName.equals("FillWithSalePrice")) {
            chkFillWithCostPrice.setSelected(false);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(medTableModel);
        lblInfo = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUnit = new javax.swing.JTable(){
            protected JTableHeader createDefaultTableHeader() {
                return new GroupableTableHeader(columnModel);
            }
        };
        chkCalculate = new javax.swing.JCheckBox();
        txtPurPrice = new javax.swing.JTextField();
        txtCostPrice = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        cboItemType = new javax.swing.JComboBox<>();
        cboCategory = new javax.swing.JComboBox<>();
        cboItemBrand = new javax.swing.JComboBox<>();
        cboLocation = new javax.swing.JComboBox<>();
        txtPurFrom = new javax.swing.JFormattedTextField();
        txtPurTo = new javax.swing.JFormattedTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        txtDate = new javax.swing.JFormattedTextField();
        txtRemark = new javax.swing.JTextField();
        txtSPercent = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        butSearch = new javax.swing.JButton();
        butFill = new javax.swing.JButton();
        butGetTran = new javax.swing.JButton();
        txtAPercent = new javax.swing.JTextField();
        txtDPercent = new javax.swing.JTextField();
        txtBPercent = new javax.swing.JTextField();
        txtEPercent = new javax.swing.JTextField();
        txtCPercent = new javax.swing.JTextField();
        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        chkFillWithCostPrice = new javax.swing.JCheckBox();
        butFindLost = new javax.swing.JButton();
        chkFillWithSalePrice = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCostDetail = new javax.swing.JTable();

        tblMedicine.setFont(Global.textFont);
        tblMedicine.setRowHeight(23);
        tblMedicine.setVerifyInputWhenFocusTarget(false);
        tblMedicine.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblMedicineFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblMedicine);

        lblInfo.setFont(Global.lableFont);
        lblInfo.setText("Info");

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("Edit");

        tblUnit.setFont(Global.textFont);
        tblUnit.setModel(unitTableModel);
        tblUnit.setRowHeight(23);
        tblUnit.setShowVerticalLines(false);
        tblUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblUnitFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(tblUnit);

        chkCalculate.setFont(Global.lableFont);
        chkCalculate.setText("Calculate");

        txtPurPrice.setEditable(false);
        txtPurPrice.setBorder(javax.swing.BorderFactory.createTitledBorder("Pur Price"));

        txtCostPrice.setEditable(false);
        txtCostPrice.setBorder(javax.swing.BorderFactory.createTitledBorder("Cost Price"));

        cboItemType.setFont(Global.textFont);
        cboItemType.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Type"));

        cboCategory.setFont(Global.textFont);
        cboCategory.setBorder(javax.swing.BorderFactory.createTitledBorder("Category"));

        cboItemBrand.setFont(Global.textFont);
        cboItemBrand.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Brand"));
        cboItemBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboItemBrandActionPerformed(evt);
            }
        });

        cboLocation.setFont(Global.textFont);
        cboLocation.setBorder(javax.swing.BorderFactory.createTitledBorder("Location"));

        txtPurFrom.setEditable(false);
        txtPurFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("Pur From"));
        txtPurFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPurFromMouseClicked(evt);
            }
        });

        txtPurTo.setEditable(false);
        txtPurTo.setBorder(javax.swing.BorderFactory.createTitledBorder("Pur To"));
        txtPurTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPurToMouseClicked(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Vou No"));
        txtVouNo.setFont(Global.textFont);

        txtDate.setEditable(false);
        txtDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));
        txtDate.setFont(Global.textFont);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });

        txtRemark.setFont(Global.textFont);
        txtRemark.setBorder(javax.swing.BorderFactory.createTitledBorder("Remark"));

        txtSPercent.setBorder(javax.swing.BorderFactory.createTitledBorder("S %"));

        butSearch.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        butFill.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        butFill.setText("Fill");
        butFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFillActionPerformed(evt);
            }
        });

        butGetTran.setText("G-Tran");
        butGetTran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGetTranActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(butGetTran)
            .addComponent(butFill)
            .addComponent(butSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butFill, butGetTran, butSearch});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(butSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butGetTran)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butFill)
                .addContainerGap())
        );

        txtAPercent.setBorder(javax.swing.BorderFactory.createTitledBorder("A %"));

        txtDPercent.setBorder(javax.swing.BorderFactory.createTitledBorder("D %"));

        txtBPercent.setBorder(javax.swing.BorderFactory.createTitledBorder("B %"));

        txtEPercent.setBorder(javax.swing.BorderFactory.createTitledBorder("E %"));

        txtCPercent.setBorder(javax.swing.BorderFactory.createTitledBorder("C %"));

        txtFrom.setEditable(false);
        txtFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("From"));
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtTo.setEditable(false);
        txtTo.setBorder(javax.swing.BorderFactory.createTitledBorder("To"));
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        chkFillWithCostPrice.setText("Fill With Cost");
        chkFillWithCostPrice.setName("FillWithCost"); // NOI18N
        chkFillWithCostPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFillWithCostPriceActionPerformed(evt);
            }
        });

        butFindLost.setText("Find Lost");
        butFindLost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFindLostActionPerformed(evt);
            }
        });

        chkFillWithSalePrice.setText("Fill With Pur Price");
        chkFillWithSalePrice.setName("FillWithSalePrice"); // NOI18N
        chkFillWithSalePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFillWithSalePriceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtRemark))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboItemType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboCategory, 0, 157, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboLocation, 0, 157, Short.MAX_VALUE)
                    .addComponent(cboItemBrand, 0, 157, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPurTo)
                    .addComponent(txtPurFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chkFillWithSalePrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkFillWithCostPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFindLost, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(txtPurFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtPurTo, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboItemBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFrom)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSPercent)
                        .addComponent(txtAPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDPercent)
                        .addComponent(txtCPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkFillWithCostPrice)
                            .addComponent(butFindLost))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkFillWithSalePrice))))
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboItemBrand, txtFrom, txtPurFrom});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboItemType, txtDate, txtRemark, txtVouNo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtAPercent, txtBPercent, txtCPercent, txtDPercent, txtEPercent, txtSPercent});

        tblCostDetail.setFont(Global.textFont);
        tblCostDetail.setModel(modelSCDT);
        tblCostDetail.setRowHeight(23);
        jScrollPane3.setViewportView(tblCostDetail);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCostPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCalculate)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCostPrice, txtPurPrice});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkCalculate)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCostPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDate.setText(strDate);

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtDateMouseClicked

    private void butFillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFillActionPerformed
        fillPercent();
    }//GEN-LAST:event_butFillActionPerformed

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void tblMedicineFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblMedicineFocusLost
        /*try{
            if(tblMedicine.getCellEditor() != null){
                tblMedicine.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblMedicineFocusLost

    private void tblUnitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblUnitFocusLost
        /*try{
            if(tblUnit.getCellEditor() != null){
                tblUnit.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblUnitFocusLost

    private void cboItemBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemBrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboItemBrandActionPerformed

    private void txtPurFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPurFromMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtPurFrom.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtPurFromMouseClicked

    private void txtPurToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPurToMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtPurTo.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtPurToMouseClicked

    private void butGetTranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGetTranActionPerformed
        if (listDetail.size() > 1) {
            calculateCost();
            getAnalystData();
        }
    }//GEN-LAST:event_butGetTranActionPerformed

    private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFrom.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFromMouseClicked

    private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTo.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtToMouseClicked

    private void butFindLostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFindLostActionPerformed
        checkLost();
    }//GEN-LAST:event_butFindLostActionPerformed

    private void chkFillWithCostPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFillWithCostPriceActionPerformed
        checkBoxSelect(chkFillWithCostPrice.getName());
    }//GEN-LAST:event_chkFillWithCostPriceActionPerformed

    private void chkFillWithSalePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFillWithSalePriceActionPerformed
        checkBoxSelect(chkFillWithSalePrice.getName());
    }//GEN-LAST:event_chkFillWithSalePriceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butFill;
    private javax.swing.JButton butFindLost;
    private javax.swing.JButton butGetTran;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboItemBrand;
    private javax.swing.JComboBox<String> cboItemType;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JCheckBox chkCalculate;
    private javax.swing.JCheckBox chkFillWithCostPrice;
    private javax.swing.JCheckBox chkFillWithSalePrice;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCostDetail;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTable tblUnit;
    private javax.swing.JTextField txtAPercent;
    private javax.swing.JTextField txtBPercent;
    private javax.swing.JTextField txtCPercent;
    private javax.swing.JTextField txtCostPrice;
    private javax.swing.JTextField txtDPercent;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtEPercent;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtPurFrom;
    private javax.swing.JTextField txtPurPrice;
    private javax.swing.JFormattedTextField txtPurTo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSPercent;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
