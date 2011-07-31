/*
 * ConnectionsTreePanel.java
 *
 * Copyright (C) 2002-2010 Takis Diakoumis
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

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.databasemediators.ConnectionMediator;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.DatabaseConnectionFactory;
import org.executequery.databasemediators.spi.DatabaseConnectionFactoryImpl;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseObjectFactory;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.databaseobjects.impl.DatabaseObjectFactoryImpl;
import org.executequery.event.ApplicationEvent;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.event.ConnectionRepositoryEvent;
import org.executequery.event.DefaultConnectionRepositoryEvent;
import org.executequery.event.UserPreferenceEvent;
import org.executequery.event.UserPreferenceListener;
import org.executequery.gui.AbstractDockedTabActionPanel;
import org.executequery.gui.browser.nodes.DatabaseHostNode;
import org.executequery.gui.browser.nodes.DatabaseObjectNode;
import org.executequery.gui.browser.nodes.RootDatabaseObjectNode;
import org.executequery.gui.browser.tree.SchemaTree;
import org.executequery.repository.DatabaseConnectionRepository;
import org.executequery.repository.RepositoryCache;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.toolbar.PanelToolBar;
import org.underworldlabs.swing.tree.DynamicTree;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.util.SystemProperties;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1525 $
 * @date     $Date: 2009-05-17 12:40:04 +1000 (Sun, 17 May 2009) $
 */
