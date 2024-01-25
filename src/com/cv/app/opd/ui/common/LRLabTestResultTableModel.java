/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

//import com.cv.app.opd.database.entity.LabResultAutoText;
import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.LabResultHis;
import com.cv.app.opd.database.entity.LabResultMethod;
import com.cv.app.opd.ui.util.JavaFXHTMLEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LRLabTestResultTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LRLabTestResultTableModel.class.getName());
    private List<LabResultHis> listLRH = new ArrayList();
    private final String[] columnNames = {"Lab Test", "Result", "Ref. Range", 
        "Unit", "Remark", "Method", "Print", "Comments"};
    private final AbstractDataAccess dao = Global.dao;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1 || column == 2 || column == 4 || column == 5 || column == 6 || column == 7;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 6:
                return Boolean.class;
            case 5:
                return LabResultMethod.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        LabResultHis record = listLRH.get(row);

        switch (column) {
            case 0: //Test
                return record.getResultText();
            case 1: //Result
                return record.getResult();
            case 2: //Ref. Range
                return record.getRefRange();
            case 3: //Unit
                return record.getResultUnit();
            case 4: //Remark
                return record.getRemark();
            case 5: //Method
                if (record.getMethod() == null) {
                    return null;
                } else {
                    return record.getMethod();
                }
            case 6: //Print
                if(record.getPrint() == null){
                    return false;
                } else {
                    return record.getPrint();
                }
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        LabResultHis record = listLRH.get(row);

        switch (column) {
            case 1: //Result
                if (value != null) {
                    /*if(value instanceof LabResultAutoText){
                        String tmpResult = ((LabResultAutoText)value).getAutoText();
                        record.setResult(tmpResult);
                    }else{*/
                    record.setResult(value.toString());
                    if (!value.toString().trim().isEmpty()) {
                        record.setPrint(Boolean.TRUE);
                    }else{
                        record.setPrint(Boolean.FALSE);
                    }
                    //}
                } else {
                    record.setResult(null);
                    record.setPrint(Boolean.FALSE);
                }
                break;
            case 2: //Ref Range
                if(value == null){
                    record.setRefRange(null);
                }else{
                    record.setRefRange(value.toString());
                }
                break;
            case 4: //Remark
                if (value != null) {
                    record.setRemark(value.toString());
                } else {
                    record.setRemark(null);
                }
                break;
            case 5: //Method
                if (value != null) {
                    if (value.toString().isEmpty()) {
                        record.setMethod(null);
                    } else {
                        record.setMethod((LabResultMethod) value);
                    }
                } else {
                    record.setMethod(null);
                }
                break;
            case 6: //Print
                if (value != null) {
                    record.setPrint((Boolean) value);
                } else {
                    record.setPrint(Boolean.FALSE);
                }
        }

        fireTableCellUpdated(row, column);
        fireTableCellUpdated(row, 6);
        saveRecord(record);
    }

    @Override
    public int getRowCount() {
        return listLRH.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<LabResultHis> getListLRH() {
        return listLRH;
    }

    public void setListLRH(List<LabResultHis> listLRH) {
        this.listLRH = listLRH;
        fireTableDataChanged();
    }

    private void saveRecord(LabResultHis record) {
        if (record.getKey() != null) {
            if (record.getKey().getLabResultId() != null) {
                try {
                    if (record.getResult() != null) {
                        record.setResult(record.getResult().trim());
                    }
                    if (record.getRemark() != null) {
                        record.setRemark(record.getRemark().trim());
                    }
                    dao.save(record);
                } catch (Exception ex) {
                    log.error("saveRecord : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }
            }
        }
    }
    
    public void showCommentDialog(int row){
        LabResultHis record = listLRH.get(row);
        JavaFXHTMLEditor.showEditor(record.getResultText(), record, "LR");
    }
}
