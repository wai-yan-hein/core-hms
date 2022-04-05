/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author WSwe
 */
public class UnitAutoCompleter extends javax.swing.JDialog{

    private JTable tblUnit = new JTable();
    private List<ItemUnit> lstUnit;
    private ItemUnit selUnit = null;
    private boolean selection = false;
    private int x;
    private int y;

    public UnitAutoCompleter(int x, int y, List<ItemUnit> lstUnit, Frame parent) {
        super(parent, true);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        
        this.x = x;
        this.y = y;
        this.lstUnit = lstUnit;
        this.setUndecorated(true);
        
        tblUnit.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblUnit.setRowHeight(23);
        initTable();

        JScrollPane scroll = new JScrollPane(tblUnit);
        scroll.setBorder(null);

        
        scroll.getVerticalScrollBar().setFocusable(true);
        scroll.getHorizontalScrollBar().setFocusable(true);
        tblUnit.setRowSelectionInterval(0, 0);
        contentPane.add(scroll);
        this.setLocation(x, y);
        this.setSize(80, 110);
        this.show();
    }
    
    private void initTable(){
        JTableBinding jTableBinding = SwingBindings.createJTableBinding(
                AutoBinding.UpdateStrategy.READ_WRITE, lstUnit, tblUnit);
        JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.
                jdesktop.beansbinding.ELProperty.create("${itemUnitName}"));
        columnBinding.setColumnName("Unit");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        
        jTableBinding.bind();
        
        tblUnit.setFocusable(true);
        
        tblUnit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUnit.getSelectionModel().addListSelectionListener(
                new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent e) {
                    int row = tblUnit.getSelectedRow();
                    
                    if(row != -1){
                        selUnit = lstUnit.get(tblUnit.convertRowIndexToModel(row));
                    }
                    else
                        selUnit = null;
                }
            }
         );
        
        tblUnit.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Enter-Action");
        tblUnit.getActionMap().put("Enter-Action", actionEnterKey);
        
        tblUnit.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "ESC-Action");
        tblUnit.getActionMap().put("ESC-Action", actionEscKey);
        
        tblUnit.getColumnModel().getColumn(0).setPreferredWidth(50);
        
        //Capture mouse double click
        tblUnit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                if(e.getClickCount() > 1){
                    if(selUnit != null)
                        selection = true;
                    else
                        selection = false;
                    
                    UnitAutoCompleter.this.dispose();
                }
            }
        });
    }
    
    public ItemUnit getSelUnit(){
        return selUnit;
    }
    
    public boolean isSelected(){
        return selection;
    }
    
    private Action actionEnterKey = new AbstractAction(){
        public void actionPerformed(ActionEvent e){ 
            if(selUnit != null){
                selection = true;
            }else{
                selection = false;
            }
            
            UnitAutoCompleter.this.dispose();
        }
    };
    
    private Action actionEscKey = new AbstractAction(){
        public void actionPerformed(ActionEvent e){ 
            selection = false;
            UnitAutoCompleter.this.dispose();
        }
    };
}
