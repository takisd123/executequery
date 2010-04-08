package org.executequery.datasource;

import java.sql.Driver;

import org.apache.commons.lang.StringUtils;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.log.Log;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.util.MiscUtils;

public abstract class AbstractConnectionPool implements ConnectionPool {

    private static final DriverLoader DRIVER_LOADER;
    
    static final String PORT = "[port]";
    static final String SOURCE = "[source]";
    static final String HOST = "[host]";
    
    static {
        
        DRIVER_LOADER = new DefaultDriverLoader();
    }
    
    protected final Driver loadDriver(DatabaseDriver databaseDriver) {

        return DRIVER_LOADER.loadDriver(databaseDriver);
    }
    
    protected final String generateUrl(DatabaseConnection databaseConnection) {

        String url = databaseConnection.getURL();
        
        if (StringUtils.isBlank(url)) {
        
            url = databaseConnection.getJDBCDriver().getURL();            
            Log.info("JDBC URL pattern: " + url);
    
            url = replacePart(url, databaseConnection.getHost(), HOST);
            url = replacePart(url, databaseConnection.getPort(), PORT);
            url = replacePart(url, databaseConnection.getSourceName(), SOURCE);
            Log.info("JDBC URL generated: "+url);
    
        } else {
          
            Log.info("Using user specified JDBC URL: " + url);
        }

        return url;
    }

    private String replacePart(String url, String value, String propertyName) {
     
        if (url.contains(propertyName)) {

            if (MiscUtils.isNull(value)) {
            
                handleMissingInformationException();
            }

            String regex = propertyName.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]");
            url = url.replaceAll(regex, value);
        }

        return url;
    }

    private void handleMissingInformationException() {

        throw new DataSourceException(
                "Insufficient information was provided to establish the connection.\n" +
                "Please ensure all required details have been entered.");
    }

    protected final void rethrowAsDataSourceException(Throwable e) {
        
        throw new DataSourceException(e);
    }
    
}
