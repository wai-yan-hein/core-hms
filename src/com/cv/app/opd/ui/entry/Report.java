/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.inpatient.database.entity.InpCategory;
import com.cv.app.inpatient.ui.common.DCTableCellEditor;
import com.cv.app.inpatient.ui.common.RptDCServiceFilterTableModel;
import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.opd.database.entity.OPDCusLabGroup;
import com.cv.app.opd.database.entity.OPDGroup;
import com.cv.app.opd.database.entity.OPDLabGroup;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.excel.AdmissionByDoctorExcel;
import com.cv.app.opd.excel.CraftGroupRptExcel;
import com.cv.app.opd.excel.DCIncomeByDoctorExcel;
import com.cv.app.opd.excel.DCIncomeByServiceDetailExcel;
import com.cv.app.opd.excel.DcPatientByDoctorExcel;
import com.cv.app.opd.excel.LabReaderWithDoctorExcel;
import com.cv.app.opd.excel.LabReaderWithDoctorSeinExcel;
import com.cv.app.opd.excel.LabReferDoctorSummarySeinExcel;
import com.cv.app.opd.excel.LabReferExcel;
import com.cv.app.opd.excel.LabReferWithDoctorExcel;
import com.cv.app.opd.excel.LabReferWithDoctorSeinExcel;
import com.cv.app.opd.excel.LabTeachWithTechExcel;
import com.cv.app.opd.excel.OPDIncomeByDoctorExcel;
import com.cv.app.opd.excel.OPDIncomeByServiceWithPatientExcel;
import com.cv.app.opd.excel.OPDServiceFeeListExcel;
import com.cv.app.opd.excel.OtIncomeByDoctorExcel;
import com.cv.app.opd.excel.OtIncomeByServiceDetailExcel;
import com.cv.app.opd.excel.PatientBalanceExcel;
import com.cv.app.opd.excel.PatientSummaryWHOlExcel;
import com.cv.app.opd.ui.common.OPDTableCellEditor;
import com.cv.app.opd.ui.common.RptOPDServiceFilterTableModel;
import com.cv.app.ot.database.entity.OTProcedureGroup;
import com.cv.app.ot.ui.common.OTTableCellEditor;
import com.cv.app.ot.ui.common.RptOTServiceFilterTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.Menu;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.tempentity.TmpMonthFilter;
import com.cv.app.pharmacy.excel.GenExcel;
import com.cv.app.pharmacy.ui.common.ReportListTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author WSwe
 */
public class Report extends javax.swing.JPanel implements SelectionObserver, KeyPropagate {

    private final ReportListTableModel tableModel = new ReportListTableModel();
    private final AbstractDataAccess dao = Global.dao;
    static Logger log = Logger.getLogger(Report.class.getName());
    private final RptOPDServiceFilterTableModel tblServiceTableModel = new RptOPDServiceFilterTableModel();
    private final RptDCServiceFilterTableModel tblDCServiceTableModel = new RptDCServiceFilterTableModel();
    private final RptOTServiceFilterTableModel tblOTServiceTableModel = new RptOTServiceFilterTableModel();

    /**
     * Creates new form Report
     */
    public Report() {
        initComponents();
        initTextBox();
        initCombo();
        initTable();
        actionMapping();
        getReportList();
    }

    private void initTextBox() {
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
            BindingUtil.BindCombo(cboReportType, dao.findAll("Menu",
                    "parent = 76 and menuType.typeId = 5"));
            BindingUtil.BindComboFilter(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindComboFilter(cboCurrency, dao.findAll("Currency"));
            BindingUtil.BindComboFilter(cboDoctor,
                    dao.findAllHSQL("select o from Doctor o where o.active = true order by o.doctorName"));
            BindingUtil.BindComboFilter(cboOPDGroup,
                    dao.findAllHSQL("select o from OPDCategory o order by o.catName"));
            BindingUtil.BindComboFilter(cboDCGroup,
                    dao.findAllHSQL("select o from InpCategory o order by o.catName"));
            BindingUtil.BindComboFilter(cboOTGroup,
                    dao.findAllHSQL("select o from OTProcedureGroup o order by o.groupName"));
            BindingUtil.BindComboFilter(cboCity,
                    dao.findAllHSQL("select o from City o order by o.cityName"));
            BindingUtil.BindComboFilter(cboGender, dao.findAll("Gender"));
            BindingUtil.BindComboFilter(cboPtType,
                    dao.findAllHSQL("select o from CustomerGroup o order by o.groupName"));
            BindingUtil.BindComboFilter(cboOPDCG,
                    dao.findAllHSQL("select o from OPDCusLabGroup o order by o.groupName"));
            BindingUtil.BindComboFilter(cboOPDLG,
                    dao.findAllHSQL("select o from OPDLabGroup o order by o.description"));
            BindingUtil.BindComboFilter(cboChargeType, dao.findAll("ChargeType"));
            BindingUtil.BindComboFilter(cboPayable,
                    dao.findAllHSQL("select o from ExpenseType o order by o.expenseName"));
            BindingUtil.BindComboFilter(cboOPDHead, dao.findAll("OPDGroup"));

            AutoCompleteDecorator.decorate(cboPtType);
            AutoCompleteDecorator.decorate(cboSession);
            AutoCompleteDecorator.decorate(cboPayment);
            AutoCompleteDecorator.decorate(cboCurrency);
            AutoCompleteDecorator.decorate(cboReportType);
            AutoCompleteDecorator.decorate(cboDoctor);
            AutoCompleteDecorator.decorate(cboOPDGroup);
            AutoCompleteDecorator.decorate(cboDCGroup);
            AutoCompleteDecorator.decorate(cboOTGroup);
            AutoCompleteDecorator.decorate(cboCity);
            AutoCompleteDecorator.decorate(cboGender);
            AutoCompleteDecorator.decorate(cboOPDCG);
            AutoCompleteDecorator.decorate(cboOPDLG);
            AutoCompleteDecorator.decorate(cboChargeType);
            AutoCompleteDecorator.decorate(cboPayable);
            AutoCompleteDecorator.decorate(cboOPDHead);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
        }
    }

