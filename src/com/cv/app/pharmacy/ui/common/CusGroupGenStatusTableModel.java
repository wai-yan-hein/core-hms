/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.helper.CusGroupGenStatus;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class CusGroupGenStatusTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CusGroupGenStatusTableModel.class.getName());
    private List<CusGroupGenStatus> listCGGS = new ArrayList();
    private final String[] columnNames = {"Group Id", "Group Name", "Status"};
    private AbstractDataAccess dao;
    private String parentId;

    public CusGroupGenStatusTableModel(AbstractDataAccess dao, String parentId) {
        this.dao = dao;
        this.parentId = parentId;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Group Id
                return String.class;
            case 1: //Group Name
                return String.class;
            case 2: //Status
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listCGGS == null) {
            return null;
        }

        if (listCGGS.isEmpty()) {
            return null;
        }

        try {
            CusGroupGenStatus cggs = listCGGS.get(row);

            switch (column) {
                case 0: //Group Id
                    return cggs.getGroupId();
                case 1: //Group Name
                    return cggs.getGroupName();
                case 2: //Status
                    return cggs.isGenerate();
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
        if (listCGGS == null) {
            return;
        }

        if (listCGGS.isEmpty()) {
            return;
        }

        try {
            if (column == 2) {
                CusGroupGenStatus cggs = listCGGS.get(row);
                cggs.setGenerate(!cggs.isGenerate());

                if (cggs.isGenerate()) { //Need to generate
                    String strSql = "insert into trader(discriminator, trader_id, active, created_id, created_date,"
                            + "trader_name, updated_id,updated_date, credit_days, credit_limit, type_id, email, phone,"
                            + "township, pay_method_id, parent, group_id) "
                            + "select t.discriminator, concat('CUS',cg.group_id, substr(t.trader_id, 4)), true, t.created_id, t.created_date,"
                            + "concat(replace(t.trader_name,concat(' (',t.group_id,')'),''), ' (', cg.group_id, ')'), t.updated_id, t.updated_date, t.credit_days,"
                            + "t.credit_limit,t.type_id, t.email, t.phone, t.township, t.pay_method_id,t.trader_id,"
                            + "cg.group_id from customer_group cg, trader t "
                            + "where t.discriminator = 'C' and t.trader_id = '" + parentId + "' and "
                            + "cg.group_id = '" + cggs.getGroupId() + "'";
                    log.info(strSql);
                    dao.execSql(strSql);
                } else { //Need to delete
                    String strSql = "delete from trader where trader_id = '" + parentId + "' "
                            + " and group_id = '" + cggs.getGroupId() + "'";
                    dao.execSql(strSql);
                }
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        //System.out.println("Row : " + row + " Col : " + column);
    }

    @Override
    public int getRowCount() {
        if(listCGGS == null){
            return 0;
        }
        return listCGGS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void setListCGGS(List<CusGroupGenStatus> listCGGS) {
        this.listCGGS = listCGGS;
        fireTableDataChanged();
    }
}
