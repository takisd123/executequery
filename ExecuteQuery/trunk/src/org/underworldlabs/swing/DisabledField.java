/*
 * DisabledField.java
 *
 * Copyright (C) 2002-2010 Takis Diakoumis
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

package org.underworldlabs.swing;

import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.UIManager;

/** 
 * A convenience class providing a simple component
 * to display text within a rectangle achieving the same
 * effect as displayed when disabling a <code>JTextField</code>
 * under the Metal L&F. This will provide a common 'disabled'
 * component across all L&Fs that remains lightweight and does
 * not feature or require those methods as would be available
 * using a <code>JTextField</code>.
 *
 * <p>Some limitations have been deliberately introduced. The
 * component height will always be 19px. The width is determined
 * as specified or by the layout manager. In any case, This
 * component's size should not require any modification given its
 * limited design and purpose.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class DisabledField extends JTextField {
    
    private static final int HEIGHT = 24;
    
//    protected static Border border;
    protected static Insets insets;

    public DisabledField() {
        this("");
    }

    public DisabledField(String text) {

        super(text);
        
        if (insets == null) {// || border == null) {
            insets = new Insets(4, 3, 4, 3);
//            border = new DisabledBorder(
//                        UIManager.getColor("TextField.inactiveForeground"));
        }
        
        setMargin(insets);
//        setBorder(border);

        setBackground(UIManager.getColor("Label.background"));
        setForeground(UIManager.getColor("Label.foreground"));
    }

    @Override
    public Insets getInsets() {

        return insets;
    }
    
    @Override
    public boolean isEditable() {

        return false;
    }
    
    @Override
    public int getHeight() {
     
        return HEIGHT;
    }
    
/*
    class DisabledBorder extends LineBorder {

        public DisabledBorder(Color color) {
            super(color, 1, false);
        }

        public Insets getBorderInsets() {
            return DisabledField.insets;
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            return DisabledField.insets;
        }

    } // class DisabledBorder
*/

}

