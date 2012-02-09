package org.executequery.util;

import java.io.File;
import java.util.Comparator;

class UserSettingsDirectoryBuildNumberComparator implements Comparator<File> {

    public int compare(File o1, File o2) {

        String name1 = o1.getName();
        String name2 = o2.getName();

        return name1.compareTo(name2);
    }
    
}
