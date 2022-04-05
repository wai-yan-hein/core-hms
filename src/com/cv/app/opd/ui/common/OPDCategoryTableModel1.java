/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.common.Global;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OPDCategoryTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDCategoryTableModel1.class.getName());

    private List<OPDCategory> listOPDCategory = new ArrayList();
    private final String[] columnNames = {"Description", "OPD Acc Code", "IPD Acc Code",
        "Dep Code", "Mo Fee Acc", "Staff Fee", "Tech Fee", "Refer Fee", "Read Fee",
        "Payable Acc Code", "Payable Acc Opt", "MO Ref Dr", "Staff Ref Dr", "IPD Dept Code"};
    private final AbstractDataAccess dao;
    private int groupId;

    public OPDCategoryTableModel1(AbstractDataAccess dao) {
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        OPDCategory record = listOPDCategory.get(row);

        switch (column) {
            case 0: //Description
                return record.getCatName();
            case 1: //Account Id
                return record.getOpdAccCode();
            case 2:
                return record.getIpdAccCode();
            case 3:
                return record.getDepCode();
            case 4:
                return record.getSrvF1AccId();
            case 5:
                return record.getSrvF2AccId();
            case 6:
                return record.getSrvF3AccId();
            case 7:
                return record.getSrvF4AccId();
            case 8:
                return record.getSrvF5AccId();
            case 9:
                return record.getPayableAccId();
            case 10:
                return record.getPayableAccOpt();
            case 11:
                return record.getSrvF2RefDr();
            case 12:
                return record.getSrvF3RefDr();
            case 13:
                return record.getIpdDeptCode();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDCategory record = listOPDCategory.get(row);

        switch (column) {
            case 0: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setCatName(value.toString());
                    record.setGroupId(groupId);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OPD Category",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1: //Opd Code
                if (value != null) {
                    record.setOpdAccCode(value.toString());
                } else {
                    record.setOpdAccCode(null);
                }
                break;
            case 2: //Ipd Code
                if (value != null) {
                    record.setIpdAccCode(value.toString());
                } else {
                    record.setIpdAccCode(null);
                }
                break;
            case 3: //Dep Code
                if (value != null) {
                    record.setDepCode(value.toString());
                } else {
                    record.setDepCode(null);
                }
                break;
            case 4:
                if (value != null) {
                    record.setSrvF1AccId(value.toString());
                } else {
                    record.setSrvF1AccId(null);
                }
                break;
            case 5:
                if (value != null) {
                    record.setSrvF2AccId(value.toString());
                } else {
                    record.setSrvF2AccId(null);
                }
                break;
            case 6:
                if (value != null) {
                    record.setSrvF3AccId(value.toString());
                } else {
                    record.setSrvF3AccId(null);
                }
                break;
            case 7:
                if (value != null) {
                    record.setSrvF4AccId(value.toString());
                } else {
                    record.setSrvF4AccId(null);
                }
                break;
            case 8:
                if (value != null) {
                    record.setSrvF5AccId(value.toString());
                } else {
                    record.setSrvF5AccId(null);
                }
                break;
            case 9:
                if (value != null) {
                    record.setPayableAccId(value.toString());
                } else {
                    record.setPayableAccId(null);
                }
                break;
            case 10:
                if (value != null) {
                    record.setPayableAccOpt(value.toString());
                } else {
                    record.setPayableAccOpt(null);
                }
                break;
            case 11:
                if (value != null) {
                    record.setSrvF2RefDr(value.toString());
                } else {
                    record.setSrvF2RefDr(null);
                }
                break;
            case 12:
                if (value != null) {
                    record.setSrvF3RefDr(value.toString());
                } else {
                    record.setSrvF3RefDr(null);
                }
                break;
            case 13:
                if (value != null) {
                    record.setIpdDeptCode(value.toString());
                } else {
                    record.setIpdDeptCode(null);
                }
                break;
        }

        save(record);

        try {
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

        }
    }

    private void save(OPDCategory record) {
        if (!Util1.getString(record.getCatName(), "-").equals("-")) {
            try {
                int catId = NumberUtil.NZeroInt(record.getCatId());
                String strSql = "insert into bk_opd_category(cat_id, cat_name, group_id, mig_id, opd_acc_code,\n"
                        + "  ipd_acc_code, dep_code, srvf1_acc_id, srvf2_acc_id, srvf4_acc_id, srvf3_acc_id,\n"
                        + "  srvf5_acc_id, payable_acc_id, payable_acc_opt, crvf2_ref_dr, crvf3_ref_dr,\n"
                        + "  ipd_dept_code, bk_user, bk_dt, bk_machine)\n"
                        + "select cat_id, cat_name, group_id, mig_id, opd_acc_code,\n"
                        + "  ipd_acc_code, dep_code, srvf1_acc_id, srvf2_acc_id, srvf4_acc_id, srvf3_acc_id,\n"
                        + "  srvf5_acc_id, payable_acc_id, payable_acc_opt, crvf2_ref_dr, crvf3_ref_dr,\n"
                        + "  ipd_dept_code, '" + Global.loginUser.getUserId() + "',  SYSDATE(), \n"
                        + "  " + Global.machineId + " \n"
                        + "from opd_category where cat_id = " + catId;
                dao.execSql(strSql);
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "OPD Category Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        return listOPDCategory.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDCategory> getListOPDCategory() {
        return listOPDCategory;
    }

    public void setListOPDCategory(List<OPDCategory> listOPDCategory) {
        this.listOPDCategory = listOPDCategory;
        fireTableDataChanged();
    }

    public OPDCategory getOPDCategory(int row) {
        return listOPDCategory.get(row);
    }

    public void setOPDCategory(int row, OPDCategory oPDCategory) {
        listOPDCategory.set(row, oPDCategory);
        fireTableRowsUpdated(row, row);
    }

    public void addOPDCategory(OPDCategory oPDCategory) {
        listOPDCategory.add(oPDCategory);
        fireTableRowsInserted(listOPDCategory.size() - 1, listOPDCategory.size() - 1);
    }

    public void deleteOPDCategory(int row) {
        listOPDCategory.remove(row);
        fireTableRowsDeleted(0, listOPDCategory.size() - 1);
    }

    public void setGroupId(int id) {
        groupId = id;
        getCategory();
    }

    private void getCategory() {
        //listOPDCategory = dao.findAll("OPDCategory", "groupId = " + groupId);
        listOPDCategory = dao.findAllHSQL(
                "select o from OPDCategory o where o.groupId = " + groupId + " order by o.catName");
        addNewRow();
        fireTableDataChanged();
    }

    private void addNewRow() {
        int count = listOPDCategory.size();

        if (count == 0 || listOPDCategory.get(count - 1).getCatId() != null) {
            listOPDCategory.add(new OPDCategory());
        }
    }

    public void delete(int row) {
        OPDCategory record = listOPDCategory.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getCatId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from opd_category where cat_id = '" + record.getCatId() + "'";
                dao.deleteSQL(sql);
            } catch (Exception e) {
                dao.rollBack();
            } finally {
                dao.close();
            }
        }

        listOPDCategory.remove(row);

        fireTableRowsDeleted(row, row);

        addNewRow();
    }
}
