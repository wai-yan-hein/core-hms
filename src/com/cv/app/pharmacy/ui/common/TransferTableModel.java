/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.TransferDetailHis;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.ui.util.StockBalListDialog;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.pharmacy.util.StockList;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TransferTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TransferTableModel.class.getName());
    private List<TransferDetailHis> listDetail;
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "In Hand", "Qty", "Unit", "Balance", "Price", "Amount"};
    private JTable parent;
    private final AbstractDataAccess dao;
    private final MedicineUP medUp;
    private final MedInfo medInfo;
    private final String TABLE = "com.cv.app.pharmacy.database.entity.TransferDetailHis";
    private final SelectionObserver observer;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private int maxUniqueId = 0;
    private String deletedList;
    private StockList stockList;
    private Location location;

    public TransferTableModel(List<TransferDetailHis> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo, SelectionObserver observer) {
        this.listDetail = listDetail;
        this.dao = dao;
        this.medUp = medUp;
        this.medInfo = medInfo;
        this.observer = observer;
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
        return !(column == 1 || column == 2 || column == 3 || column == 7
                || column == 9 || column == 4 || column == 6 || column == 8);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
            case 1: //Medicine Name
            case 2: //Relation-Str
            case 4: //In hand
            case 7: //Balance
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 5: //Qty
                return Integer.class;
            case 8: //Price
                return Double.class;
            case 9: //Amount
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
            TransferDetailHis record = listDetail.get(row);

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
                case 4: //In Hand
                    return record.getInHandQtyStr();
                case 5: //Qty
                    return record.getQty();
                case 6: //Unit
                    return record.getUnit();
                case 7: //Balance
                    return record.getBalQtyStr();
                case 8: //Price
                    return record.getPrice();
                case 9: //Amount
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
            TransferDetailHis record = listDetail.get(row);
            String medId = listDetail.get(parent.getSelectedRow()).getMedicineId().getMedId();
            int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
            int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
            UnitAutoCompleter unitPopup;
            switch (column) {
                case 0: //Code
                    //record.getMedicineId().setMedId((String) value);
                    //dao.open();
                    //medInfo.getMedInfo((String) value);
                    //dao.close();
                    getMedInfo((String) value, row);
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
                case 4: //In Hand
                    //record.setInHandQtyStr((String) value);
                    break;
                case 5: //Qty
                    record.setQty(NumberUtil.NZeroFloat(value));
                    //parent.setColumnSelectionInterval(6, 6);
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
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(0, 0);

                    String key = record.getMedicineId().getMedId() + "-"
                            + record.getUnit().getItemUnitCode();
                    record.setSmallestQty(record.getQty() * medUp.getQtyInSmallest(key));

                    record.setBalQtyStr(MedicineUtil.getQtyInStr(record.getMedicineId(),
                            NumberUtil.NZeroFloat(record.getInHandQtySmall()) - record.getSmallestQty()));
                    assignPrice(record);
                    //Update to stock balance list
                    observer.selected("DemanUpdate", record);
                    break;
                case 6: //Unit
                    record.setUnit((ItemUnit) value);
                    parent.setColumnSelectionInterval(8, 8);
                    break;
                case 7: //Balance
                    //record.setPrice(Double.valueOf(value.toString()));
                    break;
                case 8: //Price
                    record.setPrice(NumberUtil.NZero(value));
                default:
                    System.out.println("invalid index");
            }

            calculateAmount(row);
            fireTableCellUpdated(row, 9);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        parent.requestFocusInWindow();
        fireTableCellUpdated(row, column);
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

        TransferDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getMedicineId().getMedId() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            TransferDetailHis record = new TransferDetailHis();
            record.setMedicineId(new Medicine());
            listDetail.add(record);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    public void setMed(Medicine med, int pos, StockList stockList) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        String strSQL;
        List list;
        TransferDetailHis record = listDetail.get(pos);

        record.setMedicineId(med);
        record.setPrice(null);
        record.setUnit(null);
        record.setQty(null);
        record.setExpireDate(null);

        if (stockList.getStockList(med.getMedId()).size() > 0) {
            new StockBalListDialog(stockList, med, medUp);
            List<Medicine> listMed = stockList.getMedList();

            //for (Medicine tmpMed : listMed) {
            List<Stock> listStock = stockList.getStockList(med.getMedId());
            for (Stock stock : listStock) {
                if (NumberUtil.NZeroFloat(stock.getUnitQty()) > 0
                        || NumberUtil.NZeroFloat(stock.getFocUnitQty()) > 0) {
                    try {
                        if (NumberUtil.NZeroFloat(stock.getUnitQty()) > 0) {
                            strSQL = "SELECT * FROM " + TABLE
                                    + " WHERE medicineId.medId = '" + stock.getMed().getMedId() + "'"
                                    + " and expireDate = '" + stock.getExpDate() + "'";
                            list = JoSQLUtil.getResult(strSQL, listDetail);

                            if (list.isEmpty()) {
                                strSQL = "SELECT * FROM " + TABLE
                                        + " WHERE medicineId.medId = '" + stock.getMed().getMedId() + "'"
                                        + " and expireDate = null and (qty = 0 or qty = null)";
                                list = JoSQLUtil.getResult(strSQL, listDetail);
                            }

                            if (!list.isEmpty()) {
                                for (int i = 0; i < list.size(); i++) {
                                    TransferDetailHis tdh = (TransferDetailHis) list.get(i);
                                    tdh.setQty(stock.getUnitQty());
                                    tdh.setUnit(stock.getUnit());
                                    tdh.setExpireDate(stock.getExpDate());
                                    tdh.setInHandQtyStr(stock.getQtyStr());
                                    tdh.setInHandQtySmall(stock.getQtySmallest());
                                    tdh.setBalQtyStr(stock.getQtyStrBal());
                                    //Add sale price assign code
                                    String key = tdh.getMedicineId().getMedId() + "-"
                                            + stock.getUnit().getItemUnitCode();
                                    tdh.setSmallestQty(stock.getUnitQty() * medUp.getQtyInSmallest(key));
                                    assignPrice(tdh);
                                    calculateAmount(pos);

                                    i = list.size();
                                }
                            } else {
                                TransferDetailHis tdh = new TransferDetailHis();
                                tdh.setMedicineId(med);
                                tdh.setQty(stock.getUnitQty());
                                tdh.setUnit(stock.getUnit());
                                tdh.setExpireDate(stock.getExpDate());
                                tdh.setInHandQtySmall(stock.getQtySmallest());
                                tdh.setInHandQtyStr(stock.getQtyStr());
                                tdh.setBalQtyStr(stock.getQtyStrBal());
                                //Add sale price assign code
                                String key = tdh.getMedicineId().getMedId() + "-"
                                        + stock.getUnit().getItemUnitCode();
                                tdh.setSmallestQty(stock.getUnitQty() * medUp.getQtyInSmallest(key));
                                assignPrice(tdh);
                                listDetail.set(listDetail.size() - 1, tdh);
                                calculateAmount(pos);
                            }
                        }
                    } catch (Exception ex) {
                        log.error("setMed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                    }
                }
            }

            if (!hasEmptyRow()) {
                addEmptyRow();
            }
            parent.setRowSelectionInterval(pos + 1, pos + 1);
            parent.setColumnSelectionInterval(0, 0);
        } else {
            parent.setColumnSelectionInterval(5, 5);
            if (!hasEmptyRow()) {
                addEmptyRow();
            }
        }

        fireTableCellUpdated(pos, 0);
    }

    public void delete(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                TransferDetailHis record = listDetail.get(row);
                if (record.getTranDetailId() != null) {
                    if (deletedList == null) {
                        deletedList = "'" + record.getTranDetailId() + "'";
                    } else {
                        deletedList = deletedList + ",'" + record.getTranDetailId() + "'";
                    }
                }

                if (listDetail.size() > row) {
                    listDetail.remove(row);
                }
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

        TransferDetailHis rodh = listDetail.get(row);
        String key = "";
        double amount;

        if (rodh.getMedicineId() != null) {
            if (rodh.getUnit() != null) {
                key = rodh.getMedicineId().getMedId() + "-" + rodh.getUnit().getItemUnitCode();
            } else {
                key = rodh.getMedicineId().getMedId();
            }
        }

        rodh.setSmallestQty(NumberUtil.NZeroFloat(rodh.getQty())
                * medUp.getQtyInSmallest(key));

        amount = NumberUtil.NZero(rodh.getQty()) * NumberUtil.NZero(rodh.getPrice());
        rodh.setAmount(amount);
    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;
        int dataCnt = 0;

        if (listDetail != null) {
            for (TransferDetailHis record : listDetail) {
                if (record.getMedicineId().getMedId() != null) {
                    dataCnt++;
                    if (NumberUtil.NZero(record.getQty()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (NumberUtil.NZeroInt(record.getUniqueId()) == 0) {
                        record.setUniqueId(row + 1);
                        row++;
                    }
                }
            }
        }

        if (dataCnt == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No transfer record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        maxUniqueId = row;
        parent.setRowSelectionInterval(0, 0);
        return status;
    }

    public void setListDetail(List<TransferDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                TransferDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    private void assignPrice(TransferDetailHis tdh) {
        Medicine med = tdh.getMedicineId();
        String key;
        String strSql = "select vp.pur_unit, vp.pur_unit_cost "
                + "from v_purchase vp, (select med_id, pur_unit, max(pur_date) pur_date "
                + "from v_purchase where med_id = '" + med.getMedId() + "') max_date where vp.pur_date = "
                + "max_date.pur_date and vp.med_id = max_date.med_id and vp.pur_unit = max_date.pur_unit";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    String unitCode = rs.getString("pur_unit");

                    if (unitCode.equals(tdh.getUnit().getItemUnitCode())) {
                        tdh.setPrice(rs.getDouble("pur_unit_cost"));
                    } else {
                        key = med.getMedId() + "-" + unitCode;
                        float qtySmall = medUp.getQtyInSmallest(key);
                        double cost =rs.getDouble("pur_unit_cost");
                        Double smallPrice = cost / qtySmall;
                        key = med.getMedId() + "-" + tdh.getUnit().getItemUnitCode();
                        tdh.setPrice(smallPrice * medUp.getQtyInSmallest(key));
                    }
                }
                rs.close();
            } else {
                key = med.getMedId() + "-" + tdh.getUnit().getItemUnitCode();
                tdh.setPrice(medUp.getPrice(key, "N", 0));
            }

            if (NumberUtil.NZero(tdh.getPrice()) == 0.0) {
                if (med.getPurUnit() != null) {
                    key = med.getMedId() + "-" + med.getPurUnit().getItemUnitCode();
                    float qtySmall = medUp.getQtyInSmallest(key);
                    Double smallPrice = med.getPurPrice() / qtySmall;
                    key = med.getMedId() + "-" + tdh.getUnit().getItemUnitCode();
                    tdh.setPrice(smallPrice * medUp.getQtyInSmallest(key));
                }
            }
            tdh.setAmount(NumberUtil.NZero(tdh.getPrice())
                    * NumberUtil.NZero(tdh.getQty()));
        } catch (SQLException ex) {
            log.error("assignPrice : " + ex.getMessage());
        }
    }

    public double getTotalAmount() {
        double total = 0;

        if (listDetail != null) {
            for (TransferDetailHis tdh : listDetail) {
                if (tdh != null) {
                    total += NumberUtil.NZero(tdh.getAmount());
                }
            }
        }

        return total;
    }

    public List<TransferDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.TransferDetailHis"
                + " WHERE medicineId.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void clear() {
        maxUniqueId = 0;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from transfer_detail_his where tran_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public void getMedInfo(String medCode, int index) {
        Medicine medicine;

        try {
            if (!medCode.trim().isEmpty()) {
                medicine = (Medicine) dao.find("Medicine", "medId = '"
                        + medCode + "' and active = true");

                if (medicine != null) {
                    selectMedicine(medicine, index);
                } else { //For barcode
                    medicine = (Medicine) dao.find("Medicine", "barcode = '"
                            + medCode + "' and active = true");

                    if (medicine != null) {
                        selectMedicine(medicine, index);
                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                                "Invalid.", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                System.out.println("Blank medicine code.");
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void selectMedicine(Medicine medicine, int index) {
        try {
            dao.open();
            Medicine med = (Medicine) dao.find(Medicine.class, medicine.getMedId());
            List<RelationGroup> listRel = med.getRelationGroupId();
            med.setRelationGroupId(listRel);

            if (listRel.size() > 0) {
                medUp.add(med);
                stockList.add(med, location);
                setMed(med, index, stockList);
            } else {
                System.out.println("Transfer.selected MedicineList : Cannot get relation group");
            }
        } catch (Exception ex) {
            log.error("MedicineList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public StockList getStockList() {
        return stockList;
    }

    public void setStockList(StockList stockList) {
        this.stockList = stockList;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
