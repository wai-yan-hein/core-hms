/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.tempentity.VouSrvFilter;
import com.cv.app.opd.database.view.VService;
import com.cv.app.opd.ui.common.OPDTableCellEditor;
import com.cv.app.opd.ui.common.OPDVouTableModel;
import com.cv.app.opd.ui.common.ServiceFilterTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.MachineInfo;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OPDVouSearchDialog extends javax.swing.JDialog implements SelectionObserver {

    static Logger log = Logger.getLogger(OPDVouSearchDialog.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final SelectionObserver observer;
    private VouFilter vouFilter;
    private int selectedRow = -1;
    private final TableRowSorter<TableModel> sorter;
    private ServiceFilterTableModel srvFilterTableModel
            = new ServiceFilterTableModel(dao, true, "OPDVoucherSearch");
    private OPDVouTableModel vouTableModel = new OPDVouTableModel();
    private final String option;
    private String focusCtrlName = "-";
    private final KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblVou")) {
                int selRow = tblVou.getSelectedRow();
                if (selRow == -1 || selRow == 0) {
                    cboSession.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboSession")) {
                if (!cboSession.isPopupVisible()) {
                    cboPayment.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboPayment")) {
                if (!cboPayment.isPopupVisible()) {
                    cboCurrency.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboCurrency")) {
                if (!cboCurrency.isPopupVisible()) {
                    txtRemark.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtRemark")) {
                txtDocNo.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDocNo")) {
                txtPtNo.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtPtNo")) {
                txtDocNo.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDocNo")) {
                txtRemark.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtRemark")) {
                cboCurrency.requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboCurrency")) {
                if (!cboCurrency.isPopupVisible()) {
                    cboPayment.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboPayment")) {
                if (!cboPayment.isPopupVisible()) {
                    cboSession.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboSession")) {
                if (!cboSession.isPopupVisible()) {
                    tblVou.requestFocus();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER && focusCtrlName.equals("tblVou")) {
                select();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    /**
     * Creates new form OPDVouSearchDialog
     */
    public OPDVouSearchDialog(SelectionObserver observer, String option) {
        super(Util1.getParent(), true);
        initComponents();
        this.observer = observer;
        this.option = option;

        initCombo();
        getPrvFilter(); //Search history
        initTable();
        sorter = new TableRowSorter(tblVou.getModel());
        search();
        tblVou.setRowSorter(sorter);
        actionMapping();
        initFocus();
        timerFocus();

        Dimension screen = Util1.getScreenSize();
        setSize(screen.width - 200, screen.height - 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initFocus() {
        txtPtNo.addKeyListener(keyListener);
        txtDocNo.addKeyListener(keyListener);
        txtRemark.addKeyListener(keyListener);

        cboCurrency.getEditor().getEditorComponent().addKeyListener(keyListener);
        cboCurrency.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboCurrency.requestFocus();
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

        tblVou.addKeyListener(keyListener);
    }

    private void timerFocus() {
        Timer timer = new Timer(500, (ActionEvent e) -> {
            System.out.println("OPD Voucher");
            tblVou.requestFocus();
            if (!vouTableModel.getListOPDHis().isEmpty()) {
                tblVou.setRowSelectionInterval(0, 0);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
            BindingUtil.BindComboFilter(cboCurrency, dao.findAll("Currency"));
            BindingUtil.BindComboFilter(cboMachine,
                    dao.findAllHSQL("select o from MachineInfo o order by o.machineName"));
            BindingUtil.BindComboFilter(cboUser,
                    dao.findAllHSQL("select o from Appuser o order by o.userName"));

            new ComBoBoxAutoComplete(cboPayment);
            new ComBoBoxAutoComplete(cboCurrency);
            new ComBoBoxAutoComplete(cboSession);
            new ComBoBoxAutoComplete(cboMachine);
            new ComBoBoxAutoComplete(cboUser);

            cboPayment.setSelectedIndex(0);
            cboCurrency.setSelectedIndex(0);
            cboSession.setSelectedIndex(0);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getPrvFilter() {
        String tranOption = "OPDVouSearch";
        if (option.equals("RESULT")) {
            tranOption = "ResultSearch";
        }

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

                if (vouFilter.getTrader() != null) {
                    txtPtNo.setText(vouFilter.getTrader().getTraderId());
                    txtPtName.setText(vouFilter.getTrader().getTraderName());
                }

                if (vouFilter.getSession() != null) {
                    cboSession.setSelectedItem(vouFilter.getSession());
                }

                if (vouFilter.getPaymentType() != null) {
                    cboPayment.setSelectedItem(vouFilter.getPaymentType());
                }

                if (vouFilter.getCurrency() != null) {
                    cboCurrency.setSelectedItem(vouFilter.getCurrency());
                }

                if (vouFilter.getRemark() != null) {
                    txtRemark.setText(vouFilter.getRemark());
                }

                if (vouFilter.getMachine() != null) {
                    cboMachine.setSelectedItem(vouFilter.getMachine());
                }

                if (vouFilter.getAppUser() != null) {
                    cboUser.setSelectedItem(vouFilter.getAppUser());
                }

                if (vouFilter.getPatient() != null) {
                    txtPtName.setText(vouFilter.getPatient().getPatientName());
                    txtPtNo.setText(vouFilter.getPatient().getRegNo());
                } else if (vouFilter.getPtName() != null) {
                    if (!vouFilter.getPtName().isEmpty()) {
                        txtPtName.setText(vouFilter.getPtName());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getPrvFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        try {
            List<VouSrvFilter> listVouSrvFilter = dao.findAll("VouSrvFilter",
                    "key.tranOption = 'OPDVoucherSearch' and key.userId = '"
                    + Global.machineId + "'");
            srvFilterTableModel.setListVouSrvFilter(listVouSrvFilter);
            srvFilterTableModel.addEmptyRow();
            tblService.getColumnModel().getColumn(0).setCellEditor(
                    new OPDTableCellEditor(dao));
            tblService.getTableHeader().setFont(Global.lableFont);
            tblVou.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblVou.getColumnModel().getColumn(1).setPreferredWidth(70);
            tblVou.getColumnModel().getColumn(2).setPreferredWidth(190);
            tblVou.getColumnModel().getColumn(3).setPreferredWidth(30);
            tblVou.getColumnModel().getColumn(4).setPreferredWidth(20);

            //Define table selection model to single row selection.
            tblVou.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblVou.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                selectedRow = tblVou.getSelectedRow();
            });
            tblVou.getTableHeader().setFont(Global.lableFont);
            tblVou.setFont(Global.textFont);
            tblVou.setRowHeight(Global.rowHeight);
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private String getHSQL() {
        /*String strSql = "select distinct v from OPDHis v join v.listOPDDetailHis h left join v.patient p where date(v.invDate) between '"
                + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'";*/
        String strSql = "select distinct date(sh.opd_date) opd_date, sh.opd_inv_id, sh.remark, \n"
                + "sh.patient_id cus_id, sh.patient_name cus_name, "
                + "usr.user_name, sh.vou_total, sh.deleted, sh.package_id\n"
                + "from opd_his sh\n"
                + "join opd_details_his sdh on sh.opd_inv_id = sdh.vou_no\n"
                + "join opd_service med on sdh.service_id = med.service_id\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "where date(sh.opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'";

        vouFilter.setFromDate(DateUtil.toDate(txtFrom.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtTo.getText()));

        if (!txtRemark.getText().isEmpty()) {
            strSql = strSql + " and sh.remark like '%" + txtRemark.getText() + "%'";
            vouFilter.setRemark(txtRemark.getText());
        } else {
            vouFilter.setRemark(null);
        }

        if (!txtPtNo.getText().trim().isEmpty()) {
            strSql = strSql + " and sh.patient_id = '" + txtPtNo.getText().trim() + "'";
        } else if (!txtPtName.getText().trim().isEmpty()) {
            strSql = strSql + " and upper(sh.patient_name) like '%"
                    + txtPtName.getText().trim().toUpperCase() + "%'";
            vouFilter.setPtName(txtPtName.getText().trim());
        } else {
            vouFilter.setPtName(null);
        }

        if (!txtDocNo.getText().isEmpty()) {
            strSql = strSql + " and sh.doctor_id = '" + txtDocNo.getText() + "'";
        }

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            strSql = strSql + " and sh.currency_id = '" + curr.getCurrencyCode()
                    + "'";
            vouFilter.setCurrency(curr);
        } else {
            vouFilter.setCurrency(null);
        }

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            PaymentType pType = (PaymentType) cboPayment.getSelectedItem();
            strSql = strSql + " and sh.payment_id = " + pType.getPaymentTypeId();
            vouFilter.setPaymentType(pType);
        } else {
            vouFilter.setPaymentType(null);
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            Session sess = (Session) cboSession.getSelectedItem();
            strSql = strSql + " and sh.session_id = " + sess.getSessionId();
            vouFilter.setSession(sess);
        } else {
            vouFilter.setSession(null);
        }

        if (cboMachine.getSelectedItem() instanceof MachineInfo) {
            MachineInfo machine = (MachineInfo) cboMachine.getSelectedItem();
            String machineId = machine.getMachineId().toString();
            if (machineId.length() == 1) {
                machineId = "0" + machineId;
            }
            strSql = strSql + " and sh.opd_inv_id like '" + machineId + "%'";
            vouFilter.setMachine(machine);
        } else {
            vouFilter.setMachine(null);
        }

        if (cboUser.getSelectedItem() instanceof Appuser) {
            Appuser user = (Appuser) cboUser.getSelectedItem();
            strSql = strSql + " and sh.created_by = '" + user.getUserId() + "'";
            vouFilter.setAppUser(user);
        } else {
            vouFilter.setAppUser(null);
        }

        if (srvFilterTableModel.getFilterCodeStr() != null) {
            strSql = strSql + " and sdh.service_id in (" + srvFilterTableModel.getFilterCodeStr()
                    + ")";
        }

        if (option.equals("RESULT")) {
            String filterId = "";
            try {
                List<VService> listS = dao.findAllHSQL("select o from VService o where o.groupId = 1");
                for (VService vs : listS) {
                    if (filterId.isEmpty()) {
                        filterId = vs.getServiceId().toString();
                    } else {
                        filterId = filterId + "," + vs.getServiceId().toString();
                    }
                }
            } catch (Exception ex) {
                log.error("getHSQL1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }

            if (!filterId.isEmpty()) {
                strSql = strSql + " and sdh.service_id in (" + filterId + ")";
            }
        }

        strSql = strSql + " order by date(sh.opd_date) desc, sh.opd_inv_id desc";

        //Filter history save
        try {
            dao.save(vouFilter);
        } catch (Exception ex) {
            log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return strSql;
    }

    private List<VoucherSearch> getSearchVoucher() {
        String strSql = getHSQL();
        List<VoucherSearch> listVS = null;
        double ttlAmt = 0.0;
        try {
            ResultSet rs = dao.execSQL(strSql);
            listVS = new ArrayList();
            if (rs != null) {
                while (rs.next()) {
                    VoucherSearch vs = new VoucherSearch();
                    vs.setCusName(rs.getString("cus_name"));
                    vs.setCusNo(rs.getString("cus_id"));
                    vs.setInvNo(rs.getString("opd_inv_id"));
                    vs.setIsDeleted(rs.getBoolean("deleted"));
                    vs.setRefNo(rs.getString("remark"));
                    vs.setTranDate(rs.getDate("opd_date"));
                    vs.setUserName(rs.getString("user_name"));
                    vs.setVouTotal(rs.getDouble("vou_total"));
                    vs.setDcStatus(rs.getInt("package_id"));
                    listVS.add(vs);
                    if (!vs.getIsDeleted()) {
                        ttlAmt += vs.getVouTotal();
                    }
                }
            }
            txtTotalAmount.setValue(ttlAmt);

        } catch (Exception ex) {
            log.error("getSearchVoucher : " + ex.toString());
        } finally {
            dao.close();
        }

        return listVS;
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "PatientSearch":
                if (selectObj != null) {
                    Patient pt = (Patient) selectObj;
                    txtPtNo.setText(pt.getRegNo());
                    txtPtName.setText(pt.getPatientName());
                    vouFilter.setPatient(pt);
                    //vouFilter.setTrader(pt);
                } else {
                    txtPtNo.setText(null);
                    txtPtName.setText(null);
                    //vouFilter.setTrader(null);
                    vouFilter.setPatient(null);
                }

                break;
            case "DoctorSearch":
                if (selectObj != null) {
                    Doctor dt = (Doctor) selectObj;
                    txtDocNo.setText(dt.getDoctorId());
                    txtDocName.setText(dt.getDoctorName());
                } else {
                    txtDocNo.setText(null);
                    txtDocName.setText(null);
                }
                break;
        }
    }

    private void search() {
        vouTableModel.setListOPDHis(getSearchVoucher());
        lblTotalRec.setText("Total Records : " + vouTableModel.getRowCount());
    }

    private void select() {
        if (selectedRow >= 0) {
            observer.selected("OPDVouList",
                    vouTableModel.getOPDHis(tblVou.convertRowIndexToModel(selectedRow)));
            dispose();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select OPD voucher.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionMapping() {
        tblService.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblService.getActionMap().put("F8-Action", actionServiceFilterDelete);
    }
    private Action actionServiceFilterDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblService.getSelectedRow() >= 0) {
                try {
                    if (tblService.getCellEditor() != null) {
                        tblService.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }
                srvFilterTableModel.delete(tblService.getSelectedRow());
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

        jScrollPane2 = new javax.swing.JScrollPane();
        tblVou = new javax.swing.JTable();
        lblTotalRec = new javax.swing.JLabel();
        butSelect = new javax.swing.JButton();
        butSearch = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cboPayment = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDocName = new javax.swing.JTextField();
        txtDocNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboSession = new javax.swing.JComboBox();
        txtPtNo = new javax.swing.JTextField();
        txtFrom = new javax.swing.JFormattedTextField();
        txtPtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cboMachine = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        cboUser = new javax.swing.JComboBox<>();
        txtTotalAmount = new javax.swing.JFormattedTextField();
        lblTotalAmount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OPD Vou Search");
        setPreferredSize(new java.awt.Dimension(1042, 600));

        tblVou.setFont(Global.textFont);
        tblVou.setModel(vouTableModel);
        tblVou.setRowHeight(23);
        tblVou.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblVouFocusGained(evt);
            }
        });
        tblVou.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVouMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVou);

        lblTotalRec.setFont(Global.lableFont);
        lblTotalRec.setText("Total Records :");

        butSelect.setFont(Global.lableFont);
        butSelect.setText("Select");
        butSelect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                butSelectMouseClicked(evt);
            }
        });

        butSearch.setFont(Global.lableFont);
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

        cboPayment.setFont(Global.textFont);
        cboPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboPaymentFocusGained(evt);
            }
        });
        cboPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Payment");

        cboCurrency.setFont(Global.textFont);
        cboCurrency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboCurrencyFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtRemark.setFont(Global.textFont);
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtDocName.setEditable(false);
        txtDocName.setFont(Global.textFont);
        txtDocName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDocNameMouseClicked(evt);
            }
        });

        txtDocNo.setFont(Global.textFont);
        txtDocNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDocNoFocusGained(evt);
            }
        });
        txtDocNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDocNoActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Doctor");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date ");

        tblService.setFont(Global.textFont);
        tblService.setModel(srvFilterTableModel);
        tblService.setRowHeight(23);
        jScrollPane1.setViewportView(tblService);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Session");

        txtTo.setEditable(false);
        txtTo.setFont(Global.textFont);
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Patient");

        cboSession.setFont(Global.textFont);
        cboSession.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboSessionFocusGained(evt);
            }
        });

        txtPtNo.setFont(Global.textFont);
        txtPtNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPtNoFocusGained(evt);
            }
        });
        txtPtNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPtNoActionPerformed(evt);
            }
        });

        txtFrom.setEditable(false);
        txtFrom.setFont(Global.textFont);
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtPtName.setFont(Global.textFont);
        txtPtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPtNameMouseClicked(evt);
            }
        });
        txtPtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPtNameActionPerformed(evt);
            }
        });

        jLabel2.setText("To");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Machine");

        cboMachine.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("User");

        cboUser.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboSession, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtDocNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDocName, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFrom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTo))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPtName))
                            .addComponent(cboPayment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboCurrency, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRemark))
                        .addGap(2, 2, 2))
                    .addComponent(cboMachine, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboUser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDocNo, txtPtNo});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPtNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDocNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDocName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cboMachine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtTotalAmount.setEditable(false);
        txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAmount.setFont(Global.lableFont);

        lblTotalAmount.setFont(Global.lableFont);
        lblTotalAmount.setText("Total Amount : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalAmount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butSelect))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butSearch, butSelect});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTotalAmount)
                                .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTotalRec)
                                .addComponent(butSelect)
                                .addComponent(butSearch)))
                        .addGap(10, 10, 10)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboPaymentActionPerformed

  private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_butSearchActionPerformed

  private void txtPtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPtNameActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_txtPtNameActionPerformed

  private void txtPtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPtNameMouseClicked
      if (evt.getClickCount() == 2) {
          PatientSearch dialog = new PatientSearch(dao, this);
      }
  }//GEN-LAST:event_txtPtNameMouseClicked

  private void txtDocNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDocNameMouseClicked
      if (evt.getClickCount() == 2) {
          DoctorSearchDialog dialog = new DoctorSearchDialog(dao, this);
      }
  }//GEN-LAST:event_txtDocNameMouseClicked

  private void butSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_butSearchMouseClicked
      search();
  }//GEN-LAST:event_butSearchMouseClicked

  private void butSelectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_butSelectMouseClicked
      select();
  }//GEN-LAST:event_butSelectMouseClicked

  private void tblVouMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVouMouseClicked
      if (evt.getClickCount() == 2) {
          select();
      }
  }//GEN-LAST:event_tblVouMouseClicked

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

  private void txtPtNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPtNoActionPerformed
      if (txtPtNo.getText().isEmpty()) {
          selected("PatientSearch", null);
      } else if (txtPtNo.getText() != null && !txtPtNo.getText().isEmpty()) {
          try {
              Patient pt;

              dao.open();
              pt = (Patient) dao.find(Patient.class, txtPtNo.getText());
              dao.close();

              if (pt == null) {
                  txtPtNo.setText(null);
                  txtPtName.setText(null);

                  JOptionPane.showMessageDialog(Util1.getParent(),
                          "Invalid patient code.",
                          "Patient Code", JOptionPane.ERROR_MESSAGE);
              } else {
                  selected("PatientSearch", pt);
                  tblService.requestFocusInWindow();
              }
          } catch (Exception ex) {
              log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
          }
      }
  }//GEN-LAST:event_txtPtNoActionPerformed

    private void tblVouFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblVouFocusGained
        focusCtrlName = "tblVou";
    }//GEN-LAST:event_tblVouFocusGained

    private void txtPtNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPtNoFocusGained
        focusCtrlName = "txtPtNo";
        txtPtNo.selectAll();
    }//GEN-LAST:event_txtPtNoFocusGained

    private void txtDocNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDocNoFocusGained
        focusCtrlName = "txtDocNo";
        txtDocNo.selectAll();
    }//GEN-LAST:event_txtDocNoFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        focusCtrlName = "txtRemark";
        txtRemark.selectAll();
    }//GEN-LAST:event_txtRemarkFocusGained

    private void cboCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboCurrencyFocusGained
        focusCtrlName = "cboCurrency";
    }//GEN-LAST:event_cboCurrencyFocusGained

    private void cboPaymentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboPaymentFocusGained
        focusCtrlName = "cboPayment";
    }//GEN-LAST:event_cboPaymentFocusGained

    private void cboSessionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboSessionFocusGained
        focusCtrlName = "cboSession";
    }//GEN-LAST:event_cboSessionFocusGained

    private void txtDocNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDocNoActionPerformed
        // TODO add your handling code here:
        if (txtDocNo.getText() != null && !txtDocNo.getText().isEmpty()) {
            try {
                Doctor dr;

                dao.open();
                dr = (Doctor) dao.find(Doctor.class, txtDocNo.getText());
                dao.close();

                if (dr == null) {
                    txtDocNo.setText(null);
                    txtDocName.setText(null);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid doctor code.",
                            "Doctor Code", JOptionPane.ERROR_MESSAGE);

                } else {
                    selected("DoctorSearch", dr);
                }
            } catch (Exception ex) {
                log.error("getDoctor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtDocNo.setText(null);
            txtDocName.setText(null);
        }
    }//GEN-LAST:event_txtDocNoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelect;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox<String> cboMachine;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JComboBox<String> cboUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JTable tblService;
    private javax.swing.JTable tblVou;
    private javax.swing.JTextField txtDocName;
    private javax.swing.JTextField txtDocNo;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtPtName;
    private javax.swing.JTextField txtPtNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotalAmount;
    // End of variables declaration//GEN-END:variables
}
