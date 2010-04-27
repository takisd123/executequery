package org.executequery.gui;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetDelimitedFileWriter {

    public int write(String fileName, String delimiter, ResultSet resultSet, boolean columnNamesAsFirstRow) {

        PrintWriter writer = null;
        
        try {

            StringBuilder sb = new StringBuilder();
            writer = new PrintWriter(new FileWriter(fileName, false), true);

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            
            if (columnNamesAsFirstRow) {
                
                writer.println(columnNames(delimiter, resultSetMetaData));
            }
            
            int recordCount = 0;
            int columnCount = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {

                if (Thread.interrupted()) {

//                    setProgressStatus(recordCount);
                    throw new InterruptedException();
                }

                for (int i = 1; i <= columnCount; i++) {
                    
                    sb.append(resultSet.getString(i));
                    
                    if (i < columnCount) {
                     
                        sb.append(delimiter);
                    }
                    
                }

                writer.println(sb.toString());
                recordCount++;

                sb.setLength(0);
            }
            
            return recordCount;

        } catch (IOException e) {

            e.printStackTrace();

        } catch (SQLException e) {

            e.printStackTrace();

        } catch (InterruptedException e) {

            e.printStackTrace();
            
        } finally {
            
            if (writer != null) {

                writer.close();
            }
            
        }

        return -1;
    }

    private String columnNames(String delimiter, 
            ResultSetMetaData resultSetMetaData) throws SQLException {

        StringBuilder sb = new StringBuilder();
        for (int i = 1, n = resultSetMetaData.getColumnCount(); i <= n; i++) {
            
            sb.append(resultSetMetaData.getColumnName(i));
            sb.append(delimiter);
        }

        return sb.substring(0, sb.length() - delimiter.length());
    }
    
}
