/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.healper.PackageDetailEdit;
import com.cv.app.opd.database.entity.ClinicPackageDetailHis;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PackageDetailEditTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PackageDetailEditTableModel.class.getName());
    private List<PackageDetailEdit> list = new ArrayList();
    private final String[] columnNames = {"Item Option", "Item Code", "Item Name",
        "Ttl Use Qty", "Amount", "Ttl Allow Qty", "Over Use Qty", "Status"};
    private final AbstractDataAccess dao;
    private double packageAmount;
    private JFormattedTextField txtTotalBill;
    private JFormattedTextField txtOverUsage;
    private JFormattedTextField txtExtraUsage;
    private JFormattedTextField txtPackageUsage;
    private JFormattedTextField txtGainLost;
    private boolean status = false;
    private Long pkgId;
    private String vouNo;
    private String pkgOption;
    
    public PackageDetailEditTableModel() {
        dao = Global.dao;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        //PackageDetailEdit record = list.get(row);

        switch (column) {
            case 5: //Ttl Allow Qty
                return true;
            /*case 8: //Include
                return !(record.getTtlAllowQty() > 0);
            case 9: //Exclude
                return record.getTtlAllowQty() > 0;*/
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Item Option
                return String.class;
            case 1: //Item Code
                return String.class;
            case 2: //Item Name
                return String.class;
            case 3: //Ttl Use Qty
                return Float.class;
            case 4: //Amount
                return Double.class;
            case 5: //Ttl Allow Qty
                return Float.class;
            case 6: //Over Use Qty
                return Float.class;
            case 7: //Status
                return String.class;
            /*case 8: //Include
                return Boolean.class;
            case 9: //Exclude
                return Boolean.class;*/
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list != null) {
            if (!list.isEmpty()) {
                try {
                    PackageDetailEdit record = list.get(row);

                    switch (column) {
                        case 0: //Item Option
                            return record.getItemOption();
                        case 1: //Item Code
                            return record.getItemId();
                        case 2: //Item Name
                            return record.getItemName();
                        case 3: //Ttl Use Qty
                            return record.getTtlUseQty();
                        case 4: //Amount
                            return record.getTtlAmount();
                        case 5: //Ttl Allow Qty
                            return record.getTtlAllowQty();
                        case 6: //Over Use Qty
                            return record.getOverUseQty();
                        case 7: //Status
                            return record.getStrStatus();
                        /*case 8: //Include
                            return record.getInclude();
                        case 9: //Exclude
                            return record.getExclude();*/
                        default:
                            return null;
                    }
                } catch (Exception ex) {
                    log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        try {
            PackageDetailEdit record = list.get(row);

            switch (column) {
                case 5: //Ttl Allow Qty
                    Float prvValue = record.getTtlAllowQty();
                    String prvStatus = record.getStrStatus();

                    if (value == null) {
                        record.setTtlAllowQty(0f);
                    } else if (value.toString().trim().isEmpty()) {
                        record.setTtlAllowQty(0f);
                    } else {
                        record.setTtlAllowQty(Float.parseFloat(value.toString()));
                    }
                    Float overUsage = (record.getTtlUseQty() - record.getTtlAllowQty());
                    record.setOverUseQty(overUsage);
                    saveRecord(record, prvValue, prvStatus);
                    calcTotal();
                    status = true;
                    break;
                /*case 8: //Include
                    record.setInclude((Boolean) value);
                    break;
                case 9: //Exclude
                    record.setExclude((Boolean) value);
                    break;*/
            }
            fireTableCellUpdated(row, column);
            fireTableCellUpdated(row, 6);
            fireTableCellUpdated(row, 7);
        } catch (NumberFormatException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void initData(String vouNo, String regNo, String admissionNo, String tranOption) {
        list = new ArrayList();
        System.gc();
        this.vouNo = vouNo;
        double billTotal = 0;
        float overUsageTotal = 0f;
        float extraUsageTotal = 0f;
        double packageUsageTotal = 0;

        String strSql = "select os.reg_no, os.admission_no, vui.item_option, vui.med_id, os.item_key, vui.med_name, \n"
                + "	   sum(os.ttl_use_qty) ttl_use_qty, sum(os.ttl_amt) ttl_amt, sum(os.ttl_allow_qty) ttl_allow_qty, \n"
                + "       if(sum(os.ttl_use_qty) - sum(os.ttl_allow_qty)>0, sum(os.ttl_use_qty) - sum(os.ttl_allow_qty), 0) over_usage\n"
                + "  from (\n"
                + "		select a.reg_no, a.admission_no, a.item_key, sum(a.ttl_use_qty) ttl_use_qty,\n"
                + "			   sum(a.ttl_amt) ttl_amt, (sum(a.ttl_amt)/sum(a.ttl_use_qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from (select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(sale_smallest_qty) ttl_use_qty,\n"
                + "					   sum(sale_amount) ttl_amt\n"
                + "				  from (select sh.deleted, sh.admission_no, sh.reg_no, sdh.med_id, sdh.sale_smallest_qty, sdh.sale_amount\n"
                + "						  from sale_his sh, sale_detail_his sdh\n"
                + "						 where sh.sale_inv_id = sdh.vou_no) a \n"
                + "				 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + admissionNo + "'\n"
                + "                                 and reg_no = '" + regNo + "' "
                + "				 group by reg_no, admission_no, med_id\n"
                + "				 union all\n"
                + "				select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(ret_in_smallest_qty)*-1 ttl_use_qty,\n"
                + "					   sum(ret_in_amount)*-1 ttl_amt\n"
                + "				  from v_return_in\n"
                + "				 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + admissionNo + "'\n"
                + "                                 and reg_no = '" + regNo + "' "
                + "				 group by reg_no, admission_no, med_id\n"
                + "				) a \n"
                + "		 where ifNull(a.admission_no,'')<>'' and a.admission_no ='" + admissionNo + "' \n"
                 + "                                 and a.reg_no = '" + regNo + "' \n"
                + "group by a.reg_no, a.admission_no, a.item_id\n"
                + "		 union all\n"
                + "		select patient_id reg_no, admission_no, concat('OPD-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                + "			   sum(amount) ttl_amt, (sum(amount)/sum(qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from v_opd\n"
                + "		 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + admissionNo + "' "
                + "                                 and patient_id = '" + regNo + "' \n"
                + "              group by reg_no, admission_no, service_id\n"
                + "		 union all\n"
                + "		select patient_id reg_no, admission_no, concat('OT-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                + "			   sum(amount) ttl_amt, (sum(amount)/sum(qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from v_ot\n"
                + "		 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + admissionNo + "' \n"
                + "		   and service_id not in (select sys_prop_value from sys_prop \n"
                + "								   where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id',\n"
                + "										 'system.ot.refund.id')) \n"
                + "                                 and patient_id = '" + regNo + "' \n"
                + "		 group by patient_id, admission_no, service_id\n"
                + "		 union all\n"
                + "		select patient_id reg_no, admission_no, concat('DC-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                + "			   sum(amount) ttl_amt, (sum(amount)/sum(qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from v_dc\n"
                + "		 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + admissionNo + "' \n"
                + "		   and dc_inv_id <> '" + vouNo + "' \n"
                + "		   and service_id not in (select sys_prop_value from sys_prop \n"
                + "								   where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id',\n"
                + "										 'system.dc.refund.id')) \n"
                + "                                 and patient_id = '" + regNo + "' \n"
                + "		 group by patient_id, admission_no, service_id\n"
                + "		 union all\n"
                + "		select '" + regNo + "' reg_no, '" + admissionNo + "' admission_no, item_key, 0 ttl_use_qty, 0 ttl_amt, 0 item_price, \n"
                + "			   sum(ifnull(qty_smallest,0)) ttl_allow_qty\n"
                + "		  from clinic_package_detail_his\n"
                + "		 where dc_inv_no = '" + vouNo + "' and pkg_id = " + pkgId + " \n"
                + "		 group by item_key) os, v_union_item vui\n"
                + "where os.item_key = vui.item_key and (vui.item_option = '" + tranOption + "' or '"
                + tranOption + "' = 'ALL' )"
                + "group by os.reg_no, os.admission_no, vui.item_option, os.item_key, vui.med_name";
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    PackageDetailEdit item = new PackageDetailEdit(
                            rs.getString("item_option"),
                            rs.getString("med_id"),
                            rs.getString("med_name"),
                            rs.getFloat("ttl_use_qty"),
                            rs.getDouble("ttl_amt"),
                            rs.getFloat("ttl_allow_qty"),
                            rs.getFloat("over_usage"),
                            rs.getString("item_key")
                    );

                    billTotal += item.getTtlAmount();
                    if (item.getStrStatus().equals("Over Use")) {
                        if (item.getTtlUseQty() != 0) {
                            overUsageTotal += (item.getTtlAmount() / item.getTtlUseQty())
                                    * item.getOverUseQty();
                        }
                    }
                    if (item.getStrStatus().equals("Extra Usage")) {
                        if (item.getTtlUseQty() != 0) {
                            extraUsageTotal += (item.getTtlAmount() / item.getTtlUseQty())
                                    * item.getOverUseQty();
                        }
                    }
                    list.add(item);
                }
                fireTableDataChanged();

                packageUsageTotal = billTotal - (overUsageTotal + extraUsageTotal);

                txtTotalBill.setValue(billTotal);
                txtOverUsage.setValue(overUsageTotal);
                txtExtraUsage.setValue(extraUsageTotal);
                txtPackageUsage.setValue(packageUsageTotal);
                txtGainLost.setValue(packageAmount - packageUsageTotal);
            }
        } catch (SQLException ex) {
            log.error("initData : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void setControl(JFormattedTextField txtTotalBill,
            JFormattedTextField txtOverUsage,
            JFormattedTextField txtExtraUsage,
            JFormattedTextField txtPackageUsage,
            JFormattedTextField txtGainLost) {
        this.txtTotalBill = txtTotalBill;
        this.txtOverUsage = txtOverUsage;
        this.txtExtraUsage = txtExtraUsage;
        this.txtPackageUsage = txtPackageUsage;
        this.txtGainLost = txtGainLost;
    }

    private void calcTotal() {
        double billTotal = 0;
        float overUsageTotal = 0f;
        float extraUsageTotal = 0f;
        double packageUsageTotal = 0;

        for (PackageDetailEdit item : list) {
            billTotal += item.getTtlAmount();
            if (item.getStrStatus().equals("Over Use")) {
                if (item.getTtlUseQty() != 0) {
                    overUsageTotal += (item.getTtlAmount() / item.getTtlUseQty())
                            * item.getOverUseQty();
                }
            }
            if (item.getStrStatus().equals("Extra Usage")) {
                if (item.getTtlUseQty() != 0) {
                    extraUsageTotal += (item.getTtlAmount() / item.getTtlUseQty())
                            * item.getOverUseQty();
                }
            }
        }

        packageUsageTotal = billTotal - (overUsageTotal + extraUsageTotal);

        txtTotalBill.setValue(billTotal);
        txtOverUsage.setValue(overUsageTotal);
        txtExtraUsage.setValue(extraUsageTotal);
        txtPackageUsage.setValue(packageUsageTotal);
        txtGainLost.setValue(packageAmount - packageUsageTotal);
    }

    public void setPackageAmount(double packageAmount) {
        this.packageAmount = packageAmount;
    }

    public boolean getStatus() {
        return status;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
    }

    public void saveRecord(PackageDetailEdit record, Float prvQty, String prvStatus) {
        String itemKey = record.getItemKey();
        try {
            ClinicPackageDetailHis cpdh = null;
            if (prvStatus.equals("Extra Usage")) {
                cpdh = new ClinicPackageDetailHis();
                cpdh.setDcInvNo(vouNo);
                cpdh.setItemKey(itemKey);
                cpdh.setPkgId(pkgId);
                cpdh.setQtySmallest(record.getTtlAllowQty());
                cpdh.setEditStatus(prvStatus);
                cpdh.setPrvSmallestQty(prvQty);
                cpdh.setQtySmallest(record.getTtlAllowQty());
                cpdh.setPkgOption(pkgOption);
            } else {
                String strSql = "select o from ClinicPackageDetailHis o where o.dcInvNo = '"
                        + vouNo + "' and o.pkgId = " + pkgId.toString() + " and o.itemKey = '"
                        + itemKey + "'";
                List<ClinicPackageDetailHis> tmpList = dao.findAllHSQL(strSql);
                if (list != null) {
                    if (!list.isEmpty()) {
                        cpdh = tmpList.get(0);
                        if (cpdh.getEditStatus() == null) {
                            cpdh.setEditStatus(prvStatus);
                            cpdh.setPrvSmallestQty(prvQty);
                        }
                        cpdh.setQtySmallest(record.getTtlAllowQty());
                    }
                }
            }

            if (cpdh != null) {
                if (cpdh.getId() != null) {
                    backupPackage(cpdh.getId(), "Package-Edit", "PACKAGE-EDIT");
                }
                dao.save(cpdh);
            }
        } catch (Exception ex) {
            log.error("saveRecord : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void backupPackage(Long id, String from, String desp) {
        String strSql = "insert into clinic_bk_clinic_package_detail_his (\n"
                + "  dc_inv_no, pkg_id, item_key, unit_qty, item_unit,\n"
                + "  qty_smallest, sys_price, usr_price, pk_detail_id,\n"
                + "  sys_amt, usr_amt, id, edit_status, prv_smallest_qty,\n"
                + "  updated_date, updated_by, bk_date, bk_by, bk_desp\n"
                + ")\n"
                + "select dc_inv_no, pkg_id, item_key, unit_qty, item_unit,\n"
                + "  qty_smallest, sys_price, usr_price, pk_detail_id,\n"
                + "  sys_amt, usr_amt, id, edit_status, prv_smallest_qty,\n"
                + "  updated_date, updated_by, now(), '" + Global.loginUser.getUserId()
                + "', '" + desp + "' \n"
                + "from clinic_package_detail_his where dc_inv_no = '"
                + vouNo + "' and id = " + id.toString();
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("backupPackage : " + from + " : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public List<PackageDetailEdit> getList() {
        return list;
    }

    public String getPkgOption() {
        return pkgOption;
    }

    public void setPkgOption(String pkgOption) {
        this.pkgOption = pkgOption;
    }
}
