/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.util.Util1;
import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommandExecutor;

/**
 *
 * @author winswe
 */
public class ItemSetupView extends AbstractView {

    private FormAction formAction;

    @Override
    protected JComponent createControl() {
        String codeUsage = Util1.getPropValue("system.item.code.field");
        JComponent jc;
        
        if (codeUsage.equals("SHORTNAME")) {
            ItemSetupKS panel = new ItemSetupKS();
            formAction = panel;
            jc = panel;
        } else {
            ItemSetup panel = new ItemSetup();
            formAction = panel;
            jc = panel;
        }

        return jc;
    }

    @Override
    public void componentFocusGained() {
        //System.out.println("Focus in view.");
        //panel.setFocus();
    }

    /*
     * Registering command for toolbar.
     */
    @Override
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("saveCommand", new ItemSetupView.SaveExecutor());
        context.register("newVouCommand", new ItemSetupView.NewFormExecutor());
        context.register("deleteCommand", new DeleteExecutor());
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

    private class DeleteExecutor implements ActionCommandExecutor {

        @Override
        public void execute() {
            formAction.delete(); //Calling FormAction method
        }
    }

    @Override
    public void close() {
        super.close();
    }
}
