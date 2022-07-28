/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.common.KeyPropagate;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.ot.ui.util.OTDrFeeAutoCompleter;
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
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTDrFeeTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    static Logger log = Logger.getLogger(OTDrFeeTableCellEditor.class.getName());
    private JComponent component = null;
    private final AbstractDataAccess dao;
    private OTDrFeeAutoCompleter completer;
    private KeyPropagate kp;

    /*public OTDrFeeTableCellEditor(AbstractDataAccess dao){
        this.dao = dao;
    }*/
    public OTDrFeeTableCellEditor(AbstractDataAccess dao, KeyPropagate kp) {
        this.dao = dao;
        this.kp = kp;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();

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
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }

        try {
            List<Doctor> listService = dao.findAllHSQL("select o from Doctor o where o.active = true");
            completer = new OTDrFeeAutoCompleter(jtf, listService, this);
        } catch (Exception ex) {
            log.error("getTableCellEditorComponent : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Doctor srv = completer.getSelDoctor();
        return srv;
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
