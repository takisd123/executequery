package org.executequery.databaseobjects.impl;

public class ColumnInformation {

    private final String name;
    private final String description;

    ColumnInformation(String name, String description) {

        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
}
