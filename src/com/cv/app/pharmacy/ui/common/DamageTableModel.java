/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.DamageDetailHis;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.util.JoSQLUtil;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DamageTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DamageTableModel.class.getName());
    private List<DamageDetailHis> listDetail;
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Unit", "Cost Price", "Amount"};
    private JTable parent;
    private AbstractDataAccess dao;
    private MedicineUP medUp;
    private MedInfo medInfo;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private int maxUniqueId = 0;
    private String deletedList;
    private String currency;
    
    public DamageTableModel(List<DamageDetailHis> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo) {
        this.listDetail = listDetail;
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
        return !(column == 1 || column == 2 || column == 7);
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
                return Integer.class;
            case 6: //Cost Price
                return Double.class;
            case 7: //Amount
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
            DamageDetailHis record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getMedicineId() == null) {
                            return null;
                        } else {
                            return record.getMedicineId().getShortName();
                        }
                    } else {
                        if (record.getMedicineId() == null) {
                            return null;
                        } else {
                            return record.getMedicineId().getMedId();
                        }
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
                case 4: //Qty
                    return record.getQty();
                case 5: //Unit
                    return record.getUnit();
                case 6: //Cost Price
                    return record.getCostPrice();
                case 7: //Amount
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

        try {
            DamageDetailHis record = listDetail.get(row);
            String medId = listDetail.get(parent.getSelectedRow()).getMedicineId().getMedId();
            int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
            int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
            UnitAutoCompleter unitPopup;

            switch (column) {
                case 0: //Code
                    dao.open();
                    medInfo.getMedInfo((String) value);
                    dao.close();
                    record.setUnit(null);
                    record.setQty(null);
                    record.setExpireDate(null);
                    break;
                case 1: //Medicine Name
                    record.getMedicineId().setMedName((String) value);
                    break;
                case 2: //Relation-Str
                    record.getMedicineId().setRelStr((String) value);
                    break;
                case 3: //Exp-Date
                    if (DateUtil.isValidDate(value)) {
                        record.setExpireDate(DateUtil.toDate(value));
                    }
                    break;
                case 4: //Qty
                    record.setQty(NumberUtil.NZeroFloat(value));
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
                    assignPrice(record);
                    calculateAmount(row);
                    break;
                case 5: //Unit
                    record.setUnit((ItemUnit) value);
                    break;
                case 6: //Cost Price
                    record.setCostPrice(NumberUtil.NZero(value));
                    calculateAmount(row);
                    break;
                default:
                    System.out.println("invalid index");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        fireTableCellUpdated(row, 7);
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

        DamageDetailHis record = listDetail.get(listDetail.size() - 1);

        if (record.getMedicineId().getMedId() != null) {
            return false;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            DamageDetailHis record = new DamageDetailHis();

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

        if (listDetail.isEmpty()) {
            return;
        }

        DamageDetailHis record = listDetail.get(pos);

        record.setMedicineId(med);
        record.setUnit(null);
        record.setQty(null);
        record.setExpireDate(null);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableCellUpdated(pos, 0);
        parent.setColumnSelectionInterval(3, 3);
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        DamageDetailHis record = listDetail.get(row);
        if (record.getDmgDetailId() != null) {
            if (deletedList == null) {
                deletedList = "'" + record.getDmgDetailId() + "'";
            } else {
                deletedList = deletedList + ",'" + record.getDmgDetailId() + "'";
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
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        DamageDetailHis ddh = listDetail.get(row);
        String key = "";

        if (ddh.getMedicineId() != null) {
            key = ddh.getMedicineId().getMedId() + "-" + ddh.getUnit().getItemUnitCode();
        }

        ddh.setSmallestQty(NumberUtil.NZeroFloat(ddh.getQty())
                * medUp.getQtyInSmallest(key));

        ddh.setAmount(NumberUtil.NZero(ddh.getQty())
                * NumberUtil.NZero(ddh.getCostPrice()));
    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;

        if (listDetail != null) {
            for (DamageDetailHis record : listDetail) {
                if (record.getMedicineId().getMedId() != null) {
                    if (NumberUtil.NZero(record.getQty()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
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
            JOptionPane.showMessageDialog(Util1.getParent(), "No damage record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        //parent.setRowSelectionInterval(row, row);
        return status;
    }

    public void setListDetail(List<DamageDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                DamageDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    private void assignPrice(DamageDetailHis ddh) {
        Medicine med = ddh.getMedicineId();
        String key;

        try {
            String strSql = "select vp.pur_unit, vp.pur_unit_cost "
                    + "from v_purchase vp, (select med_id, pur_unit, max(pur_date) pur_date "
                    + "from v_purchase where med_id = '" + med.getMedId() + "') max_date where vp.pur_date = "
                    + "max_date.pur_date and vp.med_id = max_date.med_id and vp.pur_unit = max_date.pur_unit";
            ResultSet rs = dao.execSQL(strSql);
            if (rs.next()) {
                String unitCode = rs.getString("pur_unit");

                if (unitCode.equals(ddh.getUnit().getItemUnitCode())) {
                    ddh.setCostPrice(rs.getDouble("pur_unit_cost"));
                } else {
                    key = med.getMedId() + "-" + unitCode;
                    float qtySmall = medUp.getQtyInSmallest(key);
                    Double smallPrice = rs.getDouble("pur_unit_cost") / qtySmall;
                    key = med.getMedId() + "-" + ddh.getUnit().getItemUnitCode();
                    ddh.setCostPrice(smallPrice * medUp.getQtyInSmallest(key));
                }
            } else {
                key = med.getMedId() + "-" + ddh.getUnit().getItemUnitCode();
                ddh.setCostPrice(medUp.getPrice(key, "N", 0));
            }

            ddh.setAmount(NumberUtil.NZero(ddh.getCostPrice())
                    * NumberUtil.NZero(ddh.getQty()));
        } catch (Exception ex) {
            log.error("assignPrice : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public double getTotalAmount() {
        double total = 0;

        if (listDetail != null) {
            for (DamageDetailHis ddh : listDetail) {
                if (ddh != null) {
                    total += NumberUtil.NZero(ddh.getAmount());
                }
            }
        }

        return total;
    }

    public List<DamageDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.DamageDetailHis"
                + " WHERE medicineId.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void clear() {
        maxUniqueId = 0;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from dmg_detail_his where dmg_detail_id in ("
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
}
