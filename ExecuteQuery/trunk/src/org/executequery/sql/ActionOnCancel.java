package org.executequery.sql;

public enum ActionOnCancel {

    HALT_ROLLBACK("Halt and Rollback"),
    HALT_COMMIT("Halt and Commit");
    
    private final String label;
    
    private ActionOnCancel(String label) {
    
        this.label = label;
    }
    
    @Override
    public String toString() {
        
        return label;
    }

}
