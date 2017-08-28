package org.executequery.localisation;

import org.executequery.gui.menu.MenuItem;
import org.executequery.util.StringBundle;
import org.underworldlabs.swing.actions.ActionBuilder;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class eqlang {
    public static String getString(String key) {
        Locale locale = new Locale(System.getProperty("user.language"));
        ResourceBundle rb = ResourceBundle.getBundle("org.executequery.localisation.langResources", locale);
        try{
            key=rb.getString(key);
        }
        catch (Exception e)
        {

        }
        return key;
    }
    public static String[] getStrings(String[] key) {
        for (int i=0;i<key.length;i++)
        {

                key[i]=getString(key[i]);

        }

        return key;
    }

    public static Map<String, Action> translateActions(Map<String, Action> actionsMap) {
        for (String id : actionsMap.keySet()) {
            Action a = actionsMap.get(id);

            try {

                a.putValue("Name", getString(id + "-name"));
                actionsMap.put(id, a);

            } catch (Exception e) {

            }
            try {
                a.putValue("ShortDescription", getString(id + "-description"));
                actionsMap.put(id, a);

            } catch (Exception e) {

            }
        }
        return actionsMap;
    }
    public static List<MenuItem> translatemenu(List<MenuItem> items) {
        for (MenuItem item : items ){
            if (!item.isSeparator()) {
                String id = item.getName();
                try {

                    item.setName(getString(id + "-name"));

                } catch (Exception e) {

                }
                try {

                    item.setToolTip(getString(id + "-description"));

                } catch (Exception e) {

                }

                try {

                    item.setChildren(translatemenu(item.getChildren()));

                } catch (Exception e) {

                }
            }
        }
        return items;
    }
}
