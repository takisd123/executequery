/*
 * DefaultConnectionListener.java
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

package org.executequery.listeners;

import org.executequery.GUIUtilities;
import org.executequery.components.StatusBarPanel;
import org.executequery.datasource.ConnectionManager;
import org.executequery.event.ApplicationEvent;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;

public class DefaultConnectionListener implements ConnectionListener {

    public void connected(ConnectionEvent connectionEvent) {
        
        updateStatusBarDataSourceCounter();
    }

    public void disconnected(ConnectionEvent connectionEvent) {

        updateStatusBarDataSourceCounter();
    }

    public boolean canHandleEvent(ApplicationEvent event) {

        return (event instanceof ConnectionEvent);
    }

    private void updateStatusBarDataSourceCounter() {

        statusBar().setFirstLabelText(
                " Active Data Sources: " + 
                ConnectionManager.getActiveConnectionPoolCount());
    }

    private StatusBarPanel statusBar() {

        return GUIUtilities.getStatusBar();
    }

}
