package org.executequery.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.datasource.ConnectionManager;
import org.underworldlabs.util.FileUtils;

public class SqlScriptRunner {

    private Connection connection;
    
    private final ExecutionController executionController;

    public SqlScriptRunner(ExecutionController executionController) {

        super();
        this.executionController = executionController;
    }

    public SqlStatementResult execute(DatabaseConnection databaseConnection,
            String fileName, ActionOnError actionOnError) {

        int count = 0;
        int result = 0;

        Statement statement = null;
        SqlStatementResult sqlStatementResult = new SqlStatementResult();
        
        try {

            executionController.message("Reading input file " + fileName);            
            String script = FileUtils.loadFile(fileName);
            
            executionController.message("Scanning and tokenizing queries...");
            QueryTokenizer queryTokenizer = new QueryTokenizer();
            List<DerivedQuery> queries = queryTokenizer.tokenize(script);
            
            close();
            connection = ConnectionManager.getConnection(databaseConnection);
            connection.setAutoCommit(false);

            List<DerivedQuery> executableQueries = new ArrayList<DerivedQuery>();
            
            for (DerivedQuery query : queries) {
                
                if (query.isExecutable()) {

                    executableQueries.add(query);
                }
                
            }
            queries.clear();
            
            executionController.message("Found " + executableQueries.size() + " executable queries");            
            executionController.message("Executing...");
            
            statement = connection.createStatement();

            for (DerivedQuery query : executableQueries) {

                if (Thread.interrupted()) {

                    throw new InterruptedException();
                }

                String derivedQuery = query.getDerivedQuery();
                
                try {

                    count++;
//                    executionController.message("Statement No. " + count);
//                    executionController.queryMessage(query.getLoggingQuery());

                    result += statement.executeUpdate(derivedQuery);
                    
                } catch (SQLException e) {

                    executionController.errorMessage("Error executing statement:");
                    executionController.actionMessage(derivedQuery);

                    if (actionOnError != ActionOnError.CONTINUE) {
                        
                        throw e;

                    } else {

                        executionController.errorMessage(e.getMessage());
                    }

                }
                
            }
            
        } catch (IOException e) {

            sqlStatementResult.setOtherException(e);
            executionController.errorMessage("Error opening script file:\n" + e.getMessage());

        } catch (SQLException e) {

            sqlStatementResult.setSqlException(e);

        } catch (InterruptedException e) {
           
            sqlStatementResult.setOtherException(e);

        } finally {

            if (statement != null) {
                
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        }
        
        sqlStatementResult.setUpdateCount(result);
        sqlStatementResult.setStatementCount(count);

        return sqlStatementResult;
    }

    public void close() throws SQLException {
        
        if (connection != null) {

            connection.close();
        }
    }
    
    public void rollback() throws SQLException {

        if (connection != null) {
        
            connection.rollback();
        }
    }

    public void commit() throws SQLException {
     
        if (connection != null) {
         
            connection.commit();
        }
    }

    
}
