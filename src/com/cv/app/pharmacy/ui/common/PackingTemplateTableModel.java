/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.PackingTemplateDetail;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PackingTemplateTableModel extends AbstractTableModel {

    private List<PackingTemplateDetail> listPackingTemplate = new ArrayList();
    private String[] columnNames = {"Unit Qty", "Unit"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 0) {
            return Integer.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PackingTemplateDetail record = listPackingTemplate.get(row);

        switch (column) {
            case 0: //Unit Qty
                return record.getUnitQty();
            case 1: //Unit
                if (record.getItemUnit() == null) {
                    return null;
                } else {
                    return record.getItemUnit().getItemUnitCode();
                }
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        PackingTemplateDetail record = listPackingTemplate.get(row);

        switch (column) {
            case 0: //Unit Qty
                float qty = NumberUtil.NZeroInt(value);
                if (row == 0 && column == 0 && (qty > 1 || qty < 1)) {
                    qty = 1;
                }
                record.setUnitQty(qty);
                break;
            case 1: //Unit
                if (value != null && value instanceof ItemUnit) {
                    ItemUnit itemUnit = (ItemUnit) value;
                    if (!isDublicateUnit(itemUnit.getItemUnitCode())) {
                        record.setItemUnit(itemUnit);
                        addNewRow();
                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Unit is already had in template.",
                                "Duplicate Unit", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listPackingTemplate.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PackingTemplateDetail> getListTemplate() {
        String className = "com.cv.app.pharmacy.database.entity.PackingTemplateDetail";
        String strSql = "SELECT * FROM " + className + " WHERE unitQty > 0 AND itemUnit IS NOT NULL";
        List<PackingTemplateDetail> list = JoSQLUtil.getResult(strSql, listPackingTemplate);

        return list;
    }

    public void setListTemplate(List<PackingTemplateDetail> listPackingTemplate) {
        this.listPackingTemplate.removeAll(this.listPackingTemplate);
        this.listPackingTemplate.addAll(listPackingTemplate);
        fireTableDataChanged();
        addNewRow();
    }

    public void addNewRow() {
        if (!isHasBlankRow()) {
            listPackingTemplate.add(new PackingTemplateDetail());
            int pos = listPackingTemplate.size() - 1;
            fireTableRowsInserted(pos, pos);
        }
    }

    private boolean isHasBlankRow() {
        boolean status = true;

        if (listPackingTemplate.isEmpty()) {
            status = false;
        } else if (NumberUtil.NZeroInt(listPackingTemplate.get(
                listPackingTemplate.size() - 1).getUnitQty()) == 0) {
            status = false;
        }

        return status;
    }

    public void removeAll() {
        listPackingTemplate.removeAll(listPackingTemplate);
        fireTableDataChanged();
    }

    public void deleteRow(int row) {
        if (row < listPackingTemplate.size() - 1) {
            listPackingTemplate.remove(row);
            fireTableRowsDeleted(0, listPackingTemplate.size());
        }
    }

    public boolean isValidEntry() {
        boolean status = true;

        if (listPackingTemplate.size() == 1) {
            JOptionPane.showMessageDialog(Util1.getParent(),
                            "No data.",
                            "Invalid relation", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            for (int i = 0; i < listPackingTemplate.size() - 1; i++) {
                PackingTemplateDetail ptd = listPackingTemplate.get(i);

                if (NumberUtil.NZero(ptd.getUnitQty()) < 1) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Quantity must be grater then zero.",
                            "Invalid quantity.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    i = listPackingTemplate.size();
                } else if (ptd.getItemUnit() == null) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid unit.",
                            "Unit null.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    i = listPackingTemplate.size();
                } else {
                    ptd.setUniqueId(i);
                }
            }
        }

        return status;
    }

    public String getRelCodeAndRelStr() {
        String relStr = "";
        float smallestQty = 1;

        for (int i = 0; i < listPackingTemplate.size(); i++) {
            PackingTemplateDetail packingTemplate = listPackingTemplate.get(i);
            float unitQty = packingTemplate.getUnitQty() == null ? 0 : packingTemplate.getUnitQty();

            if (packingTemplate.getItemUnit() != null) {
                if (relStr.length() > 0) {
                    relStr = relStr + "*" + unitQty + packingTemplate.getItemUnit().getItemUnitCode();
                } else {
                    relStr = unitQty + packingTemplate.getItemUnit().getItemUnitCode();
                }

                smallestQty = smallestQty * unitQty;
            }
        }

        System.out.println("Smallest : " + smallestQty);

        for (int i = 0; i < listPackingTemplate.size(); i++) {
            PackingTemplateDetail packingTemplate = listPackingTemplate.get(i);
            float unitQty = packingTemplate.getUnitQty() == null ? 1 : packingTemplate.getUnitQty();

            if (packingTemplate.getItemUnit() != null && (listPackingTemplate.size() - 1) > i) {
                smallestQty = smallestQty / unitQty;
                packingTemplate.setSmallestQty(smallestQty);
            } else {
                packingTemplate.setSmallestQty(new Float(1));
            }

            packingTemplate.setUniqueId(i + 1);

            if (packingTemplate.getItemUnit() != null) {
                System.out.println(packingTemplate.getSmallestQty() + " "
                        + packingTemplate.getItemUnit().getItemUnitCode());
            }
        }

        return relStr;
    }

    public PackingTemplateDetail getTemplateDetail(int row) {
        if (row >= 0 && row < listPackingTemplate.size()) {
            return listPackingTemplate.get(row);
        } else {
            return null;
        }
    }

    private boolean isDublicateUnit(String unitId) {
        boolean status = false;
        String className = "com.cv.app.pharmacy.database.entity.PackingTemplateDetail";
        String strSql = "SELECT * FROM " + className + " WHERE itemUnit.itemUnitCode = '"
                + unitId + "'";

        if (JoSQLUtil.getResult(strSql, listPackingTemplate).size() > 0) {
            status = true;
        }

        return status;
    }
}
