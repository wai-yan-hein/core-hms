/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.util.Util1;
import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommandExecutor;

/**
 *
 * This class is container class of Transfer.
 */
public class TransferView extends AbstractView{
    private JComponent panel;
    private FormAction formAction;
    
    @Override
    protected JComponent createControl() {
        String propValue = Util1.getPropValue("system.transfer.form");
        if (propValue.equals("NEW")) {
            Transfer1 purchase = new Transfer1();
            formAction = purchase;
            panel = purchase;
        } else {
            Transfer purchase = new Transfer();
            formAction = purchase;
            panel = purchase;
        }
        
        return panel;
    }
    
    /*
     * Registering command for toolbar.
     */
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context){
        context.register("saveCommand", new SaveExecutor());
        context.register("newVouCommand", new NewFormExecutor());
        context.register("historyCommand", new HistoryExecutor());
        context.register("deleteCommand", new DeleteExecutor());
        context.register("printCommand", new PrintExecutor());
    }
    
    @Override
    public void componentFocusGained(){
        //System.out.println("Focus in view.");
        //panel.setFocus();
    }
    
    private class SaveExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            formAction.save(); //Calling FormAction method
        }
    }
    
    private class NewFormExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            formAction.newForm(); //Calling FormAction method
        }
    }
    
    private class HistoryExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            formAction.history(); //Calling FormAction method
        }
    }
    
    private class DeleteExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            formAction.delete(); //Calling FormAction method
        }
    }
    
    private class PrintExecutor implements ActionCommandExecutor{
        @Override
        public void execute(){
            formAction.print(); //Calling FormAction method
        }
    }
}
