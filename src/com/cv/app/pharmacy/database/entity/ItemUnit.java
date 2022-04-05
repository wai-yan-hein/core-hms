package com.cv.app.pharmacy.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ItemUnit used by medicine packing unit.
 * Database table name is item_unit.
 */
@Entity
@Table(name="item_unit")
public class ItemUnit  implements java.io.Serializable {


     private String itemUnitCode;
     private String itemUnitName;

    public ItemUnit() {
    }

    public ItemUnit(String itemUnitCode, String itemUnitName) {
       this.itemUnitCode = itemUnitCode;
       this.itemUnitName = itemUnitName;
    }
   
     @Id 
    
    @Column(name="item_unit_code", unique=true, nullable=false, length=10)
    public String getItemUnitCode() {
        return this.itemUnitCode;
    }
    
    public void setItemUnitCode(String itemUnitCode) {
        this.itemUnitCode = itemUnitCode;
    }
    
    @Column(name="item_unit_name", nullable=false, length=45, unique=true)
    public String getItemUnitName() {
        return this.itemUnitName;
    }
    
    public void setItemUnitName(String itemUnitName) {
        this.itemUnitName = itemUnitName;
    }

    @Override
    public String toString(){
        return this.itemUnitCode;
    }


}


