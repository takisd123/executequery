/*
 * QueryEditorPopupMenu.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
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

import org.executequery.Constants;
import org.executequery.UserPreferencesManager;
import org.executequery.sql.QueryDelegate;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.underworldlabs.swing.actions.ReflectiveAction;
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
        add(createClearOutputMenuItem());
        add(createRecycleResultSetTabMenuItem());
        
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
        JMenuItem menuItem = new JMenuItem(ActionBuilder.get("help-command"));
        menuItem.setIcon(null);
        menuItem.setActionCommand("qedit");
        menuItem.setText("Help");
        return menuItem;
    }

    private JMenuItem createClearOutputMenuItem() {
        JMenuItem menuItem = new JMenuItem(
                ActionBuilder.get("clear-editor-output-command"));
        menuItem.setIcon(null);
        menuItem.setText("Clear Output Log");
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createRecycleResultSetTabMenuItem() {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(action());
        menuItem.setText("Use Single Resut Set Tab");
        menuItem.setSelected(UserPreferencesManager.isResultSetTabSingle());
        menuItem.setActionCommand("recycleResultSetTabs");
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createFormatSqlMenuItem() {
        JMenuItem menuItem = new JMenuItem(
                ActionBuilder.get("editor-format-sql-command"));
        menuItem.setIcon(null);
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createRollbackMenuItem() {
        JMenuItem menuItem = new JMenuItem(action());
        menuItem.setText("Rollback");
        menuItem.setActionCommand("rollback");
        executeActionButtons().add(menuItem);
        transactionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createCommitMenuItem() {
        JMenuItem menuItem = new JMenuItem(action());
        menuItem.setText("Commit");
        menuItem.setActionCommand("commit");
        executeActionButtons().add(menuItem);
        transactionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createStopMenuItem() {
        JMenuItem menuItem = new JMenuItem(action());
        menuItem.setText("Cancel Query");
        menuItem.setActionCommand("cancelQuery");
        menuItem.setEnabled(false);
        executingButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createExecuteBlockMenuItem() {
        JMenuItem menuItem = new JMenuItem(action());
        menuItem.setActionCommand("executeAsBlock");
        menuItem.setText("Execute as Single Statement");
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createExecuteSelectionMenuItem() {
        JMenuItem menuItem = new JMenuItem(
                ActionBuilder.get("execute-selection-command"));
        menuItem.setText("Execute Selected Query Text");
        menuItem.setIcon(null);
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createPartialExecuteMenuItem() {
        JMenuItem menuItem = new JMenuItem(
                ActionBuilder.get("execute-at-cursor-command"));
        menuItem.setText("Execute Query at Cursor");
        menuItem.setIcon(null);
        executeActionButtons().add(menuItem);
        return menuItem;
    }

    private JMenuItem createExecuteMenuItem() {
        JMenuItem menuItem = new JMenuItem(action());
        menuItem.setText("Execute");
        menuItem.setActionCommand("execute");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        executeActionButtons().add(menuItem);
        return menuItem;
    }
    
    private JMenuItem createPasteMenuItem() {
        JMenuItem paste = new JMenuItem(ActionBuilder.get("paste-command"));
        paste.setText("Paste");
        paste.setIcon(null);
        return paste;
    }

    private JMenuItem createCopyMenuItem() {
        JMenuItem copy = new JMenuItem(ActionBuilder.get("copy-command"));
        copy.setText("Copy");
        copy.setIcon(null);
        return copy;
    }

    private JMenuItem createCutMenuItem() {
        JMenuItem cut = new JMenuItem(ActionBuilder.get("cut-command"));
        cut.setText("Cut");
        cut.setIcon(null);
        return cut;
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
