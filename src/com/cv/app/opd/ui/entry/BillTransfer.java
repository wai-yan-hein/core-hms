/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import static com.cv.app.common.Global.dao;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.entity.BTDKey;
import com.cv.app.opd.database.entity.BillTransferDetailHis;
import com.cv.app.opd.database.entity.BillTransferHis;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.helper.BillTransferDetail;
import com.cv.app.opd.ui.common.BillTransferTableModel;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author cv-svr
 */
public class BillTransfer extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(BillTransfer.class.getName());
    private final int mouseClick = 2;
    private GenVouNoImpl vouEngine = null;
    private Trader cus = null;
    private BillTransferTableModel model = new BillTransferTableModel();
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;

    /**
     * Creates new form BillTransfer
     */
    public BillTransfer() {
        initComponents();

        try {
            txtDataFrom.setText(DateUtil.getTodayDateStr());
            txtTranDate.setText(DateUtil.getTodayDateStr());
            initCombo();
            txtTranVouNo.setFormatterFactory(new VouFormatFactory());
            vouEngine = new GenVouNoImpl(dao, "WHOBillTransfer", DateUtil.getPeriod(txtTranDate.getText()));
            genVouNo();
            initTable();
        } catch (Exception ex) {
            log.error("BillTransfer : " + ex.getMessage());
        } finally {
            dao.close();
        }

        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblTransaction.getModel());
        tblTransaction.setRowSorter(sorter);
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

    private void genVouNo() {
        try {
            String vouNo = vouEngine.getVouNo();
            txtTranVouNo.setText(vouNo);
            if (vouNo.equals("-")) {
                log.error("Voucher error : " + txtTranVouNo.getText() + " @ "
                        + txtTranDate.getText() + " @ " + vouEngine.getVouInfo());
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "Vou no error. Exit the program and try again.",
                        "Vou No", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (Exception ex) {
            log.error("genVouNo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getCustomerList() {
        int locationId = -1;
        TraderSearchDialog dialog = new TraderSearchDialog(this,
                "Customer List", locationId);
        dialog.setTitle("Customer List");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                cus = (Trader) selectObj;
                String cusId = cus.getTraderId();
                String cusName = cus.getTraderName();
                txtCustomer.setText(cusId + " - " + cusName);
                break;
        }
    }

    private void search() {
        String strSql = "select b.*,pd.patient_name, pt.payment_type_name\n"
                + "  from (select if(ifnull(reg_no,'')='','-',reg_no) as reg_no, if(ifnull(admission_no,'')='','-',admission_no) as admission_no, currency_id,pay_type, round(sum(amt) ,0)amt\n"
                + "		  from (select reg_no, ifnull(admission_no,'') as admission_no, currency_id,payment_type_id as pay_type,sum(balance) amt\n"
                + "				  from sale_his \n"
                + "				 where deleted = 0 \n"
                + "			           and date(sale_date)<= $P{to_date} \n"
                + "				   and balance <> 0\n"
                + "				   and (ifnull(reg_no,'-') = $P{reg_no}   or '-' =  $P{reg_no})\n"
                + "                                and (payment_type_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by reg_no,ifnull(admission_no,''), currency_id, payment_type_id\n"
                + "				 union all\n"
                + "				select reg_no, ifnull(admission_no,'') as admission_no, currency, payment_type as pay_type, sum(balance)*-1 amt\n"
                + "				  from ret_in_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(ret_in_date)<=$P{to_date} \n"
                + "				   and balance <> 0\n"
                + "				   and (ifnull(reg_no,'-') =  $P{reg_no} or '-' =  $P{reg_no})\n"
                + "                                and (payment_type = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency = $P{currency_id}) \n"
                + "				 group by reg_no,ifnull(admission_no,''), currency,payment_type\n"
                + "				 union all\n"
                + "				select patient_id, ifnull(admission_no,'') as admission_no, currency_id, payment_id as pay_type, sum(vou_balance) amt\n"
                + "				  from opd_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(opd_date)<=$P{to_date} \n"
                + "				   and vou_balance <>0\n"
                + "				   and (ifnull(patient_id,'-') =  $P{reg_no} or'-' = $P{reg_no})\n"
                + "                                and (payment_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by patient_id,ifnull(admission_no,''), currency_id,payment_id\n"
                + "				 union all\n"
                + "				select patient_id, ifnull(admission_no,'') as admission_no, currency_id, payment_id as pay_type, sum(vou_total) amt\n"
                + "				  from ot_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(ot_date)<=$P{to_date} \n"
                + "				   and (ifnull(patient_id,'-') =  $P{reg_no} or '-' = $P{reg_no})\n"
                + "                                and (payment_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by patient_id,ifnull(admission_no,''), currency_id, payment_id\n"
                + "				 union all\n"
                + "				select patient_id, ifnull(admission_no,'') as admission_no, currency_id, payment_id as pay_type, sum(paid)*-1 amt\n"
                + "				  from ot_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(ot_date)<=$P{to_date} \n"
                + "				   and (ifnull(patient_id,'-') =  $P{reg_no} or '-' = $P{reg_no})\n"
                + "                                and (payment_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by patient_id,ifnull(admission_no,''), currency_id, payment_id\n"
                + "				 union all\n"
                + "				select patient_id, ifnull(admission_no,'') as admission_no, currency_id, payment_id as pay_type, sum(disc_a)*-1 amt\n"
                + "				  from ot_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(ot_date)<=$P{to_date} \n"
                + "				   and (ifnull(patient_id,'-') =  $P{reg_no} or '-' = $P{reg_no})\n"
                + "                                and (payment_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by patient_id,ifnull(admission_no,''), currency_id, payment_id\n"
                + "				 union all\n"
                + "				select patient_id, ifnull(admission_no,'') as admission_no, currency_id, payment_id as pay_type, sum(vou_total) amt\n"
                + "			      from dc_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(dc_date)<=$P{to_date} \n"
                + "				   and (ifnull(patient_id,'-') =  $P{reg_no} or '-' = $P{reg_no})\n"
                + "                                and (payment_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by patient_id,ifnull(admission_no,''), currency_id,payment_id\n"
                + "				 union all\n"
                + "				select patient_id, ifnull(admission_no,'') as admission_no, currency_id, payment_id as pay_type, sum(disc_a)*-1 amt\n"
                + "				  from dc_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(dc_date)<=$P{to_date} \n"
                + "				   and (ifnull(patient_id,'-') =  $P{reg_no} or '-' = $P{reg_no})\n"
                + "                                and (payment_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by patient_id,ifnull(admission_no,''), currency_id,payment_id\n"
                + "				 union all \n"
                + "				select patient_id, ifnull(admission_no,'') as admission_no, currency_id, payment_id as pay_type, sum(paid)*-1 amt\n"
                + "				  from dc_his \n"
                + "				 where deleted = 0 \n"
                + "				   and date(dc_date)<=$P{to_date} \n"
                + "				   and (ifnull(patient_id,'-') =  $P{reg_no} or '-' = $P{reg_no})\n"
                + "                                and (payment_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by patient_id,ifnull(admission_no,''), currency_id, payment_id\n"
                + "				 union all\n"
                + "				select reg_no, ifnull(admission_no,'') as admission_no, currency_id, bill_type_id as pay_type, sum(pay_amt)*-1 amt\n"
                + "				  from opd_patient_bill_payment \n"
                + "				 where date(pay_date)<=$P{to_date} and deleted = false \n"
                + "				   and (ifnull(reg_no,'-') =  $P{reg_no} or '-'= $P{reg_no})\n"
                + "                                and (bill_type_id = $P{payment} or $P{payment} = 0) \n"
                + "				   and (currency_id = $P{currency_id}) \n"
                + "				 group by reg_no,ifnull(admission_no,''), currency_id,bill_type_id\n"
                + "			)a\n"
                + "			where amt <> 0\n"
                + "		group by reg_no,admission_no, currency_id,pay_type)b\n"
                + "		left join patient_detail pd on b.reg_no = pd.reg_no\n"
                + "		left join admission ad on pd.reg_no = ad.reg_no\n"
                + "        left join payment_type pt on b.pay_type = pt.payment_type_id\n"
                + "		where (b.amt >0 or b.amt <0)\n"
                + "	    group by b.reg_no,b.admission_no, b.currency_id, b.pay_type, pt.payment_type_name\n"
                + "		order by pd.patient_name";

        Integer billType = 0;
        if (cboBillType.getSelectedItem() instanceof PaymentType) {
            billType = ((PaymentType) cboBillType.getSelectedItem()).getPaymentTypeId();
        }
        String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
        strSql = strSql.replace("$P{to_date}", "'" + DateUtil.toDateStrMYSQL(txtDataFrom.getText()) + "'")
                .replace("$P{payment}", billType.toString())
                .replace("$P{reg_no}", "'-'")
                .replace("$P{currency_id}", "'" + currency + "'");

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<BillTransferDetail> list = new ArrayList();
                double ttlAmt = 0;
                while (rs.next()) {
                    String pdStatus = "OPD";
                    if (!rs.getString("admission_no").equals("-")) {
                        pdStatus = "ADMISSION";
                    }
                    String regNo = rs.getString("reg_no");
                    if (regNo.equals("-")) {
                        //regNo = "@" + rs.getString("pay_type");
                        regNo = null;
                    }
                    BillTransferDetail btd = new BillTransferDetail(
                            DateUtil.toDate(txtDataFrom.getText()),
                            regNo,
                            rs.getString("patient_name"),
                            pdStatus,
                            rs.getString("admission_no"),
                            rs.getString("payment_type_name"),
                            rs.getDouble("amt"),
                            rs.getInt("pay_type")
                    );

                    list.add(btd);
                    ttlAmt += NumberUtil.NZero(btd.getAmount());
                }

                if (!list.isEmpty()) {
                    model.setListBTD(list);
                    txtTotalRecords.setValue(list.size());
                    txtTotal.setValue(ttlAmt);
                } else {
                    model.clear();
                    txtTotalRecords.setValue(0);
                    txtTotal.setValue(0d);
                }
            }
        } catch (Exception ex) {
            log.error("search : " + strSql);
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblTransaction.getColumnModel().getColumn(0).setPreferredWidth(30);//Date
        tblTransaction.getColumnModel().getColumn(1).setPreferredWidth(20);//Reg No.
        tblTransaction.getColumnModel().getColumn(2).setPreferredWidth(20);//Admission No.
        tblTransaction.getColumnModel().getColumn(3).setPreferredWidth(400);//Name
        tblTransaction.getColumnModel().getColumn(4).setPreferredWidth(200);//Bill type
        tblTransaction.getColumnModel().getColumn(5).setPreferredWidth(30);//Amount

        tblTransaction.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    private void clear() {
        txtDataFrom.setText(DateUtil.getTodayDateStr());
        txtTranDate.setText(DateUtil.getTodayDateStr());
        txtCustomer.setText(null);
        txtRemark.setText(null);
        genVouNo();
        model.clear();
        txtTotalRecords.setValue(0);
        txtTotal.setValue(0d);
        cus = null;
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (cus == null && !chkFixbalance.isSelected()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select customer.",
                    "Customer", JOptionPane.ERROR_MESSAGE);
        } else if (!(cboBillType.getSelectedItem() instanceof PaymentType)) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select bill type.",
                    "Bill Type", JOptionPane.ERROR_MESSAGE);
        }

        return status;
    }

    private void save() {
        try {
            Date vouSaleDate = DateUtil.toDate(txtTranDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer billType = ((PaymentType) cboBillType.getSelectedItem()).getPaymentTypeId();
            String billTypeDesp = ((PaymentType) cboBillType.getSelectedItem()).getPaymentTypeName();
            Date createdDate = new Date();
            String vouNo = txtTranVouNo.getText();

            List<BillTransferDetail> listBTD = model.getListBTD();
            for (BillTransferDetail btd : listBTD) {
                BTDKey key = new BTDKey();
                key.setBthId(txtTranVouNo.getText());
                key.setRegNo(Util1.isNull(btd.getRegNo(), "-"));

                BillTransferDetailHis btdh = new BillTransferDetailHis();
                btdh.setKey(key);
                btdh.setAmount(btd.getAmount());

                dao.save(btdh);

                PatientBillPayment pbp = new PatientBillPayment();
                if (Util1.getNullTo(btd.getAdmissionNo(), "-").equals("-")) {
                    pbp.setAdmissionNo(null);
                    pbp.setPtType("OPD");
                } else {
                    pbp.setAdmissionNo(btd.getAdmissionNo());
                    pbp.setPtType("ADMISSION");
                }
                
                pbp.setDelete(Boolean.FALSE);
                pbp.setBillTypeDesp(billTypeDesp);
                pbp.setBillTypeId(btd.getPayTypeId());
                pbp.setCreatedBy(Global.loginUser.getUserId());
                pbp.setCreatedDate(createdDate);
                pbp.setCurrency("MMK");
                pbp.setPayAmt(btd.getAmount());
                pbp.setPayDate(DateUtil.toDate(txtTranDate.getText()));
                pbp.setRegNo(btd.getRegNo());
                
                if (chkFixbalance.isSelected()) {
                    pbp.setRemark(vouNo + "@Fix Balance");
                } else {
                    pbp.setRemark(vouNo + "@Bill Transfer");
                }

                dao.save(pbp);
            }

            BillTransferHis bth = new BillTransferHis();
            bth.setBillType(billType);
            bth.setBthId(vouNo);
            bth.setCreatedDate(createdDate);
            bth.setDataFrom(DateUtil.toDate(txtDataFrom.getText()));
            //bth.setDataTo(DateUtil.toDate(txtDataTo.getText()));
            bth.setMacId(Integer.parseInt(Global.machineId));
            bth.setRemark(txtRemark.getText());
            bth.setTranDate(DateUtil.toDate(txtTranDate.getText()));
            bth.setUserId(Global.loginUser.getUserId());
            bth.setTotalAmt(NumberUtil.NZero(txtTotal.getValue()));
            if (chkFixbalance.isSelected()) {
                bth.setTranOption("FIXBALANCE");
            } else {
                bth.setTranOption("BILLTRANSFER");
                bth.setTraderId(cus.getTraderId());
            }
            dao.save(bth);

            vouEngine.updateVouNo();

            genVouNo();
            search();
        } catch (Exception ex) {
            log.error("save : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void save(BillTransferDetail btd) {
        try {

            Date vouSaleDate = DateUtil.toDate(txtTranDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer billType = btd.getPayTypeId();
            String billTypeDesp = btd.getStrAge();
            Date createdDate = new Date();
            String vouNo = txtTranVouNo.getText();

            BTDKey key = new BTDKey();
            key.setBthId(txtTranVouNo.getText());
            key.setRegNo(Util1.isNull(btd.getRegNo(), "-"));

            BillTransferDetailHis btdh = new BillTransferDetailHis();
            btdh.setKey(key);
            btdh.setAmount(btd.getAmount());

            dao.save(btdh);

            PatientBillPayment pbp = new PatientBillPayment();
            if (Util1.getNullTo(btd.getAdmissionNo(), "-").equals("-")) {
                pbp.setAdmissionNo(null);
                pbp.setPtType("OPD");
            } else {
                pbp.setAdmissionNo(btd.getAdmissionNo());
                pbp.setPtType("ADMISSION");
            }
            
            pbp.setDelete(Boolean.FALSE);
            pbp.setBillTypeDesp(billTypeDesp);
            pbp.setBillTypeId(billType);
            pbp.setCreatedBy(Global.loginUser.getUserId());
            pbp.setCreatedDate(createdDate);
            pbp.setCurrency("MMK");
            pbp.setPayAmt(btd.getAmount());
            pbp.setPayDate(DateUtil.toDate(txtTranDate.getText()));
            pbp.setRegNo(btd.getRegNo());
            if (chkFixbalance.isSelected()) {
                pbp.setRemark(vouNo + "@Fix Balance");
            } else {
                pbp.setRemark(vouNo + "@Bill Transfer");
            }

            dao.save(pbp);

            BillTransferHis bth = new BillTransferHis();
            bth.setBillType(billType);
            bth.setBthId(vouNo);
            bth.setCreatedDate(createdDate);
            bth.setDataFrom(DateUtil.toDate(txtDataFrom.getText()));
            //bth.setDataTo(DateUtil.toDate(txtDataTo.getText()));
            bth.setMacId(Integer.parseInt(Global.machineId));
            bth.setRemark(txtRemark.getText());
            bth.setTranDate(DateUtil.toDate(txtTranDate.getText()));
            bth.setUserId(Global.loginUser.getUserId());
            bth.setTotalAmt(NumberUtil.NZero(txtTotal.getValue()));
            if (chkFixbalance.isSelected()) {
                bth.setTranOption("FIXBALANCE");
            } else {
                bth.setTranOption("BILLTRANSFER");
                bth.setTraderId(cus.getTraderId());
            }
            dao.save(bth);

            vouEngine.updateVouNo();

            genVouNo();
            search();
        } catch (Exception ex) {
            log.error("save1 : " + ex.getMessage());
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

        txtDataFrom = new javax.swing.JFormattedTextField();
        cboBillType = new javax.swing.JComboBox<>();
        butSearch = new javax.swing.JButton();
        txtTranDate = new javax.swing.JFormattedTextField();
        txtTranVouNo = new javax.swing.JFormattedTextField();
        butTransfer = new javax.swing.JButton();
        txtCustomer = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransaction = new javax.swing.JTable();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtTotalRecords = new javax.swing.JFormattedTextField();
        txtRemark = new javax.swing.JTextField();
        chkFixbalance = new javax.swing.JCheckBox();
        cboCurrency = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();

        txtDataFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("Balance Date"));
        txtDataFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDataFromMouseClicked(evt);
            }
        });

        cboBillType.setFont(Global.textFont);
        cboBillType.setBorder(javax.swing.BorderFactory.createTitledBorder("Bill Type"));

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        txtTranDate.setEditable(false);
        txtTranDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Tran Date"));
        txtTranDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTranDateMouseClicked(evt);
            }
        });

        txtTranVouNo.setEditable(false);
        txtTranVouNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Tran No."));

        butTransfer.setText("Transfer");
        butTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTransferActionPerformed(evt);
            }
        });

        txtCustomer.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer"));
        txtCustomer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCustomerFocusLost(evt);
            }
        });
        txtCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCustomerMouseClicked(evt);
            }
        });
        txtCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerActionPerformed(evt);
            }
        });

        tblTransaction.setFont(Global.textFont);
        tblTransaction.setModel(model);
        tblTransaction.setRowHeight(23);
        tblTransaction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTransactionMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTransaction);

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setText("Total :");

        jLabel2.setText("Total Records : ");

        txtTotalRecords.setEditable(false);
        txtTotalRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtRemark.setBorder(javax.swing.BorderFactory.createTitledBorder("Remark"));

        chkFixbalance.setText("Fix Balance");
        chkFixbalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFixbalanceActionPerformed(evt);
            }
        });

        cboCurrency.setBorder(javax.swing.BorderFactory.createTitledBorder("Currency"));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Filter : ");

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(2, 2, 2)
                        .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDataFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBillType, 0, 41, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCustomer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 12, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFixbalance, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butTransfer)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDataFrom, txtTranDate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDataFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboBillType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butSearch)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTranVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butTransfer)
                        .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkFixbalance))
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboBillType, cboCurrency, txtCustomer, txtDataFrom, txtRemark, txtTranDate, txtTranVouNo});

    }// </editor-fold>//GEN-END:initComponents

    private void txtDataFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDataFromMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDataFrom.setText(strDate);
            }

        }
    }//GEN-LAST:event_txtDataFromMouseClicked

    private void txtTranDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTranDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTranDate.setText(strDate);
                vouEngine.setPeriod(DateUtil.getPeriod(txtTranDate.getText()));
                genVouNo();
            }

        }
    }//GEN-LAST:event_txtTranDateMouseClicked

    private void txtCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCustomerMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCustomerMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void butTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTransferActionPerformed
        if (isValidEntry()) {
            save();
            clear();
        }
    }//GEN-LAST:event_butTransferActionPerformed

    private void txtCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerActionPerformed
        if (txtCustomer.getText().isEmpty()) {
            cus = null;
        }
    }//GEN-LAST:event_txtCustomerActionPerformed

    private void txtCustomerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCustomerFocusLost
        if (txtCustomer.getText().isEmpty()) {
            cus = null;
        }
    }//GEN-LAST:event_txtCustomerFocusLost

    private void chkFixbalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFixbalanceActionPerformed
        if (chkFixbalance.isSelected()) {
            butTransfer.setText("Fixed");
        } else {
            butTransfer.setText("Transfer");
        }
    }//GEN-LAST:event_chkFixbalanceActionPerformed

    private void tblTransactionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTransactionMouseClicked
        if (evt.getClickCount() == 2) {
            if (cus == null && !chkFixbalance.isSelected()) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Please select customer.",
                        "Customer", JOptionPane.ERROR_MESSAGE);
            } else {
                int selectIndex = tblTransaction.getSelectedRow();
                selectIndex = tblTransaction.convertRowIndexToModel(selectIndex);
                BillTransferDetail btd = model.getSelectedData(selectIndex);
                save(btd);
            }
        }
    }//GEN-LAST:event_tblTransactionMouseClicked

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorter.setRowFilter(swrf);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
        }
    }//GEN-LAST:event_txtFilterKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butTransfer;
    private javax.swing.JComboBox<String> cboBillType;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JCheckBox chkFixbalance;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTransaction;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JFormattedTextField txtDataFrom;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalRecords;
    private javax.swing.JFormattedTextField txtTranDate;
    private javax.swing.JFormattedTextField txtTranVouNo;
    // End of variables declaration//GEN-END:variables
}
