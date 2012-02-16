package org.underworldlabs.swing.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.border.LineBorder;

public class BluerpleBorder extends LineBorder {

    private static final Insets INSETS = new Insets(4, 4, 4, 4);
    
    public BluerpleBorder() {

        super(Color.GRAY);
    }

    @Override
    public Insets getBorderInsets(Component c) {

        return INSETS;
    }
    
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {

        insets.top = INSETS.top;
        insets.left = INSETS.left;
        insets.bottom = INSETS.bottom;
        insets.right = INSETS.right;
        return insets;
    }
    
}
