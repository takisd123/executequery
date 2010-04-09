package org.executequery.datasource;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.log.Log;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.util.DynamicLibraryLoader;
import org.underworldlabs.util.MiscUtils;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class DefaultDriverLoader implements DriverLoader {

    private static final Map<DatabaseDriver, Driver> LOADED_DRIVERS = new HashMap<DatabaseDriver, Driver>();
    
    public Driver loadDriver(DatabaseDriver databaseDriver) {

        Driver driver = null;
        
        if (LOADED_DRIVERS.containsKey(databaseDriver)) {
            
            return LOADED_DRIVERS.get(databaseDriver);
        }

        try {
        
            Class<?> clazz = null;
            String driverName = databaseDriver.getClassName();
    
            Log.info("Loading JDBC driver class: " + driverName);
            
            if (!databaseDriver.isDefaultSunOdbc()) {
                
                String path = databaseDriver.getPath();
                
                if (!MiscUtils.isNull(path)) {
    
                    URL[] urls = MiscUtils.loadURLs(path);
                    DynamicLibraryLoader loader = new DynamicLibraryLoader(urls);
                    clazz = loader.loadLibrary(driverName);
                
                } else {
    
                    clazz = Class.forName(driverName, true,
                                          ClassLoader.getSystemClassLoader());
                }
    
            } else {
                
                clazz = Class.forName(driverName, true,
                                      ClassLoader.getSystemClassLoader());
            } 
    
            Object object = clazz.newInstance();
            driver = (Driver) object;
            
            Log.info("JDBC driver " + driverName + " loaded - v" 
                    + driver.getMajorVersion() + "." + driver.getMinorVersion());
            
            LOADED_DRIVERS.put(databaseDriver, driver);
            
        } catch (ClassNotFoundException e) {
            
            handleException("The specified JDBC driver class was not found", databaseDriver, e);
        
        } catch (MalformedURLException e) {

            handleException("Error loading the driver from the specified path.", databaseDriver, e);
            
        } catch (InstantiationException e) {
            
            handleException(e.getMessage(), databaseDriver, e);

        } catch (IllegalAccessException e) {
            
            handleException("The specified JDBC driver class was not accessible", databaseDriver, e);
        }

        return driver;
    }

    private void handleException(String message, DatabaseDriver databaseDriver, Throwable e) {

        if (Log.isDebugEnabled()) {
            
            Log.error("Error loading JDBC driver " + databaseDriver.getClassName(), e);
        }

        throw new DataSourceException(message);
    }
    
}
