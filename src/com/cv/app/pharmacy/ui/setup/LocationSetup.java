/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.LocationType;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Date;
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

/**
 *
 * @author WSwe
 */
public class LocationSetup extends javax.swing.JPanel implements TreeSelectionListener {
    
    static Logger log = Logger.getLogger(LocationSetup.class.getName());
    private Location currLocation = new Location();
    private final AbstractDataAccess dao = Global.dao;
    private DefaultMutableTreeNode treeRoot;
    private BestAppFocusTraversalPolicy focusPolicy;

    /**
     * Creates new form LocationSetup
     */
    public LocationSetup() {
        initComponents();
        
        try {
            dao.open();
            initCombo();
            initTree();
            dao.close();
        } catch (Exception ex) {
            log.error("LocationSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        
        treLocation.addTreeSelectionListener(this);
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }
    
    public void setCurrLocation(Location currLocation) {
        try {
            this.currLocation = currLocation;
            txtLocationName.setText(currLocation.getLocationName());
            chkCalcStock.setSelected(currLocation.isCalcStock());
            txtDeptCode.setText(currLocation.getAccDeptCode());
            txtAccCode.setText(currLocation.getAccountCode());
            txtPurAccCode.setText(currLocation.getPurAccountCode());
            txtPurDeptCode.setText(currLocation.getPurAccDeptCode());
            txtRetInAccCode.setText(currLocation.getRetInAccDeptCode());
            txtRetInDeptCode.setText(currLocation.getRetInAccDeptCode());
            txtRetOutAccCode.setText(currLocation.getRetOutAccDeptCode());
            txtRetOutDeptCode.setText(currLocation.getRetOutAccDeptCode());
            txtDmgAccCode.setText(currLocation.getDmgAccountCode());
            txtDmgDeptCode.setText(currLocation.getDmgAccDeptCode());
            txtAdjAccCode.setText(currLocation.getAdjAccountCode());
            txtAdjDeptCode.setText(currLocation.getAdjAccDeptCode());
            txtIssueAccCode.setText(currLocation.getIssuAccountCode());
            txtIssueDeptCode.setText(currLocation.getIssuAccDeptCode());
            
            txtSaleDiscAcc.setText(currLocation.getSaleDiscAcc());
            txtSalePaidAcc.setText(currLocation.getSalePaidAcc());
            txtSaleTaxAcc.setText(currLocation.getSaleTaxAcc());
            txtSaleBalAcc.setText(currLocation.getSaleBalAcc());
            txtSaleIPDAcc.setText(currLocation.getSaleIPDAcc());
            txtSaleIPDDept.setText(currLocation.getSaleIPDDept());
            
            txtRetInPaidAcc.setText(currLocation.getRetinPaidAcc());
            txtRetInBalAcc.setText(currLocation.getRetinBalAcc());
            txtRetInIPDAcc.setText(currLocation.getRetinIPDAcc());
            txtRetInIPDDept.setText(currLocation.getRetinIPDDept());
            
            txtPurDiscAcc.setText(currLocation.getPurDiscAcc());
            txtPurPaidAcc.setText(currLocation.getPurPaidAcc());
            txtPurTaxAcc.setText(currLocation.getPurTaxAcc());
            txtPurBalAcc.setText(currLocation.getPurBalAcc());
            
            txtRetOutPaidAcc.setText(currLocation.getRetoutPaidAcc());
            txtRetOutBalAcc.setText(currLocation.getRetoutBalAcc());
            
            lblStatus.setText("EDIT");
            if (currLocation.getParent() > 0) {
                dao.open();
                Location loc = (Location) dao.find(Location.class, currLocation.getParent());
                cboParent.setSelectedItem(loc);
                LocationType lt = (LocationType) dao.find(LocationType.class, currLocation.getLocationType());
                cboLocationType.setSelectedItem(lt);
                //initCombo();
                dao.close();
            }
        } catch (Exception ex) {
            cboParent.setSelectedItem(null);
            log.error("setCurrLocation : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }
    
    private void clear() {
        lblStatus.setText("NEW");
        currLocation = new Location();
        txtLocationName.setText(null);
        txtAccCode.setText(null);
        txtDeptCode.setText(null);
        txtPurAccCode.setText(null);
        txtPurDeptCode.setText(null);
        txtRetInAccCode.setText(null);
        txtRetInDeptCode.setText(null);
        txtRetOutAccCode.setText(null);
        txtRetOutDeptCode.setText(null);
        txtDmgAccCode.setText(null);
        txtDmgDeptCode.setText(null);
        txtAdjAccCode.setText(null);
        txtAdjDeptCode.setText(null);
        txtIssueAccCode.setText(null);
        txtIssueDeptCode.setText(null);
        
        txtSaleDiscAcc.setText(null);
        txtSalePaidAcc.setText(null);
        txtSaleTaxAcc.setText(null);
        txtSaleBalAcc.setText(null);
        txtSaleIPDAcc.setText(null);
        txtSaleIPDDept.setText(null);
        
        txtRetInPaidAcc.setText(null);
        txtRetInBalAcc.setText(null);
        txtRetInIPDAcc.setText(null);
        txtRetInIPDDept.setText(null);
        
        txtPurDiscAcc.setText(null);
        txtPurPaidAcc.setText(null);
        txtPurTaxAcc.setText(null);
        txtPurBalAcc.setText(null);
        
        txtRetOutPaidAcc.setText(null);
        txtRetOutBalAcc.setText(null);
        
        cboParent.setSelectedItem(null);
        cboLocationType.setSelectedItem(null);
        setFocus();
    }
    
    private void initCombo() {
        int currLocationId = 0;
        
        try {
            if (currLocation.getLocationId() != null) {
                currLocationId = currLocation.getLocationId();
            }
            
            BindingUtil.BindCombo(cboParent, dao.findAllHSQL("from Location as loc where loc.locationId <> "
                    + currLocationId));
            BindingUtil.BindCombo(cboLocationType, dao.findAllHSQL("from LocationType o order by o.description"));
            
            new ComBoBoxAutoComplete(cboParent);
            new ComBoBoxAutoComplete(cboLocationType);
            cboParent.setSelectedItem(null);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }
    
    private void initTree() {
        DefaultTreeModel treeModel = (DefaultTreeModel) treLocation.getModel();
        try {
            treeModel.setRoot(null);
        } catch (Exception ex) {
            log.error("initTree : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        
        treeRoot = null;
        treeRoot = new DefaultMutableTreeNode("CoreValue");
        createTreeNode(0, treeRoot);
        treeModel.setRoot(treeRoot);
    }
    
    private void createTreeNode(int parentLocID, DefaultMutableTreeNode treeRoot) {
        try {
            List<Location> listLocation = dao.findAllHSQL("from Location as loc where loc.parent =" + parentLocID);
            
            for (Location location : listLocation) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(location);
                treeRoot.add(child);
                createTreeNode(location.getLocationId(), child);
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
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        if ((selectedNode.isLeaf() && !selectedNode.isRoot())) {
            Location selLocation = (Location) selectedNode.getUserObject();
            try {
                setCurrLocation((Location) dao.find(Location.class, selLocation.getLocationId()));
            } catch (Exception ex) {
                log.error("valueChanged : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }
    
    private int getParentLocationId(Location location) {
        int id = 0;
        
        try {
            if (location != null) {
                id = location.getLocationId();
            }
        } catch (Exception ex) {
            log.error("getParentLocationId : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        
        return id;
    }
    
    public void setFocus() {
        txtLocationName.requestFocusInWindow();
    }
    
    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector(7);
        
        focusOrder.add(txtLocationName);
        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
    private void AddFocusMoveKey() {
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

    private boolean isValidEntry() {
        boolean status = true;
        
        if (Util1.nullToBlankStr(txtLocationName.getText()).equals("")) {
            status = false;
        } else {
            currLocation.setLocationName(txtLocationName.getText().trim());
            currLocation.setCalcStock(chkCalcStock.isSelected());
            currLocation.setAccDeptCode(txtDeptCode.getText().trim());
            currLocation.setAccountCode(txtAccCode.getText().trim());
            currLocation.setPurAccountCode(txtPurAccCode.getText().trim());
            currLocation.setPurAccDeptCode(txtPurDeptCode.getText().trim());
            currLocation.setRetInAccDeptCode(txtRetInAccCode.getText().trim());
            currLocation.setRetInAccDeptCode(txtRetInDeptCode.getText().trim());
            currLocation.setRetOutAccDeptCode(txtRetOutAccCode.getText().trim());
            currLocation.setRetOutAccDeptCode(txtRetOutDeptCode.getText().trim());
            currLocation.setDmgAccountCode(txtDmgAccCode.getText().trim());
            currLocation.setDmgAccDeptCode(txtDmgDeptCode.getText().trim());
            currLocation.setAdjAccountCode(txtAdjAccCode.getText().trim());
            currLocation.setAdjAccDeptCode(txtAdjDeptCode.getText().trim());
            currLocation.setIssuAccountCode(txtIssueAccCode.getText().trim());
            currLocation.setIssuAccDeptCode(txtIssueDeptCode.getText().trim());
            
            currLocation.setSaleDiscAcc(txtSaleDiscAcc.getText());
            currLocation.setSalePaidAcc(txtSalePaidAcc.getText());
            currLocation.setSaleTaxAcc(txtSaleTaxAcc.getText());
            currLocation.setSaleBalAcc(txtSaleBalAcc.getText());
            currLocation.setSaleIPDAcc(txtSaleIPDAcc.getText());
            currLocation.setSaleIPDDept(txtSaleIPDDept.getText());
            
            currLocation.setRetinPaidAcc(txtRetInPaidAcc.getText());
            currLocation.setRetinBalAcc(txtRetInBalAcc.getText());
            currLocation.setRetinIPDAcc(txtRetInIPDAcc.getText());
            currLocation.setRetinIPDDept(txtRetInIPDDept.getText());
            
            currLocation.setPurDiscAcc(txtPurDiscAcc.getText());
            currLocation.setPurPaidAcc(txtPurPaidAcc.getText());
            currLocation.setPurTaxAcc(txtPurTaxAcc.getText());
            currLocation.setPurBalAcc(txtPurBalAcc.getText());
            
            currLocation.setRetoutPaidAcc(txtRetOutPaidAcc.getText());
            currLocation.setRetoutBalAcc(txtRetOutBalAcc.getText());
            
            if (cboLocationType.getSelectedItem() != null) {
                currLocation.setLocationType(((LocationType) cboLocationType.getSelectedItem()).getTypeId());
            } else {
                currLocation.setLocationType(null);
            }
            
            if (currLocation.getLocationId() == null) {
                currLocation.setCreatedBy(Global.loginUser.getUserId());
                currLocation.setCreatedDate(new Date());
            } else {
                currLocation.setUpdatedBy(Global.loginUser.getUserId());
                currLocation.setUpdatedDate(new Date());
            }
            currLocation.setMachineId(Global.machineId);
        }
        
        return status;
    }
    
    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Location Delete", JOptionPane.YES_NO_OPTION);
                
                if (yes_no == 0) {
                    dao.delete(currLocation);
                    initCombo();
                    initTree();
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this location.",
                        "Location Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
        
        clear();
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
        treLocation = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        txtLocationName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboParent = new javax.swing.JComboBox();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        butDelete = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        chkCalcStock = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        cboLocationType = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDeptCode = new javax.swing.JTextField();
        txtAccCode = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtSaleDiscAcc = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtSalePaidAcc = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtSaleTaxAcc = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtSaleBalAcc = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtSaleIPDAcc = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtSaleIPDDept = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtRetInDeptCode = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtRetInAccCode = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtRetInPaidAcc = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtRetInBalAcc = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtRetInIPDAcc = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtRetInIPDDept = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtPurDeptCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtPurAccCode = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtPurDiscAcc = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtPurPaidAcc = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtPurTaxAcc = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txtPurBalAcc = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtRetOutDeptCode = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtRetOutAccCode = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtRetOutPaidAcc = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txtRetOutBalAcc = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtAdjDeptCode = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtAdjAccCode = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtDmgDeptCode = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtDmgAccCode = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtIssueDeptCode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtIssueAccCode = new javax.swing.JTextField();

        treLocation.setFont(Global.textFont);
        treLocation.setRowHeight(23);
        jScrollPane1.setViewportView(treLocation);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Name");

        txtLocationName.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Parent");

        cboParent.setFont(Global.textFont);

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        butDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Calc-Stock");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Type");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Sale"));

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Dept Code");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Acc Code");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Disc Acc");

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Paid Acc");

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Tax Acc");

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Bal Acc");

        jLabel23.setFont(Global.lableFont);
        jLabel23.setText("IPD Acc");

        jLabel24.setFont(Global.lableFont);
        jLabel24.setText("IPD Dept");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel19)
                        .addComponent(jLabel20)
                        .addComponent(jLabel21)
                        .addComponent(jLabel22)
                        .addComponent(jLabel23)
                        .addComponent(jLabel24)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAccCode)
                    .addComponent(txtDeptCode, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(txtSaleDiscAcc)
                    .addComponent(txtSalePaidAcc)
                    .addComponent(txtSaleTaxAcc)
                    .addComponent(txtSaleBalAcc)
                    .addComponent(txtSaleIPDAcc)
                    .addComponent(txtSaleIPDDept)))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel19, jLabel20, jLabel21, jLabel22, jLabel23, jLabel24, jLabel5, jLabel6});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtSaleDiscAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtSalePaidAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtSaleTaxAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtSaleBalAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtSaleIPDAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtSaleIPDDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Return In"));

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Dept Code");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Acc Code");

        jLabel25.setFont(Global.lableFont);
        jLabel25.setText("Paid Acc");

        jLabel26.setFont(Global.lableFont);
        jLabel26.setText("Bal Acc");

        jLabel27.setFont(Global.lableFont);
        jLabel27.setText("IPD Acc");

        jLabel28.setFont(Global.lableFont);
        jLabel28.setText("IPD Dept");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRetInDeptCode)
                    .addComponent(txtRetInAccCode)))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRetInIPDDept, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(txtRetInPaidAcc)
                    .addComponent(txtRetInBalAcc)
                    .addComponent(txtRetInIPDAcc)))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel25, jLabel26, jLabel27, jLabel28, jLabel9});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtRetInDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtRetInAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtRetInPaidAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtRetInBalAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(txtRetInIPDAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtRetInIPDDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Purchase"));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Dept Code");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Acc Code");

        jLabel29.setFont(Global.lableFont);
        jLabel29.setText("Disc Acc");

        jLabel30.setFont(Global.lableFont);
        jLabel30.setText("Paid Acc");

        jLabel31.setFont(Global.lableFont);
        jLabel31.setText("Tax Acc");

        jLabel32.setFont(Global.lableFont);
        jLabel32.setText("Bal Acc");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPurBalAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPurDeptCode))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPurAccCode)
                    .addComponent(txtPurDiscAcc)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPurPaidAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPurTaxAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 17, Short.MAX_VALUE))))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel29, jLabel30, jLabel31, jLabel32, jLabel7, jLabel8});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtPurDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txtPurAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(txtPurDiscAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(txtPurPaidAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(txtPurTaxAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(txtPurBalAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Return Out"));

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Dept Code ");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Acc Code");

        jLabel33.setFont(Global.lableFont);
        jLabel33.setText("Paid Acc");

        jLabel34.setFont(Global.lableFont);
        jLabel34.setText("Bal Acc");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRetOutDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRetOutAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRetOutPaidAcc)
                            .addComponent(txtRetOutBalAcc))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtRetOutDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtRetOutAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(txtRetOutPaidAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(txtRetOutBalAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock Adjust"));

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Dept Code");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Acc Code");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAdjDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAdjAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtAdjAccCode, txtAdjDeptCode});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(txtAdjDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtAdjAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Damage"));

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Dept Code");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Acc Code");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDmgAccCode))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDmgDeptCode))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtDmgDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtDmgAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock Issue"));

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Dept Code");

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Acc Code");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtIssueAccCode, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIssueDeptCode))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtIssueDeptCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtIssueAccCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel4, jPanel8});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLocationName)
                            .addComponent(cboParent, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(chkCalcStock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStatus)
                        .addGap(18, 18, 18)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(cboLocationType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtLocationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cboParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboLocationType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(butClear)
                                .addComponent(butSave)
                                .addComponent(butDelete)
                                .addComponent(lblStatus))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(chkCalcStock)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        currLocation.setParent(getParentLocationId((Location) cboParent.getSelectedItem()));
        
        try {
            if (isValidEntry()) {
                dao.execSql("insert into bk_location(location_id, location_name, parent, calc_stock, updated_date, location_type,\n"
                        + "  acc_dept_code, account_code, pur_dept_code, pur_account_code, retin_dept_code, retin_account_code,\n"
                        + "  retout_dept_code, retout_account_code, damage_dept_code, damage_account_code, adj_dept_code,\n"
                        + "  adj_account_code, issu_dept_code, issu_account_code, sale_disc_acc, sale_paid_acc, sale_tax_acc,\n"
                        + "  sale_bal_acc, sale_ipd_acc, sale_ipd_dept, retin_paid_acc, retin_bal_acc, retin_ipd_acc, \n"
                        + "  retin_ipd_dept, pur_disc_acc, pur_paid_acc, pur_tax_acc, pur_bal_acc, retout_paid_acc, retout_bal_acc,\n"
                        + "  trader_code, created_date, created_by, updated_by, machine_id)\n"
                        + "select location_id, location_name, parent, calc_stock, updated_date, location_type,\n"
                        + "  acc_dept_code, account_code, pur_dept_code, pur_account_code, retin_dept_code, retin_account_code,\n"
                        + "  retout_dept_code, retout_account_code, damage_dept_code, damage_account_code, adj_dept_code,\n"
                        + "  adj_account_code, issu_dept_code, issu_account_code, sale_disc_acc, sale_paid_acc, sale_tax_acc,\n"
                        + "  sale_bal_acc, sale_ipd_acc, sale_ipd_dept, retin_paid_acc, retin_bal_acc, retin_ipd_acc, \n"
                        + "  retin_ipd_dept, pur_disc_acc, pur_paid_acc, pur_tax_acc, pur_bal_acc, retout_paid_acc, retout_bal_acc,\n"
                        + "  trader_code, created_date, created_by, updated_by, machine_id\n"
                        + "from location where location_id = " + currLocation.getLocationId());
                dao.save(currLocation);
                clear();
                dao.open();
                initCombo();
                initTree();
                dao.close();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Name", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox<String> cboLocationType;
    private javax.swing.JComboBox cboParent;
    private javax.swing.JCheckBox chkCalcStock;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTree treLocation;
    private javax.swing.JTextField txtAccCode;
    private javax.swing.JTextField txtAdjAccCode;
    private javax.swing.JTextField txtAdjDeptCode;
    private javax.swing.JTextField txtDeptCode;
    private javax.swing.JTextField txtDmgAccCode;
    private javax.swing.JTextField txtDmgDeptCode;
    private javax.swing.JTextField txtIssueAccCode;
    private javax.swing.JTextField txtIssueDeptCode;
    private javax.swing.JTextField txtLocationName;
    private javax.swing.JTextField txtPurAccCode;
    private javax.swing.JTextField txtPurBalAcc;
    private javax.swing.JTextField txtPurDeptCode;
    private javax.swing.JTextField txtPurDiscAcc;
    private javax.swing.JTextField txtPurPaidAcc;
    private javax.swing.JTextField txtPurTaxAcc;
    private javax.swing.JTextField txtRetInAccCode;
    private javax.swing.JTextField txtRetInBalAcc;
    private javax.swing.JTextField txtRetInDeptCode;
    private javax.swing.JTextField txtRetInIPDAcc;
    private javax.swing.JTextField txtRetInIPDDept;
    private javax.swing.JTextField txtRetInPaidAcc;
    private javax.swing.JTextField txtRetOutAccCode;
    private javax.swing.JTextField txtRetOutBalAcc;
    private javax.swing.JTextField txtRetOutDeptCode;
    private javax.swing.JTextField txtRetOutPaidAcc;
    private javax.swing.JTextField txtSaleBalAcc;
    private javax.swing.JTextField txtSaleDiscAcc;
    private javax.swing.JTextField txtSaleIPDAcc;
    private javax.swing.JTextField txtSaleIPDDept;
    private javax.swing.JTextField txtSalePaidAcc;
    private javax.swing.JTextField txtSaleTaxAcc;
    // End of variables declaration//GEN-END:variables
}
