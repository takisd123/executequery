/*
 * AutoCompleteSelectionsFactory.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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
import org.executequery.databaseobjects.impl.ColumnInformation;
import org.executequery.log.Log;
import org.executequery.repository.KeywordRepository;
import org.executequery.repository.RepositoryCache;

public class AutoCompleteSelectionsFactory {

    private static final String DATABASE_TABLE_DESCRIPTION = "Database Table";
    
    private static final String DATABASE_TABLE_VIEW = "Database View";
    
    private static final String DATABASE_COLUMN_DESCRIPTION = "Database Column";
    
    private QueryEditorAutoCompletePopupProvider provider;
    
    public AutoCompleteSelectionsFactory(QueryEditorAutoCompletePopupProvider provider) {
        super();
        this.provider = provider;
    }

    public void build(DatabaseHost databaseHost, boolean autoCompleteKeywords, boolean autoCompleteSchema) {

        List<AutoCompleteListItem> listSelections = new ArrayList<AutoCompleteListItem>();
        if (autoCompleteKeywords) {
        
            addSQL92Keywords(listSelections);
            addUserDefinedKeywords(listSelections);
            
            addToProvider(listSelections);
        }

        if (databaseHost != null && databaseHost.isConnected()) {

            if (autoCompleteKeywords) {
            
                addDatabaseDefinedKeywords(databaseHost, listSelections);
                addToProvider(listSelections);
            }

            if (autoCompleteSchema) {
             
                List<AutoCompleteListItem> tables = databaseTablesForHost(databaseHost);
                listSelections.addAll(tables);
                addToProvider(listSelections);
                
                
                List<AutoCompleteListItem> columns = databaseColumnsForTables(databaseHost, tables);
                listSelections.addAll(columns);
                addToProvider(listSelections);
            }

        }

//        Log.debug("Sorting suggestions list for host [ " + databaseHost.getName() + " ]");        
//        Collections.sort(listSelections, new AutoCompleteListItemComparator());

//        return listSelections;
    }

    private void addToProvider(List<AutoCompleteListItem> listSelections) {
        provider.addListItems(listSelections);
        listSelections.clear();
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
    
    private List<String> tables;
    
    private List<AutoCompleteListItem> databaseTablesForHost(DatabaseHost databaseHost) {

        tables = databaseObjectsForHost(databaseHost, "TABLE");
        Log.debug("Generated list for type TABLE with size " + tables.size());

        List<String> views = databaseObjectsForHost(databaseHost, "VIEW");
        Log.debug("Generated list for type VIEW with size " + views.size());
        
        List<AutoCompleteListItem> list = new ArrayList<AutoCompleteListItem>();
        tablesToAutoCompleteListItems(list, tables, 
                DATABASE_TABLE_DESCRIPTION, AutoCompleteListItemType.DATABASE_TABLE);
        tablesToAutoCompleteListItems(list, views,
                DATABASE_TABLE_VIEW, AutoCompleteListItemType.DATABASE_VIEW);

        return list;
    }

    private List<String> databaseObjectsForHost(DatabaseHost databaseHost, String type) {
        
    	try {
    	
    		Log.debug("Building autocomplete object list using [ " + databaseHost.getName() + " ] for type - " + type);
    		
	        return databaseHost.getTableNames(defaultCatalogForHost(databaseHost), 
	                defaultSchemaForHost(databaseHost), type);
    	
    	} finally {

    		Log.debug("Finished autocomplete object list using [ " + databaseHost.getName() + " ] for type - " + type);
    	}
    }
    
    private List<AutoCompleteListItem> tablesToAutoCompleteListItems(
            List<AutoCompleteListItem> list, List<String> tables, 
            String databaseObjectDescription, AutoCompleteListItemType autoCompleteListItemType) {

        for (String table : tables) {

            list.add(new AutoCompleteListItem(table, 
                    table, databaseObjectDescription, autoCompleteListItemType)); 
        }
        
        return list;
    }

    private String defaultSchemaForHost(DatabaseHost databaseHost) {
        
        if (databaseHost.isConnected()) {

            DatabaseSource schema = databaseHost.getDefaultSchema();
            if (schema != null) {
            
                return schema.getName();
            }
        }        
        return null;
    }

    private String defaultCatalogForHost(DatabaseHost databaseHost) {

        if (databaseHost.isConnected()) {
        
            DatabaseSource catalog = databaseHost.getDefaultCatalog();
            if (catalog != null) {
            
                return catalog.getName();
            }
        }        
        return null;
    }

    private List<AutoCompleteListItem> databaseColumnsForTables(DatabaseHost databaseHost, List<AutoCompleteListItem> tables) {

    	Log.debug("Retrieving column names for tables for host [ " + databaseHost.getName() + " ]");
    	
        List<AutoCompleteListItem> list = new ArrayList<AutoCompleteListItem>();
        if (databaseHost.isConnected()) {
        
            for (AutoCompleteListItem table : tables) {
    
            	Log.debug("Retrieving column names for table [ " + table.getDisplayValue() + " ]");
            	
                List<ColumnInformation> columns = databaseHost.getColumnInformation(
                        defaultCatalogForHost(databaseHost), 
                        defaultSchemaForHost(databaseHost), table.getValue());
                
                for (ColumnInformation column : columns) {
                    
                    list.add(new AutoCompleteListItem(
                            column.getName(), 
                            table.getValue(),
//                            formatColumnName(table.getValue(), columnName), 
                            column.getDescription(),
                            DATABASE_COLUMN_DESCRIPTION, 
                            AutoCompleteListItemType.DATABASE_TABLE_COLUMN)); 
                }
                
            }
            
        }
        
        Log.debug("Finished retrieving column names for tables for host [ " + databaseHost.getName() + " ]");
        return list;
    }

    /*
    private String formatColumnName(String table, String column) {
        
        return new StringBuilder().append(column).
            append("  [").append(table).append("]").toString();
    }
    */
    
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
