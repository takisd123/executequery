/*
 * AbstractDatabaseObject.java
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

package org.executequery.databaseobjects.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.executequery.databaseobjects.DatabaseColumn;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseObject;
import org.executequery.databaseobjects.TablePrivilege;
import org.underworldlabs.jdbc.DataSourceException;

/**
 * Abstract database object implementation.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1508 $
 * @date     $Date: 2009-04-07 21:02:56 +1000 (Tue, 07 Apr 2009) $
 */
public abstract class AbstractDatabaseObject extends AbstractNamedObject
                                             implements DatabaseObject {

    /** the host parent object */
    private DatabaseHost host;

    /** the catalog name */
    private String catalogName;

    /** the schema name */
    private String schemaName;

    /** the object's remarks */
    private String remarks;

    /** this objects columns */
    private List<DatabaseColumn> columns;

    /** the data row count */
    private int dataRowCount = -1;

    /** statement object for open queries */
    private Statement statement;

    /**
     * Returns the catalog name parent to this database object.
     *
     * @return the catalog name
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * Sets the parent catalog name to that specified.
     *
     * @param catalog the catalog name
     */
    public void setCatalogName(String catalog) {
        this.catalogName = catalog;
    }

    /**
     * Returns the schema name parent to this database object.
     *
     * @return the schema name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the parent schema name to that specified.
     *
     * @param schema the schema name
     */
    public void setSchemaName(String schema) {
        this.schemaName = schema;
    }

    public String getNamePrefix() {

        String _schema = getSchemaName();

        if (StringUtils.isNotBlank(_schema)) {

        	return _schema;
        }

        return getCatalogName(); // may still be null
    }


    /**
     * Returns the column from this table witt the specified name,
     * or null if the column does not exist.
     *
     * @return the table column
     */
    public DatabaseColumn getColumn(String name) throws DataSourceException {

        List<DatabaseColumn> columns = getColumns();
        for (DatabaseColumn column : columns) {

            if (column.getName().equalsIgnoreCase(name)) {

                return column;
            }

        }

        return null;
    }

    /**
     * Returns the columns (if any) of this object.
     *
     * @return the columns
     */
    public List<DatabaseColumn> getColumns() throws DataSourceException {
        if (!isMarkedForReload() && columns != null) {
            return columns;
        }

        try {
            DatabaseHost host = getHost();
            if (host != null) {
                columns = host.getColumns(getCatalogName(),
                                          getSchemaName(),
                                          getName());

                if (columns != null) {
                    for (DatabaseColumn i : columns) {
                        i.setParent(this);
                    }
                }

            }
        }
        finally {
            setMarkedForReload(false);
        }
        return columns;
    }

    /**
     * Returns the privileges (if any) of this object.
     *
     * @return the privileges
     */
    public List<TablePrivilege> getPrivileges() throws DataSourceException {
        DatabaseHost host = getHost();
        if (host != null) {
            return host.getPrivileges(getCatalogName(),
                                      getSchemaName(),
                                      getName());
        }
        return null;
    }

    /**
     * Returns the parent host object.
     *
     * @return the parent object
     */
    public DatabaseHost getHost() {
        return host;
    }

    /**
     * Sets the host object to that specified.
     *
     * @param host the host object
     */
    public void setHost(DatabaseHost host) {
        this.host = host;
    }

    /**
     * Returns any remarks attached to this object.
     *
     * @return database object remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks to that specified.
     *
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Override to clear the columns.
     */
    public void reset() {
        super.reset();
        dataRowCount = -1;
        columns = null;
    }

    /**
     * Drops this named object in the database.
     *
     * @return drop statement result
     */
    public int drop() throws DataSourceException {

        String queryStart = null;
        int _type = getType();

        switch (_type) {
            case FUNCTION:
                queryStart = "DROP FUNCTION ";
                break;
            case INDEX:
                queryStart = "DROP INDEX ";
                break;
            case PROCEDURE:
                queryStart = "DROP PROCEDURE ";
                break;
            case SEQUENCE:
                queryStart = "DROP SEQUENCE ";
                break;
            case SYNONYM:
                queryStart = "DROP SYNONYM ";
                break;
            case SYSTEM_TABLE:
            case TABLE:
                queryStart = "DROP TABLE ";
                break;
            case TRIGGER:
                queryStart = "DROP TRIGGER ";
                break;
            case VIEW:
                queryStart = "DROP VIEW ";
                break;
            case OTHER:
                throw new DataSourceException(
                        "Dropping objects of this type is not currently supported");
        }

        Statement stmnt = null;

        try {

            Connection connection = getHost().getConnection();
            stmnt = connection.createStatement();

            int result = stmnt.executeUpdate(queryStart + getNameWithPrefixForQuery());
            if (!connection.getAutoCommit()) {

                connection.commit();
            }

            return result;

        } catch (SQLException e) {

            throw new DataSourceException(e);

        } finally {

            releaseResources(stmnt);
        }

    }

    /**
     * Retrieves the data row count for this object (where applicable).
     *
     * @return the data row count for this object
     */
    public int getDataRowCount() throws DataSourceException {

        if (dataRowCount != -1) {

            return dataRowCount;
        }

        ResultSet rs = null;
        Statement stmnt = null;

        try {

           stmnt = getHost().getConnection().createStatement();
           rs = stmnt.executeQuery(recordCountQueryString());

           if (rs.next()) {

               dataRowCount = rs.getInt(1);
           }

           return dataRowCount;

        } catch (SQLException e) {

           throw new DataSourceException(e);

        }  finally {

            releaseResources(stmnt, rs);
        }

    }

    /**
     * Retrieves the data for this object (where applicable).
     *
     * @return the data for this object
     */
    public ResultSet getData() throws DataSourceException {

        ResultSet rs = null;

        try {

            if (statement != null) {

                try {

                    statement.close();

                } catch (SQLException e) {}

            }

            statement = getHost().getConnection().createStatement();
            rs = statement.executeQuery(recordsQueryString());

            return rs;

        } catch (SQLException e) {

            throw new DataSourceException(e);
        }

    }

    public void cancelStatement() {

        if (statement != null) {

            try {

                statement.cancel();
                statement.close();
                statement = null;

            } catch (SQLException e) {

                logThrowable(e);
            }

        }

    }

    private String recordCountQueryString() {

        return "SELECT COUNT(*) FROM " + getNameWithPrefixForQuery();
    }

    private String recordsQueryString() {

        return "SELECT * FROM " + getNameWithPrefixForQuery();
    }

    protected final String getNameWithPrefixForQuery() {

        String prefix = getNamePrefix();

        if (StringUtils.isNotBlank(prefix)) {

            return prefix + "." + getNameForQuery();
        }

        return getNameForQuery();
    }

    public final String getNameForQuery() {

        String name = getName();
        if (name.contains(" ") // eg. access db allows this
                || (isLowerCase(name) && host.storesLowerCaseQuotedIdentifiers())
                || (isUpperCase(name) && host.storesUpperCaseQuotedIdentifiers())
                || (isMixedCase(name) && (host.storesMixedCaseQuotedIdentifiers()
                        || host.supportsMixedCaseQuotedIdentifiers()))) {

            return quotedDatabaseObjectName(name);
        }

        return name;
    }

    private String quotedDatabaseObjectName(String name) {

        String quoteString = getIdentifierQuoteString();
        return quoteString + name + quoteString;
    }

    protected boolean isMixedCase(String value) {

        return value.matches(".*[A-Z].*") && value.matches(".*[a-z].*");
    }

    protected boolean isLowerCase(String value) {

        return value.matches("[^A-Z]*");
    }

    protected boolean isUpperCase(String value) {

        return value.matches("[^a-z]*");
    }

    protected String getIdentifierQuoteString() {
        
        try {

            return getHost().getDatabaseMetaData().getIdentifierQuoteString();

        } catch (SQLException e) {

            logThrowable(e);
            return "\"";
        }
    }
    
}

