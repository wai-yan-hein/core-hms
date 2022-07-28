/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.auditlog;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.DCDetailHis;
import com.cv.app.inpatient.database.entity.DCDoctorFee;
import com.cv.app.inpatient.database.entity.DCHis;
import com.cv.app.inpatient.database.entity.DCStatus;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.ot.database.entity.DrDetailId;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.ui.auditlog.SaleAudit;
import com.cv.app.pharmacy.ui.auditlog.SaleAuditLog;
import com.cv.app.pharmacy.ui.auditlog.SaleAuditTableModel;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class DCAudit extends javax.swing.JPanel implements SelectionObserver {

    private final AbstractDataAccess dao = Global.dao;
    private final SaleAuditTableModel model = new SaleAuditTableModel();
    static Logger log = Logger.getLogger(SaleAudit.class.getName());
    private DCHis dcHis;
    private DCEntryRestoreView rv = new DCEntryRestoreView();

    /**
     * Creates new form DCAudit
     */
    public DCAudit() {
        initComponents();
        initTable();
        panelRestore.setLayout(new ScrollPaneLayout());
        panelRestore.setViewportView(rv);

        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
    }

    private String getSql() {
        String strSql = "select bdh.bk_dc_id, bdh.dc_inv_id, bdh.dc_date, bdh.bk_date, "
                + "u.user_name bk_user, bdh.vou_total\n"
                + "from bk_dc_his bdh\n"
                + "left join appuser u on bdh.bk_user = u.user_id\n"
                + "left join session sess on bdh.session_id = sess.session_id\n"
                + "where date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'";

        if (!txtVouNo.getText().trim().isEmpty()) {
            strSql = strSql + " and bdh.dc_inv_id = '" + txtVouNo.getText().trim() + "' ";
        }

        if (!txtPatientNo.getText().trim().isEmpty()) {
            strSql = strSql + " and bdh.patient_id = '" + txtPatientNo.getText().trim() + "'";
        }
        strSql = strSql + " order by bdh.dc_inv_id, bdh.bk_date";
        return strSql;
    }

    private void search() {
        try {
            ResultSet rs = dao.execSQL(getSql());
            if (rs != null) {
                List<SaleAuditLog> listSAL = new ArrayList();
                while (rs.next()) {
                    SaleAuditLog sal = new SaleAuditLog();
                    sal.setBkId(rs.getLong("bk_dc_id"));
                    sal.setTranDate(rs.getTimestamp("bk_date"));
                    sal.setTranUser(rs.getString("bk_user"));
                    sal.setVouDate(rs.getTimestamp("dc_date"));
                    sal.setVouNo(rs.getString("dc_inv_id"));
                    sal.setVouTotal(rs.getDouble("vou_total"));

                    listSAL.add(sal);
                }

                rs.close();
                model.setListSAL(listSAL);
            }
        } catch (Exception ex) {
            log.error("search : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblAuditList.getTableHeader().setFont(Global.lableFont);
        tblAuditList.getColumnModel().getColumn(0).setPreferredWidth(80);//Vou No
        tblAuditList.getColumnModel().getColumn(1).setPreferredWidth(100);//Bak-Date
        tblAuditList.getColumnModel().getColumn(2).setPreferredWidth(50);//Vou-Date
        tblAuditList.getColumnModel().getColumn(3).setPreferredWidth(50);//Vou Total
        tblAuditList.getColumnModel().getColumn(4).setPreferredWidth(50);//User

        tblAuditList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAuditList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    if (tblAuditList.getSelectedRow() < model.getRowCount()) {
                        int selRow = tblAuditList.convertRowIndexToModel(tblAuditList.getSelectedRow());
                        SaleAuditLog sal = model.getSaleAuditLog(selRow);
                        //log.info("valueChanged : " + e.getValueIsAdjusting() + " - " + sal.getBkId());
                        if (sal != null) {
                            getDcData(sal);
                            rv.selected("DCVouList", dcHis);
                        } else {
                            rv.newForm();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (selectObj != null) {
            switch (source.toString()) {
                case "PatientSearch":
                    Patient pt = (Patient) selectObj;
                    txtPatientNo.setText(pt.getRegNo());
                    txtPTName.setText(pt.getPatientName());
                    break;
                case "CustomerList":
                    Trader trd = (Trader) selectObj;
                    txtPatientNo.setText(trd.getTraderId());
                    txtPTName.setText(trd.getTraderName());
                    break;
            }
        }
    }

    private void getCustomerList() {
        switch (Util1.getPropValue("system.app.usage.type")) {
            case "Hospital":
            case "School":
                PatientSearch ptDialog = new PatientSearch(dao, this);
                break;
            /*case "School":
             MarchantSearch dialog = new MarchantSearch(dao, this);
             break;*/
            default:
                UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao, -1);
                break;
        }
    }

    private boolean getDcData(SaleAuditLog sal) {
        boolean status = true;
        try {
            ResultSet rs = dao.execSQL("select * from bk_dc_his where bk_dc_id = " + sal.getBkId());
            if (rs != null) {
                rs.next();

                dcHis = new DCHis();
                dcHis.setAdmissionNo(rs.getString("admission_no"));
                //dcHis.setAgeRange(null);
                Appuser usr = (Appuser) dao.find(Appuser.class, rs.getString("created_by"));
                dcHis.setCreatedBy(usr);
                dcHis.setCreatedDate(rs.getDate("created_date"));
                Currency curr = (Currency) dao.find(Currency.class, rs.getString("currency_id"));
                dcHis.setCurrency(curr);
                DCStatus ds = (DCStatus) dao.find(DCStatus.class, rs.getInt("dc_status"));
                dcHis.setDcStatus(ds);
                dcHis.setDeleted(false);
                dcHis.setDiagnosis(null);
                dcHis.setDiscountA(rs.getDouble("disc_a"));
                dcHis.setDiscountP(rs.getDouble("tax_p"));
                if (rs.getString("doctor_id") != null) {
                    Doctor dr = (Doctor) dao.find(Doctor.class, rs.getString("doctor_id"));
                    dcHis.setDoctor(dr);
                }
                //dcHis.setDonorName(TOOL_TIP_TEXT_KEY);
                dcHis.setExRateToP(rs.getDouble("ex_rate_to_pcurr"));
                dcHis.setInvDate(rs.getDate("dc_date"));
                dcHis.setOpdInvId(rs.getString("dc_inv_id"));
                dcHis.setPaid(rs.getDouble("paid"));
                dcHis.setPaidCurrAmount(rs.getDouble("paid_curr_amt"));
                dcHis.setPaidCurrExRate(rs.getDouble("paid_curr_ex_rate"));
                Currency pcurr = (Currency) dao.find(Currency.class, rs.getString("paid_currency_id"));
                dcHis.setPaidCurrnecy(pcurr);
                if (rs.getString("patient_id") != null) {
                    Patient pat = (Patient) dao.find(Patient.class, rs.getString("patient_id"));
                    dcHis.setPatient(pat);
                }
                //dcHis.setPatientName(TOOL_TIP_TEXT_KEY);
                PaymentType pt = (PaymentType) dao.find(PaymentType.class, rs.getInt("payment_id"));
                dcHis.setPaymentType(pt);
                dcHis.setRemark(rs.getString("remark"));
                dcHis.setSession(rs.getInt("session_id"));
                dcHis.setTaxA(rs.getDouble("tax_a"));
                dcHis.setTaxP(rs.getDouble("tax_p"));
                if (rs.getString("updated_by") != null) {
                    Appuser uusr = (Appuser) dao.find(Appuser.class, rs.getString("updated_by"));
                    dcHis.setUpdatedBy(uusr);
                }
                dcHis.setUpdatedDate(rs.getDate("updated_date"));
                dcHis.setVouBalance(rs.getDouble("vou_balance"));
                dcHis.setVouTotal(rs.getDouble("vou_total"));

                //Detail
                ResultSet rsSD = dao.execSQL(
                        "select * from bk_dc_details_his where bk_dc_id = " + sal.getBkId()
                        + " order by unique_id"
                );
                if (rsSD != null) {
                    List<DCDetailHis> listDDH = new ArrayList();
                    while (rsSD.next()) {
                        DCDetailHis ddh = new DCDetailHis();
                        ddh.setAmount(rsSD.getDouble("amount"));
                        ChargeType ct = (ChargeType) dao.find(ChargeType.class, rsSD.getInt("charge_type"));
                        ddh.setChargeType(ct);
                        //ddh.setFeesVersionId(WIDTH);
                        ddh.setPrice(rsSD.getDouble("price"));
                        ddh.setQuantity(rsSD.getInt("qty"));
                        //ddh.setReaderStatus(status);
                        Integer serviceId = rsSD.getInt("service_id");
                        InpService sv = (InpService) dao.find(InpService.class, serviceId);
                        ddh.setService(sv);
                        ddh.setSrvFee1(rsSD.getDouble("srv_fee1"));
                        ddh.setSrvFee2(rsSD.getDouble("srv_fee2"));
                        ddh.setSrvFee3(rsSD.getDouble("srv_fee3"));
                        ddh.setSrvFee4(rsSD.getDouble("srv_fee4"));
                        ddh.setSrvFee5(rsSD.getDouble("srv_fee5"));
                        ddh.setUniqueId(rsSD.getInt("unique_id"));

                        if (isNeedDetail(serviceId)) {
                            Integer detailId = rsSD.getInt("dc_detail_id");
                            ResultSet rsDR = dao.execSQL(
                                    "select j.dr_fee_id, ddf.doctor_id, ddf.dr_fee\n"
                                    + "from dc_details_his j, dc_doctor_fee ddf\n"
                                    + "where j.dc_detail_id = ddf.dc_detail_id and j.dc_detail_id = " + detailId.toString()
                            );
                            if (rsDR != null) {
                                List<DCDoctorFee> listDCDF = new ArrayList();
                                while (rsDR.next()) {
                                    DCDoctorFee dcdf = new DCDoctorFee();
                                    Doctor dr = (Doctor) dao.find(Doctor.class, rsDR.getString("doctor_id"));
                                    dcdf.setDoctor(dr);
                                    dcdf.setDrFee(rsDR.getDouble("dr_fee"));

                                    listDCDF.add(dcdf);
                                }
                                ddh.setListDCDF(listDCDF);
                                rsDR.close();
                            }
                        }

                        listDDH.add(ddh);
                    }
                    dcHis.setListOPDDetailHis(listDDH);
                    rsSD.close();
                }
                rs.close();
            }
        } catch (Exception ex) {
            log.error("getDcData : " + ex.toString());
            status = false;
        }
        return status;
    }

    private boolean isNeedDetail(int serviceId) {
        boolean status = false;
        try {
            List<DrDetailId> listDDI = dao.findAllHSQL(
                    "select o from DrDetailId o where o.key.option = 'DC' and o.key.serviceId = " + serviceId);
            if (listDDI != null) {
                if (!listDDI.isEmpty()) {
                    status = true;
                }
            }
        } catch (Exception ex) {
            log.error("isNeedDetail : " + ex.getMessage());
        } finally {
            dao.close();
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

        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPatientNo = new javax.swing.JTextField();
        txtPTName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAuditList = new javax.swing.JTable();
        panelRestore = new javax.swing.JScrollPane();
        butSearch = new javax.swing.JButton();
        txtVouNo = new javax.swing.JTextField();

        jLabel1.setText("From ");

        jLabel2.setText("To ");

        jLabel3.setText("Patient ");

        tblAuditList.setFont(Global.textFont);
        tblAuditList.setModel(model);
        tblAuditList.setRowHeight(23);
        jScrollPane1.setViewportView(tblAuditList);

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPatientNo)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtPTName)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRestore, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFrom, txtTo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(butSearch)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPTName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                    .addComponent(panelRestore))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane panelRestore;
    private javax.swing.JTable tblAuditList;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPTName;
    private javax.swing.JTextField txtPatientNo;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
