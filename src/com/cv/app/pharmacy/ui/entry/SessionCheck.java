/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.SchoolDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.LocationGroup;
import com.cv.app.pharmacy.database.entity.LocationGroupMapping;
import com.cv.app.pharmacy.database.entity.MachineInfo;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.SessionFilter;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.SessionTtl;
import com.cv.app.pharmacy.database.tempentity.TmpSessionTotal;
import com.cv.app.pharmacy.database.view.VMarchant;
import com.cv.app.pharmacy.database.view.VSession;
import com.cv.app.pharmacy.ui.common.SessionTableModel;
import com.cv.app.pharmacy.ui.common.SessionTotalTableModel;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.jms.MapMessage;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SessionCheck extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(SessionCheck.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final SessionTableModel tableModel = new SessionTableModel();
    private final TableRowSorter<TableModel> sorter;
    private final SessionTotalTableModel sTableModel = new SessionTotalTableModel();
    private boolean bindStatus = false;
    private final javax.swing.JTable ttlTable = new javax.swing.JTable(1, 14);
    private String strSessionFilter = "-";
    private int mouseClick = 2;

    /**
     * Creates new form SessionCheck
     */
    public SessionCheck() {
        initComponents();
        assignDate();
        initCombo();
        initTable();
        getSessionFilter();
        sorter = new TableRowSorter(tblSession.getModel());
        tblSession.setRowSorter(sorter);
        //jScrollPane2.setVisible(false);
        //initTotalTable();
        String propValue = Util1.getPropValue("system.date.mouse.click");
        if (propValue != null) {
            if (!propValue.equals("-")) {
                if (!propValue.isEmpty()) {
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if (tmpValue != 0) {
                        mouseClick = tmpValue;
                    }
                }
            }
        }
    }

    private void getSessionFilter() {
        try {
            List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'Pharmacy'");
            if (listSF != null) {
                if (!listSF.isEmpty()) {
                    for (SessionFilter sf : listSF) {
                        if (strSessionFilter.equals("-")) {
                            strSessionFilter = "'" + sf.getKey().getTranSource() + "'";
                        } else {
                            strSessionFilter = strSessionFilter + ",'" + sf.getKey().getTranSource() + "'";
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getSessionFilter : " + ex.getMessage());
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
            BindingUtil.BindComboFilter(cboUser, dao.findAll("Appuser"));
            BindingUtil.BindComboFilter(cboLocation, getLocationFilter());
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
            BindingUtil.BindComboFilter(cboMachine, dao.findAll("MachineInfo"));
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
            BindingUtil.BindComboFilter(cboPaidCurrency, dao.findAll("Currency"));
            BindingUtil.BindComboFilter(cboLGroup, dao.findAll("LocationGroup"));
            BindingUtil.BindComboFilter(cboCusGroup, dao.findAll("CustomerGroup"));

            new ComBoBoxAutoComplete(cboUser);
            new ComBoBoxAutoComplete(cboLocation);
            new ComBoBoxAutoComplete(cboSession);
            new ComBoBoxAutoComplete(cboMachine);
            new ComBoBoxAutoComplete(cboTranType);
            new ComBoBoxAutoComplete(cboDelete);
            new ComBoBoxAutoComplete(cboSource);
            new ComBoBoxAutoComplete(cboCurrency);
            new ComBoBoxAutoComplete(cboPaidCurrency);

            cboUser.setSelectedIndex(0);
            cboLocation.setSelectedIndex(0);
            cboSession.setSelectedIndex(0);
            cboMachine.setSelectedIndex(0);
            cboTranType.setSelectedIndex(0);
            cboDelete.setSelectedIndex(0);
            cboSource.setSelectedIndex(0);
            cboCurrency.setSelectedIndex(0);
            cboPaidCurrency.setSelectedIndex(0);

            bindStatus = true;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowSessCheck = true) order by o.locationName");
            } else {
                return dao.findAll("Location");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return null;
    }

    private String getHSQL() {
        String strSql = "select s from VSession s where date(s.tranDate) between '"
                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";

        if (!strSessionFilter.equals("-")) {
            strSql = strSql + " and s.key.source in (" + strSessionFilter + ")";
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            Session session = (Session) cboSession.getSelectedItem();
            strSql = strSql + " and s.sessionId = " + session.getSessionId();
        }

        return strSql + " order by s.tranDate desc";
    }

    private void search() {
        try {
            List<VSession> listVS = dao.findAllHSQL(getHSQL());
            tableModel.setListVSession(listVS);
            //System.out.println("search : " + listVS.size());
            //getTotal();
            applyFilter();
        } catch (Exception ex) {
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblSession.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblSession.getColumnModel().getColumn(0).setPreferredWidth(60);//Tran Date
        tblSession.getColumnModel().getColumn(1).setPreferredWidth(70);//Vou No
        tblSession.getColumnModel().getColumn(2).setPreferredWidth(40);//Ref. Vou.
        tblSession.getColumnModel().getColumn(3).setPreferredWidth(130);//Cus-Name
        tblSession.getColumnModel().getColumn(4).setPreferredWidth(15);//V-Total
        tblSession.getColumnModel().getColumn(5).setPreferredWidth(3);//V-Currency
        tblSession.getColumnModel().getColumn(6).setPreferredWidth(10);//Paid
        tblSession.getColumnModel().getColumn(7).setPreferredWidth(3);//P-Currency
        tblSession.getColumnModel().getColumn(8).setPreferredWidth(10);//Discount
        tblSession.getColumnModel().getColumn(9).setPreferredWidth(15);//Balance
        tblSession.getColumnModel().getColumn(10).setPreferredWidth(10);//Exp-In
        tblSession.getColumnModel().getColumn(11).setPreferredWidth(10);//Exp-Out
        tblSession.getColumnModel().getColumn(12).setPreferredWidth(10);//Tax
        tblSession.getColumnModel().getColumn(13).setPreferredWidth(10);//User
        tblSession.getColumnModel().getColumn(14).setPreferredWidth(20);//Source
        tblSession.getColumnModel().getColumn(15).setPreferredWidth(20);//Session
        tblSession.getColumnModel().getColumn(16).setPreferredWidth(20);//Bill
        tblSession.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());

        tblSessTtl.getColumnModel().getColumn(0).setPreferredWidth(70);//Desp
        tblSessTtl.getColumnModel().getColumn(1).setPreferredWidth(10);//Currency
        tblSessTtl.getColumnModel().getColumn(2).setPreferredWidth(40);//Amount
    }

    private void initTotalTable() {
        //panelTotal.add(ttlTable);

        ttlTable.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        ttlTable.setRowHeight(23);
        ttlTable.setShowVerticalLines(false);
        ttlTable.setShowHorizontalLines(false);
        ttlTable.setGridColor(new Color(199, 225, 186));

        TableColumnModel model = ttlTable.getColumnModel();
        model.getColumn(0).setPreferredWidth(8);//Tran Date
        model.getColumn(1).setPreferredWidth(50);//Vou No
        model.getColumn(2).setPreferredWidth(40);//Ref. Vou.
        model.getColumn(3).setPreferredWidth(200);//Cus-Name
        model.getColumn(4).setPreferredWidth(15);//V-Total
        model.getColumn(5).setPreferredWidth(3);//V-Currency
        model.getColumn(6).setPreferredWidth(10);//Paid
        model.getColumn(7).setPreferredWidth(3);//P-Currency
        model.getColumn(8).setPreferredWidth(10);//Discount
        model.getColumn(9).setPreferredWidth(15);//Balance
        model.getColumn(10).setPreferredWidth(10);//Expense
        model.getColumn(11).setPreferredWidth(10);//Tax
        model.getColumn(12).setPreferredWidth(10);//User
        model.getColumn(13).setPreferredWidth(45);//Source
        TotalTableCellRenderer ttcr = new TotalTableCellRenderer();
        model.getColumn(0).setCellRenderer(ttcr);
        model.getColumn(1).setCellRenderer(ttcr);
        model.getColumn(2).setCellRenderer(ttcr);
        model.getColumn(3).setCellRenderer(ttcr);
        model.getColumn(4).setCellRenderer(ttcr);
        model.getColumn(5).setCellRenderer(ttcr);
        model.getColumn(6).setCellRenderer(ttcr);
        model.getColumn(7).setCellRenderer(ttcr);
        model.getColumn(8).setCellRenderer(ttcr);
        model.getColumn(9).setCellRenderer(ttcr);
        model.getColumn(10).setCellRenderer(ttcr);
        model.getColumn(11).setCellRenderer(ttcr);
        model.getColumn(12).setCellRenderer(ttcr);
        model.getColumn(13).setCellRenderer(ttcr);
    }

    private String getFilterString() {
        String filterString = "SELECT * FROM com.cv.app.pharmacy.database.view.VSession ";
        String strWhere = "";

        if (cboUser.getSelectedItem() instanceof Appuser) {
            Appuser user = (Appuser) cboUser.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " userId = '" + user.getUserId() + "'";
            } else {
                strWhere = strWhere + " and userId = '" + user.getUserId() + "'";
            }
        }

        if (cboMachine.getSelectedItem() instanceof MachineInfo) {
            MachineInfo machine = (MachineInfo) cboMachine.getSelectedItem();
            String machineId = machine.getMachineId().toString();

            if (machineId.length() == 1) {
                machineId = "0" + machineId;
            }

            if (strWhere.isEmpty()) {
                strWhere = " machineId = '" + machineId + "'";
            } else {
                strWhere = strWhere + " and machineId = '" + machineId + "'";
            }
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " locationId = " + location.getLocationId();
            } else {
                strWhere = strWhere + " and locationId = " + location.getLocationId();
            }
        }

        try {
            if (cboLGroup.getSelectedItem() instanceof LocationGroup) {
                LocationGroup lg = (LocationGroup) cboLGroup.getSelectedItem();
                List<LocationGroupMapping> listLGM = dao.findAllHSQL(
                        "select o from LocationGroupMapping o where o.key.groupId = " + lg.getId().toString());
                String strLocation = "";

                if (listLGM != null) {
                    for (LocationGroupMapping lgm : listLGM) {
                        if (strLocation.isEmpty()) {
                            strLocation = lgm.getKey().getLocation().getLocationId().toString();
                        } else {
                            strLocation = strLocation + "," + lgm.getKey().getLocation().getLocationId().toString();
                        }
                    }
                }

                if (!strLocation.isEmpty()) {
                    if (strWhere.isEmpty()) {
                        strWhere = " locationId in (" + strLocation + ")";
                    } else {
                        strWhere = strWhere + " and locationId in (" + strLocation + ")";
                    }
                }
            }
        } catch (Exception ex) {
            log.error("location error : " + ex.getMessage());
        } finally {
            dao.close();
        }
        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            CustomerGroup cg = (CustomerGroup) cboCusGroup.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " groupId = '" + cg.getGroupId() + "'";
            } else {
                strWhere = strWhere + " and groupId = '" + cg.getGroupId() + "'";
            }
        }

        String ptType = cboPatientType.getSelectedItem().toString();
        if (!ptType.equals("All")) {
            switch (ptType) {
                case "OPD":
                    if (strWhere.isEmpty()) {
                        strWhere = "admissionNo is null";
                    } else {
                        strWhere = strWhere + " and admissionNo is null";
                    }
                    break;
                case "Inpatient":
                    if (strWhere.isEmpty()) {
                        strWhere = "admissionNo is not null";
                    } else {
                        strWhere = strWhere + " and admissionNo is not null";
                    }
                    break;
            }
        }

        String tranType = cboTranType.getSelectedItem().toString();
        if (tranType.equals("Cash")) {
            if (strWhere.isEmpty()) {
                strWhere = " paid <> 0 ";
            } else {
                strWhere = strWhere + " and paid <> 0";
            }
        } else if (tranType.equals("Balance")) {
            if (strWhere.isEmpty()) {
                strWhere = " balance <> 0 ";
            } else {
                strWhere = strWhere + " and balance <> 0 ";
            }
        } else if (tranType.equals("Discount")) {
            if (strWhere.isEmpty()) {
                strWhere = " discount <> 0 ";
            } else {
                strWhere = strWhere + " and discount <> 0 ";
            }
        } else if (tranType.equals("Tax")) {
            if (strWhere.isEmpty()) {
                strWhere = " taxAmt <> 0 ";
            } else {
                strWhere = strWhere + " and taxAmt <> 0 ";
            }
        }

        if (!cboDelete.getSelectedItem().toString().equals("All")) {
            String strDelete = cboDelete.getSelectedItem().toString();
            boolean isDeleted = false;

            if (strDelete.equals("Deleted")) {
                isDeleted = true;
            }

            if (strWhere.isEmpty()) {
                strWhere = " deleted = " + isDeleted;
            } else {
                strWhere = strWhere + " and deleted = " + isDeleted;
            }
        }

        if (!cboSend.getSelectedItem().toString().equals("All")) {
            String strStauts = cboSend.getSelectedItem().toString();
            String isStatus = "";

            if (strStauts.equals("Send")) {
                isStatus = " intgUpdStatus <> null";
            } else if (strStauts.equals("Not Send")) {
                isStatus = " intgUpdStatus = null";
            }

            if (strWhere.isEmpty()) {
                strWhere = isStatus;
            } else {
                strWhere = strWhere + " and" + isStatus;
            }
        }

        if (!cboSource.getSelectedItem().toString().equals("All")) {
            String strSource = cboSource.getSelectedItem().toString();

            if (strSource.equals("General Expense")) {
                if (strWhere.isEmpty()) {
                    strWhere = " (key.source = 'Cash In' or key.source = 'Cash Out')";
                } else {
                    strWhere = strWhere + " and (key.source = 'Cash In' or key.source = 'Cash Out')";
                }
            } else if (strWhere.isEmpty()) {
                strWhere = " key.source = '" + strSource + "'";
            } else {
                strWhere = strWhere + " and key.source = '" + strSource + "'";
            }
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            Currency currency = (Currency) cboCurrency.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " currency = '" + currency.getCurrencyCode() + "'";
            } else {
                strWhere = strWhere + " and currency = '" + currency.getCurrencyCode() + "'";
            }
        }

        if (!txtCusId.getText().trim().isEmpty()) {
            if (strWhere.isEmpty()) {
                strWhere = " traderId = '" + txtCusId.getText().trim() + "'";
            } else {
                strWhere = strWhere + " and traderId = '" + txtCusId.getText().trim() + "'";
            }
        }

        if (!txtInvTotal.getText().trim().isEmpty()) {
            String sign = (String) cboCondition.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " vouTotal " + sign + " " + txtInvTotal.getText().trim();
            } else {
                strWhere = strWhere + " and vouTotal " + sign + " " + txtInvTotal.getText().trim();
            }
        }

        if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
            Currency currency = (Currency) cboPaidCurrency.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " paidCurrency = '" + currency.getCurrencyCode() + "'";
            } else {
                strWhere = strWhere + " and paidCurrency = '" + currency.getCurrencyCode() + "'";
            }
        }

        try {
            List<SessionFilter> listSF = dao.findAllHSQL(
                    "select o from SessionFilter o where o.key.progId = 'PHARFILTER'"
                    + " and o.rptParameter <> '-' and o.apply = true"
            );
            if (listSF != null) {
                if (!listSF.isEmpty()) {
                    for (SessionFilter sf : listSF) {
                        if (strWhere.isEmpty()) {
                            strWhere = applySessionFilter(sf.getKey().getTranSource());
                        } else {
                            strWhere = strWhere + " and " + applySessionFilter(sf.getKey().getTranSource());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("session filter : " + ex.getMessage());
        } finally {
            dao.close();
        }
        if (!strWhere.isEmpty()) {
            filterString = filterString + " WHERE " + strWhere;
        }
        System.out.println("filter string : " + filterString);
        return filterString;
    }

    private String applySessionFilter(String strOption) {
        switch (strOption) {
            case "RETURN-PAID <> 0":
                return "((key.source = 'Return In' and paid <> 0) or key.source <> 'Return In')";
            default:
                return "-";
        }
    }

    private void applyFilter() {
        tableModel.applyFilter(getFilterString());
        //getTotal();
        calculateTotal();
        //callculateTotalKSW();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;
                txtCusId.setText(cus.getTraderId());
                txtCusName.setText(cus.getTraderName());
                applyFilter();
                break;
            case "PatientSearch":
                Patient ptt = (Patient) selectObj;
                if (ptt != null) {
                    txtCusId.setText(ptt.getRegNo());
                    txtCusName.setText(ptt.getPatientName());
                }
                break;
        }
    }

    private void getCustomerList() {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            /*dialog = new UtilDialog(Util1.getParent(), true, this,
             "Patient List", dao);*/
            PatientSearch dialog = new PatientSearch(dao, this);
        } else {
            /*UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                    "Customer List", dao);*/
            int locationId = -1;
            if (cboLocation.getSelectedItem() instanceof Location) {
                locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }
            TraderSearchDialog dialog = new TraderSearchDialog(this,
                    "Customer List", locationId);
            dialog.setTitle("Customer List");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

        }
    }

    private void getTotal() {
        String sessionId;
        String userId;
        String machineId;
        String locationId;
        String traderId;
        String tranType = cboTranType.getSelectedItem().toString();
        String deleted;
        String source = cboSource.getSelectedItem().toString();
        String vCurrency;
        String pCurrency;
        String amount = "";

        if (cboSession.getSelectedItem() instanceof Session) {
            sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        } else {
            sessionId = "0";
        }

        if (cboUser.getSelectedItem() instanceof Appuser) {
            userId = ((Appuser) cboUser.getSelectedItem()).getUserId();
        } else {
            userId = "0";
        }

        if (cboMachine.getSelectedItem() instanceof MachineInfo) {
            machineId = ((MachineInfo) cboMachine.getSelectedItem()).getMachineId().toString();
        } else {
            machineId = "0";
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId().toString();
        } else {
            locationId = "0";
        }

        if (txtCusId.getText().isEmpty()) {
            traderId = "All";
        } else {
            traderId = txtCusId.getText();
        }

        if (cboDelete.getSelectedItem().toString().equals("Normal")) {
            deleted = "0";
        } else {
            deleted = "1";
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            vCurrency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
        } else {
            vCurrency = "All";
        }

        if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
            pCurrency = ((Currency) cboPaidCurrency.getSelectedItem()).getCurrencyCode();
        } else {
            pCurrency = "All";
        }

        if (!txtInvTotal.getText().isEmpty()) {
            amount = txtInvTotal.getText();
        }

        try {
            ResultSet resultSet = dao.getPro("session_check", DateUtil.toDateTimeStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateTimeStrMYSQL(txtTo.getText()), sessionId, userId, machineId, locationId,
                    traderId, tranType, deleted, source, vCurrency, pCurrency, amount);

            if (resultSet != null) {
                List<SessionTtl> listTtl = new ArrayList();

                while (resultSet.next()) {
                    SessionTtl sttl = new SessionTtl(resultSet.getString("tran_option"),
                            resultSet.getString("currency"), resultSet.getDouble("ttl_amt"));

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

    /*
     Sale Paid    ==> Total Cash
     Sale Exp In  ==> Total Cash In
     Sale Exp Out ==> Total Cash Out
     Pur Paid     ==> Total Cash Out
     Pur Exp      ==> 
     Ret In Paid  ==> Total Cash Out
     Ret Out Paid ==> Total Cash In
     Payment CUS  ==> Total Cash In
     Payment SUP  ==> Total Cash Out
     Exp Entry DR ==> Total Cash In
     Exp Entry CR ==> Total Cash Out
     Net Cash     ==> Total Cash + Total Cash In - Total Cash Out
     */
    private void calculateTotal() {
        List<VSession> listVS = tableModel.getListVSession();

        double ttlSVou = 0;
        double ttlSCash = 0;
        double ttlSDisc = 0;
        double ttlSBal = 0;
        double ttlSTax = 0;
        double ttlSEI = 0;
        double ttlSEO = 0;
        double ttlPVou = 0;
        double ttlPCash = 0;
        double ttlPDisc = 0;
        double ttlPBal = 0;
        double ttlPTax = 0;
        double ttlPE = 0;
        double ttlRiVou = 0;
        double ttlRiCash = 0;
        double ttlRiBal = 0;
        double ttlRoVou = 0;
        double ttlRoCash = 0;
        double ttlRoBal = 0;
        double ttlCashIn = 0;
        double ttlCashOut = 0;
        double netCash = 0;
        double ttlCusPaid = 0;
        double ttlSupPaid = 0;
        double ttlExpIn = 0;
        double ttlExpOut = 0;

        HashMap<String, String> hmFilter = new HashMap();
        try {
            List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'PHARTTL'");
            if (listSF != null) {
                if (!listSF.isEmpty()) {
                    for (SessionFilter sf : listSF) {
                        hmFilter.put(sf.getKey().getTranSource(), sf.getKey().getTranSource());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("session filter : " + ex.getMessage());
        } finally {
            dao.close();
        }
        for (VSession vs : listVS) {
            if (vs.getKey().getSource().equals("Sale")) {
                ttlSVou += NumberUtil.NZero(vs.getVouTotal());
                ttlSCash += NumberUtil.NZero(vs.getPaid());
                ttlSDisc += NumberUtil.NZero(vs.getDiscount());
                ttlSBal += NumberUtil.NZero(vs.getBalance());
                ttlSTax += NumberUtil.NZero(vs.getTaxAmt());
                ttlSEI += NumberUtil.NZero(vs.getExpIn());
                ttlSEO += NumberUtil.NZero(vs.getExpense());
            } else if (vs.getKey().getSource().equals("Purchase")) {
                ttlPVou += NumberUtil.NZero(vs.getVouTotal());
                ttlPCash += NumberUtil.NZero(vs.getPaid());
                ttlPDisc += NumberUtil.NZero(vs.getDiscount());
                ttlPBal += NumberUtil.NZero(vs.getBalance());
                ttlPTax += NumberUtil.NZero(vs.getTaxAmt());
                ttlPE += NumberUtil.NZero(vs.getExpense());
            } else if (vs.getKey().getSource().equals("Return In")) {
                ttlRiVou += NumberUtil.NZero(vs.getVouTotal());
                ttlRiCash += NumberUtil.NZero(vs.getPaid());
                ttlRiBal += NumberUtil.NZero(vs.getBalance());
            } else if (vs.getKey().getSource().equals("Return Out")) {
                ttlRoVou += NumberUtil.NZero(vs.getVouTotal());
                ttlRoCash += NumberUtil.NZero(vs.getPaid());
                ttlRoBal += NumberUtil.NZero(vs.getBalance());
            } else if (vs.getKey().getSource().equals("Trader Payment")) {
                String strOption = vs.getTraderId().substring(0,3);
                if (strOption.equals("CUS")) {
                    ttlCusPaid += vs.getPaid();
                } else if (strOption.equals("SUP")) {
                    ttlSupPaid += (vs.getPaid() * -1);
                }else{
                    ttlCusPaid += vs.getPaid();
                }
            } else if (vs.getKey().getSource().equals("Cash In")) {
                ttlExpIn += NumberUtil.NZero(vs.getExpIn());
            } else if (vs.getKey().getSource().equals("Cash Out")) {
                ttlExpOut += NumberUtil.NZero(vs.getExpense());
            }
        }

        double ttlCFFees = 0;
        if (!cboDelete.getSelectedItem().equals("Deleted")) {
            String sessionId = "0";
            if (cboSession.getSelectedItem() instanceof Session) {
                Session session = (Session) cboSession.getSelectedItem();
                sessionId = session.getSessionId().toString();
            }

            String userId = "-";
            if (cboUser.getSelectedItem() instanceof Appuser) {
                Appuser user = (Appuser) cboUser.getSelectedItem();
                userId = user.getUserId();
            }

            String machineId = "-1";
            if (cboMachine.getSelectedItem() instanceof MachineInfo) {
                MachineInfo machine = (MachineInfo) cboMachine.getSelectedItem();
                machineId = machine.getMachineId().toString();
            }

            String locationId = "-1";
            if (cboLocation.getSelectedItem() instanceof Location) {
                Location location = (Location) cboLocation.getSelectedItem();
                locationId = location.getLocationId().toString();
            }

            String locationGroupId = "-1";
            if (cboLGroup.getSelectedItem() instanceof LocationGroup) {
                LocationGroup lg = (LocationGroup) cboLGroup.getSelectedItem();
                locationGroupId = lg.getId().toString();
            }

            String cusGroup = "-";
            if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
                CustomerGroup cg = (CustomerGroup) cboCusGroup.getSelectedItem();
                cusGroup = cg.getGroupId();
            }

            String tranType = cboTranType.getSelectedItem().toString();
            if (tranType.equals("All")) {
                tranType = "-";
            }

            String isDeleted = "false";
            if (!cboDelete.getSelectedItem().toString().equals("All")) {
                String strDelete = cboDelete.getSelectedItem().toString();
                if (strDelete.equals("Deleted")) {
                    isDeleted = "true";
                }
            }

            String sendStatus = "-";
            if (!cboSend.getSelectedItem().toString().equals("All")) {
                sendStatus = cboSend.getSelectedItem().toString();
            }

            String source = "-";
            if (!cboSource.getSelectedItem().toString().equals("All")) {
                source = cboSource.getSelectedItem().toString();
            }

            String currency = "-";
            if (cboCurrency.getSelectedItem() instanceof Currency) {
                Currency curr = (Currency) cboCurrency.getSelectedItem();
                currency = curr.getCurrencyCode();
            }

            String cusId = "-";
            if (!txtCusId.getText().trim().isEmpty()) {
                cusId = txtCusId.getText().trim();
            }

            String paidCurr = "-";
            if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
                Currency curr = (Currency) cboPaidCurrency.getSelectedItem();
                paidCurr = curr.getCurrencyCode();
            }

            String ptType = cboPatientType.getSelectedItem().toString();
            if (ptType.equals("All")) {
                ptType = "-";
            }

            if (hmFilter.containsKey("Total CF Fees")) {
                try {
                    ResultSet resultSet = dao.getPro("get_doctor_fee",
                            DateUtil.toDateStrMYSQL(txtFrom.getText()),
                            DateUtil.toDateStrMYSQL(txtTo.getText()),
                            sessionId, userId, machineId, locationId,
                            locationGroupId, cusGroup, tranType,
                            isDeleted, sendStatus, source,
                            currency, cusId, paidCurr, ptType
                    );
                    if (resultSet != null) {
                        resultSet.next();
                        ttlCFFees = resultSet.getDouble("samount");
                    }
                } catch (SQLException ex) {
                    log.error("printSessionD : " + ex.getMessage());
                } catch (Exception ex) {
                    log.error("calculateTotal : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }

        ttlCashIn = ttlSCash + ttlRoCash + ttlSEI + ttlCusPaid + ttlExpIn;
        ttlCashOut = ttlPCash + ttlRiCash + ttlSEO + ttlPE + ttlSupPaid + ttlExpOut + ttlCFFees;
        netCash = ttlCashIn - ttlCashOut;

        String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
        List<SessionTtl> listTtl = new ArrayList();

        if (ttlSVou != 0) {
            if (hmFilter.containsKey("Total Sale")) {
                listTtl.add(new SessionTtl("Total Sale", currency, ttlSVou));
            }
        }

        if (ttlSCash != 0) {
            if (hmFilter.containsKey("Total Cash Sale")) {
                listTtl.add(new SessionTtl("Total Cash Sale", currency, ttlSCash));
            }
        }

        if (ttlSDisc != 0) {
            if (hmFilter.containsKey("Total Sale Discount")) {
                listTtl.add(new SessionTtl("Total Sale Discount", currency, ttlSDisc));
            }
        }

        if (ttlSBal != 0) {
            if (hmFilter.containsKey("Total Sale Balance")) {
                listTtl.add(new SessionTtl("Total Sale Balance", currency, ttlSBal));
            }
        }

        if (ttlSTax != 0) {
            if (hmFilter.containsKey("Total Sale Tax")) {
                listTtl.add(new SessionTtl("Total Sale Tax", currency, ttlSTax));
            }
        }

        if (ttlSEI != 0) {
            if (hmFilter.containsKey("Total Sale Exp-In")) {
                listTtl.add(new SessionTtl("Total Sale Exp-In", currency, ttlSEI));
            }
        }

        if (ttlSEO != 0) {
            if (hmFilter.containsKey("Total Sale Exp-Out")) {
                listTtl.add(new SessionTtl("Total Sale Exp-Out", currency, ttlSEO));
            }
        }

        if (ttlPVou != 0) {
            if (hmFilter.containsKey("Total Purchase")) {
                listTtl.add(new SessionTtl("Total Purchase", currency, ttlPVou));
            }
        }

        if (ttlPCash != 0) {
            if (hmFilter.containsKey("Total Purchase Cash")) {
                listTtl.add(new SessionTtl("Total Purchase Cash", currency, ttlPCash));
            }
        }

        if (ttlPDisc != 0) {
            if (hmFilter.containsKey("Total Purchase Discount")) {
                listTtl.add(new SessionTtl("Total Purchase Discount", currency, ttlPDisc));
            }
        }

        if (ttlPBal != 0) {
            if (hmFilter.containsKey("Total Purchase Balance")) {
                listTtl.add(new SessionTtl("Total Purchase Balance", currency, ttlPBal));
            }
        }

        if (ttlPTax != 0) {
            if (hmFilter.containsKey("Total Purchase Tax")) {
                listTtl.add(new SessionTtl("Total Purchase Tax", currency, ttlPTax));
            }
        }

        if (ttlPE != 0) {
            if (hmFilter.containsKey("Total Purchase Exp")) {
                listTtl.add(new SessionTtl("Total Purchase Exp", currency, ttlPE));
            }
        }

        if (ttlRiVou != 0) {
            if (hmFilter.containsKey("Total Return In")) {
                listTtl.add(new SessionTtl("Total Return In", currency, ttlRiVou));
            }
        }

        if (ttlRiCash != 0) {
            if (hmFilter.containsKey("Total Cash Return In")) {
                listTtl.add(new SessionTtl("Total Cash Return In", currency, ttlRiCash * -1));
            }
        }

        if (ttlRiBal != 0) {
            if (hmFilter.containsKey("Total Return In Balance")) {
                listTtl.add(new SessionTtl("Total Return In Balance", currency, ttlRiBal));
            }
        }

        if (ttlRoVou != 0) {
            if (hmFilter.containsKey("Total Return Out")) {
                listTtl.add(new SessionTtl("Total Return Out", currency, ttlRoVou));
            }
        }

        if (ttlRoCash != 0) {
            if (hmFilter.containsKey("Total Return Out Cash")) {
                listTtl.add(new SessionTtl("Total Return Out Cash", currency, ttlRoCash));
            }
        }

        if (ttlRoBal != 0) {
            if (hmFilter.containsKey("Total Return Out Balance")) {
                listTtl.add(new SessionTtl("Total Return Out Balance", currency, ttlRoBal));
            }
        }

        if (ttlCusPaid != 0) {
            if (hmFilter.containsKey("Total Cus Paid")) {
                listTtl.add(new SessionTtl("Total Cus Paid", currency, ttlCusPaid));
            }
        }

        if (ttlSupPaid != 0) {
            if (hmFilter.containsKey("Total Sup Paid")) {
                listTtl.add(new SessionTtl("Total Sup Paid", currency, ttlSupPaid));
            }
        }

        if (ttlExpIn != 0) {
            if (hmFilter.containsKey("Total General Exp-In")) {
                listTtl.add(new SessionTtl("Total General Exp-In", currency, ttlExpIn));
            }
        }

        if (ttlExpOut != 0) {
            if (hmFilter.containsKey("Total General Exp-Out")) {
                listTtl.add(new SessionTtl("Total General Exp-Out", currency, ttlExpOut));
            }
        }

        if (ttlCashIn != 0) {
            if (hmFilter.containsKey("Total Cash In")) {
                listTtl.add(new SessionTtl("Total Cash In", currency, ttlCashIn));
            }
        }

        if (ttlCashOut != 0) {
            if (hmFilter.containsKey("Total Cash Out")) {
                listTtl.add(new SessionTtl("Total Cash Out", currency, ttlCashOut));
            }
        }

        if (ttlCFFees != 0) {
            if (hmFilter.containsKey("Total CF Fees")) {
                listTtl.add(new SessionTtl("Total CF Fees", currency, ttlCFFees * -1));
            }
        }

        if (netCash != 0) {
            if (hmFilter.containsKey("Net Cash")) {
                listTtl.add(new SessionTtl("Net Cash", currency, netCash));
            }
        }

        sTableModel.setListSessionTtl(listTtl);

        /*ttlTable.setValueAt(ttlVou, 0, 4);
         ttlTable.setValueAt(ttlPaid, 0, 6);
         ttlTable.setValueAt(ttlDiscount, 0, 8);
         ttlTable.setValueAt(ttlBalance, 0, 9);
         ttlTable.setValueAt(ttlExpense, 0, 10);
         ttlTable.setValueAt(ttlTaxAmt, 0, 11);*/
    }

    private void callculateTotalKSW() {
        List<VSession> listVS = tableModel.getListVSession();
        double cashSale = 0;
        double cashRetOut = 0;
        double cashPur = 0;
        double cashRetIn = 0;
        double payIn = 0;
        double payOut = 0;
        double cashIn = 0;
        double cashOut = 0;
        double netCash = 0;
        double cifh = 0;
        double cifm = 0;
        double cotm = 0;
        double cifs = 0;
        double cos = 0;

        for (VSession vs : listVS) {
            if (vs.getKey().getSource().equals("Sale")) {
                cashSale += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getKey().getSource().equals("Return Out")) {
                cashRetOut += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getKey().getSource().equals("Purchase")) {
                cashPur += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getKey().getSource().equals("Return In")) {
                cashRetIn += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getKey().getSource().equals("Trader Payment")) {
                if (vs.getDiscriminator().equals("C")) {
                    payIn += NumberUtil.NZero(vs.getPaid());
                } else if (vs.getDiscriminator().equals("S")) {
                    payOut += NumberUtil.NZero(vs.getPaid());
                }
            } else if (vs.getExpType().equals(
                    NumberUtil.NZeroInt(Util1.getPropValue("system.session.cifh")))) {
                cifh += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getExpType().equals(
                    NumberUtil.NZeroInt(Util1.getPropValue("system.session.cifm")))) {
                cifm += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getExpType().equals(
                    NumberUtil.NZeroInt(Util1.getPropValue("system.session.cotm")))) {
                cotm += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getExpType().equals(
                    NumberUtil.NZeroInt(Util1.getPropValue("system.session.cifs")))) {
                cifs += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getExpType().equals(
                    NumberUtil.NZeroInt(Util1.getPropValue("system.session.cos")))) {
                cos += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getKey().getSource().equals("Cash In")) {
                cashIn += NumberUtil.NZero(vs.getPaid());
            } else if (vs.getKey().getSource().equals("Cash Out")) {
                cashOut += NumberUtil.NZero(vs.getPaid());
            }
        }

        netCash = (cashSale + cashRetOut + payIn + cashIn + cifh + cifm + cifs)
                - (cashPur + cashRetIn + payOut + cashOut + cotm + cos);

        if (cashPur > 0) {
            cashPur = cashPur * -1;
        }

        if (cashRetIn > 0) {
            cashRetIn = cashRetIn * -1;
        }

        if (payOut > 0) {
            payOut = payOut * -1;
        }

        if (cashOut > 0) {
            cashOut = cashOut * -1;
        }

        if (cos > 0) {
            cos = cos * -1;
        }

        if (cotm > 0) {
            cotm = cotm * -1;
        }

        String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
        List<SessionTtl> listTtl = new ArrayList();

        if (cifh != 0) {
            listTtl.add(new SessionTtl("Cash In (from Home)", currency, cifh));
        }

        if (cifs != 0) {
            listTtl.add(new SessionTtl("Cash In (from SPZ)", currency, cifs));
        }

        if (cashSale != 0) {
            listTtl.add(new SessionTtl("Cash Sale", currency, cashSale));
        }

        if (payIn != 0) {
            listTtl.add(new SessionTtl("Cash In(Cus. Payment)", currency, payIn));
        }

        if (cifm != 0) {
            listTtl.add(new SessionTtl("Cash In(from MGZ)", currency, cifm));
        }

        if (cashRetOut != 0) {
            listTtl.add(new SessionTtl("Cash In(Return Out)", currency, cashRetOut));
        }

        if (cashIn != 0) {
            listTtl.add(new SessionTtl("Cash In (Total)", currency, cashIn));
        }

        if (cotm != 0) {
            listTtl.add(new SessionTtl("Cash Out (to MGZ)", currency, cotm));
        }

        if (cos != 0) {
            listTtl.add(new SessionTtl("Cash Out (to SPZ)", currency, cos));
        }

        if (cashPur != 0) {
            listTtl.add(new SessionTtl("Cash Out (Cash Purchase)", currency, cashPur));
        }

        if (payOut != 0) {
            listTtl.add(new SessionTtl("Cash Out (Sup. Payment)", currency, payOut));
        }

        if (cashRetIn != 0) {
            listTtl.add(new SessionTtl("Cash Out (Return In)", currency, cashRetIn));
        }

        if (cashOut != 0) {
            listTtl.add(new SessionTtl("Cash Out (Total Expense)", currency, cashOut));
        }

        if (cos != 0) {
            listTtl.add(new SessionTtl("Cash Out (to SPZ)", currency, cos));
        }

        listTtl.add(new SessionTtl("Net Cash", currency, netCash));

        sTableModel.setListSessionTtl(listTtl);
    }

    private void printSession() {
        insertTotalToTamp(sTableModel.getListSessionTtl());
        //Properties prop = ReportUtil.loadReportPathProperties();
        String rpName = Util1.getPropValue("system.pharmacy.session.check.print.report");
        rpName = Util1.isNullOrEmpty(rpName) ? "PharmacySessionCheckVoucher" : rpName;
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + rpName;
        Map<String, Object> params = new HashMap();
        String sessionName;
        String sessionUser;
        String sessionDate;
        String locId;
        String sessionId;
        String sessionCurr;
        String userId;
        String machineId;

        if (cboSession.getSelectedItem() instanceof Session) {
            sessionName = ((Session) cboSession.getSelectedItem()).getSessionName();
            sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        } else {
            sessionName = "All";
            sessionId = "-";
        }

        if (cboUser.getSelectedItem() instanceof Appuser) {
            sessionUser = ((Appuser) cboUser.getSelectedItem()).getUserName();
        } else {
            sessionUser = "All";
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            locId = ((Location) cboLocation.getSelectedItem()).getLocationId().toString();
        } else {
            locId = "-";
        }

        if (txtFrom.getText().equals(txtTo.getText())) {
            sessionDate = txtFrom.getText();
        } else {
            sessionDate = txtFrom.getText() + " To " + txtTo.getText();
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            sessionCurr = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
        } else {
            sessionCurr = "All";
        }

        if (cboUser.getSelectedItem() instanceof Appuser) {
            userId = ((Appuser) cboUser.getSelectedItem()).getUserId();
        } else {
            userId = "-";
        }

        if (cboMachine.getSelectedItem() instanceof MachineInfo) {
            machineId = ((MachineInfo) cboMachine.getSelectedItem()).getMachineId().toString();
        } else {
            machineId = "All";
        }
        params.put("user", Global.loginUser.getUserShortName());
        params.put("session_name", sessionName);
        params.put("session_user", sessionUser);
        params.put("session_date", sessionDate);
        params.put("p_location_id", locId);
        params.put("session_fdate", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("session_tdate", DateUtil.toDateStrMYSQL(txtTo.getText()));
        params.put("sess_id", sessionId);
        params.put("session_currency", sessionCurr);
        params.put("p_user_id", userId);
        params.put("tran_user_id", Global.loginUser.getUserId());
        params.put("p_machine_id", machineId);
        params.put("comp_name", Util1.getPropValue("report.company.name"));
        params.put("p_user_name", cboUser.getSelectedItem().toString());
        params.put("p_session_name", cboSession.getSelectedItem().toString());
        params.put("p_loc_name", cboLocation.getSelectedItem().toString());
        params.put("data_date", "Between " + txtFrom.getText()
                + " and " + txtTo.getText());
        params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
        //Need to change
        //ReportUtil.viewReport(reportPath, params, sTableModel.getListSessionTtl());
        //insertTotalToTamp(sTableModel.getListSessionTtl());
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
    }

    private void insertTotalToTamp(List<SessionTtl> listSessionTtl) {
        try {
            dao.execSql("delete from tmp_session_total where user_id = '"
                    + Global.machineId + "'");
            for (SessionTtl sttl : listSessionTtl) {
                TmpSessionTotal tst = new TmpSessionTotal(sttl.getDesc(), sttl.getCurrency(),
                        sttl.getTtlPaid(), Global.machineId);
                dao.save(tst);
            }
        } catch (Exception ex) {
            log.error("insertTotalToTamp : " + ex.toString());
        }
    }

    class TotalTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            Component c = super.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);

            c.setBackground(new Color(199, 225, 186));

            return c;
        }
    }

    private void getCustomer() {
        if (txtCusId.getText() != null && !txtCusId.getText().isEmpty()) {
            try {

                if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    //cus = (Patient) dao.find(Patient.class, txtCusId.getText());
                    dao.open();
                    Patient ptt = (Patient) dao.find(Patient.class, txtCusId.getText());
                    dao.close();

                    if (ptt == null) {
                        txtCusId.setText(null);
                        txtCusName.setText(null);
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid Patient code.",
                                "Patient Code", JOptionPane.ERROR_MESSAGE);
                        txtCusId.requestFocus();
                    } else {
                        selected("PatientList", ptt);
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    //String url = Util1.getPropValue("system.app.school.url");
                    dao.open();
                    SchoolDataAccess sda = new SchoolDataAccess(dao);
                    String stuNo = txtCusId.getText();
                    VMarchant vm = sda.getMarchant(stuNo);
                    dao.close();

                    if (vm == null) {
                        txtCusId.setText(null);
                        txtCusName.setText(null);

                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid student number or not enroll student.",
                                "Student Number", JOptionPane.ERROR_MESSAGE);
                    } else {
                        txtCusId.setText(vm.getPersonNumber());
                        txtCusName.setText(vm.getPersonName());
                    }
                } else {
                    /*dao.open();
                    Trader cus = (Trader) dao.find(Trader.class, txtCusId.getText());
                    dao.close();*/

                    Trader cus = getTrader(txtCusId.getText().toUpperCase());
                    if (cus == null) {
                        txtCusId.setText(null);
                        txtCusName.setText(null);
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid customer code.",
                                "Trader Code", JOptionPane.ERROR_MESSAGE);

                    } else {
                        selected("CustomerList", cus);
                    }
                }
            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtCusId.requestFocus();
        }
    }

    private void printSessionD() {
        //Properties prop = ReportUtil.loadReportPathProperties();
        String rpName = Util1.getPropValue("system.pharmacy.session.check.print.reportd");
        rpName = Util1.isNullOrEmpty(rpName) ? "PharmacySessionCheckDetail" : rpName;
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + rpName;
        Map<String, Object> params = new HashMap();
        params.put("data_date", "Between " + txtFrom.getText()
                + " and " + txtTo.getText());
        params.put("comp_name", Util1.getPropValue("report.company.name"));
        params.put("prm_from", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_to", DateUtil.toDateStrMYSQL(txtTo.getText()));
        if (cboSession.getSelectedItem() instanceof Session) {
            String sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
            params.put("sess_id", sessionId);
        } else {
            params.put("sess_id", "-");
        }

        //New add 20190727
        if (cboUser.getSelectedItem() instanceof Appuser) {
            params.put("prm_user_id", ((Appuser) cboUser.getSelectedItem()).getUserId());
        } else {
            params.put("prm_user_id", "-");
        }

        if (cboMachine.getSelectedItem() instanceof MachineInfo) {
            params.put("prm_machine_id", ((MachineInfo) cboMachine.getSelectedItem()).getMachineId());
        } else {
            params.put("prm_machine_id", -1);
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            params.put("prm_location_id", ((Location) cboLocation.getSelectedItem()).getLocationId());
        } else {
            params.put("prm_location_id", -1);
        }

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            params.put("prm_cus_group", ((CustomerGroup) cboCusGroup.getSelectedItem()).getGroupId());
        } else {
            params.put("prm_cus_group", "-");
        }

        if (cboDelete.getSelectedItem().equals("Normal")) {
            params.put("prm_deleted", false);
        } else if (cboDelete.getSelectedItem().equals("Deleted")) {
            params.put("prm_deleted", true);
        }

        if (cboDelete.getSelectedItem().equals("Normal")) {
            params.put("prm_deleted", false);
        } else if (cboDelete.getSelectedItem().equals("Deleted")) {
            params.put("prm_deleted", true);
        }

        if (cboSource.getSelectedItem().equals("All")) {
            params.put("prm_source", "-");
        } else {
            params.put("prm_source", cboSource.getSelectedItem().toString());
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            params.put("prm_curr", ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode());
        } else {
            params.put("prm_curr", "-");
        }

        if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
            params.put("prm_paid_curr", ((Currency) cboPaidCurrency.getSelectedItem()).getCurrencyCode());
        } else {
            params.put("prm_paid_curr", "-");
        }

        if (cboLGroup.getSelectedItem() instanceof LocationGroup) {
            params.put("prm_loc_group", ((LocationGroup) cboLGroup.getSelectedItem()).getId());
        } else {
            params.put("prm_loc_group", -1);
        }

        if (cboSend.getSelectedItem().equals("All")) {
            params.put("prm_send", "-");
        } else {
            params.put("prm_send", cboSend.getSelectedItem().toString());
        }

        try {
            List<SessionFilter> listSF = dao.findAllHSQL(
                    "select o from SessionFilter o where o.key.progId = 'PHARFILTER'"
                    + " and o.rptParameter <> '-' and o.apply = true"
            );
            if (listSF != null) {
                if (!listSF.isEmpty()) {
                    for (SessionFilter sf : listSF) {
                        if (sf.isApply()) {
                            params.put(sf.getRptParameter(), "@");
                        } else {
                            params.put(sf.getRptParameter(), "-");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("session filter : " + ex.getMessage());
        } finally {
            dao.close();
        }

        if (!txtCusId.getText().trim().isEmpty()) {
            params.put("prm_cus_id", txtCusId.getText().trim());
        } else {
            params.put("prm_cus_id", "-");
        }
        //===========================================================================================

        if (cboTranType.getSelectedItem().toString().equals("All")) {
            params.put("prm_tran_type", "-");
        } else {
            params.put("prm_tran_type", cboTranType.getSelectedItem().toString());
        }

        String patientType = cboPatientType.getSelectedItem().toString();
        if (patientType.equals("All")) {
            patientType = "-";
        }
        params.put("patient_type", patientType);

        if (cboDelete.getSelectedItem().equals("Deleted")) {
            params.put("dfees", 0);
        } else {
            try {
                String sessionId = "0";
                if (cboSession.getSelectedItem() instanceof Session) {
                    Session session = (Session) cboSession.getSelectedItem();
                    sessionId = session.getSessionId().toString();
                }

                String userId = "-";
                params.put("p_user_id", userId);
                if (cboUser.getSelectedItem() instanceof Appuser) {
                    Appuser user = (Appuser) cboUser.getSelectedItem();
                    userId = user.getUserId();
                    params.put("p_user_id", userId);
                }

                String machineId = "-1";
                if (cboMachine.getSelectedItem() instanceof MachineInfo) {
                    MachineInfo machine = (MachineInfo) cboMachine.getSelectedItem();
                    machineId = machine.getMachineId().toString();
                }

                String locationId = "-1";
                params.put("p_loc_id", locationId);
                if (cboLocation.getSelectedItem() instanceof Location) {
                    Location location = (Location) cboLocation.getSelectedItem();
                    locationId = location.getLocationId().toString();
                    params.put("p_loc_id", locationId);
                }

                String locationGroupId = "-1";
                if (cboLGroup.getSelectedItem() instanceof LocationGroup) {
                    LocationGroup lg = (LocationGroup) cboLGroup.getSelectedItem();
                    locationGroupId = lg.getId().toString();
                }

                String cusGroup = "-";
                if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
                    CustomerGroup cg = (CustomerGroup) cboCusGroup.getSelectedItem();
                    cusGroup = cg.getGroupId();
                }

                String tranType = cboTranType.getSelectedItem().toString();
                if (tranType.equals("All")) {
                    tranType = "-";
                }

                String isDeleted = "false";
                if (!cboDelete.getSelectedItem().toString().equals("All")) {
                    String strDelete = cboDelete.getSelectedItem().toString();
                    if (strDelete.equals("Deleted")) {
                        isDeleted = "true";
                    }
                }

                String sendStatus = "-";
                if (!cboSend.getSelectedItem().toString().equals("All")) {
                    sendStatus = cboSend.getSelectedItem().toString();
                }

                String source = "-";
                if (!cboSource.getSelectedItem().toString().equals("All")) {
                    source = cboSource.getSelectedItem().toString();
                }

                String currency = "-";
                if (cboCurrency.getSelectedItem() instanceof Currency) {
                    Currency curr = (Currency) cboCurrency.getSelectedItem();
                    currency = curr.getCurrencyCode();
                }

                String cusId = "-";
                if (!txtCusId.getText().trim().isEmpty()) {
                    cusId = txtCusId.getText().trim();
                }

                String paidCurr = "-";
                if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
                    Currency curr = (Currency) cboPaidCurrency.getSelectedItem();
                    paidCurr = curr.getCurrencyCode();
                }

                if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    ResultSet resultSet = dao.getPro("get_doctor_fee",
                            DateUtil.toDateStrMYSQL(txtFrom.getText()),
                            DateUtil.toDateStrMYSQL(txtTo.getText()),
                            sessionId, userId, machineId, locationId,
                            locationGroupId, cusGroup, tranType,
                            isDeleted, sendStatus, source,
                            currency, cusId, paidCurr, patientType
                    );
                    if (resultSet != null) {
                        resultSet.next();
                        params.put("dfees", resultSet.getDouble("samount"));
                    } else {
                        params.put("dfees", 0);
                    }
                } else {
                    params.put("dfees", 0);
                }

            } catch (SQLException ex) {
                log.error("printSessionD : " + ex.getMessage());
            } catch (Exception ex) {
                log.error("printSessionD : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
        params.put("p_user_name", cboUser.getSelectedItem().toString());
        params.put("p_session_name", cboSession.getSelectedItem().toString());
        params.put("p_loc_name", cboLocation.getSelectedItem().toString());
        ReportUtil.viewReport(reportPath, params, dao.getConnection());
    }

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            if (!traderId.contains("SUP")) {
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    if (!traderId.contains("CUS")) {
                        traderId = "CUS" + traderId;
                    }
                }
            }
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                int locationId = -1;
                if (cboLocation.getSelectedItem() instanceof Location) {
                    locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                }
                List<Trader> listTrader = dao.findAllHSQL("select o from Trader o where "
                        + "o.active = true and o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ") order by o.traderName");
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                cus = (Trader) dao.find(Trader.class, traderId);
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
    }

    private void viewReport() {
        int row = tblSession.convertRowIndexToModel(tblSession.getSelectedRow());
        if (row >= 0) {
            String vouNo = tableModel.getVSession(row).getKey().getInvId();
            String reportName = Util1.getPropValue("report.file.comp");
            String reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + reportName;
            Map<String, Object> params = new HashMap();
            String compName = Util1.getPropValue("report.company.name");
            params.put("comp_name", compName);
            params.put("comp_address", Util1.getPropValue("report.address"));
            params.put("phone", Util1.getPropValue("report.phone"));
            params.put("inv_id", vouNo);
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
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
        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboSession = new javax.swing.JComboBox();
        butClear = new javax.swing.JButton();
        butSearch = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cboUser = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboMachine = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        txtInvTotal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboTranType = new javax.swing.JComboBox();
        cboDelete = new javax.swing.JComboBox();
        cboSource = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        cboPaidCurrency = new javax.swing.JComboBox();
        cboCondition = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        txtCusId = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        cboSend = new javax.swing.JComboBox();
        butToAcc = new javax.swing.JButton();
        butPrint = new javax.swing.JButton();
        butPrintD = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        cboLGroup = new javax.swing.JComboBox();
        cboCusGroup = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cboPatientType = new javax.swing.JComboBox();
        butCheckPoint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSession = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSessTtl = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Search"));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From ");

        txtFrom.setEditable(false);
        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        txtTo.setEditable(false);
        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Session");

        cboSession.setFont(Global.textFont);

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(butSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtTo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFrom, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboSession, 0, 113, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butClear)
                    .addComponent(butSearch))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("User ");

        cboUser.setFont(Global.textFont);
        cboUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUserActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Machine ");

        cboMachine.setFont(Global.textFont);
        cboMachine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMachineActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Location");

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Inv Total");

        txtInvTotal.setFont(Global.textFont);
        txtInvTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInvTotalActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Tran Type");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Del");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Source ");

        cboTranType.setFont(Global.textFont);
        cboTranType.setModel(new javax.swing.DefaultComboBoxModel(
            new String[] { "All", "Cash", "Balance",
                "Discount", "Tax"}));
    cboTranType.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboTranTypeActionPerformed(evt);
        }
    });

    cboDelete.setFont(Global.textFont);
    cboDelete.setModel(new javax.swing.DefaultComboBoxModel(
        new String[] { "Normal", "Deleted" }));
cboDelete.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        cboDeleteActionPerformed(evt);
    }
    });

    cboSource.setFont(Global.textFont);
    cboSource.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Sale", "Purchase", "Return In", "Return Out", "Trader Payment", "General Expense" }));
    cboSource.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboSourceActionPerformed(evt);
        }
    });

    jLabel12.setFont(Global.lableFont);
    jLabel12.setText("Currency");

    cboCurrency.setFont(Global.textFont);
    cboCurrency.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboCurrencyActionPerformed(evt);
        }
    });

    jLabel13.setFont(Global.lableFont);
    jLabel13.setText("P-Currency");

    cboPaidCurrency.setFont(Global.textFont);
    cboPaidCurrency.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboPaidCurrencyActionPerformed(evt);
        }
    });

    cboCondition.setFont(Global.textFont);
    cboCondition.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ">", ">=", "=", "<", "<=" }));
    cboCondition.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboConditionActionPerformed(evt);
        }
    });

    jLabel11.setFont(Global.lableFont);
    jLabel11.setText("Customer");

    txtCusId.setFont(Global.textFont);
    txtCusId.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtCusIdActionPerformed(evt);
        }
    });

    txtCusName.setEditable(false);
    txtCusName.setFont(Global.textFont);
    txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            txtCusNameMouseClicked(evt);
        }
    });
    txtCusName.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtCusNameActionPerformed(evt);
        }
    });

    cboSend.setFont(Global.textFont);
    cboSend.setModel(new javax.swing.DefaultComboBoxModel(
        new String[] { "All", "Send" , "Not Send"}));
