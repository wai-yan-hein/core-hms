/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.SaleMenTableModel;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author winswe
 */
public class SaleMenSetup extends javax.swing.JDialog {
    
    static Logger log = Logger.getLogger(SaleMenSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private Doctor currSaleMen = new Doctor();
    private TableRowSorter<TableModel> sorter;
    private SaleMenTableModel tableModel = new SaleMenTableModel();
    private int selectRow = -1;
    private StartWithRowFilter swrf;
    
    /**
     * Creates new form SaleMenSetup
     */
    public SaleMenSetup() {
        super(Util1.getParent(), true);
        initComponents();
        
        try{
            swrf = new StartWithRowFilter(txtFilter);
            initTable();
            sorter = new TableRowSorter(tblSaleMen.getModel());
            tblSaleMen.setRowSorter(sorter);
            tableModel.setListSM(dao.findAll("Doctor"));
        }catch(Exception ex){
            log.error("SaleMenSetup() : " + ex);
        }
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        this.setLocation(x, y);
        setVisible(true);
    }

    private void initTable() {

        //Adjust table column width
        TableColumn column;
        column = tblSaleMen.getColumnModel().getColumn(0);
        column.setPreferredWidth(60);

        column = tblSaleMen.getColumnModel().getColumn(1);
        column.setPreferredWidth(150);

        //Define table selection model to single row selection.
        tblSaleMen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblSaleMen.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (tblSaleMen.getSelectedRow() >= 0) {
                            selectRow = tblSaleMen.convertRowIndexToModel(tblSaleMen.getSelectedRow());
                        }

                        if (selectRow >= 0) {
                            setCurrSaleMan(tableModel.getSaleMen(selectRow));
                        }
                    }
                });
    }
    
    public void setCurrSaleMan(Doctor saleMen) {
        currSaleMen = saleMen;
        txtName.setText(saleMen.getDoctorName());
        txtId.setText(saleMen.getDoctorId());
        chkActive.setSelected(saleMen.isActive());
        txtPhone.setText(currSaleMen.getPhoneNo());
        lblStatus.setText("EDIT");
    }
    
    private boolean isValidEntry() {
        boolean status = true;
        
        if (Util1.nullToBlankStr(txtId.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "ID cannot be blank.",
                        "Sale Man", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (Util1.nullToBlankStr(txtName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Name cannot be blank.",
                        "Sale Man", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            currSaleMen.setDoctorId(txtId.getText().trim());
            currSaleMen.setDoctorName(txtName.getText().trim());
            currSaleMen.setActive(chkActive.isSelected());
            currSaleMen.setPhoneNo(txtPhone.getText());
            currSaleMen.setUpdateDate(new Date());
        }
        
        return status;
    }
    
    private void delete(){
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Sale Man Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currSaleMen);
                    int tmpRow = selectRow;
                    selectRow = -1;
                    tableModel.deleteSaleMen(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this sale man.",
                        "Sale Ma Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
    }
    
    private void clear() {
        selectRow = -1;
        currSaleMen = new Doctor();
        txtName.setText(null);
        txtId.setText(null);
        lblStatus.setText("NEW");
        tblSaleMen.clearSelection();
    }
    
    private void save(){
        try {
            if (isValidEntry()) {
                dao.save(currSaleMen);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addSaleMen(currSaleMen);
                } else {
                    tableModel.setSaleMen(selectRow, currSaleMen);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Sale Man Save", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }finally{
            dao.close();
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

        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSaleMen = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblSaleMen.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblSaleMen.setModel(tableModel);
        tblSaleMen.setRowHeight(23);
        jScrollPane1.setViewportView(tblSaleMen);

        jLabel1.setText("Name : ");

        txtName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        jLabel2.setText("Active : ");

        chkActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActiveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        jLabel3.setText("ID :");

        jLabel4.setText("Phone : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                    .addComponent(txtFilter))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName)
                            .addComponent(txtId)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPhone))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(chkActive))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butDelete)
                            .addComponent(butSave)
                            .addComponent(lblStatus))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActiveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkActiveActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().length() == 0) {
          sorter.setRowFilter(null);
      } else {
          if(Util1.getPropValue("system.text.filter.method").equals("SW")){
              sorter.setRowFilter(swrf);
          } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
          }
      }
    }//GEN-LAST:event_txtFilterKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblSaleMen;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    // End of variables declaration//GEN-END:variables
}
