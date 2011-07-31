package org.executequery.gui.browser;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.underworldlabs.swing.menu.MenuItemFactory;

public class BrowserTreeRootPopopMenu extends JPopupMenu {

    public BrowserTreeRootPopopMenu(ConnectionsTreePanel treePanel) {

        add(createMenuItem("New Connection", "newConnection", treePanel));
        addSeparator();

        add(createMenuItem("Connect All", "connectAll", treePanel));
        add(createMenuItem("Disconnect All", "disconnectAll", treePanel));

        addSeparator();
        add(createMenuItem("Sort Connections", "sortConnections", treePanel));
        add(createMenuItem("Search Nodes...", "searchNodes", treePanel));
    }

    private JMenuItem createMenuItem(String text,
                                     String actionCommand,
                                     ActionListener listener) {

        JMenuItem menuItem = MenuItemFactory.createMenuItem(text);
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(listener);
        return menuItem;
    }

}
