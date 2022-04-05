/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.common;

import com.cv.app.pharmacy.database.entity.Menu;
import com.cv.app.pharmacy.database.entity.MenuType;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class MenuSystemModel extends AbstractTreeTableModel implements TreeTableModel {
    // Names of the columns.
    static protected String[] cNames = {"Name", "Type", "Allow"};
    static protected MenuNode root = new MenuNode(new Menu(0,"BEST-System",new MenuType(1,"System")));

    // Types of the columns.
    static protected Class[] cTypes = {TreeTableModel.class, String.class, Boolean.class};
    static Logger log = Logger.getLogger(MenuSystemModel.class.getName());
    
    public MenuSystemModel(){
        super(root);
    }

    public MenuNode getTreeRoot(){
        return root;
    }
    
    protected Menu getMenu(Object node){
        MenuNode menuNode = (MenuNode)node;
        return menuNode.getMenu();
    }

    protected Object[] getChildren(Object node){
        //String str = node.getClass().getName();
        MenuNode menuNode = (MenuNode)node;
        return menuNode.getChildren();
    }

    @Override
    public int getChildCount(Object node){
        Object[] children = getChildren(node);
        return (children == null) ? 0 : children.length;
    }

    @Override
    public Object getChild(Object node, int i){
        return getChildren(node)[i];
    }

    @Override
    public int getColumnCount() {
        return cNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return cNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return cTypes[column];
    }

    @Override
    public Object getValueAt(Object node, int column) {
        Menu menu = getMenu(node);
        try {
            switch (column) {
                case 0:
                    return menu.getMenuName();
                case 1:
                    return menu.getMenuType().getTypeDesp();
                case 2:
                    return ((MenuNode)node).getAllow();
                /*case 3:
                    return new Date(file.lastModified());*/
            }
        } catch (SecurityException se) {
            log.error(se.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
        MenuNode menuNode = (MenuNode)node;
        menuNode.setAllow((Boolean)aValue);
    }
}

/*
class MenuNode {

        Menu menu;
        Object[] children;
        Boolean allow = false;

        public void setAllow(Boolean allow){
            this.allow = allow;
        }

        public Boolean getAllow(){
            return this.allow;
        }
        
        public MenuNode(Menu menu) {
            MenuDaoImpl menuDao = new MenuDaoImpl();
            this.menu = menu;

            try{
                List<Menu> menuList = menuDao.findMenu("select * from menu where parentmenuid =" +
                        menu.getMenuID());
                menuDao = null;
                
                if(menuList != null){
                    children = new MenuNode[menuList.size()];
                    int i = 0;

                    for(Menu menu1 : menuList){
                        children[i] = new MenuNode(menu1);
                        i++;
                    }
                }
            }catch(Exception ex){

            }
        }

        public String toString() {
            return menu.getMenuNameE();
        }

        public Menu getMenu() {
            return this.menu;
        }

        protected Object[] getChildren() {
            ///*if (children != null) {
            //    return children;
            //}

            //return children;
        }
    }*/