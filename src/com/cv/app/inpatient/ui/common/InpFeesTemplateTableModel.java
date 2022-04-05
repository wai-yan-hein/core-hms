/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.InpFeesTemplate;
import com.cv.app.util.JoSQLUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class InpFeesTemplateTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(InpFeesTemplateTableModel.class.getName());
    private List<InpFeesTemplate> listFeesTemplate = new ArrayList();
    private final String[] columnNames = {"Fees Name"};
    private int groupId;

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
        if (listFeesTemplate == null) {
            return null;
        }

        if (listFeesTemplate.isEmpty()) {
            return null;
        }

        try {
            InpFeesTemplate record = listFeesTemplate.get(row);

            switch (column) {
                case 0: //Fees Name
                    return record.getTemplateName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listFeesTemplate == null) {
            return;
        }

        if (listFeesTemplate.isEmpty()) {
            return;
        }

        try {
            InpFeesTemplate record = listFeesTemplate.get(row);

            switch (column) {
                case 0: //Fees Name
                    if (value != null) {
                        record.setTemplateName(value.toString());
                        record.setGroupId(groupId);
                        addNewRow();
                    }
                    break;
            }

            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    @Override
    public int getRowCount() {
        if (listFeesTemplate == null) {
            return 0;
        }
        return listFeesTemplate.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<InpFeesTemplate> getListFeesTemplate() {
        if (listFeesTemplate == null) {
            return null;
        }
        String strSQL = "SELECT * FROM com.cv.app.inpatient.database.entity.InpFeesTemplate"
                + " WHERE groupId > 0";
        List<InpFeesTemplate> list = JoSQLUtil.getResult(strSQL, listFeesTemplate);

        return list;
    }

    public void setListFeesTemplate(List<InpFeesTemplate> listFeesTemplate) {
        this.listFeesTemplate = listFeesTemplate;
        addNewRow();
        fireTableDataChanged();
    }

    public void deleteFeesTemplate(int row) {
        if (listFeesTemplate != null) {
            if (!listFeesTemplate.isEmpty()) {
                listFeesTemplate.remove(row);
                fireTableRowsDeleted(0, listFeesTemplate.size() - 1);
            }
        }
    }

    private void addNewRow() {
        if(listFeesTemplate == null){
            return;
        }
        int count = listFeesTemplate.size();

        if (count == 0 || listFeesTemplate.get(count - 1).getGroupId() != null) {
            listFeesTemplate.add(new InpFeesTemplate());
        }

        fireTableRowsInserted(listFeesTemplate.size() + 1, listFeesTemplate.size() + 1);
    }

    public void setGroupId(int id) {
        groupId = id;
    }

    public void removeAll() {
        if(listFeesTemplate == null){
            return;
        }
        
        listFeesTemplate.removeAll(listFeesTemplate);
        addNewRow();

        fireTableDataChanged();
    }
}
