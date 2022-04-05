/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MenuTypeSetup.java
 *
 * Created on Apr 22, 2012, 9:53:11 PM
 */
package com.cv.app.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.MenuType;
import com.cv.app.util.Util1;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author winswe
 */
public class MenuTypeSetup extends javax.swing.JPanel {
    static Logger log = Logger.getLogger(MenuTypeSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<MenuType> listMenuType = ObservableCollections.
            observableList(new ArrayList<MenuType>());
    /** Creates new form MenuTypeSetup */
    public MenuTypeSetup() {
        initComponents();
        clear();
        
        try{
            dao.open();
            initTable();
            dao.close();
        }catch(Exception ex){
            log.error(ex.toString());
        }
    }

    private MenuType currMenuType;

    /**
     * Get the value of currMenuType
     *
     * @return the value of currMenuType
     */
    public MenuType getCurrMenuType() {
        return currMenuType;
    }

    /**
     * Set the value of currMenuType
     *
     * @param currMenuType new value of currMenuType
     */
    public void setCurrMenuType(MenuType currMenuType) {
        this.currMenuType = currMenuType;
        
        propertyChangeSupport.firePropertyChange("currMenuType", 0, 1);
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
        setCurrMenuType(new MenuType());
    }
    
    /*
     * Initialize tblCategory
     */
    private void initTable(){
        //Get MenuType from database.
        listMenuType = ObservableCollections.observableList(dao.findAll("MenuType"));
        
        //Binding table with listCategory using beansbinding library.
        JTableBinding jTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE, 
                listMenuType, tblMenuType);
        JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.
                jdesktop.beansbinding.ELProperty.create("${typeDesp}"));
        columnBinding.setColumnName("Type Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        jTableBinding.bind();
        
        //Define table selection model to single row selection.
        tblMenuType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblMenuType.getSelectionModel().addListSelectionListener(
                new ListSelectionListener(){
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int row = tblMenuType.getSelectedRow();
                    
                    if(row != -1)
                        setCurrMenuType(listMenuType.get(tblMenuType.convertRowIndexToModel(row)));
                }
            }
         );
    }
    
    private boolean isValidEntry(){
        boolean status = true;
        
        if(Util1.nullToBlankStr(currMenuType.getTypeDesp()).equals(""))
            status = false;
        else
            currMenuType.setTypeDesp(currMenuType.getTypeDesp().trim());
        
        return status;
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
        tblMenuType = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        tblMenuType.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        tblMenuType.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblMenuType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Name");

        txtName.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currMenuType.typeDesp}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butClear))
                    .addComponent(txtName))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try{
            if(isValidEntry()){
                dao.save(currMenuType);
                clear();
                dao.open();
                initTable();
                dao.close();
            }
        }catch(Exception ex){
            log.error(ex.toString());
        }
    }//GEN-LAST:event_butSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMenuType;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
