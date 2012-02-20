/*
 * QueryTypes.java
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

package org.executequery.databasemediators;

public final class QueryTypes {

    public static final int ALL_UPDATES = 80;
    
    /** An SQL INSERT statement */
    public static final int INSERT = 80;

    /** An SQL UPDATE statement */
    public static final int UPDATE = 81;
    
    /** An SQL DELETE statement */
    public static final int DELETE = 82;
    
    /** An SQL SELECT statement */
    public static final int SELECT = 10;
    
    /** A DESCRIBE statement - table meta data */
    public static final int DESCRIBE = 16;
    
    /** An SQL EXPLAIN statement */
    public static final int EXPLAIN = 15;
    
    /** An SQL EXECUTE statement (procedure) */
    public static final int EXECUTE = 11;
    
    /** An SQL DROP TABLE statement */
    public static final int DROP_TABLE = 20;
    
    /** An SQL CREATE TABLE statement */
    public static final int CREATE_TABLE = 21;
    
    /** An SQL ALTER TABLE statement */
    public static final int ALTER_TABLE = 22;
    
    /** An SQL CREATE SEQUENCE statement */
    public static final int CREATE_SEQUENCE = 23;
    
    /** An SQL CREATE FUNCTION statement */
    public static final int CREATE_FUNCTION = 26;
    
    /** An SQL CREATE PROCEDURE statement */
    public static final int CREATE_PROCEDURE = 25;
    
    /** An SQL GRANT statement */
    public static final int GRANT = 27;
    
    /** An SQL GRANT statement */
    public static final int CREATE_SYNONYM = 28;
    
    /** An unknown SQL statement */
    public static final int UNKNOWN = 99;
    
    /** A commit statement */
    public static final int COMMIT = 12;
    
    /** A rollback statement */
    public static final int ROLLBACK = 13;
    
    /** A connect statement */
    public static final int CONNECT = 14;

    private QueryTypes() {}

}



