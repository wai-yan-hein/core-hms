drop view if exists v_session;
create algorithm=undefined definer=root@localhost sql security definer view v_session as select sh.sale_date as tran_date,sh.sale_inv_id as inv_id,ifnull(sh.cus_id,pd.reg_no) as cus_id,ifnull(t.trader_name,pd.patient_name) as trader_name,sh.vou_total as vou_total,sh.paid_amount as paid,sh.discount as discount,sh.balance as balance,sh.sale_exp_total as expense,sh.created_by as user_id,au.user_short_name as user_short_name,sh.session_id as session_id,sh.intg_upd_status as intg_upd_status,substr(sh.sale_inv_id,1,2) as machine,sh.location_id as location_id,ifnull(sh.deleted,0) as deleted,'Sale' as source,sh.currency_id as currency,sh.paid_currency as paid_currency,sh.paid_currency_amt as paid_currency_amt,t.discriminator as discriminator,sh.payment_type_id as payment_type,sh.tax_p as tax_p,sh.tax_amt as tax_amt,sh.remark as ref_no,0 as exp_type,'' as expense_name,sh.account_id as tran_account_id,t.account_code as cv_account_code,sh.sale_exp_total_in as exp_in,t.group_id as group_id,s.session_name as session_name,pt.payment_type_name as payment_type_name,if(sh.admission_no = '',null,sh.admission_no) as admission_no,sh.doctor_id as doctor_id,dr.doctor_name as doctor_name,null as account_id,t.stu_no as stu_no from ((((((sale_his sh left join trader t on(sh.cus_id = t.trader_id)) left join appuser au on(sh.created_by = au.user_id)) left join patient_detail pd on(sh.reg_no = pd.reg_no)) left join session s on(sh.session_id = s.session_id)) left join payment_type pt on(sh.payment_type_id = pt.payment_type_id)) left join doctor dr on(sh.doctor_id = dr.doctor_id)) union all select ph.pur_date as tran_date,ph.pur_inv_id as inv_id,ph.cus_id as cus_id,t.trader_name as trader_name,ph.vou_total as vou_total,ph.paid as paid,ph.discount as discount,ph.balance as balance,ph.pur_exp_total as expense,ph.created_by as user_id,au.user_short_name as user_short_name,ph.session_id as session_id,ph.intg_upd_status as intg_upd_status,substr(ph.pur_inv_id,1,2) as machine,ph.location as location,ifnull(ph.deleted,0) as deleted,'Purchase' as source,ph.currency as currency,ph.currency as paid_currency,0 as paid_currency_amt,t.discriminator as discriminator,ph.payment_type as payment_type,ph.tax_p as tax_p,ph.tax_amt as tax_amt,ph.ref_no as ref_no,0 as exp_type,'' as expense_name,ph.account_id as tran_account_id,t.account_code as cv_account_code,0 as exp_in,t.group_id as group_id,s.session_name as session_name,pt.payment_type_name as payment_type_name,null as admission_no,null as doctor_id,null as doctor_name,null as account_id,t.stu_no as stu_no from ((((pur_his ph left join trader t on(ph.cus_id = t.trader_id)) left join appuser au on(ph.created_by = au.user_id)) left join session s on(ph.session_id = s.session_id)) left join payment_type pt on(ph.payment_type = pt.payment_type_id)) union all select rih.ret_in_date as tran_date,rih.ret_in_id as inv_id,ifnull(rih.cus_id,pd.reg_no) as cus_id,ifnull(t.trader_name,pd.patient_name) as trader_name,rih.vou_total as vou_total,rih.paid as paid,0 as discount,rih.balance as balance,0 as expense,rih.created_by as user_id,au.user_short_name as user_short_name,rih.session_id as session_id,rih.intg_upd_status as intg_upd_status,substr(rih.ret_in_id,1,2) as machine,rih.location as location,ifnull(rih.deleted,0) as deleted,'Return In' as source,rih.currency as currency,rih.currency as paid_currency,0 as paid_currency_amt,t.discriminator as discriminator,rih.payment_type as payment_type,0 as tax_p,0 as tax_amt,'' as ref_no,0 as exp_type,'' as expense_name,rih.account_id as tran_account_id,t.account_code as cv_account_code,0 as exp_in,t.group_id as group_id,s.session_name as session_name,pt.payment_type_name as payment_type_name,if(rih.admission_no = '',null,rih.admission_no) as admission_no,null as doctor_id,null as doctor_name,null as account_id,t.stu_no as stu_no from (((((ret_in_his rih left join trader t on(rih.cus_id = t.trader_id)) left join appuser au on(rih.created_by = au.user_id)) left join patient_detail pd on(rih.reg_no = pd.reg_no)) left join session s on(rih.session_id = s.session_id)) left join payment_type pt on(rih.payment_type = pt.payment_type_id)) union all select roh.ret_out_date as tran_date,roh.ret_out_id as inv_id,roh.cus_id as cus_id,t.trader_name as trader_name,roh.vou_total as vou_total,roh.paid as paid,0 as discount,roh.balance as balance,0 as expense,roh.created_by as user_id,au.user_short_name as user_short_name,roh.session_id as session_id,roh.intg_upd_status as intg_upd_status,substr(roh.ret_out_id,1,2) as machine,roh.location as location,ifnull(roh.deleted,0) as deleted,'Return Out' as source,roh.currency as currency,roh.currency as paid_currency,0 as paid_currency_amt,t.discriminator as discriminator,roh.payment_type as payment_type,0 as tax_p,0 as tax_amt,'' as ref_no,0 as exp_type,'' as expense_name,roh.account_id as tran_account_id,t.account_code as cv_account_code,0 as exp_in,t.group_id as group_id,s.session_name as session_name,pt.payment_type_name as payment_type_name,null as admission_no,null as doctor_id,null as doctor_name,null as account_id,t.stu_no as stu_no from ((((ret_out_his roh left join trader t on(roh.cus_id = t.trader_id)) left join appuser au on(roh.created_by = au.user_id)) left join session s on(roh.session_id = s.session_id)) left join payment_type pt on(roh.payment_type = pt.payment_type_id)) union all select ph.pay_date as tran_date,ph.payment_id as inv_id,ph.trader_id as cus_id,t.trader_name as trader_name,0 as vou_total,if(t.discriminator = 'S',ph.paid_amtc * -1,ph.paid_amtc) as paid,0 as discount,0 as balance,0 as expense,ph.created_by as user_id,au.user_short_name as user_short_name,ph.session_id as session_id,ph.intg_upd_status as intg_upd_status,coalesce(ph.machine_id,0) as machine,ph.location_id as location_id,ifnull(ph.deleted,0) as deleted,'Trader Payment' as source,ph.currency_id as currency,ph.currency_id as paid_currency,0 as paid_currency_amt,t.discriminator as discriminator,if(ph.pay_option = 'Cash',1,0) as payment_type,0 as tax_p,0 as tax_amt,'' as ref_no,0 as exp_type,'' as expense_name,ph.account_id as tran_account_id,t.account_code as cv_account_code,0 as exp_in,t.group_id as group_id,s.session_name as session_name,'Cash' as payment_type_name,null as admission_no,null as doctor_id,null as doctor_name,ph.account_id as account_id,t.stu_no as stu_no from (((payment_his ph left join trader t on(ph.trader_id = t.trader_id)) left join appuser au on(ph.created_by = au.user_id)) left join session s on(ph.session_id = s.session_id)) union all select cast(ge.exp_date as date) as tran_date,ge.gene_id as inv_id,' ' as cus_id,ge.desp as trader_name,0 as vou_total,0 as paid,0 as discount,0 as balance,ge.exp_amount as expense,ge.created_by as user_id,au.user_short_name as user_short_name,ge.session_id as session_id,null as intg_upd_status,'00' as machine,ge.location_id as location_id,0 as deleted,if(ifnull(ge.exp_amount,0) = 0,'Cash In','Cash Out') as source,'MMK' as currency,'MMK' as paid_currency,if(ifnull(ge.exp_amount,0) = 0,ge.dr_amt,ge.exp_amount) as paid_currency_amt,'E' as discriminator,1 as payment_type,0 as tax_p,0 as tax_amt,'' as ref_no,ge.exp_type as exp_type,et.expense_name as expense_name,'' as tran_account_id,'' as cv_account_code,ge.dr_amt as exp_in,'-' as group_id,s.session_name as session_name,'Cash' as payment_type_name,null as admission_no,null as doctor_id,null as doctor_name,null as account_id,null as stu_no from (((gen_expense ge left join appuser au on(ge.created_by = au.user_id)) left join expense_type et on(ge.exp_type = et.expense_type_id)) left join session s on(ge.session_id = s.session_id)) union all select th.tran_date as tran_date,th.transfer_id as inv_id,th.cus_id as cus_id,t.trader_name as trader_name,th.vou_total as vou_total,0 as paid,0 as discount,th.vou_total as balance,0 as expense,th.created_by as user_id,au.user_short_name as user_short_name,th.session_id as session_id,th.intg_upd_status_sale as intg_upd_status,substr(th.transfer_id,1,2) as machine,th.from_location as location_id,th.deleted as deleted,'TRAN-Sale' as source,'MMK' as currency,null as paid_currency,0 as paid_currency_amt,t.discriminator as discriminator,2 as payment_type,0 as tax_p,0 as tax_amt,th.remark as ref_no,0 as exp_type,'' as expense_name,null as tran_account_id,t.account_code as cv_account_code,0 as exp_in,t.group_id as group_id,s.session_name as session_name,'Credit' as payment_type_name,null as admission_no,null as doctor_id,null as doctor_name,null as account_id,t.stu_no as stu_no from (((transfer_his th join trader t on(th.cus_id = t.trader_id)) left join appuser au on(th.created_by = au.user_id)) left join session s on(th.session_id = s.session_id)) union all select th.tran_date as tran_date,th.transfer_id as inv_id,th.cus_id as cus_id,t.trader_name as trader_name,th.vou_total as vou_total,0 as paid,0 as discount,th.vou_total as balance,0 as expense,th.created_by as user_id,au.user_short_name as user_short_name,th.session_id as session_id,th.intg_upd_status_pur as intg_upd_status,substr(th.transfer_id,1,2) as machine,th.to_location as location_id,th.deleted as deleted,'TRAN-Purchase' as source,'MMK' as currency,null as paid_currency,0 as paid_currency_amt,t.discriminator as discriminator,2 as payment_type,0 as tax_p,0 as tax_amt,th.remark as ref_no,0 as exp_type,'' as expense_name,null as tran_account_id,t.account_code as cv_account_code,0 as exp_in,t.group_id as group_id,s.session_name as session_name,'Credit' as payment_type_name,null as admission_no,null as doctor_id,null as doctor_name,null as account_id,t.stu_no as stu_no from (((transfer_his th join trader t on(th.sup_id = t.trader_id)) left join appuser au on(th.created_by = au.user_id)) left join session s on(th.session_id = s.session_id));

