/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.SchoolDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.MachineInfo;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.pharmacy.database.view.VMarchant;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.SaleVouSearchTableModel1;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleVouSearch1 extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(SaleVouSearch1.class.getName());
    private AbstractDataAccess dao;
    private Object parent;
    private SelectionObserver observer;
    private int selectedRow = -1;
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "SaleSearch");
    private TableRowSorter<TableModel> sorter;
    private SaleVouSearchTableModel1 tableVouModel = new SaleVouSearchTableModel1();
    private VouFilter vouFilter;
    private String focusCtrlName = "-";
    private int mouseClick = 1;
    private String propValueFH = Util1.getPropValue("system.search.filter.history");
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");

    private KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblVoucher")) {
                int selRow = tblVoucher.getSelectedRow();
                if (selRow == -1 || selRow == 0) {
                    txtRemark.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtRemark")) {
                cboVStatus.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboVStatus")) {
                if (!cboVStatus.isPopupVisible()) {
                    cboCusGroup.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboCusGroup")) {
                if (!cboCusGroup.isPopupVisible()) {
                    txtVouNo.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtVouNo")) {
                if (!cboPayment.isPopupVisible()) {
                    cboPayment.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboPayment")) {
                if (!cboPayment.isPopupVisible()) {
                    cboSession.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboSession")) {
                if (!cboSession.isPopupVisible()) {
                    cboLocation.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboLocation")) {
                if (!cboLocation.isPopupVisible()) {
                    txtCusCode.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusCode")) {
                txtRemark.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtCusCode")) {
                cboLocation.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboLocation")) {
                if (!cboLocation.isPopupVisible()) {
                    cboSession.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboSession")) {
                if (!cboSession.isPopupVisible()) {
                    cboPayment.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboPayment")) {
                if (!cboPayment.isPopupVisible()) {
                    txtVouNo.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtVouNo")) {
                cboCusGroup.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboCusGroup")) {
                if (!cboCusGroup.isPopupVisible()) {
                    cboVStatus.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboVStatus")) {
                if (!cboVStatus.isPopupVisible()) {
                    txtRemark.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtRemark")) {
                tblVoucher.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER && focusCtrlName.equals("tblVoucher")) {
                select();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (parent instanceof JDialog) {
                    ((JDialog) parent).dispose();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    /**
     * Creates new form SaleVouSearch
     */
    public SaleVouSearch1(Object parent, SelectionObserver observer, AbstractDataAccess dao) {
        initComponents();

        this.parent = parent;
        this.observer = observer;
        this.dao = dao;

        try {
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("SaleVouSearch : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        //Search history
        getPrvFilter();
        initTableMedicine();
        initTableVoucher();
        actionMapping();

        sorter = new TableRowSorter(tblVoucher.getModel());
        tblVoucher.setRowSorter(sorter);
        addSelectionListenerVoucher();
        initFocus();
        this.requestFocus();
        search();

        timerFocus();

        String propValue = Util1.getPropValue("system.date.mouse.click");
        if (propValue != null) {
            if (!propValue.equals("-")) {
                if (!propValue.isEmpty()) {
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if (tmpValue != 0) {
                        mouseClick = tmpValue;
                    }
                }
            }
        }

        txtTotalAmount.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void initFocus() {
        txtCusCode.addKeyListener(keyListener);
        txtVouNo.addKeyListener(keyListener);
        txtRemark.addKeyListener(keyListener);

        cboLocation.getEditor().getEditorComponent().addKeyListener(keyListener);
        cboLocation.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboLocation.requestFocus();
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

        cboSession.getEditor().getEditorComponent().addKeyListener(keyListener);
        cboSession.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboSession.requestFocus();
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

        cboPayment.getEditor().getEditorComponent().addKeyListener(keyListener);
        cboPayment.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboPayment.requestFocus();
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

        cboCusGroup.getEditor().getEditorComponent().addKeyListener(keyListener);
        cboCusGroup.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboCusGroup.requestFocus();
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

        cboVStatus.getEditor().getEditorComponent().addKeyListener(keyListener);
        cboVStatus.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboVStatus.requestFocus();
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

        tblVoucher.addKeyListener(keyListener);
    }

    private void timerFocus() {
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tblVoucher.requestFocus();
                if (tableVouModel.getListVS() != null) {
                    if (tableVouModel.getListVS().size() > 0) {
                        tblVoucher.setRowSelectionInterval(0, 0);
                    }
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // <editor-fold defaultstate="collapsed" desc="selected">
    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;

                if (cus != null) {
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCusCode.setText(cus.getStuCode());
                    } else {
                        txtCusCode.setText(cus.getTraderId());
                    }
                    //txtCusCode.setText(cus.getTraderId());
                    txtCusName.setText(cus.getTraderName());
                    vouFilter.setTrader(cus);
                } else {
                    vouFilter.setTrader(null);
                }

                break;
            case "PatientList":
            case "PatientSearch":
                Patient ptt = (Patient) selectObj;
                if (ptt != null) {
                    txtCusCode.setText(ptt.getRegNo());
                    txtCusName.setText(ptt.getPatientName());
                    vouFilter.setPatient(ptt);
                }
                break;
        }
    }// </editor-fold>

    private void getCustomerList() {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            /*dialog = new UtilDialog(Util1.getParent(), true, this,
                    "Patient List", dao);*/
            PatientSearch dialog = new PatientSearch(dao, this);
        } else {
            int locationId = -1;
            if (cboLocation.getSelectedItem() instanceof Location) {
                locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }
            UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                    "Customer List", dao, locationId);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindComboFilter(cboLocation, getLocationFilter());
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
            if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                BindingUtil.BindComboFilter(cboCusGroup, dao.findAll("City"));
            } else {
                BindingUtil.BindComboFilter(cboCusGroup, dao.findAll("CustomerGroup"));
            }
            BindingUtil.BindComboFilter(cboVStatus, dao.findAll("VouStatus"));

            if (Util1.hashPrivilege("UseSaleMan")) {
                jLabel11.setText("Sale Man");
                BindingUtil.BindComboFilter(cboMachine,
                        dao.findAllHSQL("select o from Doctor o order by o.doctorName"));
            } else {
                jLabel11.setText("Machine");
                BindingUtil.BindComboFilter(cboMachine,
                        dao.findAllHSQL("select o from MachineInfo o order by o.machineName"));
            }
            if (Util1.hashPrivilege("UseDeletedStatus")) {
                jLabel12.setText("Type");
                List<String> listType = new ArrayList();
                listType.add("Normal");
                listType.add("Deleted");
                BindingUtil.BindCombo(cboUser, listType);
            } else {
                jLabel12.setText("User");
                BindingUtil.BindComboFilter(cboUser,
                        dao.findAllHSQL("select o from Appuser o order by o.userName"));
            }

            new ComBoBoxAutoComplete(cboPayment);
            new ComBoBoxAutoComplete(cboLocation);
            new ComBoBoxAutoComplete(cboSession);
            new ComBoBoxAutoComplete(cboCusGroup);
            new ComBoBoxAutoComplete(cboVStatus);
            new ComBoBoxAutoComplete(cboMachine);
            new ComBoBoxAutoComplete(cboUser);

            cboPayment.setSelectedIndex(0);
            cboLocation.setSelectedIndex(0);
            cboSession.setSelectedIndex(0);
            cboCusGroup.setSelectedIndex(0);
            cboVStatus.setSelectedIndex(0);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowSale = true) order by o.locationName");
            } else {
                return dao.findAll("Location");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return null;
    }

    private void initTableMedicine() {
        try {
            List<VouCodeFilter> listMedicine = dao.findAll("VouCodeFilter",
                    "key.tranOption = 'SaleSearch' and key.userId = '"
                    + Global.machineId + "'");
            tblMedicineModel.setListCodeFilter(listMedicine);
        } catch (Exception ex) {
            log.error("initTableMedicine : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblMedicineModel.addEmptyRow();
        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblMedicine.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void initTableVoucher() {
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(15);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(25);

        tblVoucher.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    private void addSelectionListenerVoucher() {
        //Define table selection model to single row selection.
        tblVoucher.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblVoucher.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblVoucher.getSelectedRow();
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="getMedInfo">
    public void getMedInfo(String medCode) {
        try {
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                selected("MedicineList", medicine);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                        "Invalid.", JOptionPane.ERROR_MESSAGE);
                //getMedList(medCode);
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMedList">
    private void getMedList(String filter) {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        String cusGroup = "-";
        /*if(currSaleVou.getCustomerId() != null){
            if(currSaleVou.getCustomerId().getTraderGroup() != null){
                cusGroup = currSaleVou.getCustomerId().getTraderGroup().getGroupId();
            }
        }*/
        MedListDialog1 dialog = new MedListDialog1(dao, filter, locationId, cusGroup);

        if (dialog.getSelect() != null) {
            selected("MedicineList", dialog.getSelect());
        }
    }// </editor-fold>

    private String getHSQL() {
        String strFieldName = "sh.cus_id";
        if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
            strFieldName = "tr.stu_no";
        }
        String strSql = "select distinct date(sh.sale_date) sale_date, sh.sale_inv_id, sh.remark, \n"
                + "ifnull(sh.cus_id,ifnull(sh.reg_no, sh.stu_no)) cus_id, ifnull(tr.trader_name, ifnull(pd.patient_name, sh.stu_name)) cus_name, "
                + "usr.user_name, sh.vou_total, l.location_name, sh.deleted,sh.tax_amt,sale_exp_total_in,sale_exp_total,discount,\n"
                + "tr.stu_no \n"
                + "from sale_his sh\n"
                + "join sale_detail_his sdh on sh.sale_inv_id = sdh.vou_no\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "join location l on sh.location_id = l.location_id \n"
                + "left join patient_detail pd on sh.reg_no = pd.reg_no \n"
                + "left join trader tr on sh.cus_id = tr.trader_id "
                + "where sh.sale_date between '" + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";
        /*String strSql = "select distinct date(sh.sale_date) sale_date, sh.sale_inv_id, sh.remark, \n"
                + "ifnull(sh.cus_id,ifnull(sh.reg_no, sh.stu_no)) cus_id, ifnull(tr.trader_name, ifnull(pd.patient_name, sh.stu_name)) cus_name, "
                + "usr.user_name, sh.vou_total, l.location_name, sh.deleted,sh.tax_amt,sale_exp_total_in,sale_exp_total,discount\n"
                + "from sale_his sh\n"
                + "join sale_detail_his sdh on sh.sale_inv_id = sdh.vou_no\n"
                + "join medicine med on sdh.med_id = med.med_id\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "join location l on sh.location_id = l.location_id \n"
                + "left join patient_detail pd on sh.reg_no = pd.reg_no \n"
                + "left join trader tr on sh.cus_id = tr.trader_id "
                + "where sh.sale_date between '" + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";*/
 /*String strSql = "select distinct v from SaleHis v join v.saleDetailHis h left join v.patientId pd left join v.customerId cus where v.saleDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";*/

        vouFilter.setFromDate(DateUtil.toDate(txtFromDate.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtToDate.getText()));

        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                strSql = strSql + " and sh.reg_no = '" + txtCusCode.getText().trim() + "'";
            } else {
                strSql = strSql + " and " + strFieldName + " = '" + txtCusCode.getText().trim() + "'";
            }
        } else if (!txtCusName.getText().isEmpty()) {
            if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                strSql = strSql + " and (upper(sh.stu_name) like '%"
                        + txtCusName.getText().trim().toUpperCase() + "%')";
            } else {
                strSql = strSql + " and (upper(sh.stu_name) like '%"
                        + txtCusName.getText().trim().toUpperCase() + "%')";
            }
            vouFilter.setPtName(txtCusName.getText().trim());
        } else {
            vouFilter.setTrader(null);
            vouFilter.setPatient(null);
            vouFilter.setPtName(null);
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            //strSql = strSql + " and v.locationId.locationId = " + location.getLocationId();
            strSql = strSql + " and sh.location_id = " + location.getLocationId();
            vouFilter.setLocation(location);
        } else {
            vouFilter.setLocation(null);
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                /*strSql = strSql + " and v.locationId.locationId in (select a.key.locationId from UserLocationMapping a \n"
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowSessCheck = true)";*/
                strSql = strSql + " and sh.location_id in (select a.location_id from user_location_mapping a \n"
                        + "where a.user_id = '" + Global.loginUser.getUserId()
                        + "' and a.allow_sale = true)";
            }
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            Session session = (Session) cboSession.getSelectedItem();
            //strSql = strSql + " and v.session = " + session.getSessionId();
            strSql = strSql + " and sh.session_id = " + session.getSessionId();
            vouFilter.setSession(session);
        } else {
            vouFilter.setSession(null);
        }

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            PaymentType paymentType = (PaymentType) cboPayment.getSelectedItem();
            //strSql = strSql + " and v.paymentTypeId.paymentTypeId = " + paymentType.getPaymentTypeId();
            strSql = strSql + " and sh.payment_type_id = " + paymentType.getPaymentTypeId();
            vouFilter.setPaymentType(paymentType);
        } else {
            vouFilter.setPaymentType(null);
        }

        if (!txtVouNo.getText().trim().isEmpty()) {
            //strSql = strSql + " and v.saleInvId like '" + txtVouNo.getText().trim() + "%'";
            strSql = strSql + " and sh.sale_inv_id like '" + txtVouNo.getText().trim() + "%'";
            vouFilter.setVouNo(txtVouNo.getText().trim());
        } else {
            vouFilter.setVouNo(null);
        }

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            /*if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                City ciGroup = (City) cboCusGroup.getSelectedItem();
                strSql = strSql + " and v.patientId.city.cityId = '"
                        + ciGroup.getCityId() + "'";
                vouFilter.setCity(ciGroup);
            } else {
                CustomerGroup cusGroup = (CustomerGroup) cboCusGroup.getSelectedItem();
                strSql = strSql + " and v.customerId.traderGroup.groupId = '"
                        + cusGroup.getGroupId() + "'";
                vouFilter.setCusGroup(cusGroup);
            }*/
            if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                City ciGroup = (City) cboCusGroup.getSelectedItem();
                strSql = strSql + " and pd.city_id = '"
                        + ciGroup.getCityId() + "'";
                vouFilter.setCity(ciGroup);
            } else {
                CustomerGroup cusGroup = (CustomerGroup) cboCusGroup.getSelectedItem();
                strSql = strSql + " and tr.group_id = '"
                        + cusGroup.getGroupId() + "'";
                vouFilter.setCusGroup(cusGroup);
            }
        } else {
            vouFilter.setCusGroup(null);
        }

        if (cboVStatus.getSelectedItem() instanceof VouStatus) {
            VouStatus vouStatus = (VouStatus) cboVStatus.getSelectedItem();
            //strSql = strSql + " and v.vouStatus.statusId = " + vouStatus.getStatusId();
            strSql = strSql + " and sh.vou_status = " + vouStatus.getStatusId();
            vouFilter.setVouStatus(vouStatus);
        } else {
            vouFilter.setVouStatus(null);
        }

        if (!txtRemark.getText().trim().isEmpty()) {
            //strSql = strSql + " and v.remark like '%" + txtRemark.getText() + "%' ";
            strSql = strSql + " and sh.remark like '%" + txtRemark.getText() + "%' ";
            vouFilter.setRemark(txtRemark.getText());
        } else {
            vouFilter.setRemark(null);
        }

        if (Util1.hashPrivilege("UseSaleMan")) {
            if (cboMachine.getSelectedItem() instanceof Doctor) {
                String doctorId = ((Doctor) cboMachine.getSelectedItem()).getDoctorId();
                strSql = strSql + " and sh.doctor_id = '" + doctorId + "'";
                vouFilter.setPtName(doctorId);
            } else {
                vouFilter.setPtName(null);
            }
        } else if (cboMachine.getSelectedItem() instanceof MachineInfo) {
            MachineInfo machine = (MachineInfo) cboMachine.getSelectedItem();
            String machineId = machine.getMachineId().toString();
            if (machineId.length() == 1) {
                machineId = "0" + machineId;
            }
            //strSql = strSql + " and v.saleInvId like '" + machineId + "%'";
            strSql = strSql + " and sh.sale_inv_id like '" + machineId + "%'";
            vouFilter.setMachine(machine);
        } else {
            vouFilter.setMachine(null);
        }

        if (Util1.hashPrivilege("UseDeletedStatus")) {
            String status = cboUser.getSelectedItem().toString();
            if (status.equals("Normal")) {
                strSql = strSql + " and sh.deleted = false";
            } else {
                strSql = strSql + " and sh.deleted = true";
            }
        } else if (cboUser.getSelectedItem() instanceof Appuser) {
            Appuser user = (Appuser) cboUser.getSelectedItem();
            //strSql = strSql + " and v.createdBy.userId = '" + user.getUserId() + "'";
            strSql = strSql + " and sh.created_by = '" + user.getUserId() + "'";
            vouFilter.setAppUser(user);
        } else {
            vouFilter.setAppUser(null);
        }

        if (txtCode.getText() != null) {
            if (!txtCode.getText().trim().isEmpty()) {
                String itemCode = txtCode.getText().trim().toUpperCase();
                vouFilter.setItemCode(itemCode);
                strSql = strSql + " and upper(med.short_name) like '" + itemCode.toUpperCase() + "%'";
            } else {
                vouFilter.setItemCode(null);
            }
        } else {
            vouFilter.setItemCode(null);
        }

        if (txtDesp.getText() != null) {
            if (!txtDesp.getText().trim().isEmpty()) {
                String itemDesp = txtDesp.getText().trim().toUpperCase();
                vouFilter.setItemDesp(itemDesp);
                strSql = strSql + " and upper(med.med_name) like '" + itemDesp.toUpperCase() + "%'";
            } else {
                vouFilter.setItemDesp(null);
            }
        } else {
            vouFilter.setItemDesp(null);
        }

        //Filter history save
        try {
            if (chkSave.isSelected()) {
                dao.save(vouFilter);
            } else {
                dao.delete(vouFilter);
            }
        } catch (Exception ex) {
            log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }

        List<VouCodeFilter> listMedicine = tblMedicineModel.getListCodeFilter();

        if (listMedicine.size() > 1) {
            strSql = strSql + " and sdh.med_id in (";
            String medList = "";

            for (VouCodeFilter filter : listMedicine) {
                if (filter.getKey() != null) {
                    if (medList.length() > 0) {
                        medList = medList + ",'" + filter.getKey().getItemCode().getMedId() + "'";
                    } else {
                        medList = "'" + filter.getKey().getItemCode().getMedId() + "'";
                    }
                }
            }

            strSql = strSql + medList + ")";
        }
        return strSql + " order by date(sh.sale_date) desc, sh.sale_inv_id desc";
    }

    private List<VoucherSearch> getSearchVoucher() {
        String strSql = getHSQL();
        log.info("strSql : " + strSql);
        List<VoucherSearch> listVS = null;

        try {
            ResultSet rs = dao.execSQL(strSql);
            listVS = new ArrayList();
            if (rs != null) {
                while (rs.next()) {
                    VoucherSearch vs = new VoucherSearch();
                    vs.setCusName(rs.getString("cus_name"));
                    if (prifxStatus.equals("Y")) {
                        vs.setCusNo(rs.getString("stu_no"));
                    } else {
                        vs.setCusNo(rs.getString("cus_id"));
                    }
                    vs.setInvNo(rs.getString("sh.sale_inv_id"));
                    vs.setIsDeleted(rs.getBoolean("deleted"));
                    vs.setLocation(rs.getString("location_name"));
                    vs.setRefNo(rs.getString("remark"));
                    vs.setTranDate(rs.getDate("sale_date"));
                    vs.setUserName(rs.getString("user_name"));
                    vs.setVouTotal(rs.getDouble("vou_total"));
                    vs.setTaxAmt(rs.getDouble("tax_amt"));
                    vs.setExpTotal(rs.getDouble("sale_exp_total"));
                    vs.setExpInTotal(rs.getDouble("sale_exp_total_in"));
                    vs.setDiscount(rs.getDouble("discount"));
                    listVS.add(vs);
                }
            }
        } catch (SQLException ex) {
            log.error("getSearchVoucher : " + ex.toString());
        } finally {
            dao.close();
        }

        return listVS;
    }

    private void search() {
        /*String strSql = getHSQL();
        log.info("strSql : " + strSql);
        try {
            dao.open();
            tableVouModel.setListSaleHis(dao.findAllHSQL(strSql));
            dao.close();
            lblTotalRec.setText("Total Records : " + tableVouModel.getRowCount());
            txtTotalAmount.setValue(tableVouModel.getTotal());
            tblVoucher.requestFocus();
        } catch (Exception ex) {
            log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }*/
        log.info("Start Time : " + new Date());
        tableVouModel.setListVS(getSearchVoucher());
        lblTotalRec.setText("Total Records : " + tableVouModel.getRowCount());
        List<VoucherSearch> listVS = tableVouModel.getListVS();
        double ttlAmt = 0.0;
        double grandAmt = 0.0;
        if (listVS != null) {
            if (!listVS.isEmpty()) {
                for (VoucherSearch vs : listVS) {
                    if (!vs.getIsDeleted()) {
                        ttlAmt += NumberUtil.NZero(vs.getVouTotal());
                        double taxAmt = NumberUtil.NZero(vs.getTaxAmt());
                        double expIn = NumberUtil.NZero(vs.getExpInTotal());
                        double exp = NumberUtil.NZero(vs.getExpTotal());
                        double dis = NumberUtil.NZero(vs.getDiscount());
                        grandAmt += NumberUtil.NZero(vs.getVouTotal()) + taxAmt + expIn + exp - dis;
                    }
                }
            }
        }
        txtTotalAmount.setValue(ttlAmt);
        tblVoucher.requestFocus();
        log.info("End Time : " + new Date());
    }

    private void select() {
        if (selectedRow >= 0) {
            VoucherSearch vs = tableVouModel.getSelectVou(tblVoucher.convertRowIndexToModel(selectedRow));
            observer.selected("SaleVouList", vs.getInvNo());

            /*observer.selected("SaleVouList",
                    tableVouModel.getSelectVou(tblVoucher.convertRowIndexToModel(selectedRow)));*/
            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select sale voucher.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getPrvFilter() {
        VouFilter tmpFilter;

        try {
            if (propValueFH.equals("M")) {
                tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'SaleVouSearch'"
                        + " and key.userId = '" + Global.machineId + "'");
            } else {
                tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'SaleVouSearch'"
                        + " and key.userId = '" + Global.machineId + "'");
            }

            if (tmpFilter == null) {
                chkSave.setSelected(false);
                vouFilter = new VouFilter();
                vouFilter.getKey().setTranOption("SaleVouSearch");
                if (propValueFH.equals("M")) {
                    vouFilter.getKey().setUserId(Global.machineId);
                } else {
                    vouFilter.getKey().setUserId(Global.machineId);
                }

                txtFromDate.setText(DateUtil.getTodayDateStr());
                txtToDate.setText(DateUtil.getTodayDateStr());
            } else {
                chkSave.setSelected(true);
                vouFilter = tmpFilter;
                txtFromDate.setText(DateUtil.toDateStr(vouFilter.getFromDate()));
                txtToDate.setText(DateUtil.toDateStr(vouFilter.getToDate()));

                if (vouFilter.getTrader() != null) {
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCusCode.setText(vouFilter.getTrader().getStuCode());
                    } else {
                        txtCusCode.setText(vouFilter.getTrader().getTraderId());
                    }
                    txtCusName.setText(vouFilter.getTrader().getTraderName());
                } else if (vouFilter.getPatient() != null) {
                    txtCusCode.setText(vouFilter.getPatient().getRegNo());
                    txtCusName.setText(vouFilter.getPatient().getPatientName());
                } else if (vouFilter.getPtName() != null) {
                    if (Util1.hashPrivilege("UseSaleMan")) {

                    } else {
                        txtCusName.setText(vouFilter.getPtName());
                    }
                }

                if (vouFilter.getLocation() != null) {
                    cboLocation.setSelectedItem(vouFilter.getLocation());
                }

                if (vouFilter.getSession() != null) {
                    cboSession.setSelectedItem(vouFilter.getSession());
                }

                if (vouFilter.getPaymentType() != null) {
                    cboPayment.setSelectedItem(vouFilter.getPaymentType());
                }

                txtVouNo.setText(vouFilter.getVouNo());

                if (vouFilter.getCusGroup() != null) {
                    cboCusGroup.setSelectedItem(vouFilter.getCusGroup());
                }

                if (vouFilter.getVouStatus() != null) {
                    cboVStatus.setSelectedItem(vouFilter.getVouStatus());
                }

                if (vouFilter.getRemark() != null) {
                    txtRemark.setText(vouFilter.getRemark());
                }

                if (Util1.hashPrivilege("UseSaleMan")) {
                    String doctorId = vouFilter.getPtName();
                    try {
                        Doctor dr = (Doctor) dao.find(Doctor.class, doctorId);
                        cboMachine.setSelectedItem(dr);
                    } catch (Exception ex) {
                        log.error("getPrvFilter : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                } else if (vouFilter.getMachine() != null) {
                    cboMachine.setSelectedItem(vouFilter.getMachine());
                }

                if (Util1.hashPrivilege("UseDeletedStatus")) {

                } else if (vouFilter.getAppUser() != null) {
                    cboUser.setSelectedItem(vouFilter.getAppUser());
                }

                if (vouFilter.getItemCode() != null) {
                    txtCode.setText(vouFilter.getItemCode().trim());
                }

                if (vouFilter.getItemDesp() != null) {
                    txtDesp.setText(vouFilter.getItemDesp().trim());
                }
            }
        } catch (Exception ex) {
            log.error("getPrvFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getCustomer() {
        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            try {

                if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    //cus = (Patient) dao.find(Patient.class, txtCusId.getText());
                    dao.open();
                    Patient ptt = (Patient) dao.find(Patient.class, txtCusCode.getText());
                    dao.close();

                    if (ptt == null) {
                        txtCusCode.setText(null);
                        txtCusName.setText(null);
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid Patient code.",
                                "Patient Code", JOptionPane.ERROR_MESSAGE);
                        txtCusCode.requestFocus();
                    } else {
                        selected("PatientList", ptt);
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    //String url = Util1.getPropValue("system.app.school.url");
                    dao.open();
                    SchoolDataAccess sda = new SchoolDataAccess(dao);
                    String stuNo = txtCusCode.getText();
                    VMarchant vm = sda.getMarchant(stuNo);
                    dao.close();

                    if (vm == null) {
                        txtCusCode.setText(null);
                        txtCusName.setText(null);

                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid student number or not enroll student.",
                                "Student Number", JOptionPane.ERROR_MESSAGE);
                    } else {
                        txtCusCode.setText(vm.getPersonNumber());
                        txtCusName.setText(vm.getPersonName());
                    }
                } else {
                    /*dao.open();
                    Trader cus = (Trader) dao.find(Trader.class, txtCusCode.getText());
                    dao.close();*/
                    Trader cus = getTrader(txtCusCode.getText().trim().toUpperCase());
                    if (cus == null) {
                        txtCusCode.setText(null);
                        txtCusName.setText(null);
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid customer code.",
                                "Trader Code", JOptionPane.ERROR_MESSAGE);

                    } else {
                        selected("CustomerList", cus);
                    }
                }
            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtCusCode.requestFocus();
        }
    }

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            String strFieldName = "o.traderId";
            if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                strFieldName = "o.stuCode";
            }
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                String strSql;
                int locationId;
                if (cboLocation.getSelectedItem() instanceof Location) {
                    locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId = "
                            + locationId + ") and " + strFieldName + " = '" + traderId.toUpperCase() + "' order by o.traderName";
                } else {
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId in ("
                            + "select a.key.locationId from UserLocationMapping a "
                            + "where a.key.userId = '" + Global.loginUser.getUserId()
                            + "' and a.isAllowSale = true)) and " + strFieldName + " = '"
                            + traderId.toUpperCase() + "' order by o.traderName";
                }
                List<Trader> listTrader = dao.findAllHSQL(strSql);
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                cus = (Trader) dao.find(Trader.class, traderId);
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
    }

    private void actionMapping() {
        //F3 event on tblSale
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblMedicine.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblMedicine.getActionMap().put("F8-Action", actionMedDelete);

        //Enter event on tblSale
        //tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        //tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);
    }

    private Action actionMedList = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tblMedicine.getCellEditor().stopCellEditing();
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            }
        }
    };

    private Action actionMedDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblMedicine.getSelectedRow() >= 0) {
                tblMedicineModel.delete(tblMedicine.getSelectedRow());
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFromDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtToDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboSession = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        cboCusGroup = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        cboVStatus = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cboMachine = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cboUser = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        chkSave = new javax.swing.JCheckBox();
        txtCode = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(tblMedicineModel);
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        butSelect = new javax.swing.JButton();
        butSearch = new javax.swing.JButton();
        lblTotalRec = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JFormattedTextField();

        setPreferredSize(new java.awt.Dimension(1200, 600));

        jPanel1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPanel1FocusGained(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtFromDate.setEditable(false);
        txtFromDate.setFont(Global.textFont);
        txtFromDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromDateMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        txtToDate.setEditable(false);
        txtToDate.setFont(Global.textFont);
        txtToDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToDateMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Customer");

        txtCusCode.setFont(Global.textFont);
        txtCusCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusCodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCusCodeFocusLost(evt);
            }
        });
        txtCusCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusCodeMouseClicked(evt);
            }
        });
        txtCusCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusCodeActionPerformed(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.setFont(Global.textFont);
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Location");

        cboLocation.setFont(Global.textFont);
        cboLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboLocationFocusGained(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Session");

        cboSession.setFont(Global.textFont);
        cboSession.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboSessionFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Payment");

        cboPayment.setFont(Global.textFont);
        cboPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboPaymentFocusGained(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou No");

        txtVouNo.setFont(Global.textFont);
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Cus-G");

        cboCusGroup.setFont(Global.textFont);
        cboCusGroup.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboCusGroupFocusGained(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("V-Status");

        cboVStatus.setFont(Global.textFont);
        cboVStatus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboVStatusFocusGained(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Ref. Vou.");

        txtRemark.setFont(Global.textFont);
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Machine");

        cboMachine.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("User");

        cboUser.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Code");

        chkSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSaveActionPerformed(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Desp");

        tblMedicine.setFont(Global.textFont);
        tblMedicine.setRowHeight(23);
        tblMedicine.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblMedicine);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel7)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCusCode)
                    .addComponent(txtCusName)
                    .addComponent(cboLocation, 0, 235, Short.MAX_VALUE)
                    .addComponent(cboSession, 0, 235, Short.MAX_VALUE)
                    .addComponent(cboPayment, 0, 235, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(cboCusGroup, 0, 235, Short.MAX_VALUE)
                    .addComponent(cboVStatus, 0, 235, Short.MAX_VALUE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(cboMachine, 0, 235, Short.MAX_VALUE)
                    .addComponent(cboUser, 0, 235, Short.MAX_VALUE)
                    .addComponent(txtCode)
                    .addComponent(txtDesp)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkSave, jLabel1, jLabel10, jLabel11, jLabel12, jLabel13, jLabel15, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboVStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cboMachine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblVoucher.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblVoucher.setModel(tableVouModel);
        tblVoucher.setRowHeight(23);
        tblVoucher.setShowVerticalLines(false);
        tblVoucher.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblVoucherFocusGained(evt);
            }
        });
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVoucher);

        butSelect.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSelect.setText("Select");
        butSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelectActionPerformed(evt);
            }
        });

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                butSearchMouseClicked(evt);
            }
        });
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        lblTotalRec.setText("Total Records : 0");

        lblTotalAmount.setText("Total Amount : ");

        txtTotalAmount.setEditable(false);
        txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 874, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butSearch, butSelect});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSelect)
                    .addComponent(butSearch)
                    .addComponent(lblTotalRec)
                    .addComponent(lblTotalAmount)
                    .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="event section">
    private void txtFromDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFromDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFromDateMouseClicked

    private void txtToDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtToDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtToDateMouseClicked

    private void txtCusCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusCodeMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusCodeMouseClicked

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
        tblVoucher.requestFocus();
        if (tableVouModel.getListVS() != null) {
            if (tableVouModel.getListVS().size() > 0) {
                tblVoucher.setRowSelectionInterval(0, 0);
            }
        }
    }//GEN-LAST:event_butSearchActionPerformed

    private void txtCusCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusCodeActionPerformed
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtCusCodeActionPerformed

    private void txtCusCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusCodeFocusLost
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        }
    }//GEN-LAST:event_txtCusCodeFocusLost

  private void butSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelectActionPerformed
      select();
  }//GEN-LAST:event_butSelectActionPerformed

  private void butSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_butSearchMouseClicked
      // TODO add your handling code here:
  }//GEN-LAST:event_butSearchMouseClicked

    private void jPanel1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanel1FocusGained
        tblVoucher.requestFocus();
    }//GEN-LAST:event_jPanel1FocusGained

    private void tblVoucherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblVoucherFocusGained
        focusCtrlName = "tblVoucher";
    }//GEN-LAST:event_tblVoucherFocusGained

    private void txtCusCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusCodeFocusGained
        focusCtrlName = "txtCusCode";
        txtCusCode.selectAll();
    }//GEN-LAST:event_txtCusCodeFocusGained

    private void cboLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboLocationFocusGained
        focusCtrlName = "cboLocation";
    }//GEN-LAST:event_cboLocationFocusGained

    private void cboSessionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboSessionFocusGained
        focusCtrlName = "cboSession";
    }//GEN-LAST:event_cboSessionFocusGained

    private void cboPaymentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboPaymentFocusGained
        focusCtrlName = "cboPayment";
    }//GEN-LAST:event_cboPaymentFocusGained

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        focusCtrlName = "txtVouNo";
        txtVouNo.selectAll();
    }//GEN-LAST:event_txtVouNoFocusGained

    private void cboCusGroupFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboCusGroupFocusGained
        focusCtrlName = "cboCusGroup";
    }//GEN-LAST:event_cboCusGroupFocusGained

    private void cboVStatusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboVStatusFocusGained
        focusCtrlName = "cboVStatus";
    }//GEN-LAST:event_cboVStatusFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        focusCtrlName = "txtRemark";
        txtRemark.selectAll();
    }//GEN-LAST:event_txtRemarkFocusGained

    private void chkSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSaveActionPerformed
        try {
            if (chkSave.isSelected()) {
                dao.save(vouFilter);
            } else {
                dao.delete(vouFilter);
            }
        } catch (Exception ex) {
            log.error("chkSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }//GEN-LAST:event_chkSaveActionPerformed
    // </editor-fold>
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelect;
    private javax.swing.JComboBox cboCusGroup;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox<String> cboMachine;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JComboBox<String> cboUser;
    private javax.swing.JComboBox cboVStatus;
    private javax.swing.JCheckBox chkSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtFromDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmount;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
