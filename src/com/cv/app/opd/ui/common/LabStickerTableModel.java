/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LabStickerTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LabStickerTableModel.class.getName());
    private List<VoucherSearch> list = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No.", "Reg No.", "Adm", "Name"};

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
        switch (column) {
            case 0: //Date
                return String.class;
            case 1: //Vou No.
                return String.class;
            case 2: //Reg No.
                return String.class;
            case 3: //Admission
                return String.class;
            case 4: //Name
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VoucherSearch record = list.get(row);

        switch (column) {
            case 0: //Date
                return DateUtil.toDateStr(record.getTranDate());
            case 1: //Vou No.
                return record.getInvNo();
            case 2: //Reg No
                return record.getCusNo();
            case 3: //Admission
                return record.getRefNo();
            case 4: //Name
                return record.getCusName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getList() {
        return list;
    }

    public void setList(List<VoucherSearch> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public VoucherSearch getSelectedRecord(int row){
        return list.get(row);
    }
}
