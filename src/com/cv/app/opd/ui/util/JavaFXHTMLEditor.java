/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.util;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.LabResultHis;
import com.cv.app.opd.database.entity.OPDDetailHis;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class JavaFXHTMLEditor {

    static Logger log = Logger.getLogger(JavaFXHTMLEditor.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private HTMLEditor htmlEditor;
    private Object id;
    private String option;
    private Object tmpObj;

    private void initAndShowGUI(String title, Object id, String option) {
        this.id = id;
        this.option = option;

        try {
            // This method is invoked on the EDT thread
            JFrame frame = new JFrame(title + " Comment");
            final JFXPanel fxPanel = new JFXPanel();
            JPanel buttonPanel = new JPanel();
            JButton butSave = new JButton();
            butSave.setText("Save Comment");
            butSave.addActionListener(actionSave);
            butSave.setFont(Global.lableFont);
            
            Dimension d = new Dimension();
            d.height = 30;
            d.width = 740;
            butSave.setSize(740, 30);
            butSave.setPreferredSize(d);
            
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(butSave);
            
            //JPanel panel = new JPanel();
            //panel.setLayout(new BoxLayout(panel.getContentPane(), BoxLayout.Y_AXIS));
            frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
            //frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
            
            frame.getContentPane().add(fxPanel);
            //frame.getContentPane().add(butSave);
            frame.getContentPane().add(buttonPanel);
            
            //frame.add(fxPanel);
            //frame.add(butSave);

            Dimension screen = Util1.getScreenSize();
            frame.setLocation(300, 100);
            frame.setSize(screen.width - 650, screen.height - 200);
            frame.setResizable(false);
            //frame.setAlwaysOnTop(true);
            frame.setVisible(true);

            /*frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
 /*JDialog dialog = new JDialog(Util1.getParent(), true);
                   dialog.setTitle("JavaFX Editor");
                   dialog.setSize(300, 200);
                   dialog.setLayout(new FlowLayout());
                   Container contentPane = dialog.getContentPane();
                   contentPane.add(fxPanel);
                   dialog.setLocationRelativeTo(null);
                   dialog.setVisible(true);*/
            //initFX(fxPanel);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    initFX(fxPanel);
                }
            });
            getOldData();
        } catch (Exception ex) {
            log.error("initAndShowGUI : " + ex.getMessage());
        }
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private Scene createScene() {
        Group root = new Group();
        Scene scene = new Scene(root, Color.ALICEBLUE);

        htmlEditor = new HTMLEditor();
        /*Text  text  =  new  Text();
        
        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");*/

        //root.getChildren().add(text);
        root.getChildren().add(htmlEditor);
        return (scene);
    }

    private void getOldData() {
        try {
            if (option.equals("LT")) {
                String detailId = id.toString();
                final OPDDetailHis odh = (OPDDetailHis) dao.find(OPDDetailHis.class, detailId);
                tmpObj = odh;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (odh.getLabRemark() != null) {
                            htmlEditor.setHtmlText(odh.getLabRemark());
                        }
                    }
                });

            } else if (option.equals("LR")) {
                //LabResultHisKey key = (LabResultHisKey) id;
                //final LabResultHis record = (LabResultHis) dao.find(LabResultHis.class, key);
                
                tmpObj = id;
                final LabResultHis record = (LabResultHis)tmpObj;
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (record.getLabRemark() != null) {
                            htmlEditor.setHtmlText(record.getLabRemark());
                        }
                    }
                });
            }
        } catch (Exception ex) {
            log.error("getOldData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public static void showEditor(final String text, final Object id, final String option) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JavaFXHTMLEditor editor = new JavaFXHTMLEditor();
                editor.initAndShowGUI(text, id, option);
            }
        });
    }

    private final Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String tmpStr = htmlEditor.getHtmlText();

                        if (option.equals("LT")) {
                            try {
                                OPDDetailHis odh = (OPDDetailHis) tmpObj;
                                odh.setLabRemark(tmpStr);
                                dao.save(odh);
                            } catch (Exception ex) {
                                log.error("actionSave : " + ex.getMessage());
                            } finally {
                                dao.close();
                            }
                        } else if (option.equals("LR")) {
                            try {
                                LabResultHis record = (LabResultHis) tmpObj;
                                record.setLabRemark(tmpStr);
                                dao.save(record);
                            } catch (Exception ex) {
                                log.error("actionSave : " + ex.getMessage());
                            } finally {
                                dao.close();
                            }
                        }
                    }
                });
            } catch (Exception ex) {
                log.error("actionSave : " + ex.getMessage());
            }
        }
    };
}