alter table trader_op 
add column intg_upd_status varchar(15) null after currency;

alter table opd_category 
add column expense bit(1) not null default 0;

alter table inp_category 
add column expense bit(1) not null default 0;

alter table ot_group 
add column expense bit(1) not null default 0;

alter table opd_category 
add column intg_upd_status varchar(15);

alter table inp_category 
add column intg_upd_status varchar(15);

alter table ot_group 
add column intg_upd_status varchar(15);

alter table opd_patient_bill_payment 
add column deleted  bit(1) not null default 0;

ALTER TABLE `opd_patient_bill_payment` 
ADD COLUMN `intg_upd_status` VARCHAR(15) NULL AFTER `deleted`;

drop view if exists v_opd_patient_bill_payment;
create view v_opd_patient_bill_payment as select opbp.id as id,opbp.reg_no as reg_no,opbp.bill_type_id as bill_type_id,opbp.currency_id as currency_id,opbp.pay_amt as pay_amt,opbp.pay_date as pay_date,opbp.remark as remark,opbp.created_by as created_by,opbp.created_date as created_date,opbp.deleted as deleted,pd.patient_name as patient_name,pd.father_name as father_name,pt.payment_type_name as payment_type_name,usr.user_name as user_name,usr.user_short_name as user_short_name from (((patient_detail pd join opd_patient_bill_payment opbp) join payment_type pt) join appuser usr) where pd.reg_no = opbp.reg_no and opbp.bill_type_id = pt.payment_type_id and opbp.created_by = usr.user_id;

