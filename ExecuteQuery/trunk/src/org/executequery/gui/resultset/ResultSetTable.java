/*
 * ResultSetTable.java
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

package org.executequery.gui.resultset;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.table.StringCellEditor;
import org.underworldlabs.util.SystemProperties;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ResultSetTable extends JTable {

    private DefaultCellEditor cellEditor;

    private ResultsTableColumnModel columnModel;

    private ResultSetTableCellRenderer cellRenderer;

    private TableColumn dummyColumn = new TableColumn();

    public ResultSetTable() {

        super();
        setDefaultOptions();

        final StringCellEditor stringCellEditor = new StringCellEditor();
        stringCellEditor.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

        cellEditor = new DefaultCellEditor(stringCellEditor) {
            public Object getCellEditorValue() {
                return stringCellEditor.getValue(); }
        };

    }

    public ResultSetTable(TableModel model) {

        super(model);
        setDefaultOptions();
    }

    private void setDefaultOptions() {

        setColumnSelectionAllowed(true);
        columnModel = new ResultsTableColumnModel();
        setColumnModel(columnModel);

        cellRenderer = new ResultSetTableCellRenderer();
        cellRenderer.setFont(getFont());

        applyUserPreferences();
    }

    public void selectCellAtPoint(Point point) {

        int row = rowAtPoint(point);
        int col = columnAtPoint(point);

        setColumnSelectionInterval(col, col);
        setRowSelectionInterval(row, row);
    }

    public boolean hasMultipleColumnAndRowSelections() {

        int cols = getSelectedColumnCount();
        int rows = getSelectedRowCount();

        return (cols > 1 || rows > 1);
    }

    public void selectRow(Point point) {

        if (point != null) {

            setColumnSelectionAllowed(false);
            setRowSelectionAllowed(true);

            int selectedRowCount = getSelectedRowCount();
            if (selectedRowCount > 1) {

                int[] selectedRows = getSelectedRows();
                setRowSelectionInterval(selectedRows[0], selectedRows[selectedRows.length - 1]);

            } else {

                clearSelection();
                int row = rowAtPoint(point);
                setRowSelectionInterval(row, row);
            }

        }

    }

    public void selectColumn(Point point) {

        if (point != null) {

            setColumnSelectionAllowed(true);
            setRowSelectionAllowed(false);

            int columnCount = getSelectedColumnCount();
            if (columnCount > 1) {

                int[] selectedColumns = getSelectedColumns();
                setColumnSelectionInterval(selectedColumns[0], selectedColumns[selectedColumns.length - 1]);

            } else {

                clearSelection();
                int column = columnAtPoint(point);
                setColumnSelectionInterval(column, column);
            }

        }

    }

    public void copySelectedCells() {

        StringBuilder sb = new StringBuilder();

        int cols = getSelectedColumnCount();
        int rows = getSelectedRowCount();

        if (cols == 0 && rows == 0) {

            return;
        }

        int[] selectedRows = getSelectedRows();
        int[] selectedCols = getSelectedColumns();

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                sb.append(getValueAt(selectedRows[i], selectedCols[j]));

                if (j < cols - 1) {

                    sb.append('\t');
                }

            }

            if (i < rows - 1) {

                sb.append('\n');
            }

        }

        GUIUtilities.copyToClipBoard(sb.toString());
    }

    public Object valueAtPoint(Point point) {

        int row = rowAtPoint(point);
        int col = columnAtPoint(point);

        return getValueAt(row, col);
    }

    public TableModel selectedCellsAsTableModel() {

        int cols = getSelectedColumnCount();
        int rows = getSelectedRowCount();

        if (cols == 0 && rows == 0) {
            return null;
        }

        int[] selectedRows = getSelectedRows();
        int[] selectedCols = getSelectedColumns();

        Vector data = new Vector(rows);
        Vector columns = new Vector(cols);

        for (int i = 0; i < rows; i++) {

            Vector rowVector = new Vector(cols);

            for (int j = 0; j < cols; j++) {

                rowVector.add(getValueAt(selectedRows[i], selectedCols[j]));

                if (i == 0) {

                    columns.add(getColumnName(selectedCols[j]));
                }

            }

            data.add(rowVector);
         }

        return new DefaultTableModel(data, columns);
    }

    public void applyUserPreferences() {

        setDragEnabled(true);
        setCellSelectionEnabled(true);

        setBackground(SystemProperties.getColourProperty(
                "user", "results.table.cell.background.colour"));

        setRowHeight(SystemProperties.getIntProperty(
                "user", "results.table.column.height"));

        setRowSelectionAllowed(SystemProperties.getBooleanProperty(
                "user", "results.table.row.select"));

        getTableHeader().setResizingAllowed(SystemProperties.getBooleanProperty(
                "user", "results.table.column.resize"));

        getTableHeader().setReorderingAllowed(SystemProperties.getBooleanProperty(
                "user", "results.table.column.reorder"));

        setTableColumnWidth(getUserPreferredColumnWidth());

        cellRenderer.applyUserPreferences();
    }

    public void resetTableColumnWidth() {
        setTableColumnWidth(getUserPreferredColumnWidth());
    }

    public void setBackground(Color background) {
        if (cellRenderer != null) {
            cellRenderer.setTableBackground(background);
        }
        super.setBackground(background);
    }

    public void setFont(Font font) {
        super.setFont(font);
        if (cellRenderer != null) {
            cellRenderer.setFont(font);
        }
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        return cellRenderer;
    }

    public TableCellEditor getCellEditor(int row, int column) {
        return cellEditor;
    }

    private int getUserPreferredColumnWidth() {
        return SystemProperties.getIntProperty("user", "results.table.column.width");
    }

    private void setTableColumnWidth(int columnWidth) {
        TableColumnModel tcm = getColumnModel();
        if (columnWidth != 75) {
            TableColumn col = null;
            for (Enumeration<TableColumn> i = tcm.getColumns(); i.hasMoreElements();) {
                col = i.nextElement();
                col.setPreferredWidth(columnWidth);
            }
        }
    }

    class ResultsTableColumnModel extends DefaultTableColumnModel {

        // dumb work-around for update issue noted
        public TableColumn getColumn(int columnIndex) {
            try {
                return super.getColumn(columnIndex);
            } catch (Exception e) {
                return dummyColumn;
            }
        }

    } // class ResultsTableColumnModel

}




