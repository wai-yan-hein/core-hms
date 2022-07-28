/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.controller;

import com.cv.app.pharmacy.database.entity.DamageHis;
import com.cv.app.pharmacy.database.entity.Gl;
import com.cv.app.pharmacy.database.entity.PurHis;
import com.cv.app.pharmacy.database.entity.RetInHis;
import com.cv.app.pharmacy.database.entity.RetOutHis;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.view.VMarchant;
import com.cv.app.util.Util1;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class SchoolDataAccess {

    static Logger log = Logger.getLogger(SchoolDataAccess.class.getName());
    private AbstractDataAccess dao;

    public SchoolDataAccess(AbstractDataAccess dao) {
        this.dao = dao;
    }

    public VMarchant getMarchant(String code) {
        VMarchant vm = null;
        try {
            String strSql = "select o from VMarchant o where o.personNumber = '" + code + "'";
            List<VMarchant> listVM = dao.findAllHSQL(strSql);
            if (listVM != null) {
                if (!listVM.isEmpty()) {
                    vm = listVM.get(0);
                }
            }
        } catch (Exception ex) {
            log.error("getMarchant : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return vm;
    }

    public void saveGL(Gl gl) throws Exception {
        dao.save(gl);
    }

    public void saveSaleGl(SaleHis currVou, String status) throws Exception {
        String sourceAccIdVou = "-";
        String accIdVou = "-";
        String sourceAccIdPaid = "-";
        String accIdPaid = "-";
        int compId = 0;

        if (!status.equals("NEW")) {
            String strSql = "select o from Gl o where o.vouNo = '" + currVou.getSaleInvId()
                    + "' and o.tranSource = 'INV-SALE'";
            List<Gl> listGL = dao.findAllHSQL(strSql);
            for (Gl gl : listGL) {
                gl.setModifyBy(currVou.getUpdatedBy().getUserId());
                gl.setModifyDate(currVou.getUpdatedDate());

                gl.setGlDate(currVou.getSaleDate());
                gl.setDescription(currVou.getRemark());
                gl.setToCurId("MMK");
                gl.setReference(null);

                if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    if (currVou.getRegNo() == null) {
                        gl.setTraderId(null);
                    } else if (currVou.getRegNo().isEmpty()) {
                        gl.setTraderId(null);
                    } else {
                        String traderId = currVou.getRegNo();
                        gl.setTraderId(Long.parseLong(traderId));
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                } else {
                    if (currVou.getCustomerId() == null) {
                        gl.setTraderId(null);
                    } else {
                        String traderId = currVou.getCustomerId().getTraderId();
                        gl.setTraderId(Long.parseLong(traderId));
                    }
                }

                if (gl.getSourceAcId().equals(sourceAccIdVou)) {
                    gl.setDrAmt(currVou.getBalance());
                    gl.setCrAmt(Double.NaN);
                } else if (gl.getSourceAcId().equals(sourceAccIdPaid)) {
                    gl.setDrAmt(currVou.getPaid());
                    gl.setCrAmt(Double.NaN);
                }

                dao.save(gl);
            }
        } else {
            if (currVou.getDeleted()) {
                String strSql = "delete from gl where vouNo = '" + currVou.getSaleInvId()
                        + "' and tranSource = 'INV-SALE'";
                dao.execSql(strSql);
            } else {
                Gl glVou = new Gl();
                glVou.setCreatedBy(currVou.getCreatedBy().getUserId());
                glVou.setCreatedDate(currVou.getCreatedDate());
                glVou.setGlDate(currVou.getSaleDate());
                glVou.setDescription(currVou.getRemark());
                glVou.setSourceAcId(sourceAccIdVou);
                glVou.setAccountId(accIdVou);
                glVou.setToCurId("MMK");
                glVou.setDrAmt(currVou.getBalance());
                glVou.setCrAmt(Double.NaN);
                glVou.setReference(null);
                glVou.setVouNo(currVou.getSaleInvId());
                if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    if (currVou.getRegNo() == null) {
                        glVou.setTraderId(null);
                    } else if (currVou.getRegNo().isEmpty()) {
                        glVou.setTraderId(null);
                    } else {
                        String traderId = currVou.getRegNo();
                        glVou.setTraderId(Long.parseLong(traderId));
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                } else {
                    if (currVou.getCustomerId() == null) {
                        glVou.setTraderId(null);
                    } else {
                        String traderId = currVou.getCustomerId().getTraderId();
                        glVou.setTraderId(Long.parseLong(traderId));
                    }
                }
                glVou.setCompId(compId);
                glVou.setTranSource("INV-SALE");
                dao.save(glVou);

                Gl glPaid = new Gl();
                glPaid.setCreatedBy(currVou.getCreatedBy().getUserId());
                glPaid.setCreatedDate(currVou.getCreatedDate());
                glPaid.setGlDate(currVou.getSaleDate());
                glPaid.setDescription(currVou.getRemark());
                glPaid.setSourceAcId(sourceAccIdPaid);
                glPaid.setAccountId(accIdPaid);
                glPaid.setToCurId("MMK");
                glPaid.setDrAmt(currVou.getPaid());
                glPaid.setCrAmt(Double.NaN);
                glPaid.setReference(null);
                glPaid.setVouNo(currVou.getSaleInvId());
                if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    if (currVou.getRegNo() == null) {
                        glPaid.setTraderId(null);
                    } else if (currVou.getRegNo().isEmpty()) {
                        glPaid.setTraderId(null);
                    } else {
                        String traderId = currVou.getRegNo();
                        glPaid.setTraderId(Long.parseLong(traderId));
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                } else {
                    String traderId = currVou.getCustomerId().getTraderId();
                    glPaid.setTraderId(Long.parseLong(traderId));
                }
                glPaid.setCompId(compId);
                glPaid.setTranSource("INV-SALE");

                dao.save(glPaid);
            }
        }
    }

    public void saveRetInGl(RetInHis currRetIn, String status) throws Exception {
        String sourceAccIdVou = "-";
        String accIdVou = "-";
        String sourceAccIdPaid = "-";
        String accIdPaid = "-";
        int compId = 0;

        if (!status.equals("NEW")) {
            String strSql = "select o from Gl o where o.vouNo = '" + currRetIn.getRetInId()
                    + "' and o.tranSource = 'INV-RETIN'";
            List<Gl> listGL = dao.findAllHSQL(strSql);
            for (Gl gl : listGL) {
                gl.setModifyBy(currRetIn.getUpdatedBy().getUserId());
                gl.setModifyDate(currRetIn.getUpdatedDate());

                gl.setGlDate(currRetIn.getRetInDate());
                gl.setDescription(currRetIn.getRemark());
                gl.setToCurId("MMK");
                gl.setReference(null);

                if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    if (currRetIn.getRegNo() == null) {
                        gl.setTraderId(null);
                    } else if (currRetIn.getRegNo().isEmpty()) {
                        gl.setTraderId(null);
                    } else {
                        String traderId = currRetIn.getRegNo();
                        gl.setTraderId(Long.parseLong(traderId));
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                } else {
                    if (currRetIn.getCustomer() == null) {
                        gl.setTraderId(null);
                    } else {
                        String traderId = currRetIn.getCustomer().getTraderId();
                        gl.setTraderId(Long.parseLong(traderId));
                    }
                }

                if (gl.getSourceAcId().equals(sourceAccIdVou)) {
                    gl.setDrAmt(currRetIn.getBalance());
                    gl.setCrAmt(Double.NaN);
                } else if (gl.getSourceAcId().equals(sourceAccIdPaid)) {
                    gl.setDrAmt(currRetIn.getPaid());
                    gl.setCrAmt(Double.NaN);
                }

                dao.save(gl);
            }
        } else {
            if (currRetIn.isDeleted()) {
                String strSql = "delete from gl where vouNo = '" + currRetIn.getRetInId()
                        + "' and tranSource = 'INV-RETIN'";
                dao.execSql(strSql);
            } else {
                Gl glVou = new Gl();
                glVou.setCreatedBy(currRetIn.getCreatedBy().getUserId());
                glVou.setCreatedDate(currRetIn.getCreatedDate());
                glVou.setGlDate(currRetIn.getRetInDate());
                glVou.setDescription(currRetIn.getRemark());
                glVou.setSourceAcId(sourceAccIdVou);
                glVou.setAccountId(accIdVou);
                glVou.setToCurId("MMK");
                glVou.setDrAmt(currRetIn.getBalance());
                glVou.setCrAmt(Double.NaN);
                glVou.setReference(null);
                glVou.setVouNo(currRetIn.getRetInId());
                if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    if (currRetIn.getRegNo() == null) {
                        glVou.setTraderId(null);
                    } else if (currRetIn.getRegNo().isEmpty()) {
                        glVou.setTraderId(null);
                    } else {
                        String traderId = currRetIn.getRegNo();
                        glVou.setTraderId(Long.parseLong(traderId));
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                } else {
                    if (currRetIn.getCustomer() == null) {
                        glVou.setTraderId(null);
                    } else {
                        String traderId = currRetIn.getCustomer().getTraderId();
                        glVou.setTraderId(Long.parseLong(traderId));
                    }
                }
                glVou.setCompId(compId);
                glVou.setTranSource("INV-RETIN");
                dao.save(glVou);

                Gl glPaid = new Gl();
                glPaid.setCreatedBy(currRetIn.getCreatedBy().getUserId());
                glPaid.setCreatedDate(currRetIn.getCreatedDate());
                glPaid.setGlDate(currRetIn.getRetInDate());
                glPaid.setDescription(currRetIn.getRemark());
                glPaid.setSourceAcId(sourceAccIdPaid);
                glPaid.setAccountId(accIdPaid);
                glPaid.setToCurId("MMK");
                glPaid.setDrAmt(currRetIn.getPaid());
                glPaid.setCrAmt(Double.NaN);
                glPaid.setReference(null);
                glPaid.setVouNo(currRetIn.getRetInId());
                if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                    if (currRetIn.getRegNo() == null) {
                        glPaid.setTraderId(null);
                    } else if (currRetIn.getRegNo().isEmpty()) {
                        glPaid.setTraderId(null);
                    } else {
                        String traderId = currRetIn.getRegNo();
                        glPaid.setTraderId(Long.parseLong(traderId));
                    }
                } else if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                } else {
                    String traderId = currRetIn.getCustomer().getTraderId();
                    glPaid.setTraderId(Long.parseLong(traderId));
                }
                glPaid.setCompId(compId);
                glPaid.setTranSource("INV-RETIN");

                dao.save(glPaid);
            }
        }
    }

    public void savePurchaseGl(PurHis currVou, String status) throws Exception {
        String sourceAccIdVou = "-";
        String accIdVou = "-";
        String sourceAccIdPaid = "-";
        String accIdPaid = "-";
        int compId = 0;

        if (!status.equals("NEW")) {
            String strSql = "select o from Gl o where o.vouNo = '" + currVou.getPurInvId()
                    + "' and o.tranSource = 'INV-PURCHASE'";
            List<Gl> listGL = dao.findAllHSQL(strSql);
            for (Gl gl : listGL) {
                gl.setModifyBy(currVou.getUpdatedBy().getUserId());
                gl.setModifyDate(currVou.getUpdatedDate());

                gl.setGlDate(currVou.getPurDate());
                gl.setDescription(currVou.getRemark());
                gl.setToCurId("MMK");
                gl.setReference(null);

                if (currVou.getCustomerId() == null) {
                    gl.setTraderId(null);
                } else {
                    String traderId = currVou.getCustomerId().getTraderId();
                    gl.setTraderId(Long.parseLong(traderId));
                }

                if (gl.getSourceAcId().equals(sourceAccIdVou)) {
                    gl.setDrAmt(currVou.getBalance());
                    gl.setCrAmt(Double.NaN);
                } else if (gl.getSourceAcId().equals(sourceAccIdPaid)) {
                    gl.setDrAmt(currVou.getPaid());
                    gl.setCrAmt(Double.NaN);
                }

                dao.save(gl);
            }
        } else {
            if (currVou.getDeleted()) {
                String strSql = "delete from gl where vouNo = '" + currVou.getPurInvId()
                        + "' and tranSource = 'INV-PURCHASE'";
                dao.execSql(strSql);
            } else {
                Gl glVou = new Gl();
                glVou.setCreatedBy(currVou.getCreatedBy().getUserId());
                glVou.setCreatedDate(currVou.getCreatedDate());
                glVou.setGlDate(currVou.getPurDate());
                glVou.setDescription(currVou.getRemark());
                glVou.setSourceAcId(sourceAccIdVou);
                glVou.setAccountId(accIdVou);
                glVou.setToCurId("MMK");
                glVou.setDrAmt(currVou.getBalance());
                glVou.setCrAmt(Double.NaN);
                glVou.setReference(null);
                glVou.setVouNo(currVou.getPurInvId());
                if (currVou.getCustomerId() == null) {
                    glVou.setTraderId(null);
                } else {
                    String traderId = currVou.getCustomerId().getTraderId();
                    glVou.setTraderId(Long.parseLong(traderId));
                }
                glVou.setCompId(compId);
                glVou.setTranSource("INV-PURCHASE");
                dao.save(glVou);

                Gl glPaid = new Gl();
                glPaid.setCreatedBy(currVou.getCreatedBy().getUserId());
                glPaid.setCreatedDate(currVou.getCreatedDate());
                glPaid.setGlDate(currVou.getPurDate());
                glPaid.setDescription(currVou.getRemark());
                glPaid.setSourceAcId(sourceAccIdPaid);
                glPaid.setAccountId(accIdPaid);
                glPaid.setToCurId("MMK");
                glPaid.setDrAmt(currVou.getPaid());
                glPaid.setCrAmt(Double.NaN);
                glPaid.setReference(null);
                glPaid.setVouNo(currVou.getPurInvId());

                String traderId = currVou.getCustomerId().getTraderId();
                glPaid.setTraderId(Long.parseLong(traderId));

                glPaid.setCompId(compId);
                glPaid.setTranSource("INV-PURCHASE");

                dao.save(glPaid);
            }
        }
    }

    public void saveReturnOutGl(RetOutHis currVou, String status) throws Exception {
        String sourceAccIdVou = "-";
        String accIdVou = "-";
        String sourceAccIdPaid = "-";
        String accIdPaid = "-";
        int compId = 0;

        if (!status.equals("NEW")) {
            String strSql = "select o from Gl o where o.vouNo = '" + currVou.getRetOutId()
                    + "' and o.tranSource = 'INV-RETOUT'";
            List<Gl> listGL = dao.findAllHSQL(strSql);
            for (Gl gl : listGL) {
                gl.setModifyBy(currVou.getUpdatedBy().getUserId());
                gl.setModifyDate(currVou.getUpdatedDate());

                gl.setGlDate(currVou.getRetOutDate());
                gl.setDescription(currVou.getRemark());
                gl.setToCurId("MMK");
                gl.setReference(null);

                if (currVou.getCustomer() == null) {
                    gl.setTraderId(null);
                } else {
                    String traderId = currVou.getCustomer().getTraderId();
                    gl.setTraderId(Long.parseLong(traderId));
                }

                if (gl.getSourceAcId().equals(sourceAccIdVou)) {
                    gl.setDrAmt(currVou.getBalance());
                    gl.setCrAmt(Double.NaN);
                } else if (gl.getSourceAcId().equals(sourceAccIdPaid)) {
                    gl.setDrAmt(currVou.getPaid());
                    gl.setCrAmt(Double.NaN);
                }

                dao.save(gl);
            }
        } else {
            if (currVou.isDeleted()) {
                String strSql = "delete from gl where vouNo = '" + currVou.getRetOutId()
                        + "' and tranSource = 'INV-RETOUT'";
                dao.execSql(strSql);
            } else {
                Gl glVou = new Gl();
                glVou.setCreatedBy(currVou.getCreatedBy().getUserId());
                glVou.setCreatedDate(currVou.getCreatedDate());
                glVou.setGlDate(currVou.getRetOutDate());
                glVou.setDescription(currVou.getRemark());
                glVou.setSourceAcId(sourceAccIdVou);
                glVou.setAccountId(accIdVou);
                glVou.setToCurId("MMK");
                glVou.setDrAmt(currVou.getBalance());
                glVou.setCrAmt(Double.NaN);
                glVou.setReference(null);
                glVou.setVouNo(currVou.getRetOutId());
                if (currVou.getCustomer() == null) {
                    glVou.setTraderId(null);
                } else {
                    String traderId = currVou.getCustomer().getTraderId();
                    glVou.setTraderId(Long.parseLong(traderId));
                }
                glVou.setCompId(compId);
                glVou.setTranSource("INV-RETOUT");
                dao.save(glVou);

                Gl glPaid = new Gl();
                glPaid.setCreatedBy(currVou.getCreatedBy().getUserId());
                glPaid.setCreatedDate(currVou.getCreatedDate());
                glPaid.setGlDate(currVou.getRetOutDate());
                glPaid.setDescription(currVou.getRemark());
                glPaid.setSourceAcId(sourceAccIdPaid);
                glPaid.setAccountId(accIdPaid);
                glPaid.setToCurId("MMK");
                glPaid.setDrAmt(currVou.getPaid());
                glPaid.setCrAmt(Double.NaN);
                glPaid.setReference(null);
                glPaid.setVouNo(currVou.getRetOutId());

                String traderId = currVou.getCustomer().getTraderId();
                glPaid.setTraderId(Long.parseLong(traderId));

                glPaid.setCompId(compId);
                glPaid.setTranSource("INV-RETOUT");

                dao.save(glPaid);
            }
        }
    }

    public void saveDamageGl(DamageHis currVou, String status) throws Exception {
        String sourceAccIdVou = "-";
        String accIdVou = "-";
        int compId = 0;

        if (!status.equals("NEW")) {
            String strSql = "select o from Gl o where o.vouNo = '" + currVou.getDmgVouId()
                    + "' and o.tranSource = 'INV-DAMAGE'";
            List<Gl> listGL = dao.findAllHSQL(strSql);
            for (Gl gl : listGL) {
                gl.setModifyBy(currVou.getUpdatedBy().getUserId());
                gl.setModifyDate(currVou.getUpdatedDate());

                gl.setGlDate(currVou.getDmgDate());
                gl.setDescription(currVou.getRemark());
                gl.setToCurId("MMK");
                gl.setReference(null);

                if (gl.getSourceAcId().equals(sourceAccIdVou)) {
                    gl.setDrAmt(currVou.getTotalAmount());
                    gl.setCrAmt(Double.NaN);
                }

                dao.save(gl);
            }
        } else {
            if (currVou.isDeleted()) {
                String strSql = "delete from gl where vouNo = '" + currVou.getDmgVouId()
                        + "' and tranSource = 'INV-DAMAGE'";
                dao.execSql(strSql);
            } else {
                Gl glVou = new Gl();
                glVou.setCreatedBy(currVou.getCreatedBy().getUserId());
                glVou.setCreatedDate(currVou.getCreatedDate());
                glVou.setGlDate(currVou.getDmgDate());
                glVou.setDescription(currVou.getRemark());
                glVou.setSourceAcId(sourceAccIdVou);
                glVou.setAccountId(accIdVou);
                glVou.setToCurId("MMK");
                glVou.setDrAmt(currVou.getTotalAmount());
                glVou.setCrAmt(Double.NaN);
                glVou.setReference(null);
                glVou.setVouNo(currVou.getDmgVouId());

                glVou.setCompId(compId);
                glVou.setTranSource("INV-DAMAGE");
                dao.save(glVou);
            }
        }
    }

    public void savePaymentGl(TraderPayHis currVou, String status) throws Exception {
        String sourceAccIdVou = "-";
        String accIdVou = "-";
        int compId = 0;

        if (!status.equals("NEW")) {
            String strSql = "select o from Gl o where o.vouNo = '" + currVou.getPaymentId().toString()
                    + "' and o.tranSource = 'INV-PAYMENT'";
            List<Gl> listGL = dao.findAllHSQL(strSql);
            for (Gl gl : listGL) {
                gl.setModifyBy(currVou.getUpdateBy().getUserId());
                gl.setModifyDate(currVou.getUpdatedDate());

                gl.setGlDate(currVou.getPayDate());
                gl.setDescription(currVou.getRemark());
                gl.setToCurId("MMK");
                gl.setReference(null);

                if (gl.getSourceAcId().equals(sourceAccIdVou)) {
                    gl.setDrAmt(currVou.getPaidAmtC());
                    gl.setCrAmt(Double.NaN);
                }

                dao.save(gl);
            }
        } else {
            if (currVou.isDeleted()) {
                String strSql = "delete from gl where vouNo = '" + currVou.getPaymentId().toString()
                        + "' and tranSource = 'INV-PAYMENT'";
                dao.execSql(strSql);
            } else {
                Gl glVou = new Gl();
                glVou.setCreatedBy(currVou.getCreatedBy().getUserId());
                glVou.setCreatedDate(currVou.getCreatedDate());
                glVou.setGlDate(currVou.getPayDate());
                glVou.setDescription(currVou.getRemark());
                glVou.setSourceAcId(sourceAccIdVou);
                glVou.setAccountId(accIdVou);
                glVou.setToCurId("MMK");
                glVou.setDrAmt(currVou.getPaidAmtC());
                glVou.setCrAmt(Double.NaN);
                glVou.setReference(null);
                glVou.setVouNo(currVou.getPaymentId().toString());

                glVou.setCompId(compId);
                glVou.setTranSource("INV-PAYMENT");
                dao.save(glVou);
            }
        }
    }
}
