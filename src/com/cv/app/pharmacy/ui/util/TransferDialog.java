/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.view.VReOrderLevel;
import com.cv.app.pharmacy.ui.entry.Transfer;
import com.cv.app.util.Util1;
import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

/**
 *
 * @author winswe
 */
public class TransferDialog extends javax.swing.JDialog{
    
    public TransferDialog(java.awt.Frame parent, List<VReOrderLevel> listReOrderLevel,
            Location from, Location to){
        super(parent, "Transfer", false);
        initComponents(listReOrderLevel, from, to);
    }
    
    private void initComponents(List<VReOrderLevel> listReOrderLevel, Location from, Location to) {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.add(new Transfer(listReOrderLevel, from, to));
        
        Dimension screen = Util1.getScreenSize();
        screen.height = screen.height - 200;
        screen.width = screen.width - 300;
        this.setMinimumSize(screen);
        /*int x = ((screen.width - this.getWidth()) / 2);
        int y = ((screen.height - this.getHeight()) / 2);

        setLocation(x, y);*/
        setVisible(true);
        pack();
    }
}
