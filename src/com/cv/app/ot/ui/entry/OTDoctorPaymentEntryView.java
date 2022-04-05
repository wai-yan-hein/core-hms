/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.entry;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author winswe
 */
public class OTDoctorPaymentEntryView extends AbstractView {

    private OTDoctorPayment panel;

    @Override
    protected JComponent createControl() {
        panel = new OTDoctorPayment();
        return panel;
    }
    
    @Override
    public void componentFocusGained(){
        System.out.println("Focus in view.");
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
            
        }
    }

    private class NewFormExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            
        }
    }

    private class DeleteExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            
        }
    }

    private class PrintExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            
        }
    }
    
    private class HistoryExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            
        }
    }
    
    private class DeleteCopyExecutor implements ActionCommandExecutor {
        @Override
        public void execute(){
            
        }
    }*/
}
