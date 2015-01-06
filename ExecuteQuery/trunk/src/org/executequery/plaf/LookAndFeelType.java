package org.executequery.plaf;


public enum LookAndFeelType {

    EXECUTE_QUERY("Execute Query Default"),
    EXECUTE_QUERY_GRADIENT("Execute Query Default 3D"),
    SMOOTH_GRADIENT("Smooth Gradient"),
    BUMPY_GRADIENT("Bumpy Gradient"),
    EXECUTE_QUERY_THEME("Execute Query Theme"),
    METAL("Metal - Classic"),
    OCEAN("Metal - Ocean (JDK1.5+)"),
    WINDOWS("Windows"),
    MOTIF("CDE/Motif"),
    GTK("GTK+"),
    PLUGIN("Plugin"),
    NATIVE("Native");
 
    private String description;
    
    private LookAndFeelType(String description) {

        this.description = description;
    }

    public String getDescription() {
     
        return description;
    }

    @Override
    public String toString() {

        return getDescription();
    }
    
    public boolean isExecuteQueryLookCompatible() {
        
        return (this == SMOOTH_GRADIENT ||
                this == EXECUTE_QUERY_THEME ||
                this == EXECUTE_QUERY ||
                this == EXECUTE_QUERY_GRADIENT);
    }
    
}
