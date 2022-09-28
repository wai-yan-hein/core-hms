/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import static com.cv.app.common.Global.dao;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.BTDKey;
import com.cv.app.opd.database.entity.BillTransferDetailHis;
import com.cv.app.opd.database.entity.BillTransferHis;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.helper.BillTransferDetail;
import com.cv.app.opd.ui.common.BillTransferWHOTableModel;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
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
import org.apache.log4j.Logger;

/**
 *
 * @author cv-svr
 */
public class BillTransferWHO extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(BillTransferWHO.class.getName());
    private final int mouseClick = 2;
    private GenVouNoImpl vouEngine = null;
    private Trader cus = null;
    private BillTransferWHOTableModel model = new BillTransferWHOTableModel();

    /**
     * Creates new form BillTransfer
     */
    public BillTransferWHO() {
        initComponents();
        try {
            txtDataFrom.setText(DateUtil.getTodayDateStr());
            txtDataTo.setText(DateUtil.getTodayDateStr());
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
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboBillType, dao.findAllHSQL(
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
        String strSql = "select a.* from (select pt.payment_type_name,a.cus_id, a.trader_name, if(ifnull(admission_no,'')='','OPD','Admission') as pd_status,\n"
                + "GET_AGE(pd.dob) as age,sum(a.vou_total) as ttl_amt, min(tran_date) as tran_date, admission_no \n"
                + "from (\n"
                + "select source, date(tran_date) tran_date, inv_id, cus_id, trader_name,\n"
                + "if(source = 'Return In', (vou_total+ifnull(tax_amt,0)-ifnull(paid,0)-ifnull(discount,0))*-1,vou_total+ifnull(tax_amt,0)-ifnull(paid,0)-ifnull(discount,0)) vou_total, paid, discount, tax_amt,\n"
                + "if(source = 'Return In', balance*-1, balance) balance, payment_type_name,\n"
                + "currency, payment_type\n"
                + "from v_session\n"
                + "where deleted = false and date(tran_date) between $P{from_date} and $P{to_date}\n"
                + "and (payment_type = $P{payment} or $P{payment} = -1)\n"
                + "and source in ('Sale', 'Return In')\n"
                + "and (cus_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "and (bt_id is null) \n"
                + "and (currency = $P{currency_id} or $P{currency_id} = '-') \n"
                + "union all\n"
                + "select tran_option source, date(tran_date) tran_date, opd_inv_id inv_id, patient_id cus_id,\n"
                + "pt_name trader_name, (vou_total+ifnull(tax_a,0)-ifnull(paid,0)-ifnull(disc_a,0)) as vou_total, paid, disc_a discount, tax_a tax_amt, vou_balance balance,\n"
                + "payment_type_name, currency_id currency, payment_type_id payment_type\n"
                + "from v_session_clinic\n"
                + "where deleted = false and date(tran_date) between $P{from_date} and $P{to_date}\n"
                + "and (payment_type_id = $P{payment} or $P{payment} = -1)\n"
                + "and (patient_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "and (bt_id is null) \n"
                + "and (currency_id = $P{currency_id} or $P{currency_id} = '-') \n"
                + ") a\n"
                + "left join patient_detail pd on a.cus_id = pd.reg_no\n"
                + "left join payment_type pt on a.payment_type = pt.payment_type_id\n"
                + "group by pt.payment_type_name,a.cus_id, a.trader_name\n"
                + "order by a.trader_name, a.source, date(a.tran_date)) a order by a.tran_date,a.trader_name";

        Integer billType = ((PaymentType) cboBillType.getSelectedItem()).getPaymentTypeId();
        String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
        strSql = strSql.replace("$P{from_date}", "'" + DateUtil.toDateStrMYSQL(txtDataFrom.getText()) + "'")
                .replace("$P{to_date}", "'" + DateUtil.toDateStrMYSQL(txtDataTo.getText()) + "'")
                .replace("$P{payment}", billType.toString())
                .replace("$P{reg_no}", "'-'")
                .replace("$P{currency_id}", "'" + currency + "'");

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<BillTransferDetail> list = new ArrayList();
                double ttlAmt = 0;
                while (rs.next()) {
                    BillTransferDetail btd = new BillTransferDetail(
                            rs.getDate("tran_date"),
                            rs.getString("cus_id"),
                            rs.getString("trader_name"),
                            rs.getString("pd_status"),
                            rs.getString("admission_no"),
                            rs.getString("age"),
                            rs.getDouble("ttl_amt")
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
        tblTransaction.getColumnModel().getColumn(4).setPreferredWidth(20);//Age
        tblTransaction.getColumnModel().getColumn(5).setPreferredWidth(30);//Amount

        tblTransaction.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    private void clear() {
        txtDataFrom.setText(DateUtil.getTodayDateStr());
        txtDataTo.setText(DateUtil.getTodayDateStr());
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
        
        if(cus == null){
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select customer.",
                                "Customer", JOptionPane.ERROR_MESSAGE);
        }
        
        return status;
    }

    private void save(){
        try{
            Integer billType = ((PaymentType) cboBillType.getSelectedItem()).getPaymentTypeId();
            String billTypeDesp = ((PaymentType) cboBillType.getSelectedItem()).getPaymentTypeName();
            Date createdDate = new Date();
            String vouNo = txtTranVouNo.getText();
            
            int i = 0;
            List<BillTransferDetail> listBTD = model.getListBTD();
            for(BillTransferDetail btd : listBTD){
                /*log.info("Record : " + i);
                if(i == 5){
                    i = 0;
                }*/
                BTDKey key = new BTDKey();
                key.setBthId(txtTranVouNo.getText());
                key.setRegNo(Util1.isNull(btd.getRegNo(), "-"));
                
                BillTransferDetailHis btdh = new BillTransferDetailHis();
                btdh.setKey(key);
                btdh.setAmount(btd.getAmount());
                
                dao.save(btdh);
                
                PatientBillPayment pbp = new PatientBillPayment();
                if (Util1.getNullTo(btd.getAdmissionNo(),"-").equals("-")) {
                    pbp.setAdmissionNo(null);
                    pbp.setPtType("OPD");
                } else {
                    pbp.setAdmissionNo(btd.getAdmissionNo());
                    pbp.setPtType("ADMISSION");
                }
                pbp.setBillTypeDesp(billTypeDesp);
                pbp.setBillTypeId(billType);
                pbp.setCreatedBy(Global.loginUser.getUserId());
                pbp.setCreatedDate(createdDate);
                pbp.setCurrency("MMK");
                pbp.setPayAmt(btd.getAmount());
                pbp.setPayDate(DateUtil.toDate(txtTranDate.getText()));
                pbp.setRegNo(btd.getRegNo());
                pbp.setRemark(vouNo + "@Bill Transfer");
                pbp.setPtType("OPD");
                
                dao.save(pbp);
                i++;
            }
            
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            BillTransferHis bth = new BillTransferHis();
            bth.setCurrency(currency);
            bth.setBillType(billType);
            bth.setBthId(vouNo);
            bth.setCreatedDate(createdDate);
            bth.setDataFrom(DateUtil.toDate(txtDataFrom.getText()));
            bth.setDataTo(DateUtil.toDate(txtDataTo.getText()));
            bth.setMacId(Integer.parseInt(Global.machineId));
            bth.setRemark(txtRemark.getText().trim());
            bth.setTranDate(DateUtil.toDate(txtTranDate.getText()));
            bth.setUserId(Global.loginUser.getUserId());
            bth.setTraderId(cus.getTraderId());
            bth.setTotalAmt(NumberUtil.NZero(txtTotal.getValue()));
            bth.setTranOption("BILLTRANSFER");
            dao.save(bth);
            
            vouEngine.updateVouNo();
            
            updateToTran(vouNo);
        }catch(Exception ex){
            log.error("save : " + ex.getMessage());
        }finally{
            dao.close();
        }
    }
    
    private void updateToTran(String vouNo){
        Integer billType = ((PaymentType) cboBillType.getSelectedItem()).getPaymentTypeId();
        
        String strSqlSale = "update sale_his set bt_id = '" + vouNo 
                + "' where date(sale_date) between '" + DateUtil.toDateStrMYSQL(txtDataFrom.getText()) 
                + "' and '" + DateUtil.toDateStrMYSQL(txtDataTo.getText()) 
                + "' and payment_type_id = " + billType.toString();
        String strSqlRetIn = "update ret_in_his set bt_id = '" + vouNo 
                + "' where date(ret_in_date) between '" + DateUtil.toDateStrMYSQL(txtDataFrom.getText()) 
                + "' and '" + DateUtil.toDateStrMYSQL(txtDataTo.getText()) 
                + "' and payment_type = " + billType.toString();
        String strSqlDC = "update dc_his set bt_id = '" + vouNo 
                + "' where date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtDataFrom.getText()) 
                + "' and '" + DateUtil.toDateStrMYSQL(txtDataTo.getText()) 
                + "' and payment_id = " + billType.toString();
        String strSqlOPD = "update opd_his set bt_id = '" + vouNo 
                + "' where date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtDataFrom.getText()) 
                + "' and '" + DateUtil.toDateStrMYSQL(txtDataTo.getText()) 
                + "' and payment_id = " + billType.toString();
        String strSqlOT = "update ot_his set bt_id = '" + vouNo 
                + "' where date(ot_date) between '" + DateUtil.toDateStrMYSQL(txtDataFrom.getText()) 
                + "' and '" + DateUtil.toDateStrMYSQL(txtDataTo.getText()) 
                + "' and payment_id = " + billType.toString();
        
        try{
            dao.execSql(
                    strSqlSale,
                    strSqlRetIn,
                    strSqlDC,
                    strSqlOPD,
                    strSqlOT
            );
        }catch(Exception ex){
            log.error("updateToTran : " + ex.getMessage());
        }finally{
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
        txtDataTo = new javax.swing.JFormattedTextField();
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
        cboCurrency = new javax.swing.JComboBox<>();

        txtDataFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("From"));
        txtDataFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDataFromMouseClicked(evt);
            }
        });

        txtDataTo.setBorder(javax.swing.BorderFactory.createTitledBorder("To"));
        txtDataTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDataToMouseClicked(evt);
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
        jScrollPane1.setViewportView(tblTransaction);

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setText("Total :");

        jLabel2.setText("Total Records : ");

        txtTotalRecords.setEditable(false);
        txtTotalRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtRemark.setBorder(javax.swing.BorderFactory.createTitledBorder("Remark"));

        cboCurrency.setBorder(javax.swing.BorderFactory.createTitledBorder("Currency"));

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDataFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDataTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBillType, 0, 22, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCustomer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRemark)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butTransfer)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDataFrom, txtDataTo, txtTranDate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboBillType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSearch)
                    .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTranVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butTransfer)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboBillType, cboCurrency, txtCustomer, txtDataFrom, txtDataTo, txtRemark, txtTranDate, txtTranVouNo});

    }// </editor-fold>//GEN-END:initComponents

    private void txtDataFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDataFromMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDataFrom.setText(strDate);
            }

        }
    }//GEN-LAST:event_txtDataFromMouseClicked

    private void txtDataToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDataToMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDataTo.setText(strDate);
            }

        }
    }//GEN-LAST:event_txtDataToMouseClicked

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
        if(txtCustomer.getText().isEmpty()){
            cus = null;
        }
    }//GEN-LAST:event_txtCustomerActionPerformed

    private void txtCustomerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCustomerFocusLost
        if(txtCustomer.getText().isEmpty()){
            cus = null;
        }
    }//GEN-LAST:event_txtCustomerFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butTransfer;
    private javax.swing.JComboBox<String> cboBillType;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTransaction;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JFormattedTextField txtDataFrom;
    private javax.swing.JFormattedTextField txtDataTo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalRecords;
    private javax.swing.JFormattedTextField txtTranDate;
    private javax.swing.JFormattedTextField txtTranVouNo;
    // End of variables declaration//GEN-END:variables
}
