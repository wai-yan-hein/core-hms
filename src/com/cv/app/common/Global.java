/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.view.VMedicine1;
import java.awt.Font;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author WSwe
 */
public class Global {

    public static Appuser loginUser;
    public static String dateFormat;
    public static String machineId;
    public static String machineName;
    public static int sessionId;
    public static String sessionName;
    public static HashMap<String, Boolean> hashPrivilege;
    public static HashMap<String, Object> defaultValue;
    public static Properties systemProperties;
    public static Font lableFont;
    public static Font textFont;
    public static Font menuFont = new java.awt.Font("Zawgyi-One", 0, 16);
    public static Font sysFont = new java.awt.Font("Zawgyi-One", 1, 14);
    public static ServerSocket sock;
    public static ActiveMQConnection mqConnection;
    public static String programId = "INVENTORY";
    public static String queueName = "INVENTORY";
    public static String loginDate;
    public static Location loginLocation;
    //public static List<Medicine> listItem;
    public static List<VMedicine1> listItem;
    public static AbstractDataAccess dao;
    //public static Date lockDate;
}
