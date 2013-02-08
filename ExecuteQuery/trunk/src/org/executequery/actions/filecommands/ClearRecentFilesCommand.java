/*
 * ClearRecentFilesCommand.java
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

import org.executequery.log.Log;
import org.executequery.repository.RecentlyOpenFileRepository;
import org.executequery.repository.RepositoryCache;
import org.executequery.repository.RepositoryException;
import org.underworldlabs.swing.actions.BaseCommand;

public class ClearRecentFilesCommand implements BaseCommand {

    public void execute(ActionEvent e) {
        
        try {
         
            recentlyOpenFileRepository().clear();

        } catch (RepositoryException re) {

            Log.error("An IO error occurred clearing the recent open files list: " +
                    re.getMessage());
            
        }
        
    }

    private RecentlyOpenFileRepository recentlyOpenFileRepository() {

        return (RecentlyOpenFileRepository)RepositoryCache.load(
                    RecentlyOpenFileRepository.REPOSITORY_ID);        
    }

}









