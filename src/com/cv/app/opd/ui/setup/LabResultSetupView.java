/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
/**
 *
 * @author winswe
 */
public class LabResultSetupView extends AbstractView{
  private LabResultSetup panel;

  @Override
  protected JComponent createControl() {
    panel = new LabResultSetup();
    return panel;
  }

  @Override
  protected void registerLocalCommandExecutors(PageComponentContext context) {
    
  }
}
