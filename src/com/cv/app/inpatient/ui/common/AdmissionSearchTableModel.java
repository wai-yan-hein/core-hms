/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class AdmissionSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AdmissionSearchTableModel.class.getName());
    private List<Ams> listAms = new ArrayList();
    private final String[] columnNames = {"Reg No.", "Date", "Admission No.", "Name", "Father Name",
        "Room No.", "City", "DC-Status", "DC-Date"};

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
        if (listAms != null) {
            if (!listAms.isEmpty()) {
                try {
                    Ams record = listAms.get(row);

                    if (record.getKey() == null) {
                        return null;
                    }

                    switch (column) {
                        case 0: //Reg No.
                            return record.getKey().getRegister().getRegNo();
                        case 1: //Date
                            return DateUtil.toDateStr(record.getAmsDate());
                        case 2: //Reg-No
                            return record.getKey().getAmsNo();
                        case 3: //Name
                            return record.getPatientName();
                        case 4: //Father Name
                            return record.getFatherName();
                        case 5: //Room No.
                            if (record.getBuildingStructure() != null) {
                                return record.getBuildingStructure().getDescription();
                            } else {
                                return null;
                            }
                        case 6: //City
                            if (record.getCity() != null) {
                                return record.getCity().getCityName();
                            } else {
                                return null;
                            }
                        case 7: //DC-Status
                            if (record.getDcStatus() != null) {
                                return record.getDcStatus().getStatusDesp();
                            } else {
                                return null;
                            }
                        case 8: //DC-Date
                            if (record.getDcDateTime() != null) {
                                if (record.getDcStatus() != null) {
                                    return DateUtil.toDateTimeStr(record.getDcDateTime(), "dd/MM/yyyy hh:mm:ss a");
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        default:
                            return null;
                    }
                } catch (Exception ex) {
                    log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listAms != null) {
            return listAms.size();
        } else {
            return 0;
        }

    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Ams> getListAms() {
        return listAms;
    }

    public void setListAms(List<Ams> listPatient) {
        this.listAms = listPatient;
        if (this.listAms != null) {
            fireTableDataChanged();
        }
    }

    public Ams getAms(int row) {
        if (listAms != null) {
            if (!listAms.isEmpty()) {
                return listAms.get(row);
            }
        }
        return null;
    }
}
