package org.executequery.gui;

import java.awt.Insets;

import org.underworldlabs.swing.NumberTextField;

public class DefaultNumberTextField extends NumberTextField {

    public DefaultNumberTextField() {
        
        super();
    }

    public DefaultNumberTextField(int digits) {

        super(digits);
    }

    public Insets getMargin() {

        return GUIConstants.DEFAULT_FIELD_MARGIN;
    }
    
    public int getHeight() {

        return super.getHeight() < GUIConstants.DEFAULT_FIELD_HEIGHT ? 
                GUIConstants.DEFAULT_FIELD_HEIGHT : super.getHeight();
    }
    
}
