/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.StockOpeningDetailHis;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockOpTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(StockOpTableModel.class.getName());
    private List<StockOpeningDetailHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Unit", "Cost Price"};
    private JTable parent;
    private final AbstractDataAccess dao;
    private final MedicineUP medUp;
    private final MedInfo medInfo;
    private String strOpDate;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private String deleteList = "-";
    private int maxUniqueId = 0;

    public StockOpTableModel(AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo) {
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
                        //dao.open();
                        //medInfo.getMedInfo((String) value);
                        //dao.close();
                        assignMed(value.toString(), row);
                    }
                    record.setUnit(null);
                    record.setQty(null);
                    record.setExpDate(null);
                    break;
                case 1: //Medicine Name
                    //record.getMed().setMedName((String) value);
                    break;
                case 2: //Relation-Str
                    //record.getMed().setRelStr((String) value);
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
                    //parent.setColumnSelectionInterval(5, 5);
                    //For unit popup
                    if (medUp.getUnitList(medId).size() > 1) {
                        unitPopup = new UnitAutoCompleter(x, y,
                                medUp.getUnitList(medId), Util1.getParent());

                        if (unitPopup.isSelected()) {
                            record.setUnit(unitPopup.getSelUnit());
                            //parent.setRowSelectionInterval(row + 1, row + 1);
                            //parent.setColumnSelectionInterval(0, 0);
                        }
                    } else {
                        record.setUnit(medUp.getUnitList(medId).get(0));
                    }

                    try {
                        String tmpMedId = record.getMed().getMedId();
                        String key = tmpMedId + "-" + record.getUnit().getItemUnitCode();
                        calculateMed(tmpMedId);
                        double smallestCost = getSmallestCost(tmpMedId);
                        float smallestQty = medUp.getQtyInSmallest(key);
                        double unitCost = smallestCost * smallestQty;
                        record.setCostPrice(unitCost);
                    } catch (Exception ex) {
                        log.error("qty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    } finally {
                        dao.close();
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
            //saveRecord(record);
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
                    //fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
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
            //record.setUserId(Global.loginUser.getUserId());

            if (!hasEmptyRow()) {
                addEmptyRow();
            }

            //saveRecord(record);
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
        int yes_no = -1;

        if (opdh.getMed() != null) {
            if (opdh.getMed().getMedId() != null) {
                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
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

                        //deleteRecord(opdh.getTranId());
                        //fireTableRowsDeleted(row, row);
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

        //parent.setRowSelectionInterval(row, row);
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
            listDetail = new ArrayList();
        }
        System.gc();
        addEmptyRow();
        fireTableDataChanged();
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

    private void calculate(String vouNo, String option) {
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
            String tmpDate = DateUtil.subDateTo(DateUtil.toDate(strOpDate), -1);
            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(tmpDate), "Opening",
                    Global.machineId);
            if (strMethod.equals("AVG") || strMethod.equals("FIFO")) {
                dao.execProc("insert_cost_detail",
                        "Opening", DateUtil.toDateStrMYSQL(tmpDate),
                        Global.machineId, strMethod);
            } else {
                insertCostDetailPurOP("Opening", DateUtil.toDateStrMYSQL(tmpDate),
                        strMethod);
            }
            dao.commit();
        } catch (Exception ex) {
            log.error("calculate : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void insertCostDetailPurOP(String costFor, String costDate, String method) {
        String userId = Global.machineId;
        String strDelete = "delete from tmp_costing_detail where cost_for = '" + costFor + "' and user_id = '" + userId + "'";

        String strSql = "select tsc.med_id item_id, bal_qty ttl_stock, cost_price.tran_date, cost_price.tran_option, \n"
                + "         cost_price.ttl_qty, cost_price.smallest_cost, cost_price, item_unit\n"
                + "    from tmp_stock_costing tsc, \n"
                + "         (select 'Purchase' tran_option, vpur.med_id item_id, pur_date tran_date, \n"
                + "                 sum(pur_smallest_qty+ifnull(pur_foc_smallest_qty,0)) ttl_qty, pur_unit_cost cost_price, \n"
                + "                 (pur_unit_cost/vm.smallest_qty) smallest_cost, vpur.pur_unit item_unit\n"
                + "            from v_purchase vpur, (select med_id, min(op_date) op_date\n"
                + "					from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                    group by med_id) tsf,\n"
                + "				 v_medicine vm\n"
                + "           where vpur.med_id = tsf.med_id and deleted = false and date(pur_date) >= op_date\n"
                + "			 and vpur.med_id = vm.med_id and vpur.pur_unit = vm.item_unit\n"
                + "			 and date(pur_date) <= prm_cost_date and vm.active = true\n"
                + "           group by vpur.med_id, pur_date, pur_unit_cost, vpur.pur_unit\n"
                + "           union all\n"
                + "          select 'Opening' tran_option, vso.med_id item_id, vso.op_date tran_date, \n"
                + "				 sum(vso.op_smallest_qty) ttl_qty, vso.cost_price, \n"
                + "				 (vso.cost_price/vm.smallest_qty) smallest_cost, vso.item_unit\n"
                + "            from v_stock_op vso, tmp_stock_filter tsf, v_medicine vm\n"
                + "		   where vso.med_id = tsf.med_id and vso.location = tsf.location_id\n"
                + "             and vso.med_id = vm.med_id and vso.item_unit = vm.item_unit\n"
                + "             and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                + "			 and vm.active = true\n"
                + "           group by vso.med_id, vso.op_date, vso.cost_price, vso.item_unit) cost_price\n"
                + "   where tsc.med_id = cost_price.item_id and tsc.user_id = prm_user_id and tsc.tran_option = prm_cost_for\n"
                + "   order by item_id, cost_price.tran_date desc, cost_price desc";

        if (method.equals("AVG (OP&PUR)")) {
            strSql = "select tsc.med_id item_id, bal_qty as ttl_stock, prm_cost_date as tran_date, '-' as tran_option, \n"
                    + "         cost_price.ttl_qty, if(cost_price.ttl_qty=0,0,(cost_price.ttl_cost/cost_price.ttl_qty)) as smallest_cost, \n"
                    + "         cost_price.ttl_cost as cost_price, '-' as item_unit\n"
                    + "    from tmp_stock_costing tsc, \n"
                    + "         (select a.item_id, sum(a.ttl_qty) as ttl_qty, sum(a.ttl_cost) as ttl_cost"
                    + "            from ("
                    + "          select 'Purchase' tran_option, vpur.med_id item_id, \n"
                    + "                 sum(pur_smallest_qty+ifnull(pur_foc_smallest_qty,0)) ttl_qty, sum(pur_qty*pur_unit_cost) ttl_cost\n"
                    + "            from v_purchase vpur, (select med_id, min(op_date) op_date\n"
                    + "								     from tmp_stock_filter where user_id = prm_user_id\n"
                    + "                                    group by med_id) tsf,\n"
                    + "				 v_medicine vm\n"
                    + "           where vpur.med_id = tsf.med_id and deleted = false and date(pur_date) >= op_date\n"
                    + "			 and vpur.med_id = vm.med_id and vpur.pur_unit = vm.item_unit\n"
                    + "			 and date(pur_date) <= prm_cost_date\n"
                    + "           group by vpur.med_id\n"
                    + "           union all\n"
                    + "          select 'Opening' tran_option, vso.med_id item_id, \n"
                    + "				 sum(vso.op_smallest_qty) ttl_qty, sum(vso.op_qty*vso.cost_price) ttl_cost\n"
                    + "            from v_stock_op vso, tmp_stock_filter tsf, v_medicine vm\n"
                    + "		   where vso.med_id = tsf.med_id and vso.location = tsf.location_id\n"
                    + "             and vso.med_id = vm.med_id and vso.item_unit = vm.item_unit\n"
                    + "             and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "           group by vso.med_id) a group by a.item_id) cost_price\n"
                    + "   where tsc.med_id = cost_price.item_id and tsc.user_id = prm_user_id and tsc.tran_option = 'Opening'\n"
                    + "   order by item_id";
        }

        strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                .replace("prm_cost_date", "'" + costDate + "'")
                .replace("prm_cost_for", "'" + costFor + "'");
        //log.info("strSql : " + strSql);
        try {
            dao.execSql(strDelete);
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                String prvItemId = "-";
                String itemId;
                Double totalStock;
                String tranDate;
                String tranOption;
                Double ttlQty;
                Double smallestCost;
                Double unitCost;
                String unit;
                Double leftStock = 0.0;
                Double prvTtlStock = 0.0;
                String prvTranDate = "-";
                Double prvTtlQty = 0.0;
                Double prvCost = 0.0;
                Double prvLeftStock = 0.0;
                Double prvSmallestCost = 0.0;
                String prvTranOption = "-";
                Double costQty;

                while (rs.next()) {
                    itemId = rs.getString("item_id");
                    if (itemId.equals("101094")) {
                        log.info("Error Tran : " + itemId);
                    }
                    totalStock = rs.getDouble("ttl_stock");
                    tranDate = DateUtil.toDateStrMYSQL(DateUtil.toDateStr(rs.getDate("tran_date")));
                    tranOption = rs.getString("tran_option");
                    ttlQty = rs.getDouble("ttl_qty");
                    smallestCost = rs.getDouble("smallest_cost");
                    unitCost = rs.getDouble("cost_price");
                    unit = rs.getString("item_unit");

                    if (!prvItemId.equals(itemId)) {
                        if (leftStock > 0.0) {
                            String tmpSql = "insert into tmp_costing_detail(item_id, ttl_stock, tran_date, tran_option, ttl_qty, \n"
                                    + "					cost_price, cost_qty, smallest_cost, user_id, cost_for, item_unit)\n"
                                    + "		values('" + prvItemId + "', " + prvTtlStock + ", '" + prvTranDate
                                    + "', 'ERR', " + prvTtlQty + ",\n"
                                    + prvCost + ", " + prvLeftStock + ", " + prvSmallestCost + ", '" + userId + "',\n'"
                                    + costFor + "', '" + unit + "')";
                            dao.execSql(tmpSql);
                        }

                        prvItemId = itemId;
                        prvTtlStock = totalStock;
                        prvTranDate = tranDate;
                        prvTranOption = tranOption;
                        prvTtlQty = ttlQty;
                        prvSmallestCost = smallestCost;
                        prvCost = unitCost;
                        leftStock = totalStock;
                    }

                    if (leftStock > 0) {
                        if (leftStock >= ttlQty) {
                            costQty = ttlQty;
                            leftStock = leftStock - ttlQty;
                        } else {
                            costQty = leftStock;
                            leftStock = 0.0;
                        }

                        if (costQty > 0) {
                            String tmpSql1 = "insert into tmp_costing_detail(item_id, ttl_stock, tran_date, tran_option, ttl_qty, \n"
                                    + "					cost_price, cost_qty, smallest_cost, user_id, cost_for, item_unit)\n"
                                    + "	  values('" + itemId + "', " + totalStock + ", '" + tranDate + "', '" + tranOption + "', " + ttlQty + ",\n"
                                    + unitCost + ", " + costQty + ", " + smallestCost + ", '" + userId + "',\n'"
                                    + costFor + "' , '" + unit + "')";
                            dao.execSql(tmpSql1);
                        }
                    }
                }

                //rs.close();
                if (method.equals("FIFO (OP&PUR)")) {
                    String tmpSql2 = "update tmp_stock_costing tsc, (select item_id, sum(cost_qty*smallest_cost) ttl_cost, user_id\n"
                            + "						    from tmp_costing_detail\n"
                            + "					      where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "					      group by item_id, user_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    tmpSql2 = tmpSql2.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(tmpSql2);
                } else if (method.equals("AVG (OP&PUR)")) {
                    String tmpSql3 = "update tmp_costing_detail tcd, (\n"
                            + "	select user_id, item_id, sum(ttl_qty) ttl_qty, sum(ttl_qty*smallest_cost) ttl_amt, \n"
                            + "		(sum(ttl_qty*smallest_cost)/sum(ttl_qty)) as avg_cost\n"
                            + "	  from tmp_costing_detail\n"
                            + "	  where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "	  group by user_id,item_id) avgc\n"
                            + "	set tcd.smallest_cost = avgc.avg_cost\n"
                            + "	where tcd.item_id = avgc.item_id and tcd.user_id = avgc.user_id and tcd.user_id = prm_user_id";
                    tmpSql3 = tmpSql3.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    String tmpSql4 = "update tmp_stock_costing tsc, \n"
                            + "               (select user_id, item_id, sum(cost_qty*smallest_cost) ttl_cost\n"
                            + "				  from tmp_costing_detail\n"
                            + "				 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "				 group by user_id,item_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    tmpSql4 = tmpSql4.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(tmpSql3, tmpSql4);
                }
            }
        } catch (Exception ex) {
            log.error("insertCostDetail : " + ex.toString());
        } finally {
            //dao.close();
        }
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
            String tmpDate = DateUtil.subDateTo(DateUtil.toDate(strOpDate), -1);
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

    private void insertStockFilterCode(String vouNo) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date < '" + DateUtil.toDateStrMYSQL(strOpDate) + "'";

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

    private void insertStockFilterCodeMed(String MedId) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date < '" + DateUtil.toDateStrMYSQL(strOpDate) + "'";

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

    public void reAssignCost(String vouNo, String option) {
        calculate(vouNo, option);
        int i = 1;
        if (listDetail != null) {
            for (StockOpeningDetailHis tmpSO : listDetail) {
                try {
                    if (tmpSO.getMed() != null) {
                        /*if(i == 462){
                            log.info("err check");
                        }*/
                        if (tmpSO.getUnit() != null) {
                            String tmpMedId = tmpSO.getMed().getMedId();
                            String key = tmpMedId + "-" + tmpSO.getUnit().getItemUnitCode();
                            double smallestCost = getSmallestCost(tmpMedId);
                            float smallestQty = medUp.getQtyInSmallest(key);
                            double unitCost = smallestCost * smallestQty;
                            tmpSO.setCostPrice(unitCost);
                        } else {
                            log.info("Not Process : " + tmpSO.getMed().getMedId());
                        }

                        log.info(i + " : " + tmpSO.getMed().getMedId() + " finished");
                        i++;
                    }
                } catch (Exception ex) {
                    log.error("reAssignCost : " + ex.getStackTrace()[0].getLineNumber()
                            + " - " + tmpSO.getMed().getMedId() + "  " + ex);
                } finally {
                    //dao.close();
                }
            }
            fireTableDataChanged();
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

        if (deleteList != null && !deleteList.equals("-")) {
            strSQL = "delete from stock_op_detail_his where op_detail_id in ("
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
                List<RelationGroup> listRel = dao.findAllHSQL("select o from RelationGroup o where o.medId = '"
                        + medicine.getMedId() + "' order by o.relUniqueId");
                medicine.setRelationGroupId(listRel);
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
