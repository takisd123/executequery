package org.executequery.gui.browser;

import org.underworldlabs.swing.print.AbstractPrintableTableModel;

public abstract class AbstractDatabaseTableViewModel extends AbstractPrintableTableModel {

    public String getPrintValueAt(int row, int col) {
        Object value = getValueAt(row, col);
        if (value != null && col > 0) {
            return value.toString();
        }
        return "";
    }

}
