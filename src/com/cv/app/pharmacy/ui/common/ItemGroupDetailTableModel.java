/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemGroupDetail;
import com.cv.app.pharmacy.database.entity.Medicine;
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
public class ItemGroupDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemGroupDetailTableModel.class.getName());
    private List<ItemGroupDetail> listItemGroupDetail = new ArrayList();
    private final String[] columnNames = {"Item Code", "Description", "Relation-Str"};
    private AbstractDataAccess dao;
    private int groupId;

    public ItemGroupDetailTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;

        try {
            String strSql = "select igd from ItemGroupDetail igd where igd.groupId = "
                    + groupId;
            listItemGroupDetail = dao.findAllHSQL(strSql);
            fireTableDataChanged();
            addEmptyRow();
            System.gc();
        } catch (Exception ex) {
            log.error("setGroupId : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0) {
            return groupId != 0;
        } else {
            return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listItemGroupDetail == null) {
            return null;
        }

        if (listItemGroupDetail.isEmpty()) {
            return null;
        }

        try {
            ItemGroupDetail itemGroupDetail = listItemGroupDetail.get(row);

            switch (column) {
                case 0: //Item Code
                    if (itemGroupDetail.getItem() != null) {
                        return itemGroupDetail.getItem().getMedId();
                    } else {
                        return null;
                    }
                case 1: //Descritption
                    if (itemGroupDetail.getItem() != null) {
                        return itemGroupDetail.getItem().getMedName();
                    } else {
                        return null;
                    }
                case 2: //Relation-Str
                    if (itemGroupDetail.getItem() != null) {
                        return itemGroupDetail.getItem().getRelStr();
                    } else {
                        return null;
                    }
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

        switch (column) {
            case 0: //Item Code
                if (value != null) {
                    getItem(value.toString(), row);
                }
                break;
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listItemGroupDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ItemGroupDetail> getListItemGroupDetail() {
        return listItemGroupDetail;
    }

    public void setListItemGroupDetail(List<ItemGroupDetail> listItemGroupDetail) {
        this.listItemGroupDetail = listItemGroupDetail;
        fireTableDataChanged();
    }

    public void deleteItemGroupDetail(int row) {
        if (listItemGroupDetail != null) {
            if (!listItemGroupDetail.isEmpty()) {
                listItemGroupDetail.remove(row);
                fireTableRowsDeleted(0, listItemGroupDetail.size());
            }
        }
    }

    public void addEmptyRow() {
        if (groupId > 0) {
            if (listItemGroupDetail != null) {
                if (listItemGroupDetail.isEmpty()) {
                    listItemGroupDetail.add(new ItemGroupDetail());
                    fireTableRowsInserted(listItemGroupDetail.size() - 1, listItemGroupDetail.size() - 1);
                } else {
                    ItemGroupDetail igd = listItemGroupDetail.get(listItemGroupDetail.size() - 1);

                    if (igd.getGroupDetailId() != null) {
                        listItemGroupDetail.add(new ItemGroupDetail());
                        fireTableRowsInserted(listItemGroupDetail.size() - 1, listItemGroupDetail.size() - 1);
                    }
                }
            }
        }
    }

    private void getItem(String itemCode, int row) {
        final String TABLE = "com.cv.app.pharmacy.database.entity.ItemGroupDetail";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE item.medId = '" + itemCode + "'";

        try {
            if (!JoSQLUtil.isAlreadyHave(strSQL, listItemGroupDetail)) {
                dao.open();
                Medicine item = (Medicine) dao.find(Medicine.class, itemCode);
                dao.close();

                if (item != null) {
                    ItemGroupDetail igd = new ItemGroupDetail();
                    igd.setItem(item);
                    igd.setGroupId(groupId);
                    dao.save(igd);
                    listItemGroupDetail.set(row, igd);
                    fireTableRowsUpdated(row, row);
                    addEmptyRow();
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid item code.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate item code.",
                        "Duplicate", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getItem : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void deleteItem(int row) {
        if(listItemGroupDetail == null){
            return;
        }
        
        if(!listItemGroupDetail.isEmpty()){
            return;
        }
        
        try {
            ItemGroupDetail igd = listItemGroupDetail.get(row);
            if (igd.getGroupDetailId() != null) {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                        "Are you sure to delete?",
                        "Grouping item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    dao.delete(igd);
                    listItemGroupDetail.remove(row);
                    fireTableRowsDeleted(row, row);
                }
            }
        } catch (Exception ex) {
            log.error("deleteItem : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    public void addItemType(int groupId, String itemType) {
        String strSql = "insert into item_group_detail(item_id, group_id) "
                + "select med_id, " + groupId
                + " from medicine\n"
                + " where med_type_id = '" + itemType
                + "' and med_id not in (select item_id from item_group_detail "
                + "where group_id = " + groupId + ")";
        try {
            dao.execSql(strSql);
            setGroupId(groupId);
        } catch (Exception ex) {
            log.error("addItemType : "+ ex.getStackTrace()[0].getLineNumber() + " - "  + ex);
        } finally {
            dao.close();
        }
    }

    public void deleteItemType(int groupId, String itemType) {
        String strSql = "delete from item_group_detail where group_id = " + groupId
                + " and item_id in (select med_id from medicine where med_type_id = '" + itemType + "')";
        try {
            dao.execSql(strSql);
            setGroupId(groupId);
        } catch (Exception ex) {
            log.error("deleteItemType : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    public void addCategory(int groupId, int catId) {
        String strSql = "insert into item_group_detail(item_id, group_id) "
                + "select med_id, " + groupId
                + " from medicine\n"
                + " where category_id = " + catId
                + " and med_id not in (select item_id from item_group_detail "
                + "where group_id = " + groupId + ")";
        try {
            dao.execSql(strSql);
            setGroupId(groupId);
        } catch (Exception ex) {
            log.error("addCategory : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    public void deleteCategory(int groupId, int catId) {
        String strSql = "delete from item_group_detail where group_id = " + groupId
                + " and item_id in (select med_id from medicine where category_id = " + catId + ")";
        try {
            dao.execSql(strSql);
            setGroupId(groupId);
        } catch (Exception ex) {
            log.error("deleteCategory : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    public void addBrand(int groupId, int brandId) {
        String strSql = "insert into item_group_detail(item_id, group_id) "
                + "select med_id, " + groupId
                + " from medicine\n"
                + " where brand_id = " + brandId
                + " and med_id not in (select item_id from item_group_detail "
                + "where group_id = " + groupId + ")";
        try {
            dao.execSql(strSql);
            setGroupId(groupId);
        } catch (Exception ex) {
            log.error("addBrand : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    public void deleteBrand(int groupId, int branId) {
        String strSql = "delete from item_group_detail where group_id = " + groupId
                + " and item_id in (select med_id from medicine where brand_id = " + branId + ")";
        try {
            dao.execSql(strSql);
            setGroupId(groupId);
        } catch (Exception ex) {
            log.error("deleteBrand : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }
}
