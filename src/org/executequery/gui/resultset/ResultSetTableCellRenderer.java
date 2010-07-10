/*
 * ResultSetTableCellRenderer.java
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

package org.executequery.gui.resultset;

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.executequery.Constants;
import org.underworldlabs.util.MiscUtils;
import org.underworldlabs.util.SystemProperties;

// much of this from the article Christmas Tree Applications at
// http://java.sun.com/products/jfc/tsc/articles/ChristmasTree
// and is an attempt at a better performing cell renderer for the
// results table.

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1502 $
 * @date     $Date: 2009-04-05 23:00:46 +1000 (Sun, 05 Apr 2009) $
 */
class ResultSetTableCellRenderer extends DefaultTableCellRenderer {
    
    private Color background;
    private Color foreground;
    
    private Color selectionForeground;
    private Color selectionBackground;
    
    private Color tableForeground;
    private Color tableBackground;
    
    private Color editableForeground;
    private Color editableBackground;
    
    private Border focusBorder;
    
    private SimpleDateFormat dateFormat;

    private String nullValueDisplayString;
    private Color nullValueDisplayColor;
    
    ResultSetTableCellRenderer() {
        
        focusBorder = loadUIBorder("Table.focusCellHighlightBorder");
        editableForeground = loadUIColour("Table.focusCellForeground");
        editableBackground = loadUIColour("Table.focusCellBackground");
        selectionForeground = loadUIColour("Table.selectionForeground");
        selectionBackground = loadUIColour("Table.selectionBackground");
        tableForeground = loadUIColour("Table.foreground");

        applyUserPreferences();
    }

    private Border loadUIBorder(String key) {
        return UIManager.getBorder(key);
    }

    private Color loadUIColour(String key) {
        return UIManager.getColor(key);
    }
    
    public Component getTableCellRendererComponent(
				JTable table, Object value,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
        
        if (isSelected) {
            
            setForeground(selectionForeground);
            setBackground(selectionBackground);

        } else {
            
            if (tableBackground == null) {
                
                tableBackground = table.getBackground();
            }

            setForeground(tableForeground);
            setBackground(tableBackground);
        }
        
        if (hasFocus) {
            
            setBorder(focusBorder);
            
            if (table.isCellEditable(row, column)) {
                
                setForeground(editableForeground);
                setBackground(editableBackground);
            }
            
        } else {
            
            setBorder(noFocusBorder);
        }

        formatValueForDisplay(value, isSelected);
        
        return this;
    }

    private void formatValueForDisplay(Object value, boolean isSelected) {

        if (value != null) {

        	if (value instanceof RecordDataItem) {
        		
        		RecordDataItem recordDataItem = (RecordDataItem) value;

        		if (recordDataItem.isValueNull()) {
        			
        			formatValueForDisplay(null, isSelected);
        			return;

        		} else {
        			
        			formatValueForDisplay(recordDataItem.getDisplayValue(), isSelected);
        			return;
        		}
        		
        	}
        	
            if (!isDateValue(value)) {

                setValue(value);

            } else {

                setValue(dateFormatted((Date)value));
            }
            

        } else {

            setValue(nullValueDisplayString);
            if (!isSelected) {
            
                setBackground(nullValueDisplayColor);
            }

        }

    }
    
    private boolean isDateValue(Object value) {

        return (value instanceof Date);
    }

    public void applyUserPreferences() {

        String datePattern = SystemProperties.getProperty(
                Constants.USER_PROPERTIES_KEY, "resuts.date.pattern");

        if (!MiscUtils.isNull(datePattern)) {

            dateFormat = new SimpleDateFormat(datePattern);

        } else {
            
            dateFormat = null;
        }
        
        nullValueDisplayColor = SystemProperties.getColourProperty(
                Constants.USER_PROPERTIES_KEY, "results.table.cell.null.background.colour");

        nullValueDisplayString = SystemProperties.getStringProperty(
                Constants.USER_PROPERTIES_KEY, "results.table.cell.null.text");
    }

    private String dateFormatted(Date date) {

        if (dateFormat != null) {

            return dateFormat.format(date);

        } else {
            
            return date.toString();
        }
    }

    public void setTableBackground(Color c) {
        
        this.tableBackground = c;
    }
    
    public void setBackground(Color c) {
        
        this.background = c;
    }
    
    public Color getBackground() {
        
        return background;
    }
    
    public void setForeground(Color c) {
        
        this.foreground = c;
    }
    
    public Color getForeground() {
        
        return foreground;
    }
    
    public boolean isOpaque() {
        
        return background != null;
    }
    
    public void invalidate() {}
    
    public void repaint() {}
    
    public void firePropertyChange(
            String propertyName, boolean oldValue, boolean newValue) {}
    
    protected void firePropertyChange(
            String propertyName, Object oldValue, Object newValue) {}
    
}







