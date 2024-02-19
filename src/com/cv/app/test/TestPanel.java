/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.test;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.ui.entry.Sale;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TestPanel extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(Sale.class.getName());
    private final AbstractDataAccess dao = Global.dao;

    /**
     * Creates new form TestPanel
     */
    public TestPanel() {
        initComponents();

    }

    private void connectToOracle() {
        try {
            //step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //step2 create  the connection object
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@server:1521:wesley", "WESLEY", "WESLEY");

            //step3 create the statement object
            Statement stmt = con.createStatement();

            //step4 execute query
            ResultSet rs = stmt.executeQuery("select * from patient");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            }

            //step5 close the connection object
            con.close();
        } catch (Exception ex) {
            log.error("connectToOracle : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    private void massDelete() {
        String strSql = "select med_id from medicine where phar_sys_id is null \n"
                + "and med_id not in (select med_id from cv_inv_sdm.tmp_code_list)\n"
                + "and med_type_id in ('01','02','03','04','05','06','07','08','09','10','11','12')\n"
                + "and date(created_date) = '2021-07-19'";
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    String medId = rs.getString("med_id");
                    String childId = null;
                    Medicine currMedicine = (Medicine) dao.find(Medicine.class, medId);
                    dao.open();
                    dao.beginTran();

                    String sql = "delete from med_rel where med_id = '" + medId + "'";
                    dao.execSqlT(sql);

                    List<RelationGroup> list = currMedicine.getRelationGroupId();
                    if (list != null) {
                        if (!list.isEmpty()) {
                            for (RelationGroup rg : list) {
                                if (childId == null) {
                                    childId = rg.getRelGId().toString();
                                } else {
                                    childId = childId + "," + rg.getRelGId().toString();
                                }
                            }
                            sql = "delete from relation_group where rel_group_id in (" + childId + ")";
                            dao.execSqlT(sql);
                        }
                    }
                    sql = "delete from medicine where med_id = '" + currMedicine.getMedId() + "'";
                    dao.execSqlT(sql);
                    dao.commit();

                    log.info("medId : " + medId + " deleted.");
                }
            }
        } catch (Exception ex) {
            log.error("massDelete : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void priceUpdate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processCSV(selectedFile);
        }
    }

    private void processCSV(File file) {
        if (file != null) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                try ( CSVReader csvReader = new CSVReader(reader)) {
                    String[] nextRecord;
                    int ttlRec = 0;
                    int ttlInsert = 0;
                    log.info("processCSV Start");
                    while ((nextRecord = csvReader.readNext()) != null) {
                        String medId = nextRecord[0];
                        String itemUnit = nextRecord[1];
                        String strSalePrice = nextRecord[2];
                        String strPriceA = nextRecord[3];
                        String strPriceB = nextRecord[4];
                        String strPriceC = nextRecord[5];
                        String strPriceD = nextRecord[6];
                        double salePrice = 0;
                        double priceA = 0;
                        double priceB = 0;
                        double priceC = 0;
                        double priceD = 0;

                        if (!strSalePrice.equals("NULL")) {
                            salePrice = Double.parseDouble(strSalePrice);
                        }

                        if (!strPriceA.equals("NULL")) {
                            priceA = Double.parseDouble(strPriceA);
                        }

                        if (!strPriceB.equals("NULL")) {
                            priceB = Double.parseDouble(strPriceB);
                        }

                        if (!strPriceC.equals("NULL")) {
                            priceC = Double.parseDouble(strPriceC);
                        }

                        if (!strPriceD.equals("NULL")) {
                            priceD = Double.parseDouble(strPriceD);
                        }
                        ttlRec++;
                        /*log.info("Rec No : " + ttlRec + " medId : " + medId + " itemUnit : " + itemUnit
                                + " salePrice : " + salePrice + " priceA : " + priceA
                                + " priceB : " + priceB + " priceC : " + priceC
                                + " priceD : " + priceD);*/

                        try {
                            String strUpdSql = "update medicine med\n"
                                    + "join med_rel mr on med.med_id = mr.med_id\n"
                                    + "join relation_group rg on mr.rel_group_id = rg.rel_group_id\n"
                                    + "set rg.sale_price = " + salePrice + ", "
                                    + "rg.sale_pric_a = " + priceA + ", "
                                    + "rg.sale_pric_b = " + priceB + ", "
                                    + "rg.sale_pric_c = " + priceC + ", "
                                    + "rg.sale_pric_d = " + priceD + " \n"
                                    + "where med.med_id = '" + medId
                                    + "' and rg.item_unit = '" + itemUnit + "'";
                            dao.execSql(strUpdSql);
                            log.info("medId : " + medId + " finished.");
                        } catch (Exception ex) {
                            log.error("Rec No : " + ttlRec + " medId : " + medId + " itemUnit : " + itemUnit
                                    + " salePrice : " + salePrice + " priceA : " + priceA
                                    + " priceB : " + priceB + " priceC : " + priceC
                                    + " priceD : " + priceD);
                        } finally {
                            dao.close();
                        }
                    }

                    log.info("processCSV End : Total Rec : " + ttlRec + " Total Insert : " + ttlInsert);
                }
            } catch (Exception ex) {
                log.error("processCSV : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void correctRelation() {
        String strSql = "select med_id from medicine where med_id in (\n"
                + "select distinct med_id from med_rel where rel_group_id in (\n"
                + "select rel_group_id from relation_group where item_unit is null))";
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    String medId = rs.getString("med_id");
                    Medicine med = (Medicine) dao.find(Medicine.class, medId);
                    String relStr = med.getRelStr().replace("*", ",");
                    String[] arrRelStr = relStr.split(",");
                    if (arrRelStr.length > 1) {
                        if (arrRelStr.length == 3) {
                            if (arrRelStr[2].equals("5Ap")) {
                                List<RelationGroup> list = med.getRelationGroupId();
                                if (list != null) {
                                    RelationGroup rg = list.get(2);
                                    ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class, "Amp");
                                    rg.setUnitId(iu);
                                    med.setRelStr(arrRelStr[0] + "*" + arrRelStr[1] + "*5Amp");
                                    med.setRelationGroupId(list);
                                    dao.save(med);
                                    log.info("med_id : " + medId);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("massDelete : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        butTestLog = new javax.swing.JButton();
        txtFormatTest = new javax.swing.JFormattedTextField();
        jButton2 = new javax.swing.JButton();
        ckh1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        butCR = new javax.swing.JButton();

        setBackground(new java.awt.Color(161, 154, 154));

        butTestLog.setText("Test Log");
        butTestLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTestLogActionPerformed(evt);
            }
        });

        txtFormatTest.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        ckh1.setText("jCheckBox1");

        jLabel1.setText("jLabel1");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 102)));

        butCR.setText("Correct Relation");
        butCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCRActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 183, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ckh1)
                        .addGap(124, 124, 124))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(170, 170, 170))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(butTestLog)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(txtFormatTest, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(butCR)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butTestLog)
                    .addComponent(jButton2))
                .addGap(13, 13, 13)
                .addComponent(ckh1)
                .addGap(14, 14, 14)
                .addComponent(txtFormatTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(butCR)
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addContainerGap(114, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butTestLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTestLogActionPerformed
        //log.error("Test log.");
        String tmp = "092018";
        String tmp1 = tmp.substring(0, 2);
        String tmp2 = tmp.substring(2, 6);
    }//GEN-LAST:event_butTestLogActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        /*String strTmp = "0(-25)H";
        for(int i = 0; i < strTmp.length(); i++){
            String tmpChar = strTmp.substring(i, i+1);
            System.out.println(tmpChar);
        }*/
        //massDelete();
        //priceUpdate();
        String qtyStr = MedicineUtil.getQtyInStr("30/10/1", "Bx/Crd/t", 1010f);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void butCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCRActionPerformed
        correctRelation();
    }//GEN-LAST:event_butCRActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCR;
    private javax.swing.JButton butTestLog;
    private javax.swing.JCheckBox ckh1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JFormattedTextField txtFormatTest;
    // End of variables declaration//GEN-END:variables
}
