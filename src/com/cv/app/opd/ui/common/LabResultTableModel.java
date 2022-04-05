/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.OPDLabResult;
import com.cv.app.opd.database.entity.OPDResultType;
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
public class LabResultTableModel extends AbstractTableModel {
    
    static Logger log = Logger.getLogger(LabResultTableModel.class.getName());
    private List<OPDLabResult> listResult = new ArrayList();
    private final String[] columnNames = {"Printed Text", "Unit", "Normal", "Type", "Sort Order"};
    private final AbstractDataAccess dao;
    private int serviceId = -1;
    
    public LabResultTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }
    
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Printed Text
                return String.class;
            case 1: //Unit
                return String.class;
            case 2: //Normal
                return String.class;
            case 3: //Type
                return OPDResultType.class;
            case 4: //Sort Order
                return Integer.class;
            default:
                return Object.class;
        }
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        if (listResult == null) {
            return null;
        }
        
        if (listResult.isEmpty()) {
            return null;
        }
        
        OPDLabResult result = listResult.get(row);
        
        switch (column) {
            case 0: //Printed Text
                return result.getResultText();
            case 1: //Unit
                return result.getResultUnit();
            case 2: //Normal
                return result.getResultNormal();
            case 3: //Type
                return result.getResultType();
            case 4: //Sort Order
                return result.getSortOrder();
            default:
                return null;
        }
    }
    
    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listResult == null) {
            return;
        }
        
        if (listResult.isEmpty()) {
            return;
        }
        
        OPDLabResult result = listResult.get(row);
        
        switch (column) {
            case 0: //Printed Text
                if (value != null) {
                    result.setResultText(value.toString());
                } else {
                    result.setResultText(null);
                }
                break;
            case 1: //Unit
                if (value != null) {
                    result.setResultUnit(value.toString());
                } else {
                    result.setResultUnit(null);
                }
                break;
            case 2: //Normal
                if (value != null) {
                    result.setResultNormal(value.toString());
                } else {
                    result.setResultNormal(null);
                }
                break;
            case 3: //Type
                if (value != null) {
                    OPDResultType typeId = (OPDResultType) value;
                    result.setResultType(typeId);
                } else {
                    result.setResultType(null);
                }
                break;
            case 4: //Sort Order
                if (value != null) {
                    result.setSortOrder((int) value);
                } else {
                    result.setSortOrder(null);
                }
        }
        
        saveRecord(result);
        addNewRow();
    }
    
    private void saveRecord(OPDLabResult result) {
        if ((result.getResultId() != null
                || !Util1.getString(result.getResultNormal(), "-").equals("-")
                || !Util1.getString(result.getResultText(), "-").equals("-")
                || result.getResultType() != null
                || !Util1.getString(result.getResultUnit(), "-").equals("-")) && serviceId != -1) {
            if (result.getServiceId() == null) {
                result.setServiceId(serviceId);
            }
            
            try {
                dao.save(result);
            } catch (Exception ex) {
                log.error("saveRecord : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            }
        }
    }
    
    @Override
    public int getRowCount() {
        return listResult.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public List<OPDLabResult> getListResult() {
        return listResult;
    }
    
    public void setListResult(List<OPDLabResult> listResult) {
        this.listResult = listResult;
        fireTableDataChanged();
    }
    
    public void setSelectTestId(Integer serviceId) {
        if (serviceId == null) {
            this.serviceId = -1;
        } else {
            this.serviceId = serviceId;
        }
        try {
            String strSql = "select o from OPDLabResult o where o.serviceId = " 
                    + serviceId + " order by o.sortOrder";
            List<OPDLabResult> tmpListResult = dao.findAllHSQL(strSql);
            
            if (tmpListResult == null) {
                tmpListResult = new ArrayList();
            }
            
            setListResult(tmpListResult);
            addNewRow();
        } catch (Exception ex) {
            log.error("setSelectTestId : " + ex.toString());
        }finally{
            dao.close();
        }
    }
    
    public void addNewRow() {
        int count = listResult.size();
        
        if (count == 0 || listResult.get(count - 1).getServiceId() != null) {
            listResult.add(new OPDLabResult());
            fireTableRowsInserted(listResult.size() - 1, listResult.size() - 1);
        }
    }
    
    public void delete(int index) {
        if (index > listResult.size() - 1) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Data.",
                    "Invalid index", JOptionPane.ERROR_MESSAGE);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Result delete", JOptionPane.YES_NO_OPTION);
            
            if (yes_no == 0) {
                try {
                    OPDLabResult tmpInd = listResult.get(index);
                    dao.open();
                    dao.beginTran();
                    String sql = "delete from opd_lab_result where result_id = "
                            + tmpInd.getResultId().toString();
                    dao.deleteSQL(sql);
                    listResult.remove(index);
                    fireTableRowsDeleted(index, index);
                    addNewRow();
                } catch (Exception e) {
                    dao.rollBack();
                    log.error("delete : " + e);
                } finally {
                    dao.close();
                }
            }
        }
    }
}
