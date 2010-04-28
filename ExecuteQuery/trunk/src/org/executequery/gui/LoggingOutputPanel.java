package org.executequery.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.executequery.UserPreferencesManager;
import org.executequery.gui.editor.LoggingOutputPane;
import org.underworldlabs.swing.plaf.UIUtils;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
*/
public class LoggingOutputPanel extends JPanel 
                                implements DocumentListener {

    private LoggingOutputPane outputPane;

    public LoggingOutputPanel() {

        super(new BorderLayout());
        outputPane = new LoggingOutputPane();
        outputPane.setMargin(new Insets(5, 5, 5, 5));
        outputPane.setDisabledTextColor(Color.black);
        
        Color bg = UserPreferencesManager.getOutputPaneBackground();
        outputPane.setBackground(bg);

        JScrollPane textOutputScroller = new JScrollPane(outputPane);
        textOutputScroller.setBackground(bg);
        textOutputScroller.setBorder(BorderFactory.createLineBorder(UIUtils.getDefaultBorderColour()));
        textOutputScroller.getViewport().setBackground(bg);

        add(textOutputScroller, BorderLayout.CENTER);
        
        addDocumentListener(this);
    }

    public void append(String text) {
        
        outputPane.append(text);
    }
    
    public void append(int type, String text) {
        
        outputPane.append(type, text);
    }

    public void appendError(String text) {
     
        outputPane.appendError(text);
    }

    public void appendWarning(String text) {
        
        outputPane.appendWarning(text);
    }

    public void appendPlain(String text) {
        
        outputPane.appendPlain(text);
    }

    public void appendAction(String text) {
        
        outputPane.appendAction(text);
    }

    public void appendErrorFixedWidth(String text) {
        
        outputPane.appendErrorFixedWidth(text);
    }

    public void appendWarningFixedWidth(String text) {
        
        outputPane.appendWarningFixedWidth(text);
    }

    public void appendPlainFixedWidth(String text) {
        
        outputPane.appendPlainFixedWidth(text);
    }

    public void appendActionFixedWidth(String text) {
        
        outputPane.appendActionFixedWidth(text);
    }

    public Document getDocument() {
        
        return outputPane.getDocument();
    }
    
    public void addDocumentListener(DocumentListener listener) {
        
        outputPane.getDocument().addDocumentListener(listener);
    }

    public JTextPane getTextPane() {
        
        return outputPane;
    }
    
    public void changedUpdate(DocumentEvent e) {

        documentChanged();
    }

    public void insertUpdate(DocumentEvent e) {
        
        documentChanged();
    }

    public void removeUpdate(DocumentEvent e) {
        
        documentChanged();
    }

    private void documentChanged() {

        outputPane.setCaretPosition(getDocument().getLength());
    }

}
