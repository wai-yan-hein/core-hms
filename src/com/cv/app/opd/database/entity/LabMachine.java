package com.cv.app.opd.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Medicine category. Will be used in medicine setup and filtering. Database
 * table is category.
 */
@Entity
@Table(name = "lab_machine")
public class LabMachine implements java.io.Serializable {

    private Integer lMachineId;
    private String lMachineName;
    
    public LabMachine() {
    }

    public LabMachine(String lMachineName) {
        this.lMachineName = lMachineName;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lab_machine_id", unique = true, nullable = false)
    public Integer getlMachineId() {
        return lMachineId;
    }

    public void setlMachineId(Integer lMachineId) {
        this.lMachineId = lMachineId;
    }

    @Column(name = "lab_machine_name", nullable = false, length = 500, unique = true)
    public String getlMachineName() {
        return this.lMachineName;
    }

    public void setlMachineName(String lMachineName) {
        this.lMachineName = lMachineName;
    }

    @Override
    public String toString() {
        return this.lMachineName;
    }
    
}