drop procedure patient_bill_payment;
DELIMITER $$
create  procedure `patient_bill_payment`(in prm_patient_id varchar(15),
  in prm_date datetime, in prm_curr_id varchar(5), in prm_user_id varchar(15))
begin
  delete from tmp_bill_payment where user_id = prm_user_id;

  insert into tmp_bill_payment(reg_no, currency, bill_type, op_date, user_id)
  select a.reg_no, a.cur_code, a.payment_type_id, ifnull(max(op_date), '1900-01-01') op_date, prm_user_id
    from (select pd.reg_no, cur.cur_code, pt.payment_type_id from patient_detail pd, currency cur, payment_type pt where pd.reg_no = prm_patient_id) a 
    left join (select * from patient_op po where reg_no = prm_patient_id and date(op_date) <= date(prm_date) and (currency = prm_curr_id or '-' = prm_curr_id)) po 
      on a.reg_no = po.reg_no and a.cur_code = po.currency and a.payment_type_id = po.bill_type
   where a.reg_no = prm_patient_id and (a.cur_code = prm_curr_id or prm_curr_id = '-')
   group by a.reg_no, a.cur_code, a.payment_type_id;
  
  select a.reg_no, a.currency, a.bill_type, pt.payment_type_name, sum(amt) balance
    from (
  select po.reg_no, po.currency, po.bill_type, sum(ifnull(op_amount,0)) amt
    from patient_op po, tmp_bill_payment po1
   where po.reg_no = prm_patient_id and date(po.op_date) <= date(prm_date) and po1.user_id = prm_user_id 
     and (po.currency = prm_curr_id or '-' = prm_curr_id)
     and po.reg_no = po1.reg_no and po.bill_type = po1.bill_type and po.currency = po1.currency and po.op_date = po1.op_date
   group by po.reg_no, po.currency, po.bill_type
  union all
  select tbp.reg_no, tbp.currency, tbp.bill_type, sum(ifnull(sh.balance,0)) amt
    from tmp_bill_payment tbp, sale_his sh
   where tbp.reg_no = sh.reg_no and tbp.currency = sh.currency_id and tbp.bill_type = sh.payment_type_id
     and sh.reg_no = prm_patient_id and date(sh.sale_date) between tbp.op_date and prm_date
     and tbp.user_id = prm_user_id
     and (sh.currency_id = prm_curr_id or '-' = prm_curr_id) and sh.deleted = false
   group by tbp.reg_no, tbp.currency, tbp.bill_type 
   union all
  select tbp.reg_no, tbp.currency, tbp.bill_type, sum(ifnull(oh.vou_balance,0)) amt
    from tmp_bill_payment tbp, opd_his oh
   where tbp.reg_no = oh.patient_id and tbp.currency = oh.currency_id and tbp.bill_type = oh.payment_id
     and oh.patient_id = prm_patient_id and date(oh.opd_date) between tbp.op_date and prm_date
     and tbp.user_id = prm_user_id
     and (oh.currency_id = prm_curr_id or '-' = prm_curr_id) and oh.deleted = false
   group by tbp.reg_no, tbp.currency, tbp.bill_type
  union all
  select tbp.reg_no, tbp.currency, tbp.bill_type, sum(ifnull(oh.vou_balance,0)) amt
    from tmp_bill_payment tbp, ot_his oh
   where tbp.reg_no = oh.patient_id and tbp.currency = oh.currency_id and tbp.bill_type = oh.payment_id
     and oh.patient_id = prm_patient_id and date(oh.ot_date) between tbp.op_date and prm_date
     and (oh.currency_id = prm_curr_id or '-' = prm_curr_id) and oh.deleted = false and tbp.user_id = prm_user_id
   group by tbp.reg_no, tbp.currency, tbp.bill_type
   union all
  select tbp.reg_no, tbp.currency, tbp.bill_type, sum(ifnull(oh.vou_balance,0)) amt
    from tmp_bill_payment tbp, dc_his oh
   where tbp.reg_no = oh.patient_id and tbp.currency = oh.currency_id and tbp.bill_type = oh.payment_id
     and oh.patient_id = prm_patient_id and date(oh.dc_date) between tbp.op_date and prm_date
     and (oh.currency_id = prm_curr_id or '-' = prm_curr_id) and oh.deleted = false and tbp.user_id = prm_user_id
   group by tbp.reg_no, tbp.currency, tbp.bill_type 
   union all
  select tbp.reg_no, tbp.currency, tbp.bill_type, (sum(ifnull(rih.balance,0))*-1) amt
    from tmp_bill_payment tbp, ret_in_his rih
   where tbp.reg_no = rih.reg_no and tbp.currency = rih.currency and tbp.bill_type = rih.payment_type
     and rih.reg_no = prm_patient_id and date(rih.ret_in_date) between tbp.op_date and prm_date
     and tbp.user_id = prm_user_id
     and (rih.currency = prm_curr_id or '-' = prm_curr_id) and rih.deleted = false
   group by tbp.reg_no, tbp.currency, tbp.bill_type
   union all
  select tbp.reg_no, tbp.currency, tbp.bill_type, (sum(ifnull(opbp.pay_amt,0))*-1) amt
    from tmp_bill_payment tbp, opd_patient_bill_payment opbp
   where tbp.reg_no = opbp.reg_no and tbp.currency = opbp.currency_id and tbp.bill_type = opbp.bill_type_id
     and opbp.reg_no = prm_patient_id and date(opbp.pay_date) between tbp.op_date and prm_date
     and tbp.user_id = prm_user_id
     and (opbp.currency_id = prm_curr_id or '-' = prm_curr_id)
     and opbp.deleted = 0
   group by tbp.reg_no, tbp.currency, tbp.bill_type) a, payment_type pt
   where a.bill_type = pt.payment_type_id group by a.reg_no, a.currency, a.bill_type, pt.payment_type_name;
   
