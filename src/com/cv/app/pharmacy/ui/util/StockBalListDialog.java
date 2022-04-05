/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.MachineInfo;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.ui.common.StockBalTableModel;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.StockList;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author WSwe
 */
public class StockBalListDialog extends javax.swing.JDialog {

    private StockList stockList;
    private StockBalTableModel tblBalListModel = new StockBalTableModel();
    private DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    private boolean bindStatus = false;

    /**
     * Creates new form StockBalListDialog
     */
    public StockBalListDialog(StockList stockList, Medicine med, MedicineUP medUp) {
        super(Util1.getParent(), true);

        this.stockList = stockList;

        initComponents();
        initCombo();
        initTable();
        actionMapping();

        cboMedicine.setSelectedItem(med);
        tblBalListModel.setListDetail(stockList.getStockList(med.getMedId()));

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        tblBalListModel.setPoint(new Point(x + (this.getWidth() / 2),
                y + (this.getHeight() / 2)));
        tblBalListModel.setMedUp(medUp);
        lblLocation.setText(stockList.getLocation());

        this.setLocation(x, y);
        this.show();
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        BindingUtil.BindCombo(cboMedicine, stockList.getMedList());

        new ComBoBoxAutoComplete(cboMedicine);
        bindStatus = true;
    }// </editor-fold>

    private void initTable() {
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblBalList.setModel(tblBalListModel);
        
        //tblBalList.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        //tblBalList.getActionMap().put("F5-Action", actionMedList);
        
        tblBalList.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblBalList.getColumnModel().getColumn(1).setPreferredWidth(30);
        tblBalList.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblBalList.getColumnModel().getColumn(3).setPreferredWidth(30);
        tblBalList.getColumnModel().getColumn(4).setPreferredWidth(30);
        tblBalList.getColumnModel().getColumn(5).setPreferredWidth(50);

        tblBalList.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
        tblBalList.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());

        tblBalList.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblBalList.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblBalList.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tblBalList.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

    }

    private void actionMapping() {
        tblBalList.getInputMap().put(KeyStroke.getKeyStroke("F11"), "F11-Action");
        tblBalList.getActionMap().put("F11-Action", actionF11Key);
        tblBalList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        tblBalList.getActionMap().put("ESCAPE", actionF11Key);
        tblBalList.registerKeyboardAction(actionF11Key, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);

        cboMedicine.registerKeyboardAction(actionF11Key, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);
    }

    private Action actionF11Key = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };

    private void timerFocus() {
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("Focus Timer");
                    tblBalList.requestFocus();
                    tblBalList.setColumnSelectionInterval(3, 3);
                    tblBalList.setRowSelectionInterval(0, 0);
                } catch (Exception ex) {
                    System.out.println("Error : " + ex.toString());
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
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
        cboMedicine = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBalList = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stock Balance");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                activated(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jLabel1.setText("Medicine");

        cboMedicine.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        cboMedicine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMedicineActionPerformed(evt);
            }
        });

        tblBalList.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblBalList.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBalList.setRowHeight(23);
        tblBalList.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblBalList);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Location");

        lblLocation.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        lblLocation.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(lblLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboMedicine, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboMedicine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(lblLocation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void activated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_activated
        tblBalList.requestFocusInWindow();
    }//GEN-LAST:event_activated

    private void cboMedicineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMedicineActionPerformed
        if (bindStatus) {
            Medicine med = (Medicine) cboMedicine.getSelectedItem();
            tblBalListModel.setListDetail(stockList.getStockList(med.getMedId()));
        }
    }//GEN-LAST:event_cboMedicineActionPerformed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        // TODO add your handling code here:
        if (KeyEvent.VK_ESCAPE == evt.getKeyCode()) {
            dispose();
        }
    }//GEN-LAST:event_formKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        timerFocus();
    }//GEN-LAST:event_formComponentShown

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboMedicine;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JTable tblBalList;
    // End of variables declaration//GEN-END:variables
}
