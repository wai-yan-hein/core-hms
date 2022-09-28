/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.DCAutoAddIdMapping;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DCAutoAddIdTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DCAutoAddIdTableModel.class.getName());
    private List<DCAutoAddIdMapping> listService;
    private final String[] columnNames = {"Description", "Qty", "Price", "Sort Order"};
    private final AbstractDataAccess dao = Global.dao;
    private Integer serviceId;

    public DCAutoAddIdTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 2) {
            return true;
        }
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Description
                return String.class;
            case 1: //Qty
                return Integer.class;
            case 2: //Price
                return Double.class;
            case 3: //Sort Order
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listService == null) {
            return null;
        }

        if (listService.isEmpty()) {
            return null;
        }

        DCAutoAddIdMapping record = listService.get(row);
        switch (column) {
            case 0: //Description
                if (record.getKey() != null) {
                    if (record.getKey().getAddServiceId() != null) {
                        return record.getKey().getAddServiceId().getServiceName();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            case 1: //Qty
                return record.getQty();
            case 2: //Price
                if (record.getKey() != null) {
                    if (record.getKey().getAddServiceId() != null) {
                        return record.getKey().getAddServiceId().getFees();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            case 3: //Sort Order
                return record.getSortOrder();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listService == null) {
            return;
        }

        if (listService.isEmpty()) {
            return;
        }

        DCAutoAddIdMapping record = listService.get(row);
        switch (column) {
            case 0: //Description
                if (value == null) {
                    record.getKey().setAddServiceId(null);
                } else {
                    record.getKey().setAddServiceId((InpService) value);
                    record.getKey().setTranOption("DC");
                    record.getKey().setTranServiceId(serviceId);
                    record.setQty(1);
                    record.setSortOrder(row + 1);
                }
                break;
            case 1: //Qty
                if (value == null) {
                    record.setQty(1);
                } else {
                    record.setQty(Integer.parseInt(value.toString()));
                }
                break;
            case 2: //Sort Order
                if (value == null) {
                    record.setQty(null);
                } else {
                    record.setQty(Integer.parseInt(value.toString()));
                }
                break;
        }

        if (record.getKey() != null) {
            if (record.getKey().getAddServiceId() != null) {
                try {
                    dao.save(record);
                } catch (Exception ex) {
                    log.error("setValueAt : " + ex.toString());
                } finally {
                    dao.close();
                }
            }
        }

        fireTableCellUpdated(row, 0);
        fireTableCellUpdated(row, 1);
        fireTableCellUpdated(row, 2);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
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

    public void assignAutoId(Integer serviceId) {
        try {
            if (serviceId != null) {
                this.serviceId = serviceId;
                String strSql = "select o from DCAutoAddIdMapping o "
                        + "where o.key.tranOption = 'DC' and o.key.tranServiceId = "
                        + serviceId + " order by o.sortOrder";
                listService = dao.findAllHSQL(strSql);
                if (listService == null) {
                    listService = new ArrayList();
                }
                listService.add(new DCAutoAddIdMapping());
                fireTableDataChanged();
            }
        } catch (Exception ex) {
            log.error("assignAutoId : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void delete(int row) {
        if (listService != null) {
            if (!listService.isEmpty()) {
                DCAutoAddIdMapping mp = listService.get(row);

                try {
                    dao.execSql("delete from auto_add_id_mapping where tran_option = '" 
                            + mp.getKey().getTranOption() + "' and"
                            + " tran_service_id = "
                            + mp.getKey().getTranServiceId() + " and add_service_id =" 
                            + mp.getKey().getAddServiceId().getServiceId());
                    listService.remove(row);
                } catch (Exception ex) {
                    log.error("delete : " + ex.toString());
                }
                fireTableRowsDeleted(0, listService.size());
            }
        }

    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listService != null) {
            if (!listService.isEmpty()) {
                DCAutoAddIdMapping srv = listService.get(listService.size() - 1);
                if (srv != null) {
                    if (srv.getKey() != null) {
                        if (srv.getKey().getAddServiceId() != null) {
                            status = false;
                        }
                    }
                }
            }
        }
        return status;
    }

    private void addEmptyRow() {
        if (listService != null) {
            listService.add(new DCAutoAddIdMapping());
            fireTableRowsInserted(listService.size() - 1, listService.size() - 1);
        }
    }

    public DCAutoAddIdMapping getSelectMapping(int row) {
        if (listService == null) {
            return null;
        } else if (listService.isEmpty()) {
            return null;
        } else {
            return listService.get(row);
        }
    }
}
