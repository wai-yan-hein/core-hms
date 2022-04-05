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
public class DCPatientBalanceView extends AbstractView {

    @Override
    protected JComponent createControl() {
        return new DCPatientBalance();
    }

    @Override
    public void componentFocusGained(){
        
    }
    
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        /*context.register("saveCommand", new SaveExecutor());
        context.register("newVouCommand", new NewFormExecutor());
        context.register("deleteCommand", new DeleteExecutor());
        context.register("printCommand", new PrintExecutor());
        context.register("historyCommand", new HistoryExecutor());
        context.register("delCopyCommand", new DeleteCopyExecutor());*/
    }

    /*private class SaveExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            panel.save(); //Calling FormAction method
        }
    }

    private class NewFormExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            panel.newForm(); //Calling FormAction method
        }
    }

    private class DeleteExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.delete(); //Calling FormAction method
        }
    }

    private class PrintExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.print(); //Calling FormAction method
        }
    }
    
    private class HistoryExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            formAction.history(); //Calling FormAction method
        }
    }
    
    private class DeleteCopyExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            formAction.deleteCopy(); //Calling FormAction method
        }
    }*/
}
