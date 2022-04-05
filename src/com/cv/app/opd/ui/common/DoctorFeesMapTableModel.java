/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.DoctorFeesMapping;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
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
public class DoctorFeesMapTableModel extends AbstractTableModel {

    private List<DoctorFeesMapping> listDoctorFeesMapping = new ArrayList();
    private final String[] columnNames = {"Service", "Fees"};
    private final AbstractDataAccess dao;
    private String deletedList;

    public DoctorFeesMapTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }

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
        switch (column) {
            case 0: //Service Description
                return String.class;
            case 1: //Fees
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        DoctorFeesMapping record = listDoctorFeesMapping.get(row);

        switch (column) {
            case 0: //Description
                if (record.getService() != null) {
                    return record.getService().getServiceName();
                } else {
                    return null;
                }
            case 1: //Fees
                return record.getFees();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        DoctorFeesMapping record = listDoctorFeesMapping.get(row);

        switch (column) {
            case 0: //Description
                if (value != null) {
                    if (value instanceof Service) {
                        Service service = (Service) value;
                        record.setService(service);
                    }
                }
                addNewRow();
                break;
            case 1: //Fees
                if (NumberUtil.isNumber(value)) {
                    record.setFees(NumberUtil.NZero(value));
                } else {
                }
                break;
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listDoctorFeesMapping.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<DoctorFeesMapping> getListDoctorFeesMapping() {
        String strSql = "SELECT * FROM com.cv.app.opd.database.entity.DoctorFeesMapping"
                + " WHERE service IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDoctorFeesMapping);
    }

    public void setListDoctorFeesMapping(List<DoctorFeesMapping> listDoctorFeesMapping) {
        this.listDoctorFeesMapping = listDoctorFeesMapping;
        addNewRow();
        fireTableDataChanged();
    }

    public DoctorFeesMapping getDoctorFeesMapping(int row) {
        return listDoctorFeesMapping.get(row);
    }

    public void setDoctorFeesMapping(int row, DoctorFeesMapping doctorFeesMapping) {
        listDoctorFeesMapping.set(row, doctorFeesMapping);
        fireTableRowsUpdated(row, row);
    }

    public void addDoctorFeesMapping(DoctorFeesMapping doctorFeesMapping) {
        listDoctorFeesMapping.add(doctorFeesMapping);
        fireTableRowsInserted(listDoctorFeesMapping.size() - 1, listDoctorFeesMapping.size() - 1);
    }

    public void deleteDoctorFeesMapping(int row) {
        DoctorFeesMapping dfm = listDoctorFeesMapping.get(row);

        if (NumberUtil.NZero(dfm.getMapId()) > 0) {
            if (deletedList == null) {
                deletedList = dfm.getMapId().toString();
            } else {
                deletedList = deletedList + "," + dfm.getMapId().toString();
            }
        }

        listDoctorFeesMapping.remove(row);
        fireTableRowsDeleted(0, listDoctorFeesMapping.size() - 1);
    }

    public void addNewRow() {
        int count = listDoctorFeesMapping.size();

        if (count == 0 || listDoctorFeesMapping.get(count - 1).getService() != null) {
            listDoctorFeesMapping.add(new DoctorFeesMapping());
        }
    }

    public void clear() {
        deletedList = null;
        listDoctorFeesMapping.removeAll(listDoctorFeesMapping);
        addNewRow();
        fireTableDataChanged();
    }

    public String getDeletedList() {
        return deletedList;
    }

    public boolean isValidEntry() {
        boolean status = true;
        int row = 0;

        for (DoctorFeesMapping dfm : listDoctorFeesMapping) {
            if (row < listDoctorFeesMapping.size() - 1 && status) {
                if (NumberUtil.NZeroInt(dfm.getService()) == null){
                    JOptionPane.showMessageDialog(Util1.getParent(), "Please select service.",
                            "No Service Define", JOptionPane.ERROR_MESSAGE);
                    status = false;
                }else if(NumberUtil.NZero(dfm.getFees()) <= 0){
                    JOptionPane.showMessageDialog(Util1.getParent(), "Price must be positive value.",
                            "Minus or zero price.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                }else{
                    dfm.setUniqueId(row+1);
                }
            }
            
            row++;
        }

        return status;
    }
}
