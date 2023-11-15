/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.common.PBPharmacyTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PurchaseIMEINo;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.SaleDetailHis;

import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleStockTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.util.MedPriceAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.pharmacy.util.StockList;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.util.ZPLUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author cv-svr
 */
public class ZPLPrint extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate, KeyListener {

    private final AbstractDataAccess dao = Global.dao;
    private SaleStockTableModel stockTableModel = new SaleStockTableModel();
    static Logger log = Logger.getLogger(ZPLPrint.class.getName());
    private List<SaleDetailHis> listDetail = ObservableCollections.observableList(new ArrayList());
    private final SaleTableCodeCellEditor codeCellEditor = new SaleTableCodeCellEditor(dao);
    private MedicineUP medUp = new MedicineUP(dao);
    private PBPharmacyTableModel pharmacyTableModel = new PBPharmacyTableModel(listDetail, dao,
            medUp, this, this);
    private StockList stockList = new StockList(dao, medUp);

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Creates new form ZPLPrint
     */
    public ZPLPrint() {
        initComponents();
        initPharmacyTable();
        initPrinter();
        initCommandText();
        initPharmacyCommandText();
        addNewRow();
        actionMapping();
        pharmacyTableModel.setParent(tblPharmacy);
        pharmacyTableModel.setStockTableModel(stockTableModel);
    }

    private void initCommandText() {
        txtRegZPLCode.setText("^XA\n"
                + "\n"
                + "^FX Info Section\n"
                + "^CF0,20\n"
                + "^FO10,10^FD       Name : ^FS\n"
                + "^FO115,10^FDPatient#1^FS\n"
                + "^FO10,40^FD         DOB : ^FS\n"
                + "^FO115,40^FD10/07/1999^FS\n"
                + "^FO10,70^FD    Address : ^FS\n"
                + "^FO115,70^FDYangon^FS\n"
                + "^FO9,100^FD Phone No. : ^FS\n"
                + "^FO115,100^FD09979658665^FS\n"
                + "\n"
                + "^FX Bar Code Section\n"
                + "^BY4,2,100\n"
                + "^FO10,140^BC^FD12345678^FS\n"
                + "\n"
                + "^XZ");
    }

    private void initPharmacyCommandText() {
        txtPharZPLCode.setText("^XA\n"
                + "^FX Bar Code Section\n"
                + "^BY1,1,80\n"
                + "^FO40,10^BCC^FDcoreBarCode^FS\n"
                + "^XZ");
    }

    private void initPrinter() {
        List<String> listPrinter = ZPLUtil.getPrinter();
        BindingUtil.BindCombo(cboPrinter, listPrinter);
    }

    private void getPatient() {
        if (txtRegNo.getText() != null && !txtRegNo.getText().trim().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class, txtRegNo.getText().trim());
                dao.close();

                if (pt == null) {
                    txtRegNo.setText(null);
                    txtDOB.setText(null);
                    txtAge.setText(null);
                    txtPtName.setText(null);
                    txtAddress.setText(null);
                    txtPhone.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    txtRegNo.setText(pt.getRegNo());
                    txtDOB.setText(DateUtil.toDateStr(pt.getDob()));
                    txtAge.setText(DateUtil.getAge(txtDOB.getText()));
                    txtPtName.setText(pt.getPatientName());
                    txtAddress.setText(pt.getAddress());
                    txtPhone.setText(pt.getContactNo());
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtRegNo.setText(null);
            txtDOB.setText(null);
            txtAge.setText(null);
            txtPtName.setText(null);
            txtAddress.setText(null);
            txtPhone.setText(null);
        }
    }

    private void regClear() {
        txtRegNo.setText(null);
        txtDOB.setText(null);
        txtAge.setText(null);
        txtPtName.setText(null);
        txtAddress.setText(null);
        txtPhone.setText(null);
    }

    private void addNewRow() {
        SaleDetailHis his = new SaleDetailHis();
        his.setMedId(new Medicine());
        listDetail.add(his);
    }

    private void pharPrint() {
       
         if (txtPharZPLCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "You need to configure command text.",
                    "Command Text Blank", JOptionPane.ERROR_MESSAGE);
            return;
        }
         String selPrinter = cboPrinter.getSelectedItem().toString();
        
