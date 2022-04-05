/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RptDCServiceFilterTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RptDCServiceFilterTableModel.class.getName());
    private final List<InpService> listService = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};
    private JTable parent;

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
        if (listService == null) {
            return null;
        }

        if (listService.isEmpty()) {
            return null;
        }

        try {
            InpService record = listService.get(row);

            switch (column) {
                case 0: //Code
                    return record.getServiceCode();
                case 1: //Description
                    return record.getServiceName();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        switch (column) {
            case 0: //Code
                if (value != null) {
                    setService((InpService) value, row);
                }
                break;
            default:
                log.info("setValueAt : invalid index");
        }
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if (listService == null) {
            return 0;
        }
        return listService.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listService == null) {
            return false;
        }

        if (listService.isEmpty()) {
            return false;
        }
        InpService record = listService.get(listService.size() - 1);
        return record.getServiceId() == null;
    }

    public void addEmptyRow() {
        if (listService != null) {
            InpService record = new InpService();
            listService.add(record);
            fireTableRowsInserted(listService.size() - 1, listService.size() - 1);
            parent.setRowSelectionInterval(listService.size() - 1, listService.size() - 1);
            parent.setColumnSelectionInterval(0, 0);
        }
    }

    public void delete(int row) {
        if (listService == null) {
            return;
        }

        if (listService.isEmpty()) {
            return;
        }

        try {
            InpService record = listService.get(row);

            if (record.getServiceId() != null) {
                listService.remove(row);
                fireTableRowsDeleted(row, row);
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }
            }
        } catch (Exception ex) {
            log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public void setSrvFilter(InpService record, int pos) {
        if (listService == null) {
            return;
        }

        if (listService.isEmpty()) {
            return;
        }

        try {
            listService.set(pos, record);
            fireTableDataChanged();

            if (!hasEmptyRow()) {
                addEmptyRow();
            }
        } catch (Exception ex) {
            log.error("setSrvFilter : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public void setService(InpService service, int row) {
        final String TABLE = "com.cv.app.inpatient.database.entity.InpService";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE serviceId = " + service.getServiceId();

        if (listService != null) {
            try {
                if (!JoSQLUtil.isAlreadyHave(strSQL, listService)) {
                    setSrvFilter(service, row);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate item code.",
                            "Duplicate", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                log.error("setService : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    public String getFilterCodeStr() {
        String strTmp = null;

        if (listService != null) {
            for (InpService service : listService) {
                if (service.getServiceId() != null) {
                    if (strTmp == null) {
                        strTmp = service.getServiceId().toString();
                    } else {
                        strTmp = strTmp + "," + service.getServiceId().toString();
                    }
                }
            }
        }

        return strTmp;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }
}
