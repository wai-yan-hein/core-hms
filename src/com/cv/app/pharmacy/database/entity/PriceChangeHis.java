/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "price_change_his")
public class PriceChangeHis implements java.io.Serializable{
    private String priceChangeVouId;
    private Date priceChangeDate;
    private String remark;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private Integer session;
    private List<PriceChangeMedHis> listDetail;
    
    @ManyToOne
    @JoinColumn(name="created_by")
    public Appuser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Appuser createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date", insertable=false, updatable=false, 
            columnDefinition="timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value=GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="change_date")
    public Date getPriceChangeDate() {
        return priceChangeDate;
    }

    public void setPriceChangeDate(Date priceChangeDate) {
        this.priceChangeDate = priceChangeDate;
    }

    @Id
    @Column(name="price_change_id", unique=true, nullable=false, length=15)
    public String getPriceChangeVouId() {
        return priceChangeVouId;
    }

    public void setPriceChangeVouId(String priceChangeVouId) {
        this.priceChangeVouId = priceChangeVouId;
    }

    @Column(name="remark", length=25)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @ManyToOne
    @JoinColumn(name="updated_by")
    public Appuser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Appuser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "pc_his_join", joinColumns = { @JoinColumn(name = "price_change_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "pc_med_his_id") })
    @OrderBy("uniqueId")
    public List<PriceChangeMedHis> getListDetail(){
        return listDetail;
    }
    
    public void setListDetail(List<PriceChangeMedHis> listDetail){
        this.listDetail = listDetail;
    }
}
