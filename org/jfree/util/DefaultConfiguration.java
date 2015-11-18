package org.jfree.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import org.jfree.base.config.ModifiableConfiguration;

public class DefaultConfiguration extends Properties implements ModifiableConfiguration {
    public String getConfigProperty(String key) {
        return getProperty(key);
    }

    public String getConfigProperty(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    public Iterator findPropertyKeys(String prefix) {
        TreeSet collector = new TreeSet();
        Enumeration enum1 = keys();
        while (enum1.hasMoreElements()) {
            String key = (String) enum1.nextElement();
            if (key.startsWith(prefix) && !collector.contains(key)) {
                collector.add(key);
            }
        }
        return Collections.unmodifiableSet(collector).iterator();
    }

    public Enumeration getConfigProperties() {
        return keys();
    }

    public void setConfigProperty(String key, String value) {
        if (value == null) {
            remove(key);
        } else {
            setProperty(key, value);
        }
    }
}
