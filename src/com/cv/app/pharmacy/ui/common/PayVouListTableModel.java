/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
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
public class PayVouListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PayVouListTableModel.class.getName());
    private String[] columnNames = {"Vou Date", "Vou No", "Ref-No", "Vou Type",
        "Vou-Balance", "Paid", "Balance"};
    private List<PaymentVou> listVou = new ArrayList();
    private double ttlPaid = 0;
    private boolean editable = false;
    private double paid = 0;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 5 && editable) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Vou Date
                return Date.class;
            case 1: //Vou No
            case 2: //Ref-No
            case 3: //Vou-Type
                return String.class;
            case 4: //Vou Balance
            case 5: //Paid
            case 6: //Balance
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PaymentVou record = listVou.get(row);

        switch (column) {
            case 0: //Vou Date
                return record.getVouDate();
            case 1: //Vou No
                return record.getVouNo();
            case 2: //Ref-No
                return record.getRefNo();
            case 3: //Vou-Type
                return record.getVouType();
            case 4: //Vou-Balance
                return record.getVouBal();
            case 5: //Paid
                return record.getVouPaid();
            case 6: //Balance
                return record.getBalance();
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
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
            case 5: //Paid
                double dPv = 0;

                if (value != null) {
                    if (isValidAmt((Double) value, record)) {
                        record.setVouPaid((Double) value);
                        record.setBalance(record.getVouBal() - record.getVouPaid());
                    }
                } else {
                    record.setVouPaid(null);
                    record.setBalance(record.getVouBal());
                }

                for (PaymentVou pv : listVou) {
                    if (pv.getVouPaid() != null) {
                        dPv = dPv + pv.getVouPaid();
                    }
                }
                if (dPv > ttlPaid) {
                    record.setVouPaid(null);
                    record.setBalance(record.getVouBal());
                    fireTableCellUpdated(row, column);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Vouchers paid over paid amount",
                            "Check Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
            /*case 4: //Balance
             record.setBalance((Double)value);
             break;*/
            default:
                System.out.println("invalid index");
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
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
        ttlPaid = 0;
        listVou.removeAll(listVou);
        fireTableDataChanged();
    }

    public void fillPyment(double ttlPay) {
        this.ttlPaid = ttlPay;
        double leftAmt = ttlPay;

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
        }

        fireTableDataChanged();
    }

    private boolean isValidAmt(double payAmt, PaymentVou record) {
        if (payAmt <= ttlPaid) {
            if (payAmt <= record.getVouBal()) {
                return true;
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Your amount is grater than voucher balance.",
                        "Over amount.", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Your amount is grater than total paid amount.",
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
            log.error("getEntryVou : " + +ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return list;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    private void reAdjust(double leftAmt, int index) {
        for (int i = 0; i < listVou.size(); i++) {
            if (i != index) {
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
            }

            fireTableDataChanged();
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        List<PaymentVou> listPaymentVou = getEntryVou();
        double ttlAmt = 0;

        if (listPaymentVou != null) {
            if (!listPaymentVou.isEmpty()) {
                for (PaymentVou pv : listPaymentVou) {
                    ttlAmt += NumberUtil.NZero(pv.getVouPaid());
                }

                String pamount = Util1.getPropValue("system.payment.amount.type");
                if (pamount.toUpperCase().equals("Y")) {
                    status = true;
                }else if (ttlPaid != ttlAmt) {
                    status = false;
                }
            }
        }

        return status;
    }
}
