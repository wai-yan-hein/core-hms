/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.migration;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.DCDetailHis;
import com.cv.app.inpatient.database.entity.DCHis;
import com.cv.app.inpatient.database.entity.Diagnosis;
import com.cv.app.inpatient.database.entity.InpCategory;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.Initial;
import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.opd.database.entity.OPDDetailHis;
import com.cv.app.opd.database.entity.OPDHis;
import com.cv.app.opd.database.entity.OPDLabResult;
import com.cv.app.opd.database.entity.OPDLabResultInd;
import com.cv.app.opd.database.entity.OPDResultType;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.opd.database.entity.Speciality;
import com.cv.app.ot.database.entity.OTDetailHis;
import com.cv.app.ot.database.entity.OTHis;
import com.cv.app.ot.database.entity.OTProcedure;
import com.cv.app.ot.database.entity.OTProcedureGroup;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.AdjDetailHis;
import com.cv.app.pharmacy.database.entity.AdjHis;
import com.cv.app.pharmacy.database.entity.AdjType;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.CharacterNo;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.database.entity.CompoundKeyTraderOp;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.DamageDetailHis;
import com.cv.app.pharmacy.database.entity.DamageHis;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.MedOpDate;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.PurDetailHis;
import com.cv.app.pharmacy.database.entity.PurHis;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.RetInDetailHis;
import com.cv.app.pharmacy.database.entity.RetInHis;
import com.cv.app.pharmacy.database.entity.RetOutDetailHis;
import com.cv.app.pharmacy.database.entity.RetOutHis;
import com.cv.app.pharmacy.database.entity.SaleDetailHis;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.database.entity.StockOpeningDetailHis;
import com.cv.app.pharmacy.database.entity.StockOpeningHis;
import com.cv.app.pharmacy.database.entity.Supplier;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderOpening;
import com.cv.app.pharmacy.database.entity.TraderType;
import com.cv.app.pharmacy.database.entity.TransferDetailHis;
import com.cv.app.pharmacy.database.entity.TransferHis;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author admin
 */
public class SystemMigration extends javax.swing.JPanel {

    private final AbstractDataAccess dao = Global.dao;
    static Logger log = Logger.getLogger(SystemMigration.class.getName());
    private HashMap<Integer, PaymentType> hmBillTo;
    private HashMap<Integer, Location> hmLocation;
    private HashMap<Integer, Appuser> hmUser;
    private HashMap<Integer, Integer> hmSession;
    private HashMap<Integer, OPDResultType> hmRType;
    private HashMap<Integer, Integer> hmIngZgy;
    private HashMap<String, TraderType> hmTraderType;

    //private HashMap<Integer, String> hmDoctor;
    private final Currency curr = (Currency) dao.find(Currency.class, "MMK");
    private final VouStatus vs = (VouStatus) dao.find(VouStatus.class, 1);
    private final ChargeType ct = (ChargeType) dao.find(ChargeType.class, 1);
    private final AdjType atP = (AdjType) dao.find(AdjType.class, "+");
    private final AdjType atM = (AdjType) dao.find(AdjType.class, "-");
    private Connection con;
    private GenVouNoImpl vouEngine;

    /**
     * Creates new form SystemMigration
     */
    public SystemMigration() {
        initComponents();
        initData();
    }

    private void initData() {
        txtDbSid.setText("DSL");
        txtPassword.setText("DSL");
        txtServerName.setText("server");
        txtUser.setText("DSL");
        butProcess.setEnabled(false);
        butConnect.setEnabled(false);
        cboDataToProcess.setEnabled(false);

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            butConnect.setEnabled(true);
            initBillTo();
            initLocation();
            initUser();
            initSession();
            initResultType();
            initFont();
            intTraderType();
        } catch (Exception ex) {
            lblStatus.setText("ERROR : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            log.error("initData : " + ex);
        }
    }

    private void initFont() {
        hmIngZgy = new HashMap();
        try {
            ResultSet rs = dao.execSQL("select integrakeycode, zawgyikeycode from convert_code.font");
            while (rs.next()) {
                hmIngZgy.put(rs.getInt("integrakeycode"), rs.getInt("zawgyikeycode"));
            }
        } catch (Exception ex) {
            log.error("initFont : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private String getZawgyiText(String text) {
        String tmpStr = "";

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);

                if (hmIngZgy.containsKey(tmpChar)) {
                    char tmpc = (char) hmIngZgy.get(tmpChar).intValue();
                    if (tmpStr.isEmpty()) {
                        tmpStr = Character.toString(tmpc);
                    } else {
                        tmpStr = tmpStr + Character.toString(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.isEmpty()) {
                        tmpStr = "ႏ";
                    } else {
                        tmpStr = tmpStr + "ႏ";
                    }
                } else if (tmpStr.isEmpty()) {
                    tmpStr = tmpS;
                } else {
                    tmpStr = tmpStr + tmpS;
                }
            }
        }

        return tmpStr;
    }

    private void initBillTo() {
        hmBillTo = new HashMap();
        hmBillTo.put(303, (PaymentType) dao.find(PaymentType.class, 2));
        hmBillTo.put(341, (PaymentType) dao.find(PaymentType.class, 3));
        hmBillTo.put(282, (PaymentType) dao.find(PaymentType.class, 4));
        hmBillTo.put(322, (PaymentType) dao.find(PaymentType.class, 5));
    }

    private void intTraderType() {
        hmTraderType = new HashMap();
        hmTraderType.put("A", (TraderType) dao.find(TraderType.class, 2));
        hmTraderType.put("B", (TraderType) dao.find(TraderType.class, 3));
        hmTraderType.put("C", (TraderType) dao.find(TraderType.class, 4));
        hmTraderType.put("N", (TraderType) dao.find(TraderType.class, 1));
    }

    private void initLocation() {
        hmLocation = new HashMap();
        hmLocation.put(1, (Location) dao.find(Location.class, 1));
        hmLocation.put(2, (Location) dao.find(Location.class, 2));
        hmLocation.put(6, (Location) dao.find(Location.class, 3));
        hmLocation.put(7, (Location) dao.find(Location.class, 4));
        hmLocation.put(8, (Location) dao.find(Location.class, 5));
        hmLocation.put(3, (Location) dao.find(Location.class, 6));
        hmLocation.put(5, (Location) dao.find(Location.class, 7));
        hmLocation.put(9, (Location) dao.find(Location.class, 8));
        hmLocation.put(10, (Location) dao.find(Location.class, 9));
        hmLocation.put(11, (Location) dao.find(Location.class, 10));
        hmLocation.put(12, (Location) dao.find(Location.class, 11));
        hmLocation.put(13, (Location) dao.find(Location.class, 12));
        hmLocation.put(19, (Location) dao.find(Location.class, 13));
        hmLocation.put(21, (Location) dao.find(Location.class, 14));
        hmLocation.put(22, (Location) dao.find(Location.class, 15));
        hmLocation.put(26, (Location) dao.find(Location.class, 16));
        hmLocation.put(30, (Location) dao.find(Location.class, 17));
    }

    private void initUser() {
        hmUser = new HashMap();
        hmUser.put(1, (Appuser) dao.find(Appuser.class, "1.0"));
        /*hmUser.put(21, (Appuser) dao.find(Appuser.class, "013"));
        hmUser.put(39, (Appuser) dao.find(Appuser.class, "014"));
        hmUser.put(17, (Appuser) dao.find(Appuser.class, "2.0"));
        hmUser.put(37, (Appuser) dao.find(Appuser.class, "001"));
        hmUser.put(28, (Appuser) dao.find(Appuser.class, "016"));
        hmUser.put(26, (Appuser) dao.find(Appuser.class, "009"));
        hmUser.put(8, (Appuser) dao.find(Appuser.class, "003"));
        hmUser.put(4, (Appuser) dao.find(Appuser.class, "004"));
        hmUser.put(71, (Appuser) dao.find(Appuser.class, "005"));
        hmUser.put(6, (Appuser) dao.find(Appuser.class, "006"));
        hmUser.put(10, (Appuser) dao.find(Appuser.class, "007"));
        hmUser.put(3, (Appuser) dao.find(Appuser.class, "019"));*/
    }

    private void initSession() {
        hmSession = new HashMap();
        hmSession.put(1, 1);
        hmSession.put(2, 2);
        hmSession.put(3, 3);
    }

    private void initResultType() {
        hmRType = new HashMap();
        hmRType.put(-1, (OPDResultType) dao.find(OPDResultType.class, -1));
        hmRType.put(0, (OPDResultType) dao.find(OPDResultType.class, 0));
        hmRType.put(2, (OPDResultType) dao.find(OPDResultType.class, 2));
    }

