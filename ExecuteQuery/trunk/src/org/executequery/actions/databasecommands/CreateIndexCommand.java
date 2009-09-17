/*
 * CreateIndexCommand.java
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

package org.executequery.actions.databasecommands;

import java.awt.event.ActionEvent;

import org.executequery.GUIUtilities;
import org.executequery.actions.OpenFrameCommand;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.CreateIndexPanel;
import org.underworldlabs.swing.actions.BaseCommand;

/** 
 * Command execution for Database | Create Index.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class CreateIndexCommand extends OpenFrameCommand
                                implements BaseCommand {
    
    public void execute(ActionEvent e) {
        
        if (!isConnected()) {
            return;
        }

        if (isActionableDialogOpen()) {
            GUIUtilities.acionableDialogToFront();
            return;
        }

        if (!isDialogOpen(CreateIndexPanel.TITLE)) {

            try {

                GUIUtilities.showWaitCursor();

                BaseDialog dialog = 
                        createDialog(CreateIndexPanel.TITLE, false);
                
                CreateIndexPanel panel = new CreateIndexPanel(dialog);
                
                dialog.addDisplayComponentWithEmptyBorder(panel);
                dialog.display();

            } finally {
              
                GUIUtilities.showNormalCursor();
            }

        }

    }
    
}





