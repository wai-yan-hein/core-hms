/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.util;

/*import com.jaspersoft.ireport.jasperserver.JServer;
 import com.jaspersoft.ireport.jasperserver.ws.WSClient;
 import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;*/
import java.awt.HeadlessException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ReportUtil {

    static Logger log = Logger.getLogger(ReportUtil.class.getName());
    
    public static JasperPrint getReport(String reportPath, Map<String, Object> parameters,
            Connection con) {
        JasperPrint jp;

        try {
            reportPath = reportPath + ".jasper";
            /*JasperReport jr = JasperCompileManager.compileReport(reportPath);
             jp = JasperFillManager.fillReport(jr, parameters, con);*/
            jp = JasperFillManager.fillReport(reportPath, parameters, con);
        } catch (Exception ex) {
            log.error("getReport1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(Util1.getParent(), "Error in report." + ex.toString(),
                    "Report Compilatin Error.", JOptionPane.ERROR_MESSAGE);
            jp = null;
        }

        return jp;
    }

    public static JasperPrint getReport(String reportPath, Map<String, Object> parameters,
            Collection coll) {
        JasperPrint jp;

        try {
            reportPath = reportPath + ".jasper";
            /*JasperReport jr = JasperCompileManager.compileReport(reportPath);
             jp = JasperFillManager.fillReport(jr, parameters, 
             new JRBeanCollectionDataSource(coll));*/
            jp = JasperFillManager.fillReport(reportPath, parameters,
                    new JRBeanCollectionDataSource(coll));
        } catch (Exception ex) {
            log.error("getReport2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(Util1.getParent(), "Error in report." + ex.toString(),
                    "Report Compilatin Error.", JOptionPane.ERROR_MESSAGE);
            jp = null;
        }

        return jp;
    }

    public static void viewReport(String reportPath, Map<String, Object> parameters,
            Connection con) {
        try {
            JasperPrint jp = getReport(reportPath, parameters, con);
            if (jp != null) {
                JasperViewer.viewReport(jp, false);
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            log.error("ReportUtil.viewReport : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public static void viewReport(String reportPath, Map<String, Object> parameters,
            Collection coll) {
        try {
            JasperPrint jp = getReport(reportPath, parameters, coll);
            if (jp != null) {
                JasperViewer.viewReport(jp, false);
            }
        } catch (Exception ex) {
            log.error("ReportUtil.viewReport : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public static void printJasper(JasperPrint jp, String printerName) {
        PrintService printService = getPrintService(printerName);

        try {
            if (printService != null) {
                PrinterJob job = PrinterJob.getPrinterJob();
                //PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                //MediaSizeName mediaSizeName = MediaSize.findMedia(Float.parseFloat(String.valueOf(8.264)), 
                //        Float.parseFloat(String.valueOf(11.694)), MediaPrintableArea.INCH);
                JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                //printRequestAttributeSet.add(mediaSizeName);
                //printRequestAttributeSet.add(new Copies(1));
                //jp.setOrientation(OrientationEnum.PORTRAIT);
                job.setPrintService(printService);

                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
                exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService);
                exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
                        printService.getAttributes());
                //exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, 
                //        printRequestAttributeSet);
                exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                exporter.exportReport();
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Cannot find printer.",
                        "No printer.", JOptionPane.ERROR_MESSAGE);
            }
        } catch (PrinterException | JRException | HeadlessException ex) {
            log.error("ReportUtil.printJasper : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public static void printReport(String reportPath, Map<String, Object> parameters,
            Connection con, String printerName) {
        printJasper(getReport(reportPath, parameters, con), printerName);
    }

    public static PrintService getPrintService(String printerName) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService selService = null;

        for (int i = 0; i < services.length; i++) {
            if (services[i].getName().toUpperCase().equals(printerName.toUpperCase())) {
                selService = services[i];
                i = services.length;
            }
        }

        return selService;
    }

    public static void viewReportServer() {
        /*try{
         JServer server = new JServer();

         server.setUrl("http://localhost:8080/jasperserver/services/repository");

         server.setUsername("username");

         server.setPassword("password");

         WSClient client = new WSClient(server);

         ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

         resourceDescriptor.setUriString ("/reports/myreportname");

         Map<String, Object> parameterMap = new HashMap<String, Object>();

         parameterMap.put("MY_PARAMETER_NAME", "myparametervalue");

         JasperPrint printer = client.runReport(resourceDescriptor, parameterMap);

         JasperViewer.viewReport(printer, false, Locale.GERMAN);
         }catch(Exception ex){
            
         }*/
    }

    public static ByteArrayOutputStream exportPDF(JasperPrint jp) {
        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

        try {
            exporter.exportReport();
            return baos;
        } catch (Exception e) {
            return null;
            //throw new RuntimeException(e);
        }
    }

    public static ByteArrayOutputStream exportExcel(JasperPrint jp) {
        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Here we assign the parameters jp and baos to the exporter
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

        // Excel specific parameters
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);

        try {
            exporter.exportReport();
            return baos;
        } catch (JRException e) {
            return null;
        }
    }
    
    public static void writeToFile(String path, ByteArrayOutputStream baos){
        try{
            OutputStream outputStream = new FileOutputStream (path); 
            baos.writeTo(outputStream);
        }catch(Exception ex){
            
        }
    }
    
    
}
