/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.entity.PurHis;
import com.cv.app.pharmacy.database.view.VPurPromo;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PurPromoSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurPromoSearchTableModel.class.getName());
    private List<VPurPromo> listPurHis = new ArrayList();
    private final String[] columnNames = {"Pur Date", "Cus Name", "Vou No.",
    "Code", "Name", "Description", "Start-Date", "End-Date", "Give-Per", "Get-Per", "Get-complete"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 10) {
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
            case 6:
            case 7:
                return Date.class;
            case 1: //cus Name
                return String.class;
            case 2: //vou no
                return String.class;
            case 3: //code
                return String.class;
            case 4: //med name
                return String.class;
            case 5: //Des
                return String.class;
            case 8:
            case 9://s DAte
                return Float.class;
            case 10:
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPurHis == null) {
            return null;
        }
        if (listPurHis.isEmpty()) {
            return null;
        }

        try {
            VPurPromo ph = listPurHis.get(row);

            switch (column) {
                case 0: //Date
                    return ph.getPurDate();
                case 1: //cus name
                    return ph.getCusName();
                case 2: //vou no
                    return ph.getPurInvId();
                case 3: //med code
                    return ph.getMedId();
                case 4: //med name
                    return ph.getMedName();
                case 5: //Des
                    return ph.getPromoDesp();
                case 6: //s date
                    return ph.getPromoStartDate();
                case 7: //e date
                    return ph.getPromoEndDate();
                case 8: //give per
                    return ph.getPromoGivePercent();
                case 9: //get per
                    return ph.getPromoGetPercent();
                case 10: //complete
                    return ph.isPromoGetComplete();
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
        if (listPurHis == null) {
            return;
        }

        if (listPurHis.isEmpty()) {
            return;
        }
        try {
            VPurPromo record = listPurHis.get(row);
            switch(column){
                case 10:
                    record.setPromoGetComplete((Boolean) value);
                    if (record.getPurDetailId().toString() != null & !record.getPurDetailId().toString().equals("")
                            & !record.getPurDetailId().toString().equals("0")) {
                        Global.dao.execSql("update pur_detail_his set promo_get_complete ="+ value +" where pur_detail_id = "+ record.getPurDetailId());
                    }
                    else{
                        Global.dao.execSql("update pur_his set promo_get_complete ="+ value +" where pur_inv_id = '"+ record.getPurInvId()+"'");
                    }
                    break;
            }
                
        } catch (Exception ex) {
            log.error("getSetValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if(listPurHis == null){
            return 0;
        }
        return listPurHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VPurPromo> getListPurHis() {
        return listPurHis;
    }

    public void setListPurHis(List<VPurPromo> listPurHis) {
        this.listPurHis = listPurHis;
        fireTableDataChanged();
    }

    public VPurPromo getSelectVou(int row) {
        if(listPurHis != null){
            if(!listPurHis.isEmpty()){
                return listPurHis.get(row);
            }
        }
        return null;
    }
}
