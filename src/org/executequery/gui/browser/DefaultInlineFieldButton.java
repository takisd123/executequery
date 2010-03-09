package org.executequery.gui.browser;

import javax.swing.Action;
import javax.swing.Icon;

import org.executequery.gui.GUIConstants;
import org.underworldlabs.swing.DefaultButton;

public class DefaultInlineFieldButton extends DefaultButton {

    public DefaultInlineFieldButton() {
        super();
    }

    public DefaultInlineFieldButton(Action a) {
        super(a);
    }

    public DefaultInlineFieldButton(Icon icon) {
        super(icon);
    }

    public DefaultInlineFieldButton(String text, Icon icon) {
        super(text, icon);
    }

    public DefaultInlineFieldButton(String text) {
        super(text);
    }

    public int getHeight() {        
        return GUIConstants.DEFAULT_FIELD_HEIGHT + 1;
    }
    
}
