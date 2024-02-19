/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.inpatient.ui.util.AdmissionSearch;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemGroup;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.LocationGroup;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.Menu;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.PharmacySystem;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.database.helper.MinusPlusList;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.database.tempentity.BarcodeFilter;
import com.cv.app.pharmacy.database.tempentity.ItemCodeFilterRpt;
import com.cv.app.pharmacy.database.tempentity.TmpCostDetails;
import com.cv.app.pharmacy.database.tempentity.TmpMinusFixed;
import com.cv.app.pharmacy.database.tempentity.TmpMinusFixedKey;
import com.cv.app.pharmacy.database.tempentity.TmpMonthFilter;
import com.cv.app.pharmacy.database.tempentity.TmpStockBalOuts;
import com.cv.app.pharmacy.database.tempentity.TmpStockBalOutsKey;
import com.cv.app.pharmacy.database.tempentity.TmpStockOpeningDetailHisInsert;
import com.cv.app.pharmacy.database.view.VMedRel;
import com.cv.app.pharmacy.excel.AuditLogExcel;
import com.cv.app.pharmacy.excel.ControlDrugForm3Excel;
import com.cv.app.pharmacy.excel.ControlDrugForm3WSExcel;
import com.cv.app.pharmacy.excel.CustomerListExcel;
import com.cv.app.pharmacy.excel.GenExcel;
import com.cv.app.pharmacy.excel.ItemCodeListExcel;
import com.cv.app.pharmacy.excel.ItemCodeListWitSalePriceExcel;
import com.cv.app.pharmacy.excel.ItemCodeListWitSystemSalePriceExcel;
import com.cv.app.pharmacy.excel.ItemCodeListWithInfoExcel;
import com.cv.app.pharmacy.excel.ItemCodeListWithPurPriceExcel;
import com.cv.app.pharmacy.excel.PurchaseByDocumentExcel;
import com.cv.app.pharmacy.excel.PurchaseItemSummaryBySupplierExcel;
import com.cv.app.pharmacy.excel.PurchaseItemSummaryExcel;
import com.cv.app.pharmacy.excel.PurchaseItemTypeSummaryExcel;
import com.cv.app.pharmacy.excel.PurchaseSummaryExcel;
import com.cv.app.pharmacy.excel.ReturnInBySaleVoucherExcel;
import com.cv.app.pharmacy.excel.ReturnInItemSummaryExcel;
import com.cv.app.pharmacy.excel.ReturnOutItemSummaryExcel;
import com.cv.app.pharmacy.excel.SaleIncomeByDoctorExcel;
import com.cv.app.pharmacy.excel.SaleItemSummaryByCodeExcel;
import com.cv.app.pharmacy.excel.SaleItemSummaryByDateExcel;
import com.cv.app.pharmacy.excel.SaleItemSummaryDoctorExcel;
import com.cv.app.pharmacy.excel.SaleItemSummaryPatientAdmExcel;
import com.cv.app.pharmacy.excel.SaleItemSummaryPatientExcel;
import com.cv.app.pharmacy.excel.SaleSummaryExcel;
import com.cv.app.pharmacy.excel.StockAdjustItemSummaryExcel;
import com.cv.app.pharmacy.excel.StockBalanceBrandWithSalePriceExcel;
import com.cv.app.pharmacy.excel.StockBalanceExcel;
import com.cv.app.pharmacy.excel.StockInOutExcel;
import com.cv.app.pharmacy.excel.StockMovementExcel;
import com.cv.app.pharmacy.excel.SupplierListExcel;
import com.cv.app.pharmacy.excel.TraderBalanceExcel;
import com.cv.app.pharmacy.excel.TransferItemSummaryExcel;
import com.cv.app.pharmacy.ui.common.DoctorFilterTableCellEditor;
import com.cv.app.pharmacy.ui.common.DoctorFilterTableModel;
import com.cv.app.pharmacy.ui.common.ReportItemCodeFilterTableModel;
import com.cv.app.pharmacy.ui.common.ReportListTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.TraderFilterTableCellEditor;
import com.cv.app.pharmacy.ui.common.TraderFilterTableModel;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.json.JSONObject;

/**
 *
 * @author WSwe
 */
public class Report extends javax.swing.JPanel implements SelectionObserver, KeyPropagate {

    static Logger log = Logger.getLogger(Report.class.getName());
    private final ReportListTableModel tableModel = new ReportListTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private final MedicineUP medUp = new MedicineUP(dao);
    private ReportItemCodeFilterTableModel codeTableModel = new ReportItemCodeFilterTableModel(dao, true, medUp);
    private TraderFilterTableModel traderTableModel = new TraderFilterTableModel(dao);
    private DoctorFilterTableModel doctorFilterTableModel = new DoctorFilterTableModel(dao);
    //private final String strCodeFilter = Util1.getPropValue("system.item.location.filter");
    private int mouseClick = 2;
    private final String prefix = Util1.getPropValue("system.sale.emitted.prifix");
    private final String remotePrint = Util1.getPropValue("system.remote.print");

