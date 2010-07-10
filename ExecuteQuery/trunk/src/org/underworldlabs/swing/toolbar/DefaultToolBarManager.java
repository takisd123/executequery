/*
 * DefaultToolBarManager.java
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

package org.underworldlabs.swing.toolbar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.underworldlabs.Constants;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.swing.actions.ActionBuilder;

/**
 * Tool bar manager class for managing and creating
 * tool bars and associated components.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class DefaultToolBarManager {
    
    /** All tool bars added */
    private Map<String, ToolBar> toolBars;

    /** The tool bar base panel */
    private ToolBarBase toolBarBase;

    /** The button comparator */
    private ButtonComparator buttonComparator;

    /**
     * Creates a new instance of DefaultToolBarManager.
     * The toolsConfigPath variable may be null and is usually
     * a user defined/modified file system path differing from the 
     * defaults specified as a package resource XML path.
     *
     * @param toolsConfigPath - the user XML conf file path
     * @param defaultToolsResourcePath - the default XML conf resource file
     */
    public DefaultToolBarManager(String toolsConfigPath, 
                                 String defaultToolsResourcePath) {
        ToolBarProperties.init(toolsConfigPath, defaultToolsResourcePath);
        
        // initialise the tool bar cache
        toolBars = new HashMap<String, ToolBar>();
        
        // initialise the tools base panel
        toolBarBase = new ToolBarBase(ToolBarProperties.getNextToolbarRow());
        
        // button comparator for sorting
        buttonComparator = new ButtonComparator();
    }
    
    /**
     * Returns the tool bar base panel.
     */
    public ToolBarBase getToolBarBasePanel() {
        return toolBarBase;
    }

    /**
     * Resets the state of the base tool bar panel.
     */
    protected void reset() {
        toolBarBase.removeAll();
        toolBarBase.setRows(ToolBarProperties.getNextToolbarRow());
    }

    /**
     * Repaints and revalidates the tool bar base following a change to
     * its structure.
     */
    protected void fireToolbarsChanged() {
        toolBarBase.repaint();
        toolBarBase.revalidate();
    }
    
    /**
     * Builds (or rebuilds) the tool bar with the specified name
     * and adds it to the tool bar base panel and local cache.
     *
     * @param name - the name of the tool bar as it appears in the
     *               XML tool bar conf file
     * @param rebuild - whether this is a rebuild of an existing tool bar
     */
    protected void buildToolBar(String name, boolean rebuild) {
        ToolBarWrapper eqtb = ToolBarProperties.getToolBar(name);
        
        if (!eqtb.isVisible() || !eqtb.hasButtons()) {
            return;
        }
        
        ToolBar toolBar = null;
        if (rebuild) {
            toolBar = (ToolBar)toolBars.get(name);

            if (toolBar != null) {
                toolBar.removeAllButtons();
                toolBar.invalidate();
            }
            else {
                toolBar = new ToolBar(toolBarBase, name);
                toolBars.put(name, toolBar);
            }
            
        }
        else {
            toolBar = new ToolBar(toolBarBase, name);
            toolBars.put(name, toolBar);
        }
        
        Vector buttons = eqtb.getButtonsVector();
        Collections.sort(buttons, buttonComparator);
        
        RolloverButton button;
        
        for (int i = 0, k = buttons.size(); i < k; i++) {

            ToolBarButton tb = (ToolBarButton)buttons.get(i);
            
            if (!tb.isVisible()) {
            
                continue;
            }
            
            if (tb.isSeparator()) {

                toolBar.addSeparator();

            } else {

                button = new RolloverButton(ActionBuilder.get(tb.getActionId()),
                                           tb.getName());
                button.setText(Constants.EMPTY);
                toolBar.addButton(button);
            }

        }
        
        toolBar.buildToolBar();
        toolBarBase.addToolBar(toolBar, eqtb.getConstraints());
    }

}






