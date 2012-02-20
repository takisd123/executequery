/*
 * CopyCommand.java
 *
 * Copyright (C) 2002-2012 Takis Diakoumis
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
import org.executequery.gui.text.TextEditor;
import org.underworldlabs.swing.actions.BaseCommand;

/**
 * <p>Performs the 'COPY' command and those objects
 *  implementing <code>TextEditor</code>.
 * 
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class CopyCommand implements BaseCommand {
    
    /**
     * Executes the copy command on the <code>TextEditor</code>.
     * 
     * @param the originating event
     */
    public void execute(ActionEvent e) {
        TextEditor textEditor = GUIUtilities.getTextEditorInFocus();        
        if (textEditor != null) {
            textEditor.copy();
        }
        textEditor = null;        
    }
    
}
















