/*
 * ApplicationContext.java
 *
 * Copyright (C) 2002-2010 Takis Diakoumis
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

package org.executequery;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision:1105 $
 * @date     $Date:2008-02-08 15:05:55 +0000 (Fri, 08 Feb 2008) $
 */
public final class ApplicationContext {

    private static final String EXECUTEQUERY_BUILD = "executequery.build";

    private static final String EXECUTEQUERY_USER_HOME_DIR = "executequery.user.home.dir";

    private static final String USER_HOME = "user.home";

    private static final String[] PROPERTY_OVERRIDES = {EXECUTEQUERY_USER_HOME_DIR};
    
    private static ApplicationContext applicationContext;
    
    private Map<String, String> settingsMap;
    
    private ApplicationContext() {
        
        settingsMap = new HashMap<String, String>();
    }

    public static synchronized ApplicationContext getInstance() {
        
        if (applicationContext == null) {
            
            applicationContext = new ApplicationContext();
        }
        
        return applicationContext;
    }

    public String getUserHome() {
        
        return System.getProperty(USER_HOME);
    }
    
    public String getUserSettingsDirectoryName() {

        return settingsMap.get(EXECUTEQUERY_USER_HOME_DIR);
    }

    public void setUserSettingsDirectoryName(String settingsDirectoryName) {

        settingsMap.put(EXECUTEQUERY_USER_HOME_DIR, settingsDirectoryName);
    }

    public String getUserSettingsHome() {

        StringBuilder sb = new StringBuilder();
        
        sb.append(getUserHome()).
            append(fileSeparator()).
            append(getUserSettingsDirectoryName()).
            append(fileSeparator());

        return sb.toString();
    }

    private String fileSeparator() {
        
        return System.getProperty("file.separator");
    }

    public String getBuild() {
        
        return settingsMap.get(EXECUTEQUERY_BUILD);
    }
    
    public void setBuild(String build) {

        settingsMap.put(EXECUTEQUERY_BUILD, build);
    }

    public void startup(String[] args) {

        if (args != null && args.length > 0) {
            
            for (String arg : args) {
                
                if (isValidStartupArg(arg)) {
                    
                    int index = arg.indexOf("="); 

                    String key = arg.substring(0, index);
                    String value = arg.substring(index + 1);
                    settingsMap.put(key, value);
                }

            }
            
        }
        
    }

    private boolean isValidStartupArg(String arg) {

        for (String key : PROPERTY_OVERRIDES) {
            
            if (arg.contains(key)) {
                
                return true;
            }
        }
            
        return false;
    }
    
}


