/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.MedUsageKey;
import com.cv.app.inpatient.database.entity.InpMedUsage;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class InpMedUsageTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(InpMedUsageTableModel.class.getName());
    private AbstractDataAccess dao;
    private List<InpMedUsage> listInpMedUsage = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Qty", "Unit"};
    private int srvId = -1;
    private MedicineUP medUp;
    private SelectionObserver observer;
    private boolean versionUpdate = false;

    public InpMedUsageTableModel(AbstractDataAccess dao, SelectionObserver observer) {
        this.dao = dao;
        this.observer = observer;
        medUp = new MedicineUP(dao);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0 || column == 2;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Qty
                return Integer.class;
            case 3: //Unit
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listInpMedUsage == null) {
            return null;
        }

        if (listInpMedUsage.isEmpty()) {
            return null;
        }

        try {
            InpMedUsage record = listInpMedUsage.get(row);

            switch (column) {
                case 0: //Code
                    if (record.getKey() != null) {
                        if (record.getKey().getMed() != null) {
                            return record.getKey().getMed().getMedId();
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                case 1: //Description
                    if (record.getKey() != null) {
                        if (record.getKey().getMed() != null) {
                            return record.getKey().getMed().getMedName();
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                case 2: //Qty
                    return record.getUnitQty();
                case 3: //Unit
                    if (record.getUnit() != null) {
                        return record.getUnit().getItemUnitCode();
                    } else {
                        return null;
                    }
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
        if (listInpMedUsage == null) {
            return;
        }

        if (listInpMedUsage.isEmpty()) {
            return;
        }

        try {
            InpMedUsage record = listInpMedUsage.get(row);

            switch (column) {
                case 0:
                    if (value != null) {
                        String medId = value.toString();

                        dao.open();
                        Medicine med = (Medicine) dao.find(Medicine.class, medId);

                        if (med.getRelationGroupId().size() > 0) {
                            medUp.add(med);
                        }

                        if (record.getKey() == null) {
                            MedUsageKey key = new MedUsageKey(srvId, med);
                            record.setKey(key);
                        } else {
                            record.getKey().setMed(med);
                        }

                        record.setUnit(null);
                        record.setQtySmall(null);
                        record.setUnitQty(null);
                    }
                    break;
                case 2:
                    if (value != null) {
                        if (record.getKey() != null) {
                            String medId = record.getKey().getMed().getMedId();
                            record.setUnitQty(NumberUtil.NZeroFloat(value));

                            medUp.loadRecord(medId, dao);
                            if (medUp.getUnitList(medId).size() > 1) {
                                int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
                                int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
                                UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                        medUp.getUnitList(medId), Util1.getParent());

                                if (unitPopup.isSelected()) {
                                    record.setUnit(unitPopup.getSelUnit());
                                    String key = medId + "-" + record.getUnit().getItemUnitCode();
                                    record.setQtySmall(record.getUnitQty() * medUp.getQtyInSmallest(key));
                                }
                            } else {

                                record.setUnit(medUp.getUnitList(medId).get(0));
                                String key = medId + "-" + record.getUnit().getItemUnitCode();
                                record.setQtySmall(record.getUnitQty() * medUp.getQtyInSmallest(key));
                            }
                        }
                    }
                    break;
            }

            saveRecord(record);
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    @Override
    public int getRowCount() {
        if (listInpMedUsage == null) {
            return 0;
        }
        return listInpMedUsage.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<InpMedUsage> getListInpMedUsage() {
        return listInpMedUsage;
    }

    public void setListInpMedUsage(List<InpMedUsage> listInpMedUsage) {
        this.listInpMedUsage = listInpMedUsage;
        fireTableDataChanged();
    }

    public InpMedUsage getInpMedUsage(int row) {
        if (listInpMedUsage != null) {
            if (!listInpMedUsage.isEmpty()) {
                return listInpMedUsage.get(row);
            }
        }
        return null;
    }

    public void delete(int row) {
        if (listInpMedUsage != null) {
            if (!listInpMedUsage.isEmpty()) {
                InpMedUsage record = listInpMedUsage.get(row);
                String sql;
                if (NumberUtil.NZeroL(record.getKey().getService()) > 0) {
                    try {
                        dao.open();
                        dao.beginTran();
                        sql = "delete from inp_med_usage where service_id = '" + record.getKey().getService() + "' and med_id='" + record.getKey().getMed().getMedId() + "'";
                        dao.deleteSQL(sql);
                    } catch (Exception e) {
                        dao.rollBack();
                    } finally {
                        dao.close();
                    }
                }

                listInpMedUsage.remove(row);
                fireTableRowsDeleted(row, row);
                addNewRow();
            }
        }
    }

    public void setSrvId(int srvId) {
        versionUpdate = false;
        this.srvId = srvId;
        getMedUsage();
    }

    private void getMedUsage() {
        try {
            listInpMedUsage = dao.findAll("InpMedUsage", "key.service = " + srvId);
            addNewRow();
        } catch (Exception ex) {
            log.error("getMedUsage : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void addNewRow() {
        if (listInpMedUsage != null) {
            int count = listInpMedUsage.size();

            if ((count == 0 || listInpMedUsage.get(count - 1).getKey() != null)
                    && srvId != -1) {
                listInpMedUsage.add(new InpMedUsage());
                fireTableRowsInserted(listInpMedUsage.size() - 1, listInpMedUsage.size() - 1);
            } else if (srvId == -1) {
                listInpMedUsage.removeAll(listInpMedUsage);
                fireTableDataChanged();
            }
        }
    }

    private void saveRecord(InpMedUsage record) {
        try {
            dao.save(record);
            addNewRow();
            if (!versionUpdate) {
                observer.selected("MedUsageChange", null);
                versionUpdate = true;
            }
        } catch (Exception ex) {
            dao.rollBack();
            log.error("saveRecord : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }
}
