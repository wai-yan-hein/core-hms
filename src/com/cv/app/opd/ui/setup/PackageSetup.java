/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.ClinicPackage;
import com.cv.app.opd.database.view.VClinicPackageDetail;
import com.cv.app.opd.database.view.VClinicPackageTotal;
import com.cv.app.opd.ui.common.ClinicPackageDetailTableModel;
import com.cv.app.opd.ui.common.ClinicPackageTableModel;
import com.cv.app.opd.ui.common.PackageItemCellEditor;
import com.cv.app.opd.ui.common.PackageTotalTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PackageSetup extends javax.swing.JPanel implements PropertyChangeListener {

    static Logger log = Logger.getLogger(PackageSetup.class.getName());
    private ClinicPackage currPackage;
    private final AbstractDataAccess dao = Global.dao;
    private final ClinicPackageTableModel cpTableModel = new ClinicPackageTableModel();
    private final ClinicPackageDetailTableModel cpdTableModel = new ClinicPackageDetailTableModel();
    private final PackageTotalTableModel pTotalTableModel = new PackageTotalTableModel();
    private final TableRowSorter<TableModel> sorter;
    private final PackageItemCellEditor cellEditor = new PackageItemCellEditor();

    /**
     * Creates new form PackageSetup
     */
    public PackageSetup() {
        initComponents();
        initTable();
        actionMapping();
        cpdTableModel.setTxtSysTotal(txtSalePriceTotal);
        cpdTableModel.setTxtUsrTotal(txtCusPriceTotal);
        txtCusPriceTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtSalePriceTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPrice.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtCusPriceTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtSalePriceTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPrice.setFormatterFactory(NumberUtil.getDecimalFormat());
        sorter = new TableRowSorter(tblPackageDetail.getModel());
        tblPackageDetail.setRowSorter(sorter);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        log.info("propertyChange : " + evt.getPropertyName());
        if (evt.getPropertyName().equals("change")) {
            assignTotal();
        }
    }

    private void setPackage(ClinicPackage cPackage) {
        try {
            this.currPackage = cPackage;
            if (cPackage != null) {
                txtPackageName.setText(cPackage.getPackageName());
                txtPrice.setValue(cPackage.getPrice());
                chkActiveE.setSelected(cPackage.getStatus());
                cboPackageType.setSelectedItem(cPackage.getPackageType());
                List<VClinicPackageDetail> listVCPD = dao.findAllHSQL(
                        "select o from VClinicPackageDetail o where o.pkgId = "
                        + cPackage.getId().toString() + " order by o.id"
                );
                cpdTableModel.setListVCPD(listVCPD);
                cpdTableModel.addEmptyRow();
                cpdTableModel.setPkgId(cPackage.getId());

                assignTotal();
            } else {
                txtPackageName.setText(null);
                txtPrice.setValue(null);
                chkActiveE.setSelected(false);
            }

        } catch (Exception ex) {
            log.error("setPackage : " + ex.getMessage());
        }
    }

    private void initTable() {
        tblPackage.getColumnModel().getColumn(0).setPreferredWidth(150);//Package Name
        tblPackage.getColumnModel().getColumn(1).setPreferredWidth(50);//Price
        tblPackage.getColumnModel().getColumn(2).setPreferredWidth(20);//Status
        tblPackage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPackage.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectRow = -1;
                if (tblPackage.getSelectedRow() >= 0) {
                    selectRow = tblPackage.convertRowIndexToModel(tblPackage.getSelectedRow());
                }

                if (selectRow >= 0) {
                    ClinicPackage cp = cpTableModel.getSelectedPackage(selectRow);
                    setPackage(cp);
                }
            }
        });
        try {
            List<ClinicPackage> listCP = dao.findAllHSQL(
                    "select o from ClinicPackage o"
            );
            cpTableModel.setListCP(listCP);
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblPackageDetail.getColumnModel().getColumn(0).setPreferredWidth(30);//Item Option
        tblPackageDetail.getColumnModel().getColumn(1).setPreferredWidth(200);//Description
        tblPackageDetail.getColumnModel().getColumn(2).setPreferredWidth(20);//Qty
        tblPackageDetail.getColumnModel().getColumn(3).setPreferredWidth(20);//Unit
        tblPackageDetail.getColumnModel().getColumn(4).setPreferredWidth(50);//Sale Price
        tblPackageDetail.getColumnModel().getColumn(5).setPreferredWidth(50);//User Price
        tblPackageDetail.getColumnModel().getColumn(6).setPreferredWidth(50);//Sale Amt
        tblPackageDetail.getColumnModel().getColumn(7).setPreferredWidth(50);//User Amt
        tblPackageDetail.getColumnModel().getColumn(1).setCellEditor(cellEditor);
        tblPackageDetail.getColumnModel().getColumn(2).setCellEditor(
                new BestTableCellEditor()
        );
        tblPackageDetail.getColumnModel().getColumn(4).setCellEditor(
                new BestTableCellEditor()
        );
        tblPackageDetail.getColumnModel().getColumn(5).setCellEditor(
                new BestTableCellEditor()
        );
        cpdTableModel.setParent(tblPackageDetail);
        cpdTableModel.addPropertyChangeListener(this);

        tblPackageTotal.getColumnModel().getColumn(0).setPreferredWidth(30);//Item Option
        tblPackageTotal.getColumnModel().getColumn(1).setPreferredWidth(200);//Group Name
        tblPackageTotal.getColumnModel().getColumn(2).setPreferredWidth(50);//Sale Total
        tblPackageTotal.getColumnModel().getColumn(3).setPreferredWidth(50);//User Total
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(currPackage);
                List<ClinicPackage> listCP = dao.findAllHSQL(
                        "select o from ClinicPackage o"
                );
                cpTableModel.setListCP(listCP);
                cpdTableModel.addEmptyRow();
                cpdTableModel.setPkgId(currPackage.getId());
            } catch (Exception ex) {
                log.error("save : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtPackageName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid package name.",
                    "Package Name", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (txtPrice.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid package price.",
                    "Package Price", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            if (currPackage == null) {
                currPackage = new ClinicPackage();
            }

            currPackage.setPackageName(txtPackageName.getText().trim());
            Double pkgPrice = Double.parseDouble(txtPrice.getValue().toString());
            currPackage.setPrice(pkgPrice);
            currPackage.setStatus(chkActiveE.isSelected());
            String packageType = cboPackageType.getSelectedItem().toString();
            currPackage.setPackageType(packageType);
        }

        return status;
    }

    private void clean() {
        txtPackageName.setText(null);
        txtPrice.setValue(null);
        chkActiveE.setSelected(false);
        cpdTableModel.clear();
        pTotalTableModel.clear();
        currPackage = null;
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (tblPackageDetail.getSelectedRow() >= 0) {
                try {
                    if (tblPackageDetail.getCellEditor() != null) {
                        tblPackageDetail.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }
                int selectRow = tblPackageDetail.convertRowIndexToModel(tblPackageDetail.getSelectedRow());
                cpdTableModel.deleteItem(selectRow);
            }

        }
    };

    private void actionMapping() {
        //F8 event on tblSale
        tblPackageDetail.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblPackageDetail.getActionMap().put("F8-Action", actionItemDelete);
    }

    private void assignTotal() {
        try {
            List<VClinicPackageTotal> listVCPT = dao.findAllHSQL(
                    "select o from VClinicPackageTotal o where o.pkgId = "
                    + currPackage.getId().toString() + " order by o.itemOption, o.typeName"
            );
            pTotalTableModel.setListTTL(listVCPT);
        } catch (Exception ex) {
            log.error("assignTotal : " + ex.getMessage());
        } finally {
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

        jPanel1 = new javax.swing.JPanel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPackage = new javax.swing.JTable();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtPackageName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JFormattedTextField();
        chkActiveE = new javax.swing.JCheckBox();
        butSave = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPackageDetail = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPackageTotal = new javax.swing.JTable();
        txtCusPriceTotal = new javax.swing.JFormattedTextField();
        txtSalePriceTotal = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cboPackageType = new javax.swing.JComboBox<>();

        txtFilter.setFont(Global.textFont);

        tblPackage.setFont(Global.textFont);
        tblPackage.setModel(cpTableModel);
        tblPackage.setRowHeight(23);
        jScrollPane1.setViewportView(tblPackage);

        jCheckBox2.setText("Active");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setText("Package Name : ");

        txtPackageName.setFont(Global.textFont);

        jLabel2.setText("Price : ");

        chkActiveE.setText("Active");
        chkActiveE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActiveEActionPerformed(evt);
            }
        });

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        tblPackageDetail.setFont(Global.textFont);
        tblPackageDetail.setModel(cpdTableModel);
        tblPackageDetail.setRowHeight(23);
        jScrollPane2.setViewportView(tblPackageDetail);

        tblPackageTotal.setFont(Global.textFont);
        tblPackageTotal.setModel(pTotalTableModel);
        tblPackageTotal.setRowHeight(23);
        jScrollPane3.setViewportView(tblPackageTotal);

        txtCusPriceTotal.setEditable(false);

        txtSalePriceTotal.setEditable(false);

        jLabel3.setText("Total : ");

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jLabel4.setText("Type : ");

        cboPackageType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DC", "OPD" }));
        cboPackageType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPackageTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSalePriceTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCusPriceTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPackageType, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPackageName, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkActiveE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCusPriceTotal, txtSalePriceTotal});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActiveE)
                    .addComponent(butSave)
                    .addComponent(butClear)
                    .addComponent(jLabel4)
                    .addComponent(cboPackageType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCusPriceTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSalePriceTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkActiveEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActiveEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkActiveEActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clean();
    }//GEN-LAST:event_butClearActionPerformed

    private void cboPackageTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPackageTypeActionPerformed

    }//GEN-LAST:event_cboPackageTypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox<String> cboPackageType;
    private javax.swing.JCheckBox chkActiveE;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblPackage;
    private javax.swing.JTable tblPackageDetail;
    private javax.swing.JTable tblPackageTotal;
    private javax.swing.JFormattedTextField txtCusPriceTotal;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtPackageName;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JFormattedTextField txtSalePriceTotal;
    // End of variables declaration//GEN-END:variables
}
