/*
 * TabControlIcon.java
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

package org.executequery.base;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public interface TabControlIcon extends Icon {
    
    /** the icon width */
    public static final int ICON_WIDTH = 7;
    
    /** the icon height */
    public static final int ICON_HEIGHT = 7;

    /** The icon image colour */
    public static final Color ICON_COLOR = 
            UIManager.getColor("controlShadow").darker().darker();
    
}


