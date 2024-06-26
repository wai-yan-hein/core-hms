/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.pharmacy.ui.common.*;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemRule;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PurchaseIMEINo;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.SaleDetailHis;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.ui.util.StockBalListDialog;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.pharmacy.util.StockList;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class PBPharmacyTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PBPharmacyTableModel.class.getName());
    private List<SaleDetailHis> listDetail;
    private String[] columnNames = {"Code", "Description", "Qty", "Sale Price"};
    private JTable parent;
    private AbstractDataAccess dao;
    private MedicineUP medUp;
    private MedInfo medInfo;
    private ChargeType defaultChargeType;
    private ChargeType focChargeType;
    private final String TABLE = "com.cv.app.pharmacy.database.entity.SaleDetailHis";
    private String cusType;
    private SelectionObserver observer;
    private String deletedList;
    private Location location;
    private List<ItemRule> listItemRule = new ArrayList(); //For Item Rules
    private Date saleDate;
    private String strBarcodeUnit;
    private JLabel lblItemBrand;
    private HashMap<Integer, String> hmLocPrice = new HashMap();
    private String vouStatus = "NEW";
    private boolean canEdit = true;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private int maxUniqueId = 0;
    private StockList stockList;
    private JLabel lblRemark;
    private SaleStockTableModel stockTableModel;
    private String bookType = "-";

    public SaleStockTableModel getStockTableModel() {
        return stockTableModel;
    }

    public void setStockTableModel(SaleStockTableModel stockTableModel) {
        this.stockTableModel = stockTableModel;
    }

    public JLabel getLblRemark() {
        return lblRemark;
    }

    public void setLblRemark(JLabel lblRemark) {
        this.lblRemark = lblRemark;
    }

    public PBPharmacyTableModel(List<SaleDetailHis> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo, SelectionObserver observer) {
        this.listDetail = listDetail;
        this.dao = dao;
        this.medUp = medUp;
        this.medInfo = medInfo;
        this.observer = observer;

        try {
            dao.open();
            defaultChargeType = (ChargeType) dao.find(ChargeType.class, 1);
            focChargeType = (ChargeType) dao.find(ChargeType.class, 2);
            dao.close();
            String strLocPrice = Util1.getPropValue("system.pharmacy.location.price");
            if (!strLocPrice.equals("-") && !strLocPrice.isEmpty()) {
                String[] strLoc = strLocPrice.split(";");
                for (String strTmp : strLoc) {
                    String[] strPrice = strTmp.split(":");
                    hmLocPrice.put(Integer.parseInt(strPrice[0]), strPrice[1]);
                }
            }

        } catch (Exception ex) {
            log.error("SaleTableModel : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
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
        if (listDetail == null) {
            return false;
        }

        if (listDetail.isEmpty()) {
            return false;
        }

        if (!canEdit) {
            return false;
        }

        if (vouStatus.equals("EDIT") && !Util1.hashPrivilege("SaleEditVoucherChange")) {
            if (!canEdit) {
                return false;
            }
        }

        SaleDetailHis record = listDetail.get(row);

        switch (column) {
            case 0: //code
                return true;
            case 1: //Medicine Name
                return false;
//            case 2: //Relation-Str
//                return false;
//            case 3: //Exp-Date
//                return record.getMedId().getMedId() != null;
            case 2: //Qty
                return record.getMedId().getMedId() != null;

            case 3: //Sale Price
                return Util1.hashPrivilege("SalePriceChange");
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Medicine Name
                return String.class;
//            case 2: //Relation-Str
//                return String.class;
//            case 3: //Exp-Date
//                //return Date.class;
//                return String.class;
            case 2: //Qty
                return Float.class;
//            case 5: //Unit
//                return Object.class;
            case 3: //Sale Price
                return Double.class;
//            case 7: //Discount
//                return Double.class;
//            case 8: //FOC
//                return Integer.class;
//            case 9: //FOC-Unit
//                return Object.class;
//            case 10: //Amount
//                return Double.class;
//            case 11: //Location
//                return Location.class;
//            case 12: //Balance
//                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        SaleDetailHis record;
        try {
            record = listDetail.get(row);
        } catch (Exception ex) {
            return null;
        }

        if (record == null) {
            return null;
        }

        switch (column) {
            case 0: //Code
                if (codeUsage.equals("SHORTNAME")) {
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getShortName();
                    }
                } else if (record.getMedId() == null) {
                    return null;
                } else {
                    return record.getMedId().getMedId();
                }
            case 1: //Medicine Name
                if (record.getMedId() == null) {
                    return null;
                } else {
                    return record.getMedId().getMedName();
                }
//            case 2: //Relation-Str
//                if (record.getMedId() == null) {
//                    return null;
//                } else {
//                    return record.getMedId().getRelStr();
//                }
//            case 3: //Exp-Date
//                return DateUtil.toDateStr(record.getExpireDate());
            case 2: //Qty
                return record.getQuantity();
//            case 5: //Unit
//                return record.getUnitId();
            case 3: //Sale Price
                return record.getPrice();
//            case 7: //Discount
//                return record.getDiscount();
//            case 8: //FOC
//                return record.getFocQty();
//            case 9: //FOC-Unit
//                return record.getFocUnit();
//            case 10: //Amount
//                return record.getAmount();
//            case 11: //Location
//                return record.getLocation();
//            case 12: //Balance
//                log.info("row : " + row + " balance : " + record.getBalQtyInString());
//                return record.getBalQtyInString();
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        boolean isAmount = false;

        try {
            SaleDetailHis record = listDetail.get(row);
            switch (column) {
                case 0: //Code
                    if (value.toString().isEmpty()) {
                        return;
                    }
                    if (!((String) value).isEmpty()) {
                        dao.open();
                        String[] strList = value.toString().split("@");
                        if (strList.length > 1) {
                            strBarcodeUnit = strList[1];
                        } else {
                            strBarcodeUnit = null;
                        }
                        medInfo.getMedInfo(strList[0]);
                        dao.close();
//                        if (record.getMedId() != null) {
//                            if (record.getMedId().getMedId() != null) {
//                               // lblRemark.setText(record.getMedId().getChemicalName());
//                               // record.setExpireDate(stockTableModel.getExpireDate(record.getLocation().getLocationId()));
//                                if (Util1.getPropValue("system.app.sale.stockBalance").equals("D")) {
//                                    assignBalance(record);
//                                    fireTableCellUpdated(row, 12);
//                                }
//                            }
//                        }

                    }
                    break;
                case 1: //Medicine Name
                    record.getMedId().setMedName((String) value);
                    break;
//                case 2: //Relation-Str
//                    //record.getMedId().setRelStr((String) value);
//                    break;
//                case 3: //Exp-Date
//                    if (value != null) {
//                        record.setExpireDate(DateUtil.toDate(value, "dd/MM/yyyy"));
//                    } else {
//                        record.setExpireDate(null);
//                    }
//                    break;
                case 2: //Qty
                    String tmpQtyStr = NumberUtil.getEngNumber("1");
                    float qty = NumberUtil.NZeroFloat(tmpQtyStr);
                    record.setQuantity(qty);
                    if (Util1.getPropValue("system.app.sale.musthavestock").equals("Y")) {
                        String medId = record.getMedId().getMedId();
                        Integer locationId = location.getLocationId();
                        String key = medId + "-" + record.getUnitId().getItemUnitCode();
                        float tmpQty = getTotalSaleQty(row, medId);
                        float saleSmallestQty = (record.getQuantity() * medUp.getQtyInSmallest(key)) + tmpQty;
                        if (isOverSell(medId, saleSmallestQty, locationId)) {
                            record.setPrice(null);
                            record.setUnitId(null);
                            record.setQuantity(null);
                            record.setAmount(null);
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Not enough stock.",
                                    "Out Of Stock", JOptionPane.ERROR_MESSAGE);
                        } else {
                            observer.selected("SaleQtyUpdate", record);
                            if (Util1.getPropValue("system.sale.FocusPrice").equals("N")) {
                                if (NumberUtil.FloatZero(record.getQuantity()) > 0 || NumberUtil.FloatZero(record.getFocQty()) > 0) {
                                    parent.setRowSelectionInterval(row + 1, row + 1);
                                    parent.setColumnSelectionInterval(0, 0);
                                    //parent.setColumnSelectionInterval(6, 6);
                                }
                            }
                        }
                    } else {
                        //Update to stock balance list
                        observer.selected("SaleQtyUpdate", record);
                        //calcSTKBalance(record);
                        //fireTableCellUpdated(row, 12);
                        if (Util1.getPropValue("system.sale.FocusPrice").equals("N")) {
                            if (NumberUtil.FloatZero(record.getQuantity()) > 0 || NumberUtil.FloatZero(record.getFocQty()) > 0) {
                                parent.setRowSelectionInterval(row + 1, row + 1);
                                parent.setColumnSelectionInterval(0, 0);
                                //parent.setColumnSelectionInterval(6, 6);
                            }
                        }
                    }
//                    record.setQuantity(qty);
//                    String tmpQtyStr = NumberUtil.getEngNumber(value.toString());
//                    float qty = NumberUtil.NZeroFloat(tmpQtyStr);
//                    record.setQuantity(qty);
//                    //parent.setColumnSelectionInterval(5, 5);
//                    //record.setChargeId(defaultChargeType);
//                    //For unit popup
                    break;
                case 3: //Sale Price
                    record.setSalePriceP(Double.MAX_VALUE);
                    String tmpSalePriceStr = NumberUtil.getEngNumber(value.toString());
                    if (Util1.getPropValue("system.sale.price.choose").equals("Y")) {
                        String medId = listDetail.get(parent.getSelectedRow()).getMedId().getMedId();
                        String key = medId + "-" + medUp.getUnitList(medId).get(0).getItemUnitCode();
                        int pN = NumberUtil.NZeroInt(medUp.getPrice(key, "N", NumberUtil.NZeroFloat(value)));
                        int pA = NumberUtil.NZeroInt(medUp.getPrice(key, "A", NumberUtil.NZeroFloat(value)));
                        int pB = NumberUtil.NZeroInt(medUp.getPrice(key, "B", NumberUtil.NZeroFloat(value)));
                        int pC = NumberUtil.NZeroInt(medUp.getPrice(key, "C", NumberUtil.NZeroFloat(value)));
                        int pD = NumberUtil.NZeroInt(medUp.getPrice(key, "D", NumberUtil.NZeroFloat(value)));
                        if (pN == NumberUtil.NZeroInt(value) || pA == NumberUtil.NZeroInt(value)
                                || pB == NumberUtil.NZeroInt(value)
                                || pC == NumberUtil.NZeroInt(value)
                                || pD == NumberUtil.NZeroInt(value)) {

                            record.setPrice(NumberUtil.NZero(tmpSalePriceStr));
                        } else {
                            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Price.",
                                    "Invalid.", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        record.setPrice(NumberUtil.NZero(tmpSalePriceStr));
                    }

                    if (value != null) {
                        String status = medUp.validePrice(record.getMedId().getMedId(),
                                record.getUnitId().getItemUnitCode(), record.getPrice());
                        if (status.equals("G")) {

                        } else if (status.equals("L")) {
                            //record.setPrice(null);
                            delete(row);
                        } else {//need to delete that row
                            parent.setColumnSelectionInterval(7, 7);
                            //delete(row);
                        }
                    }

                    break;
//                case 7: //Discount
//                    String tmpDiscountStr = NumberUtil.getEngNumber(value.toString());
//                    record.setDiscount(NumberUtil.NZero(tmpDiscountStr));
//                    //parent.setColumnSelectionInterval(8, 8);
//                    break;
//                case 8: //FOC
//                    String tmpFOCStr = NumberUtil.getEngNumber(value.toString());
//                    record.setFocQty(NumberUtil.NZeroFloat(tmpFOCStr));
//
//                    String medId1 = listDetail.get(parent.getSelectedRow()).getMedId().getMedId();
//                    if (medUp.getUnitList(medId1).size() > 1) {
//                        int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
//                        int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
//                        UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
//                                medUp.getUnitList(medId1), Util1.getParent());
//
//                        if (unitPopup.isSelected()) {
//                            record.setFocUnit(unitPopup.getSelUnit());
//                            parent.setColumnSelectionInterval(6, 6);
//                        }
//                    } else {
//                        String key = medId1 + "-" + medUp.getUnitList(medId1).get(0).getItemUnitCode();
//                        record.setFocUnit(medUp.getUnitList(medId1).get(0));
//                        //record.setPrice(medUp.getPrice(key, getCusType()));
//                    }
//
//                    String key = medId1 + "-" + record.getFocUnit().getItemUnitCode();
//                    float focQty = NumberUtil.NZeroFloat(record.getFocQty());
//                    record.setFocSmallestQty(focQty * medUp.getQtyInSmallest(key));
//                    //calcSTKBalance(record);
//                    //fireTableCellUpdated(row, 12);
//                    //Update to stock balance list
//                    observer.selected("SaleQtyUpdate", record);
//
//                    parent.setRowSelectionInterval(row + 1, row + 1);
//                    parent.setColumnSelectionInterval(0, 0);
//                    break;
//                case 9: //FOC-Unit
//                    //record.setAmount(Double.valueOf(value.toString()));
//                    break;
//                case 10: //Amount
//                    if (value != null) {
//                        if (NumberUtil.NZeroInt(record.getQuantity()) != 0) {
//                            double amount = NumberUtil.FloatZero(value);
//                            record.setPrice(amount / NumberUtil.NZeroInt(record.getQuantity()));
//                            record.setDiscount(null);
//                            record.setAmount(amount);
//                            isAmount = true;
//                            fireTableCellUpdated(row, 6);
//                        } else {
//                            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid quantity.",
//                                    "Invalid.", JOptionPane.ERROR_MESSAGE);
//                        }
//                    }
//                    break;
//                case 11: //Location
//                    if (value != null) {
//                        record.setLocation((Location) value);
//                    } else {
//                        record.setLocation(null);
//                    }
//                    break;
                default:
                    System.out.println("invalid index");
            }

            if (bookType.equals("Emergency")) {
                if (NumberUtil.isNumber(Util1.getPropValue("system.emergency.percent"))) {
                    float percent = Float.parseFloat(Util1.getPropValue("system.emergency.percent"));
                    double price = record.getPrice();
                    double percentPrice = price * (percent / 100);
                    log.info("Emergency Price : % " + percent + " New Price : " + percentPrice);
                    record.setPrice(NumberUtil.roundTo(price + percentPrice, 0));
                }
            }

            if (!isAmount) {
                calculateAmount(row);
                fireTableCellUpdated(row, 10);
            }

            if (record.getMedId() != null) {
                if (record.getMedId().getMedId() != null) {
                    if (Util1.getPropValue("system.app.sale.stockBalance").equals("D")) {
                        calcSTKBalance(record);
                        fireTableCellUpdated(row, 12);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        parent.requestFocusInWindow();

        fireTableCellUpdated(row, column);

        /*if (column != 0) {
         parent.setColumnSelectionInterval(4, 4);
         }*/
    }

    public void setSaleDate(Date sDate) {
        saleDate = sDate;
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

        SaleDetailHis record = listDetail.get(listDetail.size() - 1);
        if (record.getMedId() != null) {
            return record.getMedId().getMedId() == null;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleDetailHis record = new SaleDetailHis();

                record.setMedId(new Medicine());
                listDetail.add(record);

                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    public void setMed(Medicine med, int pos, StockList stockList) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        if (this.stockList == null) {
            this.stockList = stockList;
        }

        String strSQL;
        List list;
        SaleDetailHis record = listDetail.get(pos);

        if (med.getBrand() != null) {

            //    lblItemBrand.setText(med.getBrand().getBrandName());
        }
        record.setMedId(med);
        record.setChargeId(defaultChargeType);
        record.setDiscount(null);

        if (Util1.getPropValue("system.sale.detail.location").equals("Y")) {
            record.setLocation(location);
        }

        if (Util1.getPropValue("system.sale.barcode").equals("Y")) {
            ItemUnit iu = medUp.getSmallestUnit(med.getMedId()); //Can change with getGratestUnit
            if (strBarcodeUnit != null) {
                iu = medUp.getUnit(med.getMedId(), strBarcodeUnit);
            }
            String key = med.getMedId() + "-" + iu.getItemUnitCode();

            record.setUnitId(iu);
            record.setPrice(medUp.getPrice(key, getCusType(), NumberUtil.NZeroInt(record.getQuantity())));
            record.setQuantity(1f);
            record.setAmount(NumberUtil.NZero(record.getPrice()) * record.getQuantity());
            record.setSaleSmallestQty(record.getQuantity() * medUp.getQtyInSmallest(key));
        } else {
            record.setPrice(null);
            record.setUnitId(null);
            record.setQuantity(null);
            record.setAmount(null);
        }

        List<Stock> listStock = null;
        if (Util1.getPropValue("system.app.sale.stockBalance").equals("Y")) {
            listStock = stockList.getStockList(med.getMedId());
        }

        if (listStock != null) {
            if (!listStock.isEmpty()) {
                StockBalListDialog dialog = new StockBalListDialog(stockList, med, medUp);
                //List<Medicine> listMed = stockList.getMedList();

                //for (Medicine tmpMed : listMed) {
                listStock = stockList.getStockList(med.getMedId());
                for (Stock stock : listStock) {
                    if (NumberUtil.NZeroFloat(stock.getUnitQty()) > 0
                            || NumberUtil.NZeroFloat(stock.getFocUnitQty()) > 0) {
                        try {
                            if (NumberUtil.NZeroFloat(stock.getUnitQty()) > 0) {
                                strSQL = "SELECT * FROM " + TABLE
                                        + " WHERE medId.medId = '" + stock.getMed().getMedId() + "'"
                                        + " and expireDate = " + stock.getExpDate()
                                        + " and chargeId.chargeTypeDesc = 'Normal'";
                                list = JoSQLUtil.getResult(strSQL, listDetail);

                                if (list.isEmpty()) {
                                    strSQL = "SELECT * FROM " + TABLE
                                            + " WHERE medId.medId = '" + stock.getMed().getMedId() + "'"
                                            + " and expireDate = null and (quantity = 0 or quantity = null)"
                                            + " and chargeId.chargeTypeDesc = null";
                                    list = JoSQLUtil.getResult(strSQL, listDetail);
                                }

                                if (!list.isEmpty()) {
                                    for (int i = 0; i < list.size(); i++) {
                                        SaleDetailHis sdh = (SaleDetailHis) list.get(i);
                                        sdh.setQuantity(stock.getUnitQty());
                                        sdh.setUnitId(stock.getUnit());
                                        sdh.setExpireDate(stock.getExpDate());
                                        sdh.setChargeId(defaultChargeType);

                                        //Add sale price assign code
                                        String key = sdh.getMedId().getMedId() + "-"
                                                + stock.getUnit().getItemUnitCode();
                                        sdh.setPrice(medUp.getPrice(key, getCusType(), NumberUtil.NZeroInt(sdh.getQuantity())));
                                        sdh.setSaleSmallestQty(stock.getUnitQty() * medUp.getQtyInSmallest(key));
                                        int index = listDetail.indexOf(sdh);

                                        sdh.setFocQty(stock.getFocUnitQty());
                                        sdh.setFocUnit(stock.getFocUnit());
                                        if (stock.getFocUnit() != null) {
                                            key = sdh.getMedId().getMedId() + "-"
                                                    + stock.getFocUnit().getItemUnitCode();
                                            sdh.setFocSmallestQty(stock.getFocUnitQty() * medUp.getQtyInSmallest(key));
                                        } else {
                                            sdh.setFocSmallestQty(0f);
                                        }
                                        calculateAmount(index);

                                        i = list.size();
                                    }
                                } else {
                                    SaleDetailHis sdh = new SaleDetailHis();
                                    sdh.setMedId(med);
                                    sdh.setQuantity(stock.getUnitQty());
                                    sdh.setUnitId(stock.getUnit());
                                    sdh.setExpireDate(stock.getExpDate());
                                    sdh.setChargeId(defaultChargeType);
                                    sdh.setLocation(location);

                                    //Add sale price assign code
                                    String key = sdh.getMedId().getMedId() + "-"
                                            + stock.getUnit().getItemUnitCode();
                                    sdh.setPrice(medUp.getPrice(key, getCusType(), NumberUtil.NZeroInt(sdh.getQuantity())));
                                    sdh.setSaleSmallestQty(stock.getUnitQty() * medUp.getQtyInSmallest(key));

                                    sdh.setFocQty(stock.getFocUnitQty());
                                    sdh.setFocUnit(stock.getFocUnit());
                                    if (stock.getFocUnit() != null) {
                                        key = sdh.getMedId().getMedId() + "-"
                                                + stock.getFocUnit().getItemUnitCode();
                                        sdh.setFocSmallestQty(stock.getFocUnitQty() * medUp.getQtyInSmallest(key));
                                    } else {
                                        sdh.setFocSmallestQty(0f);
                                    }
                                    /*key = sdh.getMedId().getMedId() + "-"
                                     + stock.getFocUnit().getItemUnitCode();
                                     sdh.setFocSmallestQty(stock.getFocUnitQty() * medUp.getQtyInSmallest(key));*/

                                    listDetail.set(listDetail.size() - 1, sdh);
                                }
                            }

                            //FOC
                            if (NumberUtil.NZeroInt(stock.getFocUnitQty()) > 0) {
                                strSQL = "SELECT * FROM " + TABLE
                                        + " WHERE medId.medId = '" + stock.getMed().getMedId() + "'"
                                        + " and expireDate = " + stock.getExpDate();
                                list = JoSQLUtil.getResult(strSQL, listDetail);

                                /*
                                 * if (list.isEmpty()) { strSQL = "SELECT * FROM " + TABLE + "
                                 * WHERE medId.medId = '" + stock.getMed().getMedId() + "'" + "
                                 * and expireDate = null and (quantity = 0 or quantity = null)"
                                 * + " and chargeId.chargeTypeDesc = null"; list =
                                 * JoSQLUtil.getResult(strSQL, listDetail); }
                                 */
                                if (!list.isEmpty()) {
                                    for (int i = 0; i < list.size(); i++) {
                                        SaleDetailHis sdh = (SaleDetailHis) list.get(i);
                                        sdh.setFocQty(stock.getFocUnitQty());
                                        sdh.setFocUnit(stock.getFocUnit());

                                        if (stock.getFocUnit() != null) {
                                            String key = sdh.getMedId().getMedId() + "-"
                                                    + stock.getFocUnit().getItemUnitCode();
                                            sdh.setFocSmallestQty(stock.getFocUnitQty() * medUp.getQtyInSmallest(key));
                                        } else {
                                            sdh.setFocSmallestQty(null);
                                        }

                                        i = list.size();
                                    }
                                }
                                /*
                                 * else { SaleDetailHis sdh = new SaleDetailHis();
                                 * sdh.setMedId(med); sdh.setQuantity(stock.getFocUnitQty());
                                 * sdh.setUnitId(stock.getFocUnit());
                                 * sdh.setExpireDate(stock.getExpDate());
                                 * sdh.setChargeId(focChargeType);
                                 *
                                 * //Add sale price assign code String key =
                                 * sdh.getMedId().getMedId() + "-" +
                                 * stock.getFocUnit().getItemUnitCode();
                                 * sdh.setPrice(medUp.getPrice(key, getCusType()));
                                 * sdh.setSaleSmallestQty(stock.getFocUnitQty() *
                                 * medUp.getQtyInSmallest(key)); listDetail.add(sdh);
                                 * calculateAmount(listDetail.size() - 1); }
                                 */
                            }
                        } catch (Exception ex) {
                            log.error("setMed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                        }
                    }
                }
                //}
            }
        }

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        //fireTableCellUpdated(pos, 0);
        fireTableRowsUpdated(pos, pos);
        /*
         * try{ parent.getCellEditor().stopCellEditing(); }catch(Exception ex){
         *
         * }
         */
        if (!Util1.getPropValue("system.sale.barcode").equals("Y")) {
            parent.setColumnSelectionInterval(4, 4);
        } else if (pos + 1 < listDetail.size()) {
            parent.setRowSelectionInterval(pos + 1, pos + 1);
        }
    }

    public void delete(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }
        SaleDetailHis sdh = listDetail.get(row);

        if (sdh.getSaleDetailId() != null) {
            if (deletedList == null) {
                deletedList = "'" + sdh.getSaleDetailId() + "'";
            } else {
                deletedList = deletedList + ",'" + sdh.getSaleDetailId() + "'";
            }
        }

        listDetail.remove(row);
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        }
    }

    private void calculateAmount(int row) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        SaleDetailHis sdh = listDetail.get(row);
        String key = "";
        double discount = NumberUtil.NZero(sdh.getDiscount());
        double saleQty = NumberUtil.NZero(sdh.getQuantity());
        float saleQty1 = NumberUtil.NZeroFloat(sdh.getQuantity());
        double price = NumberUtil.NZero(sdh.getPrice());
        if (sdh.getMedId() == null) {
            return;
        }
        if (sdh.getUnitId() != null) {
            key = sdh.getMedId().getMedId() + "-" + sdh.getUnitId().getItemUnitCode();
        }
        sdh.setSaleSmallestQty(saleQty1 * medUp.getQtyInSmallest(key));
        key = "";

        if (sdh.getFocUnit() != null) {
            key = sdh.getMedId().getMedId() + "-" + sdh.getFocUnit().getItemUnitCode();
        }
        float focQty = NumberUtil.NZeroFloat(sdh.getFocQty());
        sdh.setFocSmallestQty(focQty * medUp.getQtyInSmallest(key));

        String discType = Util1.getPropValue("system.app.sale.discount.calculation");
        switch (discType) {
            case "Percent":
                discount = ((saleQty * price) * (discount / 100));
                break;
            case "Value":
                break;
            case "Each":
                discount = discount * saleQty;
                break;
        }

        double amount = NumberUtil.roundDouble((saleQty * price) - discount, 4);
        sdh.setAmount(amount);
    }

    public boolean isValidEntry() {
        boolean status = true;

        if (listDetail == null) {
            return false;
        }

        int row = maxUniqueId;
        int recordCnt = 0;
        for (SaleDetailHis record : listDetail) {
            if (record.getMedId().getMedId() != null) {
                recordCnt++;
                if (NumberUtil.NZero(record.getQuantity()) <= 0
                        && NumberUtil.NZero(record.getFocQty()) <= 0) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                            "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                } else if (NumberUtil.NZero(record.getDiscount()) < 0) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Discount must be positive value.",
                            "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                } else if (NumberUtil.NZero(record.getPrice()) < 0) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Price must be positive value.",
                            "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                } else if (NumberUtil.NZeroInt(record.getUniqueId()) == 0) {
                    record.setUniqueId(row + 1);
                    row++;
                }
            }
        }

        if (recordCnt == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No sale record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        parent.setRowSelectionInterval(0, 0);
        maxUniqueId = row;

        return status;
    }

    public void setListDetail(List<SaleDetailHis> listDetail) {
        this.listDetail = listDetail;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                SaleDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    public void setCusType(String type, String option) {
        int row = 0;

        cusType = type;
        if (Util1.getPropValue("system.pricelevel").equals("Y")) {
            if (!option.equals("vou")) {
                if (listDetail != null) {
                    for (SaleDetailHis sdh : listDetail) {
                        if (row != (listDetail.size() - 1)) {
                            if (sdh.getMedId() != null && sdh.getUniqueId() != null) {
                                String key = sdh.getMedId().getMedId() + "-"
                                        + sdh.getUnitId().getItemUnitCode();
                                double salePrice = NumberUtil.NZero(medUp.getPrice(key, cusType,
                                        NumberUtil.NZeroInt(sdh.getQuantity())));
                                setValueAt(salePrice, row, 6);
                            }
                        }
                        row++;
                    }
                }
            }
        }
    }

    public void setCusType1(String type) {
        cusType = type;
    }

    private String getCusType() {
        if (cusType == null) {
            cusType = "N";
        }

        if (hmLocPrice.containsKey(location.getLocationId())) {
            cusType = hmLocPrice.get(location.getLocationId());
        }
        return cusType;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from sale_detail_his where sale_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (this.location != null) {
            for (SaleDetailHis sh : listDetail) {
                if (sh.getMedId() != null && sh.getMedId().getMedId() != null) {
                    sh.setLocation(location);
                }
            }
            fireTableDataChanged();
        }
        /*if (location != null) {
         if (hmLocPrice.containsKey(location.getLocationId())) {
         cusType = hmLocPrice.get(location.getLocationId());

         for (SaleDetailHis sdh : listDetail) {
         if (sdh.getMedId() != null) {
         String medId = sdh.getMedId().getMedId();
         if (medId != null) {
         if (sdh.getUnitId() != null) {
         String strUnit = sdh.getUnitId().getItemUnitCode();
         String key = medId + "-" + strUnit;
         sdh.setPrice(medUp.getPrice(key, getCusType(), NumberUtil.NZeroInt(sdh.getQuantity())));
         sdh.setAmount(NumberUtil.FloatZero(sdh.getQuantity()) * NumberUtil.NZero(sdh.getPrice()));
         }
         }
         }
         }
                
         fireTableDataChanged();
         }
         }*/
    }

    public void setStrBarcodeUnit(String strBarcodeUnit) {
        this.strBarcodeUnit = strBarcodeUnit;
    }

    public void setLblItemBrand(JLabel lblItemBrand) {
        this.lblItemBrand = lblItemBrand;
    }

    public String getRemark(int index) {
        try {
            SaleDetailHis sdh = listDetail.get(index);
            if (sdh.getMedId() != null) {
                return sdh.getMedId().getChemicalName();
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public String getBrandName(int index) {
        int ttlRow = listDetail.size();

        if (ttlRow - 1 >= index) {
            try {
                SaleDetailHis sdh = listDetail.get(index);
                if (sdh.getMedId() != null) {
                    ItemBrand ib = sdh.getMedId().getBrand();
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

    public void setPurIMEINo(PurchaseIMEINo purImeiNo, int pos) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }
        SaleDetailHis record = listDetail.get(pos);
        Medicine medicine;
        String itemId = "";

        if (purImeiNo.getKey() != null) {
            try {
                itemId = purImeiNo.getKey().getItemId();
                medicine = (Medicine) dao.find("Medicine", "med_id = '"
                        + itemId + "'");
                List<RelationGroup> listRel = dao.findAllHSQL("select o from RelationGroup o where o.medId = '"
                        + medicine.getMedId() + "' order by o.relUniqueId");
                medicine.setRelationGroupId(listRel);
                medUp.add(medicine);

                record.getMedId().setMedId(itemId);
                record.getMedId().setMedName(medicine.getMedName());
                record.setImei1(purImeiNo.getKey().getImei1());
                record.setImei2(purImeiNo.getImei2());
                record.setSdNo(purImeiNo.getSdNo());
            } catch (Exception ex) {
                log.error("setPurIMEINo : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
        if (Util1.getPropValue("system.sale.detail.location").equals("Y")) {
            record.setLocation(location);
        }
        ItemUnit iu = medUp.getSmallestUnit(itemId); //Can change with getGratestUnit
        if (strBarcodeUnit != null) {
            iu = medUp.getUnit(itemId, strBarcodeUnit);
        }
        String key = itemId + "-" + iu.getItemUnitCode();

        record.setUnitId(iu);
        record.setPrice(medUp.getPrice(key, getCusType(), NumberUtil.NZeroInt(record.getQuantity())));
        record.setQuantity(0f);
        record.setAmount(NumberUtil.NZero(record.getPrice()) * record.getQuantity());
        record.setSaleSmallestQty(record.getQuantity() * medUp.getQtyInSmallest(key));

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableCellUpdated(pos, 0);

        if (!Util1.getPropValue("system.sale.barcode").equals("Y")) {
            parent.setColumnSelectionInterval(4, 4);
        } else if (pos + 1 < listDetail.size()) {
            parent.setRowSelectionInterval(pos + 1, pos + 1);
        }
    }

    public void setVouStatus(String vouStatus) {
        this.vouStatus = vouStatus;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    private void assignBalance(SaleDetailHis record) {
        String medId = record.getMedId().getMedId();
        try {
            ResultSet resultSet = dao.getPro("GET_STOCK_BALANCE_CODE",
                    location.getLocationId().toString(), medId, Global.machineId);
            if (resultSet != null) {
                float balance = 0f;
                while (resultSet.next()) {
                    balance += NumberUtil.FloatZero(resultSet.getInt("TTL_QTY"));
                }
                resultSet.close();

                record.setBalSmallestQty(balance);
                String qtyStr = MedicineUtil.getQtyInStr(record.getMedId(), balance);
                record.setBalQtyInString(qtyStr);
            }
        } catch (SQLException ex) {
            log.error("assignBalance : " + ex.getMessage());
        } catch (Exception ex) {
            log.error("assignBalance : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void calcSTKBalance(SaleDetailHis record) {
        float balance = record.getBalSmallestQty() - (record.getSaleSmallestQty() + record.getFocSmallestQty());
        //record.setBalSmallestQty(balance);
        String qtyStr = MedicineUtil.getQtyInStr(record.getMedId(), balance);
        record.setBalQtyInString(qtyStr);
    }

    public String getMedIdList() {
        String strMedIdList = null;
        int row = 0;

        if (listDetail != null) {
            for (SaleDetailHis record : listDetail) {
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

    public List<SaleDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.SaleDetailHis"
                + " WHERE medId.medId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void clear() {
        maxUniqueId = 0;
    }

    private boolean isOverSell(String medId, float sellQty, Integer locationId) {
        boolean status = false;
        float ttlQty = 0f;

        if (stockList != null) {
            List<Stock> listStock = stockList.getStockList(medId);
            if (listStock != null) {
                if (!listStock.isEmpty()) {
                    ttlQty = listStock.stream()
                            .filter(s -> (s.getLocationId().equals(locationId)))
                            .map(s -> s.getQtySmallest())
                            .reduce(ttlQty, (accumulator, _item) -> accumulator + _item);
                }
            }
        }

        if (sellQty > ttlQty) {
            status = true;
        }

        return status;
    }

    private float getTotalSaleQty(int index, String medId) {
        float ttlQty = 0f;
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                int locIndex = 0;
                for (SaleDetailHis sdh : listDetail) {
                    if (sdh.getMedId() != null) {
                        if (sdh.getMedId().getMedId() != null) {
                            if (sdh.getMedId().getMedId().equals(medId)) {
                                if (index != locIndex) {
                                    ttlQty += NumberUtil.FloatZero(sdh.getSaleSmallestQty());
                                }
                            }
                        }
                    }
                    locIndex++;
                }
            }
        }
        return ttlQty;
    }

    public Double getTotalAmount() {
        double ttlAmt = 0;

        for (SaleDetailHis sdh : listDetail) {
            if (sdh.getMedId() != null) {
                //String key = "";
                double discount = NumberUtil.NZero(sdh.getDiscount());
                float saleQty = NumberUtil.NZeroFloat(sdh.getQuantity());
                double price = NumberUtil.NZero(sdh.getPrice());

                /*key = sdh.getMedId().getMedId() + "-" + sdh.getUnitId().getItemUnitCode();

                sdh.setSaleSmallestQty(saleQty * medUp.getQtyInSmallest(key));
                key = "";

                if (sdh.getFocUnit() != null) {
                    key = sdh.getMedId().getMedId() + "-" + sdh.getFocUnit().getItemUnitCode();
                }
                float focQty = NumberUtil.NZeroFloat(sdh.getFocQty());
                sdh.setFocSmallestQty(focQty * medUp.getQtyInSmallest(key));*/
                String discType = Util1.getPropValue("system.app.sale.discount.calculation");
                switch (discType) {
                    case "Percent":
                        discount = ((saleQty * price) * (discount / 100));
                        break;
                    case "Value":
                        break;
                    case "Each":
                        discount = discount * saleQty;
                        break;
                }

                double amount = (saleQty * price) - discount;
                ttlAmt += amount;
                sdh.setAmount(amount);
            }
        }

        return ttlAmt;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public void addEMGPercent(float percent) {
        for (SaleDetailHis record : listDetail) {
            if (record.getMedId() != null) {
                if (record.getMedId().getMedId() != null) {
                    ChargeType ct = record.getChargeId();
                    if (ct.getChargeTypeId() != 2) {
                        //double price = NumberUtil.NZero(record.getPrice());
                        String key = record.getMedId().getMedId() + "-" + record.getUnitId().getItemUnitCode();
                        double price = medUp.getPrice(key, getCusType(), record.getQuantity());
                        double incValue = price * (percent / 100);
                        record.setPrice(price + incValue);
                        record.setAmount(NumberUtil.FloatZero(record.getQuantity())
                                * NumberUtil.NZero(record.getPrice()));
                    }
                }
            }
        }

        fireTableDataChanged();
    }
}
