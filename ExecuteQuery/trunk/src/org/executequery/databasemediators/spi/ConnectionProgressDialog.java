/*
 * ConnectionProgressDialog.java
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

package org.executequery.databasemediators.spi;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.databasemediators.ConnectionBuilder;
import org.executequery.log.Log;
import org.underworldlabs.swing.ProgressBar;
import org.underworldlabs.swing.ProgressBarFactory;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision$
 * @date     $Date$
 */
public class ConnectionProgressDialog extends JDialog
                                      implements Runnable,
                                                 ActionListener {

    /** The connection event parent to this object */
    private ConnectionBuilder connectonBuilder;

    /** The progress bar widget */
    private ProgressBar progressBar;

    /** connection name label */
    private JLabel connectionNameLabel;

    public ConnectionProgressDialog(ConnectionBuilder connectonBuilder) {

        super(GUIUtilities.getParentFrame(), "Connecting...", true);
        this.connectonBuilder = connectonBuilder;

        try {

            jbInit();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void run() {
        progressBar.start();
        setVisible(true);
    }

    private void jbInit() throws Exception {

        progressBar = ProgressBarFactory.create(true, true);
        ((JComponent) progressBar).setPreferredSize(new Dimension(260, 18));

        JPanel base = new JPanel(new GridBagLayout());

        JButton cancelButton = new CancelButton();
        cancelButton.addActionListener(this);

        connectionNameLabel = new JLabel("Establishing connection to " + connectonBuilder.getConnectionName());

        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(10, 20, 10, 20);
        gbc.insets = ins;
        base.add(connectionNameLabel, gbc);
        gbc.gridy = 1;
        gbc.insets.top = 0;
        base.add(((JComponent) progressBar), gbc);
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.insets.left = 10;
        gbc.insets.right = 10;
        base.add(cancelButton, gbc);

        base.setBorder(BorderFactory.createEtchedBorder());

        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(base, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                           GridBagConstraints.SOUTHEAST,
                                           GridBagConstraints.BOTH,
                                           new Insets(5, 5, 5, 5), 0, 0));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        pack();
        setLocation(GUIUtilities.getLocationForDialog(getSize()));
    }

    public void updateLabel(String name) {

        connectionNameLabel.setText("Establishing connection to " + name);
        Runnable update = new Runnable() {
            public void run() {
                Dimension dim = connectionNameLabel.getSize();
                connectionNameLabel.paintImmediately(0, 0, dim.width, dim.height);
            }
        };
        SwingUtilities.invokeLater(update);
    }

    public void actionPerformed(ActionEvent e) {

        Log.info("Connection cancelled");

        connectonBuilder.cancel();
        dispose();
    }

    public void dispose() {

        if (progressBar != null) {

            progressBar.stop();
            progressBar.cleanup();
        }

        super.dispose();
    }


    class CancelButton extends JButton {

        private int DEFAULT_WIDTH = 75;
        private int DEFAULT_HEIGHT = 30;

        public CancelButton() {

            super("Cancel");
            setMargin(Constants.EMPTY_INSETS);
        }

        public int getWidth() {

            int width = super.getWidth();

            if (width < DEFAULT_WIDTH) {

                return DEFAULT_WIDTH;
            }

            return width;
        }

        public int getHeight() {

            int height = super.getHeight();

            if (height < DEFAULT_HEIGHT) {

                return DEFAULT_HEIGHT;
            }

            return height;
        }

        public Dimension getPreferredSize() {

            return new Dimension(getWidth(), getHeight());
        }

    }

}









