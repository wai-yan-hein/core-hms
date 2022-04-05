/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.ui.util.ItemUnitAutoCompleter;
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
public class TableUnitCellEditor extends AbstractCellEditor implements TableCellEditor{
    private JComponent component = null;
    private List<ItemUnit> listUnit;
    private ItemUnitAutoCompleter completer;
    
    public TableUnitCellEditor(List<ItemUnit> listUnit){
        this.listUnit = listUnit;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        
        component = jtf;
        completer = new ItemUnitAutoCompleter(jtf, listUnit, this);
        
        return component;
    }
    
    @Override
    public Object getCellEditorValue() {
        Object obj = completer.getSelUnit();
        return obj;
    }
}
