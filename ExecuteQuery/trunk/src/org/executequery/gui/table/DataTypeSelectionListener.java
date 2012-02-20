/*
 * DataTypeSelectionListener.java
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

package org.executequery.gui.table;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines those panel's that respond to data type
 * selections.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public interface DataTypeSelectionListener {

    /**
     * Called when a data type has been selected.
     *
     * @param the data type value string
     */
    public void dataTypeSelected(String dataType);

    /**
     * Called when the selction is cancelled.
     */
    public void dataTypeSelectionCancelled();

}











