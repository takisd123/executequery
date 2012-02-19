/*
 * QueryEditorAutoCompletePopupPanel.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.executequery.gui.editor.TypeAheadList;
import org.executequery.gui.editor.TypeAheadListProvider;
import org.underworldlabs.swing.plaf.UIUtils;

public class QueryEditorAutoCompletePopupPanel extends JPopupMenu
                implements TypeAheadListProvider {

    /*
    private static final String POPUP_CANCELLED_KEY = "popupCancelledAction";

    private static final String LIST_SELECTION_CANCELLED_KEY = "listSelectionCancelledAction";

    private static final KeyStroke KEY_STROKE_ESC = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    private static final KeyStroke KEY_STROKE_SPACE = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);

    private static final KeyStroke KEY_STROKE_BACKSPACE = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);

    private static final KeyStroke KEY_STROKE_SHIFT_TAB = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK);
    */

    private static final Dimension PREFERRED_SIZE = new Dimension(450, 145);

    private TypeAheadList list;

    private List<AutoCompletePopupListener> listeners;

    public QueryEditorAutoCompletePopupPanel() {

        init();
    }

    private void init() {

        setLayout(new BorderLayout());
        setLightWeightPopupEnabled(false);
        setVisible(false);

        list = new TypeAheadList(this);
        list.setCellRenderer(new AutoCompleteListItemCellRenderer());

        /*
        ActionMap listActionMap = list.getActionMap();
        listActionMap.put(POPUP_CANCELLED_KEY, new PopupCancelledAction());
        listActionMap.put(LIST_SELECTION_CANCELLED_KEY, new ListSelectionCancelledAction());

        InputMap listInputMap = list.getInputMap();
        listInputMap.put(KEY_STROKE_ESC, POPUP_CANCELLED_KEY);
        listInputMap.put(KEY_STROKE_SPACE, POPUP_CANCELLED_KEY);
        listInputMap.put(KEY_STROKE_BACKSPACE, LIST_SELECTION_CANCELLED_KEY);
        listInputMap.put(KEY_STROKE_SHIFT_TAB, LIST_SELECTION_CANCELLED_KEY);
        */

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.getDefaultBorderColour()));

        setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SIZE);
    }

    public String getSelectedValue() {

        Object selectedValue = list.getSelectedValue();
        if (selectedValue != null) {

            return selectedValue.toString();
        }

        return "";
    }

    public Object getSelectedItem() {

        return list.getSelectedValue();
    }

    public void addAutoCompletePopupListener(AutoCompletePopupListener autoCompletePopupListener) {

        if (listeners == null) {

            listeners = new ArrayList<AutoCompletePopupListener>();
        }
        listeners.add(autoCompletePopupListener);
    }

    protected void resetValues(List<AutoCompleteListItem> values) {

        list.resetValues(values);
        if (values != null && !values.isEmpty()) {

            selectListIndex(0);
        }

    }

    protected void scrollSelectedIndexUp() {

        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex > 0) {

            selectListIndex(selectedIndex - 1);
        
        } else if (selectedIndex == 0) {
            
            selectListIndex(list.getModel().getSize() - 1);
        }

    }

    protected void scrollSelectedIndexDown() {

        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex < list.getModel().getSize() - 1) {

            selectListIndex(selectedIndex + 1);
        
        } else {
            
            selectListIndex(0);
        }

    }

    private static final int PAGE_SCROLL_SIZE = 5;

    protected void scrollSelectedIndexPageUp() {

        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex > 0) {

            int newIndex = selectedIndex - PAGE_SCROLL_SIZE;
            if (newIndex > 0) {

                selectListIndex(newIndex);

            } else {

                selectListIndex(0);
            }

        }

    }

    protected void scrollSelectedIndexPageDown() {

        int selectedIndex = list.getSelectedIndex();
        int modelSize = list.getModel().getSize();
        if (selectedIndex < modelSize - 1) {

            int newIndex = selectedIndex + PAGE_SCROLL_SIZE;
            if (newIndex < modelSize) {

                selectListIndex(newIndex);

            } else {

                selectListIndex(modelSize - 1);
            }

        }

    }

    private void selectListIndex(int index) {

        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }

    public void focusAndSelectList() {

        list.setListItemSelectedAndFocus(0);
    }

    public void listValueSelected(Object selectedValue) {

        firePopupSelectionMade();
        hidePopup();
    }

    public void refocus() {}

    protected void hidePopup() {

        if (isVisible()) {

            setVisible(false);
        }

        firePopupClosed();
    }

    private void firePopupClosed() {

        for (AutoCompletePopupListener listener : listeners) {

            listener.popupClosed();
        }

    }

    private void firePopupSelectionMade() {

        for (AutoCompletePopupListener listener : listeners) {

            listener.popupSelectionMade();
        }

    }

    private void firePopupSelectionCancelled() {

        for (AutoCompletePopupListener listener : listeners) {

            listener.popupSelectionCancelled();
        }

    }

    class ListSelectionCancelledAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            hidePopup();
            firePopupSelectionCancelled();
        }

    } // ListSelectionCancelledAction


    class PopupCancelledAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {

            hidePopup();
            firePopupSelectionCancelled();
        }

    } // PopupCancelledAction

}

