/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * RetInVouSearch.java
 *
 * Created on Jul 24, 2012, 10:20:48 PM
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.RetInVouSearchTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class RetInVouSearch extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(RetInVouSearch.class.getName());
    private AbstractDataAccess dao;
    private Object parent;
    private SelectionObserver observer;
    private int selectedRow = -1;
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "RetInSearch");
    private RetInVouSearchTableModel vouTableModel = new RetInVouSearchTableModel();
    private TableRowSorter<TableModel> sorter;
    private VouFilter vouFilter;
    private int mouseClick = 2;
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");

    /**
     * Creates new form RetInVouSearch
     */
    public RetInVouSearch(Object parent, SelectionObserver observer, AbstractDataAccess dao) {
        initComponents();

        this.parent = parent;
        this.observer = observer;
        this.dao = dao;

        try {
            //tblMedicineModel = new CodeTableModel(dao, true, "RetInSearch");
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("RetInVoucher : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        //Search history
        getPrvFilter();

        search();
        initTableMedicine();
        sorter = new TableRowSorter(tblVoucher.getModel());
        tblVoucher.setRowSorter(sorter);
        actionMapping();
        initTableVoucher();
        addSelectionListenerVoucher();

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
                    txtCusName.setText(cus.getTraderName());
                    vouFilter.setTrader(cus);
                } else {
                    vouFilter.setTrader(null);
                }

                break;
        }
    }

    private void getCustomerList() {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Customer List", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindComboFilter(cboLocation, dao.findAll("Location"));
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));

            new ComBoBoxAutoComplete(cboPayment);
            new ComBoBoxAutoComplete(cboLocation);
            new ComBoBoxAutoComplete(cboSession);

            cboPayment.setSelectedIndex(0);
            cboLocation.setSelectedIndex(0);
            cboSession.setSelectedIndex(0);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    private void initTableMedicine() {
        try {
            List<VouCodeFilter> listMedicine = dao.findAll("VouCodeFilter",
                    "key.tranOption = 'RetInSearch' and key.userId = '"
                    + Global.machineId + "'");
            tblMedicineModel.setListCodeFilter(listMedicine);
        } catch (Exception ex) {
            log.error("initTableMedicine : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblMedicineModel.addEmptyRow();
        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMedicine.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void initTableVoucher() {
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(20);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(30);

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
    private Action actionMedList = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblMedicine.getCellEditor() != null) {
                    tblMedicine.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                getMedList("");
            }
        }
    };

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

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
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
    }// </editor-fold>
    private Action actionMedDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblMedicine.getSelectedRow() >= 0) {
                tblMedicineModel.delete(tblMedicine.getSelectedRow());
            }
        }
    };

    private String getHSQL() {
        /*String strSql = "select distinct v from RetInHis v join v.listDetail h where v.retInDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";*/
        String strFieldName = "sh.cus_id";
        if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
            strFieldName = "tr.stu_no";
        }
        String strSql = "select distinct date(sh.ret_in_date) ret_in_date, sh.ret_in_id, sh.remark, \n"
                + "ifnull(sh.cus_id,ifnull(sh.reg_no, sh.stu_no)) cus_id, ifnull(tr.trader_name, ifnull(pd.patient_name, sh.stu_name)) cus_name, "
                + "usr.user_name, sh.vou_total, l.location_name, sh.deleted, \n"
                + "tr.stu_no \n"
                + "from ret_in_his sh\n"
                + "join ret_in_detail_his sdh on sh.ret_in_id = sdh.vou_no\n"
                + "join medicine med on sdh.med_id = med.med_id\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "join location l on sh.location = l.location_id \n"
                + "left join patient_detail pd on sh.reg_no = pd.reg_no \n"
                + "left join trader tr on sh.cus_id = tr.trader_id "
                + "where sh.ret_in_date between '" + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";

        vouFilter.setFromDate(DateUtil.toDate(txtFromDate.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtToDate.getText()));

        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                strSql = strSql + " and sh.reg_no = '" + txtCusCode.getText() + "'";
            } else {
                strSql = strSql + " and " + strFieldName + " = '" + txtCusCode.getText() + "'";
            }
        } else {
            vouFilter.setTrader(null);
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strSql = strSql + " and sh.location = " + location.getLocationId();
            vouFilter.setLocation(location);
        } else {
            vouFilter.setLocation(null);
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                strSql = strSql + " and sh.location in (select a.location_id from user_location_mapping a \n"
                        + "where a.user_id = '" + Global.loginUser.getUserId()
                        + "' and a.allow_sale = true)";
            }
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            Session session = (Session) cboSession.getSelectedItem();
            strSql = strSql + " and sh.session_id = " + session.getSessionId();
            vouFilter.setSession(session);
        } else {
            vouFilter.setSession(null);
        }

        if (cboPayment.getSelectedItem() instanceof PaymentType) {
            PaymentType paymentType = (PaymentType) cboPayment.getSelectedItem();
            strSql = strSql + " and sh.payment_type = " + paymentType.getPaymentTypeId();
            vouFilter.setPaymentType(paymentType);
        } else {
            vouFilter.setPaymentType(null);
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
            dao.save(vouFilter);
        } catch (Exception ex) {
            log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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

        return strSql + " order by date(sh.ret_in_date) desc, sh.ret_in_id";
    }

    private List<VoucherSearch> getSearchVoucher() {
        String strSql = getHSQL();
        List<VoucherSearch> listVS = null;

        try {
            ResultSet rs = dao.execSQL(strSql);
            listVS = new ArrayList();
            if (rs != null) {
                while (rs.next()) {
                    VoucherSearch vs = new VoucherSearch();
                    vs.setCusName(rs.getString("cus_name"));
                    if (prifxStatus.equals("Y")) {
                        log.info("stu_no : " + rs.getString("stu_no"));
                        vs.setCusNo(rs.getString("stu_no"));
                    } else {
                        vs.setCusNo(rs.getString("cus_id"));
                    }
                    vs.setInvNo(rs.getString("ret_in_id"));
                    vs.setIsDeleted(rs.getBoolean("deleted"));
                    vs.setLocation(rs.getString("location_name"));
                    vs.setRefNo(rs.getString("remark"));
                    vs.setTranDate(rs.getDate("ret_in_date"));
                    vs.setUserName(rs.getString("user_name"));
                    vs.setVouTotal(rs.getDouble("vou_total"));

                    listVS.add(vs);
                }
            }

        } catch (Exception ex) {
            log.error("getSearchVoucher : " + ex.toString());
        } finally {
            dao.close();
        }

        return listVS;
    }

    private void search() {
        vouTableModel.setListRetInHis(getSearchVoucher());
        txtTotalAmount.setValue(vouTableModel.getTotal());
        lblTotalRec.setText("Total Records : " + vouTableModel.getRowCount());
    }

    private void select() {
        if (selectedRow >= 0) {
            observer.selected("RetInVouList",
                    vouTableModel.getSelectVou(tblVoucher.convertRowIndexToModel(selectedRow)));

            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select return in voucher.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getPrvFilter() {
        try {
            VouFilter tmpFilter;
            String propValue = Util1.getPropValue("system.search.filter.history");
            if (propValue.equals("M")) {
                tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'RetInVouSearch'"
                        + " and key.userId = '" + Global.machineId + "'");
            } else {
                tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'RetInVouSearch'"
                        + " and key.userId = '" + Global.machineId + "'");
            }

            if (tmpFilter == null) {
                vouFilter = new VouFilter();
                vouFilter.getKey().setTranOption("RetInVouSearch");
                if (propValue.equals("M")) {
                    vouFilter.getKey().setUserId(Global.machineId);
                } else {
                    vouFilter.getKey().setUserId(Global.machineId);
                }

                txtFromDate.setText(DateUtil.getTodayDateStr());
                txtToDate.setText(DateUtil.getTodayDateStr());
            } else {
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
            }
        } catch (Exception ex) {
            log.error("getPrvFilter : " + ex.getMessage());
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
                int locationId;
                String strSql;
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
                            + "' and a.isAllowRetIn = true)) and " + strFieldName + " = '"
                            + traderId.toUpperCase() + "' order by o.traderName";
                }
                List<Trader> listTrader = dao.findAllHSQL(strSql);
                log.info("strSql : " + strSql);
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                String strSql = "select o from Trader where " + strFieldName + " = '" + traderId + "'";
                log.info("strSql 1: " + strSql);
                List<Trader> listTrader = dao.findAllHSQL(strSql);
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(tblMedicineModel);
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        txtDesp = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        lblTotalRec = new javax.swing.JLabel();
        butSearch = new javax.swing.JButton();
        butSelect = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JFormattedTextField();

        setPreferredSize(new java.awt.Dimension(1200, 600));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtFromDate.setEditable(false);
        txtFromDate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFromDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromDateMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        txtToDate.setEditable(false);
        txtToDate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtToDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToDateMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Customer");

        txtCusCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusCode.addFocusListener(new java.awt.event.FocusAdapter() {
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
        txtCusName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Location");

        cboLocation.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Session");

        cboSession.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Payment");

        cboPayment.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou No");

        txtVouNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblMedicine.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblMedicine.setRowHeight(23);
        tblMedicine.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblMedicine);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Code");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Desp");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDesp)
                            .addComponent(txtCode)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCusName, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
                            .addComponent(cboLocation, 0, 195, Short.MAX_VALUE)
                            .addComponent(cboSession, 0, 195, Short.MAX_VALUE)
                            .addComponent(cboPayment, 0, 195, Short.MAX_VALUE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 4, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel8, jLabel9});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblVoucher.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblVoucher.setModel(vouTableModel);
        tblVoucher.setRowHeight(23);
        tblVoucher.setShowVerticalLines(false);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVoucher);

        lblTotalRec.setText("Total Records : 0");

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        butSelect.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSelect.setText("Select");
        butSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelectActionPerformed(evt);
            }
        });

        jLabel7.setText("Total Amount : ");

        txtTotalAmount.setEditable(false);
        txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butSearch, butSelect});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butSelect)
                        .addComponent(butSearch))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTotalRec)
                        .addComponent(jLabel7)
                        .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusCodeMouseClicked

    private void txtCusCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusCodeFocusLost
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        }
    }//GEN-LAST:event_txtCusCodeFocusLost

    private void txtCusCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusCodeActionPerformed
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        } else {
            Trader trader = getTrader(txtCusCode.getText().trim());
            if (trader != null) {
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    txtCusCode.setText(trader.getStuCode());
                } else {
                    txtCusCode.setText(trader.getTraderId());
                }
                txtCusName.setText(trader.getTraderName());
                vouFilter.setTrader(trader);
            } else {
                vouFilter.setTrader(null);
                txtCusCode.setText(null);
                txtCusName.setText(null);
            }
        }
    }//GEN-LAST:event_txtCusCodeActionPerformed

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void butSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelectActionPerformed
        select();
    }//GEN-LAST:event_butSelectActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelect;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboSession;
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
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtFromDate;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmount;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
