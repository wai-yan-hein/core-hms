/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "opd_category")
public class OPDCategory implements java.io.Serializable {

    private Integer catId;
    private String catName;
    private Integer groupId;
    private Integer migId;
    private String opdAccCode;
    private String ipdAccCode;
    private String depCode;
    private String srvF1AccId;
    private String srvF2AccId;
    private String srvF3AccId;
    private String srvF4AccId;
    private String srvF5AccId;
    private String payableAccId;
    private String payableAccOpt;
    private String srvF2RefDr;
    private String srvF3RefDr;
    private String ipdDeptCode;
    private String userCode;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cat_id", unique = true, nullable = false)
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name = "cat_name", unique = true, length = 50)
    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Column(name = "group_id")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Column(name = "mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }

    @Override
    public String toString() {
        return catName;
    }

    @Column(name = "dep_code", length = 15)
    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    @Column(name = "srvf1_acc_id", length = 15)
    public String getSrvF1AccId() {
        return srvF1AccId;
    }

    public void setSrvF1AccId(String srvF1AccId) {
        this.srvF1AccId = srvF1AccId;
    }

    @Column(name = "srvf2_acc_id", length = 15)
    public String getSrvF2AccId() {
        return srvF2AccId;
    }

    public void setSrvF2AccId(String srvF2AccId) {
        this.srvF2AccId = srvF2AccId;
    }

    @Column(name = "srvf3_acc_id", length = 15)
    public String getSrvF3AccId() {
        return srvF3AccId;
    }

    public void setSrvF3AccId(String srvF3AccId) {
        this.srvF3AccId = srvF3AccId;
    }

    @Column(name = "srvf4_acc_id", length = 15)
    public String getSrvF4AccId() {
        return srvF4AccId;
    }

    public void setSrvF4AccId(String srvF4AccId) {
        this.srvF4AccId = srvF4AccId;
    }

    @Column(name = "srvf5_acc_id", length = 15)
    public String getSrvF5AccId() {
        return srvF5AccId;
    }

    public void setSrvF5AccId(String srvF5AccId) {
        this.srvF5AccId = srvF5AccId;
    }

    @Column(name = "opd_acc_code", length = 15)
    public String getOpdAccCode() {
        return opdAccCode;
    }

    public void setOpdAccCode(String opdAccCode) {
        this.opdAccCode = opdAccCode;
    }

    @Column(name = "ipd_acc_code", length = 15)
    public String getIpdAccCode() {
        return ipdAccCode;
    }

    public void setIpdAccCode(String ipdAccCode) {
        this.ipdAccCode = ipdAccCode;
    }
    
    @Column(name="payable_acc_id", length=25)
    public String getPayableAccId() {
        return payableAccId;
    }

    public void setPayableAccId(String payableAccId) {
        this.payableAccId = payableAccId;
    }

    @Column(name="payable_acc_opt", length=20)
    public String getPayableAccOpt() {
        return payableAccOpt;
    }

    public void setPayableAccOpt(String payableAccOpt) {
        this.payableAccOpt = payableAccOpt;
    }

    @Column(name="crvf2_ref_dr", length=20)
    public String getSrvF2RefDr() {
        return srvF2RefDr;
    }

    public void setSrvF2RefDr(String srvF2RefDr) {
        this.srvF2RefDr = srvF2RefDr;
    }

    @Column(name="crvf3_ref_dr", length=20)
    public String getSrvF3RefDr() {
        return srvF3RefDr;
    }

    public void setSrvF3RefDr(String srvF3RefDr) {
        this.srvF3RefDr = srvF3RefDr;
    }

    @Column(name="ipd_dept_code", length=15)
    public String getIpdDeptCode() {
        return ipdDeptCode;
    }

    public void setIpdDeptCode(String ipdDeptCode) {
        this.ipdDeptCode = ipdDeptCode;
    }

    @Column(name="user_code", length=15)
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
