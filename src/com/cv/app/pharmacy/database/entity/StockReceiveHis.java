/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "stock_receive_his")
public class StockReceiveHis implements java.io.Serializable {

    private String receivedId;
    private Date receiveDate;
    private String remark;
    private boolean deleted;
    private Location location;
    private Appuser createdBy;
    private Appuser updatedBy;
    private Date createdDate;
    private Date updatedDate;
    private List<StockReceiveDetailHis> listDetail;
    private Long exrId;
    private String currencyId;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    public Appuser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Appuser createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "receive_date", nullable = false)
    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    @Id
    @Column(name = "receive_id", unique = true, nullable = false, length = 15)
    public String getReceivedId() {
        return receivedId;
    }

    public void setReceivedId(String receivedId) {
        this.receivedId = receivedId;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne
    @JoinColumn(name = "updated_by")
    public Appuser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Appuser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "updated_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name = "deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    /*@Transient
    public List<StockReceiveDetailHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<StockReceiveDetailHis> listDetail) {
        this.listDetail = listDetail;
    }*/

    @ManyToOne
    @JoinColumn(name = "location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    @Column(name="exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }
    
    @Column(name="currency_id", length=15)
    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
}
