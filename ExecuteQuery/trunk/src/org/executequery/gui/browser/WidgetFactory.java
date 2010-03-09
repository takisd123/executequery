package org.executequery.gui.browser;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.executequery.gui.DefaultNumberTextField;
import org.underworldlabs.swing.DefaultButton;
import org.underworldlabs.swing.DefaultComboBox;
import org.underworldlabs.swing.DefaultPasswordField;
import org.underworldlabs.swing.DefaultTextField;
import org.underworldlabs.swing.NumberTextField;

public final class WidgetFactory {

    public static JButton createButton(String text) {
        
        return new DefaultButton(text);
    }
    
    public static JComboBox createComboBox(Vector<?> items) {
        
        return new DefaultComboBox(items);
    }
    
    public static JComboBox createComboBox(ComboBoxModel model) {
        
        return new DefaultComboBox(model);
    }
    
    public static JComboBox createComboBox(Object[] items) {
        
        return new DefaultComboBox(items);
    }
    
    public static JComboBox createComboBox() {
        
        return new DefaultComboBox();
    }
    
    public static NumberTextField createNumberTextField() {
        
        return new DefaultNumberTextField();
    }
    
    public static JTextField createTextField() {
        
        return new DefaultTextField();
    }
    
    public static JTextField createTextField(String text) {
        
        return new DefaultTextField(text);
    }
    
    public static JPasswordField createPasswordField() {
        
        return new DefaultPasswordField();
    }
    
}
