/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
import org.hibernate.annotations.GenerationTime;
/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "machine_info")
public class MachineInfo implements java.io.Serializable{
    private Integer machineId;
    private String machineName;
    private String ipAddress;
    private Date regDate;
    private String actionStatus;
    
    public MachineInfo(){}

    @Column(name="machine_ip")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="machine_id", unique=true, nullable=false)
    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }
    
    @Column(name="machine_name", unique=true, nullable=false)
    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    @Column(name = "created_date", insertable=false, updatable=false, 
            columnDefinition="timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value=GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }
    
    @Override
    public String toString(){
        return machineName;
    }

    @Column(name="action_status", length=5)
    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }
}
