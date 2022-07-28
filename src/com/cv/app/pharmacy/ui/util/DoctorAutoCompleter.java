/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.pharmacy.ui.common.DoctorAutoCompleteTableModel;
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
public class DoctorAutoCompleter implements KeyListener {
    static Logger log = Logger.getLogger(DoctorAutoCompleter.class.getName());
    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private DoctorAutoCompleteTableModel acTableModel;
    private Doctor selDoctor;
    private AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;

    public DoctorAutoCompleter(JTextComponent comp, List<Doctor> list,
            AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(new java.awt.Font("Zawgyi-One", 0, 11));
        acTableModel = new DoctorAutoCompleteTableModel(list);
        table.setModel(acTableModel);
        table.setFont(new java.awt.Font("Zawgyi-One", 0, 11)); // NOI18N
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        table.setRowHeight(23);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(110);//Description

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(400, 200);

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
            DoctorAutoCompleter completer = (DoctorAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);

            if (completer.table.getSelectedRow() != -1) {
                selDoctor = acTableModel.getDoctor(completer.table.convertRowIndexToModel(
                        completer.table.getSelectedRow()));
                ((JTextField) completer.textComp).setText(selDoctor.getDoctorId());
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
            DoctorAutoCompleter completer = (DoctorAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DoctorAutoCompleter completer = (DoctorAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DoctorAutoCompleter completer = (DoctorAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public Doctor getSelDoctor() {
        return selDoctor;
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
            sorter.setRowFilter(startsWithFilter);
        }
        
        try {
            if (e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_UP) {
                table.setRowSelectionInterval(0, 0);
            }
        } catch (Exception ex) {
        }
    }
    
    private RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                if (entry.getStringValue(i).toUpperCase().startsWith(
                        textComp.getText().toUpperCase())) {
                    return true;
                }
            }
            
            return false;
        }
    };
}
