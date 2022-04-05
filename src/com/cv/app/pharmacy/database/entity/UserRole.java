/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "user_role")
public class UserRole implements Serializable {

    private Integer roleId;
    private String roleName;
    private List<Privilege> privilege;
    private PaymentType paymentType;
    private VouStatus vouStatus;
    private Currency currency;
    private boolean vouPrinter;
    private Trader trader;
    private Date updatedDate;

    public UserRole() {
    }

    public UserRole(String roleName) {
        this.roleName = roleName;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "role_id", unique = true, nullable = false)
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Column(name = "role_name", unique = true, nullable = false, length = 10)
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "role_privilege", joinColumns = {
        @JoinColumn(name = "role_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "privilege_id")})
    public List<Privilege> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<Privilege> privilege) {
        this.privilege = privilege;
    }

    @ManyToOne
    @JoinColumn(name = "payment_type")
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @ManyToOne
    @JoinColumn(name = "vou_status")
    public VouStatus getVouStatus() {
        return vouStatus;
    }

    public void setVouStatus(VouStatus vouStatus) {
        this.vouStatus = vouStatus;
    }

    @ManyToOne
    @JoinColumn(name = "currency_id")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name = "vou_printer")
    public boolean isVouPrinter() {
        return vouPrinter;
    }

    public void setVouPrinter(boolean vouPrinter) {
        this.vouPrinter = vouPrinter;
    }

    @ManyToOne
    @JoinColumn(name = "cus_id")
    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    @Column(name="updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
