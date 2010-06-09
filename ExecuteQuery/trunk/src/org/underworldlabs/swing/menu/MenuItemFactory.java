package org.underworldlabs.swing.menu;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


public final class MenuItemFactory {

    public static JMenu createMenu(String text) {
        
        return new MainMenu(text);
    }
    
    public static JMenuItem createMenuItem() {
        
        return new MainMenuItem();
    }

    public static JMenuItem createMenuItem(Action action) {
        
        return new MainMenuItem(action);
    }    

    public static JMenuItem createMenuItem(String text) {
        
        return new MainMenuItem(text);
    }

    public static JMenuItem createMenuItem(String text, Icon icon) {

        return new MainMenuItem(text, icon);
    }

    public static JCheckBoxMenuItem createCheckBoxMenuItem(String text) {
        
        return new MainCheckBoxMenuItem(text);
    }

    public static JCheckBoxMenuItem createCheckBoxMenuItem(Action action) {

        return new MainCheckBoxMenuItem(action);
    }

    public static JCheckBoxMenuItem createCheckBoxMenuItem(String text, boolean selected) {

        return new MainCheckBoxMenuItem(text, selected);
    }

}
