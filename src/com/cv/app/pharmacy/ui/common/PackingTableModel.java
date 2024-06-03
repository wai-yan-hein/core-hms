/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PackingItem;
import com.cv.app.pharmacy.database.entity.PackingItemKey;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PackingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PackingTableModel.class.getName());
    private List<PackingItem> listPI = new ArrayList();
    private String[] columnNames = {"Code", "Item Description", "Qty", "Unit"};
    private AbstractDataAccess dao;
    private MedicineUP medUp;
    private String selectedMed;
    private String deleteId = "";

    public PackingTableModel(AbstractDataAccess dao) {
        this.dao = dao;
        medUp = new MedicineUP(dao);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 1 || column == 3) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 2) {
            return Float.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PackingItem pi = listPI.get(row);

        switch (column) {
            case 0: //Code
                return pi.getKey().getItem().getMedId();
            case 1: //Item Description
                return pi.getKey().getItem().getMedName();
            case 2: //Qty
                return pi.getUnitQty();
            case 3: //Unit
                return pi.getUnit().getItemUnitCode();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        PackingItem pi = listPI.get(row);

        try {
            switch (column) {
                case 0: //Code
                    if (value == null) {
                        pi.setKey(new PackingItemKey());
                        pi.setQtyInSmallest(0d);
                        pi.setUnit(null);
                        pi.setUnitQty(0f);
                    } else if (value.toString().trim().isEmpty()) {
                        pi.setKey(new PackingItemKey());
                        pi.setQtyInSmallest(0d);
                        pi.setUnit(null);
                        pi.setUnitQty(0f);
                    } else {
                        Medicine med = getMedInfo(value.toString().trim());
                        pi.getKey().setItem(med);
                    }
                    fireTableCellUpdated(row, 1);
                    break;
                case 1: //Item Description
                    break;
                case 2: //Qty
                    float qty = NumberUtil.NZeroFloat(value);
                    pi.setUnitQty(qty);
                    String medId = pi.getKey().getItem().getMedId();

                    if (medId != null) {
                        if (medUp.getUnitList(medId) != null) {
                            if (medUp.getUnitList(medId).size() > 1) {
                                int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
                                int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
                                UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                        medUp.getUnitList(medId), Util1.getParent());

                                if (unitPopup.isSelected()) {
                                    pi.setUnit(unitPopup.getSelUnit());
                                }
                            } else {
                                pi.setUnit(medUp.getUnitList(medId).get(0));
                            }

                            if (pi.getUnit() != null) {
                                String key = medId + "-" + pi.getUnit().getItemUnitCode();
                                double smallQty = pi.getUnitQty() * medUp.getQtyInSmallest(key);
                                pi.setQtyInSmallest(smallQty);
                            }
                        }
                        addEmptyRow();
                    }
                    break;
                case 3: //Unit
                    /*if (value instanceof ItemUnit) {
                     ItemUnit iu = (ItemUnit) value;
                     pi.setUnit(iu);
                     String key = selectedMed + "-" + iu.getItemUnitCode();
                     double qtyInSmall = NumberUtil.NZeroFloat(pi.getUnitQty()) * medUp.getQtyInSmallest(key);
                     pi.setQtyInSmallest(qtyInSmall);

                     if (!hasEmptyRow()) {
                     addEmptyRow();
                     }
                     }*/
                    break;
            }

            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
    }

    @Override
    public int getRowCount() {
        return listPI.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<PackingItem> getListPI() {
        return listPI;
    }

    public void setListPI(List<PackingItem> listPI) {
        try {
            this.listPI = listPI;
            fireTableDataChanged();

            for (PackingItem pi : listPI) {
                medUp.add(pi.getKey().getItem());
            }
        } catch (Exception ex) {

        }
    }

    public Medicine getMedInfo(String medCode) {
        Medicine medicine;

        if (!medCode.trim().isEmpty()) {
            try {
                medicine = (Medicine) dao.find("Medicine", "medId = '"
                        + medCode + "' and active = true");

                if (medicine != null) {
                    medUp.add(medicine);
                    return medicine;
                } else { //For barcode
                    medicine = (Medicine) dao.find("Medicine", "barcode = '"
                            + medCode + "' and active = true");

                    if (medicine != null) {
                        medUp.add(medicine);
                        return medicine;
                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                                "Invalid.", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                log.error("getMedInfo : " + ex.getMessage());
            } finally {
                dao.close();
            }
        } else {
            log.info("getMedInfo : Blank medicine code.");
        }

        return null;
    }

    private boolean hasEmptyRow() {
        if (listPI.isEmpty()) {
            return false;
        }

        PackingItem record = listPI.get(listPI.size() - 1);

        if (record.getKey().getItem().getMedId() != null) {
            return false;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (!hasEmptyRow()) {
            PackingItem record = new PackingItem();

            record.getKey().setItem(new Medicine());
            listPI.add(record);

            fireTableRowsInserted(listPI.size() - 1, listPI.size() - 1);
            //parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount()-1, 0, true));
        }
    }

    public boolean isValidEntry() {
        boolean status = true;

        if (listPI.size() > 1) {
            for (int i = 0; i < listPI.size() - 1; i++) {
                PackingItem pi = listPI.get(i);

                if (pi.getKey().getItem() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid packing item.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                } else if (pi.getUnitQty() == 0) {
                    status = false;
                    JOptionPane.showMessageDialog(Util1.getParent(), "Packing item quantity cannot be zero.",
                            "Zero quantity.", JOptionPane.ERROR_MESSAGE);
                } else if (pi.getUnit() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid packing item unit.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                } else {
                    pi.getKey().setPackingItemCode(selectedMed);
                    pi.setUniqueId(i + 1);
                }
            }
        } else {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "At least one packing item needed.",
                    "Invalid.", JOptionPane.ERROR_MESSAGE);
        }

        return status;
    }

    public String getSelectedMed() {
        return selectedMed;
    }

    public void setSelectedMed(String selectedMed) {
        this.selectedMed = selectedMed;
    }

    public void delete(int row) {
        PackingItem pi = listPI.get(row);
        String itemId = pi.getKey().getItem().getMedId();

        if (itemId != null) {
            if (deleteId.isEmpty()) {
                deleteId = "'" + itemId + "'";
            } else {
                deleteId = ",'" + itemId + "'";
            }
        }

        listPI.remove(row);
        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);
    }

    public String getDeleteId() {
        return deleteId;
    }
}
