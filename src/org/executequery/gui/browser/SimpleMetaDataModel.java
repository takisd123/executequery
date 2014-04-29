package org.executequery.gui.browser;

import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.executequery.Constants;

public class SimpleMetaDataModel extends AbstractTableModel {

    private String[] columns = {"Property", "Value"};
    private String[] propertyNames;
    private String[] propertyValues;
    private Map<String, String> map;
    
    public int getRowCount() {
        if (propertyNames == null) {
            return 0;
        }
        return propertyNames.length;
    }
    
    public int getColumnCount() {
        return columns.length;
    }

    public void setValues(Map<String, String> map) {

        if (this.map == map) {

            return;
        }
        this.map = map;
        
        if (map == null) {

            propertyNames = new String[0];
            propertyValues = new String[0];
            fireTableDataChanged();
            return;
        }

        int size = map.size();
        propertyNames = new String[size];
        propertyValues = new String[size];
        
        int count = 0;
        for (String key : map.keySet()) {

            propertyNames[count] = key;
            propertyValues[count] = (String) map.get(key);
            count++;            
        }        
        
        fireTableDataChanged();
    }
    
    public Object getValueAt(int row, int col) {
        switch (col) {                
            case 0:
                return propertyNames[row];
            case 1:
                return propertyValues[row];
            default:
                return Constants.EMPTY;                   
        }
    }
    
    public void setValueAt(Object value, int row, int col) {
        
        switch (col) {
            case 0:
                propertyNames[row] = ((String)value);
                break;                    
            case 1:
                propertyValues[row] = ((String)value);
                break;                    
        }
        
        fireTableCellUpdated(row, col);            
    }
    
    public String getColumnName(int col) {
        return columns[col];
    }
    
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    

}
