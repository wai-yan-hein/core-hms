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
public class ConditionRenderer extends DefaultTableCellRenderer{
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,   boolean hasFocus, int row, int column) {
      Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      //cell.setBackground(backgroundColor);
      if(value == null){
          cell.setForeground(Color.black);
      }else{
          float tmpValue = Float.parseFloat(value.toString());
          if(tmpValue < 0){
              cell.setForeground(Color.red);
          }else{
              cell.setForeground(Color.black);
          }
      }
      
      ((JLabel) cell).setHorizontalAlignment(JLabel.RIGHT);
      
      return cell;
   }
}
