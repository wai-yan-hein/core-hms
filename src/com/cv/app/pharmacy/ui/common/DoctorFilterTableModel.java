/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.tempentity.DoctorFilter;
import com.cv.app.pharmacy.database.tempentity.DoctorFilterKey;
import com.cv.app.util.JoSQLUtil;
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
public class DoctorFilterTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DoctorFilterTableModel.class.getName());
    private List<DoctorFilter> listDoctorFilter = new ArrayList();
    private final String[] columnNames = {"Code", "Doctor Name"};
    private AbstractDataAccess dao;

    public DoctorFilterTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDoctorFilter == null) {
            return null;
        }

        if (listDoctorFilter.isEmpty()) {
            return null;
        }

        try {
            DoctorFilter record = listDoctorFilter.get(row);

            switch (column) {
                case 0: //Code
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getDoctorId().getDoctorId();
                    }
                case 1: //Trader Name
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getDoctorId().getDoctorName();
                    }
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

        switch (column) {
            case 0: //Code
                if (value != null) {
                    getDoctorInfo(value.toString(), row);
                }
                break;
            case 1: //Desp
                //record.setMedName(value.toString());
                break;
            default:
                System.out.println("invalid index");
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if (listDoctorFilter == null) {
            return 0;
        }
        return listDoctorFilter.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listDoctorFilter == null) {
            return false;
        }
        if (listDoctorFilter.isEmpty()) {
            return false;
        }

        DoctorFilter record = listDoctorFilter.get(listDoctorFilter.size() - 1);

        return record.getKey() == null;
    }

    public void addEmptyRow() {
        if (listDoctorFilter != null) {
            DoctorFilter record = new DoctorFilter();
            listDoctorFilter.add(record);
            fireTableRowsInserted(listDoctorFilter.size() - 1, listDoctorFilter.size() - 1);
        }
    }

    public void delete(int row) {
        try {
            DoctorFilter record = listDoctorFilter.get(row);
            if (record.getKey().getDoctorId() != null) {
                dao.delete(record);
                listDoctorFilter.remove(row);

                if (!hasEmptyRow()) {
                    addEmptyRow();
                }

                fireTableRowsDeleted(row, row);
            }
        } catch (Exception ex) {
            log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public void setDoctorFilter(DoctorFilter doctorFilter, int pos) {
        if(listDoctorFilter == null){
            return;
        }
        
        if(listDoctorFilter.isEmpty()){
            return;
        }
        
        listDoctorFilter.set(pos, doctorFilter);
        fireTableDataChanged();

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
    }

    public void getDoctorInfo(String doctorId, int row) {
        String userId = Global.loginUser.getUserId();
        final String TABLE = "com.cv.app.pharmacy.database.tempentity.DoctorFilter";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE key.doctorId.doctorId = '" + doctorId + "'";

        try {
            if (!JoSQLUtil.isAlreadyHave(strSQL, listDoctorFilter)) {
                dao.open();
                Doctor doctor = (Doctor) dao.find("Doctor", "doctorId = '"
                        + doctorId + "' and active = true");
                dao.close();

                if (doctor != null) {
                    DoctorFilter tf = new DoctorFilter(
                            new DoctorFilterKey(doctor, userId));
                    dao.save(tf);
                    setDoctorFilter(tf, row);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid doctor id.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate doctor code.",
                        "Duplicate", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getDoctorInfo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }
}
