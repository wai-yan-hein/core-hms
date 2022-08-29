/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.inpatient.database.entity.DCStatus;
import com.cv.app.inpatient.ui.common.AdmissionSearchTableModel;
import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class AdmissionSearch extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(AdmissionSearch.class.getName());
    private final AbstractDataAccess dao;
    private final SelectionObserver observer;
    private final AdmissionSearchTableModel tableModel = new AdmissionSearchTableModel();
    private int selectedRow = -1;
    private final TableRowSorter<TableModel> sorter;
    private VouFilter vouFilter;

    /**
     * Creates new form PatientSearch
     *
     * @param dao
     * @param observer
     */
    public AdmissionSearch(AbstractDataAccess dao, SelectionObserver observer) {
        super(Util1.getParent(), true);
        initComponents();
        this.dao = dao;
        this.observer = observer;
        initCombo();
        //getPrvFilter();
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

    private void getPrvFilter() {
        String tranOption = "ADMSearch";
        try {
            VouFilter tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = '" + tranOption + "'"
                    + " and key.userId = '" + Global.machineId + "'");

            if (tmpFilter == null) {
                vouFilter = new VouFilter();
                vouFilter.getKey().setTranOption(tranOption);
                vouFilter.getKey().setUserId(Global.machineId);

                txtFrom.setText(DateUtil.getTodayDateStr());
                txtTo.setText(DateUtil.getTodayDateStr());
            } else {
                vouFilter = tmpFilter;
                txtFrom.setText(DateUtil.toDateStr(vouFilter.getFromDate()));
                txtTo.setText(DateUtil.toDateStr(vouFilter.getToDate()));

                if (vouFilter.getRemark() != null) {
                    txtFatherName.setText(vouFilter.getRemark());
                }

                if (vouFilter.getPtName() != null) {
                    if (!vouFilter.getPtName().isEmpty()) {
                        txtName.setText(vouFilter.getPtName());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getPrvFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboGender, dao.findAll("Gender"));
            BindingUtil.BindComboFilter(cboCity, dao.findAll("City"));
            BindingUtil.BindComboFilter(cboDcStatus, dao.findAll("DCStatus"));

            new ComBoBoxAutoComplete(cboGender);
            new ComBoBoxAutoComplete(cboCity);

            cboGender.setSelectedIndex(0);
            cboCity.setSelectedIndex(0);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblPatient.getColumnModel().getColumn(0).setPreferredWidth(15);
        tblPatient.getColumnModel().getColumn(1).setPreferredWidth(25);
        tblPatient.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblPatient.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblPatient.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblPatient.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblPatient.getColumnModel().getColumn(6).setPreferredWidth(30);
        tblPatient.getColumnModel().getColumn(7).setPreferredWidth(30);
        tblPatient.getTableHeader().setFont(Global.lableFont);
        //Define table selection model to single row selection.
        tblPatient.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblPatient.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            selectedRow = tblPatient.getSelectedRow();
        });

        try {
            List<Ams> listPatient = dao.findAllHSQL("select o from Ams o where o.dcStatus is null");
            tableModel.setListAms(listPatient);
            if (listPatient != null) {
                Integer total = listPatient.size();
                lblTotalRec.setText("Total Records : " + total.toString());
            } else {
                lblTotalRec.setText("Total Records : 0");
            }
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void select() {
        if (selectedRow >= 0) {

            observer.selected("AdmissionSearch",
                    tableModel.getAms(tblPatient.convertRowIndexToModel(selectedRow)));
            dispose();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select patient.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getHSQL() {
        /*String strSql = "select distinct o from Ams o where o.amsDate "
         + "between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
         + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";*/
        String strSql = "select distinct o from Ams o ";
        String strFilter = null;

        if (!txtFrom.getText().isEmpty() && !txtTo.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.amsDate between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                        + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";
            } else {
                strFilter = strFilter + " and o.amsDate between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                        + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";
            }

            if (vouFilter != null) {
                vouFilter.setFromDate(DateUtil.toDate(txtFrom.getText()));
                vouFilter.setToDate(DateUtil.toDate(txtTo.getText()));
            }
        } else if (!txtFrom.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.amsDate >= '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "'";
            } else {
                strFilter = strFilter + " and o.amsDate >= '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "'";
            }

            if (vouFilter != null) {
                vouFilter.setFromDate(DateUtil.toDate(txtFrom.getText()));
                vouFilter.setToDate(null);
            }
        } else if (!txtTo.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.amsDate <= '" + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";
            } else {
                strFilter = strFilter + " and o.amsDate <= '" + DateUtil.toDateTimeStrMYSQL(txtTo.getText()) + "'";
            }

            if (vouFilter != null) {
                vouFilter.setFromDate(null);
                vouFilter.setToDate(DateUtil.toDate(txtTo.getText()));
            }
        } else if (vouFilter != null) {
            vouFilter.setFromDate(null);
            vouFilter.setToDate(null);
        }

        if (cboGender.getSelectedItem() instanceof Gender) {
            Gender gender = (Gender) cboGender.getSelectedItem();

            if (strFilter == null) {
                strFilter = "o.key.register.sex.genderId = '" + gender.getGenderId() + "'";
            } else {
                strFilter = strFilter + " and o.key.register.sex.genderId = '" + gender.getGenderId() + "'";
            }
        }

        if (cboCity.getSelectedItem() instanceof City) {
            City city = (City) cboCity.getSelectedItem();

            if (strFilter == null) {
                strFilter = "o.key.register.city.cityId = " + city.getCityId();
            } else {
                strFilter = strFilter + " and o.key.register.city.cityId = " + city.getCityId();
            }
        }

        if (txtName.getText() != null && !txtName.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.key.register.patientName like '%" + txtName.getText().trim() + "%'";
            } else {
                strFilter = strFilter + " and o.key.register.patientName like '%" + txtName.getText().trim() + "%'";
            }
            if (vouFilter != null) {
                vouFilter.setPtName(txtName.getText().trim());
            }
        } else if (vouFilter != null) {
            vouFilter.setPtName(null);
        }

        if (txtFatherName.getText() != null && !txtFatherName.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.key.register.fatherName like '" + txtFatherName.getText()
                        + "%'";
            } else {
                strFilter = strFilter + " and o.key.register.fatherName like '%"
                        + txtFatherName.getText() + "%'";
            }
            if (vouFilter != null) {
                vouFilter.setRemark(txtFatherName.getText().trim());
            }
        } else if (vouFilter != null) {
            vouFilter.setRemark(null);
        }

        if (!txtBedNo.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.buildingStructure.description = '" + txtBedNo.getText().trim() + "'";
            } else {
                strFilter = strFilter + " and o.buildingStructure.description = '" + txtBedNo.getText().trim() + "'";
            }
        }

        if (cboDcStatus.getSelectedItem() instanceof DCStatus) {
            DCStatus dcs = (DCStatus) cboDcStatus.getSelectedItem();
            if (strFilter == null) {
                strFilter = "o.dcStatus.statusId = " + dcs.getStatusId().toString();
            } else {
                strFilter = strFilter + " and o.dcStatus.statusId = " + dcs.getStatusId().toString();
            }
        }

        if (!txtRegNo.getText().isEmpty()) {
            if (strFilter == null) {
                strFilter = "o.key.register.regNo = '" + txtRegNo.getText().trim() + "'";
            } else {
                strFilter = strFilter + " and o.key.register.regNo = '" + txtRegNo.getText().trim() + "'";
            }
        }

        if (strFilter != null) {
            strSql = strSql + " where " + strFilter;
        }

        //Filter history save
        if (vouFilter != null) {
            try {
                dao.save(vouFilter);
            } catch (Exception ex) {
                log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }

        return strSql;
    }

    private void search() {
        String strSql = getHSQL();

        if (strSql != null) {
            try {
                dao.open();
                tableModel.setListAms(dao.findAllHSQL(strSql));
                dao.close();
            } catch (Exception ex) {
                log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            try {
                List<Ams> listPatient = dao.findAllHSQL("select o from Ams o where o.dcStatus is null");
                tableModel.setListAms(listPatient);
            } catch (Exception ex) {
                log.error("search1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
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

        jScrollPane2 = new javax.swing.JScrollPane();
        tblPatient = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtNationality = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtFatherName = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        lblTotalRec = new javax.swing.JLabel();
        txtNRIC = new javax.swing.JTextField();
        butSearch = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        txtBedNo = new javax.swing.JTextField();
        cboCity = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cboGender = new javax.swing.JComboBox();
        cboDcStatus = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Admission Search");
        setSize(new java.awt.Dimension(600, 400));

        tblPatient.setFont(Global.textFont);
        tblPatient.setModel(tableModel);
        tblPatient.setRowHeight(24);
        tblPatient.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPatientMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPatient);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtNationality.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From ");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Nationality");

        txtFatherName.setFont(Global.textFont);

        txtName.setFont(Global.textFont);

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("NIRC");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Father Name ");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("DC Status");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Room No ");

        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        lblTotalRec.setFont(Global.lableFont);
        lblTotalRec.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalRec.setText(" ");

        txtNRIC.setFont(Global.textFont);

        butSearch.setFont(Global.lableFont);
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Name");

        txtRegNo.setFont(Global.textFont);

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("City");

        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        txtBedNo.setFont(Global.textFont);

        cboCity.setFont(Global.textFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Gender");

        cboGender.setFont(Global.textFont);

        cboDcStatus.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Reg No.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtTo))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNRIC)
                            .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNationality))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRegNo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(butSearch)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtBedNo)
                            .addComponent(cboDcStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel16, jLabel18, jLabel5});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel19});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel14, jLabel15});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(txtBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtNRIC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(txtNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(cboDcStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(butSearch)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1291, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
    private javax.swing.JComboBox cboCity;
    private javax.swing.JComboBox<String> cboDcStatus;
    private javax.swing.JComboBox cboGender;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblPatient;
    private javax.swing.JTextField txtBedNo;
    private javax.swing.JTextField txtFatherName;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtNRIC;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNationality;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
