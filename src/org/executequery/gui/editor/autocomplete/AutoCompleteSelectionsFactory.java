/*
 * AutoCompleteSelectionsFactory.java
 *
 * Copyright (C) 2002-2010 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.executequery.gui.editor.autocomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseSource;
import org.executequery.repository.KeywordRepository;
import org.executequery.repository.RepositoryCache;
import org.executequery.sql.QueryTable;

public class AutoCompleteSelectionsFactory {

    private static final String DATABASE_TABLE_DESCRIPTION = "Database Table";
    
    private static final String DATABASE_COLUMN_DESCRIPTION = "Database Column";
    
    public List<AutoCompleteListItem> build(DatabaseHost databaseHost,
            boolean autoCompleteKeywords, boolean autoCompleteSchema) {

        List<AutoCompleteListItem> listSelections = new ArrayList<AutoCompleteListItem>();
        
        if (autoCompleteKeywords) {
        
            addSQL92Keywords(listSelections);
            addUserDefinedKeywords(listSelections);
        }

        if (databaseHost != null && databaseHost.isConnected()) {

            if (autoCompleteKeywords) {
            
                addDatabaseDefinedKeywords(databaseHost, listSelections);
            }

            if (autoCompleteSchema) {
             
                List<AutoCompleteListItem> tables = databaseTablesForHost(databaseHost);
                listSelections.addAll(tables);
                
                List<AutoCompleteListItem> columns = databaseColumnsForTables(databaseHost, tables);
                listSelections.addAll(columns);
            }

        }

        Collections.sort(listSelections, new AutoCompleteListItemComparator());

        return listSelections;
    }
    
    public List<AutoCompleteListItem> buildKeywords(DatabaseHost databaseHost, 
            boolean autoCompleteKeywords) {

        List<AutoCompleteListItem> listSelections = new ArrayList<AutoCompleteListItem>();
 
        if (autoCompleteKeywords) {
        
            addSQL92Keywords(listSelections);
            addUserDefinedKeywords(listSelections);
    
            if (databaseHost != null && databaseHost.isConnected()) {
    
                addDatabaseDefinedKeywords(databaseHost, listSelections);
            }
    
            Collections.sort(listSelections, new AutoCompleteListItemComparator());
        }

        return listSelections;
    }
    
    public List<AutoCompleteListItem> loadForTables(DatabaseHost databaseHost, List<QueryTable> queryTables) {

        if (tables == null) {
            
            databaseTablesForHost(databaseHost);
        }
        
        List<AutoCompleteListItem> list = new ArrayList<AutoCompleteListItem>();

        String catalog = defaultCatalogForHost(databaseHost);
        String schema = defaultSchemaForHost(databaseHost);
        
        for (QueryTable table : queryTables) {
            
            String _catalog = catalog;
            String _schema = schema;
            
            if (table.hasCatalogOrSchemaPrefix()) {
                
                _catalog = table.getCatalogOrSchemaPrefix();
                _schema= table.getCatalogOrSchemaPrefix();
            }
            
            String tableName = databaseHeldTableName(table.getName());
            List<String> columns = databaseHost.getColumnNames(_catalog, _schema, tableName);
            
            for (String columnName : columns) {
                
                list.add(new AutoCompleteListItem(
                        columnName, 
                        table.getName(),
                        formatColumnName(table.getName(), columnName), 
                        DATABASE_COLUMN_DESCRIPTION, 
                        AutoCompleteListItemType.DATABASE_TABLE_COLUMN)); 
            }
                
        }
        
        return list;
    }

    private String databaseHeldTableName(String name) {

        for (String table : tables) {
            
            if (table.equalsIgnoreCase(name)) {
                
                return table;
            }
        }

        return name;
    }

    private List<String> tables;
    
    private List<AutoCompleteListItem> databaseTablesForHost(DatabaseHost databaseHost) {

        List<AutoCompleteListItem> list = new ArrayList<AutoCompleteListItem>();

        tables = databaseHost.getTableNames(defaultCatalogForHost(databaseHost), 
                defaultSchemaForHost(databaseHost), "TABLE");

        return tablesToAutoCompleteListItems(list, tables);
    }

    private List<AutoCompleteListItem> tablesToAutoCompleteListItems(List<AutoCompleteListItem> list, List<String> tables) {

        for (String table : tables) {

            list.add(new AutoCompleteListItem(table, 
                    table, DATABASE_TABLE_DESCRIPTION, AutoCompleteListItemType.DATABASE_TABLE)); 
        }
        
        return list;
    }

    private String defaultSchemaForHost(DatabaseHost databaseHost) {
        
        DatabaseSource schema = databaseHost.getDefaultSchema();
        
        if (schema != null) {
        
            return schema.getName();
        }
        
        return null;
    }

    private String defaultCatalogForHost(DatabaseHost databaseHost) {

        DatabaseSource catalog = databaseHost.getDefaultCatalog();
        
        if (catalog != null) {
        
            return catalog.getName();
        }
        
        return null;
    }

    private List<AutoCompleteListItem> databaseColumnsForTables(DatabaseHost databaseHost,
            List<AutoCompleteListItem> tables) {

        List<AutoCompleteListItem> list = new ArrayList<AutoCompleteListItem>();
        
        if (databaseHost.isConnected()) {
        
            for (AutoCompleteListItem table : tables) {
    
                List<String> columns = databaseHost.getColumnNames(
                        defaultCatalogForHost(databaseHost), 
                        defaultSchemaForHost(databaseHost), table.getValue());
                
                for (String columnName : columns) {
                    
                    list.add(new AutoCompleteListItem(
                            columnName, 
                            table.getValue(),
                            formatColumnName(table.getValue(), columnName), 
                            DATABASE_COLUMN_DESCRIPTION, 
                            AutoCompleteListItemType.DATABASE_TABLE_COLUMN)); 
                }
                
            }
            
        }
        
        return list;
    }

    private String formatColumnName(String table, String column) {
        
        return new StringBuilder().append(column).
            append("  [").append(table).append("]").toString();
    }
    
    private void addDatabaseDefinedKeywords(DatabaseHost databaseHost,
            List<AutoCompleteListItem> list) {

        String[] keywords = databaseHost.getDatabaseKeywords();
        List<String> asList = new ArrayList<String>();

        for (String keyword : keywords) {
            
            asList.add(keyword);
        }

        addKeywordsFromList(asList, list, 
                "Database Defined Keyword", AutoCompleteListItemType.DATABASE_DEFINED_KEYWORD);
        
    }

    private void addSQL92Keywords(List<AutoCompleteListItem> list) {

        addKeywordsFromList(keywords().getSQL92(),
                list, "SQL92 Keyword", AutoCompleteListItemType.SQL92_KEYWORD);
    }

    private void addUserDefinedKeywords(List<AutoCompleteListItem> list) {

        addKeywordsFromList(keywords().getUserDefinedSQL(),
                list, "User Defined Keyword", AutoCompleteListItemType.USER_DEFINED_KEYWORD);
    }

    private void addKeywordsFromList(List<String> keywords, List<AutoCompleteListItem> list,
            String description, AutoCompleteListItemType autoCompleteListItemType) {
        
        for (String keyword : keywords) {
            
            list.add(new AutoCompleteListItem(keyword, 
                    keyword, description, autoCompleteListItemType)); 
        }
        
    }
    
    private KeywordRepository keywords() {

        return (KeywordRepository)RepositoryCache.load(KeywordRepository.REPOSITORY_ID);
    }

    
    static class AutoCompleteListItemComparator implements Comparator<AutoCompleteListItem> {

        public int compare(AutoCompleteListItem o1, AutoCompleteListItem o2) {

            return o1.getValue().toUpperCase().compareTo(o2.getValue().toUpperCase());
        }
        
    }


}

