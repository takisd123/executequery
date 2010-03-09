package org.underworldlabs.swing;

import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.executequery.gui.GUIConstants;

public class DefaultFieldLabel extends JLabel {

    public DefaultFieldLabel() {
        
        super();
    }

    public DefaultFieldLabel(Icon image, int horizontalAlignment) {
        
        super(image, horizontalAlignment);
    }

    public DefaultFieldLabel(Icon image) {
        
        super(image);
    }

    public DefaultFieldLabel(String text, Icon icon, int horizontalAlignment) {
        
        super(text, icon, horizontalAlignment);
    }

    public DefaultFieldLabel(String text, int horizontalAlignment) {
       
        super(text, horizontalAlignment);
    }

    public DefaultFieldLabel(String text) {
        
        super(text);
    }

    public Insets getMargin() {

        return GUIConstants.DEFAULT_FIELD_MARGIN;
    }
    
    public int getHeight() {

        return Math.max(super.getHeight(), GUIConstants.DEFAULT_FIELD_HEIGHT);
    }
    
}
