/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import com.cv.app.pharmacy.database.entity.ChargeType;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Eitar
 */
@Entity
@Table(name = "dc_details_his")
public class DCDetailHis implements java.io.Serializable {

    private String opdDetailId;
    private InpService service;
    private Integer quantity;
    private Double price;
    private Integer feesVersionId;
    private Integer uniqueId;
    private ChargeType chargeType;
    private Double amount;
    private boolean readerStatus;
    private Double srvFee1;
    private Double srvFee2;
    private Double srvFee3;
    private Double srvFee4;
    private Double srvFee5;
    private List<DCDoctorFee> listDCDF;
    private String vouNo;
    private String payId1;
    private String payId2;
    private String payId3;
    private String payId4;
    private String payId5;
    private Boolean needDoctor = false;

    @Id
    @Column(name = "dc_detail_id", unique = true, nullable = false, length = 25)
    public String getOpdDetailId() {
        return opdDetailId;
    }

    public void setOpdDetailId(String opdDetailId) {
        this.opdDetailId = opdDetailId;
    }

    @ManyToOne
    @JoinColumn(name = "service_id")
    public InpService getService() {
        return service;
    }

    public void setService(InpService service) {
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

    @Column(name = "srv_fee1")
    public Double getSrvFee1() {
        return srvFee1;
    }

    public void setSrvFee1(Double srvFee1) {
        this.srvFee1 = srvFee1;
    }

    @Column(name = "srv_fee2")
    public Double getSrvFee2() {
        return srvFee2;
    }

    public void setSrvFee2(Double srvFee2) {
        this.srvFee2 = srvFee2;
    }

    @Column(name = "srv_fee3")
    public Double getSrvFee3() {
        return srvFee3;
    }

    public void setSrvFee3(Double srvFee3) {
        this.srvFee3 = srvFee3;
    }

    @Column(name = "srv_fee4")
    public Double getSrvFee4() {
        return srvFee4;
    }

    public void setSrvFee4(Double srvFee4) {
        this.srvFee4 = srvFee4;
    }

    @Column(name = "srv_fee5")
    public Double getSrvFee5() {
        return srvFee5;
    }

    public void setSrvFee5(Double srvFee5) {
        this.srvFee5 = srvFee5;
    }

    /*@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinTable(name = "dc_dr_fee_join", joinColumns = {
        @JoinColumn(name = "dc_detail_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "dr_fee_id")})*/
    @Transient
    public List<DCDoctorFee> getListDCDF() {
        return listDCDF;
    }

    public void setListDCDF(List<DCDoctorFee> listDCDF) {
        this.listDCDF = listDCDF;
    }

    @Column(name = "vou_no", length = 25)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name = "pay_id1", length = 15)
    public String getPayId1() {
        return payId1;
    }

    public void setPayId1(String payId1) {
        this.payId1 = payId1;
    }

    @Column(name = "pay_id2", length = 15)
    public String getPayId2() {
        return payId2;
    }

    public void setPayId2(String payId2) {
        this.payId2 = payId2;
    }

    @Column(name = "pay_id3", length = 15)
    public String getPayId3() {
        return payId3;
    }

    public void setPayId3(String payId3) {
        this.payId3 = payId3;
    }

    @Column(name = "pay_id4", length = 15)
    public String getPayId4() {
        return payId4;
    }

    public void setPayId4(String payId4) {
        this.payId4 = payId4;
    }

    @Column(name = "pay_id5", length = 15)
    public String getPayId5() {
        return payId5;
    }

    public void setPayId5(String payId5) {
        this.payId5 = payId5;
    }

    @Column(name="need_doctor")
    public Boolean isNeedDoctor() {
        return needDoctor;
    }

    public void setNeedDoctor(Boolean needDoctor) {
        if (needDoctor == null) {
            this.needDoctor = false;
        } else {
            this.needDoctor = needDoctor;
        }
    }
}
