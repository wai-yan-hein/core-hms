/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.util;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.*;
import java.awt.print.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author cv-svr
 */
public class ZPLUtil {
    static Logger log = Logger.getLogger(ZPLUtil.class.getName());
    
    public static void printZpl(String zpl,
                                String printerName,
                                String paperWidth,
                                String paperHeight) throws PrintException {
        // ZPL command to print a sample label with a barcode

//        ZPLPrinter();
//         Find the printer by its name
//        PrintService printer = findPrinterByName(printerName);
//        if (printer != null) {
//            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
//            // Create a SimpleDoc with the ZPL data and flavor
//            SimpleDoc doc = new SimpleDoc(zpl.getBytes(), flavor, null);
//            // Create a PrintRequestAttributeSet for print job attributes (e.g., number of copies)
//            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//            //attributes.add(new Copies(printCount)); // Example: 1 copy
//            String[] widthPart = paperWidth.split(" ");
//            String[] heightPart = paperHeight.split(" ");
//            float pageWidthInInches = convertInch(Float.parseFloat(widthPart[0]), widthPart[1]);
//            float pageHeightInInches = convertInch(Float.parseFloat(heightPart[0]), heightPart[1]);
//            log.info("width : " + pageWidthInInches + "height : " + pageHeightInInches);
//            int dpi = 203; // 203 dots per inch
//            int pageWidthInDots = (int) (pageWidthInInches * dpi);
//            int pageHeightInDots = (int) (pageHeightInInches * dpi);
//            MediaSizeName mediaSizeName = MediaSize.findMedia(pageWidthInDots, pageHeightInDots, MediaPrintableArea.INCH);
//            if (mediaSizeName != null) {
//                attributes.add(mediaSizeName);
//            } else {
//                // If the custom size is not supported, you can set a default media size or throw an exception
//                log.error("Custom media size not supported. Using default size.");
//                attributes.add(MediaSizeName.NA_LETTER); // Default to Letter size (8.5x11 inches)
//            }
//            // Send the print job to the target printer
//             log.error(doc);
//            printer.createPrintJob().print(doc, attributes);
//        } else {
//            log.error("printer not found.");
//        }

        //pann
        
        PrintService printer = findPrinterByName(printerName);
        if (printer != null) {
    // Define the document flavor as BYTE_ARRAY.AUTOSENSE
    DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

    // Create a SimpleDoc with the ZPL data and flavor
    SimpleDoc doc = new SimpleDoc(zpl.getBytes(), flavor, null);

    // Create a PrintRequestAttributeSet for print job attributes (e.g., number of copies)
    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

    // Uncomment the following line if you want to specify the number of copies
    // attributes.add(new Copies(printCount)); // Example: 1 copy

    // Split paper dimensions into width and height
    String[] widthPart = paperWidth.split(" ");
    String[] heightPart = paperHeight.split(" ");

    // Convert the width and height to inches
    float pageWidthInInches = convertInch(Float.parseFloat(widthPart[0]), widthPart[1]);
    float pageHeightInInches = convertInch(Float.parseFloat(heightPart[0]), heightPart[1]);

    log.info("width : " + pageWidthInInches + " height : " + pageHeightInInches);

    int dpi = 203; // 203 dots per inch

    // Calculate page dimensions in dots
    int pageWidthInDots = Math.round(pageWidthInInches * dpi);
    int pageHeightInDots = Math.round(pageHeightInInches * dpi);

    // Find a suitable media size based on the calculated dimensions
    MediaSizeName mediaSizeName = MediaSize.findMedia(pageWidthInDots, pageHeightInDots, MediaPrintableArea.INCH);
    if (mediaSizeName != null) {
        // Add the selected media size to the print job attributes
        attributes.add(mediaSizeName);
    } else {
        // If the custom size is not supported, you can set a default media size or handle it differently
        log.error("Custom media size not supported. Using default size.");
        attributes.add(MediaSizeName.NA_LETTER); // Default to Letter size (8.5x11 inches)
    }

    // Send the print job to the target printer
    log.info("Printing ZPL label...");
    printer.createPrintJob().print(doc, attributes);
} else {
    log.error("Printer not found.");
}
        
    }
    
    public static void ZPLPrinter() {
        // ZPL code for your label
        String zplCode = "^XA\n" +
                "^FO50,50^A0N,50,50^FDHello, ZPL!^FS\n" +
                "^XZ";

        try {
            // Convert the ZPL code to a byte array
            byte[] zplBytes = zplCode.getBytes("UTF-8");

            // Create a DocFlavor for ZPL
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

            // Create a Doc with the ZPL data
            Doc doc = new SimpleDoc(zplBytes, flavor, null);

            // Get a printer job
            PrinterJob printerJob = PrinterJob.getPrinterJob();

            // Get the default print service (assumes your Zebra printer is set as the default printer)
            // You can also specify a specific printer by name
             PrintService[] printServices = PrinterJob.lookupPrintServices();
             PrintService myPrinter = printServices[1];
//            PrintService myPrinter = printerJob.getPrintService();

            // Create a print job
            DocPrintJob printJob = myPrinter.createPrintJob();

            // Print the ZPL code
            printJob.print(doc, null);

        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }
    }


    public static List<String> getPrinter() {
        List<String> str = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {
            str.add(printer.getName());
        }
        return str;
    }

