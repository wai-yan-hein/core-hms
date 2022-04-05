/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="opd_reader_his")
public class OPDReaderHis implements java.io.Serializable{
  private String opdId;
  private String detailId;
  private Service service;
  
}
