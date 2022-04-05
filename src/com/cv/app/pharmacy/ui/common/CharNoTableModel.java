/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.CharacterNo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CharNoTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CharNoTableModel.class.getName());
    private List<CharacterNo> listCharacterNo = new ArrayList<CharacterNo>();
    private final String[] columnNames = {"Char", "No"};

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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listCharacterNo == null) {
            return null;
        }

        if (listCharacterNo.isEmpty()) {
            return null;
        }

        try {
            CharacterNo characterNo = listCharacterNo.get(row);

            switch (column) {
                case 0: //Char
                    return characterNo.getCh();
                case 1: //No
                    return characterNo.getStrNumber();
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
    }

    @Override
    public int getRowCount() {
        if (listCharacterNo == null) {
            return 0;
        }
        return listCharacterNo.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<CharacterNo> getListCharacterNo() {
        return listCharacterNo;
    }

    public void setListCharacterNo(List<CharacterNo> listCharacterNo) {
        this.listCharacterNo = listCharacterNo;
        fireTableDataChanged();
    }

    public CharacterNo getCharNo(int row) {
        if (listCharacterNo != null) {
            if (!listCharacterNo.isEmpty()) {
                return listCharacterNo.get(row);
            }
        }
        return null;
    }

    public void setCharNo(int row, CharacterNo characterNo) {
        if (listCharacterNo != null) {
            if (!listCharacterNo.isEmpty()) {
                listCharacterNo.set(row, characterNo);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addCharNo(CharacterNo characterNo) {
        if (listCharacterNo != null) {
            listCharacterNo.add(characterNo);
            fireTableRowsInserted(listCharacterNo.size() - 1, listCharacterNo.size() - 1);
        }
    }

    public void deleteCharNo(int row) {
        if (listCharacterNo != null) {
            if (!listCharacterNo.isEmpty()) {
                listCharacterNo.remove(row);
                fireTableRowsDeleted(0, listCharacterNo.size() - 1);
            }
        }
    }
}
