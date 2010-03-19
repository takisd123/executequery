/*
 * PropertiesAppearance.java
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

package org.executequery.gui.prefs;

import java.util.ArrayList;
import java.util.List;

import org.executequery.Constants;
import org.underworldlabs.util.SystemProperties;

/** 
 * System preferences appearance panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class PropertiesAppearance extends PropertiesBasePanel {
    
    private SimplePreferencesPanel preferencesPanel;
    
    /** <p>Constructs a new instance. */
    public PropertiesAppearance() {
        try  {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Initializes the state of this instance. */
    private void init() throws Exception {
        
    	List<UserPreference> list = new ArrayList<UserPreference>();

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "General",
                    null));
        
        String key = "system.display.statusbar";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Status bar",
                    Boolean.valueOf(stringUserProperty(key))));

        key = "system.display.console";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "System console",
                    Boolean.valueOf(stringUserProperty(key))));

        key = "system.display.connections";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Connections",
                    Boolean.valueOf(stringUserProperty(key))));

        key = "system.display.drivers";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Drivers",
                    Boolean.valueOf(stringUserProperty(key))));

        key = "system.display.keywords";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "SQL Keywords",
                    Boolean.valueOf(stringUserProperty(key))));

        key = "system.display.state-codes";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "SQL State Codes",
                    Boolean.valueOf(stringUserProperty(key))));

        key = "system.display.systemprops";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "System properties palette",
                    Boolean.valueOf(stringUserProperty(key))));

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "Appearance",
                    null));

        key = "startup.display.lookandfeel";
        list.add(new UserPreference(
                    UserPreference.STRING_TYPE,
                    key,
                    "Look and feel (requires restart)",
                    stringUserProperty(key),
                    Constants.LOOK_AND_FEELS));

        key = "desktop.background.custom.colour";
        list.add(new UserPreference(
                    UserPreference.COLOUR_TYPE,
                    key,
                    "Desktop background",
                    SystemProperties.getColourProperty("user", key)));

        key = "decorate.dialog.look";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Decorate dialogs",
                    Boolean.valueOf(stringUserProperty(key))));

        key = "decorate.frame.look";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Decorate frame",
                    Boolean.valueOf(stringUserProperty(key))));

        UserPreference[] preferences = 
                (UserPreference[])list.toArray(new UserPreference[list.size()]);
        preferencesPanel = new SimplePreferencesPanel(preferences);
        addContent(preferencesPanel);

    }
    
    public void restoreDefaults() {
        preferencesPanel.savePreferences();
    }
    
    public void save() {
        preferencesPanel.savePreferences();
    }
    
}


