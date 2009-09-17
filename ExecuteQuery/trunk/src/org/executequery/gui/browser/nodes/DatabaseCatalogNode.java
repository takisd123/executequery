/*
 * DatabaseCatalogNode.java
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

package org.executequery.gui.browser.nodes;

import java.util.ArrayList;
import java.util.List;

import org.executequery.databaseobjects.DatabaseCatalog;
import org.executequery.databaseobjects.DatabaseMetaTag;
import org.executequery.databaseobjects.DatabaseSchema;
import org.underworldlabs.jdbc.DataSourceException;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class DatabaseCatalogNode extends DatabaseObjectNode {
    
    /** the direct descendants of this object */
    private List<DatabaseObjectNode> children;
    
    /** Creates a new instance of DatabaseCatalogNode */
    public DatabaseCatalogNode(DatabaseCatalog catalog) {
        super(catalog);
    }

    /**
     * Returns the children associated with this node.
     *
     * @return a list of children for this node
     */
    public List<DatabaseObjectNode> getChildObjects() throws DataSourceException {

        if (children != null) {
            return children;
        }
        
        // check for schemas - then meta tags
        DatabaseCatalog catalog = (DatabaseCatalog)getDatabaseObject();
        
        // check for schemas
        List _children = catalog.getSchemas();
        if (_children == null || _children.isEmpty()) {
            // otherwise get meta tags
            _children = catalog.getMetaObjects();
        }
        else {
            int count = _children.size();
            children = new ArrayList<DatabaseObjectNode>(count);
            for (int i = 0; i < count; i++) {
                DatabaseSchema schema = (DatabaseSchema)_children.get(i);
                children.add(new DatabaseSchemaNode(schema));
            }
            return children;
        }

        // check we have meta tags
        if (_children != null && !_children.isEmpty()) {
            
            int count = _children.size();
            children = new ArrayList<DatabaseObjectNode>(count);
            for (int i = 0; i < count; i++) {
                DatabaseMetaTag metaTag = (DatabaseMetaTag)_children.get(i);
                children.add(new DatabaseObjectNode(metaTag));
            }
            return children;
        }

        return null;
    }

    /**
     * Override to return true.
     */
    public boolean allowsChildren() {
        return true;
    }

    /**
     * Indicates whether this node is a leaf node.
     *
     * @return true | false
     */
    public boolean isLeaf() {
        return false;
    }

    /**
     * Clears out the children of this node.
     */
    public void reset() {
        super.reset();
        children = null;
    }
    
}






