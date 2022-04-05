/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.LabMachine;
import com.cv.app.opd.database.entity.OPDDetailHis;
import com.cv.app.opd.database.entity.Pathologiest;
import com.cv.app.opd.database.view.VOpd;
import com.cv.app.opd.ui.util.JavaFXHTMLEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LRLabTestTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LRLabTestTableModel.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<VOpd> listVOPD = new ArrayList();
    private final String[] columnNames = {"Lab Test", "Date", "Ref. Doctor",
        "Lab Tech", "Print", "Lab Machine", "Result Order","Comments"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3 || column == 4 || column == 5 || column == 6;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 4:
                return Boolean.class;
            case 5:
                return LabMachine.class;
            case 6:
                return Integer.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VOpd record = listVOPD.get(row);

        switch (column) {
            case 0: //Lab test
                return record.getServiceName();
            case 1: //Date
                return DateUtil.toDateStr(record.getOpdDate());
            case 2: //Doctor
                return record.getReferDrName();
            case 3: //Patho
                return record.getPathologyName();
            case 4: //Print
                return record.getPrint();
            case 5: //Lab Machine
                return record.getLabMachineName();
            case 6: //Result Order
                return record.getResultOrder();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listVOPD == null) {
            return;
        }

        if (listVOPD.isEmpty()) {
            return;
        }

        VOpd record = listVOPD.get(row);
        String detailId = record.getKey().getOpdDetailId();

        switch (column) {
            case 3: //Patho
                if (value != null) {
                    if (value.toString().isEmpty()) {
                        try {
                            OPDDetailHis odh = (OPDDetailHis) dao.find(OPDDetailHis.class, detailId);
                            if (odh != null) {
                                odh.setPathoId(null);
                                dao.save(odh);
                                record.setPathoId(null);
                                record.setPathologyName(null);
                            }
                        } catch (Exception ex) {
                            log.error("Patho de assign : " + ex.getMessage());
                        } finally {
                            dao.close();
                        }
                    } else {
                        Pathologiest patho = (Pathologiest) value;
                        try {
                            OPDDetailHis odh = (OPDDetailHis) dao.find(OPDDetailHis.class, detailId);
                            if (odh != null) {
                                odh.setPathoId(patho.getPathoId());
                                dao.save(odh);
                                record.setPathoId(patho.getPathoId());
                                record.setPathologyName(patho.getPathologyName());
                            }
                        } catch (Exception ex) {
                            log.error("Patho assign : " + ex.getMessage());
                        } finally {
                            dao.close();
                        }
                    }
                } else {
                    record.setPathoId(null);
                    record.setPathologyName(null);
                }
                break;
            case 4: //Print
                if (value != null) {
                    Boolean print = (Boolean) value;
                    record.setPrint(print);
                } else {
                    record.setPrint(Boolean.FALSE);
                }
                break;
            case 5: //Lab Machine
                if (value != null) {
                    try {
                        OPDDetailHis odh = (OPDDetailHis) dao.find(OPDDetailHis.class, detailId);
                        if (odh != null) {
                            LabMachine lm = (LabMachine)value;
                            odh.setLabMachineId(lm.getlMachineId());
                            dao.save(odh);
                            record.setLabMachineId(lm.getlMachineId());
                            record.setLabMachineName(lm.getlMachineName());
                        }
                    } catch (Exception ex) {
                        log.error("Lab Machine : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                } else {
                    record.setLabMachineId(null);
                    record.setLabMachineName(null);
                }
                break;
            case 6: //Result Order
                if(value != null){
                    try {
                        Integer resultOrder = Integer.parseInt(value.toString());
                        record.setResultOrder(resultOrder);
                        OPDDetailHis odh = (OPDDetailHis) dao.find(OPDDetailHis.class, detailId);
                        if (odh != null) {
                            odh.setSortOrder(resultOrder);
                            dao.save(odh);
                            
                        }
                    } catch (Exception ex) {
                        log.error("Result Order : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }else{
                    record.setResultOrder(null);
                }
                break;
        }
        
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listVOPD.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VOpd> getListVOPD() {
        return listVOPD;
    }

    public void setListVOPD(List<VOpd> listVOPD) {
        this.listVOPD = listVOPD;
        fireTableDataChanged();
    }
    
    public void showCommentDialog(int row){
        VOpd record = listVOPD.get(row);
        Long detailId = Long.parseLong(record.getKey().getOpdDetailId().toString());
        JavaFXHTMLEditor.showEditor(record.getServiceName(), detailId, "LT");
    }
}
