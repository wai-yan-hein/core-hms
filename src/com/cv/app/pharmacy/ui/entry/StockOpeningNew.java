/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.StockOpeningHis;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.pharmacy.ui.common.StockOpTableModelNew;
import static com.cv.app.pharmacy.ui.entry.StockOpening.log;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;

/**
 *
 * @author ACER
 */
public class StockOpeningNew extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate{

    static Logger log = Logger.getLogger(StockOpening.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private MedicineUP medUp = new MedicineUP(dao);
    private boolean bindComplete = false;
    private int rowIndex = -1;
    private int mouseClick = 2;
    private StockOpTableModelNew stkOTableModel = new StockOpTableModelNew(medUp);
    private StockOpeningHis currStockOpening = new StockOpeningHis();
    
    /**
     * Creates new form StockOpeningNew
     */
    public StockOpeningNew() {
        initComponents();
        try{
            txtOpID.setFormatterFactory(new VouFormatFactory());
            txtGroundDate.setText(DateUtil.getTodayDateStr());
            txtStockDate.setText(DateUtil.getTodayDateStr());
            vouEngine = new GenVouNoImpl(dao, "StockOpening", DateUtil.getPeriod(txtGroundDate.getText()));
            genVouNo();
            initCombo();
            initTable();
            actionMapping();
        }catch(Exception ex){
            log.error("StockOpeningNew : " + ex.toString());
        }finally{
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj){
        
    }
    
    @Override
    public void save(){
        
    }
    
    @Override
    public void newForm(){
        rowIndex = -1;
        lblStatus.setText("NEW");
        currStockOpening = new StockOpeningHis();
        stkOTableModel.clear();
        assignDefaultValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtGroundDate.getText()));
        genVouNo();
        butCost.setEnabled(false);
    }
    
    @Override
    public void history(){
        
    }
    
    @Override
    public void delete(){
        
    }
    
    @Override
    public void deleteCopy(){
        
    }
    
    @Override
    public void print(){
        
    }
    
    @Override
    public void getMedInfo(String medCode){
        
    }
    
