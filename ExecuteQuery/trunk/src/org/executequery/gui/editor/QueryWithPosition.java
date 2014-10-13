package org.executequery.gui.editor;

public class QueryWithPosition {

    private int start;
    private int end;
    private int position;
    private String query;
    
    public QueryWithPosition(int position, int start, int end, String query) {
        super();
        this.position = position;
        this.start = start;
        this.end = end;
        this.query = query;
    }

    public int getPosition() {
        return position;
    }
    
    public String getQuery() {
        
        /*
        QueryTokenizer tokenizer = new QueryTokenizer();
        List<DerivedQuery> queries = tokenizer.tokenize(query);
        if (queries.size() == 1) {
            
            return query;
        }
        
        int length = 0;
        int offset = position - start;
        
        System.out.println("offset " + offset);
        
        for (DerivedQuery derivedQuery : queries) {
            
            String originalQuery = derivedQuery.getOriginalQuery();
            length += originalQuery.length();

            System.out.println("length " + length + " - " + originalQuery);
            
            if (length >= offset) {
                
                return originalQuery;
            }

        }
        */
        
        return query;
    }
    
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
        return end;
    }
    
}
