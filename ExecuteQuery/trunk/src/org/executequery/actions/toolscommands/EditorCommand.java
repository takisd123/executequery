/*
 * EditorCommand.java
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

package org.executequery.actions.toolscommands;

import java.awt.event.ActionEvent;

import org.executequery.GUIUtilities;
import org.executequery.actions.OpenFrameCommand;
import org.executequery.gui.editor.QueryEditor;
import org.underworldlabs.swing.actions.BaseCommand;

/** 
 * Command execution for a new Query Editor
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class EditorCommand extends OpenFrameCommand
                           implements BaseCommand {
    
    public void execute(ActionEvent e) {
        GUIUtilities.addCentralPane(QueryEditor.TITLE,
                                    QueryEditor.FRAME_ICON, 
                                    new QueryEditor(),
                                    null,
                                    true);
    }
    
}










