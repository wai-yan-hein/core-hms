/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VSession;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.Util1;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SessionTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SessionTableModel.class.getName());
    private List<VSession> listVSession = new ArrayList();
    private List<VSession> listMVSession = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Ref. Vou.", "Cus-Name", "V-Total", "VC",
        "Paid", "PC", "Disc", "Balance", "Exp-In", "Exp-Out", "Tax", "User", "Source", "Session", "Bill"};
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");

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
            case 0: //Tran Date
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Ref. Vou.
                return String.class;
            case 3: //Cus-Name
                return String.class;
            case 4: //V-Total
                return Double.class;
            case 5: //V-Currency
                return String.class;
            case 6: //Paid
                return Double.class;
            case 7: //P-Currency
                return String.class;
            case 8: //Discount
                return Double.class;
            case 9: //Balance
                return Double.class;
            case 10: //Exp-In
                return Double.class;
            case 11: //Exp-Out
                return Double.class;
            case 12: //Tax
                return Double.class;
            case 13: //User
                return String.class;
            case 14: //Source
                return String.class;
            case 15: //Session
                return String.class;
            case 16: //Pay type
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVSession == null) {
            return null;
        }

        if (listVSession.isEmpty()) {
            return null;
        }

        try {
            VSession vSession = listVSession.get(row);

            switch (column) {
                case 0: //Tran Date
                    return DateUtil.toDateStr(vSession.getTranDate(), "dd/MM/yyyy hh.mm aa");
                case 1: //Vou No
                    if (vSession.isDeleted()) {
                        return vSession.getKey().getInvId() + "*";
                    } else {
                        return vSession.getKey().getInvId();
                    }
                case 2: //Ref. Vou.
                    return vSession.getRefNo();
                case 3: //Cus-Name
                    if (vSession.getTraderId() != null) {
                        if (prifxStatus.equals("Y")) {
                            return vSession.getTraderId().replace("CUS", "")
                                    .replace("SUP", "") + " - " + vSession.getTraderName();
                        } else {
                            return vSession.getTraderId() + " - " + vSession.getTraderName();
                        }
                    } else {
                        return vSession.getTraderName();
                    }
                case 4: //V-Total
                    return NumberUtil.roundTo(vSession.getVouTotal(), 0);
                case 5: //V-Currency
                    return vSession.getCurrency();
                case 6: //Paid
                    return NumberUtil.roundTo(vSession.getPaid(), 0);
                case 7: //P-Currency
                    return vSession.getPaidCurrency();
                case 8: //Disc
                    return NumberUtil.roundTo(vSession.getDiscount(), 0);
                case 9: //Balance
                    return NumberUtil.roundTo(vSession.getBalance(), 0);
                case 10: //Exp-In
                    return NumberUtil.roundTo(vSession.getExpIn(), 0);
                case 11: //Exp-Out
                    return NumberUtil.roundTo(vSession.getExpense(), 0);
                case 12: //Tax
                    return NumberUtil.roundTo(vSession.getTaxAmt(), 0);
                case 13: //User
                    return vSession.getUserShortName();
                case 14: //Source
                    return vSession.getKey().getSource();
                case 15: //Session
                    return vSession.getSessionName();
                case 16: //pay type
                    return vSession.getPayType();
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
        if (listVSession == null) {
            return 0;
        }
        return listVSession.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VSession> getListVSession() {
        return listVSession;
    }

    public void setListVSession(List<VSession> listVSession) {
        this.listVSession = listVSession;
        listMVSession = listVSession;
        fireTableDataChanged();
        System.gc();
    }

    public VSession getVSession(int row) {
        return listVSession.get(row);
    }

    public void setVSession(int row, VSession vSession) {
        if (listVSession != null) {
            listVSession.set(row, vSession);
            fireTableRowsUpdated(row, row);
        }
    }

    public void applyFilter(String strFilter) {
        List<VSession> tmpVSession = JoSQLUtil.getResult(strFilter, listMVSession);
        listVSession = tmpVSession;
        fireTableDataChanged();
        System.out.println("Ttl row : " + listVSession.size());
        System.gc();
    }

}
