/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.emr.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.emr.database.entity.AgeRange;
import com.cv.app.emr.ui.common.AgeRangeTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.BestDataAccess;
import com.cv.app.util.Util1;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author MyintMo
 */
public class AgeRangeSetup extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(AgeRangeSetup.class.getName());
    private final AgeRangeTableModel arTableModel = new AgeRangeTableModel();
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private int selectRow = -1;
    private AgeRange currAR = new AgeRange();
    
    /**
     * Creates new form AgeRange
     */
    public AgeRangeSetup() {
        super(Util1.getParent(), true);
        initComponents();
        lblStatus.setText("NEW");
        initTable();
    }

    private void initTable(){
        arTableModel.setListAR(dao.findAll("AgeRange"));
        tblAgeRange.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAgeRange.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblAgeRange.getSelectedRow() >= 0) {
                    selectRow = tblAgeRange.convertRowIndexToModel(tblAgeRange.getSelectedRow());
                }

                if (selectRow >= 0) {
                    setRecord(arTableModel.getAgeRange(selectRow));
                }
            }
        });
    }
    
    private void setRecord(AgeRange ar){
        currAR = ar;
        txtAgeRangeDesp.setText(currAR.getAgerDesp());
        txtSortOrder.setText(currAR.getSortOrder().toString());
        lblStatus.setText("EDIT");
    }
    
    private void clear(){
        currAR = new AgeRange();
        txtAgeRangeDesp.setText(null);
        txtSortOrder.setText(null);
        lblStatus.setText("NEW");
        selectRow = -1;
        txtAgeRangeDesp.requestFocus();
        System.gc();
    }
    
    private boolean isValidEntry(){
        boolean status = true;
        
        if (Util1.nullToBlankStr(txtAgeRangeDesp.getText().trim()).equals("")) {
            JOptionPane.showMessageDialog(this, "Description cannot be blank.",
                    "Blank", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtAgeRangeDesp.requestFocusInWindow();
        } else {
            currAR.setAgerDesp(txtAgeRangeDesp.getText().trim());
            if(txtSortOrder.getText().trim().isEmpty()){
                currAR.setSortOrder(null);
            }else{
                currAR.setSortOrder(Integer.parseInt(txtSortOrder.getText()));
            }
        }
        return status;
    }
    
    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(currAR);
                if(lblStatus.getText().equals("NEW")){
                    arTableModel.addAgeRange(currAR);
                }else{
                    arTableModel.setAgeRange(ERROR, currAR);
                }
                clear();
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Duplicate entry.",
                        "Age Range Name", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }
    
    private void delete(){
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Age Range Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currAR);
                    int tmpRow = selectRow;
                    selectRow = -1;
                    arTableModel.deleteAgeRange(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this age range.",
                        "Age Range Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
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
        tblAgeRange = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtAgeRangeDesp = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtSortOrder = new javax.swing.JTextField();
        butDelete = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Age Range Setup");
        setPreferredSize(new java.awt.Dimension(700, 300));

        tblAgeRange.setFont(Global.textFont);
        tblAgeRange.setModel(arTableModel);
        tblAgeRange.setRowHeight(23);
        jScrollPane1.setViewportView(tblAgeRange);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Desp");

        txtAgeRangeDesp.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Sort Order ");

        txtSortOrder.setFont(Global.textFont);

        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtAgeRangeDesp))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 67, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSortOrder)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAgeRangeDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(txtSortOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butDelete)
                            .addComponent(butClear)
                            .addComponent(butSave))
                        .addGap(12, 12, 12)
                        .addComponent(lblStatus)
                        .addGap(0, 170, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblAgeRange;
    private javax.swing.JTextField txtAgeRangeDesp;
    private javax.swing.JTextField txtSortOrder;
    // End of variables declaration//GEN-END:variables
}
