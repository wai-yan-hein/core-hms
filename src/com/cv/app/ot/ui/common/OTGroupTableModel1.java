/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.common.Global;
import com.cv.app.ot.database.entity.OTProcedureGroup;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTGroupTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTGroupTableModel1.class.getName());
    private List<OTProcedureGroup> listOTG = new ArrayList();
    private final String[] columnNames = {"Description", "OPD Acc Code", "IPD Acc Code",
        "Dep Code", "Staff", "Nurse", "MO", "Payable Acc Code", "Payable Acc Opt", "Expense"};
    private final AbstractDataAccess dao;

    public OTGroupTableModel1(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 9) {
            return Boolean.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        OTProcedureGroup record = listOTG.get(row);

        switch (column) {
            case 0: //Description
                return record.getGroupName();
            case 1: //opd Id
                return record.getOpdAccCode();
            case 2:
                return record.getIpdAccCode();
            case 3:
                return record.getDepCode();
            case 4:
                return record.getSrvF2AccId();
            case 5:
                return record.getSrvF3AccId();
            case 6:
                return record.getSrvF4AccId();
            case 7:
                return record.getPayableAccId();
            case 8:
                return record.getPayableAccOpt();
            case 9:
                return record.isExpense();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OTProcedureGroup record = listOTG.get(row);

        switch (column) {
            case 0: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setGroupName(value.toString());

                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OT Group",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1: //opd Id
                if (value != null) {
                    record.setOpdAccCode(value.toString());
                } else {
                    record.setOpdAccCode(null);
                }
                break;
            case 2: //ipd Id
                if (value != null) {
                    record.setIpdAccCode(value.toString());
                } else {
                    record.setIpdAccCode(null);
                }
                break;
            case 3: //Dep Id
                if (value != null) {
                    record.setDepCode(value.toString());
                } else {
                    record.setDepCode(null);
                }
                break;
            case 4: //
                if (value != null) {
                    record.setSrvF2AccId(value.toString());
                } else {
                    record.setSrvF2AccId(null);
                }
                break;
            case 5: //
                if (value != null) {
                    record.setSrvF3AccId(value.toString());
                } else {
                    record.setSrvF3AccId(null);
                }
                break;
            case 6: //
                if (value != null) {
                    record.setSrvF4AccId(value.toString());
                } else {
                    record.setSrvF4AccId(null);
                }
                break;
            case 7: //
                if (value != null) {
                    record.setPayableAccId(value.toString());
                } else {
                    record.setPayableAccId(null);
                }
                break;
            case 8: //
                if (value != null) {
                    record.setPayableAccOpt(value.toString());
                } else {
                    record.setPayableAccOpt(null);
                }
                break;
            case 9:
                Boolean ex = (Boolean) value;
                record.setExpense(ex);
                break;
        }

        save(record);
        fireTableCellUpdated(row, column);
    }

    private void save(OTProcedureGroup record) {
        if (!Util1.getString(record.getGroupName(), "-").equals("-")) {
            try {
                int groupId = NumberUtil.NZeroInt(record.getGroupId());
                String strSql = "insert into bk_ot_group(group_id, group_name, sort_order, mig_id, opd_acc_code, ipd_acc_code,\n"
                        + "  dep_code, srvf1_acc_id, srvf2_acc_id, srvf3_acc_id, srvf4_acc_id, srvf5_acc_id,\n"
                        + "  payable_acc_id, payable_acc_opt, bk_user, bk_dt, bk_machine)\n"
                        + "select group_id, group_name, sort_order, mig_id, opd_acc_code, ipd_acc_code,\n"
                        + "  dep_code, srvf1_acc_id, srvf2_acc_id, srvf3_acc_id, srvf4_acc_id, srvf5_acc_id,\n"
                        + "  payable_acc_id, payable_acc_opt, '" + Global.loginUser.getUserId() + "',  SYSDATE(), \n"
                        + Global.machineId + " \n"
                        + "from ot_group where group_id = " + groupId;
                dao.execSql(strSql);
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "OT Group Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        return listOTG.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OTProcedureGroup> getListOTG() {
        return listOTG;
    }

    public void setListOTG(List<OTProcedureGroup> listOTG) {
        this.listOTG = listOTG;
        fireTableDataChanged();
    }

    public OTProcedureGroup getOTProcedureGroup(int row) {
        if (listOTG != null) {
            if (!listOTG.isEmpty()) {
                if (listOTG.size() > row) {
                    return listOTG.get(row);
                }
            }
        }

        return null;
    }

    public void getOTGroup() {
        listOTG = dao.findAllHSQL("select o from OTProcedureGroup o order by o.groupName");
        if (listOTG == null) {
            listOTG = new ArrayList();
        }
        addNewRow();
        fireTableDataChanged();
    }

    private void addNewRow() {
        int count = listOTG.size();

        if (count == 0 || listOTG.get(count - 1).getGroupId() != null) {
            listOTG.add(new OTProcedureGroup());
        }
    }

    public void delete(int row) {
        OTProcedureGroup record = listOTG.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getGroupId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from ot_group where group_id = " + record.getGroupId().toString();
                dao.deleteSQL(sql);
                listOTG.remove(row);
                fireTableRowsDeleted(row, row);
                addNewRow();
            } catch (Exception e) {
                dao.rollBack();
                log.error("delete : " + e);
            } finally {
                dao.close();
            }
        }
    }
}
