/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.PCSearchTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PriceChangeSearch extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(PriceChangeSearch.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private Object parent;
    private SelectionObserver observer;
    private int selectedRow = -1;
    //private List<PriceChangeHis> listPCH = new ArrayList();
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "PCSearch");
    private PCSearchTableModel tableModel = new PCSearchTableModel();
    private VouFilter vouFilter;

    /**
     * Creates new form PriceChangeSearch
     */
    public PriceChangeSearch(Object parent, SelectionObserver observer, AbstractDataAccess dao) {
        initComponents();

        this.parent = parent;
        this.observer = observer;
        //this.dao = dao;

        //Search history
        getPrvFilter();

        try {
            dao.open();
            initTableMedicine();
            initTableVoucher();
            dao.close();
        } catch (Exception ex) {
            log.error("PriceChangeSearch : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        addSelectionListenerVoucher();
        search();
    }

    private void initTableMedicine() {
        try {
            List<VouCodeFilter> listMedicine = dao.findAll("VouCodeFilter",
                    "key.tranOption = 'PCSearch' and key.userId = '"
                    + Global.machineId + "'");
            tblMedicineModel.setListCodeFilter(listMedicine);
        } catch (Exception ex) {
            log.error("initTableMedicine : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblMedicineModel.addEmptyRow();
        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMedicine.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void initTableVoucher() {
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(50);

        tblVoucher.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    private void actionMapping() {
        //F3 event on tblSale
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblMedicine.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblMedicine.getActionMap().put("F8-Action", actionMedDelete);

        //Enter event on tblSale
        //tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        //tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionMedList">
    private Action actionMedList = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblMedicine.getCellEditor() != null) {
                    tblMedicine.getCellEditor().stopCellEditing();
                }
                getMedList("");
            } catch (Exception ex) {
                //No entering medCode, only press F3

            }
        }
    };// </editor-fold>
    private Action actionMedDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblMedicine.getSelectedRow() >= 0) {
                tblMedicineModel.delete(tblMedicine.getSelectedRow());
            }
        }
    };

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

    private String getHSQL() {
        String strSql = "select distinct v from PriceChangeHis1 v join v.listDetail h where v.priceChangeDate between '"
                + DateUtil.toDateStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQL(txtToDate.getText()) + "'";

        vouFilter.setFromDate(DateUtil.toDate(txtFromDate.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtToDate.getText()));

        if (txtDesp.getText() != null && !txtDesp.getText().isEmpty()) {
            strSql = strSql + " and v.remark like '%" + txtDesp.getText() + "%'";
        }
        vouFilter.setRemark(txtDesp.getText());

        if (txtVouNo.getText() != null && !txtVouNo.getText().isEmpty()) {
            strSql = strSql + " and v.priceChangeVouId = '" + txtVouNo.getText() + "'";
        }
        vouFilter.setVouNo(txtVouNo.getText());

        //Filter history save
        try {
            dao.save(vouFilter);
        } catch (Exception ex) {
            log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        List<VouCodeFilter> listMedicine = tblMedicineModel.getListCodeFilter();

        if (listMedicine.size() > 1) {
            strSql = strSql + " and h.med.medId in (";
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

        return strSql;
    }

    // <editor-fold defaultstate="collapsed" desc="selected">
    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("MedicineList")) {
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMedInfo">
    public void getMedInfo(String medCode) {
        try {
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                selected("MedicineList", medicine);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                        "Invalid.", JOptionPane.ERROR_MESSAGE);
                //getMedList(medCode);
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getMessage());
        } finally {
            dao.close();
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

    private void select() {
        if (selectedRow >= 0) {
            observer.selected("PriceChangeVouList",
                    tableModel.getSelectVou(tblVoucher.convertRowIndexToModel(selectedRow)));

            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select price change.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getPrvFilter() {
        try {
            VouFilter tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'PriceChangeSearch'"
                    + " and key.userId = '" + Global.machineId + "'");

            if (tmpFilter == null) {
                vouFilter = new VouFilter();
                vouFilter.getKey().setTranOption("PriceChangeSearch");
                vouFilter.getKey().setUserId(Global.machineId);

                txtFromDate.setText(DateUtil.getTodayDateStr());
                txtToDate.setText(DateUtil.getTodayDateStr());
            } else {
                vouFilter = tmpFilter;
                txtFromDate.setText(DateUtil.toDateStr(vouFilter.getFromDate()));
                txtToDate.setText(DateUtil.toDateStr(vouFilter.getToDate()));
                txtVouNo.setText(vouFilter.getVouNo());
                txtDesp.setText(vouFilter.getRemark());
            }
        } catch (Exception ex) {
            log.error("getPrvFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void search() {
        String strSql = getHSQL();

        try {
            dao.open();
            tableModel.setListPriceChangeHis(dao.findAllHSQL(strSql));
            dao.close();
        } catch (Exception ex) {
            log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        lblTotalRec.setText("Total Records : " + tableModel.getRowCount());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        butSearch = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFromDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtToDate = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(tblMedicineModel);
        butSelect = new javax.swing.JButton();
        lblTotalRec = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

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

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Remark");

        txtDesp.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou No");

        txtVouNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblMedicine.setFont(Global.textFont);
        tblMedicine.setRowHeight(23);
        tblMedicine.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblMedicine);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDesp)
                            .addComponent(txtVouNo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
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
                    .addComponent(jLabel7)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        butSelect.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSelect.setText("Select");
        butSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelectActionPerformed(evt);
            }
        });

        lblTotalRec.setText("Total Records : 0");

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(tableModel);
        tblVoucher.setRowHeight(23);
        tblVoucher.setShowVerticalLines(false);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVoucher);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butSearch, butSelect});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butSelect)
                        .addComponent(butSearch))
                    .addComponent(lblTotalRec))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

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

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void butSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelectActionPerformed
        select();
    }//GEN-LAST:event_butSelectActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtFromDate;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
