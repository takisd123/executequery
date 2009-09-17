/*
 * AddQueryBookmarkPanel.java
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.event.DefaultQueryBookmarkEvent;
import org.executequery.gui.ActionContainer;
import org.executequery.gui.DefaultActionButtonsPanel;
import org.executequery.gui.DefaultPanelButton;
import org.executequery.gui.FocusComponentPanel;
import org.executequery.gui.text.SQLTextPane;
import org.executequery.repository.QueryBookmark;
import org.executequery.repository.QueryBookmarks;
import org.executequery.repository.RepositoryException;
import org.executequery.util.StringBundle;
import org.executequery.util.SystemResources;
import org.underworldlabs.util.MiscUtils;

public class AddQueryBookmarkPanel extends DefaultActionButtonsPanel 
                                   implements FocusComponentPanel {

    public static final String TITLE = "Add Query Bookmark";
    public static final String FRAME_ICON = "Bookmarks16.gif";

    private static final String SAVE_COMMAND_NAME = "save";
    private static final String CANCEL_COMMAND_NAME = "cancel";

    private JComboBox nameField;
    
    private SQLTextPane textPane;

    private final ActionContainer parent;

    private final String queryText;

    private StringBundle bundle;
    
    public AddQueryBookmarkPanel(ActionContainer parent, String queryText) {
        
        this.parent = parent;
        this.queryText = queryText.trim();
        
        init();
    }

    private void init() {
        
        createNameComboBox();

        textPane = new SQLTextPane();
        textPane.setText(queryText);
        
        addActionButton(createSaveButton());
        addActionButton(createCancelButton());

        JPanel panel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets.top = 7;
        gbc.insets.left = 5;
        panel.add(new JLabel(bundle().getString("AddQueryBookmarkPanel.bookmarkName")), gbc);
        gbc.gridx = 1;
        gbc.insets.top = 5;
        gbc.insets.right = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets.bottom = 5;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(new JScrollPane(textPane), gbc);

        addContentPanel(panel);

        setPreferredSize(new Dimension(450, 250));
    }

    private JButton createCancelButton() {

        JButton button = new JButton(bundleString("cancelButton"));
        
        button.setActionCommand(CANCEL_COMMAND_NAME);
        button.addActionListener(this);

        return button;
    }
    
    private JButton createSaveButton() {

        JButton button = new DefaultPanelButton(bundleString("okButton"));
        
        button.setActionCommand(SAVE_COMMAND_NAME);
        button.addActionListener(this);

        return button;
    }

    private void createNameComboBox() {

        List<QueryBookmark> bookmarks = bookmarks().getQueryBookmarks();
        
        Vector<String> names = new Vector<String>(bookmarks.size() + 1) ;
        names.add("");
        
        for (QueryBookmark bookmark : bookmarks) {

            names.add(bookmark.getName());
        }

        nameField = new JComboBox(names);
        nameField.setEditable(true);
        nameField.setActionCommand(SAVE_COMMAND_NAME);

        nameField.setMinimumSize(new Dimension(nameField.getWidth(), 22));
        
        nameFieldEditor().setActionCommand(SAVE_COMMAND_NAME);
        nameFieldEditor().addActionListener(this);
    }

    private JTextField nameFieldEditor() {
        return (JTextField)((ComboBoxEditor)nameField.getEditor()).getEditorComponent();
    }
    
    public void cancel() {
        parent.finished();
    }

    public void save() {
        
        if (!fieldsValid()) {

            return;
        }

        QueryBookmark bookmark = createBookmark();

        if (bookmark == null) {
            
            return;
        }
        
        try {

            if (bookmark.isNew()) {

                bookmarks().addBookmark(bookmark);
                
            } else {

                bookmarks().save();
            }

            EventMediator.fireEvent(
                    new DefaultQueryBookmarkEvent(this, DefaultQueryBookmarkEvent.BOOKMARK_ADDED));
            
            parent.finished();
            
        } catch (RepositoryException e) {

            GUIUtilities.displayExceptionErrorDialog(
                    bundleString("saveError"), e);
        }

    }

    private QueryBookmark createBookmark() {

        QueryBookmark bookmark = null;
        
        String name = getNameFieldText();
        if (bookmarks().nameExists(name)) {

            int result = GUIUtilities.displayConfirmCancelDialog(
                    bundleString("validation.uniqueName"));
            
            if (result == JOptionPane.YES_OPTION) {

                bookmark = bookmarks().findBookmarkByName(name);
                bookmark.setQuery(textPane.getText());
                
            } else {

                return null;
            }
            
        } else {
        
            bookmark = newBookmark();
        }

        return bookmark;
    }

    private String getNameFieldText() {
        return nameField.getEditor().getItem().toString();
    }

    private QueryBookmarks bookmarks() {
        return QueryBookmarks.getInstance();
    }

    private boolean fieldsValid() {

        String value = getNameFieldText();
        if (MiscUtils.isNull(value)) {
            
            GUIUtilities.displayErrorMessage(bundleString("validation.name"));
            return false;
        }

        value = textPane.getText();
        if (MiscUtils.isNull(value)) {
            
            GUIUtilities.displayErrorMessage(bundleString("validation.query"));
            return false;
        }
        
        return true;
    }

    private QueryBookmark newBookmark() {
        
        QueryBookmark bookmark = new QueryBookmark();
        bookmark.setName(getNameFieldText());
        bookmark.setQuery(textPane.getText());
        
        return bookmark;
    }

    public Component getDefaultFocusComponent() {
        return nameField;
    }

    private StringBundle bundle() {
        if (bundle == null) {
            bundle = SystemResources.loadBundle(AddQueryBookmarkPanel.class);
        }
        return bundle;
    }

    private String bundleString(String key) {
        return bundle().getString("AddQueryBookmarkPanel." + key);
    }

}





