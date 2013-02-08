/*
 * AbstractRecordDataItem.java
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


/**
 * 
 * @author Takis Diakoumis
 * @version $Revision$
 * @date $Date$
 */
public abstract class AbstractRecordDataItem implements RecordDataItem {

	private Object value;

	private int dataType;

	private String dataTypeName;
	
	private boolean changed;

	public AbstractRecordDataItem(int dataType, String dataTypeName) {

		super();
		this.dataType = dataType;
		this.dataTypeName = dataTypeName;
	}

	public int length() {
	    
	    if (!isValueNull()) {
	    
	        return toString().length();

	    } else {
	        
	        return 0;
	    }
	}
	
	public String getDataTypeName() {
		return dataTypeName;
	}

	public int getDataType() {
		return dataType;
	}

	public Object getDisplayValue() {
		return getValue();
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void valueChanged(Object newValue) {
	    setValue(newValue);
	    changed = true;
	}
	
	public boolean isValueNull() {
		return (value == null);
	}
	
	public String toString() {

		if (getValue() != null) {

			return getValue().toString();
		}

		return null;
	}

	public void setNull() {
		value = null;
	}
	
	public boolean isChanged() {
        return changed;
    }
	
}




