/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.ActionCommandExecutor;

/**
 *
 * @author winswe
 */
public class PackageSetupView extends AbstractView{
  private PackageSetup panel;

  @Override
  protected JComponent createControl() {
    panel = new PackageSetup();
    return panel;
  }

  @Override
  protected void registerLocalCommandExecutors(PageComponentContext context) {
    
  }

  private class SaveExecutor implements ActionCommandExecutor {

    @Override
    public void execute() {
      //panel.save(); //Calling FormAction method
    }
  }

  private class NewFormExecutor implements ActionCommandExecutor {

    @Override
    public void execute() {
      //panel.newForm(); //Calling FormAction method
    }
  }

  private class DeleteExecutor implements ActionCommandExecutor {

    @Override
    public void execute() {
      //formAction.delete(); //Calling FormAction method
    }
  }
}
