package org.executequery.gui.text;

public enum LineSeparator {

    DOS(0, "Unix (\\n)", "\n"),
    WINDOWS(1, "Dos/Windows (\\r\\n)", "\r\n"),
    MAC_OS(2, "MacOS (\\r)", "\r");
    
    public int index;
    public String label;
    public String value;
    
    private LineSeparator(int index, String label, String value) {
        this.index = index;
        this.label = label;
        this.value = value;
    }

    public static LineSeparator valueForIndex(int index) {
        
        switch(index) {
        
            case 0:
                return DOS;
        
            case 1:
                return WINDOWS;
        
            case 2:
                return MAC_OS;

        }

        throw new IllegalArgumentException("Invalid index for line separator " + index);
    }
    
}
