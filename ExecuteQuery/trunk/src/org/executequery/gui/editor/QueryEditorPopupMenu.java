/*
 * QueryEditorPopupMenu.java
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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.executequery.Constants;
import org.executequery.UserPreferencesManager;
import org.executequery.sql.QueryDelegate;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.underworldlabs.swing.menu.MenuItemFactory;
import org.underworldlabs.util.SystemProperties;

public class QueryEditorPopupMenu extends JPopupMenu
                                  implements MouseListener {

    private final QueryDelegate queryDelegate;

    public QueryEditorPopupMenu(QueryDelegate queryDelegate) {

        this.queryDelegate = queryDelegate;

        init();
    }

    private void init() {

        add(createCutMenuItem());
        add(createCopyMenuItem());
        add(createPasteMenuItem());

        addSeparator();

        add(createExecuteMenuItem());
        add(createPartialExecuteMenuItem());
        add(createExecuteSelectionMenuItem());
        add(createExecuteBlockMenuItem());
        add(createStopMenuItem());

        addSeparator();

        add(createCommitMenuItem());
        add(createRollbackMenuItem());

        addSeparator();

        add(createFormatSqlMenuItem());
        add(createDuplicateRowUpMenuItem());
        add(createDuplicateRowDownMenuItem());
        add(createMoveRowUpMenuItem());
        add(createMoveRowDownMenuItem());
        addSeparator();

        add(createClearOutputMenuItem());
        add(createAddToUserDefinedKeywordsMenuItem());
        add(createRemoveCommentsForQueryMenuItem());
        add(createRecycleResultSetTabMenuItem());

        addSeparator();
        add(createOptionsMenuItem());
        addSeparator();
        add(createHelpMenuItem());
    }

    public void statementExecuting() {

        setExecuteActionButtonsEnabled(false);

        setExecutingButtonsEnabled(true);
    }

    public void statementFinished() {

        setExecuteActionButtonsEnabled(true);

        setExecutingButtonsEnabled(false);
    }

    public void setCommitMode(boolean autoCommit) {

        setTransactionButtonsEnabled(!autoCommit);
    }

    public void execute(ActionEvent e) {

        queryDelegate.executeQuery(null);
    }

    public void executeAsBlock(ActionEvent e) {

        queryDelegate.executeQuery(null, true);
    }

    public void cancelQuery(ActionEvent e) {

        queryDelegate.interrupt();
    }

    public void recycleResultSetTabs(ActionEvent e) {

        JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();

        SystemProperties.setBooleanProperty(
                Constants.USER_PROPERTIES_KEY, "editor.results.tabs.single", item.isSelected());

        UserPreferencesManager.fireUserPreferencesChanged();
    }

    public void removeCommentsPriorToQueryExecution(ActionEvent e) {

        JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();

        SystemProperties.setBooleanProperty(
                Constants.USER_PROPERTIES_KEY, "editor.execute.remove.comments", item.isSelected());

        UserPreferencesManager.fireUserPreferencesChanged();
    }

    public void commit(ActionEvent e) {

        queryDelegate.commit();
    }

    public void rollback(ActionEvent e) {

        queryDelegate.rollback();
    }

    public void mousePressed(MouseEvent e) {

        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {

        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {

            show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


    public void removeAll() {

        executingButtons().clear();
        executeActionButtons().clear();
        transactionButtons().clear();

        super.removeAll();
    }

    private JMenuItem createHelpMenuItem() {

        JMenuItem menuItem = createExecuteActionMenuItem("help-command", "Help");
        menuItem.setActionCommand("qedit");
        return menuItem;
    }

    private JMenuItem createOptionsMenuItem() {

        return createExecuteActionMenuItem("customise-query-editor-command", "Preferences...");
    }

    private JMenuItem createClearOutputMenuItem() {

        return createExecuteActionMenuItem("clear-editor-output-command", "Clear Output Log");
    }

    private JMenuItem createRecycleResultSetTabMenuItem() {
        JCheckBoxMenuItem menuItem = MenuItemFactory.createCheckBoxMenuItem(action());
        menuItem.setText("Use Single Resut Set Tab");
        menuItem.setSelected(UserPreferencesManager.isResultSetTabSingle());
        menuItem.setActionCommand("recycleResultSetTabs");
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createRemoveCommentsForQueryMenuItem() {
        JCheckBoxMenuItem menuItem = MenuItemFactory.createCheckBoxMenuItem(action());
        menuItem.setText("Remove comments for execution");
        menuItem.setSelected(SystemProperties.getBooleanProperty(
                Constants.USER_PROPERTIES_KEY, "editor.execute.remove.comments"));
        menuItem.setActionCommand("removeCommentsPriorToQueryExecution");
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createFormatSqlMenuItem() {

        return createExecuteActionMenuItem("editor-format-sql-command", null);
    }

    private JMenuItem createMoveRowUpMenuItem() {

        return createExecuteActionMenuItem("move-row-up-command", null);
    }

    private JMenuItem createMoveRowDownMenuItem() {

        return createExecuteActionMenuItem("move-row-down-command", null);
    }

    private JMenuItem createDuplicateRowUpMenuItem() {

        return createExecuteActionMenuItem("duplicate-row-up-command", null);
    }

    private JMenuItem createDuplicateRowDownMenuItem() {

        return createExecuteActionMenuItem("duplicate-row-down-command", null);
    }

    private JMenuItem createAddToUserDefinedKeywordsMenuItem() {

        return createExecuteActionMenuItem("editor-add-user-keyword", null);
    }

    private JMenuItem createRollbackMenuItem() {
        JMenuItem menuItem = MenuItemFactory.createMenuItem(action());
        menuItem.setText("Rollback");
        menuItem.setActionCommand("rollback");
        executeActionButtons().add(menuItem);
        transactionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createCommitMenuItem() {
        JMenuItem menuItem = MenuItemFactory.createMenuItem(action());
        menuItem.setText("Commit");
        menuItem.setActionCommand("commit");
        executeActionButtons().add(menuItem);
        transactionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createStopMenuItem() {
        JMenuItem menuItem = MenuItemFactory.createMenuItem(action());
        menuItem.setText("Cancel Query");
        menuItem.setActionCommand("cancelQuery");
        menuItem.setEnabled(false);
        executingButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createExecuteBlockMenuItem() {
        JMenuItem menuItem = MenuItemFactory.createMenuItem(action());
        menuItem.setActionCommand("executeAsBlock");
        menuItem.setText("Execute as Single Statement");
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createExecuteSelectionMenuItem() {

        return createExecuteActionMenuItem("execute-selection-command", "Execute Selected Query Text");
    }

    private JMenuItem createPartialExecuteMenuItem() {

        return createExecuteActionMenuItem("execute-at-cursor-command", "Execute Query at Cursor");
    }

    private JMenuItem createExecuteMenuItem() {
        JMenuItem menuItem = MenuItemFactory.createMenuItem(action());
        menuItem.setText("Execute");
        menuItem.setActionCommand("execute");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createPasteMenuItem() {

        return createMenuItem("paste-command", "Paste");
    }

    private JMenuItem createCopyMenuItem() {

        return createMenuItem("copy-command", "Copy");
    }

    private JMenuItem createCutMenuItem() {

        return createMenuItem("cut-command", "Cut");
    }

    private JMenuItem createExecuteActionMenuItem(String actionName, String text) {

        JMenuItem menuItem = createMenuItem(actionName, text);
        executeActionButtons().add(menuItem);

        return menuItem;
    }

    private JMenuItem createMenuItem(String actionName, String text) {

        JMenuItem menuItem = MenuItemFactory.createMenuItem(ActionBuilder.get(actionName));
        menuItem.setIcon(null);

        if (StringUtils.isNotBlank(text)) {

            menuItem.setText(text);
        }

        return menuItem;
    }



    private void setTransactionButtonsEnabled(boolean enable) {

        for (JMenuItem menuItem : transactionButtons()) {

            menuItem.setEnabled(enable);
        }

    }

    private void setExecutingButtonsEnabled(boolean enable) {

        for (JMenuItem menuItem : executingButtons()) {

            menuItem.setEnabled(enable);
        }

    }

    private void setExecuteActionButtonsEnabled(boolean enable) {

        for (JMenuItem menuItem : executeActionButtons()) {

            menuItem.setEnabled(enable);
        }

    }

    private List<JMenuItem> executeActionButtons() {

        if (executeActionButtons == null) {

            executeActionButtons = new ArrayList<JMenuItem>();
        }

        return executeActionButtons;
    }

    private List<JMenuItem> executingButtons() {

        if (executingButtons == null) {

            executingButtons = new ArrayList<JMenuItem>();
        }

        return executingButtons;
    }

    private List<JMenuItem> transactionButtons() {

        if (transactionButtons == null) {

            transactionButtons = new ArrayList<JMenuItem>();
        }

        return transactionButtons;
    }

    private Action action() {

        if (action == null) {

            action = new ReflectiveAction(this);
        }

        return action;
    }

    private ReflectiveAction action;

    private List<JMenuItem> executeActionButtons;

    private List<JMenuItem> executingButtons;

    private List<JMenuItem> transactionButtons;

}

