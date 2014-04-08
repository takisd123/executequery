/*
 * ResultSetTableModel.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.executequery.gui.ErrorMessagePublisher;
import org.executequery.log.Log;
import org.executequery.util.UserProperties;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.table.AbstractSortableTableModel;
import org.underworldlabs.util.MiscUtils;

/**
 * The sql result set table model.
 * 
 * @author Takis Diakoumis
 * @version $Revision$
 * @date $Date$
 */
public class ResultSetTableModel extends AbstractSortableTableModel {

    /** Whether the meta data should be generated */
    private boolean holdMetaData;

    /** The maximum number of records displayed */
    private int maxRecords;

    /** Indicates that the query executing has been interrupted */
    private boolean interrupted;

    /** The column names */
    private List<String> columnHeaders;

    /** The table values */
    private List<List<RecordDataItem>> tableData;

    /** result set meta data model */
    private ResultSetMetaDataTableModel metaDataTableModel;

    private RecordDataItemFactory recordDataItemFactory;

    private String query;

    public ResultSetTableModel() {
        
        this(null, -1);
    }

    public ResultSetTableModel(int maxRecords) {
        
        this(null, maxRecords);
    }

    public ResultSetTableModel(ResultSet resultSet, int maxRecords) {

        this(resultSet, maxRecords, null);
    }

    public ResultSetTableModel(ResultSet resultSet, int maxRecords, String query) {
        
        this.maxRecords = maxRecords;
        this.query = query;
        
        columnHeaders = new ArrayList<String>();
        tableData = new ArrayList<List<RecordDataItem>>();
        recordDataItemFactory = new RecordDataItemFactory();
        
        holdMetaData = UserProperties.getInstance().getBooleanProperty("editor.results.metadata");
        
        if (resultSet != null) {
            
            createTable(resultSet);
        }
        
    }
    
