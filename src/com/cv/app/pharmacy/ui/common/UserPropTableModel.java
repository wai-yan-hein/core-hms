/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.UserProperty;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class UserPropTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(UserPropTableModel.class.getName());
    private List<UserProperty> listUserProperty = new ArrayList();
    private final String[] columnNames = {"Description", "Vaue", "Remark"};
    private AbstractDataAccess dao;

    public UserPropTableModel(AbstractDataAccess dao) {
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
        if (listUserProperty == null) {
            return null;
        }

        if (listUserProperty.isEmpty()) {
            return null;
        }

        try {
            UserProperty prop = listUserProperty.get(row);

            switch (column) {
                case 0: //Description
                    return prop.getPropDesp();
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
        if (listUserProperty == null) {
            return;
        }

        if (listUserProperty.isEmpty()) {
            return;
        }

        boolean status = false;

        try {
            UserProperty prop = listUserProperty.get(row);
            switch (column) {
                case 0: //Description
                    if (value != null) {
                        String strValue = value.toString().trim();
                        if (!strValue.isEmpty()) {
                            if (prop.getPropDesp() != null) {
                                dao.delete(prop);
                            }
                            prop.setPropDesp(strValue);
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
                dao.save(prop);
                addNewRow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if (listUserProperty == null) {
            return 0;
        }
        return listUserProperty.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<UserProperty> getListUserProperty() {
        return listUserProperty;
    }

    public void setListUserProperty(List<UserProperty> listUserProperty) {
        this.listUserProperty = listUserProperty;
        fireTableDataChanged();
    }

    public UserProperty getUserProperty(int row) {
        if (listUserProperty != null) {
            if (!listUserProperty.isEmpty()) {
                return listUserProperty.get(row);
            }
        }
        return null;
    }

    public void setUserProperty(int row, UserProperty prop) {
        if (listUserProperty != null) {
            if (!listUserProperty.isEmpty()) {
                listUserProperty.set(row, prop);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addUserProperty(UserProperty prop) {
        if (listUserProperty != null) {
            listUserProperty.add(prop);
            fireTableRowsInserted(listUserProperty.size() - 1, listUserProperty.size() - 1);
        }
    }

    public void deleteUserProperty(int row) {
        try {
            UserProperty prop = listUserProperty.get(row);

            dao.delete(prop);
            listUserProperty.remove(row);
            fireTableRowsDeleted(0, listUserProperty.size());
        } catch (Exception ex) {
            log.error("deleteUserProperty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public void addNewRow() {
        if (!hasNewRow()) {
            if (listUserProperty != null) {
                listUserProperty.add(new UserProperty());
                int index = listUserProperty.size();

                fireTableRowsInserted(index - 1, index - 1);
            }
        }
    }

    private boolean hasNewRow() {
        boolean status = true;

        if (listUserProperty != null) {
            if (listUserProperty.isEmpty()) {
                status = false;
            } else {
                UserProperty lastProp = listUserProperty.get(listUserProperty.size() - 1);

                if (lastProp.getPropDesp() != null) {
                    status = false;
                }
            }
        } else {
            status = false;
        }
        return status;
    }
}
