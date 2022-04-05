/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemGroup;
import com.cv.app.pharmacy.database.entity.PharmacySystem;
import com.cv.app.util.Util1;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.RowFilter;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author winswe
 */
public class PharmacySystemTreeSetup extends javax.swing.JDialog implements TreeSelectionListener {

    static Logger log = Logger.getLogger(PharmacySystemTreeSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private PharmacySystem ps = new PharmacySystem();
    private TableRowSorter<TableModel> sorter;
    private int selectRow = -1;
    private StartWithRowFilter swrf;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Pharmacy System");
    private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private JPopupMenu popup = new JPopupMenu();

    /**
     * Creates new form PharmacySystem
     */
    public PharmacySystemTreeSetup() {
        super(Util1.getParent(), true);
        initComponents();

        try {
            swrf = new StartWithRowFilter(txtFilter);
            initTree();
        } catch (Exception ex) {
            log.error("SaleMenSetup() : " + ex);
        }
    }

    private void initTree() {
        treeModel.addTreeModelListener(new GroupingTreeModelListener());
        trePharSystem.setEditable(true);
        trePharSystem.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        trePharSystem.setShowsRootHandles(true);
        trePharSystem.addMouseListener(new MousePopupListener());
        createTreeNode(0l, rootNode);
        trePharSystem.addTreeSelectionListener(this);
    }

    private void setCurrSystem(PharmacySystem ps) {
        this.ps = ps;
        txtSystemDesp.setText(ps.getSystemDesp());
        txtMMDesp.setText(ps.getMmDesp());
        lblStatus.setText("EDIT");
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtSystemDesp.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "System desp cannot be blank.",
                    "System Desp", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            ps.setSystemDesp(txtSystemDesp.getText().trim());
            ps.setUpdatedDate(new Date());
            ps.setMmDesp(txtMMDesp.getText());
        }

        return status;
    }

    private void clear() {
        ps = new PharmacySystem();
        lblStatus.setText("NEW");
        txtSystemDesp.setText("");
        txtMMDesp.setText("");
        txtSystemDesp.requestFocus();
    }

    private void save() {
        try {
            if (isValidEntry()) {
                dao.save(ps);
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) trePharSystem.getSelectionPath().getLastPathComponent();
                selectedNode.setUserObject(ps);
                clear();
                /*tableModel.setListPS(dao.findAllHSQL(
                        "select o from PharmacySystem o order by o.systemDesp"));*/
            }
        } catch (Exception ex) {
            log.error("save : " + ex.toString());
        }
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Pharmacy System", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(ps);
                    int tmpRow = selectRow;
                    selectRow = -1;
                    //tableModel.delete(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this pharmacy system.",
                        "Pharmacy System", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
    }

    @Override
    public void valueChanged(TreeSelectionEvent se) {
        JTree tree = (JTree) se.getSource();
        TreePath tmpPath = tree.getSelectionPath();

        if (tmpPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tmpPath.getLastPathComponent();

            if (selectedNode.getUserObject() instanceof PharmacySystem) {
                setCurrSystem((PharmacySystem) selectedNode.getUserObject());
            } else {
                clear();
            }
        } else {
            clear();
        }
    }

    class GroupingTreeModelListener implements TreeModelListener {

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            TreePath parentPath = trePharSystem.getSelectionPath();
            node = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
            
            if (node != null) {
                setCurrSystem((PharmacySystem) node.getUserObject());
            }
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }

    // An inner class to check whether mouse events are the popup trigger
    class MousePopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            checkPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            checkPopup(e);
        }

        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int row = trePharSystem.getClosestRowForLocation(e.getX(), e.getY());
                trePharSystem.setSelectionRow(row);
                popup.show(PharmacySystemTreeSetup.this, e.getX(), e.getY() + 20);
            }
        }
    }

    private void createTreeNode(Long parentGroupId, DefaultMutableTreeNode treeRoot) {
        List<PharmacySystem> listPS = dao.findAllHSQL(
                "from PharmacySystem as ps where ps.parentId =" + parentGroupId
        + " order by ps.systemDesp");

        listPS.forEach(tmpPS -> {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(tmpPS);
            treeRoot.add(child);
            createTreeNode(tmpPS.getId(), child);
        });
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
        jLabel1 = new javax.swing.JLabel();
        txtSystemDesp = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMMDesp = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        trePharSystem = new javax.swing.JTree(treeModel);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pharmacy System Setup");
        setPreferredSize(new java.awt.Dimension(1024, 600));

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jLabel1.setText("System Desp : ");

        txtSystemDesp.setFont(Global.textFont);

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

        lblStatus.setText("NEW");

        jLabel2.setText("MM Desp : ");

        txtMMDesp.setFont(Global.textFont);

        jLabel3.setText("Type : ");

        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Oral", "Injection" }));

        jScrollPane2.setViewportView(trePharSystem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSystemDesp))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMMDesp))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtSystemDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtMMDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butDelete)
                            .addComponent(butSave)
                            .addComponent(lblStatus)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
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
        if (txtFilter.getText().length() == 0) {
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
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTree trePharSystem;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtMMDesp;
    private javax.swing.JTextField txtSystemDesp;
    // End of variables declaration//GEN-END:variables
}
