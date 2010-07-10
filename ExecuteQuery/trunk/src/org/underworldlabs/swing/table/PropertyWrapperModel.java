/*
 * PropertyWrapperModel.java
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

package org.underworldlabs.swing.table;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import org.underworldlabs.util.KeyValuePair;

/** Simple wrapper class for key/value property values
 *  providing table model and sorting by key or value.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1506 $
 * @date     $Date: 2009-04-07 01:03:33 +1000 (Tue, 07 Apr 2009) $
 */
public class PropertyWrapperModel extends AbstractSortableTableModel {

    public static final int SORT_BY_KEY = 0;
    public static final int SORT_BY_VALUE = 1;

    private static final String[] HEADER = {"Property", "Value"};

    private int sortBy;
    private KeyValuePair[] valuePairs;

    public PropertyWrapperModel() {
        this(SORT_BY_KEY);
    }

    public PropertyWrapperModel(int sortBy) {
        this.sortBy = sortBy;
    }

    public PropertyWrapperModel(Properties values) {
        this(values, SORT_BY_KEY);
    }

    public PropertyWrapperModel(Properties values, int sortBy) {
        this.sortBy = sortBy;
        setValues(values);
    }

    public void setValues(Properties values) {
        int count = 0;
        valuePairs = new KeyValuePair[values.size()];

        for (Map.Entry<Object, Object> entry : values.entrySet()) {

            valuePairs[count++] = new KeyValuePair(
                    entry.getKey().toString(), entry.getValue().toString());
        }
        
        fireTableDataChanged();
    }

    public void setValues(Map<Object, Object> values, boolean sort) {

        Properties properties = new Properties();
        
        for (Map.Entry<Object, Object> entry : values.entrySet()) {

            properties.put(entry.getKey(), entry.getValue());
        }

        setValues(properties);
        
        if (sort) {
            sort();
        }
        
    }
    
    public void sort() {
        if (valuePairs == null || valuePairs.length == 0) {
            return;
        }
        Arrays.sort(valuePairs, new KeyValuePairSorter());
        fireTableDataChanged();
    }

    public void sort(int sortBy) {
        this.sortBy = sortBy;
        sort();
    }
    
    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        if (valuePairs == null) {
            return 0;
        }
        return valuePairs.length;
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }
    
    public Object getValueAt(int row, int col) {        
        KeyValuePair value = valuePairs[row];
        
        if (col == 0) {
            return value.key;
        } else {
            return value.value;
        }
        
    }

    public String getColumnName(int col) {
        return HEADER[col];
    }


    class KeyValuePairSorter implements Comparator {

        public int compare(Object obj1, Object obj2) {
            KeyValuePair pair1 = (KeyValuePair)obj1;
            KeyValuePair pair2 = (KeyValuePair)obj2;

            String value1 = null;
            String value2 = null;

            if (sortBy == SORT_BY_KEY) {
                value1 = pair1.key.toUpperCase();
                value2 = pair2.key.toUpperCase();
            } else {
                value1 = pair1.value.toUpperCase();
                value2 = pair2.value.toUpperCase();
            }

            int result = value1.compareTo(value2);

            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                return 0;            
            }
        }

    } // class KeyValuePairSorter
    
}

