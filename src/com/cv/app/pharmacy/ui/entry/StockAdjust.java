/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.pharmacy.database.entity.AdjHis;
import com.cv.app.pharmacy.database.entity.AdjDetailHis;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.StockAdjTableModel;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import com.cv.app.common.ActiveMQConnection;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.tempentity.TmpEXRate;
import com.cv.app.pharmacy.ui.common.CurrencyEditor;
import com.cv.app.pharmacy.ui.common.CurrencyTotalTableModel;
import com.cv.app.pharmacy.ui.common.TmpEXRateTableModel;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.ReportUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author WSwe
 */
public class StockAdjust extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(StockAdjust.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private List<AdjDetailHis> listDetail
            = ObservableCollections.observableList(new ArrayList<AdjDetailHis>());
    private MedicineUP medUp = new MedicineUP(dao);
    private BestAppFocusTraversalPolicy focusPolicy;
    private StockAdjTableModel adjTableModel = new StockAdjTableModel(listDetail, dao,
            medUp, this);
    private AdjHis currAdjust = new AdjHis();
    private boolean isBind = false;
    private int mouseClick = 2;
    private CurrencyTotalTableModel cttlModel = new CurrencyTotalTableModel();
    private final TmpEXRateTableModel tmpExrTableModel = new TmpEXRateTableModel();

    /**
     * Creates new form StockAdjust
     */
    public StockAdjust() {
        initComponents();

        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            initAdjTable();
            if (cboLocation.getSelectedItem() != null) {
                Location loc = (Location) cboLocation.getSelectedItem();
                adjTableModel.setLocationId(loc.getLocationId());
            }
            dao.close();
        } catch (Exception ex) {
            log.error("StockAdjust : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        txtAdjDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "Adjust", DateUtil.getPeriod(txtAdjDate.getText()));
        genVouNo();
        addNewRow();

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        adjTableModel.setParent(tblAdjDetail);
        lblStatus.setText("NEW");

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

        String appCurr = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
        adjTableModel.setCurrency(appCurr);

        if (!isBind) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            if (curr != null) {
                tmpExrTableModel.setFromCurr(curr.getCurrencyCode());
            }
        }
        tmpExrTableModel.setParent(tblExRate);
        tmpExrTableModel.addEmptyRow();

        String strSql = "delete from tmp_ex_rate where user_id = '"
                + Global.loginUser.getUserId() + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("clear temp data : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtVouNo.setText(vouNo);
        List<AdjHis> listAH = dao.findAllHSQL(
                "select o from AdjHis o where o.adjVouId = '" + txtVouNo.getText() + "'"
        );

        if (listAH != null) {
            if (!listAH.isEmpty()) {
                log.error("Duplicate adjust vour error : " + txtVouNo.getText() + " @ "
                        + txtAdjDate.getText());
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "Duplicate return out vou no. Exit the program and try again.",
                        "Adjust Vou No", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    private void clear() {
        try {
            if (tblAdjDetail.getCellEditor() != null) {
                tblAdjDetail.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }

        txtRemark.setText("");
        lblStatus.setText("NEW");

        assignDefaultValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtAdjDate.getText()));
        genVouNo();
        setFocus();

        tmpExrTableModel.clear();
        tmpExrTableModel.addEmptyRow();
        deleteExRateTmp();
        
        System.gc();
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        isBind = true;
        BindingUtil.BindCombo(cboLocation, getLocationFilter());
        BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            if (cboLocation.getItemCount() > 0) {
                cboLocation.setSelectedIndex(0);
            }
        }
        new ComBoBoxAutoComplete(cboLocation, this);
        new ComBoBoxAutoComplete(cboCurrency, this);
        isBind = false;
    }// </editor-fold>

    private List getLocationFilter() {
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            return dao.findAllHSQL(
                    "select o from Location o where o.locationId in ("
                    + "select a.key.locationId from UserLocationMapping a "
                    + "where a.key.userId = '" + Global.loginUser.getUserId()
                    + "' and a.isAllowAdj = true) order by o.locationName");
        } else {
            return dao.findAllHSQL("select o from Location o order by o.locationName");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    private void actionMapping() {
        //F3 event on tblSale
        tblAdjDetail.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblAdjDetail.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblAdjDetail.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblAdjDetail.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblTransfer
        tblAdjDetail.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblAdjDetail.getActionMap().put("ENTER-Action", actionTblAdjustEnterKey);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtAdjDate);
        formActionKeyMapping(cboLocation);
        formActionKeyMapping(tblAdjDetail);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionMedList">
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblAdjDetail.getCellEditor() != null) {
                    tblAdjDetail.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            }
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionItemDelete">
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            AdjDetailHis pdh;
            int yes_no = -1;

            if (tblAdjDetail.getSelectedRow() >= 0) {
                pdh = listDetail.get(tblAdjDetail.getSelectedRow());

                if (pdh.getMedicineId().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Adjust item delete", JOptionPane.YES_NO_OPTION);
                        if (tblAdjDetail.getCellEditor() != null) {
                            tblAdjDetail.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                    if (yes_no == 0) {
                        adjTableModel.delete(tblAdjDetail.getSelectedRow());
                    }
                }
            }
        }
    };// </editor-fold>

    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine;

        if (!medCode.trim().isEmpty()) {
            medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                selected("MedicineList", medicine);
            } else { //For barcode
                medicine = (Medicine) dao.find("Medicine", "barcode = '"
                        + medCode + "' and active = true");

                if (medicine != null) {
                    selected("MedicineList", medicine);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            System.out.println("Blank medicine code.");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getMedList">
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
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="selected">
    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "AdjVouList":
                try {
                VoucherSearch vs = (VoucherSearch) selectObj;
                dao.open();
                currAdjust = (AdjHis) dao.find(AdjHis.class, vs.getInvNo());

                if (Util1.getNullTo(currAdjust.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                txtVouNo.setText(currAdjust.getAdjVouId());
                txtAdjDate.setText(DateUtil.toDateStr(currAdjust.getAdjDate()));
                txtRemark.setText(currAdjust.getRemark());
                cboLocation.setSelectedItem(currAdjust.getLocation());
                //txtTotalAmount.setValue(currAdjust.getAmount());
                Currency curr = (Currency) dao.find(Currency.class, currAdjust.getCurrencyId());
                cboCurrency.setSelectedItem(curr);
                listDetail = dao.findAllHSQL(
                        "select o from AdjDetailHis o where o.vouNo = '"
                        + currAdjust.getAdjVouId() + "' order by o.uniqueId");
                currAdjust.setListDetail(listDetail);
                /*if (currAdjust.getListDetail().size() > 0) {
                        listDetail = currAdjust.getListDetail();
                    }*/

                for (AdjDetailHis adh : listDetail) {
                    medUp.add(adh.getMedicineId());
                }
                adjTableModel.setListDetail(listDetail);
                String appCurr = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
                adjTableModel.setCurrency(appCurr);
                //For exchange rate
                cttlModel.setList(adjTableModel.getCurrTotal());
                deleteExRateTmp();
                insertExRateToTemp(currAdjust.getAdjVouId());
                getExRate();
                dao.close();
            } catch (Exception ex) {
                log.error("adjVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }

            tblAdjDetail.requestFocusInWindow();
            break;
            case "MedicineList":
                try {
                dao.open();
                Medicine med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                medUp.add(med);
                int selectRow = tblAdjDetail.getSelectedRow();
                adjTableModel.setMed(med, selectRow);
                dao.close();
            } catch (Exception ex) {
                log.error("MedicineList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            break;
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addNewRow">
    private void addNewRow() {
        AdjDetailHis his = new AdjDetailHis();

        his.setMedicineId(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hasNewRow">
    private boolean hasNewRow() {
        boolean status = false;
        AdjDetailHis adjDetailHis = listDetail.get(listDetail.size() - 1);

        String ID = adjDetailHis.getMedicineId().getMedId();
        if (ID == null) {
            status = true;
        }

        return status;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="initAdjTable">
    private void initAdjTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblAdjDetail.setCellSelectionEnabled(true);
        }
        tblAdjDetail.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblAdjDetail.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblAdjDetail.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name
        tblAdjDetail.getColumnModel().getColumn(2).setPreferredWidth(60);//Relstr
        tblAdjDetail.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblAdjDetail.getColumnModel().getColumn(4).setPreferredWidth(50);//Sys-Bal
        tblAdjDetail.getColumnModel().getColumn(5).setPreferredWidth(50);//Usr-Balance
        tblAdjDetail.getColumnModel().getColumn(6).setPreferredWidth(30);//Usr-Unit
        tblAdjDetail.getColumnModel().getColumn(7).setPreferredWidth(10);//Currency
        tblAdjDetail.getColumnModel().getColumn(8).setPreferredWidth(20);//Adj Qty
        tblAdjDetail.getColumnModel().getColumn(9).setPreferredWidth(20);//Unit
        tblAdjDetail.getColumnModel().getColumn(10).setPreferredWidth(30);//Type
        tblAdjDetail.getColumnModel().getColumn(11).setPreferredWidth(30);//Balance
        tblAdjDetail.getColumnModel().getColumn(12).setPreferredWidth(40);//Cost Price
        tblAdjDetail.getColumnModel().getColumn(13).setPreferredWidth(40);//Amount

        tblAdjDetail.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblAdjDetail.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor(this));
        tblAdjDetail.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor(this));
        tblAdjDetail.getColumnModel().getColumn(7).setCellEditor(new CurrencyEditor());
        tblAdjDetail.getColumnModel().getColumn(8).setCellEditor(new BestTableCellEditor(this));
        JComboBox cboAdjType = new JComboBox();
        BindingUtil.BindCombo(cboAdjType, dao.findAll("AdjType"));
        tblAdjDetail.getColumnModel().getColumn(10).setCellEditor(
                new DefaultCellEditor(cboAdjType));
        tblAdjDetail.getColumnModel().getColumn(11).setCellEditor(new BestTableCellEditor(this));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblAdjDetail.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tblAdjDetail.getColumnModel().getColumn(11).setCellRenderer(rightRenderer);

        tblAdjDetail.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //txtTotalAmount.setValue(adjTableModel.getTotalAmount());
                cttlModel.setList(adjTableModel.getCurrTotal());
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
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FormAction Implementation">
    @Override
    public void save() {
        try {
            if (tblAdjDetail.getCellEditor() != null) {
                tblAdjDetail.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (isValidEntry() && adjTableModel.isValidEntry()) {
            //removeEmptyRow();
            try {
                String vouNo = currAdjust.getAdjVouId();
                deleteExRateHis(vouNo);
                insertExRateHis(vouNo);
                List<AdjDetailHis> listTmp = adjTableModel.getListDetail();
                
                dao.open();
                dao.beginTran();
                for (AdjDetailHis adh : listTmp) {
                    adh.setVouNo(vouNo);
                    if (adh.getAdjDetailId() == null) {
                        adh.setAdjDetailId(vouNo + "-" + adh.getUniqueId().toString());
                    }
                    dao.save1(adh);
                }
                currAdjust.setListDetail(listTmp);
                dao.save1(currAdjust);
                dao.commit();
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                deleteDetail();
                uploadToAccount(currAdjust);
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
        clear();
    }

    @Override
    public void history() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Adjust Search", dao);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtAdjDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.getNullTo(currAdjust.isDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "Adjust delete", JOptionPane.ERROR);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Adjust delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                currAdjust.setDeleted(true);
                currAdjust.setIntgUpdStatus(null);
                save();
            }
        }
    }

    @Override
    public void deleteCopy() {
        System.out.println("Delete Copy");
    }

    @Override
    public void print() {
        String vouNo = txtVouNo.getText();
        save();

        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "AdjustVoucher";
        String compName = Util1.getPropValue("report.company.name");

        Map<String, Object> params = new HashMap();
        params.put("compName", compName);
        params.put("vou_no", vouNo);

        try {
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
        } catch (Exception ex) {
            log.error("print : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    // </editor-fold>

    public void setFocus() {
        tblAdjDetail.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        //focusOrder.add(txtCusId);
        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
    private void AddFocusMoveKey() {
        Set backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);

        Set forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);

        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));

        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
    }//</editor-fold>

    private boolean isValidEntry() {
        Date vouSaleDate = DateUtil.toDate(txtAdjDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean status = true;

        if (cboLocation.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose location.",
                    "No location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboLocation.requestFocusInWindow();
        } else {
            try {
                if (tblAdjDetail.getCellEditor() != null) {
                    tblAdjDetail.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }
            currAdjust.setAdjVouId(txtVouNo.getText());
            currAdjust.setAdjDate(DateUtil.toDate(txtAdjDate.getText()));
            currAdjust.setLocation((Location) cboLocation.getSelectedItem());
            currAdjust.setRemark(txtRemark.getText());
            //currAdjust.setAmount(NumberUtil.NZero(txtTotalAmount.getText()));
            currAdjust.setDeleted(Util1.getNullTo(currAdjust.isDeleted()));
            //String appCurr = Util1.getPropValue("system.app.currency");
            String appCurr = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            currAdjust.setCurrencyId(appCurr);
            currAdjust.setIntgUpdStatus(null);
            if (lblStatus.getText().equals("NEW")) {
                currAdjust.setDeleted(false);
            }

            currAdjust.setSession(Global.sessionId);

            if (lblStatus.getText().equals("NEW")) {
                currAdjust.setCreatedBy(Global.loginUser);
            } else {
                currAdjust.setUpdatedBy(Global.loginUser);
                currAdjust.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            if (NumberUtil.NZeroL(currAdjust.getExrId()) == 0) {
                Long exrId = getExchangeId(txtAdjDate.getText(), currAdjust.getCurrencyId());
                currAdjust.setExrId(exrId);
            }
        }

        return status;
    }

    private void assignDefaultValue() {
        listDetail = ObservableCollections.observableList(new ArrayList<AdjDetailHis>());
        adjTableModel.setListDetail(listDetail);

        txtAdjDate.setText(DateUtil.getTodayDateStr());
    }

    private Action actionTblAdjustEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblAdjDetail.getCellEditor() != null) {
                    tblAdjDetail.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblAdjDetail.getSelectedRow();
            int col = tblAdjDetail.getSelectedColumn();

            AdjDetailHis ridh = listDetail.get(row);

            if (col == 0 && ridh.getMedicineId().getMedId() != null) {
                tblAdjDetail.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 1 && ridh.getMedicineId().getMedId() != null) {
                tblAdjDetail.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 2 && ridh.getMedicineId().getMedId() != null) {
                tblAdjDetail.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 3 && ridh.getMedicineId().getMedId() != null) {
                tblAdjDetail.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && ridh.getMedicineId().getMedId() != null) {
                tblAdjDetail.setColumnSelectionInterval(6, 6); //Move to Adj Type
            } else if (col == 5 && ridh.getMedicineId().getMedId() != null) {
                tblAdjDetail.setColumnSelectionInterval(6, 6); //Move to Qty
            } else if (col == 6 && ridh.getMedicineId().getMedId() != null) {
                tblAdjDetail.setColumnSelectionInterval(7, 7); //Move to Qty
            } else if (col == 7 && ridh.getMedicineId().getMedId() != null) {
                if ((row + 1) <= listDetail.size()) {
                    tblAdjDetail.setRowSelectionInterval(row + 1, row + 1);
                }
                tblAdjDetail.setColumnSelectionInterval(0, 0); //Move to Code
            }
        }
    };

    private void removeEmptyRow() {
        listDetail.remove(listDetail.size() - 1);
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
    };// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actionHistory">
    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };// </editor-fold>

    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    private Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };

    private void deleteDetail() {
        String deleteSQL;

        //All detail section need to explicity delete
        //because of save function only delete to join table
        deleteSQL = adjTableModel.getDeleteSql();
        if (deleteSQL != null) {
            dao.execSql(deleteSQL);
        }
        //delete section end
    }

    private void uploadToAccount(AdjHis currAdjust) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            if (!Global.mqConnection.isStatus()) {
                String mqUrl = Util1.getPropValue("system.mqserver.url");
                Global.mqConnection = new ActiveMQConnection(mqUrl);
            }
            if (Global.mqConnection != null) {
                if (Global.mqConnection.isStatus()) {
                    try {
                        ActiveMQConnection mq = Global.mqConnection;
                        MapMessage msg = mq.getMapMessageTemplate();
                        msg.setString("program", Global.programId);
                        msg.setString("entity", "ADJUST");
                        msg.setString("VOUCHER-NO", currAdjust.getAdjVouId());
                        msg.setString("queueName", "INVENTORY");
                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber()
                                + " - " + currAdjust.getAdjVouId() + " - " + ex);
                    }
                } else {
                    log.error("Connection status error : " + currAdjust.getAdjVouId());
                }
            } else {
                log.error("Connection error : " + currAdjust.getAdjVouId());
            }
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

    private void deleteExRateTmp(){
        String strSql = "delete from tmp_ex_rate where user_id = '"
                + Global.loginUser.getUserId() + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("deleteExRateTmp : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    private void deleteExRateHis(String vouNo){
        String strSql = "delete from stock_adjust_ex_rate_his where vou_no = '"
                + vouNo + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("deleteExRateHis : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    private void insertExRateHis(String vouNo){
        String strSql = "insert into stock_adjust_ex_rate_his(vou_no, from_curr, to_curr, ex_rate) "
                + "select '" + vouNo + "', from_curr, to_curr, ex_rate from "
                + "tmp_ex_rate where user_id = '" + Global.loginUser.getUserId() + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("insertExRateHis : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    private void insertExRateToTemp(String vouNo){
        String strSql = "insert into tmp_ex_rate(user_id, from_curr, to_curr, ex_rate) "
                + "select '" + Global.loginUser.getUserId() + "', from_curr, to_curr, ex_rate "
                + "from stock_adjust_ex_rate_his where vou_no = '" + vouNo + "'";
        try {
            dao.deleteSQL(strSql);
        } catch (Exception ex) {
            log.error("insertExRateToTemp : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    private void getExRate(){
        String strSql = "select o from TmpEXRate o where o.key.userId = '" + Global.loginUser.getUserId() + "'";
        
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
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txtAdjDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAdjDetail = new javax.swing.JTable(adjTableModel);
        lblStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExRate = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCurrTotal = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(674, 613));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Location");

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtAdjDate.setFont(Global.textFont);
        txtAdjDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtAdjDateMouseClicked(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRemarkActionPerformed(evt);
            }
        });

        tblAdjDetail.setFont(Global.textFont);
        tblAdjDetail.setModel(adjTableModel);
        tblAdjDetail.setRowHeight(23);
        tblAdjDetail.setShowVerticalLines(false);
        tblAdjDetail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblAdjDetailFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblAdjDetail);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblStatus.setText("jLabel5");

        jLabel6.setText("Currency ");

        cboCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCurrencyActionPerformed(evt);
            }
        });

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(24, 24, 24)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtAdjDate, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(txtAdjDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblStatus)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtAdjDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdjDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtAdjDate.setText(strDate);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtAdjDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtAdjDateMouseClicked

    private void txtRemarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRemarkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkActionPerformed

    private void tblAdjDetailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblAdjDetailFocusLost
        /*try{
         if(tblAdjDetail.getCellEditor() != null){
         tblAdjDetail.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblAdjDetailFocusLost

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (!isBind) {
            if (cboLocation.getSelectedItem() != null) {
                Location loc = (Location) cboLocation.getSelectedItem();
                adjTableModel.setLocationId(loc.getLocationId());
            }
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        if (!isBind) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            if (curr == null) {
                adjTableModel.setCurrency("MMK");
            } else {
                adjTableModel.setCurrency(curr.getCurrencyCode());
            }
        }
    }//GEN-LAST:event_cboCurrencyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblAdjDetail;
    private javax.swing.JTable tblCurrTotal;
    private javax.swing.JTable tblExRate;
    private javax.swing.JFormattedTextField txtAdjDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
