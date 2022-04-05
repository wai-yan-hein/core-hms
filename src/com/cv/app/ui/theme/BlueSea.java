/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.theme;

import com.jgoodies.looks.plastic.PlasticTheme;
import javax.swing.plaf.ColorUIResource;

/**
 *
 * @author Aung Naing Lin <anlin at best>
 */
public class BlueSea extends PlasticTheme {
    
    private static ColorUIResource BLUE_MEDIUM_DARKEST = 
            new ColorUIResource(47, 78, 111);
    private static ColorUIResource BLUE_MEDIUM_LIGHTER = 
            new ColorUIResource(152, 177, 196);
    private static ColorUIResource BLUE_MEDIUM_LIGHTEST = 
            new ColorUIResource(200, 215, 217);
    
    private static ColorUIResource ORANGE = 
            new ColorUIResource(225, 81, 25);
   
    @Override
    public String getName() {
        return "BlueSea";
    }

    @Override
    protected ColorUIResource getPrimary1() {
        return BLUE_MEDIUM_DARKEST;
    }
    
    @Override
    protected ColorUIResource getPrimary2() {
        return BLUE_MEDIUM_LIGHTER;
    }
    
    @Override
    protected ColorUIResource getPrimary3() {
        return BLUE_MEDIUM_LIGHTEST;
    }
    
    @Override
    protected ColorUIResource getSecondary1() {
        return BLUE_MEDIUM_DARKEST;
    }
    
    @Override
    protected ColorUIResource getSecondary2() {
        return BLUE_MEDIUM_LIGHTER;
    }
    
    @Override
    protected ColorUIResource getSecondary3() {
        return BLUE_MEDIUM_LIGHTEST;
    }

    @Override
    public ColorUIResource getMenuItemSelectedBackground() {
        return ORANGE;
    }
    
    @Override
    public ColorUIResource getMenuItemSelectedForeground() {
        return getWhite();
    }
    
    @Override
    public ColorUIResource getMenuSelectedBackground() {
        return BLUE_MEDIUM_LIGHTER;
    }

//    public ColorUIResource getFocusColor() {
//        return PlasticLookAndFeel.getHighContrastFocusColorsEnabled()
//            ? Colors.YELLOW_FOCUS
//            : super.getFocusColor();
//    }

    /*
     * TODO: The following two lines are likely an improvement.
     *       However, they require a rewrite of the PlasticInternalFrameTitlePanel.
    public    ColorUIResource getWindowTitleBackground()     { return getPrimary1(); }
    public    ColorUIResource getWindowTitleForeground()     { return WHITE;     }  
    */

//    public void addCustomEntriesToTable(UIDefaults table) {
//        super.addCustomEntriesToTable(table);
//        Object[] uiDefaults =
//            { PlasticScrollBarUI.MAX_BUMPS_WIDTH_KEY, new Integer(30), };
//        table.putDefaults(uiDefaults);
//    }
}
