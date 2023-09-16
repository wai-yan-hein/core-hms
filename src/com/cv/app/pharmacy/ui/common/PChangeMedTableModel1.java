/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PriceChangeMedHis1;
import com.cv.app.pharmacy.database.entity.PriceChangeUnitHis1;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.StockOpeningDetailHis;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PChangeMedTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(PChangeMedTableModel1.class.getName());
    private List<PriceChangeMedHis1> listDetail;
    private final List<PriceChangeUnitHis1> listDetailUnit;
    private final String[] columnNames = {"Code", "Description", "Pur Price",
        "Cost Price", "Market Price", "Remark", "Ttl-Sale", "SFOC", "S-Amt",
        "Ttl-Pur", "PFOC", "P-Amt", "Balance"};
    private JTable parent;
    private final AbstractDataAccess dao;
    private final MedicineUP medUp;
    private final MedInfo medInfo;
    private final PChangeUnitTableModel1 unitModel;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");

    public PChangeMedTableModel1(List<PriceChangeMedHis1> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo, List<PriceChangeUnitHis1> listDetailUnit,
            PChangeUnitTableModel1 unitModel) {
        this.listDetail = listDetail;
        this.dao = dao;
        this.medUp = medUp;
        this.medInfo = medInfo;
        this.listDetailUnit = listDetailUnit;
        this.unitModel = unitModel;
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
        return column == 0 || column == 4 || column == 5;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Medicine Name
                return String.class;
            case 2: //Pur Price
                return String.class;
            case 3: //Cost Price
                return String.class;
            case 4: //Market Price
                return String.class;
            case 5: //Remark
                return String.class;
            case 6: //Ttl-Sale
                return String.class;
            case 7: //SFOC
                return String.class;
            case 8: //S-Amt
                return Double.class;
            case 9: //Ttl-Pur
                return String.class;
            case 10: //PFOC
                return String.class;
            case 11: //P-Amt
                return Double.class;
            case 12: //Balance
                return String.class;
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
            PriceChangeMedHis1 record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getMed() == null) {
                            return null;
                        } else {
                            return record.getMed().getShortName();
                        }
                    } else if (record.getMed() == null) {
                        return null;
                    } else {
                        return record.getMed().getMedId();
                    }
                case 1: //Medicine Name
                    if (record.getMed() == null) {
                        return null;
                    } else {
                        return record.getMed().getMedName();
                    }
                case 2: //Pur Price
                    String strPurPrice = String.format("%,.2f", NumberUtil.NZero(record.getPurchasePrice()));
                    String strPurUnit = "";
                    if (record.getPurchaseUnit() != null) {
                        strPurUnit = record.getPurchaseUnit().getItemUnitCode();
                    }
                    return strPurPrice + " " + strPurUnit;
                case 3: //Cost Price
                    String strCostPrice = String.format("%,.2f", NumberUtil.NZero(record.getCostPrice()));
                    String strCostUnit = "";
                    if (record.getCostUnit() != null) {
                        strCostUnit = record.getCostUnit().getItemUnitCode();
                    }
                    return strCostPrice + " " + strCostUnit;
                case 4: //Market Price
                    String strMarketPrice = String.format("%,.2f", NumberUtil.NZero(record.getMarketPrice()));
                    String strMarketUnit = "";
                    if (record.getMarketUnit() != null) {
                        strMarketUnit = record.getMarketUnit().getItemUnitCode();
                    }
                    return strMarketPrice + " " + strMarketUnit;
                case 5: //Remark
                    return record.getRemark();
                case 6: //Ttl-Sale
                    return record.getTtlSaleStr();
                case 7: //SFOC
                    return record.getSaleFocStr();
                case 8: //S-Amt
                    return record.getSaleAmt();
                case 9: //Ttl-Pur
                    return record.getTtlPurStr();
                case 10: //PFOC
                    return record.getPurFocStr();
                case 11: //P-Amt
                    return record.getPurAmt();
                case 12: //Balance
                    return record.getStkBalanceStr();
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

        PriceChangeMedHis1 record;
        int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
        int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
        UnitAutoCompleter unitPopup;

        try {
            String medId = listDetail.get(parent.getSelectedRow()).getMed().getMedId();
            record = listDetail.get(row);
            switch (column) {
                case 0: //Code
                    if (!value.toString().isEmpty()) {
                        record.getMed().setMedId((String) value);
                        dao.open();
                        medInfo.getMedInfo((String) value);
                        dao.close();
                        /*record.setPurchasePrice(null);
                         record.setPurchaseUnit(null);
                         record.setCostPrice(null);
                         record.setCostUnit(null);
                         record.setMarketPrice(null);
                         record.setMarketUnit(null);
                         record.setRemark(null);*/
                    }
                    break;
                case 1: //Medicine Name
                    //record.getMed().setMedName((String) value);
                    break;
                case 2: //Pur Price
                    break;
                case 3: //Cost Price
                    break;
                case 4: //Market Price
                    record.setMarketPrice(Double.valueOf(value.toString()));
                    //parent.setColumnSelectionInterval(7, 7);
                    //For unit popup
                    unitPopup = new UnitAutoCompleter(x, y,
                            medUp.getUnitList(medId), Util1.getParent());

                    if (unitPopup.isSelected()) {
                        record.setMarketUnit(unitPopup.getSelUnit());
                        parent.setColumnSelectionInterval(5, 5);
                    }
                    break;
                case 5: //Remark
                    record.setRemark((String) value);
                    parent.setRowSelectionInterval(row + 1, row + 1);
                    parent.setColumnSelectionInterval(0, 0);
                    break;
                default:
                    System.out.println("invalid index");
            }

            //calculateAmount(row);
            //fireTableCellUpdated(row, 9);
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

        PriceChangeMedHis1 record = listDetail.get(listDetail.size() - 1);
        return record.getMed().getMedId() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            PriceChangeMedHis1 record = new PriceChangeMedHis1();

            record.setMed(new Medicine());
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

        try {
            med = (Medicine) dao.find(Medicine.class, med.getMedId());
            if (!med.getRelationGroupId().isEmpty()) {
                med.setRelationGroupId(med.getRelationGroupId());
            }
        } catch (Exception ex) {
            log.error("setMed1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }

        PriceChangeMedHis1 record = null;

        try {
            record = listDetail.get(pos);
        } catch (Exception ex) {
            log.error("setMed2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        if (record != null) {
            record.setMed(med);
            record.setPurchasePrice(null);
            record.setPurchaseUnit(null);
            record.setCostPrice(null);
            record.setCostUnit(null);
            record.setMarketPrice(null);
            record.setMarketUnit(null);
            record.setRemark(null);
        }

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableCellUpdated(pos, 0);
        parent.setColumnSelectionInterval(2, 2);

        HashMap<String, ItemUnit> hmUnit = null;
        try {
            hmUnit = MedicineUtil.getUnitHash(dao.findAll("ItemUnit"));
        } catch (Exception ex) {
            log.error("setMed5 : " + ex.getMessage());
        } finally {
            dao.close();
        }
        double smallestCost = 0;
        if (record != null) {
            try {
                String strLatestPur = "select vp.med_id, pur_unit,((ifnull(pur_unit_cost,0)*ifnull(pur_qty,0))/ifnull(pur_smallest_qty,1)) smallest_cost,"
                        + "pur_price, pur_unit_cost from v_purchase vp, (select med_id, "
                        + "max(pur_date) pur_date from v_purchase where deleted = false "
                        + "and med_id = '" + med.getMedId() + "' group by med_id) ma where vp.med_id = ma.med_id "
                        + "and vp.pur_date = ma.pur_date and deleted = false and vp.med_id = '" + med.getMedId() + "'";
                ResultSet rsLatestPur = dao.execSQL(strLatestPur);

                if (rsLatestPur.next()) {
                    //Double costPrice = NumberUtil.roundTo(rsLatestPur.getDouble("pur_unit_cost"), 0);
                    Double costPrice = rsLatestPur.getDouble("pur_unit_cost");
                    record.setCostPrice(costPrice);
                    record.setPurchasePrice(rsLatestPur.getDouble("pur_price"));
                    //smallestCost = NumberUtil.roundToF(rsLatestPur.getDouble("smallest_cost"), 0);
                    smallestCost = rsLatestPur.getDouble("smallest_cost");
                    if (Double.isNaN(smallestCost)) {
                        smallestCost = 0;
                    }
                    String strUnit = Util1.getString(rsLatestPur.getString("pur_unit"), "-");
                    if (hmUnit.containsKey(strUnit)) {
                        record.setCostUnit(hmUnit.get(strUnit));
                        record.setPurchaseUnit(hmUnit.get(strUnit));
                    }
                } else {
                    List<StockOpeningDetailHis> sodhList = dao.findAllHSQL(
                            "select o from StockOpeningDetailHis o where o.med.medId = '"
                            + med.getMedId() + "'");
                    if (sodhList != null) {
                        if (!sodhList.isEmpty()) {
                            float costPrice = NumberUtil.FloatZero(sodhList.get(0).getCostPrice());
                            float smallQty = sodhList.get(0).getSmallestQty();
                            float qty = sodhList.get(0).getQty();
                            smallestCost = costPrice
                                    / (smallQty / qty);
                            if (Double.isNaN(smallestCost)) {
                                smallestCost = 0;
                            }
                            record.setCostPrice(sodhList.get(0).getCostPrice());
                            record.setCostUnit(sodhList.get(0).getUnit());
                        }
                    }
                }
            } catch (SQLException ex) {
                log.error("setMed3 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            } catch (Exception ex) {
                log.error("setMed4 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            } finally {
                dao.closeStatment();
            }
        }

        if (record != null) {
            /*if (NumberUtil.NZero(smallestCost) == 0) {
                List<StockOpeningDetailHis> sodhList = dao.findAllHSQL(
                        "select o from StockOpeningDetailHis o where o.med.medId = '"
                        + record.getMed().getMedId() + "'");
                if (sodhList != null) {
                    if (!sodhList.isEmpty()) {
                        smallestCost = NumberUtil.roundToF(sodhList.get(0).getCostPrice(), 0);
                    }
                }
            }*/

            if (record.getListUnit() == null) {
                List<PriceChangeUnitHis1> listUnit = new ArrayList();

                List<RelationGroup> listRelation = med.getRelationGroupId();
                for (RelationGroup rg : listRelation) {
                    PriceChangeUnitHis1 pcuh = new PriceChangeUnitHis1(rg.getUnitId(), rg.getSalePrice(),
                            rg.getSalePriceA(), rg.getSalePriceB(), rg.getSalePriceC(),
                            rg.getSalePriceD(), rg.getStdCost());
                    double costPrice = smallestCost * NumberUtil.FloatZero(rg.getSmallestQty());
                    pcuh.setCostPrice(costPrice);
                    listUnit.add(pcuh);
                }

                listDetailUnit.removeAll(listDetailUnit);
                listDetailUnit.addAll(listUnit);
                listDetail.get(pos).setListUnit(listUnit);

                unitModel.dataChange();
            }
        }

        if (record != null) {
            try {
                /*String strLatestMarket = "select med_id, max(change_date) last_date, "
                        + "market_price, market_unit, remark_med "
                        + "from v_price_change_med where med_id = '" + med.getMedId()
                        + "' group by med_id";*/
                String strLatestMarket = "select market_price, market_unit, remark_med\n"
                        + "from v_price_change_med\n"
                        + "where med_id = '" + med.getMedId() + "'\n"
                        + "order by created_date desc\n"
                        + "limit 1";
                ResultSet rsLatestMarket = dao.execSQL(strLatestMarket);

                while (rsLatestMarket.next()) {
                    record.setMarketPrice(rsLatestMarket.getDouble("market_price"));
                    record.setRemark(rsLatestMarket.getString("remark_med"));

                    String strUnit = Util1.getString(rsLatestMarket.getString("market_unit"), "-");
                    if (hmUnit.containsKey(strUnit)) {
                        record.setMarketUnit(hmUnit.get(strUnit));
                    }
                }
            } catch (Exception ex) {
                log.error("setMed4 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            } finally {
                dao.closeStatment();
            }
        }
    }

    public void delete(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                listDetail.remove(row);
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }

                fireTableRowsDeleted(row, row);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        /*PriceChangeMedHis pdh = listDetail.get(row);
         String key = "";
         double amount = 0;
        
         if(pdh.getUnitId() != null)
         key = pdh.getMedId().getMedId() + "-" + pdh.getUnitId().getItemUnitCode();
        
         pdh.setPurSmallestQty(NumberUtil.NZeroInt(pdh.getQuantity()) * 
         medUp.getQtyInSmallest(key));
        
         double tmpAmount = NumberUtil.NZero(pdh.getQuantity()) * NumberUtil.NZero(pdh.getPrice());
         double percent1 = tmpAmount * (NumberUtil.NZero(pdh.getDiscount1())/100);
         double percent2 = (tmpAmount - percent1) * (NumberUtil.NZero(pdh.getDiscount2())/100);
        
         System.out.println("tmpAmount : " + tmpAmount);
         System.out.println("percent1 : " + percent1);
         System.out.println("percent2 : " + percent2);
        
         amount = tmpAmount - (percent1 + percent2);
         pdh.setAmount(amount);
        
         if(NumberUtil.NZero(pdh.getFocQty()) > 0){
         double smallestCost = amount / (pdh.getQuantity() * medUp.getQtyInSmallest(key));
         String focKey = pdh.getMedId().getMedId() + "-" + pdh.getFocUnitId().getItemUnitCode();
         pdh.setUnitCost(amount - (smallestCost * (pdh.getFocQty() * medUp.getQtyInSmallest(focKey))));
         }else{
         pdh.setUnitCost(amount);
         }*/
    }// </editor-fold>

    public boolean isValidEntry() {
        boolean status = true;
        int row = 0;

        /*for(PriceChangeMedHis record : listDetail){
         if(row < listDetail.size()-1){
         if(record.getMed().getMedId() != null){
         if(NumberUtil.NZero(record.getQuantity()) <= 0){
         JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.", 
         "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
         status = false;
         }else if(NumberUtil.NZero(record.getDiscount1()) < 0){
         JOptionPane.showMessageDialog(Util1.getParent(), "Discount1 must be positive value.", 
         "Minus qty.", JOptionPane.ERROR_MESSAGE);
         status = false;
         }else if(NumberUtil.NZero(record.getDiscount2()) < 0){
         JOptionPane.showMessageDialog(Util1.getParent(), "Discount2 must be positive value.", 
         "Minus qty.", JOptionPane.ERROR_MESSAGE);
         status = false;
         }else if(NumberUtil.NZero(record.getPrice()) < 0){
         JOptionPane.showMessageDialog(Util1.getParent(), "Price must be positive value.", 
         "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
         status = false;
         }else{
         record.setUniqueId(row + 1);
         }

         row++;
         }
         }
         }*/
        if (row == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No purchase record.",
                    "No data.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        parent.setRowSelectionInterval(row, row);

        return status;
    }

    public void setListDetail(List<PriceChangeMedHis1> listDetail) {
        this.listDetail = listDetail;

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        try {
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("setListDetail : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public List<PriceChangeMedHis1> getListDetail(){
        return listDetail;
    }
    
    public int getLastIndex() {
        return listDetail.size() - 1;
    }

    public void fireDataChanged() {
        fireTableDataChanged();
    }

    public void clear() {
        listDetail.clear();

    }
}
