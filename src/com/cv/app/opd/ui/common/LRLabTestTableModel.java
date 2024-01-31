/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.LabMachine;
import com.cv.app.opd.database.entity.MUKey;
import com.cv.app.opd.database.entity.MUsage;
import com.cv.app.opd.database.entity.OPDDetailHis;
import com.cv.app.opd.database.entity.OPDMedUsage;
import com.cv.app.opd.database.tempentity.LabUsage;
import com.cv.app.opd.database.view.VOpd;
import com.cv.app.opd.ui.util.JavaFXHTMLEditor;
import com.cv.app.opd.ui.util.LabUsageChoiceDialog;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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
        "Lab Tech", "Print", "Lab Machine", "Result Order", "Comments"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3 || column == 4 || column == 5 || column == 6 || column == 7;
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
                return record.getTechName();
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
                        if (value instanceof Doctor) {
                            try {
                                Doctor patho = (Doctor) value;
                                OPDDetailHis odh = (OPDDetailHis) dao.find(OPDDetailHis.class, detailId);
                                if (odh != null) {
                                    odh.setTechnician(patho);
                                    //odh.setPathoId(patho.getPathoId());
                                    dao.save(odh);
                                    record.setTechId(patho.getDoctorId());
                                    record.setTechName(patho.getDoctorName());
                                }
                            } catch (Exception ex) {
                                log.error("Patho assign : " + ex.getMessage());
                            } finally {
                                dao.close();
                            }
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
                            LabMachine lm = (LabMachine) value;
                            odh.setLabMachineId(lm.getlMachineId());
                            dao.save(odh);
                            record.setLabMachineId(lm.getlMachineId());
                            record.setLabMachineName(lm.getlMachineName());
                            assignLabStockInfo(record, lm.getlMachineId());
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
                if (value != null) {
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
                } else {
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

    public void showCommentDialog(int row) {
        VOpd record = listVOPD.get(row);
        String detailId = record.getKey().getOpdDetailId();
        JavaFXHTMLEditor.showEditor(record.getServiceName(), detailId, "LT");
    }

    public void setMachine(int row, LabMachine machine) {
        try {
            VOpd record = listVOPD.get(row);
            String detailId = record.getKey().getOpdDetailId();
            OPDDetailHis odh = (OPDDetailHis) dao.find(OPDDetailHis.class, detailId);
            if (machine != null) {
                if (odh != null) {
                    odh.setLabMachineId(machine.getlMachineId());
                    dao.save(odh);
                    record.setLabMachineId(machine.getlMachineId());
                    record.setLabMachineName(machine.getlMachineName());
                }
            } else {
                if (odh != null) {
                    odh.setLabMachineId(null);
                    dao.save(odh);
                    record.setLabMachineId(null);
                    record.setLabMachineName(null);
                }
            }
            fireTableCellUpdated(row, 5);
        } catch (Exception ex) {
            log.error("Lab Machine : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    private void assignLabStockInfo(VOpd test, int machineId) {
        try {
            int serviceId = test.getKey().getServiceId();
            String vouNo = test.getKey().getVouNo();
            String strSql = "select o from OPDMedUsage o where o.key.service = "
                    + serviceId + " and o.key.labMachine = " + machineId;
            List<OPDMedUsage> listOMU = dao.findAllHSQL(strSql);
            /*String medIds = getMedId(listOMU);
            
            log.info("Start cost calculation.");
            insertStockFilterCodeMed(medIds);
            DateUtil.setStartTime();
            calculateMed();
            log.info("Cost calculate duration : " + DateUtil.getDuration());*/
            
            dao.execSql("delete from med_usaged where vou_type = 'OPD' and vou_no = '" + vouNo + "'");

            boolean needToAsk = false;
            for (OPDMedUsage omu : listOMU) {
                if (omu.getCalcStatus().equals("ASK")) {
                    needToAsk = true;
                } else {
                    MUKey mkey = new MUKey();
                    String medId = omu.getKey().getMed().getMedId();
                    mkey.setMedId(medId);
                    mkey.setVouType("OPD");
                    mkey.setVouNo(vouNo);
                    mkey.setServiceId(omu.getKey().getService());

                    MUsage mu = new MUsage();
                    mu.setKey(mkey);
                    mu.setCreatedDate(new Date());
                    mu.setLocation(omu.getLocation().getLocationId());
                    mu.setQtySmallest(omu.getQtySmall());
                    //double sCost = getSmallestCost(medId);
                    double sCost = 0;
                    mu.setSmallestCost(sCost);
                    mu.setTtlCost((sCost * mu.getQtySmallest()) * test.getQty());
                    mu.setUnitId(omu.getUnit().getItemUnitCode());
                    mu.setUnitQty(omu.getUnitQty());
                    log.info("Insert to med_usaged");
                    dao.save(mu);
                }
            }

            if (needToAsk) { //Need to edit
                ResultSet rs1 = dao.execSQL("SELECT omu.med_id, m.med_name, omu.unit_qty, "
                        + "omu.unit_id, omu.qty_smallest, omu.location_id, omu.machine_id \n" 
                        + "FROM opd_med_usage omu \n" 
                        + "join medicine m on omu.med_id = m.med_id \n" 
                        + "WHERE service_id = " + test.getKey().getServiceId() + " and calc_status = 'ASK'");
                if(rs1 != null){
                    List<LabUsage> list = new ArrayList();
                    log.info("Show ask list");
                    while(rs1.next()){
                        LabUsage lu = new LabUsage();
                        lu.setDesc(rs1.getString("med_name"));
                        lu.setLocationId(rs1.getInt("location_id"));
                        lu.setMedId(rs1.getString("med_id"));
                        lu.setQty(rs1.getString("unit_qty") + " " + rs1.getString("unit_id"));
                        lu.setQtySmallest(rs1.getFloat("qty_smallest"));
                        lu.setUnitId(rs1.getString("unit_id"));
                        lu.setUnitQty(rs1.getFloat("unit_qty"));
                        
                        list.add(lu);
                    }
                    
                    LabUsageChoiceDialog dailog = new LabUsageChoiceDialog(test.getKey().getVouNo(),
                    test.getKey().getServiceId(), list, test.getQty());
                    dailog.setLocationRelativeTo(null);
                    dailog.setVisible(true);
                }
            }

            test.setCompleteStatus(Boolean.TRUE);
            dao.execSql("update opd_details_his set complete_status = true where opd_detail_id = '" + test.getKey().getOpdDetailId() + "'");
        } catch (Exception ex) {
            log.error("assignLabStockInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    private double getSmallestCost(String medId) {
        double cost = 0.0;
        List<StockCosting> listStockCosting = null;

        try {
            listStockCosting = dao.findAllHSQL(
                    "select o from StockCosting o where o.key.medicine.medId = '" + medId
                    + "' and o.key.userId = '" + Global.machineId + "' "
                    + "and o.key.tranOption = 'Opening'"
            );
        } catch (Exception ex) {
            log.error("getSmallestCost : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        } finally {
            dao.close();
        }

        if (listStockCosting != null) {
            if (!listStockCosting.isEmpty()) {
                StockCosting sc = listStockCosting.get(0);
                double tmpCost = NumberUtil.NZero(sc.getTtlCost());
                float tmpBalQty = NumberUtil.NZeroFloat(sc.getBlaQty());
                if (tmpBalQty != 0) {
                    cost = tmpCost / tmpBalQty;
                }
            }
        }
        return cost;
    }

    private String getMedId(List<OPDMedUsage> listOMU) {
        String medIds = "";
        for (OPDMedUsage omu : listOMU) {
            if (medIds.isEmpty()) {
                medIds = "'" + omu.getKey().getMed().getMedId() + "'";
            } else {
                medIds = medIds + ",'" + omu.getKey().getMed().getMedId() + "'";
            }
        }

        return medIds;
    }

    private void insertStockFilterCodeMed(String medId) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date < '" + DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true and m.med_id in (" + medId + ")";

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void calculateMed() {
        try {
            String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                    + Global.machineId + "'";
            String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                    + Global.machineId + "'";
            String strMethod = "AVG";

            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();

            String tmpDate = DateUtil.getTodayDateStr();
            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(tmpDate), "Opening",
                    Global.machineId);

            dao.execProc("insert_cost_detail",
                    "Opening", DateUtil.toDateStrMYSQL(tmpDate),
                    Global.machineId, strMethod);
            dao.commit();

        } catch (Exception ex) {
            log.error("calculate : " + ex.toString());
        } finally {
            dao.close();
        }
    }
}
