/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author winswe 
 */
public class GroupingSetupView extends AbstractView{
    
    @Override
    protected JComponent createControl() {
        return new GroupingSetup();
    }
    
    @Override
    public void componentFocusGained(){
    }
    
    /*
     * Registering command for toolbar.
     */
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context){
    }
    
}
