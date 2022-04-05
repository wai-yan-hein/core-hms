/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="investigation_result")
public class InvestigationResult implements java.io.Serializable{
    private String keyField;
    private String regNo;
    private String liverSize;
    private String liverMargin;
    private String liverEchogenicity;
    private String sol;
    private String portalVeinDiameter;
    private String portalVeinThrombus;
    private String gbSize;
    private String gbContent;
    private String gbWail;
    private String cbd;
    private String intrahepaticDucts;
    private String pancreas;
    private String spleen;
    private String kidneyR;
    private String kidneyL;
    private String bladder;
    private String prostate;
    private String uterus;
    private String ovary;
    private String ascites;
    private String lymphNode;
    private String others;
    private String impression;

    @Id @Column(name="key_field", unique=true, nullable=false, length=30)
    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    @Column(name="reg_no", length=15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name="liver_size", length=15)
    public String getLiverSize() {
        return liverSize;
    }

    public void setLiverSize(String liverSize) {
        this.liverSize = liverSize;
    }

    @Column(name="liver_margin", length=15)
    public String getLiverMargin() {
        return liverMargin;
    }

    public void setLiverMargin(String liverMargin) {
        this.liverMargin = liverMargin;
    }

    @Column(name="liver_echogenicity", length=15)
    public String getLiverEchogenicity() {
        return liverEchogenicity;
    }

    public void setLiverEchogenicity(String liverEchogenicity) {
        this.liverEchogenicity = liverEchogenicity;
    }

    @Column(name="sol", length=255)
    public String getSol() {
        return sol;
    }

    public void setSol(String sol) {
        this.sol = sol;
    }

    @Column(name="portal_vein_diameter", length=100)
    public String getPortalVeinDiameter() {
        return portalVeinDiameter;
    }

    public void setPortalVeinDiameter(String portalVeinDiameter) {
        this.portalVeinDiameter = portalVeinDiameter;
    }

    @Column(name="portal_vein_thrombus", length=15)
    public String getPortalVeinThrombus() {
        return portalVeinThrombus;
    }

    public void setPortalVeinThrombus(String portalVeinThrombus) {
        this.portalVeinThrombus = portalVeinThrombus;
    }

    @Column(name="gb_size", length=100)
    public String getGbSize() {
        return gbSize;
    }

    public void setGbSize(String gbSize) {
        this.gbSize = gbSize;
    }

    @Column(name="gb_content", length=100)
    public String getGbContent() {
        return gbContent;
    }

    public void setGbContent(String gbContent) {
        this.gbContent = gbContent;
    }

    @Column(name="gb_wail", length=255)
    public String getGbWail() {
        return gbWail;
    }

    public void setGbWail(String gbWail) {
        this.gbWail = gbWail;
    }

    @Column(name="cbd", length=100)
    public String getCbd() {
        return cbd;
    }

    public void setCbd(String cbd) {
        this.cbd = cbd;
    }

    @Column(name="intrahepatic_ducts", length=100)
    public String getIntrahepaticDucts() {
        return intrahepaticDucts;
    }

    public void setIntrahepaticDucts(String intrahepaticDucts) {
        this.intrahepaticDucts = intrahepaticDucts;
    }

    @Column(name="pancreas", length=100)
    public String getPancreas() {
        return pancreas;
    }

    public void setPancreas(String pancreas) {
        this.pancreas = pancreas;
    }

    @Column(name="spleen", length=100)
    public String getSpleen() {
        return spleen;
    }

    public void setSpleen(String spleen) {
        this.spleen = spleen;
    }

    @Column(name="kidney_r", length=100)
    public String getKidneyR() {
        return kidneyR;
    }

    public void setKidneyR(String kidneyR) {
        this.kidneyR = kidneyR;
    }

    @Column(name="kidney_l", length=100)
    public String getKidneyL() {
        return kidneyL;
    }

    public void setKidneyL(String kidneyL) {
        this.kidneyL = kidneyL;
    }

    @Column(name="bladder", length=100)
    public String getBladder() {
        return bladder;
    }

    public void setBladder(String bladder) {
        this.bladder = bladder;
    }

    @Column(name="prostate", length=100)
    public String getProstate() {
        return prostate;
    }

    public void setProstate(String prostate) {
        this.prostate = prostate;
    }

    @Column(name="uterus", length=100)
    public String getUterus() {
        return uterus;
    }

    public void setUterus(String uterus) {
        this.uterus = uterus;
    }

    @Column(name="ovary", length=100)
    public String getOvary() {
        return ovary;
    }

    public void setOvary(String ovary) {
        this.ovary = ovary;
    }

    @Column(name="ascites", length=100)
    public String getAscites() {
        return ascites;
    }

    public void setAscites(String ascites) {
        this.ascites = ascites;
    }

    @Column(name="lymph_node", length=100)
    public String getLymphNode() {
        return lymphNode;
    }

    public void setLymphNode(String lymphNode) {
        this.lymphNode = lymphNode;
    }

    @Column(name="others", length=255)
    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    @Column(name="impression", length=500)
    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }
    
    
}
