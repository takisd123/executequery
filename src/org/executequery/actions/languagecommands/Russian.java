package org.executequery.actions.languagecommands;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.BaseCommand;
import org.underworldlabs.util.SystemProperties;

import java.awt.event.ActionEvent;

public class Russian implements BaseCommand {
    @Override
    public void execute(ActionEvent e) {
        SystemProperties.setProperty(Constants.USER_PROPERTIES_KEY, "locale.language",
                "ru");

    }
}
