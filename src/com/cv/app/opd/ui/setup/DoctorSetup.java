/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.opd.database.entity.Initial;
import com.cv.app.opd.database.entity.Speciality;
import com.cv.app.opd.database.entity.DoctorFeesMapping;
import com.cv.app.opd.database.entity.DoctorFeesMappingDC;
import com.cv.app.opd.database.entity.DoctorFeesMappingOT;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.inpatient.database.entity.DCHis;
import com.cv.app.inpatient.ui.common.DCTableCellEditor;
import com.cv.app.opd.database.entity.OPDHis;
import com.cv.app.opd.ui.common.DCDoctorFeesMapTableModel;
import com.cv.app.opd.ui.common.OTDoctorFeesMapTableModel;
import com.cv.app.opd.ui.common.DoctorFeesMapTableModel;
import com.cv.app.opd.ui.common.DoctorTableModel;
import com.cv.app.opd.ui.common.OPDTableCellEditor;
import com.cv.app.opd.ui.util.DoctorSetupFilterDialog;
import com.cv.app.ot.database.entity.OTHis;
import com.cv.app.ot.ui.common.OTTableCellEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author winswe
 */
public class DoctorSetup extends javax.swing.JPanel implements FormAction, KeyPropagate {

    static Logger log = Logger.getLogger(DoctorSetup.class.getName());
    private Doctor currDoctor = new Doctor();
    private final AbstractDataAccess dao = Global.dao;
    private int selectRow = -1;
    private final DoctorTableModel tableModel = new DoctorTableModel();
    private DoctorFeesMapTableModel feesMapTableModel = new DoctorFeesMapTableModel(dao);
    private final DCDoctorFeesMapTableModel dcMapTableModel = new DCDoctorFeesMapTableModel(dao);
    private final OTDoctorFeesMapTableModel otMapTableModel = new OTDoctorFeesMapTableModel(dao);
    private final TableRowSorter<TableModel> sorter;
    private boolean statusFilter = false;
    private BestAppFocusTraversalPolicy focusPolicy;
    private final StartWithRowFilter swrf;

