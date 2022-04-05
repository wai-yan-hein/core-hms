/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.ItemTypeMapping;
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
public class ItemTypeMappingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemTypeMappingTableModel.class.getName());
    private List<ItemTypeMapping> listLGM = new ArrayList();
    private final String[] columnNames = {"ItemType"};

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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listLGM == null) {
            return null;
        }

        if (listLGM.isEmpty()) {
            return null;
        }

        try {
            ItemTypeMapping item = listLGM.get(row);

            switch (column) {
                case 0: //Name
                    if (item.getKey() != null) {
                        if (item.getKey().getItemType() != null) {
                            return item.getKey().getItemType().getItemTypeName();
                        }
                    }
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listLGM == null) {
            return;
        }

        if (listLGM.isEmpty()) {
            return;
        }

        try {
            ItemTypeMapping item = listLGM.get(row);
            switch (column) {
                case 0: //Location
                    for (ItemTypeMapping tmp : listLGM) {
                        if (tmp.getKey() != null && value != null && tmp.getKey().getItemType() != null) {
                            if (tmp.getKey().getItemType().getItemTypeCode().equals(((ItemType) value).getItemTypeCode())) {
                                JOptionPane.showMessageDialog(Util1.getParent(),
                                        "Exists code.",
                                        "Item type Code", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                    if (value != null) {
                        item.getKey().setItemType((ItemType) value);
                    } else {
                        item.getKey().setItemType(null);
                    }
                    break;
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getMessage());
        }

        fireTableCellUpdated(row, column);
        addNewRow();
    }

    @Override
    public int getRowCount() {
        if (listLGM == null) {
            return 0;
        }
        return listLGM.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ItemTypeMapping> getListLGM() {
        return listLGM;
    }

    public void addNewRow() {
        if (listLGM != null && !listLGM.isEmpty()) {
            ItemTypeMapping lgm = listLGM.get(listLGM.size() - 1);
            if (lgm.getKey() != null) {
                if (lgm.getKey().getItemType() != null) {
                    listLGM.add(new ItemTypeMapping());
                    fireTableDataChanged();
                }
            }
        } else {
            listLGM = new ArrayList();
            listLGM.add(new ItemTypeMapping());
        }
    }

    public void setListLGM(List<ItemTypeMapping> listLGM) {
        this.listLGM = listLGM;
        addNewRow();
        fireTableDataChanged();
    }

    public ItemTypeMapping getItemTypeMapping(int row) {
        ItemTypeMapping lgm = null;
        if (listLGM != null) {
            if (!listLGM.isEmpty()) {
                if (row >= 0 && row < listLGM.size()) {
                    lgm = listLGM.get(row);
                }
            }
        }
        return lgm;
    }
}
