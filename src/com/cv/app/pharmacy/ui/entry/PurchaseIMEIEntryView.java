/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * This class is container class of Purchase.
 */
public class PurchaseIMEIEntryView extends AbstractView{
    private PurchaseIMEIEntry panel;
    
    @Override
    protected JComponent createControl() {
        panel = new PurchaseIMEIEntry();
        
        return panel;
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
