/*
 * SystemPropertiesDockedTab.java
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

package org.executequery.gui;

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import org.underworldlabs.swing.HeapMemoryPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * System properties docked component.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class SystemPropertiesDockedTab extends AbstractDockedTabActionPanel {
    
    public static final String TITLE = "System Properties";
    
    /** the system properties panel */
    private SystemPropertiesPanel propertiesPanel;
    
    /** the heap resources panel */
    private HeapMemoryPanel resourcesPanel;
    
    /** Creates a new instance of SystemPropertiesDockedTab */
    public SystemPropertiesDockedTab() {
        super(new BorderLayout());
        init();
    }
    
    private void init() {
        propertiesPanel = new SystemPropertiesPanel();
        resourcesPanel = new HeapMemoryPanel();
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("System", propertiesPanel);
        tabs.add("Resources", resourcesPanel);
        
        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Override to clean up the mem thread.
     */
    public boolean tabViewClosing() {
        resourcesPanel.stopTimer();
        return true;
    }

    /**
     * Override to make sure the timer has started.
     */
    public boolean tabViewSelected() {
        resourcesPanel.startTimer();
        return true;
    }

    // ----------------------------------------
    // DockedTabView Implementation
    // ----------------------------------------

    public static final String MENU_ITEM_KEY = "viewSystemProperties";
    
    public static final String PROPERTY_KEY = "system.display.systemprops";
    
    /**
     * Returns the display title for this view.
     *
     * @return the title displayed for this view
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Returns the name defining the property name for this docked tab view.
     *
     * @return the key
     */
    public String getPropertyKey() {
        return PROPERTY_KEY;
    }

    /**
     * Returns the name defining the menu cache property
     * for this docked tab view.
     *
     * @return the preferences key
     */
    public String getMenuItemKey() {
        return MENU_ITEM_KEY;
    }

}










