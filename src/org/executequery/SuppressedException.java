package org.executequery;

import java.io.PrintStream;
import java.io.PrintWriter;

public class SuppressedException extends RuntimeException {

    private final String message;

    public SuppressedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    private boolean stackPrintable() {
//      return Log.isDebugEnabled();
      return false;
    }

    @Override
    public void printStackTrace() {
        if (stackPrintable()) {
            super.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if (stackPrintable()) {
            super.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (stackPrintable()) {
            super.printStackTrace(s);
        }
    }
    
    @Override
    public StackTraceElement[] getStackTrace() {
        return new StackTraceElement[0];
    }
    
}
