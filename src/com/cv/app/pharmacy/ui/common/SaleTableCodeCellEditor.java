/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.util.ItemAutoCompleter;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleTableCodeCellEditor extends AbstractCellEditor implements TableCellEditor {

    static Logger log = Logger.getLogger(SaleTableCodeCellEditor.class.getName());
    private JComponent component = null;
    private final AbstractDataAccess dao;
    private ItemAutoCompleter completer;
    private String cusGroup = null;
    private final String strCodeFilter = Util1.getPropValue("system.item.location.filter");
    private Integer locationId;
    //private List<Medicine> listItem = new ArrayList();
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            JTextField jtf = (JTextField)evt.getSource();
            jtf.setCaretPosition(jtf.getText().length());
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            //stopCellEditing();
        }
    };
    
    public SaleTableCodeCellEditor(final AbstractDataAccess dao) {
        this.dao = dao;
        //if (Global.listItem == null) {
        //    log.info("Global.listItem is null.");
            getItem();
        //}
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        //List<Medicine> listItem = dao.findAll("Medicine", "active = true");
        log.info("Code Editor Start.");
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();

                if ((keyEvent.isControlDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyEvent.isShiftDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyCode == KeyEvent.VK_F5)
                        || (keyCode == KeyEvent.VK_F7)
                        || (keyCode == KeyEvent.VK_F9)
                        || (keyCode == KeyEvent.VK_F10)
                        || (keyCode == KeyEvent.VK_ESCAPE)) {
                    stopCellEditing();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }

            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }
        };
        log.info("Code Editor Before Listener.");
        jtf.addKeyListener(keyListener);
        jtf.addFocusListener(fa);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        log.info("Code Editor Before listItem.");
        List listItem;
        String strBrandFilter = Util1.getPropValue("system.pharmacy.bran.filter");
        String strShowAllGroup = Util1.getPropValue("system.pharmacy.no.filter.group");
        /*if (cusGroup != null && strBrandFilter.equals("Y")) {
            if (cusGroup.equals(strShowAllGroup) || strShowAllGroup.equals("-")) {
                listItem = Global.listItem;
            } else {
                String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.Medicine"
                        + " WHERE brand.cusGroup = '" + cusGroup + "'";
                listItem = JoSQLUtil.getResult(strSql, Global.listItem);
            }
        } else {
            listItem = Global.listItem;
        }*/
        
        if (cusGroup != null && strBrandFilter.equals("Y")) {
            if (cusGroup.equals(strShowAllGroup) || strShowAllGroup.equals("-")) {
                listItem = Global.listItem;
            } else {
                /*String strSql = "SELECT * FROM com.cv.app.pharmacy.database.view.VMedicine1"
                        + " WHERE cusGroup = '" + cusGroup + "'";
                listItem = JoSQLUtil.getResult(strSql, Global.listItem);*/
                String strSql;
                String baseGroup = Util1.getPropValue("system.cus.base.group");
                if (cusGroup.equals(baseGroup)) {
                    strSql = "select o from VMedicine1 o where o.medId in (select a.key.itemId from VLocationItemMapping a"
                            + " where a.mapStatus = true and a.key.locationId = " + locationId.toString() + ") "
                            + "order by o.medName";
                } else {
                    strSql = "select o from VMedicine1 o where o.cusGroup = '"
                            + cusGroup + "' and o.medId in (select a.key.itemId from VLocationItemMapping a"
                            + " where a.mapStatus = true and a.key.locationId = " + locationId.toString() + ") "
                            + "order by o.medName";
                }
                listItem = dao.findAllHSQL(strSql);
            }
        } else {
            getItem();
            listItem = Global.listItem;
        }
        
        log.info("Code Editor Before ItemAutoCompleter.");
        completer = new ItemAutoCompleter(jtf, listItem, this);
        log.info("Editor List : " + listItem.size());
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        VMedicine1 med = completer.getSelMed();

        if (med != null) {
            obj = med.getMedId();
        } else {
            obj = ((JTextField) component).getText();
        }

        return obj;
    }

    /*
     * To prevent mouse click cell editing and 
     * function key press
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return false;
        } else if (anEvent instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) anEvent;

            if (ke.isActionKey()) {//Function key
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public String getCusGroup() {
        return cusGroup;
    }

    public void setCusGroup(String cusGroup) {
        this.cusGroup = cusGroup;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
        if (strCodeFilter.equals("Y")){
            getItem();
        }
    }

    private void getItem() {
        try {
            log.info("start : " + new Date());
            /*if (dao.getRowCount("select count(*) from item_type_mapping where group_id =" + Global.loginUser.getUserRole().getRoleId()) > 0) {
                Global.listItem = dao.findAll("Medicine", "active = true and medTypeId.itemTypeCode in (select a.key.itemType.itemTypeCode from ItemTypeMapping a)");
            } else {
                if (strCodeFilter.equals("Y")) {
                    Global.listItem = dao.findAllHSQL(
                            "select o from Medicine o where o.medId in (select a.key.itemId from "
                            + "LocationItemMapping a where a.key.locationId = "
                            + locationId.toString() + ") order by o.medId, o.medName");
                } else {
                    Global.listItem = dao.findAll("Medicine", "active = true");
                }
            }*/
            if (dao.getRowCount("select count(*) from item_type_mapping where group_id =" + Global.loginUser.getUserRole().getRoleId()) > 0) {
                Global.listItem = dao.findAllHSQL(
                        "select o from VMedicine1 o where o.active = true and o.medTypeId in "
                                + "(select a.key.itemType.itemTypeCode from ItemTypeMapping a"
                                + " where a.key.groupId = " + Global.loginUser.getUserRole().getRoleId() 
                                + ") order by o.medName");
            } else {
                if (strCodeFilter.equals("Y")) {
                    Global.listItem = dao.findAllHSQL(
                            "select o from VMedicine1 o where o.medId in (select a.key.itemId from "
                            + "LocationItemMapping a where a.key.locationId = "
                            + locationId.toString() + ") order by o.medName");
                } else {
                    Global.listItem = dao.findAllHSQL("select o from VMedicine1 o where active = true order by o.medName");
                }
            }
            log.info("end : " + new Date());
        } catch (Exception ex) {
            log.error("SaleTableCodeCellEditor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }finally{
            dao.close();
        }
    }

}