    /**
     * Creates new form Report
     */
    public Report() {
        initComponents();
        initTextBox();
        initCombo();
        getReportList();
        initTable();
        actionMapping();
        deleteTmpData();
        codeTableModel.setLabel(lblTotal);

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

        lblDueDate.setVisible(false);
        txtDueDate.setVisible(false);
        txtDueTo.setVisible(false);
        if (remotePrint.equals("Y")) {
            butRemotePrint.setVisible(true);
        } else {
            butRemotePrint.setVisible(false);
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboReportType, dao.findAll("Menu",
                    "parent = 19 and menuType.typeId = 5"));
            BindingUtil.BindComboFilter(cboPayment,
                    dao.findAllHSQL("select o from PaymentType o order by o.paymentTypeName"));
            BindingUtil.BindComboFilter(cboLocation,
                    dao.findAllHSQL("select o from Location o order by o.locationName"));
            BindingUtil.BindComboFilter(cboToLocation,
                    dao.findAllHSQL("select o from Location o order by o.locationName"));
            BindingUtil.BindComboFilter(cboVouStatus,
                    dao.findAllHSQL("select o from VouStatus o order by o.statusDesp"));
            BindingUtil.BindComboFilter(cboCurrency,
                    dao.findAllHSQL("select o from Currency o order by o.currencyName"));
            BindingUtil.BindComboFilter(cboCusGroup,
                    dao.findAllHSQL("select o from CustomerGroup o order by o.groupName"));
            BindingUtil.BindComboFilter(cboItemType,
                    dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
            BindingUtil.BindComboFilter(cboCategory,
                    dao.findAllHSQL("select o from Category o order by o.catName"));
            BindingUtil.BindComboFilter(cboBrand,
                    dao.findAllHSQL("select o from ItemBrand o order by o.brandName"));
            BindingUtil.BindComboFilter(cboTownship,
                    dao.findAllHSQL("select o from Township o order by o.townshipName"));
            BindingUtil.BindComboFilter(cboExpenseType,
                    dao.findAllHSQL("select o from ExpenseType o order by o.expenseName"));
            BindingUtil.BindComboFilter(cboCustomG, dao.findAllHSQL(
                    "select o from ItemGroup o order by o.groupName"));
            BindingUtil.BindComboFilter(cboSession,
                    dao.findAllHSQL("select o from Session o order by o.sessionName"));
            BindingUtil.BindComboFilter(cboLocGroup,
                    dao.findAllHSQL("select o from LocationGroup o order by o.groupName"));
            BindingUtil.BindComboFilter(cboSystem,
                    dao.findAllHSQL("select o from PharmacySystem o order by o.systemDesp"));
            BindingUtil.BindComboFilter(cboDoctor,
                    dao.findAllHSQL("select o from Doctor o order by o.doctorName"));

            AutoCompleteDecorator.decorate(cboPayment);
            AutoCompleteDecorator.decorate(cboLocation);
            AutoCompleteDecorator.decorate(cboToLocation);
            AutoCompleteDecorator.decorate(cboVouStatus);
            AutoCompleteDecorator.decorate(cboCurrency);
            AutoCompleteDecorator.decorate(cboReportType);
            AutoCompleteDecorator.decorate(cboCusGroup);
            AutoCompleteDecorator.decorate(cboCategory);
            AutoCompleteDecorator.decorate(cboBrand);
            AutoCompleteDecorator.decorate(cboTownship);
            AutoCompleteDecorator.decorate(cboExpenseType);
            AutoCompleteDecorator.decorate(cboCustomG);
            AutoCompleteDecorator.decorate(cboSession);
            AutoCompleteDecorator.decorate(cboLocGroup);
            AutoCompleteDecorator.decorate(cboSystem);
            AutoCompleteDecorator.decorate(cboDoctor);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private String getHSQL() {
        int parentMenuId = ((Menu) cboReportType.getSelectedItem()).getMenuId();
        String strSql = "select distinct m from Menu m where m.menuId in (";
        String subQuery = "select p.menuId from UserRole ur join ur.privilege p where ur.roleId ="
                + Global.loginUser.getUserRole().getRoleId() + " and p.allow = true";

        strSql = strSql + subQuery + ") and m.parent = " + parentMenuId + " order by m.menuName";

        return strSql;
    }

    private void getReportList() {
        try {
            List<Menu> listReport = dao.findAllHSQL(getHSQL());
            tableModel.setListReport(listReport);
        } catch (Exception ex) {
            log.error("getReportList : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PatientSearch":
                if (selectObj != null) {
                    Patient pt = (Patient) selectObj;
                    txtRegNo.setText(pt.getRegNo());
                    txtPtName.setText(pt.getPatientName());
                } else {
                    txtRegNo.setText(null);
                    txtPtName.setText(null);
                }

                break;
            case "AdmissionSearch":
                if (selectObj != null) {
                    Ams adm = (Ams) selectObj;
                    txtAdmNo.setText(adm.getKey().getAmsNo());
                    txtAdmName.setText(adm.getPatientName());
                } else {
                    txtAdmNo.setText(null);
                    txtAdmName.setText(null);
                }

                break;
        }
    }

    private void initTable() {
        codeTableModel.addEmptyRow();
        tblItemCodeFilter.getTableHeader().setFont(Global.lableFont);
        tblItemCodeFilter.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblItemCodeFilter.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblItemCodeFilter.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblItemCodeFilter.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblItemCodeFilter.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor(this));
        tblItemCodeFilter.getColumnModel().getColumn(3).setCellEditor(new CodeTableUnitCellEditor());

        traderTableModel.addEmptyRow();
        tblTraderFilter.getTableHeader().setFont(Global.lableFont);
        tblTraderFilter.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblTraderFilter.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblTraderFilter.getColumnModel().getColumn(0).setCellEditor(
                new TraderFilterTableCellEditor(dao));

        doctorFilterTableModel.addEmptyRow();
        tblDoctorFilter.getTableHeader().setFont(Global.lableFont);
        tblDoctorFilter.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblDoctorFilter.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblDoctorFilter.getColumnModel().getColumn(0).setCellEditor(
                new DoctorFilterTableCellEditor(dao));

        tblReportList.getTableHeader().setFont(Global.lableFont);
        //Define table selection model to single row selection.
        tblReportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblReportList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //selectedRow = tblReportList.getSelectedRow();
            }
        });
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
        }
    }

    private void initTextBox() {
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
    }

    private final Action actionItemFilterDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblItemCodeFilter.getSelectedRow() >= 0) {
                codeTableModel.delete(tblItemCodeFilter.getSelectedRow());
            }
        }
    };

    private final Action actionTraderFilterDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblTraderFilter.getSelectedRow() >= 0) {
                traderTableModel.delete(tblTraderFilter.getSelectedRow());
            }
        }
    };

    private final Action actionDoctorFilterDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblDoctorFilter.getSelectedRow() >= 0) {
                doctorFilterTableModel.delete(tblDoctorFilter.getSelectedRow());
            }
        }
    };

    private void actionMapping() {
        tblItemCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblItemCodeFilter.getActionMap().put("F8-Action", actionItemFilterDelete);

        tblTraderFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblTraderFilter.getActionMap().put("F8-Action", actionTraderFilterDelete);

        tblDoctorFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblDoctorFilter.getActionMap().put("F8-Action", actionDoctorFilterDelete);
    }

    private void insertStockFilterCode() {
        int locationId = 0;
        int index = tblReportList.convertRowIndexToModel(tblReportList.getSelectedRow());
        Menu report = tableModel.getSelectedReport(index);
        String strOpDate = DateUtil.toDateStrMYSQL(txtFrom.getText());

        switch (report.getMenuClass()) {
            case "StockBalanceBrand":
            case "StockBalanceBrandWithSalePrice":
            case "StockBalanceExp":
            case "StockBalanceExpKS":
            case "StockBalance":
            case "StockBalanceKS":
            case "StockBalanceAllLoc":
            case "StockBalanceSystem":
                strOpDate = DateUtil.toDateStrMYSQL(txtTo.getText());
                break;
        }

        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        /*String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + strOpDate + "'";*/
        String strSQL = "insert into tmp_stock_filter\n"
                + "select m.location_id, m.med_id, '1900-01-01', '" + Global.machineId + "' \n"
                + "from v_med_loc m\n"
                + "where m.calc_stock = true";

        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }

        if (locationId != 0) {
            strSQL = strSQL + " and m.location_id = " + locationId;
        }

        //Need to change
        /*if (strCodeFilter.equals("Y")) {
            if (locationId == 0) {
                strSQL = strSQL + " and m.med_id in ()";
            } else {

            }
        }*/
        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();
            strSQL = strSQL + " and m.med_type_id = '" + itemType.getItemTypeCode() + "'";
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();
            strSQL = strSQL + " and m.category_id = " + cat.getCatId();
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            strSQL = strSQL + " and m.brand_id = " + itemBrand.getBrandId();
        }

        if (codeTableModel.getListCodeFilter().size() > 1) {
            strSQL = strSQL + " and m.med_id in (select item_code from "
                    + "tmp_item_code_filter_rpt where user_id = '"
                    + Global.machineId + "')";
        }

        if (cboCustomG.getSelectedItem() instanceof ItemGroup) {
            ItemGroup ig = (ItemGroup) cboCustomG.getSelectedItem();
            strSQL = strSQL + " and m.med_id in (select item_id from item_group_detail where group_id = "
                    + ig.getGroupId().toString() + ")";
        }

        String itemActive = cboItemStatus.getSelectedItem().toString();
        if (!itemActive.equals("All")) {
            if (itemActive.equals("Active")) {
                strSQL = strSQL + " and m.active = true";
            } else if (itemActive.equals("In Active")) {
                strSQL = strSQL + " and m.active = false";
            }
        }

        if (cboSystem.getSelectedItem() instanceof PharmacySystem) {
            PharmacySystem ps = (PharmacySystem) cboSystem.getSelectedItem();
            strSQL = strSQL + " and m.phar_sys_id = " + ps.getId();
        }

        /*strSQL = strSQL + " and m.med_id in (select distinct med_id from stock_op_detail_his where med_id in "
                + "(select med_id from medicine where active = false)\n"
                + "and op_qty <> 0)";*/
        try {
            if (dao.getRowCount("select count(*) from item_type_mapping where group_id ="
                    + Global.loginUser.getUserRole().getRoleId()) > 0) {
                strSQL = strSQL + " and m.med_type_id in (select item_type_code from item_type_mapping where "
                        + "group_id = " + Global.loginUser.getUserRole().getRoleId() + ")";
            }
            dao.open();
            dao.execSql(strSQLDelete);
            dao.close();

            dao.open();
            dao.execSql(strSQL);
            String strSql = "update tmp_stock_filter tsf join (select location_id, med_id, max(op_date) op_date from med_op_date \n"
                    + "where op_date <= '" + strOpDate + "' \n"
                    + "	group by location_id, med_id) meod on tsf.med_id = meod.med_id  and tsf.location_id = meod.location_id\n"
                    + "set tsf.op_date = meod.op_date\n"
                    + "where tsf.user_id = '" + Global.machineId + "'";
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    /*private void insertItemFilterCode() {
        String strDelSql = "delete from tmp_code_filter where user_id = '"
                + Global.machineId + "'";
        String strSql = "insert into tmp_code_filter(item_code, user_id) "
                + "select m.med_id, '" + Global.machineId + "' from medicine m ";
        String strWhere = "";

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();
            if (!strWhere.isEmpty()) {
                strWhere = strWhere + " and m.med_type_id = '" + itemType.getItemTypeCode() + "'";
            } else {
                strWhere = "m.med_type_id = '" + itemType.getItemTypeCode() + "'";
            }
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();
            if (!strWhere.isEmpty()) {
                strWhere = strWhere + " and m.category_id = " + cat.getCatId();
            } else {
                strWhere = "m.category_id = " + cat.getCatId();
            }
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            if (!strWhere.isEmpty()) {
                strWhere = strWhere + " and m.brand_id = " + itemBrand.getBrandId();
            } else {
                strWhere = "m.brand_id = " + itemBrand.getBrandId();
            }
        }

        if (codeTableModel.getListCodeFilter().size() > 1) {
            if (!strWhere.isEmpty()) {
                strWhere = strWhere + " and m.med_id in (select item_code from "
                        + "tmp_item_code_filter_rpt where user_id = '"
                        + Global.machineId + "')";
            } else {
                strWhere = "m.med_id in (select item_code from "
                        + "tmp_item_code_filter_rpt where user_id = '"
                        + Global.machineId + "')";
            }
        }

        if (!strWhere.isEmpty()) {
            strSql = strSql + strWhere;
        }

        try {
            dao.open();
            dao.execSql(strDelSql);
            dao.close();

            dao.open();
            dao.execSql(strSql);
            //dao.execSql(strDelSql, strSql);
        } catch (Exception ex) {
            log.error("insertItemFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }*/
    private void insertTraderFilterCode(String disc) {
        String currencyId;
        String strSQLDelete = "delete from tmp_trader_bal_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_trader_bal_filter(trader_id,currency,op_date,user_id, amount) "
                + "select t.trader_id, t.cur_code, "
                + " ifnull(trop.op_date, '1900-01-01'),'" + Global.machineId
                + "', sum(ifnull(trop.op_amount,0))"
                + " from v_trader_cur t left join "
                + "(select a.trader_id, a.currency, a.op_date, a.op_amount\n"
                + "from trader_op a, (select trader_id, currency, max(op_date) op_date from trader_op \n"
                + "where op_date <= '" + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' group by trader_id, currency) b\n"
                + "where a.trader_id = b.trader_id and a.currency = b.currency and a.op_date = b.op_date) trop on t.trader_id = trop.trader_id \n"
                + " and t.cur_code = trop.currency ";

        String strFilter = "";

        if (disc.equals("C") || disc.equals("S") || disc.equals("P")) {
            if (strFilter.isEmpty()) {
                strFilter = "discriminator = '" + disc + "'";
            } else {
                strFilter = strFilter + " and discriminator = '" + disc + "'";
            }
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            currencyId = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            if (strFilter.isEmpty()) {
                strFilter = "t.cur_code = '" + currencyId + "'";
            } else {
                strFilter = strFilter + " and t.cur_code = '" + currencyId + "'";
            }
        }

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            String cusGID = ((CustomerGroup) cboCusGroup.getSelectedItem()).getGroupId();
            if (strFilter.isEmpty()) {
                strFilter = "group_id = '" + cusGID + "'";
            } else {
                strFilter = strFilter + " and group_id = '" + cusGID + "'";
            }
        }

        if (cboTownship.getSelectedItem() instanceof Township) {
            String townshipId = ((Township) cboTownship.getSelectedItem()).getTownshipId().toString();
            if (strFilter.isEmpty()) {
                strFilter = "t.township = " + townshipId;
            } else {
                strFilter = strFilter + " and t.township = " + townshipId;
            }
        }

        if (traderTableModel.getRowCount() > 1) {
            if (strFilter.isEmpty()) {
                strFilter = "t.trader_id in (select trader_id from tmp_trader_filter "
                        + " where user_id = '" + Global.machineId + "')";
            } else {
                strFilter = strFilter + " and t.trader_id in (select trader_id from tmp_trader_filter "
                        + " where user_id = '" + Global.machineId + "')";
            }
        }

        if (prefix.equals("Y")) {
            if (cboLocation.getSelectedItem() instanceof Location) {
                int locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                if (strFilter.isEmpty()) {
                    strFilter = "t.trader_id in (select trader_id from location_trader_mapping where location_id = " + locationId + ")";
                } else {
                    strFilter = strFilter + " and t.trader_id in (select trader_id from location_trader_mapping where location_id = " + locationId + ")";
                }
            } else {
                /*if(strFilter.isEmpty()){
                    strFilter = "t.trader_id in ()";
                }else{
                    strFilter = strFilter + " and t.trader_id in ()";
                }*/
            }
        }

        if (!strFilter.isEmpty()) {
            strSQL = strSQL + " where " + strFilter;
        }

        strSQL = strSQL + " group by t.trader_id, t.cur_code,  ifnull(trop.op_date, '1900-01-01')";

        try {
            dao.open();
            dao.execSql(strSQLDelete);
            dao.close();

            dao.open();
            dao.execSql(strSQL);
            //dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertTraderFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    /*private String getUserLocationFilterSql(String option) {
        return null;
    }*/

 /*
     * option = -1 for sale outstand option = -2 for purchase outstand
     */
 /*private void insertStockOutsFilterCode(int option) {
        String filter = "";
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "' and location_id = " + option;
        String strSQL = "insert into tmp_stock_filter(location_id, med_id, user_id) "
                + "select " + option + ", m.med_id, '" + Global.machineId
                + "' from medicine m ";

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();

            if (filter.isEmpty()) {
                filter = "m.med_type_id = '" + itemType.getItemTypeCode() + "'";
            } else {
                filter = filter + " and m.med_type_id = '" + itemType.getItemTypeCode() + "'";
            }
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();

            if (filter.isEmpty()) {
                filter = "m.category_id = " + cat.getCatId();
            } else {
                filter = filter + " and m.category_id = " + cat.getCatId();
            }
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();

            if (filter.isEmpty()) {
                filter = filter + "m.brand_id = " + itemBrand.getBrandId();
            } else {
                filter = filter + " and m.brand_id = " + itemBrand.getBrandId();
            }
        }

        if (codeTableModel.getListCodeFilter().size() > 1) {
            if (filter.isEmpty()) {
                filter = "m.med_id in (select item_code from "
                        + "tmp_item_code_filter_rpt where user_id = '"
                        + Global.machineId + "')";
            } else {
                filter = filter + " and m.med_id in (select item_code from "
                        + "tmp_item_code_filter_rpt where user_id = '"
                        + Global.machineId + "')";
            }
        }

        if (!filter.isEmpty()) {
            strSQL = strSQL + " where " + filter;
        }

        try {
            dao.open();
            dao.execSql(strSQLDelete);
            dao.close();

            dao.open();
            dao.execSql(strSQL);
            //dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockOutsFilter : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }*/
    private void reportGeneration() {
        try {
            if (tblDoctorFilter.getCellEditor() != null) {
                tblDoctorFilter.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        try {
            if (tblItemCodeFilter.getCellEditor() != null) {
                tblItemCodeFilter.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        try {
            if (tblTraderFilter.getCellEditor() != null) {
                tblTraderFilter.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        int index = tblReportList.convertRowIndexToModel(tblReportList.getSelectedRow());
        if (index >= 0) {
            try {
                Menu report = tableModel.getSelectedReport(index);
                Menu parent = (Menu) dao.find(Menu.class, report.getParent());
                String disc = "A";

                if (parent.getMenuClass().equals("CustomerBalanceUPV")) {
                    disc = "C";
                }
                insertStockFilterCode();
                insertTraderFilterCode(disc);
                switch (report.getMenuClass()) {
                    case "StockBalanceBrand":
                    case "StockBalanceBrandWithSalePrice":
                    case "StockBalanceExp":
                    case "StockBalanceExpKS":
                    case "StockBalance":
                    case "StockBalanceKS":
                    case "StockBalanceAllLoc":
                    case "StockBalanceSystem":
                        DateUtil.setStartTime();
                        execStockBalanceExp(txtTo.getText());
                        //String strDueFrom = Util1.nullToBlankStr(txtDueDate.getText());
                        String strDueTo = Util1.nullToBlankStr(txtDueTo.getText());
                        if (!strDueTo.isEmpty() && !strDueTo.equals("")) {
                            String strSql = "delete from tmp_stock_balance_exp where user_id = '"
                                    + Global.machineId + "' and exp_date > '"
                                    + DateUtil.toDateStrMYSQL(strDueTo) + "'";
                            try {
                                dao.execSql(strSql);
                            } catch (Exception ex) {
                                log.error("delete : " + ex.getMessage());
                            }
                        }
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "StockExpireList":
                        DateUtil.setStartTime();
                        execStockBalanceExp(DateUtil.toDateStr(new Date()));
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "StockMovementExp":
                    case "StockMovementExpKS":
                    case "StockMovement":
                    case "StockMovementKS":
                    case "stockMovementPurchaseOnly":
                        DateUtil.setStartTime();
                        execStockMovementExp();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "StockMovementExpKSReOrder":
                    case "StockMovementExpKSReOrderNoSale":
                    case "StockMovementExpKSReOrderNoPur":
                    case "StockMovementExpKSReOrderChem":
                        DateUtil.setStartTime();
                        execStockMovementExp1(report.getMenuClass());
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "CustomerBalance":
                    case "CustomerBalanceByLocation":
                    case "CustomerBalanceByTownship":
                    case "SupplierBalance":
                    case "CustomerBalance_Tsp":
                        DateUtil.setStartTime();
                        execTraderBalanceDate();
                        deleteTraderBalanceZero();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "CustomerInOutBalance":
                        DateUtil.setStartTime();
                        execTraderBalanceDetail();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "CustomerInOutBalanceDetailA4":
                    case "CustomerInOutBalanceDetailA4BT":
                        DateUtil.setStartTime();
                        execTraderBalance();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "SupplierInOutBalance":
                    case "SupplierInOutBalanceDetailA4":
                        DateUtil.setStartTime();
                        supplierTraderBalanceDetail();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "SupplierInOutBalanceSummaryA4":
                        DateUtil.setStartTime();
                        supplierTraderBalanceSummary();
                        //execTraderInOutSummary();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "CustomerInOutBalanceDetail":
                        DateUtil.setStartTime();
                        execTraderBalance();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "rptCusBalanceRemark":
                        DateUtil.setStartTime();
                        execTraderBalanceDetailRemark();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "CustomerInOutBalSummary":
                    case "SupplierInOutBalSummary":
                    case "CustomerInOutBalSummaryByDaily":
                    case "BIDCustomer":
                        DateUtil.setStartTime();
                        execTraderInOutSummary();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "StockInOut":
                    case "StockInOutKS":
                        DateUtil.setStartTime();
                        execStockInOutBal();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "rptBarCode":
                    case "rptBarCodeMO":
                    case "rptBarCode_Time_Store":
                    case "rptCodePriceLabel":
                        //fillBarcode();
                        insertBarcodeFilter();
                        break;
                    case "CustomerBalanceUPV":
                        DateUtil.setStartTime();
                        execTraderBalanceWithUPV();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "rptGroundDifference":
                        DateUtil.setStartTime();
                        execStockBalanceExp(txtTo.getText());
                        fixMinusBalance(txtTo.getText());
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "rptDailyTransaction":
                        DateUtil.setStartTime();
                        printSessionRpt();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "rptSaleDoctorDetail":
                    case "rptSaleDoctorSummary":
                        break;
                    case "ControlDrugForm3WS":
                        try {
                        DateUtil.setStartTime();
                        dao.execProc("control_drug_in_out1",
                                DateUtil.toDateStrMYSQL(txtFrom.getText()),
                                DateUtil.toDateStrMYSQL(txtTo.getText()),
                                getLocationId().toString(),
                                Global.machineId);
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                    } catch (Exception ex) {
                        log.error("ControlDrugForm3WS control_drug_in_out1 : " + ex.toString());
                    } finally {
                        dao.close();
                    }
                    break;
                    case "ControlDrugForm3":
                    case "ControlDrugForm3MO":
                        try {
                        DateUtil.setStartTime();
                        dao.execProc("control_drug_in_out",
                                DateUtil.toDateStrMYSQL(txtFrom.getText()),
                                DateUtil.toDateStrMYSQL(txtTo.getText()),
                                getLocationId().toString(),
                                Global.machineId);
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                    } catch (Exception ex) {
                        log.error("ControlDrugForm3MO control_drug_in_out : " + ex.toString());
                    } finally {
                        dao.close();
                    }
                    break;
                    case "BorrowBalance":
                        DateUtil.setStartTime();
                        calculateBorrowBalance();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "CustomerBalanceNoSale":
                        DateUtil.setStartTime();
                        execTraderBalanceDateNoSale();
                        deleteTraderBalanceZero();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    case "CustomerBalanceNoPay":
                        DateUtil.setStartTime();
                        execTraderBalanceDateNoPay();
                        deleteTraderBalanceZero();
                        log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                        break;
                    default:
                        System.out.println("Invalid Report");
                }

                clearValue(report.getMenuClass());

                String reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + report.getMenuUrl();
                Map<String, Object> params = getParameter(report.getMenuClass());
                params.put("p_report_name", report.getMenuName());
                switch (report.getMenuClass()) {
                    case "SalePurSummaryMonthlyC":
                    case "SaleAnalystUnit":
                    case "PurAnalystUnit":
                    case "SaleMonthlyC":
                    case "PurMonthlyC":
                        int location = getLocationId();
                        insertMonthFilter(txtFrom.getText(), txtTo.getText(),
                                Global.machineId, params, location);
                        break;
                }
                dao.close();
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
                dao.commit();
            } catch (Exception ex) {
                log.info("rpt : " + ex.getMessage());
            }

            /*if (report.getMenuClass().equals("ControlDrugForm3") && report.getMenuUrl().equals("xls")) {
             ResultSet rs = null;
             try {
             rs = dao.execSQL("select tran_date, item_id, item_name, tsio.location_id, location_name, "
             + "op_qty, sale_qty, in_ttl, out_ttl, closing,\n"
             + "name1 as vou_no, reg_no, name2 as pt_name, name3 as nric, dr.doctor_name\n"
             + "from tmp_stock_in_out tsio join location l on  tsio.location_id = l.location_id\n"
             + "left join doctor dr on tsio.name4 = dr.doctor_id\n"
             + "where user_id = '" + Global.loginUser.getUserId()
             + "' and (sale_qty <> 0 or in_ttl <> 0 or out_ttl <> 0)\n"
             + "order by tran_id, item_id, tran_date;");
             POIUtil util = new POIUtil();
             util.createControlDrugForm3(rs, "/Users/winswe/Documents/mws/CoreValue/Cus_program/wesley/test.xls");
             } catch (Exception ex) {
             log.error("excel : " + ex);
             } finally {
             if(rs != null){
             try{
             rs.close();
             }catch(Exception ex1){
                            
             }
             }
             dao.close();
             }
             } else {*/
            //}
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select report in the list.",
                    "No Report Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearValue(String reportName) {
        String strSql = "";

        switch (reportName) {
            case "StockBalanceExp":
            case "StockBalanceBrandWithSalePrice":
            case "StockBalanceExpKS":
            case "StockBalance":
            case "StockBalanceKS":
            case "StockBalanceAllLoc":
            case "StockBalanceSystem":
                try {
                if (!chkMinus.isSelected()) {
                    if (strSql.isEmpty()) {
                        strSql = "a.bal_qty < 0";
                    } else {
                        strSql = strSql + " or a.bal_qty < 0";
                    }
                }

                if (!chkZero.isSelected()) {
                    if (strSql.isEmpty()) {
                        strSql = "a.bal_qty = 0";
                    } else {
                        strSql = strSql + " or a.bal_qty = 0";
                    }
                }

                if (!chkPlus.isSelected()) {
                    if (strSql.isEmpty()) {
                        strSql = "a.bal_qty > 0";
                    } else {
                        strSql = strSql + " or a.bal_qty > 0";
                    }
                }

                if (!strSql.isEmpty()) {
                    strSql = "delete t from tmp_stock_balance_exp t "
                            + "inner join (select med_id, user_id, location_id, bal_qty "
                            + "from (select med_id, user_id, location_id, sum(ifnull(bal_qty,0)) bal_qty "
                            + "	    from tmp_stock_balance_exp where user_id = '"
                            + Global.machineId + "' group by user_id, location_id, med_id) a "
                            + "where (" + strSql + ")) dt on t.user_id = dt.user_id and t.med_id = dt.med_id and t.location_id = dt.location_id "
                            + "where t.user_id = '" + Global.machineId + "'";
                    dao.execSql(strSql);
                }
            } catch (Exception ex) {
                log.error("clearValue : StockBalanceExp : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "StockMovementExp":
            case "StockMovementExpKS":
            case "StockMovement":
            case "StockMovementKS":
                /*if (!chkMinus.isSelected()) {
                 if(strSql.isEmpty()){
                 strSql = "bal_qty < 0";
                 }else{
                 strSql = strSql + " or bal_qty < 0";
                 }
                 }
                
                 if (!chkZero.isSelected()) {
                 if(strSql.isEmpty()){
                 strSql = "bal_qty = 0";
                 }else{
                 strSql = strSql + " or bal_qty = 0";
                 }
                 }
                
                 if (!chkPlus.isSelected()) {
                 if(strSql.isEmpty()){
                 strSql = "bal_qty > 0";
                 }else{
                 strSql = strSql + " or bal_qty > 0";
                 }
                 }
                
                 if(!strSql.isEmpty()){
                 strSql = "delete from tmp_stock_balance_exp where user_id = '" +
                 Global.loginUser.getUserId() + "' and (" + strSql+ ")";
                 dao.execSql(strSql);
                 }*/
                break;
        }
    }

    private void execStockBalanceExp(String stockDate) {
        Integer location = 0;

        if (cboLocation.getSelectedItem() instanceof Location) {
            location = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        //PharmacyReport.StockBalanceExp(dao, stockDate, location.toString(), Global.loginUser.getUserId());
        try {
            dao.execSql("delete from tmp_stock_balance_exp where user_id = '"
                    + Global.machineId + "'");
            /*dao.execProc("stock_balance_exp", "Opening",
                    DateUtil.toDateStrMYSQL(stockDate),
                    location.toString(),
                    Global.loginUser.getUserId());
            "insert into tmp_stock_balance_exp(user_id, tran_option, location_id, med_id, exp_date,\n"
                    + "                bal_qty, qty_str)\n"
                    + 
             */
            String strSql = "insert into tmp_stock_balance_exp(user_id, tran_option, location_id, med_id, exp_date,\n"
                    + "                bal_qty, qty_str)\n"
                    + "    select prm_user_id, A.tran_option, A.location_id, A.med_id,\n"
                    + "           ifnull(A.exp_date, '1900-01-01'), sum(A.ttl_qty) as ttl_qty,\n"
                    + "           get_qty_in_str(ifnull(sum(A.ttl_qty),0), B.unit_smallest, B.unit_str)\n"
                    + "      from (\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Opening', prm_tran_opt) tran_option,\n"
                    + "                   vso.location location_id, vso.med_id, vso.expire_date exp_date,\n"
                    + "                   sum(ifnull(vso.op_smallest_qty,0)) ttl_qty\n"
                    + "              from v_stock_op vso, tmp_stock_filter tsf\n"
                    + "             where vso.location = tsf.location_id and vso.med_id = tsf.med_id\n"
                    + "               and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and (vso.location = prm_location or prm_location = 0)\n"
                    + "             group by vso.location, vso.med_id,vso.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Sale', prm_tran_opt) tran_option,\n"
                    + "                   vs.location_id, vs.med_id, vs.expire_date exp_date,\n"
                    + "                   sum((ifnull(vs.sale_smallest_qty, 0)+ifnull(vs.foc_smallest_qty,0))*-1) ttl_qty\n"
                    + "              from v_sale1 vs, tmp_stock_filter tsf\n"
                    + "             where vs.location_id = tsf.location_id and vs.med_id = tsf.med_id\n"
                    + "               and date(vs.sale_date) >= tsf.op_date and date(vs.sale_date) <= prm_stock_date\n"
                    + "               and vs.deleted = false and vs.vou_status = 1 and tsf.user_id = prm_user_id\n"
                    + "               and (vs.location_id = prm_location or prm_location = 0)\n"
                    + "             group by vs.location_id, vs.med_id, vs.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Purchase', prm_tran_opt) tran_option,\n"
                    + "                   vp.location location_id, vp.med_id, vp.expire_date exp_date,\n"
                    + "                   sum(ifnull(vp.pur_smallest_qty,0)+ifnull(vp.pur_foc_smallest_qty,0)) ttl_qty\n"
                    + "              from v_purchase vp, tmp_stock_filter tsf\n"
                    + "             where vp.location = tsf.location_id and vp.med_id = tsf.med_id\n"
                    + "               and date(vp.pur_date) >= tsf.op_date and date(vp.pur_date) <= prm_stock_date\n"
                    + "               and vp.deleted = false and vp.vou_status = 1 and tsf.user_id = prm_user_id\n"
                    + "               and (vp.location = prm_location or prm_location = 0)\n"
                    + "             group by vp.location, vp.med_id, vp.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Return In', prm_tran_opt) tran_option,\n"
                    + "                   vri.location location_id, vri.med_id, vri.expire_date exp_date,\n"
                    + "                   sum(ifnull(vri.ret_in_smallest_qty,0)) ttl_qty\n"
                    + "              from v_return_in vri, tmp_stock_filter tsf\n"
                    + "             where vri.location = tsf.location_id and vri.med_id = tsf.med_id\n"
                    + "               and date(vri.ret_in_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vri.ret_in_date) <= prm_stock_date and vri.deleted = false\n"
                    + "               and (vri.location = prm_location or prm_location = 0)\n"
                    + "             group by vri.location, vri.med_id, vri.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Return Out', prm_tran_opt) tran_option,\n"
                    + "                   vro.location location_id, vro.med_id, vro.expire_date exp_date,\n"
                    + "                   sum(ifnull(ret_out_smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_return_out vro, tmp_stock_filter tsf\n"
                    + "             where vro.location = tsf.location_id and vro.med_id = tsf.med_id\n"
                    + "               and date(vro.ret_out_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vro.ret_out_date) <= prm_stock_date and vro.deleted = false\n"
                    + "               and (vro.location = prm_location or prm_location = 0)\n"
                    + "             group by vro.location , vro.med_id, vro.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Adjust', prm_tran_opt) tran_option,\n"
                    + "                   va.location location_id, va.med_id, va.expire_date exp_date,\n"
                    + "                   sum(if(va.adj_type = '-',(ifnull(va.adj_smallest_qty,0)*-1),\n"
                    + "                        ifnull(va.adj_smallest_qty,0))) ttl_qty\n"
                    + "              from v_adj va, tmp_stock_filter tsf\n"
                    + "             where va.location = tsf.location_id and va.med_id = tsf.med_id\n"
                    + "               and date(va.adj_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(va.adj_date) <= prm_stock_date and va.deleted = false\n"
                    + "               and (va.location = prm_location or prm_location = 0)\n"
                    + "             group by va.location, va.med_id, va.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Transfer From', prm_tran_opt) tran_option,\n"
                    + "                   vt.from_location location_id, vt.med_id, vt.expire_date exp_date,\n"
                    + "                   sum(ifnull(tran_smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_transfer vt, tmp_stock_filter tsf\n"
                    + "             where vt.from_location = tsf.location_id and vt.med_id = tsf.med_id\n"
                    + "               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n"
                    + "               and (vt.from_location = prm_location or prm_location = 0)\n"
                    + "             group by vt.from_location, vt.med_id, vt.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Transfer To', prm_tran_opt) tran_option,\n"
                    + "                   vt.to_location location_id, vt.med_id, vt.expire_date exp_date,\n"
                    + "                   sum(ifnull(tran_smallest_qty,0)) ttl_qty\n"
                    + "              from v_transfer vt, tmp_stock_filter tsf\n"
                    + "             where vt.to_location = tsf.location_id and vt.med_id = tsf.med_id\n"
                    + "               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n"
                    + "               and (vt.to_location = prm_location or prm_location = 0)\n"
                    + "             group by vt.to_location, vt.med_id, vt.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Issue', prm_tran_opt) tran_option,\n"
                    + "                   vsi.location_id, vsi.med_id, vsi.expire_date exp_date,\n"
                    + "                   sum(ifnull(smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_stock_issue vsi, tmp_stock_filter tsf\n"
                    + "             where vsi.location_id = tsf.location_id and vsi.med_id = tsf.med_id\n"
                    + "               and date(vsi.issue_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vsi.issue_date) <= prm_stock_date and vsi.deleted = false\n"
                    + "               and (vsi.location_id = prm_location or prm_location = 0)\n"
                    + "             group by vsi.location_id, vsi.med_id, vsi.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Receive', prm_tran_opt) tran_option,\n"
                    + "                   vsr.location_id, vsr.rec_med_id med_id, vsr.expire_date exp_date,\n"
                    + "                   sum(ifnull(smallest_qty,0)) ttl_qty\n"
                    + "              from v_stock_receive vsr, tmp_stock_filter tsf\n"
                    + "             where vsr.location_id = tsf.location_id and vsr.rec_med_id = tsf.med_id\n"
                    + "               and date(vsr.receive_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vsr.receive_date) <= prm_stock_date and vsr.deleted = false\n"
                    + "               and (vsr.location_id = prm_location or prm_location = 0)\n"
                    + "             group by vsr.location_id, vsr.rec_med_id, vsr.expire_date\n"
                    + "             union all\n"
                    + "            select if(prm_tran_opt = 'Balance', 'Damage', prm_tran_opt) tran_option,\n"
                    + "                   vd.location location_id, vd.med_id, vd.expire_date exp_date,\n"
                    + "                   sum(ifnull(dmg_smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_damage vd, tmp_stock_filter tsf\n"
                    + "             where vd.location = tsf.location_id and vd.med_id = tsf.med_id\n"
                    + "               and date(vd.dmg_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vd.dmg_date) <= prm_stock_date and vd.deleted = false\n"
                    + "               and (vd.location = prm_location or prm_location  = 0)\n"
                    + "             group by vd.location, vd.med_id, vd.expire_date"
                    + "             union all \n"
                    + "            select if(prm_tran_opt = 'Balance', 'Damage', prm_tran_opt) tran_option,\n"
                    + "                   mu.location_id, mu.med_id, null as exp_date, \n"
                    + "                   sum(ifnull(mu.qty_smallest,0)*-1) ttl_qty\n" 
                    + "              from med_usaged mu, tmp_stock_filter tsf \n" 
                    + "             where mu.location_id = tsf.location_id and mu.med_id = tsf.med_id\n" 
                    + "               and date(mu.created_date) between tsf.op_date and prm_stock_date and tsf.user_id = prm_user_id\n" 
                    + "             group by mu.location_id, mu.med_id \n"
                    + ") A,\n"
                    + "            v_med_unit_smallest_rel B\n"
                    + "     where A.med_id = B.med_id\n"
                    + "     group by A.tran_option, A.location_id, A.med_id, A.exp_date";
            strSql = strSql.replace("prm_user_id", "'" + Global.machineId + "'")
                    .replace("prm_tran_opt", "'Opening'")
                    .replace("prm_stock_date", "'" + DateUtil.toDateStrMYSQL(stockDate) + "'")
                    .replace("prm_location", location.toString());
            dao.execSql(strSql);
            fixedMinus(Global.machineId);
        } catch (Exception ex) {
            log.error("execStockBalanceExp : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void execStockMovementExp() {
        try {
            Integer location = 0;

            if (cboLocation.getSelectedItem() instanceof Location) {
                location = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }

            dao.execProc("stock_movement_exp",
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()),
                    location.toString(),
                    Global.machineId);
            fixedMinus(Global.machineId);
        } catch (Exception ex) {
            log.error("execStockMovementExp : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execStockMovementExp1(String report) {
        try {
            Integer location = 0;

            if (cboLocation.getSelectedItem() instanceof Location) {
                location = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }

            dao.execProc("stock_movement_exp1",
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()),
                    location.toString(),
                    Global.machineId);

            String strSql = "update tmp_stock_balance_exp tsbe, v_med_cost_price vmcp\n"
                    + "set tsbe.pur_price = vmcp.cost_price, tsbe.pur_unit = vmcp.cost_unit\n"
                    + "where tsbe.med_id = vmcp.med_id and tsbe.user_id = '" + Global.machineId + "'";

            dao.execSql(strSql);

            switch (report) {
                case "StockMovementExpKSReOrder":
                    if (!chkMinus.isSelected()) {
                        String strMinusDelete = "delete from tmp_stock_balance_exp where user_id = '"
                                + Global.machineId + "' and med_id in "
                                + "(select med_id from v_stock_movement_exp1 vsm where user_id = '"
                                + Global.machineId + "' and ((vsm.ttl_sale*-1)-vsm.ttl_stock_balance)<0)";
                        dao.execSql(strMinusDelete);
                    }

                    if (!chkZero.isSelected()) {
                        String strZeroDelete = "delete from tmp_stock_balance_exp where user_id = '"
                                + Global.machineId + "' and med_id in "
                                + "(select med_id from v_stock_movement_exp1 vsm where user_id = '"
                                + Global.machineId + "' and ((vsm.ttl_sale*-1)-vsm.ttl_stock_balance)=0)";
                        dao.execSql(strZeroDelete);
                    }

                    if (!chkPlus.isSelected()) {
                        String strPlusDelete = "delete from tmp_stock_balance_exp where user_id = '"
                                + Global.machineId + "' and med_id in "
                                + "(select med_id from v_stock_movement_exp1 vsm where user_id = '"
                                + Global.machineId + "' and ((vsm.ttl_sale*-1)-vsm.ttl_stock_balance)>0)";
                        dao.execSql(strPlusDelete);
                    }
                    break;
                case "StockMovementExpKSReOrderNoSale":
                case "StockMovementExpKSReOrderNoPur":
                case "StockMovementExpKSReOrderChem":
                    if (!chkMinus.isSelected()) {
                        String strMinusDelete = "delete from tmp_stock_balance_exp where user_id = '"
                                + Global.machineId + "' and med_id in "
                                + "(select med_id from v_stock_movement_exp1 vsm where user_id = '"
                                + Global.machineId + "' and (vsm.ttl_stock_balance<0)";
                        dao.execSql(strMinusDelete);
                    }

                    if (!chkZero.isSelected()) {
                        String strZeroDelete = "delete from tmp_stock_balance_exp where user_id = '"
                                + Global.machineId + "' and med_id in "
                                + "(select med_id from v_stock_movement_exp1 vsm where user_id = '"
                                + Global.machineId + "' and (vsm.ttl_stock_balance=0)";
                        dao.execSql(strZeroDelete);
                    }

                    if (!chkPlus.isSelected()) {
                        String strPlusDelete = "delete from tmp_stock_balance_exp where user_id = '"
                                + Global.machineId + "' and med_id in "
                                + "(select med_id from v_stock_movement_exp1 vsm where user_id = '"
                                + Global.machineId + "' and (vsm.ttl_stock_balance>0)";
                        dao.execSql(strPlusDelete);
                    }
                    break;
            }
            fixedMinus(Global.machineId);
        } catch (Exception ex) {
            log.error("execStockMovementExp1 : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceDate() {
        try {
            dao.execProc("trader_balance_date",
                    DateUtil.toDateStrMYSQL(txtTo.getText()),
                    Global.machineId);
        } catch (Exception ex) {
            log.error("execTraderBalanceDate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceDateNoSale() {
        try {
            dao.execProc("trader_balance_date",
                    DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()),
                    Global.machineId);
            String strSql = "delete from tmp_trader_bal_date where user_id = '"
                    + Global.machineId + "' \n"
                    + "and trader_id in (select distinct cus_id from sale_his where deleted = false \n"
                    + "and date(sale_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "')";
            log.info("execTraderBalanceDateNoSale : " + strSql);
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("execTraderBalanceDate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceDateNoPay() {
        try {
            dao.execProc("trader_balance_date",
                    DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()),
                    Global.machineId);
            String strSql = "delete from tmp_trader_bal_date where user_id = '"
                    + Global.machineId + "' \n"
                    + "and trader_id in (select distinct trader_id from payment_his where deleted = false \n"
                    + "and pay_date between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "')";
            log.info("execTraderBalanceDateNoPay : " + strSql);
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("execTraderBalanceDate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceDetail() {
        try {
            dao.execProc("trader_balance_detail",
                    Global.machineId,
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()));
        } catch (Exception ex) {
            log.error("execTraderBalanceDetail : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void supplierTraderBalanceDetail() {
        try {
            dao.execProc("trader_balance_detail1",
                    Global.machineId,
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()));
        } catch (Exception ex) {
            log.error("supplierTraderBalanceDetail : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void supplierTraderBalanceSummary() {
        try {
            dao.execProc("trader_opening",
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    Global.machineId);
        } catch (Exception ex) {
            log.error("supplierTraderBalanceSummary : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceDetailRemark() {
        try {
            dao.execProc("trader_balance_detail_remark",
                    Global.machineId,
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()));
        } catch (Exception ex) {
            log.error("execTraderBalanceDetailRemark : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execTraderInOutSummary() {
        try {
            dao.execProc("trader_bal_in_out_summary",
                    Global.machineId,
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()));
        } catch (Exception ex) {
            log.error("execTraderInOutSummary : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execStockInOutBal() {
        Integer location = 0;

        if (cboLocation.getSelectedItem() instanceof Location) {
            location = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }

        try {
            dao.execProc("stock_in_out_bal",
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()),
                    location.toString(),
                    Global.machineId);
        } catch (Exception ex) {
            log.error("execStockInOutBal : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceWithUPV() {
        try {
            dao.execProc("get_trader_unpaid_vou", Global.machineId,
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtDueDate.getText()));
        } catch (Exception ex) {
            log.error("execTraderBalanceWithUPV : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void printSessionRpt() {
        String from = DateUtil.toDateStrMYSQL(txtFrom.getText());
        String to = DateUtil.toDateStrMYSQL(txtFrom.getText());
        String strSession = "0";
        String vouUserId = "All";
        String machineId = "-1";
        String tranType = "All";
        String deleted = "Normal";
        String strSource = "All";
        String traderId = "-";
        String sign = "-";
        String strAmount = "0";
        String paidCurr = "-";
        String locationId = "-1";

        /*if (cboLocation.getSelectedItem() instanceof Location) {
         Location location = (Location) cboLocation.getSelectedItem();
         locationId = location.getLocationId().toString();
         }else{
         locationId = "-1";
         }*/
        String curr = "All";
        /*if (cboCurrency.getSelectedItem() instanceof Currency) {
         Currency currency = (Currency) cboCurrency.getSelectedItem();
         curr = currency.getCurrencyCode();
         }else{
         curr = "All";
         }*/

        try {
            dao.execProc("session_report", from, to, strSession, vouUserId, machineId,
                    locationId, tranType, deleted, strSource, curr, traderId, sign, paidCurr,
                    strAmount, Global.machineId);
        } catch (Exception ex) {
            log.error("printSessionRpt : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private Map getParameter(String report) {
        Map<String, Object> params = new HashMap();

        params.put("tran_date", txtFrom.getText());
        params.put("user_id", Global.machineId);
        params.put("compName", Util1.getPropValue("report.company.name"));
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
        params.put("prm_location", getLocationId());
        params.put("data_date", "Between " + txtFrom.getText()
                + " and " + txtTo.getText());
        params.put("prm_currency", getCurrencyId());
        params.put("prm_payment", getPaymentType());
        params.put("prm_vou_type", getVouStatus());
        params.put("reg_no", Util1.getString(txtRegNo.getText(), "-"));
        params.put("prm_location_group", getLocationGroup());
        params.put("prm_cus_group", getCusGroup());
        
        String toLocationName = "All";
        int toLocationId = 0;
        if (cboToLocation.getSelectedItem() instanceof Location) {
            Location toLocation = (Location) cboToLocation.getSelectedItem();
            toLocationName = toLocation.getLocationName();
            toLocationId = toLocation.getLocationId();
        }
        params.put("prm_to_location_name", toLocationName);
        params.put("prm_tlocation", toLocationId);

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType it = (ItemType) cboItemType.getSelectedItem();
            params.put("item_type", it.getItemTypeName());
            params.put("p_item_type", it.getItemTypeCode());
        } else {
            params.put("item_type", "All");
            params.put("p_item_type", "-");
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            params.put("session", ((Session) cboSession.getSelectedItem()).getSessionId());
        } else {
            params.put("session", "-");
        }

        if (cboDoctor.getSelectedItem() instanceof Doctor) {
            String drId = ((Doctor) cboDoctor.getSelectedItem()).getDoctorId();
            params.put("p_doctor_id", drId);
            //params.put("tech_id", drId);
        } else {
            params.put("p_doctor_id", "-");
            //params.put("tech_id", "-");
        }

        switch (report) {
            case "StockBalance":
            case "StockBalanceKS":
            case "StockBalanceAllLoc":
            case "StockBalanceExp":
            case "StockBalanceExpKS":
                params.put("data_date", txtTo.getText());
                break;
            case "StockMovementExp":
            case "StockMovementExpKS":
            case "StockMovement":
            case "StockMovementKS":
            case "StockInOut":
            case "StockInOutKS":
            case "rptSaleSummary":
            case "rptSaleDoctorDetail":
            case "rptSaleDoctorSummary":
            case "rptSaleByDocument":
            case "rptSaleByDocumentT":
                break;
            case "rptSaleItemSummary":
            case "rptSaleItemSummaryByDateSein":
            case "rptSaleItemSummaryByCodeSein":
            case "rptSaleItemTypeSummary":
            case "rptSaleItemSummaryByDate":
            case "rptSaleItemSummaryMo":
            case "rptSaleItemSummaryByDoctor":
            case "rptSaleItemSummaryByDateWS":
                if (txtRegNo.getText().isEmpty()) {
                    params.put("reg_no", "All");
                } else {
                    params.put("reg_no", txtRegNo.getText());
                }
                params.put("cus_group", getCusGroup());
                break;
            case "rptSaleItemSummaryAdm":

                if (txtAdmNo.getText().isEmpty()) {
                    params.put("adm_no", "All");
                } else {
                    params.put("adm_no", txtAdmNo.getText());
                }
                params.put("cus_group", getCusGroup());
                break;
            case "rptSaleItemSummaryMoByDoctor":
                if (txtRegNo.getText().isEmpty()) {
                    params.put("reg_no", "All");
                } else {
                    params.put("reg_no", txtRegNo.getText());
                }
                params.put("doctor_id", "All");
                break;
            case "rptPurItemHistory":
            case "rptSaleItemByBrand":
                break;
            case "rptPurchaseSummary":
            case "rptPurSummaryByDate":
            case "rptPurchaseByDocument":
                params.put("data_date", "Between " + DateUtil.toDateStrMYSQL(txtFrom.getText())
                        + " and " + DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                params.put("prm_payment", getPaymentType());
                params.put("prm_vou_type", getVouStatus());
                break;
            case "rptPurItemSummary":
            case "rptPurItemSummarySein":
            case "rptPurItemSummaryBySupplier":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                break;
            case "rptReturnInSummary":
            case "rptReturnInSummaryForPatient":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                params.put("prm_payment", getPaymentType());
                break;
            case "rptReturnInItemSummary":
            case "rptReturnInItemSummarySein":
            case "rptReturnInItemSummaryByCustomer":
            case "rptReturnInItemSummaryForPatient":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                break;
            case "rptReturnOutSummary":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                params.put("prm_payment", getPaymentType());
                break;
            case "rptRetOutItemSummary":
            case "rptRetOutItemSummarySein":
            case "rptRetOutItemSummaryBySupplier":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                break;
            case "rptDmgItemSummary":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                break;
            case "rptTranItemSummary":
            case "rptTranByDoc":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_flocation", getLocationId());
                params.put("prm_tlocation", getToLocationId());
                params.put("prm_currency", getCurrencyId());
                break;
            case "CustomerBalance":
            case "CustomerBalance_Tsp":
                params.put("data_date", txtTo.getText());
                break;
            case "CustomerInOutBalance":
            case "rptCusBalanceRemark":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                break;
            case "CustomerInOutBalSummary":
            case "SupplierInOutBalSummary":
            case "CustomerInOutBalSummaryByDaily":
            case "BIDCustomer":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                break;
            case "GroundStockValue":
            case "GroundStockValueSystem":
            case "GroundStockValueBrand":
            case "GroundStockValueByBrand":
            case "GroundStockValueByBrandKS":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_flocation", getLocationId());
                break;
            case "rptPurchaseByDueDate":
            case "rptSaleByDueDate":
                /*params.put("data_date", "Between " + txtDueDate.getText()
                        + " and " + txtDueTo.getText());
                params.put("prm_due_from", DateUtil.toDateStrMYSQL(txtDueDate.getText()));
                params.put("prm_due_to", DateUtil.toDateStrMYSQL(txtDueTo.getText()));*/
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_due_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_due_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_location", getLocationId());
                params.put("prm_currency", getCurrencyId());
                params.put("prm_payment", getPaymentType());
                params.put("prm_vou_type", getVouStatus());
                break;
            case "rptExpense":
            case "rptExpenseByType":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                params.put("prm_currency", getCurrencyId());
                params.put("prm_exp_type", getExpenseType());
                break;
            case "rptPriceChangeHis":
                params.put("data_date", "Between " + txtFrom.getText()
                        + " and " + txtTo.getText());
                params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
                params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
                break;
        }

        return params;
    }

    private Integer getLocationId() {
        int locationId;

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location loc = (Location) cboLocation.getSelectedItem();
            locationId = loc.getLocationId();
        } else {
            locationId = 0;
        }
        System.out.println("loc : " + locationId);
        return locationId;
    }

    private int getToLocationId() {
        int locationId;

        if (cboToLocation.getSelectedItem() instanceof Location) {
            Location loc = (Location) cboToLocation.getSelectedItem();
            locationId = loc.getLocationId();
        } else {
            locationId = 0;
        }

        return locationId;
    }

    private Integer getLocationGroup() {
        int locationGroup = 0;
        if (cboLocGroup.getSelectedItem() instanceof LocationGroup) {
            LocationGroup lg = (LocationGroup) cboLocGroup.getSelectedItem();
            locationGroup = lg.getId();
        }
        return locationGroup;
    }

    private String getCurrencyId() {
        String currencyId;

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            currencyId = curr.getCurrencyCode();
        } else {
            currencyId = "All";
        }
        System.out.println("currencyId : " + currencyId);
        return currencyId;
    }

    private Integer getPaymentType() {
        int typeId;

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();
            typeId = pt.getPaymentTypeId();
        } else {
            typeId = 0;
        }
        System.out.println("payment : " + typeId);
        return typeId;
    }

    private Integer getVouStatus() {
        int status;

        if (cboVouStatus.getSelectedItem() instanceof VouStatus) {
            VouStatus vs = (VouStatus) cboVouStatus.getSelectedItem();
            status = vs.getStatusId();
        } else {
            status = 0;
        }
        System.out.println("vou status : " + status);
        return status;
    }

    private String getCusGroup() {
        String group;

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            CustomerGroup cg = (CustomerGroup) cboCusGroup.getSelectedItem();
            group = cg.getGroupId();
        } else {
            group = "All";
        }

        return group;
    }

    private String getItemType() {
        String itemType;

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType it = (ItemType) cboItemType.getSelectedItem();
            itemType = it.getItemTypeCode();
        } else {
            itemType = "All";
        }

        return itemType;
    }

    private Integer getCategory() {
        int cat;

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category c = (Category) cboCategory.getSelectedItem();
            cat = c.getCatId();
        } else {
            cat = 0;
        }

        return cat;
    }

    private int getExpenseType() {
        int ept;

        if (cboExpenseType.getSelectedItem() instanceof ExpenseType) {
            ExpenseType et = (ExpenseType) cboExpenseType.getSelectedItem();
            ept = et.getExpenseId();
        } else {
            ept = 0;
        }

        return ept;
    }

    private String getSession() {
        String value = "-";
        if (cboSession.getSelectedItem() instanceof Session) {
            value = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        }
        return value;
    }

    private String getCustomG() {
        String value = "-";
        if (cboCustomG.getSelectedItem() instanceof ItemGroup) {
            ItemGroup ig = (ItemGroup) cboCustomG.getSelectedItem();
            value = ig.getGroupId().toString();
        }
        return value;
    }

    private void deleteTmpData() {
        try {
            String strSql1 = "delete from tmp_item_code_filter_rpt where user_id ='"
                    + Global.machineId + "'";
            String strSql2 = "delete from tmp_trader_filter where user_id ='"
                    + Global.machineId + "'";
            String strSql3 = "delete from tmp_doctor_filter where user_id ='"
                    + Global.machineId + "'";

            dao.execSql(strSql1, strSql2, strSql3);
        } catch (Exception ex) {
            log.error("deleteTmpData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    /*private void fillBarcode() {
        String strSQL = "delete from tmp_barcode_filter where user_id = '"
                + Global.machineId + "'";
        HashMap<String, Integer> listCopy = new HashMap();
        List<ItemCodeFilterRpt> listFilter = codeTableModel.getListCodeFilter();

        dao.execSql(strSQL);
        dao.closeStatment();
        for (ItemCodeFilterRpt icf : listFilter) {
            if (icf.getItem() != null) {
                listCopy.put(icf.getItem().getMedId(), icf.getNoOfCopy());
            }
        }

        strSQL = "select distinct med_id from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        ResultSet resultSet = dao.execSQL(strSQL);

        try {
            String userId = Global.machineId;

            while (!resultSet.next()) {
                String medId = resultSet.getString("med_id");

                if (listCopy.containsKey(medId)) {
                    int count = listCopy.get(medId);
                    BarcodeFilter bf = new BarcodeFilter();

                    bf.setItemId(medId);
                    bf.setUserId(userId);
                    for (int i = 0; i < count; i++) {
                        dao.save(bf);
                        bf.setTranId(null);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("fillBarcode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }*/
    private void insertBarcodeFilter() {
        List<ItemCodeFilterRpt> listCodeFilter = codeTableModel.getListCodeFilter();
        List<BarcodeFilter> listBarcodeFilter = new ArrayList();

        for (ItemCodeFilterRpt icf : listCodeFilter) {
            if (icf.getItem() != null) {
                for (int i = 0; i < icf.getNoOfCopy(); i++) {
                    BarcodeFilter bf = new BarcodeFilter();

                    bf.setItemId(icf.getItem().getMedId());
                    if (icf.getUnit() != null) {
                        bf.setUnitShort(icf.getUnit().getItemUnitCode());
                    }
                    bf.setUserId(Global.machineId);
                    listBarcodeFilter.add(bf);
                }
            }
        }

        try {
            String strSQL = "delete from tmp_barcode_filter where user_id = '"
                    + Global.machineId + "'";

            dao.execSql(strSQL);
            dao.saveBatch(listBarcodeFilter);
        } catch (Exception ex) {
            log.error("insertBarcodeFilter : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void fixMinusBalance(String stockDate) {
        try {
            dao.deleteSQL("delete from tmp_stock_op_detail_his where user_id = '"
                    + Global.machineId + "'");
        } catch (Exception ex) {
            log.error("fixfMinusBalance : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        String strSQL = "select distinct med_id from tmp_stock_balance_exp where user_id = '"
                + Global.machineId + "'";

        try {
            dao.execProc("fix_minus", Global.machineId);
            dao.commit();
            dao.execProc("insert_cost", DateUtil.toDateStrMYSQL(stockDate),
                    Global.machineId);
            dao.commit();
            ResultSet resultSet = dao.execSQL(strSQL);
            if (resultSet != null) {
                while (resultSet.next()) {
                    String medId = resultSet.getString("med_id");
                    strSQL = "select v from TmpMinusFixed v where v.key.userId = '"
                            + Global.machineId + "' and v.key.itemId = '"
                            + medId + "' and balance <> 0 order by v.key.expDate desc";
                    List<TmpMinusFixed> listTMF = dao.findAllHSQL(strSQL);
                    strSQL = "select v from TmpCostDetails v where v.userId = '"
                            + Global.machineId + "' and v.itemId = '"
                            + medId + "' order by v.tranDate desc, v.tranId";
                    List<TmpCostDetails> listTCD = dao.findAllHSQL(strSQL);

                    //Calculate cost with first in first out
                    if (listTCD != null) {
                        if (listTCD.size() > 0) {
                            for (TmpMinusFixed tmf : listTMF) {
                                int leftQty = tmf.getBalance();
                                do {
                                    TmpCostDetails tcd = listTCD.get(0);
                                    int costQty = tcd.getTtlQty();
                                    double cost = tcd.getSmallestCost();
                                    int tmpQty = 0;

                                    if (leftQty >= costQty) {
                                        tmpQty = costQty;
                                        leftQty = leftQty - costQty;
                                        listTCD.remove(tcd);
                                    } else if (leftQty < costQty) {
                                        tmpQty = leftQty;
                                        leftQty = 0;
                                        tcd.setTtlQty(costQty - leftQty);
                                    }

                                    insertTmpStockDetails(medId, tmf.getKey().getExpDate(),
                                            tmpQty, cost, tmf.getKey().getLocationId());
                                } while (leftQty != 0);
                            }
                        }
                    }
                }
                resultSet.close();
            }
        } catch (Exception ex) {
            log.error("fixMinusBalance : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {

        }
    }

    private void insertTmpStockDetails(String medId, Date expDate,
            int ttlQty, double smallestCost, int locationId) {
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
                tsodh.setLocationId(locationId);

                try {
                    dao.save(tsodh);
                } catch (Exception ex) {
                    log.error("insertTmpStockDetails : 1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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
                    tsodh.setLocationId(locationId);

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
                    log.error("insertTmpStockDetails : 2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }
        } catch (Exception ex) {
            log.error("insertTmpStockDetails : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getPatient() {
        if (txtRegNo.getText() != null && !txtRegNo.getText().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class, txtRegNo.getText());
                dao.close();

                if (pt == null) {
                    txtRegNo.setText(null);
                    txtPtName.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    txtRegNo.setText(pt.getRegNo());
                    txtPtName.setText(pt.getPatientName());
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtRegNo.setText(null);
            txtPtName.setText(null);
        }
    }

    private void getAdmPatient() {
        if (txtAdmNo.getText() != null && !txtAdmNo.getText().isEmpty()) {
            try {
                dao.open();
                List<Ams> listAms = dao.findAllHSQL(
                        "select o from Ams o where o.key.amsNo = '" + txtAdmNo.getText().trim() + "'"
                );
                Ams ams = null;
                if (listAms != null) {
                    if (!listAms.isEmpty()) {
                        ams = listAms.get(0);
                    }
                }
                dao.close();

                if (ams == null) {
                    txtAdmNo.setText(null);
                    txtAdmName.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid admitted patient code.",
                            "Admitted Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    txtAdmNo.setText(ams.getKey().getAmsNo());
                    txtAdmName.setText(ams.getPatientName());
                }
            } catch (Exception ex) {
                log.error("getAdmPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtAdmNo.setText(null);
            txtAdmName.setText(null);
        }
    }

    public void insertMonthFilter(String from, String to, String userId,
            Map<String, Object> params, int location) {
        try {
            Date dFrom = DateUtil.toDate(from);
            Date dTo = DateUtil.toDate(to);
            int fromYear = DateUtil.getDatePart(dFrom, "yyyy");
            int fromMonth = DateUtil.getDatePart(dFrom, "MM");
            int toYear = DateUtil.getDatePart(dTo, "yyyy");
            int toMonth = DateUtil.getDatePart(dTo, "MM");
            String strField = "";
            String strSql = "";

            dao.execSql("delete from tmp_month_filter where user_id = '" + userId + "'");
            dao.execSql("delete from tmp_phar_yearly_summary where user_id = '" + userId + "'");

            for (int i = 1; i <= 12; i++) {
                if (fromMonth > 12 && fromYear < toYear) {
                    fromMonth = 1;
                    fromYear++;
                } else if (fromMonth > toMonth && fromYear == toYear) {
                    fromYear = toYear + 1;
                }

                String ym = fromMonth + "-" + fromYear;
                if (fromYear <= toYear) {
                    params.put("m" + i, ym);
                    if (strSql.isEmpty()) {
                        strSql = "sum(case y_m when '" + ym + "' then ttl_s_qty else 0 end) as " + "s_m" + i
                                + ",sum(case y_m when '" + ym + "' then ttl_p_qty else 0 end) as " + "p_m" + i;
                    } else {
                        strSql = strSql + ", sum(case y_m when '" + ym + "' then ttl_s_qty else 0 end) as " + "s_m" + i
                                + ",sum(case y_m when '" + ym + "' then ttl_p_qty else 0 end) as " + "p_m" + i;
                    }
                } else {
                    params.put("m" + i, " ");
                    if (strSql.isEmpty()) {
                        strSql = "0 as " + "s_m" + i
                                + ", 0 as p_m" + i;
                    } else {
                        strSql = strSql + ", 0 as " + "s_m" + i
                                + ", 0 as p_m" + i;
                    }
                }

                if (strField.isEmpty()) {
                    strField = "s_m" + i + ",p_m" + i;
                } else {
                    strField = strField + ",s_m" + i + ",p_m" + i;
                }

                if (fromMonth <= 12 && fromYear <= toYear) {
                    TmpMonthFilter tmf = new TmpMonthFilter(userId,
                            ym, fromMonth, fromYear);
                    try {
                        dao.save(tmf);
                    } catch (Exception ex) {
                        log.error("insertMonthFilter : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                    }
                }

                fromMonth++;
            }

            strSql = "insert into tmp_phar_yearly_summary(user_id, item_code, " + strField + ") "
                    + "select '" + userId + "', med_id," + strSql + " from ("
                    + "select a.med_id, a.y_m, ifnull(b.ttl_s_qty,0) ttl_s_qty, ifnull(c.ttl_p_qty,0) ttl_p_qty\n"
                    + "from (\n"
                    + "select distinct tsf.med_id, tsf.user_id, tmf.y_m, tmf.f_m, tmf.f_y\n"
                    + "from (select distinct med_id, user_id from tmp_stock_filter where user_id = '" + userId + "') tsf, tmp_month_filter tmf\n"
                    + "where tsf.user_id = tmf.user_id\n"
                    + "and tsf.user_id = '" + userId + "' and tmf.user_id = '" + userId + "') a \n"
                    + "left join\n"
                    + "(select concat(month(vs.sale_date),'-',year(vs.sale_date)) y_m, vs.med_id,\n"
                    + "sum(ifnull(vs.sale_smallest_qty,0)) as ttl_s_qty, tsf.user_id\n"
                    + "from v_sale vs join (select distinct med_id, user_id from tmp_stock_filter where user_id = '" + userId + "') tsf on vs.med_id = tsf.med_id where deleted = false and "
                    + "tsf.user_id = '" + userId + "' and date(vs.sale_date) between '"
                    + DateUtil.toDateStrMYSQL(from) + "' and '" + DateUtil.toDateStrMYSQL(to) + "' "
                    + " and (vs.location_id = " + location + " or " + location + " = 0) \n"
                    + "group by concat(month(sale_date),'-',year(sale_date)), med_id,tsf.user_id) b\n"
                    + "on a.med_id = b.med_id and a.y_m = b.y_m and a.user_id = b.user_id\n"
                    + "left join\n"
                    + "(select concat(month(vp.pur_date),'-',year(vp.pur_date)) y_m, vp.med_id,\n"
                    + "sum(ifnull(vp.pur_smallest_qty,0)) as ttl_p_qty, tsf.user_id\n"
                    + "from v_purchase vp join (select distinct med_id, user_id from tmp_stock_filter where user_id = '" + userId + "') tsf on vp.med_id = tsf.med_id \n"
                    + "where vp.deleted = false and tsf.user_id = '" + userId + "' and "
                    + " date(vp.pur_date) between '" + DateUtil.toDateStrMYSQL(from) + "' and '" + DateUtil.toDateStrMYSQL(to) + "' "
                    + " and (vp.location = " + location + " or " + location + " = 0) \n"
                    + "group by concat(month(vp.pur_date),'-',year(vp.pur_date)), vp.med_id, tsf.user_id) c\n"
                    + "on a.med_id = c.med_id and a.y_m = c.y_m and a.user_id = c.user_id\n"
                    + "order by a.f_y, a.f_m"
                    + ") a group by med_id";

            dao.execSql(strSql);
            dao.execSql("update tmp_phar_yearly_summary \n"
                    + "set s_total = (ifnull(s_m1,0)+ifnull(s_m2,0)+ifnull(s_m3,0)+ifnull(s_m4,0)+ifnull(s_m5,0)+ifnull(s_m6,0)\n"
                    + "+ifnull(s_m7,0)+ifnull(s_m8,0)+ifnull(s_m9,0)+ifnull(s_m10,0)+ifnull(s_m11,0)+ifnull(s_m12,0)),\n"
                    + "p_total = (ifnull(p_m1,0)+ifnull(p_m2,0)+ifnull(p_m3,0)+ifnull(p_m4,0)+ifnull(p_m5,0)+ifnull(p_m6,0)\n"
                    + "+ifnull(p_m7,0)+ifnull(p_m8,0)+ifnull(p_m9,0)+ifnull(p_m10,0)+ifnull(p_m11,0)+ifnull(p_m12,0))\n"
                    + "where user_id = '" + userId + "'");
        } catch (Exception ex) {
            log.error("insertMonthFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private class CodeTableUnitCellEditor extends javax.swing.AbstractCellEditor implements TableCellEditor {

        JComponent component = null;
        int colIndex = -1;
        private Object oldValue;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            oldValue = value;
            colIndex = vColIndex;

            try {
                ItemCodeFilterRpt icf = codeTableModel.getCodeFilter(rowIndex);
                if (icf.getItem() != null) {
                    String medId = icf.getItem().getMedId();
                    JComboBox jb = new JComboBox();
                    jb.setFont(Global.textFont);
                    BindingUtil.BindCombo(jb, medUp.getUnitList(medId));
                    component = jb;
                }
            } catch (Exception ex) {
                log.error("getTableCellEditorComponent : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }

            return component;
        }

        @Override
        public Object getCellEditorValue() {
            if (component == null) {
                return null;
            }

            Object obj;
            obj = ((JComboBox) component).getSelectedItem();
            return obj;
        }
    }

    public void generateExcel() {
        int index = tblReportList.convertRowIndexToModel(tblReportList.getSelectedRow());
        if (index >= 0) {
            try {
                Menu report = tableModel.getSelectedReport(index);
                GenExcel genExcel = null;
                String rptName = report.getMenuClass();
                switch (rptName) {
                    case "rptCodeList":
                    case "rptCodeListSein":
                        genExcel = new ItemCodeListExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        break;
                    case "rptCodeListSmallestWithPurPrice":
                        genExcel = new ItemCodeListWithPurPriceExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        break;
                    case "rptSaleItemSummaryMo":
                        genExcel = new SaleItemSummaryPatientExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setCurrencyId(getCurrencyId());
                        if (!txtRegNo.getText().isEmpty()) {
                            genExcel.setRegNo(txtRegNo.getText().trim());
                        }
                        genExcel.setSession(getSession());
                        break;
                    case "rptSaleItemSummaryAdm":
                        genExcel = new SaleItemSummaryPatientAdmExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setCurrencyId(getCurrencyId());
                        if (!txtRegNo.getText().isEmpty()) {
                            genExcel.setRegNo(txtRegNo.getText().trim());
                        }
                        genExcel.setSession(getSession());
                        if (!txtAdmNo.getText().trim().isEmpty()) {
                            genExcel.setAdmNo(txtAdmNo.getText().trim());
                        }
                        break;
                    case "rptSaleItemSummaryByCodeSein":
                        genExcel = new SaleItemSummaryByCodeExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setSession(getSession());
                        break;
                    case "rptSaleItemSummaryByDoctor":
                        genExcel = new SaleItemSummaryDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setCurrencyId(getCurrencyId());
                        if (!txtRegNo.getText().isEmpty()) {
                            genExcel.setRegNo(txtRegNo.getText().trim());
                        }
                        genExcel.setSession(getSession());
                        break;
                    case "StockBalance":
                    case "StockBalanceKS":
                        genExcel = new StockBalanceExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setItemActive(cboItemStatus.getSelectedItem().toString());
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        execStockBalanceExp(txtTo.getText());
                        clearValue(rptName);
                        break;
                    case "rptPurItemSummary":
                        genExcel = new PurchaseItemSummaryExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        break;
                    case "rptTranItemSummary":
                        genExcel = new TransferItemSummaryExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setToLocationId(String.valueOf(getToLocationId()));
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        break;
                    case "rptAdjustItemSummary":
                        genExcel = new StockAdjustItemSummaryExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        break;
                    case "StockMovement":
                    case "StockMovementKS":
                        genExcel = new StockMovementExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.insertStockFilterCode();
                        execStockMovementExp();
                        break;
                    case "rptPurchaseItemTypeSummary":
                        genExcel = new PurchaseItemTypeSummaryExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.setPaymentType(String.valueOf(getPaymentType()));
                        genExcel.setCurrencyId(getCurrencyId());
                        //genExcel.setVouType(String.valueOf(getVouStatus()));
                        genExcel.setSession(getSession());
                        genExcel.insertStockFilterCode();
                        break;
                    case "rptRetOutItemSummary":
                        genExcel = new ReturnOutItemSummaryExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.insertStockFilterCode();
                        break;
                    case "StockInOut":
                    case "StockInOutKS":
                        genExcel = new StockInOutExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        execStockBalanceExp(txtTo.getText());
                        break;
                    case "rptReturnInItemSummary":
                        genExcel = new ReturnInItemSummaryExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.insertStockFilterCode();
                        break;
                    case "SaleIncomeByDoctor":
                        genExcel = new SaleIncomeByDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setLocationGroup(getLocationGroup().toString());

                        //genExcel.insertStockFilterCode();
                        break;
                    case "rptPurItemSummaryBySupplier":
                        genExcel = new PurchaseItemSummaryBySupplierExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        break;
                    case "rptPurchaseByDocument":
                        genExcel = new PurchaseByDocumentExcel(dao, rptName + ".xls");
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.setUserId(Global.machineId);
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        genExcel.setVouType(getVouStatus().toString());
                        genExcel.insertStockFilterCode();
                        break;
                    case "rptBarCode":
                        genExcel = new ItemCodeListWithInfoExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        break;
                    case "ControlDrugForm3":
                        genExcel = new ControlDrugForm3Excel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.insertStockFilterCode();
                        dao.execProc("control_drug_in_out",
                                DateUtil.toDateStrMYSQL(txtFrom.getText()),
                                DateUtil.toDateStrMYSQL(txtTo.getText()),
                                getLocationId().toString(),
                                Global.machineId);
                        break;
                    case "ControlDrugForm3WS":
                        genExcel = new ControlDrugForm3WSExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setCustomG(getCustomG());
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.insertStockFilterCode();
                        dao.execProc("control_drug_in_out1",
                                DateUtil.toDateStrMYSQL(txtFrom.getText()),
                                DateUtil.toDateStrMYSQL(txtTo.getText()),
                                getLocationId().toString(),
                                Global.machineId);
                        break;
                    case "rptSaleItemSummaryByDateSein":
                        genExcel = new SaleItemSummaryByDateExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(String.valueOf(getLocationId()));
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setSession(getSession());
                        break;
                    case "rptPurchaseSummary":
                        genExcel = new PurchaseSummaryExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.setPaymentType(String.valueOf(getPaymentType()));
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setVouType(String.valueOf(getVouStatus()));
                        genExcel.setSession(getSession());
                        genExcel.setCustomG(getCustomG());
                        genExcel.insertStockFilterCode();
                        break;
                    case "SupplierBalance":
                        insertTraderFilterCode("S");
                        execTraderBalanceDate();
                        genExcel = new TraderBalanceExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        break;
                    case "rptCodeListPriceS":
                        genExcel = new ItemCodeListWitSalePriceExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        break;
                    case "CustomerListName":
                        insertTraderFilterCode("C");
                        genExcel = new CustomerListExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        break;
                    case "SupplierListName":
                        insertTraderFilterCode("S");
                        genExcel = new SupplierListExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        break;
                    case "AuditLog":
                        genExcel = new AuditLogExcel(dao, rptName + ".xls");
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        break;
                    case "rptSaleSummary":
                        genExcel = new SaleSummaryExcel(dao, rptName + ".xls");
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setUserId(Global.machineId);
                        genExcel.setVouType(getVouStatus().toString());
                        genExcel.setLocationId(getLocationId().toString());
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setSession(getSession());
                        genExcel.setPaymentType(String.valueOf(getPaymentType()));
                        genExcel.setSession(getSession());
                        genExcel.insertStockFilterCode();
                        break;
                    case "ReturnWithSaleVouInfo":
                        genExcel = new ReturnInBySaleVoucherExcel(dao, rptName + ".xls");
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        break;
                    case "rptCodeListSystem":
                    case "rptCodeListSystemWithSP":
                        genExcel = new ItemCodeListWitSystemSalePriceExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        break;
                    case "StockBalanceBrandWithSalePrice":
                        genExcel = new StockBalanceBrandWithSalePriceExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setItemType(getItemType());
                        genExcel.setBrandId(getBrand().toString());
                        genExcel.setCategoryId(getCategory().toString());
                        genExcel.setCusGroup(getCusGroup());
                        genExcel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setItemActive(cboItemStatus.getSelectedItem().toString());
                        if (codeTableModel.getListCodeFilter().size() > 1) {
                            genExcel.setItemCodeFilter("f");
                        }
                        genExcel.insertStockFilterCode();
                        execStockBalanceExp(txtTo.getText());
                        clearValue(rptName);
                        break;
                }

                if (genExcel != null) {
                    genExcel.genExcel();
                }
            } catch (Exception ex) {
                log.error("generateExcel : " + ex.getMessage());
            }
        }
    }

    private Integer getBrand() {
        int brandId = 0;
        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            brandId = itemBrand.getBrandId();
        }

        return brandId;
    }

    private void execTraderBalance() {
        try {
            dao.execProc("trader_balance_detail1",
                    Global.machineId,
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()));
        } catch (Exception ex) {
            log.error("execTraderBalance : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    /*private void fixedMinus(String userId) {
        String strSql = "select location_id, med_id, exp_date, bal_qty\n"
                + "from tmp_stock_balance_exp\n"
                + "where user_id = '" + userId + "' and bal_qty <> 0\n"
                + "order by location_id, med_id, bal_qty, exp_date";
        try {
            dao.execSql("delete from tmp_minus_fixed where user_id = '" + userId + "'");
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                HashMap<String, List<StockExp>> minusHM = new HashMap();
                String key = "-";
                String prvMedId = "-";
                while (rs.next()) {
                    float qty = NumberUtil.FloatZero(rs.getInt("bal_qty"));
                    String medId = rs.getString("med_id");
                    String locationId = rs.getString("location_id");
                    if (key.equals("-")) {
                        key = medId + "-" + locationId;
                        prvMedId = medId;
                    }

                    if (!key.equals(medId + "-" + locationId)) {
                        key = medId + "-" + locationId;
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

                    List<StockExp> minusList = minusHM.get(key);
                    if (qty < 0) {
                        if (minusList == null) {
                            minusList = new ArrayList();
                            minusHM.put(key, minusList);
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
                            //log.info("if (minusList == null) { : insert : " + medId);
                        } else if (minusList.isEmpty()) {

                        } else {
                            StockExp tmpStk = minusList.get(0);
                            float tmpBalance = tmpStk.getBalance();
                            if ((tmpBalance * -1) > qty) {
                                float tmpQty = tmpBalance + qty;
                                tmpStk.setBalance(tmpQty);
                            } else if ((tmpBalance * -1) == qty) {
                                tmpStk.setBalance(0f);
                                minusList.remove(0);
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

                                    if (qty > 0) {
                                        String strInsert = "insert into tmp_minus_fixed(location_id, item_id, exp_date, balance, user_id)\n"
                                                + " values(" + rs.getInt("location_id") + ",'" + medId + "','"
                                                + DateUtil.toDateStr(rs.getDate("exp_date"), "yyyy-MM-dd") + "',"
                                                + qty + ", '" + userId + "')";
                                        dao.execSql(strInsert);
                                        //log.info("if (qty > 0) { insert : " + medId);
                                    }
                                } else if (qty > 0) {
                                    String strInsert = "insert into tmp_minus_fixed(location_id, item_id, exp_date, balance, user_id)\n"
                                            + " values(" + rs.getInt("location_id") + ",'" + medId + "','"
                                            + DateUtil.toDateStr(rs.getDate("exp_date"), "yyyy-MM-dd") + "',"
                                            + qty + ", '" + userId + "')";
                                    dao.execSql(strInsert);
                                    //log.info("if (qty > 0) { insert : " + medId);
                                }
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
    }*/
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
                List<Stock> listMinusStock;
                List<Stock> listPlusStock;
                Medicine med = null;
                HashMap<String, MinusPlusList> hmMP = new HashMap();
                List<String> locList = new ArrayList();

                while (rs.next()) {
                    float qty = NumberUtil.FloatZero(rs.getInt("bal_qty"));
                    String medId = rs.getString("med_id");
                    int locationId = rs.getInt("location_id");

                    if (prvMedId.equals("-")) {
                        prvMedId = medId;
                        med = (Medicine) dao.find(Medicine.class, medId);
                    }

                    String sKey = locationId + "-" + medId;

                    /*if (prvMedId.equals("16160002")) {
                        log.error("Error");
                    }*/
                    if (!prvMedId.equals(medId)) {
                        List<Stock> listS = new ArrayList();
                        for (String loc : locList) {
                            MinusPlusList mpl = hmMP.get(loc);
                            if (mpl != null) {
                                listS.addAll(PharmacyUtil.getStockList(mpl.getListMinusStock(), mpl.getListPlusStock()));
                            }
                        }

                        for (Stock s : listS) {
                            TmpMinusFixedKey key = new TmpMinusFixedKey();
                            key.setExpDate(s.getExpDate());
                            key.setItemId(s.getMed().getMedId());
                            key.setLocationId(s.getLocationId());
                            key.setUserId(userId);
                            TmpMinusFixed tmf = new TmpMinusFixed();
                            tmf.setKey(key);
                            tmf.setBalance(Math.round(s.getBalance()));
                            log.info("insert : " + tmf.toString());
                            dao.save(tmf);
                        }

                        //log.info("Med Id : " + prvMedId);
                        //listMinusStock = new ArrayList();
                        //listPlusStock = new ArrayList();
                        med = (Medicine) dao.find(Medicine.class, medId);
                        hmMP = new HashMap();
                        locList = new ArrayList();
                        //locList.add(sKey);
                    }

                    if (!hmMP.containsKey(sKey)) {
                        listMinusStock = new ArrayList();
                        listPlusStock = new ArrayList();
                        MinusPlusList mpl = new MinusPlusList(listMinusStock, listPlusStock);
                        hmMP.put(sKey, mpl);
                        locList.add(sKey);
                    } else {
                        MinusPlusList mpl = hmMP.get(sKey);
                        listMinusStock = mpl.getListMinusStock();
                        listPlusStock = mpl.getListPlusStock();
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

                List<Stock> listS = new ArrayList();
                for (String loc : locList) {
                    MinusPlusList mpl = hmMP.get(loc);
                    if (mpl != null) {
                        listS.addAll(PharmacyUtil.getStockList(mpl.getListMinusStock(), mpl.getListPlusStock()));
                    }
                }

                for (Stock s : listS) {
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
        } catch (Exception ex) {
            log.error("fixedMinus : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private String[] getCopyData() {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipTf = sysClip.getContents(null);
        String[] tmpCode = null;

        if (clipTf != null) {
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                    tmpCode = ret.split("\n");
                    log.info(tmpCode.length);
                } catch (Exception ex) {
                    log.error("Copy Paste Error : " + ex.toString());
                }
            }
        }

        return tmpCode;
    }

    private void calculateBorrowBalance() {
        String strToDate = DateUtil.toDateStrMYSQL(txtTo.getText());
        String userId = Global.machineId;
        String strSql = "select a.cus_id, a.med_id, sum(a.ttl_receive) as ttl_receive, sum(a.ttl_issue) as ttl_issue, \n"
                + "sum(a.ttl_receive-a.ttl_issue) as balance\n"
                + "from (\n"
                + "select issue_opt, issue_date, ifnull(currency_id,'MMK') as currency_id, cus_id, med_id, \n"
                + "sum(ifnull(smallest_qty,0)) as ttl_issue, 0 as ttl_receive\n"
                + "from v_stock_issue\n"
                + "where issue_opt = 'Borrow' and deleted = false\n"
                + " and date(issue_date) <= '" + strToDate + "' \n"
                + " and med_id in (select distinct med_id from tmp_stock_filter where user_id = '" + userId + "') \n"
                + " and cus_id in (select distinct trader_id from tmp_trader_bal_filter where user_id = '" + userId + "') \n"
                + "group by issue_opt, issue_date, ifnull(currency_id,'MMK'), cus_id, med_id\n"
                + "union all\n"
                + "select rec_option, receive_date, ifnull(currency_id,'MMK') as currency_id, cus_id, rec_med_id, \n"
                + "0 as ttl_issue, sum(ifnull(smallest_qty,0)) as ttl_receive\n"
                + "from v_stock_receive\n"
                + "where rec_option = 'Borrow' and deleted = false\n"
                + " and date(receive_date) <= '" + strToDate + "' \n"
                + " and rec_med_id in (select distinct med_id from tmp_stock_filter where user_id = '" + userId + "') \n"
                + " and cus_id in (select distinct trader_id from tmp_trader_bal_filter where user_id = '" + userId + "') \n"
                + "group by rec_option, receive_date, ifnull(currency_id,'MMK'), cus_id, rec_med_id) a\n"
                + "group by a.cus_id, a.med_id";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                dao.execSql("delete from tmp_stock_bal_outs where user_id = '" + userId + "'");
                while (rs.next()) {
                    TmpStockBalOutsKey key = new TmpStockBalOutsKey();
                    String medId = rs.getString("med_id");
                    Medicine med = (Medicine) dao.find(Medicine.class, medId);
                    if (!med.getRelationGroupId().isEmpty()) {
                        med.setRelationGroupId(med.getRelationGroupId());
                    }
                    key.setItemId(medId);
                    key.setTraderId(rs.getString("cus_id"));
                    key.setTranOption("Borrow");
                    key.setUserId(userId);

                    TmpStockBalOuts sb = new TmpStockBalOuts();
                    sb.setKey(key);
                    sb.setQtySmallese(rs.getFloat("balance"));
                    sb.setQtyStr(MedicineUtil.getQtyInStr(med, rs.getFloat("balance")));
                    dao.save(sb);
                }
            }
        } catch (Exception ex) {
            log.error("calculateBorrowBalance : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void deleteTraderBalanceZero() {
        if (!chkZero.isSelected()) {
            try {
                String strSql = "delete from tmp_trader_bal_date where user_id = '"
                        + Global.machineId + "' and round(amount,0) <=1 and round(amount,0) >=-1";
                dao.execSql(strSql);
            } catch (Exception ex) {
                log.error("traderBalanceZero : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    /*private void listMethod() {
        //Method methods[];

        try {
            Class obj = this.getClass();
            Method mt1 = obj.getMethod("generateExcel", null);
            mt1.invoke(obj.newInstance(), null);
            Method[] methods = this.getClass().getMethods();
            for(Method mt : methods){
                String name = mt.getName();
                log.info("Method Name : " + name);
            }
        } catch (Exception ex) {
            log.error("listMethod : " + ex.getMessage());
        }
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        butPrint = new javax.swing.JButton();
        chkMinus = new javax.swing.JCheckBox();
        chkZero = new javax.swing.JCheckBox();
        chkPlus = new javax.swing.JCheckBox();
        butExcel = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        cboItemStatus = new javax.swing.JComboBox<>();
        butRemotePrint = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        cboReportType = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReportList = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        lblDueDate = new javax.swing.JLabel();
        txtDueDate = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboVouStatus = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        cboCusGroup = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        cboItemType = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        cboCategory = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        cboToLocation = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        cboTownship = new javax.swing.JComboBox();
        txtDueTo = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        cboExpenseType = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        cboCustomG = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        cboLocGroup = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboSystem = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItemCodeFilter = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTraderFilter = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDoctorFilter = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtAdmNo = new javax.swing.JTextField();
        txtAdmName = new javax.swing.JTextField();
        butItemPaste = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cboSession = new javax.swing.JComboBox();
        cboDoctor = new javax.swing.JComboBox<>();

        butPrint.setFont(Global.lableFont);
        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        chkMinus.setSelected(true);
        chkMinus.setText("-");
        chkMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMinusActionPerformed(evt);
            }
        });

        chkZero.setSelected(true);
        chkZero.setText("0");

        chkPlus.setSelected(true);
        chkPlus.setText("+");

        butExcel.setText("Excel");
        butExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExcelActionPerformed(evt);
            }
        });

        jLabel21.setText("Item : ");

        cboItemStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Active", "In Active" }));

        butRemotePrint.setFont(Global.lableFont);
        butRemotePrint.setText("Remote Print");
        butRemotePrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRemotePrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butRemotePrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butExcel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butPrint))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkMinus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkZero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPlus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboItemStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butExcel, butPrint});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMinus)
                    .addComponent(chkZero)
                    .addComponent(chkPlus)
                    .addComponent(jLabel21)
                    .addComponent(cboItemStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butExcel)
                        .addComponent(butRemotePrint))
                    .addComponent(butPrint))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {butExcel, butPrint});

        cboReportType.setFont(Global.textFont);
        cboReportType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboReportTypeActionPerformed(evt);
            }
        });

        tblReportList.setFont(Global.textFont);
        tblReportList.setModel(tableModel);
        tblReportList.setRowHeight(23);
        jScrollPane1.setViewportView(tblReportList);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cboReportType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(cboReportType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1))
        );

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From");

        txtFrom.setEditable(false);
        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });
        txtFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFromActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        txtTo.setEditable(false);
        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });
        txtTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtToActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location ");

        cboLocation.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Currency ");

        cboCurrency.setFont(Global.textFont);

        lblDueDate.setFont(Global.lableFont);
        lblDueDate.setText("Due Date ");

        txtDueDate.setFont(Global.textFont);
        txtDueDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDueDateMouseClicked(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Payment ");

        cboPayment.setFont(Global.textFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Vou Status");

        cboVouStatus.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Cus Group");

        cboCusGroup.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Item Type");

        cboItemType.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Category ");

        cboCategory.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Brand");

        cboBrand.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("T-Location");

        cboToLocation.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Township");

        cboTownship.setFont(Global.textFont);

        txtDueTo.setFont(Global.textFont);
        txtDueTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDueToMouseClicked(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Exp-Type");

        cboExpenseType.setFont(Global.textFont);

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Custom G");

        cboCustomG.setFont(Global.textFont);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Loc-Group");

        cboLocGroup.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("System");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboToLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboExpenseType, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDueTo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cboCustomG, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLocGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboSystem, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFrom, txtTo});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel13, jLabel14, jLabel17, jLabel20, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9, lblDueDate});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboBrand, cboCategory, cboCurrency, cboCusGroup, cboCustomG, cboExpenseType, cboItemType, cboPayment, cboToLocation, cboTownship, cboVouStatus});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboLocGroup, cboLocation});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDueDate)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDueTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(cboLocGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboToLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(cboExpenseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(cboCustomG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblItemCodeFilter.setFont(Global.textFont);
        tblItemCodeFilter.setModel(codeTableModel);
        tblItemCodeFilter.setRowHeight(23);
        tblItemCodeFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblItemCodeFilterFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(tblItemCodeFilter);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Trader Filter"));

        tblTraderFilter.setFont(Global.textFont);
        tblTraderFilter.setModel(traderTableModel);
        tblTraderFilter.setRowHeight(23);
        tblTraderFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblTraderFilterFocusLost(evt);
            }
        });
        jScrollPane3.setViewportView(tblTraderFilter);

        lblTotal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel16.setText("Total :");

        tblDoctorFilter.setFont(Global.textFont);
        tblDoctorFilter.setModel(doctorFilterTableModel);
        tblDoctorFilter.setRowHeight(23);
        tblDoctorFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblDoctorFilterFocusLost(evt);
            }
        });
        jScrollPane4.setViewportView(tblDoctorFilter);

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Patient");

        txtRegNo.setFont(Global.textFont);
        txtRegNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtRegNoMouseClicked(evt);
            }
        });
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        txtPtName.setFont(Global.textFont);
        txtPtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPtNameMouseClicked(evt);
            }
        });

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Adm No.");

        txtAdmNo.setFont(Global.textFont);
        txtAdmNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtAdmNoMouseClicked(evt);
            }
        });
        txtAdmNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAdmNoActionPerformed(evt);
            }
        });

        txtAdmName.setFont(Global.textFont);
        txtAdmName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtAdmNameMouseClicked(evt);
            }
        });

        butItemPaste.setText("Paste");
        butItemPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butItemPasteActionPerformed(evt);
            }
        });

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Session");

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Doctor");

        cboSession.setFont(Global.textFont);

        cboDoctor.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmName, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addComponent(butItemPaste)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addGap(11, 11, 11)
                            .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtPtName))))
                .addGap(0, 10, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel18, jLabel22});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboDoctor, cboSession});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(butItemPaste))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAdmName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboReportTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboReportTypeActionPerformed
        if (cboReportType.getSelectedItem() != null) {
            getReportList();
        }
    }//GEN-LAST:event_cboReportTypeActionPerformed

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

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        reportGeneration();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butPrintActionPerformed

    private void chkMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMinusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkMinusActionPerformed

    private void txtToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtToActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtToActionPerformed

    private void txtFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFromActionPerformed

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        getPatient();
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void txtRegNoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtRegNoMouseClicked
        if (evt.getClickCount() == 2) {
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtRegNoMouseClicked

    private void txtPtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPtNameMouseClicked
        if (evt.getClickCount() == 2) {
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtPtNameMouseClicked

    private void txtAdmNoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdmNoMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            AdmissionSearch dialog = new AdmissionSearch(dao, this);
        }
    }//GEN-LAST:event_txtAdmNoMouseClicked

    private void txtAdmNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdmNoActionPerformed
        getAdmPatient();
    }//GEN-LAST:event_txtAdmNoActionPerformed

    private void txtAdmNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdmNameMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            AdmissionSearch dialog = new AdmissionSearch(dao, this);
        }
    }//GEN-LAST:event_txtAdmNameMouseClicked

    private void tblItemCodeFilterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblItemCodeFilterFocusLost
        /*try{
         if(tblItemCodeFilter.getCellEditor() != null){
         tblItemCodeFilter.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblItemCodeFilterFocusLost

    private void tblTraderFilterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblTraderFilterFocusLost
        /*try{
         if(tblTraderFilter.getCellEditor() != null){
         tblTraderFilter.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblTraderFilterFocusLost

    private void tblDoctorFilterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblDoctorFilterFocusLost
        /*try{
         if(tblDoctorFilter.getCellEditor() != null){
         tblDoctorFilter.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblDoctorFilterFocusLost

    private void butExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExcelActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        generateExcel();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butExcelActionPerformed

    private void txtDueToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDueToMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDueTo.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtDueToMouseClicked

    private void txtDueDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDueDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDueDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtDueDateMouseClicked

    private void butItemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butItemPasteActionPerformed
        codeTableModel.setCostList(getCopyData());
    }//GEN-LAST:event_butItemPasteActionPerformed

    private void butRemotePrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRemotePrintActionPerformed
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost req = new HttpPost("http://localhost:8089/HMSPrintApi/printPharmacyReport");
            req.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            JSONObject parameters = new JSONObject();
            parameters.put("valueA", 1);
            parameters.put("valueB", 2);

            StringEntity se = new StringEntity(parameters.toString());
            req.setEntity(se);

            CloseableHttpResponse res = client.execute(req);

            log.info("Status : " + res.getCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            String output;
            while ((output = br.readLine()) != null) {
                log.info("return from server : " + output);
            }
            /*RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject parameters = new JSONObject();
            parameters.put("valueA", 1);
            parameters.put("valueB", 2);

            RequestEntity requestEntity = new RequestEntity(parameters.toString(), 
                    headers, HttpMethod.GET, URI.create("http://localhost:8089/printPharmacyReport"));
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);*/

            //log.info("JSONObject : " + parameters.toString(3));
        } catch (Exception ex) {
            log.error("butRemotePrint : " + ex.getMessage());
        }
    }//GEN-LAST:event_butRemotePrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butExcel;
    private javax.swing.JButton butItemPaste;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butRemotePrint;
    private javax.swing.JComboBox cboBrand;
    private javax.swing.JComboBox cboCategory;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboCusGroup;
    private javax.swing.JComboBox<String> cboCustomG;
    private javax.swing.JComboBox<String> cboDoctor;
    private javax.swing.JComboBox cboExpenseType;
    private javax.swing.JComboBox<String> cboItemStatus;
    private javax.swing.JComboBox cboItemType;
    private javax.swing.JComboBox cboLocGroup;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboReportType;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JComboBox<String> cboSystem;
    private javax.swing.JComboBox cboToLocation;
    private javax.swing.JComboBox cboTownship;
    private javax.swing.JComboBox cboVouStatus;
    private javax.swing.JCheckBox chkMinus;
    private javax.swing.JCheckBox chkPlus;
    private javax.swing.JCheckBox chkZero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblDueDate;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblDoctorFilter;
    private javax.swing.JTable tblItemCodeFilter;
    private javax.swing.JTable tblReportList;
    private javax.swing.JTable tblTraderFilter;
    private javax.swing.JTextField txtAdmName;
    private javax.swing.JTextField txtAdmNo;
    private javax.swing.JFormattedTextField txtDueDate;
    private javax.swing.JFormattedTextField txtDueTo;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
