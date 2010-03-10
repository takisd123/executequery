package org.executequery.gui.menu;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import org.executequery.gui.GUIConstants;

public class MainCheckBoxMenuItem extends JCheckBoxMenuItem {
    
    public MainCheckBoxMenuItem() {

        super();
    }
    
    public MainCheckBoxMenuItem(String text) {
        
        super(text);
    }

    public MainCheckBoxMenuItem(Action action) {

        super(action);
    }

    public MainCheckBoxMenuItem(String text, boolean selected) {

        super(text, selected);
    }

    public Dimension getPreferredSize() {

        Dimension preferredSize = super.getPreferredSize();
        preferredSize.height = Math.max(getHeight(), GUIConstants.DEFAULT_MENU_HEIGHT);

        return preferredSize;
    }

}
