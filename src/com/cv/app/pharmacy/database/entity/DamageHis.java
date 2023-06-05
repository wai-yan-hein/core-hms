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
@Table(name = "dmg_his")
public class DamageHis implements java.io.Serializable {

    private String dmgVouId;
    private Date dmgDate;
    private Location location;
    private String remark;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private Boolean deleted;
    private Integer session;
    private List<DamageDetailHis> listDetail;
    private Double totalAmount;
    private String migId;
    private String currencyId;
    private Long exrId;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dmg_date")
    public Date getDmgDate() {
        return dmgDate;
    }

    public void setDmgDate(Date dmgDate) {
        this.dmgDate = dmgDate;
    }

    @Id
    @Column(name = "dmg_id", unique = true, nullable = false, length = 15)
    public String getDmgVouId() {
        return dmgVouId;
    }

    public void setDmgVouId(String dmgVouId) {
        this.dmgVouId = dmgVouId;
    }

    @ManyToOne
    @JoinColumn(name = "location")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "remark", length = 25)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
    @JoinTable(name = "dmg_join", joinColumns = {
        @JoinColumn(name = "dmg_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "dmg_detail_id")})
    @OrderBy("uniqueId")*/
    @Transient
    public List<DamageDetailHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<DamageDetailHis> listDetail) {
        this.listDetail = listDetail;
    }

    @Column(name = "amount")
    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "mig_id", length = 25)
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    @Column(name = "currency_id", length = 15)
    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    @Column(name = "exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }
}
