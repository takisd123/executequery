package org.executequery.gui;

import javax.swing.text.JTextComponent;

public interface ReadOnlyTextPane {
    
    void clear();
    
    void selectAll();
    
    void copy();
    
    String getText();

    JTextComponent getTextComponent();
    
}
