/*
 * SqlStatementResult.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
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

package org.executequery.databasemediators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import org.executequery.Constants;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class SqlStatementResult {
    
    private int type;
    
    private int updateCount;
    
    private String message;
    
    private String otherErrorMessage;
    
    private ResultSet resultSet;
    
    private SQLException sqlException;
    
    private SQLWarning sqlWarning;
    
    private Object otherResult;
    
    /** Creates a new instance of SqlStatementResult */
    public SqlStatementResult() {}

    public SqlStatementResult(ResultSet resultSet, 
                              SQLException sqlException,
                              SQLWarning sqlWarning) {
        this.resultSet = resultSet;
        this.sqlException = sqlException;
        this.sqlWarning = sqlWarning;
    }

    public void reset(ResultSet resultSet, 
                      SQLException sqlException,
                      SQLWarning sqlWarning) {
        updateCount = -1;
        this.resultSet = resultSet;
        this.sqlException = sqlException;
        this.sqlWarning = sqlWarning;
    }

    public void reset() {
        updateCount = -1;
        message = Constants.EMPTY;
        resultSet = null;
        sqlException = null;
        sqlWarning = null;
        otherResult = null;
        otherErrorMessage = null;
    }
    
    public ResultSet getResultSet() {
        return resultSet;
    }

    public String getErrorMessage() {
        
        if (sqlException == null && otherErrorMessage == null) {

            return message;

        } else if (otherErrorMessage != null) {

            return otherErrorMessage;
        }

        String text = sqlException.getMessage();

        if (text != null) {

            int errorCode = 0;
            
            StringBuilder message = new StringBuilder();
            SQLException sqlExc = sqlException;
            
            while (true) {

                if (sqlExc == null) {

                    break;
                }

                String _message = sqlExc.getMessage();
                message.append(_message);

                if (!_message.endsWith("\n")) {

                    message.append("\n");
                }

                errorCode = sqlException.getErrorCode();
                if (errorCode > 0) {

                    message.append("[Error Code: ").
                            append(errorCode).
                            append("]\n");
                }

                text = sqlException.getSQLState();
                if (text != null) {

                    message.append("[SQL State: ").
                            append(text).
                            append("]\n");
                }

                sqlExc = sqlExc.getNextException();
            }

            return message.toString();

        } else {
          
            return "An indeterminate error has occurred";
        }

    }
    
    public boolean isResultSet() {
        return resultSet != null;
    }
    
    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public SQLException getSqlException() {
        return sqlException;
    }

    public void setSqlException(SQLException sqlException) {
        this.sqlException = sqlException;
    }

    public SQLWarning getSqlWarning() {
        return sqlWarning;
    }

    public void setSqlWarning(SQLWarning sqlWarning) {
        this.sqlWarning = sqlWarning;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getOtherResult() {
        return otherResult;
    }

    public void setOtherResult(Object otherResult) {
        this.otherResult = otherResult;
    }

    public String getOtherErrorMessage() {
        return otherErrorMessage;
    }

    public void setOtherErrorMessage(String otherErrorMessage) {
        this.otherErrorMessage = otherErrorMessage;
    }
    
}





