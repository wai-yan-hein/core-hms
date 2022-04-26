/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import com.cv.app.util.DateUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemGroup;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.ItemGroupDetail;
import com.cv.app.pharmacy.database.entity.LocationGroup;
import com.cv.app.pharmacy.database.view.VStockCosting;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.tempentity.ItemCodeFilter;
import com.cv.app.pharmacy.database.tempentity.StockCostingDetail;
import com.cv.app.pharmacy.excel.CostingExcel;
import com.cv.app.pharmacy.excel.GenExcel;
import com.cv.app.pharmacy.ui.common.ItemCodeFilterTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.StockCostingDetailTableModel1;
import com.cv.app.pharmacy.ui.common.StockCostingTableModel;
import com.cv.app.pharmacy.database.entity.DataLock;
import com.cv.app.pharmacy.database.tempentity.TmpEXRate;
import com.cv.app.pharmacy.ui.common.CurrencyEditor;
import com.cv.app.pharmacy.ui.common.TmpEXRateTableModel;
import com.cv.app.ui.common.BestTableCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class CostingMC extends javax.swing.JPanel implements SelectionObserver, KeyPropagate {

    static Logger log = Logger.getLogger(CostingMC.class.getName());
    private final AbstractDataAccess dao = Global.dao; //Use for database access
    private final StockCostingTableModel costingTableMode = new StockCostingTableModel();
    private final StockCostingDetailTableModel1 modelSCDT = new StockCostingDetailTableModel1();
    private ItemCodeFilterTableModel codeTableModel = new ItemCodeFilterTableModel(dao, true);
    private final TableRowSorter<TableModel> sorter;
    private boolean isInit = false;
    private int mouseClick = 2;
    private final TmpEXRateTableModel tmpExrTableModel = new TmpEXRateTableModel();

    /**
     * Creates new form Costing
     */
    public CostingMC() {
        initComponents();
        initTable();
        initCombo();
        actionMapping();
        deleteTmpData();

        if (!isInit) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            if (curr != null) {
                lblCurrency.setText(curr.getCurrencyCode());
                tmpExrTableModel.setFromCurr(curr.getCurrencyCode());
            }
        }
        txtTotalCost.setHorizontalAlignment(JFormattedTextField.RIGHT);
        //txtTotalCost.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtCostDate.setText(DateUtil.getTodayDateStr());
        sorter = new TableRowSorter(tblCost.getModel());
        tblCost.setRowSorter(sorter);
        tmpExrTableModel.setParent(tblERate);
        tmpExrTableModel.addEmptyRow();

        String strSql = "delete from tmp_ex_rate where user_id = '"
                + Global.loginUser.getUserId() + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("clear temp data : " + ex.getMessage());
        } finally {
            dao.close();
        }

        String propValue = Util1.getPropValue("system.date.mouse.click");
        if (propValue != null) {
            if (!propValue.equals("-")) {
                if (!propValue.isEmpty()) {
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if (tmpValue != 0) {
                        mouseClick = tmpValue;
                    }
                }
            }
        }
    }

    private void initTable() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        tblCost.getTableHeader().setFont(Global.lableFont);
        tblCost.getColumnModel().getColumn(0).setPreferredWidth(30); //Code
        tblCost.getColumnModel().getColumn(1).setPreferredWidth(300); //Desception
        tblCost.getColumnModel().getColumn(2).setPreferredWidth(50); //Packing-Size
        tblCost.getColumnModel().getColumn(3).setPreferredWidth(50); //Balance
        tblCost.getColumnModel().getColumn(4).setPreferredWidth(50); //Smallest Qty
        tblCost.getColumnModel().getColumn(5).setPreferredWidth(50); //Total Cost

        tblCost.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        tblCostDetail.getTableHeader().setFont(Global.lableFont);
        tblCostDetail.getColumnModel().getColumn(0).setPreferredWidth(30); //Tran Date
        tblCostDetail.getColumnModel().getColumn(1).setPreferredWidth(30); //Tran Option
        tblCostDetail.getColumnModel().getColumn(2).setPreferredWidth(50); //Tran Qty
        tblCostDetail.getColumnModel().getColumn(3).setPreferredWidth(50); //Cost Price
        tblCostDetail.getColumnModel().getColumn(4).setPreferredWidth(30); //Unit
        tblCostDetail.getColumnModel().getColumn(5).setPreferredWidth(50); //Cost Qty
        tblCostDetail.getColumnModel().getColumn(6).setPreferredWidth(50); //Smallest Cost
        tblCostDetail.getColumnModel().getColumn(7).setPreferredWidth(50); //Amount

        codeTableModel.addEmptyRow();
        tblCodeFilter.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblCodeFilter.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblCodeFilter.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));

        tblERate.getColumnModel().getColumn(0).setCellEditor(
                new CurrencyEditor());
        tblERate.getColumnModel().getColumn(1).setCellEditor(
                new BestTableCellEditor(this));
        tblERate.getColumnModel().getColumn(2).setCellEditor(
                new BestTableCellEditor(this));

        tblCodeFilter.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                applyFilter();
            }
        });

        //Define table selection model to single row selection.
        tblCost.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblCost.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tblCost.getSelectedRow();

                if (selectedRow >= 0) {
                    VStockCosting sc = costingTableMode.getStockCosting(
                            tblCost.convertRowIndexToModel(selectedRow));

                    String itemId = sc.getMedId();
                    // String strLoc = "and 0 = 0";
                    /*if (cboLocation.getSelectedItem() != "All") {
                             if (cboLocation.getSelectedItem() instanceof Location) {
                             Location location = (Location) cboLocation.getSelectedItem();
                             strLoc = "and c.location = " + location.getLocationId().toString();
                             }
                             }*/

                    String strHSQL = "select c from StockCostingDetail c where c.itemId = '"
                            + itemId + "' and c.userId = '" + Global.loginUser.getUserId() + "'"
                            + " and c.costFor = 'Opening' ";

                    List<StockCostingDetail> listCostDetail = dao.findAllHSQL(strHSQL);

                    lblDescription.setText(sc.getMedName());
                    modelSCDT.setListStockCostingDetail(listCostDetail);
                } else {
                    modelSCDT.removeAll();
                    lblDescription.setText(null);
                }
            }
        });
    }

    private void calculate() {
        String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                + Global.loginUser.getUserId() + "'";
        String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                + Global.loginUser.getUserId() + "'";
        String strMethod = cboMethod.getSelectedItem().toString();

        dao.execSql(deleteTmpData1, deleteTmpData2);
        dao.commit();
        dao.close();
        insertStockFilterCode();

        dao.execProc("gen_cost_balance",
                DateUtil.toDateStrMYSQL(txtCostDate.getText()), "Opening",
                Global.loginUser.getUserId());

        String strLocation;
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strLocation = location.getLocationId().toString();
        } else {
            strLocation = "0";
        }

        Currency currency = (Currency) cboCurrency.getSelectedItem();
        String strCurr = "MMK";
        if (currency != null) {
            strCurr = currency.getCurrencyCode();
        }

        List<TmpEXRate> listEXR = dao.findAllHSQL("select o from TmpEXRate o where o.key.userId = '"
                + Global.loginUser.getUserId() + "'");
        boolean localCost = false;
        if (listEXR != null) {
            if (!listEXR.isEmpty()) {
                localCost = true;
            }
        }

        if (localCost) {
            calculateWithLocalExRate("Opening", DateUtil.toDateStrMYSQL(txtCostDate.getText()),
                    Global.loginUser.getUserId(), strMethod, strCurr);
        } else {
            try {
                dao.execProc("insert_cost_detail_mc",
                        "Opening", DateUtil.toDateStrMYSQL(txtCostDate.getText()),
                        Global.loginUser.getUserId(), strMethod, strCurr);
                dao.commit();
            } catch (Exception ex) {
                log.error("insert_cost_detail_mc : " + ex.toString());
            } finally {
                dao.close();
            }
        }

        calculateLocation();
        applyFilter();
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
                        } else {
                            if (curr.equals(fromCurrId) || curr.equals(toCurrId)) {
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

    private void calculateLocation() {
        String strLocation = "0";
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strLocation = location.getLocationId().toString();
        }

        String userId = Global.loginUser.getUserId();
        dao.execSql("delete from tmp_stock_balance_exp where user_id = '"
                + userId + "'");
        dao.execSql("update tmp_stock_costing set location_id = null, "
                + "loc_ttl_small_qty = null, loc_ttl_cost = null where user_id = '" + userId + "'");

        if (!strLocation.equals("0")) {
            dao.execProc("stock_balance_exp", "Opening",
                    DateUtil.toDateStrMYSQL(txtCostDate.getText()),
                    strLocation, userId);
            dao.execSql("update tmp_stock_costing tsc, (\n"
                    + "select med_id, sum(ifnull(bal_qty,0)) ttl_bal_qty\n"
                    + "from tmp_stock_balance_exp\n"
                    + "where user_id = '" + userId + "' and location_id = " + strLocation + " and tran_option = 'Opening'\n"
                    + "group by med_id) a\n"
                    + "set tsc.location_id = " + strLocation + ", tsc.loc_ttl_small_qty = a.ttl_bal_qty, \n"
                    + "tsc.loc_ttl_cost = if(tsc.bal_qty=0,0,(tsc.total_cost/tsc.bal_qty)*a.ttl_bal_qty)\n"
                    + "where tsc.med_id = a.med_id and tsc.user_id = '" + userId + "' and tran_option = 'Opening'");
        }
    }

    private void applyFilter() {
        String strHSQL = getHSQL();
        List<VStockCosting> listStockCosting = dao.findAllHSQL(strHSQL);
        dao.commit();
        costingTableMode.setListStockCosting(listStockCosting);

        String strLocation = "0";
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strLocation = location.getLocationId().toString();
        }

        double totalCost = 0;
        double totalLocCost = 0;

        for (VStockCosting sc : listStockCosting) {
            totalCost += NumberUtil.NZero(sc.getTtlCost());
            totalLocCost += NumberUtil.NZero(sc.getLocTtlCost());
        }

        txtTotalCost.setValue(totalCost);
        txtLocationCost.setValue(totalLocCost);
        dao.close();
    }

    private String getHSQL() {
        String strHSQL = "select c from VStockCosting c where c.userId = '"
                + Global.loginUser.getUserId() + "' and c.tranOption = 'Opening'";

        if (chkMinus.isSelected()) {
            strHSQL = strHSQL + " and c.balQty < 0";
        }
        if (!chkZero.isSelected()) {
            strHSQL = strHSQL + " and c.balQty != 0";
        }

        if (cboCustomGroup.getSelectedItem() instanceof ItemGroup) {
            ItemGroup itemGroup = (ItemGroup) cboCustomGroup.getSelectedItem();
            List<ItemGroupDetail> listItemGroupDetail = dao.findAll("ItemGroupDetail",
                    "group_id = " + itemGroup.getGroupId());
            String tmpItemList = "";

            for (ItemGroupDetail igd : listItemGroupDetail) {
                if (tmpItemList.isEmpty()) {
                    tmpItemList = "'" + igd.getItem().getMedId() + "'";
                } else {
                    tmpItemList = tmpItemList + ",'" + igd.getItem().getMedId() + "'";
                }
            }

            strHSQL = strHSQL + " and c.medId in (" + tmpItemList + ")";
        }

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();
            strHSQL = strHSQL + " and c.medTypeId = '" + itemType.getItemTypeCode() + "'";
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();
            strHSQL = strHSQL + " and c.catId = " + cat.getCatId();
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            strHSQL = strHSQL + " and c.brandId = " + itemBrand.getBrandId();
        }
        /*if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strHSQL = strHSQL + " and c.locationId = " + location.getLocationId();
        }*/

        if (codeTableModel.getListCodeFilter().size() > 1) {
            List<ItemCodeFilter> listCodeFilter = codeTableModel.getListCodeFilter();
            String codeFilter = "";

            for (ItemCodeFilter icf : listCodeFilter) {
                if (icf.getKey() != null) {
                    if (codeFilter.isEmpty()) {
                        codeFilter = "'" + icf.getKey().getItemCode().getMedId() + "'";
                    } else {
                        codeFilter = codeFilter + ",'" + icf.getKey().getItemCode().getMedId() + "'";
                    }
                }
            }

            strHSQL = strHSQL + " and c.medId in (" + codeFilter + ")";
        }

        return strHSQL;
    }

    @Override
    public void selected(Object source, Object selectObj) {
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
        }
    }

    private void initCombo() {
        isInit = true;
        BindingUtil.BindComboFilter(cboItemType, dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
        BindingUtil.BindComboFilter(cboCategory, dao.findAllHSQL("select o from Category o order by o.catName"));
        BindingUtil.BindComboFilter(cboBrand, dao.findAllHSQL("select o from ItemBrand o order by o.brandName"));
        BindingUtil.BindComboFilter(cboCustomGroup, dao.findAllHSQL("select o from ItemGroup o order by o.groupName"));
        BindingUtil.BindComboFilter(cboLocation, dao.findAllHSQL("select o from Location o order by o.locationName"));
        BindingUtil.BindComboFilter(cboLocG, dao.findAllHSQL("select o from LocationGroup o order by o.groupName"));
        BindingUtil.BindCombo(cboCurrency, dao.findAllHSQL("select o from Currency o order by o.currencyCode"));
        String currCode = Util1.getPropValue("system.app.currency");
        if (!currCode.isEmpty()) {
            Currency homeCurrency = (Currency) dao.find(Currency.class, currCode);
            cboCurrency.setSelectedItem(homeCurrency);
        }
        new ComBoBoxAutoComplete(cboItemType, this);
        new ComBoBoxAutoComplete(cboCategory, this);
        new ComBoBoxAutoComplete(cboBrand, this);
        new ComBoBoxAutoComplete(cboCustomGroup, this);
        new ComBoBoxAutoComplete(cboLocation, this);

        isInit = false;
    }

    private void actionMapping() {
        tblCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCodeFilter.getActionMap().put("F8-Action", actionItemFilterDelete);

        //F8 event on tblSale
        tblERate.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblERate.getActionMap().put("F8-Action", actionExRateItemDelete);
    }

    private final Action actionItemFilterDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblCodeFilter.getSelectedRow() >= 0) {
                codeTableModel.delete(tblCodeFilter.getSelectedRow());
            }
        }
    };

    private final Action actionExRateItemDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblERate.getSelectedRow() >= 0) {
                tmpExrTableModel.delete(tblERate.getSelectedRow());
            }
        }
    };

    private void insertStockFilterCode() {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.loginUser.getUserId() + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.loginUser.getUserId()
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(txtCostDate.getText()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true";

        if (cboLocG.getSelectedItem() instanceof LocationGroup) {
            LocationGroup lg = (LocationGroup) cboLocG.getSelectedItem();
            strSQL = strSQL + " and m.location_id in (select location_id from loc_group_mapping where group_id = "
                    + lg.getId() + ")";
        }

        if (chkFilter.isSelected()) {
            if (cboCustomGroup.getSelectedItem() instanceof ItemGroup) {
                ItemGroup itemGroup = (ItemGroup) cboCustomGroup.getSelectedItem();
                strSQL = strSQL + " and m.med_id in (select item_id from item_group_detail "
                        + "where group_id = " + itemGroup.getGroupId().toString() + ")";
            }

            if (cboItemType.getSelectedItem() instanceof ItemType) {
                ItemType itemType = (ItemType) cboItemType.getSelectedItem();
                strSQL = strSQL + " and m.med_type_id = '" + itemType.getItemTypeCode() + "'";
            }

            if (cboCategory.getSelectedItem() instanceof Category) {
                Category cat = (Category) cboCategory.getSelectedItem();
                strSQL = strSQL + " and m.category_id = " + cat.getCatId();
            }

            if (cboBrand.getSelectedItem() instanceof ItemBrand) {
                ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
                strSQL = strSQL + " and m.brand_id = " + itemBrand.getBrandId();
            }

            if (codeTableModel.getListCodeFilter().size() > 1) {
                strSQL = strSQL + " and m.med_id in (select item_code from "
                        + "tmp_item_code_filter where user_id = '"
                        + Global.loginUser.getUserId() + "')";
            }
        }

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void print() {
        String rptName = getReportName();

        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + rptName;
        Map<String, Object> params = new HashMap();
        String compName = Util1.getPropValue("report.company.name");

        params.put("compName", compName);
        params.put("data_date", txtCostDate.getText());
        params.put("user_id", Global.loginUser.getUserId());
        params.put("method", cboMethod.getSelectedItem().toString());

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();
            params.put("item_type", itemType.getItemTypeCode());
        } else {
            params.put("item_type", 0);
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();
            params.put("cate_id", cat.getCatId());
        } else {
            params.put("cate_id", 0);
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            params.put("brand_id", itemBrand.getBrandId());
        } else {
            params.put("brand_id", 0);
        }

        if (cboCustomGroup.getSelectedItem() instanceof ItemGroup) {
            ItemGroup itemGroup = (ItemGroup) cboCustomGroup.getSelectedItem();
            params.put("cg_id", itemGroup.getGroupId());
        } else {
            params.put("cg_id", 0);
        }
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            params.put("loc_id", location.getLocationId());
            params.put("loc_name", location.getLocationName());
        } else {
            params.put("loc_id", 0);
        }

        dao.close();
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
        dao.commit();
    }

    private String getReportName() {
        String selReport = cboReport.getSelectedItem().toString();
        String rptName = "rptCosting";
        if (cboLocation.getSelectedItem() instanceof Location) {
            switch (selReport) {
                case "Costing":
                    rptName = "rptCostingL";
                    break;
                case "Costing Item Type Summary":
                    rptName = "rptCostingItemTypeSummaryL";
                    break;
                case "Costing System Summary":
                    rptName = "rptCostingSystemSummaryL";
                    break;
                case "Costing With Last Pur Price":
                    rptName = "rptCostingWithLastPurPriceL";
                    break;
            }
        } else {
            switch (selReport) {
                case "Costing Item Type Summary":
                    rptName = "rptCostingItemTypeSummary";
                    break;
                case "Costing System Summary":
                    rptName = "rptCostingSystemSummary";
                    break;
                case "Costing With Last Pur Price":
                    rptName = "rptCostingWithLastPurPrice";
                    break;
            }
        }
        return rptName;
    }

    private void deleteTmpData() {
        String strSql1 = "delete from tmp_item_code_filter where user_id ='"
                + Global.loginUser.getUserId() + "'";

        dao.execSql(strSql1);
    }

    private String getItemType() {
        String itemType;

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType it = (ItemType) cboItemType.getSelectedItem();
            itemType = it.getItemTypeCode();
        } else {
            itemType = "All";
        }

        return itemType;
    }

    private Integer getBrand() {
        int brandId = 0;
        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            brandId = itemBrand.getBrandId();
        }

        return brandId;
    }

    private Integer getCategory() {
        int cat;

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category c = (Category) cboCategory.getSelectedItem();
            cat = c.getCatId();
        } else {
            cat = 0;
        }

        return cat;
    }

    private Integer getItemCusGroup() {
        int grpId = 0;
        if (cboCustomGroup.getSelectedItem() instanceof ItemGroup) {
            ItemGroup ig = (ItemGroup) cboCustomGroup.getSelectedItem();
            grpId = ig.getGroupId();
        }

        return grpId;
    }

    private void sendToAccount() {
        String deptCode = Util1.getPropValue("system.location.all.dept");
        if (cboLocG.getSelectedItem() instanceof LocationGroup) {
            LocationGroup lg = (LocationGroup) cboLocG.getSelectedItem();
            deptCode = lg.getDeptCode();
        }

        if (!deptCode.isEmpty() && !deptCode.equals("-")) {
            String isIntegration = Util1.getPropValue("system.integration");
            if (isIntegration.toUpperCase().equals("Y")) {
                if (!Global.mqConnection.isStatus()) {
                    String mqUrl = Util1.getPropValue("system.mqserver.url");
                    Global.mqConnection = new ActiveMQConnection(mqUrl);
                }
                if (Global.mqConnection != null) {
                    if (Global.mqConnection.isStatus()) {
                        try {
                            Double ttlValue = NumberUtil.getDouble(txtTotalCost.getText());
                            String strTranDate = DateUtil.toDateStrMYSQL(txtCostDate.getText());
                            ActiveMQConnection mq = Global.mqConnection;
                            String appCurr = Util1.getPropValue("system.app.currency");
                            MapMessage msg = mq.getMapMessageTemplate();
                            msg.setString("program", Global.programId);
                            msg.setString("entity", "OPENING-STOCK");
                            msg.setDouble("VALUE", ttlValue);
                            msg.setString("DEPTCODE", deptCode);
                            msg.setString("TRAN-DATE", strTranDate);
                            msg.setString("CURRENCY", appCurr);
                            msg.setString("queueName", "INVENTORY");
                            mq.sendMessage("ACCOUNT", msg);
                            log.info("sendToAccount : " + deptCode);
                        } catch (Exception ex) {
                            log.error("sendToAccount : " + ex.toString());
                        }
                    } else {
                        log.error("uploadToAccount : not connected");
                    }
                } else {
                    log.error("uploadToAccount : not connected");
                }
            }
        }
    }

    private void dataLock() {
        try {
            List<DataLock> listDL = dao.findAllHSQL(
                    "select o from DataLock o where date(o.lockDate) >= '"
                    + DateUtil.toDateStrMYSQL(txtCostDate.getText()) + "'");
            if (listDL != null) {
                if (!listDL.isEmpty()) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Already Locked.",
                            "Data Lock", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            DataLock dl = new DataLock();
            dl.setLockDate(DateUtil.toDate(txtCostDate.getText()));
            dl.setLockMachineId(Integer.parseInt(Global.machineId));
            dl.setLockUserId(Global.loginUser.getUserId());
            dl.setTranDate(new Date());
            dao.save(dl);
        } catch (Exception ex) {
            log.error("dataLock : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCostDate = new javax.swing.JFormattedTextField();
        butCalculate = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        chkFilter = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        cboMethod = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cboLocG = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboItemType = new javax.swing.JComboBox();
        cboCategory = new javax.swing.JComboBox();
        cboBrand = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCodeFilter = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        cboCustomGroup = new javax.swing.JComboBox();
        chkMinus = new javax.swing.JCheckBox();
        butPrint = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        butExcel = new javax.swing.JButton();
        chkZero = new javax.swing.JCheckBox();
        butSendToAcc = new javax.swing.JButton();
        cboReport = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        butDataLock = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblERate = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCost = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtTotalCost = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCostDetail = new javax.swing.JTable();
        lblDescription = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtLocationCost = new javax.swing.JFormattedTextField();
        lblLocationTotalCost = new javax.swing.JLabel();
        lblCurrency = new javax.swing.JLabel();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date :");

        txtCostDate.setEditable(false);
        txtCostDate.setFont(Global.textFont);
        txtCostDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCostDateMouseClicked(evt);
            }
        });

        butCalculate.setFont(Global.textFont);
        butCalculate.setText("Calculate");
        butCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCalculateActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Filter :");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Method :");

        cboMethod.setFont(Global.textFont);
        cboMethod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AVG", "FIFO" }));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Summary");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("LOC-G :");

        cboLocG.setFont(Global.textFont);
        cboLocG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocGActionPerformed(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Currency :");

        cboCurrency.setFont(Global.textFont);
        cboCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCurrencyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(18, 18, 18)
                            .addComponent(chkFilter)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(butCalculate))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8)
                                .addComponent(jLabel12)
                                .addComponent(jLabel14)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cboLocG, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboMethod, 0, 125, Short.MAX_VALUE)
                                    .addComponent(txtCostDate))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel12, jLabel14, jLabel2, jLabel8});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCostDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboLocG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(butCalculate)
                    .addComponent(chkFilter)
                    .addComponent(jLabel2))
                .addGap(23, 23, 23)
                .addComponent(jLabel10))
        );

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Item Type : ");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category :");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Brand :");

        cboItemType.setFont(Global.textFont);
        cboItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboItemTypeActionPerformed(evt);
            }
        });

        cboCategory.setFont(Global.textFont);
        cboCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoryActionPerformed(evt);
            }
        });

        cboBrand.setFont(Global.textFont);
        cboBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBrandActionPerformed(evt);
            }
        });

        tblCodeFilter.setFont(Global.textFont);
        tblCodeFilter.setModel(codeTableModel);
        tblCodeFilter.setRowHeight(23);
        jScrollPane3.setViewportView(tblCodeFilter);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Custom Group :");

        cboCustomGroup.setFont(Global.textFont);
        cboCustomGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCustomGroupActionPerformed(evt);
            }
        });

        chkMinus.setText("Show Minus Only");
        chkMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMinusActionPerformed(evt);
            }
        });

        butPrint.setFont(Global.textFont);
        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Location :");

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        butExcel.setText("Export to Excel");
        butExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExcelActionPerformed(evt);
            }
        });

        chkZero.setText("Show Zero");
        chkZero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkZeroActionPerformed(evt);
            }
        });

        butSendToAcc.setText("Send To Acc");
        butSendToAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSendToAccActionPerformed(evt);
            }
        });

        cboReport.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Costing", "Costing Item Type Summary", "Costing System Summary", "Costing With Last Pur Price" }));

        jLabel13.setText("Report : ");

        butDataLock.setText("Data Lock");
        butDataLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDataLockActionPerformed(evt);
            }
        });

        tblERate.setModel(tmpExrTableModel);
        tblERate.setRowHeight(23);
        jScrollPane4.setViewportView(tblERate);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboLocation, 0, 147, Short.MAX_VALUE)
                            .addComponent(cboCustomGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(chkMinus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkZero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(butSendToAcc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboReport, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(butDataLock)))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel4, jLabel5, jLabel6, jLabel9});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butExcel, butPrint, butSendToAcc});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cboCustomGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkMinus)
                            .addComponent(cboReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkZero)
                            .addComponent(butDataLock))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(butPrint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butExcel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butSendToAcc))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tblCost.setFont(Global.textFont);
        tblCost.setModel(costingTableMode);
        tblCost.setRowHeight(23);
        jScrollPane1.setViewportView(tblCost);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("All Location Total Cost :");

        txtTotalCost.setEditable(false);
        txtTotalCost.setFont(Global.textFont);

        tblCostDetail.setFont(Global.textFont);
        tblCostDetail.setModel(modelSCDT);
        tblCostDetail.setRowHeight(23);
        jScrollPane2.setViewportView(tblCostDetail);

        lblDescription.setFont(new java.awt.Font("Zawgyi-One", 1, 12)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Transaction Detail :");

        txtLocationCost.setEditable(false);
        txtLocationCost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtLocationCost.setFont(Global.textFont);

        lblLocationTotalCost.setFont(Global.lableFont);
        lblLocationTotalCost.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocationTotalCost.setText("Location Total  Cost : ");

        lblCurrency.setFont(Global.lableFont);
        lblCurrency.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalCost, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblLocationTotalCost, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLocationCost, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotalCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtLocationCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLocationTotalCost))
                    .addComponent(lblDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

  private void txtCostDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCostDateMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtCostDate.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtCostDateMouseClicked

    private void butCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCalculateActionPerformed
        calculate();
    }//GEN-LAST:event_butCalculateActionPerformed

  private void cboItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemTypeActionPerformed
      if (!isInit) {
          applyFilter();
      }
  }//GEN-LAST:event_cboItemTypeActionPerformed

  private void cboCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoryActionPerformed
      if (!isInit) {
          applyFilter();
      }
  }//GEN-LAST:event_cboCategoryActionPerformed

  private void cboBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBrandActionPerformed
      if (!isInit) {
          applyFilter();
      }
  }//GEN-LAST:event_cboBrandActionPerformed

  private void cboCustomGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCustomGroupActionPerformed
      if (!isInit) {
          applyFilter();
      }
  }//GEN-LAST:event_cboCustomGroupActionPerformed

  private void chkMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMinusActionPerformed
      applyFilter();
  }//GEN-LAST:event_chkMinusActionPerformed

  private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
      print();
  }//GEN-LAST:event_butPrintActionPerformed

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (!isInit) {
            calculateLocation();
            applyFilter();
            String despQty = "Location Qty";
            String despCost = "Location Total Cost";
            if (cboLocation.getSelectedItem() instanceof Location) {
                String locName = ((Location) cboLocation.getSelectedItem()).getLocationName();
                despQty = locName + " Qty";
                despCost = locName + " Total Cost";
            }

            JTableHeader th = tblCost.getTableHeader();
            TableColumnModel tcm = th.getColumnModel();
            TableColumn tc = tcm.getColumn(6);
            tc.setHeaderValue(despQty);
            tc = tcm.getColumn(7);
            tc.setHeaderValue(despCost);
            th.repaint();

            lblLocationTotalCost.setText(despCost);
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void butExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExcelActionPerformed
        try {
            GenExcel excel = new CostingExcel(dao, "costing.xls");
            excel.setUserId(Global.loginUser.getUserId());
            excel.setItemType(getItemType());
            excel.setBrandId(getBrand().toString());
            excel.setCategoryId(getCategory().toString());
            excel.setStrOpDate(DateUtil.toDateStrMYSQL(txtCostDate.getText()));
            if (codeTableModel.getListCodeFilter().size() > 1) {
                excel.setItemCodeFilter("f");
            }
            excel.setItemGroup(getItemCusGroup().toString());
            excel.insertStockFilterCode();
            excel.genExcel();
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }//GEN-LAST:event_butExcelActionPerformed

    private void chkZeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkZeroActionPerformed
        // TODO add your handling code here:
        applyFilter();
    }//GEN-LAST:event_chkZeroActionPerformed

    private void butSendToAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSendToAccActionPerformed
        sendToAccount();
    }//GEN-LAST:event_butSendToAccActionPerformed

    private void cboLocGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocGActionPerformed
        if (cboLocG.getSelectedItem() instanceof LocationGroup) {
            LocationGroup lg = (LocationGroup) cboLocG.getSelectedItem();
            String strSQL = "select o from Location o where o.locationId in "
                    + "(select lg.key.location.locationId from LocationGroupMapping lg where lg.key.groupId = "
                    + lg.getId() + ") order by o.locationName";

            BindingUtil.BindComboFilter(cboLocation, dao.findAllHSQL(strSQL));
        } else {
            BindingUtil.BindComboFilter(cboLocation,
                    dao.findAllHSQL("select o from Location o order by o.locationName"));
        }
    }//GEN-LAST:event_cboLocGActionPerformed

    private void butDataLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDataLockActionPerformed
        dataLock();
    }//GEN-LAST:event_butDataLockActionPerformed

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        if (!isInit) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            lblCurrency.setText(curr.getCurrencyCode());
            tmpExrTableModel.setFromCurr(curr.getCurrencyCode());
        }
    }//GEN-LAST:event_cboCurrencyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCalculate;
    private javax.swing.JButton butDataLock;
    private javax.swing.JButton butExcel;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butSendToAcc;
    private javax.swing.JComboBox cboBrand;
    private javax.swing.JComboBox cboCategory;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JComboBox cboCustomGroup;
    private javax.swing.JComboBox cboItemType;
    private javax.swing.JComboBox<String> cboLocG;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboMethod;
    private javax.swing.JComboBox<String> cboReport;
    private javax.swing.JCheckBox chkFilter;
    private javax.swing.JCheckBox chkMinus;
    private javax.swing.JCheckBox chkZero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCurrency;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblLocationTotalCost;
    private javax.swing.JTable tblCodeFilter;
    private javax.swing.JTable tblCost;
    private javax.swing.JTable tblCostDetail;
    private javax.swing.JTable tblERate;
    private javax.swing.JFormattedTextField txtCostDate;
    private javax.swing.JFormattedTextField txtLocationCost;
    private javax.swing.JFormattedTextField txtTotalCost;
    // End of variables declaration//GEN-END:variables
}
