package org.executequery.gui.importexport;

import org.executequery.util.StringBundle;
import org.executequery.util.SystemResources;
import org.underworldlabs.swing.wizard.WizardProcessPanel;

public abstract class ImportExportWizardProcessPanel extends WizardProcessPanel {

    private StringBundle bundle;

    protected final StringBundle getBundle() {

        return bundle();
    }

    private StringBundle bundle() {
        
        if (bundle == null) {            
        
            bundle = SystemResources.loadBundle(getClass());
        }

        return bundle;
    }

    protected final String getString(String key) {

        return getBundle().getString(key);
    }

    protected final String getString(String key, Object arg) {

        return getBundle().getString(key, arg);
    }

}
