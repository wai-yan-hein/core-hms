/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import com.cv.app.pharmacy.database.view.VReOrderLevel;
import com.cv.app.pharmacy.database.entity.ReOrderLevel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ReOrderLevelTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ReOrderLevelTableModel.class.getName());
    private List<VReOrderLevel> listReOrderLevel = new ArrayList();
    private final String[] columnNames = {"Item Code", "Item Name", "Packing", "Max Qty",
        "Max Unit", "Min Qty", "Min Unit", "Balance", "Bal > Max", "Bal < Min", "-",
        "Order Qty", "Order Unit"};
    private final MedicineUP medUp;
    private final AbstractDataAccess dao;
    private Integer locationId;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");

    public ReOrderLevelTableModel(AbstractDataAccess dao) {
        this.dao = dao;
        medUp = new MedicineUP(dao);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3 || column == 5 || column == 11;
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 3 || column == 5 || column == 11) {
            return Integer.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listReOrderLevel == null) {
            return null;
        }

        if (listReOrderLevel.isEmpty()) {
            return null;
        }

        try {
            VReOrderLevel record = listReOrderLevel.get(row);

            switch (column) {
                case 0: //Item Code
                    if (codeUsage.equals("SHORTNAME")) {
                        return record.getShortName();
                    } else {
                        return record.getKey().getMed();
                    }
                case 1: //Item Name
                    return record.getMedName();
                case 2: //Packing
                    return record.getRelStr();
                case 3: //Max Qty
                    return record.getMaxUnitQty();
                case 4: //Max Unit
                    if (record.getMaxItemUnit() != null) {
                        return record.getMaxItemUnit().getItemUnitCode();
                    } else {
                        return null;
                    }
                case 5: //Min Qty
                    return record.getMinUnitQty();
                case 6: //Min Unit
                    if (record.getMinItemUnit() != null) {
                        return record.getMinItemUnit().getItemUnitCode();
                    } else {
                        return null;
                    }
                case 7: //Balance
                    return record.getStrBalance();
                case 8: //Bal > Max
                    return record.getStrBalMax();
                case 9: //Bal < Min
                    return record.getStrBalMin();
                case 10: //Main Balance
                    return record.getStrBalMain();
                case 11: //Order Qty
                    return record.getOrderQty();
                case 12: //Order Unit
                    if (record.getOrderUnit() != null) {
                        return record.getOrderUnit().getItemUnitCode();
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listReOrderLevel == null) {
            return;
        }

        if (listReOrderLevel.isEmpty()) {
            return;
        }

        try {
            VReOrderLevel record = listReOrderLevel.get(row);
            int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
            int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
            UnitAutoCompleter unitPopup;

            switch (column) {
                case 3: //Max Qty
                    if (value != null) {
                        if (NumberUtil.NZeroInt(value) < 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Qty must be positive value.",
                                    "Minus qty.", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String medId = record.getKey().getMed();
                            Medicine med = (Medicine) dao.find(Medicine.class, medId);

                            medUp.add(med);
                            dao.close();

                            if (medUp.getUnitList(medId).size() > 1) {
                                unitPopup = new UnitAutoCompleter(x, y,
                                        medUp.getUnitList(medId), Util1.getParent());

                                if (unitPopup.isSelected()) {
                                    record.setMaxItemUnit(unitPopup.getSelUnit());
                                }
                            } else {
                                record.setMaxItemUnit(medUp.getUnitList(medId).get(0));
                            }

                            record.setMaxUnitQty(NumberUtil.NZeroFloat(value));
                            String key = medId + "-" + record.getMaxItemUnit().getItemUnitCode();
                            record.setMaxSmallest(record.getMaxUnitQty()
                                    * medUp.getQtyInSmallest(key));

                            if (NumberUtil.NZeroInt(record.getBalance()) != 0) {
                                if (NumberUtil.NZeroInt(record.getBalance())
                                        > NumberUtil.NZeroInt(record.getMaxSmallest())) {

                                    record.setBalMax(record.getBalance() - record.getMaxSmallest());
                                    record.setStrBalMax(MedicineUtil.getQtyInStr(
                                            medUp.getRelation(medId), record.getBalMax()));
                                }
                            }
                        }
                    } else {
                        record.setMaxItemUnit(null);
                        record.setMaxSmallest(null);
                        record.setMaxUnitQty(null);
                        record.setBalMax(null);
                        record.setStrBalMax(null);

                    }
                    break;
                case 5: //Min Qty
                    if (value != null) {
                        if (NumberUtil.NZeroInt(value) < 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Qty must be positive value.",
                                    "Minus qty.", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String medId = record.getKey().getMed();
                            Medicine med = (Medicine) dao.find(Medicine.class, medId);

                            medUp.add(med);
                            dao.close();
                            if (medUp.getUnitList(medId).size() > 1) {
                                unitPopup = new UnitAutoCompleter(x, y,
                                        medUp.getUnitList(medId), Util1.getParent());

                                if (unitPopup.isSelected()) {
                                    record.setMinItemUnit(unitPopup.getSelUnit());
                                }
                            } else {
                                record.setMinItemUnit(medUp.getUnitList(medId).get(0));
                            }

                            record.setMinUnitQty(NumberUtil.NZeroFloat(value));
                            String key = medId + "-" + record.getMinItemUnit().getItemUnitCode();
                            record.setMinSmallest(record.getMinUnitQty()
                                    * medUp.getQtyInSmallest(key));

                            if (NumberUtil.NZeroInt(record.getBalance()) != 0) {
                                if (NumberUtil.NZeroInt(record.getBalance())
                                        < NumberUtil.NZeroInt(record.getMinSmallest())) {
                                    record.setBalMin(record.getMinSmallest() - record.getBalance());
                                    record.setStrBalMin(MedicineUtil.getQtyInStr(
                                            medUp.getRelation(medId), record.getBalMin()));
                                }
                            }

                        }
                    } else {
                        record.setMinUnitQty(null);
                        record.setMinSmallest(null);
                        record.setMinItemUnit(null);
                        record.setBalMin(null);
                        record.setStrBalMin(null);
                    }
                    break;
                case 11: //Order qty
                    if (value != null) {
                        if (NumberUtil.NZeroInt(value) < 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Qty must be positive value.",
                                    "Minus qty.", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String medId = record.getKey().getMed();
                            Medicine med = (Medicine) dao.find(Medicine.class, medId);

                            medUp.add(med);
                            dao.close();
                            if (medUp.getUnitList(medId).size() > 1) {
                                unitPopup = new UnitAutoCompleter(x, y,
                                        medUp.getUnitList(medId), Util1.getParent());

                                if (unitPopup.isSelected()) {
                                    record.setOrderUnit(unitPopup.getSelUnit());
                                }
                            } else {
                                record.setOrderUnit(medUp.getUnitList(medId).get(0));
                            }

                            record.setOrderQty(NumberUtil.NZeroFloat(value));
                            String key = medId + "-" + record.getMinItemUnit().getItemUnitCode();
                            record.setOrderQtySmallest(record.getMinUnitQty()
                                    * medUp.getQtyInSmallest(key));
                        }
                    } else {
                        record.setOrderQty(null);
                        record.setOrderQtySmallest(null);
                        record.setOrderUnit(null);
                    }
                    break;
                default:
                    System.out.println("Invalid column.");
            }
            //dao.save(record);
            listReOrderLevel.set(row, updateLevel(record));
            fireTableCellUpdated(row, 8);
            fireTableCellUpdated(row, 9);
            fireTableCellUpdated(row, 10);
            fireTableCellUpdated(row, column);
            fireTableRowsUpdated(row, row);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listReOrderLevel == null) {
            return 0;
        }
        return listReOrderLevel.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VReOrderLevel> getListReOrderLevel() {
        return listReOrderLevel;
    }

    public void setListReOrderLevel(List<VReOrderLevel> listReOrderLevel) {
        this.listReOrderLevel = listReOrderLevel;
        fireTableDataChanged();
    }

    public VReOrderLevel getSelectedRec(int row) {
        if (listReOrderLevel == null) {
            return null;
        }

        if (row >= 0 && row < listReOrderLevel.size()) {
            return listReOrderLevel.get(row);
        } else {
            return null;
        }
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public VReOrderLevel updateLevel(VReOrderLevel record) {
        try {
            ReOrderLevel tmpRec = (ReOrderLevel) dao.find(ReOrderLevel.class, record.getKey());
            tmpRec.setMaxItemUnit(record.getMaxItemUnit());
            tmpRec.setMaxSmallest(record.getMaxSmallest());
            tmpRec.setMaxUnitQty(record.getMaxUnitQty());
            tmpRec.setBalMax(record.getBalMax());
            tmpRec.setStrBalMax(record.getStrBalMax());
            tmpRec.setMinUnitQty(record.getMinUnitQty());
            tmpRec.setMinSmallest(record.getMinSmallest());
            tmpRec.setMinItemUnit(record.getMinItemUnit());
            tmpRec.setBalMin(record.getBalMin());
            tmpRec.setStrBalMin(record.getStrBalMin());
            tmpRec.setOrderQty(record.getOrderQty());
            tmpRec.setOrderQtySmallest(record.getOrderQtySmallest());
            tmpRec.setOrderUnit(record.getOrderUnit());
            dao.save(tmpRec);
            
            String strSql = "update re_order_level rol left join (select user_id, location_id, med_id, sum(ifnull(bal_qty,0)) ttl_qty\n"
                    + "	from tmp_stock_balance_exp\n"
                    + "	where user_id = '" + Global.machineId + "' and location_id = " + locationId.toString() + "\n"
                    + "           and ifnull(bal_qty,0) <> 0 "
                    + "	group by user_id, location_id, med_id) tsbe on rol.item_id = tsbe.med_id and rol.location_id = tsbe.location_id join "
                    + "		   v_med_unit_smallest_rel vmusr on rol.item_id = vmusr.med_id "
                    + "	   set rol.balance = tsbe.ttl_qty, \n"
                    + "		   rol.balance_str = get_qty_in_str(ifnull(tsbe.ttl_qty,0), vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "		   rol.bal_max = max_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.max_smallest,0)),\n"
                    + "		   rol.bal_max_str = get_qty_in_str(max_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.max_smallest,0)),\n"
                    + "							vmusr.unit_smallest, vmusr.unit_str),\n"
                    + "		   rol.bal_min = min_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.min_smallest,0)),\n"
                    + "		   rol.bal_min_str = get_qty_in_str(min_qty(ifnull(tsbe.ttl_qty,0), ifnull(rol.min_smallest,0)),\n"
                    + "							vmusr.unit_smallest, vmusr.unit_str)\n"
                    + "	 where rol.item_id = '" + record.getKey().getMed() + "' "
                    + " and rol.location_id = " + locationId.toString();
            dao.execSql(strSql);
            record = (VReOrderLevel) dao.find(VReOrderLevel.class, record.getKey());
        } catch (Exception ex) {
            log.error("updateLevel : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }

        return record;
    }
}
