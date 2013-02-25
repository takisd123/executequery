/*
 * LookAndFeelLoader.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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

package org.executequery.util;

import java.awt.event.KeyEvent;
import java.util.Map.Entry;

import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.DefaultEditorKit;

import org.executequery.ApplicationException;
import org.executequery.Constants;
import org.executequery.plaf.ExecuteQueryTheme;
import org.underworldlabs.swing.plaf.UIUtils;
import org.underworldlabs.swing.plaf.base.CustomTextAreaUI;
import org.underworldlabs.swing.plaf.base.CustomTextPaneUI;
import org.underworldlabs.swing.plaf.bumpygradient.BumpyGradientLookAndFeel;

public final class LookAndFeelLoader {

    public int loadLookAndFeel(int lookAndFeel) {

        try {

            switch (lookAndFeel) {
                case Constants.EQ_DEFAULT_LAF:
                    loadDefaultLookAndFeel();
                    break;
                case Constants.SMOOTH_GRADIENT_LAF:
                    loadDefaultLookAndFeel();
                    break;
                case Constants.BUMPY_GRADIENT_LAF:
                    BumpyGradientLookAndFeel
                            .setCurrentTheme(new ExecuteQueryTheme());
                    UIManager.setLookAndFeel(new BumpyGradientLookAndFeel());
                    break;
                case Constants.EQ_THM:
                    loadDefaultLookAndFeelTheme();
                    break;
                case Constants.METAL_LAF:
                    loadDefaultMetalLookAndFeelTheme();
                    break;
                case Constants.OCEAN_LAF:
                    UIManager.setLookAndFeel(
                            "javax.swing.plaf.metal.MetalLookAndFeel");
                    break;
                case Constants.MOTIF_LAF:
                    UIManager.setLookAndFeel(
                            "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    break;
                case Constants.WIN_LAF:
                    UIManager.setLookAndFeel(
                            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    break;
                case Constants.GTK_LAF:
                    UIManager.setLookAndFeel(
                            "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    break;
                case Constants.PLUGIN_LAF:
                    loadCustomLookAndFeel();
                    break;
                case Constants.NATIVE_LAF:
                    loadNativeLookAndFeel();
                    break;
                default:
                    loadDefaultLookAndFeel();
                    break;
            }

        } catch (UnsupportedLookAndFeelException e) {

            throw new ApplicationException(e);

        } catch (ClassNotFoundException e) {

            throw new ApplicationException(e);

        } catch (InstantiationException e) {

            throw new ApplicationException(e);

        } catch (IllegalAccessException e) {

            throw new ApplicationException(e);
        
        } 
     
        if (!UIUtils.isNativeMacLookAndFeel()) {

            CustomTextAreaUI.initialize();
            CustomTextPaneUI.initialize();            	
        }
        
        applyMacSettings();        
        return lookAndFeel;
    }

    private void applyMacSettings() {

        if (UIUtils.isMac()) {

            String[] textComponents = {"TextField", "TextPane", "TextArea", "EditorPane", "PasswordField"};
            for (String textComponent : textComponents) {
                
                InputMap im = (InputMap) UIManager.get(textComponent + ".focusInputMap");
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
            }
            
            if (UIUtils.isNativeMacLookAndFeel()) {
            	
            	UIManager.put("Table.gridColor", UIUtils.getDefaultBorderColour());
            }

        }
        
        
    }

    private void loadCustomLookAndFeel() {

        PluginLookAndFeelManager pluginManager = new PluginLookAndFeelManager();

        try {
            pluginManager.loadLookAndFeel();

        } catch (Exception e) {

            throw new ApplicationException(e);
        }

        if (!pluginManager.isInstalled()) {

            loadDefaultLookAndFeel();
        }

    }

    /**
     * Sets the default metal look and feel theme on Metal.
     */
    private void loadDefaultMetalLookAndFeelTheme() {

        try {

            MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());

        } catch (UnsupportedLookAndFeelException e) {

            throw new ApplicationException(e);
        }

    }

    /**
     * Sets the default look and feel theme on Metal.
     */
    private void loadDefaultLookAndFeelTheme() {

        try {

            MetalLookAndFeel.setCurrentTheme(new ExecuteQueryTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());

        } catch (UnsupportedLookAndFeelException e) {

            throw new ApplicationException(e);
        }

    }

    /**
     * Sets the default 'Execute Query' look and feel.
     */
    public void loadDefaultLookAndFeel() {

        try {
            org.underworldlabs.swing.plaf.UnderworldLabsLookAndFeel metal =
                    new org.underworldlabs.swing.plaf.UnderworldLabsLookAndFeel();

            UIManager.setLookAndFeel(metal);

        } catch (UnsupportedLookAndFeelException e) {

            throw new ApplicationException(e);
        }

    }

    public void loadNativeLookAndFeel() {
        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {

            throw new ApplicationException(e);
        }
    }

    public void loadCrossPlatformLookAndFeel() {
        try {

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        } catch (Exception e) {

            throw new ApplicationException(e);
        }
    }

    public void decorateDialogsAndFrames(boolean decorateDialogs, boolean decorateFrames) {

        JDialog.setDefaultLookAndFeelDecorated(decorateDialogs);
        JFrame.setDefaultLookAndFeelDecorated(decorateFrames);
    }

}









