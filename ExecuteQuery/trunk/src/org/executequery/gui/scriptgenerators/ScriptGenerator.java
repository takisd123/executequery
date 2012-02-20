/*
 * ScriptGenerator.java
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

package org.executequery.gui.scriptgenerators;

import java.util.Vector;

import org.executequery.gui.browser.ColumnData;

/**
 * @deprecated
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public interface ScriptGenerator {
    
    public String getScriptFilePath();
    
    public ColumnData[] getColumnDataArray(String tableName);
    
    public Vector getSelectedTables();
    
    public boolean hasSelectedTables();
    
    public boolean includeConstraints();
    
    public boolean includeConstraintsInCreate();
    
    public void setResult(int result);
    
    public void dispose();
    
    public boolean includeConstraintsAsAlter();
    
    public void enableButtons(boolean enable);
    
    public String getDatabaseProductName();
    
    public String getSchemaName();
    
}



