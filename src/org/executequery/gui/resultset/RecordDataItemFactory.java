/*
 * RecordDataItemFactory.java
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

import java.sql.Types;


public class RecordDataItemFactory {

	public RecordDataItem create(int dataType, String dataTypeName) {

		switch (dataType) {

	        case Types.CLOB:
	        case Types.LONGVARCHAR:
	        	return new ClobRecordDataItem(dataType, dataTypeName);

	        case Types.BLOB:
	        case Types.BINARY:
	        case Types.VARBINARY:
	        case Types.LONGVARBINARY:
	        	return new BlobRecordDataItem(dataType, dataTypeName);

			default:
				return new SimpleRecordDataItem(dataType, dataTypeName);

		}
		
	}
	
}

