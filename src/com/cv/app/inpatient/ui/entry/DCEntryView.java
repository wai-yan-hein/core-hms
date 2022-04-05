/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.entry;

import com.cv.app.pharmacy.ui.common.FormAction;
import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommandExecutor;

/**
 *
 * @author winswe
 */
public class DCEntryView extends AbstractView {

    private DCEntry1 panel;
    private FormAction formAction;

    @Override
    protected JComponent createControl() {
        panel = new DCEntry1();
        formAction = panel;
        return panel;
    }

    @Override
    public void componentFocusGained(){
        System.out.println("Focus in view.");
        panel.timerFocus();
    }
    
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("saveCommand", new SaveExecutor());
        context.register("newVouCommand", new NewFormExecutor());
        context.register("deleteCommand", new DeleteExecutor());
        context.register("printCommand", new PrintExecutor());
        context.register("historyCommand", new HistoryExecutor());
        context.register("delCopyCommand", new DeleteCopyExecutor());
    }

    private class SaveExecutor implements ActionCommandExecutor {

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
    }
}
