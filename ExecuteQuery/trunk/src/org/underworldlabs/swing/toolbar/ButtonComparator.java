/*
 * ButtonComparator.java
 *
 * Copyright (C) 2002-2012 Takis Diakoumis
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

import java.util.Comparator;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class ButtonComparator implements Comparator<ToolBarButton> {
    
    public int compare(ToolBarButton obj1, ToolBarButton obj2) {
        int first = obj1.getOrder();
        int second = obj2.getOrder();
        
        if (first < second)
            return -1;
        else if (first > second)
            return 1;
        else
            return 0;
        
    }
    
}