    private String getSql(String type) {
        switch (type) {
            case "Sale":
                return "SELECT SIH.SALE_DATE, SIH.SALE_INV_ID, SIH.PAYMENT_TYPE_ID, "
                        + "SIH.LOCATION_ID, SIH.DISCOUNT_AMT, SIH.DOCTOR_ID, SIH.SALE_ID,\n"
                        + "SIH.PAID_AMT, SIH.USER_ID, SIH.SESSION_ID, SIH.INV_TOTAL, SIH.REMARK, P.U_PREG_NO\n"
                        + "FROM SALE_INV_HIS SIH, PATIENT P\n"
                        + "WHERE SIH.IS_DELETED <> 0 AND SIH.SALE_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018'\n"
                        + "AND SIH.PREG_NO = P.PREG_NO(+) AND SIH.SALE_INV_ID = '8100626092018'";
            case "Purchase":
                return "SELECT PIH.P_INV_ID, PIH.SUPPLIER_ID, PIH.P_DATE, PIH.LOCATION_ID, PIH.PAYMENT_TYPE_ID,\n"
                        + "PIH.DISCOUNT_AMT, PIH.PAID_AMT, PIH.USER_ID, PIH.DUE_DATE, PIH.DIS_PERCENT\n"
                        + "FROM PUR_INV_HIS PIH\n"
                        + "WHERE PIH.P_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018'";
            case "Return In":
                return "SELECT RIIH.RET_IN_INV_ID, RIIH.CURRENCY_ID, RIIH.LOCATION_ID, RIIH.PAID_AMT, RIIH.SESSION_ID, "
                        + "RIIH.USER_ID, RIIH.RET_DATE, RIIH.PAYMENT_TYPE_ID, RIIH.SESSION_ID, P.U_PREG_NO\n"
                        + "FROM RET_IN_INV_HIS RIIH, PATIENT P\n"
                        + "WHERE RIIH.PREG_NO = P.PREG_NO(+) AND RIIH.RET_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018'";
            case "Return Out":
                return "SELECT ROIH.R_INV_ID, ROIH.CURRENCY_ID, ROIH.SUPPLIER_ID, ROIH.R_DATE, ROIH.LOCATION_ID, "
                        + "ROIH.PAYMENT_TYPE_ID, ROIH.PAID_AMT, ROIH.USER_ID\n"
                        + "FROM RET_OUT_INV_HIS ROIH, SUPPLIER SUP\n"
                        + "WHERE ROIH.SUPPLIER_ID = SUP.SUPPLIER_ID AND ROIH.R_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018';";
            case "Damage":
                return "SELECT DIH.DMG_INV_ID, DIH.DMG_DATE, DIH.LOCATION_ID, DIH.USER_ID\n"
                        + "FROM DMG_INV_HIS DIH\n"
                        + "WHERE DIH.DMG_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018'";
            case "Adjust":
                return "SELECT SAIH.ADJ_INV_ID, SAIH.ADJ_DATE, SAIH.LOCATION_ID, SAIH.USER_ID\n"
                        + "FROM STOCK_ADJ_INV_HIS SAIH\n"
                        + "WHERE SAIH.ADJ_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018'";
            case "Transfer":
                return "SELECT TRAN_INV_ID, TRAN_DATE, FRM_LOCATION_ID, TO_LOCATION_ID, USER_ID\n"
                        + "FROM TRAN_INV_HIS\n"
                        + "WHERE TRAN_DATE BETWEEN '30-SEP-2018' AND '05-OCT-2018'";
            case "Opening":
                return "SELECT DISTINCT LOCATION_ID, OP_DATE, USER_ID FROM STOCK_OPENING WHERE OP_DATE = '30-SEP-2018' AND LOCATION_ID = 1";
            case "Supplier":
                return "SELECT SUPPLIER_ID, SUPPLIER_NAME, SUP_ADDRESS, SUP_PHONE FROM SUPPLIER ORDER BY SUPPLIER_NAME";
            case "Patient":
                return "SELECT P.REG_DATE, P.PATIENT_NAME, P.SEX, P.U_PREG_NO, P.AGE, P.CITY_ID, "
                        + "P.DOCTOR_ID, P.FATHER_NAME, PP.ADDRESS, PP.PHONE, PP.TSP_ID "
                        + "FROM PATIENT P, PATIENT_PERSONAL PP "
                        + "WHERE P.PREG_NO = PP.PREG_NO order by p.u_preg_no";
            case "OPD":
                return "SELECT LH.LAB_ID, LH.LAB_VOU_NO, LH.LAB_DATE, LH.DOCTOR_ID, PT.U_PREG_NO, "
                        + "LH.TOTAL, LH.PAID, LH.DISCOUNT, LH.BILL_TO, LH.USER_ID, LH.PNAME, LH.BILL_TO, LH.REMARK, LH.SESSION_ID "
                        + "FROM LAB_HIS LH LEFT JOIN PATIENT PT ON LH.PREG_NO = PT.PREG_NO "
                        + "WHERE LH.LAB_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018' AND IS_DELETED = 0 AND LAB_VOU_NO = '8100263092018'";
            case "OT":
                return "SELECT OH.OT_ID, OH.U_OT_ID, OH.OT_DATE, OH.SURGEN_ID, PT.U_PREG_NO, OH.TOTAL, "
                        + "OH.PAID_AMT, OH.DISCOUNT, OH.USER_ID, OH.BILL_TO, OH.REMARK, "
                        + "OH.SESSION_ID, OH.TOTAL\n"
                        + "FROM OT_HIS OH LEFT JOIN PATIENT PT ON OH.PREG_NO = PT.PREG_NO \n"
                        + "WHERE OH.OT_DATE BETWEEN '30-SEP-2018' AND '30-SEP-2018' "
                        + "AND IS_DELETED = 0;";
            case "DC":
                return "SELECT OH.ISERVICE_ID, OH.VOU_NO, OH.IDATE, OH.DOCTOR_ID, PT.U_PREG_NO, OH.TOTAL, "
                        + "OH.PAID, OH.DISCOUNT, OH.BILL_TO, OH.USER_ID, OH.SESSION_ID, OH.DC_STATUS\n"
                        + "FROM DC_HIS OH LEFT JOIN PATIENT PT ON OH.PREG_NO = PT.PREG_NO \n"
                        + "WHERE OH.IDATE BETWEEN '30-SEP-2018' AND '30-SEP-2018' "
                        + "AND IS_DELETED = 0";
            case "OPD Setup":
                return "SELECT LAB_CLASS_ID, LAB_CLASS_DESP, SOURCE_ID\n"
                        + "FROM LAB_TEST_CLASS";
            case "Item Setup":
                return "SELECT MED_ID, SHORT_CODE, MED_DESP, DRUG_TYPE_ID, MANUFACTURER_ID, CHEMICAL_ID, ACTIVE,\n"
                        + "RELATION_STR\n"
                        + "FROM MEDICINE, RELATION_STR \n"
                        + "WHERE REL_GRP_ID = RELATION_GRP_ID AND SHORT_CODE IS NOT NULL AND MED_DESP IS NOT NULL ORDER BY ACTIVE";
            case "Lab Result":
                return "";
            case "City":
                return "SELECT TSP_ID, TSP_DESP\n"
                        + " FROM TOWNSHIP ORDER BY TSP_DESP";
            case "Township":
                return "SELECT CITY_ID, CITY_DESP\n"
                        + " FROM CITY WHERE CITY_DESP IS NOT NULL ORDER BY CITY_DESP";
            case "Doctor":
                return "SELECT DOCTOR_ID, DOCTOR_NAME, NVL(SPECIAL,'-') SPECIAL, SEX, PHONE, INITIAL_NAME\n"
                        + " FROM DOCTORS ORDER BY DOCTOR_NAME";
            case "DC Setup":
                return "SELECT TYPE_ID, TYPE_DESP, SORT_ORDER\n"
                        + "FROM WARD_PROCEDURES_TYPE ORDER BY TYPE_DESP";
            case "OT Setup":
                return "SELECT OT_TYPE_ID, OT_TYPE_DESP, SORT_ORDER\n"
                        + "FROM OT_TYPE ORDER BY OT_TYPE_DESP";
            case "Appuser":
                return "SELECT USER_ID, UNAME, UPASSWORD, USHORT\n"
                        + "FROM USER_LOGIN WHERE UNAME IS NOT NULL AND UPASSWORD IS NOT NULL ORDER BY UNAME";
            case "Customer":
                return "SELECT GET_CUS_GRP_CODE(CUS_CODE) AS G_CODE,CUS_CODE,CUS_NAME,CUS_ID,\n"
                        + "CUS_TYPE_ID, DECODE(CUS_TYPE_ID,1,'A',2,'B',3,'C') PRICE_TYPE, CR_LIMIT,\n"
                        + "CR_DAYS, TOWNSHIP_ID\n"
                        + "FROM CUSTOMER";
            case "OP Cost Price":
                return "SELECT MED.SHORT_CODE, MP.UNIT_SHORT, MP.PUR_PRICE\n"
                        + "FROM MEDICINE MED, MEDICINE_PRICE MP\n"
                        + "WHERE MED.MED_ID = MP.MEDICINE_ID\n"
                        + "ORDER BY MED.SHORT_CODE, MP.QTY_IN_SMALLEST DESC";
            case "Item Price Update":
                return "SELECT MED_ID, SHORT_CODE, MED_DESP, DRUG_TYPE_ID, MANUFACTURER_ID, CHEMICAL_ID, ACTIVE,\n"
                        + "RELATION_STR\n"
                        + "FROM MEDICINE, RELATION_STR \n"
                        + "WHERE REL_GRP_ID = RELATION_GRP_ID AND SHORT_CODE IS NOT NULL AND MED_DESP IS NOT NULL AND ACTIVE = -1 ORDER BY ACTIVE";
            case "Customer Opening":
                return "SELECT CUS_ID, CUS_CODE, CUS_NAME, CL_BAL, TRAN_DATE, USER_ID, TSP_DESP\n"
                        + "FROM RPT_CUS_PAYMENT\n"
                        + "WHERE (((CL_BAL)<>0) AND ((USER_ID)=?))";
            default:
                return "";
        }
    }

