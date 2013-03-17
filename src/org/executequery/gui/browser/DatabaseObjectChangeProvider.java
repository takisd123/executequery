package org.executequery.gui.browser;

import java.text.ParseException;

import javax.swing.JOptionPane;

import org.executequery.GUIUtilities;
import org.executequery.databaseobjects.DatabaseTable;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.gui.ErrorMessagePublisher;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.InterruptibleProgressDialog;
import org.underworldlabs.swing.util.Interruptible;
import org.underworldlabs.swing.util.SwingWorker;

public class DatabaseObjectChangeProvider implements Interruptible {
    
    private SwingWorker worker;

    private NamedObject namedObject;

    private InterruptibleProgressDialog interruptibleProgressDialog;
    
    public DatabaseObjectChangeProvider(NamedObject namedObject) {

        this.namedObject = namedObject;
    }
    
    public boolean applyChanges() {
        
        return applyChanges(false);
    }
    
    public boolean applyChanges(boolean showDialog) {

        // dialog

        // need to run in worker
        
        if (isTable() && table().isAltered()) {
        
            if (showDialog) {
            
                return apply();
                
            } else {
                
                execute();
            }
        }
        
        return true;
    }

    private boolean apply() {

        int yesNo = GUIUtilities.displayConfirmCancelDialog("Do you wish to apply your changes?");
        if (yesNo == JOptionPane.NO_OPTION) {

            table().revert();

        } else if (yesNo == JOptionPane.CANCEL_OPTION) {

            return false;

        } else if (yesNo == JOptionPane.YES_OPTION) {

            execute();
        }

        return true;
    }

    public void interrupt() {
        
        if (worker != null) {
            
            table().cancelChanges();
            worker.interrupt();
        }
        
    }

    public void setCancelled(boolean cancelled) {}
    
    private void execute() {

        interruptibleProgressDialog = new InterruptibleProgressDialog(GUIUtilities.getParentFrame(), "Applying changes", "Please wait...", this);

        worker = new SwingWorker() {
            
            @Override
            public Object construct() {

                try {

                    table().applyChanges();
        
                } catch (DataSourceException e) {
        
                    StringBuilder sb = new StringBuilder();
                    sb.append("An error occurred applying the specified changes.\n\nThe system returned:\n");
        
                    Throwable cause = e.getCause();
                    if (cause instanceof NumberFormatException) {
                        
                        sb.append("Invalid number for value - ");
                    
                    } else if (cause instanceof ParseException) {
                        
                        sb.append("Invalid date format for value - ");
                    }
                    sb.append(e.getExtendedMessage());
                    
                    if (table().hasTableDataChanges()) {
                        
                        sb.append("\nRollback was issued for all data changes.");
                    }

                    dispose();
                    ErrorMessagePublisher.publish(sb.toString(), e.getCause());
                }

                return "done";
            }
            
            @Override
            public void finished() {

                dispose();
            }
            
        };
     
        worker.start();
        interruptibleProgressDialog.run();
    }

    private void dispose() {

        if (interruptibleProgressDialog != null) {
            
            interruptibleProgressDialog.dispose();
            interruptibleProgressDialog = null;
        }        
    }
    
    private boolean isTable() {
        
        return (table() != null);
        
    }
    
    private DatabaseTable table() {

        if (namedObject instanceof DatabaseTable) {
        
            return (DatabaseTable) namedObject;
        }
        return null;
    }
    
    
    
}
