/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.MachineProperty;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class MachinePropTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(MachinePropTableModel.class.getName());
    private List<MachineProperty> listMachineProperty = new ArrayList();
    private final String[] columnNames = {"Description", "Vaue", "Remark"};
    private AbstractDataAccess dao;
    private int machineId;

    public MachinePropTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        /*SysProperty prop = listSysProperty.get(row);

         if (prop.getPropDesp().isEmpty()) {
         return true;
         } else {
         return false;
         }*/

        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listMachineProperty == null) {
            return null;
        }

        if (listMachineProperty.isEmpty()) {
            return null;
        }

        try {
            MachineProperty prop = listMachineProperty.get(row);

            switch (column) {
                case 0: //Description
                    return prop.getKey().getPropDesp();
                case 1: //Value
                    return prop.getPropValue();
                case 2: //Remark
                    return prop.getPropRemark();
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
        boolean status = false;

        if (listMachineProperty == null) {
            return;
        }

        if (listMachineProperty.isEmpty()) {
            return;
        }

        try {
            MachineProperty prop = listMachineProperty.get(row);
            switch (column) {
                case 0: //Description
                    if (value != null) {
                        String strValue = value.toString().trim();
                        if (!strValue.isEmpty()) {
                            /*if (prop.getKey().getPropDesp() != null) {
                             dao.delete(prop);
                             }*/
                            prop.getKey().setPropDesp(strValue);
                            status = true;
                        }
                    }
                    break;
                case 1: //Value
                    if (value != null) {
                        String strValue = value.toString().trim();
                        if (!strValue.isEmpty()) {
                            prop.setPropValue(strValue);
                            status = true;
                        }
                    }
                    break;
                case 2: //Remark
                    if (value != null) {
                        String strValue = value.toString().trim();
                        if (!strValue.isEmpty()) {
                            prop.setPropRemark(strValue);
                            status = true;
                        }
                    }
                default:
                    System.out.println("Invalid index");
            }

            if (status) {
                prop.getKey().setMachineId(machineId);
                dao.save(prop);
                addNewRow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if (listMachineProperty == null) {
            return 0;
        }
        return listMachineProperty.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<MachineProperty> getListMachineProperty() {
        return listMachineProperty;
    }

    public void setListMachineProperty(List<MachineProperty> listMachineProperty) {
        this.listMachineProperty = listMachineProperty;
        fireTableDataChanged();
    }

    public MachineProperty getMachineProperty(int row) {
        if (listMachineProperty != null) {
            if (!listMachineProperty.isEmpty()) {
                return listMachineProperty.get(row);
            }
        }
        return null;
    }

    public void setMachineProperty(int row, MachineProperty prop) {
        if (listMachineProperty != null) {
            if (!listMachineProperty.isEmpty()) {
                listMachineProperty.set(row, prop);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addMachineProperty(MachineProperty prop) {
        if (listMachineProperty != null) {
            listMachineProperty.add(prop);
            fireTableRowsInserted(listMachineProperty.size() - 1, listMachineProperty.size() - 1);
        }
    }

    public void deleteMachineProperty(int row) {
        if (listMachineProperty != null) {
            try {
                MachineProperty prop = listMachineProperty.get(row);

                dao.delete(prop);
                listMachineProperty.remove(row);
                fireTableRowsDeleted(0, listMachineProperty.size());
            } catch (Exception ex) {
                log.error("deleteMachineProperty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    public void addNewRow() {
        if (!hasNewRow()) {
            if (listMachineProperty != null) {
                listMachineProperty.add(new MachineProperty());
                int index = listMachineProperty.size();

                fireTableRowsInserted(index - 1, index - 1);
            }
        }
    }

    private boolean hasNewRow() {
        boolean status = true;

        if (listMachineProperty != null) {
            if (listMachineProperty.isEmpty()) {
                status = false;
            } else {
                MachineProperty lastProp = listMachineProperty.get(listMachineProperty.size() - 1);

                if (lastProp == null) {
                    status = false;
                } else if (lastProp.getKey().getPropDesp() != null) {
                    status = false;
                }
            }
        }

        return status;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
        try {
            listMachineProperty = dao.findAllHSQL("select o from MachineProperty o where o.key.machineId = " + machineId);
        } catch (Exception ex) {
            log.error("setMachineId : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        addNewRow();
        fireTableDataChanged();
    }
}
