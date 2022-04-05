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
 * @author winswe
 */
@Entity
@Table(name = "pur_his")
public class PurHis implements java.io.Serializable{
    private String purInvId;
    private Trader customerId;
    private Date purDate;
    private Date dueDate;
    private PaymentType paymentTypeId;
    private Location locationId;
    private Boolean deleted;
    private Double vouTotal;
    private Double paid;
    private Double discount;
    private Double balance;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private String remark;
    private List<PurDetailHis> purDetailHis;
    private String refNo;
    private VouStatus vouStatus;
    private List<PurchaseExpense> listExpense;
    private Double expenseTotal;
    private Integer session;
    private Currency currency;
    private Double discP;
    private Double taxP;
    private Double taxAmt;
    private boolean cashOut; //for purchase expense
    private Double exRateP; //for parent currency
    private List<PurchaseOutstand> listOuts;
    private String migId;
    private String intgUpdStatus;
    
    private String promoDesp;
    private Date promoStartDate;
    private Date promoEndDate;
    private Float promoGivePercent;
    private Float promoGetPercent;
    private Boolean promoGetComplete;
    private Long exrId;
    
    public PurHis(){}

    @Id 
    @Column(name="pur_inv_id", unique=true, nullable=false, length=15)
    public String getPurInvId() {
        return purInvId;
    }

    public void setPurInvId(String PurInvId) {
        this.purInvId = PurInvId;
    }

    @Column(name="balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

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
    
    @ManyToOne
    @JoinColumn(name="cus_id")
    public Trader getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Trader customerId) {
        this.customerId = customerId;
    }

    @Column(name="deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name="discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="due_date")
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @ManyToOne
    @JoinColumn(name="location")
    public Location getLocationId() {
        return locationId;
    }

    public void setLocationId(Location locationId) {
        this.locationId = locationId;
    }

    @Column(name="paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @ManyToOne
    @JoinColumn(name="payment_type")
    public PaymentType getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(PaymentType paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="pur_date", nullable=false)
    public Date getPurDate() {
        return purDate;
    }

    public void setPurDate(Date purDate) {
        this.purDate = purDate;
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

    @Column(name="vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    /*@OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "pur_join", joinColumns = { @JoinColumn(name = "pur_inv_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "pur_detail_id") })
    @OrderBy("uniqueId")*/
    @Transient
    public List<PurDetailHis> getPurDetailHis() {
        return purDetailHis;
    }

    public void setPurDetailHis(List<PurDetailHis> purDetailHis) {
        this.purDetailHis = purDetailHis;
    }

    @Column(name="ref_no")
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @ManyToOne
    @JoinColumn(name="vou_status")
    public VouStatus getVouStatus() {
        return vouStatus;
    }

    public void setVouStatus(VouStatus vouStatus) {
        this.vouStatus = vouStatus;
    }

    /*@OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "pur_expense_join", joinColumns = { @JoinColumn(name = "pur_inv_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "purchase_expense_id") })*/
    @Transient
    public List<PurchaseExpense> getListExpense() {
        return listExpense;
    }

    public void setListExpense(List<PurchaseExpense> listExpense) {
        this.listExpense = listExpense;
    }
    
    @Column(name="pur_exp_total")
    public Double getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(Double expenseTotal) {
        this.expenseTotal = expenseTotal;
    }
    
    @Column(name="session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @ManyToOne
    @JoinColumn(name="currency")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name="disc_p")
    public Double getDiscP() {
        return discP;
    }

    public void setDiscP(Double discP) {
        this.discP = discP;
    }

    @Column(name="tax_p")
    public Double getTaxP() {
        return taxP;
    }

    public void setTaxP(Double taxP) {
        this.taxP = taxP;
    }

    @Column(name="tax_amt")
    public Double getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(Double taxAmt) {
        this.taxAmt = taxAmt;
    }

    @Column(name="cash_out")
    public boolean isCashOut() {
        return cashOut;
    }

    public void setCashOut(boolean cashOut) {
        this.cashOut = cashOut;
    }

    @Column(name="exchange_rate_p")
    public Double getExRateP() {
        return exRateP;
    }

    public void setExRateP(Double exRateP) {
        this.exRateP = exRateP;
    }

    /*@OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "purchase_outs_join", joinColumns = { @JoinColumn(name = "pur_inv_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "outstanding_id") })*/
    @Transient
    public List<PurchaseOutstand> getListOuts() {
        return listOuts;
    }

    public void setListOuts(List<PurchaseOutstand> listOuts) {
        this.listOuts = listOuts;
    }
    
    @Column(name="mig_id", length=25)
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }
    
    @Column(name="promo_desp", length=500)
    public String getPromoDesp() {
        return promoDesp;
    }

    public void setPromoDesp(String promoDesp) {
        this.promoDesp = promoDesp;
    }

    @Column(name="promo_start_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoStartDate() {
        return promoStartDate;
    }

    public void setPromoStartDate(Date promoStartDate) {
        this.promoStartDate = promoStartDate;
    }

    @Column(name="promo_end_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoEndDate() {
        return promoEndDate;
    }

    public void setPromoEndDate(Date promoEndDate) {
        this.promoEndDate = promoEndDate;
    }

    @Column(name="promo_give_percent")
    public Float getPromoGivePercent() {
        return promoGivePercent;
    }

    public void setPromoGivePercent(Float promoGivePercent) {
        this.promoGivePercent = promoGivePercent;
    }

    @Column(name="promo_get_percent")
    public Float getPromoGetPercent() {
        return promoGetPercent;
    }

    public void setPromoGetPercent(Float promoGetPercent) {
        this.promoGetPercent = promoGetPercent;
    }

    @Column(name="promo_get_complete")
    public Boolean isPromoGetComplete() {
        return promoGetComplete;
    }

    public void setPromoGetComplete(Boolean promoGetComplete) {
        this.promoGetComplete = promoGetComplete;
    }

    @Column(name="intg_upd_status")
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }
    
    @Column(name="exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }
}
