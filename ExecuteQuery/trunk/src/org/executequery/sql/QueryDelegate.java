/*
 * QueryDelegate.java
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

package org.executequery.sql;

import java.sql.SQLException;
import java.sql.ResultSet;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public interface QueryDelegate {
    
    /**
     * Notifies the runner that the commit mode has changed 
     * to that specified.
     * 
     * @param the commit mode
     */
    void commitModeChanged(boolean autoCommit);
    
    /** 
     * Notifies the runner that a query is executing.
     *
     *  @param a message
     */
    void executing();

    /** <p>Sets the result text within the designated
     *  statement results area to the specified values.
     *
     *  @param the result of the executed query (update)
     *  @param the type of statement executed
     */
    void setResult(int result, int type);
    
    /** <p>Sets the text within status bar's left-hand
     *  message area to the specified value.
     *
     *  @param the text to display
     */
    void setStatusMessage(String text);
    
    /** <p>Sets the error message text within the
     *  designated area to the specified value. This
     *  will usually send <code>SQLException</code>
     *  messages or dumps.
     *
     *  @param the error message to display
     */
    void setOutputMessage(int type, String text);

    /** 
     * Sets the error message text within the
     * designated area to the specified value and selects
     * the output pane as specified.
     *
     * @param the error message to display
     */
    void setOutputMessage(int type, String text, boolean selectTab);

    /** 
     * Interrupts any executing statement. 
     */
    void interrupt();

    /** <p>Sets the table results to the specified
     *  <code>ResultSet</code> object for display.
     *
     *  @param the table results to display
     *  @param the executed query of the result set
     */
    void setResultSet(ResultSet rs, String query) throws SQLException;

    /** <p>Adds the specified SQL statement to the statement
     *  history list if available.
     *
     *  @param the statement to add
     */
    void statementExecuted(String statement);
    
    /** 
     * Notifies that am execution has finished.
     */
    void finished(String message);
    
    /**
     * Indicates whether an internal logger is used.
     */
    boolean isLogEnabled();
    
    /**
     * Logs the specified message.
     * 
     * @param message
     */
    void log(String message);
 
    /**
     * Attempts to commit any currently open transaction.
     */
    void commit();

    /**
     * Attempts to rollback any currently open transaction.
     */
    void rollback();
    
    /**
     * Executes the specified query.
     *
     * @param query - the query
     */
    void executeQuery(String query);

    /**
     * Executes the specified query as a 'block' if specified.
     *
     * @param the query
     * @param whether to execute ALL query text as one statement
     */
    void executeQuery(String query, boolean executeAsBlock);

}



