/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.common;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author WSwe
 */
public class TableDecimalFieldRenderer extends DefaultTableCellRenderer {

    DecimalFormat f = new DecimalFormat("#,##0.00000");

    public TableDecimalFieldRenderer(){
        super();
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        
        if (value instanceof Date) {
            value = f.format(value);
        }
        
        return super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
    }
}
