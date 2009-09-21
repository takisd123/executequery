/*
 * NewTableConstraintsPanel.java
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

package org.executequery.gui.table;

import java.util.Vector;

import org.executequery.gui.browser.*;
import org.executequery.gui.table.TableConstraintFunction;
import org.underworldlabs.swing.table.ComboBoxCellEditor;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class NewTableConstraintsPanel extends TableConstraintsPanel
                                      implements CreateTableSQLSyntax {
    
    /** The table creator object - parent to this */
    private TableConstraintFunction creator;
    
    /** The buffer for the current line */
    private StringBuffer line;
    
    /** The buffer off all SQL generated */
    private StringBuffer sqlBuffer;
    
    public NewTableConstraintsPanel(TableConstraintFunction creator) {
        super();
        this.creator = creator;
        line = new StringBuffer(50);
        sqlBuffer = new StringBuffer(100);
    }
    
    public ColumnData[] getTableColumnData() {
        return creator.getTableColumnData();
    }
    
    public int getMode() {
        return CREATE_TABLE_MODE;
    }

    public void updateCellEditor(int col, int row, String value) {
        ColumnConstraint cc = getConstraintAt(row);
        
        switch(col) {
            
            case 0:
            case 1:
                return;
            
            case 2:
                setCellEditor(3, new ComboBoxCellEditor(
                                        creator.getTableColumnDataVector()));
                
                if (cc.getType() != -1 && cc.getTypeName() == ColumnConstraint.FOREIGN) {
                    Vector schemas = creator.getHostedSchemasVector();
                    setCellEditor(4, new ComboBoxCellEditor(schemas));
                    
                    if (schemas == null || schemas.size() == 0)
                        setCellEditor(5, new ComboBoxCellEditor(
                        creator.getSchemaTables(value)));
                    
                }
                break;
                
            case 3:
                break;
                
            case 4:
                setCellEditor(5, new ComboBoxCellEditor(
                creator.getSchemaTables(value)));
                break;
                
            case 5:
                String schema = cc.getRefSchema();
                if (schema == null || schema.length() == 0) {
                    schema = "";
                }
                
                try {
                    setCellEditor(6, new ComboBoxCellEditor(
                                creator.getColumnNamesVector(value, schema)));
                }
                catch (NullPointerException nullExc) {}
                break;
                
        }
        
    }
    
    public void columnValuesChanged(int col, int row, String value) {
        Vector v = getKeys();
        String name = null;
        boolean hasName = false;
        
        int v_size = v.size();
        sqlBuffer.setLength(0);
        
        for (int i = 0; i < v_size; i++) {
            ColumnConstraint cc = (ColumnConstraint)v.elementAt(i);
            
            if (i == row && value != null && value.length() != 0) {
                name = value;
                hasName = true;
            }
            
            else if (cc.getName() != cc.EMPTY) {
                name = cc.getName();
                hasName = true;
            }
            
            else {
                hasName = false;
            }
            
            if (hasName) {
                
                sqlBuffer.append(COMMA).append(NEW_LINE_2).append(CONSTRAINT);
                sqlBuffer.append(name).append(SPACE);
                
                if (cc.getType() != -1) {
                    
                    if (cc.getType() == cc.UNIQUE_KEY) {
                        sqlBuffer.append(cc.UNIQUE).append(B_OPEN);
                        sqlBuffer.append(cc.getColumn()).append(B_CLOSE);
                    }
                    
                    else {
                        sqlBuffer.append(cc.getTypeName()).append(KEY).append(B_OPEN);
                        sqlBuffer.append(cc.getColumn());
                        sqlBuffer.append(B_CLOSE);
                        
                        if (cc.getType() == cc.FOREIGN_KEY) {
                            sqlBuffer.append(INDENT).append(REFERENCES);
                            
                            if (cc.hasSchema())
                                sqlBuffer.append(cc.getRefSchema()).append(DOT);
                            
                            sqlBuffer.append(cc.getRefTable()).
                            append(B_OPEN).append(cc.getRefColumn()).
                            append(B_CLOSE);
                        }
                        
                    }
                    
                }
                
            }
            
        }
        creator.setSQLText(sqlBuffer.toString(), TableModifier.CONSTRAINT_VALUES);        
    }
    
    public void resetSQLText() {
        columnValuesChanged(0, 0, null);
    }
    
    public String getSQLText() {
        return sqlBuffer.toString();
    }
    
    public void columnValuesChanged() {
        columnValuesChanged(-1, -1, null);
    }
    
    
} // class












