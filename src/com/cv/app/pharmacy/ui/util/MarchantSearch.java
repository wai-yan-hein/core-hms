/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.view.VMarchant;
import com.cv.app.pharmacy.ui.common.MarchantSearchTableModel;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class MarchantSearch extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(MarchantSearch.class.getName());
    private final SelectionObserver observer;
    private int selectedRow = -1;
    private final MarchantSearchTableModel tableModel = new MarchantSearchTableModel();
    private final AbstractDataAccess dao;

    /**
     * Creates new form StudentSearch
     */
    public MarchantSearch(AbstractDataAccess dao, SelectionObserver observer) {
        super(Util1.getParent(), true);
        initComponents();
        initTable();
        this.observer = observer;
        this.dao = dao;

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        setVisible(true);
    }

    private void select() {
        if (selectedRow >= 0) {
            observer.selected("VMarchantSearch",
                    tableModel.getSelectVM(tblStudentList.convertRowIndexToModel(selectedRow)));
            dispose();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select Student.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void search() {
        String stuNo = Util1.getNullTo(txtStuNo.getText(), "-");
        String stuName = Util1.getNullTo(txtStuName.getText(), "-");
        String strFilter = "";

        if (!stuNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.personNumber like '%" + stuNo + "%'";
            } else {
                strFilter = strFilter + " and o.personNumber like '%" + stuNo + "%'";
            }
        }

        if (!stuName.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.personName like '%" + stuName + "%'";
            } else {
                strFilter = strFilter + " and o.personName like '%" + stuName + "%'";
            }
        }

        if (strFilter.isEmpty()) {
            strFilter = "select o from VMarchant o";
        } else {
            strFilter = "select o from VMarchant o where " + strFilter;
        }

        try {
            List<VMarchant> listVM = dao.findAllHSQL(strFilter);
            tableModel.setListVM(listVM);
        } catch (Exception ex) {
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblStudentList.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblStudentList.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblStudentList.getColumnModel().getColumn(2).setPreferredWidth(30);

        //Define table selection model to single row selection.
        tblStudentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblStudentList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblStudentList.getSelectedRow();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtStuNo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtStuName = new javax.swing.JTextField();
        butSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStudentList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Marchant Search");

        jLabel1.setText("Code");

        jLabel2.setText("Name");

        txtStuName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStuNameActionPerformed(evt);
            }
        });

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        tblStudentList.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblStudentList.setModel(tableModel);
        tblStudentList.setRowHeight(23);
        tblStudentList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStudentListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblStudentList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtStuNo, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtStuName)
                        .addGap(18, 18, 18)
                        .addComponent(butSearch)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStuNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtStuName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(butSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtStuNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStuNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStuNameActionPerformed

    private void tblStudentListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStudentListMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            select();
        }
    }//GEN-LAST:event_tblStudentListMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStudentList;
    private javax.swing.JTextField txtStuName;
    private javax.swing.JTextField txtStuNo;
    // End of variables declaration//GEN-END:variables
}
