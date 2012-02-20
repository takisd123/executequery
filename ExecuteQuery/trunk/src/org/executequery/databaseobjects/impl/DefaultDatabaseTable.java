/*
 * DefaultDatabaseTable.java
 *
 * Copyright (C) 2002-2012 Takis Diakoumis
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

package org.executequery.databaseobjects.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.executequery.databaseobjects.DatabaseColumn;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseObject;
import org.executequery.databaseobjects.DatabaseSource;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.sql.SQLFormatter;
import org.executequery.sql.StatementGenerator;
import org.executequery.sql.StatementGeneratorFactory;
import org.underworldlabs.jdbc.DataSourceException;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class DefaultDatabaseTable extends DefaultDatabaseObject implements DatabaseTable {

    /** the table columns */
    private List<DatabaseColumn> columns;

    /** the table columns exported */
    private List<DatabaseColumn> exportedColumns;

    /** the table indexed columns */
    private List<TableColumnIndex> indexes;

    /** the user modified SQL text for changes */
    private String modifiedSQLText;

    /** Creates a new instance of DatabaseTable */
    public DefaultDatabaseTable(DatabaseObject object) {

        this(object.getHost());

        setCatalogName(object.getCatalogName());
        setSchemaName(object.getSchemaName());
        setName(object.getName());
        setRemarks(object.getRemarks());
    }

    /** Creates a new instance of DatabaseTable */
    public DefaultDatabaseTable(DatabaseHost host) {
        super(host, "TABLE");
    }

    /**
     * Propagates the call to getColumns().
     */
    public List<NamedObject> getObjects() throws DataSourceException {

        List<DatabaseColumn> _columns = getColumns();
        if (_columns == null) {

            return null;
        }

        List<NamedObject> objects = new ArrayList<NamedObject>(_columns.size());
        for (DatabaseColumn i : _columns) {

            objects.add(i);
        }

        return objects;
    }

    public List<DatabaseColumn> getExportedKeys() throws DataSourceException {

        if (!isMarkedForReload() && exportedColumns != null) {

            return exportedColumns;
        }

        if (exportedColumns != null) {

            exportedColumns.clear();
            exportedColumns = null;
        }

        DatabaseHost host = getHost();
        if (host != null) {

            exportedColumns = host.getExportedKeys(getCatalogName(),
                                                   getSchemaName(),
                                                   getName());
        }

        return exportedColumns;
    }

    public boolean hasReferenceTo(DatabaseTable anotherTable) {

        /*
        try {

            DatabaseMetaData metaData = getHost().getDatabaseMetaData();

            int FKTABLE_NAME_INDEX = 3;

            ResultSet rs = metaData.getImportedKeys(getCatalogName(), getSchemaName(), getName());

            try {

                String anotherTableName = anotherTable.getName();

                while (rs.next()) {

                    System.out.println("table: " + getName() +
                            " fktable: "+rs.getString(FKTABLE_NAME_INDEX));

                    if (anotherTableName.equalsIgnoreCase(
                            rs.getString(FKTABLE_NAME_INDEX))) {

                        return true;
                    }

                }

            } finally {

                if (rs != null) {

                    rs.close();
                }

            }

            return false;

        } catch (SQLException e) {

            throw new DataSourceException(e);
        }

        */

        List<ColumnConstraint> constraints = getConstraints();

        String anotherTableName = anotherTable.getName();

        for (ColumnConstraint constraint : constraints) {

            if (constraint.isForeignKey()) {

                if (constraint.getReferencedTable().equals(anotherTableName)) {

                    return true;
                }

            }

        }

        return false;

    }

    /**
     * Returns the column count of this table.
     *
     * @return the column count
     */
    public int getColumnCount() throws DataSourceException {

        return getColumns().size();
    }

    /**
     * Returns the columns of this table.
     *
     * @return the columns
     */
    public List<DatabaseColumn> getColumns() throws DataSourceException {

        if (!isMarkedForReload() && columns != null) {

            return columns;
        }

        // otherwise cleanup existing references
        if (columns != null) {

            columns.clear();
            columns = null;
        }

        DatabaseHost host = getHost();
        if (host != null) {

            ResultSet rs = null;
            try {

                List<DatabaseColumn> _columns =
                        host.getColumns(getCatalogName(),
                                        getSchemaName(),
                                        getName());

                if (_columns != null) {

                    columns = new ArrayList<DatabaseColumn>(_columns.size());
                    for (DatabaseColumn i : _columns) {

                        columns.add(new DatabaseTableColumn(this, i));
                    }

                    // reload and define the constraints
                    String _catalog = getCatalogName();
                    String _schema = getSchemaName();
                    DatabaseMetaData dmd = host.getDatabaseMetaData();

                    // check that the db supports catalog and schema names
                    if (!dmd.supportsCatalogsInTableDefinitions()) {

                        _catalog = null;
                    }

                    if (!dmd.supportsSchemasInTableDefinitions()) {

                        _schema = null;
                    }

                    rs = dmd.getPrimaryKeys(_catalog, _schema, getName());
                    while (rs.next()) {

                        String pkColumn = rs.getString(4);

                        for (DatabaseColumn i : columns) {

                            if (i.getName().equalsIgnoreCase(pkColumn)) {

                                DatabaseTableColumn column = (DatabaseTableColumn)i;
                                TableColumnConstraint constraint =
                                        new TableColumnConstraint(
                                            column, ColumnConstraint.PRIMARY_KEY);

                                constraint.setName(rs.getString(6));
                                column.addConstraint(constraint);
                                break;

                            }
                        }
                    }
                    rs.close();

                    try {

                    // TODO: XXX

                    // sapdb amd maxdb dump on imported/exported keys
                    // surround with try/catch hack to get at least a columns list

                    rs = dmd.getImportedKeys(_catalog, _schema, getName());
                    while (rs.next()) {

                        String fkColumn = rs.getString(8);

                        for (DatabaseColumn i : columns) {

                            if (i.getName().equalsIgnoreCase(fkColumn)) {

                                DatabaseTableColumn column = (DatabaseTableColumn)i;
                                TableColumnConstraint constraint =
                                        new TableColumnConstraint(
                                            column, ColumnConstraint.FOREIGN_KEY);

                                constraint.setReferencedCatalog(rs.getString(1));
                                constraint.setReferencedSchema(rs.getString(2));
                                constraint.setReferencedTable(rs.getString(3));
                                constraint.setReferencedColumn(rs.getString(4));
                                constraint.setName(rs.getString(12));
                                column.addConstraint(constraint);
                                break;

                            }
                        }
                    }

                    } catch (SQLException e) {}
                }

            } catch (DataSourceException e) {

                // catch and re-throw here to create
                // an empty column list so we don't
                // keep hitting the same error
                columns = new ArrayList<DatabaseColumn>(0);
                throw e;

            } catch (SQLException e) {

                // catch and re-throw here to create
                // an empty column list so we don't
                // keep hitting the same error
                columns = new ArrayList<DatabaseColumn>(0);
                throw new DataSourceException(e);

            } finally {

                releaseResources(rs);
                setMarkedForReload(false);
            }

        }
        return columns;
    }

    /**
     * Returns the columns of this table.
     *
     * @return the columns
     */
    public List<ColumnConstraint> getConstraints() throws DataSourceException {

        if (getColumns() != null) {

            List<ColumnConstraint> constraints = new ArrayList<ColumnConstraint>();

            for (DatabaseColumn i : columns) {

                DatabaseTableColumn column = (DatabaseTableColumn)i;

                if (column.hasConstraints()) {

                    List<ColumnConstraint> columnConstraints = column.getConstraints();
                    for (ColumnConstraint j : columnConstraints) {

                        constraints.add(j);
                    }

                }

            }

            return constraints;

        } else {

            return new ArrayList<ColumnConstraint>(0);
        }
    }

    /**
     * Returns the indexes of this table.
     *
     * @return the indexes
     */
    public List<TableColumnIndex> getIndexes() throws DataSourceException {

        if (!isMarkedForReload() && indexes != null) {

            return indexes;
        }

        ResultSet rs = null;
        try {
            DatabaseHost _host = getHost();
            rs = _host.getDatabaseMetaData().getIndexInfo(
                    getCatalogName(), getSchemaName(), getName(), false, true);

            indexes = new ArrayList<TableColumnIndex>();
            while (rs.next()) {

                String name = rs.getString(6);

                if (StringUtils.isBlank(name)) {

                    continue;
                }

                TableColumnIndex index = new TableColumnIndex(name);

                index.setNonUnique(rs.getBoolean(4));
                index.setIndexedColumn(rs.getString(9));
                indexes.add(index);
            }

            return indexes;

        } catch (DataSourceException e) {

            // catch and re-throw here to create
            // an empty index list so we don't
            // keep hitting the same error
            indexes = new ArrayList<TableColumnIndex>(0);
            throw e;

        } catch (SQLException e) {

            // catch and re-throw here to create
            // an empty index list so we don't
            // keep hitting the same error
            indexes = new ArrayList<TableColumnIndex>(0);
            throw new DataSourceException(e);

        } finally {

            releaseResources(rs);
            setMarkedForReload(false);
        }
    }

    /**
     * Returns this table's column meta data result set.
     *
     * @return the column meta data result set
     */
    public ResultSet getColumnMetaData() throws DataSourceException {
        try {
            String _catalog = getCatalogName();
            String _schema = getSchemaName();
            DatabaseMetaData dmd = getHost().getDatabaseMetaData();

            // check that the db supports catalog and schema names
            if (!dmd.supportsCatalogsInTableDefinitions()) {
                _catalog = null;
            }

            if (!dmd.supportsSchemasInTableDefinitions()) {
                _schema = null;
            }
            return dmd.getColumns(_catalog, _schema, getName(), null);
        }
        catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }

    /**
     * Returns the database object type.
     *
     * @return the object type
     */
    public int getType() {
        return TABLE;
    }

    /**
     * Returns the meta data key name of this object.
     *
     * @return the meta data key name.
     */
    public String getMetaDataKey() {
        return "TABLE";
    }

    /**
     * Override to clear the columns.
     */
    public void reset() {
        super.reset();
        modifiedSQLText = null;
        clearColumns();
        clearIndexes();
    }

    private void clearColumns() {
        if (columns != null) {
            columns.clear();
        }
        columns = null;
    }

    private void clearIndexes() {
        if (indexes != null) {
            indexes.clear();
        }
        indexes = null;
    }

    /**
     * Reverts any changes made to this table and associated elements.
     */
    public void revert() {

        List<DatabaseColumn> newColumns = new ArrayList<DatabaseColumn>();

        for (DatabaseColumn i : columns) {

            DatabaseTableColumn column = (DatabaseTableColumn)i;

            if (!column.isNewColumn()) {

                column.revert();

            } else {

                newColumns.add(column);
            }

        }

        for (DatabaseColumn column : newColumns) {

            columns.remove(column);
        }

        newColumns.clear();
        modifiedSQLText = null;
    }

    /**
     * Applies any changes to the database.
     */
    public int applyChanges() throws DataSourceException {

        Statement stmnt = null;

        try {

            String changes = getModifiedSQLText();
            if (StringUtils.isBlank(changes)) {

                // bail if we're empty here

                return 1;
            }

            int result = 0;
            String[] queries = changes.split(StatementGenerator.END_DELIMITER);

            Connection connection = getHost().getConnection();
            stmnt = connection.createStatement();

            for (int i = 0; i < queries.length; i++) {

                String query = queries[i].trim();
                if (StringUtils.isNotBlank(query)) {

                    result += stmnt.executeUpdate(query);
                }

            }

            if (!connection.getAutoCommit()) {

                connection.commit();
            }

            // set to reset for the next call
            reset();

            return result;

        } catch (SQLException e) {
            
            throw new DataSourceException(e);

        } finally {
            
            releaseResources(stmnt);
        }
    }

    /**
     * Indicates whether this table or any of its columns
     * or constraints have pending modifications to be applied.
     *
     * @return true | false
     */
    public boolean isAltered() throws DataSourceException {

        List<DatabaseColumn> _columns = getColumns();

        if (_columns != null) {

            for (DatabaseColumn i : _columns) {

                DatabaseTableColumn column = (DatabaseTableColumn)i;

                if (column.hasChanges()) {

                    return true;
                }

            }
        }

        List<ColumnConstraint> constraints = getConstraints();

        if (constraints != null) {

            for (ColumnConstraint i : constraints) {

                if (i.isNewConstraint() || i.isAltered()) {

                    return true;
                }

            }

        }

        return false;
    }

    /**
     * Returns the ALTER TABLE statement to modify this constraint.
     */
    public String getAlteredSQLText() throws DataSourceException {

        StringBuilder sb = new StringBuilder();

        // retrieve column alter changes
        List<DatabaseColumn> _columns = getColumns();

        if (_columns != null) {

            StatementGenerator statementGenerator = createStatementGenerator();
            String columnsAlter = statementGenerator.alterTable(databaseProductName(), this);
            sb.append(columnsAlter);
        }

        // retrieve constraint changes
        /*
        List<ColumnConstraint> constraints = getConstraints();
        if (constraints != null) {
            for (ColumnConstraint i : constraints) {
                if (i.isNewConstraint() || i.isAltered()) {
                    sb.append(i.getAlteredSQLText());
                    sb.append("\n");
                }
            }
        }
        */

        return sb.toString();
    }

    public String getCreateSQLText() throws DataSourceException {

        return getCreateSQLText(STYLE_CONSTRAINTS_ALTER);
    }

    public String getDropSQLText(boolean cascadeConstraints) {

        StatementGenerator statementGenerator = createStatementGenerator();

        String databaseProductName = databaseProductName();

        String dropStatement = null;

        if (cascadeConstraints) {

            dropStatement = statementGenerator.dropTableCascade(databaseProductName, this);

        } else {

            dropStatement = statementGenerator.dropTable(databaseProductName, this);
        }

        return dropStatement;
    }

    public List<ColumnConstraint> getPrimaryKeys() {

        List<ColumnConstraint> primaryKeys = new ArrayList<ColumnConstraint>();

        List<ColumnConstraint> _constraints = getConstraints();

        for (int i = 0, n = _constraints.size(); i < n; i++) {

            ColumnConstraint columnConstraint = _constraints.get(i);

            if (columnConstraint.isPrimaryKey()) {

                primaryKeys.add(columnConstraint);
            }

        }

        return primaryKeys;
    }

    public List<ColumnConstraint> getForeignKeys() {

        List<ColumnConstraint> foreignKeys = new ArrayList<ColumnConstraint>();

        List<ColumnConstraint> _constraints = getConstraints();

        for (int i = 0, n = _constraints.size(); i < n; i++) {

            ColumnConstraint columnConstraint = _constraints.get(i);

            if (columnConstraint.isForeignKey()) {

                foreignKeys.add(columnConstraint);
            }

        }

        return foreignKeys;
    }

    public List<ColumnConstraint> getUniqueKeys() {

        List<ColumnConstraint> uniqueKeys = new ArrayList<ColumnConstraint>();

        List<ColumnConstraint> _constraints = getConstraints();

        for (int i = 0, n = _constraints.size(); i < n; i++) {

            ColumnConstraint columnConstraint = _constraints.get(i);

            if (columnConstraint.isUniqueKey()) {

                uniqueKeys.add(columnConstraint);
            }

        }

        return uniqueKeys;
    }

    public String getAlterSQLTextForUniqueKeys() {

        StatementGenerator statementGenerator = createStatementGenerator();

        return statementGenerator.createUniqueKeyChange(databaseProductName(), this);
    }

    public String getAlterSQLTextForForeignKeys() {

        StatementGenerator statementGenerator = createStatementGenerator();

        return statementGenerator.createForeignKeyChange(databaseProductName(), this);
    }

    public String getAlterSQLTextForPrimaryKeys() {

        StatementGenerator statementGenerator = createStatementGenerator();

        return statementGenerator.createPrimaryKeyChange(databaseProductName(), this);
    }

    public String getCreateConstraintsSQLText() throws DataSourceException {

        StatementGenerator statementGenerator = createStatementGenerator();

        String databaseProductName = databaseProductName();

        return statementGenerator.tableConstraintsAsAlter(databaseProductName, this);
    }

    /**
     * Returns the CREATE TABLE statement for this database table.
     * This will be table column (plus data type) definitions only,
     * this does not include constraint meta data.
     */
    public String getCreateSQLText(int style) throws DataSourceException {

        StatementGenerator statementGenerator = createStatementGenerator();

        String databaseProductName = databaseProductName();

        if (style == STYLE_CONSTRAINTS_DEFAULT) {

            String createStatement =
                statementGenerator.createTableWithConstraints(databaseProductName, this);

            return formatSqlText(createStatement);

        } else if (style == STYLE_CONSTRAINTS_ALTER) {

            String createStatement = statementGenerator.createTable(databaseProductName, this);

            StringBuilder sb = new StringBuilder();

            sb.append(formatSqlText(createStatement));
            sb.append("\n\n");
            sb.append(statementGenerator.tableConstraintsAsAlter(databaseProductName, this));

            return sb.toString();

        } else {

            String createStatement = statementGenerator.createTable(databaseProductName, this);

            return formatSqlText(createStatement);
        }

    }

    private String formatSqlText(String text) {

        return new SQLFormatter(text).format();
    }

    private String databaseProductName() {

        return getHost().getDatabaseProductName();
    }

    private StatementGenerator createStatementGenerator() {

        return StatementGeneratorFactory.create();
    }

    /**
     * Returns the CREATE TABLE statement for this database table.
     * This will be table column (plus data type) definitions only,
     * this does not include constraint meta data.
     */
    public String getCreateSQLTextX(int style) throws DataSourceException {

        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ");
        sb.append(getName());
        sb.append(" (");

        // determine the spaces from the left side to each column name
        String firstIndent = getSpacesForLength(sb.length());

        // determine the spaces from the column name to the data type
        int maxLength = 0;
        for (DatabaseColumn i : columns) {
            DatabaseTableColumn column = (DatabaseTableColumn)i;
            maxLength = Math.max(maxLength, column.getName().length());
        }
        // add another 5 spaces from the max
        maxLength += 5;

        int secondIndentLength = 0;
        for (int i = 0, n = columns.size(); i < n; i++) {
            DatabaseTableColumn column = (DatabaseTableColumn)columns.get(i);

            if (i > 0) {
                sb.append(firstIndent);
            }

            String columnName = column.getName();
            sb.append(columnName.toUpperCase());

            secondIndentLength = maxLength - columnName.length();
            for (int j = 0; j < secondIndentLength; j++) {
                sb.append(" ");
            }

            sb.append(column.getFormattedDataType());

            if (StringUtils.isNotBlank(column.getDefaultValue())) {
                sb.append(" DEFAULT ");
                sb.append(column.getDefaultValue());
            }

            if (column.isRequired()) {
                sb.append(" NOT NULL");
            }

            if (i < (n-1)) {
                sb.append(",\n");
            }

        }

        if (style == STYLE_CONSTRAINTS_DEFAULT) {
            sb.append(",\n");
            List<ColumnConstraint> constraints = getConstraints();
            for (int i = 0, n = constraints.size(); i < n; i++) {
                TableColumnConstraint constraint =
                        (TableColumnConstraint)constraints.get(i);
                sb.append(firstIndent);
                sb.append(constraint.getConstraintSQLText());

                if (i < (n-1)) {
                    sb.append(",\n");
                }

            }
            sb.append(");\n");
        }
        else if (style == STYLE_CONSTRAINTS_ALTER) {

            sb.append(");\n\n");
            List<ColumnConstraint> constraints = getConstraints();

            for (ColumnConstraint i : constraints) {

                TableColumnConstraint constraint = (TableColumnConstraint)i;

                sb.append(constraint.getCreateSQLText());
                sb.append("\n");
            }

        } else {

            // finish off the statement as is
            sb.append(");\n");
        }

        return sb.toString();
    }

    /**
     * Returns the user modified SQL text to apply
     * any pending changes. If this has not been set (no
     * changes were made) then a call to getAlteredSQLText()
     * is made.
     *
     * @return the modified SQL
     */
    public String getModifiedSQLText() throws DataSourceException {
        if (modifiedSQLText == null) {
            return getAlteredSQLText();
        }
        return modifiedSQLText;
    }

    public void setModifiedSQLText(String modifiedSQLText) {
        this.modifiedSQLText = modifiedSQLText;
    }

    public String getInsertSQLText() {

        try {

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ");
            sb.append(getNameForQuery());
            sb.append(" (");

            String indent = getSpacesForLength(sb.length());

            List<DatabaseColumn> _columns = getColumns();

            for (int i = 0, n = _columns.size(); i < n; i++) {

                DatabaseTableColumn column = (DatabaseTableColumn)_columns.get(i);

                sb.append(column.getNameForQuery());

                if (i < n - 1) {
                    sb.append(",\n");
                    sb.append(indent);
                }

            }

            sb.append(")\n");

            String valuesString = "VALUES (";
            sb.append(valuesString);

            indent = getSpacesForLength(valuesString.length());

            for (int i = 0, n = _columns.size(); i < n; i++) {
                DatabaseTableColumn column = (DatabaseTableColumn)_columns.get(i);

                sb.append(columnAsValueString(column.getName()));

                if (i < n - 1) {
                    sb.append(",\n");
                    sb.append(indent);
                }

            }

            sb.append(");\n");

            return sb.toString();

        } catch (DataSourceException e) {

            logThrowable(e);
            return "";
        }
    }

    public String getUpdateSQLText() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ");
            sb.append(getNameForQuery());

            String setString = "SET ";
            sb.append("\n");
            sb.append(setString);

            String indent = getSpacesForLength(setString.length());

            List<DatabaseColumn> _columns = getColumns();

            for (int i = 0, n = _columns.size(); i < n; i++) {
                DatabaseTableColumn column = (DatabaseTableColumn)_columns.get(i);

                sb.append(column.getNameForQuery());
                sb.append(" = ");
                sb.append(columnAsValueString(column.getName()));

                if (i < n - 1) {
                    sb.append(",\n");
                    sb.append(indent);
                }

            }

            sb.append(";\n");

            return sb.toString();
        }
        catch (DataSourceException e) {
            
            logThrowable(e);
            return "";
        }
    }

    public String getSelectSQLText() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");

            String indent = getSpacesForLength(sb.length());

            List<DatabaseColumn> _columns = getColumns();

            for (int i = 0, n = _columns.size(); i < n; i++) {
                DatabaseTableColumn column = (DatabaseTableColumn)_columns.get(i);

                sb.append(column.getNameForQuery());

                if (i < n - 1) {
                    sb.append(",\n");
                    sb.append(indent);
                }

            }

            sb.append("\nFROM ");
            sb.append(getNameForQuery());
            sb.append(";\n");

            return sb.toString();

        } catch (DataSourceException e) {

            logThrowable(e);
            return "";
        }

    }

    private String columnAsValueString(String column) {

        return toCamelCase(column);
    }

    private String getSpacesForLength(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public DatabaseSource getDatabaseSource() {

        if (getParent() != null) {

            return (DatabaseSource) getParent().getParent();
        }

        return null;
    }

    public String getParentNameForStatement() {

        if (getParent() != null && getParent().getParent() != null) {

            return getParent().getParent().getName();
        }

        return null;
    }

}



