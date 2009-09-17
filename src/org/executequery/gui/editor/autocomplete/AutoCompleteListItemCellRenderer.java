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
        
        sql92Keyword = GUIUtilities.loadIcon("Sql92.gif", true);
        userDefinedKeyword = GUIUtilities.loadIcon("User16.gif", true);
        databaseSpecificKeyword = GUIUtilities.loadIcon("DatabaseKeyword16.gif", true);
        databaseTable = GUIUtilities.loadIcon("PlainTable16.gif", true);
        databaseTableColumn = GUIUtilities.loadIcon("TableColumn16.gif", true);
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
