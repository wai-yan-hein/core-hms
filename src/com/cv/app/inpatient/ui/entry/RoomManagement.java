/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.cv.app.inpatient.ui.entry;

import com.cv.app.common.CalculateObserver;
import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.inpatient.database.healper.RoomStatus;
import com.cv.app.inpatient.ui.common.RoomManagementTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author cv-svr
 */
public class RoomManagement extends javax.swing.JPanel implements CalculateObserver {

    static Logger log = Logger.getLogger(RoomManagement.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private final RoomManagementTableModel tableModel = new RoomManagementTableModel();
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    
    /**
     * Creates new form RoomManagement
     */
    public RoomManagement() {
        initComponents();
        initTable();
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblRoomList.getModel());
        tblRoomList.setRowSorter(sorter);
        getData();
    }

    @Override
    public void calculate(){
        getData();
    }
    
    private void getData(){
        try {
            ResultSet rs = dao.execSQL("select aa.id, str.code, str.description as floor, aa.description as room, aa.price, aa.reg_no, pd.patient_name\n"
                    + "  from (SELECT bs.*,bst.type_desp,ft.price \n"
                    + "		  FROM building_structur_type bst,building_structure bs left join facility_type ft on bs.facility_type_id =ft.type_id\n"
                    + "	     where bs.structure_type_id = bst.type_id and facility_type_id is not null) aa \n"
                    + "inner join building_structure str on aa.parent_id = str.id\n"
                    + "left join patient_detail pd on aa.reg_no = pd.reg_no\n"
                    + "order by str.code, str.description, aa.description");
            if (rs != null) {
                List<RoomStatus> list = new ArrayList();
                while (rs.next()) {
                    RoomStatus ros = new RoomStatus(
                            rs.getInt("id"),
                            rs.getString("floor"),
                            rs.getString("code"),
                            rs.getString("room"),
                            rs.getDouble("price"),
                            rs.getString("reg_no"),
                            rs.getString("patient_name"),
                            false
                    );
                    if (ros.getRegNo() == null) {
                        ros.setStatus(false);
                    } else {
                        ros.setStatus(true);
                    }
                    list.add(ros);
                }

                tableModel.setList(list);
            }
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    private void initTable() {
        tableModel.setObserver(this);
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
        tblRoomList = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();

        tblRoomList.setFont(Global.textFont);
        tblRoomList.setModel(tableModel);
        tblRoomList.setRowHeight(23);
        jScrollPane1.setViewportView(tblRoomList);

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(txtFilter))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorter.setRowFilter(swrf);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblRoomList;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
