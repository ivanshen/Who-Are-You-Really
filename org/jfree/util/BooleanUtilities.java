package org.jfree.util;

public class BooleanUtilities {
    private BooleanUtilities() {
    }

    public static Boolean valueOf(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }
}
