package org.executequery.datasource;

import org.underworldlabs.jdbc.DataSourceException;

public abstract class AbstractConnectionPool implements ConnectionPool {

    protected final void rethrowAsDataSourceException(Throwable e) {
        
        throw new DataSourceException(e);
    }
    
}
