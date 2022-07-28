/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Doctor;
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
import com.cv.app.pharmacy.ui.common.SessionTableModel1;
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
public class SessionCheck1 extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(SessionCheck1.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final SessionTableModel1 tableModel = new SessionTableModel1();
    private final TableRowSorter<TableModel> sorter;
    private boolean bindStatus = false;
    private final javax.swing.JTable ttlTable = new javax.swing.JTable(1, 14);
    private String strSessionFilter = "-";
    private int mouseClick = 2;

    /**
     * Creates new form SessionCheck
     */
    public SessionCheck1() {
        initComponents();
        assignDate();
        initCombo();
        initTable();
        getSessionFilter();
        decimalFormat();
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
            BindingUtil.BindComboFilter(cboSaleMan,
                    dao.findAllHSQL("select o from Doctor o order by o.doctorName"));

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
        String strWhere = "select s from VSession s where s.tranDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";

        if (!strSessionFilter.equals("-")) {
            strWhere = strWhere + " and s.key.source in (" + strSessionFilter + ")";
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            Session session = (Session) cboSession.getSelectedItem();
            strWhere = strWhere + " and s.sessionId = " + session.getSessionId();
        }

        //String strWhere = "";
        if (cboUser.getSelectedItem() instanceof Appuser) {
            Appuser user = (Appuser) cboUser.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " s.userId = '" + user.getUserId() + "'";
            } else {
                strWhere = strWhere + " and s.userId = '" + user.getUserId() + "'";
            }
        }

        if (cboMachine.getSelectedItem() instanceof MachineInfo) {
            MachineInfo machine = (MachineInfo) cboMachine.getSelectedItem();
            String machineId = machine.getMachineId().toString();

            if (machineId.length() == 1) {
                machineId = "0" + machineId;
            }

            if (strWhere.isEmpty()) {
                strWhere = " s.machineId = '" + machineId + "'";
            } else {
                strWhere = strWhere + " and s.machineId = '" + machineId + "'";
            }
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " s.locationId = " + location.getLocationId();
            } else {
                strWhere = strWhere + " and s.locationId = " + location.getLocationId();
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
                        strWhere = " s.locationId in (" + strLocation + ")";
                    } else {
                        strWhere = strWhere + " and s.locationId in (" + strLocation + ")";
                    }
                }
            }
        } catch (Exception ex) {
            log.error("LocationGroupMapping : " + ex.getMessage());
        } finally {
            dao.close();
        }

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            CustomerGroup cg = (CustomerGroup) cboCusGroup.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " s.groupId = '" + cg.getGroupId() + "'";
            } else {
                strWhere = strWhere + " and s.groupId = '" + cg.getGroupId() + "'";
            }
        }

        if (cboSaleMan.getSelectedItem() instanceof Doctor) {
            Doctor saleMan = (Doctor) cboSaleMan.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " s.doctorId = '" + saleMan.getDoctorId() + "'";
            } else {
                strWhere = strWhere + " and s.doctorId = '" + saleMan.getDoctorId() + "'";
            }
        }

        String tranType = cboTranType.getSelectedItem().toString();
        if (tranType.equals("Cash")) {
            if (strWhere.isEmpty()) {
                strWhere = " s.paid <> 0 ";
            } else {
                strWhere = strWhere + " and s.paid <> 0";
            }
        } else if (tranType.equals("Balance")) {
            if (strWhere.isEmpty()) {
                strWhere = " s.balance <> 0 ";
            } else {
                strWhere = strWhere + " and s.balance <> 0 ";
            }
        } else if (tranType.equals("Discount")) {
            if (strWhere.isEmpty()) {
                strWhere = " s.discount <> 0 ";
            } else {
                strWhere = strWhere + " and s.discount <> 0 ";
            }
        } else if (tranType.equals("Tax")) {
            if (strWhere.isEmpty()) {
                strWhere = " s.taxAmt <> 0 ";
            } else {
                strWhere = strWhere + " and s.taxAmt <> 0 ";
            }
        }

        if (!cboDelete.getSelectedItem().toString().equals("All")) {
            String strDelete = cboDelete.getSelectedItem().toString();
            boolean isDeleted = false;

            if (strDelete.equals("Deleted")) {
                isDeleted = true;
            }

            if (strWhere.isEmpty()) {
                strWhere = " s.deleted = " + isDeleted;
            } else {
                strWhere = strWhere + " and s.deleted = " + isDeleted;
            }
        }

        if (!cboSend.getSelectedItem().toString().equals("All")) {
            String strStauts = cboSend.getSelectedItem().toString();
            String isStatus = "";

            if (strStauts.equals("Send")) {
                isStatus = " s.intgUpdStatus <> null";
            } else if (strStauts.equals("Not Send")) {
                isStatus = " s.intgUpdStatus = null";
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
                    strWhere = " (s.key.source = 'Cash In' or s.key.source = 'Cash Out')";
                } else {
                    strWhere = strWhere + " and (s.key.source = 'Cash In' or s.key.source = 'Cash Out')";
                }
            } else if (strWhere.isEmpty()) {
                strWhere = " s.key.source = '" + strSource + "'";
            } else {
                strWhere = strWhere + " and s.key.source = '" + strSource + "'";
            }
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            Currency currency = (Currency) cboCurrency.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " s.currency = '" + currency.getCurrencyCode() + "'";
            } else {
                strWhere = strWhere + " and s.currency = '" + currency.getCurrencyCode() + "'";
            }
        }

        if (!txtCusId.getText().trim().isEmpty()) {
            if (strWhere.isEmpty()) {
                strWhere = " s.traderId = '" + txtCusId.getText().trim() + "'";
            } else {
                strWhere = strWhere + " and s.traderId = '" + txtCusId.getText().trim() + "'";
            }
        }

        if (!txtInvTotal.getText().trim().isEmpty()) {
            String sign = (String) cboCondition.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " s.vouTotal " + sign + " " + txtInvTotal.getText().trim();
            } else {
                strWhere = strWhere + " and s.vouTotal " + sign + " " + txtInvTotal.getText().trim();
            }
        }

        if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
            Currency currency = (Currency) cboPaidCurrency.getSelectedItem();

            if (strWhere.isEmpty()) {
                strWhere = " s.paidCurrency = '" + currency.getCurrencyCode() + "'";
            } else {
                strWhere = strWhere + " and s.paidCurrency = '" + currency.getCurrencyCode() + "'";
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
        return strWhere;
    }

    private void search() {
        try {
            List<VSession> listVS = dao.findAllHSQL(getHSQL());
            tableModel.setListVSession(listVS);
            System.out.println("search : " + listVS.size());
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
        tblSession.getColumnModel().getColumn(0).setPreferredWidth(18);//Tran Date
        tblSession.getColumnModel().getColumn(1).setPreferredWidth(50);//Vou No
        tblSession.getColumnModel().getColumn(2).setPreferredWidth(10);//Ref. Vou.
        tblSession.getColumnModel().getColumn(3).setPreferredWidth(130);//Cus-Name
        tblSession.getColumnModel().getColumn(4).setPreferredWidth(30);//V-Total
        tblSession.getColumnModel().getColumn(5).setPreferredWidth(30);//Paid
        tblSession.getColumnModel().getColumn(6).setPreferredWidth(5);//Discount
        tblSession.getColumnModel().getColumn(7).setPreferredWidth(30);//Balance
        tblSession.getColumnModel().getColumn(8).setPreferredWidth(5);//User
        tblSession.getColumnModel().getColumn(9).setPreferredWidth(10);//Source
        tblSession.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
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
            log.error("LocationGroupMapping : " + ex.getMessage());
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

        String ptType = cboSaleMan.getSelectedItem().toString();
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
            log.error("SessionFilter : " + ex.getMessage());
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
        //tableModel.applyFilter(getFilterString());
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

                //sTableModel.setListSessionTtl(listTtl);
            } else {
                //sTableModel.removeAll();
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

        //For Whole Sale
        double ttlSVou = 0;
        double ttlSCash = 0;
        double ttlSDisc = 0;
        double ttlSBal = 0;
        double ttlSTax = 0;
        //For Retail Sale
        double ttlSRVou = 0;
        double ttlSRCash = 0;
        double ttlSRDisc = 0;
        double ttlSRBal = 0;
        double ttlSRTax = 0;

        double ttlSEI = 0;
        double ttlSEO = 0;
        double ttlPVou = 0;
        double ttlPCash = 0;
        double ttlPDisc = 0;
        double ttlPBal = 0;
        double ttlPTax = 0;
        double ttlPE = 0;
        //Whole Sale Return In
        double ttlRiVou = 0;
        double ttlRiCash = 0;
        double ttlRiBal = 0;
        //Retail Sale Return In
        double ttlRRiVou = 0;
        double ttlRRiCash = 0;
        double ttlRRiBal = 0;

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
        double ttlTranfSale = 0;
        double ttlTranfPurchase = 0;

        HashMap<String, String> hmFilter = new HashMap();
        String retailCusId = Util1.getPropValue("system.default.customer");
        try {
            List<SessionFilter> listSF = dao.findAllHSQL("select o from SessionFilter o where o.key.progId = 'PHARTTL'");
            if (listSF != null) {
                if (!listSF.isEmpty()) {
                    for (SessionFilter sf : listSF) {
                        hmFilter.put(sf.getKey().getTranSource(), sf.getKey().getTranSource());
                    }
                }
            }

            String prefix = Util1.getPropValue("system.sale.emitted.prifix");
            if (prefix.equals("Y")) {
                List<Trader> listT = dao.findAllHSQL("select o from Trader o where o.stuCode = '" + retailCusId + "'");
                if (listT != null) {
                    if (!listT.isEmpty()) {
                        retailCusId = listT.get(0).getTraderId();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("calculateTotal : " + ex.getMessage());
        } finally {
            dao.close();
        }
        log.info("session retailCusId : " + retailCusId);

        for (VSession vs : listVS) {
            switch (vs.getKey().getSource()) {
                case "Sale":
                    if (vs.getTraderId().equals(retailCusId)) {
                        ttlSRVou += NumberUtil.NZero(vs.getVouTotal());
                        ttlSRCash += NumberUtil.NZero(vs.getPaid());
                        ttlSRDisc += NumberUtil.NZero(vs.getDiscount());
                        ttlSRBal += NumberUtil.NZero(vs.getBalance());
                        ttlSRTax += NumberUtil.NZero(vs.getTaxAmt());
                    } else {
                        ttlSVou += NumberUtil.NZero(vs.getVouTotal());
                        ttlSCash += NumberUtil.NZero(vs.getPaid());
                        ttlSDisc += NumberUtil.NZero(vs.getDiscount());
                        ttlSBal += NumberUtil.NZero(vs.getBalance());
                        ttlSTax += NumberUtil.NZero(vs.getTaxAmt());
                        ttlSEI += NumberUtil.NZero(vs.getExpIn());
                        ttlSEO += NumberUtil.NZero(vs.getExpense());
                    }
                    break;
                case "Purchase":
                    ttlPVou += NumberUtil.NZero(vs.getVouTotal());
                    ttlPCash += NumberUtil.NZero(vs.getPaid());
                    ttlPDisc += NumberUtil.NZero(vs.getDiscount());
                    ttlPBal += NumberUtil.NZero(vs.getBalance());
                    ttlPTax += NumberUtil.NZero(vs.getTaxAmt());
                    ttlPE += NumberUtil.NZero(vs.getExpense());
                    break;
                case "Return In":
                    if (vs.getTraderId().equals(retailCusId)) {
                        ttlRRiVou += NumberUtil.NZero(vs.getVouTotal());
                        ttlRRiCash += NumberUtil.NZero(vs.getPaid());
                        ttlRRiBal += NumberUtil.NZero(vs.getBalance());
                    } else {
                        ttlRiVou += NumberUtil.NZero(vs.getVouTotal());
                        ttlRiCash += NumberUtil.NZero(vs.getPaid());
                        ttlRiBal += NumberUtil.NZero(vs.getBalance());
                    }
                    break;
                case "Return Out":
                    ttlRoVou += NumberUtil.NZero(vs.getVouTotal());
                    ttlRoCash += NumberUtil.NZero(vs.getPaid());
                    ttlRoBal += NumberUtil.NZero(vs.getBalance());
                    break;
                case "Trader Payment":
                    if (vs.getDiscriminator().equals("C")) {
                        ttlCusPaid += NumberUtil.NZero(vs.getPaid());
                    } else if (vs.getDiscriminator().equals("S")) {
                        ttlSupPaid += (NumberUtil.NZero(vs.getPaid()) * -1);
                    }
                    break;
                case "Cash In":
                    ttlExpIn += NumberUtil.NZero(vs.getExpIn());
                    break;
                case "Cash Out":
                    ttlExpOut += NumberUtil.NZero(vs.getExpense());
                    break;
                case "TRAN-Sale":
                    ttlTranfSale += NumberUtil.NZero(vs.getVouTotal());
                    break;
                case "TRAN-Purchase":
                    ttlTranfPurchase += NumberUtil.NZero(vs.getVouTotal());
                    break;
                default:
                    break;
            }
        }

        double ttlCFFees = 0;

        ttlCashIn = ttlSCash + ttlRoCash + ttlSEI + ttlCusPaid + ttlExpIn + ttlSRCash;
        ttlCashOut = ttlPCash + ttlRiCash + ttlSEO + ttlPE + ttlSupPaid + ttlExpOut
                + ttlCFFees + ttlRRiCash;
        netCash = ttlCashIn - ttlCashOut;

        txtWholeSaleTotal.setValue(ttlSVou);
        txtWholeSalePaid.setValue(ttlSCash);
        txtWholeSaleDisc.setValue(ttlSDisc);
        txtWholeSaleBalance.setValue(ttlSBal);

        txtRetailSaleTotal.setValue(ttlSRVou);
        txtRetailPaid.setValue(ttlSRCash);
        txtRetailDisc.setValue(ttlSRDisc);
        txtRetailBalance.setValue(ttlSRBal);

        txtPurchaseTotal.setValue(ttlPVou);
        txtPurPaid.setValue(ttlPCash);
        txtPurDiscount.setValue(ttlPDisc);
        txtPurBalance.setValue(ttlPBal);

        txtReturnInTotal.setValue(ttlRRiVou);
        txtReturnInPaid.setValue(ttlRRiCash);
        txtReturnInBalance.setValue(ttlRRiBal);

        txtReturnInTotalW.setValue(ttlRiVou);
        txtReturnInPaidW.setValue(ttlRiCash);
        txtReturnInBalanceW.setValue(ttlRiBal);

        txtReturnOutTotal.setValue(ttlRoVou);
        txtRetOutPaid.setValue(ttlRoCash);
        txtRetOutBal.setValue(ttlRoBal);

        txtCusPay.setValue(ttlCusPaid);
        txtSupPay.setValue(ttlSupPaid);

        txtTotalCashIn.setValue(netCash);

        txtTranfSale.setValue(ttlTranfSale);
        txtTranfPurchase.setValue(ttlTranfPurchase);
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

        //sTableModel.setListSessionTtl(listTtl);
    }

    private void printSession() {
        //Properties prop = ReportUtil.loadReportPathProperties();
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + Util1.getPropValue("system.pharmacy.session.check.print.report");
        Map<String, Object> params = new HashMap();
        String sessionName;
        String sessionUser;
        String sessionDate;
        String location;
        String sessionId;
        String sessionCurr;
        String userId;
        String machineId;

        if (cboSession.getSelectedItem() instanceof Session) {
            sessionName = ((Session) cboSession.getSelectedItem()).getSessionName();
            sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        } else {
            sessionName = "All";
            sessionId = "All";
        }

        if (cboUser.getSelectedItem() instanceof Appuser) {
            sessionUser = ((Appuser) cboUser.getSelectedItem()).getUserName();
        } else {
            sessionUser = "All";
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            location = ((Location) cboLocation.getSelectedItem()).getLocationId().toString();
        } else {
            location = "All";
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
            userId = "All";
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
        params.put("p_location_id", location);
        params.put("session_fdate", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("session_tdate", DateUtil.toDateStrMYSQL(txtTo.getText()));
        params.put("session_id", sessionId);
        params.put("session_currency", sessionCurr);
        params.put("user_id", userId);
        params.put("tran_user_id", Global.loginUser.getUserId());
        params.put("p_machine_id", machineId);
        params.put("comp_name", Util1.getPropValue("report.company.name"));
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
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + Util1.getPropValue("system.pharmacy.session.check.print.reportd");
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
            log.error("SessionFilter : " + ex.getMessage());
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

        String patientType = cboSaleMan.getSelectedItem().toString();
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
                String strSql;
                if (cboLocation.getSelectedItem() instanceof Location) {
                    int locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId = "
                            + locationId + ") order by o.traderName";
                } else {
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId in ("
                            + "select a.key.locationId from UserLocationMapping a "
                            + "where a.key.userId = '" + Global.loginUser.getUserId()
                            + "' and a.isAllowSessCheck = true)) order by o.traderName";
                }
                List<Trader> listTrader = dao.findAllHSQL(strSql);
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

    private void decimalFormat() {
        txtWholeSaleBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtWholeSaleDisc.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtWholeSalePaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtWholeSaleTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRetailBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRetailDisc.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRetailPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRetailSaleTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtReturnInBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtReturnInPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtReturnInTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPurBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPurDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPurPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPurchaseTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRetOutBal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRetOutDisc.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtRetOutPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtReturnOutTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtCusPay.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTotalCashIn.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTranfSale.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTranfPurchase.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void saveAudit() {

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
        cboSaleMan = new javax.swing.JComboBox();
        butSaveAudit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSession = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtWholeSaleTotal = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtWholeSaleDisc = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txtWholeSalePaid = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtWholeSaleBalance = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRetailSaleTotal = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtRetailDisc = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        txtRetailPaid = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        txtRetailBalance = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        txtReturnInTotal = new javax.swing.JFormattedTextField();
        jLabel26 = new javax.swing.JLabel();
        txtReturnInPaid = new javax.swing.JFormattedTextField();
        jLabel27 = new javax.swing.JLabel();
        txtReturnInBalance = new javax.swing.JFormattedTextField();
        jLabel28 = new javax.swing.JLabel();
        txtPurchaseTotal = new javax.swing.JFormattedTextField();
        jLabel29 = new javax.swing.JLabel();
        txtPurDiscount = new javax.swing.JFormattedTextField();
        jLabel30 = new javax.swing.JLabel();
        txtPurPaid = new javax.swing.JFormattedTextField();
        jLabel31 = new javax.swing.JLabel();
        txtPurBalance = new javax.swing.JFormattedTextField();
        jLabel32 = new javax.swing.JLabel();
        txtReturnOutTotal = new javax.swing.JFormattedTextField();
        jLabel33 = new javax.swing.JLabel();
        txtRetOutDisc = new javax.swing.JFormattedTextField();
        jLabel34 = new javax.swing.JLabel();
        txtRetOutPaid = new javax.swing.JFormattedTextField();
        jLabel35 = new javax.swing.JLabel();
        txtRetOutBal = new javax.swing.JFormattedTextField();
        jLabel37 = new javax.swing.JLabel();
        txtTotalCashIn = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel38 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel39 = new javax.swing.JLabel();
        txtTranfSale = new javax.swing.JFormattedTextField();
        txtTranfPurchase = new javax.swing.JFormattedTextField();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel40 = new javax.swing.JLabel();
        txtReturnInTotalW = new javax.swing.JFormattedTextField();
        jLabel41 = new javax.swing.JLabel();
        txtReturnInPaidW = new javax.swing.JFormattedTextField();
        jLabel42 = new javax.swing.JLabel();
        txtReturnInBalanceW = new javax.swing.JFormattedTextField();
        jLabel43 = new javax.swing.JLabel();
        txtSupPay = new javax.swing.JFormattedTextField();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel36 = new javax.swing.JLabel();
        txtCusPay = new javax.swing.JFormattedTextField();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
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
    jLabel14.setText("Sale Man");

    cboSaleMan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "OPD", "Inpatient" }));

    butSaveAudit.setText("Save Audit");

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
                            .addComponent(cboCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtInvTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(cboDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboTranType, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(butSaveAudit)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboSource, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboSend, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(butToAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboPaidCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(butPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboLGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(butPrintD, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel14)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboSaleMan, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jLabel11)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(txtCusId, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboLocation, cboMachine, cboUser});

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel16, jLabel4, jLabel5, jLabel6});

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
            .addGap(7, 7, 7)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel11)
                .addComponent(txtCusId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel15)
                .addComponent(cboLGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(butPrintD))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(cboSaleMan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(butSaveAudit))
            .addGap(0, 22, Short.MAX_VALUE))
    );

    tblSession.setFont(Global.textFont);
    tblSession.setModel(tableModel);
    tblSession.setRowHeight(23);
    tblSession.setShowVerticalLines(false);
    jScrollPane1.setViewportView(tblSession);

    jScrollPane3.setBorder(null);

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Total"));

    jLabel17.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel17.setText("Whole Sale Total : ");

    txtWholeSaleTotal.setEditable(false);
    txtWholeSaleTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtWholeSaleTotal.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel18.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel18.setText("Discount : ");

    txtWholeSaleDisc.setEditable(false);
    txtWholeSaleDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtWholeSaleDisc.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel19.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel19.setText("Paid : ");

    txtWholeSalePaid.setEditable(false);
    txtWholeSalePaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtWholeSalePaid.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel20.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel20.setText("Balance : ");

    txtWholeSaleBalance.setEditable(false);
    txtWholeSaleBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtWholeSaleBalance.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel21.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel21.setText("Retail Sale Total : ");

    txtRetailSaleTotal.setEditable(false);
    txtRetailSaleTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtRetailSaleTotal.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel22.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel22.setText("Discount : ");

    txtRetailDisc.setEditable(false);
    txtRetailDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtRetailDisc.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel23.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel23.setText("Paid : ");

    txtRetailPaid.setEditable(false);
    txtRetailPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtRetailPaid.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel24.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel24.setText("Balance : ");

    txtRetailBalance.setEditable(false);
    txtRetailBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtRetailBalance.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel25.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel25.setText(" Retail Sale Return In : ");

    txtReturnInTotal.setEditable(false);
    txtReturnInTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtReturnInTotal.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel26.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel26.setText("Paid : ");

    txtReturnInPaid.setEditable(false);
    txtReturnInPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtReturnInPaid.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel27.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel27.setText("Balance : ");

    txtReturnInBalance.setEditable(false);
    txtReturnInBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtReturnInBalance.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel28.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel28.setText("Purchase Total : ");

    txtPurchaseTotal.setEditable(false);
    txtPurchaseTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtPurchaseTotal.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel29.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel29.setText("Discount : ");

    txtPurDiscount.setEditable(false);
    txtPurDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtPurDiscount.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel30.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel30.setText("Paid : ");

    txtPurPaid.setEditable(false);
    txtPurPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtPurPaid.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel31.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel31.setText("Balance : ");

    txtPurBalance.setEditable(false);
    txtPurBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtPurBalance.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel32.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel32.setText("Return Out Total : ");

    txtReturnOutTotal.setEditable(false);
    txtReturnOutTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtReturnOutTotal.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel33.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel33.setText("Discount : ");

    txtRetOutDisc.setEditable(false);
    txtRetOutDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtRetOutDisc.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel34.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel34.setText("Paid : ");

    txtRetOutPaid.setEditable(false);
    txtRetOutPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtRetOutPaid.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel35.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel35.setText("Balance : ");

    txtRetOutBal.setEditable(false);
    txtRetOutBal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtRetOutBal.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel37.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel37.setText("Total Cash In : ");

    txtTotalCashIn.setEditable(false);
    txtTotalCashIn.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtTotalCashIn.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel38.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel38.setText("Tranf-Sale : ");

    jLabel39.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel39.setText("Tranf-Purchase : ");

    txtTranfSale.setEditable(false);
    txtTranfSale.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtTranfSale.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    txtTranfPurchase.setEditable(false);
    txtTranfPurchase.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtTranfPurchase.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel40.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel40.setText("Whole Sale Return In : ");

    txtReturnInTotalW.setEditable(false);
    txtReturnInTotalW.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtReturnInTotalW.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel41.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel41.setText("Paid : ");

    txtReturnInPaidW.setEditable(false);
    txtReturnInPaidW.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtReturnInPaidW.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel42.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel42.setText("Balance : ");

    txtReturnInBalanceW.setEditable(false);
    txtReturnInBalanceW.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtReturnInBalanceW.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel43.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel43.setText("Supplier Pay : ");

    txtSupPay.setEditable(false);
    txtSupPay.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtSupPay.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    jLabel36.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
    jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel36.setText("Customer Pay : ");

    txtCusPay.setEditable(false);
    txtCusPay.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    txtCusPay.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txtWholeSaleTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtWholeSaleDisc, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtWholeSalePaid, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtWholeSaleBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)))
        .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addComponent(jSeparator5, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txtRetailSaleTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtRetailDisc, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtRetailPaid, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtRetailBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtReturnInTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtReturnInPaid, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtReturnInBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtPurchaseTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtPurDiscount, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtPurPaid, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtPurBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtReturnOutTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtRetOutDisc, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtRetOutPaid, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtRetOutBal, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)))
        .addComponent(jSeparator8, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(3, 3, 3)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txtReturnInTotalW, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addComponent(txtReturnInPaidW, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addComponent(txtReturnInBalanceW, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)))
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txtTotalCashIn, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtTranfSale, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addComponent(txtTranfPurchase, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)))
        .addComponent(jSeparator6, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addComponent(jSeparator7, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel43)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtSupPay, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
        .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtCusPay))
    );

    jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel18, jLabel19, jLabel20, jLabel21, jLabel22, jLabel23, jLabel24, jLabel25, jLabel26, jLabel27, jLabel28, jLabel29, jLabel30, jLabel31, jLabel32, jLabel33, jLabel34, jLabel35, jLabel36, jLabel37, jLabel38, jLabel39, jLabel40, jLabel41, jLabel42, jLabel43});

    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel17)
                .addComponent(txtWholeSaleTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel18)
                .addComponent(txtWholeSaleDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel19)
                .addComponent(txtWholeSalePaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel20)
                .addComponent(txtWholeSaleBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel40)
                .addComponent(txtReturnInTotalW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel41)
                .addComponent(txtReturnInPaidW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel42)
                .addComponent(txtReturnInBalanceW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel21)
                .addComponent(txtRetailSaleTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel22)
                .addComponent(txtRetailDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel23)
                .addComponent(txtRetailPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel24)
                .addComponent(txtRetailBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(3, 3, 3)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel25)
                .addComponent(txtReturnInTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel26)
                .addComponent(txtReturnInPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel27)
                .addComponent(txtReturnInBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(1, 1, 1)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel28)
                .addComponent(txtPurchaseTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel29)
                .addComponent(txtPurDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel30)
                .addComponent(txtPurPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel31)
                .addComponent(txtPurBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel32)
                .addComponent(txtReturnOutTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel33)
                .addComponent(txtRetOutDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel34)
                .addComponent(txtRetOutPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel35)
                .addComponent(txtRetOutBal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel36)
                .addComponent(txtCusPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel43)
                .addComponent(txtSupPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel37)
                .addComponent(txtTotalCashIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(3, 3, 3)
            .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(5, 5, 5)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel38)
                .addComponent(txtTranfSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel39)
                .addComponent(txtTranfPurchase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jScrollPane3.setViewportView(jPanel3);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 398, Short.MAX_VALUE))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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

    public String getExpStr(String str, int i) {
        if (i == 0) {
            if (!str.isEmpty()) {
                switch (str) {
                    case "Sale":
                        return "SALE";
                    case "TRAN-Sale":
                        return "TRANSALE";
                    case "Purchase":
                        return "PURCHASE";
                    case "TRAN-Purchase":
                        return "TRANPURCHASE";
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
                case "TRAN-Sale":
                    return "saleDate";
                case "Purchase":
                case "TRAN-Purchase":
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
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butPrintD;
    private javax.swing.JButton butSaveAudit;
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
    private javax.swing.JComboBox cboSaleMan;
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
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTable tblSession;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtCusPay;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtInvTotal;
    private javax.swing.JFormattedTextField txtPurBalance;
    private javax.swing.JFormattedTextField txtPurDiscount;
    private javax.swing.JFormattedTextField txtPurPaid;
    private javax.swing.JFormattedTextField txtPurchaseTotal;
    private javax.swing.JFormattedTextField txtRetOutBal;
    private javax.swing.JFormattedTextField txtRetOutDisc;
    private javax.swing.JFormattedTextField txtRetOutPaid;
    private javax.swing.JFormattedTextField txtRetailBalance;
    private javax.swing.JFormattedTextField txtRetailDisc;
    private javax.swing.JFormattedTextField txtRetailPaid;
    private javax.swing.JFormattedTextField txtRetailSaleTotal;
    private javax.swing.JFormattedTextField txtReturnInBalance;
    private javax.swing.JFormattedTextField txtReturnInBalanceW;
    private javax.swing.JFormattedTextField txtReturnInPaid;
    private javax.swing.JFormattedTextField txtReturnInPaidW;
    private javax.swing.JFormattedTextField txtReturnInTotal;
    private javax.swing.JFormattedTextField txtReturnInTotalW;
    private javax.swing.JFormattedTextField txtReturnOutTotal;
    private javax.swing.JFormattedTextField txtSupPay;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotalCashIn;
    private javax.swing.JFormattedTextField txtTranfPurchase;
    private javax.swing.JFormattedTextField txtTranfSale;
    private javax.swing.JFormattedTextField txtWholeSaleBalance;
    private javax.swing.JFormattedTextField txtWholeSaleDisc;
    private javax.swing.JFormattedTextField txtWholeSalePaid;
    private javax.swing.JFormattedTextField txtWholeSaleTotal;
    // End of variables declaration//GEN-END:variables
}
