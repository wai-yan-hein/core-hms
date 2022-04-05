/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.PurDetailHis;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.view.VLastCostPriceUnit;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PurchaseTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurchaseTableModel1.class.getName());
    private List<PurDetailHis> listDetail;
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Pur Price", "%1", "FOC",
        "Expense %", "Expense", "Unit Cost", "Amount"};
    private JTable parent;
    private final AbstractDataAccess dao;
    private final MedicineUP medUp;
    private final MedInfo medInfo;
    private final ChargeType defaultChargeType;
    private String deletedList;
    private Location location;
    private JLabel lblItemBrand;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private Float expPercent;

    public PurchaseTableModel1(List<PurDetailHis> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo) {
        this.listDetail = listDetail;
        this.dao = dao;
        this.medUp = medUp;
        this.medInfo = medInfo;
        defaultChargeType = (ChargeType) dao.find(ChargeType.class, 1);
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
        switch (column) {
            case 0: //Code
                return true;
            case 1: //Description
                return false;
            case 2: //Relation-Str
                return false;
            case 3: //Exp-Date
                return true;
            case 4: //Qty
                return true;
            case 5: //Pur Price
                return true;
            case 6: //%1
                return true;
            case 7: //FOC
                return true;
            case 8: //Expense %
                return true;
            case 9: //Expense
                return true;
            case 10: //Unit cost
                return true;
            case 11: //Amount
                return false;
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Relation-Str
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 4: //Qty
                return String.class;
            case 5: //Pur Price
                return Double.class;
            case 6: //%1
                return Double.class;
            case 7: //FOC
                return String.class;
            case 8: //Expense %
                return Double.class;
            case 9: //Expense
                return Double.class;
            case 10: //Unit cost
                return Double.class;
            case 11: //Amount
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
            PurDetailHis record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getMedId() == null) {
                            return null;
                        } else {
                            return record.getMedId().getShortName();
                        }
                    } else {
                        if (record.getMedId() == null) {
                            return null;
                        } else {
                            return record.getMedId().getMedId();
                        }
                    }
                case 1: //Medicine Name
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getMedName();
                    }
                case 2: //Relation-Str
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getRelStr();
                    }
                case 3: //Exp-Date
                    return DateUtil.toDateStr(record.getExpireDate());
                case 4: //Qty
                    if (record.getQuantity() == null) {
                        return null;
                    } else {
                        return record.getQuantity() + " " + record.getUnitId();
                    }
                case 5: //Pur Price
                    return record.getPrice();
                case 6: //%1
                    return record.getDiscount1();
                case 7: //FOC
                    if (record.getFocQty() == null) {
                        return null;
                    } else {
                        return record.getFocQty() + " " + record.getFocUnitId();
                    }
                case 8: //Expense %
                    return record.getItemExpenseP();
                case 9: //Expense
                    return record.getItemExpense();
                case 10: //Unit Cost
                    return record.getUnitCost();
                case 11: //Amount
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
            PurDetailHis record = listDetail.get(row);
            String medId = listDetail.get(parent.getSelectedRow()).getMedId().getMedId();
            switch (column) {
                case 0: //Code
                    medInfo.getMedInfo((String) value);
                    /*record.setAmount(null);
                     record.setDiscount1(null);
                     record.setDiscount2(null);
                     record.setFocQty(null);
                     record.setFocUnitId(null);
                     record.setUnitCost(null);
                     record.setPrice(null);
                     record.setUnitId(null);
                     record.setQuantity(null);*/
                    break;
                case 1: //Medicine Name
                    if (value != null) {
                        record.getMedId().setMedName((String) value);
                    } else {
                        record.getMedId().setMedName(null);
                    }
                    break;
                case 2: //Relation-Str
                    if (value != null) {
                        record.getMedId().setRelStr((String) value);
                    } else {
                        record.getMedId().setRelStr(null);
                    }
                    break;
                case 3: //Exp-Date
                    if (DateUtil.isValidDate(value)) {
                        record.setExpireDate(DateUtil.toDate(value));
                    }
                    break;
                case 4: //Qty
                    record.setQuantity(NumberUtil.NZeroFloat(value));
                    //parent.setColumnSelectionInterval(5, 5);
                    //For unit popup
                    if (record.getQuantity() > 0) {
                        if (medUp.getUnitList(medId).size() > 1) {
                            unitPopup = new UnitAutoCompleter(x, y,
                                    medUp.getUnitList(medId), Util1.getParent());

                            if (unitPopup.isSelected()) {
                                record.setUnitId(unitPopup.getSelUnit());
                                //parent.setColumnSelectionInterval(6, 6);
                            }
                        } else {
                            record.setUnitId(medUp.getUnitList(medId).get(0));
                        }

                        //record.setPrice(getLastCostPrice(medId, record.getUnitId().getItemUnitCode()));
                        getPrice(medId, record);
                    } else {
                        record.setUnitId(null);
                    }
                    break;
                case 5: //Pur Price
                    record.setPrice(Double.valueOf(value.toString()));
                    //parent.setColumnSelectionInterval(6, 6);
                    break;
                case 6: //Discount1
                    if (value == null) {
                        record.setDiscount1(null);
                    } else {
                        if (value.toString().trim().isEmpty()) {
                            record.setDiscount1(null);
                        } else {
                            record.setDiscount1(Double.valueOf(value.toString().trim()));
                        }
                    }
                    break;
                case 7: //FOC
                    if (NumberUtil.NZeroFloat(value) == 0) {
                        record.setFocQty(null);
                        record.setFocSmallestQty(new Float(0));
                    } else {
                        record.setFocQty(Float.valueOf(value.toString()));
                    }

                    if (NumberUtil.NZeroFloat(record.getFocQty()) > 0) {
                        if (medUp.getUnitList(medId).size() > 1) {
                            unitPopup = new UnitAutoCompleter(x, y,
                                    medUp.getUnitList(medId), Util1.getParent());

                            if (unitPopup.isSelected()) {
                                record.setFocUnitId(unitPopup.getSelUnit());
                                float qtySmall = medUp.getQtyInSmallest(medId + "-"
                                        + unitPopup.getSelUnit().getItemUnitCode());
                                record.setFocSmallestQty(record.getFocQty() * qtySmall);
                                //parent.setRowSelectionInterval(row + 1, row + 1);
                                //parent.setColumnSelectionInterval(0, 0);
                            }
                        } else {
                            record.setFocUnitId(medUp.getUnitList(medId).get(0));
                            float qtySmall = medUp.getQtyInSmallest(medId + "-"
                                    + medUp.getUnitList(medId).get(0).getItemUnitCode());
                            record.setFocSmallestQty(record.getFocQty() * qtySmall);
                        }
                    } else {
                        record.setFocUnitId(null);
                        fireTableCellUpdated(row, 10);
                    }
                    break;
                case 8: //Expense %
                    if (value == null) {
                        record.setItemExpenseP(null);
                    } else {
                        record.setItemExpenseP(Float.valueOf(value.toString()));
                        record.setItemExpense(null);
                    }
                    break;
                case 9: //Expense
                    record.setItemExpenseP(null);
                    record.setItemExpense(Double.valueOf(value.toString()));
                    fireTableCellUpdated(row, 8);
                    break;
                case 10: //Unit Cose
                    record.setUnitCost(Double.valueOf(value.toString()));
                    break;
                case 11: //Amount
                    record.setAmount(Double.valueOf(value.toString()));
                    break;
                default:
                    System.out.println("invalid index");
            }

            calculateAmount(row);
            fireTableCellUpdated(row, 9);
            fireTableCellUpdated(row, 10);
            fireTableCellUpdated(row, 11);
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

        PurDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getMedId().getMedId() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            PurDetailHis record = new PurDetailHis();
            record.setMedId(new Medicine());
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

        PurDetailHis record = listDetail.get(pos);

        if (med.getBrand() != null) {
            lblItemBrand.setText(med.getBrand().getBrandName());
        }
        record.setMedId(med);
        record.setAmount(null);
        record.setDiscount1(null);
        record.setDiscount2(null);
        record.setFocQty(null);
        record.setFocUnitId(null);
        record.setUnitCost(null);
        record.setPrice(null);
        record.setUnitId(null);
        record.setQuantity(null);
        record.setChargeId(defaultChargeType);
        record.setItemExpenseP(expPercent);

        if (Util1.getPropValue("system.purchase.detail.location").equals("Y")) {
            record.setLocation(location);
        }

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableCellUpdated(pos, 0);
        parent.setColumnSelectionInterval(4, 4);
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }
        if (listDetail.isEmpty()) {
            return;
        }

        PurDetailHis record = listDetail.get(row);

        if (NumberUtil.NZeroL(record.getPurDetailId()) > 0) {
            if (deletedList == null) {
                deletedList = record.getPurDetailId().toString();
            } else {
                deletedList = deletedList + "," + record.getPurDetailId().toString();
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
        PurDetailHis pdh = listDetail.get(row);
        String key = "";
        double amount;

        if (pdh.getUnitId() != null) {
            key = pdh.getMedId().getMedId() + "-" + pdh.getUnitId().getItemUnitCode();
        }

        pdh.setPurSmallestQty(NumberUtil.NZeroFloat(pdh.getQuantity())
                * medUp.getQtyInSmallest(key));

        double tmpAmount = NumberUtil.NZeroFloat(pdh.getQuantity()) * NumberUtil.NZero(pdh.getPrice());
        double percent1 = tmpAmount * (NumberUtil.NZero(pdh.getDiscount1()) / 100);
        double percent2 = 0;
        float ttlQty = NumberUtil.NZeroFloat(pdh.getFocSmallestQty())
                + NumberUtil.NZeroFloat(pdh.getPurSmallestQty());

        if (Util1.getPropValue("system.app.purchase.disc2").equals("Y")) {
            percent2 = NumberUtil.NZero(pdh.getDiscount2());
        } else {
            percent2 = (tmpAmount - percent1) * (NumberUtil.NZero(pdh.getDiscount2()) / 100);
        }
        amount = tmpAmount - (percent1 + percent2);

        if (NumberUtil.NZero(pdh.getItemExpenseP()) != 0) {
            double itemExpense = amount
                    * (NumberUtil.FloatZero(pdh.getItemExpenseP()) / 100);
            pdh.setItemExpense(itemExpense);
        }

        if (pdh.getChargeId() != null) {
            if (pdh.getChargeId().getChargeTypeId() != 1) {
                tmpAmount = 0;
                pdh.setAmount(0.0);
            } else {
                pdh.setAmount(amount);
            }
        } else {
            pdh.setAmount(amount);
        }

        if ((NumberUtil.NZeroFloat(pdh.getQuantity())
                + NumberUtil.NZeroFloat(pdh.getFocQty())) > 0) {
            if (pdh.getChargeId().getChargeTypeId() != 1) {
                pdh.setUnitCost(NumberUtil.NZero(pdh.getPrice())
                        + NumberUtil.NZero(pdh.getItemExpense()));
            } else {
                double smallestExpense = NumberUtil.NZero(pdh.getItemExpense()) / ttlQty;
                double smallestCost = (amount / ttlQty) + smallestExpense;
                double cost = 0.0;
                if (NumberUtil.NZeroFloat(pdh.getQuantity()) > 0) {
                    cost = (smallestCost * NumberUtil.NZeroFloat(pdh.getPurSmallestQty()))
                            / NumberUtil.NZeroFloat(pdh.getQuantity());
                }
                //double focValue = NumberUtil.NZeroInt(pdh.getFocSmallestQty()) * (tmpAmount/pdh.getPurSmallestQty());
                //double cost = ((amount- focValue)/NumberUtil.NZeroInt(pdh.getQuantity())) + 
                //        NumberUtil.NZero(pdh.getItemExpense());
                pdh.setUnitCost(cost);
            }
        } else {
            pdh.setUnitCost(0.0);
        }

    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;

        if (listDetail != null) {
            return false;
        }
        
        PurDetailHis tmpRecord = listDetail.get(listDetail.size() - 2);
        int row = NumberUtil.NZeroInt(tmpRecord.getUniqueId());
        for (PurDetailHis record : listDetail) {
            if (row < listDetail.size() - 1) {
                if (record.getMedId().getMedId() != null) {
                    if (NumberUtil.NZeroFloat(record.getQuantity()) <= 0
                            && NumberUtil.NZeroFloat(record.getFocQty()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } /*else if (NumberUtil.NZero(record.getDiscount1()) < 0) {
                         JOptionPane.showMessageDialog(Util1.getParent(), "Discount1 must be positive value.",
                         "Minus qty.", JOptionPane.ERROR_MESSAGE);
                         status = false;
                         } else if (NumberUtil.NZero(record.getDiscount2()) < 0) {
                         JOptionPane.showMessageDialog(Util1.getParent(), "Discount2 must be positive value.",
                         "Minus qty.", JOptionPane.ERROR_MESSAGE);
                         status = false;
                         }*/ else if (NumberUtil.NZero(record.getPrice()) < 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Price must be positive value.",
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

        if (row == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No purchase record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        parent.setRowSelectionInterval(row, row);

        return status;
    }

    public void setListDetail(List<PurDetailHis> listDetail) {
        this.listDetail = listDetail;

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from pur_detail_his where pur_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public String getMedIdList() {
        String strMedIdList = null;
        int row = 0;

        if (listDetail != null) {
            for (PurDetailHis record : listDetail) {
                if (row < listDetail.size() - 1) {
                    if (strMedIdList == null) {
                        strMedIdList = "'" + record.getMedId().getMedId() + "'";
                    } else {
                        strMedIdList = strMedIdList + ",'" + record.getMedId().getMedId() + "'";
                    }
                }

                row++;
            }
        }

        return strMedIdList;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getLastCostPrice(String itemId, String unit) {
        String strSql = "select v from VLastCostPriceUnit v where v.key.medId = '"
                + itemId + "' and v.key.unitId = '" + unit + "'";
        List<VLastCostPriceUnit> listLcpu = null;

        try {
            listLcpu = dao.findAllHSQL(strSql);
        } catch (Exception ex) {
            log.error("getLastCostPrice : " + ex.getMessage());
        }

        Double value = 0.0;

        if (listLcpu != null) {
            if (!listLcpu.isEmpty()) {
                value = listLcpu.get(0).getUnitCostPrice();
            }
        }
        return value;
    }

    public Double getPrice(String itemId, PurDetailHis record) {
        String strSql = "select vmr.med_id, vmr.smallest_qty, vmr.item_unit, a.pur_unit, a.pur_price, a.pur_unit_cost, a.smallest_pur_price, a.smallest_cost,\n"
                + "(a.smallest_pur_price*vmr.smallest_qty) as unit_pur_price, (a.smallest_cost*vmr.smallest_qty) as unit_cost_price\n"
                + "from v_med_rel vmr\n"
                + "join (\n"
                + "select vmr.smallest_qty, vmr.item_unit, a.*, \n"
                + "(a.pur_price/vmr.smallest_qty) as smallest_pur_price,\n"
                + "(a.pur_unit_cost/vmr.smallest_qty) as smallest_cost\n"
                + "from v_med_rel vmr\n"
                + "join (select vp.med_id, vp.pur_price, vp.pur_unit, vp.pur_unit_cost\n"
                + "from v_purchase vp\n"
                + "where vp.deleted = false and vp.med_id = '" + itemId + "' \n"
                + "and vp.pur_date = (select max(pur_date) from v_purchase where med_id = '" + itemId + "')) a\n"
                + "on vmr.med_id = a.med_id and vmr.item_unit = a.pur_unit\n"
                + "where vmr.med_id = '" + itemId + "') a on vmr.med_id = a.med_id where vmr.item_unit = '"
                + record.getUnitId().getItemUnitCode() + "'";
        Double value = 0.0;

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    record.setPrice(rs.getDouble("unit_pur_price"));
                    record.setUnitCost(rs.getDouble("unit_cost_price"));
                    /*switch(option){
                        case "pur":
                            value = rs.getDouble("unit_pur_price");
                            break;
                        case "cost":
                            value = rs.getDouble("unit_cost_price");
                            break;
                    }*/
                }
            }
        } catch (Exception ex) {
            log.error("getPrice : " + ex.getMessage());
        }

        return value;
    }

    public String getBrandName(int index) {
        int ttlRow = listDetail.size();

        if (ttlRow - 1 >= index) {
            try {
                PurDetailHis pdh = listDetail.get(index);
                if (pdh.getMedId() != null) {
                    ItemBrand ib = pdh.getMedId().getBrand();
                    if (ib != null) {
                        return ib.getBrandName();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setLblItemBrand(JLabel lblItemBrand) {
        this.lblItemBrand = lblItemBrand;
    }

    public Float getExpPercent() {
        return expPercent;
    }

    public void setExpPercent(Float expPercent) {
        this.expPercent = expPercent;
    }

    public void reCalculsteExpense() {
        if (listDetail != null) {
            int i = 0;
            for (PurDetailHis record : listDetail) {
                if (record.getMedId() != null) {
                    if (record.getMedId().getMedId() != null) {
                        record.setItemExpense(0.0);
                        record.setItemExpenseP(expPercent);
                        calculateAmount(i);
                        i++;
                    }
                }
            }

            fireTableDataChanged();
        }
    }

    public List<PurDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.PurDetailHis"
                + " WHERE medId.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }
}
