/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.PurVouSearchTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import static com.cv.app.pharmacy.ui.util.SaleVouSearch.log;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
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
 * @author wswe
 */
public class PurchaseVouSearch extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(PurchaseVouSearch.class.getName());
    private final AbstractDataAccess dao;
    private Object parent;
    private SelectionObserver observer;
    private int selectedRow = -1;
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "PurSearch");
    private PurVouSearchTableModel purVouTableModel = new PurVouSearchTableModel();
    private TableRowSorter<TableModel> sorter;
    private VouFilter vouFilter;
    private int mouseClick = 2;
    
    /**
     * Creates new form PurchaseVouSearch
     */
    public PurchaseVouSearch(Object parent, SelectionObserver observer, AbstractDataAccess dao) {
        initComponents();

        this.parent = parent;
        this.observer = observer;
        this.dao = dao;

        try {
            //tblMedicineModel = new CodeTableModel(dao, true, "PurSearch");
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("PurchaseVouSearc : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        //Search history
        getPrvFilter();
        initTableMedicine();
        search();
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
                    txtCusCode.setText(cus.getTraderId());
                    txtCusName.setText(cus.getTraderName());
                    vouFilter.setTrader(cus);
                } else {
                    vouFilter.setTrader(null);
                }

                break;
        }
    }

    private void getCustomerList() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Supplier List", dao);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboPayment, dao.findAll("PaymentType"));
        BindingUtil.BindComboFilter(cboLocation, dao.findAll("Location"));
        BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
        BindingUtil.BindComboFilter(cboCusGroup, dao.findAll("CustomerGroup"));
        BindingUtil.BindComboFilter(cboVStatus, dao.findAll("VouStatus"));

        new ComBoBoxAutoComplete(cboPayment);
        new ComBoBoxAutoComplete(cboLocation);
        new ComBoBoxAutoComplete(cboSession);
        new ComBoBoxAutoComplete(cboCusGroup);
        new ComBoBoxAutoComplete(cboVStatus);

        cboPayment.setSelectedIndex(0);
        cboLocation.setSelectedIndex(0);
        cboSession.setSelectedIndex(0);
        cboCusGroup.setSelectedIndex(0);
        cboVStatus.setSelectedIndex(0);
    }

    private void initTableMedicine() {
        List<VouCodeFilter> listMedicine = dao.findAll("VouCodeFilter",
                "key.tranOption = 'PurSearch' and key.userId = '"
                + Global.loginUser.getUserId() + "'");
        tblMedicineModel.setListCodeFilter(listMedicine);
        tblMedicineModel.addEmptyRow();
        tblMedicine.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblMedicine.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMedicine.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
    }

    private void initTableVoucher() {
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(7).setPreferredWidth(30);

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
                if(tblMedicine.getCellEditor() != null){
                    tblMedicine.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
            }

            getMedList("");
        }
    };

    // <editor-fold defaultstate="collapsed" desc="getMedInfo">
    public void getMedInfo(String medCode) {
        Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                + medCode + "' and active = true");

        if (medicine != null) {
            selected("MedicineList", medicine);
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                    "Invalid.", JOptionPane.ERROR_MESSAGE);
            //getMedList(medCode);
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
        //tblMedicine.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        //tblMedicine.getActionMap().put("F3-Action", actionMedList);

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
        /*String strSql = "select distinct v from PurHis v join v.purDetailHis h where v.purDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";*/
        String strSql = "select distinct date(sh.pur_date) pur_date, sh.pur_inv_id, sh.remark, sh.ref_no, \n"
                + "sh.cus_id, tr.trader_name, "
                + "usr.user_name, sh.vou_total, l.location_name, sh.deleted, sh.balance \n"
                + "from pur_his sh\n"
                + "join pur_detail_his sdh on sh.pur_inv_id = sdh.vou_no\n"
                + "join medicine med on sdh.med_id = med.med_id\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "join location l on sh.location = l.location_id \n"
                + "left join trader tr on sh.cus_id = tr.trader_id "
                + "where sh.pur_date between '" + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";
        
        vouFilter.setFromDate(DateUtil.toDate(txtFromDate.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtToDate.getText()));

        if (txtCusCode.getText() != null && !txtCusCode.getText().isEmpty()) {
            strSql = strSql + " and sh.cus_id = '" + txtCusCode.getText() + "'";
        } else {
            vouFilter.setTrader(null);
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strSql = strSql + " and sh.location = " + location.getLocationId();
        } else {
            vouFilter.setLocation(null);
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                strSql = strSql + " and sh.location_id in (select a.location_id from user_location_mapping a \n"
                        + "where a.user_id = '" + Global.loginUser.getUserId()
                        + "' and a.allow_sale = true)";
            }
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            Session session = (Session) cboSession.getSelectedItem();
            strSql = strSql + " and sh.session_id = " + session.getSessionId();
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

        if (!txtVouNo.getText().trim().isEmpty()) {
            strSql = strSql + " and sh.pur_inv_id = '" + txtVouNo.getText().trim() + "'";
            vouFilter.setVouNo(txtVouNo.getText().trim());
        } else {
            vouFilter.setVouNo(null);
        }

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            CustomerGroup cusGroup = (CustomerGroup) cboCusGroup.getSelectedItem();
            strSql = strSql + " and tr.group_id = '"
                        + cusGroup.getGroupId() + "'";
        } else {
            vouFilter.setCusGroup(null);
        }

        if (cboVStatus.getSelectedItem() instanceof VouStatus) {
            VouStatus vouStatus = (VouStatus) cboVStatus.getSelectedItem();
            strSql = strSql + " and sh.vou_status = " + vouStatus.getStatusId();
            vouFilter.setVouStatus(vouStatus);
        } else {
            vouFilter.setVouStatus(null);
        }

        if (!txtRemark.getText().trim().isEmpty()) {
            strSql = strSql + " and sh.remark like '%" + txtRemark.getText() + "%' ";
            vouFilter.setRemark(txtRemark.getText());
        } else {
            vouFilter.setRemark(null);
        }

        //Filter history save
        try {
            dao.save(vouFilter);
        } catch (Exception ex) {
            log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        List<VouCodeFilter> listMedicine = tblMedicineModel.getListCodeFilter();

        if (listMedicine != null) {
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
        }

        return strSql + " order by date(sh.pur_date) DESC, sh.pur_inv_id DESC";
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
                    vs.setCusName(rs.getString("trader_name"));
                    vs.setCusNo(rs.getString("cus_id"));
                    vs.setInvNo(rs.getString("sh.pur_inv_id"));
                    vs.setIsDeleted(rs.getBoolean("deleted"));
                    vs.setLocation(rs.getString("location_name"));
                    vs.setRefNo(rs.getString("ref_no"));
                    vs.setTranDate(rs.getDate("pur_date"));
                    vs.setUserName(rs.getString("user_name"));
                    vs.setVouTotal(rs.getDouble("vou_total"));
                    vs.setBalance(rs.getDouble("balance"));

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
        purVouTableModel.setListPurHis(getSearchVoucher());
        txtTotalAmount.setValue(purVouTableModel.getTotal());
        lblTotalRec.setText("Total Records : " + purVouTableModel.getRowCount());
    }

    private void select() {
        if (selectedRow >= 0) {
            observer.selected("PurVouList",
                    purVouTableModel.getSelectVou(tblVoucher.convertRowIndexToModel(selectedRow)));

            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select purchase voucher.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getPrvFilter() {
        VouFilter tmpFilter;
        String propValue = Util1.getPropValue("system.search.filter.history");
        if (propValue.equals("M")) {
            tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'PurVouSearch'"
                    + " and key.userId = '" + Global.machineId + "'");
        } else {
            tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'PurVouSearch'"
                    + " and key.userId = '" + Global.loginUser.getUserId() + "'");
        }

        if (tmpFilter == null) {
            vouFilter = new VouFilter();
            vouFilter.getKey().setTranOption("PurVouSearch");
            if (propValue.equals("M")) {
                vouFilter.getKey().setUserId(Global.machineId);
            } else {
                vouFilter.getKey().setUserId(Global.loginUser.getUserId());
            }

            txtFromDate.setText(DateUtil.getTodayDateStr());
            txtToDate.setText(DateUtil.getTodayDateStr());
        } else {
            vouFilter = tmpFilter;
            txtFromDate.setText(DateUtil.toDateStr(vouFilter.getFromDate()));
            txtToDate.setText(DateUtil.toDateStr(vouFilter.getToDate()));

            if (vouFilter.getTrader() != null) {
                txtCusCode.setText(vouFilter.getTrader().getTraderId());
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

            if (vouFilter.getCusGroup() != null) {
                cboCusGroup.setSelectedItem(vouFilter.getCusGroup());
            }

            if (vouFilter.getVouStatus() != null) {
                cboVStatus.setSelectedItem(vouFilter.getVouStatus());
            }

            if (vouFilter.getRemark() != null) {
                txtRemark.setText(vouFilter.getRemark());
            }
        }
    }

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            if (!traderId.contains("SUP")) {
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    if (!Util1.getPropValue("system.default.customer").equals(traderId)) {
                        traderId = "SUP" + traderId;
                    }
                }
            }
            /*if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                String strSql;
                int locationId;
                if (cboLocation.getSelectedItem() instanceof Location) {
                    locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId = "
                            + locationId + ") and o.traderId = '" + traderId.toUpperCase() + "' order by o.traderName";
                } else {
                    strSql = "select o from Trader o where "
                            + "o.active = true and o.traderId in (select a.key.traderId "
                            + "from LocationTraderMapping a where a.key.locationId in ("
                            + "select a.key.locationId from UserLocationMapping a "
                            + "where a.key.userId = '" + Global.loginUser.getUserId()
                            + "' and a.isAllowSessCheck = true)) and o.traderId = '"
                            + traderId.toUpperCase() + "' order by o.traderName";
                }
                List<Trader> listTrader = dao.findAllHSQL(strSql);
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {*/
                cus = (Trader) dao.find(Trader.class, traderId);
            //}
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
        jLabel9 = new javax.swing.JLabel();
        cboCusGroup = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(tblMedicineModel);
        jLabel10 = new javax.swing.JLabel();
        cboVStatus = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        butSearch = new javax.swing.JButton();
        butSelect = new javax.swing.JButton();
        lblTotalRec = new javax.swing.JLabel();
        butPromo = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
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

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Cus-G");

        cboCusGroup.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblMedicine.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblMedicine.setRowHeight(23);
        tblMedicine.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblMedicine);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("V-Status");

        cboVStatus.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Ref. Vou.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
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
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCusName))
                            .addComponent(cboLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboSession, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboPayment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtVouNo)
                            .addComponent(cboCusGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 25, Short.MAX_VALUE))
                            .addComponent(cboVStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark)))
                .addContainerGap())
        );
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
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboVStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblVoucher.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblVoucher.setModel(purVouTableModel);
        tblVoucher.setRowHeight(23);
        tblVoucher.setShowVerticalLines(false);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVoucher);

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

        lblTotalRec.setText("Total Records : 0");

        butPromo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butPromo.setText("Promo List");
        butPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPromoActionPerformed(evt);
            }
        });

        jLabel11.setText("Total Amount : ");

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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(butPromo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSelect)
                    .addComponent(butSearch)
                    .addComponent(lblTotalRec)
                    .addComponent(butPromo)
                    .addComponent(jLabel11)
                    .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void txtCusCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusCodeActionPerformed
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        } else {
            Trader trader = getTrader(txtCusCode.getText().trim());
            if(trader != null){
                txtCusCode.setText(trader.getTraderId());
                txtCusName.setText(trader.getTraderName());
            }
        }
    }//GEN-LAST:event_txtCusCodeActionPerformed

    private void txtCusCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusCodeFocusLost
        if (txtCusCode.getText() == null || txtCusCode.getText().isEmpty()) {
            txtCusName.setText(null);
        }
    }//GEN-LAST:event_txtCusCodeFocusLost

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == 2) {
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
    }//GEN-LAST:event_butSearchActionPerformed

    private void butSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelectActionPerformed
        select();
    }//GEN-LAST:event_butSelectActionPerformed

    private void butPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPromoActionPerformed
        // TODO add your handling code here:
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Purchase Promo Search", dao);
    }//GEN-LAST:event_butPromoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butPromo;
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelect;
    private javax.swing.JComboBox cboCusGroup;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JComboBox cboVStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtFromDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmount;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
