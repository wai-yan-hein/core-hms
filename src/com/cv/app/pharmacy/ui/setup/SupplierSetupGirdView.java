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
public class SupplierSetupGirdView extends AbstractView{
    private SupplierSetupGrid panel;

    @Override
    protected JComponent createControl() {
       panel = new SupplierSetupGrid();
       return panel;
    }
    @Override
     protected void registerLocalCommandExecutors(PageComponentContext context) {
       
    }
    
}
