/*
 * CreateIndexPanel.java
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

package org.executequery.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.executequery.ActiveComponent;
import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.base.DefaultTabViewActionPanel;
import org.executequery.components.FileChooserDialog;
import org.executequery.components.MinimumWidthActionButton;
import org.executequery.components.TableSelectionCombosGroup;
import org.executequery.databasemediators.SqlStatementResult;
import org.executequery.databasemediators.spi.DefaultStatementExecutor;
import org.executequery.databasemediators.spi.StatementExecutor;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.event.ApplicationEvent;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.event.DefaultKeywordEvent;
import org.executequery.gui.importexport.ImportExportDataException;
import org.executequery.gui.importexport.ResultSetDelimitedFileWriter;
import org.executequery.log.Log;
import org.underworldlabs.swing.AbstractStatusBarPanel;
import org.underworldlabs.swing.IndeterminateProgressBar;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.util.FileUtils;
import org.underworldlabs.util.MiscUtils;

/** 
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ExecuteSqlScriptPanel extends DefaultTabViewActionPanel
                                  implements NamedView,
                                             FocusComponentPanel,
                                             ActiveComponent,
                                             ConnectionListener {
    
    public static final String TITLE = "Execute SQL Script ";
    public static final String FRAME_ICON = "ExportDelimited16.gif";
    
    private JComboBox connectionsCombo; 

    private JTextField fileNameField;

    private TableSelectionCombosGroup combosGroup;

    private LoggingOutputPanel outputPanel;
    
    private SqlTextPaneStatusBar statusBar;

    private JComboBox actionOnErrorCombo;
    
    private JComboBox actionOnCancelCombo;
    
    private JButton startButton;

    private JButton rollbackButton;
    
    private JButton commitButton;
    
    private JButton stopButton;

    public ExecuteSqlScriptPanel() {

        super(new BorderLayout());

        try  {

            init();

        } catch (Exception e) {
          
            e.printStackTrace();
        }

    }
    
    private void init() throws Exception {
        
        fileNameField = WidgetFactory.createTextField();
        connectionsCombo = WidgetFactory.createComboBox();
        combosGroup = new TableSelectionCombosGroup(connectionsCombo);

        actionOnErrorCombo = WidgetFactory.createComboBox();

        ActionOnError[] actionsOnError = {
                ActionOnError.HALT_ROLLBACK,
                ActionOnError.HALT_COMMIT,
                ActionOnError.CONTINUE
        };        
        
        actionOnErrorCombo.setModel(new DefaultComboBoxModel(actionsOnError));
        
        actionOnCancelCombo = WidgetFactory.createComboBox();

        ActionOnCancel[] actionsOnCancel = {
                ActionOnCancel.HALT_ROLLBACK,
                ActionOnCancel.HALT_COMMIT
        };        
        
        actionOnCancelCombo.setModel(new DefaultComboBoxModel(actionsOnCancel));

        outputPanel = new LoggingOutputPanel();
        statusBar = new SqlTextPaneStatusBar();
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 1));

        JButton button = WidgetFactory.createInlineFieldButton("Browse");
        button.setActionCommand("browse");
        button.addActionListener(this);
        button.setMnemonic('r');

        JPanel mainPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridheight = 1;
        gbc.insets.top = 5;
        gbc.insets.bottom = 5;
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Connection:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(connectionsCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 2;
        mainPanel.add(new JLabel("Action on Error:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(actionOnErrorCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Action on Stop:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(actionOnCancelCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 2;
        mainPanel.add(new JLabel("Input File:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fileNameField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets.left = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(button, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.insets.top = 0;
        gbc.insets.left = 5;
        gbc.insets.bottom = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(outputPanel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weighty = 0;
        gbc.insets.bottom = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusBar, gbc);
        
        mainPanel.setBorder(BorderFactory.createEtchedBorder());

        int minimumButtonWidth = 85;
        startButton = new MinimumWidthActionButton(minimumButtonWidth, this, "Start", "start");
        rollbackButton = new MinimumWidthActionButton(minimumButtonWidth, this, "Commit", "commit");
        commitButton = new MinimumWidthActionButton(minimumButtonWidth, this, "Rollback", "rollback");
        stopButton = new MinimumWidthActionButton(minimumButtonWidth, this, "Stop", "stop");

        rollbackButton.setEnabled(false);
        commitButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 5));
        buttonPanel.add(startButton);
        buttonPanel.add(rollbackButton);
        buttonPanel.add(commitButton);
        buttonPanel.add(stopButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);        
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        EventMediator.registerListener(this);        
    }

    public void browse() {

        FileChooserDialog fileChooser = new FileChooserDialog();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        fileChooser.setDialogTitle("Select Export File Path");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

        int result = fileChooser.showDialog(GUIUtilities.getInFocusDialogOrWindow(), "Select");
        if (result == JFileChooser.CANCEL_OPTION) {

            return;
        }

        File file = fileChooser.getSelectedFile();
        fileNameField.setText(file.getAbsolutePath());
    }

    private boolean fieldsValid() {
        
        String fileName = fileNameField.getText();
        if (StringUtils.isBlank(fileName)) {

            GUIUtilities.displayErrorMessage("Please select an input file");
            return false;            
        
        } else {

            File file = new File(fileName); 
            
            if (!file.exists()) {
                
                GUIUtilities.displayErrorMessage("The selected file does not exists in the file system");
                return false;
            }
            
        }
        
        return true;
    }
    
    public boolean tabViewClosing() {

        cleanup();
        return true;
    }

    public void cleanup() {

        combosGroup.close();

        if (statusBar != null) {
         
            statusBar.cleanup();
        }
        
        EventMediator.deregisterListener(this);
    }

    public boolean canHandleEvent(ApplicationEvent event) {

        return (event instanceof DefaultKeywordEvent) || (event instanceof ConnectionEvent);
    }

    public Component getDefaultFocusComponent() {

        return fileNameField;
    }

    private SwingWorker swingWorker;
    private boolean executing;
    
    public void start() {

        if (executing) {

            if (swingWorker != null) {
                
                swingWorker.interrupt();
            }

        } else {
        
            if (fieldsValid()) {
    
                swingWorker = new SwingWorker() {
                    public Object construct() {

                        executing = true;
                        return execute();
                    }
                    public void finished() {
    
                        try {
    
                            Integer recordCount = (Integer) get();
                            if (recordCount != -1) {
        
                                outputPanel.append("Records transferred: " + recordCount);
                            }
    
                        } finally {

                            executing = false;                         
                        }
                    }
                };
                swingWorker.start();
            }
            
        }
    }
    
    private int execute() {
     
        ResultSet resultSet = null;
        DatabaseHost host = combosGroup.getSelectedHost();
        StatementExecutor statementExecutor = new DefaultStatementExecutor(host.getDatabaseConnection());

        int result = -1;
        long startTime = System.currentTimeMillis();
        SqlStatementResult statementResult = null;

        try {

            String query = null;
            
        //    SqlScriptLoader scriptLoader = new SqlScriptLoader(fileNameField.getText()); 
            
            String script = FileUtils.loadFile(fileNameField.getText());
            
            statusBar.setStatusText("Executing...");
            statusBar.startProgressBar();

            outputPanel.appendAction("Executing:");
            outputPanel.appendActionFixedWidth(query);

            int type = statementExecutor.getQueryType(query);
            statementResult = statementExecutor.execute(type, query);

            if (statementResult.isException()) {
                
                throw statementResult.getSqlException();
                
            } else if (statementResult.isResultSet()) {

                resultSet = statementResult.getResultSet();
                result = writeToFile(resultSet);

            } else {

                outputPanel.appendWarning("The executed query did not return a valid result set");
            }
            
        } catch (SQLException e) {

            if (statementResult != null) {
            
                outputPanel.appendError(statementResult.getErrorMessage());

            } else {

                outputPanel.appendError("Execution error:\n" + e.getMessage());
            }
            
        } catch (ImportExportDataException e) {

            outputPanel.appendError("Execution error:\n" + e.getMessage());

        } catch (InterruptedException e) {
            
            outputPanel.appendWarning("Operation cancelled by user action");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            long endTime = System.currentTimeMillis();

            statusBar.setStatusText("Done");
            statusBar.stopProgressBar();

            outputPanel.append("Duration: " + MiscUtils.formatDuration(endTime - startTime));

            try {
                
                if (resultSet != null) {

                    resultSet.close();
                }
                
                statementExecutor.closeConnection();

            } catch (SQLException e) {

                e.printStackTrace();
            }

            host.close();
            GUIUtilities.scheduleGC();
        }
        
        return result;
    }
    
    private int writeToFile(ResultSet resultSet) throws InterruptedException {

        ResultSetDelimitedFileWriter writer = new ResultSetDelimitedFileWriter(); 
        return 1 ;//writer.write(fileNameField.getText(), 
                //delimiterCombo.getSelectedItem().toString(), resultSet, includeColumNamesCheck.isSelected());
    }

    private static int count = 1;
    public String getDisplayName() {

        return TITLE + (count++);
    }

    public String toString() {

        return getDisplayName();
    }

    // ---------------------------------------------
    // ConnectionListener implementation
    // ---------------------------------------------
    
    /**
     * Indicates a connection has been established.
     * 
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent) {

        combosGroup.connectionOpened(connectionEvent.getDatabaseConnection());
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent) {

        combosGroup.connectionClosed(connectionEvent.getDatabaseConnection());
    }

    private static final int STATUS_BAR_HEIGHT = 21;

    private IndeterminateProgressBar progressBar;
    
    class SqlTextPaneStatusBar extends AbstractStatusBarPanel {

        protected SqlTextPaneStatusBar() {
            
            super(STATUS_BAR_HEIGHT);

            addLabel(0, 200, true);
            progressBar = new IndeterminateProgressBar(false);
            addComponent(progressBar, 1, 120, false);
        }
        
        public void setStatusText(String text) {
            
            setLabelText(0, text);
        }
        
        public void cleanup() {
         
            progressBar.cleanup();
            progressBar = null;
        }
        
        public void startProgressBar() {

            progressBar.start();
        }

        public void stopProgressBar() {
            
            progressBar.stop();
        }

    } // SqlTextPaneStatusBar
    
    private String instructionNote() {

        try {

            return FileUtils.loadResource(
                    "org/executequery/gui/resource/exportResultSetInstruction.html");

        } catch (IOException e) {

            if (Log.isDebugEnabled()) {
                
                Log.debug("Error loading export result set instruction note", e);
            }

        }

        return "Enter the SQL SELECT query below";
    }

    enum ActionOnError {
        
        HALT_ROLLBACK("Halt and Rollback"),
        HALT_COMMIT("Halt and Commit"),
        CONTINUE("Continue");
        
        private final String label;
        
        private ActionOnError(String label) {
        
            this.label = label;
        }
        
        @Override
        public String toString() {
            
            return label;
        }
        
    }
    
    enum ActionOnCancel {
        
        HALT_ROLLBACK("Halt and Rollback"),
        HALT_COMMIT("Halt and Commit");
        
        private final String label;
        
        private ActionOnCancel(String label) {
        
            this.label = label;
        }
        
        @Override
        public String toString() {
            
            return label;
        }
        
    }

}
