package org.executequery.gui.browser.sql;

import java.util.List;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.gui.resultset.RecordDataItem;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class TableDataChange {

    private final DatabaseTable databaseTable;
    private final List<RecordDataItem> rowDataForRow;

    public TableDataChange(DatabaseTable databaseTable, List<RecordDataItem> rowDataForRow) {
        this.databaseTable = databaseTable;
        this.rowDataForRow = rowDataForRow;
    }


    public DatabaseTable getDatabaseTable() {
        return databaseTable;
    }

    public List<RecordDataItem> getRowDataForRow() {
        return rowDataForRow;
    }
}
