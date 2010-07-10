/*
 * NodeMoveTransferHandler.java
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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author takisd
 */
public class NodeMoveTransferHandler extends TransferHandler {
    
    public NodeMoveTransferHandler() {
        
        super();        
    }

    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return true;
    }
    
    /**
     * create a transferable that contains all paths that are currently selected in
     * a given tree
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     * @return  all selected paths in the given tree
     * (or null if the given component is not a tree)
     */
    protected Transferable createTransferable(JComponent c) {
        Transferable t = null;
        
        if(c instanceof JTree) {
            
            JTree tree = (JTree) c;
            t = new GenericTransferable(tree.getSelectionPaths());

            dragPath = tree.getSelectionPath();

            if (dragPath != null) {
                draggedNode = (MutableTreeNode) dragPath.getLastPathComponent();
            }

        }
        return t;
    }
    
    /**
     * move selected paths when export of drag is done
     * @param source  the component that was the source of the data
     * @param data  the data that was transferred or possibly null if the action is NONE.
     * @param action  the actual action that was performed
     */
    protected void exportDone(JComponent source, Transferable data, int action) {
        
        //System.out.println("data: "+ data);
        
        if(source instanceof JTree) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            TreePath currentPath = tree.getSelectionPath();
            if(currentPath != null) {
                addNodes(currentPath, model, data);
            } else {
                insertNodes(tree, model, data);
            }
        }
        draggedNode = null;
        super.exportDone(source, data, action);
    }
    
    /**
     * add a number of given nodes
     * @param currentPath  the tree path currently selected
     * @param model  tree model containing the nodes
     * @param data  nodes to add
     */
    private void addNodes(TreePath currentPath, DefaultTreeModel model, Transferable data) {
        
        if (data == null) return;
        
        MutableTreeNode targetNode = (MutableTreeNode) currentPath.getLastPathComponent();
        try {
            TreePath[] movedPaths = (TreePath[]) data.getTransferData(DataFlavor.stringFlavor);
            for(int i = 0; i < movedPaths.length; i++) {
                MutableTreeNode moveNode = (MutableTreeNode) movedPaths[i].getLastPathComponent();
                if(!moveNode.equals(targetNode)) {
                    model.removeNodeFromParent(moveNode);
                    model.insertNodeInto(moveNode, targetNode, targetNode.getChildCount());
                }
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * insert a number of given nodes
     * @param tree  the tree showing the nodes
     * @param model  the model containing the nodes
     * @param data  the nodes to insert
     */
    private void insertNodes(JTree tree, DefaultTreeModel model, Transferable data) {
        Point location = ((TreeDropTarget) tree.getDropTarget()).getMostRecentDragLocation();
        TreePath path = tree.getClosestPathForLocation(location.x, location.y);
        MutableTreeNode targetNode = (MutableTreeNode) path.getLastPathComponent();
        MutableTreeNode parent = (MutableTreeNode) targetNode.getParent();
        try {
            TreePath[] movedPaths = (TreePath[]) data.getTransferData(DataFlavor.stringFlavor);
            for(int i = 0; i < movedPaths.length; i++) {
                MutableTreeNode moveNode = (MutableTreeNode) movedPaths[i].getLastPathComponent();
                if(!moveNode.equals(targetNode)) {
                    model.removeNodeFromParent(moveNode);
                    model.insertNodeInto(moveNode, parent, model.getIndexOfChild(parent, targetNode));
                }
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the type of transfer actions supported by the source.
     * This transfer handler supports moving of tree nodes so it returns MOVE.
     *
     * @return TransferHandler.MOVE
     */
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }
    
    /**
     * get a drag image from the currently dragged node (if any)
     * @param tree  the tree showing the node
     * @return  the image to draw during drag
     */
    public BufferedImage getDragImage(JTree tree) {
        BufferedImage image = null;
        try {
            if (dragPath != null) {
                Rectangle pathBounds = tree.getPathBounds(dragPath);
                TreeCellRenderer r = tree.getCellRenderer();
                DefaultTreeModel m = (DefaultTreeModel)tree.getModel();
                boolean nIsLeaf = m.isLeaf(dragPath.getLastPathComponent());
                JComponent lbl = (JComponent)r.getTreeCellRendererComponent(tree, draggedNode, false ,
                        tree.isExpanded(dragPath),nIsLeaf, 0,false);
                lbl.setBounds(pathBounds);
                image = new BufferedImage(lbl.getWidth(), lbl.getHeight(),
                        java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D graphics = image.createGraphics();
                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                lbl.setOpaque(false);
                lbl.paint(graphics);
                graphics.dispose();
            }
        } catch (RuntimeException re) {}
        return image;
    }
    
    /** remember the path to the currently dragged node here (got from createTransferable) */
    private MutableTreeNode draggedNode;

    /** remember the currently dragged node here (got from createTransferable) */
    private TreePath dragPath;
    
    
}






