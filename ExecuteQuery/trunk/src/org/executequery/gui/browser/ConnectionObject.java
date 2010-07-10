/*
 * ConnectionObject.java
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

package org.executequery.gui.browser;

import org.executequery.databasemediators.DatabaseConnection;

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
public class ConnectionObject extends BaseDatabaseObject {
    
    /** the meta key names for this connection */
    private String[] metaKeyNames;
    
    /** whether catalogs are available for this connection */
    private boolean catalogsInUse;
    
    /** the database connection properties object */
    private DatabaseConnection databaseConnection;

    /** Creates a new instance of HostMetaObject */
    public ConnectionObject(DatabaseConnection databaseConnection) {
        super(BrowserConstants.HOST_NODE, databaseConnection.getName());
        this.databaseConnection = databaseConnection;
    }

    public boolean hasMetaKeys() {
        return metaKeyNames != null && metaKeyNames.length > 0;
    }

    public String[] getMetaKeyNames() {
        return metaKeyNames;
    }

    public void setMetaKeyNames(String[] metaKeyNames) {
        this.metaKeyNames = metaKeyNames;
    }

    public boolean isConnected() {
        if (databaseConnection != null) {
            return databaseConnection.isConnected();
        }
        return false;
    }
    
    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /*
    public MetaDataValues getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaDataValues metaData) {
        this.metaData = metaData;
    }
    */

    public boolean isCatalogsInUse() {
        return catalogsInUse;
    }

    public void setCatalogsInUse(boolean catalogsInUse) {
        this.catalogsInUse = catalogsInUse;
    }
    
    public String getName() {
        return databaseConnection.getName();
    }
    
    public String toString() {
        return getName();
    }
    
}










