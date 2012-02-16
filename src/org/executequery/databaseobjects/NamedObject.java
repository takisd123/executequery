/*
 * NamedObject.java
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
 * Defines a database named object.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public interface NamedObject extends java.io.Serializable {

    public static final int ROOT = 96;
    public static final int CATALOG = 98;
    public static final int HOST = 99;
    public static final int SCHEMA = 97;

    public static final int OTHER = 95;
    
    public static final int TABLE_COLUMN = 94;

    public static final int META_TAG = 93;

    
    public static final int FUNCTION = 0;
    public static final int INDEX = 1;
    public static final int PROCEDURE = 2;
    public static final int SEQUENCE = 3;
    public static final int SYNONYM = 4;
    public static final int SYSTEM_TABLE = 5;
    public static final int TABLE = 6;
    public static final int TRIGGER = 7;
    public static final int VIEW = 8;

    public static final int SYSTEM_VIEW = 13;
    
    public static final int SYSTEM_FUNCTION = 9;
    
    public static final int SYSTEM_STRING_FUNCTIONS = 10;
    
    public static final int SYSTEM_NUMERIC_FUNCTIONS = 11;
    
    public static final int SYSTEM_DATE_TIME_FUNCTIONS = 12;
    
    
    public static final String[] META_TYPES = {"FUNCTION",
                                               "INDEX",
                                               "PROCEDURE",
                                               "SEQUENCE",
                                               "SYNONYM",
                                               "SYSTEM TABLE",
                                               "TABLE",
                                               "TRIGGER",
                                               "VIEW",
                                               "SYSTEM FUNCTIONS",
                                               "SYSTEM_STRING_FUNCTIONS",
                                               "SYSTEM_NUMERIC_FUNCTIONS",
                                               "SYSTEM_DATE_TIME_FUNCTIONS",
                                               "SYSTEM VIEW"};

    /**
     * Marks this object as being 'reset', where for any loaded object
     * these are cleared and a fresh database call would be made where 
     * appropriate.
     */
    void reset();
    
    /**
     * Returns the database object type.
     *
     * @return the object type
     */
    int getType();

    /**
     * Returns the name of this object.
     *
     * @return the object name
     */
    String getName();

    /**
     * Sets the name of this database object as specified.
     *
     * @param name the name of this database object
     */
    void setName(String name);
    
    /**
     * Returns the display name of this object.
     *
     * @return the display name
     */
    String getShortName();
    
    /**
     * Returns the meta data key name of this object.
     *
     * @return the meta data key name.
     */
    String getMetaDataKey();
    
    /**
     * Retrieves child database objects of this named object.
     * Depending on the type of named object - this may return null.
     *
     * @return this meta tag's child database objects.
     */
    List<NamedObject> getObjects() throws DataSourceException;
    
    /**
     * Returns the parent named object of this object.
     *
     * @return the parent object or null if we are at the top of the hierarchy
     */
    NamedObject getParent();
    
    /**
     * Sets the parent object to that specified.
     *
     * @param the parent named object
     */
    void setParent(NamedObject parent);

    /**
     * Drops this named object in the database.
     *
     * @return drop statement result
     */
    int drop() throws DataSourceException;
    
    String getDescription();
    
}

