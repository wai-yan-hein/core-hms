/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.opd.database.entity.Pathologiest;
import com.cv.app.opd.ui.common.PathoTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.util.Util1;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author MyintMo
 */
public class PathologiestSetup extends javax.swing.JPanel implements FormAction, KeyPropagate {

    static Logger log = Logger.getLogger(PathologiestSetup.class.getName());
    private Pathologiest patho = new Pathologiest();
    private final AbstractDataAccess dao = Global.dao;
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    private final PathoTableModel pTableModel = new PathoTableModel();

    /**
     * Creates new form PathologiestSetup
     */
    public PathologiestSetup() {
        initComponents();
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblPatho.getModel());
        tblPatho.setRowSorter(sorter);
        initTable();
    }

    private void setPathologiest(Pathologiest patho) {
        this.patho = patho;

        lblStatus.setText("NEW");
        if (patho != null) {
            txtName.setText(patho.getPathologyName());
            txtRank.setText(patho.getRank());
            txtPost.setText(patho.getPost());
            txtDept.setText(patho.getDept());
            txtLabSection.setText(patho.getLabSection());
            chkActive.setSelected(patho.isActive());
            if (patho.getPathoId() != null) {
                lblStatus.setText("EDIT");
            }
        } else {
            txtName.setText(null);
            txtRank.setText(null);
            txtPost.setText(null);
            txtDept.setText(null);
            txtLabSection.setText(null);
            chkActive.setSelected(false);
        }
    }

    private boolean isValidPathologiest() {
        boolean status = true;
        if (txtName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid pathologiest name.",
                    "Name", JOptionPane.ERROR_MESSAGE);
        } else {
            patho.setActive(chkActive.isSelected());
            patho.setDept(txtDept.getText().trim());
            patho.setLabSection(txtLabSection.getText().trim());
            patho.setPathologyName(txtName.getText().trim());
            patho.setPost(txtPost.getText().trim());
            patho.setRank(txtRank.getText().trim());
        }

        return status;
    }

    private void initTable() {
        try{
        pTableModel.setListPatho(dao.findAll("Pathologiest"));
        tblPatho.getColumnModel().getColumn(0).setPreferredWidth(200); //Name
        tblPatho.getColumnModel().getColumn(1).setPreferredWidth(100); //Rank
        tblPatho.getColumnModel().getColumn(2).setPreferredWidth(100);//Post
        tblPatho.getColumnModel().getColumn(3).setPreferredWidth(10);//Active

        tblPatho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblPatho.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    int row = tblPatho.getSelectedRow();

                    if (row != -1) {
                        setPathologiest(pTableModel.getPatho(tblPatho.convertRowIndexToModel(row)));
                    }
                }
            }
        });
        }catch(Exception ex){
            log.error("initTable : " + ex.getMessage());
        }finally{
            dao.close();
        }
    }

    @Override
    public void save() {
        if (isValidPathologiest()) {
            try {
                dao.save(patho);
                newForm();
                pTableModel.setListPatho(dao.findAll("Pathologiest"));
            } catch (Exception ex) {
                log.error("save : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void newForm() {
        setPathologiest(new Pathologiest());
        txtName.requestFocus();
        System.gc();
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

    @Override
    public void keyEvent(KeyEvent e) {

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
        tblPatho = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtRank = new javax.swing.JTextField();
        txtPost = new javax.swing.JTextField();
        txtDept = new javax.swing.JTextField();
        txtLabSection = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblPatho.setFont(Global.textFont);
        tblPatho.setModel(pTableModel);
        tblPatho.setRowHeight(23);
        jScrollPane1.setViewportView(tblPatho);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Name");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Rank");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Post");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Department");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Lab Section");

        txtName.setFont(Global.textFont);

        txtRank.setFont(Global.textFont);

        txtPost.setFont(Global.textFont);

        txtDept.setFont(Global.textFont);

        txtLabSection.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Active");

        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRank))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPost))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLabSection))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDept))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addGap(0, 89, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtRank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtLabSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(chkActive))
                .addGap(18, 18, 18)
                .addComponent(lblStatus)
                .addContainerGap(134, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(txtFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblPatho;
    private javax.swing.JTextField txtDept;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtLabSection;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPost;
    private javax.swing.JTextField txtRank;
    // End of variables declaration//GEN-END:variables
}
