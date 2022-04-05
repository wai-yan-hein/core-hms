/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.setup;

import java.awt.Container;

/**
 *
 * @author winswe
 */
public class SetupDialog extends javax.swing.JDialog {
    private String panelName;
    
    public SetupDialog(java.awt.Frame parent, boolean modal, String panelName) {
        super(parent, panelName, modal);
        this.panelName = panelName;
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        
        if(panelName.equals("Menu Type Setup"))
            contentPane.add(new MenuTypeSetup());
                
        pack();
    }// </editor-fold>    
}
