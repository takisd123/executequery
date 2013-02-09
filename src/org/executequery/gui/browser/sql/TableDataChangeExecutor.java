package org.executequery.gui.browser.sql;

import java.util.ArrayList;
import java.util.List;
import org.executequery.databaseobjects.DatabaseObject;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.gui.resultset.RecordDataItem;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class TableDataChangeExecutor {

    public void apply(List<TableDataChange> rows) {

        for (TableDataChange tableDataChange : rows) {

            DatabaseTable table = tableDataChange.getDatabaseTable();
            List<RecordDataItem> row = tableDataChange.getRowDataForRow();

            execute(table, row);
        }


    }

    private void execute(DatabaseTable table, List<RecordDataItem> values) {

        List<RecordDataItem> changes = new ArrayList<RecordDataItem>();
        for (RecordDataItem item : values) {

            if (item.isChanged()) {

                changes.add(item);
            }

        }

//        table.prepareStatement();




    }

}
