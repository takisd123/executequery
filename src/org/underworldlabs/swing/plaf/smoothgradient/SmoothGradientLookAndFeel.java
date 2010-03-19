/*
 * SmoothGradientLookAndFeel.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.underworldlabs.swing.plaf.smoothgradient;

import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import org.underworldlabs.swing.plaf.UIUtils;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class SmoothGradientLookAndFeel extends MetalLookAndFeel {
    
    public static final Color FRAME_BUTTON_START_ACTIVE = new Color(157,157,225);
    public static final Color FRAME_BUTTON_STOP_ACTIVE = new Color(185,185,244);
    public static final Color FRAME_BUTTON_START_INACTIVE = new Color(223, 223, 223, 223);
    public static final Color FRAME_BUTTON_STOP_INACTIVE = new Color(243, 243, 243, 243);
    
    public static final Color DARKEN_START = new Color(0, 0, 0, 0);
    public static final Color DARKEN_STOP = new Color(0, 0, 0, 64);
    public static final Color LT_DARKEN_STOP = new Color(0, 0, 0, 32);
    public static final Color BRIGHTEN_START = new Color(255, 255, 255, 0);
    public static final Color BRIGHTEN_STOP = new Color(255, 255, 255, 128);
    public static final Color LT_BRIGHTEN_STOP = new Color(255, 255, 255, 64);
    
    /** Client property key to disable the pseudo 3D effect. */
    public static final String IS_3D_KEY = "Plastic.is3D";
    
    /** The current color theme. */
    private static MetalTheme metalTheme;
    
    /** The look-global state for the 3D enabledment. */
    private static boolean	 is3DEnabled = true;
    
    /** The look dependent <code>FontSizeHints</code> */
    private static FontSizeHints fontSizeHints;
    
    /** The modified darker highlight for internal frame bumps */
    private static Color internalFrameBumpsHighlight = new Color(198,198,246);
    
    public SmoothGradientLookAndFeel() {}
    
    public String getID() {
        return "SmoothGradient";
    }
    
    public String getName() {
        return "Smooth Gradient Look and Feel";
    }
    
    public String getDescription() {
        return "The Execute Query Smooth Gradient Look and Feel - modified from " +
                "The JGoodies Plastic Look and Feel";
    }
    
    // Special Properties ***************************************************
    
    /**
     * Answers the current <code>FontSizeHints</code>,
     * where look specific settings shadow the global users defaults
     * as stored under key <code>FontSizeHints.KEY</code>.
     *
     * @see Options#setGlobalFontSizeHints
     * @see FontSizeHints
     */
    public static FontSizeHints getFontSizeHints() {
        return fontSizeHints != null
                ? fontSizeHints
                : Options.getGlobalFontSizeHints();
    }
    
    /**
     * Sets <code>FontSizeHints</code> that shadow the global font size hints.
     *
     * @see Options#setGlobalFontSizeHints
     * @see FontSizeHints
     */
    public static void setFontSizeHints(FontSizeHints newHints) {
        fontSizeHints = newHints;
    }
    
    protected boolean is3DEnabled() {
        return is3DEnabled;
    }
    
    public static void set3DEnabled(boolean b) {
        is3DEnabled = b;
    }
    
    // Overriding Superclass Behavior ***************************************
    
    /**
     * Initializes the class defaults, that is, overrides some UI delegates
     * with JGoodies Plastic implementations.
     *
     * @see javax.swing.plaf.basic.BasicLookAndFeel#getDefaults
     */
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        
        String NAME_PREFIX = "org.underworldlabs.swing.plaf.smoothgradient.SmoothGradient";
        
        // Overwrite some of the uiDefaults.
        Object[] uiDefaults = {
            "ButtonUI",	NAME_PREFIX + "ButtonUI",
            //"ToggleButtonUI",	NAME_PREFIX + "ToggleButtonUI",
            "ComboBoxUI", NAME_PREFIX + "ComboBoxUI",
            "ScrollBarUI", NAME_PREFIX + "ScrollBarUI",
            //			"SpinnerUI", NAME_PREFIX + "SpinnerUI",
//            "TreeUI", NAME_PREFIX + "TreeUI",
//            "RootPaneUI", NAME_PREFIX + "RootPaneUI",
            "InternalFrameUI", NAME_PREFIX + "InternalFrameUI",

        };
        
        table.putDefaults(uiDefaults);
        
    }
    
    
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        
//        Icon checkBoxMenuItemIcon		= SmoothGradientIconFactory.getCheckBoxMenuItemIcon();
        
        // 	Should be active.
        int treeFontSize = table.getFont("Tree.font").getSize();
        Integer rowHeight = Integer.valueOf(treeFontSize + 6);
        Object treeExpandedIcon = SmoothGradientIconFactory.getExpandedTreeIcon();
        Object treeCollapsedIcon = SmoothGradientIconFactory.getCollapsedTreeIcon();
        ColorUIResource gray = new ColorUIResource(
                UIUtils.getBrighter(Color.GRAY,0.8));
        
        final Object[] internalFrameIconArgs = new Object[1];
        internalFrameIconArgs[0] = Integer.valueOf(16);
        
        Boolean is3D = Boolean.TRUE;
        
        Object[] defaults = {
            //"CheckBoxMenuItem.checkIcon",	checkBoxMenuItemIcon,
            "ComboBox.selectionForeground",	getMenuSelectedForeground(),
            "ComboBox.selectionBackground",	getMenuSelectedBackground(),
            "ComboBox.arrowButtonBorder",   SmoothGradientBorders.getComboBoxArrowButtonBorder(),
            "ComboBox.editorBorder",        SmoothGradientBorders.getComboBoxEditorBorder(),
            "Menu.arrowIcon",				SmoothGradientIconFactory.getMenuArrowIcon(),
            /*
            "FileView.computerIcon",		makeIcon(getClass(), "icons/Computer.gif"),
            "FileView.directoryIcon",		makeIcon(getClass(), "icons/TreeClosed.gif"),
            "FileView.fileIcon", 			makeIcon(getClass(), "icons/File.gif"),
            "FileView.floppyDriveIcon", 	makeIcon(getClass(), "icons/FloppyDrive.gif"),
            "FileView.hardDriveIcon", 		makeIcon(getClass(), "icons/HardDrive.gif"),
            "FileChooser.homeFolderIcon", 	makeIcon(getClass(), "icons/HomeFolder.gif"),
            "FileChooser.newFolderIcon", 	makeIcon(getClass(), "icons/NewFolder.gif"),
            "FileChooser.upFolderIcon",		makeIcon(getClass(), "icons/UpFolder.gif"),
             */
//            "Tree.closedIcon", 				makeIcon(getClass(), "icons/TreeClosed.gif"),
//            "Tree.openIcon", 				makeIcon(getClass(), "icons/TreeOpen.gif"),
//            "Tree.leafIcon", 				makeIcon(getClass(), "icons/TreeLeaf.gif"),
            "Tree.expandedIcon", 			treeExpandedIcon,
            "Tree.collapsedIcon", 			treeCollapsedIcon,
            "Tree.line",					gray,
            "Tree.hash",					gray,
            "Tree.rowHeight",				rowHeight,
            "InternalFrame.iconifyIcon", SmoothGradientIconFactory.getInternalFrameMinimizeIcon(16),
            "InternalFrame.maximizeIcon", SmoothGradientIconFactory.getInternalFrameMaximizeIcon(16),
            "InternalFrame.minimizeIcon", SmoothGradientIconFactory.getInternalFrameAltMaximizeIcon(16),
            "InternalFrame.closeIcon", SmoothGradientIconFactory.getInternalFrameCloseIcon(16),

            "Button.is3DEnabled",			is3D,
            "ComboBox.is3DEnabled",			is3D,
            "ScrollBar.is3DEnabled",		is3D,
            "ToggleButton.is3DEnabled",		is3D,
                    //      PolishedScrollBarUI.MAX_BUMPS_WIDTH_KEY, Integer.valueOf(22),
                    
            "InternalFrame.optionDialogBorder", SmoothGradientBorders.getOptionDialogBorder(),
            "InternalFrame.border", SmoothGradientBorders.getInternalFrameBorder()
            /*
            "RootPane.plainDialogBorder", SmoothGradientBorders.getDialogBorder(),
            "RootPane.informationDialogBorder", SmoothGradientBorders.getDialogBorder(),
            "RootPane.errorDialogBorder", SmoothGradientBorders.getErrorDialogBorder(),
            "RootPane.colorChooserDialogBorder", SmoothGradientBorders.getQuestionDialogBorder(),
            "RootPane.fileChooserDialogBorder", SmoothGradientBorders.getQuestionDialogBorder(),
            "RootPane.questionDialogBorder", SmoothGradientBorders.getQuestionDialogBorder(),
            "RootPane.warningDialogBorder", SmoothGradientBorders.getWarningDialogBorder()
             */

        };
        
        table.putDefaults(defaults);
        
    }
    
    /**
     * Gets the current <code>PlasticTheme</code>.
     */
    public static MetalTheme getCurrentTheme() {
        return metalTheme;
    }
    
    
    // Accessed by ProxyLazyValues ******************************************
    /*
    public static BorderUIResource getInternalFrameBorder() {
        return new BorderUIResource(PlasticBorders.getInternalFrameBorder());
    }
     
    public static BorderUIResource getPaletteBorder() {
        return new BorderUIResource(PlasticBorders.getPaletteBorder());
    }
     */
    
    public static Color getInternalFrameBumpsHighlight() {
        return internalFrameBumpsHighlight;
    }
    
    
    // Accessing Theme Colors and Fonts *************************************
