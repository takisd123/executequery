/*
 * SmoothGradientTitlePane.java
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

package org.underworldlabs.swing.plaf.smoothgradient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
class SmoothGradientTitlePane extends JComponent {

    private static final Border handyEmptyBorder = new EmptyBorder(0,0,0,0);
    private static final int IMAGE_HEIGHT = 16;
    private static final int IMAGE_WIDTH = 16;

    /**
     * PropertyChangeListener added to the JRootPane.
     */
    private PropertyChangeListener propertyChangeListener;

    /**
     * JMenuBar, typically renders the system menu items.
     */
    private JMenuBar menuBar;
    /**
     * Action used to close the Window.
     */
    private Action closeAction;

    /**
     * Action used to iconify the Frame.
     */
    private Action iconifyAction;

    /**
     * Action to restore the Frame size.
     */
    private Action restoreAction;

    /**
     * Action to restore the Frame size.
     */
    private Action maximizeAction;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton toggleButton;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton iconifyButton;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton closeButton;

    /**
     * Icon used for toggleButton when window is normal size.
     */
    private Icon maximizeIcon;

    /**
     * Icon used for toggleButton when window is maximized.
     */
    private Icon minimizeIcon;

    /**
     * Listens for changes in the state of the Window listener to update
     * the state of the widgets.
     */
    private WindowListener windowListener;

    /**
     * Window we're currently in.
     */
    private Window window;

    /**
     * JRootPane rendering for.
     */
    private JRootPane rootPane;

    /**
     * Room remaining in title for bumps.
     */
    private int buttonsWidth;

    /**
     * Buffered Frame.state property. As state isn't bound, this is kept
     * to determine when to avoid updating widgets.
     */
    private int state;

    /**
     * MetalRootPaneUI that created us.
     */
    private SmoothGradientRootPaneUI rootPaneUI;
    

    // Colors
    private Color inactiveBackground = UIManager.getColor("inactiveCaption");
    private Color inactiveForeground = UIManager.getColor("inactiveCaptionText");
    private Color inactiveShadow = UIManager.getColor("inactiveCaptionBorder");
    private Color activeBumpsHighlight = SmoothGradientLookAndFeel.getInternalFrameBumpsHighlight();
    private Color activeBumpsShadow = SmoothGradientLookAndFeel.getPrimaryControlDarkShadow();
    private Color activeBackground = null;
    private Color activeForeground = null;
    private Color activeShadow = null;

    public SmoothGradientTitlePane(JRootPane root, SmoothGradientRootPaneUI ui) {
        this.rootPane = root;
        rootPaneUI = ui;

        state = -1;

        installSubcomponents();
        determineColors();
        installDefaults();

        setLayout(createLayout());
    }

    /**
     * Uninstalls the necessary state.
     */
    private void uninstall() {
        uninstallListeners();
        window = null;
        removeAll();
    }

    /**
     * Installs the necessary listeners.
     */
    private void installListeners() {
        if (window != null) {
            windowListener = createWindowListener();
            window.addWindowListener(windowListener);
            propertyChangeListener = createWindowPropertyChangeListener();
            window.addPropertyChangeListener(propertyChangeListener);
        }
    }

    /**
     * Uninstalls the necessary listeners.
     */
    private void uninstallListeners() {
        if (window != null) {
            window.removeWindowListener(windowListener);
            window.removePropertyChangeListener(propertyChangeListener);
        }
    }

    /**
     * Returns the <code>WindowListener</code> to add to the
     * <code>Window</code>.
     */
    private WindowListener createWindowListener() {
        return new WindowHandler();
    }

    /**
     * Returns the <code>PropertyChangeListener</code> to install on
     * the <code>Window</code>.
     */
    private PropertyChangeListener createWindowPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Returns the <code>JRootPane</code> this was created for.
     */
    public JRootPane getRootPane() {
        return rootPane;
    }

    /**
     * Returns the decoration style of the <code>JRootPane</code>.
     */
    private int getWindowDecorationStyle() {
        return getRootPane().getWindowDecorationStyle();
    }

    public void addNotify() {
        super.addNotify();

        uninstallListeners();

        window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            if (window instanceof Frame) {
                setState(((Frame)window).getExtendedState());
            }
            else {
                setState(0);
            }
            setActive(window.isActive());
            installListeners();
        }
    }

    public void removeNotify() {
        super.removeNotify();

        uninstallListeners();
        window = null;
    }

    /**
     * Adds any sub-Components contained in the <code>MetalTitlePane</code>.
     */
    private void installSubcomponents() {
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            createActions();
            menuBar = createMenuBar();
            add(menuBar);
            createButtons();
            add(iconifyButton);
            add(toggleButton);
            add(closeButton);
        }
    }

    /**
     * Determines the Colors to draw with.
     */
    private void determineColors() {

//    Color _activeBumpsHighlight = activeBumpsHighlight;

        switch (getWindowDecorationStyle()) {
        case JRootPane.FRAME:
            activeBackground = UIManager.getColor("activeCaption");
            activeForeground = UIManager.getColor("activeCaptionText");
            activeShadow = UIManager.getColor("activeCaptionBorder");
            break;
        case JRootPane.ERROR_DIALOG:
            activeBackground = UIManager.getColor(
                "OptionPane.errorDialog.titlePane.background");
            activeForeground = UIManager.getColor(
                "OptionPane.errorDialog.titlePane.foreground");
            activeShadow = UIManager.getColor(
                "OptionPane.errorDialog.titlePane.shadow");
            break;
        case JRootPane.QUESTION_DIALOG:
        case JRootPane.COLOR_CHOOSER_DIALOG:
        case JRootPane.FILE_CHOOSER_DIALOG:
            activeBackground = UIManager.getColor(
                "OptionPane.questionDialog.titlePane.background");
            activeForeground = UIManager.getColor(
                "OptionPane.questionDialog.titlePane.foreground");
            activeShadow = UIManager.getColor(
                "OptionPane.questionDialog.titlePane.shadow");
            break;
        case JRootPane.WARNING_DIALOG:
            activeBackground = UIManager.getColor(
                "OptionPane.warningDialog.titlePane.background");
            activeForeground = UIManager.getColor(
                "OptionPane.warningDialog.titlePane.foreground");
            activeShadow = UIManager.getColor(
                "OptionPane.warningDialog.titlePane.shadow");
            break;
        case JRootPane.PLAIN_DIALOG:
        case JRootPane.INFORMATION_DIALOG:
        default:
//            _activeBumpsHighlight = SmoothGradientLookAndFeel.getInternalFrameBumpsHighlight();
            activeBackground = UIManager.getColor("activeCaption");
            activeForeground = UIManager.getColor("activeCaptionText");
            activeShadow = UIManager.getColor("activeCaptionBorder");
            break;
        }

    }

    /**
     * Installs the fonts and necessary properties on the MetalTitlePane.
     */
    private void installDefaults() {
        setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
    }
    
    /**
     * Uninstalls any previously installed UI values.
     */
    private void uninstallDefaults() {
    }

    /**
     * Returns the <code>JMenuBar</code> displaying the appropriate 
     * system menu items.
     */
    protected JMenuBar createMenuBar() {
        menuBar = new SystemMenuBar();
        menuBar.setFocusable(false);
        menuBar.setBorderPainted(true);
        menuBar.add(createMenu());
        return menuBar;
    }

    /**
     * Closes the Window.
     */
    private void close() {
        Window window = getWindow();

        if (window != null) {
            window.dispatchEvent(new WindowEvent(
                                 window, WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * Iconifies the Frame.
     */
    private void iconify() {
        Frame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(state | Frame.ICONIFIED);
        }
    }

    /**
     * Maximizes the Frame.
     */
    private void maximize() {
        Frame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Restores the Frame size.
     */
    private void restore() {
        Frame frame = getFrame();

        if (frame == null) {
            return;
        }

        if ((state & Frame.ICONIFIED) != 0) {
            frame.setExtendedState(state & ~Frame.ICONIFIED);
        } else {
            frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Create the <code>Action</code>s that get associated with the
     * buttons and menu items.
     */
    private void createActions() {
        closeAction = new CloseAction();
        iconifyAction = new IconifyAction();
        restoreAction = new RestoreAction();
        maximizeAction = new MaximizeAction();
    }

    /**
     * Returns the <code>JMenu</code> displaying the appropriate menu items
     * for manipulating the Frame.
     */
    private JMenu createMenu() {
        JMenu menu = new JMenu("");
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            addMenuItems(menu);
        }
        return menu;
    }

    /**
     * Adds the necessary <code>JMenuItem</code>s to the passed in menu.
     */
    private void addMenuItems(JMenu menu) {
//        Locale locale = getRootPane().getLocale();
        JMenuItem mi = menu.add(restoreAction);
        int mnemonic = SmoothGradientUtils.getInt("MetalTitlePane.restoreMnemonic", -1);

        if (mnemonic != -1) {
            mi.setMnemonic(mnemonic);
        }

        mi = menu.add(iconifyAction);
        mnemonic = SmoothGradientUtils.getInt("MetalTitlePane.iconifyMnemonic", -1);
        if (mnemonic != -1) {
            mi.setMnemonic(mnemonic);
        }

        if (Toolkit.getDefaultToolkit().isFrameStateSupported(
                Frame.MAXIMIZED_BOTH)) {
            mi = menu.add(maximizeAction);
            mnemonic =
                SmoothGradientUtils.getInt("MetalTitlePane.maximizeMnemonic", -1);
            if (mnemonic != -1) {
                mi.setMnemonic(mnemonic);
            }
        }

        menu.add(new JSeparator());

        mi = menu.add(closeAction);
        mnemonic = SmoothGradientUtils.getInt("MetalTitlePane.closeMnemonic", -1);
        if (mnemonic != -1) {
            mi.setMnemonic(mnemonic);
        }
    }

    /**
     * Returns a <code>JButton</code> appropriate for placement on the
     * TitlePane.
     */
    private JButton createTitleButton() {
        JButton button = new JButton();

        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(true);
        return button;
    }

    /**
     * Creates the Buttons that will be placed on the TitlePane.
     */
    private void createButtons() {
        maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
        minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");

        closeButton = createTitleButton();
        closeButton.setAction(closeAction);
        closeButton.setText(null);
        closeButton.putClientProperty("paintActive", Boolean.TRUE);
        closeButton.setBorder(handyEmptyBorder);
        closeButton.getAccessibleContext().setAccessibleName("Close");
        closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));

        iconifyButton = createTitleButton();
        iconifyButton.setAction(iconifyAction);
        iconifyButton.setText(null);
        iconifyButton.putClientProperty("paintActive", Boolean.TRUE);
        iconifyButton.setBorder(handyEmptyBorder);
        iconifyButton.getAccessibleContext().setAccessibleName("Iconify");
        iconifyButton.setIcon(UIManager.getIcon("InternalFrame.iconifyIcon"));

        toggleButton = createTitleButton();
        toggleButton.setAction(restoreAction);
        toggleButton.putClientProperty("paintActive", Boolean.TRUE);
        toggleButton.setBorder(handyEmptyBorder);
        toggleButton.getAccessibleContext().setAccessibleName("Maximize");
        toggleButton.setIcon(maximizeIcon);
    }

    /**
     * Returns the <code>LayoutManager</code> that should be installed on
     * the <code>MetalTitlePane</code>.
     */
    private LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    /**
     * Updates state dependant upon the Window's active state.
     */
    private void setActive(boolean isActive) {
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            Boolean activeB = isActive ? Boolean.TRUE : Boolean.FALSE;

            iconifyButton.putClientProperty("paintActive", activeB);
            closeButton.putClientProperty("paintActive", activeB);
            toggleButton.putClientProperty("paintActive", activeB);
        }
        // Repaint the whole thing as the Borders that are used have
        // different colors for active vs inactive
        getRootPane().repaint();
    }

    /**
     * Sets the state of the Window.
     */
    private void setState(int state) {
        setState(state, false);
    }

    /**
     * Sets the state of the window. If <code>updateRegardless</code> is
     * true and the state has not changed, this will update anyway.
     */
    private void setState(int state, boolean updateRegardless) {
        Window w = getWindow();

        if (w != null && getWindowDecorationStyle() == JRootPane.FRAME) {
            if (this.state == state && !updateRegardless) {
                return;
            }
            Frame frame = getFrame();

            if (frame != null) {
                JRootPane rootPane = getRootPane();

                if (((state & Frame.MAXIMIZED_BOTH) != 0) &&
                        (rootPane.getBorder() == null ||
                        (rootPane.getBorder() instanceof UIResource)) &&
                            frame.isShowing()) {
                    rootPane.setBorder(null);
                }
                else if ((state & Frame.MAXIMIZED_BOTH) == 0) {
                    // This is a croak, if state becomes bound, this can
                    // be nuked.
//                    rootPaneUI.installBorder(rootPane);
                }
                if (frame.isResizable()) {
                    if ((state & Frame.MAXIMIZED_BOTH) != 0) {
                        updateToggleButton(restoreAction, minimizeIcon);
                        maximizeAction.setEnabled(false);
                        restoreAction.setEnabled(true);
                    }
                    else {
                        updateToggleButton(maximizeAction, maximizeIcon);
                        maximizeAction.setEnabled(true);
                        restoreAction.setEnabled(false);
                    }
                    if (toggleButton.getParent() == null ||
                        iconifyButton.getParent() == null) {
                        add(toggleButton);
                        add(iconifyButton);
                        revalidate();
                        repaint();
                    }
                    toggleButton.setText(null);
                }
                else {
                    maximizeAction.setEnabled(false);
                    restoreAction.setEnabled(false);
                    if (toggleButton.getParent() != null) {
                        remove(toggleButton);
                        revalidate();
                        repaint();
                    }
                }
            }
            else {
                // Not contained in a Frame
                maximizeAction.setEnabled(false);
                restoreAction.setEnabled(false);
                iconifyAction.setEnabled(false);
                remove(toggleButton);
                remove(iconifyButton);
                revalidate();
                repaint();
            }
            closeAction.setEnabled(true);
            this.state = state;
        }
    }

    /**
     * Updates the toggle button to contain the Icon <code>icon</code>, and
     * Action <code>action</code>.
     */
    private void updateToggleButton(Action action, Icon icon) {
        toggleButton.setAction(action);
        toggleButton.setIcon(icon);
        toggleButton.setText(null);
    }

    /**
     * Returns the Frame rendering in. This will return null if the
     * <code>JRootPane</code> is not contained in a <code>Frame</code>.
     */
    private Frame getFrame() {
        Window window = getWindow();

        if (window instanceof Frame) {
            return (Frame)window;
        }
        return null;
    }

    /**
     * Returns the <code>Window</code> the <code>JRootPane</code> is
     * contained in. This will return null if there is no parent ancestor
     * of the <code>JRootPane</code>.
     */
    private Window getWindow() {
        return window;
    }

    /**
     * Returns the String to display as the title.
     */
    private String getTitle() {
        Window w = getWindow();

        if (w instanceof Frame) {
            return ((Frame)w).getTitle();
        }
        else if (w instanceof Dialog) {
            return ((Dialog)w).getTitle();
        }
        return null;
    }

    /**
     * Renders the TitlePane.
     */
    public void paintComponent(Graphics g)  {
        // As state isn't bound, we need a convenience place to check
        // if it has changed. Changing the state typically changes the
        if (getFrame() != null) {
            setState(getFrame().getExtendedState());
        }
        Window window = getWindow();
        boolean leftToRight = (window == null) ?
                               getRootPane().getComponentOrientation().isLeftToRight() :
                               window.getComponentOrientation().isLeftToRight();
        boolean isSelected = (window == null) ? true : window.isActive();
        int width = getWidth();
        int height = getHeight();

        Color background;
        Color foreground;
        Color darkShadow;

        if (isSelected) {
            background = activeBackground;
            foreground = activeForeground;
            darkShadow = activeShadow;
        } else {
            background = inactiveBackground;
            foreground = inactiveForeground;
            darkShadow = inactiveShadow;
        }

        g.setColor(background);
        g.fillRect(0, 0, width, height);

        g.setColor( darkShadow );
        g.drawLine ( 0, height - 1, width, height -1);
        g.drawLine ( 0, 0, 0 ,0);    
        g.drawLine ( width - 1, 0 , width -1, 0);

        int xOffset = leftToRight ? 5 : width - 5;

        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            xOffset += leftToRight ? IMAGE_WIDTH + 5 : - IMAGE_WIDTH - 5;
        }
        
        String theTitle = getTitle();
        if (theTitle != null) {
//            Font f = getFont();
            FontMetrics fm = g.getFontMetrics();

            g.setColor(foreground);

            int yOffset = ( (height - fm.getHeight() ) / 2 ) + fm.getAscent();

            Rectangle rect = new Rectangle(0, 0, 0, 0);
            if (iconifyButton != null && iconifyButton.getParent() != null) {
                rect = iconifyButton.getBounds();
            }
            int titleW;

            if( leftToRight ) {
                if (rect.x == 0) {
                    rect.x = window.getWidth() - window.getInsets().right-2;
                }
                titleW = rect.x - xOffset - 4;
                theTitle = clippedText(theTitle, fm, titleW);
            } else {
                titleW = xOffset - rect.x - rect.width - 4;
                theTitle = clippedText(theTitle, fm, titleW);
                xOffset -= SwingUtilities.computeStringWidth(fm, theTitle);
            }
            int titleLength = SwingUtilities.computeStringWidth(fm, theTitle);
            g.drawString( theTitle, xOffset, yOffset );
            xOffset += leftToRight ? titleLength + 5  : -5;
        }

        Rectangle r = new Rectangle(1,0,width,height);
        SmoothGradientUtils.addLight3DEffekt(g, r, true);

    }

    /**
     * Convenience method to clip the passed in text to the specified
     * size.
     */
    private String clippedText(String text, FontMetrics fm,
                                 int availTextWidth) {
        if ((text == null) || (text.equals("")))  {
            return "";
        }
        int textWidth = SwingUtilities.computeStringWidth(fm, text);
        String clipString = "...";
        if (textWidth > availTextWidth) {
            int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
            int nChars;
            for(nChars = 0; nChars < text.length(); nChars++) {
                totalWidth += fm.charWidth(text.charAt(nChars));
                if (totalWidth > availTextWidth) {
                    break;
                }
            }
            text = text.substring(0, nChars) + clipString;
        }
        return text;
    }


    /**
     * Actions used to <code>close</code> the <code>Window</code>.
     */
    private class CloseAction extends AbstractAction {
        public CloseAction() {
            super(UIManager.getString("MetalTitlePane.closeTitle",
                                      getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            close();
        }      
    }


    /**
     * Actions used to <code>iconfiy</code> the <code>Frame</code>.
     */
    private class IconifyAction extends AbstractAction {
        public IconifyAction() {
            super(UIManager.getString("MetalTitlePane.iconifyTitle",
                                      getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            iconify();
        }
    } 


    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class RestoreAction extends AbstractAction {
        public RestoreAction() {
            super(UIManager.getString
                  ("MetalTitlePane.restoreTitle", getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            restore();
        }
    }


    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class MaximizeAction extends AbstractAction {
        public MaximizeAction() {
            super(UIManager.getString("MetalTitlePane.maximizeTitle",
                                      getLocale()));
        }

        public void actionPerformed(ActionEvent e) {
            maximize();
        }
    }


    /**
     * Class responsible for drawing the system menu. Looks up the
     * image to draw from the Frame associated with the
     * <code>JRootPane</code>.
     */
    private class SystemMenuBar extends JMenuBar {
        public void paint(Graphics g) {
            Frame frame = getFrame();

            if (isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            Image image = (frame != null) ? frame.getIconImage() : null;

            if (image != null) {
                g.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            } else {
                Icon icon = UIManager.getIcon("InternalFrame.icon");

                if (icon != null) {
                    icon.paintIcon(this, g, 0, 0);
                }
            }
        }
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();

            return new Dimension(Math.max(IMAGE_WIDTH, size.width),
                                 Math.max(size.height, IMAGE_HEIGHT));
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    private class TitlePaneLayout implements LayoutManager {  
        public void addLayoutComponent(String name, Component c) {}
        public void removeLayoutComponent(Component c) {}   
        public Dimension preferredLayoutSize(Container c)  {
            int height = computeHeight();
            return new Dimension(height, height);
        }
        
        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        } 
    
        private int computeHeight() {      

            FontMetrics fm = getFontMetrics(getFont());
//                = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
            int fontHeight = fm.getHeight();
            fontHeight += 7;
            int iconHeight = 0;
            if (getWindowDecorationStyle() == JRootPane.FRAME) {
                iconHeight = IMAGE_HEIGHT;
            }

            int finalHeight = Math.max( fontHeight, iconHeight );
            return finalHeight;
        }    
                    
        public void layoutContainer(Container c) {
            if (getWindowDecorationStyle() != JRootPane.FRAME) {
                buttonsWidth = 0;
                return;
            }
            boolean leftToRight = (window == null) ?
                               getRootPane().getComponentOrientation().isLeftToRight() :
                               window.getComponentOrientation().isLeftToRight();

            int w = getWidth();
            int x; 
            int y = 3;
            int spacing;
            int buttonHeight; 
            int buttonWidth;
            
            if (closeButton != null && closeButton.getIcon() != null) {
                buttonHeight = closeButton.getIcon().getIconHeight(); 
                buttonWidth = closeButton.getIcon().getIconWidth();
            }
            else {
                buttonHeight = IMAGE_HEIGHT;
                buttonWidth = IMAGE_WIDTH;
            }
            
            // assumes all buttons have the same dimensions
            // these dimensions include the borders

            x = leftToRight ? w : 0;

            spacing = 5;
            x = leftToRight ? spacing : w - buttonWidth - spacing;
            menuBar.setBounds(x, y, buttonWidth, buttonHeight);

            x = leftToRight ? w : 0;
            spacing = 4;
            x += leftToRight ? -spacing -buttonWidth : spacing;
            if (closeButton != null) {
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
            }

            if( !leftToRight ) x += buttonWidth;

            if (Toolkit.getDefaultToolkit().isFrameStateSupported(
                    Frame.MAXIMIZED_BOTH)) {
                if (toggleButton.getParent() != null) {
                    spacing = 1;//10
                    x += leftToRight ? -spacing -buttonWidth : spacing;
                    toggleButton.setBounds(x, y, buttonWidth, buttonHeight);
                    if (!leftToRight) {
                        x += buttonWidth;
                    }
                }
            }

            if (iconifyButton != null && iconifyButton.getParent() != null) {
                spacing = 1;
                x += leftToRight ? -spacing -buttonWidth : spacing;
                iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }
            buttonsWidth = leftToRight ? w - x : x;
        }
    }



    /**
     * PropertyChangeListener installed on the Window. Updates the necessary
     * state as the state of the Window changes.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pce) {
            String name = pce.getPropertyName();

            // Frame.state isn't currently bound.
            if ("resizable".equals(name) || "state".equals(name)) {
                Frame frame = getFrame();

                if (frame != null) {
                    setState(frame.getExtendedState(), true);
                }
                if ("resizable".equals(name)) {
                    getRootPane().repaint();
                }
            }
            else if ("title".equals(name)) {
                repaint();
            }
            else if ("componentOrientation".equals(name)) {
                revalidate();
                repaint();
            }
        }
    }


    /**
     * WindowListener installed on the Window, updates the state as necessary.
     */
    private class WindowHandler extends WindowAdapter {
        public void windowActivated(WindowEvent ev) {
            setActive(true);
        }

        public void windowDeactivated(WindowEvent ev) {
            setActive(false);
        }
    }

}













