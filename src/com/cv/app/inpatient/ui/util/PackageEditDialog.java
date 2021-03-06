/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.util;

import com.cv.app.common.Global;
import com.cv.app.inpatient.ui.common.PackageDetailEditTableModel;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.POIUtil;
import com.cv.app.util.Util1;
import javax.swing.JFormattedTextField;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PackageEditDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(PackageEditDialog.class.getName());
    private final PackageDetailEditTableModel tableModel = new PackageDetailEditTableModel();
    private final String vouNo;
    private final String regNo;
    private final String admissionNo;
    //private final String pkgOption;
    private final TableRowSorter<TableModel> sorter;
    
    /**
     * Creates new form PackageEditDialog
     * @param vouNo
     * @param regNo
     * @param admissionNo
     * @param pkgId
     * @param ptName
     * @param pkgName
     * @param pkgAmount
     * @param pkgOption
     */
    public PackageEditDialog(String vouNo, String regNo, String admissionNo,
            Long pkgId, String ptName, String pkgName, Double pkgAmount,
            String pkgOption) {
        super(Util1.getParent(), true);
        initComponents();
        this.vouNo = vouNo;
        this.regNo = regNo;
        this.admissionNo = admissionNo;
        //this.pkgOption = pkgOption;
        //this.pkgId = pkgId;
        txtPackagePrice.setValue(pkgAmount);
        txtVouNo.setText(vouNo);
        txtRegNo.setText(regNo);
        txtName.setText(ptName);
        txtPackage.setText(pkgName);
        
        initTable();
        initTextBoxAlign();
        sorter = new TableRowSorter(tblPackageDetail.getModel());
        tblPackageDetail.setRowSorter(sorter);
        tableModel.setPackageAmount(pkgAmount);
        tableModel.setPkgId(pkgId);
        tableModel.initData(vouNo, regNo, admissionNo, "ALL");
        tableModel.setPkgOption(pkgOption);
    }

    private void initTable(){
        tblPackageDetail.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblPackageDetail.getColumnModel().getColumn(0).setPreferredWidth(40);//Item Option
        tblPackageDetail.getColumnModel().getColumn(1).setPreferredWidth(40);//Item Code
        tblPackageDetail.getColumnModel().getColumn(2).setPreferredWidth(250);//Item Name
        tblPackageDetail.getColumnModel().getColumn(3).setPreferredWidth(15);//Ttl Use Qty
        tblPackageDetail.getColumnModel().getColumn(4).setPreferredWidth(40);//Amount
        tblPackageDetail.getColumnModel().getColumn(5).setPreferredWidth(15);//Ttl Allow Qty
        tblPackageDetail.getColumnModel().getColumn(6).setPreferredWidth(15);//Over Use Qty
        tblPackageDetail.getColumnModel().getColumn(7).setPreferredWidth(20);//Status
        //tblPackageDetail.getColumnModel().getColumn(8).setPreferredWidth(5);//Include
        //tblPackageDetail.getColumnModel().getColumn(9).setPreferredWidth(5);//Exclude
        tblPackageDetail.getColumnModel().getColumn(5).setCellEditor(
                new BestTableCellEditor());
        
        tableModel.setControl(txtTotalBill, txtTotalOverUsage, txtTotalExtraUsage, 
                txtTotalPKUse, txtGainLost);
    }
    
    private void initTextBoxAlign() {
        txtPackagePrice.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTotalBill.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTotalOverUsage.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTotalExtraUsage.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTotalPKUse.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtGainLost.setHorizontalAlignment(JFormattedTextField.RIGHT);
        
        txtPackagePrice.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTotalBill.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTotalOverUsage.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTotalExtraUsage.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTotalPKUse.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtGainLost.setFormatterFactory(NumberUtil.getDecimalFormat());
    }
    
    public boolean getStatus(){
        return tableModel.getStatus();
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
        txtVouNo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPackage = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPackageDetail = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        cboItemOption = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtPackagePrice = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTotalBill = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTotalOverUsage = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTotalExtraUsage = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtTotalPKUse = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtGainLost = new javax.swing.JFormattedTextField();
        butGenExcel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Package Edit");
        setPreferredSize(new java.awt.Dimension(1300, 700));

        jLabel1.setText("Vou No : ");

        txtVouNo.setEditable(false);

        jLabel2.setText("Reg No. :");

        txtRegNo.setEditable(false);

        jLabel3.setText("Name : ");

        txtName.setEditable(false);
        txtName.setFont(Global.textFont);

        jLabel4.setText("Package : ");

        txtPackage.setEditable(false);
        txtPackage.setFont(Global.textFont);

        tblPackageDetail.setFont(Global.textFont);
        tblPackageDetail.setModel(tableModel);
        tblPackageDetail.setRowHeight(23);
        jScrollPane1.setViewportView(tblPackageDetail);

        jLabel5.setText("Item Option : ");

        cboItemOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "PHARMACY", "OPD", "OT", "DC" }));
        cboItemOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboItemOptionActionPerformed(evt);
            }
        });

        jLabel6.setText("Package Price : ");

        txtPackagePrice.setEditable(false);

        jLabel7.setText("Total Bill : ");

        txtTotalBill.setEditable(false);

        jLabel8.setText("Total Over Usage : ");

        txtTotalOverUsage.setEditable(false);

        jLabel9.setText("Total Extra Usage : ");

        txtTotalExtraUsage.setEditable(false);

        jLabel10.setText("Total PK Use : ");

        txtTotalPKUse.setEditable(false);

        jLabel11.setText("Gain/Lost : ");

        txtGainLost.setEditable(false);

        butGenExcel.setText("Gen-Excel");
        butGenExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGenExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPackage, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboItemOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butGenExcel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPackagePrice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalBill)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalOverUsage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalExtraUsage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalPKUse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGainLost)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtPackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cboItemOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butGenExcel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPackagePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtTotalBill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtTotalOverUsage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtTotalExtraUsage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtTotalPKUse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtGainLost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboItemOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboItemOptionActionPerformed
        tableModel.initData(vouNo, regNo, admissionNo, cboItemOption.getSelectedItem().toString());
    }//GEN-LAST:event_cboItemOptionActionPerformed

    private void butGenExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGenExcelActionPerformed
        try{
            //String vouNo = txtVouNo.getText();
            //String regNo = txtRegNo.getText();
            String packageName = txtPackage.getText();
            String ptName = txtName.getText();
            Double packagePrice = Double.parseDouble(txtPackagePrice.getValue().toString());
            String fileName = regNo + "-" + ptName + ".xls";
            POIUtil.genExcelPatientPackage(vouNo, regNo, ptName, packageName, 
                    packagePrice, fileName, tableModel.getList());
        }catch(Exception ex){
            log.error("butGenExcelActionPerformed : " + ex.toString());
        }
    }//GEN-LAST:event_butGenExcelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butGenExcel;
    private javax.swing.JComboBox<String> cboItemOption;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPackageDetail;
    private javax.swing.JFormattedTextField txtGainLost;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPackage;
    private javax.swing.JFormattedTextField txtPackagePrice;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JFormattedTextField txtTotalBill;
    private javax.swing.JFormattedTextField txtTotalExtraUsage;
    private javax.swing.JFormattedTextField txtTotalOverUsage;
    private javax.swing.JFormattedTextField txtTotalPKUse;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
