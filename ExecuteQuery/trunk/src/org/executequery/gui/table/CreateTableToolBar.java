/*
 * CreateTableToolBar.java
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

package org.executequery.gui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.ActionUtilities;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class CreateTableToolBar extends JPanel
                                implements ActionListener {
    
    /** The parent panel where this tool bar will be attached */
    private TableFunction parent;
    
    /** The insert row (column) after button */
    private JButton insertAfterButton;
    
    /** The insert row (column) before button */
    private JButton insertBeforeButton;
    
    /** The delete row (column) button */
    private JButton deleteRowButton;
    
    /** The move row (column) up button */
    private JButton moveUpButton;
    
    /** The move row (column) down button */
    private JButton moveDownButton;
    
    /** Whether the move buttons are available */
    private boolean canMove;
    
    public CreateTableToolBar(TableFunction parent) {
        this(parent, true);
    }
    
    public CreateTableToolBar(TableFunction parent, boolean canMove) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.parent = parent;
        this.canMove = canMove;
        initialiseButtons();
    }
    
    /** <p>Creates the tool bar buttons and associates
     *  these with the relevant listener. */
    private void initialiseButtons() {
        insertAfterButton = ActionUtilities.createButton(
                this,
                GUIUtilities.getAbsoluteIconPath("ColumnInsertAfter16.gif"),
                "Insert a value after the current selection", 
                null);

        insertBeforeButton = ActionUtilities.createButton(
                this,
                GUIUtilities.getAbsoluteIconPath("ColumnInsertBefore16.gif"),
                "Insert a value before the current selection", 
                null);

        deleteRowButton = ActionUtilities.createButton(
                this,
                GUIUtilities.getAbsoluteIconPath("ColumnDelete16.gif"),
                "Delete the selected value", 
                null);

        add(insertAfterButton);
        add(insertBeforeButton);
        add(deleteRowButton);
        
        if (canMove) {
            moveUpButton = ActionUtilities.createButton(
                    this,
                    "Up16.gif",
                    "Move the selection up", 
                    null);

            moveDownButton = ActionUtilities.createButton(
                    this,
                    "Down16.gif",
                    "Move the selection down", 
                    null);

            add(moveUpButton);
            add(moveDownButton);
        }
        
    }
    
    /** <p>Enables/disables as specified the buttons
     *  insert before, move up and move down.
     *
     *  @param <code>true</code> to enable these buttons
     *         <code>false</code> to disable these buttons
     */
    public void enableButtons(boolean enable) {
        insertBeforeButton.setEnabled(enable);
        
        if (canMove) {
            moveUpButton.setEnabled(enable);
            moveDownButton.setEnabled(enable);
        }
    }
    
    /** <p>Determines which button was selected and
     *  calls the relevant method to execute that action.
     *
     *  @param the event initiating this action
     */
    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        
        if (button == insertAfterButton)
            parent.insertAfter();
        
        else if (button == insertBeforeButton)
            parent.insertBefore();
        
        else if (button == deleteRowButton)
            parent.deleteRow();
        
        else if (button == moveUpButton)
            parent.moveColumnUp();
        
        else if (button == moveDownButton)
            parent.moveColumnDown();
        
    }
    
}









