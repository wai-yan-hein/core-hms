/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TransferDetailHis;
import com.cv.app.pharmacy.database.entity.TransferHis;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.view.VReOrderLevel;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.TransferTableModel;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.pharmacy.util.StockList;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author WSwe
 */
public class Transfer extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(Transfer.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private List<TransferDetailHis> listDetail
            = ObservableCollections.observableList(new ArrayList<TransferDetailHis>());
    private MedicineUP medUp = new MedicineUP(dao);
    private BestAppFocusTraversalPolicy focusPolicy;
    private TransferTableModel tblTransferModel = new TransferTableModel(listDetail, dao,
            medUp, this, this);
    private TransferHis currTransfer = new TransferHis();
    private StockList stockList = new StockList(dao, medUp);

    private String strPrvDate;
    private Object prvFLocation;
    private Object prvTLocation;

    private int mouseClick = 2;
    private String strOption = "-";
    private boolean isBind = false;

    /**
     * Creates new form Sale
     */
    public Transfer() {
        initComponents();

        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("Transfer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        initTransferTable();

        txtTranDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "Transfer", DateUtil.getPeriod(txtTranDate.getText()));
        genVouNo();
        addNewRow();
        assignLocation();
        tblTransferModel.setStockList(stockList);
        //applyFocusPolicy();
        //AddFocusMoveKey();
        //this.setFocusTraversalPolicy(focusPolicy);
        tblTransferModel.setParent(tblTransfer);
        butPrint.setVisible(false);
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
    }

    public Transfer(List<VReOrderLevel> listReOrderLevel, Location from, Location to) {
        initComponents();

        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("Transfer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        initTransferTable();

        txtTranDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "Transfer", DateUtil.getPeriod(txtTranDate.getText()));
        genVouNo();
        addNewRow();
        assignLocation();
        tblTransferModel.setStockList(stockList);

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        tblTransferModel.setParent(tblTransfer);

        lblStatus.setText("NEW");

        cboFromLocation.setSelectedItem(from);
        cboToLocation.setSelectedItem(to);

        stockList.setLocation(from);
        initTransferItem(listReOrderLevel);
        tblTransferModel.setListDetail(listDetail);
        //addNewRow();
    }

    private void initTransferItem(List<VReOrderLevel> listReOrderLevel) {
        listDetail = new ArrayList();

        try {
            for (VReOrderLevel rol : listReOrderLevel) {
                float qtyInSmallest = NumberUtil.FloatZero(rol.getBalMin());
                float qtyBal = NumberUtil.FloatZero(rol.getBalMain());
                Medicine med = (Medicine) dao.find(Medicine.class, rol.getKey().getMed());
                //Medicine med = rol.getKey().getMed();
                medUp.add(med);
                med.setRelationGroupId(medUp.getRelation(med.getMedId()));
                stockList.add(med, qtyBal, null);

                List<RelationGroup> listRelG = med.getRelationGroupId();
                try {
                    for (RelationGroup rg : listRelG) {
                        float unitQty;
                        String unit = rg.getUnitId().getItemUnitCode();

                        if (qtyInSmallest >= rg.getSmallestQty() && qtyInSmallest != 0) {
                            TransferDetailHis tdh = new TransferDetailHis();
                            tdh.setInHandQtySmall(qtyBal);
                            tdh.setInHandQtyStr(MedicineUtil.getQtyInStr(listRelG, qtyBal));

                            unitQty = qtyInSmallest / rg.getSmallestQty();
                            qtyInSmallest = qtyInSmallest - (unitQty * rg.getSmallestQty());
                            qtyBal = qtyBal - (unitQty * rg.getSmallestQty());

                            tdh.setMedicineId(med);
                            tdh.setBalQtyStr(MedicineUtil.getQtyInStr(listRelG, qtyBal));
                            //tdh.setExpireDate(null);
                            tdh.setQty(unitQty);
                            tdh.setSmallestQty(unitQty * rg.getSmallestQty());
                            tdh.setUnit(rg.getUnitId());
                            String key = med.getMedId() + "-" + unit;
                            tdh.setPrice(medUp.getPrice(key, key, unitQty));
                            //tdh.setAmount();
                            listDetail.add(tdh);

                            if (qtyInSmallest == 0) {
                                break;
                            }

                            /*if (qtyStr.length() > 0) {
                             qtyStr = qtyStr + "," + unitQty + unit;
                             } else {
                             qtyStr = unitQty + unit;
                             }*/
                        }
                    }
                } catch (Exception ex) {
                    log.error("initTransferItem inner : " + med.getMedId() + " :- " + ex);
                }
            }
        } catch (Exception ex) {
            log.error("initTransferItem : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void genVouNo() {
        try {
            String vouNo = vouEngine.getVouNo();
            List<TransferHis> listTFH = dao.findAllHSQL(
                    "select o from TransferHis o where o.tranVouId = '" + vouNo + "'"
            );
            if (listTFH != null) {
                if (!listTFH.isEmpty()) {
                    log.error("Duplicate Sale vour error : " + txtVouNo.getText() + " @ "
                            + txtTranDate.getText());
                    JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate tran vou no. Exit the program and try again.",
                            "Transfer Vou No", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                } else {
                    txtVouNo.setText(vouNo);
                }
            } else {
                txtVouNo.setText(vouNo);
            }
        } catch (Exception ex) {
            log.error("genVouNo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        try {
            if (tblTransfer.getCellEditor() != null) {
                tblTransfer.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (lblStatus.getText().equals("NEW")) {
            strPrvDate = txtTranDate.getText();
            prvFLocation = cboFromLocation.getSelectedItem();
            prvTLocation = cboToLocation.getSelectedItem();
        }
        txtCusId.setText(null);
        txtCusName.setText(null);
        txtSupId.setText(null);
        txtSupName.setText(null);
        txtRemark.setText("");
        lblStatus.setText("NEW");
        medUp.clear();
        stockList.clear();
        tblTransferModel.clear();
        assignLocation();
        assignDefaultValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtTranDate.getText()));
        genVouNo();
        //setFocus();
        txtTotalItem.setText("0");
        System.gc();
    }

    private void initCombo() {
        isBind = true;
        BindingUtil.BindCombo(cboToLocation, getLocationToFilter());
        BindingUtil.BindCombo(cboFromLocation, getLocationFromFilter());
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            if (cboToLocation.getItemCount() > 0) {
                cboToLocation.setSelectedIndex(0);
            }
            if (cboFromLocation.getItemCount() > 0) {
                cboFromLocation.setSelectedIndex(0);
            }
        }
        new ComBoBoxAutoComplete(cboToLocation, this);
        new ComBoBoxAutoComplete(cboFromLocation, this);
        isBind = false;
    }

    private List getLocationFromFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowTranOut = true) order by o.locationName");
            } else {
                return dao.findAllHSQL("select o from Location o order by o.locationName");
            }
        } catch (Exception ex) {
            log.error("getLocationFromFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return null;
    }

    private List getLocationToFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowTranIn = true) order by o.locationName");
            } else {
                return dao.findAll("Location");
            }
        } catch (Exception ex) {
            log.error("getLocationToFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return null;
    }

    private void actionMapping() {
        //F3 event on tblSale
        tblTransfer.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblTransfer.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblTransfer.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblTransfer.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblTransfer
        tblTransfer.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblTransfer.getActionMap().put("ENTER-Action", actionTblTransferEnterKey);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtTranDate);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(tblTransfer);
    }
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //try {
            //if (tblTransfer.getCellEditor() != null) {
            //    tblTransfer.getCellEditor().stopCellEditing();
            //}
            //} catch (Exception ex) {
            //No entering medCode, only press F3
            try {
                dao.open();
                getMedList("");
                dao.close();
            } catch (Exception ex1) {
                log.error("actionMedList : " + ex1.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
            }
            //}
        }
    };
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TransferDetailHis transferdh;
            int yes_no = -1;

            if (tblTransfer.getSelectedRow() >= 0) {
                transferdh = listDetail.get(tblTransfer.getSelectedRow());

                if (transferdh.getMedicineId().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Sale item delete", JOptionPane.YES_NO_OPTION);

                        if (tblTransfer.getCellEditor() != null) {
                            tblTransfer.getCellEditor().stopCellEditing();
                        }
                    } catch (HeadlessException ex) {
                        log.error(ex.toString());
                    }

                    if (yes_no == 0) {
                        tblTransferModel.delete(tblTransfer.getSelectedRow());
                        txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
                    }
                }
            }
        }
    };// </editor-fold>

    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine;

        if (!medCode.trim().isEmpty()) {
            try {
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
            } catch (Exception ex) {
                log.error("getMedInfo : " + ex.getMessage());
            } finally {
                dao.close();
            }
        } else {
            System.out.println("Blank medicine code.");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getMedList">
    private void getMedList(String filter) {
        int locationId = -1;
        if (cboFromLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboFromLocation.getSelectedItem()).getLocationId();
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
            case "MedicineList":
                try {
                dao.open();
                Medicine med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                List<RelationGroup> listRel = med.getRelationGroupId();
                med.setRelationGroupId(listRel);

                if (listRel.size() > 0) {
                    medUp.add(med);
                    stockList.add(med, (Location) cboFromLocation.getSelectedItem());
                    int selectRow = tblTransfer.getSelectedRow();
                    tblTransferModel.setMed(med, selectRow, stockList);
                } else {
                    System.out.println("Transfer.selected MedicineList : Cannot get relation group");
                }
                txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
            } catch (Exception ex) {
                log.error("MedicineList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
            break;
            case "TransferVouList":
                try {
                VoucherSearch vs = (VoucherSearch) selectObj;
                dao.open();
                currTransfer = (TransferHis) dao.find(TransferHis.class, vs.getInvNo());

                if (Util1.getNullTo(currTransfer.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                txtVouNo.setText(currTransfer.getTranVouId());
                txtTranDate.setText(DateUtil.toDateStr(currTransfer.getTranDate()));
                txtRemark.setText(currTransfer.getRemark());
                cboFromLocation.setSelectedItem(currTransfer.getFromLocation());
                cboToLocation.setSelectedItem(currTransfer.getToLocation());

                listDetail = dao.findAllHSQL(
                        "select o from TransferDetailHis o where o.vouNo = '"
                        + currTransfer.getTranVouId() + "' order by o.uniqueId"
                );
                /*if (currTransfer.getListDetail().size() > 0) {
                        listDetail = currTransfer.getListDetail();
                    }*/
                currTransfer.setListDetail(listDetail);

                for (TransferDetailHis td : listDetail) {
                    medUp.add(td.getMedicineId());
                }
                tblTransferModel.setListDetail(listDetail);

                if (currTransfer.getCusId() != null) {
                    if (!currTransfer.getCusId().isEmpty()) {
                        Trader cus = (Trader) dao.find(Trader.class, currTransfer.getCusId());
                        if (cus != null) {
                            txtCusId.setText(cus.getTraderId());
                            txtCusName.setText(cus.getTraderName());
                        }
                    }
                }

                if (currTransfer.getSupId() != null) {
                    if (!currTransfer.getSupId().isEmpty()) {
                        Trader sup = (Trader) dao.find(Trader.class, currTransfer.getSupId());
                        if (sup != null) {
                            txtSupId.setText(sup.getTraderId());
                            txtSupName.setText(sup.getTraderName());
                        }
                    }
                }
                txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
                dao.close();
            } catch (Exception ex) {
                log.error("TransferVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }

            tblTransfer.requestFocusInWindow();
            break;
            case "DemanUpdate":
                //Update to stock balance list
                String entity = "com.cv.app.pharmacy.database.helper.Stock";
                TransferDetailHis tdh = (TransferDetailHis) selectObj;
                String strSQL = "SELECT * FROM " + entity + " where med.medId = '"
                        + tdh.getMedicineId().getMedId() + "' and expDate = "
                        + tdh.getExpireDate();
                List<Stock> list = JoSQLUtil.getResult(strSQL,
                        stockList.getStockList(tdh.getMedicineId().getMedId()));

                if (list.size() > 0) {
                    for (Stock stock : list) {
                        stock.setQtyStrDeman(tdh.getQty() + tdh.getUnit().getItemUnitCode());
                        float balance = stock.getQtySmallest() - tdh.getSmallestQty();
                        stock.setQtyStrBal(MedicineUtil.getQtyInStr(tdh.getMedicineId(),
                                balance));
                    }
                }
                break;
            case "CustomerList":
                if (strOption.equals("CUS")) {
                    assignCustomer((Trader) selectObj);
                } else if (strOption.equals("SUP")) {
                    assignSupplier((Trader) selectObj);
                }
                break;
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addNewRow">
    private void addNewRow() {
        TransferDetailHis his = new TransferDetailHis();

        his.setMedicineId(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hasNewRow">
    private boolean hasNewRow() {
        boolean status = false;
        TransferDetailHis retInDetailHis = listDetail.get(listDetail.size() - 1);

        String ID = retInDetailHis.getMedicineId().getMedId();

        if (ID == null) {
            status = true;
        }

        return status;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        TransferDetailHis transferdh = listDetail.get(row);
        String key = "";

        if (transferdh.getUnit() != null) {
            key = transferdh.getMedicineId().getMedId() + "-" + transferdh.getUnit().getItemUnitCode();
        }

        transferdh.setSmallestQty(NumberUtil.NZeroFloat(transferdh.getQty())
                * medUp.getQtyInSmallest(key));
        tblTransfer.setValueAt((NumberUtil.NZero(transferdh.getQty()) * NumberUtil.NZero(transferdh.getPrice())), row, 12);
    }// </editor-fold>

    private void initTransferTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblTransfer.setCellSelectionEnabled(true);
        }
        tblTransfer.getTableHeader().setFont(Global.lableFont);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        //Adjust column width
        tblTransfer.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblTransfer.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name
        tblTransfer.getColumnModel().getColumn(2).setPreferredWidth(60);//Relstr
        tblTransfer.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblTransfer.getColumnModel().getColumn(4).setPreferredWidth(50);//In Hand 
        tblTransfer.getColumnModel().getColumn(5).setPreferredWidth(40);//Qty
        tblTransfer.getColumnModel().getColumn(6).setPreferredWidth(15);//Unit
        tblTransfer.getColumnModel().getColumn(7).setPreferredWidth(50);//Balance
        tblTransfer.getColumnModel().getColumn(8).setPreferredWidth(30);//Price
        tblTransfer.getColumnModel().getColumn(9).setPreferredWidth(50);//Amount

        tblTransfer.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor());

        tblTransfer.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tblTransfer.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        tblTransfer.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));

        //Change JTable cell editor
        tblTransfer.getColumnModel().getColumn(6).setCellEditor(new TransferTableUnitCellEditor());
        tblTransfer.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                txtTotalAmount.setValue(tblTransferModel.getTotalAmount());
                txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="TransferTableUnitCellEditor">
    private class TransferTableUnitCellEditor extends AbstractCellEditor implements TableCellEditor {

        JComponent component = null;
        int colIndex = -1;
        // This method is called when a cell value is edited by the user.

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

            colIndex = vColIndex;

            if (isSelected) {
                // cell (and perhaps other cells) are selected
            }

            String medId = listDetail.get(rowIndex).getMedicineId().getMedId();

            try {
                JComboBox jb = new JComboBox();

                BindingUtil.BindCombo(jb, medUp.getUnitList(medId));
                component = jb;
            } catch (Exception ex) {
                log.error("getTableCellEditor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            // Configure the component with the specified value
            //((JTextField) component).setText("");

            // Return the configured component
            return component;
        }

        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        @Override
        public Object getCellEditorValue() {
            Object obj = ((JComboBox) component).getSelectedItem();

            return obj;
        }
    }// </editor-fold>

    @Override
    public void save() {
        System.out.println("Save");
        try {
            if (tblTransfer.getCellEditor() != null) {
                tblTransfer.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (isValidEntry() && tblTransferModel.isValidEntry()) {
            //removeEmptyRow();
            try {
                dao.open();
                dao.beginTran();
                List<TransferDetailHis> listTmp = tblTransferModel.getListDetail();
                String vouNo = currTransfer.getTranVouId();
                for (TransferDetailHis tdh : listTmp) {
                    tdh.setVouNo(vouNo);
                    if (tdh.getTranDetailId() == null) {
                        tdh.setTranDetailId(vouNo + "-" + tdh.getUniqueId().toString());
                    }
                    dao.save1(tdh);
                }
                currTransfer.setListDetail(listTmp);
                dao.save1(currTransfer);
                dao.commit();

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
        try {
            if (tblTransfer.getCellEditor() != null) {
                tblTransfer.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        int locationId = -1;
        if (cboFromLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboFromLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Transfer Voucher Search", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtTranDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (tblTransfer.getCellEditor() != null) {
                tblTransfer.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (Util1.getNullTo(currTransfer.isDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "Transfer voucher delete", JOptionPane.ERROR);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Transfer voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                currTransfer.setDeleted(true);
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
        System.out.println("Print");
        if (isValidEntry() && tblTransferModel.isValidEntry()) {
            //removeEmptyRow();
            try {
                dao.open();
                dao.beginTran();
                List<TransferDetailHis> listTmp = tblTransferModel.getListDetail();
                String vouNo = currTransfer.getTranVouId();
                for (TransferDetailHis tdh : listTmp) {
                    tdh.setVouNo(vouNo);
                    if (tdh.getTranDetailId() == null) {
                        tdh.setTranDetailId(vouNo + "-" + tdh.getUniqueId().toString());
                    }
                    dao.save1(tdh);
                }
                currTransfer.setListDetail(listTmp);
                dao.save1(currTransfer);
                dao.commit();

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                String strPrintVouNo = currTransfer.getTranVouId();
                deleteDetail();
                newForm();

                String reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + "rptTranVouA4";
                String compName = Util1.getPropValue("report.company.name");
                Map<String, Object> params = new HashMap();
                params.put("compName", compName);
                params.put("tran_id", strPrintVouNo);
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
            } catch (Exception ex) {
                dao.rollBack();
                log.error("print : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    public void setFocus() {
        cboFromLocation.requestFocusInWindow();
        if (lblStatus.getText().equals("NEW")) {
            genVouNo();
        }
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector(7);

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
        Date vouSaleDate = DateUtil.toDate(txtTranDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean status = true;

        if (cboFromLocation.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose from location.",
                    "No location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboFromLocation.requestFocusInWindow();
        } else if (cboToLocation.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose to location.",
                    "No location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboToLocation.requestFocusInWindow();
        } else {
            currTransfer.setTranVouId(txtVouNo.getText());
            currTransfer.setTranDate(DateUtil.toDate(txtTranDate.getText()));
            currTransfer.setFromLocation((Location) cboFromLocation.getSelectedItem());
            currTransfer.setToLocation((Location) cboToLocation.getSelectedItem());
            currTransfer.setRemark(txtRemark.getText());
            currTransfer.setVouTotal(NumberUtil.NZero(txtTotalAmount.getText()));
            currTransfer.setDeleted(Util1.getNullTo(currTransfer.isDeleted()));
            currTransfer.setCusId(txtCusId.getText());
            currTransfer.setSupId(txtSupId.getText());

            if (lblStatus.getText().equals("NEW")) {
                currTransfer.setDeleted(false);
            }

            currTransfer.setSession(Global.sessionId);

            if (lblStatus.getText().equals("NEW")) {
                currTransfer.setCreatedBy(Global.loginUser);
            } else {
                currTransfer.setUpdatedBy(Global.loginUser);
                currTransfer.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            try {
                if (tblTransfer.getCellEditor() != null) {
                    tblTransfer.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }
        }

        return status;
    }

    private void assignDefaultValue() {
        listDetail = ObservableCollections.observableList(new ArrayList<TransferDetailHis>());
        tblTransferModel.setListDetail(listDetail);

        txtTranDate.setText(DateUtil.getTodayDateStr());
    }
    private Action actionTblTransferEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblTransfer.getCellEditor() != null) {
                    tblTransfer.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblTransfer.getSelectedRow();
            int col = tblTransfer.getSelectedColumn();

            TransferDetailHis ridh = listDetail.get(row);

            if (col == 0 && ridh.getMedicineId().getMedId() != null) {
                tblTransfer.setColumnSelectionInterval(5, 5); //Move to Qty
            } else if (col == 1 && ridh.getMedicineId().getMedId() != null) {
                tblTransfer.setColumnSelectionInterval(5, 5); //Move to Qty
            } else if (col == 2 && ridh.getMedicineId().getMedId() != null) {
                tblTransfer.setColumnSelectionInterval(5, 5); //Move to Qty
            } else if (col == 3 && ridh.getMedicineId().getMedId() != null) {
                tblTransfer.setColumnSelectionInterval(5, 5); //Move to Qty
            } else if (col == 4 && ridh.getMedicineId().getMedId() != null) {
                tblTransfer.setColumnSelectionInterval(5, 5); //Move to Qty
            } else if (col == 5 && ridh.getMedicineId().getMedId() != null) {
                if ((row + 1) <= listDetail.size()) {
                    tblTransfer.setRowSelectionInterval(row + 1, row + 1);
                }
                tblTransfer.setColumnSelectionInterval(0, 0); //Move to Code
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
    };
    private Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };
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

    private Trader getTrader(String traderId, int locationId) {
        Trader cus = null;
        try {
            if (!traderId.contains("SUP")) {
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    if (!traderId.contains("CUS")) {
                        traderId = "CUS" + traderId;
                    }
                }
            }
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                List<Trader> listTrader = dao.findAllHSQL("select o from Trader o where "
                        + "o.active = true and o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ") and o.traderId = '" + traderId + "' order by o.traderName");
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                cus = (Trader) dao.find(Trader.class, traderId);
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
    }

    private void assignCustomer(Trader pTrader) {

        int locationId = -1;
        if (cboFromLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboFromLocation.getSelectedItem()).getLocationId();
        }

        Trader cus;
        if (pTrader != null) {
            cus = pTrader;
        } else {
            cus = getTrader(txtCusId.getText().trim().toUpperCase(), locationId);
        }
        if (cus == null) {
            txtCusId.setText(null);
            txtCusName.setText(null);
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Invalid customer code.",
                    "Trader Code", JOptionPane.ERROR_MESSAGE);

        } else {
            txtCusId.setText(cus.getTraderId());
            txtCusName.setText(cus.getTraderName());
        }

    }

    private void assignSupplier(Trader pTrader) {

        int locationId = -1;
        if (cboToLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboToLocation.getSelectedItem()).getLocationId();
        }

        Trader cus;
        if (pTrader != null) {
            cus = pTrader;
        } else {
            cus = getTrader(txtSupId.getText().trim().toUpperCase(), locationId);
        }
        if (cus == null) {
            txtSupId.setText(null);
            txtSupName.setText(null);
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Invalid customer code.",
                    "Trader Code", JOptionPane.ERROR_MESSAGE);

        } else {
            txtSupId.setText(cus.getTraderId());
            txtSupName.setText(cus.getTraderName());
        }

    }

    private void getCustomerList() {
        int locationId = -1;
        if (cboFromLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboFromLocation.getSelectedItem()).getLocationId();
        }
        TraderSearchDialog dialog = new TraderSearchDialog(this,
                "Customer List", locationId);
        dialog.setTitle("Customer List");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void getSupplierList() {
        int locationId = -1;
        if (cboFromLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboFromLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Supplier List", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void deleteDetail() {
        String deleteSQL;

        //All detail section need to explicity delete
        //because of save function only delete to join table
        deleteSQL = tblTransferModel.getDeleteSql();
        if (deleteSQL != null) {
            dao.execSql(deleteSQL);
        }
        //delete section end
    }

    private void assignLocation() {
        if (!isBind) {
            Location location = (Location) cboFromLocation.getSelectedItem();
            tblTransferModel.setLocation(location);
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
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTranDate = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cboToLocation = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboFromLocation = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransfer = new javax.swing.JTable(tblTransferModel);
        jPanel3 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTotalItem = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtCusId = new javax.swing.JTextField();
        txtSupId = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        txtSupName = new javax.swing.JTextField();
        butPrint = new javax.swing.JButton();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Tran Date ");

        txtTranDate.setEditable(false);
        txtTranDate.setFont(Global.textFont);
        txtTranDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTranDateMouseClicked(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("To");

        cboToLocation.setFont(Global.textFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("From");

        cboFromLocation.setFont(Global.textFont);
        cboFromLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFromLocationActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(jLabel5)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(txtVouNo)
                            .add(cboFromLocation, 0, 197, Short.MAX_VALUE))
                        .add(18, 18, 18)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(cboToLocation, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(txtTranDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(txtRemark))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(txtTranDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(cboFromLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(cboToLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblTransfer.setFont(Global.textFont);
        tblTransfer.setModel(tblTransferModel);
        tblTransfer.setRowHeight(23);
        tblTransfer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblTransferFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblTransfer);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
        );

        lblStatus.setFont(new java.awt.Font("Velvenda Cooler", 0, 40)); // NOI18N
        lblStatus.setText("DELETED");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Total :");

        txtTotalAmount.setEditable(false);
        txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAmount.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel9.setText("Total Item : ");

        txtTotalItem.setEditable(false);
        txtTotalItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTotalItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTotalAmount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(txtTotalAmount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(31, 42, Short.MAX_VALUE))
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(txtTotalItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(lblStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel4.setText("Customer ");

        jLabel8.setText("Supplier");

        txtCusId.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusIdMouseClicked(evt);
            }
        });
        txtCusId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusIdActionPerformed(evt);
            }
        });

        txtSupId.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSupIdMouseClicked(evt);
            }
        });
        txtSupId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSupIdActionPerformed(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        txtSupName.setEditable(false);
        txtSupName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSupNameMouseClicked(evt);
            }
        });

        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtCusName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtSupId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(butPrint))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtSupName)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {jLabel4, jLabel8}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.linkSize(new java.awt.Component[] {txtCusId, txtSupId}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtCusName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(txtSupId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtSupName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(butPrint)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTranDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTranDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTranDate.setText(strDate);

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtTranDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtTranDateMouseClicked

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        print();
    }//GEN-LAST:event_butPrintActionPerformed

    private void tblTransferFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblTransferFocusLost
        /*try{
            if(tblTransfer.getCellEditor() != null){
                tblTransfer.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblTransferFocusLost

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        if (txtCusId.getText() == null || txtCusId.getText().isEmpty()) {
            txtCusName.setText(null);
        } else {
            assignCustomer(null);
        }
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void txtSupIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSupIdActionPerformed
        if (txtSupId.getText() == null || txtSupId.getText().isEmpty()) {
            txtSupName.setText(null);
        } else {
            assignSupplier(null);
        }
    }//GEN-LAST:event_txtSupIdActionPerformed

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            strOption = "CUS";
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void txtSupNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSupNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            strOption = "SUP";
            getSupplierList();
        }
    }//GEN-LAST:event_txtSupNameMouseClicked

    private void txtCusIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusIdMouseClicked
        /*if (evt.getClickCount() == mouseClick) {
            strOption = "CUS";
            getCustomerList();
        }*/
    }//GEN-LAST:event_txtCusIdMouseClicked

    private void txtSupIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSupIdMouseClicked
        /*if (evt.getClickCount() == mouseClick) {
            strOption = "SUP";
            getSupplierList();
        }*/
    }//GEN-LAST:event_txtSupIdMouseClicked

    private void cboFromLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFromLocationActionPerformed
        assignLocation();
    }//GEN-LAST:event_cboFromLocationActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butPrint;
    private javax.swing.JComboBox cboFromLocation;
    private javax.swing.JComboBox cboToLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblTransfer;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSupId;
    private javax.swing.JTextField txtSupName;
    private javax.swing.JFormattedTextField txtTotalAmount;
    private javax.swing.JTextField txtTotalItem;
    private javax.swing.JFormattedTextField txtTranDate;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
