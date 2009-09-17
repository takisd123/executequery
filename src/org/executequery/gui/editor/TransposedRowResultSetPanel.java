package org.executequery.gui.editor;

import javax.swing.table.TableColumnModel;

import org.executequery.gui.resultset.ResultSetTableModel;

public class TransposedRowResultSetPanel extends ResultSetPanel {

    private static final int COLUMN_NAME_COLUMN_WIDTH = 200;
    
    private static final int VALUE_COLUMN_WIDTH = 500;

    public TransposedRowResultSetPanel(
            ResultSetTableContainer resultSetTableContainer, ResultSetTableModel model) {
        
        super(resultSetTableContainer);
        setResultSet(model, false);
     
        setTableProperties();
    }

    public void setTableProperties() {
        
        super.setTableProperties();

        TableColumnModel columnModel = getTable().getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(COLUMN_NAME_COLUMN_WIDTH);
        columnModel.getColumn(1).setPreferredWidth(VALUE_COLUMN_WIDTH);
    }
    
}
