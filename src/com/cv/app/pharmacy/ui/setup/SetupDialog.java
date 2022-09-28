/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.inpatient.ui.setup.DiagnosisSetup;
import com.cv.app.opd.ui.setup.CitySetup;
import com.cv.app.opd.ui.setup.PatientOpSetup;
import com.cv.app.util.Util1;
import java.awt.Container;
import java.awt.Dimension;

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

        switch (panelName) {
            case "Item Type Setup":
                contentPane.add(new ItemTypeSetup());
                break;
            case "Item Unit Setup":
                contentPane.add(new ItemUnitSetup());
                break;
            case "Packing Template Setup":
                contentPane.add(new PackingTemplateSetup());
                break;
            case "Category Setup":
                contentPane.add(new CategorySetup());
                break;
            case "Character No Setup":
                contentPane.add(new CharacterNoSetup());
                break;
            case "Currency Setup":
                contentPane.add(new CurrencySetup());
                break;
            case "Customer Group Setup":
                contentPane.add(new CustomerGroupSetup());
                break;
            case "Location Setup":
                contentPane.add(new LocationSetup());
                break;
            case "Payment Type Setup":
                contentPane.add(new PaymentTypeSetup());
                break;
            case "Session Setup":
                contentPane.add(new SessionSetup());
                break;
            case "Charge Type Setup":
                contentPane.add(new ChargeTypeSetup());
                break;
            case "Expense Type Setup":
                contentPane.add(new ExpenseTypeSetup());
                break;
            case "Voucher Status Setup":
                contentPane.add(new VouStatusSetup());
                break;
            case "Adjustment Type Setup":
                contentPane.add(new AdjTypeSetup());
                break;
            case "Trader Opening Setup":
                contentPane.add(new TraderOpSetup());
                break;
            case "Brand Setup":
                contentPane.add(new ItemBrandSetup());
                break;
            case "City Setup":
                contentPane.add(new CitySetup());
                break;
            case "Patient Opening Setup":
                contentPane.add(new PatientOpSetup());
                break;
            case "Diagnosis":
                contentPane.add(new DiagnosisSetup());
                break;
            default:
                break;
        }
        Dimension screen = Util1.getScreenSize();
        setSize(screen.width / 2, screen.height / 2);
        setLocationRelativeTo(null);
    }// </editor-fold>    
}
