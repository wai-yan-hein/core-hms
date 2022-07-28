/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.inpatient.database.tempentity.DCVouSrvFilter;
import com.cv.app.inpatient.database.tempentity.DCVouSrvFilterKey;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
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
public class DCServiceFilterTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DCServiceFilterTableModel.class.getName());
    private List<DCVouSrvFilter> listVouSrvFilter = new ArrayList();
    private String[] columnNames = {"Description"};
    private AbstractDataAccess dao;
    private boolean insStatus = true; //Insert to the table
    private String modelOption;

    public DCServiceFilterTableModel(AbstractDataAccess dao, boolean insStatus, String modelOption) {
        this.dao = dao;
        this.insStatus = insStatus;
        this.modelOption = modelOption;
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        DCVouSrvFilter record = listVouSrvFilter.get(row);

        switch (column) {
            case 0: //Code
                if (record.getKey() == null) {
                    return null;
                } else {
                    return record.getKey().getService().getServiceName();
                }
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        DCVouSrvFilter record = listVouSrvFilter.get(row);

        try {
            switch (column) {
                case 0: //Description
                    if (value != null) {
                        setService((InpService) value, row);
                    }
                    break;
                default:
                    log.info("setValueAt : invalid index");
            }
        } catch (Exception ex) {
            log.error("setVaueAt : " + ex.toString());
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listVouSrvFilter.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listVouSrvFilter.isEmpty()) {
            return false;
        }

        DCVouSrvFilter record = listVouSrvFilter.get(listVouSrvFilter.size() - 1);

        if (record.getKey() != null) {
            return false;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        DCVouSrvFilter record = new DCVouSrvFilter();

        listVouSrvFilter.add(record);

        fireTableRowsInserted(listVouSrvFilter.size() - 1, listVouSrvFilter.size() - 1);
    }

    public void delete(int row) {
        DCVouSrvFilter record = listVouSrvFilter.get(row);

        if (record.getKey().getService() != null) {
            try {
                dao.delete(record);
                listVouSrvFilter.remove(row);
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }

                fireTableRowsDeleted(row, row);
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    public void setVouSrvFilter(DCVouSrvFilter record, int pos) {
        listVouSrvFilter.set(pos, record);
        fireTableDataChanged();

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
    }

    public void setService(InpService service, int row) {
        String userId = Global.machineId;
        final String TABLE = "com.cv.app.opd.database.tempentity.VouSrvFilter";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE key.service.serviceId = " + service.getServiceId();

        try {
            if (!JoSQLUtil.isAlreadyHave(strSQL, listVouSrvFilter)) {
                DCVouSrvFilter vsf = new DCVouSrvFilter(
                        new DCVouSrvFilterKey(modelOption, userId, service));

                if (insStatus) {
                    dao.open();
                    dao.save(vsf);
                }

                setVouSrvFilter(vsf, row);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate medicine code.",
                        "Duplicate", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("setService : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public List<DCVouSrvFilter> getListVouSrvFilter() {
        return listVouSrvFilter;
    }

    public void setListVouSrvFilter(List<DCVouSrvFilter> listVouSrvFilter) {
        this.listVouSrvFilter = listVouSrvFilter;
        fireTableDataChanged();
    }

    public String getFilterCodeStr() {
        String strTmp = null;

        for (DCVouSrvFilter code : listVouSrvFilter) {
            if (code.getKey() != null) {
                if (code.getKey().getService() != null) {
                    if (strTmp == null) {
                        strTmp = code.getKey().getService().getServiceId().toString();
                    } else {
                        strTmp = strTmp + "," + code.getKey().getService().getServiceId().toString();
                    }
                }
            }
        }

        return strTmp;
    }
}
