/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.opd.ui.util;

import com.cv.app.opd.database.entity.LabMachine;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author winswe
 */
public class LabMachineAutoCompleter extends javax.swing.JDialog {
    private final JTable tblMachine = new JTable();
    private final List<LabMachine> listLM;
    private boolean selection = false;
    private LabMachine selLM;
    
    public LabMachineAutoCompleter(List<LabMachine> listLM, Frame parent){
        super(parent, true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        this.listLM = listLM;
        this.setUndecorated(true);
        tblMachine.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblMachine.setRowHeight(23);
        initTable();

        JScrollPane scroll = new JScrollPane(tblMachine);
        scroll.setBorder(null);

        
        scroll.getVerticalScrollBar().setFocusable(true);
        scroll.getHorizontalScrollBar().setFocusable(true);
        tblMachine.setRowSelectionInterval(0, 0);
        contentPane.add(scroll);
        this.pack();
    }
    
    private void initTable(){
        JTableBinding jTableBinding = SwingBindings.createJTableBinding(
                AutoBinding.UpdateStrategy.READ_WRITE, listLM, tblMachine);
        JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.
                jdesktop.beansbinding.ELProperty.create("${lMachineName}"));
        columnBinding.setColumnName("Lab Machine");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        
        jTableBinding.bind();
        
        tblMachine.setFocusable(true);
        
        tblMachine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMachine.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int row = tblMachine.getSelectedRow();
            
            if(row != -1){
                selLM = listLM.get(tblMachine.convertRowIndexToModel(row));
            }
            else
                selLM = null;
        });
        
        tblMachine.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Enter-Action");
        tblMachine.getActionMap().put("Enter-Action", actionEnterKey);
        
        tblMachine.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "ESC-Action");
        tblMachine.getActionMap().put("ESC-Action", actionEscKey);
        
        tblMachine.getColumnModel().getColumn(0).setPreferredWidth(50);
        
        //Capture mouse double click
        tblMachine.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                if(e.getClickCount() > 1){
                    selection = selLM != null;
                    
                    LabMachineAutoCompleter.this.dispose();
                }
            }
        });
    }
    
    private final Action actionEnterKey = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent e){ 
            selection = selLM != null;
            
            LabMachineAutoCompleter.this.dispose();
        }
    };
    
    private final Action actionEscKey = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent e){ 
            selection = false;
            LabMachineAutoCompleter.this.dispose();
        }
    };
    
    public boolean isSelected(){
        return selection;
    }
    
    public LabMachine getSelectMachine(){
        return selLM;
    }
}
