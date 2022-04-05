/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemGroup;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.view.VReOrderLevel;
import com.cv.app.pharmacy.ui.common.ItemCodeFilterTableModel;
import com.cv.app.pharmacy.ui.common.ReOrderLevelTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.util.TransferDialog;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.POIUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ReOrderLevelEntry extends javax.swing.JPanel implements KeyPropagate {

    static Logger log = Logger.getLogger(ReOrderLevelEntry.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final ReOrderLevelTableModel tblReOrderModel = new ReOrderLevelTableModel(dao);
    private boolean bindStatus = false;
    private ItemCodeFilterTableModel codeTableModel = new ItemCodeFilterTableModel(dao, false);
    private int mouseClick = 2;

    /**
     * Creates new form ReOrderLevel
     */
    public ReOrderLevelEntry() {
        initComponents();
        initCombo();
        getReOrderLevel();
        applyFilter();
        initTable();
        actionMapping();

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
        BindingUtil.BindComboFilter(cboLocation, getLocationFilter());
        BindingUtil.BindComboFilter(cboTrLocation, getLocationFilter());
        BindingUtil.BindComboFilter(cboItemType, dao.findAll("ItemType"));
        BindingUtil.BindComboFilter(cboCategory, dao.findAll("Category"));
        BindingUtil.BindComboFilter(cboBrand, dao.findAll("ItemBrand"));
        BindingUtil.BindComboFilter(cboGroup, dao.findAllHSQL(
                "select o from ItemGroup o order by o.groupName"));

        new ComBoBoxAutoComplete(cboLocation, this);
        new ComBoBoxAutoComplete(cboItemType, this);
        new ComBoBoxAutoComplete(cboCategory, this);
        new ComBoBoxAutoComplete(cboBrand, this);
        cboTrLocation.setSelectedItem(null);
        bindStatus = true;
    }

    private List getLocationFilter() {
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            return dao.findAllHSQL(
                    "select o from Location o where o.locationId in ("
                    + "select a.key.locationId from UserLocationMapping a "
                    + "where a.key.userId = '" + Global.loginUser.getUserId()
                    + "' and a.isAllowReOrder = true) order by o.locationName");
        } else {
            return dao.findAll("Location");
        }
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

    private void getReOrderLevel() {
        if (cboLocation.getSelectedItem() != null) {
            Integer location = 0;
            if (cboLocation.getSelectedItem() instanceof Location) {
                location = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }
            insertStockFilterCode(location);
            execStockBalanceExp(location);
            /*Integer locationId = ((Location) cboLocation.getSelectedItem())
             .getLocationId();*/
            tblReOrderModel.setLocationId(location);
            dao.execProc("gen_re_order_level", location.toString());
            /*dao.execProc("update_re_order_balance", Global.loginUser.getUserId(),
             locationId.toString());*/
 /*String strSql = "update re_order_level rol left join (select user_id, location_id, med_id, sum(ifnull(bal_qty,0)) ttl_qty\n"
                    + "	from tmp_stock_balance_exp\n"
                    + "	where user_id = '" + Global.loginUser.getUserId() + "' and (location_id = " + location.toString() + " or 0 = " + location.toString() + ")\n"
                    + "           and ifnull(bal_qty,0) <> 0\n"
                    + "	group by user_id, location_id, med_id) tsbe on rol.item_id = tsbe.med_id and rol.location_id = tsbe.location_id join "
                    + "		   v_med_unit_smallest_rel vmusr on rol.item_id = vmusr.med_id "
                    + "	   set rol.balance = tsbe.ttl_qty, \n"
                    + "		   rol.balance_str = get_qty_in_str(ifnull(tsbe.ttl_qty,0), vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "		   rol.bal_max = max_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.max_smallest,0)),\n"
                    + "		   rol.bal_max_str = get_qty_in_str(max_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.max_smallest,0)),\n"
                    + "							vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "		   rol.bal_min = min_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.min_smallest,0)),\n"
                    + "		   rol.bal_min_str = get_qty_in_str(min_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.min_smallest,0)),\n"
                    + "							vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "            rol.main_bal = null, rol.main_bal_str = null "
                    + "	 where (rol.location_id = " + location.toString() + " or 0 = " + location.toString() + ")";*/
            String strSql = "update re_order_level rol left join (select user_id, med_id, sum(ifnull(bal_qty,0)) ttl_qty\n"
                    + "	from tmp_stock_balance_exp\n"
                    + "	where user_id = '" + Global.loginUser.getUserId() + "' \n"
                    + "           and ifnull(bal_qty,0) <> 0\n"
                    + "	group by user_id, med_id) tsbe on rol.item_id = tsbe.med_id join "
                    + "		   v_med_unit_smallest_rel vmusr on rol.item_id = vmusr.med_id "
                    + "	   set rol.balance = tsbe.ttl_qty, \n"
                    + "		   rol.balance_str = get_qty_in_str(ifnull(tsbe.ttl_qty,0), vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "		   rol.bal_max = max_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.max_smallest,0)),\n"
                    + "		   rol.bal_max_str = get_qty_in_str(max_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.max_smallest,0)),\n"
                    + "							vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "		   rol.bal_min = min_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.min_smallest,0)),\n"
                    + "		   rol.bal_min_str = get_qty_in_str(min_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.min_smallest,0)),\n"
                    + "							vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "            rol.main_bal = null, rol.main_bal_str = null "
                    + "	 where (rol.location_id = " + location.toString() + " or 0 = " + location.toString() + ")";
            dao.execSql(strSql);
        }
    }

    private String getHSQL() {
        Integer locationId = 0;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        String strHSQL = "select rol from VReOrderLevel rol where key.locationId = "
                + locationId;

        if (cboItemType.getSelectedItem() instanceof ItemType) {
            String itemType = ((ItemType) cboItemType.getSelectedItem()).getItemTypeCode();
            strHSQL = strHSQL + " and rol.itemTypeId = '" + itemType + "'";
        }

        if (cboCategory.getSelectedItem() instanceof Category) {
            int catId = ((Category) cboCategory.getSelectedItem()).getCatId();
            strHSQL = strHSQL + " and rol.catId = " + catId;
        }

        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            int brandId = ((ItemBrand) cboBrand.getSelectedItem()).getBrandId();
            strHSQL = strHSQL + " and rol.brandId = " + brandId;
        }

        if (codeTableModel.getFilterCodeStr() != null) {
            strHSQL = strHSQL + " and rol.key.med in (" + codeTableModel.getFilterCodeStr() + ")";
        }

        if (cboBalFilter.getSelectedItem() != null) {
            String strBalFilter = cboBalFilter.getSelectedItem().toString();

            switch (strBalFilter) {
                case "Bal > Max":
                    strHSQL = strHSQL + " and rol.balMax > 0";
                    break;
                case "Bal < Min":
                    strHSQL = strHSQL + " and rol.balMin > 0";
                    break;
            }
        }

        if (cboActive.getSelectedItem() != null) {
            String value = cboActive.getSelectedItem().toString();
            switch (value) {
                case "Active":
                    strHSQL = strHSQL + " and rol.itemActive = true";
                    break;
                case "In Active":
                    strHSQL = strHSQL + " and rol.itemActive = false";
                    break;
            }
        }

        if (cboGroup.getSelectedItem() instanceof ItemGroup) {
            String value = ((ItemGroup) cboGroup.getSelectedItem()).getGroupId().toString();
            strHSQL = strHSQL + " and rol.key.med in (select o.item.medId from ItemGroupDetail o "
                    + "where o.groupId = " + value + ")";
        }

        log.info("getHSQL : " + strHSQL);
        return strHSQL;
    }

    private void applyFilter() {
        try {
            if (tblReOrderLevel.getCellEditor() != null) {
                tblReOrderLevel.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        /*Thread thread = new Thread() {
         @Override
         public void run() {
         List<ReOrderLevel> listReOrderLevel = dao.findAllHSQL(getHSQL());
         tblReOrderModel.setListReOrderLevel(listReOrderLevel);
         System.gc();
         };
         };
         thread.start();*/
        List<VReOrderLevel> listReOrderLevel = dao.findAllHSQL(getHSQL());
        tblReOrderModel.setListReOrderLevel(listReOrderLevel);
        System.gc();
    }

    private void initTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblReOrderLevel.setCellSelectionEnabled(true);
        }
        codeTableModel.addEmptyRow();
        tblReOrderLevel.getTableHeader().setFont(Global.lableFont);
        tblReOrderLevel.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        tblReOrderLevel.getColumnModel().getColumn(1).setPreferredWidth(200);//Medicine Name
        tblReOrderLevel.getColumnModel().getColumn(2).setPreferredWidth(80);//Relstr
        tblReOrderLevel.getColumnModel().getColumn(3).setPreferredWidth(40);//Max Qty
        tblReOrderLevel.getColumnModel().getColumn(4).setPreferredWidth(30);//Max Unit
        tblReOrderLevel.getColumnModel().getColumn(5).setPreferredWidth(40);//Min Qty
        tblReOrderLevel.getColumnModel().getColumn(6).setPreferredWidth(30);//Min Unit
        tblReOrderLevel.getColumnModel().getColumn(10).setPreferredWidth(50);//Min Unit

        tblCodeFilter.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblCodeFilter.getColumnModel().getColumn(1).setPreferredWidth(100);//Item Name
        tblCodeFilter.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));

        tblCodeFilter.getModel().addTableModelListener((TableModelEvent e) -> {
            applyFilter();
        });

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblReOrderLevel.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        tblReOrderLevel.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
        tblReOrderLevel.getColumnModel().getColumn(9).setCellRenderer(rightRenderer);
        tblReOrderLevel.getColumnModel().getColumn(10).setCellRenderer(rightRenderer);
    }

    private void execStockBalanceExp(Integer location) {
        dao.execSql("delete from tmp_stock_balance_exp where user_id = '"
                + Global.loginUser.getUserId() + "'");

        dao.execProc("stock_balance_exp", "Opening",
                DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()),
                location.toString(),
                Global.loginUser.getUserId());
    }

    private void insertStockFilterCode(int locationId) {
        //int locationId = 0;
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.loginUser.getUserId() + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.loginUser.getUserId()
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where m.active = true and m.calc_stock = true";

        /*if (cboLocation.getSelectedItem() instanceof Location) {
         locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
         }*/
        //if (locationId != 0) {
        strSQL = strSQL + " and (m.location_id = " + locationId + " or 0 = " + locationId + ")";
        //}

        /*if (cboItemType.getSelectedItem() instanceof ItemType) {
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
         }*/
        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void actionMapping() {
        tblCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCodeFilter.getActionMap().put("F8-Action", actionItemFilterDelete);
    }

    private final Action actionItemFilterDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblCodeFilter.getSelectedRow() >= 0) {
                codeTableModel.delete(tblCodeFilter.getSelectedRow());
            }
        }
    };

    private void updateMain(Integer location) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        insertStockFilterCode(location);
        execStockBalanceExp(location);
        Integer sourceLoc = ((Location) cboLocation.getSelectedItem()).getLocationId();
        String strSql = "update re_order_level rol left join (select user_id, med_id, sum(ifnull(bal_qty,0)) ttl_qty\n"
                + "	from tmp_stock_balance_exp\n"
                + "	where user_id = '" + Global.loginUser.getUserId() + "' and ifnull(bal_qty,0) <> 0\n"
                + "	group by user_id, med_id) tsbe on rol.item_id = tsbe.med_id join "
                + "		   v_med_unit_smallest_rel vmusr on rol.item_id = vmusr.med_id "
                + "	   set rol.main_bal = tsbe.ttl_qty, \n"
                + "		   rol.main_bal_str = get_qty_in_str(ifnull(tsbe.ttl_qty,0), vmusr.unit_smallest, vmusr.unit_str) "
                + "	 where rol.location_id = " + sourceLoc.toString();
        dao.execSql(strSql);
        applyFilter();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void print() {
        String allBal = "All";
        if (cboTrLocation.getSelectedItem() instanceof Location) {
            Location loc = (Location) cboTrLocation.getSelectedItem();
            allBal = loc.getLocationName();
        } else if (cboTrLocation.getSelectedItem() == null) {
            allBal = " ";
        }

        int location = 0;
        if (cboLocation.getSelectedItem() instanceof Location) {
            location = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }

        String rptName = Util1.getPropValue("system.pharmacy.reorder.level");
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + rptName;
        Map<String, Object> params = new HashMap();
        params.put("compName", Util1.getPropValue("report.company.name"));
        params.put("all_bal", allBal);
        params.put("p_loc", location);

        String itemType = "-";
        if (cboItemType.getSelectedItem() instanceof ItemType) {
            itemType = ((ItemType) cboItemType.getSelectedItem()).getItemTypeCode();
        }
        params.put("p_item_type", itemType);

        int catId = 0;
        if (cboCategory.getSelectedItem() instanceof Category) {
            catId = ((Category) cboCategory.getSelectedItem()).getCatId();
        }
        params.put("p_cat_id", catId);

        int brandId = 0;
        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            brandId = ((ItemBrand) cboBrand.getSelectedItem()).getBrandId();
        }
        params.put("p_brand_id", brandId);

        int balFilter = 0;
        if (cboBalFilter.getSelectedItem() != null) {
            String strBalFilter = cboBalFilter.getSelectedItem().toString();

            switch (strBalFilter) {
                case "Bal > Max":
                    balFilter = 1;
                    break;
                case "Bal < Min":
                    balFilter = 2;
                    break;
            }
        }
        params.put("p_bal_filter", balFilter);

        int groupId = 0;
        if (cboGroup.getSelectedItem() instanceof ItemGroup) {
            groupId = ((ItemGroup) cboGroup.getSelectedItem()).getGroupId();
        }
        params.put("p_group", groupId);

        params.put("p_active", cboActive.getSelectedItem().toString());

        ReportUtil.viewReport(reportPath, params, dao.getConnection());
    }

    private void genExcel() {
        /*String allBal = "All";
        if (cboTrLocation.getSelectedItem() instanceof Location) {
            Location loc = (Location) cboTrLocation.getSelectedItem();
            allBal = loc.getLocationName();
        } else if (cboTrLocation.getSelectedItem() == null) {
            allBal = " ";
        }*/

        Integer location = 0;
        if (cboLocation.getSelectedItem() instanceof Location) {
            location = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }

        String strSql = "select rol.item_id, med.med_name, med.med_type_id, it.item_type_name, med.med_rel_str,\n"
                + "if(rol.max_unit_qty = null, null, concat(rol.max_unit_qty, rol.max_unit)) as max_qty,\n"
                + "if(rol.min_unit_qty = null, null, concat(rol.min_unit_qty, rol.min_unit)) as min_qty,\n"
                + "rol.balance_str,rol.bal_max_str, rol.bal_min_str, rol.main_bal_str, ifnull(loc.location_name, 'All') as location_name,\n"
                + "loc.location_id, ib.brand_name\n"
                + "from re_order_level rol\n"
                + "join medicine med on rol.item_id = med.med_id\n"
                + "left join location loc on rol.location_id = loc.location_id\n"
                + "left join item_type it on med.med_type_id = it.item_type_code\n"
                + "left join item_brand ib on med.brand_id = ib.brand_id\n"
                + "where rol.location_id = $P{p_loc}\n"
                + "and (ifnull(rol.max_unit_qty,0)<>0 or ifnull(rol.min_unit_qty,0)<>0 or ifnull(rol.balance,0)<>0 or\n"
                + "ifnull(rol.bal_max,0)<>0 or ifnull(rol.bal_min,0)<>0 or ifnull(rol.main_bal,0)<>0)\n"
                + "and (med.brand_id = $P{p_brand_id} or $P{p_brand_id} = 0)\n"
                + "and (med.category_id = $P{p_cat_id} or $P{p_cat_id} = 0)\n"
                + "and (med.med_type_id = $P{p_item_type} or $P{p_item_type} = '-')\n"
                + "and (($P{p_group} > 0 and rol.item_id in (select item_id from item_group_detail where group_id = $P{p_group})) or $P{p_group} = 0)\n"
                + "and (($P{p_bal_filter} = 1 and ifnull(rol.bal_max,0) > 0) or ($P{p_bal_filter} = 2 and ifnull(rol.bal_min,0) > 0) or $P{p_bal_filter} = 0)\n"
                + "and ($P{p_active} = 'All' or ($P{p_active} = 'Active' and med.active = true) \n"
                + "or ($P{p_active} = 'In Active' and med.active = false))\n"
                + "order by ib.brand_name,med.med_type_id, rol.item_id";
        String itemType = "-";
        if (cboItemType.getSelectedItem() instanceof ItemType) {
            itemType = ((ItemType) cboItemType.getSelectedItem()).getItemTypeCode();
        }
        
        Integer catId = 0;
        if (cboCategory.getSelectedItem() instanceof Category) {
            catId = ((Category) cboCategory.getSelectedItem()).getCatId();
        }
        
        Integer brandId = 0;
        if (cboBrand.getSelectedItem() instanceof ItemBrand) {
            brandId = ((ItemBrand) cboBrand.getSelectedItem()).getBrandId();
        }
        
        Integer balFilter = 0;
        if (cboBalFilter.getSelectedItem() != null) {
            String strBalFilter = cboBalFilter.getSelectedItem().toString();

            switch (strBalFilter) {
                case "Bal > Max":
                    balFilter = 1;
                    break;
                case "Bal < Min":
                    balFilter = 2;
                    break;
            }
        }
        
        Integer groupId = 0;
        if (cboGroup.getSelectedItem() instanceof ItemGroup) {
            groupId = ((ItemGroup) cboGroup.getSelectedItem()).getGroupId();
        }
        
        strSql = strSql.replace("$P{p_loc}", location.toString())
                .replace("$P{p_item_type}", "'" + itemType + "'")
                .replace("$P{p_cat_id}", catId.toString())
                .replace("$P{p_brand_id}", brandId.toString())
                .replace("$P{p_bal_filter}", balFilter.toString())
                .replace("$P{p_group}", groupId.toString())
                .replace("$P{p_active}", "'" + cboActive.getSelectedItem().toString() + "'");
                
        try{
            List<String> listHeader = new ArrayList();
            listHeader.add("Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("Min Qty");
            listHeader.add("Balance");
            listHeader.add("Bal < Min");
            listHeader.add("Supplier");
            
            List<String> listField = new ArrayList();
            listField.add("item_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            listField.add("min_qty");
            listField.add("balance_str");
            listField.add("bal_min_str");
            listField.add("brand_name");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("item_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("min_qty", POIUtil.FormatType.TEXT);
            hmType.put("balance_str", POIUtil.FormatType.TEXT);
            hmType.put("bal_min_str", POIUtil.FormatType.TEXT);
            hmType.put("brand_name", POIUtil.FormatType.TEXT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, "ReOrder.xls", "-", "-", "-");
        }catch(Exception ex){
            log.error("genExcel : " + ex.toString());
        }finally{
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
        cboLocation = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cboItemType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        cboCategory = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReOrderLevel = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCodeFilter = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cboActive = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboBalFilter = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboTrLocation = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        cboGroup = new javax.swing.JComboBox();
        butPrint = new javax.swing.JButton();
        butTransfer = new javax.swing.JButton();
        butExcel = new javax.swing.JButton();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Location ");

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Item Type");

        cboItemType.setFont(Global.textFont);
        cboItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboItemTypeActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Category ");

        cboCategory.setFont(Global.textFont);
        cboCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoryActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Brand ");

        cboBrand.setFont(Global.textFont);
        cboBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBrandActionPerformed(evt);
            }
        });

        tblReOrderLevel.setFont(Global.textFont);
        tblReOrderLevel.setModel(tblReOrderModel);
        tblReOrderLevel.setRowHeight(23);
        tblReOrderLevel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblReOrderLevelFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblReOrderLevel);

        tblCodeFilter.setFont(Global.textFont);
        tblCodeFilter.setModel(codeTableModel);
        tblCodeFilter.setRowHeight(23);
        tblCodeFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblCodeFilterFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(tblCodeFilter);

        jLabel6.setText("Active ");

        cboActive.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Active", "In Active" }));
        cboActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboActiveActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Bal-Filter");

        cboBalFilter.setFont(Global.textFont);
        cboBalFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Bal > Max", "Bal < Min" }));
        cboBalFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBalFilterActionPerformed(evt);
            }
        });

        jLabel7.setText("Tr-Loc");

        cboTrLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrLocationActionPerformed(evt);
            }
        });

        jLabel8.setText("Group");

        cboGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboGroupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboTrLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboActive, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboBalFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7, jLabel8});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboActive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboBalFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboTrLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        butTransfer.setText("Transfer");
        butTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTransferActionPerformed(evt);
            }
        });

        butExcel.setText("Excel");
        butExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboCategory, 0, 102, Short.MAX_VALUE)
                                    .addComponent(cboBrand, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cboLocation, 0, 102, Short.MAX_VALUE)
                                    .addComponent(cboItemType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(butPrint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butTransfer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butExcel)))
                        .addGap(0, 16, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butPrint)
                            .addComponent(butTransfer)
                            .addComponent(butExcel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (bindStatus) {
            getReOrderLevel();
            applyFilter();
            cboTrLocation.setSelectedItem(null);
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void cboItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemTypeActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboItemTypeActionPerformed

    private void cboCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoryActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboCategoryActionPerformed

    private void cboBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBrandActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboBrandActionPerformed

  private void cboBalFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBalFilterActionPerformed
      applyFilter();
  }//GEN-LAST:event_cboBalFilterActionPerformed

    private void cboActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboActiveActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboActiveActionPerformed

    private void cboTrLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrLocationActionPerformed
        if (bindStatus) {
            if (cboTrLocation.getSelectedItem() != null) {
                Integer location = 0;
                String desp = "All Bal";

                if (cboTrLocation.getSelectedItem() instanceof Location) {
                    location = ((Location) cboTrLocation.getSelectedItem()).getLocationId();
                    desp = ((Location) cboTrLocation.getSelectedItem()).getLocationName() + " Bal";
                }

                JTableHeader th = tblReOrderLevel.getTableHeader();
                TableColumnModel tcm = th.getColumnModel();
                TableColumn tc = tcm.getColumn(10);
                tc.setHeaderValue(desp);
                th.repaint();

                updateMain(location);
            }
        }
    }//GEN-LAST:event_cboTrLocationActionPerformed

    private void cboGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboGroupActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboGroupActionPerformed

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        print();
    }//GEN-LAST:event_butPrintActionPerformed

    private void butTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTransferActionPerformed
        if (cboTrLocation.getSelectedItem() instanceof Location && cboLocation.getSelectedItem() instanceof Location) {
            TransferDialog dialog = new TransferDialog(Util1.getParent(),
                    tblReOrderModel.getListReOrderLevel(),
                    (Location) cboTrLocation.getSelectedItem(),
                    (Location) cboLocation.getSelectedItem()
            );
            applyFilter();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select location to transfer.",
                    "Transfer Location", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butTransferActionPerformed

    private void tblCodeFilterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblCodeFilterFocusLost
        /*try {
            if (tblCodeFilter.getCellEditor() != null) {
                tblCodeFilter.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }*/
    }//GEN-LAST:event_tblCodeFilterFocusLost

    private void tblReOrderLevelFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblReOrderLevelFocusLost
        /*try{
            if(tblReOrderLevel.getCellEditor() != null){
                tblReOrderLevel.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblReOrderLevelFocusLost

    private void butExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExcelActionPerformed
        genExcel();
    }//GEN-LAST:event_butExcelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butExcel;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butTransfer;
    private javax.swing.JComboBox cboActive;
    private javax.swing.JComboBox cboBalFilter;
    private javax.swing.JComboBox cboBrand;
    private javax.swing.JComboBox cboCategory;
    private javax.swing.JComboBox cboGroup;
    private javax.swing.JComboBox cboItemType;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboTrLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblCodeFilter;
    private javax.swing.JTable tblReOrderLevel;
    // End of variables declaration//GEN-END:variables
}
