/*
 * SQLStateCode.java
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

package org.executequery.gui.sqlstates;

/**
 * SQL State Code definition.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class SQLStateCode {
    
    private String sqlStateClass;
    private String sqlStateSubClass;
    private String description;
    
    /** Creates a new instance of SQLStateCode */
    public SQLStateCode(String sqlStateClass, 
                        String sqlStateSubClass, 
                        String description) {
        this.sqlStateClass = sqlStateClass;
        this.sqlStateSubClass = sqlStateSubClass;
        this.description = description;
    }

    public String getSqlStateClass() {
        return sqlStateClass;
    }

    public String getSqlStateSubClass() {
        return sqlStateSubClass;
    }

    public String getDescription() {
        return description;
    }
    
}






