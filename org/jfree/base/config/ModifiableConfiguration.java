package org.jfree.base.config;

import java.util.Enumeration;
import java.util.Iterator;
import org.jfree.util.Configuration;

public interface ModifiableConfiguration extends Configuration {
    Iterator findPropertyKeys(String str);

    Enumeration getConfigProperties();

    void setConfigProperty(String str, String str2);
}
