/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RetOutDetailHis;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RetOutTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetOutTableModel.class.getName());
    private List<RetOutDetailHis> listDetail;
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private final AbstractDataAccess dao;
    private final MedicineUP medUp;
    private final MedInfo medInfo;
    private String deletedList;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private int maxUniqueId = 0;

    public RetOutTableModel(List<RetOutDetailHis> listDetail, AbstractDataAccess dao,
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
            case 6: //Price
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
            RetOutDetailHis record = listDetail.get(row);

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
                case 6: //Pur Price
                    return record.getPrice();
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

        int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
        int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
        UnitAutoCompleter unitPopup;

        try {
            RetOutDetailHis record = listDetail.get(row);
            String medId = listDetail.get(parent.getSelectedRow()).getMedicineId().getMedId();

            switch (column) {
                case 0: //Code
                    record.getMedicineId().setMedId((String) value);
                    dao.open();
                    medInfo.getMedInfo((String) value);
                    dao.close();
                    record.setAmount(null);
                    record.setPrice(null);
                    record.setUnit(null);
                    record.setQty(null);
                    parent.setColumnSelectionInterval(4, 4);
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
                    String tmpQtyStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setQty(NumberUtil.NZeroFloat(tmpQtyStr));
                    //For unit popup
                    if (medUp.getUnitList(medId).size() > 1) {
                        unitPopup = new UnitAutoCompleter(x, y,
                                medUp.getUnitList(medId), Util1.getParent());

                        if (unitPopup.isSelected()) {
                            record.setUnit(unitPopup.getSelUnit());
                        }
                    } else {
                        record.setUnit(medUp.getUnitList(medId).get(0));
                    }

                    if (Util1.getPropValue("system.sale.FocusPrice").equals("N")) {
                        if (record.getQty() > 0) {
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            parent.setColumnSelectionInterval(0, 0);
                            //parent.setColumnSelectionInterval(6, 6);
                        }
                    }

                    calculateAmount(row);
                    break;
                case 5: //Unit
                    record.setUnit((ItemUnit) value);
                    break;
                case 6: //Price
                    String tmpPriceStr = NumberUtil.getEngNumber(value.toString().trim());
                    record.setPrice(Double.valueOf(tmpPriceStr));
                    calculateAmount(row);
                    break;
                case 7: //Amount
                    record.setAmount(Double.valueOf(value.toString()));
                default:
                    System.out.println("invalid index");
            }
            fireTableCellUpdated(row, 9);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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

        RetOutDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getMedicineId().getMedId() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            RetOutDetailHis record = new RetOutDetailHis();
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

        RetOutDetailHis record = listDetail.get(pos);

        record.setMedicineId(med);
        record.setAmount(null);
        record.setPrice(null);
        record.setUnit(null);
        record.setQty(null);

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

        RetOutDetailHis record = listDetail.get(row);

        if (record != null) {
            if (record.getRetOutDetailId() != null) {
                if (deletedList == null) {
                    deletedList = "'" + record.getRetOutDetailId() + "'";
                } else {
                    deletedList = deletedList + ",'" + record.getRetOutDetailId() + "'";
                }
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
        RetOutDetailHis rodh = listDetail.get(row);
        String key = "";
        double amount;

        if (rodh.getMedicineId() != null) {
            key = rodh.getMedicineId().getMedId() + "-" + rodh.getUnit().getItemUnitCode();
        }

        rodh.setSmallestQty(NumberUtil.NZeroFloat(rodh.getQty())
                * medUp.getQtyInSmallest(key));

        amount = NumberUtil.NZero(rodh.getQty()) * NumberUtil.NZero(rodh.getPrice());

        rodh.setAmount(amount);
    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;
        int recordCnt = 0;
        if (listDetail != null) {
            for (RetOutDetailHis record : listDetail) {
                if (record.getMedicineId().getMedId() != null) {
                    recordCnt++;
                    if (NumberUtil.NZero(record.getQty()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (NumberUtil.NZero(record.getPrice()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Price must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else {
                        record.setUniqueId(row + 1);
                        row++;
                    }
                }
            }
        }

        if (recordCnt == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No purchase record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        //parent.setRowSelectionInterval(row, row);
        return status;
    }

    public void setListDetail(List<RetOutDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                RetOutDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from ret_out_detail_his where ret_out_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public List<RetOutDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.RetOutDetailHis"
                + " WHERE medicineId.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void clear() {
        maxUniqueId = 0;
    }
}
