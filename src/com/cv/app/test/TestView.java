/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.test;

import javax.swing.JComponent;
import javax.swing.JPanel;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author WSwe
 */
public class TestView extends AbstractView {
    
    @Override
    protected JComponent createControl() {
        JPanel panel = new TestPanel();
        return panel;
    }
}