/*
 
    public static ColorUIResource getPrimaryControlDarkShadow() {
        return getMyCurrentTheme().getPrimaryControlDarkShadow();
    }
 
    public static ColorUIResource getPrimaryControlHighlight() {
        return getMyCurrentTheme().getPrimaryControlHighlight();
    }
 
    public static ColorUIResource getPrimaryControlInfo() {
        return getMyCurrentTheme().getPrimaryControlInfo();
    }
 
    public static ColorUIResource getPrimaryControlShadow() {
        return getMyCurrentTheme().getPrimaryControlShadow();
    }
 
    public static ColorUIResource getPrimaryControl() {
        return getMyCurrentTheme().getPrimaryControl();
    }
 
    public static ColorUIResource getControlHighlight() {
        return getMyCurrentTheme().getControlHighlight();
    }
 
    public static ColorUIResource getControlDarkShadow() {
        return getMyCurrentTheme().getControlDarkShadow();
    }
 
    public static ColorUIResource getControl() {
        return getMyCurrentTheme().getControl();
    }
 
    public static ColorUIResource getFocusColor() {
        return getMyCurrentTheme().getFocusColor();
    }
 
    public static ColorUIResource getMenuItemBackground() {
        return getMyCurrentTheme().getMenuItemBackground();
    }
 
    public static ColorUIResource getMenuItemSelectedBackground() {
        return getMyCurrentTheme().getMenuItemSelectedBackground();
    }
 
    public static ColorUIResource getMenuItemSelectedForeground() {
        return getMyCurrentTheme().getMenuItemSelectedForeground();
    }
 
    public static ColorUIResource getWindowTitleBackground() {
        return getMyCurrentTheme().getWindowTitleBackground();
    }
 
    public static ColorUIResource getWindowTitleForeground() {
        return getMyCurrentTheme().getWindowTitleForeground();
    }
 
    public static ColorUIResource getWindowTitleInactiveBackground() {
        return getMyCurrentTheme().getWindowTitleInactiveBackground();
    }
 
    public static ColorUIResource getWindowTitleInactiveForeground() {
        return getMyCurrentTheme().getWindowTitleInactiveForeground();
    }
 
    public static ColorUIResource getSimpleInternalFrameForeground() {
        return getMyCurrentTheme().getSimpleInternalFrameForeground();
    }
 
    public static ColorUIResource getSimpleInternalFrameBackground() {
        return getMyCurrentTheme().getSimpleInternalFrameBackground();
    }
 
    public static ColorUIResource getTitleTextColor() {
        return getMyCurrentTheme().getTitleTextColor();
    }
 
    public static FontUIResource getTitleTextFont() {
        return getMyCurrentTheme().getTitleTextFont();
    }*/
    
}

