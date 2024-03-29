/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.util;

import com.cv.app.common.Global;
import com.cv.app.opd.database.tempentity.TempAmountLink;
import com.cv.app.opd.ui.common.AmountLinkTableModel;
import com.cv.app.util.Util1;
import java.util.List;

/**
 *
 * @author MyintMo
 */
public class AmountLinkDialog extends javax.swing.JDialog {

    private final AmountLinkTableModel tblAmountLinkTableModel = new AmountLinkTableModel();

    public AmountLinkDialog(List<TempAmountLink> listTAL) {
        super(Util1.getParent(), true);
        initComponents();
        initTable();
        setLocationRelativeTo(null);
        tblAmountLinkTableModel.setListTAL(listTAL);
    }

    /**
     * Creates new form AmountLinkDialog
     */
    public AmountLinkDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public double getTtlAmt() {
        if (tblAmountLinkTableModel != null) {
            return tblAmountLinkTableModel.getTotalAmount();
        } else {
            return 0.0;
        }
    }

    public double getTtlDisount() {
        return tblAmountLinkTableModel.getTotalDiscount();
    }

    private void initTable() {
        tblAmountLink.getColumnModel().getColumn(0).setPreferredWidth(40);//Option
        tblAmountLink.getColumnModel().getColumn(1).setPreferredWidth(100);//Vou No
        tblAmountLink.getColumnModel().getColumn(2).setPreferredWidth(60);//Amount
        tblAmountLink.getColumnModel().getColumn(3).setPreferredWidth(20);//Status
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblAmountLink = new javax.swing.JTable();
        butOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Amount Link");

        tblAmountLink.setFont(Global.textFont);
        tblAmountLink.setModel(tblAmountLinkTableModel);
        tblAmountLink.setRowHeight(23);
        jScrollPane1.setViewportView(tblAmountLink);

        butOK.setText("OK");
        butOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butOK)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butOK)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOKActionPerformed
        dispose();
    }//GEN-LAST:event_butOKActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AmountLinkDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AmountLinkDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AmountLinkDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AmountLinkDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AmountLinkDialog dialog = new AmountLinkDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butOK;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblAmountLink;
    // End of variables declaration//GEN-END:variables
}
