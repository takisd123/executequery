package org.executequery.gui.resultset;

public abstract class AbstractLobRecordDataItem extends AbstractRecordDataItem 
												implements LobRecordDataItem {

	private byte[] data;
	
	public AbstractLobRecordDataItem(int dataType, String dataTypeName) {
		
		super(dataType, dataTypeName);
	}

	public int length() {
		
		return (data == null ? 0 : data.length);
	}
	
	public byte[] getData() {
		
		if (data == null) {
			
			data = readLob();
		}
		
		return data;
	}

	abstract byte[] readLob();
	
    public String asBinaryString() {
        
        StringBuilder sb = new StringBuilder();

        char space = ' ';

        String stripPrefix = "ffffff";

        int defaultBytesToProcess = 496;
        int bytesToProcess = Math.min(data.length, defaultBytesToProcess);

        for (int i = 0; i < bytesToProcess; i++) {
            
            String hexString = Integer.toHexString(data[i]);

            if (hexString.startsWith(stripPrefix)) {

                hexString = hexString.substring(stripPrefix.length());
            }

            if (hexString.length() == 1) {
                
                sb.append('0');
            }

            sb.append(hexString.toUpperCase()).append(space);            
        }

        if (bytesToProcess == defaultBytesToProcess) {
        
            sb.append("..");
        }
        
        return sb.toString();
    }

}
