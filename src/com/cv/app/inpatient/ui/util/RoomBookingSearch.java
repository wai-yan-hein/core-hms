/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.ui.common.RoomBookingSearchTableModel;
import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
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
 * @author winswe
 */
public class RoomBookingSearch extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(RoomBookingSearch.class.getName());
    private AbstractDataAccess dao;
    private SelectionObserver observer;
    private RoomBookingSearchTableModel tableModel = new RoomBookingSearchTableModel();
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form PatientSearch
     */
    public RoomBookingSearch(AbstractDataAccess dao, SelectionObserver observer) {
        super(Util1.getParent(), true);
        initComponents();
        this.dao = dao;
        this.observer = observer;
        initCombo();
        initTable();
        sorter = new TableRowSorter(tblPatient.getModel());
        tblPatient.setRowSorter(sorter);
        //txtFrom.setText(DateUtil.getTodayDateStr());
        //txtTo.setText(DateUtil.getTodayDateStr());

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        setVisible(true);
    }

    private void initCombo() {
        
    }

    private void initTable() {
        tblPatient.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPatient.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPatient.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblPatient.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblPatient.getColumnModel().getColumn(4).setPreferredWidth(30);

        //Define table selection model to single row selection.
        tblPatient.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblPatient.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblPatient.getSelectedRow();
            }
        });
    }

    private void select() {
        if (selectedRow >= 0) {

            observer.selected("BookingSearch",
                tableModel.getRoomBooking(tblPatient.convertRowIndexToModel(selectedRow)));
            dispose();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select Name.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getHSQL() {
        /*String strSql = "select distinct o from Ams o where o.amsDate "
                + "between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";*/
        String strSql = "select distinct o from RBooking o ";
        String strFilter = null;
        
        if (!txtFrom.getText().isEmpty() && !txtTo.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.bookingDate between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                        + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";
            } else {
                strFilter = strFilter + " and o.bookingDate between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                        + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";
            }
        } else if (!txtFrom.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.bookingDate >= '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "'";
            } else {
                strFilter = strFilter + " and o.bookingDate >= '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "'";
            }
        } else if (!txtTo.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.bookingDate <= '" + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";
            } else {
                strFilter = strFilter + " and o.bookingDate <= '" + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";
            }
        }
        
       
        if (txtName.getText() != null && !txtName.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.bookingName like '" + txtName.getText().trim() + "%'";
            } else {
                strFilter = strFilter + " and o.bookingName like '" + txtName.getText().trim() + "%'";
            }
        }

        if (txtFatherName.getText() != null && !txtFatherName.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.bookingRemark like '" + txtFatherName.getText()
                        + "%'";
            } else {
                strFilter = strFilter + " and o.bookingRemark like '%"
                        + txtFatherName.getText() + "%'";
            }
        }

        if (strFilter != null) {
            strSql = strSql + " where " + strFilter;
        }

        return strSql;
    }

    private void search() {
        String strSql = getHSQL();

        if (strSql != null) {
            try {
                dao.open();
                tableModel.setListBooking(dao.findAllHSQL(strSql));
                dao.close();
            } catch (Exception ex) {
                log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tblPatient = new javax.swing.JTable();
        butSearch = new javax.swing.JButton();
        txtFatherName = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Room Booking Search");

        tblPatient.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblPatient.setModel(tableModel);
        tblPatient.setRowHeight(23);
        tblPatient.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPatientMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPatient);

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        txtFatherName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Remark");

        txtName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Name");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From ");

        txtFrom.setEditable(false);
        txtFrom.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        txtTo.setEditable(false);
        txtTo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(txtName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addGap(2, 2, 2)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(butSearch)
                            .addComponent(txtFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblPatientMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPatientMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            select();
        }
    }//GEN-LAST:event_tblPatientMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

  private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
      if (evt.getClickCount() == 2) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtFrom.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtFromMouseClicked

  private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
      if (evt.getClickCount() == 2) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtTo.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtToMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblPatient;
    private javax.swing.JTextField txtFatherName;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtName;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
