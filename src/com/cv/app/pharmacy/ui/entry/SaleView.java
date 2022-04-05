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
 * This class is container class of Sale.
 */
public class SaleView extends AbstractView {

    private JComponent panel;
    private FormAction formAction;
    private Sale1 sale1;
    private Sale sale;
    @Override
    protected JComponent createControl() {
        String propValue = Util1.getPropValue("system.sale.form");
        if (propValue.equals("NEW")) {
            sale1 = new Sale1();
            panel = sale1;
            formAction = sale1;
        } else {
            sale = new Sale();
            panel = sale;
            formAction = sale;
        }
        
        return panel;
    }

    @Override
    public void componentFocusGained() {
        System.out.println("Focus in view.");
        if(sale != null){
            sale.timerFocus();
        }else if(sale1 != null){
            sale1.timerFocus();
        }
    }

    /*
     * Registering command for toolbar.
     */
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("saveCommand", new SaveExecutor());
        context.register("newVouCommand", new NewFormExecutor());
        context.register("historyCommand", new HistoryExecutor());
        context.register("deleteCommand", new DeleteExecutor());
        context.register("delCopyCommand", new DeleteCopyExecutor());
        context.register("printCommand", new PrintExecutor());
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

    private class HistoryExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.history(); //Calling FormAction method
        }
    }

    private class DeleteExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.delete(); //Calling FormAction method
        }
    }

    private class DeleteCopyExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.deleteCopy(); //Calling FormAction method
        }
    }

    private class PrintExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.print(); //Calling FormAction method
        }
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void dispose() {
        System.out.println("Dispose");
        super.dispose();
    }
}
