/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import com.cv.app.inpatient.database.entity.DCCusGroup;
import com.cv.app.inpatient.ui.common.DCCusGroupTableModel;
import com.cv.app.inpatient.ui.common.DCCusGroupDetailTableModel;
import com.cv.app.inpatient.database.entity.DCCusGroupDetail;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author PHSH MDY
 */
public class DCCustomGroup extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(DCCustomGroup.class.getName());
    private final DCCusGroupTableModel oclgTableModel = new DCCusGroupTableModel();
    private final DCCusGroupDetailTableModel oclgdTableModel = new DCCusGroupDetailTableModel();
    private final AbstractDataAccess dao = Global.dao;

    /**
     * Creates new form OPDCustomGroup
     */
    public DCCustomGroup() {
        super(Util1.getParent(), true);
        initComponents();
        oclgTableModel.setParent(tblCusGroup);
        oclgdTableModel.setParent(tblOPDGroup);
        initTable();
        actionMapping();
    }

    private void initTable() {
        try {
            List<DCCusGroup> listOCLG = dao.findAllHSQL("select o from DCCusGroup o order by o.groupName");
            oclgTableModel.setListOCLG(listOCLG);
            tblCusGroup.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int selectRow = -1;
                    if (tblCusGroup.getSelectedRow() >= 0) {
                        selectRow = tblCusGroup.convertRowIndexToModel(tblCusGroup.getSelectedRow());
                    }

                    if (selectRow >= 0) {
                        DCCusGroup group = oclgTableModel.getSelectGroup(selectRow);
                        if(group != null){
                            int groupId = -1;
                            if(group.getId() != null){
                                groupId = group.getId();
                            }
                            oclgdTableModel.setGroupId(groupId);
                        }
                    }
                }
            });
            
            JComboBox cboOPDCat = new JComboBox();
            BindingUtil.BindCombo(cboOPDCat, dao.findAllHSQL("select o from InpCategory o order by o.catName"));
            tblOPDGroup.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cboOPDCat));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        }
    }

    private final Action actionItemDeleteG = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            int row = tblCusGroup.getSelectedRow();
            if (row >= 0) {
                oclgTableModel.deleteRow(row);
            }
        }
    };
    
    private final Action actionItemDeleteC = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            int row = tblOPDGroup.getSelectedRow();
            if (row >= 0) {
                DCCusGroupDetail oclgd = oclgdTableModel.getSelectData(row);
                if(oclgd.getKey().getCusGrpId() != null){
                    try {
                        tblOPDGroup.getCellEditor().stopCellEditing();
                    } catch (Exception ex) {
                    }
                    oclgdTableModel.deleteItem(row, oclgd.getKey().getOpdCatId().getCatId());
                }
            }
        }
    };
    
    private void actionMapping(){
        //F8 event on tblService
        tblCusGroup.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCusGroup.getActionMap().put("F8-Action", actionItemDeleteG);
        
        tblOPDGroup.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblOPDGroup.getActionMap().put("F8-Action", actionItemDeleteC);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFilter1 = new javax.swing.JTextField();
        txtFilter2 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCusGroup = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOPDGroup = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DC Custom Group");

        tblCusGroup.setFont(Global.textFont);
        tblCusGroup.setModel(oclgTableModel);
        tblCusGroup.setRowHeight(23);
        jScrollPane1.setViewportView(tblCusGroup);

        tblOPDGroup.setFont(Global.textFont);
        tblOPDGroup.setModel(oclgdTableModel);
        tblOPDGroup.setRowHeight(23);
        jScrollPane2.setViewportView(tblOPDGroup);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(txtFilter1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter2)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblCusGroup;
    private javax.swing.JTable tblOPDGroup;
    private javax.swing.JTextField txtFilter1;
    private javax.swing.JTextField txtFilter2;
    // End of variables declaration//GEN-END:variables
}
