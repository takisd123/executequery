/*
 * TextAreaLogAppender.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
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

package org.executequery.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class TextAreaLogAppender extends AppenderSkeleton {

    /** the text area component to be appended to */
    private JTextArea textArea;

    /** matcher to remove new lines from log messages */
    private Matcher newLineMatcher;

    /** space string for new line replacement */
    private static final String SPACE = " ";
    
    /** Creates a new instance of TextAreaLogAppender */
    public TextAreaLogAppender(JTextArea textArea) {
        this.textArea = textArea;
        newLineMatcher = Pattern.compile("[\n\r]+").matcher("");
    }
    
    public boolean requiresLayout() {
        return true;
    }
    
    public synchronized void append(LoggingEvent event) {
        String text = null;
        Object message = event.getMessage();
        if (message instanceof String) {
            text = (String)message;
            if (text.length() == 1 && Character.isWhitespace(text.charAt(0))) {
                return;
            }
        }

        text = layout.format(event);
        newLineMatcher.reset(text);
        text = newLineMatcher.replaceAll(SPACE).trim();

        textArea.append(text);
        textArea.append(Layout.LINE_SEP);

        String[] s = event.getThrowableStrRep();
        if (s != null) {
            int len = s.length;
            for(int i = 0; i < len; i++) {
                textArea.append(s[i]);
                textArea.append(Layout.LINE_SEP);
            }
        }
        
        textArea.setCaretPosition(textArea.getText().length());
    }
    
    public void close() {}

}









