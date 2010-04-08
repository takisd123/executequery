package org.executequery.datasource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.executequery.databasemediators.DatabaseConnection;
import org.underworldlabs.jdbc.DataSourceException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3poConnectionPool extends AbstractConnectionPool {

    private final ComboPooledDataSource dataSource = new ComboPooledDataSource();
    
    private final DatabaseConnection databaseConnection;

    public C3poConnectionPool(DatabaseConnection databaseConnection) {

        this.databaseConnection = databaseConnection;
        Driver driver = loadDriver(databaseConnection.getJDBCDriver());
        
        try {

            dataSource.setDriverClass(driver.getClass().getName());
            dataSource.setJdbcUrl(generateUrl(databaseConnection));

            dataSource.setUser(databaseConnection.getUserName());
            dataSource.setPassword(databaseConnection.getUnencryptedPassword());

        } catch (PropertyVetoException e) {

            rethrowAsDataSourceException(e);
        }

    }
    
    public DatabaseConnection getDatabaseConnection() {
     
        return databaseConnection;
    }
    
    public void close(Connection connection) {

        try {
        
            connection.close();

        } catch (SQLException e) {
            
            rethrowAsDataSourceException(e);
        }

    }

    public void close() {

        dataSource.close();        
    }

    public Connection getConnection() {

        Connection connection = null;
        
        try {

            connection = dataSource.getConnection();
            
        } catch (SQLException e) {

            rethrowAsDataSourceException(e);
        }

        return connection;
    }

    public DataSource getDataSource() {

        return dataSource;
    }

    public int getMaximumConnections() {

        return dataSource.getMaxPoolSize();
    }

    public int getMaximumUseCount() {

        return 0;
    }

    public int getMinimumConnections() {

        return dataSource.getMinPoolSize();
    }

    public int getPoolActiveSize() {

        int activeSize = 0;
        
        try {
        
            activeSize = dataSource.getNumConnectionsDefaultUser();

        } catch (SQLException e) {

            rethrowAsDataSourceException(e);
        }
        
        return activeSize;
    }

    public int getSize() {

        return 0;
    }

    public boolean isTransactionSupported() {

        return false;
    }

    public void setDataSource(DataSource dataSource) {
        
        // not allowed here 
    }

    public int getInitialConnections() {

        return dataSource.getInitialPoolSize();
    }
    
    public void setInitialConnections(int initialConnections) {

        if (initialConnections < 1) {
            
            throw new IllegalArgumentException("Initial connection count must be at least 1");
        }

        dataSource.setInitialPoolSize(initialConnections);
    }
    
    public void setMaximumConnections(int maximumConnections) {

        if (maximumConnections < 1) {
            
            throw new IllegalArgumentException("Maximum connection count must be at least 1");
        }

        dataSource.setMaxPoolSize(maximumConnections);
    }

    public void setMaximumUseCount(int maximumUseCount) {


    }

    public void setMinimumConnections(int minimumConnections) {

        if (minimumConnections < 1) {
            
            throw new IllegalArgumentException("Minimum connection count must be at least 1");
        }
        
        dataSource.setMinPoolSize(minimumConnections);
    }

    public void setTransactionIsolationLevel(int isolationLevel)
            throws DataSourceException {

        
    }
    
}
