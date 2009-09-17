/*
 * DatabaseColumn.java
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

package org.executequery.databaseobjects;

import java.util.List;
import java.util.Map;

import org.executequery.databaseobjects.impl.ColumnConstraint;
import org.underworldlabs.jdbc.DataSourceException;

/**
 * Defines a database column. This may be a column as defined by 
 * the meta data and not limited to table columns.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public interface DatabaseColumn extends DatabaseObjectElement {
    
    /**
     * Returns the name of the parent object.
     *
     * @return the parent object's name
     */
    String getParentsName();
    
    /**
     * Returns the data type name of this database column.
     *
     * @return the data type name
     */
    String getTypeName();
    
    /**
     * The type identfier int from java.sql.Type.
     *
     * @return the type int
     */
    int getTypeInt();
    
    /**
     * Returns the size of this database column.
     *
     * @return the size
     */
    int getColumnSize();

    /**
     * Returns the scale of this database column.
     *
     * @return the scale
     */
    int getColumnScale();

    /**
     * Indicates whether this database column is required 
     * (may the value be null).
     *
     * @return true | false
     */
    boolean isRequired();
    
    /**
     * Indicates whether this column is a primary key column.
     *
     * @return true | false
     */
    boolean isPrimaryKey();

    /**
     * Indicates whether this column is a foreign key column.
     *
     * @return true | false
     */
    boolean isForeignKey();
    
    /**
     * Indicates whether this column is unique.
     *
     * @return true | false
     */
    boolean isUnique();
    
    /**
     * Indicates whether this column has any constraints.
     * 
     * @return true | false
     */
    boolean hasConstraints();
    
    /**
     * Returns the default value of this column.
     *
     * @return the column default value
     */
    String getDefaultValue();

    /**
     * Returns the meta data as a map of this column.
     *
     * @return the meta data
     */
    Map<String,String> getMetaData() throws DataSourceException;

    /**
     * Returns a formatted string representation of the
     * column's data type and size - eg. VARCHAR(10).
     *
     * @return the formatted type string
     */
    String getFormattedDataType();

    List<ColumnConstraint> getConstraints();
    
}






