/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.tempentity.TraderFilter;
import com.cv.app.pharmacy.database.tempentity.TraderFilterKey;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TraderFilterTableModel extends AbstractTableModel {
    static Logger log = Logger.getLogger(TraderFilterTableModel.class.getName());
    private List<TraderFilter> listTraderFilter = new ArrayList();
    private final String[] columnNames = {"Code", "Trader Name"};
    private AbstractDataAccess dao;
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");
    
    public TraderFilterTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listTraderFilter == null){
            return null;
        }
        
        if(listTraderFilter.isEmpty()){
            return null;
        }
        
        try{
        TraderFilter record = listTraderFilter.get(row);

        switch (column) {
            case 0: //Code
                if (record.getKey() == null) {
                    return null;
                } else {
                    if (prifxStatus.equals("Y")) {
                        return record.getKey().getTraderId().getStuCode();
                    }else{
                        return record.getKey().getTraderId().getTraderId();
                    }
                }
            case 1: //Trader Name
                if (record.getKey() == null) {
                    return null;
                } else {
                    return record.getKey().getTraderId().getTraderName();
                }
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

        switch (column) {
            case 0: //Code
                if(value != null){
                    getTraderInfo(value.toString(), row);
                }
                break;
            case 1: //Desp
                //record.setMedName(value.toString());
                break;
            default:
                System.out.println("invalid index");
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listTraderFilter.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if(listTraderFilter == null){
            return false;
        }
        if (listTraderFilter.isEmpty()) {
            return false;
        }

        TraderFilter record = listTraderFilter.get(listTraderFilter.size() - 1);
        return record.getKey() == null;
    }

    public void addEmptyRow() {
        if(listTraderFilter != null){
        TraderFilter record = new TraderFilter();
        listTraderFilter.add(record);
        fireTableRowsInserted(listTraderFilter.size() - 1, listTraderFilter.size() - 1);
        }
    }

    public void delete(int row) {
        try{
            TraderFilter record = listTraderFilter.get(row);
            if (record.getKey().getTraderId() != null) {
                dao.delete(record);
                listTraderFilter.remove(row);
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }

                fireTableRowsDeleted(row, row);
            }
        }catch(Exception ex){
            log.error("delete : " + ex.getMessage());
        }
    }

    public void setTraderFilter(TraderFilter traderFilter, int pos) {
        if(listTraderFilter != null){
            if(!listTraderFilter.isEmpty()){
                listTraderFilter.set(pos, traderFilter);
            }
        }
        
        fireTableDataChanged();
        if (!hasEmptyRow()) {
            addEmptyRow();
        }
    }

    public void getTraderInfo(String traderId, int row) {
        String userId = Global.machineId;
        final String TABLE = "com.cv.app.pharmacy.database.tempentity.TraderFilter";
        String strSQL = "SELECT * FROM " + TABLE
                                        + " WHERE key.traderId.traderId = '" + traderId + "'";
        
        try {
            if(!JoSQLUtil.isAlreadyHave(strSQL, listTraderFilter)){
                dao.open();
                Trader trader = (Trader) dao.find("Trader", "traderId = '"
                        + traderId + "' and active = true");
                dao.close();

                if (trader != null) {
                    TraderFilter tmp = listTraderFilter.get(row);
                    dao.delete(tmp);
                    TraderFilter tf = new TraderFilter(
                            new TraderFilterKey(trader, userId));
                    dao.save(tf);
                    setTraderFilter(tf, row);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid trader id.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate trader code.",
                            "Duplicate", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getTraderInfo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }
}
