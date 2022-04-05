/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.view.ReturnInItemList;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.RetInItemSearchTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author WSwe
 */
public class ReturnInItemSearchDialog extends javax.swing.JDialog implements SelectionObserver {

    private AbstractDataAccess dao = Global.dao;
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "RetInItemSearch");
    private RetInItemSearchTableModel tblRetInItemList = new RetInItemSearchTableModel();
    private SelectionObserver observer;
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form ReturnInItemSearchDialog
     */
    public ReturnInItemSearchDialog(SelectionObserver observer, String traderId,
            String traderName) {
        super(Util1.getParent(), true);
        initComponents();

        this.observer = observer;
        txtTrcode.setText(traderId);
        txtTrName.setText(traderName);
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());

        initTableMedicine();
        initTableRetInItemList();
        actionMapping();

        sorter = new TableRowSorter(tblReturnItemList.getModel());
        tblReturnItemList.setRowSorter(sorter);

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        setVisible(true);
        
        search();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;

                if (cus != null) {
                    txtTrcode.setText(cus.getTraderId());
                    txtTrName.setText(cus.getTraderName());
                    //vouFilter.setTrader(cus);
                } else {
                    //vouFilter.setTrader(null);
                }

                break;
        }
    }

    private void initTableMedicine() {
        List<VouCodeFilter> listMedicine = dao.findAll("VouCodeFilter",
                "key.tranOption = 'RetInItemSearch' and key.userId = '"
                + Global.loginUser.getUserId() + "'");
        if(listMedicine == null){
            listMedicine = new ArrayList();
        }
        tblMedicineModel.setListCodeFilter(listMedicine);
        tblMedicineModel.addEmptyRow();
        tblCodeFilter.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCodeFilter.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblCodeFilter.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void initTableRetInItemList() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        tblReturnItemList.getColumnModel().getColumn(0).setPreferredWidth(30); //Sale Date
        tblReturnItemList.getColumnModel().getColumn(1).setPreferredWidth(60); //Vou No
        tblReturnItemList.getColumnModel().getColumn(2).setPreferredWidth(30); //Ref. Vou.
        tblReturnItemList.getColumnModel().getColumn(3).setPreferredWidth(30); //Exp-Date
        tblReturnItemList.getColumnModel().getColumn(4).setPreferredWidth(30); //Item Code
        tblReturnItemList.getColumnModel().getColumn(5).setPreferredWidth(100); //Item Name
        tblReturnItemList.getColumnModel().getColumn(6).setPreferredWidth(30); //Qty
        tblReturnItemList.getColumnModel().getColumn(7).setPreferredWidth(30); //Price

        tblReturnItemList.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
    }

    private void search() {
        List<ReturnInItemList> listRInItems = dao.findAllHSQL(getHSQL());
        tblRetInItemList.setListRetInItems(listRInItems);
    }

    private void select() {
        int index = tblReturnItemList.convertRowIndexToModel(tblReturnItemList.getSelectedRow());

        if (index >= 0) {
            ReturnInItemList rii = tblRetInItemList.getSelectedRetInItem(index);
            observer.selected("RetInItemSearch", rii);
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select sale item for return in.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getHSQL() {
        String strHSQL = "select i from ReturnInItemList i where deleted = false ";

        if(!txtTrcode.getText().isEmpty()){
            if(Util1.getPropValue("system.app.usage.type").equals("Hospital")){
                strHSQL = strHSQL + " and i.regNo = '"
                    + txtTrcode.getText() + "'";
            }else{
                strHSQL = strHSQL + " and i.traderId = '"
                    + txtTrcode.getText() + "'";
            }
        }
        
        if (DateUtil.isValidDate(txtFrom.getText()) && DateUtil.isValidDate(txtTo.getText())) {
            strHSQL = strHSQL + " and i.saleDate between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText())
                    + "' and '" + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "' ";
        } else {
        }

        if (!txtSalevou.getText().isEmpty()) {
            strHSQL = strHSQL + " and i.key.saleInvId like '" + txtSalevou.getText()
                    + "%'";
        }

        if (!txtRefNo.getText().isEmpty()) {
            strHSQL = strHSQL + " and i.remark like '" + txtRefNo.getText()
                    + "%'";
        }

        if (tblMedicineModel.getListCodeFilter().size() > 1) {
            strHSQL = strHSQL + " and i.key.item.medId in ("
                    + tblMedicineModel.getFilterCodeStr() + ")";
        }

        return strHSQL;
    }

    private void actionMapping() {
        //F8 event on tblSale
        tblCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCodeFilter.getActionMap().put("F8-Action", actionMedDelete);
    }

    private Action actionMedDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblCodeFilter.getSelectedRow() >= 0) {
                tblMedicineModel.delete(tblCodeFilter.getSelectedRow());
            }
        }
    };

    private void getCustomerList() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Customer List", dao);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tblReturnItemList = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSalevou = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRefNo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTrcode = new javax.swing.JTextField();
        txtTrName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCodeFilter = new javax.swing.JTable();
        butSearch = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Return In Item Search");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(1042, 600));

        tblReturnItemList.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblReturnItemList.setModel(tblRetInItemList);
        tblReturnItemList.setRowHeight(23);
        tblReturnItemList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblReturnItemListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblReturnItemList);

        jLabel1.setText("From ");

        txtFrom.setEditable(false);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel2.setText("To");

        txtTo.setEditable(false);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setText("Sale Vou");

        jLabel4.setText("Ref. No.");

        jLabel5.setText("Trader ");

        txtTrcode.setEditable(false);
        txtTrcode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTrcodeMouseClicked(evt);
            }
        });
        txtTrcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTrcodeActionPerformed(evt);
            }
        });
        txtTrcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTrcodeFocusLost(evt);
            }
        });

        txtTrName.setEditable(false);
        txtTrName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtTrName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTrNameMouseClicked(evt);
            }
        });

        tblCodeFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblCodeFilter.setModel(tblMedicineModel);
        tblCodeFilter.setRowHeight(23);
        jScrollPane1.setViewportView(tblCodeFilter);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSalevou)
                            .addComponent(txtRefNo)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtTrcode, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTrName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtSalevou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTrcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTrName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(jLabel5)))
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtTrName, txtTrcode});

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSearch)))
                .addGap(13, 13, 13))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butSearch)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFrom.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFromMouseClicked

    private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTo.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtToMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void tblReturnItemListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReturnItemListMouseClicked
        if (evt.getClickCount() == 2 && tblReturnItemList.getSelectedRow() >= 0) {
            select();
        }
    }//GEN-LAST:event_tblReturnItemListMouseClicked

    private void txtTrcodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTrcodeMouseClicked
        /*if (evt.getClickCount() == 2) {
            getCustomerList();
        }*/
    }//GEN-LAST:event_txtTrcodeMouseClicked

    private void txtTrNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTrNameMouseClicked
        /*if (evt.getClickCount() == 2) {
            getCustomerList();
        }*/
    }//GEN-LAST:event_txtTrNameMouseClicked

    private void txtTrcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTrcodeActionPerformed
        if (txtTrcode.getText() == null || txtTrcode.getText().isEmpty()) {
            txtTrName.setText(null);
        }
    }//GEN-LAST:event_txtTrcodeActionPerformed

    private void txtTrcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTrcodeFocusLost
        if (txtTrcode.getText() == null || txtTrcode.getText().isEmpty()) {
            txtTrName.setText(null);
        }
    }//GEN-LAST:event_txtTrcodeFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblCodeFilter;
    private javax.swing.JTable tblReturnItemList;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtSalevou;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JTextField txtTrName;
    private javax.swing.JTextField txtTrcode;
    // End of variables declaration//GEN-END:variables
}
