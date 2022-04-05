/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MenuSetup.java
 *
 * Created on Apr 22, 2012, 9:52:49 PM
 */
package com.cv.app.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Menu;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.richclient.application.Application;

/**
 *
 * @author winswe
 */
public class MenuSetup extends javax.swing.JPanel implements TreeSelectionListener{
    static Logger log = Logger.getLogger(MenuSetup.class.getName());
    private Menu currMenu;
    private final AbstractDataAccess dao = Global.dao;
    private DefaultMutableTreeNode treeRoot;
    private BestAppFocusTraversalPolicy focusPolicy;
    
    /** Creates new form MenuSetup */
    public MenuSetup() {
        initComponents();
        
        try{
            dao.open();
            initCombo();
            initTree();
            dao.close();
        }catch(Exception ex){
            log.error(ex.toString());
        }
        
        clear();
        treMenu.addTreeSelectionListener(this);
        
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    /**
     * Get the value of currMenu
     *
     * @return the value of currMenu
     */
    public Menu getCurrMenu() {
        return currMenu;
    }

    /**
     * Set the value of currMenu
     *
     * @param currMenu new value of currMenu
     */
    public void setCurrMenu(Menu currMenu) {
        System.out.println("setCurrMenu");
        
        this.currMenu = currMenu;
        
        try{
            dao.open();
            if(currMenu.getParent() != null){
                Menu menu = (Menu)dao.find(Menu.class, currMenu.getParent());
                cboParent.setSelectedItem(menu);
            }
            
            dao.close();
        }catch(Exception ex){
            log.error(ex.toString());
            cboParent.setSelectedItem(null);
        }
        
        propertyChangeSupport.firePropertyChange("currMenu", 0, 1);
            
    }
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void clear(){
        setCurrMenu(new Menu());
        setFocus();
    }
    
    private void initTree(){
        DefaultTreeModel treeModel = (DefaultTreeModel)treMenu.getModel();
        try{
            treeModel.setRoot(null);
        }catch(Exception ex){
            log.error(ex.toString());
        }
        
        treeRoot = null;
        treeRoot = new DefaultMutableTreeNode("BEST-System");
        createTreeNode(0,treeRoot);
        treeModel.setRoot(treeRoot);
        
        
        //treMenu.addPropertyChangeListener(propertyChangeListener);
    }
    
    private void createTreeNode(int parentMenuID, DefaultMutableTreeNode treeRoot){
        List<Menu> listMenu = dao.findAllHSQL("from Menu as mu where mu.parent =" + parentMenuID);
        
        for(Menu menu : listMenu){
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(menu);
            treeRoot.add(child);
            createTreeNode(menu.getMenuId(), child);
        }
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent se) {
        JTree tree = (JTree) se.getSource();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
            .getLastSelectedPathComponent();
        
        if(selectedNode != null){
            //if ((selectedNode.isLeaf() && !selectedNode.isRoot())) {
                Menu selMenu = (Menu) selectedNode.getUserObject();
                setCurrMenu(selMenu);
            //}
        }
    }
    
    private void showDialog(String panelName){
        SetupDialog dialog = new SetupDialog(Application.instance().getActiveWindow().getControl(), 
                true, panelName);
        
        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - dialog.getWidth()) /2;
        int y = (screen.height - dialog.getHeight()) /2;
        
        dialog.setLocation(x, y);
        dialog.show();
        System.out.println("After dialog close");
    }
    
    private void initCombo(){
        int currMenuId = 0;
        
        try{
          if(currMenu != null){
            currMenuId = NumberUtil.NZeroInt(currMenu.getMenuId());
          }
        }catch(Exception ex){
            log.error(ex.toString());
        }
        
        BindingUtil.BindCombo(cboType, dao.findAll("MenuType"));
        BindingUtil.BindCombo(cboParent, dao.findAllHSQL("from Menu as mu where mu.menuId <> " 
                + currMenuId));
        new ComBoBoxAutoComplete(cboType);
        new ComBoBoxAutoComplete(cboParent);
    }
    
    private int getParentMenuId(Menu menu){
        int id = 0;
        
        try{
            id = menu.getMenuId();
        }catch(Exception ex){
            log.error(ex.toString());
        }
        
        return id;
    }
    
    private boolean isValidEntry(){
        boolean status = true;
        
        if(Util1.nullToBlankStr(currMenu.getMenuName()).equals("")){
            status = false;
            JOptionPane.showMessageDialog(this, "Menu Name must not be blank or null.", 
                    "Menu Name null error.", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocusInWindow();
        }else if(currMenu.getMenuType() == null){
            status = false;
            JOptionPane.showMessageDialog(this, "Menu Type must be choose.", 
                    "Menu Type null error.", JOptionPane.ERROR_MESSAGE);
            cboType.requestFocusInWindow();
        }else{
            currMenu.setMenuName(currMenu.getMenuName().trim());
            
            if(currMenu.getMenuClass() != null)
                currMenu.setMenuClass(currMenu.getMenuClass().trim());
            
            if(currMenu.getMenuUrl() != null)
                currMenu.setMenuUrl(currMenu.getMenuUrl().trim());
            
            currMenu.setParent(getParentMenuId((Menu)cboParent.getSelectedItem()));
        }
        
        return status;
    }
    
    private void applyFocusPolicy(){
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();
        
        focusOrder.add(txtName);
        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }
    
    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
    private void AddFocusMoveKey(){
        Set backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);
        
        Set forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);

        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        
        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
    }//</editor-fold>
    
    public void setFocus(){
        txtName.requestFocusInWindow();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jScrollPane1 = new javax.swing.JScrollPane();
        treMenu = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        cboParent = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtClass = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        butMenuTypeSetup = new javax.swing.JButton();

        treMenu.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        jScrollPane1.setViewportView(treMenu);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Name");

        txtName.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currMenu.menuName}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Type");

        cboType.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        cboType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currMenu.menuType}"), cboType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Parent");

        cboParent.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        cboParent.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboParentActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Class");

        txtClass.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currMenu.menuClass}"), txtClass, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("URL");

        txtURL.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currMenu.menuUrl}"), txtURL, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butMenuTypeSetup.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        butMenuTypeSetup.setText("...");
        butMenuTypeSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butMenuTypeSetupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                .addGap(10, 10, 10))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtURL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                    .addComponent(txtClass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                    .addComponent(cboParent, javax.swing.GroupLayout.Alignment.LEADING, 0, 165, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(cboType, 0, 124, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(butMenuTypeSetup, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)))
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 69, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(butMenuTypeSetup))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
        //DefaultTreeModel treeModel = (DefaultTreeModel)treMenu.getModel();
        //treeModel.setRoot(null);
    }//GEN-LAST:event_butClearActionPerformed

    private void butMenuTypeSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butMenuTypeSetupActionPerformed
        showDialog("Menu Type Setup");
    }//GEN-LAST:event_butMenuTypeSetupActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try{
            if(isValidEntry()){
                dao.save(currMenu);
                clear();
                dao.open();
                initCombo();
                initTree();
            }
        }catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Menu Name", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        }catch(Exception ex){
            log.error(ex.toString());
        }finally{
          dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void cboParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboParentActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butMenuTypeSetup;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox cboParent;
    private javax.swing.JComboBox cboType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree treMenu;
    private javax.swing.JTextField txtClass;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtURL;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
