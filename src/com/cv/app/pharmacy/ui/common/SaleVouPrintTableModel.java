/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleVouPrintTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleVouSearchTableModel.class.getName());
    private List<VoucherSearch> listVS = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Ref. Vou.", "Customer",
        "User", "Location", "Sale Man", "Voucher Check", "IP", "V-Total",
        "Discount", "Paid", "Balance"
    };
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");
    private Double vouTotal = 0.0;
    private Double discTotal = 0.0;
    private Double paidTotal = 0.0;
    private Double balanceTotal = 0.0;
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Vou No
                return String.class;
            case 2: //Remark
                return String.class;
            case 3: //Customer
                return String.class;
            case 4: //User
                return String.class;
            case 5: //Location
                return String.class;
            case 6: //Sale Man
                return String.class;
            case 7: //Voucher Check
                return String.class;
            case 8: //Is printed
                return Boolean.class;
            case 9: //V-Total
                return Double.class;
            case 10: //Discount
                return Double.class;
            case 11: //Paid
                return Double.class;
            case 12: //Balance
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVS == null) {
            return null;
        }

        if (listVS.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch vs = listVS.get(row);

            switch (column) {
                case 0: //Date
                    return vs.getTranDate();
                case 1: //Vou No
                    if (Util1.getNullTo(vs.getIsDeleted())) {
                        return vs.getInvNo() + "**";
                    } else {
                        return vs.getInvNo();
                    }
                case 2: //Remark
                    return vs.getRefNo();
                case 3: //Customer
                    if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {

                        if (vs.getCusNo() != null) {
                            return vs.getCusNo() + " - " + vs.getCusName();
                        } else {
                            return vs.getCusName();
                        }
                    } else {
                        if (vs.getCusNo() != null) {
                            if (prifxStatus.equals("Y")) {
                                return vs.getCusNo().replace("CUS", "").replace("SUP", "")
                                        + " - " + vs.getCusName();
                            } else {
                                return vs.getCusNo() + " - " + vs.getCusName();
                            }
                        } else {
                            if (vs.getCusName() != null) {
                                return vs.getCusName();
                            } else {
                                return null;
                            }
                        }
                    }
                case 4: //User
                    return vs.getUserName();
                case 5: //Location
                    return vs.getLocation();
                case 6: //Sale Man
                    return vs.getSaleMan();
                case 7: //Voucher Check
                    return vs.getVoucherChecker();
                case 8: //Is Printed
                    return vs.getIsPrinted();
                case 9: //V-Total
                    return NumberUtil.roundTo(vs.getVouTotal(), 0);
                case 10: //Discount
                    return NumberUtil.roundTo(vs.getDiscount(), 0);
                case 11: //Paid
                    return NumberUtil.roundTo(vs.getPaid(), 0);
                case 12: //Balance
                    return NumberUtil.roundTo(vs.getBalance(), 0);
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listVS == null) {
            return 0;
        }
        return listVS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListVS() {
        return listVS;
    }

    public void setListVS(List<VoucherSearch> listVS) {
        this.listVS = listVS;
        fireTableDataChanged();
        System.gc();
        calculateTotal();
    }

    public VoucherSearch getSelectVou(int row) {
        if (listVS != null) {
            if (!listVS.isEmpty()) {
                return listVS.get(row);
            }
        }
        return null;
    }

    public void calculateTotal() {
        vouTotal = 0.0;
        discTotal = 0.0;
        paidTotal = 0.0;
        balanceTotal = 0.0;
    
        if (listVS != null) {
            if (!listVS.isEmpty()) {
                listVS.stream().map(vs -> {
                    vouTotal += NumberUtil.NZero(vs.getVouTotal());
                    return vs;
                }).map(vs -> {
                    discTotal += NumberUtil.NZero(vs.getDiscount());
                    return vs;
                }).map(vs -> {
                    paidTotal += NumberUtil.NZero(vs.getPaid());
                    return vs;
                }).forEachOrdered(vs -> {
                    balanceTotal += NumberUtil.NZero(vs.getBalance());
                });
            }
        }
    }

    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    public Double getDiscTotal() {
        return discTotal;
    }

    public void setDiscTotal(Double discTotal) {
        this.discTotal = discTotal;
    }

    public Double getPaidTotal() {
        return paidTotal;
    }

    public void setPaidTotal(Double paidTotal) {
        this.paidTotal = paidTotal;
    }

    public Double getBalanceTotal() {
        return balanceTotal;
    }

    public void setBalanceTotal(Double balanceTotal) {
        this.balanceTotal = balanceTotal;
    }
    
    public void clear(){
        this.listVS = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
    
    public void setVoucherChecker(int index, String name){
        VoucherSearch vs = listVS.get(index);
        vs.setVoucherChecker(name);
        fireTableCellUpdated(index, 7);
    }
    
    public void setPrintStatus(int index, boolean status){
        VoucherSearch vs = listVS.get(index);
        vs.setIsPrinted(status);
        fireTableCellUpdated(index, 7);
    }
}
