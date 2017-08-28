/*
 * BrowserTreePopupMenu.java
 *
 * Copyright (C) 2002-2015 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.executequery.gui.browser;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databaseobjects.DatabaseCatalog;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.gui.browser.nodes.DatabaseCatalogNode;
import org.executequery.gui.browser.nodes.DatabaseHostNode;
import org.executequery.gui.browser.nodes.DatabaseObjectNode;
import org.executequery.localisation.eqlang;
import org.underworldlabs.swing.menu.MenuItemFactory;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1487 $
 * @date     $Date: 2015-08-23 22:21:42 +1000 (Sun, 23 Aug 2015) $
 */
class BrowserTreePopupMenu extends JPopupMenu {

    private JMenuItem addNewConnection;
    private JMenuItem connect;
    private JMenuItem disconnect;
    private JMenuItem reload;
    private JMenuItem duplicate;
    private JMenuItem duplicateWithSource;
    private JMenuItem delete;
    private JMenuItem recycleConnection;
    private JMenuItem copyName;
    
    private JCheckBoxMenuItem showDefaultCatalogsAndSchemas; 

    private JMenu sql;
    private JMenu exportData;
    private JMenu importData;

    private BrowserTreePopupMenuActionListener listener;
    
    BrowserTreePopupMenu(BrowserTreePopupMenuActionListener listener) {

        this.listener = listener;
        
        connect = createMenuItem(eqlang.getString("Connect"), "connect", listener);
        add(connect);
        disconnect = createMenuItem(eqlang.getString("Disconnect"), "disconnect", listener);
        add(disconnect);

        addSeparator();

        reload = createMenuItem(eqlang.getString("Reload"), "reload", listener);
        add(reload);
        recycleConnection = createMenuItem(eqlang.getString("Recycle this Connection"), "recycle", listener);
        add(recycleConnection);

        addSeparator();

        showDefaultCatalogsAndSchemas = createCheckBoxMenuItem(
                eqlang.getString("Show only default Catalog or Schema"),
                "switchDefaultCatalogAndSchemaDisplay", listener);
        add(showDefaultCatalogsAndSchemas);

        addSeparator();

        addNewConnection = createMenuItem(eqlang.getString("New Connection"), "addNewConnection", listener);
        add(addNewConnection);
        duplicate = createMenuItem(eqlang.getString("Duplicate"), "duplicate", listener);
        add(duplicate);
        duplicateWithSource = createMenuItem(eqlang.getString("Duplicate source"), "duplicateWithSource", listener);
        add(duplicateWithSource);
        delete = createMenuItem(eqlang.getString("Delete"), "delete", listener);
        add(delete);

        addSeparator();

        copyName = createMenuItem(eqlang.getString("Copy Name"), "copyName", listener);
        add(copyName);
        
        createSqlMenu(listener);
        createExportMenu(listener);
        createImportMenu(listener);

        addSeparator();
        add(createMenuItem(eqlang.getString("Move to folder..."), "moveToFolder", listener));
        add(createMenuItem(eqlang.getString("Connection Properties"), "properties", listener));
    }

    public void show(Component invoker, int x, int y) {
        setToConnect(!getCurrentSelection().isConnected());
        super.show(invoker, x, y);
    }
    