end$$
DELIMITER ;

drop procedure patient_balance_detail;
DELIMITER $$
create  procedure `patient_balance_detail`(in prm_patient_id varchar(15),
  in prm_from_date datetime, in prm_to_date datetime, in prm_curr_id varchar(5), in prm_user_id varchar(15))
begin
declare var_done int default false;
declare var_tran_date date;
declare var_reg_no varchar(15);
declare var_admission_no varchar(15);
declare var_curr_id varchar(5);
declare var_op_balance double;
declare var_op_balance1 double;
declare var_pharmacy_amt double;
declare var_pharmacy_paid_amt double;
declare var_opd_amt double;
declare var_opd_paid_amt double;
declare var_ot_amt double;
declare var_ot_paid_amt double;
declare var_dc_amt double;
declare var_dc_paid_amt double;
declare var_retin_amt double;
declare var_retin_paid_amt double;
declare var_pay_amt double;
declare var_closing double;

declare patient_in_out cursor for
   select a.tran_date, a.reg_no, a.admission_no, a.curr_id, 
       sum(if(a.tran_option='Opening',a.ttl_op_amt,0)) as op_balance,
       sum(if(a.tran_option='Pharmacy',a.ttl_amt,0)) as pharmacy_amt,
       sum(if(a.tran_option='Pharmacy',a.ttl_paid,0)) as pharmacy_paid_amt,
       sum(if(a.tran_option='OPD',a.ttl_amt,0)) as opd_amt,
       sum(if(a.tran_option='OPD',a.ttl_paid,0)) as opd_paid_amt,
       sum(if(a.tran_option='OT',a.ttl_amt,0)) as ot_amt,
       sum(if(a.tran_option='OT',a.ttl_paid,0)) as ot_paid_amt,
       sum(if(a.tran_option='DC',a.ttl_amt,0)) as dc_amt,
       sum(if(a.tran_option='DC',a.ttl_paid,0)) as dc_paid_amt,
       sum(if(a.tran_option='Return In',a.ttl_amt,0)) as retin_amt,
       sum(if(a.tran_option='Return In',a.ttl_paid,0)) as retin_paid_amt,
       sum(if(a.tran_option='payment',a.ttl_paid,0)) as pay_amt
	from (	  
	  select tran_date, tran_option, trader_id as reg_no, null as admission_no, curr_id, sum(amount) as ttl_op_amt,
			 0 as ttl_amt, 0 as ttl_paid
		from tmp_patient_bal_date
	   where user_id = prm_user_id
	   group by tran_date, tran_option, trader_id, curr_id
	   union all
	  select date(sh.sale_date) as tran_date, 'Pharmacy' as tran_option,sh.reg_no, sh.admission_no, sh.currency_id as curr_id ,
			 0 as ttl_op_amt, sum(ifnull(sh.vou_total,0)) as ttl_amt, sum(ifnull(sh.paid_amount,0)+ifnull(sh.discount,0)) as ttl_paid
		from tmp_patient_bal_date tpbd, sale_his sh
	   where tpbd.trader_id = sh.reg_no and tpbd.curr_id = sh.currency_id
		 and date(sh.sale_date) between prm_from_date and prm_to_date
		 and tpbd.user_id = prm_user_id
		 and (sh.currency_id = prm_curr_id or '-' = prm_curr_id) and sh.deleted = false
	   group by date(sh.sale_date), sh.reg_no, sh.admission_no, sh.currency_id
	   union all
	  select date(oh.opd_date) as tran_date, 'OPD' as tran_option, oh.patient_id as reg_no, oh.admission_no, 
			 oh.currency_id as curr_id, 0 as ttl_op_amt, sum(ifnull(oh.vou_total,0)) ttl_amt,
			 sum(ifnull(oh.paid,0)+ifnull(oh.disc_a,0)) as ttl_paid
		from tmp_patient_bal_date tpbd, opd_his oh
	   where tpbd.trader_id = oh.patient_id and tpbd.curr_id = oh.currency_id
		 and date(oh.opd_date) between prm_from_date and prm_to_date
		 and tpbd.user_id = prm_user_id
		 and (oh.currency_id = prm_curr_id or '-' = prm_curr_id) and oh.deleted = false
	   group by date(oh.opd_date), oh.patient_id, oh.admission_no, oh.currency_id 
	  union all
	  select date(oh.ot_date) as tran_date, 'OT' as tran_option, oh.patient_id as reg_no, oh.admission_no, 
			 oh.currency_id as curr_id, 0 as ttl_op_amt, sum(ifnull(oh.vou_total,0)) as ttl_amt,
			 sum(ifnull(oh.paid,0)+ifnull(oh.disc_a,0)) as ttl_paid
		from tmp_patient_bal_date tpbd, ot_his oh
	   where tpbd.trader_id = oh.patient_id and tpbd.curr_id = oh.currency_id
		 and date(oh.ot_date) between prm_from_date and prm_to_date
		 and (oh.currency_id = prm_curr_id or '-' = prm_curr_id) and oh.deleted = false
		 and tpbd.user_id = prm_user_id
	   group by date(oh.ot_date), oh.patient_id, oh.admission_no, oh.currency_id
	   union all
	  select date(oh.dc_date) as tran_date, 'DC' as tran_option, oh.patient_id as reg_no, oh.admission_no, 
			 oh.currency_id as curr_id, 0 as ttl_op_amt, sum(ifnull(oh.vou_total,0)) as ttl_amt, 
			 sum(ifnull(oh.paid,0)+ifnull(oh.disc_a,0)) as ttl_paid
		from tmp_patient_bal_date tpbd, dc_his oh
	   where tpbd.trader_id = oh.patient_id and tpbd.curr_id = oh.currency_id
		 and date(oh.dc_date) between prm_from_date and prm_to_date
		 and (oh.currency_id = prm_curr_id or '-' = prm_curr_id) and oh.deleted = false
		 and tpbd.user_id = prm_user_id
	   group by date(oh.dc_date), oh.patient_id, oh.admission_no, oh.currency_id
	   union all
	  select date(rih.ret_in_date) as tran_date, 'Return In' as tran_option, rih.reg_no, rih.admission_no, 
			 rih.currency as curr_id, 0 as ttl_op_amt, sum(ifnull(rih.vou_total,0)) as ttl_amt,
			 sum(ifnull(rih.paid,0)) as ttl_paid
		from tmp_patient_bal_date tpbd, ret_in_his rih
	   where tpbd.trader_id = rih.reg_no and tpbd.curr_id = rih.currency
		 and date(rih.ret_in_date) between prm_from_date and prm_to_date
		 and tpbd.user_id = prm_user_id
		 and (rih.currency = prm_curr_id or '-' = prm_curr_id) and rih.deleted = false
	   group by date(rih.ret_in_date), rih.reg_no, rih.admission_no, rih.currency
	   union all
	  select date(opbp.pay_date) as tran_date, 'Payment' as tran_option, opbp.reg_no, null as admission_no, 
			 opbp.currency_id as curr_id, 0 as ttl_op_amt, 0 as ttl_amount, sum(ifnull(opbp.pay_amt,0)) as ttl_paid
		from tmp_patient_bal_date tpbd, opd_patient_bill_payment opbp
	   where tpbd.trader_id = opbp.reg_no and tpbd.curr_id = opbp.currency_id
		 and date(opbp.pay_date) between prm_from_date and prm_to_date
		 and tpbd.user_id = prm_user_id
         and opbp.deleted = 0
		 and (opbp.currency_id = prm_curr_id or '-' = prm_curr_id)
	   group by date(opbp.pay_date), opbp.reg_no, opbp.currency_id) a
	group by a.tran_date, a.reg_no, a.admission_no, a.curr_id
	order by a.reg_no, a.curr_id, a.tran_date;

