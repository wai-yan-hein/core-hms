/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.ui.util.TownshipAutoCompleter;
import java.awt.Component;
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
public class TownshipTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    static Logger log = Logger.getLogger(TownshipTableCellEditor.class.getName());
    private JComponent component = null;
    private final AbstractDataAccess dao;
    private TownshipAutoCompleter completer;

    public TownshipTableCellEditor(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        component = jtf;
        try {
            List<Township> listTownship = dao.findAllHSQL("select o from Township o order by o.townshipName");
            completer = new TownshipAutoCompleter(jtf, listTownship, this);
        } catch (Exception ex) {
            log.error("getTableCellEditorComponent : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj = completer.getSelTownship();
        return obj;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return !(anEvent instanceof MouseEvent);
    }
}
