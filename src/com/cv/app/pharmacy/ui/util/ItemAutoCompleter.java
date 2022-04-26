/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.common.ItemAutoCompleteTableModel;
import com.cv.app.pharmacy.ui.common.ItemAutoCompleteTableModel1;
import com.cv.app.util.NumberUtil;
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
public class ItemAutoCompleter implements KeyListener {

    static Logger log = Logger.getLogger(ItemAutoCompleter.class.getName());
    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private ItemAutoCompleteTableModel acTableModel;
    private ItemAutoCompleteTableModel1 acTableModel1;
    private VMedicine1 selMed;
    public AbstractCellEditor editor;
    private final TableRowSorter<TableModel> sorter;
    private String strSpaceFilter = Util1.getPropValue("system.pharmacy.space.filter");
    private String catFilter = Util1.getPropValue("system.pharmacy.code.filter.cat");
    private String completeModel = Util1.getPropValue("system.pharmacy.itemcomplete.model");

    public ItemAutoCompleter(JTextComponent comp, List<VMedicine1> list,
            AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);
        if (completeModel.equals("O")) {
            acTableModel1 = new ItemAutoCompleteTableModel1(list);
            table.setModel(acTableModel1);
        } else {
            acTableModel = new ItemAutoCompleteTableModel(list);
            table.setModel(acTableModel);
        }
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(23);
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(250);//Description
        table.getColumnModel().getColumn(2).setPreferredWidth(30);//Relation-Srt
        if (!completeModel.equals("O")) {
            table.getColumnModel().getColumn(3).setPreferredWidth(30);//Barcode
            table.getColumnModel().getColumn(4).setPreferredWidth(30);//Category
        }
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mouseSelect();
                }
            }
        });

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(800, 250);

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

        if (list.size() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            if (completeModel.equals("O")) {
                selMed = acTableModel1.getItem(table.convertRowIndexToModel(
                        table.getSelectedRow()));
            } else {
                selMed = acTableModel.getItem(table.convertRowIndexToModel(
                        table.getSelectedRow()));
            }
            ((JTextField) textComp).setText(selMed.getMedId());
        }

        popup.setVisible(false);
        editor.stopCellEditing();
    }

    private Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            ItemAutoCompleter completer = (ItemAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);

            if (completer.table.getSelectedRow() != -1) {
                if (completeModel.equals("O")) {
                    selMed = acTableModel1.getItem(completer.table.convertRowIndexToModel(
                            completer.table.getSelectedRow()));
                } else {
                    selMed = acTableModel.getItem(completer.table.convertRowIndexToModel(
                            completer.table.getSelectedRow()));
                }
                ((JTextField) completer.textComp).setText(selMed.getMedId());
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
                    log.error(ex.toString());
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
            ItemAutoCompleter completer = (ItemAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            ItemAutoCompleter completer = (ItemAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            ItemAutoCompleter completer = (ItemAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
            }
        }
    };

    protected void selectNextPossibleValue() {
        int si = table.getSelectedRow();

        if (si < table.getRowCount() - 1) {
            try {
                table.setRowSelectionInterval(si + 1, si + 1);
            } catch (Exception ex) {

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
            try {
                table.setRowSelectionInterval(si - 1, si - 1);
            } catch (Exception ex) {

            }
        }

        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    public VMedicine1 getSelMed() {
        return selMed;
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
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Handle the key-released event from the text field.
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        String filter = textComp.getText();

        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            String value = Util1.getPropValue("system.iac.filter");

            if (value.equals("Y")) {
                //sorter.setRowFilter(RowFilter.regexFilter(filter));
                sorter.setRowFilter(inStringFilter);
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

    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String text = textComp.getText().toUpperCase();
            String[] strList = text.split(" ");
            if (completeModel.equals("O")) {
                String tmp1 = entry.getStringValue(0).toUpperCase();
                String tmp2 = entry.getStringValue(1).toUpperCase();

                return tmp1.startsWith(text) || tmp2.startsWith(text);
            } else {
                if (strSpaceFilter.equals("Y") && strList.length == 2) {
                    if (!strList[1].isEmpty()) {
                        String tmp1 = entry.getStringValue(0).toUpperCase();
                        String tmp2 = entry.getStringValue(1).toUpperCase();

                        if (strList.length > 2) {
                            tmp2 = text.replace(strList[0], "");
                        }
                        if (NumberUtil.isNumber(strList[0])) {
                            return tmp1.startsWith(strList[0]) && tmp2.startsWith(strList[1]);
                        } else {
                            return tmp1.startsWith(strList[1]) && tmp2.startsWith(strList[0]);
                        }
                    } else {
                        String tmp1 = entry.getStringValue(0).toUpperCase();
                        String tmp2 = entry.getStringValue(1).toUpperCase();
                        String tmp3 = entry.getStringValue(3).toUpperCase();
                        //String tmp4 = entry.getStringValue(4).toUpperCase();

                        return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text); //|| tmp4.startsWith(text);
                    }
                } else {
                    String tmp1 = entry.getStringValue(0).toUpperCase();
                    String tmp2 = entry.getStringValue(1).toUpperCase();
                    String tmp3 = entry.getStringValue(3).toUpperCase();
                    String tmp4 = entry.getStringValue(4).toUpperCase();
                    if (catFilter.equals("Y")) {
                        return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text) || tmp4.startsWith(text);
                    } else {
                        return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text);
                    }
                }
            }
        }
    };

    private final RowFilter<Object, Object> inStringFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String text = textComp.getText();
            String[] strList = text.split(" ");
            if (completeModel.equals("O")) {
                String tmp1 = entry.getStringValue(0);
                String tmp2 = entry.getStringValue(1);
                boolean result = tmp1.startsWith(text);
                int result1 = tmp2.indexOf(text);

                if (result) {
                    return true;
                } else {
                    if (result1 == -1) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                if (strSpaceFilter.equals("Y") && strList.length == 2) {
                    if (!strList[1].isEmpty()) {
                        String tmp1 = entry.getStringValue(0).toUpperCase();
                        String tmp2 = entry.getStringValue(1).toUpperCase();

                        if (strList.length > 2) {
                            tmp2 = text.replace(strList[0], "");
                        }
                        if (NumberUtil.isNumber(strList[0])) {
                            return tmp1.startsWith(strList[0]) && tmp2.startsWith(strList[1]);
                        } else {
                            return tmp1.startsWith(strList[1]) && tmp2.startsWith(strList[0]);
                        }
                    } else {
                        String tmp1 = entry.getStringValue(0).toUpperCase();
                        String tmp2 = entry.getStringValue(1).toUpperCase();
                        String tmp3 = entry.getStringValue(3).toUpperCase();
                        //String tmp4 = entry.getStringValue(4).toUpperCase();

                        return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text); //|| tmp4.startsWith(text);
                    }
                } else {
                    String tmp1 = entry.getStringValue(0).toUpperCase();
                    String tmp2 = entry.getStringValue(1).toUpperCase();
                    String tmp3 = entry.getStringValue(3).toUpperCase();
                    String tmp4 = entry.getStringValue(4).toUpperCase();
                    if (catFilter.equals("Y")) {
                        return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text) || tmp4.startsWith(text);
                    } else {
                        return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text);
                    }
                }
            }
        }
    };
}
