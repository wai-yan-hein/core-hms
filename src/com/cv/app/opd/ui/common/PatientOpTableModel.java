/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.CompoundKeyPatientOp;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientOpening;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PatientOpTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PatientOpTableModel.class.getName());
    private final String[] columnNames = {"Date", "Currency", "Bill Type", "PT Type", "Amount"};
    private List<PatientOpening> listOp = new ArrayList();
    //private List<PatientOpening> listDeleteOp = new ArrayList();
    private Patient patient = new Patient();
    private boolean editable;
    private final AbstractDataAccess dao = Global.dao;

    public PatientOpTableModel() {
        editable = false;
    }

    public PatientOpTableModel(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return editable;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 1: //Currency
                return Currency.class;
            case 2: //Bill Type
                return PaymentType.class;
            case 3: //PT Type
                return String.class;
            case 4: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PatientOpening record = listOp.get(row);

        switch (column) {
            case 0: //Date
                if (record.getKey() != null) {
                    return DateUtil.toDateStr(record.getKey().getOpDate());
                } else {
                    return null;
                }
            case 1: //Currency
                if (record.getKey() != null) {
                    return record.getKey().getCurrency();
                } else {
                    return null;
                }
            case 2: //Bill Type
                if (record.getKey() != null) {
                    return record.getKey().getBillType();
                } else {
                    return null;
                }
            case 3: //PT Type
                if(record.getKey() != null){
                    return record.getKey().getPatientType();
                }else{
                    return null;
                }
            case 4: //Amount
                return record.getAmount();
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        PatientOpening record = listOp.get(row);

        switch (column) {
            case 0: //Date
                if (value != null) {
                    Date tmpDate = DateUtil.toDate(value.toString());

                    if (tmpDate == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening date.",
                                "Opening date", JOptionPane.ERROR_MESSAGE);
                    } else {
                        record.getKey().setOpDate(tmpDate);
                    }
                }

                if (!hasEmptyRow()) {
                    addEmptyRow();
                }
                break;
            case 1: //Currency
                record.getKey().setCurrency((Currency) value);
                break;
            case 2: //Bill Type
                if (value != null) {
                    record.getKey().setBillType((PaymentType) value);
                } else {
                    record.getKey().setBillType(null);
                }
                break;
            case 3: //Amount
                record.setAmount((Double) value);
            default:
                System.out.println("invalid index");
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listOp.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listOp.isEmpty()) {
            return false;
        }

        PatientOpening record = listOp.get(listOp.size() - 1);

        if (record.getKey().getOpDate() != null) {
            return false;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (editable) {
            PatientOpening record = new PatientOpening(new CompoundKeyPatientOp(patient));
            listOp.add(record);
            fireTableRowsInserted(listOp.size() - 1, listOp.size() - 1);
        }
    }

    public void delete(int row) {
        PatientOpening op = listOp.get(row);

        if (op.getKey().getCurrency() != null) {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                    "Are you sure to delete?",
                    "Patient Opening delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                if (!editable) {
                    try {
                        String strSql = "delete from patient_op where op_date = '"
                                + DateUtil.toDateStrMYSQL(DateUtil.toDateStr(op.getKey().getOpDate()))
                                + "' and reg_no = '" + op.getKey().getPatient().getRegNo()
                                + "' and currency = '" + op.getKey().getCurrency().getCurrencyCode()
                                + "' and bill_type = " + op.getKey().getBillType().getPaymentTypeId().toString()
                                + " and patient_type = '" + op.getKey().getPatientType() + "'";
                        dao.execSql(strSql);

                        listOp.remove(row);
                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }

                        fireTableRowsDeleted(row, row);
                    } catch (Exception ex) {
                        log.error("delete : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            }
        }
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        listOp.removeAll(listOp);
        fireTableDataChanged();

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
    }

    public List<PatientOpening> getListOp() {
        return listOp;
    }

    public void setListOp(List<PatientOpening> listOp) {
        this.listOp = listOp;
        fireTableDataChanged();
    }

    public void clear() {
        listOp.removeAll(listOp);
        fireTableDataChanged();
    }

    public boolean isValidEntry() {
        boolean status = true;

        for (int i = 0; i < listOp.size() - 1; i++) {
            PatientOpening patientOP = listOp.get(i);

            if (patientOP.getKey().getOpDate() == null) {
                status = false;
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid opening date.",
                        "Date null.", JOptionPane.ERROR_MESSAGE);
                i = listOp.size();
            } else if (patientOP.getKey().getCurrency() == null) {
                status = false;
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid currency.",
                        "Currency null", JOptionPane.ERROR_MESSAGE);
                i = listOp.size();
            } else if (patientOP.getAmount() == null) {
                status = false;
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid amount.",
                        "Amount null", JOptionPane.ERROR_MESSAGE);
                i = listOp.size();
            }
        }

        return status;
    }
}
