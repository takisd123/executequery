/*
 * EqColumnConfig.java
 *
 * Copyright (C) 2002-2012 Takis Diakoumis
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
import liquibase.database.core.MaxDBDatabase;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.structure.type.DataType;
import liquibase.database.typeconversion.TypeConverterFactory;

class EqColumnConfig extends ColumnConfig {

    private final Database database;
    private final String typeName;

    public EqColumnConfig(String typeName, Database database) {
        this.typeName = typeName;
        this.database = database;
    }

    @Override
    public String getType() {
     
        DataType dataType = TypeConverterFactory.getInstance().findTypeConverter(database).getDataType(typeName, isAutoIncrement());
        if (dataType.getMaxParameters() < 1) {

            return dataType.getDataTypeName();
        }
        
        return super.getType();
    }
    
    public String getDefaultValue() {

        String columnDefaultValue = super.getDefaultValue();
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

        return super.getDefaultValue();
    }

    private boolean shouldQuoteDefaultValue(Database database) {

        return database instanceof MySQLDatabase
            || database instanceof MaxDBDatabase;
    }

}

