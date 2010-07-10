/*
 * ResultSetDelimitedFileWriter.java
 *
 * Copyright (C) 2002-2010 Takis Diakoumis
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

package org.executequery.gui.importexport;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetDelimitedFileWriter {

    public int write(String fileName, String delimiter, ResultSet resultSet, boolean columnNamesAsFirstRow) throws InterruptedException {

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

                    throw new InterruptedException();
                }

                for (int i = 1; i <= columnCount; i++) {

                    if (Thread.interrupted()) {

                        throw new InterruptedException();
                    }

                    String value = resultSet.getString(i);
                    if (!resultSet.wasNull()) {

                        sb.append(value);                        
                    }
                    
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

            handleError(e);

        } catch (SQLException e) {

            handleError(e);

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
    
    private void handleError(Throwable e) {
        
        throw new ImportExportDataException(e);
    }
    
}

