package org.executequery.util;

import org.executequery.ApplicationException;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class SystemWebBrowserLauncher {

    public void launch(final String url) {

        ThreadUtils.startWorker(new Runnable() {
           
            public void run() {

                try {

                    BrowserLauncher launcher = new BrowserLauncher();
                    launcher.openURLinBrowser(url);

                } catch (BrowserLaunchingInitializingException e) {
                    
                    throw new ApplicationException(e);

                } catch (UnsupportedOperatingSystemException e) {
                  
                    throw new ApplicationException(e);
                }

            }
            
        });

    }
    
}
