package org.executequery.util;

import org.underworldlabs.util.MiscUtils;

public class Timer {

    private long start;
    private long stop;
    
    public void start() {
        
        stop = 0;
        start = System.currentTimeMillis();
    }
    
    public void stop() {
        
        stop = System.currentTimeMillis();
    }
    
    public long duration() {
        
        return stop - start;
    }
    
    public String durationAsString() {

        return MiscUtils.formatDuration(duration());
    }
    
}
