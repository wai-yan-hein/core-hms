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
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.opencsv.CSVReader;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
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
    //private TraderOpTableModel tblEntryModel = new TraderOpTableModel(true);
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
        //initTblEntry();
        initTblLastOp();
        actionMapping();
        sorter = new TableRowSorter(tblTrader.getModel());
        tblTrader.setRowSorter(sorter);
        initCombo();
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTblTrader() {
        try {
            tblTraderModel.setListTrader(dao.findAllHSQL("select t from Trader t where t.active = true"));
            //Adjust table column width
            TableColumn column = null;
            column = tblTrader.getColumnModel().getColumn(0);
            column.setPreferredWidth(50);

            column = tblTrader.getColumnModel().getColumn(1);
            column.setPreferredWidth(150);

            column = tblTrader.getColumnModel().getColumn(2);
            column.setPreferredWidth(80);

            column = tblTrader.getColumnModel().getColumn(3);
            column.setPreferredWidth(20);

            //Define table selection model to single row selection.
            tblTrader.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblTrader.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                int row = tblTrader.getSelectedRow();

                if (row != -1) {
                    selTrader = tblTraderModel.getTrader(tblTrader.convertRowIndexToModel(row));
                    //tblEntryModel.setTrader(selTrader);
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCusCode.setText(selTrader.getStuCode());
                    } else {
                        txtCusCode.setText(selTrader.getTraderId());
                    }

                    txtCusName.setText(selTrader.getTraderName());
                    String strSQL = "select o from TraderOpening o where o.key.trader.traderId = '"
                            + selTrader.getTraderId() + "'";
                    try {
                        List<TraderOpening> prvOP = dao.findAllHSQL(strSQL);
                        tblLastOpModel.setListOp(prvOP);
                    } catch (Exception ex) {
                        log.error("valueChanged : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTblTrader : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    /*private void initTblEntry() {
        try {
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
        } catch (Exception ex) {
            log.error("initTblEntry : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }*/
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
        txtAmount.setText(null);
        txtOPDate.setText(null);
        txtDesp.setText(null);
        //tblEntryModel.clear();
        //tblEntryModel.addEmptyRow();
        tblLastOpModel.clear();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (Util1.isNull(txtOPDate.getText().trim(), "").isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening date.",
                    "Date", JOptionPane.ERROR_MESSAGE);
        } else if (!DateUtil.isValidDate(txtOPDate.getText().trim())) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening date.",
                    "Date", JOptionPane.ERROR_MESSAGE);
        } else if (Util1.isNull(txtDesp.getText().trim(), "").isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid desp.",
                    "Desp", JOptionPane.ERROR_MESSAGE);
        } else if (Util1.isNull(txtAmount.getText().trim(), "").isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening amount.",
                    "Amount", JOptionPane.ERROR_MESSAGE);
        } else if (!NumberUtil.isNumber(txtAmount.getText().trim())) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening amount.",
                    "Amount", JOptionPane.ERROR_MESSAGE);
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.open();

                /*List<TraderOpening> listDeleteOp = tblLastOpModel.getDeleteList();
                for (TraderOpening op : listDeleteOp) {
                    dao.delete(op);
                }*/
                TraderOpening record = new TraderOpening(new CompoundKeyTraderOp(selTrader));
                Currency curr = (Currency) cboCurrency.getSelectedItem();
                record.getKey().setCurrency(curr);
                record.getKey().setDesp(txtDesp.getText().trim());
                record.getKey().setOpDate(DateUtil.toDate(txtOPDate.getText().trim()));
                record.getKey().setTrader(selTrader);
                record.setAmount(NumberUtil.getDouble(txtAmount.getText().trim()));

                dao.save(record);

                clear();

                String strSQL = "select o from TraderOpening o where o.key.trader.traderId = '"
                        + selTrader.getTraderId() + "'";

                List<TraderOpening> prvOP = dao.findAllHSQL(strSQL);
                tblLastOpModel.setListOp(prvOP);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(), ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                log.error("save : " + ex.getMessage());
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
        //tblEntry.getInputMap().put(delKey, "F8-Action");
        //tblEntry.getActionMap().put("F8-Action", actionItemDelete);

        //F8 event on tblLastOp
        //tblLastOp.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblLastOp.getInputMap().put(delKey, "F8-Action");
        tblLastOp.getActionMap().put("F8-Action", actionItemDeleteLastOp);
    }

    /*private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblEntry.getSelectedRow() >= 0) {
                tblEntryModel.delete(tblEntry.convertRowIndexToModel(tblEntry.getSelectedRow()));
            }
        }
    };*/
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
            } catch (Exception ex) {
                log.error("processCSV1 : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase().replace(" ", "");
            String tmp2 = entry.getStringValue(1).toUpperCase().replace(" ", "");
            String text = txtFilter.getText().toUpperCase().replace(" ", "");
            return tmp1.startsWith(text) || tmp2.startsWith(text);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLastOp = new javax.swing.JTable();
        btnToAcc = new javax.swing.JButton();
        butUpload = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtOPDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        txtAmount = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(1000, 600));

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

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("OP Date : ");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Currency : ");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Desp : ");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Amount : ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtOPDate))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtAmount)))
                            .addGap(144, 144, 144))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(butUpload)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnToAcc)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(butSave)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrency, 0, 108, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel4, jLabel5, jLabel6});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboCurrency, txtAmount, txtDesp, txtOPDate});

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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtOPDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butClear)
                        .addComponent(butSave))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnToAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butUpload)))
                .addContainerGap(29, Short.MAX_VALUE))
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(butFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
          sorter.setRowFilter(startsWithFilter);
      }
  }//GEN-LAST:event_txtFilterKeyReleased

    private void btnToAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToAccActionPerformed
        String isIntegration = Util1.getPropValue("system.integration");
        try {
            if (isIntegration.toUpperCase().equals("Y")) {
                String strDate = JOptionPane.showInputDialog("Please enter opening date : ");
                String appCurr = Util1.getPropValue("system.app.currency");
                String strSql = "select o from VTraderOpToAcc o where o.key.opDate = '"
                        + DateUtil.toDateStrMYSQL(strDate) + "' "
                        + " and o.key.currency.currencyCode = '" + appCurr + "'";
                List<VTraderOpToAcc> listTraderOp = dao.findAllHSQL(strSql);
                log.info("Total Records : " + listTraderOp.size());
                if (!listTraderOp.isEmpty()) {
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
                                } catch (JMSException ex) {
                                    log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + listTraderOp + " - " + ex);
                                }
                                //i = listTraderOp.size();
                            }
                        }

                    }
                }
            }
        } catch (Exception ex) {
            log.error("btnToAccActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
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
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblLastOp;
    private javax.swing.JTable tblTrader;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JFormattedTextField txtOPDate;
    // End of variables declaration//GEN-END:variables
}
