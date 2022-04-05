/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author winswe 
 */
public class UserPropSetupView extends AbstractView{
    
    @Override
    protected JComponent createControl() {
        return new UserPropSetup();
    }
    
    /*@Override
    public void componentFocusGained(){
        System.out.println("Focus in view.");
        panel.setFocus();
    }*/
    
    /*
     * Registering command for toolbar.
     */
    /*@Override
    protected void registerLocalCommandExecutors(PageComponentContext context){
        context.register("saveCommand", new SysPropSetupView.SaveExecutor());
        context.register("newVouCommand", new SysPropSetupView.NewFormExecutor());
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
    }*/
}
