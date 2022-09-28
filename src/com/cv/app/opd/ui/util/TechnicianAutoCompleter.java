/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.util;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Technician;
import com.cv.app.opd.ui.common.TechnicianAutoCompleteTableModel;
import com.cv.app.util.Util1;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class TechnicianAutoCompleter implements KeyListener {

    static Logger log = Logger.getLogger(TechnicianAutoCompleter.class.getName());
    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private TechnicianAutoCompleteTableModel acTableModel;
    private Technician selTech;
    private AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;

    public TechnicianAutoCompleter(JTextComponent comp, List<Technician> list,
            AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(new java.awt.Font("Zawgyi-One", 0, 11));
        acTableModel = new TechnicianAutoCompleteTableModel(list);
        table.setModel(acTableModel);
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(23);
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        //table.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        //table.getColumnModel().getColumn(1).setPreferredWidth(150);//Description
        //table.getColumnModel().getColumn(2).setPreferredWidth(30);//Fees

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(400, 300);

        popup.add(scroll);

        if (textComp instanceof JTextField) {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                    JComponent.WHEN_FOCUSED);
            textComp.getDocument().addDocumentListener(documentListener);
        } else {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
                    KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
        }

        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                JComponent.WHEN_FOCUSED);
        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);

        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        table.setRequestFocusEnabled(false);
        //if (list.size() > 0) {
        //    table.setRowSelectionInterval(0, 0);
        //}
    }

    private Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TechnicianAutoCompleter completer = (TechnicianAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);

            if (completer.table.getSelectedRow() != -1) {
                selTech = acTableModel.getTechnician(completer.table.convertRowIndexToModel(
                        completer.table.getSelectedRow()));
                //((JTextField) completer.textComp).setText(selService.getMedId());
            }

            completer.popup.setVisible(false);
            editor.stopCellEditing();
        }
    };

    DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            showPopup();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            showPopup();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    private void showPopup() {
        if (!popup.isVisible()) {
            textComp.addKeyListener(this);
            //popup.setVisible(false); 
            if (textComp.isEnabled()) {
                if (!(textComp instanceof JTextField)) {
                    textComp.getDocument().addDocumentListener(documentListener);
                }

                textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        JComponent.WHEN_FOCUSED);

                int x = 0;
                try {
                    int pos = Math.min(textComp.getCaret().getDot(), textComp.getCaret().getMark());
                    x = textComp.getUI().modelToView(textComp, pos).x;
                } catch (BadLocationException ex) {
                    log.info("showPopup : " + ex.toString());
                }

                popup.show(textComp, x, textComp.getHeight());
            } else {
                popup.setVisible(false);
            }
        }
        textComp.requestFocus();
    }

    static Action showAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TechnicianAutoCompleter completer = (TechnicianAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    completer.showPopup();
                }
            }
        }
    };

    static Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TechnicianAutoCompleter completer = (TechnicianAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectPreviousPossibleValue();
                }
            }
        }
    };

    static Action hidePopupAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TechnicianAutoCompleter completer = (TechnicianAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
            }
        }
    };

    protected void selectNextPossibleValue() {
        int si = table.getSelectedRow();
        if (si < acTableModel.getSize() - 1) {
            try {
                table.setRowSelectionInterval(si + 1, si + 1);
            } catch (Exception ex) {
                log.error("selectNextPossibleValue : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            }
        }

        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    /**
     * Selects the previous item in the list. It won't change the selection if
     * the currently selected item is already the first item.
     */
    protected void selectPreviousPossibleValue() {
        int si = table.getSelectedRow();

        if (si > 0) {
            table.setRowSelectionInterval(si - 1, si - 1);
        }

        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    public Technician getTechnician() {
        return selTech;
    }

    /*
     * KeyListener implementation
     */
    /**
     * Handle the key typed event from the text field.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Handle the key-pressed event from the text field.
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Handle the key-released event from the text field.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        String filter = textComp.getText();

        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            //sorter.setRowFilter(RowFilter.regexFilter(filter));
            String value = Util1.getPropValue("system.iac.filter");

            if (value.equals("Y")) {
                sorter.setRowFilter(RowFilter.regexFilter(filter));
            } else {
                sorter.setRowFilter(startsWithFilter);
            }

            try {
                if (e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_UP) {
                    table.setRowSelectionInterval(0, 0);
                }
            } catch (Exception ex) {
            }
        }
    }

    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            /*if(entry.getStringValue(0).toUpperCase().startsWith(
             textComp.getText().toUpperCase())){
             return true;
             }
            
             return false;*/

            String tmp1 = entry.getStringValue(0).toUpperCase();
            String text = textComp.getText().toUpperCase();

            if (tmp1.startsWith(text)) {
                return true;
            } else {
                return false;
            }
        }
    };
}
