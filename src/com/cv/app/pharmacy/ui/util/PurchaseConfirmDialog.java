/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.PurHis;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PurchaseConfirmDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(PurchaseConfirmDialog.class.getName());
    private PurHis currPurVou;
    private AbstractDataAccess dao;
    private boolean confStatus = false;
    private double supBalance;
    private boolean cashOut;

    private DocumentListener discPListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calcDisc("Percent", txtDiscP.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtDiscP.getText()) > 0) {
                txtDiscount.setEnabled(false);
            } else {
                txtDiscount.setEnabled(true);
            }
            calcDisc("Percent", txtDiscP.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtDiscP.getText()) > 0) {
                txtDiscount.setEnabled(false);
            } else {
                txtDiscount.setEnabled(true);
            }
            calcDisc("Percent", txtDiscP.getText());
        }
    };
    private DocumentListener discAmtListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calcDisc("Amt", txtDiscount.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calcDisc("Amt", txtDiscount.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calcDisc("Amt", txtDiscount.getText());
        }
    };
    private DocumentListener taxPListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calcTax("Percent", txtTaxP.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtTaxP.getText()) > 0) {
                txtTaxAmt.setEnabled(false);
            } else {
                txtTaxAmt.setEnabled(true);
            }
            calcTax("Percent", txtTaxP.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtTaxP.getText()) > 0) {
                txtTaxAmt.setEnabled(false);
            } else {
                txtTaxAmt.setEnabled(true);
            }
            calcTax("Percent", txtTaxP.getText());
        }
    };
    private DocumentListener taxAmtListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calcTax("Amt", txtTaxAmt.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calcTax("Amt", txtTaxAmt.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calcTax("Amt", txtTaxAmt.getText());
        }
    };

    private final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            System.out.println("keyTyped");
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("keyPressed");
        }

        @Override
        public void keyReleased(KeyEvent e) {
            String name = "-";
            if (e.getSource() instanceof JFormattedTextField) {
                name = ((JFormattedTextField) e.getSource()).getName();
                if (name == null) {
                    name = "-";
                }
            }

            switch (name) {
                case "txtDiscP":
                    if (NumberUtil.NZero(txtDiscP.getText()) > 0) {
                        txtDiscount.setEnabled(false);
                    } else {
                        txtDiscount.setEnabled(true);
                    }
                    calDiscPercent("Percent", txtDiscP.getText());
                    break;
                case "txtDiscountAmt":
                    calDiscPercent("Amt", txtDiscount.getText());
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtTaxAmt.requestFocus();
                    }
                    break;
                case "txtTaxP":
                    if (NumberUtil.NZero(txtTaxP.getText()) > 0) {
                        txtTaxAmt.setEnabled(false);
                    } else {
                        txtTaxAmt.setEnabled(true);
                    }
                    calTaxPercent("Percent", txtTaxP.getText());
                    break;
                case "txtTaxAmt":
                    calTaxPercent("Amt", txtTaxAmt.getText());
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtPaid.requestFocus();
                    }
                    break;
                case "txtPaidAmtC":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtPaid.requestFocus();
                    }
                    break;
            }
            reCalculate(name);
        }
    };
    
    private void reCalculate(String ctrlName) {
        if (!ctrlName.equals("txtPaidAmtC")) {
            PaymentType pt = currPurVou.getPaymentTypeId();
            if (pt != null) {
                if (pt.getPaymentTypeId() == 1) {
                    Double vouTotal = NumberUtil.NZero(txtVTotal.getText());
                    Double discount = NumberUtil.NZero(txtDiscount.getText());
                    Double taxAmt = NumberUtil.NZero(txtTaxAmt.getText());
                    Double cashPaid = vouTotal - discount + taxAmt;
                    txtPaid.setText(cashPaid.toString());
                }
            }
        }

        Double vTotal = NumberUtil.NZero(txtVTotal.getText())
                + NumberUtil.NZero(txtExpense.getText())
                + NumberUtil.NZero(txtTaxAmt.getText())
                - NumberUtil.NZero(txtDiscount.getText());
        Double balance = vTotal - NumberUtil.NZero(txtPaid.getText());

        if (balance < 0) {
            balance = -1 * balance;
            txtVouBalance.setText(balance.toString());
            //jLabel12.setText("Change : ");
        } else {
            txtVouBalance.setText(balance.toString());
            //jLabel12.setText("Vou Balance : ");
        }

        //System.out.println("Balance : " + balance + " Discount : " + NumberUtil.NZero(txtDiscountAmt.getValue()));
    }

    private void calDiscPercent(String evtFrom, String value) {
        if (evtFrom.equals("Percent")) {
            double percent = NumberUtil.NZero(value);
            Double vTotal = NumberUtil.NZero(txtVTotal.getText());
            Double discAmt = (vTotal * percent) / 100;
            txtDiscount.setText(discAmt.toString());
        } else if (evtFrom.equals("Amt")) {
            txtDiscP.setText("0.0");
        }
    }

    private void calTaxPercent(String evtFrom, String value) {
        if (evtFrom.equals("Percent")) {
            double percent = NumberUtil.NZero(value);
            Double vTotal = NumberUtil.NZero(txtVTotal.getText());
            Double taxAmt = (vTotal * percent) / 100;
            txtTaxAmt.setText(taxAmt.toString());
        } else if (evtFrom.equals("Amt")) {
            txtTaxP.setText("0.0");
        }
    }
    
    /**
     * Creates new form PurchaseConfirmDialog
     */
    public PurchaseConfirmDialog(PurHis currPurVou, AbstractDataAccess dao,
            double supBalance, boolean cashOut) {
        super(Util1.getParent(), true);
        this.currPurVou = currPurVou;
        this.dao = dao;
        this.supBalance = supBalance;
        this.cashOut = cashOut;
        initComponents();
        setTitle("Purchase Confirm");
        initCombo();
        initTextBoxAlign();
        initTextBox();

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        show();
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboPayment, dao.findAll("PaymentType"));
            new ComBoBoxAutoComplete(cboPayment);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTextBoxAlign() {
        txtDiscount.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtLastBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxAmt.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtExpense.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtLastBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxAmt.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtExpense.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void initTextBox() {
        txtDiscP.addKeyListener(keyListener);
        txtDiscount.addKeyListener(keyListener);
        txtTaxP.addKeyListener(keyListener);
        txtTaxAmt.addKeyListener(keyListener);
        txtPaid.addKeyListener(keyListener);
        
        txtDiscount.setValue(currPurVou.getDiscount());
        txtPaid.setValue(currPurVou.getPaid());
        txtVouBalance.setValue(currPurVou.getBalance());
        txtVTotal.setValue(currPurVou.getVouTotal());
        txtDiscP.setValue(currPurVou.getDiscP());
        txtTaxP.setValue(currPurVou.getTaxP());
        txtTaxAmt.setValue(currPurVou.getTaxAmt());
        txtExpense.setValue(currPurVou.getExpenseTotal());

        cboPayment.setSelectedItem(currPurVou.getPaymentTypeId());
    }

    private void calcDisc(String option, String value) {
        if (option.equals("Amt")) {
            txtDiscP.setValue(0.0);
        } else {
            double discPercent = NumberUtil.NZero(value);
            double vouTotal = NumberUtil.NZero(txtVTotal.getValue());

            txtDiscount.setValue((vouTotal * discPercent) / 100);
        }

        reCalculateAmt();
    }

    private void calcTax(String option, String value) {
        if (option.equals("Amt")) {
            txtTaxP.setValue(0.0);
        } else {
            double taxPercent = NumberUtil.NZero(value);
            double discount = NumberUtil.NZero(txtDiscount.getValue());
            double vouTotal = NumberUtil.NZero(txtVTotal.getValue());

            txtTaxAmt.setValue(((vouTotal - discount) * taxPercent) / 100);
        }

        reCalculateAmt();
    }

    private void reCalculateAmt() {
        double vouTotal = currPurVou.getVouTotal();
        double paid = NumberUtil.NZero(txtPaid.getValue());
        double discount = NumberUtil.NZero(txtDiscount.getValue());
        double tax = NumberUtil.NZero(txtTaxAmt.getValue());
        double expense = NumberUtil.NZero(txtExpense.getValue());

        if (cashOut) {
            expense = 0;
        }

        if (((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId().equals(1)) {
            paid = (vouTotal + expense + tax) - discount;
            txtPaid.setValue(paid);
            txtPaid.setEditable(false);
        } else {
            txtPaid.setEditable(true);
        }

        txtVouBalance.setValue((vouTotal + tax + expense) - (paid + discount));
        txtLastBalance.setValue(supBalance + ((vouTotal + tax + expense) - (paid + discount)));
    }

    public boolean getConfStatus() {
        return confStatus;
    }

    private void assignToVou() {
        currPurVou.setPaymentTypeId((PaymentType) cboPayment.getSelectedItem());
        currPurVou.setVouTotal(NumberUtil.getDouble(txtVTotal.getText()));
        currPurVou.setPaid(NumberUtil.getDouble(txtPaid.getText()));
        currPurVou.setDiscount(NumberUtil.getDouble(txtDiscount.getText()));
        currPurVou.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
        currPurVou.setDiscP(NumberUtil.getDouble(txtDiscP.getText()));
        currPurVou.setTaxP(NumberUtil.getDouble(txtTaxP.getText()));
        currPurVou.setTaxAmt(NumberUtil.getDouble(txtTaxAmt.getText()));
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
        cboPayment = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txtVTotal = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPaid = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtLastBalance = new javax.swing.JFormattedTextField();
        butConfirm = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtTaxAmt = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtExpense = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Payment ");

        cboPayment.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboPayment.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  }));
        cboPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total ");

        txtVTotal.setEditable(false);
        txtVTotal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Paid ");

        txtPaid.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaidActionPerformed(evt);
            }
        });
        txtPaid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPaidFocusLost(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Discount");

        txtDiscount.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDiscount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscountFocusLost(evt);
            }
        });
        txtDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscountActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Vou Bal");

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Last Bal");

        txtLastBalance.setEditable(false);
        txtLastBalance.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        butConfirm.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butConfirm.setText("Confirm");
        butConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConfirmActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Tax");

        txtDiscP.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDiscP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscPFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscPFocusLost(evt);
            }
        });
        txtDiscP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscPActionPerformed(evt);
            }
        });

        jLabel8.setText("%");

        jLabel9.setText("%");

        txtTaxAmt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTaxAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxAmtFocusLost(evt);
            }
        });
        txtTaxAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxAmtActionPerformed(evt);
            }
        });

        txtTaxP.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTaxP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxPFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxPFocusLost(evt);
            }
        });
        txtTaxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxPActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Expense");

        txtExpense.setEditable(false);
        txtExpense.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(butConfirm)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(24, 24, 24))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(22, 22, 22)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouBalance)
                                    .addComponent(txtPaid)
                                    .addComponent(txtLastBalance)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jLabel3)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVTotal)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtTaxP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .addComponent(txtDiscP, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDiscount, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTaxAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))))
                            .addComponent(cboPayment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtExpense))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtVTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTaxAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLastBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(butConfirm)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscPActionPerformed

    }//GEN-LAST:event_txtDiscPActionPerformed

    private void txtDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountActionPerformed

    }//GEN-LAST:event_txtDiscountActionPerformed

    private void txtTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxPActionPerformed

    }//GEN-LAST:event_txtTaxPActionPerformed

    private void txtTaxAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxAmtActionPerformed

    }//GEN-LAST:event_txtTaxAmtActionPerformed

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        if (cboPayment.getSelectedItem() != null) {
            reCalculateAmt();
        }
    }//GEN-LAST:event_cboPaymentActionPerformed

    private void butConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConfirmActionPerformed
        reCalculateAmt();
        assignToVou();
        confStatus = true;
        dispose();
    }//GEN-LAST:event_butConfirmActionPerformed

    private void txtPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaidActionPerformed
        reCalculateAmt();
    }//GEN-LAST:event_txtPaidActionPerformed

    private void txtDiscPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusLost
        txtDiscP.getDocument().removeDocumentListener(discPListener);
    }//GEN-LAST:event_txtDiscPFocusLost

    private void txtTaxPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusLost
        txtTaxP.getDocument().removeDocumentListener(taxPListener);
    }//GEN-LAST:event_txtTaxPFocusLost

    private void txtDiscountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountFocusLost
        txtDiscount.getDocument().removeDocumentListener(discAmtListener);
    }//GEN-LAST:event_txtDiscountFocusLost

    private void txtTaxAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxAmtFocusLost
        txtTaxAmt.getDocument().removeDocumentListener(taxAmtListener);
    }//GEN-LAST:event_txtTaxAmtFocusLost

    private void txtPaidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaidFocusLost
        reCalculateAmt();
    }//GEN-LAST:event_txtPaidFocusLost

    private void txtDiscPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusGained
        txtDiscP.getDocument().addDocumentListener(discPListener);
    }//GEN-LAST:event_txtDiscPFocusGained

    private void txtDiscountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountFocusGained
        txtDiscount.getDocument().addDocumentListener(discAmtListener);
    }//GEN-LAST:event_txtDiscountFocusGained

    private void txtTaxPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusGained
        txtTaxP.getDocument().addDocumentListener(taxPListener);
    }//GEN-LAST:event_txtTaxPFocusGained

    private void txtTaxAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxAmtFocusGained
        txtTaxAmt.getDocument().addDocumentListener(taxAmtListener);
    }//GEN-LAST:event_txtTaxAmtFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butConfirm;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JFormattedTextField txtDiscount;
    private javax.swing.JFormattedTextField txtExpense;
    private javax.swing.JFormattedTextField txtLastBalance;
    private javax.swing.JFormattedTextField txtPaid;
    private javax.swing.JFormattedTextField txtTaxAmt;
    private javax.swing.JFormattedTextField txtTaxP;
    private javax.swing.JFormattedTextField txtVTotal;
    private javax.swing.JFormattedTextField txtVouBalance;
    // End of variables declaration//GEN-END:variables
}
