/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.ui.common.TraderListTableModel;
import com.cv.app.util.Util1;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class TraderSearchDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(CustomerList.class.getName());
    private Object parent;
    private final SelectionObserver observer;
    private final AbstractDataAccess dao = Global.dao;
    private int selectedRow = -1;
    private final TableRowSorter<TableModel> sorter;
    private final TraderListTableModel tableModel = new TraderListTableModel();
    private final String title;
    private StartWithRowFilter filter;
    private int locationId;

    /**
     * Creates new form TraderSearchDialog
     *
     * @param observer
     * @param title
     * @param locationId
     */
    public TraderSearchDialog(SelectionObserver observer, String title, int locationId) {
        super(Util1.getParent(), true);
        initComponents();
        this.observer = observer;
        this.title = title;
        this.locationId = locationId;
        if (title.contains("Supplier")) {
            chkOption.setText("Customer");
        } else {
            chkOption.setText("Supplier");
        }
        filter = new StartWithRowFilter(txtFilter);
        initTable();
        sorter = new TableRowSorter(tblCustomer.getModel());
        tblCustomer.setRowSorter(sorter);
        txtFilter.registerKeyboardAction(upArrowAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                JComponent.WHEN_FOCUSED);
        txtFilter.registerKeyboardAction(downArrowAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                JComponent.WHEN_FOCUSED);
        txtFilter.registerKeyboardAction(escapeAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);
        txtFilter.registerKeyboardAction(enterAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_FOCUSED);

        if (tableModel.getListTrader().size() > 0) {
            tblCustomer.setRowSelectionInterval(0, 0);
        }
        actionMapping();
    }

    private void initTable() {
        getData();

        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblCustomer.getColumnModel().getColumn(3).setPreferredWidth(80);

        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedRow = tblCustomer.getSelectedRow();
            }
        });
    }

    private void getData() {
        List<Trader> listTrader;

        try {
            
            if ((title.contains("Customer") && chkOption.isSelected()) ||
                    (title.contains("Supplier") && !chkOption.isSelected())) {
                if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                    String strSql;
                    if (locationId != -1) {
                        strSql = "select o from Supplier o where "
                                + "o.active = true and o.traderId in (select a.key.traderId "
                                + "from LocationTraderMapping a where a.key.locationId = "
                                + locationId + ") order by o.traderName";
                    } else {
                        strSql = "select o from Supplier o where "
                                + "o.active = true and o.traderId in (select a.key.traderId "
                                + "from LocationTraderMapping a where a.key.locationId in ("
                                + "select a.key.locationId from UserLocationMapping a "
                                + "where a.key.userId = '" + Global.loginUser.getUserId()
                                + "' and a.isAllowSessCheck = true)) order by o.traderName";
                    }
                    listTrader = dao.findAllHSQL(strSql);
                } else {
                    listTrader = dao.findAllHSQL(
                            "select o from Supplier o where o.active = true order by o.traderName");
                }
                //listTrader = dao.findAll("Supplier", "Active = true");
            } else if ((title.contains("Customer") && !chkOption.isSelected()) ||
                    title.contains("Supplier") && chkOption.isSelected()) {
                if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                    String strSql;
                    if (locationId != -1) {
                        strSql = "select o from Customer o where "
                                + "o.active = true and o.traderId in (select a.key.traderId "
                                + "from LocationTraderMapping a where a.key.locationId = "
                                + locationId + ") order by o.traderName";
                    } else {
                        strSql = "select o from Customer o where "
                                + "o.active = true and o.traderId in (select a.key.traderId "
                                + "from LocationTraderMapping a where a.key.locationId in ("
                                + "select a.key.locationId from UserLocationMapping a "
                                + "where a.key.userId = '" + Global.loginUser.getUserId()
                                + "' and a.isAllowSessCheck = true)) order by o.traderName";
                    }
                    listTrader = dao.findAllHSQL(strSql);
                } else {
                    listTrader = dao.findAllHSQL(
                            "select o from Customer o where active = true order by o.traderName");
                }
            } else {
                if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                    String strSql;
                    if (locationId != -1) {
                        strSql = "select o from Trader o where "
                                + "o.active = true and o.traderId in (select a.key.traderId "
                                + "from LocationTraderMapping a where a.key.locationId = "
                                + locationId + ") order by o.traderName";
                    } else {
                        strSql = "select o from Trader o where "
                                + "o.active = true and o.traderId in (select a.key.traderId "
                                + "from LocationTraderMapping a where a.key.locationId in ("
                                + "select a.key.locationId from UserLocationMapping a "
                                + "where a.key.userId = '" + Global.loginUser.getUserId()
                                + "' and a.isAllowSessCheck = true)) order by o.traderName";
                    }
                    listTrader = dao.findAllHSQL(strSql);
                } else {
                    listTrader = dao.findAll("Trader", "Active = true");
                }
            }

            tableModel.setListTrader(listTrader);
        } catch (Exception ex) {
            log.error("getData : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void actionMapping() {
        //Enter event on tblSale
        tblCustomer.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblCustomer.getActionMap().put("ENTER-Action", actionTblCustomerEnterKey);
    }

    private final Action actionTblCustomerEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(tblCustomer.getCellEditor() != null){
                    tblCustomer.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            selectTrader();
        }
    };

    private void selectTrader() {
        if (selectedRow >= 0) {
            observer.selected("CustomerList",
                    tableModel.getTrader(tblCustomer.convertRowIndexToModel(selectedRow)));
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select trader.",
                    "No Selection.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final Action upArrowAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int si = tblCustomer.getSelectedRow();

            if (si > 0) {
                tblCustomer.setRowSelectionInterval(si - 1, si - 1);
            }

            Rectangle rect = tblCustomer.getCellRect(tblCustomer.getSelectedRow(), 0, true);
            tblCustomer.scrollRectToVisible(rect);
        }
    };

    private final Action downArrowAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int si = tblCustomer.getSelectedRow();
            if (si < tableModel.getListTrader().size() - 1) {
                tblCustomer.setRowSelectionInterval(si + 1, si + 1);
            }

            Rectangle rect = tblCustomer.getCellRect(tblCustomer.getSelectedRow(), 0, true);
            tblCustomer.scrollRectToVisible(rect);
        }
    };

    private final Action escapeAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

            closeDialog();
        }
    };

    private final Action enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectTrader();
        }
    };

    private void filterItem(String strFilter) {
        tableModel.setListTrader(dao.findAll("Trader", strFilter));
        //statusFilter = true;
    }

    private void closeDialog() {
        this.dispose();
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
        chkOption = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 600));

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        chkOption.setText("Customer");
        chkOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOptionActionPerformed(evt);
            }
        });

        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(tableModel);
        tblCustomer.setRowHeight(23);
        tblCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCustomer);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkOption)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOption))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            String option = Util1.getPropValue("system.text.filter.method");

            if (option.equals("SW")) {
                sorter.setRowFilter(filter);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void chkOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOptionActionPerformed
        getData();
    }//GEN-LAST:event_chkOptionActionPerformed

    private void tblCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerMouseClicked
        if (evt.getClickCount() == 2 && selectedRow >= 0) {
            selectTrader();
        }
    }//GEN-LAST:event_tblCustomerMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkOption;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
