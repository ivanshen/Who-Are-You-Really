package org.jfree.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public class StackableRuntimeException extends RuntimeException {
    private Exception parent;

    public StackableRuntimeException(String message, Exception ex) {
        super(message);
        this.parent = ex;
    }

    public StackableRuntimeException(String message) {
        super(message);
    }

    public Exception getParent() {
        return this.parent;
    }

    public void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);
        if (getParent() != null) {
            stream.println("ParentException: ");
            getParent().printStackTrace(stream);
        }
    }

    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (getParent() != null) {
            writer.println("ParentException: ");
            getParent().printStackTrace(writer);
        }
    }
}
