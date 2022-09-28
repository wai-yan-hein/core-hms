/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PurchaseIMEINo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PurchaseIMEIDetailTableModel extends AbstractTableModel {

    private final AbstractDataAccess dao = Global.dao;
    static Logger log = Logger.getLogger(PurchaseIMEIDetailTableModel.class.getName());
    private List<PurchaseIMEINo> listIMEINo;
    private final String[] columnNames = {"IMEI-1", "IMEI-2", "SD No."};
    public JTable parent;
    private int count = 1;
    private int qty;
    private PurchaseIMEINo tmpObj;
    private String deleteList;

    public PurchaseIMEIDetailTableModel() {

    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0:
                PurchaseIMEINo pur = listIMEINo.get(row);
                if (pur.getKey().getImei1() == null) {
                    return true;
                } else {
                    return false;
                }
            case 1:
                return true;
            case 2:
                return true;
            default:
                return false;
        }
        
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //IMEI-1
                return String.class;
            case 1: //IMEI-2 Name
                return String.class;
            case 2: //SD No
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listIMEINo == null) {
            return null;
        }

        if (listIMEINo.isEmpty()) {
            return null;
        }

        try {
            PurchaseIMEINo record = listIMEINo.get(row);

            switch (column) {
                case 0: //IMEI-1
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getImei1();
                    }
                case 1: //IMEI-2
                    return record.getImei2();
                case 2: //SD No.
                    return record.getSdNo();
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
        if (listIMEINo == null) {
            return;
        }

        if (listIMEINo.isEmpty()) {
            return;
        }

        try {
            PurchaseIMEINo record = listIMEINo.get(row);
            record.getKey().setItemId(tmpObj.getKey().getItemId());
            record.getKey().setPurVouNo(tmpObj.getKey().getPurVouNo());
            record.getKey().setPurDetailId(tmpObj.getKey().getPurDetailId());
            switch (column) {
                case 0: //IMEI-1
                    if (value != null) {
                        if (record.getKey() != null) {
                            record.getKey().setImei1((String) value);
                        }
                    } else {
                        if (record.getKey() != null) {
                            record.getKey().setImei1(null);
                        }
                    }
                    if (count < qty) {
                        blankRow();
                        count += 1;
                    }
                    //parent.setColumnSelectionInterval(1, 1);
                    break;
                case 1: //IMEI-2
                    if (value != null) {
                        record.setImei2((String) value);
                    } else {
                        record.setImei2(null);
                    }
                    //parent.setColumnSelectionInterval(2, 2);
                    break;
                case 2: //SD No.
                    if (value != null) {
                        record.setSdNo((String) value);
                    } else {
                        record.setSdNo(null);
                    }
                    //parent.setRowSelectionInterval(row + 1, row + 1);
                    //parent.setColumnSelectionInterval(0, 0);
                    break;
                default:
                    break;
            }
            if (record.getKey().getImei1() != null) {
                dao.save(record);
            }

            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listIMEINo == null) {
            return 0;
        }
        return listIMEINo.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PurchaseIMEINo> getListIMEINo() {
        return listIMEINo;
    }

    public void setListIMEINo(List<PurchaseIMEINo> listIMEINo) {

        if (!listIMEINo.isEmpty()) {
            this.listIMEINo = listIMEINo;

        } else {
            this.listIMEINo = new ArrayList();
            this.listIMEINo.add(new PurchaseIMEINo());
        }
        fireTableDataChanged();
    }

    public void getIMEIList(String vouNo, String purDetailId, String itemId, float qty) {
        count = 1;

        this.qty = (int) qty;
        PurchaseIMEINo purImei = new PurchaseIMEINo();

        String strSql = "select o from PurchaseIMEINo o where o.key.purVouNo = '"
                + vouNo + "' and o.key.purDetailId = '" + purDetailId
                + "' and o.key.itemId = '" + itemId + "'";
        try {
            List<PurchaseIMEINo> listTmpIMEINo = dao.findAllHSQL(strSql);
            //setListIMEINo(listTmpIMEINo);
            if (!listTmpIMEINo.isEmpty()) {
                listIMEINo = listTmpIMEINo;
                if (listIMEINo.size() < qty) {

                    listIMEINo.add(new PurchaseIMEINo());
                    //count += 1;
                    this.qty -= (listIMEINo.size() - 1);

                } else {
                    count = this.qty;
                }

            } else {
                listIMEINo = new ArrayList();
                listIMEINo.add(new PurchaseIMEINo());
            }
            purImei.getKey().setItemId(itemId);
            purImei.getKey().setPurDetailId(Long.parseLong(purDetailId));
            purImei.getKey().setPurVouNo(vouNo);
            tmpObj = purImei;
            fireTableDataChanged();

        } catch (Exception ex) {
            log.error("getIMEIList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void addEmptyRow() {
        if (listIMEINo != null) {
            if (!listIMEINo.isEmpty()) {
                PurchaseIMEINo tmpNo = listIMEINo.get(listIMEINo.size() - 1);
                if (tmpNo != null) {
                    if (tmpNo.getKey().getImei1() != null) {
                        listIMEINo.add(new PurchaseIMEINo());
                    }
                } else {
                    listIMEINo.add(new PurchaseIMEINo());
                }
            } else {
            }
        }
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void blankRow() {
        listIMEINo.add(new PurchaseIMEINo());
        fireTableRowsInserted(listIMEINo.size() - 1, listIMEINo.size() - 1);
    }

    public void clear() {
        try {
            listIMEINo.clear();
            fireTableDataChanged();

        } catch (Exception e) {
        }

    }

    public List<PurchaseIMEINo> setList() {
        return listIMEINo;
    }

    public void delete(int row) {
        if (listIMEINo == null) {
            return;
        }

        if (listIMEINo.isEmpty()) {
            return;
        }

        PurchaseIMEINo record = listIMEINo.get(row);

        if (record != null) {
            if (record.getKey().getImei1() != null && record.getKey().getItemId() != null
                    && record.getKey().getPurDetailId() != null && record.getKey().getPurVouNo() != null) {
                String strSQL;

                strSQL = " delete from pur_imei_no where pur_inv_id='" + record.getKey().getPurVouNo()
                        + "' and pur_detail_id='" + record.getKey().getPurDetailId() + "' and item_id='" + record.getKey().getItemId()
                        + "' and imei1='" + record.getKey().getImei1() + "'";
                try {
                    dao.open();
                    dao.execSql(strSQL);
                    dao.close();
                } catch (Exception ex) {
                    log.error("Delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                }
            }

            listIMEINo.remove(row);

            if (!hasEmptyRow()) {
                listIMEINo.add(new PurchaseIMEINo());
            }
            fireTableRowsDeleted(row, row);

        }
    }

    public boolean hasEmptyRow() {
        if (listIMEINo == null) {
            return false;
        }
        if (listIMEINo.isEmpty()) {
            return false;
        }

        PurchaseIMEINo record = listIMEINo.get(listIMEINo.size() - 1);
        return !(record.getKey().getImei1() != null || record.getKey().getItemId() != null
                || record.getKey().getPurVouNo() != null || record.getKey().getPurDetailId() != null);
    }

}
