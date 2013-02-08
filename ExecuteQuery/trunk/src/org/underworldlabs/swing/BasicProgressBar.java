/*
 * BasicProgressBar.java
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


