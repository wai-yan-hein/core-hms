/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Pathologiest;
import com.cv.app.opd.ui.util.PathoAutoCompleter;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;

/**
 *
 * @author MyintMo
 */
public class PathoCellEditor extends AbstractCellEditor implements TableCellEditor {
    
    static Logger log = Logger.getLogger(PathoCellEditor.class.getName());
    private JComponent component = null;
    private PathoAutoCompleter completer;
    //private final AbstractDataAccess dao = Global.dao;
    
    public PathoCellEditor(){
        
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();

                if ((keyEvent.isControlDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyEvent.isShiftDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyCode == KeyEvent.VK_F5)
                        || (keyCode == KeyEvent.VK_F7)
                        || (keyCode == KeyEvent.VK_F9)
                        || (keyCode == KeyEvent.VK_F10)
                        || (keyCode == KeyEvent.VK_ESCAPE)) {
                    stopCellEditing();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }

            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }
        };

        jtf.addKeyListener(keyListener);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        completer = new PathoAutoCompleter(jtf, this);
        
        return component;
    }
    
    @Override
    public Object getCellEditorValue() {
        Object obj;
        Pathologiest patho = completer.getPatho();
        
        if(patho != null){
            obj = patho;
        } else {
            obj = ((JTextField) component).getText();
        }
        
        return obj;
    }
}
