/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.ui.common.*;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderPayAccount;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.helper.VoucherPayment;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.apache.log4j.Logger;

/**
 *
 * @author lenovo
 */
public class SPaymentEntryTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDTableModel.class.getName());
    private List<VoucherPayment> listVP = new ArrayList();
    private final String[] columnNames = {"Vou Date", "Vou No.", "Ref No.","Sup-No", "Sup-Name",
        "Due Date", "Ttl Overdue", "Currency", "Vou Total", "Ttl Prv Paid", "Pay Date", "Paid",
        "Discount", "Vou Balance", "Full Paid"};
    // private AbstractDataAccess dao;
    private SelectionObserver observer;
    private final AbstractDataAccess dao = Global.dao;
    private JComboBox cboPayment;

    public JComboBox getCboPayment() {
        return cboPayment;
    }

    public void setCboPayment(JComboBox cboPayment) {
        this.cboPayment = cboPayment;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (listVP == null) {
            return false;
        }

        if (listVP.isEmpty()) {
            return false;
        }

        if (listVP.get(row).getTranId() != null) {
            if (listVP.get(row).getTranId() == -1) {
                return false;
            }
        }

        VoucherPayment record = listVP.get(row);
        switch (column) {
            case 10:
                return true;
            case 11:
                return true;
            case 12:
                return record.getCurrentPaid() == null;
            case 13:
                return record.getCurrentDiscount() == null;
            case 14:
                return !record.isIsFullPaid();
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0://Vou Date
            case 5://Due Date
                return Date.class;
            case 6://Ttl Overdue
                return Integer.class;
            case 7://Currency
                return String.class;
            case 8://Vou Total
            case 9://Ttl Paid
                return Double.class;
            case 10://Pay Date
                return String.class;
            case 11://Paid
            case 12://Discount
            case 13://Vou Balance
                return Double.class;
            case 14://Full Paid
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVP == null) {
            return null;
        }

        if (listVP.isEmpty()) {
            return null;
        }

        try {
            VoucherPayment record = listVP.get(row);

            switch (column) {
                case 0: //Vou Date
                    return record.getTranDate();
                case 1: //Vou No.
                    return record.getVouNo();
                case 2: //Ref No
                    return record.getRefNo();
                case 3: //Cus-No
                    return record.getTraderId();
                case 4: //Cus-Name
                    return record.getTraderName();
                case 5: //Due Date
                    return record.getDueDate();
                case 6: //Ttl Overdue
                    return record.getTtlOverdue();
                case 7: //Currency
                    return record.getCurrency();
                case 8: //Vou Total
                    return record.getVouTotal();
                case 9: //Ttl Paid
                    return record.getTtlPaid();
                case 10: //Pay Date
                    return DateUtil.toDateStr(record.getPayDate());
                case 11: //Paid
                    return record.getCurrentPaid();
                case 12: //Discount
                    return record.getCurrentDiscount();
                case 13: //Vou Balance
                    return record.getVouBalance();
                case 14: //Full Paid
                    return record.isIsFullPaid();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listVP == null) {
            return;
        }

        VoucherPayment record = listVP.get(row);
        switch (column) {
            case 10://Pay Date
                if (DateUtil.isValidDate(value)) {
                    record.setPayDate(DateUtil.toDate(value));
                }
                break;
            case 11://Paid
                record.setCurrentPaid(NumberUtil.NZero(value));
                record.setListIndex(row);
                record.setTranId(-1);
                break;
            case 12://Discount
                record.setCurrentDiscount(NumberUtil.NZero(value));
                record.setListIndex(row);
                record.setTranId(-1);
                break;
            case 14://Full Paid
                if (record.getPayDate() == null) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Please enter pay date.",
                            "Invalid Pay Date.", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (value != null) {
                        Boolean fullPaid = (Boolean) value;
                        record.setIsFullPaid(fullPaid);
                        if (fullPaid) {
                            record.setCurrentPaid(NumberUtil.NZero(record.getVouBalance())
                                    - NumberUtil.NZero(record.getCurrentDiscount()));
                            //record.setVouBalance(0.0);
                            record.setCurrentDiscount(0.0);

                        } else {
                            record.setCurrentDiscount(0.0);
                            record.setCurrentPaid(0.0);
                            record.setVouBalance(NumberUtil.NZero(record.getVouTotal())
                                    - NumberUtil.NZero(record.getTtlPaid()));
                        }
                    }

                    record.setListIndex(row);
                    record.setTranId(-1);
                    fireTableCellUpdated(row, 9);
                    fireTableCellUpdated(row, 10);
                }
                break;

        }

        Save(record);
        calculateBalance(record);
        fireTableRowsUpdated(row, row);
    }

    private void Save(VoucherPayment vp) {
        if (vp.isIsFullPaid() || NumberUtil.NZero(vp.getCurrentPaid()) != 0
                || NumberUtil.NZero(vp.getCurrentDiscount()) != 0) {
            if (vp.getPayDate() == null) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Please enter pay date.",
                        "Invalid Pay Date.", JOptionPane.ERROR_MESSAGE);
                vp.setIsFullPaid(false);
                return;
            }
            Date vouTranDate = vp.getPayDate();
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouTranDate.before(lockDate) || vouTranDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ". Data is not saved.",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (NumberUtil.NZero(vp.getCurrentPaid()) == 0 && NumberUtil.NZero(vp.getDiscount()) == 0) {
                return;
            }

            try {
                TraderPayHis tph = new TraderPayHis();
                vp.setRemark("FullPaid");
                if (vp.getPayDate() == null) {
                    vp.setPayDate(new Date());
                }
                vp.setUserId(Global.loginUser.getUserId());
                tph.setPayDate(vp.getPayDate());
                List<Trader> cus = dao.findAllHSQL("select o from Trader o where o.traderId='" + vp.getTraderId() + "'");
                if (!cus.isEmpty()) {
                    tph.setTrader(cus.get(0));
                }
                tph.setRemark(vp.getRemark());
                tph.setPaidAmtC(vp.getCurrentPaid());
                tph.setDiscount(vp.getCurrentDiscount());
                //String appCurr = Util1.getPropValue("system.app.currency");
                String appCurr = vp.getCurrency();
                List<Currency> curr = dao.findAllHSQL("Select o from Currency o where o.currencyCode='" + appCurr + "'");
                tph.setCurrency(curr.get(0));
                //tph.setCurrency(curr.getCurrencyCode());
                tph.setExRate(1.0);
                tph.setPaidAmtP(vp.getCurrentPaid());
                // Object user = dao.find(Appuser.class, vp.getUserId());
                List<Appuser> user = dao.findAllHSQL("select o from Appuser o where  o.userId='" + vp.getUserId() + "'");
                tph.setCreatedBy(user.get(0));
                tph.setPayOption("Cash");
                tph.setParentCurr(tph.getCurrency());
                tph.setPayDt(vp.getPayDate());
                TraderPayAccount h = (TraderPayAccount) cboPayment.getSelectedItem();
                tph.setPayAccount(h);
                PaymentVou pv = new PaymentVou();
                pv.setBalance(vp.getVouBalance());
                pv.setVouNo(vp.getVouNo());
                pv.setVouPaid(vp.getCurrentPaid());
                pv.setVouBal(vp.getCurrentPaid());
                pv.setVouDate(vp.getTranDate());
                pv.setDiscount(vp.getCurrentDiscount());
                pv.setVouType(vp.getVouType());
                List<PaymentVou> listPV = new ArrayList();
                listPV.add(pv);
                tph.setListDetail(listPV);
                dao.save(tph);
                uploadToAccount(tph.getPaymentId());
            } catch (Exception e) {
                log.error("saveTraderpayHis : " + e.getMessage());
            }
        }
    }

    private void uploadToAccount(Integer payId) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try ( CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/payment";
                    final HttpPost request = new HttpPost(url);
                    final List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("payId", payId.toString()));
                    request.setEntity(new UrlEncodedFormEntity(params));
                    CloseableHttpResponse response = httpClient.execute(request);
                    log.info(url + response.toString());
                } catch (IOException e) {
                    log.error("uploadToAccount : " + e.getMessage());
                    updateNull(payId);
                }
            }
        } else {
            updateNull(payId);
        }
    }

    private CloseableHttpClient createHttpClientWithTimeouts() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ONE_MILLISECOND)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private void updateNull(Integer payId) {
        try {
            dao.execSql("update payment_his set intg_upd_status = null where payment_id = " + payId);
        } catch (Exception ex) {
            log.error("sale updateNull: " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void calculateBalance(VoucherPayment record) {
        log.info("Vou Type : " + record.getVouType());
        if (record.getVouType().equals("Opening")) {
            Double balance = NumberUtil.NZero(record.getVouBalance())
                    - (NumberUtil.NZero(record.getCurrentDiscount())
                    + NumberUtil.NZero(record.getCurrentPaid()) + NumberUtil.NZero(record.getTtlPaid()));
            record.setVouBalance(balance);
        } else {
            Double balance = NumberUtil.NZero(record.getVouTotal())
                    - (NumberUtil.NZero(record.getCurrentDiscount())
                    + NumberUtil.NZero(record.getCurrentPaid()) + NumberUtil.NZero(record.getTtlPaid()));
            record.setVouBalance(balance);
        }
    }

    @Override
    public int getRowCount() {
        if (listVP == null) {
            return 0;
        }
        return listVP.size();

    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public VoucherPayment getRecord(int row) {
        VoucherPayment record = listVP.get(row);
        return record;
    }

    public List<VoucherPayment> getListVP() {
        return listVP;
    }

    public void setListVP(List<VoucherPayment> listVP) {
        this.listVP = listVP;
        fireTableDataChanged();
    }

    public Double getTotalBalance() {
        double ttlBalance = 0;

        if (listVP != null) {
            for (VoucherPayment vp : listVP) {
                ttlBalance += vp.getVouBalance();
            }
        }

        return ttlBalance;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }
}
