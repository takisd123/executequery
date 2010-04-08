/*
 * ConnectionManager.java
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

package org.executequery.datasource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.log.Log;
import org.executequery.repository.DatabaseDriverRepository;
import org.executequery.repository.RepositoryCache;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.util.SystemProperties;

/**
 * Manages all data source connections across multiple
 * sources and associated connection pools.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1525 $
 * @date     $Date: 2009-05-17 12:40:04 +1000 (Sun, 17 May 2009) $
 */
public final class ConnectionManager {
    
    /** the connection 'container' */
    private static Map<DatabaseConnection, ConnectionPool> connectionPools;
    
    /** 
     * Creates a stored data source for the specified database
     * connection properties object.
     *
     * @param the database connection properties object
     */
    public static synchronized void createDataSource(DatabaseConnection databaseConnection) {
        
        // check the connection has a driver
        if (databaseConnection.getJDBCDriver() == null) {
            
            long driverId = databaseConnection.getDriverId();
            DatabaseDriver driver = driverById(driverId);

            if (driver != null) {

                databaseConnection.setJDBCDriver(driver);

            } else {

                throw new DataSourceException("No JDBC driver specified");
            }

        }

        Log.info("Initialising data source for " + databaseConnection.getName());

        DataSource dataSource = new ConnectionDataSource(databaseConnection);
        ConnectionPool pool = new DefaultConnectionPool(dataSource);

//        ConnectionPool pool = new C3poConnectionPool(databaseConnection);
        
        //pool.setPoolScheme(SystemProperties.getIntProperty("connection.scheme"));
        pool.setMinimumConnections(SystemProperties.getIntProperty("user", "connection.initialcount"));
        pool.setMaximumConnections(5);
        pool.setInitialConnections(SystemProperties.getIntProperty("user", "connection.initialcount"));
//        pool.ensureCapacity();
        
        // TODO: ?????????????????
        //pool.setMinConns(determineMinimumConnections());

        if (connectionPools == null) {

            connectionPools = new HashMap<DatabaseConnection, ConnectionPool>();
        }

        connectionPools.put(databaseConnection, pool);
        databaseConnection.setConnected(true);
        
        Log.info("Data source " + databaseConnection.getName() +" initialised.");
    }

    /**
     * Returns a connection from the pool of the specified type.
     *
     * @param the stored database connection properties object
     * @return the connection itself
     */
    public static Connection getConnection(DatabaseConnection databaseConnection) throws DataSourceException {

        if (databaseConnection == null) {

            return null;
        }
        
        synchronized (databaseConnection) {
            
            if (connectionPools == null || 
                    !connectionPools.containsKey(databaseConnection)) {

                createDataSource(databaseConnection);
            }

            ConnectionPool pool = connectionPools.get(databaseConnection);
            Connection connection = pool.getConnection();

            return connection;
        }
        
    }

    /**
     * Closes all connections and removes the pool of the specified type.
     *
     * @param the stored database connection properties object
     */
    public static void closeConnection(DatabaseConnection databaseConnection) throws DataSourceException {

        synchronized (databaseConnection) {

            if (connectionPools.containsKey(databaseConnection)) {

                Log.info("Disconnecting from data source " + databaseConnection.getName());
                
                ConnectionPool pool = connectionPools.get(databaseConnection);
                pool.close();

                connectionPools.remove(databaseConnection);
                databaseConnection.setConnected(false);
            }

        }
        
    }

    /**
     * Closes all connections and removes the pool of the specified type.
     *
     * @param the stored database connection properties object
     */
    public static void close() throws DataSourceException {

        if (connectionPools == null || connectionPools.isEmpty()) {
            return;
        }

        // iterate and close all the pools
        for (Iterator<DatabaseConnection> i = 
        		connectionPools.keySet().iterator(); i.hasNext();) {
            ConnectionPool pool = connectionPools.get(i.next());
            pool.close();
        }
        connectionPools.clear();
    }

    /**
     * Retrieves the data source objetc of the specified connection.
     * 
     * @return the data source object
     */
    public static DataSource getDataSource(DatabaseConnection databaseConnection) {
        if (connectionPools == null || 
                !connectionPools.containsKey(databaseConnection)) {
            return null;
        }
        return connectionPools.get(databaseConnection).getDataSource();
    }
    
    /**
     * Sets the transaction isolation level to that specified
     * for <i>all</i> connections in the pool of the specified connection.
     *
     * @param the isolation level
     * @see java.sql.Connection for possible values
     */
    public static void setTransactionIsolationLevel(
                    DatabaseConnection databaseConnection, int isolationLevel) 
        throws DataSourceException {
        if (connectionPools == null || 
                connectionPools.containsKey(databaseConnection)) {
            ConnectionPool pool = connectionPools.get(databaseConnection);
            pool.setTransactionIsolationLevel(isolationLevel);
        }
    }

    /**
     * Returns a collection of database connection property 
     * objects that are active (connected).
     *
     * @return a collection of active connections
     */
    public static Vector<DatabaseConnection> getActiveConnections() {
        if (connectionPools == null || connectionPools.isEmpty()) {
            return new Vector<DatabaseConnection>(0);
        }
        Vector<DatabaseConnection> connections = 
                new Vector<DatabaseConnection>(connectionPools.size());
        for (Iterator<DatabaseConnection> i = 
        		connectionPools.keySet().iterator(); i.hasNext();) {
            connections.add(i.next());
        }
        return connections;
    }
    
    /**
     * Returns the open connection count for the specified connection.
     *
     * @param dc - the connection to be polled
     */
    public static int getOpenConnectionCount(DatabaseConnection dc) {
        ConnectionPool pool = connectionPools.get(dc);
        if (pool != null) {
            return pool.getSize();
        }
        return 0;
    }
    
    public static boolean hasConnections() {
        
        return getActiveConnectionPoolCount() > 0;
    }
    
    /**
     * Returns the number of pools currently active.
     *
     * @return number of active pools
     */
    public static int getActiveConnectionPoolCount() {
        if (connectionPools == null) {
            return 0;
        }
        return connectionPools.size();
    }

    /**
     * Closes the connection completely. The specified connection
     * is not returned to the pool.
     *
     * @param the connection be closed
     */
    public static void close(
            DatabaseConnection databaseConnection, Connection connection)
            throws DataSourceException {
        if (connectionPools == null || connectionPools.isEmpty()) {
            return;
        }
        if (connectionPools.containsKey(databaseConnection)) {            
            ConnectionPool pool = connectionPools.get(databaseConnection);
            pool.close(connection);
        }
    }
    
    /**
     * Returns whether the specified connection [driver] supports transactions.
     *
     * @param databaseConnection the connection to be polled
     * @return true | false
     */
    public static boolean isTransactionSupported(
            DatabaseConnection databaseConnection) {
        if (connectionPools.containsKey(databaseConnection)) {
            ConnectionPool pool = connectionPools.get(databaseConnection);
            return pool.isTransactionSupported();
        }        
        return false;
    }

    private static final int MAX_CONNECTION_USE_COUNT = 50;
    
    /** 
     * Retrieves the maximum use count for each open connection 
     * before being closed.
     *
     * @return the max connection use count
     */
    public static int getMaxUseCount() {

        return MAX_CONNECTION_USE_COUNT;
    }

    private static DatabaseDriver driverById(long driverId) {

        return ((DatabaseDriverRepository) RepositoryCache.load(
                DatabaseDriverRepository.REPOSITORY_ID)).findById(driverId);
    }

    private ConnectionManager() {}
}

