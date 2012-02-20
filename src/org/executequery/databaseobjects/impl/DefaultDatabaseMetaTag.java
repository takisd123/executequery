/*
 * DefaultDatabaseMetaTag.java
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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.executequery.databaseobjects.DatabaseCatalog;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseMetaTag;
import org.executequery.databaseobjects.DatabaseObject;
import org.executequery.databaseobjects.DatabaseSchema;
import org.executequery.databaseobjects.NamedObject;
import org.underworldlabs.jdbc.DataSourceException;

/**
 * Default meta tag object implementation.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class DefaultDatabaseMetaTag extends AbstractNamedObject 
                                    implements DatabaseMetaTag {
    
    /** the catalog object for this meta tag */
    private DatabaseCatalog catalog;

    /** the schema object for this meta tag */
    private DatabaseSchema schema;
    
    /** the host object for this meta tag */
    private DatabaseHost host;

    /** the meta data key name of this object */
    private String metaDataKey;
    
    /** the child objects of this meta type */
    private List<NamedObject> children;
    
    /** Creates a new instance of DefaultDatabaseMetaTag */
    public DefaultDatabaseMetaTag(DatabaseHost host,
                                  DatabaseCatalog catalog, 
                                  DatabaseSchema schema,
                                  String metaDataKey) {
        this.host = host;
        setCatalog(catalog);
        setSchema(schema);
        this.metaDataKey = metaDataKey;
    }

    /**
     * Returns the db object with the specified name or null if 
     * it does not exist.
     *
     * @param name  the name of the object
     * @return the NamedObject or null if not found
     */
    public NamedObject getNamedObject(String name) throws DataSourceException {
        
        List<NamedObject> objects = getObjects();
        if (objects != null) {
            
            name = name.toUpperCase();
            
            for (NamedObject object : objects) {
                
                if (name.equals(object.getName().toUpperCase())) {
                    
                    return object;
                }
                
            }

        }
        
        return null;
    }
    
    /**
     * Retrieves child objects classified as this tag type.
     * These may be database tables, functions, procedures, sequences,
     * views, etc.
     *
     * @return this meta tag's child database objects.
     */
    public List<NamedObject> getObjects() throws DataSourceException {

        if (!isMarkedForReload() && children != null) {

            return children;
        }

        int type = getSubType();

        if (type != SYSTEM_FUNCTION) {


            if (type == FUNCTION || type == PROCEDURE) {
    
                children = loadFunctionsOrProcedures(type);
    
            } else {

                children = getHost().getTables(getCatalogName(), 
                                               getSchemaName(), 
                                               getMetaDataKey());

                if (children != null && type == TABLE) {

                    // reset as editable tables for a default
                    // connection and meta type TABLE
                    
                    List<NamedObject> _children = 
                            new ArrayList<NamedObject>(children.size());
    
                    for (NamedObject i : children) {
    
                        _children.add(new DefaultDatabaseTable((DatabaseObject)i));
                    }
                    
                    children = _children;
                }

            }

        } else {

            // system functions break down further
            
            children = getSystemFunctionTypes();
        }

        // loop through and add this object as the parent object
        addAsParentToObjects(children);

        return children;
    }

    private void addAsParentToObjects(List<NamedObject> children) {
        
        if (children != null) {

            for (NamedObject i : children) {

                ((DatabaseObject)i).setParent(this);
            }

        }

    }

    private List<NamedObject> loadFunctionsOrProcedures(int type) 
        throws DataSourceException {

        try {
   
            String procTerm = getHost().getDatabaseMetaData().getProcedureTerm();
   
            if (procTerm != null && getName().equalsIgnoreCase(procTerm)) {
   
                // check what the term is - proc or function
                if (type == FUNCTION) {
   
                    return getFunctions();
   
                } else if (type == PROCEDURE) {
   
                    return getProcedures();
                }
   
            }
   
        } catch (SQLException e) {

            throw new DataSourceException(e);
        }
        
        return new ArrayList<NamedObject>(0);
    }

    public boolean hasChildObjects() throws DataSourceException {

        return (getObjects() != null && !getObjects().isEmpty());
    }

    /**
     * Loads the database functions.
     */
    private List<NamedObject> getFunctions() throws DataSourceException {
        ResultSet rs = null;
        try {
            String _catalog = getCatalogName();
            String _schema = getSchemaName();

            DatabaseMetaData dmd = getHost().getDatabaseMetaData();
            rs = dmd.getProcedures(_catalog, _schema, null);

            List<NamedObject> list = new ArrayList<NamedObject>();
            
            if (rs != null) { // informix returns null rs
            
                while (rs.next()) {
                    DefaultDatabaseFunction function = 
                            new DefaultDatabaseFunction(this, rs.getString(3));
                    function.setRemarks(rs.getString(7));
                    list.add(function);
                }

            }
            return list;
        
        } catch (SQLException e) {
          
            logThrowable(e);
            return new ArrayList<NamedObject>(0);

        } finally {

            releaseResources(rs);
        }
    }

    /**
     * Loads the database procedures.
     */
    private List<NamedObject> getProcedures() throws DataSourceException {
        
        ResultSet rs = null;

        try {

            String _catalog = getCatalogName();
            String _schema = getSchemaName();

            DatabaseMetaData dmd = getHost().getDatabaseMetaData();
            rs = dmd.getProcedures(_catalog, _schema, null);

            List<NamedObject> list = new ArrayList<NamedObject>();

            while (rs.next()) {

                DefaultDatabaseProcedure function = 
                        new DefaultDatabaseProcedure(this, rs.getString(3));
                function.setRemarks(rs.getString(7));
                list.add(function);
            }

            return list;

        } catch (SQLException e) {
          
            logThrowable(e);
            return new ArrayList<NamedObject>(0);

        } finally {

            releaseResources(rs);
        }
    }

    /**
     * Loads the system function types.
     */
    private List<NamedObject> getSystemFunctionTypes() 
        throws DataSourceException {

        List<NamedObject> objects = new ArrayList<NamedObject>(3);

        objects.add(new DefaultSystemFunctionMetaTag(
                this, SYSTEM_STRING_FUNCTIONS, "String Functions"));
        
        objects.add(new DefaultSystemFunctionMetaTag(
                this, SYSTEM_NUMERIC_FUNCTIONS, "Numeric Functions"));
        
        objects.add(new DefaultSystemFunctionMetaTag(
                this, SYSTEM_DATE_TIME_FUNCTIONS, "Date/Time Functions"));
        
        return objects;
    }
    
    /**
     * Returns the sub-type indicator of this meta tag - the type this
     * meta tag ultimately represents.
     *
     * @return the sub-type, or -1 if not found/available
     */
    public int getSubType() {

        String key = getMetaDataKey();

        for (int i = 0; i < META_TYPES.length; i++) {
        
            if (META_TYPES[i].equals(key)) {

                return i;
            }

        }

        return -1;
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
     * Returns the name of this object.
     *
     * @return the object name
     */
    public String getName() {
        return getMetaDataKey();
    }

    /**
     * Override to do nothing - name is the meta data key value.
     */
    public void setName(String name) {}

    /**
     * Returns the catalog name or null if there is 
     * no catalog attached.
     */
    private String getCatalogName() {

        DatabaseCatalog _catalog = getCatalog();
        
        if (_catalog != null) {
        
            return _catalog.getName();
        }
        
        return null;
    }
    
    /**
     * Returns the parent catalog object.
     *
     * @return the parent catalog object
     */
    public DatabaseCatalog getCatalog() {
        return catalog;
    }

    /**
     * Returns the schema name or null if there is 
     * no schema attached.
     */
    private String getSchemaName() {

        DatabaseSchema _schema = getSchema();

        if (_schema != null) {
        
            return _schema.getName();
        }
        
        return null;
    }

    /**
     * Returns the parent schema object.
     *
     * @return the parent schema object
     */
    public DatabaseSchema getSchema() {
        return schema;
    }

    /**
     * Returns the parent named object of this object.
     *
     * @return the parent object - catalog or schema
     */
    public NamedObject getParent() {
        return getSchema() == null ? getCatalog() : getSchema();
    }

    /**
     * Returns the database object type.
     *
     * @return the object type
     */
    public int getType() {
        return META_TAG;
    }

    /**
     * Returns the meta data key name of this object.
     *
     * @return the meta data key name.
     */
    public String getMetaDataKey() {
        return metaDataKey;
    }

    /**
     * Does nothing.
     */
    public int drop() throws DataSourceException {
        return 0;
    }

    public void setCatalog(DatabaseCatalog catalog) {
        this.catalog = catalog;
    }

    public void setSchema(DatabaseSchema schema) {
        this.schema = schema;
    }
    
}








