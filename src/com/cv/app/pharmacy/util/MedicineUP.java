/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.util;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.helper.LatestPurPrice;
import com.cv.app.pharmacy.ui.util.MessageDialog;
import com.cv.app.ui.common.MedPriceList;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class MedicineUP {

    static Logger log = Logger.getLogger(MedicineUP.class.getName());
    private HashMap<String, List<ItemUnit>> hasUnit;
    private HashMap<String, List<MedPriceList>> hasPrice;
    private HashMap<String, Float> hasQtyInSmallest;
    private HashMap<String, List<RelationGroup>> hasRelation;
    private HashMap<String, LatestPurPrice> hasLPP;
    private AbstractDataAccess dao;

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public MedicineUP(AbstractDataAccess dao) {
        hasUnit = new HashMap();
        hasPrice = new HashMap();
        hasQtyInSmallest = new HashMap();
        hasRelation = new HashMap();
        hasLPP = new HashMap();
        this.dao = dao;
    }

    public void add(Medicine med) {
        if (!hasUnit.containsKey(med.getMedId())) {
            med = (Medicine) dao.find(Medicine.class, med.getMedId());
            if (med.getRelationGroupId() != null) {
                if (med.getRelationGroupId().size() > 0) {
                    med.setRelationGroupId(med.getRelationGroupId());
                }
                List<ItemUnit> listItemUnit = new ArrayList();
                hasRelation.put(med.getMedId(), med.getRelationGroupId());

                for (int i = 0; i < med.getRelationGroupId().size(); i++) {
                    List<MedPriceList> listPrice = new ArrayList();
                    RelationGroup relG = med.getRelationGroupId().get(i);

                    listItemUnit.add(relG.getUnitId());

                    listPrice.add(new MedPriceList("N", relG.getSalePrice()));
                    listPrice.add(new MedPriceList("A", relG.getSalePriceA()));
                    listPrice.add(new MedPriceList("B", relG.getSalePriceB()));
                    listPrice.add(new MedPriceList("C", relG.getSalePriceC()));
                    listPrice.add(new MedPriceList("D", relG.getSalePriceD()));
                    listPrice.add(new MedPriceList("E", relG.getStdCost()));

                    String key = med.getMedId() + "-" + relG.getUnitId().getItemUnitCode();

                    hasPrice.put(key, listPrice);
                    hasQtyInSmallest.put(key, relG.getSmallestQty());
                }

                hasUnit.put(med.getMedId(), listItemUnit);

                //For condition of salePrice between purPrice and purPrice + (20%)
                String propPriceStatus = Util1.getPropValue("system.sale.priceRange");

                if (propPriceStatus.toUpperCase().equals("Y")) {
                    try {
                        ResultSet rs = dao.getPro("get_latest_pur_price", med.getMedId());

                        if (rs != null) {
                            if (rs.next()) {
                                LatestPurPrice lpp = new LatestPurPrice();
                                lpp.setMedId(rs.getString("med_id"));
                                lpp.setPurPrice(rs.getDouble("pur_price"));
                                lpp.setUnit(rs.getString("pur_unit"));

                                hasLPP.put(med.getMedId(), lpp);
                            }
                            rs.close();
                        }
                    } catch (Exception ex) {
                        log.error("add : " + ex);
                    } finally {
                        //dao.close();
                    }
                }
            }
        }
    }

    public List<MedPriceList> getPriceList(String key) {
        if (hasPrice.containsKey(key)) {
            return hasPrice.get(key);
        } else {
            return null;
        }
    }

    public List<ItemUnit> getUnitList(String key) {
        if (hasUnit.containsKey(key)) {
            return hasUnit.get(key);
        } else {
            return null;
        }
    }

    public Double getPrice(String key, String priceType, float qty) {
        Double price = new Double(0);
        List<MedPriceList> priceList = getPriceList(key);
        int index = -1;

        /*if(qty > 0 && priceType.equals("N")){
            String strQty = Util1.getPropValue("system.sale.normal.qty");
            String strPriceType = Util1.getPropValue("system.sale.normal.qty.pricetype");

            if(!strQty.isEmpty()){
                int tmpQty = NumberUtil.NZeroInt(strQty);

                if(qty >= tmpQty && !strPriceType.isEmpty()){
                    priceType = strPriceType;
                }
            }
        }*/
        switch (priceType) {
            case "N":
                index = 0;
                break;
            case "A":
                index = 1;
                break;
            case "B":
                index = 2;
                break;
            case "C":
                index = 3;
                break;
            case "D":
                index = 4;
                break;
            case "E":
                index = 5;
                break;
        }

        try {
            if (index != -1) {
                price = priceList.get(index).getPrice();
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        return price;
    }

    public Float getQtyInSmallest(String key) {
        float value = new Float(0);

        try {
            if (!key.equals("")) {
                value = (float) hasQtyInSmallest.get(key);
            }
        } catch (Exception ex) {

        }

        return NumberUtil.NZeroFloat(value);
    }

    public void clear() {
        hasUnit.clear();
        hasPrice.clear();
        hasQtyInSmallest.clear();
    }

    public List<RelationGroup> getRelation(String key) {
        if (hasRelation.containsKey(key)) {
            return hasRelation.get(key);
        } else {
            return null;
        }
    }

    public void loadRecord(String medId, AbstractDataAccess dao) {
        if (!hasUnit.containsKey(medId)) {
            try {
                dao.open();
                Medicine med = (Medicine) dao.find(Medicine.class, medId);

                if (med != null) {
                    List<RelationGroup> listRel = med.getRelationGroupId();
                    //med.setRelationGroupId(listRel);
                    if (listRel.size() > 0) {
                        add(med);
                    }

                    System.out.println("Size : " + listRel.size());
                }
            } catch (Exception ex) {
                log.error(ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    public ItemUnit getSmallestUnit(String medId) {
        ItemUnit smallestUnit = new ItemUnit();
        float smallestQty = 0;
        List<RelationGroup> listRG = getRelation(medId);

        for (RelationGroup rg : listRG) {
            if (smallestQty == 0) {
                smallestQty = rg.getSmallestQty();
                smallestUnit = rg.getUnitId();
            } else {
                if (smallestQty > rg.getSmallestQty()) {
                    smallestQty = rg.getSmallestQty();
                    smallestUnit = rg.getUnitId();
                }
            }
        }

        return smallestUnit;
    }

    public ItemUnit getGratestUnit(String medId) {
        ItemUnit gratestUnit = null;
        float gratestQty = 0;
        List<RelationGroup> listRG = getRelation(medId);

        for (RelationGroup rg : listRG) {
            if (gratestQty == 0) {
                gratestQty = rg.getSmallestQty();
                gratestUnit = rg.getUnitId();
            } else {
                if (gratestQty < rg.getSmallestQty()) {
                    gratestQty = rg.getSmallestQty();
                    gratestUnit = rg.getUnitId();
                }
            }
        }

        return gratestUnit;
    }

    public String validePrice(String medId, String unit, double price) {
        String propPriceStatus = Util1.getPropValue("system.sale.priceRange");
        String status = "-";

        if (propPriceStatus.toUpperCase().equals("Y")) {
            LatestPurPrice lpp = hasLPP.get(medId);
            String strPercent = Util1.getPropValue("system.sale.purPricePc");
            double purPricePc = NumberUtil.NZero(strPercent);

            if (lpp != null) {
                String saleKey = medId + "-" + unit;
                String purKey = medId + "-" + lpp.getUnit();
                float saleQtySmall = getQtyInSmallest(saleKey);
                double saleSmallPrice = price / saleQtySmall;
                float purQtySmall = getQtyInSmallest(purKey);
                double purSmallPrice = 0;
                if (purQtySmall != 0) {
                    purSmallPrice = lpp.getPurPrice() / purQtySmall;
                }
                double tmpPrice = purSmallPrice + (purSmallPrice * (purPricePc / 100));

                if (saleSmallPrice > tmpPrice) {
                    /*JOptionPane.showMessageDialog(Util1.getParent(), "Sale price is more then " + strPercent + "%.",
                                    "Price Level", JOptionPane.ERROR_MESSAGE);*/
                    MessageDialog msgd = new MessageDialog("Sale Price > " + strPercent + "% of Purchase Price");
                    status = "G";
                } else if (saleSmallPrice < purSmallPrice) {
                    /*JOptionPane.showMessageDialog(Util1.getParent(), "Sale price is less then purchase price.",
                                    "Price Level", JOptionPane.ERROR_MESSAGE);*/
                    MessageDialog msgd = new MessageDialog("Sale Price < Purchase Price");
                    status = "L";
                }
            }
        }

        return status;
    }

    public ItemUnit getUnit(String medId, String unitId) {
        List<ItemUnit> listIU = hasUnit.get(medId);
        ItemUnit iu = null;

        for (ItemUnit tmpIU : listIU) {
            if (tmpIU.getItemUnitCode().equals(unitId)) {
                iu = tmpIU;
                return iu;
            }
        }
        return iu;
    }
}
