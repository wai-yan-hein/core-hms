/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PriceChangeUnitHis1;
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
public class PChangeUnitTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(PChangeUnitTableModel1.class.getName());
    private final List<PriceChangeUnitHis1> listDetailUnit;
    private final String[] columnNames = {"Unit", "Old", "New", "Old", "New",
        "Old", "New", "Old", "New", "Old", "New", "Old", "New"};
    private JTable parent;
    private Medicine currMed;
    private JCheckBox chkCalculate;

    public PChangeUnitTableModel1(List<PriceChangeUnitHis1> listDetailUnit) {
        this.listDetailUnit = listDetailUnit;
    }

    public void setCurrMed(Medicine currMed) {
        this.currMed = currMed;
    }

    public void setAutoCalculate(JCheckBox chkCalculate) {
        this.chkCalculate = chkCalculate;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 0 || column == 1 || column == 3 || column == 5
                || column == 7 || column == 9);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Unit
                return String.class;
            case 1: //Price N old
            case 2: //Price N new
            case 3: //Price A old
            case 4: //Price A new
            case 5: //Price B old
            case 6: //Price B new
            case 7: //Price C old
            case 8: //Price C new
            case 9://Price D old
            case 10://Price D new
            case 11://Price E old
            case 12://Price E New
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listDetailUnit == null){
            return null;
        }
        
        if(listDetailUnit.isEmpty()){
            return null;
        }
        
        PriceChangeUnitHis1 record = listDetailUnit.get(row);

        switch (column) {
            case 0: //Unit
                if (record.getPcUnit() == null) {
                    return null;
                } else {
                    return String.format("%,.0f", record.getCostPrice()) + " " + 
                            record.getPcUnit().getItemUnitCode();
                }
            case 1: //Price N old
                return record.getnPriceOld();
            case 2: //Price N new
                return record.getnPriceNew();
            case 3: //Price A old
                return record.getaPriceOld();
            case 4: //Price A new
                return record.getaPriceNew();
            case 5: //Price B old
                return record.getbPriceOld();
            case 6: //Price B new
                return record.getbPriceNew();
            case 7: //Price C old
                return record.getcPriceOld();
            case 8: //Price C new
                return record.getcPriceNew();
            case 9: //Price D old
                return record.getdPriceOld();
            case 10: //Price D new
                return record.getdPriceNew();
            case 11: //Price E old
                return record.getePriceOld();
            case 12: //Price E New
                return record.getePriceNew();
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if(listDetailUnit == null){
            return;
        }
        
        if(listDetailUnit.isEmpty()){
            return;
        }
        
        PriceChangeUnitHis1 record = listDetailUnit.get(row);
        /*String medId = listDetailUnit.get(parent.getSelectedRow()).getMed().getMedId();
        int x = Util1.getParent().getX() + (Util1.getParent().getWidth()/2) + 50;
        int y = Util1.getParent().getY() + (Util1.getParent().getHeight()/2) - 200;
        UnitAutoCompleter unitPopup = null;*/

        try {
            switch (column) {
                case 0: //Unit
                    //record.setPcUnit((ItemUnit)value);
                    break;
                case 1: //Price N old
                    //record.setnPriceOld((Double)value);
                    break;
                case 2: //Price N new
                    String tmpNStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setnPriceNew(NumberUtil.NZero(tmpNStr));
                    break;
                case 3: //Price A old
                    //record.setaPriceOld((Double)value);
                    break;
                case 4: //Price A new
                    String tmpAStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setaPriceNew(NumberUtil.NZero(tmpAStr));
                    break;
                case 5: //Price B old
                    //record.setbPriceOld((Double)value);
                    break;
                case 6: //Price B new
                    String tmpBStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setbPriceNew(NumberUtil.NZero(tmpBStr));
                    break;
                case 7: //Price C old
                    //record.setcPriceOld((Double)value);
                    break;
                case 8: //Price C new
                    String tmpCStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setcPriceNew(NumberUtil.NZero(tmpCStr));
                    break;
                case 9: //Price D old
                    //record.setdPriceOld((Double)value);
                    break;
                case 10: //Price D new
                    String tmpDStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setdPriceNew(NumberUtil.NZero(tmpDStr));
                    break;
                case 11: //Price E old
                    record.setePriceOld(NumberUtil.NZero(value));
                    break;
                case 12: //Price E new
                    String tmpEStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setePriceNew(NumberUtil.NZero(tmpEStr));
                    break;
                default:
                    System.out.println("invalid index");
            }

            /*if (chkCalculate.isSelected() && column > 0) {
                autoCalculate(row, column, NumberUtil.NZero(value));
            }*/

            if(row == 0 || chkCalculate.isSelected()){
                autoCalculate(row, column, NumberUtil.NZero(value));
            }
            //calculateAmount(row);
            //fireTableCellUpdated(row, 9);
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        if (column != 0) {
            fireTableCellUpdated(row, column);
        }
    }

    @Override
    public int getRowCount() {
        return listDetailUnit.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void dataChange() {
        fireTableDataChanged();
    }

    private void autoCalculate(int row, int col, double price) {
        float qtySmall;
        int i = 0;
        double smallestPrice = 0;
        if (NumberUtil.NZero(currMed.getRelationGroupId().get(row).getSmallestQty()) != 0) {
            smallestPrice = price / currMed.getRelationGroupId().get(row).getSmallestQty();
        }

        //Need to simplify this section
        switch (col) {
            case 2:
                for (PriceChangeUnitHis1 unit : listDetailUnit) {
                    if (i != row) {
                        qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                        unit.setnPriceNew(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
                    }
                    fireTableCellUpdated(i, col);
                    i++;
                }
                break;
            case 4:
                for (PriceChangeUnitHis1 unit : listDetailUnit) {
                    if (i != row) {
                        qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                        unit.setaPriceNew(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
                    }
                    fireTableCellUpdated(i, col);
                    i++;
                }
                break;
            case 6:
                for (PriceChangeUnitHis1 unit : listDetailUnit) {
                    if (i != row) {
                        qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                        unit.setbPriceNew(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
                    }
                    fireTableCellUpdated(i, col);
                    i++;
                }
                break;
            case 8:
                for (PriceChangeUnitHis1 unit : listDetailUnit) {
                    if (i != row) {
                        qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                        unit.setcPriceNew(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
                    }
                    fireTableCellUpdated(i, col);
                    i++;
                }
                break;
            case 10:
                for (PriceChangeUnitHis1 unit : listDetailUnit) {
                    if (i != row) {
                        qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                        unit.setdPriceNew(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
                    }
                    fireTableCellUpdated(i, col);
                    i++;
                }
                break;
            case 12:
                for (PriceChangeUnitHis1 unit : listDetailUnit) {
                    if (i != row) {
                        qtySmall = currMed.getRelationGroupId().get(i).getSmallestQty();
                        unit.setePriceNew(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
                    }
                    fireTableCellUpdated(i, col);
                    i++;
                }
                break;
            default:
                break;
        }

        //dataChange();
    }
}
