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
import com.cv.app.pharmacy.database.entity.Supplier;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.PurchaseVoucher;
import com.cv.app.pharmacy.ui.common.PurVouDetailTableModel;
import com.cv.app.pharmacy.ui.common.PurVouListTableModel;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PurchaseCostUpdate1 extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(PurchaseCostUpdate1.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final PurVouListTableModel vouListTableModel = new PurVouListTableModel();
    private final PurVouDetailTableModel detailTableModel = new PurVouDetailTableModel();
    private int mouseClick = 2;
    private final TableRowSorter<TableModel> sorter;
    private String selTraderId = "-";
    /**
     * Creates new form PurchaseCostUpdate
     */
    public PurchaseCostUpdate1() {
        initComponents();
        sorter = new TableRowSorter(tblVouList.getModel());
        tblVouList.setRowSorter(sorter);

        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());

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

        initTable();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            switch (source.toString()) {
                case "CustomerList":
                    Supplier sup = (Supplier) selectObj;
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtTraderId.setText(sup.getStuCode());
                    } else {
                        txtTraderId.setText(sup.getTraderId());
                    }
                    selTraderId = sup.getTraderId();
                    txtSupName.setText(sup.getTraderName());
                    break;
            }
        }
    }

    private void getCustomer() {
        Supplier sup = null;

        if (txtTraderId.getText() != null && !txtTraderId.getText().isEmpty()) {
            try {
                dao.open();
                String traderId = txtTraderId.getText().trim().toUpperCase();
                String strFieldName = "o.traderId";
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    strFieldName = "o.stuCode";
                }
                List<Trader> listT = dao.findAllHSQL("select o from Supplier o where " + strFieldName + " = '" + traderId + "'");
                if (listT != null) {
                    if (!listT.isEmpty()) {
                        sup = (Supplier) listT.get(0);
                    }
                }

            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        if (sup == null && !txtTraderId.getText().isEmpty()) {
            getCustomerList();
        } else {
            selected("CustomerList", sup);
        }

    }

    private void getCustomerList() {
        int locationId = -1;
        TraderSearchDialog dialog = new TraderSearchDialog(this,
                "Supplier List", locationId);
        dialog.setTitle("Supplier List");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void initTable() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        tblVouList.getColumnModel().getColumn(0).setPreferredWidth(27);//Date
        tblVouList.getColumnModel().getColumn(1).setPreferredWidth(60);//Vou No
        tblVouList.getColumnModel().getColumn(2).setPreferredWidth(180);//Supplier
        tblVouList.getColumnModel().getColumn(3).setPreferredWidth(60);//Vou Total
        tblVouList.getColumnModel().getColumn(4).setPreferredWidth(3);//Select
        tblVouList.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());

        tblVouList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVouList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setSelect();
            }
        });

        tblDetail.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblDetail.getColumnModel().getColumn(1).setPreferredWidth(150);//Medicine Name
        tblDetail.getColumnModel().getColumn(2).setPreferredWidth(50);//Relstr
        tblDetail.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblDetail.getColumnModel().getColumn(3).setCellRenderer(new TableDateFieldRenderer());
        tblDetail.getColumnModel().getColumn(4).setPreferredWidth(40);//Qty
        tblDetail.getColumnModel().getColumn(5).setPreferredWidth(30);//Pur price
        tblDetail.getColumnModel().getColumn(6).setPreferredWidth(20);//Discount1
        tblDetail.getColumnModel().getColumn(7).setPreferredWidth(20);//FOC
        tblDetail.getColumnModel().getColumn(8).setPreferredWidth(20);//Expense %
        tblDetail.getColumnModel().getColumn(9).setPreferredWidth(30);//Expense
        tblDetail.getColumnModel().getColumn(10).setPreferredWidth(30);//Unit cost
        tblDetail.getColumnModel().getColumn(11).setPreferredWidth(50);//Amount

        tblDetail.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tblDetail.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
    }

    private void search() {
        String strSql = "select date(ph.pur_date) pur_date, ph.pur_inv_id, ph.cus_id, t.trader_name, ph.vou_total,\n"
                + "t.stu_no \n"
                + "from pur_his ph left join trader t on ph.cus_id = t.trader_id\n"
                + "where deleted = false";
        strSql = strSql + " and date(ph.pur_date) between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";
        if (txtTraderId.getText() != null) {
            if (!txtTraderId.getText().trim().isEmpty()) {
                strSql = strSql + " and ph.cus_id = '" + selTraderId + "'";
            }
        }

        if (txtSupName.getText() != null) {
            if (!txtSupName.getText().trim().isEmpty()) {
                strSql = strSql + " and upper(t.trader_name) like '%" + txtSupName.getText().trim()
                        + "%'";
            }
        }

        strSql = strSql + " order by ph.pur_date, ph.pur_inv_id";
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<PurchaseVoucher> listPV = new ArrayList();
                double total = 0;
                while (rs.next()) {
                    PurchaseVoucher pv = new PurchaseVoucher();
                    pv.setPurDate(rs.getDate("pur_date"));
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        pv.setSupId(rs.getString("stu_no"));
                    } else {
                        pv.setSupId(rs.getString("cus_id"));
                    }
                    pv.setSupName(rs.getString("trader_name"));
                    pv.setVouTotal(rs.getDouble("vou_total"));
                    pv.setVouoNo(rs.getString("pur_inv_id"));
                    pv.setCheck(true);
                    total += rs.getDouble("vou_total");

                    listPV.add(pv);
                }
                vouListTableModel.setListPH(listPV);

                txtTotalRecord.setValue(listPV.size());
                txtTotalAmount.setValue(total);

                rs.close();
            }
        } catch (Exception ex) {
            log.error("search : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private List<PurDetailHis> getDetail(String vouNo) {
        List<PurDetailHis> listPDH = null;
        try {
            listPDH = dao.findAllHSQL(
                    "select o from PurDetailHis o where o.vouNo = '" + vouNo
                    + "' order by o.uniqueId");
            /*PurHis ph = (PurHis) dao.find(PurHis.class, vouNo);

            if (ph.getPurDetailHis().size() > 0) {
                listPDH = ph.getPurDetailHis();
            }*/
        } catch (Exception ex) {
            log.error("getDetail : " + ex.toString());
        } finally {
            dao.close();
        }
        return listPDH;
    }

    private void setSelect() {
        if (tblVouList.getSelectedRow() >= 0) {
            int selectIndex = tblVouList.convertRowIndexToModel(tblVouList.getSelectedRow());
            if (selectIndex != -1) {
                PurchaseVoucher pv = vouListTableModel.getSelected(selectIndex);
                if (pv != null) {
                    detailTableModel.setListD(getDetail(pv.getVouoNo()));
                }
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
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTraderId = new javax.swing.JTextField();
        txtSupName = new javax.swing.JTextField();
        butSearch = new javax.swing.JButton();
        butUpdate = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtPercent = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVouList = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txtTotalRecord = new javax.swing.JFormattedTextField();
        txtTotalAmount = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();

        jLabel1.setText("From : ");

        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel2.setText("To : ");

        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setText("Supplier : ");

        txtTraderId.setFont(Global.textFont);
        txtTraderId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTraderIdActionPerformed(evt);
            }
        });

        txtSupName.setFont(Global.textFont);
        txtSupName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSupNameMouseClicked(evt);
            }
        });

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        butUpdate.setText("Update");
        butUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUpdateActionPerformed(evt);
            }
        });

        jLabel4.setText("%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTraderId, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSupName, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butUpdate)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtTraderId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSearch)
                    .addComponent(butUpdate)
                    .addComponent(jLabel4)
                    .addComponent(txtPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblVouList.setFont(Global.textFont);
        tblVouList.setModel(vouListTableModel);
        tblVouList.setRowHeight(23);
        jScrollPane1.setViewportView(tblVouList);

        tblDetail.setFont(Global.textFont);
        tblDetail.setModel(detailTableModel);
        tblDetail.setRowHeight(23);
        jScrollPane2.setViewportView(tblDetail);

        jLabel5.setText("Total Record : ");

        txtTotalRecord.setEditable(false);
        txtTotalRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTotalAmount.setEditable(false);
        txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setText("Total Amount : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFrom.setText(strDate);
            }

        }
    }//GEN-LAST:event_txtFromMouseClicked

    private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTo.setText(strDate);
            }

        }
    }//GEN-LAST:event_txtToMouseClicked

    private void txtTraderIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTraderIdActionPerformed
        if (txtTraderId.getText() == null || txtTraderId.getText().isEmpty()) {
            txtSupName.setText(null);
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtTraderIdActionPerformed

    private void txtSupNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSupNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtSupNameMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void butUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUpdateActionPerformed
        String strVouList = vouListTableModel.getUpdateVoucher();
        float percent = NumberUtil.FloatZero(txtPercent.getText());
        if (!strVouList.equals("-") && percent != 0) {
            String strSql = "update pur_his ph join pur_join pj on ph.pur_inv_id = pj.pur_inv_id\n"
                    + "join pur_detail_his pdh on pj.pur_detail_id = pdh.pur_detail_id\n"
                    + "set item_expense_p = " + percent + ", item_expense = ifnull(pdh.pur_amount,0) * (" + percent + " / 100 ), \n"
                    + "pur_unit_cost = (((ifnull(pdh.pur_amount,0)/(ifnull(pdh.pur_smallest_qty,0) + ifnull(pdh.pur_foc_smallest_qty,0)))+((ifnull(pdh.pur_amount,0) * \n"
                    + "	(" + percent + " / 100 ))/(ifnull(pdh.pur_smallest_qty,0) + ifnull(pdh.pur_foc_smallest_qty,0)))) * ifnull(pdh.pur_smallest_qty,0))/\n"
                    + " ifnull(pdh.pur_qty,0)\n"
                    + "where ph.pur_inv_id in (" + strVouList + ") and ifnull(pdh.pur_qty,0) <> 0";
            log.info("vouList : " + strSql);
            try {
                dao.execSql(strSql);
                setSelect();
            } catch (Exception ex) {
                log.error("butUpdateActionPerformed : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }//GEN-LAST:event_butUpdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTable tblVouList;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPercent;
    private javax.swing.JTextField txtSupName;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotalAmount;
    private javax.swing.JFormattedTextField txtTotalRecord;
    private javax.swing.JTextField txtTraderId;
    // End of variables declaration//GEN-END:variables
}
