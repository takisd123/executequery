package org.executequery.components;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import org.underworldlabs.swing.DefaultButton;

/** 
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
*/
public class MinimumWidthActionButton extends DefaultButton {

    private final int minimumWidth;
    
    public MinimumWidthActionButton(int minimumWidth,
                                  ActionListener actionListener, 
                                  String name, 
                                  String command) {

        super(name);
        this.minimumWidth = minimumWidth;
        setActionCommand(command);
        addActionListener(actionListener);
    }
    
    @Override
    public Dimension getPreferredSize() {

        Dimension dimension = super.getPreferredSize();
        dimension.width = Math.max(minimumWidth, dimension.width);

        return dimension;
    }

}
