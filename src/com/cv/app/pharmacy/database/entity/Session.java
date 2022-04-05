package com.cv.app.pharmacy.database.entity;
// Generated Apr 22, 2012 10:00:47 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Session is login session.
 * Database table name is session.
 */
@Entity
@Table(name="session")
public class Session  implements java.io.Serializable {

     private Integer sessionId;
     private String sessionName;
     private String startTime;
     private String endTime;
     
    public Session() {
    }

    public Session(String sessionName) {
       this.sessionName = sessionName;
    }
   
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="session_id", unique=true, nullable=false)
    public Integer getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }
    
    @Column(name="session_name", nullable=false, length=20)
    public String getSessionName() {
        return this.sessionName;
    }
    
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    @Column(name="end_time",  length=10)
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Column(name="start_time", length=10)
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    @Override
    public String toString(){
        return sessionName;
    }
}


