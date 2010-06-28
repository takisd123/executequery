package org.executequery.sql;


public class InsertDerivedTableStrategy extends AbstractDerivedTableStrategy {

    @Override
    public String extractTablesAndAliases(String query) {

        String tables = null;

        int index = query.indexOf(INSERT);
        if (index != -1) {
            
            String portion = query.substring(index + INSERT.length()).trim();
            
            index = portion.indexOf('(');
            if (index != -1) {
                
                tables = portion.substring(0, index); 
            
            }

        }
        
        return tables;
    }

}
