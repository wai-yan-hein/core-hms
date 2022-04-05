/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
/**
 *
 * @author winswe
 */
public class InpSetupView extends AbstractView{
  private InpSetup panel;

  @Override
  protected JComponent createControl() {
    panel = new InpSetup();
    return panel;
  }

  @Override
  protected void registerLocalCommandExecutors(PageComponentContext context) {
    
  }
}