    public static boolean isZplStringValid(String zplString) {
        // Define a simple regex pattern for a ZPL command
        String zplCommandPattern = "\\^([A-Z0-9]+)(?:,[^\\^]+)?\\^";

        // Use a regex pattern matcher to find all ZPL commands in the string
        Pattern pattern = Pattern.compile(zplCommandPattern);
        Matcher matcher = pattern.matcher(zplString);

        // Check if all ZPL commands are well-formed
        while (matcher.find()) {
            String command = matcher.group();
            if (!zplString.contains(command)) {
                // If the ZPL command is not present in the ZPL string, it is malformed.
                return false;
            }
        }

        // If all ZPL commands are well-formed, the ZPL string is valid.
        return true;
    }

    private static PrintService findPrinterByName(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().equalsIgnoreCase(printerName)) {
                return printService;
            }
        }
        return null;
    }

    private static PageFormat getPageFormat(String paperWidth, String paperHeight, PrinterJob printerJob) {

        PageFormat pageFormat = printerJob.defaultPage();
        Paper paper = new Paper();
        double leftMargin = 0.5; // Set the left margin in inches
        double rightMargin = 0.5; // Set the right margin in inches
        double topMargin = 0.5; // Set the top margin in inches
        double bottomMargin = 0.5; // Set the bottom margin in inches
        String[] widthPart = paperWidth.split(" ");
        String[] heightPart = paperHeight.split(" ");
        double width = convertInch(Integer.parseInt(widthPart[0]), widthPart[1]);
        double height = convertInch(Integer.parseInt(heightPart[0]), heightPart[1]);
        paper.setSize(width * 72, height * 72);
        paper.setImageableArea(leftMargin * 72.0, topMargin * 72.0, (width - leftMargin - rightMargin) * 72.0, (height - topMargin - bottomMargin) * 72.0);

        pageFormat.setPaper(paper);
        log.info(pageFormat.getPaper().getWidth() + "width");
        log.info(pageFormat.getPaper().getHeight() + "height");
        log.info(pageFormat.getPaper().getImageableHeight() + "i-height");
        log.info(pageFormat.getPaper().getImageableWidth() + "i-width");
        log.info(pageFormat.getPaper().getImageableX() + "i-x");
        log.info(pageFormat.getPaper().getImageableY() + "i-y");
        return pageFormat;
        /*Paper paper = new Paper();
        double leftMargin = 0.5; // Set the left margin in inches
        double rightMargin = 0.5; // Set the right margin in inches
        double topMargin = 0.5; // Set the top margin in inches
        double bottomMargin = 0.5; // Set the bottom margin in inches
        String[] widthPart = paperWidth.split(" ");
        String[] heightPart = paperHeight.split(" ");

        paper.setSize(width * 72.0, height * 72.0); // Convert inches to points (1 inch = 72 points)
        paper.setImageableArea(leftMargin * 72.0, topMargin * 72.0, (width - leftMargin - rightMargin) * 72.0, (height - topMargin - bottomMargin) * 72.0);
        pageFormat.setPaper(paper);
        log.info("------------------");
        log.info(pageFormat.getPaper().getWidth()+"width");
        log.info(pageFormat.getPaper().getHeight()+"height");
        log.info(pageFormat.getPaper().getImageableHeight()+"i-height");
        log.info(pageFormat.getPaper().getImageableWidth()+"i-width");
        log.info(pageFormat.getPaper().getImageableX()+"i-x");
        log.info(pageFormat.getPaper().getImageableY()+"i-y");
        return pageFormat;*/
    }

    public static float convertInch(float value, String unit) {
        // Define conversion factors
        float inchesToCm = 2.54f;
        float inchesToMm = 25.4f;
        float cmToInches = 1 / inchesToCm;
        float mmToInches = 1 / inchesToMm;
        float cvtValue;
        
        switch (unit) {
            case "inch":
                cvtValue = value;
                break;
            case "cm":
                cvtValue = value * cmToInches;
                break;
            case "mm":
                cvtValue = value * mmToInches;
                break;
            default:
                cvtValue = -1;
        }
        
        return cvtValue;
    }

    public static void print2(String zpl, String printerName, String width, String height) throws PrinterException {
        PrintService defaultPrinter = findPrinterByName(printerName);

        if (defaultPrinter == null) {
            System.out.println("No printers found.");
            return;
        }

        // Create a PrinterJob
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintService(defaultPrinter);
        // Create a PageFormat object and set page setup parameters

        PageFormat pageFormat = getPageFormat(width, height, printerJob);
        // Set the PageFormat in the PrinterJob
        printerJob.setPrintable((graphics, pageFormat1, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            log.info(pageFormat1.getPaper().getWidth() + "width new");
            log.info(pageFormat1.getPaper().getHeight() + "height");
            log.info(pageFormat1.getPaper().getImageableHeight() + "i-height");
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.scale(72.0 / 300.0, 72.0 / 300.0); // Scale from points to DPI
            // Print the ZPL code
            g2d.drawString(zpl, 0, 0);
            return Printable.PAGE_EXISTS;
        }, pageFormat);
        printerJob.print();

        // Show the page setup dialog (optional)
       /* if (printerJob.printDialog()) {
            try {
                // Send the page setup to the printer

            } catch (PrinterException e) {
                System.out.println("Printing failed: " + e.getMessage());
            }
        }*/
    }
}
