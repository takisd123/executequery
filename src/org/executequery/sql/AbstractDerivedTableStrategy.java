package org.executequery.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractDerivedTableStrategy implements DerivedTableStrategy {

    public List<QueryTable> deriveTables(String query) {

        List<QueryTable> queryTables = new ArrayList<QueryTable>();
        String tables = extractTablesAndAliases(query.toUpperCase());
        
        if (StringUtils.isNotBlank(tables)) {
            
            String[] namesAndAliases = StringUtils.split(tables, ",");                
            for (String nameAndAlias : namesAndAliases) {
                
                String[] strings = StringUtils.split(nameAndAlias, " ");                
                queryTables.add(new QueryTable(strings[0].trim(), strings.length > 1 ? strings[1].trim() : null));
            }

        }

        return queryTables;
    }

    abstract String extractTablesAndAliases(String query);

}
