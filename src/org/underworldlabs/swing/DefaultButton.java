package org.underworldlabs.swing;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import org.executequery.gui.GUIConstants;

public class DefaultButton extends JButton {

    public DefaultButton() {

        super();
    }

    public DefaultButton(Action a) {

        super(a);
    }

    public DefaultButton(Icon icon) {

        super(icon);
    }

    public DefaultButton(String text, Icon icon) {

        super(text, icon);
    }

    public DefaultButton(String text) {

        super(text);
    }

    public DefaultButton(ActionListener actionListener, String text, String actionCommand) {

        super(text);
        addActionListener(actionListener);
        setActionCommand(actionCommand);
    }

    @Override
    public Dimension getPreferredSize() {

        Dimension preferredSize = super.getPreferredSize();
        preferredSize.height = getHeight();
        
        return preferredSize;
    }
    
    public int getHeight() {

        return Math.max(super.getHeight(), GUIConstants.DEFAULT_BUTTON_HEIGHT); 
    }
    
}
