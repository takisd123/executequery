package org.executequery.gui.editor.autocomplete;

import javax.swing.Action;

public interface AutoCompletePopupProvider {

    void firePopupTrigger();
    
    Action getPopupAction();

}
