package org.executequery.databaseobjects.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionAgnosticResultSet extends DelegatingResultSet {

    private final boolean originalAutoCommit;
    
    public TransactionAgnosticResultSet(Connection connection, Statement statement, ResultSet delegateResultSet, boolean originalAutoCommit) {

        super(connection, statement, delegateResultSet);
        this.originalAutoCommit = originalAutoCommit;
    }

    @Override
    public void close() throws SQLException {

        try {

            getConnection().commit();
            super.close();

            Statement statement = getStatement();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {} 
            }

        } finally {
        
            /*
            Connection connection = getConnection();
            if (connection != null) {
    
                connection.setAutoCommit(originalAutoCommit);
            }
            */
        }

    }
    
}
