package org.jfree.base.modules;

import org.jfree.util.StackableException;

public class ModuleInitializeException extends StackableException {
    public ModuleInitializeException(String s, Exception e) {
        super(s, e);
    }

    public ModuleInitializeException(String s) {
        super(s);
    }
}
