package org.executequery.gui.resultset;

import org.underworldlabs.swing.table.TableCellValue;

/**
 * 
 * @author Takis Diakoumis
 * @version $Revision: 1479 $
 * @date $Date: 2009-03-13 02:18:53 +1100 (Fri, 13 Mar 2009) $
 */
public interface RecordDataItem extends TableCellValue {

    int length();
    
	int getDataType();

	Object getDisplayValue();
	
	void setValue(Object value);

	boolean isValueNull();
	
	void setNull();

}