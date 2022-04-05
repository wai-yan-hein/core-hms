/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.MachineInfo;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.VouStatus;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "tmp_vou_filter")
public class VouFilter implements java.io.Serializable {

    private VouFilterKey key;
    private Date fromDate;
    private Date toDate;
    private Trader trader;
    private Location location;
    private Location toLocation;
    private Session session;
    private PaymentType paymentType;
    private String vouNo;
    private CustomerGroup cusGroup;
    private VouStatus vouStatus;
    private String remark;
    private Currency currency;
    private Patient patient;
    private City city;
    private MachineInfo machine;
    private Appuser appUser;
    private String ptName;
    private String itemCode;
    private String itemDesp;
    
    public VouFilter() {
        key = new VouFilterKey();
    }

    @EmbeddedId
    public VouFilterKey getKey() {
        return key;
    }

    public void setKey(VouFilterKey key) {
        this.key = key;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "from_date")
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date")
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @ManyToOne
    @JoinColumn(name = "cus_id")
    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    @ManyToOne
    @JoinColumn(name = "location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @ManyToOne
    @JoinColumn(name = "session_id")
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @ManyToOne
    @JoinColumn(name = "payment_id")
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Column(name = "vou_no")
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @ManyToOne
    @JoinColumn(name = "cus_group")
    public CustomerGroup getCusGroup() {
        return cusGroup;
    }

    public void setCusGroup(CustomerGroup cusGroup) {
        this.cusGroup = cusGroup;
    }

    @ManyToOne
    @JoinColumn(name = "vou_status")
    public VouStatus getVouStatus() {
        return vouStatus;
    }

    public void setVouStatus(VouStatus vouStatus) {
        this.vouStatus = vouStatus;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne
    @JoinColumn(name = "currency_id")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @ManyToOne
    @JoinColumn(name = "to_location")
    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    @ManyToOne
    @JoinColumn(name = "reg_no")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @ManyToOne
    @JoinColumn(name = "city_id")
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @ManyToOne
    @JoinColumn(name="machine_id")
    public MachineInfo getMachine() {
        return machine;
    }

    public void setMachine(MachineInfo machine) {
        this.machine = machine;
    }

    @ManyToOne
    @JoinColumn(name="app_user")
    public Appuser getAppUser() {
        return appUser;
    }

    public void setAppUser(Appuser appUser) {
        this.appUser = appUser;
    }

    @Column(name="pt_name", length=250)
    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    @Column(name="item_code", length=15)
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    @Column(name="item_desp", length=100)
    public String getItemDesp() {
        return itemDesp;
    }

    public void setItemDesp(String itemDesp) {
        this.itemDesp = itemDesp;
    }
}
