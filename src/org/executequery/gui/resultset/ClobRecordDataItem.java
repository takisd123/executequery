package org.executequery.gui.resultset;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import org.executequery.Constants;
import org.executequery.log.Log;
import org.underworldlabs.util.SystemProperties;

public class ClobRecordDataItem extends AbstractLobRecordDataItem {

	private int displayLength;

	private String displayValue;
	
	public ClobRecordDataItem(int dataType, String dataTypeName) {

		super(dataType, dataTypeName);
		
		displayLength = SystemProperties.getIntProperty(
                Constants.USER_PROPERTIES_KEY, "results.table.clob.length");
	}

	@Override
	public Object getDisplayValue() {

		if (displayValue == null) {

		    displayValue = new String(getData());
		    if (displayValue.length() > displayLength) {
		    
		        displayValue = displayValue.substring(0, displayLength);
		    }
		}
		
		return displayValue;
	}
	
	public String getLobRecordItemName() {

		return getDataTypeName();
	}

    protected byte[] readLob() {
    	
        Object value = getValue();

        if (value instanceof String) {

            return ((String) value).getBytes();
        }

    	Clob clob = (Clob) value;

    	Reader reader;
    	Writer writer = new StringWriter();
    	
		try {

			reader = clob.getCharacterStream();

		} catch (SQLException e) {

			if (Log.isDebugEnabled()) {
			
				Log.debug("Error reading CLOB data", e);
			}

			return e.getMessage().getBytes();
		}

        try {
        
            int read;

            while ((read = reader.read()) > -1) {
	
	            writer.write(read);
	        }
	        
	        writer.flush();

		} catch (IOException e) {

			if (Log.isDebugEnabled()) {
				
				Log.debug("Error reading CLOB data", e);
			}

			return e.getMessage().getBytes();
		
		} finally {
		    
            try {
                
                if (writer != null) {
                    
                    writer.close();
                }
                
            } catch (IOException e) {}

		}

    	return writer.toString().trim().getBytes();
    }
    
}
