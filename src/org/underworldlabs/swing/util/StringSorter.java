/*
 * StringSorter.java
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

package org.underworldlabs.swing.util;

import java.io.Serializable;
import java.util.Comparator;

/** 
 * Simple string sorter.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public final class StringSorter implements Comparator<String>, Serializable {

    public int compare(String value1, String value2) {

        int result = value1.compareTo(value2);

        if (result < 0) {

            return -1;

        } else if (result > 0) {
        
            return 1;

        } else {
        
            return 0;
        }

    }
    
}






