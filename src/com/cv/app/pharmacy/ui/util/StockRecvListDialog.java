/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.VouFilter;
import com.cv.app.pharmacy.ui.common.CodeTableModel;
import com.cv.app.pharmacy.ui.common.StockReceiveSearchTableModel;
import static com.cv.app.pharmacy.ui.util.DamageSearch.log;
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author winswe
 */
public class StockRecvListDialog extends javax.swing.JDialog implements SelectionObserver {

    static Logger log = Logger.getLogger(StockRecvListDialog.class.getName());
    private AbstractDataAccess dao;
    private SelectionObserver observer;
    private List<Medicine> listMedicine = ObservableCollections.observableList(new ArrayList<Medicine>());
    private CodeTableModel tblMedicineModel = new CodeTableModel(Global.dao, true, "StkRecvSearch");
    private int selectedRow = -1;
    private StockReceiveSearchTableModel vouTableModel = new StockReceiveSearchTableModel();
    private TableRowSorter<TableModel> sorter;
    private VouFilter vouFilter;
    private int mouseClick = 2;
    
    /**
     * Creates new form StockRecvListDialog
     */
    public StockRecvListDialog(AbstractDataAccess dao, SelectionObserver observer) {
        super(Util1.getParent(), true);

        this.dao = dao;
        this.observer = observer;

        initComponents();
        initCombo();
        initTableMedicine();
        actionMapping();
        //Search history
        getPrvFilter();
        search();
        initTblRecvList();
        sorter = new TableRowSorter(tblRecvList.getModel());
        tblRecvList.setRowSorter(sorter);
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
        
        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        this.show();
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboLocation, dao.findAll("Location"));
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("MedicineList")) {
        }
    }

    private void initTableMedicine() {
        tblMedicineModel.addEmptyRow();
        tblCodeFilter.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCodeFilter.getColumnModel().getColumn(1).setPreferredWidth(200);
    }
    private Action actionMedList = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(tblCodeFilter.getCellEditor() != null){
                    tblCodeFilter.getCellEditor().stopCellEditing();
                }
                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex1.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            } catch (Exception ex) {
                
            }
        }
    };
    private Action actionMedDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblCodeFilter.getSelectedRow() >= 0) {
                tblMedicineModel.delete(tblCodeFilter.getSelectedRow());
            }
        }
    };

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
    }

    private void actionMapping() {
        //F3 event on tblSale
        tblCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblCodeFilter.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblCodeFilter.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCodeFilter.getActionMap().put("F8-Action", actionMedDelete);

        //Enter event on tblSale
        //tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        //tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);
    }

    private void initTblRecvList() {
        /*
     * JTableBinding jTableBinding =
     * SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE,
     * listRecv, tblRecvList); JTableBinding.ColumnBinding columnBinding =
     * jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${receiveDate}"));
     * columnBinding.setColumnName("Date");
     * columnBinding.setColumnClass(Date.class);
     * columnBinding.setEditable(false);
     *
     * columnBinding =
     * jTableBinding.addColumnBinding(ELProperty.create("${receivedId}"));
     * columnBinding.setColumnName("Vou No");
     * columnBinding.setColumnClass(String.class);
     * columnBinding.setEditable(false);
     *
     * columnBinding =
     * jTableBinding.addColumnBinding(ELProperty.create("${location.locationName}"));
     * columnBinding.setColumnName("Location");
     * columnBinding.setColumnClass(String.class);
     * columnBinding.setEditable(false);
     *
     * columnBinding =
     * jTableBinding.addColumnBinding(ELProperty.create("${createdBy.userShortName}"));
     * columnBinding.setColumnName("User");
     * columnBinding.setColumnClass(String.class);
     * columnBinding.setEditable(false);
     *
     * jTableBinding.bind();
         */

        tblRecvList.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblRecvList.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblRecvList.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblRecvList.getColumnModel().getColumn(3).setPreferredWidth(30);

        tblRecvList.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    private String getHSQL() {
        /*String strSql = "select distinct v from StockReceiveHis v join v.listDetail h where v.receiveDate between '"
                + DateUtil.toDateStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'";*/
        String strSql = "select distinct date(sh.receive_date) receive_date, sh.receive_id, sh.remark, \n"
                + "usr.user_name, l.location_name, sh.deleted\n"
                + "from stock_receive_his sh\n"
                + "join stock_receive_detail_his sdh on sh.receive_id = sdh.vou_no\n"
                + "join medicine med on sdh.rec_med_id = med.med_id\n"
                + "join appuser usr on sh.created_by = usr.user_id\n"
                + "join location l on sh.location_id = l.location_id \n"
                + "where sh.receive_date between '" + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "'";
        
        vouFilter.setFromDate(DateUtil.toDate(txtFrom.getText()));
        vouFilter.setToDate(DateUtil.toDate(txtTo.getText()));

        if (cboLocation.getSelectedItem() instanceof Location) {
            Location location = (Location) cboLocation.getSelectedItem();
            strSql = strSql + " and sh.location_id = " + location.getLocationId();
            vouFilter.setLocation(location);
        } else {
            vouFilter.setLocation(null);
        }

        if (txtRemark.getText() != null) {
            strSql = strSql + " and sh.remark = '" + txtRemark.getText() + "'";
        }
        vouFilter.setRemark(txtRemark.getText());

        //Filter history save
        try {
            dao.save(vouFilter);
        } catch (Exception ex) {
            log.error("getHSQL : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        if (!listMedicine.isEmpty()) {
            if (listMedicine.get(0).getMedId() != null) {
                strSql = strSql + " and sdh.rec_med_id in (";
                String medList = "";

                for (Medicine med : listMedicine) {
                    if (med.getMedId() != null) {
                        if (medList.length() > 0) {
                            medList = medList + ",'" + med.getMedId() + "'";
                        } else {
                            medList = "'" + med.getMedId() + "'";
                        }
                    }
                }
                strSql = strSql + medList + ")";
            }
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
                    vs.setInvNo(rs.getString("sh.receive_id"));
                    vs.setIsDeleted(rs.getBoolean("deleted"));
                    vs.setLocation(rs.getString("location_name"));
                    vs.setRefNo(rs.getString("remark"));
                    vs.setTranDate(rs.getDate("receive_date"));
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
    
    private void addSelectionListenerVoucher() {
        //Define table selection model to single row selection.
        tblRecvList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblRecvList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblRecvList.getSelectedRow();
            }
        });
    }

    private void search() {
        vouTableModel.setListStockReceiveHis(getSearchVoucher());
        lblTotalRec.setText("Total Records : " + vouTableModel.getRowCount());
    }

    private void select() {
        if (selectedRow >= 0) {
            observer.selected("StockReceiveList",
                    vouTableModel.getSelectVou(tblRecvList.convertRowIndexToModel(selectedRow)));

            dispose();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select stock receiving.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getPrvFilter() {
        VouFilter tmpFilter = (VouFilter) dao.find("VouFilter", "key.tranOption = 'StockRecvSearch'"
                + " and key.userId = '" + Global.loginUser.getUserId() + "'");

        if (tmpFilter == null) {
            vouFilter = new VouFilter();
            vouFilter.getKey().setTranOption("StockRecvSearch");
            vouFilter.getKey().setUserId(Global.loginUser.getUserId());

            txtFrom.setText(DateUtil.getTodayDateStr());
            txtTo.setText(DateUtil.getTodayDateStr());
        } else {
            vouFilter = tmpFilter;
            txtFrom.setText(DateUtil.toDateStr(vouFilter.getFromDate()));
            txtTo.setText(DateUtil.toDateStr(vouFilter.getToDate()));

            if (vouFilter.getLocation() != null) {
                cboLocation.setSelectedItem(vouFilter.getLocation());
            }

            txtRemark.setText(vouFilter.getRemark());
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        cboLocation = new javax.swing.JComboBox();
        txtRemark = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCodeFilter = new javax.swing.JTable(tblMedicineModel);
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRecvList = new javax.swing.JTable();
        butSelect = new javax.swing.JButton();
        butSearch = new javax.swing.JButton();
        lblTotalRec = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stock Receive Search");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtFrom.setEditable(false);
        txtFrom.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtTo.setEditable(false);
        txtTo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        cboLocation.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        txtRemark.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblCodeFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblCodeFilter.setRowHeight(23);
        tblCodeFilter.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblCodeFilter);

        tblRecvList.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblRecvList.setModel(vouTableModel);
        tblRecvList.setRowHeight(23);
        tblRecvList.setShowVerticalLines(false);
        tblRecvList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRecvListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblRecvList);

        butSelect.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSelect.setText("Select");
        butSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelectActionPerformed(evt);
            }
        });

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        lblTotalRec.setText("Total records : 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtTo))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboLocation, 0, 150, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtFrom)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalRec, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butSelect))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butSearch, butSelect});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSelect)
                    .addComponent(butSearch)
                    .addComponent(lblTotalRec))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtFrom.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtFromMouseClicked

  private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtTo.setText(strDate);
          }
      }
  }//GEN-LAST:event_txtToMouseClicked

  private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
      search();
  }//GEN-LAST:event_butSearchActionPerformed

  private void tblRecvListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRecvListMouseClicked
      if (evt.getClickCount() == 2 && selectedRow >= 0) {
          select();
      }
  }//GEN-LAST:event_tblRecvListMouseClicked

    private void butSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelectActionPerformed
        select();
    }//GEN-LAST:event_butSelectActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelect;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotalRec;
    private javax.swing.JTable tblCodeFilter;
    private javax.swing.JTable tblRecvList;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
