/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.CompoundKeyTraderOp;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderOpening;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TraderOpTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TraderOpTableModel.class.getName());
    private final String[] columnNames = {"Date", "Currency", "Amount"};
    private List<TraderOpening> listOp = new ArrayList();
    private List<TraderOpening> listDeleteOp = new ArrayList();
    private Trader trader = new Trader();
    private boolean editable;
    
    public TraderOpTableModel(){
        editable = false;
    }
    
    public TraderOpTableModel(boolean editable){
        this.editable = editable;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return editable;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 1: //Currency
                return Currency.class;
            case 2: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listOp == null){
            return null;
        }
        
        if(listOp.isEmpty()){
            return null;
        }
        
        try{
        TraderOpening record = listOp.get(row);

        switch (column) {
            case 0: //Date
                return DateUtil.toDateStr(record.getKey().getOpDate());
            case 1: //Currency
                return record.getKey().getCurrency();
            case 2: //Amount
                return record.getAmount();
            default:
                return new Object();
        }
        }catch(Exception ex){
            log.error("getValueAt : " + ex.getMessage());
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if(listOp == null){
            return;
        }
        
        if(listOp.isEmpty()){
            return;
        }
        
        try{
        TraderOpening record = listOp.get(row);

        switch (column) {
            case 0: //Date
                if(value != null){
                    Date tmpDate = DateUtil.toDate(value.toString());
                    
                    if(tmpDate == null){
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening date.",
                        "Opening date", JOptionPane.ERROR_MESSAGE);
                    }else{
                        record.getKey().setOpDate(tmpDate);
                    }
                }
                
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }
                break;
            case 1: //Currency
                record.getKey().setCurrency((Currency) value);
                break;
            case 2: //Amount
                record.setAmount((Double) value);
            default:
                System.out.println("invalid index");
        }
        }catch(HeadlessException ex){
            log.error("setValueAt : " + ex.getMessage());
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if(listOp == null){
            return 0;
        }
        return listOp.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if(listOp == null){
            return false;
        }
        if (listOp.isEmpty()) {
            return false;
        }

        TraderOpening record = listOp.get(listOp.size() - 1);
        return record.getKey().getOpDate() == null;
    }

    public void addEmptyRow() {
        if(listOp != null){
        if(editable){
            TraderOpening record = new TraderOpening(new CompoundKeyTraderOp(trader));
            listOp.add(record);
            fireTableRowsInserted(listOp.size() - 1, listOp.size() - 1);
        }
        }
    }

    public void delete(int row) {
        if(listOp == null){
            return;
        }
        
        if(listOp.isEmpty()){
            return;
        }
        
        TraderOpening op = listOp.get(row);
        
        if (op.getKey().getCurrency() != null) {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Sale item delete", JOptionPane.YES_NO_OPTION);
            
            if(yes_no == 0){
                if(!editable){
                    listDeleteOp.add(op);
                }

                listOp.remove(row);
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }

                fireTableRowsDeleted(row, row);
            }
        }
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
        if(listOp != null){
        listOp.removeAll(listOp);
        }
        fireTableDataChanged();
        
        if (!hasEmptyRow()) {
            addEmptyRow();
        }
    }

    public List<TraderOpening> getListOp() {
        return listOp;
    }

    public void setListOp(List<TraderOpening> listOp) {
        this.listOp = listOp;
        fireTableDataChanged();
    }

    public void clear() {
        if(listOp != null){
        listOp.removeAll(listOp);
        }
        if(listDeleteOp != null){
        listDeleteOp.removeAll(listDeleteOp);
        }
        
        fireTableDataChanged();
    }

    public boolean isValidEntry() {
        boolean status = true;
        
        if(listOp == null){
            return false;
        }
        
        for (int i = 0; i < listOp.size() - 1; i++) {
            TraderOpening traderOP = listOp.get(i);

            if (traderOP.getKey().getOpDate() == null) {
                status = false;
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening date.",
                        "Date null.", JOptionPane.ERROR_MESSAGE);
                i = listOp.size();
            }else if(traderOP.getKey().getCurrency() == null){
                status = false;
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid currency.",
                        "Currency null", JOptionPane.ERROR_MESSAGE);
                i = listOp.size();
            }else if(traderOP.getAmount() == null){
                status = false;
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid amount.",
                        "Amount null", JOptionPane.ERROR_MESSAGE);
                i = listOp.size();
            }
        }
        
        return status;
    }
    
    public List<TraderOpening> getDeleteList(){
        return listDeleteOp;
    }
}
