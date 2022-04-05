/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.util;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.DCHis;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author WSwe
 */
public class DCConfirDialog extends javax.swing.JDialog {
    private DCHis currOPDHis;
    private boolean status = false;
    
    /**
     * Creates new form OPDConfirDialog
     */
    public DCConfirDialog(DCHis currOPDHis) {
        super(Util1.getParent(), true);
        initComponents();
        
        this.currOPDHis = currOPDHis;
        initTextBoxAlign();
        initControl();
        setVisible(true);
        
        formActionKeyMapping(txtPtNo);
        formActionKeyMapping(txtPtName);
        formActionKeyMapping(txtVouTotal);
        formActionKeyMapping(txtDiscountP);
        formActionKeyMapping(txtDiscountA);
        formActionKeyMapping(txtTaxA);
        formActionKeyMapping(txtTaxP);
        formActionKeyMapping(txtPaid);
        formActionKeyMapping(txtBalance);
    }

    private void initControl(){
        if(currOPDHis.getPatient() != null){
          txtPtNo.setText(currOPDHis.getPatient().getRegNo());
          txtPtName.setText(currOPDHis.getPatient().getPatientName());
        }else{
          txtPtName.setText(currOPDHis.getPatientName());
        }
        txtVouTotal.setValue(currOPDHis.getVouTotal());
        txtDiscountP.setValue(currOPDHis.getDiscountP());
        txtDiscountA.setValue(currOPDHis.getDiscountA());
        txtTaxP.setValue(currOPDHis.getTaxP());
        txtTaxA.setValue(currOPDHis.getTaxA());
        txtPaid.setValue(currOPDHis.getPaid());
        txtBalance.setValue(currOPDHis.getVouBalance());
        
        if(currOPDHis.getPaymentType().getPaymentTypeId().equals(1)){
            txtPaid.setEditable(false);
        }else{
            txtPaid.setEditable(true);
        }
    }
    
    private void initTextBoxAlign() {
        txtVouTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscountP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscountA.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxA.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscountP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscountA.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxA.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
    }
    
    private void calcDiscount(String opt){
        switch(opt){
            case "Percent":
                double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());
                double percent = NumberUtil.NZero(txtDiscountP.getValue());
                txtDiscountA.setValue((vouTotal * percent)/100);
                break;
            case "Amt":
                txtDiscountP.setValue(0.0);
                break;
        }
        
        calcBalance();
    }
    
    private void calcTax(String opt){
        switch(opt){
            case "Percent":
                double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());
                double percent = NumberUtil.NZero(txtTaxP.getValue());
                txtTaxA.setValue((vouTotal * percent)/100);
                break;
            case "Amt":
                txtTaxP.setValue(0.0);
                break;
        }
        
        calcBalance();
    }
    
    private void calcBalance(){
        double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());
        double disc = NumberUtil.NZero(txtDiscountA.getValue());
        double tax = NumberUtil.NZero(txtTaxA.getValue());
        double paid;
        
        if(currOPDHis.getPaymentType().getPaymentTypeId().equals(1)){
            txtPaid.setValue(vouTotal - (disc + tax));
        }
        paid = NumberUtil.NZero(txtPaid.getValue());
        
        txtBalance.setValue(vouTotal - (disc + tax + paid));
    }

    public boolean isStatus() {
        return status;
    }
    
    private void saveValue(){
        currOPDHis.setDiscountP(NumberUtil.NZero(txtDiscountP.getValue()));
        currOPDHis.setDiscountA(NumberUtil.NZero(txtDiscountA.getValue()));
        currOPDHis.setTaxP(NumberUtil.NZero(txtTaxP.getValue()));
        currOPDHis.setTaxA(NumberUtil.NZero(txtTaxA.getValue()));
        currOPDHis.setPaid(NumberUtil.NZero(txtPaid.getValue()));
    }
    
    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Print
        jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
        jc.getActionMap().put("F7-Action", actionSave);
    }
    
    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            action();
        }
    };

    private void action() {
        saveValue();
        status = true;
        dispose();
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
        txtPtNo = new javax.swing.JTextField();
        txtPtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        txtDiscountP = new javax.swing.JFormattedTextField();
        txtDiscountA = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();
        txtTaxA = new javax.swing.JFormattedTextField();
        txtPaid = new javax.swing.JFormattedTextField();
        txtBalance = new javax.swing.JFormattedTextField();
        butSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DC Confirm");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Patient ");

        txtPtNo.setEditable(false);
        txtPtNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtPtName.setEditable(false);
        txtPtName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Vou Total ");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Discount ");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Tax");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Paid ");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Balance ");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtDiscountP.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDiscountP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscountPActionPerformed(evt);
            }
        });

        txtDiscountA.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDiscountA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscountAActionPerformed(evt);
            }
        });

        txtTaxP.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtTaxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxPActionPerformed(evt);
            }
        });

        txtTaxA.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtTaxA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxAActionPerformed(evt);
            }
        });

        txtPaid.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaidActionPerformed(evt);
            }
        });

        txtBalance.setEditable(false);
        txtBalance.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPtName, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTaxP, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                                    .addComponent(txtDiscountP))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDiscountA)
                                    .addComponent(txtTaxA)))
                            .addComponent(txtVouTotal)
                            .addComponent(txtPaid)
                            .addComponent(txtBalance)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDiscountP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiscountA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(butSave)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtDiscountPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountPActionPerformed
        calcDiscount("Percent");
    }//GEN-LAST:event_txtDiscountPActionPerformed

    private void txtDiscountAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountAActionPerformed
        calcDiscount("Amt");
    }//GEN-LAST:event_txtDiscountAActionPerformed

    private void txtTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxPActionPerformed
        calcTax("Percent");
    }//GEN-LAST:event_txtTaxPActionPerformed

    private void txtTaxAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxAActionPerformed
        calcTax("Amt");
    }//GEN-LAST:event_txtTaxAActionPerformed

    private void txtPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaidActionPerformed
        calcBalance();
    }//GEN-LAST:event_txtPaidActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        saveValue();
        status = true;
        dispose();
    }//GEN-LAST:event_butSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JFormattedTextField txtBalance;
    private javax.swing.JFormattedTextField txtDiscountA;
    private javax.swing.JFormattedTextField txtDiscountP;
    private javax.swing.JFormattedTextField txtPaid;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtPtNo;
    private javax.swing.JFormattedTextField txtTaxA;
    private javax.swing.JFormattedTextField txtTaxP;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables
}
