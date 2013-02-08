/*
 * DefaultDatabaseObject.java
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

package org.executequery.databaseobjects.impl;

import java.util.ArrayList;
import java.util.List;
import org.executequery.databaseobjects.DatabaseColumn;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.sql.StatementGenerator;
import org.executequery.sql.StatementGeneratorFactory;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.util.MiscUtils;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class DefaultDatabaseObject extends AbstractDatabaseObject {

    /** the meta data key name for this object */
    private String metaDataKey;

    /** Creates a new instance of DefaultDatabaseObject */
    public DefaultDatabaseObject(DatabaseHost host) {
        setHost(host);
    }

    /** Creates a new instance of DefaultDatabaseObject */
    public DefaultDatabaseObject(DatabaseHost host, String metaDataKey) {
        setHost(host);
        this.metaDataKey = metaDataKey;
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
     * Propagates the call to getColumns() for TABLE and
     * SYSTEM_TABLE types only.
     * All others will return a null list.
     */
    public List<NamedObject> getObjects() throws DataSourceException {

        if (getType() == SYSTEM_TABLE || getType() == TABLE) {

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

        return null;
    }

    /**
     * Returns the database object type.
     *
     * @return the object type
     */
    public int getType() {

        String key = getMetaDataKey();
        for (int i = 0; i < META_TYPES.length; i++) {

            if (META_TYPES[i].equals(key)) {

                return i;
            }

        }

        // check if this a 'derivative object' -
        // ie. a SYSTEM INDEX is still an INDEX
        for (int i = 0; i < META_TYPES.length; i++) {

            if (MiscUtils.containsWholeWord(key, META_TYPES[i])) {

                return i;
            }

        }

        // ...and if all else fails
        return OTHER;
    }

    protected String toCamelCase(String value) {

        String underscore = "_";
        String _value = value.replaceAll(" ", underscore);

        if (!_value.contains(underscore)) {

            return _value.toLowerCase();
        }

        StringBuilder sb = new StringBuilder();

        String[] parts = _value.split(underscore);
        for (int i = 0; i < parts.length; i++) {

            if (i > 0) {

                sb.append(MiscUtils.firstLetterToUpper(parts[i].toLowerCase()));

            } else {

                sb.append(parts[i].toLowerCase());
            }

        }

        return sb.toString();
    }

    protected String databaseProductName() {

        return getHost().getDatabaseProductName();
    }

    protected StatementGenerator createStatementGenerator() {

        return StatementGeneratorFactory.create();
    }
    
}


