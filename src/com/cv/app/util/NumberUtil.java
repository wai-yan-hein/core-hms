/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author WSwe
 */
public class NumberUtil {

    public static Long NZeroL(Object number) {
        try {
            if (number == null) {
                return 0l;
            } else {
                return Long.parseLong(number.toString());
            }
        } catch (Exception ex) {
            System.out.println("NumberUtil.NZero : " + ex.getMessage());
            return 0l;
        }
    }

    public static Double NZero(Object number) {
        try {
            if (number == null) {
                return 0d;
            } else {
                return Double.parseDouble(number.toString().replace(",", ""));
            }
        } catch (Exception ex) {
            System.out.println("NumberUtil.NZero : " + ex.getMessage());
            return 0d;
        }
    }

    public static Float FloatZero(Object number) {
        try {
            if (number == null) {
                return 0f;
            } else {
                return Float.parseFloat(number.toString().replace(",", ""));
            }
        } catch (NumberFormatException ex) {
            System.out.println("NumberUtil.NZero : " + ex.getMessage());
            return 0f;
        }
    }

    public static DefaultFormatterFactory getDecimalFormat() {
        return new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#,##0.00")));
    }

    public static DefaultFormatterFactory getDecimalFormat1() {
        return new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#,##0.##")));
    }

    public static Integer NZeroInt(Object number) {
        Integer value = 0;

        try {
            value = Integer.parseInt(number.toString());
        } catch (Exception ex) {
        }

        return value;
    }

    public static Float NZeroFloat(Object number) {
        Float value = 0f;

        try {
            value = Float.parseFloat(number.toString());
        } catch (Exception ex) {
        }

        return value;
    }

    public static Double getDouble(Object obj) {
        Double value;

        if (obj != null) {
            value = getDouble(obj.toString());
        } else {
            value = null;
        }

        return value;
    }

    public static Double getDouble(String number) {
        double value = 0.0;

        try {
            value = Double.parseDouble(number.replace(",", ""));
        } catch (Exception ex) {

        }
        return value;
    }

    public static boolean isNumber(Object obj) {
        boolean status;

        if (obj == null) {
            status = true;
        } else {
            status = isNumber(obj.toString());
        }

        return status;
    }

    public static boolean isNumber(String number) {
        boolean status = false;

        try {
            if (number != null && !number.isEmpty()) {
                double tmp = Double.parseDouble(number);
                status = true;
            } else {
                status = true;
            }
        } catch (Exception ex) {
            //System.out.println("NumberUtil.isNumber : " + ex.getMessage());
        }
        return status;
    }

    public static Integer getNumber(String strText) {
        Integer tmpInt = null;
        String strN = "";

        try {
            for (int i = 0; i < strText.length(); i++) {
                String tmpStr = strText.substring(i, i + 1);

                if (isNumber(tmpStr)) {
                    strN = strN + tmpStr;
                }
            }

            tmpInt = Integer.parseInt(strN);
        } catch (Exception ex) {
            System.out.println("NumberUtil.getNumber : " + ex.getMessage());
        }

        return tmpInt;
    }

    public static Double roundTo(Double value, int scale) {
        BigDecimal bd = new BigDecimal(value);

        return bd.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static Float roundToF(Double value, int scale) {
        BigDecimal bd = new BigDecimal(value);

        return bd.setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    public static Object toDataType(Object value, Class toDataType) {
        if (value != null) {
            if (!value.getClass().equals(toDataType)) {
                value = toDataType.cast(value);
            }
        }

        return value;
    }

    public static String toChar(Object value) {
        String tmpStr = null;

        if (value != null) {
            tmpStr = value.toString();
        }

        return tmpStr;
    }

    public static String getEngNumber(String inValue) {
        String strMMNumber = "၀၁၂၃၄၅၆၇၈၉.";
        HashMap<String, String> hm = new HashMap();
        int length = inValue.length();
        String outValue = "";

        hm.put("၀", "0");
        hm.put("၁", "1");
        hm.put("၂", "2");
        hm.put("၃", "3");
        hm.put("၄", "4");
        hm.put("၅", "5");
        hm.put("၆", "6");
        hm.put("၇", "7");
        hm.put("၈", "8");
        hm.put("၉", "9");
        hm.put(".", ".");
        
        for (int i = 0; i < length; i++) {
            String tmpNum = inValue.substring(i, i + 1);

            if (strMMNumber.contains(tmpNum)) {
                outValue = outValue + hm.get(tmpNum);
            } else {
                outValue = outValue + tmpNum;
            }
        }

        return outValue;
    }
    
    public static double roundDouble(double value, int place){
        if(place < 0){
            return 0;
        }else{
            BigDecimal bd = new BigDecimal(Double.toString(value));
            bd = bd.setScale(place, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
    
    public static float roundFloat(double value, int place){
        if(place < 0){
            return 0;
        }else{
            BigDecimal bd = new BigDecimal(Double.toString(value));
            bd = bd.setScale(place, RoundingMode.HALF_UP);
            return bd.floatValue();
        }
    }
}
