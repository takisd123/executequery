package org.executequery.databaseobjects.impl;

import java.sql.Types;

public class ColumnInformationFactory {

    public ColumnInformation build(String tableName, String columnName, String typeName, 
            int typeInt, int size, int scale, boolean isNotNullable) {
        
        String typeString = typeName == null ? "" : typeName;

        StringBuilder buffer = new StringBuilder();

        buffer.append(columnName);
        buffer.append(" ");
        buffer.append(typeString);

        // if the type doesn't end with a digit or it
        // is a char type then add the size - attempt
        // here to avoid int4, int8 etc. type values

        if (!typeString.matches("\\b\\D+\\d+\\b") ||
                (typeInt == Types.CHAR ||
                 typeInt == Types.VARCHAR ||
                 typeInt == Types.LONGVARCHAR)) {

            if (size > 0 && !isDateDataType(typeInt) 
                                    && !isNonPrecisionType(typeInt)) {

                buffer.append("(");
                buffer.append(size);

                if (scale > 0) {

                    buffer.append(",");
                    buffer.append(scale);
                }
                buffer.append(")");
            }

        }

        if (isNotNullable) {
            
            buffer.append(" NOT NULL");
        }

        buffer.append("  [");
        buffer.append(tableName);
        buffer.append("]");

        return new ColumnInformation(columnName, buffer.toString());
    }
    
    private boolean isNonPrecisionType(int typeInt) {

        return typeInt == Types.BIT;
    }

    private boolean isDateDataType(int typeInt) {

        return typeInt == Types.DATE ||
                typeInt == Types.TIME ||
                typeInt == Types.TIMESTAMP;
    }

    
}
