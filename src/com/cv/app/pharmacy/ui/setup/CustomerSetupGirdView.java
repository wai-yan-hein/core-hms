/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import javax.swing.JComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author lenovo
 */
public class CustomerSetupGirdView extends AbstractView{
    private CustomerSetupGrid panel;

    @Override
    protected JComponent createControl() {
       panel = new CustomerSetupGrid();
       return panel;
    }
    @Override
     protected void registerLocalCommandExecutors(PageComponentContext context) {
       
    }
    
}
