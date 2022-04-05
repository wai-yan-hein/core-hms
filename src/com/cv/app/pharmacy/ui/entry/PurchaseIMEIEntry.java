/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PurDetailHis;
import com.cv.app.pharmacy.database.entity.PurHis;
import com.cv.app.pharmacy.database.entity.PurchaseIMEINo;
import com.cv.app.pharmacy.ui.common.PurchaseIMEIDetailTableModel;
import com.cv.app.pharmacy.ui.common.PurchaseIMEITableModel;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
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
public class PurchaseIMEIEntry extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(PurchaseIMEIEntry.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final PurchaseIMEITableModel purIMEITableModel = new PurchaseIMEITableModel();
    private final PurchaseIMEIDetailTableModel detailTableModel = new PurchaseIMEIDetailTableModel();
    private List<PurchaseIMEINo> listDetail = new ArrayList();
    private TableRowSorter<TableModel> sorter;
    private int mouseClick = 2;
    
    /**
     * Creates new form PurchaseIMEIEntry
     */
    public PurchaseIMEIEntry() {
        initComponents();
        initTable();
        actionMapping();
        sorter = new TableRowSorter(tblIMEI.getModel());
        tblIMEI.setRowSorter(sorter);
        
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

    @Override
    public void selected(Object source, Object selectObj) {
        try {
            PurHis currPurVou = (PurHis) dao.find(PurHis.class, ((PurHis) selectObj).getPurInvId());
            txtVouNo.setText(currPurVou.getPurInvId());
            txtVouDate.setText(DateUtil.toDateStr(currPurVou.getPurDate()));
            txtSupNo.setText(currPurVou.getCustomerId().getTraderId());
            txtSupName.setText(currPurVou.getCustomerId().getTraderName());
            purIMEITableModel.setListDetail(currPurVou.getPurDetailHis());
        } catch (Exception ex) {
            log.error("selected : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {

        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblIMEI.setCellSelectionEnabled(true);
        }
        tblIMEI.getTableHeader().setFont(Global.lableFont);
        tblPurDetail.getTableHeader().setFont(Global.lableFont);
        detailTableModel.setParent(tblIMEI);

        /*tblIMEI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblIMEI.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblIMEI.getSelectedRow() >= 0) {
                    int selectRow = tblIMEI.convertRowIndexToModel(tblIMEI.getSelectedRow());
                    if (selectRow >= 0) {
                        PurDetailHis pdh = purIMEITableModel.getPurDetailHis(selectRow);
                        if (pdh == null) {
                            detailTableModel.setListIMEINo(null);
                        } else {
                            detailTableModel.getIMEIList(txtVouNo.getText(),
                                    pdh.getPurDetailId().toString(), pdh.getMedId().getMedId());
                        }
                    }
                }
            }
        });*/
        tblPurDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPurDetail.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblPurDetail.getSelectedRow() >= 0) {
                    int selectRow = tblPurDetail.convertRowIndexToModel(tblPurDetail.getSelectedRow());
                    if (selectRow >= 0) {
                        PurDetailHis pdh = purIMEITableModel.getPurDetailHis(selectRow);
                        if (pdh == null) {
                            detailTableModel.setListIMEINo(null);
                        } else {
                            detailTableModel.getIMEIList(txtVouNo.getText(),
                                    pdh.getPurDetailId().toString(), pdh.getMedId().getMedId(), pdh.getQuantity());
                        }
                    }
                }
            }
        });
    }

    private void clear() {
        try {
            if (tblIMEI.getCellEditor() != null) {
                tblIMEI.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        txtSupNo.setText("");
        txtSupName.setText("");
        txtVouDate.setText("");
        txtVouNo.setText("");
        txtFilter.setText("");
        purIMEITableModel.clear();
        detailTableModel.clear();
    }

    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            if (NumberUtil.isNumber(txtFilter.getText())) {
                if (entry.getStringValue(0).toUpperCase().startsWith(
                        txtFilter.getText().toUpperCase())) {
                    return true;
                }
            } else if (entry.getStringValue(1).toUpperCase().startsWith(
                    txtFilter.getText().toUpperCase())) {
                return true;
            }
            return false;
        }
    };

    private void actionMapping() {
        tblIMEI.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblIMEI.getActionMap().put("ENTER-Action", actionTblIMEIEnterKey);

        tblIMEI.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblIMEI.getActionMap().put("F8-Action", actionTblIMEIDelete);

        tblIMEI.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "DELETE-Action");
        tblIMEI.getActionMap().put("DELETE-Action", actionTblIMEIDelete);

    }
    private Action actionTblIMEIEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(tblIMEI.getCellEditor() != null){
                    tblIMEI.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblIMEI.getSelectedRow();
            int col = tblIMEI.getSelectedColumn();
            listDetail = detailTableModel.setList();
            PurchaseIMEINo sdh = listDetail.get(row);

            if (col == 0 && sdh.getKey().getImei1() != null) {

                tblIMEI.setColumnSelectionInterval(1, 1);
            } else if (col == 1) {
                tblIMEI.setColumnSelectionInterval(2, 2); //Move to Qty
            } else if (col == 2 && sdh.getKey().getImei1() != null) {
                if ((row + 1) < listDetail.size()) {
                    tblIMEI.setRowSelectionInterval(row + 1, row + 1);

                }
                tblIMEI.setColumnSelectionInterval(0, 0); //Move to Code
            }

        }
    };

    private Action actionTblIMEIDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblIMEI.getSelectedRow() >= 0) {
                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                            "IMEI delete", JOptionPane.YES_NO_OPTION);

                    if(tblIMEI.getCellEditor() != null){
                        tblIMEI.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    detailTableModel.delete(tblIMEI.convertRowIndexToModel(tblIMEI.getSelectedRow()));
                }
            }

        }
    };

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
        txtVouDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSupNo = new javax.swing.JTextField();
        txtSupName = new javax.swing.JTextField();
        butHistory = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPurDetail = new javax.swing.JTable();
        btnClear = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblIMEI = new javax.swing.JTable();

        jLabel1.setText("Date : ");

        txtVouDate.setEditable(false);

        jLabel2.setText("Vou No : ");

        txtVouNo.setEditable(false);

        jLabel3.setText("Supplier : ");

        txtSupNo.setEditable(false);

        txtSupName.setEditable(false);

        butHistory.setText("History");
        butHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butHistoryActionPerformed(evt);
            }
        });

        tblPurDetail.setModel(purIMEITableModel);
        tblPurDetail.setRowHeight(23);
        tblPurDetail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblPurDetailKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblPurDetail);

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSupNo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSupName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(4, 4, 4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)
                        .addGap(0, 7, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butHistory)
                    .addComponent(btnClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSupNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel4.setText("Filter : ");

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblIMEI.setModel(detailTableModel);
        tblIMEI.setRowHeight(23);
        jScrollPane2.setViewportView(tblIMEI);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butHistoryActionPerformed
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Purchase Voucher Search", dao);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_butHistoryActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorter.setRowFilter(startsWithFilter);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void tblPurDetailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPurDetailKeyReleased
        // TODO add your handling code here:


    }//GEN-LAST:event_tblPurDetailKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton butHistory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblIMEI;
    private javax.swing.JTable tblPurDetail;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtSupName;
    private javax.swing.JTextField txtSupNo;
    private javax.swing.JFormattedTextField txtVouDate;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
