/*
 * TableDataTab.java
 *
 * Copyright (C) 2002-2012 Takis Diakoumis
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.commons.lang.StringUtils;
import org.executequery.databaseobjects.DatabaseObject;
import org.executequery.gui.editor.ResultSetTableContainer;
import org.executequery.gui.editor.ResultSetTablePopupMenu;
import org.executequery.gui.resultset.ResultSetTable;
import org.executequery.gui.resultset.ResultSetTableModel;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.table.TableSorter;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.util.SystemProperties;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class TableDataTab extends JPanel implements ResultSetTableContainer {

    private ResultSetTableModel tableModel;

    private ResultSetTable table;

    private JScrollPane scroller;

    private DatabaseObject databaseObject;

    private boolean executing = false;

    private GridBagConstraints scrollerConstraints;

    private GridBagConstraints errorLabelConstraints;

    public TableDataTab() {

        super(new GridBagLayout());
        try {

            init();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void init() throws Exception {

        scroller = new JScrollPane();
        scrollerConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                GridBagConstraints.SOUTHEAST,
                                GridBagConstraints.BOTH,
                                new Insets(5, 5, 5, 5), 0, 0);
        errorLabelConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0);
    }

    public void loadDataForTable(final DatabaseObject databaseObject) {

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {

                    executing = true;
                    showWaitCursor();

                    removeAll();
                    return setTableResultsPanel(databaseObject);

                } catch (Exception e) {

                    addErrorLabel(e);
                    
//                    GUIUtilities.displayExceptionErrorDialog(
//                                        "An error occured retrieving the object data.\n" +
//                                        e.getMessage(), e);
                    return "done";
                }
            }
            public void finished() {

                executing = false;
                showNormalCursor();
            }

        };
        worker.start();
    }

    /**
     * Contsructs and displays the specified <code>ResultSet</code>
     * object within the results table.
     *
     * @param the <code>ResultSet</code> data object
     * @return the <code>String</code> 'done' when finished
     */
    private Object setTableResultsPanel(DatabaseObject databaseObject) {

        this.databaseObject = databaseObject;

        try {

            if (tableModel == null) {

                tableModel = new ResultSetTableModel(
                        SystemProperties.getIntProperty("user", "browser.max.records"));
                tableModel.setHoldMetaData(false);
            }

            ResultSet resultSet = databaseObject.getData(true);
            tableModel.createTable(resultSet);

            if (table == null) {

                createResultSetTable();
            }

            TableSorter sorter = new TableSorter(tableModel);
            table.setModel(sorter);
            sorter.setTableHeader(table.getTableHeader());

            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            scroller.getViewport().add(table);
            add(scroller, scrollerConstraints);
            
        } catch (DataSourceException e) {

            addErrorLabel(e);
            
//            GUIUtilities.displayExceptionErrorDialog("Error retrieving table data.", e);
        }

        validate();
        repaint();

        return "done";
    }

    private void addErrorLabel(Throwable e) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><p><center>Error retrieving object data");
        String message = e.getMessage();
        if (StringUtils.isNotBlank(message)) {

            sb.append("<br />[ ").append(message);
        }

        sb.append(" ]</center></p><p><center><i>(Note: Data will not always be available for all object types)</i></center></p></body></html>");
        add(new JLabel(sb.toString()), errorLabelConstraints);
    }

    private void createResultSetTable() {

        table = new ResultSetTable();
        table.addMouseListener(new ResultSetTablePopupMenu(table, this));

        setTableProperties();
    }

    /**
     * Whether a SQL SELECT statement is currently being executed by this class.
     *
     * @return <code>true</code> | <code>false</code>
     */
    public boolean isExecuting() {

        return executing;
    }

    /** Cancels the currently executing statement. */
    public void cancelStatement() {

        databaseObject.cancelStatement();
    }

    /** Sets default table display properties. */
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

    public boolean isTransposeAvailable() {

        return false;
    }

    public void transposeRow(TableModel tableModel, int row) {

        // do nothing
    }

}


