/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.helper.VoucherPayment;
import com.cv.app.pharmacy.database.view.VTraderPayment;
import com.cv.app.pharmacy.ui.common.CPaymentEntryTableModel1;
import com.cv.app.pharmacy.ui.common.PaymentHisTableModel;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.MapMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author lenovo
 */
public class CustomerPayment1 extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(CustomerPayment1.class.getName());
    private final CPaymentEntryTableModel1 tblPaymentEntry = new CPaymentEntryTableModel1();
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;
    private final PaymentHisTableModel model = new PaymentHisTableModel();
    private int selectRow = -1;
    private int mouseClick = 2;

    //private final TableRowSorter<TableModel> tblTraderSorter;
    /**
     * Creates new form CustomerPayment1
     */
    public CustomerPayment1() {
        initComponents();
        initTableCusPay();
        initCombo();
        // tblPaymentEntry.setObserver(this);
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblVouList.getModel());
        tblVouList.setRowSorter(sorter);
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        getData();
        initTable();
        calculateTotal();
        actionMapping();

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

    private void initTableCusPay() {
        tblVouList.getTableHeader().setFont(Global.lableFont);

        tblVouList.getColumnModel().getColumn(0).setPreferredWidth(50);//Cus-No
        tblVouList.getColumnModel().getColumn(1).setPreferredWidth(200);//Cus-Name
        tblVouList.getColumnModel().getColumn(2).setPreferredWidth(60);//Vou No.
        tblVouList.getColumnModel().getColumn(3).setPreferredWidth(30);//Vou Date
        tblVouList.getColumnModel().getColumn(4).setPreferredWidth(30);//Due Date
        tblVouList.getColumnModel().getColumn(5).setPreferredWidth(15);//Overdue
        tblVouList.getColumnModel().getColumn(6).setPreferredWidth(40);//Vou Total
        tblVouList.getColumnModel().getColumn(7).setPreferredWidth(40);//Ttl Paid
        tblVouList.getColumnModel().getColumn(8).setPreferredWidth(40);//Paid
        tblVouList.getColumnModel().getColumn(9).setPreferredWidth(30);//Discount
        tblVouList.getColumnModel().getColumn(10).setPreferredWidth(40);//Vou Balance
        tblVouList.getColumnModel().getColumn(11).setPreferredWidth(3);//FP

        tblVouList.getColumnModel().getColumn(3).setCellRenderer(new TableDateFieldRenderer());
        tblVouList.getColumnModel().getColumn(4).setCellRenderer(new TableDateFieldRenderer());
    }

    private void calculateTotal() {
        Double totalBalance = 0.0;
        Double totalDiscount = 0.0;
        Double totalPayment = 0.0;

        for (int i = 0; i < tblVouList.getRowCount(); i++) {
            totalBalance += NumberUtil.NZero(tblVouList.getValueAt(i, 10));
            totalDiscount += NumberUtil.NZero(tblVouList.getValueAt(i, 9));
            totalPayment += NumberUtil.NZero(tblVouList.getValueAt(i, 8));
        }

        txtTotalBalance.setValue(totalBalance);
        txtTotalDiscount.setValue(totalDiscount);
        txtTotalPaid.setValue(totalPayment);
    }

    private void initCombo() {
        try {
            bindStatus = true;
            BindingUtil.BindComboFilter(cboCusGroup,
                    dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'CUS' order by o.groupName"));
            BindingUtil.BindComboFilter(cboCusGroupFilter,
                    dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'CUS' order by o.groupName"));

            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                Object tmpObj;
                if (Util1.getPropValue("system.login.default.value").equals("Y")) {
                    tmpObj = Global.loginLocation;
                } else {
                    tmpObj = Util1.getDefaultValue("Location");
                }
                BindingUtil.BindCombo(cboLocation, getLocationFilter());
                cboLocation.setSelectedItem(tmpObj);
                int index = cboLocation.getSelectedIndex();
                if (index == -1) {
                    if (cboLocation.getItemCount() > 0) {
                        cboLocation.setSelectedIndex(0);
                    }
                }
            } else {
                BindingUtil.BindComboFilter(cboLocation, getLocationFilter());
            }

            Object tmpObj;
            if (Util1.getPropValue("system.login.default.value").equals("Y")) {
                tmpObj = Global.loginLocation;
            } else {
                tmpObj = Util1.getDefaultValue("Location");
            }
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                BindingUtil.BindCombo(cboLocation1, getLocationFilter());
                cboLocation1.setSelectedItem(tmpObj);
                int index = cboLocation1.getSelectedIndex();
                if (index == -1) {
                    if (cboLocation1.getItemCount() > 0) {
                        cboLocation1.setSelectedIndex(0);
                    }
                }
            } else {
                BindingUtil.BindCombo(cboLocation1, dao.findAll("Location",
                        new Location(0, "All")));
                cboLocation1.setSelectedIndex(0);
            }
            BindingUtil.BindCombo(cboUser, dao.findAll("Appuser",
                    new Appuser("000", "All")));

            cboUser.setSelectedIndex(0);

            new ComBoBoxAutoComplete(cboLocation1);
            new ComBoBoxAutoComplete(cboUser);

            cboPayOption.removeAllItems();
            cboPayOption.addItem("All");
            cboPayOption.addItem("Cash");
            cboPayOption.addItem("Credit Card");

            bindStatus = false;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowCusPayVou = true) order by o.locationName");
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

    private void getData() {
        try {

            String strCusGroup = "-";
            if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
                strCusGroup = ((CustomerGroup) cboCusGroup.getSelectedItem()).getGroupId();
            }
            String strBaseGroup = Util1.getPropValue("system.cus.base.group");
            if (strBaseGroup == null) {
                strBaseGroup = "-";
            } else if (strBaseGroup.isEmpty()) {
                strBaseGroup = "-";
            }
            String strSql = "select a.*, if(a.ttl_overdue<0,0,a.ttl_overdue) as ttl_overdue1 from (\n"
                    + "select vob.tran_date sale_date, vob.vou_no, vob.trader_id cus_id, \n"
                    + "		   t.trader_name, vob.vou_type, date_add(vob.tran_date, interval ifnull(t.credit_days,0) day) due_date,\n"
                    + "		   'Opening' ref_no, vob.vou_total, vob.paid_amount ttl_paid, vob.discount, vob.balance,\n"
                    + "		   vob.bal, if(date_add(vob.tran_date, interval ifnull(t.credit_days,0) day)='-',0,\n"
                    + "		   if(DATEDIFF(sysdate(),date_add(vob.tran_date, interval ifnull(t.credit_days,0) day))<0,0,\n"
                    + "		   DATEDIFF(sysdate(),date_add(vob.tran_date, interval ifnull(t.credit_days,0) day)))) ttl_overdue,\n"
                    + "         t.stu_no \n"
                    + "	  from v_opening_balance vob\n"
                    + "	  join trader t on vob.trader_id = t.trader_id\n"
                    + "	  left join customer_group cg on t.group_id = cg.group_id\n"
                    + "	 where bal > 0 and (ifnull(t.group_id,'-') = '" + strCusGroup + "' or '" + strCusGroup + "' = '-')"
                    + "    and (ifnull(t.group_id,'-') <> '" + strBaseGroup + "' or '" + strBaseGroup + "' = '-') "
                    + "    and vob.trader_id in (select trader_id from v_location_trader_mapping where "
                    + "location_id = strLocation and map_status = true and discriminator = 'C')"
                    + " union all "
                    + "select sh.sale_date, sale_inv_id vou_no, sh.cus_id, t.trader_name, 'SALE' vou_type,\n"
                    + "       sh.due_date, sh.remark ref_no, sh.vou_total, (sh.paid_amount+ifnull(pah.pay_amt,0)) as ttl_paid, "
                    + "sh.discount, sh.balance,\n"
                    + "	   sum(ifnull(balance,0))-(ifnull(pah.pay_amt,0)) bal, \n"
                    + "	   if(ifnull(sh.due_date,'-')='-',0,if(DATEDIFF(sysdate(),sh.due_date)<0,0,DATEDIFF(sysdate(),sh.due_date))) as ttl_overdue, \n"
                    + "    t.stu_no \n"
                    + "from sale_his sh\n"
                    + "left join trader t on sh.cus_id = t.trader_id\n"
                    + "left join (select pv.vou_no, sum(ifnull(pv.vou_paid,0)+ifnull(pv.discount,0)) pay_amt, pv.vou_type\n"
                    + "			   from payment_his ph, pay_his_join phj, payment_vou pv\n"
                    + "			  where ph.payment_id = phj.payment_id and phj.tran_id = pv.tran_id\n"
                    + "				and ph.deleted = false\n"
                    + "				and pv.vou_type = 'SALE'\n"
                    + "			  group by pv.vou_no, pv.vou_type) pah on sh.sale_inv_id = pah.vou_no\n"
                    + "where sh.deleted = false and (ifnull(t.group_id,'-') = '" + strCusGroup + "' or '" + strCusGroup + "' = '-')"
                    + "    and (ifnull(t.group_id,'-') <> '" + strBaseGroup + "' or '" + strBaseGroup + "' = '-') ";

            String strLocation = "-";
            if (cboLocation.getSelectedItem() instanceof Location) {
                strLocation = ((Location) cboLocation.getSelectedItem()).getLocationId().toString();
                strSql = strSql + " and sh.location_id = " + strLocation;
                strSql = strSql + " and sh.cus_id in (select trader_id from v_location_trader_mapping where "
                        + "location_id = " + strLocation + " and map_status = true)";
                strSql = strSql.replace("strLocation", strLocation);
            }
            strSql = strSql + " group by sh.sale_inv_id, sh.sale_date,sh.vou_total, sh.paid_amount, sh.discount, sh.balance) a\n"
                    + "where a.bal > 0 order by a.ttl_overdue desc, a.sale_date, a.vou_no";

            log.info("sql : " + strSql);

            ResultSet rs = dao.execSQL(strSql);

            List<VoucherPayment> listVP = null;
            if (rs != null) {
                listVP = new ArrayList();
                while (rs.next()) {
                    listVP.add(new VoucherPayment(
                            rs.getDate("sale_date"),
                            rs.getString("vou_no"),
                            rs.getString("cus_id"),
                            rs.getString("trader_name"),
                            rs.getString("vou_type"),
                            rs.getDate("due_date"),
                            rs.getDouble("vou_total"),
                            rs.getDouble("discount"),
                            rs.getDouble("ttl_paid"),
                            rs.getDouble("bal"),
                            rs.getInt("ttl_overdue1"),
                            null,
                            null,
                            rs.getString("stu_no")
                    ));
                }
                //tblPaymentEntry.setListVP(listVP);
            }
            tblPaymentEntry.setListVP(listVP);

        } catch (Exception ex) {
            log.error("initTable tblOTProcedure : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void search() {
        String strSql = getHSQL();
        log.info("strSql : " + strSql);
        try {
            dao.open();
            model.setListTraderPayHis(dao.findAllHSQL(strSql));
            dao.close();
            dao.closeStatment();
            Double total = model.getTotal();
            txtTotal.setValue(total);
        } catch (Exception ex) {
            log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private String getHSQL() {
        String strFieldName = "p.traderId";
        if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
            strFieldName = "p.stuNo";
        }

        String strSql = "select distinct p from VTraderPayment p where p.payDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "' and p.deleted = false ";

        if (txtCode.getText() != null && !txtCode.getText().isEmpty()) {
            //strSql = strSql + " and p.traderId = '" + txtCode.getText() + "'";
            strSql = strSql + " and " + strFieldName + " = '" + txtCode.getText().trim() + "'";
        }

        if (((Location) cboLocation1.getSelectedItem()).getLocationId() != 0) {
            Location location = (Location) cboLocation1.getSelectedItem();
            strSql = strSql + " and p.location = " + location.getLocationId();
        }

        if (!((Appuser) cboUser.getSelectedItem()).getUserId().equals("000")) {
            Appuser user = (Appuser) cboUser.getSelectedItem();
            strSql = strSql + " and p.createdBy = '" + user.getUserId() + "'";
        }

        if (!((String) cboPayOption.getSelectedItem()).equals("All")) {
            String strPayOpt = (String) cboPayOption.getSelectedItem();
            strSql = strSql + " and p.payOpt = '" + strPayOpt + "'";
        }

        /*if (txtRemark.getText() != null && !txtRemark.getText().isEmpty()) {
            String strRemark = txtRemark.getText();
            strSql = strSql + " and p.remark = '" + strRemark + "'";
        }*/
        if (cboCusGroupFilter.getSelectedItem() instanceof CustomerGroup) {
            CustomerGroup cg = (CustomerGroup) cboCusGroupFilter.getSelectedItem();
            strSql = strSql + " and p.cusGroupId = '" + cg.getGroupId() + "'";
        }
        strSql = strSql + " and p.discm = 'C'";

        return strSql;
    }

    private void getCustomerList() {
        //UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }

        TraderSearchDialog dialog = new TraderSearchDialog(this,
                "Customer List", locationId);
        dialog.setTitle("Customer List");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            switch (source.toString()) {
                case "CalculateTotal":
                    calculateTotal();
                    break;
                case "Paid":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("Paid");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
                case "Discount":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("Discount");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
                case "FullPaid":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("FullPaid");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
                case "CustomerList":
                    model.removeAll();
                    Trader searchTrader = (Trader) selectObj;
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCode.setText(searchTrader.getStuCode());
                    } else {
                        txtCode.setText(searchTrader.getTraderId());
                    }
                    txtName.setText(searchTrader.getTraderName());
                    break;
            }
        }
    }

    private void assignData() {
        try {
            TraderPayHis tph = new TraderPayHis();
            VoucherPayment vp = new VoucherPayment();
            tph.setPayDate(vp.getPayDate());
            List<Customer> cus = dao.findAllHSQL("select o from Customer o where o.traderId='" + vp.getTraderId() + "'");
            tph.setTrader(cus.get(0));
            tph.setRemark(vp.getRemark());
            tph.setPaidAmtC(vp.getCurrentPaid());
            tph.setDiscount(vp.getCurrentDiscount());
            List<Currency> curr = dao.findAllHSQL("select o from Currency o where o.currencyCode='MMK'");
            tph.setCurrency(curr.get(0));
            tph.setExRate(1.0);
            tph.setPaidAmtP(vp.getCurrentPaid());
            List<Appuser> user = dao.findAllHSQL("select o from Appuser o where  o.userId='" + vp.getUserId() + "'");
            tph.setCreatedBy(user.get(0));
            tph.setPayOption("Cash");
            tph.setParentCurr(curr.get(0));
            tph.setPayDt(vp.getPayDate());
            PaymentVou pv = new PaymentVou();
            pv.setBalance(vp.getVouBalance());
            pv.setVouNo(vp.getVouNo());
            pv.setVouPaid(vp.getCurrentPaid());
            pv.setBalance(vp.getVouBalance());
            pv.setVouDate(vp.getTranDate());
            pv.setDiscount(vp.getDiscount());
            pv.setVouType("Sale");
            List<PaymentVou> listPV = new ArrayList();
            listPV.add(pv);
            tph.setListDetail(listPV);

            dao.save(tph);
        } catch (Exception ex) {
            log.error("assignData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            String strFieldName = "o.traderId";
            if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                strFieldName = "o.stuCode";
            }
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                String strSql;
                int locationId;
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
                cus = (Trader) dao.find(Trader.class, traderId);
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
    }

    private void getCustomer() {
        Trader cus = getTrader(txtCode.getText().trim().toUpperCase());
        if (cus == null) {
            txtCode.setText(null);
            txtName.setText(null);
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Invalid customer code.",
                    "Trader Code", JOptionPane.ERROR_MESSAGE);

        } else {
            selected("CustomerList", cus);
        }
    }

    private void initTable() {
        search();
        tblPayList.getTableHeader().setFont(Global.lableFont);
        tblPayList.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblPayList.getColumnModel().getColumn(1).setPreferredWidth(30);
        tblPayList.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblPayList.getColumnModel().getColumn(3).setPreferredWidth(40);

        tblPayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPayList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblPayList.getSelectedRow() >= 0) {
                    selectRow = tblPayList.convertRowIndexToModel(tblPayList.getSelectedRow());
                }
            }
        });
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectRow != -1) {
                VTraderPayment vtp = model.getSelectVou(selectRow);
                boolean canDelete = true;
                if (vtp != null) {
                    if (Util1.getPropValue("system.cuspayment.delete.date.check").equals("Y")) {
                        Date payDate = vtp.getPayDate();
                        Date currDate = DateUtil.toDate(DateUtil.getTodayDateStr());
                        if (payDate.before(currDate)) {
                            canDelete = false;
                        }
                    }

                    if (canDelete) {
                        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                                "Are you sure to delete?",
                                "Payment delete", JOptionPane.YES_NO_OPTION);
                        if (yes_no == 0) {
                            try {
                                TraderPayHis traderPayHis = (TraderPayHis) dao.find(TraderPayHis.class, vtp.getPayId());

                                if (traderPayHis.getPaymentId() != null) {
                                    try {
                                        dao.getPro("bkpayment", traderPayHis.getPaymentId().toString(),
                                                Global.loginUser.getUserId());
                                    } catch (Exception ex) {
                                        log.error("bkpayment : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                                    } finally {
                                        dao.close();
                                    }
                                }

                                traderPayHis.setDeleted(true);

                                dao.open();
                                dao.save(traderPayHis);

                                //For upload to account
                                uploadToAccount(traderPayHis);

                                model.remove(selectRow);
                                selectRow = -1;
                            } catch (Exception ex) {
                                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "You cannot delete previous days voucher.",
                                "Delete", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            /*if (!canEdit) {
                
            }*/

        }
    };

    private void uploadToAccount(TraderPayHis tph) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            if (!Global.mqConnection.isStatus()) {
                String mqUrl = Util1.getPropValue("system.mqserver.url");
                Global.mqConnection = new ActiveMQConnection(mqUrl);
            }
            if (Global.mqConnection != null) {
                if (Global.mqConnection.isStatus()) {
                    try {
                        ActiveMQConnection mq = Global.mqConnection;
                        MapMessage msg = mq.getMapMessageTemplate();
                        msg.setString("program", Global.programId);
                        msg.setString("entity", "PAYMENT");
                        msg.setInt("vouNo", tph.getPaymentId());
                        msg.setString("remark", tph.getRemark());
                        msg.setString("cusId", tph.getTrader().getAccountId());
                        msg.setString("payDate", DateUtil.toDateStr(tph.getPayDate(), "yyyy-MM-dd"));
                        msg.setBoolean("deleted", tph.isDeleted());
                        msg.setDouble("payment", tph.getPaidAmtP());
                        msg.setDouble("discount", tph.getDiscount());
                        msg.setString("currency", tph.getCurrency().getCurrencyAccId());
                        if (tph.getTrader().getTraderGroup() != null) {
                            msg.setString("sourceAccId", tph.getTrader().getTraderGroup().getAccountId());
                        } else {
                            msg.setString("sourceAccId", "-");
                        }
                        if (tph.getPayAccount() != null) {
                            msg.setString("account_id", tph.getPayAccount().getAccount());
                        } else {
                            msg.setString("account_id", "-");
                        }
                        msg.setString("queueName", "INVENTORY");
                        if (tph.getTrader() instanceof Customer) {
                            msg.setString("traderType", "CUS");
                        } else {
                            msg.setString("traderType", "SUP");
                        }
                        msg.setString("dept", "-");
                        if (tph.getTrader().getTraderGroup() != null) {
                            if (tph.getTrader().getTraderGroup().getDeptId() != null) {
                                if (!tph.getTrader().getTraderGroup().getDeptId().isEmpty()) {
                                    msg.setString("dept", tph.getTrader().getTraderGroup().getDeptId().trim());
                                }
                            }
                        }
                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + tph.getPaymentId() + " - " + ex);
                    }
                }
            }
        }
    }

    private void actionMapping() {
        //F8 event on tblSale
        tblPayList.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblPayList.getActionMap().put("F8-Action", actionItemDelete);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        cboPayOption = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        txtCode = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPayList = new javax.swing.JTable();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        butSearch = new javax.swing.JButton();
        cboLocation1 = new javax.swing.JComboBox<>();
        cboUser = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        cboCusGroupFilter = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        chkOverdue = new javax.swing.JCheckBox();
        butRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVouList = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtTotalPaid = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTotalDiscount = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        cboCusGroup = new javax.swing.JComboBox<>();
        cboLocation = new javax.swing.JComboBox<>();
        txtTotalBalance = new javax.swing.JFormattedTextField();
        lblRefresh = new javax.swing.JButton();

        txtFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("From"));
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtTo.setBorder(javax.swing.BorderFactory.createTitledBorder("To"));
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        cboPayOption.setFont(Global.textFont);
        cboPayOption.setBorder(javax.swing.BorderFactory.createTitledBorder("Pay Option"));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboPayOption, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cboPayOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtCode.setBorder(javax.swing.BorderFactory.createTitledBorder("Code"));
        txtCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCodeMouseClicked(evt);
            }
        });
        txtCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodeActionPerformed(evt);
            }
        });

        txtName.setFont(Global.textFont);
        txtName.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));
        txtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtNameMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tblPayList.setFont(Global.textFont);
        tblPayList.setModel(model);
        tblPayList.setRowHeight(23);
        jScrollPane2.setViewportView(tblPayList);

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText("Total : ");

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        cboLocation1.setFont(Global.textFont);
        cboLocation1.setBorder(javax.swing.BorderFactory.createTitledBorder("Location"));

        cboUser.setFont(Global.textFont);
        cboUser.setBorder(javax.swing.BorderFactory.createTitledBorder("User"));

        cboCusGroupFilter.setFont(Global.textFont);
        cboCusGroupFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Cus Group"));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboCusGroupFilter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboCusGroupFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(butSearch, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cboLocation1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboUser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(89, 89, 89)))
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(butSearch)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboLocation1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jLabel1.setText("Filter");

        txtFilter.setFont(Global.textFont);
        txtFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterActionPerformed(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        chkOverdue.setText("Overdue");
        chkOverdue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOverdueActionPerformed(evt);
            }
        });

        butRefresh.setText("Save");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        tblVouList.setFont(Global.textFont);
        tblVouList.setModel(tblPaymentEntry);
        tblVouList.setRowHeight(23);
        jScrollPane1.setViewportView(tblVouList);

        jLabel4.setText("Total Paid : ");

        txtTotalPaid.setEditable(false);
        txtTotalPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("Total Discount : ");

        txtTotalDiscount.setEditable(false);
        txtTotalDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Total Balance : ");

        cboCusGroup.setFont(Global.textFont);
        cboCusGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCusGroupActionPerformed(evt);
            }
        });

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        txtTotalBalance.setEditable(false);
        txtTotalBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lblRefresh.setText("Refresh");
        lblRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkOverdue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRefresh)
                        .addGap(2, 2, 2))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalPaid)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalDiscount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalBalance)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOverdue)
                    .addComponent(butRefresh)
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorter.setRowFilter(swrf);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void chkOverdueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOverdueActionPerformed
        sorter.setRowFilter(swrf);
        calculateTotal();
    }//GEN-LAST:event_chkOverdueActionPerformed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        tblPaymentEntry.saveAll();
        getData();
        calculateTotal();
        //butRefresh.setEnabled(false);
    }//GEN-LAST:event_butRefreshActionPerformed

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterActionPerformed

    private void cboCusGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCusGroupActionPerformed
        if (!bindStatus) {
            getData();
            calculateTotal();
        }
    }//GEN-LAST:event_cboCusGroupActionPerformed

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (!bindStatus) {
            getData();
            calculateTotal();
        }
    }//GEN-LAST:event_cboLocationActionPerformed

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

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void txtCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCodeMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCodeMouseClicked

    private void txtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtNameMouseClicked

    private void txtCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodeActionPerformed
        if (txtCode.getText() == null || txtCode.getText().isEmpty()) {
            txtName.setText(null);
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtCodeActionPerformed

    private void lblRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblRefreshActionPerformed
        getData();
        calculateTotal();
    }//GEN-LAST:event_lblRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butRefresh;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboCusGroup;
    private javax.swing.JComboBox<String> cboCusGroupFilter;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JComboBox<String> cboLocation1;
    private javax.swing.JComboBox<String> cboPayOption;
    private javax.swing.JComboBox<String> cboUser;
    private javax.swing.JCheckBox chkOverdue;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton lblRefresh;
    private javax.swing.JTable tblPayList;
    private javax.swing.JTable tblVouList;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtName;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalBalance;
    private javax.swing.JFormattedTextField txtTotalDiscount;
    private javax.swing.JFormattedTextField txtTotalPaid;
    // End of variables declaration//GEN-END:variables
}
