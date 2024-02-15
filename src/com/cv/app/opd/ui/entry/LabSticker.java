/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.OPDHis;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.tempentity.LabUsage;
import com.cv.app.opd.database.view.VOpd;
import com.cv.app.opd.database.view.VService;
import com.cv.app.opd.ui.common.LabStickerMedUseTableModel;
import com.cv.app.opd.ui.common.LabStickerTableModel;
import com.cv.app.opd.ui.common.LabStickerTestTableModel;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class LabSticker extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(LabSticker.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final LabStickerTableModel model = new LabStickerTableModel();
    private final LabStickerTestTableModel lstModel = new LabStickerTestTableModel();
    private final LabStickerMedUseTableModel muModel = new LabStickerMedUseTableModel();
    private String option = "FILTER";

    /**
     * Creates new form LabSticker
     */
    public LabSticker() {
        initComponents();
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        initTable();
        searchVoucher();
    }

    private void getPatient() {
        try {
            if (option.equals("FILTER")) {
                if (txtRegNo.getText() == null || txtRegNo.getText().trim().isEmpty()) {
                    txtRegNo.setText("");
                    txtPtName.setText("");
                } else {
                    String regNo = txtRegNo.getText().trim();
                    List<Patient> listP = dao.findAllHSQL(
                            "select o from Patient o where o.regNo = '" + regNo + "'");
                    Patient ptt = null;
                    if (listP != null) {
                        if (!listP.isEmpty()) {
                            ptt = listP.get(0);
                        }
                    }

                    if (ptt == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registerationn no.",
                                "Reg No.", JOptionPane.ERROR_MESSAGE);
                        txtPtName.setText("");
                    } else {
                        txtRegNo.setText(ptt.getRegNo());
                        txtPtName.setText(ptt.getPatientName());
                    }
                }
            } else {
                if (txtRegNo1.getText() == null || txtRegNo1.getText().trim().isEmpty()) {
                    txtRegNo1.setText("");
                    txtPtName1.setText("");
                } else {
                    String regNo = txtRegNo1.getText().trim();
                    List<Patient> listP = dao.findAllHSQL(
                            "select o from Patient o where o.regNo = '" + regNo + "'");
                    Patient ptt = null;
                    if (listP != null) {
                        if (!listP.isEmpty()) {
                            ptt = listP.get(0);
                        }
                    }

                    if (ptt == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registerationn no.",
                                "Reg No.", JOptionPane.ERROR_MESSAGE);
                        txtPtName1.setText("");
                    } else {
                        txtRegNo1.setText(ptt.getRegNo());
                        txtPtName1.setText(ptt.getPatientName());
                        if (ptt.getDob() != null) {
                            txtAge.setText(DateUtil.getAge(DateUtil.toDateStr(ptt.getDob())));
                        }
                        
                        if (ptt.getAdmissionNo() != null) {
                            if (!ptt.getAdmissionNo().isEmpty()) {
                                txtAdmNo1.setText(ptt.getAdmissionNo());
                                AdmissionKey key = new AdmissionKey();
                                key.setAmsNo(ptt.getAdmissionNo());
                                key.setRegister(ptt);
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
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getPatient : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PatientSearch":
                Patient ptt = (Patient) selectObj;
                if (option.equals("FILTER")) {
                    txtRegNo.setText(ptt.getRegNo());
                    txtPtName.setText(ptt.getPatientName());
                } else {
                    txtRegNo1.setText(ptt.getRegNo());
                    txtPtName1.setText(ptt.getPatientName());
                }
                break;
        }
    }

    private void searchVoucher() {
        String strSql = "select distinct date(sh.opd_date) opd_date, sh.opd_inv_id, sh.remark, \n"
                + "sh.patient_id cus_id, sh.patient_name cus_name, sh.admission_no, sh.donor_name \n"
                + "from opd_his sh\n"
                + "join opd_details_his sdh on sh.opd_inv_id = sdh.vou_no\n"
                + "join opd_service med on sdh.service_id = med.service_id\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "where date(sh.opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQL(txtTo.getText()) + "' and sh.deleted = false ";

        if (!txtRegNo.getText().trim().isEmpty()) {
            strSql = strSql + " and sh.patient_id = '" + txtRegNo.getText().trim() + "'";
        } else if (!txtPtName.getText().trim().isEmpty()) {
            strSql = strSql + " and upper(sh.patient_name) like '%"
                    + txtPtName.getText().trim().toUpperCase() + "%'";
        }

        String filterId = "";
        try {
            List<VService> listS = dao.findAllHSQL("select o from VService o where o.groupId = 1");
            for (VService vs : listS) {
                if (filterId.isEmpty()) {
                    filterId = vs.getServiceId().toString();
                } else {
                    filterId = filterId + "," + vs.getServiceId().toString();
                }
            }
        } catch (Exception ex) {
            log.error("getHSQL1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        if (!filterId.isEmpty()) {
            strSql = strSql + " and sdh.service_id in (" + filterId + ")";
        }

        strSql = strSql + " order by sh.opd_date desc, sh.opd_inv_id desc";

        List<VoucherSearch> listVS = new ArrayList();
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    VoucherSearch vs = new VoucherSearch();
                    vs.setTranDate(rs.getDate("opd_date"));
                    vs.setInvNo(rs.getString("opd_inv_id"));
                    vs.setCusNo(rs.getString("cus_id"));
                    vs.setRefNo(rs.getString("admission_no"));
                    vs.setCusName(rs.getString("cus_name"));
                    vs.setUserName(rs.getString("remark"));
                    vs.setLocation(rs.getString("donor_name"));
                    listVS.add(vs);
                }
            }
        } catch (Exception ex) {
            log.error("searchVoucher : " + ex.toString());
        } finally {
            dao.close();
        }

        model.setList(listVS);
        txtTotalRecord.setValue(listVS.size());
    }

    private void initTable() {
        tblVou.getTableHeader().setFont(Global.lableFont);
        tblVou.setFont(Global.textFont);
        tblVou.setRowHeight(Global.rowHeight);

        tblVou.getColumnModel().getColumn(0).setPreferredWidth(20);//Date
        tblVou.getColumnModel().getColumn(1).setPreferredWidth(50);//Vou No
        tblVou.getColumnModel().getColumn(2).setPreferredWidth(30);//Reg No.
        tblVou.getColumnModel().getColumn(3).setPreferredWidth(30);//Adm
        tblVou.getColumnModel().getColumn(4).setPreferredWidth(150);//Name

        tblVou.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVou.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int selectRow = -1;
            if (tblVou.getSelectedRow() >= 0) {
                selectRow = tblVou.convertRowIndexToModel(tblVou.getSelectedRow());
            }
            //System.out.println("Table Selection : " + selectRow);
            if (selectRow >= 0) {
                VoucherSearch vou = model.getSelectedRecord(selectRow);
                clear();
                setData(vou);
                assignLabTest(vou.getInvNo());
            }
        });

        tblLabTest.getColumnModel().getColumn(0).setPreferredWidth(150);//Lab Test
        tblLabTest.getColumnModel().getColumn(1).setPreferredWidth(10);//Qty
        tblLabTest.getColumnModel().getColumn(2).setPreferredWidth(20);//Price
        tblLabTest.getColumnModel().getColumn(3).setPreferredWidth(30);//Amount

        tblLabTest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLabTest.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int selectRow = -1;
            if (tblLabTest.getSelectedRow() >= 0) {
                selectRow = tblLabTest.convertRowIndexToModel(tblLabTest.getSelectedRow());
            }
            //System.out.println("Table Selection : " + selectRow);
            if (selectRow >= 0) {
                VOpd test = lstModel.getSelectedRecord(selectRow);
                Integer serviceId = test.getKey().getServiceId();
                assignLabStock(txtVouNo.getText(), serviceId);
            }
        });

        tblLabUsage.getColumnModel().getColumn(0).setPreferredWidth(30);//Med Id
        tblLabUsage.getColumnModel().getColumn(1).setPreferredWidth(150);//Description
        tblLabUsage.getColumnModel().getColumn(2).setPreferredWidth(10);//Qty
        tblLabUsage.getColumnModel().getColumn(3).setPreferredWidth(10);//Unit
        tblLabUsage.getColumnModel().getColumn(3).setPreferredWidth(30);//Location
    }

    private void setData(VoucherSearch vou) {
        if (vou != null) {
            txtVouDate.setText(DateUtil.toDateStr(vou.getTranDate()));
            txtVouNo.setText(vou.getInvNo());
            txtRegNo1.setText(vou.getCusNo());
            txtAdmNo1.setText(vou.getRefNo());
            txtPtName1.setText(vou.getCusName());
            boolean donorOrExternal = false;

            if (vou.getLocation() != null) {
                if (!vou.getLocation().isEmpty()) {
                    txtPtName1.setText(vou.getLocation());
                    donorOrExternal = true;
                } else {
                    if (vou.getUserName() != null) {
                        if (!vou.getUserName().isEmpty()) {
                            txtPtName1.setText(vou.getUserName());
                            donorOrExternal = true;
                        }
                    }
                }
            } else {
                if (vou.getUserName() != null) {
                    if (!vou.getUserName().isEmpty()) {
                        txtPtName1.setText(vou.getUserName());
                        donorOrExternal = true;
                    }
                }
            }

            try {
                OPDHis opdHis = (OPDHis) dao.find(OPDHis.class, vou.getInvNo());
                String strAge = "";
                if (NumberUtil.NZeroInt(opdHis.getAge()) != 0 || NumberUtil.NZeroInt(opdHis.getAgeM()) != 0
                        || NumberUtil.NZeroInt(opdHis.getAgeD()) != 0) {
                    if (NumberUtil.NZeroInt(opdHis.getAge()) != 0) {
                        strAge = opdHis.getAge() + "y";
                    }

                    if (NumberUtil.NZeroInt(opdHis.getAgeM()) != 0) {
                        if (strAge.isEmpty()) {
                            strAge = opdHis.getAge() + "m";
                        } else {
                            strAge = strAge + "," + opdHis.getAge() + "m";
                        }
                    }

                    if (NumberUtil.NZeroInt(opdHis.getAgeD()) != 0) {
                        if (strAge.isEmpty()) {
                            strAge = opdHis.getAge() + "d";
                        } else {
                            strAge = strAge + "," + opdHis.getAge() + "d";
                        }
                    }

                }

                if (!strAge.isEmpty()) {
                    txtAge.setText(strAge);
                } else {
                    Patient pt = opdHis.getPatient();
                    if (pt != null) {
                        if (donorOrExternal) {

                        } else {
                            if (pt.getDob() != null) {
                                txtAge.setText(DateUtil.getAge(DateUtil.toDateStr(pt.getDob())));
                            }
                        }
                    }
                }

                if (opdHis.getAdmissionNo() != null) {
                    if (!opdHis.getAdmissionNo().isEmpty()) {
                        AdmissionKey key = new AdmissionKey(opdHis.getPatient(), opdHis.getAdmissionNo());
                        Ams adm = (Ams) dao.find(Ams.class, key);
                        if (adm != null) {
                            if (adm.getBuildingStructure() != null) {
                                txtBedNo.setText(adm.getBuildingStructure().getDescription());
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("setData : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void clear() {
        txtVouDate.setText("");
        txtVouNo.setText("");
        txtRegNo1.setText("");
        txtAdmNo1.setText("");
        txtPtName1.setText("");
        txtAge.setText("");
        txtBedNo.setText("");
    }

    private void print() {
        try {
            if (txtRegNo1.getText() != null) {
                if (!txtRegNo1.getText().trim().isEmpty()) {
                    String printerName = Util1.getPropValue("system.labstiker.printer");
                    String reportName = Util1.getPropValue("system.labstiker.report");
                    String viewStatus = Util1.getPropValue("system.labstiker.printer.mode");
                    String reportPath = Util1.getAppWorkFolder() + Util1.getPropValue("report.folder.path")
                            + "clinic/" + reportName;

                    if (!reportName.isEmpty()) {
                        if (!reportName.equals("-")) {
                            Map<String, Object> params = new HashMap();
                            params.put("tran_date", txtVouDate.getText());
                            params.put("reg_no", txtRegNo1.getText());
                            params.put("pt_name", txtPtName1.getText());
                            params.put("room_no", txtBedNo.getText());
                            params.put("age", txtAge.getText());

                            if (viewStatus.equals("View")) {
                                ReportUtil.viewReport(reportPath, params, dao.getConnection());
                            } else {
                                JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
                                ReportUtil.printJasper(jp, printerName);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("print : " + ex.getMessage());
        }
    }

    private void assignLabTest(String vouNo) {
        try {
            List<VOpd> listOPD = dao.findAllHSQL("select o from VOpd o where o.key.vouNo = '"
                    + vouNo + "' and o.groupId = 1 order by o.key.uniqueId");
            lstModel.setList(listOPD);
        } catch (Exception ex) {
            log.error("assignLabTest ; " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void assignLabStock(String vouNo, Integer serviceId) {
        try {
            String strSql = "SELECT mu.vou_type, mu.vou_no, mu.service_id, mu.med_id, mu.unit_id, \n"
                    + "mu.unit_qty, mu.qty_smallest, mu.created_date, mu.updated_date,\n"
                    + "       mu.location_id, mu.smallest_cost, mu.total_cost, m.med_name, l.location_name\n"
                    + "  FROM med_usaged mu \n"
                    + "  JOIN medicine m on mu.med_id = m.med_id \n"
                    + "  JOIN location l on mu.location_id = l.location_id \n"
                    + " WHERE mu.vou_type = 'OPD' and mu.vou_no = '" + vouNo
                    + "' and mu.service_id = " + serviceId;
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<LabUsage> list = new ArrayList();
                while (rs.next()) {
                    LabUsage lu = new LabUsage();
                    lu.setCreatedDate(rs.getDate("created_date"));
                    lu.setDesc(rs.getString("med_name"));
                    lu.setLocationId(rs.getInt("location_id"));
                    lu.setLocationName(rs.getString("location_name"));
                    lu.setMedId(rs.getString("med_id"));
                    lu.setQtySmallest(rs.getFloat("qty_smallest"));
                    lu.setServiceId(rs.getInt("service_id"));
                    lu.setSmallestCost(rs.getDouble("smallest_cost"));
                    lu.setTtlCost(rs.getDouble("total_cost"));
                    lu.setUnitId(rs.getString("unit_id"));
                    lu.setUnitQty(rs.getFloat("unit_qty"));
                    lu.setUpdatedDate(rs.getDate("updated_date"));
                    lu.setVouNo(rs.getString("vou_no"));
                    lu.setVouType(rs.getString("vou_type"));

                    list.add(lu);
                }

                muModel.setList(list);
            }
        } catch (Exception ex) {
            log.error("assignLabStock : " + ex.getMessage());
        } finally {
            dao.close();
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
        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        butRefresh = new javax.swing.JButton();
        txtRegNo = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVou = new javax.swing.JTable();
        txtTotalRecord = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtVouDate = new javax.swing.JFormattedTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        txtRegNo1 = new javax.swing.JTextField();
        txtAdmNo1 = new javax.swing.JTextField();
        txtPtName1 = new javax.swing.JTextField();
        txtBedNo = new javax.swing.JTextField();
        butPrint = new javax.swing.JButton();
        txtAge = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLabTest = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblLabUsage = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        txtFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("From"));

        txtTo.setBorder(javax.swing.BorderFactory.createTitledBorder("To"));

        butRefresh.setText("Refresh");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        txtRegNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Ret No."));
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        txtPtName.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));
        txtPtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPtNameMouseClicked(evt);
            }
        });

        tblVou.setModel(model);
        tblVou.setRowHeight(23);
        jScrollPane1.setViewportView(tblVou);

        txtTotalRecord.setEditable(false);
        txtTotalRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setText("Total Records : ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butRefresh))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPtName))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFrom, txtRegNo, txtTo});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(10, 10, 10))
        );

        txtVouDate.setEditable(false);
        txtVouDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Vou Date"));

        txtVouNo.setEditable(false);
        txtVouNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Vou No"));

        txtRegNo1.setBorder(javax.swing.BorderFactory.createTitledBorder("Reg No."));
        txtRegNo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNo1ActionPerformed(evt);
            }
        });

        txtAdmNo1.setEditable(false);
        txtAdmNo1.setBorder(javax.swing.BorderFactory.createTitledBorder("Adm No."));

        txtPtName1.setEditable(false);
        txtPtName1.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));
        txtPtName1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPtName1MouseClicked(evt);
            }
        });

        txtBedNo.setEditable(false);
        txtBedNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Room No"));

        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        txtAge.setEditable(false);
        txtAge.setBorder(javax.swing.BorderFactory.createTitledBorder("Age"));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRegNo1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAdmNo1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPtName1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butPrint)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRegNo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAdmNo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPtName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butPrint)
                    .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Lab Test"));

        tblLabTest.setModel(lstModel);
        jScrollPane2.setViewportView(tblLabTest);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Lab Usage"));

        tblLabUsage.setModel(muModel);
        jScrollPane3.setViewportView(tblLabUsage);

        jFormattedTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));

        jComboBox1.setBorder(javax.swing.BorderFactory.createTitledBorder("Machine"));

        jButton1.setText("Add Usage");

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Machine Usage"));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        option = "FILTER";
        getPatient();
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void txtPtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPtNameMouseClicked
        if (evt.getClickCount() == 2) {
            option = "FILTER";
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtPtNameMouseClicked

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        searchVoucher();
    }//GEN-LAST:event_butRefreshActionPerformed

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        print();
    }//GEN-LAST:event_butPrintActionPerformed

    private void txtPtName1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPtName1MouseClicked
        if (evt.getClickCount() == 2) {
            option = "STIKER";
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtPtName1MouseClicked

    private void txtRegNo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNo1ActionPerformed
        option = "STIKER";
        getPatient();
    }//GEN-LAST:event_txtRegNo1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butRefresh;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable tblLabTest;
    private javax.swing.JTable tblLabUsage;
    private javax.swing.JTable tblVou;
    private javax.swing.JTextField txtAdmNo1;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtBedNo;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtPtName1;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextField txtRegNo1;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotalRecord;
    private javax.swing.JFormattedTextField txtVouDate;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
