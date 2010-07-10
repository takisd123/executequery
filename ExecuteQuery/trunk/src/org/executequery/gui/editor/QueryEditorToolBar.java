/*
 * QueryEditorToolBar.java
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

package org.executequery.gui.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.repository.QueryBookmark;
import org.executequery.repository.QueryBookmarks;
import org.underworldlabs.swing.PopupMenuButton;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.underworldlabs.swing.menu.MenuItemFactory;
import org.underworldlabs.swing.toolbar.PanelToolBar;

/** 
 * The Query Editor's tool bar.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
class QueryEditorToolBar extends PanelToolBar {
    
    private static final String QUERY_BOOKMARKS = "query-bookmarks";

    private static final String QUERY_SHORTCUTS = "manage-shortcuts-command";

    private static final String EDITOR_HELP_COMMAND = "editor-help-command";

    private static final String FORMAT_SQL_COMMAND = "editor-format-sql-command";
    
//    private static final String REMOVE_COMMENT_LINES_COMMAND = "remove-comment-lines-command";

    private static final String COMMENT_LINES_COMMAND = "comment-lines-command";

    private static final String SHIFT_TEXT_RIGHT_COMMAND = "shift-text-right-command";

    private static final String SHIFT_TEXT_LEFT_COMMAND = "shift-text-left-command";

    private static final String TOGGLE_EDITOR_OUTPUT_COMMAND = "toggle-editor-output-command";

    private static final String EDITOR_EXPORT_COMMAND = "editor-export-command";

    private static final String EDITOR_RS_METADATA_COMMAND = "editor-rs-metadata-command";

    private static final String EDITOR_CONN_CHANGE_COMMAND = "editor-conn-change-command";

    private static final String TOGGLE_AUTOCOMMIT_COMMAND = "toggle-autocommit-command";

    private static final String ROLLBACK_COMMAND = "rollback-command";

    private static final String COMMIT_COMMAND = "commit-command";

    private static final String EDITOR_NEXT_COMMAND = "editor-next-command";

    private static final String EDITOR_PREVIOUS_COMMAND = "editor-previous-command";

    private static final String SQL_HISTORY_COMMAND = "sql-history-command";

    private static final String CLEAR_EDITOR_OUTPUT_COMMAND = "clear-editor-output-command";

    private static final String EDITOR_STOP_COMMAND = "editor-stop-command";

    private static final String EXECUTE_SELECTION_COMMAND = "execute-selection-command";

    private static final String EXECUTE_AT_CURSOR_COMMAND = "execute-at-cursor-command";

    private static final String EXECUTE_COMMAND = "execute-command";

    public static final String NAME = "Query Editor Tool Bar";
    
    /** button access map */
    private Map<String, RolloverButton> buttons;

    private final ActionMap queryEditorActionMap;

    private final InputMap queryEditorInputMap;
    
    public QueryEditorToolBar(ActionMap queryEditorActionMap, InputMap queryEditorInputMap) {

        this.queryEditorActionMap = queryEditorActionMap;
        this.queryEditorInputMap = queryEditorInputMap;

        try {
            
            init();

        } catch (Exception e) {

            e.printStackTrace();
        }   

    }
    
    /** 
     * Initializes the state of this instance. 
     */
    private void init() throws Exception {
        buttons = new HashMap<String,RolloverButton>();

        addButton(createButton(EXECUTE_COMMAND, 
                     "Execute the contents of the query editor"));

        addButton(createButton(EXECUTE_AT_CURSOR_COMMAND, 
                     "Execute query at cursor"));
        
        addButton(createButton(EXECUTE_SELECTION_COMMAND, 
                     "Execute the current text selection"));
        
        addButton(createButton(EDITOR_STOP_COMMAND, 
                     "Cancel Current Statement"));
        
        addSeparator();

        addButton(createButton(CLEAR_EDITOR_OUTPUT_COMMAND, 
                     "Clear the editor's output log panel"));

        addButton(createButton(SQL_HISTORY_COMMAND, "SQL command history"));

        addButton(createQueryBookmarkButton());

        addButton(createButton(QUERY_SHORTCUTS, "SQL shortcuts"));

        addButton(createButton(EDITOR_PREVIOUS_COMMAND, "Previous Statement"));

        addButton(createButton(EDITOR_NEXT_COMMAND, "Next Statement"));

        addSeparator();
        
        addButton(createButton(COMMIT_COMMAND, 
                     "Commit all changes since last commit/rollback"));
        
        addButton(createButton(ROLLBACK_COMMAND, 
                     "Rollback all changes since last commit/rollback"));
  
        addButton(createButton(TOGGLE_AUTOCOMMIT_COMMAND, 
                     "Toggle auto-commit on/off"));

        addButton(createButton(EDITOR_CONN_CHANGE_COMMAND, 
                     "Closes the editor's connection and retrieves another from the pool"));

        addSeparator();

        addButton(createButton(EDITOR_RS_METADATA_COMMAND, 
                     "Display this result set's meta data"));

        addButton(createButton(EDITOR_EXPORT_COMMAND, 
                     "Export the selected result set to file"));

        addSeparator();

        addButton(createButton(TOGGLE_EDITOR_OUTPUT_COMMAND, 
                     "Show/hide the output pane"));

        addSeparator();
        
        addButton(createButton(SHIFT_TEXT_LEFT_COMMAND, 
                     "Shift line/selection left"));

        addButton(createButton(SHIFT_TEXT_RIGHT_COMMAND, 
                     "Shift line/selection right"));
        
        addSeparator();
        
        addButton(createButton(COMMENT_LINES_COMMAND, "Comment/Uncomment"));

//        addButton(createButton(REMOVE_COMMENT_LINES_COMMAND, 
//                     "Uncomment"));

        addButton(createButton(FORMAT_SQL_COMMAND, "Format SQL"));
        
        addSeparator();
        
        addButton(createButton(EDITOR_HELP_COMMAND, "Query Editor help"));
        
    }
    
    private JButton createQueryBookmarkButton() {

        PopupMenuButton button = new PopupMenuButton(
                GUIUtilities.loadIcon("Bookmarks16.png"), "Query Bookmarks");
        button.setText(null);

        // TODO: configurable shortcut keys for bookmark actions
        
        String actionMapKey = "bookmarks-button";
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control B");

        button.setKeyStroke(keyStroke);
        
        queryEditorActionMap.put(actionMapKey, button.getAction());
        queryEditorInputMap.put(keyStroke, actionMapKey);

        actionMapKey = "add-bookmark-command";
        keyStroke = KeyStroke.getKeyStroke("control shift B");
        
        queryEditorActionMap.put(actionMapKey, ActionBuilder.get(actionMapKey));
        queryEditorInputMap.put(keyStroke, actionMapKey);
        
        createQueryBookmarkMenuItems(button);

        buttons.put(QUERY_BOOKMARKS, button);
        
        return button;
    }

    /**
     * Enables/disables all tool bar buttons as specified.
     *
     * @param true | false
     */
    public void enableAllButtons(boolean enable) {
        
        for (String key : buttons.keySet()) {

            buttons.get(key).setEnabled(enable);
        }

    }

    /**
     * Enables/disables the button with the specified action ID.
     *
     * @param actionId - the action ID string
     * @param enable true | false
     */
    public void setButtonEnabled(String actionId, boolean enable) {
        RolloverButton button = buttons.get(actionId);
        if (button != null) {
            button.setEnabled(enable);
        }
    }
    
    public void setMetaDataButtonEnabled(boolean enable) {
        buttons.get(EDITOR_RS_METADATA_COMMAND).setEnabled(enable);
    }
    
    public void setPreviousButtonEnabled(boolean enable) {
        buttons.get(EDITOR_PREVIOUS_COMMAND).setEnabled(enable);
    }

    public void setNextButtonEnabled(boolean enable) {
        buttons.get(EDITOR_NEXT_COMMAND).setEnabled(enable);
    }

    public void setStopButtonEnabled(boolean enable) {
        buttons.get(EDITOR_STOP_COMMAND).setEnabled(enable);
        buttons.get(EXECUTE_COMMAND).setEnabled(!enable);
        buttons.get(EXECUTE_AT_CURSOR_COMMAND).setEnabled(!enable);
        buttons.get(EXECUTE_SELECTION_COMMAND).setEnabled(!enable);
    }
    
    public void setCommitsEnabled(boolean enable) {
        buttons.get(COMMIT_COMMAND).setEnabled(enable);
        buttons.get(ROLLBACK_COMMAND).setEnabled(enable);
    }
    
    public void setExportButtonEnabled(boolean enable) {
        buttons.get(EDITOR_EXPORT_COMMAND).setEnabled(enable);
    }
    
    public String toString() {
        return NAME;
    }
    
    protected void reloadBookmarkItems() {
        
        PopupMenuButton button = (PopupMenuButton)buttons.get(QUERY_BOOKMARKS);
        button.removeMenuItems();

        createQueryBookmarkMenuItems(button);
    }

    private void createQueryBookmarkMenuItems(PopupMenuButton button) {

        button.addMenuItem(createMenuItemFromCommand("add-bookmark-command"));
        button.addMenuItem(createMenuItemFromCommand("manage-bookmarks-command"));
        
        if (QueryBookmarks.getInstance().hasQueryBookmarks()) {
            
            button.addSeparator();

            List<QueryBookmark> bookmarks = 
                QueryBookmarks.getInstance().getQueryBookmarks();

            for (QueryBookmark bookmark : bookmarks) {

                JMenuItem menuItem = createMenuItemFromCommand("select-bookmark-command");
                menuItem.setActionCommand(bookmark.getName());
                menuItem.setText(bookmark.getName());

                button.addMenuItem(menuItem);
            }

        }
    }

    private JMenuItem createMenuItemFromCommand(String actionId) {
        return MenuItemFactory.createMenuItem(ActionBuilder.get(actionId));
    }

    /**
     * Creates a button with the action specified by the action name
     * and with the specified tool tip text.
     */
    private RolloverButton createButton(String actionId, String toolTipText) {
        RolloverButton button = 
                new RolloverButton(ActionBuilder.get(actionId), toolTipText);
        button.setText(Constants.EMPTY);
        buttons.put(actionId, button);
        return button;
    }
    
}




