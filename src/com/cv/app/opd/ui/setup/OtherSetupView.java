/*
 * To change this template, choose Tools | Templates
 * and open the template in  the editor.
 */
package com.cv.app.opd.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author winswe
 */
public class OtherSetupView extends AbstractView{

  @Override
  protected JComponent createControl() {
    return new OtherSetup();
  }

  @Override
  protected void registerLocalCommandExecutors(PageComponentContext context) {
  }

  
}
