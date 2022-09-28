/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PurDetailHis;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Eitar
 */
public class PurchaseItemPromoTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurchaseItemPromoTableModel.class.getName());
    private List<PurDetailHis> listPurDetail;
    private MedicineUP medUp;
    private final String[] columnNames = {"Code", "Name", "Description", "Start-Date", "End-Date", "Give-Per", "Get-Per", "Get-complete"};

    public PurchaseItemPromoTableModel(List<PurDetailHis> listPurDetail, MedicineUP medUp) {
        this.listPurDetail = listPurDetail;
        this.medUp = medUp;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    
    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 2 || column == 3 || column == 4 || column == 5
               || column == 6 || column == 7) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
         if (column == 7) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPurDetail == null) {
            return null;
        }

        if (listPurDetail.isEmpty()) {
            return null;
        }
        try {
            PurDetailHis record = listPurDetail.get(row);

            switch (column) {
                case 0: //code
                    return record.getMedId().getMedId();
                case 1: //Name
                    return record.getMedId().getMedName();
                case 2: //des
                    return record.getPromoDesp();
                case 3: //Start Date
                    return DateUtil.toDateStr(record.getPromoStartDate());
                case 4: //Exp-Date
                    return DateUtil.toDateStr(record.getPromoEndDate());
                case 5: //Give-Per
                    return record.getPromoGivePercent();
                case 6: //Get-Per
                    return record.getPromoGetPercent();
                case 7: //Get-Complete
                    return record.isPromoGetComplete();
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
        if (listPurDetail == null) {
            return;
        }

        if (listPurDetail.isEmpty()) {
            return;
        }

        try {
            PurDetailHis record = listPurDetail.get(row);

            switch (column) {
                case 2: //des
                    if (value != null) {
                        record.setPromoDesp((String) value);
                    } else {
                        record.setPromoDesp(null);
                    }
                    break;
                case 3: //sdate
                    if (DateUtil.isValidDate(value)) {
                        record.setPromoStartDate(DateUtil.toDate(value));
                    }
                    break;
                case 4: //edate
                    if (DateUtil.isValidDate(value)) {
                        record.setPromoEndDate(DateUtil.toDate(value));
                    }
                    break;
                case 5: //give
                    record.setPromoGivePercent(NumberUtil.NZeroFloat(value));
                    break;
                case 6: //get
                    record.setPromoGetPercent(NumberUtil.NZeroFloat(value));
                    break;
                case 7: //com
                    record.setPromoGetComplete((Boolean) value);
                    break;
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listPurDetail == null) {
            return 0;
        }
        return listPurDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PurDetailHis> getListItemPromo() {
        return listPurDetail;
    }

    public void setListItemPromo(List<PurDetailHis> listPurDetail) {
        if (listPurDetail == null) {
            return;
        }

        if (listPurDetail.isEmpty()) {
            return;
        }
        this.listPurDetail = listPurDetail;
        fireTableDataChanged();
    }

}
