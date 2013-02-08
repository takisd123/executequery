/*
 * AbstractDriverPanel.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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

package org.executequery.gui.drivers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.executequery.GUIUtilities;
import org.executequery.components.FileChooserDialog;
import org.executequery.components.TextFieldPanel;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.datasource.DatabaseDefinition;
import org.executequery.gui.SimpleValueSelectionDialog;
import org.executequery.gui.WidgetFactory;
import org.executequery.gui.browser.DefaultInlineFieldButton;
import org.executequery.log.Log;
import org.executequery.repository.DatabaseDefinitionCache;
import org.executequery.util.StringBundle;
import org.executequery.util.SystemResources;
import org.underworldlabs.swing.DefaultButton;
import org.underworldlabs.swing.DefaultFieldLabel;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.underworldlabs.swing.FileSelector;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.underworldlabs.util.MiscUtils;

public abstract class AbstractDriverPanel extends JPanel
                                          implements ItemListener,
                                                     DriverPanel {

    private StringBundle bundle;

    private JTextField nameField;
    private JTextField descField;
    private JTextField classField;

    private JList jarPathList;
    private DefaultListModel jarPathListModel;

    private JComboBox driverUrlCombo;
    private JComboBox databaseNameCombo;

    private DynamicComboBoxModel urlComboModel;

    /** the currently selected driver */
    private DatabaseDriver databaseDriver;

    public AbstractDriverPanel() {

        super(new BorderLayout());

        bundle = SystemResources.loadBundle(AbstractDriverPanel.class);
        init();
    }

    public abstract void driverNameChanged();

    private void init() {

        ReflectiveAction action = new ReflectiveAction(this);

        JButton browseButton = new DefaultButton(
                action, getString("AbstractDriverPanel.addLibraryButton"), "browseDrivers");
        JButton removeButton = new DefaultButton(
                action, getString("AbstractDriverPanel.addRemoveButton"), "removeDrivers");

        JButton findButton = new DefaultInlineFieldButton(action);
        findButton.setText(getString("AbstractDriverPanel.addFindButton"));
        findButton.setActionCommand("findDriverClass");

        jarPathListModel = new DefaultListModel();

        nameField = textFieldWithKey("AbstractDriverPanel.driverNameToolTip");
        descField = textFieldWithKey("AbstractDriverPanel.descriptionToolTip");
        classField = textFieldWithKey("AbstractDriverPanel.classNameToolTip");

        jarPathList = new JList(jarPathListModel);
        jarPathList.setToolTipText(getString("AbstractDriverPanel.pathToolTip"));
        JScrollPane jarPathListScrollPane = new JScrollPane(jarPathList) {
            private int height = 120;
            @Override
            public Dimension getPreferredSize() {

                Dimension size = super.getPreferredSize();
                size.height = height;
                return size;
            }
            @Override
            public Dimension getMinimumSize() {

                Dimension size = super.getMinimumSize();
                size.height = height;
                return size;
            }
        };

        nameField.addFocusListener(new DriverNameFieldListener(this));

        databaseNameCombo = WidgetFactory.createComboBox(createDatabaseComboValues());
        databaseNameCombo.setToolTipText(getString("AbstractDriverPanel.databaseToolTip"));
        databaseNameCombo.addItemListener(this);

        urlComboModel = new DynamicComboBoxModel();
        driverUrlCombo = WidgetFactory.createComboBox(urlComboModel);
        driverUrlCombo.setToolTipText(getString("AbstractDriverPanel.jdbcUrlToolTip"));
        driverUrlCombo.setEditable(true);

        JPanel base = new TextFieldPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy++;
        gbc.insets = new Insets(10,10,5,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        base.add(labelWithKey("AbstractDriverPanel.driverNameLabel"), gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        base.add(labelWithKey("AbstractDriverPanel.descriptionLabel"), gbc);
        gbc.gridy++;
        base.add(labelWithKey("AbstractDriverPanel.databaseLabel"), gbc);
        gbc.gridy++;
        base.add(labelWithKey("AbstractDriverPanel.jdbcUrlLabel"), gbc);
        gbc.gridy++;
        base.add(labelWithKey("AbstractDriverPanel.pathLabel"), gbc);
        gbc.gridy+=4;
        base.add(labelWithKey("AbstractDriverPanel.classNameLabel"), gbc);
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        base.add(nameField, gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        base.add(descField, gbc);
        gbc.gridy++;
        base.add(databaseNameCombo, gbc);
        gbc.gridy++;
        base.add(driverUrlCombo, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets.right = 0;
        gbc.gridheight = 2;
        gbc.insets.top = 5;
        base.add(jarPathListScrollPane, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets.right = 10;
        gbc.gridheight = 1;
        gbc.insets.left = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(browseButton, gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        base.add(removeButton, gbc);

        gbc.gridy+=3;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.right = 0;
        gbc.insets.left = 10;
        gbc.insets.top = 0;
        base.add(classField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        gbc.insets.right = 10;
        gbc.insets.left = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(findButton, gbc);

        add(base, BorderLayout.CENTER);
    }

    private JLabel labelWithKey(String key) {

        return new DefaultFieldLabel(getString(key));
    }

    private JTextField textFieldWithKey(String key) {

        JTextField field = WidgetFactory.createTextField();
        field.setToolTipText(getString(key));

        return field;
    }

    private Vector<DatabaseDefinition> createDatabaseComboValues() {

        // retrieve the db name list
        List<DatabaseDefinition> databases = loadDatabaseDefinitions();

        int count = databases.size() + 1;
        Vector<DatabaseDefinition> databasesVector = new Vector<DatabaseDefinition>(count);

        // create a new list with a dummy value
        for (int i = 1; i < count; i++) {

            databasesVector.add(databases.get(i - 1));
        }

        // add the dummy
        databasesVector.add(0, new DatabaseDefinition(
                DatabaseDefinition.INVALID_DATABASE_ID,
                getString("AbstractDriverPanel.selectWithDots")));

        return databasesVector;
    }

    private List<DatabaseDefinition> loadDatabaseDefinitions() {

        return DatabaseDefinitionCache.getDatabaseDefinitions();
    }

    public void itemStateChanged(ItemEvent e) {

        // interested in selections only
        if (e.getStateChange() == ItemEvent.DESELECTED) {

            urlComboModel.removeAllElements();
            return;
        }

        DatabaseDefinition database = getSelectedDatabase();

        int id = database.getId();
        if (id > 0) {

            // reload the urls for the combo selection
            resetUrlCombo(database);

        } else {

            // otherwise clear all
            urlComboModel.removeAllElements();
        }

    }

    private DatabaseDefinition getSelectedDatabase() {

        return (DatabaseDefinition)databaseNameCombo.getSelectedItem();
    }

    private String jarPathsFormatted() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = jarPathListModel.size(); i < n; i++) {

            sb.append(jarPathListModel.get(i));

            if (i < (n-1)) {

                sb.append(";");
            }

        }

        return sb.toString();
    }

    public void findDriverClass(ActionEvent e) {

        if (databaseDriver.isDefaultSunOdbc()) {

            return;
        }

        if (jarPathListModel.isEmpty()) {

            GUIUtilities.displayErrorMessage(
                    getString("AbstractDriverPanel.pathListEmptyError"));

            return;
        }

        String[] drivers = null;
        try {

            GUIUtilities.showWaitCursor();
            drivers = MiscUtils.findImplementingClasses(
                                            "java.sql.Driver", jarPathsFormatted());

        } catch (MalformedURLException urlExc) {

            logError(urlExc);

            GUIUtilities.displayErrorMessage(
                    getString("AbstractDriverPanel.pathListEmptyError"));

        } catch (IOException ioExc) {

            logError(ioExc);

            GUIUtilities.displayErrorMessage(
                    getString("AbstractDriverPanel.ioError", ioExc.getMessage()));

        } finally {

            GUIUtilities.showNormalCursor();
        }

        if (drivers == null || drivers.length == 0) {

            GUIUtilities.displayWarningMessage(
                    getString("AbstractDriverPanel.noDriverClassesError"));

            return;
        }

        int result = -1;
        String value = null;

        while (true) {

            SimpleValueSelectionDialog dialog =
                    new SimpleValueSelectionDialog(
                            getString("AbstractDriverPanel.selectJdbcDriverLabel"), drivers);

            result = dialog.showDialog();
            if (result == JOptionPane.OK_OPTION) {

                value = dialog.getValue();
                if (value == null) {

                    GUIUtilities.displayErrorMessage(
                            getString("AbstractDriverPanel.selectJdbcDriverNoSelection"));

                } else {

                    classField.setText(value);
                    databaseDriver.setClassName(value);
                    break;
                }

            } else {

                break;
            }

        }
    }

    private void logError(Throwable e) {

        if (Log.isDebugEnabled()) {

            Log.debug("Error locating driver class", e);
        }

    }

    public void removeDrivers(ActionEvent e) {

        int selectedIndex = jarPathList.getSelectedIndex();

        Object[] selections = jarPathList.getSelectedValues();
        if (selections != null && selections.length > 0) {

            for (Object path : selections) {

                jarPathListModel.removeElement(path);
            }

        }

        int newSize = jarPathListModel.size();
        if (newSize > 0) {

            if (selectedIndex > newSize - 1) {

                selectedIndex = newSize - 1;
            }

            jarPathList.setSelectedIndex(selectedIndex);
        }

    }

    public void browseDrivers(ActionEvent e) {

        if (databaseDriver.isDefaultSunOdbc()) {

            return;
        }

        FileSelector jarFiles = new FileSelector(
                new String[] {"jar"}, getString("AbstractDriverPanel.javaArchiveFiles"));

        FileSelector zipFiles = new FileSelector(
                new String[] {"zip"}, getString("AbstractDriverPanel.zipArchiveFiles"));

        FileChooserDialog fileChooser = new FileChooserDialog();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(zipFiles);
        fileChooser.addChoosableFileFilter(jarFiles);

        fileChooser.setDialogTitle(getString("AbstractDriverPanel.selectJdbcDrivers"));
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);

        int result = fileChooser.showDialog(
                GUIUtilities.getInFocusDialogOrWindow(), getString("AbstractDriverPanel.select"));

        if (result == JFileChooser.CANCEL_OPTION) {

            return;
        }

        File[] files = fileChooser.getSelectedFiles();
        for (int i = 0; i < files.length; i++) {

            jarPathListModel.addElement(files[i].getAbsolutePath());
        }

        databaseDriver.setPath(jarPathsFormatted());
    }

    /**
     * Populates the driver object from the field values.
     */
    protected final void populateDriverObject() {

        if (databaseDriver == null) {

            return;
        }

        // ODBC driver can not be changed
        if (databaseDriver.isDefaultSunOdbc()) {

            return;
        }

        databaseDriver.setName(nameField.getText());
        databaseDriver.setDescription(descField.getText());
        databaseDriver.setClassName(classField.getText());
        databaseDriver.setURL(driverUrlCombo.getEditor().getItem().toString());
        databaseDriver.setPath(jarPathsFormatted());

        DatabaseDefinition database = getSelectedDatabase();
        if (database.getId() > 0) {

            databaseDriver.setDatabaseType(database.getId());
        }

        if (databaseDriver.getId() == 0) {

            databaseDriver.setId(System.currentTimeMillis());
            // need to check exisitng conns with this driver's name
        }

    }

    private void driverPathsToList(String driversPath) {

        if (!MiscUtils.isNull(driversPath)) {

            String[] paths = driversPath.split(";");
            for (String path : paths) {

                jarPathListModel.addElement(path);
            }

        }

    }

    public void setDriver(DatabaseDriver databaseDriver) {

        try {

            databaseNameCombo.removeItemListener(this);

            this.databaseDriver = databaseDriver;

            nameField.setText(databaseDriver.getName());
            descField.setText(formatDriverDescription(databaseDriver));
            classField.setText(databaseDriver.getClassName());

            jarPathListModel.clear();
            driverPathsToList(databaseDriver.getPath());

            int databaseId = databaseDriver.getType();
            DatabaseDefinition database = loadDatabaseDefinition(databaseId);

            if (database != null && database.isValid()) {

                resetUrlCombo(database);
                databaseNameCombo.setSelectedItem(database);

            } else {

                urlComboModel.removeAllElements();
                databaseNameCombo.setSelectedIndex(0);
            }

            String url = databaseDriver.getURL();
            if (!MiscUtils.isNull(url)) {

                urlComboModel.insertElementAt(url, 0);
            }

            if (urlComboModel.getSize() > 0) {

                driverUrlCombo.setSelectedIndex(0);
            }

            nameField.requestFocusInWindow();
            nameField.selectAll();

        } finally {

            databaseNameCombo.addItemListener(this);
        }

    }

    private DatabaseDefinition loadDatabaseDefinition(int databaseId) {

        return DatabaseDefinitionCache.getDatabaseDefinition(databaseId);
    }

    private String formatDriverDescription(DatabaseDriver databaseDriver) {

        return databaseDriver.getDescription().equals("Not Available") ?
                                "" : databaseDriver.getDescription();
    }

    private void resetUrlCombo(DatabaseDefinition database) {

        String firstElement = null;
        if (urlComboModel.getElementAt(0) != null) {
            firstElement = urlComboModel.getElementAt(0).toString();
        }

        urlComboModel.removeAllElements();

        if (firstElement != null) {
            urlComboModel.addElement(firstElement);
        }

        for (int i = 0, n = database.getUrlCount(); i < n; i++) {
            urlComboModel.addElement(database.getUrl(i));
        }
    }

    public DatabaseDriver getDriver() {
        populateDriverObject();
        return databaseDriver;
    }

    protected final StringBundle getBundle() {
        return bundle;
    }

    protected final String getString(String key) {
        return getBundle().getString(key);
    }

    protected final String getString(String key, Object args) {
        return getBundle().getString(key, args);
    }

}





