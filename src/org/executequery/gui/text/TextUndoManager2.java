package org.executequery.gui.text;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import org.executequery.GUIUtilities;
import org.executequery.gui.UndoableComponent;
import org.underworldlabs.swing.actions.ActionBuilder;

public class TextUndoManager2 extends AbstractUndoableEdit implements UndoableEditListener, FocusListener {
    
    private int limit;
    
    private int pointer = -1;
    
    private String lastEditName = null;
    
    private MyCompoundEdit current;
    
    private Action undoCommand;
    
    private Action redoCommand;

    private JTextComponent textComponent;

    private ArrayList<MyCompoundEdit> edits = new ArrayList<MyCompoundEdit>();
 
    public TextUndoManager2(JTextComponent textComponent) {

        this.textComponent = textComponent;
        textComponent.getDocument().addUndoableEditListener(this);

        textComponent.addFocusListener(this);
        
        undoCommand = ActionBuilder.get("undo-command");
        redoCommand = ActionBuilder.get("redo-command");
    }
    
    public void undoableEditHappened(UndoableEditEvent e) {
        
        UndoableEdit edit = e.getEdit();
        if (edit instanceof AbstractDocument.DefaultDocumentEvent) {

            try {

                System.out.println(((AbstractDocument.DefaultDocumentEvent) edit).getType());
                
                boolean isNeedStart = false;
                
                AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
                EventType eventType = event.getType();
                if (eventType == EventType.CHANGE) {
                    
                    return;
                
                } else if (eventType == EventType.REMOVE) {
                    
                    isNeedStart = true;
                    
                } else {
                
                    int start = event.getOffset();
                    int len = event.getLength();
                    String text = event.getDocument().getText(start, len);
    
                    if (current == null) {
    
                        isNeedStart = true;
    
                    } else if (text.contains("\n") || text.contains(" ")) {
    
                        isNeedStart = true;
    
                    } else if (lastEditName == null || !lastEditName.equals(edit.getPresentationName())) {
                    
                        isNeedStart = true;
                    }
                    
                }

                while (pointer < editsCount() - 1) {

                    edits.remove(editsCount() - 1);
                    isNeedStart = true;
                }

                if (isNeedStart) {

                    createCompoundEdit();
                }

                current.addEdit(edit);
                lastEditName = edit.getPresentationName();

                refreshControls();

            } catch (BadLocationException e1) {

                createCompoundEdit();
                e1.printStackTrace();
            }
        }

    }
 
    public void createCompoundEdit() {

        if (current == null) {

            current= new MyCompoundEdit();
        }

        else if (current.getLength() > 0) {

            current= new MyCompoundEdit();
        }

        edits.add(current);
        pointer++;
    }
 
    public void undo() throws CannotUndoException {

        if (!canUndo()) {
        
            throw new CannotUndoException();
        }

//        MyCompoundEdit u = edits.get(pointer);
        MyCompoundEdit u = edits.get(edits.size() - 1);
        u.undo();
        pointer--;

        refreshControls();
    }
 
    public void redo() throws CannotUndoException {
        
        if (!canRedo()) {

            throw new CannotUndoException();
        }
 
        pointer++;
        MyCompoundEdit u = edits.get(pointer);
        u.redo();

        refreshControls();
    }
 
    public boolean canUndo() {

        return pointer >= 0;
    }

    public boolean canRedo() {

        int editsCount = editsCount();
        return editsCount > 0 && pointer < editsCount - 1;
    }

    private int editsCount() {
        
        return edits.size();
    }
 
    public void refreshControls() {

        undoCommand.setEnabled(canUndo());
        redoCommand.setEnabled(canRedo());
    }

    /**
     * Updates the state of undo/redo on a focus gain.
     */
    public void focusGained(FocusEvent e) {
        // register this as an undo/redo component
        if (textComponent instanceof UndoableComponent) {
            GUIUtilities.registerUndoRedoComponent((UndoableComponent)textComponent);
        }
        refreshControls();
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
    
    class MyCompoundEdit extends CompoundEdit {

        private boolean isUnDone=false;

        public int getLength() {
        
            return editsCount();
        }
 
        public void undo() throws CannotUndoException {
            
            super.undo();
            isUnDone=true;
        }

        public void redo() throws CannotUndoException {
            
            super.redo();
            isUnDone=false;
        }
        
        public boolean canUndo() {
        
            return editsCount() > 0 && !isUnDone;
        }

        public boolean canRedo() {

            return editsCount() > 0 && isUnDone;
        }
 
    }

    public void setLimit(int limit) {

        this.limit = limit;
    }

    public void discardAllEdits() {

        for (UndoableEdit e : edits) {

            e.die();
        }
        edits.clear();
    }

    public void suspend() {

        textComponent.getDocument().removeUndoableEditListener(this);
    }

    public void reinstate() {

        textComponent.getDocument().addUndoableEditListener(this);
    }

    public void addUndoEdit() {
        // TODO Auto-generated method stub
        
    }

}
