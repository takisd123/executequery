/*
 * TableDataTab.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.apache.commons.lang.StringUtils;
import org.executequery.databaseobjects.DatabaseObject;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.databaseobjects.impl.ColumnConstraint;
import org.executequery.gui.browser.sql.TableDataChange;
import org.executequery.gui.editor.ResultSetTableContainer;
import org.executequery.gui.editor.ResultSetTablePopupMenu;
import org.executequery.gui.resultset.ResultSetTable;
import org.executequery.gui.resultset.ResultSetTableModel;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.DisabledField;
import org.underworldlabs.swing.table.TableSorter;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.util.SystemProperties;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class TableDataTab extends JPanel implements ResultSetTableContainer, TableModelListener {

    private ResultSetTableModel tableModel;

    private ResultSetTable table;

    private JScrollPane scroller;

    private DatabaseObject databaseObject;

    private boolean executing = false;

    private GridBagConstraints scrollerConstraints;

    private GridBagConstraints errorLabelConstraints;

    private GridBagConstraints rowCountPanelConstraints;

    private DisabledField rowCountField;

    private JPanel rowCountPanel;

    private final boolean displayRowCount;
    private List<TableDataChange> tableDataChanges;

    public TableDataTab(boolean displayRowCount) {

        super(new GridBagLayout());
        this.displayRowCount = displayRowCount;

        try {

            init();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void init() throws Exception {

        if (displayRowCount) {
            
            initRowCountPanel();
        }
        
        scroller = new JScrollPane();
        scrollerConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                GridBagConstraints.SOUTHEAST,
                                GridBagConstraints.BOTH,
                                new Insets(5, 5, 5, 5), 0, 0);

        rowCountPanelConstraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 0,
                GridBagConstraints.SOUTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0);

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
     * @param databaseObject <code>ResultSet</code> data object
     * @return the <code>String</code> 'done' when finished
     */
    private Object setTableResultsPanel(DatabaseObject databaseObject) {

        this.databaseObject = databaseObject;
        try {

            if (tableModel == null) {

                tableModel = new ResultSetTableModel(SystemProperties.getIntProperty("user", "browser.max.records"));
                tableModel.setHoldMetaData(false);
            }

            tableModel.removeTableModelListener(this);

            if (isDatabaseTable(databaseObject)) {

                DatabaseTable databaseTable = (DatabaseTable) databaseObject;
                if (databaseTable.hasPrimaryKey()) {

                    List<String> primaryKeyColumns = derivePrimaryKeyColumns(databaseTable);
                    tableModel.setNonEditableColumns(primaryKeyColumns);
                }
                
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
            
            if (displayRowCount) {
                
                add(rowCountPanel, rowCountPanelConstraints);
                rowCountField.setText(String.valueOf(sorter.getRowCount()));
            }
            
        } catch (DataSourceException e) {

            addErrorLabel(e);

        } finally {

            tableModel.addTableModelListener(this);
        }

        validate();
        repaint();

        return "done";
    }

    private List<String> derivePrimaryKeyColumns(DatabaseTable databaseTable) {

        List<String> primaryKeyColumns = new ArrayList<String>();
        List<ColumnConstraint> primaryKeys = databaseTable.getPrimaryKeys();

        for (ColumnConstraint constraint : primaryKeys) {

            primaryKeyColumns.add(constraint.getColumnName());
        }

        return primaryKeyColumns;
    }

    private boolean isDatabaseTable(DatabaseObject databaseObject) {
        return databaseObject instanceof DatabaseTable;
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

    private void initRowCountPanel() {

        rowCountField = new DisabledField();
        rowCountPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        rowCountPanel.add(new JLabel("Data Row Count:"), gbc);
        gbc.gridx = 2;
        gbc.insets.bottom = 2;
        gbc.insets.left = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.right = 0;
        rowCountPanel.add(rowCountField, gbc);        
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

    @Override
    public void tableChanged(TableModelEvent e) {

        if (isDatabaseTable(this.databaseObject)) {

            int row = e.getFirstRow();
            tableDataChanges().add(
                    new TableDataChange(this.databaseObject, tableModel.getRowDataForRow(row)));


        }

        System.out.println("table canged");

    }

    private List<TableDataChange> tableDataChanges() {

        if (tableDataChanges == null) {

            tableDataChanges = new ArrayList<TableDataChange>();
        }

        return tableDataChanges;
    }


}




