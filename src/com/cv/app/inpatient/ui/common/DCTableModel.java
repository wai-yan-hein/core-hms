/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.CalculateObserver;
import com.cv.app.inpatient.database.entity.DCDetailHis;
import com.cv.app.inpatient.database.entity.DCDoctorFee;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.inpatient.ui.util.DCDoctorFeeDialog;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.view.VAddService;
import com.cv.app.ot.database.entity.DrDetailId;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DCTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DCTableModel.class.getName());
    private List<DCDetailHis> listOPDDetailHis = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Qty", "Price", "Charge Type", "Amount"};
    private final AbstractDataAccess dao;
    private ChargeType defaultChargeType;
    private String deletedList;
    private HashMap<Integer, Double> doctFees;
    private JTable parent;
    private String vouStatus;
    private String vouDate;
    private boolean canEdit = true;
    private int maxUniqueId = 0;
    private Double roomFee = 0.0;
    private final String roomFeeId = Util1.getPropValue("system.dc.room.fee.id");
    private String delDrFee = "";
    private CalculateObserver calObserver;

    public DCTableModel(AbstractDataAccess dao) {
        this.dao = dao;
        try {
            defaultChargeType = (ChargeType) dao.find(ChargeType.class, 1);
        } catch (Exception ex) {
            log.error("DCTableModel : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void setParent(JTable table) {
        parent = table;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (!canEdit) {
            return false;
        }
        DCDetailHis record = listOPDDetailHis.get(row);
        boolean isAlreadyP = isAlreadyPay(record);

        if (column == 1 || column == 3 || column == 5) {
            if (column == 3) {
                if (record.getService() == null) {
                    return false;
                } else if (vouStatus.equals("EDIT")) {
                    if (vouDate.equals(DateUtil.getTodayDateStr())) {
                        if (record.getService().isCfs() == null) {
                            return false;
                        } else {
                            if (isAlreadyP) {
                                return !isAlreadyP;
                            }
                            return record.getService().isCfs();
                        }
                    } else if (Util1.hashPrivilege("DCVoucherEditChange")) {
                        if (record.getService().isCfs() == null) {
                            return false;
                        } else {
                            if (isAlreadyP) {
                                return !isAlreadyP;
                            }
                            return record.getService().isCfs();
                        }
                    } else {
                        return false;
                    }
                } else if (record.getService().isCfs() == null) {
                    return false;
                } else {
                    if (isAlreadyP) {
                        return !isAlreadyP;
                    }
                    return record.getService().isCfs();
                }
            } else {
                return false;
            }

        } else /*if (record.getService() == null) {
             return true;
             } else {*/ if (vouStatus.equals("EDIT")) {
            if (vouDate.equals(DateUtil.getTodayDateStr())) {
                if (isAlreadyP) {
                    return !isAlreadyP;
                }
                return true;
            } else {
                if (isAlreadyP) {
                    return !isAlreadyP;
                }
                if (canEdit) {
                    return canEdit;
                } else {
                    return Util1.hashPrivilege("DCVoucherEditChange");
                }
            }
        } else {
            if (isAlreadyP) {
                return !isAlreadyP;
            }
            return true;
        } //}
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Qty
                return Integer.class;
            case 3: //Price
                return Double.class;
            case 4: //Charge Type
                return Object.class;
            case 5: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                try {
                    DCDetailHis record = listOPDDetailHis.get(row);
                    doctorFeeAssign(record);

                    switch (column) {
                        case 0: //Code
                            if (record.getService() != null) {
                                return record.getService().getServiceCode();
                            } else {
                                return null;
                            }
                        case 1: //Description
                            if (record.getService() != null) {
                                if (record.getListDCDF() != null) {
                                    if (!record.getListDCDF().isEmpty()) {
                                        DCDoctorFee otdf = record.getListDCDF().get(0);
                                        if (otdf != null) {
                                            Doctor dr = otdf.getDoctor();
                                            if (dr != null) {
                                                return record.getService().getServiceName() + " ("
                                                        + dr.getDoctorName() + ")";
                                            } else {
                                                return record.getService().getServiceName();
                                            }
                                        } else {
                                            return record.getService().getServiceName();
                                        }
                                    } else {
                                        return record.getService().getServiceName();
                                    }
                                } else {
                                    return record.getService().getServiceName();
                                }
                            } else {
                                return null;
                            }
                        /*if (record.getService() != null) {
                         return record.getService().getServiceName();
                         } else {
                         return null;
                         }*/
                        case 2: //Qty
                            return record.getQuantity();
                        case 3: //Price
                            return record.getPrice();
                        case 4: //Charge Type
                            return record.getChargeType();
                        case 5: //Amount
                            return record.getAmount();
                        default:
                            return null;
                    }
                } catch (Exception ex) {
                    log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listOPDDetailHis == null) {
            return;
        }

        if (listOPDDetailHis.isEmpty()) {
            return;
        }

        try {
            DCDetailHis record = listOPDDetailHis.get(row);

            switch (column) {
                case 0: //Code
                    if (value != null) {
                        if (value instanceof InpService) {
                            InpService service = (InpService) value;
                            /*int tmpPaidId = NumberUtil.NZeroInt(Util1.getPropValue("system.dc.paid.id"));
                             int tmpRefundId = NumberUtil.NZeroInt(Util1.getPropValue("system.dc.refund.id"));
                        
                             if(service.getServiceId() == tmpPaidId){
                            
                             }else if(service.getServiceId() == tmpRefundId){
                            
                             }*/
                            record.setService(service);
                            record.setQuantity(1);
                            if (record.getService().getServiceId().toString().equals(roomFeeId)) {
                                record.setPrice(roomFee);
                            } else {
                                record.setPrice(service.getFees());
                            }
                            record.setSrvFee1(service.getFees1());
                            record.setSrvFee2(service.getFees2());
                            record.setSrvFee3(service.getFees3());
                            record.setSrvFee4(service.getFees4());
                            record.setSrvFee5(service.getFees5());
                            if (record.getListDCDF() != null) {
                                if (!record.getListDCDF().isEmpty()) {
                                    if (delDrFee.isEmpty()) {
                                        delDrFee = "'" + record.getOpdDetailId() + "'";
                                    } else {
                                        delDrFee = delDrFee + ",'" + record.getOpdDetailId() + "'";
                                    }
                                }
                            }
                            record.setListDCDF(null);
                            record.setFeesVersionId(service.getPriceVersionId());
                            record.setChargeType(defaultChargeType);
                            if (isNeedDetail(service.getServiceId())) {
                                doctorFeePopup(record);
                            }
                            addAutoService(service.getServiceId());
                            parent.setColumnSelectionInterval(3, 3);
                        }
                    }
                    addNewRow();
                    break;
                case 1: //Description
                    /*if (value != null) {
                     if (value instanceof Service) {
                     Service service = (Service) value;
                     record.setService(service);
                     record.setQuantity(1);
                        
                     if(doctFees != null){
                     if(doctFees.containsKey(service.getServiceId())){
                     record.setPrice(doctFees.get(service.getServiceId()));
                     }else{
                     record.setPrice(service.getFees());
                     }
                     }else{
                     record.setPrice(service.getFees());
                     }
                        
                     record.setFeesVersionId(service.getPriceVersionId());
                     record.setChargeType(defaultChargeType);
                     updateAllFees();
                     }
                     }
                     addNewRow();*/
                    break;
                case 2: //Qty
                    if (NumberUtil.isNumber(value)) {
                        double tmpAmount = NumberUtil.NZero(record.getPrice()) * NumberUtil.NZero(value);
                        if (isValidDetailAmount(record, tmpAmount)) {
                            record.setQuantity(NumberUtil.NZeroInt(value));
                        }
                    } else {
                    }
                    break;
                case 3: //Price
                    if (NumberUtil.isNumber(value)) {
                        double tmpAmount = NumberUtil.NZero(record.getQuantity()) * NumberUtil.NZero(value);
                        if (isValidDetailAmount(record, tmpAmount)) {
                            record.setPrice(NumberUtil.NZero(value));
                        }
                    } else {
                    }
                    if ((getRowCount() - 1) > row) {
                        parent.setRowSelectionInterval(row + 1, row + 1);
                        parent.setColumnSelectionInterval(0, 0);
                    }
                    break;
                case 4: //Charge Type
                    record.setChargeType((ChargeType) value);
                    break;
            }
            calculateAmount(row);
            fireTableRowsUpdated(row, row);
            parent.requestFocus();

        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        
        calObserver.calculate();
    }

    private boolean isValidDetailAmount(DCDetailHis record, double amount) {
        boolean status = true;
        List<DCDoctorFee> listDCDF = record.getListDCDF();
        if (listDCDF != null) {
            if (!listDCDF.isEmpty()) {
                double total = 0;
                for (DCDoctorFee drf : listDCDF) {
                    total += NumberUtil.NZero(drf.getDrFee());
                }

                /*if (amount != total) {
                    status = false;
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid price. Check with doctor fees details.",
                            "Detail Fees", JOptionPane.ERROR_MESSAGE);
                }*/
            }
        }
        return status;
    }

    @Override
    public int getRowCount() {
        if (listOPDDetailHis == null) {
            return 0;
        } else {
            return listOPDDetailHis.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<DCDetailHis> getListOPDDetailHis() {
        if (listOPDDetailHis == null) {
            return null;
        }

        String strSql = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis"
                + " WHERE service IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listOPDDetailHis);
    }

    public void setListOPDDetailHis(List<DCDetailHis> listOPDDetailHis) {
        this.listOPDDetailHis = listOPDDetailHis;
        if (this.listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                DCDetailHis tmpD = listOPDDetailHis.get(listOPDDetailHis.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
            addNewRow();
            fireTableDataChanged();
        }
    }

    public DCDetailHis getOPDDetailHis(int row) {
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                return listOPDDetailHis.get(row);
            }
        }
        return null;
    }

    public void setOPDDetailHis(int row, DCDetailHis oPDDetailHis) {
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                listOPDDetailHis.set(row, oPDDetailHis);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addOPDDetailHis(DCDetailHis oPDDetailHis) {
        if (listOPDDetailHis != null) {
            listOPDDetailHis.add(oPDDetailHis);
            fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
        }
    }

    public void deleteOPDDetailHis(int row) {
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                DCDetailHis opdh = listOPDDetailHis.get(row);
                if (isAlreadyPay(opdh)) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Doctor payment already made. You cannot delete this service.",
                            "Doctor payment", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (opdh.getOpdDetailId() != null) {
                    if (deletedList == null) {
                        deletedList = "'" + opdh.getOpdDetailId() + "'";
                    } else {
                        deletedList = deletedList + ",'" + opdh.getOpdDetailId() + "'";
                    }
                }

                listOPDDetailHis.remove(row);
                fireTableRowsDeleted(0, listOPDDetailHis.size() - 1);
                if (row - 1 >= 0) {
                    parent.setRowSelectionInterval(row - 1, row - 1);
                }
            }
        }
        
        calObserver.calculate();
    }

    public void addNewRow() {
        if (listOPDDetailHis != null) {
            int count = listOPDDetailHis.size();
            if (count == 0 || listOPDDetailHis.get(count - 1).getService() != null) {
                listOPDDetailHis.add(new DCDetailHis());
                fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    public void calculateAmount(int row) {
        if (listOPDDetailHis == null) {
            return;
        }

        if (listOPDDetailHis.isEmpty()) {
            return;
        }

        DCDetailHis record = listOPDDetailHis.get(row);
        Double amount = null;

        if (record.getChargeType() != null) {
            if (record.getChargeType().getChargeTypeId() == 1) {
                amount = NumberUtil.NZeroInt(record.getQuantity())
                        * NumberUtil.NZero(record.getPrice());
            }
        }

        record.setAmount(amount);
    }

    public double getTotal() {
        if (listOPDDetailHis != null) {
            /*String strSql = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis"
             + " WHERE service IS NOT NULL EXECUTE ON ALL sum(amount) AS total";
             Object total = JoSQLUtil.getSaveValue(listOPDDetailHis, strSql, "total");

             if (total == null) {
             return 0;
             } else {
             return Double.parseDouble(total.toString());
             }*/

            int dcDepositId = NumberUtil.NZeroInt(Util1.getPropValue("system.dc.deposite.id"));
            int dcDiscountId = NumberUtil.NZeroInt(Util1.getPropValue("system.dc.disc.id"));
            int dcPaidId = NumberUtil.NZeroInt(Util1.getPropValue("system.dc.paid.id"));
            int dcRefundId = NumberUtil.NZeroInt(Util1.getPropValue("system.dc.refund.id"));

            double total = 0;
            for (DCDetailHis ddh : listOPDDetailHis) {
                if (ddh.getService() != null) {
                    int serviceId = ddh.getService().getServiceId();
                    if (serviceId != dcDepositId
                            && serviceId != dcDiscountId
                            && serviceId != dcPaidId
                            && serviceId != dcRefundId) {
                        double amount = 0;
                        if (ddh.getChargeType() != null) {
                            if (ddh.getChargeType().getChargeTypeId() == 1) {
                                amount = NumberUtil.NZeroInt(ddh.getQuantity())
                                        * NumberUtil.NZero(ddh.getPrice());
                            }
                        }

                        ddh.setAmount(amount);
                        total += NumberUtil.NZero(ddh.getAmount());
                    }
                }
            }

            return total;
        } else {
            return 0;
        }
    }

    public void clear() {
        deletedList = null;
        delDrFee = "";
        maxUniqueId = 0;
        roomFee = 0.0;
        if (listOPDDetailHis != null) {
            listOPDDetailHis.removeAll(listOPDDetailHis);
            addNewRow();
            fireTableDataChanged();
        }
    }

    public String getDeletedList() {
        return deletedList;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from dc_details_his where dc_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;
        int entryRow = 0;

        if (listOPDDetailHis != null) {
            for (DCDetailHis opdh : listOPDDetailHis) {
                String gainLostId = Util1.getPropValue("system.dc.pkggain.id");
                if (status && opdh.getService() != null) {
                    boolean needDetail = isNeedDetail(opdh.getService().getServiceId());
                    entryRow++;
                    /*if (NumberUtil.NZeroInt(opdh.getQuantity()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else*/ if (!opdh.getService().getServiceId().toString().equals(gainLostId)
                            && NumberUtil.NZero(opdh.getPrice()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Price must be positive value.",
                                "Minus or zero price.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (opdh.getChargeType() == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid charge type.",
                                "Charge Type", JOptionPane.ERROR_MESSAGE);
                        status = false;

                    } else if (opdh.getChargeType().getChargeTypeId() == 1 && NumberUtil.NZero(opdh.getAmount()) == 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid amount.",
                                "Amount", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (needDetail && opdh.getListDCDF() == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid " + opdh.getService().getServiceName()
                                + " fee. You must enter doctor name.",
                                "Amount", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (needDetail && opdh.getListDCDF().isEmpty()) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid " + opdh.getService().getServiceName()
                                + " fee. You must enter doctor name.",
                                "Amount", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else if (opdh.getService() != null) {
                        Double doctorTotal = 0.0;
                        if (needDetail) {
                            List<DCDoctorFee> listDrFee = opdh.getListDCDF();
                            for (DCDoctorFee drf : listDrFee) {
                                doctorTotal += NumberUtil.NZero(drf.getDrFee());
                            }
                        }

                        if (needDetail && (!Objects.equals(doctorTotal, NumberUtil.NZero(opdh.getPrice())))) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid " + opdh.getService().getServiceName()
                                    + ". You must enter the same amount in doctor detail.",
                                    "Amount", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        } else {
                            if (NumberUtil.NZeroInt(opdh.getUniqueId()) == 0) {
                                row += 1;
                                opdh.setUniqueId(row);
                            }
                            opdh.setReaderStatus(false);
                        }
                    }
                }
            }
        }

        if (entryRow == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "No voucher data.",
                    "No Data", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        return status;
    }

    //update opd price history for background fees
    private void updateAllFees() {
        String strSelectedId = null;
        String strSQL = "select service_id from opd_service os "
                + "where os.price_ver_id <> ifnull(os.ver_upd_id,0)";

        try {
            ResultSet rs = dao.execSQL(strSQL);
            if (rs != null) {
                while (rs.next()) {
                    String strTmpId = rs.getString("service_id");

                    if (strSelectedId == null) {
                        strSelectedId = strTmpId;
                    } else {
                        strSelectedId = strSelectedId + "," + strTmpId;
                    }
                }
            }

            strSQL = "insert into srv_fees_his(version_id, service_id, fees_id, fees)"
                    + " select os.price_ver_id, os.service_id, sf.fees_id, sf.srv_fees "
                    + "from opd_service os, service_fees sf "
                    + "where os.service_id = sf.service_id and os.service_id in ("
                    + strSelectedId + ")";
            dao.execSql(strSQL);

            strSQL = "update opd_service set ver_upd_id = price_ver_id "
                    + "where service_id in (" + strSelectedId + ")";
            dao.execSql(strSQL);
        } catch (Exception ex) {
            dao.rollBack();
            log.error("updateAllFees : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.closeStatment();
            dao.close();
        }
    }

    public HashMap<Integer, Double> getDoctFees() {
        return doctFees;
    }

    public void setDoctFees(HashMap<Integer, Double> doctFees) {
        this.doctFees = doctFees;
    }

    public int getTotalRecord() {
        if (listOPDDetailHis == null) {
            return 0;
        } else {
            return listOPDDetailHis.size();
        }
    }

    public void setDoctorFees(int serviceId, double fees) {
        if (listOPDDetailHis != null) {
            String strSql = "SELECT * FROM com.cv.app.dc.database.entity.DCDetailHis"
                    + " WHERE service.serviceId = " + serviceId;
            List<DCDetailHis> list = JoSQLUtil.getResult(strSql, listOPDDetailHis);

            if (!list.isEmpty()) {
                DCDetailHis odh = list.get(0);
                odh.setPrice(fees);
                odh.setAmount(odh.getQuantity() * odh.getPrice());
            }
        }
    }

    /*public void fireDataChange() {
        fireDataChange();
    }*/
    //update opd med usage history
    private void updateMedUsage() {

    }

    private boolean isNeedDetail(int serviceId) {
        boolean status = false;
        try {
            List<DrDetailId> listDDI = dao.findAllHSQL(
                    "select o from DrDetailId o where o.key.option = 'DC' and o.key.serviceId = " + serviceId);
            if (listDDI != null) {
                if (!listDDI.isEmpty()) {
                    status = true;
                }
            }
        } catch (Exception ex) {
            log.error("isNeedDetail : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return status;
    }

    private void doctorFeePopup(DCDetailHis record) {
        if (record != null) {
            if (record.getService() != null) {
                if (record.getService().getServiceId() != null) {
                    List<DCDoctorFee> listDrFee = record.getListDCDF();
                    if (listDrFee == null) {
                        try {
                            listDrFee = dao.findAllHSQL(
                                    "select o from DCDoctorFee o where o.dcDetailId = '"
                                    + record.getOpdDetailId() + "' order by o.uniqueId");
                        } catch (Exception ex) {
                            log.error("doctorFeePopup : " + ex.toString());
                        } finally {
                            dao.close();
                        }
                        if (listDrFee == null) {
                            listDrFee = new ArrayList();
                        }
                    }
                    DCDoctorFeeDialog dialog = new DCDoctorFeeDialog(listDrFee, record.getService().getServiceId());
                    dialog.setVisible(true);
                    record.setListDCDF(dialog.getEntryDrFee());
                    record.setPrice(dialog.getTotal());
                }
            }
        }
    }

    private void doctorFeeAssign(DCDetailHis record) {
        if (record != null) {
            if (record.getService() != null) {
                if (record.getService().getServiceId() != null) {
                    if (isNeedDetail(record.getService().getServiceId())) {
                        List<DCDoctorFee> listDrFee = record.getListDCDF();
                        if (listDrFee == null) {
                            try {
                                listDrFee = dao.findAllHSQL(
                                        "select o from DCDoctorFee o where o.dcDetailId = '"
                                        + record.getOpdDetailId() + "' order by o.uniqueId");
                                record.setListDCDF(listDrFee);
                            } catch (Exception ex) {
                                log.error("doctorFeePopup : " + ex.toString());
                            } finally {
                                dao.close();
                            }
                        }
                    }
                }
            }
        }
    }

    private void addAutoService(int serviceId) {
        try {
            String strSql = "select o from InpService o where o.serviceId in "
                    + "(select a.key.addServiceId from AutoAddIdMapping a where a.key.tranOption = 'DC' "
                    + "and a.key.tranServiceId = " + serviceId + ")";
            List<InpService> listSrv = dao.findAllHSQL(strSql);
            List<DCDetailHis> listTmp = new ArrayList();
            Collections.copy(listOPDDetailHis, listTmp);

            if (listSrv != null) {
                if (!listSrv.isEmpty()) {
                    for (InpService srv : listSrv) {
                        DCDetailHis record = new DCDetailHis();
                        record.setService(srv);
                        List<VAddService> listAdd = dao.findAllHSQL("select o from VAddService o where o.addServiceId = '"
                                + srv.getServiceId() + "' and o.tranOption = 'DC' and o.tranServiceId = " + serviceId);
                        if (!listAdd.isEmpty()) {
                            record.setQuantity(listAdd.get(0).getAddQty());
                        } else {
                            record.setQuantity(1);
                        }

                        record.setPrice(srv.getFees());
                        record.setSrvFee1(srv.getFees1());
                        record.setSrvFee2(srv.getFees2());
                        record.setSrvFee3(srv.getFees3());
                        record.setSrvFee4(srv.getFees4());
                        record.setSrvFee5(srv.getFees5());
                        record.setChargeType(defaultChargeType);

                        Double amount = null;
                        if (record.getChargeType() != null) {
                            if (record.getChargeType().getChargeTypeId() == 1) {
                                amount = NumberUtil.NZeroInt(record.getQuantity()) * NumberUtil.NZero(record.getPrice());
                            }
                        }
                        record.setAmount(amount);
                        listTmp.add(record);

                        //listOPDDetailHis.add(record);
                        //calculateAmount(listOPDDetailHis.size() - 1);
                        //fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
                        //parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
                        parent.getCellRect(parent.getRowCount() - 1, 0, true);
                    }
                    listOPDDetailHis = listTmp;
                }
            }
        } catch (Exception ex) {
            log.error("addAutoService : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void setVouStatus(String vouStatus) {
        this.vouStatus = vouStatus;
    }

    public String getVouDate() {
        return vouDate;
    }

    public void setVouDate(String vouDate) {
        this.vouDate = vouDate;
    }

    public void addPaidGain(Double paid, Double gainAmt) {
        if (paid != 0) {
            String paidId = Util1.getPropValue("system.dc.paid.id");
            if (paid < 0) {
                paidId = Util1.getPropValue("system.dc.refund.id");
            }
            if (!paidId.isEmpty()) {
                String strFilter = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis WHERE "
                        + "service.serviceId in (" + paidId + ")";
                List listPaid = JoSQLUtil.getResult(strFilter, listOPDDetailHis);
                try {
                    if (listPaid == null) {
                        InpService service = (InpService) dao.find(InpService.class, Integer.valueOf(paidId));
                        addService(service, paid);
                    } else if (listPaid.isEmpty()) {
                        InpService service = (InpService) dao.find(InpService.class, Integer.valueOf(paidId));
                        addService(service, paid);
                    } else {
                        DCDetailHis record = (DCDetailHis) listPaid.get(0);
                        record.setPrice(paid);
                        record.setAmount(record.getQuantity() * record.getPrice());
                    }
                    fireTableDataChanged();
                } catch (Exception ex) {
                    log.error("addPaidGain : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }

        if (gainAmt != 0) {
            String pkgGainId = Util1.getPropValue("system.dc.pkggain.id");
            if (!pkgGainId.isEmpty()) {
                String strFilter = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis WHERE "
                        + "service.serviceId in (" + pkgGainId + ")";
                List list = JoSQLUtil.getResult(strFilter, listOPDDetailHis);
                try {
                    if (list == null) {
                        InpService service = (InpService) dao.find(InpService.class, Integer.valueOf(pkgGainId));
                        addService(service, gainAmt);
                    } else if (list.isEmpty()) {
                        InpService service = (InpService) dao.find(InpService.class, Integer.valueOf(pkgGainId));
                        addService(service, gainAmt);
                    } else {
                        DCDetailHis record = (DCDetailHis) list.get(0);
                        record.setPrice(gainAmt);
                        record.setAmount(record.getQuantity() * record.getPrice());
                    }
                    fireTableDataChanged();
                } catch (Exception ex) {
                    log.error("addPaidGain 1" + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }

        addNewRow();
    }

    private void addService(InpService service, Double price) {
        DCDetailHis record = listOPDDetailHis.get(listOPDDetailHis.size() - 1);
        if (record.getService() != null) {
            record = new DCDetailHis();
            record.setService(service);
            record.setChargeType(defaultChargeType);
            record.setPrice(price);
            record.setQuantity(1);
            record.setAmount(record.getQuantity() * record.getPrice());
            listOPDDetailHis.add(record);
        } else {
            record.setService(service);
            record.setChargeType(defaultChargeType);
            record.setPrice(price);
            record.setQuantity(1);
            record.setAmount(record.getQuantity() * record.getPrice());
        }
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public void removePayItem() {
        String paidId = Util1.getPropValue("system.dc.paid.id");
        String refundId = Util1.getPropValue("system.dc.refund.id");
        String pkgGainId = Util1.getPropValue("system.dc.pkggain.id");
        int index = 0;
        List<DCDetailHis> listItem = new ArrayList();
        listItem.addAll(listOPDDetailHis);

        for (DCDetailHis record : listItem) {
            if (record.getService() != null) {
                if (record.getService().getServiceId().toString().equals(paidId)
                        || record.getService().getServiceId().toString().equals(refundId)
                        || record.getService().getServiceId().toString().equals(pkgGainId)) {
                    listOPDDetailHis.remove(index);
                }
                index++;
            }
        }

        fireTableDataChanged();
    }

    public void setRoomFee(Double roomFee) {
        this.roomFee = roomFee;
    }

    public String getDelDrFee() {
        return delDrFee;
    }

    public void setDelDrFee(String delDrFee) {
        this.delDrFee = delDrFee;
    }

    public void showDoctorDetail(int index) {
        if (listOPDDetailHis == null) {
            return;
        }

        if (listOPDDetailHis.isEmpty()) {
            return;
        }

        DCDetailHis record = listOPDDetailHis.get(index);
        if (!isAlreadyPay(record)) {
            InpService service = record.getService();
            if (isNeedDetail(service.getServiceId())) {
                doctorFeePopup(record);
                calculateAmount(index);
                fireTableCellUpdated(index, 5);
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Doctor payment already made. You cannot edit this service.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isPayAlready() {
        boolean status = false;
        for (DCDetailHis record : listOPDDetailHis) {
            if (record.getListDCDF() != null) {
                if (!record.getListDCDF().isEmpty()) {
                    DCDoctorFee df = record.getListDCDF().get(0);
                    if (df.getPayId() != null) {
                        status = true;
                        break;
                    }
                }
            }
            if (record.getPayId1() != null) {
                status = true;
                break;
            } else if (record.getPayId2() != null) {
                status = true;
                break;
            } else if (record.getPayId3() != null) {
                status = true;
                break;
            } else if (record.getPayId4() != null) {
                status = true;
                break;
            } else if (record.getPayId5() != null) {
                status = true;
                break;
            }
        }
        return status;
    }

    private boolean isAlreadyPay(DCDetailHis record) {
        boolean status = false;
        List<DCDoctorFee> listDF = record.getListDCDF();
        if (listDF != null) {
            for (DCDoctorFee df : listDF) {
                if (df.getPayId() != null) {
                    status = true;
                    break;
                }
            }
        }
        if (!status) {
            if (record.getPayId1() != null) {
                status = true;
            } else if (record.getPayId2() != null) {
                status = true;
            } else if (record.getPayId3() != null) {
                status = true;
            } else if (record.getPayId4() != null) {
                status = true;
            } else if (record.getPayId5() != null) {
                status = true;
            }
        }
        return status;
    }

    public void setCalObserver(CalculateObserver calObserver) {
        this.calObserver = calObserver;
    }
}
