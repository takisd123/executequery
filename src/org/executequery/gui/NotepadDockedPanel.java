package org.executequery.gui;

import java.awt.BorderLayout;

public class NotepadDockedPanel extends AbstractDockedTabActionPanel {

    public static final String TITLE = "Notepad";

    public static final String MENU_ITEM_KEY = "viewNotepad";
    
    public static final String PROPERTY_KEY = "system.display.notepad";

    private ScratchPadPanel scratchPadPanel;
    
    public NotepadDockedPanel() {

        super(new BorderLayout());
        
        scratchPadPanel = new ScratchPadPanel();
        scratchPadPanel.getPanelToolBar().remove(0);
        add(scratchPadPanel, BorderLayout.CENTER);
    }

    public boolean tabViewClosing() {
        return true;
    }

    public boolean tabViewSelected() {
        return true;
    }

    public boolean tabViewDeselected() {
        return true;
    }

    public String toString() {
        return TITLE;
    }

    public String getMenuItemKey() {
        return MENU_ITEM_KEY;
    }

    public String getPropertyKey() {
        return PROPERTY_KEY;
    }

    public String getTitle() {
        return TITLE;
    }

    
    
}
