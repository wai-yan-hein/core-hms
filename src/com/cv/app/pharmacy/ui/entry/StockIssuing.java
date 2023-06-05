/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.BestDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.StockIssueDetailHis;
import com.cv.app.pharmacy.database.entity.StockIssueHis;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.StockOutstanding;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.StockCostingDetail;
import com.cv.app.pharmacy.database.tempentity.TmpEXRate;
import com.cv.app.pharmacy.ui.common.CurrencyEditor;
import com.cv.app.pharmacy.ui.common.CurrencyTotalTableModel;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.IssueTableModel;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.TmpEXRateTableModel;
import com.cv.app.pharmacy.ui.common.TraderCodeCellEditor;
import static com.cv.app.pharmacy.ui.entry.Sale.log;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.OutstandingStockList;
import com.cv.app.pharmacy.ui.util.StockIssueListDialog;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import javax.jms.MapMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockIssuing extends javax.swing.JPanel implements SelectionObserver,
        FormAction, MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(StockIssuing.class.getName());
    private AbstractDataAccess dao = new BestDataAccess();
    private GenVouNoImpl vouEngine = null;
    private IssueTableModel issueTableModel = new IssueTableModel(dao, this, this);
    private StockIssueHis sih = new StockIssueHis();
    private int mouseClick = 2;
    private boolean isBind = false;
    private CurrencyTotalTableModel cttlModel = new CurrencyTotalTableModel();
    private final TmpEXRateTableModel tmpExrTableModel = new TmpEXRateTableModel();

    /**
     * Creates new form StockIssuing
     */
    public StockIssuing() {
        initComponents();
        try {
            txtIssueDate.setText(DateUtil.getTodayDateStr());
            lblStatus.setText("NEW");
            txtIssueId.setFormatterFactory(new VouFormatFactory());
            vouEngine = new GenVouNoImpl(dao, "StockIssuing",
                    DateUtil.getPeriod(txtIssueDate.getText()));
            genVouNo();
            initCombo();
            initTable();
            actionMapping();
            issueTableModel.addEmptyRow();
            issueTableModel.setIssueId(txtIssueId.getText());
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

            Currency appCurr = (Currency) cboCurrency.getSelectedItem();
            issueTableModel.setCurrency(appCurr);

            if (!isBind) {
                Currency curr = (Currency) cboCurrency.getSelectedItem();
                if (curr != null) {
                    tmpExrTableModel.setFromCurr(curr.getCurrencyCode());
                }
            }
            tmpExrTableModel.setParent(tblExRate);
            tmpExrTableModel.addEmptyRow();

            String strSql = "delete from tmp_ex_rate where user_id = '"
                    + Global.machineId + "'";
            try {
                dao.deleteSQL(strSql);
            } catch (Exception ex) {
                log.error("clear temp data : " + ex.getMessage());
            } finally {
                dao.close();
            }
        } catch (Exception ex) {
            log.error("StockIssuing : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private void genVouNo() {
        try {
            String vouNo = vouEngine.getVouNo();
            txtIssueId.setText(vouNo);
            List<StockIssueHis> listSIH = dao.findAllHSQL("select o from StockIssueHis o where o.issueId = '"
                    + txtIssueId.getText() + "'");
            if (listSIH != null) {
                if (!listSIH.isEmpty()) {
                    log.error("Duplicate stock issue vou error : " + txtIssueId.getText() + " @ "
                            + txtIssueDate.getText());
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Duplicate stock issue vou no. Exit the program and try again.",
                            "Stock Issue Vou No", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        } catch (Exception ex) {
            log.error("genVouNo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "Outstanding":
                StockOutstanding outs = (StockOutstanding) selectObj;
                issueTableModel.add(outs);
                break;
            case "StockIssueList":
                try {
                dao.open();
                VoucherSearch vs = (VoucherSearch) selectObj;
                sih = (StockIssueHis) dao.find(StockIssueHis.class, vs.getInvNo());

                //List<StockIssueDetailHis> list = sih.getListDetail();
                List<StockIssueDetailHis> list = dao.findAllHSQL(
                        "select o from StockIssueDetailHis o where o.issueId = '"
                        + sih.getIssueId() + "' order by o.uniqueId");
                sih.setListDetail(list);
                System.out.println(list.size());

                if (sih.isDeleted()) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                txtIssueId.setText(sih.getIssueId());
                txtIssueDate.setText(DateUtil.toDateStr(sih.getIssueDate()));
                txtRemark.setText(sih.getRemark());
                cboLocation.setSelectedItem(sih.getLocation());
                cboToLocation.setSelectedItem(sih.getToLocation());
                Currency curr = (Currency) dao.find(Currency.class, sih.getCurrencyId());
                cboCurrency.setSelectedItem(curr);
                issueTableModel.setListDetail(list);
                issueTableModel.addEmptyRow();

                //For exchange rate
                deleteExRateTmp();
                insertExRateToTemp(sih.getIssueId());
                getExRate();
            } catch (Exception ex) {
                log.error("StockIssueList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
            break;
            case "MedicineList":
                if (tblIssue.getSelectedRow() >= 0) {
                    String medId = ((Medicine) selectObj).getMedId();
                    issueTableModel.setMed((Medicine) selectObj, tblIssue.getSelectedRow());
                    calculateCost(medId);
                }
                break;
            case "TraderCode":
                try {
                String traderCode = (String) selectObj;
                Trader trader = (Trader) dao.find("Trader", "traderId = '"
                        + traderCode.toUpperCase() + "' and active = true");
                selected("CustomerList", trader);
            } catch (Exception ex) {
                log.error("selected : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "CustomerList":
                if (tblIssue.getSelectedRow() >= 0) {
                    issueTableModel.setTrader((Trader) selectObj, tblIssue.getSelectedRow());
                }
                break;
        }
    }

    @Override
    public void save() {
        System.out.println("save");
        try {
            if (tblIssue.getCellEditor() != null) {
                tblIssue.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (isValidEntry() && issueTableModel.isValidEntry()) {
            List<StockIssueDetailHis> listDetail = issueTableModel.getListDetail();
            sih.setListDetail(listDetail);

            try {
                //dao.open();
                //dao.beginTran();
                String vouNo = sih.getIssueId();
                deleteExRateHis(vouNo);
                insertExRateHis(vouNo);
                if (listDetail != null) {
                    for (StockIssueDetailHis sidh : listDetail) {
                        sidh.setIssueId(vouNo);
                        if (sidh.getTranId() == null) {
                            sidh.setTranId(vouNo + "-" + sidh.getUniqueId().toString());
                        }
                        dao.save(sidh);
                    }
                }
                dao.save(sih);
                //dao.commit();
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                deleteDetail();
                uploadToAccount(sih.getIssueId());
                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void newForm() {
        System.out.println("newForm");
        try {
            if (tblIssue.getCellEditor() != null) {
                tblIssue.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        lblStatus.setText("NEW");
        txtRemark.setText("");
        txtIssueDate.setText(DateUtil.getTodayDateStr());
        issueTableModel.clear();
        vouEngine.setPeriod(DateUtil.getPeriod(txtIssueDate.getText()));
        sih = new StockIssueHis();
        vouEngine.setPeriod(DateUtil.getPeriod(txtIssueDate.getText()));
        genVouNo();
        issueTableModel.addEmptyRow();
        issueTableModel.setIssueId(txtIssueId.getText());
        tmpExrTableModel.clear();
        tmpExrTableModel.addEmptyRow();
        deleteExRateTmp();
        System.gc();
    }

    @Override
    public void history() {
        try {
            if (tblIssue.getCellEditor() != null) {
                tblIssue.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        StockIssueListDialog dialog = new StockIssueListDialog(dao, this);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtIssueDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("delete");
        try {
            if (tblIssue.getCellEditor() != null) {
                tblIssue.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                "Stock issuing delete", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            sih.setDeleted(true);
            save();
        }
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
    }

    private void initCombo() {
        try {
            isBind = true;
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboToLocation, getLocationFilter());
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                if (cboLocation.getItemCount() > 0) {
                    cboLocation.setSelectedIndex(0);
                }
            }
            new ComBoBoxAutoComplete(cboLocation, this);
            new ComBoBoxAutoComplete(cboToLocation, this);
            new ComBoBoxAutoComplete(cboCurrency, this);
            Object tmpObj = Util1.getDefaultValue("Currency");
            if (tmpObj != null) {
                cboCurrency.setSelectedItem(tmpObj);
            }
            isBind = false;
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
                        + "' and a.isAllowStkIssue = true) order by o.locationName");
            } else {
                return dao.findAllHSQL("select o from Location o order by o.locationName");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return null;
    }

    private void initTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblIssue.setCellSelectionEnabled(true);
        }
        tblIssue.getTableHeader().setFont(Global.lableFont);
        tblIssue.getColumnModel().getColumn(0).setPreferredWidth(10);  //Option
        tblIssue.getColumnModel().getColumn(1).setPreferredWidth(90);  //Vou No
        tblIssue.getColumnModel().getColumn(2).setPreferredWidth(30); //Med Code
        tblIssue.getColumnModel().getColumn(3).setPreferredWidth(150);  //Medicine
        tblIssue.getColumnModel().getColumn(4).setPreferredWidth(30); //T-Code
        tblIssue.getColumnModel().getColumn(5).setPreferredWidth(150);  //Trader Name
        tblIssue.getColumnModel().getColumn(6).setPreferredWidth(40);  //Outstanding
        tblIssue.getColumnModel().getColumn(7).setPreferredWidth(10);  //Exp-Date
        tblIssue.getColumnModel().getColumn(8).setPreferredWidth(5);  //Currency
        tblIssue.getColumnModel().getColumn(9).setPreferredWidth(2);   //Qty
        tblIssue.getColumnModel().getColumn(10).setPreferredWidth(10);  //Unit
        tblIssue.getColumnModel().getColumn(11).setPreferredWidth(30);  //Balance
        tblIssue.getColumnModel().getColumn(9).setCellEditor(new BestTableCellEditor(this));
        tblIssue.getColumnModel().getColumn(2).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblIssue.getColumnModel().getColumn(4).setCellEditor(
                new TraderCodeCellEditor(dao));
        //tblIssue.getColumnModel().getColumn(6).setCellRenderer(new TableDateFieldRenderer());
        //JComboBox cboLocationCell = new JComboBox();
        //cboLocationCell.setFont(Global.textFont); // NOI18N
        //BindingUtil.BindCombo(cboLocationCell, dao.findAll("Currency"));
        //tblIssue.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cboLocationCell));
        tblIssue.getColumnModel().getColumn(8).setCellEditor(new CurrencyEditor());

        issueTableModel.setParent(tblIssue);

        tblIssue.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                //txtTotal.setValue(issueTableModel.getTotal());
                cttlModel.setList(issueTableModel.getCurrTotal());
            }
        });

        tblCurrTotal.getColumnModel().getColumn(0).setPreferredWidth(10);  //Option
        tblCurrTotal.getColumnModel().getColumn(1).setPreferredWidth(50);  //Vou No

        tblExRate.getColumnModel().getColumn(0).setCellEditor(
                new CurrencyEditor());
        tblExRate.getColumnModel().getColumn(1).setCellEditor(
                new BestTableCellEditor(this));
        tblExRate.getColumnModel().getColumn(2).setCellEditor(
                new BestTableCellEditor(this));
    }

    private boolean isValidEntry() {
        Date vouSaleDate = DateUtil.toDate(txtIssueDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean status = true;

        if (cboLocation.getSelectedItem() == null) {
            status = false;

            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
        } else {
            sih.setIssueId(txtIssueId.getText());
            sih.setRemark(txtRemark.getText());
            sih.setIssueDate(DateUtil.toDate(txtIssueDate.getText()));
            sih.setLocation((Location) cboLocation.getSelectedItem());
            sih.setToLocation((Location) cboToLocation.getSelectedItem());
            //String appCurr = Util1.getPropValue("system.app.currency");
            String appCurr = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            sih.setCurrencyId(appCurr);
            //sih.setTtlAmt(NumberUtil.NZero(txtTotal.getValue()));
            if (lblStatus.getText().equals("NEW")) {
                sih.setDeleted(false);
                sih.setCreatedBy(Global.loginUser);
            } else {
                sih.setUpdatedBy(Global.loginUser);
                sih.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            try {
                if (tblIssue.getCellEditor() != null) {
                    tblIssue.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }
            if (NumberUtil.NZeroL(sih.getExrId()) == 0) {
                Long exrId = getExchangeId(txtIssueDate.getText(), sih.getCurrencyId());
                sih.setExrId(exrId);
            }
        }

        return status;
    }

    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblIssue.getSelectedRow() >= 0) {
                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Receiving item delete", JOptionPane.YES_NO_OPTION);
                    if (tblIssue.getCellEditor() != null) {
                        tblIssue.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                    log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }

                if (yes_no == 0) {
                    issueTableModel.delete(tblIssue.getSelectedRow());
                }
            }
        }
    };

    private final Action actionExRateItemDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblExRate.getSelectedRow() >= 0) {
                tmpExrTableModel.delete(tblExRate.getSelectedRow());
            }
        }
    };

    private void actionMapping() {
        //F3 event on tblSale
        tblIssue.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblIssue.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblIssue.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblIssue.getActionMap().put("F8-Action", actionItemDelete);

        //F4 trader list
        tblIssue.getInputMap().put(KeyStroke.getKeyStroke("F4"), "F4-Action");
        tblIssue.getActionMap().put("F4-Action", actionTraderList);

        //Enter event on tblService
        tblIssue.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblIssue.getActionMap().put("ENTER-Action", actionTblServiceEnterKey);

        tblExRate.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblExRate.getActionMap().put("F8-Action", actionExRateItemDelete);

        //F8 event on tblExpense
        //tblReceive.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        //tblReceive.getActionMap().put("F8-Action", actionItemDeleteExp);
        formActionKeyMapping(txtIssueId);
        formActionKeyMapping(txtIssueDate);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(butBorrow);
        formActionKeyMapping(butOutstanding);
        formActionKeyMapping(tblIssue);
    }

    private void borrow() {
        System.out.println("Borrow");
        StockOutstanding outs = new StockOutstanding();

        outs.setTranOption("Borrow");
        outs.setInvId(txtIssueId.getText());
        outs.setTranDate(DateUtil.toDate(txtIssueDate.getText()));

        issueTableModel.add(outs);
    }

    @Override
    public void getMedInfo(String medCode) {
        try {
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                selected("MedicineList", medicine);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                        "Invalid.", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getMedList(String filter) {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        String cusGroup = "-";
        /*if(currSaleVou.getCustomerId() != null){
            if(currSaleVou.getCustomerId().getTraderGroup() != null){
                cusGroup = currSaleVou.getCustomerId().getTraderGroup().getGroupId();
            }
        }*/
        MedListDialog1 dialog = new MedListDialog1(dao, filter, locationId, cusGroup);

        if (dialog.getSelect() != null) {
            selected("MedicineList", dialog.getSelect());
        }

    }

    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblIssue.getCellEditor() != null) {
                    tblIssue.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    //getMedInfo("");
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            }
        }
    };

    private Action actionTraderList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblIssue.getCellEditor() != null) {
                    tblIssue.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            getCustomerList();
        }
    };

    private void getCustomerList() {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Customer List", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            delete();
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            deleteCopy();
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
            save();
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
            print();
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
            history();
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
            newForm();
        }
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

        //Delete
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
        jc.getActionMap().put("Ctrl-F8-Action", actionDelete);
    }

    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };

    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actionPrint">
    private Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };// </editor-fold>

    private Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };

    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    private void deleteDetail() {
        try {
            String deleteSql = issueTableModel.getDeleteSql();
            dao.execSql(deleteSql);
        } catch (Exception ex) {
            log.error("deleteDetail : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private Action actionTblServiceEnterKey = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblIssue.getCellEditor() != null) {
                    tblIssue.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblIssue.getSelectedRow();
            int col = tblIssue.getSelectedColumn();

            StockIssueDetailHis record = issueTableModel.getSelectedData(row);
            if (col == 0) {
                tblIssue.setColumnSelectionInterval(2, 2); //to Item Code
            } else if (col == 1) {
                tblIssue.setColumnSelectionInterval(2, 2); //to Charge Type
            } else if (col == 2 && record.getIssueMed().getMedId() != null) {
                tblIssue.setColumnSelectionInterval(8, 8); //to Charge Type
            } else if (col == 8 && record.getIssueMed().getMedId() != null) {
                if ((row + 1) <= issueTableModel.getListDetail().size()) {
                    tblIssue.setRowSelectionInterval(row + 1, row + 1);
                }
                tblIssue.setColumnSelectionInterval(2, 2); //to Description
            }
        }
    };

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String url = Util1.getPropValue("system.intg.api.url") + vouNo;
                HttpGet request = new HttpGet(url);
                CloseableHttpResponse response = httpClient.execute(request);
                // Handle the response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String output;
                    while ((output = br.readLine()) != null) {
                        log.info("return from server : " + output);
                    }
                }
            } catch (IOException e) {
                try {
                    dao.execSql("update stock_issue_his set intg_upd_status = null where issue_id = '" + vouNo + "'");
                } catch (Exception ex) {
                    log.error("uploadToAccount error : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }

        }
    }

    private void insertStockFilterCode(String medId) {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(txtIssueDate.getText()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true";

        strSQL = strSQL + " and m.med_id = '" + medId + "'";

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void calculateCost(String medId) {
        try {
            String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                    + Global.machineId + "'";
            String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                    + Global.machineId + "'";
            String strMethod = "FIFO";

            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();
            insertStockFilterCode(medId);

            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(txtIssueDate.getText()), "Opening",
                    Global.machineId);

            Currency currency = (Currency) cboCurrency.getSelectedItem();
            String strCurr = "MMK";
            if (currency != null) {
                strCurr = currency.getCurrencyCode();
            }

            List<TmpEXRate> listEXR = dao.findAllHSQL("select o from TmpEXRate o where o.key.userId = '"
                    + Global.machineId + "'");
            boolean localCost = false;
            if (listEXR != null) {
                if (!listEXR.isEmpty()) {
                    localCost = true;
                }
            }

            if (localCost) {
                calculateWithLocalExRate("Opening", DateUtil.toDateStrMYSQL(txtIssueDate.getText()),
                        Global.machineId, strMethod, strCurr);
            } else {
                insertCostDetail("Opening", DateUtil.toDateStrMYSQL(txtIssueDate.getText()),
                        strMethod);
                dao.commit();
            }

        } catch (Exception ex) {
            dao.rollBack();
            log.error("calculateCost : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void insertCostDetail(String costFor, String costDate, String method) {
        String userId = Global.machineId;
        String strDelete = "delete from tmp_costing_detail where cost_for = '" + costFor + "' and user_id = '" + userId + "'";
        String strSql = "select tsc.med_id item_id, bal_qty ttl_stock, cost_price.tran_date, cost_price.tran_option, \n"
                + "         cost_price.ttl_qty, cost_price.smallest_cost, cost_price, item_unit\n"
                + "    from tmp_stock_costing tsc, \n"
                + "         (select 'Adjust' tran_option, v_adj.med_id item_id, adj_date tran_date, \n"
                + "                 sum(adj_smallest_qty) ttl_qty, cost_price,\n"
                + "                 (cost_price/vm.smallest_qty) smallest_cost, v_adj.item_unit\n"
                + "            from v_adj, (select med_id, min(op_date) op_date\n"
                + "	                       from tmp_stock_filter where user_id = prm_user_id\n"
                + "                          group by med_id) tsf,\n"
                + "				 v_medicine vm\n"
                + "           where v_adj.med_id = tsf.med_id and deleted = false \n"
                + "			 and v_adj.med_id = vm.med_id and v_adj.item_unit = vm.item_unit\n"
                + "             and date(adj_date) >= op_date and date(adj_date) <= prm_cost_date\n"
                + "			 and vm.active = true\n"
                + "		   group by v_adj.med_id, adj_date, cost_price, v_adj.item_unit\n"
                + "		   union all\n"
                + "          select 'Purchase' tran_option, vpur.med_id item_id, pur_date tran_date, \n"
                + "                 sum(pur_smallest_qty+ifnull(pur_foc_smallest_qty,0)) ttl_qty, pur_unit_cost cost_price, \n"
                + "                 (pur_unit_cost/vm.smallest_qty) smallest_cost, vpur.pur_unit item_unit\n"
                + "            from v_purchase vpur, (select med_id, min(op_date) op_date\n"
                + "								     from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                    group by med_id) tsf,\n"
                + "				 v_medicine vm\n"
                + "           where vpur.med_id = tsf.med_id and deleted = false and date(pur_date) >= op_date\n"
                + "			 and vpur.med_id = vm.med_id and vpur.pur_unit = vm.item_unit\n"
                + "			 and date(pur_date) <= prm_cost_date and vm.active = true\n"
                + "           group by vpur.med_id, pur_date, pur_unit_cost, vpur.pur_unit\n"
                + "		   union all\n"
                + "          select 'Return-In' tran_option, vretin.med_id item_id, ret_in_date tran_date, \n"
                + "                 sum(ret_in_smallest_qty) ttl_qty, ret_in_price cost_price, \n"
                + "                 (ret_in_price/vm.smallest_qty) smallest_cost, vretin.item_unit\n"
                + "            from v_return_in vretin, (select med_id, min(op_date) op_date\n"
                + "	                                    from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                       group by med_id) tsf,\n"
                + "                 v_medicine vm\n"
                + "           where vretin.med_id = tsf.med_id and deleted = false and date(ret_in_date) >= op_date\n"
                + "	         and vretin.med_id = vm.med_id and vretin.item_unit = vm.item_unit\n"
                + "			 and date(ret_in_date) <= prm_cost_date and vm.active = true\n"
                + "           group by vretin.med_id, ret_in_date, ret_in_price, vretin.item_unit\n"
                + "           union all\n"
                + "          select 'Opening' tran_option, vso.med_id item_id, vso.op_date tran_date, \n"
                + "				 sum(vso.op_smallest_qty) ttl_qty, vso.cost_price, \n"
                + "				 (vso.cost_price/vm.smallest_qty) smallest_cost, vso.item_unit\n"
                + "            from v_stock_op vso, tmp_stock_filter tsf, v_medicine vm\n"
                + "		   where vso.med_id = tsf.med_id and vso.location = tsf.location_id\n"
                + "             and vso.med_id = vm.med_id and vso.item_unit = vm.item_unit\n"
                + "             and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                + "			 and vm.active = true\n"
                + "           group by vso.med_id, vso.op_date, vso.cost_price, vso.item_unit) cost_price\n"
                + "   where tsc.med_id = cost_price.item_id and tsc.user_id = prm_user_id and tsc.tran_option = prm_cost_for\n"
                + "   order by item_id, cost_price.tran_date desc, cost_price desc";

        strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                .replace("prm_cost_date", "'" + costDate + "'")
                .replace("prm_cost_for", "'" + costFor + "'");
        try {
            dao.execSql(strDelete);
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                String prvItemId = "-";
                String itemId;
                Double totalStock;
                String tranDate;
                String tranOption;
                Double ttlQty;
                Double smallestCost;
                Double unitCost;
                String unit;
                Double leftStock = 0.0;
                Double prvTtlStock = 0.0;
                String prvTranDate = "-";
                Double prvTtlQty = 0.0;
                Double prvCost = 0.0;
                Double prvLeftStock = 0.0;
                Double prvSmallestCost = 0.0;
                String prvTranOption = "-";
                Double costQty;

                while (rs.next()) {
                    itemId = rs.getString("item_id");
                    if (itemId.equals("101094")) {
                        log.info("Error Tran : " + itemId);
                    }
                    totalStock = rs.getDouble("ttl_stock");
                    tranDate = DateUtil.toDateStrMYSQL(DateUtil.toDateStr(rs.getDate("tran_date")));
                    tranOption = rs.getString("tran_option");
                    ttlQty = rs.getDouble("ttl_qty");
                    smallestCost = rs.getDouble("smallest_cost");
                    unitCost = rs.getDouble("cost_price");
                    unit = rs.getString("item_unit");

                    if (!prvItemId.equals(itemId)) {
                        if (leftStock > 0.0) {
                            String tmpSql = "insert into tmp_costing_detail(item_id, ttl_stock, tran_date, tran_option, ttl_qty, \n"
                                    + "					cost_price, cost_qty, smallest_cost, user_id, cost_for, item_unit)\n"
                                    + "		values('" + prvItemId + "', " + prvTtlStock + ", '" + prvTranDate
                                    + "', 'ERR', " + prvTtlQty + ",\n"
                                    + prvCost + ", " + prvLeftStock + ", " + prvSmallestCost + ", '" + userId + "',\n'"
                                    + costFor + "', '" + unit + "')";
                            dao.execSql(tmpSql);
                        }

                        prvItemId = itemId;
                        prvTtlStock = totalStock;
                        prvTranDate = tranDate;
                        prvTranOption = tranOption;
                        prvTtlQty = ttlQty;
                        prvSmallestCost = smallestCost;
                        prvCost = unitCost;
                        leftStock = totalStock;
                    }

                    if (leftStock > 0) {
                        if (leftStock >= ttlQty) {
                            costQty = ttlQty;
                            leftStock = leftStock - ttlQty;
                        } else {
                            costQty = leftStock;
                            leftStock = 0.0;
                        }

                        if (costQty > 0) {
                            String tmpSql1 = "insert into tmp_costing_detail(item_id, ttl_stock, tran_date, tran_option, ttl_qty, \n"
                                    + "					cost_price, cost_qty, smallest_cost, user_id, cost_for, item_unit)\n"
                                    + "	  values('" + itemId + "', " + totalStock + ", '" + tranDate + "', '" + tranOption + "', " + ttlQty + ",\n"
                                    + unitCost + ", " + costQty + ", " + smallestCost + ", '" + userId + "',\n'"
                                    + costFor + "' , '" + unit + "')";
                            dao.execSql(tmpSql1);
                        }
                    }
                }

                //rs.close();
                if (method.equals("FIFO")) {
                    String tmpSql2 = "update tmp_stock_costing tsc, (select item_id, sum(cost_qty*smallest_cost) ttl_cost, user_id\n"
                            + "						    from tmp_costing_detail\n"
                            + "					      where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "					      group by item_id, user_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    tmpSql2 = tmpSql2.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(tmpSql2);
                } else if (method.equals("AVG")) {
                    String tmpSql3 = "update tmp_costing_detail tcd, (\n"
                            + "	select user_id, item_id, sum(ttl_qty) ttl_qty, sum(ttl_qty*smallest_cost) ttl_amt, \n"
                            + "		(sum(ttl_qty*smallest_cost)/sum(ttl_qty)) as avg_cost\n"
                            + "	  from tmp_costing_detail\n"
                            + "	  where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "	  group by user_id,item_id) avgc\n"
                            + "	set tcd.smallest_cost = avgc.avg_cost\n"
                            + "	where tcd.item_id = avgc.item_id and tcd.user_id = avgc.user_id and tcd.user_id = prm_user_id";
                    tmpSql3 = tmpSql3.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    String tmpSql4 = "update tmp_stock_costing tsc, \n"
                            + "               (select user_id, item_id, sum(cost_qty*smallest_cost) ttl_cost\n"
                            + "				  from tmp_costing_detail\n"
                            + "				 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "				 group by user_id,item_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    tmpSql4 = tmpSql4.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(tmpSql3, tmpSql4);
                }
            }
        } catch (Exception ex) {
            log.error("insertCostDetail : " + ex.toString());
        } finally {
            //dao.close();
        }
    }

    private Long getExchangeId(String strDate, String curr) {
        long id = 0;
        if (Util1.getPropValue("system.multicurrency").equals("Y")) {
            try {
                Object value = dao.getMax("exr_id", "exchange_rate",
                        "(to_curr = '" + curr + "' or from_curr = '" + curr
                        + "') and date(created_date) = '"
                        + DateUtil.toDateStrMYSQL(strDate)
                        + "'"
                );
                if (value != null) {
                    id = NumberUtil.NZeroL(value);
                }
            } catch (Exception ex) {
                log.error("getExchangeId : " + ex.getMessage());
            }
        }
        return id;
    }

    private void calculateWithLocalExRate(String costFor, String costDate, String userId,
            String method, String curr) {
        String strSql = "select tsc.med_id item_id, bal_qty ttl_stock, cost_price.tran_date, cost_price.tran_option, \n"
                + "         cost_price.ttl_qty, cost_price.smallest_cost, cost_price, item_unit, currency_id,\n"
                + "         exr.from_curr, exr.to_curr, exr.ex_rate\n"
                + "    from tmp_stock_costing tsc, \n"
                + "         (select 'Adjust' tran_option, v_adj.med_id item_id, adj_date tran_date, \n"
                + "                 sum(adj_smallest_qty) ttl_qty, cost_price,\n"
                + "                 (cost_price/vm.smallest_qty) smallest_cost, v_adj.item_unit, v_adj.currency_id\n"
                + "            from v_adj join (select med_id, min(op_date) op_date\n"
                + "	                       from tmp_stock_filter where user_id = prm_user_id\n"
                + "                          group by med_id) tsf on v_adj.med_id = tsf.med_id\n"
                + "		    join v_medicine vm on v_adj.med_id = vm.med_id and v_adj.item_unit = vm.item_unit\n"
                + "           where deleted = false \n"
                + "             and date(adj_date) >= op_date and date(adj_date) <= prm_cost_date\n"
                + "		   group by v_adj.med_id, adj_date, v_adj.currency_id, cost_price, v_adj.item_unit\n"
                + "		   union all\n"
                + "          select 'Purchase' tran_option, vpur.med_id item_id, pur_date tran_date, \n"
                + "                 sum(pur_smallest_qty+ifnull(pur_foc_smallest_qty,0)) ttl_qty, pur_unit_cost cost_price, \n"
                + "                 (pur_unit_cost/vm.smallest_qty) smallest_cost, vpur.pur_unit item_unit, vpur.currency as currency_id\n"
                + "            from v_purchase vpur join (select med_id, min(op_date) op_date\n"
                + "								     from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                    group by med_id) tsf on vpur.med_id = tsf.med_id\n"
                + "			join v_medicine vm on vpur.med_id = vm.med_id and vpur.pur_unit = vm.item_unit\n"
                + "           where deleted = false and date(pur_date) >= op_date and date(pur_date) <= prm_cost_date\n"
                + "           group by vpur.med_id, pur_date, vpur.currency, pur_unit_cost, vpur.pur_unit\n"
                + "		   union all\n"
                + "          select 'Return-In' tran_option, vretin.med_id item_id, ret_in_date tran_date, \n"
                + "                 sum(ret_in_smallest_qty) ttl_qty, ret_in_price cost_price, \n"
                + "                 (ret_in_price/vm.smallest_qty) smallest_cost, vretin.item_unit, vretin.currency as currency_id\n"
                + "            from v_return_in vretin join (select med_id, min(op_date) op_date\n"
                + "	                                    from tmp_stock_filter where user_id = prm_user_id\n"
                + "                                       group by med_id) tsf on vretin.med_id = tsf.med_id\n"
                + "            join v_medicine vm on vretin.med_id = vm.med_id and vretin.item_unit = vm.item_unit\n"
                + "           where deleted = false and date(ret_in_date) >= op_date and date(ret_in_date) <= prm_cost_date\n"
                + "           group by vretin.med_id, ret_in_date, vretin.currency, ret_in_price, vretin.item_unit\n"
                + "           union all\n"
                + "          select 'Opening' tran_option, vso.med_id item_id, vso.op_date tran_date, \n"
                + "				 sum(vso.op_smallest_qty) ttl_qty, vso.cost_price, \n"
                + "				 (vso.cost_price/vm.smallest_qty) smallest_cost, vso.item_unit, sp.sys_prop_value as currency_id\n"
                + "            from v_stock_op vso join tmp_stock_filter tsf on vso.med_id = tsf.med_id and vso.location = tsf.location_id\n"
                + "            join v_medicine vm on vso.med_id = vm.med_id and vso.item_unit = vm.item_unit\n"
                + "            join (select sys_prop_value from sys_prop where sys_prop_desp = 'system.app.currency') sp\n"
                + "		   where vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n"
                + "           group by vso.med_id, vso.op_date, vso.cost_price, vso.item_unit) cost_price, \n"
                + "           (select * from tmp_ex_rate where user_id = prm_user_id) exr\n"
                + "   where tsc.med_id = cost_price.item_id and (cost_price.currency_id = exr.from_curr or cost_price.currency_id = exr.to_curr) \n"
                + "     and tsc.user_id = prm_user_id and tsc.tran_option = prm_cost_for\n"
                + "   order by item_id, cost_price.tran_date desc, cost_price desc";
        strSql = strSql.replace("prm_cost_for", "'" + costFor + "'")
                .replace("prm_cost_date", "'" + costDate + "'")
                .replace("prm_user_id", "'" + userId + "'")
                .replace("p_method", "'" + method + "'");

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                String prvMedId = "-";
                int leftStock = 0;

                while (rs.next()) {
                    StockCostingDetail scd = new StockCostingDetail();
                    String medId = rs.getString("item_id");
                    int ttlQty = rs.getInt("ttl_qty");
                    String currId = rs.getString("currency_id");
                    String fromCurrId = rs.getString("from_curr");
                    String toCurrId = rs.getString("to_curr");

                    scd.setItemId(medId);
                    scd.setTranDate(rs.getDate("tran_date"));
                    scd.setTranOption(rs.getString("tran_option"));
                    scd.setTranQty(ttlQty);
                    scd.setPackingCost(rs.getDouble("cost_price"));
                    scd.setSmallestCost(rs.getDouble("smallest_cost"));
                    scd.setUserId(userId);
                    scd.setCostFor(costFor);
                    scd.setUnit(rs.getString("item_unit"));
                    scd.setCurrencyId(rs.getString("currency_id"));
                    scd.setHomeCurr(curr);
                    scd.setExrDesp(fromCurrId + "-" + toCurrId);
                    scd.setExrRate(rs.getDouble("ex_rate"));

                    if (!prvMedId.equals(medId)) {
                        prvMedId = medId;
                        leftStock = rs.getInt("ttl_stock");
                    }

                    if (leftStock > 0) {
                        if (leftStock >= ttlQty) {
                            scd.setCostQty(ttlQty);
                            leftStock = leftStock - ttlQty;
                        } else {
                            scd.setCostQty(leftStock);
                            leftStock = 0;
                        }

                        if (curr.equals(currId)) {
                            scd.setExrSmallCost(rs.getDouble("smallest_cost"));
                            scd.setExrTtlCost(scd.getCostQty() * scd.getExrSmallCost());
                        } else if (curr.equals(fromCurrId) || curr.equals(toCurrId)) {
                            double exRate = rs.getDouble("ex_rate");
                            double ttlStock = rs.getDouble("ttl_stock");
                            double smlCost = rs.getDouble("smallest_cost");
                            double amount = ttlStock * smlCost * exRate;
                            scd.setExrSmallCost(smlCost * exRate);
                            //scd.setExrTtlCost(scd.getCostQty() * scd.getExrSmallCost());
                            scd.setExrTtlCost(amount);
                        } else {
                            scd.setExrSmallCost(0.0);
                            scd.setExrTtlCost(scd.getCostQty() * scd.getExrSmallCost());
                        }

                        dao.save(scd);
                    }
                }

                if (method.equals("FIFO")) {
                    strSql = "update tmp_stock_costing tsc, (select item_id, sum(cost_qty*exr_smallest_cost) ttl_cost, user_id\n"
                            + "	          from tmp_costing_detail\n"
                            + "		 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "	      group by item_id, user_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(strSql);
                } else if (method.equals("AVG")) {
                    strSql = "update tmp_costing_detail tcd, (\n"
                            + "					select user_id, item_id, sum(ttl_qty) ttl_qty, sum(ttl_qty*exr_smallest_cost) ttl_amt, \n"
                            + "						   (sum(ttl_qty*exr_smallest_cost)/if(sum(ttl_qty)=0,1,sum(ttl_qty))) as avg_cost\n"
                            + "				      from tmp_costing_detail\n"
                            + "					 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "					 group by user_id,item_id) avgc\n"
                            + "		set tcd.exr_smallest_cost = avgc.avg_cost\n"
                            + "		where tcd.item_id = avgc.item_id and tcd.user_id = avgc.user_id and tcd.user_id = prm_user_id";
                    strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(strSql);
                    strSql = "update tmp_stock_costing tsc, \n"
                            + "               (select user_id, item_id, sum(cost_qty*exr_smallest_cost) ttl_cost\n"
                            + "				  from tmp_costing_detail\n"
                            + "				 where user_id = prm_user_id and cost_for = prm_cost_for\n"
                            + "				 group by user_id,item_id) cd\n"
                            + "		   set total_cost = cd.ttl_cost\n"
                            + "		 where tsc.med_id = cd.item_id and tsc.user_id = cd.user_id\n"
                            + "		   and tsc.user_id = prm_user_id and tran_option = prm_cost_for";
                    strSql = strSql.replace("prm_user_id", "'" + userId + "'")
                            .replace("prm_cost_for", "'" + costFor + "'");
                    dao.execSql(strSql);
                }
            }
        } catch (Exception ex) {
            log.error("calculateWithLocalExRate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void deleteExRateTmp() {
        String strSql = "delete from tmp_ex_rate where user_id = '"
                + Global.machineId + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("deleteExRateTmp : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void deleteExRateHis(String vouNo) {
        String strSql = "delete from stock_issue_ex_rate_his where vou_no = '"
                + vouNo + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("deleteExRateHis : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void insertExRateHis(String vouNo) {
        String strSql = "insert into stock_issue_ex_rate_his(vou_no, from_curr, to_curr, ex_rate) "
                + "select '" + vouNo + "', from_curr, to_curr, ex_rate from "
                + "tmp_ex_rate where user_id = '" + Global.machineId + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("insertExRateHis : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void insertExRateToTemp(String vouNo) {
        String strSql = "insert into tmp_ex_rate(user_id, from_curr, to_curr, ex_rate) "
                + "select '" + Global.machineId + "', from_curr, to_curr, ex_rate "
                + "from stock_issue_ex_rate_his where vou_no = '" + vouNo + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("insertExRateToTemp : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getExRate() {
        String strSql = "select o from TmpEXRate o where o.key.userId = '" + Global.machineId + "'";

        try {
            List<TmpEXRate> list = dao.findAllHSQL(strSql);
            tmpExrTableModel.setList(list);
            tmpExrTableModel.addEmptyRow();
        } catch (Exception ex) {
            log.error("getExRate : " + ex.getMessage());
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

        jLabel1 = new javax.swing.JLabel();
        txtIssueId = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtIssueDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();
        butOutstanding = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblIssue = new javax.swing.JTable();
        butBorrow = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cboToLocation = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExRate = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCurrTotal = new javax.swing.JTable();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Issue Id");

        txtIssueId.setEditable(false);
        txtIssueId.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date ");

        txtIssueDate.setEditable(false);
        txtIssueDate.setFont(Global.textFont);
        txtIssueDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtIssueDateMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location ");

        cboLocation.setFont(Global.textFont);

        lblStatus.setText("NEW");

        butOutstanding.setFont(Global.lableFont);
        butOutstanding.setText("Outstanding");
        butOutstanding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOutstandingActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);

        tblIssue.setFont(Global.textFont);
        tblIssue.setModel(issueTableModel);
        tblIssue.setRowHeight(23);
        tblIssue.setShowVerticalLines(false);
        tblIssue.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblIssueFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblIssue);

        butBorrow.setFont(Global.lableFont);
        butBorrow.setText("Borrow");
        butBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBorrowActionPerformed(evt);
            }
        });

        jLabel6.setText("Currency ");

        cboCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCurrencyActionPerformed(evt);
            }
        });

        jLabel7.setText("To Location");

        cboToLocation.setFont(Global.textFont);

        tblExRate.setModel(tmpExrTableModel);
        tblExRate.setRowHeight(23);
        jScrollPane2.setViewportView(tblExRate);

        tblCurrTotal.setModel(cttlModel);
        tblCurrTotal.setRowHeight(23);
        jScrollPane3.setViewportView(tblCurrTotal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtIssueId, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtIssueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboLocation, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboToLocation, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butBorrow)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butOutstanding))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIssueId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtIssueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus)
                    .addComponent(butOutstanding)
                    .addComponent(butBorrow)
                    .addComponent(jLabel6)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(cboToLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtIssueDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtIssueDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtIssueDate.setText(strDate);

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtIssueDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtIssueDateMouseClicked

    private void butOutstandingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOutstandingActionPerformed
        OutstandingStockList dialog = new OutstandingStockList(dao, this, "StockIssuing");
    }//GEN-LAST:event_butOutstandingActionPerformed

    private void butBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBorrowActionPerformed
        borrow();
    }//GEN-LAST:event_butBorrowActionPerformed

    private void tblIssueFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblIssueFocusLost
        /*try{
            if(tblIssue.getCellEditor() != null){
                tblIssue.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblIssueFocusLost

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        if (!isBind) {
            Currency appCurr = (Currency) cboCurrency.getSelectedItem();
            issueTableModel.setCurrency(appCurr);
            tmpExrTableModel.setFromCurr(appCurr.getCurrencyCode());
        }
    }//GEN-LAST:event_cboCurrencyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butBorrow;
    private javax.swing.JButton butOutstanding;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox<String> cboToLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCurrTotal;
    private javax.swing.JTable tblExRate;
    private javax.swing.JTable tblIssue;
    private javax.swing.JFormattedTextField txtIssueDate;
    private javax.swing.JFormattedTextField txtIssueId;
    private javax.swing.JTextField txtRemark;
    // End of variables declaration//GEN-END:variables
}
