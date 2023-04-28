/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.common.CalculateObserver;
import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.view.VAddService;
import com.cv.app.ot.database.entity.DrDetailId;
import com.cv.app.ot.database.entity.OTDetailHis;
import com.cv.app.ot.database.entity.OTDoctorFee;
import com.cv.app.ot.database.entity.OTEntryTranLog;
import com.cv.app.ot.database.entity.OTProcedure;
import com.cv.app.ot.ui.util.OTDoctorFeeDialog;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTTableModel.class.getName());
    private List<OTDetailHis> listOPDDetailHis = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Qty", "Price", "Charge Type", "Amount"};
    private AbstractDataAccess dao;
    private ChargeType defaultChargeType;
    private String deletedList;
    private HashMap<Integer, Double> doctFees;
    private JTable parent;
    private String otInvId;
    private String vouStatus;
    private boolean canEdit = true;
    private int maxUniqueId = 0;
    private String delDrFee = "";
    private int autoAddCnt = 0;
    private CalculateObserver calObserver;
    
    public OTTableModel(AbstractDataAccess dao) {
        this.dao = dao;
        try {
            defaultChargeType = (ChargeType) dao.find(ChargeType.class, 1);
        } catch (Exception ex) {
            log.error("OTTableModel : " + ex.getMessage());
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

        OTDetailHis record = listOPDDetailHis.get(row);
        boolean isAlreadyP = isAlreadyPay(record);

        if (column == 1 || column == 3 || column == 8) {
            if (column == 3) {
                if (record.getService() == null) {
                    return false;
                } else if (vouStatus.equals("EDIT")) {
                    if (Util1.hashPrivilege("OTVoucherEditChange")) {
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
            if (isAlreadyP) {
                return !isAlreadyP;
            }
            if (canEdit) {
                return canEdit;
            } else {
                return Util1.hashPrivilege("OTVoucherEditChange");
            }
        } else if (isAlreadyP) {
            return !isAlreadyP;
        } else {
            if (column == 0) {
                if (record.getListOTDF() == null) {
                    return true;
                } else if (record.getListOTDF().isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } //}
    }

    public void setVouStatus(String vouStatus) {
        this.vouStatus = vouStatus;
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
        OTDetailHis record = listOPDDetailHis.get(row);
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
                    if (record.getListOTDF() != null) {
                        if (!record.getListOTDF().isEmpty()) {
                            OTDoctorFee otdf = record.getListOTDF().get(0);
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
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OTDetailHis record = listOPDDetailHis.get(row);
        int serviceId = -1;
        try {
            switch (column) {
                case 0: //Code
                    if (value != null) {
                        if (value instanceof OTProcedure) {
                            OTProcedure service = (OTProcedure) value;
                            OTProcedure oldService = record.getService();
                            String tranLogId = Util1.getPropValue("system.ot.tran.log.id");
                            if (oldService != null) {
                                if (oldService.getGroupId().toString().equals(tranLogId)) {
                                    saveTranLog(service.getServiceId(),
                                            "",
                                            "Service replace. Old Service : " + oldService.getServiceName()
                                            + " to New Service : " + service.getServiceName());
                                }
                            }
                            record.setService(service);
                            record.setQuantity(1);
                            record.setPrice(service.getSrvFees());
                            record.setSrvFee1(service.getSrvFees1());
                            record.setSrvFee2(service.getSrvFees2());
                            record.setSrvFee3(service.getSrvFees3());
                            record.setSrvFee4(service.getSrvFees4());
                            record.setSrvFee5(service.getSrvFees5());
                            if (record.getListOTDF() != null) {
                                if (!record.getListOTDF().isEmpty()) {
                                    if (delDrFee.isEmpty()) {
                                        delDrFee = "'" + record.getOpdDetailId() + "'";
                                    } else {
                                        delDrFee = delDrFee + ",'" + record.getOpdDetailId() + "'";
                                    }
                                }
                            }
                            record.setListOTDF(null);
                            record.setChargeType(defaultChargeType);
                            serviceId = service.getServiceId();
                            boolean status = isNeedDetail(serviceId);
                            if (status) {
                                doctorFeePopup(record);
                            }
                            addAutoService(serviceId);
                            if (status) {
                                parent.setColumnSelectionInterval(0, 0);
                            } else {
                                parent.setColumnSelectionInterval(3, 3);
                            }
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
                        record.setQuantity(NumberUtil.NZeroInt(value));
                    } else {
                    }
                    break;
                case 3: //Price
                    if (NumberUtil.isNumber(value)) {
                        record.setPrice(NumberUtil.NZero(value));
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
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.toString());
        }

        calculateAmount(row);
        //fireTableCellUpdated(row, 2);
        //fireTableCellUpdated(row, 3);
        fireTableCellUpdated(row, column);
        if (serviceId == 111 || serviceId == 962 || serviceId == 581) {
            /*if ((getRowCount() - 1) > row) {
             parent.setRowSelectionInterval(row + 1, row + 1);
             }*/
            try {
                parent.setRowSelectionInterval(getRowCount() - 1, getRowCount() - 1);
            } catch (Exception ex) {

            }
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

    public List<OTDetailHis> getListOPDDetailHis() {
        String strSql = "SELECT * FROM com.cv.app.ot.database.entity.OTDetailHis"
                + " WHERE service IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listOPDDetailHis);
    }

    public void setListOPDDetailHis(List<OTDetailHis> listOPDDetailHis) {
        this.listOPDDetailHis = listOPDDetailHis;
        if (listOPDDetailHis != null) {
            if (!listOPDDetailHis.isEmpty()) {
                OTDetailHis tmpD = listOPDDetailHis.get(listOPDDetailHis.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
        }
        addNewRow();
        fireTableDataChanged();
    }

    public OTDetailHis getOPDDetailHis(int row) {
        return listOPDDetailHis.get(row);
    }

    public void setOPDDetailHis(int row, OTDetailHis oPDDetailHis) {
        listOPDDetailHis.set(row, oPDDetailHis);
        fireTableRowsUpdated(row, row);
    }

    public void addOPDDetailHis(OTDetailHis oPDDetailHis) {
        listOPDDetailHis.add(oPDDetailHis);
        fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
    }

    public void deleteOPDDetailHis(int row) {
        OTDetailHis opdh = listOPDDetailHis.get(row);
        if (isAlreadyPay(opdh)) {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Doctor payment already made. You cannot delete this service.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }
        OTProcedure service = opdh.getService();
        String tranLogId = Util1.getPropValue("system.ot.tran.log.id");
        if (service.getGroupId().toString().equals(tranLogId)) {
            saveTranLog(opdh.getService().getServiceId(), "", "Service delete : " + service.getServiceName());
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
        
        calObserver.calculate();
    }

    public void addNewRow() {
        int count = listOPDDetailHis.size();
        if (count == 0 || listOPDDetailHis.get(count - 1).getService() != null) {
            listOPDDetailHis.add(new OTDetailHis());
            fireTableRowsInserted(listOPDDetailHis.size() - 1, listOPDDetailHis.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    private void calculateAmount(int row) {
        OTDetailHis record = listOPDDetailHis.get(row);
        Double amount = null;

        if (record.getChargeType() != null) {
            if (record.getChargeType().getChargeTypeId() == 1) {
                amount = NumberUtil.NZeroInt(record.getQuantity())
                        * NumberUtil.NZero(record.getPrice());
            }
        }

        record.setAmount(amount);
    }

    private void calculateAmount(OTDetailHis record){
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
        int dcDepositId = NumberUtil.NZeroInt(Util1.getPropValue("system.ot.deposite.id"));
        int dcDiscountId = NumberUtil.NZeroInt(Util1.getPropValue("system.ot.disc.id"));
        int dcPaidId = NumberUtil.NZeroInt(Util1.getPropValue("system.ot.paid.id"));
        int dcRefundId = NumberUtil.NZeroInt(Util1.getPropValue("system.ot.refund.id"));

        double total = 0;
        if (listOPDDetailHis != null) {
            for (OTDetailHis oth : listOPDDetailHis) {
                if (oth.getService() != null) {
                    int serviceId = oth.getService().getServiceId();
                    if (serviceId != dcDepositId
                            && serviceId != dcDiscountId
                            && serviceId != dcPaidId
                            && serviceId != dcRefundId) {
                        calculateAmount(oth);
                        total += NumberUtil.NZero(oth.getAmount());
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
        autoAddCnt = 0;
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
            strSQL = "delete from ot_details_his where ot_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;
        int entryRow = 0;
        for (OTDetailHis opdh : listOPDDetailHis) {
            if (status && opdh.getService() != null) {
                /*if (NumberUtil.NZeroInt(opdh.getQuantity()) <= 0) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                            "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                } else*/ if (NumberUtil.NZero(opdh.getPrice()) <= 0) {
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

    public HashMap<Integer, Double> getDoctFees() {
        return doctFees;
    }

    public void setDoctFees(HashMap<Integer, Double> doctFees) {
        this.doctFees = doctFees;
    }

    public int getTotalRecord() {
        return listOPDDetailHis.size();
    }

    public void setOtInvId(String otInvId) {
        this.otInvId = otInvId;
    }

    private void doctorFeePopup(OTDetailHis record) {
        List<OTDoctorFee> listDrFee = record.getListOTDF();
        if (listDrFee == null) {
            try {
                listDrFee = dao.findAllHSQL(
                        "select o from OTDoctorFee o where o.otDetailId = '"
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
        OTDoctorFeeDialog dialog = new OTDoctorFeeDialog(listDrFee, record.getService().getServiceId());
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        record.setListOTDF(dialog.getEntryDrFee());
        record.setPrice(dialog.getTotal());
    }

    private void doctorFeeAssign(OTDetailHis record) {
        if (record.getService() != null) {
            if (isNeedDetail(record.getService().getServiceId())) {
                List<OTDoctorFee> listDrFee = record.getListOTDF();
                if (listDrFee == null) {
                    try {
                        listDrFee = dao.findAllHSQL(
                                "select o from OTDoctorFee o where o.otDetailId = '"
                                + record.getOpdDetailId() + "' order by o.uniqueId");
                        record.setListOTDF(listDrFee);
                    } catch (Exception ex) {
                        log.error("doctorFeePopup : " + ex.toString());
                    } finally {
                        dao.close();
                    }
                }
            }
        }
    }

    private boolean isNeedDetail(int serviceId) {
        boolean status = false;
        try {
            List<DrDetailId> listDDI = dao.findAllHSQL(
                    "select o from DrDetailId o where o.key.option = 'OT' and o.key.serviceId = " + serviceId);
            if (listDDI != null) {
                if (!listDDI.isEmpty()) {
                    status = true;
                }
            }
        } catch (Exception ex) {
            log.error("isNeedDetail : " + ex.toString());
        } finally {
            dao.close();
        }
        return status;
    }

    private void addAutoService(int serviceId) {
        String strSql = "select o from OTProcedure o where o.serviceId in "
                + "(select a.key.addServiceId from AutoAddIdMapping a where a.key.tranOption = 'OT' "
                + "and a.key.tranServiceId = " + serviceId + ")";

        try {
            List<OTProcedure> listSrv = dao.findAllHSQL(strSql);
            List<OTDetailHis> listTmp = new ArrayList();
            Collections.copy(listOPDDetailHis, listTmp);

            if (listSrv != null) {
                if (!listSrv.isEmpty()) {
                    /*if(autoAddCnt >= 1){
                        JOptionPane.showMessageDialog(Util1.getParent(), "Auto add conflit.",
                        "Duplicate Auto Add", JOptionPane.ERROR_MESSAGE);
                        log.error("Duplicate Auto Add : " + serviceId);
                        return;
                    }*/
                    autoAddCnt++;
                    //saveTranLog(serviceId, "", "Auto service add.");
                    for (OTProcedure srv : listSrv) {
                        OTDetailHis record = new OTDetailHis();
                        record.setService(srv);
                        String tranLogId = Util1.getPropValue("system.ot.tran.log.id");
                        if (srv.getGroupId().toString().equals(tranLogId)) {
                            saveTranLog(srv.getServiceId(), "", "Auto add service : " + srv.getServiceName());
                        }
                        List<VAddService> listAdd = dao.findAllHSQL("select o from VAddService o where o.addServiceId = '"
                                + srv.getServiceId() + "' and o.tranOption = 'OT' and o.tranServiceId = " + serviceId);
                        if (!listAdd.isEmpty()) {
                            record.setQuantity(listAdd.get(0).getAddQty());
                        } else {
                            record.setQuantity(1);
                        }
                        record.setPrice(srv.getSrvFees());
                        record.setSrvFee1(srv.getSrvFees1());
                        record.setSrvFee2(srv.getSrvFees2());
                        record.setSrvFee3(srv.getSrvFees3());
                        record.setSrvFee4(srv.getSrvFees4());
                        record.setSrvFee5(srv.getSrvFees5());
                        record.setChargeType(defaultChargeType);

                        Double amount = null;
                        if (record.getChargeType() != null) {
                            if (record.getChargeType().getChargeTypeId() == 1) {
                                amount = NumberUtil.NZeroInt(record.getQuantity()) * NumberUtil.NZero(record.getPrice());
                            }
                        }
                        record.setAmount(amount);
                        listTmp.add(record);
                        parent.getCellRect(parent.getRowCount() - 1, 0, true);
                    }
                    listOPDDetailHis = listTmp;
                }
            }
        } catch (Exception ex) {
            log.error("addAutoService : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void showDoctorDetail(int index) {
        OTDetailHis record = listOPDDetailHis.get(index);
        if (!isAlreadyPay(record)) {
            if (record != null) {
                if (record.getService() != null) {
                    if (isNeedDetail(record.getService().getServiceId())) {
                        doctorFeePopup(record);
                        calculateAmount(index);
                        fireTableCellUpdated(index, 5);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Doctor payment already made. You cannot edit this service.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public void setDoctorFees(int serviceId, double fees) {
        String strSql = "SELECT * FROM com.cv.app.ot.database.entity.OTDetailHis"
                + " WHERE service.serviceId = " + serviceId;
        List<OTDetailHis> list = JoSQLUtil.getResult(strSql, listOPDDetailHis);

        if (list.size() > 0) {
            OTDetailHis odh = list.get(0);
            odh.setPrice(fees);
            odh.setAmount(odh.getQuantity() * odh.getPrice());
        }
    }

    public String getDelDrFee() {
        return delDrFee;
    }

    public void setDelDrFee(String delDrFee) {
        this.delDrFee = delDrFee;
    }

    private void saveTranLog(Integer serviceId, String serviceName, String tranDesp) {
        if (Util1.getPropValue("system.ot.tran.log").equals("Y")) {
            try {
                OTEntryTranLog otetl = new OTEntryTranLog();
                otetl.setServiceId(serviceId);
                otetl.setServiceName(serviceName);
                otetl.setTranDesp(tranDesp);
                otetl.setVouNo(otInvId);
                otetl.setTranDate(new Date());
                otetl.setUserId(Global.loginUser.getUserId());
                dao.save(otetl);
                log.info("saveTranLog : Save");
            } catch (Exception ex) {
                log.error("saveTranLog : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    public boolean isPayAlready() {
        boolean status = false;
        for (OTDetailHis record : listOPDDetailHis) {
            if (record.getListOTDF() != null) {
                if (!record.getListOTDF().isEmpty()) {
                    OTDoctorFee df = record.getListOTDF().get(0);
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

    private boolean isAlreadyPay(OTDetailHis record) {
        boolean status = false;
        List<OTDoctorFee> listOTDF = record.getListOTDF();

        if (listOTDF != null) {
            for (OTDoctorFee df : listOTDF) {
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
