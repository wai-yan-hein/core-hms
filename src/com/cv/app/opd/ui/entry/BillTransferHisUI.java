/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import static com.cv.app.common.Global.dao;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.BillTransferHis;
import com.cv.app.opd.database.helper.BillTransferDetail;
import com.cv.app.opd.ui.common.BillTransferDetailHisTableModel;
import com.cv.app.opd.ui.common.BillTransferHisTableModel;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author cv-svr
 */
public class BillTransferHisUI extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(BillTransferHisUI.class);
    private final int mouseClick = 2;
    private final BillTransferHisTableModel bthTableModel = new BillTransferHisTableModel();
    private final BillTransferDetailHisTableModel bthdTable = new BillTransferDetailHisTableModel();

    /**
     * Creates new form BillTransferHIs
     */
    public BillTransferHisUI() {
        initComponents();
        initCombo();
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        initTable();

        butClear.setEnabled(false);
        butDelete.setEnabled(false);
        butPrint.setEnabled(false);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;
                String cusId = cus.getTraderId();
                String cusName = cus.getTraderName();
                txtCusCode.setText(cusId);
                txtCustomer.setText(cusName);
                break;
        }
    }

    private void getCustomer() {
        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            try {
                Trader trader = getTrader(txtCusCode.getText().trim().toUpperCase());
                if (trader == null) {
                    txtCusCode.setText(null);
                    txtCusName.setText(null);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid customer code.",
                            "Trader Code", JOptionPane.ERROR_MESSAGE);

                } else {
                    selected("CustomerList", trader);
                }
            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtCusCode.requestFocus();
        }
    }

    private Trader getTrader(String traderId) {
        Trader trader = null;
        try {
            String strFieldName = "o.traderId";

            String strSql = "select o from Trader where " + strFieldName + " = '" + traderId + "'";
            log.info("strSql 1: " + strSql);
            List<Trader> listTrader = dao.findAllHSQL(strSql);
            if (listTrader != null) {
                if (!listTrader.isEmpty()) {
                    trader = listTrader.get(0);
                }
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return trader;
    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboBillType, dao.findAllHSQL(
                    "select o from PaymentType o order by o.paymentTypeName"));
            BindingUtil.BindCombo(cboCurrency, dao.findAllHSQL(
                    "select o from Currency o order by o.currencyCode"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblHistory.getColumnModel().getColumn(0).setPreferredWidth(10);//Date
        tblHistory.getColumnModel().getColumn(1).setPreferredWidth(40);//Vou No.
        tblHistory.getColumnModel().getColumn(2).setPreferredWidth(110);//Bill
        tblHistory.getColumnModel().getColumn(3).setPreferredWidth(130);//Customer
        tblHistory.getColumnModel().getColumn(4).setPreferredWidth(30);//Ttl-Disc
        tblHistory.getColumnModel().getColumn(5).setPreferredWidth(30);//Ttl-Paid

        tblHistory.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());

        tblHDetail.getColumnModel().getColumn(0).setPreferredWidth(10);//Reg No.
        tblHDetail.getColumnModel().getColumn(1).setPreferredWidth(10);//Admission No.
        tblHDetail.getColumnModel().getColumn(2).setPreferredWidth(200);//Name
        tblHDetail.getColumnModel().getColumn(3).setPreferredWidth(30);//Discount
        tblHDetail.getColumnModel().getColumn(4).setPreferredWidth(30);//Paid
    }

    private void search() {
        String strSql = "select bth_id, tran_date, pt.payment_type_name, concat(bth.trader_id, '-', t.trader_name) as customer,\n"
                + "bth.remark, total_amt, bth.deleted, bth.discount \n"
                + "from bill_transfer_his bth\n"
                + "join payment_type pt on bth.bill_type = pt.payment_type_id\n"
                + "left join trader t on bth.trader_id = t.trader_id\n"
                + "where tran_option = '" + getSelType() + "'\n"
                + "and bth.tran_date between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "' \n"
                + "and currency_id = '" + getSelCurrency() + "'";

        if (!txtCusCode.getText().trim().isEmpty()) {
            strSql = strSql + "and bth.trader_id = '" + txtCusCode.getText().trim() + "' \n";
        }

        if (cboBillType.getSelectedItem() instanceof PaymentType) {
            PaymentType pt = (PaymentType) cboBillType.getSelectedItem();
            strSql = strSql + "and bth.bill_type = " + pt.getPaymentTypeId().toString();
        }

        if (chkDeleted.isSelected()) {
            strSql = strSql + "and ifnull(deleted,false) = true";
        } else {
            strSql = strSql + "and ifnull(deleted,false) = false";
        }

        strSql = strSql + " order by bth.tran_date, bth.bth_id";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<BillTransferHis> listBTH = new ArrayList();
                double ttlDisc = 0;
                double ttlPaid = 0;
                while (rs.next()) {
                    BillTransferHis bth = new BillTransferHis();
                    bth.setBthId(rs.getString("bth_id"));
                    bth.setTranDate(rs.getDate("tran_date"));
                    bth.setTranOption(rs.getString("payment_type_name"));
                    bth.setTraderId(rs.getString("customer"));
                    bth.setRemark(rs.getString("remark"));
                    ttlPaid += NumberUtil.NZero(rs.getDouble("total_amt"));
                    bth.setTotalAmt(rs.getDouble("total_amt"));
                    bth.setDelated(rs.getBoolean("deleted"));
                    ttlDisc += NumberUtil.NZero(rs.getDouble("discount"));
                    bth.setDiscount(rs.getDouble("discount"));
                    listBTH.add(bth);
                }

                rs.close();
                bthTableModel.setListTBL(listBTH);
                txtHTtlRecord.setValue(listBTH.size());
                txtHTtlPaid.setValue(ttlPaid);
                txtHTtlDisc.setValue(ttlDisc);
            }
        } catch (Exception ex) {
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void assignDetail(int row) {
        BillTransferHis bth = bthTableModel.getData(row);
        String bthId = bth.getBthId();
        txtTranDate.setText(DateUtil.toDateStr(bth.getTranDate()));
        txtTranNo.setText(bthId);
        txtBillType.setText(bth.getTranOption());
        txtCustomer.setText(bth.getTraderId());
        txtRemark.setText(bth.getRemark());
        txtTtlAmt.setValue(bth.getTotalAmt());
        txtTtlDisc.setValue(bth.getDiscount());

        //Assign Detail
        String strSql = "select a.reg_no, a.admission_no, b.patient_name, a.discount, a.paid\n"
                + "from bill_transfer_detail_his a\n"
                + "left join patient_detail b on a.reg_no = b.reg_no\n"
                + "where bth_id = '" + bthId + "'\n"
                + "order by b.patient_name";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<BillTransferDetail> listBTD = new ArrayList();
                while (rs.next()) {
                    BillTransferDetail btd = new BillTransferDetail(
                            null, rs.getString("reg_no"), rs.getString("patient_name"),
                            null, rs.getString("admission_no"), null, null, null
                    );
                    btd.setDiscount(rs.getDouble("discount"));
                    btd.setPaid(rs.getDouble("paid"));

                    listBTD.add(btd);
                }

                bthdTable.setListBTD(listBTD);
                txtTtlRecord.setValue(listBTD.size());

                if (bth.getDelated()) {
                    butDelete.setEnabled(false);
                    butPrint.setEnabled(false);
                } else {
                    butDelete.setEnabled(true);
                    butPrint.setEnabled(true);
                }
                butClear.setEnabled(true);

            }
        } catch (Exception ex) {
            log.error("assignDetail : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        txtTranDate.setText(null);
        txtTranNo.setText(null);
        txtBillType.setText(null);
        txtCustomer.setText(null);
        txtRemark.setText(null);
        txtTtlAmt.setValue(null);
        txtTtlDisc.setValue(null);
        txtTtlRecord.setValue(null);
        bthdTable.clear();
        butClear.setEnabled(false);
        butDelete.setEnabled(false);
        butPrint.setEnabled(false);
    }

    private void delete(int row) {
        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                "Bill Transfer", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            try {
                Date vouSaleDate = DateUtil.toDate(txtTranDate.getText());
                Date lockDate = PharmacyUtil.getLockDate(dao);
                if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                            + DateUtil.toDateStr(lockDate) + ".",
                            "Locked Data", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BillTransferHis bth = bthTableModel.getData(row);
                if (bth != null) {
                    String id = bth.getBthId();
                    String type = cboType.getSelectedItem().toString();
                    String strSql1 = "update opd_patient_bill_payment set deleted = true \n"
                            + "where remark = '" + id + "@" + type + "'";
                    String strSql2 = "update bill_transfer_his set deleted = true, "
                            + "del_by = '" + Global.loginUser.getUserId() + "',"
                            + "del_time = '" + DateUtil.toDateTimeStrMYSQL(new Date()) + "',"
                            + "del_machine = '" + Global.machineId + "' \n"
                            + "where bth_id = '" + id + "'";
                    dao.execSql(strSql1, strSql2);
                    bthTableModel.deleteRow(row);
                    clear();

                    List<BillTransferHis> listBTH = bthTableModel.getListTBL();
                    double ttlDisc = 0;
                    double ttlPaid = 0;
                    for (BillTransferHis bth1 : listBTH) {
                        ttlDisc += NumberUtil.NZero(bth1.getDiscount());
                        ttlPaid += NumberUtil.NZero(bth1.getTotalAmt());
                    }

                    txtHTtlDisc.setValue(ttlDisc);
                    txtHTtlPaid.setValue(ttlPaid);
                    txtHTtlRecord.setValue(listBTH.size());
                }
            } catch (Exception ex) {
                log.error("delete : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private String getSelType() {
        String selType = cboType.getSelectedItem().toString();
        switch (selType) {
            case "Bill Transfer":
                return "BILLTRANSFER";
            case "Fixed Balance":
                return "FIXBALANCE";
            case "Bill Payment":
                return "BILLPAYMENT";
            default:
                return "";
        }
    }

    private void print(String option) {
        Map<String, Object> params = null;
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path");

        switch (option) {
            case "BILLTRANLIST":
                params = getBillTranListParams();
                reportPath = reportPath + Util1.getPropValue("system.opd.billtranlist");
                break;
            case "LISTDETAIL":
                params = getListDetailParams();
                reportPath = reportPath + Util1.getPropValue("system.opd.billlistdetail");
                break;
            case "PDETAIL":
                if (isDeletedData()) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Deleted data. \n You cannot print.",
                            "Bill Delete", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                params = getPatientDetailParams();
                reportPath = reportPath + Util1.getPropValue("system.opd.billpdetail");
                break;
        }

        if (params != null) {
            try {
                dao.close();
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
                dao.commit();
            } catch (Exception ex) {
                log.error("print : " + ex.getMessage());
            } finally {
                dao.close();
            }
        } else {

        }
    }

    private Map<String, Object> getBillTranListParams() {
        Map<String, Object> params = new HashMap();
        params.put("prm_fDate", DateUtil.toDateStrMYSQL(txtFrom.getText()));
        params.put("prm_tDate", DateUtil.toDateStrMYSQL(txtTo.getText()));
        params.put("data_date",
                "Date Between " + txtFrom.getText() + " and "
                + txtTo.getText()
        );

        if (txtCusCode.getText() != null) {
            if (!txtCusCode.getText().trim().isEmpty()) {
                params.put("prm_cusid", txtCusCode.getText().trim());
                params.put("prm_cus_name",
                        txtCusCode.getText().trim() + "-"
                        + txtCusName.getText());
            } else {
                params.put("prm_cusid", "-");
                params.put("prm_cus_name", "All");
            }
        } else {
            params.put("prm_cusid", "-");
            params.put("prm_cus_name", "All");
        }

        params.put("prm_type_id", getSelType());
        params.put("prm_type_desp", cboType.getSelectedItem().toString());
        params.put("prm_billtype", getSelBillType());
        params.put("prm_currency", getSelCurrency());
        boolean isDeleted = chkDeleted.isSelected();
        params.put("prm_del_status", isDeleted);
        if (isDeleted) {
            params.put("prm_del_desp", "Deleted List");
        } else {
            params.put("prm_del_desp", "");
        }
        return params;
    }

    private Map<String, Object> getListDetailParams() {
        Map<String, Object> params = new HashMap();
        params.put("prm_vouno", txtTranNo.getText().trim());
        boolean isDeleted = chkDeleted.isSelected();
        params.put("prm_del_status", isDeleted);
        if (isDeleted) {
            params.put("prm_del_desp", "Deleted List");
        } else {
            params.put("prm_del_desp", "");
        }
        return params;
    }

    private Map<String, Object> getPatientDetailParams() {
        Map<String, Object> params = new HashMap();
        params.put("prm_reg_no", ui);
        params.put("prm_admission_no", ui);
        params.put("prm_from", ui);
        params.put("prm_to", ui);
        params.put("prm_customer", txtCustomer.getText());
        params.put("prm_vouno", txtTranNo.getText().trim());
        return params;
    }

    private String getSelCurrency() {
        Currency curr = (Currency) cboCurrency.getSelectedItem();
        return curr.getCurrencyCode();
    }

    private int getSelBillType() {
        if (cboBillType.getSelectedItem() instanceof PaymentType) {
            PaymentType pt = (PaymentType) cboBillType.getSelectedItem();
            return pt.getPaymentTypeId();
        } else {
            return -1;
        }
    }

    private boolean isDeletedData() {
        int selRow = tblHistory.getSelectedRow();
        selRow = tblHistory.convertRowIndexToModel(selRow);
        BillTransferHis bth = bthTableModel.getData(selRow);
        return bth.getDelated();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        txtCusCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        cboBillType = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHistory = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtTranDate = new javax.swing.JFormattedTextField();
        txtTranNo = new javax.swing.JFormattedTextField();
        txtBillType = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        txtRemark = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblHDetail = new javax.swing.JTable();
        txtTtlAmt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        butPrint = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtTtlRecord = new javax.swing.JFormattedTextField();
        txtTtlDisc = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        butSearch = new javax.swing.JButton();
        chkDeleted = new javax.swing.JCheckBox();
        cboType = new javax.swing.JComboBox<>();
        txtHTtlPaid = new javax.swing.JFormattedTextField();
        txtHTtlDisc = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtHTtlRecord = new javax.swing.JFormattedTextField();
        butHPrint = new javax.swing.JButton();
        cboCurrency = new javax.swing.JComboBox<>();

        txtFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("From"));
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtTo.setBorder(javax.swing.BorderFactory.createTitledBorder("To"));
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        txtCusCode.setBorder(javax.swing.BorderFactory.createTitledBorder("Cus No."));
        txtCusCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusCodeActionPerformed(evt);
            }
        });

        txtCusName.setBorder(javax.swing.BorderFactory.createTitledBorder("Cus Name"));

        cboBillType.setBorder(javax.swing.BorderFactory.createTitledBorder("Bill Type"));

        tblHistory.setFont(Global.textFont);
        tblHistory.setModel(bthTableModel);
        tblHistory.setRowHeight(23);
        tblHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHistoryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblHistory);

        txtTranDate.setEditable(false);
        txtTranDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));

        txtTranNo.setEditable(false);
        txtTranNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Tran No."));

        txtBillType.setEditable(false);
        txtBillType.setBorder(javax.swing.BorderFactory.createTitledBorder("Bill Type"));

        txtCustomer.setEditable(false);
        txtCustomer.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer"));

        txtRemark.setEditable(false);
        txtRemark.setBorder(javax.swing.BorderFactory.createTitledBorder("Remark"));

        tblHDetail.setFont(Global.textFont);
        tblHDetail.setModel(bthdTable);
        tblHDetail.setRowHeight(23);
        tblHDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHDetailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblHDetail);

        txtTtlAmt.setEditable(false);
        txtTtlAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setText("Total Paid :");

        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        butDelete.setText("Delete");
        butDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                butDeleteMouseClicked(evt);
            }
        });

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jLabel2.setText("Total Record : ");

        txtTtlRecord.setEditable(false);
        txtTtlRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlDisc.setEditable(false);
        txtTtlDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("Total Disc : ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTranNo, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBillType, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRemark))
            .addComponent(jScrollPane2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(butPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTtlAmt, txtTtlDisc});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTranNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBillType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(butPrint)
                    .addComponent(butDelete)
                    .addComponent(butClear)
                    .addComponent(jLabel2)
                    .addComponent(txtTtlRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        chkDeleted.setText("Deleted");

        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bill Transfer", "Fixed Balance", "Bill Payment" }));
        cboType.setBorder(javax.swing.BorderFactory.createTitledBorder("Type"));

        txtHTtlPaid.setEditable(false);
        txtHTtlPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtHTtlDisc.setEditable(false);
        txtHTtlDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText("Total : ");

        jLabel5.setText("Total Record : ");

        txtHTtlRecord.setEditable(false);
        txtHTtlRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        butHPrint.setText("Print");
        butHPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butHPrintActionPerformed(evt);
            }
        });

        cboCurrency.setBorder(javax.swing.BorderFactory.createTitledBorder("Currency"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboType, 0, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDeleted)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHTtlRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butHPrint)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHTtlDisc, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHTtlPaid, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCusCode)
                            .addComponent(cboCurrency, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboBillType, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCusName))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCusCode, txtFrom, txtTo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butSearch)
                        .addComponent(chkDeleted))
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboBillType, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHTtlPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHTtlDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(txtHTtlRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butHPrint))
                .addGap(7, 7, 7))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboBillType, cboCurrency, cboType, txtFrom, txtTo});

    }// </editor-fold>//GEN-END:initComponents

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

    private void txtCusCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusCodeActionPerformed
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtCusCodeActionPerformed

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void tblHistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHistoryMouseClicked
        if (evt.getClickCount() == 2) {
            int selRow = tblHistory.getSelectedRow();
            selRow = tblHistory.convertRowIndexToModel(selRow);
            log.info("Double Click : " + selRow);
            assignDetail(selRow);
        }
    }//GEN-LAST:event_tblHistoryMouseClicked

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_butDeleteMouseClicked
        int selRow = tblHistory.getSelectedRow();
        selRow = tblHistory.convertRowIndexToModel(selRow);
        delete(selRow);
    }//GEN-LAST:event_butDeleteMouseClicked

    private void butHPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butHPrintActionPerformed
        print("BILLTRANLIST");
    }//GEN-LAST:event_butHPrintActionPerformed

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        print("LISTDETAIL");
    }//GEN-LAST:event_butPrintActionPerformed

    private void tblHDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHDetailMouseClicked
        if (evt.getClickCount() == 2) {
            print("PDETAIL");
        }
    }//GEN-LAST:event_tblHDetailMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butHPrint;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboBillType;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JCheckBox chkDeleted;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblHDetail;
    private javax.swing.JTable tblHistory;
    private javax.swing.JTextField txtBillType;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtHTtlDisc;
    private javax.swing.JFormattedTextField txtHTtlPaid;
    private javax.swing.JFormattedTextField txtHTtlRecord;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTranDate;
    private javax.swing.JFormattedTextField txtTranNo;
    private javax.swing.JFormattedTextField txtTtlAmt;
    private javax.swing.JFormattedTextField txtTtlDisc;
    private javax.swing.JFormattedTextField txtTtlRecord;
    // End of variables declaration//GEN-END:variables
}
