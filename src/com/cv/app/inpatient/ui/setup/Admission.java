/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.inpatient.database.entity.BuildingStructure;
import com.cv.app.inpatient.database.entity.DCHis;
import com.cv.app.inpatient.database.entity.DCRoomTransferHis;
import com.cv.app.inpatient.database.entity.RBooking;
import com.cv.app.inpatient.database.entity.ReCoverAdmissionDeleteLog;
import com.cv.app.inpatient.ui.util.AdmissionSearch;
import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.OPDHis;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.ot.database.entity.OTHis;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CompoundKey;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.VouId;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author winswe
 */
public class Admission extends javax.swing.JPanel implements FormAction,
        KeyPropagate, SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(Admission.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private Ams currPatient = new Ams();
    private final RegNo regNo = new RegNo(dao, "amsNo");
    //private AvailableDoctorTableModel avaDTableModel = new AvailableDoctorTableModel();
    private BestAppFocusTraversalPolicy focusPolicy;
    private String focusCtrlName = "-";
    private boolean bindStatus = false;
    private boolean saveStatus = false;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (!focusCtrlName.equals("-")) {
                if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtAmsNo")) {
                    txtRegNo.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN)
                        && focusCtrlName.equals("txtRegNo")) {
                    txtName.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
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
                    if (!cboDoctor.isPopupVisible()) {
                        txtRegNo.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboDoctor")) {
                    txtContactNo.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtContactNo")) {
                    txtReligion.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtReligion")) {
                    cboCity.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboCity")) {
                    if (!cboCity.isPopupVisible()) {
                        txtNationality.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtNationality")) {
                    txtNIRC.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtNIRC")) {
                    cboGender.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboGender")) {
                    if (!cboGender.isPopupVisible()) {
                        txtAge.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtAge")) {
                    txtDOB.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDOB")) {
                    txtFatherName.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtFatherName")) {
                    txtName.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtName")) {
                    txtRegNo.requestFocus();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void initForFocus() {
        txtAddress.addKeyListener(this);
        txtAge.addKeyListener(this);
        txtAmsNo.addKeyListener(this);
        txtContactNo.addKeyListener(this);
        txtDOB.addKeyListener(this);
        txtFatherName.addKeyListener(this);
        txtNIRC.addKeyListener(this);
        txtName.addKeyListener(this);
        txtNationality.addKeyListener(this);
        txtRegNo.addKeyListener(this);
        txtReligion.addKeyListener(this);

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
    }

    /**
     * Creates new form PatientSetup
     */
    public Admission() {
        initComponents();
        initCombo();
        assignDefaultValue();
        actionMapping();
        //applyFocusPolicy();
        //AddFocusMoveKey();
        //this.setFocusTraversalPolicy(focusPolicy);
        initForFocus();
        lblBooking.setVisible(false);
        butSave.setEnabled(false);
        //cboRTFromRoom.setEnabled(false);
        cboRTFromRoom.setSelectedItem(null);
        cboRTToRoom.setSelectedItem(null);
    }

    @Override
    public void save() {
        if (isValidEntry() && !saveStatus) {
            saveStatus = true;

            try {
                dao.save(currPatient);
                if (lblStatus.getText().equals("NEW")) {
                    regNo.updateRegNo();
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Patient admission number is " + currPatient.getKey().getAmsNo(),
                            "Admission", JOptionPane.INFORMATION_MESSAGE);
                }

                //Update to patient
                //Patient pt = currPatient.getKey().getRegister();
                Patient pt = (Patient) dao.find(Patient.class, currPatient.getKey().getRegister().getRegNo());
                if (currPatient.getDcStatus() == null) {
                    pt.setAdmissionNo(currPatient.getKey().getAmsNo());
                }
                pt.setAddress(currPatient.getAddress());
                pt.setCity(currPatient.getCity());
                pt.setContactNo(currPatient.getContactNo());
                pt.setDob(currPatient.getDob());
                pt.setDoctor(currPatient.getDoctor());
                pt.setFatherName(currPatient.getFatherName());
                pt.setNationality(currPatient.getNationality());
                pt.setNirc(currPatient.getNirc());
                pt.setPatientName(currPatient.getPatientName());
                pt.setReligion(currPatient.getReligion());
                pt.setSex(currPatient.getSex());
                dao.save(pt);

                try {

                    if (cboBooking.getSelectedItem() != null) {
                        String strSql = "update booking_room set check_status = 0 "
                                + " where booking_id =" + ((RBooking) cboBooking.getSelectedItem()).getBookingId() + "";
                        dao.execSql(strSql);
                    }
                    //update building
                    if (cboRoom.getSelectedItem() != null) {
                        String strSql = "update building_structure set reg_no ='" + regNo.getRegNo() + "'"
                                + " where id =" + ((BuildingStructure) cboRoom.getSelectedItem()).getId() + "";
                        dao.execSql(strSql);
                    }
                } catch (Exception ex) {
                    log.error("save1 : " + ex.toString());
                }

                /*if (lblStatus.getText().equals("NEW")) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Patient admission number is " + currPatient.getKey().getAmsNo(),
                            "Admission", JOptionPane.INFORMATION_MESSAGE);
                }*/
                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }

            saveStatus = false;
        } else {
            log.error("double save error : " + saveStatus);
        }
    }

    @Override
    public void newForm() {
        lblStatus.setText("NEW");
        currPatient = new Ams();
        txtAmsNo.setText(null);
        txtAmsDateTime.setText(null);
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
        lblBooking.setVisible(false);
        txtAmsNo.setEnabled(true);
        txtRegNo.setEnabled(true);
        getValidRoom();
        assignDefaultValue();
        cboRoom.setEnabled(false);
        lblRoom.setText(null);
        txtName.requestFocusInWindow();
    }

    @Override
    public void history() {
        AdmissionSearch ps = new AdmissionSearch(dao, this);

    }

    @Override
    public void delete() {
        String amsNo = txtAmsNo.getText();
        String regNo = txtRegNo.getText();

        if (!amsNo.isEmpty()) {

            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Admission delete", JOptionPane.YES_NO_OPTION);
            boolean status = true;
            if (yes_no == 0) {
                try {
                    List<SaleHis> listSH = dao.findAllHSQL(
                            "select o from SaleHis o where o.deleted = false and o.admissionNo = '"
                            + amsNo + "' and o.patientId.regNo = '" + regNo + "'");
                    if (listSH != null) {
                        if (!listSH.isEmpty()) {
                            status = false;
                            JOptionPane.showMessageDialog(Util1.getParent(), "Pharmacy Transaction have. You cannot delete.",
                                    "Admission delete", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (status) {
                        List<OPDHis> listOPD = dao.findAllHSQL(
                                "select o from OPDHis o where o.deleted = false and o.admissionNo = '"
                                + amsNo + "' and o.patient.regNo = '" + regNo + "'");
                        if (!listOPD.isEmpty()) {
                            status = false;
                            JOptionPane.showMessageDialog(Util1.getParent(), "OPD Transaction have. You cannot delete.",
                                    "Admission delete", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (status) {
                        List<OTHis> listOT = dao.findAllHSQL(
                                "select o from OTHis o where o.deleted = false and o.admissionNo = '"
                                + amsNo + "' and o.patient.regNo = '" + regNo + "'");
                        if (!listOT.isEmpty()) {
                            status = false;
                            JOptionPane.showMessageDialog(Util1.getParent(), "OT Transaction have. You cannot delete.",
                                    "Admission delete", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (status) {
                        List<DCHis> listDC = dao.findAllHSQL(
                                "select o from DCHis o where o.deleted = false and o.admissionNo = '"
                                + amsNo + "' and o.patient.regNo = '" + regNo + "'");
                        if (!listDC.isEmpty()) {
                            status = false;
                            JOptionPane.showMessageDialog(Util1.getParent(), "DC Transaction have. You cannot delete.",
                                    "Admission delete", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    log.error("delete1 : " + ex.getMessage());
                } finally {
                    dao.close();
                }
                if (status) {
                    String strSql = "delete from admission where ams_no = '" + amsNo + "' and reg_no = '" + regNo + "'";
                    try {
                        Patient pt = (Patient) dao.find(Patient.class, regNo);
                        pt.setAdmissionNo(null);
                        dao.save(pt);
                        dao.execSql(strSql);
                        newForm();
                    } catch (Exception ex) {
                        log.error("delete : " + ex);
                    } finally {
                        dao.close();
                    }
                }
            }
        }
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
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
            BindingUtil.BindCombo(cboGender, dao.findAll("Gender"));
            AutoCompleteDecorator.decorate(cboGender);
            BindingUtil.BindCombo(cboDoctor, dao.findAllHSQL("select o from Doctor o "
                    + "where o.active = true order by o.doctorName"));
            AutoCompleteDecorator.decorate(cboDoctor);
            BindingUtil.BindCombo(cboTownship,
                    dao.findAllHSQL("select o from Township o order by o.townshipName"));
            AutoCompleteDecorator.decorate(cboTownship);
            BindingUtil.BindCombo(cboType,
                    dao.findAllHSQL("select o from CustomerGroup o order by o.groupName"));
            AutoCompleteDecorator.decorate(cboType);
            BindingUtil.BindCombo(cboBooking,
                    dao.findAllHSQL("select o from RBooking o where checkStatus = true order by o.bookingName"));
            AutoCompleteDecorator.decorate(cboBooking);
            getValidRoom();
            bindStatus = true;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("PatientSearch")) {
            //lblStatus.setText("EDIT");
            Patient pt = (Patient) selectObj;

            txtRegNo.setText(pt.getRegNo());
            txtName.setText(pt.getPatientName());
            txtRegDateTime.setText(DateUtil.toDateTimeStr(pt.getRegDate(),
                    "dd/MM/yyyy HH:mm:ss"));
            txtFatherName.setText(pt.getFatherName());
            txtDOB.setText(DateUtil.toDateStr(pt.getDob()));
            txtAge.setText(DateUtil.getAge(DateUtil.toDateStr(pt.getDob())));
            cboGender.setSelectedItem(pt.getSex());
            txtNIRC.setText(pt.getNirc());
            txtNationality.setText(pt.getNationality());
            txtAddress.setText(pt.getAddress());
            cboCity.setSelectedItem(pt.getCity());
            txtReligion.setText(pt.getReligion());
            txtContactNo.setText(pt.getContactNo());
            cboDoctor.setSelectedItem(pt.getDoctor());
            cboTownship.setSelectedItem(pt.getTownship());
            cboType.setSelectedItem(pt.getPtType());
            currPatient.getKey().setRegister(pt);
            dao.close();
            cboRoom.setSelectedItem(null);
            cboRoom.setEnabled(true);
        } else if (source.equals("AdmissionSearch")) {
            lblStatus.setText("EDIT");
            currPatient = (Ams) selectObj;
            txtAmsNo.setText(currPatient.getKey().getAmsNo());
            txtAmsDateTime.setText(DateUtil.toDateTimeStr(currPatient.getAmsDate(),
                    "dd/MM/yyyy HH:mm:ss"));
            txtRegNo.setText(currPatient.getKey().getRegister().getRegNo());
            txtName.setText(currPatient.getKey().getRegister().getPatientName());
            txtRegDateTime.setText(DateUtil.toDateTimeStr(currPatient.getKey().getRegister().getRegDate(),
                    "dd/MM/yyyy HH:mm:ss"));
            txtFatherName.setText(currPatient.getKey().getRegister().getFatherName());
            txtDOB.setText(DateUtil.toDateStr(currPatient.getKey().getRegister().getDob()));
            txtAge.setText(DateUtil.getAge(DateUtil.toDateStr(currPatient.getKey().getRegister().getDob())));
            cboGender.setSelectedItem(currPatient.getKey().getRegister().getSex());
            txtNIRC.setText(currPatient.getKey().getRegister().getNirc());
            txtNationality.setText(currPatient.getKey().getRegister().getNationality());
            txtAddress.setText(currPatient.getKey().getRegister().getAddress());
            cboCity.setSelectedItem(currPatient.getKey().getRegister().getCity());
            txtReligion.setText(currPatient.getKey().getRegister().getReligion());
            txtContactNo.setText(currPatient.getKey().getRegister().getContactNo());
            cboDoctor.setSelectedItem(currPatient.getDoctor());
            cboTownship.setSelectedItem(currPatient.getTownship());
            cboType.setSelectedItem(currPatient.getPtType());
            cboRoom.setSelectedItem(currPatient.getBuildingStructure());
            cboRoom.setEnabled(currPatient.getBuildingStructure() == null);
            txtAmsNo.setEnabled(false);
            txtRegNo.setEnabled(false);
            dao.close();
        }
    }

    private void assignDefaultValue() {
        txtAmsDateTime.setText(DateUtil.getTodayDateStr("dd/MM/yyyy HH:mm:ss"));
        //txtAmsNo.setText(regNo.getRegNo());
        cboCity.setSelectedItem(null);
        cboDoctor.setSelectedItem(null);
        cboGender.setSelectedItem(null);
        cboTownship.setSelectedItem(null);
        cboType.setSelectedItem(null);
        cboRoom.setSelectedItem(null);
        cboBooking.setSelectedItem(null);
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
        } else if (DateUtil.toDateTime(txtAmsDateTime.getText()) == null) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Invalid admission date.", "Admission Date",
                    JOptionPane.ERROR_MESSAGE);
            txtAmsDateTime.requestFocusInWindow();
        } else if (cboRoom.getSelectedItem() instanceof BuildingStructure) {
            status = isValidRoom((BuildingStructure) cboRoom.getSelectedItem());
        }
        if (status) {
            if (Util1.nullToBlankStr(txtAmsNo.getText()).trim().equals("")) {
                currPatient.getKey().setAmsNo(regNo.getRegNo());
                String tmpAmsNo = currPatient.getKey().getAmsNo();
                tmpAmsNo = tmpAmsNo.replace(regNo.getPeriod(), "");
                if (tmpAmsNo.equals("0")) {
                    currPatient.getKey().setAmsNo(regNo.getRegNo());
                    tmpAmsNo = currPatient.getKey().getAmsNo();
                    if (tmpAmsNo.equals("0")) {
                        log.error("generateRegNo : " + "Admission number generate to zero.");
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Error in id generation. Please exit the program and try again", "Duplicate",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                    }
                }
            } else {
                currPatient.getKey().setAmsNo(txtAmsNo.getText());
            }

            if (txtAmsNo.getText().isEmpty()) {
                try {
                    List<Ams> listAMS = dao.findAllHSQL("select o from Ams o where o.key.amsNo = '" + currPatient.getKey().getAmsNo() + "'");
                    if (listAMS != null) {
                        if (!listAMS.isEmpty()) {
                            log.error("Reg Check : " + currPatient.getKey().getRegister().getRegNo() + ";" + currPatient.getPatientName());
                            resetAmsNo(regNo.getPeriod());
                            currPatient.getKey().setAmsNo(regNo.getRegNo());
                        }
                    }
                } catch (Exception ex) {
                    log.error("isValidEntry : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }

            currPatient.setAmsDate(DateUtil.toDateTime(txtAmsDateTime.getText()));
            currPatient.setAddress(txtAddress.getText().trim());
            currPatient.setCity((City) cboCity.getSelectedItem());
            currPatient.setContactNo(txtContactNo.getText().trim());
            currPatient.setDob(DateUtil.toDate(txtDOB.getText()));
            if (cboDoctor.getSelectedItem() instanceof Doctor) {
                currPatient.setDoctor((Doctor) cboDoctor.getSelectedItem());
            } else {
                currPatient.setDoctor(null);
            }
            currPatient.setFatherName(txtFatherName.getText().trim());
            currPatient.setNationality(txtNationality.getText().trim());
            currPatient.setNirc(txtNIRC.getText().trim());
            currPatient.setPatientName(txtName.getText().trim());
            currPatient.setReligion(txtReligion.getText().trim());
            if (txtAge.getText().isEmpty()) {
                currPatient.setAge(null);
            } else {
                Integer age = NumberUtil.getNumber(txtAge.getText());
                currPatient.setAge(age);
            }

            if (cboGender.getSelectedItem() == null) {
                currPatient.setSex(null);
            } else {
                currPatient.setSex((Gender) cboGender.getSelectedItem());
            }

            if (cboTownship.getSelectedItem() == null) {
                currPatient.setTownship(null);
            } else {
                currPatient.setTownship((Township) cboTownship.getSelectedItem());
            }
            if (cboType.getSelectedItem() == null) {
                currPatient.setPtType(null);
            } else {
                currPatient.setPtType((CustomerGroup) cboType.getSelectedItem());
            }
            if (cboRoom.getSelectedItem() == null) {
                currPatient.setBuildingStructure(null);
            } else {
                currPatient.setBuildingStructure((BuildingStructure) cboRoom.getSelectedItem());
            }
            if (lblStatus.getText().equals("NEW")) {
                currPatient.setCreatedDate(new Date());
            }

        }
        return status;
    }

    private boolean isValidRoom(BuildingStructure b) {
        if (lblStatus.getText().equals("NEW")) {
            String sql = "select reg_no from building_structure where id = " + b.getId() + "";

            try {
                ResultSet rs = dao.execSQL(sql);
                if (rs.next()) {
                    String no = rs.getString("reg_no");
                    if (no != null) {
                        JOptionPane.showMessageDialog(this, no + " is admitted at " + b.getDescription());
                        return false;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());

            }
        }
        return true;
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtAmsNo);
        focusOrder.add(txtRegNo);
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

    private final Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };

    private final Action actionHistory = new AbstractAction() {
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

        formActionKeyMapping(txtAmsNo);
        formActionKeyMapping(txtRegNo);
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

    private void getPatientList() {
        /*if (Util1.hashPrivilege("SaleCustomerChange")) {
         if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
         PatientSearch dialog = new PatientSearch(dao, this);
         } else if (Util1.getPropValue("system.app.usage.type").equals("School")) {
         MarchantSearch dialog = new MarchantSearch(dao, this);
         } else {
         UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
         }
         }*/
        PatientSearch ps = new PatientSearch(dao, this);
    }

    private void getPatient() {
        if (!Util1.isNullOrEmpty(txtAmsNo.getText())) {
            return;
        }

        if (txtRegNo.getText() != null && !txtRegNo.getText().isEmpty()) {
            try {
                Patient pt;
                dao.open();
                pt = (Patient) dao.find(Patient.class, txtRegNo.getText());
                dao.close();

                if (pt == null) {
                    txtRegNo.setText(null);
                    txtName.setText(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                } else if (!Util1.getNullTo(pt.getAdmissionNo(), "").trim().isEmpty()) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "This patient already admitted.",
                            "Admitted patient", JOptionPane.ERROR_MESSAGE);
                    newForm();
                } else {
                    selected("PatientSearch", pt);
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtRegNo.setText(null);
            txtName.setText(null);
        }
    }

    private void getValidRoom() {
        try {
            List<BuildingStructure> list = dao.findAllHSQL("select o from BuildingStructure o where o.regNo is null "
                    + " and o.structureType.typeId in (3,4) order by o.description");
            BindingUtil.BindCombo(cboRoom, list);
            AutoCompleteDecorator.decorate(cboRoom);
            lblRoom.setText("Available Room : " + String.valueOf(list.size()));
        } catch (Exception ex) {
            log.error("verifyRoom : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        txtRTRegNo.setText("");
        txtRTAdmissionNo.setText("");
        txtRTName.setText("");
        butSave.setEnabled(false);
        cboRTFromRoom.setEnabled(false);
        cboRTToRoom.setEnabled(false);
        cboRTFromRoom.setSelectedItem(null);
        cboRTToRoom.setSelectedItem(null);
        txtAmsNo.setEnabled(true);
        txtRegNo.setEnabled(true);
    }

    private void transferSave() {
        BuildingStructure toRoom = (BuildingStructure) cboRTToRoom.getSelectedItem();
        if (toRoom == null) {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Invalid transfer room.",
                    "Transfer Room", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                String tmpRegNo = txtRTRegNo.getText().trim();
                String admNo = txtRTAdmissionNo.getText().trim();
                Patient pt = (Patient) dao.find(Patient.class, tmpRegNo);
                AdmissionKey key = new AdmissionKey();
                key.setRegister(pt);
                key.setAmsNo(admNo);
                Ams admission = (Ams) dao.find(Ams.class, key);
                DCRoomTransferHis his = new DCRoomTransferHis();
                BuildingStructure fromRoom = (BuildingStructure) cboRTFromRoom.getSelectedItem();
                admission.setBuildingStructure(toRoom);
                toRoom.setRegNo(tmpRegNo);

                if (fromRoom != null) {
                    fromRoom.setRegNo(null);
                    his.setFromRoom(fromRoom.getId());
                }
                his.setAdmissionNo(admNo);
                his.setToRoom(toRoom.getId());
                his.setRegNo(tmpRegNo);
                his.setTranDate(new Date());
                his.setUserId(Global.loginUser.getUserId());

                if (isValidRoom(toRoom)) {
                    if (fromRoom != null) {
                        dao.save(fromRoom);
                    }
                    dao.save(toRoom);
                    dao.save(his);
                    dao.save(admission);
                }
                clear();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("transferSave : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void resetAmsNo(String period) {
        String strSql = "select max(cast(replace(ams_no,'" + period
                + "','') as unsigned)) as sr from admission where ams_no like '%" + period + "'";
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    Integer lastNo = rs.getInt("sr") + 1;
                    String vouType = "amsNo";
                    VouId vouId = (VouId) dao.find(VouId.class,
                            new CompoundKey(vouType, vouType, period));
                    if (vouId == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Duplicate admission number. You cannot save.", "Duplicate",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                    } else {
                        vouId.setVouNo(lastNo);
                        dao.save(vouId);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("resetAmsNo : " + ex.getMessage());
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Duplicate registeration number. You cannot save.", "Duplicate",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } finally {
            dao.close();
        }
    }

    private void recoverAdmissionDelete() {
        String tmpRegNo = txtRCARegNo.getText().trim();
        String admNo = txtRCADAdmNo.getText().trim();
        try {
            Patient pt = (Patient) dao.find(Patient.class, tmpRegNo);
            if (pt == null) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "Invalid reg no.", "Reg No.",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                AdmissionKey key = new AdmissionKey();
                key.setRegister(pt);
                key.setAmsNo(admNo);
                Ams ams = (Ams) dao.find(Ams.class, key);
                if (ams == null) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid admission no.", "Admission No.",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    pt.setAdmissionNo(admNo);
                    ams.setDcStatus(null);
                    ams.setDcDateTime(null);
                    dao.save(pt);
                    dao.save(ams);

                    ReCoverAdmissionDeleteLog rcad = new ReCoverAdmissionDeleteLog();
                    rcad.setAmsNo(admNo);
                    rcad.setLogDate(new Date());
                    rcad.setMachineId(Global.machineId);
                    rcad.setRegNo(pt.getRegNo());
                    rcad.setUserId(Global.loginUser.getUserId());
                    dao.save(rcad);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Recover Success.", "Admission Recover",
                            JOptionPane.ERROR_MESSAGE);

                    txtRCARegNo.setText(null);
                    txtRCADAdmNo.setText(null);
                }
            }
        } catch (Exception ex) {
            log.error("recoverAdmissionDelete : " + ex.getMessage());
            JOptionPane.showMessageDialog(Util1.getParent(),
                    ex.getMessage(), "Recover Admission",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            dao.close();
        }
    }

    private void initRoom() throws Exception {
        BindingUtil.BindCombo(cboRTFromRoom,
                dao.findAllHSQL("select o from BuildingStructure o where "
                        + " o.structureType.typeId in (3,4) order by o.description"));
        AutoCompleteDecorator.decorate(cboRTFromRoom);
        BindingUtil.BindCombo(cboRTToRoom,
                dao.findAllHSQL("select o from BuildingStructure o where o.regNo is null "
                        + " and o.structureType.typeId in (3,4) order by o.description"));
        AutoCompleteDecorator.decorate(cboRTToRoom);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        txtRTRegNo = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtRTAdmissionNo = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtRTName = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        cboRTFromRoom = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        cboRTToRoom = new javax.swing.JComboBox<>();
        butSave = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtAmsDateTime = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cboCity = new javax.swing.JComboBox();
        txtRegNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtReligion = new javax.swing.JTextField();
        txtRegDateTime = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtContactNo = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtFatherName = new javax.swing.JTextField();
        cboDoctor = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtDOB = new javax.swing.JFormattedTextField();
        cboTownship = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtAge = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        cboRoom = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        cboBooking = new javax.swing.JComboBox();
        cboGender = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        txtNIRC = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNationality = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtAmsNo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblBooking = new javax.swing.JLabel();
        lblRoom = new javax.swing.JLabel();
        panelReCoverAdmissionDelete = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        txtRCARegNo = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtRCADAdmNo = new javax.swing.JTextField();
        butReCover = new javax.swing.JButton();
        panelReCoverAdmissionDelete1 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        txtRCARegNo1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtRCADAdmNo1 = new javax.swing.JTextField();
        butReCover1 = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Room Transfer"));

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Reg No.");

        txtRTRegNo.setFont(Global.textFont);
        txtRTRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRTRegNoActionPerformed(evt);
            }
        });

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Ams No");

        txtRTAdmissionNo.setEditable(false);
        txtRTAdmissionNo.setFont(Global.textFont);

        jLabel23.setFont(Global.lableFont);
        jLabel23.setText("Name ");

        txtRTName.setEditable(false);
        txtRTName.setFont(Global.textFont);

        jLabel24.setFont(Global.lableFont);
        jLabel24.setText("From ");

        cboRTFromRoom.setFont(Global.textFont);
        cboRTFromRoom.setEnabled(false);

        jLabel25.setFont(Global.lableFont);
        jLabel25.setText("To");

        cboRTToRoom.setFont(Global.textFont);
        cboRTToRoom.setEnabled(false);

        butSave.setFont(Global.lableFont);
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butClear.setFont(Global.lableFont);
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboRTFromRoom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRTRegNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboRTToRoom, 0, 137, Short.MAX_VALUE)
                    .addComponent(txtRTAdmissionNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)
                        .addGap(0, 131, Short.MAX_VALUE))
                    .addComponent(txtRTName))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboRTFromRoom, cboRTToRoom});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtRTRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(txtRTAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtRTName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(butSave)
                                .addComponent(butClear))
                            .addComponent(cboRTToRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel25))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24)
                        .addComponent(cboRTFromRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtAmsDateTime.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("City");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Reg-No");

        cboCity.setEditable(true);
        cboCity.setFont(Global.textFont);
        cboCity.setEnabled(true);
        cboCity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboCityFocusGained(evt);
            }
        });

        txtRegNo.setFont(Global.textFont);
        txtRegNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRegNoFocusGained(evt);
            }
        });
        txtRegNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtRegNoMouseClicked(evt);
            }
        });
        txtRegNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNoActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Reg-Date Time ");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Religion");

        txtReligion.setFont(Global.textFont);
        txtReligion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtReligionFocusGained(evt);
            }
        });

        txtRegDateTime.setEditable(false);
        txtRegDateTime.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Contact No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtContactNo.setFont(Global.textFont);
        txtContactNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtContactNoFocusGained(evt);
            }
        });

        txtName.setFont(Global.textFont);
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Father Name ");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Doctor ");

        txtFatherName.setFont(Global.textFont);
        txtFatherName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFatherNameFocusGained(evt);
            }
        });

        cboDoctor.setEditable(true);
        cboDoctor.setFont(Global.textFont);
        cboDoctor.setEnabled(true);
        cboDoctor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboDoctorFocusGained(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("DOB");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Township");

        try {
            txtDOB.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            System.out.println("Registration : " + ex.toString());
        }
        txtDOB.setFont(Global.textFont);
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

        cboTownship.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Age ");

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Type");

        txtAge.setFont(Global.textFont);
        txtAge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAgeFocusGained(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Gender");

        cboType.setFont(Global.textFont);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Room No.");

        cboRoom.setEditable(true);
        cboRoom.setFont(Global.textFont);
        cboRoom.setEnabled(false);
        cboRoom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboRoomFocusGained(evt);
            }
        });
        cboRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboRoomActionPerformed(evt);
            }
        });

        jLabel19.setFont(Global.lableFont);
        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Booking");

        cboBooking.setEditable(true);
        cboBooking.setFont(Global.textFont);
        cboBooking.setEnabled(true);
        cboBooking.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboBookingFocusGained(evt);
            }
        });

        cboGender.setEditable(true);
        cboGender.setFont(Global.textFont);
        cboGender.setEnabled(true);
        cboGender.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboGenderFocusGained(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("NIRC");

        txtNIRC.setFont(Global.textFont);
        txtNIRC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNIRCFocusGained(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Nationality");

        txtNationality.setFont(Global.textFont);
        txtNationality.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNationalityFocusGained(evt);
            }
        });

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Ams-No");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Address");

        txtAmsNo.setFont(Global.textFont);

        txtAddress.setColumns(20);
        txtAddress.setFont(Global.textFont);
        txtAddress.setRows(5);
        jScrollPane1.setViewportView(txtAddress);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Ams-Date Time ");

        jLabel19.setFont(Global.lableFont);
        lblBooking.setFont(new java.awt.Font("Zawgyi-One", 1, 14)); // NOI18N
        lblBooking.setForeground(new java.awt.Color(255, 51, 51));
        lblBooking.setText("Booking Status");

        jLabel19.setFont(Global.lableFont);
        lblRoom.setFont(Global.textFont);
        lblRoom.setForeground(new java.awt.Color(0, 0, 0));
        lblRoom.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cboBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblBooking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addComponent(lblRoom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(txtAmsNo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtRegDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtAmsDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(txtNIRC, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel8)
                            .addGap(18, 18, 18)
                            .addComponent(txtNationality))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(txtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel5)
                            .addGap(18, 18, 18)
                            .addComponent(cboGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(txtFatherName, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(lblStatus))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtReligion, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 311, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(lblRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBooking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(lblBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(txtAmsNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(txtAmsDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtRegDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel2)
                    .addGap(18, 18, 18)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(cboGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txtNIRC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(txtNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(cboCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(txtReligion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblStatus))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel14)
                        .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(152, Short.MAX_VALUE)))
        );

        panelReCoverAdmissionDelete.setBorder(javax.swing.BorderFactory.createTitledBorder("Re-Cover Admission Delete"));

        jLabel26.setFont(Global.lableFont);
        jLabel26.setText("Reg No. ");

        txtRCARegNo.setFont(Global.textFont);

        jLabel27.setFont(Global.lableFont);
        jLabel27.setText("Admission No.");

        txtRCADAdmNo.setFont(Global.textFont);

        butReCover.setFont(Global.lableFont);
        butReCover.setText("Re-Cover");
        butReCover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butReCoverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelReCoverAdmissionDeleteLayout = new javax.swing.GroupLayout(panelReCoverAdmissionDelete);
        panelReCoverAdmissionDelete.setLayout(panelReCoverAdmissionDeleteLayout);
        panelReCoverAdmissionDeleteLayout.setHorizontalGroup(
            panelReCoverAdmissionDeleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReCoverAdmissionDeleteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRCARegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRCADAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butReCover)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelReCoverAdmissionDeleteLayout.setVerticalGroup(
            panelReCoverAdmissionDeleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReCoverAdmissionDeleteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelReCoverAdmissionDeleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtRCARegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(txtRCADAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butReCover))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelReCoverAdmissionDelete1.setBorder(javax.swing.BorderFactory.createTitledBorder("Re-Cover Admission Delete"));

        jLabel28.setText("Reg No. ");
        panelReCoverAdmissionDelete1.add(jLabel28);
        panelReCoverAdmissionDelete1.add(txtRCARegNo1);

        jLabel29.setText("Admission No.");
        panelReCoverAdmissionDelete1.add(jLabel29);
        panelReCoverAdmissionDelete1.add(txtRCADAdmNo1);

        butReCover1.setText("Re-Cover");
        butReCover1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butReCoverActionPerformed(evt);
            }
        });
        panelReCoverAdmissionDelete1.add(butReCover1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelReCoverAdmissionDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelReCoverAdmissionDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDOBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDOBActionPerformed
        if (DateUtil.isValidDate(txtDOB.getText())) {
            txtAge.setText(DateUtil.getAge(txtDOB.getText()));
        } else {
            txtDOB.setText(null);
        }
    }//GEN-LAST:event_txtDOBActionPerformed

    private void txtRegNoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtRegNoMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            if (!Util1.isNullOrEmpty(txtAmsNo.getText())) {
                return;
            }
            getPatientList();
        }
    }//GEN-LAST:event_txtRegNoMouseClicked

    private void txtRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNoActionPerformed
        getPatient();
    }//GEN-LAST:event_txtRegNoActionPerformed

    private void txtRegNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRegNoFocusGained
        focusCtrlName = "txtRegNo";
        txtRegNo.selectAll();
    }//GEN-LAST:event_txtRegNoFocusGained

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        focusCtrlName = "txtName";
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void txtFatherNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFatherNameFocusGained
        focusCtrlName = "txtFatherName";
        txtFatherName.selectAll();
    }//GEN-LAST:event_txtFatherNameFocusGained

    private void txtDOBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDOBFocusGained
        focusCtrlName = "txtDOB";
        txtDOB.selectAll();
    }//GEN-LAST:event_txtDOBFocusGained

    private void txtAgeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAgeFocusGained
        focusCtrlName = "txtAge";
        txtAge.selectAll();
    }//GEN-LAST:event_txtAgeFocusGained

    private void cboGenderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboGenderFocusGained
        focusCtrlName = "cboGender";
    }//GEN-LAST:event_cboGenderFocusGained

    private void txtNIRCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNIRCFocusGained
        focusCtrlName = "txtNIRC";
        txtNIRC.selectAll();
    }//GEN-LAST:event_txtNIRCFocusGained

    private void txtNationalityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNationalityFocusGained
        focusCtrlName = "txtNationality";
        txtNationality.selectAll();
    }//GEN-LAST:event_txtNationalityFocusGained

    private void cboCityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboCityFocusGained
        focusCtrlName = "cboCity";
    }//GEN-LAST:event_cboCityFocusGained

    private void txtReligionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReligionFocusGained
        focusCtrlName = "txtReligion";
        txtReligion.selectAll();
    }//GEN-LAST:event_txtReligionFocusGained

    private void txtContactNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactNoFocusGained
        focusCtrlName = "txtContactNo";
        txtContactNo.selectAll();
    }//GEN-LAST:event_txtContactNoFocusGained

    private void cboDoctorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboDoctorFocusGained
        focusCtrlName = "cboDoctor";
    }//GEN-LAST:event_cboDoctorFocusGained

    private void cboRoomFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboRoomFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_cboRoomFocusGained

    private void cboBookingFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboBookingFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_cboBookingFocusGained

    private void cboRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboRoomActionPerformed
        // TODO add your handling code here:
        if (bindStatus) {
            if (cboRoom.getSelectedItem() != null) {
                if (cboRoom.getSelectedItem() instanceof BuildingStructure) {
                    try {
                        List<RBooking> listBR = dao.findAllHSQL("select o from RBooking o "
                                + " where o.buildingStructure.id = " + ((BuildingStructure) cboRoom.getSelectedItem()).getId() + ""
                                + " and o.checkStatus = 1");
                        if (!listBR.isEmpty()) {
                            lblBooking.setVisible(true);
                            lblBooking.setText("This room have booking : " + listBR.get(0).getBookingName());
                        } else {
                            lblBooking.setVisible(false);
                        }
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(Admission.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }
    }//GEN-LAST:event_cboRoomActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtRTRegNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRTRegNoActionPerformed
        if (!txtRTRegNo.getText().trim().isEmpty()) {
            try {
                String tmpRegNo = txtRTRegNo.getText().trim();
                Patient pt = (Patient) dao.find(Patient.class, tmpRegNo);
                if (pt != null) {
                    if (Util1.getNullTo(pt.getAdmissionNo(), "").isEmpty()) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "This patient is not admitted.",
                                "Admitted Patient", JOptionPane.ERROR_MESSAGE);
                        clear();
                    } else {
                        initRoom();
                        cboRTToRoom.setEnabled(true);
                        butSave.setEnabled(true);
                        String admNo = pt.getAdmissionNo();
                        txtRTRegNo.setText(pt.getRegNo());
                        txtRTAdmissionNo.setText(admNo);
                        txtRTName.setText(pt.getPatientName());
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(pt);
                        key.setAmsNo(admNo);
                        Ams admission = (Ams) dao.find(Ams.class, key);
                        BuildingStructure bs = admission.getBuildingStructure();
                        if (bs == null) {
                            cboRTFromRoom.setSelectedItem(null);
                        } else {
                            cboRTFromRoom.setSelectedItem(bs);
                        }
                        cboRTFromRoom.setEnabled(false);
                        BindingUtil.BindCombo(cboRTToRoom,
                                dao.findAllHSQL("select o from BuildingStructure o where o.regNo is null "
                                        + " and o.structureType.typeId in (3,4) order by o.description"));
                    }
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                    clear();
                }
            } catch (Exception ex) {
                log.error("Patient Search : " + ex.toString());
            } finally {
                dao.close();
            }
        } else {
            clear();
        }
    }//GEN-LAST:event_txtRTRegNoActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        transferSave();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butReCoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butReCoverActionPerformed
        recoverAdmissionDelete();
    }//GEN-LAST:event_butReCoverActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butReCover;
    private javax.swing.JButton butReCover1;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox cboBooking;
    private javax.swing.JComboBox cboCity;
    private javax.swing.JComboBox cboDoctor;
    private javax.swing.JComboBox cboGender;
    private javax.swing.JComboBox<String> cboRTFromRoom;
    private javax.swing.JComboBox<String> cboRTToRoom;
    private javax.swing.JComboBox cboRoom;
    private javax.swing.JComboBox<String> cboTownship;
    private javax.swing.JComboBox<String> cboType;
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
    private javax.swing.JLabel jLabel19;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblBooking;
    private javax.swing.JLabel lblRoom;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelReCoverAdmissionDelete;
    private javax.swing.JPanel panelReCoverAdmissionDelete1;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtAge;
    private javax.swing.JFormattedTextField txtAmsDateTime;
    private javax.swing.JTextField txtAmsNo;
    private javax.swing.JTextField txtContactNo;
    private javax.swing.JFormattedTextField txtDOB;
    private javax.swing.JTextField txtFatherName;
    private javax.swing.JTextField txtNIRC;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNationality;
    private javax.swing.JTextField txtRCADAdmNo;
    private javax.swing.JTextField txtRCADAdmNo1;
    private javax.swing.JTextField txtRCARegNo;
    private javax.swing.JTextField txtRCARegNo1;
    private javax.swing.JTextField txtRTAdmissionNo;
    private javax.swing.JTextField txtRTName;
    private javax.swing.JTextField txtRTRegNo;
    private javax.swing.JFormattedTextField txtRegDateTime;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextField txtReligion;
    // End of variables declaration//GEN-END:variables
}
