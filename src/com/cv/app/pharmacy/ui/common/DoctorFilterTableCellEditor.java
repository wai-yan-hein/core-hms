/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.util.DoctorAutoCompleter;
import java.awt.Component;
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
public class DoctorFilterTableCellEditor extends AbstractCellEditor implements TableCellEditor{
    private JComponent component = null;
    private AbstractDataAccess dao;
    private DoctorAutoCompleter completer;
    
    public DoctorFilterTableCellEditor(AbstractDataAccess dao){
        this.dao = dao;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        List<Doctor> listDoctor = dao.findAll("Doctor", "active = true");
        
        component = jtf;
        completer = new DoctorAutoCompleter(jtf, listDoctor, this);
        
        return component;
    }
    
    @Override
    public Object getCellEditorValue() {
        Object obj = null;
        Doctor doctor = completer.getSelDoctor();
        
        if(doctor != null){
            obj = doctor.getDoctorId();
        }else{
            obj = ((JTextField)component).getText();
        }
        
        return obj;
    }
}
