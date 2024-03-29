/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.DoctorFeesMappingOT;
import com.cv.app.ot.database.entity.OTDoctorFee;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTDoctorFeeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTDoctorFeeTableModel.class.getName());
    private List<OTDoctorFee> listDrFee = new ArrayList();
    private String[] columnNames = {"Doctor Name", "Fee"};
    private AbstractDataAccess dao;
    private JTable parent;
    private Integer serviceId = -1;

    public OTDoctorFeeTableModel(AbstractDataAccess dao, Integer serviceId) {
        this.dao = dao;
        this.serviceId = serviceId;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
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
            case 0: //Doctor Name
                return String.class;
            case 1: //Fee
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        OTDoctorFee record = listDrFee.get(row);

        switch (column) {
            case 0: //Doctor Name
                if (record.getDoctor() != null) {
                    return record.getDoctor();
                } else {
                    return null;
                }
            case 1: //Fees
                return record.getDrFee();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OTDoctorFee record = listDrFee.get(row);

        switch (column) {
            case 0: //Doctor
                if (value != null) {
                    Doctor dr = (Doctor) value;
                    record.setDoctor(dr);
                    Double drFee = getDrFee(dr.getDoctorId());
                    record.setDrFee(drFee);
                } else {

                }
                addNewRow();
                parent.setRowSelectionInterval(row, row);
                parent.setColumnSelectionInterval(column + 1, column + 1);
                break;
            case 1: //Fee
                if (value != null) {
                    record.setDrFee(NumberUtil.NZero(value));
                } else {
                    record.setDrFee(null);
                }
                parent.setRowSelectionInterval(row + 1, row + 1);
                parent.setColumnSelectionInterval(column - 1, column - 1);
                break;
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listDrFee.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OTDoctorFee> getListDrFee() {
        return listDrFee;
    }

    public void setListDrFee(List<OTDoctorFee> listDrFee) {
        this.listDrFee = listDrFee;
        addNewRow();
        fireTableDataChanged();
    }

    private void addNewRow() {
        if (listDrFee.isEmpty()) {
            listDrFee.add(new OTDoctorFee());
            fireTableDataChanged();
        } else {

            if (listDrFee.get(listDrFee.size() - 1).getDoctor() != null) {
                listDrFee.add(new OTDoctorFee());
                fireTableDataChanged();
            }
        }
    }

    public List<OTDoctorFee> getEntryDrFee() {
        String strSql = "SELECT * FROM com.cv.app.ot.database.entity.OTDoctorFee"
                + " WHERE doctor IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDrFee);
    }

    private Double getDrFee(String drId) {
        Double fee = null;

        try {
            List<DoctorFeesMappingOT> listDFM = dao.findAllHSQL(
                    "select o from DoctorFeesMappingOT o where o.drId = '" + drId
                    + "' and o.service.serviceId = " + serviceId.toString());
            if (listDFM != null) {
                if (listDFM.size() > 0) {
                    fee = listDFM.get(0).getFees();
                }
            }
        } catch (Exception ex) {
            log.error("getDrFee : " + ex.toString());
        } finally {
            dao.close();
        }

        return fee;
    }
}
