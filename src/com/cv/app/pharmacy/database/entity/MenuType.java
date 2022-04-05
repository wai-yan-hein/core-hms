package com.cv.app.pharmacy.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MenuType is type of menu.
 * Database table name is menu_type.
 */
@Entity
@Table(name="menu_type")
public class MenuType  implements java.io.Serializable {
    
     private Integer typeId;
     private String typeDesp;

    public MenuType() {
    }

    public MenuType(Integer typeId, String typeDesp) {
        this.typeId = typeId;
       this.typeDesp = typeDesp;
    }
   
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="type_id", unique=true, nullable=false)
    public Integer getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    
    @Column(name="type_desp", nullable=false, length=45)
    public String getTypeDesp() {
        return this.typeDesp;
    }
    
    public void setTypeDesp(String typeDesp) {
        this.typeDesp = typeDesp;
    }

    @Override
    public String toString(){
        return typeDesp;
    }
}


