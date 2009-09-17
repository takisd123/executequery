package org.executequery.gui.editor;

import java.util.List;

import org.executequery.gui.resultset.RecordDataItem;
import org.executequery.gui.resultset.ResultSetTableModel;

public class TransposedRowResultSetTableModel extends ResultSetTableModel {

	public TransposedRowResultSetTableModel(List<String> columnHeaders,
			List<List<RecordDataItem>> tableData) {
		
		super(columnHeaders, tableData);
	}

	@Override
	public boolean canSortColumn(int column) {

		return (column != 1);
	}
	
}
