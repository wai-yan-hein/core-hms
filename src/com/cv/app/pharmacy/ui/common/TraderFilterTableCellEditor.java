/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.ui.util.TraderAutoCompleter;
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
public class TraderFilterTableCellEditor extends AbstractCellEditor implements TableCellEditor{
    
    static Logger log = Logger.getLogger(TraderFilterTableCellEditor.class.getName());
    private JComponent component = null;
    private AbstractDataAccess dao;
    private TraderAutoCompleter completer;
    
    public TraderFilterTableCellEditor(AbstractDataAccess dao){
        this.dao = dao;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        List<Trader> listTrader = null;
        
        try{
            listTrader = dao.findAll("Trader", "active = true");
        }catch(Exception ex){
            log.error("getTableCellEditorComponent : " + ex.getMessage());
        }
        
        component = jtf;
        completer = new TraderAutoCompleter(jtf, listTrader, this);
        
        return component;
    }
    
    @Override
    public Object getCellEditorValue() {
        Object obj = null;
        Trader trader = completer.getSelTrader();
        
        if(trader != null){
            obj = trader.getTraderId();
        }else{
            obj = ((JTextField)component).getText();
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
