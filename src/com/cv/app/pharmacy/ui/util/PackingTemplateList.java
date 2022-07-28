/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PackingTemplate;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author WSwe
 */
public class PackingTemplateList {

    static Logger log = Logger.getLogger(PackingTemplateList.class.getName());
    private JDialog dialog;
    private JScrollPane jScrollPane1;
    private JTable tblPackingTemplate;
    private java.util.List<PackingTemplate> listPackingTemplate
            = ObservableCollections.observableList(new java.util.ArrayList<PackingTemplate>());
    private final AbstractDataAccess dao = Global.dao;
    private int selectedRow = -1;
    private SelectionObserver observer;

    public PackingTemplateList(Frame parent, SelectionObserver observer) {
        this.observer = observer;
        dialog = new JDialog(parent, "Packing Template List", true);
        initComponents();

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - dialog.getWidth()) / 2;
        int y = (screen.height - dialog.getHeight()) / 2;

        dialog.setLocation(x, y);

        dialog.show();
    }

    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblPackingTemplate = new javax.swing.JTable();

        dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        tblPackingTemplate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblPackingTemplate.setRowHeight(23);
        try {
            dao.open();
            initTable();
            dao.close();
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        tblPackingTemplate.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPackingTemplateMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(tblPackingTemplate);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(dialog.getContentPane());
        dialog.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                                .addContainerGap())
        );

        dialog.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {
                System.out.println("windowActivated");
            }

            public void windowClosed(WindowEvent e) {
                System.out.println("windowClosed");
            }

            public void windowClosing(WindowEvent e) {
                System.out.println("windowClosing");
            }

            public void windowDeactivated(WindowEvent e) {
                System.out.println("windowDeactivated");
            }

            public void windowDeiconified(WindowEvent e) {
                System.out.println("windowDeiconified");
            }

            public void windowIconified(WindowEvent e) {
                System.out.println("windowIconified");
            }

            public void windowOpened(WindowEvent e) {
                System.out.println("windowOpened");
            }

        });
        dialog.pack();
    }

    private void tblPackingTemplateMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            setSelected(listPackingTemplate.get(tblPackingTemplate.convertRowIndexToModel(selectedRow)));
            dialog.dispose();
        }
    }

    private void initTable() {
        try {
            listPackingTemplate = ObservableCollections.observableList(dao.findAll("PackingTemplate"));

            JTableBinding jTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                    listPackingTemplate, tblPackingTemplate);
            JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relStr}"));
            columnBinding.setColumnName("Relation String");
            columnBinding.setColumnClass(String.class);
            columnBinding.setEditable(false);
            System.out.println("Before bind.");
            jTableBinding.bind();

            tblPackingTemplate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblPackingTemplate.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    selectedRow = tblPackingTemplate.getSelectedRow();
                }
            }
            );
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void setSelected(PackingTemplate selected) {
        observer.selected("PackingTemplate", selected);
    }
}
