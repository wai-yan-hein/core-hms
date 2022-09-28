/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.healper.CurrPTBalance;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CurrPTBalanceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CurrPTBalanceTableModel.class.getName());
    private List<CurrPTBalance> listBal;
    private final String[] columnNames = {"Reg No.", "Adm No", "Patient", "Balance"};
    private final AbstractDataAccess dao = Global.dao;
    private Double total = 0.0;
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Reg No.
                return String.class;
            case 1: //Adm No
                return String.class;
            case 2: //Patient
                return String.class;
            case 3: //Vou Total
                return Double.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listBal == null) {
            return null;
        }

        if (listBal.isEmpty()) {
            return null;
        }

        try {
            CurrPTBalance record = listBal.get(row);

            switch (column) {
                case 0: //Reg No.
                    return record.getRegNo();
                case 1: //Adm No.
                    return record.getAdmNo();
                case 2: //Patient
                    return record.getPtName();
                case 3: //Balance
                    return record.getBalance();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listBal == null) {
            return 0;
        } else {
            return listBal.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void getData() {
        try {
            String regNo = "-";
            String appCurr = Util1.getPropValue("system.app.currency");
            String strDate = DateUtil.getTodayDateStrMYSQL();
            String userId = Global.machineId;
            dao.execProc("patient_balance", DateUtil.getTodayDateStrMYSQL(),
                    appCurr, userId, regNo);
            
            String strSQLs = "update tmp_bill_payment tbp, (\n"
                    + "select dh.patient_id, max(dh.admission_no) admission_no, max(dh.dc_date) dc_date\n"
                    + "from dc_his dh\n"
                    + "where dh.dc_status is not null and date(dh.dc_date) between '"
                    + strDate + "' and '" + strDate + "'\n"
                    + "and dh.deleted = false\n"
                    + "group by dh.patient_id) dc\n"
                    + "set tbp.dc_date = dc.dc_date\n"
                    + "where tbp.user_id = '" + userId
                    + "' and tbp.reg_no = dc.patient_id and tbp.admission_no is null";
            dao.execSql(strSQLs);
            strSQLs = "select a.reg_no, a.currency, sum(amt) balance,bs.description,a.patient_name, a.admission_no ams_no, tbp.dc_date\n"
                    + "    from (\n"
                    + "	  select po.reg_no, po1.patient_name, po1.admission_no, po.currency, sum(ifnull(op_amount,0)) amt,\n"
                    + "	         po1.dc_date\n"
                    + "	    from patient_op po, tmp_bill_payment po1\n"
                    + "	   where date(po.op_date) <= $P{to_date} and po1.user_id = $P{user_id}\n"
                    + "	     and (po.currency = $P{currency} or '-' = $P{currency})\n"
                    + "	     and po.reg_no = po1.reg_no and po.bill_type = po1.bill_type and po.currency = po1.currency\n"
                    + "	     and po.op_date = po1.op_date\n"
                    + "	   group by po.reg_no, po1.patient_name, po1.admission_no, po.currency, po1.dc_date\n"
                    + "	   union all\n"
                    + "	  select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(a.amt), tbp.dc_date\n"
                    + "              from tmp_bill_payment tbp join (\n"
                    + "                   select sh.reg_no, sh.currency_id,date(sh.sale_date) sale_date, sum(ifnull(sh.balance,0)) amt\n"
                    + "                     from sale_his sh\n"
                    + "                    where deleted = false and sh.reg_no is not null\n"
                    + "                    group by sh.reg_no,sh.currency_id,date(sh.sale_date)) a on tbp.reg_no = a.reg_no\n"
                    + "               and tbp.currency = a.currency_id and a.sale_date between tbp.op_date and $P{to_date}\n"
                    + "             where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "             group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	   union all\n"
                    + "	  select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n"
                    + "	    from tmp_bill_payment tbp join (\n"
                    + "                    select patient_id, currency_id, date(opd_date) opd_date, sum(ifnull(vou_balance,0)) amt\n"
                    + "                      from opd_his\n"
                    + "                     where deleted = false and patient_id is not null\n"
                    + "                     group by patient_id, currency_id, date(opd_date)) oh on tbp.reg_no = oh.patient_id\n"
                    + "                and tbp.currency = oh.currency_id and date(oh.opd_date) between tbp.op_date and $P{to_date}\n"
                    + "              where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "              group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "	          select patient_id, currency_id, date(ot_date) ot_date, sum(ifnull(vou_balance,0)) amt\n"
                    + "	            from ot_his\n"
                    + "	           where deleted = false and patient_id is not null\n"
                    + "		 group by patient_id, currency_id, date(ot_date)) oh on tbp.reg_no = oh.patient_id\n"
                    + "                and tbp.currency = oh.currency_id\n"
                    + "	      and date(oh.ot_date) between tbp.op_date and $P{to_date}\n"
                    + "	    where (tbp.currency = $P{currency} or '-' = $P{currency}) and tbp.user_id = $P{user_id}\n"
                    + "	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency,sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "                    select patient_id, currency_id, date(dc_date) dc_date, sum(ifnull(vou_balance,0)) amt\n"
                    + "		 from dc_his\n"
                    + "		where deleted = false and patient_id is not null\n"
                    + "		group by patient_id, currency_id, date(dc_date)) oh on tbp.reg_no = oh.patient_id\n"
                    + "                and tbp.currency = oh.currency_id and date(oh.dc_date) between tbp.op_date and $P{to_date}\n"
                    + "	    where (tbp.currency = $P{currency} or '-' = $P{currency}) and tbp.user_id = $P{user_id}\n"
                    + "	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, (sum(ifnull(rih.amt,0))*-1) amt,\n"
                    + "                     tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "                    select reg_no, currency, date(ret_in_date) ret_in_date, sum(ifnull(balance,0)) amt\n"
                    + "		 from ret_in_his\n"
                    + "		where deleted = false and reg_no is not null\n"
                    + "	          group by reg_no, currency, date(ret_in_date)) rih on tbp.reg_no = rih.reg_no\n"
                    + "                and tbp.currency = rih.currency and date(rih.ret_in_date) between tbp.op_date and $P{to_date}\n"
                    + "	    where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name,tbp.admission_no, tbp.currency,\n"
                    + "                    (sum(ifnull(opbp.amt,0))*-1) amt, tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "                     select reg_no, currency_id, date(pay_date) pay_date, (sum(ifnull(pay_amt,0))) amt\n"
                    + "		  from opd_patient_bill_payment\n"
                    + "		 where reg_no is not null\n"
                    + "		 group by reg_no, currency_id, date(pay_date)) opbp on tbp.reg_no = opbp.reg_no\n"
                    + "                 and tbp.currency = opbp.currency_id and date(opbp.pay_date) between tbp.op_date and $P{to_date}\n"
                    + "	     where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "	     group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date) a\n"
                    + "    join admission adm on a.admission_no = adm.ams_no and adm.dc_status is null\n"
                    + "    left join building_structure bs on adm.building_structure_id = bs.id\n"
                    + "    join (select * from tmp_bill_payment where (currency = $P{currency} or '-' = $P{currency})) tbp on a.reg_no = tbp.reg_no and tbp.user_id = $P{user_id}\n"
                    + "   where (a.reg_no = $P{reg_no} or '-' = $P{reg_no})\n"
                    + "   group by a.reg_no, a.currency, bs.description,a.patient_name, tbp.dc_date\n"
                    + "   having sum(a.amt) <> 0\n"
                    + "order by a.patient_name";
            strSQLs = strSQLs.replace("$P{to_date}", "'" + strDate + "'")
                    .replace("$P{currency}", "'" + appCurr + "'")
                    .replace("$P{reg_no}", "'" + regNo + "'")
                    .replace("$P{user_id}", "'" + userId + "'");
            ResultSet rs = dao.execSQL(strSQLs);
            if(rs != null){
                listBal = new ArrayList();
                total = 0.0;
                while(rs.next()){
                    CurrPTBalance cpb = new CurrPTBalance(
                            rs.getString("reg_no"),
                            rs.getString("ams_no"),
                            rs.getString("patient_name"),
                            rs.getDouble("balance")
                    );
                    total += cpb.getBalance();
                    listBal.add(cpb);
                }
                rs.close();
                fireTableDataChanged();
            }
        } catch (Exception ex) {
            log.error("getData : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public Double getTotal() {
        return total;
    }
    
    public int getCount(){
        if(listBal != null){
            return listBal.size();
        }else{
            return 0;
        }
    }
    
    public CurrPTBalance getPatientBalance(int index){
        if(listBal != null){
            return listBal.get(index);
        }else{
            return null;
        }
    }
}
