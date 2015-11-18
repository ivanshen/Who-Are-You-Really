package org.jfree.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class StackableException extends Exception {
    private Exception parent;

    public StackableException(String message, Exception ex) {
        super(message);
        this.parent = ex;
    }

    public StackableException(String message) {
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

    public void printStackTrace() {
        synchronized (System.err) {
            printStackTrace(System.err);
        }
    }
}
