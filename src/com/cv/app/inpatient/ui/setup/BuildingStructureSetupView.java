/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.pharmacy.ui.common.FormAction;
import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommandExecutor;

/**
 *
 * @author winswe 
 */
public class BuildingStructureSetupView extends AbstractView{
    private FormAction formAction;
    private BuildingStructureSetup panel;
    
    @Override
    protected JComponent createControl() {
        panel = new BuildingStructureSetup();
        formAction = panel;
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
        context.register("saveCommand", new SaveExecutor());
        context.register("newVouCommand", new NewFormExecutor());
    }
    
    private class SaveExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.save(); //Calling FormAction method
        }
    }
    
    private class NewFormExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.newForm(); //Calling FormAction method
        }
    }
}
