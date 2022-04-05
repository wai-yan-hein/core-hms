/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author WSwe
 */
public class NumberKeyListener implements KeyListener{
     /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     */
    @Override
    public void keyTyped(KeyEvent e){
        char c = e.getKeyChar();
        
        if(Character.isLetter(c)){
            e.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     */
    @Override
    public void keyPressed(KeyEvent e){
        
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     */
    @Override
    public void keyReleased(KeyEvent e){
        
    }
}
