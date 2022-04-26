/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Point;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class StockBalTableModel extends AbstractTableModel {
    
    static Logger log = Logger.getLogger(StockBalTableModel.class.getName());
    private List<Stock> listBalance;
    private final String[] columnNames = {"Description", "Exp-Date", "In-Hand", "Deman",
        "FOC", "Balance"};
    private Point p;
    private MedicineUP medUp;
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3 || column == 4;
    }
    
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Medicine
                return String.class;
            case 1: //Exp-Date
                return Date.class;
            case 2: //In-Hand
                return String.class;
            case 3: //Deman
                return String.class;
            case 4: //FOC
                return String.class;
            case 5: //Balance
                return String.class;
            default:
                return Object.class;
        }
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        if (listBalance == null) {
            return null;
        }
        
        if (listBalance.isEmpty()) {
            return null;
        }
        
        try {
            Stock record = listBalance.get(row);
            
            switch (column) {
                case 0: //Medicine
                    return record.getMed().getMedName();
                case 1: //Exp-Date
                    return record.getExpDate();
                case 2: //In-Hand
                    return record.getQtyStr();
                case 3: //Deman
                    return record.getQtyStrDeman();
                case 4: //FOC
                    return record.getFocQtyStr();
                case 5: //Balance
                    return record.getQtyStrBal();
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
        if (listBalance == null) {
            return;
        }
        
        if (listBalance.isEmpty()) {
            return;
        }
        
        try {
            Stock record = listBalance.get(row);
            
            switch (column) {
                case 0: //Medicine
                    record.setMed((Medicine) value);
                    break;
                case 1: //Exp-Date
                    record.setExpDate((Date) value);
                    break;
                case 2: //In-Hand
                    record.setQtyStr((String) value);
                    break;
                case 3: //Deman
                    if (value != null && !value.toString().isEmpty()) {
                        String medId = record.getMed().getMedId();
                        if (medUp.getUnitList(medId).size() > 1) {
                            UnitAutoCompleter unitPopup = new UnitAutoCompleter(p.x, p.y,
                                    medUp.getUnitList(medId),
                                    Util1.getParent());
                            
                            if (unitPopup.isSelected()) {
                                record.setUnit(unitPopup.getSelUnit());
                            }
                        } else {
                            record.setUnit(medUp.getUnitList(medId).get(0));
                        }
                        
                        String qtyStr = (String) value + record.getUnit().getItemUnitCode();
                        
                        record.setQtyStrDeman(qtyStr);
                        record.setUnitQty(new Float(value.toString()));
                    } else {
                        record.setQtyStrDeman(null);
                        record.setUnit(null);
                        record.setUnitQty(null);
                    }
                    
                    record.setQtyStrBal(MedicineUtil.getQtyInStr(record.getMed(), getBalance(record)));
                    break;
                case 4: //FOC
                    if (value != null && !value.toString().isEmpty()) {
                        String medId = record.getMed().getMedId();
                        
                        UnitAutoCompleter unitPopup = new UnitAutoCompleter(p.x, p.y,
                                medUp.getUnitList(medId),
                                Util1.getParent());
                        
                        if (unitPopup.isSelected()) {
                            record.setFocUnit(unitPopup.getSelUnit());
                            String qtyStr = (String) value + unitPopup.getSelUnit().getItemUnitCode();
                            record.setFocQtyStr(qtyStr);
                            record.setFocUnitQty(new Float(value.toString()));
                        }
                    } else {
                        record.setFocQtyStr(null);
                        record.setFocUnit(null);
                        record.setFocUnitQty(null);
                    }
                    
                    record.setQtyStrBal(MedicineUtil.getQtyInStr(record.getMed(), getBalance(record)));
                    break;
                case 5: //Balance
                    record.setQtyStrBal((String) value);
                    break;
                default:
                    System.out.println("invalid index");
            }
        } catch (NumberFormatException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        fireTableCellUpdated(row, column);
        fireTableCellUpdated(row, 5);
    }
    
    @Override
    public int getRowCount() {
        if(listBalance == null){
            return 0;
        }
        return listBalance.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public void setListDetail(List<Stock> listBalance) {
        this.listBalance = listBalance;
        
        fireTableDataChanged();
    }
    
    public void setPoint(Point p) {
        this.p = p;
    }
    
    public void setMedUp(MedicineUP medUp) {
        this.medUp = medUp;
    }
    
    public float getBalance(Stock record) {
        float balance = 0;
        float demanQtySmall = 0;
        float focQtySmall = 0;
        String demanKey = null;
        String focKey = null;
        
        if (record.getUnit() != null) {
            demanKey = record.getMed().getMedId() + "-"
                    + record.getUnit().getItemUnitCode();
        }
        
        if (record.getFocUnit() != null) {
            focKey = record.getMed().getMedId() + "-"
                    + record.getFocUnit().getItemUnitCode();
        }
        
        if (demanKey != null) {
            demanQtySmall = medUp.getQtyInSmallest(demanKey);
        }
        
        if (focKey != null) {
            focQtySmall = medUp.getQtyInSmallest(focKey);
        }
        
        balance = NumberUtil.NZeroFloat(record.getQtySmallest())
                - ((NumberUtil.NZeroFloat(record.getUnitQty()) * demanQtySmall)
                + (NumberUtil.NZeroFloat(record.getFocUnitQty()) * focQtySmall));
        
        return balance;
    }
}
