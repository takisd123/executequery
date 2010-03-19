/*
 * DriverPanel.java
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

package org.executequery.gui.drivers;

import org.executequery.databasemediators.DatabaseDriver;

public interface DriverPanel {

    String TITLE = "Drivers";

    String FRAME_ICON = "DatabaseDrivers16.gif";

    boolean saveDrivers();
    
    void driverNameChanged();
    
    void setDriver(DatabaseDriver databaseDriver);
    
    DatabaseDriver getDriver();
    
}
