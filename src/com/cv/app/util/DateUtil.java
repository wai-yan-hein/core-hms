/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.util;

import com.cv.app.common.Global;
import datechooser.beans.DateChooserDialog;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.richclient.application.Application;

/**
 *
 * @author WSwe
 */
public class DateUtil {

    static private LocalTime startTime;

    public static Date getDateDialog() {
        Date selectedDate = null;
        DateChooserDialog dateChooserDialog = new DateChooserDialog();
        dateChooserDialog.showDialog(Application.instance().getActiveWindow().getControl(), true);

        if (dateChooserDialog.isSelected()) {
            selectedDate = dateChooserDialog.getSelectedDate().getTime();
        }

        return selectedDate;
    }

    public static String getDateDialogStr() {
        SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);
        Date date = getDateDialog();

        if (date != null) {
            return formatter.format(date);
        } else {
            return null;
        }
    }

    public static String getDateDialogStr(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = getDateDialog();

        if (date != null) {
            return formatter.format(date);
        } else {
            return null;
        }
    }

    public static String getTodayDateStr() {
        SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);

        return formatter.format(Calendar.getInstance().getTime());
    }

    public static String getTodayDateStr(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        return formatter.format(Calendar.getInstance().getTime());
    }

    public static String getTodayDateStrMYSQL() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(Calendar.getInstance().getTime());
        } catch (Exception ex) {
            //System.out.println("toDateStrMYSQL Error : " + ex.getMessage());
        }

        return date;
    }

    public static String getTodayDateTimeStrMYSQL() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(Calendar.getInstance().getTime());
        } catch (Exception ex) {
            //System.out.println("getTodayDateTimeStrMYSQL Error : " + ex.getMessage());
        }

        return date;
    }

    public static String getTodayTimeStrMYSQL() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(Calendar.getInstance().getTime());
        } catch (Exception ex) {
            //System.out.println("getTodayDateTimeStrMYSQL Error : " + ex.getMessage());
        }

        return date;
    }

    public static String getTimeStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String strTime = null;

        try {
            strTime = formatter.format(date);
        } catch (Exception ex) {

        }

        return strTime;
    }

    public static Date toDate(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);
        Date date = null;
        try {
            if (objDate != null) {
                date = formatter.parse(objDate.toString());
            }
        } catch (ParseException ex) {
            //System.out.println("toDateStr : " + ex.getMessage());
        }

        return date;
    }

    public static Date toDate(String strDate, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        Date date = null;

        try {
            date = formatter.parse(strDate);
        } catch (ParseException ex) {
            //System.out.println("toDateStr : " + ex.getMessage());
        }

        return date;
    }

    public static Date toDate(Object strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;

        try {
            date = formatter.parse(strDate.toString());
        } catch (Exception ex) {
            //System.out.println("toDateStr : " + ex.getMessage());
        }

        return date;
    }

    public static Date toDateTime(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String strDate = objDate.toString() + " " + now.getHour() + ":"
                + now.getMinute() + ":" + now.getSecond();
        Date date = null;
        try {
            date = formatter.parse(strDate);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    public static boolean isValidDate(Object objDate) {
        boolean status = true;

        try {
            if (objDate != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);
                formatter.setLenient(false);
                Date date = formatter.parse(objDate.toString());
                System.out.println("Date : " + date);
            }
        } catch (ParseException ex) {
            status = false;
        }

        return status;
    }

    public static String getPeriod(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
        String strPeriod = null;
        Date date = toDate(strDate);

        if (date != null) {
            strPeriod = formatter.format(date);
        }

        return strPeriod;
    }

    public static String getPeriodY(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String strPeriod = null;
        Date date = toDate(strDate);

        if (date != null) {
            strPeriod = formatter.format(date);
        }

        return strPeriod;
    }

    public static String toDateStr(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String date = null;

        try {
            date = formatter.format(toDate(strDate));
        } catch (Exception ex) {
            //System.out.println("toDateStr : " + ex.getMessage());
        }

        return date;
    }

    public static String toDateStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String strDate = null;

        try {
            strDate = formatter.format(date);
        } catch (Exception ex) {
            System.out.println("toDateStr : " + ex.getMessage());
        }
        return strDate;
    }

    public static String toDateStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);
        String strDate = null;

        try {
            strDate = formatter.format(date);
        } catch (Exception ex) {
            //System.out.println("toDateStr : " + ex.getMessage());
        }

        return strDate;
    }

    public static String toDateTimeStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(toDate(strDate));
        } catch (Exception ex) {
            //System.out.println("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String toDateTimeStrMYSQL(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = null;

        try {
            strDate = formatter.format(date);
        } catch (Exception ex) {
            //System.out.println("toDateTimeStrMYSQL : " + ex.toString());
        }

        return strDate;
    }

    public static String toDateStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate));
        } catch (Exception ex) {
            //System.out.println("toDateStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String toDateStrMYSQLEnd(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate)) + " 23:59:59";
        } catch (Exception ex) {
            //System.out.println("toDateStrMYSQL Error : " + ex.getMessage());
        }

        return date;
    }

    public static java.sql.Date getSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static String getTodayDateTimeStr() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static Date getTodayDateTime() {
        return new Date();
    }

    public static String getAge(String dob) {
        String strAge = null;
        Date birthDate = toDate(dob);

        try {
            if (birthDate != null) {
                Date todayDate = toDate(getTodayDateStr());
                int dobYear = birthDate.getYear();
                int dobMonth = birthDate.getMonth();
                int dobDay = birthDate.getDate();
                int currYear = todayDate.getYear();
                int currMonth = todayDate.getMonth();
                int currDay = todayDate.getDate();
                int year = currYear - dobYear;
                int month = (currMonth + 1) - dobMonth;
                int day = currDay - dobDay;
                int[] dayInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

                if (dobMonth < currMonth && dobDay > currDay) {
                    month = month - 1;
                    int tempDyMth = dayInMonth[dobMonth];
                    day = tempDyMth + day;
                }

                if (day < 0) {
                    day = getDaysInMonth(dobMonth, dobDay, currDay);
                    month = Math.abs(month);
                    month = (12 - month) - 1;
                    year = year - 1;
                }

                if (month < 0) {
                    month = Math.abs(month);
                    month = 12 - month;
                    year = year - 1;
                }

                strAge = year + "y," + month + "m," + day + "d";
            }
        } catch (Exception ex) {
        }
        return strAge;
    }

    private static int getDaysInMonth(int usrMth, int usrDy, int tdDay) {
        int usrMonth, usrDay, daysInMth, day, outDay = 0;
        int[] dayInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        usrMonth = usrMth - 1;
        usrDay = usrDy;
        day = tdDay;

        try {
            daysInMth = dayInMonth[usrMonth];
            outDay = daysInMth - usrDay;
            outDay = outDay + day;
        } catch (Exception ex) {
        }
        return outDay;
    }

    public static String toDateTimeStr(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.format(date);
    }

    public static boolean isValidSession(String stTime,
            String endTime) {
        int stHr = Integer.parseInt(stTime);
        int enHr = Integer.parseInt(endTime);
        int curHr = getHour();
        return curHr >= stHr && curHr < enHr;
    }

    public static int getHour() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH");
        LocalDateTime now = LocalDateTime.now();
        return Integer.parseInt(dtf.format(now));
    }

    public static String addTodayDateTo(int day) {
        String output = null;
        SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(new Date()); // Now use today date.
            c.add(Calendar.DATE, day);
            output = formatter.format(c.getTime());
        } catch (Exception ex) {
            //System.out.println("DateUtil.addTodayDateTo : " + ex.toString());
        }

        return output;
    }

    public static String getDOB(int year, int month, int day) {
        String output = null;
        SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(new Date()); // Now use today date.
            c.add(Calendar.YEAR, year);

            //check month empty
            if (month == 0) {

                c.add(Calendar.MONTH, -month);
            } else {
                c.add(Calendar.MONTH, month);

            }
            //check day empty
            if (day == 0) {
                c.add(Calendar.DAY_OF_YEAR, -day);
            } else {
                c.add(Calendar.DAY_OF_YEAR, day);

            }
            output = formatter.format(c.getTime());
        } catch (Exception ex) {

        }

        return output;
    }

    public static String getDOB1(int year, int month, int day) {
        String output = null;

        try {
            Date currDate = new Date();
            int ttlDayInYear = year * 365;
            int ttlDayInMonth = month * 30;
            int totalDays = (ttlDayInYear + ttlDayInMonth + day) * -1;

            output = subDateTo(currDate, totalDays);
        } catch (Exception ex) {

        }
        return output;
    }

    public static String subDateTo(Date date, int day) {
        String output = null;
        SimpleDateFormat formatter = new SimpleDateFormat(Global.dateFormat);
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(date); // Now use today date.
            c.add(Calendar.DATE, day);
            output = formatter.format(c.getTime());
        } catch (Exception ex) {
            //System.out.println("DateUtil.addTodayDateTo : " + ex.toString());
        }

        return output;
    }

    private static int[] getDateDifference(Date date1, Date date2) {
        int[] monthDay = {31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        Calendar fromDate;
        Calendar toDate;
        int increment = 0;
        int[] ageDiffArr = new int[3];

        int year;
        int month;
        int day;

        Calendar d1 = new GregorianCalendar().getInstance();
        d1.setTime(date1);

        Calendar d2 = new GregorianCalendar().getInstance();
        d2.setTime(date2);

        if (d1.getTime().getTime() > d2.getTime().getTime()) {
            fromDate = d2;
            toDate = d1;
        } else {
            fromDate = d1;
            toDate = d2;
        }

        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment = monthDay[fromDate.get(Calendar.MONTH)];
        }

        GregorianCalendar cal = new GregorianCalendar();
        boolean isLeapYear = cal.isLeapYear(fromDate.get(Calendar.YEAR));

        if (increment == -1) {
            if (isLeapYear) {
                increment = 29;
            } else {
                increment = 28;
            }
        }

        // DAY CALCULATION
        if (increment != 0) {
            day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
            increment = 1;
        } else {
            day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
        }

        // MONTH CALCULATION
        if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
            month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 0;
        }

        // YEAR CALCULATION
        year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);

        ageDiffArr[0] = day;
        ageDiffArr[1] = month;
        ageDiffArr[2] = year;

        return ageDiffArr;      // RESULT AS DAY, MONTH AND YEAR in form of Array
    }

    public static String getAgeStr(Date from, Date to) {
        int[] tmp = getDateDifference(from, to);
        String strTmp = "";

        if (tmp[2] != 0) {
            if (strTmp.length() == 0) {
                strTmp = tmp[2] + "y";
            } else {
                strTmp = strTmp + "," + tmp[2] + "y";
            }
        }

        if (tmp[1] != 0) {
            if (strTmp.length() == 0) {
                strTmp = tmp[1] + "m";
            } else {
                strTmp = strTmp + "," + tmp[1] + "m";
            }
        }

        if (tmp[0] != 0) {
            if (strTmp.length() == 0) {
                strTmp = tmp[0] + "y";
            } else {
                strTmp = strTmp + "," + tmp[0] + "y";
            }
        }

        return strTmp;
    }

    public static int getTotalYear(Date from, Date to) {
        int[] tmp = getDateDifference(from, to);

        return tmp[2];
    }

    public static int getTotalMonth(Date from, Date to) {
        int[] tmp = getDateDifference(from, to);
        int ttlMonth = tmp[1] + (tmp[2] * 12);

        return ttlMonth;
    }

    public static int getTotalDays(Date from, Date to) {
        long diff = to.getTime() - from.getTime();
        int daysDiff = (int) Math.ceil(diff / (float) (24 * 3600 * 1000));

        return daysDiff;
    }

    public static Date addTodayDateTo(int field, int value) {
        Date output = null;
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(new Date()); // Now use today date.
            c.add(field, value);
            output = c.getTime();
        } catch (Exception ex) {
            //System.out.println("DateUtil.addTodayDateTo date : " + ex.toString());
        }

        return output;
    }

    public static int getDatePart(Date d, String format) {
        int intValue = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String value = sdf.format(d);

            if (!value.isEmpty()) {
                intValue = Integer.parseInt(value);
            }
        } catch (Exception ex) {

        }

        return intValue;
    }

    public static boolean isSameDate(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d1).equals(sdf.format(d2));
    }

    public static void setStartTime() {
        startTime = LocalTime.now();
    }

    public static long getDuration() {
        if (startTime == null) {
            return -1;
        } else {
            LocalTime endTime = LocalTime.now();
            Duration diff = Duration.between(startTime, endTime);
            return diff.getSeconds();
        }
    }

    public static String getYesterdayateStrMYSQL() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            date = formatter.format(cal.getTime());
        } catch (Exception ex) {
            //System.out.println("toDateStrMYSQL Error : " + ex.getMessage());
        }

        return date;
    }
}
