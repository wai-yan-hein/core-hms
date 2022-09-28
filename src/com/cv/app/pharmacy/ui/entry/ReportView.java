/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import javax.swing.JComponent;
import org.springframework.richclient.application.support.AbstractView;

/**
 *
 * @author WSwe
 */
public class ReportView extends AbstractView{
    private Report report;
    
    @Override
    protected JComponent createControl() {
        report = new Report();
        return report;
    }
    
    @Override
    public void dispose() {
        //deleteTmpData();
        super.dispose();
    }
    
    /*private void deleteTmpData() {
        String userId = Global.loginUser.getUserId();
        String strSQL1 = "delete * from tmp_item_code_filter where user_id = '"
                + userId + "'";
        String strSQL2 = "delete * from tmp_trader_filter where user_id = '"
                + userId + "'";
        
        AbstractDataAccess dao = new BestDataAccess();
        dao.execSql(strSQL1, strSQL2);
    }*/
}
