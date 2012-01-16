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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.executequery.GUIUtilities;
import org.executequery.components.table.BrowserTreeCellRenderer;
import org.executequery.gui.browser.BrowserConstants;
import org.executequery.gui.browser.ConnectionsTreePanel;
import org.executequery.gui.browser.nodes.DatabaseHostNode;
import org.underworldlabs.swing.tree.DynamicTree;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class SchemaTree extends DynamicTree
                        implements TreeExpansionListener,
                                   TreeSelectionListener,
                                   MouseMotionListener,
                                   MouseListener {

    private static final int ROW_HEIGHT = 22;
    
    private boolean mouseDragging;

    private ConnectionsTreePanel panel;

    /** Creates a new instance of SchemaTree */
    public SchemaTree(DefaultMutableTreeNode root, ConnectionsTreePanel panel) {

        super(root);
        this.panel = panel;

        addTreeSelectionListener(this);
        addTreeExpansionListener(this);

//        addMouseListener(this);
//        addMouseMotionListener(this);

        DefaultTreeCellRenderer renderer = new BrowserTreeCellRenderer(loadIcons());
        setCellRenderer(renderer);

        //setCellEditor(new ConnectionTreeCellEditor(this, renderer));

        setShowsRootHandles(true);

        setDragEnabled(true);

        TransferHandler handler = new NodeMoveTransferHandler();
        setTransferHandler(handler);
        //setDropTarget(new TreeDropTarget(handler));

        //setEditable(true);

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
    
    private int lastPointY = 0;

    protected void _paintComponent(Graphics g) {

        super.paintComponent(g);

        if (mouseDragging) {

            Point mousePoint = getMousePosition();
            if (mousePoint != null) {
                TreePath draggingPath =
                        getClosestPathForLocation(mousePoint.x, mousePoint.y);

                if (draggingPath != null &&
                        draggingPath.getLastPathComponent() instanceof DatabaseHostNode) {

                    Rectangle r = getPathBounds(draggingPath);

                    int xOffsetLeft = -5;
                    int xOffsetRight = -35;

                    g.setColor(Color.RED);
                    g.fillRect(r.x + xOffsetLeft, r.y, getWidth() + xOffsetRight, 2);

                    int row = getRowForPath(draggingPath);

                    if (row > 0) {

                        if (lastPointY < mousePoint.y) {

                            row = row + 1;
                        } else {

                            row = row - 1;
                        }

                        scrollRowToVisible(row);

                        lastPointY = mousePoint.y;
                    }

                }

            }

        }

    }

    private boolean canMoveNode() {

        return (getLastPathComponent() instanceof DatabaseHostNode);
    }

    /**
     * Removes the tree listener.
     */
    public void removeTreeSelectionListener() {

        removeTreeSelectionListener(this);
    }

    /**
     * Adds the tree listener.
     */
    public void addTreeSelectionListener() {

        addTreeSelectionListener(this);
    }

    // --------------------------------------------------
    // ------- TreeSelectionListener implementation
    // --------------------------------------------------

    /**
     * Called whenever the value of the selection changes.
     * This will store the current path selection.
     *
     * @param the event that characterizes the change
     */
    public void valueChanged(TreeSelectionEvent e) {

        if (mouseDragging) {

            return;
        }

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

    // --------------------------------------------------

    public boolean axgetDragEnabled() {

        return true;//!canMoveNode();
    }

    private MutableTreeNode movingNode;

    public void mouseDragged(MouseEvent e) {

        /*
        if (!mouseDragging  && canMoveNode()) {

            movingNode = (MutableTreeNode)getLastPathComponent();

            mouseDragging = true;

            setCursor(DragSource.DefaultMoveDrop);

        }

        repaint(); */
    }

    public void mouseReleased(MouseEvent e) { /*

        if (mouseDragging) {

            try {

                repaint();
                setCursor(Cursor.getDefaultCursor());

                TreePath draggingPath =
                        getClosestPathForLocation(e.getX(), e.getY());

                if (draggingPath != null &&
                        draggingPath.getLastPathComponent() instanceof DatabaseHostNode) {

                    if (draggingPath.getLastPathComponent() == movingNode) {

                        return;
                    }

                    MutableTreeNode rootNode = getRootNode();

                    int previousRow = rootNode.getIndex(movingNode);
                    int newRow = getRowForPath(draggingPath);

                    System.out.println("prev: "+previousRow + " new: "+newRow);

                    if (newRow != previousRow) {

                        if (newRow < previousRow) {

                            newRow = newRow - 1;
                        } else {

                            newRow = newRow - 2;
                        }

                        System.out.println("row count: "+ getRowCount());

                        String state = getExpansionState(this, 0);

                        rootNode.remove(movingNode);

                        rootNode.insert(movingNode, newRow);

                        restoreExpanstionState(this, 0, state);


                        panel.nodeMoved((DatabaseHostNode)movingNode, newRow);
                        nodeStructureChanged(rootNode);

                    }

                }

            } finally {

                mouseDragging = false;
            }


        }
*/
    }


    public boolean isDescendant(TreePath path1, TreePath path2){
        int count1 = path1.getPathCount();
        int count2 = path2.getPathCount();
        if(count1<=count2)
            return false;
        while(count1!=count2){
            path1 = path1.getParentPath();
            count1--;
        }
        return path1.equals(path2);
    }

    public String getExpansionState(JTree tree, int row){
        TreePath rowPath = tree.getPathForRow(row);
        StringBuilder buf = new StringBuilder();
        int rowCount = tree.getRowCount();

        for (int i = row; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);

            if (i == row || isDescendant(path, rowPath)) {

                if (tree.isExpanded(path)) {

                    buf.append(',');
                    buf.append((i-row));
                }

            } else {

                break;
            }

        }
        return buf.toString();
    }

    public void restoreExpanstionState(JTree tree, int row, String expansionState) {

        //System.out.println("state: " + expansionState);

        StringTokenizer stok = new StringTokenizer(expansionState, ",");

        while(stok.hasMoreTokens()) {

            int token = row + Integer.parseInt(stok.nextToken());
            tree.expandRow(token);
        }
    }


    private Enumeration<TreePath> saveExpansionState() {

        return getExpandedDescendants(new TreePath(getModel().getRoot()));
    }

    private void loadExpansionState(Enumeration enumeration) {

        if (enumeration != null) {

            while (enumeration.hasMoreElements()) {

                TreePath treePath = (TreePath) enumeration.nextElement();
                expandPath(treePath);
            }

        }

    }

/*
public class TreeUtil{
 http://www.javalobby.org/java/forums/t19857.html
    // is path1 descendant of path2
    public static boolean isDescendant(TreePath path1, TreePath path2){
        int count1 = path1.getPathCount();
        int count2 = path2.getPathCount();
        if(count1<=count2)
            return false;
        while(count1!=count2){
            path1 = path1.getParentPath();
            count1--;
        }
        return path1.equals(path2);
    }

    public static String getExpansionState(JTree tree, int row){
        TreePath rowPath = tree.getPathForRow(row);
        StringBuilder buf = new StringBuilder();
        int rowCount = tree.getRowCount();
        for(int i=row; i<rowCount; i++){
            TreePath path = tree.getPathForRow(i);
            if(i==row || isDescendant(path, rowPath)){
                if(tree.isExpanded(path)) {
                    buf.append(',');
                    buf.append((i-row));
                    //buf.append(","+String.valueOf(i-row));
            }else
                break;
        }
        return buf.toString();
    }

    public static void restoreExpanstionState(JTree tree, int row, String expansionState){
        StringTokenizer stok = new StringTokenizer(expansionState, ",");
        while(stok.hasMoreTokens()){
            int token = row + Integer.parseInt(stok.nextToken());
            tree.expandRow(token);
        }
    }
}


 */

    public void mouseMoved(MouseEvent e) {

        // do nothing
    }

    public void mouseEntered(MouseEvent e) {

        // do nothing
    }

    public void mouseExited(MouseEvent e) {

        // do nothing
    }

    public void mouseClicked(MouseEvent e) {

        // do nothing
    }

    public void mousePressed(MouseEvent e) {

        //System.out.println("A");

        JComponent c = (JComponent)e.getSource();
        TransferHandler handler = c.getTransferHandler();
        handler.exportAsDrag(c, e, TransferHandler.COPY);

// do nothing
    }

}






