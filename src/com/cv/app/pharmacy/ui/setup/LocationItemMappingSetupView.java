/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author Eitar
 */
public class LocationItemMappingSetupView extends AbstractView{
    private LocationItemMappingSetup panel;
    //private FormAction formAction;
    
    @Override
    protected JComponent createControl() {
        panel = new LocationItemMappingSetup();
        //formAction = panel;
        
        return panel;
    }
    
    @Override
    public void componentFocusGained(){
        //System.out.println("Focus in view.");
        //panel.setFocus();
    }
    
    /*
     * Registering command for toolbar.
     */
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context){
        /*context.register("saveCommand", new LocationItemMappingView.SaveExecutor());
        context.register("newVouCommand", new LocationItemMappingView.NewFormExecutor());
        context.register("deleteCommand", new DeleteExecutor());*/
    }
    
    /*private class SaveExecutor implements ActionCommandExecutor {
        public void execute(){
            formAction.save(); //Calling FormAction method
        }
    }
    
    private class NewFormExecutor implements ActionCommandExecutor {
        public void execute(){
            formAction.newForm(); //Calling FormAction method
        }
    }
    
    private class DeleteExecutor implements ActionCommandExecutor {
        public void execute(){
            formAction.delete(); //Calling FormAction method
        }
    }*/
    
    @Override
    public void close() {
        super.close();
    }
}
