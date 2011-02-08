package org.executequery.sql;

import org.executequery.sql.spi.LiquibaseStatementGenerator;

public class StatementGeneratorFactory {

    public static StatementGenerator create() {
        
        return new LiquibaseStatementGenerator();
    }
    
    private StatementGeneratorFactory() {}
    
}
