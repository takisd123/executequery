package org.executequery.sql.spi;

import liquibase.database.core.HsqlDatabase;

public class ShiftyHsqlDatabase extends HsqlDatabase {

    public String escapeColumnName(String schemaName, String tableName, String columnName) {
        if (columnName.contains("(")) { // from liquibase.database.core.HsqlDatabase
            return columnName;
        }
        return "\"" + columnName + "\"";
    }
    
}
