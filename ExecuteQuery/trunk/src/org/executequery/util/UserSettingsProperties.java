/*
 * UserSettingsProperties.java
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

package org.executequery.util;

import org.executequery.ApplicationContext;

public final class UserSettingsProperties {

    public String getUserSettingsDirectory() {
        
        StringBuilder sb = new StringBuilder();

        sb.append(getUserSettingsBaseHome()).
           append(eqBuild()).
           append(fileSeparator());

        return sb.toString();
    }

    public String getUserSettingsBaseHome() {
        
        StringBuilder sb = new StringBuilder();

        sb.append(userHome()).
           append(fileSeparator()).
           append(userSettingsHome()).
           append(fileSeparator());

        return sb.toString();
    }

    private String eqBuild() {

        return ApplicationContext.getInstance().getBuild();
    }

    private String userSettingsHome() {

        return ApplicationContext.getInstance().getUserSettingsDirectoryName();
    }

    private String userHome() {

        return ApplicationContext.getInstance().getUserHome();
    }
    
    private String fileSeparator() {

        return System.getProperty("file.separator");
    }
    
}









