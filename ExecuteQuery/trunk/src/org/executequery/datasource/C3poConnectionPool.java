package org.executequery.datasource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.executequery.databasemediators.DatabaseConnection;
import org.underworldlabs.jdbc.DataSourceException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3poConnectionPool extends AbstractConnectionPool {

    private static final int ACQUIRE_INCREMENT = 1;

    private final List<Connection> activeConnections = new ArrayList<Connection>();
    
    private final ComboPooledDataSource dataSource = new ComboPooledDataSource();
    
    private final DatabaseConnection databaseConnection;

    private int defaultTxIsolation;
    
    private Properties properties = new Properties();
    
    public C3poConnectionPool(DatabaseConnection databaseConnection) {

        this.databaseConnection = databaseConnection;
        Driver driver = loadDriver(databaseConnection.getJDBCDriver());
        
        try {

            dataSource.setDriverClass(driver.getClass().getName());
            dataSource.setJdbcUrl(generateUrl(databaseConnection));

            dataSource.setUser(databaseConnection.getUserName());
            dataSource.setPassword(databaseConnection.getUnencryptedPassword());

            dataSource.setAcquireIncrement(ACQUIRE_INCREMENT);

            if (databaseConnection.hasAdvancedProperties()) {
                
                populateAdvancedProperties();
            }

            dataSource.setProperties(properties);
            
            Connection connection = dataSource.getConnection();
            connection.close();
            
        } catch (PropertyVetoException e) {

            rethrowAsDataSourceException(e);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    @SuppressWarnings("unchecked")
    private void populateAdvancedProperties() {

        Properties advancedProperties = databaseConnection.getJdbcProperties();
        
        for (Iterator i = advancedProperties.keySet().iterator(); i.hasNext();) {

            String key = (String) i.next();
            properties.put(key, advancedProperties.getProperty(key));
        }
        
    }

    public DatabaseConnection getDatabaseConnection() {
     
        return databaseConnection;
    }
    
    public void close(Connection connection) {

        try {

            activeConnections.remove(connection);
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
            defaultTxIsolation = connection.getTransactionIsolation();
            connection.setTransactionIsolation(databaseConnection.getTransactionIsolation());

            activeConnections.add(connection);
            
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

        return activeConnections.size();
    }

    public int getSize() {

        int size = 0;
        try {
        
            size = dataSource.getNumConnectionsDefaultUser();

        } catch (SQLException e) {

            rethrowAsDataSourceException(e);
        }
        
        return size;
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

    public void setTransactionIsolationLevel(int isolationLevel) {

        if (!isTransactionSupported()) {

            return;
        }
        
        if (isolationLevel == -1) {
         
            isolationLevel = defaultTxIsolation;
        }

        try {
        
            for (Connection connection : activeConnections) {

                if (!connection.isClosed()) {
                
                    connection.setTransactionIsolation(databaseConnection.getTransactionIsolation());
                }

            }

        } catch (SQLException e) {
            
            throw new DataSourceException(e);
        }
        
    }
    
}
