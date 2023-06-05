/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.GenExpense;
import com.cv.app.pharmacy.database.view.VGenExpense;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ExpenseEntryTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ExpenseEntryTableModel.class.getName());
    private List<VGenExpense> listGenExpense = new ArrayList();
    private final String[] columnNames = {"Date", "Description", "Remark", "Exp-Type",
        "Location", "Lock", "Lock Time", "DR-Amount", "CR-Amount"};
    private final AbstractDataAccess dao;
    private String strDate;
    private String expType = "GENERAL_EXPENSE";

    public ExpenseEntryTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        /*if (expType.equals("GENERAL_EXPENSE")) {
            return true;
        } else {
            return false;
        }*/
 /*VGenExpense record = listGenExpense.get(row);
        if(!record.getRecLock()){
            if(column == 7){
                return true;
            }
        }*/
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Description
                return String.class;
            case 2: //Remark
                return String.class;
            case 3: //Expense Type
                return Object.class;
            case 4: //Location
                return Object.class;
            case 5: //Lock
                return Boolean.class;
            case 6: //Lock Time
                return Date.class;
            case 7: //DR-Amount
                return Double.class;
            case 8: //CR-Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VGenExpense record = listGenExpense.get(row);

        switch (column) {
            case 0: //Date
                return record.getExpnDate();
            case 1: //Description
                return record.getDesp();
            case 2: //Remark
                return record.getRemark();
            case 3: //Expense Type
                return record.getExpType();
            case 4: //Location
                return record.getLocation();
            case 5: //Locak
                return record.getRecLock();
            case 6: //Lock Time
                if (record.getDeleted()) {
                    record.getUpdatedDate();
                } else {
                    return record.getLockDT();
                }
            case 7: //DR-Amount
                return record.getDrAmt();
            case 8: //CR-Amount
                return record.getExpAmt();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        /*VGenExpense record = listGenExpense.get(row);

        switch (column) {
            case 0: //Date
                if (DateUtil.isValidDate(value)) {
                    record.setExpnDate(DateUtil.toDate(value));
                } else {
                    record.setExpnDate(null);
                }
                break;
            case 1: //Description
                if (value != null) {
                    record.setDesp(value.toString());
                    //addNewRow();
                } else {
                    record.setDesp(null);
                }
                break;
            case 2: //Remark
                if (value != null) {
                    record.setRemark(value.toString());
                } else {
                    record.setRemark(null);
                }
                break;
            case 3: //Expense Type
                if (value != null) {
                    record.setExpType((ExpenseType) value);
                } else {
                    record.setExpType(null);
                }
                break;
            case 4: //Location
                if (value != null) {
                    record.setLocation((Location) value);
                } else {
                    record.setLocation(null);
                }
                break;
            case 5: //DR-Amount
                record.setDrAmt(NumberUtil.NZero(value));
                break;
            case 6: //CR-Amount
                record.setExpAmt(NumberUtil.NZero(value));
                break;
        }

        //saveData(record);
        fireTableCellUpdated(row, column);*/
    }

    @Override
    public int getRowCount() {
        return listGenExpense.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VGenExpense> getListGenExpense() {
        return listGenExpense;
    }

    public void setListGenExpense(List<VGenExpense> listGenExpense) {
        this.listGenExpense = listGenExpense;
        fireTableDataChanged();
    }

    public VGenExpense getGenExpense(int row) {
        return listGenExpense.get(row);
    }

    public void deleteGenExpense(int row) {
        VGenExpense ge = listGenExpense.get(row);
        Date vouSaleDate = ge.getExpnDate();
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ge.getDeleted()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Record is already deleted.",
                    "Expense Delete.", JOptionPane.ERROR_MESSAGE);
        } else if (ge.getRecLock()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "You cannot delete payment. Record is locked.",
                    "Expense Delete.", JOptionPane.ERROR_MESSAGE);
        } else {
            if (ge.getTranType().equals("GENERAL_EXPENSE")) {
                try {
                    List<GenExpense> listGE = dao.findAllHSQL("select o from \n"
                            + "GenExpense o where o.vouNo = '" + ge.getVouNo() + "'\n"
                            + " and o.expenseOpotion = '" + ge.getExpenseOption() + "'");
                    if (listGE != null) {
                        dao.open();
                        dao.beginTran();
                        if (!listGE.isEmpty()) {
                            String deleteId = "";
                            for (GenExpense tmpGE : listGE) {
                                if (tmpGE.getGeneId() != null) {
                                    if (deleteId.isEmpty()) {
                                        deleteId = tmpGE.getGeneId().toString();
                                    } else {
                                        deleteId = deleteId + "," + tmpGE.getGeneId().toString();
                                    }
                                }
                                tmpGE.setDeleted(true);
                                tmpGE.setUpdatedBy(Global.loginUser.getUserId());
                                tmpGE.setUpdatedDate(new Date());
                                dao.save1(tmpGE);
                            }

                            if (!deleteId.isEmpty()) {
                                String strSql = "insert into gen_expense_log(gene_id, exp_amount, desp, exp_date, created_by, created_date,\n"
                                        + "  updated_by, updated_date, remark, session_id, exp_type, dr_amt, location_id, intg_upd_status,\n"
                                        + "  vou_no, source_acc_id, acc_id, dept_id, paid_for, expense_option, doctor_id, deleted,\n"
                                        + "  machine_id, action_user, action_name, action_time, lock_dt, currency_id)\n"
                                        + "select gene_id, exp_amount, desp, exp_date, created_by, created_date,\n"
                                        + "  updated_by, updated_date, remark, session_id, exp_type, dr_amt, location_id, intg_upd_status,\n"
                                        + "  vou_no, source_acc_id, acc_id, dept_id, paid_for, expense_option, doctor_id, deleted,\n"
                                        + "  " + Global.machineId + ",'" + Global.loginUser.getUserId() + "','DELETE','"
                                        + DateUtil.getTodayDateTimeStrMYSQL() + "', lock_dt, currency_id \n"
                                        + "from gen_expense\n"
                                        + "where gene_id in (" + deleteId + ")";
                                dao.execSqlT(strSql);
                            }
                        }
                        dao.commit();
                    }
                    //dao.open();
                    //String strSql = "delete from gen_expense where gene_id = " + ge.getExpId();
                    //dao.execSql(strSql);
                    listGenExpense.remove(row);
                    fireTableRowsDeleted(0, listGenExpense.size() - 1);
                    uploadToAccount(ge.getExpId().toString());
                } catch (Exception ex) {
                    log.error("deleteGenExpense : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    dao.rollBack();
                } finally {
                    dao.close();
                }

                updateOption(ge);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "You cannot delete voucher expense.",
                        "Expense Delete.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try (CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/expense";
                    final HttpPost request = new HttpPost(url);
                    final List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("vouNo", vouNo));
                    request.setEntity(new UrlEncodedFormEntity(params));
                    CloseableHttpResponse response = httpClient.execute(request);
                    log.info(url + response.toString());
                } catch (IOException e) {
                    log.error("uploadToAccount : " + e.getMessage());
                    updateNull(vouNo);
                }
            }
        } else {
            updateNull(vouNo);
        }
    }

    private void updateNull(String vouNo) {
        try {
            dao.execSql("update gen_expense set intg_upd_status = null where gene_id = '" + vouNo + "'");
        } catch (Exception ex) {
            log.error("uploadToAccount error : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private CloseableHttpClient createHttpClientWithTimeouts() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ONE_MILLISECOND)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private void updateOption(VGenExpense ge) {
        ExpenseType et = ge.getExpType();
        String vouNo = ge.getVouNo();
        String expOption = et.getExpenseOption();

        if (vouNo != null) {
            try {
                dao.open();
                dao.beginTran();
                String strSql = "-";
                //String strSql = "delete from gen_expense where vou_no = '" + vouNo + "'";
                //dao.execSqlT(strSql);
                switch (expOption) {
                    case "Pharmacy":
                        break;
                    case "OPD":
                        String sysCode = et.getSysCode();
                        switch (sysCode) {
                            case "OPDCF": //CF Fee Payment
                                strSql = "update opd_details_his set fee1_id = null where fee1_id = '" + vouNo + "'";
                                break;
                            case "OPDREFER": //Refer Fee Payment
                                strSql = "update opd_details_his set fee5_id = null where fee5_id = '" + vouNo + "'";
                                break;
                            case "OPDREAD": //Reader Fee Payment
                                strSql = "update opd_details_his set fee6_id = null where fee6_id = '" + vouNo + "'";
                                break;
                            case "OPDMO": //MO Fee Payment
                                strSql = "update opd_details_his set fee2_id = null where fee2_id = '" + vouNo + "'";
                                break;
                            case "OPDTECH": //Tech Fee Payment
                                strSql = "update opd_details_his set fee4_id = null where fee4_id = '" + vouNo + "'";
                                break;
                            case "OPDSTAFF": //Staff Fee Payment
                                strSql = "update opd_details_his set fee3_id = null where fee3_id = '" + vouNo + "'";
                                break;
                        }
                        dao.execSqlT(strSql);
                        break;
                    case "OT":
                        sysCode = et.getSysCode();
                        switch (sysCode) {
                            case "OTDR": //OT Doctor Payment
                                if (et.getNeedDr()) {
                                    strSql = "update ot_doctor_fee set pay_id = null, pay_amt = null where pay_id = '" + vouNo + "'";
                                } else {
                                    strSql = "update ot_details_his set pay_id1 = null, fee1_pay_amt = null where pay_id1 = '" + vouNo + "'";
                                }
                                break;
                            case "OTSTAFF": //DC Staff Payment
                                strSql = "update ot_details_his set pay_id2 = null, fee2_pay_amt = null where pay_id2 = '" + vouNo + "'";
                                break;
                            case "OTNURSE": //DC Nurse Payment
                                strSql = "update ot_details_his set pay_id3 = null, fee3_pay_amt = null where pay_id3 = '" + vouNo + "'";
                                break;
                            case "OTMO": //OT MO Payment
                                strSql = "update ot_details_his set pay_id4 = null, fee4_pay_amt = null where pay_id4 = '" + vouNo + "'";
                                break;
                        }
                        dao.execSqlT(strSql);
                        break;
                    case "DC":
                        sysCode = et.getSysCode();
                        switch (sysCode) {
                            case "DCDR": //DC Doctor Fee Payment
                                if (et.getNeedDr()) {
                                    strSql = "update dc_doctor_fee set pay_id = null,pay_amt = null where pay_id = '" + vouNo + "'";
                                } else {
                                    strSql = "update dc_details_his set pay_id1 = null, fee2_pay_amt = null where pay_id1 = '" + vouNo + "'";
                                }
                                break;
                            case "DCNURSE":
                                strSql = "update dc_details_his set pay_id2 = null, fee2_pay_amt = null where pay_id2 = '" + vouNo + "'";
                                break;
                            case "DCTECH":
                                strSql = "update dc_details_his set pay_id3 = null, fee3_pay_amt = null where pay_id3 = '" + vouNo + "'";
                                break;
                            case "DCMO":
                                strSql = "update dc_details_his set pay_id4 = null, fee4_pay_amt = null where pay_id4 = '" + vouNo + "'";
                                break;
                        }
                        dao.execSqlT(strSql);
                        break;
                }
                dao.commit();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("updateOption : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    public void addNewRow() {
        if (listGenExpense.isEmpty()) {
            VGenExpense ge = new VGenExpense();
            ge.setExpnDate(DateUtil.toDate(strDate));
            ge.setTranType("GENERAL_EXPENSE");
            listGenExpense.add(ge);
            fireTableRowsInserted(listGenExpense.size() - 1, listGenExpense.size() - 1);
        } else {
            VGenExpense ge = listGenExpense.get(listGenExpense.size() - 1);
            if (ge.getDesp() != null) {
                VGenExpense ge1 = new VGenExpense();
                ge1.setExpnDate(DateUtil.toDate(strDate));
                ge1.setTranType("GENERAL_EXPENSE");
                listGenExpense.add(ge1);
                fireTableRowsInserted(listGenExpense.size() - 1, listGenExpense.size() - 1);
            }
        }
    }

    public Double getTotalAmount() {
        double total = 0;

        for (VGenExpense ge : listGenExpense) {
            total += NumberUtil.NZero(ge.getExpAmt());
        }

        return total;
    }

    public Double getTotalAmountDR() {
        double total = 0;

        for (VGenExpense ge : listGenExpense) {
            total += NumberUtil.NZero(ge.getDrAmt());
        }

        return total;
    }

    public void saveData(VGenExpense ge) {
        if (ge.getTranType().equals("GENERAL_EXPENSE")) {
            try {
                if (ge.getDesp() != null
                        && (NumberUtil.NZero(ge.getExpAmt()) != 0
                        || NumberUtil.NZero(ge.getDrAmt()) != 0)
                        && ge.getExpnDate() != null) {
                    GenExpense rec;

                    if (ge.getExpId() == null) {
                        rec = new GenExpense();
                        rec.setCreatedBy(Global.loginUser.getUserId());
                        rec.setSession(Global.sessionId);
                    } else {
                        rec = (GenExpense) dao.find(GenExpense.class, NumberUtil.NZeroInt(ge.getExpId()));
                        rec.setUpdatedBy(Global.loginUser.getUserId());
                        rec.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
                    }

                    rec.setAmount(ge.getExpAmt());
                    rec.setDesp(ge.getDesp());
                    rec.setExpDate(ge.getExpnDate());
                    rec.setExpType(ge.getExpType());
                    rec.setRemark(ge.getRemark());
                    rec.setLocation(ge.getLocation());
                    rec.setDrAmt(ge.getDrAmt());
                    dao.open();
                    dao.save(rec);
                    ge.setExpId(rec.getGeneId());
                }
            } catch (Exception ex) {
                log.error("saveData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    public void setDate(String strDate) {
        this.strDate = strDate;

        if (!listGenExpense.isEmpty()) {
            int row = listGenExpense.size() - 1;
            int col = 0;

            setValueAt(strDate, row, col);
        }
    }

    public void setSelectRow(int row) {
        if (row != -1) {
            expType = listGenExpense.get(row).getTranType();
        }
    }
}
