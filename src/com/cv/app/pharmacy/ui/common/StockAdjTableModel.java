/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.AdjDetailHis;
import com.cv.app.pharmacy.database.entity.AdjType;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.helper.CurrencyTtl;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.pharmacy.database.tempentity.StockCostingDetail;
import com.cv.app.pharmacy.database.tempentity.TmpEXRate;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockAdjTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(StockAdjTableModel.class.getName());
    private List<AdjDetailHis> listDetail;
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Sys-Bal", "Usr-Balance", "Usr-Unit", "Currency", "Adj Qty",
        "Unit", "Type", "Balance", "Cost Price", "Amount"};
    private JTable parent;
    private final AbstractDataAccess dao;
    private final MedicineUP medUp;
    private final MedInfo medInfo;
    private AdjType defaultAdjType;
    private AdjType minusAdjType;
    private Integer locationId;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private int maxUniqueId = 0;
    private String deletedList;
    private String currency;
    private boolean editStatus = false;
    
    public StockAdjTableModel(List<AdjDetailHis> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo) {
        this.listDetail = listDetail;
        this.dao = dao;
        this.medUp = medUp;
        this.medInfo = medInfo;
        try {
            defaultAdjType = (AdjType) dao.find(AdjType.class, "+");
            minusAdjType = (AdjType) dao.find(AdjType.class, "-");
        } catch (Exception ex) {
            log.error("StockAdjTableModel : " + ex.getMessage());
        } finally {
            dao.close();
        }
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
        return !(column == 1 || column == 2
                || column == 4
                || column == 6
                || column == 9
                || column == 11
                || column == 13);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Medicine Name
                return String.class;
            case 2: //Relation-Str
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 4: //Sys-Bal
                return String.class;
            case 5: //Usr Bal
                return Float.class;
            case 6: //Usr Unit
                return String.class;
            case 7: //Currency
                return String.class;
            case 8: //Adj Qty
                return Float.class;
            case 9: //Unit
                return String.class;
            case 10: //Type
                return String.class;
            case 11: //Balance
                return String.class;
            case 12: //Cost Price
                return Double.class;
            case 13: //Amount
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
            AdjDetailHis record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getMedicineId() == null) {
                            return null;
                        } else {
                            return record.getMedicineId().getShortName();
                        }
                    } else if (record.getMedicineId() == null) {
                        return null;
                    } else {
                        return record.getMedicineId().getMedId();
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
                case 4: //Sys-Balance
                    return record.getStrSysBalance();
                case 5: //Usr-Balance
                    return record.getUsrBalQty();
                case 6: //Usr-Unit
                    if (record.getUsrBalUnit() != null) {
                        return record.getUsrBalUnit().getItemUnitCode();
                    } else {
                        return null;
                    }
                case 7: //Currency
                    return record.getCurrencyId();
                case 8: //Qty
                    return record.getQty();
                case 9: //Unit
                    return record.getUnit();
                case 10: //Adj Type
                    return record.getAdjType();
                case 11: //Balance
                    return record.getStrBalance();
                case 12: //Cost Price
                    return record.getCostPrice();
                case 13: //Amount
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
            AdjDetailHis record = listDetail.get(row);
            String medId = listDetail.get(parent.getSelectedRow()).getMedicineId().getMedId();

            switch (column) {
                case 0: //Code
                    record.getMedicineId().setMedId((String) value);
                    dao.open();
                    medInfo.getMedInfo((String) value);
                    dao.close();
                    record.setUnit(null);
                    record.setQty(null);
                    record.setExpireDate(null);
                    record.setAdjType(null);
                    record.setCurrencyId(currency);
                    assignBalance(record);
                    break;
                case 1: //Medicine Name
                    //record.getMedicineId().setMedName((String) value);
                    break;
                case 2: //Relation-Str
                    //record.getMedicineId().setRelStr((String) value);
                    break;
                case 3: //Exp-Date
                    if (DateUtil.isValidDate(value)) {
                        record.setExpireDate(DateUtil.toDate(value));
                    }
                    break;
                case 4: //Sys-Balance
                    break;
                case 5: //Usr-Balance
                    if(editStatus){
                        record.setOldSmallestQty(record.getSmallestQty());
                    }
                    if (value == null) {
                        record.setUsrBalQty(null);
                        record.setUsrBalUnit(null);
                    } else {
                        record.setUsrBalQty(NumberUtil.NZeroFloat(value));
                        if (!medUp.getUnitList(medId).isEmpty()) {
                            unitPopup = new UnitAutoCompleter(x, y,
                                    medUp.getUnitList(medId), Util1.getParent());

                            if (unitPopup.isSelected()) {
                                record.setUsrBalUnit(unitPopup.getSelUnit());
                            }
                        } else {
                            record.setUsrBalUnit(medUp.getUnitList(medId).get(0));
                        }
                        String key = medId + "-" + record.getUsrBalUnit().getItemUnitCode();
                        record.setUsrBalsmallest(record.getUsrBalQty()
                                * medUp.getQtyInSmallest(key));
                        ItemUnit smallestUnit = medUp.getSmallestUnit(medId);
                        record.setUnit(smallestUnit);
                        float diffQty = NumberUtil.FloatZero(record.getSysBalance())
                                - NumberUtil.FloatZero(record.getUsrBalsmallest());
                        if (diffQty > 0) {
                            record.setQty(diffQty);
                            record.setAdjType(minusAdjType);
                            record.setBalance(record.getSysBalance() - diffQty);
                        } else {
                            record.setQty(diffQty * -1);
                            record.setAdjType(defaultAdjType);
                            //record.setBalance(record.getSysBalance() + diffQty);
                            record.setBalance(record.getUsrBalsmallest());
                        }
                        record.setStrBalance(MedicineUtil.getQtyInStr(record.getMedicineId(), record.getBalance()));
                        assignPrice(record);
                        fireTableCellUpdated(row, 5);
                        fireTableCellUpdated(row, 6);
                        fireTableCellUpdated(row, 7);
                        fireTableCellUpdated(row, 10);
                    }
                    break;
                case 6: //Usr-Unit
                    break;
                case 7: //Currency
                    if (value == null) {
                        record.setCurrencyId(null);
                    } else {
                        Currency curr = (Currency) value;
                        record.setCurrencyId(curr.getCurrencyCode());
                    }
                    break;
                case 8: //Qty
                    if(editStatus){
                        record.setOldSmallestQty(record.getSmallestQty());
                    }
                    String tmpQtyStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setQty(NumberUtil.NZeroFloat(tmpQtyStr));
                    //For unit popup
                    if (medUp.getUnitList(medId).size() > 0) {
                        unitPopup = new UnitAutoCompleter(x, y,
                                medUp.getUnitList(medId), Util1.getParent());

                        if (unitPopup.isSelected()) {
                            record.setUnit(unitPopup.getSelUnit());
                        }
                    } else {
                        record.setUnit(medUp.getUnitList(medId).get(0));
                    }
                    record.setAdjType(defaultAdjType);
                    fireTableCellUpdated(row, 6);
                    assignPrice(record);
                    break;
                case 9: //Unit
                    record.setUnit((ItemUnit) value);
                    break;
                case 10: //Adj Type
                    record.setAdjType((AdjType) value);
                    //assignPrice(record);
                    break;
                case 12: //Cost Price
                    String tmpCostPriceStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setCostPrice(NumberUtil.NZero(tmpCostPriceStr));
                    //assignPrice(record);
                    break;
                default:
                    System.out.println("invalid index");
            }
        } catch (Exception ex) {
            log.error("setValueAt " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        calculateAmount(row);
        fireTableCellUpdated(row, 8);
        fireTableCellUpdated(row, 4);
        fireTableCellUpdated(row, 12);
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

        AdjDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getMedicineId().getMedId() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            AdjDetailHis record = new AdjDetailHis();
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

        AdjDetailHis record = listDetail.get(pos);

        record.setMedicineId(med);
        record.setUnit(null);
        record.setQty(null);
        record.setExpireDate(null);
        record.setAdjType(null);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableCellUpdated(pos, 0);
        parent.setColumnSelectionInterval(3, 3);
    }

    public void delete(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                AdjDetailHis record = listDetail.get(row);
                if (record.getAdjDetailId() != null) {
                    if (deletedList == null) {
                        deletedList = "'" + record.getAdjDetailId() + "'";
                    } else {
                        deletedList = deletedList + ",'" + record.getAdjDetailId() + "'";
                    }
                }
                listDetail.remove(row);
            }
        }

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);
    }

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        AdjDetailHis rodh = listDetail.get(row);
        String key = "";

        if (rodh.getMedicineId() != null && rodh.getAdjType() != null
                && rodh.getUnit() != null) {
            key = rodh.getMedicineId().getMedId() + "-" + rodh.getUnit().getItemUnitCode();
        }

        rodh.setSmallestQty(NumberUtil.NZeroFloat(rodh.getQty())
                * medUp.getQtyInSmallest(key));

        double amount = NumberUtil.NZero(rodh.getQty())
                * NumberUtil.NZero(rodh.getCostPrice());

        if (rodh.getAdjType() != null) {
            if (rodh.getAdjType().getAdjTypeId().equals("-")) {
                amount = -1 * amount;
            }
        }

        rodh.setAmount(amount);
    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;
        int dataCount = 0;

        if (listDetail != null) {
            for (AdjDetailHis record : listDetail) {
                if (record.getMedicineId().getMedId() != null) {
                    dataCount++;
                    if (NumberUtil.NZero(record.getQty()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (record.getAdjType() == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Adjust type must not be null.",
                                "No Adjust Type.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (NumberUtil.NZeroInt(record.getUniqueId()) == 0) {
                        record.setUniqueId(row + 1);
                        row++;
                    }
                }
            }
        }

        if (dataCount == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No adjust record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        //parent.setRowSelectionInterval(row, row);
        return status;
    }

    public void setListDetail(List<AdjDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                AdjDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    private void assignPrice(AdjDetailHis adh) {
        DateUtil.setStartTime();
        Medicine med = adh.getMedicineId();
        String key;

        if (Util1.getPropValue("system.adj.stdcost").equals("Y")) {
            String strSql = "select std_cost from v_medicine where med_id = '" + med.getMedId()
                    + "' and item_unit = '" + adh.getUnit().getItemUnitCode() + "'";
            try {
                ResultSet rs = dao.execSQL(strSql);
                if (rs != null) {
                    if (NumberUtil.NZero(adh.getCostPrice()) == 0) {
                        if (rs.next()) {
                            double stdCost = rs.getDouble("std_cost");
                            if (stdCost == 0) {
                                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid standard cost in item setup. Please Check.",
                                        "No Standard Cost", JOptionPane.ERROR_MESSAGE);
                            } else {
                                adh.setCostPrice(stdCost);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("assignPrice : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }
        } else {
            boolean localCost = false;
            try {
                List<TmpEXRate> listEXR = dao.findAllHSQL("select o from TmpEXRate o where o.key.userId = '"
                        + Global.machineId + "'");

                if (listEXR != null) {
                    if (!listEXR.isEmpty()) {
                        localCost = true;
                    }
                }
            } catch (Exception ex) {
                log.error("assignPrice TmpEXRate : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            } finally {
                dao.close();
            }
            if (Util1.getPropValue("system.multicurrency").equals("Y")) {
                if (localCost) {
                    try {
                        String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                                + Global.machineId + "'";
                        String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                                + Global.machineId + "'";
                        String strMethod = "AVG";
                        String medId = adh.getMedicineId().getMedId();

                        dao.execSql(deleteTmpData1, deleteTmpData2);
                        dao.commit();
                        dao.close();
                        insertStockFilterCodeMed(medId);
                        String tmpDate = DateUtil.getTodayDateStr();
                        dao.execProc("gen_cost_balance",
                                DateUtil.toDateStrMYSQL(tmpDate), "Opening",
                                Global.machineId);
                        calculateWithLocalExRate("Opening", DateUtil.toDateStrMYSQL(tmpDate),
                                Global.machineId, strMethod, currency);
                        key = medId + "-" + adh.getUnit().getItemUnitCode();
                        double smallestCost = getSmallestCost(medId);
                        float smallestQty = medUp.getQtyInSmallest(key);
                        double unitCost = smallestCost * smallestQty;
                        adh.setCostPrice(unitCost);
                    } catch (Exception ex) {
                        log.error("system.multicurrency : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            } else {
                String strSql = "select vp.pur_unit, vp.pur_unit_cost "
                        + "from v_purchase vp, (select med_id, pur_unit, max(pur_date) pur_date \n"
                        + "from v_purchase where med_id = '" + med.getMedId() + "') max_date where vp.pur_date = \n"
                        + "max_date.pur_date and vp.med_id = max_date.med_id and vp.pur_unit = max_date.pur_unit \n"
                        + "and ifnull(vp.pur_unit_cost,0) <> 0";

                try {
                    ResultSet rs = dao.execSQL(strSql);
                    if (rs != null) {
                        if (NumberUtil.NZero(adh.getCostPrice()) == 0) {
                            if (rs.next()) {
                                String unitCode = rs.getString("pur_unit");

                                if (unitCode.equals(adh.getUnit().getItemUnitCode())) {
                                    adh.setCostPrice(rs.getDouble("pur_unit_cost"));
                                } else {
                                    key = med.getMedId() + "-" + unitCode;
                                    float qtySmall = medUp.getQtyInSmallest(key);
                                    Double smallPrice = rs.getDouble("pur_unit_cost") / qtySmall;
                                    key = med.getMedId() + "-" + adh.getUnit().getItemUnitCode();
                                    adh.setCostPrice(smallPrice * medUp.getQtyInSmallest(key));
                                }

                            } else {
                                //Need to add adjust last price
                                strSql = "select va.med_id, va.item_unit, va.cost_price \n"
                                        + "from v_adj va, (select va.med_id, vam.adj_date, max(va.adj_detail_id) adj_detail_id\n"
                                        + "from v_adj va, (\n"
                                        + "select med_id, max(adj_date) adj_date from v_adj where med_id = '" + med.getMedId() + "' group by med_id) vam\n"
                                        + "where va.med_id = vam.med_id and date(va.adj_date) = vam.adj_date and va.med_id = '" + med.getMedId() + "') vam\n"
                                        + "where va.med_id = vam.med_id and va.adj_date = vam.adj_date and va.adj_detail_id = vam.adj_detail_id"
                                        + " and va.med_id = '" + med.getMedId() + "'";
                                rs = dao.execSQL(strSql);

                                if (rs != null) {
                                    if (rs.next()) {
                                        String unitCode = rs.getString("item_unit");
                                        key = med.getMedId() + "-" + unitCode;
                                        float qtySmall = medUp.getQtyInSmallest(key);
                                        Double smallPrice = rs.getDouble("cost_price") / qtySmall;
                                        key = med.getMedId() + "-" + adh.getUnit().getItemUnitCode();
                                        adh.setCostPrice(smallPrice * medUp.getQtyInSmallest(key));
                                    } else {
                                        key = med.getMedId() + "-" + adh.getUnit().getItemUnitCode();
                                        adh.setCostPrice(medUp.getPrice(key, "N", 0));
                                    }
                                } else {
                                    key = med.getMedId() + "-" + adh.getUnit().getItemUnitCode();
                                    adh.setCostPrice(medUp.getPrice(key, "N", 0));
                                }

                            }
                        }

                        if (rs != null) {
                            rs.close();
                        }
                    }
                    adh.setAmount(NumberUtil.NZero(adh.getCostPrice())
                            * NumberUtil.NZero(adh.getQty()));
                } catch (Exception ex) {
                    log.error("assignPrice : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                }
            }
        }
        log.info("assignPrice time taken : " + DateUtil.getDuration());
    }

    public double getTotalAmount() {
        double total = 0;

        if (listDetail != null) {
            for (AdjDetailHis adh : listDetail) {
                if (adh != null) {
                    total += NumberUtil.NZero(adh.getAmount());
                }
            }
        }
        return total;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    private void assignBalance(AdjDetailHis record) {
        DateUtil.setStartTime();
        String medId = record.getMedicineId().getMedId();
        try {
            ResultSet resultSet = dao.getPro("GET_STOCK_BALANCE_CODE",
                    locationId.toString(), medId, Global.machineId);
            if (resultSet != null) {
                float balance = 0f;
                while (resultSet.next()) {
                    balance += NumberUtil.FloatZero(resultSet.getInt("TTL_QTY"));
                }
                resultSet.close();
                if (balance != 0f) {
                    record.setSysBalance(balance);
                    String strBalance = MedicineUtil.getQtyInStr(record.getMedicineId(), balance);
                    record.setStrSysBalance(strBalance);
                } else {
                    record.setSysBalance(null);
                    record.setStrSysBalance(null);
                }
            }
        } catch (Exception ex) {
            log.error("assignBalance : " + ex.getMessage());
        }
        log.info("assignBalance time taken : " + DateUtil.getDuration());
    }

    public List<AdjDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.AdjDetailHis"
                + " WHERE medicineId.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void clear() {
        maxUniqueId = 0;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from adj_detail_his where adj_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<CurrencyTtl> getCurrTotal() {
        List<CurrencyTtl> listCTTL = new ArrayList();
        HashMap<String, CurrencyTtl> hmCTTL = new HashMap();

        listDetail.forEach(sidh -> {
            String currId = sidh.getCurrencyId();
            if (currId == null) {
                currId = "MMK";
            }
            if (currId != null) {
                if (hmCTTL.containsKey(currId)) {
                    CurrencyTtl cttl = hmCTTL.get(currId);
                    cttl.setTtlPaid(NumberUtil.NZero(cttl.getTtlPaid())
                            + NumberUtil.NZero(sidh.getAmount()));
                } else {
                    CurrencyTtl cttl = new CurrencyTtl(currId,
                            NumberUtil.NZero(sidh.getAmount()));
                    listCTTL.add(cttl);
                    hmCTTL.put(currId, cttl);
                }
            }
        });

        return listCTTL;
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

    private void calculateWithLocalExRate(String costFor, String costDate, String userId,
            String method, String curr) {
        String strSql = "select tsc.med_id item_id, bal_qty ttl_stock, cost_price.tran_date, cost_price.tran_option, \n"
                + "         cost_price.ttl_qty, cost_price.smallest_cost, cost_price, item_unit, currency_id,\n"
                + "         exr.from_curr, exr.to_curr, exr.ex_rate\n"
                + "    from tmp_stock_costing tsc, \n"
                + "         (select 'Adjust' tran_option, v_adj.med_id item_id, adj_date tran_date, \n"
                + "                 sum(adj_smallest_qty) ttl_qty, cost_price,\n"
                + "                 (cost_price/vm.smallest_qty) smallest_cost, v_adj.item_unit, v_adj.currency_id\n"
                + "            from v_adj join (select med_id, min(op_date) op_date\n"
                + "	                       from tmp_stock_filter where user_id = prm_user_id\n"
                + "                          group by med_id) tsf on v_adj.med_id = tsf.med_id\n"
                + "		    join v_medicine vm on v_adj.med_id = vm.med_id and v_adj.item_unit = vm.item_unit\n"
                + "           where deleted = false \n"
                + "             and date(adj_date) >= op_date and date(adj_date) <= prm_cost_date\n"
                + "		   group by v_adj.med_id, adj_date, v_adj.currency_id, cost_price, v_adj.item_unit\n"
                + "		   union all\n"
                + "          select 'Purchase' tran_option, vpur.med_id item_id, pur_date tran_date, \n"
                + "                 sum(pur_smallest_qty+ifnull(pur_foc_smallest_qty,0)) ttl_qty, pur_unit_cost cost_price, \n"
                + "                 (pur_unit_cost/vm.smallest_qty) smallest_cost, vpur.pur_unit item_unit, vpur.currency as currency_id\n"
                + "            from v_purchase vpur join (select med_id, min(op_date) op_date\n"
                + "								     from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                    group by med_id) tsf on vpur.med_id = tsf.med_id\n"
                + "			join v_medicine vm on vpur.med_id = vm.med_id and vpur.pur_unit = vm.item_unit\n"
                + "           where deleted = false and date(pur_date) >= op_date and date(pur_date) <= prm_cost_date\n"
                + "           group by vpur.med_id, pur_date, vpur.currency, pur_unit_cost, vpur.pur_unit\n"
                + "		   union all\n"
                + "          select 'Return-In' tran_option, vretin.med_id item_id, ret_in_date tran_date, \n"
                + "                 sum(ret_in_smallest_qty) ttl_qty, ret_in_price cost_price, \n"
                + "                 (ret_in_price/vm.smallest_qty) smallest_cost, vretin.item_unit, vretin.currency as currency_id\n"
                + "            from v_return_in vretin join (select med_id, min(op_date) op_date\n"
                + "	                                    from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                       group by med_id) tsf on vretin.med_id = tsf.med_id\n"
                + "            join v_medicine vm on vretin.med_id = vm.med_id and vretin.item_unit = vm.item_unit\n"
                + "           where deleted = false and date(ret_in_date) >= op_date and date(ret_in_date) <= prm_cost_date\n"
                + "           group by vretin.med_id, ret_in_date, vretin.currency, ret_in_price, vretin.item_unit\n"
                + "           union all\n"
                + "          select 'Opening' tran_option, vso.med_id item_id, vso.op_date tran_date, \n"
                + "				 sum(vso.op_smallest_qty) ttl_qty, vso.cost_price, \n"
                + "				 (vso.cost_price/vm.smallest_qty) smallest_cost, vso.item_unit, sp.sys_prop_value as currency_id\n"
                + "            from v_stock_op vso join tmp_stock_filter tsf on vso.med_id = tsf.med_id and vso.location = tsf.location_id\n"
                + "            join v_medicine vm on vso.med_id = vm.med_id and vso.item_unit = vm.item_unit\n"
                + "            join (select sys_prop_value from sys_prop where sys_prop_desp = 'system.app.currency') sp\n"
                + "		   where vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                + "           group by vso.med_id, vso.op_date, vso.cost_price, vso.item_unit) cost_price, \n"
                + "           (select * from tmp_ex_rate where user_id = prm_user_id) exr\n"
                + "   where tsc.med_id = cost_price.item_id and (cost_price.currency_id = exr.from_curr or cost_price.currency_id = exr.to_curr) \n"
                + "     and tsc.user_id = prm_user_id and tsc.tran_option = prm_cost_for\n"
                + "   order by item_id, cost_price.tran_date desc, cost_price desc";
        strSql = strSql.replace("prm_cost_for", "'" + costFor + "'")
                .replace("prm_cost_date", "'" + costDate + "'")
                .replace("prm_user_id", "'" + userId + "'")
                .replace("p_method", "'" + method + "'");

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                String prvMedId = "-";
                int leftStock = 0;

                while (rs.next()) {
                    StockCostingDetail scd = new StockCostingDetail();
                    String medId = rs.getString("item_id");
                    int ttlQty = rs.getInt("ttl_qty");
                    String currId = rs.getString("currency_id");
                    String fromCurrId = rs.getString("from_curr");
                    String toCurrId = rs.getString("to_curr");

                    scd.setItemId(medId);
                    scd.setTranDate(rs.getDate("tran_date"));
                    scd.setTranOption(rs.getString("tran_option"));
                    scd.setTranQty(ttlQty);
                    scd.setPackingCost(rs.getDouble("cost_price"));
                    scd.setSmallestCost(rs.getDouble("smallest_cost"));
                    scd.setUserId(userId);
                    scd.setCostFor(costFor);
                    scd.setUnit(rs.getString("item_unit"));
                    scd.setCurrencyId(rs.getString("currency_id"));
                    scd.setHomeCurr(curr);
                    scd.setExrDesp(fromCurrId + "-" + toCurrId);
                    scd.setExrRate(rs.getDouble("ex_rate"));

                    if (!prvMedId.equals(medId)) {
                        prvMedId = medId;
                        leftStock = rs.getInt("ttl_stock");
                    }

                    if (leftStock > 0) {
                        if (leftStock >= ttlQty) {
                            scd.setCostQty(ttlQty);
                            leftStock = leftStock - ttlQty;
                        } else {
                            scd.setCostQty(leftStock);
                            leftStock = 0;
                        }

                        if (curr.equals(currId)) {
                            scd.setExrSmallCost(rs.getDouble("smallest_cost"));
                            scd.setExrTtlCost(scd.getCostQty() * scd.getExrSmallCost());
                        } else if (curr.equals(fromCurrId) || curr.equals(toCurrId)) {
                            double exRate = rs.getDouble("ex_rate");
                            double ttlStock = scd.getCostQty();
                            double smlCost = rs.getDouble("smallest_cost");
                            double amount = ttlStock * smlCost * exRate;
                            scd.setExrSmallCost(smlCost * exRate);
                            //scd.setExrTtlCost(scd.getCostQty() * scd.getExrSmallCost());
                            scd.setExrTtlCost(amount);
                        } else {
                            scd.setExrSmallCost(0.0);
                            scd.setExrTtlCost(scd.getCostQty() * scd.getExrSmallCost());
                        }

                        dao.save(scd);
                    }
                }

                if (method.equals("FIFO")) {
                    strSql = "update tmp_stock_costing tsc, (select item_id, sum(cost_qty*exr_smallest_cost) ttl_cost, user_id\n"
                            + "	          from tmp_costing_detail\n"
                            + "		 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "	      group by item_id, user_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(strSql);
                } else if (method.equals("AVG")) {
                    strSql = "update tmp_costing_detail tcd, (\n"
                            + "					select user_id, item_id, sum(ttl_qty) ttl_qty, sum(ttl_qty*exr_smallest_cost) ttl_amt, \n"
                            + "						   (sum(ttl_qty*exr_smallest_cost)/if(sum(ttl_qty)=0,1,sum(ttl_qty))) as avg_cost\n"
                            + "				      from tmp_costing_detail\n"
                            + "					 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "					 group by user_id,item_id) avgc\n"
                            + "		set tcd.exr_smallest_cost = avgc.avg_cost\n"
                            + "		where tcd.item_id = avgc.item_id and tcd.user_id = avgc.user_id and tcd.user_id = prm_user_id";
                    strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(strSql);
                    strSql = "update tmp_stock_costing tsc, \n"
                            + "               (select user_id, item_id, sum(cost_qty*exr_smallest_cost) ttl_cost\n"
                            + "				  from tmp_costing_detail\n"
                            + "				 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "				 group by user_id,item_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(strSql);
                }
            }
        } catch (Exception ex) {
            log.error("calculateWithLocalExRate : " + ex.getMessage());
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

    public boolean isEditStatus() {
        return editStatus;
    }

    public void setEditStatus(boolean editStatus) {
        this.editStatus = editStatus;
    }
}
