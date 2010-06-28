package org.executequery.sql;

import org.apache.commons.lang.StringUtils;

public class QueryTable {

    private final String name;

    private final String alias;

    public QueryTable(String name, String alias) {

        this.name = name;
        this.alias = alias;
    }

    public String getAlias() {
     
        return alias;
    }
    
    public String getName() {
     
        return name;
    }

    public String getCompareName() {
        
        if (name.contains(".")) {
            
            return name.substring(name.indexOf('.') + 1);
        }
        return name;
    }
    
    public boolean hasCatalogOrSchemaPrefix() {
        
        return name.contains(".");
    }
    
    public String getCatalogOrSchemaPrefix() {
        
        if (hasCatalogOrSchemaPrefix()) {
            
            return name.substring(0, name.indexOf('.')); 
        }
        return null;        
    }
    
    public boolean isNameOrAlias(String nameOrAlias) {

        String testString = nameOrAlias.toUpperCase();
        
        if (name.toUpperCase().equals(testString)) {
            
            return true;
        }
        
        if (StringUtils.isNotBlank(alias)) {
            
            return alias.toUpperCase().equals(testString);
        }
         
        return false;
    }
    
}
