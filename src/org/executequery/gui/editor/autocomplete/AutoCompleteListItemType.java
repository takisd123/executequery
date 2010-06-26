package org.executequery.gui.editor.autocomplete;

public enum AutoCompleteListItemType {

    SQL92_KEYWORD, 
    USER_DEFINED_KEYWORD,
    DATABASE_DEFINED_KEYWORD, 
    DATABASE_TABLE, 
    DATABASE_TABLE_COLUMN,
    DATABASE_SEQUENCE,
    DATABASE_DATA_TYPE,
    NOTHING_PROPOSED;

    public boolean isKeyword() {
        
        return this == SQL92_KEYWORD 
            || this == USER_DEFINED_KEYWORD
            || this == DATABASE_DEFINED_KEYWORD;
    }
    
    public boolean isTableColumn() {
        
        return this == DATABASE_TABLE_COLUMN;
    }

    public boolean isTable() {

        return this == DATABASE_TABLE;
    }
    
}
