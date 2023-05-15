/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.OPDCategory;
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
public class OPDCategoryTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDCategoryTableModel.class.getName());
    private List<OPDCategory> listOPDCategory = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};
    private final AbstractDataAccess dao;
    private int groupId;

    public OPDCategoryTableModel(AbstractDataAccess dao) {
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
            case 0: //Code
                return record.getUserCode();
            case 1: //Description
                return record.getCatName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDCategory record = listOPDCategory.get(row);

        switch (column) {
            case 0: //Code
                if (value != null) {
                    record.setUserCode(value.toString());
                }
                break;
            case 1: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setCatName(value.toString());
                    record.setGroupId(groupId);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OPD Category",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
        }
        
        save(record);
        uploadToAccount(record.getCatId());
        
        try {
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {

        }
    }

    private void save(OPDCategory record) {
        if (!Util1.getString(record.getCatName(), "-").equals("-")) {
            try {
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
        try {
            listOPDCategory = dao.findAllHSQL(
                    "select o from OPDCategory o where o.groupId = " + groupId + " order by o.userCode");
            addNewRow();
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("getCategory : " + ex.getMessage());
        } finally {
            dao.close();
        }
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
    
    private void uploadToAccount(Integer vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");

            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    String url = rootUrl + "/opdCategory";
                    final HttpPost request = new HttpPost(url);
                    final List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("id", vouNo.toString()));
                    request.setEntity(new UrlEncodedFormEntity(params));
                    CloseableHttpResponse response = httpClient.execute(request);
                    // Handle the response
                    try ( BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                        String output;
                        while ((output = br.readLine()) != null) {
                            if (!output.equals("Sent")) {
                                log.error("Error in server : " + vouNo + " : " + output);
                                try {
                                    dao.execSql("update opd_category set intg_upd_status = null where cat_id = " + vouNo);
                                } catch (Exception ex) {
                                    log.error("uploadToAccount error 1: " + ex.getMessage());
                                } finally {
                                    dao.close();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    try {
                        dao.execSql("update opd_category set intg_upd_status = null where cat_id = " + vouNo);
                    } catch (Exception ex) {
                        log.error("uploadToAccount error : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            } else {
                try {
                    dao.execSql("update opd_category set intg_upd_status = null where cat_id = " + vouNo);
                } catch (Exception ex) {
                    log.error("uploadToAccount error : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }
    }
}
