/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.emr.database.entity.AgeRange;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.LabMachine;
import com.cv.app.opd.database.entity.LabResultHis;
import com.cv.app.opd.database.entity.LabResultHisKey;
import com.cv.app.opd.database.entity.MUKey;
import com.cv.app.opd.database.entity.MUsage;
import com.cv.app.opd.database.entity.OPDHis;
import com.cv.app.opd.database.entity.OPDLabResult;
import com.cv.app.opd.database.entity.OPDMedUsage;
import com.cv.app.opd.database.entity.temp.TempLabResultFilter;
import com.cv.app.opd.database.tempentity.LabUsage;
import com.cv.app.opd.database.view.VOpd;
import com.cv.app.opd.ui.common.ButtonColumn;
import com.cv.app.opd.ui.common.LRLabTestResultTableModel;
import com.cv.app.opd.ui.common.LRLabTestTableModel;
import com.cv.app.opd.ui.common.LabResultTableCellEditor;
import com.cv.app.opd.ui.common.PathoCellEditor;
import com.cv.app.opd.ui.util.LabMachineAutoCompleter;
import com.cv.app.opd.ui.util.LabUsageChoiceDialog;
import com.cv.app.opd.ui.util.OPDVouSearchDialog;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.StockCosting;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class LabResult extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(LabResult.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final LRLabTestTableModel tblLabTestTableModel = new LRLabTestTableModel();
    private final LRLabTestResultTableModel tblLabResultTableModel = new LRLabTestResultTableModel();
    private String strDrName, strAge, strSex;
    private Date strDate;
    private HashMap<Integer, List<LabResultHis>> hm = new HashMap();

    /**
     * Creates new form LabResult
     */
    public LabResult() {
        initComponents();
        initCombo();
        initTable();
        assignDefaultValue();
        actionMapping();
    }

    private void initCombo() {
        try {
            List<AgeRange> listAR = dao.findAllHSQL("select o from AgeRange o order by o.sortOrder");
            BindingUtil.BindCombo(cboAgeRange, listAR);
            new ComBoBoxAutoComplete(cboAgeRange, this);
            cboAgeRange.setSelectedItem(null);
            cboAgeRange.setEnabled(false);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    //FormAction
    @Override
    public void save() {
        try {
            OPDHis his = (OPDHis) dao.find(OPDHis.class, txtVouNo.getText());
            if (his != null) {
                AgeRange ar = (AgeRange) cboAgeRange.getSelectedItem();
                his.setAgeRange(ar);
                his.setExamRequired(txtExaminationRequired.getText().trim());
                his.setNos(txtNatureOfSpecimen.getText().trim());
                dao.save(his);
            }
        } catch (Exception ex) {
            log.error("save age range : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            dao.close();
        }

        List<LabResultHis> listLRH = tblLabResultTableModel.getListLRH();

        try {
            dao.saveBatch(listLRH);
            newForm();
        } catch (Exception ex) {
            log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    @Override
    public void newForm() {
        hm = new HashMap();
        txtVouNo.setText(null);
        txtRegNo.setText(null);
        txtPtName.setText(null);
        txtNatureOfSpecimen.setText(null);
        txtExaminationRequired.setText(null);
        txtBedNo.setText(null);
        assignDefaultValue();
        tblLabResultTableModel.setListLRH(new ArrayList());
        tblLabTestTableModel.setListVOPD(new ArrayList());
        cboAgeRange.setEnabled(false);
    }

    @Override
    public void history() {
        OPDVouSearchDialog dialog = new OPDVouSearchDialog(this, "RESULT");
    }

    @Override
    public void delete() {

    }

    @Override
    public void deleteCopy() {

    }

    @Override
    public void print() {
        List<VOpd> listVOpd = tblLabTestTableModel.getListVOPD();
        boolean status = false;
        String delSql = "delete from tmp_lab where user_id = '"
                + Global.machineId + "'";
        int serviceId = -1;//528
        String testRefDrName = "-";

        try {
            dao.execSql(delSql);
            dao.execSql("delete from tmp_lab_result_filter where user_id = '"
                    + Global.machineId + "'");

            for (VOpd tmp : listVOpd) {
                if (tmp.getPrint() != null) {
                    if (tmp.getPrint()) {
                        if (testRefDrName.equals("-")) {
                            if (tmp.getReferDrName() == null) {
                                testRefDrName = "";
                            } else {
                                testRefDrName = tmp.getReferDrName();
                            }
                        }
                        if (tmp.getKey().getServiceId().toString().equals(Util1.getPropValue("result.uanalyzer"))) { //For Urine Analyzer
                            serviceId = tmp.getKey().getServiceId();
                            status = true;
                            break;
                        }

                        List<LabResultHis> listLRH = hm.get(tmp.getKey().getServiceId());
                        for (LabResultHis lrh : listLRH) {
                            if (lrh.getPrint() != null) {
                                if (lrh.getPrint()) {
                                    TempLabResultFilter tlrf = new TempLabResultFilter();
                                    tlrf.setResultId(lrh.getKey().getLabResultId());
                                    tlrf.setUserId(Global.machineId);

                                    dao.save(tlrf);
                                }
                            }
                        }

                        status = true;
                        String strSql = "INSERT INTO tmp_lab(user_id,service_id)"
                                + "  values ('" + Global.machineId + "','" + tmp.getKey().getServiceId() + "') ";
                        dao.execSql(strSql);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Print : " + ex.getMessage());
        } finally {
            dao.close();
        }

        if (status) {
            Map<String, Object> params = new HashMap();
            params.put("user_id", Global.machineId);
            params.put("invoiceNo", txtVouNo.getText());
            if (!txtRemark.getText().trim().isEmpty()) {
                params.put("patientName", txtRemark.getText().trim());
            } else {
                params.put("patientName", txtPtName.getText());
            }
            params.put("regNo", txtRegNo.getText());
            params.put("resultDate", txtResultDate.getText());
            params.put("compName", Util1.getPropValue("report.company.name"));
            params.put("comAddress", Util1.getPropValue("report.address"));
            params.put("phoneNo", Util1.getPropValue("report.phone"));
            log.info("Age : " + strAge);
            params.put("age", strAge);
            params.put("sex", strSex);
            params.put("user", Global.loginUser.getUserShortName());
            params.put("room_no", txtBedNo.getText());

            if (!txtDonor.getText().trim().isEmpty()) {
                params.put("premark", txtDonor.getText().trim());
            } else {
                params.put("premark", "");
            }
            String strTmpDr = strDrName;

            if (testRefDrName != null) {
                if (!testRefDrName.equals("-")) {
                    strTmpDr = testRefDrName;
                }
                if (testRefDrName.equals("-")) {
                    params.put("drName", strDrName);
                } else {
                    params.put("drName", testRefDrName);
                }
            } else {
                params.put("drName", "");
            }
            params.put("drName", strTmpDr);
            params.put("invDate", strDate);
            params.put("IMAGE_PATH", Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path"));
            String compName = Util1.getPropValue("report.company.name");
            String phoneNo = Util1.getPropValue("report.phone");
            String address = Util1.getPropValue("report.address");
            params.put("compName", compName);
            params.put("category", Util1.getPropValue("report.company.cat"));
            params.put("phoneNo", phoneNo);
            params.put("comAddress", address);
            params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path") + "Clinic\\");

            String reportFile = "rptLabResult";
            if (serviceId == -1) {
                if (!Util1.getPropValue("report.file.labresult").isEmpty()) {
                    if (chkHeader.isSelected()) {
                        reportFile = "rptLabResultwithHeaderOnly";
                    } else if (chkEnvelope.isSelected()) {
                        reportFile = "rptLabResultEnvelope";
                    } else if (chkA5.isSelected()) {
                        reportFile = Util1.getPropValue("report.file.labresult.A5");
                    } else {
                        reportFile = Util1.getPropValue("report.file.labresult");
                    }
                }
            } else {
                //reportFile = "rptLabResultSeinUA";
                reportFile = Util1.getPropValue("report.file.ua");
            }
            String reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "clinic/" + reportFile;

            dao.close();
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
            dao.commit();

        }
    }

    //KeyPropagate
    @Override
    public void keyEvent(KeyEvent e) {

    }

    //SelectionObserver
    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "OPDVouList":
                try {
                    if (selectObj != null) {
                        VoucherSearch vs = (VoucherSearch) selectObj;
                        OPDHis opdHis = (OPDHis) dao.find(OPDHis.class, vs.getInvNo());
                        if (opdHis.isDeleted()) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "This voucher is deleted.",
                                    "Deleted", JOptionPane.ERROR_MESSAGE);
                        } else {
                            newForm();
                            if (opdHis.getPatient() != null) {
                                if (opdHis.getDoctor() != null) {
                                    strDrName = opdHis.getDoctor().getDoctorName();
                                } else if (opdHis.getPatient().getDoctor() != null) {
                                    strDrName = opdHis.getPatient().getDoctor().toString();
                                } else {
                                    strDrName = "";
                                }

                                strAge = "";
                                if(NumberUtil.NZeroInt(opdHis.getAge()) != 0 || NumberUtil.NZeroInt(opdHis.getAgeM()) != 0
                                        || NumberUtil.NZeroInt(opdHis.getAgeD()) != 0){
                                    if(NumberUtil.NZeroInt(opdHis.getAge()) != 0){
                                        strAge = opdHis.getAge() + "y";
                                    }
                                    
                                    if(NumberUtil.NZeroInt(opdHis.getAgeM()) != 0){
                                        if(strAge.isEmpty()){
                                            strAge = opdHis.getAge() + "m";
                                        } else {
                                            strAge = strAge + "," + opdHis.getAge() + "m";
                                        }
                                    }
                                    
                                    if(NumberUtil.NZeroInt(opdHis.getAgeD()) != 0){
                                        if(strAge.isEmpty()){
                                            strAge = opdHis.getAge() + "d";
                                        } else {
                                            strAge = strAge + "," + opdHis.getAge() + "d";
                                        }
                                    }
                                    
                                } else {
                                    if(opdHis.getPatient() != null){
                                        if(opdHis.getPatient().getDob() != null){
                                            strAge = DateUtil.getAge(DateUtil.toDateStr(opdHis.getPatient().getDob()));
                                        }
                                    }
                                }
                                
                                if (opdHis.getPatient().getSex() != null) {
                                    strSex = opdHis.getPatient().getSex().toString();
                                } else {
                                    strSex = "";
                                }
                                
                                txtRegNo.setText(opdHis.getPatient().getRegNo());

                                if (opdHis.getAdmissionNo() != null) {
                                    AdmissionKey key = new AdmissionKey();
                                    key.setAmsNo(opdHis.getAdmissionNo());
                                    key.setRegister(opdHis.getPatient());
                                    Ams ams = (Ams) dao.find(Ams.class, key);
                                    if (ams != null) {
                                        if (ams.getBuildingStructure() != null) {
                                            if (ams.getBuildingStructure().getCode() != null) {
                                                if (!ams.getBuildingStructure().getCode().isEmpty()) {
                                                    txtBedNo.setText(ams.getBuildingStructure().getCode());
                                                } else {
                                                    txtBedNo.setText(ams.getBuildingStructure().getDescription());
                                                }
                                            } else {
                                                txtBedNo.setText(ams.getBuildingStructure().getDescription());
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (opdHis.getDoctor() != null) {
                                    strDrName = opdHis.getDoctor().getDoctorName();
                                } else {
                                    strDrName = "";
                                }

                                if (opdHis.getAge() != null) {
                                    strAge = opdHis.getAge().toString();
                                }
                                strSex = "";
                                txtRegNo.setText("");
                            }
                            strDate = opdHis.getInvDate();
                            if (opdHis.getPatient() != null) {
                                txtPtName.setText(opdHis.getPatient().getPatientName());
                            } else {
                                txtPtName.setText(opdHis.getPatientName());
                            }
                            txtVouNo.setText(opdHis.getOpdInvId());
                            txtDonor.setText(opdHis.getDonorName());
                            txtRemark.setText(opdHis.getRemark());
                            txtNatureOfSpecimen.setText(opdHis.getNos());
                            txtExaminationRequired.setText(opdHis.getExamRequired());
                            List<VOpd> listOPD = dao.findAllHSQL("select o from VOpd o where o.key.vouNo = '"
                                    + opdHis.getOpdInvId() + "' and o.groupId = 1");
                            tblLabTestTableModel.setListVOPD(listOPD);
                            cboAgeRange.setEnabled(true);
                            cboAgeRange.setSelectedItem(opdHis.getAgeRange());
                        }
                    }
                } catch (Exception ex) {
                    log.error("selected OPDVouLIst : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                } finally {
                    dao.close();
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        /*if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
         if (!focusCtrlName.equals("-")) {
         if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtPatientNo")) {
         txtDoctorNo.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDoctorNo")) {
         txtDonorName.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDonorName")) {
         tblService.requestFocus();
         }else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusId")) {
         txtDonorName.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDoctorNo")) {
         txtPatientNo.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblService")) {
         int selRow = tblService.getSelectedRow();

         if (selRow == -1 || selRow == 0) {
         txtDonorName.requestFocus();
         }
         }
         }
         }*/
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void initForFocus() {
        /*txtPatientNo.addKeyListener(this);
         txtDoctorNo.addKeyListener(this);
         txtDonorName.addKeyListener(this);
         tblService.addKeyListener(this);*/
    }

    private void assignDefaultValue() {
        txtResultDate.setText(DateUtil.getTodayDateStr());
        cboAgeRange.setSelectedItem(null);
    }

    private void initTable() {
        try {
            tblLabTest.setName("tblLabTest");
            tblLabTest.getTableHeader().setFont(Global.lableFont);
            tblLabTest.getColumnModel().getColumn(0).setPreferredWidth(200);//Lab Test
            tblLabTest.getColumnModel().getColumn(1).setPreferredWidth(20);//Date
            tblLabTest.getColumnModel().getColumn(2).setPreferredWidth(100);//Doctor
            tblLabTest.getColumnModel().getColumn(3).setPreferredWidth(100);//Patho
            tblLabTest.getColumnModel().getColumn(3).setCellEditor(new PathoCellEditor());
            tblLabTest.getColumnModel().getColumn(4).setPreferredWidth(5);
            tblLabTest.getColumnModel().getColumn(5).setPreferredWidth(5);
            tblLabTest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JComboBox cboLabMachine = new JComboBox();
            BindingUtil.BindCombo(cboLabMachine, dao.findAllHSQL("select o from LabMachine o order by o.lMachineName"));
            cboLabMachine.setEditable(true);
            tblLabTest.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cboLabMachine));

            ButtonColumn buttonColumn = new ButtonColumn(tblLabTest, actionComment, 7);

            tblLabResult.setName("tblLabResult");
            tblLabResult.getTableHeader().setFont(Global.lableFont);
            tblLabResult.getColumnModel().getColumn(0).setPreferredWidth(130);//Test
            tblLabResult.getColumnModel().getColumn(1).setPreferredWidth(250);//Result
            tblLabResult.getColumnModel().getColumn(2).setPreferredWidth(150);//Ref. Range
            tblLabResult.getColumnModel().getColumn(3).setPreferredWidth(10);//Unit
            tblLabResult.getColumnModel().getColumn(4).setPreferredWidth(80);//Remark
            tblLabResult.getColumnModel().getColumn(5).setPreferredWidth(30);//Method
            tblLabResult.getColumnModel().getColumn(6).setPreferredWidth(5);//Print
            tblLabResult.getColumnModel().getColumn(7).setPreferredWidth(5);//Comment
            ButtonColumn buttonColumn1 = new ButtonColumn(tblLabResult, actionComment, 7);

            tblLabResult.getColumnModel().getColumn(1).setCellEditor(
                    new LabResultTableCellEditor(dao));//Result
            tblLabResult.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
            tblLabResult.getColumnModel().getColumn(4).setCellEditor(
                    new LabResultTableCellEditor(dao));//Remark

            JComboBox cboLabResultMethod = new JComboBox();
            BindingUtil.BindCombo(cboLabResultMethod, dao.findAll("LabResultMethod"));
            cboLabResultMethod.setEditable(true);
            tblLabResult.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cboLabResultMethod));

            tblLabTest.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int selectRow = -1;
                    if (tblLabTest.getSelectedRow() >= 0) {
                        selectRow = tblLabTest.convertRowIndexToModel(tblLabTest.getSelectedRow());
                    }

                    log.info("Select Status : " + e.getValueIsAdjusting());
                    if (selectRow >= 0) {
                        selectedTest(selectRow);
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void selectedTest(int selectRow) {
        VOpd test = tblLabTestTableModel.getListVOPD().get(selectRow);

        if (!hm.containsKey(test.getKey().getServiceId())) {
            try {
                List<LabResultHis> listLRH = dao.findAllHSQL("select o from LabResultHis o where o.key.opdInvId = '"
                        + test.getKey().getVouNo() + "' and o.key.labTestId = " + test.getKey().getServiceId().toString()
                        + " order by o.sortOrder");
                List<OPDLabResult> listResult = dao.findAllHSQL("select o from OPDLabResult o where o.serviceId = "
                        + test.getKey().getServiceId().toString() + " order by o.sortOrder");
                int uniqueId = 1;

                if (listLRH == null) {
                    listLRH = new ArrayList();

                    for (OPDLabResult result : listResult) {
                        LabResultHis lrh = new LabResultHis();
                        LabResultHisKey key = new LabResultHisKey();

                        key.setLabResultId(result.getResultId());
                        key.setLabTestId(result.getServiceId());
                        key.setOpdInvId(test.getKey().getVouNo());
                        key.setUniqueId(uniqueId);

                        lrh.setKey(key);
                        lrh.setResultText(result.getResultText());
                        lrh.setRefRange(result.getResultNormal());
                        lrh.setResultUnit(result.getResultUnit());
                        if (result.getResultType() != null) {
                            lrh.setResultType(result.getResultType().getTypeId());
                        }
                        lrh.setSortOrder(result.getSortOrder());

                        dao.save(lrh);
                        listLRH.add(lrh);

                        uniqueId++;
                    }
                } else {
                    for (OPDLabResult result : listResult) {
                        boolean found = false;
                        for (int i = 0; i < listLRH.size(); i++) {
                            LabResultHis lrh = listLRH.get(i);

                            if (result.getResultId().equals(lrh.getKey().getLabResultId())) {
                                if (result.getResultType() != null) {
                                    lrh.setResultType(result.getResultType().getTypeId());
                                }
                                lrh.setSortOrder(result.getSortOrder());
                                dao.save(lrh);

                                if (lrh.getResult() != null) {
                                    if (!lrh.getResult().isEmpty()) {
                                        lrh.setPrint(Boolean.TRUE);
                                    }
                                }

                                found = true;
                                i = listLRH.size();
                            }
                        }

                        if (!found) {
                            LabResultHis lrh = new LabResultHis();
                            LabResultHisKey key = new LabResultHisKey();

                            key.setLabResultId(result.getResultId());
                            key.setLabTestId(result.getServiceId());
                            key.setOpdInvId(test.getKey().getVouNo());
                            key.setUniqueId(uniqueId);

                            lrh.setKey(key);
                            lrh.setResultText(result.getResultText());
                            lrh.setRefRange(result.getResultNormal());
                            lrh.setResultUnit(result.getResultUnit());
                            if (result.getResultType() != null) {
                                lrh.setResultType(result.getResultType().getTypeId());
                            }
                            lrh.setSortOrder(result.getSortOrder());

                            dao.save(lrh);
                            listLRH.add(lrh);
                        }

                        uniqueId++;
                    }
                }
                hm.put(test.getKey().getServiceId(), listLRH);

                if (Util1.getPropValue("system.opd.labstock").equals("Y")) {
                    if (!test.isCompleteStatus()) {
                        assignLabStockInfo(test, selectRow);
                    }
                }
            } catch (Exception ex) {
                log.error("selectedTest : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
        }

        tblLabResultTableModel.setListLRH(hm.get(test.getKey().getServiceId()));
    }

    private void assignLabStockInfo(VOpd test, int row) {
        try {
            int serviceId = test.getKey().getServiceId();
            ResultSet rs = dao.execSQL("SELECT service_id, count(machine_id) as mcnt\n"
                    + "from opd_med_usage omu \n"
                    + "where service_id = " + serviceId + " and ifnull(unit_qty,0) != 0 and machine_id !=0 \n"
                    + "GROUP by service_id ");
            int machineId = 0;
            LabMachine selMacine = null;

            if (rs != null) {
                if (rs.next()) {
                    if (rs.getInt("mcnt") > 1) { //More then one machine
                        String strSql = "select * from lab_machine where lab_machine_id in (select distinct "
                                + "machine_id from opd_med_usage where service_id = " + serviceId + ")";
                        ResultSet rs1 = dao.execSQL(strSql);
                        if (rs1 != null) {
                            List<LabMachine> listLM = new ArrayList();
                            while (rs1.next()) {
                                LabMachine lm = new LabMachine();
                                lm.setlMachineId(rs1.getInt("lab_machine_id"));
                                lm.setlMachineName(rs1.getString("lab_machine_name"));
                                listLM.add(lm);
                            }
                            rs1.close();

                            LabMachineAutoCompleter lmac = new LabMachineAutoCompleter(listLM, Util1.getParent());
                            lmac.setLocationRelativeTo(null);
                            lmac.setVisible(true);
                            if (lmac.isSelected()) {
                                machineId = lmac.getSelectMachine().getlMachineId();
                                selMacine = lmac.getSelectMachine();
                            }
                        }
                    } else {
                        ResultSet rs1 = dao.execSQL("select distinct machine_id from opd_med_usage where service_id = " + serviceId);
                        if (rs1 != null) {
                            if (rs1.next()) {
                                machineId = rs1.getInt("machine_id");
                                rs1.close();
                                selMacine = (LabMachine) dao.find(LabMachine.class, machineId);
                            }
                        }
                    }
                }

                rs.close();
            }

            tblLabTestTableModel.setMachine(row, selMacine);

            String strSql = "select o from OPDMedUsage o where o.key.service = "
                    + serviceId + " and o.key.labMachine = " + machineId;
            List<OPDMedUsage> listOMU = dao.findAllHSQL(strSql);
            /*String medIds = getMedId(listOMU);
            
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            log.info("Start cost calculation.");
            insertStockFilterCodeMed(medIds);
            DateUtil.setStartTime();
            calculateMed();
            log.info("Cost calculate duration : " + DateUtil.getDuration());
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));*/

            dao.execSql("delete from med_usaged where vou_type = 'OPD' and vou_no = '"
                    + txtVouNo.getText() + "' and service_id = " + serviceId);

            boolean needToAsk = false;
            for (OPDMedUsage omu : listOMU) {
                if (omu.getCalcStatus().equals("ASK")) {
                    needToAsk = true;
                } else {
                    MUKey mkey = new MUKey();
                    String medId = omu.getKey().getMed().getMedId();
                    mkey.setMedId(medId);
                    mkey.setVouType("OPD");
                    mkey.setVouNo(txtVouNo.getText());
                    mkey.setServiceId(omu.getKey().getService());

                    MUsage mu = new MUsage();
                    mu.setKey(mkey);
                    mu.setCreatedDate(new Date());
                    mu.setLocation(omu.getLocation().getLocationId());
                    mu.setQtySmallest(omu.getQtySmall());
                    //double sCost = getSmallestCost(medId);
                    double sCost = 0;
                    mu.setSmallestCost(sCost);
                    mu.setTtlCost((sCost * mu.getQtySmallest()) * test.getQty());
                    mu.setUnitId(omu.getUnit().getItemUnitCode());
                    mu.setUnitQty(omu.getUnitQty());
                    log.info("Insert to med_usaged");
                    dao.save(mu);
                }
            }

            if (needToAsk) { //Need to edit
                ResultSet rs1 = dao.execSQL("SELECT omu.med_id, m.med_name, omu.unit_qty, "
                        + "omu.unit_id, omu.qty_smallest, omu.location_id, omu.machine_id \n"
                        + "FROM opd_med_usage omu \n"
                        + "join medicine m on omu.med_id = m.med_id \n"
                        + "WHERE service_id = " + test.getKey().getServiceId() + " and calc_status = 'ASK'");
                if (rs1 != null) {
                    List<LabUsage> list = new ArrayList();
                    log.info("Show ask list");
                    while (rs1.next()) {
                        LabUsage lu = new LabUsage();
                        lu.setDesc(rs1.getString("med_name"));
                        lu.setLocationId(rs1.getInt("location_id"));
                        lu.setMedId(rs1.getString("med_id"));
                        lu.setQty(rs1.getString("unit_qty") + " " + rs1.getString("unit_id"));
                        lu.setQtySmallest(rs1.getFloat("qty_smallest"));
                        lu.setUnitId(rs1.getString("unit_id"));
                        lu.setUnitQty(rs1.getFloat("unit_qty"));

                        list.add(lu);
                    }

                    LabUsageChoiceDialog dailog = new LabUsageChoiceDialog(test.getKey().getVouNo(),
                            test.getKey().getServiceId(), list, test.getQty());
                    dailog.setLocationRelativeTo(null);
                    dailog.setVisible(true);
                }
            }

            insertNoMachine(test);
            test.setCompleteStatus(Boolean.TRUE);
            dao.execSql("update opd_details_his set complete_status = true where opd_detail_id = '" + test.getKey().getOpdDetailId() + "'");
        } catch (Exception ex) {
            log.error("assignLabStockInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void insertNoMachine(VOpd test) {
        int serviceId = test.getKey().getServiceId();
        String strSql = "select o from OPDMedUsage o where o.key.service = "
                + serviceId + " and o.key.labMachine = 0 and calc_status != 'ASK'";
        try {
            List<OPDMedUsage> listOMU = dao.findAllHSQL(strSql);
            for (OPDMedUsage omu : listOMU) {
                MUKey mkey = new MUKey();
                String medId = omu.getKey().getMed().getMedId();
                mkey.setMedId(medId);
                mkey.setVouType("OPD");
                mkey.setVouNo(txtVouNo.getText());
                mkey.setServiceId(omu.getKey().getService());

                MUsage mu = new MUsage();
                mu.setKey(mkey);
                mu.setCreatedDate(new Date());
                mu.setLocation(omu.getLocation().getLocationId());
                mu.setQtySmallest(omu.getQtySmall());
                //double sCost = getSmallestCost(medId);
                double sCost = 0;
                mu.setSmallestCost(sCost);
                mu.setTtlCost((sCost * mu.getQtySmallest()) * test.getQty());
                mu.setUnitId(omu.getUnit().getItemUnitCode());
                mu.setUnitQty(omu.getUnitQty());
                log.info("Insert to med_usaged");
                dao.save(mu);
            }
        } catch (Exception ex) {
            log.error("insertNoMachine : " + ex.getMessage());
        }
    }

    private double getSmallestCost(String medId) {
        double cost = 0.0;
        List<StockCosting> listStockCosting = null;

        try {
            listStockCosting = dao.findAllHSQL(
                    "select o from StockCosting o where o.key.medicine.medId = '" + medId
                    + "' and o.key.userId = '" + Global.machineId + "' "
                    + "and o.key.tranOption = 'Opening'"
            );
        } catch (Exception ex) {
            log.error("getSmallestCost : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        } finally {
            dao.close();
        }

        if (listStockCosting != null) {
            if (!listStockCosting.isEmpty()) {
                StockCosting sc = listStockCosting.get(0);
                double tmpCost = NumberUtil.NZero(sc.getTtlCost());
                float tmpBalQty = NumberUtil.NZeroFloat(sc.getBlaQty());
                if (tmpBalQty != 0) {
                    cost = tmpCost / tmpBalQty;
                }
            }
        }
        return cost;
    }

    private String getMedId(List<OPDMedUsage> listOMU) {
        String medIds = "";
        for (OPDMedUsage omu : listOMU) {
            if (medIds.isEmpty()) {
                medIds = "'" + omu.getKey().getMed().getMedId() + "'";
            } else {
                medIds = medIds + ",'" + omu.getKey().getMed().getMedId() + "'";
            }
        }

        return medIds;
    }

    private void insertStockFilterCodeMed(String medId) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date < '" + DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true and m.med_id in (" + medId + ")";

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void calculateMed() {
        try {
            String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                    + Global.machineId + "'";
            String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                    + Global.machineId + "'";
            String strMethod = "AVG";

            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();

            String tmpDate = DateUtil.getTodayDateStr();
            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(tmpDate), "Opening",
                    Global.machineId);

            dao.execProc("insert_cost_detail",
                    "Opening", DateUtil.toDateStrMYSQL(tmpDate),
                    Global.machineId, strMethod);
            dao.commit();

        } catch (Exception ex) {
            log.error("calculate : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void actionMapping() {
        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRegNo);
        formActionKeyMapping(txtPtName);
        formActionKeyMapping(txtResultDate);
        formActionKeyMapping(tblLabTest);
        formActionKeyMapping(tblLabResult);
    }

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Print
        jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
        jc.getActionMap().put("F7-Action", actionPrint);

        //History
        jc.getInputMap().put(KeyStroke.getKeyStroke("F9"), "F9-Action");
        jc.getActionMap().put("F9-Action", actionHistory);

        //Clear
        jc.getInputMap().put(KeyStroke.getKeyStroke("F10"), "F10-Action");
        jc.getActionMap().put("F10-Action", actionNewForm);
    }

    private final Action actionComment = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) e.getSource();
            int modelRow = Integer.valueOf(e.getActionCommand());
            if (table.getName().equals("tblLabTest")) {
                tblLabTestTableModel.showCommentDialog(modelRow);
            } else if (table.getName().equals("tblLabResult")) {
                tblLabResultTableModel.showCommentDialog(modelRow);
            }
            //System.out.println(table.getName() + " row : " + modelRow);
            //JavaFXHTMLEditor.showEditor(null);
        }
    };

    private final Action actionSave = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };

    private final Action actionPrint = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };

    private final Action actionHistory = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };

    private final Action actionNewForm = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPtName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtResultDate = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLabTest = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLabResult = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        cboAgeRange = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtDonor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNatureOfSpecimen = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtExaminationRequired = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtBedNo = new javax.swing.JTextField();
        chkA5 = new javax.swing.JCheckBox();
        chkHeader = new javax.swing.JCheckBox();
        chkEnvelope = new javax.swing.JCheckBox();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No. ");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Reg No. ");

        txtRegNo.setEditable(false);
        txtRegNo.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name ");

        txtPtName.setEditable(false);
        txtPtName.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Result Date ");

        txtResultDate.setEditable(false);
        txtResultDate.setFont(Global.textFont);
        txtResultDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtResultDateMouseClicked(evt);
            }
        });

        tblLabTest.setFont(new java.awt.Font("Zawgyi-One", 0, 15)); // NOI18N
        tblLabTest.setModel(tblLabTestTableModel);
        tblLabTest.setRowHeight(30);
        jScrollPane1.setViewportView(tblLabTest);

        tblLabResult.setFont(new java.awt.Font("Zawgyi-One", 0, 15)); // NOI18N
        tblLabResult.setModel(tblLabResultTableModel);
        tblLabResult.setRowHeight(30);
        jScrollPane2.setViewportView(tblLabResult);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Age Range");

        cboAgeRange.setFont(Global.textFont);
        cboAgeRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboAgeRangeActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Donor");

        txtDonor.setEditable(false);
        txtDonor.setFont(Global.textFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Remark");

        txtRemark.setEditable(false);
        txtRemark.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Nature of specimen");

        txtNatureOfSpecimen.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Examination required");

        txtExaminationRequired.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDonor, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNatureOfSpecimen, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtExaminationRequired, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(txtDonor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel7)
                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel8)
                .addComponent(txtNatureOfSpecimen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel9)
                .addComponent(txtExaminationRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel10.setText("Bed No");

        txtBedNo.setEditable(false);

        chkA5.setText("A5");
        chkA5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkA5ActionPerformed(evt);
            }
        });

        chkHeader.setText("H");
        chkHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHeaderActionPerformed(evt);
            }
        });

        chkEnvelope.setText("E");
        chkEnvelope.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEnvelopeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboAgeRange, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPtName, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtResultDate, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkA5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkEnvelope, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtResultDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkA5)
                    .addComponent(chkHeader)
                    .addComponent(chkEnvelope))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cboAgeRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addContainerGap())
        );

        chkHeader.getAccessibleContext().setAccessibleName("Header");
    }// </editor-fold>//GEN-END:initComponents

    private void txtResultDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtResultDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtResultDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtResultDateMouseClicked

    private void cboAgeRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboAgeRangeActionPerformed
        try {
            OPDHis his = (OPDHis) dao.find(OPDHis.class, txtVouNo.getText());
            if (his != null) {
                AgeRange ar = (AgeRange) cboAgeRange.getSelectedItem();
                his.setAgeRange(ar);
                his.setExamRequired(txtExaminationRequired.getText().trim());
                his.setNos(txtNatureOfSpecimen.getText().trim());
                dao.save(his);
            }
        } catch (Exception ex) {
            log.error("save age range : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            dao.close();
        }
    }//GEN-LAST:event_cboAgeRangeActionPerformed

    private void chkA5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkA5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkA5ActionPerformed

    private void chkHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHeaderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkHeaderActionPerformed

    private void chkEnvelopeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEnvelopeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkEnvelopeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboAgeRange;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkEnvelope;
    private javax.swing.JCheckBox chkHeader;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblLabResult;
    private javax.swing.JTable tblLabTest;
    private javax.swing.JTextField txtBedNo;
    private javax.swing.JTextField txtDonor;
    private javax.swing.JTextField txtExaminationRequired;
    private javax.swing.JTextField txtNatureOfSpecimen;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtResultDate;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

}
