package org.executequery.components;

import java.awt.Dialog;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import org.executequery.util.SystemWebBrowserLauncher;
import org.underworldlabs.swing.DialogMessageContent;

public class SimpleHtmlContentPane extends JEditorPane implements DialogMessageContent {

    private Dialog dialog;

    public SimpleHtmlContentPane(String message) {

        super("text/html", message.replaceAll("\n", "<br>"));

        setEditable(false);
        setOpaque(false);

        setSelectionColor(UIManager.getColor("OptionPane.background"));

        Font font = UIManager.getFont("OptionPane.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                "font-size: " + (font.getSize() - 1) + "pt; }";

        ((HTMLDocument) getDocument()).getStyleSheet().addRule(bodyRule);
        
        addHyperlinkListener(new OpensLinkInBrowserListener());
    }

    class OpensLinkInBrowserListener implements HyperlinkListener {

        public void hyperlinkUpdate(HyperlinkEvent e) {

            if (isActiveEvent(e)) {

                try {
                
                    new SystemWebBrowserLauncher().launch(e.getDescription());
                
                } finally {
                    
                    dialog.dispose();
                }

            }

        }

        private boolean isActiveEvent(HyperlinkEvent e) {

            return HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType());
        }
        
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }
    
    
}
