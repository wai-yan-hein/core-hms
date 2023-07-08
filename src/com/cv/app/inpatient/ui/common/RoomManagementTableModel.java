/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.CalculateObserver;
import com.cv.app.common.Global;
import com.cv.app.inpatient.database.healper.RoomStatus;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
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
public class RoomManagementTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RoomManagementTableModel.class.getName());
    private List<RoomStatus> list = new ArrayList();
    private final String[] columnNames = {"Floor", "Code", "Room", "Price", "Reg-No.",
        "Name", "Status"};
    private CalculateObserver observer;
    private final AbstractDataAccess dao = Global.dao;
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        RoomStatus rs = list.get(row);
        return column == 6 && rs.isStatus();
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0://Floor
                return String.class;
            case 1://Code
                return String.class;
            case 2://Room
                return String.class;
            case 3://Price
                return Double.class;
            case 4://Reg-No
                return String.class;
            case 5://Name
                return String.class;
            case 6://Status
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list != null) {
            if (!list.isEmpty()) {
                try {
                    RoomStatus record = list.get(row);

                    switch (column) {
                        case 0://Floor
                            return record.getFloor();
                        case 1: //Code
                            return record.getCode();
                        case 2: //Room
                            return record.getRoom();
                        case 3: //Price
                            return record.getPrice();
                        case 4: //Reg-No
                            return record.getRegNo();
                        case 5: //Name
                            return record.getPtName();
                        case 6: //Status
                            return record.isStatus();
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
        if (list != null) {
            if (!list.isEmpty()) {
                RoomStatus record = list.get(row);
                try {
                    switch (column) {
                        case 0://Floor
                            break;
                        case 1: //Code
                            break;
                        case 2: //Room
                            break;
                        case 3: //Price
                            break;
                        case 4: //Reg-No
                            break;
                        case 5: //Name
                            break;
                        case 6: //Status
                            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to kick out from the room?",
                                    "Room kick out", JOptionPane.YES_NO_OPTION);
                            if (yes_no == 0) {
                                if (value != null) {
                                    record.setStatus((Boolean) value);
                                } else {
                                    record.setStatus(false);
                                }
                                dao.execSql("update building_structure set reg_no = null where id = " + record.getId());
                                if (observer != null) {
                                    observer.calculate();
                                }
                            }
                            break;
                    }
                } catch (Exception ex) {
                    log.error("getValueAt : " + record.getId() + "-" + record.getRegNo() + " - " + ex.toString());
                } finally {
                    dao.close();
                }
            }
        }
    }

    @Override
    public int getRowCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }

    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<RoomStatus> getList() {
        return list;
    }

    public void setList(List<RoomStatus> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public void setObserver(CalculateObserver observer) {
        this.observer = observer;
    }
}
