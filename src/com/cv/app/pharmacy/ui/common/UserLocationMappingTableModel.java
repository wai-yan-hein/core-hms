/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.UserLocationMapping;
import com.cv.app.pharmacy.database.view.VUserLocationMapping;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class UserLocationMappingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(UserLocationMappingTableModel.class.getName());
    private List<VUserLocationMapping> list;
    private final String[] columnNames = {"Location Name", "Sale", "Pur", "Ret-In", "Ret-Out",
        "Tran-In", "Tran-Out", "Dmg", "P-Change", "Adj", "STK-OP", "Cus-Pay", "Sess-Check",
        "STK-Issue", "STK-Recv", "Re-Order"};
    private String userId = "-";
    private final AbstractDataAccess dao = Global.dao;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 0) {
            return String.class;
        } else {
            return Boolean.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        try {
            VUserLocationMapping record = list.get(row);

            switch (column) {
                case 0: //Location
                    return record.getLocationName();
                case 1: //Sale
                    return record.getIsAllowSale();
                case 2: //Pur
                    return record.getIsAllowPur();
                case 3: //Ret-In
                    return record.getIsAllowRetIn();
                case 4: //Ret-Out
                    return record.getIsAllowRetOut();
                case 5: //Tran-In
                    return record.getIsAllowTranIn();
                case 6: //Tran-Out
                    return record.getIsAllowTranOut();
                case 7: //Dmg
                    return record.getIsAllowDmg();
                case 8: //P-Change
                    return record.getIsAllowPriceChange();
                case 9: //Adj
                    return record.getIsAllowAdj();
                case 10: //STK-OP
                    return record.getIsAllowStkOp();
                case 11: //Cus-Pay
                    return record.getIsAllowCusPayVou();
                case 12: //Sess-Check
                    return record.getIsAllowSessCheck();
                case 13: //STK-Issue
                    return record.getIsAllowStkIssue();
                case 14: //STK-Recv
                    return record.getIsAllowStkReceive();
                case 15: //Re-Order
                    return record.getIsAllowReOrder();
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
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        try {
            VUserLocationMapping record = list.get(row);

            switch (column) {
                case 0: //Location
                    break;
                case 1: //Sale
                    record.setIsAllowSale((Boolean) value);
                    break;
                case 2: //Pur
                    record.setIsAllowPur((Boolean) value);
                    break;
                case 3: //Ret-In
                    record.setIsAllowRetIn((Boolean) value);
                    break;
                case 4: //Ret-Out
                    record.setIsAllowRetOut((Boolean) value);
                    break;
                case 5: //Tran-In
                    record.setIsAllowTranIn((Boolean) value);
                    break;
                case 6: //Tran-Out
                    record.setIsAllowTranOut((Boolean) value);
                    break;
                case 7: //Dmg
                    record.setIsAllowDmg((Boolean) value);
                    break;
                case 8: //P-Change
                    record.setIsAllowPriceChange((Boolean) value);
                    break;
                case 9: //Adj
                    record.setIsAllowAdj((Boolean) value);
                    break;
                case 10: //STK-OP
                    record.setIsAllowStkOp((Boolean) value);
                    break;
                case 11: //Cus-Pay
                    record.setIsAllowCusPayVou((Boolean) value);
                    break;
                case 12: //Sess-Check
                    record.setIsAllowSessCheck((Boolean) value);
                    break;
                case 13: //STK-Issue
                    record.setIsAllowStkIssue((Boolean) value);
                    break;
                case 14: //STK-Recv
                    record.setIsAllowStkReceive((Boolean) value);
                    break;
                case 15: //Re-Order
                    record.setIsAllowReOrder((Boolean) value);
                    break;
            }
            fireTableCellUpdated(row, column);
            saveRecord(record);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        try {
            list = dao.findAllHSQL("select o from VUserLocationMapping o where o.key.userId = '"
                    + userId + "'");
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("setUserId : " + userId + " : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void saveRecord(VUserLocationMapping record) {
        try {
            UserLocationMapping ulm = (UserLocationMapping) dao.find(UserLocationMapping.class, record.getKey());
            if (ulm == null) {
                ulm = new UserLocationMapping();
                ulm.setKey(record.getKey());
            }
            ulm.setIsAllowSale(record.getIsAllowSale());
            ulm.setIsAllowPur(record.getIsAllowPur());
            ulm.setIsAllowRetIn(record.getIsAllowRetIn());
            ulm.setIsAllowRetOut(record.getIsAllowRetOut());
            ulm.setIsAllowTranIn(record.getIsAllowTranIn());
            ulm.setIsAllowTranOut(record.getIsAllowTranOut());
            ulm.setIsAllowDmg(record.getIsAllowDmg());
            ulm.setIsAllowPriceChange(record.getIsAllowPriceChange());
            ulm.setIsAllowAdj(record.getIsAllowAdj());
            ulm.setIsAllowStkOp(record.getIsAllowStkOp());
            ulm.setIsAllowCusPayVou(record.getIsAllowCusPayVou());
            ulm.setIsAllowSessCheck(record.getIsAllowSessCheck());
            ulm.setIsAllowStkIssue(record.getIsAllowStkIssue());
            ulm.setIsAllowStkReceive(record.getIsAllowStkReceive());
            ulm.setIsAllowReOrder(record.getIsAllowReOrder());
            ulm.setUpdatedDate(new Date());
            dao.save(ulm);
        } catch (Exception ex) {
            log.error("saveRecord : " + ex.toString());
        } finally {
            dao.close();
        }
    }
}
