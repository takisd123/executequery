/*
 * ResultSetTableModel.java
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

package org.executequery.gui.resultset;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.executequery.log.Log;
import org.executequery.util.UserProperties;
import org.underworldlabs.swing.table.AbstractSortableTableModel;

/**
 * The sql result set table model.
 * 
 * @author Takis Diakoumis
 * @version $Revision: 1521 $
 * @date $Date: 2009-04-20 02:49:39 +1000 (Mon, 20 Apr 2009) $
 */
public class ResultSetTableModel extends AbstractSortableTableModel {

    /** The column types */
    private int[] columnTypes;

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

    public ResultSetTableModel() {
        
        this(null, -1);
    }

    public ResultSetTableModel(int maxRecords) {
        
        this(null, maxRecords);
    }

    public ResultSetTableModel(ResultSet resultSet, int maxRecords) {

        this.maxRecords = maxRecords;
        
        recordDataItemFactory = new RecordDataItemFactory();
        
        holdMetaData = UserProperties.getInstance().getBooleanProperty(
                "editor.results.metadata");

        if (resultSet != null) {

            createTable(resultSet);
        }

    }

    public ResultSetTableModel(List<String> columnHeaders, List<List<RecordDataItem>> tableData) {
        
        this.columnHeaders = columnHeaders;
        this.tableData = tableData;
    }
    
    public void createTable(ResultSet resultSet) {

        if (!isOpenAndValid(resultSet)) {

            clearData();
            return;
        }

        try {

            resetMetaData();

            ResultSetMetaData rsmd = resultSet.getMetaData();

            int count = rsmd.getColumnCount();

            if (columnHeaders != null) {

                columnHeaders.clear();

            } else {

                columnHeaders = new ArrayList<String>(count);
            }

            if (tableData != null) {

                tableData.clear();

            } else {

                tableData = new ArrayList<List<RecordDataItem>>();
            }

            columnTypes = new int[count];
            for (int i = 1; i <= count; i++) {

                columnHeaders.add(rsmd.getColumnName(i));
                columnTypes[i - 1] = rsmd.getColumnType(i);
            }

            int recordCount = 0;
            List<RecordDataItem> rowData = null;
            interrupted = false;

            while (resultSet.next()) {

                if (interrupted || Thread.interrupted()) {

                    throw new InterruptedException();
                }

                recordCount++;
                rowData = new ArrayList<RecordDataItem>(count);

                for (int i = 1; i <= count; i++) {

                	RecordDataItem value = recordDataItemFactory.create(
                			rsmd.getColumnType(i), rsmd.getColumnTypeName(i));

                    try {

                        int dataType = columnTypes[i - 1];
                        
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
                            case Types.TIMESTAMP:
                                value.setValue(resultSet.getTimestamp(i));
                                break;
                            case Types.TIME:
                                value.setValue(resultSet.getTime(i));
                                break;
                            case Types.CLOB:
                            case Types.LONGVARCHAR:
                                value.setValue(resultSet.getClob(i));
                                break;
                            case Types.BLOB:
                            case Types.BINARY:
                            case Types.VARBINARY:
                            case Types.LONGVARBINARY:
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
                            
                            value.setValue("<Error - " 
                                    + sqlException.getMessage() + ">");
                            
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

            fireTableStructureChanged();

        } catch (SQLException e) {

            System.err.println("SQL error populating table model at: " + e.getMessage());

            if (Log.isDebugEnabled()) {

                e.printStackTrace();
            }

        } catch (Exception e) {

            String message = e.getMessage();

            if (StringUtils.isBlank(message)) {

                System.err.println("Exception populating table model.");

            } else {
            
                System.err.println("Exception populating table model at: " + message);
            }

            if (Log.isDebugEnabled()) {
            
                e.printStackTrace();
            }

        } finally {

            if (resultSet != null) {

                try {
                
                    resultSet.close();
                    
                    Statement statement = resultSet.getStatement();
                    if (statement != null) {
                        
                        statement.close();
                    }

                } catch (SQLException sqlExc) {}

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

                        } catch (AbstractMethodError e) {

                            continue;

                        } catch (IllegalArgumentException e) {

                            continue;

                        } catch (IllegalAccessException e) {

                            continue;

                        } catch (InvocationTargetException e) {

                            continue;
                        }

                    }

                }

                metaData.add(rowData);

            }

        } catch (SQLException e) {
            
            if (Log.isDebugEnabled()) {
                
                e.printStackTrace();
            }
            
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

    public void setCellsEditable(boolean cellsEditable) {
     
        this.cellsEditable = cellsEditable;
    }
    
    public boolean isCellEditable(int row, int column) {

        return cellsEditable;
    }

    public String getColumnName(int column) {
        
        return columnHeaders.get(column);
    }

    public Class<?> getColumnClass(int col) {

        List<RecordDataItem> rowData = tableData.get(0);
        int columnType = rowData.get(col).getDataType();
        
        //switch (columnTypes[col]) {
        switch (columnType) {

            case Types.TINYINT:
                return Byte.class;
    
            case Types.SMALLINT:
                return Short.class;
    
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.BIT:
            case Types.BOOLEAN: // don't display the checkbox
                return String.class;
    
            case Types.INTEGER:
                return Integer.class;
    
            case Types.DECIMAL:
            case Types.NUMERIC:
                return BigDecimal.class;
    
            case Types.BIGINT:
                return Long.class;
    
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
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




