/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import static com.cv.app.common.Global.dao;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.SaleExpense;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.helper.SaleDetailHis;
import com.cv.app.pharmacy.database.helper.TTranDetail;
import com.cv.app.pharmacy.database.helper.TraderTransaction;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.ui.common.SaleExpTableModel1;
import com.cv.app.pharmacy.ui.common.SaleVouPrintDetailTableModel;
import com.cv.app.pharmacy.ui.common.SaleVouPrintTableModel;
import com.cv.app.pharmacy.ui.common.TTranDetailTableModel;
import com.cv.app.pharmacy.ui.common.TransactionTableModel;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class SaleVouPrint extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(SaleVouPrint.class.getName());
    private final SaleVouPrintTableModel tableModel = new SaleVouPrintTableModel();
    private final SaleExpTableModel1 seTableModel = new SaleExpTableModel1();
    private int mouseClick = 2;
    private final TableRowSorter<TableModel> sorter;
    private final TransactionTableModel tranTableModel = new TransactionTableModel();
    private final TTranDetailTableModel ttdTableModel = new TTranDetailTableModel();
    private final SaleVouPrintDetailTableModel sdhTableModel = new SaleVouPrintDetailTableModel();
    private int selectRow = -1;
    private Date lastSaleDate;
    private double selVouBalance = 0;

    /**
     * Creates new form SaleVouPrint
     */
    public SaleVouPrint() {
        initComponents();
        assignDate();
        initCombo();
        initTable();
        sorter = new TableRowSorter(tblVouList.getModel());
        tblVouList.setRowSorter(sorter);
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        //txtFrom.setText("01/01/2021");
        //txtTo.setText("15/01/2021");
        cboVouType.setSelectedItem("Whole Sale");

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

    private void assignDate() {
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboLocation, getLocationFilter());
        new ComBoBoxAutoComplete(cboLocation);

        try {
            List<Doctor> listDR = dao.findAllHSQL("select o from Doctor o order by o.doctorName");
            if (listDR != null) {
                if (!listDR.isEmpty()) {
                    List<String> listVC = new ArrayList();
                    for (Doctor dr : listDR) {
                        listVC.add(dr.getDoctorName());
                    }
                    BindingUtil.BindCombo(cboVoucherChecker, listVC);
                    new ComBoBoxAutoComplete(cboVoucherChecker);
                }
            }
            cboLocation.setSelectedIndex(3);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.toString());
        } finally {
            dao.close();
        }
        cboVoucherChecker.setSelectedItem(null);
    }

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowSessCheck = true) order by o.locationName");
            } else {
                return dao.findAll("Location");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return null;
    }

    private String getHSQL() {
        String strFieldName = "sh.cus_id";
        if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
            strFieldName = "tr.stu_no";
        }
        String strSql = "select distinct date(sh.sale_date) sale_date, sh.sale_inv_id, sh.remark, sh.sale_date as sale_dt, \n"
                + "ifnull(sh.cus_id,ifnull(sh.reg_no, sh.stu_no)) cus_id, sh.balance, sh.discount, sh.paid_amount, \n"
                + "ifnull(tr.trader_name, ifnull(pd.patient_name, sh.stu_name)) cus_name, "
                + "usr.user_name, sh.vou_total, l.location_name, sh.deleted, \n"
                + "dr.doctor_name as sale_man, sh.visit_id as vou_chk_usr, sh.is_printed, \n"
                + "tr.stu_no \n"
                + "from sale_his sh\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "join location l on sh.location_id = l.location_id \n"
                + "left join patient_detail pd on sh.reg_no = pd.reg_no \n"
                + "left join trader tr on sh.cus_id = tr.trader_id \n"
                + "left join doctor dr on sh.doctor_id = dr.doctor_id \n"
                + "where date(sh.sale_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQL(txtTo.getText()) + "' and sh.deleted = false ";

        log.info("loc");
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();

            if (strSql.isEmpty()) {
                strSql = " sh.location_id = " + location.getLocationId();
            } else {
                strSql = strSql + " and sh.location_id = " + location.getLocationId();
            }
        } else if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
            strSql = strSql + " and sh.location_id in (select a.location_id from user_location_mapping a \n"
                    + "where a.user_id = '" + Global.loginUser.getUserId()
                    + "' and a.allow_sale = true)";
        }

        log.info("cus");
        if (txtCusId.getText() != null && !txtCusId.getText().isEmpty()) {
            if (strSql.isEmpty()) {
                //strSql = " sh.cus_id = '" + txtCusId.getText() + "'";
                strSql = " " + strFieldName + " = '" + txtCusId.getText().trim() + "'";
            } else {
                //strSql = strSql + " and sh.cus_id = '" + txtCusId.getText() + "'";
                strSql = strSql + " and " + strFieldName + " = '" + txtCusId.getText().trim() + "'";
            }
        }

        log.info("case");
        String vouType = cboVouType.getSelectedItem().toString();
        Trader trader = getTrader(Util1.getPropValue("system.default.customer"));
        String retailCusId = "-";
        if (trader != null) {
            retailCusId = trader.getTraderId();
        }
        switch (vouType) {
            case "Whole Sale":
                if (strSql.isEmpty()) {
                    strSql = " sh.cus_id <> '" + retailCusId + "'";
                } else {
                    strSql = strSql + " and sh.cus_id <> '" + retailCusId + "'";
                }
                break;
            case "Retail Sale":
                if (strSql.isEmpty()) {
                    strSql = " sh.cus_id = '" + retailCusId + "'";
                } else {
                    strSql = strSql + " and sh.cus_id = '" + retailCusId + "'";
                }
                break;
        }

        strSql = strSql + " order by date(sh.sale_date) desc, sh.sale_inv_id desc";

        return strSql;
    }

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            String strFieldName = "o.traderId";
            if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                strFieldName = "o.stuCode";
            }

            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                int locationId = -1;
                String strSql;
                if (cboLocation.getSelectedItem() instanceof Location) {
                    locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId = "
                            + locationId + ") and " + strFieldName + " = '" + traderId.toUpperCase() + "' order by o.traderName";
                } else {
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId in ("
                            + "select a.key.locationId from UserLocationMapping a "
                            + "where a.key.userId = '" + Global.loginUser.getUserId()
                            + "' and a.isAllowSessCheck = true)) and " + strFieldName + " = '"
                            + traderId.toUpperCase() + "' order by o.traderName";
                }
                List<Trader> listTrader = dao.findAllHSQL(strSql);
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                List<Trader> listTrader = dao.findAllHSQL("select o from Trader where " + strFieldName + " = '" + traderId + "'");
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
    }

    private void search() {
        log.info("search Start : " + new Date());
        try {
            String strSql = getHSQL();
            log.info("sql : " + strSql);
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<VoucherSearch> listVS = new ArrayList();
                while (rs.next()) {
                    VoucherSearch vs = new VoucherSearch();
                    vs.setBalance(rs.getDouble("balance"));
                    vs.setCusName(rs.getString("cus_name"));
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        vs.setCusNo(rs.getString("stu_no"));
                    } else {
                        vs.setCusNo(rs.getString("cus_id"));
                    }
                    vs.setDiscount(rs.getDouble("discount"));
                    vs.setInvNo(rs.getString("sale_inv_id"));
                    vs.setRefNo(rs.getString("remark"));
                    vs.setIsPrinted(rs.getBoolean("is_printed"));
                    vs.setLocation(rs.getString("location_name"));
                    vs.setPaid(rs.getDouble("paid_amount"));
                    vs.setSaleMan(rs.getString("sale_man"));
                    vs.setTranDate(rs.getDate("sale_date"));
                    vs.setUserName(rs.getString("user_name"));
                    vs.setVouTotal(rs.getDouble("vou_total"));
                    vs.setVoucherChecker(rs.getString("vou_chk_usr"));
                    vs.setTranDT(rs.getTimestamp("sale_dt"));
                    vs.setTraderId(rs.getString("cus_id"));
                    listVS.add(vs);

                    /*log.info("Vour No : " + rs.getString("sale_inv_id") 
                            + " dt : " + rs.getDate("sale_dt")
                            + " sd : " + rs.getDate("sale_date")
                            + " ts : " + rs.getTimestamp("sale_dt")
                    );*/
                }
                tableModel.setListVS(listVS);
                txtTtlRecords.setValue(listVS.size());
                txtTtlBalance.setValue(NumberUtil.roundTo(tableModel.getBalanceTotal(), 0));
                txtTtlDiscount.setValue(NumberUtil.roundTo(tableModel.getDiscTotal(), 0));
                txtTtlPaid.setValue(NumberUtil.roundTo(tableModel.getPaidTotal(), 0));
                txtTtlVou.setValue(NumberUtil.roundTo(tableModel.getVouTotal(), 0));
                rs.close();
            }
        } catch (Exception ex) {
            log.error("search : " + ex.toString());
        }
        log.info("search End : " + new Date());
    }

    private void getCustomer() {
        if (txtCusId.getText() != null && !txtCusId.getText().isEmpty()) {
            try {
                Trader cus = getTrader(txtCusId.getText().trim().toUpperCase());
                if (cus == null) {
                    txtCusId.setText(null);
                    txtCusName.setText(null);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid customer code.",
                            "Trader Code", JOptionPane.ERROR_MESSAGE);

                } else {
                    selected("CustomerList", cus);
                }

            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtCusId.requestFocus();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;
                if (cus != null) {
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCusId.setText(cus.getStuCode());
                    } else {
                        txtCusId.setText(cus.getTraderId());
                    }
                    //txtCusId.setText(cus.getTraderId());
                    txtCusName.setText(cus.getTraderName());
                    search();
                }
                break;
        }
    }

    private void getCustomerList() {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Customer List", dao, locationId);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void initTable() {
        try {
            tblVouList.getTableHeader().setReorderingAllowed(false);
            tblVouList.getTableHeader().setFont(Global.lableFont);
            tblVouList.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            //Adjust column width
            tblVouList.getColumnModel().getColumn(0).setPreferredWidth(30);//Date
            tblVouList.getColumnModel().getColumn(1).setPreferredWidth(50);//Vou No
            tblVouList.getColumnModel().getColumn(2).setPreferredWidth(30);//Ref. Vou.
            tblVouList.getColumnModel().getColumn(3).setPreferredWidth(200);//Customer
            tblVouList.getColumnModel().getColumn(4).setPreferredWidth(30);//User
            tblVouList.getColumnModel().getColumn(5).setPreferredWidth(15);//Location
            tblVouList.getColumnModel().getColumn(6).setPreferredWidth(30);//Sale Man
            tblVouList.getColumnModel().getColumn(7).setPreferredWidth(30);//Voucher Check
            tblVouList.getColumnModel().getColumn(8).setPreferredWidth(5);//IP
            tblVouList.getColumnModel().getColumn(9).setPreferredWidth(40);//V-Total
            tblVouList.getColumnModel().getColumn(10).setPreferredWidth(30);//Discount
            tblVouList.getColumnModel().getColumn(11).setPreferredWidth(30);//Paid
            tblVouList.getColumnModel().getColumn(12).setPreferredWidth(30);//Balance

            tblVouList.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());

            tblVouList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblVouList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (tblVouList.getSelectedRow() >= 0 && !e.getValueIsAdjusting()) {
                        selectRow = tblVouList.convertRowIndexToModel(tblVouList.getSelectedRow());
                        VoucherSearch vs = tableModel.getSelectVou(selectRow);
                        getVouDetail(vs);
                        log.info("Is Adjusting : " + e.getValueIsAdjusting() + " Vou No : " + vs.getInvNo());
                    }
                }
            });

            tblSE.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblSE.getColumnModel().getColumn(0).setPreferredWidth(50); //Date
            tblSE.getColumnModel().getColumn(1).setPreferredWidth(150);//Expense Type
            tblSE.getColumnModel().getColumn(2).setPreferredWidth(60);//Amt-Out
            JComboBox cboExpenseType = new JComboBox();
            cboExpenseType.setFont(new java.awt.Font("Zawgyi-One", 0, 11));
            BindingUtil.BindCombo(cboExpenseType, dao.findAll("ExpenseType"));
            tblSE.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cboExpenseType));
            addExpenseTableModelListener();

            tblSDH.getTableHeader().setReorderingAllowed(false);
            tblSDH.getTableHeader().setFont(Global.lableFont);
            tblSDH.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            //Adjust column width
            tblSDH.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
            tblSDH.getColumnModel().getColumn(1).setPreferredWidth(200);//Description
            tblSDH.getColumnModel().getColumn(2).setPreferredWidth(30);//Relation-Str
            tblSDH.getColumnModel().getColumn(3).setPreferredWidth(25);//Qty
            tblSDH.getColumnModel().getColumn(4).setPreferredWidth(30);//Cost
            tblSDH.getColumnModel().getColumn(5).setPreferredWidth(40);//Sale Price
            tblSDH.getColumnModel().getColumn(6).setPreferredWidth(30);//Discount
            tblSDH.getColumnModel().getColumn(7).setPreferredWidth(15);//FOC
            tblSDH.getColumnModel().getColumn(8).setPreferredWidth(50);//Amount
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        selVouBalance = 0;
        tableModel.clear();
        seTableModel.clear();
        tranTableModel.clear();
        ttdTableModel.clear();
        sdhTableModel.clear();
        txtCusId.setText("");
        txtCusName.setText("");
        txtTtlRecords.setText("");
        txtTtlVou.setText("");
        txtTtlBalance.setText("");
        txtTtlDiscount.setText("");
        txtTtlPaid.setText("");
        txtPrvLastBalance.setText("");
        txtTtlTransaction.setText("");
        txtTtlExpense.setText("");
        txtCurrPayment.setText("");
        txtLastBalance.setText("");
        cboLocation.setSelectedIndex(0);
        cboVoucherChecker.setSelectedItem(null);
        cboVouType.setSelectedItem("Whole Sale");
        lastSaleDate = null;
    }

    private void getVouDetail(VoucherSearch vs) {
        /*seTableModel.clear();
        tranTableModel.clear();
        ttdTableModel.clear();
        sdhTableModel.clear();*/
        selVouBalance = 0;
        String vouNo = vs.getInvNo();
        cboVoucherChecker.setSelectedItem(vs.getVoucherChecker());
        try {
            SaleHis sh = (SaleHis) dao.find(SaleHis.class, vouNo);
            selVouBalance = NumberUtil.NZero(sh.getBalance());
            double payAmt = NumberUtil.NZero(sh.getPaymentAmt());
            if (payAmt != 0) {
                txtCurrPayment.setValue(payAmt);
                txtCurrPayment.setEnabled(false);
            } else {
                txtCurrPayment.setEnabled(true);
                txtCurrPayment.setValue(0);
            }
            List<SaleExpense> listSaleExpense = dao.findAllHSQL(
                    "select o from SaleExpense o where o.vouNo = '"
                    + vouNo + "'"
            );
            seTableModel.setSaleDate(vs.getTranDate());
            seTableModel.setListDetail(listSaleExpense);
        } catch (Exception ex) {
            log.error("getSaleExpense : " + ex.toString());
        } finally {
            dao.close();
        }
        log.info("Sale Expense");
        String traderId = vs.getTraderId();
        String strTrdOpt;
        log.info("strTodayDateTime : " + vs.getTranDT());
        String strTodayDateTime = DateUtil.toDateTimeStrMYSQL(vs.getTranDT());
        log.info("strTodayDateTime1 : " + strTodayDateTime);
        if (traderId.substring(0, 3).contains("SUP")) {
            strTrdOpt = "SUP";
        } else {
            strTrdOpt = "CUS";
        }

        try {
            dao.execProc("get_trader_transaction",
                    traderId, strTrdOpt, strTodayDateTime, "MMK",
                    Global.loginUser.getUserId(),
                    Global.machineId);
            dao.execSql("delete from trader_tran where machine_id = " + Global.machineId
                    + " and tran_option = 'Sale Voucher Payment (" + vouNo + ")'");
            List<TraderTransaction> listTran = dao.findAll("TraderTransaction",
                    "userId = '" + Global.loginUser.getUserId() + "' and tranType = 'D'"
                    + " and machineId = '" + Global.machineId + "'");
            if (listTran != null) {
                tranTableModel.setListDetail(listTran);
            }
            txtTtlTransaction.setValue(tranTableModel.getTotal());
            List<TTranDetail> listTTDetail = dao.findAll("TTranDetail", "userId = '" + Global.loginUser.getUserId()
                    + "' and machineId = '" + Global.machineId + "'");
            ttdTableModel.setListTTranDetail(listTTDetail);
        } catch (Exception ex) {
            log.error("getTraderTransaction : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
        log.info("Trader Transaction");
        try {
            ResultSet resultSet = dao.getPro("trader_last_balance",
                    traderId, strTrdOpt,
                    strTodayDateTime, "MMK");

            if (resultSet != null) {
                resultSet.next();
                txtPrvLastBalance.setValue(resultSet.getDouble("var_last_balance"));
                if (resultSet.getDate("var_last_sale_date") != null) {
                    lastSaleDate = resultSet.getDate("var_last_sale_date");
                    String strLastSaleDate = DateUtil.toDateStr(lastSaleDate) + " Prv Last Balance : ";
                    lblPrvLastBalance.setText(strLastSaleDate);
                } else {
                    lblPrvLastBalance.setText("Prv Last Balance : ");
                    lastSaleDate = null;
                }
            }
        } catch (Exception ex) {
            log.error("getTraderLastBalance : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
        log.info("Trader Last Balance");
        try {
            ResultSet rs = dao.execSQL("select sdh.med_id, med.med_name, med.med_rel_str, "
                    + "sdh.expire_date, concat(sdh.sale_qty, ' ', sdh.item_unit) as qty,\n"
                    + "sdh.cost_price, sdh.sale_price, sdh.item_discount, "
                    + "if(ifnull(foc_qty,0)=0,'',concat(sdh.foc_qty,' ', sdh.foc_unit)) as foc_qty,\n"
                    + "sdh.sale_amount, med.short_name \n"
                    + "from sale_detail_his sdh, medicine med\n"
                    + "where sdh.med_id = med.med_id and sdh.vou_no = '"
                    + vouNo + "' order by sdh.unique_id");
            if (rs != null) {
                List<SaleDetailHis> listSDH = new ArrayList();
                while (rs.next()) {
                    SaleDetailHis sdh = new SaleDetailHis();
                    sdh.setCostPrice(rs.getDouble("cost_price"));
                    sdh.setExpDate(rs.getDate("expire_date"));
                    sdh.setFocQty(rs.getString("foc_qty"));
                    sdh.setItemDiscount(rs.getFloat("item_discount"));
                    sdh.setMedId(rs.getString("short_name"));
                    sdh.setMedName(rs.getString("med_name"));
                    sdh.setRelStr(rs.getString("med_rel_str"));
                    sdh.setSaleAmount(rs.getDouble("sale_amount"));
                    sdh.setSalePrice(rs.getDouble("sale_price"));
                    sdh.setSaleQty(rs.getString("qty"));

                    listSDH.add(sdh);
                }

                sdhTableModel.setList(listSDH);
            }
            log.info("Sale Detail His");
        } catch (Exception ex) {
            log.error("saleDetailHis : " + ex.toString());
        } finally {
            dao.close();
        }

        calculateLastBalance();
    }

    private void addExpenseTableModelListener() {
        tblSE.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();
                switch (column) {
                    case 2: //Amt Out
                        txtTtlExpense.setValue(seTableModel.getTotal());
                        break;
                }
                calculateLastBalance();
            }
        });
    }

    private boolean save() {
        boolean isSave = true;
        selectRow = tblVouList.convertRowIndexToModel(tblVouList.getSelectedRow());
        VoucherSearch vs = tableModel.getSelectVou(selectRow);
        String vouNo = vs.getInvNo();
        String traderId = vs.getCusNo();
        double ttlPay = NumberUtil.NZero(txtCurrPayment.getText());
        try {
            SaleHis sh = (SaleHis) dao.find(SaleHis.class, vouNo);
            if (cboVoucherChecker.getSelectedItem() != null) {
                sh.setVisitId(cboVoucherChecker.getSelectedItem().toString());
            } else {
                sh.setVisitId(null);
            }

            if (sh.getPaymentId() == null && ttlPay != 0) {
                sh.setPaymentAmt(ttlPay);

                TraderPayHis tph = new TraderPayHis();
                tph.setPayDate(sh.getSaleDate());
                tph.setTrader(sh.getCustomerId());
                tph.setRemark("Sale Voucher Payment (" + vouNo + ")");
                tph.setPaidAmtC(ttlPay);
                tph.setDiscount(0.0);
                tph.setCurrency(sh.getCurrencyId());
                tph.setExRate(1.0);
                tph.setPaidAmtP(ttlPay);
                tph.setCreatedBy(Global.loginUser);
                tph.setPayOption("Cash");
                tph.setParentCurr(tph.getCurrency());
                tph.setPayDt(sh.getSaleDate());

                String strTrdOpt;
                if (traderId.substring(0, 3).contains("SUP")) {
                    strTrdOpt = "SUP";
                } else {
                    strTrdOpt = "CUS";
                }
                List<PaymentVou> listPV = getUnPaidVour(traderId, strTrdOpt,
                        sh.getCurrencyId().getCurrencyCode(),
                        DateUtil.toDateStr(vs.getTranDate()), ttlPay);
                tph.setListDetail(listPV);

                Location location = sh.getLocationId();
                tph.setLocation(location);

                try {
                    dao.open();
                    dao.save(tph);
                    sh.setPaymentId(tph.getPaymentId());
                    dao.save(sh);
                    //dao.commit();
                    txtCurrPayment.setEnabled(false);
                    //ToLedgerPharmacy.entryToLedgerCusPay(tph, "NEW");
                } catch (Exception e) {
                    //dao.rollBack();
                    isSave = false;
                    log.error("saveTraderpayHis : " + e.getMessage());
                } finally {
                    dao.close();
                }
            } else {
                dao.save(sh);
            }

            if (cboVoucherChecker.getSelectedItem() != null) {
                tableModel.setVoucherChecker(selectRow, cboVoucherChecker.getSelectedItem().toString());
            } else {
                tableModel.setVoucherChecker(selectRow, null);
            }
        } catch (Exception ex) {
            isSave = false;
            log.error("save : " + ex.toString());
        } finally {
            dao.close();
        }

        return isSave;
    }

    private List<PaymentVou> getUnPaidVour(String traderId, String strTrdOpt,
            String strCurrency, String payDate, double ttlPay) {
        List<PaymentVou> listVou = new ArrayList();
        try {
            ResultSet resultSet1 = dao.getPro("GET_PAYMENT_INFO",
                    traderId, strTrdOpt,
                    //DateUtil.getTodayDateStrMYSQL(), 
                    //DateUtil.toDateStrMYSQL(txtPayDate.getText()),
                    DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(payDate)),
                    strCurrency);
            String strOpDate = null;

            if (resultSet1 != null) {
                while (resultSet1.next()) {
                    strOpDate = resultSet1.getString("VAR_OP_DATE");
                }
            }

            ResultSet resultSet = dao.getPro("GET_UNPAID_VOU", traderId,
                    strTrdOpt, strOpDate,
                    DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(payDate)),
                    strCurrency, "1900-01-01", "1900-01-01");
            if (resultSet != null) {
                while (resultSet.next()) {
                    double balance = NumberUtil.NZero(resultSet.getDouble("BAL"));
                    if (balance != 0.0) {
                        listVou.add(new PaymentVou(resultSet.getString("VOU_NO"),
                                resultSet.getDouble("BAL"), resultSet.getString("VOU_TYPE"),
                                resultSet.getDate("tran_date"),
                                resultSet.getString("ref_no"),
                                resultSet.getDouble("BAL")));
                    }
                }

                double leftAmt = ttlPay;
                List<PaymentVou> tmpListVou = new ArrayList();
                for (int i = 0; i < listVou.size(); i++) {
                    PaymentVou pv = listVou.get(i);

                    if (pv.getVouBal() == leftAmt) {
                        pv.setVouPaid(leftAmt);
                        pv.setBalance(0.0);
                        i = listVou.size();
                    } else if (leftAmt > pv.getVouBal()) {
                        pv.setVouPaid(pv.getVouBal());
                        pv.setBalance(0.0);
                        leftAmt = leftAmt - pv.getVouBal();
                    } else {
                        pv.setVouPaid(leftAmt);
                        pv.setBalance(pv.getVouBal() - pv.getVouPaid());
                        i = listVou.size();
                    }

                    tmpListVou.add(pv);
                }

                listVou = tmpListVou;
            }
        } catch (SQLException ex) {
            log.error("getUnPaidVour : " + ex.toString());
        } catch (Exception ex1) {
            log.error("getUnPaidVour : " + ex1.toString());
        } finally {
            dao.close();
        }
        return listVou;
    }

    private void print() {
        if (save()) {
            selectRow = tblVouList.convertRowIndexToModel(tblVouList.getSelectedRow());
            VoucherSearch vs = tableModel.getSelectVou(selectRow);
            String vouNo = vs.getInvNo();
            try {
                SaleHis sh = (SaleHis) dao.find(SaleHis.class, vouNo);
                insertTraderTran(sh);

                String traderId = Util1.getPropValue("system.sale.general.customer");
                String tmpTraderId = sh.getCustomerId().getTraderId();
                CustomerGroup cusGroup = sh.getCustomerId().getTraderGroup();
                String strCusGroup = "-";
                if (cusGroup != null) {
                    strCusGroup = cusGroup.getGroupId();
                }
                String reportNameProp;
                if (traderId.equals(tmpTraderId)) {
                    reportNameProp = "report.file.saleV";
                } else if (!strCusGroup.equals("KS")) {
                    reportNameProp = "report.file.comp";
                } else {
                    reportNameProp = "report.file.saleW";
                }
                String reportName = Util1.getPropValue(reportNameProp);
                String reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + reportName;
                //String printerName = Util1.getPropValue("report.vou.printer");
                String compName = Util1.getPropValue("report.company.name");
                //String printMode = Util1.getPropValue("report.vou.printer.mode");
                String bankInfo = Util1.getPropValue("report.bankinfo");

                Map<String, Object> params = new HashMap();
                params.put("p_bank_desp", bankInfo);
                params.put("invoiceNo", sh.getSaleInvId());
                params.put("due_date", sh.getDueDate());
                if (sh.getCustomerId() != null) {
                    params.put("customerName", sh.getCustomerId().getTraderName());
                    params.put("reg_no", sh.getCustomerId().getTraderId());
                } else {
                    params.put("customerName", "");
                    params.put("reg_no", "");
                }
                params.put("saleDate", sh.getSaleDate());
                params.put("grandTotal", sh.getVouTotal());
                params.put("paid", sh.getPaid());
                params.put("discount", sh.getDiscount());
                params.put("tax", sh.getTaxAmt());
                params.put("voubalance", sh.getBalance());
                params.put("balance", sh.getRefund());
                params.put("vou_balance", sh.getVouTotal() + sh.getTaxAmt()
                        - (sh.getDiscount() + sh.getPaid()));
                params.put("user", Global.loginUser.getUserShortName());
                //params.put("listParam", listExpense);
                params.put("compName", compName);
                params.put("prv_date", lblPrvLastBalance.getText());
                params.put("prv_balance", Double.parseDouble(txtPrvLastBalance.getValue().toString()));
                params.put("tran_total", NumberUtil.NZero(tranTableModel.getTotal()));
                params.put("last_balance", txtLastBalance.getValue());
                params.put("lblPrvBalance", txtPrvLastBalance.getValue().toString());
                params.put("prv_date", lblPrvLastBalance.getText());
                params.put("p_machine_id", Global.machineId);
                Location loc = (Location) sh.getLocationId();
                if (loc != null) {
                    params.put("loc_name", loc.getLocationName());
                } else {
                    params.put("loc_name", "-");
                }
                if (sh.getCustomerId() != null) {
                    Trader tr = sh.getCustomerId();
                    if (tr.getTownship() != null) {
                        params.put("township", tr.getTownship().getTownshipName());
                    } else {
                        params.put("township", " ");
                    }
                    if (tr.getAddress() != null) {
                        params.put("address", tr.getAddress());
                    } else {
                        params.put("address", " ");
                    }
                }

                if (sh.getDueDate() != null) {
                    params.put("pay_info", DateUtil.toDateStr(sh.getDueDate()) + " ေငြေကာက္မည္");
                } else {
                    params.put("pay_info", " ");
                }

                params.put("vou_status", " ");
                params.put("pay_retin", tranTableModel.getTotal());
                params.put("comp_address", Util1.getPropValue("report.address"));
                params.put("phone", Util1.getPropValue("report.phone"));
                params.put("user_id", Global.loginUser.getUserId());
                params.put("user_short", Global.loginUser.getUserShortName());
                params.put("inv_id", sh.getSaleInvId());
                params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path"));
                params.put("IMAGE_PATH", Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path"));
                params.put("comp_name", Util1.getPropValue("report.company.name"));
                params.put("category", Util1.getPropValue("report.company.cat"));
                params.put("remark", sh.getRemark());
                params.put("REPORT_CONNECTION", dao.getConnection());
                PaymentType pt = sh.getPaymentTypeId();
                if (pt != null) {
                    params.put("prm_pay_type", pt.getPaymentTypeName());
                }

                ReportUtil.viewReport(reportPath, params, dao.getConnection());
                sh.setIsPrinted(true);
                tableModel.setPrintStatus(selectRow, true);
                dao.save(sh);
            } catch (Exception ex) {
                log.error("print : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void insertTraderTran(SaleHis sh) {
        try {
            String traderId = Util1.getPropValue("system.sale.general.customer");
            if (sh.getCustomerId() != null) {
                if (!sh.getCustomerId().getTraderId().equals(traderId)) {
                    TraderTransaction tt = new TraderTransaction();
                    tt.setAmount(
                            NumberUtil.getDouble(txtPrvLastBalance.getText())
                            + NumberUtil.getDouble(sh.getBalance())
                            + NumberUtil.getDouble(txtTtlTransaction.getText())
                            - NumberUtil.getDouble(txtCurrPayment.getValue())
                    );
                    tt.setUserId(Global.loginUser.getUserId());
                    //tt.setTranOption("Last Balance : ");
                    if (tt.getAmount() < 0) {
                        tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalanceM"));
                    } else {
                        tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalance"));
                    }
                    tt.setTranDate(sh.getSaleDate());
                    tt.setTranType("N");
                    tt.setSortId(3);
                    tt.setMachineId(Global.machineId);
                    dao.execSql("delete from trader_tran where machine_id = "
                            + Global.machineId + " and tran_option = '"
                            + tt.getTranOption() + "' and tran_date = '"
                            + DateUtil.toDateStrMYSQL(DateUtil.toDateStr(sh.getSaleDate())) + "'"
                    );
                    dao.save(tt);

                    //For Expense
                    List<SaleExpense> listExpense = seTableModel.getListExpense();
                    if (listExpense != null) {
                        if (!listExpense.isEmpty()) {
                            for (SaleExpense se : listExpense) {
                                tt = new TraderTransaction();
                                tt.setAmount(se.getExpAmount());
                                tt.setUserId(Global.loginUser.getUserId());
                                //tt.setTranOption("Current Vou : ");
                                tt.setTranOption(se.getExpType().getExpenseName());
                                tt.setTranDate(sh.getSaleDate());
                                tt.setTranType("N");
                                tt.setSortId(2);
                                tt.setMachineId(Global.machineId);
                                dao.execSql("delete from trader_tran where machine_id = "
                                        + Global.machineId + " and tran_option = '"
                                        + tt.getTranOption() + "' and tran_date = '"
                                        + DateUtil.toDateStrMYSQL(DateUtil.toDateStr(sh.getSaleDate())) + "'"
                                );
                                dao.save(tt);
                            }
                        }
                    }

                    tt = new TraderTransaction();
                    tt.setAmount(NumberUtil.NZero(txtPrvLastBalance.getText()));
                    tt.setUserId(Global.loginUser.getUserId());
                    //tt.setTranOption("Pre. Balance : ");
                    tt.setTranOption(Util1.getPropValue("system.app.sale.prvbalance"));
                    tt.setTranDate(lastSaleDate);
                    tt.setTranType("N");
                    tt.setSortId(1);
                    tt.setMachineId(Global.machineId);
                    String strSql = "delete from trader_tran where machine_id = "
                            + Global.machineId + " and tran_option = '"
                            + tt.getTranOption() + "' and tran_date = '"
                            + DateUtil.toDateStrMYSQL(DateUtil.toDateStr(lastSaleDate)) + "'";
                    dao.execSql(strSql);
                    dao.save(tt);

                    double currPayment = NumberUtil.NZero(txtCurrPayment.getText());
                    if (currPayment != 0) {
                        tt = new TraderTransaction();
                        tt.setAmount(currPayment);
                        tt.setUserId(Global.loginUser.getUserId());
                        tt.setTranOption("Sale Vou Payment (" + sh.getSaleInvId() + ")");
                        tt.setTranDate(sh.getSaleDate());
                        tt.setTranType("D");
                        tt.setSortId(2);
                        tt.setMachineId(Global.machineId);
                        dao.execSql("delete from trader_tran where machine_id = "
                                + Global.machineId + " and tran_option = '"
                                + tt.getTranOption() + "' and tran_date = '"
                                + DateUtil.toDateStrMYSQL(DateUtil.toDateStr(sh.getSaleDate())) + "'"
                        );
                        dao.save(tt);
                    }

                    //Last Balance
                    /*tt = new TraderTransaction();
                    tt.setAmount(NumberUtil.getDouble(txtLastBalance.getText()));
                    tt.setUserId(Global.loginUser.getUserId());
                    //tt.setTranOption("Last Balance : ");
                    if (tt.getAmount() < 0) {
                        tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalanceM"));
                    } else {
                        tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalance"));
                    }
                    tt.setTranDate(sh.getSaleDate());
                    tt.setTranType("N");
                    tt.setSortId(4);
                    tt.setMachineId(Global.machineId);
                    dao.execSql("delete from trader_tran where machine_id = "
                            + Global.machineId + " and tran_option = '"
                            + tt.getTranOption() + "' and tran_date = '"
                            + DateUtil.toDateStrMYSQL(DateUtil.toDateStr(sh.getSaleDate())) + "'"
                            + " and tran_type = 'N' and sort_order = 4"
                    );
                    dao.save(tt);*/
                }
            }
        } catch (Exception ex) {
            log.error("insertTraderTran : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void calculateLastBalance() {
        double prvBalance = NumberUtil.NZero(txtPrvLastBalance.getValue());
        double ttlTran = NumberUtil.NZero(txtTtlTransaction.getValue());
        double ttlExpense = NumberUtil.NZero(txtTtlExpense.getValue());
        double ttlPay = NumberUtil.NZero(txtCurrPayment.getValue());
        double lastBalance = selVouBalance
                + prvBalance
                + ttlTran
                + ttlExpense
                - ttlPay;
        txtLastBalance.setValue(lastBalance);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCusId = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        butSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVouList = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cboVouType = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblSDH = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSE = new javax.swing.JTable();
        txtTtlExpense = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtPrvLastBalance = new javax.swing.JFormattedTextField();
        lblPrvLastBalance = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTransaction = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblTranDetail = new javax.swing.JTable();
        txtTtlTransaction = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCurrPayment = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtLastBalance = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        cboVoucherChecker = new javax.swing.JComboBox<>();
        butSave = new javax.swing.JButton();
        butPrint = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtTtlBalance = new javax.swing.JFormattedTextField();
        txtTtlPaid = new javax.swing.JFormattedTextField();
        txtTtlDiscount = new javax.swing.JFormattedTextField();
        txtTtlVou = new javax.swing.JFormattedTextField();
        txtTtlRecords = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel1.setText("From : ");

        txtFrom.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel2.setText("To : ");

        txtTo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel3.setText("Customer : ");

        txtCusId.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        txtCusId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusIdActionPerformed(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.setFont(Global.textFont);
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        butSearch.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        tblVouList.setFont(Global.textFont);
        tblVouList.setModel(tableModel);
        tblVouList.setRowHeight(30);
        jScrollPane1.setViewportView(tblVouList);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel4.setText("Location : ");

        cboLocation.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel5.setText("Type : ");

        cboVouType.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        cboVouType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Whole Sale", "Retail Sale" }));

        tblSDH.setFont(Global.textFont);
        tblSDH.setModel(sdhTableModel);
        tblSDH.setRowHeight(30);
        jScrollPane5.setViewportView(tblSDH);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        tblSE.setFont(Global.textFont);
        tblSE.setModel(seTableModel);
        tblSE.setRowHeight(27);
        jScrollPane2.setViewportView(tblSE);

        txtTtlExpense.setEditable(false);
        txtTtlExpense.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlExpense.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Expense Total : ");

        txtPrvLastBalance.setEditable(false);
        txtPrvLastBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrvLastBalance.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        lblPrvLastBalance.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblPrvLastBalance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrvLastBalance.setText("Prv Last Balance : ");

        tblTransaction.setFont(Global.textFont);
        tblTransaction.setModel(tranTableModel);
        tblTransaction.setRowHeight(27);
        jScrollPane3.setViewportView(tblTransaction);

        tblTranDetail.setFont(Global.textFont);
        tblTranDetail.setModel(ttdTableModel);
        tblTranDetail.setRowHeight(27);
        jScrollPane4.setViewportView(tblTranDetail);

        txtTtlTransaction.setEditable(false);
        txtTtlTransaction.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlTransaction.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTtlTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTtlTransactionActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Transaction Total : ");

        txtCurrPayment.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCurrPayment.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtCurrPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCurrPaymentFocusLost(evt);
            }
        });
        txtCurrPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrPaymentActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Current Payment : ");

        txtLastBalance.setEditable(false);
        txtLastBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtLastBalance.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Last Balance : ");

        cboVoucherChecker.setFont(Global.textFont);
        cboVoucherChecker.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voucher Checker", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 15))); // NOI18N

        butSave.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butPrint.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtTtlTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurrPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lblPrvLastBalance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrvLastBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLastBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(cboVoucherChecker, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCurrPayment, txtLastBalance, txtTtlExpense});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butPrint, butSave});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrvLastBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrvLastBalance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCurrPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLastBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboVoucherChecker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butPrint)
                        .addComponent(butSave)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {butPrint, butSave, cboVoucherChecker});

        butClear.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        txtTtlBalance.setEditable(false);
        txtTtlBalance.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total Balance", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 15))); // NOI18N
        txtTtlBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlBalance.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtTtlPaid.setEditable(false);
        txtTtlPaid.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total Paid", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 15))); // NOI18N
        txtTtlPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlPaid.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtTtlDiscount.setEditable(false);
        txtTtlDiscount.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total Discount", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 15))); // NOI18N
        txtTtlDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlDiscount.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtTtlVou.setEditable(false);
        txtTtlVou.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total Voucher", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 15))); // NOI18N
        txtTtlVou.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlVou.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtTtlRecords.setEditable(false);
        txtTtlRecords.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total Records", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 13))); // NOI18N
        txtTtlRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlRecords.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel11.setText("       ");

        jLabel12.setText("           ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtTtlRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTtlBalance, txtTtlDiscount, txtTtlPaid, txtTtlVou});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTtlBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTtlPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTtlDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtTtlVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

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
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCusId, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCusName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVouType, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCusId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSearch)
                    .addComponent(jLabel4)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cboVouType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );
    }// </editor-fold>//GEN-END:initComponents

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

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        if (txtCusId.getText() == null || txtCusId.getText().isEmpty()) {
            txtCusName.setText(null);
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        tableModel.clear();
        seTableModel.clear();
        tranTableModel.clear();
        ttdTableModel.clear();
        sdhTableModel.clear();
        /*txtCusId.setText("");
        txtCusName.setText("");
        txtTtlRecords.setText("");
        txtTtlVou.setText("");
        txtTtlBalance.setText("");
        txtTtlDiscount.setText("");
        txtTtlPaid.setText("");
        txtPrvLastBalance.setText("");
        txtTtlTransaction.setText("");
        txtTtlExpense.setText("");
        txtCurrPayment.setText("");
        txtLastBalance.setText("");
        cboLocation.setSelectedIndex(0);*/
        cboVoucherChecker.setSelectedItem(null);
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtTtlTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTtlTransactionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTtlTransactionActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        print();
    }//GEN-LAST:event_butPrintActionPerformed

    private void txtCurrPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrPaymentActionPerformed
        calculateLastBalance();
    }//GEN-LAST:event_txtCurrPaymentActionPerformed

    private void txtCurrPaymentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrPaymentFocusLost
        calculateLastBalance();
    }//GEN-LAST:event_txtCurrPaymentFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JComboBox<String> cboVouType;
    private javax.swing.JComboBox<String> cboVoucherChecker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblPrvLastBalance;
    private javax.swing.JTable tblSDH;
    private javax.swing.JTable tblSE;
    private javax.swing.JTable tblTranDetail;
    private javax.swing.JTable tblTransaction;
    private javax.swing.JTable tblVouList;
    private javax.swing.JFormattedTextField txtCurrPayment;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtLastBalance;
    private javax.swing.JFormattedTextField txtPrvLastBalance;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTtlBalance;
    private javax.swing.JFormattedTextField txtTtlDiscount;
    private javax.swing.JFormattedTextField txtTtlExpense;
    private javax.swing.JFormattedTextField txtTtlPaid;
    private javax.swing.JFormattedTextField txtTtlRecords;
    private javax.swing.JFormattedTextField txtTtlTransaction;
    private javax.swing.JFormattedTextField txtTtlVou;
    // End of variables declaration//GEN-END:variables
}
