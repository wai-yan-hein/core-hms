/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.pharmacy.ui.util.FillVouPaymentDialog;
import com.cv.app.util.Util1;
import java.awt.Dimension;

/**
 *
 * @author WSwe
 */
public class OtherSetup extends javax.swing.JPanel {

    /**
     * Creates new form OtherSetup
     */
    public OtherSetup() {
        initComponents();
    }

    private void showDialog(String panelName){
        SetupDialog dialog = new SetupDialog(Util1.getParent(), true, panelName);
        
        //Calculate dialog position to centre.
        /*Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - dialog.getWidth()) /2;
        int y = (screen.height - dialog.getHeight()) /2;
        
        dialog.setLocation(x, y);
        dialog.show();*/
        dialog.setLocationRelativeTo(null);
        dialog.setPreferredSize(new Dimension(2000, 1000));
        dialog.setVisible(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        butCurrencySetup = new javax.swing.JButton();
        butCusGroupSetup = new javax.swing.JButton();
        butLocationSetup = new javax.swing.JButton();
        butPaymentTypeSetup = new javax.swing.JButton();
        butSessionSetup = new javax.swing.JButton();
        butChargeType = new javax.swing.JButton();
        butExpenseType = new javax.swing.JButton();
        butVouStatus = new javax.swing.JButton();
        butAdjType = new javax.swing.JButton();
        butTraderOpening = new javax.swing.JButton();
        butFillVouPayment = new javax.swing.JButton();
        butSaleMen = new javax.swing.JButton();
        butLocationGroup = new javax.swing.JButton();
        butLocationType = new javax.swing.JButton();
        butTraderPayAccount = new javax.swing.JButton();
        butPayBy = new javax.swing.JButton();
        butExRate = new javax.swing.JButton();

        butCurrencySetup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butCurrencySetup.setText("Currency");
        butCurrencySetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCurrencySetupActionPerformed(evt);
            }
        });

        butCusGroupSetup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butCusGroupSetup.setText("Cus-Group");
        butCusGroupSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCusGroupSetupActionPerformed(evt);
            }
        });

        butLocationSetup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butLocationSetup.setText("Location");
        butLocationSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLocationSetupActionPerformed(evt);
            }
        });

        butPaymentTypeSetup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butPaymentTypeSetup.setText("Payment Type");
        butPaymentTypeSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPaymentTypeSetupActionPerformed(evt);
            }
        });

        butSessionSetup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butSessionSetup.setText("Session");
        butSessionSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSessionSetupActionPerformed(evt);
            }
        });

        butChargeType.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butChargeType.setText("Charge Type");
        butChargeType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butChargeTypeActionPerformed(evt);
            }
        });

        butExpenseType.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butExpenseType.setText("Expense Type");
        butExpenseType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExpenseTypeActionPerformed(evt);
            }
        });

        butVouStatus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butVouStatus.setText("Vou Status");
        butVouStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butVouStatusActionPerformed(evt);
            }
        });

        butAdjType.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butAdjType.setText("Adj Type Setup");
        butAdjType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAdjTypeActionPerformed(evt);
            }
        });

        butTraderOpening.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butTraderOpening.setText("Trader Opening");
        butTraderOpening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTraderOpeningActionPerformed(evt);
            }
        });

        butFillVouPayment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butFillVouPayment.setText("Fill Vou Payment");
        butFillVouPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFillVouPaymentActionPerformed(evt);
            }
        });

        butSaleMen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butSaleMen.setText("Sale Men");
        butSaleMen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaleMenActionPerformed(evt);
            }
        });

        butLocationGroup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        butLocationGroup.setText("Location Group");
        butLocationGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLocationGroupActionPerformed(evt);
            }
        });

        butLocationType.setText("Location Type");
        butLocationType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLocationTypeActionPerformed(evt);
            }
        });

        butTraderPayAccount.setText("Trader Pay Account");
        butTraderPayAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTraderPayAccountActionPerformed(evt);
            }
        });

        butPayBy.setText("Pay Method");
        butPayBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPayByActionPerformed(evt);
            }
        });

        butExRate.setText("Exchange Rate");
        butExRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExRateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(butCurrencySetup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butCusGroupSetup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butLocationSetup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butPaymentTypeSetup, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(butSessionSetup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butChargeType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butExpenseType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butVouStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(butTraderOpening, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butFillVouPayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butAdjType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butSaleMen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butLocationGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butLocationType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butTraderPayAccount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(butPayBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(butExRate)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butChargeType, butCurrencySetup, butCusGroupSetup, butExpenseType, butLocationSetup, butPaymentTypeSetup, butSessionSetup, butVouStatus});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butCurrencySetup)
                            .addComponent(butAdjType)
                            .addComponent(butExRate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butCusGroupSetup)
                            .addComponent(butTraderOpening))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butLocationSetup))
                    .addComponent(butFillVouPayment))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butPaymentTypeSetup)
                    .addComponent(butSaleMen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSessionSetup)
                    .addComponent(butLocationGroup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butChargeType)
                    .addComponent(butLocationType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butExpenseType)
                    .addComponent(butTraderPayAccount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butVouStatus)
                    .addComponent(butPayBy))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butCurrencySetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCurrencySetupActionPerformed
        showDialog("Currency Setup");
    }//GEN-LAST:event_butCurrencySetupActionPerformed

    private void butCusGroupSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCusGroupSetupActionPerformed
        showDialog("Customer Group Setup");
    }//GEN-LAST:event_butCusGroupSetupActionPerformed

    private void butLocationSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLocationSetupActionPerformed
        showDialog("Location Setup");
    }//GEN-LAST:event_butLocationSetupActionPerformed

    private void butPaymentTypeSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPaymentTypeSetupActionPerformed
        showDialog("Payment Type Setup");
    }//GEN-LAST:event_butPaymentTypeSetupActionPerformed

    private void butSessionSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSessionSetupActionPerformed
        showDialog("Session Setup");
    }//GEN-LAST:event_butSessionSetupActionPerformed

    private void butChargeTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butChargeTypeActionPerformed
        showDialog("Charge Type Setup");
    }//GEN-LAST:event_butChargeTypeActionPerformed

    private void butExpenseTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExpenseTypeActionPerformed
        showDialog("Expense Type Setup");
    }//GEN-LAST:event_butExpenseTypeActionPerformed

    private void butVouStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butVouStatusActionPerformed
        showDialog("Voucher Status Setup");
    }//GEN-LAST:event_butVouStatusActionPerformed

    private void butAdjTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAdjTypeActionPerformed
        showDialog("Adjustment Type Setup");
    }//GEN-LAST:event_butAdjTypeActionPerformed

    private void butTraderOpeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTraderOpeningActionPerformed
        showDialog("Trader Opening Setup");
    }//GEN-LAST:event_butTraderOpeningActionPerformed

    private void butFillVouPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFillVouPaymentActionPerformed
        FillVouPaymentDialog dialog = new FillVouPaymentDialog();
    }//GEN-LAST:event_butFillVouPaymentActionPerformed

    private void butSaleMenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaleMenActionPerformed
        SaleMenSetup dialog = new SaleMenSetup();
    }//GEN-LAST:event_butSaleMenActionPerformed

    private void butLocationGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLocationGroupActionPerformed
        LocationGrouMappingSetup dialog = new LocationGrouMappingSetup();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_butLocationGroupActionPerformed

    private void butLocationTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLocationTypeActionPerformed
        LocationTypeSetupDialog dialog = new LocationTypeSetupDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_butLocationTypeActionPerformed

    private void butTraderPayAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTraderPayAccountActionPerformed
        TraderPayAccountSetupDialog dialog = new TraderPayAccountSetupDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_butTraderPayAccountActionPerformed

    private void butPayByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPayByActionPerformed
        PayMethodSetupDialog dialog = new PayMethodSetupDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_butPayByActionPerformed

    private void butExRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExRateActionPerformed
        ExchangeRateDialog dialog = new ExchangeRateDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_butExRateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdjType;
    private javax.swing.JButton butChargeType;
    private javax.swing.JButton butCurrencySetup;
    private javax.swing.JButton butCusGroupSetup;
    private javax.swing.JButton butExRate;
    private javax.swing.JButton butExpenseType;
    private javax.swing.JButton butFillVouPayment;
    private javax.swing.JButton butLocationGroup;
    private javax.swing.JButton butLocationSetup;
    private javax.swing.JButton butLocationType;
    private javax.swing.JButton butPayBy;
    private javax.swing.JButton butPaymentTypeSetup;
    private javax.swing.JButton butSaleMen;
    private javax.swing.JButton butSessionSetup;
    private javax.swing.JButton butTraderOpening;
    private javax.swing.JButton butTraderPayAccount;
    private javax.swing.JButton butVouStatus;
    // End of variables declaration//GEN-END:variables
}
