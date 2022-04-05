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
 * This class is container class of PriceChange.
 */
public class PriceChangeView extends AbstractView {

    //private PriceChange1 panel;
    private FormAction formAction;

    @Override
    protected JComponent createControl() {
        if (Util1.getPropValue("system.pharmacy.pricechange").equals("N")) {
            PriceChange1 panel = new PriceChange1();
            formAction = panel;
            return panel;
        } else {
            PriceChange panel = new PriceChange();
            formAction = panel;
            return panel;
        }
    }

    @Override
    public void componentFocusGained() {
        System.out.println("Focus in view.");
        //panel.setFocus();
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
        context.register("printCommand", new PrintExecutor());
    }

    private class SaveExecutor implements ActionCommandExecutor {

        public void execute() {
            formAction.save(); //Calling FormAction method
        }
    }

    private class NewFormExecutor implements ActionCommandExecutor {

        public void execute() {
            formAction.newForm(); //Calling FormAction method
        }
    }

    private class HistoryExecutor implements ActionCommandExecutor {

        public void execute() {
            formAction.history(); //Calling FormAction method
        }
    }

    private class DeleteExecutor implements ActionCommandExecutor {

        public void execute() {
            formAction.delete(); //Calling FormAction method
        }
    }

    private class PrintExecutor implements ActionCommandExecutor {

        public void execute() {
            formAction.print(); //Calling FormAction method
        }
    }
}
