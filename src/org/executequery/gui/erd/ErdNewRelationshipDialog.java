/*
 * ErdNewRelationshipDialog.java
 *
 * Copyright (C) 2002-2010 Takis Diakoumis
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

package org.executequery.gui.erd;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.executequery.gui.DefaultPanelButton;
import org.executequery.gui.WidgetFactory;
import org.executequery.gui.browser.ColumnConstraint;
import org.executequery.gui.browser.ColumnData;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1525 $
 * @date     $Date: 2009-05-17 12:40:04 +1000 (Sun, 17 May 2009) $
 */
public class ErdNewRelationshipDialog extends ErdPrintableDialog {
    
    /** The controller for the ERD viewer */
    private ErdViewerPanel parent;
    /** The constraint name text field */
    private JTextField nameField;
    /** The referencing table combo */
    private JComboBox referencingTableCombo;
    /** The referencing column combo */
    private JComboBox referencingColumnCombo;
    /** The referenced table combo */
    private JComboBox referencedTableCombo;
    /** The referenced column combo */
    private JComboBox referencedColumnCombo;
    /** The SQL text string buffer */
    private StringBuffer sqlBuffer;
    
    /** The literal 'ALTER TABLE ' */
    private static final String ALTER_TABLE = "ALTER TABLE ";
    /** The literal ' ADD CONSTRAINT ' */
    private static final String ADD_CONSTRAINT = "\n  ADD CONSTRAINT ";
    /** The literal ' FOREIGN KEY(' */
    private static final String FOREIGN_KEY = " FOREIGN KEY(";
    /** The literal ') REFERENCES ' */
    private static final String REFERENCES = ")\n  REFERENCES ";
    /** The literal '(' */
    private static final char OPEN_B = '(';
    /** The literal ');' */
    private static final String CLOSE_END = ");\n";
    
    public ErdNewRelationshipDialog(ErdViewerPanel parent) {
        super("New Table Relationship");
        
        this.parent = parent;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        display();
        
    }
    
    private void jbInit() throws Exception {
        JButton createButton = new DefaultPanelButton("Create");
        JButton cancelButton = new DefaultPanelButton("Cancel");
        
        ActionListener btnListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons_actionPerformed(e); }
        };
        
        cancelButton.addActionListener(btnListener);
        createButton.addActionListener(btnListener);
        
        sqlText.setPreferredSize(new Dimension(420, 120));
        
        nameField = WidgetFactory.createTextField();
        nameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                setSQLText(); }
        });
        
        ErdTable[] tables = parent.getAllComponentsArray();
        referencingTableCombo = WidgetFactory.createComboBox(tables);
        referencedTableCombo = WidgetFactory.createComboBox(tables);
        
        referencingColumnCombo = WidgetFactory.createComboBox();
        referencedColumnCombo = WidgetFactory.createComboBox();
        
        Dimension fieldDim = new Dimension(50, 20);
        nameField.setPreferredSize(fieldDim);
        referencingTableCombo.setPreferredSize(fieldDim);
        referencedTableCombo.setPreferredSize(fieldDim);
        referencingColumnCombo.setPreferredSize(fieldDim);
        referencedColumnCombo.setPreferredSize(fieldDim);
        
        referencingTableCombo.addActionListener(btnListener);
        referencedTableCombo.addActionListener(btnListener);
        referencingColumnCombo.addActionListener(btnListener);
        referencedColumnCombo.addActionListener(btnListener);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Constraint Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.left = 0;
        gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        gbc.insets.top = 0;
        gbc.gridy = 1;
        panel.add(referencingTableCombo, gbc);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.insets.left = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Referencing Table:"), gbc);
        gbc.gridy = 2;
        panel.add(new JLabel("Referencing Column:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.left = 0;
        panel.add(referencingColumnCombo, gbc);
        gbc.gridy = 3;
        panel.add(referencedTableCombo, gbc);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.insets.left = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Referenced Table:"), gbc);
        gbc.gridy = 4;
        panel.add(new JLabel("Referenced Column:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        panel.add(referencedColumnCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.left = 5;
        gbc.insets.bottom = 5;
        panel.add(sqlText, gbc);
        gbc.gridy = 6;
        gbc.gridx = 2;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createButton, gbc);
        gbc.gridx = 3;
        gbc.insets.left = 0;
        gbc.weightx = 0;
        panel.add(cancelButton, gbc);
        
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        
        c.add(panel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH,
                                new Insets(7, 7, 7, 7), 0, 0));
        
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                
                if (width < 456)
                    width = 456;
                
                if (height < 335)
                    height = 335;
                
                setSize(width, height);
            }
        });
        
        sqlBuffer = new StringBuffer(75);
        
        ErdTable table = (ErdTable)referencingTableCombo.getSelectedItem();
        referencingColumnCombo.setModel(new DefaultComboBoxModel(
        table.getTableColumns()));
        table = (ErdTable)referencedTableCombo.getSelectedItem();
        referencedColumnCombo.setModel(new DefaultComboBoxModel(
        table.getTableColumns()));
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
    }
    
    private void setSQLText() {
        sqlBuffer.delete(0, sqlBuffer.length());
        
        sqlBuffer.append(ALTER_TABLE).
        append(referencingTableCombo.getSelectedItem()).
        append(ADD_CONSTRAINT).
        append(nameField.getText()).
        append(FOREIGN_KEY).
        append(referencingColumnCombo.getSelectedItem()).
        append(REFERENCES).
        append(referencedTableCombo.getSelectedItem()).
        append(OPEN_B).
        append(referencedColumnCombo.getSelectedItem()).
        append(CLOSE_END);
        
        sqlText.setSQLText(sqlBuffer.toString());
        
    }
    
    private void create() {
        
        ColumnData column = (ColumnData)referencingColumnCombo.getSelectedItem();
        
        ColumnConstraint constraint = new ColumnConstraint();
        constraint.setName(nameField.getText());
        constraint.setRefTable(referencedTableCombo.getSelectedItem().toString());
        constraint.setColumn(column.getColumnName());
        constraint.setRefColumn(referencedColumnCombo.getSelectedItem().toString());
        constraint.setType(ColumnConstraint.FOREIGN_KEY);
        
        column.addConstraint(constraint);
        column.setForeignKey(true);
        
        ErdTable referencingTable = (ErdTable)referencingTableCombo.getSelectedItem();
        referencingTable.setAddConstraintsScript(sqlText.getSQLText());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                parent.updateTableRelationships();
            }
        });
        
        dispose();
        
    }
    
    private void buttons_actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        
        if (button instanceof JButton) {
            String command = e.getActionCommand();
            
            if (command.equals("Cancel"))
                dispose();
            
            else if (command.equals("Create"))
                create();
            
        }
        
        else {
            
            if (button == referencingTableCombo) {
                ErdTable table = (ErdTable)referencingTableCombo.getSelectedItem();
                referencingColumnCombo.setModel(new DefaultComboBoxModel(
                table.getTableColumns()));
            }
            
            else if (button == referencedTableCombo) {
                ErdTable table = (ErdTable)referencedTableCombo.getSelectedItem();
                referencedColumnCombo.setModel(new DefaultComboBoxModel(
                table.getTableColumns()));
            }
            
            setSQLText();
            
        }
        
    }
    
}













