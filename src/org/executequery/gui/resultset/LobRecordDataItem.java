package org.executequery.gui.resultset;


public interface LobRecordDataItem extends RecordDataItem {
	
	int length();
	
	byte[] getData();
	
	String getLobRecordItemName();
	
	String asBinaryString();
	
}