cboSend.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        cboSendActionPerformed(evt);
    }
    });

    butToAcc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    butToAcc.setText("To Acc");
    butToAcc.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            butToAccActionPerformed(evt);
        }
    });

    butPrint.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    butPrint.setText("Print");
    butPrint.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            butPrintActionPerformed(evt);
        }
    });

    butPrintD.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    butPrintD.setText("Print D");
    butPrintD.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            butPrintDActionPerformed(evt);
        }
    });

    jLabel15.setFont(Global.lableFont);
    jLabel15.setText("L-Group ");

    cboLGroup.setFont(Global.textFont);
    cboLGroup.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboLGroupActionPerformed(evt);
        }
    });

    cboCusGroup.setFont(Global.textFont);
    cboCusGroup.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cboCusGroupActionPerformed(evt);
        }
    });

    jLabel16.setFont(Global.lableFont);
    jLabel16.setText("Cus-Group ");

    jLabel14.setFont(Global.lableFont);
    jLabel14.setText("Patient Type ");

    cboPatientType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "OPD", "Inpatient" }));

    butCheckPoint.setText("Check Point");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboMachine, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLocation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(18, 18, 18)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(cboCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtInvTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cboPaidCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(123, 123, 123)
                                    .addComponent(jLabel15)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(butCheckPoint)
                                        .addComponent(cboLGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(butPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(butPrintD, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(cboDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(butToAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(cboTranType, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboSource, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboSend, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel11)
                        .addComponent(jLabel16))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel14)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboPatientType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(txtCusId, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboLocation, cboMachine, cboUser});

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel4, jLabel5, jLabel6});

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel12, jLabel13, jLabel15, jLabel7, jLabel8, jLabel9});

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboCurrency, cboPaidCurrency, cboSource});

    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cboMachine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(cboDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butToAcc))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(cboCondition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtInvTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(cboPaidCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butPrint)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTranType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(cboSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(9, 9, 9)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtCusId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(cboLGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(butPrintD)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(cboPatientType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(butCheckPoint))))
    );

    tblSession.setFont(Global.textFont);
    tblSession.setModel(tableModel);
    tblSession.setRowHeight(23);
    tblSession.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblSessionMouseClicked(evt);
        }
    });
    jScrollPane1.setViewportView(tblSession);

    jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Total"));

    tblSessTtl.setFont(Global.textFont);
    tblSessTtl.setModel(sTableModel);
    tblSessTtl.setRowHeight(23);
    jScrollPane2.setViewportView(tblSessTtl);

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
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 586, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1190, Short.MAX_VALUE))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
            .addContainerGap())
    );
    }// </editor-fold>//GEN-END:initComponents

  private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
      search();
  }//GEN-LAST:event_butSearchActionPerformed

  private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtFrom.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtFromMouseClicked

  private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtTo.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtToMouseClicked

    private void cboSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSendActionPerformed

    private void butToAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butToAccActionPerformed
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            List<VSession> listVS;
            listVS = tableModel.getListVSession();
            if (listVS.size() > 0) {
                if (!Global.mqConnection.isStatus()) {
                    String mqUrl = Util1.getPropValue("system.mqserver.url");
                    Global.mqConnection = new ActiveMQConnection(mqUrl);
                }
                for (int i = 0; i < listVS.size(); i++) {
                    if (Global.mqConnection != null) {
                        if (Global.mqConnection.isStatus()) {
                            try {
                                ActiveMQConnection mq = Global.mqConnection;
                                MapMessage msg = mq.getMapMessageTemplate();
                                msg.setString("program", Global.programId);
                                String strEntiry = getExpStr(listVS.get(i).getKey().getSource(), 0);
                                msg.setString("entity", strEntiry);
                                msg.setString("vouNo", listVS.get(i).getKey().getInvId());
                                msg.setString("remark", listVS.get(i).getRefNo());
                                Trader trader = (Trader) dao.find(Trader.class, listVS.get(i).getTraderId());
                                msg.setString("cusId", trader.getAccountId());
                                msg.setString(getExpStr(listVS.get(i).getKey().getSource(), 1), DateUtil.toDateStr(listVS.get(i).getTranDate(), "yyyy-MM-dd"));
                                msg.setBoolean("deleted", false);
                                if (NumberUtil.NZero(listVS.get(i).getPaid()) < 0) {
                                    msg.setDouble("payment", listVS.get(i).getPaid() * -1);
                                } else {
                                    msg.setDouble("payment", NumberUtil.NZero(listVS.get(i).getPaid()));
                                }
                                //msg.setDouble("vouTotal", listVS.get(i).getVouTotal());
                                msg.setDouble("vouTotal", NumberUtil.NZero(listVS.get(i).getBalance()));
                                msg.setDouble("discount", NumberUtil.NZero(listVS.get(i).getDiscount()));

                                msg.setDouble("tax", listVS.get(i).getTaxAmt());
                                Currency currency = (Currency) dao.find(Currency.class, listVS.get(i).getCurrency());
                                msg.setString("currency", currency.getCurrencyAccId());

                                if (trader.getTraderGroup() != null) {
                                    msg.setString("sourceAccId", trader.getTraderGroup().getAccountId());
                                } else {
                                    msg.setString("sourceAccId", "-");
                                }
                                msg.setString("queueName", "INVENTORY");

                                if (trader.getTraderId().contains("CUS")) {
                                    msg.setString("traderType", "CUS");
                                } else {
                                    msg.setString("traderType", "SUP");
                                }

                                msg.setString("dept", "-");
                                if (trader.getTraderGroup() != null) {
                                    if (trader.getTraderGroup().getDeptId() != null) {
                                        if (!trader.getTraderGroup().getDeptId().isEmpty()) {
                                            msg.setString("dept", trader.getTraderGroup().getDeptId().trim());
                                        }
                                    }
                                }

                                if (strEntiry.equals("PAYMENT")) {
                                    msg.setString("account_id", "-");
                                    String accountId = listVS.get(i).getAccountId();
                                    if (accountId != null) {
                                        msg.setString("account_id", accountId.trim());
                                    } else {
                                        accountId = trader.getTraderGroup().getAccountId();
                                        msg.setString("account_id", accountId.trim());
                                    }
                                }
                                mq.sendMessage(Global.queueName, msg);
                            } catch (Exception ex) {
                                log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + listVS.get(i).getKey().getInvId() + " - " + ex);
                            }

                            //i = listVS.size();
                        }
                    }

                }
            }
        }
    }//GEN-LAST:event_butToAccActionPerformed

    private void cboConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboConditionActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboConditionActionPerformed

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        printSession();
    }//GEN-LAST:event_butPrintActionPerformed

    private void cboPaidCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaidCurrencyActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboPaidCurrencyActionPerformed

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboCurrencyActionPerformed

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        if (txtCusId.getText().trim().isEmpty()) {
            txtCusName.setText(null);
        } else {
            getCustomer();
        }
        applyFilter();
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void cboSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSourceActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboSourceActionPerformed

    private void cboDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDeleteActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboDeleteActionPerformed

    private void cboTranTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTranTypeActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboTranTypeActionPerformed

    private void txtInvTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInvTotalActionPerformed
        applyFilter();
    }//GEN-LAST:event_txtInvTotalActionPerformed

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void cboMachineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMachineActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboMachineActionPerformed

    private void cboUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUserActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboUserActionPerformed

    private void butPrintDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintDActionPerformed
        printSessionD();
    }//GEN-LAST:event_butPrintDActionPerformed

    private void txtCusNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusNameActionPerformed

    private void cboLGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLGroupActionPerformed
        if (bindStatus) {
            applyFilter();
        }
    }//GEN-LAST:event_cboLGroupActionPerformed

    private void cboCusGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCusGroupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboCusGroupActionPerformed

    private void tblSessionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSessionMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() > 1) {
            viewReport();
        }
    }//GEN-LAST:event_tblSessionMouseClicked

    public String getExpStr(String str, int i) {
        if (i == 0) {
            if (!str.isEmpty()) {
                switch (str) {
                    case "Sale":
                        return "SALE";
                    case "Purchase":
                        return "PURCHASE";
                    case "Return In":
                        return "RETURNIN";
                    case "Return Out":
                        return "RETURNOUT";
                    case "Trader Payment":
                        return "PAYMENT";
                }
            }
        } else {
            switch (str) {
                case "Sale":
                    return "saleDate";
                case "Purchase":
                    return "purDate";
                case "Return In":
                    return "retInDate";
                case "Return Out":
                    return "retOutDate";
                case "Trader Payment":
                    return "payDate";
            }
        }
        return str;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCheckPoint;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butPrintD;
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butToAcc;
    private javax.swing.JComboBox cboCondition;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboCusGroup;
    private javax.swing.JComboBox cboDelete;
    private javax.swing.JComboBox cboLGroup;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboMachine;
    private javax.swing.JComboBox cboPaidCurrency;
    private javax.swing.JComboBox cboPatientType;
    private javax.swing.JComboBox cboSend;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JComboBox cboSource;
    private javax.swing.JComboBox cboTranType;
    private javax.swing.JComboBox cboUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblSessTtl;
    private javax.swing.JTable tblSession;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtInvTotal;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
