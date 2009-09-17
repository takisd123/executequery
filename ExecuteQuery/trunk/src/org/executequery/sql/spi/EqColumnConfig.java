/*
 * EqColumnConfig.java
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

package org.executequery.sql.spi;

import liquibase.change.ColumnConfig;
import liquibase.database.Database;
import liquibase.database.MaxDBDatabase;
import liquibase.database.MySQLDatabase;

class EqColumnConfig extends ColumnConfig {

    public String getDefaultColumnValue(Database database) {

        String columnDefaultValue = getDefaultValue();

        if (columnDefaultValue != null) {
            
            if ("null".equalsIgnoreCase(columnDefaultValue)) {

                return "NULL";
            }

            if (shouldQuoteDefaultValue(database)) {
                
                if (!database.shouldQuoteValue(columnDefaultValue)) {

                    return columnDefaultValue;

                } else {

                    return "'" + columnDefaultValue.replaceAll("'", "''") + "'";
                }

            }

            return columnDefaultValue;
        }

        return super.getDefaultColumnValue(database);
    }

    private boolean shouldQuoteDefaultValue(Database database) {

        return database instanceof MySQLDatabase
            || database instanceof MaxDBDatabase;
    }

}





