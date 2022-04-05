/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PurDetailHis;
import com.cv.app.pharmacy.database.entity.PurchaseOutstand;
import com.cv.app.pharmacy.database.entity.RelationGroup;
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
public class PurchaseOutstandTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurchaseOutstandTableModel.class.getName());
    private List<PurchaseOutstand> listPurOutstand = new ArrayList();
    private List<PurDetailHis> listPurDetail;
    private MedicineUP medUp;
    private final String[] columnNames = {"Option", "Code", "Description", "Exp-Date", "Outs-Qty"};

    public PurchaseOutstandTableModel(List<PurDetailHis> listPurDetail, MedicineUP medUp) {
        this.listPurDetail = listPurDetail;
        this.medUp = medUp;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 4) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPurOutstand == null) {
            return null;
        }

        if (listPurOutstand.isEmpty()) {
            return null;
        }
        try {
            PurchaseOutstand record = listPurOutstand.get(row);

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
        if (listPurOutstand == null) {
            return;
        }

        if (listPurOutstand.isEmpty()) {
            return;
        }

        try {
            PurchaseOutstand record = listPurOutstand.get(row);

            switch (column) {
                case 4: //Outs-Qty
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
                    List<RelationGroup> listRelation = medUp.getRelation(record.getItemId().getMedId());
                    record.setQtyStr(MedicineUtil.getQtyInStr(listRelation, ttlQtySmall));

                    break;
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if(listPurOutstand == null){
            return 0;
        }
        return listPurOutstand.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PurchaseOutstand> getListPurOutstand() {
        return listPurOutstand;
    }

    public void setListPurOutstand(List<PurchaseOutstand> listPurOutstand) {
        if (listPurOutstand == null || listPurOutstand.isEmpty()) {
            for (PurDetailHis pdh : listPurDetail) {
                if (pdh.getMedId().getMedId() != null) {
                    if (NumberUtil.NZeroFloat(pdh.getPurSmallestQty()) > 0) {
                        this.listPurOutstand.add(getPurOutstand(pdh));
                    }

                    if (NumberUtil.NZeroFloat(pdh.getFocSmallestQty()) > 0) {
                        this.listPurOutstand.add(getPurOutstandFoc(pdh));
                    }
                }
            }
        } else {
            this.listPurOutstand = listPurOutstand;

            HashMap<String, PurchaseOutstand> hm = new HashMap();

            for (PurchaseOutstand so : this.listPurOutstand) {
                String key = so.getItemId().getMedId()
                        + DateUtil.toDateStr(so.getExpDate()) + so.getOutsOption();

                hm.put(key, so);
            }

            //if (this.listPurOutstand.size() != listPurDetail.size() - 1) {
            for (PurDetailHis pdh : listPurDetail) {
                if (pdh.getMedId().getMedId() != null) {
                    String key = pdh.getMedId().getMedId()
                            + DateUtil.toDateStr(pdh.getExpireDate());

                    if (pdh.getChargeId().getChargeTypeId() == 2) {
                        key = key + "Purchase-Foc";
                    } else {
                        key = key + "Purchase";
                    }

                    if (!hm.containsKey(key)
                            && NumberUtil.NZeroInt(pdh.getPurSmallestQty()) > 0) {
                        this.listPurOutstand.add(getPurOutstand(pdh));
                    }

                    if (NumberUtil.NZeroInt(pdh.getFocSmallestQty()) > 0) {
                        String key1 = pdh.getMedId().getMedId()
                                + DateUtil.toDateStr(pdh.getExpireDate())
                                + "FOC";

                        if (!hm.containsKey(key1)
                                && NumberUtil.NZeroInt(pdh.getFocSmallestQty()) > 0) {
                            this.listPurOutstand.add(getPurOutstandFoc(pdh));
                        }
                    }
                }
            }
            //}
        }

        fireTableDataChanged();
    }

    public void deletePurOutstand(int row) {
        if(listPurOutstand == null){
            return;
        }
        
        if(listPurOutstand.isEmpty()){
            return;
        }
        listPurOutstand.remove(row);
        fireTableRowsDeleted(0, listPurOutstand.size() - 1);
    }

    private PurchaseOutstand getPurOutstand(PurDetailHis pdh) {
        PurchaseOutstand outstand = new PurchaseOutstand();

        outstand.setItemId(pdh.getMedId());
        outstand.setExpDate(pdh.getExpireDate());

        if (pdh.getChargeId().getChargeTypeId() == 2) {
            outstand.setOutsOption("Purchase-Foc");
        } else {
            outstand.setOutsOption("Purchase");
        }

        return outstand;
    }

    private PurchaseOutstand getPurOutstandFoc(PurDetailHis pdh) {
        PurchaseOutstand outstand = new PurchaseOutstand();

        outstand.setItemId(pdh.getMedId());
        outstand.setExpDate(pdh.getExpireDate());
        outstand.setOutsOption("FOC");

        return outstand;
    }
}
