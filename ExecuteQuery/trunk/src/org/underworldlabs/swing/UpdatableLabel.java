package org.underworldlabs.swing;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class UpdatableLabel extends JLabel {

	public UpdatableLabel() {
		super();
	}

	public UpdatableLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public UpdatableLabel(Icon image) {
	    super(image);
	}

	public UpdatableLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public UpdatableLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public UpdatableLabel(String text) {
		super(text);
	}

	@Override
	public void setText(String text) {
	    super.setText(text);
	    scheduleRepaint();
	}

    private void scheduleRepaint() {

        Runnable update = new Runnable() {
            public void run() {
                Dimension dim = getSize();
                paintImmediately(0, 0, dim.width, dim.height);
            }
        };
        SwingUtilities.invokeLater(update);
    }
	
}
