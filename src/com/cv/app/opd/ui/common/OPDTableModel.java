/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.CalculateObserver;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.OPDAutoAddIdMapping;
import com.cv.app.opd.database.entity.OPDDetailHis;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.opd.ui.util.UrgentDialog;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author WSwe
 */
public class OPDTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDTableModel.class.getName());
    private List<OPDDetailHis> listOPDDetailHis = ObservableCollections.observableList(new ArrayList());
    private final String[] columnNames = {"Code", "Description", "Qty", "Price",
        "Charge Type", "Refer Dr", "Read Dr", "Technician", "Amount"};
    private final AbstractDataAccess dao;
    private ChargeType defaultChargeType = null;
    private String deletedList;
    private HashMap<Integer, Double> doctFees;
    private JTable parent;
    private final SelectionObserver observer;
    private String vouStatus;
    private String readerDoctor = "-";
    private Doctor referDoctor;
    private boolean canEdit = true;
    private int maxUniqueId = 1;
    private double pkgTotal = 0.0;
    private double extraTotal = 0.0;
    String useOPDFactor = Util1.getPropValue("system.opd.chargetype.factor");
    private CalculateObserver calObserver;
    private String bookType = "-";

    public OPDTableModel(AbstractDataAccess dao, SelectionObserver observer) {
        this.dao = dao;
        this.observer = observer;
        try {
            defaultChargeType = (ChargeType) dao.find(ChargeType.class, 1);
        } catch (Exception ex) {
            log.error("OPDTableModel : " + ex.getMessage());
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

        OPDDetailHis record = listOPDDetailHis.get(row);
        boolean isAlreadyP = isAlreadyPay(record);

        if (column == 1 || column == 3 || column == 8) {
            if (column == 3) {
                if (record.getService() == null) {
                    return false;
                } else if (vouStatus.equals("EDIT")) {
                    if (Util1.hashPrivilege("OPDVoucherEditChange")) {
                        if (!record.getPkgItem()) {
                            if (isAlreadyP) {
                                return !isAlreadyP;
                            }
                            return record.getService().isCfs();
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else if (!record.getPkgItem()) {
                    if (isAlreadyP) {
                        return !isAlreadyP;
                    }
                    return record.getService().isCfs();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else /*if (record.getService() == null) {
             return true;
             } else {*/ if (vouStatus.equals("EDIT")) {
            if (!record.getPkgItem()) {
                if (isAlreadyP) {
                    return !isAlreadyP;
                }
                if (canEdit) {
                    return canEdit;
                } else {
                    return Util1.hashPrivilege("OPDVoucherEditChange");
                }
            } else {
                return false;
            }
        } else if (!record.getPkgItem()) {
            if (isAlreadyP) {
                return !isAlreadyP;
            }
            return true;
        } else {
            return false;
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
            case 5: //Refer Dr
                return Doctor.class;
            case 6: //Read Dr
                return Doctor.class;
            case 7: //Technician
                return Doctor.class;
            case 8: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        OPDDetailHis record = listOPDDetailHis.get(row);

        switch (column) {
            case 0: //Code
                if (record.getService() != null) {
                    return record.getService().getServiceCode();
                } else {
                    return null;
                }
            case 1: //Description
                if (record.getService() != null) {
                    return record.getService().getServiceName();
                } else {
                    return null;
                }
            case 2: //Qty
                return record.getQuantity();
            case 3: //Price
                return record.getPrice();
            case 4: //Charge Type
                return record.getChargeType();
            case 5: //Refer Dr
                return record.getReferDr();
            case 6: //Read Dr
                return record.getDr();
            case 7: //Technician
                return record.getTechnician();
            case 8: //Amount
                return record.getAmount();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDDetailHis record = listOPDDetailHis.get(row);

        switch (column) {
            case 0: //Code
                try {
                if (value != null) {
                    if (value instanceof Service) {
                        Service service = (Service) value;
                        record.setService(service);
                        record.setQuantity(1);
                        record.setFees1(service.getFees1());
                        record.setFees2(service.getFees2());
                        record.setFees3(service.getFees3());
                        record.setFees4(service.getFees4());
                        record.setFees5(service.getFees5());
                        record.setFees6(service.getFees6());
                        record.setServiceCost(service.getServiceCost());
                        record.setPercent(service.isPercent());
                        record.setReferDr(referDoctor);
                        record.setLabRemark(service.getLabRemark());
                        record.setFees(service.getFees());

                        if (doctFees != null) {
                            if (doctFees.containsKey(service.getServiceId())) {
                                record.setPrice(doctFees.get(service.getServiceId()));
                            } else {
                                record.setPrice(service.getFees());
                            }
                        } else {
                            record.setPrice(service.getFees());
                        }

                        record.setFeesVersionId(service.getPriceVersionId());
                        record.setChargeType(defaultChargeType);
                        updateAllFees();
                        if (service.getDoctor() != null) {
                            observer.selected("DoctorSearch", service.getDoctor());
                        }

                        if (!Util1.getPropValue("system.opd.idforauto").equals("-")
                                && getRowCount() == 1) {
                            if (Util1.getPropValue("system.opd.idforauto").equals(record.getService().getServiceId().toString())) {
                                if (!Util1.getPropValue("system.opd.autoid").equals("-")
                                        && getRowCount() == 1) {
                                    int id = NumberUtil.NZeroInt(Util1.getPropValue("system.opd.autoid"));
                                    Service tmpService = (Service) dao.find(Service.class, id);
                                    if (tmpService != null) {
                                        addAutoService(tmpService);
                                    }
                                } else {
                                    addAutoService(service.getServiceId());
                                }
                            } else {
                                addAutoService(service.getServiceId());
                            }
                        }
                        // else{
                        //addAutoService(service.getServiceId());
                        // }
                    }
                }
            } catch (Exception ex) {
                log.error("setValueAt code : " + ex.getMessage());
            } finally {
                dao.close();
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
                    record.setQuantity(NumberUtil.NZeroInt(value));
                } else {
                }
                break;
            case 3: //Price
                if (NumberUtil.isNumber(value)) {
                    record.setPrice(NumberUtil.NZero(value));
                } else {
                }
                break;
            case 4: //Charge Type
                try {
                record.setChargeType((ChargeType) value);
                if (record.getService() != null) {
                    Service tmpService = (Service) dao.find(Service.class, record.getService().getServiceId());
                    record.setPrice(tmpService.getFees());
                }
            } catch (Exception ex) {
                log.error("setValueAt Charge Type : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case 5: //Rrefer Dr
                record.setReferDr((Doctor) value);
                break;
            case 6: //Read Dr
                record.setDr((Doctor) value);
                break;
            case 7: //Technician
                record.setTechnician((Doctor) value);
                break;
        }

        if (bookType != null) {
            if (bookType.equals("Emergency")) {
                if (NumberUtil.isNumber(Util1.getPropValue("system.emergency.percent"))) {
                    float percent = Float.parseFloat(Util1.getPropValue("system.emergency.percent"));
                    double price = record.getPrice();
                    double percentPrice = price * (percent / 100);
                    log.info("Emergency Price : % " + percent + " New Price : " + percentPrice);
                    record.setPrice(NumberUtil.roundTo(price + percentPrice, 0));
                }
            }
        }
        
        observer.selected("CAL-TOTAL", "CAL-TOTAL");
        calculateAmount(row);
        fireTableRowsUpdated(row, row);
        try {
            parent.setRowSelectionInterval(getRowCount() - 1, getRowCount() - 1);
            parent.requestFocus();
            parent.setColumnSelectionInterval(0, 0);
        } catch (Exception ex) {

        }

        calObserver.calculate();
    }

    @Override
    public int getRowCount() {
        return listOPDDetailHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDDetailHis> getListOPDDetailHis() {
        String strSql = "SELECT * FROM com.cv.app.opd.database.entity.OPDDetailHis"
                + " WHERE service IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listOPDDetailHis);
    }

    public void setListOPDDetailHis(List<OPDDetailHis> listOPDDetailHis) {
        this.listOPDDetailHis = listOPDDetailHis;
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                OPDDetailHis tmpD = listOPDDetailHis.get(listOPDDetailHis.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        addNewRow();
        fireTableDataChanged();
    }

    public OPDDetailHis getOPDDetailHis(int row) {
        return listOPDDetailHis.get(row);
    }

    public void setOPDDetailHis(int row, OPDDetailHis oPDDetailHis) {
        listOPDDetailHis.set(row, oPDDetailHis);
        fireTableRowsUpdated(row, row);
    }

    public void addOPDDetailHis(OPDDetailHis oPDDetailHis) {
        listOPDDetailHis.add(oPDDetailHis);
        fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
    }

    public void deleteOPDDetailHis(int row) {
        OPDDetailHis opdh = listOPDDetailHis.get(row);
        if (isAlreadyPay(opdh)) {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Doctor payment already made. You cannot delete this service.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!opdh.getPkgItem()) {
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
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "You cannot delete package item.",
                    "Package Item Delete", JOptionPane.ERROR_MESSAGE);
        }

        calObserver.calculate();
    }

    public void addNewRow() {
        int count = listOPDDetailHis.size();
        if (count == 0 || listOPDDetailHis.get(count - 1).getService() != null) {
            listOPDDetailHis.add(new OPDDetailHis());
            fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    private void calculateAmount(int row) {
        OPDDetailHis record = listOPDDetailHis.get(row);
        Double amount = null;
        boolean isAmount = false;

        if (record.getChargeType() != null) {
            int chargeType = record.getChargeType().getChargeTypeId();

            switch (chargeType) {
                case 1: //Normal
                    amount = NumberUtil.NZeroInt(record.getQuantity())
                            * NumberUtil.NZero(record.getPrice());
                    break;
                case 2: //FOC
                    break;
                default:
                    if (useOPDFactor.equals("Y")) {
                        float factor = NumberUtil.FloatZero(record.getChargeType().getFactor());
                        if (record.getChargeType() != null) {
                            isAmount = record.getChargeType().getIsAmount();
                        }
                        if (isAmount) {
                            double tmpPrice = NumberUtil.NZero(record.getPrice()) + factor;
                            record.setPrice(tmpPrice);
                        } else {
                            double tmpPercentAmt = (NumberUtil.NZero(record.getPrice()) * factor) / 100;
                            double tmpPrice = NumberUtil.NZero(record.getPrice()) + tmpPercentAmt;
                            record.setPrice(tmpPrice);
                        }
                    }
                    amount = NumberUtil.NZeroInt(record.getQuantity())
                            * NumberUtil.NZero(record.getPrice());
            }
        }
        record.setAmount(amount);
    }

    private void calculateAmount(OPDDetailHis record) {
        Double amount = null;
        boolean isAmount = false;

        if (record.getChargeType() != null) {
            int chargeType = record.getChargeType().getChargeTypeId();

            switch (chargeType) {
                case 1: //Normal
                    amount = NumberUtil.NZeroInt(record.getQuantity())
                            * NumberUtil.NZero(record.getPrice());
                    break;
                case 2: //FOC
                    break;
                default:
                    if (useOPDFactor.equals("Y")) {
                        float factor = NumberUtil.FloatZero(record.getChargeType().getFactor());
                        if (record.getChargeType() != null) {
                            isAmount = record.getChargeType().getIsAmount();
                        }
                        if (isAmount) {
                            double tmpPrice = NumberUtil.NZero(record.getPrice()) + factor;
                            record.setPrice(tmpPrice);
                        } else {
                            double tmpPercentAmt = (NumberUtil.NZero(record.getPrice()) * factor) / 100;
                            double tmpPrice = NumberUtil.NZero(record.getPrice()) + tmpPercentAmt;
                            record.setPrice(tmpPrice);
                        }
                    }
                    amount = NumberUtil.NZeroInt(record.getQuantity())
                            * NumberUtil.NZero(record.getPrice());
            }
        }
        record.setAmount(amount);
    }

    public double getTotal() {
        double total = 0.0;
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                for (OPDDetailHis odh : listOPDDetailHis) {
                    calculateAmount(odh);
                    total += NumberUtil.NZero(odh.getAmount());
                }
            }
        }

        /*if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                total = listOPDDetailHis.stream().map(odh -> NumberUtil.NZero(odh.getAmount()))
                        .reduce(total, (accumulator, _item) -> accumulator + _item);
            }
        }*/
        return total;
    }

    public void calculatePkgTotal() {
        pkgTotal = 0.0;
        extraTotal = 0.0;

        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                listOPDDetailHis.forEach(odh -> {
                    if (odh.getPkgItem()) {
                        pkgTotal += NumberUtil.NZero(odh.getAmount());
                    } else {
                        extraTotal += NumberUtil.NZero(odh.getAmount());
                    }
                });
            }
        }
    }

    public void clear() {
        deletedList = null;
        maxUniqueId = 1;
        pkgTotal = 0.0;
        extraTotal = 0.0;
        listOPDDetailHis.removeAll(listOPDDetailHis);
        addNewRow();
        fireTableDataChanged();
    }

    public String getDeletedList() {
        return deletedList;
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from opd_details_his where opd_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;
        int entryRow = 0;

        for (OPDDetailHis opdh : listOPDDetailHis) {
            if (status && opdh.getService() != null) {
                if (NumberUtil.NZero(opdh.getPrice()) <= 0) {
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
                } else if (opdh.getService() != null) {
                    entryRow++;
                    if (NumberUtil.NZeroInt(opdh.getUniqueId()) == 0) {
                        row += 1;
                        opdh.setUniqueId(row);
                    }
                    opdh.setReaderStatus(false);
                    if (opdh.getDr() != null && readerDoctor.equals("-")) {
                        readerDoctor = opdh.getDr().getDoctorName();
                    }
                } //}
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
        return listOPDDetailHis.size();
    }

    public void setDoctorFees(int serviceId, double fees) {
        String strSql = "SELECT * FROM com.cv.app.opd.database.entity.OPDDetailHis"
                + " WHERE service.serviceId = " + serviceId;
        List<OPDDetailHis> list = JoSQLUtil.getResult(strSql, listOPDDetailHis);

        if (list.size() > 0) {
            OPDDetailHis odh = list.get(0);
            odh.setPrice(fees);
            odh.setAmount(odh.getQuantity() * odh.getPrice());
        }
    }

    /*public void fireDataChange() {
        fireDataChange();
    }*/
    //update opd med usage history
    private void updateMedUsage() {

    }

    private void addAutoService(int serviceId) {
        /*String strSql = "select o from Service o where o.serviceId in "
         + "(select a.key.addServiceId from AutoAddIdMapping a where a.key.tranOption = 'OPD' "
         + "and a.key.tranServiceId = " + serviceId + " order by a.sortOrder)";
         List<Service> listSrv = dao.findAllHSQL(strSql);*/
        try {
            String strSql = "select o from OPDAutoAddIdMapping o where o.key.tranOption = 'OPD' "
                    + "and o.key.tranServiceId = " + serviceId + " order by o.sortOrder";
            List<OPDAutoAddIdMapping> listIDM = dao.findAllHSQL(strSql);
            //List<OPDDetailHis> listTmp = new ArrayList();
            //Collections.copy(listOPDDetailHis, listTmp);
            if (listIDM != null) {
                if (!listIDM.isEmpty()) {
                    //listOPDDetailHis.remove(listOPDDetailHis.size()-1);
                    for (OPDAutoAddIdMapping idm : listIDM) {
                        Service srv = idm.getKey().getAddServiceId();
                        OPDDetailHis record = new OPDDetailHis();
                        record.setService(srv);
                        record.setQuantity(1);
                        record.setFees1(srv.getFees1());
                        record.setFees2(srv.getFees2());
                        record.setFees3(srv.getFees3());
                        record.setFees4(srv.getFees4());
                        record.setFees5(srv.getFees5());
                        record.setFees6(srv.getFees6());
                        record.setPercent(srv.isPercent());
                        record.setPrice(srv.getFees());
                        record.setFees(srv.getFees());
                        record.setFeesVersionId(srv.getPriceVersionId());
                        record.setChargeType(defaultChargeType);

                        listOPDDetailHis.add(record);
                        calculateAmount(listOPDDetailHis.size() - 1);
                        fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
                        parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("addAutoService : " + ex.getMessage());
        }
    }

    private void addAutoService(Service srv) {
        OPDDetailHis record = new OPDDetailHis();
        record.setService(srv);
        record.setQuantity(1);
        record.setFees1(srv.getFees1());
        record.setFees2(srv.getFees2());
        record.setFees3(srv.getFees3());
        record.setFees4(srv.getFees4());
        record.setFees5(srv.getFees5());
        record.setFees6(srv.getFees6());
        record.setPercent(srv.isPercent());
        record.setPrice(srv.getFees());
        record.setFees(srv.getFees());
        record.setFeesVersionId(srv.getPriceVersionId());
        record.setChargeType(defaultChargeType);

        listOPDDetailHis.add(record);
        calculateAmount(listOPDDetailHis.size() - 1);
        fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
        parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
    }

    public void addAutoServiceByDoctor() {
        String drAutoAddId = Util1.getPropValue("system.opd.dr.autoaddId");
        String strFilter = "SELECT * FROM com.cv.app.opd.database.entity.OPDDetailHis "
                + "WHERE service.serviceId in (" + drAutoAddId + ")";
        List list = JoSQLUtil.getResult(strFilter, listOPDDetailHis);

        if (!drAutoAddId.isEmpty() && !drAutoAddId.equals("-") && list.isEmpty()) {
            try {
                String strSql = "select o from Service o where o.serviceId in (" + drAutoAddId + ")";
                List<Service> listService = dao.findAllHSQL(strSql);
                if (listService != null) {
                    if (!listService.isEmpty()) {
                        listOPDDetailHis.remove(listOPDDetailHis.size() - 1);
                        for (Service service : listService) {
                            OPDDetailHis record = new OPDDetailHis();
                            record.setService(service);
                            record.setQuantity(1);
                            record.setFees1(service.getFees1());
                            record.setFees2(service.getFees2());
                            record.setFees3(service.getFees3());
                            record.setFees4(service.getFees4());
                            record.setFees5(service.getFees5());
                            record.setFees6(service.getFees6());
                            record.setPercent(service.isPercent());
                            record.setPrice(service.getFees());
                            record.setFees(service.getFees());
                            record.setFeesVersionId(service.getPriceVersionId());
                            record.setChargeType(defaultChargeType);

                            listOPDDetailHis.add(record);
                            calculateAmount(listOPDDetailHis.size() - 1);
                            fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
                            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
                        }

                        addNewRow();
                    }
                }
            } catch (Exception ex) {
                log.error("addAutoServiceByDoctor : " + ex.getMessage());
            }
        }
    }

    public void setVouStatus(String vouStatus) {
        this.vouStatus = vouStatus;
    }

    public String getReaderDoctor() {
        return readerDoctor;
    }

    public void setReaderDoctor(String readerDoctor) {
        this.readerDoctor = readerDoctor;
    }

    public Doctor getReferDoctor() {
        return referDoctor;
    }

    public void setReferDoctor(Doctor referDoctor) {
        this.referDoctor = referDoctor;
    }

    public void updateReferDoctor() {
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                listOPDDetailHis.stream().filter(opdh -> (opdh.getService() != null)).forEachOrdered(opdh -> {
                    opdh.setReferDr(referDoctor);
                });
                fireTableDataChanged();
            }
        }
    }

    public void showUrgentDialog(int index) {
        UrgentDialog dialog = new UrgentDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        Boolean status = dialog.getUrgentStatus();
        OPDDetailHis record = listOPDDetailHis.get(index);

        if (status) {
            record.setPrice(getPrice(record));
        } else {
            record.setPrice(record.getFees());
        }
        if (NumberUtil.NZero(record.getPrice()) == 0) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid urgent fee.",
                    "Urgent Fee", JOptionPane.ERROR_MESSAGE);
            record.setPrice(record.getFees());
        }
        record.setUrgent(status);
        record.setAmount(NumberUtil.NZero(record.getQuantity()) * NumberUtil.NZero(record.getPrice()));
        fireTableCellUpdated(index, 3);
        fireTableCellUpdated(index, 8);
    }

    private Double getPrice(OPDDetailHis record) {
        String priceField = Util1.getPropValue("system.opd.urgent");
        switch (priceField) {
            case "srv_fees1":
                return record.getFees1();
            case "srv_fees2":
                return record.getFees2();
            case "srv_fees3":
                return record.getFees3();
            case "srv_fees4":
                return record.getFees4();
            case "srv_fees5":
                return record.getFees5();
            case "srv_fees6":
                return record.getFees6();
            default:
                return record.getFees();
        }
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public ChargeType getDefaultChargeType() {
        return defaultChargeType;
    }

    public void addPackageItem(OPDDetailHis record) {
        if (listOPDDetailHis != null) {
            OPDDetailHis tmpRec = listOPDDetailHis.get(listOPDDetailHis.size() - 1);
            if (tmpRec.getService() == null) {
                listOPDDetailHis.set(listOPDDetailHis.size() - 1, record);
            } else {
                listOPDDetailHis.add(record);
            }
        }
    }

    public void dataChange() {
        try {
            fireTableDataChanged();
        } catch (Exception ex) {

        }
    }

    public void removePackage() {
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                List<OPDDetailHis> listTmp = new ArrayList();
                listTmp.addAll(listOPDDetailHis);
                for (int i = 0; i < listTmp.size(); i++) {
                    OPDDetailHis tmpRec = listTmp.get(i);
                    if (tmpRec.getPkgItem()) {
                        listOPDDetailHis.remove(tmpRec);
                        if (tmpRec.getOpdDetailId() != null) {
                            if (deletedList == null) {
                                deletedList = "'" + tmpRec.getOpdDetailId() + "'";
                            } else {
                                deletedList = deletedList + ",'" + tmpRec.getOpdDetailId() + "'";
                            }
                        }
                    }
                }

                fireTableDataChanged();
            }
        }
    }

    public double getPkgTotal() {
        return pkgTotal;
    }

    public double getExtraTotal() {
        return extraTotal;
    }

    public boolean isPayAlready() {
        boolean status = false;
        for (OPDDetailHis record : listOPDDetailHis) {
            if (record.getFee1Id() != null) {
                status = true;
                break;
            } else if (record.getFee2Id() != null) {
                status = true;
                break;
            } else if (record.getFee3Id() != null) {
                status = true;
                break;
            } else if (record.getFee4Id() != null) {
                status = true;
                break;
            } else if (record.getFee5Id() != null) {
                status = true;
                break;
            } else if (record.getFee6Id() != null) {
                status = true;
                break;
            }
        }
        return status;
    }

    private boolean isAlreadyPay(OPDDetailHis record) {
        boolean status = false;
        if (record.getFee1Id() != null) {
            status = true;
        } else if (record.getFee2Id() != null) {
            status = true;
        } else if (record.getFee3Id() != null) {
            status = true;
        } else if (record.getFee4Id() != null) {
            status = true;
        } else if (record.getFee5Id() != null) {
            status = true;
        } else if (record.getFee6Id() != null) {
            status = true;
        }
        return status;
    }

    public void setCalObserver(CalculateObserver calObserver) {
        this.calObserver = calObserver;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public void addEMGPercent(float percent) {
        for (OPDDetailHis odh : listOPDDetailHis) {
            if (odh.getService() != null) {
                if (odh.getService().getServiceId() != null) {
                    ChargeType ct = odh.getChargeType();
                    if (ct.getChargeTypeId() != 2) {
                        double price = odh.getService().getFees();
                        double incValue = price * (percent / 100);
                        odh.setPrice(price + incValue);
                        odh.setAmount(NumberUtil.FloatZero(odh.getQuantity())
                                * NumberUtil.NZero(odh.getPrice()));
                    }
                }
            }
        }
        fireTableDataChanged();
    }
}