    /**
     * Creates new form DoctorSetup
     */
    public DoctorSetup() {
        initComponents();
        initCombo();
        initTable();
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblDoctor.getModel());
        tblDoctor.setRowSorter(sorter);
        addTableSelection();
        getDoctorId();
        lblStatus.setText("NEW");
        chkActive.setSelected(true);
        feesMapTableModel.addNewRow();
        dcMapTableModel.addNewRow();
        otMapTableModel.addNewRow();
        actionMapping();
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboInitial, dao.findAll("Initial"));
            new ComBoBoxAutoComplete(cboInitial);
            BindingUtil.BindCombo(cboSex, dao.findAll("Gender"));
            new ComBoBoxAutoComplete(cboSex);
            BindingUtil.BindCombo(cboSpeciality, dao.findAll("Speciality"));
            new ComBoBoxAutoComplete(cboSpeciality);
            new ComBoBoxAutoComplete(cboType);
            cboType.setSelectedItem(null);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void setFocus() {
        cboInitial.requestFocusInWindow();
    }

    private void clear() {
        selectRow = -1;
        txtName.setText(null);
        txtCode.setText(null);
        cboSpeciality.setSelectedItem(null);
        txtNIRC.setText(null);
        lblStatus.setText("NEW");
        chkActive.setSelected(true);
        currDoctor = new Doctor();
        feesMapTableModel.clear();
        dcMapTableModel.clear();
        otMapTableModel.clear();
        tblDoctor.clearSelection();
        getDoctorId();
        System.gc();
        setFocus();
    }

    private void initTable() {
        try {
            tableModel.setListDoctor(dao.findAllHSQL(
                    "select o from Doctor o order by o.doctorName"
            ));

            tblDoctor.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblDoctor.getColumnModel().getColumn(1).setPreferredWidth(30);
            tblDoctor.getColumnModel().getColumn(2).setPreferredWidth(150);
            tblDoctor.getColumnModel().getColumn(3).setPreferredWidth(10);

            tblFeesMap.getColumnModel().getColumn(0).setPreferredWidth(150);
            tblFeesMap.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblFeesMap.getColumnModel().getColumn(0).setCellEditor(
                    new OPDTableCellEditor(dao));

            tblDC.getColumnModel().getColumn(0).setPreferredWidth(150);
            tblDC.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblDC.getColumnModel().getColumn(0).setCellEditor(
                    new DCTableCellEditor(dao));

            tblOT.getColumnModel().getColumn(0).setPreferredWidth(150);
            tblOT.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblOT.getColumnModel().getColumn(0).setCellEditor(
                    new OTTableCellEditor(dao));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addTableSelection() {
        //Define table selection model to single row selection.
        tblDoctor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblDoctor.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = tblDoctor.getSelectedRow();

                selectRow = -1;
                if (row != -1) {
                    selectRow = tblDoctor.convertRowIndexToModel(row);
                    setCurrDoctor(tableModel.getDoctor(selectRow));
                }
            }
        });
    }

    private void setCurrDoctor(Doctor doctor) {
        try {
            currDoctor = (Doctor) dao.find(Doctor.class, doctor.getDoctorId());
            String drId = currDoctor.getDoctorId();

            List<DoctorFeesMapping> listDFM = dao.findAllHSQL(
                    "select o from DoctorFeesMapping o where o.drId = '"
                    + drId + "'");
            currDoctor.setListFees(listDFM);
            if (listDFM != null) {
                if (listDFM.size() > 0) {
                    feesMapTableModel.setListDoctorFeesMapping(currDoctor.getListFees());
                } else {
                    feesMapTableModel.clear();
                }
            }

            List<DoctorFeesMappingDC> listDFMDC = dao.findAllHSQL(
                    "select o from DoctorFeesMappingDC o where o.drId = '"
                    + drId + "'");
            currDoctor.setListDFMDC(listDFMDC);
            if (listDFMDC != null) {
                if (listDFMDC.size() > 0) {
                    dcMapTableModel.setListDoctorFeesMapping(currDoctor.getListDFMDC());
                } else {
                    dcMapTableModel.clear();
                }
            }

            List<DoctorFeesMappingOT> listDFMOT = dao.findAllHSQL(
                    "select o from DoctorFeesMappingOT o where o.drId = '"
                    + drId + "'");
            currDoctor.setListDFMOT(listDFMOT);
            if (listDFMOT != null) {
                if (listDFMOT.size() > 0) {
                    otMapTableModel.setListDoctorFeesMapping(currDoctor.getListDFMOT());
                } else {
                    otMapTableModel.clear();
                }
            }

            txtName.setText(doctor.getDoctorName());
            txtCode.setText(doctor.getDoctorId());
            cboInitial.setSelectedItem(currDoctor.getInitialID());
            cboSex.setSelectedItem(currDoctor.getGenderId());
            txtNIRC.setText(currDoctor.getLicenseNo());
            cboSpeciality.setSelectedItem(currDoctor.getSpeciality());
            chkActive.setSelected(currDoctor.isActive());
            lblStatus.setText("EDIT");
            cboType.setSelectedItem(currDoctor.getDrType());
        } catch (Exception ex) {
            log.error("setCurrDoctor : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtCode.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Please key in the doctor code.", "Code",
                    JOptionPane.ERROR_MESSAGE);
            txtCode.requestFocusInWindow();
        } else if (cboInitial.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose Initial.",
                    "No Initial.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboInitial.requestFocusInWindow();
        } else if (cboSex.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose Gender.",
                    "No Gender.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboSex.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtName.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Please key in the doctor Name.",
                    "Name", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocusInWindow();
        } else if (txtNIRC.getText().length() > 16) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "License no shouldn't be more then 16 characters.",
                    "License No Length", JOptionPane.ERROR_MESSAGE);
            txtNIRC.requestFocusInWindow();
        } else if (!feesMapTableModel.isValidEntry()) {
            status = false;
        } else {
            currDoctor.setDoctorId(txtCode.getText());
            currDoctor.setGenderId((Gender) cboSex.getSelectedItem());
            currDoctor.setInitialID((Initial) cboInitial.getSelectedItem());
            currDoctor.setDoctorName(txtName.getText());
            //currDoctor.setNirc();
            currDoctor.setSpeciality((Speciality) cboSpeciality.getSelectedItem());
            currDoctor.setActive(chkActive.isSelected());
            currDoctor.setLicenseNo(txtNIRC.getText());
            currDoctor.setListFees(feesMapTableModel.getListDoctorFeesMapping());
            currDoctor.setListDFMDC(dcMapTableModel.getListDoctorFeesMapping());
            currDoctor.setListDFMOT(otMapTableModel.getListDoctorFeesMapping());
            if (cboType.getSelectedItem() != null) {
                currDoctor.setDrType(cboType.getSelectedItem().toString());
            } else {
                currDoctor.setDrType(null);
            }
        }

        return status;
    }

    private void showDialog(String panelName) {
        Dialog dialog;
        if (panelName.equals("gender")) {
            dialog = new GenderSetupDialog(dao);
        } else {
            dialog = new InitialSetupDialog(dao);
        }

        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - dialog.getWidth()) / 2;
        int y = (screen.height - dialog.getHeight()) / 2;

        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }

    @Override
    public void save() {
        try {
            if (isValidEntry()) {
                List<DoctorFeesMapping> listDFM = currDoctor.getListFees();
                dao.open();
                dao.beginTran();
                String drId = currDoctor.getDoctorId();
                for (DoctorFeesMapping dfm : listDFM) {
                    dfm.setDrId(drId);
                    dao.save1(dfm);
                }

                List<DoctorFeesMappingDC> listDFMDC = currDoctor.getListDFMDC();
                for (DoctorFeesMappingDC dfmDC : listDFMDC) {
                    dfmDC.setDrId(drId);
                    dao.save1(dfmDC);
                }

                List<DoctorFeesMappingOT> listDFMOT = currDoctor.getListDFMOT();
                for (DoctorFeesMappingOT dfmOT : listDFMOT) {
                    dfmOT.setDrId(drId);
                    dao.save1(dfmOT);
                }
                dao.save1(currDoctor);
                dao.commit();

                //tblDoctor.setRowSorter(null);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addDoctor(currDoctor);
                    PharmacyUtil.updateSeq("Doctor", dao);
                } else {
                    tableModel.setDoctor(selectRow, currDoctor);

                    String deleteList = feesMapTableModel.getDeletedList();
                    if (deleteList != null) {
                        String strSQL = "delete from doctor_fees_map where map_id in ("
                                + deleteList + ")";
                        dao.execSql(strSQL);
                    }

                    String deleteListDC = dcMapTableModel.getDeletedList();
                    if (deleteList != null) {
                        String strSQL = "delete from doctor_fees_map_dc where map_id in ("
                                + deleteListDC + ")";
                        dao.execSql(strSQL);
                    }

                    String deleteListOT = otMapTableModel.getDeletedList();
                    if (deleteList != null) {
                        String strSQL = "delete from doctor_fees_map_ot where map_id in ("
                                + deleteListOT + ")";
                        dao.execSql(strSQL);
                    }
                }
                //tblDoctor.setRowSorter(sorter);

                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Doctor", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            dao.close();
        }
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void delete() {
        String drId = txtCode.getText();
        if (isCanDelete(drId)) {
            String strSql = "delete from doctor where doctor_id = '" + drId + "'";
            try {
                dao.execSql(strSql);
                tableModel.setListDoctor(dao.findAllHSQL(
                        "select o from Doctor o where o.doctorName"
                ));
            } catch (Exception ex) {
                log.equals("delete : " + drId + " : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private boolean isCanDelete(String drId) {
        try {
            List<SaleHis> listSH = dao.findAllHSQL(
                    "select o from SaleHis o where o.doctor.doctorId = '" + drId + "'"
            );
            if (listSH != null) {
                if (!listSH.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "This doctor have sale record.\n You cannot delete this doctor.",
                            "Doctor Delete", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            List<OPDHis> listOPD = dao.findAllHSQL(
                    "Select o from OPDHis o where o.doctor.doctorId = '" + drId + "'"
            );
            if (listOPD != null) {
                if (!listOPD.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "This doctor have opd record.\n You cannot delete this doctor.",
                            "Doctor Delete", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            List<OTHis> listOT = dao.findAllHSQL(
                    "Select o from OTHis o where o.doctor.doctorId = '" + drId + "'"
            );
            if (listOT != null) {
                if (!listOT.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "This doctor have ot record.\n You cannot delete this doctor.",
                            "Doctor Delete", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            List<DCHis> listDC = dao.findAllHSQL(
                    "Select o from DCHis o where o.doctor.doctorId = '" + drId + "'"
            );
            if (listOT != null) {
                if (!listOT.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "This doctor have dc record.\n You cannot delete this doctor.",
                            "Doctor Delete", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (Exception ex) {
            log.error("isCanDelete : " + ex.getMessage());
            return false;
        } finally {
            dao.close();
        }

        return true;
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

    private void getDoctorId() {
        String tmpId = PharmacyUtil.getSeqR("Doctor", dao).toString();
        int idLength = 3;

        tmpId = tmpId.replace(".0", "");
        if (tmpId.length() < 3) {
            int currLength = tmpId.length();

            for (int i = 0; i < (idLength - currLength); i++) {
                tmpId = "0" + tmpId;
            }
        }

        txtCode.setText(tmpId);
    }

    private Action actionFeesMapDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblFeesMap.getSelectedRow();

            if (row >= 0) {
                DoctorFeesMapping dfm = feesMapTableModel.getDoctorFeesMapping(row);

                if (dfm.getService() != null) {
                    try {
                        if (tblFeesMap.getCellEditor() != null) {
                            tblFeesMap.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                    }

                    int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Delete", JOptionPane.YES_NO_OPTION);

                    if (yes_no == 0) {
                        feesMapTableModel.deleteDoctorFeesMapping(row);
                    }
                }
            }
        }
    };

    private void actionMapping() {
        //F8 event on tblService
        tblFeesMap.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblFeesMap.getActionMap().put("F8-Action", actionFeesMapDelete);

        //Enter event on tblService
        //tblFeesMap.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        //tblFeesMap.getActionMap().put("ENTER-Action", actionTblServiceEnterKey);
    }

    private void filterDoctor(String strFilter) {
        try {
            if (!strFilter.isEmpty()) {
                tableModel.setListDoctor(dao.findAll("Doctor", strFilter));
                statusFilter = true;
            } else {
                tableModel.setListDoctor(dao.findAll("Doctor"));
            }
        } catch (Exception ex) {
            log.error("filterDoctor : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(cboInitial);
        focusOrder.add(txtName);
        focusOrder.add(cboSex);
        focusOrder.add(txtNIRC);
        focusOrder.add(txtNIRC);
        focusOrder.add(cboSpeciality);
        focusOrder.add(chkActive);
        focusOrder.add(butSave);
        focusOrder.add(butClear);

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFilter = new javax.swing.JTextField();
        butFilter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDoctor = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cboInitial = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        butInitial = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        butSex = new javax.swing.JButton();
        cboSex = new javax.swing.JComboBox();
        butSave = new javax.swing.JButton();
        txtNIRC = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFeesMap = new javax.swing.JTable();
        cboSpeciality = new javax.swing.JComboBox();
        butSpeciality = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblOT = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDC = new javax.swing.JTable();

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        butFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butFilter.setText("...");
        butFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFilterActionPerformed(evt);
            }
        });

        tblDoctor.setFont(Global.textFont);
        tblDoctor.setModel(tableModel);
        tblDoctor.setRowHeight(23);
        tblDoctor.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblDoctor);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Initial     ");

        cboInitial.setFont(Global.textFont);

        lblStatus.setText("NEW");

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("License No");

        butInitial.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butInitial.setText("...");
        butInitial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butInitialActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Code");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Gender");

        txtCode.setEditable(false);
        txtCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodeActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Speciality");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        butSex.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSex.setText("...");
        butSex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSexActionPerformed(evt);
            }
        });

        cboSex.setFont(Global.textFont);

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        txtNIRC.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Active");

        jLabel8.setText("  ");

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("OPD Fees Mapping"));

        tblFeesMap.setFont(Global.textFont);
        tblFeesMap.setModel(feesMapTableModel);
        tblFeesMap.setRowHeight(23);
        jScrollPane2.setViewportView(tblFeesMap);

        cboSpeciality.setFont(Global.textFont);

        butSpeciality.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSpeciality.setText("...");
        butSpeciality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSpecialityActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Type");

        cboType.setFont(Global.textFont);
        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Doctor", "Technician", "Nurse", "MO" }));

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("OT Fees Mapping"));

        tblOT.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tblOT.setFont(Global.textFont);
        tblOT.setModel(otMapTableModel);
        tblOT.setRowHeight(23);
        jScrollPane3.setViewportView(tblOT);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("DC Fees Mapping"));

        tblDC.setFont(Global.textFont);
        tblDC.setModel(dcMapTableModel);
        tblDC.setRowHeight(23);
        jScrollPane4.setViewportView(tblDC);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cboSex, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(8, 8, 8)
                                .addComponent(butSex))
                            .addComponent(txtName)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(chkActive)
                        .addGap(6, 6, 6)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butClear))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cboSpeciality, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSpeciality))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtCode))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cboInitial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butInitial, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtNIRC))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(cboType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel9});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butInitial, butSex, butSpeciality});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboInitial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butInitial)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSex)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNIRC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboSpeciality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSpeciality))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblStatus)
                        .addComponent(jLabel7))
                    .addComponent(butClear)
                    .addComponent(butSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboInitial, cboSex, txtNIRC});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                        .addGap(8, 8, 8)
                        .addComponent(butFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(butFilter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butInitialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butInitialActionPerformed
        try {
            showDialog("initial");
            Initial ini = (Initial) cboInitial.getSelectedItem();
            BindingUtil.BindCombo(cboInitial, dao.findAll("Initial"));
            cboInitial.setSelectedItem(ini);
        } catch (Exception ex) {
            log.error("butInitialActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butInitialActionPerformed

    private void butSexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSexActionPerformed
        try {
            showDialog("gender");
            Gender gen = (Gender) cboSex.getSelectedItem();
            BindingUtil.BindCombo(cboSex, dao.findAll("Gender"));
            cboSex.setSelectedItem(gen);
        } catch (Exception ex) {
            log.error("butSexActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSexActionPerformed

    private void txtCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodeActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

  private void butSpecialityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSpecialityActionPerformed
      try {
          SpecialitySetup dialog = new SpecialitySetup();
          Speciality spec = (Speciality) cboSpeciality.getSelectedItem();
          BindingUtil.BindCombo(cboSpeciality, dao.findAll("Speciality"));
          cboSpeciality.setSelectedItem(spec);
      } catch (Exception ex) {
          log.error("butSpecialityActionPerformed : " + ex.getMessage());
      } finally {
          dao.close();
      }
  }//GEN-LAST:event_butSpecialityActionPerformed

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
      if (txtFilter.getText().length() == 0) {
          sorter.setRowFilter(null);
      } else {
          sorter.setRowFilter(swrf);
      }
  }//GEN-LAST:event_txtFilterKeyReleased

  private void butFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFilterActionPerformed
      DoctorSetupFilterDialog dialog = new DoctorSetupFilterDialog(dao);

      if (dialog.getStatus()) {
          filterDoctor(dialog.getFilter());
      }
  }//GEN-LAST:event_butFilterActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butFilter;
    private javax.swing.JButton butInitial;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butSex;
    private javax.swing.JButton butSpeciality;
    private javax.swing.JComboBox cboInitial;
    private javax.swing.JComboBox cboSex;
    private javax.swing.JComboBox cboSpeciality;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblDC;
    private javax.swing.JTable tblDoctor;
    private javax.swing.JTable tblFeesMap;
    private javax.swing.JTable tblOT;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtNIRC;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
