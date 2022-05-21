/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.util;

import java.util.ArrayList;
import java.util.List;
import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

/**
 *
 * @author WSwe
 */
public class JoSQLUtil {
    public static List getResult(String strSQL, List listDetail){
        QueryResults qr;
        Query q = new Query();
        List list = new ArrayList();
        
        if(listDetail != null){
          try{
              q.parse(strSQL);
              qr = q.execute(listDetail);
              list = qr.getResults();
          }catch(QueryParseException qpe){
              System.out.println("JoSQLUtil.getResult qpe: " + qpe.toString());
          }catch(QueryExecutionException ex){
              System.out.println("JoSQLUtil.getResult : " + ex.toString());
          }
        }
        
        return list;
    }
    
    public static boolean isAlreadyHave(String strSQL, List listDetail){
        boolean status = false;
        QueryResults qr;
        Query q = new Query();
        
        try{
            q.parse(strSQL);
            qr = q.execute(listDetail);
            
            if(!qr.getResults().isEmpty()){
                status = true;
            }
        }catch(QueryParseException qpe){
            System.out.println("JoSQLUtil.isAlreadyHave qpe: " + qpe.toString());
        }catch(Exception ex){
            System.out.println("JoSQLUtil.isAlreadyHave : " + ex.toString());
        }
        
        return status;
    }
    
    public static Object getSaveValue(List listDetail, String strSql, String fieldName){
        QueryResults qr;
        Query q = new Query();
        Object obj = null;
                
        try{
            q.parse(strSql);
            qr = q.execute(listDetail);
            obj = qr.getSaveValue(fieldName);
        }catch(QueryParseException qpe){
            System.out.println("JoSQLUtil.isAlreadyHave qpe: " + qpe.toString());
        }catch(Exception ex){
            System.out.println("JoSQLUtil.isAlreadyHave : " + ex.toString());
        }
        
        return obj;
    }
}
