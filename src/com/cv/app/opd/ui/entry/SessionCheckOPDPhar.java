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
import com.cv.app.opd.ui.common.SessionCheckOPDPharTableModel;
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
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class SessionCheckOPDPhar extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(SessionCheckOPDPhar.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final SessionCheckOPDPharTableModel tableSCModel = new SessionCheckOPDPharTableModel();
    private final SessionTotalTableModel sTableModel = new SessionTotalTableModel();
    private TableRowSorter<TableModel> sorter;
    private PaymentType ptCash;

    /**
     * Creates new form SessionCheck
     */
    public SessionCheckOPDPhar() {
        initComponents();
        assignDate();
        initCombo();
        initTable();
        try {
            ptCash = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
            cboPayment.setSelectedItem(ptCash);
        } catch (Exception ex) {
            log.error("SessionCheckOPDPhar : " + ex.getMessage());
        } finally {
            dao.close();
        }
        butCheckPoint.setEnabled(false);
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
        String strSql = "select s from VSessionClinicPhar s where date(s.tranDate) between '"
                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and s.tranOption <> 'PHARMACY-Purchase'";
        int session = getSessionFilter();

        if (session != 0) {
            strSql = strSql + " and s.session = " + session;
        }

        return strSql;
    }

    private String getFilterString() {
        String filterString = "SELECT * FROM com.cv.app.opd.database.view.VSessionClinicPhar ";
        String strWhere = "";
        String tmpStrValue;
        int tmpIntValue;
        String sqlFilter = "";
        String strSUBGFilter = "";

        tmpStrValue = getUserFilter();
        if (!tmpStrValue.equals("All")) {
            if (strWhere.isEmpty()) {
                strWhere = " user = '" + tmpStrValue + "'";
                sqlFilter = "user_id = '" + tmpStrValue + "'";
                strSUBGFilter = "user_id = '" + tmpStrValue + "'";
            } else {
                strWhere = strWhere + " and user = '" + tmpStrValue + "'";
                sqlFilter = sqlFilter + " and user_id = '" + tmpStrValue + "'";
                strSUBGFilter = strSUBGFilter + " and user_id = '" + tmpStrValue + "'";
            }
        }

        if (strWhere.isEmpty()) {
            strWhere = " deleted = " + getDeletedFilter();
            sqlFilter = "deleted = " + getDeletedFilter();
            strSUBGFilter = "deleted = " + getDeletedFilter();
        } else {
            strWhere = strWhere + " and deleted = " + getDeletedFilter();
            sqlFilter = sqlFilter + " and deleted = " + getDeletedFilter();
            strSUBGFilter = strSUBGFilter + " and deleted = " + getDeletedFilter();
        }

        tmpStrValue = getCurrencyFilter();
        if (!tmpStrValue.equals("All")) {
            if (strWhere.isEmpty()) {
                strWhere = " currency = '" + tmpStrValue + "'";
                sqlFilter = "currency_id = '" + tmpStrValue + "'";
                strSUBGFilter = "currency_id = '" + tmpStrValue + "'";
            } else {
                strWhere = strWhere + " and currency = '" + tmpStrValue + "'";
                sqlFilter = sqlFilter + " and currency_id = '" + tmpStrValue + "'";
                strSUBGFilter = strSUBGFilter + " and currency_id = '" + tmpStrValue + "'";
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
                strSUBGFilter = "patient_id = '" + txtPtNo.getText().trim() + "'";
            } else {
                strWhere = strWhere + " and ptId = '" + txtPtNo.getText().trim() + "'";
                sqlFilter = sqlFilter + " and patient_id = '" + txtPtNo.getText().trim() + "'";
                strSUBGFilter = strSUBGFilter + " and patient_id = '" + txtPtNo.getText().trim() + "'";
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
                strSUBGFilter = "doctor_id = '" + txtDrNo.getText().trim() + "'";
            } else {
                strWhere = strWhere + " and drId = '" + txtDrNo.getText().trim() + "'";
                sqlFilter = sqlFilter + " and doctor_id = '" + txtDrNo.getText().trim() + "'";
                strSUBGFilter = strSUBGFilter + " and doctor_id = '" + txtDrNo.getText().trim() + "'";
            }
        }

        //String strSUBGFilter = sqlFilter;
        String tranType = cboTranType.getSelectedItem().toString();
        switch (tranType) {
            case "OPD":
            case "OT":
            case "DC":
            case "PHARMACY-Sale":
            case "PHARMACY-Return In":
                if (strWhere.isEmpty()) {
                    strWhere = " tranOption = '" + tranType + "' ";
                    sqlFilter = "tran_option = '" + tranType + "'";
                } else {
                    strWhere = strWhere + " and tranOption = '" + tranType + "' ";
                    sqlFilter = sqlFilter + " and tran_option = '" + tranType + "'";
                }
                break;
            case "OPD-Group":
                if (strWhere.isEmpty()) {
                    strWhere = " tranOption in ('OPD','PHARMACY-Sale','PHARMACY-Return In') ";
                    sqlFilter = "tran_option in ('OPD','PHARMACY-Sale','PHARMACY-Return In')";
                } else {
                    strWhere = strWhere + " and tranOption in ('OPD','PHARMACY-Sale','PHARMACY-Return In') ";
                    sqlFilter = sqlFilter + " and tran_option in ('OPD','PHARMACY-Sale','PHARMACY-Return In') ";
                }
                break;
        }

        String ptType = cboPtType.getSelectedItem().toString();
        switch (ptType) {
            case "OPD":
                if (strWhere.isEmpty()) {
                    strWhere = " and (admissionNo='' or admissionNo is null) ";
                    sqlFilter = " and if(admission_no='',null,admission_no) is null";
                } else {
                    strWhere = strWhere + " and (admissionNo='' or admissionNo is null) ";;
                    sqlFilter = sqlFilter + " and if(admission_no='',null,admission_no) is null";
                }
                break;
            case "Inpatient":
                if (strWhere.isEmpty()) {
                    strWhere = " and (admissionNo<>'' and admissionNo is not null) ";
                    sqlFilter = " and if(admission_no='',null,admission_no) is not null";
                } else {
                    strWhere = strWhere + " and (admissionNo<>'' and admissionNo is not null) ";
                    sqlFilter = sqlFilter + " and if(admission_no='',null,admission_no) is not null";
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
                + "from v_session_clinic_phar\n";

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
        strSql = strSql + " and tran_option <> 'PHARMACY-Return In' group by tran_option";

        try {
            dao.execSql(
                    "delete from tmp_clinic_session_check where user_id = '" + userId + "'",
                    strSql
            );

            //=========================================================
            //Substract group
            if (tranType.equals("OPD") || tranType.equals("All") || tranType.equals("OPD-Group")) {
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
                                + "select '" + Global.machineId
                                + "','SUBGOPD' tran_option, ifnull(currency_id,'MMK'), ifnull(sum(amount)*-1,0)\n"
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

                //OPD Read Fee Substract (Ultrasound OPD)
                List<SessionFilter> listSF1 = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBULT'");
                SessionFilter sf1 = null;
                if (listSF1 != null) {
                    if (!listSF1.isEmpty()) {
                        sf1 = listSF1.get(0);
                    }
                }
                if (sf1 != null) {
                    String strGroupId = sf1.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.machineId
                                + "','SUBULT' tran_option, ifnull(currency_id,'MMK'), ifnull(sum(ifnull(qty,0)*ifnull(srv_fees6,0))*-1,0)\n"
                                + "from v_opd\n"
                                + "where date(opd_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")"
                                + " and ifnull(charge_type,0) = 1";

                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }

                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }
                        strOpd = strOpd + " and if(admission_no ='','-',ifnull(admission_no,'-')) = '-'";
                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBGOPD : " + ex.getMessage());
                        }
                    }
                }

                //OPD Read Fee Substract (Ultrasound IPD)
                listSF1 = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBULTIPD'");
                sf1 = null;
                if (listSF1 != null) {
                    if (!listSF1.isEmpty()) {
                        sf1 = listSF1.get(0);
                    }
                }
                if (sf1 != null) {
                    String strGroupId = sf1.getRptParameter();
                    if (!strGroupId.equals("-")) {
                        String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                                + "select '" + Global.machineId
                                + "','SUBULTIPD' tran_option, ifnull(currency_id,'MMK'), ifnull(sum(ifnull(qty,0)*ifnull(srv_fees6,0))*-1,0)\n"
                                + "from v_opd\n"
                                + "where date(opd_date) between '"
                                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                                + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and cat_id in (" + strGroupId + ")"
                                + " and ifnull(charge_type,0) = 1";

                        if (session != 0) {
                            strOpd = strOpd + " and session_id = " + session;
                        }

                        if (!strSUBGFilter.isEmpty()) {
                            strOpd = strOpd + " and " + strSUBGFilter;
                        }
                        strOpd = strOpd + " and (if(admission_no ='','-',ifnull(admission_no,'-')) <> '-')";
                        try {
                            dao.execSql(strOpd);
                        } catch (Exception ex) {
                            log.error("getFilterString SUBULTIPD : " + ex.getMessage());
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
                                + "select '" + Global.machineId + "','SUBGOT' tran_option, ifnull(currency_id,'MMK'), "
                                + "ifnull(sum(amount)*-1, 0)\n"
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
                                + "select '" + Global.machineId
                                + "','SUBGDC' tran_option, ifnull(currency_id,'MMK), ifnull(sum(amount)*-1,0)\n"
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

            if (tranType.equals("PHARMACY-Return In") || tranType.equals("All") || tranType.equals("OPD-Group")) {
                List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'SUBRTN'");
                SessionFilter sf = null;
                if (listSF != null) {
                    if (!listSF.isEmpty()) {
                        sf = listSF.get(0);
                    }
                }
                if (sf != null) {
                    String strOpd = "insert into tmp_clinic_session_check(user_id, tran_option, curr_id, ttl_paid)\n"
                            + "select '" + Global.machineId
                            + "','SUBRTN' tran_option, ifnull(currency_id,'MMK'), ifnull(sum(paid)*-1,0)\n"
                            + "from v_session_clinic_phar\n"
                            + "where date(tran_date) between '"
                            + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                            + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "' and tran_option = 'PHARMACY-Return In'";

                    if (session != 0) {
                        strOpd = strOpd + " and session_id = " + session;
                    }

                    if (!strSUBGFilter.isEmpty()) {
                        strOpd = strOpd + " and " + strSUBGFilter;
                    }

                    try {
                        dao.execSql(strOpd);
                    } catch (Exception ex) {
                        log.error("getFilterString SUBRTN : " + ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getFilterString : " + ex.getMessage());
        }
        //=========================================================

        if (!strWhere.isEmpty()) {
            filterString = filterString + " WHERE " + strWhere;
        }

        return filterString;
    }

    private void applyFilter() {
        String joFilter = getFilterString();
        tableSCModel.applyFilter(joFilter);
        //List<VSessionClinic> listVSC = tableSCModel.getListVSessionClinic();
        List<SessionTtl> listTtl = new ArrayList();
        //QueryResults qr;
        //Query q = new Query();

        //OPD Total
        /*try {
         String strSql = "SELECT * FROM com.cv.app.opd.database.view.VSessionClinic "
         + "EXECUTE ON ALL sum(vouTotal) AS VouTotal, sum(discount) AS DiscTotal,"
         + "sum(tax) AS TaxTotal, sum(paid) AS PaidTotal, sum(vouBalance) AS VBalTotal";
         String strFilter = joFilter + " and tranOption = 'OPD'";

         q.parse(strSql);
         qr = q.execute(JoSQLUtil.getResult(strFilter, listVSC));
         listTtl.add(new SessionTtl("OPD Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VouTotal").toString())));
         listTtl.add(new SessionTtl("OPD Discount Total", "MMK",
         Double.parseDouble(qr.getSaveValue("DiscTotal").toString())));
         listTtl.add(new SessionTtl("OPD Tax Total", "MMK",
         Double.parseDouble(qr.getSaveValue("TaxTotal").toString())));
         listTtl.add(new SessionTtl("OPD Paid Total", "MMK",
         Double.parseDouble(qr.getSaveValue("PaidTotal").toString())));
         listTtl.add(new SessionTtl("OPD Balance Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VBalTotal").toString())));
         } catch (QueryParseException qpe) {
         log.error("JoSQLUtil.isAlreadyHave qpe: " + qpe.getStackTrace()[0].getLineNumber() + " - " + qpe.toString());
         } catch (QueryExecutionException | NumberFormatException ex) {
         log.error("JoSQLUtil.isAlreadyHave : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
         }*/
        //OT Total
        /*try {
         String strSql = "SELECT * FROM com.cv.app.opd.database.view.VSessionClinic "
         + "WHERE tranOption = 'OT' EXECUTE ON ALL sum(vouTotal) AS VouTotal, sum(discount) AS DiscTotal,"
         + "sum(tax) AS TaxTotal, sum(paid) AS PaidTotal, sum(vouBalance) AS VBalTotal";
         String strFilter = joFilter + " and tranOption = 'OT'";

         q = new Query();
         q.parse(strSql);
         qr = q.execute(JoSQLUtil.getResult(strFilter, listVSC));
         listTtl.add(new SessionTtl("OT Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VouTotal").toString())));
         listTtl.add(new SessionTtl("OT Discount Total", "MMK",
         Double.parseDouble(qr.getSaveValue("DiscTotal").toString())));
         listTtl.add(new SessionTtl("OT Tax Total", "MMK",
         Double.parseDouble(qr.getSaveValue("TaxTotal").toString())));
         listTtl.add(new SessionTtl("OT Paid Total", "MMK",
         Double.parseDouble(qr.getSaveValue("PaidTotal").toString())));
         listTtl.add(new SessionTtl("OT Balance Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VBalTotal").toString())));
         } catch (QueryParseException qpe) {
         log.error("JoSQLUtil.isAlreadyHave qpe: " + qpe.getStackTrace()[0].getLineNumber() + " - " + qpe.toString());
         } catch (QueryExecutionException | NumberFormatException ex) {
         log.error("JoSQLUtil.isAlreadyHave : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
         }*/
        //DC Total
        /*try {
         String strSql = "SELECT * FROM com.cv.app.opd.database.view.VSessionClinic "
         + "WHERE tranOption = 'DC' EXECUTE ON ALL sum(vouTotal) AS VouTotal, sum(discount) AS DiscTotal,"
         + "sum(tax) AS TaxTotal, sum(paid) AS PaidTotal, sum(vouBalance) AS VBalTotal";
         String strFilter = joFilter + " and tranOption = 'DC'";

         q = new Query();
         q.parse(strSql);
         qr = q.execute(JoSQLUtil.getResult(strFilter, listVSC));
         listTtl.add(new SessionTtl("DC Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VouTotal").toString())));
         listTtl.add(new SessionTtl("DC Discount Total", "MMK",
         Double.parseDouble(qr.getSaveValue("DiscTotal").toString())));
         listTtl.add(new SessionTtl("DC Tax Total", "MMK",
         Double.parseDouble(qr.getSaveValue("TaxTotal").toString())));
         listTtl.add(new SessionTtl("DC Paid Total", "MMK",
         Double.parseDouble(qr.getSaveValue("PaidTotal").toString())));
         listTtl.add(new SessionTtl("DC Balance Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VBalTotal").toString())));
         } catch (QueryParseException qpe) {
         System.out.println("JoSQLUtil.isAlreadyHave qpe: " + qpe.toString());
         } catch (Exception ex) {
         System.out.println("JoSQLUtil.isAlreadyHave : " + ex.toString());
         }*/
        //All Total
        /*try {
         String strSql = "SELECT * FROM com.cv.app.opd.database.view.VSessionClinic "
         + "EXECUTE ON ALL sum(vouTotal) AS VouTotal, sum(discount) AS DiscTotal,"
         + "sum(tax) AS TaxTotal, sum(paid) AS PaidTotal, sum(vouBalance) AS VBalTotal";
         String strFilter = joFilter;

         q = new Query();
         q.parse(strSql);
         qr = q.execute(JoSQLUtil.getResult(strFilter, listVSC));
         listTtl.add(new SessionTtl("All Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VouTotal").toString())));
         listTtl.add(new SessionTtl("All Discount Total", "MMK",
         Double.parseDouble(qr.getSaveValue("DiscTotal").toString())));
         listTtl.add(new SessionTtl("All Tax Total", "MMK",
         Double.parseDouble(qr.getSaveValue("TaxTotal").toString())));
         listTtl.add(new SessionTtl("All Paid Total", "MMK",
         Double.parseDouble(qr.getSaveValue("PaidTotal").toString())));
         listTtl.add(new SessionTtl("All Balance Total", "MMK",
         Double.parseDouble(qr.getSaveValue("VBalTotal").toString())));
         } catch (QueryParseException qpe) {
         System.out.println("JoSQLUtil.isAlreadyHave qpe: " + qpe.toString());
         } catch (QueryExecutionException | NumberFormatException ex) {
         System.out.println("JoSQLUtil.isAlreadyHave : " + ex.toString());
         }*/
        try {
            String strSql = "select vstc.*\n"
                    + "from v_sess_ttl_clinic vstc, session_filter sf\n"
                    + "where vstc.tran_option = sf.tran_source\n"
                    + "and vstc.user_id = '" + Global.machineId
                    + "' and sf.program_id in ('CLINICTTL','SUBGOPD','SUBGOT','SUBRTN','SUBULT','SUBULTIPD')\n"
                    + " and sf.apply = true  "
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

        sTableModel.setListSessionTtl(listTtl);
        System.gc();
        //getTotal();
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
        } catch (SQLException ex) {
            log.error("getTotal : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } catch (Exception ex) {
            log.error("getTotal : " + ex.getMessage());
        } finally {
            dao.close();
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

    private void checkPointInsert() {
        String strLFilter = "";
        String tranType = cboTranType.getSelectedItem().toString();
        switch (tranType) {
            case "OPD":
            case "OT":
            case "DC":
            case "PHARMACY-Sale":
            case "PHARMACY-Return In":
                if (strLFilter.isEmpty()) {
                    strLFilter = "tran_option = '" + tranType + "'";
                } else {
                    strLFilter = strLFilter + " and tran_option = '" + tranType + "'";
                }
                break;
            case "OPD-Group":
                if (strLFilter.isEmpty()) {
                    strLFilter = "tran_option in ('OPD','PHARMACY-Sale','PHARMACY-Return In')";
                } else {
                    strLFilter = strLFilter + " and tran_option in ('OPD','PHARMACY-Sale','PHARMACY-Return In') ";
                }
                break;
        }

        String ptType = cboPtType.getSelectedItem().toString();
        switch (ptType) {
            case "OPD":
                if (strLFilter.isEmpty()) {
                    strLFilter = " and if(admission_no='',null,admission_no) is null";
                } else {
                    strLFilter = strLFilter + " and if(admission_no='',null,admission_no) is null";
                }
                break;
            case "Inpatient":
                if (strLFilter.isEmpty()) {
                    strLFilter = " and if(admission_no='',null,admission_no) is not null";
                } else {
                    strLFilter = strLFilter + " and if(admission_no='',null,admission_no) is not null";
                }
                break;
        }

        int tmpIntValue = getPaymentFilter();
        if (tmpIntValue != 0) {
            if (strLFilter.isEmpty()) {
                if (tmpIntValue == 1) {//For cash
                    strLFilter = "paid <> 0";
                } else {
                    strLFilter = "payment_type_id = " + tmpIntValue;
                }
            } else if (tmpIntValue == 1) {//For cash
                strLFilter = strLFilter + " and paid <> 0";
            } else {
                strLFilter = strLFilter + " and payment_type_id = " + tmpIntValue;
            }
        }

        int session = getSessionFilter();
        String strSql = "insert into session_check_checkpoint (tran_option, tran_date, tran_inv_id,\n"
                + "  patient_id, currency_id, deleted, vou_total, disc_a, tax_a, paid, vou_balance,\n"
                + "  session_id, user_id, doctor_id, payment_type_id, admission_no, check_point_session,\n"
                + "  check_point_date, check_point_user)\n"
                + "select tran_option, tran_date, tran_inv_id, patient_id, currency_id, if(deleted, true, false), vou_total,\n"
                + "disc_a, tax_a, paid, vou_balance, session_id, user_id, doctor_id, payment_type_id,\n"
                + "admission_no," + session + ",sysdate(),'" + Global.machineId
                + "' from v_session_clinic_phar "
                + "where date(tran_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "' and (session_id = "
                + session + " or 0 = " + session + ") ";

        if (!strLFilter.isEmpty()) {
            strSql = strSql + " and " + strLFilter;
        }
        try {
            dao.execSql(strSql);
            butCheckPoint.setEnabled(false);
        } catch (Exception ex) {
            log.error("checkPointInsert : " + ex.getMessage());
            JOptionPane.showMessageDialog(Util1.getParent(), ex.getMessage(),
                    "Check Point Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            dao.close();
        }
    }

    private boolean isCheckPointHave() {
        boolean status = false;
        String strLFilter = "";
        String tranType = cboTranType.getSelectedItem().toString();
        switch (tranType) {
            case "OPD":
            case "OT":
            case "DC":
            case "PHARMACY-Sale":
            case "PHARMACY-Return In":
                if (strLFilter.isEmpty()) {
                    strLFilter = "tran_option = '" + tranType + "'";
                } else {
                    strLFilter = strLFilter + " and tran_option = '" + tranType + "'";
                }
                break;
            case "OPD-Group":
                if (strLFilter.isEmpty()) {
                    strLFilter = "tran_option in ('OPD','PHARMACY-Sale','PHARMACY-Return In')";
                } else {
                    strLFilter = strLFilter + " and tran_option in ('OPD','PHARMACY-Sale','PHARMACY-Return In') ";
                }
                break;
        }

        String ptType = cboPtType.getSelectedItem().toString();
        switch (ptType) {
            case "OPD":
                if (strLFilter.isEmpty()) {
                    strLFilter = " if(admission_no='',null,admission_no) is null";
                } else {
                    strLFilter = strLFilter + " and if(admission_no='',null,admission_no) is null";
                }
                break;
            case "Inpatient":
                if (strLFilter.isEmpty()) {
                    strLFilter = " if(admission_no='',null,admission_no) is not null";
                } else {
                    strLFilter = strLFilter + " and if(admission_no='',null,admission_no) is not null";
                }
                break;
        }

        int tmpIntValue = getPaymentFilter();
        if (tmpIntValue != 0) {
            if (strLFilter.isEmpty()) {
                if (tmpIntValue == 1) {//For cash
                    strLFilter = "paid <> 0";
                } else {
                    strLFilter = "payment_type_id = " + tmpIntValue;
                }
            } else if (tmpIntValue == 1) {//For cash
                strLFilter = strLFilter + " and paid <> 0";
            } else {
                strLFilter = strLFilter + " and payment_type_id = " + tmpIntValue;
            }
        }
        int session = getSessionFilter();
        String strSql = "select count(*) from session_check_checkpoint "
                + "where date(tran_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "' and (session_id = "
                + session + " or 0 = " + session + ") ";
        if (!strLFilter.isEmpty()) {
            strSql = strSql + " and " + strLFilter;
        }
        long cnt = dao.getRowCount(strSql);
        if (cnt > 0) {
            status = true;
        }
        return status;
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
        cboTranType = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cboPtType = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        cboUser = new javax.swing.JComboBox();
        cboDelete = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtPtNo = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        butPrintD = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtDrNo = new javax.swing.JTextField();
        txtDrName = new javax.swing.JTextField();
        butPrint = new javax.swing.JButton();
        butCheckPoint = new javax.swing.JButton();
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

        cboTranType.setFont(Global.textFont);
        cboTranType.setModel(new javax.swing.DefaultComboBoxModel(
            new String[] { "All", "OPD", "OT",
                "DC", "PHARMACY-Sale", "PHARMACY-Return In", "OPD-Group"}));
    cboTranType.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboTranTypeActionPerformed(evt);
        }
    });

    jLabel8.setFont(Global.lableFont);
    jLabel8.setText("Tran Type");

    jLabel11.setFont(Global.lableFont);
    jLabel11.setText("Pt Type");

    cboPtType.setFont(Global.textFont);
    cboPtType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "OPD", "Inpatient" }));

    jLabel5.setFont(Global.lableFont);
    jLabel5.setText("Currency ");

    cboCurrency.setFont(Global.textFont);

    jLabel6.setFont(Global.lableFont);
    jLabel6.setText("Payment");

    jLabel9.setFont(Global.lableFont);
    jLabel9.setText("User");

    jLabel10.setFont(Global.lableFont);
    jLabel10.setText("Del");

    cboPayment.setFont(Global.textFont);

    cboUser.setFont(Global.textFont);

    cboDelete.setFont(Global.textFont);
    cboDelete.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Deleted" }));

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addComponent(jLabel10)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboDelete, 0, 130, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                    .addComponent(jLabel5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboCurrency, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboPayment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                    .addComponent(jLabel9)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboUser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGap(0, 0, 0))
    );

    jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel5, jLabel6, jLabel9});

    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(8, 8, 8)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel10)
                .addComponent(cboDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    jLabel3.setFont(Global.lableFont);
    jLabel3.setText("Patient");

    txtPtNo.setFont(Global.textFont);
    txtPtNo.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtPtNoActionPerformed(evt);
        }
    });

    txtPtName.setFont(Global.textFont);
    txtPtName.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            txtPtNameMouseClicked(evt);
        }
    });

    butPrintD.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
    butPrintD.setText("Print D");
    butPrintD.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            butPrintDActionPerformed(evt);
        }
    });

    jLabel4.setFont(Global.lableFont);
    jLabel4.setText("Doctor");

    txtDrNo.setFont(Global.textFont);
    txtDrNo.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtDrNoActionPerformed(evt);
        }
    });

    txtDrName.setFont(Global.textFont);

    butPrint.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
    butPrint.setText("Print");
    butPrint.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            butPrintActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtPtName))
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDrNo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDrName)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(butPrintD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(butPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(2, 2, 2))
    );

    jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel4});

    jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butPrint, butPrintD});

    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(butPrintD))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel4)
                .addComponent(txtDrNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtDrName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(butPrint)))
    );

    butCheckPoint.setText("Check Point");
    butCheckPoint.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            butCheckPointActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboTranType, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(butCheckPoint, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                .addComponent(cboPtType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGap(0, 21, Short.MAX_VALUE))))
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(5, 5, 5)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(cboTranType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(cboPtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(butCheckPoint)
            .addContainerGap())
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
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
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
        if (!isCheckPointHave()) {
            butCheckPoint.setEnabled(true);
        }
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

    private void butCheckPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCheckPointActionPerformed
        checkPointInsert();
    }//GEN-LAST:event_butCheckPointActionPerformed

    private void print() {
        //Properties prop = ReportUtil.loadReportPathProperties();
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + Util1.getPropValue("system.clinic.session.check.print.report");
        Map<String, Object> params = new HashMap();
        String sessionName;
        String sessionId;
        String sessionUser;
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
            sessionId = "-1";
        }

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            sessionPaymentName = ((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeName();
            sessionPaymentId = ((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId().toString();
        } else {
            sessionPaymentName = "All";
            sessionPaymentId = "-1";
        }

        if (cboUser.getSelectedItem() instanceof Appuser) {
            sessionUser = ((Appuser) cboUser.getSelectedItem()).getUserName();
        } else {
            sessionUser = "All";
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
        params.put("session_user", sessionUser);
        params.put("session_date", sessionDate);
        params.put("comp_name", Util1.getPropValue("report.company.name"));
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));

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

        String sessionUser;
        if (cboUser.getSelectedItem() instanceof Appuser) {
            sessionUser = ((Appuser) cboUser.getSelectedItem()).getUserName();
        } else {
            sessionUser = "-";
        }

        boolean sessionDelete = false;
        if (cboDelete.getSelectedItem().toString().equals("Normal")) {
            sessionDelete = false;
        } else if (cboDelete.getSelectedItem().toString().equals("Deleted")) {
            sessionDelete = true;
        }

        String ptType = cboPtType.getSelectedItem().toString();

        //Properties prop = ReportUtil.loadReportPathProperties();
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "Clinic/"
                + "ClinicSessionCheckPhar";
        Map<String, Object> params = new HashMap();
        params.put("data_date", "Between " + txtFrom.getText()
                + " and " + txtTo.getText());
        params.put("comp_name", Util1.getPropValue("report.company.name"));
        params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));

        params.put("session_currency", sessionCurrency);
        params.put("session_paymentid", sessionPaymentId);
        params.put("session_user", sessionUser);
        params.put("deleted", sessionDelete);
        params.put("p_pt_type", ptType);

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
    private javax.swing.JButton butCheckPoint;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butPrintD;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboDelete;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox<String> cboPtType;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JComboBox cboTranType;
    private javax.swing.JComboBox cboUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
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
