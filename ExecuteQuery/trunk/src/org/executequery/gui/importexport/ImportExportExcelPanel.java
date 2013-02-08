/*
 * ImportExportExcelPanel.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.executequery.gui.importexport;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.executequery.ActiveComponent;
import org.executequery.GUIUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.gui.ActionContainer;
import org.executequery.log.Log;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.underworldlabs.swing.wizard.DefaultWizardProcessModel;
import org.underworldlabs.swing.wizard.WizardProcessPanel;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class ImportExportExcelPanel extends WizardProcessPanel
                                    implements ImportExportProcess,
                                               ActiveComponent {
    
    /** The dimension of each child panel */
    private Dimension childDim;
    
    /** The object to retrieve table details */
    private MetaDataValues metaData;
    
    /** The worker that will run the process */
    private ImportExportWorker worker;
    
    /** The first panel displayed */
    private ImportExportExcelPanel_1 firstPanel;
    
    /** The second panel displayed */
    private ImportExportPanel_2 secondPanel;
    
    /** The third panel displayed */
    private ImportExportPanel_3 thirdPanel;
    
    /** The fourth panel displayed - export */
    private ImportExportExcelPanel_4 fourthPanel;
    
    /** The fourth panel displayed - import */
    private ImportXMLPanel_4 fourthPanel_im;
    
    /** The fifth panel displayed */
    private ImportExportExcelPanel_5 fifthPanel;
    
    /** The sixth panel displayed */
    //  private ImportExportXMLPanel_6 sixthPanel;
    /** The progress panel */
    private ImportExportProgressPanel progressPanel;
    
    /** The type of transfer - import/export */
    private int transferType;
    
    /** Whether the process was cancelled */
    private boolean processCancelled;
    
    /** Whether a transfer is currently underway */
    private boolean processing;
    
    /** the parent container */
    private ActionContainer parent;

    /** the wizard model */
    private TransferExcelWizardModel model;
    
    public ImportExportExcelPanel(ActionContainer parent, int transferType) {
        this(parent, transferType, null, null, null);
    }
    
    public ImportExportExcelPanel(ActionContainer parent, 
                                  int transferType,
                                  DatabaseConnection databaseConnection,
                                  String schemaName,
                                  String tableName) {

        this.transferType = transferType;
        model = new TransferExcelWizardModel();
        setModel(model);

        this.parent = parent;

        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        if (databaseConnection != null) {
            firstPanel.setDatabaseConnection(databaseConnection);
            next();
            
            if (schemaName != null) {
                secondPanel.setSelectedSchema(schemaName);
                if (tableName != null) {
                    secondPanel.setSelectedTable(tableName);
                    secondPanel.selectAllAvailable();
                }

            }

        }

    }
    
    private void jbInit() throws Exception {
        metaData = new MetaDataValues();
        childDim = new Dimension(525, 340);
        
        // set the help action
        setHelpAction(ActionBuilder.get("help-command"), "export-excel");
       
        firstPanel = new ImportExportExcelPanel_1(this);
        model.addPanel(firstPanel);
        prepare();
    }
    
    /**
     * Returns the transfer format - XML, CSV etc.
     */
    public int getTransferFormat() {
        return EXCEL;
    }

    public boolean isExport() {

        int type = getTransferType();
        
        return type == EXPORT || 
            type == EXPORT_XML || 
            type == EXPORT_DELIMITED;
    }

    /**
     * Releases database resources before closing.
     */
    public void cleanup() {
        metaData.closeConnection();
    }

    public String getSchemaName() {
        return secondPanel.getSelectedSchema();
    }
    
    /** <p>Cancels the current in-process transfer */
    public void cancelTransfer() {
        processCancelled = true;
    }
    
    /** <p>Return whether this process is an
     *  import or export process.
     *
     *  @return <code>ImportExportProcess.IMPORT |
     *          ImportExportProcess.EXPORT</code>
     */
    public int getTransferType() {
        return transferType;
    }
    
    /** <p>Retrieves the selected rollback size for
     *  the transfer.
     *
     *  @return the rollback size
     */
    public int getRollbackSize() {
        return fifthPanel.getRollbackSize();
    }
    
    /** <p>Retrieves the action on an error occuring
     *  during the import/export process.
     *
     *  @return the action on error -<br>either:
     *          <code>ImportExportProcess.LOG_AND_CONTINUE</code> or
     *          <code>ImportExportProcess.STOP_TRANSFER</code>
     */
    public int getOnError() {
        return fifthPanel.getOnError();
    }
    
    /** <p>Retrieves the date format for date fields
     *  contained within the data file/database table.
     *
     *  @return the date format (ie. ddMMyyy)
     */
    public String getDateFormat() {
        return fifthPanel.getDateFormat();
    }

    /**
     * Returns whether to parse date values.
     *
     * @return true | false
     */
    public boolean parseDateValues() {
        return false;
    }

    /** 
     * Retrieves the selected type of delimiter within
     * the file to be used with this process.
     *
     * @return the selected delimiter
     */
    public String getDelimiter() {
        return "";
    }

    public void setProcessComplete(boolean success) {
        setButtonsEnabled(true);
        setNextButtonEnabled(false);
        setBackButtonEnabled(true);
        setCancelButtonEnabled(true);

        if (success) {
            setCancelButtonText("Finish");
        }
        processing  = false;        
    }
    
    public JDialog getDialog() {
        if (parent.isDialog()) {
            return (JDialog)parent;
        }
        return null;
    }

    public void doImport() {}
    
    /** <p>Begins an export process. */
    public void doExport() {
        Log.info("Beginning data export process");
        processCancelled = false;
        setNextButtonEnabled(false);
        setCancelButtonEnabled(false);
        setBackButtonEnabled(false);
        worker = new ExportExcelWorker(this, progressPanel);
    }
    
    /** <p>Retrieves the selected tables for this process.
     *
     *  @return the selected table names
     */
    public String[] getSelectedTables() {
        return secondPanel.getSelectedTables();
    }
    
    /** <p>Retrieves the table name for this process in
     *  the case of a single table import/export.
     *
     *  @return the table name
     */
    public String getTableName() {
        return secondPanel.getSelectedTables()[0];
    }
    
    /** <p>Retrieves the column names for this process.
     *
     *  @return the column names
     */
    public Vector getSelectedColumns() {
        return secondPanel.getSelectedColumns();
    }
    
    /** <p>Returns a <code>Vector</code> of <code>
     *  DataTransferObject</code> objects containing
     *  all relevant data for the process.
     *
     *  @return a <code>Vector</code> of
     *          <code>DataTransferObject</code> objects
     */
    public Vector getDataFileVector() {
        return thirdPanel.getDataFileVector();
    }
    
    public String[][] getSheetNameValues() {
        return fourthPanel.getSheetNameValues();
    }
    
    public boolean mapDataTypesToCells() {
        return fourthPanel.mapDataTypesToCells();
    }
    
    /**
     * Returns whether to include column names as the
     * first row of a delimited export process.
     *
     * @return true | false
     */
    public boolean includeColumnNames() {
        return fourthPanel.includeColumnNamesRowOne();
    }
    
    /**
     * Returns whether to trim whitespace on column data values.
     *
     * @return true | false
     */
    public boolean trimWhitespace() {
        return false;
    }

    // returns single or multiple table export
    public int getTableTransferType() {
        return firstPanel.getTableTransferType();
    }
    
    /** <p>Returns the type of multiple table
     *  transfer - single or multiple file.
     *
     *  @return the type of multiple table transfer
     */
    public int getMutlipleTableTransferType() {
        return firstPanel.getMutlipleTableTransferType();
    }

    public boolean isSingleFileExport() {
        return getMutlipleTableTransferType() == SINGLE_FILE;
    }
    
    /** <p>Indicates whether the process (import only)
     *  should be run as a batch process.
     *
     *  @return whether to run as a batch process
     */
    public boolean runAsBatchProcess() {
        return fifthPanel.runAsBatchProcess();
    }
    
    public int getXMLFormat() {
        return -1;
    }

    private boolean doNext() {
        JPanel nextPanel = null;
        int index = model.getSelectedIndex();
        switch (index) {
            
            case 0:
                DatabaseConnection dc = getDatabaseConnection();
                if (dc != null) {
                    metaData.setDatabaseConnection(dc);
                }

                if (secondPanel == null) {
                    secondPanel = new ImportExportPanel_2(this);
                }
                nextPanel = secondPanel;
                
                secondPanel.setListData(getTableTransferType());
                break;
                
            case 1:
                
                if (!secondPanel.hasSelections()) {
                    if (getTableTransferType() == ImportExportProcess.MULTIPLE_TABLE) {
                        GUIUtilities.displayErrorMessage(
                        "You must select at least one table");
                    }
                    else if (getTableTransferType() == ImportExportProcess.SINGLE_TABLE) {
                        GUIUtilities.displayErrorMessage(
                        "You must select at least one column");
                    }
                    return false;
                }
                
                if (thirdPanel == null) {
                    thirdPanel = new ImportExportPanel_3(this);
                } else {
                    thirdPanel.buildTable();
                }
                nextPanel = thirdPanel;
                break;
                
            case 2:
                
                if (!thirdPanel.transferObjectsComplete()) {
                    return false;
                }

                if (fourthPanel == null) {
                    fourthPanel = new ImportExportExcelPanel_4(this);
                }
                nextPanel = fourthPanel;

                fourthPanel.reset(getSelectedTables());
                break;
                
            case 3:
                
                if (!fourthPanel.entriesComplete()) {
                    GUIUtilities.displayErrorMessage(
                            "Please ensure all required fields been entered correctly.");
                    return false;
                }
                
                if (fifthPanel == null) {
                    fifthPanel = new ImportExportExcelPanel_5(this);
                }
                nextPanel = fifthPanel;
                break;

            case 4:
                
                if (progressPanel == null) {
                    progressPanel = new ImportExportProgressPanel(this);
                }
                processing = true;
                model.addPanel(progressPanel);
                
                if (transferType == EXPORT) {
                    doExport();
                } else if (transferType == IMPORT) {
                    doImport();
                }
                setButtonsEnabled(false);
                return true;
                
        }
        
        model.addPanel(nextPanel);
        return true;
    }
    
    /**
     * Defines the action for the BACK button.
     */
    private boolean doPrevious() {
        // make sure the cancel button says cancel
        setCancelButtonText("Cancel");
        return true;
    }

    /** 
     * Stops the current process. 
     */
    public void stopTransfer() {
        setButtonsEnabled(true);
        if (processing) {
            worker.cancelTransfer();
            setBackButtonEnabled(true);
            processCancelled = true;
        }
    }

    /** 
     * Defines the action for the CANCEL button.
     */
    public void cancel() {
        setButtonsEnabled(true);
        if (processing) {
            worker.cancelTransfer();
            setBackButtonEnabled(true);
            processCancelled = true;
        }
        else {
            worker = null;
            parent.finished();
        }        
    }
    
    /**
     * Returns the selected database connection properties object.
     *
     * @return the connection properties object
     */
    public DatabaseConnection getDatabaseConnection() {
        return firstPanel.getDatabaseConnection();
    }

    public Dimension getChildDimension() {
        return childDim;
    }
    
    public MetaDataValues getMetaDataUtility() {
        return metaData;
    }
    

    private class TransferExcelWizardModel extends DefaultWizardProcessModel {
        
        public TransferExcelWizardModel() {
            int type = getTransferType();
            String firstTitle = "Database Connection and Export Type";
            String lastTitle = "Exporting Data...";
            if (type == ImportExportProcess.IMPORT) {
                firstTitle = "Database Connection and Import Type";
                lastTitle = "Importing Data...";
            }

            String[] titles = {firstTitle,
                               "Table Selection",
                               "Data File Selection",
                               "Spreadsheet Options",
                               "Options",
                               lastTitle};
            setTitles(titles);

            String[] steps = {"Select database connection and transfer type",
                              "Select the tables/columns",
                              type == ImportExportProcess.IMPORT ?
                                  "Select the data file(s) to import from" :
                                  "Select the data file(s) to export to",
                              "Set any spreadsheet specific options",
                              "Set any further transfer options",
                              type == ImportExportProcess.IMPORT ?
                                  "Import the data" :
                                  "Export the data"};
            setSteps(steps);
        }

        public boolean previous() {
            if (doPrevious()) {
                return super.previous();
            }
            return false;
        }
        
        public boolean next() {
            if (doNext()) {
                return super.next();
            }
            return false;
        }

    }

}













