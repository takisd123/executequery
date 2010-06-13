package org.executequery.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.sql.DataSource;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.log.Log;
import org.underworldlabs.jdbc.DataSourceException;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1521 $
 * @date     $Date: 2009-04-20 02:49:39 +1000 (Mon, 20 Apr 2009) $
 */
public class ConnectionPoolImpl extends AbstractConnectionPool implements PooledConnectionListener {

    private int maximumConnections = MAX_POOL_SIZE;
    
    private int minimumConnections = MIN_POOL_SIZE;
    
    private int initialConnections = INITIAL_POOL_SIZE;

    private final List<PooledConnection> openConnections = new Vector<PooledConnection>();
    
    private final List<PooledConnection> activeConnections = new Vector<PooledConnection>();
    
    private final DatabaseConnection databaseConnection;

    private int defaultTxIsolation = -1;

    private DataSource dataSource;
    
    public ConnectionPoolImpl(DatabaseConnection databaseConnection) {

        this.databaseConnection = databaseConnection;
        
        if (Log.isDebugEnabled()) {

            Log.debug("Creating new pool for connection " + databaseConnection.getName()); 
        }
        
    }

    public DatabaseConnection getDatabaseConnection() {
     
        return databaseConnection;
    }
    
    public void connectionClosed(PooledConnection pooledConnection) {

        if (Log.isDebugEnabled()) {
            
            Log.debug("Removing connection " + pooledConnection.getId() 
                    + " from active connections list");
        }
        
        activeConnections.remove(pooledConnection);
        reduceCapacity(minimumConnections);
    }

    public void close(Connection connection) {

        if (connection != null) {
        
            activeConnections.remove(connection);
    
            PooledConnection pooledConnection = (PooledConnection) connection;
            pooledConnection.destroy();
            
            openConnections.remove(pooledConnection);
        }

        ensureCapacity(minimumConnections);
    }

    public synchronized void close() {

        if (Log.isDebugEnabled()) {
            
            Log.debug("Closing connection pool for connection " + databaseConnection.getName()); 
        }

        for (Connection connection : openConnections) {
            
            PooledConnection pooledConnection = (PooledConnection) connection;
            pooledConnection.destroy();
        }

        activeConnections.clear();
        openConnections.clear();
    }

    public synchronized Connection getConnection() {

        if (openConnections.size() < minimumConnections) {
            
            ensureCapacity(minimumConnections);
        }

        PooledConnection connection = getNextOpenAvailable();
        
        if (connection != null) {
            
            connection.setInUse(true);
            activeConnections.add(connection);

        } else if (openConnections.size() < maximumConnections) {

            createConnection();
            return getConnection();

        } else {

            throw new DataSourceException("Maximum open connection count exceeded");
        }

        if (Log.isDebugEnabled()) {
            
            Log.debug("Retrieving connection " + connection.getId()); 
        }

        return connection;
    }

    private void ensureCapacity(int capacity) {

        if (Log.isDebugEnabled()) {
            
            Log.debug("Ensuring pool capacity " + capacity); 
        }
        
        while (openConnections.size() < capacity) {

            createConnection();
        }
        
    }

    private void reduceCapacity(int capacity) {

        if (Log.isDebugEnabled()) {
            
            Log.debug("Reducing pool capacity " + capacity); 
        }

        while (openConnections.size() > capacity) {
            
            PooledConnection connection = getNextOpenAvailable();
            if (connection != null) {

                close(connection);
            
            } else {
                
                break;
            }

        }

    }

    private PooledConnection createConnection() {

        PooledConnection connection = null;
        
        try {

            if (dataSource == null) {
                
                dataSource = new SimpleDataSource(databaseConnection);
            }

            Connection realConnection = dataSource.getConnection();
            if (realConnection == null) {
                
                throw new DataSourceException(
                        "A connection to the database could not be " +
                		"established.\nPlease ensure that the details " +
                		"are correct and the supplied host is available.");
            }
            
            if (defaultTxIsolation == -1) {
             
                defaultTxIsolation = realConnection.getTransactionIsolation();
            }
            
            int transactionIsolation = databaseConnection.getTransactionIsolation();
            if (transactionIsolation != -1) {
            
                realConnection.setTransactionIsolation(databaseConnection.getTransactionIsolation());
            }

            connection = new PooledConnection(realConnection);
            connection.addPooledConnectionListener(this);

            openConnections.add(connection);
            
            if (Log.isDebugEnabled()) {

                Log.debug("Added new connection to the pool - " + connection.getId()); 
            }
            
        } catch (SQLException e) {

            rethrowAsDataSourceException(e);
        }

        return connection;
    }

    private PooledConnection getNextOpenAvailable() {
        
        for (PooledConnection pooledConnection : openConnections) {
            
            if (pooledConnection.isAvailable()) {
                
                return pooledConnection;
            }
            
        }
        
        return null;
    }
    
    public DataSource getDataSource() {

        return dataSource;
    }

    public int getMaximumConnections() {

        return maximumConnections;
    }

    public int getMaximumUseCount() {

        return 0;
    }

    public int getMinimumConnections() {
        
        return minimumConnections;
    }

    public int getPoolActiveSize() {

        return activeConnections.size();
    }

    public int getSize() {

        return openConnections.size();
    }

    public boolean isTransactionSupported() {

        return false;
    }

    public void setDataSource(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public int getInitialConnections() {
        
        return initialConnections;
    }
    
    public void setInitialConnections(int initialConnections) {

        if (initialConnections < 1) {
            
            throw new IllegalArgumentException("Initial connection count must be at least 1");
        }

        this.initialConnections = initialConnections;
    }
    
    public void setMaximumConnections(int maximumConnections) {

        if (maximumConnections < 1) {
            
            throw new IllegalArgumentException("Maximum connection count must be at least 1");
        }

        this.maximumConnections = maximumConnections;
    }

    public void setMaximumUseCount(int maximumUseCount) {


    }

    public void setMinimumConnections(int minimumConnections) {

        if (minimumConnections < 1) {
            
            throw new IllegalArgumentException("Minimum connection count must be at least 1");
        }
        
        this.minimumConnections = minimumConnections;
        ensureCapacity(minimumConnections);
    }

    public void setTransactionIsolationLevel(int isolationLevel) {

        if (!isTransactionSupported()) {

            return;
        }
        
        if (isolationLevel == -1) {
         
            isolationLevel = defaultTxIsolation;
        }

        try {
        
            for (Connection connection : openConnections) {

                if (!connection.isClosed()) {
                
                    connection.setTransactionIsolation(databaseConnection.getTransactionIsolation());
                }

            }

        } catch (SQLException e) {
            
            throw new DataSourceException(e);
        }
        
    }

}
