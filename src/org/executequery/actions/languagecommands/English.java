package org.executequery.actions.languagecommands;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.actions.filecommands.PrintPreviewCommand;
import org.executequery.util.StringBundle;
import org.executequery.util.SystemResources;
import org.underworldlabs.swing.actions.BaseCommand;
import org.underworldlabs.util.SystemProperties;

import java.awt.event.ActionEvent;

public class English implements BaseCommand {
    @Override
    public void execute(ActionEvent e) {
        SystemProperties.setProperty(Constants.USER_PROPERTIES_KEY, "locale.language",
                "en");
        GUIUtilities.displayInformationMessage(bundleString("messageRestart"));


    }
    private StringBundle bundle() {

        StringBundle   bundle = SystemResources.loadBundle(English.class);

        return bundle;
    }

    private String bundleString(String key) {
        return bundle().getString("English." + key);
    }
}