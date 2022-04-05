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
@Table(name = "stock_op_his")
public class StockOpeningHis implements java.io.Serializable{
    private String stockOpHisId;
    private Date opDate;
    private Location stockLocation;
    private String remark;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private Integer session;
    private List<StockOpeningDetailHis> listDetail;
    private Long exrId;
    
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
    @Column(name="op_date")
    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
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
    @JoinColumn(name="location")
    public Location getStockLocation() {
        return stockLocation;
    }

    public void setStockLocation(Location stockLocation) {
        this.stockLocation = stockLocation;
    }

    @Id
    @Column(name="op_id", unique=true, nullable=false, length=15)
    public String getStockOpHisId() {
        return stockOpHisId;
    }

    public void setStockOpHisId(String stockOpHisId) {
        this.stockOpHisId = stockOpHisId;
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
    
    /*@OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "op_join", joinColumns = { @JoinColumn(name = "op_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "op_detail_id") })*/
    @Transient        
    public List<StockOpeningDetailHis> getListDetail(){
        return listDetail;
    }
    
    public void setListDetail(List<StockOpeningDetailHis> listDetail){
        this.listDetail = listDetail;
    }
    
    @Column(name="exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }
}
