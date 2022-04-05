/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author winswe
 */
public class NormalPriceRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null) {
            String tmpStr = String.format("%,.0f", Double.parseDouble(value.toString()));
            ((JLabel) cell).setText(tmpStr);
        }

        ((JLabel) cell).setHorizontalAlignment(JLabel.RIGHT);
        cell.setBackground(new Color(227,232,229));
        cell.setForeground(Color.black);

        return cell;
    }
}
