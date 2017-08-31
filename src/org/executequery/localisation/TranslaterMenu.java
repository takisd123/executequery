package org.executequery.localisation;

import org.executequery.gui.menu.MenuItem;
import org.executequery.util.StringBundle;
import org.executequery.util.SystemResources;

import javax.swing.Action;
import java.util.List;
import java.util.Map;


public class TranslaterMenu {

    private static StringBundle bundle() {

        StringBundle  bundle = SystemResources.loadBundle(TranslaterMenu.class);

        return bundle;
    }

    private static String getString(String key) {
        return bundle().getString(key);
    }
    public static Map<String, Action> translateActions(Map<String, Action> actionsMap) {
        for (String id : actionsMap.keySet()) {
            Action a = actionsMap.get(id);



            try {
                String name=(String)a.getValue( "Name");
                a.putValue("Name", getString(name ));
                actionsMap.put(id, a);

            } catch (Exception e) {

            }
            try {
                String desc=(String)a.getValue("ShortDescription");
                a.putValue("ShortDescription", getString(desc));
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

                    item.setName(getString(id ));

                } catch (Exception e) {

                }
                id=item.getToolTip();
                try {

                    item.setToolTip(getString(id ));

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
