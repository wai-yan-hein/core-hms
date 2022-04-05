/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "privilege")
public class Privilege implements Serializable{
    private Integer privilegeId;
    private Integer menuId;
    private Boolean allow;
    
    public Privilege(){}
    
    public Privilege(Integer menuId, Boolean allow){
        this.menuId = menuId;
        this.allow = allow;
    }

    @Column(name="allow")
    public Boolean getAllow() {
        return allow;
    }

    public void setAllow(Boolean allow) {
        this.allow = allow;
    }

    @Column(name="menu_id")
    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="privilege_id", unique=true, nullable=false)
    public Integer getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Integer privilegeId) {
        this.privilegeId = privilegeId;
    }
    
    
}
