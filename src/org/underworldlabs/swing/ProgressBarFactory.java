package org.underworldlabs.swing;

import org.underworldlabs.swing.plaf.UIUtils;

public class ProgressBarFactory {

	public static ProgressBar create() {
		return create(true);
	}
	
	public static ProgressBar create(boolean paintBorder) {
		return create(paintBorder, false);		
	}
	
	public static ProgressBar create(boolean paintBorder, boolean ignoreLaF) {

		if (!ignoreLaF) {		
			if (UIUtils.isNativeMacLookAndFeel()) {
				return new BasicProgressBar(paintBorder); 
			}
		}

		return new IndeterminateProgressBar(paintBorder);
	}
	
}
