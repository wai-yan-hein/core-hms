/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.helper.OPDDrPayment;
import com.cv.app.opd.ui.common.UnPaidPayableTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class UnPaidPayable extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(UnPaidPayable.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private int mouseClick = 2;
    private boolean bindStatus = false;
    private final UnPaidPayableTableModel tblModel = new UnPaidPayableTableModel();

    /**
     * Creates new form UnPaidPayable
     */
    public UnPaidPayable() {
        initComponents();
        txtDate.setText(DateUtil.getTodayDateStr());
        tblModel.setTranDate(txtDate.getText());
        initCombo();
        initTable();
        getData();

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
    }

    private void initCombo() {
        bindStatus = true;
        try {
            BindingUtil.BindComboFilter(cboExpType,
                    dao.findAllHSQL("select o from ExpenseType o order by o.expenseName"));
            List listDr = dao.findAllHSQL("select o from Doctor o where o.active = true order by o.doctorName");
            listDr.add(0, "All");
            listDr.add(1, "-");
            BindingUtil.BindCombo(cboDoctor, listDr);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
        bindStatus = false;
    }

    private void initTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblExpense.setCellSelectionEnabled(true);
        }

        tblExpense.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblExpense.getColumnModel().getColumn(0).setPreferredWidth(30);//Tran Type
        tblExpense.getColumnModel().getColumn(1).setPreferredWidth(40);//Tran Date
        tblExpense.getColumnModel().getColumn(2).setPreferredWidth(200);//Payment Name
        tblExpense.getColumnModel().getColumn(3).setPreferredWidth(200);//Doctor
        tblExpense.getColumnModel().getColumn(4).setPreferredWidth(50);//Ttl Amount
        tblExpense.getColumnModel().getColumn(5).setPreferredWidth(50);//Ttl Pay
        tblExpense.getColumnModel().getColumn(6).setPreferredWidth(50);//Ttl Balance
        tblExpense.getColumnModel().getColumn(7).setPreferredWidth(5);//Un Paid

        tblExpense.getColumnModel().getColumn(1).setCellRenderer(new TableDateFieldRenderer());
    }

    private void getData() {
        clear();
        String strSql = "select vcp.tran_option, vcp.tran_date, vcp.expense_name, vcp.doctor_id, dr.doctor_name,\n"
                + "vcp.expense_type_id, \n"
                + "sum(vcp.ttl_amt) as ttl_amt, sum(vcp.ttl_pay) as ttl_pay, sum(vcp.balance) as balance\n"
                + "from v_clinic_payable_bal_by_date vcp\n"
                + "left join doctor dr on vcp.doctor_id = dr.doctor_id\n"
                + "where vcp.balance <> 0 and vcp.tran_date <= '" + DateUtil.toDateStrMYSQL(txtDate.getText()) + "' \n";

        String expOption = cboOption.getSelectedItem().toString();
        if (!expOption.equals("All")) {
            strSql = strSql + " and vcp.tran_option = '" + expOption + "'";
        }

        if (cboDoctor.getSelectedItem() instanceof Doctor) {
            Doctor dr = (Doctor) cboDoctor.getSelectedItem();
            strSql = strSql + " and vcp.doctor_id = '" + dr.getDoctorId() + "'";
        } else {
            String selDr = cboDoctor.getSelectedItem().toString();
            if (selDr.equals("-")) {
                strSql = strSql + " and vcp.doctor_id = '" + selDr + "'";
            }
        }

        if (cboExpType.getSelectedItem() instanceof ExpenseType) {
            ExpenseType et = (ExpenseType) cboExpType.getSelectedItem();
            strSql = strSql + " and vcp.expense_type_id = " + et.getExpenseId();
        }

        strSql = strSql + " group by vcp.tran_option, vcp.tran_date, "
                + "vcp.expense_name, vcp.doctor_id, dr.doctor_name, vcp.expense_type_id";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<OPDDrPayment> listRecord = new ArrayList();
                double ttlAmt = 0;
                double ttlPay = 0;
                double ttlBal = 0;
                while (rs.next()) {
                    OPDDrPayment record = new OPDDrPayment();
                    record.setAdmissionNo(rs.getString("tran_option"));
                    record.setTranDate(rs.getDate("tran_date"));
                    record.setServiceName(rs.getString("expense_name"));
                    record.setQty(rs.getInt("expense_type_id"));
                    String drId = rs.getString("doctor_id");
                    record.setRegNo(drId);
                    String drName = rs.getString("doctor_name");
                    if (drId.equals("-")) {
                        drName = "-";
                    } else {
                        drName = drId + "-" + drName;
                    }
                    record.setPtName(drName);
                    record.setAmount(rs.getDouble("ttl_amt"));
                    record.setMoFee(rs.getDouble("ttl_pay"));
                    record.setPrice(rs.getDouble("balance"));

                    ttlAmt += NumberUtil.NZero(rs.getDouble("ttl_amt"));
                    ttlPay += NumberUtil.NZero(rs.getDouble("ttl_pay"));
                    ttlBal += NumberUtil.NZero(rs.getDouble("balance"));

                    listRecord.add(record);
                }

                tblModel.setList(listRecord);
                txtTtlRec.setValue(listRecord.size());
                txtTtlAmt.setValue(ttlAmt);
                txtTtlPay.setValue(ttlPay);
                txtTtlBal.setValue(ttlBal);
            }
        } catch (SQLException ex) {
            log.error("getData : " + ex.toString());
        } catch (Exception ex) {
            log.error("getData : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        txtTtlRec.setValue(0);
        txtTtlAmt.setValue(0);
        txtTtlPay.setValue(0);
        txtTtlBal.setValue(0);
        tblModel.clear();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        cboOption = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cboDoctor = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cboExpType = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        butRefresh = new javax.swing.JButton();
        txtTtlBal = new javax.swing.JFormattedTextField();
        txtTtlPay = new javax.swing.JFormattedTextField();
        txtTtlAmt = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtTtlRec = new javax.swing.JFormattedTextField();

        jLabel1.setText("Date : ");

        txtDate.setEditable(false);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });

        jLabel2.setText("Option : ");

        cboOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "OPD", "OT", "DC" }));
        cboOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOptionActionPerformed(evt);
            }
        });

        jLabel3.setText("Doctor : ");

        cboDoctor.setFont(Global.textFont);
        cboDoctor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDoctorActionPerformed(evt);
            }
        });

        jLabel4.setText("Expense Type : ");

        cboExpType.setFont(Global.textFont);
        cboExpType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboExpTypeActionPerformed(evt);
            }
        });

        tblExpense.setModel(tblModel);
        tblExpense.setRowHeight(23);
        jScrollPane1.setViewportView(tblExpense);

        butRefresh.setText("Refresh");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        txtTtlBal.setEditable(false);
        txtTtlBal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlPay.setEditable(false);
        txtTtlPay.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlAmt.setEditable(false);
        txtTtlAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText("Total : ");

        jLabel6.setText("Total Records : ");

        txtTtlRec.setEditable(false);
        txtTtlRec.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboOption, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboDoctor, 0, 47, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboExpType, 0, 46, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butRefresh))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlRec, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlBal, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTtlAmt, txtTtlBal, txtTtlPay});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cboOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cboExpType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlBal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(txtTtlRec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        getData();
    }//GEN-LAST:event_butRefreshActionPerformed

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDate.setText(strDate);
                tblModel.setTranDate(strDate);
                getData();
            }
        }
    }//GEN-LAST:event_txtDateMouseClicked

    private void cboOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOptionActionPerformed
        if (!bindStatus) {
            getData();
        }
    }//GEN-LAST:event_cboOptionActionPerformed

    private void cboDoctorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDoctorActionPerformed
        if (!bindStatus) {
            getData();
        }
    }//GEN-LAST:event_cboDoctorActionPerformed

    private void cboExpTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboExpTypeActionPerformed
        if (!bindStatus) {
            getData();
        }
    }//GEN-LAST:event_cboExpTypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butRefresh;
    private javax.swing.JComboBox<String> cboDoctor;
    private javax.swing.JComboBox<String> cboExpType;
    private javax.swing.JComboBox<String> cboOption;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblExpense;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JFormattedTextField txtTtlAmt;
    private javax.swing.JFormattedTextField txtTtlBal;
    private javax.swing.JFormattedTextField txtTtlPay;
    private javax.swing.JFormattedTextField txtTtlRec;
    // End of variables declaration//GEN-END:variables
}
