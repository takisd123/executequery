package org.executequery.gui.editor.autocomplete;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

public class AutoCompletePopupAction extends AbstractAction {

    private final AutoCompletePopupProvider autoCompletePopup; 
    
    public AutoCompletePopupAction(AutoCompletePopupProvider autoCompletePopup) {

        super();

        this.autoCompletePopup = autoCompletePopup;
        putValue(Action.ACCELERATOR_KEY, keyStrokeForAction());
    }

    private KeyStroke keyStrokeForAction() {

        return KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK);
    }

    public void actionPerformed(ActionEvent e) {

        autoCompletePopup.firePopupTrigger();
    }

}
