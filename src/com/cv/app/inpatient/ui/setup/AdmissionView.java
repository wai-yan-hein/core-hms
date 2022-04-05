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
public class AdmissionView extends AbstractView {

    private Admission panel;
    private FormAction formAction;

    @Override
    protected JComponent createControl() {
        panel = new Admission();
        formAction = panel;
        return panel;
    }

    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("saveCommand", new SaveExecutor());
        context.register("newVouCommand", new NewFormExecutor());
        context.register("deleteCommand", new DeleteExecutor());
        context.register("historyCommand", new HistoryExecutor());
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

    private class HistoryExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.history(); //Calling FormAction method
        }
    }
}
