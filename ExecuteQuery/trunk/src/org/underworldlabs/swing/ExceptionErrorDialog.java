/*
 * ExceptionErrorDialog.java
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

package org.underworldlabs.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.underworldlabs.swing.util.IconUtilities;

/**
 * Generic error dialog box displaying the stack trace.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ExceptionErrorDialog extends AbstractBaseDialog 
                                  implements ActionListener,
                                             ComponentListener {
    
    /** the error message */
    private String message;
    
    /** the exception list */
    private Vector<Throwable> exceptions;
    
    /** empty exception indicating the last in a chain */
    private Throwable noMoreExceptions;
    
    /** the stack trace text pane */
    private JTextArea textPane;
    
    /** the show stack button */
    private JButton showStackButton;
    
    /** the close button */
    private JButton closeButton;
    
    /** the next excpetion button */
    private JButton nextButton;

    /** the previous excpetion button */
    private JButton previousButton;

    /** the paste stack button */
    private JButton pasteButton;
    
    /** the stack trace panel */
    private JPanel stackTracePanel;
    
    /** the default height */
    private int defaultHeight;

    /** the default width */
    private int defaultWidth;

    /** the current exception index (chained exceptions) */
    private int selectedIndex;
    
    /** Creates a new instance of ExceptionErrorDialog */
    public ExceptionErrorDialog(Frame owner, String message, Throwable exception) {
        super(owner, "Error Message", true);
        this.message = message;

        exceptions = new Vector<Throwable>();
        // we want the underlying cause
        if (exception.getCause() != null) {
            exceptions.add(exception.getCause());
        } else {
            exceptions.add(exception);
        }
        selectedIndex = 0;
        
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() throws Exception {
        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        if (errorIcon == null) {
            // if we don't have one (some LAFs), try the warning icon
            errorIcon = UIManager.getIcon("OptionPane.warningIcon");
        }
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);

        showStackButton = new JButton("Show Stack Trace") {
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                dim.width = 150;
                return dim;
            }
        };
        showStackButton.addActionListener(this);
        
        // format the text
        StringBuffer sb = new StringBuffer();
        sb.append("<html><table border=\"0\" cellpadding=\"2\">");
        
        String delim = "\n";
        boolean wasDelim = true;
        StringTokenizer st = new StringTokenizer(message, delim, true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (delim.equals(token)) {
                if (wasDelim) {
                    sb.append("<tr><td></td></tr>");
                }
                wasDelim = true;
                continue;
            }
            sb.append("<tr><td>");
            sb.append(token);
            sb.append("</td></tr>");
            wasDelim = false;
        }

        sb.append("</table></html>");

        JPanel base = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.left = 5;
        gbc.insets.right = 20;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        base.add(new JLabel(errorIcon), gbc);
        gbc.gridx = 1;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        base.add(new JLabel(sb.toString()), gbc);
        gbc.gridy++;

        gbc.gridx = 1;
        gbc.insets.top = 15;
        gbc.insets.right = 5;
        gbc.insets.bottom = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        base.add(showStackButton, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets.right = 0;
        base.add(closeButton, gbc);

        stackTracePanel = new JPanel(new GridBagLayout());
        stackTracePanel.setVisible(false);

        JPanel stackTraceBase = new JPanel(new BorderLayout());
        stackTraceBase.add(stackTracePanel, BorderLayout.CENTER);
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets.top = 10;
        gbc.insets.left = 5;
        gbc.insets.bottom = 5;
        gbc.insets.right = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        contentPane.add(base, gbc);
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.insets.top = 0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(stackTraceBase, gbc);

        addComponentListener(this);
        
        //this.setLayout(new BorderLayout());
        //add(contentPane, BorderLayout.CENTER);
        
        pack();
        Dimension size = getSize();
        
        // get the absolute center position and adjust 
        // for possible dialog expansion on stack trace
        Point location = GUIUtils.getLocationForDialog(getOwner(), size);
        location.y -= (STACK_HEIGHT/2);
        setLocation(location);

        // set the height and width for resets
        defaultHeight = size.height;
        defaultWidth = 450;
        
        setVisible(true);
    }    
    
    public Dimension getMinimumSize() {
        return new Dimension(400, getSize().height);
    }
    
    /**
     * Builds the stack trace text pane and associated
     * buttons in the case of SQLExceptions.
     */
    private void buildStackTracePanel() {
        if (textPane == null) {
            textPane = new JTextArea();
            textPane.setMargin(new Insets(2,2,2,2));
            textPane.setEditable(false);
            textPane.setWrapStyleWord(false);
            textPane.setBackground(getBackground());
            JScrollPane scroller = new JScrollPane(textPane);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridy++;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            stackTracePanel.add(scroller, gbc);
            
            pasteButton = new RolloverButton(
                    IconUtilities.loadDefaultIconResource("Paste16.png", true),
                    "Paste stack to clipboard");
            pasteButton.addActionListener(this);
            
            gbc.gridy++;
            gbc.insets.top = 2;
            gbc.weighty = 0;
            gbc.weightx = 0;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            stackTracePanel.add(pasteButton, gbc);

            
            if (exceptions.get(selectedIndex) instanceof SQLException) {
                SQLException sqlExc = (SQLException)exceptions.get(selectedIndex);
                if (sqlExc.getNextException() != null) {
                    nextButton = new JButton("Next Exception");
                    nextButton.addActionListener(this);
                    previousButton = new JButton("Previous Exception");
                    previousButton.addActionListener(this);
                    previousButton.setEnabled(false);

                    //gbc.gridy++;
                    gbc.insets.top = 5;
                    gbc.insets.right = 5;
                    gbc.insets.left = 0;
                    gbc.weighty = 0;
                    gbc.gridwidth = 1;
                    gbc.weightx = 1.0;
                    gbc.fill = GridBagConstraints.NONE;
                    gbc.anchor = GridBagConstraints.EAST;
                    stackTracePanel.add(previousButton, gbc);
                    gbc.weightx = 0;
                    gbc.gridx = 1;
                    gbc.insets.right = 0;
                    stackTracePanel.add(nextButton, gbc);
                }
            }
        }
    }
    
    /** the stack trace pane height */
    private static final int STACK_HEIGHT = 220;

    /**
     * Prints the specified exception's stack trace 
     * to the text pane.
     *
     * @param e - the exception to be printed
     */
    private void printException(Throwable e) {
        if (e != null && e != noMoreExceptions) {
            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);
            e.printStackTrace(out);
            textPane.setText(sw.toString());
        }
        else {
            textPane.setText("Exception stack trace not available.");
        }
        textPane.setCaretPosition(0);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == showStackButton) {
            buildStackTracePanel();

            if (stackTracePanel.isVisible()) {
                stackTracePanel.setVisible(false);
                setSize(new Dimension(getWidth(), defaultHeight));
                showStackButton.setText("Show Stack Trace");
            } else {
                stackTracePanel.setVisible(true);
                showStackButton.setText("Hide Stack Trace");
                setSize(new Dimension(
                        getWidth(), defaultHeight + (STACK_HEIGHT + 30)));

                printException(exceptions.get(selectedIndex));
            }

        }
        else if (source == nextButton) {
            selectedIndex++;
            if (exceptions.size() - 1 < selectedIndex) {
                SQLException sqlException = (SQLException)exceptions.get(selectedIndex - 1);
                SQLException nextSQLException = sqlException.getNextException();
                
                if (nextSQLException == null) {                    
                    // add the dummy to the end
                    if (noMoreExceptions == null) {
                        noMoreExceptions = new Throwable();
                        exceptions.add(noMoreExceptions);
                    }
                }
                else {
                    exceptions.add(nextSQLException);
                }

            }
            
            Throwable currentException = exceptions.get(selectedIndex);
            printException(currentException);

            if (currentException == noMoreExceptions || currentException == null) {
                nextButton.setEnabled(false);
            }
            previousButton.setEnabled(true);
        }
        else if (source == previousButton) {
            selectedIndex--;
            Throwable currentException = exceptions.get(selectedIndex);
            printException(currentException);
            
            if (selectedIndex == 0) {
                previousButton.setEnabled(false);
            }
            nextButton.setEnabled(true);
        }
        else if (source == pasteButton) {
            Toolkit.getDefaultToolkit().getSystemClipboard().
                        setContents(new StringSelection(textPane.getText()), null);
        }
        else if (source == closeButton) {
            dispose();
        }
    }
    
    /**
     * Invoked when the component's size changes.
     */
    public void componentResized(ComponentEvent e) {
        Dimension _size = getSize();
        boolean resizeRequired = false;
        if (_size.height < defaultHeight) {
            _size.height = defaultHeight;
            resizeRequired = true;
        }

        if (_size.width < defaultWidth) {
            _size.width = defaultWidth;
            resizeRequired = true;
        }

        if (resizeRequired) {
            setSize(_size);
        }
    }

    /**
     * Invoked when the component's position changes.
     */    
    public void componentMoved(ComponentEvent e) {}

    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown(ComponentEvent e) {}

    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(ComponentEvent e) {}

}









