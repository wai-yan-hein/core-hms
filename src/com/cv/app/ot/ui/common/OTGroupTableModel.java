/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.ot.database.entity.OTProcedureGroup;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTGroupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTGroupTableModel.class.getName());
    private List<OTProcedureGroup> listOTG = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};
    private final AbstractDataAccess dao;

    public OTGroupTableModel(AbstractDataAccess dao) {
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
        OTProcedureGroup record = listOTG.get(row);

        switch (column) {
            case 0: //Code
                return record.getUserCode();
            case 1: //Description
                return record.getGroupName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OTProcedureGroup record = listOTG.get(row);

        switch (column) {
            case 0: //Code
                if (value != null) {
                    record.setUserCode(value.toString());
                } else {
                    record.setUserCode(null);
                }
                break;
            case 1: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setGroupName(value.toString());

                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OT Group",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
        }

        save(record);
        uploadToAccount(record.getGroupId());

        try {
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {

        }
    }

    private void save(OTProcedureGroup record) {
        if (!Util1.getString(record.getGroupName(), "-").equals("-")) {
            try {
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
        try {
            listOTG = dao.findAllHSQL("select o from OTProcedureGroup o order by o.userCode");
            if (listOTG == null) {
                listOTG = new ArrayList();
            }
            addNewRow();
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("getOTGroup : " + ex.getMessage());
        } finally {
            dao.close();
        }
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

    private void uploadToAccount(Integer vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");

            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    String url = rootUrl + "/otGroup";
                    final HttpPost request = new HttpPost(url);
                    final List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("id", vouNo.toString()));
                    request.setEntity(new UrlEncodedFormEntity(params));
                    CloseableHttpResponse response = httpClient.execute(request);
                    try {
                        dao.execSql("update ot_group set intg_upd_status = null where cat_id = " + vouNo);
                    } catch (Exception ex) {
                        log.error("uploadToAccount error 1: " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                    // Handle the response
                    try ( BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                        String output;
                        while ((output = br.readLine()) != null) {
                            if (!output.equals("Sent")) {
                                log.error("Error in server : " + vouNo + " : " + output);
                            }
                        }
                    }
                } catch (IOException e) {
                    try {
                        dao.execSql("update ot_group set intg_upd_status = null where cat_id = " + vouNo);
                    } catch (Exception ex) {
                        log.error("uploadToAccount error : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            } else {
                try {
                    dao.execSql("update ot_group set intg_upd_status = null where cat_id = " + vouNo);
                } catch (Exception ex) {
                    log.error("uploadToAccount error : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }
    }
}
