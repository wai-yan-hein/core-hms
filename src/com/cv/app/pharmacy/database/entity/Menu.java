package com.cv.app.pharmacy.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Application menu class.
 * Database table name is menu.
 */
@Entity
@Table(name="menu")
public class Menu  implements java.io.Serializable {
     private Integer menuId;
     private String menuName;
     private MenuType menuType;
     private Integer parentMenuId;
     private String menuClass;
     private String menuUrl;
    
    public Menu() {
    }

    public Menu(Integer menuId, String menuName, MenuType menuType) {
        this.menuId = menuId;
       this.menuName = menuName;
       this.menuType = menuType;
    }
   
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="menu_id", unique=true, nullable=false)
    public Integer getMenuId() {
        return this.menuId;
    }
    
    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }
    
    @Column(name="menu_name", nullable=false, length=50)
    public String getMenuName() {
        return this.menuName;
    }
    
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    
    @ManyToOne
    @JoinColumn(name="menu_type")
    public MenuType getMenuType() {
        return this.menuType;
    }
    
    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }

    @Column(name="menu_class", nullable=false, length=150)
    public String getMenuClass() {
        return menuClass;
    }

    public void setMenuClass(String menuClass) {
        this.menuClass = menuClass;
    }

    @Column(name="menu_url", nullable=false, length=150)
    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    @Column(name = "parent_menu_id")
    public Integer getParent() {
        return parentMenuId;
    }

    public void setParent(Integer parentMenuId) {
        this.parentMenuId = parentMenuId;
    }
    
    @Override
    public String toString(){
        return menuName;
    }
}