    private void processCityData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());

            while (rs.next()) {
                if (dao.getRowCount("select count(city_name) from city where city_name='" + getZawgyiText(rs.getString("TSP_DESP")) + "'") == 0) {
                    City obj = new City();
                    obj.setMigId(rs.getInt("TSP_ID"));
                    obj.setCityName(getZawgyiText(rs.getString("TSP_DESP")));
                    dao.save(obj);
                }
            }
            rs.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processCityData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void processTownshipData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());

            while (rs.next()) {
                if (dao.getRowCount("select count(township_name) from township where township_name='" + getZawgyiText(rs.getString("CITY_DESP")) + "'") == 0) {
                    Township obj = new Township();
                    obj.setMigId(rs.getInt("CITY_ID"));
                    obj.setTownshipName(getZawgyiText(rs.getString("CITY_DESP")));
                    dao.save(obj);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processTownshipData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void processDoctorData() {
        String str = null;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());

            while (rs.next()) {
                if (dao.getRowCount("select count(doctor_name) from doctor where doctor_name='" + getZawgyiText(rs.getString("DOCTOR_NAME")) + "'") == 0) {
                    Doctor obj = new Doctor();
                    obj.setDoctorId(rs.getString("DOCTOR_ID"));
                    obj.setDoctorName(getZawgyiText(rs.getString("DOCTOR_NAME")));
                    str = rs.getString("DOCTOR_ID");
                    obj.setActive(true);
                    if (rs.getString("SEX") != null) {
                        Gender gd = (Gender) dao.find(Gender.class, rs.getString("SEX"));
                        obj.setGenderId(gd);
                    }
                    List<Speciality> listSpec = dao.findAllHSQL(
                            "select o from Speciality o where o.desp = '" + rs.getString("SPECIAL") + "'");
                    if (listSpec != null) {
                        if (!listSpec.isEmpty()) {
                            obj.setSpeciality(listSpec.get(0));
                        }
                    }

                    List<Initial> listIna = dao.findAllHSQL(
                            "select o from Initial o where o.initialName = '" + rs.getString("INITIAL_NAME") + "'");
                    if (listIna != null) {
                        if (!listIna.isEmpty()) {
                            obj.setInitialID(listIna.get(0));
                        }
                    }

                    dao.save(obj);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processDoctorData : " + str + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void processSaleData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    double discAmt = rs.getDouble("DISCOUNT_AMT");
                    double paidAmt = rs.getDouble("PAID_AMT");
                    double vouTotal = rs.getDouble("INV_TOTAL");
                    vouEngine = new GenVouNoImpl(dao, "Sale",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("SALE_DATE"))));
                    SaleHis sh = new SaleHis();

                    sh.setBalance(vouTotal - (discAmt + paidAmt));
                    sh.setCreatedBy(hmUser.get(1));
                    sh.setCurrencyId(curr);
                    sh.setDiscount(discAmt);
                    Doctor doctor = (Doctor) dao.find(Doctor.class, rs.getString("DOCTOR_ID"));
                    sh.setDoctor(doctor);
                    sh.setLocationId(hmLocation.get(rs.getInt("LOCATION_ID")));
                    sh.setMigId(rs.getString("SALE_INV_ID"));
                    sh.setPaid(paidAmt);
                    Patient pd = (Patient) dao.find(Patient.class, rs.getString("U_PREG_NO"));
                    sh.setPatientId(pd);
                    sh.setRemark(rs.getString("REMARK"));
                    sh.setSaleDate(rs.getDate("SALE_DATE"));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setSaleInvId(vouNo);
                    sh.setSession(hmSession.get(rs.getInt("SESSION_ID")));
                    sh.setVouStatus(vs);
                    sh.setVouTotal(vouTotal);
                    sh.setDiscP(0.0);
                    sh.setDiscount(0.0);
                    sh.setTaxP(0.0);
                    sh.setTaxAmt(0.0);

                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, SDH.SALE_PRICE, SDH.QTY_IN_SMALLEST1, UT.UNIT_SHORT_SYMBOL, REPLACE(SDH.QTY_IN_STR1,UT.UNIT_SHORT_SYMBOL,'') UNIT_QTY \n"
                            + "FROM SALE_DETAIL_HIS SDH, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE SDH.MEDICINE_ID = MED.MED_ID AND SDH.SALE_UNIT_ID = UT.UNIT_TYPE_ID  AND SDH.SALE_ID = " + rs.getLong("SALE_ID")
                            + " ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<SaleDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        SaleDetailHis sdh = new SaleDetailHis();
                        sdh.setAmount(rsDetail.getDouble("SALE_PRICE")
                                * rsDetail.getInt("UNIT_QTY"));
                        sdh.setChargeId(ct);
                        sdh.setLocation(hmLocation.get(rs.getInt("LOCATION_ID")));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMedId(med);
                        sdh.setPrice(rsDetail.getDouble("SALE_PRICE"));
                        sdh.setQuantity(rsDetail.getFloat("UNIT_QTY"));
                        sdh.setSaleSmallestQty(rsDetail.getFloat("QTY_IN_SMALLEST1"));
                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnitId(iu);
                        uniqueId++;

                        listSDH.add(sdh);
                    }
                    sh.setSaleDetailHis(listSDH);
                    rsDetail.close();
                    stmtDetail.close();

                    dao.save(sh);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("SALE_INV_ID") + "@" + rs.getDate("SALE_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("SALE_INV_ID") + "@" + rs.getDate("SALE_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processSaleData : " + ex.getMessage());
            log.error("processSaleData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processPurchaseData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    double vouTotal = 0.0;
                    PurHis sh = new PurHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, PDH.PUR_PRICE, PDH.PUR_PRICE1, PDH.QTY_IN_SMALLEST, PDH.TOTAL_PRICE, PDH.DIS_PERCENT, PDH.QTY_IN_UNIT, UT.UNIT_SHORT_SYMBOL\n"
                            + "FROM PUR_DETAIL_HIS PDH, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE PDH.MEDICINE_ID = MED.MED_ID AND PDH.UNIT_TYPE_ID = UT.UNIT_TYPE_ID AND PDH.PUR_ID = "
                            + rs.getString("PUR_ID") + " \n"
                            + "ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<PurDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        PurDetailHis sdh = new PurDetailHis();
                        sdh.setAmount(rsDetail.getDouble("TOTAL_PRICE"));
                        sdh.setDiscount1(rs.getDouble("DIS_PERCENT"));
                        sdh.setChargeId(ct);
                        sdh.setLocation(hmLocation.get(1));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMedId(med);
                        sdh.setPrice(rsDetail.getDouble("PUR_PRICE"));
                        sdh.setQuantity(rsDetail.getFloat("QTY_IN_UNIT"));
                        sdh.setPurSmallestQty(rsDetail.getFloat("QTY_IN_SMALLEST"));
                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnitId(iu);
                        sdh.setUnitCost(rsDetail.getDouble("PUR_PRICE1"));

                        vouTotal += sdh.getAmount();
                        uniqueId++;
                        listSDH.add(sdh);
                    }
                    sh.setPurDetailHis(listSDH);

                    rsDetail.close();
                    stmtDetail.close();

                    double discAmt = rs.getDouble("DISCOUNT_AMT");
                    double paidAmt = rs.getDouble("PAID_AMT");

                    vouEngine = new GenVouNoImpl(dao, "Purchase",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("P_DATE"))));

                    sh.setBalance(vouTotal - (discAmt + paidAmt));
                    sh.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    sh.setCurrency(curr);
                    sh.setDiscount(discAmt);
                    sh.setLocationId(hmLocation.get(1));
                    sh.setMigId(rs.getString("PUR_ID"));
                    sh.setPaid(paidAmt);
                    sh.setPurDate(rs.getDate("P_DATE"));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setPurInvId(vouNo);
                    sh.setVouTotal(vouTotal);
                    sh.setDiscP(0.0);
                    sh.setDiscount(0.0);
                    sh.setTaxP(0.0);
                    sh.setTaxAmt(0.0);
                    Trader trader = getSupplier(rs.getInt("SUPPLIER_ID"));
                    sh.setCustomerId(trader);

                    //dao.save(sh);
                    //vouEngine.updateVouNo();
                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("P_INV_ID") + "@" + rs.getDate("P_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("P_INV_ID") + "@" + rs.getDate("P_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processPurchaseData : " + ex.getMessage());
            log.error("processPurchaseData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processSupplierData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    Supplier sup = new Supplier();
                    String traderId = getSupplierId();
                    sup.setTraderId(traderId);
                    sup.setActive(true);
                    sup.setCreatedBy(Global.loginUser.getUserId());
                    sup.setCreatedDate(DateUtil.getTodayDateTime());
                    sup.setMigId(rs.getInt("SUPPLIER_ID"));
                    sup.setTraderName(getZawgyiText(rs.getString("SUPPLIER_NAME")));
                    sup.setAddress(rs.getString("SUP_ADDRESS"));
                    sup.setPhone(rs.getString("SUP_PHONE"));

                    dao.save(sup);
                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("SUPPLIER_ID") + "@" + rs.getString("SUPPLIER_NAME") + "\n");
                } catch (Exception ex) {
                    log.error("Insert error : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("SUPPLIER_ID") + "@" + rs.getString("SUPPLIER_NAME") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex1) {
            lblStatus.setText("processSupplierData : " + ex1.getMessage());
            log.error("processSupplierData : " + ex1);
        }
    }

    private void processCustomerData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    String cusCode = "CUS" + rs.getString("CUS_CODE");
                    Customer tmpCus = (Customer) dao.find(Customer.class, cusCode);
                    if (tmpCus != null) {
                        log.info("Existing Customer : " + cusCode + " : " + tmpCus.getTraderName());
                    } else {
                        Customer cus = new Customer();
                        String strCG = rs.getString("G_CODE");
                        CustomerGroup cg = (CustomerGroup) dao.find(CustomerGroup.class, strCG);
                        cus.setTraderGroup(cg);
                        List<Township> listTS = dao.findAllHSQL("select o from Township o where o.migId = "
                                + rs.getString("TOWNSHIP_ID"));
                        Township ts = null;
                        if (listTS != null) {
                            if (!listTS.isEmpty()) {
                                ts = listTS.get(0);
                            }
                        }

                        cus.setTownship(ts);
                        cus.setTraderId("CUS" + rs.getString("CUS_CODE"));
                        cus.setTraderName(getZawgyiText(rs.getString("CUS_NAME")));
                        cus.setMigId(rs.getInt("CUS_ID"));
                        cus.setTypeId(hmTraderType.get(rs.getString("PRICE_TYPE")));
                        cus.setCreditDays(rs.getInt("CR_DAYS"));
                        cus.setCreditLimit(rs.getInt("CR_LIMIT"));
                        cus.setCreatedBy(Global.loginUser.getUserId());
                        cus.setCreatedDate(DateUtil.getTodayDateTime());
                        dao.save(cus);

                        ttlSuccRecords++;
                        lblSuccessTtl.setText(ttlSuccRecords.toString());
                        txaSuccessData.append(rs.getString("CUS_CODE") + "@" + rs.getString("CUS_NAME") + "\n");
                        log.info(cus.getTraderId() + "-" + rs.getString("PRICE_TYPE"));
                    }
                } catch (Exception ex) {
                    log.error("Insert error : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("CUS_CODE") + "@" + rs.getString("CUS_NAME") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex1) {
            lblStatus.setText("processCustomerData : " + ex1.getMessage());
            log.error("processCustomerData : " + ex1);
        }
    }

    private void processCustomerOpening() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            if (rs != null) {
                while (rs.next()) {
                    Trader trader = (Trader) dao.find(Trader.class, rs.getString("CUS_CODE"));
                    String cusName = getZawgyiText(rs.getString("CUS_NAME"));
                    if (trader != null) {
                        if (cusName.equals(trader.getTraderName())) {
                            CompoundKeyTraderOp key = new CompoundKeyTraderOp();
                            key.setCurrency(curr);
                            key.setOpDate(DateUtil.toDate("2020-11-16", "yyyy-MM-dd"));
                            key.setTrader(trader);
                            TraderOpening tOpening = new TraderOpening();
                            tOpening.setKey(key);
                            Double amount = rs.getDouble("CL_BAL");
                            tOpening.setAmount(amount);

                            try {
                                dao.save(tOpening);
                            } catch (Exception ex) {
                                log.error(ex.getMessage());
                            }
                        } else {
                            log.info("processCustomerOpening : duplicate : "
                                    + rs.getString("CUS_CODE") + " - " + cusName);
                        }
                    } else {
                        String strSql = "SELECT GET_CUS_GRP_CODE(CUS_CODE) AS G_CODE,CUS_CODE,CUS_NAME,CUS_ID,\n"
                                + "CUS_TYPE_ID, DECODE(CUS_TYPE_ID,1,'A',2,'B',3,'C') PRICE_TYPE, CR_LIMIT,\n"
                                + "CR_DAYS, TOWNSHIP_ID\n"
                                + "FROM CUSTOMER where CUS_ID = " + rs.getString("CUS_ID");
                        Statement stmtCus = con.createStatement();
                        ResultSet rsCus = stmtCus.executeQuery(strSql);
                        rsCus.next();
                        
                        Customer cus = new Customer();
                        String strCG = rsCus.getString("G_CODE");
                        CustomerGroup cg = (CustomerGroup) dao.find(CustomerGroup.class, strCG);
                        cus.setTraderGroup(cg);
                        List<Township> listTS = dao.findAllHSQL("select o from Township o where o.migId = "
                                + rsCus.getString("TOWNSHIP_ID"));
                        Township ts = null;
                        if (listTS != null) {
                            if (!listTS.isEmpty()) {
                                ts = listTS.get(0);
                            }
                        }

                        cus.setTownship(ts);
                        cus.setTraderId("CUS" + rsCus.getString("CUS_CODE"));
                        cus.setTraderName(getZawgyiText(rsCus.getString("CUS_NAME")));
                        cus.setMigId(rsCus.getInt("CUS_ID"));
                        cus.setTypeId(hmTraderType.get(rsCus.getString("PRICE_TYPE")));
                        cus.setCreditDays(rsCus.getInt("CR_DAYS"));
                        cus.setCreditLimit(rsCus.getInt("CR_LIMIT"));
                        cus.setCreatedBy(Global.loginUser.getUserId());
                        cus.setCreatedDate(DateUtil.getTodayDateTime());
                        dao.save(cus);

                        CompoundKeyTraderOp key = new CompoundKeyTraderOp();
                        key.setCurrency(curr);
                        key.setOpDate(DateUtil.toDate("2020-11-16", "yyyy-MM-dd"));
                        key.setTrader(cus);
                        TraderOpening tOpening = new TraderOpening();
                        tOpening.setKey(key);
                        Double amount = rs.getDouble("CL_BAL");
                        tOpening.setAmount(amount);

                        try {
                            dao.save(tOpening);
                        } catch (Exception ex) {
                            log.error(ex.getMessage());
                        }
                        
                        rsCus.close();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("processCustomerOpening : " + ex.toString());
        }
    }

    private void processRetInData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    double vouTotal = 0.0;
                    RetInHis sh = new RetInHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, RIDH.QTY_IN_SMALLEST1, UT.UNIT_SHORT_SYMBOL, RIDH.SALE_PRICE, RIDH.TOTAL_PRICE \n"
                            + "FROM RET_IN_DETAIL_HIS RIDH, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE RIDH.MEDICINE_ID = MED.MED_ID AND RIDH.UNIT_TYPE_ID1 = UT.UNIT_TYPE_ID "
                            + "AND RIDH.RET_IN_INV_ID = '" + rs.getString("RET_IN_INV_ID") + "' "
                            + "ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<RetInDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        RetInDetailHis sdh = new RetInDetailHis();
                        sdh.setAmount(rsDetail.getDouble("TOTAL_PRICE"));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMedicineId(med);
                        sdh.setPrice(rsDetail.getDouble("SALE_PRICE"));
                        sdh.setQty(rsDetail.getFloat("QTY_IN_SMALLEST1"));
                        sdh.setSmallestQty(rsDetail.getFloat("QTY_IN_SMALLEST1"));
                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnit(iu);
                        vouTotal += sdh.getAmount();
                        uniqueId++;
                        listSDH.add(sdh);
                    }
                    sh.setListDetail(listSDH);

                    rsDetail.close();
                    stmtDetail.close();

                    double paidAmt = rs.getDouble("PAID_AMT");

                    vouEngine = new GenVouNoImpl(dao, "RetIn",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("RET_DATE"))));

                    sh.setBalance(vouTotal - paidAmt);
                    sh.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    sh.setCurrency(curr);
                    sh.setLocation(hmLocation.get(rs.getInt("LOCATION_ID")));
                    sh.setMigId(rs.getString("P_INV_ID"));
                    sh.setPaid(paidAmt);
                    sh.setRetInDate(rs.getDate("RET_DATE"));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setRetInId(vouNo);
                    sh.setVouTotal(vouTotal);
                    sh.setSession(hmSession.get(rs.getInt("SESSION_ID")));
                    Patient pd = (Patient) dao.find(Patient.class, rs.getString("U_PREG_NO"));
                    sh.setPatient(pd);
                    dao.save(sh);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("RET_IN_INV_ID") + "@" + rs.getDate("RET_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("RET_IN_INV_ID") + "@" + rs.getDate("RET_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processReturnInData : " + ex.getMessage());
            log.error("processReturnInData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processRetOutData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    double vouTotal = 0.0;
                    RetOutHis sh = new RetOutHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, RODH.R_PRICE, UT.UNIT_SHORT_SYMBOL, "
                            + "RODH.TOTAL_PRICE, RODH.QTY_IN_SMALLEST\n"
                            + "FROM RET_OUT_DETAIL_HIS RODH, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE RODH.MEDICINE_ID = MED.MED_ID AND RODH.UNIT_TYPE_ID = UT.UNIT_TYPE_ID "
                            + "AND R_INV_ID = '" + rs.getString("R_INV_ID") + "' "
                            + "ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<RetOutDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        RetOutDetailHis sdh = new RetOutDetailHis();
                        sdh.setAmount(rsDetail.getDouble("TOTAL_PRICE"));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMedicineId(med);
                        sdh.setPrice(rsDetail.getDouble("R_PRICE"));
                        sdh.setQty(rsDetail.getFloat("QTY_IN_SMALLEST"));
                        sdh.setSmallestQty(rsDetail.getFloat("QTY_IN_SMALLEST"));
                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnit(iu);

                        vouTotal += sdh.getAmount();
                        uniqueId++;
                        listSDH.add(sdh);
                    }
                    sh.setListDetail(listSDH);

                    rsDetail.close();
                    stmtDetail.close();

                    double paidAmt = rs.getDouble("PAID_AMT");
                    vouEngine = new GenVouNoImpl(dao, "RetOut",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("R_DATE"))));

                    sh.setBalance(vouTotal - paidAmt);
                    sh.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    sh.setCurrency(curr);
                    sh.setLocation(hmLocation.get(rs.getInt("LOCATION_ID")));
                    sh.setMigId(rs.getString("R_INV_ID"));
                    sh.setPaid(paidAmt);
                    sh.setRetOutDate(rs.getDate("R_DATE"));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setRetOutId(vouNo);
                    sh.setVouTotal(vouTotal);
                    Trader trader = getSupplier(rs.getInt("SUPPLIER_ID"));
                    sh.setCustomer(trader);

                    dao.save(sh);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("R_INV_ID") + "@" + rs.getDate("R_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("R_INV_ID") + "@" + rs.getDate("R_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processRetOutData : " + ex.getMessage());
            log.error("processRetOutData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processDamageData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    DamageHis sh = new DamageHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, UT.UNIT_SHORT_SYMBOL, DDH.QTY_IN_SMALLEST\n"
                            + "FROM DMG_DETAIL_HIS DDH, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE DDH.MEDICINE_ID = MED.MED_ID AND DDH.UNIT_TYPE_ID = UT.UNIT_TYPE_ID "
                            + "AND DMG_INV_ID = '" + rs.getString("DMG_INV_ID") + "' "
                            + "ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<DamageDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        DamageDetailHis sdh = new DamageDetailHis();
                        //sdh.setAmount(rsDetail.getDouble("TOTAL_PRICE"));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMedicineId(med);
                        //sdh.setPrice(rsDetail.getDouble("R_PRICE"));
                        sdh.setQty(rsDetail.getFloat("QTY_IN_SMALLEST"));
                        sdh.setSmallestQty(rsDetail.getFloat("QTY_IN_SMALLEST"));
                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnit(iu);
                        uniqueId++;
                        listSDH.add(sdh);
                    }
                    sh.setListDetail(listSDH);

                    rsDetail.close();
                    stmtDetail.close();

                    //double paidAmt = rs.getDouble("PAID_AMT");
                    vouEngine = new GenVouNoImpl(dao, "Damage",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("DMG_DATE"))));

                    //sh.setBalance(vouTotal - paidAmt);
                    sh.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    sh.setLocation(hmLocation.get(rs.getInt("LOCATION_ID")));
                    sh.setMigId(rs.getString("DMG_INV_ID"));
                    sh.setDmgDate(rs.getDate("DMG_DATE"));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setDmgVouId(vouNo);
                    sh.setDeleted(Boolean.FALSE);

                    dao.save(sh);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("DMG_INV_ID") + "@" + rs.getDate("DMG_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("DMG_INV_ID") + "@" + rs.getDate("DMG_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processDamageData : " + ex.getMessage());
            log.error("processDamageData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processAdjData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    AdjHis sh = new AdjHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, UT.UNIT_SHORT_SYMBOL, SADH.QTY_IN_SMALLEST\n"
                            + "FROM STOCK_ADJ_DETAIL_HIS SADH, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE SADH.MEDICINE_ID = MED.MED_ID AND SADH.UNIT_TYPE_ID = UT.UNIT_TYPE_ID "
                            + "AND ADJ_INV_ID = '" + rs.getString("ADJ_INV_ID") + "' "
                            + "ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<AdjDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        AdjDetailHis sdh = new AdjDetailHis();
                        //sdh.setAmount(rsDetail.getDouble("TOTAL_PRICE"));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMedicineId(med);
                        //sdh.setCostPrice(rsDetail.getDouble("R_PRICE"));
                        float qty = rsDetail.getFloat("QTY_IN_SMALLEST");
                        if (qty < 0) {
                            sdh.setQty(qty * -1);
                            sdh.setSmallestQty(qty * -1);
                            sdh.setAdjType(atM);
                        } else {
                            sdh.setQty(qty);
                            sdh.setSmallestQty(qty);
                            sdh.setAdjType(atP);
                        }

                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnit(iu);
                        uniqueId++;
                        listSDH.add(sdh);
                    }
                    sh.setListDetail(listSDH);

                    rsDetail.close();
                    stmtDetail.close();

                    //double paidAmt = rs.getDouble("PAID_AMT");
                    vouEngine = new GenVouNoImpl(dao, "Adjust",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("ADJ_DATE"))));

                    //sh.setBalance(vouTotal - paidAmt);
                    sh.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    sh.setLocation(hmLocation.get(rs.getInt("LOCATION_ID")));
                    sh.setMigId(rs.getString("ADJ_INV_ID"));
                    sh.setAdjDate(rs.getDate("ADJ_DATE"));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setAdjVouId(vouNo);
                    sh.setDeleted(Boolean.FALSE);

                    dao.save(sh);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("ADJ_INV_ID") + "@" + rs.getDate("ADJ_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("ADJ_INV_ID") + "@" + rs.getDate("ADJ_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processAdjData : " + ex.getMessage());
            log.error("processAdjData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processTransferData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    TransferHis sh = new TransferHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, UT.UNIT_SHORT_SYMBOL, TDH.QTY_IN_SMALLEST, "
                            + "TDH.SALE_PRICE, TDH.TOTAL_PRICE\n"
                            + "FROM TRAN_DETAIL_HIS TDH, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE TDH.MEDICINE_ID = MED.MED_ID AND TDH.UNIT_TYPE_ID = UT.UNIT_TYPE_ID\n"
                            + "AND TRAN_INV_ID = '" + rs.getString("TRAN_INV_ID") + "' "
                            + "ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<TransferDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;
                    double totalAmount = 0;

                    while (rsDetail.next()) {
                        TransferDetailHis sdh = new TransferDetailHis();
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMedicineId(med);
                        //sdh.setCostPrice(rsDetail.getDouble("R_PRICE"));
                        float qty = rsDetail.getFloat("QTY_IN_SMALLEST");
                        sdh.setQty(qty);
                        sdh.setSmallestQty(qty);
                        sdh.setPrice(rsDetail.getDouble("SALE_PRICE"));
                        sdh.setAmount(rsDetail.getDouble("TOTAL_PRICE"));
                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnit(iu);

                        totalAmount += NumberUtil.NZero(rsDetail.getDouble("TOTAL_PRICE"));
                        uniqueId++;
                        listSDH.add(sdh);
                    }
                    sh.setListDetail(listSDH);

                    rsDetail.close();
                    stmtDetail.close();

                    //double paidAmt = rs.getDouble("PAID_AMT");
                    vouEngine = new GenVouNoImpl(dao, "Transfer",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("TRAN_DATE"))));

                    //sh.setBalance(vouTotal - paidAmt);
                    sh.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    sh.setFromLocation(hmLocation.get(rs.getInt("FRM_LOCATION_ID")));
                    sh.setToLocation(hmLocation.get(rs.getInt("TO_LOCATION_ID")));
                    sh.setMigId(rs.getString("TRAN_INV_ID"));
                    sh.setTranDate(rs.getDate("TRAN_DATE"));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setTranVouId(vouNo);
                    sh.setDeleted(Boolean.FALSE);
                    sh.setVouTotal(totalAmount);
                    dao.save(sh);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("TRAN_INV_ID") + "@" + rs.getDate("TRAN_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("TRAN_INV_ID") + "@" + rs.getDate("TRAN_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processTransferData : " + ex.getMessage());
            log.error("processTransferData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processPatientData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                rs.setFetchSize(30000);
                try {
                    Patient p = new Patient();
                    p.setAddress(getZawgyiText(rs.getString("ADDRESS")));
                    p.setAge(rs.getInt("AGE"));

                    City city = (City) dao.find(City.class, rs.getInt("TSP_ID"));
                    p.setCity(city);

                    Township tsp = (Township) dao.find(Township.class, rs.getInt("CITY_ID"));
                    p.setTownship(tsp);

                    p.setContactNo(rs.getString("PHONE"));
                    p.setCreatedBy(Global.loginUser.getUserId());
                    Doctor doctor = (Doctor) dao.find(Doctor.class, rs.getString("DOCTOR_ID"));
                    p.setDoctor(doctor);
                    p.setFatherName(getZawgyiText(rs.getString("FATHER_NAME")));
                    p.setPatientName(getZawgyiText(rs.getString("PATIENT_NAME")));
                    p.setRegDate(rs.getDate("REG_DATE"));
                    p.setRegNo(rs.getString("U_PREG_NO"));
                    Gender sex = (Gender) dao.find(Gender.class, rs.getString("SEX"));
                    p.setSex(sex);

                    dao.save(p);
                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("U_PREG_NO") + "@" + rs.getString("PATIENT_NAME") + "\n");
                    log.info(ttlSuccRecords + " : " + rs.getString("U_PREG_NO") + "@" + rs.getString("PATIENT_NAME"));
                } catch (Exception ex) {
                    log.error("Insert error : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("U_PREG_NO") + "@" + rs.getString("PATIENT_NAME") + "\n");
                }
                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex1) {
            lblStatus.setText("processPatientData : " + ex1.getMessage());
            log.error("processPatientData : " + ex1);
        }
    }

    private void processOPDData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    double vouTotal = 0.0;
                    OPDHis h = new OPDHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT LAB_TEST_CODE, QTY, FEE, (QTY*FEE) AMOUNT "
                            + "FROM LAB_TEST_HIS WHERE LAB_ID = " + rs.getString("LAB_ID") + " ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<OPDDetailHis> listDetail = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        OPDDetailHis d = new OPDDetailHis();
                        d.setAmount(rsDetail.getDouble("AMOUNT"));
                        d.setChargeType(ct);
                        Service service = getOPDService(rsDetail.getInt("LAB_TEST_CODE"));
                        d.setService(service);
                        d.setFeesVersionId(service.getPriceVersionId());
                        d.setPrice(rsDetail.getDouble("FEE"));
                        d.setQuantity(rsDetail.getInt("QTY"));
                        d.setUniqueId(uniqueId);
                        vouTotal += d.getAmount();
                        uniqueId++;
                        listDetail.add(d);
                    }
                    h.setListOPDDetailHis(listDetail);

                    rsDetail.close();
                    stmtDetail.close();

                    double discAmt = rs.getDouble("DISCOUNT");
                    double paidAmt = rs.getDouble("PAID");
                    vouEngine = new GenVouNoImpl(dao, "OPD",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("LAB_DATE"))));

                    h.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    h.setCreatedDate(DateUtil.getTodayDateTime());
                    h.setCurrency(curr);
                    h.setDeleted(false);
                    h.setDiscountA(discAmt);
                    h.setDiscountP(0.0);
                    Doctor doctor = (Doctor) dao.find(Doctor.class, rs.getString("DOCTOR_ID"));
                    h.setDoctor(doctor);
                    h.setInvDate(rs.getDate("LAB_DATE"));
                    h.setMigId(rs.getString("LAB_VOU_NO"));
                    String vouNo = vouEngine.getVouNo1();
                    h.setOpdInvId(vouNo);
                    h.setPaid(paidAmt);
                    Patient patient = (Patient) dao.find(Patient.class, rs.getString("U_PREG_NO"));
                    h.setPatient(patient);
                    h.setPatientName(rs.getString("PNAME"));
                    h.setPaymentType(hmBillTo.get(rs.getInt("BILL_TO")));
                    h.setRemark(rs.getString("REMARK"));
                    h.setSession(hmSession.get(rs.getInt("SESSION_ID")));
                    h.setTaxA(0.0);
                    h.setTaxP(0.0);
                    h.setVouBalance(vouTotal - (discAmt + paidAmt));
                    h.setVouTotal(vouTotal);

                    dao.save(h);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("LAB_VOU_NO") + "@" + rs.getDate("LAB_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("LAB_VOU_NO") + "@" + rs.getDate("LAB_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processOPDData : " + ex.getMessage());
            log.error("processOPDData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processOTData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    double vouTotal = 0.0;
                    OTHis h = new OTHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT OT_PROC_ID, QTY, FEE, (QTY*FEE) AMOUNT\n"
                            + "FROM OT_PROC_HIS WHERE OT_ID = " + rs.getLong("OT_ID")
                            + " ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<OTDetailHis> listDetail = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        OTDetailHis d = new OTDetailHis();
                        d.setAmount(rsDetail.getDouble("AMOUNT"));
                        d.setChargeType(ct);
                        OTProcedure service = (OTProcedure) dao.find(OTProcedure.class, rsDetail.getInt("OT_PROC_ID"));
                        d.setService(service);
                        d.setPrice(rsDetail.getDouble("FEE"));
                        d.setQuantity(rsDetail.getInt("QTY"));
                        d.setSrvFee1(service.getSrvFees1());
                        d.setSrvFee2(service.getSrvFees2());
                        d.setSrvFee3(service.getSrvFees3());
                        d.setSrvFee4(service.getSrvFees4());
                        d.setSrvFee5(service.getSrvFees5());
                        d.setUniqueId(uniqueId);
                        vouTotal += d.getAmount();
                        uniqueId++;
                        listDetail.add(d);
                    }
                    h.setListOPDDetailHis(listDetail);

                    rsDetail.close();
                    stmtDetail.close();

                    double discAmt = rs.getDouble("DISCOUNT");
                    double paidAmt = rs.getDouble("PAID_AMT");
                    vouEngine = new GenVouNoImpl(dao, "OT",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("OT_DATE"))));

                    h.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    h.setCreatedDate(DateUtil.getTodayDateTime());
                    h.setCurrency(curr);
                    h.setDeleted(false);
                    h.setDiscountA(discAmt);
                    h.setDiscountP(0.0);
                    Doctor doctor = (Doctor) dao.find(Doctor.class, rs.getString("SURGEN_ID"));
                    h.setDoctor(doctor);
                    h.setInvDate(rs.getDate("OT_DATE"));
                    h.setMigId(rs.getString("U_OT_ID"));
                    String vouNo = vouEngine.getVouNo1();
                    h.setOpdInvId(vouNo);
                    h.setPaid(paidAmt);
                    Patient patient = (Patient) dao.find(Patient.class, rs.getString("U_PREG_NO"));
                    h.setPatient(patient);
                    //h.setPatientName(rs.getString("PNAME"));
                    h.setPaymentType(hmBillTo.get(rs.getInt("BILL_TO")));
                    h.setRemark(rs.getString("REMARK"));
                    h.setSession(hmSession.get(rs.getInt("SESSION_ID")));
                    h.setTaxA(0.0);
                    h.setTaxP(0.0);
                    h.setVouBalance(vouTotal - (discAmt + paidAmt));
                    h.setVouTotal(vouTotal);

                    dao.save(h);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("U_OT_ID") + "@" + rs.getDate("OT_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("U_OT_ID") + "@" + rs.getDate("OT_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processOTData : " + ex.getMessage());
            log.error("processOTData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processDCData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    double vouTotal = 0.0;
                    DCHis h = new DCHis();
                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT WARD_PROC_ID, QTY, FEES, (QTY*FEES) AMOUNT "
                            + "FROM DC_PROC_HIS WHERE ISERVICE_ID = " + rs.getLong("ISERVICE_ID")
                            + " ORDER BY UNIQUE_ID";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<DCDetailHis> listDetail = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        DCDetailHis d = new DCDetailHis();
                        d.setAmount(rsDetail.getDouble("AMOUNT"));
                        d.setChargeType(ct);
                        InpService service = (InpService) dao.find(InpService.class, rsDetail.getInt("WARD_PROC_ID"));
                        d.setService(service);
                        //d.setFeesVersionId(service.getPriceVersionId());
                        d.setPrice(rsDetail.getDouble("FEE"));
                        d.setQuantity(rsDetail.getInt("QTY"));
                        d.setSrvFee1(service.getFees1());
                        d.setSrvFee2(service.getFees2());
                        d.setSrvFee3(service.getFees3());
                        d.setSrvFee4(service.getFees4());
                        d.setSrvFee5(service.getFees5());
                        d.setUniqueId(uniqueId);
                        vouTotal += d.getAmount();
                        uniqueId++;
                        listDetail.add(d);
                    }
                    h.setListOPDDetailHis(listDetail);

                    rsDetail.close();
                    stmtDetail.close();

                    double discAmt = rs.getDouble("DISCOUNT");
                    double paidAmt = rs.getDouble("PAID");
                    vouEngine = new GenVouNoImpl(dao, "DC",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("IDATE"))));

                    h.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    h.setCreatedDate(DateUtil.getTodayDateTime());
                    h.setCurrency(curr);
                    h.setDeleted(false);
                    h.setDiscountA(discAmt);
                    h.setDiscountP(0.0);
                    Doctor doctor = (Doctor) dao.find(Doctor.class, rs.getString("DOCTOR_ID"));
                    h.setDoctor(doctor);
                    h.setInvDate(rs.getDate("IDATE"));
                    h.setMigId(rs.getString("VOU_NO"));
                    String vouNo = vouEngine.getVouNo1();
                    h.setOpdInvId(vouNo);
                    h.setPaid(paidAmt);
                    Patient patient = (Patient) dao.find(Patient.class, rs.getString("U_PREG_NO"));
                    h.setPatient(patient);
                    //h.setPatientName(rs.getString("PNAME"));
                    h.setPaymentType(hmBillTo.get(rs.getInt("BILL_TO")));
                    //h.setRemark(rs.getString("REMARK"));
                    h.setSession(hmSession.get(rs.getInt("SESSION_ID")));
                    h.setTaxA(0.0);
                    h.setTaxP(0.0);
                    h.setVouBalance(vouTotal - (discAmt + paidAmt));
                    h.setVouTotal(vouTotal);

                    dao.save(h);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("VOU_NO") + "@" + rs.getDate("IDATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("VOU_NO") + "@" + rs.getDate("IDATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processDCData : " + ex.getMessage());
            log.error("processDCData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processOPDSetupData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    OPDCategory opdc = new OPDCategory();
                    opdc.setCatName(getZawgyiText(rs.getString("LAB_CLASS_DESP")));
                    opdc.setGroupId(rs.getInt("SOURCE_ID"));
                    opdc.setMigId(rs.getInt("LAB_CLASS_ID"));
                    dao.save(opdc);

                    /*if(opdc.getCatId() == 16 || opdc.getCatId() == 3){
                     System.out.println("Error check");
                     }*/
                    try {
                        Statement stDetail = con.createStatement();
                        String sqlDetail = "SELECT LAB_CLASS_ID, LAB_TEST_CODE, LAB_TEST_DESP, FEES, NORMAL,\n"
                                + "SRV_FEES, MO_FEES, STAFF_FEES, TECH_FEES, READ_FEES, REFER_FEES, PERCENT "
                                + "FROM LAB_TEST_CODE WHERE LAB_CLASS_ID = " + rs.getString("LAB_CLASS_ID");
                        ResultSet rsDetail = stDetail.executeQuery(sqlDetail);
                        while (rsDetail.next()) {
                            Service srv = new Service();
                            srv.setCatId(opdc.getCatId());
                            srv.setFees(rsDetail.getDouble("FEES"));
                            //srv.setServiceCode(rsDetail.getString("NORMAL"));
                            srv.setMigId(rsDetail.getInt("LAB_TEST_CODE"));
                            //srv.setServiceId(rsDetail.getInt("LAB_TEST_CODE"));
                            srv.setServiceName(getZawgyiText(rsDetail.getString("LAB_TEST_DESP")));
                            srv.setStatus(true);
                            srv.setFees1(rsDetail.getDouble("SRV_FEES"));
                            srv.setFees2(rsDetail.getDouble("MO_FEES"));
                            srv.setFees3(rsDetail.getDouble("STAFF_FEES"));
                            srv.setFees4(rsDetail.getDouble("TECH_FEES"));
                            srv.setFees5(rsDetail.getDouble("REFER_FEES"));
                            srv.setFees6(rsDetail.getDouble("READ_FEES"));
                            srv.setPercent(rsDetail.getBoolean("PERCENT"));
                            dao.save(srv);

                            ttlSuccRecords++;
                            lblSuccessTtl.setText(ttlSuccRecords.toString());
                            txaSuccessData.append(rs.getString("LAB_CLASS_ID") + "@" + rsDetail.getString("LAB_TEST_CODE") + "\n");
                        }
                    } catch (Exception ex1) {
                        log.error("service : " + rs.getString("LAB_CLASS_ID") + "@" + rs.getString("LAB_CLASS_DESP") + "\n" + ex1);
                        ttlErrRecords++;
                        lblErrorDataTtl.setText(ttlErrRecords.toString());
                        txaErrorData.append(rs.getString("LAB_CLASS_ID") + "@" + rs.getString("LAB_CLASS_DESP") + "\n");
                    }
                } catch (Exception ex) {
                    log.error("Insert error : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("LAB_CLASS_ID") + "@" + rs.getString("LAB_CLASS_DESP") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex1) {
            lblStatus.setText("processOPDSetupData : " + ex1.getMessage());
            log.error("processOPDSetupData : " + ex1);
        }
    }

    private void processDCSetupData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    InpCategory grp = new InpCategory();
                    grp.setCatName(getZawgyiText(rs.getString("TYPE_DESP")));
                    grp.setImgId(rs.getInt("TYPE_ID"));
                    grp.setSortOrder(rs.getInt("SORT_ORDER"));
                    dao.save(grp);

                    /*if(opdc.getCatId() == 16 || opdc.getCatId() == 3){
                     System.out.println("Error check");
                     }*/
                    try {
                        Statement stDetail = con.createStatement();
                        String sqlDetail = "SELECT WARD_PROC_ID, WARD_PROC_DESP, FEES, UHC_FEES, MO_FEES, STAFF_FEES, NURSE_FEES \n"
                                + "FROM WARD_PROCEDURES WHERE TYPE = " + rs.getString("TYPE_ID") + " ORDER BY WARD_PROC_DESP";
                        ResultSet rsDetail = stDetail.executeQuery(sqlDetail);
                        while (rsDetail.next()) {
                            InpService srv = new InpService();
                            srv.setCatId(grp.getCatId());
                            srv.setFees(rsDetail.getDouble("FEES"));
                            srv.setMigId(rsDetail.getInt("WARD_PROC_ID"));
                            srv.setServiceName(getZawgyiText(rsDetail.getString("WARD_PROC_DESP")));
                            srv.setStatus(true);
                            srv.setFees1(rsDetail.getDouble("UHC_FEES"));
                            srv.setFees2(rsDetail.getDouble("MO_FEES"));
                            srv.setFees3(rsDetail.getDouble("STAFF_FEES"));
                            srv.setFees4(rsDetail.getDouble("NURSE_FEES"));
                            dao.save(srv);

                            ttlSuccRecords++;
                            lblSuccessTtl.setText(ttlSuccRecords.toString());
                            txaSuccessData.append(rs.getString("TYPE_ID") + "@" + rsDetail.getString("WARD_PROC_DESP") + "\n");
                        }
                    } catch (Exception ex1) {
                        log.error("service : " + rs.getString("TYPE_ID") + "@" + rs.getString("WARD_PROC_DESP") + "\n" + ex1);
                        ttlErrRecords++;
                        lblErrorDataTtl.setText(ttlErrRecords.toString());
                        txaErrorData.append(rs.getString("TYPE_ID") + "@" + rs.getString("LAB_CLASS_DESP") + "\n");
                    }
                } catch (Exception ex) {
                    log.error("Insert error : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("TYPE_ID") + "@" + rs.getString("TYPE_DESP") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex1) {
            lblStatus.setText("processDCSetupData : " + ex1.getMessage());
            log.error("processDCSetupData : " + ex1);
        }
    }

    private void processOTSetupData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    OTProcedureGroup opdc = new OTProcedureGroup();
                    opdc.setGroupName(getZawgyiText(rs.getString("OT_TYPE_DESP")));
                    opdc.setMigId(rs.getInt("OT_TYPE_ID"));
                    opdc.setSortOrder(rs.getInt("SORT_ORDER"));
                    dao.save(opdc);

                    /*if(opdc.getCatId() == 16 || opdc.getCatId() == 3){
                     System.out.println("Error check");
                     }*/
                    try {
                        Statement stDetail = con.createStatement();
                        String sqlDetail = "SELECT OT_PROC_ID, DESPCRIPTION, FEES, UHC_FEES, STAFF_FEES, NURSAGE_FEES \n"
                                + " FROM OT_PROCEDURES WHERE OT_TYPE = " + rs.getString("OT_TYPE_ID") + " ORDER BY DESPCRIPTION";
                        ResultSet rsDetail = stDetail.executeQuery(sqlDetail);
                        while (rsDetail.next()) {
                            OTProcedure srv = new OTProcedure();
                            srv.setGroupId(opdc.getGroupId());
                            srv.setSrvFees(rsDetail.getDouble("FEES"));
                            srv.setMigId(rsDetail.getInt("OT_PROC_ID"));
                            srv.setServiceName(getZawgyiText(rsDetail.getString("DESPCRIPTION")));
                            srv.setStatus(true);
                            srv.setSrvFees1(rsDetail.getDouble("UHC_FEES"));
                            srv.setSrvFees2(rsDetail.getDouble("STAFF_FEES"));
                            srv.setSrvFees3(rsDetail.getDouble("NURSAGE_FEES"));
                            dao.save(srv);

                            ttlSuccRecords++;
                            lblSuccessTtl.setText(ttlSuccRecords.toString());
                            txaSuccessData.append(rs.getString("OT_TYPE_ID") + "@" + rsDetail.getString("DESPCRIPTION") + "\n");
                        }
                    } catch (Exception ex1) {
                        log.error("service : " + rs.getString("OT_TYPE_ID") + "@" + rs.getString("OT_TYPE_DESP") + "\n" + ex1);
                        ttlErrRecords++;
                        lblErrorDataTtl.setText(ttlErrRecords.toString());
                        txaErrorData.append(rs.getString("OT_TYPE_ID") + "@" + rs.getString("OT_TYPE_DESP") + "\n");
                    }
                } catch (Exception ex) {
                    log.error("Insert error : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("OT_TYPE_ID") + "@" + rs.getString("OT_TYPE_DESP") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex1) {
            lblStatus.setText("processOTSetupData : " + ex1.getMessage());
            log.error("processOTSetupData : " + ex1);
        }
    }

    private void processItemSetupData() {
        String strSql = "select o from Medicine o";
        List<Medicine> listMed = dao.findAllHSQL(strSql);

        for (Medicine med : listMed) {
            try {
                List<RelationGroup> listRG = med.getRelationGroupId();
                for (int i = 0; i < listRG.size(); i++) {
                    RelationGroup rg = listRG.get(i);
                    Statement stmt = con.createStatement();
                    String oraSql = "SELECT MP.PUR_PRICE, MP.UNIT_SHORT, MP.SALE_PRICE, MP.WS_PRICE1\n"
                            + "FROM MEDICINE MED, MEDICINE_PRICE MP\n"
                            + "WHERE MED.MED_ID = MP.MEDICINE_ID "
                            + "AND MED.SHORT_CODE = '" + med.getMedId() + "' "
                            //+ "AND MP.UNIT_SHORT = '" + getUnitCodeToOracle(rg.getUnitId().getItemUnitCode()) + "'";
                            + "AND MP.UNIT_SHORT = '" + rg.getUnitId().getItemUnitCode() + "'";
                    ResultSet rs = stmt.executeQuery(oraSql);

                    /*if (rs.next()) {
                        med.setPurPrice(rs.getDouble("PUR_PRICE"));
                        med.setPurUnit(rg.getUnitId());
                        med.getRelationGroupId().get(0).setSalePrice(rs.getDouble("SALE_PRICE"));
                        dao.save(med);
                    }*/
                    if (rs.next()) {
                        rg.setSalePriceA(rs.getDouble("WS_PRICE1"));
                    }
                    rs.close();
                    stmt.close();
                    //i = listRG.size();
                }
                dao.save(med);
            } catch (Exception ex) {
                log.error("processItemSetupData : " + ex.getStackTrace()[0].getLineNumber() + " - " + med.getMedId() + "@" + med.getMedName() + ex);
            }
        }

        /*dao.execSql("update transfer_detail_his tdh join medicine med on tdh.med_id = med.med_id\n"
                + "set tran_price = ifnull(med.pur_price,0), tdh.amount = tdh.tran_qty * ifnull(med.pur_price,0)");*/
    }

    private void processItemTypeData() {
        String strSql = "SELECT DRUG_TYPE_ID, DRUG_TYPE_NAME FROM DRUG_TYPE ORDER BY DRUG_TYPE_ID";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(strSql);

            while (rs.next()) {
                ItemType it = new ItemType();
                it.setItemTypeCode(rs.getString("DRUG_TYPE_ID"));
                it.setItemTypeName(rs.getString("DRUG_TYPE_NAME"));

                dao.save(it);
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processItemTypeData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void processCategoryData() {
        String strSql = "SELECT CHEMICAL_ID, CHEMICAL_DESP FROM CHEMICAL ORDER BY CHEMICAL_DESP";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(strSql);

            while (rs.next()) {
                if (dao.getRowCount("select count(cat_name) from category where cat_name='" + rs.getString("CHEMICAL_DESP") + "'") == 0) {
                    Category cat = new Category();
                    cat.setMigId(rs.getInt("CHEMICAL_ID"));
                    cat.setCatName(rs.getString("CHEMICAL_DESP"));
                    dao.save(cat);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processCategoryData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void processBrandNameData() {
        String strSql = "SELECT MANUFACTURER_ID, MANUF_NAME FROM MANUFACTURER ORDER BY MANUF_NAME";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(strSql);

            while (rs.next()) {
                if (dao.getRowCount("select count(brand_name) from item_brand where brand_name='" + rs.getString("MANUF_NAME") + "'") == 0) {
                    ItemBrand ib = new ItemBrand();
                    ib.setMigId(rs.getInt("MANUFACTURER_ID"));
                    ib.setBrandName(rs.getString("MANUF_NAME"));
                    dao.save(ib);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processBrandNameData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void processItemSetupData1() {
        try {
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(txaQuery.getText());
                
                while (rs.next()) {
                    String medId = rs.getString("SHORT_CODE");
                    Medicine tmpMed = (Medicine) dao.find(Medicine.class, medId);
                    if (tmpMed != null) {
                        log.info("Duplicate Medicine : " + medId + " : " + tmpMed.getMedName());
                    } else {
                        Medicine mc = new Medicine();
                        mc.setMedId(rs.getString("SHORT_CODE"));
                        log.info("med_id : " + mc.getMedId());
                        /*if(mc.getMedId().equals("119081")){
                        System.out.println("aaa");
                        }*/
                        String medDesp = getZawgyiText(rs.getString("MED_DESP"));
                        mc.setMedName(medDesp);
                        mc.setActive(rs.getBoolean("ACTIVE"));
                        mc.setRelStr(getZawgyiText(rs.getString("RELATION_STR")));
                        
                        ItemType it = (ItemType) dao.find(ItemType.class, rs.getString("DRUG_TYPE_ID"));
                        mc.setMedTypeId(it);
                        
                        List<ItemBrand> listIB = dao.findAllHSQL(
                                "select o from ItemBrand o where o.migId = " + rs.getInt("MANUFACTURER_ID"));
                        if (listIB != null) {
                            if (!listIB.isEmpty()) {
                                mc.setBrand(listIB.get(0));
                            }
                        }
                        
                        List<Category> listCAT = dao.findAllHSQL(
                                "select o from Category o where o.migId = " + rs.getInt("CHEMICAL_ID"));
                        if (listCAT != null) {
                            if (!listCAT.isEmpty()) {
                                mc.setCatId(listCAT.get(0));
                            }
                        }
                        
                        Statement sDetail = con.createStatement();
                        ResultSet rs1 = sDetail.executeQuery("SELECT UNIT_SHORT, QTY_IN_SMALLEST, PUR_PRICE, SALE_PRICE, WS_PRICE1, WS_PRICE2, WS_PRICE3 \n"
                                + "FROM MEDICINE_PRICE WHERE MEDICINE_ID = " + rs.getString("MED_ID")
                                + " ORDER BY QTY_IN_SMALLEST DESC");
                        
                        List<RelationGroup> listRG = new ArrayList();
                        int uniqueId = 1;
                        float prvQty = 0;
                        
                        while (rs1.next()) {
                            RelationGroup rg = new RelationGroup();
                            rg.setRelUniqueId(uniqueId);
                            rg.setSalePrice(rs1.getDouble("SALE_PRICE"));
                            rg.setSalePriceA(rs1.getDouble("WS_PRICE1"));
                            rg.setSalePriceB(rs1.getDouble("WS_PRICE2"));
                            rg.setSalePriceC(rs1.getDouble("WS_PRICE3"));
                            rg.setSmallestQty(rs1.getFloat("QTY_IN_SMALLEST"));
                            if (uniqueId == 1) {
                                rg.setUnitQty(Float.parseFloat("1"));
                            } else {
                                rg.setUnitQty(prvQty / rg.getSmallestQty());
                            }
                            prvQty = rg.getSmallestQty();
                            
                            ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class, rs1.getString("UNIT_SHORT"));
                            rg.setUnitId(iu);
                            listRG.add(rg);
                            
                            uniqueId++;
                        }
                        
                        rs1.close();
                        sDetail.close();
                        
                        mc.setRelationGroupId(listRG);
                        mc.setActive(true);
                        try {
                            dao.save(mc);
                        } catch (Exception ex) {
                            log.error(mc.getMedId() + "@" + mc.getMedName() + " : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                            //dao.flush();
                        } finally {
                            dao.close();
                        }
                    }
                }
                
                rs.close();
            }
        } catch (Exception ex) {
            lblStatus.setText("processItemSetupData1 : " + ex.getMessage());
            log.error("processItemSetupData1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processItemPriceUpdate() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            if (rs != null) {
                while (rs.next()) {
                    String medId = rs.getString("SHORT_CODE");
                    Medicine tmpMed = (Medicine) dao.find(Medicine.class, medId);
                    if (tmpMed.getRelationGroupId().size() > 0) {
                        tmpMed.setRelationGroupId(tmpMed.getRelationGroupId());
                    }

                    HashMap<String, RelationGroup> priceList = new HashMap();
                    List<RelationGroup> listRG = tmpMed.getRelationGroupId();
                    for (RelationGroup rg : listRG) {
                        priceList.put(rg.getUnitId().getItemUnitCode(), rg);
                    }

                    Statement sDetail = con.createStatement();
                    ResultSet rs1 = sDetail.executeQuery("SELECT UNIT_SHORT, QTY_IN_SMALLEST, PUR_PRICE, SALE_PRICE, WS_PRICE1, WS_PRICE2, WS_PRICE3 \n"
                            + "FROM MEDICINE_PRICE WHERE MEDICINE_ID = " + rs.getString("MED_ID")
                            + " ORDER BY QTY_IN_SMALLEST DESC");
                    while (rs1.next()) {
                        RelationGroup rg = priceList.get(rs1.getString("UNIT_SHORT"));
                        rg.setSalePrice(rs1.getDouble("SALE_PRICE"));
                        rg.setSalePriceA(rs1.getDouble("WS_PRICE1"));
                        rg.setSalePriceB(rs1.getDouble("WS_PRICE2"));
                        rg.setSalePriceC(rs1.getDouble("WS_PRICE3"));
                    }

                    dao.save(tmpMed);
                }
            }
        } catch (Exception ex) {
            log.error("processItemPriceUpdate : " + ex.toString());
        }
    }

    private void processItemMarge() {
        String strSql = "SELECT MED_ID, SHORT_CODE, MED_DESP, DRUG_TYPE_ID, MANUFACTURER_ID, CHEMICAL_ID, ACTIVE,\n"
                + "RELATION_STR\n"
                + "FROM MEDICINE, RELATION_STR \n"
                + "WHERE REL_GRP_ID = RELATION_GRP_ID AND SHORT_CODE IS NOT NULL AND MED_DESP IS NOT NULL AND ACTIVE = -1 ORDER BY ACTIVE";
        String source = "KOSOE-SHOP";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(strSql);
            if (rs != null) {
                while (rs.next()) {
                    CodeMarge cm = new CodeMarge();
                    cm.setItemCode(rs.getString("SHORT_CODE"));
                    cm.setItemName(rs.getString("MED_DESP"));
                    cm.setOraId(rs.getString("MED_ID"));
                    cm.setRelStr(rs.getString("RELATION_STR"));
                    cm.setSource(source);

                    dao.save(cm);
                }

                rs.close();
            }
        } catch (Exception ex) {
            log.error("processItemMarge : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void processOpeningData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(txaQuery.getText());
            Integer ttlRecords = 0;
            Integer ttlSuccRecords = 0;
            Integer ttlErrRecords = 0;

            while (rs.next()) {
                try {
                    vouEngine = new GenVouNoImpl(dao, "StockOpening",
                            DateUtil.getPeriod(DateUtil.toDateStr(rs.getDate("OP_DATE"))));
                    StockOpeningHis sh = new StockOpeningHis();

                    sh.setCreatedBy(hmUser.get(rs.getInt("USER_ID")));
                    sh.setCreatedDate(DateUtil.getTodayDateTime());
                    sh.setOpDate(rs.getDate("OP_DATE"));
                    sh.setStockLocation(hmLocation.get(rs.getInt("LOCATION_ID")));
                    String vouNo = vouEngine.getVouNo1();
                    sh.setStockOpHisId(vouNo);

                    Statement stmtDetail = con.createStatement();
                    String sqlDetail = "SELECT MED.SHORT_CODE, UT.UNIT_SHORT_SYMBOL, SO.QTY_IN_SMALLEST, "
                            + "SO.PUR_PRICE1\n"
                            + "FROM STOCK_OPENING SO, MEDICINE MED, UNIT_TYPE UT\n"
                            + "WHERE SO.MED_ID = MED.MED_ID AND SO.UNIT_TYPE_ID = UT.UNIT_TYPE_ID\n"
                            + "AND SO.LOCATION_ID = " + rs.getInt("LOCATION_ID")
                            + " AND SO.OP_DATE = '" + DateUtil.toDateStr(rs.getDate("OP_DATE"), "dd-MMM-yyyy") + "'";
                    ResultSet rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<StockOpeningDetailHis> listSDH = new ArrayList();
                    int uniqueId = 1;

                    while (rsDetail.next()) {
                        StockOpeningDetailHis sdh = new StockOpeningDetailHis();
                        sdh.setCostPrice(rsDetail.getDouble("PUR_PRICE1"));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsDetail.getString("SHORT_CODE"));
                        sdh.setMed(med);
                        sdh.setQty(rsDetail.getFloat("QTY_IN_SMALLEST"));
                        sdh.setSmallestQty(rsDetail.getFloat("QTY_IN_SMALLEST"));
                        sdh.setUniqueId(uniqueId);
                        ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class,
                                getUnitCodeToMySql(rsDetail.getString("UNIT_SHORT_SYMBOL")));
                        sdh.setUnit(iu);
                        uniqueId++;

                        MedOpDate mod = new MedOpDate();
                        mod.getKey().setLocationId(sh.getStockLocation().getLocationId());
                        mod.getKey().setMedId(sdh.getMed().getMedId());
                        mod.getKey().setOpDate(sh.getOpDate());
                        dao.save(mod);

                        listSDH.add(sdh);
                    }
                    sh.setListDetail(listSDH);
                    rsDetail.close();
                    stmtDetail.close();

                    dao.save(sh);
                    vouEngine.updateVouNo();

                    ttlSuccRecords++;
                    lblSuccessTtl.setText(ttlSuccRecords.toString());
                    txaSuccessData.append(rs.getString("LOCATION_ID") + "@" + rs.getDate("OP_DATE") + "\n");
                } catch (Exception ex1) {
                    log.error("Insert error : " + ex1);
                    ttlErrRecords++;
                    lblErrorDataTtl.setText(ttlErrRecords.toString());
                    txaErrorData.append(rs.getString("LOCATION_ID") + "@" + rs.getDate("OP_DATE") + "\n");
                }

                ttlRecords++;
                lblProcessTtl.setText(ttlRecords.toString());
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            lblStatus.setText("processOpeningData : " + ex.getMessage());
            log.error("processOpeningData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processLabResultData() {
        try {
            List<OPDLabResult> tmpList = dao.findAllHSQL(
                    "select o from OPDLabResult o");
            for (OPDLabResult srv : tmpList) {
                if (srv != null) {

                    String srvPtName;

                    srvPtName = srv.getResultText();

                    if (srvPtName != null) {
                        if (srvPtName.equals("Gram's Stain")) {
                            srvPtName = "Gram Stain";
                        }
                    } else {
                        srvPtName = "-";
                    }

                    String resultCode = "select result_code, printed_text, unit, normal, heading "
                            + "from lab_test_result_code "
                            + "where printed_text = '" + srvPtName + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rsRC = stmt.executeQuery(resultCode);
                    System.out.println("com.cv.app.migration.SystemMigration.processLabResultData()" + srv.getServiceId());
                    if (rsRC.getRow() >= 0) {
                        while (rsRC.next()) {
                            String pt, un, mal;
                            int strSrv;
                            pt = rsRC.getString("printed_text");
                            un = rsRC.getString("unit");
                            mal = rsRC.getString("normal");
                            strSrv = srv.getServiceId();
                            if (pt == null) {
                                pt = "-";
                            }
                            if (un == null) {
                                un = "-";
                            }
                            if (mal == null) {
                                mal = "-";
                            }
                            if (srv.getServiceId() == null) {
                                strSrv = 0;
                            }
                            /*String strSql = " update opd_lab_result  lr"
                                    + " set lr.result_normal ='" + mal + "'"
                                    + " ,lr.result_unit = '" + un + "'"
                                    + " ,lr.mig_id = '" + rsRC.getString("result_code") + "'"
                                    + " where lr.result_text ='" + pt + "'"
                                    + " and lr.service_id = " + strSrv;
                            dao.open();
                            dao.execSql(strSql);
                            dao.close();*/

                            OPDLabResult lr = new OPDLabResult();
                            lr.setMigId(rsRC.getInt("result_code"));
                            lr.setResultNormal(mal);
                            lr.setResultText(pt);
                            //lr.setResultType(hmRType.get(rsRC.getInt("heading")));
                            lr.setResultUnit(un);
                            lr.setServiceId(srv.getServiceId());
                            dao.save(lr);

                            /*String resultInd = "select desp, low_value, high_value, sex "
                            + "from lab_test_result_indicator "
                            + "where result_code = " + lr.getMigId();
                    Statement stmtI = con.createStatement();
                    ResultSet rsRCI = stmtI.executeQuery(resultInd);

                    while (rsRCI.next()) {
                        OPDLabResultInd lri = new OPDLabResultInd();
                        lri.setHighValue(rsRCI.getString("high_value"));
                        lri.setLowValue(rsRCI.getString("low_value"));
                        lri.setResultId(lr.getResultId());
                        Gender sex = (Gender) dao.find(Gender.class, rsRCI.getString("sex"));
                        lri.setSex(sex);

                        dao.save(lr);
                    }*/
                        }
                    }
                    rsRC.close();
                    stmt.close();
                }
            }
        } catch (Exception ex) {
            log.error("processLabResultData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processLabResultSetupData() {
        try {
            List<Service> tmpList = dao.findAllHSQL(
                    "select o from Service o");
            for (Service srv : tmpList) {
                if (srv != null) {
                    String resultCode = "select result_code, printed_text, unit, normal, heading "
                            + "from lab_test_result_code "
                            + "where lab_test_code = " + srv.getMigId().toString();
                    Statement stmt = con.createStatement();
                    ResultSet rsRC = stmt.executeQuery(resultCode);
                    if (rsRC.getRow() >= 0) {
                        while (rsRC.next()) {
                            OPDLabResult lr = new OPDLabResult();
                            lr.setMigId(rsRC.getInt("result_code"));
                            lr.setResultNormal(rsRC.getString("normal"));
                            lr.setResultText(rsRC.getString("printed_text"));
                            lr.setResultType(hmRType.get(rsRC.getInt("heading")));
                            lr.setResultUnit(rsRC.getString("unit"));
                            lr.setServiceId(srv.getServiceId());
                            dao.save(lr);

                            String resultInd = "select desp, low_value, high_value, sex "
                                    + "from lab_test_result_indicator "
                                    + "where result_code = " + lr.getMigId();
                            Statement stmtI = con.createStatement();
                            ResultSet rsRCI = stmtI.executeQuery(resultInd);

                            while (rsRCI.next()) {
                                OPDLabResultInd lri = new OPDLabResultInd();
                                lri.setHighValue(rsRCI.getString("high_value"));
                                lri.setLowValue(rsRCI.getString("low_value"));
                                lri.setResultId(lr.getResultId());
                                Gender sex = (Gender) dao.find(Gender.class, rsRCI.getString("sex"));
                                lri.setSex(sex);

                                dao.save(lr);
                            }

                            rsRCI.close();
                            stmtI.close();
                        }
                    }
                    rsRC.close();
                    stmt.close();
                }
            }
        } catch (Exception ex) {
            log.error("processLabResultData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processDiagnosisData() {
        try {
            String strSql = "select diagonosis_id, diagonosis_desp from diagonosis order by diagonosis_desp";
            Statement stmt = con.createStatement();
            ResultSet rsRC = stmt.executeQuery(strSql);

            while (rsRC.next()) {
                Diagnosis dg = new Diagnosis();
                dg.setLocalName(rsRC.getString("diagonosis_desp"));
                dg.setMigId(rsRC.getInt("diagonosis_id"));

                dao.save(dg);
            }

            rsRC.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processDiagnosisData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void processUserData() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rsRC = stmt.executeQuery(txaQuery.getText());

            while (rsRC.next()) {
                Appuser obj = new Appuser();
                obj.setActive(true);
                obj.setMigId(rsRC.getInt("USER_ID"));
                obj.setPassword(rsRC.getString("UPASSWORD"));
                obj.setUserName(rsRC.getString("UNAME"));
                obj.setUserShortName(rsRC.getString("USHORT"));
                String userId = getUserId();
                obj.setUserId(userId);

                try {
                    dao.save(obj);
                } catch (Exception ex) {
                    log.error(rsRC.getInt("USER_ID") + "@" + rsRC.getString("UNAME") + " : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
            }

            rsRC.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processUserData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private String getUnitCodeToOracle(String unit) {
        switch (unit) {
            case "bot":
                return "b";
            case "Bx":
                return "B";
            case "Sa":
                return "Sah";
            default:
                return unit;
        }
    }

    private String getUnitCodeToMySql(String unit) {
        switch (unit) {
            case "b":
                return "bot";
            case "B":
                return "Bx";
            case "Sah":
                return "Sa";
            default:
                return unit;
        }
    }

    private String getSupplierId() {
        String strFilter = "discriminator = 'S'";
        Object maxSupId = null;
        int maxLength = NumberUtil.NZeroInt(Util1.getPropValue("system.supplier.code.length"));

        try {
            maxSupId = dao.getMax("CONVERT(replace(trader_id,'SUP', ''),UNSIGNED INTEGER)", "trader", strFilter);

            if (maxSupId == null) {
                maxSupId = "SUP001";
            } else {
                String strSupIdSerial;
                Integer tmpSerial = Integer.parseInt(maxSupId.toString().replaceAll("SUP", "")) + 1;

                strSupIdSerial = tmpSerial.toString();
                int i = strSupIdSerial.length();

                for (; i < maxLength; i++) {
                    strSupIdSerial = "0" + strSupIdSerial;
                }

                strSupIdSerial = "SUP" + strSupIdSerial;

                /*if (strSupIdSerial.length() == 1) {
                 strSupIdSerial = "SUP00" + strSupIdSerial;
                 } else if (strSupIdSerial.length() == 2) {
                 strSupIdSerial = "SUP0" + strSupIdSerial;
                 }*/
                maxSupId = strSupIdSerial;
            }
        } catch (Exception ex) {
            log.error("getSupplierId : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        if (maxSupId == null) {
            return null;
        } else {
            return maxSupId.toString();
        }
    }

    private Trader getSupplier(int migId) {
        String strSql = "select o from Trader o where o.migId = " + migId;
        List<Trader> listSUP = dao.findAllHSQL(strSql);
        Trader trader = null;

        if (listSUP != null) {
            if (!listSUP.isEmpty()) {
                trader = listSUP.get(0);
            }
        }

        return trader;
    }

    private Service getOPDService(int migId) {
        List<Service> listSVC = dao.findAllHSQL("select o from Service o where o.migId = "
                + migId);
        Service svc = null;
        if (listSVC != null) {
            if (!listSVC.isEmpty()) {
                svc = listSVC.get(0);
            }
        }
        return svc;
    }

    private String getUserId() {
        String tmpId = PharmacyUtil.getSeq("AppUser", dao).toString();
        int idLength = 3;

        tmpId = tmpId.replaceAll(".0", "");
        if (tmpId.length() < 3) {
            int currLength = tmpId.length();

            for (int i = 0; i < (idLength - currLength); i++) {
                tmpId = "0" + tmpId;
            }
        }
        return tmpId;
    }

    private void processOpCostPrice() {
        HashMap<String, Double> hmPrice = new HashMap();
        try {
            String strSql = txaQuery.getText();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(strSql);

            while (rs.next()) {
                String medCode = rs.getString("SHORT_CODE");
                String unitShort = rs.getString("UNIT_SHORT");
                Double costPrice = rs.getDouble("PUR_PRICE");
                String key = medCode + "-" + unitShort;

                hmPrice.put(key, costPrice);
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            log.error("processOpCostPrice1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }

        if (!hmPrice.isEmpty()) {
            try {
                List<StockOpeningDetailHis> listSODH = dao.findAllHSQL(
                        "select o from StockOpeningDetailHis o order by o.med.medId");
                for (StockOpeningDetailHis sodh : listSODH) {
                    if (sodh.getMed() != null && sodh.getUnit() != null) {
                        String key = sodh.getMed().getMedId() + "-" + sodh.getUnit().getItemUnitCode();
                        try {
                            Double costPrice = hmPrice.get(key);
                            //log.info(key + " : " + costPrice);
                            sodh.setCostPrice(costPrice);
                            dao.save(sodh);
                        } catch (Exception ex) {
                            log.error("Error : " + key);
                        }
                    } else {
                        log.error("Error in : " + sodh.getMed().getMedId());
                    }
                }
            } catch (Exception ex) {
                log.error("processOpCostPrice2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }
        }
    }

    private String getMedCode(String strCh, String typeId) {
        String medCode = "";
        CharacterNo characterNo = null;

        if (!strCh.equals("") && typeId != null) {
            try {
                dao.open();
                Object tmpObj;

                for (int i = 0; i < strCh.length(); i++) {
                    String strTmp = strCh.substring(i, i + 1).toUpperCase();
                    if (!strTmp.trim().equals("")) { //Space character detection
                        if (NumberUtil.isNumber(strTmp)) {
                            i = strCh.length();
                            characterNo = new CharacterNo(" ", "00");
                        } else {
                            tmpObj = dao.find(CharacterNo.class, strTmp);
                            if (tmpObj != null) {
                                i = strCh.length();
                                characterNo = (CharacterNo) tmpObj;
                            }
                        }
                    }
                }
                dao.close();
            } catch (Exception ex) {
                log.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }

            medCode = typeId + Util1.getString(characterNo.getStrNumber(), "");
            int typeLength = typeId.length();

            try {
                dao.open();
                Object maxMedId = dao.getMax("med_id", "medicine", "med_id like '" + medCode + "%'");
                int ttlLength = NumberUtil.NZeroInt(Util1.getPropValue("system.itemsetup.code.length"));
                int leftLength = ttlLength - medCode.length();

                if (maxMedId == null) {
                    for (int i = 0; i < leftLength - 1; i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + "1";
                } else {
                    /*int ln = itemType.getItemTypeCode().length();
                     String tmpMedSerial = maxMedId.toString().substring(4, 5 + ln);

                     Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;

                     tmpMedSerial = tmpSerial.toString();

                     if (tmpMedSerial.length() == 1) {
                     tmpMedSerial = "00" + tmpMedSerial;
                     } else if (tmpMedSerial.length() == 2) {
                     tmpMedSerial = "0" + tmpMedSerial;
                     }

                     medCode = medCode + tmpMedSerial;*/

                    //String tmpMedSerial = maxMedId.toString().replace(medCode, "");
                    int begin = typeLength + 2;
                    String tmpMedSerial = maxMedId.toString().substring(begin);

                    Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;
                    tmpMedSerial = tmpSerial.toString();

                    for (int i = 0; i < (leftLength - tmpMedSerial.length()); i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + tmpMedSerial;
                }
                dao.close();
            } catch (Exception ex) {
                log.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        return medCode;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtServerName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDbSid = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cboDataToProcess = new javax.swing.JComboBox<>();
        butProcess = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        butConnect = new javax.swing.JButton();
        lblProcessTtl = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaQuery = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblErrorDataTtl = new javax.swing.JLabel();
        lblSuccessTtl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaSuccessData = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txaErrorData = new javax.swing.JTextArea();

        jLabel1.setText("Server Name ");

        jLabel2.setText("DB SID ");

        jLabel3.setText("User ");

        jLabel4.setText("Password ");

        jLabel5.setText("Data to process ");

        cboDataToProcess.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sale", "Purchase", "Return In", "Return Out", "Damage", "Adjust", "Opening", "Supplier", "Patient", "OPD", "OT", "DC", "OPD Setup", "Item Setup", "Lab Result", "Diagnosis", "Transfer", "City", "Township", "Doctor", "DC Setup", "OT Setup", "Appuser", "Customer", "OP Cost Price", "Item Price Update", "Customer Opening" }));
        cboDataToProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDataToProcessActionPerformed(evt);
            }
        });

        butProcess.setText("Process");
        butProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butProcessActionPerformed(evt);
            }
        });

        lblStatus.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        lblStatus.setText(" ");

        butConnect.setText("Connect");
        butConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConnectActionPerformed(evt);
            }
        });

        lblProcessTtl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProcessTtl.setText(" ");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Total Process ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboDataToProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProcessTtl, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(butConnect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butProcess))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(cboDataToProcess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(butProcess)
                .addComponent(lblStatus)
                .addComponent(butConnect)
                .addComponent(lblProcessTtl)
                .addComponent(jLabel6))
        );

        txaQuery.setColumns(20);
        txaQuery.setRows(5);
        jScrollPane1.setViewportView(txaQuery);

        jLabel7.setText("Success Data");

        jLabel8.setText("Error Data");

        lblErrorDataTtl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblErrorDataTtl.setText(" ");

        lblSuccessTtl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSuccessTtl.setText(" ");

        txaSuccessData.setColumns(20);
        txaSuccessData.setRows(5);
        jScrollPane2.setViewportView(txaSuccessData);

        txaErrorData.setColumns(20);
        txaErrorData.setRows(5);
        jScrollPane3.setViewportView(txaErrorData);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtServerName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDbSid)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPassword))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblSuccessTtl, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblErrorDataTtl, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtServerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDbSid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(lblErrorDataTtl)
                    .addComponent(lblSuccessTtl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConnectActionPerformed
        try {
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@"
                    + txtServerName.getText()
                    + ":1521:"
                    + txtDbSid.getText(), txtUser.getText(), txtPassword.getText());
            lblStatus.setText("Connection success.");
            txaQuery.setText(getSql(cboDataToProcess.getSelectedItem().toString()));
            butConnect.setEnabled(false);
            butProcess.setEnabled(true);
            cboDataToProcess.setEnabled(true);
            lblErrorDataTtl.setText("0");
            lblProcessTtl.setText("0");
            lblSuccessTtl.setText("0");
        } catch (Exception ex) {
            lblStatus.setText("Connection failed.");
            log.error("Connect : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }

        //String tmp = getZawgyiText("èí¢è½üè¢ìà¥ýü");
        //lblStatus.setText(tmp);
    }//GEN-LAST:event_butConnectActionPerformed

    private void cboDataToProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDataToProcessActionPerformed
        txaQuery.setText(getSql(cboDataToProcess.getSelectedItem().toString()));
        lblErrorDataTtl.setText("0");
        lblProcessTtl.setText("0");
        lblSuccessTtl.setText("0");
    }//GEN-LAST:event_cboDataToProcessActionPerformed

    private void butProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butProcessActionPerformed
        butProcess.setEnabled(false);
        switch (cboDataToProcess.getSelectedItem().toString()) {
            case "Sale":
                processSaleData();
                break;
            case "Purchase":
                processPurchaseData();
                break;
            case "Return In":
                processRetInData();
                break;
            case "Return Out":
                processRetOutData();
                break;
            case "Damage":
                processDamageData();
                break;
            case "Adjust":
                processAdjData();
                break;
            case "Transfer":
                processTransferData();
                break;
            case "Opening":
                processOpeningData();
                break;
            case "Supplier":
                processSupplierData();
                break;
            case "Patient":
                processPatientData();
                break;
            case "OPD":
                processOPDData();
                break;
            case "OT":
                processOTData();
                break;
            case "DC":
                processDCData();
                break;
            case "OPD Setup":
                processOPDSetupData();
                break;
            case "Item Setup":
                //-processItemSetupData();
                //processItemTypeData();
                //processCategoryData();
                //processBrandNameData();
                processItemSetupData1();
                //processItemMarge();
                break;
            case "Lab Result":
                //processLabResultData();
                processLabResultSetupData();
                break;
            case "Diagnosis":
                processDiagnosisData();
                break;
            case "City":
                processCityData();
                break;
            case "Township":
                processTownshipData();
                break;
            case "Doctor":
                processDoctorData();
                break;
            case "DC Setup":
                processDCSetupData();
                break;
            case "OT Setup":
                processOTSetupData();
                break;
            case "Appuser":
                processUserData();
                break;
            case "Customer":
                processCustomerData();
                break;
            case "OP Cost Price":
                processOpCostPrice();
                break;
            case "Item Price Update":
                processItemPriceUpdate();
                break;
            case "Customer Opening":
                processCustomerOpening();
                break;
        }
        butProcess.setEnabled(true);
    }//GEN-LAST:event_butProcessActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butConnect;
    private javax.swing.JButton butProcess;
    private javax.swing.JComboBox<String> cboDataToProcess;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblErrorDataTtl;
    private javax.swing.JLabel lblProcessTtl;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSuccessTtl;
    private javax.swing.JTextArea txaErrorData;
    private javax.swing.JTextArea txaQuery;
    private javax.swing.JTextArea txaSuccessData;
    private javax.swing.JTextField txtDbSid;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtServerName;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