declare continue handler for not found set var_done = true;

  call patient_balance_date(prm_patient_id, date_sub(prm_from_date, interval 1 day), prm_curr_id, prm_user_id);
  
  delete from tmp_patient_in_out where user_id = prm_user_id;
  commit;
  
  open patient_in_out;
  
  loop_start: loop
    fetch patient_in_out into var_tran_date,var_reg_no,var_admission_no,var_curr_id,var_op_balance,
							  var_pharmacy_amt,var_pharmacy_paid_amt,var_opd_amt,var_opd_paid_amt,
							  var_ot_amt,var_ot_paid_amt,var_dc_amt,var_dc_paid_amt,var_retin_amt,
							  var_retin_paid_amt,var_pay_amt;

        if var_done then
            leave loop_start;
        end if;

        if ifnull(var_op_balance1,'-') = '-' then
            set var_op_balance1 = var_op_balance;
        end if;
       
        
        set var_closing = var_op_balance1 + var_pharmacy_amt+var_opd_amt+var_ot_amt+var_dc_amt-
							(var_pharmacy_paid_amt+var_opd_paid_amt+var_ot_paid_amt+var_dc_paid_amt+
                             var_pay_amt + (var_retin_amt-var_retin_paid_amt));

        insert into tmp_patient_in_out(tran_date, reg_no, admission_no, curr_id, opening_bal,
                    phar_amt, phar_paid, opd_amt, opd_paid, ot_amt, ot_paid, dc_amt, dc_paid, 
                    retin_amt, retin_paid, patient_payment_opd, closing_amt, user_id)
        values(var_tran_date, var_reg_no, var_admission_no, var_curr_id, var_op_balance1,
               var_pharmacy_amt, var_pharmacy_paid_amt, var_opd_amt, var_opd_paid_amt,
               var_ot_amt, var_ot_paid_amt, var_dc_amt, var_dc_paid_amt,
               var_retin_amt, var_retin_paid_amt, var_pay_amt, var_closing, prm_user_id);
        set var_op_balance1 = var_closing;
  end loop;
  commit;
end$$
DELIMITER ;

ALTER TABLE `tmp_amount_link` 
ADD COLUMN `discount` DOUBLE NULL AFTER `print_status`;

ALTER TABLE `sale_his` 
ADD COLUMN `bt_id` VARCHAR(15);

ALTER TABLE `opd_his` 
ADD COLUMN `bt_id` VARCHAR(15);

ALTER TABLE `dc_his` 
ADD COLUMN `bt_id` VARCHAR(15);

ALTER TABLE `ot_his` 
ADD COLUMN `bt_id` VARCHAR(15);

set sql_safe_updates =0;
update building_structure b join(
select reg_no,building_structure_id from admission where dc_status is null) c on b.id = c.building_structure_id
set b.reg_no = c.reg_no

alter table pay_method 
add column allow_amt double null after updated_date,
add column factor int null after allow_amt,
add column trader_id varchar(15) null after factor,
add column group_code varchar(15) null after trader_id;


alter table opd_details_his 
add column need_doctor bit(1) null;


alter table ot_details_his 
add column need_doctor bit(1) null;


alter table dc_details_his 
add column need_doctor bit(1) null;

