package org.executequery.datasource;

import java.sql.Driver;

import org.executequery.databasemediators.DatabaseDriver;

public interface DriverLoader {

    public abstract Driver loadDriver(DatabaseDriver databaseDriver);

}