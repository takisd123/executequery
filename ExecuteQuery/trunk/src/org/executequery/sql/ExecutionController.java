package org.executequery.sql;

public interface ExecutionController {

    void actionMessage(String message);
    
    void queryMessage(String message);
    
    void errorMessage(String message);

    void warningMessage(String message);
    
    void message(String message);
    
}
