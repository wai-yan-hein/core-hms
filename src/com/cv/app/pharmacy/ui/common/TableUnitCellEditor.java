/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.ui.util.ItemUnitAutoCompleter;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author WSwe
 */
public class TableUnitCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JComponent component = null;
    private List<ItemUnit> listUnit;
    private ItemUnitAutoCompleter completer;
    private KeyPropagate kp;

    public TableUnitCellEditor(List<ItemUnit> listUnit, KeyPropagate kp) {
        this.listUnit = listUnit;
        this.kp = kp;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        if (kp != null) {
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
                        kp.keyEvent(keyEvent);
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
        }
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        completer = new ItemUnitAutoCompleter(jtf, listUnit, this);
        component = jtf;
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj = completer.getSelUnit();
        return obj;
    }
}
