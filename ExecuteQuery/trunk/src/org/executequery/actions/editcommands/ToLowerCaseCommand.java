/*
 * ToLowerCaseCommand.java
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

package org.executequery.actions.editcommands;

import java.awt.event.ActionEvent;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.BaseCommand;
import org.executequery.gui.text.TextEditor;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * To Lower Case command execution.
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ToLowerCaseCommand implements BaseCommand {
    
    /**
     * 
     * Executes the to-lower-case command on the <code>TextEditor</code>.
     * 
     * @param the originating event
     */
    public void execute(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.changeSelectionCase(false);
        }
        textFunction = null;
    }

}









