package org.underworldlabs.swing;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.executequery.gui.GUIConstants;

public class DefaultComboBox extends JComboBox {

    public DefaultComboBox() {
        
        super();
    }

    public DefaultComboBox(ComboBoxModel aModel) {

        super(aModel);
    }

    public DefaultComboBox(Object[] items) {

        super(items);
    }

    public DefaultComboBox(Vector<?> items) {

        super(items);
    }

    public int getHeight() {

        return Math.max(super.getHeight(), GUIConstants.DEFAULT_FIELD_HEIGHT); 
    }
    
}
