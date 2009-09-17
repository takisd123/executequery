/*
 * ImportExportXMLPanel_5.java
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

package org.executequery.gui.importexport;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ImportExportXMLPanel_5 extends JPanel {
    
    /** The data format text field */
//    private JTextField dateFormatField;
    
    /** The on error combo box */
    private JComboBox errorCombo;
    
    /** The rollback combo box */
    private JComboBox rollbackCombo;
    
    /** The batch process check box */
    private JCheckBox batchCheck;
    
    /** The controlling object for this process */
    private ImportExportProcess parent;
    
    /** the date parsing selection panel */
    private ParseDateSelectionPanel dateFormatPanel;

    /** <p>Creates a new instance with the specified
     *  process as the parent.
     *
     *  @param the parent controlling the process
     */
    public ImportExportXMLPanel_5(ImportExportProcess parent) {
        super(new GridBagLayout());
        this.parent = parent;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** <p>Initialises the state of this instance and
     *  lays out components on the panel. */
    private void jbInit() throws Exception {
        JLabel rollbackLabel = new JLabel("Rollback Segment Size:");
        
//        JLabel dateFormatLabel = new JLabel("Date Format:");
//        dateFormatField = new JTextField();
        
        String[] errors = {"Log and Continue", "Stop Transfer"};
        errorCombo = new JComboBox(errors);
        
        String[] rolls = {"50", "100", "500", "1000", "5000",
                          "10000", "50000", "End of File", "End of all Files"};
        rollbackCombo = new JComboBox(rolls);
        rollbackCombo.setSelectedIndex(2);
        
        batchCheck = new JCheckBox("Run as a batch process");
        
        Dimension comboDim = new Dimension(140, 20);
        errorCombo.setPreferredSize(comboDim);
        rollbackCombo.setPreferredSize(comboDim);

        JLabel instructLabel = new JLabel("Enter any particulars of the XML files " +
                                          "and select transfer options.");
        
        dateFormatPanel = new ParseDateSelectionPanel(parent);

        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(5,10,20,10);
        gbc.insets = ins;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        gbc.gridx = 0;
        add(instructLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets.bottom = 10;
        gbc.insets.left = 20;
        add(new JLabel("On Error:"), gbc);
        gbc.gridx = 1;
        gbc.insets.left = 0;
        add(errorCombo, gbc);
        gbc.gridy++;
        add(rollbackCombo, gbc);
        gbc.gridx = 0;
        gbc.insets.left = 20;
        add(rollbackLabel, gbc);
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(dateFormatPanel, gbc);

        /*
        gbc.gridy++;
        add(dateFormatLabel, gbc);
        gbc.gridx = 1;
        gbc.insets.left = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(dateFormatField, gbc);
         */
        
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets.left = 20;
        add(batchCheck, gbc);
        
        int type = parent.getTransferType();
        
        if (type == ImportExportProcess.EXPORT) {
//            dateFormatPanel.setEnabled(false);
            //dateFormatField.setOpaque(false);
            //dateFormatField.setEnabled(false);
            //dateFormatLabel.setEnabled(false);
            rollbackCombo.setOpaque(false);
            rollbackCombo.setEnabled(false);
            rollbackLabel.setEnabled(false);
            batchCheck.setEnabled(false);
        }
        
    }
    
    /** <p>Retrieves the selected rollback size for
     *  the transfer.
     *
     *  @return the rollback size
     */
    public int getRollbackSize() {
        if (!rollbackCombo.isEnabled()) {
            return -1;
        }

        int index = rollbackCombo.getSelectedIndex();
        if (index == 7) {
            return ImportExportProcess.COMMIT_END_OF_FILE;
        } 
        else if (index == 8) {
            return ImportExportProcess.COMMIT_END_OF_ALL_FILES;
        }
        else {
            return Integer.parseInt((String)rollbackCombo.getSelectedItem());
        }
    }
    
    /** <p>Retrieves the action on an error occuring
     *  during the import/export process.
     *
     *  @return the action on error -<br>either:
     *          <code>ImportExportProcess.LOG_AND_CONTINUE</code> or
     *          <code>ImportExportProcess.STOP_TRANSFER</code>
     */
    public int getOnError() {
        if (errorCombo.getSelectedIndex() == 0)
            return ImportExportProcess.LOG_AND_CONTINUE;
        else
            return ImportExportProcess.STOP_TRANSFER;
    }
    
    /** <p>Indicates whether the process (import only)
     *  should be run as a batch process.
     *
     *  @return whether to run as a batch process
     */
    public boolean runAsBatchProcess() {
        return batchCheck.isSelected();
    }
    
    /** <p>Retrieves the date format for date fields
     *  contained within the data file/database table.
     *
     *  @return the date format (ie. ddMMyyy)
     */
    public String getDateFormat() {
        return dateFormatPanel.getDateFormat();
        //return dateFormatField.getText();
    }
 
    /**
     * Returns whether to parse date values.
     *
     * @return true | false
     */
    public boolean parseDateValues() {
        return dateFormatPanel.parseDates();
    }

}



