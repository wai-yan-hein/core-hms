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
public class GoGreen extends PlasticTheme {
    
    private static ColorUIResource GREEN_MEDIUM_DARKEST = 
            new ColorUIResource(57, 114, 73);
    private static ColorUIResource GREEN_MEDIUM_LIGHTER = 
            new ColorUIResource(98, 139, 97);
    private static ColorUIResource GREEN_MEDIUM_LIGHTEST = 
            new ColorUIResource(199, 225, 186);
    
    private static ColorUIResource ORANGE = 
            new ColorUIResource(225, 81, 25);
    private static ColorUIResource LIGHT_GREEN = 
            new ColorUIResource(243, 213, 189);
   
    @Override
    public String getName() {
        return "BlueSea";
    }

    @Override
    protected ColorUIResource getPrimary1() {
        return GREEN_MEDIUM_DARKEST;
    }
    
    @Override
    protected ColorUIResource getPrimary2() {
        return GREEN_MEDIUM_LIGHTER;
    }
    
    @Override
    protected ColorUIResource getPrimary3() {
        //return GREEN_MEDIUM_LIGHTEST;
        return LIGHT_GREEN;
    }
    
    @Override
    protected ColorUIResource getSecondary1() {
        return GREEN_MEDIUM_DARKEST;
    }
    
    @Override
    protected ColorUIResource getSecondary2() {
        return GREEN_MEDIUM_LIGHTER;
    }
    
    @Override
    protected ColorUIResource getSecondary3() {
        return GREEN_MEDIUM_LIGHTEST;
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
        return GREEN_MEDIUM_LIGHTER;
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
