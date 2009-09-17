/*
 * ViewMenuCheckBoxMenuItem.java
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

package org.executequery.gui.menu;

import javax.swing.JCheckBoxMenuItem;

public class ViewMenuCheckBoxMenuItem extends JCheckBoxMenuItem {

    private String propertyKey;

    public String getPropertyKey() {

        return propertyKey;
    }
    
    public void setPropertyKey(String propertyKey) {

        this.propertyKey = propertyKey;
    }
    
}





