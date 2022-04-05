/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CompoundKeyTraderOp;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderOpening;
import com.cv.app.pharmacy.database.view.VTraderOpToAcc;
import com.cv.app.pharmacy.ui.common.TraderOpTableModel;
import com.cv.app.pharmacy.ui.common.TraderOpTrListTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import com.opencsv.CSVReader;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author Eitar
 */
public class TraderOpSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(TraderOpSetup.class.getName());
    //private List<Trader> listTrader;
    private final AbstractDataAccess dao = Global.dao;
    private Trader selTrader;
    private TraderOpTableModel tblEntryModel = new TraderOpTableModel(true);
    private TraderOpTableModel tblLastOpModel = new TraderOpTableModel();
    private TableRowSorter<TableModel> sorter;
    private final TraderOpTrListTableModel tblTraderModel = new TraderOpTrListTableModel();

    /**
     * Creates new form TraderOpSetup
     */
    public TraderOpSetup() {
        initComponents();
        //listTrader = dao.findAllHSQL("select t from Trader t where t.active = true");
        initTblTrader();
        initTblEntry();
        initTblLastOp();
        actionMapping();
        sorter = new TableRowSorter(tblTrader.getModel());
        tblTrader.setRowSorter(sorter);
    }

    private void initTblTrader() {
        /*JTableBinding jTableBinding = SwingBindings.
         createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE, listTrader,
         tblTrader);
         JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${traderId}"));
         columnBinding.setColumnName("Code");
         columnBinding.setColumnClass(String.class);
         columnBinding.setEditable(false);
        
         columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${traderName}"));
         columnBinding.setColumnName("Name");
         columnBinding.setColumnClass(String.class);
         columnBinding.setEditable(false);
        
         columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${township.townshipName}"));
         columnBinding.setColumnName("Township");
         columnBinding.setColumnClass(String.class);
         columnBinding.setEditable(false);
        
         columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${active}"));
         columnBinding.setColumnName("Active");
         columnBinding.setColumnClass(Boolean.class);
         columnBinding.setEditable(false);
        
         jTableBinding.bind();*/

        tblTraderModel.setListTrader(dao.findAllHSQL("select t from Trader t where t.active = true"));
        //Adjust table column width
        TableColumn column = null;
        column = tblTrader.getColumnModel().getColumn(0);
        column.setPreferredWidth(60);

        column = tblTrader.getColumnModel().getColumn(1);
        column.setPreferredWidth(150);

        column = tblTrader.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);

        column = tblTrader.getColumnModel().getColumn(3);
        column.setPreferredWidth(20);

        //Define table selection model to single row selection.
        tblTrader.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblTrader.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = tblTrader.getSelectedRow();

                if (row != -1) {
                    selTrader = tblTraderModel.getTrader(tblTrader.convertRowIndexToModel(row));
                    tblEntryModel.setTrader(selTrader);
                    txtCusCode.setText(selTrader.getTraderId());
                    txtCusName.setText(selTrader.getTraderName());
                    String strSQL = "select o from TraderOpening o where o.key.trader.traderId = '"
                            + selTrader.getTraderId() + "'";
                    List<TraderOpening> prvOP = dao.findAllHSQL(strSQL);
                    tblLastOpModel.setListOp(prvOP);
                }
            }
        }
        );
    }

    private void initTblEntry() {
        tblEntry.setModel(tblEntryModel);
        tblEntryModel.addEmptyRow();

        //Adjust table column width
        TableColumn column = null;
        column = tblEntry.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);

        column = tblEntry.getColumnModel().getColumn(1);
        column.setPreferredWidth(50);

        column = tblEntry.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);

        JComboBox cboChargeType = new JComboBox();
        BindingUtil.BindCombo(cboChargeType, dao.findAll("Currency"));
        tblEntry.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cboChargeType));
    }

    private void initTblLastOp() {
        tblLastOp.setModel(tblLastOpModel);

        //Adjust table column width
        TableColumn column = null;
        column = tblLastOp.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);

        column = tblLastOp.getColumnModel().getColumn(1);
        column.setPreferredWidth(50);

        column = tblLastOp.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);
    }

    private void clear() {
        txtCusCode.setText(null);
        txtCusName.setText(null);
        tblEntryModel.clear();
        tblEntryModel.addEmptyRow();
        tblLastOpModel.clear();
    }

    private void save() {
        if (tblEntryModel.isValidEntry()) {
            try {
                dao.open();

                List<TraderOpening> listDeleteOp = tblLastOpModel.getDeleteList();
                for (TraderOpening op : listDeleteOp) {
                    dao.delete(op);
                }

                List<TraderOpening> listOp = tblEntryModel.getListOp();
                for (int i = 0; i < listOp.size() - 1; i++) {
                    dao.save(listOp.get(i));
                }

                clear();
            } catch (Exception ex) {
                log.error(ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void actionMapping() {
        //F8 event on tblEntry
        //KeyStroke delKey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.META_MASK);
        //KeyStroke delKey = KeyStroke.getKeyStroke("BACK_SPACE");
        KeyStroke delKey = KeyStroke.getKeyStroke("F8");
        //tblEntry.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblEntry.getInputMap().put(delKey, "F8-Action");
        tblEntry.getActionMap().put("F8-Action", actionItemDelete);

        //F8 event on tblLastOp
        //tblLastOp.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblLastOp.getInputMap().put(delKey, "F8-Action");
        tblLastOp.getActionMap().put("F8-Action", actionItemDeleteLastOp);
    }

    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblEntry.getSelectedRow() >= 0) {
                tblEntryModel.delete(tblEntry.convertRowIndexToModel(tblEntry.getSelectedRow()));
            }
        }
    };

    private Action actionItemDeleteLastOp = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblLastOp.getSelectedRow() >= 0) {
                tblLastOpModel.delete(tblLastOp.convertRowIndexToModel(tblLastOp.getSelectedRow()));
            }
        }
    };

    private void processCSV(File file) {
        if (file != null) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                try ( CSVReader csvReader = new CSVReader(reader)) {
                    String[] nextRecord;
                    int ttlRec = 0;
                    int ttlSave = 0;

                    log.info("processCSV Start");
                    String appCurr = Util1.getPropValue("system.app.currency");
                    Currency curr = (Currency) dao.find(Currency.class, appCurr);
                    while ((nextRecord = csvReader.readNext()) != null) {
                        Trader trader = (Trader) dao.find(Trader.class, nextRecord[0]);
                        if (trader != null) {
                            CompoundKeyTraderOp key = new CompoundKeyTraderOp();
                            key.setCurrency(curr);
                            key.setOpDate(DateUtil.toDate("2020-01-01", "yyyy-MM-dd"));
                            key.setTrader(trader);
                            TraderOpening tOpening = new TraderOpening();
                            tOpening.setKey(key);
                            Double amount = Double.parseDouble(nextRecord[1].replace(",", ""));
                            tOpening.setAmount(amount);

                            try {
                                dao.save(tOpening);
                                ttlSave++;
                            } catch (Exception ex) {
                                log.error(ex.getMessage());
                            }
                        }

                        ttlRec++;
                        log.info("processCSV End: " + ttlRec + " - " + ttlSave);
                    }
                }
            } catch (IOException | NumberFormatException ex) {
                log.error("processCSV : " + ex.getMessage());
            } finally {
                dao.close();
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

        txtFilter = new javax.swing.JTextField();
        butFilter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTrader = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        txtCusName = new javax.swing.JTextField();
        txtCusCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblEntry = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLastOp = new javax.swing.JTable();
        btnToAcc = new javax.swing.JButton();
        butUpload = new javax.swing.JButton();

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        butFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butFilter.setText("...");

        tblTrader.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblTrader.setModel(tblTraderModel);
        tblTrader.setRowHeight(23);
        tblTrader.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblTrader);

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtCusCode.setEditable(false);
        txtCusCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Code");

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Entry"));

        tblEntry.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblEntry.setModel(new javax.swing.table.DefaultTableModel(
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
        tblEntry.setRowHeight(23);
        tblEntry.setShowVerticalLines(false);
        jScrollPane3.setViewportView(tblEntry);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Last Opening"));

        tblLastOp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tblLastOp.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblLastOp.setModel(new javax.swing.table.DefaultTableModel(
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
        tblLastOp.setRowHeight(23);
        tblLastOp.setShowVerticalLines(false);
        jScrollPane2.setViewportView(tblLastOp);

        btnToAcc.setText("To Acc");
        btnToAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToAccActionPerformed(evt);
            }
        });

        butUpload.setText("CSV");
        butUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(butUpload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnToAcc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butClear)
                        .addComponent(butSave))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnToAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(butUpload))
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(butFilter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
      if (txtFilter.getText().isEmpty()) {
          sorter.setRowFilter(null);
      } else {
          sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
      }
  }//GEN-LAST:event_txtFilterKeyReleased

    private void btnToAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToAccActionPerformed
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String strDate = JOptionPane.showInputDialog("Please enter opening date : ");
            String appCurr = Util1.getPropValue("system.app.currency");
            String strSql = "select o from VTraderOpToAcc o where o.key.opDate = '"
                    + DateUtil.toDateStrMYSQL(strDate) + "' "
                    + " and o.key.currency.currencyCode = '" + appCurr + "'";
            List<VTraderOpToAcc> listTraderOp = dao.findAllHSQL(strSql);
            log.info("Total Records : " + listTraderOp.size());
            if (listTraderOp.size() > 0) {
                for (VTraderOpToAcc vtota : listTraderOp) {
                    if (Global.mqConnection != null) {
                        if (Global.mqConnection.isStatus()) {
                            try {
                                ActiveMQConnection mq = Global.mqConnection;
                                MapMessage msg = mq.getMapMessageTemplate();
                                msg.setString("program", Global.programId);
                                msg.setString("entity", "OPENING-CV");
                                msg.setString("sourceAccId", vtota.getSourceAccId());
                                msg.setString("currency", vtota.getKey().getCurrency().getCurrencyCode());
                                Trader trader = vtota.getKey().getTrader();
                                if (trader != null) {
                                    if (trader.getTraderGroup() != null) {
                                        CustomerGroup cg = trader.getTraderGroup();
                                        msg.setString("deptId", cg.getDeptId());
                                    } else {
                                        msg.setString("deptId", "-");
                                    }
                                } else {
                                    msg.setString("deptId", "-");
                                }
                                msg.setString("cusId", trader.getTraderId());
                                msg.setString("cusType", vtota.getDiscriminator());
                                msg.setBoolean("deleted", false);
                                msg.setDouble("opAmount", vtota.getAmount());

                                msg.setString("queueName", "INVENTORY");

                                //mq.sendMessage(Global.queueName, msg);
                                mq.sendMessage("ACCOUNT", msg);
                            } catch (Exception ex) {
                                log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + listTraderOp + " - " + ex);
                            }
                            //i = listTraderOp.size();
                        }
                    }

                }
            }
        }
    }//GEN-LAST:event_btnToAccActionPerformed

    private void butUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUploadActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processCSV(selectedFile);
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }//GEN-LAST:event_butUploadActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnToAcc;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butFilter;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butUpload;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblEntry;
    private javax.swing.JTable tblLastOp;
    private javax.swing.JTable tblTrader;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
