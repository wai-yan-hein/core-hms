/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.pharmacy.ui.common.AdjSearchTableModel;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
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
 * @author WSwe
 */
public class AdjSearch extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(AdjSearch.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private Object parent;
    private SelectionObserver observer;
    private int selectedRow = -1;
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "AdjSearch");
    private AdjSearchTableModel vouTableModel = new AdjSearchTableModel();
    private TableRowSorter<TableModel> sorter;
    private VouFilter vouFilter;
    private int mouseClick = 2;
    
    /**
     * Creates new form AdjSearch
     */
    public AdjSearch(Object parent, SelectionObserver observer, AbstractDataAccess dao) {
        initComponents();

        this.parent = parent;
        this.observer = observer;
        //this.dao = dao;

        try {
            dao.open();
            initCombo();
            //Search history
            getPrvFilter();
            search();
            initTableMedicine();
            initTableVoucher();
            sorter = new TableRowSorter(tblVoucher.getModel());
            tblVoucher.setRowSorter(sorter);
            dao.close();
        } catch (Exception ex) {
            log.error("AdjSearch : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        addSelectionListenerVoucher();
        
        String propValue = Util1.getPropValue("system.date.mouse.click");
        if(propValue != null){
            if(!propValue.equals("-")){
                if(!propValue.isEmpty()){
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if(tmpValue != 0){
                        mouseClick = tmpValue;
                    }
                }
            }
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("MedicineList")) {
        }
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        BindingUtil.BindComboFilter(cboLocation, dao.findAll("Location"));
        BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));

        new ComBoBoxAutoComplete(cboLocation);
        new ComBoBoxAutoComplete(cboSession);

        cboLocation.setSelectedIndex(0);
        cboSession.setSelectedIndex(0);
    }// </editor-fold>

    private void initTableMedicine() {
        List<VouCodeFilter> listMedicine = dao.findAll("VouCodeFilter",
                "key.tranOption = 'AdjSearch' and key.userId = '"
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
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(30);

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
                getMedList("");
            }
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
        if(cboLocation.getSelectedItem() instanceof Location){
            locationId = ((Location)cboLocation.getSelectedItem()).getLocationId();
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
        /*String strSql = "select distinct v from AdjHis v join v.listDetail h where v.adjDate between '"
                + DateUtil.toDateStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQL(txtToDate.getText()) + "'";*/
        String strSql = "select distinct date(sh.adj_date) adj_date, sh.adj_id, sh.remark, \n"
                + "usr.user_name, l.location_name, sh.deleted\n"
                + "from adj_his sh\n"
                + "join adj_detail_his sdh on sh.adj_id = sdh.vou_no\n"
                + "join medicine med on sdh.med_id = med.med_id\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "join location l on sh.location = l.location_id \n"
                + "where sh.adj_date between '" + DateUtil.toDateTimeStrMYSQL(txtFromDate.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtToDate.getText()) + "'";
        
        vouFilter.setFromDate(DateUtil.toDate(txtFromDate.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtToDate.getText()));

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strSql = strSql + " and sh.location = " + location.getLocationId();
            vouFilter.setLocation(location);
        } else {
            vouFilter.setLocation(null);
        }

        if (cboSession.getSelectedItem() instanceof Session) {
            Session session = (Session) cboSession.getSelectedItem();
            strSql = strSql + " and sh.session_id = " + session.getSessionId();
            vouFilter.setSession(session);
        } else {
            vouFilter.setSession(null);
        }

        if (txtDesp.getText() != null && !txtDesp.getText().isEmpty()) {
            strSql = strSql + " and sh.remark = '" + txtDesp.getText() + "'";
        }

        if (txtVouNo.getText() != null && !txtVouNo.getText().isEmpty()) {
            strSql = strSql + " and sh.adj_id = '" + txtVouNo.getText() + "'";
        }

        vouFilter.setVouNo(txtVouNo.getText());

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

        return strSql;
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
                    vs.setInvNo(rs.getString("sh.adj_id"));
                    vs.setIsDeleted(rs.getBoolean("deleted"));
                    vs.setLocation(rs.getString("location_name"));
                    vs.setRefNo(rs.getString("remark"));
                    vs.setTranDate(rs.getDate("adj_date"));
                    vs.setUserName(rs.getString("user_name"));

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
        vouTableModel.setListAdjHis(getSearchVoucher());
        lblTotalRec.setText("Total Records : " + vouTableModel.getRowCount());
    }

    private void select() {
        if (selectedRow >= 0) {
            observer.selected("AdjVouList",
                    vouTableModel.getSelectVou(tblVoucher.convertRowIndexToModel(selectedRow)));

            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select adjust voucher.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getPrvFilter() {
        VouFilter tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'AdjVouSearch'"
                + " and key.userId = '" + Global.loginUser.getUserId() + "'");

        if (tmpFilter == null) {
            vouFilter = new VouFilter();
            vouFilter.getKey().setTranOption("AdjVouSearch");
            vouFilter.getKey().setUserId(Global.loginUser.getUserId());

            txtFromDate.setText(DateUtil.getTodayDateStr());
            txtToDate.setText(DateUtil.getTodayDateStr());
        } else {
            vouFilter = tmpFilter;
            txtFromDate.setText(DateUtil.toDateStr(vouFilter.getFromDate()));
            txtToDate.setText(DateUtil.toDateStr(vouFilter.getToDate()));

            if (vouFilter.getLocation() != null) {
                cboLocation.setSelectedItem(vouFilter.getLocation());
            }

            if (vouFilter.getSession() != null) {
                cboSession.setSelectedItem(vouFilter.getSession());
            }

            txtVouNo.setText(vouFilter.getVouNo());
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFromDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtToDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cboSession = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicine = new javax.swing.JTable(tblMedicineModel);
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        lblTotalRec = new javax.swing.JLabel();
        butSearch = new javax.swing.JButton();
        butSelect = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(759, 473));

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

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Location");

        cboLocation.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Session");

        cboSession.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Remark");

        txtDesp.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou No");

        txtVouNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblMedicine.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblMedicine.setRowHeight(23);
        tblMedicine.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblMedicine);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboSession, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDesp)
                            .addComponent(txtVouNo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
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
                    .addComponent(jLabel4)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
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

        lblTotalRec.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 275, Short.MAX_VALUE)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butSearch, butSelect});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butSelect)
                        .addComponent(butSearch))
                    .addComponent(lblTotalRec))
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelect;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboSession;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblMedicine;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtFromDate;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
