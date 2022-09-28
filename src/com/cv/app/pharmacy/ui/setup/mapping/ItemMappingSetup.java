/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * ItemSetup.java
 *
 * Created on Apr 29, 2012, 12:13:58 PM
 */
package com.cv.app.pharmacy.ui.setup.mapping;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PackingTemplate;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.ItemTableModel;
import com.cv.app.pharmacy.ui.common.RelationPriceTableModel;
import com.cv.app.pharmacy.ui.setup.*;
import com.cv.app.pharmacy.ui.util.ItemSetupFilterDialog;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * Medicine setup panel.
 */
public class ItemMappingSetup extends javax.swing.JPanel implements SelectionObserver, FormAction,
        KeyPropagate {

    static Logger log = Logger.getLogger(ItemMappingSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private BestAppFocusTraversalPolicy focusPolicy;
    private RelationPriceTableModel rpTableMode = new RelationPriceTableModel();
    private TableRowSorter<TableModel> sorter;
    private ItemTableModel itemTableModel = new ItemTableModel();
    private int selectRow = -1;
    private String filter = "";

    /**
     * Creates new form ItemSetup
     */
    public ItemMappingSetup() {
        initComponents();

        try {
            dao.open();
            initTableItem();
            sorter = new TableRowSorter(tblItem.getModel());
            tblItem.setRowSorter(sorter);
            assignDefaultValue();
            actionMapping();
            dao.close();
        } catch (Exception ex) {
            log.error("ItemMappingSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        tblItem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblItem.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblItem.getSelectedRow() >= 0) {
                    selectRow = tblItem.convertRowIndexToModel(tblItem.getSelectedRow());
                }
                System.out.println("Table Selection : " + selectRow);
                if (selectRow >= 0) {
                    try {
                        //tblRelationPrice.getCellEditor().stopCellEditing();
                    } catch (Exception ex) {

                    }
                    selected("Medicine", itemTableModel.getMedicine(selectRow));
                }
            }
        });

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        autoAssignTemplate();
    }

    public void setFocus() {

    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        //focusOrder.add(txtCusId);
        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
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
    }//</editor-fold>

    @Override
    public void save() {

    }

    @Override
    public void newForm() {

    }

    @Override
    public void history() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
    }

    private void filterItem(String strFilter) {
        try {
            itemTableModel.setListMedicine(dao.findAll("Medicine", strFilter));
        } catch (Exception ex) {
            log.error("filterItem : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            if (NumberUtil.isNumber(txtSearch.getText())) {
                if (entry.getStringValue(0).toUpperCase().startsWith(
                        txtSearch.getText().toUpperCase())) {
                    return true;
                }
            } else if (entry.getStringValue(1).toUpperCase().startsWith(
                    txtSearch.getText().toUpperCase())) {
                return true;
            }/*
             * else if(entry.getStringValue(2).toUpperCase().startsWith(
             * txtSearch.getText().toUpperCase())){ return true;
             }
             */

 /*
             * for (int i = 0; i < entry.getValueCount(); i++) { String tmp =
             * entry.getStringValue(i); if
             * (entry.getStringValue(i).toUpperCase().startsWith(
             * textComp.getText().toUpperCase())) { return true; }
             }
             */
            return false;
        }
    };

    private void autoAssignTemplate() {
        if (Util1.getPropValue("system.app.ItemSetup.AutoTemplate").equals("Y")) {
            int id = NumberUtil.NZeroInt(Util1.getPropValue("system.app.ItemSetup.AutoTemplateId"));
            try {
                PackingTemplate pt = (PackingTemplate) dao.find(PackingTemplate.class, id);

                if (pt != null) {
                    selected("PackingTemplate", pt);
                }
            } catch (Exception ex) {
                log.error("autoAssignTemplate : " + ex.getMessage());
            } finally {
                dao.close();
            }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        cmdFilter = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        tblItem.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblItem.setModel(itemTableModel);
        tblItem.setRowHeight(23);
        jScrollPane1.setViewportView(tblItem);

        txtSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        cmdFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cmdFilter.setText("...");
        cmdFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFilterActionPerformed(evt);
            }
        });

        jLabel1.setText("jLabel1");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdFilter)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showDialog(String panelName) {
        SetupDialog dialog = new SetupDialog(Util1.getParent(), true, panelName);

        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - dialog.getWidth()) / 2;
        int y = (screen.height - dialog.getHeight()) / 2;

        dialog.setLocation(x, y);
        dialog.show();
        System.out.println("After dialog close");
    }

  private void txtSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyTyped
  }//GEN-LAST:event_txtSearchKeyTyped

  private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
      //if (statusFilter) {
      /*if (filter.isEmpty()) {
       itemTableModel.setListMedicine(dao.findAll("Medicine"));
       } else {
       itemTableModel.setListMedicine(dao.findAll("Medicine", filter));
       }
       statusFilter = false;*/
      //}

      if (txtSearch.getText().length() == 0) {
          sorter.setRowFilter(null);
      } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
          sorter.setRowFilter(startsWithFilter);
      } else {
          sorter.setRowFilter(RowFilter.regexFilter(txtSearch.getText()));
      }
  }//GEN-LAST:event_txtSearchKeyReleased

  private void cmdFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFilterActionPerformed
      ItemSetupFilterDialog filterDialog = new ItemSetupFilterDialog(dao);

      if (filterDialog.getStatus()) {
          filter = filterDialog.getFilter();
          filterItem(filter);
      }
  }//GEN-LAST:event_cmdFilterActionPerformed

    @Override
    public void selected(Object source, Object selectObj) {

    }

    private void assignDefaultValue() {
        rpTableMode.setListDetail(new ArrayList<RelationGroup>());
    }

    private void initTableItem() {
        try {
            itemTableModel.setListMedicine(dao.findAll("VMedicine1"));

            //Adjust table column width
            TableColumn column = tblItem.getColumnModel().getColumn(0);
            column.setPreferredWidth(30);

            column = tblItem.getColumnModel().getColumn(1);
            column.setPreferredWidth(200);

            column = tblItem.getColumnModel().getColumn(2);
            column.setPreferredWidth(5);

            column = tblItem.getColumnModel().getColumn(3);
            column.setPreferredWidth(30);
        } catch (Exception ex) {
            log.error("initTableItem : " + ex.getMessage());
        } finally {
            dao.close();
        }
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

    private void actionMapping() {

        formActionKeyMapping(txtSearch);
        formActionKeyMapping(tblItem);
        formActionKeyMapping(cmdFilter);
    }

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

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
    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };
    private Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
