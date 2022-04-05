/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.util;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author WSwe
 */
public class MedicineUtil {

    public static String getQtyInStr(Medicine med, float qtyInSmallest) {
        List<RelationGroup> listRelG = med.getRelationGroupId();

        return getQtyInStr(listRelG, qtyInSmallest);
    }
    
    public static String getQtyInStr(List<RelationGroup> listRelG, float qtyInSmallest){
        boolean isMinus = false;
        String qtyStr = "";

        if (qtyInSmallest < 0) {
            isMinus = true;
            qtyInSmallest = qtyInSmallest * -1;
        }

        for (int i = 0; i < listRelG.size(); i++) {
            RelationGroup relG = listRelG.get(i);
            float unitQty;
            long lUnitQty;
            String unit = relG.getUnitId().getItemUnitCode();

            if (qtyInSmallest >= relG.getSmallestQty() && qtyInSmallest != 0) {
                unitQty = qtyInSmallest / relG.getSmallestQty();
                lUnitQty = (long)(qtyInSmallest / relG.getSmallestQty());
                
                if(listRelG.size() > 1){
                    unitQty = lUnitQty;
                }
                qtyInSmallest = qtyInSmallest - (unitQty * relG.getSmallestQty());

                if (qtyInSmallest == 0) {
                    i = listRelG.size();
                }

                DecimalFormat df = new DecimalFormat("###.#");
                String strUnitQty = "0";
                try{
                    strUnitQty = df.format(unitQty);
                }catch(Exception ex){
                    
                }
                if (qtyStr.length() > 0) {
                    qtyStr = qtyStr + "," + strUnitQty + unit;
                } else {
                    qtyStr = strUnitQty + unit;
                }
            }
        }

        if (isMinus) {
            qtyStr = "-" + qtyStr;
        }

        return qtyStr;
    }
    
    public static HashMap getUnitHash(List<ItemUnit> listUnit){
        HashMap<String, ItemUnit> hmUnit = new HashMap();
        
        for(ItemUnit iu : listUnit){
            hmUnit.put(iu.getItemUnitCode(), iu);
        }
        
        return hmUnit;
    }
}
