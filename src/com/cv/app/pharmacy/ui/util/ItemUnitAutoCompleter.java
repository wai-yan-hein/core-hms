/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.ui.common.ItemUnitAutoCompleteTableModel;
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
public class ItemUnitAutoCompleter implements KeyListener {

    static Logger log = Logger.getLogger(ItemUnitAutoCompleter.class.getName());
    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private ItemUnitAutoCompleteTableModel acTableModel;
    private ItemUnit selUnit;
    private AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;

    public ItemUnitAutoCompleter(JTextComponent comp, List<ItemUnit> list,
            AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);
        acTableModel = new ItemUnitAutoCompleteTableModel(list);
        table.setModel(acTableModel);
        table.setFont(Global.textFont); // NOI18N
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        table.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);//Unit
        table.getColumnModel().getColumn(1).setPreferredWidth(80);//Description

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(150, 200);

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
    }

    private Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            ItemUnitAutoCompleter completer = (ItemUnitAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);

            if (completer.table.getSelectedRow() != -1) {
                selUnit = acTableModel.getItemUnit(completer.table.convertRowIndexToModel(
                        completer.table.getSelectedRow()));
                ((JTextField) completer.textComp).setText(selUnit.getItemUnitCode());
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
            ItemUnitAutoCompleter completer = (ItemUnitAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            ItemUnitAutoCompleter completer = (ItemUnitAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            ItemUnitAutoCompleter completer = (ItemUnitAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
            }
        }
    };

    protected void selectNextPossibleValue() {
        int si = table.getSelectedRow();
        if (si < acTableModel.getSize() - 1) {
            table.setRowSelectionInterval(si + 1, si + 1);
        }

        //This code for auto scroll
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

        //This code for auto scroll
        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    public ItemUnit getSelUnit() {
        return selUnit;
    }

    /*
     * KeyListener implementation
     */
    /**
     * Handle the key typed event from the text field.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("keyTyped");
    }

    /**
     * Handle the key-pressed event from the text field.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("keyPressed");
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            editor.stopCellEditing();
        }
    }

    /**
     * Handle the key-released event from the text field.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("keyReleased");
        String filter = textComp.getText();

        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            //sorter.setRowFilter(RowFilter.regexFilter(filter));
            sorter.setRowFilter(startsWithFilter);
        }
    }

    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            //for (int i = entry.getValueCount() - 1; i >= 0; i--) {
            /*if(NumberUtil.isNumber(textComp.getText())){
                if(entry.getStringValue(0).toUpperCase().startsWith(
                        textComp.getText().toUpperCase())){
                    return true;
                }
            }else{
                if(entry.getStringValue(1).toUpperCase().startsWith(
                        textComp.getText().toUpperCase())){
                    return true;
                }else if(entry.getStringValue(2).toUpperCase().startsWith(
                        textComp.getText().toUpperCase())){
                    return true;
                }
            }*/

            for (int i = 0; i < entry.getValueCount(); i++) {
                //String tmp = entry.getStringValue(i);
                if (entry.getStringValue(i).toUpperCase().startsWith(
                        textComp.getText().toUpperCase())) {
                    return true;
                }
            }

            return false;
        }
    };
}
