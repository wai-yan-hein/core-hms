/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.DamageDetailHis;
import com.cv.app.pharmacy.database.entity.DamageHis;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.TmpEXRate;
import com.cv.app.pharmacy.ui.common.CurrencyEditor;
import com.cv.app.pharmacy.ui.common.CurrencyTotalTableModel;
import com.cv.app.pharmacy.ui.common.DamageTableModel;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.TmpEXRateTableModel;
import static com.cv.app.pharmacy.ui.entry.Sale.log;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * Damage entry use for medicine damage. User must key in date and location.
 * This class is using in DamageView class as panel
 *
 */
public class Damage extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(Damage.class.getName());
    private final AbstractDataAccess dao = Global.dao; //Use for database access
    private GenVouNoImpl vouEngine = null; //Use for voucher number generation
    //listDetail use in tblDamage
    private List<DamageDetailHis> listDetail
            = ObservableCollections.observableList(new ArrayList<DamageDetailHis>());
    private MedicineUP medUp = new MedicineUP(dao); //Use for unit popup
    private BestAppFocusTraversalPolicy focusPolicy; //For focus managment
    private DamageTableModel dmgTableModel = new DamageTableModel(listDetail, dao,
            medUp, this); //Model for tblDamage
    private DamageHis currDamage = new DamageHis(); //Current damage entry class
    private int mouseClick = 2;
    private boolean canEdit = true;
    private boolean isBind = false;
    private CurrencyTotalTableModel cttlModel = new CurrencyTotalTableModel();
    private final TmpEXRateTableModel tmpExrTableModel = new TmpEXRateTableModel();

    /**
     * Creates new form Damage
     */
    public Damage() {
        initComponents();

        //formatting txtVouNo text box.
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("Damage : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        initDmgTable();
        actionMapping();
        txtDmgDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "Damage", DateUtil.getPeriod(txtDmgDate.getText()));
        genVouNo();
        addNewRow();
        dmgTableModel.setParent(tblDamage);

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
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
        dmgTableModel.setCurrency(appCurr);

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
    }

    //Get latest Damage voucher serial number
    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtVouNo.setText(vouNo);
        try {
            List<DamageHis> listDH = dao.findAllHSQL(
                    "select o from DamageHis o where o.dmgVouId = '" + txtVouNo.getText() + "'"
            );

            if (listDH != null) {
                if (!listDH.isEmpty()) {
                    log.error("Duplicate Sale vour error : " + txtVouNo.getText() + " @ "
                            + txtDmgDate.getText());
                    JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate damage vou no. Exit the program and try again.",
                            "Sale Vou No", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        } catch (Exception ex) {
            log.error("genVouNo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    //Clearing the whole form. Calling from newForm method
    private void clear() {
        try {
            if (tblDamage.getCellEditor() != null) {
                tblDamage.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        canEdit = true;
        txtRemark.setText("");
        lblStatus.setText("NEW");
        dmgTableModel.clear();
        assignDefaultValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtDmgDate.getText()));
        genVouNo();
        setFocus();

        tmpExrTableModel.clear();
        tmpExrTableModel.addEmptyRow();
        deleteExRateTmp();

        System.gc();
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    /*
     * Initializing combobox data
     */
    private void initCombo() {
        try {
            isBind = true;
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboLocation, this);
            new ComBoBoxAutoComplete(cboCurrency, this);
            isBind = false;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowDmg = true) order by o.locationName");
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

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    /*
     * Function key mapping
     */
    private void actionMapping() {
        /*
         * F3 event on tblDamage
         * Medicine list popup dialoug will show
         */
        tblDamage.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblDamage.getActionMap().put("F3-Action", actionMedList);

        /*
         * F8 event on tblDamage
         * Delete row in tblDamage table
         */
        tblDamage.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblDamage.getActionMap().put("F8-Action", actionItemDelete);

        /*
         * Enter event on tblDamage
         * For tblDamage focus managment
         */
        tblDamage.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblDamage.getActionMap().put("ENTER-Action", actionTblDmgEnterKey);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtDmgDate);
        formActionKeyMapping(cboLocation);
        formActionKeyMapping(tblDamage);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actionMedList">
    /*
     * This function will called when pressing function key F3
     * in tblDamage.
     */
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblDamage.getCellEditor() != null) {
                    tblDamage.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + +ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            }
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionItemDelete">
    /*
     * This function will called when pressing function key F8
     * in tblDamage.
     */
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            DamageDetailHis dmgdh;
            int yes_no = -1;

            if (tblDamage.getSelectedRow() >= 0) {
                dmgdh = listDetail.get(tblDamage.getSelectedRow());

                if (dmgdh.getMedicineId().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Damage item delete", JOptionPane.YES_NO_OPTION);
                        if (tblDamage.getCellEditor() != null) {
                            tblDamage.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                    if (yes_no == 0) {
                        dmgTableModel.delete(tblDamage.getSelectedRow());
                    }
                }
            }
        }
    };// </editor-fold>

    /*
     * This function will called when pressing F3 or
     * entering medicine code in code field in tblDamage.
     * When medicine code cannot find in database and 
     * medicine list popup will be show.
     */
    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine;

        try {
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
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getMedList">
    /*
     * This function showing medicine list dialouge.
     */
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
    /*
     * This function will be call from when user select medicine from 
     * medicine list popup or select history data from Damage Search 
     * dialouge.
     */
    @Override
    public void selected(Object source, Object selectObj) {
        try {
            dao.open();
            switch (source.toString()) {
                case "DamageList":
                    VoucherSearch vs = (VoucherSearch) selectObj;
                    currDamage = (DamageHis) dao.find(DamageHis.class, vs.getInvNo());

                    if (Util1.getNullTo(currDamage.isDeleted())) {
                        lblStatus.setText("DELETED");
                    } else {
                        lblStatus.setText("EDIT");
                    }

                    txtVouNo.setText(currDamage.getDmgVouId());
                    txtDmgDate.setText(DateUtil.toDateStr(currDamage.getDmgDate()));
                    txtRemark.setText(currDamage.getRemark());
                    cboLocation.setSelectedItem(currDamage.getLocation());
                    //txtTotalAmount.setValue(NumberUtil.NZero(currDamage.getTotalAmount()));

                    listDetail = dao.findAllHSQL(
                            "select o from DamageDetailHis o where o.vouNo = '"
                            + currDamage.getDmgVouId() + "' order by o.uniqueId");
                    currDamage.setListDetail(listDetail);
                    /*if (currDamage.getListDetail().size() > 0) {
                        listDetail = currDamage.getListDetail();
                    }*/

                    for (DamageDetailHis ddh : listDetail) {
                        medUp.add(ddh.getMedicineId());
                    }
                    dmgTableModel.setListDetail(listDetail);
                    tblDamage.requestFocusInWindow();
                    Currency curr = (Currency) dao.find(Currency.class, currDamage.getCurrencyId());
                    cboCurrency.setSelectedItem(curr);

                    //For exchange rate
                    deleteExRateTmp();
                    insertExRateToTemp(currDamage.getDmgVouId());
                    getExRate();

                    break;
                case "MedicineList":
                    Medicine med = (Medicine) dao.find(Medicine.class,
                            ((Medicine) selectObj).getMedId());
                    medUp.add(med);
                    int selectRow = tblDamage.getSelectedRow();
                    dmgTableModel.setMed(med, selectRow);
                    break;
            }
            dao.close();
        } catch (Exception ex) {
            log.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addNewRow">
    /*
     * Using for adding blank row for tblDamage.
     */
    private void addNewRow() {
        DamageDetailHis his = new DamageDetailHis();

        his.setMedicineId(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="initDmgTable">
    /*
     * Initializing tblDamage and set column with of the table.
     */
    private void initDmgTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblDamage.setCellSelectionEnabled(true);
        }
        tblDamage.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblDamage.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblDamage.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name
        tblDamage.getColumnModel().getColumn(2).setPreferredWidth(60);//Relstr
        tblDamage.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblDamage.getColumnModel().getColumn(4).setPreferredWidth(40);//Qty
        tblDamage.getColumnModel().getColumn(5).setPreferredWidth(30);//Unit
        tblDamage.getColumnModel().getColumn(6).setPreferredWidth(40);//Currency
        tblDamage.getColumnModel().getColumn(7).setPreferredWidth(50);//Cost Price
        tblDamage.getColumnModel().getColumn(8).setPreferredWidth(50);//Amount

        tblDamage.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblDamage.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor(this));
        tblDamage.getColumnModel().getColumn(6).setCellEditor(new CurrencyEditor());
        tblDamage.getColumnModel().getColumn(7).setCellEditor(new BestTableCellEditor(this));

        tblDamage.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //txtTotalAmount.setValue(dmgTableModel.getTotalAmount());
                cttlModel.setList(dmgTableModel.getCurrTotal());
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

    /*
     * This section is implementation of FormAction interface.
     * Include common function of all entry form. Those function will be
     * call from DamageView.
     * Save
     * newForm
     * history
     * delete
     * deleteCopy
     * print
     */
    @Override
    public void save() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isValidEntry() && dmgTableModel.isValidEntry()) {
            //removeEmptyRow();

            try {
                //dao.open();
                //dao.beginTran();
                List<DamageDetailHis> listTmp = dmgTableModel.getListDetail();
                String vouNo = currDamage.getDmgVouId();
                deleteExRateHis(vouNo);
                insertExRateHis(vouNo);
                for (DamageDetailHis ddh : listTmp) {
                    ddh.setVouNo(vouNo);
                    if (ddh.getDmgDetailId() == null) {
                        ddh.setDmgDetailId(vouNo + "-" + ddh.getUniqueId().toString());
                    }
                    dao.save(ddh);
                }
                currDamage.setListDetail(listTmp);
                dao.save(currDamage);
                //dao.commit();

                //For upload to account
                //uploadToAccount(currDamage);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                deleteDetail();
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
                "Damage Search", dao, -1);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtDmgDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.getNullTo(currDamage.isDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "Damage delete", JOptionPane.ERROR);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Damage delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                currDamage.setDeleted(true);
                currDamage.setIntgUpdStatus(null);
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
        if (isValidEntry() && dmgTableModel.isValidEntry()) {
            try {
                //removeEmptyRow();
                //dao.open();
                //dao.beginTran();
                List<DamageDetailHis> listTmp = dmgTableModel.getListDetail();
                String vouNo = currDamage.getDmgVouId();
                deleteExRateHis(vouNo);
                insertExRateHis(vouNo);
                for (DamageDetailHis ddh : listTmp) {
                    ddh.setVouNo(vouNo);
                    if (ddh.getDmgDetailId() == null) {
                        ddh.setDmgDetailId(vouNo + "-" + ddh.getUniqueId().toString());
                    }
                    dao.save(ddh);
                }
                currDamage.setListDetail(listTmp);
                dao.save(currDamage);
                //dao.commit();
                //String vouNo = currDamage.getDmgVouId();
                //For upload to account
                //uploadToAccount(currDamage);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                newForm();

                String reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + "DamageVoucher";
                String compName = Util1.getPropValue("report.company.name");
                Map<String, Object> params = new HashMap();
                params.put("compName", compName);
                params.put("prm_dmg_id", vouNo);
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }
    // </editor-fold>

    //Set focus to txtRemark.
    public void setFocus() {
        txtRemark.requestFocusInWindow();
    }

    /*
     * Focus managment function
     */
    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        //focusOrder.add(txtCusId);
        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
    /*
     * Adding focus movement key.
     * Default movement key is tab key.
     * Adding arrow key down and up to focus movement key
     */
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

    /*
     * Validate user entry
     */
    private boolean isValidEntry() {
        Date vouSaleDate = DateUtil.toDate(txtDmgDate.getText());
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
            currDamage.setDmgVouId(txtVouNo.getText());
            currDamage.setDmgDate(DateUtil.toDate(txtDmgDate.getText()));
            currDamage.setLocation((Location) cboLocation.getSelectedItem());
            currDamage.setRemark(txtRemark.getText());
            //currDamage.setTotalAmount(NumberUtil.NZero(txtTotalAmount.getText()));
            currDamage.setDeleted(Util1.getNullTo(currDamage.isDeleted()));
            currDamage.setIntgUpdStatus(null);
            //String appCurr = Util1.getPropValue("system.app.currency");
            String appCurr = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            currDamage.setCurrencyId(appCurr);
            if (lblStatus.getText().equals("NEW")) {
                currDamage.setDeleted(false);
            }

            currDamage.setSession(Global.sessionId);

            if (lblStatus.getText().equals("NEW")) {
                currDamage.setCreatedBy(Global.loginUser);
            } else {
                currDamage.setUpdatedBy(Global.loginUser);
                currDamage.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            try {
                if (tblDamage.getCellEditor() != null) {
                    tblDamage.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            if (NumberUtil.NZeroL(currDamage.getExrId()) == 0) {
                Long exrId = getExchangeId(txtDmgDate.getText(), currDamage.getCurrencyId());
                currDamage.setExrId(exrId);
            }
        }

        return status;
    }

    /*
     * This function calling from clear method.
     * User request newForm or after saved entry and this function was call.
     */
    private void assignDefaultValue() {
        listDetail = ObservableCollections.observableList(new ArrayList<DamageDetailHis>());
        dmgTableModel.setListDetail(listDetail);

        txtDmgDate.setText(DateUtil.getTodayDateStr());
    }
    /*
     * Focus managment for tblDamage when user press enter key
     */
    private Action actionTblDmgEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblDamage.getCellEditor() != null) {
                    tblDamage.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblDamage.getSelectedRow();
            int col = tblDamage.getSelectedColumn();

            DamageDetailHis ddh = listDetail.get(row);

            if (col == 0 && ddh.getMedicineId().getMedId() != null) {
                tblDamage.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 2 && ddh.getMedicineId().getMedId() != null) {
                tblDamage.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 3 && ddh.getMedicineId().getMedId() != null) {
                tblDamage.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && ddh.getMedicineId().getMedId() != null) {
                tblDamage.setColumnSelectionInterval(6, 6); //Move to Cost Price
            } else if (col == 5 && ddh.getMedicineId().getMedId() != null) {
                tblDamage.setColumnSelectionInterval(6, 6); //Move to Cost Price
            } else if (col == 6 && ddh.getMedicineId().getMedId() != null) {
                if ((row + 1) <= listDetail.size()) {
                    tblDamage.setRowSelectionInterval(row + 1, row + 1);
                }
                tblDamage.setColumnSelectionInterval(0, 0); //Move to Code
            }
        }
    };

    /*
     * tblDamage has at least one blank row for enterning data.
     * Before saving record, need to remove blank row.
     * Will call from save method.
     */
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
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionHistory">
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

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String url = "http://example.com/api/users/" + vouNo;
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
                    dao.execSql("update dmg_his set intg_upd_status = null where dmg_id = '" + vouNo + "'");
                } catch (Exception ex) {
                    log.error("uploadToAccount error : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }

        }
    }
    
    /*private void uploadToAccount(DamageHis dh) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            if (Global.mqConnection != null) {
                if (Global.mqConnection.isStatus()) {
                    try {
                        ActiveMQConnection mq = Global.mqConnection;
                        MapMessage msg = mq.getMapMessageTemplate();
                        msg.setString("program", Global.programId);
                        msg.setString("entity", "DAMAGE");
                        msg.setString("VOUCHER-NO", dh.getDmgVouId());
                        msg.setString("remark", dh.getRemark());
                        msg.setString("dmbDate", DateUtil.toDateStr(dh.getDmgDate(), "yyyy-MM-dd"));
                        msg.setBoolean("deleted", dh.isDeleted());
                        msg.setDouble("vouTotal", dh.getTotalAmount());
                        msg.setString("currency", "MMK");
                        msg.setString("queueName", "INVENTORY");

                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + dh.getDmgVouId() + " - " + ex);
                    }
                }
            }
        }
    }*/

    private void deleteDetail() {
        String deleteSQL;

        //All detail section need to explicity delete
        //because of save function only delete to join table
        deleteSQL = dmgTableModel.getDeleteSql();
        if (deleteSQL != null) {
            dao.execSql(deleteSQL);
        }
        //delete section end
    }

    private void setEditStatus(String invId) {
        //canEdit
        /*List<SessionCheckCheckpoint> list = dao.findAllHSQL(
                "select o from SessionCheckCheckpoint o where o.tranOption = 'PHARMACY-Return In' "
                + " and o.tranInvId = '" + invId + "'");*/
        if (!Util1.hashPrivilege("CanEditPurchaseCheckPoint")) {
            List list = dao.findAllSQLQuery(
                    "select * from c_bk_pur_his where pur_inv_id = '" + invId + "'");
            if (list != null) {
                canEdit = list.isEmpty();
            } else {
                canEdit = true;
            }
        } else {
            canEdit = true;
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
        String strSql = "delete from dmg_ex_rate_his where vou_no = '"
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
        String strSql = "insert into dmg_ex_rate_his(vou_no, from_curr, to_curr, ex_rate) "
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
                + "from dmg_ex_rate_his where vou_no = '" + vouNo + "'";
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
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDmgDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDamage = new javax.swing.JTable(dmgTableModel);
        lblStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExRate = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCurrTotal = new javax.swing.JTable();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date");

        txtDmgDate.setFont(Global.textFont);
        txtDmgDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDmgDateMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location");

        cboLocation.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);

        tblDamage.setFont(Global.textFont);
        tblDamage.setModel(dmgTableModel);
        tblDamage.setRowHeight(23);
        tblDamage.setShowVerticalLines(false);
        tblDamage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblDamageFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblDamage);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        lblStatus.setText("Edit");

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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(19, 19, 19)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtDmgDate, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 55, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(237, 237, 237)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(txtDmgDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDmgDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDmgDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDmgDate.setText(strDate);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtDmgDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtDmgDateMouseClicked

    private void tblDamageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblDamageFocusLost
        /*try{
            if(tblDamage.getCellEditor() != null){
                tblDamage.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblDamageFocusLost

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        if (!isBind) {
            String appCurr = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            dmgTableModel.setCurrency(appCurr);
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
    private javax.swing.JTable tblCurrTotal;
    private javax.swing.JTable tblDamage;
    private javax.swing.JTable tblExRate;
    private javax.swing.JFormattedTextField txtDmgDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
