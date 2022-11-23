/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.excel;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;

/**
 *
 * @author winswe
 */
public abstract class GenExcel {

    private final AbstractDataAccess dao;
    private String userId = "-";
    private String strOpDate = "-";
    private String locationId = "-";
    private String itemType = "-";
    private String categoryId = "-";
    private String brandId = "-";
    private String itemCodeFilter = "-";
    private String itemGroup = "-";
    private String currencyId = "-";
    private String fromDate = "-";
    private String toDate = "-";
    private String cusGroup = "-";
    private String townshipId = "-";
    private String traderList = "-";
    private String regNo = "-";
    private String session = "0";
    private String paymentType = "0";
    private String toLocationId = "0";
    private String locationGroup = "0";
    private String doctorId = "-";
    private String vouType = "0";
    private String admNo = "-";
    private String customG = "-";
    private String opdCat = "-";
    private String itemActive = "All";
    private String chargeType = "-1";
    private String city = "-1";
    private String gender = "-";

    public GenExcel(AbstractDataAccess dao) {
        this.dao = dao;
    }

    public abstract void genExcel();

    public void insertStockFilterCode() throws Exception {
        String strSQLDelete = "delete from tmp_stock_filter where user_id = '"
                + userId + "'";
        
        String strSQL = "insert into tmp_stock_filter\n"
                + "select m.location_id, m.med_id, '1900-01-01', '" + userId + "' \n"
                + "from v_med_loc m\n"
                + "where m.calc_stock = true";

        strSQL = strSQL + " group by location_id, med_id) meod on m.med_id = meod.med_id "
                + " and m.location_id = meod.location_id where m.calc_stock = true";

        if (!locationId.equals("-") && !locationId.equals("0")) {
            strSQL = strSQL + " and m.location_id = " + locationId;
        }

        if (!itemType.equals("-")) {
            strSQL = strSQL + " and m.med_type_id = '" + itemType + "'";
        }

        if (!categoryId.equals("-")) {
            strSQL = strSQL + " and m.category_id = " + categoryId;
        }

        if (!brandId.equals("-")) {
            strSQL = strSQL + " and m.brand_id = " + brandId;
        }

        if (!itemCodeFilter.equals("-")) {
            strSQL = strSQL + " and m.med_id in (select item_code from "
                    + "tmp_item_code_filter_rpt where user_id = '"
                    + userId + "')";
        }

        if (!itemGroup.equals("-")) {
            strSQL = strSQL + " and m.med_id in (select item_id from item_group_detail where group_id = "
                    + itemGroup + ")";
        }

        if (!customG.equals("-")) {
            strSQL = strSQL + " and m.med_id in (select item_id from item_group_detail where group_id = "
                    + customG + ")";
        }

        if (!itemActive.equals("All")) {
            if (itemActive.equals("Active")) {
                strSQL = strSQL + " and m.active = true";
            } else if (itemActive.equals("In Active")) {
                strSQL = strSQL + " and m.active = false";
            }
        }

        dao.open();
        dao.execSql(strSQLDelete);
        dao.close();

        dao.open();
        dao.execSql(strSQL);
        dao.close();

        String strSql = "update tmp_stock_filter tsf join (select location_id, med_id, max(op_date) op_date from med_op_date \n"
                + "where op_date <= '" + strOpDate + "' \n"
                + "	group by location_id, med_id) meod on tsf.med_id = meod.med_id  and tsf.location_id = meod.location_id\n"
                + "set tsf.op_date = meod.op_date\n"
                + "where tsf.user_id = '" + userId + "'";
        dao.open();
        dao.execSql(strSql);
        dao.close();
    }

    public void insertTraderFilterCode(String disc) throws Exception {
        String strSQLDelete = "delete from tmp_trader_bal_filter where user_id = '"
                + userId + "'";
        String strSQL = "insert into tmp_trader_bal_filter(trader_id,currency,op_date,user_id, amount) "
                + "select t.trader_id, t.cur_code, "
                + " ifnull(trop.op_date, '1900-01-01'),'" + userId
                + "', ifnull(trop.op_amount,0)"
                + " from v_trader_cur t left join "
                + "(select trader_id, currency, max(op_date) op_date, op_amount from trader_op "
                //+ " where op_date <= '" + DateUtil.toDateStrMYSQL(DateUtil.subDateTo(DateUtil.toDate(txtFrom.getText()), -1)) + "'"
                + " where op_date <= '" + fromDate + "'";

        strSQL = strSQL + " group by trader_id, currency) trop on t.trader_id = trop.trader_id "
                + " and t.cur_code = trop.currency where t.active = true ";

        if (disc.equals("C") || disc.equals("S") || disc.equals("P")) {
            strSQL = strSQL + " and discriminator = '" + disc + "'";
        }

        if (!currencyId.equals("-")) {
            strSQL = strSQL + " and t.cur_code = '" + currencyId + "'";
        }

        if (!cusGroup.equals("-")) {
            strSQL = strSQL + " and group_id = '" + cusGroup + "'";
        }

        if (!townshipId.equals("-")) {
            strSQL = strSQL + " and t.township = " + townshipId;
        }

        if (!traderList.equals("-")) {
            strSQL = strSQL + " and t.trader_id in (select trader_id from tmp_trader_filter "
                    + " where user_id = '" + userId + "')";
        }

        dao.open();
        dao.execSql(strSQLDelete);
        dao.close();

        dao.open();
        dao.execSql(strSQL);
        dao.close();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStrOpDate() {
        return strOpDate;
    }

    public void setStrOpDate(String strOpDate) {
        this.strOpDate = strOpDate;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        if (!itemType.equals("0") && !itemType.equals("All")) {
            this.itemType = itemType;
        }
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        if (!categoryId.equals("0")) {
            this.categoryId = categoryId;
        }
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        if (!brandId.equals("0")) {
            this.brandId = brandId;
        }
    }

    public String getItemCodeFilter() {
        return itemCodeFilter;
    }

    public void setItemCodeFilter(String itemCodeFilter) {
        this.itemCodeFilter = itemCodeFilter;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getCusGroup() {
        return cusGroup;
    }

    public void setCusGroup(String cusGroup) {
        this.cusGroup = cusGroup;
    }

    public String getTownshipId() {
        return townshipId;
    }

    public void setTownshipId(String townshipId) {
        this.townshipId = townshipId;
    }

    public String getTraderList() {
        return traderList;
    }

    public void setTraderList(String traderList) {
        this.traderList = traderList;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        if (regNo == null) {
            this.regNo = "-";
        } else if (regNo.isEmpty()) {
            this.regNo = "-";
        } else {
            this.regNo = regNo;
        }
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(String toLocationId) {
        this.toLocationId = toLocationId;
    }

    public String getLocationGroup() {
        return locationGroup;
    }

    public void setLocationGroup(String locationGroup) {
        this.locationGroup = locationGroup;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    public String getAdmNo() {
        return admNo;
    }

    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    public void setCustomG(String customG) {
        this.customG = customG;
    }

    public String getOpdCat() {
        return opdCat;
    }

    public void setOpdCat(String opdCat) {
        this.opdCat = opdCat;
    }

    public String getItemActive() {
        return itemActive;
    }

    public void setItemActive(String itemActive) {
        this.itemActive = itemActive;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
