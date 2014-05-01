/*
 * TextUndoManager.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.executequery.gui.text;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.executequery.gui.UndoableComponent;

/**
 * Undo manager for text components. 
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class TextUndoManager extends UndoManager 
                             implements UndoableEditListener,
                                        FocusListener,
                                        KeyListener {
    
    /** the text component this manager is assigned to */
    private JTextComponent textComponent;
    
    /** the text component's document */
    private Document document;
    
    /** the current compound edit */
    private CompoundEdit compoundEdit;
    
    /** The undo command */
    private Action undoCommand;
    
    /** The redo command */
    private Action redoCommand;
    
    /** Creates a new instance of TextUndoManager */
    public TextUndoManager(JTextComponent textComponent) {
        this.textComponent = textComponent;
        document = textComponent.getDocument();
        document.addUndoableEditListener(this);
        
        // add a key listener
        textComponent.addKeyListener(this);
        
        // add the focus listener
        textComponent.addFocusListener(this);
        
        // retrieve the undo/redo actions from the cache
        undoCommand = ActionBuilder.get("undo-command");
        redoCommand = ActionBuilder.get("redo-command");
        
        // initialise the compound edit
        compoundEdit = new CompoundEdit();
    }

    /**
     * Updates the state of undo/redo on a focus gain.
     */
    public void focusGained(FocusEvent e) {
        // register this as an undo/redo component
        if (textComponent instanceof UndoableComponent) {
            GUIUtilities.registerUndoRedoComponent((UndoableComponent)textComponent);
        }
        updateUndo();
    }

    /**
     * Updates the state of undo/redo on a focus lost.
     */
    public void focusLost(FocusEvent e) {
        if (undoCommand != null) {
            undoCommand.setEnabled(false);
        }        
        if (redoCommand != null) {
            redoCommand.setEnabled(false);
        }
        // deregister this as an undo/redo component
        if (textComponent instanceof UndoableComponent) {
            GUIUtilities.registerUndoRedoComponent(null);
        }
    }

    /**
     * Invoked when a key has been pressed. 
     */
    public void keyPressed(KeyEvent e) {
        if (!e.isActionKey()) {
            
            // we want to check the char that was typed to determine
            // if it was whitespace. we want whitespace chars in
            // a sequence to be undone as a block
            char keyChar = e.getKeyChar();
            if (Character.isWhitespace(keyChar)) {

                if (!lastEntryWhitespace) {
                    lastEntryWhitespace = true;
                    addUndoEdit();
                }

            }
            else {
                // if it was and isn't now - complete the edit
                if (lastEntryWhitespace) {
                    lastEntryWhitespace = false;
                    addUndoEdit();
                }
            }

        }
    }

    /**
     * Invoked when a key has been released. Does nothing.
     */
    public void keyReleased(KeyEvent e) {}

    /**
     * Invoked when a key has been typed. Does nothing.
     */
    public void keyTyped(KeyEvent e) {}

    /**
     * Updates the state of the undo/redo actions.
     */
    public void updateUndo() {
        undoCommand.setEnabled(canUndo());
        redoCommand.setEnabled(canRedo());
    }

    /** the last row where an undoable edit happened */
    private int lastEditRow;
    
    /** indicates that the last entry was whitespace */
    private boolean lastEntryWhitespace;
    
    /**
     * An undoable edit happened
     */
    public void undoableEditHappened(UndoableEditEvent e) {
        int caretPosition = textComponent.getCaretPosition();
        int currentRow = document.getDefaultRootElement().getElementIndex(caretPosition);
        
        // if we've changed rows and the last entry 
        // was not whitespace complete the edit
        if (currentRow != lastEditRow && !lastEntryWhitespace) {
            addUndoEdit();
        }

        compoundEdit.addEdit(e.getEdit());

        // always allow an undo at this point
        undoCommand.setEnabled(true);

        // check for redo
        redoCommand.setEnabled(canRedo());

        // update the last edit row
        lastEditRow = currentRow;
    }

    /**
     * Ensures the component regains focus and actions are updated.
     */
    public void undo() {
        try {
            if (!canRedo()) {
                addUndoEdit();
            }
            super.undo();
        }
        catch (CannotUndoException e) {
            return;
        }

        updateUndo();
        if (!textComponent.hasFocus()) {
            textComponent.requestFocus();
        }
    }

    /**
     * Ensures the component regains focus and actions are updated.
     */
    public void redo() {
        try {
            super.redo();
        }
        catch (CannotUndoException e) {
            return;
        }

        // always enable the undo command
        undoCommand.setEnabled(true);
        redoCommand.setEnabled(canRedo());

        if (!textComponent.hasFocus()) {
            textComponent.requestFocus();
        }
    }

    /**
     * Suspends this manager by removing itself from the document.
     */
    public void suspend() {
        document.removeUndoableEditListener(this);
    }
    
    /**
     * Suspends this manager by removing itself from the document.
     */
    public void reinstate() {
        document.addUndoableEditListener(this);
    }

    /**
     * Completes a compound edit and adds it to the manager.
     */
    public void addUndoEdit() {
        if (compoundEdit.isInProgress()) {
            compoundEdit.end();
        }
        addEdit(compoundEdit);
        compoundEdit = new CompoundEdit();
    }

}













