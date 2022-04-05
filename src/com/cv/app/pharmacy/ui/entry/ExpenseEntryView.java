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
 * This class is container class of Damage. 
 */
public class ExpenseEntryView extends AbstractView{
    
    @Override
    protected JComponent createControl() {
        return new ExpenseEntry();
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
