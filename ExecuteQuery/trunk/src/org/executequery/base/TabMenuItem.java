/*
 * TabMenuItem.java
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

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class TabMenuItem extends JMenuItem {

    /** the tab component for this menu item */
    private Component tabComponent;

    /** Creates a new instance of TabMenuItem */
    public TabMenuItem(String title, Component component) {
        this(title, null, component);
    }

    /** Creates a new instance of TabMenuItem */
    public TabMenuItem(String title, Icon icon, Component component) {
        super(title, icon);
        this.tabComponent = component;
    }

    public Component getTabComponent() {
        return tabComponent;
    }

    public void setTabComponent(Component component) {
        this.tabComponent = component;
    }
    
}









