/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.LabMachine;
import com.cv.app.opd.database.entity.OPDLabResultInd;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.NumberUtil;
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
public class LabResultTableModelInd extends AbstractTableModel {

    static Logger log = Logger.getLogger(LabResultTableModelInd.class.getName());
    private List<OPDLabResultInd> listResultInd = new ArrayList();
    private final String[] columnNames = {"Low Oper", "Low Value", "High Oper", 
        "High Value", "Sex", "Age FOper", "Age From", "Age TOper", "Age To", "Machine",
        "Description"
    };
    private final AbstractDataAccess dao;
    private Integer serviceId = -1;

    public LabResultTableModelInd(AbstractDataAccess dao) {
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
            case 0: //Low Oper
                return String.class;
            case 1: //Low Value
                return Float.class;
            case 2: //High Oper
                return String.class;
            case 3: //High Value
                return Float.class;
            case 4: //Sex
                return Gender.class;
            case 5: //Age FOper
                return String.class;
            case 6: //Age From
                return Integer.class;
            case 7: //Age TOper
                return String.class;
            case 8: //Age To
                return Integer.class;
            case 9: //Machine
                return LabMachine.class;
            case 10://Description
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        OPDLabResultInd result = listResultInd.get(row);

        switch (column) {
            case 0: //Low Oper
                return result.getLowVOperator();
            case 1: //Low Value
                return result.getLowValue();
            case 2: //High Oper
                return result.getHighVOperator();
            case 3: //High Value
                return result.getHighValue();
            case 4: //Sex
                return result.getSex();
            case 5: //Age FOper
                return result.getAfOperator();
            case 6: //Age From
                return result.getAgeFrom();
            case 7: //Age TOper
                return result.getAtOperator();
            case 8: //Age To
                return result.getAgeTo();
            case 9: //Machine
                return result.getMachine();
            case 10: //desp
                return result.getDesp();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDLabResultInd result = listResultInd.get(row);

        switch (column) {
            case 0: //Low Oper
                if (value != null) {
                    result.setLowVOperator(value.toString());
                } else {
                    result.setLowVOperator(null);
                }
                break;
            case 1: //Low Value
                if (value != null) {
                    result.setLowValue(NumberUtil.FloatZero(value));
                } else {
                    result.setLowValue(null);
                }
                break;
            case 2: //High Oper
                if (value != null) {
                    result.setHighVOperator(value.toString());
                } else {
                    result.setHighVOperator(null);
                }
                break;
            case 3: //High Value
                if (value != null) {
                    result.setHighValue(NumberUtil.FloatZero(value));
                } else {
                    result.setHighValue(null);
                }
                break;
            case 4: //Sex
                if (value != null) {
                    Gender sex = (Gender) value;
                    result.setSex(sex);
                } else {
                    result.setSex(null);
                }
                break;
            case 5: //Age FOper
                if (value != null) {
                    result.setAfOperator(value.toString());
                } else {
                    result.setAfOperator(null);
                }
                break;
                
            case 6: //Age From
                if (value != null) {
                    result.setAgeFrom(NumberUtil.NZeroInt(value));
                } else {
                    result.setAgeFrom(null);
                }
                break;
                
            case 7: //Age TOper
                if (value != null) {
                    result.setAtOperator(value.toString());
                } else {
                    result.setAtOperator(null);
                }
                break;
            case 8: //Age To
                if (value != null) {
                    result.setAgeTo(NumberUtil.NZeroInt(value));
                } else {
                    result.setAgeTo(null);
                }
                break;
            case 9: //Machine
                if (value != null) {
                    LabMachine machine = (LabMachine) value;
                    result.setMachine(machine);
                } else {
                    result.setMachine(null);
                }
                break;
            case 10: //Desc
                if(value != null){
                    result.setDesp(value.toString());
                }else{
                    result.setDesp(null);
                }
        }

        saveRecord(result);
        addNewRow();
    }

    private void saveRecord(OPDLabResultInd result) {
        if ((result.getHighValue() != null || result.getLowValue() != null
                || result.getSex() != null) && serviceId != -1) {
            if (result.getResultId() == null) {
                result.setResultId(serviceId);
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
        return listResultInd.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDLabResultInd> getListResult() {
        return listResultInd;
    }

    public void setListResult(List<OPDLabResultInd> listResultInd) {
        this.listResultInd = listResultInd;
        fireTableDataChanged();
    }

    public void setSelectResultId(Integer serviceId) {
        this.serviceId = serviceId;
        try {
            String strSql = "select o from OPDLabResultInd o where o.resultId = " + serviceId;
            List<OPDLabResultInd> tmpListResultInd = dao.findAllHSQL(strSql);

            if (tmpListResultInd == null) {
                tmpListResultInd = new ArrayList();
            }

            setListResult(tmpListResultInd);
        } catch (Exception ex) {
            log.error("setSelectResultId : " + ex.getMessage());
        } finally {
            dao.close();
        }
        addNewRow();
    }

    public void addNewRow() {
        int count = listResultInd.size();

        if (count == 0 || listResultInd.get(count - 1).getResultId() != null) {
            listResultInd.add(new OPDLabResultInd());
            fireTableRowsInserted(listResultInd.size() - 1, listResultInd.size() - 1);
        }
    }

    public void delete(int index) {
        if (index > listResultInd.size() - 1) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Data.",
                    "Invalid index", JOptionPane.ERROR_MESSAGE);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Result delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                try {
                    OPDLabResultInd tmpInd = listResultInd.get(index);
                    dao.open();
                    dao.beginTran();
                    String sql = "delete from opd_lab_result_ind where ind_id = "
                            + tmpInd.getIndId().toString();
                    dao.deleteSQL(sql);
                    listResultInd.remove(index);
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
