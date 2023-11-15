/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.ui.common.MedPriceList;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author WSwe
 */
public class MedPriceAutoCompleter {
    static Logger log = Logger.getLogger(MedPriceAutoCompleter.class.getName());
    private JTable tblPrice = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private List<MedPriceList> lstMedPrice;
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private AbstractCellEditor editor;
    
    public MedPriceAutoCompleter(JTextComponent comp, List<MedPriceList> lstMedPrice,
            AbstractCellEditor editor){
        this.textComp = comp;
        this.lstMedPrice = lstMedPrice;
        this.editor = editor;
        
        textComp.putClientProperty(AUTOCOMPLETER, this); 
        initTable();
        
        JScrollPane scroll = new JScrollPane(tblPrice); 
        scroll.setBorder(null); 
        
        tblPrice.setFocusable(false);
        tblPrice.setRowHeight(23);
        scroll.getVerticalScrollBar().setFocusable( false ); 
        scroll.getHorizontalScrollBar().setFocusable( false ); 
 
        popup.setBorder(BorderFactory.createLineBorder(Color.black)); 
        popup.setPopupSize(120, 180);
        
        popup.add(scroll);
        
        if(textComp instanceof JTextField){ 
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), 
                    JComponent.WHEN_FOCUSED); 
            textComp.getDocument().addDocumentListener(documentListener); 
        }else 
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 
                    InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_FOCUSED); 
 
        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), 
                JComponent.WHEN_FOCUSED); 
        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
                JComponent.WHEN_FOCUSED); 
 
        popup.addPopupMenuListener(new PopupMenuListener(){ 
            public void popupMenuWillBecomeVisible(PopupMenuEvent e){ 
            } 
 
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ 
                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)); 
            } 
 
            public void popupMenuCanceled(PopupMenuEvent e){ 
            } 
        });
        
        tblPrice.setRequestFocusEnabled(false);
    }
    
    private Action acceptAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            MedPriceAutoCompleter completer = (MedPriceAutoCompleter)tf.getClientProperty(AUTOCOMPLETER); 
            
            if(completer.tblPrice.getSelectedRow() != -1){
                ((JTextField)completer.textComp).setText(completer.lstMedPrice.get(completer.tblPrice
                        .getSelectedRow()).getPrice().toString());
            }
            
            completer.popup.setVisible(false);
            editor.stopCellEditing();
        } 
    };
    
    DocumentListener documentListener = new DocumentListener(){ 
        public void insertUpdate(DocumentEvent e){ 
            showPopup(); 
        } 
 
        public void removeUpdate(DocumentEvent e){ 
            showPopup(); 
        } 
 
        public void changedUpdate(DocumentEvent e){} 
    }; 
 
    private void showPopup(){
        if(!popup.isVisible()){
            //popup.setVisible(false); 
            if(textComp.isEnabled() && !lstMedPrice.isEmpty()){ 
                if(!(textComp instanceof JTextField)) 
                    textComp.getDocument().addDocumentListener(documentListener); 

                textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
                        JComponent.WHEN_FOCUSED); 

                int x = 0; 
                try{ 
                    int pos = Math.min(textComp.getCaret().getDot(), textComp.getCaret().getMark()); 
                    x = textComp.getUI().modelToView(textComp, pos).x; 
                } catch(BadLocationException ex){ 
                    log.error(ex.toString());
                }
                
                popup.show(textComp, x, textComp.getHeight());
            }else 
                popup.setVisible(false); 
        }
        textComp.requestFocus(); 
    } 
 
    static Action showAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            MedPriceAutoCompleter completer = (MedPriceAutoCompleter)tf.getClientProperty(AUTOCOMPLETER); 
            if(tf.isEnabled()){ 
                if(completer.popup.isVisible()) 
                    completer.selectNextPossibleValue(); 
                else 
                    completer.showPopup(); 
            } 
        } 
    }; 
 
    static Action upAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            MedPriceAutoCompleter completer = (MedPriceAutoCompleter)tf.getClientProperty(AUTOCOMPLETER); 
            if(tf.isEnabled()){ 
                if(completer.popup.isVisible()) 
                    completer.selectPreviousPossibleValue(); 
            } 
        } 
    }; 
 
    static Action hidePopupAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            MedPriceAutoCompleter completer = (MedPriceAutoCompleter)tf.getClientProperty(AUTOCOMPLETER); 
            if(tf.isEnabled()) 
                completer.popup.setVisible(false); 
        } 
    };
    
    protected void selectNextPossibleValue(){ 
        int si = tblPrice.getSelectedRow();
        
        if(si < lstMedPrice.size() - 1){ 
            tblPrice.setRowSelectionInterval(si+1, si+1);
        } 
    } 
 
    /** 
     * Selects the previous item in the list.  It won't change the selection if the 
     * currently selected item is already the first item. 
     */ 
    protected void selectPreviousPossibleValue(){ 
        int si = tblPrice.getSelectedRow();
 
        if(si > 0){ 
            tblPrice.setRowSelectionInterval(si-1, si-1);
        } 
    }
    
    /*
     * Initialize tblPrice
     */
    private void initTable(){
        //Binding table with lstMedPrice using beansbinding library.
        JTableBinding jTableBinding = SwingBindings.createJTableBinding(
                AutoBinding.UpdateStrategy.READ_WRITE, lstMedPrice, tblPrice);
        JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.
                jdesktop.beansbinding.ELProperty.create("${type}"));
        columnBinding.setColumnName("Type");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        
        columnBinding = jTableBinding.addColumnBinding(org.
                jdesktop.beansbinding.ELProperty.create("${price}"));
        columnBinding.setColumnName("Price");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        
        jTableBinding.bind();
    }
}
