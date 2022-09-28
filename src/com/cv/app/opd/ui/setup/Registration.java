/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.AutoClearEditor;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.entity.Booking;
import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.view.VBooking;
import com.cv.app.opd.ui.common.AvailableDoctorTableModel;
import com.cv.app.opd.ui.common.BookingTableModel;
import com.cv.app.opd.ui.util.DoctorSearchFilterDialog;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CompoundKey;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.VouId;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author winswe
 */
public final class Registration extends javax.swing.JPanel implements FormAction,
        KeyPropagate, SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(Registration.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private Patient currPatient = new Patient();
    private Booking booking = new Booking();
    private Doctor doctor = new Doctor();
    private final RegNo regNo = new RegNo(dao, "RegNo");
    private final AvailableDoctorTableModel avaDTableModel = new AvailableDoctorTableModel();
    private final BookingTableModel bkTableModel = new BookingTableModel();
    private BestAppFocusTraversalPolicy focusPolicy;
    private String focusCtrlName = "-";
    private Boolean bkPTF = false;
    private final StartWithRowFilter swrfGroup;
    private final TableRowSorter<TableModel> sorterGroup;
    private boolean print = false;

    public void timerFocus() {
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtName.requestFocus();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Creates new form PatientSetup
     */
    public Registration() {
        initComponents();
        initCombo();
        assignDefaultValue();
        initTable();
        actionMapping();
        /*swrfGroup = new StartWithRowFilter(doctorFilter);
         sorterGroup = new TableRowSorter(tblBooking.getModel());
         tblBooking.setRowSorter(sorterGroup);*/
        // TableModel model = new DefaultTableModel(tblBooking.getRowCount(), tblBooking.getColumnCount());

        swrfGroup = new StartWithRowFilter(txtSearch);
        sorterGroup = new TableRowSorter(tblBooking.getModel());
        tblBooking.setRowSorter(sorterGroup);

        initForFocus();
        timerFocus();

        if (Util1.getPropValue("system.opd.doctor.booking").equals("Y")) {
            jPanel1.setVisible(true);
            jPanel2.setVisible(true);
        } else {
            jPanel1.setVisible(false);
            jPanel2.setVisible(false);
        }

        //applyFocusPolicy();
        //AddFocusMoveKey();
        //this.setFocusTraversalPolicy(focusPolicy);
        txtAdmissionNo.setEnabled(false);
    }

    //For focus movement key
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!focusCtrlName.equals("-")) {
            if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtName")) {
                txtFatherName.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtFatherName")) {
                txtDOB.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtDOB")) {
                txtAge.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtAge")) {
                cboGender.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("cboGender")) {
                if (!cboGender.isPopupVisible()) {
                    txtNIRC.requestFocus();
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtNIRC")) {
                txtNationality.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtNationality")) {
                cboCity.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("cboCity")) {
                if (!cboCity.isPopupVisible()) {
                    txtReligion.requestFocus();
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtReligion")) {
                txtContactNo.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("txtContactNo")) {
                cboDoctor.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                    && focusCtrlName.equals("cboDoctor")) {
                if (!cboCity.isPopupVisible()) {
                    txtName.requestFocus();
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtName")) {
                cboDoctor.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtFatherName")) {
                txtName.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtDOB")) {
                txtFatherName.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtAge")) {
                txtDOB.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("cboGender")) {
                if (!cboGender.isPopupVisible()) {
                    txtAge.requestFocus();
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtNIRC")) {
                cboGender.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtNationality")) {
                txtNIRC.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("cboCity")) {
                if (!cboCity.isPopupVisible()) {
                    txtNationality.requestFocus();
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtReligion")) {
                cboCity.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("txtContactNo")) {
                txtReligion.requestFocus();
            } else if ((e.getKeyCode() == KeyEvent.VK_UP)
                    && focusCtrlName.equals("cboDoctor")) {
                if (!cboDoctor.isPopupVisible()) {
                    txtName.requestFocus();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void save() {
        if (isValidEntry()) {
            try {
                dao.save(currPatient);
                log.error("Reg Check : " + currPatient.getRegNo() + ";" + currPatient.getPatientName());
                if (lblStatus.getText().equals("NEW")) {
                    regNo.updateRegNo();
                }

                if (lblStatus.getText().equals("NEW")) {
                    if (!Util1.getPropValue("system.opd.showregno").equals("Y")) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Patient registeration number is " + currPatient.getRegNo(), "Registeration No",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                if (print) {
                    String printMode = Util1.getPropValue("report.vou.printer.mode");
                    String path = Util1.getAppWorkFolder()
                            + Util1.getPropValue("report.folder.path")
                            + "Clinic/PatientInfo";
                    String printerName = Util1.getPropValue("label.printer");
                    Map<String, Object> p = new HashMap();
                    p.put("p_patient", currPatient.getPatientName());
                    p.put("p_dob", DateUtil.toDateStr(currPatient.getDob(), "dd/MM/yyyy"));
                    p.put("p_age", getAge() + "," + currPatient.getSex().getGenderId());
                    p.put("p_address", getAddress());
                    p.put("p_phone", currPatient.getContactNo());
                    p.put("p_reg_no", currPatient.getRegNo());
                    if (printMode.equals("View")) {
                        ReportUtil.viewReport(path, p, dao.getConnection());
                    } else {
                        JasperPrint jp = ReportUtil.getReport(path, p, dao.getConnection());
                        ReportUtil.printJasper(jp, printerName);
                    }
                }
                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private String getAge() {
        String age = txtAge.getText();
        String m = txtMonth.getText();
        String d = txtDay.getText();
        String str = "";
        if (!age.isEmpty() && Integer.parseInt(age) > 0) {
            str = "," + age + "y";
        }
        if (!m.isEmpty() && Integer.parseInt(m) > 0) {
            str += "," + m + "m";

        }
        if (!d.isEmpty() && Integer.parseInt(d) > 0) {
            str += "," + d + "d";
        }
        return str;
    }

    private String getAddress() {
        String add = "";
        if (currPatient.getTownship() != null) {
            add = currPatient.getTownship().getTownshipName() + ",";
        }
        if (currPatient.getCity() != null) {
            add += currPatient.getCity().getCityName();
        }
        return add;
    }

    @Override
    public void newForm() {
        lblStatus.setText("NEW");
        currPatient = new Patient();
        txtRegNo.setText(null);
        txtRegDateTime.setText(null);
        txtName.setText(null);
        txtFatherName.setText(null);
        txtDOB.setText(null);
        txtAge.setText(null);
        txtNIRC.setText(null);
        txtNationality.setText(null);
        txtAddress.setText(null);
        txtReligion.setText(null);
        txtContactNo.setText(null);
        txtMonth.setText(null);
        txtDay.setText(null);
        assignDefaultValue();
        txtName.requestFocus();
        txtBillID.setText(null);
        txtAdmissionNo.setText(null);
        lblAgeStr.setText(null);
        print = false;
    }

    @Override
    public void history() {
        PatientSearch ps = new PatientSearch(dao, this);
    }

    @Override
    public void delete() {
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
        print = true;
        save();
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            delete();
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            deleteCopy();
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
            save();
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
            print();
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
            history();
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
            newForm();
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboCity,
                    dao.findAllHSQL("select o from City o order by o.cityName"));
            AutoCompleteDecorator.decorate(cboCity);
            //new ComBoBoxAutoComplete(cboCity);

            BindingUtil.BindCombo(cboGender, dao.findAll("Gender"));
            AutoCompleteDecorator.decorate(cboGender);

            BindingUtil.BindCombo(cboDoctor, dao.findAllHSQL("select o from Doctor o "
                    + "where o.active = true order by o.doctorName"));
            AutoCompleteDecorator.decorate(cboDoctor);

            BindingUtil.BindComboFilter(cboDr, dao.findAllHSQL("select o from Doctor o "
                    + "where o.active = true order by o.doctorName"));
            AutoCompleteDecorator.decorate(cboDr);
            BindingUtil.BindCombo(cboTownship,
                    dao.findAllHSQL("select o from Township o order by o.townshipName"));
            AutoCompleteDecorator.decorate(cboTownship);

            BindingUtil.BindCombo(cboType,
                    dao.findAllHSQL("select o from CustomerGroup o order by o.groupName"));
            AutoCompleteDecorator.decorate(cboType);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblBooking.getColumnModel().getColumn(0).setPreferredWidth(20);//Date
        tblBooking.getColumnModel().getColumn(1).setPreferredWidth(80);//Visit No
        tblBooking.getColumnModel().getColumn(2).setPreferredWidth(20);//Reg Id
        tblBooking.getColumnModel().getColumn(3).setPreferredWidth(150);//Patient Name
        tblBooking.getColumnModel().getColumn(4).setPreferredWidth(150);//Doctor Name
        tblBooking.getColumnModel().getColumn(5).setPreferredWidth(150);//Phone
        tblBooking.getColumnModel().getColumn(6).setPreferredWidth(10);//Serial ID
        tblBooking.getColumnModel().getColumn(7).setPreferredWidth(3);//Active
        tblBooking.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
        tblBooking.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblBooking.getTableHeader().setFont(Global.textFont);
        searchBooking(false);
    }

    private void searchBooking(boolean print) {
        try {
            String fromDate = DateUtil.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
            String toDate = DateUtil.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
            String doctorId = "-";
            if (cboDr.getSelectedItem() instanceof Doctor) {
                Doctor d = (Doctor) cboDr.getSelectedItem();
                doctorId = d.getDoctorId();
            }
            if (print) {
                String path = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + "Clinic/W/OPDAppointmentByDoctor";
                Map<String, Object> p = new HashMap();
                p.put("p_comp_name", Util1.getPropValue("report.company.name"));
                p.put("p_data_date", String.format(
                        "Between %s and %s",
                        DateUtil.toDateStr(txtFromDate.getDate(), "dd/MM/yyyy"),
                        DateUtil.toDateStr(txtToDate.getDate(), "dd/MM/yyyy")));
                p.put("p_from_date", fromDate);
                p.put("p_to_date", toDate);
                p.put("p_doctor", doctorId);
                ReportUtil.viewReport(path, p, dao.getConnection());
            } else {
                bkTableModel.setListBooking(
                        dao.findAllHSQL(
                                "select o from VBooking o\n"
                                + "where date(o.bkDate) between '" + fromDate + "' and '" + toDate + "' and (o.doctorId = '" + doctorId + "' or '-' = '" + doctorId + "')"));
                calculateTotalRecord();
            }
        } catch (Exception ex) {
            log.error("dateFilterTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PatientSearch":
                if (bkPTF == true) {
                    Patient tmpPatient = (Patient) selectObj;
                    bkRegno.setText(tmpPatient.getRegNo());
                    bkpatientName.setText(tmpPatient.getPatientName());
                    bkPhone.setText(tmpPatient.getContactNo());
                    bkPTF = false;
                } else {
                    lblStatus.setText("EDIT");
                    currPatient = (Patient) selectObj;
                    //PatientDetail pd = currPatient;

                    txtRegNo.setText(currPatient.getRegNo());
                    txtName.setText(currPatient.getPatientName());
                    txtRegDateTime.setText(DateUtil.toDateTimeStr(currPatient.getRegDate(),
                            "dd/MM/yyyy HH:mm:ss"));
                    txtFatherName.setText(currPatient.getFatherName());
                    txtDOB.setText(DateUtil.toDateStr(currPatient.getDob()));
                    if (DateUtil.isValidDate(txtDOB.getText())) {
                        lblAgeStr.setText(DateUtil.getAge(txtDOB.getText()));
                    }
                    if (currPatient.getAge() != null) {
                        txtAge.setText(currPatient.getAge().toString());
                    }
                    if (currPatient.getMonth() != null) {
                        txtMonth.setText(currPatient.getMonth().toString());
                    }
                    if (currPatient.getDay() != null) {
                        txtDay.setText(currPatient.getDay().toString());
                    }
                    cboGender.setSelectedItem(currPatient.getSex());
                    txtNIRC.setText(currPatient.getNirc());
                    txtNationality.setText(currPatient.getNationality());
                    txtAddress.setText(currPatient.getAddress());
                    cboCity.setSelectedItem(currPatient.getCity());
                    txtReligion.setText(currPatient.getReligion());
                    txtContactNo.setText(currPatient.getContactNo());
                    cboDoctor.setSelectedItem(currPatient.getDoctor());
                    //txtAge.setText(currPatient.getAge().toString());
                    cboTownship.setSelectedItem(currPatient.getTownship());
                    cboType.setSelectedItem(currPatient.getPtType());
                    dao.close();
                    txtBillID.setText(currPatient.getOtId());
                    txtAdmissionNo.setText(currPatient.getAdmissionNo());
                }
                break;
            case "DoctorSearchFilter":
                try {
                doctor = (Doctor) selectObj;

                if (doctor != null) {
                    //txtDrCode.setText(doc.getDoctorId());
                    bkDoctor.setText(doctor.getDoctorName());
                    booking = new Booking();
                    booking.setDoctor(doctor);

                    //currSaleVou.setDoctor(doc);
                    //tblSale.requestFocus();
                }
            } catch (Exception ex) {
                log.error("selected DoctorSearch : " + selectObj.toString() + " - " + ex.getMessage());
            }
        }

    }

    private void assignDefaultValue() {
        txtRegDateTime.setText(DateUtil.getTodayDateStr("dd/MM/yyyy HH:mm:ss"));
        bkDate.setText(DateUtil.getTodayDateStr("dd/MM/yyyy"));
        if (Util1.getPropValue("system.opd.showregno").equals("Y")) {
            txtRegNo.setText(regNo.getRegNo());
        }
        cboGender.setSelectedItem(null);
        cboCity.setSelectedItem(null);
        cboDoctor.setSelectedItem(null);
        cboTownship.setSelectedItem(null);
        cboType.setSelectedItem(null);
        txtFromDate.setDate(DateUtil.getTodayDateTime());
        txtToDate.setDate(DateUtil.getTodayDateTime());
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtName.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Please key in name.", "Patient Name",
                    JOptionPane.ERROR_MESSAGE);
            txtName.requestFocusInWindow();
        } else if (txtNIRC.getText().length() > 100) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "NIRC character length is 100.", "NIRC Length",
                    JOptionPane.ERROR_MESSAGE);
            txtNIRC.requestFocusInWindow();
        } else {
            try {
                Patient pt = null;
                if (txtRegNo.getText().trim().isEmpty()) {
                    currPatient.setRegNo(regNo.getRegNo());
                    pt = (Patient) dao.find(Patient.class, currPatient.getRegNo());
                    String tmpRegNo = currPatient.getRegNo();
                    tmpRegNo = tmpRegNo.replace(regNo.getPeriod(), "");
                    if (tmpRegNo.equals("0")) {
                        currPatient.setRegNo(regNo.getRegNo());
                        tmpRegNo = currPatient.getRegNo();
                        if (tmpRegNo.equals("0")) {
                            log.error("generateRegNo : " + "Registeration number generate to zero.");
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Error in id generation. Please exit the program and try again", "Duplicate",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(-1);
                        }
                    }
                } else {
                    currPatient.setRegNo(txtRegNo.getText());
                }

                if (txtRegNo.getText().trim().isEmpty()) {
                    if (pt != null) {
                        log.error("Duplicate Registeration : " + currPatient.getRegNo() + ";" + currPatient.getPatientName());
                        /*JOptionPane.showMessageDialog(Util1.getParent(),
                        "Duplicate registeration number. You cannot save.", "Duplicate",
                        JOptionPane.ERROR_MESSAGE);*/
                        resetRegNo(regNo.getPeriod());
                        currPatient.setRegNo(regNo.getRegNo());
                    }
                }

                currPatient.setPatientName(txtName.getText().trim());
                currPatient.setAddress(txtAddress.getText().trim());
                currPatient.setContactNo(txtContactNo.getText().trim());
                currPatient.setCreatedBy(Global.loginUser.getUserId());
                //currPatient.setRegNo(txtRegNo.getText());
                currPatient.setRegDate(DateUtil.toDateTime(txtRegDateTime.getText()));
                currPatient.setFatherName(txtFatherName.getText().trim());
                if (txtDOB.getText().isEmpty()) {
                    if (!txtAge.getText().isEmpty()) {
                        currPatient.setDob(getDOB());
                    }
                } else {
                    currPatient.setDob(DateUtil.toDate(txtDOB.getText(), "dd/MM/yyyy"));
                }
                currPatient.setSex((Gender) cboGender.getSelectedItem());
                currPatient.setNirc(txtNIRC.getText().trim());
                currPatient.setNationality(txtNationality.getText().trim());
                currPatient.setCity((City) cboCity.getSelectedItem());
                currPatient.setReligion(txtReligion.getText().trim());
                currPatient.setDoctor((Doctor) cboDoctor.getSelectedItem());
                currPatient.setAge(NumberUtil.NZeroInt(txtAge.getText()));
                currPatient.setMonth(NumberUtil.NZeroInt(txtMonth.getText()));
                currPatient.setDay(NumberUtil.NZeroInt(txtDay.getText()));
                currPatient.setTownship((Township) cboTownship.getSelectedItem());
                currPatient.setPtType((CustomerGroup) cboType.getSelectedItem());
                currPatient.setAdmissionNo(txtAdmissionNo.getText().trim());
                if (!txtBillID.getText().isEmpty()) {
                    currPatient.setOtId(txtBillID.getText());
                } else {
                    currPatient.setOtId(null);
                }
            } catch (Exception ex) {
                log.error("isValidEntry : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
        return status;
    }

    private void clearBooking() {
        bkRegno.setText("");
        bkpatientName.setText("");
        bkDoctor.setText("");
        bkPhone.setText("");
        bkPTF = false;
        print = false;
    }

    private Date getDOB() {
        int year;
        int month;
        int day;
        year = NumberUtil.NZeroInt(txtAge.getText());
        month = NumberUtil.NZeroInt(txtMonth.getText());
        day = NumberUtil.NZeroInt(txtDay.getText());
        String bdDate = DateUtil.getDOB1(year, month, day);
        return DateUtil.toDate(bdDate);
    }

    private void calDOB() {
        int year;
        int month;
        int day;
        year = NumberUtil.NZeroInt(txtAge.getText());
        month = NumberUtil.NZeroInt(txtMonth.getText());
        day = NumberUtil.NZeroInt(txtDay.getText());
        String bdDate = DateUtil.getDOB1(year, month, day);
        txtDOB.setText(bdDate);
    }

    private boolean isValidEntryBooking() {
        boolean status = true;
        if (Util1.nullToBlankStr(bkpatientName.getText()).equals("")) {
            status = false;
            /*JOptionPane.showMessageDialog(Util1.getParent(),
             "Please key in name.", "Patient Name",
             JOptionPane.ERROR_MESSAGE);
             txtName.requestFocusInWindow();*/
        } else if (Util1.nullToBlankStr(bkDoctor.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Please key in doctor.", "Doctor",
                    JOptionPane.ERROR_MESSAGE);
            bkDoctor.requestFocusInWindow();
        } else {
            // bkPatient = new Booking();
            booking.setRegNo(bkRegno.getText());
            booking.setPatientName(bkpatientName.getText());
            booking.setBkDate(DateUtil.toDate(bkDate.getText()));
            booking.setBkPhone(bkPhone.getText());
            booking.setCreatedBy(Global.loginUser.getUserId());

        }

        return status;
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtName);
        focusOrder.add(txtFatherName);
        focusOrder.add(txtDOB);
        focusOrder.add(cboGender);
        focusOrder.add(txtNIRC);
        focusOrder.add(txtNationality);
        focusOrder.add(txtAddress);
        focusOrder.add(cboCity);
        focusOrder.add(txtReligion);
        focusOrder.add(txtContactNo);
        focusOrder.add(cboDoctor);

        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    private void AddFocusMoveKey() {
        Set backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);

        Set forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);

        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));

        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
    }

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Print
        /*jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
         jc.getActionMap().put("F7-Action", actionPrint);*/
        //History
        jc.getInputMap().put(KeyStroke.getKeyStroke("F9"), "F9-Action");
        jc.getActionMap().put("F9-Action", actionHistory);

        //Clear
        jc.getInputMap().put(KeyStroke.getKeyStroke("F10"), "F10-Action");
        jc.getActionMap().put("F10-Action", actionNewForm);

        //Delete
        /*KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
         jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
         jc.getActionMap().put("Ctrl-F8-Action", actionDelete);*/
        //Delete Copy
        /*keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.SHIFT_DOWN_MASK);
         jc.getInputMap().put(keyStroke, "Shift-F8-Action");
         jc.getActionMap().put("Shift-F8-Action", actionDeleteCopy);*/
    }

    public void saveBkPatient() {
        if (isValidEntryBooking()) {
            try {
                genBkSerialNo();
                dao.save(booking);
                initTable();
                //bkTableModel.addBooking(bkPatient);
                clearBooking();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("saveBk : " + ex.toString());
            } finally {
                dao.close();
            }
        }

    }

    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };

    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };

    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    private void genBkSerialNo() {
        String sql = "select count(*)\n"
                + "from opd_booking\n"
                + "where doctor_id = '" + doctor.getDoctorId() + "' and bk_date = '" + DateUtil.toDateStr(bkDate.getText(), "yyyy-MM-dd") + "'";
        ResultSet rs = dao.execSQL(sql);
        if (rs != null) {
            try {
                rs.next();
                String tmpNo = rs.getString(1);
                booking.setBkSerialNo(Integer.parseInt(tmpNo) + 1);

            } catch (SQLException ex) {
                log.info("ERROR : " + ex.getMessage());
            }

        } else {
            booking.setBkSerialNo(1);
        }
    }

    private void actionMapping() {
        //F3 event on tblSale
        /*tblSale.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
         tblSale.getActionMap().put("F3-Action", actionMedList);

         //F8 event on tblSale
         tblSale.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
         tblSale.getActionMap().put("F8-Action", actionItemDelete);

         //Enter event on tblSale
         tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
         tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);

         //Enter event on tblExpense
         tblExpense.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
         tblExpense.getActionMap().put("ENTER-Action", actionTblExpEnterKey);

         //F8 event on tblExpense
         tblExpense.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
         tblExpense.getActionMap().put("F8-Action", actionItemDeleteExp);

         //F8 event on tblTransaction
         tblTransaction.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
         tblTransaction.getActionMap().put("F8-Action", actionItemDeleteTran);*/
        tblBooking.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblBooking.getActionMap().put("F8-Action", actionBookingDelete);

        formActionKeyMapping(txtName);
        formActionKeyMapping(txtFatherName);
        formActionKeyMapping(txtDOB);
        formActionKeyMapping(txtAge);
        formActionKeyMapping(cboGender);
        formActionKeyMapping(txtNIRC);
        formActionKeyMapping(txtNationality);
        formActionKeyMapping(txtAddress);
        formActionKeyMapping(cboCity);
        formActionKeyMapping(txtReligion);
        formActionKeyMapping(txtContactNo);
        formActionKeyMapping(cboDoctor);
    }

    private void initForFocus() {
        txtName.addKeyListener(this);
        txtFatherName.addKeyListener(this);
        txtDOB.addKeyListener(this);
        txtAge.addKeyListener(this);
        txtNIRC.addKeyListener(this);
        txtNationality.addKeyListener(this);
        txtReligion.addKeyListener(this);
        txtContactNo.addKeyListener(this);

        cboGender.getEditor().getEditorComponent().addKeyListener(this);
        cboGender.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboGender.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        cboCity.getEditor().getEditorComponent().addKeyListener(this);
        cboCity.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboCity.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        cboDoctor.getEditor().getEditorComponent().addKeyListener(this);
        cboDoctor.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboDoctor.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    private final Action actionBookingDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblBooking.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Booking delete", JOptionPane.YES_NO_OPTION);

                    if (tblBooking.getCellEditor() != null) {
                        tblBooking.getCellEditor().stopCellEditing();
                    }
                } catch (HeadlessException ex) {
                }

                if (yes_no == 0) {
                    bkTableModel.delete(tblBooking.getSelectedRow());
                    searchBooking(false);
                }

            }
        }
    };

    private void getPatient() {
        if (bkRegno.getText() != null && !bkRegno.getText().trim().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class, bkRegno.getText().trim());
                dao.close();

                if (pt == null) {
                    bkRegno.setText(null);
                    bkpatientName.setText(null);
                    bkPhone.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    bkPTF = true;
                    selected("PatientSearch", pt);
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            bkRegno.setText(null);
            bkpatientName.setText(null);
            bkPhone.setText(null);
        }
    }

    private void printVisitSlip() {
        VBooking tmpBooking = bkTableModel.getBooking(tblBooking.convertRowIndexToModel(tblBooking.getSelectedRow()));
        if (tmpBooking != null) {
            if (tmpBooking.getRegNo() == null) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "Invalid registeration number.", "Invalid",
                        JOptionPane.ERROR_MESSAGE);
            } else if (tmpBooking.getRegNo().isEmpty()) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "Invalid registeration number.", "Invalid",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                String reportName = Util1.getPropValue("system.opd.booking.report");
                if (reportName.isEmpty() || reportName.equals("-")) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid report.", "Invalid",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String reportPath = Util1.getAppWorkFolder()
                            + Util1.getPropValue("report.folder.path")
                            + "Clinic/"
                            + reportName;
                    Map<String, Object> params = new HashMap();
                    String compName = Util1.getPropValue("report.company.name");
                    String phoneNo = Util1.getPropValue("report.phone");
                    String address = Util1.getPropValue("report.address");
                    String printMode = Util1.getPropValue("report.vou.printer.mode");
                    String printerName = Util1.getPropValue("report.vou.printer");
                    params.put("p_user_id", Global.machineId);
                    params.put("p_user_short", Global.loginUser.getUserShortName());
                    params.put("compName", compName);
                    params.put("phone", phoneNo);
                    params.put("comp_address", address);
                    params.put("p_tran_date", DateUtil.toDateStr(tmpBooking.getBkDate()));
                    params.put("p_doctor", tmpBooking.getDoctorName());
                    params.put("p_patient", tmpBooking.getPatientName());
                    params.put("p_reg_no", tmpBooking.getRegNo());
                    params.put("p_book_no", tmpBooking.getBkSerialNo());
                    params.put("p_visit_no", tmpBooking.getDoctorId() + " " + tmpBooking.getRegNo() + " "
                            + DateUtil.getDatePart(tmpBooking.getBkDate(), "ddMMyyyy")
                            + " " + tmpBooking.getBkSerialNo());
                    /*params.put("p_visit_no", tmpBooking.getDoctorId() + tmpBooking.getRegNo()
                            + DateUtil.getDatePart(tmpBooking.getBkDate(), "ddMMyyyy")
                            + tmpBooking.getBkSerialNo());*/

                    try {
                        dao.close();
                        if (printMode.equals("View")) {
                            ReportUtil.viewReport(reportPath, params, dao.getConnection());
                        } else {
                            JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
                            ReportUtil.printJasper(jp, printerName);
                        }
                        dao.commit();
                    } catch (Exception ex) {
                        log.error("printVisitSlip : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            }
        }
    }

    private void calculateTotalRecord() {
        txtTotalRecord.setValue(tblBooking.getRowCount());
    }

    private void resetRegNo(String period) {
        String strSql = "select max(cast(replace(reg_no,'" + period
                + "','') as unsigned)) as sr from patient_detail where reg_no like '%" + period + "'";
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    Integer lastNo = rs.getInt("sr") + 1;
                    String vouType = "RegNo";
                    VouId vouId = (VouId) dao.find(VouId.class,
                            new CompoundKey(vouType, vouType, period));
                    if (vouId == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Duplicate registeration number. You cannot save.", "Duplicate",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                    } else {
                        vouId.setVouNo(lastNo);
                        dao.save(vouId);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("resetRegNo : " + ex.getMessage());
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Duplicate registeration number. You cannot save.", "Duplicate",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } finally {
            dao.close();
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

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtRegDateTime = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFatherName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDOB = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtAge = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNIRC = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNationality = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cboCity = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        txtReligion = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtContactNo = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cboDoctor = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        cboTownship = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        cboGender = new javax.swing.JComboBox();
        butBillID = new javax.swing.JButton();
        txtBillID = new javax.swing.JTextField();
        txtMonth = new javax.swing.JTextField();
        txtDay = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAdmissionNo = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        lblAgeStr = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        bkRegno = new javax.swing.JFormattedTextField();
        bkDoctor = new javax.swing.JFormattedTextField();
        bkDate = new javax.swing.JFormattedTextField();
        bkPhone = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        bkSave = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        bkpatientName = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBooking = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JFormattedTextField();
        txtTotalRecord = new javax.swing.JFormattedTextField();
        jLabel29 = new javax.swing.JLabel();
        butRefresh = new javax.swing.JButton();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel32 = new javax.swing.JLabel();
        cboDr = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registration", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Reg-No");

        txtRegNo.setEditable(false);
        txtRegNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Reg-Date Time ");

        txtRegDateTime.setEditable(false);
        txtRegDateTime.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtRegDateTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegDateTimeActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Father Name ");

        txtFatherName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFatherName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFatherNameFocusGained(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("DOB");

        try {
            txtDOB.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            System.out.println("Registration : " + ex.toString());
        }
        txtDOB.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtDOB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDOBFocusGained(evt);
            }
        });
        txtDOB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDOBActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Age ");

        txtAge.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAge.setToolTipText("");
        txtAge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAgeFocusGained(evt);
            }
        });
        txtAge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAgeActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("NIRC");

        txtNIRC.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtNIRC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNIRCFocusGained(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Nationality");

        txtNationality.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtNationality.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNationalityFocusGained(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("City");

        cboCity.setFont(Global.textFont);
        cboCity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboCityFocusGained(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Religion");

        txtReligion.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtReligion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtReligionFocusGained(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Contact No");

        txtContactNo.setFont(Global.textFont);
        txtContactNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtContactNoFocusGained(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Doctor ");

        cboDoctor.setFont(Global.textFont);
        cboDoctor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboDoctorFocusGained(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Township");

        cboTownship.setFont(Global.textFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Type");

        cboType.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Address");

        txtAddress.setColumns(20);
        txtAddress.setFont(Global.textFont);
        txtAddress.setRows(5);
        jScrollPane1.setViewportView(txtAddress);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Gender");

        cboGender.setFont(Global.textFont);
        cboGender.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboGenderFocusGained(evt);
            }
        });

        butBillID.setFont(Global.lableFont);
        butBillID.setText("Bill ID");
        butBillID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBillIDActionPerformed(evt);
            }
        });

        txtMonth.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMonth.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMonthFocusGained(evt);
            }
        });
        txtMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMonthActionPerformed(evt);
            }
        });

        txtDay.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtDay.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDayFocusGained(evt);
            }
        });
        txtDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDayActionPerformed(evt);
            }
        });

        jLabel26.setText("Y");

        jLabel27.setText("M");

        jLabel28.setText("D");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Admission No : ");

        jLabel18.setText("Age : ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jLabel10)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel9)
                            .addComponent(jLabel1)
                            .addComponent(butBillID))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtReligion, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(txtNIRC, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNationality, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtContactNo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblStatus)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtBillID, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel11))
                                .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtFatherName, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(txtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel26)
                                    .addGap(6, 6, 6)
                                    .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(6, 6, 6)
                                    .addComponent(jLabel27)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(3, 3, 3)
                                    .addComponent(jLabel28)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel3)
                                    .addGap(40, 40, 40)
                                    .addComponent(txtRegDateTime))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(cboDoctor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(cboTownship, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGap(18, 18, 18)
                                            .addComponent(jLabel16)))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel18)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(lblAgeStr, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cboType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap()))))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtRegDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26)
                        .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(jLabel28)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtNIRC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtReligion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAgeStr, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBillID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butBillID)
                    .addComponent(jLabel17)
                    .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Appointment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        bkRegno.setFont(Global.textFont);
        bkRegno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bkRegnoMouseClicked(evt);
            }
        });
        bkRegno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bkRegnoActionPerformed(evt);
            }
        });

        bkDoctor.setEditable(false);
        bkDoctor.setFont(Global.textFont);
        bkDoctor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bkDoctorFocusGained(evt);
            }
        });
        bkDoctor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bkDoctorMouseClicked(evt);
            }
        });
        bkDoctor.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                bkDoctorInputMethodTextChanged(evt);
            }
        });
        bkDoctor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bkDoctorActionPerformed(evt);
            }
        });
        bkDoctor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                bkDoctorKeyPressed(evt);
            }
        });

        bkDate.setEditable(false);
        bkDate.setFont(Global.textFont);
        bkDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bkDateMouseClicked(evt);
            }
        });

        bkPhone.setFont(Global.textFont);
        bkPhone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bkPhoneActionPerformed(evt);
            }
        });

        jLabel25.setFont(Global.lableFont);
        jLabel25.setText("Phone");

        jLabel23.setFont(Global.lableFont);
        jLabel23.setText("Reg-No");

        jLabel24.setFont(Global.lableFont);
        jLabel24.setText("Doctor");

        bkSave.setFont(Global.lableFont);
        bkSave.setText("Save");
        bkSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bkSaveActionPerformed(evt);
            }
        });

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Date");

        bkpatientName.setFont(Global.textFont);
        bkpatientName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bkpatientNameActionPerformed(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Name");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bkRegno, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bkDate, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bkPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bkpatientName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bkSave, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bkDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel21, jLabel23, jLabel25});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel22, jLabel24});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bkRegno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22)
                    .addComponent(bkDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(bkpatientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(bkPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(bkDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bkSave, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Appointment  History", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        tblBooking.setFont(Global.textFont);
        tblBooking.setModel(bkTableModel);
        tblBooking.setToolTipText("Year");
        tblBooking.setRowHeight(23);
        tblBooking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBookingMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblBooking);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Search");

        txtSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        txtTotalRecord.setEditable(false);
        txtTotalRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel29.setFont(Global.lableFont);
        jLabel29.setText("Total : ");

        butRefresh.setFont(Global.lableFont);
        butRefresh.setText("Refresh");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);
        txtFromDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtFromDatePropertyChange(evt);
            }
        });

        jLabel30.setFont(Global.lableFont);
        jLabel30.setText("From Date");

        jLabel31.setFont(Global.lableFont);
        jLabel31.setText("To Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);
        txtToDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtToDatePropertyChange(evt);
            }
        });

        jLabel32.setFont(Global.lableFont);
        jLabel32.setText("Doctor");

        cboDr.setFont(Global.textFont);
        cboDr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboDr.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDrItemStateChanged(evt);
            }
        });

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboDr, 0, 103, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(butRefresh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addComponent(txtSearch)))
                    .addComponent(jScrollPane2)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboDr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel32)
                        .addComponent(butRefresh)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butBillIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBillIDActionPerformed
        try {
            RegNo regNo = new RegNo(dao, "OT-ID");
            txtBillID.setText(regNo.getRegNo());
            regNo.updateRegNo();
        } catch (Exception ex) {
            log.error("butOTIDActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butBillIDActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void cboGenderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboGenderFocusGained
        focusCtrlName = "cboGender";
    }//GEN-LAST:event_cboGenderFocusGained

    private void cboDoctorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboDoctorFocusGained
        focusCtrlName = "cboDoctor";
    }//GEN-LAST:event_cboDoctorFocusGained

    private void txtContactNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactNoFocusGained
        focusCtrlName = "txtContactNo";
        txtContactNo.selectAll();
    }//GEN-LAST:event_txtContactNoFocusGained

    private void txtReligionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReligionFocusGained
        focusCtrlName = "txtReligion";
        txtReligion.selectAll();
    }//GEN-LAST:event_txtReligionFocusGained

    private void cboCityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboCityFocusGained
        focusCtrlName = "cboCity";
    }//GEN-LAST:event_cboCityFocusGained

    private void txtNationalityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNationalityFocusGained
        focusCtrlName = "txtNationality";
        txtNationality.selectAll();
    }//GEN-LAST:event_txtNationalityFocusGained

    private void txtNIRCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNIRCFocusGained
        focusCtrlName = "txtNIRC";
        txtNIRC.selectAll();
    }//GEN-LAST:event_txtNIRCFocusGained

    private void txtAgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAgeActionPerformed
        if (!txtAge.getText().isEmpty()) {
            calDOB();
        }
        /* String strAge = txtAge.getText().trim().toLowerCase();
         String[] strArray = strAge.split(",");
         int year = 0;
         int month = 0;
         int day = 0;

         for (String strTmp : strArray) {
         if (strTmp.toLowerCase().contains("y")) {
         strTmp = strTmp.replace("y", "");
         year = Integer.parseInt(strTmp);
         } else if (strTmp.toLowerCase().contains("m")) {
         strTmp = strTmp.replace("m", "");
         month = Integer.parseInt(strTmp);
         } else if (strTmp.toLowerCase().contains("d")) {
         strTmp = strTmp.replace("d", "");
         day = Integer.parseInt(strTmp);
         } else {
         year = Integer.parseInt(strTmp);
         }
         }

         String strDate = DateUtil.getDOB1(year, month, day);
         txtDOB.setText(strDate);
         }*/
    }//GEN-LAST:event_txtAgeActionPerformed

    private void txtAgeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAgeFocusGained
        focusCtrlName = "txtAge";
        txtAge.selectAll();
    }//GEN-LAST:event_txtAgeFocusGained

    private void txtDOBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDOBActionPerformed
        if (DateUtil.isValidDate(txtDOB.getText())) {
            lblAgeStr.setText(DateUtil.getAge(txtDOB.getText()));
        } else {
            txtDOB.setText(null);
        }
    }//GEN-LAST:event_txtDOBActionPerformed

    private void txtDOBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDOBFocusGained
        focusCtrlName = "txtDOB";
        txtDOB.selectAll();
    }//GEN-LAST:event_txtDOBFocusGained

    private void txtFatherNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFatherNameFocusGained
        focusCtrlName = "txtFatherName";
        txtFatherName.selectAll();
    }//GEN-LAST:event_txtFatherNameFocusGained

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        focusCtrlName = "txtName";
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void txtRegDateTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegDateTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegDateTimeActionPerformed

    private void bkSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bkSaveActionPerformed
        // TODO add your handling code here:
        saveBkPatient();
    }//GEN-LAST:event_bkSaveActionPerformed

    private void bkDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bkDateMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                bkDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_bkDateMouseClicked

    private void bkDoctorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bkDoctorMouseClicked
        // TODO add your handling code here:
        DoctorSearchFilterDialog dialog = new DoctorSearchFilterDialog(dao, this);
    }//GEN-LAST:event_bkDoctorMouseClicked

    private void bkDoctorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bkDoctorKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_bkDoctorKeyPressed

    private void bkDoctorInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_bkDoctorInputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_bkDoctorInputMethodTextChanged

    private void bkDoctorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bkDoctorFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_bkDoctorFocusGained

    private void bkRegnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bkRegnoMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            bkPTF = true;
            PatientSearch dialog = new PatientSearch(dao, this);
        }

    }//GEN-LAST:event_bkRegnoMouseClicked

    private void bkRegnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bkRegnoActionPerformed
        getPatient();
    }//GEN-LAST:event_bkRegnoActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        if (txtSearch.getText().isEmpty()) {
            sorterGroup.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorterGroup.setRowFilter(swrfGroup);
        } else {
            sorterGroup.setRowFilter(RowFilter.regexFilter(txtSearch.getText()));
        }
        calculateTotalRecord();
    }//GEN-LAST:event_txtSearchKeyReleased

    private void bkpatientNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bkpatientNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bkpatientNameActionPerformed

    private void bkDoctorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bkDoctorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bkDoctorActionPerformed

    private void bkPhoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bkPhoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bkPhoneActionPerformed

    private void txtMonthFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMonthFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMonthFocusGained

    private void txtMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMonthActionPerformed
        // TODO add your handling code here:
        if (!txtMonth.getText().isEmpty()) {
            calDOB();
        }
    }//GEN-LAST:event_txtMonthActionPerformed

    private void txtDayFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDayFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDayFocusGained

    private void txtDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDayActionPerformed
        // TODO add your handling code here:
        if (!txtDay.getText().isEmpty()) {
            calDOB();
        }
    }//GEN-LAST:event_txtDayActionPerformed

    private void tblBookingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBookingMouseClicked
        if (evt.getClickCount() == 2) {
            printVisitSlip();
        }
    }//GEN-LAST:event_tblBookingMouseClicked

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        searchBooking(false);
    }//GEN-LAST:event_butRefreshActionPerformed

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void txtFromDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtFromDatePropertyChange
        // TODO add your handling code here:
        searchBooking(false);
    }//GEN-LAST:event_txtFromDatePropertyChange

    private void txtToDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtToDatePropertyChange
        // TODO add your handling code here:
        searchBooking(false);
    }//GEN-LAST:event_txtToDatePropertyChange

    private void cboDrItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDrItemStateChanged
        // TODO add your handling code here:
        searchBooking(false);
    }//GEN-LAST:event_cboDrItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        searchBooking(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField bkDate;
    private javax.swing.JFormattedTextField bkDoctor;
    private javax.swing.JFormattedTextField bkPhone;
    private javax.swing.JFormattedTextField bkRegno;
    private javax.swing.JButton bkSave;
    private javax.swing.JFormattedTextField bkpatientName;
    private javax.swing.JButton butBillID;
    private javax.swing.JButton butRefresh;
    private javax.swing.JComboBox cboCity;
    private javax.swing.JComboBox cboDoctor;
    private javax.swing.JComboBox<String> cboDr;
    private javax.swing.JComboBox cboGender;
    private javax.swing.JComboBox<String> cboTownship;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAgeStr;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblBooking;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtBillID;
    private javax.swing.JTextField txtContactNo;
    private javax.swing.JFormattedTextField txtDOB;
    private javax.swing.JTextField txtDay;
    private javax.swing.JTextField txtFatherName;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtMonth;
    private javax.swing.JTextField txtNIRC;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNationality;
    private javax.swing.JFormattedTextField txtRegDateTime;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextField txtReligion;
    private javax.swing.JFormattedTextField txtSearch;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JFormattedTextField txtTotalRecord;
    // End of variables declaration//GEN-END:variables
}
