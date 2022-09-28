/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.BusinessType;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.CustomerWithParent;
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
public class CustomerSetupGridTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CustomerSetupGridTableModel.class.getName());
    private List<CustomerWithParent> listCUS = new ArrayList();
    private final String[] columnNames = {"Code", "Customer Name", "A", "Phone",
        "Cus Group", "Business Type", "Credit Days",
        "Credit Limit", "PT", "Township", "Parent"};
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
            case 5: //Business Type
                return false;
            case 6: //Credit Days
                return true;
            case 7: //Credit Limit
                return true;
            case 8: //Price Type
                return true;
            case 9: //Township
                return true;
            case 10: //Parent
                return false;
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
            case 5: //Business Type
                return String.class;
            case 6: //Credit Days
                return Integer.class;
            case 7: //Credit Limit
                return Double.class;
            case 8: //Price Type
                return String.class;
            case 9: //Township
                return String.class;
            case 10: //Parent
                return String.class;
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
            CustomerWithParent record = listCUS.get(row);

            switch (column) {
                case 0: //Code
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        return record.getStuCode();
                    } else {
                        return record.getTraderId();
                    }
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
                case 5: //Business Type
                    if (record.getBusinessType() != null) {
                        return record.getBusinessType().getDescription();
                    } else {
                        return null;
                    }
                case 6: //Credit Days
                    return record.getCreditDays();
                case 7: //Credit Limit
                    return record.getCreditLimit();
                case 8: //Price Type
                    if (record.getTypeId() != null) {
                        return record.getTypeId().getDescription();
                    } else {
                        return null;
                    }
                case 9: //Township
                    if (record.getTownship() != null) {
                        return record.getTownship().getTownshipName();
                    } else {
                        return null;
                    }
                case 10: //Parent
                    if(record.getParent() == null){
                        return null;
                    }else{
                        record.getParent().getTraderId();
                    }
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
            CustomerWithParent record = listCUS.get(row);

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
                case 5: //Business Type
                    if (value == null) {
                        record.setBusinessType(null);
                    } else {
                        record.setBusinessType((BusinessType) value);
                    }
                    needToSave = true;
                    break;
                case 6: //Credit Days
                    if(value == null){
                        record.setCreditDays(0);
                    }else{
                        Integer tmpValue = Integer.parseInt(value.toString());
                        record.setCreditDays(tmpValue);
                    }
                    needToSave = true;
                    break;
                case 7: //Credit Limit
                    if(value == null){
                        record.setCreditLimit(0);
                    }else{
                        Integer tmpValue = Integer.parseInt(value.toString());
                        record.setCreditLimit(tmpValue);
                    }
                    needToSave = true;
                    break;
                case 8: //Price Type
                    if (value != null) {
                        record.setTypeId((TraderType)value);
                    } else {
                        record.setTypeId(null);
                    }
                    needToSave = true;
                    break;
                case 9: //Township
                    if (value != null) {
                        record.setTownship((Township)value);
                    } else {
                        record.setTownship(null);
                    }
                    needToSave = true;
                    break;
            }
            
            saveRecord(record);
            updateCustomerName(record);
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
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
    
    private void saveRecord(CustomerWithParent record){
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

    public List<CustomerWithParent> getListCUS() {
        return listCUS;
    }

    public void setListCUS(List<CustomerWithParent> listCUS) {
        this.listCUS = listCUS;
        fireTableDataChanged();
    }
    
    private void updateCustomerName(CustomerWithParent record){
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
