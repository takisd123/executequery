package org.executequery.gui.resultset;

public class StringRecordDataItem extends SimpleRecordDataItem {

    private static final int DATA_TYPE_INT = -1;

    private static final String DATA_TYPE_NAME = "Simple String Record Data Item";
    
    public StringRecordDataItem(String value) {

        super(DATA_TYPE_INT, DATA_TYPE_NAME);
        setValue(value);
    }
    
}
