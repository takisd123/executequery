package org.executequery;

import java.awt.Color;

import org.executequery.event.DefaultUserPreferenceEvent;
import org.executequery.event.UserPreferenceEvent;
import org.underworldlabs.util.SystemProperties;

/** 
 * Proposed user preferences manager util for one-stop access.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1481 $
 * @date     $Date: 2009-03-14 01:42:16 +1100 (Sat, 14 Mar 2009) $
 */
public final class UserPreferencesManager {

    private UserPreferencesManager() {}

    public static void fireUserPreferencesChanged() {
        
        EventMediator.fireEvent(
                new DefaultUserPreferenceEvent(
                        UserPreferencesManager.class, null, UserPreferenceEvent.ALL));
    }
    
    public static Color getOutputPaneBackground() {

        return SystemProperties.getColourProperty(
                Constants.USER_PROPERTIES_KEY, "editor.results.background.colour");
    }
    
    public static boolean isTransposingSingleRowResultSets() {
        
        return SystemProperties.getBooleanProperty(
                Constants.USER_PROPERTIES_KEY, "results.table.single.row.transpose");
    }
    
    public static boolean isResultSetTabSingle() {
        
        return SystemProperties.getBooleanProperty(
                Constants.USER_PROPERTIES_KEY, "editor.results.tabs.single");
    }
    
    public static boolean doubleClickOpenItemView() {

        return SystemProperties.getBooleanProperty(
                Constants.USER_PROPERTIES_KEY, "results.table.double-click.record.dialog"); 
    }
    
}
