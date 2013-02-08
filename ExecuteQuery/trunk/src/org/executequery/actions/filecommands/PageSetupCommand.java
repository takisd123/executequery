/*
 * PageSetupCommand.java
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

package org.executequery.actions.filecommands;

import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;

import org.executequery.print.PrintingSupport;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.swing.actions.BaseCommand;

/** 
 * <p>The File | Page Setup command.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class PageSetupCommand implements BaseCommand {
    
    public void execute(ActionEvent e) {

        SwingWorker worker = new SwingWorker() {
            public Object construct() {

                return showDialog();
            }
            public void finished() {}
        };

        worker.start();

    }
    
    private PageFormat showDialog() {
    
        PrintingSupport printer = new PrintingSupport(); 
        
        return printer.pageSetup();
    }
    
}









