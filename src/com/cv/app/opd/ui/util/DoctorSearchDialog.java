/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.Initial;
import com.cv.app.opd.database.entity.Speciality;
import com.cv.app.opd.ui.common.AvailableDoctorTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author wswe
 */
public class DoctorSearchDialog extends javax.swing.JDialog {
    static Logger log = Logger.getLogger(DoctorSearchDialog.class.getName());
    private AvailableDoctorTableModel tableModel = new AvailableDoctorTableModel();
    private AbstractDataAccess dao;
    private SelectionObserver observer;
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;
    
    /**
     * Creates new form DoctorSearchDialog
     */
    public DoctorSearchDialog(AbstractDataAccess dao, SelectionObserver observer) {
        super(Util1.getParent(), true);
        initComponents();
        this.dao = dao;
        this.observer = observer;
        initCombo();
        initTable();
        search();
        sorter = new TableRowSorter(tblDoctor.getModel());
        tblDoctor.setRowSorter(sorter);
        
        if (!Util1.getPropValue("system.app.usage.type").equals("Hospital")){
            this.setTitle("Sale Men");
            lblName.setText("Name");
            lblSpeciality.setVisible(false);
            lblGender.setVisible(false);
            lblInitial.setVisible(false);
            cboSpeciality.setVisible(false);
            cboGender.setVisible(false);
            cboInitial.setVisible(false);
        }
        
        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        setVisible(true);
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboGender, dao.findAll("Gender"));
        BindingUtil.BindComboFilter(cboInitial, dao.findAll("Initial"));
        BindingUtil.BindComboFilter(cboSpeciality, dao.findAll("Speciality"));
        
        new ComBoBoxAutoComplete(cboGender);
        new ComBoBoxAutoComplete(cboInitial);
        new ComBoBoxAutoComplete(cboSpeciality);
        
        cboGender.setSelectedIndex(0);
        cboInitial.setSelectedIndex(0);
    }
    
    private void initTable() {
        tblDoctor.getColumnModel().getColumn(0).setPreferredWidth(15);
        tblDoctor.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblDoctor.getColumnModel().getColumn(2).setPreferredWidth(50);

        //Define table selection model to single row selection.
        tblDoctor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblDoctor.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblDoctor.getSelectedRow();
            }
        });
    }
    
    private void select() {
        if (selectedRow >= 0) {
            observer.selected("DoctorSearch",
                    tableModel.getDoctor(tblDoctor.convertRowIndexToModel(selectedRow)));
            dispose();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select doctor.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getHSQL() {
        String strSql = "select distinct d from Doctor d ";
        String strFilter = "active = true";

        if (cboGender.getSelectedItem() instanceof Gender) {
            Gender gender = (Gender) cboGender.getSelectedItem();

            if (strFilter == null) {
                strFilter = "genderId.genderId = '" + gender.getGenderId() + "'";
            } else {
                strFilter = strFilter + " and genderId.genderId = '" + gender.getGenderId() + "'";
            }
        }

        if (cboInitial.getSelectedItem() instanceof Initial) {
            Initial initial = (Initial) cboInitial.getSelectedItem();

            if (strFilter == null) {
                strFilter = "initialID.initialId = " + initial.getInitialId();
            } else {
                strFilter = strFilter + " and initialID.initialId = " + initial.getInitialId();
            }
        }

        if (txtDoctorName.getText() != null && !txtDoctorName.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "doctorName like '" + txtDoctorName.getText() + "%'";
            } else {
                strFilter = strFilter + " and doctorName like '" + txtDoctorName.getText() + "%'";
            }
        }

        if (cboSpeciality.getSelectedItem() instanceof Speciality) {
          Speciality spec = (Speciality)cboSpeciality.getSelectedItem();
          
            if (strFilter == null) {
                strFilter = "speciality.specId =" + spec.getSpecId();
            } else {
                strFilter = strFilter + " and speciality.specId ="
                        + spec.getSpecId();
            }
        }

        if (strFilter != null) {
            strSql = strSql + " where " + strFilter;
        } else {
            //strSql = null;
        }

        return strSql;
    }

    private void search() {
        String strSql = getHSQL();

        try {
            dao.open();
            tableModel.setListDoctor(dao.findAllHSQL(strSql));
            dao.close();
        } catch (Exception ex) {
            log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        lblTotalRec.setText("Total Records : " + tableModel.getRowCount());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        txtDoctorName = new javax.swing.JTextField();
        lblSpeciality = new javax.swing.JLabel();
        lblInitial = new javax.swing.JLabel();
        cboInitial = new javax.swing.JComboBox();
        lblGender = new javax.swing.JLabel();
        cboGender = new javax.swing.JComboBox();
        butSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDoctor = new javax.swing.JTable();
        lblTotalRec = new javax.swing.JLabel();
        cboSpeciality = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Doctor Search");

        lblName.setFont(Global.lableFont);
        lblName.setText("Doctor Name ");

        txtDoctorName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        lblSpeciality.setFont(Global.lableFont);
        lblSpeciality.setText("Speciality ");

        lblInitial.setFont(Global.lableFont);
        lblInitial.setText("Initial ");

        cboInitial.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboInitial.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblGender.setFont(Global.lableFont);
        lblGender.setText("Gender");

        cboGender.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboGender.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        tblDoctor.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblDoctor.setModel(tableModel);
        tblDoctor.setRowHeight(23);
        tblDoctor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDoctorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDoctor);

        lblTotalRec.setText("  ");

        cboSpeciality.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboSpeciality.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblInitial))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDoctorName)
                            .addComponent(cboInitial, 0, 159, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSpeciality)
                            .addComponent(lblGender))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboGender, 0, 160, Short.MAX_VALUE)
                            .addComponent(cboSpeciality, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSpeciality)
                    .addComponent(cboSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInitial)
                    .addComponent(cboInitial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGender)
                    .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(butSearch)
                    .addComponent(lblTotalRec))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblDoctorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDoctorMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            select();
        }
    }//GEN-LAST:event_tblDoctorMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox cboGender;
    private javax.swing.JComboBox cboInitial;
    private javax.swing.JComboBox cboSpeciality;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblInitial;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSpeciality;
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblDoctor;
    private javax.swing.JTextField txtDoctorName;
    // End of variables declaration//GEN-END:variables
}
