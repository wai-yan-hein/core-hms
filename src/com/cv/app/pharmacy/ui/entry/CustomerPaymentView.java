/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.util.Util1;
import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author lenovo
 */
public class CustomerPaymentView extends AbstractView {
    //private CustomerPayment1 panel;

    @Override
    protected JComponent createControl() {
        if (Util1.getPropValue("system.pharmacy.cuspayment").equals("N")) {
            return new CustomerPayment1();
        } else {
            return new CustomerPayment();
        }
    }

    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {

    }

}
