set sql_safe_updates=0;

#delete from adj_join;
delete from adj_detail_his;
delete from adj_his;
#alter table adj_detail_his auto_increment = 1;

#delete from dmg_join;
delete from dmg_detail_his;
delete from dmg_his;
#alter table dmg_detail_his auto_increment = 1;

#delete from op_join;
delete from stock_op_detail_his;
delete from stock_op_his;
#alter table stock_op_detail_his auto_increment = 1;

#delete from opd_join;
delete from opd_details_his;
delete from opd_his;
#alter table opd_details_his auto_increment = 1;

#delete from template_join;
delete from packing_template_detail;
delete from packing_template;
#alter table packing_template_detail auto_increment = 1;

delete from pay_his_join;
delete from payment_vou;
delete from payment_his;
alter table payment_vou auto_increment = 1;

delete from pc_his_join;
delete from pcm_his_join;
delete from price_change_med_his;
delete from price_change_his;
delete from price_change_unit_his;
alter table price_change_med_his auto_increment = 1;
alter table price_change_unit_his auto_increment = 1;

#delete from pur_join;
#delete from pur_expense_join;
#delete from purchase_outs_join;
delete from pur_detail_his;
delete from purchase_expense;
delete from purchase_outstanding;
delete from pur_his;
#alter table pur_detail_his auto_increment = 1;
#alter table purchase_expense auto_increment = 1;
#alter table purchase_outstanding auto_increment = 1;

#delete from ret_in_join;
delete from ret_in_detail_his;
delete from ret_in_his;
#alter table ret_in_detail_his auto_increment = 1;

#delete from ret_out_join;
delete from ret_out_detail_his;
delete from ret_out_his;
#alter table ret_out_detail_his auto_increment = 1;

#delete from sale_join;
#delete from sale_expense_join;
#delete from sale_outs_join;
#delete from sale_warr_join;
delete from sale_detail_his;
delete from sale_expense;
delete from sale_outstanding;
delete from sale_warranty;
delete from sale_his;
#alter table sale_detail_his auto_increment = 1;
#alter table sale_expense auto_increment = 1;
#alter table sale_outstanding auto_increment = 1;
#alter table sale_warranty auto_increment = 1;

#delete from stock_issue_join;
delete from stock_issue_detail_his;
delete from stock_issue_his;
#alter table stock_issue_detail_his auto_increment = 1;

#delete from stock_receive_join;
delete from stock_receive_detail_his;
delete from stock_receive_his;
#alter table stock_receive_detail_his auto_increment = 1;

#delete from tran_join;
delete from transfer_detail_his;
delete from transfer_his;
#alter table transfer_detail_his auto_increment = 1;

#delete from dc_dr_fee_join;
delete from dc_doctor_fee;
#delete from dc_join;
delete from dc_details_his;
delete from dc_his;
#alter table dc_details_his auto_increment = 1;

#delete from opd_join;
delete from opd_details_his;
delete from opd_his;
#alter table opd_details_his auto_increment = 1;

#delete from ot_join;
#delete from ot_dr_fee_join;
delete from ot_doctor_fee;
delete from ot_details_his;
delete from ot_his;
#alter table ot_doctor_fee auto_increment = 1;
#alter table ot_details_his auto_increment = 1;

#delete from doctor_fee_join;
delete from doctor_fees_map;
delete from doctor;
#alter table doctor_fees_map auto_increment = 1;

delete from med_rel;
delete from relation_group;
alter table relation_group auto_increment = 1;

delete from re_order_level;
delete from opd_med_usage;
delete from inp_med_usage;
delete from medicine;

delete from template_join;
delete from packing_template_detail;
delete from packing_template;
alter table packing_template_detail auto_increment = 1;

delete from category;
alter table category auto_increment = 1;

delete from expense_type;
delete from gen_expense;
delete from gross_profits;
delete from item_brand;
alter table item_brand auto_increment = 1;
delete from item_group;
delete from item_group_detail;
delete from item_type;
delete from machine_info;
delete from med_op_date;

delete from service_fees;
delete from opd_service;
delete from opd_category;

delete from city;
alter table city auto_increment = 1;

delete from speciality;
alter table speciality auto_increment = 1;

delete from inp_service;
alter table inp_service auto_increment = 1;
delete from inp_category;
alter table inp_category auto_increment = 1;

delete from ot_service;
alter table ot_service auto_increment = 1;
delete from ot_group;
alter table ot_group auto_increment = 1;

delete from admission;
delete from patient_op;
delete from patient_detail;
delete from re_order_level;
delete from sequence;
delete from service_fees;
delete from tmp_barcode_filter;
delete from tmp_cost_details;
delete from tmp_costing_detail;
delete from tmp_cus_in_out;
delete from tmp_item_code_filter;
delete from tmp_minus_fixed;
delete from tmp_stock_bal_outs;
delete from tmp_stock_balance_exp;
delete from tmp_stock_costing;
delete from tmp_stock_filter;
delete from tmp_stock_in_out;
delete from tmp_stock_op_detail_his;
delete from tmp_trader_bal_date;
delete from tmp_trader_bal_filter;
delete from tmp_trader_filter;
delete from tmp_trader_in_out_summary;
delete from tmp_trader_unpaid_vou;
delete from tmp_vou_code_filter;
delete from tmp_vou_filter;
delete from tmp_vou_srv_filter_clinic;
delete from trader_op;
delete from customer_group;
delete from trader_tran;
delete from trader_tran_detail;
delete from trader;
delete from township;
alter table township auto_increment = 1;
delete from vou_id;

truncate table bk_dc_details_his;
truncate table bk_dc_his;
truncate table bk_medicine;
truncate table bk_opd_details_his;
truncate table bk_opd_his;
truncate table bk_ot_details_his;
truncate table bk_ot_his;
truncate table bk_payment_his;
truncate table bk_pur_detail_his;
truncate table bk_pur_his;
truncate table bk_purchase_expense;
truncate table bk_purchase_outstanding;
truncate table bk_ret_in_detail_his;
truncate table bk_ret_in_his;
truncate table bk_ret_out_detail_his;
truncate table bk_ret_out_his;
truncate table bk_sale_detail_his;
truncate table bk_sale_his;
truncate table bk_sale_outstanding;
truncate table bk_sale_warranty;
truncate table bk_trader;
truncate table machine_info;