/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cv.app.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.BestDataAccess;
import com.cv.app.pharmacy.database.entity.Menu;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class MenuNode {
    static Logger log = Logger.getLogger(MenuNode.class.getName());
    Menu menu;
    Object[] children;
    boolean allow = false;

    public void setAllow(Boolean allow){
        this.allow = allow;
    }

    public Boolean getAllow(){
        return this.allow;
    }

    public MenuNode(Menu menu) {
        AbstractDataAccess menuDao = new BestDataAccess();
        this.menu = menu;

        try{
            menuDao.open();
            List<Menu> menuList = menuDao.findAllHSQL("from Menu as mu where mu.parent =" + 
                    menu.getMenuId());
            menuDao.close();

            if(menuList != null){
                children = new MenuNode[menuList.size()];
                int i = 0;

                for(Menu menu1 : menuList){
                    children[i] = new MenuNode(menu1);
                    i++;
                }
            }
        }catch(Exception ex){
            log.error(ex.toString());
        }
    }

    @Override
    public String toString() {
        return menu.getMenuName();
    }

    public Menu getMenu() {
        return this.menu;
    }

    public Object[] getChildren() {
        /*if (children != null) {
            return children;
        }*/

        return children;
    }
}
