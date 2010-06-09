/*
 * BrowserTreePopupMenu.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
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
import javax.swing.tree.TreePath;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.gui.browser.nodes.DatabaseHostNode;
import org.executequery.gui.browser.nodes.DatabaseObjectNode;
import org.underworldlabs.swing.menu.MenuItemFactory;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
class BrowserTreePopupMenu extends JPopupMenu {

    private JMenuItem addNewConnection;
    private JMenuItem connect;
    private JMenuItem disconnect;
    private JMenuItem reload;
    private JMenuItem duplicate;
    private JMenuItem delete;
    private JMenuItem recycleConnection;
    
    private JCheckBoxMenuItem showDefaultCatalogsAndSchemas; 

    private JMenu sql;
    private JMenu exportData;
    private JMenu importData;

    private BrowserTreePopupMenuActionListener listener;
    
    BrowserTreePopupMenu(BrowserTreePopupMenuActionListener listener) {

        this.listener = listener;
        
        connect = createMenuItem("Connect", "connect", listener);
        add(connect);
        disconnect = createMenuItem("Disconnect", "disconnect", listener);
        add(disconnect);

        addSeparator();

        reload = createMenuItem("Reload", "reload", listener);
        add(reload);
        recycleConnection = createMenuItem("Recycle this Connection", "recycle", listener);
        add(recycleConnection);

        addSeparator();

        showDefaultCatalogsAndSchemas = createCheckBoxMenuItem(
                "Show only default Catalog or Schema", 
                "switchDefaultCatalogAndSchemaDisplay", listener);
        add(showDefaultCatalogsAndSchemas);

        addSeparator();

        addNewConnection = createMenuItem("New Connection", "addNewConnection", listener);
        add(addNewConnection);
        duplicate = createMenuItem("Duplicate", "duplicate", listener);
        add(duplicate);
        delete = createMenuItem("Delete", "delete", listener);
        add(delete);

        addSeparator();

        createSqlMenu(listener);
        createExportMenu(listener);
        createImportMenu(listener);

        addSeparator();
        add(createMenuItem("Connection Properties", "properties", listener));
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
        // check whether reload is available
        if (listener.hasCurrentPath()) {
            Object object = listener.getCurrentPathComponent();
            
            if (object instanceof DatabaseObjectNode) {
                
                DatabaseObjectNode node = (DatabaseObjectNode)object;
                
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

                    /*
                    boolean importExport = 
                            (node.getDatabaseUserObject().getType() == 
                                BrowserConstants.TABLE_NODE && !node.isTypeParent());
                     */
                    
                    sql.setEnabled(importExport);
                    exportData.setEnabled(importExport);
                    importData.setEnabled(importExport);
                }
            }
        }

        // re-label the menu items
        if (listener.hasCurrentSelection()) {
            String name = listener.getCurrentSelection().getName();
            connect.setText("Connect Data Source " + name);
            disconnect.setText("Disconnect Data Source " + name);
            delete.setText("Remove Data Source " + name);
            duplicate.setText("Create Duplicate of Data Source " + name);

            if (label != null) {
                reload.setText("Reload " + label);
            } else {
                reload.setText("Reload");
            }

        }

    }

    private void createImportMenu(ActionListener listener) {
        importData = MenuItemFactory.createMenu("Import Data");
        importData.add(
                createMenuItem("Import from XML File", "importXml", listener));
        importData.add(
                createMenuItem("Import from Delimited File", "importDelimited", listener));
        add(importData);
    }

    private void createExportMenu(ActionListener listener) {
        exportData = MenuItemFactory.createMenu("Export Data");
        exportData.add(createMenuItem("Export as SQL", "exportSQL", listener));
        exportData.add(createMenuItem("Export to XML File", "exportXml", listener));
        exportData.add(createMenuItem("Export to Delimited File", "exportDelimited", listener));
        exportData.add(createMenuItem("Export to Excel Spreadsheet", "exportExcel", listener));
        add(exportData);
    }

    private void createSqlMenu(ActionListener listener) {
        sql = MenuItemFactory.createMenu("SQL");
        sql.add(createMenuItem("SELECT statement", "selectStatement", listener));
        sql.add(createMenuItem("INSERT statement", "insertStatement", listener));
        sql.add(createMenuItem("UPDATE statement", "updateStatement", listener));
        sql.add(createMenuItem("CREATE TABLE statement", "createTableStatement", listener));            
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
