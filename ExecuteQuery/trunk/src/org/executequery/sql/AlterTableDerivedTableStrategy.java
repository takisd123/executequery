package org.executequery.sql;

public class AlterTableDerivedTableStrategy extends AbstractDerivedTableStrategy {

    @Override
    public String extractTablesAndAliases(String query) {

        String tables = null;

        int index = query.indexOf(ALTER_TABLE);
        if (index != -1) {
            
            String portion = query.substring(index + ALTER_TABLE.length());
            
            char[] chars = portion.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                
                if (i > 0 && Character.isWhitespace(chars[i])) {
                    
                    tables = portion.substring(0, i).trim();
                }
                
            }
            
        }
        
        return tables;
    }

}
