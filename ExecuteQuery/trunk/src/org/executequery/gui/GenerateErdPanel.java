/*
 * GenerateErdPanel.java
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

package org.executequery.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.executequery.GUIUtilities;
import org.executequery.gui.erd.ErdGenerateProgressDialog;
import org.executequery.gui.erd.ErdSelectionPanel;
import org.executequery.components.BottomButtonPanel;

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
public class GenerateErdPanel extends JPanel
                              implements ActionListener {
    
    public static final String TITLE = "Generate ERD";

    /** The table selection panel */
    private ErdSelectionPanel selectionPanel;
    
    /** the parent container */
    private ActionContainer parent;
    
    public GenerateErdPanel(ActionContainer parent) {
        super(new BorderLayout());
        this.parent = parent;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void jbInit() throws Exception {
        selectionPanel = new ErdSelectionPanel();
        JPanel basePanel = new JPanel(new BorderLayout());
        basePanel.add(selectionPanel, BorderLayout.NORTH);
        basePanel.add(new BottomButtonPanel(this, "Generate", "erd", true),
                                                BorderLayout.SOUTH);
        add(basePanel, BorderLayout.CENTER);
    }
    
    /**
     * Releases database resources before closing.
     */
    public void cleanup() {
        selectionPanel.cleanup();
    }

    public void setInProcess(boolean inProcess) {
        if (inProcess) {

            parent.block();
            
        } else {
            
            parent.unblock();
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        
        if (selectionPanel.hasSelections()) {
            new ErdGenerateProgressDialog(selectionPanel.getDatabaseConnection(),
                                          selectionPanel.getSelectedValues(),
                                          selectionPanel.getSchema());
        }
        else {
            GUIUtilities.displayErrorMessage(
                            "You must select at least one table.");
        }
        
    }
    
}













