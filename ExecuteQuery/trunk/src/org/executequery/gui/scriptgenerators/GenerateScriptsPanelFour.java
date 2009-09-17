/*
 * GenerateScriptsPanelFour.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
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

package org.executequery.gui.scriptgenerators;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.executequery.ApplicationException;
import org.executequery.GUIUtilities;
import org.executequery.databaseobjects.NamedObject;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.swing.wizard.InterruptibleWizardProcess;
import org.underworldlabs.swing.wizard.WizardProgressBarPanel;

/**
 * Step three panel in the generate scripts wizard.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class GenerateScriptsPanelFour extends JPanel 
                                      implements InterruptibleWizardProcess,
                                                 ScriptGenerationObserver {
    
    /** result indicator for success */
    private static final String SUCCESS = "success";

    /** result indicator for success */
    private static final String FAILED = "failed";

    /** result indicator for success */
    private static final String CANCELLED = "cancelled";
    
    /** the view script when done check box */
    private JCheckBox viewScriptCheck;
    
    /** the progress bar panel */
    private WizardProgressBarPanel progressPanel;
    
    /** the parent controller */
    private GenerateScriptsWizard parent;

    /** The worker thread */
    private SwingWorker worker;
    
    /** Creates a new instance of GenerateScriptsPanelFour */
    public GenerateScriptsPanelFour(GenerateScriptsWizard parent) {

        super(new GridBagLayout());

        this.parent = parent;
        
        try {
        
            init();
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
    }

    private void init() throws Exception {

        viewScriptCheck = new JCheckBox("View generated script", true);
        viewScriptCheck.setEnabled(false);

        progressPanel = new WizardProgressBarPanel(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx++;
        gbc.gridy++;
        gbc.insets = new Insets(10,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(progressPanel, gbc);
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets.top = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(viewScriptCheck, gbc);
    }

    /**
     * The status of the view script check box.
     */
    protected boolean viewScriptOnCompletion() {

        return viewScriptCheck.isSelected();
    }
    
    /**
     * Starts the generation process in a worker thread.
     */
    protected void start() {

        worker = new SwingWorker() {

            public Object construct() {
            
                progressPanel.reset();
                viewScriptCheck.setEnabled(false);
                
                progressPanel.setMinimum(0);
                progressPanel.setMaximum(parent.getSelectedItemCount() + 1);

                progressPanel.setProgressStatus(1);
                progressPanel.appendProgressText("\nLoading required references...\n");

                if (isCreateTableScript()) {

                    return createTableScript();

                } else {
                    
                    return dropTableScript();
                }

            }
            public void finished() {

                GUIUtilities.scheduleGC();

                boolean success = (get() == SUCCESS);

                viewScriptCheck.setEnabled(success);
                parent.finished(success);
            }
        };

        worker.start();
    }
    
    private boolean isCreateTableScript() {
        
        return parent.getScriptType() == GenerateScriptsWizard.CREATE_TABLES;
    }
    
    private Object createTableScript() {
        
        try {
        
            SchemaTablesScriptGenerator generator = createScriptGenerator();
    
            generator.writeCreateTablesScript(parent.getConstraintsStyle());
            
            finished();
            
            return SUCCESS;
          
        } catch (ApplicationException e) {

            return handleApplicationException(e);
        }

    }

    private Object dropTableScript() {
        
        try {
        
            SchemaTablesScriptGenerator generator = createScriptGenerator();
    
            generator.writeDropTablesScript(parent.cascadeWithDrop());

            finished();
            
            return SUCCESS;
          
        } catch (ApplicationException e) {

            return handleApplicationException(e);
        }

    }

    private void finished() {

        progressPanel.finished();
        
        progressPanel.appendProgressText(
                "\nScript generated successfully to " + outputFile().getName());

        progressPanel.appendProgressText("Done.\n\n");
    }

    private Object handleApplicationException(ApplicationException e) {

        if (e.getCause() instanceof InterruptedException) {
            
            processCancelled();
            
            return CANCELLED;
        }

        progressPanel.appendExceptionError(
                "Error writing to ouput file.", e.getCause());

        return FAILED;
    }

    private void processCancelled() {

        progressPanel.finished();
        
        progressPanel.appendProgressText(
                "Script generation cancelled on user request");

        File file = outputFile();
        
        if (file != null && file.exists()) {

            file.delete();
        }
        
    }

    private File outputFile() {

        return new File(parent.getOutputFilePath());
    }

    private SchemaTablesScriptGenerator createScriptGenerator() {

        SchemaTablesScriptGenerator generator = 
            new SchemaTablesScriptGenerator(
                    parent.getScriptType(),
                    parent.getOutputFilePath(),
                    parent.getSelectedSource(),
                    parent.getSelectedTables());

        generator.addScriptGenerationObserver(this);
        
        return generator;
    }

    public void stop() {

        worker.interrupt();
    }

    private int count = 1;
    
    public void finishedNamedObjectScript(NamedObject namedObject) {

        progressPanel.setProgressStatus(++count);        
    }

    public void startedNamedObjectScript(NamedObject namedObject) {

        if (isCreateTableScript()) {
        
            progressPanel.appendProgressText(
                    "Generating CREATE TABLE " + namedObject.getName());
            
        } else {
            
            progressPanel.appendProgressText(
                    "Generating DROP TABLE " + namedObject.getName());            
        }        
        
    }
            
}

