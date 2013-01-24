package org.underworldlabs.swing;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;

public class BasicProgressBar extends JProgressBar implements ProgressBar {

	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 100;
	
	public BasicProgressBar() {
		this(true);
	}

	public BasicProgressBar(boolean paintBorder) {
		super(MIN_VALUE, MAX_VALUE);
		setIndeterminate(true);
		if (!paintBorder) {
			setBorder(BorderFactory.createEmptyBorder());
		}
	}
	
	public void start() {
		setValue(MIN_VALUE);
	}

	public void stop() {
		setValue(MAX_VALUE);
	}

	public void cleanup() {
		setValue(MAX_VALUE);
	}

}
