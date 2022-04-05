/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cv.app.ui.common;

import com.cv.app.pharmacy.database.entity.Menu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author winswe
 */
public class TreeMenuSelectionListener implements TreeSelectionListener{
    @Override
    public void valueChanged(TreeSelectionEvent se) {
        JTree tree = (JTree) se.getSource();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
            .getLastSelectedPathComponent();
                
        if ((selectedNode.isLeaf() && !selectedNode.isRoot())) {
            Menu selMenu = (Menu) selectedNode.getUserObject();
            //Global.setMenu(selMenu);
            tree.firePropertyChange("MenuSelect,"+selMenu.getMenuId() + "," + selMenu.getParent() +
                    "," + selMenu.getMenuClass(), 0, 1);
        }
    }
}
