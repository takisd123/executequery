/*
 * LiquibaseStatementGenerator.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
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

package org.executequery.sql.spi;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import liquibase.change.AddColumnChange;
import liquibase.change.AddDefaultValueChange;
import liquibase.change.AddForeignKeyConstraintChange;
import liquibase.change.AddNotNullConstraintChange;
import liquibase.change.AddPrimaryKeyChange;
import liquibase.change.AddUniqueConstraintChange;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.CreateTableChange;
import liquibase.change.DropColumnChange;
import liquibase.change.DropForeignKeyConstraintChange;
import liquibase.change.DropPrimaryKeyChange;
import liquibase.change.DropTableChange;
import liquibase.change.DropUniqueConstraintChange;
import liquibase.change.ModifyColumnChange;
import liquibase.change.RenameColumnChange;
import liquibase.database.Database;
import liquibase.database.sql.SqlStatement;
import liquibase.exception.StatementNotSupportedOnDatabaseException;
import liquibase.exception.UnsupportedChangeException;

import org.executequery.ApplicationException;
import org.executequery.databaseobjects.DatabaseColumn;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.databaseobjects.impl.ColumnConstraint;
import org.executequery.databaseobjects.impl.DatabaseTableColumn;
import org.executequery.log.Log;
import org.executequery.sql.StatementGenerator;

public class LiquibaseStatementGenerator implements StatementGenerator {

    private LiquibaseDatabaseFactory databaseFactory;

    public String columnNameValueEscaped(DatabaseTableColumn tableColumn) {

        Database database = databaseFromName(
                connectionFromTable(tableColumn.getTable()), tableColumn.getTable().getHost().getDatabaseProductName());
        
        return database.escapeColumnName(tableColumn.getSchemaName(),
                tableColumn.getTable().getName(), tableColumn.getName());
    }
    
    public String dropTable(String databaseName, DatabaseTable table) {

        return dropTable(databaseName, table, false);
    }

    public String dropTableCascade(String databaseName, DatabaseTable table) {

        return dropTable(databaseName, table, true);
    }

    public String alterTable(String databaseName, DatabaseTable table) {
        
        StringBuilder sb = new StringBuilder();

        Database database = databaseFromName(connectionFromTable(table), databaseName);

        for (DatabaseColumn column : table.getColumns()) {

            sb.append(alterColumn(column, database));
        }

        for (ColumnConstraint constraint : table.getConstraints()) {

            if (constraint.isMarkedDeleted()) {
                
                sb.append(dropConstraint(constraint, database));

            } else if (constraint.isNewConstraint()) {

                sb.append(addConstraint(constraint, database));
            }

        }
        
        return sb.toString();
    }

    public String tableConstraintsAsAlter(String databaseName, DatabaseTable table) {
        
        StringBuilder sb = new StringBuilder();

        Database database = databaseFromName(connectionFromTable(table), databaseName);

        sb.append(addPrimaryKeys(primaryKeysForTable(table), database));

        for (ColumnConstraint constraint : table.getConstraints()) {

            if (!constraint.isPrimaryKey()) {
            
                sb.append(addConstraint(constraint, database));
            }
        }
        
        return sb.toString();
    }

    public String createUniqueKeyChange(String databaseName, DatabaseTable table) {

        StringBuilder sb = new StringBuilder();
        List<ColumnConstraint> uniqueKeys = table.getUniqueKeys();

        Database database = databaseFromName(connectionFromTable(table), databaseName);

        for (ColumnConstraint constraint : uniqueKeys) {

            AddUniqueConstraintChange change = new AddUniqueConstraintChange();
            change.setTableName(constraint.getTableName());
            change.setColumnNames(constraint.getColumnName());
            change.setConstraintName(constraint.getName());
            
            sb.append(generateStatements(change, database));            
        }
        
        return sb.toString();
    }

    public String createForeignKeyChange(String databaseName, DatabaseTable table) {

        StringBuilder sb = new StringBuilder();
        List<ColumnConstraint> foreignKeys = table.getForeignKeys();

        Database database = databaseFromName(connectionFromTable(table), databaseName);
        
        for (ColumnConstraint constraint : foreignKeys) {

            AddForeignKeyConstraintChange change = new AddForeignKeyConstraintChange();
            change.setBaseTableName(constraint.getTableName());
            change.setBaseColumnNames(constraint.getColumnName());
            change.setConstraintName(constraint.getName());
            change.setReferencedTableName(constraint.getReferencedTable());
            change.setReferencedColumnNames(constraint.getReferencedColumn());
            
            sb.append(generateStatements(change, database));            
        }
        
        return sb.toString();
    }

    public String createPrimaryKeyChange(String databaseName, DatabaseTable table) {
    
        List<ColumnConstraint> primaryKeys = table.getPrimaryKeys();

        if (primaryKeys.isEmpty()) {
            
            return "";
        }
        
        AddPrimaryKeyChange primaryKeyChange = new AddPrimaryKeyChange();
        primaryKeyChange.setTableName(table.getName());

        StringBuilder sb = new StringBuilder();
        
        for (int i = 0, n = primaryKeys.size(); i < n; i++) {
        
            ColumnConstraint columnConstraint = primaryKeys.get(i);
            sb.append(columnConstraint.getColumnName());
            
            if (i < (n - 1)) {
                
                sb.append(",");
            }
            
            primaryKeyChange.setConstraintName(columnConstraint.getName());            
        }
        
        primaryKeyChange.setColumnNames(sb.toString());

        Database database = databaseFromName(connectionFromTable(table), databaseName);
        
        return generateStatements(primaryKeyChange, database);
    }

    public String createTableWithConstraints(String databaseName, DatabaseTable table) {
        
        CreateTableChange tableChange = createTableChange(table);

        List<DatabaseColumn> columns = table.getColumns();

        for (DatabaseColumn column : columns) {

            if (column.hasConstraints()) {

                ColumnConfig columnConfig = columnConfigForColumn(tableChange, column);
                
                ConstraintsConfig constraintConfig = new ConstraintsConfig();
                
                for (ColumnConstraint constraint : column.getConstraints()) {
                    
                    if (constraint.isPrimaryKey()) {

                        constraintConfig.setPrimaryKey(Boolean.TRUE);
                    }
                    
                    if (constraint.isForeignKey()) {
                    
                        constraintConfig.setForeignKeyName(constraint.getName());
                        
                        constraintConfig.setReferences(
                                constraint.getReferencedTable() 
                                + "(" + 
                                constraint.getReferencedColumn()
                                + ")");
                    }
                    
                    if (constraint.isUniqueKey()) {

                        constraintConfig.setUnique(Boolean.TRUE);
                        constraintConfig.setUniqueConstraintName(constraint.getName());
                    }

                }

                columnConfig.setConstraints(constraintConfig);
            }
            
        }
        
        Database database = databaseFromName(connectionFromTable(table), databaseName);
        
        return generateStatements(tableChange, database);
    }

    public String createTable(String databaseName, DatabaseTable table) {
        
        CreateTableChange tableChange = createTableChange(table);

        Database database = databaseFromName(connectionFromTable(table), databaseName);
        
        return generateStatements(tableChange, database);
    }

    private String dropTable(String databaseName, 
            DatabaseTable table, boolean cascadeConstraints) {
        
        DropTableChange tableChange = dropTableChange(table);
        tableChange.setCascadeConstraints(Boolean.valueOf(cascadeConstraints));

        Database database = databaseFromName(connectionFromTable(table), databaseName);
        
        return generateStatements(tableChange, database).trim();
    }
    
    private ColumnConfig columnConfigForColumn(
            CreateTableChange tableChange, DatabaseColumn column) {

        String name = column.getName();

        List<ColumnConfig> columns = tableChange.getColumns();

        for (ColumnConfig columnConfig : columns) {
            
            if (columnConfig.getName().equalsIgnoreCase(name)) {

                return columnConfig;
            }

        }

        return null;
    }

    private Connection connectionFromTable(DatabaseTable table) {
        
        return table.getHost().getConnection();
    }
    
    private DropTableChange dropTableChange(DatabaseTable table) {
        
        DropTableChange tableChange = new DropTableChange();
        tableChange.setTableName(table.getName());
        
        return tableChange;
    }
    
    private CreateTableChange createTableChange(DatabaseTable table) {

        CreateTableChange tableChange = new CreateTableChange();      
        //tableChange.setSchemaName(table.getSchemaName());
        tableChange.setTableName(table.getName());

        for (DatabaseColumn column : table.getColumns()) {

            tableChange.addColumn(createColumn(column));
        }

        return tableChange;
    }

    private String addConstraint(ColumnConstraint constraint, Database database) {

        StringBuilder sb = new StringBuilder();
        
        if (constraint.isPrimaryKey()) {
            
            sb.append(addPrimaryKey(constraint, database));
        }

        if (constraint.isForeignKey()) {
            
            sb.append(addForeignKey(constraint, database));
        }

        if (constraint.isUniqueKey()) {

            sb.append(addUniqueKey(constraint, database));
        }
        
        return sb.toString();
    }

    private String addUniqueKey(ColumnConstraint constraint, Database database) {

        AddUniqueConstraintChange change = new AddUniqueConstraintChange();
        change.setTableName(constraint.getTableName());
        change.setColumnNames(constraint.getColumnName());
        change.setConstraintName(constraint.getName());

        return generateStatements(change, database);
    }

    private String addForeignKey(ColumnConstraint constraint, Database database) {

        AddForeignKeyConstraintChange change = new AddForeignKeyConstraintChange();
        change.setBaseTableName(constraint.getTableName());
        change.setBaseColumnNames(constraint.getColumnName());
        change.setConstraintName(constraint.getName());
        change.setReferencedTableName(constraint.getReferencedTable());
        change.setReferencedColumnNames(constraint.getReferencedColumn());

        return generateStatements(change, database);
    }

    private String addPrimaryKey(ColumnConstraint constraint, Database database) {

        AddPrimaryKeyChange change = new AddPrimaryKeyChange();
        change.setTableName(constraint.getTableName());
        change.setColumnNames(constraint.getColumnName());
        change.setConstraintName(constraint.getName());
        
        return generateStatements(change, database);
    }

    private String addPrimaryKeys(List<ColumnConstraint> primaryKeys, Database database) {

        if (primaryKeys == null || primaryKeys.isEmpty()) {
            
            return "";
        }
        
        if (primaryKeys.size() == 1) {
            
            return addPrimaryKey(primaryKeys.get(0), database);
        }
        
        StringBuilder sb = new StringBuilder();
        
        String tableName = null;
        String constraintName = null;

        for (int i = 0, n = primaryKeys.size(); i < n; i++) {
            
            ColumnConstraint primaryKey = primaryKeys.get(i);
            
            if (i > 0) {
                
                sb.append(",");
                
            } else {
                
                tableName = primaryKey.getTableName();
                constraintName = primaryKey.getName();
            }

            sb.append(primaryKey.getColumnName());
        }

        AddPrimaryKeyChange change = new AddPrimaryKeyChange();
        change.setTableName(tableName);
        change.setColumnNames(sb.toString());
        change.setConstraintName(constraintName);
        
        return generateStatements(change, database);
    }

    private String dropConstraint(ColumnConstraint constraint, Database database) {

        StringBuilder sb = new StringBuilder();
        
        if (constraint.isPrimaryKey()) {
            
            sb.append(dropPrimaryKey(constraint, database));
        }

        if (constraint.isForeignKey()) {
            
            sb.append(dropForeignKey(constraint, database));
        }

        if (constraint.isUniqueKey()) {

            sb.append(dropUniqueKey(constraint, database));
        }
        
        return sb.toString();
    }

    private String dropUniqueKey(ColumnConstraint constraint, Database database) {

        DropUniqueConstraintChange change = new DropUniqueConstraintChange();
        change.setTableName(constraint.getTableName());
        change.setConstraintName(constraint.getName());
        
        return generateStatements(change, database);
    }

    private String dropForeignKey(ColumnConstraint constraint, Database database) {

        DropForeignKeyConstraintChange change = new DropForeignKeyConstraintChange();
        change.setBaseTableName(constraint.getTableName());
        change.setConstraintName(constraint.getName());

        return generateStatements(change, database);
    }

    private String dropPrimaryKey(ColumnConstraint constraint, Database database) {

        DropPrimaryKeyChange change = new DropPrimaryKeyChange();
        change.setTableName(constraint.getTableName());
        change.setConstraintName(constraint.getName());
        
        return generateStatements(change, database);
    }

    private String alterColumn(DatabaseColumn column, Database database) {

        DatabaseTableColumn tableColumn = (DatabaseTableColumn)column;
        
        StringBuilder sb = new StringBuilder();

        boolean isNewOrDeleted = true;
        
        if (tableColumn.isNewColumn()) {
            
            sb.append(addColumnChange(tableColumn, database));     
            
        } else if (tableColumn.isMarkedDeleted()) {

            sb.append(dropColumnChange(tableColumn, database));

        } else {
            
            isNewOrDeleted = false;
        }
        
        if (isNewOrDeleted) {
            
            return sb.toString();
        }
        
        if (tableColumn.isNameChanged()) {
            
            sb.append(renameColumnChange(tableColumn, database));
        } 
        
        if (tableColumn.isDataTypeChanged()) {
        
            sb.append(modifyColumnChange(tableColumn, database));
        }

        if (tableColumn.isRequiredChanged()) {
            
            sb.append(addNotNullConstraintChange(tableColumn, database));
        }
        
        if (tableColumn.isDefaultValueChanged()) {
            
            sb.append(addDefaultValueChange(tableColumn, database));
        }
         
        return sb.toString();
    }

    private List<ColumnConstraint> primaryKeysForTable(DatabaseTable table) {

        List<ColumnConstraint> primaryKeys = new ArrayList<ColumnConstraint>();
        
        for (ColumnConstraint constraint : table.getConstraints()) {
            
            if (constraint.isPrimaryKey()) {
                
                primaryKeys.add(constraint);
            }
            
        }
        
        return primaryKeys;
    }

    private String addNotNullConstraintChange(DatabaseTableColumn tableColumn, Database database) {

        AddNotNullConstraintChange columnChange = new AddNotNullConstraintChange();
        
        //columnChange.setSchemaName(tableColumn.getSchemaName());
        columnChange.setTableName(tableColumn.getTable().getName());
        columnChange.setColumnName(tableColumn.getName());
        columnChange.setColumnDataType(tableColumn.getFormattedDataType());

        return generateStatements(columnChange, database);        
    }

    private String addDefaultValueChange(DatabaseTableColumn tableColumn, Database database) {

        AddDefaultValueChange columnChange = new AddDefaultValueChange();
        
        //columnChange.setSchemaName(tableColumn.getSchemaName());
        columnChange.setTableName(tableColumn.getTable().getName());
        columnChange.setColumnName(tableColumn.getName());
        columnChange.setDefaultValue(tableColumn.getDefaultValue());

        return generateStatements(columnChange, database);        
    }

    private String modifyColumnChange(DatabaseTableColumn tableColumn, Database database) {

        ModifyColumnChange columnChange = new ModifyColumnChange();      
        
        //columnChange.setSchemaName(tableColumn.getSchemaName());
        columnChange.setTableName(tableColumn.getTable().getName());
        columnChange.addColumn(createColumn(tableColumn));     

        return generateStatements(columnChange, database);
    }

    private String addColumnChange(DatabaseTableColumn tableColumn, Database database) {

        AddColumnChange columnChange = new AddColumnChange();
        
        //columnChange.setSchemaName(tableColumn.getSchemaName());
        columnChange.setTableName(tableColumn.getTable().getName());
        columnChange.addColumn(createColumn(tableColumn));

        return generateStatements(columnChange, database);
    }
    
    private String dropColumnChange(DatabaseTableColumn tableColumn, Database database) {

        DropColumnChange columnChange = new DropColumnChange();
        
        //columnChange.setSchemaName(tableColumn.getSchemaName());
        columnChange.setTableName(tableColumn.getTable().getName());
        columnChange.setColumnName(tableColumn.getName());     

        return generateStatements(columnChange, database);
    }
    
    private String renameColumnChange(DatabaseTableColumn tableColumn, Database database) {

        RenameColumnChange columnChange = new RenameColumnChange();
        
        //columnChange.setSchemaName(tableColumn.getSchemaName());
        columnChange.setTableName(tableColumn.getTable().getName());
        columnChange.setNewColumnName(tableColumn.getName());
        columnChange.setOldColumnName(tableColumn.getOriginalColumn().getName());
        columnChange.setColumnDataType(tableColumn.getFormattedDataType());
        
        return generateStatements(columnChange, database);
    }
    
    private String generateStatements(Change change, Database database) {

        StringBuilder sb = new StringBuilder();

        try {
         
            SqlStatement[] statements = change.generateStatements(database);

            for (SqlStatement statement : statements) {

                sb.append(statement.getSqlStatement(database));
                sb.append(END_DELIMITER);
                sb.append("\n");
            }

        } catch (UnsupportedChangeException e) {

            handleAndRethrowException(e);
            
        } catch (StatementNotSupportedOnDatabaseException e) {

            handleAndRethrowException(e);
        }
        
        return sb.toString();
    }

    private ColumnConfig createColumn(DatabaseColumn column) {

        ColumnConfig columnConfig = new EqColumnConfig();
        
        columnConfig.setName(column.getName() == null ? "" : column.getName());
        columnConfig.setType(column.getFormattedDataType());
        columnConfig.setDefaultValue(column.getDefaultValue());
        
        if (column.isRequired()) {

            ConstraintsConfig constraintConfig = new ConstraintsConfig();
            constraintConfig.setNullable(Boolean.FALSE);
            columnConfig.setConstraints(constraintConfig);
        }
        
        return columnConfig;
    }

    private Database databaseFromName(Connection connection, String databaseName) {

        Database database = databaseFactory().createDatabase(databaseName);
        database.setConnection(connection);

        return database;        
    }
    
    private LiquibaseDatabaseFactory databaseFactory() {

        if (databaseFactory == null) {

            databaseFactory = new LiquibaseDatabaseFactory();
        }
        
        return databaseFactory;
    }

    private void handleAndRethrowException(Throwable e) {

        if (Log.isDebugEnabled()) {
            
            Log.error("Error generating SQL statement", e);
        }
        
        throw new ApplicationException(e);
    }
    
}





