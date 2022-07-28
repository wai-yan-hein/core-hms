/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.StockReceiveDetailHis;
import com.cv.app.pharmacy.database.entity.StockReceiveHis;
import com.cv.app.pharmacy.database.helper.StockOutstanding;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.ReceiveTableModel;
import com.cv.app.pharmacy.ui.util.OutstandingStockList;
import com.cv.app.pharmacy.ui.util.StockRecvListDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.TraderCodeCellEditor;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockReceiving extends javax.swing.JPanel implements SelectionObserver,
        FormAction, KeyPropagate, MedInfo {

    static Logger log = Logger.getLogger(StockReceiving.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private ReceiveTableModel recTblModel = new ReceiveTableModel(dao);
    private StockReceiveHis srHis = new StockReceiveHis();
    private int mouseClick = 2;
    private boolean isBind = false;

    /**
     * Creates new form StockReceiving
     */
    public StockReceiving() {
        initComponents();
        try {
            txtReceiveDate.setText(DateUtil.getTodayDateStr());
            lblStatus.setText("NEW");
            txtRevId.setFormatterFactory(new VouFormatFactory());
            vouEngine = new GenVouNoImpl(dao, "StockReceiving", DateUtil.getPeriod(txtReceiveDate.getText()));
            genVouNo();
            initCombo();
            initTable();
            actionMapping();

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

            Currency appCurr = (Currency) cboCurrency.getSelectedItem();
            recTblModel.setCurrency(appCurr);
        } catch (Exception ex) {
            log.error("StockReceiving : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtRevId.setText(vouNo);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "Outstanding":
                StockOutstanding outs = (StockOutstanding) selectObj;
                recTblModel.add(outs);
                break;
            case "StockReceiveList":
                try {
                    VoucherSearch vs = (VoucherSearch) selectObj;
                    dao.open();
                    srHis = (StockReceiveHis) dao.find(StockReceiveHis.class, vs.getInvNo());
                    List<StockReceiveDetailHis> list = dao.findAllHSQL(
                            "select o from StockReceiveDetailHis o where o.vouNo = '"
                            + vs.getInvNo() + "' order by o.uniqueId");
                    //srHis.setListDetail(list);
                    System.out.println(list.size());

                    if (srHis.isDeleted()) {
                        lblStatus.setText("DELETED");
                    } else {
                        lblStatus.setText("EDIT");
                    }

                    txtRevId.setText(srHis.getReceivedId());
                    txtReceiveDate.setText(DateUtil.toDateStr(srHis.getReceiveDate()));
                    txtRemark.setText(srHis.getRemark());
                    cboLocation.setSelectedItem(srHis.getLocation());
                    recTblModel.setListDetail(list);
                    recTblModel.addEmptyRow();
                    dao.close();
                } catch (Exception ex) {
                    log.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
        }
    }

    @Override
    public void save() {
        if (isValidEntry() && recTblModel.isValidEntry()) {
            try {
                dao.open();
                dao.beginTran();
                String vouNo = srHis.getReceivedId();
                List<StockReceiveDetailHis> list = recTblModel.getListDetail();
                for (StockReceiveDetailHis srdh : list) {
                    srdh.setVouNo(vouNo);
                    srdh.setTranId(vouNo + "-" + srdh.getUniqueId().toString());
                    /*if(srdh.getTraderId() == null){
                        srdh.setTraderId("-");
                    }*/
                    dao.save1(srdh);
                }
                //srHis.setListDetail(list);
                dao.save1(srHis);
                dao.commit();
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                deleteDetail();
                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void newForm() {
        lblStatus.setText("NEW");
        txtRemark.setText("");
        txtReceiveDate.setText(DateUtil.getTodayDateStr());
        recTblModel.clear();
        vouEngine.setPeriod(DateUtil.getPeriod(txtReceiveDate.getText()));
        srHis = new StockReceiveHis();
        vouEngine.setPeriod(DateUtil.getPeriod(txtReceiveDate.getText()));
        genVouNo();
        recTblModel.setReceiveId(txtRevId.getText());
        recTblModel.addEmptyRow();
        Currency appCurr = (Currency) cboCurrency.getSelectedItem();
        recTblModel.setCurrency(appCurr);
        System.gc();
    }

    @Override
    public void history() {
        StockRecvListDialog dialog = new StockRecvListDialog(dao, this);
    }

    @Override
    public void delete() {
        System.out.println("delete");

        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                "Stock receive delete", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            srHis.setDeleted(true);
            save();
        }
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboLocation, this);
            new ComBoBoxAutoComplete(cboCurrency, this);
            Object tmpObj = Util1.getDefaultValue("Currency");
            if (tmpObj != null) {
                cboCurrency.setSelectedItem(tmpObj);
            }
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowStkReceive = true) order by o.locationName");
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

    private void initTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblReceive.setCellSelectionEnabled(true);
        }
        tblReceive.getTableHeader().setFont(Global.lableFont);
        tblReceive.getColumnModel().getColumn(0).setPreferredWidth(10); //Option
        tblReceive.getColumnModel().getColumn(1).setPreferredWidth(50); //Vou No
        tblReceive.getColumnModel().getColumn(2).setPreferredWidth(110);//Order Medicine
        tblReceive.getColumnModel().getColumn(3).setPreferredWidth(30);//Med Code
        tblReceive.getColumnModel().getColumn(4).setPreferredWidth(110); //Rec-Medicine
        tblReceive.getColumnModel().getColumn(5).setPreferredWidth(110); //Trader
        tblReceive.getColumnModel().getColumn(6).setPreferredWidth(30); //Outstanding
        tblReceive.getColumnModel().getColumn(7).setPreferredWidth(30); //Exp-Date
        tblReceive.getColumnModel().getColumn(8).setPreferredWidth(10); //Qty
        tblReceive.getColumnModel().getColumn(9).setPreferredWidth(2); //Unit
        tblReceive.getColumnModel().getColumn(10).setPreferredWidth(10); //Balance
        tblReceive.getColumnModel().getColumn(11).setPreferredWidth(5); //Currency
        tblReceive.getColumnModel().getColumn(12).setPreferredWidth(10); //Cost Price

        tblReceive.getColumnModel().getColumn(3).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        recTblModel.setParent(tblReceive);
        recTblModel.setReceiveId(txtRevId.getText());
        recTblModel.addEmptyRow();
        tblReceive.getColumnModel().getColumn(12).setCellEditor(new BestTableCellEditor(this));
        //tblReceive.getColumnModel().getColumn(6).setCellRenderer(new TableDateFieldRenderer());
        tblReceive.getColumnModel().getColumn(5).setCellEditor(
                new TraderCodeCellEditor(dao));
    }

    private boolean isValidEntry() {
        Date vouSaleDate = DateUtil.toDate(txtReceiveDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean status = true;

        if (cboLocation.getSelectedItem() == null) {
            status = false;

            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
        } else {
            srHis.setReceivedId(txtRevId.getText());
            srHis.setRemark(txtRemark.getText());
            srHis.setReceiveDate(DateUtil.toDate(txtReceiveDate.getText()));
            srHis.setLocation((Location) cboLocation.getSelectedItem());

            if (lblStatus.getText().equals("NEW")) {
                srHis.setDeleted(false);
                srHis.setCreatedBy(Global.loginUser);
            } else {
                srHis.setUpdatedBy(Global.loginUser);
                srHis.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            try {
                if (tblReceive.getCellEditor() != null) {
                    tblReceive.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            if (NumberUtil.NZeroL(srHis.getExrId()) == 0) {
                Long exrId = getExchangeId(txtReceiveDate.getText(), srHis.getCurrencyId());
                srHis.setExrId(exrId);
            }
        }

        return status;
    }
    private Action actionItemDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblReceive.getSelectedRow() >= 0) {
                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Receiving item delete", JOptionPane.YES_NO_OPTION);

                    if (tblReceive.getCellEditor() != null) {
                        tblReceive.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                    log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }

                if (yes_no == 0) {
                    recTblModel.delete(tblReceive.getSelectedRow());
                }
            }
        }
    };

    private void actionMapping() {
        //F3 event on tblSale
        //tblReceive.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        //tblReceive.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblReceive.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblReceive.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblSale
        //tblReceive.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        //tblReceive.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);
        //F8 event on tblExpense
        //tblReceive.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        //tblReceive.getActionMap().put("F8-Action", actionItemDeleteExp);
        formActionKeyMapping(txtRevId);
        formActionKeyMapping(txtReceiveDate);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(butOutstanding);
        formActionKeyMapping(tblReceive);
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

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Print
        jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
        jc.getActionMap().put("F7-Action", actionPrint);

        //History
        jc.getInputMap().put(KeyStroke.getKeyStroke("F9"), "F9-Action");
        jc.getActionMap().put("F9-Action", actionHistory);

        //Clear
        jc.getInputMap().put(KeyStroke.getKeyStroke("F10"), "F10-Action");
        jc.getActionMap().put("F10-Action", actionNewForm);

        //Delete
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
        jc.getActionMap().put("Ctrl-F8-Action", actionDelete);
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
    };// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actionPrint">
    private Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };// </editor-fold>

    private Action actionDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };
    private Action actionNewForm = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    private void deleteDetail() {
        String deleteSQL;

        //All detail section need to explicity delete
        //because of save function only delete to join table
        deleteSQL = recTblModel.getDeleteSql();
        if (deleteSQL != null) {
            dao.execSql(deleteSQL);
        }
        //delete section end
    }

    @Override
    public void getMedInfo(String medCode) {
        try {
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                selected("MedicineList", medicine);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                        "Invalid.", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private Long getExchangeId(String strDate, String curr) {
        long id = 0;
        if (Util1.getPropValue("system.multicurrency").equals("Y")) {
            try {
                Object value = dao.getMax("exr_id", "exchange_rate",
                        "(to_curr = '" + curr + "' or from_curr = '" + curr
                        + "') and date(created_date) = '"
                        + DateUtil.toDateStrMYSQL(strDate)
                        + "'"
                );
                if (value != null) {
                    id = NumberUtil.NZeroL(value);
                }
            } catch (Exception ex) {
                log.error("getExchangeId : " + ex.getMessage());
            }
        }
        return id;
    }

    private void borrow() {
        System.out.println("Borrow");
        Currency appCurr = (Currency) cboCurrency.getSelectedItem();
        StockReceiveDetailHis srdh = new StockReceiveDetailHis();

        srdh.setRecOption("Borrow");
        srdh.setRefVou(txtRevId.getText().trim());
        srdh.setCurrency(appCurr);
        recTblModel.addBorrow(srdh);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        txtRevId = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblReceive = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        butOutstanding = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtReceiveDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox<>();
        butBorrow = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(500, 306));

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRevId.setEditable(false);
        txtRevId.setFont(Global.textFont);
        txtRevId.setMaximumSize(new java.awt.Dimension(32767, 32767));

        tblReceive.setFont(Global.textFont);
        tblReceive.setModel(recTblModel);
        tblReceive.setRowHeight(23);
        tblReceive.setShowVerticalLines(false);
        tblReceive.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblReceiveFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(tblReceive);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("ID");

        txtRemark.setFont(Global.textFont);

        butOutstanding.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butOutstanding.setText("Outstanding");
        butOutstanding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOutstandingActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location ");

        txtReceiveDate.setEditable(false);
        txtReceiveDate.setFont(Global.textFont);
        txtReceiveDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtReceiveDateMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date ");

        cboLocation.setFont(Global.textFont);

        lblStatus.setText("NEW");

        jLabel1.setText("Currency");

        cboCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCurrencyActionPerformed(evt);
            }
        });

        butBorrow.setText("Borrow");
        butBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBorrowActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2)
                    .add(layout.createSequentialGroup()
                        .add(jLabel4)
                        .add(18, 18, 18)
                        .add(txtRevId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(txtReceiveDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblStatus)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(butBorrow)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(butOutstanding))
                    .add(layout.createSequentialGroup()
                        .add(jLabel5)
                        .add(18, 18, 18)
                        .add(txtRemark)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(butOutstanding)
                    .add(jLabel4)
                    .add(txtRevId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(txtReceiveDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblStatus)
                    .add(jLabel1)
                    .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(butBorrow))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

  private void txtReceiveDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtReceiveDateMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtReceiveDate.setText(strDate);

              if (lblStatus.getText().equals("NEW")) {
                  vouEngine.setPeriod(DateUtil.getPeriod(txtReceiveDate.getText()));
                  genVouNo();
              }
          }
      }
  }//GEN-LAST:event_txtReceiveDateMouseClicked

  private void butOutstandingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOutstandingActionPerformed
      OutstandingStockList dialog = new OutstandingStockList(dao, this, "StockReceiving");
  }//GEN-LAST:event_butOutstandingActionPerformed

    private void tblReceiveFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblReceiveFocusLost
        /*try{
            if(tblReceive.getCellEditor() != null){
                tblReceive.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblReceiveFocusLost

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        if (!isBind) {
            Currency appCurr = (Currency) cboCurrency.getSelectedItem();
            recTblModel.setCurrency(appCurr);
        }
    }//GEN-LAST:event_cboCurrencyActionPerformed

    private void butBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBorrowActionPerformed
        borrow();
    }//GEN-LAST:event_butBorrowActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butBorrow;
    private javax.swing.JButton butOutstanding;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblReceive;
    private javax.swing.JFormattedTextField txtReceiveDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtRevId;
    // End of variables declaration//GEN-END:variables
}
