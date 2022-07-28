/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author WSwe
 */
public interface AbstractDataAccess {
    public void open() throws Exception;
    public void close();
    public void save(Object o) throws Exception;
    public void save1(Object o) throws Exception;
    public Object find(Class type, Serializable id) throws Exception;
    public Object find(String entityName, String filter) throws Exception;
    public List findAll(String entityName) throws Exception;
    public List findAll(String entityName, Object obj) throws Exception;
    public List findAll(String entityName, String filter) throws Exception;
    public List findAllHSQL(String strHSQL) throws Exception;
    public List findAllSQLQuery(String strSQL);
    public Object getMax(String strField, String strTable, String strFilter) throws Exception;
    public void flush();
    public ResultSet getPro(String procName, List parameters) throws Exception;
    public ResultSet getPro(String procName, String... parameters) throws Exception;
    public void execProc(String procName, String... parameters);
    public Connection getConnection();
    public void closeStatment();
    public ResultSet execSQL(String strSQL);
    public ResultSet execSQL(String strSQL, List param);
    public void execSql(String... strSQLs);
    public void execSqlT(String... strSQLs) throws Exception;
    public void executeUpdateHSQL(String strHSQL, Object... paremeters) throws Exception;
    public void commit();
    public void rollBack();
    public void beginTran();
    public void delete(Object o) throws Exception;
    public void deleteSQL(String strSQL) throws Exception;
    public Statement getStatement();
    public void saveBatch(List list) throws Exception;
    public Session getHibSession();
    public void execSql(List<String> listSQL);
    public void update(Object obj) throws Exception;
    public long getRowCount(String strSql);
    public void deleteSQLNoTran(String strSQL) throws Exception;
    public void evict(Object obj);
}
