/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author WSwe
 */
public class SaleConfirmDialog1 extends javax.swing.JDialog {

    private SaleHis currSaleVou;
    private boolean confStatus = false;
    private AbstractDataAccess dao;
    private double cusLastBalance;

    private DocumentListener discPListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calDiscPercent("Percent", txtDiscP.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtDiscP.getText()) > 0) {
                txtDiscountAmt.setEnabled(false);
            } else {
                txtDiscountAmt.setEnabled(true);
            }
            calDiscPercent("Percent", txtDiscP.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtDiscP.getText()) > 0) {
                txtDiscountAmt.setEnabled(false);
            } else {
                txtDiscountAmt.setEnabled(true);
            }
            calDiscPercent("Percent", txtDiscP.getText());
        }
    };
    private DocumentListener discAmtListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calDiscPercent("Amt", txtDiscountAmt.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calDiscPercent("Amt", txtDiscountAmt.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calDiscPercent("Amt", txtDiscountAmt.getText());
        }
    };
    private DocumentListener taxPListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calTaxPercent("Percent", txtTaxP.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtTaxP.getText()) > 0) {
                txtTaxAmt.setEnabled(false);
            } else {
                txtTaxAmt.setEnabled(true);
            }
            calTaxPercent("Percent", txtTaxP.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (NumberUtil.NZero(txtTaxP.getText()) > 0) {
                txtTaxAmt.setEnabled(false);
            } else {
                txtTaxAmt.setEnabled(true);
            }
            calTaxPercent("Percent", txtTaxP.getText());
        }
    };
    private DocumentListener taxAmtListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calTaxPercent("Amt", txtTaxAmt.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calTaxPercent("Amt", txtTaxAmt.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calTaxPercent("Amt", txtTaxAmt.getText());
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
                        txtDiscountAmt.setEnabled(false);
                    } else {
                        txtDiscountAmt.setEnabled(true);
                    }
                    calDiscPercent("Percent", txtDiscP.getText());
                    break;
                case "txtDiscountAmt":
                    calDiscPercent("Amt", txtDiscountAmt.getText());
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
                        txtPaidAmtC.requestFocus();
                    }
                    break;
                case "txtPaidAmtC":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtDiscountAmt.requestFocus();
                    }
                    break;
            }
            reCalculate(name);
        }
    };

    /**
     * Creates new form SaleConfirmDialog1
     */
    public SaleConfirmDialog1(SaleHis currSaleVou,
            double totalBalance, AbstractDataAccess dao, String option) {
        super(Util1.getParent(), true);
        initComponents();

        formActionKeyMapping(txtCusCode);
        formActionKeyMapping(txtCusName);
        formActionKeyMapping(txtDiscP);
        formActionKeyMapping(txtDiscountAmt);
        formActionKeyMapping(txtExpense);
        formActionKeyMapping(txtGrandTotal);
        formActionKeyMapping(txtGrossTotal);
        formActionKeyMapping(txtPaidAmtC);
        formActionKeyMapping(txtTaxAmt);
        formActionKeyMapping(txtTaxP);
        formActionKeyMapping(txtTtlBfTax);
        formActionKeyMapping(txtVouBalance);
        formActionKeyMapping(butConfirm);

        butConfirm.setText(option);
        this.currSaleVou = currSaleVou;
        this.dao = dao;
        this.setTitle("Sale Confirm.");
        cusLastBalance = totalBalance;
        initTextBoxAlign();
        initTextBox();
        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        show();
        
        /*Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Focus");
                txtDiscountAmt.requestFocus();
            }
        });
        timer.setRepeats(false);
        timer.start();*/
    }

    private void initTextBox() {
        txtDiscP.addKeyListener(keyListener);
        txtDiscountAmt.addKeyListener(keyListener);
        txtTaxP.addKeyListener(keyListener);
        txtTaxAmt.addKeyListener(keyListener);
        txtPaidAmtC.addKeyListener(keyListener);

        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (currSaleVou.getPatientId() != null) {
                txtCusCode.setText(currSaleVou.getPatientId().getRegNo());
                txtCusName.setText(currSaleVou.getPatientId().getPatientName());
            }
        } else {
            if (currSaleVou.getPatientId() != null) {
                txtCusCode.setText(currSaleVou.getCustomerId().getTraderId());
                txtCusName.setText(currSaleVou.getCustomerId().getTraderName());
            }
        }
        txtGrossTotal.setText(currSaleVou.getVouTotal().toString());
        txtDiscP.setText(currSaleVou.getDiscP().toString());
        txtDiscountAmt.setText(currSaleVou.getDiscount().toString());
        Double ttlBfTax = currSaleVou.getVouTotal() - currSaleVou.getDiscount();
        txtTtlBfTax.setText(ttlBfTax.toString());
        txtTaxP.setText(currSaleVou.getTaxP().toString());
        if (NumberUtil.NZero(txtTaxP.getText()) > 0) {
            txtTaxAmt.setEnabled(false);
        } else {
            txtTaxAmt.setEnabled(true);
        }
        txtTaxAmt.setText(currSaleVou.getTaxAmt().toString());
        txtExpense.setText(currSaleVou.getExpenseTotal().toString());
        Double gTtl = NumberUtil.NZero(txtTtlBfTax.getText())
                + NumberUtil.NZero(currSaleVou.getExpenseTotal())
                + NumberUtil.NZero(currSaleVou.getTaxAmt());
        Double payAmt = NumberUtil.NZero(currSaleVou.getPayAmt());
        Double paid = NumberUtil.NZero(currSaleVou.getPaid());
        txtGrandTotal.setText(gTtl.toString());
        if (payAmt > gTtl) {
            jLabel12.setText("Change :");
            txtVouBalance.setText(currSaleVou.getRefund().toString());
            txtPaidAmtC.setText(payAmt.toString());
        } else {
            jLabel12.setText("Vou Balance :");
            Double vouBalance = NumberUtil.NZero(txtGrandTotal.getText()) - paid;
            txtVouBalance.setText(vouBalance.toString());
            txtPaidAmtC.setText(paid.toString());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="initTextBoxAlign">
    private void initTextBoxAlign() {
        txtGrossTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscountAmt.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTtlBfTax.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxAmt.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtGrandTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtExpense.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaidAmtC.setHorizontalAlignment(JFormattedTextField.RIGHT);

        /*txtGrossTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtDiscountAmt.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtTtlBfTax.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtTaxAmt.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtGrandTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtExpense.setFormatterFactory(NumberUtil.getDecimalFormat());
         txtPaidAmtC.setFormatterFactory(NumberUtil.getDecimalFormat());*/
    }//</editor-fold>

    public boolean getConfStatus() {
        return confStatus;
    }

    private void assignToVou() {
        currSaleVou.setDiscP(NumberUtil.NZero(txtDiscP.getText()));
        currSaleVou.setDiscount(NumberUtil.NZero(txtDiscountAmt.getText()));
        currSaleVou.setTaxP(NumberUtil.NZero(txtTaxP.getText()));
        currSaleVou.setTaxAmt(NumberUtil.NZero(txtTaxAmt.getText()));
        double balance = NumberUtil.NZero(txtVouBalance.getText());
        double paid = NumberUtil.NZero(txtPaidAmtC.getText());
        double grandTtl = NumberUtil.NZero(txtGrandTotal.getText());
        
        if (paid >= grandTtl) {
            currSaleVou.setBalance(0.0);
            currSaleVou.setPaid(grandTtl);
            currSaleVou.setPaidCurrencyAmt(grandTtl);
        } else {
            currSaleVou.setBalance(balance);
            currSaleVou.setPaidCurrencyAmt(paid);
            currSaleVou.setPaid(paid);
        }
        
        currSaleVou.setPayAmt(paid);
        currSaleVou.setRefund(balance);
    }

    private void reCalculate(String ctrlName) {
        if (!ctrlName.equals("txtPaidAmtC")) {
            PaymentType pt = currSaleVou.getPaymentTypeId();
            if (pt != null) {
                if (pt.getPaymentTypeId() == 1) {
                    Double vouTotal = NumberUtil.NZero(txtGrossTotal.getText());
                    Double discount = NumberUtil.NZero(txtDiscountAmt.getText());
                    Double taxAmt = NumberUtil.NZero(txtTaxAmt.getText());
                    Double cashPaid = vouTotal - discount + taxAmt;
                    txtPaidAmtC.setText(cashPaid.toString());
                }
            }
        }

        Double ttlBfTax = NumberUtil.NZero(txtGrossTotal.getText())
                - NumberUtil.NZero(txtDiscountAmt.getText());
        txtTtlBfTax.setText(ttlBfTax.toString());
        Double grandTotal = NumberUtil.NZero(txtTtlBfTax.getText())
                + NumberUtil.NZero(txtExpense.getText())
                + NumberUtil.NZero(txtTaxAmt.getText());
        txtGrandTotal.setText(grandTotal.toString());
        Double balance = NumberUtil.NZero(txtGrandTotal.getText())
                - NumberUtil.NZero(txtPaidAmtC.getText());

        if (balance < 0) {
            balance = -1 * balance;
            txtVouBalance.setText(balance.toString());
            jLabel12.setText("Change : ");
        } else {
            txtVouBalance.setText(balance.toString());
            jLabel12.setText("Vou Balance : ");
        }

        //System.out.println("Balance : " + balance + " Discount : " + NumberUtil.NZero(txtDiscountAmt.getValue()));
    }

    private void calDiscPercent(String evtFrom, String value) {
        if (evtFrom.equals("Percent")) {
            double percent = NumberUtil.NZero(value);
            double ttlBfTax = NumberUtil.NZero(txtGrossTotal.getText());
            Double discAmt = (ttlBfTax * percent) / 100;
            txtDiscountAmt.setText(discAmt.toString());
        } else if (evtFrom.equals("Amt")) {
            txtDiscP.setText("0.0");
        }
    }

    private void calTaxPercent(String evtFrom, String value) {
        if (evtFrom.equals("Percent")) {
            double percent = NumberUtil.NZero(value);
            double ttlBfTax = NumberUtil.NZero(txtTtlBfTax.getText());
            Double taxAmt = (ttlBfTax * percent) / 100;
            txtTaxAmt.setText(taxAmt.toString());
        } else if (evtFrom.equals("Amt")) {
            txtTaxP.setText("0.0");
        }
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
        assignToVou();
        confStatus = true;
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtGrossTotal = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDiscountAmt = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTtlBfTax = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTaxP = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtExpense = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtPaidAmtC = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTaxAmt = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        butConfirm = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Gross Total : ");

        txtGrossTotal.setEditable(false);
        txtGrossTotal.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Discount(-) : ");

        txtDiscP.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDiscP.setName("txtDiscP"); // NOI18N
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
        txtDiscP.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtDiscPVetoableChange(evt);
            }
        });

        jLabel3.setText("%");

        txtDiscountAmt.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDiscountAmt.setName("txtDiscountAmt"); // NOI18N
        txtDiscountAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscountAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscountAmtFocusLost(evt);
            }
        });
        txtDiscountAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscountAmtActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Tot Before Tax : ");

        txtTtlBfTax.setEditable(false);
        txtTtlBfTax.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Tax (+) :");

        txtTaxP.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtTaxP.setName("txtTaxP"); // NOI18N
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

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Expense : ");

        txtExpense.setEditable(false);
        txtExpense.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Grand Total : ");

        txtGrandTotal.setEditable(false);
        txtGrandTotal.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Paid Amt : ");

        txtPaidAmtC.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtPaidAmtC.setName("txtPaidAmtC"); // NOI18N
        txtPaidAmtC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPaidAmtCFocusGained(evt);
            }
        });
        txtPaidAmtC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaidAmtCActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Vou Balance : ");

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel13.setText("%");

        txtTaxAmt.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtTaxAmt.setName("txtTaxAmt"); // NOI18N
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

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Code/Name : ");

        txtCusCode.setEditable(false);
        txtCusCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtCusName.setEditable(false);
        txtCusName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butConfirm.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butConfirm.setText("Confirm");
        butConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConfirmActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtExpense)
                            .addComponent(txtGrandTotal)))
                    .addComponent(butConfirm, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(194, 194, 194)
                        .addComponent(txtCusName, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGrossTotal))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiscountAmt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTaxAmt))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlBfTax))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouBalance)
                            .addComponent(txtPaidAmtC))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDiscP, txtTaxP});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel12, jLabel14, jLabel2, jLabel4, jLabel5, jLabel6, jLabel7});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtGrossTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiscountAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTtlBfTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(txtTaxAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtPaidAmtC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butConfirm))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConfirmActionPerformed
        action();
    }//GEN-LAST:event_butConfirmActionPerformed

    private void txtTaxAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxAmtActionPerformed
        //calTaxPercent("Amt");
    }//GEN-LAST:event_txtTaxAmtActionPerformed

    private void txtTaxAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxAmtFocusLost
        //txtTaxAmt.getDocument().removeDocumentListener(taxAmtListener);
    }//GEN-LAST:event_txtTaxAmtFocusLost

    private void txtPaidAmtCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaidAmtCActionPerformed

    }//GEN-LAST:event_txtPaidAmtCActionPerformed

    private void txtTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxPActionPerformed
        //calTaxPercent("Percent");
    }//GEN-LAST:event_txtTaxPActionPerformed

    private void txtTaxPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusLost
        //txtTaxP.getDocument().removeDocumentListener(taxPListener);
    }//GEN-LAST:event_txtTaxPFocusLost

    private void txtDiscountAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountAmtActionPerformed
        //calDiscPercent("Amt");

    }//GEN-LAST:event_txtDiscountAmtActionPerformed

    private void txtDiscountAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountAmtFocusLost
        //txtDiscountAmt.getDocument().removeDocumentListener(discAmtListener);

    }//GEN-LAST:event_txtDiscountAmtFocusLost

    private void txtDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscPActionPerformed

    }//GEN-LAST:event_txtDiscPActionPerformed

    private void txtDiscPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusLost
        //txtDiscP.getDocument().removeDocumentListener(discPListener);
    }//GEN-LAST:event_txtDiscPFocusLost

    private void txtDiscPVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtDiscPVetoableChange
        //int a = 5;
    }//GEN-LAST:event_txtDiscPVetoableChange

    private void txtDiscPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusGained
        //txtDiscP.getDocument().addDocumentListener(discPListener);
        txtDiscP.selectAll();
    }//GEN-LAST:event_txtDiscPFocusGained

    private void txtDiscountAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountAmtFocusGained
        //txtDiscountAmt.getDocument().addDocumentListener(discAmtListener);
        txtDiscountAmt.selectAll();
    }//GEN-LAST:event_txtDiscountAmtFocusGained

    private void txtTaxPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusGained
        //txtTaxP.getDocument().addDocumentListener(taxPListener);
        txtTaxP.selectAll();
    }//GEN-LAST:event_txtTaxPFocusGained

    private void txtTaxAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxAmtFocusGained
        //txtTaxAmt.getDocument().addDocumentListener(taxAmtListener);
        txtTaxAmt.selectAll();
    }//GEN-LAST:event_txtTaxAmtFocusGained

    private void txtPaidAmtCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaidAmtCFocusGained
        txtPaidAmtC.selectAll();
    }//GEN-LAST:event_txtPaidAmtCFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butConfirm;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JFormattedTextField txtDiscountAmt;
    private javax.swing.JFormattedTextField txtExpense;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JFormattedTextField txtGrossTotal;
    private javax.swing.JFormattedTextField txtPaidAmtC;
    private javax.swing.JFormattedTextField txtTaxAmt;
    private javax.swing.JFormattedTextField txtTaxP;
    private javax.swing.JFormattedTextField txtTtlBfTax;
    private javax.swing.JFormattedTextField txtVouBalance;
    // End of variables declaration//GEN-END:variables
}
