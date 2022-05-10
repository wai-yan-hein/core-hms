/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderPayAccount;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.helper.CurrencyTtl;
import com.cv.app.pharmacy.database.view.VTraderPayment;
import com.cv.app.pharmacy.ui.common.PayVouListTableModel;
import com.cv.app.pharmacy.ui.common.PaymentHisListTableModel;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.util.PharmacyUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.MapMessage;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author WSwe
 */
public class Payment extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(Payment.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private String cusSource = "";
    private Trader paymentTrader;
    private List<PaymentVou> listVou = new ArrayList();
    private List<TraderPayHis> listPayHis = new ArrayList();
    private List<CurrencyTtl> listCurrencyTtl = new ArrayList();
    private PayVouListTableModel tblPaidVouListModel = new PayVouListTableModel();
    private PaymentHisListTableModel tblPayListModel = new PaymentHisListTableModel();
    private TraderPayHis traderPayHis = new TraderPayHis();
    private boolean bindStatus = false;
    private TableRowSorter<TableModel> sorter;

    private String strPrvDate;
    private Object prvLocation;
    private Object prvPymet;
    private int mouseClick = 2;

    /**
     * Creates new form Payment
     */
    public Payment() {
        initComponents();
        initCombo();

        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        txtPayDate.setText(DateUtil.getTodayDateStr());
        initTextBoxAlign();
        tblPaidVouList.setModel(tblPaidVouListModel);
        assignPayOption();

        clearPayment();
        initTblPayList();
        initTblTotal();
        initTblPaidVou();
        //Remove table header.
        tblTotal.setTableHeader(null);
        sorter = new TableRowSorter(tblPayList.getModel());
        tblPayList.setRowSorter(sorter);
        chkVouUpdate.setSelected(true);

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

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        Object tmpObj;
        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            tmpObj = Global.loginLocation;
        } else {
            tmpObj = Util1.getDefaultValue("Location");
        }
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            cboLocation.setSelectedItem(tmpObj);
            int index = cboLocation.getSelectedIndex();
            if (index == -1) {
                if (cboLocation.getItemCount() > 0) {
                    cboLocation.setSelectedIndex(0);
                }
            }

            BindingUtil.BindCombo(cboLocation1, getLocationFilter());
            cboLocation1.setSelectedItem(tmpObj);
            index = cboLocation.getSelectedIndex();
            if (index == -1) {
                if (cboLocation1.getItemCount() > 0) {
                    cboLocation1.setSelectedIndex(0);
                }
            }
        } else {
            BindingUtil.BindCombo(cboLocation1, dao.findAll("Location"));
            BindingUtil.BindCombo(cboLocation, dao.findAll("Location",
                    new Location(0, "All")));
            cboLocation.setSelectedIndex(0);
        }
        BindingUtil.BindCombo(cboUser, dao.findAll("Appuser",
                new Appuser("000", "All")));
        BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
        BindingUtil.BindCombo(cboCurrencySearch, dao.findAll("Currency",
                new Currency("000", "All")));
        BindingUtil.BindCombo(cboPCurrency, dao.findAll("Currency"));
        BindingUtil.BindCombo(cboAccount,
                dao.findAllHSQL("select o from TraderPayAccount o where o.status = true order by o.payId"));

        cboUser.setSelectedIndex(0);
        cboCurrencySearch.setSelectedIndex(0);

        new ComBoBoxAutoComplete(cboLocation1);
        new ComBoBoxAutoComplete(cboLocation);
        new ComBoBoxAutoComplete(cboUser);
        new ComBoBoxAutoComplete(cboCurrency);
        new ComBoBoxAutoComplete(cboCurrencySearch);
        new ComBoBoxAutoComplete(cboPCurrency);
        new ComBoBoxAutoComplete(cboAccount);
        bindStatus = true;
    }// </editor-fold>

    private List getLocationFilter() {
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            return dao.findAllHSQL(
                    "select o from Location o where o.locationId in ("
                    + "select a.key.locationId from UserLocationMapping a "
                    + "where a.key.userId = '" + Global.loginUser.getUserId()
                    + "' and a.isAllowCusPayVou = true) order by o.locationName");
        } else {
            return dao.findAll("Location");
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                tblPaidVouListModel.removeList();

                switch (cusSource) {
                    case "cus1":
                        Trader searchTrader = (Trader) selectObj;

                        txtCusCode.setText(searchTrader.getTraderId());
                        txtCusName.setText(searchTrader.getTraderName());
                        break;
                    case "cus2":
                        //clearPayment();
                        paymentTrader = (Trader) selectObj;
                        txtCusCode1.setText(paymentTrader.getTraderId());
                        txtCusName1.setText(paymentTrader.getTraderName());
                        currencyChange();
                        break;
                }
                break;
            case "PayHis":
                lblStatus.setText("EDIT");
                traderPayHis = (TraderPayHis) dao.find(TraderPayHis.class,
                        ((VTraderPayment) selectObj).getPayId());
                tblPaidVouListModel.removeList();
                listVou.addAll(traderPayHis.getListDetail());
                cboLocation1.setSelectedItem(traderPayHis.getLocation());
                cboPayOpt.setSelectedItem(traderPayHis.getPayOption());
                cboCurrency.setSelectedItem(traderPayHis.getCurrency());
                cboPCurrency.setSelectedItem(traderPayHis.getParentCurr());
                txtPayDate.setValue(DateUtil.toDateStr(traderPayHis.getPayDate()));
                txtCusCode1.setText(traderPayHis.getTrader().getTraderId());
                txtCusName1.setText(traderPayHis.getTrader().getTraderName());
                txtRemark1.setText(traderPayHis.getRemark());
                txtPaidC.setValue(traderPayHis.getPaidAmtC());
                txtDiscount.setValue(traderPayHis.getDiscount());
                txtExRate.setValue(traderPayHis.getExRate());
                txtPaidP.setValue(traderPayHis.getPaidAmtP());
                cboAccount.setSelectedItem(traderPayHis.getPayAccount());
                tblPaidVouListModel.setListVou(listVou);
                dao.close();
                lock();
                break;
        }
    }

    private void getCustomerList() {
        //UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
        int locationId = -1;
        if (cusSource.equals("cus1")) {
            if (cboLocation.getSelectedItem() instanceof Location) {
                locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }
        } else if (cusSource.equals("cus2")) {
            if (cboLocation1.getSelectedItem() instanceof Location) {
                locationId = ((Location) cboLocation1.getSelectedItem()).getLocationId();
            }
        }

        TraderSearchDialog dialog = new TraderSearchDialog(this,
                "Customer List", locationId);
        dialog.setTitle("Customer List");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void clearPayment() {
        if (lblStatus.getText().equals("NEW")) {
            strPrvDate = txtPayDate.getText();
            prvLocation = cboLocation.getSelectedItem();
            prvPymet = cboPayOpt1.getSelectedItem();
        }
        paymentTrader = null;
        if (strPrvDate != null) {
            txtPayDate.setText(strPrvDate);
        } else {
            txtPayDate.setText(DateUtil.getTodayDateStr());
        }
        //cboLocation1.setSelectedItem(null);
        txtCusCode1.setText(null);
        txtCusName1.setText(null);
        txtRemark1.setText(null);
        txtLastPaidDate.setText(null);
        txtPrvBalance.setValue(null);
        txtRePurchase.setValue(null);
        txtReturnIn.setValue(null);
        txtPaidC.setValue(null);
        txtDiscount.setValue(null);
        Currency defaultCurrency = (Currency) dao.find(Currency.class,
                Util1.getPropValue("system.app.currency"));
        cboCurrency.setSelectedItem(defaultCurrency);
        cboPCurrency.setSelectedItem(defaultCurrency);
        cboAccount.setSelectedItem(null);
        txtExRate.setValue(null);
        txtPaidP.setValue(null);
        txtTtlPaid.setValue(null);
        txtLastBalance.setValue(null);

        traderPayHis = new TraderPayHis();
        tblPaidVouListModel.removeList();
        paymentTrader = null;
        lblStatus.setText("NEW");
        butSave.setEnabled(true);
        unLock();

        System.gc();
    }

    // <editor-fold defaultstate="collapsed" desc="initTextBoxAlign">
    private void initTextBoxAlign() {
        txtPrvBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtRePurchase.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtReturnIn.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaidC.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscount.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtExRate.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaidP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTtlPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtLastBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtPrvBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRePurchase.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtReturnIn.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaidC.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtExRate.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaidP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTtlPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtLastBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void calculateAmount() {
        double paid = 0;

        if (NumberUtil.NZero(txtExRate.getValue()) > 0) {
            if (((Currency) cboPCurrency.getSelectedItem()).getCurrencyCode().equals("MMK")) {
                paid = NumberUtil.NZero(txtPaidC.getValue())
                        * NumberUtil.NZero(txtExRate.getValue());
            } else {
                paid = NumberUtil.NZero(txtPaidC.getValue())
                        / NumberUtil.NZero(txtExRate.getValue());
            }
            //paid = paid + NumberUtil.NZero(txtDiscount.getValue());
        }

        txtPaidP.setValue(paid);
        txtTtlPaid.setValue(NumberUtil.NZero(txtRePurchase.getValue())
                + NumberUtil.NZero(txtReturnIn.getValue())
                + NumberUtil.NZero(txtPaidP.getValue()));
        txtLastBalance.setValue(NumberUtil.NZero(txtPrvBalance.getValue())
                - NumberUtil.NZero(txtTtlPaid.getValue()) - NumberUtil.NZero(txtDiscount.getValue()));

        if (lblStatus.getText().equals("NEW")) {
            tblPaidVouListModel.fillPyment(NumberUtil.NZero(txtTtlPaid.getValue()) + NumberUtil.NZero(txtDiscount.getValue()));
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        Date vouSaleDate = DateUtil.toDate(txtPayDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (paymentTrader == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No vendor selected.",
                    "Invalid vendor.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (cboLocation1.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Select payment location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (NumberUtil.NZero(txtPaidP.getValue()) == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No pay amount.",
                    "No payment make.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (cboPCurrency.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Select P-Currency.",
                    "Invalid Parent Currency.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (!tblPaidVouListModel.isValidEntry()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "You must fill total pay amount in voucher.",
                    "Invalid Vou Paid Amount.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (cboAccount.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Select Payment Type.",
                    "Invalid Vou Type.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            traderPayHis.setPayDate(DateUtil.toDateTime(txtPayDate.getText()));
            traderPayHis.setLocation((Location) cboLocation1.getSelectedItem());
            traderPayHis.setTrader(paymentTrader);
            traderPayHis.setRemark(txtRemark1.getText());
            traderPayHis.setPaidAmtC(NumberUtil.NZero(txtPaidC.getValue()));
            traderPayHis.setDiscount(NumberUtil.NZero(txtDiscount.getValue()));
            traderPayHis.setCurrency((Currency) cboCurrency.getSelectedItem());
            traderPayHis.setExRate(NumberUtil.NZero(txtExRate.getValue()));
            traderPayHis.setPaidAmtP(NumberUtil.NZero(txtPaidP.getValue()));
            traderPayHis.setListDetail(tblPaidVouListModel.getEntryVou());
            traderPayHis.setCreatedBy(Global.loginUser);
            traderPayHis.setPayOption((String) cboPayOpt1.getSelectedItem());
            traderPayHis.setParentCurr((Currency) cboPCurrency.getSelectedItem());
            traderPayHis.setPayDt(traderPayHis.getPayDate());
            traderPayHis.setIntgUpdStatus(null);
            if (cboAccount.getSelectedItem() != null) {
                traderPayHis.setPayAccount((TraderPayAccount) cboAccount.getSelectedItem());
            } else {
                traderPayHis.setPayAccount(null);
            }
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                if (traderPayHis.getPaymentId() != null) {
                    dao.getPro("bkpayment", traderPayHis.getPaymentId().toString(), Global.loginUser.getUserId());
                }

                dao.save(traderPayHis);

                //For upload to account
                uploadToAccount(traderPayHis);

                /*if(chkVouUpdate.isSelected()){
                 updateSaveVouTime(txtPayDate.getText(), traderPayHis.getTrader().getTraderId());
                 }*/
                clearPayment();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    /*
     private void updateSaveVouTime(String strPayDate, String traderId){
     String strSql = "select v from TraderPayHis v where v.payDate between '" +
     DateUtil.toDateTimeStrMYSQL(strPayDate) +
     "' and '" + DateUtil.toDateStrMYSQLEnd(strPayDate) + "'" +
     " and v.trader.traderId = '" + traderId + "'";
     List<TraderPayHis> listSH = dao.findAllHSQL(strSql);
        
     if(listSH != null){
     if(!listSH.isEmpty()){
     TraderPayHis sh = null;
                
     for(TraderPayHis tmpSH : listSH){
     if(sh == null){
     sh = tmpSH;
     }else{
     if(sh.getPayDt().before(tmpSH.getPayDt())){
     sh = tmpSH;
     }
     }
     }
                
     if(sh != null){
     String strDate = DateUtil.toDateStr(sh.getPayDt(), "dd/MM/yyyy");
     strSql = "update payment_his set pay_date = '" + 
     DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(strDate)) + 
     "' where payment_id = '" + sh.getPaymentId() + "'";
     dao.execSql(strSql);
     }
     }
     }
     }*/
    private void assignPayOption() {
        cboPayOpt1.removeAllItems();
        cboPayOpt1.addItem("Cash");
        cboPayOpt1.addItem("Credit Card");

        cboPayOpt.removeAllItems();
        cboPayOpt.addItem("All");
        cboPayOpt.addItem("Cash");
        cboPayOpt.addItem("Credit Card");
    }

    private void searchClear() {
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        txtCusCode.setText(null);
        txtCusName.setText(null);
        txtRemark.setText(null);

        cboUser.setSelectedIndex(0);
        cboLocation.setSelectedIndex(0);
        cboCurrencySearch.setSelectedIndex(0);
        cboPayOpt.setSelectedIndex(0);

        listCurrencyTtl.removeAll(listCurrencyTtl);
        listPayHis.removeAll(listPayHis);
        initTblPayList();
        initTblTotal();
    }

    private void search() {
        String strSql = getHSQL();

        try {
            dao.open();
            tblPayListModel.setListTraderPayHis(dao.findAllHSQL(strSql));
            dao.close();
            //initTblPayList();
            calcTotalAmount();
            dao.closeStatment();
        } catch (Exception ex) {
            log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private String getHSQL() {
        String strSql = "select distinct p from VTraderPayment p where p.payDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "' and p.deleted = false ";

        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            strSql = strSql + " and p.traderId = '" + txtCusCode.getText() + "'";
        }

        if (((Location) cboLocation.getSelectedItem()).getLocationId() != 0) {
            Location location = (Location) cboLocation.getSelectedItem();
            strSql = strSql + " and p.location = " + location.getLocationId();
        }

        if (!((Appuser) cboUser.getSelectedItem()).getUserId().equals("000")) {
            Appuser user = (Appuser) cboUser.getSelectedItem();
            strSql = strSql + " and p.createdBy = '" + user.getUserId() + "'";
        }

        if (!((Currency) cboCurrencySearch.getSelectedItem()).getCurrencyCode().equals("000")) {
            Currency currency = (Currency) cboCurrencySearch.getSelectedItem();
            strSql = strSql + " and p.currency = '"
                    + currency.getCurrencyCode() + "'";
        }

        if (!((String) cboPayOpt.getSelectedItem()).equals("All")) {
            String strPayOpt = (String) cboPayOpt.getSelectedItem();
            strSql = strSql + " and p.payOpt = '" + strPayOpt + "'";
        }

        if (txtRemark.getText() != null && !txtRemark.getText().isEmpty()) {
            String strRemark = txtRemark.getText();
            strSql = strSql + " and p.remark = '" + strRemark + "'";
        }

        if ((chkCustomer.isSelected() && !chkSupplier.isSelected())
                || !chkCustomer.isSelected() && chkSupplier.isSelected()) {
            if (chkCustomer.isSelected()) {
                strSql = strSql + " and p.discm = 'C'";
            } else if (chkSupplier.isSelected()) {
                strSql = strSql + " and p.discm = 'S'";
            }
        }

        return strSql;
    }

    private void initTblPayList() {
        /*
         * JTableBinding jTableBinding =
         * SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE,
         * listPayHis, tblPayList); JTableBinding.ColumnBinding columnBinding =
         * jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${payDate}"));
         * columnBinding.setColumnName("Date");
         * columnBinding.setColumnClass(Date.class);
         * columnBinding.setEditable(false);
         *
         * columnBinding =
         * jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${trader.traderName}"));
         * columnBinding.setColumnName("Trader");
         * columnBinding.setColumnClass(String.class);
         * columnBinding.setEditable(false);
         *
         * columnBinding =
         * jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${paidAmtC}"));
         * columnBinding.setColumnName("C-Amount");
         * columnBinding.setColumnClass(Double.class);
         * columnBinding.setEditable(false);
         *
         * columnBinding =
         * jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${currency.currencyName}"));
         * columnBinding.setColumnName("Currency");
         * columnBinding.setColumnClass(String.class);
         * columnBinding.setEditable(false);
         *
         * columnBinding =
         * jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${paidAmtP}"));
         * columnBinding.setColumnName("P-Amount");
         * columnBinding.setColumnClass(Double.class);
         * columnBinding.setEditable(false);
         *
         * jTableBinding.bind();
         */

        tblPayList.getTableHeader().setFont(Global.lableFont);
        tblPayList.getColumnModel().getColumn(0).setPreferredWidth(25);
        tblPayList.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblPayList.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblPayList.getColumnModel().getColumn(3).setPreferredWidth(15);
        tblPayList.getColumnModel().getColumn(4).setPreferredWidth(50);

        tblPayList.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    private void calcTotalAmount() {
        String strSQL = "select c.cur_name, sum(paid_amtc) cur_ttl_paid_amtc, "
                + "sum(paid_amtp) cur_ttl_paid_amtp from v_trader_payment ph, currency c "
                + "where ph.currency_id = c.cur_code and ph.deleted = false and"
                + " ph.pay_date between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";

        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            strSQL = strSQL + " and ph.trader_id = '" + txtCusCode.getText() + "'";
        }

        if (((Location) cboLocation.getSelectedItem()).getLocationId() != 0) {
            Location location = (Location) cboLocation.getSelectedItem();
            strSQL = strSQL + " and ph.location_id = " + location.getLocationId();
        }

        if (!((Appuser) cboUser.getSelectedItem()).getUserId().equals("000")) {
            Appuser user = (Appuser) cboUser.getSelectedItem();
            strSQL = strSQL + " and ph.created_by = '" + user.getUserId() + "'";
        }

        if (!((Currency) cboCurrencySearch.getSelectedItem()).getCurrencyCode().equals("000")) {
            Currency currency = (Currency) cboCurrencySearch.getSelectedItem();
            strSQL = strSQL + " and ph.currency_id = '"
                    + currency.getCurrencyCode() + "'";
        }

        if (!((String) cboPayOpt.getSelectedItem()).equals("All")) {
            String strPayOpt = (String) cboPayOpt.getSelectedItem();
            strSQL = strSQL + " and ph.pay_option = '" + strPayOpt + "'";
        }

        if (txtRemark.getText() != null && !txtRemark.getText().isEmpty()) {
            String strRemark = txtRemark.getText();
            strSQL = strSQL + " and p.remark like '" + strRemark + "%'";
        }

        if ((chkCustomer.isSelected() && !chkSupplier.isSelected())
                || !chkCustomer.isSelected() && chkSupplier.isSelected()) {
            if (chkCustomer.isSelected()) {
                strSQL = strSQL + " and ph.discriminator = 'C'";
            } else if (chkSupplier.isSelected()) {
                strSQL = strSQL + " and ph.discriminator = 'S'";
            }
        }

        strSQL = strSQL + " group by c.cur_name";
        listCurrencyTtl.removeAll(listCurrencyTtl);

        try {
            ResultSet resultSet = dao.execSQL(strSQL);

            if (resultSet != null) {
                while (resultSet.next()) {
                    listCurrencyTtl.add(new CurrencyTtl(resultSet.getString("cur_name"),
                            resultSet.getDouble("cur_ttl_paid_amtc")));
                    //System.out.println(resultSet.getString("cur_ttl_paid_amtp"));
                }
            }
        } catch (SQLException ex) {
            log.error("calcTotalAmount : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        initTblTotal();
    }

    private void initTblTotal() {
        JTableBinding jTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                listCurrencyTtl, tblTotal);
        JTableBinding.ColumnBinding columnBinding
                = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${currency}"));
        columnBinding.setColumnName("Currency");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);

        columnBinding
                = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ttlPaid}"));
        columnBinding.setColumnName("Total Paid");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);

        jTableBinding.bind();

        tblTotal.getColumnModel().getColumn(0).setPreferredWidth(25);
        tblTotal.getColumnModel().getColumn(1).setPreferredWidth(50);
    }

    private void initTblPaidVou() {
        tblPaidVouList.getTableHeader().setFont(Global.lableFont);
        tblPaidVouList.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblPaidVouList.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblPaidVouList.getColumnModel().getColumn(2).setPreferredWidth(10);
        tblPaidVouList.getColumnModel().getColumn(3).setPreferredWidth(10);
        tblPaidVouList.getColumnModel().getColumn(4).setPreferredWidth(30);
        tblPaidVouList.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblPaidVouList.getColumnModel().getColumn(6).setPreferredWidth(30);
    }

    private void lock() {
        cboLocation1.setEnabled(false);
        cboPayOpt1.setEnabled(false);
        txtCusCode1.setEnabled(false);
        txtRemark1.setEnabled(false);
        butSave.setEnabled(false);
        butDelete.setEnabled(true);
        txtPaidC.setEnabled(false);
        txtDiscount.setEnabled(false);
        cboCurrency.setEnabled(false);
        txtExRate.setEnabled(false);
        tblPaidVouListModel.setEditable(false);
        cboAccount.setEnabled(false);
    }

    private void unLock() {
        cboLocation1.setEnabled(true);
        cboPayOpt1.setEnabled(true);
        txtCusCode1.setEnabled(true);
        txtRemark1.setEnabled(true);
        butSave.setEnabled(true);
        butDelete.setEnabled(false);
        txtPaidC.setEnabled(true);
        txtDiscount.setEnabled(true);
        cboCurrency.setEnabled(true);
        txtExRate.setEnabled(true);
        tblPaidVouListModel.setEditable(true);
        cboAccount.setEnabled(true);
    }

    private void delete() {
        if (traderPayHis.getSaleVou() == null) {
            Date vouSaleDate = DateUtil.toDate(txtPayDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                    "Are you sure to delete?",
                    "Sale item delete", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
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
                traderPayHis.setIntgUpdStatus(null);

                try {
                    dao.open();
                    dao.save(traderPayHis);
                    String strSql = "update sale_his set payment_id = null, payment_amt = null "
                            + "where payment_id = " + traderPayHis.getPaymentId().toString();
                    dao.execSql(strSql);
                    //For upload to account
                    uploadToAccount(traderPayHis);

                    clearPayment();
                } catch (Exception ex) {
                    log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "This payment is already add to sale voucher '"
                    + traderPayHis.getSaleVou() + "'. You cannot delete.",
                    "Used Payment.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void currencyChange() {
        Currency currency = (Currency) cboCurrency.getSelectedItem();
        Currency currency1 = (Currency) cboPCurrency.getSelectedItem();

        if (currency != null & currency1 != null) {
            if (currency.getCurrencyCode().equals(currency1.getCurrencyCode())) {
                txtExRate.setValue(1.0);
                txtExRate.setEditable(false);
            } else {
                txtExRate.setEditable(true);
            }
        }

        paymentInfo();
        calculateAmount();
    }

    private void paymentInfo() {
        ResultSet resultSet = null;

        try {
            String strTrdOpt;

            if (paymentTrader != null) {

                if (paymentTrader.getTraderId().substring(0, 3).equals("SUP")) {
                    strTrdOpt = "SUP";
                    lblRePurchase.setText("Re-Sell");
                    lblReturnIn.setText("Return Out");
                } else {
                    strTrdOpt = "CUS";
                    lblRePurchase.setText("Re-Purchase");
                    lblReturnIn.setText("Return In");
                }

                listVou.removeAll(listVou);
                String appCurr = Util1.getPropValue("system.app.currency");
                if (cboPCurrency.getSelectedItem() != null) {
                    appCurr = ((Currency) cboPCurrency.getSelectedItem()).getCurrencyCode();
                }

                resultSet = dao.getPro("GET_PAYMENT_INFO",
                        paymentTrader.getTraderId(), strTrdOpt,
                        //DateUtil.getTodayDateStrMYSQL(), 
                        //DateUtil.toDateStrMYSQL(txtPayDate.getText()),
                        DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtPayDate.getText())),
                        appCurr);
                String strOpDate = null;
                double opBalance = 0;
                Date opDate = null;

                if (resultSet != null) {
                    while (resultSet.next()) {
                        txtLastPaidDate.setValue(resultSet.getDate("VAR_LAST_PAY_DATE"));
                        txtPrvBalance.setValue(resultSet.getDouble("VAR_LAST_BALANCE") + resultSet.getDouble("VAR_RE_RETIN"));
                        txtRePurchase.setValue(resultSet.getDouble("VAR_RE_PUR"));
                        txtReturnIn.setValue(resultSet.getDouble("VAR_RE_RETIN"));
                        strOpDate = resultSet.getString("VAR_OP_DATE");
                        opDate = resultSet.getDate("VAR_OP_DATE");
                        opBalance = resultSet.getDouble("var_op_amt");
                    }
                }

                /*if(opBalance > 0){
                 listVou.add(new PaymentVou(paymentTrader.getTraderId() + "-" + strCurrency,
                 opBalance, "Opening", DateUtil.toDateStr(opDate),
                 "", opBalance));
                 }*/
                //if (paymentTrader.getPayMethod().getMethodId() == 1) {
                resultSet = dao.getPro("GET_UNPAID_VOU", paymentTrader.getTraderId(),
                        strTrdOpt, strOpDate,
                        //DateUtil.getTodayDateStrMYSQL(),
                        DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtPayDate.getText())),
                        appCurr, "1900-01-01", "1900-01-01");
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
                }

                tblPaidVouListModel.setListVou(listVou);
                //}

                calculateAmount();
            }
        } catch (Exception ex) {
            log.error("paymentInfo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception ex1) {

            }
            dao.closeStatment();
        }
    }

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
                        msg.setString("type", "ADD");
                        /*msg.setString("remark", tph.getRemark());
                        msg.setString("cusId", tph.getTrader().getAccountId());
                        msg.setString("payDate", DateUtil.toDateStr(tph.getPayDate(), "yyyy-MM-dd"));
                        msg.setBoolean("deleted", tph.isDeleted());
                        msg.setDouble("payment", tph.getPaidAmtP());
                        msg.setDouble("discount", tph.getDiscount());
                        msg.setString("currency", tph.getCurrency().getCurrencyAccId());
                         */
 /*if (tph.getTrader().getTraderGroup() != null) {
                            msg.setString("sourceAccId", tph.getTrader().getTraderGroup().getAccountId());
                        } else {
                            msg.setString("sourceAccId", "-");
                        }*/
 /*if (tph.getPayAccount() != null) {
                            msg.setString("account_id", tph.getPayAccount().getAccountId());
                        } else {
                            msg.setString("account_id", tph.getTrader().getTraderGroup().getAccountId());
                        }
                        msg.setString("queueName", "INVENTORY");
                        if (tph.getTrader().getTraderId().contains("CUS")) {
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
                        }*/
                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + tph.getPaymentId() + " - " + ex);
                    }
                }
            }
        }
    }

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            String prefix = traderId.toUpperCase().substring(0, 3);
            if (!prefix.contains("SUP")) {
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    if (!prefix.contains("CUS")) {
                        traderId = "CUS" + traderId;
                    }
                }
            }
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                int locationId = -1;
                if (cusSource.equals("cus2")) {
                    if (cboLocation1.getSelectedItem() instanceof Location) {
                        locationId = ((Location) cboLocation1.getSelectedItem()).getLocationId();
                    }
                } else if (cusSource.equals("cus1")) {
                    if (cboLocation.getSelectedItem() instanceof Location) {
                        locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                    }
                }

                List<Trader> listTrader = dao.findAllHSQL("select o from Trader o where "
                        + "o.active = true and o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ") and o.traderId = '" + traderId + "' order by o.traderName");
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        searchPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cboUser = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboCurrencySearch = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPayList = new javax.swing.JTable();
        butSearch = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cboPayOpt = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTotal = new javax.swing.JTable();
        butClearSearch = new javax.swing.JButton();
        chkCustomer = new javax.swing.JCheckBox();
        chkSupplier = new javax.swing.JCheckBox();
        entryPane = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtPayDate = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        cboLocation1 = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        cboPayOpt1 = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        txtCusCode1 = new javax.swing.JTextField();
        txtCusName1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtRemark1 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        txtLastPaidDate = new javax.swing.JFormattedTextField();
        txtPrvBalance = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtRePurchase = new javax.swing.JFormattedTextField();
        lblRePurchase = new javax.swing.JLabel();
        lblReturnIn = new javax.swing.JLabel();
        txtReturnIn = new javax.swing.JFormattedTextField();
        txtPaidP = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txtPaidC = new javax.swing.JFormattedTextField();
        cboCurrency = new javax.swing.JComboBox();
        txtTtlPaid = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtExRate = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPaidVouList = new javax.swing.JTable();
        butSave = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        txtLastBalance = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        butDelete = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cboPCurrency = new javax.swing.JComboBox();
        chkVouUpdate = new javax.swing.JCheckBox();
        txtDiscount = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cboAccount = new javax.swing.JComboBox<>();

        jSplitPane1.setDividerLocation(500);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From ");

        txtFrom.setEditable(false);
        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To ");

        txtTo.setEditable(false);
        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location ");

        cboLocation.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("User ");

        cboUser.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Currency");

        cboCurrencySearch.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Customer ");

        txtCusCode.setFont(Global.textFont);
        txtCusCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusCodeMouseClicked(evt);
            }
        });
        txtCusCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusCodeActionPerformed(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.setFont(Global.textFont);
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Remark");

        txtRemark.setFont(Global.textFont);

        tblPayList.setFont(Global.textFont);
        tblPayList.setModel(tblPayListModel);
        tblPayList.setRowHeight(23);
        tblPayList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPayListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPayList);

        butSearch.setFont(Global.textFont);
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Pay-Opt");

        cboPayOpt.setFont(Global.textFont);

        tblTotal.setFont(Global.textFont);
        tblTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblTotal.setRowHeight(23);
        jScrollPane3.setViewportView(tblTotal);

        butClearSearch.setFont(Global.textFont);
        butClearSearch.setText("Clear");
        butClearSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearSearchActionPerformed(evt);
            }
        });

        chkCustomer.setFont(Global.lableFont);
        chkCustomer.setText("Customer");

        chkSupplier.setFont(Global.lableFont);
        chkSupplier.setText("Supplier");

        javax.swing.GroupLayout searchPaneLayout = new javax.swing.GroupLayout(searchPane);
        searchPane.setLayout(searchPaneLayout);
        searchPaneLayout.setHorizontalGroup(
            searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(searchPaneLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark))
                    .addGroup(searchPaneLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(10, 10, 10)
                        .addComponent(cboUser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(23, 23, 23)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrencySearch, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboPayOpt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(searchPaneLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCusName))
                    .addGroup(searchPaneLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFrom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTo)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPaneLayout.createSequentialGroup()
                        .addComponent(chkCustomer)
                        .addGap(18, 18, 18)
                        .addComponent(chkSupplier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClearSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        searchPaneLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClearSearch, butSearch});

        searchPaneLayout.setVerticalGroup(
            searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cboCurrencySearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(cboPayOpt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butClearSearch)
                    .addComponent(butSearch)
                    .addComponent(chkCustomer)
                    .addComponent(chkSupplier))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        searchPaneLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {butClearSearch, butSearch});

        jSplitPane1.setLeftComponent(searchPane);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Pay Date ");

        txtPayDate.setEditable(false);
        txtPayDate.setFont(Global.textFont);
        txtPayDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPayDateMouseClicked(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Location ");

        cboLocation1.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Pay-Opt");

        cboPayOpt1.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Customer ");

        txtCusCode1.setFont(Global.textFont);
        txtCusCode1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusCode1MouseClicked(evt);
            }
        });
        txtCusCode1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusCode1ActionPerformed(evt);
            }
        });

        txtCusName1.setEditable(false);
        txtCusName1.setFont(Global.textFont);
        txtCusName1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusName1MouseClicked(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Remark");

        txtRemark1.setFont(Global.textFont);

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Last Paid Date ");

        txtLastPaidDate.setEditable(false);
        txtLastPaidDate.setFont(Global.textFont);

        txtPrvBalance.setEditable(false);
        txtPrvBalance.setFont(Global.textFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Prv-Balance");

        txtRePurchase.setEditable(false);
        txtRePurchase.setFont(Global.textFont);

        lblRePurchase.setFont(Global.lableFont);
        lblRePurchase.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRePurchase.setText("Re-Purchase ");

        lblReturnIn.setFont(Global.lableFont);
        lblReturnIn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblReturnIn.setText("Return In ");

        txtReturnIn.setEditable(false);
        txtReturnIn.setFont(Global.textFont);

        txtPaidP.setEditable(false);
        txtPaidP.setFont(Global.textFont);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Pay Amount ");

        txtPaidC.setFont(Global.textFont);
        txtPaidC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPaidCFocusLost(evt);
            }
        });
        txtPaidC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaidCActionPerformed(evt);
            }
        });

        cboCurrency.setFont(Global.textFont);
        cboCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCurrencyActionPerformed(evt);
            }
        });

        txtTtlPaid.setEditable(false);
        txtTtlPaid.setFont(Global.textFont);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Total Paid ");

        txtExRate.setFont(Global.textFont);
        txtExRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtExRateActionPerformed(evt);
            }
        });
        txtExRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtExRateFocusLost(evt);
            }
        });

        tblPaidVouList.setFont(Global.textFont);
        tblPaidVouList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblPaidVouList.setRowHeight(23);
        tblPaidVouList.setVerifyInputWhenFocusTarget(false);
        jScrollPane2.setViewportView(tblPaidVouList);

        butSave.setFont(Global.lableFont);
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butClear.setFont(Global.lableFont);
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        txtLastBalance.setEditable(false);
        txtLastBalance.setFont(Global.textFont);

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Last Balance ");

        lblStatus.setText("NEW");

        butDelete.setFont(Global.lableFont);
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("P-Currency ");

        cboPCurrency.setFont(Global.textFont);
        cboPCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPCurrencyActionPerformed(evt);
            }
        });

        chkVouUpdate.setFont(Global.lableFont);
        chkVouUpdate.setText("Update To Vou");
        chkVouUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVouUpdateActionPerformed(evt);
            }
        });

        txtDiscount.setFont(Global.textFont);
        txtDiscount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscountFocusLost(evt);
            }
        });
        txtDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscountActionPerformed(evt);
            }
        });

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Discount");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Pyament Type");

        cboAccount.setFont(Global.textFont);

        javax.swing.GroupLayout entryPaneLayout = new javax.swing.GroupLayout(entryPane);
        entryPane.setLayout(entryPaneLayout);
        entryPaneLayout.setHorizontalGroup(
            entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entryPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(entryPaneLayout.createSequentialGroup()
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboPCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2)
                    .addComponent(jSeparator1)
                    .addGroup(entryPaneLayout.createSequentialGroup()
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkVouUpdate)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLastBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                    .addGroup(entryPaneLayout.createSequentialGroup()
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(entryPaneLayout.createSequentialGroup()
                                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblRePurchase)
                                    .addComponent(lblReturnIn)
                                    .addGroup(entryPaneLayout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtLastPaidDate)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel16))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, entryPaneLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, entryPaneLayout.createSequentialGroup()
                                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel19)
                                            .addComponent(jLabel22))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(entryPaneLayout.createSequentialGroup()
                                                .addComponent(txtPaidC, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtExRate, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addGap(18, 18, 18)
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtReturnIn)
                            .addComponent(txtPrvBalance, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRePurchase, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTtlPaid, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPaidP)))
                    .addGroup(entryPaneLayout.createSequentialGroup()
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, entryPaneLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtPayDate))
                            .addGroup(entryPaneLayout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addComponent(txtCusCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(entryPaneLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(cboLocation1, 0, 171, Short.MAX_VALUE))
                            .addComponent(txtCusName1))
                        .addGap(18, 18, 18)
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(entryPaneLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(cboPayOpt1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(entryPaneLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboAccount, 0, 63, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        entryPaneLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        entryPaneLayout.setVerticalGroup(
            entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entryPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtPayDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(cboLocation1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(cboPayOpt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtCusCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(cboAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtRemark1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(cboPCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtLastPaidDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrvBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(entryPaneLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRePurchase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRePurchase))
                        .addGap(18, 18, 18)
                        .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblReturnIn)
                            .addComponent(txtReturnIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)))
                    .addGroup(entryPaneLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPaidP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtPaidC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtExRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(entryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSave)
                    .addComponent(butClear)
                    .addComponent(txtLastBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(butDelete)
                    .addComponent(chkVouUpdate))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(entryPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
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

    private void txtPayDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPayDateMouseClicked
        if (evt.getClickCount() == mouseClick && !lblStatus.getText().equals("EDIT")) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtPayDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtPayDateMouseClicked

    private void txtCusCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusCodeMouseClicked
        if (evt.getClickCount() == 2) {
            cusSource = "cus1";
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusCodeMouseClicked

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            cusSource = "cus1";
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void txtCusCode1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusCode1MouseClicked
        if (evt.getClickCount() == 2) {
            cusSource = "cus2";
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusCode1MouseClicked

    private void txtCusName1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusName1MouseClicked
        if (evt.getClickCount() == mouseClick) {
            cusSource = "cus2";
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusName1MouseClicked

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        if (bindStatus) {
            currencyChange();
        }
    }//GEN-LAST:event_cboCurrencyActionPerformed

    private void txtPaidCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaidCActionPerformed
        calculateAmount();
    }//GEN-LAST:event_txtPaidCActionPerformed

    private void txtPaidCFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaidCFocusLost
        calculateAmount();
    }//GEN-LAST:event_txtPaidCFocusLost

    private void txtExRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtExRateActionPerformed
        calculateAmount();
    }//GEN-LAST:event_txtExRateActionPerformed

    private void txtExRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtExRateFocusLost
        calculateAmount();
    }//GEN-LAST:event_txtExRateFocusLost

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clearPayment();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearSearchActionPerformed
        searchClear();
    }//GEN-LAST:event_butClearSearchActionPerformed

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void tblPayListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPayListMouseClicked
        int row = tblPayList.getSelectedRow();
        if (evt.getClickCount() == 2 && row >= 0) {
            selected("PayHis", tblPayListModel.getSelectVou(tblPayList.convertRowIndexToModel(row)));
        }
    }//GEN-LAST:event_tblPayListMouseClicked

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

  private void cboPCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPCurrencyActionPerformed
      if (bindStatus) {
          txtPaidC.setValue(null);
          txtDiscount.setValue(null);
          txtExRate.setValue(null);
          currencyChange();
      }
  }//GEN-LAST:event_cboPCurrencyActionPerformed

  private void txtCusCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusCodeActionPerformed
      if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
          txtCusName.setText(null);
      } else {
          Trader cus = getTrader(txtCusCode.getText().trim().toUpperCase());
          if (cus == null) {
              txtCusCode.setText(null);
              txtCusName.setText(null);

              JOptionPane.showMessageDialog(Util1.getParent(),
                      "Invalid customer code.",
                      "Trader Code", JOptionPane.ERROR_MESSAGE);
          } else {
              cusSource = "cus1";
              selected("CustomerList", cus);
          }
      }
  }//GEN-LAST:event_txtCusCodeActionPerformed

  private void txtCusCode1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusCode1ActionPerformed
      if (txtCusCode1.getText() == null || txtCusCode1.getText().isEmpty()) {
          txtCusName1.setText(null);
      } else {
          cusSource = "cus2";
          Trader cus = getTrader(txtCusCode1.getText().trim().toUpperCase());
          if (cus == null) {
              txtCusCode1.setText(null);
              txtCusName1.setText(null);

              JOptionPane.showMessageDialog(Util1.getParent(),
                      "Invalid customer code.",
                      "Trader Code", JOptionPane.ERROR_MESSAGE);
          } else {
              selected("CustomerList", cus);
          }
      }
  }//GEN-LAST:event_txtCusCode1ActionPerformed

    private void chkVouUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVouUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkVouUpdateActionPerformed

    private void txtDiscountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountFocusLost
        // TODO add your handling code here:
        calculateAmount();
    }//GEN-LAST:event_txtDiscountFocusLost

    private void txtDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountActionPerformed
        // TODO add your handling code here:
        calculateAmount();
    }//GEN-LAST:event_txtDiscountActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butClearSearch;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboAccount;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboCurrencySearch;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboLocation1;
    private javax.swing.JComboBox cboPCurrency;
    private javax.swing.JComboBox cboPayOpt;
    private javax.swing.JComboBox cboPayOpt1;
    private javax.swing.JComboBox cboUser;
    private javax.swing.JCheckBox chkCustomer;
    private javax.swing.JCheckBox chkSupplier;
    private javax.swing.JCheckBox chkVouUpdate;
    private javax.swing.JPanel entryPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblRePurchase;
    private javax.swing.JLabel lblReturnIn;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel searchPane;
    private javax.swing.JTable tblPaidVouList;
    private javax.swing.JTable tblPayList;
    private javax.swing.JTable tblTotal;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusCode1;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCusName1;
    private javax.swing.JFormattedTextField txtDiscount;
    private javax.swing.JFormattedTextField txtExRate;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtLastBalance;
    private javax.swing.JFormattedTextField txtLastPaidDate;
    private javax.swing.JFormattedTextField txtPaidC;
    private javax.swing.JFormattedTextField txtPaidP;
    private javax.swing.JFormattedTextField txtPayDate;
    private javax.swing.JFormattedTextField txtPrvBalance;
    private javax.swing.JFormattedTextField txtRePurchase;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtRemark1;
    private javax.swing.JFormattedTextField txtReturnIn;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTtlPaid;
    // End of variables declaration//GEN-END:variables
}
