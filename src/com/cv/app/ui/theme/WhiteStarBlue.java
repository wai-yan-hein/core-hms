/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.theme;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.Silver;
import javax.swing.plaf.ColorUIResource;

/**
 *
 * @author Aung Naing Lin <anlin at best>
 */
public class WhiteStarBlue extends Silver{
     private static final ColorUIResource GRAY_VERY_LIGHT =
        new ColorUIResource(244, 244, 244);
     private static final ColorUIResource GRAY_LIGHT =
        new ColorUIResource(204, 204, 204);
     private static final ColorUIResource DARK_BLUE_GREEN = 
        new ColorUIResource(82, 132, 163);
     private static final ColorUIResource SKY_BLUE = 
        new ColorUIResource(62, 140, 248);
     private static final ColorUIResource ORANGE = 
        new ColorUIResource(225, 81, 25);

    @Override
    public String getName() {
        return "White Star Blue";
    }

    @Override
    protected ColorUIResource getPrimary1() { // Selection
        return DARK_BLUE_GREEN;
    }
    
    @Override
    protected ColorUIResource getPrimary2() {
        return DARK_BLUE_GREEN;
    }
    
    @Override
    protected ColorUIResource getPrimary3() {  // GRAY_VERY_LIGHT;
        return DARK_BLUE_GREEN;
    }

    @Override
    protected ColorUIResource getSecondary1() {  // 3D Schattenseite
        return GRAY_VERY_LIGHT;
    }
    
    @Override
    protected ColorUIResource getSecondary2() {  // Disabled Border
        return SKY_BLUE;
    }
    
    @Override
    protected ColorUIResource getSecondary3() {  // Window background
        return GRAY_LIGHT; 
    }

    @Override
    public ColorUIResource getFocusColor() {
        return PlasticLookAndFeel.getHighContrastFocusColorsEnabled()
            ? ORANGE
            : DARK_BLUE_GREEN;
    }

    @Override
    public ColorUIResource getTitleTextColor() {
        return DARK_BLUE_GREEN;
    }

    @Override
    public ColorUIResource getSimpleInternalFrameBackground() {
        return GRAY_VERY_LIGHT;
    }
}
