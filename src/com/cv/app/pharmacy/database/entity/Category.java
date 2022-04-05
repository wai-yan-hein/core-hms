package com.cv.app.pharmacy.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Medicine category. Will be used in medicine setup and filtering. Database
 * table is category.
 */
@Entity
@Table(name = "category")
public class Category implements java.io.Serializable {

    private Integer catId;
    private String catName;
    private Integer migId;
    private Date updatedDate;
    
    public Category() {
    }

    public Category(String catName) {
        this.catName = catName;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cat_id", unique = true, nullable = false)
    public Integer getCatId() {
        return this.catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name = "cat_name", nullable = false, length = 500, unique = true)
    public String getCatName() {
        return this.catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Override
    public String toString() {
        return this.catName;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
