/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import com.cv.app.util.Util1;

/**
 *
 * @author winswe
 */
public class CusGroupGenStatus {
    private String groupId;
    private String groupName;
    private boolean generate;
    private String traderId;
    
    public CusGroupGenStatus(String groupId, String groupName, Boolean generate,
            String traderId){
        this.groupId = groupId;
        this.groupName = groupName;
        this.generate = Util1.getNullTo(generate);
        this.traderId = traderId;
    }
    
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }
}
