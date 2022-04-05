/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.util;

import com.cv.app.inpatient.database.healper.PackageDetailEdit;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author MyintMo
 */
public class POIUtil {

    public enum FormatType {

        TEXT,
        INTEGER,
        FLOAT,
        DOUBLE,
        DATE
    }

    public static void genExcelFile(List<String> listHeader, List<String> listFieldName,
            HashMap<String, FormatType> hmType, ResultSet rs, String fileName, String title,
            String fromDate, String toDate) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        int rowIndex = 0;
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCellStyle my_style = workbook.createCellStyle();
        XSSFFont my_font = workbook.createFont();
        my_font.setFontName("Zawgyi-One");
        my_style.setFont(my_font);

        //SXSSFWorkbook wb = null;
        
        if(!title.equals("-")){
            row.createCell(0, CellType.STRING).setCellValue(title);
            rowIndex++;
        }
        
        if(!fromDate.equals("-")){
            row = sheet.createRow(rowIndex);
            row.createCell(0, CellType.STRING).setCellValue(fromDate + " to " + toDate);
            rowIndex++;
        }
        
        row = sheet.createRow(rowIndex);
        int colIndex = 0;
        for (String header : listHeader) {
            row.createCell(colIndex, CellType.STRING).setCellValue(header);
            colIndex++;
        }

