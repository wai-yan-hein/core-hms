/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.common.SessionCheckTableModel;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.SessionFilter;
import com.cv.app.pharmacy.database.helper.SessionTtl;
import com.cv.app.pharmacy.ui.common.SessionTotalTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class SessionCheckOPD extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(SessionCheckOPD.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final SessionCheckTableModel tableSCModel = new SessionCheckTableModel();
    private final SessionTotalTableModel sTableModel = new SessionTotalTableModel();
    private TableRowSorter<TableModel> sorter;
    private PaymentType ptCash;

    /**
     * Creates new form SessionCheck
     */
    public SessionCheckOPD() {
        initComponents();
        assignDate();
        initCombo();
        initTable();
        try {
            ptCash = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
            cboPayment.setSelectedItem(ptCash);
        } catch (Exception ex) {
            log.error("SessionCheckOPD : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void assignDate() {
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
            BindingUtil.BindComboFilter(cboCurrency, dao.findAll("Currency"));
            BindingUtil.BindComboFilter(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindComboFilter(cboUser, dao.findAll("Appuser"));

            new ComBoBoxAutoComplete(cboSession);
            new ComBoBoxAutoComplete(cboCurrency);
            new ComBoBoxAutoComplete(cboPayment);
            new ComBoBoxAutoComplete(cboUser);
            new ComBoBoxAutoComplete(cboTranType);

            cboSession.setSelectedIndex(0);
            cboCurrency.setSelectedIndex(0);
            cboPayment.setSelectedIndex(0);
            cboUser.setSelectedIndex(0);
            cboTranType.setSelectedIndex(0);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        sorter = new TableRowSorter(tblSession.getModel());
        tblSession.setRowSorter(sorter);
        tblSession.getTableHeader().setFont(Global.lableFont);
        tblSession.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblSession.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblSession.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblSession.getColumnModel().getColumn(3).setPreferredWidth(170);
        tblSession.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblSession.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblSession.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblSession.getColumnModel().getColumn(7).setPreferredWidth(50);
        tblSession.getColumnModel().getColumn(8).setPreferredWidth(50);
        tblSession.getColumnModel().getColumn(9).setPreferredWidth(60);
        tblSession.getColumnModel().getColumn(10).setPreferredWidth(15);
        tblSession.getColumnModel().getColumn(11).setPreferredWidth(15);
        tblSession.getColumnModel().getColumn(12).setPreferredWidth(15);
        tblSession.getColumnModel().getColumn(13).setPreferredWidth(15);

        tblTotal.getTableHeader().setFont(Global.lableFont);
        tblTotal.getColumnModel().getColumn(0).setPreferredWidth(100);//Desp
        tblTotal.getColumnModel().getColumn(1).setPreferredWidth(7);//Currency
        tblTotal.getColumnModel().getColumn(2).setPreferredWidth(40);//Amount
    }

    private String getHSQL() {
        String strSql = "select s from VSessionClinic s where date(s.tranDate) between '"
                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";
        int session = getSessionFilter();

        if (session != 0) {
            strSql = strSql + " and s.session = " + session;
        }

        return strSql;
    }

    private String getFilterString() {
        String filterString = "SELECT * FROM com.cv.app.opd.database.view.VSessionClinic ";
        String strWhere = "";
        String tmpStrValue;
        int tmpIntValue;
        String sqlFilter = "";

        tmpStrValue = getUserFilter();
        if (!tmpStrValue.equals("All")) {
            if (strWhere.isEmpty()) {
                strWhere = " user = '" + tmpStrValue + "'";
                sqlFilter = "user_id = '" + tmpStrValue + "'";
            } else {
                strWhere = strWhere + " and user = '" + tmpStrValue + "'";
                sqlFilter = sqlFilter + " and user_id = '" + tmpStrValue + "'";
            }
        }

        if (strWhere.isEmpty()) {
            strWhere = " deleted = " + getDeletedFilter();
            sqlFilter = "deleted = " + getDeletedFilter();
        } else {
            strWhere = strWhere + " and deleted = " + getDeletedFilter();
            sqlFilter = sqlFilter + " and deleted = " + getDeletedFilter();
        }

        tmpStrValue = getCurrencyFilter();
        if (!tmpStrValue.equals("All")) {
            if (strWhere.isEmpty()) {
                strWhere = " currency = '" + tmpStrValue + "'";
                sqlFilter = "currency_id = '" + tmpStrValue + "'";
            } else {
                strWhere = strWhere + " and currency = '" + tmpStrValue + "'";
                sqlFilter = sqlFilter + " and currency_id = '" + tmpStrValue + "'";
            }
        }

        tmpIntValue = getPaymentFilter();
        if (tmpIntValue != 0) {
            if (strWhere.isEmpty()) {
                if (tmpIntValue == 1) {//For cash
                    strWhere = "paid <> 0";
                    sqlFilter = "paid <> 0";
                } else {
                    strWhere = " payType = " + tmpIntValue;
                    sqlFilter = "payment_type_id = " + tmpIntValue;
                }
            } else if (tmpIntValue == 1) {//For cash
                strWhere = strWhere + " and paid <> 0";
                sqlFilter = sqlFilter + " and paid <> 0";
            } else {
                strWhere = strWhere + " and payType = " + tmpIntValue;
                sqlFilter = sqlFilter + " and payment_type_id = " + tmpIntValue;
            }
        }

        if (!txtPtNo.getText().trim().isEmpty()) {
            if (strWhere.isEmpty()) {
                strWhere = " ptId = '" + txtPtNo.getText().trim() + "'";
                sqlFilter = "patient_id = '" + txtPtNo.getText().trim() + "'";
            } else {
                strWhere = strWhere + " and ptId = '" + txtPtNo.getText().trim() + "'";
                sqlFilter = sqlFilter + " and patient_id = '" + txtPtNo.getText().trim() + "'";
            }
        }

        if (!txtPtName.getText().trim().isEmpty()) {
            if (strWhere.isEmpty()) {
                strWhere = " ptName like '%" + txtPtName.getText() + "%'";
            } else {
                strWhere = strWhere + " and ptName like '%" + txtPtName.getText() + "%'";
            }
        }

        if (!txtDrNo.getText().trim().isEmpty()) {
            if (strWhere.isEmpty()) {
                strWhere = " drId = '" + txtDrNo.getText().trim() + "'";
                sqlFilter = "doctor_id = '" + txtDrNo.getText().trim() + "'";
            } else {
                strWhere = strWhere + " and drId = '" + txtDrNo.getText().trim() + "'";
                sqlFilter = sqlFilter + " and doctor_id = '" + txtDrNo.getText().trim() + "'";
            }
        }

        String strSUBGFilter = sqlFilter;
        String tranType = cboTranType.getSelectedItem().toString();
        switch (tranType) {
            case "OPD":
                if (strWhere.isEmpty()) {
                    strWhere = " tranOption = 'OPD' ";
                    sqlFilter = "tran_option = 'OPD'";
                } else {
                    strWhere = strWhere + " and tranOption = 'OPD' ";
                    sqlFilter = sqlFilter + " and tran_option = 'OPD'";
                }
                break;
            case "OT":
                if (strWhere.isEmpty()) {
                    strWhere = " tranOption = 'OT' ";
                    sqlFilter = "tran_option = 'OT'";
                } else {
                    strWhere = strWhere + " and tranOption = 'OT' ";
                    sqlFilter = sqlFilter + " and tran_option = 'OT'";
                }
                break;
            case "DC":
                if (strWhere.isEmpty()) {
                    sqlFilter = "tran_option = 'DC'";
                    strWhere = " tranOption = 'DC' ";
                } else {
                    strWhere = strWhere + " and tranOption = 'DC' ";
                    sqlFilter = sqlFilter + " and tran_option = 'DC'";
                }
                break;
        }

        //Calculate total in sql
        int session = getSessionFilter();

        String userId = Global.machineId;
        String strSql = "insert into tmp_clinic_session_check(user_id,\n"
                + "  tran_option,curr_id,ttl_v,ttl_dics,ttl_tax,\n"
                + "  ttl_paid,ttl_deposite,ttl_credit)\n"
                + "select '" + userId + "',tran_option, currency_id, sum(vou_total) ttl_v, "
                + "sum(disc_a) ttl_disc, sum(tax_a) ttl_tax, sum(paid) ttl_paid,\n"
                + "sum(if(vou_balance < 0, vou_balance, 0)) ttl_deposite, sum(if(vou_balance > 0, vou_balance, 0)) ttl_credit\n"
                + "from v_session_clinic\n";

        if (!sqlFilter.isEmpty()) {
            strSql = strSql + " where date(tran_date) between '"
                    + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                    + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and " + sqlFilter;
        } else {
            strSql = strSql + " where date(tran_date) between '"
                    + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                    + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";
        }

        if (session != 0) {
            strSql = strSql + " and session_id = " + session;
        }
        strSql = strSql + " group by tran_option";

        try {
            dao.execSql(
                    "delete from tmp_clinic_session_check where user_id = '" + userId + "'",
                    strSql
            );

            //=========================================================
            String appCurr = Util1.getPropValue("system.app.currency");
            //Substract group
            if (tranType.equals("OPD") || tranType.equals("All")) {
                List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBGOPD'");
                SessionFilter sf = null;
                if (listSF != null) {
                    if (!listSF.isEmpty()) {
                        sf = listSF.get(0);
                    }
                }
                if (sf != null) {
                    String strGroupId = sf.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.machineId + "','SUBGOPD' tran_option, ifnull(currency_id,'"
                                + appCurr + "'), sum(amount)*-1\n"
                                + "from v_opd\n"
                                + "where date(opd_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")";

                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }

                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }

                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBGOPD : " + ex.getMessage());
                        }
                    }
                }
            }

            if (tranType.equals("OT") || tranType.equals("All")) {
                List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBGOT'");
                SessionFilter sf = null;
                if (listSF != null) {
                    if (!listSF.isEmpty()) {
                        sf = listSF.get(0);
                    }
                }
                if (sf != null) {
                    String strGroupId = sf.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.machineId + "','SUBGOT' tran_option, ifnull(currency_id,'"
                                + appCurr + "'), sum(amount)*-1\n"
                                + "from v_ot\n"
                                + "where date(ot_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")";

                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }

                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }

                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBGOT : " + ex.getMessage());
                        }
                    }
                }
            }

            if (tranType.equals("DC") || tranType.equals("All")) {
                List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBGDC'");
                SessionFilter sf = null;
                if (listSF != null) {
                    if (!listSF.isEmpty()) {
                        sf = listSF.get(0);
                    }
                }
                if (sf != null) {
                    String strGroupId = sf.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.machineId + "','SUBGDC' tran_option, ifnull(currency_id,'"
                                + appCurr + "'), sum(amount)*-1\n"
                                + "from v_dc\n"
                                + "where date(dc_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")";

                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }

                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }

                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBGDC : " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getFilterString : " + ex.getMessage());
        }
        //=========================================================
        String appCurr = Util1.getPropValue("system.app.currency");
        //Substract group
        if (tranType.equals("OPD") || tranType.equals("All")) {
            try {
                List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBGOPD'");
                SessionFilter sf = null;
                if (listSF != null) {
                    if (!listSF.isEmpty()) {
                        sf = listSF.get(0);
                    }
                }
                if (sf != null) {
                    String strGroupId = sf.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.loginUser.getUserId() + "','SUBGOPD' tran_option, ifnull(currency_id,'"
                                + appCurr + "'), sum(amount)*-1\n"
                                + "from v_opd\n"
                                + "where date(opd_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")";
                        
                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }
                        
                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }
                        
                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBGOPD : " + ex.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SessionCheckOPD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (tranType.equals("OT") || tranType.equals("All")) {
            try {
                List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBGOT'");
                SessionFilter sf = null;
                if (listSF != null) {
                    if (!listSF.isEmpty()) {
                        sf = listSF.get(0);
                    }
                }
                if (sf != null) {
                    String strGroupId = sf.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.loginUser.getUserId() + "','SUBGOT' tran_option, ifnull(currency_id,'"
                                + appCurr + "'), sum(amount)*-1\n"
                                + "from v_ot\n"
                                + "where date(ot_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")";
                        
                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }
                        
                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }
                        
                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBGOT : " + ex.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SessionCheckOPD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (tranType.equals("DC") || tranType.equals("All")) {
            try {
                List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBGDC'");
                SessionFilter sf = null;
                if (listSF != null) {
                    if (!listSF.isEmpty()) {
                        sf = listSF.get(0);
                    }
                }
                if (sf != null) {
                    String strGroupId = sf.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.loginUser.getUserId() + "','SUBGDC' tran_option, ifnull(currency_id,'"
                                + appCurr + "'), sum(amount)*-1\n"
                                + "from v_dc\n"
                                + "where date(dc_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")";
                        
                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }
                        
                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }
                        
                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBGDC : " + ex.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SessionCheckOPD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //=========================================================

        if (!strWhere.isEmpty()) {
            filterString = filterString + " WHERE " + strWhere;
        }

        return filterString;
    }

    private void applyFilter() {
        String fromDate = DateUtil.toDateStrMYSQL(txtFrom.getText());
        String toDate = DateUtil.toDateStrMYSQL(txtTo.getText());
        String joFilter = getFilterString();
        tableSCModel.applyFilter(joFilter);
        List<SessionTtl> listTtl = new ArrayList();
        try {
            String strSql = "select vstc.*\n"
                    + "from v_sess_ttl_clinic vstc, session_filter sf\n"
                    + "where vstc.tran_option = sf.tran_source\n"
                    + "and vstc.user_id = '" + Global.machineId
                    + "' and sf.program_id in ('CLINICTTL', 'SUBGDC', 'SUBGOPD', 'SUBGOT')\n"
                    + "order by sf.sort_order";
            ResultSet rs = dao.execSQL(strSql);

            if (rs != null) {
                while (rs.next()) {
                    listTtl.add(new SessionTtl(
                            rs.getString("tran_option"),
                            rs.getString("curr_id"),
                            rs.getDouble("ttl")
                    ));
                }
            }
        } catch (SQLException ex) {
            log.error("applyFilter : " + ex.getMessage());
        }
        listTtl.addAll(getBillPayment(fromDate, toDate));
        sTableModel.setListSessionTtl(listTtl);
        System.gc();
        //getTotal();
    }

    private List<SessionTtl> getBillPayment(String fromDate, String toDate) {
        List<SessionTtl> values = new ArrayList<>();
        String sql = "select sum(pay_amt) amt,currency_id\n"
                + "from opd_patient_bill_payment\n"
                + "where date(pay_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "group by currency_id";
        ResultSet rs = dao.execSQL(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    values.add(new SessionTtl(
                            "Total Bill Payment",
                            rs.getString("currency_id"),
                            rs.getDouble("amt")));
                }
            } catch (SQLException ex) {
                log.error(String.format("getBillPayment: %s", ex.getMessage()));
            }
        }
        return values;
    }

    private void search() {
        try {
            tableSCModel.setListVSessionClinic(dao.findAllHSQL(getHSQL()));
            applyFilter();
        } catch (Exception ex) {
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getTotal() {
        String deleteFilter;

        if (getDeletedFilter()) {
            deleteFilter = "1";
        } else {
            deleteFilter = "0";
        }

        try {
            ResultSet resultSet = dao.getPro("session_total_clinic",
                    DateUtil.toDateTimeStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateTimeStrMYSQL(txtTo.getText()),
                    NumberUtil.toChar(getSessionFilter()), getCurrencyFilter(),
                    NumberUtil.toChar(getPaymentFilter()), getUserFilter(),
                    deleteFilter, getPatientFilter(),
                    getDrFilter());

            if (resultSet != null) {
                List<SessionTtl> listTtl = new ArrayList();

                while (resultSet.next()) {
                    SessionTtl sttl = new SessionTtl(resultSet.getString("tran_option"),
                            resultSet.getString("currency_id"), resultSet.getDouble("ttl_amt"));

                    listTtl.add(sttl);
                }

                sTableModel.setListSessionTtl(listTtl);
            } else {
                sTableModel.removeAll();
            }
        } catch (Exception ex) {
            log.error("getTotal : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private String getUserFilter() {
        String userId = "All";

        if (cboUser.getSelectedItem() instanceof Appuser) {
            Appuser user = (Appuser) cboUser.getSelectedItem();
            userId = user.getUserId();
        }

        return userId;
    }

    private boolean getDeletedFilter() {
        boolean deleted = false;

        if (cboDelete.getSelectedItem().toString().equals("Deleted")) {
            deleted = true;
        }

        return deleted;
    }

    private String getCurrencyFilter() {
        String tmpCurrency = "All";

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            tmpCurrency = curr.getCurrencyCode();
        }

        return tmpCurrency;
    }

    private int getPaymentFilter() {
        int tmpPayment = 0;

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();
            tmpPayment = pt.getPaymentTypeId();
        }
        return tmpPayment;
    }

    private int getSessionFilter() {
        int tmpSession = 0;

        if (cboSession.getSelectedItem() instanceof Session) {
            Session se = (Session) cboSession.getSelectedItem();
            tmpSession = se.getSessionId();
        }
        return tmpSession;
    }

    private String getPatientFilter() {
        String tmpPatient = "All";

        if (!txtPtNo.getText().trim().isEmpty()) {
            tmpPatient = txtPtNo.getText();
        }

        return tmpPatient;
    }

    private String getDrFilter() {
        String tmpDr = "All";

        if (!txtDrNo.getText().trim().isEmpty()) {
            tmpDr = txtDrNo.getText();
        }

        return tmpDr;
    }

    private void getPatient() {
        if (txtPtNo.getText() != null && !txtPtNo.getText().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class, txtPtNo.getText());
                dao.close();

                if (pt == null) {
                    txtPtNo.setText(null);
                    txtPtName.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtPtNo.setText(null);
            txtPtName.setText(null);
        }
    }

    private void getDoctor() {
        if (txtDrNo.getText() != null && !txtDrNo.getText().isEmpty()) {
            try {
                Doctor dr;

                dao.open();
                dr = (Doctor) dao.find(Doctor.class, txtDrNo.getText());
                dao.close();

                if (dr == null) {
                    txtDrNo.setText(null);
                    txtDrName.setText(null);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid doctor code.",
                            "Doctor Code", JOptionPane.ERROR_MESSAGE);

                } else {
                    txtDrNo.setText(dr.getDoctorId());
                    txtDrName.setText(dr.getDoctorName());
                }
            } catch (Exception ex) {
                log.error("getDoctor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtDrNo.setText(null);
            txtDrName.setText(null);
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PatientSearch":
                if (selectObj != null) {
                    Patient pt = (Patient) selectObj;
                    txtPtNo.setText(pt.getRegNo());
                    txtPtName.setText(pt.getPatientName());
                    //vouFilter.setTrader(pt);
                } else {
                    txtPtNo.setText(null);
                    txtPtName.setText(null);
                }

                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboSession = new javax.swing.JComboBox();
        butSearch = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPtNo = new javax.swing.JTextField();
        txtDrNo = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        txtDrName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cboUser = new javax.swing.JComboBox();
        butPrint = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        cboDelete = new javax.swing.JComboBox();
        cboTranType = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        butPrintD = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTotal = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSession = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Search"));

        txtTo.setEditable(false);
        txtTo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To ");

        txtFrom.setEditable(false);
        txtFrom.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From ");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Session");

        cboSession.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFrom)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTo)
                                    .addComponent(cboSession, 0, 105, Short.MAX_VALUE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSearch});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSearch)
                    .addComponent(butClear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Currency ");

        cboCurrency.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Payment");

        cboPayment.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Patient");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Doctor");

        txtPtNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtPtNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPtNoActionPerformed(evt);
            }
        });

        txtDrNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDrNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDrNoActionPerformed(evt);
            }
        });

        txtPtName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtPtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPtNameMouseClicked(evt);
            }
        });

        txtDrName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("User");

        cboUser.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butPrint.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Del");

        cboDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboDelete.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Deleted" }));

        cboTranType.setFont(Global.textFont);
        cboTranType.setModel(new javax.swing.DefaultComboBoxModel(
            new String[] { "All", "OPD", "OT",
                "DC"}));
    cboTranType.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboTranTypeActionPerformed(evt);
        }
    });

    jLabel8.setFont(Global.lableFont);
    jLabel8.setText("Tran Type");

    butPrintD.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
    butPrintD.setText("Print D");
    butPrintD.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            butPrintDActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6)
                        .addComponent(jLabel9))
                    .addGap(18, 18, 18)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cboUser, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboPayment, javax.swing.GroupLayout.Alignment.LEADING, 0, 130, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jLabel10)
                    .addGap(18, 18, 18)
                    .addComponent(cboDelete, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel3)
                .addComponent(jLabel4)
                .addComponent(jLabel8))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(1, 1, 1)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cboTranType, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDrNo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtDrName)
                                .addComponent(txtPtName)))))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(butPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(butPrintD)))
            .addContainerGap())
    );

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel5, jLabel6, jLabel9});

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel4, jLabel8});

    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(41, 41, 41)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDrNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDrName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(15, 15, 15)
                    .addComponent(cboTranType)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butPrint)
                        .addComponent(butPrintD))
                    .addGap(1, 1, 1))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(15, 15, 15)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))))
                    .addGap(15, 15, 15)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel8))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(cboDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap(19, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Total"));

    tblTotal.setFont(Global.textFont);
    tblTotal.setModel(sTableModel);
    tblTotal.setRowHeight(23);
    jScrollPane1.setViewportView(tblTotal);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
            .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
    );

    tblSession.setFont(Global.textFont);
    tblSession.setModel(tableSCModel);
    tblSession.setRowHeight(23);
    jScrollPane2.setViewportView(tblSession);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane2))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
            .addContainerGap())
    );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFrom.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFromMouseClicked

    private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTo.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtToMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void cboTranTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTranTypeActionPerformed

    }//GEN-LAST:event_cboTranTypeActionPerformed

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        // TODO add your handling code here:
        print();
    }//GEN-LAST:event_butPrintActionPerformed

    private void txtPtNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPtNoActionPerformed
        getPatient();
    }//GEN-LAST:event_txtPtNoActionPerformed

    private void txtDrNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDrNoActionPerformed
        getDoctor();
    }//GEN-LAST:event_txtDrNoActionPerformed

    private void txtPtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPtNameMouseClicked
        if (evt.getClickCount() == 2) {
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtPtNameMouseClicked

    private void butPrintDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintDActionPerformed
        printSessionD();
    }//GEN-LAST:event_butPrintDActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_butClearActionPerformed

    private void print() {
        //Properties prop = ReportUtil.loadReportPathProperties();
        String rpName = Util1.getPropValue("system.clinic.session.check.print.report");
        rpName = Util1.isNullOrEmpty(rpName) ? "ClinicSessionCheckVoucher" : rpName;
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "Clinic/"
                + rpName;
        Map<String, Object> params = new HashMap();
        String sessionName;
        String sessionId;
        String sessionUser;
        String sessionUserId;
        String sessionDate;
        String sessionCurrency;
        String sessionPaymentId;
        String sessionPaymentName;
        String sessionPtId = "-1";
        String sessionDrId = "-1";
        String sessionDelete = "All";

        if (cboSession.getSelectedItem() instanceof Session) {
            sessionName = ((Session) cboSession.getSelectedItem()).getSessionName();
            sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        } else {
            sessionName = "All";
            sessionId = "-";
        }

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            sessionPaymentName = ((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeName();
            sessionPaymentId = ((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId().toString();
        } else {
            sessionPaymentName = "All";
            sessionPaymentId = "-1";
        }
        String userId;
        if (cboUser.getSelectedItem() instanceof Appuser) {
            userId = ((Appuser) cboUser.getSelectedItem()).getUserId();
        } else {
            userId = "-";
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            sessionCurrency = ((Currency) cboUser.getSelectedItem()).getCurrencyName();
        } else {
            sessionCurrency = "All";
        }

        if (txtFrom.getText().equals(txtTo.getText())) {
            sessionDate = txtFrom.getText();
        } else {
            sessionDate = txtFrom.getText() + " To " + txtTo.getText();
        }

        if (!txtPtNo.getText().isEmpty()) {
            sessionPtId = txtPtName.getText();
        }

        if (!txtDrNo.getText().isEmpty()) {
            sessionDrId = txtDrNo.getText();
        }

        if (cboDelete.getSelectedItem().toString().equals("Normal")) {
            sessionDelete = "false";
        } else if (cboDelete.getSelectedItem().toString().equals("Deleted")) {
            sessionDelete = "true";
        }

        /*String userId = "All";
         String userName = "All";
         if (cboUser.getSelectedItem() instanceof Appuser) {
         Appuser user = (Appuser) cboUser.getSelectedItem();
         userId = user.getUserId();
         userName = user.getUserShortName();
         }*/
        params.put("session_fdate", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("session_tdate", DateUtil.toDateStrMYSQL(txtTo.getText()));
        params.put("session_name", sessionName);
        params.put("session_id", sessionId);
        params.put("session_currency", sessionCurrency);
        params.put("session_paymentname", sessionPaymentName);
        params.put("session_paymentid", sessionPaymentId);
        params.put("user", Global.loginUser.getUserShortName());
        params.put("user_id", Global.machineId);
        params.put("pt_id", sessionPtId);
        params.put("dr_id", sessionDrId);
        params.put("tran_option", cboTranType.getSelectedItem().toString());
        params.put("deleted", sessionDelete);
        params.put("session_date", sessionDate);
        params.put("comp_name", Util1.getPropValue("report.company.name"));
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("data_date", "Between " + txtFrom.getText()
                + " and " + txtTo.getText());
        params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
        params.put("p_user_id", userId);
        params.put("p_user_name", cboUser.getSelectedItem().toString());
        params.put("p_session_name", cboSession.getSelectedItem().toString());
        params.put("sess_id", sessionId);
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
    }

    private void printSessionD() {
        String sessionCurrency;
        if (cboCurrency.getSelectedItem() instanceof Currency) {
            sessionCurrency = ((Currency) cboUser.getSelectedItem()).getCurrencyName();
        } else {
            sessionCurrency = "-";
        }

        int sessionPaymentId;
        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            //sessionPaymentName = ((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeName();
            sessionPaymentId = ((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId();
        } else {
            //sessionPaymentName = "All";
            sessionPaymentId = -1;
        }

        String userId;
        if (cboUser.getSelectedItem() instanceof Appuser) {
            userId = ((Appuser) cboUser.getSelectedItem()).getUserId();
        } else {
            userId = "-";
        }

        boolean sessionDelete = false;
        if (cboDelete.getSelectedItem().toString().equals("Normal")) {
            sessionDelete = false;
        } else if (cboDelete.getSelectedItem().toString().equals("Deleted")) {
            sessionDelete = true;
        }
        //Properties prop = ReportUtil.loadReportPathProperties();
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "Clinic/"
                + "ClinicSessionCheckDetail";
        Map<String, Object> params = new HashMap();
        params.put("data_date", "Between " + txtFrom.getText()
                + " and " + txtTo.getText());
        params.put("comp_name", Util1.getPropValue("report.company.name"));
        params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
        params.put("session_currency", sessionCurrency);
        params.put("session_paymentid", sessionPaymentId);
        params.put("p_user_id", userId);
        params.put("deleted", sessionDelete);
        params.put("p_user_name", cboUser.getSelectedItem().toString());
        params.put("p_session_name", cboSession.getSelectedItem().toString());

        if (cboSession.getSelectedItem() instanceof Session) {
            String sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
            params.put("sess_id", sessionId);
        } else {
            params.put("sess_id", "-");
        }
        if (cboTranType.getSelectedItem().toString().equals("All")) {
            params.put("tran_type", "-");
        } else {
            params.put("tran_type", cboTranType.getSelectedItem().toString());
        }

        if (!txtPtNo.getText().trim().isEmpty()) {
            params.put("pt_id", txtPtNo.getText().trim());
        } else {
            params.put("pt_id", "-");
        }
        if (!txtDrNo.getText().trim().isEmpty()) {
            params.put("dr_id", txtDrNo.getText().trim());
        } else {
            params.put("dr_id", "-");
        }
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butPrintD;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboDelete;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JComboBox cboTranType;
    private javax.swing.JComboBox cboUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblSession;
    private javax.swing.JTable tblTotal;
    private javax.swing.JTextField txtDrName;
    private javax.swing.JTextField txtDrNo;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtPtNo;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
