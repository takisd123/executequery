/*
 * TextFieldPanel.java
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

package org.executequery.components;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import org.underworldlabs.swing.TextFieldFocusController;


/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class TextFieldPanel extends JPanel
                            implements TextFieldFocusController {
    
    public TextFieldPanel() {
        super();
    }

    public TextFieldPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }
    
    public TextFieldPanel(LayoutManager layout) {
        super(layout);
    }
    
    public TextFieldPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }
    
}











