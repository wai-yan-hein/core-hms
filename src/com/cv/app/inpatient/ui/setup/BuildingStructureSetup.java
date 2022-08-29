/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.inpatient.database.entity.BuildingStructurType;
import com.cv.app.inpatient.database.entity.BuildingStructure;
import com.cv.app.inpatient.database.entity.FacilityType;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class BuildingStructureSetup extends javax.swing.JPanel implements TreeSelectionListener,
        KeyPropagate, FormAction {

    static Logger log = Logger.getLogger(BuildingStructureSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(Util1.getPropValue("report.company.name"));
    private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private final JPopupMenu popup = new JPopupMenu();
    private int newNodeSuffix = 1;
    private BuildingStructure selStructure;

    @Override
    public void newForm() {
        selStructure = new BuildingStructure();
        txtCode.setText(null);
        txtDescription.setText(null);
        cboFacilityType.setSelectedItem(null);
        cboStructureType.setSelectedItem(null);
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

    /**
     * Creates new form GroupingSetup
     */
    public BuildingStructureSetup() {
        initComponents();
        initTree();
        initPopupMenu();
        actionMapping();
        initCombo();
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboStructureType,
                    dao.findAllHSQL("select o from BuildingStructurType o order by o.typeDesp"));
            BindingUtil.BindCombo(cboFacilityType,
                    dao.findAllHSQL("select o from FacilityType o order by o.typeDesp"));

            new ComBoBoxAutoComplete(cboStructureType, this);
            new ComBoBoxAutoComplete(cboFacilityType, this);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTree() {
        treeModel.addTreeModelListener(new GroupingTreeModelListener());
        treGrouping.setEditable(true);
        treGrouping.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treGrouping.setShowsRootHandles(true);
        treGrouping.addMouseListener(new MousePopupListener());
        createTreeNode(0, rootNode);
        treGrouping.addTreeSelectionListener(this);
    }

    private void initPopupMenu() {
        ActionListener menuListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    DefaultMutableTreeNode tmpNode;
                    TreePath parentPath = treGrouping.getSelectionPath();

                    tmpNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());

                    switch (event.getActionCommand()) {
                        case "Add":
                            if (tmpNode != null) {
                                String nodeName = "New-" + newNodeSuffix++;
                                BuildingStructure bs = new BuildingStructure();
                                bs.setDescription(nodeName);

                                if (tmpNode.getUserObject() instanceof BuildingStructure) {
                                    bs.setParent(((BuildingStructure) tmpNode.getUserObject()).getId());
                                } else {
                                    bs.setParent(0);
                                }

                                dao.save(bs);
                                addObject(bs);
                            } else {
                            }
                            break;
                        case "Delete":
                            if (tmpNode != null) {
                                if (tmpNode.getUserObject() instanceof BuildingStructure) {
                                    int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                                            "Are you sure to delete?",
                                            "Delete", JOptionPane.YES_NO_OPTION);
                                    if (yes_no == 0) {
                                        int id = ((BuildingStructure) tmpNode.getUserObject()).getId();
                                        removeGrouping(id);
                                        removeCurrentNode();
                                    }
                                }
                            }
                            break;
                        case "Rename":
                            if (tmpNode != null) {
                                treGrouping.setEditable(true);
                                treGrouping.startEditingAtPath(parentPath);
                            }
                            break;
                    }
                } catch (Exception ex) {
                    log.error("initPopupMenu : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    dao.rollBack();
                } finally {
                    dao.close();
                }

            }
        };

        JMenuItem item;
        popup.add(item = new JMenuItem("Add"));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Delete"));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Rename"));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
    }

    /**
     * Remove the currently selected node.
     */
    public void removeCurrentNode() {
        TreePath currentSelection = treGrouping.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                //return;
            }
        }

        // Either there was no selection, or the root was selected.
        //toolkit.beep();
    }

    private void removeGrouping(int groupId) {
        try {
            String strSqlDelete = "delete from building_structure where id = " + groupId;
            dao.execSql(strSqlDelete);
        } catch (Exception ex) {
            log.error("remove : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    /**
     * Add child to the currently selected node.
     */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode;
        TreePath parentPath = treGrouping.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child) {
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child,
            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode
                = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent,
                parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            treGrouping.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    class GroupingTreeModelListener implements TreeModelListener {

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            TreePath parentPath = treGrouping.getSelectionPath();
            node = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());

            if (node != null) {
                /*String strGrpName = node.getUserObject().toString();
                selectedIg.setGroupName(strGrpName);

                try {
                    dao.save(selectedIg);
                    node.setUserObject(selectedIg);
                    lblGroupName.setText(strGrpName);
                    enableCtrl();
                } catch (Exception ex) {
                    log.error("treeNodesChanged : " + ex.toString());
                } finally {
                    dao.close();
                }*/
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
                int row = treGrouping.getClosestRowForLocation(e.getX(), e.getY());
                treGrouping.setSelectionRow(row);
                popup.show(BuildingStructureSetup.this, e.getX(), e.getY() + 20);
            }
        }
    }

    private void createTreeNode(int parentGroupId, DefaultMutableTreeNode treeRoot) {
        try {
            List<BuildingStructure> listItemGroup = dao.findAllHSQL(
                    "from BuildingStructure as o where o.parent =" + parentGroupId);

            for (BuildingStructure ig : listItemGroup) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(ig);
                treeRoot.add(child);
                createTreeNode(ig.getId(), child);
            }
        } catch (Exception ex) {
            log.error("createTreeNode : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent se) {
        JTree tree = (JTree) se.getSource();
        TreePath tmpPath = tree.getSelectionPath();

        if (tmpPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tmpPath.getLastPathComponent();

            if (selectedNode.getUserObject() instanceof BuildingStructure) {
                selStructure = (BuildingStructure) selectedNode.getUserObject();
                txtCode.setText(selStructure.getCode());
                txtDescription.setText(selStructure.getDescription());
                cboFacilityType.setSelectedItem(selStructure.getFacilityType());
                cboStructureType.setSelectedItem(selStructure.getStructureType());
            } else {

            }
        } else {
        }
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            /*if (tblItem.getSelectedRow() >= 0) {
                try {
                    tblItem.getCellEditor().stopCellEditing();
                } catch (Exception ex) {
                }
                //groupDetailTableModel.deleteItem(tblItem.convertRowIndexToModel(tblItem.getSelectedRow()));
            }*/
        }
    };

    private void actionMapping() {
        //F8 event on tblSale
        //tblItem.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        //tblItem.getActionMap().put("F8-Action", actionItemDelete);
    }

    @Override
    public void keyEvent(KeyEvent e) {
        /*if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
            
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
            
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
            
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
            
        }*/
    }

    @Override
    public void save() {
        if (txtDescription.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid type description.",
                    "Type Description", JOptionPane.ERROR_MESSAGE);
        } else {
            selStructure.setCode(txtCode.getText().trim());
            selStructure.setDescription(txtDescription.getText().trim());
            selStructure.setFacilityType((FacilityType) cboFacilityType.getSelectedItem());
            selStructure.setStructureType((BuildingStructurType) cboStructureType.getSelectedItem());

            try {
                dao.save(selStructure);

                DefaultMutableTreeNode node;
                TreePath parentPath = treGrouping.getSelectionPath();
                node = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());

                if (node != null) {
                    node.setUserObject(selStructure);
                }

                newForm();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
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
        treGrouping = new javax.swing.JTree(treeModel);
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        txtDescription = new javax.swing.JTextField();
        cboStructureType = new javax.swing.JComboBox<>();
        cboFacilityType = new javax.swing.JComboBox<>();
        butStructureType = new javax.swing.JButton();
        butFacilityType = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        treGrouping.setFont(Global.textFont);
        jScrollPane1.setViewportView(treGrouping);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Code");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Description");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Structure Type");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Facility Type");

        txtCode.setFont(Global.textFont);

        txtDescription.setFont(Global.textFont);

        cboStructureType.setFont(Global.textFont);

        cboFacilityType.setFont(Global.textFont);

        butStructureType.setText("...");
        butStructureType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStructureTypeActionPerformed(evt);
            }
        });

        butFacilityType.setText("...");
        butFacilityType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFacilityTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboFacilityType, 0, 91, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butFacilityType))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboStructureType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butStructureType))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDescription)
                            .addComponent(txtCode))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboStructureType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butStructureType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboFacilityType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butFacilityType))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butStructureTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStructureTypeActionPerformed
        StructureTypeSetupDialog dialog = new StructureTypeSetupDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        initCombo();
    }//GEN-LAST:event_butStructureTypeActionPerformed

    private void butFacilityTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFacilityTypeActionPerformed
        FacilityTypeSetupDialog dialog = new FacilityTypeSetupDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        initCombo();
    }//GEN-LAST:event_butFacilityTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butFacilityType;
    private javax.swing.JButton butStructureType;
    private javax.swing.JComboBox<String> cboFacilityType;
    private javax.swing.JComboBox<String> cboStructureType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree treGrouping;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtDescription;
    // End of variables declaration//GEN-END:variables
}
