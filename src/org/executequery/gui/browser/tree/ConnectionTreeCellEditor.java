/*
 * ConnectionTreeCellEditor.java
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

import java.util.EventObject;

import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.executequery.gui.browser.nodes.DatabaseHostNode;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Rev:$
 * @date     $Date:$
 */
public class ConnectionTreeCellEditor extends DefaultTreeCellEditor {
    
    private final SchemaTree schemaTree;

    public ConnectionTreeCellEditor(SchemaTree tree, DefaultTreeCellRenderer renderer) {
        
        super(tree, renderer);
        schemaTree = tree;
    }

    public boolean isCellEditable(EventObject event) {
        
        if (!(tree.getSelectionPath().getLastPathComponent() instanceof DatabaseHostNode)) {

            return false;
        }

        return true;        
    }
    
    public Object getCellEditorValue() {

        Object value = super.getCellEditorValue();
        Object lastPathComponent = tree.getSelectionPath().getLastPathComponent();
        if (lastPathComponent instanceof DatabaseHostNode) {

            schemaTree.connectionNameChanged((String)value);
        }

        return value;
    }
    
}