    private void initTable() {
        tblService.getColumnModel().getColumn(0).setCellEditor(
                new OPDTableCellEditor(dao));
        tblService.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblService.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblServiceTableModel.setParent(tblService);
        tblServiceTableModel.addEmptyRow();

        tblDCService.getColumnModel().getColumn(0).setCellEditor(
                new DCTableCellEditor(dao));
        tblDCService.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblDCService.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblDCServiceTableModel.setParent(tblDCService);
        tblDCServiceTableModel.addEmptyRow();

        tblOTService.getColumnModel().getColumn(0).setCellEditor(
                new OTTableCellEditor(dao));
        tblOTService.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblOTService.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblOTServiceTableModel.setParent(tblOTService);
        tblOTServiceTableModel.addEmptyRow();
    }

    private void actionMapping() {
        tblService.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblService.getActionMap().put("F8-Action", actionOPDServiceFilterDelete);
        tblDCService.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblDCService.getActionMap().put("F8-Action", actionDCServiceFilterDelete);
        tblOTService.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblOTService.getActionMap().put("F8-Action", actionOTServiceFilterDelete);
    }

    private final Action actionOPDServiceFilterDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblService.getSelectedRow() >= 0) {
                try {
                    if (tblService.getCellEditor() != null) {
                        tblService.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }
                tblServiceTableModel.delete(tblService.getSelectedRow());
            }
        }
    };

    private final Action actionDCServiceFilterDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblDCService.getSelectedRow() >= 0) {
                try {
                    if (tblDCService.getCellEditor() != null) {
                        tblDCService.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }
                tblDCServiceTableModel.delete(tblDCService.getSelectedRow());
            }
        }
    };

    private final Action actionOTServiceFilterDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblOTService.getSelectedRow() >= 0) {
                try {
                    if (tblOTService.getCellEditor() != null) {
                        tblOTService.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }
                tblOTServiceTableModel.delete(tblOTService.getSelectedRow());
            }
        }
    };

    private String getHSQL() {
        int parentMenuId = ((Menu) cboReportType.getSelectedItem()).getMenuId();
        String strSql = "select distinct m from Menu m where m.menuId in (";
        String subQuery = "select p.menuId from UserRole ur join ur.privilege p where ur.roleId ="
                + Global.loginUser.getUserRole().getRoleId() + " and p.allow = true";

        strSql = strSql + subQuery + ") and m.parent = " + parentMenuId + " order by m.menuName";

        return strSql;
    }

    private void getReportList() {
        try {
            List<Menu> listReport = dao.findAllHSQL(getHSQL());
            tableModel.setListReport(listReport);
        } catch (Exception ex) {
            log.error("getReportList : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private String getCurrencyId() {
        String currencyId;

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            currencyId = curr.getCurrencyCode();
        } else {
            currencyId = "-";
        }

        return currencyId;
    }

    private Integer getPaymentType() {
        int typeId;

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();
            typeId = pt.getPaymentTypeId();
        } else {
            typeId = -1;
        }

        return typeId;
    }

    private void getPatient() {
        if (txtRegNo.getText() != null && !txtRegNo.getText().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class, txtRegNo.getText());
                dao.close();

                if (pt == null) {
                    txtRegNo.setText(null);
                    txtPtName.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    txtRegNo.setText(pt.getRegNo());
                    txtPtName.setText(pt.getPatientName());
                    txtAdmNo.setText(pt.getAdmissionNo());
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtRegNo.setText(null);
            txtPtName.setText(null);
        }
    }

    private void insertOPDFilter() {
        String strSql = "insert into tmp_opd_service_filter(service_id, user_id) "
                + "select service_id, '" + Global.machineId + "' "
                + "from opd_service \n";
        String strFilter = "";
        String delSql = "delete from tmp_opd_service_filter where user_id = '" + Global.machineId + "'";

        if (cboOPDGroup.getSelectedItem() instanceof OPDCategory) {
            OPDCategory grp = (OPDCategory) cboOPDGroup.getSelectedItem();
            if (strFilter.isEmpty()) {
                strFilter = "cat_id = " + grp.getCatId().toString();
            } else {
                strFilter = strFilter + " and cat_id = " + grp.getCatId().toString();
            }
        }

        if (cboOPDCG.getSelectedItem() instanceof OPDCusLabGroup) {
            OPDCusLabGroup grp = (OPDCusLabGroup) cboOPDCG.getSelectedItem();
            if (strFilter.isEmpty()) {
                strFilter = "cat_id in (select opd_cat_id "
                        + "from opd_cus_lab_group_detail where cus_grp_id = " + grp.getId() + ")";
            } else {
                strFilter = strFilter + " and cat_id in (select opd_cat_id "
                        + "from opd_cus_lab_group_detail where cus_grp_id = " + grp.getId() + ")";
            }
        }

        String filterService = tblServiceTableModel.getFilterCodeStr();
        if (filterService != null) {
            if (strFilter.isEmpty()) {
                strFilter = "service_id in (" + filterService + ")";
            } else {
                strFilter = strFilter + " and service_id in (" + filterService + ")";
            }
        }

        if (cboOPDLG.getSelectedItem() instanceof OPDLabGroup) {
            OPDLabGroup grp = (OPDLabGroup) cboOPDLG.getSelectedItem();
            if (strFilter.isEmpty()) {
                strFilter = "cus_group_id = " + grp.getId().toString();
            } else {
                strFilter = strFilter + " and cus_group_id = " + grp.getId().toString();
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        dao.execSql(delSql, strSql);
    }

    private void insertDCFilter() {
        String strSql = "insert into tmp_dc_service_filter(service_id, user_id) "
                + "select service_id, '" + Global.machineId + "' "
                + "from inp_service \n";
        String strFilter = "";
        String delSql = "delete from tmp_dc_service_filter where user_id = '" + Global.machineId + "'";

        if (cboDCGroup.getSelectedItem() instanceof InpCategory) {
            InpCategory grp = (InpCategory) cboDCGroup.getSelectedItem();
            if (strFilter.isEmpty()) {
                strFilter = "cat_id = " + grp.getCatId().toString();
            } else {
                strFilter = strFilter + " and cat_id = " + grp.getCatId().toString();
            }
        }

        String filterService = tblDCServiceTableModel.getFilterCodeStr();
        if (filterService != null) {
            if (strFilter.isEmpty()) {
                strFilter = "service_id in (" + filterService + ")";
            } else {
                strFilter = strFilter + " and service_id in (" + filterService + ")";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        dao.execSql(delSql, strSql);
    }

    private void insertOTFilter() {
        String strSql = "insert into tmp_ot_service_filter(service_id, user_id) "
                + "select service_id, '" + Global.machineId + "' "
                + "from ot_service \n";
        String strFilter = "";
        String delSql = "delete from tmp_ot_service_filter where user_id = '" + Global.machineId + "'";

        if (cboOTGroup.getSelectedItem() instanceof OTProcedureGroup) {
            OTProcedureGroup grp = (OTProcedureGroup) cboOTGroup.getSelectedItem();
            if (strFilter.isEmpty()) {
                strFilter = "group_id = " + grp.getGroupId().toString();
            } else {
                strFilter = strFilter + " and group_id = " + grp.getGroupId().toString();
            }
        }

        String filterService = tblOTServiceTableModel.getFilterCodeStr();
        if (filterService != null) {
            if (strFilter.isEmpty()) {
                strFilter = "service_id in (" + filterService + ")";
            } else {
                strFilter = strFilter + " and service_id in (" + filterService + ")";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        dao.execSql(delSql, strSql);
    }

    private void removeOtPaymentService() {
        String strIds = "";
        String depositId = Util1.getPropValue("system.ot.deposite.id");
        if (!depositId.isEmpty() && !depositId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = depositId;
            } else {
                strIds = strIds + "," + depositId;
            }
        }

        String discId = Util1.getPropValue("system.ot.disc.id");
        if (!discId.isEmpty() && !discId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = discId;
            } else {
                strIds = strIds + "," + discId;
            }
        }

        String paidId = Util1.getPropValue("system.ot.paid.id");
        if (!paidId.isEmpty() && !paidId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = paidId;
            } else {
                strIds = strIds + "," + paidId;
            }
        }

        String refundId = Util1.getPropValue("system.ot.refund.id");
        if (!refundId.isEmpty() && !refundId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = refundId;
            } else {
                strIds = strIds + "," + refundId;
            }
        }

        if (!strIds.isEmpty()) {
            String strSql = "delete from tmp_ot_service_filter where user_id = '"
                    + Global.machineId + "' and service_id in (" + strIds + ")";
            try {
                dao.execSql(strSql);
            } catch (Exception ex) {
                log.error("removeOtPaymentService : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void removeDCPaymentService() {
        String strIds = "";
        String depositId = Util1.getPropValue("system.dc.deposite.id");
        if (!depositId.isEmpty() && !depositId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = depositId;
            } else {
                strIds = strIds + "," + depositId;
            }
        }

        String discId = Util1.getPropValue("system.dc.disc.id");
        if (!discId.isEmpty() && !discId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = discId;
            } else {
                strIds = strIds + "," + discId;
            }
        }

        String paidId = Util1.getPropValue("system.dc.paid.id");
        if (!paidId.isEmpty() && !paidId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = paidId;
            } else {
                strIds = strIds + "," + paidId;
            }
        }

        String refundId = Util1.getPropValue("system.dc.refund.id");
        if (!refundId.isEmpty() && !refundId.equals("-")) {
            if (strIds.isEmpty()) {
                strIds = refundId;
            } else {
                strIds = strIds + "," + refundId;
            }
        }

        if (!strIds.isEmpty()) {
            String strSql = "delete from tmp_dc_service_filter where user_id = '"
                    + Global.machineId + "' and service_id in (" + strIds + ")";
            try {
                dao.execSql(strSql);
            } catch (Exception ex) {
                log.error("removeDCPaymentService : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private Map getParameters() {
        Map<String, Object> params = new HashMap();

        params.put("user_id", Global.machineId);
        params.put("compName", Util1.getPropValue("report.company.name"));
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("from_date", DateUtil.toDateStrMYSQL(txtFrom.getText().trim()));
        params.put("to_date", DateUtil.toDateStrMYSQL(txtTo.getText().trim()));
        params.put("currency", getCurrencyId());
        params.put("payment", getPaymentType());
        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();
            params.put("payment_name", pt.getPaymentTypeName());
        } else {
            params.put("payment_name", "All");
        }
        if (cboDoctor.getSelectedItem() instanceof Doctor) {
            String drId = ((Doctor) cboDoctor.getSelectedItem()).getDoctorId();
            params.put("doctor", drId);
            params.put("tech_id", drId);
        } else {
            params.put("doctor", "-");
            params.put("tech_id", "-");
        }
        if (!txtRegNo.getText().trim().isEmpty()) {
            params.put("reg_no", txtRegNo.getText().trim());
            params.put("pt_name", "-");
        } else if (!txtPtName.getText().isEmpty()) {
            params.put("reg_no", "-");
            params.put("pt_name", txtPtName.getText().trim());
        } else {
            params.put("reg_no", "-");
            params.put("pt_name", "-");
        }
        if (cboCity.getSelectedItem() instanceof City) {
            params.put("city", ((City) cboCity.getSelectedItem()).getCityId());
        } else {
            params.put("city", -1);
        }
        if (cboOPDGroup.getSelectedItem() instanceof OPDCategory) {
            params.put("opd_category", ((OPDCategory) cboOPDGroup.getSelectedItem()).getCatId());
        } else {
            params.put("opd_category", -1);
        }
        if (cboDCGroup.getSelectedItem() instanceof InpCategory) {
            params.put("dc_category", ((InpCategory) cboDCGroup.getSelectedItem()).getCatId());
        } else {
            params.put("dc_category", -1);
        }
        if (cboOTGroup.getSelectedItem() instanceof OTProcedureGroup) {
            params.put("ot_category", ((OTProcedureGroup) cboOTGroup.getSelectedItem()).getGroupId());
        } else {
            params.put("ot_category", -1);
        }
        if (txtFrom.getText().equals(txtTo.getText())) {
            params.put("data_date", txtFrom.getText());
        } else {
            params.put("data_date", "Between " + txtFrom.getText()
                    + " and " + txtTo.getText());
        }
        if (cboGender.getSelectedItem() instanceof Gender) {
            params.put("gender", ((Gender) cboGender.getSelectedItem()).getGenderId());
        } else {
            params.put("gender", "-");
        }
        if (cboSession.getSelectedItem() instanceof Session) {
            Session sess = (Session) cboSession.getSelectedItem();
            params.put("session", sess.getSessionId());
            params.put("sess_name", sess.getSessionName());
        } else {
            params.put("session", "-");
            params.put("sess_name", "All");
        }
        if (cboPtType.getSelectedItem() instanceof CustomerGroup) {
            CustomerGroup cg = (CustomerGroup) cboPtType.getSelectedItem();
            String tmp = cg.getGroupId();
            System.out.println("Patient Type : " + tmp);
            //params.put("pt_type", ((CustomerGroup)cboPtType.getSelectedItem()).getGroupId());
            params.put("pt_type", tmp);
        } else {
            params.put("pt_type", "-");
        }
        if (cboChargeType.getSelectedItem() instanceof ChargeType) {
            ChargeType ct = (ChargeType) cboChargeType.getSelectedItem();
            params.put("charge_type", ct.getChargeTypeId());
        } else {
            params.put("charge_type", -1);
        }

        if (cboPayable.getSelectedItem() instanceof ExpenseType) {
            ExpenseType et = (ExpenseType) cboPayable.getSelectedItem();
            params.put("payable_type", et.getExpenseId());
        } else {
            params.put("payable_type", -1);
        }

        if (cboOPDHead.getSelectedItem() instanceof OPDGroup) {
            OPDGroup et = (OPDGroup) cboOPDHead.getSelectedItem();
            params.put("opd_head", et.getGroupId());
        } else {
            params.put("opd_head", -1);
        }

        params.put("clinic_type", cboType.getSelectedItem().toString());

        return params;
    }

    private boolean executeProcedure(String report) {
        boolean status = true;

        try {
            switch (report) {
                case "OPDServiceFees":
                case "OPDIncomeByService":
                case "OPDIncomeByServiceWithDoctor":
                case "OPDIncomeByServiceWithPatient":
                case "OPDIncomeByServiceDetail":
                case "OPDIncomeByServiceWithDoctorDetail":
                case "LabRefer":
                case "LabReferWithDoctor":
                case "LabReferAmt":
                case "TechAmt":
                case "OPDIncomeByServiceSummary":
                case "LabTechAmt":
                case "LabReferWithDoctorSein":
                case "LabReferSummarySein":
                case "LabReaderWithDoctorSein":
                case "OPDIncomeByServiceSummarySein":
                case "OPDIncomeByGroupSummary":
                case "OPDIncomeByServiceSubSummary":
                case "OPDIncomeByServiceSummaryByDate":
                    insertOPDFilter();
                    break;
                case "OTServiceFees":
                case "OTIncomeByService":
                case "OTDoctorFees":
                case "OTIncomeByServiceWithDoctor":
                case "OTIncomeByServiceWithPatient":
                case "OTIncomeByDoctor":
                case "OTIncomeByServiceWithDoctorDetail":
                case "OTIncomeByServiceSummary":
                case "OTDailyByPatient":
                case "OTIncomeByServiceSummaryByDate":
                case "OTIncomeByGroupSummary":
                case "OTIncomeByServiceDetail":
                    insertOTFilter();
                    break;
                case "DCServiceFees":
                case "DCIncomeByService":
                case "DCIncomeByServiceWithDoctor":
                case "DCIncomeByServiceWithPatient":
                case "DCIncomeByDoctor":
                case "DCIncomeByServiceWithDoctorDetail":
                case "DCIncomeByServiceWithDoctorDetailSein":
                case "CurrentAdmissionByDoctor":
                case "InpatientSummary":
                case "DCIncomeByServiceSummaryByDate":
                case "DCIncomeByGroupSummary":
                case "DCIncomeByServiceDetail":
                    insertDCFilter();
                    break;
                case "DCIncomeByServiceSummary":
                    insertDCFilter();
                    //insertOPDFilter();
                    break;
            }
        } catch (Exception ex) {
            log.error("executeProcedure : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Error in store procedure.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return status;
    }

    public void insertMonthFilterDiag(String from, String to, String userId,
            Map<String, Object> params) {
        Date dFrom = DateUtil.toDate(from);
        Date dTo = DateUtil.toDate(to);
        int fromYear = DateUtil.getDatePart(dFrom, "yyyy");
        int fromMonth = DateUtil.getDatePart(dFrom, "MM");
        int toYear = DateUtil.getDatePart(dTo, "yyyy");
        int toMonth = DateUtil.getDatePart(dTo, "MM");
        String strField = "";
        String strSql = "";

        dao.execSql("delete from tmp_month_filter where user_id = '" + userId + "'");
        dao.execSql("delete from tmp_diagnosis_yearly_summary where user_id = '" + userId + "'");

        for (int i = 1; i <= 12; i++) {
            if (fromMonth > 12 && fromYear < toYear) {
                fromMonth = 1;
                fromYear++;
            } else if (fromMonth > toMonth && fromYear == toYear) {
                fromYear = toYear + 1;
            }

            String ym = fromMonth + "-" + fromYear;
            if (fromYear <= toYear) {
                params.put("m" + i, ym);
                if (strSql.isEmpty()) {
                    strSql = "sum(case tmf.y_m when '" + ym + "' then ifnull(pt_cnt,0) else 0 end) as " + "m" + i;
                } else {
                    strSql = strSql + ", sum(case tmf.y_m when '" + ym + "' then ifnull(pt_cnt,0) else 0 end) as " + "m" + i;
                }
            } else {
                params.put("m" + i, " ");
                if (strSql.isEmpty()) {
                    strSql = "0 as " + "m" + i;
                } else {
                    strSql = strSql + ", 0 as " + "m" + i;
                }
            }

            if (strField.isEmpty()) {
                strField = "m" + i;
            } else {
                strField = strField + ",m" + i;
            }

            if (fromMonth <= 12 && fromYear <= toYear) {
                TmpMonthFilter tmf = new TmpMonthFilter(userId,
                        ym, fromMonth, fromYear);
                try {
                    dao.save(tmf);
                } catch (Exception ex) {
                    log.error("insertMonthFilterDiag : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                }
            }

            fromMonth++;
        }

        strSql = "insert into tmp_diagnosis_yearly_summary(user_id,diagnosis_id,age_range_id," + strField + ") "
                + "select '" + userId + "',d.diagnosis_id, d.age_range_id," + strSql + " "
                + "from tmp_month_filter tmf left join (select a.diagnosis_id, a.age_range_id, "
                + "a.y_m, count(*) pt_cnt\n"
                + "from (select adm.ams_no, dg.local_name, ear.ager_desp, concat(month(adm.dc_datetime),'-',year(adm.dc_datetime)) y_m,\n"
                + "			 adm.diagnosis_id, adm.age_range_id\n"
                + "	    from admission adm, diagnosis dg, emr_age_range ear\n"
                + "	   where adm.diagnosis_id = dg.id and adm.age_range_id = ear.id"
                + " and date(adm.dc_datetime) between '" + DateUtil.toDateStrMYSQL(from) + "' and '"
                + DateUtil.toDateStrMYSQL(from) + "') a\n"
                + "       group by a.local_name, a.ager_desp) d\n"
                + "on tmf.y_m = d.y_m\n"
                + "where tmf.user_id = '" + userId + "' "
                + "group by d.diagnosis_id, d.age_range_id";

        dao.execSql(strSql);

        dao.execSql("update tmp_diagnosis_yearly_summary set total = ifnull(m1,0)+ifnull(m2,0)+ifnull(m3,0)"
                + "+ifnull(m4,0)+ifnull(m5,0)+ifnull(m6,0)+ifnull(m7,0)+ifnull(m8,0)+ifnull(m9,0)+"
                + "ifnull(m10,0)+ifnull(m11,0)+ifnull(m12,0)\n"
                + " where user_id = '" + userId + "'");
    }

    private boolean insertPatientFilter() {
        boolean status = true;
        String strFilter = "";

        try {
            if (cboPtType.getSelectedItem() instanceof CustomerGroup) {
                CustomerGroup cg = (CustomerGroup) cboPtType.getSelectedItem();
                if (strFilter.isEmpty()) {
                    strFilter = "pt_type = '" + cg.getGroupId() + "'";
                } else {
                    strFilter = strFilter + " and pt_type = '" + cg.getGroupId() + "'";
                }
            }

            if (!txtRegNo.getText().trim().isEmpty()) {
                if (strFilter.isEmpty()) {
                    strFilter = "reg_no = '" + txtRegNo.getText().trim() + "'";
                } else {
                    strFilter = strFilter + " and reg_no = '" + txtRegNo.getText().trim() + "'";
                }
            } else if (!txtPtName.getText().trim().isEmpty()) {
                if (strFilter.isEmpty()) {
                    strFilter = "patient_name like '%" + txtPtName.getText().trim() + "%'";
                } else {
                    strFilter = strFilter + " and patient_name like '%" + txtPtName.getText().trim() + "%'";
                }
            }

            if (!strFilter.isEmpty()) {
                dao.execSql("delete from tmp_patient_filter where user_id = '"
                        + Global.machineId + "'");
                strFilter = "insert into tmp_patient_filter(reg_no, adm_no, user_id) "
                        + "select reg_no, admission_no,'" + Global.machineId + "' "
                        + "from patient_detail "
                        + " where " + strFilter;
                dao.execSql(strFilter);
            } else {
                status = false;
            }
        } catch (Exception ex) {
            log.error("insertPatientFilter : " + ex.getStackTrace()[0].getLineNumber()
                    + " - " + ex.getMessage());
            status = false;
        } finally {
            dao.close();
        }

        return status;
    }

    private void generateExcel() {
        int index = tblReport.convertRowIndexToModel(tblReport.getSelectedRow());
        if (index >= 0) {
            try {
                Menu report = tableModel.getSelectedReport(index);
                GenExcel genExcel = null;
                insertOPDFilter();
                String rptName = report.getMenuClass();
                switch (rptName) {
                    case "PatientBalance":
                        String regNo = "-";
                        if (!txtRegNo.getText().trim().isEmpty()) {
                            regNo = txtRegNo.getText().trim();
                        }
                        dao.execProc("patient_balance", DateUtil.toDateStrMYSQL(txtTo.getText()),
                                getCurrencyId(), Global.machineId, regNo);
                        genExcel = new PatientBalanceExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        break;
                    case "DCPatientByDoctor":
                        genExcel = new DcPatientByDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        break;
                    case "OtIncomeByDoctor":
                        genExcel = new OtIncomeByDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        break;
                    case "DCIncomeByDoctor":
                        genExcel = new DCIncomeByDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        break;
                    case "DCIncomeByServiceWithDoctorDetail":
                        genExcel = new DCIncomeByServiceDetailExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setCurrencyId(getCurrencyId());
                        //genExcel.setDoctorId(txtTo.getText());
                        //genExcel.setRegNo(txtRegNo.getText());
                        //genExcel.setRegNo(txtRegNo.getText().trim());
                        genExcel.setPaymentType(String.valueOf(getPaymentType()));
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        // genExcel.insertStockFilterCode();
                        break;

                    case "OTIncomeByServiceWithDoctorDetail":
                        genExcel = new OtIncomeByServiceDetailExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setPaymentType(String.valueOf(getPaymentType()));
                        genExcel.setCurrencyId(getCurrencyId());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        break;
                    case "OPDServiceFees":
                        insertOPDFilter();
                        genExcel = new OPDServiceFeeListExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        break;
                    case "OPDIncomeByDoctor":
                        genExcel = new OPDIncomeByDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        break;
                    case "LabRefer":
                        genExcel = new LabReferExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "LabReferWithDoctor":
                        genExcel = new LabReferWithDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "LabReaderWithDoctor":
                        genExcel = new LabReaderWithDoctorExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "LabTechWithDoctor":
                        genExcel = new LabTeachWithTechExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "LabReaderWithDoctorSein":
                        genExcel = new LabReaderWithDoctorSeinExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "LabReferDoctorSummarySein":
                        genExcel = new LabReferDoctorSummarySeinExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "LabReferWithDoctorSein":
                        genExcel = new LabReferWithDoctorSeinExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "OPDIncomeByServiceWithPatient":
                        genExcel = new OPDIncomeByServiceWithPatientExcel(dao, rptName + ".xls");
                        genExcel.setUserId(Global.machineId);
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setRegNo(txtRegNo.getText().trim());
                        if (cboDoctor.getSelectedItem() instanceof Doctor) {
                            genExcel.setDoctorId(((Doctor) cboDoctor.getSelectedItem()).getDoctorId());
                        } else {
                            genExcel.setDoctorId("-");
                        }
                        genExcel.setCurrencyId(getCurrencyId());
                        genExcel.setPaymentType(getPaymentType().toString());
                        if (cboChargeType.getSelectedItem() instanceof ChargeType) {
                            ChargeType ct = (ChargeType) cboChargeType.getSelectedItem();
                            genExcel.setChargeType(ct.getChargeTypeId().toString());
                        } else {
                            genExcel.setChargeType("-1");
                        }
                        //genExcel.setSession();
                        genExcel.setOpdCat(getOpdCat().toString());
                        break;
                    case "AdmissionByDoctor":
                        genExcel = new AdmissionByDoctorExcel(dao, rptName + ".xls");
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setCity(getCity().toString());
                        break;
                    case "PatientSummary":
                        genExcel = new PatientSummaryWHOlExcel(dao, rptName + ".xls");
                        genExcel.setFromDate(DateUtil.toDateStrMYSQL(txtFrom.getText()));
                        genExcel.setToDate(DateUtil.toDateStrMYSQL(txtTo.getText()));
                        genExcel.setPaymentType(String.valueOf(getPaymentType()));
                        break;
                }

                if (genExcel != null) {
                    genExcel.genExcel();
                }
            } catch (Exception ex) {
                log.error("generateExcel : " + ex.getMessage());
            }
        }
    }

    private Integer getOpdCat() {
        int catId = 0;
        if (cboOPDGroup.getSelectedItem() instanceof OPDCategory) {
            catId = ((OPDCategory) cboOPDGroup.getSelectedItem()).getCatId();
        }
        return catId;
    }

    private Integer getCity() {
        int cityId = -1;
        if (cboCity.getSelectedItem() instanceof City) {
            cityId = ((City) cboCity.getSelectedItem()).getCityId();
        }
        return cityId;
    }

    private void getAdmPatient() {
        if (txtAdmNo.getText() != null && !txtAdmNo.getText().isEmpty()) {
            try {
                dao.open();
                List<Ams> listAms = dao.findAllHSQL(
                        "select o from Ams o where o.key.amsNo = '" + txtAdmNo.getText().trim() + "'"
                );
                Ams ams = null;
                if (listAms != null) {
                    if (!listAms.isEmpty()) {
                        ams = listAms.get(0);
                    }
                }
                dao.close();

                if (ams == null) {
                    txtAdmNo.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid admitted patient code.",
                            "Admitted Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    txtAdmNo.setText(ams.getKey().getAmsNo());
                    txtPtName.setText(ams.getPatientName());
                    txtRegNo.setText(ams.getKey().getRegister().getRegNo());
                }
            } catch (Exception ex) {
                log.error("getAdmPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtAdmNo.setText(null);
            txtPtName.setText(null);
            txtRegNo.setText(null);
        }
    }
    
    private boolean isValidAdm(Map<String, Object> params){
        boolean status = true;
        String imagePath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path");
        params.put("IMAGE_PATH", imagePath);
        params.put("bed_no", "-");
        params.put("sex", "-");
        params.put("age", "-");
        params.put("dc_status", "-");
        params.put("address", "-");
        params.put("dr_name", "-");
        params.put("pt_name", txtPtName.getText().trim());
        params.put("adm_date", DateUtil.toDateStrMYSQL(txtFrom.getText().trim()));
        params.put("tran_date", DateUtil.toDateStrMYSQL(txtTo.getText().trim()));
        params.put("adm_no", txtAdmNo.getText().trim());
        String compName = Util1.getPropValue("report.company.name");
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDCService = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cboOPDLG = new javax.swing.JComboBox<>();
        cboChargeType = new javax.swing.JComboBox<>();
        butCraftRpt = new javax.swing.JButton();
        butExcel = new javax.swing.JButton();
        butPrint = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        cboPtType = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblOTService = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox<>();
        cboDoctor = new javax.swing.JComboBox();
        cboOPDGroup = new javax.swing.JComboBox();
        cboDCGroup = new javax.swing.JComboBox();
        cboOTGroup = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cboCity = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cboGender = new javax.swing.JComboBox<>();
        cboSession = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cboOPDCG = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        cboPayable = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        cboOPDHead = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        txtAdmNo = new javax.swing.JTextField();
        cboReportType = new javax.swing.JComboBox();

        tblReport.setFont(Global.textFont);
        tblReport.setModel(tableModel);
        tblReport.setRowHeight(23);
        jScrollPane1.setViewportView(tblReport);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OPD", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblService.setFont(Global.textFont);
        tblService.setModel(tblServiceTableModel);
        tblService.setRowHeight(23);
        jScrollPane2.setViewportView(tblService);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ward Procedure", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblDCService.setFont(Global.textFont);
        tblDCService.setModel(tblDCServiceTableModel);
        tblDCService.setRowHeight(23);
        jScrollPane3.setViewportView(tblDCService);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("OPD LG");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Charge Type");

        cboOPDLG.setFont(Global.textFont);

        cboChargeType.setFont(Global.textFont);

        butCraftRpt.setText("Craft Rpt");
        butCraftRpt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCraftRptActionPerformed(evt);
            }
        });

        butExcel.setText("Excel");
        butExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExcelActionPerformed(evt);
            }
        });

        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Pt-Type");

        cboPtType.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 123, Short.MAX_VALUE)
                        .addComponent(butCraftRpt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butExcel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butPrint))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboOPDLG, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboChargeType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboPtType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel16, jLabel17});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(cboOPDLG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(cboChargeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(cboPtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butPrint)
                    .addComponent(butExcel)
                    .addComponent(butCraftRpt))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OT Procedure", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblOTService.setFont(Global.textFont);
        tblOTService.setModel(tblOTServiceTableModel);
        tblOTService.setRowHeight(23);
        jScrollPane4.setViewportView(tblOTService);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From ");

        txtFrom.setEditable(false);
        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtTo.setEditable(false);
        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        cboCurrency.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Payment");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Doctor ");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("OPD Group");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("DC Group");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("OT Group");

        cboPayment.setFont(Global.textFont);

        cboDoctor.setFont(Global.textFont);

        cboOPDGroup.setFont(Global.textFont);

        cboDCGroup.setFont(Global.textFont);

        cboOTGroup.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Reg No.");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Name");

        txtRegNo.setFont(Global.textFont);
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        txtPtName.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("City");

        cboCity.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Gender");

        cboGender.setFont(Global.textFont);

        cboSession.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Session");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("OPD CG");

        cboOPDCG.setFont(Global.textFont);

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Payable ");

        cboPayable.setFont(Global.textFont);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Type");

        cboType.setFont(Global.textFont);
        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Pharmacy", "OPD", "OT", "DC", "Ward" }));

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("OPD Head");

        cboOPDHead.setFont(Global.textFont);

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Adm No.");

        txtAdmNo.setFont(Global.textFont);
        txtAdmNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAdmNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(10, 10, 10)
                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addGap(18, 18, 18)
                            .addComponent(cboPayable, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel19)
                            .addGap(18, 18, 18)
                            .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addGap(18, 18, 18)
                            .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addGap(18, 18, 18)
                            .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel12)
                                .addComponent(jLabel13))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9)
                                .addComponent(jLabel15))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtRegNo)
                                .addComponent(cboOPDCG, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addGap(18, 18, 18)
                            .addComponent(cboDCGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(18, 18, 18)
                            .addComponent(cboOTGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addComponent(jLabel20))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cboOPDHead, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboOPDGroup, 0, 174, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addComponent(txtAdmNo)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFrom, txtTo});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboCity, cboCurrency, cboDCGroup, cboDoctor, cboGender, cboOPDCG, cboOPDGroup, cboOTGroup, cboPayable, cboPayment, cboSession, cboType, txtPtName, txtRegNo});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel13, jLabel15, jLabel18, jLabel19, jLabel21, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(cboPayable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(cboOPDHead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboOPDGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboDCGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboOTGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cboOPDCG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        cboReportType.setFont(Global.textFont);
        cboReportType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboReportTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cboReportType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(31, 31, 31))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboReportType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

    private void cboReportTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboReportTypeActionPerformed
        if (cboReportType.getSelectedItem() != null) {
            getReportList();
        }
    }//GEN-LAST:event_cboReportTypeActionPerformed

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        getPatient();
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        Menu menu = (Menu) cboReportType.getSelectedItem();
        if (menu != null) {
            int index = tblReport.convertRowIndexToModel(tblReport.getSelectedRow());
            if (index >= 0) {
                switch (menu.getMenuClass()) {
                    case "OPD":
                        insertOPDFilter();
                        break;
                    case "DC":
                        insertDCFilter();
                        break;
                    case "OT":
                        insertOTFilter();
                        break;
                }

                Menu report = tableModel.getSelectedReport(index);
                if (executeProcedure(report.getMenuClass())) {
                    Map<String, Object> params = getParameters();
                    switch (report.getMenuClass()) {
                        case "DiagnosisMonthly":
                            DateUtil.setStartTime();
                            insertMonthFilterDiag(txtFrom.getText(), txtTo.getText(),
                                    Global.machineId, params);
                            log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                            break;
                        case "PatientBalance":
                        case "CurrentPatientBalance":
                            DateUtil.setStartTime();
                            String regNo = "-";
                            if (!txtRegNo.getText().trim().isEmpty()) {
                                regNo = txtRegNo.getText().trim();
                            }
                            dao.execProc("patient_balance", DateUtil.toDateStrMYSQL(txtTo.getText()),
                                    getCurrencyId(), Global.machineId, regNo);
                            String strSQLs = "update tmp_bill_payment tbp, (\n"
                                    + "select dh.patient_id, max(dh.admission_no) admission_no, max(dh.dc_date) dc_date\n"
                                    + "from dc_his dh\n"
                                    + "where dh.dc_status is not null and date(dh.dc_date) between '"
                                    + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                    + "and dh.deleted = false\n"
                                    + "group by dh.patient_id) dc\n"
                                    + "set tbp.dc_date = dc.dc_date\n"
                                    + "where tbp.user_id = '" + Global.machineId
                                    + "' and tbp.reg_no = dc.patient_id and tbp.admission_no is null";
                            dao.execSql(strSQLs);
                            log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                            break;
                        case "InpatientSummary":
                            DateUtil.setStartTime();
                            dao.execProc("rpt_inp", DateUtil.toDateStrMYSQL(txtFrom.getText()),
                                    DateUtil.toDateStrMYSQL(txtTo.getText()), Global.machineId);
                            log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                            break;
                        case "PatientInOutBalance":
                            String appCurr = Util1.getPropValue("system.app.currency");
                            if (txtRegNo.getText() == null) {
                                JOptionPane.showMessageDialog(Util1.getParent(),
                                        "Please select patient.",
                                        "Invalid Patient", JOptionPane.ERROR_MESSAGE);
                                return;
                            } else if (txtRegNo.getText().isEmpty()) {
                                JOptionPane.showMessageDialog(Util1.getParent(),
                                        "Please select patient.",
                                        "Invalid Patient", JOptionPane.ERROR_MESSAGE);
                                return;
                            } else {
                                DateUtil.setStartTime();
                                dao.execProc("patient_balance_detail", txtRegNo.getText().trim(),
                                        DateUtil.toDateStrMYSQL(txtFrom.getText()),
                                        DateUtil.toDateStrMYSQL(txtTo.getText()),
                                        appCurr, Global.machineId);
                                double opBalance = 0;
                                try {
                                    ResultSet rs = dao.execSQL("select tran_date, tran_option, trader_id as reg_no, null as admission_no, curr_id, sum(amount) as ttl_op_amt,\n"
                                            + "			 0 as ttl_amt, 0 as ttl_paid\n"
                                            + "		from tmp_patient_bal_date\n"
                                            + "	   where user_id = '" + Global.machineId + "' \n"
                                            + "	   group by tran_date, tran_option, trader_id, curr_id");
                                    if (rs != null) {
                                        if (rs.next()) {
                                            opBalance = rs.getDouble("ttl_op_amt");
                                        }
                                    }
                                } catch (SQLException ex) {
                                    log.error("PatientInOutBalance : " + ex.getMessage());
                                }
                                log.info(report.getMenuClass() + " time taken : " + DateUtil.getDuration());
                                params.put("p_op_balance", opBalance);
                            }
                            break;
                        case "ClinicPayableBalance":
                        case "ClinicPayableBalanceByDr":
                            String strDate = Util1.getPropValue("system.opd.payablebalance.startdate");
                            params.put("from_date", strDate);
                            break;
                        case "OTIncomeByGroupSummary":
                        case "OTIncomeByService":
                            removeOtPaymentService();
                            break;
                        case "DCIncomeByGroupSummary":
                        case "DCIncomeByService":
                            removeDCPaymentService();
                            break;
                        case "WardPayableDetail":
                            insertDCFilter();
                            break;
                        case "OPDPayableDetail":
                            insertOPDFilter();
                            break;
                        case "OTPayableDetail":
                            insertOTFilter();
                            break;
                        case "DCDailySDM":
                            if(!isValidAdm(params)){
                                return;
                            }
                            break;
                    }

                    String reportPath = Util1.getAppWorkFolder()
                            + Util1.getPropValue("report.folder.path")
                            + "clinic/"
                            + report.getMenuUrl();
                    dao.close();
                    DateUtil.setStartTime();
                    ReportUtil.viewReport(reportPath, params, dao.getConnection());
                    log.info(report.getMenuClass() + " view time taken : " + DateUtil.getDuration());
                    dao.commit();
                }
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "Please select report in the list.",
                        "No Report Selection", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_butPrintActionPerformed

    private void butExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExcelActionPerformed
        generateExcel();
    }//GEN-LAST:event_butExcelActionPerformed

    private void butCraftRptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCraftRptActionPerformed
        try {
            dao.execProc("insert_craft_group_rpt", DateUtil.toDateStrMYSQL(txtFrom.getText()),
                    DateUtil.toDateStrMYSQL(txtTo.getText()), Global.machineId);
            GenExcel genExcel = new CraftGroupRptExcel(dao, "CraftGroupRpt.xls");
            genExcel.setUserId(Global.machineId);
            genExcel.setFromDate(txtFrom.getText());
            genExcel.setToDate(txtTo.getText());
            genExcel.genExcel();
        } catch (Exception ex) {
            log.error("butCraftRptActionPerformed : " + ex.getMessage());
        }
    }//GEN-LAST:event_butCraftRptActionPerformed

    private void txtAdmNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdmNoActionPerformed
        getAdmPatient();
    }//GEN-LAST:event_txtAdmNoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCraftRpt;
    private javax.swing.JButton butExcel;
    private javax.swing.JButton butPrint;
    private javax.swing.JComboBox<String> cboChargeType;
    private javax.swing.JComboBox<String> cboCity;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox<String> cboDCGroup;
    private javax.swing.JComboBox<String> cboDoctor;
    private javax.swing.JComboBox<String> cboGender;
    private javax.swing.JComboBox<String> cboOPDCG;
    private javax.swing.JComboBox<String> cboOPDGroup;
    private javax.swing.JComboBox<String> cboOPDHead;
    private javax.swing.JComboBox<String> cboOPDLG;
    private javax.swing.JComboBox<String> cboOTGroup;
    private javax.swing.JComboBox<String> cboPayable;
    private javax.swing.JComboBox<String> cboPayment;
    private javax.swing.JComboBox<String> cboPtType;
    private javax.swing.JComboBox cboReportType;
    private javax.swing.JComboBox<String> cboSession;
    private javax.swing.JComboBox<String> cboType;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tblDCService;
    private javax.swing.JTable tblOTService;
    private javax.swing.JTable tblReport;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField txtAdmNo;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
