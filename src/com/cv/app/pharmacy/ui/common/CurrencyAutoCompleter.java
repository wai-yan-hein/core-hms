/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 *
 * @author winswe
 */
public class CurrencyAutoCompleter implements KeyListener {

    private final JTable table = new JTable();
    private final JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    public AbstractCellEditor editor;
    private final TableRowSorter<TableModel> sorter;
    private final List<Currency> list;
    private final CurrencyTableModel model;
    private Currency selItem;
    private final AbstractDataAccess dao = Global.dao;
    
    public CurrencyAutoCompleter(JTextComponent comp, AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        table.setRowHeight(23);

        list = dao.findAllHSQL("select o from Currency o order by o.currencyName");

        model = new CurrencyTableModel();
        model.setListCurrency(list);
        table.setModel(model);
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(50);//Code
        table.getColumnModel().getColumn(2).setPreferredWidth(20);//Code

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
        popup.setPopupSize(200, 100);

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
            selItem = model.getCurrency(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            ((JTextField) textComp).setText(selItem.getCurrencyCode());
        }

        popup.setVisible(false);
        editor.stopCellEditing();
    }

    static Action showAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            CurrencyAutoCompleter completer = (CurrencyAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            CurrencyAutoCompleter completer = (CurrencyAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            CurrencyAutoCompleter completer = (CurrencyAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            sorter.setRowFilter(startsWithFilter);
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

            String tmp1 = entry.getStringValue(0).toUpperCase();
            String tmp2 = entry.getStringValue(1).toUpperCase();

            return tmp1.startsWith(text) || tmp2.startsWith(text);
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

                }

                popup.show(textComp, x, textComp.getHeight());
            } else {
                popup.setVisible(false);
            }
        }
        textComp.requestFocus();
    }

    private final Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            CurrencyAutoCompleter completer = (CurrencyAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);

            if (completer.table.getSelectedRow() != -1) {
                selItem = model.getCurrency(completer.table.convertRowIndexToModel(
                        completer.table.getSelectedRow()));
                ((JTextField) completer.textComp).setText(selItem.getCurrencyCode());
            }

            completer.popup.setVisible(false);
            editor.stopCellEditing();
        }
    };
    
    public Currency getSelectedItem(){
        return selItem;
    }
}
