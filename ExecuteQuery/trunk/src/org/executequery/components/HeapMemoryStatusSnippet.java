/*
 * HeapMemoryStatusSnippet.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.MouseInputAdapter;
import org.executequery.Constants;

import org.executequery.GUIUtilities;
import org.executequery.log.Log;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.HeapMemoryDialog;

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
public class HeapMemoryStatusSnippet extends JPanel
                                     implements ActionListener {
    
    /** Timer for the heap memory display */
    private java.util.Timer progTimer;
    /** The progress bar */
    private JProgressBar memProgress;
    
    public HeapMemoryStatusSnippet() {
        super(new BorderLayout());
        
        try {
            jbInit();
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void jbInit() throws Exception {
        ProgressModel progModel = new ProgressModel();
        memProgress = new JProgressBar(progModel);
        memProgress.addMouseListener(new ProgressMouseAdapter());
        
        JButton gcButton = new NoFocusButton();
        gcButton.addActionListener(this);

        memProgress.setBorder(null);
        memProgress.setBorderPainted(false);

        add(memProgress, BorderLayout.CENTER);
        add(gcButton, BorderLayout.EAST);
        
        startMeasure(progModel);
    }
    
    public void actionPerformed(ActionEvent e) {
        int total = (int)Runtime.getRuntime().totalMemory();
        int free = (int)Runtime.getRuntime().freeMemory();
        int totalUsedBefore = total - free;
        
        System.gc();
        
        total = (int)Runtime.getRuntime().totalMemory();
        free = (int)Runtime.getRuntime().freeMemory();
        int totalUserAfter = total - free;
        
        Log.info("Garbage collection realeased " +
            ((totalUsedBefore - totalUserAfter) / 1000) + "Kb.");
    }
    
    private void startMeasure(final ProgressModel progModel) {
        
        memProgress.setStringPainted(true);
        memProgress.setFont(new Font("dialog", 0, 10));
        final String mb_slash = "Mb/";
        final String mb = "Mb";
        final int million = 1000000;
        
        final Runnable showProgress = new Runnable() {
            public void run() {
                int total = (int)Runtime.getRuntime().totalMemory();
                int free = (int)Runtime.getRuntime().freeMemory();
                int used = total - free;
                progModel.setMaximum(total);
                progModel.setValue(used);
                memProgress.setString((used/million) + mb_slash + (total/million) + mb);
            }
        };
        
        java.util.TimerTask updateProgress = new java.util.TimerTask() {
            public void run() {
                java.awt.EventQueue.invokeLater(showProgress);
            }
        };
        
        progTimer = new java.util.Timer();
        progTimer.schedule(updateProgress, 0, 2000);
        System.gc();
    }
    
    class ProgressMouseAdapter extends MouseInputAdapter {
        
        public ProgressMouseAdapter() {}
        
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                new HeapMemoryDialog(GUIUtilities.getParentFrame());
            }
        }
        
        public void mouseEntered(MouseEvent e) {
            int total = (int)Runtime.getRuntime().totalMemory();
            int free = (int)Runtime.getRuntime().freeMemory();
            int used = total - free;
            String text = (used/1000) + "Kb/" + (total/1000) + "Kb";
            memProgress.setToolTipText("Java heap size: " + text);
        }
        
    } // class ProgressMouseAdapter
    
    static class ProgressModel extends DefaultBoundedRangeModel {
        
        private int max;

        public ProgressModel() {}
        
        public int getMaximum() {
            return max;
        }
        
        public int getMinimum() {
            return 0;
        }
        
        public int getValue() {
            return getMaximum() - (int)Runtime.getRuntime().freeMemory();
        }
        
        public void setMaximum(int i) {
            max = i;
        }
        
        public void setValue(int i) {
            fireStateChanged();
        }
        
    } // ProgressModel
    
    private class NoFocusButton extends JButton {
        
        private Color borderColour;
        private Color rolloverColour;
        private boolean rollover;

        public NoFocusButton() {
            borderColour = GUIUtils.getDefaultBorderColour();
            rolloverColour = borderColour.darker();
            setFocusPainted(false);
            setBorderPainted(false);
            setMargin(Constants.EMPTY_INSETS);
            setIcon(GUIUtilities.loadIcon("GcDelete16.png"));
            setPressedIcon(GUIUtilities.loadIcon("GcDeletePressed16.png"));
            
            try {
                setUI(new javax.swing.plaf.basic.BasicButtonUI());
            } catch (NullPointerException nullExc) {}

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    rollover = true;
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    rollover = false;
                    repaint();
                }
            });
            
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            int height = getHeight();
            int width = getWidth();

            if (rollover) {
                g.setColor(rolloverColour);
                g.drawRect(1,0,width-2,height-1);
            } else {
                g.setColor(borderColour);
                g.drawLine(1,0,1,height-1);
            }
            
        }

        public boolean isFocusTraversable() {
            return false;
        }
        
        public void requestFocus() {};
        
    };  // NoFocusButton
    
}





