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
