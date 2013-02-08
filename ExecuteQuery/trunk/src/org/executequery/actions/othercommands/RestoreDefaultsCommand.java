/*
 * RestoreDefaultsCommand.java
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

import org.executequery.gui.prefs.UserPreferenceFunction;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Restore system defaults command for respective
 *  preferences panels.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class RestoreDefaultsCommand extends AbstractBaseCommand {
    
    private UserPreferenceFunction frame;
    
    public RestoreDefaultsCommand(UserPreferenceFunction frame) {
        super("Restore Defaults");
        this.frame = frame;
    }
    
    public void execute(ActionEvent e) {
        frame.restoreDefaults();
    }
    
}

















