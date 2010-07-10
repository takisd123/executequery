/*
 * AutoCompleteListItemCellRenderer.java
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

package org.executequery.gui.editor.autocomplete;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.executequery.GUIUtilities;

public class AutoCompleteListItemCellRenderer extends DefaultListCellRenderer {

    private static final int TEXT_ICON_GAP = 10;

    private static final Icon sql92Keyword;
    private static final Icon userDefinedKeyword;
    private static final Icon databaseSpecificKeyword;
    private static final Icon databaseTable;
    private static final Icon databaseTableColumn;
    
    static {
        
        sql92Keyword = GUIUtilities.loadIcon("Sql92.png", true);
        userDefinedKeyword = GUIUtilities.loadIcon("User16.png", true);
        databaseSpecificKeyword = GUIUtilities.loadIcon("DatabaseKeyword16.png", true);
        databaseTable = GUIUtilities.loadIcon("PlainTable16.png", true);
        databaseTableColumn = GUIUtilities.loadIcon("TableColumn16.png", true);
    }
    

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        JLabel listLabel = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        listLabel.setIconTextGap(TEXT_ICON_GAP);
        
        AutoCompleteListItem item = (AutoCompleteListItem) value;
        
        switch (item.getType()) {
        
            case SQL92_KEYWORD:
                setIcon(sql92Keyword);
                break;

            case DATABASE_DEFINED_KEYWORD:
                setIcon(databaseSpecificKeyword);
                break;
        
            case USER_DEFINED_KEYWORD:
                setIcon(userDefinedKeyword);
                break;
        
            case DATABASE_TABLE:
                setIcon(databaseTable);
                break;
        
            case DATABASE_TABLE_COLUMN:
                setIcon(databaseTableColumn);
                break;

            case NOTHING_PROPOSED:
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                setBorder(noFocusBorder);
                break;

        }
        
        return listLabel;
    }
    
}

