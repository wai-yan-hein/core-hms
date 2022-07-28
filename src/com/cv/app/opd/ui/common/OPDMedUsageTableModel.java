/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.MedUsageKey;
import com.cv.app.opd.database.entity.OPDMedUsage;
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
public class OPDMedUsageTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDMedUsageTableModel.class.getName());
    private AbstractDataAccess dao;
    private List<OPDMedUsage> listOPDMedUsage = new ArrayList();
    private String[] columnNames = {"Code", "Description", "Qty", "Unit"};
    private int srvId = -1;
    private MedicineUP medUp;
    private SelectionObserver observer;
    private boolean versionUpdate = false;
    private String deletedList;

    public OPDMedUsageTableModel(AbstractDataAccess dao, SelectionObserver observer) {
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
        if (column == 0 || column == 2) {
            return true;
        } else {
            return false;
        }
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
        OPDMedUsage record = listOPDMedUsage.get(row);

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
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDMedUsage record = listOPDMedUsage.get(row);

        try {
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
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        saveRecord(record);
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listOPDMedUsage.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDMedUsage> getListOPDMedUsage() {
        return listOPDMedUsage;
    }

    public void setListOPDMedUsage(List<OPDMedUsage> listOPDMedUsage) {
        this.listOPDMedUsage = listOPDMedUsage;
        fireTableDataChanged();
    }

    public OPDMedUsage getOPDMedUsage(int row) {
        return listOPDMedUsage.get(row);
    }

    public void deleteCity(int row) {
        listOPDMedUsage.remove(row);
        fireTableRowsDeleted(0, listOPDMedUsage.size() - 1);
    }

    public void setSrvId(int srvId) {
        versionUpdate = false;
        this.srvId = srvId;
        getMedUsage();
    }

    private void getMedUsage() {
        try {
            listOPDMedUsage = dao.findAll("OPDMedUsage", "key.service = " + srvId);
            addNewRow();
        } catch (Exception ex) {
            log.error("getMedUsage : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void addNewRow() {
        int count = listOPDMedUsage.size();

        if ((count == 0 || listOPDMedUsage.get(count - 1).getKey() != null)
                && srvId != -1) {
            listOPDMedUsage.add(new OPDMedUsage());
            fireTableRowsInserted(listOPDMedUsage.size() - 1, listOPDMedUsage.size() - 1);
        } else if (srvId == -1) {
            listOPDMedUsage.removeAll(listOPDMedUsage);
            fireTableDataChanged();
        }
    }

    private void saveRecord(OPDMedUsage record) {
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

    public void delete(int row) {
        OPDMedUsage record = listOPDMedUsage.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getKey().getService()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from opd_med_usage where service_id = '" + record.getKey().getService() + "' and med_id='" + record.getKey().getMed().getMedId() + "'";
                dao.deleteSQL(sql);
            } catch (Exception e) {
                dao.rollBack();
            } finally {
                dao.close();
            }
        }

        listOPDMedUsage.remove(row);

        fireTableRowsDeleted(row, row);

        addNewRow();
    }

    /* public boolean hasEmptyRow() {
        if (listOPDMedUsage.isEmpty()) {
            return false;
        }

        OPDMedUsage record = listOPDMedUsage.get(listOPDMedUsage.size() - 1);

        if (record.getKey().getService() != null) {
            return false;
        } else {
            return true;
        }
    }*/
}
