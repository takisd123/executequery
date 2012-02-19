/*
 * SchemaTree.java
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

package org.executequery.gui.browser.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.executequery.GUIUtilities;
import org.executequery.SuppressedException;
import org.executequery.components.table.BrowserTreeCellRenderer;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.gui.browser.BrowserConstants;
import org.executequery.gui.browser.ConnectionsTreePanel;
import org.executequery.gui.browser.nodes.DatabaseHostNode;
import org.executequery.gui.browser.nodes.RootDatabaseObjectNode;
import org.executequery.util.ThreadUtils;
import org.underworldlabs.swing.tree.DynamicTree;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class SchemaTree extends DynamicTree
                        implements TreeExpansionListener,
                                   TreeSelectionListener {

    private static final int ROW_HEIGHT = 24;
    
    private ConnectionsTreePanel panel;

    public SchemaTree(DefaultMutableTreeNode root, ConnectionsTreePanel panel) {

        super(root);
        this.panel = panel;

        addTreeSelectionListener(this);
        addTreeExpansionListener(this);

        DefaultTreeCellRenderer renderer = new BrowserTreeCellRenderer(loadIcons());
        setCellRenderer(renderer);

//        setEditable(true);
//        setCellEditor(new ConnectionTreeCellEditor(this, renderer));
        
        setShowsRootHandles(true);
        setDragEnabled(true);

        setDropMode(DropMode.INSERT);
        setTransferHandler(new TreeTransferHandler());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        setRowHeight(ROW_HEIGHT);
    }

    private Map<String, Icon> loadIcons() {

        Map<String, Icon> icons = new HashMap<String, Icon>();
        for (int i = 0; i < BrowserConstants.NODE_ICONS.length; i++) {
            icons.put(BrowserConstants.NODE_ICONS[i],
                GUIUtilities.loadIcon(BrowserConstants.NODE_ICONS[i], true));
        }

        icons.put(BrowserConstants.DATABASE_OBJECT_IMAGE,
            GUIUtilities.loadIcon(BrowserConstants.DATABASE_OBJECT_IMAGE, true));

        return icons;
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {

        panel.schemaTreeMouseEvent(e);
        super.processMouseEvent(e);
    }
    
    /** Removes the tree listener. */
    public void removeTreeSelectionListener() {

        removeTreeSelectionListener(this);
    }

    /** Adds the tree listener. */
    public void addTreeSelectionListener() {

        addTreeSelectionListener(this);
    }

    // --------------------------------------------------
    // ------- TreeSelectionListener implementation
    // --------------------------------------------------

    public void valueChanged(TreeSelectionEvent e) {

        panel.pathChanged(e.getOldLeadSelectionPath(), e.getPath());
    }


    // --------------------------------------------------
    // ------- TreeExpansionListener implementation
    // --------------------------------------------------

    public void treeExpanded(TreeExpansionEvent e) {

        panel.pathExpanded(e.getPath());
    }

    public void treeCollapsed(TreeExpansionEvent e) {

        // do nothing
    }

    // nice example: http://www.coderanch.com/t/346509/GUI/java/JTree-drag-drop-inside-one
    
    class TreeTransferHandler extends TransferHandler {

        DataFlavor nodesFlavor;
        DataFlavor[] flavors = new DataFlavor[1];
        DefaultMutableTreeNode[] nodesToRemove;

        public TreeTransferHandler() {
            try {
                String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                                  ";class=\"" +
                    javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
                                  "\"";
                nodesFlavor = new DataFlavor(mimeType);
                flavors[0] = nodesFlavor;
            } catch(ClassNotFoundException e) {
                System.out.println("ClassNotFound: " + e.getMessage());
            }
        }

        public boolean canImport(TransferHandler.TransferSupport support) {
            
            if (!support.isDrop()) {

                return false;
            }

            support.setShowDropLocation(true);
            if (!support.isDataFlavorSupported(nodesFlavor)) {

                return false;
            }

            // Do not allow a drop on the drag source selections.
            JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
            JTree tree = (JTree) support.getComponent();

            int dropRow = tree.getRowForPath(dl.getPath());
            int[] selRows = tree.getSelectionRows();
            for (int i = 0; i < selRows.length; i++) {
                if (selRows[i] == dropRow) {
                    return false;
                }
            }
            // Do not allow MOVE-action drops if a non-leaf node is
            // selected unless all of its children are also selected.
            int action = support.getDropAction();
            if (action == MOVE) {

                return haveCompleteNode(tree);
            }

            // Do not allow a non-leaf node to be copied to a level
            // which is less than its source level.
            TreePath dest = dl.getPath();
            DefaultMutableTreeNode target = (DefaultMutableTreeNode)dest.getLastPathComponent();
            TreePath path = tree.getPathForRow(selRows[0]);
            DefaultMutableTreeNode firstNode =
                (DefaultMutableTreeNode)path.getLastPathComponent();

            if (firstNode.getChildCount() > 0 &&
                   target.getLevel() < firstNode.getLevel()) {
                return false;
            }
            return true;
        }

        private boolean haveCompleteNode(JTree tree) {
            
            int[] selRows = tree.getSelectionRows();
            TreePath path = tree.getPathForRow(selRows[0]);
            DefaultMutableTreeNode first = (DefaultMutableTreeNode)path.getLastPathComponent();
            
            int childCount = first.getChildCount();

            // first has children and no children are selected.
            if (childCount > 0 && selRows.length == 1) {
             
                return false;
            }

            // first may have children.
            for (int i = 1; i < selRows.length; i++) {

                path = tree.getPathForRow(selRows[i]);
                DefaultMutableTreeNode next = (DefaultMutableTreeNode)path.getLastPathComponent();
                if (first.isNodeChild(next)) {

                    // Found a child of first.
                    if (childCount > selRows.length-1) {

                        // Not all children of first are selected.
                        return false;
                    }

                }
            }
            return true;
        }

        @Override
        public void exportAsDrag(JComponent comp, InputEvent e, int action) {
            
            if (loadingNode) {

                // hack! 
                throw new SuppressedException("Node selection pending before drag");
            }
            
            super.exportAsDrag(comp, e, action);
        }
        
        protected Transferable createTransferable(JComponent c) {

            JTree tree = (JTree)c;
            TreePath[] paths = tree.getSelectionPaths();

            if (paths != null) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
                if (!(node instanceof DatabaseHostNode) || isExpanded(paths[0])) {
                    
                    return null;
                }
                
                // Make up a node array of copies for transfer and
                // another for/of the nodes that will be removed in
                // exportDone after a successful drop.
                List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
                List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
                
                DefaultMutableTreeNode copy = copy((DatabaseHostNode) node);
                copies.add(copy);
                toRemove.add(node);

                for (int i = 1; i < paths.length; i++) {

                    DefaultMutableTreeNode next =
                        (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                    
                    // Do not allow higher level nodes to be added to list.
                    if (next.getLevel() < node.getLevel()) {
                    
                        break;

                    } else if (next.getLevel() > node.getLevel()) {  // child node

                        copy.add(copy(next));
                        // node already contains child

                    } else {                                        // sibling

                        copies.add(copy(next));
                        toRemove.add(next);
                    }

                }

                DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
                nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
            
                return new NodesTransferable(nodes);
            }

            return null;
        }

        /** Defensive copy used in createTransferable. */
        private DefaultMutableTreeNode copy(TreeNode node) {
            return new DefaultMutableTreeNode(node);
        }

        private DefaultMutableTreeNode copy(DatabaseHostNode node) {
            return new DatabaseHostNode((DatabaseHost) node.getDatabaseObject());
        }
        
        protected void exportDone(JComponent source, Transferable data, int action) {

            TreePath[] paths = getSelectionPaths();
            final Object lastPathComponent = paths[0].getLastPathComponent();

            if ((action & MOVE) == MOVE) {
                
                JTree tree = (JTree)source;
                DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

                // Remove nodes saved in nodesToRemove in createTransferable.
                for(int i = 0; i < nodesToRemove.length; i++) {

                    model.removeNodeFromParent(nodesToRemove[i]);
                }

                panel.rebuildConnectionsFromTree();

                panel.repaint();
                ThreadUtils.invokeLater(new Runnable() {
                    public void run() {
                        if (lastPathComponent instanceof DatabaseHostNode) {

                            String prefix = lastPathComponent.toString();
                            TreePath path = null;

                            // need to make sure we have the right node since
                            // nodes with the same prefix but higher will 
                            // return first 
                            
                            int index = 0;
                            int rowCount = getRowCount();
                            while (index < rowCount) { 

                                path = getNextMatch(prefix, index, Position.Bias.Forward);
                                if (prefix.equals(path.getLastPathComponent().toString())) {

                                    break;
                                }

                                index++;
                            }
                            
                            if (path != null) {
                            
                                try {
                                    
                                    removeTreeSelectionListener();
                                    scrollPathToVisible(path);
                                    setSelectionPath(path);
    
                                } finally {

                                    addTreeSelectionListener();
                                }

                            }

                        }
                    }    
                });
                
            }
            
        }

        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        public boolean importData(TransferHandler.TransferSupport support) {
            
            if (!canImport(support)) {
                return false;
            }

            // Extract transfer data.
            DefaultMutableTreeNode[] nodes = null;
            try {
                Transferable t = support.getTransferable();
                nodes = (DefaultMutableTreeNode[])t.getTransferData(nodesFlavor);
            } catch(UnsupportedFlavorException ufe) {
                System.out.println("UnsupportedFlavor: " + ufe.getMessage());
            } catch(java.io.IOException ioe) {
                System.out.println("I/O error: " + ioe.getMessage());
            }
            // Get drop location info.
            JTree.DropLocation dl =
                    (JTree.DropLocation)support.getDropLocation();
            int childIndex = dl.getChildIndex();
            TreePath dest = dl.getPath();
            DefaultMutableTreeNode parent =
                (DefaultMutableTreeNode)dest.getLastPathComponent();
            
            if (!(parent instanceof RootDatabaseObjectNode)) {
                return false;
            }
            
            JTree tree = (JTree)support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            // Configure for drop mode.
            int index = childIndex;    // DropMode.INSERT
            if (childIndex == -1) {     // DropMode.ON
                index = parent.getChildCount();
            }
            // Add data to model.
            for(int i = 0; i < nodes.length; i++) {
                model.insertNodeInto(nodes[i], parent, index++);
            }
            
            return true;
        }

        public String toString() {
            return getClass().getName();
        }

        public class NodesTransferable implements Transferable {

            private DefaultMutableTreeNode[] nodes;

            public NodesTransferable(DefaultMutableTreeNode[] nodes) {
                this.nodes = nodes;
             }

            public Object getTransferData(DataFlavor flavor)
                                     throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor))
                    throw new UnsupportedFlavorException(flavor);
                return nodes;
            }

            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return nodesFlavor.equals(flavor);
            }
        }
    
    }

    private boolean loadingNode;

    public void startLoadingNode() {

        loadingNode = true;
    }

    public void finishedLoadingNode() {

        loadingNode = false;
    }

    public void connectionNameChanged(String name) {
        
        panel.connectionNameChanged(name);
    }
    
}
