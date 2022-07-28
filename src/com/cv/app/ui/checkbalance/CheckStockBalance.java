/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.checkbalance;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.RegNo;
import com.cv.app.pharmacy.database.entity.StockCheckBatch;
import com.cv.app.pharmacy.database.entity.StockCheckDetail;
import com.cv.app.ui.common.CheckStockBalanceTableModel;
import com.cv.app.util.Util1;
import com.cv.app.util.DateUtil;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.helper.CheckStockBalanceEntity;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.NumberUtil;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;

/**
 *
 * @author ACER
 */
public class CheckStockBalance extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(CheckStockBalance.class.getName());
    private final AbstractDataAccess dao = Global.dao; //Use for database access
    private int mouseClick = 2;
    private final CheckStockBalanceTableModel tableModel = new CheckStockBalanceTableModel();

    /**
     * Creates new form CheckStockBalance
     */
    public CheckStockBalance() {
        initComponents();
        initCombo();
        txtStockDate.setText(DateUtil.getTodayDateStr());
        initTable();
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

        butPrint.setVisible(false);
    }

    private void calculate() {
        String deleteTmpData1 = "delete from tmp_costing_detail where user_id = '"
                + Global.machineId + "'";
        String deleteTmpData2 = "delete from tmp_stock_costing where user_id = '"
                + Global.machineId + "'";
        String strMethod = cboMethod.getSelectedItem().toString();

        try {
            dao.execSql(deleteTmpData1, deleteTmpData2);
            dao.commit();
            dao.close();
            insertStockFilterCode();

            dao.execProc("gen_cost_balance",
                    DateUtil.toDateStrMYSQL(txtStockDate.getText()), "Opening",
                    Global.machineId);

            /*String strLocation;
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strLocation = location.getLocationId().toString();
        } else {
            strLocation = "0";
        }*/
            dao.execProc("insert_cost_detail",
                    "Opening", DateUtil.toDateStrMYSQL(txtStockDate.getText()),
                    Global.machineId, strMethod);

            dao.commit();

            calculateLocation();
            getData();
        } catch (Exception ex) {
            log.error("calculate : " + ex.toString());
        } finally {
            dao.close();
        }

        //applyFilter();
    }

    private void insertStockFilterCode() {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_stock_filter select m.location_id, m.med_id, "
                + " ifnull(meod.op_date, '1900-01-01'),'" + Global.machineId
                + "' from v_med_loc m left join "
                + "(select location_id, med_id, max(op_date) op_date from med_op_date "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(txtStockDate.getText()) + "'";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where (m.active = true or m.active = false) "
                + "and m.calc_stock = true";

        try {
            dao.open();
            dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertStockFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void calculateLocation() {
        String strLocation = "0";
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strLocation = location.getLocationId().toString();
        }

        String userId = Global.machineId;
        try {
            dao.execSql("delete from tmp_stock_balance_exp where user_id = '"
                    + userId + "'");
            dao.execSql("update tmp_stock_costing set location_id = null, "
                    + "loc_ttl_small_qty = null where user_id = '" + userId + "'");
            dao.execSql("delete from tmp_stock_checking where user_id = '" + userId + "'");

            if (!strLocation.equals("0")) {
                dao.execProc("stock_balance_exp", "Opening",
                        DateUtil.toDateStrMYSQL(txtStockDate.getText()),
                        strLocation, userId);
                dao.execSql("update tmp_stock_costing tsc, (\n"
                        + "select med_id, sum(ifnull(bal_qty,0)) ttl_bal_qty\n"
                        + "from tmp_stock_balance_exp\n"
                        + "where user_id = '" + userId + "' and location_id = " + strLocation + " and tran_option = 'Opening'\n"
                        + "group by med_id) a\n"
                        + "set tsc.location_id = " + strLocation + ", tsc.loc_ttl_small_qty = a.ttl_bal_qty, \n"
                        + "tsc.loc_ttl_cost = if(tsc.bal_qty=0,0,(tsc.total_cost/tsc.bal_qty)*a.ttl_bal_qty)\n"
                        + "where tsc.med_id = a.med_id and tsc.user_id = '" + userId + "' and tran_option = 'Opening'");
                dao.execSql("insert into tmp_stock_checking (\n"
                        + "  med_id, user_id, bal_qty, bal_qty_str, total_cost, smallest_cost, location_id)\n"
                        + "select tsc.med_id, tsc.user_id, ifnull(tsc.loc_ttl_small_qty,0) as loc_ttl_small_qty, \n"
                        + "get_qty_in_str(ifnull(tsc.loc_ttl_small_qty,0), B.unit_smallest, B.unit_str) as bal_qty_str,\n"
                        + "round(ifnull(tsc.loc_ttl_cost,0),0) as total_cost, \n"
                        + "0+round(if(ifnull(tsc.bal_qty,0)=0,0,ifnull(tsc.total_cost,0)/tsc.bal_qty),0) as smallest_cost,\n"
                        + "tsc.location_id\n"
                        + "from tmp_stock_costing tsc, v_med_unit_smallest_rel B\n"
                        + "where tsc.med_id = b.med_id and tsc.user_id = '" + Global.machineId
                        + "' and tsc.location_id = " + strLocation + " and tsc.tran_option = 'Opening'");
            }
        } catch (Exception ex) {
            log.error("calculateLocation : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboLocation, dao.findAllHSQL("select o from Location o order by o.locationName"));
            new ComBoBoxAutoComplete(cboLocation);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getData() {
        String strSql = "select tsc.med_id, tsc.med_name, tsc.med_rel_str, tsc.user_id, tsc.bal_qty, tsc.bal_qty_str,\n"
                + "tsc.total_cost, tsc.smallest_cost, tsc.location_id, tsc.usr_bal_qty, tsc.usr_bal_qty_str,\n"
                + "tsc.usr_ttl_cost, tsc.diff_qty, tsc.diff_qty_str, tsc.diff_cost\n"
                + "from v_stock_checking tsc where tsc.user_id = '" + Global.machineId + "'";

        String filter = cboFilter.getSelectedItem().toString();
        if (!filter.equals("All")) {
            strSql = strSql + " and (ifnull(tsc.diff_qty,0)<>0 or ifnull(tsc.diff_cost,0)<>0)";
        }
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<CheckStockBalanceEntity> listCSBE = new ArrayList();
                double ttlSystem = 0;
                double ttlUser = 0;
                double ttlDiff = 0;
                while (rs.next()) {
                    CheckStockBalanceEntity csbe = new CheckStockBalanceEntity(
                            rs.getString("med_id"),
                            rs.getString("med_name"),
                            rs.getString("med_rel_str"),
                            rs.getFloat("bal_qty"),
                            rs.getString("bal_qty_str"),
                            rs.getDouble("total_cost"),
                            rs.getFloat("smallest_cost"),
                            rs.getFloat("usr_bal_qty"),
                            rs.getString("usr_bal_qty_str"),
                            rs.getDouble("usr_ttl_cost"),
                            rs.getFloat("diff_qty"),
                            rs.getString("diff_qty_str"),
                            rs.getDouble("diff_cost")
                    );
                    ttlSystem += NumberUtil.NZero(rs.getDouble("total_cost"));
                    ttlUser += NumberUtil.NZero(rs.getDouble("usr_ttl_cost"));
                    ttlDiff += NumberUtil.NZero(rs.getDouble("diff_cost"));
                    listCSBE.add(csbe);
                }

                tableModel.setList(listCSBE);
                txtTtlSysAmt.setValue(ttlSystem);
                txtTtlUsrAmt.setValue(ttlUser);
                txtTtlDiffAmt.setValue(ttlDiff);
            }
        } catch (Exception ex) {
            log.error("getData : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblStock.getTableHeader().setReorderingAllowed(false);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(300);//Name
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(60);//Relstr
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(50);//Sys-Balance
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(30);//Sys-Ttl-Amt
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(15);//Smallest Cost
        tblStock.getColumnModel().getColumn(6).setPreferredWidth(50);//Usr-Balance
        tblStock.getColumnModel().getColumn(7).setPreferredWidth(30);//Usr-Ttl-Amt
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(50);//Diff-Qty
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(30);//Diff-Amt

        tblStock.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblStock.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        tblStock.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
    }

    private void processCSV(File file) {
        if (file != null) {
            RegNo regNo = new RegNo(dao, "SCBNO");
            String batchNo = regNo.getRegNo();
            Integer locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
            txtBatchNo.setText(batchNo);
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);

                StockCheckBatch scb = new StockCheckBatch();
                scb.setBatchNo(txtBatchNo.getText());
                scb.setTranDate(new Date());
                scb.setUserId(Global.machineId);

                dao.open();
                dao.beginTran();
                dao.save1(scb);

                try (CSVReader csvReader = new CSVReader(reader)) {
                    String[] nextRecord;
                    int ttlRec = 0;

                    log.info("processCSV Start");
                    //List<StockCheckDetail> listDetail = new ArrayList();
                    while ((nextRecord = csvReader.readNext()) != null) {
                        String medId = nextRecord[0];
                        Float smallestQty = Float.parseFloat(nextRecord[1].replace(",", ""));
                        StockCheckDetail scd = new StockCheckDetail();
                        scd.setBatchNo(batchNo);
                        scd.setLocationId(locationId);
                        scd.setMedId(medId);
                        scd.setTtlQty(smallestQty);
                        log.info("medId : " + medId + " smallestQty : " + smallestQty);
                        dao.save1(scd);
                    }

                    dao.commit();
                    regNo.updateRegNo();
                    log.info("processCSV End: " + ttlRec);
                }
            } catch (Exception ex) {
                dao.rollBack();
                log.error("processCSV : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void updateBalance() {
        Integer locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        String batchNo = txtBatchNo.getText();
        String strSql = "update tmp_stock_checking tsc join stock_check_detail scd on tsc.med_id = scd.med_id and tsc.location_id = scd.location_id\n"
                + "join v_med_unit_smallest_rel b on tsc.med_id = b.med_id\n"
                + "set tsc.usr_bal_qty = ifnull(scd.ttl_qty,0), tsc.usr_bal_qty_str = get_qty_in_str(ifnull(scd.ttl_qty,0), b.unit_smallest, b.unit_str),\n"
                + "tsc.usr_ttl_cost = ifnull(scd.ttl_qty,0)*ifnull(tsc.smallest_cost,0),\n"
                + "tsc.diff_qty = ifnull(tsc.bal_qty,0)-ifnull(scd.ttl_qty,0),\n"
                + "tsc.diff_qty_str = get_qty_in_str(ifnull(tsc.bal_qty,0)-ifnull(scd.ttl_qty,0), b.unit_smallest, b.unit_str),\n"
                + "tsc.diff_cost = (ifnull(tsc.bal_qty,0)-ifnull(scd.ttl_qty,0))*ifnull(tsc.smallest_cost,0)\n"
                + "where tsc.user_id = '" + Global.machineId
                + "' and tsc.location_id = " + locationId.toString() + " and scd.batch_no = '" + batchNo + "'";
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("updateBalance : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        tableModel.setList(new ArrayList());
        txtBatchNo.setText("");
        txtStockDate.setText(DateUtil.getTodayDateStr());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtStockDate = new javax.swing.JFormattedTextField();
        cboMethod = new javax.swing.JComboBox<>();
        cboLocation = new javax.swing.JComboBox<>();
        butCalculate = new javax.swing.JButton();
        butUploadStockBalance = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        txtTtlDiffAmt = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTtlUsrAmt = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTtlSysAmt = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        butPrint = new javax.swing.JButton();
        cboFilter = new javax.swing.JComboBox<>();
        butClear = new javax.swing.JButton();

        txtStockDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock Date"));
        txtStockDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtStockDateMouseClicked(evt);
            }
        });

        cboMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AVG", "FIFO" }));
        cboMethod.setBorder(javax.swing.BorderFactory.createTitledBorder("Cost Method"));

        cboLocation.setBorder(javax.swing.BorderFactory.createTitledBorder("Location"));

        butCalculate.setText("Calculate");
        butCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCalculateActionPerformed(evt);
            }
        });

        butUploadStockBalance.setText("Upload Stock Balance");
        butUploadStockBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUploadStockBalanceActionPerformed(evt);
            }
        });

        tblStock.setFont(Global.textFont);
        tblStock.setModel(tableModel);
        tblStock.setRowHeight(23);
        jScrollPane1.setViewportView(tblStock);

        txtTtlDiffAmt.setEditable(false);
        txtTtlDiffAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText("Total Diff-Amt : ");

        txtTtlUsrAmt.setEditable(false);
        txtTtlUsrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText("Total User Amt : ");

        txtTtlSysAmt.setEditable(false);
        txtTtlSysAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setText("Total System Amt : ");

        txtBatchNo.setEditable(false);
        txtBatchNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Batch No"));

        butPrint.setText("Print");

        cboFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Different" }));
        cboFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        cboFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFilterActionPerformed(evt);
            }
        });

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtStockDate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCalculate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butUploadStockBalance)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlSysAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlUsrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlDiffAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTtlDiffAmt, txtTtlSysAmt, txtTtlUsrAmt});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStockDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butCalculate)
                    .addComponent(butUploadStockBalance)
                    .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butPrint)
                    .addComponent(cboFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlDiffAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtTtlUsrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtTtlSysAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtStockDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtStockDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtStockDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtStockDateMouseClicked

    private void butCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCalculateActionPerformed
        calculate();
    }//GEN-LAST:event_butCalculateActionPerformed

    private void butUploadStockBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUploadStockBalanceActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processCSV(selectedFile);
            updateBalance();
            getData();
        }
    }//GEN-LAST:event_butUploadStockBalanceActionPerformed

    private void cboFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFilterActionPerformed
        getData();
    }//GEN-LAST:event_cboFilterActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCalculate;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butUploadStockBalance;
    private javax.swing.JComboBox<String> cboFilter;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JComboBox<String> cboMethod;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JFormattedTextField txtStockDate;
    private javax.swing.JFormattedTextField txtTtlDiffAmt;
    private javax.swing.JFormattedTextField txtTtlSysAmt;
    private javax.swing.JFormattedTextField txtTtlUsrAmt;
    // End of variables declaration//GEN-END:variables
}
