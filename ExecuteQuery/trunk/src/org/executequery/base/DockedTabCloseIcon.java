/*
 * DockedTabCloseIcon.java
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

package org.executequery.base;

import java.awt.Component;
import java.awt.Graphics;

/**
 * Simple icon drawing the close button for a closeable tab on the CloseTabbedPane.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class DockedTabCloseIcon implements TabControlIcon {
    
    /** Creates a new instance of TabCloseButtonIcon */
    public DockedTabCloseIcon() {}
    
    /**
     * Returns the icon's height.
     * 
     * @return the height of the icon
     */
    public int getIconHeight() {
        return ICON_HEIGHT;
    }

    /**
     * Returns the icon's width.
     * 
     * @return the width of the icon
     */
    public int getIconWidth() {
        return ICON_WIDTH;
    }

    /**
     * Draw the icon at the specified location.
     *
     * @param the component
     * @param the graphics context
     * @param x coordinate
     * @param y coordinate
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(ICON_COLOR);
        g.drawLine(x, y, x + ICON_WIDTH - 1, y + ICON_HEIGHT - 1);
        g.drawLine(x + ICON_WIDTH - 1, y, x, y + ICON_HEIGHT - 1);
    }
    
}
