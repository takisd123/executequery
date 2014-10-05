/*
 * ExportResultsTableCommand.java
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

package org.executequery.actions.othercommands;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import org.executequery.GUIUtilities;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.gui.editor.VisibleResultSetColumnsDialog;


/** 
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1185 $
 * @date     $Date: 2013-02-08 22:16:55 +1100 (Fri, 08 Feb 2013) $
 */
public class ShowHideResultSetColumnsCommand extends AbstractBaseCommand {
    
    public void execute(ActionEvent e) {

        JPanel panel = GUIUtilities.getSelectedCentralPane();
        if (panel instanceof QueryEditor) {
        
            QueryEditor editor = (QueryEditor) panel;
            if (editor.isResultSetSelected()) {
            
                new VisibleResultSetColumnsDialog(editor.getResultSetTable());
            
            } else {
              
                GUIUtilities.displayErrorMessage(
                        "The required result set tab panel must be selected to proceed.");
            }

        }        
    }
    
}