        rowIndex++;
        if (rs != null) {
            while (rs.next()) {
                row = sheet.createRow(rowIndex);
                colIndex = 0;
                for (String field : listFieldName) {
                    if (null != hmType.get(field)) switch (hmType.get(field)) {
                        case TEXT:
                            XSSFCell cell = row.createCell(colIndex);
                            cell.setCellValue(rs.getString(field));
                            cell.setCellStyle(my_style);
                            break;
                        case INTEGER:
                            row.createCell(colIndex).setCellValue(rs.getInt(field));
                            break;
                        case FLOAT:
                            row.createCell(colIndex).setCellValue(rs.getFloat(field));
                            break;
                        case DOUBLE:
                            row.createCell(colIndex).setCellValue(rs.getDouble(field));
                            break;
                        case DATE:
                            row.createCell(colIndex).setCellValue(DateUtil.toDateStr(rs.getDate(field)));
                            break;
                        default:
                            break;
                    }

                    colIndex++;
                }

                rowIndex++;
            }

            File file = new File(fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                fos.flush();
            }
        }
    }

    public static void genExcelFileForControlDrugForm3(List<String> listHeader, List<String> listFieldName,
            HashMap<String, FormatType> hmType, ResultSet rs, String fileName) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        int titleIndex = 0;
        int rowIndex = 0;

        XSSFCellStyle my_style = workbook.createCellStyle();
        XSSFFont my_font = workbook.createFont();
        my_font.setFontName("Zawgyi-One");
        my_style.setFont(my_font);
        String locationName = "";
        String itemName = "";

        XSSFRow titleRow1 = sheet.createRow(0);
        XSSFCell cell1 = titleRow1.createCell(titleIndex);
        cell1.setCellValue("ထိန္းခ်ဳပ္ေဆး၀ါး အမ်ိဳးအစား တစ္ခုုခ်င္းအလိုုက္ ေန့စဥ္ ၀ယ္ယူေရာင္းခ်ျခင္း မွတ္ပံုုတင္စာအုုပ္");
        cell1.setCellStyle(my_style);
        rowIndex++;

        String locationNamePrv = "-";
        String itemNamePrv = "-";
        if (rs != null) {
            while (rs.next()) {
                locationName = rs.getString("location_name");
                itemName = rs.getString("item_name");

                if (!locationNamePrv.equals(locationName)) {
                    XSSFRow titleRow2 = sheet.createRow(rowIndex);
                    XSSFCell cell2 = titleRow2.createCell(titleIndex);
                    cell2.setCellValue("၁။ ခြင့္ျပဳခ်က္ရရွိသူအမည္၊ ေနရပ္လိုုပ္စာ၊ ႏိုုင္ခံသားစီစစ္ေရးကတ္ျပာအမွတ္၊ ေဆးဆိုုင္အမည္"
                            + '(' + locationName + ')' + "၊ ၀ယ္စလီေဆးရံုု၊ တာဟန္း၊ ကေလးျမိဳ့၊စစ္ကိုုင္းတိုုင္းေဒသၾကီး");
                    cell2.setCellStyle(my_style);
                    rowIndex++;
                }

                if (!locationNamePrv.equals(locationName) || !itemNamePrv.equals(itemName)) {
                    XSSFRow titleRow3 = sheet.createRow(rowIndex);
                    XSSFCell cell3 = titleRow3.createCell(titleIndex);
                    cell3.setCellValue("၂၊ ေဆး၀ါးအမည္(Generic and Brand Name)(ရွိလ်င္)ႏွင့္ ပါ၀င္သည့္အေလးခ်ိန္၊ ထုုတ္လုုပ္သည့္ကုမၸဏီ  ၊ စက္ရံုု၊ႏိုုင္ငံအမည္" + itemName);
                    cell3.setCellStyle(my_style);
                    rowIndex++;
                }

                int colIndex = 0;
                if (!locationNamePrv.equals(locationName) || !itemName.equals(itemNamePrv)) {
                    XSSFRow rowHeader = sheet.createRow(rowIndex);
                    for (String header : listHeader) {
                        XSSFCell cell = rowHeader.createCell(colIndex);
                        cell.setCellValue(header);
                        cell.setCellStyle(my_style);
                        colIndex++;
                    }
                    rowIndex++;
                }

                if (!locationNamePrv.equals(locationName)) {
                    locationNamePrv = locationName;
                }

                if (!itemName.equals(itemNamePrv)) {
                    itemNamePrv = itemName;
                }

                XSSFRow row = sheet.createRow(rowIndex);
                colIndex = 0;
                for (String field : listFieldName) {
                    if (hmType.get(field) == FormatType.TEXT) {
                        XSSFCell cell = row.createCell(colIndex);
                        cell.setCellValue(rs.getString(field));
                        cell.setCellStyle(my_style);
                    } else if (hmType.get(field) == FormatType.INTEGER) {
                        row.createCell(colIndex).setCellValue(rs.getInt(field));
                    } else if (hmType.get(field) == FormatType.FLOAT) {
                        row.createCell(colIndex).setCellValue(rs.getFloat(field));
                    } else if (hmType.get(field) == FormatType.DOUBLE) {
                        row.createCell(colIndex).setCellValue(rs.getDouble(field));
                    } else if (hmType.get(field) == FormatType.DATE) {
                        row.createCell(colIndex).setCellValue(DateUtil.toDateStr(rs.getDate(field)));
                    }
                    colIndex++;
                }
                rowIndex++;
            }
        }

        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.flush();
        }
    }

    public static void genExcelFileForMISReport(List<String> listHeader, List<String> listFieldName,
            HashMap<String, FormatType> hmType, ResultSet rs, String fileName, String from, String to) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        int titleIndex = 0;
        int rowIndex = 0;

        XSSFCellStyle my_style = workbook.createCellStyle();
        XSSFFont my_font = workbook.createFont();
        my_font.setFontName("Zawgyi-One");
        my_style.setFont(my_font);
        String groupName = "";

        XSSFRow titleRow1 = sheet.createRow(0);
        XSSFCell cell1 = titleRow1.createCell(titleIndex);
        cell1.setCellValue(from + " to " + to);
        cell1.setCellStyle(my_style);
        rowIndex++;

        int colIndex = 0;
        XSSFRow headerRow = sheet.createRow(rowIndex);
        for (String header : listHeader) {
            headerRow.createCell(colIndex, CellType.STRING).setCellValue(header);
            colIndex++;
        }
        rowIndex++;

        String groupNamePrv = "-";
        int scale = 2;

        if (rs != null) {
            HashMap<String, Double> hmTotal = new HashMap();
            HashMap<String, Double> hmSubTotal = new HashMap();

            while (rs.next()) {
                groupName = rs.getString("desp");
                //Group sub total
                colIndex = 0;
                if (!groupNamePrv.equals(groupName) && !groupNamePrv.equals("-")) {
                    XSSFRow titleRow = sheet.createRow(rowIndex);
                    //colIndex = 0;
                    for (String field : listFieldName) {
                        if (hmType.get(field) == FormatType.TEXT) {
                            XSSFCell cell = titleRow.createCell(colIndex);
                            if (colIndex == 1) {
                                cell.setCellValue("");
                            } else {
                                cell.setCellValue(groupNamePrv + " Total : ");
                            }
                            cell.setCellStyle(my_style);
                        } else {
                            titleRow.createCell(colIndex).setCellValue(NumberUtil.roundTo(hmSubTotal.get(field), scale));
                        }

                        if (colIndex > 1) {
                            if (hmType.get(field) != FormatType.TEXT && hmType.get(field) != FormatType.DATE) {
                                hmSubTotal.put(field, 0.0);
                            }
                        }

                        colIndex++;
                    }
                    rowIndex++;
                }

                if (!groupNamePrv.equals(groupName)) {
                    groupNamePrv = groupName;
                }

                XSSFRow row = sheet.createRow(rowIndex);
                colIndex = 0;
                for (String field : listFieldName) {
                    if (colIndex > 1) {
                        if (!hmTotal.containsKey(field)) {
                            hmTotal.put(field, 0.0);
                            hmSubTotal.put(field, 0.0);
                        }

                        if (hmType.get(field) != FormatType.TEXT && hmType.get(field) != FormatType.DATE) {
                            Double total = hmTotal.get(field);
                            Double subTotal = hmSubTotal.get(field);
                            total += rs.getDouble(field);
                            if (field.equals("ipd_amt")) {
                                System.out.println("ipd_amt");
                            }
                            subTotal += rs.getDouble(field);
                            hmTotal.put(field, total);
                            hmSubTotal.put(field, subTotal);
                        }
                    }

                    if (hmType.get(field) == FormatType.TEXT) {
                        XSSFCell cell = row.createCell(colIndex);
                        cell.setCellValue(rs.getString(field));
                        cell.setCellStyle(my_style);
                    } else if (hmType.get(field) == FormatType.INTEGER) {
                        row.createCell(colIndex).setCellValue(rs.getInt(field));
                    } else if (hmType.get(field) == FormatType.FLOAT) {
                        row.createCell(colIndex).setCellValue(NumberUtil.roundTo(rs.getDouble(field), scale));
                    } else if (hmType.get(field) == FormatType.DOUBLE) {
                        row.createCell(colIndex).setCellValue(NumberUtil.roundTo(rs.getDouble(field), scale));
                    } else if (hmType.get(field) == FormatType.DATE) {
                        row.createCell(colIndex).setCellValue(DateUtil.toDateStr(rs.getDate(field)));
                    }
                    colIndex++;
                }
                rowIndex++;
            }

            XSSFRow groupTitleRow = sheet.createRow(rowIndex);
            colIndex = 0;
            for (String field : listFieldName) {
                if (hmType.get(field) == FormatType.TEXT) {
                    XSSFCell cell = groupTitleRow.createCell(colIndex);
                    if (colIndex == 1) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(groupNamePrv + " Total : ");
                    }
                    cell.setCellStyle(my_style);
                } else {
                    groupTitleRow.createCell(colIndex).setCellValue(NumberUtil.roundTo(hmSubTotal.get(field), scale));
                }

                if (colIndex > 1) {
                    if (hmType.get(field) != FormatType.TEXT && hmType.get(field) != FormatType.DATE) {
                        hmSubTotal.put(field, 0.0);
                    }
                }

                colIndex++;
            }
            rowIndex++;

            XSSFRow titleRow = sheet.createRow(rowIndex);
            colIndex = 0;
            for (String field : listFieldName) {
                if (hmType.get(field) == FormatType.TEXT) {
                    XSSFCell cell = titleRow.createCell(colIndex);
                    if (colIndex == 1) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue("Total : ");
                    }
                    cell.setCellStyle(my_style);
                } else {
                    titleRow.createCell(colIndex).setCellValue(NumberUtil.roundTo(hmTotal.get(field), scale));
                }
                colIndex++;
            }
            rowIndex++;
        }

        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.flush();
        }
    }

    public static void genExcelFileForControlDrugForm3WS(List<String> listHeader, List<String> listFieldName,
            HashMap<String, FormatType> hmType, ResultSet rs, String fileName) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        int titleIndex = 0;
        int rowIndex = 0;

        XSSFCellStyle my_style = workbook.createCellStyle();
        XSSFFont my_font = workbook.createFont();
        my_font.setFontName("Zawgyi-One");
        my_style.setFont(my_font);
        String itemName = "";

        XSSFRow titleRow1 = sheet.createRow(0);
        XSSFCell cell1 = titleRow1.createCell(titleIndex);
        cell1.setCellValue("ထိန္းခ်ဳပ္ေဆး၀ါး အမ်ိဳးအစား တစ္ခုုခ်င္းအလိုုက္ ေန့စဥ္ ၀ယ္ယူေရာင္းခ်ျခင္း မွတ္ပံုုတင္စာအုုပ္");
        cell1.setCellStyle(my_style);
        rowIndex++;

        XSSFRow titleRow2 = sheet.createRow(rowIndex);
        XSSFCell cell2 = titleRow2.createCell(titleIndex);
        cell2.setCellValue("၁။ ခြင့္ျပဳခ်က္ရရွိသူအမည္၊ ေနရပ္လိုုပ္စာ၊ ႏိုုင္ခံသားစီစစ္ေရးကတ္ျပာအမွတ္၊ ေဆးဆိုုင္အမည္"
                + "( ၀ယ္စလီေဆးရံုု )၊ တာဟန္း၊ ကေလးျမိဳ့၊စစ္ကိုုင္းတိုုင္းေဒသၾကီး");
        cell2.setCellStyle(my_style);
        rowIndex++;

        String itemNamePrv = "-";
        if (rs != null) {
            while (rs.next()) {
                itemName = rs.getString("item_name");

                if (!itemNamePrv.equals(itemName)) {
                    XSSFRow titleRow3 = sheet.createRow(rowIndex);
                    XSSFCell cell3 = titleRow3.createCell(titleIndex);
                    cell3.setCellValue("၂၊ ေဆး၀ါးအမည္(Generic and Brand Name)(ရွိလ်င္)ႏွင့္ ပါ၀င္သည့္အေလးခ်ိန္၊ ထုုတ္လုုပ္သည့္ကုမၸဏီ  ၊ စက္ရံုု၊ႏိုုင္ငံအမည္" + itemName);
                    cell3.setCellStyle(my_style);
                    rowIndex++;
                }

                int colIndex = 0;
                if (!itemName.equals(itemNamePrv)) {
                    XSSFRow rowHeader = sheet.createRow(rowIndex);
                    for (String header : listHeader) {
                        XSSFCell cell = rowHeader.createCell(colIndex);
                        cell.setCellValue(header);
                        cell.setCellStyle(my_style);
                        colIndex++;
                    }
                    rowIndex++;
                }

                if (!itemName.equals(itemNamePrv)) {
                    itemNamePrv = itemName;
                }

                XSSFRow row = sheet.createRow(rowIndex);
                colIndex = 0;
                for (String field : listFieldName) {
                    if (null != hmType.get(field)) {
                        switch (hmType.get(field)) {
                            case TEXT:
                                XSSFCell cell = row.createCell(colIndex);
                                cell.setCellValue(rs.getString(field));
                                cell.setCellStyle(my_style);
                                break;
                            case INTEGER:
                                row.createCell(colIndex).setCellValue(rs.getInt(field));
                                break;
                            case FLOAT:
                                row.createCell(colIndex).setCellValue(rs.getFloat(field));
                                break;
                            case DOUBLE:
                                row.createCell(colIndex).setCellValue(rs.getDouble(field));
                                break;
                            case DATE:
                                row.createCell(colIndex).setCellValue(DateUtil.toDateStr(rs.getDate(field)));
                                break;
                            default:
                                break;
                        }
                    }
                    colIndex++;
                }
                rowIndex++;
            }
        }

        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.flush();
        }
    }

    public static void genExcelPatientPackage(String vouNo, String regNo, String ptName, String packageName,
            Double packageAmt, String fileName, List<PackageDetailEdit> list) throws Exception {
        List<String> listHeader = new ArrayList();
        /*listHeader.add("Vou No.");
        listHeader.add("Reg No.");
        listHeader.add("Patient Name");
        listHeader.add("Package Name");*/
        listHeader.add("Item Option");
        listHeader.add("Item Code");
        listHeader.add("Item Name");
        listHeader.add("Ttl Use Qty");
        listHeader.add("Amount");
        listHeader.add("Ttl Allow Qty");
        listHeader.add("Over Use Qty");
        listHeader.add("Status");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        //int titleIndex = 0;
        int rowIndex = 0;

        XSSFCellStyle my_style = workbook.createCellStyle();
        XSSFFont my_font = workbook.createFont();
        my_font.setFontName("Zawgyi-One");
        my_style.setFont(my_font);

        XSSFRow titleRow1 = sheet.createRow(rowIndex);
        XSSFCell cell1 = titleRow1.createCell(0);
        cell1.setCellValue("Vou No. : " + vouNo);
        cell1.setCellStyle(my_style);
        rowIndex++;

        titleRow1 = sheet.createRow(rowIndex);
        cell1 = titleRow1.createCell(0);
        cell1.setCellValue("Reg No. : " + regNo);
        cell1.setCellStyle(my_style);
        rowIndex++;

        titleRow1 = sheet.createRow(rowIndex);
        cell1 = titleRow1.createCell(0);
        cell1.setCellValue("Patient Name : " + ptName);
        cell1.setCellStyle(my_style);
        rowIndex++;

        titleRow1 = sheet.createRow(rowIndex);
        cell1 = titleRow1.createCell(0);
        cell1.setCellValue("Package Name : " + packageName);
        cell1.setCellStyle(my_style);
        rowIndex++;

        titleRow1 = sheet.createRow(rowIndex);
        cell1 = titleRow1.createCell(0);
        cell1.setCellValue("Package Price : " + packageAmt);
        cell1.setCellStyle(my_style);
        rowIndex++;

        int colIndex = 0;
        XSSFRow rowHeader = sheet.createRow(rowIndex);
        for (String header : listHeader) {
            XSSFCell cell = rowHeader.createCell(colIndex);
            cell.setCellValue(header);
            cell.setCellStyle(my_style);
            colIndex++;
        }
        rowIndex++;

        for (PackageDetailEdit pde : list) {
            XSSFRow row = sheet.createRow(rowIndex);
            XSSFCell cell;
            
            //Item Option
            cell = row.createCell(0);
            cell.setCellValue(pde.getItemOption());
            cell.setCellStyle(my_style);
            
            //Item Code
            cell = row.createCell(1);
            cell.setCellValue(pde.getItemId());
            cell.setCellStyle(my_style);
            
            //Item Name
            cell = row.createCell(2);
            cell.setCellValue(pde.getItemName());
            cell.setCellStyle(my_style);
            
            //Ttl Use Qty
            cell = row.createCell(3);
            cell.setCellValue(pde.getTtlUseQty());
            
            //Amount
            cell = row.createCell(4);
            cell.setCellValue(pde.getTtlAmount());
            
            //Ttl Allow Qty
            cell = row.createCell(5);
            cell.setCellValue(pde.getTtlAllowQty());
            
            //Over Use Qty
            cell = row.createCell(6);
            cell.setCellValue(pde.getOverUseQty());
            
            //Status
            cell = row.createCell(7);
            cell.setCellValue(pde.getStrStatus());
            cell.setCellStyle(my_style);
            
            rowIndex++;
        }

        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.flush();
        }
    }
}
