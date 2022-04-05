/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.setup;

import javax.swing.JComponent;
import javax.swing.JPanel;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author WSwe
 */
public class AppUserSetupView extends AbstractView{
    @Override
    protected JComponent createControl() {
        JPanel panel = new AppUserSetup();
        return panel;
    }
}
