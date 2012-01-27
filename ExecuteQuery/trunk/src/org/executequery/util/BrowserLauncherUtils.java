/*
 * BrowserLauncherUtils.java
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

package org.executequery.util;

import org.executequery.GUIUtilities;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 * @deprecated use SystemWebBrowserLauncher
 */
public class BrowserLauncherUtils {

    public static void launch(String url) {
        try {
            BrowserLauncher launcher = new BrowserLauncher();
            launcher.openURLinBrowser(url);
            
//            BrowserLauncherRunner runner = 
//                    new BrowserLauncherRunner(launcher, url, null);
//            Thread launcherThread = new Thread(runner);
//            launcherThread.start();
        } 
        catch (BrowserLaunchingInitializingException e) {
            handleException(e);
        }
        catch (UnsupportedOperatingSystemException e) {
            handleException(e);
        }
    }
    
    private static void handleException(Throwable e) {
        GUIUtilities.displayExceptionErrorDialog(
                "Error launching local web browser:\n" + 
                e.getMessage(), e);
    }
    
    /** Creates a new instance of BrowserLauncherUtils */
    private BrowserLauncherUtils() {}
    
}


