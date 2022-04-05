/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.healper;

/**
 *
 * @author winswe
 */
public class PackageDetailEdit {
    
    private String itemOption;
    private String itemId;
    private String itemName;
    private Float ttlUseQty;
    private Double ttlAmount;
    private Float ttlAllowQty;
    private Float overUseQty;
    private String strStatus;
    private Boolean exclude;
    private Boolean include;
    private String itemKey;
    
    public PackageDetailEdit(String itemOption, String itemId, String itemName,
            Float ttlUseQty, Double ttlAmount, Float ttlAllowQty, Float overUseQty,
            String itemKey){
        this.itemOption = itemOption;
        this.itemId = itemId;
        this.itemName = itemName;
        this.ttlUseQty = ttlUseQty;
        this.ttlAmount = ttlAmount;
        this.ttlAllowQty = ttlAllowQty;
        this.overUseQty = overUseQty;
        this.itemKey = itemKey;
    }
    
    public String getItemOption() {
        return itemOption;
    }

    public void setItemOption(String itemOption) {
        this.itemOption = itemOption;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Float getTtlUseQty() {
        return ttlUseQty;
    }

    public void setTtlUseQty(Float ttlUseQty) {
        this.ttlUseQty = ttlUseQty;
    }

    public Double getTtlAmount() {
        return ttlAmount;
    }

    public void setTtlAmount(Double ttlAmount) {
        this.ttlAmount = ttlAmount;
    }

    public Float getTtlAllowQty() {
        return ttlAllowQty;
    }

    public void setTtlAllowQty(Float ttlAllowQty) {
        this.ttlAllowQty = ttlAllowQty;
    }

    public Float getOverUseQty() {
        return overUseQty;
    }

    public void setOverUseQty(Float overUseQty) {
        this.overUseQty = overUseQty;
    }

    public String getStrStatus() {
        if(ttlAllowQty > 0 && overUseQty > 0){
            strStatus = "Over Use";
        }else if(ttlAllowQty > 0){
            strStatus = "Package Item";
        }else{
            strStatus = "Extra Usage";
        }
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }

    public Boolean getInclude() {
        return include;
    }

    public void setInclude(Boolean include) {
        this.include = include;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
}
