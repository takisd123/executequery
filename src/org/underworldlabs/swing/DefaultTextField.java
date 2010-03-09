package org.underworldlabs.swing;

import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.text.Document;

import org.executequery.gui.GUIConstants;

public class DefaultTextField extends JTextField {

    public DefaultTextField() {

        super();
    }

    public DefaultTextField(Document doc, String text, int columns) {

        super(doc, text, columns);
    }

    public DefaultTextField(int columns) {

        super(columns);
    }

    public DefaultTextField(String text, int columns) {

        super(text, columns);
    }

    public DefaultTextField(String text) {

        super(text);
    }

    public Insets getMargin() {

        return GUIConstants.DEFAULT_FIELD_MARGIN;
    }
    
    public int getHeight() {

        return Math.max(super.getHeight(), GUIConstants.DEFAULT_FIELD_HEIGHT);
    }
    
}
