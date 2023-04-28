/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import com.cv.app.pharmacy.database.entity.ChargeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "opd_details_his")
public class OPDDetailHis implements java.io.Serializable {

    private String opdDetailId;
    private Service service;
    private Integer quantity;
    private Double price;
    private Integer feesVersionId;
    private Integer uniqueId;
    private ChargeType chargeType;
    private Double amount;
    private boolean readerStatus;
    private Double fees1;
    private Double fees2;
    private Double fees3;
    private Double fees4;
    private Double fees5;
    private Double fees6;
    private boolean percent;
    private Doctor dr; //Reader Doctor
    private Doctor technician;
    private Integer pathoId;
    private Doctor referDr; //Refer Doctor
    private Boolean completeStatus = false;
    private Integer labMachineId;
    private String labRemark;
    private Boolean urgent;
    private Double fees;
    private String vouNo;
    private Boolean pkgItem;
    
    private String fee1Id;
    private String fee2Id;
    private String fee3Id;
    private String fee4Id;
    private String fee5Id;
    private String fee6Id;
    private Integer sortOrder;
    private Double serviceCost;
    
    @Id
    @Column(name = "opd_detail_id", unique = true, nullable = false, length=25)
    public String getOpdDetailId() {
        return opdDetailId;
    }

    public void setOpdDetailId(String opdDetailId) {
        this.opdDetailId = opdDetailId;
    }

    @ManyToOne
    @JoinColumn(name = "service_id")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Column(name = "qty")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(name = "price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "fees_version_id")
    public Integer getFeesVersionId() {
        return feesVersionId;
    }

    public void setFeesVersionId(Integer feesVersionId) {
        this.feesVersionId = feesVersionId;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @ManyToOne
    @JoinColumn(name = "charge_type")
    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    @Column(name = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name = "reader_status")
    public boolean isReaderStatus() {
        return readerStatus;
    }

    public void setReaderStatus(boolean readerStatus) {
        this.readerStatus = readerStatus;
    }

    @Column(name = "srv_fees1")
    public Double getFees1() {
        return fees1;
    }

    public void setFees1(Double fees1) {
        this.fees1 = fees1;
    }

    @Column(name = "srv_fees2")
    public Double getFees2() {
        return fees2;
    }

    public void setFees2(Double fees2) {
        this.fees2 = fees2;
    }

    @Column(name = "srv_fees3")
    public Double getFees3() {
        return fees3;
    }

    public void setFees3(Double fees3) {
        this.fees3 = fees3;
    }

    @Column(name = "srv_fees4")
    public Double getFees4() {
        return fees4;
    }

    public void setFees4(Double fees4) {
        this.fees4 = fees4;
    }

    @Column(name = "srv_fees5")
    public Double getFees5() {
        return fees5;
    }

    public void setFees5(Double fees5) {
        this.fees5 = fees5;
    }

    @Column(name="srv_fees6")
    public Double getFees6() {
        return fees6;
    }

    public void setFees6(Double fees6) {
        this.fees6 = fees6;
    }
    
    @Column(name = "is_percent")
    public boolean isPercent() {
        return percent;
    }

    public void setPercent(boolean percent) {
        this.percent = percent;
    }

    @ManyToOne
    @JoinColumn(name = "reader_doctor_id")
    public Doctor getDr() {
        return dr;
    }

    public void setDr(Doctor dr) {
        this.dr = dr;
    }

    @ManyToOne
    @JoinColumn(name = "tech_id")
    public Doctor getTechnician() {
        return technician;
    }

    public void setTechnician(Doctor technician) {
        this.technician = technician;
    }

    @Column(name="patho_id")
    public Integer getPathoId() {
        return pathoId;
    }

    public void setPathoId(Integer pathoId) {
        this.pathoId = pathoId;
    }

    @ManyToOne
    @JoinColumn(name = "refer_doctor_id")
    public Doctor getReferDr() {
        return referDr;
    }

    public void setReferDr(Doctor referDr) {
        this.referDr = referDr;
    }

    @Column(name="complete_status")
    public Boolean isCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(Boolean completeStatus) {
        this.completeStatus = completeStatus;
    }

    @Column(name="lab_machine_id")
    public Integer getLabMachineId() {
        return labMachineId;
    }

    public void setLabMachineId(Integer labMachineId) {
        this.labMachineId = labMachineId;
    }

    @Column(name="lab_remark", length=2500)
    public String getLabRemark() {
        return labRemark;
    }

    public void setLabRemark(String labRemark) {
        this.labRemark = labRemark;
    }
    
    @Column(name="urgent")
    public Boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    @Column(name="srv_fees")
    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="pkg_item")
    public Boolean getPkgItem() {
        if(pkgItem == null){
            return false;
        }
        return pkgItem;
    }

    public void setPkgItem(Boolean pkgItem) {
        this.pkgItem = pkgItem;
    }

    @Column(name="fee1_id", length=15)
    public String getFee1Id() {
        return fee1Id;
    }

    public void setFee1Id(String fee1Id) {
        this.fee1Id = fee1Id;
    }

    @Column(name="fee2_id", length=15)
    public String getFee2Id() {
        return fee2Id;
    }

    public void setFee2Id(String fee2Id) {
        this.fee2Id = fee2Id;
    }

    @Column(name="fee3_id", length=15)
    public String getFee3Id() {
        return fee3Id;
    }

    public void setFee3Id(String fee3Id) {
        this.fee3Id = fee3Id;
    }

    @Column(name="fee4_id", length=15)
    public String getFee4Id() {
        return fee4Id;
    }

    public void setFee4Id(String fee4Id) {
        this.fee4Id = fee4Id;
    }

    @Column(name="fee5_id", length=15)
    public String getFee5Id() {
        return fee5Id;
    }

    public void setFee5Id(String fee5Id) {
        this.fee5Id = fee5Id;
    }

    @Column(name="fee6_id", length=15)
    public String getFee6Id() {
        return fee6Id;
    }

    public void setFee6Id(String fee6Id) {
        this.fee6Id = fee6Id;
    }

    @Column(name="result_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Column(name="service_cost")
    public Double getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(Double serviceCost) {
        this.serviceCost = serviceCost;
    }
}
