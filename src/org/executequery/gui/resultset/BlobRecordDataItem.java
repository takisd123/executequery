package org.executequery.gui.resultset;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.executequery.log.Log;
import org.executequery.util.mime.MimeType;
import org.executequery.util.mime.MimeTypes;

public class BlobRecordDataItem extends AbstractLobRecordDataItem {

	private static final String UNKNOWN_TYPE = "Unknown BLOB";

	private static final String BLOB_DATA_OBJECT = "<BLOB Data Object>";
	
	public BlobRecordDataItem(int dataType, String dataTypeName) {

		super(dataType, dataTypeName);
	}

	@Override
	public Object getDisplayValue() {

		return BLOB_DATA_OBJECT;
	}
	
	public String getLobRecordItemName() {
		
        MimeType mimeType = mimeTypeFromByteArray(getData());
        if (mimeType != null) {
         
            return mimeType.getName();
        
        } else {
            
            return UNKNOWN_TYPE;
        }

	}
	
    protected byte[] readLob() {
    	
    	Blob blob = (Blob) getValue();

    	byte[] blobBytes = null;
    	InputStream binaryStream = null;
    	
    	try {

    		blobBytes = blob.getBytes(1, (int) blob.length());

    	} catch (SQLException e) {

			if (Log.isDebugEnabled()) {
				
				Log.debug("Error reading BLOB data", e);
			}

			return e.getMessage().getBytes();

		} finally {
			
			try {
			
				if (binaryStream != null) {
					
					binaryStream.close();
				}
				
			} catch (IOException e) {}
			
		}
    	
    	return blobBytes;
    }
    
    private MimeType mimeTypeFromByteArray(byte[] data) {

        return MimeTypes.get().getMimeType(data);
    }

}
