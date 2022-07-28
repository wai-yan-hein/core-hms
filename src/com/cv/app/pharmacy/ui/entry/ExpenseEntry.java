/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.opd.ui.common.ExpenseEntryTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.view.VGenExpense;
import com.cv.app.pharmacy.database.entity.GenExpenseLockHis;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.ui.common.TableDateTimeFieldRenderer;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class ExpenseEntry extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(ExpenseEntry.class.getName());

    private final AbstractDataAccess dao = Global.dao;
    private ExpenseEntryTableModel tableModel = new ExpenseEntryTableModel(dao);
    private boolean bindStatus = false;
    private int mouseClick = 2;
    //private final TableRowSorter<TableModel> sorter;

    /**
     * Creates new form ExpenseEntry
     */
    public ExpenseEntry() {
        initComponents();
        txtExpDate.setText(DateUtil.getTodayDateStr());
        txtToDate.setText(DateUtil.getTodayDateStr());
        initTable();
        initCombo();
        actionMapping();
        //sorter = new TableRowSorter(tblExpense.getModel());
        //tblExpense.setRowSorter(sorter);

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
    }

    private void initTable() {
        try {
            if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
                tblExpense.setCellSelectionEnabled(true);
            }
            getExpense();

            tblExpense.getTableHeader().setFont(Global.lableFont);
            //Adjust column width
            tblExpense.getColumnModel().getColumn(0).setPreferredWidth(30);//Date
            tblExpense.getColumnModel().getColumn(1).setPreferredWidth(350);//Description
            tblExpense.getColumnModel().getColumn(2).setPreferredWidth(250);//Remark
            tblExpense.getColumnModel().getColumn(3).setPreferredWidth(100);//Expense Type
            tblExpense.getColumnModel().getColumn(4).setPreferredWidth(10);//Location
            tblExpense.getColumnModel().getColumn(5).setPreferredWidth(5);//Lock
            tblExpense.getColumnModel().getColumn(6).setPreferredWidth(50);//Lock Time
            tblExpense.getColumnModel().getColumn(7).setPreferredWidth(50);//DR-Amount
            tblExpense.getColumnModel().getColumn(8).setPreferredWidth(50);//CR-Amount
            tblExpense.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
            tblExpense.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
            tblExpense.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
            tblExpense.getColumnModel().getColumn(6).setCellRenderer(new TableDateTimeFieldRenderer());

            JComboBox cboExpenseType = new JComboBox();
            cboExpenseType.setFont(new java.awt.Font("Zawgyi-One", 0, 12));
            BindingUtil.BindCombo(cboExpenseType, dao.findAll("ExpenseType"));
            tblExpense.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cboExpenseType));

            JComboBox cboLocation1 = new JComboBox();
            cboLocation1.setFont(new java.awt.Font("Zawgyi-One", 0, 12));
            BindingUtil.BindCombo(cboLocation1, dao.findAll("Location"));
            tblExpense.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboLocation1));

            tblExpense.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    txtTotal.setValue(tableModel.getTotalAmount());
                    txtDRAmount.setValue(tableModel.getTotalAmountDR());
                }
            });

            tblExpense.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblExpense.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    tableModel.setSelectRow(tblExpense.convertRowIndexToModel(tblExpense.getSelectedRow()));
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getExpense() {
        String strSql = "date(expnDate) between '" + DateUtil.toDateStrMYSQL(txtExpDate.getText())
                + "' and '" + DateUtil.toDateStrMYSQL(txtToDate.getText()) + "'";

        String expId = "-";
        if (cboExpType.getSelectedItem() instanceof ExpenseType) {
            ExpenseType et = (ExpenseType) cboExpType.getSelectedItem();
            expId = et.getExpenseId().toString();
            strSql = strSql + " and expType.expenseId = " + et.getExpenseId();
        }

        String locId = "-";
        if (cboLocation.getSelectedItem() instanceof Location) {
            Location loc = (Location) cboLocation.getSelectedItem();
            locId = loc.getLocationId().toString();
            strSql = strSql + " and location.locationId = " + loc.getLocationId();
        }

        String expStatus = Util1.getPropValue("system.expenseentry.status");
        if (!expStatus.equals("Y")) {
            strSql = strSql + " and tranType = 'GENERAL_EXPENSE'";
        }

        String strType = cboType.getSelectedItem().toString();
        if (strType.equals("Normal")) {
            strSql = strSql + " and ifnull(deleted, false) = false";
        } else {
            strSql = strSql + " and ifnull(deleted, false) = true";
        }

        String drId = "-";
        if (cboDoctor.getSelectedItem() instanceof Doctor) {
            Doctor dr = (Doctor) cboDoctor.getSelectedItem();
            drId = dr.getDoctorId();
            strSql = strSql + " and drId = '" + dr.getDoctorId() + "'";
        }

        String expOption = cboOption.getSelectedItem().toString();
        if (!expOption.equals("All")) {
            strSql = strSql + " and expenseOption = '" + expOption + "'";
        }

        strSql = strSql + " and upp = " + chkUPP.isSelected();

        strSql = strSql + " order by date(expnDate)";

        try {
            tblExpense.getSelectionModel().clearSelection();
            List<VGenExpense> listGenExpense = dao.findAll("VGenExpense", strSql);
            if (listGenExpense != null) {
                txtTtlRecords.setValue(listGenExpense.size());
            } else {
                txtTtlRecords.setValue(0);
                listGenExpense = new ArrayList();
            }
            tableModel.setListGenExpense(listGenExpense);
            txtTotal.setValue(tableModel.getTotalAmount());
            txtDRAmount.setValue(tableModel.getTotalAmountDR());
            tableModel.setDate(txtExpDate.getText());
        } catch (Exception ex) {
            log.error("getExpense : " + ex.toString());
        } finally {
            dao.close();
        }

        if (txtExpDate.getText().trim().equals(txtToDate.getText().trim())
                && expId.equals("-") && locId.equals("-") && strType.equals("Normal")
                && drId.equals("-") && expOption.equals("All")) {
            butLock.setEnabled(true);
        } else {
            butLock.setEnabled(false);
        }
    }

    private void actionMapping() {
        //F8 event on tblService
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblExpense.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblService
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblExpense.getActionMap().put("ENTER-Action", actionTblExpEnterKey);
    }

    private final Action actionTblExpEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tblExpense.getCellEditor().stopCellEditing();
            } catch (Exception ex) {
            }

            int row = tblExpense.getSelectedRow();
            int col = tblExpense.getSelectedColumn();

            VGenExpense record = tableModel.getGenExpense(row);
            if (col == 0 && record.getDesp() != null) {
                tblExpense.setColumnSelectionInterval(1, 1); //to Description
            } else if (col == 1 && record.getDesp() != null) {
                tblExpense.setColumnSelectionInterval(2, 2); //to Remark
            } else if (col == 2 && record.getDesp() != null) {
                tblExpense.setColumnSelectionInterval(3, 3); //to Amount
            } else if (col == 3 && record.getDesp() != null) {
                if ((row + 1) <= tableModel.getRowCount()) {
                    tblExpense.setRowSelectionInterval(row + 1, row + 1);
                }
                tblExpense.setColumnSelectionInterval(0, 0); //to Date
            }
        }
    };

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblExpense.getSelectedRow();

            if (row >= 0) {
                VGenExpense ge = tableModel.getGenExpense(row);

                if (ge.getDesp() != null) {
                    try {
                        tblExpense.getCellEditor().stopCellEditing();
                    } catch (Exception ex) {
                    }

                    int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "Delete", JOptionPane.YES_NO_OPTION);

                    if (yes_no == 0) {
                        tableModel.deleteGenExpense(row);
                    }
                }
            }
        }
    };

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboLocation,
                    dao.findAllHSQL("select o from Location o order by o.locationName"));
            BindingUtil.BindComboFilter(cboExpType,
                    dao.findAllHSQL("select o from ExpenseType o order by o.expenseName"));
            BindingUtil.BindComboFilter(cboDoctor,
                    dao.findAllHSQL("select o from Doctor o where o.active = true order by o.doctorName"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
        bindStatus = true;
    }

    private void printPayment() {
        VGenExpense vge = tableModel.getGenExpense(tblExpense.convertRowIndexToModel(tblExpense.getSelectedRow()));
        if (vge.getVouNo() != null) {
            ExpenseType et = vge.getExpType();
            String reportName = "-"; //Util1.getPropValue("report.xray.form");

            switch (et.getExpenseOption()) {
                case "OPD":
                    switch (et.getSysCode()) {
                        case "OPDCF": //CF Fee Payment
                            reportName = "OPDDoctorCFPayment";
                            break;
                        case "OPDREFER": //Refer Fee Payment
                            reportName = "OPDDoctorReferPayment";
                            break;
                        case "OPDREAD": //Reader Fee Payment
                            reportName = "OPDDoctorReaderPayment";
                            break;
                        case "OPDMO": //MO Fee Payment
                            reportName = "OPDDoctorMOPayment";
                            break;
                        case "OPDTECH": //Tech Fee Payment
                            reportName = "OPDDoctorTechPayment";
                            break;
                        case "OPDSTAFF": //Staff Fee Payemnt
                            reportName = "OPDDoctorStaffPayment";
                            break;
                    }
                    break;
                case "OT":
                    switch (et.getSysCode()) {
                        case "OTDR": //OT Doctor Payment
                            reportName = "OTDoctorFeePayment";
                            break;
                        case "OTSTAFF": //DC Staff Payment
                            reportName = "OTStaffFeePayment";
                            break;
                        case "OTNURSE": //DC Nurse Payment
                            reportName = "OTNurseFeePayment";
                            break;
                        case "OTMO": //OT MO Payment
                            reportName = "OTMOFeePayment";
                            break;
                    }
                    break;
                case "DC":
                    switch (et.getSysCode()) {
                        case "DCDR": //OT Doctor Payment
                            reportName = "DCDoctorFeePayment";
                            break;
                        case "DCNURSE":
                            reportName = "DCNurseFeePayment";
                            break;
                        case "DCTECH":
                            reportName = "DCTechFeePayment";
                            break;
                        case "DCMO":
                            reportName = "DCMOFeePayment";
                            break;
                    }
                    break;
            }

            if (!reportName.equals("-")) {
                String[] desp = vge.getDesp().split("-");
                String doctorId = desp[0].trim();
                String doctorName = desp[1].trim();
                String period = desp[2].trim().replace("(", "").replace(")", "");
                String[] dates = period.split("to");
                String fromDate = DateUtil.toDateStrMYSQL(dates[0].trim());
                String toDate = DateUtil.toDateStrMYSQL(dates[1].trim());
                String reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + "Clinic/"
                        + reportName;
                Map<String, Object> params = new HashMap();
                String compName = Util1.getPropValue("report.company.name");
                String phoneNo = Util1.getPropValue("report.phone");
                String address = Util1.getPropValue("report.address");
                params.put("p_user_id", Global.machineId);
                params.put("compName", compName);
                params.put("phoneNo", phoneNo);
                params.put("comAddress", address);
                params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path"));
                params.put("p_doctor_id", doctorId);
                params.put("p_pay_id", vge.getVouNo());
                params.put("p_doctor", doctorName);
                params.put("p_period", period);
                params.put("p_from_date", fromDate);
                params.put("p_to_date", toDate);
                params.put("p_tran_date", DateUtil.toDateStr(vge.getExpnDate()));
                params.put("p_payment_desp", et.getExpenseName());
                try {
                    dao.close();
                    ReportUtil.viewReport(reportPath, params, dao.getConnection());
                    dao.commit();
                } catch (Exception ex) {
                    log.error("printPayment : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "You cannot print this expense.",
                    "Not Printable", JOptionPane.ERROR_MESSAGE);
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
        txtExpDate = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        cboExpType = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        txtDRAmount = new javax.swing.JFormattedTextField();
        txtToDate = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        cboDoctor = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cboOption = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtTtlRecords = new javax.swing.JFormattedTextField();
        butLock = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>();
        butFefresh = new javax.swing.JButton();
        chkUPP = new javax.swing.JCheckBox();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date ");

        txtExpDate.setEditable(false);
        txtExpDate.setFont(Global.textFont);
        txtExpDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtExpDateMouseClicked(evt);
            }
        });

        tblExpense.setFont(Global.textFont);
        tblExpense.setModel(tableModel);
        tblExpense.setRowHeight(23);
        tblExpense.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblExpenseFocusLost(evt);
            }
        });
        tblExpense.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblExpenseMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblExpense);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total :");

        txtTotal.setEditable(false);
        txtTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setFont(Global.textFont);

        jLabel3.setText("Expense Type ");

        cboExpType.setFont(Global.textFont);
        cboExpType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboExpTypeActionPerformed(evt);
            }
        });

        jLabel4.setText("Location");

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        txtDRAmount.setEditable(false);
        txtDRAmount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtDRAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDRAmount.setFont(Global.textFont);

        txtToDate.setEditable(false);
        txtToDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToDateMouseClicked(evt);
            }
        });
        txtToDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtToDateActionPerformed(evt);
            }
        });

        jLabel5.setText("Doctor ");

        cboDoctor.setFont(Global.textFont);
        cboDoctor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDoctorActionPerformed(evt);
            }
        });

        jLabel6.setText("Option ");

        cboOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "OPD", "OT", "DC" }));
        cboOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOptionActionPerformed(evt);
            }
        });

        jLabel7.setText("Total Record : ");

        txtTtlRecords.setEditable(false);
        txtTtlRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        butLock.setText("Lock");
        butLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLockActionPerformed(evt);
            }
        });

        jLabel8.setText("Type");

        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Deleted" }));
        cboType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTypeActionPerformed(evt);
            }
        });

        butFefresh.setText("Refresh");
        butFefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFefreshActionPerformed(evt);
            }
        });

        chkUPP.setText("UPP");
        chkUPP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUPPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboExpType, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboOption, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butLock, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUPP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFefresh))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDRAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtExpDate, txtToDate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboExpType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cboDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cboOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butLock)
                    .addComponent(jLabel8)
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butFefresh)
                    .addComponent(chkUPP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtTtlRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDRAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtExpDate, txtToDate});

    }// </editor-fold>//GEN-END:initComponents

  private void txtExpDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtExpDateMouseClicked
      if (evt.getClickCount() == mouseClick) {
          String strDate = DateUtil.getDateDialogStr();

          if (strDate != null) {
              txtExpDate.setText(strDate);
              tableModel.setDate(strDate);
              getExpense();
          }
      }
  }//GEN-LAST:event_txtExpDateMouseClicked

    private void cboExpTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboExpTypeActionPerformed
        if (bindStatus) {
            getExpense();
        }
    }//GEN-LAST:event_cboExpTypeActionPerformed

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (bindStatus) {
            getExpense();
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void tblExpenseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblExpenseFocusLost
        /*try{
            if(tblExpense.getCellEditor() != null){
                tblExpense.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblExpenseFocusLost

    private void tblExpenseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblExpenseMouseClicked
        if (evt.getClickCount() == 2) {
            printPayment();
        }
    }//GEN-LAST:event_tblExpenseMouseClicked

    private void txtToDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtToDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtToDateActionPerformed

    private void txtToDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtToDate.setText(strDate);
                getExpense();
            }
        }
    }//GEN-LAST:event_txtToDateMouseClicked

    private void cboDoctorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDoctorActionPerformed
        if (bindStatus) {
            getExpense();
        }
    }//GEN-LAST:event_cboDoctorActionPerformed

    private void cboOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOptionActionPerformed
        if (bindStatus) {
            getExpense();
        }
    }//GEN-LAST:event_cboOptionActionPerformed

    private void butLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLockActionPerformed
        if (txtExpDate.getText().equals(txtToDate.getText())) {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                    "Are you sure to lock?",
                    "Data Lock", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                try {
                    String strSql = "update gen_expense set rec_lock = true, \n"
                            + "lock_dt = '" + DateUtil.getTodayDateTimeStrMYSQL() + "' \n"
                            + "where date(exp_date) between '" + DateUtil.toDateStrMYSQL(txtExpDate.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtToDate.getText()) + "' and rec_lock is null or rec_lock = false";
                    dao.execSql(strSql);
                    GenExpenseLockHis gelh = new GenExpenseLockHis();
                    gelh.setDataDate(DateUtil.toDate(txtExpDate.getText().trim()));
                    gelh.setInAmt(NumberUtil.NZero(txtDRAmount.getValue()));
                    gelh.setOutAmt(NumberUtil.NZero(txtTotal.getValue()));
                    gelh.setLockMachine(Integer.parseInt(Global.machineId));
                    gelh.setLockTime(new Date());
                    gelh.setLockUser(Global.loginUser.getUserId());
                    gelh.setTtlRecs(NumberUtil.NZeroInt(txtTtlRecords.getText()));
                    dao.save(gelh);
                } catch (Exception ex) {
                    log.error("butLockActionPerformed : " + ex.toString());
                } finally {
                    dao.close();
                }
                getExpense();
            }
        } else {
            JOptionPane.showMessageDialog(this, "The two date must be the same. Please check.",
                    "Date Different", JOptionPane.ERROR_MESSAGE);
            log.error("Expense Lock Error : " + txtExpDate.getText() + " - " + txtToDate.getText());
        }
    }//GEN-LAST:event_butLockActionPerformed

    private void cboTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTypeActionPerformed
        getExpense();
    }//GEN-LAST:event_cboTypeActionPerformed

    private void butFefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFefreshActionPerformed
        getExpense();
    }//GEN-LAST:event_butFefreshActionPerformed

    private void chkUPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUPPActionPerformed
        getExpense();
    }//GEN-LAST:event_chkUPPActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butFefresh;
    private javax.swing.JButton butLock;
    private javax.swing.JComboBox<String> cboDoctor;
    private javax.swing.JComboBox cboExpType;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox<String> cboOption;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JCheckBox chkUPP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblExpense;
    private javax.swing.JFormattedTextField txtDRAmount;
    private javax.swing.JFormattedTextField txtExpDate;
    private javax.swing.JFormattedTextField txtToDate;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTtlRecords;
    // End of variables declaration//GEN-END:variables
}
