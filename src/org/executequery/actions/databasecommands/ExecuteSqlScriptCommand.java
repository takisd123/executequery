/*
 * ExportSQLCommand.java
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
import org.executequery.gui.ExecuteSqlScriptPanel;
import org.executequery.gui.ExportResultSetPanel;
import org.underworldlabs.swing.actions.BaseCommand;

/** 
 * Execution for Export Excel
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1241 $
 * @date     $Date: 2008-03-24 18:19:43 +1100 (Mon, 24 Mar 2008) $
 */
public class ExecuteSqlScriptCommand extends OpenFrameCommand
                                implements BaseCommand {
    
    public void execute(ActionEvent e) {
        
        if (!isConnected()) {
            return;
        }

        if (isActionableDialogOpen()) {
            GUIUtilities.acionableDialogToFront();
            return;
        }

        if (!isDialogOpen(ExportResultSetPanel.TITLE)) {

            try {

                GUIUtilities.showWaitCursor();

                GUIUtilities.addCentralPane(ExecuteSqlScriptPanel.TITLE,
                        ExportResultSetPanel.FRAME_ICON, 
                        new ExecuteSqlScriptPanel(),
                        null,
                        true);

            } finally {
              
                GUIUtilities.showNormalCursor();
            }

        }

    }
    
}

