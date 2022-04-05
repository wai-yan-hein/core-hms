/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.SaleHis;
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
public class SaleVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleVouSearchTableModel.class.getName());
    private List<SaleHis> listSaleHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Ref. Vou.", "Customer", "User", "V-Total", "Location"};
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Vou No
                return String.class;
            case 2: //Remark
                return String.class;
            case 3: //Customer
                return String.class;
            case 4: //User
                return String.class;
            case 5: //V-Total
                return Double.class;
            case 6: //Location
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listSaleHis == null) {
            return null;
        }

        if (listSaleHis.isEmpty()) {
            return null;
        }

        try {
            SaleHis sh = listSaleHis.get(row);

            switch (column) {
                case 0: //Date
                    return sh.getSaleDate();
                case 1: //Vou No
                    if (Util1.getNullTo(sh.getDeleted())) {
                        return sh.getSaleInvId() + "**";
                    } else {
                        return sh.getSaleInvId();
                    }
                case 2: //Remark
                    return sh.getRemark();
                case 3: //Customer
                    if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                        if (sh.getPatientId() != null) {
                            return sh.getPatientId().getPatientName();
                        } else {
                            if (sh.getStuName() != null) {
                                return sh.getStuName();
                            } else {
                                return null;
                            }
                        }
                    } else {
                        if (sh.getCustomerId() != null) {
                            if (prifxStatus.equals("Y")) {
                                return sh.getCustomerId().getTraderId().replace("CUS", "").replace("SUP", "")
                                        + " - " + sh.getCustomerId().getTraderName();
                            } else {
                                return sh.getCustomerId().getTraderId() + " - " + sh.getCustomerId().getTraderName();
                            }
                        } else {
                            if (sh.getStuName() != null) {
                                return sh.getStuName();
                            } else {
                                return null;
                            }
                        }
                    }
                case 4: //User
                    return sh.getCreatedBy().getUserShortName();
                case 5: //V-Total
                    double total = (NumberUtil.NZero(sh.getVouTotal())
                            + NumberUtil.NZero(sh.getTaxAmt()))
                            - NumberUtil.NZero(sh.getDiscount());
                    return total;
                case 6: //Location
                    return sh.getLocationId().getLocationName();
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
    }

    @Override
    public int getRowCount() {
        if (listSaleHis == null) {
            return 0;
        }
        return listSaleHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SaleHis> getListSaleHis() {
        return listSaleHis;
    }

    public void setListSaleHis(List<SaleHis> listSaleHis) {
        this.listSaleHis = listSaleHis;
        fireTableDataChanged();
    }

    public SaleHis getSelectVou(int row) {
        if (listSaleHis != null) {
            if (!listSaleHis.isEmpty()) {
                return listSaleHis.get(row);
            }
        }
        return null;
    }

    public Double getTotal() {
        Double ttlAmt = 0.0;
        if (listSaleHis != null) {
            if (!listSaleHis.isEmpty()) {
                for (SaleHis sh : listSaleHis) {
                    if (!sh.getDeleted()) {
                        ttlAmt += NumberUtil.NZero(sh.getVouTotal());
                    }
                }
            }
        }
        return ttlAmt;
    }
}
