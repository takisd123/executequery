/*
 * RecordDataItemFactory.java
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

import java.sql.Types;


public class RecordDataItemFactory {

	public RecordDataItem create(String name, int dataType, String dataTypeName) {

		switch (dataType) {

    		case Types.BIT:
    		case Types.TINYINT:
    		case Types.SMALLINT:
    		case Types.INTEGER:
    		case Types.BIGINT:
    		case Types.FLOAT:
    		case Types.REAL:
    		case Types.DOUBLE:
    		case Types.NUMERIC:
    		case Types.DECIMAL:
    		case Types.CHAR:
    		case Types.VARCHAR:
    		case Types.NULL:
    		case Types.OTHER:
    		case Types.JAVA_OBJECT:
    		case Types.DISTINCT:
    		case Types.STRUCT:
    		case Types.ARRAY:
    		case Types.REF:
    		case Types.DATALINK:
    		case Types.BOOLEAN:
    		case Types.ROWID:
    		case Types.NCHAR:
    		case Types.NVARCHAR:
    		case Types.LONGNVARCHAR:
    		case Types.NCLOB:
    		case Types.SQLXML:
    		    return new SimpleRecordDataItem(name, dataType, dataTypeName);
		
    		case Types.DATE:
    		case Types.TIMESTAMP:
    		case Types.TIME:
    		    return new DateRecordDataItem(name, dataType, dataTypeName);
    		    
	        case Types.CLOB:
	        case Types.LONGVARCHAR:
	        	return new ClobRecordDataItem(name, dataType, dataTypeName);

	        case Types.BLOB:
	        case Types.BINARY:
	        case Types.VARBINARY:
	        case Types.LONGVARBINARY:
	        	return new BlobRecordDataItem(name, dataType, dataTypeName);

		}

		return new SimpleRecordDataItem(name, dataType, dataTypeName);
	}
	
}




