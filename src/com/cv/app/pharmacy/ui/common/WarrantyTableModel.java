/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.SaleDetailHis;
import com.cv.app.pharmacy.database.entity.SaleWarranty;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class WarrantyTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(WarrantyTableModel.class.getName());
    private List<SaleWarranty> listWarranty = new ArrayList();
    private List<SaleDetailHis> listSaleDetail;
    private final String[] columnNames = {"Item Code", "Item Description", "Serial",
        "Warranty", "Start Date", "End Date"};

    public WarrantyTableModel(List<SaleDetailHis> listSaleDetail) {
        this.listSaleDetail = listSaleDetail;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 0 || column == 1);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
            case 1:
            case 2:
            case 3:
                return String.class;
            case 4:
            case 5:
                return Date.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listWarranty == null) {
            return null;
        }

        if (listWarranty.isEmpty()) {
            return null;
        }

        try {
            SaleWarranty saleWarranty = listWarranty.get(row);

            switch (column) {
                case 0: //Item Code
                    return saleWarranty.getItem().getMedId();
                case 1: //Item Description
                    return saleWarranty.getItem().getMedName();
                case 2: //Serial
                    return saleWarranty.getSerialNo();
                case 3: //Warranty
                    return saleWarranty.getWarranty();
                case 4: //Start Date
                    return saleWarranty.getStartDate();
                case 5: //End Date
                    return saleWarranty.getEndDate();
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
        if (listWarranty == null) {
            return;
        }

        if (listWarranty.isEmpty()) {
            return;
        }

        try {
            SaleWarranty sw = listWarranty.get(row);

            switch (column) {
                case 0: //Item Code
                    break;
                case 1: //Item Description
                    break;
                case 2: //Serial
                    if (value != null) {
                        sw.setSerialNo(value.toString());
                    } else {
                        sw.setSerialNo(null);
                    }
                    break;
                case 3: //Warranty
                    if (value != null) {
                        if (isWarrantyPattern(value.toString())) {
                            sw.setWarranty(value.toString());
                            sw.setStartDate(DateUtil.getTodayDateTime());
                            sw.setEndDate(getWarrantyEndDate(sw.getStartDate(),
                                    sw.getWarranty()));
                            System.out.println(sw.getEndDate());
                        } else {
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Invalid warranty pattern.",
                                    "Invalid pattern", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        sw.setWarranty(null);
                        sw.setStartDate(null);
                        sw.setEndDate(null);
                    }
                    break;
                case 4: //Start Date
                    if (value != null) {
                        sw.setStartDate((Date) value);
                    } else {
                        sw.setStartDate(null);
                    }
                    break;
                case 5: //End Date
                    if (value != null) {
                        sw.setEndDate(DateUtil.toDate(value));
                    } else {
                        sw.setEndDate(null);
                    }
                    break;
                default:
                    System.out.println("Invalid Column.");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        fireTableRowsUpdated(row, row);
    }

    @Override
    public int getRowCount() {
        if(listWarranty == null){
            return 0;
        }
        return listWarranty.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SaleWarranty> getListWarranty() {
        return listWarranty;
    }

    public void setListWarranty(List<SaleWarranty> listWarranty) {
        if (listWarranty == null || listWarranty.isEmpty()) {
            for (SaleDetailHis sdh : listSaleDetail) {
                if (sdh.getMedId().getMedId() != null
                        && isWarrantyPattern(sdh.getMedId().getChemicalName())) {
                    if (NumberUtil.NZeroInt(sdh.getQuantity()) > 0) {
                        this.listWarranty.add(getWarranty(sdh));
                    }
                }
            }
        } else {
            this.listWarranty = listWarranty;

            String itemIds = "";
            for (SaleWarranty sw : this.listWarranty) {
                if (itemIds.length() == 0) {
                    itemIds = sw.getItem().getMedId();
                } else {
                    itemIds += sw.getItem().getMedId();
                }
            }

            if (this.listWarranty.size() != listSaleDetail.size() - 1) {
                for (SaleDetailHis sdh : listSaleDetail) {
                    if (sdh.getMedId() != null) {
                        if (isWarrantyPattern(sdh.getMedId().getChemicalName())) {
                            if (NumberUtil.NZeroInt(sdh.getQuantity()) > 0) {
                                if (!itemIds.contains(sdh.getMedId().getMedId())) {
                                    this.listWarranty.add(getWarranty(sdh));
                                }
                            }
                        }
                    }
                }
            }
        }

        fireTableDataChanged();
    }

    public SaleWarranty getWarranty(SaleDetailHis sdh) {
        SaleWarranty sw = new SaleWarranty();

        sw.setItem(sdh.getMedId());
        sw.setWarranty(sdh.getMedId().getChemicalName());
        sw.setStartDate(DateUtil.getTodayDateTime());
        sw.setEndDate(getWarrantyEndDate(sw.getStartDate(), sdh.getMedId().getChemicalName()));

        return sw;
    }

    private boolean isWarrantyPattern(String pattern) {
        boolean status = true;
        String[] listPattern = pattern.split(",");
        int dCount = 0;
        int mCount = 0;
        int yCount = 0;
        String dValue = null;
        String mValue = null;
        String yValue = null;

        if (listPattern.length <= 3 && !pattern.isEmpty()) {
            for (int i = 0; i < listPattern.length; i++) {
                int length = listPattern[i].length();
                String tmpStr = listPattern[i].substring(length - 1, length);

                switch (tmpStr.toLowerCase()) {
                    case "d":
                        dCount++;
                        dValue = listPattern[i].substring(0, length - 1);
                        break;
                    case "m":
                        mCount++;
                        dValue = listPattern[i].substring(0, length - 1);
                        break;
                    case "y":
                        yCount++;
                        dValue = listPattern[i].substring(0, length - 1);
                        break;
                    default:
                        status = false;
                        i = listPattern.length;
                }
            }

            if (dCount > 1 || mCount > 1 || yCount > 1) {
                status = false;
            }

            if (dValue != null && status) {
                if (!NumberUtil.isNumber(dValue)) {
                    status = false;
                }
            }

            if (mValue != null && status) {
                if (!NumberUtil.isNumber(mValue)) {
                    status = false;
                }
            }

            if (yValue != null && status) {
                if (!NumberUtil.isNumber(yValue)) {
                    status = false;
                }
            }
        } else {
            status = false;
        }

        return status;
    }

    private Date getWarrantyEndDate(Date startDate, String pattern) {
        Date tmpDate = startDate;
        String[] listPattern = pattern.split(",");
        String dValue = null;
        String mValue = null;
        String yValue = null;

        for (int i = 0; i < listPattern.length; i++) {
            int length = listPattern[i].length();
            String tmpStr = listPattern[i].substring(length - 1, length);

            switch (tmpStr.toLowerCase()) {
                case "d":
                    dValue = listPattern[i].substring(0, length - 1);
                    break;
                case "m":
                    mValue = listPattern[i].substring(0, length - 1);
                    break;
                case "y":
                    yValue = listPattern[i].substring(0, length - 1);
                    break;
            }
        }

        if (dValue != null) {
            tmpDate = DateUtil.addTodayDateTo(Calendar.DATE, NumberUtil.NZeroInt(dValue));
        }

        if (mValue != null) {
            tmpDate = DateUtil.addTodayDateTo(Calendar.MONTH, NumberUtil.NZeroInt(dValue));
        }

        if (yValue != null) {
            tmpDate = DateUtil.addTodayDateTo(Calendar.YEAR, NumberUtil.NZeroInt(dValue));
        }

        return tmpDate;
    }
}
