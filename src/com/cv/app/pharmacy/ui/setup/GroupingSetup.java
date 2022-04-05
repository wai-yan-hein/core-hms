/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemGroup;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.ui.common.ItemGroupDetailTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
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
import javax.swing.KeyStroke;
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
public class GroupingSetup extends javax.swing.JPanel implements TreeSelectionListener,
        KeyPropagate {
    static Logger log = Logger.getLogger(GroupingSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Grouping");
    private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private JPopupMenu popup = new JPopupMenu();
    private int newNodeSuffix = 1;
    private ItemGroupDetailTableModel groupDetailTableModel = new ItemGroupDetailTableModel(dao);
    private ItemGroup selectedIg;
    
    /**
     * Creates new form GroupingSetup
     */
    public GroupingSetup() {
        initComponents();
        initTree();
        initPopupMenu();
        initTable();
        actionMapping();
        initCombo();
        disableCtrl();
    }

    private void disableCtrl(){
        cboItemType.setEnabled(false);
        cboCategory.setEnabled(false);
        cboBrand.setEnabled(false);
        
        butBrandAdd.setEnabled(false);
        butBrandRemove.setEnabled(false);
        butCategoryAdd.setEnabled(false);
        butCategoryRemove.setEnabled(false);
        butItemTypeAdd.setEnabled(false);
        butItemTypeRemove.setEnabled(false);
    }
    
    private void enableCtrl(){
        cboItemType.setEnabled(true);
        cboCategory.setEnabled(true);
        cboBrand.setEnabled(true);
        
        butBrandAdd.setEnabled(true);
        butBrandRemove.setEnabled(true);
        butCategoryAdd.setEnabled(true);
        butCategoryRemove.setEnabled(true);
        butItemTypeAdd.setEnabled(true);
        butItemTypeRemove.setEnabled(true);
    }
    
    private void initCombo(){
        BindingUtil.BindCombo(cboItemType, dao.findAll("ItemType"));
        BindingUtil.BindCombo(cboCategory, 
            dao.findAllHSQL("select o from Category o order by o.catName"));
        BindingUtil.BindCombo(cboBrand, 
            dao.findAllHSQL("select o from ItemBrand o order by o.brandName"));

        new ComBoBoxAutoComplete(cboItemType, this);
        new ComBoBoxAutoComplete(cboCategory, this);
        new ComBoBoxAutoComplete(cboBrand, this);
    }
    
    private void initTable() {
        //groupDetailTableModel.addEmptyRow();
        tblItem.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblItem.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblItem.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblItem.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
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
                                String nodeName = "Group-" + newNodeSuffix++;
                                ItemGroup ig = new ItemGroup();
                                ig.setGroupName(nodeName);

                                if (tmpNode.getUserObject() instanceof ItemGroup) {
                                    ig.setParentGroupId(((ItemGroup) tmpNode.getUserObject()).getGroupId());
                                } else {
                                    ig.setParentGroupId(0);
                                }

                                dao.save(ig);
                                addObject(ig);
                            } else {
                            }
                            break;
                        case "Delete":
                            if (tmpNode != null) {
                                if (tmpNode.getUserObject() instanceof ItemGroup) {
                                    int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                                            "Are you sure to delete?",
                                            "Grouping delete", JOptionPane.YES_NO_OPTION);
                                    if (yes_no == 0) {
                                        int groupId = ((ItemGroup) tmpNode.getUserObject()).getGroupId();
                                        removeGrouping(groupId);
                                        removeCurrentNode();
                                        groupDetailTableModel.setGroupId(0);
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
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);

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
              String strGrpName = node.getUserObject().toString();
                selectedIg.setGroupName(strGrpName);
                
                try{
                    dao.save(selectedIg);
                    node.setUserObject(selectedIg);
                    lblGroupName.setText(strGrpName);
                    enableCtrl();
                }catch(Exception ex){
                    log.error("treeNodesChanged : " + ex.toString());
                }finally{
                    dao.close();
                }
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
                popup.show(GroupingSetup.this, e.getX(), e.getY() + 20);
            }
        }
    }

    private void createTreeNode(int parentGroupId, DefaultMutableTreeNode treeRoot) {
        List<ItemGroup> listItemGroup = dao.findAllHSQL(
                "from ItemGroup as ig where ig.parentGroupId =" + parentGroupId);

        for (ItemGroup ig : listItemGroup) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(ig);
            treeRoot.add(child);
            createTreeNode(ig.getGroupId(), child);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent se) {
        JTree tree = (JTree) se.getSource();
        TreePath tmpPath = tree.getSelectionPath();

        if (tmpPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tmpPath.getLastPathComponent();

            if (selectedNode.getUserObject() instanceof ItemGroup) {
                selectedIg = (ItemGroup) selectedNode.getUserObject();
                lblGroupName.setText(selectedIg.getGroupName());
                groupDetailTableModel.setGroupId(selectedIg.getGroupId());
            } else {
                lblGroupName.setText(" ");
                groupDetailTableModel.setGroupId(0);
            }
            enableCtrl();
        } else {
            groupDetailTableModel.setGroupId(0);
            lblGroupName.setText(" ");
            disableCtrl();
        }
    }
    
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblItem.getSelectedRow() >= 0) {
                try {
                    if(tblItem.getCellEditor() != null){
                        tblItem.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }
                groupDetailTableModel.deleteItem(tblItem.convertRowIndexToModel(tblItem.getSelectedRow()));
            }
        }
    };

    private void actionMapping() {
        //F8 event on tblSale
        tblItem.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblItem.getActionMap().put("F8-Action", actionItemDelete);
    }

    private void removeGrouping(int groupId) {
        String strSqlDeleteDetail1 = "delete from item_group_detail where group_id = " + groupId;
        String strSqlDeleteDetail2 = "delete from item_group_detail where group_id in "
                + "(select group_id from item_group where parent_group_id = " + groupId + ")";
        String strSqlDeleteGroup1 = "delete from item_group where parent_group_id = " + groupId;
        String strSqlDeleteGroup2 = "delete from item_group where group_id = " + groupId;

        dao.execSql(strSqlDeleteDetail1, strSqlDeleteDetail2, strSqlDeleteGroup1,
                strSqlDeleteGroup2);
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
        lblGroupName = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboItemType = new javax.swing.JComboBox<>();
        butItemTypeAdd = new javax.swing.JButton();
        butItemTypeRemove = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cboCategory = new javax.swing.JComboBox<>();
        butCategoryAdd = new javax.swing.JButton();
        butCategoryRemove = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox<>();
        butBrandAdd = new javax.swing.JButton();
        butBrandRemove = new javax.swing.JButton();

        treGrouping.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(treGrouping);

        lblGroupName.setFont(new java.awt.Font("Zawgyi-One", 1, 12)); // NOI18N
        lblGroupName.setText(" ");

        tblItem.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblItem.setModel(groupDetailTableModel);
        tblItem.setRowHeight(23);
        jScrollPane2.setViewportView(tblItem);

        jLabel1.setText("Item Type");

        butItemTypeAdd.setText("Add");
        butItemTypeAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butItemTypeAddActionPerformed(evt);
            }
        });

        butItemTypeRemove.setText("Remove");
        butItemTypeRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butItemTypeRemoveActionPerformed(evt);
            }
        });

        jLabel2.setText("Category");

        butCategoryAdd.setText("Add");
        butCategoryAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCategoryAddActionPerformed(evt);
            }
        });

        butCategoryRemove.setText("Remove");
        butCategoryRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCategoryRemoveActionPerformed(evt);
            }
        });

        jLabel3.setText("Brand");

        butBrandAdd.setText("Add");
        butBrandAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrandAddActionPerformed(evt);
            }
        });

        butBrandRemove.setText("Remove");
        butBrandRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrandRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboItemType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butItemTypeAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butItemTypeRemove))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butCategoryAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCategoryRemove))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboBrand, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butBrandAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butBrandRemove)))
                .addGap(12, 12, 12))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboItemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butItemTypeAdd)
                    .addComponent(butItemTypeRemove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butCategoryAdd)
                    .addComponent(butCategoryRemove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butBrandAdd)
                    .addComponent(butBrandRemove))
                .addContainerGap(168, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                    .addComponent(lblGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblGroupName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butItemTypeAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butItemTypeAddActionPerformed
        ItemType selIT = (ItemType)cboItemType.getSelectedItem();
        if(selIT instanceof ItemType && selectedIg != null){
            String id = selIT.getItemTypeCode();
            groupDetailTableModel.addItemType(selectedIg.getGroupId(), id);
        }
    }//GEN-LAST:event_butItemTypeAddActionPerformed

    private void butItemTypeRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butItemTypeRemoveActionPerformed
        ItemType selIT = (ItemType)cboItemType.getSelectedItem();
        if(selIT instanceof ItemType && selectedIg != null){
            String id = selIT.getItemTypeCode();
            groupDetailTableModel.deleteItemType(selectedIg.getGroupId(), id);
        }
    }//GEN-LAST:event_butItemTypeRemoveActionPerformed

    private void butCategoryAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCategoryAddActionPerformed
        Category cat = (Category)cboCategory.getSelectedItem();
        if(cat instanceof Category){
            int id = cat.getCatId();
            groupDetailTableModel.addCategory(selectedIg.getGroupId(), id);
        }
    }//GEN-LAST:event_butCategoryAddActionPerformed

    private void butCategoryRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCategoryRemoveActionPerformed
        Category cat = (Category)cboCategory.getSelectedItem();
        if(cat instanceof Category){
            int id = cat.getCatId();
            groupDetailTableModel.deleteCategory(selectedIg.getGroupId(), id);
        }
    }//GEN-LAST:event_butCategoryRemoveActionPerformed

    private void butBrandAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrandAddActionPerformed
        ItemBrand ib = (ItemBrand)cboBrand.getSelectedItem();
        if(ib instanceof ItemBrand){
            int id = ib.getBrandId();
            groupDetailTableModel.addBrand(selectedIg.getGroupId(), id);
        }
    }//GEN-LAST:event_butBrandAddActionPerformed

    private void butBrandRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrandRemoveActionPerformed
        ItemBrand ib = (ItemBrand)cboBrand.getSelectedItem();
        if(ib instanceof ItemBrand){
            int id = ib.getBrandId();
            groupDetailTableModel.deleteBrand(selectedIg.getGroupId(), id);
        }
    }//GEN-LAST:event_butBrandRemoveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butBrandAdd;
    private javax.swing.JButton butBrandRemove;
    private javax.swing.JButton butCategoryAdd;
    private javax.swing.JButton butCategoryRemove;
    private javax.swing.JButton butItemTypeAdd;
    private javax.swing.JButton butItemTypeRemove;
    private javax.swing.JComboBox<String> cboBrand;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboItemType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblGroupName;
    private javax.swing.JTable tblItem;
    private javax.swing.JTree treGrouping;
    // End of variables declaration//GEN-END:variables
}
