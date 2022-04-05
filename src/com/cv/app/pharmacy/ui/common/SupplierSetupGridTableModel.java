/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.BusinessType;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.SupplierWithParent;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.TraderType;
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
public class SupplierSetupGridTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SupplierSetupGridTableModel.class.getName());
    private List<SupplierWithParent> listCUS = new ArrayList();
    private final String[] columnNames = {"Code", "Customer Name", "A", "Phone",
        "Cus Group", "Exppense %"};
    private boolean needToSave = false;
    private final AbstractDataAccess dao = Global.dao;
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0: //Code
                return false;
            case 1: //Customer Name
                return true;
            case 2: //Active
                return true;
            case 3: //Phone
                return true;
            case 4: //Cus Group
                return false;
            case 5: //Expense %
                return true;
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Customer Name
                return String.class;
            case 2: //Active
                return Boolean.class;
            case 3: //Phone
                return String.class;
            case 4: //Cus Group
                return String.class;
            case 5: //Expense %
                return Float.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listCUS == null) {
            return null;
        }

        if (listCUS.isEmpty()) {
            return null;
        }

        try {
            SupplierWithParent record = listCUS.get(row);

            switch (column) {
                case 0: //Code
                    return record.getTraderId();
                case 1: //Customer Name
                    return record.getTraderName();
                case 2: //Active
                    return record.getActive();
                case 3: //Phone
                    return record.getPhone();
                case 4: //Cus Group
                    if (record.getTraderGroup() != null) {
                        return record.getTraderGroup().getGroupName();
                    } else {
                        return null;
                    }
                case 5: //Expense %
                    return record.getExpensePercent();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listCUS == null) {
            return;
        }

        if (listCUS.isEmpty()) {
            return;
        }

        try {
            SupplierWithParent record = listCUS.get(row);

            switch (column) {
                case 0: //Code
                    break;
                case 1: //Customer Name
                    if (value == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Name cannot be blank or null.",
                                "Name null error.", JOptionPane.ERROR_MESSAGE);
                    } else if (value.toString().isEmpty()) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Name cannot be blank or null.",
                                "Name null error.", JOptionPane.ERROR_MESSAGE);
                    } else {
                        record.setTraderName(value.toString().trim());
                        needToSave = true;
                    }
                    break;
                case 2: //Active
                    if (value == null) {
                        record.setActive(false);
                    } else {
                        record.setActive((Boolean) value);
                    }
                    needToSave = true;
                    break;
                case 3: //Phone
                    if (value == null) {
                        record.setPhone(null);
                    } else {
                        record.setPhone(value.toString().trim());
                    }
                    needToSave = true;
                    break;
                case 4: //Cus Group
                    break;
                case 5: //Expense %
                    if (value == null) {
                        record.setExpensePercent(null);
                    } else {
                        record.setExpensePercent(Float.parseFloat(value.toString()));
                    }
                    needToSave = true;
                    break;
            }
            
            saveRecord(record);
            updateCustomerName(record);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listCUS == null) {
            return 0;
        }
        return listCUS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    private void saveRecord(SupplierWithParent record){
        try{
            if(needToSave){
                dao.save(record);
                needToSave = false;
            }
        }catch(Exception ex){
            log.error("saveRecord : " + record.getTraderId() + " : " + record.getTraderName()
                + " : " + ex.getMessage());
        }finally{
            needToSave = false;
            dao.close();
        }
    }

    public List<SupplierWithParent> getListCUS() {
        return listCUS;
    }

    public void setListCUS(List<SupplierWithParent> listCUS) {
        this.listCUS = listCUS;
        fireTableDataChanged();
    }
    
    private void updateCustomerName(SupplierWithParent record){
        String cusId = record.getTraderId().replace("CUS", "");
        try{
            List<Customer> listCUSS = dao.findAllHSQL(
                    "select o from Customer o where o.traderId like '%" 
                    + cusId + "%' and o.traderId <> '" + record.getTraderId() + "'");
            if(listCUSS != null){
                if(!listCUSS.isEmpty()){
                    for(Customer cus : listCUSS){
                        cus.setTraderName(record.getTraderName() + " (" + cus.getTraderGroup().getGroupId() + ")");
                        dao.save(cus);
                    }
                }
            }
        }catch(Exception ex){
            log.error("updateCustomerName : " + ex.toString());
        }finally{
            dao.close();
        }
    }
}
