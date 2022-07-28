/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.InpCategory;
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
public class InpCategoryTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(InpCategoryTableModel1.class.getName());
    private List<InpCategory> listInpCategory = new ArrayList();
    private final String[] columnNames = {"Description", "Accound Code", "Dept Code",
        "Nurse", "Tech", "MO", "Payable Acc Code",
        "Payable Acc Opt"
    };
    private final AbstractDataAccess dao;

    public InpCategoryTableModel1(AbstractDataAccess dao) {
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
        if (listInpCategory == null) {
            return null;
        }

        if (listInpCategory.isEmpty()) {
            return null;
        }

        try {
            InpCategory record = listInpCategory.get(row);

            switch (column) {
                case 0: //Description
                    return record.getCatName();
                case 1: //Account Id
                    return record.getAccountId();
                case 2: //Dept Code
                    return record.getDeptId();
                case 3:
                    return record.getSrvF2AccId();
                case 4:
                    return record.getSrvF3AccId();
                case 5:
                    return record.getSrvF4AccId();
                case 6:
                    return record.getPayableAccId();
                case 7:
                    return record.getPayableAccOpt();
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
        if (listInpCategory == null) {
            return;
        }

        if (listInpCategory.isEmpty()) {
            return;
        }

        try {
            InpCategory record = listInpCategory.get(row);

            switch (column) {
                case 0: //Description
                    if (!Util1.getString(value, "-").equals("-")) {
                        record.setCatName(value.toString());

                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid description.", "Inpatient Category",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 1: //Account Id
                    if (value != null) {
                        record.setAccountId(value.toString());
                    } else {
                        record.setAccountId(null);
                    }
                    break;
                case 2: //Dept Code
                    if (value != null) {
                        record.setDeptId(value.toString());
                    } else {
                        record.setDeptId(null);
                    }
                    break;
                case 3:
                    if (value != null) {
                        record.setSrvF2AccId(value.toString());
                    } else {
                        record.setSrvF2AccId(null);
                    }
                    break;
                case 4:
                    if (value != null) {
                        record.setSrvF3AccId(value.toString());
                    } else {
                        record.setSrvF3AccId(null);
                    }
                    break;
                case 5:
                    if (value != null) {
                        record.setSrvF4AccId(value.toString());
                    } else {
                        record.setSrvF4AccId(null);
                    }
                    break;
                case 6:
                    if (value != null) {
                        record.setPayableAccId(value.toString());
                    } else {
                        record.setPayableAccId(null);
                    }
                    break;
                case 7:
                    if (value != null) {
                        record.setPayableAccOpt(value.toString());
                    } else {
                        record.setPayableAccOpt(null);
                    }
                    break;
            }

            save(record);

            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private void save(InpCategory record) {
        if (!Util1.getString(record.getCatName(), "-").equals("-")) {
            try {
                int catId = NumberUtil.NZeroInt(record.getCatId());
                String strSql = "insert into bk_inp_category(cat_id, cat_name, sort_order, mig_id, account_id, dep_code,\n"
                        + "  srvf1_acc_id, srvf2_acc_id, srvf3_acc_id, srvf4_acc_id, srvf5_acc_id, payable_acc_id,\n"
                        + "  payable_acc_opt, bk_user, bk_dt, bk_machine)\n"
                        + "select cat_id, cat_name, sort_order, mig_id, account_id, dep_code,\n"
                        + "  srvf1_acc_id, srvf2_acc_id, srvf3_acc_id, srvf4_acc_id, srvf5_acc_id, payable_acc_id,\n"
                        + "  payable_acc_opt, '" + Global.loginUser.getUserId() + "',  SYSDATE(), "
                        + Global.machineId + " \n"
                        + "from inp_category where cat_id = " + catId;
                dao.execSql(strSql);
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "Inpatient Category Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        if (listInpCategory == null) {
            return 0;
        } else {
            return listInpCategory.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<InpCategory> getListInpCategory() {
        return listInpCategory;
    }

    public void setListInpCategory(List<InpCategory> listInpCategory) {
        this.listInpCategory = listInpCategory;
        fireTableDataChanged();
    }

    public InpCategory getInpCategory(int row) {
        if (listInpCategory != null) {
            if (!listInpCategory.isEmpty()) {
                return listInpCategory.get(row);
            }
        }

        return null;
    }

    public void setInpCategory(int row, InpCategory oPDCategory) {
        if (listInpCategory != null) {
            if (!listInpCategory.isEmpty()) {
                listInpCategory.set(row, oPDCategory);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addInpCategory(InpCategory oPDCategory) {
        if (listInpCategory != null) {
            listInpCategory.add(oPDCategory);
            fireTableRowsInserted(listInpCategory.size() - 1, listInpCategory.size() - 1);
        }
    }

    public void deleteInpCategory(int row) {
        if (listInpCategory != null) {
            listInpCategory.remove(row);
            fireTableRowsDeleted(0, listInpCategory.size() - 1);
        }
    }

    public void getCategory() {
        try {
            listInpCategory = dao.findAllHSQL("select o from InpCategory o order by o.catName");
            if (listInpCategory == null) {
                listInpCategory = new ArrayList();
            }

            addNewRow();
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("getCategory : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addNewRow() {
        if (listInpCategory != null) {
            int count = listInpCategory.size();

            if (count == 0 || listInpCategory.get(count - 1).getCatId() != null) {
                listInpCategory.add(new InpCategory());
            }
        }
    }

    public void delete(int row) {
        if (listInpCategory != null) {
            if (listInpCategory.isEmpty()) {
                return;
            }

            try {
                InpCategory record = listInpCategory.get(row);
                String sql;
                if (NumberUtil.NZeroL(record.getCatId()) > 0) {
                    dao.open();
                    dao.beginTran();
                    sql = "delete from inp_category where cat_id = '" + record.getCatId() + "'";
                    dao.deleteSQL(sql);
                }

                listInpCategory.remove(row);
                fireTableRowsDeleted(row, row);
                addNewRow();
            } catch (Exception e) {
                log.error("delete : " + e.toString());
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }
}
