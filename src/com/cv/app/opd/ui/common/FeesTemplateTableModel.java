/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.FeesTemplate;
import com.cv.app.util.JoSQLUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class FeesTemplateTableModel extends AbstractTableModel {

  private List<FeesTemplate> listFeesTemplate = new ArrayList();
  private String[] columnNames = {"Fees Name"};
  private int groupId;

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return true;
  }

  @Override
  public Class getColumnClass(int column) {
    return String.class;
  }

  @Override
  public Object getValueAt(int row, int column) {
    FeesTemplate record = listFeesTemplate.get(row);

    switch (column) {
      case 0: //Fees Name
        return record.getTemplateName();
      default:
        return null;
    }
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    FeesTemplate record = listFeesTemplate.get(row);

    switch (column) {
      case 0: //Fees Name
        if (value != null) {
          record.setTemplateName(value.toString());
          record.setGroupId(groupId);
          addNewRow();
        }
        break;
    }
    
    fireTableCellUpdated(row, column);
  }

  @Override
  public int getRowCount() {
    return listFeesTemplate.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  public List<FeesTemplate> getListFeesTemplate() {
    String strSQL = "SELECT * FROM com.cv.app.opd.database.entity.FeesTemplate"
            + " WHERE groupId > 0";
    List<FeesTemplate> list = JoSQLUtil.getResult(strSQL, listFeesTemplate);
    
    return list;
  }

  public void setListFeesTemplate(List<FeesTemplate> listFeesTemplate) {
    this.listFeesTemplate = listFeesTemplate;
    addNewRow();
    fireTableDataChanged();
  }

  public void deleteFeesTemplate(int row) {
    listFeesTemplate.remove(row);
    fireTableRowsDeleted(0, listFeesTemplate.size() - 1);
  }

  private void addNewRow() {
    int count = listFeesTemplate.size();

    if (count == 0 || listFeesTemplate.get(count - 1).getGroupId() != null) {
      listFeesTemplate.add(new FeesTemplate());
    }
    
    fireTableRowsInserted(listFeesTemplate.size()+1, listFeesTemplate.size()+1);
  }

  public void setGroupId(int id) {
    groupId = id;
  }

  public void removeAll() {
    listFeesTemplate.removeAll(listFeesTemplate);
    addNewRow();

    fireTableDataChanged();
  }
}
