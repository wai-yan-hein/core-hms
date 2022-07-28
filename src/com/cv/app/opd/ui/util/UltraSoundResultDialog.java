/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.util;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.InvestigationResult;
import com.cv.app.opd.database.view.VOpd;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class UltraSoundResultDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(UltraSoundResultDialog.class.getName());
    private InvestigationResult invResult;
    private final AbstractDataAccess dao = Global.dao;

    /**
     * Creates new form UltraSoundResultDialog
     *
     * @param record
     */
    public UltraSoundResultDialog(VOpd record) {
        super(Util1.getParent(), true);
        initComponents();
        intiRadio();
        getData(record);
    }

    private void intiRadio() {
        radLiverSizeElarged.setActionCommand("Elarged");
        radLiverSizeNormal.setActionCommand("Normal");
        radLiverSizeReduced.setActionCommand("Reduced");

        radLiverMarginIrregular.setActionCommand("Irregular");
        radLiverMarginSmooth.setActionCommand("Smooth");

        radLiverEchogenicityIncrease.setActionCommand("Increase");
        radLiverEchogenicityNormal.setActionCommand("Normal");
        radLiverEchogenicityReduced.setActionCommand("Reduced");

        radThrombusAbsent.setActionCommand("Absent");
        radThrombusPresent.setActionCommand("Present");
    }

    private void getData(VOpd record) {
        try {
            if (record != null) {
                String key = record.getKey().getVouNo() + "-" + record.getKey().getOpdDetailId()
                        + "-" + record.getPatientId() + "-" + record.getKey().getServiceId();
                invResult = (InvestigationResult) dao.find(InvestigationResult.class, key);
                if (invResult != null) {
                    setLiverSize(invResult.getLiverSize());
                    setLiverMargin(invResult.getLiverMargin());
                    setLiverEchogenicity(invResult.getLiverEchogenicity());
                    txtSOL.setText(invResult.getSol());
                    txtDiameter.setText(invResult.getPortalVeinDiameter());
                    setThrombus(invResult.getPortalVeinThrombus());
                    txtGBSize.setText(invResult.getGbSize());
                    txtContent.setText(invResult.getGbContent());
                    txtWail.setText(invResult.getGbWail());
                    txtCBD.setText(invResult.getCbd());
                    txtIntrahepaticDucts.setText(invResult.getIntrahepaticDucts());
                    txtPancreas.setText(invResult.getPancreas());
                    txtSpleen.setText(invResult.getSpleen());
                    txtKidneyR.setText(invResult.getKidneyR());
                    txtKidneyL.setText(invResult.getKidneyL());
                    txtBladder.setText(invResult.getBladder());
                    txtProstate.setText(invResult.getProstate());
                    txtUterus.setText(invResult.getUterus());
                    txtOvary.setText(invResult.getOvary());
                    txtAscites.setText(invResult.getAscites());
                    txtLymphNode.setText(invResult.getLymphNode());
                    txtOthers.setText(invResult.getOthers());
                    txtImpression.setText(invResult.getImpression());
                } else {
                    invResult = new InvestigationResult();
                    invResult.setKeyField(key);
                }
            }
        } catch (Exception ex) {
            log.error("getData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void save() {
        if (invResult != null) {
            try {
                if (liverSizeGroup.getSelection() != null) {
                    invResult.setLiverSize(liverSizeGroup.getSelection().getActionCommand());
                }
                if (marginGroup.getSelection() != null) {
                    invResult.setLiverMargin(marginGroup.getSelection().getActionCommand());
                }
                if (echogenicityGroup.getSelection() != null) {
                    invResult.setLiverEchogenicity(echogenicityGroup.getSelection().getActionCommand());
                }
                invResult.setSol(txtSOL.getText());
                invResult.setPortalVeinDiameter(txtDiameter.getText());
                if (thrombusGroup.getSelection() != null) {
                    invResult.setPortalVeinThrombus(thrombusGroup.getSelection().getActionCommand());
                }
                invResult.setGbSize(txtGBSize.getText());
                invResult.setGbContent(txtContent.getText());
                invResult.setGbWail(txtWail.getText());
                invResult.setCbd(txtCBD.getText());
                invResult.setIntrahepaticDucts(txtIntrahepaticDucts.getText());
                invResult.setPancreas(txtPancreas.getText());
                invResult.setSpleen(txtSpleen.getText());
                invResult.setKidneyR(txtKidneyR.getText());
                invResult.setKidneyL(txtKidneyL.getText());
                invResult.setBladder(txtBladder.getText());
                invResult.setProstate(txtProstate.getText());
                invResult.setUterus(txtUterus.getText());
                invResult.setOvary(txtOvary.getText());
                invResult.setAscites(txtAscites.getText());
                invResult.setLymphNode(txtLymphNode.getText());
                invResult.setOthers(txtOthers.getText());
                invResult.setImpression(txtImpression.getText());

                dao.save(invResult);
                this.dispose();
            } catch (Exception ex) {
                log.error("save : " + ex.toString());
            }
        }
    }

    private void setLiverSize(String option) {
        if (option != null) {
            switch (option) {
                case "Normal":
                    radLiverSizeNormal.setSelected(true);
                    break;
                case "Elarged":
                    radLiverSizeElarged.setSelected(true);
                    break;
                case "Reduced":
                    radLiverSizeReduced.setSelected(true);
                    break;
            }
        }
    }

    private void setLiverMargin(String option) {
        if (option != null) {
            switch (option) {
                case "Smooth":
                    radLiverMarginSmooth.setSelected(true);
                    break;
                case "Irregular":
                    radLiverMarginIrregular.setSelected(true);
                    break;
            }
        }
    }

    private void setLiverEchogenicity(String option) {
        if (option != null) {
            switch (option) {
                case "Normal":
                    radLiverEchogenicityNormal.setSelected(true);
                    break;
                case "Increase":
                    radLiverEchogenicityIncrease.setSelected(true);
                    break;
                case "Reduced":
                    radLiverEchogenicityReduced.setSelected(true);
                    break;
            }
        }
    }

    private void setThrombus(String option) {
        if (option != null) {
            switch (option) {
                case "Present":
                    radThrombusPresent.setSelected(true);
                    break;
                case "Absent":
                    radThrombusAbsent.setSelected(true);
                    break;
            }
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

        liverSizeGroup = new javax.swing.ButtonGroup();
        marginGroup = new javax.swing.ButtonGroup();
        echogenicityGroup = new javax.swing.ButtonGroup();
        thrombusGroup = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        radLiverSizeNormal = new javax.swing.JRadioButton();
        radLiverSizeElarged = new javax.swing.JRadioButton();
        radLiverSizeReduced = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        radLiverMarginSmooth = new javax.swing.JRadioButton();
        radLiverMarginIrregular = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        radLiverEchogenicityNormal = new javax.swing.JRadioButton();
        radLiverEchogenicityIncrease = new javax.swing.JRadioButton();
        radLiverEchogenicityReduced = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSOL = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtDiameter = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        radThrombusPresent = new javax.swing.JRadioButton();
        radThrombusAbsent = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtGBSize = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtContent = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtWail = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCBD = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtIntrahepaticDucts = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtPancreas = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtSpleen = new javax.swing.JTextField();
        txtKidneyR = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtKidneyL = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtBladder = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtProstate = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtUterus = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtOvary = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtAscites = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtLymphNode = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtOthers = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtImpression = new javax.swing.JTextArea();
        butSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ultrasound Result");

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Liver"));

        jLabel1.setText("Size : ");

        liverSizeGroup.add(radLiverSizeNormal);
        radLiverSizeNormal.setText("Normal");

        liverSizeGroup.add(radLiverSizeElarged);
        radLiverSizeElarged.setText("Elarged");

        liverSizeGroup.add(radLiverSizeReduced);
        radLiverSizeReduced.setText("Reduced");
        radLiverSizeReduced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radLiverSizeReducedActionPerformed(evt);
            }
        });

        jLabel3.setText("Margin : ");

        marginGroup.add(radLiverMarginSmooth);
        radLiverMarginSmooth.setText("Smooth");

        marginGroup.add(radLiverMarginIrregular);
        radLiverMarginIrregular.setText("Irregular");

        jLabel4.setText("Echogenicity : ");

        echogenicityGroup.add(radLiverEchogenicityNormal);
        radLiverEchogenicityNormal.setText("Normal");

        echogenicityGroup.add(radLiverEchogenicityIncrease);
        radLiverEchogenicityIncrease.setText("Increase");

        echogenicityGroup.add(radLiverEchogenicityReduced);
        radLiverEchogenicityReduced.setText("Reduced");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverSizeNormal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverSizeElarged)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverSizeReduced))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverMarginSmooth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverMarginIrregular))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverEchogenicityNormal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverEchogenicityIncrease)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radLiverEchogenicityReduced)))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(radLiverSizeNormal)
                    .addComponent(radLiverSizeElarged)
                    .addComponent(radLiverSizeReduced))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(radLiverMarginSmooth)
                    .addComponent(radLiverMarginIrregular))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(radLiverEchogenicityNormal)
                    .addComponent(radLiverEchogenicityIncrease)
                    .addComponent(radLiverEchogenicityReduced)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("SOL"));

        txtSOL.setColumns(20);
        txtSOL.setRows(5);
        jScrollPane1.setViewportView(txtSOL);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Portal Vein"));

        jLabel5.setText("Diameter : ");

        jLabel6.setText("Thrombus : ");

        thrombusGroup.add(radThrombusPresent);
        radThrombusPresent.setText("Present");

        thrombusGroup.add(radThrombusAbsent);
        radThrombusAbsent.setText("Absent");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiameter))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radThrombusPresent)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radThrombusAbsent)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDiameter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(radThrombusPresent)
                    .addComponent(radThrombusAbsent))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("GB"));

        jLabel7.setText("Size : ");

        jLabel8.setText("Content : ");

        jLabel9.setText("Wail : ");

        txtWail.setColumns(20);
        txtWail.setRows(5);
        jScrollPane2.setViewportView(txtWail);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGBSize))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtContent))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtGBSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 49, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Finding 1", jPanel1);

        jLabel2.setText("CBD : ");

        jLabel10.setText("Intrahepatic-Ducts : ");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Pancreas : ");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Spleen : ");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Kidney (R) : ");

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Kidney (L) : ");

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Bladder : ");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Prostate : ");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Uterus : ");

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Ovary : ");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Ascites : ");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Lymph Node : ");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Others : ");

        txtOthers.setColumns(20);
        txtOthers.setRows(5);
        jScrollPane3.setViewportView(txtOthers);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPancreas)
                    .addComponent(txtSpleen)
                    .addComponent(txtKidneyR)
                    .addComponent(txtKidneyL)
                    .addComponent(txtBladder)
                    .addComponent(txtProstate)
                    .addComponent(txtUterus)
                    .addComponent(txtOvary)
                    .addComponent(txtAscites)
                    .addComponent(txtLymphNode)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel12, jLabel13, jLabel14, jLabel15, jLabel16, jLabel17, jLabel18, jLabel19, jLabel20});

        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtPancreas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtSpleen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtKidneyR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtKidneyL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtBladder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtProstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtUterus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtOvary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtAscites, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtLymphNode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCBD, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIntrahepaticDucts)
                .addContainerGap())
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtIntrahepaticDucts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Finding 2", jPanel2);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Impression"));

        txtImpression.setColumns(20);
        txtImpression.setRows(5);
        jScrollPane4.setViewportView(txtImpression);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Impression", jPanel7);

        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(butSave)
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butSave)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void radLiverSizeReducedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radLiverSizeReducedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radLiverSizeReducedActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSave;
    private javax.swing.ButtonGroup echogenicityGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.ButtonGroup liverSizeGroup;
    private javax.swing.ButtonGroup marginGroup;
    private javax.swing.JRadioButton radLiverEchogenicityIncrease;
    private javax.swing.JRadioButton radLiverEchogenicityNormal;
    private javax.swing.JRadioButton radLiverEchogenicityReduced;
    private javax.swing.JRadioButton radLiverMarginIrregular;
    private javax.swing.JRadioButton radLiverMarginSmooth;
    private javax.swing.JRadioButton radLiverSizeElarged;
    private javax.swing.JRadioButton radLiverSizeNormal;
    private javax.swing.JRadioButton radLiverSizeReduced;
    private javax.swing.JRadioButton radThrombusAbsent;
    private javax.swing.JRadioButton radThrombusPresent;
    private javax.swing.ButtonGroup thrombusGroup;
    private javax.swing.JTextField txtAscites;
    private javax.swing.JTextField txtBladder;
    private javax.swing.JTextField txtCBD;
    private javax.swing.JTextField txtContent;
    private javax.swing.JTextField txtDiameter;
    private javax.swing.JTextField txtGBSize;
    private javax.swing.JTextArea txtImpression;
    private javax.swing.JTextField txtIntrahepaticDucts;
    private javax.swing.JTextField txtKidneyL;
    private javax.swing.JTextField txtKidneyR;
    private javax.swing.JTextField txtLymphNode;
    private javax.swing.JTextArea txtOthers;
    private javax.swing.JTextField txtOvary;
    private javax.swing.JTextField txtPancreas;
    private javax.swing.JTextField txtProstate;
    private javax.swing.JTextArea txtSOL;
    private javax.swing.JTextField txtSpleen;
    private javax.swing.JTextField txtUterus;
    private javax.swing.JTextArea txtWail;
    // End of variables declaration//GEN-END:variables
}
