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
@Table(name = "transfer_his")
public class TransferHis implements java.io.Serializable {

    private String tranVouId;
    private Date tranDate;
    private Location fromLocation;
    private Location toLocation;
    private String remark;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private Boolean deleted;
    private Integer session;
    private List<TransferDetailHis> listDetail;
    private Double vouTotal;
    private String migId;
    private String cusId;
    private String supId;

    @ManyToOne
    @JoinColumn(name = "from_location")
    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    @Column(name = "remark", length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne
    @JoinColumn(name = "to_location")
    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "tran_date")
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Id
    @Column(name = "transfer_id", unique = true, nullable = false, length = 15)
    public String getTranVouId() {
        return tranVouId;
    }

    public void setTranVouId(String tranVouId) {
        this.tranVouId = tranVouId;
    }

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

    @Column(name = "deleted")
    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @ManyToOne
    @JoinColumn(name = "updated_by")
    public Appuser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Appuser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /*@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tran_join", joinColumns = {
        @JoinColumn(name = "transfer_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "tran_detail_id")})*/
    @Transient
    public List<TransferDetailHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<TransferDetailHis> listDetail) {
        this.listDetail = listDetail;
    }

    @Column(name = "vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Column(name="mig_id", length=25)
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    @Column(name="cus_id", length=15)
    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    @Column(name="sup_id", length=15)
    public String getSupId() {
        return supId;
    }

    public void setSupId(String supId) {
        this.supId = supId;
    }
}
