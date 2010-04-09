package org.executequery.datasource;

import java.sql.Driver;

import org.executequery.databasemediators.DatabaseDriver;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1521 $
 * @date     $Date: 2009-04-20 02:49:39 +1000 (Mon, 20 Apr 2009) $
 */
public interface DriverLoader {

    public abstract Driver loadDriver(DatabaseDriver databaseDriver);

}