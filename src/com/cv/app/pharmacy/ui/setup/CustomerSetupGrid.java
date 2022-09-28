/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.BusinessType;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.CustomerWithParent;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.entity.TraderType;
import com.cv.app.pharmacy.database.helper.VoucherPayment;
import com.cv.app.pharmacy.ui.common.BusinessTypeTableCellEditor;
import com.cv.app.pharmacy.ui.common.CustomerSetupGridTableModel;
import com.cv.app.pharmacy.ui.common.TownshipTableCellEditor;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author lenovo
 */
public class CustomerSetupGrid extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(CustomerSetupGrid.class.getName());
    private final CustomerSetupGridTableModel tblCusGridEntry = new CustomerSetupGridTableModel();
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;

    //private final TableRowSorter<TableModel> tblTraderSorter;
    /**
     * Creates new form CustomerPayment1
     */
    public CustomerSetupGrid() {
        initComponents();
        initTableCus();
        initCombo();
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblCustomer.getModel());
        tblCustomer.setRowSorter(sorter);
        chkActive.setSelected(true);
        search();
    }

    private void initTableCus() {
        tblCustomer.getTableHeader().setFont(Global.lableFont);
        tblCustomer.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(250);//Customer Name
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(5);//Active
        tblCustomer.getColumnModel().getColumn(3).setPreferredWidth(100);//Phone
        tblCustomer.getColumnModel().getColumn(4).setPreferredWidth(50);//Cus Group
        tblCustomer.getColumnModel().getColumn(5).setPreferredWidth(50);//Business Type
        tblCustomer.getColumnModel().getColumn(6).setPreferredWidth(30);//Credit Days
        tblCustomer.getColumnModel().getColumn(7).setPreferredWidth(50);//Credit Limit
        tblCustomer.getColumnModel().getColumn(8).setPreferredWidth(5);//Price Type
        tblCustomer.getColumnModel().getColumn(9).setPreferredWidth(200);//Township
        tblCustomer.getColumnModel().getColumn(10).setPreferredWidth(30);//Township

        tblCustomer.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        tblCustomer.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
        tblCustomer.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor());
        tblCustomer.getColumnModel().getColumn(7).setCellEditor(new BestTableCellEditor());
        tblCustomer.getColumnModel().getColumn(5).setCellEditor(new BusinessTypeTableCellEditor(dao));
        tblCustomer.getColumnModel().getColumn(9).setCellEditor(new TownshipTableCellEditor(dao));

        try {
            JComboBox cboCusPriceType = new JComboBox();
            BindingUtil.BindCombo(cboCusPriceType, dao.findAllHSQL(
                    "select o from TraderType o"));
            tblCustomer.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cboCusPriceType));
        } catch (Exception ex) {
            log.error("initTableCus : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initCombo() {
        bindStatus = true;
        try {
            BindingUtil.BindComboFilter(cboCusGroup, dao.findAllHSQL(
                    "select o from CustomerGroup o where o.useFor = 'CUS' order by o.groupName"));
            BindingUtil.BindComboFilter(cboPriceType, dao.findAllHSQL(
                    "select o from TraderType o"));
            BindingUtil.BindComboFilter(cboTownship, dao.findAllHSQL(
                    "select o from Township o order by o.townshipName"));
            BindingUtil.BindComboFilter(cboBusinessType, dao.findAllHSQL(
                    "select o from BusinessType o order by o.description"));
            String strBaseGroup = Util1.getPropValue("system.cus.base.group");
            if (strBaseGroup.isEmpty()) {
                strBaseGroup = "-";
            }
            BindingUtil.BindComboFilter(cboParent, dao.findAllHSQL(
                    "select o from Customer o where o.traderGroup.groupId = '"
                    + strBaseGroup + "' order by o.traderName"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
        bindStatus = false;
    }

    private void search() {
        String strSql = "";
        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            String cusGroup = ((CustomerGroup) cboCusGroup.getSelectedItem()).getGroupId();
            if (strSql.isEmpty()) {
                strSql = "o.traderGroup.groupId = '" + cusGroup + "'";
            } else {
                strSql = strSql + " and o.traderGroup.groupId = '" + cusGroup + "'";
            }
        }

        if (cboPriceType.getSelectedItem() instanceof TraderType) {
            Integer traderType = ((TraderType) cboPriceType.getSelectedItem()).getTypeId();
            if (strSql.isEmpty()) {
                strSql = "o.typeId.typeId = " + traderType;
            } else {
                strSql = strSql + " and o.typeId.typeId = " + traderType;
            }
        }

        if (cboTownship.getSelectedItem() instanceof Township) {
            Integer tspId = ((Township) cboTownship.getSelectedItem()).getTownshipId();
            if (strSql.isEmpty()) {
                strSql = "o.township.townshipId = " + tspId;
            } else {
                strSql = strSql + " and o.township.townshipId = " + tspId;
            }
        }

        if (cboBusinessType.getSelectedItem() instanceof BusinessType) {
            Integer id = ((BusinessType) cboBusinessType.getSelectedItem()).getBusinessId();
            if (strSql.isEmpty()) {
                strSql = "o.businessType.businessId = " + id;
            } else {
                strSql = strSql + " and o.businessType.businessId = " + id;
            }
        }

        if (cboParent.getSelectedItem() instanceof Customer) {
            String id = ((Customer) cboParent.getSelectedItem()).getTraderId();
            if (strSql.isEmpty()) {
                strSql = "o.parent.traderId = '" + id + "'";
            } else {
                strSql = strSql + " and o.parent.traderId = '" + id + "'";
            }
        }

        if (strSql.isEmpty()) {
            strSql = "o.active = " + chkActive.isSelected();
        } else {
            strSql = strSql + " and o.active = " + chkActive.isSelected();
        }

        if (strSql.isEmpty()) {
            strSql = "select o from CustomerWithParent o";
        } else {
            strSql = "select o from CustomerWithParent o where " + strSql;
        }

        try {
            List<CustomerWithParent> listCUS = dao.findAllHSQL(strSql);
            tblCusGridEntry.setListCUS(listCUS);
        } catch (Exception ex) {
            log.error("initTableCus : " + ex.getMessage());
        } finally {
            dao.close();
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

        jLabel1 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        cboCusGroup = new javax.swing.JComboBox<>();
        cboBusinessType = new javax.swing.JComboBox<>();
        cboPriceType = new javax.swing.JComboBox<>();
        cboTownship = new javax.swing.JComboBox<>();
        cboParent = new javax.swing.JComboBox<>();

        jLabel1.setText("Filter");

        txtFilter.setFont(Global.textFont);
        txtFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterActionPerformed(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        chkActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActiveActionPerformed(evt);
            }
        });

        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(tblCusGridEntry);
        tblCustomer.setRowHeight(23);
        jScrollPane1.setViewportView(tblCustomer);

        cboCusGroup.setFont(Global.textFont);
        cboCusGroup.setToolTipText("");
        cboCusGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCusGroupActionPerformed(evt);
            }
        });

        cboBusinessType.setFont(Global.textFont);
        cboBusinessType.setToolTipText("");
        cboBusinessType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBusinessTypeActionPerformed(evt);
            }
        });

        cboPriceType.setFont(Global.textFont);
        cboPriceType.setToolTipText("");
        cboPriceType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPriceTypeActionPerformed(evt);
            }
        });

        cboTownship.setFont(Global.textFont);
        cboTownship.setToolTipText("");
        cboTownship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTownshipActionPerformed(evt);
            }
        });

        cboParent.setFont(Global.textFont);
        cboParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboParentActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBusinessType, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPriceType, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboBusinessType, cboParent});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkActive)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboBusinessType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboPriceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorter.setRowFilter(swrf);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterActionPerformed

    private void chkActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActiveActionPerformed
        //sorter.setRowFilter(swrf);
        search();
    }//GEN-LAST:event_chkActiveActionPerformed

    private void cboCusGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCusGroupActionPerformed
        if (!bindStatus) {
            search();
        }
    }//GEN-LAST:event_cboCusGroupActionPerformed

    private void cboBusinessTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBusinessTypeActionPerformed
        if (!bindStatus) {
            search();
        }
    }//GEN-LAST:event_cboBusinessTypeActionPerformed

    private void cboPriceTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPriceTypeActionPerformed
        if (!bindStatus) {
            search();
        }
    }//GEN-LAST:event_cboPriceTypeActionPerformed

    private void cboTownshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTownshipActionPerformed
        if (!bindStatus) {
            search();
        }
    }//GEN-LAST:event_cboTownshipActionPerformed

    private void cboParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParentActionPerformed
        if (!bindStatus) {
            search();
        }
    }//GEN-LAST:event_cboParentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboBusinessType;
    private javax.swing.JComboBox<String> cboCusGroup;
    private javax.swing.JComboBox<String> cboParent;
    private javax.swing.JComboBox<String> cboPriceType;
    private javax.swing.JComboBox<String> cboTownship;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            switch (source.toString()) {
                case "CalculateTotal":

                    break;
                case "Paid":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("Paid");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
                case "Discount":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("Discount");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
                case "FullPaid":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("FullPaid");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
            }
        }
    }

    private void assignData() {
        try {
            TraderPayHis tph = new TraderPayHis();
            VoucherPayment vp = new VoucherPayment();
            tph.setPayDate(vp.getPayDate());
            List<Customer> cus = dao.findAllHSQL("select o from Customer o where o.traderId='" + vp.getTraderId() + "'");
            tph.setTrader(cus.get(0));
            tph.setRemark(vp.getRemark());
            tph.setPaidAmtC(vp.getCurrentPaid());
            tph.setDiscount(vp.getCurrentDiscount());
            String appCurr = Util1.getPropValue("system.app.currency");
            List<Currency> curr = dao.findAllHSQL("select o from Currency o where o.currencyCode='"
                    + appCurr + "'");
            tph.setCurrency(curr.get(0));
            tph.setExRate(1.0);
            tph.setPaidAmtP(vp.getCurrentPaid());
            List<Appuser> user = dao.findAllHSQL("select o from Appuser o where  o.userId='" + vp.getUserId() + "'");
            tph.setCreatedBy(user.get(0));
            tph.setPayOption("Cash");
            tph.setParentCurr(curr.get(0));
            tph.setPayDt(vp.getPayDate());
            PaymentVou pv = new PaymentVou();
            pv.setBalance(vp.getVouBalance());
            pv.setVouNo(vp.getVouNo());
            pv.setVouPaid(vp.getCurrentPaid());
            pv.setBalance(vp.getVouBalance());
            pv.setVouDate(vp.getTranDate());
            pv.setDiscount(vp.getDiscount());
            pv.setVouType("Sale");
            List<PaymentVou> listPV = new ArrayList();
            listPV.add(pv);
            tph.setListDetail(listPV);

            dao.save(tph);
        } catch (Exception ex) {
            log.error("assignData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

}
