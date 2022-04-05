/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PriceChangeMedHis1;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PriceChangeMedHis;
import com.cv.app.pharmacy.database.entity.PriceChangeUnitHis;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
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
public class PChangeMedTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PChangeMedTableModel.class.getName());
    private List<PriceChangeMedHis> listDetail;
    private List<PriceChangeUnitHis> listDetailUnit;

    private final String[] columnNames = {"Code", "Description", "Pur Price",
        "Pur Unit", "Cost Price", "Cost Unit", "Market Price", "Market Unit",
        "Remark"};
    private JTable parent;
    private AbstractDataAccess dao;
    private MedicineUP medUp;
    private MedInfo medInfo;
    private PChangeUnitTableModel unitModel;

    public PChangeMedTableModel(List<PriceChangeMedHis> listDetail, AbstractDataAccess dao,
            MedicineUP medUp, MedInfo medInfo, List<PriceChangeUnitHis> listDetailUnit,
            PChangeUnitTableModel unitModel) {
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
        return !(column == 1 || column == 2 || column == 3
                || column == 4 || column == 5 || column == 7);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
            case 1: //Medicine Name
            case 8: //Remark
                return String.class;
            case 2: //Pur Price
            case 4: //Cost Price
            case 6: //Market Price
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
            PriceChangeMedHis record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (record.getMed() == null) {
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
                    return record.getPurchasePrice();
                case 3: //Pur Unit
                    return record.getPurchaseUnit();
                case 4: //Cost Price
                    return record.getCostPrice();
                case 5: //Cost Unit
                    return record.getCostUnit();
                case 6: //Market Price
                    return record.getMarketPrice();
                case 7: //Market Unit
                    return record.getMarketUnit();
                case 8: //Remark
                    return record.getRemark();
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

        PriceChangeMedHis record;
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
                    record.getMed().setMedName((String) value);
                    break;
                case 2: //Pur Price
                    record.setPurchasePrice((Double) value);
                    parent.setColumnSelectionInterval(3, 3);
                    //For unit popup
                    unitPopup = new UnitAutoCompleter(x, y,
                            medUp.getUnitList(medId), Util1.getParent());

                    if (unitPopup.isSelected()) {
                        record.setPurchaseUnit(unitPopup.getSelUnit());
                        parent.setColumnSelectionInterval(4, 4);
                    }
                    break;
                case 3: //Pur Unit
                    record.setPurchaseUnit((ItemUnit) value);
                    break;
                case 4: //Cost Price
                    record.setCostPrice((Double) value);
                    parent.setColumnSelectionInterval(5, 5);
                    //For unit popup
                    unitPopup = new UnitAutoCompleter(x, y,
                            medUp.getUnitList(medId), Util1.getParent());

                    if (unitPopup.isSelected()) {
                        record.setCostUnit(unitPopup.getSelUnit());
                        parent.setColumnSelectionInterval(6, 6);
                    }
                    break;
                case 5: //Cost Unit
                    record.setCostUnit((ItemUnit) value);
                    break;
                case 6: //Market Price
                    record.setMarketPrice(Double.valueOf(value.toString()));
                    parent.setColumnSelectionInterval(7, 7);
                    //For unit popup
                    unitPopup = new UnitAutoCompleter(x, y,
                            medUp.getUnitList(medId), Util1.getParent());

                    if (unitPopup.isSelected()) {
                        record.setMarketUnit(unitPopup.getSelUnit());
                        parent.setColumnSelectionInterval(8, 8);
                    }
                    break;
                case 7: //Market Unit
                    record.setMarketUnit((ItemUnit) value);
                    break;
                case 8: //Remark
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

        PriceChangeMedHis record = listDetail.get(listDetail.size() - 1);
        return record.getMed().getMedId() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            PriceChangeMedHis record = new PriceChangeMedHis();

            record.setMed(new Medicine());
            listDetail.add(record);

            fireTableRowsInserted(listDetail.size(), listDetail.size());
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
            if (med.getRelationGroupId().size() > 0) {
                med.setRelationGroupId(med.getRelationGroupId());
            }
        } catch (Exception ex) {
            log.error("setMed1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }

        PriceChangeMedHis record = null;

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
        
        HashMap<String, ItemUnit> hmUnit = MedicineUtil.getUnitHash(dao.findAll("ItemUnit"));
        double smallestCost = 0;
        if (record != null) {
            try {
                String strLatestPur = "select vp.med_id, pur_unit,((ifnull(pur_unit_cost,0)*ifnull(pur_qty,0))/ifnull(pur_smallest_qty,1)) smallest_cost,"
                        + "pur_price, pur_unit_cost from v_purchase vp, (select med_id, "
                        + "max(pur_detail_id) purd_id from v_purchase where deleted = false "
                        + "and med_id = '" + med.getMedId() + "' group by med_id) ma where vp.med_id = ma.med_id "
                        + "and vp.pur_detail_id = ma.purd_id and deleted = false and vp.med_id = '" + med.getMedId() + "'";
                ResultSet rsLatestPur = dao.execSQL(strLatestPur);

                while (rsLatestPur.next()) {
                    record.setCostPrice(rsLatestPur.getDouble("pur_unit_cost"));
                    record.setPurchasePrice(rsLatestPur.getDouble("pur_price"));
                    smallestCost = rsLatestPur.getDouble("smallest_cost");
                    
                    String strUnit = Util1.getString(rsLatestPur.getString("pur_unit"), "-");
                    if (hmUnit.containsKey(strUnit)) {
                        record.setCostUnit(hmUnit.get(strUnit));
                        record.setPurchaseUnit(hmUnit.get(strUnit));
                    }
                }
            } catch (Exception ex) {
                log.error("setMed3 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            } finally {
                dao.closeStatment();
            }
        }
        
        if (record != null) {
            if (record.getListUnit() == null) {
                List<PriceChangeUnitHis> listUnit = new ArrayList();
                
                List<RelationGroup> listRelation = med.getRelationGroupId();
                for (RelationGroup rg : listRelation) {
                    PriceChangeUnitHis pcuh = new PriceChangeUnitHis(rg.getUnitId(), rg.getSalePrice(),
                            rg.getSalePriceA(), rg.getSalePriceB(), rg.getSalePriceC(),
                            rg.getSalePriceD());
                    pcuh.setCostPrice(smallestCost * NumberUtil.FloatZero(rg.getSmallestQty()));
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
                String strLatestMarket = "select med_id, max(change_date) last_date, "
                        + "market_price, market_unit, remark_med "
                        + "from v_price_change_med where med_id = '" + med.getMedId()
                        + "' group by med_id";
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

    public void setListDetail(List<PriceChangeMedHis> listDetail) {
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
}
