/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

/**
 *
 * @author WSwe
 */
public class FillVouPaymentListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(FillVouPaymentListTableModel.class.getName());
    private final String[] columnNames = {"Vou Date", "Vou No", "Ref-No", "Vou Type",
        "Vou-Ttl", "Vou-Paid", "Vou-Disc", "Vou-Balance", "Vou-Last Bal", "Paid", "Balance"};
    private List<PaymentVou> listVou = new ArrayList();
    private double ttlPaid = 0;
    private boolean editable = true;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 9 && editable;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Vou Date
            case 1: //Vou No
            case 2: //Ref-No
            case 3: //Vou type
                return String.class;
            case 4: //Vou-Ttl
            case 5: //Vou-Paid
            case 6: //Vou-Disc
            case 7: //Vou-Balance
                return Double.class;
            case 8: //Last Balance
            case 9: //Paid
            case 10: //Balance
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVou == null) {
            return null;
        }

        if (listVou.isEmpty()) {
            return null;
        }

        try {
            PaymentVou record = listVou.get(row);

            switch (column) {
                case 0: //Vou Date
                    return record.getVouDate();
                case 1: //Vou No
                    return record.getVouNo();
                case 2: //Ref-No
                    return record.getRefNo();
                case 3: //Vou Type
                    return record.getVouType();
                case 4: //Vou-Ttl
                    return record.getVouTotal();
                case 5: //Vou-Paid
                    return record.getPaidAmount();
                case 6: //Vou-Dis
                    return record.getDiscount();
                case 7: //Vou-Balance
                    return record.getVouBalance();
                case 8: //Vou-Last Bal
                    return record.getVouBal();
                case 9: //Paid
                    return record.getVouPaid();
                case 10: //Balance
                    return record.getBalance();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listVou == null) {
            return;
        }

        if (listVou.isEmpty()) {
            return;
        }

        try {
            PaymentVou record = listVou.get(row);

            switch (column) {
                /*case 0: //Vou No
                 record.setVouNo((String) value);
                 break;
                 case 1: //Vou Type
                 record.setVouType((String) value);
                 break;
                 case 2: //Amount
                 record.setVouBal((Double) value);
                 break;*/
                case 9: //Paid
                    if (value != null) {
                        if (isValidAmt((Double) value, record)) {
                            record.setVouPaid((Double) value);
                            record.setBalance(record.getVouBal() - record.getVouPaid());
                            //reAdjust(ttlPaid - record.getVouPaid(), row);
                        }
                    } else {
                        record.setVouPaid(null);
                        record.setBalance(record.getVouBal());
                        fireTableCellUpdated(row, column + 1);
                    }
                    break;
                /*case 4: //Balance
                 record.setBalance((Double)value);
                 break;*/
                default:
                    System.out.println("invalid index");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if (listVou == null) {
            return 0;
        }
        return listVou.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PaymentVou> getListVou() {
        return listVou;
    }

    public void setListVou(List<PaymentVou> listVou) {
        this.listVou = listVou;

        fireTableDataChanged();
    }

    public void removeList() {
        if (listVou != null) {
            ttlPaid = 0;
            listVou.removeAll(listVou);
            fireTableDataChanged();
        }
    }

    private boolean isValidAmt(double payAmt, PaymentVou record) {
        if (payAmt <= record.getVouBal()) {
            return true;
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Your amount is grater than voucher balance.",
                    "Over amount.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<PaymentVou> getEntryVou() {
        String strSQL = "SELECT * FROM com.cv.app.pharmacy.database.entity.PaymentVou "
                + "WHERE vouPaid > 0 ";
        Query q = new Query();
        List<PaymentVou> list = null;

        try {
            q.parse(strSQL);
            QueryResults qr = q.execute(listVou);
            list = qr.getResults();
        } catch (QueryParseException | QueryExecutionException ex) {
            log.error("getEntryVou : " + ex.getMessage());
        }

        return list;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    private void reAdjust(double leftAmt, int index) {
        if (listVou != null) {
            for (int i = 0; i < listVou.size(); i++) {
                if (i != index) {
                    PaymentVou pv = listVou.get(i);

                    if (pv.getVouBal() == leftAmt) {
                        pv.setVouPaid(leftAmt);
                        pv.setBalance(0.0);
                        i = listVou.size();
                    } else {
                        if (leftAmt > pv.getVouBal()) {
                            pv.setVouPaid(pv.getVouBal());
                            pv.setBalance(0.0);
                            leftAmt = leftAmt - pv.getVouBal();
                        } else {
                            pv.setVouPaid(leftAmt);
                            pv.setBalance(pv.getVouBal() - pv.getVouPaid());
                            i = listVou.size();
                        }
                    }
                }

                fireTableDataChanged();
            }
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        List<PaymentVou> listPaymentVou = getEntryVou();
        double ttlAmt = 0;

        for (PaymentVou pv : listPaymentVou) {
            ttlAmt += NumberUtil.NZero(pv.getVouPaid());
        }

        if (ttlPaid != ttlAmt) {
            status = false;
        }

        return status;
    }

    public void clear() {
        if (listVou != null) {
            listVou.removeAll(listVou);
            fireTableDataChanged();
        }
    }
}
