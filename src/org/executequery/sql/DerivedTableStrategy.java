package org.executequery.sql;

import java.util.List;

public interface DerivedTableStrategy {

    String WHERE = "WHERE";
    String INSERT = "INSERT INTO";
    String FROM = "FROM";
    String UPDATE = "UPDATE";
    String SET = "SET";
    String ALTER_TABLE = "ALTER TABLE";
    String DROP_TABLE = "DROP TABLE";
    
    List<QueryTable> deriveTables(String query);
    
}
