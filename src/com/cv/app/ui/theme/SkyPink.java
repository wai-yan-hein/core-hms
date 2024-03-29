/*
 * Copyright (c) 2001-2015 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cv.app.ui.theme;

import com.jgoodies.looks.plastic.PlasticScrollBarUI;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/**
 * A theme with pink foreground and a light gray window background color.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.8 $
 */
public class SkyPink extends AbstractSkyTheme {

    @Override
    public String getName() {
        return "Sky Pink";
    }

    @Override
    protected ColorUIResource getPrimary1() {
        return Colors.PINK_LOW_DARK;
    }

    @Override
    protected ColorUIResource getPrimary2() {
        return Colors.PINK_LOW_MEDIUM;
    }

    @Override
    protected ColorUIResource getPrimary3() {
        return Colors.PINK_LOW_LIGHTER;
    }

    @Override
    public ColorUIResource getHighlightedTextColor() {
        return getControlTextColor();
    }

    @Override
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Object[] uiDefaults =
            { PlasticScrollBarUI.MAX_BUMPS_WIDTH_KEY, 30, };
        table.putDefaults(uiDefaults);
    }

}
