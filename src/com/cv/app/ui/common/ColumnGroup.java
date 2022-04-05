/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.common;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * ColumnGroup
 *
 * @version 1.1 2010/10/23
 * @author Nobuo Tamemasa (modified by Q)
 */
public class ColumnGroup {

    protected TableCellRenderer renderer;
    protected Vector v;
    protected String text;
    protected int margin = 0;

    public ColumnGroup(String text) {
        this(null, text);
    }

    public ColumnGroup(TableCellRenderer renderer, String text) {
        if (renderer == null) {
            this.renderer = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    
                    JTableHeader header = table.getTableHeader();
                    
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                    
                    setHorizontalAlignment(JLabel.CENTER);
                    setText((value == null) ? "" : value.toString());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    
                    return this;
                }
            };
        } else {
            this.renderer = renderer;
        }
        
        this.text = text;
        v = new Vector();
    }

    /**
     * @param obj TableColumn or ColumnGroup
     */
    public void add(Object obj) {
        if (obj == null) {
            return;
        }
        
        v.addElement(obj);
    }

    /**
     * @param c TableColumn
     * @param v ColumnGroups
     */
    public Vector getColumnGroups(TableColumn c, Vector g) {
        g.addElement(this);
        
        if (v.contains(c)) {
            return g;
        }
        
        Enumeration en = v.elements();
        
        while (en.hasMoreElements()) {
            Object obj = en.nextElement();
            
            if (obj instanceof ColumnGroup) {
                Vector groups =
                        (Vector) ((ColumnGroup) obj).getColumnGroups(c, (Vector) g.clone());
                
                if (groups != null) {
                    return groups;
                }
            }
        }
        
        return null;
    }

    public TableCellRenderer getHeaderRenderer() {
        return renderer;
    }

    public void setHeaderRenderer(TableCellRenderer renderer) {
        if (renderer != null) {
            this.renderer = renderer;
        }
    }

    public Object getHeaderValue() {
        return text;
    }

    public Dimension getSize(JTable table) {
        Component comp = renderer.getTableCellRendererComponent(
                table, getHeaderValue(), false, false, -1, -1);
        int height = comp.getPreferredSize().height;
        int width = 0;
        Enumeration en = v.elements();
        
        while (en.hasMoreElements()) {
            Object obj = en.nextElement();
            
            if (obj instanceof TableColumn) {
                TableColumn aColumn = (TableColumn) obj;
                width += aColumn.getWidth();
            } else {
                width += ((ColumnGroup) obj).getSize(table).width;
            }
        }
        
        return new Dimension(width, height);
    }

    public void setColumnMargin(int margin) {
    }
}