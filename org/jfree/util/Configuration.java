package org.jfree.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

public interface Configuration extends Serializable, Cloneable {
    Object clone() throws CloneNotSupportedException;

    Iterator findPropertyKeys(String str);

    Enumeration getConfigProperties();

    String getConfigProperty(String str);

    String getConfigProperty(String str, String str2);
}
