/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.Global;
import com.cv.app.opd.ui.common.*;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.helper.VoucherPayment;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.MapMessage;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author lenovo
 */
public class CPaymentEntryTableModel extends AbstractTableModel {

    static Logger LOGGER = Logger.getLogger(OPDTableModel.class.getName());
    private List<VoucherPayment> listVP = new ArrayList();
    private final String[] columnNames = {"Vou Date", "Vou No.", "Cus-No", "Cus-Name",
        "Due Date", "Overdue", "Vou Total", "Ttl Paid", "Pay Date", "AC", "Paid", "Discount", "Vou Balance", "FP"};
    // private AbstractDataAccess dao;
    private SelectionObserver observer;
    private final AbstractDataAccess dao = Global.dao;

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
            case 8:
                return record.getPayDate() == null;
            case 9:
                return false;
            case 10:
                return record.getCurrentPaid() == null;
            case 11:
                return record.getCurrentDiscount() == null;
            case 13:
                return !record.isIsFullPaid();
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0://Vou Date
                return Date.class;
            case 1://Vou No
                return String.class;
            case 2://Cus-No
                return String.class;
            case 3://Cus-Name
                return String.class;
            case 4://Due Date
                return Date.class;
            case 5://Ttl Overdue
                return Integer.class;
            case 6://Vou Total
                return Double.class;
            case 7://Ttl Paid
                return Double.class;
            case 8://Pay Date
                return String.class;
            case 9://AC
                return String.class;
            case 10://Paid
                return Double.class;
            case 11://Discount
                return Double.class;
            case 12://Vou Balance
                return Double.class;
            case 13://Full Paid
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
                    if (record.getVouType().equals("Opening")) {
                        return "Opening";
                    } else {
                        return record.getVouNo();
                    }
                case 2: //Cus-No
                    return record.getTraderId();
                case 3: //Cus-Name
                    return record.getTraderName();
                case 4: //Due Date
                    return record.getDueDate();
                case 5: //Ttl Overdue
                    return record.getTtlOverdue();
                case 6: //Vou Total
                    return record.getVouTotal();
                case 7: //Ttl Paid
                    return record.getTtlPaid();
                case 8: //Pay Date
                    if (record.getPayDate() == null) {
                        return null;
                    } else {
                        return DateUtil.toDateStr(record.getPayDate());
                    }
                case 9: //AC
                    return null;
                case 10: //Paid
                    return record.getCurrentPaid();
                case 11: //Discount
                    return record.getCurrentDiscount();
                case 12: //Vou Balance
                    return record.getVouBalance();
                case 13: //Full Paid
                    return record.isIsFullPaid();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getMessage());
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
            case 8://Pay Date
                if (value == null) {
                    record.setPayDate(null);
                } else {
                    record.setPayDate(DateUtil.toDate(value.toString(), "dd/MM/yyyy"));
                }
                break;
            case 9: //Account
                break;
            case 10://Paid
                record.setCurrentPaid(NumberUtil.NZero(value));
                record.setListIndex(row);
                record.setTranId(-1);
                //observer.selected("Paid", record);
                break;
            case 11://Discount
                record.setCurrentDiscount(NumberUtil.NZero(value));
                record.setListIndex(row);
                record.setTranId(-1);
                // observer.selected("Discount", record);
                break;
            case 13://Full Paid
                if (value != null) {
                    Boolean fullPaid = (Boolean) value;
                    record.setIsFullPaid(fullPaid);
                    if (fullPaid) {
                        record.setCurrentPaid(NumberUtil.NZero(record.getVouBalance())
                                - NumberUtil.NZero(record.getCurrentDiscount()));
                        record.setVouBalance(0.0);
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
                fireTableCellUpdated(row, 8);
                fireTableCellUpdated(row, 9);
                break;

        }

        Save(record);
        calculateBalance(record);
        //  assignData(record);
        //   observer.selected("FullPaid", record);
        /*  if (observer != null) {
         observer.selected("CalculateTotal", null);
         }*/

        fireTableCellUpdated(row, 10);
    }

    private void Save(VoucherPayment vp) {
        Date vouSaleDate = vp.getPayDate();
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (vp.getPayDate() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Date.",
                    "Pay Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            TraderPayHis tph = new TraderPayHis();
            vp.setRemark("FullPaid");

            vp.setUserId(Global.loginUser.getUserId());
            tph.setPayDate(vp.getPayDate());
            List<Customer> cus = dao.findAllHSQL("select o from Customer o where o.traderId='" + vp.getTraderId() + "'");
            if (!cus.isEmpty()) {
                tph.setTrader(cus.get(0));
            }
            tph.setRemark(vp.getRemark());
            tph.setPaidAmtC(vp.getCurrentPaid());
            tph.setDiscount(vp.getCurrentDiscount());
            String appCurr = Util1.getPropValue("system.app.currency");
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
            PaymentVou pv = new PaymentVou();
            pv.setBalance(vp.getVouBalance());
            pv.setVouNo(vp.getVouNo());
            pv.setVouPaid(vp.getCurrentPaid());
            pv.setBalance(vp.getVouBalance());
            pv.setVouDate(vp.getTranDate());
            pv.setDiscount(vp.getDiscount());
            pv.setVouType(vp.getVouType());
            List<PaymentVou> listPV = new ArrayList();
            listPV.add(pv);
            tph.setListDetail(listPV);

            Location location = getLocation(tph.getTrader().getTraderId());
            tph.setLocation(location);

            dao.save(tph);
            uploadToAccount(tph);
        } catch (Exception e) {
            LOGGER.error("saveTraderpayHis : " + e.getMessage());
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
                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        LOGGER.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber()
                                + " - " + tph.getPaymentId() + " - " + ex);
                    }
                }
            }
        }
    }

    private Location getLocation(String traderId) {
        Location location = null;
        try {
            List<Location> listLOC = dao.findAllHSQL("select o from Location o "
                    + "where o.locationId in (select a.key.locationId from "
                    + "LocationTraderMapping a where a.key.traderId = '" + traderId + "')");
            if (listLOC != null) {
                if (!listLOC.isEmpty()) {
                    location = listLOC.get(0);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("getLocation : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return location;
    }

    private void calculateBalance(VoucherPayment record) {
        Double balance = NumberUtil.NZero(record.getVouTotal())
                - (NumberUtil.NZero(record.getCurrentDiscount())
                + NumberUtil.NZero(record.getCurrentPaid()) + NumberUtil.NZero(record.getTtlPaid()));
        record.setVouBalance(balance);
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
