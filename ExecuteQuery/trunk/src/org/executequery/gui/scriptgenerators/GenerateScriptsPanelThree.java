/*
 * GenerateScriptsPanelThree.java
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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.executequery.GUIUtilities;
import org.executequery.components.FileChooserDialog;
import org.executequery.gui.DefaultPanelButton;
import org.executequery.gui.browser.WidgetFactory;
import org.underworldlabs.swing.ComponentTitledPanel;
import org.underworldlabs.swing.FileSelector;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Step three panel in the generate scripts wizard.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class GenerateScriptsPanelThree extends JPanel 
                                       implements ActionListener,
                                                  ItemListener {

    /** save to path field */
    private JTextField pathField;

    private JCheckBox constraintsCheck;
    private JCheckBox consAsAlterCheck;
    private JCheckBox consInCreateCheck;
    
    private JCheckBox useCascadeCheck;

    /** the parent controller */
    private GenerateScriptsWizard parent;
    
    /** Creates a new instance of GenerateScriptsPanelThree */
    public GenerateScriptsPanelThree(GenerateScriptsWizard parent) {
        super(new GridBagLayout());
        this.parent = parent;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {

        pathField = WidgetFactory.createTextField();

        JButton browseButton = new DefaultPanelButton("Browse");
        browseButton.setMnemonic('B');
        browseButton.addActionListener(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx++;
        gbc.gridy++;
        gbc.insets = new Insets(7,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Save Path:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(pathField, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets.top = 3;
        gbc.insets.right = 5;
        add(browseButton, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.top = 10;
        
        if (parent.getScriptType() == GenerateScriptsWizard.CREATE_TABLES) {
            constraintsCheck = new JCheckBox("Include constraints");
            consAsAlterCheck = new JCheckBox("As ALTER TABLE statements", true);
            consInCreateCheck = new JCheckBox("Within CREATE TABLE statements");

            constraintsCheck.addItemListener(this);

            ButtonGroup bg = new ButtonGroup();
            bg.add(consAsAlterCheck);
            bg.add(consInCreateCheck);

            consInCreateCheck.setEnabled(false);
            consAsAlterCheck.setEnabled(false);

            ComponentTitledPanel optionsPanel = new ComponentTitledPanel(constraintsCheck);
            JPanel _panel = optionsPanel.getContentPane();
            _panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
            _panel.add(consAsAlterCheck);
            _panel.add(consInCreateCheck);
            add(optionsPanel, gbc);
        }
        else {        
            useCascadeCheck = new JCheckBox("Use CASCADE in DROP");
            add(useCascadeCheck, gbc);
        }

    }
    
    /**
     * Whether to include the CASCADE keyword within DROP statments.
     *
     * @return true | false
     */
    protected boolean cascadeWithDrop() {
        if (parent.getScriptType() == GenerateScriptsWizard.DROP_TABLES) {
            return useCascadeCheck.isSelected();
        }
        return false;
    }

    /**
     * Returns the constraints definition format - 
     * as ALTER TABLE statements or within the CREATE TABLE statements.
     */
    protected int getConstraintsStyle() {
        if (parent.getScriptType() == GenerateScriptsWizard.CREATE_TABLES) {

            if (constraintsCheck.isSelected()) {

                if (consAsAlterCheck.isSelected()) {
                    return GenerateScriptsWizard.ALTER_TABLE_CONSTRAINTS;
                } else {
                    return GenerateScriptsWizard.CREATE_TABLE_CONSTRAINTS;
                }

            }
            
        }
        return -1;
    }

    /**
     * Returns the output file path.
     *
     * @return the output file path
     */
    protected String getOutputFilePath() {
        return pathField.getText();
    }

    
    public void actionPerformed(ActionEvent e) {
        FileSelector textFiles = new FileSelector(new String[] {"txt"}, "Text files");
        FileSelector sqlFiles = new FileSelector(new String[] {"sql"}, "SQL files");
        
        FileChooserDialog fileChooser = new FileChooserDialog();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(textFiles);
        fileChooser.addChoosableFileFilter(sqlFiles);
        
        fileChooser.setDialogTitle("Select File...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        
        int result = fileChooser.showDialog(GUIUtilities.getParentFrame(), "Select");
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        String path = fileChooser.getSelectedFile().getAbsolutePath();
        if (!path.toUpperCase().endsWith(".SQL")) {
            path += ".sql";
        }
        pathField.setText(path);
    }

    /**
     * Invoked when the include constraints checkbox has 
     * been selected/deselected.
     */    
    public void itemStateChanged(ItemEvent e) {
        enableConstraintChecks(e.getStateChange() == ItemEvent.SELECTED);
    }

    /** 
     * Enables/disables the constraints check boxes as specified.
     */
    private void enableConstraintChecks(boolean enable) {
        consAsAlterCheck.setEnabled(enable);
        consInCreateCheck.setEnabled(enable);
    }

}









