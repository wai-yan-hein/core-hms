/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup.mapping;

import com.cv.app.pharmacy.ui.common.FormAction;
import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommandExecutor;

/**
 *
 * @author winswe 
 */
public class ItemMappingSetupView extends AbstractView{
    private ItemMappingSetup panel;
    private FormAction formAction;
    
    @Override
    protected JComponent createControl() {
        panel = new ItemMappingSetup();
        formAction = panel;
        
        return panel;
    }
    
    @Override
    public void componentFocusGained(){
        System.out.println("Focus in view.");
        panel.setFocus();
    }
    
    /*
     * Registering command for toolbar.
     */
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context){
        context.register("saveCommand", new SaveExecutor());
        context.register("newVouCommand", new NewFormExecutor());
        context.register("deleteCommand", new DeleteExecutor());
    }
    
    private class SaveExecutor implements ActionCommandExecutor {
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
    }
    
    @Override
    public void close() {
        super.close();
    }
}