public class ConnectionsTreePanel extends AbstractDockedTabActionPanel
                                  implements ConnectionListener,
                                             UserPreferenceListener {

    public static final String TITLE = "Connections";

    private SchemaTree tree;

    private List<DatabaseConnection> connections;

    private SwingWorker worker;

    private BrowserController controller;

    private TreePath oldSelectionPath;

    private BrowserTreePopupMenu popupMenu;

    /** indicates whether to reselect the connection
     * properties node following a disconnection event. */
    private boolean rootSelectOnDisconnect;

    private DatabaseConnectionFactory databaseConnectionFactory;

    private DatabaseObjectFactoryImpl databaseObjectFactory;

    private ConnectionsTreeToolBar toolBar;

    private DatabaseHostNodeSorter hostNodeSorter;

    private TreeFindAction treeFindAction;

    public ConnectionsTreePanel() {

        super(new BorderLayout());
        init();
    }

    private void init() {
        // default to not select the root node after a disconnection
        rootSelectOnDisconnect = false;

        // initialise the controller
        controller = new BrowserController(this);

        RootDatabaseObjectNode root = new RootDatabaseObjectNode();

        DatabaseObjectFactory factory = databaseObjectFactory();

        // loop through available connections and add as leaf objects
        int count = 0;
        connections = connections();
        for (DatabaseConnection connection : connections) {

            DatabaseHost host = factory.createDatabaseHost(connection);

            DatabaseHostNode child = new DatabaseHostNode(host);
            child.setOrder(count++);

            root.add(child);
        }

        tree = new SchemaTree(root, this);

        MouseHandler mouseHandler = new MouseHandler();
        tree.addMouseListener(mouseHandler);

        treeFindAction = new TreeFindAction();
        treeFindAction.install(tree);

        // add the tools and tree to the panel
        add(createToolBar(), BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);

        // register with the event listener
        EventMediator.registerListener(this);

        enableButtons(false, false, false, false);
        tree.setSelectionRow(0);
        tree.setToggleClickCount(2);
    }

    public Action getTreeFindAction() {

        return treeFindAction;
    }

    private List<DatabaseConnection> connections() {

        return ((DatabaseConnectionRepository)RepositoryCache.load(
                DatabaseConnectionRepository.REPOSITORY_ID)).findAll();
    }

    private DatabaseConnectionFactory databaseConnectionFactory() {

        if (databaseConnectionFactory == null) {

            databaseConnectionFactory = new DatabaseConnectionFactoryImpl();
        }

        return databaseConnectionFactory;
    }

    private DatabaseObjectFactory databaseObjectFactory() {

        if (databaseObjectFactory == null) {

            databaseObjectFactory = new DatabaseObjectFactoryImpl();
        }

        return databaseObjectFactory;
    }

    private PanelToolBar createToolBar() {

        toolBar = new ConnectionsTreeToolBar(this);

        return toolBar;
    }

    private void enableButtons(boolean enableUpButton,
                               boolean enableDownButton,
                               boolean enableReloadButton,
                               boolean enableDeleteButton) {

        toolBar.enableButtons(enableUpButton, enableDownButton,
                enableReloadButton, enableDeleteButton);
    }

    /**
     * Moves the selected connection (host node) up in the list.
     */
    public void moveConnectionUp() {

        try {

            removeTreeSelectionListener();
            tree.moveSelectionUp();

            // adjust the position of the connection
            Object object = tree.getLastPathComponent();
            if (object instanceof DatabaseHostNode) {

                moveNode((DatabaseHostNode)object, DynamicTree.MOVE_UP);
            }

        } finally {

            addTreeSelectionListener();
        }
    }

    public void sortConnections() {

        if (hostNodeSorter == null) {

            hostNodeSorter = new DatabaseHostNodeSorter();
        }

        DefaultMutableTreeNode rootNode = tree.getRootNode();
        hostNodeSorter.sort(rootNode);

        tree.nodeStructureChanged(rootNode);
    }

    /**
     * Selectes and scrolls the tree to the specified path.
     *
     * @param path the tree path
     */
    protected void selectTreePath(TreePath path) {

        try {

            removeTreeSelectionListener();

            tree.scrollPathToVisible(path);
            tree.setSelectionPath(path);

        } finally {

            addTreeSelectionListener();
        }

    }

    /**
     * Sets the selected connection tree node to the
     * specified database connection.
     *
     * @param dc - the database connection to select
     */
    public void setSelectedConnection(DatabaseConnection dc) {

        DefaultMutableTreeNode node = null;
        DefaultMutableTreeNode root = tree.getRootNode();

        for (Enumeration<?> i = root.children(); i.hasMoreElements();) {

            DefaultMutableTreeNode _node = (DefaultMutableTreeNode)i.nextElement();

            Object userObject = _node.getUserObject();

            // make sure its a connection object
            if (userObject instanceof DatabaseHost) {

                DatabaseHost object = (DatabaseHost)userObject;

                if (object.getDatabaseConnection() == dc) {

                    node = _node;
                    break;
                }

            }

        }

        // select the node path
        if (node != null) {

            selectTreePath(new TreePath(node.getPath()));

            Object object = tree.getLastPathComponent();

            if (object instanceof DatabaseHostNode) {

                controller.valueChanged_((DatabaseHostNode)object);
            }

        }
    }

    /**
     * Deletes the selected connection (host node) from the list.
     */
    public void deleteConnection() {

        Object object = tree.getLastPathComponent();
        deleteConnection((DatabaseHostNode)object);
    }

    /**
     * Deletes the specified connection (host node) from the list.
     *
     * @param the node representing the connection to be removed
     */
    public void deleteConnection(DatabaseHostNode node) {

        int yesNo = GUIUtilities.displayConfirmCancelDialog(
                        "Are you sure you want to delete the connection " +
                        node + " ?");

        if (yesNo != JOptionPane.YES_OPTION) {

            return;
        }

        DatabaseConnection dc = node.getDatabaseConnection();

        // check that we're not connected
        if (dc.isConnected()) {

            try {

                ConnectionMediator.getInstance().disconnect(dc);

            } catch (DataSourceException e) {}

        }

        // the next selection index will be the index of
        // the one being removed - (index - 1)
        int index = connections.indexOf(dc);
        if (index == -1) {

            return;
        }

        // select the next connection
        int nextIndex = index + 1;
        if (index == connections.size() - 1) {

            nextIndex = index - 1;
        }

        boolean noConnectionsAvailable = true;
        if (nextIndex >= 0) {

            noConnectionsAvailable = false;
            setSelectedConnection(connections.get(nextIndex));
        }

        // remove the node from the tree
        tree.removeNode(node);

        // remove from the connections
        connections.remove(index);

        EventMediator.fireEvent(new DefaultConnectionRepositoryEvent(this,
                ConnectionRepositoryEvent.CONNECTION_MODIFIED, dc));

        if (noConnectionsAvailable) {
//            GUIUtilities.closeSelectedCentralPane();
            GUIUtils.invokeLater(new Runnable() {
                public void run() {
                    controller.selectionChanging();
                    enableButtons(false, false, false, false);
                    tree.setSelectionRow(0);
                    //controller.displayRootPanel();
                }
            });
        }

    }

    /**
     * Returns the database connection at the specified point.
     *
     * @return the connection properties object
     */
    protected DatabaseConnection getConnectionAt(Point point) {
        return getConnectionAt(tree.getPathForLocation(point.x, point.y));
    }

    /**
     * Returns the database connection associated with the specified path.
     *
     * @return the connection properties object
     */
    protected DatabaseConnection getConnectionAt(TreePath path) {
        if (path != null) {
            Object object = path.getLastPathComponent();
            if (object instanceof DatabaseObjectNode) {
                return getDatabaseConnection((DatabaseObjectNode)object);
            }
        }
        return null;
    }

    /**
     * Removes the tree listener.
     */
    protected void removeTreeSelectionListener() {
        tree.removeTreeSelectionListener();
    }

    /**
     * Adds the tree listener.
     */
    protected void addTreeSelectionListener() {
        tree.addTreeSelectionListener();
    }

    /**
     * Selects the specified node.
     */
    protected void setNodeSelected(DefaultMutableTreeNode node) {
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            tree.setSelectionPath(path);
        }
    }


    /**
     * Removes the selected tree node (database object) from the tree.
     */
    public void removeSelectedNode() {
        int row = -1;
        try {
            // remove the listener
            removeTreeSelectionListener();

            // store the current row
            row = tree.getSelectionRows()[0];

            // retrieve the current selection node
            BrowserTreeNode node = (BrowserTreeNode)tree.getLastPathComponent();
            tree.removeNode(node);
        }
        finally {
            // add listener and select row above
            addTreeSelectionListener();
            if (row >= 0) {
                row = (row == 0 ? 1 : row - 1);
                tree.setSelectionRow(row);
            }
        }
    }

    public void connectAll() {


    }

    public void disconnectAll() {

    }

    public void searchNodes() {

        getTreeFindAction().actionPerformed(new ActionEvent(this, 0, "searchNodes"));
    }

    /**
     * Creates a new connection and adds it to the bottom of the list.
     */
    public void newConnection() {

        String name = buildConnectionName("New Connection");
        newConnection(databaseConnectionFactory().create(name));
    }

    /**
     * Creates a new connection based on the specified connection.
     *
     * @param dc - the connection the new one is to be based on
     */
    public void newConnection(DatabaseConnection dc) {

        DatabaseHost host = databaseObjectFactory().createDatabaseHost(dc);

        connections.add(dc);

        tree.addToRoot(new DatabaseHostNode(host));

        EventMediator.fireEvent(
                new DefaultConnectionRepositoryEvent(
                        this, ConnectionRepositoryEvent.CONNECTION_ADDED, dc));

    }

    /**
     * Moves the selected connection (host node) down in the list.
     */
    public void moveConnectionDown() {
        try {

            removeTreeSelectionListener();
            tree.moveSelectionDown();

            // adjust the position of the connection
            Object object = tree.getLastPathComponent();
            if (object instanceof DatabaseHostNode) {

                moveNode((DatabaseHostNode)object, DynamicTree.MOVE_DOWN);
            }

        } finally {

            addTreeSelectionListener();
        }
    }

    public void nodeMoved(DatabaseHostNode node, int newIndex) {
        DatabaseConnection dc = node.getDatabaseConnection();
        connections.remove(dc);
        connections.add(newIndex, dc);
    }

    private void moveNode(DatabaseHostNode node, int direction) {

        DatabaseConnection dc = node.getDatabaseConnection();

        int currentIndex = connections.indexOf(dc);
        if (currentIndex == 0 && direction == DynamicTree.MOVE_UP) {
            return;
        }

        int newIndex = -1;
        if (direction == DynamicTree.MOVE_UP) {
            newIndex = currentIndex - 1;
        } else {
            newIndex = currentIndex + 1;
            if (newIndex > (connections.size() - 1)) {
                return;
            }
        }

        connections.remove(currentIndex);
        connections.add(newIndex, dc);
    }

    /**
     * Indicates that a node name has changed and fires a call
     * to repaint the tree display.
     */
    protected void nodeNameValueChanged(Object nodeObject) {
        TreeNode node = tree.getNodeFromRoot(nodeObject);
        if (node != null) {
            tree.nodeChanged(node);
        }
    }

    /**
     * Returns the currently selected node's user object where the
     * node is a BrowserTreeNode and the user object is a BaseDatabaseObject.
     * If the above is not met, null is returned.
     *
     * @return the user object of the selected node where the
     *         user object is a DBaseDatabaseObject
     */
    protected BrowserTreeNode getSelectedBrowserNode() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return null;
        }

        // make sure we have a BrowserTreeNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return null;
        }

        return (BrowserTreeNode)object;
    }

    /**
     * Returns the whether the currently selected node's user object
     * is a parent type where the node is a BrowserTreeNode and the
     * user object is a BaseDatabaseObject. If the above is not met, false is
     * returned, otherwise the object is evaluated.
     *
     * @return true | false
     */
    protected boolean isTypeParentSelected() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return false;
        }

        // make sure we have a BrowserTreeNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return false;
        }

        // return the parent connection meta object
        return ((BrowserTreeNode)object).isTypeParent();
    }

    /**
     * Returns the currently selected node's user object where the
     * node is a BrowserTreeNode and the user object is a BaseDatabaseObject.
     * If the above is not met, null is returned.
     *
     * @return the user object of the selected node where the
     *         user object is a DBaseDatabaseObject
     */
    protected NamedObject getSelectedNamedObject() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return null;
        }

        // make sure we have a BrowserTreeNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof DatabaseObjectNode)) {
            return null;
        }

        DatabaseObjectNode node = (DatabaseObjectNode)object;
        return node.getDatabaseObject();
    }

    /**
     * Returns the selected meta object host node.
     *
     * @return the selected host node meta object
     */
    protected DatabaseHost getSelectedMetaObject() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return null;
        }

        // make sure we have a DatabaseObjectNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof DatabaseObjectNode)) {
            return null;
        }

        // return the parent connection meta object
        DatabaseObjectNode node = (DatabaseObjectNode)object;
        DatabaseObjectNode parent = getParentNode(node);
        return ((DatabaseHost)parent.getDatabaseObject());
    }

    /**
     * Returns the selected database connection.
     *
     * @return the selected connection properties object
     */
    public DatabaseConnection getSelectedDatabaseConnection() {
        DatabaseHost object = getSelectedMetaObject();
        if (object != null) {
            return object.getDatabaseConnection();
        }
        return null;
    }

    // ------------------------------------------
    // ConnectionListner implementation
    // ------------------------------------------

    /**
     * Indicates a connection has been established.
     *
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent) {

        DatabaseConnection dc = connectionEvent.getDatabaseConnection();
        DatabaseObjectNode node = getHostNode(dc);

        // if the host node itself is selected - enable/disable buttons
        if (tree.getSelectionPath().getLastPathComponent() == node) {

            enableButtons(true, true, true, false);
        }

        nodeStructureChanged(node);
    }

    /**
     * Indicates a connection has been closed.
     *
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent) {

        DatabaseConnection dc = connectionEvent.getDatabaseConnection();

        DatabaseHostNode host = (DatabaseHostNode)getHostNode(dc);
        host.disconnected();

        // check if we select the root node
        if (rootSelectOnDisconnect) {

            tree.setSelectionRow(0);

        } else { // otherwise select the host node if not already selected

//            if (tree.getSelectionPath().getLastPathComponent() != host) {
//
//                // change below to not reselect after disconnect
//
//                tree.setSelectionPath(new TreePath(host.getPath()));
//            }

            if (tree.getSelectionPath().getLastPathComponent() == host) {

                enableButtons(true, true, false, true);
            }

        }

        nodeStructureChanged(host);
    }

    public boolean canHandleEvent(ApplicationEvent event) {
        return (event instanceof ConnectionEvent) ||
            (event instanceof UserPreferenceEvent);
    }

    // ------------------------------------------

    /**
     * Returns the previously selected path before the current
     * selection.
     *
     * @return the previous path
     */
    protected TreePath getOldSelectionPath() {
        return oldSelectionPath;
    }

    /**
     * Returns the previously selected browse node before the
     * current selection.
     *
     * @return the previous node selection
     */
    protected BrowserTreeNode getOldBrowserNodeSelection() {
        Object object = getOldSelectionPath().getLastPathComponent();
        if (object != null && object instanceof BrowserTreeNode) {
            return (BrowserTreeNode)object;
        }
        return null;
    }

    protected BrowserTreeNode getParentConnectionNode(DatabaseConnection dc) {
        return null;
    }

    protected DatabaseObjectNode getHostNode(DatabaseConnection dc) {

        for (Enumeration<?> i = tree.getRootNode().children(); i.hasMoreElements();) {

            Object object = i.nextElement();

            if (object instanceof DatabaseObjectNode) {

                DatabaseObjectNode node = (DatabaseObjectNode)object;

                if (node.getType() == NamedObject.HOST) {

                    DatabaseHost host = (DatabaseHost)node.getDatabaseObject();

                    if (host.getDatabaseConnection() == dc) {

                        return node;
                    }

                }
            }
        }

        return null;
    }

    /**
     * Notification that the currently selected node (a host)
     * has had their associated db connection closed.
     */
    protected void selectedNodeDisconnected() {
        Object object = tree.getLastPathComponent();
        if (!(object instanceof DatabaseObjectNode)) {
            return;
        }

        // remove all choldren from the host node
        // of the current path
        DatabaseObjectNode node = (DatabaseObjectNode)object;
        if (!(node.getUserObject() instanceof DatabaseHost)) {
            node = getParentNode(node);
        }

        //node.setExpanded(false);
        DatabaseObjectNode parent = getParentNode(node);
        parent.removeAllChildren();
        nodeStructureChanged(parent);
    }

    /**
     * Notification that the currently selected node (a host)
     * has had their associated db connection created.
     */
    protected void selectedNodeConnected() {

        if (tree.isSelectionEmpty()) {

            return;
        }

        Object object = tree.getLastPathComponent();
        if (!(object instanceof DatabaseObjectNode)) {

            return;
        }

        // ensure node is expandable
        DatabaseObjectNode node = (DatabaseObjectNode)object;
        if (node.isLeaf() && node instanceof DatabaseHostNode) {

            pathExpanded(tree.getSelectionPath());
            nodeStructureChanged(node);
        }

    }

    public void pathChanged(TreePath newPath) {

        pathChanged(oldSelectionPath, newPath);
    }

    public void pathChanged(TreePath oldPath, TreePath newPath) {

        /*
        System.out.println("pathChanged");

        try {

            if (oldPath == null || shouldChangeView) {

            }

        } finally {

            shouldChangeView = true;
        }
        */
        doPathChanged(oldPath, newPath);
    }

    /**
     * Called whenever the value of the selection changes.
     * This will store the current path selection.
     *
     * @param the event that characterizes the change
     */
    private void doPathChanged(TreePath oldPath, TreePath newPath) {

        // store the last position
        oldSelectionPath = oldPath;

        // examine the last selection
        if (oldSelectionPath != null) {

            Object lastObject = oldSelectionPath.getLastPathComponent();

            if (lastObject instanceof DatabaseObjectNode) {

                DatabaseObjectNode dbObject = (DatabaseObjectNode)lastObject;

                boolean modified = false;
                try {

                    modified = dbObject.isObjectModified();

                } catch (DataSourceException dse) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("An error occurred examining any changes.").
                       append("\n\nThe system returned:\n").
                       append(dse.getExtendedMessage());

                    GUIUtilities.displayExceptionErrorDialog(sb.toString(), dse);
                    tree.setSelectionPath(oldSelectionPath);

                    return;
                }

                if (modified) {

                    // apply any changes before proceeding
                    int yesNo = GUIUtilities.displayConfirmCancelDialog(
                            "Do you wish to apply your changes?");

                    if (yesNo == JOptionPane.NO_OPTION) {

                        dbObject.revert();

                    } else if (yesNo == JOptionPane.CANCEL_OPTION) {

                        tree.setSelectionPath(oldSelectionPath);
                        return;

                    } else if (yesNo == JOptionPane.YES_OPTION) {

                        try {

                            dbObject.applyChanges();

                        } catch (DataSourceException dse) {

                            StringBuilder sb = new StringBuilder();
                            sb.append("An error occurred applying the specified changes.").
                               append("\n\nThe system returned:\n").
                               append(dse.getExtendedMessage());

                            GUIUtilities.displayExceptionErrorDialog(sb.toString(), dse);
                            tree.setSelectionPath(oldSelectionPath);

                            return;
                        }

                    }
                }
            }
        }

        Object object = newPath.getLastPathComponent();
        if (object == null) {

            return;
        }

        //System.out.println("parent: "+e.getPath().getParentPath());
        //System.out.println("node: "+newPath);

        controller.selectionChanging();

        if (object == tree.getRootNode()) { // root node

            controller.displayRootPanel();
            enableButtons(false, false, false, false);
            return;
        }

        final DatabaseObjectNode node = (DatabaseObjectNode)object;

        if (node instanceof DatabaseHostNode) {

            DatabaseHostNode hostNode = (DatabaseHostNode)node;

            boolean hostConnected = hostNode.isConnected();
            enableButtons(true, true, hostConnected, !hostConnected);

        } else {

            enableButtons(false, false, true, false);
        }

        worker = new SwingWorker() {
            public Object construct() {
                try {
                    treeExpanding = true;
                    GUIUtilities.showWaitCursor();
                    /*
                    if (node.allowsChildren() && node.isLeaf()) {
                        doNodeExpansion(node);
                        //node.populateChildren();
                    }
                    */
                    valueChanged(node);
                }
                finally {
                    treeExpanding = false;
                }
                return null;
            }
            public void finished() {
                GUIUtilities.showNormalCursor();
                treeExpanding = false;
            }
        };
        worker.start();
    }

    private synchronized void doNodeExpansion(DatabaseObjectNode node) {
        try {
            node.populateChildren();
            nodeStructureChanged(node);
        } catch (DataSourceException e) {
            controller.handleException(e);
        }
    }

    private synchronized void valueChanged(DatabaseObjectNode node) {
        controller.valueChanged_(node);
    }

    /** Reloads the currently selected node. */
    public void reloadSelection() {
        reloadPath(tree.getSelectionPath());
    }

    /** Reloads the specified tree path. */
    public void reloadPath(TreePath path) {

        try {

            if (treeExpanding || path == null) {

                return;
            }

            Object object = path.getLastPathComponent();
            if (!(object instanceof DatabaseObjectNode)) {

                return;
            }

            GUIUtilities.showWaitCursor();

            boolean expanded = tree.isExpanded(path);
            if (expanded) {

                tree.collapsePath(path);
            }

            DatabaseObjectNode node = (DatabaseObjectNode)object;
            node.reset();

            nodeStructureChanged(node);
            pathExpanded(path);

            if (expanded) {

                tree.expandPath(path);
            }

            pathChanged(oldSelectionPath, path);

        } finally {

            GUIUtilities.showNormalCursor();
        }
    }

    protected void nodeStructureChanged(TreeNode node) {
        tree.nodeStructureChanged(node);
    }

    public void pathExpanded(TreePath path) {

        Object object = path.getLastPathComponent();

        if (!(object instanceof DatabaseObjectNode)) {
            return;
        }

        final DatabaseObjectNode node = (DatabaseObjectNode)object;
        worker = new SwingWorker() {
            public Object construct() {
                GUIUtilities.showWaitCursor();
                doNodeExpansion(node);
                return null;
            }
            public void finished() {
                GUIUtilities.showNormalCursor();
            }

        };
        worker.start();
    }

    /** provides an indicator that an expansion is in progress */
    private boolean treeExpanding = false;

    private BrowserTreeRootPopopMenu rootPopupMenu;

    protected DatabaseObjectNode getParentNode(DatabaseObjectNode child) {

        if (child instanceof DatabaseHostNode) {

            return child;
        }

        TreeNode parent = child.getParent();
        while (parent != null) {

            if (parent instanceof DatabaseHostNode) {

                return (DatabaseObjectNode)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Selects the node that matches the specified prefix forward
     * from the currently selected node.
     *
     * @param prefix - the prefix of the node to select
     */
    protected void selectBrowserNode(final String prefix) {
        // make sure it has its children
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();

        if (node.getChildCount() == 0) {
            doNodeExpansion((DatabaseObjectNode)node);
        }


//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
                tree.expandSelectedRow();
                tree.selectNextNode(prefix);
//            }
//        });
    }

    /**
     * Returns the connection properties object associated with
     * the specified child node.
     */
    protected DatabaseConnection getDatabaseConnection(DatabaseObjectNode child) {

        DatabaseHost databaseHost = getConnectionObject(child);

        if (databaseHost != null) {

            return databaseHost.getDatabaseConnection();
        }

        return null;
    }

    /**
     * Returns the DatabaseHost (host node) associated with
     * the specified child node.
     */
    protected DatabaseHost getConnectionObject(DatabaseObjectNode child) {
        // get the host node for this path
        DatabaseObjectNode parent = getParentNode(child);
        if (parent != null) {
            return (DatabaseHost)parent.getDatabaseObject();
        }
        return null;
    }

    /**
     * Removes the selected node.<br>
     *
     * This will attempt to propagate the call to the connected
     * database object using a DROP statement.
     */
    public void removeTreeNode() {
        TreePath selection = tree.getSelectionPath();

        if (selection != null) {

            Object object = selection.getLastPathComponent();

            if (object instanceof DatabaseObjectNode) {

                DatabaseObjectNode dbObject = (DatabaseObjectNode)object;

                if (!dbObject.isDroppable()) {

                    return;
                }

                int yesNo = GUIUtilities.displayConfirmDialog(
                                   "Are you sure you want to drop " + dbObject + "?");

                if (yesNo == JOptionPane.NO_OPTION) {

                    return;
                }

                removeTreeSelectionListener();
                int row = tree.getSelectionRows()[0];

                try {

                    dbObject.drop(); // quoted cases
                    tree.removeNode(dbObject);

                } catch (DataSourceException e) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("An error occurred removing the selected object.").
                       append("\n\nThe system returned:\n").
                       append(e.getExtendedMessage());

                    GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);

                } finally {

                    addTreeSelectionListener();

                    if (row >= 0) {
                        row = (row == 0 ? 1 : row - 1);
                        tree.setSelectionRow(row);
                    }

                }

            }

        }

    }

    /**
     * Returns the name of a new connection to be added where
     * the name of the connection may already exist.
     *
     * @param name - the name of the connection
     */
    protected String buildConnectionName(String name) {
        int count = 0;
        for (int i = 0, n = connections.size(); i < n; i++) {
            DatabaseConnection _dc = connections.get(i);
            if (_dc.getName().startsWith(name)) {
                count++;
            }
        }

        if (count > 0) {
            count++;
            name += " " + count;
        }
        return name;
    }

    public boolean isRootSelectOnDisconnect() {
        return rootSelectOnDisconnect;
    }

    public void setRootSelectOnDisconnect(boolean rootSelectOnDisconnect) {
        this.rootSelectOnDisconnect = rootSelectOnDisconnect;
    }

    public String toString() {
        return TITLE;
    }

    public static final String MENU_ITEM_KEY = "viewConnections";

    public static final String PROPERTY_KEY = "system.display.connections";

    // ----------------------------------------
    // DockedTabView Implementation
    // ----------------------------------------

    /**
     * Returns the display title for this view.
     *
     * @return the title displayed for this view
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Returns the name defining the property name for this docked tab view.
     *
     * @return the key
     */
    public String getPropertyKey() {
        return PROPERTY_KEY;
    }

    /**
     * Returns the name defining the menu cache property
     * for this docked tab view.
     *
     * @return the preferences key
     */
    public String getMenuItemKey() {
        return MENU_ITEM_KEY;
    }


    protected void handleException(Throwable e) {
        controller.handleException(e);
    }

    protected void disconnect(DatabaseConnection dc) {
        controller.disconnect(dc);
    }

    protected void connect(DatabaseConnection dc) {
        controller.connect(dc);
    }

    protected void setTreeSelectionPath(TreePath treePath) {
        tree.setSelectionPath(treePath);
    }

    protected TreePath getTreeSelectionPath() {
        return tree.getSelectionPath();
    }

    protected TreePath getTreePathForLocation(int x, int y) {
        return tree.getPathForLocation(x, y);
    }

    private BrowserTreePopupMenu getBrowserTreePopupMenu() {
        if (popupMenu == null) {
            popupMenu = new BrowserTreePopupMenu(
                    new BrowserTreePopupMenuActionListener(this));
        }
        return popupMenu;
    }

    private BrowserTreeRootPopopMenu getBrowserRootTreePopupMenu() {
        if (rootPopupMenu == null) {
            rootPopupMenu = new BrowserTreeRootPopopMenu(this);
        }
        return rootPopupMenu;
    }

    private class MouseHandler extends MouseAdapter {

        public MouseHandler() {}

        public void mouseClicked(MouseEvent e) {

//            int clickCount = e.getClickCount();
//            System.out.println("mouseClicked clicks : " + clickCount);

            if (e.getClickCount() < 2) {

                return;
            }

            twoClicks(e);
        }

        public void mousePressed(MouseEvent e) {

//            int clickCount = e.getClickCount();
//            System.out.println("mousePressed clicks : " + clickCount);

            if (e.getClickCount() < 2) {

                maybeShowPopup(e);
                return;
            }

            twoClicks(e);
        }

        private void twoClicks(MouseEvent e) {

            TreePath path = pathFromMouseEvent(e);
            if (path != null && path == getTreeSelectionPath()) {

                connectOnDoubleClick(path);
                pathChanged(path);
            }
        }

        public void mouseReleased(MouseEvent e) {

            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {

                Point point = new Point(e.getX(), e.getY());
                TreePath treePathForLocation = getTreePathForLocation(point.x, point.y);

                if (tree.getRowForPath(treePathForLocation) == 0) {

                    BrowserTreeRootPopopMenu popup = getBrowserRootTreePopupMenu();
                    popup.show(e.getComponent(), point.x, point.y);
                    return;
                }

                BrowserTreePopupMenu popup = getBrowserTreePopupMenu();
                popup.setCurrentPath(treePathForLocation);

                DatabaseConnection connection = getConnectionAt(point);

                if (connection == null) {
                    return;
                }

                popup.setCurrentSelection(connection);

                if (popup.hasCurrentSelection()) {

                    try {

                        removeTreeSelectionListener();
                        setTreeSelectionPath(popup.getCurrentPath());

                    } finally {

                        addTreeSelectionListener();
                    }

                    popupMenu.show(e.getComponent(), point.x, point.y);
                }
            }
        }

        private TreePath pathFromMouseEvent(MouseEvent e) {

            return getTreePathForLocation(e.getX(), e.getY());
        }

        private void connectOnDoubleClick(TreePath path) {

            if (doubleClickHostToConnect()) {

                Object node = path.getLastPathComponent();
                if (node instanceof DatabaseHostNode) {

                    DatabaseHostNode hostNode = (DatabaseHostNode) node;
                    DatabaseConnection databaseConnection = hostNode.getDatabaseConnection();
                    if (!databaseConnection.isConnected()) {

                        connect(databaseConnection);
                    }

                }

            }
        }


    }

    private boolean doubleClickHostToConnect() {

        return SystemProperties.getBooleanProperty("user", "browser.double-click.to.connect");
    }

    public void preferencesChanged(UserPreferenceEvent event) {

        if (event.getEventType() == UserPreferenceEvent.ALL) {

            RootDatabaseObjectNode root = (RootDatabaseObjectNode)tree.getRootNode();
            List<DatabaseHostNode> hosts = root.getHostNodes();

            for (DatabaseHostNode host : hosts) {

                host.applyUserPreferences();
            }

        }

    }

//    private boolean shouldChangeView = true;

    public void schemaTreeMouseEvent(MouseEvent e) {

        /*

        shouldChangeView = true;
        int clickCount = e.getClickCount();

        System.out.println("schemaTreeMouseEvent clicks : " + clickCount);

        if (clickCount >= 2 && doubleClickHostToConnect()) {

            shouldChangeView = false;
        }
        */
    }

}
