/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.util;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.entity.SysProperty;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.richclient.application.Application;

/**
 *
 * @author WSwe
 */
public class Util1 {

    static Logger log = Logger.getLogger(Util1.class.getName());

    public static String getComputerName() {
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            log.info("getComputerName : " + e.toString());
        }

        return computerName;
    }

    public static String getIPAddress() {
        String iPAddress = "";

        try {
            iPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.info("getIPAddress : " + e.toString());
        }

        return iPAddress;
    }

    public static Frame getParent() {
        return Application.instance().getActiveWindow().getControl();
    }

    public static String nullToBlankStr(String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }

    public static String getAppWorkFolder() {
        return System.getProperty("user.dir");
    }

    public static String isNull(String strValue, String value) {
        if (strValue == null) {
            return value;
        } else if (strValue.isEmpty() || strValue.equals("")) {
            return value;
        } else {
            return strValue;
        }
    }

    public static Dimension getScreenSize() {
        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();

        return screen;
    }

    public static boolean hashPrivilege(String key) {
        boolean status = false;

        if (Global.hashPrivilege.containsKey(key)) {
            status = Global.hashPrivilege.get(key);
        } else {
            log.info("hashPrivilege : Invalid key : " + key);
        }

        return status;
    }

    public static String getString(String str1, String str2) {
        if (str1 == null) {
            return str2;
        } else if (str1.trim().isEmpty()) {
            return str2;
        } else {
            return str1;
        }
    }

    public static String getString(Object str1, String str2) {
        if (str1 == null) {
            return str2;
        } else {
            return getString(str1.toString(), str2);
        }
    }

    public static void loadSystemProperties() {
        Properties systemProperties = new Properties();
        //InputStream in = ReportUtil.class.getResourceAsStream("/report.properties");
        String strAppFolder = Util1.getAppWorkFolder();
        InputStream in;

        try {
            in = new FileInputStream(strAppFolder + "/system.properties");
            systemProperties.load(in);
            Global.systemProperties = systemProperties;
            in.close();
        } catch (IOException ex) {
            log.info("loadSystemProperties : " + ex.toString());
        }
    }

    public static void loadSystemProperties(List<SysProperty> listProperties) {
        Properties properties = new Properties();

        for (SysProperty prop : listProperties) {
            properties.put(prop.getPropDesp(), getNullTo(prop.getPropValue(), ""));
        }

        Global.systemProperties = properties;
    }

    public static String getPropValue(String key) {
        String value = "";

        if (Global.systemProperties.containsKey(key)) {
            value = Global.systemProperties.getProperty(key);
        }
        return value;
    }

    public static Object getDefaultValue(String key) {
        Object status = null;

        if (Global.defaultValue.containsKey(key)) {
            status = Global.defaultValue.get(key);
        } else {
            log.info("getDefaultValue : Invalid key " + key);
        }

        return status;
    }

    public static Cursor getCursor(int curs) {
        Cursor cursor = Cursor.getPredefinedCursor(curs);
        return cursor;
    }

    public static Cursor getWaitCursor() {
        return getCursor(Cursor.WAIT_CURSOR);
    }

    public static Cursor getDefaultCursor() {
        return getCursor(Cursor.CUSTOM_CURSOR);
    }

    public static String getNullTo(String value, String value1) {
        if (value == null) {
            return value1;
        } else if (value.isEmpty()) {
            return value1;
        } else {
            return value;
        }
    }

    public static boolean getNullTo(Boolean value) {
        if (value == null) {
            return false;
        } else {
            return value;
        }
    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }


    /*public static String execHttpPost(String url, List<BasicNameValuePair> parms) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(parms));
            response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            if(response != null){
                try{
                    response.close();
                }catch(Exception ex){
                    
                }
            }
            
            try{
                httpclient.close();
            }catch(Exception ex){
                
            }
        }
        
        return null;
    }*/
}
