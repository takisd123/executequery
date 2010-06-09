package org.underworldlabs.swing.menu;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.executequery.gui.GUIConstants;

public class MainMenuItem extends JMenuItem {
    
    public MainMenuItem() {
        super();
    }

    public MainMenuItem(Action a) {
        super(a);
    }

    public MainMenuItem(String text) {
        super(text);
    }

    public MainMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    public Dimension getPreferredSize() {

        Dimension preferredSize = super.getPreferredSize();
        preferredSize.height = Math.max(getHeight(), GUIConstants.DEFAULT_MENU_HEIGHT);

        return preferredSize;
    }
    
}
