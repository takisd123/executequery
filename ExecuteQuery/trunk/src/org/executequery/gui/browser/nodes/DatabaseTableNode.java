package org.executequery.gui.browser.nodes;

import java.util.ArrayList;
import java.util.List;

import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.databaseobjects.NamedObject;
import org.underworldlabs.jdbc.DataSourceException;

public class DatabaseTableNode extends DatabaseObjectNode {

    public DatabaseTableNode(NamedObject databaseObject) {

        super(databaseObject);
    }
    
    @Override
    public List<DatabaseObjectNode> getChildObjects() throws DataSourceException {

        List<DatabaseObjectNode> nodes = new ArrayList<DatabaseObjectNode>();
        nodes.add(new ColumnFolderNode());
        nodes.add(new PrimaryKeysFolderNode());
        nodes.add(new ForeignKeysFolderNode());
        nodes.add(new IndexesFolderNode());

        return nodes;
    }
    
    private DatabaseTable databaseTable() {
        
        return (DatabaseTable) getDatabaseObject();
    }

    private List<DatabaseObjectNode> asNodes(List<? extends NamedObject> values) {
        
        if (values != null) {

            List<DatabaseObjectNode> nodes = new ArrayList<DatabaseObjectNode>();
            for (int i = 0, n = values.size(); i < n; i++) {
            
                nodes.add(new DatabaseObjectNode(values.get(i)));
            }
            
            return nodes;
        }

        return null;
    }
    
    
    class ColumnFolderNode extends TableFolderNode {
        
        @Override
        public List<DatabaseObjectNode> getChildObjects() throws DataSourceException {

            return asNodes(databaseTable().getObjects());
        }
        
        @Override
        public String getName() {
            
            return "Columns";
        }
        
        @Override
        public int getType() {

            return NamedObject.COLUMNS_FOLDER_NODE;
        }
        
    }
    
    
    class ForeignKeysFolderNode extends TableFolderNode {
        
        @Override
        public List<DatabaseObjectNode> getChildObjects() throws DataSourceException {

            return asNodes(databaseTable().getForeignKeys());
        }
        
        @Override
        public String getName() {
            
            return "Foreign Keys";
        }

        @Override
        public int getType() {

            return NamedObject.FOREIGN_KEYS_FOLDER_NODE;
        }

    }
    
    class PrimaryKeysFolderNode extends TableFolderNode {
        
        @Override
        public List<DatabaseObjectNode> getChildObjects() throws DataSourceException {

            return asNodes(databaseTable().getPrimaryKeys());
        }
        
        @Override
        public String getName() {
            
            return "Primary Keys";
        }

        @Override
        public int getType() {

            return NamedObject.PRIMARY_KEYS_FOLDER_NODE;
        }

    }

    class IndexesFolderNode extends TableFolderNode {
        
        @Override
        public List<DatabaseObjectNode> getChildObjects() throws DataSourceException {

            return asNodes(databaseTable().getIndexes());
        }
        
        @Override
        public String getName() {
            
            return "Indexes";
        }
     
        @Override
        public int getType() {

            return NamedObject.INDEXES_FOLDER_NODE;
        }

    }
    
    abstract class TableFolderNode extends DatabaseObjectNode {

        @Override
        public String getDisplayName() {

            return getName();
        }
        
    }
    
}
