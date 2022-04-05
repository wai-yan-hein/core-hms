/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import javax.swing.JComponent;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author WSwe
 */
public class PaymentView extends AbstractView{
    @Override
    protected JComponent createControl() {
        return new Payment();
    }
}
