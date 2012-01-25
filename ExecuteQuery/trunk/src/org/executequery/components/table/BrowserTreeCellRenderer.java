/*
 * BrowserTreeCellRenderer.java
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

package org.executequery.components.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.executequery.Constants;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databaseobjects.DatabaseColumn;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.gui.browser.BrowserConstants;
import org.executequery.gui.browser.nodes.DatabaseHostNode;
import org.executequery.gui.browser.nodes.DatabaseObjectNode;
import org.underworldlabs.swing.plaf.UIUtils;
import org.underworldlabs.swing.tree.AbstractTreeCellRenderer;

/**
 * Tree cell renderer or the database browser.
 * 
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class BrowserTreeCellRenderer extends AbstractTreeCellRenderer {
    
    /** Icon collection for nodes */
    private Map<String, Icon> icons;
    
    private Color textForeground;
    private Color selectedTextForeground;

    /**
     * Constructs a new instance and initialises any variables
     */
    public BrowserTreeCellRenderer(Map<String, Icon> icons) {

        this.icons = icons;

        textForeground = UIManager.getColor("Tree.textForeground");
        selectedTextForeground = UIManager.getColor("Tree.selectionForeground");

        if (UIUtils.isGtkLookAndFeel()) {

            // has default black border on selection - ugly and wrong!
            setBorderSelectionColor(null);
        }

        sb = new StringBuilder();
    }
    
    /**
     * Sets the value of the current tree cell to value. If 
     * selected is true, the cell will be drawn as if selected. 
     * If expanded is true the node is currently expanded and if 
     * leaf is true the node represets a leaf and if hasFocus 
     * is true the node currently has focus. tree is the JTree 
     * the receiver is being configured for. Returns the Component 
     * that the renderer uses to draw the value.
     *
     * @return the Component that the renderer uses to draw the value
     */
    public Component getTreeCellRendererComponent(JTree tree, 
                                                  Object value,
                                                  boolean isSelected, 
                                                  boolean isExpanded,
                                                  boolean isLeaf, 
                                                  int row, 
                                                  boolean hasFocus) {
        
        this.hasFocus = hasFocus;

        DefaultMutableTreeNode child = (DefaultMutableTreeNode)value;
        
        DatabaseObjectNode node = (DatabaseObjectNode)child;
        int type = node.getType();
        String label = node.getDisplayName();

        switch (type) {

            case NamedObject.ROOT:
                setIcon(icons.get(
                        BrowserConstants.CONNECTIONS_IMAGE));
                break;

            case NamedObject.HOST:
                DatabaseHostNode _node = (DatabaseHostNode)node;

                if (_node.isConnected()) {
                    setIcon(icons.get(
                            BrowserConstants.HOST_CONNECTED_IMAGE));
                } else {
                    setIcon(icons.get(
                            BrowserConstants.HOST_NOT_CONNECTED_IMAGE));
                }
                 
                break;
                
            case NamedObject.CATALOG:
                setIcon(icons.get(BrowserConstants.CATALOG_IMAGE));
                break;
                
            case NamedObject.SCHEMA:
                setIcon(icons.get(BrowserConstants.SCHEMA_IMAGE));
                break;
            
            case NamedObject.META_TAG:
                setIcon(icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                break;
                
            case NamedObject.SYSTEM_FUNCTION:
                setIcon(icons.get(BrowserConstants.SYSTEM_FUNCTIONS_IMAGE));
                break;
                
            case NamedObject.FUNCTION:
                setIcon(icons.get(BrowserConstants.FUNCTIONS_IMAGE));
                break;
                
            case NamedObject.INDEX:
                setIcon(icons.get(BrowserConstants.INDEXES_IMAGE));
                break;
                
            case NamedObject.PROCEDURE:
                setIcon(icons.get(BrowserConstants.PROCEDURES_IMAGE));
                break;
                
            case NamedObject.SEQUENCE:
                setIcon(icons.get(BrowserConstants.SEQUENCES_IMAGE));
                break;
                
            case NamedObject.SYNONYM:
                setIcon(icons.get(BrowserConstants.SYNONYMS_IMAGE));
                break;

            case NamedObject.VIEW:
                setIcon(icons.get(BrowserConstants.VIEWS_IMAGE));
                break;

            case NamedObject.SYSTEM_TABLE:
                setIcon(icons.get(BrowserConstants.SYSTEM_TABLES_IMAGE));
                break;
                
            case NamedObject.TABLE:
                setIcon(icons.get(BrowserConstants.TABLES_IMAGE));
                break;

            case NamedObject.TABLE_COLUMN:
                
                DatabaseColumn databaseColumn = (DatabaseColumn) node.getDatabaseObject();

                if (databaseColumn.isPrimaryKey()) {

                    setIcon(icons.get(BrowserConstants.PRIMARY_COLUMNS_IMAGE));
                    
                } else if (databaseColumn.isForeignKey()) {
                    
                    setIcon(icons.get(BrowserConstants.FOREIGN_COLUMNS_IMAGE));

                } else {

                    setIcon(icons.get(BrowserConstants.COLUMNS_IMAGE));
                }
                
                break;

            case NamedObject.SYSTEM_DATE_TIME_FUNCTIONS:
            case NamedObject.SYSTEM_NUMERIC_FUNCTIONS:
            case NamedObject.SYSTEM_STRING_FUNCTIONS:
                setIcon(icons.get(BrowserConstants.SYSTEM_FUNCTIONS_IMAGE));
                break;
                
            default:
                setIcon(icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                break;
                
        }
        
        setText(label);

        if (type == BrowserConstants.HOST_NODE) {

            DatabaseConnection connection = 
                    ((DatabaseHost)node.getDatabaseObject()).getDatabaseConnection();
            setToolTipText(buildToolTip(connection));

        } else {

            setToolTipText(label);
        }

        selected = isSelected;

        if(!selected) {

            setForeground(textForeground);

        } else {
            
            setForeground(selectedTextForeground);
        }

        return this;
    }

    /** tool tip string buffer */
    private StringBuilder sb;
    
    /**
     * Builds a HTML tool tip describing this tree connection.
     * 
     * @param the connection object
     */
    private String buildToolTip(DatabaseConnection connection) {
        // reset
        sb.setLength(0);
        
        // build the html display
        sb.append("<html>");
        sb.append(Constants.TABLE_TAG_START);
        sb.append("<tr><td><b>");
        sb.append(connection.getName());
        sb.append("</b></td></tr>");
        sb.append(Constants.TABLE_TAG_END);
        sb.append("<hr>");
        sb.append(Constants.TABLE_TAG_START);
        sb.append("<tr><td>Host:</td><td width='30'></td><td>");
        sb.append(connection.getHost());
        sb.append("</td></tr><td>Data Source:</td><td></td><td>");
        sb.append(connection.getSourceName());
        sb.append("</td></tr><td>User:</td><td></td><td>");
        sb.append(connection.getUserName());
        sb.append("</td></tr><td>Driver:</td><td></td><td>");
        sb.append(connection.getDriverName());
        sb.append("</td></tr>");
        sb.append(Constants.TABLE_TAG_END);
        sb.append("</html>");

        return sb.toString();
    }

    @Override
    public Icon getClosedIcon() {

        return getIcon();
    }
    
    @Override
    public Icon getOpenIcon() {

        return getIcon();
    }
    
    @Override
    public Icon getLeafIcon() {

        return getIcon();
    }
    
}


