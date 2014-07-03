package org.executequery.gui.editor;

public class QueryWithPosition {

    private int start;
    private int end;
    private String query;
    
    public QueryWithPosition(int start, int end, String query) {
        super();
        this.start = start;
        this.end = end;
        this.query = query;
    }
    
    public String getQuery() {
        return query;
    }
    
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
        return end;
    }
    
}
