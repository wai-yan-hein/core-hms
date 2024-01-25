/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.MUKey;
import com.cv.app.opd.database.entity.MUsage;
import com.cv.app.opd.database.tempentity.LabUsage;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LabUsageChoiceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LabUsageChoiceTableModel.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<LabUsage> list = new ArrayList();
    private final String[] columnNames = {"Description", "Qty", "Use"};
    private String vouNo;
    private int serviceId;
    private int qty;

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
            case 0: //Description
                return String.class;
            case 1: //Qty
                return String.class;
            case 2: //Use
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        LabUsage record = list.get(row);

        switch (column) {
            case 0: //Description
                return record.getDesc();
            case 1: //Qty
                return record.getQty();
            case 2: //Use
                return record.getUse();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column == 2) {
            LabUsage record = list.get(row);
            if (value != null) {
                record.setUse((boolean) value);
            } else {
                record.setUse(false);
            }
            fireTableCellUpdated(row, column);
            saveRecord(record);
        }
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<LabUsage> getList() {
        return list;
    }

    public void setList(List<LabUsage> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    private void saveRecord(LabUsage record) {
        try {
            if (record.getUse()) {
                String medId = record.getMedId() ;
                //insertStockFilterCodeMed("'" + medId + "'");
                //calculateMed();

                MUKey mkey = new MUKey();
                mkey.setMedId(medId);
                mkey.setVouType("OPD");
                mkey.setVouNo(vouNo);
                mkey.setServiceId(serviceId);

                MUsage mu = new MUsage();
                mu.setKey(mkey);
                mu.setCreatedDate(new Date());
                mu.setLocation(record.getLocationId());
                mu.setQtySmallest(record.getQtySmallest());
                //double sCost = getSmallestCost(medId);
                double sCost = 0d;
                mu.setSmallestCost(sCost);
                mu.setTtlCost((sCost * mu.getQtySmallest()) * qty);
                mu.setUnitId(record.getUnitId());
                mu.setUnitQty(record.getUnitQty());

                dao.save(mu);
            } else { //Delete
                String strDelete = "delete from med_usaged where vou_no ='" + vouNo
                        + "' and vou_type = 'OPD' and service_id = " + serviceId
                        + " and med_id = '" + record.getMedId() + "'";
                dao.execSql(strDelete);
            }
        } catch (Exception ex) {
            log.error("saveRecord : " + ex.getMessage());
        }
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
}
