/*
 * DefaultDockedTabListener.java
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

package org.executequery.base;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Default empty implementation of DockedTabListener.
 * 
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class DefaultDockedTabListener implements DockedTabListener {
    
    /** Creates a new instance of AbstractDockedTabListener */
    public DefaultDockedTabListener() {}
    
    /**
     * Indicates a tab minimised event.
     *
     * @param the event 
     */
    public void tabMinimised(DockedTabEvent e) {}

    /**
     * Indicates a tab selected event.
     *
     * @param the event 
     */
    public void tabSelected(DockedTabEvent e) {}

    /**
     * Indicates a tab deselected event.
     *
     * @param the event 
     */
    public void tabDeselected(DockedTabEvent e) {}
    
    /**
     * Indicates a tab closed event.
     *
     * @param the event 
     */
    public void tabClosed(DockedTabEvent e) {}

    /**
     * Indicates a tab restored from minimised event.
     *
     * @param the event 
     */
    public void tabRestored(DockedTabEvent e) {}

}










