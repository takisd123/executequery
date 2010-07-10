/*
 * GenericTransferable.java
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

package org.executequery.gui.browser.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author takisd
 */
public class GenericTransferable implements Transferable {
    
    /**
     * construct a transferabe with a given object to transfer
     * @param data  the data object to transfer
     */
    public GenericTransferable(Object data) {
        super();        
        this.data = data;
    }
    
    /**
     * get the data flavors supported by this object
     * @return an array of supported data flavors
     */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    /**
     * determine whether or not a given data flavor is supported by this transferable
     * @return true, if the given data flavor is supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }
    
    /**
     * get the data this transferable transports
     * @return the data transported by this transferable
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }
    
    /** the data this transferable transports */
    private Object data;
    
    /** storage for data flavors supported of this transferable */
    private static final DataFlavor[] flavors = new DataFlavor[1];
    
    /** the actual flavors supported by this transferable */
    static {
        flavors[0] = DataFlavor.stringFlavor;
    }
    
}






