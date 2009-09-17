package org.executequery.gui.editor;

import org.executequery.databasemediators.DatabaseConnection;

public interface ConnectionChangeListener {

    void connectionChanged(DatabaseConnection databaseConnection);
    
}
