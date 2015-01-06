package org.executequery.components;

import java.awt.Component;

import javax.swing.JSplitPane;

import org.executequery.GUIUtilities;
import org.executequery.plaf.LookAndFeelType;
import org.underworldlabs.swing.FlatSplitPane;

public class SplitPaneFactory {

    public JSplitPane createHorizontal() {
        
        return create(JSplitPane.HORIZONTAL_SPLIT);
    }
    
    public JSplitPane createVertical() {
        
        return create(JSplitPane.VERTICAL_SPLIT);
    }
    
    public JSplitPane create(int orientation) {

        if (usesCustomSplitPane()) {
        
            return new FlatSplitPane(orientation);

        } else {
            
            return new JSplitPane(orientation);
        }

    }
    
    public JSplitPane create(int orientation, Component leftComponent, Component rightComponent) {
        
        if (usesCustomSplitPane()) {
            
            return new FlatSplitPane(orientation, leftComponent, rightComponent);
            
        } else {
            
            return new JSplitPane(orientation, leftComponent, rightComponent);
        }
        
    }
    
    public boolean usesCustomSplitPane() {
        
        return !(usesJavaSplitPane());
    }
    
    public boolean usesJavaSplitPane() {
        
        LookAndFeelType lookAndFeelType = GUIUtilities.getLookAndFeel();
        return lookAndFeelType == LookAndFeelType.PLUGIN || lookAndFeelType == LookAndFeelType.NATIVE 
                || lookAndFeelType == LookAndFeelType.GTK;
    }
    
}
