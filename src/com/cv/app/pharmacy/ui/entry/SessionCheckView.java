/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.util.Util1;
import javax.swing.JComponent;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author WSwe
 */
public class SessionCheckView extends AbstractView {

    @Override
    protected JComponent createControl() {
        String propValue = Util1.getPropValue("system.pharmacy.session.check");
        if (propValue.equals("F")) {
            return new SessionCheck1();
        } else {
            return new SessionCheck();
        }
    }
}
