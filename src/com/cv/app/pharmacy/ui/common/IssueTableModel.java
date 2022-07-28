/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.StockIssueDetailHis;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.CurrencyTtl;
import com.cv.app.pharmacy.database.helper.StockOutstanding;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.pharmacy.database.tempentity.StockCostingDetail;
import com.cv.app.pharmacy.database.tempentity.TmpEXRate;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.Util1;
import com.cv.app.util.NumberUtil;
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
 * @author wswe
 */
public class IssueTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(IssueTableModel.class.getName());
    private List<StockIssueDetailHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Option", "Vou No", "Item Code", "Description",
        "T-Code", "Trader Name", "Outstanding", "Exp-Date", "Currency", "Qty",
        "Unit", "Balance", "Unit Cost", "Amount"};
    private final AbstractDataAccess dao;
    private MedicineUP medUp;
    private final MedInfo medInfo;
    private final SelectionObserver observer;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private String deletedList;
    private int maxUniqueId = 0;
    private String issueId = "-";
    private JTable parent;
    private Currency currency;

    public IssueTableModel(AbstractDataAccess dao, MedInfo medInfo,
            SelectionObserver observer) {
        this.dao = dao;
        this.medInfo = medInfo;
        this.observer = observer;
        medUp = new MedicineUP(dao);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0: //Option
                return false;
            case 1: //Vou No
                return false;
            case 2: //Med Code
                return true;
            case 3: //Medicine
                return false;
            case 4: //T-Code
                Object tmpObj = getValueAt(row, 0);
                if (tmpObj == null) {
                    return false;
                } else {
                    return isEditable(tmpObj.toString());
                }
            case 5: //Trader Name
                return false;
            case 6: //Outstanding
                return false;
            case 7: //Exp-Date
                return true;
            case 8: //Currency
                return true;
            case 9: //Qty
                return true;
            case 10: //Unit
                return false;
            case 11: //Balance
                return false;
            case 12: //Unit Cost
                return true;
            case 13: //Amount
                return false;
            default:
                return false;
        }
    }

    private boolean isEditable(String recOption) {
        return recOption.equals("Borrow");
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Option
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Med Code
                return String.class;
            case 3: //Medicine
                return String.class;
            case 4: //T-Code
                return String.class;
            case 5: //Trader Name
                return String.class;
            case 6: //Outstanding
                return String.class;
            case 7: //Exp-Date
                return String.class;
            case 8: //Currency
                return String.class;
            case 9: //Qty
                return Float.class;
            case 10: //Unit
                return String.class;
            case 11: //Balance
                return String.class;
            case 12: //Unit Cost
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
            StockIssueDetailHis sidh = listDetail.get(row);

            switch (column) {
                case 0: //Option
                    return sidh.getIssueOpt();
                case 1: //Vou No
                    return sidh.getRefVou();
                case 2: //Med Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (sidh.getIssueMed() != null) {
                            return sidh.getIssueMed().getShortName();
                        } else {
                            return null;
                        }
                    } else if (sidh.getIssueMed() != null) {
                        return sidh.getIssueMed().getMedId();
                    } else {
                        return null;
                    }
                case 3: //Medicine
                    if (sidh.getIssueMed() != null) {
                        return sidh.getIssueMed().getMedName();
                    } else {
                        return null;
                    }
                case 4: //T-Code
                    if (sidh.getTrader() != null) {
                        return sidh.getTrader().getTraderId();
                    } else {
                        return null;
                    }
                case 5: //Trader Name
                    if (sidh.getTrader() != null) {
                        return sidh.getTrader().getTraderName();
                    } else {
                        return null;
                    }
                case 6: //Outstanding
                    return sidh.getStrOutstanding();
                case 7: //Exp-Date
                    return DateUtil.toDateStr(sidh.getExpDate());
                case 8: //Currency
                    if (sidh.getCurrency() == null) {
                        return null;
                    } else {
                        return sidh.getCurrency().getCurrencyCode();
                    }
                case 9: //Qty
                    return sidh.getUnitQty();
                case 10: //Unit
                    return sidh.getUnit();
                case 11: //Balance
                    return sidh.getBalance();
                case 12: //Unit Cost
                    return sidh.getUnitCost();
                case 13: //Amount
                    return sidh.getAmount();
                default:
                    return null;
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

        StockIssueDetailHis sidh = listDetail.get(row);
        try {
            switch (column) {
                case 0: //Option
                    //srdh.setRecOption((String)value);
                    break;
                case 1: //Vou No
                    //srdh.setRefVou((String)value);
                    break;
                case 2: //Med Code
                    String medCode = (String) value;

                    if (!medCode.isEmpty()) {
                        medInfo.getMedInfo(medCode);
                        if (sidh.getIssueOpt() == null) {
                            sidh.setIssueOpt("COMSUME");
                            sidh.setIssueId(issueId);
                            sidh.setRefVou(issueId);
                            sidh.setCurrency(currency);
                            parent.setRowSelectionInterval(row, row);
                            parent.setColumnSelectionInterval(8, 8);
                        }

                        getOutstandingBalance(sidh);
                    }
                    break;
                case 3: //Medicine
                    break;
                case 4: //T-Code                    
                    if (value != null) {
                        sidh.setTrader((Trader) value);
                        getOutstandingBalance(sidh);
                    }
                    break;
                case 5: //Trader Name
                    break;
                case 6: //Outstanding
                    break;
                case 7: //Exp-Date
                    if (DateUtil.isValidDate(value)) {
                        sidh.setExpDate(DateUtil.toDate(value));
                    } else {
                        sidh.setExpDate(null);
                    }

                    break;
                case 8: //Currency
                    if (value == null) {
                        sidh.setCurrency(null);
                    } else {
                        sidh.setCurrency((Currency) value);
                    }

                    try {
                        String medId = sidh.getIssueMed().getMedId();
                        String key = medId + "-" + sidh.getUnit().getItemUnitCode();
                        Currency tmpCurr = sidh.getCurrency();
                        if (tmpCurr == null) {
                            tmpCurr = currency;
                        }
                        String curr = "MMK";
                        if (tmpCurr != null) {
                            curr = tmpCurr.getCurrencyCode();
                        }
                        calculateMed(medId, curr);
                        double smallestCost = getSmallestCost(medId);
                        float smallestQty = medUp.getQtyInSmallest(key);
                        double unitCost = smallestCost * smallestQty;
                        sidh.setUnitCost(unitCost);
                    } catch (Exception ex) {
                        log.error("qty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    } finally {
                        dao.close();
                    }
                    break;
                case 9: //Qty
                    sidh.setUnitQty(NumberUtil.FloatZero(value));

                    String medId = sidh.getIssueMed().getMedId();
                    if (medUp.getUnitList(medId).size() > 1) {
                        int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
                        int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
                        UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                medUp.getUnitList(medId), Util1.getParent());

                        if (unitPopup.isSelected()) {
                            sidh.setUnit(unitPopup.getSelUnit());
                            String key = sidh.getIssueMed().getMedId() + "-"
                                    + sidh.getUnit().getItemUnitCode();
                            sidh.setSmallestQty(sidh.getUnitQty() * medUp.getQtyInSmallest(key));
                            float tmpBalance = NumberUtil.FloatZero(sidh.getOutsBalance())
                                    - (sidh.getUnitQty() * medUp.getQtyInSmallest(key));
                            Medicine item = sidh.getIssueMed();
                            item.setRelationGroupId(medUp.getRelation(medId));
                            sidh.setBalance(MedicineUtil.getQtyInStr(item, tmpBalance));
                            //parent.setColumnSelectionInterval(6, 6);
                        }
                    } else {
                        sidh.setUnit(medUp.getUnitList(medId).get(0));
                        String key = sidh.getIssueMed().getMedId() + "-"
                                + sidh.getUnit().getItemUnitCode();
                        sidh.setSmallestQty(sidh.getUnitQty() * medUp.getQtyInSmallest(key));
                    }

                    try {
                        String key = medId + "-" + sidh.getUnit().getItemUnitCode();
                        Currency tmpCurr = sidh.getCurrency();
                        if (tmpCurr == null) {
                            tmpCurr = currency;
                        }
                        String curr = "MMK";
                        if (tmpCurr != null) {
                            curr = tmpCurr.getCurrencyCode();
                        }
                        calculateMed(medId, curr);
                        double smallestCost = getSmallestCost(medId);
                        float smallestQty = medUp.getQtyInSmallest(key);
                        double unitCost = smallestCost * smallestQty;
                        sidh.setUnitCost(unitCost);
                    } catch (Exception ex) {
                        log.error("qty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    } finally {
                        dao.close();
                    }

                    //parent.setRowSelectionInterval(row, row);
                    //parent.setColumnSelectionInterval(2, 2);
                    break;
                case 10: //Unit
                    break;
                case 11: //Balance
                    break;
                case 12: //Unit Cost
                    if (value == null) {
                        sidh.setUnitCost(0.0);
                    } else {
                        sidh.setUnitCost((Double) value);
                    }
                    break;
                case 13: //Amount
                    break;
                default:
                    System.out.println("IssueTableModel invalid index.");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        calculateAmount(sidh, row);
        addEmptyRow();

        if (column != 0) {
            try {
                fireTableCellUpdated(row, column);
                fireTableCellUpdated(row, 6);
                fireTableCellUpdated(row, 8);
                fireTableCellUpdated(row, 12);
                fireTableCellUpdated(row, 13);
            } catch (Exception ex) {

            }
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

    public List<StockIssueDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.StockIssueDetailHis"
                + " WHERE issueMed.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void setListDetail(List<StockIssueDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (this.listDetail != null) {
            if (!listDetail.isEmpty()) {
                StockIssueDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
            for (StockIssueDetailHis sidh : listDetail) {
                medUp.add(sidh.getIssueMed());
            }
            fireTableDataChanged();
        }
    }

    public void add(StockOutstanding outs) {
        if (listDetail != null) {
            StockIssueDetailHis sidh = listDetail.get(listDetail.size() - 1);

            sidh.setBalance(outs.getQtyStr());
            sidh.setOutsBalance(outs.getBalanceQty());
            sidh.setIssueMed(outs.getMed());
            sidh.setIssueOpt(outs.getTranOption());
            sidh.setRefVou(outs.getInvId());
            try {
                if (outs.getCusId() != null) {
                    sidh.setTrader((Trader) dao.find(Trader.class,
                            outs.getCusId()));
                }
            } catch (Exception ex) {
                log.error("add : " + ex.getMessage());
            } finally {
                dao.close();
            }
            sidh.setStrOutstanding(outs.getQtyStr());

            //listDetail.add(sidh);
            if (outs.getMed() != null) {
                try {
                    dao.open();
                    medUp.add(outs.getMed());
                    dao.close();
                } catch (Exception ex) {
                    log.error("add : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }

            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            addEmptyRow();
        }
    }

    public void clear() {
        maxUniqueId = 0;
        issueId = "-";
        if (listDetail != null) {
            listDetail = new ArrayList();
            medUp = new MedicineUP(dao);
            fireTableDataChanged();
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        if (listDetail == null) {
            return false;
        }

        int row = maxUniqueId;

        for (StockIssueDetailHis his : listDetail) {
            if (his.getIssueOpt() != null) {
                if (his.getUnitQty() <= 0) {
                    status = false;
                    JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                            "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                } else if (his.getIssueMed() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine.",
                            "No Medicine", JOptionPane.ERROR_MESSAGE);
                } /*else if (his.getTrader() == null) {
             status = false;
             JOptionPane.showMessageDialog(Util1.getParent(), "Invalid trader.",
             "No Trader", JOptionPane.ERROR_MESSAGE);
             }*/ else if (NumberUtil.NZeroInt(his.getUniqueId()) == 0) {
                    his.setUniqueId(row + 1);
                    row++;
                }
            }
        }

        return status;
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                StockIssueDetailHis sidh = listDetail.get(row);
                if (sidh.getTranId() != null) {
                    if (deletedList == null) {
                        deletedList = "'" + sidh.getTranId() + "'";
                    } else {
                        deletedList = deletedList + ",'" + sidh.getTranId() + "'";
                    }
                }
                listDetail.remove(row);
                fireTableRowsDeleted(row, row);
                addEmptyRow();
            }
        }
    }

    public boolean hasEmptyRow() {
        if (listDetail == null) {
            return false;
        }
        if (listDetail.isEmpty()) {
            return false;
        }

        StockIssueDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getIssueOpt() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                StockIssueDetailHis record = new StockIssueDetailHis();
                record.setIssueMed(new Medicine());
                listDetail.add(record);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    public void setMed(Medicine med, int row) {
        if (listDetail == null) {
            return;
        }

        try {
            StockIssueDetailHis record = listDetail.get(row);
            dao.open();
            Medicine tmpMed = (Medicine) dao.find(Medicine.class,
                    med.getMedId());
            medUp.add(tmpMed);
            record.setIssueMed(tmpMed);
            dao.close();
        } catch (Exception ex) {
            log.error("setMed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        fireTableRowsUpdated(row, row);
    }

    public void setTrader(Trader trader, int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                StockIssueDetailHis record = listDetail.get(row);
                record.setTrader(trader);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from stock_issue_detail_his where issue_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    private void calculateAmount(StockIssueDetailHis sidh, int row) {
        if (sidh != null) {
            float tmpQty = NumberUtil.FloatZero(sidh.getUnitQty());
            double tmpCost = NumberUtil.NZero(sidh.getUnitCost());
            sidh.setAmount(tmpQty * tmpCost);
            fireTableCellUpdated(row, 11);
            fireTableCellUpdated(row, 12);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public StockIssueDetailHis getSelectedData(int row) {
        if (listDetail == null) {
            return null;
        } else if (listDetail.isEmpty()) {
            return null;
        }

        return listDetail.get(row);
    }

    public double getTotal() {
        if (listDetail == null) {
            return 0.0;
        }
        double total = 0;
        for (StockIssueDetailHis sidh : listDetail) {
            total += NumberUtil.NZero(sidh.getAmount());
        }
        return total;
    }

    public List<CurrencyTtl> getCurrTotal() {
        List<CurrencyTtl> listCTTL = new ArrayList();
        HashMap<String, CurrencyTtl> hmCTTL = new HashMap();

        listDetail.forEach(sidh -> {
            Currency currId = sidh.getCurrency();
            if (currId != null) {
                if (hmCTTL.containsKey(currId.getCurrencyCode())) {
                    CurrencyTtl cttl = hmCTTL.get(currId.getCurrencyCode());
                    cttl.setTtlPaid(NumberUtil.NZero(cttl.getTtlPaid())
                            + NumberUtil.NZero(sidh.getAmount()));
                } else {
                    CurrencyTtl cttl = new CurrencyTtl(currId.getCurrencyCode(),
                            NumberUtil.NZero(sidh.getAmount()));
                    listCTTL.add(cttl);
                    hmCTTL.put(currId.getCurrencyCode(), cttl);
                }
            }
        });

        return listCTTL;
    }

    private void calculateMed(String medId, String currency) {
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

            List<TmpEXRate> listEXR = dao.findAllHSQL("select o from TmpEXRate o where o.key.userId = '"
                    + Global.machineId + "'");
            boolean localCost = false;
            if (listEXR != null) {
                if (!listEXR.isEmpty()) {
                    localCost = true;
                }
            }

            if (Util1.getPropValue("system.multicurrency").equals("Y")) {
                if (localCost) {
                    calculateWithLocalExRate("Opening", DateUtil.toDateStrMYSQL(tmpDate),
                            Global.machineId, strMethod, currency);
                } else {
                    dao.execProc("insert_cost_detail_mc",
                            "Opening", DateUtil.toDateStrMYSQL(tmpDate),
                            Global.machineId, strMethod, currency);
                }
            } else {
                dao.execProc("insert_cost_detail",
                        "Opening", DateUtil.toDateStrMYSQL(tmpDate),
                        Global.machineId, strMethod);
                dao.commit();
            }

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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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

    private void getOutstandingBalance(StockIssueDetailHis sidh) {
        String medId = "";
        String cusId = "";
        String option = "";

        if (sidh.getIssueMed() != null) {
            medId = sidh.getIssueMed().getMedId();
        }

        if (sidh.getTrader() != null) {
            cusId = sidh.getTrader().getTraderId();
        }

        if (sidh.getIssueOpt() != null) {
            option = sidh.getIssueOpt();
        }

        if (!medId.isEmpty() && !cusId.isEmpty() && option.equals("Borrow")) {
            String strSql = "select sum(a.ttl) as balance\n"
                    + "from (\n"
                    + "select sum(ifnull(smallest_qty,0))*-1 as ttl\n"
                    + "from v_stock_issue\n"
                    + "where issue_opt = 'Borrow' and deleted = false\n"
                    + "and med_id = '" + medId + "' and cus_id = '" + cusId + "'\n"
                    + "union all\n"
                    + "select sum(ifnull(smallest_qty,0)) as ttl\n"
                    + "from v_stock_receive\n"
                    + "where rec_option = 'Borrow' and deleted = false\n"
                    + "and rec_med_id = '" + medId + "' and cus_id = '" + cusId + "') a";
            try {
                ResultSet rs = dao.execSQL(strSql);
                if (rs != null) {
                    if (rs.next()) {
                        float balance = rs.getFloat("balance");
                        String strBalance = MedicineUtil.getQtyInStr(sidh.getIssueMed(), balance);
                        sidh.setStrOutstanding(strBalance);
                        sidh.setOutsBalance(balance);
                    }
                }
            } catch (Exception ex) {
                log.error("getOutstandingBalance : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }
}
