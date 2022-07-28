/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import javax.swing.JFormattedTextField;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleConfirmDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(SaleConfirmDialog.class.getName());
    private SaleHis currSaleVou;
    private boolean confStatus = false;
    private AbstractDataAccess dao;
    private double cusLastBalance;

    /**
     * Creates new form SaleConfirmDialog1
     */
    public SaleConfirmDialog(SaleHis currSaleVou,
            double totalBalance, AbstractDataAccess dao, String option) {
        super(Util1.getParent(), true);
        initComponents();
        butConfirm.setText(option);
        this.currSaleVou = currSaleVou;
        this.dao = dao;
        this.setTitle("Sale Confirm.");
        cusLastBalance = totalBalance;
        initCombo();
        initTextBoxAlign();
        initTextBox();
        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        show();
    }

    private void initTextBox() {
        txtCusCode.setText(currSaleVou.getCustomerId().getTraderId());
        txtCusName.setText(currSaleVou.getCustomerId().getTraderName());
        txtRemark.setText(currSaleVou.getRemark());
        txtGrossTotal.setValue(currSaleVou.getVouTotal());
        txtDiscP.setValue(currSaleVou.getDiscP());
        txtDiscountAmt.setValue(currSaleVou.getDiscount());
        txtTtlBfTax.setValue(currSaleVou.getVouTotal()
                - currSaleVou.getDiscount());
        txtTaxP.setValue(currSaleVou.getTaxP());
        txtTaxAmt.setValue(currSaleVou.getTaxAmt());
        txtExpense.setValue(currSaleVou.getExpenseTotal());
        txtGrandTotal.setValue(NumberUtil.NZero(txtTtlBfTax.getValue())
                + currSaleVou.getExpenseTotal()
                + currSaleVou.getTaxAmt());
        txtPaid.setValue(currSaleVou.getPaid());
        txtVouBalance.setValue(NumberUtil.NZero(txtGrandTotal.getValue())
                - currSaleVou.getPaid());

        if (currSaleVou.getCustomerId() instanceof Customer) {
            txtCreditLimit.setValue(((Customer) currSaleVou.getCustomerId()).getCreditLimit());
        }

        txtExRate.setValue(currSaleVou.getPaidCurrencyExRate());
        txtPaidAmtC.setValue(currSaleVou.getPaidCurrencyAmt());
        txtLastBalance.setValue(cusLastBalance
                + NumberUtil.NZero(txtVouBalance.getValue()));
        cboPayment.setSelectedItem(currSaleVou.getPaymentTypeId());
        cboPaidCurrency.setSelectedItem(currSaleVou.getPaidCurrency());
        initPaidCurrency();
        txtDifference.setValue(NumberUtil.NZero(txtCreditLimit.getValue())
                - NumberUtil.NZero(txtLastBalance.getValue()));

        /*if(currSaleVou.getPaymentTypeId().getPaymentTypeId() == 1){
         txtPaid.setEditable(false);
         }*/
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
        txtCreditLimit.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDifference.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtLastBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtExpense.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtExRate.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaidAmtC.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtGrossTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscountAmt.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTtlBfTax.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxAmt.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtGrandTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtCreditLimit.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDifference.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtLastBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtExpense.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtExRate.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaidAmtC.setFormatterFactory(NumberUtil.getDecimalFormat());
    }//</editor-fold>

    public boolean getConfStatus() {
        return confStatus;
    }

    private void assignToVou() {
        currSaleVou.setRemark(txtRemark.getText());
        currSaleVou.setDiscP(NumberUtil.NZero(txtDiscP.getValue()));
        currSaleVou.setDiscount(NumberUtil.NZero(txtDiscountAmt.getValue()));
        currSaleVou.setTaxP(NumberUtil.NZero(txtTaxP.getValue()));
        currSaleVou.setTaxAmt(NumberUtil.NZero(txtTaxAmt.getValue()));
        currSaleVou.setPaid(NumberUtil.NZero(txtPaid.getValue()));
        currSaleVou.setBalance(NumberUtil.NZero(txtVouBalance.getValue()));
        currSaleVou.setPaidCurrency((Currency) cboPaidCurrency.getSelectedItem());
        currSaleVou.setPaidCurrencyAmt(NumberUtil.NZero(txtPaidAmtC.getValue()));
        currSaleVou.setPaidCurrencyExRate(NumberUtil.NZero(txtExRate.getValue()));
        currSaleVou.setPaymentTypeId((PaymentType) cboPayment.getSelectedItem());
    }

    private void reCalculate() {
        txtTtlBfTax.setValue(NumberUtil.NZero(txtGrossTotal.getValue())
                - NumberUtil.NZero(txtDiscountAmt.getValue()));
        txtGrandTotal.setValue(NumberUtil.NZero(txtTtlBfTax.getValue())
                + NumberUtil.NZero(txtExpense.getValue())
                + NumberUtil.NZero(txtTaxAmt.getValue()));
        calPaidCAmount();
        /*if (!txtPaid.isEditable()) {
         txtPaid.setValue(txtGrandTotal.getValue());
         }*/
        txtVouBalance.setValue(NumberUtil.NZero(txtGrandTotal.getValue())
                - NumberUtil.NZero(txtPaid.getValue()));
        txtLastBalance.setValue(cusLastBalance + NumberUtil.NZero(txtVouBalance.getValue()));
    }

    private void calDiscPercent(String evtFrom) {
        if (evtFrom.equals("Percent")) {
            double percent = NumberUtil.NZero(txtDiscP.getValue());
            double ttlBfTax = NumberUtil.NZero(txtGrossTotal.getValue());

            txtDiscountAmt.setValue((ttlBfTax * percent) / 100);

        } else if (evtFrom.equals("Amt")) {
            txtDiscP.setValue(0.0);
        }

        reCalculate();
    }

    private void calTaxPercent(String evtFrom) {
        if (evtFrom.equals("Percent")) {
            double percent = NumberUtil.NZero(txtTaxP.getText());
            double ttlBfTax = NumberUtil.NZero(txtTtlBfTax.getValue());

            txtTaxAmt.setValue((ttlBfTax * percent) / 100);

        } else if (evtFrom.equals("Amt")) {
            txtTaxP.setValue(0.0);
        }

        reCalculate();
    }

    private void calPaidCAmount() {
        /*if (((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId().equals(1)) {
            txtPaidAmtC.setValue(NumberUtil.NZero(txtGrandTotal.getValue())
                    / NumberUtil.NZero(txtExRate.getValue()));
        }*/

        double tmpVal = NumberUtil.NZero(txtPaidAmtC.getValue())
                * NumberUtil.NZero(txtExRate.getValue());
        txtPaid.setValue(tmpVal);
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindCombo(cboPaidCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboPayment);
            new ComBoBoxAutoComplete(cboPaidCurrency);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    private void initPaidCurrency() {
        Currency cur = null;

        if (cboPaidCurrency.getSelectedItem() != null) {
            cur = (Currency) cboPaidCurrency.getSelectedItem();
        }

        if (cur != null) {
            if (currSaleVou.getCurrencyId().getCurrencyCode().equals(cur.getCurrencyCode())) {
                txtExRate.setValue(1.0);
                txtPaidAmtC.setValue(txtPaid.getValue());

                txtExRate.setEditable(false);
                /*if (((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId().equals(1)) {
                    txtPaidAmtC.setEditable(false);
                } else {
                    txtPaidAmtC.setEditable(true);
                }*/
            } else {
                txtExRate.setEditable(true);
                /*if (((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId().equals(1)) {
                    txtPaidAmtC.setEditable(false);
                } else {
                    txtPaidAmtC.setEditable(true);
                }*/
            }
        } else {
            cboPaidCurrency.setSelectedItem(currSaleVou.getCurrencyId());
            txtExRate.setValue(1.0);
            txtPaidAmtC.setValue(txtPaid.getValue());
            txtExRate.setEditable(false);

            /*if (((PaymentType) cboPayment.getSelectedItem()).getPaymentTypeId().equals(1)) {
                txtPaidAmtC.setEditable(false);
            } else {
                txtPaidAmtC.setEditable(true);
            }*/
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtGrossTotal = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDiscountAmt = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        txtTtlBfTax = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTaxP = new javax.swing.JFormattedTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        txtExpense = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        cboPaidCurrency = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        txtExRate = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtPaidAmtC = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPaid = new javax.swing.JFormattedTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTaxAmt = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel16 = new javax.swing.JLabel();
        txtCreditLimit = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtLastBalance = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtDifference = new javax.swing.JFormattedTextField();
        butConfirm = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel19 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCardList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Gross Total : ");

        txtGrossTotal.setEditable(false);
        txtGrossTotal.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Discount(-) : ");

        txtDiscP.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDiscP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscPFocusLost(evt);
            }
        });
        txtDiscP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscPActionPerformed(evt);
            }
        });

        jLabel3.setText("%");

        txtDiscountAmt.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDiscountAmt.addFocusListener(new java.awt.event.FocusAdapter() {
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
        txtTaxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxPActionPerformed(evt);
            }
        });
        txtTaxP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxPFocusLost(evt);
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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Paid Currency : ");

        cboPaidCurrency.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboPaidCurrency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboPaidCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaidCurrencyActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Ex-Rate :");

        txtExRate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtExRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtExRateActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Paid Amt : ");

        txtPaidAmtC.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtPaidAmtC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaidAmtCActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Total Paid : ");

        txtPaid.setEditable(false);
        txtPaid.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtPaid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPaidFocusLost(evt);
            }
        });
        txtPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaidActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Vou Balance : ");

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel13.setText("%");

        txtTaxAmt.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtTaxAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxAmtActionPerformed(evt);
            }
        });
        txtTaxAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxAmtFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(32, 32, 32)
                        .addComponent(txtVouBalance))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator3)
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel4))
                                    .addGap(18, 18, 18))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(56, 56, 56)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11))
                                .addGap(23, 23, 23)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtGrossTotal)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDiscountAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                            .addComponent(txtTtlBfTax)
                            .addComponent(txtExpense)
                            .addComponent(txtGrandTotal)
                            .addComponent(cboPaidCurrency, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtExRate)
                            .addComponent(txtPaidAmtC)
                            .addComponent(txtPaid)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTaxAmt)))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDiscP, txtTaxP});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtGrossTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtDiscountAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTtlBfTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(txtTaxAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboPaidCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtExRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtPaidAmtC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Code/Name : ");

        txtCusCode.setEditable(false);
        txtCusCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtCusName.setEditable(false);
        txtCusName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Remark : ");

        txtRemark.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("CR-Limit : ");

        txtCreditLimit.setEditable(false);
        txtCreditLimit.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Lastl Bal : ");

        txtLastBalance.setEditable(false);
        txtLastBalance.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Difference : ");

        txtDifference.setEditable(false);
        txtDifference.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butConfirm.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butConfirm.setText("Confirm");
        butConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConfirmActionPerformed(evt);
            }
        });

        jLabel19.setText("Payment : ");

        cboPayment.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboPayment.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentActionPerformed(evt);
            }
        });

        tblCardList.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblCardList.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCardList.setRowHeight(23);
        tblCardList.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblCardList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator5)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17)
                            .addComponent(jLabel16))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemark)
                            .addComponent(txtCreditLimit)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCusName))
                            .addComponent(txtLastBalance)
                            .addComponent(txtDifference)))
                    .addComponent(jSeparator6)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(35, 35, 35)
                        .addComponent(cboPayment, 0, 257, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtCreditLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtLastBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtDifference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(butConfirm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscPActionPerformed
        calDiscPercent("Percent");
    }//GEN-LAST:event_txtDiscPActionPerformed

    private void butConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConfirmActionPerformed
        assignToVou();
        confStatus = true;
        dispose();
    }//GEN-LAST:event_butConfirmActionPerformed

    private void txtDiscPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusLost
        calDiscPercent("Percent");
    }//GEN-LAST:event_txtDiscPFocusLost

    private void txtDiscountAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountAmtActionPerformed
        calDiscPercent("Amt");
    }//GEN-LAST:event_txtDiscountAmtActionPerformed

    private void txtDiscountAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountAmtFocusLost
        calDiscPercent("Amt");
    }//GEN-LAST:event_txtDiscountAmtFocusLost

    private void txtTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxPActionPerformed
        calTaxPercent("Percent");
    }//GEN-LAST:event_txtTaxPActionPerformed

    private void txtTaxPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusLost
        if (NumberUtil.NZero(txtTaxP.getText()) > 0) {
            txtTaxAmt.setEnabled(false);
        } else {
            txtTaxAmt.setEnabled(true);
        }

        calTaxPercent("Percent");
    }//GEN-LAST:event_txtTaxPFocusLost

    private void txtTaxAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxAmtActionPerformed
        calTaxPercent("Amt");
    }//GEN-LAST:event_txtTaxAmtActionPerformed

    private void txtTaxAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxAmtFocusLost

        calTaxPercent("Amt");
    }//GEN-LAST:event_txtTaxAmtFocusLost

    private void txtPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaidActionPerformed
        reCalculate();
    }//GEN-LAST:event_txtPaidActionPerformed

    private void txtPaidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaidFocusLost
        reCalculate();
    }//GEN-LAST:event_txtPaidFocusLost

    private void txtExRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtExRateActionPerformed
        calPaidCAmount();
        reCalculate();
    }//GEN-LAST:event_txtExRateActionPerformed

    private void cboPaidCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaidCurrencyActionPerformed
        if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
            initPaidCurrency();
        }
    }//GEN-LAST:event_cboPaidCurrencyActionPerformed

    private void txtPaidAmtCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaidAmtCActionPerformed
        calPaidCAmount();
        reCalculate();
    }//GEN-LAST:event_txtPaidAmtCActionPerformed

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        if (cboPaidCurrency.getSelectedItem() instanceof Currency) {
            initPaidCurrency();
        }
    }//GEN-LAST:event_cboPaymentActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butConfirm;
    private javax.swing.JComboBox cboPaidCurrency;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTable tblCardList;
    private javax.swing.JFormattedTextField txtCreditLimit;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtDifference;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JFormattedTextField txtDiscountAmt;
    private javax.swing.JFormattedTextField txtExRate;
    private javax.swing.JFormattedTextField txtExpense;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JFormattedTextField txtGrossTotal;
    private javax.swing.JFormattedTextField txtLastBalance;
    private javax.swing.JFormattedTextField txtPaid;
    private javax.swing.JFormattedTextField txtPaidAmtC;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTaxAmt;
    private javax.swing.JFormattedTextField txtTaxP;
    private javax.swing.JFormattedTextField txtTtlBfTax;
    private javax.swing.JFormattedTextField txtVouBalance;
    // End of variables declaration//GEN-END:variables
}
