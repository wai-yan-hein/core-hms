/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PriceChangeUnitHis;
import com.cv.app.util.NumberUtil;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PChangeUnitTableModel extends AbstractTableModel{
    static Logger log = Logger.getLogger(PChangeUnitTableModel.class.getName());
    private List<PriceChangeUnitHis> listDetailUnit;
    private final String[] columnNames = {"Unit", "Cost Price", "Old", "New", "Old", "New",
                                    "Old", "New", "Old", "New", "Old", "New"};
    private JTable parent;
    private Medicine currMed;
    private JCheckBox chkCalculate;
    
    public PChangeUnitTableModel(List<PriceChangeUnitHis> listDetailUnit) {
        this.listDetailUnit = listDetailUnit;
    }
    
    public void setCurrMed(Medicine currMed){
        this.currMed = currMed;
    }
    
    public void setAutoCalculate(JCheckBox chkCalculate){
        this.chkCalculate = chkCalculate;
    }
    
    public void setParent(JTable parent){
        this.parent = parent;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0 || column == 1 || column == 2 || column == 4 ||
                column == 6 || column == 8) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            //case 0: //Unit
                //return String.class;
            case 1: //Cost Price
            case 2: //Price N old
            case 3: //Price N new
            case 4: //Price A old
            case 5: //Price A new
            case 6: //Price B old
            case 7: //Price B new
            case 8: //Price C old
            case 9: //Price C new
            case 10://Price D old
            case 11://Price D new
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PriceChangeUnitHis record = listDetailUnit.get(row);
        
        switch (column) {
            case 0: //Unit
                if(record.getPcUnit() == null)
                    return null;
                else
                    return record.getPcUnit().getItemUnitCode();
            case 1: //Cost Price
                return record.getCostPrice();
            case 2: //Price N old
                return record.getnPriceOld();
            case 3: //Price N new
                return record.getnPriceNew();
            case 4: //Price A old
                return record.getaPriceOld();
            case 5: //Price A new
                return record.getaPriceNew();
            case 6: //Price B old
                return record.getbPriceOld();
            case 7: //Price B new
                return record.getbPriceNew();
            case 8: //Price C old
                return record.getcPriceOld();
            case 9: //Price C new
                return record.getcPriceNew();
            case 10: //Price D old
                return record.getdPriceOld();
            case 11: //Price D new
                return record.getdPriceNew();
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        PriceChangeUnitHis record = listDetailUnit.get(row);
        /*String medId = listDetailUnit.get(parent.getSelectedRow()).getMed().getMedId();
        int x = Util1.getParent().getX() + (Util1.getParent().getWidth()/2) + 50;
        int y = Util1.getParent().getY() + (Util1.getParent().getHeight()/2) - 200;
        UnitAutoCompleter unitPopup = null;*/
        
        try{
            switch (column) {
                case 0: //Unit
                    //record.setPcUnit((ItemUnit)value);
                    break;
                case 1: //Cost Price
                    break;
                case 2: //Price N old
                    //record.setnPriceOld((Double)value);
                    break;
                case 3: //Price N new
                    record.setnPriceNew(NumberUtil.NZero(value));
                    parent.setColumnSelectionInterval(4, 4);
                    break;
                case 4: //Price A old
                    //record.setaPriceOld((Double)value);
                    break;
                case 5: //Price A new
                    record.setaPriceNew(NumberUtil.NZero(value));
                    parent.setColumnSelectionInterval(6, 6);
                    break;
                case 6: //Price B old
                    //record.setbPriceOld((Double)value);
                    break;
                case 7: //Price B new
                    record.setbPriceNew(NumberUtil.NZero(value));
                    parent.setColumnSelectionInterval(8, 8);
                    break;
                case 8: //Price C old
                    //record.setcPriceOld((Double)value);
                    break;
                case 9: //Price C new
                    record.setcPriceNew(NumberUtil.NZero(value));
                    parent.setColumnSelectionInterval(10, 10);
                    break;
                case 10: //Price D old
                    //record.setdPriceOld((Double)value);
                    break;
                case 11://Price D new
                    record.setdPriceNew(NumberUtil.NZero(value));
                    //parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(2, 2);
                    break;
                default:
                    System.out.println("invalid index");
            }
            
            if(chkCalculate.isSelected() && column > 0)
                autoCalculate(row, column, NumberUtil.NZero(value));
            
            //calculateAmount(row);
            //fireTableCellUpdated(row, 9);
        }catch(Exception ex){
            log.error(ex.toString());
        }
        
        if(column != 0)
            fireTableCellUpdated(row, column);
    }
    
    @Override
    public int getRowCount() {
        return listDetailUnit.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public void dataChange(){
        fireTableDataChanged();
    }
    
    private void autoCalculate(int row, int col, double price){
        float qtySmall;
        int i = 0;
        double smallestPrice = price / currMed.getRelationGroupId().get(row).getSmallestQty();

        //Need to simplify this section
        if(col == 3){
            for(PriceChangeUnitHis unit:listDetailUnit){
                if(i != row){
                    qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                    unit.setnPriceNew(smallestPrice * qtySmall);
                }
                
                i++;
            }
        }else if(col == 5){
            for(PriceChangeUnitHis unit:listDetailUnit){
                if(i != row){
                    qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                    unit.setaPriceNew(smallestPrice * qtySmall);
                }
                
                i++;
            }
        }else if(col == 7){
            for(PriceChangeUnitHis unit:listDetailUnit){
                if(i != row){
                    qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                    unit.setbPriceNew(smallestPrice * qtySmall);
                }
                
                i++;
            }
        }else if(col == 9){
            for(PriceChangeUnitHis unit:listDetailUnit){
                if(i != row){
                    qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                    unit.setcPriceNew(smallestPrice * qtySmall);
                }
                
                i++;
            }
        }else if(col == 11){
            for(PriceChangeUnitHis unit:listDetailUnit){
                if(i != row){
                    qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                    unit.setdPriceNew(smallestPrice * qtySmall);
                }
                
                i++;
            }
        }
        
        dataChange();
    }
}
