package org.underworldlabs.swing.table;

import javax.swing.table.TableModel;

public interface SortableTableModel extends TableModel {

	boolean canSortColumn(int column);
	
}
