package org.jfree.util;

public interface LogTarget {
    public static final int DEBUG = 3;
    public static final int ERROR = 0;
    public static final int INFO = 2;
    public static final String[] LEVELS;
    public static final int WARN = 1;

    void log(int i, Object obj);

    void log(int i, Object obj, Exception exception);

    static {
        LEVELS = new String[]{"ERROR: ", "WARN:  ", "INFO:  ", "DEBUG: "};
    }
}
