/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author winswe
 */
public class InpAccSetupView extends AbstractView {

    private InpAccSetup panel;

    @Override
    protected JComponent createControl() {
        panel = new InpAccSetup();
        return panel;
    }

    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {

    }
}
