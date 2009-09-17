/*
 * TableDataTab.java
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

package org.executequery.gui.browser;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.executequery.GUIUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.SqlStatementResult;
import org.executequery.databasemediators.spi.DefaultStatementExecutor;
import org.executequery.databasemediators.spi.StatementExecutor;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.gui.editor.ResultSetTableContainer;
import org.executequery.gui.editor.ResultSetTablePopupMenu;
import org.executequery.gui.resultset.ResultSetTable;
import org.executequery.gui.resultset.ResultSetTableModel;
import org.underworldlabs.swing.table.TableSorter;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.util.MiscUtils;
import org.underworldlabs.util.SystemProperties;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1521 $
 * @date     $Date: 2009-04-20 02:49:39 +1000 (Mon, 20 Apr 2009) $
 */
public class TableDataTab extends JPanel 
                          implements ResultSetTableContainer {
    
    /** Utility to return table results */
    private StatementExecutor querySender;
    
    /** The results table model */
    private ResultSetTableModel tableModel;
    
    /** The results table */
    private ResultSetTable table;
    
    /** The scroll pane containing the table */
    private JScrollPane scroller;
    
    /** Whether this class is currently executing a query */
    private boolean executing;
    
    /** The <code>String</code>literal 'SELECT * FROM ' */
    private static final String QUERY = "SELECT * FROM ";

    public TableDataTab() {
        super(new GridBagLayout());
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /** <p>Initialises the state of this instance. */
    private void jbInit() throws Exception {
        executing = false;
        
        // create the QuerySender
        querySender = new DefaultStatementExecutor();
        scroller = new JScrollPane();

        /*
        JPanel toolsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;
        gbc.gridx++;
        
        gbc.gridx++;
        gbc.insets.top = 5;
        gbc.insets.left = 10;
        gbc.insets.right = 10;
        toolsPanel.add(new JLabel("Max Rows:"), gbc);
        gbc.gridx++;
        gbc.weightx = 0.8;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        gbc.fill = GridBagConstraints.BOTH;
        toolsPanel.add(maxRowCountField, gbc);
        */
        
        add(scroller, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                GridBagConstraints.SOUTHEAST, 
                                GridBagConstraints.BOTH,
                                new Insets(5, 5, 5, 5), 0, 0));
    }

    public void getTableData(final DatabaseConnection dc,
                             final BaseDatabaseObject metaObject) {
        getTableData(dc, metaObject.getSchemaName(), metaObject.getName());
    }

    public void getTableData(DatabaseTable table) {
        getTableData(table.getHost().getDatabaseConnection(),
                     table.getNamePrefix(), table.getName());
    }

    /** <p>Executes the SQL SELECT query on the specified
     *  table using a <code>SwingWorker</code> thread.
     *
     *  @param the table name
     */
    public void getTableData(final DatabaseConnection dc,
                             final String schemaName, 
                             final String tableName) {

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    executing = true;
                    showWaitCursor();
                    return setTableResultsPanel(dc, schemaName, tableName);
                }
                catch (Exception e) {
                    GUIUtilities.displayExceptionErrorDialog(
                                        "An error occured retrieving the table data.\n" + 
                                        e.getMessage(), e);
                    return "done";
                }
            }
            public void finished() {
                executing = false;
                querySender.releaseResources();
                showNormalCursor();
            }
        };        
        worker.start();
    }
    
    public void cleanup() {
        if (querySender != null) {
            querySender.releaseResources();
        }
    }
    
    /**
     * Contsructs and displays the specified <code>ResultSet</code> 
     * object within the results table.
     *
     * @param the <code>ResultSet</code> data object
     * @return the <code>String</code> 'done' when finished
     */
    private Object setTableResultsPanel(DatabaseConnection dc, 
                                        String schemaName, 
                                        String tableName) {

        try {
            
            String queryString = "";
            if (!MiscUtils.isNull(schemaName)) {
                queryString = schemaName + ".";
            }
            queryString += tableName;
            
            // retrieve the row data
            querySender.setDatabaseConnection(dc);
            SqlStatementResult  result = 
                    querySender.getResultSet(QUERY + queryString);

            if (result.isResultSet()) {

                if (tableModel == null) {
                    tableModel = new ResultSetTableModel(
                                      SystemProperties.getIntProperty(
                                            "user", "browser.max.records"));
                    tableModel.setHoldMetaData(false);
                }
                
                ResultSet rset = result.getResultSet();
                tableModel.createTable(rset);

                if (table == null) {

                    createResultSetTable();
                }

                TableSorter sorter = new TableSorter(tableModel);
                table.setModel(sorter);
                sorter.setTableHeader(table.getTableHeader());

                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                
                scroller.getViewport().add(table);
            }

        } catch (SQLException e) {
          
            GUIUtilities.displayExceptionErrorDialog("Error retrieving table data.", e);
        }
        
        validate();
        repaint();

        return "done";
        
    }

    private void createResultSetTable() {

        table = new ResultSetTable();
        table.addMouseListener(new ResultSetTablePopupMenu(table, this));

        setTableProperties();
    }
    
    /** <p>Whether a SQL SELECT statement is currently
     *  being executed by this class.
     *
     *  @return <code>true</code> if executing,
     *          <code>false</code> otherwise.
     */
    public boolean isExecuting() {
        return executing;
    }
    
    /** <p>Cancels the currently executing statement. */
    public void cancelStatement() {
        querySender.cancelCurrentStatement();
    }
    
    /** <p>Sets default table display properties. */
    public void setTableProperties() {
        
        if (table == null) {
            return;
        }

        table.applyUserPreferences();
        table.setCellSelectionEnabled(false);
        
        tableModel.setMaxRecords(
                SystemProperties.getIntProperty("user", "browser.max.records"));
    }
    
    private void showNormalCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void showWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    public JTable getTable() {
        return table;
    }

    public void transposeRow(TableModel tableModel, int row) {}
    
    public boolean isTransposeAvailable() {

        return false;
    }
    
}





