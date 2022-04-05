/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author admin
 */
@Entity
@Table(name = "opd_lab_pathology")
public class Pathologiest implements java.io.Serializable{
    private Integer pathoId;
    private String pathologyName;
    private String rank;
    private String post;
    private String dept;
    private String labSection;
    private boolean active;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="patho_id", nullable=false, unique=true)
    public Integer getPathoId() {
        return pathoId;
    }

    public void setPathoId(Integer pathoId) {
        this.pathoId = pathoId;
    }

    @Column(name="pathology_name", length=250)
    public String getPathologyName() {
        return pathologyName;
    }

    public void setPathologyName(String pathologyName) {
        this.pathologyName = pathologyName;
    }
    
    @Column(name="rank", length=500)
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Column(name="post", length=500)
    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Column(name="department", length=100)
    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    @Column(name="lab_section_desp", length=500)
    public String getLabSection() {
        return labSection;
    }
    
    public void setLabSection(String labSection) {
        this.labSection = labSection;
    }

    @Column(name="active")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
