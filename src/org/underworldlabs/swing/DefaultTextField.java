package org.underworldlabs.swing;

import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.text.Document;

import org.executequery.gui.GUIConstants;
import org.underworldlabs.swing.menu.SimpleTextComponentPopUpMenu;

public class DefaultTextField extends JTextField {

    public DefaultTextField() {

        super();
        addPopupMenu();
    }

    public DefaultTextField(Document doc, String text, int columns) {

        super(doc, text, columns);
        addPopupMenu();
    }

    public DefaultTextField(int columns) {

        super(columns);
        addPopupMenu();
    }

    public DefaultTextField(String text, int columns) {

        super(text, columns);
        addPopupMenu();
    }

    public DefaultTextField(String text) {

        super(text);
        addPopupMenu();
    }

    private void addPopupMenu() {
        
         SimpleTextComponentPopUpMenu popUpMenu = new SimpleTextComponentPopUpMenu();
         popUpMenu.registerTextComponent(this);
    }
    
    public Insets getMargin() {

        return GUIConstants.DEFAULT_FIELD_MARGIN;
    }
    
    public int getHeight() {

        return Math.max(super.getHeight(), GUIConstants.DEFAULT_FIELD_HEIGHT);
    }
    
}
