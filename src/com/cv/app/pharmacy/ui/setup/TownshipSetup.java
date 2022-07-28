/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.ui.common.TownshipTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author WSwe
 */
public class TownshipSetup extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(TownshipSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private Township currTownship = new Township();
    private TownshipTableModel tableModel = new TownshipTableModel();
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form TownshipSetup
     */
    public TownshipSetup() {
        super(Util1.getParent(), true);
        initComponents();
        initCombo();

        try {
            lblStatus.setText("NEW");
            initTable();
            sorter = new TableRowSorter(tblTownship.getModel());
            tblTownship.setRowSorter(sorter);
        } catch (Exception ex) {
            log.error("TownshipSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);

        //String strHSQL = "Select o from Township o";
        //List listTownship = dao.findAllHSQL(strHSQL);
        // BindCombo(cboParent, listTownship);
    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboParent, dao.findAll("Township"));
            new ComBoBoxAutoComplete(cboParent);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        try {
            tableModel.setListTownship(dao.findAll("Township"));

            //Define table selection model to single row selection.
            tblTownship.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblTownship.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (tblTownship.getSelectedRow() >= 0) {
                        selectedRow = tblTownship.convertRowIndexToModel(
                                tblTownship.getSelectedRow());
                    }

                    if (selectedRow >= 0) {
                        setTownship(tableModel.getTownship(selectedRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtTownshipName.getText()).equals("")) {
            status = false;
        } else {
            currTownship.setTownshipName(txtTownshipName.getText());
            Township tsp = new Township();
            if (cboParent.getSelectedIndex() > 0) {
                currTownship.setParentId(getParentTownshipId((Township) cboParent.getSelectedItem()));
                //  currTownship.setParentId(getTownshipId((Township) cboParent.setSelectedItem()));
            }
        }

        return status;
    }

    private void setTownship(Township tsp) {
        currTownship = tsp;
        txtTownshipName.setText(tsp.getTownshipName());
        if (currTownship.getParentId() != null) {
            try {
                Township townshipParent = (Township) dao.find(Township.class, currTownship.getParentId());
                cboParent.setSelectedItem(townshipParent);
            } catch (Exception ex) {
                cboParent.setSelectedItem(null);
                log.error("setTownship : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
        lblStatus.setText("EDIT");

    }

    private void save() {
        try {
            if (isValidEntry()) {
                dao.save(currTownship);
                tblTownship.setRowSorter(null);

                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addTownship(currTownship);
                } else {
                    tableModel.setTownship(selectedRow, currTownship);
                }

                tblTownship.setRowSorter(sorter);
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Township Save", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        currTownship = new Township();
        txtTownshipName.setText(null);
        cboParent.setSelectedItem(null);
        txtTownshipName.requestFocusInWindow();
        lblStatus.setText("NEW");
    }

    private int getParentTownshipId(Township tsp) {
        int id = 0;

        try {
            id = tsp.getTownshipId();
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        return id;
    }

    private void delete() {
        try {
            if (isCanDelete(currTownship.getParentId())) {
                if (lblStatus.getText().equals("EDIT")) {
                    int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                            "Township Delete", JOptionPane.YES_NO_OPTION);

                    if (yes_no == 0) {
                        dao.delete(currTownship);
                        int tmpRow = selectedRow;
                        selectedRow = -1;
                        tblTownship.setRowSorter(null);
                        tableModel.deleteTownship(tmpRow);
                        tblTownship.setRowSorter(sorter);
                    }
                }

                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete this township.",
                    "Township Delete", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private boolean isCanDelete(int id) {
        try {
            List<Patient> listP = dao.findAllHSQL(
                    "select o from Patient o where o.township.townshipId = " + id
            );
            if (listP != null) {
                if (!listP.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "This township is use in patient.\nCannot delete this township.",
                            "Township Delete", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            List<Ams> listA = dao.findAllHSQL(
                    "select o from Ams o where o.township.townshipId = " + id
            );
            if (listA != null) {
                if (!listA.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "This township is use in admission.\nCannot delete this township.",
                            "Township Delete", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (Exception ex) {
            log.error("isCanDelete : " + id + " : " + ex.getMessage());
            return false;
        } finally {
            dao.close();
        }

        return true;
    }

    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                if (entry.getStringValue(i).toUpperCase().startsWith(
                        txtFilter.getText().toUpperCase())) {
                    return true;
                }
            }

            return false;
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

        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTownship = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtTownshipName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboParent = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Township Setup");
        setPreferredSize(new java.awt.Dimension(800, 600));

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblTownship.setFont(Global.textFont);
        tblTownship.setModel(tableModel);
        tblTownship.setRowHeight(23);
        jScrollPane1.setViewportView(tblTownship);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Township Name ");

        txtTownshipName.setFont(Global.textFont);

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Parent");

        cboParent.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        cboParent.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboParentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(lblStatus))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTownshipName)
                            .addComponent(cboParent, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtTownshipName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butSave)
                            .addComponent(butDelete)
                            .addComponent(butClear))
                        .addGap(26, 26, 26)
                        .addComponent(lblStatus)
                        .addGap(0, 132, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        String filter = txtFilter.getText();

        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            //sorter.setRowFilter(RowFilter.regexFilter(filter));
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void cboParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboParentActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox cboParent;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblTownship;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtTownshipName;
    // End of variables declaration//GEN-END:variables
}
