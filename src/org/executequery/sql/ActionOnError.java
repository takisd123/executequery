package org.executequery.sql;

public enum ActionOnError {

    HALT("Stop"),
    CONTINUE("Continue");
    
    private final String label;
    
    private ActionOnError(String label) {
    
        this.label = label;
    }
    
    @Override
    public String toString() {
        
        return label;
    }

}
