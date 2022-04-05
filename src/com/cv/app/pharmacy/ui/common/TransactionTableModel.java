/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.TraderTransaction;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class TransactionTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TransactionTableModel.class.getName());
    private List<TraderTransaction> listDetail = new ArrayList();
    private final String[] columnNames = {"Description", "Date", "Amount"};
    private List<Integer> listDeletePayment = new ArrayList();

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
            case 0: //Description
                return String.class;
            case 1: //Date
                return String.class;
            case 2: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listDetail == null){
            return null;
        }
        
        if(listDetail.isEmpty()){
            return null;
        }
        
        try{
        TraderTransaction tt = listDetail.get(row);

        switch (column) {
            case 0: //Description
                if (tt.getTranOption().equals("Payment")) {
                    return tt.getRemark();
                } else {
                    return tt.getTranOption();
                }
            case 1: //Date
                return DateUtil.toDateStr(tt.getTranDate());
            case 2: //Amount
                return tt.getAmount();
            default:
                return null;
        }
        }catch(Exception ex){
            log.error("getValueAt : " + ex.getMessage());
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

        try {
            switch (column) {
                case 0: //Option
                    //srdh.setRecOption((String)value);
                    break;
                case 1: //Date
                    //srdh.setRefVou((String)value);
                    break;
                case 2: //Amount
                    //srdh.getOrderMed().setMedId((String)value);
                    break;
                default:
                    System.out.println("ReceiveTableModel invalid index.");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        if (column != 0) {
            fireTableCellUpdated(row, column);
        }
    }

    @Override
    public int getRowCount() {
        if(listDetail == null){
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TraderTransaction> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<TraderTransaction> listDetail) {
        extractUpdatePayment();
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public void add(TraderTransaction transaction) {
        if (listDetail != null) {
            String strSQL = "SELECT * FROM com.cv.app.pharmacy.database.helper.TraderTransaction "
                    + " WHERE paymentId = " + transaction.getPaymentId();
            List list = JoSQLUtil.getResult(strSQL, listDetail);

            if (list.isEmpty()) {
                listDetail.add(transaction);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.removeAll(listDetail);
            fireTableDataChanged();
        }
    }

    public void delete(int row) {
        if(listDetail == null){
            return;
        }
        
        TraderTransaction tmp = listDetail.get(row);

        if (tmp.getTranOption().equals("Payment")) {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Expense item delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                listDeletePayment.add(tmp.getPaymentId());
                listDetail.remove(row);
                fireTableRowsDeleted(row, row);
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "You cannot delete this item.",
                    "Delete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public Double getTotal() {
        double total = 0;

        if(listDetail != null){
            total = listDetail.stream().map(tt -> NumberUtil.NZero(tt.getAmount()))
                    .reduce(total, (accumulator, _item) -> accumulator + _item);
        }
        
        return total;
    }

    public List<Integer> getDeletePayment() {
        return listDeletePayment;
    }

    public void removeDeletePayment() {
        if(listDeletePayment != null){
        listDeletePayment.removeAll(listDeletePayment);
        }
    }

    private void extractUpdatePayment() {
        if(listDetail == null){
            return;
        }
        
        String strSQL = "SELECT * FROM com.cv.app.pharmacy.database.helper.TraderTransaction "
                + " WHERE saleVouNo is not null";
        List<TraderTransaction> list = JoSQLUtil.getResult(strSQL, listDetail);

        if (list != null) {
            for (TraderTransaction tt : list) {
                listDeletePayment.add(tt.getPaymentId());
            }
        }
    }

    public TraderTransaction getTransaction(int row) {
        if(listDetail != null){
            if(!listDetail.isEmpty()){
                return listDetail.get(row);
            }
        }
        return null;
    }
}
