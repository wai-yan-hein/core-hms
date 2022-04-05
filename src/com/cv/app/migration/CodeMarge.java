/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.migration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "code_marge")
public class CodeMarge implements java.io.Serializable {
    
    private Integer id;
    private String itemCode;
    private String itemName;
    private String relStr;
    private String oraId;
    private String newItemCode;
    private Boolean itemStatus;
    private String source;

    @Id
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="item_code", length=15)
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    @Column(name="item_name", length=255)
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Column(name="rel_str", length=255)
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }

    @Column(name="ora_id", length=15)
    public String getOraId() {
        return oraId;
    }

    public void setOraId(String oraId) {
        this.oraId = oraId;
    }

    @Column(name="new_item_code", length=15)
    public String getNewItemCode() {
        return newItemCode;
    }

    public void setNewItemCode(String newItemCode) {
        this.newItemCode = newItemCode;
    }

    @Column(name="item_status")
    public Boolean getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(Boolean itemStatus) {
        this.itemStatus = itemStatus;
    }

    @Column(name="source", length=15)
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
}
