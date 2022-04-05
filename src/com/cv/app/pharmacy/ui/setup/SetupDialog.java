/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.opd.ui.setup.CitySetup;
import com.cv.app.inpatient.ui.setup.DiagnosisSetup;
import com.cv.app.opd.ui.setup.PatientOpSetup;
import java.awt.Container;

/**
 *
 * @author winswe
 */
public class SetupDialog extends javax.swing.JDialog {

    private String panelName;

    public SetupDialog(java.awt.Frame parent, boolean modal, String panelName) {
        super(parent, panelName, modal);
        this.panelName = panelName;
        initComponents();
        //this.setSize(1024, 600);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        if (panelName.equals("Item Type Setup")) {
            contentPane.add(new ItemTypeSetup());
        } else if (panelName.equals("Item Unit Setup")) {
            contentPane.add(new ItemUnitSetup());
        } else if (panelName.equals("Packing Template Setup")) {
            contentPane.add(new PackingTemplateSetup());
        } else if (panelName.equals("Category Setup")) {
            contentPane.add(new CategorySetup());
        } else if (panelName.equals("Character No Setup")) {
            contentPane.add(new CharacterNoSetup());
        } else if (panelName.equals("Currency Setup")) {
            contentPane.add(new CurrencySetup());
        } else if (panelName.equals("Customer Group Setup")) {
            contentPane.add(new CustomerGroupSetup());
        } else if (panelName.equals("Location Setup")) {
            contentPane.add(new LocationSetup());
        } else if (panelName.equals("Payment Type Setup")) {
            contentPane.add(new PaymentTypeSetup());
        } else if (panelName.equals("Session Setup")) {
            contentPane.add(new SessionSetup());
        } else if (panelName.equals("Charge Type Setup")) {
            contentPane.add(new ChargeTypeSetup());
        } else if (panelName.equals("Expense Type Setup")) {
            contentPane.add(new ExpenseTypeSetup());
        } else if (panelName.equals("Voucher Status Setup")) {
            contentPane.add(new VouStatusSetup());
        } else if (panelName.equals("Adjustment Type Setup")) {
            contentPane.add(new AdjTypeSetup());
        } else if (panelName.equals("Trader Opening Setup")) {
            contentPane.add(new TraderOpSetup());
        } else if (panelName.equals("Brand Setup")) {
            contentPane.add(new ItemBrandSetup());
        }else if (panelName.equals("City Setup")) {
            contentPane.add(new CitySetup());
        }else if (panelName.equals("Patient Opening Setup")) {
            contentPane.add(new PatientOpSetup());
        }else if (panelName.equals("Diagnosis")) {
            contentPane.add(new DiagnosisSetup());
        }
        
        pack();
    }// </editor-fold>    
}
