package org.executequery.databaseobjects.impl;

import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseObject;
import org.executequery.databaseobjects.DatabaseView;
import org.executequery.sql.StatementGenerator;
import org.underworldlabs.jdbc.DataSourceException;

public class DefaultDatabaseView extends DefaultDatabaseObject implements DatabaseView {

    public DefaultDatabaseView(DatabaseObject object) {

        this(object.getHost());

        setCatalogName(object.getCatalogName());
        setSchemaName(object.getSchemaName());
        setName(object.getName());
        setRemarks(object.getRemarks());
    }

    public DefaultDatabaseView(DatabaseHost host) {

        super(host, "VIEW");
    }

    public String getCreateSQLText() throws DataSourceException {
        
        StatementGenerator statementGenerator = createStatementGenerator();
        return statementGenerator.viewDefinition(databaseProductName(), this);
    }

    @Override
    public boolean hasSQLDefinition() {

        return true;
    }
    
}
