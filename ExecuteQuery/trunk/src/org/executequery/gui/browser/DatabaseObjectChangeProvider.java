package org.executequery.gui.browser;

import java.text.ParseException;

import javax.swing.JOptionPane;

import org.executequery.GUIUtilities;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.databaseobjects.NamedObject;
import org.underworldlabs.jdbc.DataSourceException;

public class DatabaseObjectChangeProvider {

    public boolean applyChanges(NamedObject namedObject) {
        
        return applyChanges(namedObject, false);
    }
    
    public boolean applyChanges(NamedObject namedObject, boolean showDialog) {

        if (namedObject instanceof DatabaseTable) {
            
            DatabaseTable table = (DatabaseTable) namedObject;
            if (table.isAltered()) {
             
                if (showDialog) {
                
                    return apply(table);
                    
                } else {
                    
                    execute(table);
                }
            }
            
        }
        
        return true;
    }

    private boolean apply(DatabaseTable table) {

        int yesNo = GUIUtilities.displayConfirmCancelDialog("Do you wish to apply your changes?");
        if (yesNo == JOptionPane.NO_OPTION) {

            table.revert();

        } else if (yesNo == JOptionPane.CANCEL_OPTION) {

            return false;

        } else if (yesNo == JOptionPane.YES_OPTION) {

            execute(table);
        }

        return true;
    }

    private void execute(DatabaseTable table) {

        try {

            table.applyChanges();

        } catch (DataSourceException e) {

            StringBuilder sb = new StringBuilder();
            sb.append("An error occurred applying the specified changes.").
               append("\n\nThe system returned:\n");

            Throwable cause = e.getCause();
            if (cause instanceof NumberFormatException) {
                
                sb.append("Invalid number for value - ");
            
            } else if (cause instanceof ParseException) {
                
                sb.append("Invalid date format for value - ");
            }
            sb.append(e.getExtendedMessage());
            
            if (table.hasTableDataChanges()) {
                
                sb.append("\n\nRollback was issued for all data changes.");
            }
            
            throw new DataSourceException(sb.toString(), e.getCause());
        }
    }

    
    
}
