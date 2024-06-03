/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.util.NumberUtil;
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
public class RelationPriceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RelationPriceTableModel.class.getName());
    private List<RelationGroup> listRelationGroup = new ArrayList();
    private final String[] columnNames = {"Qty", "Unit", "Sale Price", "Sale Price A",
        "Sale Price B", "Sale Price C", "Sale Price D", "Sale Price E", "Barcode"};
    private JTable parent;
    private JCheckBox chkCalculate;
    private JTextField txtUnitRelation;
    private String deletedList;
    private boolean editable = true;
    private String unitSmallest;
    private String unitStr;

    public RelationPriceTableModel() {
        //this.listRelationGroup = listRelationGroup;
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
        if (column == 7 || column == 8) {
            return true;
        }
        return editable;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Unit Qty
                return Float.class;
            case 2: //Sale Price
            case 3: //Sale Price A
            case 4: //Sale Price B
            case 5: //Sale Price C
            case 6: //Sale Price D
                return Double.class;
            case 7: //Std-Cost
                return Float.class;
            case 8: //Unit Barcode
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listRelationGroup == null) {
            return null;
        }

        if (listRelationGroup.isEmpty()) {
            return null;
        }

        try {
            RelationGroup record = listRelationGroup.get(row);

            switch (column) {
                case 0: //Unit Qty
                    return record.getUnitQty();
                case 1: //Unit
                    return record.getUnitId();
                case 2: //Sale Price
                    return record.getSalePrice();
                case 3: //Sale Price A
                    return record.getSalePriceA();
                case 4: //Sale Price B
                    return record.getSalePriceB();
                case 5: //Sale Price C
                    return record.getSalePriceC();
                case 6: //Sale Price D
                    return record.getSalePriceD();
                case 7: //Std-Cost
                    return record.getStdCost();
                case 8: //Unit Barcode
                    return record.getUnitBarcode();
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
        if (listRelationGroup == null) {
            return;
        }

        if (listRelationGroup.isEmpty()) {
            return;
        }
        /*
         * String medId =
         * listDetailUnit.get(parent.getSelectedRow()).getMed().getMedId(); int x =
         * Util1.getParent().getX() + (Util1.getParent().getWidth()/2) + 50; int y =
         * Util1.getParent().getY() + (Util1.getParent().getHeight()/2) - 200;
         * UnitAutoCompleter unitPopup = null;
         */

        try {
            RelationGroup record = listRelationGroup.get(row);
            switch (column) {
                case 0: //Unit Qty
                    if (NumberUtil.isNumber(value)) {
                        record.setUnitQty(NumberUtil.NZeroFloat(value));
                        genRelCodeAndRelStr();

                        if (chkCalculate != null) {
                            if (!hasEmptyRow()) {
                                addEmptyRow();
                            }
                        }
                    }
                    break;
                case 1: //Unit
                    if (value instanceof ItemUnit) {
                        record.setUnitId((ItemUnit) value);
                        genRelCodeAndRelStr();

                        if (chkCalculate != null) {
                            if (!hasEmptyRow()) {
                                addEmptyRow();
                            }
                        }
                    }
                    break;
                case 2: //Sale Price
                    if (NumberUtil.isNumber(value)) {
                        record.setSalePrice(NumberUtil.NZero(value));
                    } else {
                        value = record.getSalePrice();
                    }
                    break;
                case 3: //Sale Price A
                    if (NumberUtil.isNumber(value)) {
                        record.setSalePriceA(NumberUtil.NZero(value));
                    } else {
                        value = record.getSalePriceA();
                    }
                    break;
                case 4: //Sale Price B
                    if (NumberUtil.isNumber(value)) {
                        record.setSalePriceB(NumberUtil.NZero(value));
                    } else {
                        value = record.getSalePriceB();
                    }
                    break;
                case 5: //Sale Price C
                    if (NumberUtil.isNumber(value)) {
                        record.setSalePriceC(NumberUtil.NZero(value));
                    } else {
                        value = record.getSalePriceC();
                    }
                    break;
                case 6: //Sale Price D
                    if (NumberUtil.isNumber(value)) {
                        record.setSalePriceD(NumberUtil.NZero(value));
                    } else {
                        value = record.getSalePriceD();
                    }
                    break;
                case 7: //Std-Cost
                    if (NumberUtil.isNumber(value)) {
                        record.setStdCost(NumberUtil.NZero(value));
                    } else {
                        value = record.getStdCost();
                    }
                    break;
                case 8: //Unit Barcode
                    if (value != null) {
                        record.setUnitBarcode(value.toString());
                    } else {
                        record.setUnitBarcode(null);
                    }
                default:
                    System.out.println("invalid index");
            }

            if (chkCalculate != null) {
                if (chkCalculate.isSelected() && column > 1) {
                    autoCalculate(row, column, NumberUtil.NZero(value), record.getSmallestQty());
                }
            }

        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if (listRelationGroup == null) {
            return 0;
        }
        return listRelationGroup.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void dataChange() {
        fireTableDataChanged();
    }

    private void autoCalculate(int row, int col, double price, float smallestQty) {
        float qtySmall;
        int i = 0;
        double smallestPrice = price / smallestQty;

        if (listRelationGroup != null) {
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
                        case 7: //Std Cost
                            unit.setStdCost(NumberUtil.roundTo(smallestPrice * qtySmall, 0));
                            break;
                    }
                }

                i++;
            }
        }

        dataChange();
    }

    private void genRelCodeAndRelStr() {
        String relStr = "";
        float smallestQty = 1;

        String strUnitSmallest = "";
        String strUnitStr = "";

        if (listRelationGroup != null) {
            for (int i = 0; i < listRelationGroup.size(); i++) {
                RelationGroup relationGroup = listRelationGroup.get(i);
                float unitQty = relationGroup.getUnitQty() == null ? 0 : relationGroup.getUnitQty();
                String strQty = "";

                if ((unitQty - Math.floor(unitQty)) == 0) {
                    Integer qty = (int) unitQty;
                    strQty = qty.toString();
                } else {
                    strQty = strQty + unitQty;
                }

                if (relationGroup.getUnitId() != null) {
                    smallestQty = smallestQty * unitQty;

                    if (relStr.length() > 0) {
                        relStr = relStr + "*" + strQty + relationGroup.getUnitId().getItemUnitCode();
                        strUnitStr = strUnitStr + "/" + relationGroup.getUnitId().getItemUnitCode();
                    } else {
                        relStr = strQty + relationGroup.getUnitId().getItemUnitCode();
                        strUnitStr = relationGroup.getUnitId().getItemUnitCode();
                    }
                }
            }

            for (int i = 0; i < listRelationGroup.size(); i++) {
                RelationGroup relationGroup = listRelationGroup.get(i);
                float unitQty = relationGroup.getUnitQty() == null ? 1 : relationGroup.getUnitQty();

                if (relationGroup.getUnitId() != null && (listRelationGroup.size() - 1) > i) {
                    smallestQty = smallestQty / unitQty;
                    relationGroup.setSmallestQty(smallestQty);
                    if (strUnitSmallest.isEmpty()) {
                        strUnitSmallest = String.valueOf(smallestQty);
                    } else {
                        strUnitSmallest = strUnitSmallest + "/" + String.valueOf(smallestQty);
                    }
                }
                /*else {
                    relationGroup.setSmallestQty(new Float(1));
                    if (strUnitSmallest.isEmpty()) {
                        strUnitSmallest = String.valueOf(1);
                    } else {
                        strUnitSmallest = strUnitSmallest + "/" + String.valueOf(1);
                    }
                }*/

                relationGroup.setRelUniqueId(i + 1);
            }

            txtUnitRelation.setText(relStr);
            unitSmallest = strUnitSmallest;
            unitStr = strUnitStr;
        }
    }

    public boolean hasEmptyRow() {
        if (listRelationGroup == null) {
            return false;
        }
        if (listRelationGroup.isEmpty()) {
            return false;
        }

        RelationGroup record = listRelationGroup.get(listRelationGroup.size() - 1);
        return !(record.getUnitId() != null || record.getUnitQty() != null);
    }

    public void addEmptyRow() {
        if (listRelationGroup != null) {
            RelationGroup record = new RelationGroup();
            listRelationGroup.add(record);
            fireTableRowsInserted(listRelationGroup.size() - 1, listRelationGroup.size() - 1);
        }
    }

    public void delete(int row) {
        if (listRelationGroup == null) {
            return;
        }

        if (listRelationGroup.isEmpty()) {
            return;
        }

        if (!editable) {
            return;
        }

        RelationGroup record = listRelationGroup.get(row);

        if (record != null) {
            if (NumberUtil.NZeroL(record.getRelGId()) > 0) {
                if (deletedList == null) {
                    deletedList = record.getRelGId();
                } else {
                    deletedList = deletedList + "," + record.getRelGId();
                }
            }

            listRelationGroup.remove(row);
            if (!hasEmptyRow()) {
                addEmptyRow();
            }

            fireTableRowsDeleted(row, row);
        }

        genRelCodeAndRelStr();
    }

    public void setListDetail(List<RelationGroup> listDetail) {
        if (listRelationGroup != null) {
            if (listDetail != null) {
                listRelationGroup.removeAll(listRelationGroup);
                listRelationGroup.addAll(listDetail);
            }
        } else {
            listRelationGroup.removeAll(listRelationGroup);

        }

        if (listRelationGroup.isEmpty() || chkCalculate != null) {
            if (!hasEmptyRow()) {
                addEmptyRow();
            }
        }

        fireTableDataChanged();
    }

    public List<RelationGroup> getDetail(String medId) {
        if (listRelationGroup != null) {
            RelationGroup relationGroup = listRelationGroup.get(listRelationGroup.size() - 1);
            if (relationGroup.getUnitId() == null) {
                listRelationGroup.remove(listRelationGroup.size() - 1);
            }
        }

        for(RelationGroup rg : listRelationGroup){
            if(rg.getMedId() == null){
                rg.setMedId(medId);
            }
            if(rg.getRelGId() == null || rg.getRelGId().isEmpty()){
                rg.setRelGId(medId + "-" + rg.getRelUniqueId());
            }
        }
        return listRelationGroup;
    }

    public List<RelationGroup> getRelationGroup() {
        return listRelationGroup;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from relation_group where rel_group_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getUnitSmallest() {
        if (unitSmallest == null) {
            genRelCodeAndRelStr();
        }
        return unitSmallest;
    }

    public void setUnitSmallest(String unitSmallest) {
        this.unitSmallest = unitSmallest;
    }

    public String getUnitStr() {
        if (unitSmallest == null) {
            genRelCodeAndRelStr();
        }
        return unitStr;
    }

    public void setUnitStr(String unitStr) {
        this.unitStr = unitStr;
    }
}
