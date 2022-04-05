/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
 *
 * PackingTemplate class is the used of pre-define medicine packing.
 * Database table name is packing_template.
 */
@Entity
@Table(name="packing_template")
public class PackingTemplate implements java.io.Serializable{
    private Integer templateId;
    private String relStr;
    private List<PackingTemplateDetail> lstPackingTemplateDetail;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="template_id", unique=true, nullable=false)
    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    @Column(name="rel_str", unique=true, nullable=false, length=50)
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }

    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "template_join", joinColumns = { @JoinColumn(name = "template_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "template_detail_id") })
    public List<PackingTemplateDetail> getLstPackingTemplateDetail() {
        return lstPackingTemplateDetail;
    }

    public void setLstPackingTemplateDetail(List<PackingTemplateDetail> lstPackingTemplateDetail) {
        this.lstPackingTemplateDetail = lstPackingTemplateDetail;
    }
}
