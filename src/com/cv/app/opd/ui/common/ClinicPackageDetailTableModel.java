/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.opd.database.entity.ClinicPackageDetail;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.opd.database.view.VClinicPackageDetail;
import com.cv.app.opd.database.view.VUnionItem;
import com.cv.app.ot.database.entity.OTProcedure;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ClinicPackageDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ClinicPackageDetailTableModel.class.getName());
    private List<VClinicPackageDetail> listVCPD = new ArrayList();
    private final String[] columnNames = {"Item Option", "Package Item", "Qty", "Unit", "Sale Price", "User Price",
        "Sale Amt", "User Amt"};
    private JTable parent;
    private final AbstractDataAccess dao = Global.dao;
    private final MedicineUP medUp = new MedicineUP(dao);
    private Long pkgId;
    private JFormattedTextField txtSysTotal;
    private JFormattedTextField txtUsrTotal;
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1 || column == 2 || column == 5;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Item Option
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Qty
                return Float.class;
            case 3: //Unit
                return String.class;
            case 4: //Sale Price
                return Double.class;
            case 5: //User Price
                return Double.class;
            case 6: //Sale Amt 
                return Double.class;
            case 7: //User Amt
                return Double.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVCPD == null) {
            return null;
        } else if (listVCPD.isEmpty()) {
            return null;
        }

        VClinicPackageDetail record = listVCPD.get(row);

        switch (column) {
            case 0: //Item Option
                return record.getItemKey();
            case 1: //Description
                return record.getMedName();
            case 2: //Qty
                return record.getUnitQty();
            case 3: //Unit
                return record.getItemUnit();
            case 4: //Sale Price
                return record.getSysPrice();
            case 5: //User Price
                return record.getUsrPrice();
            case 6: //Sale Amt
                return record.getSysAmount();
            case 7: //User Amt
                return record.getUsrAmount();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            VClinicPackageDetail record = listVCPD.get(row);

            switch (column) {
                case 1: //Description
                    if (value != null) {
                        record.setQtySmallest(null);
                        setItem((VUnionItem) value, record);
                    }
                    break;
                case 2: //Qty
                    try {
                    if (value == null) {
                        record.setUnitQty(null);
                        record.setItemUnit(null);
                        record.setQtySmallest(null);
                    } else {
                        String recOption = record.getItemOption();
                        Float qty = Float.parseFloat(value.toString());
                        record.setUnitQty(qty);
                        record.setQtySmallest(Double.parseDouble(qty.toString()));
                        if (recOption.equals("PHARMACY")) {
                            String medId = record.getMedId();
                            if (medUp.getUnitList(medId) == null) {
                                Medicine med = (Medicine) dao.find(Medicine.class, medId);
                                List<RelationGroup> listRel = dao.findAllHSQL("select o from RelationGroup o where o.medId = '"
                                        + med.getMedId() + "' order by o.relUniqueId");
                                med.setRelationGroupId(listRel);
                                medUp.add(med);
                            }
                            if (medUp.getUnitList(medId) != null) {
                                if (medUp.getUnitList(medId).size() > 1) {
                                    int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
                                    int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
                                    UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                            medUp.getUnitList(medId), Util1.getParent());

                                    if (unitPopup.isSelected()) {
                                        record.setItemUnit(unitPopup.getSelUnit().getItemUnitCode());
                                        if (record.getItemUnit() != null) {
                                            String key = medId + "-" + unitPopup.getSelUnit().getItemUnitCode();
                                            record.setSysPrice(medUp.getPrice(key, "N", qty));
                                            Double qtySmallest
                                                    = Double.parseDouble(record.getUnitQty().toString())
                                                    * Double.parseDouble(medUp.getQtyInSmallest(key).toString());
                                            record.setQtySmallest(qtySmallest);
                                        }
                                    }
                                } else {
                                    List<ItemUnit> listIU = medUp.getUnitList(medId);
                                    ItemUnit iu = listIU.get(0);
                                    record.setItemUnit(iu.getItemUnitCode());
                                    String key = medId + "-" + iu.getItemUnitCode();
                                    Double qtySmallest = NumberUtil.NZero(record.getUnitQty())
                                            * NumberUtil.NZero(medUp.getQtyInSmallest(key));
                                    record.setQtySmallest(qtySmallest);
                                    record.setSysPrice(medUp.getPrice(key, "N", qty));
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error("setValueAt : " + ex.getMessage());
                } finally {
                    dao.close();
                }
                break;
                case 3: //Unit
                    break;
                case 4: //Sale Price
                    break;
                case 5: //User Price
                    if (value == null) {
                        record.setUsrPrice(null);
                    } else {
                        record.setUsrPrice(Double.parseDouble(value.toString()));
                    }
                    break;
            }

            setAmount(record);
            saveDetail(record);
            changes.fireIndexedPropertyChange("change", 0, 0, 1);
            fireTableCellUpdated(row, column);
            fireTableCellUpdated(row, 6);
            fireTableCellUpdated(row, 7);
            addEmptyRow();
            calculateTotal();
        } catch (NumberFormatException ex) {
            log.error("setValueAt : " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        return listVCPD.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VClinicPackageDetail> getListVCPD() {
        return listVCPD;
    }

    public void setListVCPD(List<VClinicPackageDetail> listVCPD) {
        this.listVCPD = listVCPD;
        fireTableDataChanged();
        calculateTotal();
    }

    private void setItem(VUnionItem item, VClinicPackageDetail record) {
        switch (item.getItemOption()) {
            case "PHAR":
                try {
                String medId = item.getItemCode();
                Medicine med = (Medicine) dao.find(Medicine.class, medId);
                List<RelationGroup> listRel = dao.findAllHSQL("select o from RelationGroup o where o.medId = '"
                        + med.getMedId() + "' order by o.relUniqueId");
                med.setRelationGroupId(listRel);
                medUp.add(med);
                record.setSysPrice(null);
                record.setUnitQty(null);
            } catch (Exception ex) {
                log.error("setItem : PHAR : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "OPD":
                try {
                String opdSrvId = item.getItemCode();
                Service srv = (Service) dao.find(Service.class, Integer.parseInt(opdSrvId));
                record.setUnitQty(1f);
                record.setSysPrice(srv.getFees());
                record.setQtySmallest(1.0);
            } catch (Exception ex) {
                log.error("setItem : OPD : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "OT":
                try {
                String otSrvId = item.getItemCode();
                OTProcedure otp = (OTProcedure) dao.find(OTProcedure.class, Integer.parseInt(otSrvId));
                record.setUnitQty(1f);
                record.setSysPrice(otp.getSrvFees());
                record.setQtySmallest(1.0);
            } catch (Exception ex) {
                log.error("setItem : OT : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "DC":
                try {
                String dcSrvId = item.getItemCode();
                InpService is = (InpService) dao.find(InpService.class, Integer.parseInt(dcSrvId));
                record.setUnitQty(1f);
                record.setSysPrice(is.getFees());
                record.setQtySmallest(1.0);
                //record.setSysAmount(record.getUnitQty()*record.getSysPrice());
            } catch (Exception ex) {
                log.error("setItem : DC : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
        }

        record.setItemKey(item.getItemKey());
        record.setItemOption(item.getItemOption());
        record.setMedName(item.getItemName());
        record.setMedId(item.getItemCode());
        record.setPkgId(pkgId);
        //record.setQtySmallest(null);
        record.setUsrPrice(null);
    }

    public void addEmptyRow() {
        if (listVCPD != null) {
            if (!hashEmptyRow()) {
                VClinicPackageDetail record = new VClinicPackageDetail();
                listVCPD.add(record);
                fireTableRowsInserted(listVCPD.size() - 1, listVCPD.size() - 1);
                if (parent != null) {
                    parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
                }
            }
        }
    }

    private boolean hashEmptyRow() {
        if (listVCPD == null) {
            return false;
        } else if (listVCPD.isEmpty()) {
            return false;
        }

        VClinicPackageDetail record = listVCPD.get(listVCPD.size() - 1);
        return !(record.getId() != null || record.getMedName() != null);
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public Long getPkgId() {
        return pkgId;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
    }

    private void setAmount(VClinicPackageDetail record) {
        record.setSysAmount(NumberUtil.FloatZero(record.getUnitQty()) * NumberUtil.NZero(record.getSysPrice()));
        record.setUsrAmount(NumberUtil.FloatZero(record.getUnitQty()) * NumberUtil.NZero(record.getUsrPrice()));
    }

    public VClinicPackageDetail getDetail(int index) {
        if (listVCPD == null) {
            return null;
        } else if (listVCPD.isEmpty()) {
            return null;
        } else {
            return listVCPD.get(index);
        }
    }

    public void deleteItem(int index) {
        VClinicPackageDetail vcp = getDetail(index);
        if (vcp != null) {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                    "Are you sure to delete?",
                    "Sale item delete", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                Long id = vcp.getId();
                try {
                    if (id != null) {
                        backup(vcp, "isValidEntry", "SETUP-DELETE");
                        dao.deleteSQL("delete from clinic_package_detail where id = " + id.toString());
                    }
                    listVCPD.remove(index);
                    fireTableRowsDeleted(index, index);
                    changes.fireIndexedPropertyChange("change", 0, 0, 1);
                    if (index - 1 >= 0) {
                        parent.setRowSelectionInterval(index - 1, index - 1);
                    }
                } catch (Exception ex) {
                    log.error("deleteItem : " + ex.getMessage());
                }
            }
        }
    }

    private void saveDetail(VClinicPackageDetail vcp) {
        try {
            if (isValidEntry(vcp)) {
                ClinicPackageDetail cpd = getPackageDetail(vcp);
                dao.save(cpd);
                vcp.setId(cpd.getId());
            }
        } catch (Exception ex) {
            log.error("saveDetail : " + ex.getMessage());
        }
    }

    private ClinicPackageDetail getPackageDetail(VClinicPackageDetail vcp) {
        ClinicPackageDetail cpd = null;
        try {
            if (vcp.getId() == null) {
                cpd = new ClinicPackageDetail();
            } else {
                cpd = (ClinicPackageDetail) dao.find(ClinicPackageDetail.class, vcp.getId());
            }

            cpd.setItemKey(vcp.getItemKey());
            cpd.setItemUnit(vcp.getItemUnit());
            cpd.setQtySmallest(vcp.getQtySmallest());
            cpd.setSysAmount(vcp.getSysAmount());
            cpd.setSysPrice(vcp.getSysPrice());
            cpd.setUnitQty(vcp.getUnitQty());
            cpd.setUsrAmount(vcp.getUsrAmount());
            cpd.setUsrPrice(vcp.getUsrPrice());
            cpd.setPkgId(pkgId);
        } catch (Exception ex) {
            log.error("getPackageDetail : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return cpd;
    }

    private boolean isValidEntry(VClinicPackageDetail vcp) {
        boolean status = true;

        if (vcp.getUnitQty() == null) {
            status = false;
        } else {
            //if (vcp.getId() != null) {
            String strSql = "select o from VClinicPackageDetail o where o.pkgId = "
                    + pkgId + " and o.itemKey = '" + vcp.getItemKey() + "' and o.id <> " + NumberUtil.NZeroL(vcp.getId()).toString();
            try {
                List<VClinicPackageDetail> list = dao.findAllHSQL(strSql);
                if (list != null) {
                    if (!list.isEmpty()) {
                        status = false;
                        JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate Item.",
                                "Duplicate", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                log.error("isValidEntry : " + ex.getMessage());
            } finally {
                dao.close();
            }
            //}

            if (status && vcp.getId() != null) {
                backup(vcp, "isValidEntry", "SETUP-EDIT");
            }
        }

        return status;
    }

    public void setTxtSysTotal(JFormattedTextField txtSysTotal) {
        this.txtSysTotal = txtSysTotal;
    }

    public void setTxtUsrTotal(JFormattedTextField txtUsrTotal) {
        this.txtUsrTotal = txtUsrTotal;
    }

    private void calculateTotal() {
        Double sysTtl = 0.0;
        Double usrTtl = 0.0;

        for (VClinicPackageDetail vcpd : listVCPD) {
            sysTtl += NumberUtil.NZero(vcpd.getSysAmount());
            usrTtl += NumberUtil.NZero(vcpd.getUsrAmount());
        }

        txtSysTotal.setValue(sysTtl);
        txtUsrTotal.setValue(usrTtl);
    }

    public void clear() {
        listVCPD = new ArrayList();
        pkgId = null;
        medUp.clear();
        txtSysTotal.setText(null);
        txtUsrTotal.setText(null);
        fireTableDataChanged();
        System.gc();
    }

    private void backup(VClinicPackageDetail vcp, String from, String desp) {
        String id = vcp.getId().toString();
        String strSql = "insert into clinic_bk_clinic_package_detail(pkg_id,\n"
                + "  item_key, unit_qty, item_unit, qty_smallest, sys_price,\n"
                + "  usr_price, id, sys_amt, usr_amt, updated_date, updated_by,\n"
                + "  bk_date, bk_by, bk_desp)\n"
                + "select pkg_id,item_key, unit_qty, item_unit, qty_smallest, sys_price,\n"
                + "  usr_price, id, sys_amt, usr_amt, updated_date, updated_by, now(), \n"
                + "'" + Global.loginUser.getUserId() + "', '" + desp + "'\n"
                + "from clinic_package_detail\n"
                + "where id = " + id;
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("Package Bacupk Error : " + from + " : " + ex.toString());
        } finally {
            dao.close();
        }
    }
}
