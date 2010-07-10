/*
 * ColumnConstraint.java
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

import java.io.Serializable;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>This class defines a table column constraint.
 *  The constraint may be either a primay or foreign key. */
/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ColumnConstraint implements Serializable {
    
    static final long serialVersionUID = 6696138923435851646L;
    
    /** The name of this constraint */
    private String name;
    
    /** The schema of this constraint */
    private String schema;
    
    /** The table name of this constraint */
    private String table;
    
    /** The column name of this constraint */
    private String column;
    
    /** The referenced table of this constraint */
    private String refTable;
    
    /** The referenced column of this constraint */
    private String refColumn;
    
    /** The type of constraint */
    private int type;
    
    /** Whether this constraint is new (for editing a table definition) */
    private boolean newConstraint;

    /** Whether this constraint is marked to be dropped */
    private boolean markedDeleted;
    
    public static final int PRIMARY_KEY = 0;
    public static final int FOREIGN_KEY = 1;
    public static final int UNIQUE_KEY = 2;
    
    public static final String EMPTY = "";
    public static final String PRIMARY = "PRIMARY";
    public static final String FOREIGN = "FOREIGN";
    public static final String UNIQUE = "UNIQUE";
    
    public ColumnConstraint() {
        newConstraint = false;
    }
    
    public ColumnConstraint(boolean newConstraint) {
        this.newConstraint = newConstraint;
        type = -1;
        if (newConstraint) {
            name = EMPTY;
            schema = EMPTY;
            refTable = EMPTY;
            column = EMPTY;
            refColumn = EMPTY;
        }
    }
    
    public boolean isForeignKey() {
        return type == FOREIGN_KEY;
    }
    
    public boolean isPrimaryKey() {
        return type == PRIMARY_KEY;
    }
    
    public boolean isNewConstraint() {
        return newConstraint;
    }
    
    public void setNewConstraint(boolean newConstraint) {
        this.newConstraint = newConstraint;
    }
    
    public boolean hasSchema() {
        return schema != null && schema.length() > 0;
    }
    
    public void setValues(ColumnConstraint cc) {
        name = cc.getName();
        schema = cc.getRefSchema();
        table = cc.getTable();
        refTable = cc.getRefTable();
        column = cc.getColumn();
        refColumn = cc.getRefColumn();
        type = cc.getType();
    }
    
    public String getTypeName() {
        switch (type) {
            case 0:
                return PRIMARY;
            case 1:
                return FOREIGN;
            case 2:
                return UNIQUE;
            default:
                return null;
        }
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    public void setRefColumn(String refColumn) {
        this.refColumn = refColumn;
    }
    
    public String getRefColumn() {
        return refColumn;
    }
    
    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }
    
    public String getRefTable() {
        return refTable;
    }
    
    public void setTable(String table) {
        this.table = table;
    }
    
    public String getTable() {
        return table;
    }
    
    public void setRefSchema(String schema) {
        this.schema = schema;
    }
    
    public String getRefSchema() {
        return schema == null ? EMPTY : schema;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setColumn(String column) {
        this.column = column;
    }
    
    public String getColumn() {
        return column;
    }

    public boolean isMarkedDeleted() {
        return markedDeleted;
    }

    public void setMarkedDeleted(boolean markedDeleted) {
        this.markedDeleted = markedDeleted;
    }
    
    
}











