/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.inpatient.ui.util.DCAutoCompleter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
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
public class DCTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JComponent component = null;
    private AbstractDataAccess dao;
    private DCAutoCompleter completer;

    public DCTableCellEditor(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        List<InpService> listService = dao.findAllHSQL("select o from InpService o where o.status = true");

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
        completer = new DCAutoCompleter(jtf, listService, this);

        return component;
    }

    @Override
    public Object getCellEditorValue() {
        if (completer != null) {
            InpService srv = completer.getSelService();
            return srv;
        } else {
            return null;
        }
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return false;
        } else if (anEvent instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) anEvent;

            if (ke.isActionKey()) {//Function key
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
