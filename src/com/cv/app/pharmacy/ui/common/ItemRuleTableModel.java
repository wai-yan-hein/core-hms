/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.entity.ItemRule;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ItemRuleTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemRuleTableModel.class.getName());
    private List<ItemRule> listItemRule = new ArrayList();
    private final String[] columnNames = {"Des", "S Date", "E Date", "Pri/Qty", "SQty", "Unit", "EQty", "Unit",
        "Price", "Qty", "Unit", "ProQty", "Unit"};

    private JTable parent;
    private JCheckBox chkCalculate;
    private JTextField txtUnitRelation;
    private String deletedList;
    private boolean editable = true;
    private MedicineUP medUp;
    private SelectionObserver observer;
    protected Medicine currMedicine = new Medicine();
    private String medId;

    public ItemRuleTableModel(MedicineUP medUp, SelectionObserver observer) {
        //this.listRelationGroup = listRelationGroup;
        this.medUp = medUp;
        this.observer = observer;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setAutoCalculate(JCheckBox chkCalculate) {
        this.chkCalculate = chkCalculate;
    }

    public void setRelStrControl(JTextField txtUnitRelation) {
        this.txtUnitRelation = txtUnitRelation;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0: //Desc
                return true;
            case 1: //Start Date
                return true;
            case 2: //end Date
                return true;
            case 3: //Check qty
                return true;
            case 4: //start qty
                return true;
            case 5: //start qty unit
                return false;
            case 6: //end qty
                return true;
            case 7: //end qty unit
                return false;
            case 8: //price
                return true;
            case 9: //qty
                return true;
            case 10: //qty unit
                return false;
            case 11: //promoqty
                return true;
            case 12:
                return false;
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: // Desc
            case 1: //start date
            case 2: //end date;
                return String.class;
            case 3: // check Qty price
            case 4: //start Qty
                return Double.class;
            case 5: //sq item unit
                return String.class;
            case 6: //end qty
                return Double.class;
            case 7: //eq item unit
                return String.class;
            case 8: //pirce
            case 9: //Qty
                return Double.class;
            case 10: //item unit
                return String.class;
            case 11: //promo qty
                return Double.class;
            case 12: // promo item unit
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listItemRule == null) {
            return null;
        }

        if (listItemRule.isEmpty()) {
            return null;
        }

        try {
            ItemRule record = listItemRule.get(row);
            switch (column) {
                case 0: //Description
                    return record.getDescription();
                case 1: //Start Date
                    if (record.getStartDate() != null) {
                        return DateUtil.toDateStr(record.getStartDate(), "dd/MM/yyyy");
                    } else {
                        return null;
                    }
                case 2: //End Date
                    if (record.getEndDate() != null) {
                        return DateUtil.toDateStr(record.getEndDate(), "dd/MM/yyyy");
                    } else {

                    }
                case 3: // Price/Qty
                    return record.getChekcQtyPrice();
                case 4: // Start Qty
                    return record.getStartQty();
                case 5:
                    return record.getSqItemUnit();
                case 6: // End Qty
                    return record.getEndQty();
                case 7:
                    return record.getEqItemUnit();
                case 8: // Price
                    return record.getPrice();
                case 9: // Qty
                    return record.getQty();
                case 10:
                    return record.getItemUnit();
                case 11: // promo Qty
                    return record.getProQty();
                case 12:
                    return record.getPqItemUnit();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValue : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }

        return new Object();
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listItemRule == null) {
            return;
        }

        if (listItemRule.isEmpty()) {
            return;
        }

        try {
            ItemRule record = listItemRule.get(row);
            switch (column) {
                case 0: //Description
                    if (!((String) value).isEmpty()) {
                        record.setDescription(value.toString());
                        record.setMed_id(medId);
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                            ItemRule newRecord = listItemRule.get(row + 1);
                            newRecord.setMed_id(medId);
                        }
                    }
                    break;

                case 1: //s Date
                    if (value != null) {
                        record.setStartDate(DateUtil.toDate(value, "dd/MM/yyyy"));
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }
                    }
                    break;
                case 2: //E Date
                    if (value != null) {
                        record.setEndDate(DateUtil.toDate(value, "dd/MM/yyyy"));
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }
                    }
                    break;
                case 3: // check Qty Price
                    if (NumberUtil.isNumber(value)) {
                        record.setChekcQtyPrice(NumberUtil.NZero(value));
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }
                    }
                    break;
                case 4: //Start Qty
                    callUnitBox(record, value, 5, 5);
                    break;
                case 5:
                    if (!((String) value).isEmpty()) {
                        record.setSqItemUnit(value.toString());
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }
                    }
                    break;
                case 6: //End Qty
                    callUnitBox(record, value, 7, 7);
                    break;
                case 7:
                    if (!((String) value).isEmpty()) {
                        record.setEqItemUnit(value.toString());
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }
                    }
                    break;
                case 8: //price
                    if (NumberUtil.isNumber(value)) {
                        record.setPrice(NumberUtil.NZero(value));
                    } else {
                        //value = record.getPrice();
                    }
                    break;
                case 9: // Qty
                    callUnitBox(record, value, 10, 10);
                    break;
                case 10:
                    if (!((String) value).isEmpty()) {
                        record.setItemUnit(value.toString());
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }
                    }
                    break;
                case 11: //Promo Qty
                    callUnitBox(record, value, 12, 12);
                    break;
                case 12:
                    if (!((String) value).isEmpty()) {
                        record.setSqItemUnit(value.toString());
                        //genRelCodeAndRelStr();

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }
                    }
                    break;
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
        if (listItemRule == null) {
            return 0;
        }
        return listItemRule.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void dataChange() {
        fireTableDataChanged();
    }

    public void setMedID(String medID) {
        medId = medID;
    }

    void callUnitBox(ItemRule record, Object value, int xcol, int ycol) {
        float qty = NumberUtil.NZeroFloat(value);
        parent.setColumnSelectionInterval(xcol, ycol);
        if (xcol == 5) {
            record.setStartQty(qty);
        } else if (xcol == 7) {
            record.setEndQty(qty);
        } else if (xcol == 10) {
            record.setQty(qty);
        } else if (xcol == 12) {
            record.setProQty(qty);
        }
        if (listItemRule != null) {
            if (listItemRule.get(parent.getSelectedRow()).getMed_id() != null) {
                String lmedId = listItemRule.get(parent.getSelectedRow()).getMed_id();

                if (medUp.getUnitList(lmedId) != null) {

                    ItemUnit iu = medUp.getSmallestUnit(lmedId);
                    String key = lmedId + "-" + iu.getItemUnitCode();

                    if (medUp.getUnitList(lmedId).size() > 1) {
                        int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
                        int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
                        UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                medUp.getUnitList(lmedId), Util1.getParent());

                        if (unitPopup.isSelected()) {
                            if (xcol == 5) {
                                record.setSqItemUnit(unitPopup.getSelUnit().toString());
                                record.setSqSmallestQty(record.getStartQty() * medUp.getQtyInSmallest(key));

                            } else if (xcol == 7) {
                                record.setEqItemUnit(unitPopup.getSelUnit().toString());
                                record.setEqSmallestQty(record.getEndQty() * medUp.getQtyInSmallest(key));
                            } else if (xcol == 10) {
                                record.setItemUnit(unitPopup.getSelUnit().toString());
                                record.setSmallestQty(record.getQty() * medUp.getQtyInSmallest(key));
                            } else if (xcol == 12) {
                                record.setPqItemUnit(unitPopup.getSelUnit().toString());
                                record.setPqSmallestQty(record.getProQty() * medUp.getQtyInSmallest(key));
                            }
                            if (xcol != 12) {
                                parent.setColumnSelectionInterval(xcol + 1, ycol + 1);
                            }
                        }
                    } else {
                        if (xcol == 5) {
                            record.setSqItemUnit(medUp.getUnitList(lmedId).get(0).toString());
                            record.setSqSmallestQty(record.getStartQty() * medUp.getQtyInSmallest(key));
                        } else if (xcol == 7) {
                            record.setEqItemUnit(medUp.getUnitList(lmedId).get(0).toString());
                            record.setEqSmallestQty(record.getEndQty() * medUp.getQtyInSmallest(key));
                        } else if (xcol == 10) {
                            record.setItemUnit(medUp.getUnitList(lmedId).get(0).toString());
                            record.setSmallestQty(record.getQty() * medUp.getQtyInSmallest(key));
                        } else if (xcol == 12) {
                            record.setPqItemUnit(medUp.getUnitList(lmedId).get(0).toString());
                            record.setPqSmallestQty(record.getProQty() * medUp.getQtyInSmallest(key));
                        }
                    }
                }
            }
        }
        //observer.selected("SaleQtyUpdate", record);
    }

    /*  
     private void autoCalculate(int row, int col, double price, float smallestQty) {
     float qtySmall;
     int i = 0;
     double smallestPrice = price / smallestQty;

     for (RelationGroup unit : listRelationGroup) {
     if (i != row && i < (listRelationGroup.size() - 1)) {
     qtySmall = unit.getSmallestQty();

     switch (col) {
     case 2: //Sale Price
     unit.setSalePrice(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
     break;
     case 3: //Sale Price A
     unit.setSalePriceA(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
     break;
     case 4: //Sale Price B
     unit.setSalePriceB(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
     break;
     case 5: //Sale Price C
     unit.setSalePriceC(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
     break;
     case 6: //Sale Price D
     unit.setSalePriceD(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
     break;
     }
     }

     i++;
     }

     dataChange();
     }

     private void genRelCodeAndRelStr() {
     String relStr = "";
     float smallestQty = 1;

     for (int i = 0; i < listRelationGroup.size(); i++) {
     RelationGroup relationGroup = listRelationGroup.get(i);
     float unitQty = relationGroup.getUnitQty() == null ? 0 : relationGroup.getUnitQty();

     if (relationGroup.getUnitId() != null) {
     if (relStr.length() > 0) {
     relStr = relStr + "*" + unitQty + relationGroup.getUnitId().getItemUnitCode();
     } else {
     relStr = unitQty + relationGroup.getUnitId().getItemUnitCode();
     }

     smallestQty = smallestQty * unitQty;
     }
     }

     for (int i = 0; i < listRelationGroup.size(); i++) {
     RelationGroup relationGroup = listRelationGroup.get(i);
     float unitQty = relationGroup.getUnitQty() == null ? 1 : relationGroup.getUnitQty();

     if (relationGroup.getUnitId() != null && (listRelationGroup.size() - 1) > i) {
     smallestQty = smallestQty / unitQty;
     relationGroup.setSmallestQty(smallestQty);
     } else {
     relationGroup.setSmallestQty(new Float(1));
     }

     relationGroup.setRelUniqueId(i + 1);
     }

     txtUnitRelation.setText(relStr);
     }*/
    public boolean hasEmptyRow() {
        if (listItemRule == null) {
            return false;
        }
        if (listItemRule.isEmpty()) {
            return false;
        }

        ItemRule record = listItemRule.get(listItemRule.size() - 1);
        return !(record.getDescription() != null && record.getMed_id() != null);
    }

    public void addEmptyRow() {
        if (listItemRule != null) {
            ItemRule record = new ItemRule();
            listItemRule.add(record);
            fireTableRowsInserted(listItemRule.size() - 1, listItemRule.size() - 1);
        }
    }

    public void delete(int row) {
        if (listItemRule != null) {
            ItemRule record = listItemRule.get(row);

            if (record != null) {
                if (deletedList == null) {
                    deletedList = record.getRuleId().toString();
                } else {
                    deletedList = deletedList + "," + record.getRuleId().toString();
                }
            }

            listItemRule.remove(row);
            if (!hasEmptyRow()) {
                addEmptyRow();
            }

            fireTableRowsDeleted(row, row);
        }
    }

    public void setListDetail(List<ItemRule> listDetail) {
        if (listItemRule != null) {
            listItemRule.removeAll(listItemRule);
            if (!listDetail.isEmpty()) {
                listItemRule.addAll(listDetail);
            }

            if (!hasEmptyRow()) {
                addEmptyRow();
            }

            fireTableDataChanged();
        }
    }

    public List<ItemRule> getDetail() {
        if (listItemRule != null) {
            ItemRule relationGroup = listItemRule.get(listItemRule.size() - 1);

            if (relationGroup.getRuleId() == null) {
                listItemRule.remove(listItemRule.size() - 1);
            }
        }

        return listItemRule;
    }

    public List<ItemRule> getItemRule() {
        return listItemRule;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from item_rule where item_rule_no in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public ItemRule getItemRule(int row) {
        if(listItemRule == null){
            return null;
        }
        
        if (listItemRule.size() > row) {
            return listItemRule.get(row);
        } else {
            return null;
        }
    }
}
