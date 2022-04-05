package com.cv.app.pharmacy.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ItemType used by medicine type. Database table name is item_type.
 */
@Entity
@Table(name = "item_type")
public class ItemType implements java.io.Serializable {

    private String itemTypeCode;
    private String itemTypeName;
    private String accountId;
    private String deptId;
    private Date updatedDate;
    
    public ItemType() {
    }

    public ItemType(String itemTypeCode, String itemTypeName) {
        this.itemTypeCode = itemTypeCode;
        this.itemTypeName = itemTypeName;
    }

    @Id
    @Column(name = "item_type_code", unique = true, nullable = false, length = 5)
    public String getItemTypeCode() {
        return this.itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    @Column(name = "item_type_name", nullable = false, length = 255, unique = true)
    public String getItemTypeName() {
        return this.itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    @Override
    public String toString() {
        return this.itemTypeName;
    }

    @Column(name = "account_id")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name="dept_id", length=15)
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
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
