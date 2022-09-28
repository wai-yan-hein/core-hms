/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.StockOutstanding;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author winswe
 */
public class OutstandingStockList extends javax.swing.JDialog implements SelectionObserver {

    static Logger log = Logger.getLogger(OutstandingStockList.class.getName());
    private AbstractDataAccess dao;
    private Trader trader;
    private List<StockOutstanding> listOutstanding = new ArrayList();
    private SelectionObserver observer;
    private int selectedRow = -1;
    private String source;

    /**
     * Creates new form OutstandingStockList
     */
    public OutstandingStockList(AbstractDataAccess dao, SelectionObserver observer,
            String source) {
        super(Util1.getParent(), true);

        this.dao = dao;
        this.observer = observer;
        this.source = source;

        initComponents();
        initTblOutstanding();
        addSelectionListenerVoucher();

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        this.show();
    }

    private void getCustomerList() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao, -1);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("CustomerList")) {
            trader = (Trader) selectObj;

            txtCusCode.setText(trader.getTraderId());
            txtCusName.setText(trader.getTraderName());
            listOutstanding.removeAll(listOutstanding);

            getOutstandingBalance();
        }
    }

    private void getOutstandingBalance() {
        if (trader != null) {
            try {
                ResultSet resultSet = null;
                switch (source) {
                    case "StockReceiving":
                        resultSet = dao.getPro("get_outstanding_balance", trader.getTraderId());
                        break;
                    case "StockIssuing":
                        resultSet = dao.getPro("get_outstanding_balance_sale", trader.getTraderId());
                        break;
                }

                if (resultSet != null) {
                    Medicine med = null;

                    dao.open();

                    while (resultSet.next()) {
                        System.out.println(resultSet.getString("med_id"));

                        if (med != null) {
                            if (!med.getMedId().equals(resultSet.getString("med_id"))) {
                                med = (Medicine) dao.find(Medicine.class, resultSet.getString("med_id"));
                            }
                        } else {
                            med = (Medicine) dao.find(Medicine.class, resultSet.getString("med_id"));
                        }
                        float qty = NumberUtil.FloatZero(resultSet.getFloat("ttl_qty"));
                        if (qty != 0.0) {
                            String qtyStr = MedicineUtil.getQtyInStr(med, qty);
                            StockOutstanding outstanding = new StockOutstanding(resultSet.getString("tran_opt"),
                                    resultSet.getDate("tran_date"),
                                    resultSet.getString("trader_id"),
                                    resultSet.getInt("ttl_qty"),
                                    resultSet.getString("inv_id"),
                                    qtyStr,
                                    med);

                            listOutstanding.add(outstanding);
                        }
                    }

                    initTblOutstanding();
                }
            } catch (Exception ex) {
                log.error("getOutstandingBalance : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
                dao.closeStatment();
            }
        }
    }

    private void initTblOutstanding() {
        JTableBinding jTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                listOutstanding, tblOutstanding);
        JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${tranOption}"));
        columnBinding.setColumnName("Option");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);

        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${tranDate}"));
        columnBinding.setColumnName("Date");
        columnBinding.setColumnClass(Date.class);
        columnBinding.setEditable(false);

        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${invId}"));
        columnBinding.setColumnName("Vou No");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);

        /*
         * columnBinding =
         * jTableBinding.addColumnBinding(ELProperty.create("${med.medId}"));
         * columnBinding.setColumnName("Code");
         * columnBinding.setColumnClass(String.class);
         * columnBinding.setEditable(false);
         */
        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${med.medName}"));
        columnBinding.setColumnName("Medicine");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);

        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${qtyStr}"));
        columnBinding.setColumnName("Balance");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);

        jTableBinding.bind();

        tblOutstanding.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblOutstanding.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblOutstanding.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblOutstanding.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblOutstanding.getColumnModel().getColumn(4).setPreferredWidth(50);

        tblOutstanding.getColumnModel().getColumn(1).setCellRenderer(new TableDateFieldRenderer());
    }

    private void addSelectionListenerVoucher() {
        //Define table selection model to single row selection.
        tblOutstanding.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblOutstanding.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        selectedRow = tblOutstanding.getSelectedRow();
                    }
                });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtCusCode = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOutstanding = new javax.swing.JTable();
        txtCusName = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Outstanding Stock List");

        txtCusCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusCodeMouseClicked(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Customer ");

        tblOutstanding.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblOutstanding.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOutstanding.setRowHeight(23);
        tblOutstanding.setShowVerticalLines(false);
        tblOutstanding.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOutstandingMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOutstanding);

        txtCusName.setEditable(false);
        txtCusName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCusName)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void txtCusCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusCodeMouseClicked
      if (evt.getClickCount() == 2) {
          getCustomerList();
      }
  }//GEN-LAST:event_txtCusCodeMouseClicked

  private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
      if (evt.getClickCount() == 2) {
          getCustomerList();
      }
  }//GEN-LAST:event_txtCusNameMouseClicked

  private void tblOutstandingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOutstandingMouseClicked
      if (evt.getClickCount() == 2 && selectedRow >= 0) {
          observer.selected("Outstanding",
                  listOutstanding.get(tblOutstanding.convertRowIndexToModel(selectedRow)));

          //this.dispose();
      }
  }//GEN-LAST:event_tblOutstandingMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOutstanding;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    // End of variables declaration//GEN-END:variables
}
