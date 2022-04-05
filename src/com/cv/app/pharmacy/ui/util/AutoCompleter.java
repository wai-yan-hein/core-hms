/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.pharmacy.ui.common.AutoCompleterTableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class AutoCompleter {
  static Logger log = Logger.getLogger(AutoCompleter.class.getName());
  private JTable table = new JTable();
  private JPopupMenu popup = new JPopupMenu();
  private JTextComponent textComp;
  private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
  private AutoCompleterTableModel acTableModel;
  
  public AutoCompleter(JTextComponent comp, List list, Object[] column) {
    this.textComp = comp;
    textComp.putClientProperty(AUTOCOMPLETER, this);
    acTableModel = new AutoCompleterTableModel(list, column);
    table.setModel(acTableModel);
    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(null);

    table.setFocusable(false);
    scroll.getVerticalScrollBar().setFocusable(false);
    scroll.getHorizontalScrollBar().setFocusable(false);

    popup.setBorder(BorderFactory.createLineBorder(Color.black));
    popup.setPopupSize(120, 120);

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
      AutoCompleter completer = (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);

      if (completer.table.getSelectedRow() != -1) {
        ((JTextField) completer.textComp).setText(acTableModel.getValueAt(
                completer.table.convertRowIndexToModel(completer.table.getSelectedRow()), 
                0).toString());
      }

      completer.popup.setVisible(false);
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
      AutoCompleter completer = (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
      AutoCompleter completer = (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
      AutoCompleter completer = (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
      if (tf.isEnabled()) {
        completer.popup.setVisible(false);
      }
    }
  };

  protected void selectNextPossibleValue() {
    int si = table.getSelectedRow();

    if (si != - 1) {
      table.setRowSelectionInterval(si + 1, si + 1);
    }
  }

  /**
   * Selects the previous item in the list. It won't change the selection if the
   * currently selected item is already the first item.
   */
  protected void selectPreviousPossibleValue() {
    int si = table.getSelectedRow();

    if (si > 0) {
      table.setRowSelectionInterval(si - 1, si - 1);
    }
  }
}