    private JMenuItem createMenuItem(String text, 
                                     String actionCommand, 
                                     ActionListener listener) {

        JMenuItem menuItem = MenuItemFactory.createMenuItem(text);
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private JCheckBoxMenuItem createCheckBoxMenuItem(String text, 
                                     String actionCommand, 
                                     ActionListener listener) {

        JCheckBoxMenuItem menuItem = MenuItemFactory.createCheckBoxMenuItem(text);
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private void setToConnect(boolean canConnect) {

        connect.setEnabled(canConnect);
        disconnect.setEnabled(!canConnect);
        delete.setEnabled(canConnect);

        String label = null;
        DefaultMutableTreeNode currentPathComponent = (DefaultMutableTreeNode) listener.getCurrentPathComponent();

        // check whether reload is available
        if (listener.hasCurrentPath()) {

            if (currentPathComponent instanceof DatabaseObjectNode) {
                
                DatabaseObjectNode node = asDatabaseObjectNode(currentPathComponent);
                
                //if (node.getUserObject() instanceof DatabaseHost) {
                
                if (node.isHostNode()) {

                    reload.setEnabled(false);
                    sql.setEnabled(false);
                    exportData.setEnabled(false);
                    importData.setEnabled(false);
                    
                    showDefaultCatalogsAndSchemas.setEnabled(true);
                    showDefaultCatalogsAndSchemas.setSelected(
                            ((DatabaseHostNode)node).isDefaultCatalogsAndSchemasOnly());
                    
                    recycleConnection.setEnabled(!canConnect);
                } 
                else {

                    label = node.toString();

                    reload.setEnabled(true);
                    recycleConnection.setEnabled(false);
                    showDefaultCatalogsAndSchemas.setEnabled(false);

                    boolean importExport = (node.getType() == NamedObject.TABLE);
                    sql.setEnabled(importExport);
                    exportData.setEnabled(importExport);
                    importData.setEnabled(importExport);
                }
            }
        }

        // re-label the menu items
        if (listener.hasCurrentSelection()) {

            String name = listener.getCurrentSelection().getName();
            connect.setText(eqlang.getString("Connect Data Source") + name);
            disconnect.setText(eqlang.getString("Disconnect Data Source") + name);
            delete.setText(eqlang.getString("Remove Data Source") + name);
            duplicate.setText(eqlang.getString("Create Duplicate of Connection") + name);
            
            // eeekkk...
            if (isCatalog(currentPathComponent) && asDatabaseCatalog(currentPathComponent).getHost().supportsCatalogsInTableDefinitions()) {
                
                duplicateWithSource.setEnabled(true);
                duplicateWithSource.setText(eqlang.getString("Create Connection with Data Source as") + currentPathComponent.toString());

            } else {

                duplicateWithSource.setText(eqlang.getString("Create Connection with Selected Data Source"));
                duplicateWithSource.setEnabled(false);
            }

            if (label != null) {
                reload.setText(eqlang.getString("Reload")+" " + label);
            } else {
                reload.setText(eqlang.getString("Reload"));
            }

        }

    }

    private DatabaseCatalog asDatabaseCatalog(DefaultMutableTreeNode currentPathComponent) {

        return (DatabaseCatalog) (asDatabaseObjectNode(currentPathComponent)).getDatabaseObject();
    }

    private DatabaseObjectNode asDatabaseObjectNode(DefaultMutableTreeNode currentPathComponent) {

        return (DatabaseObjectNode) currentPathComponent;
    }

    private boolean isCatalog(DefaultMutableTreeNode currentPathComponent) {
        
        return currentPathComponent instanceof DatabaseCatalogNode;
    }

    private void createImportMenu(ActionListener listener) {
        importData = MenuItemFactory.createMenu(eqlang.getString("Import Data"));
        importData.add(createMenuItem(eqlang.getString("Import from XML File"), "importXml", listener));
        importData.add(createMenuItem(eqlang.getString("Import from Delimited File"), "importDelimited", listener));
        add(importData);
    }

    private void createExportMenu(ActionListener listener) {
        exportData = MenuItemFactory.createMenu(eqlang.getString("Export Data"));
        exportData.add(createMenuItem(eqlang.getString("Export as SQL"), "exportSQL", listener));
        exportData.add(createMenuItem(eqlang.getString("Export to XML File"), "exportXml", listener));
        exportData.add(createMenuItem(eqlang.getString("Export to Delimited File"), "exportDelimited", listener));
        exportData.add(createMenuItem(eqlang.getString("Export to Excel Spreadsheet"), "exportExcel", listener));
        add(exportData);
    }

    private void createSqlMenu(ActionListener listener) {
        sql = MenuItemFactory.createMenu(eqlang.getString("SQL"));
        sql.add(createMenuItem(eqlang.getString("SELECT statement"), "selectStatement", listener));
        sql.add(createMenuItem(eqlang.getString("INSERT statement"), "insertStatement", listener));
        sql.add(createMenuItem(eqlang.getString("UPDATE statement"), "updateStatement", listener));
        sql.add(createMenuItem(eqlang.getString("CREATE TABLE statement"), "createTableStatement", listener));
        add(sql);
    }

    protected DatabaseConnection getCurrentSelection() {
        return listener.getCurrentSelection();
    }

    protected void setCurrentSelection(DatabaseConnection currentSelection) {
        listener.setCurrentSelection(currentSelection);
    }

    protected void setCurrentPath(TreePath currentPath) {
        listener.setCurrentPath(currentPath);
    }

    protected boolean hasCurrentSelection() {
        return listener.hasCurrentSelection();
    }

    protected TreePath getCurrentPath() {
        return listener.getCurrentPath();
    }

}





