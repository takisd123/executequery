package org.underworldlabs.swing;

import java.awt.Insets;

import javax.swing.JPasswordField;
import javax.swing.text.Document;

import org.executequery.gui.GUIConstants;

public class DefaultPasswordField extends JPasswordField {

    public DefaultPasswordField() {

        super();
    }

    public DefaultPasswordField(Document doc, String txt, int columns) {

        super(doc, txt, columns);
    }

    public DefaultPasswordField(int columns) {

        super(columns);
    }

    public DefaultPasswordField(String text, int columns) {

        super(text, columns);
    }

    public DefaultPasswordField(String text) {

        super(text);
    }

    public Insets getMargin() {

        return GUIConstants.DEFAULT_FIELD_MARGIN;
    }
    
    public int getHeight() {

        return Math.max(super.getHeight(), GUIConstants.DEFAULT_FIELD_HEIGHT);
    }
    
}
