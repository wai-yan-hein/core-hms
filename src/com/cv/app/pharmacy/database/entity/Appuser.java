package com.cv.app.pharmacy.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Application user class. Using for login and user right. Database table name
 * is appuser.
 */
@Entity
@Table(name = "appuser")
public class Appuser implements java.io.Serializable {

    private String userId;
    private String userName;
    private String password;
    private String userShortName;
    private String email;
    private String phone;
    private boolean active;
    private UserRole userRole;
    private Location defLocation;
    private Integer migId;
    private Date updatedDate;
    
    public Appuser() {
    }

    public Appuser(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Appuser(String userId, String userName, String password, String userShortName) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.userShortName = userShortName;
    }

    public Appuser(String userId, String userName, String password, String userShortName,
            String email, String phone, Boolean active) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.userShortName = userShortName;
        this.email = email;
        this.phone = phone;
        this.active = active;
    }

    @Id
    @Column(name = "user_id", unique = true, nullable = false, length = 15)
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "user_name", nullable = false, length = 70)
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "password", nullable = false, length = 20)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "user_short_name", nullable = false, length = 15, unique = true)
    public String getUserShortName() {
        return this.userShortName;
    }

    public void setUserShortName(String userShortName) {
        this.userShortName = userShortName;
    }

    @Column(name = "email", length = 45)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone", length = 45)
    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "active")
    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return userName;
    }

    @ManyToOne
    @JoinColumn(name = "role_id")
    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @ManyToOne
    @JoinColumn(name = "def_location")
    public Location getDefLocation() {
        return defLocation;
    }

    public void setDefLocation(Location defLocation) {
        this.defLocation = defLocation;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
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
