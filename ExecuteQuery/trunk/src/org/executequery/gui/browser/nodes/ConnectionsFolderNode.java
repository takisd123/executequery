/*
 * ConnectionsFolderNode.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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

package org.executequery.gui.browser.nodes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.gui.browser.ConnectionsFolder;

/** 
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1094 $
 * @date     $Date: 2012-02-20 23:51:20 +1100 (Mon, 20 Feb 2012) $
 */
public class ConnectionsFolderNode extends RootDatabaseObjectNode {
    
    private final ConnectionsFolder connectionsFolder;

    public ConnectionsFolderNode(ConnectionsFolder connectionsFolder) {

        this.connectionsFolder = connectionsFolder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DatabaseObjectNode copy() {

        ConnectionsFolderNode copy = new ConnectionsFolderNode(connectionsFolder);
        for (Enumeration<DatabaseObjectNode> i = children(); i.hasMoreElements();) {
            
            copy.add(i.nextElement().copy());
        }
        
        return copy;
    }
    
    @Override
    public boolean isDraggable() {
        return true;
    }
    
    @Override
    public boolean isNameEditable() {
        
        return true;
    }
    
    /**
     * Propagates the call to the underlying database object.
     */
    public int getType() {
        
        return NamedObject.BRANCH_NODE;
    }

    /**
     * Returns the name of the root node.
     */
    public String getName() {
        
        return connectionsFolder.getName();
    }

    public void setName(String name) {
        
        connectionsFolder.setName(name);
    }
    
    public ConnectionsFolder getConnectionsFolder() {
     
        return connectionsFolder;
    }

    @SuppressWarnings("unchecked")
    public List<DatabaseHost> getDatabaseHosts() {
        
        List<DatabaseHost> hosts = new ArrayList<DatabaseHost>(getChildCount());
        for (Enumeration<DatabaseHostNode> i = children(); i.hasMoreElements();) {
            
            DatabaseHostNode node = i.nextElement();
            DatabaseHost host = (DatabaseHost) node.getDatabaseObject();
            hosts.add(host);
        }
        
        return hosts;
    }
    
    @SuppressWarnings("unchecked")
    public List<DatabaseHostNode> getDatabaseHostNodes() {
        
        List<DatabaseHostNode> nodes = new ArrayList<DatabaseHostNode>(getChildCount());
        for (Enumeration<DatabaseHostNode> i = children(); i.hasMoreElements();) {
            
            nodes.add(i.nextElement());
        }
        
        return nodes;
    }
    
    @SuppressWarnings("unchecked")
    public DatabaseObjectNode getHostNode(DatabaseConnection dc) {

        for (Enumeration<DatabaseHostNode> i = children(); i.hasMoreElements();) {
            
            DatabaseHostNode node = i.nextElement();
            if (((DatabaseHost) node.getDatabaseObject()).getDatabaseConnection() == dc) {

                return node;
            }
            
        }
        
        return null;
    }

    public void removeNode(DatabaseHostNode databaseHostNode) {

        connectionsFolder.removeConnection(databaseHostNode.getDatabaseConnection().getId());
    }

    public void addNewHostNode(DatabaseHostNode databaseHostNode) {

        super.add(databaseHostNode);
        connectionsFolder.addConnection(databaseHostNode.getDatabaseConnection().getId());
    }
    
}


