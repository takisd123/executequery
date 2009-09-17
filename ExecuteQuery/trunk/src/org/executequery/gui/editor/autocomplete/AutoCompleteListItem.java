package org.executequery.gui.editor.autocomplete;

public class AutoCompleteListItem {

    private final String value;

    private final String description;
    
    private final AutoCompleteListItemType type;

    private final String displayValue;

    public AutoCompleteListItem(String value, String displayValue, String description,
            AutoCompleteListItemType type) {
        
        super();
        
        this.value = value;
        this.displayValue = displayValue;
        this.description = description;
        this.type = type;
    }

    public boolean isNothingProposed() {

        return (type == AutoCompleteListItemType.NOTHING_PROPOSED);
    }
    
    public boolean isKeyword() {
        
        return AutoCompleteListItemType.isKeyword(type);
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
        
        if (type == AutoCompleteListItemType.DATABASE_TABLE_COLUMN) {
            
            int dotIndex = value.indexOf('.');
            return value.substring(dotIndex + 1);
        
        } else {
            
            return value;
        }
        
    }
    
    public AutoCompleteListItemType getType() {
        return type;
    }
 
    @Override
    public String toString() {
        return getDisplayValue();
    }
    
}
