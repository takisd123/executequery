package org.executequery.gui.editor.autocomplete;

public enum AutoCompleteListItemType {

    SQL92_KEYWORD, 
    USER_DEFINED_KEYWORD,
    DATABASE_DEFINED_KEYWORD, 
    DATABASE_TABLE, 
    DATABASE_TABLE_COLUMN,
    DATABASE_SEQUENCE,
    NOTHING_PROPOSED;

    public static boolean isKeyword(AutoCompleteListItemType type) {
        
        return type == SQL92_KEYWORD 
            || type == USER_DEFINED_KEYWORD
            || type == DATABASE_DEFINED_KEYWORD;
    }
    
}
