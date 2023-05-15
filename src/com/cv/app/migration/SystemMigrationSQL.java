/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.migration;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Category;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.ItemBrand;
import com.cv.app.pharmacy.database.entity.ItemType;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.Supplier;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.Trader;
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
import org.apache.log4j.Logger;

/**
 *
 * @author admin
 */
public class SystemMigrationSQL extends javax.swing.JPanel {

    private final AbstractDataAccess dao = Global.dao;
    static Logger log = Logger.getLogger(SystemMigrationSQL.class.getName());
    private Connection con;
    private HashMap<Integer, Integer> hmIngZgy;

    /**
     * Creates new form SystemMigration
     */
    public SystemMigrationSQL() {
        initComponents();
        initData();
    }

    private void initData() {
        txtDbSid.setText("DSL");
        txtPassword.setText("");
        txtServerName.setText("192.168.100.47");
        txtUser.setText("sa");
        butProcess.setEnabled(false);
        butConnect.setEnabled(false);
        cboDataToProcess.setEnabled(false);

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            butConnect.setEnabled(true);
        } catch (ClassNotFoundException ex) {
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
            default:
                return "";
        }
    }

    private void processSupplierData() {
        try {
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(txaQuery.getText())) {
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

            }
        } catch (SQLException ex1) {
            lblStatus.setText("processSupplierData : " + ex1.getMessage());
            log.error("processSupplierData : " + ex1);
        }
    }

    private void processCustomerData() {
        try {
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(txaQuery.getText())) {
                Integer ttlRecords = 0;
                Integer ttlSuccRecords = 0;
                Integer ttlErrRecords = 0;

                while (rs.next()) {
                    try {
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
                        //cus.setTypeId(hmTraderType.get(rs.getString("PRICE_TYPE")));
                        cus.setCreditDays(rs.getInt("CR_DAYS"));
                        cus.setCreditLimit(rs.getInt("CR_LIMIT"));
                        cus.setCreatedBy(Global.loginUser.getUserId());
                        cus.setCreatedDate(DateUtil.getTodayDateTime());
                        dao.save(cus);

                        ttlSuccRecords++;
                        lblSuccessTtl.setText(ttlSuccRecords.toString());
                        txaSuccessData.append(rs.getString("CUS_CODE") + "@" + rs.getString("CUS_NAME") + "\n");
                        log.info(cus.getTraderId() + "-" + rs.getString("PRICE_TYPE"));
                    } catch (Exception ex) {
                        log.error("Insert error : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                        ttlErrRecords++;
                        lblErrorDataTtl.setText(ttlErrRecords.toString());
                        txaErrorData.append(rs.getString("CUS_CODE") + "@" + rs.getString("CUS_NAME") + "\n");
                    }

                    ttlRecords++;
                    lblProcessTtl.setText(ttlRecords.toString());
                }

            }
        } catch (SQLException ex1) {
            lblStatus.setText("processCustomerData : " + ex1.getMessage());
            log.error("processCustomerData : " + ex1);
        }
    }

    private void processItemTypeData() {
        String strSql = "SELECT DRUG_TYPE_ID, DRUG_TYPE_NAME FROM DRUG_TYPE ORDER BY DRUG_TYPE_ID";
        try {
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(strSql)) {

                while (rs.next()) {
                    ItemType it = new ItemType();
                    it.setItemTypeCode(rs.getString("DRUG_TYPE_ID"));
                    it.setItemTypeName(rs.getString("DRUG_TYPE_NAME"));

                    dao.save(it);
                }

            }
        } catch (Exception ex) {
            log.error("processItemTypeData : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void processItemSetupData1() {
        try {
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(txaQuery.getText())) {

                while (rs.next()) {
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

                    try {
                        ItemType it = (ItemType) dao.find(ItemType.class, rs.getString("DRUG_TYPE_ID"));
                        mc.setMedTypeId(it);
                    } catch (Exception ex) {
                        log.error("processItemSetupData1 : " + ex.getMessage());
                    }
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

                    List<RelationGroup> listRG;
                    try (Statement sDetail = con.createStatement(); ResultSet rs1 = sDetail.executeQuery("SELECT UNIT_SHORT, QTY_IN_SMALLEST, PUR_PRICE, SALE_PRICE, WS_PRICE1, WS_PRICE2, WS_PRICE3 \n"
                            + "FROM MEDICINE_PRICE WHERE MEDICINE_ID = " + rs.getString("MED_ID")
                            + " ORDER BY QTY_IN_SMALLEST DESC")) {
                        listRG = new ArrayList();
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

                            try {
                                ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class, rs1.getString("UNIT_SHORT"));
                                rg.setUnitId(iu);
                                listRG.add(rg);
                            } catch (Exception ex) {
                                log.error("processItemSetupData11 : " + ex.getMessage());
                            } finally {
                                dao.close();
                            }
                            uniqueId++;
                        }
                    }

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
        } catch (NumberFormatException | SQLException ex) {
            lblStatus.setText("processItemSetupData1 : " + ex.getMessage());
            log.error("processItemSetupData1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } catch (Exception ex) {
            log.error("processItemSetupData2 : " + ex.getMessage());
        } finally {
            dao.close();
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
        Trader trader = null;
        try {
            String strSql = "select o from Trader o where o.migId = " + migId;
            List<Trader> listSUP = dao.findAllHSQL(strSql);

            if (listSUP != null) {
                if (!listSUP.isEmpty()) {
                    trader = listSUP.get(0);
                }
            }
        } catch (Exception ex) {
            log.error("getSupplier : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return trader;
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

        cboDataToProcess.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sale", "Purchase", "Return In", "Return Out", "Damage", "Adjust", "Opening", "Supplier", "Patient", "OPD", "OT", "DC", "OPD Setup", "Item Setup", "Lab Result", "Diagnosis", "Transfer", "City", "Township", "Doctor", "DC Setup", "OT Setup", "Appuser", "Customer", "OP Cost Price" }));
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
            String strUrl = "jdbc:sqlserver:" + txtServerName.getText() + ";databaseName="
                    + txtDbSid.getText();
            con = DriverManager.getConnection(strUrl, txtUser.getText(), txtPassword.getText());
            lblStatus.setText("Connection success.");
            txaQuery.setText(getSql(cboDataToProcess.getSelectedItem().toString()));
            butConnect.setEnabled(false);
            butProcess.setEnabled(true);
            cboDataToProcess.setEnabled(true);
            lblErrorDataTtl.setText("0");
            lblProcessTtl.setText("0");
            lblSuccessTtl.setText("0");
        } catch (SQLException ex) {
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
            case "Item Setup":
                processItemSetupData1();
                break;
            case "Item Type":
                processItemTypeData();
                break;
            case "Customer":
                processCustomerData();
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
