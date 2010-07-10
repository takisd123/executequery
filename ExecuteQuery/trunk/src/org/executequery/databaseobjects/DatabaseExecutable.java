/*
 * DatabaseExecutable.java
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

package org.executequery.databaseobjects;

import java.util.List;
import org.underworldlabs.jdbc.DataSourceException;

/**
 * Defines a database executable object such as a function or
 * stored database procedure.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public interface DatabaseExecutable extends DatabaseObject {
    
    /**
     * The executable (procedure) type.
     *
     * @return the proc type
     */
    short getExecutableType();

    /**
     * Indicates whether this executable object has any parameters.
     *
     * @return true | false
     */
    boolean hasParameters();
   
    /**
     * Adds the specified values as a single parameter to this object.
     */
    void addParameter(String name, int type, int dataType, String sqlType, int size);
    
    /**
     * Returns this object's parameters.
     */
    List<ProcedureParameter> getParameters() throws DataSourceException;

    /**
     * Returns this object's parameters as an array.
     */
    ProcedureParameter[] getParametersArray() throws DataSourceException;
    
}







