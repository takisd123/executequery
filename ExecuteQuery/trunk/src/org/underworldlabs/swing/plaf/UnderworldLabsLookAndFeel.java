/*
 * UnderworldLabsLookAndFeel.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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

package org.underworldlabs.swing.plaf;

import javax.swing.UIDefaults;

import org.underworldlabs.swing.plaf.smoothgradient.SmoothGradientLookAndFeel;

/**
 * Themed extension to SmoothGradientLookAndFeel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class UnderworldLabsLookAndFeel extends SmoothGradientLookAndFeel {
    
    /** Creates a new instance of ShadesOfGrayLookAndFeel */
    public UnderworldLabsLookAndFeel() {
        
        setCurrentTheme(new BluerpleTheme());
    }
    
    public String getName() {
        
        return "Default UnderworldLabs Look and Feel with the default Bluerple Theme";
    }
    
    public String getDescription() {
        
        return "Themed extension to Smooth Gradient Look and Feel - modified from " +
                "The JGoodies Plastic Look and Feel";
    }
    
    @Override
    protected void initComponentDefaults(UIDefaults table) {

    	if (UIUtils.isMac()) {
    		
    		// TODO: !!! apply mac key bindings - delegate to new class and use across all!!!
    		// fukin' macs!
    		
    		// wee average but minor reference here:
    		// http://lists.apple.com/archives/java-dev/2008/Apr/msg00209.html
    		
    		
    	}
    	
    	super.initComponentDefaults(table);
    }
    

}





