/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import com.cv.app.inpatient.database.entity.InpService;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author wswe
 */
@Entity
@Table(name="doctor_fees_map_dc")
public class DoctorFeesMappingDC implements java.io.Serializable{
    private Integer mapId;
    private InpService service;
    private Double fees;
    private Integer uniqueId;
    private String drId;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name = "map_id", unique = true, nullable = false)
    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="service_id")    
    public InpService getService() {
        return service;
    }

    public void setService(InpService service) {
        this.service = service;
    }

    @Column(name="fees")
    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name="doctor_id", length=15)
    public String getDrId() {
        return drId;
    }

    public void setDrId(String drId) {
        this.drId = drId;
    }
}
