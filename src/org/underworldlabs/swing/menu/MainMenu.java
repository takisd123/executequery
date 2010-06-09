package org.underworldlabs.swing.menu;

import java.awt.Dimension;

import javax.swing.JMenu;

import org.executequery.gui.GUIConstants;

public class MainMenu extends JMenu {

    public MainMenu() {
    
        super();
    }
    
    public MainMenu(String text) {
        
        super(text);
    }

    public Dimension getPreferredSize() {

        Dimension preferredSize = super.getPreferredSize();
        preferredSize.height = Math.max(getHeight(), GUIConstants.DEFAULT_MENU_HEIGHT);

        return preferredSize;
    }
    
}
