/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RetInDetailHis;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.pharmacy.database.view.ReturnInItemList;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RetInTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetInTableModel.class.getName());
    private List<RetInDetailHis> listDetail;
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private final AbstractDataAccess dao;
    private final MedicineUP medUp;
    private final MedInfo medInfo;
    private String deletedList;
    private String cusType = "N";
    private boolean canEdit = true;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private int maxUniqueId = 0;

    public RetInTableModel(List<RetInDetailHis> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo) {
        this.listDetail = listDetail;
        this.dao = dao;
        this.medUp = medUp;
        this.medInfo = medInfo;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (!canEdit) {
            return false;
        }
        return !(column == 1 || column == 2 || column == 7);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
            case 1: //Medicine Name
            case 2: //Relation-Str
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 4: //Qty
                return Integer.class;
            case 6: //Price
            case 7: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }

        try {
            RetInDetailHis record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getMedicineId() == null) {
                            return null;
                        } else {
                            return record.getMedicineId().getShortName();
                        }
                    } else {
                        if (record.getMedicineId() == null) {
                            return null;
                        } else {
                            return record.getMedicineId().getMedId();
                        }
                    }
                case 1: //Medicine Name
                    if (record.getMedicineId() == null) {
                        return null;
                    } else {
                        return record.getMedicineId().getMedName();
                    }
                case 2: //Relation-Str
                    if (record.getMedicineId() == null) {
                        return null;
                    } else {
                        return record.getMedicineId().getRelStr();
                    }
                case 3: //Exp-Date
                    return DateUtil.toDateStr(record.getExpireDate());
                case 4: //Qty
                    return record.getQty();
                case 5: //Unit
                    return record.getUnit();
                case 6: //Pur Price
                    return record.getPrice();
                case 7: //Amount
                    return record.getAmount();
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
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
        int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
        UnitAutoCompleter unitPopup;

        try {
            RetInDetailHis record = listDetail.get(row);
            String medId = listDetail.get(parent.getSelectedRow()).getMedicineId().getMedId();
            switch (column) {
                case 0: //Code
                    record.getMedicineId().setMedId((String) value);
                    dao.open();
                    medInfo.getMedInfo((String) value);
                    dao.close();
                    record.setAmount(null);
                    record.setPrice(null);
                    record.setUnit(null);
                    record.setQty(null);
                    parent.setColumnSelectionInterval(4, 4);
                    break;
                case 1: //Medicine Name
                    record.getMedicineId().setMedName((String) value);
                    break;
                case 2: //Relation-Str
                    record.getMedicineId().setRelStr((String) value);
                    break;
                case 3: //Exp-Date
                    if (DateUtil.isValidDate(value)) {
                        record.setExpireDate(DateUtil.toDate(value));
                    }
                    break;
                case 4: //Qty
                    String tmpQtyStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setQty(NumberUtil.NZeroFloat(tmpQtyStr));
                    //For unit popup
                    if (medUp.getUnitList(medId).size() > 1) {
                        unitPopup = new UnitAutoCompleter(x, y,
                                medUp.getUnitList(medId), Util1.getParent());

                        if (unitPopup.isSelected()) {
                            record.setUnit(unitPopup.getSelUnit());
                        }
                    } else {
                        record.setUnit(medUp.getUnitList(medId).get(0));
                    }

                    ItemUnit prvUnit = record.getSaleUnit();
                    double prvPrice = NumberUtil.NZero(record.getSalePrice());
                    if (record.getUnit() != null) {
                        if(prvPrice == 0){
                            String key = medId + "-" + record.getUnit().getItemUnitCode();
                            record.setPrice(medUp.getPrice(key, cusType, record.getQty()));
                        }else{
                            String key = medId + "-" + prvUnit.getItemUnitCode();
                            float oldSmallQty = medUp.getQtyInSmallest(key);
                            key = medId + "-" + record.getUnit().getItemUnitCode();
                            float newSmallQty = medUp.getQtyInSmallest(key);
                            
                            if(!prvUnit.getItemUnitCode().equals(record.getUnit().getItemUnitCode())){
                                double newPrice = prvPrice*(newSmallQty/oldSmallQty);
                                record.setPrice(newPrice);
                            }
                        }
                    }
                    
                    try {
                        String key = medId + "-" + record.getUnit().getItemUnitCode();
                        calculateMed(medId);
                        double smallestCost = getSmallestCost(medId);
                        float smallestQty = medUp.getQtyInSmallest(key);
                        double unitCost = smallestCost * smallestQty;
                        record.setCostPrice(unitCost);
                    } catch (Exception ex) {
                        log.error("qty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    } finally {
                        dao.close();
                    }
                    
                    if (Util1.getPropValue("system.sale.FocusPrice").equals("N")) {
                        if (record.getQty() > 0) {
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            parent.setColumnSelectionInterval(0, 0);
                            //parent.setColumnSelectionInterval(6, 6);
                        }
                    }
                    calculateAmount(row);
                    break;
                case 5: //Unit
                    record.setUnit((ItemUnit) value);
                    break;
                case 6: //Price
                    String tmpPriceStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setPrice(Double.valueOf(tmpPriceStr));
                    calculateAmount(row);
                    break;
                case 7: //Amount
                    record.setAmount(Double.valueOf(value.toString()));
                default:
                    System.out.println("invalid index");
            }
            fireTableCellUpdated(row, 7);
        } catch (Exception ex) {
            log.error("setValueAt ; " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        if (column != 0) {
            fireTableCellUpdated(row, column);
        }
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listDetail == null) {
            return false;
        }
        if (listDetail.isEmpty()) {
            return false;
        }

        RetInDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getMedicineId().getMedId() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            RetInDetailHis record = new RetInDetailHis();
            record.setMedicineId(new Medicine());
            listDetail.add(record);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    public void setMed(Medicine med, int pos) {
        if (listDetail == null) {
            return;
        }
        if (listDetail.isEmpty()) {
            return;
        }

        RetInDetailHis record = listDetail.get(pos);

        record.setMedicineId(med);
        record.setAmount(null);
        record.setPrice(null);
        record.setUnit(null);
        record.setQty(null);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableCellUpdated(pos, 0);
        parent.setColumnSelectionInterval(3, 3);
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }
        if (listDetail.isEmpty()) {
            return;
        }

        RetInDetailHis record = listDetail.get(row);

        if (record != null) {
            if (deletedList == null) {
                deletedList = "'" + record.getRetInDetailId() + "'";
            } else {
                deletedList = deletedList + ",'" + record.getRetInDetailId() + "'";
            }
        }

        listDetail.remove(row);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);
    }

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        RetInDetailHis ridh = listDetail.get(row);
        String key = "";
        double amount;

        if (ridh.getMedicineId() != null && ridh.getUnit() != null) {
            key = ridh.getMedicineId().getMedId() + "-" + ridh.getUnit().getItemUnitCode();
        }

        ridh.setSmallestQty(NumberUtil.NZeroFloat(ridh.getQty())
                * NumberUtil.NZeroFloat(medUp.getQtyInSmallest(key)));

        amount = NumberUtil.NZero(ridh.getQty()) * NumberUtil.NZero(ridh.getPrice());

        ridh.setAmount(amount);
    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;
        int recordCnt = 0;
        if (listDetail != null) {
            for (RetInDetailHis record : listDetail) {
                if (record.getMedicineId().getMedId() != null) {
                    recordCnt++;
                    if (NumberUtil.NZero(record.getQty()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Qty must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (NumberUtil.NZero(record.getPrice()) < 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Price must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else {
                        if (NumberUtil.NZeroInt(record.getUniqueId()) == 0) {
                            record.setUniqueId(row + 1);
                            row++;
                        }
                    }
                }
            }
        }

        if (recordCnt == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No return in record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        maxUniqueId = row;
        parent.setRowSelectionInterval(0, 0);

        return status;
    }

    public void setListDetail(List<RetInDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                RetInDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from ret_in_detail_his where ret_in_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public void addRetInItem(ReturnInItemList retInItem) {
        if (listDetail == null) {
            return;
        }

        int index = listDetail.size() - 1;
        RetInDetailHis ridh = new RetInDetailHis();

        if (index < 0) {
            index = 0;
        }

        ridh.setSaleIvId(retInItem.getKey().getSaleInvId());
        ridh.setMedicineId(retInItem.getKey().getItem());
        ridh.setExpireDate(retInItem.getExpDate());
        ridh.setQty(retInItem.getSaleQty());
        ridh.setPrice(retInItem.getSalePrice());
        ridh.setUnit(retInItem.getSaleUnit());
        ridh.setAmount(retInItem.getSaleQty() * retInItem.getSalePrice());
        ridh.setSmallestQty(retInItem.getSaleQtySmallest());
        ridh.setSalePrice(retInItem.getSalePrice());
        ridh.setSaleUnit(retInItem.getSaleUnit());
        
        listDetail.add(index, ridh);
        fireTableRowsInserted(listDetail.size() - 2, listDetail.size() - 1);
    }

    public String getCusType() {
        return cusType;
    }

    public void setCusType(String cusType) {
        this.cusType = cusType;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public List<RetInDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.RetInDetailHis"
                + " WHERE medicineId.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void clear() {
        maxUniqueId = 0;
    }
    
    private void calculateMed(String medId) {
        try {
            String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                    + Global.machineId + "'";
            String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                    + Global.machineId + "'";
            String strMethod = "AVG";

            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();
            insertStockFilterCodeMed(medId);
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
    
    private void insertStockFilterCodeMed(String MedId) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date < '" + DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true and m.med_id in ('" + MedId + "')";

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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
