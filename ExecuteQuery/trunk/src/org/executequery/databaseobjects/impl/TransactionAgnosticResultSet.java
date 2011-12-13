package org.executequery.databaseobjects.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionAgnosticResultSet extends DelegatingResultSet {

    private final boolean originalAutoCommit;
    
    public TransactionAgnosticResultSet(Connection connection, ResultSet delegateResultSet, boolean originalAutoCommit) {

        super(connection, delegateResultSet);
        this.originalAutoCommit = originalAutoCommit;
    }

    @Override
    public void close() throws SQLException {

        try {
        
            super.close();

        } finally {
        
            Connection connection = getConnection();
            if (connection != null) {
    
                connection.setAutoCommit(originalAutoCommit);
            }
        }

    }
    
}
