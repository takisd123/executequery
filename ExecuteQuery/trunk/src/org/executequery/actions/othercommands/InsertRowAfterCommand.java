/*
 * InsertRowAfterCommand.java
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

package org.executequery.actions.othercommands;

import java.awt.event.ActionEvent;
import javax.swing.JPanel;

import org.executequery.GUIUtilities;
import org.executequery.gui.browser.BrowserViewPanel;
import org.executequery.gui.table.TableFunction;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Inserts a table row within relevant table type
 *  functions (ie. browser, create table, etc) after the
 *  currently selected row.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class InsertRowAfterCommand extends AbstractBaseCommand {

    public void execute(ActionEvent e) {
        JPanel panel = GUIUtilities.getSelectedCentralPane();
        if (panel != null) {
            TableFunction tableFunction = null;

            // check if the current panel is a TableFunction
            if (panel instanceof TableFunction) {
                tableFunction = (TableFunction)panel;
            }

            // otherwise, check if we are on the browser
            // then check if the current browser view is a 
            // TableFunction implementation
            else if (panel instanceof BrowserViewPanel) {
                BrowserViewPanel viewPanel = (BrowserViewPanel)panel;
                if (viewPanel.getCurrentView() instanceof TableFunction) {
                    tableFunction = (TableFunction)viewPanel.getCurrentView();
                }
            }
            // do the action
            if (tableFunction != null) {
                tableFunction.insertAfter();
            }
        }
        
    }
    
}






