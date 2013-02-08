/*
 * ExecuteSqlScriptPanel.java
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

package org.executequery.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import org.executequery.event.ApplicationEvent;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.event.DefaultKeywordEvent;
import org.executequery.sql.ActionOnError;
import org.executequery.sql.ExecutionController;
import org.executequery.sql.SqlScriptRunner;
import org.executequery.sql.SqlStatementResult;
import org.underworldlabs.swing.AbstractStatusBarPanel;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.IndeterminateProgressBar;
import org.underworldlabs.swing.ProgressBar;
import org.underworldlabs.swing.ProgressBarFactory;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.util.MiscUtils;

/** 
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class ExecuteSqlScriptPanel extends DefaultTabViewActionPanel
                                  implements NamedView,
                                             FocusComponentPanel,
                                             ActiveComponent,
                                             ExecutionController,
                                             ConnectionListener {
    
    public static final String TITLE = "Execute SQL Script ";
    public static final String FRAME_ICON = "ExecuteSqlScript16.png";
    
    private JComboBox connectionsCombo; 

    private JTextField fileNameField;

    private TableSelectionCombosGroup combosGroup;

    private LoggingOutputPanel outputPanel;
    
    private SqlTextPaneStatusBar statusBar;

    private JComboBox actionOnErrorCombo;
    
    private JButton startButton;

    private JButton rollbackButton;
    
    private JButton commitButton;
    
    private JButton stopButton;

    private SqlScriptRunner sqlScriptRunner;
    
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
                ActionOnError.HALT,
                ActionOnError.CONTINUE
        };        
        
        actionOnErrorCombo.setModel(new DefaultComboBoxModel(actionsOnError));
        
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
        gbc.insets.top = 7;
        gbc.insets.bottom = 5;
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Connection:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.top = 5;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(connectionsCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 5;
        mainPanel.add(new JLabel("Action on Error:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.top = 0;
        mainPanel.add(actionOnErrorCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 5;
        mainPanel.add(new JLabel("Input File:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.top = 0;
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
        
        closeConnection();
        EventMediator.deregisterListener(this);
    }

    private void closeConnection() {

        if (sqlScriptRunner != null) {

            try {
        
                sqlScriptRunner.close();

            } catch (SQLException e) {

                e.printStackTrace();
            }
        
        }
    }

    public boolean canHandleEvent(ApplicationEvent event) {

        return (event instanceof DefaultKeywordEvent) || (event instanceof ConnectionEvent);
    }

    public Component getDefaultFocusComponent() {

        return fileNameField;
    }

    private void enableButtons(final boolean enableStart, final boolean enableStop,
            final boolean enableCommit, final boolean enableRollback) {

        GUIUtils.invokeLater(new Runnable() {
           public void run() {
               startButton.setEnabled(enableStart);
               stopButton.setEnabled(enableStop);                
               commitButton.setEnabled(enableCommit);
               rollbackButton.setEnabled(enableRollback);                
            } 
            
        });        
    }

    private SwingWorker swingWorker;
    private boolean executing;
    
    public void start() {

        if (!executing) {

            if (fieldsValid()) {
    
                enableButtons(false, true, false, false);
                
                swingWorker = new SwingWorker() {
                    public Object construct() {

                        executing = true;
                        return execute();
                    }
                    public void finished() {

                        SqlStatementResult sqlStatementResult = (SqlStatementResult) get();
                        
                        try {

                            outputPanel.append("Statements executed: " + sqlStatementResult.getStatementCount());
                            outputPanel.append("Records affected: " + sqlStatementResult.getUpdateCount());
    
                        } finally {

                            executing = false;
                            enableButtons(false, false, true, true);
                        }
                    }
                };
                swingWorker.start();
            }
            
        }
    }
    
    public void stop() {
        
        if (executing) {

            try {
            
                if (swingWorker != null) {
                    
                    swingWorker.interrupt();
                }
                
            } finally {
                
                executing = false;
            }

        }

    }
    
    public void commit() {
        
        if (sqlScriptRunner != null) {
            
            try {

                sqlScriptRunner.commit();
                outputPanel.append("Commit complete");
                
            } catch (SQLException e) {

                outputPanel.appendError("Error during commit:\n" + e.getMessage());

            } finally {

                closeConnection();
                enableButtons(true, false, false, false);
            }
        }
        
    }
    
    public void rollback() {
        
        if (sqlScriptRunner != null) {
            
            try {

                sqlScriptRunner.rollback();
                outputPanel.append("Rollback complete");

            } catch (SQLException e) {

                outputPanel.appendError("Error during rollback:\n" + e.getMessage());
                
            } finally {
                
                closeConnection();
                enableButtons(true, false, false, false);
            }
        }
        
    }
    
    private SqlStatementResult execute() {
 
        if (sqlScriptRunner == null) {
            
            sqlScriptRunner = new SqlScriptRunner(this);
        }

        outputPanel.clear();
        long startTime = System.currentTimeMillis();
        SqlStatementResult sqlStatementResult = null;

        try {

            statusBar.setStatusText("Executing...");
            statusBar.startProgressBar();

            sqlStatementResult = sqlScriptRunner.execute(
                        combosGroup.getSelectedHost().getDatabaseConnection(),
                        fileNameField.getText(),
                        (ActionOnError) actionOnErrorCombo.getSelectedItem());

        } finally {

            if (sqlStatementResult != null && sqlStatementResult.isException()) {
                
                if (sqlStatementResult.isInterrupted()) {
                
                    outputPanel.appendWarning("Operation cancelled by user action");
                    
                } else {
                    
                    outputPanel.appendError("Execution error:\n" + sqlStatementResult.getErrorMessage());                    
                }
                
            }
            
            long endTime = System.currentTimeMillis();

            statusBar.setStatusText("Done");
            statusBar.stopProgressBar();

            outputPanel.append("Duration: " + MiscUtils.formatDuration(endTime - startTime));
            GUIUtilities.scheduleGC();
        }

        return sqlStatementResult;
    }
    
    private static int instanceCount = 1;
    public String getDisplayName() {

        return TITLE + (instanceCount++);
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

    private ProgressBar progressBar;
    
    class SqlTextPaneStatusBar extends AbstractStatusBarPanel {

        protected SqlTextPaneStatusBar() {
            
            super(STATUS_BAR_HEIGHT);

            addLabel(0, 200, true);
            progressBar = ProgressBarFactory.create(false);
            addComponent(((JComponent) progressBar), 1, 120, false);
        }
        
        public void setStatusText(String text) {
            
            setLabelText(0, text);
        }
        
        public void cleanup() {
         
            progressBar.cleanup();
            progressBar = null;
        }
        
        public void startProgressBar() {

            GUIUtils.invokeLater(new Runnable() {
               public void run() {

                   progressBar.start();
                } 
            });
            
        }

        public void stopProgressBar() {
            
            GUIUtils.invokeLater(new Runnable() {
                public void run() {

                    progressBar.stop();
                 } 
             });
        }

    } // SqlTextPaneStatusBar
    
    public void message(String message) {

        outputPanel.append(message);
    }

    public void actionMessage(String message) {

        outputPanel.appendAction(message);
    }

    public void errorMessage(String message) {

        outputPanel.appendError(message);
    }

    public void queryMessage(String message) {

        outputPanel.appendActionFixedWidth(message);
    }

    public void warningMessage(String message) {

        outputPanel.appendWarning(message);        
    }

}