         String[] barCodeLst = pharmacyTableModel.getMedIdList().split(",");
          
         
        for (String code : barCodeLst) {
         try {
//            String width = "30 mm";
//            String height = "20 mm";
//            String c = code.replace("\'", "");
//            String printZplData = txtPharZPLCode.getText().replace("coreBarCode", c);
//            ZPLUtil.printZpl(printZplData, selPrinter, width, height);
//              ZPLUtil.ZPLPrinter();

            String zplData = "^XA^FO50,50^BCN,100,Y,N,N^FD123456^FS^XZ"; // Example ZPL data
            String printerName = selPrinter; // Replace with your printer's name
            String paperWidth = "4.0 in"; // Replace with your paper width
            String paperHeight = "6.0 in"; // Replace with your paper height

            ZPLUtil.printZpl(zplData, printerName, paperWidth, paperHeight);
            
            
        } catch (Exception ex) {
              JOptionPane.showMessageDialog(Util1.getParent(),selPrinter + " " + ex.getMessage(),
                    "Command Text Blank", JOptionPane.ERROR_MESSAGE);
            log.error("regPrint : selPrinter : " + selPrinter + " " + ex.getMessage());
        }
       }
    
   
    }

    private void regPrint() {
        if (txtRegZPLCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "You need to configure command text.",
                    "Command Text Blank", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (txtRegNo.getText().trim().isEmpty() || txtPtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid patient info.",
                    "Invalid Patient", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selPrinter = cboPrinter.getSelectedItem().toString();
        try {
            String width = "80 mm";
            String height = "40 mm";
            ZPLUtil.printZpl(txtRegZPLCode.getText().trim(), selPrinter, width, height);
        } catch (Exception ex) {
            log.error("regPrint : selPrinter : " + selPrinter + " " + ex.getMessage());
        }
    }

    private void initPharmacyTable() {
        tblPharmacy.setModel(pharmacyTableModel);
        try {
            if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
                tblPharmacy.setCellSelectionEnabled(true);
            }
            tblPharmacy.getTableHeader().setReorderingAllowed(false);
            tblPharmacy.getTableHeader().setFont(Global.lableFont);
            tblPharmacy.setFont(Global.textFont);
            tblPharmacy.setRowHeight(Global.rowHeight);
            tblPharmacy.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            //Adjust column width
            tblPharmacy.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
            tblPharmacy.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name          
            tblPharmacy.getColumnModel().getColumn(2).setPreferredWidth(30);//Qty
            tblPharmacy.getColumnModel().getColumn(3).setPreferredWidth(60);//Sale price       
            addPharmacyTableModelListener();
            tblPharmacy.getColumnModel().getColumn(0).setCellEditor(codeCellEditor);
            tblPharmacy.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
//            tblPharmacy.getColumnModel().getColumn(4).setCellEditor(new ZPLPrint.PharmacyTableUnitCellEditor());

            tblPharmacy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblPharmacy.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {

                if (tblPharmacy.getSelectedRow() < pharmacyTableModel.getRowCount()) {
                    int row = tblPharmacy.convertRowIndexToModel(tblPharmacy.getSelectedRow());

                    Object tmp = tblPharmacy.getValueAt(tblPharmacy.getSelectedRow(), 0);
                    if (tmp != null) {
                        String selectMedId = tmp.toString();
                        List<Stock> listStock = stockList.getStockList(selectMedId);
                        if (listStock == null) {
                            listStock = new ArrayList();
                        }
                        stockTableModel.setListStock(listStock);
                    } else {
                        List<Stock> listStock = new ArrayList();
                        stockTableModel.setListStock(listStock);
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initPharmacyTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addPharmacyTableModelListener() {
        tblPharmacy.getModel().addTableModelListener((TableModelEvent e) -> {
            int column = e.getColumn();

            if (column >= 0) {
                switch (column) {
                    case 0: //Code
                    case 2: //Qty
                    case 3: //Sale price                  
//                    case 8: //Charge type
//                        calculateTotalAmount();
                        break;
                }
            }
        });
    }

    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine;
        PurchaseIMEINo purimeino;
        ResultSet rs;

        try {

            if (Util1.getPropValue("system.check.imei").equals("Y")) {
                String strSQL = "select * from v_sale where deleted='false' and ( imei1='" + medCode
                        + "' or imei2='" + medCode + "' or sd_no ='" + medCode + "')";
                rs = dao.execSQL(strSQL);

                if (rs.next()) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "This Item is already saled.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

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
                        String strSql = "select distinct o from Medicine o join o.relationGroupId r "
                                + "where r.unitBarcode = '" + medCode + "'";
                        List<Medicine> listMed = dao.findAllHSQL(strSql);
                        if (listMed != null) {
                            if (!listMed.isEmpty()) {
                                medicine = listMed.get(0);
                                if (!medicine.getRelationGroupId().isEmpty()) {
                                    List<RelationGroup> listRG = medicine.getRelationGroupId();
                                    medicine.setRelationGroupId(listRG);

                                    for (int i = 0; i < listRG.size(); i++) {
                                        RelationGroup rg = listRG.get(i);
                                        if (rg.getUnitBarcode() != null) {
                                            if (rg.getUnitBarcode().equals(medCode)) {
                                                pharmacyTableModel.setStrBarcodeUnit(rg.getUnitId().getItemUnitCode());
                                                i = listRG.size();
                                            }
                                        }
                                    }
                                }
                                selected("MedicineList", medicine);
                            } else {
                                strSql = "select distinct o from Medicine o join o.relationGroupId r "
                                        + "where concat(o.medId,r.relUniqueId) = '" + medCode + "'";
                                listMed = dao.findAllHSQL(strSql);

                                if (listMed != null) {
                                    if (!listMed.isEmpty()) {
                                        medicine = listMed.get(0);
                                        if (medicine.getRelationGroupId().size() > 0) {
                                            List<RelationGroup> listRG = medicine.getRelationGroupId();
                                            medicine.setRelationGroupId(listRG);

                                            for (int i = 0; i < listRG.size(); i++) {
                                                RelationGroup rg = listRG.get(i);
                                                String key = medicine.getMedId() + rg.getRelUniqueId().toString();
                                                if (key.equals(medCode)) {
                                                    pharmacyTableModel.setStrBarcodeUnit(rg.getUnitId().getItemUnitCode());
                                                    i = listRG.size();
                                                }
                                            }
                                        }
                                        selected("MedicineList", medicine);
                                    } else {
                                        purimeino = (PurchaseIMEINo) dao.find("PurchaseIMEINo", "imei1 = '"
                                                + medCode + "' or imei2 = '" + medCode + "'");

                                        if (purimeino != null) {
                                            selected("PurIMEINoList", purimeino);
                                        } else {
                                            /*JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Item code.",
                                                    "Invalid.", JOptionPane.ERROR_MESSAGE);*/
 /*try {
                                                if (tblSale.getCellEditor() != null) {
                                                    tblSale.getCellEditor().stopCellEditing();
                                                }
                                            } catch (Exception ex) {

                                            }*/
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                log.info("getMedInfo : Blank medicine code.");
            }

        } catch (Exception e) {
            log.error("getMedInfo : " + e.getMessage());
        }

    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "MedicineList":
                Medicine med;

                try {
                    dao.open();

                    if (selectObj instanceof Medicine) {
                        med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                    } else if (selectObj instanceof VMedicine1) {
                        String medId = ((VMedicine1) selectObj).getMedId();
                        med = (Medicine) dao.find(Medicine.class, medId);
                    } else {
                        dao.close();
                        return;
                    }
                   
                    List<RelationGroup> listRel = med.getRelationGroupId();
                    med.setRelationGroupId(listRel);
                    List<Stock> listStock = null;
              
                    if (!listRel.isEmpty()) {
                        medUp.add(med);
                        int selectRow = tblPharmacy.getSelectedRow();
                        if (Util1.getPropValue("system.app.sale.musthavestock").equals("Y")) {
                            if (listStock != null) {
                                  log.info("selected MedicineList : 21" + selectObj.toString());
//                                if (!listStock.isEmpty()) {
//                                    Integer locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
//                                    if (isOutOfStock(listStock, locationId)) {
//                                        JOptionPane.showMessageDialog(Util1.getParent(),
//                                                "No stock to sell (" + med.getMedId() + "-" + med.getMedName() + ")",
//                                                "No Stock", JOptionPane.ERROR_MESSAGE);
//                                        stockTableModel.setListStock(new ArrayList());
//                                    } else {
//                                        saleTableModel.setMed(med, selectRow, stockList);
//                                        //Calculate total items of voucher
//                                        txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
//                                    }
//                                } else {
//                                    JOptionPane.showMessageDialog(Util1.getParent(),
//                                            "No stock to sell (" + med.getMedId() + "-" + med.getMedName() + ")",
//                                            "No Stock", JOptionPane.ERROR_MESSAGE);
//                                    stockTableModel.setListStock(new ArrayList());
//                                }
                                pharmacyTableModel.setMed(med, selectRow, stockList);
                             
                            } else {
                                JOptionPane.showMessageDialog(Util1.getParent(),
                                        "No stock to sell (" + med.getMedId() + "-" + med.getMedName() + ")",
                                        "No Stock", JOptionPane.ERROR_MESSAGE);
                                pharmacyTableModel.getMedIdList();
                            }
                        } else {
                                 
                                 //setMed have problem
                                 pharmacyTableModel.setMed(med, selectRow, stockList);
                                
                         
                            //Calculate total items of voucher
//                            txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
                        }
                    } else {
                        System.out.println("Sale.select MedicineList : Cannot get relation group");
                    }
                } catch (Exception ex) {
                    log.error("selected MedicineList : " + selectObj.toString()
                            + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                } finally {
                    dao.close();
                }
                break;

            case "SaleQtyUpdate":
                //Update to stock balance list
//                if (lblStatus.getText().equals("NEW")) {
                SaleDetailHis sdh = (SaleDetailHis) selectObj;
                String entity;
                String strSQL;
                List<Stock> list = null;

                if (sdh.getMedId().getMedId() != null) {
                    entity = "com.cv.app.pharmacy.database.helper.Stock";
                    strSQL = "SELECT * FROM " + entity + " where med.medId = '"
                            + sdh.getMedId().getMedId() + "' and expDate = "
                            + sdh.getExpireDate();
                    list = JoSQLUtil.getResult(strSQL,
                            stockList.getStockList(sdh.getMedId().getMedId()));
                }

                if (list != null) {
                    if (list.size() > 0) {
                        for (Stock stock : list) {
                            stock.setQtyStrDeman(sdh.getQuantity() + sdh.getUnitId().getItemUnitCode());
                            stock.setUnit(sdh.getUnitId());
                            stock.setUnitQty(sdh.getQuantity());

                            if (NumberUtil.NZeroInt(sdh.getFocQty()) != 0) {
                                stock.setFocQtyStr(sdh.getFocQty() + sdh.getFocUnit().getItemUnitCode());
                                stock.setFocUnit(sdh.getFocUnit());
                                stock.setFocUnitQty(sdh.getFocQty());
                            }

                            float demanQtySmall = 0;
                            float focQtySmall = 0;
                            String demanKey = null;
                            String focKey = null;

                            if (stock.getUnit() != null) {
                                demanKey = stock.getMed().getMedId() + "-"
                                        + stock.getUnit().getItemUnitCode();
                            }

                            if (stock.getFocUnit() != null) {
                                focKey = stock.getMed().getMedId() + "-"
                                        + stock.getFocUnit().getItemUnitCode();
                            }

                            if (demanKey != null) {
                                demanQtySmall = medUp.getQtyInSmallest(demanKey);
                            }

                            if (focKey != null) {
                                focQtySmall = medUp.getQtyInSmallest(focKey);
                            }

                            float balance = NumberUtil.NZeroInt(stock.getQtySmallest())
                                    - ((NumberUtil.NZeroFloat(stock.getUnitQty()) * demanQtySmall)
                                    + (NumberUtil.NZeroInt(stock.getFocUnitQty()) * focQtySmall));

                            stock.setQtyStrBal(MedicineUtil.getQtyInStr(sdh.getMedId(),
                                    balance));
                        }
                    }
//                    }
                }
                break;
        }
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void newForm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void history() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteCopy() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void keyEvent(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private class PharmacyTableUnitCellEditor extends javax.swing.AbstractCellEditor implements TableCellEditor {

        JComponent component = null;
        int colIndex = -1;
        private Object oldValue;
        // This method is called when a cell value is edited by the user.

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            oldValue = value;
            colIndex = vColIndex;

            if (isSelected) {
                // cell (and perhaps other cells) are selected
            }

            String medId = listDetail.get(rowIndex).getMedId().getMedId();

            try {
                switch (vColIndex) {
                    case 0: //Code column
                        JTextField code = new JTextField();
                        component = code;
                        if (value != null) {
                            ((JTextField) component).setText(value.toString());
                        }
                        ((JTextField) component).selectAll();
                        break;
                    case 5: //Unit column
                        JComboBox jb = new JComboBox();

                        BindingUtil.BindCombo(jb, medUp.getUnitList(medId));
                        component = jb;
                        break;
                    case 6: //Price column
                        JTextField jtf = new JTextField();
                        component = jtf;
                        if (value != null) {
                            ((JTextField) component).setText(value.toString());
                        }
                        ((JTextField) component).selectAll();

                        SaleDetailHis sdh = listDetail.get(rowIndex);
                        if (sdh != null) {
                            if (sdh.getUnitId() != null) {
                                String unit = sdh.getUnitId().getItemUnitCode();
                                MedPriceAutoCompleter completer = new MedPriceAutoCompleter(jtf,
                                        medUp.getPriceList(medId + "-" + unit), this);
                            }
                        }
                        break;
                }
            } catch (Exception ex) {
                log.error("getTableCellEditorComponent : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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
            Object obj = null;

            if (component instanceof JComboBox) {
                obj = ((JComboBox) component).getSelectedItem();
            } else if (component instanceof JTextField) {
                obj = ((JTextField) component).getText();
            }

            switch (colIndex) {
                case 0: //Code
                    if (obj != null) {
                        getMedInfo(obj.toString());
                    }

                    tblPharmacy.setColumnSelectionInterval(0, 0);
                    break;
            }

            return obj;
        }

        /*
         * To prevent mouse click cell editing
         */
        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void actionMapping() {
        //F3 event on tblSale
        tblPharmacy.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblPharmacy.getActionMap().put("F3-Action", actionMedList);

        formActionKeyMapping(tblPharmacy);

    }

    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };

    private Action actionTblSaleEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblPharmacy.getCellEditor() != null) {
                    tblPharmacy.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            if (!Util1.getPropValue("system.sale.barcode").equals("Y")) {
                int row = tblPharmacy.getSelectedRow();
                int col = tblPharmacy.getSelectedColumn();

                SaleDetailHis sdh = listDetail.get(row);

                if (col == 0 && sdh.getMedId().getMedId() != null) {
                    tblPharmacy.setColumnSelectionInterval(4, 4); //Move to Qty
                } else if (col == 1 && sdh.getMedId().getMedId() != null) {
                    tblPharmacy.setColumnSelectionInterval(4, 4); //Move to Qty
                } else if (col == 2 && sdh.getMedId().getMedId() != null) {
                    tblPharmacy.setColumnSelectionInterval(4, 4); //Move to Qty
                } else if (col == 3 && sdh.getMedId().getMedId() != null) {
                    tblPharmacy.setColumnSelectionInterval(4, 4); //Move to Qty               
                } else if (col == 10 && sdh.getMedId().getMedId() != null) {
                    if ((row + 1) <= listDetail.size()) {
                        tblPharmacy.setRowSelectionInterval(row + 1, row + 1);
                    }
                    tblPharmacy.setColumnSelectionInterval(0, 0); //Move to Code
                }
            }
        }
    };

    private void formActionKeyMapping(JComponent jc) {

    }

//    @Override
//    public void print() {
//        if (isValidEntry() && saleTableModel.isValidEntry() && expTableModel.isValidEntry()) {
//            Date vouSaleDate = DateUtil.toDate(txtSaleDate.getText());
//            Date lockDate = PharmacyUtil.getLockDate(dao);
//            boolean isDataLock = false;
//            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
//                isDataLock = true;
//            }
//
//            boolean status = false;
//            boolean iAllow = true;
//
//            if (lblStatus.getText().equals("EDIT")) {
//                if (!Util1.hashPrivilege("SaleEditVoucherChange")) {
//                    iAllow = false;
//                }
//            }
//            if (iAllow == true) {
//                if (chkAmount.isSelected()) {
//                    SaleConfirmDialog1 dialog = new SaleConfirmDialog1(currSaleVou,
//                            NumberUtil.NZero(txtSaleLastBalance.getValue()), dao,
//                            "Print");
//                    status = dialog.getConfStatus();
//                }
//
//                if (status || !chkAmount.isSelected()) {
//                    //removeEmptyRow();
//                    //currSaleVou.setSaleDetailHis(listDetail);
//                    //currSaleVou.setSaleDetailHis(saleTableModel.getListDetail());
//                    //currSaleVou.setExpense(listExpense);
//
//                    log.error("print() start : " + currSaleVou.getSaleInvId());
//                    //For BK Pagolay
//                    try {
//                        Date d = new Date();
//                        dao.execProc("bksale",
//                                currSaleVou.getSaleInvId(),
//                                DateUtil.toDateTimeStrMYSQL(d),
//                                Global.loginUser.getUserId(),
//                                Global.machineId,
//                                currSaleVou.getVouTotal().toString(),
//                                currSaleVou.getDiscount().toString(),
//                                currSaleVou.getPaid().toString(),
//                                currSaleVou.getBalance().toString());
//                    } catch (Exception ex) {
//                        log.error("bksale : " + currSaleVou.getSaleInvId() + " - " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
//                    } finally {
//                        dao.close();
//                    }
//                    log.error("print() after bksale : " + currSaleVou.getSaleInvId());
//                    /*txtCusLastBalance.setValue(
//                            (NumberUtil.getDouble(txtCusLastBalance.getText())
//                            + NumberUtil.getDouble(currSaleVou.getBalance()))
//                            + tranTableModel.getTotal()
//                    );*/
//                    try {
//                        if (!isDataLock) {
//                            if (canEdit) {
//                                String vouNo = currSaleVou.getSaleInvId();
//                                List<SaleDetailHis> tmpListSDH = saleTableModel.getListDetail();
//                                //dao.open();
//                                //dao.beginTran();
//                                int ttlItem = 0;
//                                int fttlItem = NumberUtil.NZeroInt(txtTotalItem.getText());
//                                if (tmpListSDH != null) {
//                                    for (SaleDetailHis sdh : tmpListSDH) {
//                                        sdh.setVouNo(vouNo);
//                                        if (sdh.getSaleDetailId() == null) {
//                                            sdh.setSaleDetailId(vouNo + "-" + sdh.getUniqueId().toString());
//                                        }
//                                        dao.save(sdh);
//                                        ttlItem++;
//                                    }
//                                }
//                                log.error("print() after save sale detail his : " + currSaleVou.getSaleInvId() + " - " + tmpListSDH.size());
//                                if (ttlItem != (fttlItem - 1)) {
//                                    log.error("Error in total item, Vou No : " + currSaleVou.getSaleInvId()
//                                            + " List Items Total : " + fttlItem + " Save Items Total : " + ttlItem);
//                                }
//                                //currSaleVou.setSaleDetailHis(tmpListSDH);
//
//                                listExpense = expTableModel.getListExpense();
//                                if (listExpense != null) {
//                                    for (SaleExpense se : listExpense) {
//                                        if (se.getExpType() != null) {
//                                            if (se.getVouNo() == null) {
//                                                se.setVouNo(vouNo);
//                                                se.setSaleExpenseId(vouNo + "-" + se.getUniqueId().toString());
//                                            }
//                                            dao.save(se);
//                                        }
//                                    }
//                                }
//                                //currSaleVou.setExpense(listExpense);
//                                log.error("print() after sale expense save : " + currSaleVou.getSaleInvId());
//                                List<SaleOutstand> listOuts = getOutstandingItem();
//                                if (listOuts != null) {
//                                    for (SaleOutstand so : listOuts) {
//                                        if (so.getVouNo() == null) {
//                                            so.setVouNo(vouNo);
//                                            so.setOutsId(vouNo + "-" + so.getItemId().getMedId());
//                                        }
//                                        dao.save(so);
//                                    }
//                                }
//                                //currSaleVou.setListOuts(listOuts);
//                                log.error("print() after SaleOutstand save : " + currSaleVou.getSaleInvId());
//                                List<SaleWarranty> listWarranty = currSaleVou.getWarrandy();
//                                if (listWarranty != null) {
//                                    for (SaleWarranty sw : listWarranty) {
//                                        sw.setVouNo(vouNo);
//                                        dao.save(sw);
//                                    }
//                                }
//                                log.error("print() after SaleWarrany save : " + currSaleVou.getSaleInvId());
//                                dao.save(currSaleVou);
//                                //dao.commit();
//                                //dao.save(currSaleVou);
//                                if (lblStatus.getText().equals("NEW")) {
//                                    vouEngine.updateVouNo();
//                                }
//                                updatePayment();
//                                deleteDetail();
//                                updateVouTotal(currSaleVou.getSaleInvId());
//                                //Upload to Account
//                                uploadToAccount(currSaleVou.getSaleInvId());
//                                log.error("print() after uploadToAccount : " + currSaleVou.getSaleInvId());
//                            }
//                        }
//                        String traderId = Util1.getPropValue("system.sale.general.customer");
//                        if (currSaleVou.getCustomerId() != null) {
//                            if (!currSaleVou.getCustomerId().getTraderId().equals(traderId)) {
//                                TraderTransaction tt = new TraderTransaction();
//                                tt.setAmount(
//                                        NumberUtil.getDouble(txtSaleLastBalance.getText())
//                                        + NumberUtil.getDouble(currSaleVou.getBalance())
//                                        + tranTableModel.getTotal()
//                                );
//                                tt.setUserId(Global.machineId);
//                                //tt.setTranOption("Last Balance : ");
//                                if (tt.getAmount() < 0) {
//                                    tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalanceM"));
//                                } else {
//                                    tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalance"));
//                                }
//                                tt.setTranDate(currSaleVou.getSaleDate());
//                                tt.setTranType("N");
//                                tt.setSortId(3);
//                                tt.setMachineId(Global.machineId);
//                                dao.save(tt);
//
//                                //For Expense
//                                //List<SaleExpense> listExpense = expTableModel.getListExpense();
//                                if (listExpense != null) {
//                                    if (!listExpense.isEmpty()) {
//                                        for (SaleExpense se : listExpense) {
//                                            tt = new TraderTransaction();
//                                            tt.setAmount(se.getExpAmount());
//                                            tt.setUserId(Global.machineId);
//                                            //tt.setTranOption("Current Vou : ");
//                                            tt.setTranOption(se.getExpType().getExpenseName());
//                                            tt.setTranDate(currSaleVou.getSaleDate());
//                                            tt.setTranType("N");
//                                            tt.setSortId(2);
//                                            tt.setMachineId(Global.machineId);
//
//                                            dao.save(tt);
//                                        }
//                                    }
//                                }
//                                /*tt = new TraderTransaction();
//                                tt.setAmount(currSaleVou.getBalance());
//                                tt.setUserId(Global.loginUser.getUserId());
//                                //tt.setTranOption("Current Vou : ");
//                                tt.setTranOption(Util1.getPropValue("system.app.sale.currvoubal"));
//                                tt.setTranDate(currSaleVou.getSaleDate());
//                                tt.setTranType("N");
//                                tt.setSortId(2);
//                                tt.setMachineId(Global.machineId);
//
//                                dao.save(tt);*/
//
//                                tt = new TraderTransaction();
//                                tt.setAmount(NumberUtil.NZero(txtSaleLastBalance.getText()));
//                                tt.setUserId(Global.machineId);
//                                //tt.setTranOption("Pre. Balance : ");
//                                tt.setTranOption(Util1.getPropValue("system.app.sale.prvbalance"));
//                                tt.setTranDate(DateUtil.toDate(strLastSaleDate));
//                                tt.setTranType("N");
//                                tt.setSortId(1);
//                                tt.setMachineId(Global.machineId);
//
//                                dao.save(tt);
//                            }
//                        }
//                    } catch (Exception ex) {
//                        //dao.rollBack();
//                        log.error("print : " + currSaleVou.getSaleInvId() + " : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
//                        JOptionPane.showMessageDialog(Util1.getParent(), "Error cannot print.",
//                                "Sale print", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    } finally {
//                        dao.close();
//                    }
//                }
//            }/* else {
//             JOptionPane.showMessageDialog(Util1.getParent(), "Need Permission",
//             "Sale print", JOptionPane.ERROR_MESSAGE);
//             }*/
//
//            log.error("print() end database update : " + currSaleVou.getSaleInvId());
//
//            String reportNameProp;
//            if (chkPrintOption.isSelected()) {
//                reportNameProp = "report.file.saleV";
//            } else if (chkVouComp.isSelected()) {
//                reportNameProp = "report.file.comp";
//            } else {
//                reportNameProp = "report.file.saleW";
//            }
//
//            String reportName = Util1.getPropValue(reportNameProp);
//            String propValue = Util1.getPropValue("repor.choose");
//            if (propValue.equals("Y")) {
//                /*ReportChooseDialog diag = new ReportChooseDialog();
//                 diag.setLocationRelativeTo(null);
//                 diag.setVisible(true);
//
//                 if (diag.getrName().equals("Alpha")) {
//                 reportName = "saleVouDotMImageSHIFAAlpha";
//                 }*/
//                Trader cus = (Trader) currSaleVou.getCustomerId();
//                if (cus.getTraderGroup() != null) {
//                    String tmpReportName = cus.getTraderGroup().getReportName();
//                    if (tmpReportName != null) {
//                        if (!tmpReportName.isEmpty()) {
//                            reportName = tmpReportName;
//                        }
//                    }
//                }
//            }
//            String printerName = Util1.getPropValue("report.vou.printer");
//            String compName = Util1.getPropValue("report.company.name");
//            String bankInfo = Util1.getPropValue("report.bankinfo");
//            String printMode = Util1.getPropValue("report.vou.printer.mode");
//            if (chkA5.isSelected()) {
//                String a5Report = Util1.getPropValue("report.file.saleW");
//                if (a5Report.isEmpty() || a5Report.equals("-")) {
//                    reportName = "W/SaleVoucherInvoiceA5";
//                } else {
//                    reportName = a5Report;
//                }
//                printMode = "View";
//            }
//            String reportPath = Util1.getAppWorkFolder()
//                    + Util1.getPropValue("report.folder.path")
//                    + reportName;
//
//            Map<String, Object> params = new HashMap();
//            params.put("p_bank_desp", bankInfo);
//            params.put("link_amt_status", "N");
//            params.put("link_amt", 0);
//
//            try {
//                String delSql = "delete from tmp_amount_link where user_id = '"
//                        + Global.machineId + "'";
//                dao.execSql(delSql);
//            } catch (Exception ex) {
//                log.error("link delete : " + ex.getMessage());
//            }
//
//            if (Util1.getPropValue("system.link.amount").equals("Pharmacy")
//                    && currSaleVou.getPatientId() != null) {
//                try {
//
//                    String strSql = "INSERT INTO tmp_amount_link(user_id,tran_option,inv_id,amount,print_status)\n"
//                            + "  select '" + Global.machineId + "', tran_source, inv_id, balance, true\n"
//                            + "    from (select date(tran_date) tran_date, cus_id, currency, 'Pharmacy' as tran_source, inv_id, \n"
//                            + "                 sum(ifnull(paid,0)) balance\n"
//                            + "			from v_session\n"
//                            + "		   where ifnull(paid,0) <> 0 and source = 'Sale' and deleted = false\n"
//                            + "		   group by date(tran_date), cus_id, currency, source, inv_id\n"
//                            + "		   union all\n"
//                            + "		  select date(tran_date) tran_date, patient_id cus_id, currency_id currency, \n"
//                            + "				 tran_option tran_source, opd_inv_id inv_id, sum(ifnull(paid,0)) balance\n"
//                            + "			from v_session_clinic\n"
//                            + "		   where ifnull(paid,0) <> 0 and tran_option in ('OPD') and deleted = false\n"
//                            + "		   group by date(tran_date), patient_id, currency_id, tran_option, opd_inv_id) a\n"
//                            + "   where tran_source <> '" + Util1.getPropValue("system.link.amount")
//                            + "'    and tran_date = '" + DateUtil.toDateStr(currSaleVou.getSaleDate(), "yyyy-MM-dd")
//                            + "'    and cus_id = '" + currSaleVou.getPatientId().getRegNo() + "'";
//
//                    dao.execSql(strSql);
//
//                    List<TempAmountLink> listTAL = dao.findAllHSQL(
//                            "select o from TempAmountLink o where o.key.userId = '" + Global.machineId + "'");
//                    if (listTAL != null) {
//                        if (!listTAL.isEmpty()) {
//                            AmountLinkDialog dialog = new AmountLinkDialog(listTAL);
//                            dialog.setVisible(true);
//                            double ttlLinkAmt = dialog.getTtlAmt();
//                            if (ttlLinkAmt != 0) {
//                                params.put("link_amt_status", "Y");
//                                params.put("link_amt", ttlLinkAmt + currSaleVou.getPaid());
//                            }
//                        }
//                    }
//                } catch (Exception ex) {
//                    log.error("print link amount : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
//                } finally {
//                    dao.close();
//                }
//            }
//
//            params.put("invoiceNo", currSaleVou.getSaleInvId());
//            params.put("due_date", currSaleVou.getDueDate());
//            if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
//                if (currSaleVou.getPatientId() != null) {
//                    params.put("customerName", currSaleVou.getPatientId().getPatientName());
//                    Date regDate = currSaleVou.getPatientId().getRegDate();
//                    Date trgDate = DateUtil.toDate("08/10/2018", "dd/MM/yyyy");
//                    if (regDate.before(trgDate)) {
//                        String strYear = DateUtil.toDateStr(regDate, "yyyy");
//                        params.put("reg_no", currSaleVou.getPatientId().getRegNo() + "/" + strYear);
//                    } else {
//                        params.put("reg_no", currSaleVou.getPatientId().getRegNo());
//                    }
//                } else {
//                    params.put("customerName", txtCusName.getText());
//                    params.put("reg_no", "");
//                }
//
//            } else if (currSaleVou.getCustomerId() != null) {
//                params.put("customerName", currSaleVou.getCustomerId().getTraderName());
//                params.put("reg_no", currSaleVou.getCustomerId().getTraderId());
//            } else {
//                params.put("customerName", "");
//                params.put("reg_no", "");
//            }
//
//            if (currSaleVou.getDoctor() != null) {
//                params.put("doctor", currSaleVou.getDoctor().getDoctorName());
//            } else {
//                params.put("doctor", "");
//            }
//            params.put("saleDate", currSaleVou.getSaleDate());
//            params.put("grandTotal", currSaleVou.getVouTotal());
//            params.put("paid", currSaleVou.getPaid());
//            //params.put("paid", currSaleVou.getPayAmt());
//            params.put("discount", currSaleVou.getDiscount());
//            params.put("tax", currSaleVou.getTaxAmt());
//            params.put("voubalance", currSaleVou.getBalance());
//            params.put("balance", currSaleVou.getRefund());
//            params.put("vou_balance", currSaleVou.getVouTotal() + currSaleVou.getTaxAmt()
//                    - (currSaleVou.getDiscount() + currSaleVou.getPaid()));
//            params.put("user", Global.loginUser.getUserShortName());
//            params.put("listParam", listExpense);
//            params.put("compName", compName);
//            params.put("prv_date", lblSaleLastBal.getText());
//            //double prvBalance = NumberUtil.NZero(txtSaleLastBalance.getValue()) + NumberUtil.NZero(tranTableModel.getTotal());
//            params.put("prv_balance", Double.valueOf(txtSaleLastBalance.getValue().toString()));
//            params.put("tran_total", NumberUtil.NZero(tranTableModel.getTotal()));
//            /*NumberUtil.NZero(txtCusLastBalance.getText()) -
//             (NumberUtil.NZero(currSaleVou.getPaid())+
//             NumberUtil.NZero(currSaleVou.getDiscount()) + 
//             NumberUtil.NZero(currSaleVou.getTaxAmt()))*/
//            //double lastBalance = prvBalance + currSaleVou.getBalance();
//            params.put("last_balance", txtCusLastBalance.getValue());
//            params.put("due_date", txtDueDate.getValue());
//            params.put("lblPrvBalance", lblSaleLastBal.getText());
//            params.put("prv_date", lblSaleLastBal.getText());
//            //params.put("prv_balance", txtSaleLastBalance.getText());
//            //params.put("refund", currSaleVou.getRefund());
//            params.put("p_machine_id", Global.machineId);
//            Location loc = (Location) cboLocation.getSelectedItem();
//            if (loc != null) {
//                params.put("loc_name", loc.getLocationName());
//            } else {
//                params.put("loc_name", "-");
//            }
//            if (currSaleVou.getCustomerId() != null) {
//                Trader tr = currSaleVou.getCustomerId();
//                if (tr.getTownship() != null) {
//                    params.put("township", tr.getTownship().getTownshipName());
//                } else {
//                    params.put("township", " ");
//                }
//                if (tr.getAddress() != null) {
//                    params.put("address", tr.getAddress());
//                } else {
//                    params.put("address", " ");
//                }
//            }
//
//            if (!txtDueDate.getText().isEmpty()) {
//                params.put("pay_info", txtDueDate.getText() + " ေငြေကာက္မည္");
//            } else {
//                params.put("pay_info", " ");
//            }
//
//            if (lblStatus.getText().equals("NEW")) {
//                params.put("vou_status", " ");
//            } else {
//                params.put("vou_status", lblStatus.getText());
//            }
//
//            params.put("pay_retin", tranTableModel.getTotal());
//            params.put("comp_address", Util1.getPropValue("report.address"));
//            params.put("phone", Util1.getPropValue("report.phone"));
//
//            params.put("user_id", Global.machineId);
//            params.put("user_short", Global.loginUser.getUserShortName());
//            params.put("inv_id", currSaleVou.getSaleInvId());
//            params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
//                    + Util1.getPropValue("report.folder.path"));
//            params.put("IMAGE_PATH", Util1.getAppWorkFolder()
//                    + Util1.getPropValue("report.folder.path"));
//            String imagePath = Util1.getAppWorkFolder()
//                    + Util1.getPropValue("report.folder.path") + "img/logo.jpg";
//            params.put("imagePath", imagePath);
//            params.put("comp_name", Util1.getPropValue("report.company.name1"));
//            params.put("category", Util1.getPropValue("report.company.cat"));
//            params.put("remark", txtRemark.getText());
//            params.put("REPORT_CONNECTION", dao.getConnection());
//            PaymentType pt = currSaleVou.getPaymentTypeId();
//            if (pt != null) {
//                params.put("prm_pay_type", pt.getPaymentTypeName());
//            }
//            params.put("user_desp", "Customer Voucher, Thanks You.");
//            //String reportPath1 = "D:\\mws\\BEST\\BEST-Software\\src\\com\\best\\app\\pharmacy\\report\\test.jrxml";
//            //ReportUtil.viewReport(reportPath1, null, dao.getConnection());
//
//            try {
//                if (printMode.equals("View") || !chkPrintOption.isSelected()) {
//                    if (reportNameProp.equals("report.file.saleW")
//                            || reportNameProp.equals("report.file.comp")) {
//                        if (Util1.getPropValue("report.file.type").equals("con")) {
//                            ReportUtil.viewReport(reportPath, params, dao.getConnection());
//                        } else {
//                            ReportUtil.viewReport(reportPath, params, listDetail);
//                        }
//                    } else if (Util1.getPropValue("report.file.type").equals("con")) {
//                        ReportUtil.viewReport(reportPath, params, dao.getConnection());
//                    } else {
//                        ReportUtil.viewReport(reportPath, params, listDetail);
//                    }
//                } else {
//                    if (Util1.getPropValue("report.file.type").equals("con")) {
//                        int count = (int) spPrint.getValue();
//                        for (int i = 0; i < count; i++) {
//                            JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
//                            ReportUtil.printJasper(jp, printerName);
//                            params.put("user_desp", "Receive Voucher, Thanks You.");
//                        }
//                    } else {
//                        JasperPrint jp = ReportUtil.getReport(reportPath, params, listDetail);
//                        ReportUtil.printJasper(jp, printerName);
//                        if (Util1.getPropValue("system.pharmacy.sale.print.double").equals("Y")) {
//                            JasperPrint jp1 = ReportUtil.getReport(reportPath, params, dao.getConnection());
//                            params.put("user_desp", "Receive Voucher, Thanks You.");
//                            ReportUtil.printJasper(jp1, printerName);
//                        }
//                    }
//                    txtCusId.requestFocus();
//                }
//            } catch (Exception ex) {
//                log.error("print : " + ex.getMessage());
//            }
//            clear();
//        }
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelReg = new javax.swing.JPanel();
        txtRegNo = new javax.swing.JTextField();
        txtDOB = new javax.swing.JTextField();
        txtAge = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        butRegPrint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRegZPLCode = new javax.swing.JTextArea();
        butRegClear = new javax.swing.JButton();
        cboPrinter = new javax.swing.JComboBox<>();
        panelOPD = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOPDService = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtOPDZPLCode = new javax.swing.JTextArea();
        butOPDPrint = new javax.swing.JButton();
        txtOPDClear = new javax.swing.JButton();
        panelOT = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblOTService = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtOTZPLCode = new javax.swing.JTextArea();
        butOTPrint = new javax.swing.JButton();
        txtOTClear = new javax.swing.JButton();
        panelPharmacy = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblPharmacy = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtPharZPLCode = new javax.swing.JTextArea();
        butPharPrint = new javax.swing.JButton();
        butPharClear = new javax.swing.JButton();
        panelDC = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tbpIPD = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtIPDZPLCode = new javax.swing.JTextArea();
        butIPDPrint = new javax.swing.JButton();
        butIPDClear = new javax.swing.JButton();

        panelReg.setBorder(javax.swing.BorderFactory.createTitledBorder("Register Label Print"));

        txtRegNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Reg No."));
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        txtDOB.setEditable(false);
        txtDOB.setBorder(javax.swing.BorderFactory.createTitledBorder("DOB"));

        txtAge.setEditable(false);
        txtAge.setBorder(javax.swing.BorderFactory.createTitledBorder("Age"));

        txtPtName.setEditable(false);
        txtPtName.setFont(Global.textFont);
        txtPtName.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));

        txtAddress.setEditable(false);
        txtAddress.setFont(Global.textFont);
        txtAddress.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));

        txtPhone.setEditable(false);
        txtPhone.setFont(Global.textFont);
        txtPhone.setBorder(javax.swing.BorderFactory.createTitledBorder("Phone"));

        butRegPrint.setText("Print");
        butRegPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRegPrintActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Command Text"));

        txtRegZPLCode.setColumns(20);
        txtRegZPLCode.setRows(5);
        txtRegZPLCode.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(txtRegZPLCode);

        butRegClear.setText("Clear");
        butRegClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRegClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRegLayout = new javax.swing.GroupLayout(panelReg);
        panelReg.setLayout(panelRegLayout);
        panelRegLayout.setHorizontalGroup(
            panelRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRegLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPtName)
                    .addComponent(txtAddress)
                    .addGroup(panelRegLayout.createSequentialGroup()
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelRegLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butRegPrint)
                            .addComponent(butRegClear, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(txtPhone))
                .addContainerGap())
        );
        panelRegLayout.setVerticalGroup(
            panelRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRegLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRegNo)
                    .addComponent(txtDOB)
                    .addComponent(txtAge))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelRegLayout.createSequentialGroup()
                        .addComponent(butRegPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butRegClear)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelRegLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtPtName, txtRegNo});

        cboPrinter.setBorder(javax.swing.BorderFactory.createTitledBorder("Printer"));

        panelOPD.setBorder(javax.swing.BorderFactory.createTitledBorder("OPD Service Label Print"));

        tblOPDService.setFont(Global.textFont);
        tblOPDService.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblOPDService);

        txtOPDZPLCode.setColumns(20);
        txtOPDZPLCode.setRows(5);
        jScrollPane3.setViewportView(txtOPDZPLCode);

        butOPDPrint.setText("Print");

        txtOPDClear.setText("Clear");

        javax.swing.GroupLayout panelOPDLayout = new javax.swing.GroupLayout(panelOPD);
        panelOPD.setLayout(panelOPDLayout);
        panelOPDLayout.setHorizontalGroup(
            panelOPDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOPDLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOPDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOPDLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addGroup(panelOPDLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelOPDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butOPDPrint, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtOPDClear, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        panelOPDLayout.setVerticalGroup(
            panelOPDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOPDLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOPDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelOPDLayout.createSequentialGroup()
                        .addComponent(butOPDPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOPDClear)))
                .addContainerGap())
        );

        panelOT.setBorder(javax.swing.BorderFactory.createTitledBorder("OT Service Label Print"));

        tblOTService.setFont(Global.textFont);
        tblOTService.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblOTService);

        txtOTZPLCode.setColumns(20);
        txtOTZPLCode.setRows(5);
        jScrollPane5.setViewportView(txtOTZPLCode);

        butOTPrint.setText("Print");

        txtOTClear.setText("Clear");

        javax.swing.GroupLayout panelOTLayout = new javax.swing.GroupLayout(panelOT);
        panelOT.setLayout(panelOTLayout);
        panelOTLayout.setHorizontalGroup(
            panelOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOTLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addGroup(panelOTLayout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butOTPrint, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtOTClear, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        panelOTLayout.setVerticalGroup(
            panelOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelOTLayout.createSequentialGroup()
                        .addComponent(butOTPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOTClear)))
                .addContainerGap())
        );

        panelPharmacy.setBorder(javax.swing.BorderFactory.createTitledBorder("Pharmacy Label Print"));

        tblPharmacy.setFont(Global.textFont);
        tblPharmacy.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title1", "Title2", "Title3", "Title4"
            }
        ));
        tblPharmacy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPharmacyMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tblPharmacy);

        txtPharZPLCode.setColumns(20);
        txtPharZPLCode.setRows(5);
        jScrollPane7.setViewportView(txtPharZPLCode);

        butPharPrint.setText("Print");
        butPharPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPharPrintActionPerformed(evt);
            }
        });

        butPharClear.setText("Clear");

        javax.swing.GroupLayout panelPharmacyLayout = new javax.swing.GroupLayout(panelPharmacy);
        panelPharmacy.setLayout(panelPharmacyLayout);
        panelPharmacyLayout.setHorizontalGroup(
            panelPharmacyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPharmacyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPharmacyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addGroup(panelPharmacyLayout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPharmacyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butPharPrint, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(butPharClear, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        panelPharmacyLayout.setVerticalGroup(
            panelPharmacyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPharmacyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPharmacyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPharmacyLayout.createSequentialGroup()
                        .addComponent(butPharPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butPharClear)))
                .addGap(9, 9, 9))
        );

        panelDC.setBorder(javax.swing.BorderFactory.createTitledBorder("IPD Service Label Print"));

        tbpIPD.setFont(Global.textFont);
        tbpIPD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane8.setViewportView(tbpIPD);

        txtIPDZPLCode.setColumns(20);
        txtIPDZPLCode.setRows(5);
        jScrollPane9.setViewportView(txtIPDZPLCode);

        butIPDPrint.setText("Print");

        butIPDClear.setText("Clear");

        javax.swing.GroupLayout panelDCLayout = new javax.swing.GroupLayout(panelDC);
        panelDC.setLayout(panelDCLayout);
        panelDCLayout.setHorizontalGroup(
            panelDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                    .addGroup(panelDCLayout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butIPDPrint, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(butIPDClear, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        panelDCLayout.setVerticalGroup(
            panelDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelDCLayout.createSequentialGroup()
                        .addComponent(butIPDPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butIPDClear)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelReg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelOPD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelOT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelPharmacy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(23, 23, 23))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelReg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelOT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelOPD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelDC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelPharmacy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        getPatient();
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void butRegClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRegClearActionPerformed
        regClear();
    }//GEN-LAST:event_butRegClearActionPerformed

    private void butRegPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRegPrintActionPerformed
        regPrint();
    }//GEN-LAST:event_butRegPrintActionPerformed

    private void tblPharmacyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPharmacyMouseClicked
//        if (evt.getClickCount() == 2) {
//
//            if (!Util1.getPropValue("system.opd.urgent").equals("-")
//                    && !Util1.getPropValue("system.opd.urgent").isEmpty()) {
//                int index = tblPharmacy.convertRowIndexToModel(tblPharmacy.getSelectedRow());
//                pharmacyTableModel.showUrgentDialog(index);
//            }
//        }        // TODO add your handling code here:
    }//GEN-LAST:event_tblPharmacyMouseClicked

    private void butPharPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPharPrintActionPerformed
            pharPrint();
    }//GEN-LAST:event_butPharPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butIPDClear;
    private javax.swing.JButton butIPDPrint;
    private javax.swing.JButton butOPDPrint;
    private javax.swing.JButton butOTPrint;
    private javax.swing.JButton butPharClear;
    private javax.swing.JButton butPharPrint;
    private javax.swing.JButton butRegClear;
    private javax.swing.JButton butRegPrint;
    private javax.swing.JComboBox<String> cboPrinter;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPanel panelDC;
    private javax.swing.JPanel panelOPD;
    private javax.swing.JPanel panelOT;
    private javax.swing.JPanel panelPharmacy;
    private javax.swing.JPanel panelReg;
    private javax.swing.JTable tblOPDService;
    private javax.swing.JTable tblOTService;
    private javax.swing.JTable tblPharmacy;
    private javax.swing.JTable tbpIPD;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtDOB;
    private javax.swing.JTextArea txtIPDZPLCode;
    private javax.swing.JButton txtOPDClear;
    private javax.swing.JTextArea txtOPDZPLCode;
    private javax.swing.JButton txtOTClear;
    private javax.swing.JTextArea txtOTZPLCode;
    private javax.swing.JTextArea txtPharZPLCode;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextArea txtRegZPLCode;
    // End of variables declaration//GEN-END:variables
}
