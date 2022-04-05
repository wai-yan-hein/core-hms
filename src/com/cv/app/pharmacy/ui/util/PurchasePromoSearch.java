/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.PurPromoSearchTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author wswe
 */
public class PurchasePromoSearch extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(PurchasePromoSearch.class.getName());
    private final AbstractDataAccess dao;
    private Object parent;
    private SelectionObserver observer;
    private int selectedRow = -1;
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "PurSearch");
    private PurPromoSearchTableModel purVouTableModel = new PurPromoSearchTableModel();
    private TableRowSorter<TableModel> sorter;
    private VouFilter vouFilter;

    /**
     * Creates new form PurchaseVouSearch
     */
    public PurchasePromoSearch(Object parent, SelectionObserver observer, AbstractDataAccess dao) {
        initComponents();
        //Search history
        this.parent = parent;
        this.observer = observer;
        this.dao = dao;
                
        getPrvFilter();
        initTableMedicine();
        search();
        sorter = new TableRowSorter(tblVoucher.getModel());
        tblVoucher.setRowSorter(sorter);
        actionMapping();
        initTableVoucher();
        addSelectionListenerVoucher();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;

                if (cus != null) {
                    txtCusCode.setText(cus.getTraderId());
                    txtCusName.setText(cus.getTraderName());
                    vouFilter.setTrader(cus);
                } else {
                    vouFilter.setTrader(null);
                }

                break;
        }
    }

    private void getCustomerList() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
    }

    private void initTableMedicine() {
        List<VouCodeFilter> listMedicine = dao.findAll("VouCodeFilter",
                "key.tranOption = 'PurSearch' and key.userId = '"
                + Global.loginUser.getUserId() + "'");
        tblMedicineModel.setListCodeFilter(listMedicine);
        tblMedicineModel.addEmptyRow();
        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMedicine.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void initTableVoucher() {
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(7).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(8).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(9).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(10).setPreferredWidth(50);

        tblVoucher.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    private void addSelectionListenerVoucher() {
        //Define table selection model to single row selection.
        tblVoucher.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblVoucher.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblVoucher.getSelectedRow();
            }
        });
    }
    private Action actionMedList = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(tblMedicine.getCellEditor() != null){
                    tblMedicine.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
            }

            getMedList("");
        }
    };

    // <editor-fold defaultstate="collapsed" desc="getMedInfo">
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
    private void getMedList(String filter) {
        int locationId = -1;
        /*if(cboLocation.getSelectedItem() instanceof Location){
            locationId = ((Location)cboLocation.getSelectedItem()).getLocationId();
        }*/
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

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    private void actionMapping() {
        //F3 event on tblSale
        //tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        //tblMedicine.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblMedicine.getActionMap().put("F8-Action", actionMedDelete);

        //Enter event on tblSale
        //tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        //tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);
    }// </editor-fold>
    private Action actionMedDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblMedicine.getSelectedRow() >= 0) {
                tblMedicineModel.delete(tblMedicine.getSelectedRow());
            }
        }
    };

    private String getHSQL() {
        String strSql = "select distinct v from VPurPromo v where v.purDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";

        vouFilter.setFromDate(DateUtil.toDate(txtFromDate.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtToDate.getText()));

        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            strSql = strSql + " and v.cusId = '" + txtCusCode.getText() + "'";
        } else {
            vouFilter.setTrader(null);
        }

        //Filter history save
        try {
            dao.save(vouFilter);
        } catch (Exception ex) {
            log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        List<VouCodeFilter> listMedicine = tblMedicineModel.getListCodeFilter();

        if (listMedicine.size() > 1) {
            strSql = strSql + " and v.medId in (";
            String medList = "";

            for (VouCodeFilter filter : listMedicine) {
                if (filter.getKey() != null) {
                    if (medList.length() > 0) {
                        medList = medList + ",'" + filter.getKey().getItemCode().getMedId() + "'";
                    } else {
                        medList = "'" + filter.getKey().getItemCode().getMedId() + "'";
                    }
                }
            }

            strSql = strSql + medList + ")";
        }

        return strSql + " order by date(v.purDate) DESC, v.purInvId DESC";
    }

    private void search() {
        String strSql = getHSQL();
        //System.out.println("HSQL : " + strSql);

        try {
            dao.open();
            purVouTableModel.setListPurHis(dao.findAllHSQL(strSql));
            dao.close();
        } catch (Exception ex) {
            log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        lblTotalRec.setText("Total Records : " + purVouTableModel.getRowCount());
    }

    private void select() {
        /*if (selectedRow >= 0) {
            observer.selected("PurVouList",
                    purVouTableModel.getSelectVou(tblVoucher.convertRowIndexToModel(selectedRow)));

            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select purchase voucher.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private void getPrvFilter() {
        VouFilter tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'PurVouSearch'"
                + " and key.userId = '" + Global.loginUser.getUserId() + "'");

        if (tmpFilter == null) {
            vouFilter = new VouFilter();
            vouFilter.getKey().setTranOption("PurVouSearch");
            vouFilter.getKey().setUserId(Global.loginUser.getUserId());

            txtFromDate.setText(DateUtil.getTodayDateStr());
            txtToDate.setText(DateUtil.getTodayDateStr());
        } else {
            vouFilter = tmpFilter;
            txtFromDate.setText(DateUtil.toDateStr(vouFilter.getFromDate()));
            txtToDate.setText(DateUtil.toDateStr(vouFilter.getToDate()));

            if (vouFilter.getTrader() != null) {
                txtCusCode.setText(vouFilter.getTrader().getTraderId());
                txtCusName.setText(vouFilter.getTrader().getTraderName());
            }
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
        txtFromDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtToDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(tblMedicineModel);
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        butSearch = new javax.swing.JButton();
        lblTotalRec = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JFormattedTextField();

        setPreferredSize(new java.awt.Dimension(1042, 600));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtFromDate.setEditable(false);
        txtFromDate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFromDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromDateMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        txtToDate.setEditable(false);
        txtToDate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtToDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToDateMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Customer");

        txtCusCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusCodeMouseClicked(evt);
            }
        });
        txtCusCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusCodeActionPerformed(evt);
            }
        });
        txtCusCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCusCodeFocusLost(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou No");

        txtVouNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblMedicine.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblMedicine.setRowHeight(23);
        tblMedicine.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblMedicine);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCusName))
                            .addComponent(txtVouNo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 25, Short.MAX_VALUE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addGap(216, 216, 216))
        );

        tblVoucher.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblVoucher.setModel(purVouTableModel);
        tblVoucher.setRowHeight(23);
        tblVoucher.setShowVerticalLines(false);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVoucher);

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        lblTotalRec.setText("Total Records : 0");

        jLabel4.setText("Total Amount : ");

        txtTotalAmount.setEditable(false);
        txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSearch)
                    .addComponent(lblTotalRec)
                    .addComponent(jLabel4)
                    .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFromDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFromDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFromDateMouseClicked

    private void txtToDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtToDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtToDateMouseClicked

    private void txtCusCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusCodeMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusCodeMouseClicked

    private void txtCusCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusCodeActionPerformed
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        }
    }//GEN-LAST:event_txtCusCodeActionPerformed

    private void txtCusCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusCodeFocusLost
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        }
    }//GEN-LAST:event_txtCusCodeFocusLost

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtFromDate;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmount;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
