/*
 * ExecuteQueryMenu.java
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

package org.executequery.gui.menu;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;

import org.executequery.repository.MenuItemRepository;
import org.executequery.repository.spi.MenuItemXMLRepository;

/**
 * Application main menu.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ExecuteQueryMenu extends JMenuBar {
   
    private JMenuItemFactory jMenuItemFactory;

    public ExecuteQueryMenu() {
        
        try {

            init();
           
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        
        setBorder(null);
        
        createMenus();
    }

    private void createMenus() {
        
        jMenuItemFactory = new JMenuItemFactory();
        
        MenuItemRepository menuItemRepository = new MenuItemXMLRepository();
        
        List<MenuItem> menuItems = menuItemRepository.getMenuItems();
        
        construct(menuItems);
    }

    private void construct(List<MenuItem> menuItems) {

        buildMenuForParent(null, menuItems);
    }
    
    private void buildMenuForParent(JMenuItem parent, List<MenuItem> menuItems) {

        for (MenuItem menuItem: menuItems) {

            if (menuItem.isSeparator()) {

                if (parent instanceof JMenu) {

                    JMenu jMenu = (JMenu)parent;
                    
                    if (!menuItem.hasIndex()) {
                    
                        jMenu.addSeparator();
                        
                    } else {
                        
                        jMenu.add(createMenuSeparator(), menuItem.getIndex());
                    }

                }
                
                continue;
            }
            
            JMenuItem jMenuItem = jMenuItemFactory.createJMenuItem(parent, menuItem);

            if (jMenuItem instanceof JMenu) {

                buildMenuForParent(jMenuItem, menuItem.getChildren());
                
                if (parent == null) {

                    add(jMenuItem);
                }

            }
            
        }
        
    }

    private Separator createMenuSeparator() {

        return new JPopupMenu.Separator();
    }

}






