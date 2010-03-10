/*
 * DefaultTable.java
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

package org.executequery.gui;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.underworldlabs.swing.plaf.UIUtils;
import org.underworldlabs.swing.table.DefaultTableHeaderRenderer;

/** 
 * Default table display using a custom header renderer and fixed min sizes.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class DefaultTable extends JTable {
    
    private static final int DEFAULT_ROW_HEIGHT = 20;
    
    public DefaultTable() {

        this(null);
    }

    /** Creates a new instance of DefaultTable */
    public DefaultTable(TableModel model) {
        
        super(model);
        
        init();
    }    
    
    public DefaultTable(Object[][] rowData, Object[] columnNames) {

        super(rowData, columnNames);
        
        init();
    }
    
    private void init() {

        setRowHeight(Math.max(getRowHeight(), DEFAULT_ROW_HEIGHT));

        if (UIUtils.isDefaultLookAndFeel() || UIUtils.usingOcean()) {

            getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());

        } else {

            JTableHeader tableHeader = getTableHeader();
            tableHeader.setPreferredSize(
                    new Dimension(tableHeader.getWidth(), 
                            Math.max(tableHeader.getHeight(), DEFAULT_ROW_HEIGHT)));
            
        }

    }
    
}
