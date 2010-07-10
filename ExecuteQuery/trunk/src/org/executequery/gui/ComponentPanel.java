/*
 * ComponentPanel.java
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

package org.executequery.gui;

import java.awt.Component;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public final class ComponentPanel {

    /** the panels name */
    private String name;

    /** the panel component */
    private Component component;

    /** Creates a new instance of PanelCacheObject */
    public ComponentPanel(String name, Component component) {
        
        this.name = name;
        this.component = component;
    }

    public String getName() {
        
        return name;
    }

    public Component getComponent() {

        return component;
    }

    public void clear() {

        name = null;
        component = null;
    }
    
}






