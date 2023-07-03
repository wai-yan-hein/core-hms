/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.entry;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author winswe
 */
public class RoomManagementView extends AbstractView {

    private RoomManagement panel;

    @Override
    protected JComponent createControl() {
        panel = new RoomManagement();
        return panel;
    }

    @Override
    public void componentFocusGained(){
    }
    
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        
    }
}
