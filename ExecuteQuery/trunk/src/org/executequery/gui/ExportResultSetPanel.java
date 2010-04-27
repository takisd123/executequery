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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.executequery.ActiveComponent;
import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.components.BottomButtonPanel;
import org.executequery.components.FileChooserDialog;
import org.executequery.components.ItemSelectionListener;
import org.executequery.components.TableSelectionCombosGroup;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.SqlStatementResult;
import org.executequery.databasemediators.spi.DefaultStatementExecutor;
import org.executequery.databasemediators.spi.StatementExecutor;
import org.executequery.event.ApplicationEvent;
import org.executequery.event.DefaultKeywordEvent;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;
import org.executequery.gui.text.SimpleSqlTextPanel;
import org.executequery.gui.text.TextEditor;
import org.executequery.gui.text.TextEditorContainer;
import org.underworldlabs.swing.ActionPanel;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.util.MiscUtils;

/** 
 * The Create Index panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ExportResultSetPanel extends ActionPanel
                              implements FocusComponentPanel,
                                         ActiveComponent,
                                         KeywordListener,
                                         TextEditorContainer,
                                         ItemSelectionListener {
    
    public static final String TITLE = "Export Result Set";
    public static final String FRAME_ICON = "NewIndex16.gif";
    
    private JComboBox connectionsCombo; 

    private JComboBox delimiterCombo;

    private JTextField fileNameField;

    private JCheckBox includeColumNamesCheck;
    
    private SimpleSqlTextPanel sqlText;

    private ActionContainer parent;

    private TableSelectionCombosGroup combosGroup;

    public ExportResultSetPanel(ActionContainer parent) {

        super(new BorderLayout());

        this.parent = parent;

        try  {

            init();

        } catch (Exception e) {
          
            e.printStackTrace();
        }

    }
    
    private void init() throws Exception {
        
        fileNameField = WidgetFactory.createTextField();
        connectionsCombo = WidgetFactory.createComboBox();

        String[] delims = {"|", ",", ";", "#"};
        delimiterCombo = WidgetFactory.createComboBox(delims);
        delimiterCombo.setEditable(true);
        
        combosGroup = new TableSelectionCombosGroup(connectionsCombo);
        combosGroup.addItemSelectionListener(this);

        includeColumNamesCheck = new JCheckBox("Include column names as first row");
        
        sqlText = new SimpleSqlTextPanel();
        sqlText.getTextPane().setBackground(Color.WHITE);

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
        gbc.insets.left = 5;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 0;
        mainPanel.add(new JLabel("Data Delimiter:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(delimiterCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 2;
        mainPanel.add(new JLabel("Output File:"), gbc);
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
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        gbc.insets.top = 2;
        mainPanel.add(includeColumNamesCheck, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.insets.top = 0;
        gbc.insets.left = 5;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(sqlText, gbc);
        
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JPanel base = new JPanel(new BorderLayout());

        base.add(mainPanel, BorderLayout.CENTER);

        BottomButtonPanel buttonPanel = new BottomButtonPanel(
                this, "Execute", "export-result-set", true);
        buttonPanel.setOkButtonActionCommand("doCreateIndex");
        
        base.add(buttonPanel, BorderLayout.SOUTH);
        
        // add the base to the panel
        setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        add(base, BorderLayout.CENTER);

        setPreferredSize(new Dimension(750,480));

        // register as a keyword listener
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
        if (file.exists()) {
            
            result = GUIUtilities.displayConfirmCancelDialog("The selected file exists.\nOverwrite existing file?");

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.NO_OPTION) {
                
                browse();
                return;
            }

        }
        
        fileNameField.setText(file.getAbsolutePath());
    }

    public void cleanup() {

        combosGroup.close();
        EventMediator.deregisterListener(this);
    }

    public boolean canHandleEvent(ApplicationEvent event) {

        return (event instanceof DefaultKeywordEvent);
    }

    /** Notification of a new keyword added to the list. */
    public void keywordsAdded(KeywordEvent e) {

        sqlText.setSQLKeywords(true);
    }

    /** Notification of a keyword removed from the list. */
    public void keywordsRemoved(KeywordEvent e) {

        sqlText.setSQLKeywords(true);
    }

    /**
     * Returns the index name field.
     */
    public Component getDefaultFocusComponent() {

        return fileNameField;
    }

    public void doCreateIndex() {
        
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {

                    parent.block();
                    createIndex();

                } finally {

                    parent.unblock();
                }
            }
        });

    }
    
    private void createIndex() {

        DatabaseConnection dc = combosGroup.getSelectedHost().getDatabaseConnection();

        try {

            StatementExecutor qs = new DefaultStatementExecutor(dc);

            SqlStatementResult result = qs.updateRecords(createIndexStatement());

            if (result.getUpdateCount() >= 0) {

                GUIUtilities.displayInformationMessage(
                        "Index " + fileNameField.getText() + " created.");

                parent.finished();

            } else {

                SQLException e = result.getSqlException();

                if (e != null) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("An error occurred applying the specified changes.").
                       append("\n\nThe system returned:\n").
                       append(MiscUtils.formatSQLError(e));

                    GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);

                } else {

                    GUIUtilities.displayErrorMessage(result.getErrorMessage());
                }

            }
            
        } catch (Exception e) {
          
            GUIUtilities.displayExceptionErrorDialog(
                    "Error:\n" + e.getMessage(), e);
        }
        
    }

    private String createIndexStatement() {

        return sqlText.getSQLText();
    }
    
    // ------------------------------------------------
    // ----- TextEditorContainer implementations ------
    // ------------------------------------------------
    
    /**
     * Returns the SQL text pane as the TextEditor component 
     * that this container holds.
     */
    public TextEditor getTextEditor() {

        return sqlText;
    }
    
    
    public String getDisplayName() {

        return "";
    }

    public String toString() {
        
        return TITLE;
    }

    public void itemStateChanging(ItemEvent e) {
        
        parent.block();
    }

    public void itemStateChanged(ItemEvent event) {

        
    }
    
}





