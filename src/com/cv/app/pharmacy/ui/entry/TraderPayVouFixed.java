/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.BusinessType;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.entity.TraderType;
import com.cv.app.pharmacy.database.helper.TraderBalanceFixList;
import com.cv.app.pharmacy.database.tempentity.TmpVouAmtFix;
import com.cv.app.pharmacy.ui.common.LastTraderOPModel;
import com.cv.app.pharmacy.ui.common.PayVouFixedModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class TraderPayVouFixed extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(TraderPayVouFixed.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final LastTraderOPModel tblTraderModel = new LastTraderOPModel();
    private final PayVouFixedModel tblUnPaidVouModel = new PayVouFixedModel();
    private final TableRowSorter<TableModel> tblTraderSorter;
    private int selectedRow = -1;
    private boolean bindStatus = false;
    private int mouseClick = 2;
    
    /**
     * Creates new form TraderPayVouFixed
     */
    public TraderPayVouFixed() {
        initComponents();
        initCombo();
        initTblTrader();
        tblTraderSorter = new TableRowSorter(tblTrader.getModel());
        tblTrader.setRowSorter(tblTraderSorter);
        
        String propValue = Util1.getPropValue("system.date.mouse.click");
        if(propValue != null){
            if(!propValue.equals("-")){
                if(!propValue.isEmpty()){
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if(tmpValue != 0){
                        mouseClick = tmpValue;
                    }
                }
            }
        }
    }

    private void initTblTrader() {
        tblTrader.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblTrader.getColumnModel().getColumn(1).setPreferredWidth(200);

        //search();
        //Define table selection model to single row selection.
        tblTrader.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblTrader.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblTrader.getSelectedRow() >= 0) {
                    selectedRow = tblTrader.convertRowIndexToModel(
                            tblTrader.getSelectedRow());

                    try {
                        TraderBalanceFixList tbfl = tblTraderModel.getTrader(selectedRow);
                        List<TmpVouAmtFix> listTBFL = dao.findAllHSQL(
                                "select o from TmpVouAmtFix o where o.key.cusId = '"
                                + tbfl.getTraderId() + "' and o.key.userId = '"
                                + Global.loginUser.getUserId() + "'");
                        tblUnPaidVouModel.setList(listTBFL);
                    } catch (Exception ex) {
                        log.error("valueChange : " + ex.toString());
                    } finally {
                        dao.close();
                    }
                }
            }
        });

    }

    private void getUnPaidVou(String traderId) {
        String strTrdOpt = "CUS";
        String strCurrency = Util1.getPropValue("system.app.currency");
        String strDate = DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(DateUtil.getTodayDateStr()));
        String strOpDate = "";
        List<PaymentVou> listVou = new ArrayList();

        try {
            ResultSet rsInfo = dao.getPro("GET_PAYMENT_INFO", traderId, strTrdOpt,
                    DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(DateUtil.getTodayDateStr())),
                    strCurrency);
            if (rsInfo != null) {
                if (rsInfo.next()) {
                    strOpDate = rsInfo.getString("VAR_OP_DATE");
                }
            }

            ResultSet resultSet = dao.getPro("GET_UNPAID_VOU", traderId,
                    strTrdOpt, strOpDate, strDate, strCurrency, "1900-01-01", "1900-01-01");
            if (resultSet != null) {
                while (resultSet.next()) {
                    double balance = NumberUtil.NZero(resultSet.getDouble("BAL"));
                    if (balance != 0.0) {
                        listVou.add(new PaymentVou(resultSet.getString("VOU_NO"),
                                resultSet.getDouble("BAL"), resultSet.getString("VOU_TYPE"),
                                resultSet.getDate("tran_date"),
                                resultSet.getString("ref_no"),
                                resultSet.getDouble("BAL")));
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("getUnPaidVou : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void initCombo() {
        bindStatus = true;
        BindingUtil.BindComboFilter(cboCusGroup, dao.findAllHSQL(
                "select o from CustomerGroup o order by o.groupName"));
        BindingUtil.BindComboFilter(cboPriceType, dao.findAllHSQL(
                "select o from TraderType o"));
        BindingUtil.BindComboFilter(cboTownship, dao.findAllHSQL(
                "select o from Township o order by o.townshipName"));
        BindingUtil.BindComboFilter(cboBusinessType, dao.findAllHSQL(
                "select o from BusinessType o order by o.description"));
        String strBaseGroup = Util1.getPropValue("system.cus.base.group");
        if (strBaseGroup.isEmpty()) {
            strBaseGroup = "-";
        }
        BindingUtil.BindComboFilter(cboParent, dao.findAllHSQL(
                "select o from Customer o where o.traderGroup.groupId = '"
                + strBaseGroup + "' order by o.traderName"));
        bindStatus = false;
    }

    private void insertFilter() {
        String strSQLDelete = "delete from tmp_trader_bal_filter where user_id = '"
                + Global.loginUser.getUserId() + "'";
        String strSQL = "insert into tmp_trader_bal_filter(trader_id,currency,op_date,user_id, amount) "
                + "select t.trader_id, t.cur_code, "
                + " ifnull(trop.op_date, '1900-01-01'),'" + Global.loginUser.getUserId()
                + "', ifnull(trop.op_amount,0)"
                + " from v_trader_cur t left join "
                + "(select trader_id, currency, max(op_date) op_date, op_amount from trader_op "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()) + "'";

        strSQL = strSQL + " group by trader_id, currency) trop on t.trader_id = trop.trader_id "
                + " and t.cur_code = trop.currency ";
        String strFilter = "";

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            String cusGroup = ((CustomerGroup) cboCusGroup.getSelectedItem()).getGroupId();
            if (strFilter.isEmpty()) {
                strFilter = "t.group_id = '" + cusGroup + "'";
            } else {
                strFilter = strFilter + " and t.group_id = '" + cusGroup + "'";
            }
        }

        if (cboPriceType.getSelectedItem() instanceof TraderType) {
            Integer traderType = ((TraderType) cboPriceType.getSelectedItem()).getTypeId();
            if (strFilter.isEmpty()) {
                strFilter = "t.type_id = " + traderType;
            } else {
                strFilter = strFilter + " and t.type_id = " + traderType;
            }
        }

        if (cboTownship.getSelectedItem() instanceof Township) {
            Integer tspId = ((Township) cboTownship.getSelectedItem()).getTownshipId();
            if (strFilter.isEmpty()) {
                strFilter = "t.township = " + tspId;
            } else {
                strFilter = strFilter + " and t.township = " + tspId;
            }
        }

        if (cboBusinessType.getSelectedItem() instanceof BusinessType) {
            Integer id = ((BusinessType) cboBusinessType.getSelectedItem()).getBusinessId();
            if (strFilter.isEmpty()) {
                strFilter = "t.business_id = " + id;
            } else {
                strFilter = strFilter + " and t.business_id = " + id;
            }
        }

        if (cboParent.getSelectedItem() instanceof Customer) {
            String id = ((Customer) cboParent.getSelectedItem()).getTraderId();
            if (strFilter.isEmpty()) {
                strFilter = "t.parent = '" + id + "'";
            } else {
                strFilter = strFilter + " and t.parent = '" + id + "'";
            }
        }

        String appCurr = Util1.getPropValue("system.app.currency");
        if (strFilter.isEmpty()) {
            strFilter = "t.cur_code = '" + appCurr + "'";
        } else {
            strFilter = strFilter + " and t.cur_code = '" + appCurr + "'";
        }

        if (strFilter.isEmpty()) {
            strFilter = "t.active = true";
        } else {
            strFilter = strFilter + " and t.active = true";
        }

        if (!strFilter.isEmpty()) {
            strSQL = strSQL + " where " + strFilter;
        }

        try {
            dao.open();
            dao.execSql(strSQLDelete);
            dao.close();

            dao.open();
            dao.execSql(strSQL);
        } catch (Exception ex) {
            log.error("initTableCus : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void checkError() {
        insertFilter();
        try {
            dao.execProc("trader_balance_date_chk",
                    DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()),
                    Global.loginUser.getUserId());
        } catch (Exception ex) {
            log.error("checkError trader_balance_date : " + ex.toString());
        } finally {
            dao.close();
        }

        String strSqlDelete = "delete from tmp_vou_amt_fix where user_id = '" + Global.loginUser.getUserId() + "'";
        String strSqlOP = "insert into tmp_vou_amt_fix(sale_date, vou_no, cus_id, vou_type, due_date, ref_no, vou_total, balance, user_id)\n"
                + "select date(balv.sale_date), balv.vou_no, balv.cus_id, balv.vou_type, date(balv.due_date), balv.ref_no, balv.vou_total, \n"
                + "balv.bal,'" + Global.loginUser.getUserId() + "'\n"
                + "from (\n"
                + "select a.*, if(a.ttl_overdue<0,0,a.ttl_overdue) as ttl_overdue1 from (\n"
                + "select vob.tran_date sale_date, vob.vou_no, vob.trader_id cus_id, \n"
                + "		   t.trader_name, vob.vou_type, date_add(vob.tran_date, interval ifnull(t.credit_days,0) day) due_date,\n"
                + "		   'Opening' ref_no, vob.vou_total, vob.paid_amount ttl_paid, vob.discount, vob.balance,\n"
                + "		   vob.bal, if(date_add(vob.tran_date, interval ifnull(t.credit_days,0) day)='-',0,\n"
                + "		   if(DATEDIFF(sysdate(),date_add(vob.tran_date, interval ifnull(t.credit_days,0) day))<0,0,\n"
                + "		   DATEDIFF(sysdate(),date_add(vob.tran_date, interval ifnull(t.credit_days,0) day)))) ttl_overdue\n"
                + "	  from v_opening_balance vob\n"
                + "	  join trader t on vob.trader_id = t.trader_id\n"
                + "	  left join customer_group cg on t.group_id = cg.group_id\n"
                + "	 where bal > 0 and vob.trader_id in (select  distinct trader_id from tmp_trader_bal_filter where user_id = '" + Global.loginUser.getUserId() + "')) a\n"
                + "where a.bal > 0 order by a.ttl_overdue desc, a.sale_date, a.vou_no) balv";
        String strSale = "insert into tmp_vou_amt_fix(sale_date, vou_no, cus_id, vou_type, due_date, ref_no, vou_total, balance, user_id)\n"
                + "select date(balv.sale_date), balv.vou_no, balv.cus_id, balv.vou_type, date(balv.due_date), balv.ref_no, balv.vou_total, \n"
                + "balv.bal,'" + Global.loginUser.getUserId() + "'\n"
                + "from (\n"
                + "select a.*, if(a.ttl_overdue<0,0,a.ttl_overdue) as ttl_overdue1 from (\n"
                + "select sh.sale_date, sale_inv_id vou_no, sh.cus_id, t.trader_name, 'SALE' vou_type,\n"
                + "       sh.due_date, sh.remark ref_no, sh.vou_total, (sh.paid_amount+ifnull(pah.pay_amt,0)) as ttl_paid, sh.discount, sh.balance,\n"
                + "	   sum(ifnull(balance,0))-(ifnull(pah.pay_amt,0)) bal, \n"
                + "	   if(ifnull(sh.due_date,'-')='-',0,if(DATEDIFF(sysdate(),sh.due_date)<0,0,DATEDIFF(sysdate(),sh.due_date))) as ttl_overdue \n"
                + "from sale_his sh\n"
                + "left join trader t on sh.cus_id = t.trader_id\n"
                + "left join (select pv.vou_no, sum(ifnull(pv.vou_paid,0)+ifnull(pv.discount,0)) pay_amt, pv.vou_type\n"
                + "			   from payment_his ph, pay_his_join phj, payment_vou pv\n"
                + "			  where ph.payment_id = phj.payment_id and phj.tran_id = pv.tran_id\n"
                + "				and ph.deleted = false\n"
                + "				and pv.vou_type = 'SALE'\n"
                + "			  group by pv.vou_no, pv.vou_type) pah on sh.sale_inv_id = pah.vou_no\n"
                + "where sh.deleted = false and sh.cus_id in (select  distinct trader_id from tmp_trader_bal_filter where user_id = '" + Global.loginUser.getUserId() + "')\n"
                + "group by sh.sale_inv_id, sh.sale_date,sh.vou_total, sh.paid_amount, sh.discount, sh.balance) a\n"
                + "where a.bal > 0 order by a.ttl_overdue desc, a.sale_date, a.vou_no) balv";
        String strUpdate = "update tmp_trader_bal_date bd, (\n"
                + "select cus_id, sum(balance) balance\n"
                + "from tmp_vou_amt_fix\n"
                + "where user_id = '" + Global.loginUser.getUserId() + "'\n"
                + "group by cus_id) vb\n"
                + "set vou_ttl_amt = vb.balance\n"
                + "where bd.user_id = '" + Global.loginUser.getUserId() + "' and bd.trader_id = vb.cus_id;";
        String strSqlData = "select bd.trader_id, t.trader_name, round(bd.amount,0) amount, round(bd.vou_ttl_amt,0) vou_ttl_amt\n"
                + "from tmp_trader_bal_date bd, trader t\n"
                + "where bd.trader_id = t.trader_id and user_id = '" + Global.loginUser.getUserId() + "'\n"
                + "and round(bd.amount,0)<>round(bd.vou_ttl_amt,0)";
        try {
            dao.execSql(strSqlDelete, strSqlOP, strSale, strUpdate);
            ResultSet rs = dao.execSQL(strSqlData);
            if (rs != null) {
                List<TraderBalanceFixList> listTB = new ArrayList();
                while (rs.next()) {
                    TraderBalanceFixList tb = new TraderBalanceFixList();
                    tb.setTraderId(rs.getString("trader_id"));
                    tb.setTraderName(rs.getString("trader_name"));
                    tb.setTraderBalance(rs.getDouble("amount"));
                    tb.setVouBalance(rs.getDouble("vou_ttl_amt"));

                    listTB.add(tb);
                }
                tblTraderModel.setListTrader(listTB);
            }
        } catch (Exception ex) {
            log.error("checkError get voucher balance : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void fixError() {
        List<TraderBalanceFixList> list = tblTraderModel.getListTrader();
        if (list != null) {
            try {
                for (TraderBalanceFixList tbfl : list) {
                    List<TmpVouAmtFix> listTBFL = dao.findAllHSQL(
                            "select o from TmpVouAmtFix o where o.key.cusId = '"
                            + tbfl.getTraderId() + "' and o.key.userId = '"
                            + Global.loginUser.getUserId() + "'");
                    if (listTBFL != null) {
                        if (!listTBFL.isEmpty()) {
                            Double difference = tbfl.getDifference();
                            if (difference > 0) {
                                Double leftAmt = difference;
                                for (TmpVouAmtFix tvaf : listTBFL) {
                                    if (leftAmt > 0) {
                                        if (tvaf.getBalance() >= leftAmt) {
                                            tvaf.setPaidAmount(leftAmt);
                                            leftAmt = 0.0;
                                        } else {
                                            tvaf.setPaidAmount(tvaf.getBalance());
                                            leftAmt = leftAmt - tvaf.getBalance();
                                            tvaf.setBalance(0.0);
                                        }

                                        dao.save(tvaf);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("fixError : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void save() {
        List<TraderBalanceFixList> list = tblTraderModel.getListTrader();
        if (list != null) {
            try {
                for (TraderBalanceFixList tbfl : list) {
                    List<TmpVouAmtFix> listTBFL = dao.findAllHSQL(
                            "select o from TmpVouAmtFix o where o.key.cusId = '"
                            + tbfl.getTraderId() + "' and o.key.userId = '"
                            + Global.loginUser.getUserId() + "' and ifnull(o.paidAmount,0) <> 0");
                    if (listTBFL != null) {
                        if (!listTBFL.isEmpty()) {
                            TraderPayHis tph = new TraderPayHis();
                            tph.setPayDate(DateUtil.toDate(txtFixDate.getText()));
                            List<Customer> cus = dao.findAllHSQL("select o from Customer o where o.traderId='" + tbfl.getTraderId() + "'");
                            if (!cus.isEmpty()) {
                                tph.setTrader(cus.get(0));
                            }
                            tph.setRemark("Voucher Balance Fixed");
                            tph.setPaidAmtC(0.0);
                            tph.setDiscount(0.0);
                            String appCurr = Util1.getPropValue("system.app.currency");
                            List<Currency> curr = dao.findAllHSQL("Select o from Currency o where o.currencyCode='" 
                                    + appCurr + "'");
                            tph.setCurrency(curr.get(0));
                            //tph.setCurrency(curr.getCurrencyCode());
                            tph.setExRate(1.0);
                            tph.setPaidAmtP(0.0);
                            // Object user = dao.find(Appuser.class, vp.getUserId());
                            //List<Appuser> user = dao.findAllHSQL("select o from Appuser o where  o.userId='" + Global.loginUser + "'");
                            tph.setCreatedBy(Global.loginUser);
                            tph.setPayOption("Cash");
                            tph.setParentCurr(tph.getCurrency());

                            List<PaymentVou> listPV = new ArrayList();
                            for (TmpVouAmtFix tvaf : listTBFL) {
                                PaymentVou pv = new PaymentVou();
                                pv.setBalance(tvaf.getBalance());
                                pv.setVouNo(tvaf.getKey().getVouNo());
                                pv.setVouPaid(tvaf.getPaidAmount());
                                pv.setVouDate(tvaf.getKey().getSaleDate());
                                pv.setDiscount(0.0);
                                pv.setVouType(tvaf.getVouType());

                                listPV.add(pv);
                            }

                            tph.setListDetail(listPV);

                            //Location location = getLocation(tph.getTrader().getTraderId());
                            //tph.setLocation(location);
                            try {
                                dao.save(tph);
                            } catch (Exception e) {
                                log.error("saveTraderpayHis : " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("save : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }
    
    private void clear(){
        tblTraderModel.clear();
        tblUnPaidVouModel.clear();
        txtFixDate.setText(null);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtTraderFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTrader = new javax.swing.JTable();
        cboCusGroup = new javax.swing.JComboBox<>();
        cboBusinessType = new javax.swing.JComboBox<>();
        cboPriceType = new javax.swing.JComboBox<>();
        cboTownship = new javax.swing.JComboBox<>();
        cboParent = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUnPaidVou = new javax.swing.JTable();
        butCheckError = new javax.swing.JButton();
        butFixError = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        txtFixDate = new javax.swing.JFormattedTextField();

        txtTraderFilter.setFont(Global.textFont);
        txtTraderFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        txtTraderFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTraderFilterKeyReleased(evt);
            }
        });

        tblTrader.setFont(Global.textFont);
        tblTrader.setModel(tblTraderModel);
        tblTrader.setRowHeight(23);
        jScrollPane1.setViewportView(tblTrader);

        cboCusGroup.setFont(Global.textFont);
        cboCusGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Cus Group"));
        cboCusGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCusGroupActionPerformed(evt);
            }
        });

        cboBusinessType.setFont(Global.textFont);
        cboBusinessType.setBorder(javax.swing.BorderFactory.createTitledBorder("B-Type"));
        cboBusinessType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBusinessTypeActionPerformed(evt);
            }
        });

        cboPriceType.setFont(Global.textFont);
        cboPriceType.setBorder(javax.swing.BorderFactory.createTitledBorder("Price Type"));
        cboPriceType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPriceTypeActionPerformed(evt);
            }
        });

        cboTownship.setFont(Global.textFont);
        cboTownship.setBorder(javax.swing.BorderFactory.createTitledBorder("Township"));
        cboTownship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTownshipActionPerformed(evt);
            }
        });

        cboParent.setFont(Global.textFont);
        cboParent.setBorder(javax.swing.BorderFactory.createTitledBorder("Parent"));
        cboParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboParentActionPerformed(evt);
            }
        });

        tblUnPaidVou.setFont(Global.textFont);
        tblUnPaidVou.setModel(tblUnPaidVouModel);
        tblUnPaidVou.setRowHeight(23);
        jScrollPane2.setViewportView(tblUnPaidVou);

        butCheckError.setText("Check Error");
        butCheckError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCheckErrorActionPerformed(evt);
            }
        });

        butFixError.setText("Fix Error");
        butFixError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFixErrorActionPerformed(evt);
            }
        });

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        txtFixDate.setEditable(false);
        txtFixDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Fix Date"));
        txtFixDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFixDateMouseClicked(evt);
            }
        });
        txtFixDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFixDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtTraderFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCusGroup, 0, 98, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBusinessType, 0, 57, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPriceType, 0, 16, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTownship, 0, 118, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboParent, 0, 46, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCheckError)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFixDate, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFixError)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTraderFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboBusinessType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboPriceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butCheckError)
                    .addComponent(butFixError)
                    .addComponent(butSave)
                    .addComponent(txtFixDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboBusinessType, cboCusGroup, cboParent, cboPriceType, cboTownship, txtFixDate, txtTraderFilter});

    }// </editor-fold>//GEN-END:initComponents

    private void txtTraderFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTraderFilterKeyReleased
        if (txtTraderFilter.getText().length() == 0) {
            tblTraderSorter.setRowFilter(null);
        } else {
            //sorter.setRowFilter(startsWithFilter);
            tblTraderSorter.setRowFilter(RowFilter.regexFilter(txtTraderFilter.getText()));
        }
    }//GEN-LAST:event_txtTraderFilterKeyReleased

    private void cboCusGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCusGroupActionPerformed

    }//GEN-LAST:event_cboCusGroupActionPerformed

    private void cboBusinessTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBusinessTypeActionPerformed

    }//GEN-LAST:event_cboBusinessTypeActionPerformed

    private void cboPriceTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPriceTypeActionPerformed

    }//GEN-LAST:event_cboPriceTypeActionPerformed

    private void cboTownshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTownshipActionPerformed

    }//GEN-LAST:event_cboTownshipActionPerformed

    private void cboParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParentActionPerformed

    }//GEN-LAST:event_cboParentActionPerformed

    private void butCheckErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCheckErrorActionPerformed
        checkError();
    }//GEN-LAST:event_butCheckErrorActionPerformed

    private void butFixErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFixErrorActionPerformed
        fixError();
    }//GEN-LAST:event_butFixErrorActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        if (DateUtil.isValidDate(txtFixDate.getText().trim())) {
            save();
            clear();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid fix date.",
                            "Date", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void txtFixDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFixDateActionPerformed
        
    }//GEN-LAST:event_txtFixDateActionPerformed

    private void txtFixDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFixDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFixDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFixDateMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCheckError;
    private javax.swing.JButton butFixError;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox<String> cboBusinessType;
    private javax.swing.JComboBox<String> cboCusGroup;
    private javax.swing.JComboBox<String> cboParent;
    private javax.swing.JComboBox<String> cboPriceType;
    private javax.swing.JComboBox<String> cboTownship;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblTrader;
    private javax.swing.JTable tblUnPaidVou;
    private javax.swing.JFormattedTextField txtFixDate;
    private javax.swing.JTextField txtTraderFilter;
    // End of variables declaration//GEN-END:variables
}