    public ResultSetTableModel(List<String> columnHeaders, List<List<RecordDataItem>> tableData) {
        
        this.columnHeaders = columnHeaders;
        this.tableData = tableData;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void createTable(ResultSet resultSet) {

        if (!isOpenAndValid(resultSet)) {

            clearData();
            return;
        }

        try {

            resetMetaData();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            columnHeaders.clear();
            tableData.clear();

            int zeroBaseIndex = 0;
            int count = rsmd.getColumnCount();
            int[] columnTypes = new int[count];
            String[] columnTypeNames = new String[count];

            for (int i = 1; i <= count; i++) {

                zeroBaseIndex = i - 1;
                columnHeaders.add(rsmd.getColumnName(i));
                columnTypes[zeroBaseIndex] = rsmd.getColumnType(i);
                columnTypeNames[zeroBaseIndex] = rsmd.getColumnTypeName(i);
            }

            int recordCount = 0;
            interrupted = false;

            List<RecordDataItem> rowData;
            long time = System.currentTimeMillis();
            while (resultSet.next()) {

                if (interrupted || Thread.interrupted()) {

                    throw new InterruptedException();
                }

                recordCount++;
                rowData = new ArrayList<RecordDataItem>(count);

                for (int i = 1; i <= count; i++) {

                    zeroBaseIndex = i - 1;
                	RecordDataItem value = recordDataItemFactory.create(
                			columnHeaders.get(zeroBaseIndex),
                            columnTypes[zeroBaseIndex],
                            columnTypeNames[zeroBaseIndex]);

                    try {

                        int dataType = columnTypes[zeroBaseIndex];
                        switch (dataType) {

                            // some drivers (informix for example)
                            // was noticed to return the hashcode from
                            // getObject for -1 data types (eg. longvarchar).
                            // force string for these - others stick with
                            // getObject() for default value formatting
    
                            case Types.CHAR:
                            case Types.VARCHAR:
                                value.setValue(resultSet.getString(i));
                                break;
                            case Types.DATE:
                                value.setValue(resultSet.getDate(i));
                                break;
                            case Types.TIME:
                                value.setValue(resultSet.getTime(i));
                                break;
                            case Types.TIMESTAMP:
                                value.setValue(resultSet.getTimestamp(i));
                                break;
                            case Types.LONGVARCHAR:
                            case Types.CLOB:
                                value.setValue(resultSet.getClob(i));
                                break;
                            case Types.LONGVARBINARY:
                            case Types.VARBINARY:
                            case Types.BINARY:
                            case Types.BLOB:
                                value.setValue(resultSet.getBlob(i));
                                break;
                            default:
                                value.setValue(resultSet.getObject(i));
                                break;
                        }

                    } catch (Exception e) {

                        try {
                        
                            // ... and on dump, resort to string
                            value.setValue(resultSet.getString(i));
                            
                        } catch (SQLException sqlException) {
                            
                            // catch-all SQLException - yes, this is hideous

                            // noticed with invalid date formatted values in mysql
                            
                            value.setValue("<Error - " + sqlException.getMessage() + ">");
                        }
                    }

                    if (resultSet.wasNull()) {

                        value.setNull();
                    }

                    rowData.add(value);
                }

                tableData.add(rowData);

                if (recordCount == maxRecords) {

                    break;
                }

            }

            if (holdMetaData) {

                setMetaDataVectors(rsmd);
            }

            if (Log.isTraceEnabled()) {
            
                Log.trace("Finished populating table model - " + recordCount + " rows - [ " 
                        + MiscUtils.formatDuration(System.currentTimeMillis() - time) + "]");
            }

            fireTableStructureChanged();

        } catch (SQLException e) {
            
            System.err.println("SQL error populating table model at: " + e.getMessage());
            Log.debug("Table model error - " + e.getMessage(), e);

        } catch (Exception e) {

            if (e instanceof InterruptedException) {
                
                Log.debug("ResultSet generation interrupted.", e);
            
            } else {

                String message = e.getMessage();
                if (StringUtils.isBlank(message)) {
    
                    System.err.println("Exception populating table model.");
    
                } else {
                
                    System.err.println("Exception populating table model at: " + message);
                }
    
                Log.debug("Table model error - ", e);
            }

        } finally {

            if (resultSet != null) {

                try {
                
                    resultSet.close();

                    Statement statement = resultSet.getStatement();
                    if (statement != null) {

                        statement.close();
                    }

                } catch (SQLException e) {}

            }
        }

    }

    private boolean isOpenAndValid(ResultSet resultSet) {

        return (resultSet != null);
    }

    private void resetMetaData() {
        if (metaDataTableModel != null) {
            
            metaDataTableModel.reset();
        }
    }

    private void clearData() {

        if (tableData != null) {

            tableData.clear();

        } else {

            tableData = new ArrayList<List<RecordDataItem>>(0);
        }

        fireTableStructureChanged();
    }

    public void interrupt() {

        interrupted = true;
    }

    public void setHoldMetaData(boolean holdMetaData) {

        this.holdMetaData = holdMetaData;
    }

    private static final String STRING = "String";
    private static final String GET = "get";
    private static final String EXCLUDES = "getColumnCount";
    private static final String COLUMN_NAME = "ColumnName";
    
    private void setMetaDataVectors(ResultSetMetaData rsmd) {

        Class<?> metaClass = rsmd.getClass();
        Method[] metaMethods = metaClass.getMethods();

        List<String> columns = null;
        List<String> rowData = null;
        List<List<String>> metaData = null;

        try {

            int columnCount = rsmd.getColumnCount();
            columns = new ArrayList<String>(metaMethods.length - 1);
            metaData = new ArrayList<List<String>>(columnCount);

            Object[] obj = new Object[1];
            for (int j = 1; j <= columnCount; j++) {

                obj[0] = Integer.valueOf(j);
                rowData = new ArrayList<String>(metaMethods.length - 1);
                for (int i = 0; i < metaMethods.length; i++) {

                    String methodName = metaMethods[i].getName();
                    if (EXCLUDES.contains(methodName)) {

                        continue;
                    }

                    Class<?> c = metaMethods[i].getReturnType();

                    if (c.isPrimitive() || c.getName().endsWith(STRING)) {

                        if (methodName.startsWith(GET)) {

                            methodName = methodName.substring(3);
                        }

                        try {

                            Object res = metaMethods[i].invoke(rsmd, obj);

                            if (methodName.equals(COLUMN_NAME)) {

                                if (j == 1) {

                                    columns.add(0, methodName);
                                }

                                rowData.add(0, objectToString(res));

                            } else {

                                if (j == 1) {

                                    columns.add(methodName);
                                }

                                rowData.add(objectToString(res));

                            }

                        }
                        catch (AbstractMethodError e) {}
                        catch (IllegalArgumentException e) {}
                        catch (IllegalAccessException e) {}
                        catch (InvocationTargetException e) {}

                    }

                }

                metaData.add(rowData);

            }

        } catch (SQLException e) {
            
            Log.debug(e.getMessage(), e);
        }

        if (metaDataTableModel == null) {
            
            metaDataTableModel = new ResultSetMetaDataTableModel();
        }
        
        metaDataTableModel.setValues(columns, metaData);        
    }

    private String objectToString(Object res) {

        String value = null;
        
        if (res != null) {
            
            value = res.toString();

        } else {
            
            value = "";
        }

        return value;
    }

    public void setMaxRecords(int maxRecords) {

        this.maxRecords = maxRecords;
    }

    public boolean hasResultSetMetaData() {
        
        return (metaDataTableModel != null && metaDataTableModel.getRowCount() > 0);
    }

    public ResultSetMetaDataTableModel getResultSetMetaData() {

        return metaDataTableModel;
    }

    // ----------------------------------------------------------
    
    public int getColumnCount() {
        
        if (columnHeaders == null) {
            
            return 0;
        }

        return columnHeaders.size();
    }

    public int getRowCount() {

        if (tableData == null) {

            return 0;
        }

        return tableData.size();
    }

    public List<RecordDataItem> getRowDataForRow(int row) {
        
        return tableData.get(row);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

        List<RecordDataItem> rowData = tableData.get(row);
        if (column < rowData.size()) {

            try {
            
                rowData.get(column).valueChanged(value);
                fireTableCellUpdated(row, column);
                
            } catch (DataSourceException e) {
                
                Throwable cause = e.getCause();
                if (cause instanceof ParseException) {
                    
                    ErrorMessagePublisher.publish(
                            "Invalid value provided for type -\n" + e.getExtendedMessage(), cause);
                }
            }
                
        }
    }
    
    public Object getValueAt(int row, int column) {

        if (row < tableData.size()) {

            List<RecordDataItem> rowData = tableData.get(row);
            if (column < rowData.size()) {

                return rowData.get(column);
            }
        }

        return null;
    }

    public Object getRowValueAt(int row) {
        
        return tableData.get(row);
    }

    private boolean cellsEditable;
    private Set<String> nonEditableColumns;
    
    public void setCellsEditable(boolean cellsEditable) {
     
        this.cellsEditable = cellsEditable;
    }
    
    public boolean isCellEditable(int row, int column) {

        if (columnHeaders != null) {
            
            String name = columnHeaders.get(column);
            if (nonEditableColumns != null && nonEditableColumns.contains(name)) {
                
                return false;
            }
            
        }

        RecordDataItem recordDataItem = tableData.get(row).get(column);
        if (recordDataItem.isLob()) {

            return false;
        }
        
        return cellsEditable;
    }

    public void setNonEditableColumns(List<String> nonEditableColumns) {
        setCellsEditable(true);
        this.nonEditableColumns = new HashSet<String>(nonEditableColumns);
    }
    
    public String getColumnName(int column) {
        
        return columnHeaders.get(column);
    }

    public Class<?> getColumnClass(int col) {

        RecordDataItem recordDataItem = tableData.get(0).get(col);
        if (recordDataItem.isValueNull()) {
            
            return String.class;
        }
        
        int columnType = recordDataItem.getDataType();
        switch (columnType) {

            case Types.TINYINT:
                return Byte.class;
    
            case Types.BIGINT:
                return Long.class;
    
            case Types.SMALLINT:
                return Short.class;
    
            case Types.BIT:
            case Types.LONGVARCHAR:
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.BOOLEAN: // don't display the checkbox
                return String.class;
    
            case Types.NUMERIC:
            case Types.DECIMAL:
                return BigDecimal.class;
    
            case Types.INTEGER:
                return Integer.class;
                
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return java.util.Date.class;
    
            case Types.REAL:
                return Float.class;
    
            case Types.FLOAT:
            case Types.DOUBLE:
                return Double.class;
    
            default:
                return Object.class;

        }

    }

}
