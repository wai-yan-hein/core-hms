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
import javax.persistence.Transient;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "stock_issue_his")
public class StockIssueHis implements java.io.Serializable{
    private String issueId;
    private Date issueDate;
    private Location location;
    private boolean deleted;
    private String remark;
    private Appuser createdBy;
    private Appuser updatedBy;
    private Date createdDate;
    private Date updatedDate;
    private List<StockIssueDetailHis> listDetail;
    private Double ttlAmt;
    private String intgUpdStatus;
    private String currencyId;
    private Long exrId;
    private Location toLocation;
    
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

    @Column(name="deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="issue_date", nullable=false)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Id
    @Column(name="issue_id", unique=true, nullable=false, length=15)
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    @ManyToOne
    @JoinColumn(name="location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne
    @JoinColumn(name="updated_by")
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

    /*@OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "stock_issue_join", joinColumns = { @JoinColumn(name = "issue_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "issue_detail_id") })*/
    @Transient
    public List<StockIssueDetailHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<StockIssueDetailHis> listDetail) {
        this.listDetail = listDetail;
    }

    @Column(name="ttl_amt")
    public Double getTtlAmt() {
        return ttlAmt;
    }

    public void setTtlAmt(Double ttlAmt) {
        this.ttlAmt = ttlAmt;
    }

    @Column(name="intg_upd_status", length=5)
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }

    @Column(name="currency_id", length=15)
    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
    
    @Column(name="exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }

    @ManyToOne
    @JoinColumn(name="to_location_id")
    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }
}
