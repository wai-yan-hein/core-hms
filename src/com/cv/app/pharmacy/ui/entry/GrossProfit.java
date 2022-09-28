/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemGroup;
import com.cv.app.pharmacy.database.entity.ItemGroupDetail;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.tempentity.ItemCodeFilter;
import com.cv.app.pharmacy.database.tempentity.StockCostingDetail;
import com.cv.app.pharmacy.database.view.VGrossProfit;
import com.cv.app.pharmacy.excel.GPExcel;
import com.cv.app.pharmacy.excel.GenExcel;
import com.cv.app.pharmacy.ui.common.GPCostTableModel;
import com.cv.app.pharmacy.ui.common.GPDetailTableModel;
import com.cv.app.pharmacy.ui.common.ItemCodeFilterTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class GrossProfit extends javax.swing.JPanel implements SelectionObserver, KeyPropagate {

    static Logger log = Logger.getLogger(Costing.class.getName());
    private final AbstractDataAccess dao = Global.dao; //Use for database access
    private boolean isInit = true;
    private ItemCodeFilterTableModel codeTableModel = new ItemCodeFilterTableModel(dao, true);
    private final GPCostTableModel gpTableModel = new GPCostTableModel();
    private final GPDetailTableModel scdTableModel = new GPDetailTableModel();
    private int mouseClick = 2;

    /**
     * Creates new form GrossProfit
     */
    public GrossProfit() {
        initComponents();
        initCombo();
        actionMapping();
        initTable();
        deleteTmpData();

        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());

        txtOpValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtOpValue.setFormatterFactory(NumberUtil.getDecimalFormat());

        txtTtlPur.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTtlPur.setFormatterFactory(NumberUtil.getDecimalFormat());

        txtClValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtClValue.setFormatterFactory(NumberUtil.getDecimalFormat());

        txtCogs.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtCogs.setFormatterFactory(NumberUtil.getDecimalFormat());

        txtTtlSale.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTtlSale.setFormatterFactory(NumberUtil.getDecimalFormat());

        txtGP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtGP.setFormatterFactory(NumberUtil.getDecimalFormat());

        butView.setVisible(false);

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

    private void initCombo() {
        try {
            isInit = true;
            BindingUtil.BindComboFilter(cboItemType, dao.findAll("ItemType"));
            BindingUtil.BindComboFilter(cboCategory, dao.findAll("Category"));
            BindingUtil.BindComboFilter(cboBrand, dao.findAll("ItemBrand"));
            BindingUtil.BindComboFilter(cboCustomG, dao.findAll("ItemGroup"));

            new ComBoBoxAutoComplete(cboItemType, this);
            new ComBoBoxAutoComplete(cboCategory, this);
            new ComBoBoxAutoComplete(cboBrand, this);
            new ComBoBoxAutoComplete(cboCustomG, this);
            isInit = false;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
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

    private void actionMapping() {
        tblCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCodeFilter.getActionMap().put("F8-Action", actionItemFilterDelete);
    }

    private Action actionItemFilterDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblCodeFilter.getSelectedRow() >= 0) {
                codeTableModel.delete(tblCodeFilter.getSelectedRow());
            }
        }
    };

    private void initTable() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        tblGP.getTableHeader().setFont(Global.lableFont);
        tblGP.getColumnModel().getColumn(0).setPreferredWidth(30); //Code
        tblGP.getColumnModel().getColumn(1).setPreferredWidth(300); //Desception
        tblGP.getColumnModel().getColumn(2).setPreferredWidth(50); //Packing-Size
        tblGP.getColumnModel().getColumn(3).setPreferredWidth(50); //OP-Value
        tblGP.getColumnModel().getColumn(4).setPreferredWidth(50); //TTL-Pur
        tblGP.getColumnModel().getColumn(5).setPreferredWidth(50); //CL-Value
        tblGP.getColumnModel().getColumn(6).setPreferredWidth(50); //COGS
        tblGP.getColumnModel().getColumn(7).setPreferredWidth(50); //TTL-Sale
        tblGP.getColumnModel().getColumn(8).setPreferredWidth(50); //Gross Profit

        //tblGP.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);    
        codeTableModel.addEmptyRow();
        tblCodeFilter.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblCodeFilter.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblCodeFilter.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));

        tblCodeFilter.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                applyFilter();
            }
        });

        //Define table selection model to single row selection.
        tblGP.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblGP.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tblGP.getSelectedRow();
                try {
                    if (selectedRow >= 0) {
                        VGrossProfit vgp = gpTableModel.getGrossProfit(
                                tblGP.convertRowIndexToModel(selectedRow));

                        String itemId = vgp.getKey().getItemId();

                        String strHSQL = "select c from StockCostingDetail c where c.itemId = '"
                                + itemId + "' and c.userId = '" + Global.machineId + "'"
                                + " and c.costFor = 'Opening' ";

                        List<StockCostingDetail> listCostDetail = dao.findAllHSQL(strHSQL);

                        //lblDescription.setText(sc.getKey().getMedicine().getMedName());
                        scdTableModel.setListStockCostingDetail(listCostDetail);
                    } else {
                        scdTableModel.removeAll();
                        //lblDescription.setText(null);
                    }
                } catch (Exception ex) {
                    log.error("valueChanged : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        });

        tblGPDetail.getTableHeader().setFont(Global.lableFont);
        tblGPDetail.getColumnModel().getColumn(0).setPreferredWidth(30); //Tran Date
        tblGPDetail.getColumnModel().getColumn(1).setPreferredWidth(30); //Tran Option
        tblGPDetail.getColumnModel().getColumn(2).setPreferredWidth(50); //Tran Qty
        tblGPDetail.getColumnModel().getColumn(3).setPreferredWidth(50); //Cost Price
        tblGPDetail.getColumnModel().getColumn(4).setPreferredWidth(30); //Unit
        tblGPDetail.getColumnModel().getColumn(5).setPreferredWidth(50); //Cost Qty
        tblGPDetail.getColumnModel().getColumn(6).setPreferredWidth(50); //Smallest Cost
        tblGPDetail.getColumnModel().getColumn(7).setPreferredWidth(50); //Amount
    }

    private void calculate() {
        try {
            String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                    + Global.machineId + "'";
            String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                    + Global.machineId + "'";
            Date opDate = DateUtil.toDate(dao.getMax("op_date", "stock_op_his", null), "yyyy-MM-dd");
            if (opDate == null) {
                opDate = DateUtil.toDate("1900-12-31", "yyyy-MM-dd");
            }
            Date tranDate = DateUtil.toDate(txtFrom.getText());
            String strOpDate = null;
            String strMethod = cboMethod.getSelectedItem().toString();

            if (tranDate.before(opDate) || tranDate.equals(opDate)) {
                strOpDate = DateUtil.toDateStr(tranDate, "dd/MM/yyyy");
            } else if (tranDate.after(opDate)) {
                strOpDate = DateUtil.subDateTo(tranDate, -1);
            }

            System.out.println(strOpDate);
            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();
            //For opening value
            insertStockFilterCode(strOpDate);
            /*dao.execProc("gen_cost_balance1",
                    DateUtil.toDateStrMYSQL(strOpDate),
                    "Opening",
                    Global.loginUser.getUserId());*/
            dao.execSql("delete from tmp_stock_costing where user_id = '" + Global.machineId + "'\n"
                    + "	and tran_option = 'Opening'");
            dao.commit();
            String sql1 = "insert into tmp_stock_costing(med_id, user_id, bal_qty, qty_str, tran_option)\n"
                    + "    select C.med_id, prm_user_id, ifnull(D.ttl_qty,0), D.ttl_qty_str, prm_tran_option\n"
                    + "    from (select distinct med_id from tmp_stock_filter where user_id = prm_user_id) C left join\n"
                    + "    (select A.med_id, sum(A.ttl_qty) ttl_qty,\n"
                    + "           get_qty_in_str(ifnull(sum(A.ttl_qty),0), B.unit_smallest, B.unit_str) ttl_qty_str\n"
                    + "      from (\n"
                    + "            select vso.med_id, sum(ifnull(vso.op_smallest_qty,0)) ttl_qty\n"
                    + "              from v_stock_op vso, tmp_stock_filter tsf\n"
                    + "             where vso.location = tsf.location_id and vso.med_id = tsf.med_id\n"
                    + "               and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "             group by vso.med_id\n"
                    + "             union all\n"
                    + "            select vs.med_id, sum((ifnull(vs.sale_smallest_qty, 0)+ifnull(vs.foc_smallest_qty,0))*-1) ttl_qty\n"
                    + "              from v_sale vs, tmp_stock_filter tsf\n"
                    + "             where vs.location_id = tsf.location_id and vs.med_id = tsf.med_id\n"
                    + "               and date(vs.sale_date) >= tsf.op_date and date(vs.sale_date) <= prm_stock_date\n"
                    + "               and vs.deleted = false and vs.vou_status = 1 and tsf.user_id = prm_user_id\n"
                    + "             group by vs.med_id\n"
                    + "             union all\n"
                    + "            select vp.med_id, sum(ifnull(vp.pur_smallest_qty,0)+ifnull(vp.pur_foc_smallest_qty,0)) ttl_qty\n"
                    + "              from v_purchase vp, tmp_stock_filter tsf\n"
                    + "             where vp.location = tsf.location_id and vp.med_id = tsf.med_id\n"
                    + "               and date(vp.pur_date) >= tsf.op_date and date(vp.pur_date) <= prm_stock_date\n"
                    + "               and vp.deleted = false and vp.vou_status = 1 and tsf.user_id = prm_user_id\n"
                    + "             group by vp.med_id\n"
                    + "             union all\n"
                    + "            select vri.med_id, sum(ifnull(vri.ret_in_smallest_qty,0)) ttl_qty\n"
                    + "              from v_return_in vri, tmp_stock_filter tsf\n"
                    + "             where vri.location = tsf.location_id and vri.med_id = tsf.med_id\n"
                    + "               and date(vri.ret_in_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vri.ret_in_date) <= prm_stock_date and vri.deleted = false\n"
                    + "             group by vri.med_id\n"
                    + "             union all\n"
                    + "            select vro.med_id, sum(ifnull(ret_out_smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_return_out vro, tmp_stock_filter tsf\n"
                    + "             where vro.location = tsf.location_id and vro.med_id = tsf.med_id\n"
                    + "               and date(vro.ret_out_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vro.ret_out_date) <= prm_stock_date and vro.deleted = false\n"
                    + "             group by vro.med_id\n"
                    + "             union all\n"
                    + "            select va.med_id, sum(if(va.adj_type = '-',(ifnull(va.adj_smallest_qty,0)*-1),\n"
                    + "                        ifnull(va.adj_smallest_qty,0))) ttl_qty\n"
                    + "              from v_adj va, tmp_stock_filter tsf\n"
                    + "             where va.location = tsf.location_id and va.med_id = tsf.med_id\n"
                    + "               and date(va.adj_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(va.adj_date) <= prm_stock_date and va.deleted = false\n"
                    + "             group by va.med_id\n"
                    + "             union all\n"
                    + "            select vt.med_id, sum(ifnull(tran_smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_transfer vt, tmp_stock_filter tsf\n"
                    + "             where vt.from_location = tsf.location_id and vt.med_id = tsf.med_id\n"
                    + "               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n"
                    + "             group by vt.med_id\n"
                    + "             union all\n"
                    + "            select vt.med_id, sum(ifnull(tran_smallest_qty,0)) ttl_qty\n"
                    + "              from v_transfer vt, tmp_stock_filter tsf\n"
                    + "             where vt.to_location = tsf.location_id and vt.med_id = tsf.med_id\n"
                    + "               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n"
                    + "             group by vt.med_id\n"
                    + "             union all\n"
                    + "            select vsi.med_id, sum(ifnull(smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_stock_issue vsi, tmp_stock_filter tsf\n"
                    + "             where vsi.location_id = tsf.location_id and vsi.med_id = tsf.med_id\n"
                    + "               and date(vsi.issue_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vsi.issue_date) <= prm_stock_date and vsi.deleted = false\n"
                    + "             group by vsi.med_id\n"
                    + "             union all\n"
                    + "            select vsr.rec_med_id med_id, sum(ifnull(smallest_qty,0)) ttl_qty\n"
                    + "              from v_stock_receive vsr, tmp_stock_filter tsf\n"
                    + "             where vsr.location_id = tsf.location_id and vsr.rec_med_id = tsf.med_id\n"
                    + "               and date(vsr.receive_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vsr.receive_date) <= prm_stock_date and vsr.deleted = false\n"
                    + "             group by vsr.rec_med_id\n"
                    + "             union all\n"
                    + "            select vd.med_id, sum(ifnull(dmg_smallest_qty,0)*-1) ttl_qty\n"
                    + "              from v_damage vd, tmp_stock_filter tsf\n"
                    + "             where vd.location = tsf.location_id and vd.med_id = tsf.med_id\n"
                    + "               and date(vd.dmg_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                    + "               and date(vd.dmg_date) <= prm_stock_date and vd.deleted = false\n"
                    + "             group by vd.med_id) A\n"
                    + "            join v_med_unit_smallest_rel B on A.med_id = B.med_id\n"
                    + "     group by A.med_id) D on C.med_id = D.med_id";
            sql1 = sql1.replace("prm_tran_option", "'Opening'")
                    .replace("prm_user_id", "'" + Global.machineId + "'")
                    .replace("prm_stock_date", "'" + DateUtil.toDateStrMYSQL(strOpDate) + "'");
            dao.execSql(sql1);
            dao.commit();
            String tmpSql = "update tmp_stock_costing tsc left join (select med_id, sum(sale_smallest_qty) sale_qty \n"
                    + "from v_sale where deleted = false and date(sale_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                    + "group by med_id) ts on tsc.med_id = ts.med_id\n"
                    + "set tsc.bal_qty = ts.sale_qty\n"
                    + "where tsc.user_id = '" + Global.machineId + "' and tsc.tran_option = 'Opening'";
            dao.execSql(tmpSql);
            dao.commit();
            dao.execProc("insert_cost_detail",
                    "Opening", DateUtil.toDateStrMYSQL(txtTo.getText()),
                    Global.machineId, strMethod);
            dao.commit();

            dao.execProc("calculate_sale_cost_gp",
                    DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()),
                    Global.machineId);
            /*
            //For closing value
            insertStockFilterCode(txtTo.getText());
            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(txtTo.getText()), "Closing",
                    Global.loginUser.getUserId());
            dao.commit();
            dao.execProc("insert_cost_detail",
                    "Closing", DateUtil.toDateStrMYSQL(txtTo.getText()),
                    Global.loginUser.getUserId(), strMethod);
            dao.commit();
            //Calculating GP
            dao.execProc("calculate_gp", "Opening", "Closing", DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()), Global.loginUser.getUserId());
            dao.commit();
            dao.close();*/
            applyFilter();
        } catch (Exception ex) {
            log.error("calculate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void applyFilter() {
        try {
            String strHSQL = getHSQL();
            List<VGrossProfit> listGrossProfit
                    = dao.findAllHSQL(strHSQL);
            dao.commit();
            gpTableModel.setListGrossProfit(listGrossProfit);

            double op_value = 0;
            double ttl_pur = 0;
            double cl_value = 0;
            double cogs = 0;
            double ttl_sale = 0;
            double grossProfit = 0;

            for (VGrossProfit gp : listGrossProfit) {
                //totalCost += NumberUtil.NZero(sc.getTtlCost());
                op_value += gp.getOpValue();
                ttl_pur += gp.getTtlPur();
                cl_value += gp.getClValue();
                cogs += gp.getCogsValue();
                ttl_sale += gp.getTtlSale();
                grossProfit += gp.getGpValue();
            }

            txtOpValue.setValue(op_value);
            txtTtlPur.setValue(ttl_pur);
            txtClValue.setValue(cl_value);
            txtCogs.setValue(cogs);
            txtTtlSale.setValue(ttl_sale);
            txtGP.setValue(grossProfit);
        } catch (Exception ex) {
            log.error("applyFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private String getHSQL() {
        String strHSQL = "select c from VGrossProfit c where c.key.userId = '"
                + Global.machineId + "'";

        if (cboCustomG.getSelectedItem() instanceof ItemGroup) {
            ItemGroup itemGroup = (ItemGroup) cboCustomG.getSelectedItem();
            try {
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

                strHSQL = strHSQL + " and c.key.itemId in (" + tmpItemList + ")";
            } catch (Exception ex) {
                log.error("getHSQL : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();
            strHSQL = strHSQL + " and c.medTypeId = '" + itemType.getItemTypeCode() + "'";
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();
            strHSQL = strHSQL + " and c.categoryId = " + cat.getCatId();
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            strHSQL = strHSQL + " and c.brandId = " + itemBrand.getBrandId();
        }

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

            strHSQL = strHSQL + " and c.key.itemId in (" + codeFilter + ")";
        }

        return strHSQL;
    }

    private void insertStockFilterCode(String strDate) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(strDate) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true";

        if (chkFilter.isSelected()) {
            if (cboCustomG.getSelectedItem() instanceof ItemGroup) {
                ItemGroup itemGroup = (ItemGroup) cboCustomG.getSelectedItem();
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
                        + Global.machineId + "')";
            }
        }

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.commit();
            dao.close();
        }
    }

    private void print() {
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "rptGP";
        Map<String, Object> params = new HashMap();
        String compName = Util1.getPropValue("report.company.name");

        params.put("compName", compName);
        params.put("data_date", txtFrom.getText() + " to " + txtTo.getText());
        params.put("user_id", Global.machineId);
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

        if (cboCustomG.getSelectedItem() instanceof ItemGroup) {
            ItemGroup itemGroup = (ItemGroup) cboCustomG.getSelectedItem();
            params.put("cg_id", itemGroup.getParentGroupId());
        } else {
            params.put("cg_id", 0);
        }

        dao.close();
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
        dao.commit();
    }

    private void view() {
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "rptGP_AC";
        Map<String, Object> params = new HashMap();
        String compName = Util1.getPropValue("report.company.name");

        params.put("compName", compName);
        params.put("data_date", txtFrom.getText() + " to " + txtTo.getText());
        params.put("user_id", Global.machineId);
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

        if (cboCustomG.getSelectedItem() instanceof ItemGroup) {
            ItemGroup itemGroup = (ItemGroup) cboCustomG.getSelectedItem();
            params.put("cg_id", itemGroup.getParentGroupId());
        } else {
            params.put("cg_id", 0);
        }

        dao.close();
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
        dao.commit();
    }

    private void deleteTmpData() {
        String strSql1 = "delete from tmp_item_code_filter where user_id ='"
                + Global.machineId + "'";

        dao.execSql(strSql1);
    }

    private String getItemType() {
        String value;
        if (cboItemType.getSelectedItem() instanceof ItemType) {
            ItemType itemType = (ItemType) cboItemType.getSelectedItem();
            value = itemType.getItemTypeCode();
        } else {
            value = "0";
        }
        return value;
    }

    private String getCategory() {
        String value;
        if (cboCategory.getSelectedItem() instanceof Category) {
            Category cat = (Category) cboCategory.getSelectedItem();
            value = cat.getCatId().toString();
        } else {
            value = "0";
        }

        return value;
    }

    private String getBrand() {
        String value;
        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            ItemBrand itemBrand = (ItemBrand) cboBrand.getSelectedItem();
            value = itemBrand.getBrandId().toString();
        } else {
            value = "0";
        }

        return value;
    }

    private String getCusGroup() {
        String value;
        if (cboCustomG.getSelectedItem() instanceof ItemGroup) {
            ItemGroup itemGroup = (ItemGroup) cboCustomG.getSelectedItem();
            value = itemGroup.getParentGroupId().toString();
        } else {
            value = "0";
        }

        return value;
    }

    private void execCostBalance1(String date, String option) {
        String userId = Global.machineId;
        String sqlDelete = "delete from tmp_stock_costing where user_id = prm_user_id\n"
                + "	and tran_option = prm_tran_option\n";
        String strSql = "insert into tmp_stock_costing(med_id, user_id, bal_qty, qty_str, tran_option)\n"
                + "    select C.med_id, prm_user_id, ifnull(D.ttl_qty,0), D.ttl_qty_str, prm_tran_option\n"
                + "    from (select distinct med_id from tmp_stock_filter where user_id = prm_user_id) C left join\n"
                + "    (select A.med_id, sum(A.ttl_qty) ttl_qty,\n"
                + "           get_qty_in_str(ifnull(sum(A.ttl_qty),0), B.unit_smallest, B.unit_str) ttl_qty_str\n"
                + "      from (\n"
                + "            select vso.med_id, sum(ifnull(vso.op_smallest_qty,0)) ttl_qty\n"
                + "              from v_stock_op vso, tmp_stock_filter tsf\n"
                + "             where vso.location = tsf.location_id and vso.med_id = tsf.med_id\n"
                + "               and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                + "             group by vso.med_id\n"
                + "             union all\n"
                + "            select vs.med_id, sum((ifnull(vs.sale_smallest_qty, 0)+ifnull(vs.foc_smallest_qty,0))*-1) ttl_qty\n"
                + "              from v_sale vs, tmp_stock_filter tsf\n"
                + "             where vs.location_id = tsf.location_id and vs.med_id = tsf.med_id\n"
                + "               and date(vs.sale_date) >= tsf.op_date and date(vs.sale_date) <= prm_stock_date\n"
                + "               and vs.deleted = false and vs.vou_status = 1 and tsf.user_id = prm_user_id\n"
                + "             group by vs.med_id\n"
                + "             union all\n"
                + "            select vp.med_id, sum(ifnull(vp.pur_smallest_qty,0)+ifnull(vp.pur_foc_smallest_qty,0)) ttl_qty\n"
                + "              from v_purchase vp, tmp_stock_filter tsf\n"
                + "             where vp.location = tsf.location_id and vp.med_id = tsf.med_id\n"
                + "               and date(vp.pur_date) >= tsf.op_date and date(vp.pur_date) <= prm_stock_date\n"
                + "               and vp.deleted = false and vp.vou_status = 1 and tsf.user_id = prm_user_id\n"
                + "             group by vp.med_id\n"
                + "             union all\n"
                + "            select vri.med_id, sum(ifnull(vri.ret_in_smallest_qty,0)) ttl_qty\n"
                + "              from v_return_in vri, tmp_stock_filter tsf\n"
                + "             where vri.location = tsf.location_id and vri.med_id = tsf.med_id\n"
                + "               and date(vri.ret_in_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(vri.ret_in_date) <= prm_stock_date and vri.deleted = false\n"
                + "             group by vri.med_id\n"
                + "             union all\n"
                + "            select vro.med_id, sum(ifnull(ret_out_smallest_qty,0)*-1) ttl_qty\n"
                + "              from v_return_out vro, tmp_stock_filter tsf\n"
                + "             where vro.location = tsf.location_id and vro.med_id = tsf.med_id\n"
                + "               and date(vro.ret_out_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(vro.ret_out_date) <= prm_stock_date and vro.deleted = false\n"
                + "             group by vro.med_id\n"
                + "             union all\n"
                + "            select va.med_id, sum(if(va.adj_type = '-',(ifnull(va.adj_smallest_qty,0)*-1),\n"
                + "                        ifnull(va.adj_smallest_qty,0))) ttl_qty\n"
                + "              from v_adj va, tmp_stock_filter tsf\n"
                + "             where va.location = tsf.location_id and va.med_id = tsf.med_id\n"
                + "               and date(va.adj_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(va.adj_date) <= prm_stock_date and va.deleted = false\n"
                + "             group by va.med_id\n"
                + "             union all\n"
                + "            select vt.med_id, sum(ifnull(tran_smallest_qty,0)*-1) ttl_qty\n"
                + "              from v_transfer vt, tmp_stock_filter tsf\n"
                + "             where vt.from_location = tsf.location_id and vt.med_id = tsf.med_id\n"
                + "               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n"
                + "             group by vt.med_id\n"
                + "             union all\n"
                + "            select vt.med_id, sum(ifnull(tran_smallest_qty,0)) ttl_qty\n"
                + "              from v_transfer vt, tmp_stock_filter tsf\n"
                + "             where vt.to_location = tsf.location_id and vt.med_id = tsf.med_id\n"
                + "               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n"
                + "             group by vt.med_id\n"
                + "             union all\n"
                + "            select vsi.med_id, sum(ifnull(smallest_qty,0)*-1) ttl_qty\n"
                + "              from v_stock_issue vsi, tmp_stock_filter tsf\n"
                + "             where vsi.location_id = tsf.location_id and vsi.med_id = tsf.med_id\n"
                + "               and date(vsi.issue_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(vsi.issue_date) <= prm_stock_date and vsi.deleted = false\n"
                + "             group by vsi.med_id\n"
                + "             union all\n"
                + "            select vsr.rec_med_id med_id, sum(ifnull(smallest_qty,0)) ttl_qty\n"
                + "              from v_stock_receive vsr, tmp_stock_filter tsf\n"
                + "             where vsr.location_id = tsf.location_id and vsr.rec_med_id = tsf.med_id\n"
                + "               and date(vsr.receive_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(vsr.receive_date) <= prm_stock_date and vsr.deleted = false\n"
                + "             group by vsr.rec_med_id\n"
                + "             union all\n"
                + "            select vd.med_id, sum(ifnull(dmg_smallest_qty,0)*-1) ttl_qty\n"
                + "              from v_damage vd, tmp_stock_filter tsf\n"
                + "             where vd.location = tsf.location_id and vd.med_id = tsf.med_id\n"
                + "               and date(vd.dmg_date) >= tsf.op_date and tsf.user_id = prm_user_id\n"
                + "               and date(vd.dmg_date) <= prm_stock_date and vd.deleted = false\n"
                + "             group by vd.med_id) A\n"
                + "            join v_med_unit_smallest_rel B on A.med_id = B.med_id\n"
                + "     group by A.med_id) D on C.med_id = D.med_id";

        sqlDelete = sqlDelete.replace("prm_tran_option", "'" + option + "'")
                .replace("prm_user_id", "'" + userId + "'");
        strSql = strSql.replace("prm_stock_date", "'" + date + "'")
                .replace("prm_tran_option", "'" + option + "'")
                .replace("prm_user_id", "'" + userId + "'");
        try {
            dao.execSql(sqlDelete, strSql);
        } catch (Exception ex) {
            log.error("execCostBalance1 : " + ex.getMessage());
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        chkFilter = new javax.swing.JCheckBox();
        butCalculate = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboItemType = new javax.swing.JComboBox();
        cboCategory = new javax.swing.JComboBox();
        cboBrand = new javax.swing.JComboBox();
        cboCustomG = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCodeFilter = new javax.swing.JTable();
        butPrint = new javax.swing.JButton();
        butView = new javax.swing.JButton();
        butExcel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGP = new javax.swing.JTable();
        txtGP = new javax.swing.JFormattedTextField();
        txtTtlSale = new javax.swing.JFormattedTextField();
        txtCogs = new javax.swing.JFormattedTextField();
        txtClValue = new javax.swing.JFormattedTextField();
        txtTtlPur = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtOpValue = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        cboMethod = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblGPDetail = new javax.swing.JTable();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Filter");

        txtFrom.setEditable(false);
        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtTo.setEditable(false);
        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        butCalculate.setFont(Global.lableFont);
        butCalculate.setText("Calculate");
        butCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCalculateActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Item Type ");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Category");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Brand");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Custom G");

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

        cboCustomG.setFont(Global.textFont);
        cboCustomG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCustomGActionPerformed(evt);
            }
        });

        tblCodeFilter.setFont(Global.textFont);
        tblCodeFilter.setModel(codeTableModel);
        tblCodeFilter.setRowHeight(23);
        jScrollPane2.setViewportView(tblCodeFilter);

        butPrint.setFont(Global.lableFont);
        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        butView.setFont(Global.lableFont);
        butView.setText("View");
        butView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butViewActionPerformed(evt);
            }
        });

        butExcel.setText("Excel");
        butExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(cboCustomG, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(butPrint, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addComponent(butView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel4, jLabel5, jLabel6, jLabel7});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(butPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butView)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butExcel))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cboCustomG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblGP.setFont(Global.textFont);
        tblGP.setModel(gpTableModel);
        tblGP.setRowHeight(23);
        jScrollPane1.setViewportView(tblGP);

        txtGP.setEditable(false);
        txtGP.setFont(Global.textFont);

        txtTtlSale.setEditable(false);
        txtTtlSale.setFont(Global.textFont);

        txtCogs.setEditable(false);
        txtCogs.setFont(Global.textFont);

        txtClValue.setEditable(false);
        txtClValue.setFont(Global.textFont);

        txtTtlPur.setEditable(false);
        txtTtlPur.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total :");

        txtOpValue.setEditable(false);
        txtOpValue.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Method");

        cboMethod.setFont(Global.textFont);
        cboMethod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AVG", "FIFO" }));

        tblGPDetail.setModel(scdTableModel);
        tblGPDetail.setRowHeight(23);
        jScrollPane3.setViewportView(tblGPDetail);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(chkFilter)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(butCalculate))
                                    .addComponent(cboMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 1, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOpValue, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlPur, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClValue, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCogs, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlSale, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1))
                        .addGap(13, 13, 13))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtClValue, txtCogs, txtGP, txtOpValue, txtTtlPur, txtTtlSale});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(cboMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(chkFilter)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butCalculate))))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCogs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlPur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtOpValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

  private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtFrom.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtFromMouseClicked

  private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtTo.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtToMouseClicked

  private void butCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCalculateActionPerformed
      calculate();
  }//GEN-LAST:event_butCalculateActionPerformed

  private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
      print();
  }//GEN-LAST:event_butPrintActionPerformed

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

  private void cboCustomGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCustomGActionPerformed
      if (!isInit) {
          applyFilter();
      }
  }//GEN-LAST:event_cboCustomGActionPerformed

    private void butViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butViewActionPerformed
        view();
    }//GEN-LAST:event_butViewActionPerformed

    private void butExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExcelActionPerformed
        try {
            GenExcel excel = new GPExcel(dao, "gp.xls");
            excel.setUserId(Global.machineId);
            excel.setStrOpDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
            excel.setItemGroup(getCusGroup());
            excel.setItemType(getItemType());
            excel.setCategoryId(getCategory());
            excel.setBrandId(getBrand());
            excel.insertStockFilterCode();
            excel.genExcel();
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }//GEN-LAST:event_butExcelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCalculate;
    private javax.swing.JButton butExcel;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butView;
    private javax.swing.JComboBox cboBrand;
    private javax.swing.JComboBox cboCategory;
    private javax.swing.JComboBox cboCustomG;
    private javax.swing.JComboBox cboItemType;
    private javax.swing.JComboBox cboMethod;
    private javax.swing.JCheckBox chkFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblCodeFilter;
    private javax.swing.JTable tblGP;
    private javax.swing.JTable tblGPDetail;
    private javax.swing.JFormattedTextField txtClValue;
    private javax.swing.JFormattedTextField txtCogs;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtGP;
    private javax.swing.JFormattedTextField txtOpValue;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTtlPur;
    private javax.swing.JFormattedTextField txtTtlSale;
    // End of variables declaration//GEN-END:variables
}
