/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.SaleDetailHis;
import com.cv.app.pharmacy.database.entity.SaleOutstand;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Eitar
 */
public class SaleOutstandTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleOutstandTableModel.class.getName());
    private List<SaleOutstand> listSaleOutstand = new ArrayList();
    private List<SaleDetailHis> listSaleDetail;
    private MedicineUP medUp;
    private final String[] columnNames = {"Option", "Code", "Description", "Exp-Date", "Outs-Qty"};

    public SaleOutstandTableModel(List<SaleDetailHis> listSaleDetail, MedicineUP medUp) {
        this.listSaleDetail = listSaleDetail;
        this.medUp = medUp;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 4;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listSaleOutstand == null) {
            return null;
        }

        if (listSaleOutstand.isEmpty()) {
            return null;
        }

        try {
            SaleOutstand record = listSaleOutstand.get(row);

            switch (column) {
                case 0: //Option
                    return record.getOutsOption();
                case 1: //Code
                    return record.getItemId().getMedId();
                case 2: //Description
                    return record.getItemId().getMedName();
                case 3: //Exp-Date
                    return DateUtil.toDateStr(record.getExpDate());
                case 4: //Outs-Qty
                    return record.getQtyStr();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listSaleOutstand == null) {
            return;
        }

        if (listSaleOutstand.isEmpty()) {
            return;
        }

        try {
            SaleOutstand record = listSaleOutstand.get(row);

            switch (column) {
                case 4: //Outs-Qty
                    if (record.getItemId() != null) {
                        String itemId = record.getItemId().getMedId();
                        float unitQty = NumberUtil.NZeroFloat(value);
                        String key;
                        float ttlQtySmall = 0;

                        if (medUp.getUnitList(itemId).size() > 1) {
                            int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
                            int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
                            UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                    medUp.getUnitList(itemId), Util1.getParent());

                            if (unitPopup.isSelected()) {
                                key = itemId + "-" + unitPopup.getSelUnit().getItemUnitCode();
                                ttlQtySmall = unitQty * medUp.getQtyInSmallest(key);
                            }
                        } else {
                            key = itemId + "-" + medUp.getUnitList(itemId).get(0).getItemUnitCode();
                            ttlQtySmall = unitQty * medUp.getQtyInSmallest(key);
                        }

                        record.setOutsQtySmall(ttlQtySmall);
                        Medicine item = record.getItemId();
                        item.setRelationGroupId(medUp.getRelation(itemId));
                        record.setQtyStr(MedicineUtil.getQtyInStr(item, ttlQtySmall));
                    }
                    break;
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listSaleOutstand == null) {
            return 0;
        }
        return listSaleOutstand.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SaleOutstand> getListSaleOutstand() {
        return listSaleOutstand;
    }

    public void setListSaleOutstand(List<SaleOutstand> listSaleOutstand) {
        if (listSaleOutstand == null || listSaleOutstand.isEmpty()) {
            for (SaleDetailHis sdh : listSaleDetail) {
                if (sdh.getMedId().getMedId() != null) {
                    if (NumberUtil.NZeroFloat(sdh.getQuantity()) > 0) {
                        this.listSaleOutstand.add(getSaleOutstand(sdh, "Sale"));
                    }

                    if (NumberUtil.NZeroFloat(sdh.getFocQty()) > 0) {
                        this.listSaleOutstand.add(getSaleOutstand(sdh, "Sale-Foc"));
                    }
                }
            }
        } else {
            this.listSaleOutstand = listSaleOutstand;

            HashMap<String, SaleOutstand> hm = new HashMap();

            for (SaleOutstand so : this.listSaleOutstand) {
                String key = so.getItemId().getMedId()
                        + DateUtil.toDateStr(so.getExpDate()) + so.getOutsOption();

                hm.put(key, so);
            }

            if (this.listSaleOutstand.size() != listSaleDetail.size() - 1) {
                for (SaleDetailHis sdh : listSaleDetail) {
                    if (sdh.getMedId().getMedId() != null) {
                        String key = sdh.getMedId().getMedId()
                                + DateUtil.toDateStr(sdh.getExpireDate());

                        if (NumberUtil.NZeroInt(sdh.getQuantity()) > 0) {
                            key = key + "Sale";
                            if (!hm.containsKey(key)) {
                                this.listSaleOutstand.add(getSaleOutstand(sdh, "Sale"));
                            }
                        }

                        if (NumberUtil.NZeroInt(sdh.getFocQty()) > 0) {
                            key = key + "Sale-Foc";
                            if (!hm.containsKey(key)) {
                                this.listSaleOutstand.add(getSaleOutstand(sdh, "Sale-Foc"));
                            }
                        }
                    }
                }
            }
        }

        fireTableDataChanged();
    }

    public void deleteSaleOutstand(int row) {
        if (listSaleOutstand != null) {
            if (!listSaleOutstand.isEmpty()) {
                listSaleOutstand.remove(row);
                fireTableRowsDeleted(0, listSaleOutstand.size() - 1);
            }
        }
    }

    private SaleOutstand getSaleOutstand(SaleDetailHis sdh, String option) {
        SaleOutstand outstand = new SaleOutstand();

        outstand.setItemId(sdh.getMedId());
        outstand.setExpDate(sdh.getExpireDate());
        outstand.setOutsOption(option);

        return outstand;
    }
}
