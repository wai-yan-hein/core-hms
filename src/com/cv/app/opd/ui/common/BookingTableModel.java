/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Booking;
import com.cv.app.opd.database.view.VBooking;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import com.cv.app.opd.database.entity.Patient;
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
public class BookingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(BookingTableModel.class.getName());

    private List<VBooking> listBooking = new ArrayList();
    private List<Booking> listBk = new ArrayList();
    private Booking updateBK = new Booking();
    private final String[] columnNames = {"Date", "Visit No", "Reg No", "Name",
        "Doctor Name", "Phone", "Serial No", "W/L"};
    private final AbstractDataAccess dao = Global.dao;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2 || column == 7;
    }

    @Override
    public Class getColumnClass(int column) {

        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Visit No.
                return String.class;
            case 2: // Reg Id
                return String.class;
            case 3: // Patient Name
                return String.class;
            case 4: // Doctor Name
                return String.class;
            case 5:// Phone No
                return String.class;
            case 6:
                return Integer.class;
            case 7:
                return Boolean.class;
            default:
                return Object.class;

        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listBooking == null) {
            return null;
        }

        if (listBooking.isEmpty()) {
            return null;
        }

        VBooking booking = listBooking.get(row);
        switch (column) {
            case 0:
                return booking.getBkDate();
            case 1: //Visit No.
                return booking.getDoctorId() + "-" + booking.getRegNo() + "-"
                        + DateUtil.getDatePart(booking.getBkDate(), "ddMMyyyy")
                        + "-" + booking.getBkSerialNo();
            case 2: //ID
                return booking.getRegNo();
            case 3: //Patient Name
                return booking.getPatientName();
            case 4: //Name
                return booking.getDoctorName();
            case 5: //Phone
                return booking.getBkPhone();
            case 6:
                return booking.getBkSerialNo();
            case 7: // W/L
                return booking.getBkActive();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listBooking == null) {
            return;
        }

        if (listBooking.isEmpty()) {
            return;
        }

        VBooking booking = listBooking.get(row);
        switch (column) {
            case 2: //Reg No.
                try {
                if (value != null) {
                    String regNo = value.toString();
                    Patient pt = (Patient) dao.find(Patient.class, regNo);
                    if (pt == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid registeration number.", "Booking Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        booking.setRegNo(pt.getRegNo());
                        booking.setPatientName(pt.getPatientName());
                        booking.setBkActive(Boolean.TRUE);
                    }
                }
            } catch (Exception ex) {
                log.error("setValueAt : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case 7: //W/L
                Boolean tmpVal = (Boolean) value;
                if (tmpVal && Util1.getNullTo(booking.getRegNo(), null) == null) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid registeration number.", "Booking Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    booking.setBkActive(tmpVal);
                }
                break;
        }

        try {
            fireTableCellUpdated(row, 7);
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {

        }

        try {
            Booking tmpBooking = (Booking) dao.find(Booking.class, booking.getBookingId());
            if (tmpBooking != null) {
                /*tmpBooking.setBookingId(booking.getBookingId());
                tmpBooking.setBkDate(booking.getBkDate());*/
                tmpBooking.setRegNo(booking.getRegNo());
                tmpBooking.setPatientName(booking.getPatientName());
                tmpBooking.setBkPhone(booking.getBkPhone());
                tmpBooking.setBkActive(booking.getBkActive());
                saveBooking(tmpBooking);
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.toString());
        } finally {

        }
    }

    @Override
    public int getRowCount() {
        return listBooking.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VBooking> getListBooking() {
        return listBooking;
    }

    public void setListBooking(List<VBooking> listBooking) {
        this.listBooking = listBooking;
        fireTableDataChanged();
    }

    public VBooking getBooking(int row) {
        return listBooking.get(row);
    }

    public void setBooking(int row, VBooking booking) {
        listBooking.set(row, booking);
        fireTableRowsUpdated(row, row);
    }

    public void addBooking(VBooking booking) {
        listBooking.add(booking);
        fireTableRowsInserted(listBooking.size() - 1, listBooking.size() - 1);
    }

    public void deleteBooking(int row) {
        listBooking.remove(row);
        fireTableRowsDeleted(0, listBooking.size() - 1);
    }

    public void delete(int row) {
        VBooking booking = listBooking.get(row);
        String sql;
        if (booking.getRegNo() != null) {
            try {
                //dao.open();
                //dao.beginTran();
                /*  sql = "delete from opd_booking where reg_no = '"
                        + booking.getRegNo() + "'";*/
                sql = "delete from opd_booking where bk_id ='" + booking.getBookingId() + "'";
                dao.deleteSQL(sql);
                listBooking.remove(row);
                updateBooking(booking.getBkSerialNo(), booking.getDoctorId(), booking.getBkDate());
                fireTableRowsDeleted(row, row);
                fireTableDataChanged();

            } catch (Exception e) {
                log.error("delete :" + e.getMessage());
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    private void updateBooking(Integer serNo, String doctorId, Date date) {
        try {
            listBk = dao.findAllHSQL("select o from Booking o where o.bkSerialNo > " 
                    + serNo + " and o.doctor.doctorId ='" + doctorId + "' and o.bkDate ='" 
                    + DateUtil.toDateStr(date, "yyyy-MM-dd") + "' ");
            for (Booking bList : listBk) {

                updateBK.setBookingId(bList.getBookingId());
                updateBK.setBkDate(bList.getBkDate());
                updateBK.setRegNo(bList.getRegNo());
                updateBK.setPatientName(bList.getPatientName());
                updateBK.setBkPhone(bList.getBkPhone());
                updateBK.setBkActive(bList.getBkActive());
                updateBK.setBkSerialNo(bList.getBkSerialNo() - 1);
                updateBK.setDoctor(bList.getDoctor());
                dao.save(updateBK);

            }
        } catch (Exception ex) {
            log.info("ERROR :" + ex.getMessage());
        }finally{
            dao.close();
        }
    }

    private void saveBooking(Booking booking) {

        try {
            dao.save(booking);
        } catch (Exception ex) {
            log.error("saveBooking : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(Util1.getParent(),
                    ex.toString(), "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } finally {
            dao.close();
        }

    }

}
