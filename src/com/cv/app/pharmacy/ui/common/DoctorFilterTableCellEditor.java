/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.util.DoctorAutoCompleter;
import java.awt.Component;
import java.awt.event.KeyEvent;
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
public class DoctorFilterTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    static Logger log = Logger.getLogger(DoctorFilterTableCellEditor.class.getName());
    private JComponent component = null;
    private AbstractDataAccess dao;
    private DoctorAutoCompleter completer;

    public DoctorFilterTableCellEditor(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        try {
            JTextField jtf = new JTextField();
            List<Doctor> listDoctor = dao.findAll("Doctor", "active = true");

            component = jtf;
            completer = new DoctorAutoCompleter(jtf, listDoctor, this);

            return component;
        } catch (Exception ex) {
            log.error("getTableCellEditorComponent : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return null;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj = null;
        Doctor doctor = completer.getSelDoctor();

        if (doctor != null) {
            obj = doctor.getDoctorId();
        } else {
            obj = ((JTextField) component).getText();
        }

        return obj;
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
