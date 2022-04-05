/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.pharmacy.database.entity.PriceChangeHis1;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PriceChangeHis;
import com.cv.app.pharmacy.database.entity.PriceChangeMedHis;
import com.cv.app.pharmacy.database.entity.PriceChangeUnitHis;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.PChangeMedTableModel;
import com.cv.app.pharmacy.ui.common.PChangeUnitTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.util.FillByPercentDialog;
import com.cv.app.pharmacy.ui.util.FromToDateDialog;
import com.cv.app.pharmacy.ui.util.MedListDialog;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.ColumnGroup;
import com.cv.app.ui.common.GroupableTableHeader;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * Damage entry use for medicine price change. This class is using in
 * PriceChangeView class as panel.
 */
public class PriceChange extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(PriceChange.class.getName());
    private final AbstractDataAccess dao = Global.dao; //Use for database access
    private GenVouNoImpl vouEngine = null; //Use for voucher number generation
    //listDetail use in tblMedicine
    private List<PriceChangeMedHis> listDetail = new ArrayList();
    //listDetailUnit use in tblUnit
    private List<PriceChangeUnitHis> listDetailUnit = new ArrayList();
    private MedicineUP medUp = new MedicineUP(dao); //Use for unit popup
    private BestAppFocusTraversalPolicy focusPolicy; //For focus managment
    private PriceChangeHis currPriceChange = new PriceChangeHis(); //Current PriceChange entry class
    //Model for tblUnit
    private PChangeUnitTableModel unitTableModel = new PChangeUnitTableModel(listDetailUnit);
    //Model for tblMedicine
    private PChangeMedTableModel medTableModel = new PChangeMedTableModel(listDetail, dao,
            medUp, this, listDetailUnit, unitTableModel);

    /**
     * Creates new form PriceChange
     */
    public PriceChange() {
        initComponents();

        //formatting txtVouNo text box.
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
        } catch (Exception ex) {
            log.error("PriceChange : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        txtDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "PriceChange", DateUtil.getPeriod(txtDate.getText()));
        genVouNo();
        addNewRow();
        initMedicineTable();
        initUnitTable();
        actionMapping();

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);

        medTableModel.setParent(tblMedicine);
        unitTableModel.setParent(tblUnit);
        unitTableModel.setAutoCalculate(chkCalculate);

        lblStatus.setText("NEW");
        lblInfo.setText("");
    }

    public PriceChange(String selectedMedId) {
        this();
        getSelectedItem(selectedMedId);
    }
    //Get latest Damage voucher serial number

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();

        txtVouNo.setText(vouNo);
    }

    //Clearing the whole form. Calling from newForm method
    private void clear() {
        txtRemark.setText("");
        lblStatus.setText("NEW");

        assignDefaultValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
        genVouNo();
        setFocus();

        System.gc();
    }

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    /*
     * Function key mapping
     */
    private void actionMapping() {
        /*
         * F3 event on tblMedicine
         * Medicine list popup dialoug will show
         */
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblMedicine.getActionMap().put("F3-Action", actionMedList);

        /*
         * F8 event on tblMedicine
         * Delete row in tblMedicine table
         */
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblMedicine.getActionMap().put("F8-Action", actionItemDelete);

        /*
         * Enter event on tblMedicine
         * For tblMedicine focus managment
         */
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblMedicine.getActionMap().put("ENTER-Action", actionTblMedEnterKey);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtDate);
        formActionKeyMapping(butPurDate);
        formActionKeyMapping(tblMedicine);
        formActionKeyMapping(chkCalculate);
        formActionKeyMapping(tblUnit);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionMedList">
    /*
     * This function will called when pressing function key F3
     * in tblMedicine.
     */
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblMedicine.getCellEditor() != null) {
                    tblMedicine.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("eactionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            }
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionItemDelete">
    /*
     * This function will called when pressing function key F8
     * in tblMedicine.
     */
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PriceChangeMedHis pcmdh;
            int yes_no = -1;

            if (tblMedicine.getSelectedRow() >= 0) {
                pcmdh = listDetail.get(tblMedicine.getSelectedRow());

                if (pcmdh.getMed().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Adjust item delete", JOptionPane.YES_NO_OPTION);
                        if (tblMedicine.getCellEditor() != null) {
                            tblMedicine.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                    if (yes_no == 0) {
                        medTableModel.delete(tblMedicine.getSelectedRow());
                    }
                }
            }
        }
    };// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMedInfo">
    /*
     * This function will called when pressing F3 or
     * entering medicine code in code field in tblMedicine.
     * When medicine code cannot find in database and 
     * medicine list popup will be show.
     */
    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                + medCode + "' and active = true");

        if (medicine != null) {
            selected("MedicineList", medicine);
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                    "Invalid.", JOptionPane.ERROR_MESSAGE);
            //getMedList(medCode);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMedList">
    /*
     * This function showing medicine list dialouge.
     */
    private void getMedList(String filter) {
        MedListDialog dialog = new MedListDialog(dao, filter);

        if (dialog.getSelect() != null) {
            selected("MedicineList", dialog.getSelect());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="selected">
    /*
     * This function will be call from when user select medicine from 
     * medicine list popup or select history data from Price Change Search 
     * dialouge.
     */
    @Override
    public void selected(Object source, Object selectObj) {
        String strSource = source.toString();

        try {
            dao.open();
            switch (strSource) {
                case "MedicineList":
                    Medicine selectedMed = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                    if (selectedMed.getRelationGroupId().size() > 0) {
                        selectedMed.setRelationGroupId(selectedMed.getRelationGroupId());
                    }
                    medUp.add(selectedMed);
                    int selectRow = tblMedicine.getSelectedRow();
                    medTableModel.setMed(selectedMed, selectRow);
                    unitTableModel.setCurrMed(selectedMed);
                    lblInfo.setText(selectedMed.getMedName());
                    break;
                case "PriceChangeVouList":
                    lblStatus.setText("EDIT");
                    String vouId = ((PriceChangeHis1) selectObj).getPriceChangeVouId();
                    currPriceChange = (PriceChangeHis) dao.find(PriceChangeHis.class, vouId);
                    txtVouNo.setText(currPriceChange.getPriceChangeVouId());
                    txtDate.setText(DateUtil.toDateStr(currPriceChange.getPriceChangeDate()));
                    txtRemark.setText(currPriceChange.getRemark());
                    if (currPriceChange.getListDetail().size() > 0) {
                        listDetail = currPriceChange.getListDetail();
                        medTableModel.setListDetail(listDetail);

                        for (PriceChangeMedHis pcm : listDetail) {
                            if (pcm.getListUnit() != null) {
                                if (pcm.getListUnit().size() > 0) {

                                }
                            }
                        }
                    }
                    break;
            }

        } catch (Exception ex) {
            log.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addNewRow">
    /*
     * Using for adding blank row for tblMedicine.
     */
    private void addNewRow() {
        PriceChangeMedHis his = new PriceChangeMedHis();

        his.setMed(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="initMedicineTable">
    /*
     * Initializing tblMedicine and set column with of the table and
     * add selection listener for selecting row event.
     */
    private void initMedicineTable() {
        tblMedicine.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name
        tblMedicine.getColumnModel().getColumn(2).setPreferredWidth(50);//Pur Price
        tblMedicine.getColumnModel().getColumn(3).setPreferredWidth(30);//Pur Unit
        tblMedicine.getColumnModel().getColumn(4).setPreferredWidth(50);//Cost Price
        tblMedicine.getColumnModel().getColumn(5).setPreferredWidth(30);//Cost Unit
        tblMedicine.getColumnModel().getColumn(6).setPreferredWidth(50);//Market Price
        tblMedicine.getColumnModel().getColumn(7).setPreferredWidth(30);//Market Unit
        tblMedicine.getColumnModel().getColumn(8).setPreferredWidth(50);//Remark

        tblMedicine.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblMedicine.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor(this));

        //Define table selection model to single row selection.
        tblMedicine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblMedicine.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tblMedicine.getSelectedRow();
                List<PriceChangeUnitHis> listPcuh;
                Medicine selectedMed;

                try {
                    listDetailUnit.removeAll(listDetailUnit);
                    unitTableModel.dataChange();
                    lblInfo.setText("");
                    if (selectedRow >= 0) {
                        listPcuh = listDetail.get(selectedRow).getListUnit();
                        selectedMed = listDetail.get(selectedRow).getMed();
                        unitTableModel.setCurrMed(selectedMed);
                        if (selectedMed != null) {
                            lblInfo.setText(selectedMed.getMedName());
                        }

                        if (listPcuh != null) {
                            listDetailUnit.addAll(listPcuh);
                        }
                    }
                } catch (IndexOutOfBoundsException ex) {
                    log.error(ex.toString());
                }
            }
        });
    }// </editor-fold>

    /*
     * This section is implementation of FormAction interface.
     * Include common function of all entry form. Those function will be
     * call from PriceChangeView.
     * Save
     * newForm
     * history
     * delete
     * deleteCopy
     * print
     */
    // <editor-fold defaultstate="collapsed" desc="FormAction Implementation">
    @Override
    public void save() {
        try {
            if (tblMedicine.getCellEditor() != null) {
                tblMedicine.getCellEditor().stopCellEditing();
            }
            if (tblUnit.getCellEditor() != null) {
                tblUnit.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (lblStatus.getText().equals("NEW")) {
            try {
                currPriceChange.setPriceChangeVouId(txtVouNo.getText());
                currPriceChange.setPriceChangeDate(DateUtil.toDate(txtDate.getText()));
                currPriceChange.setCreatedBy(Global.loginUser);
                currPriceChange.setRemark(txtRemark.getText());
                removeEmptyRow();
                currPriceChange.setListDetail(listDetail);
                dao.save(currPriceChange);

                //dao.open();
                for (PriceChangeMedHis pcmHis : listDetail) {
                    if (pcmHis.getListUnit() != null) {
                        Medicine med = (Medicine) dao.find(Medicine.class, pcmHis.getMed().getMedId());
                        List<RelationGroup> listRg = med.getRelationGroupId();
                        List<PriceChangeUnitHis> listPcUh = pcmHis.getListUnit();
                        int index = 0;

                        for (PriceChangeUnitHis pcUh : listPcUh) {
                            if (pcUh.getnPriceNew() != null) {
                                listRg.get(index).setSalePrice(pcUh.getnPriceNew());
                            }

                            if (pcUh.getaPriceNew() != null) {
                                listRg.get(index).setSalePriceA(pcUh.getaPriceNew());
                            }

                            if (pcUh.getbPriceNew() != null) {
                                listRg.get(index).setSalePriceB(pcUh.getbPriceNew());
                            }

                            if (pcUh.getcPriceNew() != null) {
                                listRg.get(index).setSalePriceC(pcUh.getcPriceNew());
                            }

                            if (pcUh.getdPriceNew() != null) {
                                listRg.get(index).setSalePriceD(pcUh.getdPriceNew());
                            }

                            index++;
                        }

                        med.setUpdatedDate(new Date());
                        List<RelationGroup> listTRG = med.getRelationGroupId();
                        if (listTRG == null) {
                            log.error("price change error : " + med.getMedId());
                        } else if (listTRG.isEmpty()) {
                            log.error("price change error : " + med.getMedId() + ", " + listTRG.size());
                        } else {
                            dao.save(med);
                        }
                    }
                }
                //dao.flush();
                //dao.commit();
                vouEngine.updateVouNo();
                clear();
            } catch (Exception ex) {
                //dao.rollBack();
                log.error("save : " + +ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        } else {
            clear();
        }
    }

    private void removeEmptyRow() {
        listDetail.remove(listDetail.size() - 1);
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Price Change Search", dao);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        System.out.println("Delete");
    }

    @Override
    public void deleteCopy() {
        System.out.println("Delete Copy");
    }

    @Override
    public void print() {
        System.out.println("Print");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="initUnitTable">
    /*
     * Initializing tblUnit and set column with of the table and
     * create group table header.
     */
    private void initUnitTable() {
        //Adjust column width
        tblUnit.getColumnModel().getColumn(0).setPreferredWidth(100);//Unit
        tblUnit.getColumnModel().getColumn(1).setPreferredWidth(140);//Cost Price
        tblUnit.getColumnModel().getColumn(2).setPreferredWidth(140);//N price old
        tblUnit.getColumnModel().getColumn(3).setPreferredWidth(140);//N price new
        tblUnit.getColumnModel().getColumn(4).setPreferredWidth(140);//A price old
        tblUnit.getColumnModel().getColumn(5).setPreferredWidth(140);//A price new
        tblUnit.getColumnModel().getColumn(6).setPreferredWidth(140);//B price old
        tblUnit.getColumnModel().getColumn(7).setPreferredWidth(140);//B price new
        tblUnit.getColumnModel().getColumn(8).setPreferredWidth(140);//C price old
        tblUnit.getColumnModel().getColumn(9).setPreferredWidth(140);//C price new
        tblUnit.getColumnModel().getColumn(10).setPreferredWidth(140);//D price old
        tblUnit.getColumnModel().getColumn(11).setPreferredWidth(140);//D price new

        tblUnit.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(7).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(9).setCellEditor(new BestTableCellEditor(this));
        tblUnit.getColumnModel().getColumn(11).setCellEditor(new BestTableCellEditor(this));

        TableColumnModel cm = tblUnit.getColumnModel();

        ColumnGroup sale_price = new ColumnGroup("Sale Price");
        sale_price.add(cm.getColumn(2));
        sale_price.add(cm.getColumn(3));

        ColumnGroup price_a = new ColumnGroup("Price A");
        price_a.add(cm.getColumn(4));
        price_a.add(cm.getColumn(5));

        ColumnGroup price_b = new ColumnGroup("Price B");
        price_b.add(cm.getColumn(6));
        price_b.add(cm.getColumn(7));

        ColumnGroup price_c = new ColumnGroup("Price C");
        price_c.add(cm.getColumn(8));
        price_c.add(cm.getColumn(9));

        ColumnGroup price_d = new ColumnGroup("Price D");
        price_d.add(cm.getColumn(10));
        price_d.add(cm.getColumn(11));

        GroupableTableHeader header = (GroupableTableHeader) tblUnit.getTableHeader();
        header.addColumnGroup(sale_price);
        header.addColumnGroup(price_a);
        header.addColumnGroup(price_b);
        header.addColumnGroup(price_c);
        header.addColumnGroup(price_d);

    }//</editor-fold>

    //Set focus to tblMedicine.
    public void setFocus() {
        tblMedicine.requestFocusInWindow();
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
     * This function calling from clear method.
     * User request newForm or after saved entry and this function was call.
     */
    private void assignDefaultValue() {
        listDetail = ObservableCollections.observableList(new ArrayList<PriceChangeMedHis>());
        medTableModel.setListDetail(listDetail);
        listDetailUnit.removeAll(listDetailUnit);

        txtDate.setText(DateUtil.getTodayDateStr());
    }
    /*
     * Focus managment for tblMedicine when user press enter key
     */
    private Action actionTblMedEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblMedicine.getCellEditor() != null) {
                    tblMedicine.getCellEditor().stopCellEditing();
                }
                int row = tblMedicine.getSelectedRow();
                int col = tblMedicine.getSelectedColumn();

                PriceChangeMedHis ddh = listDetail.get(row);

                if (col == 0 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(2, 2); //Pur Price
                } else if (col == 2 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(4, 4); //Cost Price
                } else if (col == 4 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(6, 6); //Market price
                } else if (col == 6 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setColumnSelectionInterval(8, 8); //Remark
                } else if (col == 8 && ddh.getMed().getMedId() != null) {
                    tblMedicine.setRowSelectionInterval(row + 1, row + 1);
                    tblMedicine.setColumnSelectionInterval(0, 0); //Move to Code
                }
            } catch (Exception ex) {

            }
        }
    };

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

    private void getSelectedItem(String selectedMedId) {
        String strFilter = "medId in (" + selectedMedId + ")";
        HashMap<String, String> hmLatestPur = getLatestPur(selectedMedId);
        HashMap<String, String> hmMarket = getLatestMarket(selectedMedId);
        HashMap<String, ItemUnit> hmUnit = MedicineUtil.getUnitHash(dao.findAll("ItemUnit"));
        List<Medicine> listMedicine = dao.findAll("Medicine", strFilter);

        double tmpPrice;

        listDetail.removeAll(listDetail);
        for (Medicine med : listMedicine) {
            PriceChangeMedHis pcmh = new PriceChangeMedHis();

            pcmh.setMed(med);
            medUp.add(med);

            //Latest purchase
            if (hmLatestPur.containsKey(med.getMedId())) {
                String[] strLatestPur = hmLatestPur.get(med.getMedId()).split("@");

                tmpPrice = NumberUtil.getDouble(strLatestPur[1]);
                if (tmpPrice > 0.0) {
                    pcmh.setPurchasePrice(tmpPrice);
                }

                tmpPrice = NumberUtil.getDouble(strLatestPur[2]);
                if (tmpPrice > 0.0) {
                    pcmh.setCostPrice(tmpPrice);
                }

                if (hmUnit.containsKey(strLatestPur[0])) {
                    pcmh.setPurchaseUnit(hmUnit.get(strLatestPur[0]));
                    pcmh.setCostUnit(hmUnit.get(strLatestPur[0]));
                }
            }

            //Latest market
            if (hmMarket.containsKey(med.getMedId())) {
                String[] strMarket = hmMarket.get(med.getMedId()).split("@");

                tmpPrice = NumberUtil.getDouble(strMarket[1]);
                if (tmpPrice > 0.0) {
                    pcmh.setMarketPrice(tmpPrice);
                }

                if (hmUnit.containsKey(strMarket[1])) {
                    pcmh.setMarketUnit(hmUnit.get(strMarket[1]));
                }

                if (!strMarket[2].equals("-")) {
                    pcmh.setRemark(strMarket[2]);
                }
            }

            String key = pcmh.getMed().getMedId() + "-" + pcmh.getCostUnit().getItemUnitCode();
            double smallestCost = NumberUtil.NZero(pcmh.getCostPrice()) / medUp.getQtyInSmallest(key);

            List<PriceChangeUnitHis> listUnit = new ArrayList();
            med.setRelationGroupId(medUp.getRelation(med.getMedId()));
            List<RelationGroup> listRelation = med.getRelationGroupId();

            for (RelationGroup rg : listRelation) {
                PriceChangeUnitHis pcuh = new PriceChangeUnitHis(rg.getUnitId(),
                        rg.getSalePrice(), rg.getSalePriceA(), rg.getSalePriceB(),
                        rg.getSalePriceC(), rg.getSalePriceD());

                pcuh.setCostPrice(smallestCost * rg.getSmallestQty());
                listUnit.add(pcuh);
            }

            pcmh.setListUnit(listUnit);
            listDetail.add(pcmh);
        }

        medTableModel.setListDetail(listDetail);
        dao.close();
    }

    private HashMap getLatestPur(String selectedMedId) {
        /*String strLatestPur = "select med_id, max(pur_date) last_date, pur_unit,"
                + "pur_price, pur_unit_cost from v_purchase "
                + "where deleted = false and med_id in (" + selectedMedId
                + ") group by med_id";*/
        String strLatestPur = "select vlp.med_id, vlp.pur_date last_date, vlp.pur_unit, vlp.pur_price, vlp.pur_unit_cost\n"
                + "from v_purchase vlp, (\n"
                + "		select vp.med_id, max(vp.pur_detail_id) pur_detail_id\n"
                + "		from v_purchase vp, (\n"
                + "						select med_id, max(pur_date) pur_date\n"
                + "						from v_purchase where deleted = false and med_id in (" + selectedMedId + ")\n"
                + "						group by med_id) mpd\n"
                + "		where vp.med_id = mpd.med_id and vp.pur_date = mpd.pur_date\n"
                + "		and vp.deleted = false and vp.med_id in (" + selectedMedId + ")) vpid\n"
                + "where vlp.med_id = vpid.med_id and vlp.pur_detail_id = vpid.pur_detail_id";
        ResultSet rsLatestPur = dao.execSQL(strLatestPur);
        HashMap<String, String> hmLatestPur = new HashMap();

        try {
            while (rsLatestPur.next()) {
                String strMedId = rsLatestPur.getString("med_id");
                String strPurUnit = Util1.getString(rsLatestPur.getString("pur_unit"), "-");
                String strPurPrice = Util1.getString(rsLatestPur.getString("pur_price"), "0");
                String strUnitCost = Util1.getString(rsLatestPur.getString("pur_unit_cost"), "0");

                hmLatestPur.put(strMedId, strPurUnit + "@" + strPurPrice
                        + "@" + strUnitCost);
            }
        } catch (Exception ex) {
            log.error("getLastPur : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.closeStatment();
        }

        return hmLatestPur;
    }

    private HashMap getLatestMarket(String selectedMedId) {
        String strLatestMarket = "select med_id, max(change_date) last_date, "
                + "market_price, market_unit, remark_med "
                + "from v_price_change_med where med_id in (" + selectedMedId
                + ") group by med_id";
        ResultSet rsLatestMarket = dao.execSQL(strLatestMarket);
        HashMap<String, String> hmLatestMarket = new HashMap();

        try {
            while (rsLatestMarket.next()) {
                String strMedId = rsLatestMarket.getString("med_id");
                String strMarketPrice = Util1.getString(rsLatestMarket.getString("market_price"), "0");
                String strMarketUnit = Util1.getString(rsLatestMarket.getString("market_unit"), "-");
                String strRemark = Util1.getString(rsLatestMarket.getString("remark_med"), "-");

                hmLatestMarket.put(strMedId, strMarketPrice + "@" + strMarketUnit
                        + "@" + strRemark);
            }
        } catch (Exception ex) {
            log.error("getLastMarket : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.closeStatment();
        }

        return hmLatestMarket;
    }

    private void fillByPurDate() {
        FromToDateDialog dialog = new FromToDateDialog("Purchase Date");

        if (dialog.isSelected()) {
            String strSql = "select distinct med_id"
                    + " from v_purchase where date(pur_date) between '"
                    + DateUtil.toDateStrMYSQL(dialog.getFromDate())
                    + "' and '" + DateUtil.toDateStrMYSQL(dialog.getToDate()) + "'";
            String selectedMedId = null;
            ResultSet rs = dao.execSQL(strSql);

            try {
                while (rs.next()) {
                    if (selectedMedId == null) {
                        selectedMedId = "'" + rs.getString("med_id") + "'";
                    } else {
                        selectedMedId = selectedMedId + ",'" + rs.getString("med_id") + "'";
                    }
                }
            } catch (Exception ex) {
                log.error("fillByPurDate : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.closeStatment();
            }

            if (selectedMedId != null) {
                getSelectedItem(selectedMedId);
            }
        }
    }

    private void fillByPercent() {
        FillByPercentDialog dialog = new FillByPercentDialog();

        if (dialog.isSelected()) {
            String strSql = "select med_id from medicine ";
            String filter = null;

            if (dialog.getItemType() != null) {
                if (filter == null) {
                    filter = "med_type_id = '" + dialog.getItemType() + "'";
                } else {
                    filter = filter + " and med_type_id = '" + dialog.getItemType() + "'";
                }
            }

            if (dialog.getCategory() != 0) {
                if (filter == null) {
                    filter = "category_id = " + dialog.getCategory();
                } else {
                    filter = filter + " and category_id = " + dialog.getCategory();
                }
            }

            if (dialog.getBrand() != 0) {
                if (filter == null) {
                    filter = "brand_id = " + dialog.getBrand();
                } else {
                    filter = filter + " and brand_id = " + dialog.getBrand();
                }
            }

            if (filter != null) {
                strSql = strSql + " where " + filter;
            }

            String selectedMedId = null;
            ResultSet rs = dao.execSQL(strSql);

            try {
                while (rs.next()) {
                    if (selectedMedId == null) {
                        selectedMedId = "'" + rs.getString("med_id") + "'";
                    } else {
                        selectedMedId = selectedMedId + ",'" + rs.getString("med_id") + "'";
                    }
                }
            } catch (Exception ex) {
                log.error("fillByPercent : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.closeStatment();
            }

            if (selectedMedId != null) {
                getSelectedItem(selectedMedId);
            }

            double percent = dialog.getPercent();
            if (percent > 0.0) {
                fillPercent(percent);
            }
        }
    }

    private void fillPercent(double percent) {
        int count = 0;
        double percentValue = percent / 100;
        double incValue;

        for (PriceChangeMedHis pcmh : listDetail) {
            if (count < listDetail.size() - 1) {
                List<PriceChangeUnitHis> listUnit = pcmh.getListUnit();

                for (PriceChangeUnitHis pcuh : listUnit) {
                    incValue = NumberUtil.NZero(pcuh.getnPriceOld())
                            + NumberUtil.NZero(pcuh.getnPriceOld()) * percentValue;
                    pcuh.setnPriceNew(incValue);

                    incValue = NumberUtil.NZero(pcuh.getaPriceOld())
                            + NumberUtil.NZero(pcuh.getaPriceOld()) * percentValue;
                    pcuh.setaPriceNew(incValue);

                    incValue = NumberUtil.NZero(pcuh.getbPriceOld())
                            + NumberUtil.NZero(pcuh.getbPriceOld()) * percentValue;
                    pcuh.setbPriceNew(incValue);

                    incValue = NumberUtil.NZero(pcuh.getcPriceOld())
                            + NumberUtil.NZero(pcuh.getcPriceOld()) * percentValue;
                    pcuh.setcPriceNew(incValue);

                    incValue = NumberUtil.NZero(pcuh.getdPriceOld())
                            + NumberUtil.NZero(pcuh.getdPriceOld()) * percentValue;
                    pcuh.setdPriceNew(incValue);
                }
            }

            count++;
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
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(medTableModel);
        lblInfo = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUnit = new javax.swing.JTable(){
            protected JTableHeader createDefaultTableHeader() {
                return new GroupableTableHeader(columnModel);
            }
        };
        chkCalculate = new javax.swing.JCheckBox();
        butPurDate = new javax.swing.JButton();
        butPercent = new javax.swing.JButton();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date ");

        txtDate.setEditable(false);
        txtDate.setFont(Global.textFont);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);

        tblMedicine.setFont(Global.textFont);
        tblMedicine.setRowHeight(23);
        tblMedicine.setVerifyInputWhenFocusTarget(false);
        tblMedicine.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblMedicineFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblMedicine);

        lblInfo.setFont(Global.lableFont);
        lblInfo.setText("Info");

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("Edit");

        tblUnit.setFont(Global.textFont);
        tblUnit.setModel(unitTableModel);
        tblUnit.setRowHeight(23);
        tblUnit.setShowVerticalLines(false);
        tblUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblUnitFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(tblUnit);

        chkCalculate.setFont(Global.lableFont);
        chkCalculate.setText("Calculate");

        butPurDate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        butPurDate.setText("Pur-Date");
        butPurDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPurDateActionPerformed(evt);
            }
        });

        butPercent.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        butPercent.setText("Percent");
        butPercent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPercentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkCalculate))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtRemark))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(butPurDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(butPercent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butPercent))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butPurDate))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkCalculate)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDate.setText(strDate);

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtDateMouseClicked

    private void butPurDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPurDateActionPerformed
        fillByPurDate();
    }//GEN-LAST:event_butPurDateActionPerformed

    private void butPercentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPercentActionPerformed
        fillByPercent();
    }//GEN-LAST:event_butPercentActionPerformed

    private void tblMedicineFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblMedicineFocusLost
        /*try{
            if(tblMedicine.getCellEditor() != null){
                tblMedicine.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblMedicineFocusLost

    private void tblUnitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblUnitFocusLost
        /*try{
            if(tblUnit.getCellEditor() != null){
                tblUnit.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblUnitFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butPercent;
    private javax.swing.JButton butPurDate;
    private javax.swing.JCheckBox chkCalculate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTable tblUnit;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
