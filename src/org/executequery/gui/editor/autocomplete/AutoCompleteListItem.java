/*
 * AutoCompleteListItem.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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

package org.executequery.gui.editor.autocomplete;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.executequery.sql.QueryTable;

public class AutoCompleteListItem {

    private final String value;

    private final String description;
    
    private final AutoCompleteListItemType type;

    private final String displayValue;

    private final String parentName;

    public AutoCompleteListItem(String value, String displayValue, String description,
            AutoCompleteListItemType type) {

        this(value, null, displayValue, description, type);
    }

    public AutoCompleteListItem(String value, String parentName, String displayValue, 
            String description, AutoCompleteListItemType type) {

        super();
        
        this.value = value;
        this.parentName = parentName;
        this.displayValue = displayValue;
        this.description = description;
        this.type = type;
    }

    public boolean isNothingProposed() {

        return (type == AutoCompleteListItemType.NOTHING_PROPOSED);
    }
    
    public boolean isKeyword() {
        return type.isKeyword();
    }

    public boolean isTableColumn() {
        return type.isTableColumn();
    }
    
    public boolean isTable() {
        return type.isTable();
    }
    
    public boolean isSchemaObject() {
        return type.isTableColumn() || type.isTable();
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getValue() {
        return value;
    }

    public String getInsertionValue() {
        
        if (type.isTableColumn()) {
            
            int dotIndex = value.indexOf('.');
            return value.substring(dotIndex + 1);
        
        } else {
            
            return value;
        }
        
    }
    
    public boolean isForPrefix(List<QueryTable> tables, String prefix, boolean prefixHadAlias) {
        
        boolean hasTables = !(tables == null || tables.isEmpty());
        if ((type.isKeyword() || type.isTable())) {

            if (prefixHadAlias) {
                
                return false;
            }
            
            if (!hasTables || !type.isTable()) { // keyword

                return getInsertionValue().toUpperCase().startsWith(prefix, 0);
            }
        }
        
        if (!hasTables) { // ??? hhhmmmmmm
            
            return getInsertionValue().toUpperCase().startsWith(prefix, 0);
        }
        
        if (parentName != null) { // shouldn't here but does TODO:
        
            String tableName = parentName.toUpperCase();
            
            for (QueryTable table : tables) {
                
                if (tableName.equals(table.getCompareName().toUpperCase())) {
                
                    if (StringUtils.isBlank(prefix)) {
                        
                        return true;
                    }
                    
                    return getInsertionValue().toUpperCase().startsWith(prefix, 0);
                }
                
            }
        }

        return false;
    }
    
    public AutoCompleteListItemType getType() {
        return type;
    }
 
    public String getParentName() {
        return parentName;
    }
    
    @Override
    public String toString() {
        return getDisplayValue();
    }
    
}