    @Override
    public void keyEvent(KeyEvent e){
        
    }
    
    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtOpID.setText(vouNo);
    }
    
    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboGroundLocation, getLocationFilter());
            BindingUtil.BindCombo(cboStockLocation, getLocationFilter());
            //BindingUtil.BindComboFilter(cboItemType, dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
            //BindingUtil.BindComboFilter(cboBrand, dao.findAllHSQL("select o from ItemBrand o order by o.brandName"));
            //BindingUtil.BindComboFilter(cboCategory, dao.findAllHSQL("select o from Category o order by o.catName"));

            new ComBoBoxAutoComplete(cboGroundLocation, this);
            new ComBoBoxAutoComplete(cboStockLocation, this);
            //new ComBoBoxAutoComplete(cboItemType, this);
            //new ComBoBoxAutoComplete(cboBrand, this);
            //new ComBoBoxAutoComplete(cboCategory, this);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.toString());
        } finally {
            dao.close();
        }
        bindComplete = true;
    }
    
    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowStkOp = true) order by o.locationName");
            } else {
                return dao.findAllHSQL("select o from Location o order by o.locationName");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.toString());
        } finally {
            dao.close();
        }
        return null;
    }
    
    private void initTable(){
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblGround.setCellSelectionEnabled(true);
        }
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        stkOTableModel.setParent(tblGround);
        stkOTableModel.addEmptyRow();
        tblGround.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblGround.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        tblGround.getColumnModel().getColumn(1).setPreferredWidth(200);//Medicine Name
        tblGround.getColumnModel().getColumn(2).setPreferredWidth(80);//Relstr
        tblGround.getColumnModel().getColumn(3).setPreferredWidth(30);//Expire Date
        tblGround.getColumnModel().getColumn(4).setPreferredWidth(20);//Qty
        tblGround.getColumnModel().getColumn(5).setPreferredWidth(10);//Unit
        tblGround.getColumnModel().getColumn(6).setPreferredWidth(40);//Cost Price

        tblGround.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblGround.getColumnModel().getColumn(4).setCellEditor(
                new BestTableCellEditor(this));
        tblGround.getColumnModel().getColumn(6).setCellEditor(
                new BestTableCellEditor(this));
        tblGround.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void actionMapping() {
        //F3 event
        tblGround.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblGround.getActionMap().put("F3-Action", actionMedList);

        //F8 event
        tblGround.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblGround.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event
        tblGround.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblGround.getActionMap().put("ENTER-Action", actionTblSopEnterKey);

        formActionKeyMapping(txtOpID);
        formActionKeyMapping(txtGroundDate);
        formActionKeyMapping(tblGround);
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
    
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblGround.getCellEditor() != null) {
                    tblGround.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    //dao.open();
                    //getMedList("");
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                } finally {
                    dao.close();
                }
            }
        }
    };

    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblGround.getSelectedRow() >= 0) {
                try {
                    if (tblGround.getCellEditor() != null) {
                        tblGround.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                rowIndex = -1;
                int row = tblGround.convertRowIndexToModel(tblGround.getSelectedRow());
                stkOTableModel.delete(row);
            }
        }
    };
    
    private Action actionTblSopEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblGround.getCellEditor() != null) {
                    tblGround.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblGround.getSelectedRow();
            int col = tblGround.getSelectedColumn();

            if (col == 0 && !stkOTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 2 && !stkOTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 3 && !stkOTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && !stkOTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(6, 6); //Move to Cost Price
            } else if (col == 5 && !stkOTableModel.isEmptyRow(row)) {
                tblGround.setColumnSelectionInterval(6, 6); //Move to Cost Price
            } else if (col == 6 && !stkOTableModel.isEmptyRow(row)) {
                tblGround.setRowSelectionInterval(row + 1, row + 1);
                tblGround.setColumnSelectionInterval(0, 0); //Move to Code
            }
        }
    };
    
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
    
    private void assignDefaultValue() {
        txtGroundDate.setText(DateUtil.getTodayDateStr());
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
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtStockDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        cboStockLocation = new javax.swing.JComboBox<>();
        butFixMinus = new javax.swing.JButton();
        butStock = new javax.swing.JButton();
        txtStockFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtOpID = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtGroundDate = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        cboGroundLocation = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtTtlRecord = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        butCSV = new javax.swing.JButton();
        butCost = new javax.swing.JButton();
        butToAcc = new javax.swing.JButton();
        butFillZero = new javax.swing.JButton();
        txtGroundFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblGround = new javax.swing.JTable();

        jLabel1.setText("Date ");

        txtStockDate.setEditable(false);
        txtStockDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtStockDateMouseClicked(evt);
            }
        });
        txtStockDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStockDateActionPerformed(evt);
            }
        });

        jLabel2.setText("Location ");

        butFixMinus.setText("Fix Minus");

        butStock.setText("Stock");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtStockDate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboStockLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtStockFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butStock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFixMinus)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtStockDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cboStockLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butFixMinus)
                    .addComponent(butStock)
                    .addComponent(txtStockFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTable1.setFont(Global.textFont);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setRowHeight(23);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel6.setText("Op-ID");

        txtOpID.setEditable(false);

        jLabel7.setText("Date ");

        txtGroundDate.setEditable(false);
        txtGroundDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtGroundDateMouseClicked(evt);
            }
        });

        jLabel8.setText("Location ");

        jLabel9.setText("Total Record(S) : ");

        txtTtlRecord.setEditable(false);

        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOpID, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtGroundDate, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboGroundLocation, 0, 64, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTtlRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtOpID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtGroundDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(cboGroundLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblStatus, txtTtlRecord});

        butCSV.setText("CSV");

        butCost.setText("Cost");

        butToAcc.setText("To Acc");

        butFillZero.setText("Fill Zero");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtGroundFilter)
                .addGap(18, 18, 18)
                .addComponent(butFillZero)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butToAcc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butCost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butCSV)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(butCSV)
                .addComponent(butCost)
                .addComponent(butToAcc)
                .addComponent(butFillZero)
                .addComponent(txtGroundFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tblGround.setFont(Global.textFont);
        tblGround.setModel(stkOTableModel);
        tblGround.setRowHeight(23);
        jScrollPane2.setViewportView(tblGround);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtStockDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockDateActionPerformed

    private void txtStockDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtStockDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtStockDate.setText(strDate);
          }
      }
    }//GEN-LAST:event_txtStockDateMouseClicked

    private void txtGroundDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtGroundDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtGroundDate.setText(strDate);
                //sopTableModel.setStrOpDate(strDate);

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtGroundDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtGroundDateMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCSV;
    private javax.swing.JButton butCost;
    private javax.swing.JButton butFillZero;
    private javax.swing.JButton butFixMinus;
    private javax.swing.JButton butStock;
    private javax.swing.JButton butToAcc;
    private javax.swing.JComboBox<String> cboGroundLocation;
    private javax.swing.JComboBox<String> cboStockLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblGround;
    private javax.swing.JFormattedTextField txtGroundDate;
    private javax.swing.JTextField txtGroundFilter;
    private javax.swing.JFormattedTextField txtOpID;
    private javax.swing.JFormattedTextField txtStockDate;
    private javax.swing.JTextField txtStockFilter;
    private javax.swing.JFormattedTextField txtTtlRecord;
    // End of variables declaration//GEN-END:variables
}
