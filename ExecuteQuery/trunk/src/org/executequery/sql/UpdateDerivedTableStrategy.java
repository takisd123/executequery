package org.executequery.sql;

public class UpdateDerivedTableStrategy extends AbstractDerivedTableStrategy {

    @Override
    public String extractTablesAndAliases(String query) {

        String tables = null;

        int index = query.indexOf(UPDATE);
        if (index != -1) {
            
            String portion = query.substring(index + UPDATE.length()).trim();
            
            index = portion.indexOf(SET);
            if (index != -1) {

                tables = portion.substring(0, index); 
            }
            
        }
        
        return tables;
    }

}
