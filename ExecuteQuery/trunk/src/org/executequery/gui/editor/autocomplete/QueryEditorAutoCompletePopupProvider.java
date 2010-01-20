/*
 * AutocompletePopupAdapter.java
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

package org.executequery.gui.editor.autocomplete;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseObjectFactory;
import org.executequery.databaseobjects.impl.DatabaseObjectFactoryImpl;
import org.executequery.gui.editor.ConnectionChangeListener;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.log.Log;
import org.underworldlabs.swing.util.SwingWorker;

public class QueryEditorAutoCompletePopupProvider 
    implements AutoCompletePopupProvider, AutoCompletePopupListener, 
        CaretListener, ConnectionChangeListener, FocusListener {

    private static final KeyStroke KEY_STROKE_ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    
    private static final KeyStroke KEY_STROKE_DOWN = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
    
    private static final KeyStroke KEY_STROKE_UP = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);

    private static final KeyStroke KEY_STROKE_PAGE_DOWN = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0);
    
    private static final KeyStroke KEY_STROKE_PAGE_UP = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0);

    private static final KeyStroke KEY_STROKE_TAB = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
    
    private static final String LIST_FOCUS_ACTION_KEY = "focusActionKey";
    
    private static final String LIST_SCROLL_ACTION_KEY_DOWN = "scrollActionKeyDown";
    
    private static final String LIST_SCROLL_ACTION_KEY_UP = "scrollActionKeyUp";
    
    private static final String LIST_SCROLL_ACTION_KEY_PAGE_DOWN = "scrollActionKeyPageDown";
    
    private static final String LIST_SCROLL_ACTION_KEY_PAGE_UP = "scrollActionKeyPageUp";
    
    private static final String LIST_SELECTION_ACTION_KEY = "selectionActionKey";
    
    private final QueryEditor queryEditor;
    
    private AutoCompleteSelectionsFactory selectionsBuilder;
    
    private AutoCompletePopupAction autoCompletePopupAction;
    
    private QueryEditorAutoCompletePopupPanel autoCompletePopup;

    private DatabaseObjectFactory databaseObjectFactory;
    
    private DatabaseHost databaseHost;
    
    private List<AutoCompleteListItem> autoCompleteListItems;
    
    public QueryEditorAutoCompletePopupProvider(QueryEditor queryEditor) {
        
        super();
        this.queryEditor = queryEditor;
        
        selectionsBuilder = new AutoCompleteSelectionsFactory();
        databaseObjectFactory = new DatabaseObjectFactoryImpl();

        queryEditor.addConnectionChangeListener(this);
        queryEditorTextComponent().addFocusListener(this);
    }

    public Action getPopupAction() {

        if (autoCompletePopupAction == null) {
        
            autoCompletePopupAction = new AutoCompletePopupAction(this);
        }

        return autoCompletePopupAction;
    }
    
    public void firePopupTrigger() {

        try {

            final JTextComponent textComponent = queryEditorTextComponent();

            Caret caret = textComponent.getCaret();
            final Rectangle caretCoords = textComponent.modelToView(caret.getDot());

            addFocusActions();
            captureAndResetListValues();

            ((JPopupMenu) popupMenu()).show(textComponent, 
                    caretCoords.x, caretCoords.y + caretCoords.height);

            textComponent.requestFocus();

        } catch (BadLocationException e) {

            if (Log.isDebugEnabled()) {
            
                Log.debug("Error on caret coordinates", e);
            }

        }
        
    }

    private void captureAndResetListValues() {

        String wordAtCursor = queryEditor.getWordAtCursor();

        ((QueryEditorAutoCompletePopupPanel) popupMenu()).
            resetValues(itemsStartingWith(wordAtCursor));
    }

    private JComponent popupMenu() {

        if (autoCompletePopup == null) {
            
            autoCompletePopup = new QueryEditorAutoCompletePopupPanel();
            autoCompletePopup.addAutoCompletePopupListener(this);
            
            autoCompletePopup.addPopupMenuListener(new PopupMenuListener() {
                
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

                    popupHidden();
                }
                
                public void popupMenuCanceled(PopupMenuEvent e) {
                    
                    popupHidden();
                }

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

            });
            
        }
        
        return autoCompletePopup;
    }

    private JTextComponent queryEditorTextComponent() {

        return queryEditor.getEditorTextComponent();
    }

    private void focusAndSelectList() {
        
        autoCompletePopup.focusAndSelectList();
    }

    private Object existingKeyStrokeDownAction;
    private Object existingKeyStrokeUpAction;
    private Object existingKeyStrokePageDownAction;
    private Object existingKeyStrokePageUpAction;
    private Object existingKeyStrokeTabAction;
    private Object existingKeyStrokeEnterAction;

    private boolean editorActionsSaved;

    private void addFocusActions() {

        JTextComponent textComponent = queryEditorTextComponent();
        
        ActionMap actionMap = textComponent.getActionMap();
        actionMap.put(LIST_FOCUS_ACTION_KEY, listFocusAction);
        actionMap.put(LIST_SCROLL_ACTION_KEY_DOWN, listScrollActionDown);
        actionMap.put(LIST_SCROLL_ACTION_KEY_UP, listScrollActionUp);
        actionMap.put(LIST_SELECTION_ACTION_KEY, listSelectionAction);
        actionMap.put(LIST_SCROLL_ACTION_KEY_PAGE_DOWN, listScrollActionPageDown);
        actionMap.put(LIST_SCROLL_ACTION_KEY_PAGE_UP, listScrollActionPageUp);

        InputMap inputMap = textComponent.getInputMap();
        saveExistingActions(inputMap);
        
        inputMap.put(KEY_STROKE_DOWN, LIST_SCROLL_ACTION_KEY_DOWN);
        inputMap.put(KEY_STROKE_UP, LIST_SCROLL_ACTION_KEY_UP);

        inputMap.put(KEY_STROKE_PAGE_DOWN, LIST_SCROLL_ACTION_KEY_PAGE_DOWN);
        inputMap.put(KEY_STROKE_PAGE_UP, LIST_SCROLL_ACTION_KEY_PAGE_UP);

        inputMap.put(KEY_STROKE_TAB, LIST_FOCUS_ACTION_KEY);
        inputMap.put(KEY_STROKE_ENTER, LIST_SELECTION_ACTION_KEY);
        
        textComponent.addCaretListener(this);
    }

    private void saveExistingActions(InputMap inputMap) {

        if (!editorActionsSaved) {
        
            existingKeyStrokeDownAction = inputMap.get(KEY_STROKE_DOWN);
            existingKeyStrokeUpAction = inputMap.get(KEY_STROKE_UP);
            existingKeyStrokePageDownAction = inputMap.get(KEY_STROKE_PAGE_DOWN);
            existingKeyStrokePageUpAction = inputMap.get(KEY_STROKE_PAGE_UP);
            existingKeyStrokeEnterAction = inputMap.get(KEY_STROKE_ENTER);
            existingKeyStrokeTabAction = inputMap.get(KEY_STROKE_TAB);

            editorActionsSaved = true;
        }

    }

    private boolean canExecutePopupActions() {
        
        if (popupMenu().isVisible()) {
            
            return true;
        }

        resetEditorActions();
        return false;
    }
    
    private void resetEditorActions() {
        
        JTextComponent textComponent = queryEditorTextComponent();

        ActionMap actionMap = textComponent.getActionMap();
        actionMap.remove(LIST_FOCUS_ACTION_KEY);
        actionMap.remove(LIST_SELECTION_ACTION_KEY);
        actionMap.remove(LIST_SCROLL_ACTION_KEY_DOWN);
        actionMap.remove(LIST_SCROLL_ACTION_KEY_UP);

        InputMap inputMap = textComponent.getInputMap();
        inputMap.remove(KEY_STROKE_DOWN);
        inputMap.remove(KEY_STROKE_UP);
        inputMap.remove(KEY_STROKE_PAGE_DOWN);
        inputMap.remove(KEY_STROKE_PAGE_UP);
        inputMap.remove(KEY_STROKE_ENTER);
        inputMap.remove(KEY_STROKE_TAB);

        inputMap.put(KEY_STROKE_DOWN, existingKeyStrokeDownAction);
        inputMap.put(KEY_STROKE_UP, existingKeyStrokeUpAction);
        inputMap.put(KEY_STROKE_PAGE_DOWN, existingKeyStrokePageDownAction);
        inputMap.put(KEY_STROKE_PAGE_UP, existingKeyStrokePageUpAction);
        inputMap.put(KEY_STROKE_TAB, existingKeyStrokeTabAction);
        inputMap.put(KEY_STROKE_ENTER, existingKeyStrokeEnterAction);

        textComponent.removeCaretListener(this);
    }
    
    private void popupHidden() {

        resetEditorActions();
        queryEditorTextComponent().requestFocus();
    }
    
    public void refocus() {

        queryEditorTextComponent().requestFocus();
    }

    private void rebuildListSelectionsItems() {

        DatabaseConnection selectedConnection = queryEditor.getSelectedConnection();
        
        if (selectedConnection == null) {

            databaseHost = null;

        } else if (databaseHost == null) {
            
            databaseHost = databaseObjectFactory.createDatabaseHost(selectedConnection);
        }

        autoCompleteListItems = selectionsBuilder.build(databaseHost);
    }
    
    // TODO: determine query being executed and suggest based on that
    // introduce query types (select, insert etc)
    // track columns/tables in statement ????
    
    private List<AutoCompleteListItem> itemsStartingWith(String prefix) {
        
        boolean blankPrefix = StringUtils.isBlank(prefix);
        
        if (blankPrefix) {
            
            return selectionsBuilder.buildKeywords(databaseHost);
        }

        String wordPrefix = prefix.trim().toUpperCase();
        
        int dotIndex = prefix.indexOf('.');
        if (dotIndex != -1) {

            wordPrefix = wordPrefix.substring(dotIndex + 1);
            return itemsStartingWith(wordPrefix);
        }

        List<AutoCompleteListItem> itemsStartingWith = new ArrayList<AutoCompleteListItem>();

        for (AutoCompleteListItem item : autoCompleteListItems) {

            if (item.getInsertionValue().toUpperCase().startsWith(wordPrefix, 0)) {
            
                itemsStartingWith.add(item);
            }

        }
        
        if (itemsStartingWith.isEmpty()) {
            
            itemsStartingWith.add(noProposalsListItem());
        }
        
        return itemsStartingWith;
    }
    
    private AutoCompleteListItem noProposalsAutoCompleteListItem;
    
    private AutoCompleteListItem noProposalsListItem() {
        
        if (noProposalsAutoCompleteListItem == null) {
        
            noProposalsAutoCompleteListItem = new AutoCompleteListItem(null,
                    "No Proposals Available", null, AutoCompleteListItemType.NOTHING_PROPOSED);
        }

        return noProposalsAutoCompleteListItem;
        
    }
    
    private final ListFocusAction listFocusAction = new ListFocusAction();

    class ListFocusAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {

            if (!canExecutePopupActions()) {
                
                return;
            }

            focusAndSelectList();
        }

    } // ListFocusAction

    private final ListSelectionAction listSelectionAction = new ListSelectionAction();

    class ListSelectionAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {

            if (!canExecutePopupActions()) {
                
                return;
            }
            
            popupSelectionMade();
        }

    } // ListSelectionAction

    enum ListScrollType {
        
        UP, DOWN, PAGE_DOWN, PAGE_UP;
    }

    private final ListScrollAction listScrollActionDown = new ListScrollAction(ListScrollType.DOWN);
    private final ListScrollAction listScrollActionUp = new ListScrollAction(ListScrollType.UP);
    private final ListScrollAction listScrollActionPageDown = new ListScrollAction(ListScrollType.PAGE_DOWN);
    private final ListScrollAction listScrollActionPageUp = new ListScrollAction(ListScrollType.PAGE_UP);

    class ListScrollAction extends AbstractAction {

        private final ListScrollType direction;
        
        ListScrollAction(ListScrollType direction) {

            this.direction = direction;
        }

        public void actionPerformed(ActionEvent e) {

            if (!canExecutePopupActions()) {
                
                return;
            }
            
            switch (direction) {
            
                case DOWN:
                    autoCompletePopup.scrollSelectedIndexDown();
                    break;
                    
                case UP:
                    autoCompletePopup.scrollSelectedIndexUp();
                    break;
    
                case PAGE_DOWN:
                    autoCompletePopup.scrollSelectedIndexPageDown();
                    break;
                    
                case PAGE_UP:
                    autoCompletePopup.scrollSelectedIndexPageUp();
                    break;
                }

        }

    } // ListScrollAction

    public void popupClosed() {

//        popupHidden();
    }

    public void popupSelectionCancelled() {

        queryEditorTextComponent().requestFocus();
    }

    private boolean isAllLowerCase(String text) {
        
        for (char character : text.toCharArray()) {
            
            if (Character.isUpperCase(character)) {
                
                return false;
            }
            
        }
        
        return true;
    }

    public void popupSelectionMade() {

        AutoCompleteListItem selectedListItem = (AutoCompleteListItem) autoCompletePopup.getSelectedItem();
        
        if (selectedListItem == null || selectedListItem.isNothingProposed()) {
            
            return;
        }

        String selectedValue = selectedListItem.getInsertionValue();
        
        try {

            JTextComponent textComponent = queryEditorTextComponent();

            String wordAtCursor = queryEditor.getWordAtCursor();

            int wordAtCursorLength = wordAtCursor.length();
            int caretPosition = textComponent.getCaretPosition();
            int insertionIndex = caretPosition - wordAtCursorLength;

            if (selectedListItem.isKeyword() && isAllLowerCase(wordAtCursor)) {

                selectedValue = selectedValue.toLowerCase();
            }

            Document document = textComponent.getDocument();
            document.remove(insertionIndex, wordAtCursorLength);
            document.insertString(insertionIndex, selectedValue, null);

        } catch (BadLocationException e) {

            if (Log.isDebugEnabled()) {
                
                Log.debug("Error on autocomplete insertion", e);
            }

        } finally {

            autoCompletePopup.hidePopup();
        }
        
    }
    
    public void caretUpdate(CaretEvent e) {

        captureAndResetListValues();
    }

    public void connectionChanged(DatabaseConnection databaseConnection) {
        
        if (databaseHost != null) {

            databaseHost.close();
        }
        
        databaseHost = databaseObjectFactory.createDatabaseHost(databaseConnection);
        scheduleListItemLoad();
    }

    public void focusGained(FocusEvent e) {

        if (e.getSource() == queryEditorTextComponent()) {
            
            scheduleListItemLoad();
        }
        
    }

    private boolean rebuildingList;
    
    private SwingWorker worker;
    
    private void scheduleListItemLoad() {

        if (rebuildingList) {
            
            return;
        }

        worker = new SwingWorker() {

            public Object construct() {

                rebuildingList = true;
                rebuildListSelectionsItems();

                return "done";
            }

            public void finished() {

                rebuildingList = false;
            }
            
        };
        worker.start();
    }

    public void focusLost(FocusEvent e) {}
    
}
