/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StartWithRowFilter extends RowFilter<Object, Object> {
    static Logger log = Logger.getLogger(StartWithRowFilter.class.getName());
    private final JTextField jtf;
    
    public StartWithRowFilter(JTextField jtf){
        this.jtf = jtf;
    }
    
    @Override
    public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
        for (int i = 0; i < entry.getValueCount(); i++) {
            //String tmp = entry.getStringValue(i);
            //log.info("filter : Entry - " + tmp.toUpperCase() + "  type : " + jtf.getText().toUpperCase());
            if(entry.getStringValue(i) != null){
                if (entry.getStringValue(i).toUpperCase().startsWith(
                        jtf.getText().toUpperCase())) {
                    return true;
                }
            }
        }

        return false;
    }
}
