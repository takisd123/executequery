package org.executequery.sql;

public class SelectDerivedTableStrategy extends AbstractDerivedTableStrategy {

    @Override
    public String extractTablesAndAliases(String query) {

        String tables = null;
        int fromIndex = query.indexOf(FROM);
        int whereIndex = query.indexOf(WHERE);

        if (whereIndex != -1 && fromIndex != -1) {
            
            if (whereIndex != -1) {
            
                tables = query.substring(fromIndex + FROM.length(), whereIndex);
                
            } else {
            
                tables = query.substring(fromIndex + FROM.length());
            }
        
        }
        
        return tables;
    }

}
