/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.StockReceiveDetailHis;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.StockOutstanding;
import com.cv.app.pharmacy.ui.util.UnitAutoCompleter;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class ReceiveTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ReceiveTableModel.class.getName());
    private List<StockReceiveDetailHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Option", "Vou No", "Order Item", "Item Code", "Rcv-Item",
        "Trader", "Outstanding", "Exp-Date", "Qty", "Unit", "Balance", "Currency", "Cost Price"};
    private final AbstractDataAccess dao;
    private MedicineUP medUp;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    private int maxUniqueId = 0;
    private String deletedList;
    private String receiveId = "-";
    private JTable parent;
    private Currency currency;

    public ReceiveTableModel(AbstractDataAccess dao) {
        this.dao = dao;
        medUp = new MedicineUP(dao);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return !(column == 1 || column == 2 || column == 4 || column == 6);
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Option
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Order Medicine
                return String.class;
            case 3: //Med Code
                return String.class;
            case 4: //Rec-Medicine
                return String.class;
            case 5: //Trader
                return String.class;
            case 6: //Outstanding
                return String.class;
            case 7: //Exp-Date
                return String.class;
            case 8: //Qty
                return Float.class;
            case 9: //Unit
                return String.class;
            case 10: //Balance
                return String.class;
            case 11: //Currency
                return String.class;
            case 12: //Cost Price
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }

        try {
            StockReceiveDetailHis srdh = listDetail.get(row);

            switch (column) {
                case 0: //Option
                    return srdh.getRecOption();
                case 1: //Vou No
                    return srdh.getRefVou();
                case 2: //Order Medicine
                    if (srdh.getOrderMed() != null) {
                        return srdh.getOrderMed().getMedName();
                    } else {
                        return null;
                    }
                case 3: //Med Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (srdh.getRecMed() != null) {
                            return srdh.getRecMed().getShortName();
                        } else {
                            return null;
                        }
                    } else if (srdh.getRecMed() != null) {
                        return srdh.getRecMed().getMedId();
                    } else {
                        return null;
                    }
                case 4: //Rec-Medicine
                    if (srdh.getRecMed() != null) {
                        return srdh.getRecMed().getMedName();
                    } else {
                        return null;
                    }
                case 5: //Trader
                    Trader trader = srdh.getTraderId();
                    if (trader != null) {
                        return trader.getTraderName() + " (" + trader.getTraderId() + ")";
                    } else {
                        return null;
                    }
                case 6: //Outstanding
                    if (srdh.getRecMed() != null) {
                        return srdh.getStrOutstanding();
                    } else {
                        return null;
                    }
                case 7: //Exp-Date
                    return DateUtil.toDateStr(srdh.getExpDate());
                case 8: //Qty
                    return srdh.getUnitQty();
                case 9: //Unit
                    if (srdh.getUnit() != null) {
                        return srdh.getUnit().getItemUnitName();
                    } else {
                        return null;
                    }
                case 10: //Balance
                    return srdh.getBalance();
                case 11: //Currency
                    if (srdh.getCurrency() == null) {
                        return null;
                    } else {
                        return srdh.getCurrency();
                    }
                case 12://Cost Price
                    return srdh.getCostPrice();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + column + " - " + ex.getStackTrace()[0].getLineNumber()
                    + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listDetail == null) {
            return;
        }

        if (listDetail.isEmpty()) {
            return;
        }

        try {
            StockReceiveDetailHis srdh = listDetail.get(row);
            switch (column) {
                case 0: //Option
                    //srdh.setRecOption((String)value);
                    break;
                case 1: //Vou No
                    //srdh.setRefVou((String)value);
                    break;
                case 2: //Order Medicine
                    //srdh.getOrderMed().setMedId((String)value);
                    break;
                case 3: //Med Code
                    String medCode = (String) value;

                    if (!medCode.isEmpty()) {
                        getMedInfo(medCode, srdh);
                        if (srdh.getRecOption() == null) {
                            srdh.setRecOption("RECEIVE");
                            srdh.setRefVou(receiveId);
                            srdh.setVouNo(receiveId);
                            srdh.setCurrency(currency);
                            parent.setRowSelectionInterval(row, row);
                            parent.setColumnSelectionInterval(7, 7);
                        }

                        fireTableCellUpdated(0, 0);
                        fireTableCellUpdated(1, 1);
                        fireTableCellUpdated(2, 2);
                        fireTableCellUpdated(3, 3);
                        addEmptyRow();
                    }
                    break;
                case 4: //Rec-Medicine
                    break;
                case 5: //Trader
                    if (value == null) {
                        srdh.setTraderId(null);
                    } else {
                        Trader trader = (Trader) value;
                        srdh.setTraderId(trader);
                    }
                    break;
                case 6: //Outstanding
                    break;
                case 7: //Exp-Date
                    if (DateUtil.isValidDate(value)) {
                        srdh.setExpDate(DateUtil.toDate(value));
                    }
                    break;
                case 8: //Qty
                    srdh.setUnitQty((Float) value);

                    String medId = srdh.getRecMed().getMedId();
                    if (medUp.getUnitList(medId).size() > 1) {
                        int x = Util1.getParent().getX() + (Util1.getParent().getWidth() / 2) + 50;
                        int y = Util1.getParent().getY() + (Util1.getParent().getHeight() / 2) - 200;
                        UnitAutoCompleter unitPopup = new UnitAutoCompleter(x, y,
                                medUp.getUnitList(medId), Util1.getParent());

                        if (unitPopup.isSelected()) {
                            srdh.setUnit(unitPopup.getSelUnit());
                            String key = srdh.getRecMed().getMedId() + "-"
                                    + srdh.getUnit().getItemUnitCode();
                            srdh.setSmallestQty(srdh.getUnitQty() * medUp.getQtyInSmallest(key));
                            float tmpBalance = NumberUtil.FloatZero(srdh.getOutsBalance())
                                    - (NumberUtil.FloatZero(srdh.getUnitQty()) * medUp.getQtyInSmallest(key));
                            srdh.setBalance(MedicineUtil.getQtyInStr(srdh.getRecMed(), tmpBalance));
                            //parent.setColumnSelectionInterval(6, 6);
                        }
                    } else {
                        srdh.setUnit(medUp.getUnitList(medId).get(0));
                        String key = srdh.getRecMed().getMedId() + "-"
                                + srdh.getUnit().getItemUnitCode();
                        srdh.setSmallestQty(srdh.getUnitQty() * medUp.getQtyInSmallest(key));
                    }

                    parent.setRowSelectionInterval(row, row);
                    parent.setColumnSelectionInterval(10, 10);
                    break;
                case 9: //Unit
                    break;
                case 10: //Balance
                    break;
                case 11: //Currency
                    if (value == null) {
                        srdh.setCurrency(null);
                    } else {
                        srdh.setCurrency((Currency) value);
                    }
                    break;
                case 12: //Cost Price
                    srdh.setCostPrice(NumberUtil.NZero(value));
                    break;
                default:
                    System.out.println("ReceiveTableModel invalid index.");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        if (column != 0) {
            fireTableCellUpdated(row, column);
        }
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<StockReceiveDetailHis> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.StockReceiveDetailHis"
                + " WHERE recOption IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listDetail);
    }

    public void setListDetail(List<StockReceiveDetailHis> listDetail) {
        try {
            this.listDetail = listDetail;
            if (this.listDetail != null) {
                if (listDetail != null) {
                    if (!listDetail.isEmpty()) {
                        StockReceiveDetailHis tmpD = listDetail.get(listDetail.size() - 1);
                        maxUniqueId = tmpD.getUniqueId();
                    }
                }
                for (StockReceiveDetailHis srdh : this.listDetail) {
                    medUp.add(srdh.getRecMed());
                }
                fireTableDataChanged();
            }
        } catch (Exception ex) {
            log.error("setListDetail : " + ex.getMessage());
        }
    }

    public void add(StockOutstanding outs) {
        if (listDetail == null) {
            return;
        }

        try {
            StockReceiveDetailHis srdh = new StockReceiveDetailHis();

            srdh.setBalance(outs.getQtyStr());
            srdh.setOutsBalance(outs.getBalanceQty());
            srdh.setRecMed(outs.getMed());
            srdh.setRecOption(outs.getTranOption());
            srdh.setRefVou(outs.getInvId());
            Trader trader = (Trader) dao.find(Trader.class, outs.getCusId());
            srdh.setTraderId(trader);
            srdh.setOrderMed(outs.getMed());
            srdh.setStrOutstanding(outs.getQtyStr());

            int index = listDetail.size() - 1;
            StockReceiveDetailHis tmpSrdh = listDetail.get(index);
            if (tmpSrdh.getRecOption() == null) {
                listDetail.set(index, srdh);
            } else {
                listDetail.add(srdh);
            }

            addEmptyRow();
            dao.open();
            medUp.add(outs.getMed());
            dao.close();
        } catch (Exception ex) {
            log.error("add : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    public void clear() {
        maxUniqueId = 0;
        if (listDetail != null) {
            listDetail.removeAll(listDetail);
        }
        medUp = new MedicineUP(dao);

        fireTableDataChanged();
    }

    public boolean isValidEntry() {
        boolean status = true;
        int row = maxUniqueId;

        if (listDetail != null) {
            for (StockReceiveDetailHis his : listDetail) {
                if (his.getRecOption() != null) {
                    if (NumberUtil.NZeroFloat(his.getUnitQty()) <= 0) {
                        status = false;
                        JOptionPane.showMessageDialog(Util1.getParent(), "Qty must be positive value.",
                                "Minus or zero qty.", JOptionPane.ERROR_MESSAGE);
                    } else if (NumberUtil.NZeroInt(his.getUniqueId()) == 0) {
                        his.setUniqueId(row + 1);
                        row++;
                    }
                }
            }
        }

        return status;
    }

    public void delete(int row) {
        if (listDetail != null) {
            StockReceiveDetailHis record = listDetail.get(row);
            if (record.getTranId() != null) {
                if (deletedList == null) {
                    deletedList = "'" + record.getTranId() + "'";
                } else {
                    deletedList = deletedList + ",'" + record.getTranId() + "'";
                }
            }
            listDetail.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from stock_receive_detail_his where rec_detail_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public boolean hasEmptyRow() {
        if (listDetail == null) {
            return false;
        }
        if (listDetail.isEmpty()) {
            return false;
        }

        StockReceiveDetailHis record = listDetail.get(listDetail.size() - 1);
        return record.getRecOption() == null;
    }

    public void addEmptyRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                StockReceiveDetailHis record = new StockReceiveDetailHis();
                record.setRecMed(new Medicine());
                listDetail.add(record);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    private void getMedInfo(String medCode, StockReceiveDetailHis srdh) {
        try {
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                List<RelationGroup> listRel = dao.findAllHSQL("select o from RelationGroup o where o.medId = '"
                        + medicine.getMedId() + "' order by o.relUniqueId");
                medicine.setRelationGroupId(listRel);
                setMed(medicine, srdh);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                        "Invalid.", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.toString());
        }
    }

    public void setMed(Medicine med, StockReceiveDetailHis record) {
        if (listDetail == null) {
            return;
        }

        try {
            dao.open();
            medUp.add(med);
            record.setRecMed(med);
        } catch (Exception ex) {
            log.error("setMed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void addBorrow(StockReceiveDetailHis srdh) {
        if (listDetail != null) {
            int index = listDetail.size() - 1;
            listDetail.add(index, srdh);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            addEmptyRow();
        }
    }
}
