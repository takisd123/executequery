/*
 * DisabledField.java
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

package org.underworldlabs.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

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
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class DisabledField extends JLabel {
    
    protected static Border border;    
    protected static Insets insets;

    /** <p>Creates a new instance with the text to be
     *  displayed as blank.
     */
    public DisabledField() {
        this("");
    }

    /** <p>Creates a new instance with the specified
     *  text to be displayed.
     *
     *  @param the text to display
     */
    public DisabledField(String text) {
        super(text);
        
        if (insets == null || border == null) {
            insets = new Insets(3, 3, 3, 3);
            border = new DisabledBorder(
                        UIManager.getColor("TextField.inactiveForeground"));
        }
        
        setBorder(border);
    }

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

}








