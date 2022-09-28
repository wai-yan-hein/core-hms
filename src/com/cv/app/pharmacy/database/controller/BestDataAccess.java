/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.controller;

import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.*;

/**
 *
 * @author WSwe
 */
public class BestDataAccess implements AbstractDataAccess {

    static Logger log = Logger.getLogger(BestDataAccess.class.getName());
    private Transaction tran;
    private Session session;
    private Statement statement = null;

    @Override
    public void open() throws Exception {
        close();
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
        } catch (Exception ex) {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
        }
    }

    @Override
    public void close() {
        /*try {
         session.flush();
         }catch(Exception exf){
            
         }
         try {
         session.clear();
         }catch(Exception exf){
            
         }*/
        try {
            session.close();
        } catch (Exception ex) {
            //log.error("close : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    @Override
    public void evict(Object obj) {
        session.evict(obj);
    }

    @Override
    public void flush() {
        session.flush();
    }

    @Override
    public void save(Object o) throws Exception {
        open();
        tran = session.beginTransaction();

        try {
            session.saveOrUpdate(o);
        } catch (NonUniqueObjectException ex) {
            session.merge(o);
        }
        session.flush();
        tran.commit();
        close();
    }

    @Override
    public void save1(Object o) throws Exception {
        //open();
        //tran = session.beginTransaction();

        try {
            session.saveOrUpdate(o);
        } catch (NonUniqueObjectException ex) {
            session.merge(o);
        }
        //session.flush();
        //tran.commit();
        //close();
    }

    @Override
    public Object find(Class type, Serializable id) throws Exception{
        Object obj = null;

        //try {
            if (!id.equals("")) {
                open();
                tran = session.beginTransaction();
                obj = session.get(type, id);
                //tran.commit();
            }
        /*} catch (Exception ex) {
            log.error("find1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }*/

        return obj;
    }

    @Override
    public Object find(String entityName, String filter) throws Exception{
        Object obj = null;
        String strSQL = "select distinct v from " + entityName + " v where "
                + filter;
        //try {
            open();
            tran = session.beginTransaction();
            Query query = session.createQuery(strSQL);
            List list = query.list();

            if (!list.isEmpty()) {
                obj = list.get(0);
            }
        /*} catch (Exception ex) {
            log.error("find2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }*/

        return obj;
    }

    @Override
    public List findAll(String entityName) throws Exception{
        List lists = null;

        //try {
            open();
            tran = session.beginTransaction();
            Query query = session.createQuery("from " + entityName);
            lists = query.list();
            //tran.commit();
        /*} catch (Exception ex) {
            log.error("findAll1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }*/

        return lists;
    }

    @Override
    public List findAll(String entityName, Object obj) throws Exception{
        List list = findAll(entityName);
        list.add(0, obj);

        return list;
    }

    @Override
    public List findAll(String entityName, String filter) throws Exception{
        List lists = null;
        String strSQL = "select distinct v from " + entityName + " v where "
                + filter;

        try {
            open();
            tran = session.beginTransaction();
            Query query = session.createQuery(strSQL);
            lists = query.list();
        } catch (Exception ex) {
            log.error("findAll2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return lists;
    }

    @Override
    public List findAllHSQL(String strHSQL) throws Exception{
        List lists = null;

        //try {
            open();
            tran = session.beginTransaction();
            Query query = session.createQuery(strHSQL);
            lists = query.list();
            //tran.commit();
        //} catch (Exception ex) {
        //    log.error("findAllHSQL1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        //}

        return lists;
    }

    @Override
    public List findAllSQLQuery(String strSQL) {
        List lists = null;

        try {
            open();
            tran = session.beginTransaction();
            SQLQuery query = session.createSQLQuery(strSQL);
            lists = query.list();
            //tran.commit();
        } catch (Exception ex) {
            log.error("findAllSQLQuery1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return lists;
    }

    @Override
    public Object getMax(String strField, String strTable, String strFilter) throws Exception {
        Object obj = null;
        String strSQL = "select max(" + strField + ") from " + strTable;

        if (strFilter != null) {
            strSQL = strSQL + " where " + strFilter;
        }

        //try {
        open();
        tran = session.beginTransaction();
        SQLQuery query = session.createSQLQuery(strSQL);
        obj = query.uniqueResult();
        //tran.commit();
        //} catch (Exception ex) {
        //    log.error("getMax : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        //}

        return obj;
    }

    @Override
    public ResultSet getPro(String procName, List parameters) throws Exception{
        String strSQL = "{call " + procName + "(";
        ResultSet resultSet = null;

        //try {
            String tmpStr = "";
            for (Object obj : parameters) {
                if (tmpStr.isEmpty()) {
                    tmpStr = "?";
                } else {
                    tmpStr = tmpStr + ",?";
                }
            }

            strSQL = strSQL + tmpStr + ")}";
            open();
            tran = session.beginTransaction();
            statement = session.connection().prepareCall(strSQL);

            int i = 1;
            for (Object obj : parameters) {
                if (obj instanceof Date) {
                    ((CallableStatement) statement).setString(i, DateUtil.toDateTimeStrMYSQL(obj.toString()));
                } else {
                    ((CallableStatement) statement).setString(i, obj.toString());
                }

                i++;
            }

            resultSet = ((CallableStatement) statement).executeQuery();
            tran.commit();
        /*} catch (Exception ex) {
            log.error("getPro1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }*/

        return resultSet;
    }

    @Override
    public ResultSet getPro(String procName, String... parameters) throws Exception {
        String strSQL = "{call " + procName + "(";
        ResultSet resultSet = null;

        //try {
            String tmpStr = "";
            for (int i = 0; i < parameters.length; i++) {
                if (tmpStr.isEmpty()) {
                    tmpStr = "?";
                } else {
                    tmpStr = tmpStr + ",?";
                }
            }

            strSQL = strSQL + tmpStr + ")}";
            open();
            tran = session.beginTransaction();
            statement = session.connection().prepareCall(strSQL);

            int i = 1;
            for (Object obj : parameters) {
                if (obj instanceof Date) {
                    ((CallableStatement) statement).setString(i, DateUtil.toDateTimeStrMYSQL(obj.toString()));
                } else {
                    ((CallableStatement) statement).setString(i, obj.toString());
                }

                i++;
            }
            resultSet = ((CallableStatement) statement).executeQuery();
            tran.commit();
        /*} catch (Exception ex) {
            log.error("getPro2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }*/

        return resultSet;
    }

    @Override
    public void execProc(String procName, String... parameters) {
        String strSQL = "{call " + procName + "(";

        try {
            String tmpStr = "";
            for (int i = 0; i < parameters.length; i++) {
                if (tmpStr.isEmpty()) {
                    tmpStr = "?";
                } else {
                    tmpStr = tmpStr + ",?";
                }
            }

            strSQL = strSQL + tmpStr + ")}";
            open();
            tran = session.beginTransaction();
            statement = session.connection().prepareCall(strSQL);

            int i = 1;
            for (Object obj : parameters) {
                if (obj instanceof Date) {
                    ((CallableStatement) statement).setString(i, DateUtil.toDateTimeStrMYSQL(obj.toString()));
                } else {
                    if (obj == null) {
                        ((CallableStatement) statement).setObject(i, obj);
                    } else {
                        ((CallableStatement) statement).setString(i, obj.toString());
                    }
                }

                i++;
            }

            ((CallableStatement) statement).execute();
            //tran.commit();
        } catch (Exception ex) {
            log.error("execProc : " + procName + " : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        try {
            open();
            tran = session.beginTransaction();
        } catch (Exception ex) {
            log.error("getConnection : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return session.connection();
    }

    @Override
    public void closeStatment() {
        try {
            statement.close();
        } catch (Exception ex) {
            //log.error(ex.toString());
        }
    }

    @Override
    public ResultSet execSQL(String strSQL) {
        ResultSet resultSet = null;

        try {
            open();
            tran = session.beginTransaction();
            statement = session.connection().prepareStatement(strSQL);
            resultSet = ((PreparedStatement) statement).executeQuery();
            tran.commit();
        } catch (Exception ex) {
            log.error("execSQL1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return resultSet;
    }

    @Override
    public ResultSet execSQL(String strSQL, List param) {
        ResultSet resultSet = null;

        try {
            open();
            tran = session.beginTransaction();
            statement = session.connection().prepareCall(strSQL);
            int i = 1;
            for (Object obj : param) {
                if (obj instanceof Date) {
                    ((PreparedStatement) statement).setDate(i,
                            DateUtil.getSqlDate(DateUtil.toDate(obj.toString())));
                } else if (obj instanceof Integer) {
                    ((PreparedStatement) statement).setInt(i, NumberUtil.NZeroInt(obj));
                } else if (obj instanceof Double) {
                    ((PreparedStatement) statement).setDouble(i, NumberUtil.NZero(obj));
                } else if (obj instanceof Long) {
                    ((PreparedStatement) statement).setLong(i, NumberUtil.NZeroInt(obj));
                } else if (obj instanceof String) {
                    ((PreparedStatement) statement).setString(i, obj.toString());
                }

                i++;
            }

            resultSet = ((PreparedStatement) statement).executeQuery();
            tran.commit();
        } catch (Exception ex) {
            log.error("execSQL2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return resultSet;
    }

    @Override
    public void execSql(String... strSQLs) {
        try {
            open();
            beginTran();
            Connection con = session.connection();
            statement = con.createStatement();
            con.setAutoCommit(false);

            for (String strSQL : strSQLs) {
                statement.addBatch(strSQL);
            }
            statement.executeBatch();
            con.commit();
            //con.setAutoCommit(true);
            statement.clearBatch();
        } catch (Exception ex) {
            log.error("execSql1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public void execSqlT(String... strSQLs) throws Exception {
        Connection con = session.connection();
        statement = con.createStatement();
        con.setAutoCommit(false);

        for (String strSQL : strSQLs) {
            statement.addBatch(strSQL);
        }
        statement.executeBatch();
        statement.clearBatch();
    }

    @Override
    public void execSql(List<String> listSQL) {
        try {
            open();
            beginTran();
            Connection con = session.connection();
            statement = con.createStatement();
            con.setAutoCommit(false);

            for (String strSQL : listSQL) {
                statement.addBatch(strSQL);
            }
            statement.executeBatch();
            con.commit();
            con.setAutoCommit(true);
            statement.clearBatch();
        } catch (Exception ex) {
            log.error("execSql2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        } finally {
            close();
        }
    }

    @Override
    public void executeUpdateHSQL(String strHSQL, Object... paremeters) throws Exception {
        Query query = session.createQuery(strHSQL);
        int i = 0;

        for (Object obj : paremeters) {
            if (obj instanceof String) {
                query.setString(i, obj.toString());
            } else if (obj instanceof Integer) {
                query.setInteger(i, NumberUtil.NZeroInt(obj));
            } else if (obj instanceof Long) {
                query.setLong(i, Long.parseLong(obj.toString()));
            } else if (obj instanceof Float) {
                query.setFloat(i, Float.parseFloat(obj.toString()));
            } else if (obj instanceof Double) {
                query.setDouble(i, Double.parseDouble(obj.toString()));
            } else if (obj instanceof Date) {
                query.setString(i, DateUtil.toDateTimeStrMYSQL(obj.toString()));
            }

            i++;
        }

        query.executeUpdate();
    }

    @Override
    public void commit() {
        tran.commit();
    }

    @Override
    public void beginTran() {
        tran = session.beginTransaction();
    }

    @Override
    public void rollBack() {
        if (tran.isActive()) {
            tran.rollback();
        }
    }

    @Override
    public void delete(Object o) throws Exception {
        open();
        beginTran();
        session.delete(o);
        commit();
    }

    /*
     * need to catch exception need to manage rollback and commit
     */
    @Override
    public void deleteSQL(String strSQL) throws Exception {
        open();
        beginTran();
        SQLQuery query = session.createSQLQuery(strSQL);
        query.executeUpdate();
        commit();
    }

    @Override
    public void deleteSQLNoTran(String strSQL) throws Exception {
        SQLQuery query = session.createSQLQuery(strSQL);
        query.executeUpdate();
    }

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    public void saveBatch(List list) throws Exception {
        int i = 0;
        open();
        beginTran();

        for (Object obj : list) {
            session.saveOrUpdate(obj);
            i++;

            if (i % 20 == 0) {
                session.flush();
                session.clear();
            }
        }

        commit();
        close();
    }

    @Override
    public Session getHibSession() {
        return session;
    }

    @Override
    public void update(Object obj) throws Exception {
        open();
        tran = session.beginTransaction();
        session.update(obj);
        tran.commit();
    }

    @Override
    public long getRowCount(String strSql) {
        long rowCount = 0;

        try {
            open();
            beginTran();
            Connection con = session.connection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery(strSql);
            rs.next();
            rowCount = rs.getLong(1);
        } catch (Exception ex) {
            log.error("getRowCount : " + ex.getMessage());
        } finally {
            close();
        }

        return rowCount;
    }
}
