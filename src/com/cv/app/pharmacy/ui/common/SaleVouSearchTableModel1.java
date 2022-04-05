/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleVouSearchTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleVouSearchTableModel.class.getName());
    private List<VoucherSearch> listVS = new ArrayList();
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
        if (listVS == null) {
            return null;
        }

        if (listVS.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch vs = listVS.get(row);

            switch (column) {
                case 0: //Date
                    return vs.getTranDate();
                case 1: //Vou No
                    if (Util1.getNullTo(vs.getIsDeleted())) {
                        return vs.getInvNo() + "**";
                    } else {
                        return vs.getInvNo();
                    }
                case 2: //Remark
                    return vs.getRefNo();
                case 3: //Customer
                    if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                        
                        if (vs.getCusNo() != null) {
                            return vs.getCusNo() + " - " + vs.getCusName();
                        } else {
                            return vs.getCusName();
                        }
                    } else {
                        if (vs.getCusNo() != null) {
                            if (prifxStatus.equals("Y")) {
                                return vs.getCusNo().replace("CUS", "").replace("SUP", "")
                                        + " - " + vs.getCusName();
                            } else {
                                return vs.getCusNo() + " - " + vs.getCusName();
                            }
                        } else {
                            if (vs.getCusName() != null) {
                                return vs.getCusName();
                            } else {
                                return null;
                            }
                        }
                    }
                case 4: //User
                    return vs.getUserName();
                case 5: //V-Total
                    return NumberUtil.roundTo(vs.getVouTotal(), 0);
                case 6: //Location
                    return vs.getLocation();
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
        if (listVS == null) {
            return 0;
        }
        return listVS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListVS() {
        return listVS;
    }

    public void setListVS(List<VoucherSearch> listVS) {
        this.listVS = listVS;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if (listVS != null) {
            if (!listVS.isEmpty()) {
                return listVS.get(row);
            }
        }
        return null;
    }

    public Double getTotal() {
        Double ttlAmt = 0.0;
        if (listVS != null) {
            if (!listVS.isEmpty()) {
                for (VoucherSearch vs : listVS) {
                    if (!vs.getIsDeleted()) {
                        ttlAmt += NumberUtil.NZero(vs.getVouTotal());
                    }
                }
            }
        }
        return ttlAmt;
    }
}
