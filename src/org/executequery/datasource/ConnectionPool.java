package org.executequery.datasource;

import java.sql.Connection;

import javax.sql.DataSource;

interface ConnectionPool {

    int getMaximumConnections();
    void setMaximumConnections(int maximumConnections);

    int getMinimumConnections();    
    void setMinimumConnections(int minimumConnections);

    DataSource getDataSource();
    void setDataSource(DataSource dataSource);

    int getMaximumUseCount();
    void setMaximumUseCount(int maximumUseCount);

    boolean isTransactionSupported();

    void setTransactionIsolationLevel(int isolationLevel);

    Connection getConnection();

    void close();
    void close(Connection connection);

    int getSize();

    int getPoolActiveSize();
    
    int getInitialConnections();
    void setInitialConnections(int initialConnections);

}