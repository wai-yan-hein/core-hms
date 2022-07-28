/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.StockOpeningDetailHis;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author WSwe
 */
public class StockOpTableModelNew extends AbstractTableModel {

    static Logger log = Logger.getLogger(StockOpTableModelNew.class.getName());
    private List<StockOpeningDetailHis> listDetail = ObservableCollections.observableList(new ArrayList());
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Unit", "Cost Price"};
    private JTable parent;
    private final AbstractDataAccess dao = Global.dao;
    private final MedicineUP medUp;
    private String strOpDate;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private String deleteList = "-";
    private int maxUniqueId = 0;

    public StockOpTableModelNew(MedicineUP medUp) {
        this.medUp = medUp;
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
        return !(column == 1 || column == 2);
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
                return Float.class;
            case 6: //Cost Price
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
            StockOpeningDetailHis record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getMed() == null) {
                            return null;
                        } else {
                            return record.getMed().getShortName();
                        }
                    } else {
                        if (record.getMed() == null) {
                            return null;
                        } else {
                            return record.getMed().getMedId();
                        }
                    }
                case 1: //Medicine Name
                    if (record.getMed() == null) {
                        return null;
                    } else {
                        return record.getMed().getMedName();
                    }
                case 2: //Relation-Str
                    if (record.getMed() == null) {
                        return null;
                    } else {
                        return record.getMed().getRelStr();
                    }
                case 3: //Exp-Date
                    return DateUtil.toDateStr(record.getExpDate());
                case 4: //Qty
                    return record.getQty();
                case 5: //Unit
                    return record.getUnit();
                case 6: //Cost Price
                    return record.getCostPrice();
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
            StockOpeningDetailHis record = listDetail.get(row);
            //String medId = listDetail.get(parent.getSelectedRow()).getMed().getMedId();
            String medId = record.getMed().getMedId();
            
            switch (column) {
                case 0: //Code
                    if (value != null) {
                        assignMed(value.toString(), row);
                    }
                    record.setUnit(null);
                    record.setQty(null);
                    record.setExpDate(null);
                    break;
                case 1: //Medicine Name
                    break;
                case 2: //Relation-Str
                    break;
                case 3: //Exp-Date
                    if (DateUtil.isValidDate(value)) {
                        record.setExpDate(DateUtil.toDate(value));
                    } else {
                        record.setExpDate(null);
                    }
                    break;
                case 4: //Qty
                    record.setQty(NumberUtil.FloatZero(value));
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
                    calculateAmount(row);
                    break;
                case 5: //Unit
                    record.setUnit((ItemUnit) value);
                    //parent.setColumnSelectionInterval(6, 6);
                    break;
                case 6: //Cost Price
                    record.setCostPrice(NumberUtil.NZero(value));
                default:
                    System.out.println("invalid index");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
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

        StockOpeningDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getMed().getMedId() == null;
    }

    public void addEmptyRow() {
        try {
            if (!hasEmptyRow()) {
                if (listDetail != null) {
                    StockOpeningDetailHis record = new StockOpeningDetailHis();
                    record.setMed(new Medicine());
                    listDetail.add(record);
                    fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
                    parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
                }
            }
        } catch (Exception ex) {
            log.error("addEmptyRow : " + ex.toString());
        }
    }

    public void setMed(Medicine med, int pos) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        try {
            StockOpeningDetailHis record = listDetail.get(pos);

            record.setMed(med);
            record.setUnit(null);
            record.setQty(null);
            record.setExpDate(null);

            if (!hasEmptyRow()) {
                addEmptyRow();
            }

            fireTableCellUpdated(pos, 0);
            parent.setRowSelectionInterval(pos, pos);
            parent.setColumnSelectionInterval(4, 4);
        } catch (Exception ex) {
            log.error("setMed : " + ex.toString());
        }
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        StockOpeningDetailHis opdh = listDetail.get(row);

        if (opdh.getMed() != null) {
            if (opdh.getMed().getMedId() != null) {
                try {
                    int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Sale item delete", JOptionPane.YES_NO_OPTION);

                    if (yes_no == 0) {
                        if (opdh.getOpDetailId() != null) {
                            if (deleteList.equals("-")) {
                                deleteList = "'" + opdh.getOpDetailId() + "'";
                            } else {
                                deleteList = deleteList + ",'" + opdh.getOpDetailId() + "'";
                            }
                        }
                        listDetail.remove(row);

                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }

                        fireTableDataChanged();
                        if (listDetail.size() >= (row - 1)) {
                            parent.setRowSelectionInterval(row - 1, row - 1);
                            parent.setColumnSelectionInterval(0, 0);
                        }
                    }
                } catch (Exception ex) {
                    log.error("delete : " + ex.toString());
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        StockOpeningDetailHis ddh = listDetail.get(row);
        String key = "";

        if (ddh.getMed() != null) {
            key = ddh.getMed().getMedId() + "-" + ddh.getUnit().getItemUnitCode();
        }

        ddh.setSmallestQty(NumberUtil.NZeroFloat(ddh.getQty())
                * medUp.getQtyInSmallest(key));
    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;

        if (listDetail != null) {
            for (StockOpeningDetailHis record : listDetail) {
                if (record.getMed() != null) {
                    if (record.getMed().getMedId() != null) {
                        if (NumberUtil.NZeroInt(record.getUniqueId()) == 0) {
                            record.setUniqueId(row + 1);
                            row++;
                        }
                    }
                }
            }
        }

        if (row == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No stock opening record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        return status;
    }

    public List<StockOpeningDetailHis> getListDetail() {
        List<StockOpeningDetailHis> tmpListDetail = new ArrayList();
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                for (StockOpeningDetailHis sodh : listDetail) {
                    if (sodh.getMed() != null && sodh.getQty() != null) {
                        tmpListDetail.add(sodh);
                    }
                }
            }
        }
        return tmpListDetail;
    }

    public void setListDetail(List<StockOpeningDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                StockOpeningDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = NumberUtil.NZeroInt(tmpD.getUniqueId());
            }
        }
        addEmptyRow();
        try {
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("setListDetail : " + ex.toString());
        }
    }

    public void clear() {
        deleteList = "-";
        maxUniqueId = 0;
        if (listDetail != null) {
            listDetail = ObservableCollections.observableList(new ArrayList());
        }
        System.gc();
        fireTableDataChanged();
        addEmptyRow();
    }

    public boolean isEmptyRow(int row) {
        if (listDetail == null) {
            return false;
        }

        boolean status = true;

        StockOpeningDetailHis tsodh = listDetail.get(row);
        if (tsodh.getMed() != null) {
            if (tsodh.getMed().getMedId() != null) {
                if (!tsodh.getMed().getMedId().isEmpty()) {
                    status = false;
                }
            }
        }

        return status;
    }

    private void calculate(String vouNo) {
        try {
            String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                    + Global.machineId + "'";
            String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                    + Global.machineId + "'";
            String strMethod = "AVG";

            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();
            insertStockFilterCode(vouNo);
            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(strOpDate), "Opening",
                    Global.machineId);
            dao.execProc("insert_cost_detail",
                    "Opening", DateUtil.toDateStrMYSQL(strOpDate),
                    Global.machineId, strMethod);
            dao.commit();
        } catch (Exception ex) {
            log.error("calculate : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void insertStockFilterCode(String vouNo) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(strOpDate) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true and m.med_id in (select distinct med_id from v_stock_op where op_id = '" + vouNo + "')";

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void setStrOpDate(String strOpDate) {
        this.strOpDate = strOpDate;
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

    public void reAssignCost(String vouNo) {
        calculate(vouNo);
        int i = 1;
        if (listDetail != null) {
            for (StockOpeningDetailHis tmpSO : listDetail) {
                try {
                    if (tmpSO.getMed() != null) {
                        String tmpMedId = tmpSO.getMed().getMedId();
                        String key = tmpMedId + "-" + tmpSO.getUnit().getItemUnitCode();
                        double smallestCost = getSmallestCost(tmpMedId);
                        float smallestQty = medUp.getQtyInSmallest(key);
                        double unitCost = smallestCost * smallestQty;
                        tmpSO.setCostPrice(unitCost);
                        log.info(i + " : " + tmpSO.getMed().getMedId() + " finished");
                        i++;
                    }
                } catch (Exception ex) {
                    log.error("reAssignCost : " + ex.getStackTrace()[0].getLineNumber()
                            + " - " + tmpSO.getMed().getMedId() + "  " + ex);
                } finally {
                    dao.close();
                }
                fireTableDataChanged();
            }
        }
    }

    public void addList(List<StockOpeningDetailHis> listDetail) {
        this.listDetail.remove(0);
        this.listDetail.addAll(listDetail);
        try {
            fireTableDataChanged();
            addEmptyRow();
        } catch (Exception ex) {
            log.error("addList : " + ex.toString());
        }
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deleteList != null) {
            strSQL = "delete from StockOpeningDetailHis where op_detail_id in ("
                    + deleteList + ")";
            deleteList = null;
        }

        return strSQL;
    }

    private void assignMed(String medId, int index) {
        try {
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medId + "' and active = true");
            if (medicine != null) {
                medUp.add(medicine);
                setMed(medicine, index);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                        "Invalid.", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("assignMed : " + ex.toString());
        } finally {
            dao.close();
        }
    }
}
