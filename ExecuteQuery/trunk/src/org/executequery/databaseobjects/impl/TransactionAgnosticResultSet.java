package org.executequery.databaseobjects.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionAgnosticResultSet extends DelegatingResultSet {

    public TransactionAgnosticResultSet(Connection connection, Statement statement, ResultSet resultSet) {

        super(connection, statement, resultSet);
    }

    @Override
    public void close() throws SQLException {

        try {

            super.close();

            Statement statement = getStatement();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {} 
            }

        } finally {

            Connection connection = getConnection();
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException e) {}
            }

        }

    }
    
}
