package org.underworldlabs.swing.table;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractSortableTableModel extends AbstractTableModel 
                                                 implements SortableTableModel {

    public boolean canSortColumn(int column) {

        return true;
    }
    
}
